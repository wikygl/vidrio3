// Plantillas de los correos transaccionales. Funciones puras: reciben datos, devuelven asunto,
// texto y HTML. Sin Firestore ni red, para poder probarlas en aislamiento.
//
// El correo es el ÚNICO canal por el que se puede dirigir al usuario a pagar fuera de la app: la
// política de pagos de Google Play lo permite expresamente, y prohíbe hacerlo desde dentro. Por eso
// el enlace vive aquí y no en una pantalla.

const URL_POR_DEFECTO = "https://crystal-4f306.web.app";

function urlRecarga(env = process.env) {
  return env.URL_RECARGA || URL_POR_DEFECTO;
}

function fechaLegible(ms) {
  // Fecha en hora de Perú (UTC−5): al usuario le importa el día en que se le corta, no el UTC.
  return new Date(ms - 5 * 60 * 60 * 1000).toISOString().slice(0, 10).split("-").reverse().join("/");
}

// Envoltura común. Se usan estilos en línea porque los clientes de correo descartan las hojas de
// estilo, y una tabla de ancho fijo porque varios ignoran los `max-width` en div.
function envolver(titulo, cuerpoHtml, url) {
  return `<div style="font-family:-apple-system,Segoe UI,Roboto,sans-serif;background:#f6f7f9;padding:24px 12px">
  <table role="presentation" width="100%" cellpadding="0" cellspacing="0" style="max-width:460px;margin:0 auto;background:#ffffff;border:1px solid #e3e6ea;border-radius:14px">
    <tr><td style="padding:24px">
      <h1 style="margin:0 0 12px;font-size:19px;color:#12161c">${titulo}</h1>
      ${cuerpoHtml}
      <a href="${url}" style="display:block;margin-top:20px;padding:13px;background:#1f6feb;color:#ffffff;text-decoration:none;text-align:center;border-radius:10px;font-weight:600">Recargar mi saldo</a>
      <p style="margin:16px 0 0;font-size:12px;color:#5c6672;line-height:1.5">
        Consejo: al abrir el enlace, tu teléfono te ofrecerá <strong>instalarlo en la pantalla de inicio</strong>.
        Acéptalo y lo tendrás siempre a mano, sin escribir la dirección nunca más.
      </p>
    </td></tr>
  </table>
  <p style="text-align:center;font-size:11px;color:#5c6672;margin:16px 0 0">Crystal</p>
</div>`;
}

function bienvenida({ nombre } = {}, env) {
  const url = urlRecarga(env);
  const saludo = nombre ? `Hola ${nombre},` : "Hola,";
  return {
    asunto: "Bienvenido a Crystal — tu primer mes es gratis",
    texto:
      `${saludo}\n\n` +
      `Tu cuenta de Crystal ya está activa y tienes un mes completo sin costo.\n\n` +
      `Cuando quieras recargar saldo o renovar tu plan, entra aquí:\n${url}\n\n` +
      `Al abrirlo, tu teléfono te ofrecerá instalarlo en la pantalla de inicio. Acéptalo y lo tendrás ` +
      `siempre a mano.\n\nCrystal`,
    html: envolver(
      "Bienvenido a Crystal",
      `<p style="margin:0;font-size:15px;color:#12161c;line-height:1.6">${saludo}</p>
       <p style="margin:12px 0 0;font-size:15px;color:#12161c;line-height:1.6">
         Tu cuenta ya está activa y tienes <strong>un mes completo sin costo</strong>.
       </p>
       <p style="margin:12px 0 0;font-size:15px;color:#12161c;line-height:1.6">
         Cuando quieras recargar saldo o renovar tu plan, entra desde aquí.
       </p>`,
      url
    ),
  };
}

function porVencer({ nombre, dias, fullUntilMs } = {}, env) {
  const url = urlRecarga(env);
  const saludo = nombre ? `Hola ${nombre},` : "Hola,";
  const cuando = dias <= 1 ? "mañana" : `en ${dias} días`;
  return {
    asunto: `Tu plan de Crystal vence ${cuando}`,
    texto:
      `${saludo}\n\nTu plan FULL vence ${cuando}, el ${fechaLegible(fullUntilMs)}.\n\n` +
      `Para no quedarte sin acceso, recarga y renueva aquí:\n${url}\n\nCrystal`,
    html: envolver(
      `Tu plan vence ${cuando}`,
      `<p style="margin:0;font-size:15px;color:#12161c;line-height:1.6">${saludo}</p>
       <p style="margin:12px 0 0;font-size:15px;color:#12161c;line-height:1.6">
         Tu plan FULL vence <strong>${cuando}</strong>, el ${fechaLegible(fullUntilMs)}.
       </p>
       <p style="margin:12px 0 0;font-size:15px;color:#12161c;line-height:1.6">
         Para no quedarte sin acceso, recarga y renueva antes de esa fecha.
       </p>`,
      url
    ),
  };
}

function vencido({ nombre } = {}, env) {
  const url = urlRecarga(env);
  const saludo = nombre ? `Hola ${nombre},` : "Hola,";
  return {
    asunto: "Tu plan de Crystal venció",
    texto:
      `${saludo}\n\nTu plan FULL venció y tu cuenta pasó al plan BASIC.\n\n` +
      `Tus datos, tus clientes y tus medidas siguen intactos: al renovar recuperas todo tal como lo dejaste.\n\n` +
      `Renueva aquí:\n${url}\n\nCrystal`,
    html: envolver(
      "Tu plan venció",
      `<p style="margin:0;font-size:15px;color:#12161c;line-height:1.6">${saludo}</p>
       <p style="margin:12px 0 0;font-size:15px;color:#12161c;line-height:1.6">
         Tu plan FULL venció y tu cuenta pasó al plan BASIC.
       </p>
       <p style="margin:12px 0 0;font-size:15px;color:#12161c;line-height:1.6">
         <strong>Tus datos siguen intactos</strong> — tus clientes y tus medidas están donde los dejaste.
         Al renovar recuperas todo.
       </p>`,
      url
    ),
  };
}

module.exports = { bienvenida, porVencer, vencido, urlRecarga, fechaLegible };
