// Pruebas de los avisos por correo que no tocan Firestore ni la red: qué aviso corresponde, la
// clave de deduplicación y el contenido de las plantillas.

const test = require("node:test");
const assert = require("node:assert");

const { avisosPendientes, DIAS_PREAVISO } = require("./index");
const plantillas = require("./plantillas");
const { EnviadorFalso } = require("./enviador");

const DIA = 24 * 60 * 60 * 1000;
const AHORA = Date.parse("2026-08-23T15:00:00Z");
const full = (ms) => ({ mode: "FULL", full_until: { toMillis: () => ms } });

test("un plan que vence dentro del preaviso genera aviso", () => {
  const a = avisosPendientes(full(AHORA + 2 * DIA), AHORA);
  assert.strictEqual(a.length, 1);
  assert.strictEqual(a[0].tipo, "porVencer");
  assert.strictEqual(a[0].dias, 2);
});

test("un plan lejano no genera nada", () => {
  assert.deepStrictEqual(avisosPendientes(full(AHORA + 30 * DIA), AHORA), []);
});

test("el borde del preaviso avisa, y un minuto más allá no", () => {
  assert.strictEqual(avisosPendientes(full(AHORA + DIAS_PREAVISO * DIA - 1), AHORA).length, 1);
  assert.strictEqual(avisosPendientes(full(AHORA + DIAS_PREAVISO * DIA + 60000), AHORA).length, 0);
});

test("recién vencido avisa; vencido hace mucho NO (no se persigue a quien ya se fue)", () => {
  const reciente = avisosPendientes(full(AHORA - DIA), AHORA);
  assert.strictEqual(reciente.length, 1);
  assert.strictEqual(reciente[0].tipo, "vencido");
  assert.deepStrictEqual(avisosPendientes(full(AHORA - 180 * DIA), AHORA), []);
});

test("nunca se avisa a la vez por vencer y vencido", () => {
  for (const dias of [-4, -2, -1, 0.5, 1, 2, 3, 5, 10]) {
    const a = avisosPendientes(full(AHORA + dias * DIA), AHORA);
    assert.ok(a.length <= 1, `dos avisos a la vez en ${dias} días`);
  }
});

test("un plan BASIC o sin fecha no recibe avisos de vencimiento", () => {
  assert.deepStrictEqual(avisosPendientes({ mode: "BASIC" }, AHORA), []);
  assert.deepStrictEqual(avisosPendientes({ mode: "FULL" }, AHORA), []);
  assert.deepStrictEqual(avisosPendientes(null, AHORA), []);
});

test("la clave cambia al renovar, para que el usuario vuelva a poder recibir aviso", () => {
  const antes = avisosPendientes(full(AHORA + DIA), AHORA)[0].clave;
  // Renueva 30 días: nueva fecha de corte, nueva clave.
  const despues = avisosPendientes(full(AHORA + 31 * DIA), AHORA + 30 * DIA)[0].clave;
  assert.notStrictEqual(antes, despues);
});

test("la clave es estable dentro del mismo vencimiento (no se repite el aviso)", () => {
  const fin = AHORA + 2 * DIA;
  const a = avisosPendientes(full(fin), AHORA)[0];
  const b = avisosPendientes(full(fin), AHORA + 60 * 60 * 1000)[0];
  assert.strictEqual(a.clave, b.clave);
});

test("las plantillas llevan el enlace y no van vacías", () => {
  const url = "https://ejemplo.local";
  for (const m of [
    plantillas.bienvenida({ nombre: "Gonzalo" }, { URL_RECARGA: url }),
    plantillas.porVencer({ nombre: "Gonzalo", dias: 2, fullUntilMs: AHORA }, { URL_RECARGA: url }),
    plantillas.vencido({ nombre: "Gonzalo" }, { URL_RECARGA: url }),
  ]) {
    assert.ok(m.asunto && m.asunto.length > 5, "asunto vacío");
    assert.ok(m.html.includes(url), "el HTML no lleva el enlace");
    assert.ok(m.texto.includes(url), "el texto plano no lleva el enlace");
    assert.ok(m.texto.includes("Gonzalo"), "no personaliza");
  }
});

test("sin nombre las plantillas no dicen 'Hola undefined'", () => {
  const m = plantillas.bienvenida({});
  assert.ok(!/undefined|null/.test(m.texto), m.texto.slice(0, 60));
  assert.ok(m.texto.startsWith("Hola,"));
});

test("'vence mañana' cuando queda un día, no 'en 1 días'", () => {
  const m = plantillas.porVencer({ dias: 1, fullUntilMs: AHORA });
  assert.ok(m.asunto.includes("mañana"), m.asunto);
  assert.ok(!m.texto.includes("en 1 días"));
});

test("la fecha se muestra en día peruano", () => {
  // 2026-08-24 03:00 UTC son las 22:00 del 23 en Lima.
  assert.strictEqual(plantillas.fechaLegible(Date.parse("2026-08-24T03:00:00Z")), "23/08/2026");
});

test("el enviador falso registra lo enviado y propaga el fallo", async () => {
  const e = new EnviadorFalso();
  await e.enviar({ para: "a@b.c", asunto: "x", texto: "y", html: "<p>y</p>" });
  assert.strictEqual(e.enviados.length, 1);
  assert.strictEqual(e.enviados[0].para, "a@b.c");

  const roto = new EnviadorFalso({ fallarCon: "SMTP caído" });
  await assert.rejects(() => roto.enviar({ para: "a@b.c" }), /SMTP caído/);
});
