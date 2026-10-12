// Pruebas de la tienda que no tocan Firestore: validación, palabras de búsqueda, precio y
// vencimiento. Se corren con `node --test`.

const test = require("node:test");
const assert = require("node:assert");

const {
  palabrasDe,
  precioVigente,
  fotoValida,
  validarAnuncio,
  idValido,
  nuevoVencimiento,
  normalizar,
  REGLAS_VERSION,
} = require("./comun");

const UID = "abcUID123";
const ID = "AbCdEfGhIjKlMnOpQrSt";
const foto = (n) =>
  `https://firebasestorage.googleapis.com/v0/b/crystal-4f306.firebasestorage.app/o/anuncios%2F${UID}%2F${ID}%2F${n}.jpg?alt=media&token=x`;

function valido(cambios) {
  return Object.assign({
    tipo: "producto",
    categoria: "Vidrio y espejos",
    titulo: "Vidrio templado 8 mm incoloro",
    descripcion: "Planchas de 2.14 x 3.30",
    precioCent: 15000,
    unidad: "m²",
    condicion: "nuevo",
    ciudad: "Huancayo",
    fotos: [foto(0)],
    miniaturas: [foto("0_mini")],
    reglasVersion: REGLAS_VERSION,
  }, cambios || {});
}

test("un anuncio completo pasa y sale con sus palabras", () => {
  const r = validarAnuncio(valido(), UID, ID);
  assert.ok(r.ok, r.error);
  assert.ok(r.anuncio.palabras.includes("templado"));
  assert.ok(r.anuncio.palabras.includes("huancayo"));
  assert.ok(r.anuncio.palabras.includes("espejo"), "el singular de 'espejos' también busca");
});

test("lo que falta o sobra se dice en palabras del vendedor", () => {
  assert.strictEqual(validarAnuncio(valido({ titulo: "ab" }), UID, ID).error, "El título es muy corto.");
  assert.strictEqual(validarAnuncio(valido({ categoria: "Autos" }), UID, ID).error, "Elija una categoría.");
  assert.strictEqual(validarAnuncio(valido({ precioCent: -1 }), UID, ID).error, "El precio no es válido.");
  assert.strictEqual(validarAnuncio(valido({ precioCent: 10.5 }), UID, ID).error, "El precio no es válido.");
  assert.strictEqual(validarAnuncio(valido({ condicion: "" }), UID, ID).error, "Indique si es nuevo o usado.");
  assert.strictEqual(validarAnuncio(valido({ reglasVersion: 0 }), UID, ID).error, "Acepte las reglas de la tienda.");
  assert.strictEqual(validarAnuncio(valido({ ciudad: " " }), UID, ID).error, "Indique la ciudad.");
});

test("un servicio no lleva nuevo/usado y el precio a tratar es 0", () => {
  const r = validarAnuncio(valido({ tipo: "servicio", condicion: "usado", unidad: "servicio", precioCent: 0 }), UID, ID);
  assert.ok(r.ok, r.error);
  assert.strictEqual(r.anuncio.condicion, "");
  assert.strictEqual(r.anuncio.precioCent, 0);
});

test("solo valen fotos de nuestro Storage, del vendedor y del anuncio", () => {
  assert.ok(fotoValida(foto(0), UID, ID));
  assert.ok(!fotoValida("https://otro.com/a.jpg", UID, ID));
  assert.ok(!fotoValida(foto(0).replace(UID, "otroUID"), UID, ID), "foto de otro vendedor");
  assert.ok(!fotoValida(foto(0).replace(ID, "OtroAnuncio123456789"), UID, ID), "foto de otro anuncio");
  const sinMini = validarAnuncio(valido({ miniaturas: [] }), UID, ID);
  assert.strictEqual(sinMini.error, "Faltan miniaturas de las fotos.");
  const seis = Array.from({ length: 6 }, (_, i) => foto(i));
  assert.strictEqual(validarAnuncio(valido({ fotos: seis, miniaturas: seis }), UID, ID).error, "Son 5 fotos como máximo.");
});

test("el precio del panel se respeta solo si es razonable", () => {
  assert.deepStrictEqual(precioVigente(null), { precioCent: 300, dias: 30 });
  assert.deepStrictEqual(precioVigente({ precio_cent: 500, dias: 15 }), { precioCent: 500, dias: 15 });
  assert.deepStrictEqual(precioVigente({ precio_cent: 0, dias: 30 }), { precioCent: 0, dias: 30 }, "gratis se puede");
  assert.deepStrictEqual(precioVigente({ precio_cent: 300000, dias: 0 }), { precioCent: 300, dias: 30 }, "un tipeo no cobra S/ 3000");
  assert.deepStrictEqual(precioVigente({ precio_cent: "500" }), { precioCent: 300, dias: 30 });
});

test("renovar suma desde el vencimiento si sigue vigente, y desde hoy si ya venció", () => {
  const dia = 24 * 60 * 60 * 1000;
  const hoy = 1_000_000_000_000;
  assert.strictEqual(nuevoVencimiento(hoy + 5 * dia, hoy, 30), hoy + 35 * dia);
  assert.strictEqual(nuevoVencimiento(hoy - 5 * dia, hoy, 30), hoy + 30 * dia);
  assert.strictEqual(nuevoVencimiento(0, hoy, 30), hoy + 30 * dia);
});

test("las palabras salen sin tildes, sin signos y sin repetidas", () => {
  assert.strictEqual(normalizar("  Ventána  CORREDIZA, 2.5m!! "), "ventana corrediza 2 5m");
  const p = palabrasDe({ titulo: "Ventanas de aluminio y aluminio", categoria: "Otros", ciudad: "Lima", tipo: "producto" });
  assert.ok(p.includes("ventanas") && p.includes("ventana"));
  assert.strictEqual(p.filter((x) => x === "aluminio").length, 1);
  assert.ok(!p.includes("de") && !p.includes("y"));
});

test("el id del anuncio es el de Firestore: 20 letras y números", () => {
  assert.ok(idValido(ID));
  assert.ok(!idValido("corto"));
  assert.ok(!idValido("../../usuarios/xxxxxxxx"));
});
