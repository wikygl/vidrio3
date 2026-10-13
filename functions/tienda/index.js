// Tienda de anuncios del rubro (plan en docs/TIENDA_ANUNCIOS_PLAN.md).
//
// Todo lo que se cobra aquí —publicar, renovar, destacar, la vitrina de proveedores y tomar un
// pedido de un cliente— se paga con el saldo del wallet, igual que un plan (`activarPlan`): el
// servidor decide precio y vencimiento, descuenta en una transacción y deja el movimiento en
// `wallet_movs`. No se emite boleta aquí: ya salió al recargar el saldo.

// Timestamp y FieldValue se toman del módulo de Firestore y no de `admin.firestore.*`: en el entorno
// de funciones `admin.firestore` puede venir envuelto y sin esas propiedades.
const crypto = require("crypto");
const { Timestamp, FieldValue } = require("firebase-admin/firestore");
const {
  tarifas,
  tarifaDe,
  validarAnuncio,
  idValido,
  nuevoVencimiento,
  nuevoDestacado,
  validarProveedor,
  validarPedidoCliente,
  palabraDeConsulta,
  variantes,
  casaTexto,
  estadisticaPrecios,
  planAvanzado,
  anuncioPublico,
  visible,
  claveCiudad,
  CATEGORIAS,
  TIPOS,
  DIAS_PEDIDO,
  DIAS_REFERENCIA,
  DIA_MS,
} = require("./comun");

const COL = "anuncios";
const COL_PROVEEDORES = "proveedores";
const COL_PEDIDOS = "pedidos_clientes";
/** A cuántas denuncias de usuarios distintos un anuncio sale de la lista hasta que lo revisen. */
const DENUNCIAS_PARA_REVISION = 3;
/** Topes de la web por visitante: pedidos por hora y por día desde una misma IP o un mismo teléfono. */
const TOPE_POR_HORA = 3;
const TOPE_POR_DIA = 10;
const TOPE_POR_TELEFONO = 3;

class ErrorTienda extends Error {
  constructor(codigo, mensaje) {
    super(mensaje);
    this.codigo = codigo;
  }
}

async function leerTarifas(db) {
  try {
    const snap = await db.collection("config").doc("anuncios").get();
    return tarifas(snap.exists ? snap.data() : null);
  } catch (e) {
    return tarifas(null);
  }
}

function nombreDe(usuario) {
  if (!usuario || !usuario.exists) return "";
  return String(usuario.get("nombre") || usuario.get("perfil.nombre") || "").trim();
}

/** Una terminal no paga: el cobro saldría de una cuenta sin saldo. Lo hace la cuenta del patrón. */
function soloElPatron(uid, patronUid, que) {
  if (patronUid && patronUid !== uid) {
    throw new ErrorTienda("permission-denied", `${que} se hace desde la cuenta del patrón.`);
  }
}

/**
 * Descuenta del saldo dentro de la transacción y deja el movimiento. Todas las lecturas de la
 * transacción tienen que haberse hecho antes (Firestore no admite leer después de escribir).
 * Devuelve el saldo que queda, en céntimos.
 */
function cobrar(tx, uref, usuario, precioCent, movimiento) {
  const saldo = (usuario.exists && usuario.get("wallet_saldo_cent")) || 0;
  if (saldo < precioCent) throw new ErrorTienda("failed-precondition", "SALDO_INSUFICIENTE");
  const restante = saldo - precioCent;
  if (precioCent > 0) {
    tx.update(uref, { wallet_saldo_cent: restante, "wallet.saldo": restante / 100 });
    tx.set(uref.collection("wallet_movs").doc(), Object.assign({
      monto_cent: -precioCent,
      created_at: FieldValue.serverTimestamp(),
    }, movimiento));
  }
  return restante;
}

function ms(t) {
  return t && typeof t.toMillis === "function" ? t.toMillis() : 0;
}

// ═══════════════════════════════ Anuncios ═══════════════════════════════

/**
 * Cobra y publica. `uid` es quien llama; `patronUid` es el claim de una terminal: las terminales no
 * publican, lo hace la cuenta del patrón (el cobro sale del wallet de quien llama).
 */
async function publicar(db, { uid, patronUid, data, ahoraMs }) {
  soloElPatron(uid, patronUid, "Publicar anuncios");
  const anuncioId = String((data && data.anuncioId) || "");
  if (!idValido(anuncioId)) throw new ErrorTienda("invalid-argument", "Anuncio no válido.");

  const v = validarAnuncio(data, uid, anuncioId);
  if (!v.ok) throw new ErrorTienda("invalid-argument", v.error);

  const { precioCent, dias } = tarifaDe(await leerTarifas(db), v.anuncio.tipo);
  const uref = db.collection("usuarios").doc(uid);
  const aref = db.collection(COL).doc(anuncioId);

  return db.runTransaction(async (tx) => {
    const [usuario, existente] = await Promise.all([tx.get(uref), tx.get(aref)]);
    // Publicar dos veces el mismo anuncio (un doble toque, un reintento de la red) no cobra dos
    // veces: el segundo intento encuentra el documento y se detiene.
    if (existente.exists) {
      if (existente.get("uid") === uid) {
        return { ok: true, anuncioId, yaPublicado: true, venceEn: existente.get("venceEn").toMillis() };
      }
      throw new ErrorTienda("already-exists", "Anuncio no válido.");
    }
    const restante = cobrar(tx, uref, usuario, precioCent, {
      tipo: "PAGO_ANUNCIO", dias, anuncioId, titulo: v.anuncio.titulo,
    });

    const venceMs = nuevoVencimiento(0, ahoraMs, dias);
    const ahora = Timestamp.fromMillis(ahoraMs);
    tx.set(aref, Object.assign({}, v.anuncio, {
      uid,
      vendedorNombre: nombreDe(usuario),
      estado: "activo",
      publicadoEn: ahora,
      creadoEn: ahora,
      venceEn: Timestamp.fromMillis(venceMs),
      pagadoCent: precioCent,
      dias,
      vistas: 0,
      denuncias: 0,
    }));
    return { ok: true, anuncioId, venceEn: venceMs, saldoCent: restante, precioCent, dias };
  });
}

/**
 * Cobra otro período y lo suma: desde el vencimiento si sigue vigente, desde hoy si ya venció. Un
 * anuncio retirado o pausado vuelve a estar activo; uno en revisión o bloqueado, no.
 */
async function renovar(db, { uid, anuncioId, ahoraMs }) {
  if (!idValido(anuncioId)) throw new ErrorTienda("invalid-argument", "Anuncio no válido.");
  const t = await leerTarifas(db);
  const uref = db.collection("usuarios").doc(uid);
  const aref = db.collection(COL).doc(anuncioId);

  return db.runTransaction(async (tx) => {
    const [usuario, anuncio] = await Promise.all([tx.get(uref), tx.get(aref)]);
    if (!anuncio.exists || anuncio.get("uid") !== uid) {
      throw new ErrorTienda("not-found", "No se encontró el anuncio.");
    }
    const estado = anuncio.get("estado");
    if (estado === "en_revision" || estado === "bloqueado") {
      throw new ErrorTienda("failed-precondition", "El anuncio está en revisión y no se puede renovar.");
    }
    const { precioCent, dias } = tarifaDe(t, anuncio.get("tipo"));
    const restante = cobrar(tx, uref, usuario, precioCent, {
      tipo: "RENUEVA_ANUNCIO", dias, anuncioId, titulo: String(anuncio.get("titulo") || ""),
    });
    const venceMs = nuevoVencimiento(ms(anuncio.get("venceEn")), ahoraMs, dias);
    tx.update(aref, {
      estado: "activo",
      venceEn: Timestamp.fromMillis(venceMs),
      // Renovado sube arriba en la lista, como uno recién publicado: es lo que se está pagando.
      publicadoEn: Timestamp.fromMillis(ahoraMs),
      pagadoCent: FieldValue.increment(precioCent),
    });
    return { ok: true, anuncioId, venceEn: venceMs, saldoCent: restante, precioCent, dias };
  });
}

/** Cobra unos días de "Destacado": el anuncio sale primero en la tienda, con su marca. */
async function destacar(db, { uid, anuncioId, ahoraMs }) {
  if (!idValido(anuncioId)) throw new ErrorTienda("invalid-argument", "Anuncio no válido.");
  const { precioCent, dias } = (await leerTarifas(db)).destacar;
  const uref = db.collection("usuarios").doc(uid);
  const aref = db.collection(COL).doc(anuncioId);

  return db.runTransaction(async (tx) => {
    const [usuario, anuncio] = await Promise.all([tx.get(uref), tx.get(aref)]);
    if (!anuncio.exists || anuncio.get("uid") !== uid) {
      throw new ErrorTienda("not-found", "No se encontró el anuncio.");
    }
    const d = nuevoDestacado({
      estado: anuncio.get("estado"),
      venceMs: ms(anuncio.get("venceEn")),
      destacadoMs: ms(anuncio.get("destacadoHasta")),
      ahoraMs,
      dias,
    });
    if (!d.ok) throw new ErrorTienda("failed-precondition", d.error);
    const restante = cobrar(tx, uref, usuario, precioCent, {
      tipo: "DESTACA_ANUNCIO", dias, anuncioId, titulo: String(anuncio.get("titulo") || ""),
    });
    tx.update(aref, {
      destacadoHasta: Timestamp.fromMillis(d.hastaMs),
      pagadoCent: FieldValue.increment(precioCent),
    });
    return { ok: true, anuncioId, destacadoHasta: d.hastaMs, saldoCent: restante, precioCent, dias };
  });
}

/**
 * Lo que vence cada día: anuncios activos o pausados con el plazo pasado, pedidos de clientes sin
 * cerrar a los 15 días y fichas de proveedor sin renovar.
 */
async function expirar(db, ahoraMs) {
  const ahora = Timestamp.fromMillis(ahoraMs);
  const pasar = async (col, estado, campo, nuevo) => {
    let total = 0;
    // De a 300 por vuelta: un lote de Firestore admite 500 escrituras.
    for (;;) {
      const snap = await db.collection(col)
        .where("estado", "==", estado)
        .where(campo, "<", ahora)
        .limit(300)
        .get();
      if (snap.empty) break;
      const lote = db.batch();
      snap.docs.forEach((d) => lote.update(d.ref, { estado: nuevo }));
      await lote.commit();
      total += snap.size;
      if (snap.size < 300) break;
    }
    return total;
  };
  const vencidos = (await pasar(COL, "activo", "venceEn", "vencido")) +
    (await pasar(COL, "pausado", "venceEn", "vencido"));
  const pedidos = await pasar(COL_PEDIDOS, "abierto", "venceEn", "vencido");
  const proveedores = await pasar(COL_PROVEEDORES, "activo", "vigenteHasta", "vencido");
  return { vencidos, pedidos, proveedores };
}

/**
 * Cuenta una denuncia nueva. A la tercera de usuarios distintos el anuncio sale de la lista
 * (`en_revision`) hasta que el panel lo mire. Devuelve si acaba de pasar a revisión.
 */
async function contarDenuncia(db, anuncioId) {
  const aref = db.collection(COL).doc(anuncioId);
  return db.runTransaction(async (tx) => {
    const a = await tx.get(aref);
    if (!a.exists) return { ok: false };
    const denuncias = (a.get("denuncias") || 0) + 1;
    const cambios = { denuncias };
    const pasaARevision = denuncias >= DENUNCIAS_PARA_REVISION && a.get("estado") === "activo";
    if (pasaARevision) cambios.estado = "en_revision";
    tx.update(aref, cambios);
    return { ok: true, denuncias, pasaARevision, titulo: String(a.get("titulo") || ""), uid: a.get("uid") };
  });
}

// ═══════════════════════════════ Moderación (panel) ═══════════════════════════════

const ACCIONES_ANUNCIO = ["reactivar", "bloquear"];

/**
 * La decisión del panel sobre un anuncio denunciado. Reactivar lo devuelve a la tienda (o a vencido,
 * si ya pasó su plazo) con las denuncias en cero y las borra, para que se pueda volver a denunciar;
 * bloquear lo saca sin devolución y el dueño ya no puede reactivarlo. Todo queda en `moderacion`.
 */
async function moderarAnuncio(db, { anuncioId, accion, nota, porUid, ahoraMs }) {
  if (!idValido(anuncioId) || !ACCIONES_ANUNCIO.includes(accion)) {
    throw new ErrorTienda("invalid-argument", "Acción no válida.");
  }
  const aref = db.collection(COL).doc(anuncioId);
  const denuncias = await aref.collection("denuncias").limit(200).get();
  const motivos = denuncias.docs.map((d) => String(d.get("motivo") || ""));

  const r = await db.runTransaction(async (tx) => {
    const a = await tx.get(aref);
    if (!a.exists) throw new ErrorTienda("not-found", "No se encontró el anuncio.");
    const estado = accion === "bloquear" ? "bloqueado"
      : (ms(a.get("venceEn")) > ahoraMs ? "activo" : "vencido");
    tx.update(aref, Object.assign({ estado, moderadoEn: Timestamp.fromMillis(ahoraMs) },
      accion === "reactivar" ? { denuncias: 0 } : {}));
    tx.set(aref.collection("moderacion").doc(), {
      accion,
      nota: String(nota || "").slice(0, 500),
      por: porUid,
      en: Timestamp.fromMillis(ahoraMs),
      estadoAntes: String(a.get("estado") || ""),
      denunciasAntes: Number(a.get("denuncias") || 0),
      motivos,
    });
    return { uid: String(a.get("uid") || ""), titulo: String(a.get("titulo") || ""), estado };
  });
  if (accion === "reactivar" && !denuncias.empty) {
    const lote = db.batch();
    denuncias.docs.forEach((d) => lote.delete(d.ref));
    await lote.commit();
  }
  return r;
}

/** Bloquear o reactivar una ficha de la vitrina de proveedores. */
async function moderarProveedor(db, { uid, accion, nota, porUid, ahoraMs }) {
  if (!uid || !ACCIONES_ANUNCIO.includes(accion)) throw new ErrorTienda("invalid-argument", "Acción no válida.");
  const pref = db.collection(COL_PROVEEDORES).doc(String(uid));
  return db.runTransaction(async (tx) => {
    const p = await tx.get(pref);
    if (!p.exists) throw new ErrorTienda("not-found", "No se encontró el proveedor.");
    const estado = accion === "bloquear" ? "bloqueado"
      : (ms(p.get("vigenteHasta")) > ahoraMs ? "activo" : "vencido");
    tx.update(pref, { estado, moderadoEn: Timestamp.fromMillis(ahoraMs), moderacionNota: String(nota || "").slice(0, 500), moderadoPor: porUid });
    return { uid: String(uid), nombre: String(p.get("nombre") || ""), estado };
  });
}

// ═══════════════════════════════ Vitrina de proveedores ═══════════════════════════════

/**
 * Cobra un período de la vitrina y guarda la ficha. Renovar suma desde el vencimiento si sigue
 * vigente. Una ficha bloqueada por el panel no se paga de nuevo.
 */
async function suscribirProveedor(db, { uid, patronUid, data, ahoraMs }) {
  soloElPatron(uid, patronUid, "La vitrina de proveedores");
  const v = validarProveedor(data, uid);
  if (!v.ok) throw new ErrorTienda("invalid-argument", v.error);
  const { precioCent, dias } = (await leerTarifas(db)).proveedor;
  const uref = db.collection("usuarios").doc(uid);
  const pref = db.collection(COL_PROVEEDORES).doc(uid);

  return db.runTransaction(async (tx) => {
    const [usuario, prov] = await Promise.all([tx.get(uref), tx.get(pref)]);
    if (prov.exists && prov.get("estado") === "bloqueado") {
      throw new ErrorTienda("failed-precondition", "Su vitrina está bloqueada. Escríbanos por Soporte.");
    }
    const restante = cobrar(tx, uref, usuario, precioCent, {
      tipo: "PAGO_PROVEEDOR", dias, titulo: v.ficha.nombre,
    });
    const vigenteMs = nuevoVencimiento(prov.exists ? ms(prov.get("vigenteHasta")) : 0, ahoraMs, dias);
    const ahora = Timestamp.fromMillis(ahoraMs);
    tx.set(pref, Object.assign({}, v.ficha, {
      uid,
      cuentaNombre: nombreDe(usuario),
      estado: "activo",
      vigenteHasta: Timestamp.fromMillis(vigenteMs),
      pagadoCent: FieldValue.increment(precioCent),
      actualizadoEn: ahora,
    }, prov.exists ? {} : { creadoEn: ahora }), { merge: true });
    return { ok: true, vigenteHasta: vigenteMs, saldoCent: restante, precioCent, dias };
  });
}

/** Cambiar la ficha sin pagar. La escribe el servidor para que `zonasClave` salga de la ciudad y las zonas. */
async function guardarProveedor(db, { uid, data, ahoraMs }) {
  const v = validarProveedor(data, uid);
  if (!v.ok) throw new ErrorTienda("invalid-argument", v.error);
  const pref = db.collection(COL_PROVEEDORES).doc(uid);
  return db.runTransaction(async (tx) => {
    const prov = await tx.get(pref);
    if (!prov.exists) throw new ErrorTienda("not-found", "Primero active su vitrina.");
    tx.update(pref, Object.assign({}, v.ficha, { actualizadoEn: Timestamp.fromMillis(ahoraMs) }));
    return { ok: true };
  });
}

// ═══════════════════════════════ Pedidos de clientes (web) ═══════════════════════════════

/** Un resumen irreversible (para contar sin guardar la IP ni el teléfono). */
function huella(texto) {
  return crypto.createHash("sha256").update("crystal-tienda|" + texto).digest("hex").slice(0, 32);
}

/** Suma uno al contador de la hora y del día de [clave]; lanza si pasa del tope. */
function contarVisita(tx, doc, ahoraMs, topeHora, topeDia) {
  const f = new Date(ahoraMs - 5 * 60 * 60 * 1000).toISOString(); // hora de Lima
  const hora = f.slice(0, 13);
  const dia = f.slice(0, 10);
  const d = doc.exists ? doc.data() : {};
  const nHora = d.hora === hora ? (d.nHora || 0) : 0;
  const nDia = d.dia === dia ? (d.nDia || 0) : 0;
  if ((topeHora && nHora >= topeHora) || nDia >= topeDia) {
    throw new ErrorTienda("resource-exhausted", "Ya envió varios pedidos. Intente más tarde.");
  }
  tx.set(doc.ref, { hora, nHora: nHora + 1, dia, nDia: nDia + 1 });
}

/**
 * Guarda el pedido que un cliente final deja en la web. Lo público queda en `pedidos_clientes/{id}`;
 * el nombre y el teléfono van a `privado/contacto`, que ninguna app lee: lo entrega `tomarPedido`.
 * Con `anuncioId` el pedido va dirigido al vendedor de ese anuncio, que lo toma gratis.
 */
async function crearPedidoCliente(db, { data, ip, ahoraMs }) {
  const t = await leerTarifas(db);
  if (!t.webPublica) throw new ErrorTienda("unavailable", "La tienda web no está disponible.");
  const v = validarPedidoCliente(data);
  if (!v.ok) throw new ErrorTienda("invalid-argument", v.error);

  let dirigido = null;
  if (v.anuncioId) {
    const a = await db.collection(COL).doc(v.anuncioId).get();
    if (!a.exists || !visible(a.data(), ahoraMs)) {
      throw new ErrorTienda("not-found", "Ese anuncio ya no está disponible.");
    }
    dirigido = { uid: a.get("uid"), titulo: String(a.get("titulo") || ""), ciudad: String(a.get("ciudad") || "") };
  }
  const publico = Object.assign({}, v.publico);
  if (dirigido && !publico.ciudad) {
    publico.ciudad = dirigido.ciudad;
    publico.ciudadClave = claveCiudad(dirigido.ciudad);
  }

  const pref = db.collection(COL_PEDIDOS).doc();
  const limIp = db.collection("limites_web").doc("ip_" + huella(String(ip || "?")));
  const limTel = db.collection("limites_web").doc("tel_" + huella(v.contacto.telefono));
  await db.runTransaction(async (tx) => {
    const [dIp, dTel] = await Promise.all([tx.get(limIp), tx.get(limTel)]);
    contarVisita(tx, dIp, ahoraMs, TOPE_POR_HORA, TOPE_POR_DIA);
    contarVisita(tx, dTel, ahoraMs, 0, TOPE_POR_TELEFONO);
    const ahora = Timestamp.fromMillis(ahoraMs);
    tx.set(pref, Object.assign(publico, {
      estado: "abierto",
      origen: "web",
      creadoEn: ahora,
      venceEn: Timestamp.fromMillis(ahoraMs + DIAS_PEDIDO * DIA_MS),
      tomas: 0,
      maxTalleres: dirigido ? 1 : t.pedido.maxTalleres,
      tomadoPor: [],
      reportes: 0,
      paraUid: dirigido ? dirigido.uid : "",
      anuncioId: v.anuncioId || "",
      anuncioTitulo: dirigido ? dirigido.titulo : "",
    }));
    tx.set(pref.collection("privado").doc("contacto"), Object.assign({ creadoEn: ahora }, v.contacto));
  });
  return {
    ok: true,
    pedidoId: pref.id,
    ciudad: publico.ciudad,
    que: publico.que,
    paraUid: dirigido ? dirigido.uid : "",
    anuncioTitulo: dirigido ? dirigido.titulo : "",
  };
}

/**
 * Un taller toma el pedido: paga (salvo que sea un pedido dirigido a su anuncio) y recibe el nombre y
 * el teléfono, que quedan en `pedidos_clientes/{id}/tomas/{uid}` (solo lo leen él y el panel; no va
 * bajo `usuarios/{uid}`, donde el dueño puede escribir cualquier subcolección). Tomarlo otra vez devuelve
 * lo mismo sin cobrar. El último que cabe lo cierra (`completo`).
 */
async function tomarPedidoCliente(db, { uid, patronUid, pedidoId, ahoraMs }) {
  soloElPatron(uid, patronUid, "Tomar pedidos");
  if (!idValido(pedidoId)) throw new ErrorTienda("invalid-argument", "Pedido no válido.");
  const t = await leerTarifas(db);
  const uref = db.collection("usuarios").doc(uid);
  const pref = db.collection(COL_PEDIDOS).doc(pedidoId);
  const cref = pref.collection("privado").doc("contacto");
  const tref = pref.collection("tomas").doc(uid);

  return db.runTransaction(async (tx) => {
    const [usuario, pedido, contacto, toma] = await Promise.all([tx.get(uref), tx.get(pref), tx.get(cref), tx.get(tref)]);
    if (toma.exists) {
      return { ok: true, yaTomado: true, nombre: toma.get("nombre"), telefono: toma.get("telefono"), precioCent: 0 };
    }
    if (!pedido.exists || !contacto.exists) throw new ErrorTienda("not-found", "No se encontró el pedido.");
    const p = pedido.data();
    const paraUid = String(p.paraUid || "");
    if (paraUid && paraUid !== uid) throw new ErrorTienda("permission-denied", "Este pedido es para otro vendedor.");
    if (p.estado !== "abierto" || ms(p.venceEn) <= ahoraMs) {
      throw new ErrorTienda("failed-precondition", "Este pedido ya no está disponible.");
    }
    const tomas = Number(p.tomas || 0);
    const max = Number(p.maxTalleres || 1);
    if (tomas >= max) throw new ErrorTienda("failed-precondition", "Este pedido ya lo tomaron otros talleres.");

    const precioCent = paraUid ? 0 : t.pedido.precioCent;
    const restante = cobrar(tx, uref, usuario, precioCent, {
      tipo: "TOMA_PEDIDO", pedidoId, titulo: String(p.que || p.anuncioTitulo || "Pedido"),
    });
    const nombre = String(contacto.get("nombre") || "");
    const telefono = String(contacto.get("telefono") || "");
    tx.set(tref, {
      uid,
      pedidoId,
      nombre,
      telefono,
      que: String(p.que || ""),
      ciudad: String(p.ciudad || ""),
      detalle: String(p.detalle || ""),
      medidas: String(p.medidas || ""),
      anuncioTitulo: String(p.anuncioTitulo || ""),
      pagadoCent: precioCent,
      tomadoEn: Timestamp.fromMillis(ahoraMs),
      reportado: false,
    });
    tx.update(pref, {
      tomas: tomas + 1,
      tomadoPor: FieldValue.arrayUnion(uid),
      estado: tomas + 1 >= max ? "completo" : "abierto",
    });
    return { ok: true, nombre, telefono, precioCent, saldoCent: restante };
  });
}

/** Quien tomó el pedido avisa que es falso (número equivocado, broma). Una vez por taller. */
async function reportarPedidoCliente(db, { uid, pedidoId, motivo, ahoraMs }) {
  if (!idValido(pedidoId)) throw new ErrorTienda("invalid-argument", "Pedido no válido.");
  const pref = db.collection(COL_PEDIDOS).doc(pedidoId);
  const tref = pref.collection("tomas").doc(uid);
  const rref = pref.collection("reportes").doc(uid);
  return db.runTransaction(async (tx) => {
    const [pedido, toma, reporte] = await Promise.all([tx.get(pref), tx.get(tref), tx.get(rref)]);
    if (!pedido.exists || !toma.exists) throw new ErrorTienda("permission-denied", "Solo lo reporta quien lo tomó.");
    if (reporte.exists) return { ok: true, yaReportado: true };
    const reportes = Number(pedido.get("reportes") || 0) + 1;
    tx.set(rref, { motivo: String(motivo || "").slice(0, 300), en: Timestamp.fromMillis(ahoraMs) });
    tx.update(tref, { reportado: true });
    tx.update(pref, { reportes });
    return { ok: true, reportes, que: String(pedido.get("que") || ""), ciudad: String(pedido.get("ciudad") || "") };
  });
}

/**
 * El panel resuelve un pedido reportado: `devolver` reintegra a cada taller lo que pagó y lo
 * bloquea; `desestimar` deja los reportes en cero.
 */
async function resolverPedidoCliente(db, { pedidoId, accion, porUid, ahoraMs }) {
  if (!idValido(pedidoId) || !["devolver", "desestimar"].includes(accion)) {
    throw new ErrorTienda("invalid-argument", "Acción no válida.");
  }
  const pref = db.collection(COL_PEDIDOS).doc(pedidoId);
  return db.runTransaction(async (tx) => {
    const pedido = await tx.get(pref);
    if (!pedido.exists) throw new ErrorTienda("not-found", "No se encontró el pedido.");
    if (accion === "desestimar") {
      tx.update(pref, { reportes: 0, revisadoPor: porUid, revisadoEn: Timestamp.fromMillis(ahoraMs) });
      return { ok: true, devueltos: 0 };
    }
    if (pedido.get("estado") === "bloqueado") return { ok: true, devueltos: 0, yaResuelto: true };
    const uids = (pedido.get("tomadoPor") || []).map(String);
    const lecturas = await Promise.all(uids.map(async (u) => {
      const uref = db.collection("usuarios").doc(u);
      const [usuario, toma] = await Promise.all([tx.get(uref), tx.get(pref.collection("tomas").doc(u))]);
      return { uref, usuario, toma };
    }));
    let devueltos = 0;
    for (const { uref, usuario, toma } of lecturas) {
      const pagado = toma.exists ? Number(toma.get("pagadoCent") || 0) : 0;
      if (pagado <= 0 || !usuario.exists) continue;
      const saldo = Number(usuario.get("wallet_saldo_cent") || 0) + pagado;
      tx.update(uref, { wallet_saldo_cent: saldo, "wallet.saldo": saldo / 100 });
      tx.set(uref.collection("wallet_movs").doc(), {
        tipo: "DEVUELVE_PEDIDO", monto_cent: pagado, pedidoId, created_at: FieldValue.serverTimestamp(),
      });
      tx.update(toma.ref, { devueltoCent: pagado });
      devueltos++;
    }
    tx.update(pref, { estado: "bloqueado", revisadoPor: porUid, revisadoEn: Timestamp.fromMillis(ahoraMs) });
    return { ok: true, devueltos };
  });
}

// ═══════════════════════════════ Precios de referencia ═══════════════════════════════

/**
 * Mínimo, mediana y máximo de lo que se pide por algo en una ciudad y en todo el país, con los
 * anuncios de los últimos 180 días. Es del plan FULL: sin él responde `PLAN_FULL`.
 */
async function preciosReferencia(db, { uid, texto, ciudad, ahoraMs }) {
  const usuario = await db.collection("usuarios").doc(uid).get();
  if (!planAvanzado(usuario.exists ? usuario.get("estado_servicio") : null, ahoraMs)) {
    throw new ErrorTienda("failed-precondition", "PLAN_FULL");
  }
  const palabra = palabraDeConsulta(String(texto || ""));
  if (!palabra) throw new ErrorTienda("invalid-argument", "Escriba qué busca, por ejemplo «templado 8».");
  const snap = await db.collection(COL)
    .where("palabras", "array-contains-any", variantes(palabra))
    .where("publicadoEn", ">=", Timestamp.fromMillis(ahoraMs - DIAS_REFERENCIA * DIA_MS))
    .orderBy("publicadoEn", "desc")
    .limit(500)
    .get();
  const anuncios = snap.docs.map((d) => {
    const a = d.data();
    return {
      uid: a.uid, precioCent: Number(a.precioCent || 0), unidad: a.unidad, ciudad: a.ciudad, zonas: a.zonas,
      titulo: a.titulo, categoria: a.categoria, tipo: a.tipo, descripcion: a.descripcion, estado: a.estado,
      publicadoMs: ms(a.publicadoEn),
    };
  });
  return {
    ok: true,
    ciudad: estadisticaPrecios(anuncios, { texto, ciudad: String(ciudad || "") }),
    pais: estadisticaPrecios(anuncios, { texto, ciudad: "" }),
    revisados: anuncios.length,
  };
}

// ═══════════════════════════════ Web pública ═══════════════════════════════

const PAGINA_WEB = 30;

/**
 * Una página de la tienda para la web, con los campos públicos. Igual que la app: al servidor se le
 * pide un filtro (palabra, si no categoría, si no tipo) y lo demás se filtra aquí. La primera página
 * trae arriba los destacados vigentes.
 */
async function listarWeb(db, { q, categoria, tipo, despuesMs, ahoraMs }) {
  const t = await leerTarifas(db);
  if (!t.webPublica) throw new ErrorTienda("unavailable", "La tienda web no está disponible.");
  const texto = String(q || "").slice(0, 80);
  const cat = CATEGORIAS.includes(categoria) ? categoria : null;
  const tip = TIPOS.includes(tipo) ? tipo : null;
  const filtra = (a) => visible(a, ahoraMs) && (!cat || a.categoria === cat) && (!tip || a.tipo === tip) &&
    (!texto.trim() || casaTexto(a, texto));

  let consulta = db.collection(COL).where("estado", "==", "activo");
  const palabra = palabraDeConsulta(texto);
  if (palabra) consulta = consulta.where("palabras", "array-contains-any", variantes(palabra));
  else if (cat) consulta = consulta.where("categoria", "==", cat);
  else if (tip) consulta = consulta.where("tipo", "==", tip);
  consulta = consulta.orderBy("publicadoEn", "desc").limit(PAGINA_WEB);
  if (despuesMs > 0) consulta = consulta.startAfter(Timestamp.fromMillis(despuesMs));
  const snap = await consulta.get();

  // Los destacados son un adorno de la primera página: si su consulta falla (un índice que se está
  // construyendo, por ejemplo), la lista sale igual, sin ellos, como en la app.
  let destacados = [];
  if (!(despuesMs > 0)) {
    try {
      const d = await db.collection(COL).where("estado", "==", "activo")
        .where("destacadoHasta", ">", Timestamp.fromMillis(ahoraMs))
        .orderBy("destacadoHasta", "asc").limit(10).get();
      destacados = d.docs.filter((x) => filtra(x.data())).map((x) => anuncioPublico(x.id, x.data(), false, ahoraMs));
    } catch (e) {
      destacados = [];
    }
  }
  const yaArriba = new Set(destacados.map((a) => a.id));
  const normales = snap.docs.filter((x) => !yaArriba.has(x.id) && filtra(x.data()))
    .map((x) => anuncioPublico(x.id, x.data(), false, ahoraMs));
  const ultimo = snap.docs[snap.docs.length - 1];
  return {
    anuncios: destacados.concat(normales),
    siguiente: snap.size === PAGINA_WEB && ultimo ? ms(ultimo.get("publicadoEn")) : 0,
  };
}

/** Un anuncio para la web, o null si ya no se muestra. */
async function detalleWeb(db, { id, ahoraMs }) {
  const t = await leerTarifas(db);
  if (!t.webPublica) throw new ErrorTienda("unavailable", "La tienda web no está disponible.");
  if (!idValido(id)) return null;
  const d = await db.collection(COL).doc(id).get();
  if (!d.exists || !visible(d.data(), ahoraMs)) return null;
  return anuncioPublico(d.id, d.data(), true, ahoraMs);
}

module.exports = {
  ErrorTienda,
  publicar,
  renovar,
  destacar,
  expirar,
  contarDenuncia,
  moderarAnuncio,
  moderarProveedor,
  suscribirProveedor,
  guardarProveedor,
  crearPedidoCliente,
  tomarPedidoCliente,
  reportarPedidoCliente,
  resolverPedidoCliente,
  preciosReferencia,
  listarWeb,
  detalleWeb,
  DENUNCIAS_PARA_REVISION,
};
