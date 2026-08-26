const admin = require("firebase-admin");
const functions = require("firebase-functions/v1");
const facturacion = require("./facturacion");
const correo = require("./correo");

admin.initializeApp();

// Catálogo de planes FULL — fuente de verdad en el servidor (el cliente NO decide precio ni duración).
const PLANES = {
  PREPAGO: { precioCent: 100, dias: 1 },
  MENSUAL: { precioCent: 1500, dias: 30 },
  ANUAL: { precioCent: 15000, dias: 365 },
};

/**
 * Activa un plan FULL de forma segura: descuenta el saldo y fija `estado_servicio.full_until`
 * calculado en el servidor. Reemplaza la activación por transacción del cliente (que permitía
 * poner cualquier vencimiento). Si el plan sigue vigente, extiende desde su fin (no pierde días).
 */
exports.activarPlan = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Inicia sesión.");
  }
  const uid = context.auth.uid;
  const tipo = String((data && data.tipo) || "").toUpperCase();
  const plan = PLANES[tipo];
  if (!plan) {
    throw new functions.https.HttpsError("invalid-argument", "Plan no válido.");
  }

  const db = admin.firestore();
  const uref = db.collection("usuarios").doc(uid);

  return db.runTransaction(async (tx) => {
    const snap = await tx.get(uref);
    const saldo = snap.get("wallet_saldo_cent") || 0;
    if (saldo < plan.precioCent) {
      throw new functions.https.HttpsError("failed-precondition", "SALDO_INSUFICIENTE");
    }

    const ahoraMs = Date.now();
    const actual = snap.get("estado_servicio.full_until");
    const baseMs = actual && actual.toMillis && actual.toMillis() > ahoraMs
      ? actual.toMillis()
      : ahoraMs;
    const finMs = baseMs + plan.dias * 24 * 60 * 60 * 1000;
    const fin = admin.firestore.Timestamp.fromMillis(finMs);

    tx.update(uref, {
      wallet_saldo_cent: saldo - plan.precioCent,
      estado_servicio: {
        mode: "FULL",
        source: tipo,
        full_until: fin,
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      },
    });
    const movRef = uref.collection("wallet_movs").doc();
    tx.set(movRef, {
      tipo: "PAGO_PLAN_" + tipo,
      monto_cent: -plan.precioCent,
      dias: plan.dias,
      created_at: admin.firestore.FieldValue.serverTimestamp(),
    });

    return { ok: true, mode: "FULL", full_until: finMs };
  });
});

/**
 * Hora del servidor en ms. La usa el cliente para anclar su reloj anti-retroceso: cuando hay
 * conexión, `maxSeenMillis` se fija a esta hora autoritativa, así el reloj (adelantado o atrasado)
 * del equipo deja de importar en cuanto estuvo online al menos una vez. Solo lectura, sin efectos.
 */
exports.serverTime = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Inicia sesión.");
  }
  return { now: Date.now() };
});

// ===================================================================================
//  RECARGA POR MONTO ÚNICO (firma de céntimos) — dinero real, agnóstico al formato.
//  El usuario reserva un monto (soles enteros + céntimos-firma) y paga EXACTAMENTE eso.
//  Al llegar el pago (Yape→depositos_confirmados, Plin→registros), se casa por MONTO y se
//  acredita el monto exacto a wallet_saldo_cent. No hay OCR ni parser por billetera.
// ===================================================================================

const RESERVA_VENTANA_MS = 30 * 60 * 1000; // vigencia de la reserva
const GRACIA_MS = 24 * 60 * 60 * 1000;     // ventana de gracia tras expirar (pagos/comprobantes tardíos)
const MAX_RECARGA_CENT = 100000;           // tope DURO: nada acredita > S/1000 (reservarRecarga limita a 999)

// Céntimos "redondos" comunes en pagos reales; se excluyen de la firma para no chocar con ellos.
const CENTIMOS_PROHIBIDOS = new Set([0, 50, 90, 99, 25, 75]);

// Un CrystalServer se considera VIVO si latió hace menos de esto.
const LATIDO_VENTANA_MS = 35 * 60 * 1000;

// Elige el número de pago: el PRIMARIO (menor 'prioridad') que esté VIVO; si el primario cayó, el
// siguiente vivo; si ninguno late, cae al número configurado (config/pagos) para no bloquear el pago.
// Así el número se mantiene estable (el primario) salvo cuando de verdad se cae.
async function elegirNumeroPago(db) {
  const ahora = Date.now();
  let servers = [];
  try {
    const snap = await db.collection("servidores").get();
    servers = snap.docs
      .map((d) => ({
        numero: String(d.get("numero") || ""),
        prioridad: typeof d.get("prioridad") === "number" ? d.get("prioridad") : 99,
        lastSeenMs: Number(d.get("lastSeenMs") || 0),
      }))
      .filter((s) => s.numero);
  } catch (e) { /* noop */ }
  const vivos = servers
    .filter((s) => (ahora - s.lastSeenMs) < LATIDO_VENTANA_MS)
    .sort((a, b) => a.prioridad - b.prioridad);
  if (vivos.length > 0) return vivos[0].numero;            // primario vivo (o el siguiente vivo)
  if (servers.length > 0) {
    servers.sort((a, b) => a.prioridad - b.prioridad);
    return servers[0].numero;                              // ninguno vivo → primario conocido (degradado)
  }
  try {
    const cfg = await db.collection("config").doc("pagos").get();
    return String(cfg.get("numeroYape") || cfg.get("numero") || "");
  } catch (e) { return ""; }
}

// Reserva un monto único para el usuario. NO otorga dinero: solo lo acredita el pago real + comprobante.
exports.reservarRecarga = functions.https.onCall(async (data, context) => {
  if (!context.auth) throw new functions.https.HttpsError("unauthenticated", "Inicia sesión.");
  const uid = context.auth.uid;
  const soles = parseInt((data && data.soles) || 0, 10);
  if (!soles || soles < 1 || soles > 999) {
    throw new functions.https.HttpsError("invalid-argument", "Monto inválido (1 a 999 soles).");
  }
  const baseCent = soles * 100;
  const ahora = Date.now();
  const db = admin.firestore();
  const col = db.collection("reservas_recarga");
  const numeroAsignado = await elegirNumeroPago(db); // primario vivo (failover automático)

  return db.runTransaction(async (tx) => {
    // Céntimos ya ocupados por reservas activas de la MISMA base.
    const q = await tx.get(col.where("baseCent", "==", baseCent).where("estado", "==", "esperando"));
    const ocupados = new Set();
    q.docs.forEach((d) => { if ((d.get("expiraMs") || 0) > ahora) ocupados.add(d.get("cc")); });
    const disponibles = [];
    for (let c = 1; c <= 99; c++) { if (!CENTIMOS_PROHIBIDOS.has(c) && !ocupados.has(c)) disponibles.push(c); }
    if (disponibles.length === 0) {
      throw new functions.https.HttpsError("resource-exhausted", "Demasiadas recargas en curso; intenta en unos minutos.");
    }
    const cc = disponibles[Math.floor(Math.random() * disponibles.length)];
    const totalCent = baseCent + cc;
    const ref = col.doc();
    tx.set(ref, {
      uid, baseCent, cc, totalCent,
      estado: "esperando",
      pagoRecibido: false,      // ← lo marca el match por monto (CrystalServer)
      tieneComprobante: false,  // ← lo marca adjuntarComprobante (obligatorio)
      creadoMs: ahora,
      expiraMs: ahora + RESERVA_VENTANA_MS,
      numeroAsignado,
      creadoEn: admin.firestore.FieldValue.serverTimestamp(),
    });
    return {
      reservaId: ref.id,
      totalCent,
      totalTexto: `S/ ${(totalCent / 100).toFixed(2)}`,
      expiraMs: ahora + RESERVA_VENTANA_MS,
      numero: numeroAsignado,
    };
  });
});

// Acredita SOLO si hay pago casado por monto Y comprobante adjunto (y no expiró). Idempotente.
async function intentarAcreditarReserva(db, reservaRef) {
  return db.runTransaction(async (tx) => {
    const s = await tx.get(reservaRef);
    if (!s.exists) return { ok: false, motivo: "NO_EXISTE" };
    const estado = s.get("estado");
    if (estado === "aplicado") return { ok: false, motivo: "YA_APLICADO" };   // idempotente
    if (estado !== "esperando" && estado !== "expirada") return { ok: false, motivo: "NO_ACREDITABLE" };
    // Gracia: acredita mientras esté dentro de expiraMs + GRACIA_MS (aunque ya haya expirado).
    if (Date.now() > (s.get("expiraMs") || 0) + GRACIA_MS) {
      if (estado === "esperando") tx.update(reservaRef, { estado: "expirada" });
      return { ok: false, motivo: "FUERA_DE_GRACIA" };
    }
    if (!s.get("pagoRecibido")) return { ok: false, motivo: "SIN_PAGO" };
    if (!s.get("tieneComprobante")) return { ok: false, motivo: "SIN_COMPROBANTE" };
    const montoCent = s.get("totalCent");
    if (!(montoCent > 0) || montoCent > MAX_RECARGA_CENT) return { ok: false, motivo: "MONTO_INVALIDO" }; // tope duro
    const uid = s.get("uid");
    const porGracia = estado === "expirada" || Date.now() > (s.get("expiraMs") || 0);
    const userRef = db.collection("usuarios").doc(uid);
    const uSnap = await tx.get(userRef);
    const saldo = uSnap.get("wallet_saldo_cent") || 0;
    const nuevo = saldo + montoCent;
    tx.set(userRef, { wallet_saldo_cent: nuevo, "wallet.saldo": nuevo / 100 }, { merge: true });
    tx.update(reservaRef, {
      estado: "aplicado",
      acreditadoCent: montoCent,
      porGracia,
      aplicadoEn: admin.firestore.FieldValue.serverTimestamp(),
    });
    tx.set(userRef.collection("wallet_movs").doc(), {
      tipo: "RECARGA_MONTO_UNICO",
      monto_cent: montoCent,
      reservaId: reservaRef.id,
      porGracia,
      created_at: admin.firestore.FieldValue.serverTimestamp(),
    });
    // Comprobante electrónico. Solo deja el encargo escrito: emitir aquí sería llamar a una API
    // externa dentro de una transacción que se reintenta sola, y saldrían boletas duplicadas.
    facturacion.encolarComprobante(tx, db, {
      uid, montoCent, reservaId: reservaRef.id, ahoraMs: Date.now(),
    });
    return { ok: true, uid, montoCent, porGracia };
  });
}

// Marca "pago recibido" en la reserva que coincide por monto; NO acredita si aún falta comprobante.
async function marcarPagoEnReserva(db, montoCent, fuenteRef, fuenteInfo) {
  const ahora = Date.now();
  // 1) Preferir la reserva ACTIVA por ese monto (evita casar una expirada vieja si hay una nueva).
  let reservaRef = null;
  const activa = await db.collection("reservas_recarga")
    .where("totalCent", "==", montoCent).where("estado", "==", "esperando").limit(1).get();
  if (!activa.empty) {
    reservaRef = activa.docs[0].ref;
  } else {
    // 2) Gracia: una EXPIRADA reciente (dentro de la ventana) por ese mismo monto, la más nueva.
    const exp = await db.collection("reservas_recarga")
      .where("totalCent", "==", montoCent).where("estado", "==", "expirada").limit(10).get();
    const cand = exp.docs
      .filter((d) => (ahora - (d.get("expiraMs") || 0)) <= GRACIA_MS)
      .sort((a, b) => (b.get("expiraMs") || 0) - (a.get("expiraMs") || 0))[0];
    if (cand) reservaRef = cand.ref;
  }
  if (!reservaRef) return { ok: false, motivo: "SIN_RESERVA" };
  await db.runTransaction(async (tx) => {
    const s = await tx.get(reservaRef);
    if (!s.exists) return;
    const estado = s.get("estado");
    if (estado !== "esperando" && estado !== "expirada") return;   // ya aplicada/reclamada
    if (ahora > (s.get("expiraMs") || 0) + GRACIA_MS) return;       // fuera de gracia
    tx.update(reservaRef, {
      pagoRecibido: true,
      pagoInfo: fuenteInfo || null,
      pagoEn: admin.firestore.FieldValue.serverTimestamp(),
    });
    if (fuenteRef) {
      tx.set(fuenteRef, { estado: "usado", usadoPor: s.get("uid"), usadoEn: admin.firestore.Timestamp.now() }, { merge: true });
    }
  });
  return intentarAcreditarReserva(db, reservaRef); // acredita si hay comprobante y sigue en gracia
}

// El cliente adjunta el comprobante (evidencia obligatoria) a su reserva. Acredita si ya hubo pago.
exports.adjuntarComprobante = functions.https.onCall(async (data, context) => {
  if (!context.auth) throw new functions.https.HttpsError("unauthenticated", "Inicia sesión.");
  const uid = context.auth.uid;
  const reservaId = String((data && data.reservaId) || "");
  const voucherPath = String((data && data.voucherPath) || "");
  const voucherUrl = String((data && data.voucherUrl) || "");
  if (!reservaId || !voucherUrl) throw new functions.https.HttpsError("invalid-argument", "Faltan datos del comprobante.");
  const db = admin.firestore();
  const reservaRef = db.collection("reservas_recarga").doc(reservaId);
  await db.runTransaction(async (tx) => {
    const s = await tx.get(reservaRef);
    if (!s.exists) throw new functions.https.HttpsError("not-found", "Reserva no encontrada.");
    if (s.get("uid") !== uid) throw new functions.https.HttpsError("permission-denied", "No es tu reserva.");
    tx.update(reservaRef, {
      tieneComprobante: true,
      voucherPath, voucherUrl,
      comprobanteEn: admin.firestore.FieldValue.serverTimestamp(),
    });
  });
  const r = await intentarAcreditarReserva(db, reservaRef);
  return { ok: true, acreditado: r.ok, motivo: r.motivo || null };
});

// Reclamo de una reserva EXPIRADA (el pago se hizo pero venció la ventana). Crea un reclamo con el
// comprobante para revisión manual (reusa el pipeline reclamo_manual → notificarRecargaPendiente).
// Texto de una notificación capturada que indica dinero ENTRANDO. "Plineaste S/ X a …" y
// "TRAN S/ X A: …" son lo contrario —dinero saliendo— y no prueban que el taller haya cobrado.
const ENTRANTE = /te envi[oó]|te ha plinead|recibiste|abono|dep[oó]sito a tu|pago por s\//i;
const RUIDO_CAPTURA = /shopstar|dscto|descuento|consumo con tu tarjeta|playlist|oferta|cuotas|promoci/i;

/**
 * Busca en lo que capturó CrystalServer un pago por ese monto exacto, alrededor de la reserva.
 *
 * Es la comprobación que el administrador haría a mano abriendo Yape: se hace sola y se adjunta al
 * reclamo, para que resolverlo sea mirar y pulsar. No decide nada por su cuenta — solo aporta la
 * evidencia; acreditar sigue siendo un acto humano.
 */
async function buscarPagoCapturado(db, montoCent, desdeMs, hastaMs) {
  const monto = montoCent / 100;
  const hallazgos = [];

  // Las notificaciones del capturador. `listDocuments` hace falta porque los documentos padre de
  // notificaciones_servidor no existen como tales: solo existen sus subcolecciones.
  try {
    const devices = await db.collection("notificaciones_servidor").listDocuments();
    for (const dev of devices) {
      const snap = await dev.collection("registros").where("monto", "==", monto).limit(50).get();
      snap.docs.forEach((d) => {
        // El id del registro es el instante en milisegundos en que se capturó.
        const ms = Number(d.id);
        if (!Number.isFinite(ms) || ms < desdeMs || ms > hastaMs) return;
        const texto = String(d.get("contenido") || "");
        if (RUIDO_CAPTURA.test(texto)) return;
        hallazgos.push({
          ruta: d.ref.path,
          texto: texto.slice(0, 200),
          ms,
          entrante: ENTRANTE.test(texto),
          usado: d.get("estado") === "usado",
        });
      });
    }
  } catch (e) {
    functions.logger.error("buscarPagoCapturado: registros", { error: String(e) });
  }

  // Los depósitos que llegan ya normalizados (el otro camino del mismo capturador).
  try {
    const snap = await db.collection("depositos_confirmados").where("monto", "==", monto).limit(20).get();
    snap.docs.forEach((d) => {
      const f = d.get("fecha") || d.get("creadoEn");
      const ms = f && f.toMillis ? f.toMillis() : 0;
      if (ms && (ms < desdeMs || ms > hastaMs)) return;
      hallazgos.push({
        ruta: d.ref.path,
        texto: "Depósito confirmado " + d.id,
        ms,
        entrante: true,
        usado: d.get("estado") === "usado",
      });
    });
  } catch (e) {
    functions.logger.error("buscarPagoCapturado: depositos", { error: String(e) });
  }

  // Un mismo pago genera hasta tres notificaciones (el banco, la billetera y el aviso de cobro), así
  // que se ordena poniendo delante lo entrante y sin usar, que es lo que hay que mirar primero.
  hallazgos.sort((a, b) => (b.entrante - a.entrante) || (a.usado - b.usado) || (b.ms - a.ms));
  return hallazgos.slice(0, 6);
}

/**
 * El usuario afirma que sí pagó, declarando cuánto pagó de verdad.
 *
 * Nace de un caso real y repetido: la costumbre de pagar cantidades redondas. Se reserva S/1.14
 * —los céntimos son la firma que identifica el pago— y se yapea S/1.00. El dinero llega, pero no
 * casa con ninguna reserva y se queda sin dueño. Sin esto el usuario no tiene forma de avisar.
 *
 * El monto declarado NO se cree a ciegas: queda escrito junto al reservado y junto a lo que el
 * servidor capturó de verdad, para que las tres cifras se vean a la vez al resolver.
 */
exports.reclamarReserva = functions.https.onCall(async (data, context) => {
  if (!context.auth) throw new functions.https.HttpsError("unauthenticated", "Inicia sesión.");
  const uid = context.auth.uid;
  const reservaId = String((data && data.reservaId) || "");
  if (!reservaId) throw new functions.https.HttpsError("invalid-argument", "Falta reservaId.");

  const db = admin.firestore();
  const rref = db.collection("reservas_recarga").doc(reservaId);
  const s = await rref.get();
  if (!s.exists) throw new functions.https.HttpsError("not-found", "Reserva no encontrada.");
  if (s.get("uid") !== uid) throw new functions.https.HttpsError("permission-denied", "No es tu reserva.");
  if (s.get("estado") === "aplicado") throw new functions.https.HttpsError("failed-precondition", "Ya fue acreditada.");
  const voucherUrl = s.get("voucherUrl");
  if (!voucherUrl) throw new functions.https.HttpsError("failed-precondition", "SIN_COMPROBANTE");

  const reservadoCent = Number(s.get("totalCent") || 0);
  // Sin declaración se asume que pagó lo reservado, que es el caso de la recarga que simplemente
  // venció. El tope duro se aplica también aquí: nada entra por encima de él.
  const pagadoCent = Math.round(Number((data && data.montoPagadoCent) || reservadoCent));
  if (!(pagadoCent > 0) || pagadoCent > MAX_RECARGA_CENT) {
    throw new functions.https.HttpsError("invalid-argument", "Monto pagado no válido.");
  }

  const creadoMs = Number(s.get("creadoMs") || Date.now());
  // Ventana de búsqueda: desde media hora ANTES de reservar —se paga primero y se reserva después
  // más a menudo de lo que parece— hasta el final de las 24 horas de gracia.
  const hallazgos = await buscarPagoCapturado(db, pagadoCent, creadoMs - 30 * 60 * 1000, creadoMs + GRACIA_MS);
  const coincide = hallazgos.some((h) => h.entrante && !h.usado);

  // Id determinista: reclamar dos veces la misma reserva actualiza el reclamo, no crea otro.
  const cref = db.collection("reclamos_recarga").doc(reservaId);
  const previo = await cref.get();
  if (previo.exists && previo.get("estado") === "resuelto") {
    throw new functions.https.HttpsError("failed-precondition", "Este reclamo ya fue resuelto.");
  }
  await cref.set({
    uid,
    reservaId,
    estado: "nuevo",
    reservadoCent,
    pagadoCent,
    voucherUrl,
    numeroAsignado: s.get("numeroAsignado") || "",
    correo: (context.auth.token && context.auth.token.email) || "",
    hallazgos,
    coincide,
    creadoEn: admin.firestore.FieldValue.serverTimestamp(),
  }, { merge: true });

  await rref.update({ estado: "reclamada", reclamadaEn: admin.firestore.FieldValue.serverTimestamp() });
  return { ok: true, coincide, hallazgos: hallazgos.length };
});

/**
 * El administrador resuelve el reclamo: acredita lo que de verdad entró, o lo rechaza.
 *
 * El monto lo decide él mirando las tres cifras —reservado, declarado y capturado— más el
 * comprobante; el servidor solo impone el tope duro y la idempotencia. Deja rastro en `wallet_movs`
 * con el reclamo y la evidencia usada, para poder reconstruir después cualquier acreditación.
 */
exports.resolverReclamoRecarga = functions.https.onCall(async (data, context) => {
  const callerUid = context.auth && context.auth.uid;
  if (!callerUid) throw new functions.https.HttpsError("unauthenticated", "No autenticado.");
  const db = admin.firestore();
  const rol = await db.collection("admin_roles").doc(callerUid).get();
  if ((rol.data() || {}).role !== "admin") {
    throw new functions.https.HttpsError("permission-denied", "Solo un administrador acredita.");
  }

  const reclamoId = String((data && data.reclamoId) || "");
  const accion = String((data && data.accion) || "");
  const motivo = String((data && data.motivo) || "").trim();
  const evidencia = String((data && data.evidencia) || "");
  if (!reclamoId) throw new functions.https.HttpsError("invalid-argument", "Falta reclamoId.");
  if (!["acreditar", "rechazar"].includes(accion)) {
    throw new functions.https.HttpsError("invalid-argument", "Acción no válida.");
  }
  if (!motivo) throw new functions.https.HttpsError("invalid-argument", "El motivo es obligatorio.");

  let montoCent = 0;
  if (accion === "acreditar") {
    montoCent = Math.round(Number((data && data.montoCent) || 0));
    if (!(montoCent > 0) || montoCent > MAX_RECARGA_CENT) {
      throw new functions.https.HttpsError("invalid-argument", "Monto no válido.");
    }
  }

  const cref = db.collection("reclamos_recarga").doc(reclamoId);
  const res = await db.runTransaction(async (tx) => {
    const c = await tx.get(cref);
    if (!c.exists) throw new functions.https.HttpsError("not-found", "Reclamo no encontrado.");
    if (c.get("estado") === "resuelto") return { ok: true, yaEstaba: true };   // idempotente

    // La reserva es lo que el usuario ve en su historial. Dejarla en "reclamada" después de
    // resolver le enseña "en revisión" para siempre: información falsa sobre su propio dinero.
    const reservaId = c.get("reservaId") || "";
    const rref = reservaId ? db.collection("reservas_recarga").doc(reservaId) : null;

    if (accion === "rechazar") {
      tx.update(cref, {
        estado: "resuelto", resultado: "rechazado", motivo, adminUid: callerUid,
        resueltoEn: admin.firestore.FieldValue.serverTimestamp(),
      });
      if (rref) {
        tx.set(rref, {
          estado: "rechazada",
          rechazadaEn: admin.firestore.FieldValue.serverTimestamp(),
        }, { merge: true });
      }
      return { ok: true, acreditado: 0 };
    }

    const uid = c.get("uid");
    const userRef = db.collection("usuarios").doc(uid);
    const u = await tx.get(userRef);
    const saldo = u.get("wallet_saldo_cent") || 0;
    const nuevo = saldo + montoCent;
    // Se actualiza también el espejo en soles, que es el que mantiene la acreditación automática:
    // escribir uno sin el otro deja el wallet descuadrado consigo mismo.
    tx.set(userRef, { wallet_saldo_cent: nuevo, "wallet.saldo": nuevo / 100 }, { merge: true });
    tx.set(userRef.collection("wallet_movs").doc(), {
      tipo: "RECLAMO_RECARGA",
      monto_cent: montoCent,
      reclamoId,
      reservaId: c.get("reservaId") || "",
      evidencia,
      motivo,
      adminUid: callerUid,
      created_at: admin.firestore.FieldValue.serverTimestamp(),
    });
    tx.update(cref, {
      estado: "resuelto", resultado: "acreditado", acreditadoCent: montoCent,
      evidencia, motivo, adminUid: callerUid,
      resueltoEn: admin.firestore.FieldValue.serverTimestamp(),
    });
    // "aplicado" además cierra la puerta al automatismo: si el pago apareciera después, ni el
    // casador ni un segundo reclamo volverían a acreditarla.
    if (rref) {
      tx.set(rref, {
        estado: "aplicado",
        acreditadoCent: montoCent,
        porReclamo: true,
        aplicadoEn: admin.firestore.FieldValue.serverTimestamp(),
      }, { merge: true });
    }
    facturacion.encolarComprobante(tx, db, {
      uid, montoCent, reservaId: "reclamo_" + reclamoId, ahoraMs: Date.now(),
    });
    return { ok: true, acreditado: montoCent, uid };
  });

  // La evidencia se marca fuera de la transacción, y su fallo no tumba la acreditación: el dinero ya
  // está bien puesto, y marcarla es higiene para que no se reutilice en otro reclamo.
  if (accion === "acreditar" && evidencia && !res.yaEstaba) {
    try {
      await db.doc(evidencia).set({
        estado: "usado", usadoPor: res.uid, usadoEn: admin.firestore.Timestamp.now(), usadoEnReclamo: reclamoId,
      }, { merge: true });
    } catch (e) {
      functions.logger.error("No se pudo marcar la evidencia", { evidencia, error: String(e) });
    }
  }
  return res;
});
exports.matchReservaDeposito = functions.firestore
  .document("depositos_confirmados/{codigo}")
  .onCreate(async (snap) => {
    const monto = snap.get("monto");
    if (typeof monto !== "number" || monto <= 0) return null;
    const r = await marcarPagoEnReserva(admin.firestore(), Math.round(monto * 100), snap.ref, {
      tipo: "yape", codigo: snap.get("codigoOperacion") || "",
    });
    functions.logger.info("matchReservaDeposito", { monto, resultado: r });
    return null;
  });

// Plin / bancos: al llegar un registro que sea PAGO (no promo), marcar pago por monto.
exports.matchReservaRegistro = functions.firestore
  .document("notificaciones_servidor/{deviceId}/registros/{regId}")
  .onCreate(async (snap) => {
    const monto = snap.get("monto");
    if (typeof monto !== "number" || monto <= 0) return null;
    const contenido = String(snap.get("contenido") || "");
    // Solo notificaciones que sean un PAGO entrante (no promos). Incluye el formato Plin de Interbank
    // "te ha plineado S/ X" y el de Yape "te envió un pago".
    const esPago = /plinead|te ha plin|TRAN\s*S\/|te envi[oó]|pago por S\/|recibiste|abono|transferencia|yape[oó]/i.test(contenido);
    // Excluir promos/consumos que igual traen "S/".
    const esRuido = /shopstar|dscto|descuento|consumo con tu tarjeta|playlist|oferta|cuotas|promoci/i.test(contenido);
    if (!esPago || esRuido) return null;
    const r = await marcarPagoEnReserva(admin.firestore(), Math.round(monto * 100), snap.ref, { tipo: "plin_registro" });
    functions.logger.info("matchReservaRegistro", { monto, resultado: r });
    return null;
  });

// Al aprobar/rechazar un reclamo (recarga con reservaId), sincroniza la reserva del wallet para que
// deje de verse "en curso". NO acredita (eso ya lo hace gestionarRecargaPendiente).
exports.syncReservaTrasRevision = functions.firestore
  .document("usuarios/{uid}/recargas/{recargaId}")
  .onUpdate(async (change) => {
    const after = change.after.data() || {};
    const before = change.before.data() || {};
    const reservaId = after.reservaId;
    if (!reservaId || after.estado === before.estado) return null;
    const rref = admin.firestore().collection("reservas_recarga").doc(reservaId);
    if (String(after.estado).startsWith("aprobada")) {
      await rref.set({ estado: "aplicado", aplicadoEn: admin.firestore.FieldValue.serverTimestamp() }, { merge: true });
    } else if (String(after.estado).startsWith("rechazada")) {
      await rref.set({ estado: "rechazada" }, { merge: true });
    }
    return null;
  });

// Expira automáticamente las reservas cuya ventana (30 min) ya venció y siguen "esperando".
exports.expirarReservas = functions.pubsub.schedule("every 5 minutes").onRun(async () => {
  const db = admin.firestore();
  const ahora = Date.now();
  const snap = await db.collection("reservas_recarga").where("estado", "==", "esperando").get();
  const batch = db.batch();
  let n = 0;
  snap.docs.forEach((d) => {
    if ((d.get("expiraMs") || 0) <= ahora) { batch.update(d.ref, { estado: "expirada" }); n++; }
  });
  if (n > 0) await batch.commit();
  functions.logger.info("expirarReservas", { expiradas: n });
  return null;
});

// Envía un mensaje de TEXTO (gratis) al chat de Crystal de un usuario, desde el panel (admin).
// Reusa el chat existente (participantsKey) o crea uno, en el formato interop de Crystal.
exports.enviarMensajeAdmin = functions.https.onCall(async (data, context) => {
  const callerUid = context.auth && context.auth.uid;
  if (!callerUid) throw new functions.https.HttpsError("unauthenticated", "No autenticado");
  const db = admin.firestore();
  const roleDoc = await db.collection("admin_roles").doc(callerUid).get();
  const role = roleDoc.data() && roleDoc.data().role;
  if (!role || !["admin", "operador"].includes(role)) {
    throw new functions.https.HttpsError("permission-denied", "No autorizado");
  }
  const destino = String((data && data.uid) || "");
  const texto = String((data && data.texto) || "").trim();
  if (!destino || !texto) throw new functions.https.HttpsError("invalid-argument", "Faltan datos.");

  // Canal de SOPORTE dedicado (separado del chat normal) para que el usuario vea "Soporte Crystal".
  const participantsKey = "soporte_" + [callerUid, destino].sort().join("_");
  let chatId;
  const q = await db.collection("chats").where("participantsKey", "==", participantsKey).limit(1).get();
  if (!q.empty) {
    chatId = q.docs[0].id;
  } else {
    chatId = db.collection("chats").doc().id;
    await db.collection("chats").doc(chatId).set({
      id: chatId,
      name: "Soporte Crystal",
      users: [callerUid, destino],
      participantsKey,
      esSoporte: true,
      soporteUid: destino,
      lastMsgDate: admin.firestore.Timestamp.now(),
      lastMessagePreview: "",
      lastMessageType: "text",
      peerPlatform: "crystal",
      peerExternalUserId: "",
      peerCompanyName: "",
      peerCanReceive: [],
      unreadBy: { [callerUid]: 0, [destino]: 0 },
    });
  }

  const now = admin.firestore.Timestamp.now();
  const msgRef = db.collection("chats").doc(chatId).collection("messages").doc();
  await msgRef.set({
    id: msgRef.id,
    message: texto,
    from: callerUid,
    fromUid: callerUid,
    type: "text",
    tipo: "texto",
    sourceApp: "crystal",
    targetApp: "crystal",
    targetExternalUserId: "",
    syncStatus: "local",
    schemaVersion: 1,
    dob: now,
    fileName: "",
    nombreArchivo: "",
    leido: false,
    entregado: true,
    deletedFor: [],
    deletedForEveryone: false,
  });
  const chatUpdate = {
    lastMsgDate: now,
    lastMessagePreview: texto,
    lastMessageType: "text",
  };
  if (destino !== callerUid) {
    chatUpdate[`unreadBy.${destino}`] = admin.firestore.FieldValue.increment(1);
  }
  await db.collection("chats").doc(chatId).update(chatUpdate);
  return { ok: true, chatId };
});

// ===================================================================================
//  AVISO DE RECLAMOS — la mitad que faltaba.
//  crystalAdmin solo tenía la parte receptora (suscrito a `admins_soporte`); nada enviaba.
//  Los reclamos llegaban a Firestore y no sonaba nada: había que abrir la app y mirar.
// ===================================================================================

// Mensaje SOLO de datos, sin bloque `notification`, y es deliberado: así `onMessageReceived` se
// ejecuta siempre —también con la app en segundo plano— y es crystalAdmin quien crea el canal
// "support" con IMPORTANCE_HIGH y levanta el aviso. Con bloque `notification` lo pinta el sistema
// por su cuenta, en un canal de reserva de baja prioridad, y el aviso pasa desapercibido.
async function avisarAdmins(titulo, cuerpo, extra, tema) {
  const data = Object.assign({ title: titulo, body: cuerpo }, extra || {});
  // FCM exige que TODO valor de `data` sea cadena; un número cuela un error en el envío entero.
  Object.keys(data).forEach((k) => { data[k] = String(data[k] == null ? "" : data[k]); });
  try {
    const id = await admin.messaging().send({
      topic: tema || "admins_soporte",
      data,
      android: { priority: "high" },
    });
    functions.logger.info("Aviso a admins enviado", { messageId: id, tipo: data.tipo || "" });
  } catch (e) {
    // Un fallo al avisar NO puede tumbar el trigger: el reclamo ya está guardado, que es lo único
    // que no se puede perder. Queda en el log para revisarlo después.
    functions.logger.error("No se pudo avisar a los admins", { error: String(e) });
  }
}

const ASUNTO_TICKET = {
  plan_no_activado: "No pudo activar su plan",
  recarga: "Problema con una recarga",
};

/**
 * Avisa al panel de un reclamo de recarga.
 *
 * Un reclamo es dinero que ya entró y que el usuario está esperando: llegaba a la bandeja sin
 * anunciarse, así que solo se veía si a alguien se le ocurría abrir la app. Va al canal de recargas
 * —no al de soporte— porque es la misma urgencia que una recarga pendiente.
 *
 * Se escucha la escritura entera y no solo la creación: reclamar de nuevo con el importe corregido
 * actualiza el mismo documento, y ese segundo intento es justo el que hay que mirar.
 */
exports.avisarReclamoRecarga = functions.firestore
  .document("reclamos_recarga/{reclamoId}")
  .onWrite(async (cambio, context) => {
    const ahora = cambio.after.exists ? cambio.after.data() : null;
    if (!ahora || ahora.estado !== "nuevo") return null;

    const antes = cambio.before.exists ? cambio.before.data() : null;
    const esNuevo = !antes;
    const cambioElMonto = antes && antes.pagadoCent !== ahora.pagadoCent;
    if (!esNuevo && !cambioElMonto) return null;

    const pagado = Number(ahora.pagadoCent || 0) / 100;
    const reservado = Number(ahora.reservadoCent || 0) / 100;
    // El veredicto de la búsqueda va en el propio aviso porque decide qué hace el administrador:
    // con el pago encontrado es mirar y pulsar; sin él, hay que abrir Yape y buscarlo a mano.
    const titulo = (ahora.coincide ? "💰 Reclamo · pago encontrado" : "🔎 Reclamo · sin coincidencia");
    const detalle = "S/ " + pagado.toFixed(2) +
      (pagado !== reservado ? " (reservó S/ " + reservado.toFixed(2) + ")" : "") +
      " · " + String(ahora.correo || ahora.uid || "");

    await avisarAdmins(titulo, detalle, {
      tipo: "reclamo_recarga",
      reclamoId: context.params.reclamoId,
      ownerUid: ahora.uid || "",
    }, "admins_recargas");
    return null;
  });

exports.avisarTicketNuevo = functions.firestore
  .document("tickets/{ticketId}")
  .onCreate(async (snap, context) => {
    const t = snap.data() || {};
    const asunto = ASUNTO_TICKET[String(t.tipo || "")] || "Nuevo reclamo";
    const quien = String(t.correo || t.ownerUid || "");
    // El saldo va en el propio aviso porque es lo primero que el admin necesita para decidir: si
    // alcanza, el arreglo es otorgarle el FULL cobrándoselo; si no alcanza, el problema es otro.
    const saldoCent = Number(t.saldoCentAlFallar || 0);
    const detalle = saldoCent > 0
      ? quien + " — saldo S/" + (saldoCent / 100).toFixed(2)
      : quien;
    await avisarAdmins("🔔 " + asunto, detalle, {
      tipo: "ticket_nuevo",
      ticketId: context.params.ticketId,
      ownerUid: t.ownerUid || "",
    });
    return null;
  });

exports.avisarMensajeTicket = functions.firestore
  .document("tickets/{ticketId}/mensajes/{msgId}")
  .onCreate(async (snap, context) => {
    const m = snap.data() || {};
    // La respuesta del admin la escribe el propio panel: avisarle de su propio mensaje sería ruido.
    if (String(m.de || "") !== "usuario") return null;

    const ticketId = context.params.ticketId;
    const msgsRef = admin.firestore().collection("tickets").doc(ticketId).collection("mensajes");

    // Abrir un reclamo escribe el ticket Y su primer mensaje, así que sin esto cada reclamo nuevo
    // dispararía DOS avisos. Se compara contra el mensaje más antiguo y no contra la hora del
    // ticket: así no depende de relojes ni de cuánto tarde en correr el trigger.
    const primero = await msgsRef.orderBy("creado_en").limit(1).get();
    if (!primero.empty && primero.docs[0].id === context.params.msgId) return null;

    await avisarAdmins("💬 Respuesta en un reclamo", String(m.texto || "").slice(0, 180), {
      tipo: "ticket_mensaje",
      ticketId: ticketId,
    });
    return null;
  });

// ⚠️ TEMPORAL (borrar): envía un push de prueba al canal admins_recargas para verificar la suscripción.
exports.devPushTest = functions.https.onRequest(async (req, res) => {
  if ((req.query.k || "") !== "crystal-dev-2026-x7k9") { res.status(403).send("forbidden"); return; }
  try {
    const titulo = "✅ Prueba push (recargas)";
    const cuerpo = "Si ves esto, crystalAdmin sí está suscrito a admins_recargas.";
    const id = await admin.messaging().send({
      topic: "admins_recargas",
      notification: { title: titulo, body: cuerpo },
      data: { title: titulo, body: cuerpo, tipo: "recarga_pendiente" },
      android: { priority: "high", notification: { channelId: "recargas_pendientes", sound: "default" } },
    });
    res.json({ ok: true, messageId: id });
  } catch (e) { res.json({ ok: false, error: String(e) }); }
});

exports.notifyCrystalMessage = functions.firestore
  .document("chats/{chatId}/messages/{messageId}")
  .onCreate(async (snap, context) => {
    if (!snap.exists) return null;

    const message = snap.data() || {};
    const chatId = context.params.chatId;
    const fromUid = message.fromUid || message.from || "";
    if (!chatId || !fromUid) return null;

    const db = admin.firestore();
    const chatSnap = await db.collection("chats").doc(chatId).get();
    if (!chatSnap.exists) return null;

    const chat = chatSnap.data() || {};
    const users = Array.isArray(chat.users) ? chat.users : [];
    const recipients = users.filter((uid) => uid && uid !== fromUid);
    if (recipients.length === 0) return null;
    functions.logger.info("Crystal push: new message", {
      chatId,
      messageId: context.params.messageId,
      fromUid,
      recipients,
    });

    const senderSnap = await db.collection("usuarios").doc(fromUid).get();
    const sender = senderSnap.exists ? senderSnap.data() || {} : {};
    const senderName =
      sender.nombre ||
      (sender.perfil && sender.perfil.nombre) ||
      chat.name ||
      "Mensaje de Crystal";

    const preview = previewMessage(message);
    const unreadBy = chat.unreadBy || {};

    await Promise.all(
      recipients.map(async (recipientUid) => {
        const tokenResult = await collectRecipientTokens(db, recipientUid);
        const tokens = tokenResult.tokens;
        if (tokens.length === 0) {
          functions.logger.warn("Crystal push: recipient without FCM tokens", { chatId, recipientUid });
          return;
        }

        // SOLO datos, sin bloque `notification`, y aquí está la razón del aviso duplicado:
        //
        // Con bloque `notification`, cuando la app no está en primer plano el aviso lo pinta el
        // SISTEMA, con su propio identificador, y `onMessageReceived` ni siquiera se ejecuta. Pero
        // el proceso de Crystal puede seguir vivo en segundo plano con el listener de la lista de
        // chats escuchando, y ese listener levanta SU aviso con id `chatId.hashCode()`. Dos
        // productores, dos identificadores distintos, dos avisos del mismo mensaje. Y explicaba la
        // asimetría: en el teléfono cuyo proceso ya estaba muerto solo quedaba el del sistema, uno.
        //
        // Sin bloque `notification`, `onMessageReceived` se ejecuta SIEMPRE y todos los caminos
        // pasan por CrystalMessageNotifier con el mismo id por chat, así que se funden en uno.
        // De paso el aviso queda mejor: canal propio, icono, texto largo y contador de no leídos.
        const payload = {
          tokens,
          data: {
            chatId,
            messageId: context.params.messageId,
            senderUid: fromUid,
            recipientUid,
            senderName: String(senderName).slice(0, 80),
            preview: preview.slice(0, 180),
            unreadCount: String(unreadBy[recipientUid] || 1),
          },
          android: { priority: "high" },
        };

        const response = await admin.messaging().sendEachForMulticast(payload);
        functions.logger.info("Crystal push: FCM response", {
          chatId,
          recipientUid,
          successCount: response.successCount,
          failureCount: response.failureCount,
        });
        const invalidTokens = [];
        response.responses.forEach((result, index) => {
          if (!result.success && isInvalidTokenError(result.error)) {
            invalidTokens.push(tokens[index]);
          }
        });

        if (invalidTokens.length > 0) {
          await Promise.all(
            tokenResult.refs.map((ref) =>
              ref.update({
                fcmTokens: admin.firestore.FieldValue.arrayRemove(...invalidTokens),
              }).catch(() => null)
            )
          );
        }
      })
    );
    return null;
  });

async function collectRecipientTokens(db, recipientUid) {
  const refsByPath = new Map();
  const directRef = db.collection("usuarios").doc(recipientUid);
  refsByPath.set(directRef.path, directRef);

  const fields = ["uid", "ownerUid", "externalUserId"];
  await Promise.all(
    fields.map(async (field) => {
      const snap = await db.collection("usuarios")
        .where(field, "==", recipientUid)
        .limit(10)
        .get();
      snap.docs.forEach((doc) => refsByPath.set(doc.ref.path, doc.ref));
    })
  );

  const refs = [...refsByPath.values()];
  const docs = await Promise.all(refs.map((ref) => ref.get()));
  const tokens = [];
  docs.forEach((doc) => {
    if (!doc.exists) return;
    const data = doc.data() || {};
    if (Array.isArray(data.fcmTokens)) {
      tokens.push(...data.fcmTokens.filter(Boolean));
    }
  });

  return {
    tokens: [...new Set(tokens)],
    refs,
  };
}

function previewMessage(message) {
  const type = message.tipo || message.type || "texto";
  const fileName = message.nombreArchivo || message.fileName || "";
  const text = message.message || "";

  if (type === "presupuesto") return fileName ? `Presupuesto: ${fileName}` : "Presupuesto";
  if (type === "medidas") return "Medidas recibidas";
  if (["imagen", "video", "audio", "pdf", "archivo", "file"].includes(type)) {
    return fileName ? `Archivo: ${fileName}` : "Archivo recibido";
  }
  return String(text || "Nuevo mensaje");
}

function isInvalidTokenError(error) {
  const code = error && error.code;
  return (
    code === "messaging/invalid-registration-token" ||
    code === "messaging/registration-token-not-registered"
  );
}

// ===================================================================================
// VENTAS (POS) — registro unificado + numeración correlativa por serie
// ===================================================================================

/**
 * Registra una venta de forma UNIFICADA en el servidor y devuelve su número correlativo.
 *
 * Reemplaza la escritura directa desde el cliente: así todas las terminales de un mismo patrón
 * comparten un correlativo ATÓMICO por serie y sus ventas quedan bajo el patrón (no en cada equipo).
 *
 * Autorización:
 *  - El patrón (dueño) puede registrar siempre (su propio uid == patronUid).
 *  - Una terminal debe estar autorizada y `activo == true` en
 *    usuarios/{patronUid}/plan_ventas/dispositivos/autorizados/{deviceId}. En el primer registro
 *    queda LIGADA a su sesión de Firebase (authUid, TOFU); registros posteriores exigen el mismo
 *    authUid. Para re-ligar un equipo reinstalado, el patrón lo desautoriza y vuelve a autorizar.
 *
 * data = { patronUid, deviceId, venta: { serie, tipoComprobante, cliente, subtotal, igv, total,
 *          formaPago, vendedor, terminal, itemsJson, fecha } }
 */
exports.registrarVenta = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Inicia sesión.");
  }
  const callerUid = context.auth.uid;
  const patronUid = String((data && data.patronUid) || "").trim();
  const deviceId = String((data && data.deviceId) || "").trim();
  const venta = (data && data.venta) || {};
  const serie = String(venta.serie || "").trim();

  if (!patronUid) {
    throw new functions.https.HttpsError("invalid-argument", "Falta patronUid.");
  }
  const SERIES = ["B001", "F001", "T001"];
  if (!SERIES.includes(serie)) {
    throw new functions.https.HttpsError("invalid-argument", "Serie no válida.");
  }

  const db = admin.firestore();
  const uref = db.collection("usuarios").doc(patronUid);
  const esPatron = callerUid === patronUid;
  const devRef = uref
    .collection("plan_ventas").doc("dispositivos")
    .collection("autorizados").doc(deviceId);
  const contRef = uref.collection("plan_ventas").doc("contadores");
  const ventaRef = uref.collection("ventas").doc();

  return db.runTransaction(async (tx) => {
    // --- LECTURAS (todas antes de cualquier escritura) ---
    let bindAuthUid = false;
    if (!esPatron) {
      if (!deviceId) {
        throw new functions.https.HttpsError("invalid-argument", "Falta deviceId.");
      }
      const devSnap = await tx.get(devRef);
      if (!devSnap.exists) {
        throw new functions.https.HttpsError("permission-denied", "Dispositivo no autorizado.");
      }
      if (devSnap.get("activo") !== true) {
        throw new functions.https.HttpsError("permission-denied", "Dispositivo desautorizado.");
      }
      const boundUid = devSnap.get("authUid");
      if (boundUid && boundUid !== callerUid) {
        throw new functions.https.HttpsError(
          "permission-denied",
          "Dispositivo ligado a otra sesión. El administrador debe re-autorizarlo."
        );
      }
      bindAuthUid = !boundUid;
    }

    const contSnap = await tx.get(contRef);
    const actual = (contSnap.exists && typeof contSnap.get(serie) === "number")
      ? contSnap.get(serie)
      : 0;
    const numero = actual + 1;
    const numeroComprobante = serie + "-" + String(numero).padStart(8, "0");

    // --- ESCRITURAS ---
    if (bindAuthUid) {
      tx.update(devRef, {
        authUid: callerUid,
        authUidBoundAt: admin.firestore.FieldValue.serverTimestamp(),
      });
    }
    tx.set(contRef, { [serie]: numero }, { merge: true });
    tx.set(ventaRef, {
      numeroComprobante,
      serie,
      numero,
      tipoComprobante: String(venta.tipoComprobante || ""),
      cliente: String(venta.cliente || ""),
      subtotal: Number(venta.subtotal || 0),
      igv: Number(venta.igv || 0),
      total: Number(venta.total || 0),
      formaPago: String(venta.formaPago || ""),
      vendedor: String(venta.vendedor || ""),
      terminal: venta.terminal ? String(venta.terminal) : null,
      itemsJson: String(venta.itemsJson || ""),
      fecha: Number(venta.fecha || Date.now()),
      registradaPor: callerUid,
      esPatron: esPatron,
      deviceId: deviceId || null,
      created_at: admin.firestore.FieldValue.serverTimestamp(),
    });

    return { ok: true, id: ventaRef.id, numeroComprobante: numeroComprobante, numero: numero, serie: serie };
  });
});

/**
 * Le da a una TERMINAL autorizada un custom claim `patronUid`, para que pueda acceder al chat del
 * patrón (las reglas permiten `request.auth.token.patronUid in users`). Verifica que el dispositivo
 * esté autorizado y activo, y liga la sesión al authUid (TOFU) igual que registrarVenta. La terminal
 * debe refrescar su token (getIdToken(true)) tras la llamada para que el claim tome efecto.
 */
exports.asignarClaimPatron = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError("unauthenticated", "Inicia sesión.");
  }
  const callerUid = context.auth.uid;
  const patronUid = String((data && data.patronUid) || "").trim();
  const deviceId = String((data && data.deviceId) || "").trim();
  if (!patronUid || !deviceId) {
    throw new functions.https.HttpsError("invalid-argument", "Faltan patronUid o deviceId.");
  }

  const db = admin.firestore();
  const devRef = db.collection("usuarios").doc(patronUid)
    .collection("plan_ventas").doc("dispositivos")
    .collection("autorizados").doc(deviceId);
  const devSnap = await devRef.get();
  if (!devSnap.exists) {
    throw new functions.https.HttpsError("permission-denied", "Dispositivo no autorizado.");
  }
  if (devSnap.get("activo") !== true) {
    throw new functions.https.HttpsError("permission-denied", "Dispositivo desautorizado.");
  }
  const boundUid = devSnap.get("authUid");
  if (boundUid && boundUid !== callerUid) {
    throw new functions.https.HttpsError(
      "permission-denied",
      "Dispositivo ligado a otra sesión. El administrador debe re-autorizarlo."
    );
  }
  if (!boundUid) {
    await devRef.update({
      authUid: callerUid,
      authUidBoundAt: admin.firestore.FieldValue.serverTimestamp(),
    });
  }

  await admin.auth().setCustomUserClaims(callerUid, { patronUid: patronUid });
  return { ok: true, patronUid: patronUid };
});

// ===================================================================================
// PEDIDOS EN LÍNEA (chat compartido) — marcar / tomar (atómico)
// ===================================================================================

/**
 * Verifica que quien llama pueda operar sobre los pedidos del patrón [patronUid]: o es el propio
 * patrón (dueño), o es una terminal autorizada y activa de ese patrón (ligada por authUid, TOFU).
 * Lanza HttpsError si no está autorizado.
 */
async function verificarAccesoPatron(db, patronUid, deviceId, callerUid) {
  if (callerUid === patronUid) return; // el patrón dueño siempre puede
  if (!deviceId) {
    throw new functions.https.HttpsError("invalid-argument", "Falta deviceId.");
  }
  const devSnap = await db.collection("usuarios").doc(patronUid)
    .collection("plan_ventas").doc("dispositivos")
    .collection("autorizados").doc(deviceId).get();
  if (!devSnap.exists || devSnap.get("activo") !== true) {
    throw new functions.https.HttpsError("permission-denied", "Dispositivo no autorizado.");
  }
  const boundUid = devSnap.get("authUid");
  if (boundUid && boundUid !== callerUid) {
    throw new functions.https.HttpsError("permission-denied", "Dispositivo ligado a otra sesión.");
  }
}

/** Verifica que el chat exista y que patronUid sea participante (el pedido le pertenece). */
async function verificarChatDelPatron(db, chatId, patronUid) {
  if (!chatId) throw new functions.https.HttpsError("invalid-argument", "Falta chatId.");
  const chatSnap = await db.collection("chats").doc(chatId).get();
  if (!chatSnap.exists) throw new functions.https.HttpsError("not-found", "Chat no existe.");
  const users = chatSnap.get("users") || [];
  if (users.indexOf(patronUid) < 0) {
    throw new functions.https.HttpsError("permission-denied", "El pedido no pertenece a este patrón.");
  }
}

/**
 * Marca un mensaje del chat como pedido: crea un PEDIDO estructurado en la bandeja del patrón
 * (usuarios/{patronUid}/pedidos/{msgId}) y deja el flag en el mensaje para el chat. El pedidoId es
 * el msgId (idempotente: marcar dos veces no duplica).
 */
exports.marcarPedido = functions.https.onCall(async (data, context) => {
  if (!context.auth) throw new functions.https.HttpsError("unauthenticated", "Inicia sesión.");
  const callerUid = context.auth.uid;
  const patronUid = String((data && data.patronUid) || "").trim();
  const deviceId = String((data && data.deviceId) || "").trim();
  const chatId = String((data && data.chatId) || "").trim();
  const msgId = String((data && data.msgId) || "").trim();
  if (!patronUid || !msgId) {
    throw new functions.https.HttpsError("invalid-argument", "Faltan datos del pedido.");
  }

  const db = admin.firestore();
  await verificarAccesoPatron(db, patronUid, deviceId, callerUid);
  await verificarChatDelPatron(db, chatId, patronUid);

  const pedidoRef = db.collection("usuarios").doc(patronUid).collection("pedidos").doc(msgId);
  await pedidoRef.set({
    id: msgId,
    chatId: chatId,
    messageId: msgId,
    patronUid: patronUid,
    contactoNombre: String((data && data.contactoNombre) || ""),
    mensajeTipo: String((data && data.mensajeTipo) || "texto"),
    resumen: String((data && data.resumen) || ""),
    contenidoMensaje: String((data && data.contenidoMensaje) || ""),
    estado: "en_espera",
    atendidoPor: "",
    atendidoNombre: "",
    fechaLlegada: admin.firestore.FieldValue.serverTimestamp(),
  }, { merge: true });

  // Flag en el mensaje para verlo dentro del chat.
  await db.collection("chats").doc(chatId).collection("messages").doc(msgId).set({
    esPedido: true,
    estadoPedido: "en_espera",
    pedidoId: msgId,
    atendidoPor: "",
    atendidoNombre: "",
  }, { merge: true });
  return { ok: true, pedidoId: msgId };
});

/**
 * TOMA un pedido de forma ATÓMICA sobre la bandeja (usuarios/{patronUid}/pedidos/{msgId}): solo pasa
 * a "cogido" si sigue "en_espera". Si otro ya lo tomó, devuelve ok:false para que dos terminales no
 * atiendan el mismo. Refleja el estado también en el mensaje del chat.
 */
exports.tomarPedido = functions.https.onCall(async (data, context) => {
  if (!context.auth) throw new functions.https.HttpsError("unauthenticated", "Inicia sesión.");
  const callerUid = context.auth.uid;
  const patronUid = String((data && data.patronUid) || "").trim();
  const deviceId = String((data && data.deviceId) || "").trim();
  const chatId = String((data && data.chatId) || "").trim();
  const msgId = String((data && data.msgId) || "").trim();
  const atendidoNombre = String((data && data.atendidoNombre) || "").trim();
  if (!patronUid || !msgId) {
    throw new functions.https.HttpsError("invalid-argument", "Faltan datos del pedido.");
  }

  const db = admin.firestore();
  await verificarAccesoPatron(db, patronUid, deviceId, callerUid);

  const pedidoRef = db.collection("usuarios").doc(patronUid).collection("pedidos").doc(msgId);
  const resultado = await db.runTransaction(async (tx) => {
    const snap = await tx.get(pedidoRef);
    if (!snap.exists) return { ok: false, motivo: "no_existe" };
    if ((snap.get("estado") || "") !== "en_espera") {
      return { ok: false, motivo: "ya_tomado", atendidoNombre: snap.get("atendidoNombre") || "" };
    }
    tx.update(pedidoRef, {
      estado: "cogido",
      atendidoPor: callerUid,
      atendidoNombre: atendidoNombre,
      tomadoEn: admin.firestore.FieldValue.serverTimestamp(),
    });
    return { ok: true, atendidoNombre: atendidoNombre };
  });

  // Reflejar en el mensaje del chat (best-effort; la bandeja es la fuente de verdad).
  if (resultado.ok && chatId) {
    try {
      await db.collection("chats").doc(chatId).collection("messages").doc(msgId).set({
        estadoPedido: "cogido",
        atendidoPor: callerUid,
        atendidoNombre: atendidoNombre,
      }, { merge: true });
    } catch (e) {
      functions.logger && functions.logger.warn
        ? functions.logger.warn("No se pudo reflejar el pedido en el mensaje", e)
        : null;
    }
  }
  return resultado;
});

// ===================================================================================
//  FACTURACIÓN ELECTRÓNICA (boletas SUNAT)
//  El circuito vive en ./facturacion. Aquí solo quedan los disparadores.
// ===================================================================================

// Emite en cuanto se encola el comprobante. Va por trigger y no dentro de la acreditación a
// propósito: si el proveedor falla, el saldo del usuario ya quedó acreditado igual.
exports.emitirComprobante = functions.firestore
  .document("comprobantes/{comprobanteId}")
  .onCreate(async (snap, context) => {
    if (snap.get("estado") !== "pendiente") return null;
    const r = await facturacion.emitirComprobante(admin.firestore(), context.params.comprobanteId);
    functions.logger.info(Object.assign({ message: "emitirComprobante", id: context.params.comprobanteId }, r));
    return null;
  });

// Reintenta lo que quedó en error. Las boletas se informan a SUNAT por resumen diario, con plazo
// hasta el 7.º día calendario, así que reintentar cada 30 min sobra para no incumplir.
exports.reintentarComprobantes = functions.pubsub
  .schedule("every 30 minutes")
  .onRun(async () => {
    const db = admin.firestore();
    const snap = await db.collection("comprobantes")
      .where("estado", "==", "error")
      .where("reintentable", "==", true)
      .limit(20)
      .get();
    let ok = 0;
    for (const d of snap.docs) {
      // Se corta a los 10 intentos: pasado eso el problema son los datos, no la red, y hay que
      // mirarlo a mano en vez de seguir golpeando al proveedor.
      if ((d.get("intentos") || 0) >= 10) continue;
      const r = await facturacion.emitirComprobante(db, d.id);
      if (r.ok) ok++;
    }
    functions.logger.info({ message: "reintentarComprobantes", revisados: snap.size, emitidos: ok });
    return null;
  });

// ===================================================================================
//  AVISOS POR CORREO
//  Es el único canal por el que se puede dirigir al usuario a pagar fuera de la app: Google Play
//  lo permite por correo y lo prohíbe desde dentro de la aplicación.
// ===================================================================================

// Bienvenida al crear la cuenta. Se dispara en Auth y no en Firestore porque aquí el correo llega
// garantizado en el propio evento, sin depender de que el documento del usuario ya exista.
exports.correoBienvenida = functions.auth.user().onCreate(async (user) => {
  if (!user.email) return null;
  const r = await correo.enviarBienvenida(admin.firestore(), {
    uid: user.uid, correo: user.email, nombre: user.displayName || "",
  });
  functions.logger.info(Object.assign({ message: "correoBienvenida", uid: user.uid }, r));
  return null;
});

// Una pasada diaria a las 09:00 de Lima — hora en que un aviso se lee, no de madrugada.
exports.avisarVencimientos = functions.pubsub
  .schedule("0 9 * * *")
  .timeZone("America/Lima")
  .onRun(async () => {
    const r = await correo.revisarVencimientos(admin.firestore(), Date.now());
    functions.logger.info(Object.assign({ message: "avisarVencimientos" }, r));
    return null;
  });

// Cierra el consolidado del día anterior y encola su boleta. Corre a las 00:30 de Lima, ya entrado
// el día siguiente, para no cerrar un día que todavía puede recibir operaciones.
exports.cerrarConsolidadoDiario = functions.pubsub
  .schedule("30 0 * * *")
  .timeZone("America/Lima")
  .onRun(async () => {
    const db = admin.firestore();
    const fecha = require("./facturacion/comun").fechaPeru(Date.now() - 24 * 60 * 60 * 1000);
    const r = await facturacion.cerrarConsolidado(db, fecha);
    functions.logger.info(Object.assign({ message: "cerrarConsolidadoDiario", fecha }, r));
    return null;
  });
