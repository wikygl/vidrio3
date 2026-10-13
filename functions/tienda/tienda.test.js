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

// ─────────────────────────────── Fases 2 y 3 ───────────────────────────────

const {
  tarifas,
  tarifaDe,
  nuevoDestacado,
  claveCiudad,
  temaPedidos,
  validarProveedor,
  telefonoLimpio,
  validarPedidoCliente,
  estadisticaPrecios,
  planAvanzado,
  anuncioPublico,
  casaTexto,
  palabraDeConsulta,
} = require("./comun");

const DIA = 24 * 60 * 60 * 1000;
const HOY = 1_700_000_000_000;

test("las tarifas nuevas tienen su valor por defecto y su tope", () => {
  const t = tarifas(null);
  assert.deepStrictEqual(t.destacar, { precioCent: 200, dias: 7 });
  assert.deepStrictEqual(t.proveedor, { precioCent: 1000, dias: 30 });
  assert.deepStrictEqual(t.pedido, { precioCent: 100, maxTalleres: 3 });
  assert.deepStrictEqual(t.servicio, t.anuncio, "sin tarifa propia, el servicio paga como un producto");
  assert.strictEqual(t.webPublica, true);
  const p = tarifas({ precio_cent: 400, servicio_precio_cent: 600, destacar_precio_cent: 999999, web_publica: false });
  assert.strictEqual(tarifaDe(p, "servicio").precioCent, 600);
  assert.strictEqual(tarifaDe(p, "producto").precioCent, 400);
  assert.strictEqual(p.destacar.precioCent, 200, "un tipeo no cobra de más");
  assert.strictEqual(p.webPublica, false);
});

test("destacar suma desde el destacado vigente y no pasa del vencimiento", () => {
  const base = { estado: "activo", venceMs: HOY + 20 * DIA, destacadoMs: 0, ahoraMs: HOY, dias: 7 };
  assert.deepStrictEqual(nuevoDestacado(base), { ok: true, hastaMs: HOY + 7 * DIA });
  assert.strictEqual(nuevoDestacado(Object.assign({}, base, { destacadoMs: HOY + 3 * DIA })).hastaMs, HOY + 10 * DIA);
  const corto = nuevoDestacado(Object.assign({}, base, { venceMs: HOY + 2 * DIA }));
  assert.ok(!corto.ok);
  assert.match(corto.error, /vence en 2 días: renuévelo/);
  assert.ok(!nuevoDestacado(Object.assign({}, base, { estado: "pausado" })).ok);
  assert.ok(!nuevoDestacado(Object.assign({}, base, { venceMs: HOY - 1 })).ok);
});

test("un servicio lleva zonas que también se buscan, y sus propias unidades", () => {
  const r = validarAnuncio(valido({
    tipo: "servicio", unidad: "hora", zonas: ["El Tambo", "Chilca", "huancayo", " chilca "],
  }), UID, ID);
  assert.ok(r.ok, r.error);
  assert.deepStrictEqual(r.anuncio.zonas, ["El Tambo", "Chilca"], "sin repetir ni repetir la ciudad");
  assert.ok(r.anuncio.palabras.includes("chilca") && r.anuncio.palabras.includes("tambo"));
  assert.match(validarAnuncio(valido({ tipo: "servicio", unidad: "plancha" }), UID, ID).error, /servicio se cobra/);
  assert.match(validarAnuncio(valido({ tipo: "servicio", unidad: "hora", zonas: ["a", "b", "c", "d", "e", "f"] }), UID, ID).error, /5 zonas/);
  assert.deepStrictEqual(validarAnuncio(valido({ zonas: ["Chilca"] }), UID, ID).anuncio.zonas, [], "un producto no tiene zonas");
});

test("las apps que aceptaron las reglas 1 todavía publican", () => {
  assert.ok(validarAnuncio(valido({ reglasVersion: 1 }), UID, ID).ok);
  assert.ok(validarAnuncio(valido({ reglasVersion: 2 }), UID, ID).ok);
  assert.ok(!validarAnuncio(valido({ reglasVersion: 3 }), UID, ID).ok);
});

test("la clave de ciudad sirve para comparar y como tema FCM", () => {
  assert.strictEqual(claveCiudad("  Huancayo "), "huancayo");
  assert.strictEqual(claveCiudad("Cerro de Pasco"), "cerro_de_pasco");
  assert.strictEqual(claveCiudad("Ñaña"), "nana");
  assert.strictEqual(temaPedidos("San Juan de Lurigancho"), "pedidos_san_juan_de_lurigancho");
  assert.match(temaPedidos("Áncash, Perú!"), /^pedidos_[a-zA-Z0-9_]+$/);
  assert.strictEqual(temaPedidos("  "), "");
});

test("la ficha del proveedor sale limpia, con su ciudad y zonas como claves", () => {
  const logo = `https://firebasestorage.googleapis.com/v0/b/x/o/proveedores%2F${UID}%2Flogo.jpg?alt=media`;
  const r = validarProveedor({
    nombre: "Vidriería Los Andes", ciudad: "Huancayo", zonas: ["El Tambo"],
    rubros: ["Vidrio y espejos", "Autos", "Vidrio y espejos"], descripcion: "Corte al toque", logo,
  }, UID);
  assert.ok(r.ok, r.error);
  assert.deepStrictEqual(r.ficha.rubros, ["Vidrio y espejos"]);
  assert.deepStrictEqual(r.ficha.zonasClave, ["huancayo", "el_tambo"]);
  assert.strictEqual(validarProveedor({ nombre: "AB", ciudad: "Lima", rubros: ["Otros"] }, UID).error, "Escriba el nombre del negocio.");
  assert.strictEqual(validarProveedor({ nombre: "ABC", ciudad: "Lima", rubros: [] }, UID).error, "Elija al menos un rubro.");
  assert.match(validarProveedor({ nombre: "ABC", ciudad: "Lima", rubros: ["Otros"], logo: logo.replace(UID, "otro") }, UID).error, /logo/);
});

test("el teléfono queda en dígitos y un celular peruano gana el 51", () => {
  assert.strictEqual(telefonoLimpio("987 654 321"), "51987654321");
  assert.strictEqual(telefonoLimpio("+51 987-654-321"), "51987654321");
  assert.strictEqual(telefonoLimpio("064 123456"), "064123456");
  assert.strictEqual(telefonoLimpio("123"), "");
});

test("el pedido de la web separa lo público del contacto y para a los robots", () => {
  const p = { que: "Mampara de baño", ciudad: "Huancayo", detalle: "Para ducha de 1.20", medidas: "120 x 190",
    nombre: "Rosa María Quispe", telefono: "987654321" };
  const r = validarPedidoCliente(p);
  assert.ok(r.ok, r.error);
  assert.strictEqual(r.publico.nombreCorto, "Rosa");
  assert.strictEqual(r.publico.ciudadClave, "huancayo");
  assert.ok(!("telefono" in r.publico));
  assert.deepStrictEqual(r.contacto, { nombre: "Rosa María Quispe", telefono: "51987654321" });
  assert.ok(!validarPedidoCliente(Object.assign({}, p, { web: "http://spam" })).ok, "la trampa");
  assert.strictEqual(validarPedidoCliente(Object.assign({}, p, { que: "Auto" })).error, "Elija qué necesita.");
  assert.strictEqual(validarPedidoCliente(Object.assign({}, p, { telefono: "12" })).error, "Revise su número de teléfono.");
  const dirigido = validarPedidoCliente({ anuncioId: ID, nombre: "Luis", telefono: "987654321" });
  assert.ok(dirigido.ok, "un pedido a un anuncio no necesita decir qué es ni la ciudad");
});

test("los precios de referencia solo salen con 3 vendedores distintos", () => {
  const a = (uid, precioCent, extra) => Object.assign({
    uid, precioCent, unidad: "m²", ciudad: "Huancayo", titulo: "Vidrio templado 8mm", categoria: "Vidrio y espejos",
    tipo: "producto", estado: "activo", publicadoMs: HOY,
  }, extra || {});
  const lista = [a("u1", 9000), a("u2", 10000), a("u2", 99999, { publicadoMs: HOY - DIA }), a("u3", 12000),
    a("u4", 8000, { ciudad: "Lima" }), a("u5", 1, { estado: "bloqueado" }), a("u6", 0)];
  const hyo = estadisticaPrecios(lista, { texto: "templado 8", ciudad: "huancayo" });
  assert.deepStrictEqual(hyo, [{ unidad: "m²", vendedores: 3, minCent: 9000, medianaCent: 10000, maxCent: 12000 }],
    "u2 cuenta una vez con su precio más reciente; los bloqueados y sin precio no cuentan");
  assert.strictEqual(estadisticaPrecios(lista, { texto: "templado", ciudad: "Lima" }).length, 0, "un vendedor solo no se muestra");
  assert.strictEqual(estadisticaPrecios(lista, { texto: "templado", ciudad: "" })[0].vendedores, 4);
  assert.strictEqual(estadisticaPrecios(lista, { texto: "laminado", ciudad: "" }).length, 0);
});

test("buscar casa todas las palabras, en singular, y un número suelto por su comienzo", () => {
  const a = { titulo: "Vidrios templados 8mm", categoria: "Vidrio y espejos", ciudad: "Huancayo", tipo: "producto", descripcion: "incoloro" };
  assert.ok(casaTexto(a, "templado 8"));
  assert.ok(casaTexto(a, "vidrio incoloro"));
  assert.ok(!casaTexto(a, "templado bronce"));
  assert.strictEqual(palabraDeConsulta("Vidrios de 8"), "vidrio");
});

test("el plan de pago se lee como en la app", () => {
  const ts = (m) => ({ toMillis: () => m });
  assert.ok(planAvanzado({ mode: "FULL", full_until: ts(HOY + DIA) }, HOY));
  assert.ok(!planAvanzado({ mode: "FULL", full_until: ts(HOY - DIA) }, HOY));
  assert.ok(!planAvanzado({ mode: "FULL" }, HOY));
  assert.ok(!planAvanzado({ mode: "BASIC", full_until: ts(HOY + DIA) }, HOY));
  assert.ok(planAvanzado({ mode: "VENTAS" }, HOY));
  assert.ok(!planAvanzado(null, HOY));
});

test("la web no ve uid, lo pagado ni las denuncias", () => {
  const ts = (m) => ({ toMillis: () => m });
  const a = { uid: "secreto", pagadoCent: 300, denuncias: 2, titulo: "Perfil", precioCent: 100, miniaturas: ["m"], fotos: ["g"],
    publicadoEn: ts(HOY), destacadoHasta: ts(HOY + DIA), vendedorNombre: "Juan", descripcion: "d" };
  const corto = anuncioPublico("X", a, false, HOY);
  assert.strictEqual(corto.miniatura, "m");
  assert.strictEqual(corto.destacado, true);
  assert.ok(!("vendedorNombre" in corto));
  const largo = anuncioPublico("X", a, true, HOY);
  for (const k of ["uid", "pagadoCent", "denuncias"]) assert.ok(!(k in largo), k);
  assert.strictEqual(largo.vendedorNombre, "Juan");
});
