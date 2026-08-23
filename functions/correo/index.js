// Avisos por correo: bienvenida, plan por vencer y plan vencido.
//
// La regla que gobierna todo esto es la deduplicación. Un aviso repetido no es un detalle estético:
// es la diferencia entre recordarle algo al usuario y convertirte en correo basura. Cada aviso
// enviado deja constancia en `usuarios/{uid}/correos/{clave}` y nunca se repite esa clave.

const admin = require("firebase-admin");
const plantillas = require("./plantillas");
const { crearEnviador } = require("./enviador");

const DIA_MS = 24 * 60 * 60 * 1000;

// Se avisa con esta antelación, y el aviso de vencimiento solo se manda si venció hace poco: un
// usuario que dejó la app hace medio año no debe recibir un "acaba de vencer".
const DIAS_PREAVISO = 3;
const VENTANA_VENCIDO_MS = 3 * DIA_MS;

const fechaClave = (ms) => new Date(ms - 5 * 60 * 60 * 1000).toISOString().slice(0, 10);

/**
 * Decide qué avisos corresponden a un usuario. Pura, sin Firestore: recibe el estado y la hora, y
 * devuelve la lista de avisos con su clave de deduplicación.
 *
 * La clave incluye la fecha de vencimiento, así que si el usuario renueva —y `full_until` cambia—
 * el siguiente ciclo genera una clave nueva y vuelve a poder avisarse. Sin eso, quien renovara no
 * recibiría nunca más un recordatorio.
 */
function avisosPendientes(estadoServicio, ahoraMs) {
  const avisos = [];
  if (!estadoServicio || estadoServicio.mode !== "FULL") return avisos;

  const fu = estadoServicio.full_until;
  const fullUntilMs = fu && fu.toMillis ? fu.toMillis() : (typeof fu === "number" ? fu : 0);
  if (!fullUntilMs) return avisos;

  const restanteMs = fullUntilMs - ahoraMs;

  if (restanteMs > 0 && restanteMs <= DIAS_PREAVISO * DIA_MS) {
    avisos.push({
      tipo: "porVencer",
      clave: `vence_${fechaClave(fullUntilMs)}`,
      dias: Math.max(1, Math.ceil(restanteMs / DIA_MS)),
      fullUntilMs,
    });
  } else if (restanteMs <= 0 && -restanteMs < VENTANA_VENCIDO_MS) {
    avisos.push({
      tipo: "vencido",
      clave: `vencido_${fechaClave(fullUntilMs)}`,
      fullUntilMs,
    });
  }
  return avisos;
}

/**
 * Envía un aviso salvo que ya se haya mandado esa clave. La constancia se escribe con `create`, que
 * falla si el documento ya existe: eso hace la comprobación atómica y evita el doble envío cuando
 * dos ejecuciones coinciden.
 */
async function enviarSiNoEnviado(db, { uid, correo, nombre, clave, mensaje }, enviador) {
  if (!correo) return { ok: false, motivo: "SIN_CORREO" };
  const ref = db.collection("usuarios").doc(uid).collection("correos").doc(clave);

  try {
    await ref.create({
      clave,
      para: correo,
      asunto: mensaje.asunto,
      estado: "enviando",
      created_at: admin.firestore.FieldValue.serverTimestamp(),
    });
  } catch (e) {
    return { ok: false, motivo: "YA_ENVIADO" };   // ALREADY_EXISTS
  }

  try {
    const r = await (enviador || crearEnviador()).enviar({
      para: correo, asunto: mensaje.asunto, texto: mensaje.texto, html: mensaje.html,
    });
    await ref.update({ estado: "enviado", proveedorId: r.id || null });
    return { ok: true };
  } catch (e) {
    // Se deja marcado como error y NO se borra la constancia: si el fallo fue del proveedor,
    // reintentar a ciegas puede acabar mandando dos. Lo revisa el aviso del día siguiente.
    await ref.update({ estado: "error", error: String(e.message || e) });
    return { ok: false, motivo: "ERROR_ENVIO", detalle: String(e.message || e) };
  }
}

async function enviarBienvenida(db, { uid, correo, nombre }, enviador) {
  return enviarSiNoEnviado(db, {
    uid, correo, nombre, clave: "bienvenida",
    mensaje: plantillas.bienvenida({ nombre }),
  }, enviador);
}

/** Recorre los usuarios con vencimiento cercano y manda lo que corresponda. */
async function revisarVencimientos(db, ahoraMs, enviador) {
  const desde = admin.firestore.Timestamp.fromMillis(ahoraMs - VENTANA_VENCIDO_MS);
  const hasta = admin.firestore.Timestamp.fromMillis(ahoraMs + DIAS_PREAVISO * DIA_MS);

  const snap = await db.collection("usuarios")
    .where("estado_servicio.full_until", ">=", desde)
    .where("estado_servicio.full_until", "<=", hasta)
    .limit(500)
    .get();

  let enviados = 0, omitidos = 0;
  for (const d of snap.docs) {
    const avisos = avisosPendientes(d.get("estado_servicio"), ahoraMs);
    for (const a of avisos) {
      const nombre = d.get("nombreCompleto") || d.get("nombre") || "";
      const mensaje = a.tipo === "porVencer"
        ? plantillas.porVencer({ nombre, dias: a.dias, fullUntilMs: a.fullUntilMs })
        : plantillas.vencido({ nombre });
      const r = await enviarSiNoEnviado(db, {
        uid: d.id, correo: d.get("email"), nombre, clave: a.clave, mensaje,
      }, enviador);
      if (r.ok) enviados++; else omitidos++;
    }
  }
  return { revisados: snap.size, enviados, omitidos };
}

module.exports = {
  DIAS_PREAVISO,
  avisosPendientes,
  enviarSiNoEnviado,
  enviarBienvenida,
  revisarVencimientos,
};
