// Piezas compartidas de la facturación electrónica. Sin dependencias de Firestore ni de red, para
// que la aritmética del comprobante y las reglas de negocio se puedan probar en aislamiento.

// Umbral del Reglamento de Comprobantes de Pago (RS 068-93): en operaciones con consumidor final
// que NO superan S/5, emitir comprobante es facultativo, siempre que se lleve control diario y se
// emita al cierre del día una boleta por el total de las no emitidas. Por eso 500 céntimos es una
// constante legal, no un ajuste configurable.
const UMBRAL_CONSOLIDADO_CENT = 500;

// Perú es UTC−5 todo el año (no hay horario de verano). El "día" fiscal del consolidado debe ser el
// día peruano: si se usara UTC, todo lo cobrado entre las 19:00 y medianoche caería en el día
// siguiente y el consolidado no cuadraría con la caja.
const OFFSET_PERU_MS = 5 * 60 * 60 * 1000;

function fechaPeru(ms) {
  return new Date(ms - OFFSET_PERU_MS).toISOString().slice(0, 10); // YYYY-MM-DD
}

// ¿Esta operación va a boleta individual o al montón del consolidado?
function requiereBoletaIndividual(montoCent) {
  return montoCent > UMBRAL_CONSOLIDADO_CENT;
}

// Correlativo formateado como lo espera SUNAT: serie alfanumérica + número sin ceros a la izquierda
// en el JSON, pero mostrado con 8 dígitos en el comprobante.
function formatearNumero(serie, correlativo) {
  return `${serie}-${String(correlativo).padStart(8, "0")}`;
}

/**
 * Desglose del IGV a partir de un monto TOTAL (lo que el usuario pagó ya incluye el impuesto).
 *
 * `igvPorcentaje` en 0 cubre a quien no discrimina IGV (Nuevo RUS): todo va como inafecto y el
 * comprobante sale sin impuesto. Con 18 se parte el total en base + IGV.
 *
 * Se trabaja en céntimos y se redondea una sola vez, al IGV; la base se obtiene por diferencia. Así
 * base + igv === total SIEMPRE, que es lo que valida SUNAT. Calcular ambos por separado y redondear
 * los dos deja descuadres de un céntimo que hacen rechazar el comprobante.
 */
function desglosarIgv(totalCent, igvPorcentaje) {
  if (!(totalCent > 0)) throw new Error("El total debe ser mayor que cero.");
  if (!igvPorcentaje) {
    return { gravadaCent: 0, inafectaCent: totalCent, igvCent: 0, totalCent };
  }
  const factor = igvPorcentaje / 100;
  const baseCent = Math.round(totalCent / (1 + factor));
  const igvCent = totalCent - baseCent;
  return { gravadaCent: baseCent, inafectaCent: 0, igvCent, totalCent };
}

// Céntimos → número con dos decimales, que es como viajan los importes en el JSON del proveedor.
function aSoles(cent) {
  return Math.round(cent) / 100;
}

module.exports = {
  UMBRAL_CONSOLIDADO_CENT,
  fechaPeru,
  requiereBoletaIndividual,
  formatearNumero,
  desglosarIgv,
  aSoles,
};
