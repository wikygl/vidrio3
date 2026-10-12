// Tienda de anuncios del rubro (plan en docs/TIENDA_ANUNCIOS_PLAN.md).
//
// Cada anuncio se paga con el saldo del wallet, igual que un plan (`activarPlan`): el servidor
// decide precio y vencimiento, descuenta en una transacción y deja el movimiento en `wallet_movs`.
// No se emite boleta aquí: ya salió al recargar el saldo.

// Timestamp y FieldValue se toman del módulo de Firestore y no de `admin.firestore.*`: en el entorno
// de funciones `admin.firestore` puede venir envuelto y sin esas propiedades.
const { Timestamp, FieldValue } = require("firebase-admin/firestore");
const {
  precioVigente,
  validarAnuncio,
  idValido,
  nuevoVencimiento,
} = require("./comun");

const COL = "anuncios";
/** A cuántas denuncias de usuarios distintos un anuncio sale de la lista hasta que lo revisen. */
const DENUNCIAS_PARA_REVISION = 3;

class ErrorTienda extends Error {
  constructor(codigo, mensaje) {
    super(mensaje);
    this.codigo = codigo;
  }
}

async function leerPrecio(db) {
  try {
    const snap = await db.collection("config").doc("anuncios").get();
    return precioVigente(snap.exists ? snap.data() : null);
  } catch (e) {
    return precioVigente(null);
  }
}

function nombreDe(usuario) {
  if (!usuario || !usuario.exists) return "";
  return String(usuario.get("nombre") || usuario.get("perfil.nombre") || "").trim();
}

/**
 * Cobra y publica. `uid` es quien llama; `patronUid` es el claim de una terminal: las terminales no
 * publican, lo hace la cuenta del patrón (el cobro sale del wallet de quien llama).
 */
async function publicar(db, { uid, patronUid, data, ahoraMs }) {
  if (patronUid && patronUid !== uid) {
    throw new ErrorTienda("permission-denied", "Los anuncios se publican desde la cuenta del patrón.");
  }
  const anuncioId = String((data && data.anuncioId) || "");
  if (!idValido(anuncioId)) throw new ErrorTienda("invalid-argument", "Anuncio no válido.");

  const v = validarAnuncio(data, uid, anuncioId);
  if (!v.ok) throw new ErrorTienda("invalid-argument", v.error);

  const { precioCent, dias } = await leerPrecio(db);
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
    const saldo = (usuario.exists && usuario.get("wallet_saldo_cent")) || 0;
    if (saldo < precioCent) throw new ErrorTienda("failed-precondition", "SALDO_INSUFICIENTE");

    const venceMs = nuevoVencimiento(0, ahoraMs, dias);
    const restante = saldo - precioCent;
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
    if (precioCent > 0) {
      tx.update(uref, { wallet_saldo_cent: restante, "wallet.saldo": restante / 100 });
      tx.set(uref.collection("wallet_movs").doc(), {
        tipo: "PAGO_ANUNCIO",
        monto_cent: -precioCent,
        dias,
        anuncioId,
        titulo: v.anuncio.titulo,
        created_at: FieldValue.serverTimestamp(),
      });
    }
    return { ok: true, anuncioId, venceEn: venceMs, saldoCent: restante, precioCent, dias };
  });
}

/**
 * Cobra otro período y lo suma: desde el vencimiento si sigue vigente, desde hoy si ya venció. Un
 * anuncio retirado o pausado vuelve a estar activo; uno en revisión o bloqueado, no.
 */
async function renovar(db, { uid, anuncioId, ahoraMs }) {
  if (!idValido(anuncioId)) throw new ErrorTienda("invalid-argument", "Anuncio no válido.");
  const { precioCent, dias } = await leerPrecio(db);
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
    const saldo = (usuario.exists && usuario.get("wallet_saldo_cent")) || 0;
    if (saldo < precioCent) throw new ErrorTienda("failed-precondition", "SALDO_INSUFICIENTE");

    const vence = anuncio.get("venceEn");
    const venceMs = nuevoVencimiento(vence && vence.toMillis ? vence.toMillis() : 0, ahoraMs, dias);
    const restante = saldo - precioCent;

    tx.update(aref, {
      estado: "activo",
      venceEn: Timestamp.fromMillis(venceMs),
      // Renovado sube arriba en la lista, como uno recién publicado: es lo que se está pagando.
      publicadoEn: Timestamp.fromMillis(ahoraMs),
      pagadoCent: FieldValue.increment(precioCent),
    });
    if (precioCent > 0) {
      tx.update(uref, { wallet_saldo_cent: restante, "wallet.saldo": restante / 100 });
      tx.set(uref.collection("wallet_movs").doc(), {
        tipo: "RENUEVA_ANUNCIO",
        monto_cent: -precioCent,
        dias,
        anuncioId,
        titulo: String(anuncio.get("titulo") || ""),
        created_at: FieldValue.serverTimestamp(),
      });
    }
    return { ok: true, anuncioId, venceEn: venceMs, saldoCent: restante, precioCent, dias };
  });
}

/** Pasa a `vencido` lo activo o pausado cuyo plazo ya pasó. Corre una vez al día. */
async function expirar(db, ahoraMs) {
  const ahora = Timestamp.fromMillis(ahoraMs);
  let total = 0;
  for (const estado of ["activo", "pausado"]) {
    // De a 300 por vuelta: un lote de Firestore admite 500 escrituras.
    for (;;) {
      const snap = await db.collection(COL)
        .where("estado", "==", estado)
        .where("venceEn", "<", ahora)
        .limit(300)
        .get();
      if (snap.empty) break;
      const lote = db.batch();
      snap.docs.forEach((d) => lote.update(d.ref, { estado: "vencido" }));
      await lote.commit();
      total += snap.size;
      if (snap.size < 300) break;
    }
  }
  return { vencidos: total };
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

module.exports = { ErrorTienda, publicar, renovar, expirar, contarDenuncia, DENUNCIAS_PARA_REVISION };
