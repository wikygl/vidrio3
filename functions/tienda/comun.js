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
/** Un servicio se cobra por trabajo, por tiempo o por medida; no por plancha ni por kilo. */
const UNIDADES_SERVICIO = ["servicio", "hora", "día", "m²", "ml", "pie²"];

const MAX_TITULO = 80;
const MAX_DESCRIPCION = 1500;
const MAX_CIUDAD = 60;
const MAX_FOTOS = 5;
const MAX_PRECIO_CENT = 100000000; // S/ 1 000 000: más que eso es un error de tipeo
/** Las reglas vigentes. La 2 suma que los anuncios también se ven en la web pública. */
const REGLAS_VERSION = 2;
/** Las que el servidor todavía acepta: las apps ya instaladas mandan la 1. */
const REGLAS_ACEPTADAS = [1, 2];
const MAX_ZONAS = 5;
const MAX_ZONA = 40;

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
function palabrasDe({ titulo, categoria, ciudad, tipo, zonas }) {
  // Las zonas de atención de un servicio también se buscan: "Chilca" encuentra a quien atiende ahí.
  const fuente = [titulo, categoria, ciudad, tipo].concat(Array.isArray(zonas) ? zonas : [])
    .map(normalizar).join(" ");
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
  if (tipo === "servicio" && !UNIDADES_SERVICIO.includes(unidad)) {
    return err("Un servicio se cobra por servicio, por hora, por día o por medida.");
  }

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

  // Solo un servicio atiende en zonas; un producto "está" en su ciudad.
  const zonas = tipo === "servicio" ? limpiarZonas(d.zonas, ciudad) : [];
  if (zonas === null) return err(`Son ${MAX_ZONAS} zonas como máximo, de hasta ${MAX_ZONA} letras.`);

  const reglasVersion = Number(d.reglasVersion);
  if (!REGLAS_ACEPTADAS.includes(reglasVersion)) return err("Acepte las reglas de la tienda.");

  return {
    ok: true,
    anuncio: {
      tipo, categoria, titulo, descripcion, precioCent, unidad, condicion, ciudad, zonas,
      fotos, miniaturas,
      palabras: palabrasDe({ titulo, categoria, ciudad, tipo, zonas }),
      reglasVersion,
    },
  };
}

/**
 * Las zonas escritas, limpias y sin repetir (ni repetir la ciudad). Null si son demasiadas o alguna
 * es demasiado larga: eso se le dice al vendedor en vez de cortarlo en silencio.
 */
function limpiarZonas(lista, ciudad) {
  if (lista == null) return [];
  if (!Array.isArray(lista) || lista.length > MAX_ZONAS) return null;
  const vistas = new Set([normalizar(ciudad)]);
  const zonas = [];
  for (const z of lista) {
    const t = String(z || "").replace(/\s+/g, " ").trim();
    if (t.length > MAX_ZONA) return null;
    const n = normalizar(t);
    if (n.length < 2 || vistas.has(n)) continue;
    vistas.add(n);
    zonas.push(t);
  }
  return zonas;
}

// ─────────────────────────────── Tarifas ───────────────────────────────

/** Un entero de `config/anuncios` si está dentro de [min, max]; si no, el de por defecto. */
function entero(valor, min, max, porDefecto) {
  return Number.isInteger(valor) && valor >= min && valor <= max ? valor : porDefecto;
}

/**
 * Todo lo que se cobra en la tienda, leído de `config/anuncios` con su valor por defecto y su tope
 * (un error de tipeo en el panel no puede cobrar de más). El servicio usa la tarifa del producto si
 * el panel no le pone una propia.
 */
function tarifas(config) {
  const c = config || {};
  const anuncio = precioVigente(c);
  return {
    anuncio,
    servicio: {
      precioCent: entero(c.servicio_precio_cent, 0, PRECIO_MAX_CENT, anuncio.precioCent),
      dias: entero(c.servicio_dias, 1, DIAS_MAX, anuncio.dias),
    },
    destacar: {
      precioCent: entero(c.destacar_precio_cent, 0, 5000, 200),
      dias: entero(c.destacar_dias, 1, 60, 7),
    },
    proveedor: {
      precioCent: entero(c.proveedor_precio_cent, 0, 20000, 1000),
      dias: entero(c.proveedor_dias, 1, DIAS_MAX, 30),
    },
    pedido: {
      precioCent: entero(c.pedido_precio_cent, 0, 2000, 100),
      maxTalleres: entero(c.pedido_max_talleres, 1, 10, 3),
    },
    webPublica: c.web_publica !== false,
  };
}

/** La tarifa de publicar o renovar según lo que es: producto o servicio. */
function tarifaDe(t, tipo) {
  return tipo === "servicio" ? t.servicio : t.anuncio;
}

// ─────────────────────────────── Destacar ───────────────────────────────

const DIA_MS = 24 * 60 * 60 * 1000;

/**
 * Hasta cuándo queda destacado: suma desde el destacado vigente, si lo hay. No pasa del vencimiento
 * del anuncio: cobrar 7 días y dar 2 no es justo, así que se pide renovar primero.
 */
function nuevoDestacado({ estado, venceMs, destacadoMs, ahoraMs, dias }) {
  if (estado !== "activo" || !(venceMs > ahoraMs)) {
    return { ok: false, error: "Solo se destaca un anuncio activo y vigente." };
  }
  const base = destacadoMs && destacadoMs > ahoraMs ? destacadoMs : ahoraMs;
  const hastaMs = base + dias * DIA_MS;
  if (hastaMs > venceMs) {
    const quedan = Math.max(0, Math.floor((venceMs - ahoraMs) / DIA_MS));
    return {
      ok: false,
      error: `El anuncio vence en ${quedan} día${quedan === 1 ? "" : "s"}: renuévelo primero y luego destáquelo.`,
    };
  }
  return { ok: true, hastaMs };
}

// ─────────────────────────────── Ciudades ───────────────────────────────

/**
 * La ciudad como clave: sin tildes, sin eñe y con guiones bajos ("Pasco Ñaña" → "pasco_nana"). Sirve
 * para comparar ciudades escritas de distinta forma y como nombre de tema FCM, que no admite eñe ni
 * espacios. El espejo está en la app (`TiendaReglas.claveCiudad`).
 */
function claveCiudad(texto) {
  return normalizar(texto).replace(/ñ/g, "n").replace(/[^a-z0-9 ]/g, "").trim().replace(/ /g, "_");
}

/** El tema FCM donde se avisa a los talleres de una ciudad de un pedido nuevo. */
function temaPedidos(ciudad) {
  const c = claveCiudad(ciudad);
  return c ? "pedidos_" + c.slice(0, 60) : "";
}

// ─────────────────────────────── Proveedores ───────────────────────────────

const MAX_NOMBRE_PROVEEDOR = 60;
const MAX_DESCRIPCION_PROVEEDOR = 600;

/** El logo del proveedor: solo de nuestro Storage y de su propia carpeta. */
function logoValido(url, uid) {
  if (url == null || url === "") return true;
  if (typeof url !== "string" || url.length > 1000) return false;
  if (!url.startsWith("https://firebasestorage.googleapis.com/v0/b/")) return false;
  return url.includes(`/o/proveedores%2F${uid}%2F`);
}

/** La ficha de la vitrina, limpia, o el primer error. Arma `zonasClave` (ciudad + zonas). */
function validarProveedor(data, uid) {
  const d = data || {};
  const err = (mensaje) => ({ ok: false, error: mensaje });
  const nombre = String(d.nombre || "").replace(/\s+/g, " ").trim();
  if (nombre.length < 3) return err("Escriba el nombre del negocio.");
  if (nombre.length > MAX_NOMBRE_PROVEEDOR) return err("El nombre es muy largo.");
  const ciudad = String(d.ciudad || "").replace(/\s+/g, " ").trim();
  if (ciudad.length < 2) return err("Indique la ciudad.");
  if (ciudad.length > MAX_CIUDAD) return err("La ciudad es muy larga.");
  const zonas = limpiarZonas(d.zonas, ciudad);
  if (zonas === null) return err(`Son ${MAX_ZONAS} zonas como máximo, de hasta ${MAX_ZONA} letras.`);
  const rubros = Array.isArray(d.rubros)
    ? Array.from(new Set(d.rubros.map(String))).filter((r) => CATEGORIAS.includes(r)) : [];
  if (rubros.length === 0) return err("Elija al menos un rubro.");
  const descripcion = String(d.descripcion || "").trim();
  if (descripcion.length > MAX_DESCRIPCION_PROVEEDOR) {
    return err(`La descripción pasa de ${MAX_DESCRIPCION_PROVEEDOR} letras.`);
  }
  const logo = String(d.logo || "");
  if (!logoValido(logo, uid)) return err("El logo no es válido; vuelva a elegirlo.");
  const zonasClave = Array.from(new Set([ciudad].concat(zonas).map(claveCiudad).filter(Boolean)));
  return { ok: true, ficha: { nombre, ciudad, zonas, rubros, descripcion, logo, zonasClave } };
}

// ─────────────────────────────── Pedidos de clientes ───────────────────────────────

/** Lo que un cliente final puede pedir desde la web. */
const QUE_PEDIDO = ["Ventana", "Puerta", "Mampara de baño", "Vidrio o espejo", "Ropero o melamina", "Reja o estructura", "Otro"];
const MAX_DETALLE_PEDIDO = 800;
const MAX_MEDIDAS_PEDIDO = 200;
const DIAS_PEDIDO = 15;

/**
 * El teléfono con solo sus dígitos. Un celular peruano (9 dígitos que empiezan en 9) gana el 51
 * delante, para que WhatsApp lo abra directo; otros números se aceptan de 7 a 15 dígitos.
 */
function telefonoLimpio(texto) {
  const dig = String(texto || "").replace(/[^0-9]/g, "");
  if (/^9\d{8}$/.test(dig)) return "51" + dig;
  if (dig.length >= 7 && dig.length <= 15) return dig;
  return "";
}

/**
 * Revisa el pedido que llega de la web. Separa lo público (lo que ven los talleres antes de tomarlo)
 * del contacto (nombre y teléfono, que solo recibe quien lo toma). `web` es una trampa: un campo
 * escondido que solo llenan los robots.
 */
function validarPedidoCliente(data) {
  const d = data || {};
  const err = (mensaje) => ({ ok: false, error: mensaje });
  if (String(d.web || "") !== "") return err("No se pudo enviar.");
  const anuncioId = d.anuncioId ? String(d.anuncioId) : "";
  if (anuncioId && !idValido(anuncioId)) return err("No se pudo enviar.");
  const que = String(d.que || "");
  // Un pedido a un anuncio concreto no necesita decir qué es: es lo del anuncio.
  if (!anuncioId && !QUE_PEDIDO.includes(que)) return err("Elija qué necesita.");
  const ciudad = String(d.ciudad || "").replace(/\s+/g, " ").trim();
  if (!anuncioId && ciudad.length < 2) return err("Indique su ciudad.");
  if (ciudad.length > MAX_CIUDAD) return err("La ciudad es muy larga.");
  const detalle = String(d.detalle || "").trim();
  if (detalle.length > MAX_DETALLE_PEDIDO) return err(`El detalle pasa de ${MAX_DETALLE_PEDIDO} letras.`);
  const medidas = String(d.medidas || "").trim();
  if (medidas.length > MAX_MEDIDAS_PEDIDO) return err("Las medidas son muy largas.");
  if (!anuncioId && detalle.length < 5 && medidas.length < 3) return err("Cuéntenos qué necesita.");
  const nombre = String(d.nombre || "").replace(/\s+/g, " ").trim();
  if (nombre.length < 2 || nombre.length > 60) return err("Escriba su nombre.");
  const telefono = telefonoLimpio(d.telefono);
  if (!telefono) return err("Revise su número de teléfono.");
  return {
    ok: true,
    anuncioId,
    publico: {
      que: QUE_PEDIDO.includes(que) ? que : "",
      ciudad,
      ciudadClave: claveCiudad(ciudad),
      detalle,
      medidas,
      // A los talleres que todavía no lo tomaron solo se les muestra el primer nombre.
      nombreCorto: nombre.split(" ")[0],
    },
    contacto: { nombre, telefono },
  };
}

// ─────────────────────────────── Precios de referencia ───────────────────────────────

/** Con menos vendedores distintos que esto no se muestra un grupo: se adivinaría el precio de uno. */
const MIN_VENDEDORES_REFERENCIA = 3;
const DIAS_REFERENCIA = 180;

function singular(p) {
  if (p.length > 4 && p.endsWith("es")) return p.slice(0, -2);
  if (p.length > 3 && p.endsWith("s")) return p.slice(0, -1);
  return p;
}

/** Las palabras que se buscan, sin vacías: "Vidrios templados de 8" → ["vidrios","templados","8"]. */
function palabrasBuscadas(texto) {
  return normalizar(texto).split(" ").filter((p) => p.length >= 1 && !VACIAS.has(p));
}

/** La palabra con que se consulta Firestore: la más larga, en singular (como hace la app). */
function palabraDeConsulta(texto) {
  const p = palabrasBuscadas(texto).filter((x) => x.length >= 2).sort((a, b) => b.length - a.length)[0];
  return p ? singular(p) : null;
}

/** "ventana" busca también "ventanas" y "ventanes", que es como pueden haber quedado guardadas. */
function variantes(p) {
  return Array.from(new Set([p, p + "s", p + "es", singular(p)])).slice(0, 10);
}

/** ¿El anuncio tiene todas las palabras buscadas? Mismo criterio que `TiendaReglas.casa` de la app. */
function casaTexto(a, texto) {
  const propias = new Set(
    palabrasDe({ titulo: a.titulo, categoria: a.categoria, ciudad: a.ciudad, tipo: a.tipo, zonas: a.zonas })
      .concat(palabrasDe({ titulo: a.descripcion || "", categoria: "", ciudad: "", tipo: "" }))
  );
  // Un número suelto ("8" de "templado 8") se busca tal cual; los de una letra se ignoran.
  return palabrasBuscadas(texto).filter((p) => p.length >= 2 || /\d/.test(p))
    .every((p) => propias.has(p) || propias.has(singular(p)) || [...propias].some((x) => /\d/.test(p) && x.startsWith(p)));
}

function mediana(ordenados) {
  const n = ordenados.length;
  if (n === 0) return 0;
  const m = Math.floor(n / 2);
  return n % 2 ? ordenados[m] : Math.round((ordenados[m - 1] + ordenados[m]) / 2);
}

/** ¿El anuncio es de esa ciudad (o la atiende)? */
function esDeCiudad(a, clave) {
  if (!clave) return true;
  return claveCiudad(a.ciudad) === clave || (Array.isArray(a.zonas) && a.zonas.some((z) => claveCiudad(z) === clave));
}

/**
 * Mínimo, mediana y máximo por unidad de los anuncios que casan con lo buscado. Solo los grupos con
 * [MIN_VENDEDORES_REFERENCIA] vendedores distintos o más; cada vendedor cuenta una vez por grupo (su
 * precio más reciente), para que diez anuncios del mismo no muevan la mediana.
 */
function estadisticaPrecios(anuncios, { texto, ciudad }) {
  const clave = claveCiudad(ciudad || "");
  const grupos = new Map();
  const ordenados = anuncios.slice().sort((x, y) => (y.publicadoMs || 0) - (x.publicadoMs || 0));
  for (const a of ordenados) {
    if (!(a.precioCent > 0) || ["en_revision", "bloqueado"].includes(a.estado)) continue;
    if (!esDeCiudad(a, clave) || !casaTexto(a, texto)) continue;
    const g = grupos.get(a.unidad) || new Map();
    if (!g.has(a.uid)) g.set(a.uid, a.precioCent);
    grupos.set(a.unidad, g);
  }
  const salida = [];
  for (const [unidad, porVendedor] of grupos) {
    if (porVendedor.size < MIN_VENDEDORES_REFERENCIA) continue;
    const precios = Array.from(porVendedor.values()).sort((x, y) => x - y);
    salida.push({
      unidad,
      vendedores: precios.length,
      minCent: precios[0],
      medianaCent: mediana(precios),
      maxCent: precios[precios.length - 1],
    });
  }
  return salida.sort((x, y) => y.vendedores - x.vendedores);
}

/** ¿El usuario tiene un plan de pago vigente? Mismo criterio que `Suscripcion.avanzadoActivo`. */
function planAvanzado(estadoServicio, ahoraMs) {
  const es = estadoServicio || {};
  const mode = String(es.mode || "BASIC");
  if (mode === "BASIC") return false;
  const fu = es.full_until;
  const ms = fu && typeof fu.toMillis === "function" ? fu.toMillis() : Number(fu || 0);
  if (mode === "FULL") return ms > ahoraMs;
  return !(ms > 0) || ms > ahoraMs;
}

// ─────────────────────────────── Web pública ───────────────────────────────

/** Lo que la web muestra de un anuncio: nada de uid, lo pagado ni las denuncias. */
function anuncioPublico(id, a, conDetalle, ahoraMs) {
  const ms = (t) => (t && typeof t.toMillis === "function" ? t.toMillis() : 0);
  const base = {
    id,
    titulo: String(a.titulo || ""),
    precioCent: Number(a.precioCent || 0),
    unidad: String(a.unidad || ""),
    tipo: String(a.tipo || ""),
    categoria: String(a.categoria || ""),
    condicion: String(a.condicion || ""),
    ciudad: String(a.ciudad || ""),
    miniatura: (Array.isArray(a.miniaturas) && a.miniaturas[0]) || (Array.isArray(a.fotos) && a.fotos[0]) || "",
    publicadoEn: ms(a.publicadoEn),
    destacado: ms(a.destacadoHasta) > ahoraMs,
  };
  if (!conDetalle) return base;
  return Object.assign(base, {
    descripcion: String(a.descripcion || ""),
    fotos: Array.isArray(a.fotos) ? a.fotos : [],
    zonas: Array.isArray(a.zonas) ? a.zonas : [],
    vendedorNombre: String(a.vendedorNombre || ""),
  });
}

/** ¿Se puede mostrar en la tienda (app o web)? Activo y sin vencer. */
function visible(a, ahoraMs) {
  const v = a && a.venceEn && typeof a.venceEn.toMillis === "function" ? a.venceEn.toMillis() : 0;
  return a && a.estado === "activo" && v > ahoraMs;
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
  UNIDADES_SERVICIO,
  REGLAS_VERSION,
  REGLAS_ACEPTADAS,
  MAX_FOTOS,
  QUE_PEDIDO,
  DIAS_PEDIDO,
  DIAS_REFERENCIA,
  MIN_VENDEDORES_REFERENCIA,
  DIA_MS,
  normalizar,
  palabrasDe,
  precioVigente,
  tarifas,
  tarifaDe,
  fotoValida,
  validarAnuncio,
  limpiarZonas,
  idValido,
  nuevoVencimiento,
  nuevoDestacado,
  claveCiudad,
  temaPedidos,
  logoValido,
  validarProveedor,
  telefonoLimpio,
  validarPedidoCliente,
  palabraDeConsulta,
  variantes,
  casaTexto,
  estadisticaPrecios,
  planAvanzado,
  anuncioPublico,
  visible,
};
