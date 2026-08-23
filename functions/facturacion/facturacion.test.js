// Pruebas de la facturación que no tocan Firestore ni la red: aritmética del IGV, umbral del
// consolidado, día peruano y armado del payload. Se corren con `node --test`.

const test = require("node:test");
const assert = require("node:assert");

const {
  UMBRAL_CONSOLIDADO_CENT,
  fechaPeru,
  requiereBoletaIndividual,
  formatearNumero,
  desglosarIgv,
  aSoles,
} = require("./comun");
const { ProveedorNubeFact, ProveedorFalso } = require("./proveedor");

test("base + IGV siempre da exactamente el total, sin descuadres de un céntimo", () => {
  // Es la invariante que valida SUNAT. Se barren todos los montos del rango real de la app
  // (S/0.01 a S/20) más los tramos de recarga habituales.
  for (let total = 1; total <= 2000; total++) {
    const r = desglosarIgv(total, 18);
    assert.strictEqual(
      r.gravadaCent + r.igvCent, total,
      `descuadre en ${total} céntimos: ${r.gravadaCent} + ${r.igvCent}`
    );
    assert.ok(r.igvCent >= 0, `IGV negativo en ${total}`);
  }
});

test("un régimen sin IGV manda todo a inafecto", () => {
  const r = desglosarIgv(1500, 0);
  assert.strictEqual(r.gravadaCent, 0);
  assert.strictEqual(r.inafectaCent, 1500);
  assert.strictEqual(r.igvCent, 0);
  assert.strictEqual(r.totalCent, 1500);
});

test("desglose de S/15 con IGV 18%", () => {
  const r = desglosarIgv(1500, 18);
  assert.strictEqual(r.gravadaCent, 1271);  // 15 / 1.18 = 12.7118…
  assert.strictEqual(r.igvCent, 229);
  assert.strictEqual(r.gravadaCent + r.igvCent, 1500);
});

test("un total de cero o negativo no puede generar comprobante", () => {
  assert.throws(() => desglosarIgv(0, 18));
  assert.throws(() => desglosarIgv(-100, 18));
});

test("el umbral del consolidado es S/5 y está en el lado correcto", () => {
  assert.strictEqual(UMBRAL_CONSOLIDADO_CENT, 500);
  // "No superan S/5" es facultativo: exactamente 500 va al consolidado, 501 ya no.
  assert.strictEqual(requiereBoletaIndividual(500), false);
  assert.strictEqual(requiereBoletaIndividual(501), true);
  assert.strictEqual(requiereBoletaIndividual(100), false);
  assert.strictEqual(requiereBoletaIndividual(1500), true);
});

test("el día del consolidado es el peruano, no el UTC", () => {
  // 2026-08-23 02:00 UTC son las 21:00 del 22 en Lima: la operación pertenece al día 22.
  assert.strictEqual(fechaPeru(Date.parse("2026-08-23T02:00:00Z")), "2026-08-22");
  // 2026-08-23 06:00 UTC son las 01:00 del 23 en Lima.
  assert.strictEqual(fechaPeru(Date.parse("2026-08-23T06:00:00Z")), "2026-08-23");
  // Justo en el corte: 05:00 UTC = medianoche exacta en Lima.
  assert.strictEqual(fechaPeru(Date.parse("2026-08-23T05:00:00Z")), "2026-08-23");
  assert.strictEqual(fechaPeru(Date.parse("2026-08-23T04:59:59Z")), "2026-08-22");
});

test("el correlativo se muestra con ocho dígitos", () => {
  assert.strictEqual(formatearNumero("B001", 7), "B001-00000007");
  assert.strictEqual(formatearNumero("B001", 12345678), "B001-12345678");
});

test("céntimos a soles con dos decimales", () => {
  assert.strictEqual(aSoles(1500), 15);
  assert.strictEqual(aSoles(1), 0.01);
  assert.strictEqual(aSoles(1271), 12.71);
});

test("el payload de NubeFact cuadra con el desglose", () => {
  const p = new ProveedorNubeFact({ ruta: "https://x.local", token: "t" });
  const igv = desglosarIgv(1500, 18);
  const payload = p.construirPayload({
    serie: "B001",
    correlativo: 42,
    fechaEmision: "2026-08-22",
    igvPorcentaje: 18,
    gravadaCent: igv.gravadaCent,
    inafectaCent: igv.inafectaCent,
    igvCent: igv.igvCent,
    totalCent: igv.totalCent,
    clienteTipoDoc: "-",
    clienteNumDoc: "00000000",
    clienteNombre: "VARIOS",
    clienteDireccion: "",
    items: [{
      descripcion: "Recarga de saldo Crystal",
      valorUnitarioCent: igv.gravadaCent,
      precioUnitarioCent: igv.totalCent,
      subtotalCent: igv.gravadaCent,
      igvCent: igv.igvCent,
      totalCent: igv.totalCent,
    }],
  });

  assert.strictEqual(payload.tipo_de_comprobante, 2);      // boleta
  assert.strictEqual(payload.serie, "B001");
  assert.strictEqual(payload.numero, 42);
  assert.strictEqual(payload.moneda, 1);                   // PEN
  assert.strictEqual(payload.total_gravada, 12.71);
  assert.strictEqual(payload.total_igv, 2.29);
  assert.strictEqual(payload.total, 15);
  assert.strictEqual(payload.total_gravada + payload.total_igv, payload.total);
  assert.strictEqual(payload.items[0].tipo_de_igv, 1);     // gravado
});

test("sin IGV el ítem sale marcado como inafecto", () => {
  const p = new ProveedorNubeFact({ ruta: "https://x.local", token: "t" });
  const igv = desglosarIgv(1500, 0);
  const payload = p.construirPayload({
    serie: "B001", correlativo: 1, fechaEmision: "2026-08-22", igvPorcentaje: 0,
    gravadaCent: igv.gravadaCent, inafectaCent: igv.inafectaCent,
    igvCent: igv.igvCent, totalCent: igv.totalCent,
    clienteTipoDoc: "-", clienteNumDoc: "00000000", clienteNombre: "VARIOS", clienteDireccion: "",
    items: [{ descripcion: "x", valorUnitarioCent: 1500, precioUnitarioCent: 1500, subtotalCent: 1500, igvCent: 0, totalCent: 1500 }],
  });
  assert.strictEqual(payload.items[0].tipo_de_igv, 9);     // inafecto
  assert.strictEqual(payload.total_inafecta, 15);
  assert.strictEqual(payload.total_igv, 0);
});

test("NubeFact respondiendo 200 con errors se trata como fallo, no como éxito", async () => {
  const fetchFalso = async () => ({
    ok: true,
    status: 200,
    json: async () => ({ errors: "El correlativo ya fue utilizado" }),
  });
  const p = new ProveedorNubeFact({ ruta: "https://x.local", token: "t", fetchImpl: fetchFalso });
  await assert.rejects(
    () => p.emitirBoleta({ serie: "B001", correlativo: 1, igvPorcentaje: 18, gravadaCent: 1, inafectaCent: 0, igvCent: 0, totalCent: 1, items: [] }),
    /correlativo ya fue utilizado/
  );
});

test("un 5xx se marca reintentable; un rechazo de datos no", async () => {
  const p500 = new ProveedorNubeFact({
    ruta: "https://x.local", token: "t",
    fetchImpl: async () => ({ ok: false, status: 503, json: async () => ({}) }),
  });
  await p500.emitirBoleta({ serie: "B001", correlativo: 1, igvPorcentaje: 18, gravadaCent: 1, inafectaCent: 0, igvCent: 0, totalCent: 1, items: [] })
    .then(() => assert.fail("debía fallar"))
    .catch((e) => assert.strictEqual(e.reintentable, true));

  const pDatos = new ProveedorNubeFact({
    ruta: "https://x.local", token: "t",
    fetchImpl: async () => ({ ok: true, status: 200, json: async () => ({ errors: "RUC inválido" }) }),
  });
  await pDatos.emitirBoleta({ serie: "B001", correlativo: 1, igvPorcentaje: 18, gravadaCent: 1, inafectaCent: 0, igvCent: 0, totalCent: 1, items: [] })
    .then(() => assert.fail("debía fallar"))
    .catch((e) => assert.strictEqual(e.reintentable, false));
});

test("el proveedor falso devuelve un comprobante utilizable", async () => {
  const p = new ProveedorFalso();
  const r = await p.emitirBoleta({ serie: "B001", correlativo: 9, igvPorcentaje: 18, gravadaCent: 1271, inafectaCent: 0, igvCent: 229, totalCent: 1500, items: [] });
  assert.strictEqual(r.numeroCompleto, "B001-00000009");
  assert.strictEqual(r.aceptadoPorSunat, true);
  assert.strictEqual(p.emitidos.length, 1);
});
