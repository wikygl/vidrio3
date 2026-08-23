// Emisión de boletas electrónicas. Dos caminos según el monto (ver `comun.js`):
//   > S/5  → boleta individual por la recarga
//   ≤ S/5  → se acumula y al cierre del día sale UNA boleta consolidada por el total
//
// La emisión NUNCA ocurre dentro de la transacción que acredita el saldo: una transacción de
// Firestore se reintenta sola, y una llamada HTTP dentro de ella emitiría comprobantes duplicados
// e irrepetibles. La transacción solo deja el encargo escrito; otra función lo emite después.

const admin = require("firebase-admin");
const {
  fechaPeru,
  requiereBoletaIndividual,
  desglosarIgv,
} = require("./comun");
const { crearProveedor } = require("./proveedor");

const COL_COMPROBANTES = "comprobantes";
const COL_CONSOLIDADO = "consolidado_diario";
const COL_SERIES = "series";

const CONFIG_POR_DEFECTO = {
  serieBoleta: "B001",
  igvPorcentaje: 18,
  emisorRuc: "",
  emisorRazonSocial: "",
  emisorDireccion: "",
};

async function leerConfig(db) {
  try {
    const snap = await db.collection("config").doc("facturacion").get();
    if (!snap.exists) return { ...CONFIG_POR_DEFECTO };
    return {
      serieBoleta: snap.get("serie_boleta") || CONFIG_POR_DEFECTO.serieBoleta,
      // `?? ` y no `||`: un régimen sin IGV (Nuevo RUS) configura 0, y `||` lo convertiría en 18.
      igvPorcentaje: snap.get("igv_porcentaje") ?? CONFIG_POR_DEFECTO.igvPorcentaje,
      emisorRuc: snap.get("emisor_ruc") || "",
      emisorRazonSocial: snap.get("emisor_razon_social") || "",
      emisorDireccion: snap.get("emisor_direccion") || "",
    };
  } catch (e) {
    return { ...CONFIG_POR_DEFECTO };
  }
}

/**
 * Se llama DENTRO de la transacción que acredita el saldo. Solo escribe —con ids deterministas y
 * `increment`— para no añadir lecturas ni romper el orden lectura→escritura de la transacción.
 *
 * El id `rec_{reservaId}` es la garantía de idempotencia: si la acreditación se reintentara, el
 * `set` cae sobre el mismo documento en vez de crear una segunda boleta por el mismo pago.
 */
function encolarComprobante(tx, db, { uid, montoCent, reservaId, ahoraMs }) {
  const fecha = fechaPeru(ahoraMs);

  if (requiereBoletaIndividual(montoCent)) {
    const ref = db.collection(COL_COMPROBANTES).doc(`rec_${reservaId}`);
    tx.set(ref, {
      tipo: "BOLETA",
      origen: "RECARGA",
      uid,
      reservaId,
      fecha,
      totalCent: montoCent,
      estado: "pendiente",
      intentos: 0,
      created_at: admin.firestore.FieldValue.serverTimestamp(),
    }, { merge: true });
    return { destino: "individual", comprobanteId: ref.id };
  }

  // Control diario que exige el reglamento para las operaciones no facturadas individualmente.
  const acumRef = db.collection(COL_CONSOLIDADO).doc(fecha);
  tx.set(acumRef, {
    fecha,
    totalCent: admin.firestore.FieldValue.increment(montoCent),
    operaciones: admin.firestore.FieldValue.increment(1),
    estado: "abierto",
  }, { merge: true });
  tx.set(acumRef.collection("items").doc(reservaId), {
    uid,
    montoCent,
    created_at: admin.firestore.FieldValue.serverTimestamp(),
  });
  return { destino: "consolidado", fecha };
}

/**
 * Reserva el siguiente correlativo de la serie, en transacción.
 *
 * Se asigna UNA sola vez por comprobante y se conserva aunque la emisión falle: reintentar con el
 * mismo número evita huecos en la numeración, que es lo que SUNAT observa. Pedir un número nuevo en
 * cada reintento dejaría correlativos quemados.
 */
async function reservarCorrelativo(db, serie) {
  const ref = db.collection(COL_SERIES).doc(serie);
  return db.runTransaction(async (tx) => {
    const snap = await tx.get(ref);
    const ultimo = snap.exists ? (snap.get("ultimo") || 0) : 0;
    const siguiente = ultimo + 1;
    tx.set(ref, {
      serie,
      ultimo: siguiente,
      actualizado_at: admin.firestore.FieldValue.serverTimestamp(),
    }, { merge: true });
    return siguiente;
  });
}

// Datos del adquirente. Bajo S/700 la boleta no exige identificar al cliente, así que si no hay
// documento se emite como "VARIOS" en vez de bloquear la emisión.
async function resolverCliente(db, uid) {
  if (!uid) return { tipoDoc: "-", numDoc: "00000000", nombre: "VARIOS", direccion: "" };
  try {
    const snap = await db.collection("usuarios").doc(uid).get();
    const dni = snap.get("numeroDocumento") || snap.get("dni") || "";
    const nombre = snap.get("nombreCompleto") || snap.get("nombre") || "VARIOS";
    if (dni && String(dni).length === 8) {
      return { tipoDoc: "1", numDoc: String(dni), nombre, direccion: snap.get("direccion") || "" };
    }
    return { tipoDoc: "-", numDoc: "00000000", nombre, direccion: "" };
  } catch (e) {
    return { tipoDoc: "-", numDoc: "00000000", nombre: "VARIOS", direccion: "" };
  }
}

/**
 * Emite un comprobante que está en estado `pendiente` o `error`. Idempotente: si ya está emitido no
 * hace nada. Devuelve el motivo cuando no emite, para que el llamador lo registre.
 */
async function emitirComprobante(db, comprobanteId, proveedor) {
  const ref = db.collection(COL_COMPROBANTES).doc(comprobanteId);
  const snap = await ref.get();
  if (!snap.exists) return { ok: false, motivo: "NO_EXISTE" };
  if (snap.get("estado") === "emitido") return { ok: false, motivo: "YA_EMITIDO" };

  const cfg = await leerConfig(db);
  const totalCent = snap.get("totalCent") || 0;
  if (!(totalCent > 0)) return { ok: false, motivo: "MONTO_INVALIDO" };

  const serie = snap.get("serie") || cfg.serieBoleta;
  let correlativo = snap.get("correlativo");
  if (!correlativo) {
    correlativo = await reservarCorrelativo(db, serie);
    await ref.update({ serie, correlativo });
  }

  const igv = desglosarIgv(totalCent, cfg.igvPorcentaje);
  const esConsolidado = snap.get("origen") === "CONSOLIDADO_DIARIO";
  const cliente = await resolverCliente(db, esConsolidado ? null : snap.get("uid"));
  const descripcion = esConsolidado
    ? `Consolidado diario de operaciones menores a S/5 — ${snap.get("fecha")}`
    : "Recarga de saldo Crystal";

  const doc = {
    serie,
    correlativo,
    fechaEmision: snap.get("fecha") || fechaPeru(Date.now()),
    igvPorcentaje: cfg.igvPorcentaje,
    gravadaCent: igv.gravadaCent,
    inafectaCent: igv.inafectaCent,
    igvCent: igv.igvCent,
    totalCent: igv.totalCent,
    clienteTipoDoc: cliente.tipoDoc,
    clienteNumDoc: cliente.numDoc,
    clienteNombre: cliente.nombre,
    clienteDireccion: cliente.direccion,
    items: [{
      descripcion,
      valorUnitarioCent: igv.gravadaCent || igv.inafectaCent,
      precioUnitarioCent: igv.totalCent,
      subtotalCent: igv.gravadaCent || igv.inafectaCent,
      igvCent: igv.igvCent,
      totalCent: igv.totalCent,
    }],
  };

  try {
    const r = await (proveedor || crearProveedor()).emitirBoleta(doc);
    await ref.update({
      estado: "emitido",
      numeroCompleto: r.numeroCompleto,
      pdfUrl: r.pdfUrl,
      xmlUrl: r.xmlUrl,
      cdrUrl: r.cdrUrl,
      hash: r.hash,
      aceptadoPorSunat: r.aceptadoPorSunat,
      respuestaSunat: r.respuestaSunat,
      gravadaCent: igv.gravadaCent,
      inafectaCent: igv.inafectaCent,
      igvCent: igv.igvCent,
      emitido_at: admin.firestore.FieldValue.serverTimestamp(),
    });
    return { ok: true, numeroCompleto: r.numeroCompleto };
  } catch (e) {
    await ref.update({
      estado: "error",
      intentos: admin.firestore.FieldValue.increment(1),
      ultimoError: String(e.message || e),
      reintentable: e.reintentable !== false,
      error_at: admin.firestore.FieldValue.serverTimestamp(),
    });
    return { ok: false, motivo: "ERROR_PROVEEDOR", detalle: String(e.message || e) };
  }
}

/**
 * Cierra el acumulado de un día y deja encolada la boleta consolidada por el total.
 *
 * Solo cierra días YA vencidos: cerrar el día en curso dejaría fuera las operaciones que aún faltan.
 * Un pago tardío que se acredite después del cierre cae en el consolidado del día en que se acredita
 * —no reabre uno cerrado—, que es la única forma de mantener la numeración avanzando hacia adelante.
 */
async function cerrarConsolidado(db, fecha) {
  const ref = db.collection(COL_CONSOLIDADO).doc(fecha);
  const snap = await ref.get();
  if (!snap.exists) return { ok: false, motivo: "SIN_MOVIMIENTOS" };
  if (snap.get("estado") === "cerrado") return { ok: false, motivo: "YA_CERRADO" };
  const totalCent = snap.get("totalCent") || 0;
  if (!(totalCent > 0)) {
    await ref.update({ estado: "cerrado", cerrado_at: admin.firestore.FieldValue.serverTimestamp() });
    return { ok: false, motivo: "SIN_MOVIMIENTOS" };
  }

  const compRef = db.collection(COL_COMPROBANTES).doc(`con_${fecha}`);
  await compRef.set({
    tipo: "BOLETA",
    origen: "CONSOLIDADO_DIARIO",
    uid: null,
    fecha,
    totalCent,
    operaciones: snap.get("operaciones") || 0,
    estado: "pendiente",
    intentos: 0,
    created_at: admin.firestore.FieldValue.serverTimestamp(),
  }, { merge: true });

  await ref.update({
    estado: "cerrado",
    comprobanteId: compRef.id,
    cerrado_at: admin.firestore.FieldValue.serverTimestamp(),
  });
  return { ok: true, comprobanteId: compRef.id, totalCent };
}

module.exports = {
  COL_COMPROBANTES,
  COL_CONSOLIDADO,
  COL_SERIES,
  leerConfig,
  encolarComprobante,
  reservarCorrelativo,
  emitirComprobante,
  cerrarConsolidado,
};
