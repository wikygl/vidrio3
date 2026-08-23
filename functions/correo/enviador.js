// Adaptadores de envío de correo. Igual que con el proveedor de facturación: una sola operación,
// para que cambiar de proveedor SMTP sea cambiar de adaptador.

/**
 * Envío por SMTP con nodemailer. Las credenciales llegan por variable de entorno y NUNCA por
 * Firestore: `config/{docId}` tiene `allow read: if isSignedIn()`, así que una contraseña ahí
 * quedaría al alcance de cualquier usuario de Crystal.
 *
 * Se crea con `firebase functions:secrets:set SMTP_PASS` (y el resto como variables normales).
 */
class EnviadorSmtp {
  constructor({ host, puerto, usuario, clave, desde, transporteFalso }) {
    if (!host || !usuario || !clave) throw new Error("CORREO_SIN_CREDENCIALES");
    this.desde = desde || usuario;
    this.transporte = transporteFalso || require("nodemailer").createTransport({
      host,
      port: Number(puerto) || 465,
      secure: Number(puerto) !== 587,   // 587 usa STARTTLS; 465 es TLS directo
      auth: { user: usuario, pass: clave },
    });
  }

  async enviar({ para, asunto, texto, html }) {
    const info = await this.transporte.sendMail({
      from: this.desde, to: para, subject: asunto, text: texto, html,
    });
    return { ok: true, id: info.messageId || null };
  }
}

/** No sale a la red. Permite probar todo el circuito —selección, deduplicación— sin credenciales. */
class EnviadorFalso {
  constructor({ fallarCon = null } = {}) {
    this.fallarCon = fallarCon;
    this.enviados = [];
  }

  async enviar(mensaje) {
    if (this.fallarCon) throw new Error(this.fallarCon);
    this.enviados.push(mensaje);
    return { ok: true, id: `falso-${this.enviados.length}` };
  }
}

function crearEnviador(env = process.env) {
  if (env.SMTP_HOST && env.SMTP_USER && env.SMTP_PASS) {
    return new EnviadorSmtp({
      host: env.SMTP_HOST,
      puerto: env.SMTP_PORT,
      usuario: env.SMTP_USER,
      clave: env.SMTP_PASS,
      desde: env.CORREO_DESDE,
    });
  }
  if (env.FUNCTIONS_EMULATOR === "true" || env.NODE_ENV === "test") return new EnviadorFalso();
  throw new Error("CORREO_SIN_CREDENCIALES");
}

module.exports = { EnviadorSmtp, EnviadorFalso, crearEnviador };
