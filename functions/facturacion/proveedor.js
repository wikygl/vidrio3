// Adaptadores de proveedor de facturación electrónica.
//
// La emisión se define por una sola operación —`emitirBoleta(doc)`— para que cambiar de OSE sea
// cambiar de adaptador y nada más. Hoy hay dos: NubeFact (real) y uno falso para pruebas.

const { aSoles, formatearNumero } = require("./comun");

/**
 * Adaptador NubeFact. Se le manda un JSON y ellos generan, firman y envían el XML a SUNAT; devuelven
 * enlaces al PDF/XML/CDR. Por eso aquí no hay UBL, ni certificado digital, ni SOAP.
 *
 * RUTA y TOKEN llegan por variable de entorno a propósito: `config/{docId}` en Firestore tiene
 * `allow read: if isSignedIn()`, así que guardar ahí el token lo dejaría al alcance de CUALQUIER
 * usuario de Crystal.
 */
class ProveedorNubeFact {
  constructor({ ruta, token, fetchImpl }) {
    if (!ruta || !token) throw new Error("FACTURACION_SIN_CREDENCIALES");
    this.ruta = ruta;
    this.token = token;
    this.fetch = fetchImpl || globalThis.fetch;
  }

  // `doc` es el comprobante ya resuelto: serie, correlativo, importes desglosados y cliente.
  construirPayload(doc) {
    return {
      operacion: "generar_comprobante",
      tipo_de_comprobante: 2,               // 2 = boleta de venta
      serie: doc.serie,
      numero: doc.correlativo,
      sunat_transaction: 1,                 // venta interna
      cliente_tipo_de_documento: doc.clienteTipoDoc,   // 1 = DNI, "-" = sin documento
      cliente_numero_de_documento: doc.clienteNumDoc,
      cliente_denominacion: doc.clienteNombre,
      cliente_direccion: doc.clienteDireccion || "",
      fecha_de_emision: doc.fechaEmision,   // YYYY-MM-DD
      moneda: 1,                            // 1 = PEN
      porcentaje_de_igv: doc.igvPorcentaje,
      total_gravada: aSoles(doc.gravadaCent),
      total_inafecta: aSoles(doc.inafectaCent),
      total_igv: aSoles(doc.igvCent),
      total: aSoles(doc.totalCent),
      enviar_automaticamente_a_la_sunat: true,
      enviar_automaticamente_al_cliente: false,
      items: doc.items.map((it) => ({
        unidad_de_medida: "ZZ",             // ZZ = servicio
        descripcion: it.descripcion,
        cantidad: 1,
        valor_unitario: aSoles(it.valorUnitarioCent),
        precio_unitario: aSoles(it.precioUnitarioCent),
        subtotal: aSoles(it.subtotalCent),
        tipo_de_igv: doc.igvPorcentaje ? 1 : 9,  // 1 = gravado, 9 = inafecto
        igv: aSoles(it.igvCent),
        total: aSoles(it.totalCent),
      })),
    };
  }

  async emitirBoleta(doc) {
    const res = await this.fetch(this.ruta, {
      method: "POST",
      headers: {
        "Authorization": `Token token="${this.token}"`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(this.construirPayload(doc)),
    });

    const cuerpo = await res.json().catch(() => ({}));

    // NubeFact responde 200 con `errors` en el cuerpo cuando rechaza; no basta con mirar el status.
    if (!res.ok || cuerpo.errors) {
      const detalle = cuerpo.errors || `HTTP ${res.status}`;
      const err = new Error(String(detalle));
      // Un 4xx no se arregla reintentando (datos mal formados); un 5xx o una caída de red sí.
      err.reintentable = !cuerpo.errors && res.status >= 500;
      throw err;
    }

    return {
      numeroCompleto: formatearNumero(doc.serie, doc.correlativo),
      pdfUrl: cuerpo.enlace_del_pdf || cuerpo.enlace || null,
      xmlUrl: cuerpo.enlace_del_xml || null,
      cdrUrl: cuerpo.enlace_del_cdr || null,
      hash: cuerpo.cadena_para_codigo_qr || cuerpo.codigo_hash || null,
      aceptadoPorSunat: cuerpo.aceptada_por_sunat === true,
      respuestaSunat: cuerpo.sunat_description || cuerpo.sunat_note || null,
    };
  }
}

/**
 * Proveedor falso: no sale a la red. Permite construir y probar todo el circuito —correlativos,
 * consolidado, reintentos— antes de tener cuenta en NubeFact. `fallarCon` fuerza el camino de error.
 */
class ProveedorFalso {
  constructor({ fallarCon = null, reintentable = false } = {}) {
    this.fallarCon = fallarCon;
    this.reintentable = reintentable;
    this.emitidos = [];
  }

  async emitirBoleta(doc) {
    if (this.fallarCon) {
      const err = new Error(this.fallarCon);
      err.reintentable = this.reintentable;
      throw err;
    }
    const numeroCompleto = formatearNumero(doc.serie, doc.correlativo);
    this.emitidos.push({ ...doc, numeroCompleto });
    return {
      numeroCompleto,
      pdfUrl: `https://falso.local/${numeroCompleto}.pdf`,
      xmlUrl: `https://falso.local/${numeroCompleto}.xml`,
      cdrUrl: `https://falso.local/R-${numeroCompleto}.zip`,
      hash: "FALSO-HASH",
      aceptadoPorSunat: true,
      respuestaSunat: "La Boleta numero " + numeroCompleto + ", ha sido aceptada",
    };
  }
}

// Elige el adaptador según el entorno. Sin credenciales cae al falso, para que el emulador y los
// tests funcionen sin configurar nada; en producción hay que exigir las credenciales de verdad.
function crearProveedor(env = process.env) {
  const ruta = env.NUBEFACT_RUTA;
  const token = env.NUBEFACT_TOKEN;
  if (ruta && token) return new ProveedorNubeFact({ ruta, token });
  if (env.FUNCTIONS_EMULATOR === "true" || env.NODE_ENV === "test") return new ProveedorFalso();
  throw new Error("FACTURACION_SIN_CREDENCIALES");
}

module.exports = { ProveedorNubeFact, ProveedorFalso, crearProveedor };
