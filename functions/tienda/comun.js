// Tienda de anuncios: lo que no toca Firestore ni la red. Validar lo que manda la app, armar las
// palabras de búsqueda y decidir el precio vigente. Se prueba con `node --test` (tienda.test.js).
//
// La lista de categorías y la normalización de palabras están TAMBIÉN en la app
// (crystal.crystal.tienda.Anuncio.kt): si cambian aquí, cambian allá, o la búsqueda deja de casar.

/** Lo que cuesta un anuncio si `config/anuncios` no dice otra cosa: S/ 3.00 por 30 días. */
const PRECIO_POR_DEFECTO = { precioCent: 300, dias: 30 };

/** Topes del precio configurable: un error de tipeo en el panel no puede cobrar S/ 3000. */
const PRECIO_MAX_CENT = 5000;
const DIAS_MAX = 365;

const CATEGORIAS = [
  "Vidrio y espejos",
  "Aluminio y perfiles",
  "Accesorios y herrajes",
  "Melamina y madera",
  "Herramientas y maquinaria",
  "Retazos y sobrantes",
  "Puertas y ventanas hechas",
  "Instalación y servicios",
  "Otros",
];

const TIPOS = ["producto", "servicio"];
const CONDICIONES = ["nuevo", "usado"];
const UNIDADES = ["c/u", "m²", "ml", "pie²", "plancha", "kg", "juego", "servicio", "hora", "día"];

const MAX_TITULO = 80;
const MAX_DESCRIPCION = 1500;
const MAX_CIUDAD = 60;
const MAX_FOTOS = 5;
const MAX_PRECIO_CENT = 100000000; // S/ 1 000 000: más que eso es un error de tipeo
const REGLAS_VERSION = 1;

/** Palabras que no ayudan a buscar. */
const VACIAS = new Set([
  "de", "del", "la", "las", "el", "los", "y", "o", "en", "con", "para", "por", "un", "una",
  "unos", "unas", "a", "al", "se", "su", "sus", "mm", "cm",
]);

/** minúsculas, sin tildes ni signos: "Vidrio Templado 8mm" → "vidrio templado 8mm". */
function normalizar(texto) {
  return String(texto || "")
    .toLowerCase()
    .normalize("NFD")
    .replace(/[̀-ͯ]/g, "")
    .replace(/[^a-z0-9ñ ]+/g, " ")
    .replace(/\s+/g, " ")
    .trim();
}

/**
 * Las palabras con que se encuentra un anuncio. Van todas las del título, la categoría y la ciudad,
 * y también el singular de las que terminan en "s" ("ventanas" → "ventana"), porque se busca
 * "ventana" para encontrar "ventanas corredizas".
 */
function palabrasDe({ titulo, categoria, ciudad, tipo }) {
  const fuente = [titulo, categoria, ciudad, tipo].map(normalizar).join(" ");
  const set = new Set();
  for (const p of fuente.split(" ")) {
    if (p.length < 2 || VACIAS.has(p)) continue;
    set.add(p);
    if (p.length > 3 && p.endsWith("es")) set.add(p.slice(0, -2));
    if (p.length > 3 && p.endsWith("s")) set.add(p.slice(0, -1));
  }
  return Array.from(set).slice(0, 40);
}

/** El precio que rige hoy: el de `config/anuncios` si es razonable, si no el de por defecto. */
function precioVigente(config) {
  const c = config || {};
  const precioCent = Number.isInteger(c.precio_cent) && c.precio_cent >= 0 && c.precio_cent <= PRECIO_MAX_CENT
    ? c.precio_cent : PRECIO_POR_DEFECTO.precioCent;
  const dias = Number.isInteger(c.dias) && c.dias >= 1 && c.dias <= DIAS_MAX
    ? c.dias : PRECIO_POR_DEFECTO.dias;
  return { precioCent, dias };
}

/**
 * Una foto vale solo si está en NUESTRO Storage y en la carpeta del propio vendedor y del propio
 * anuncio: así nadie publica imágenes de otro sitio ni las de otro anuncio.
 */
function fotoValida(url, uid, anuncioId) {
  if (typeof url !== "string" || url.length > 1000) return false;
  if (!url.startsWith("https://firebasestorage.googleapis.com/v0/b/")) return false;
  return url.includes(`/o/anuncios%2F${uid}%2F${anuncioId}%2F`);
}

/**
 * Revisa lo que mandó la app y devuelve el anuncio limpio, o el primer error en palabras que el
 * vendedor entienda.
 */
function validarAnuncio(data, uid, anuncioId) {
  const d = data || {};
  const err = (mensaje) => ({ ok: false, error: mensaje });

  const tipo = String(d.tipo || "");
  if (!TIPOS.includes(tipo)) return err("Elija si es un producto o un servicio.");
  const categoria = String(d.categoria || "");
  if (!CATEGORIAS.includes(categoria)) return err("Elija una categoría.");

  const titulo = String(d.titulo || "").replace(/\s+/g, " ").trim();
  if (titulo.length < 4) return err("El título es muy corto.");
  if (titulo.length > MAX_TITULO) return err(`El título pasa de ${MAX_TITULO} letras.`);

  const descripcion = String(d.descripcion || "").trim();
  if (descripcion.length > MAX_DESCRIPCION) return err(`La descripción pasa de ${MAX_DESCRIPCION} letras.`);

  const precioCent = Number(d.precioCent);
  if (!Number.isInteger(precioCent) || precioCent < 0 || precioCent > MAX_PRECIO_CENT) {
    return err("El precio no es válido.");
  }
  const unidad = String(d.unidad || "");
  if (!UNIDADES.includes(unidad)) return err("Elija la unidad del precio.");

  // El estado nuevo/usado no aplica a un servicio.
  const condicion = tipo === "servicio" ? "" : String(d.condicion || "");
  if (tipo === "producto" && !CONDICIONES.includes(condicion)) return err("Indique si es nuevo o usado.");

  const ciudad = String(d.ciudad || "").replace(/\s+/g, " ").trim();
  if (ciudad.length < 2) return err("Indique la ciudad.");
  if (ciudad.length > MAX_CIUDAD) return err("La ciudad es muy larga.");

  const fotos = Array.isArray(d.fotos) ? d.fotos : [];
  const miniaturas = Array.isArray(d.miniaturas) ? d.miniaturas : [];
  if (fotos.length > MAX_FOTOS) return err(`Son ${MAX_FOTOS} fotos como máximo.`);
  if (miniaturas.length !== fotos.length) return err("Faltan miniaturas de las fotos.");
  for (const u of fotos.concat(miniaturas)) {
    if (!fotoValida(u, uid, anuncioId)) return err("Una foto no es válida; vuelva a elegirla.");
  }

  if (Number(d.reglasVersion) !== REGLAS_VERSION) return err("Acepte las reglas de la tienda.");

  return {
    ok: true,
    anuncio: {
      tipo, categoria, titulo, descripcion, precioCent, unidad, condicion, ciudad,
      fotos, miniaturas,
      palabras: palabrasDe({ titulo, categoria, ciudad, tipo }),
      reglasVersion: REGLAS_VERSION,
    },
  };
}

/** Un id de documento de Firestore como los que genera la app: 20 letras y números. */
function idValido(id) {
  return typeof id === "string" && /^[A-Za-z0-9]{20}$/.test(id);
}

/** Desde cuándo se cuentan los días al renovar: si sigue vigente, desde su vencimiento. */
function nuevoVencimiento(venceMs, ahoraMs, dias) {
  const base = venceMs && venceMs > ahoraMs ? venceMs : ahoraMs;
  return base + dias * 24 * 60 * 60 * 1000;
}

module.exports = {
  PRECIO_POR_DEFECTO,
  CATEGORIAS,
  TIPOS,
  UNIDADES,
  REGLAS_VERSION,
  MAX_FOTOS,
  normalizar,
  palabrasDe,
  precioVigente,
  fotoValida,
  validarAnuncio,
  idValido,
  nuevoVencimiento,
};
