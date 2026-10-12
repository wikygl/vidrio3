package crystal.crystal.tienda

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import java.text.Normalizer
import java.util.Locale

/**
 * Un anuncio de la Tienda (plan en docs/TIENDA_ANUNCIOS_PLAN.md).
 *
 * Lo crea solo el servidor (`publicarAnuncio`), que cobra del wallet; el dueño después edita su
 * texto, su precio y sus fotos, y lo pausa o lo retira.
 */
data class Anuncio(
    val id: String,
    val uid: String,
    val vendedorNombre: String,
    val tipo: String,
    val categoria: String,
    val titulo: String,
    val descripcion: String,
    val precioCent: Long,
    val unidad: String,
    val condicion: String,
    val ciudad: String,
    val fotos: List<String>,
    val miniaturas: List<String>,
    val estado: String,
    val publicadoEnMs: Long,
    val venceEnMs: Long,
    val vistas: Long
) {
    val esServicio: Boolean get() = tipo == TiendaReglas.SERVICIO

    /** Vigente de verdad: activo y sin vencer. La lista esconde lo demás aunque el estado no se haya puesto al día. */
    fun visibleEnLaTienda(ahoraMs: Long): Boolean = estado == ESTADO_ACTIVO && venceEnMs > ahoraMs

    companion object {
        const val ESTADO_ACTIVO = "activo"
        const val ESTADO_PAUSADO = "pausado"
        const val ESTADO_RETIRADO = "retirado"
        const val ESTADO_VENCIDO = "vencido"
        const val ESTADO_REVISION = "en_revision"
        const val ESTADO_BLOQUEADO = "bloqueado"

        fun desde(doc: DocumentSnapshot): Anuncio? {
            if (!doc.exists()) return null
            fun lista(campo: String) = (doc.get(campo) as? List<*>)?.filterIsInstance<String>().orEmpty()
            fun ms(campo: String) = (doc.get(campo) as? Timestamp)?.toDate()?.time ?: 0L
            return Anuncio(
                id = doc.id,
                uid = doc.getString("uid").orEmpty(),
                vendedorNombre = doc.getString("vendedorNombre").orEmpty(),
                tipo = doc.getString("tipo").orEmpty(),
                categoria = doc.getString("categoria").orEmpty(),
                titulo = doc.getString("titulo").orEmpty(),
                descripcion = doc.getString("descripcion").orEmpty(),
                precioCent = doc.getLong("precioCent") ?: 0L,
                unidad = doc.getString("unidad").orEmpty(),
                condicion = doc.getString("condicion").orEmpty(),
                ciudad = doc.getString("ciudad").orEmpty(),
                fotos = lista("fotos"),
                miniaturas = lista("miniaturas"),
                estado = doc.getString("estado").orEmpty(),
                publicadoEnMs = ms("publicadoEn"),
                venceEnMs = ms("venceEn"),
                vistas = doc.getLong("vistas") ?: 0L
            )
        }
    }
}

/**
 * Lo que la app y el servidor tienen que decir igual: categorías, unidades, topes, la versión de
 * las reglas y cómo se arman las palabras de búsqueda. El espejo está en `functions/tienda/comun.js`:
 * si algo cambia aquí, cambia allá, o el servidor rechaza el anuncio o la búsqueda deja de casar.
 */
object TiendaReglas {
    const val PRODUCTO = "producto"
    const val SERVICIO = "servicio"
    const val NUEVO = "nuevo"
    const val USADO = "usado"

    val CATEGORIAS = listOf(
        "Vidrio y espejos",
        "Aluminio y perfiles",
        "Accesorios y herrajes",
        "Melamina y madera",
        "Herramientas y maquinaria",
        "Retazos y sobrantes",
        "Puertas y ventanas hechas",
        "Instalación y servicios",
        "Otros"
    )

    val UNIDADES = listOf("c/u", "m²", "ml", "pie²", "plancha", "kg", "juego", "servicio", "hora", "día")

    const val MAX_TITULO = 80
    const val MAX_DESCRIPCION = 1500
    const val MAX_CIUDAD = 60
    const val MAX_FOTOS = 5
    const val MAX_PRECIO_CENT = 100_000_000L
    const val REGLAS_VERSION = 1

    /** Si `config/anuncios` no dice otra cosa: S/ 3.00 por 30 días. */
    const val PRECIO_POR_DEFECTO_CENT = 300L
    const val DIAS_POR_DEFECTO = 30

    /** El mismo criterio que `precioVigente` del servidor: lo del panel, si es razonable. */
    fun precioVigente(precioCent: Long?, dias: Long?): Pair<Long, Int> {
        val p = precioCent?.takeIf { it in 0L..5000L } ?: PRECIO_POR_DEFECTO_CENT
        val d = dias?.takeIf { it in 1L..365L }?.toInt() ?: DIAS_POR_DEFECTO
        return p to d
    }

    private val VACIAS = setOf(
        "de", "del", "la", "las", "el", "los", "y", "o", "en", "con", "para", "por", "un", "una",
        "unos", "unas", "a", "al", "se", "su", "sus", "mm", "cm"
    )

    /** minúsculas, sin tildes ni signos: "Vidrio Templado 8mm" → "vidrio templado 8mm". */
    fun normalizar(texto: String): String =
        Normalizer.normalize(texto.lowercase(Locale.ROOT), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .replace(Regex("[^a-z0-9ñ ]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    /** Las palabras con que se encuentra un anuncio; ver `palabrasDe` en el servidor. */
    fun palabrasDe(titulo: String, categoria: String, ciudad: String, tipo: String): List<String> {
        val fuente = listOf(titulo, categoria, ciudad, tipo).joinToString(" ") { normalizar(it) }
        val set = LinkedHashSet<String>()
        for (p in fuente.split(" ")) {
            if (p.length < 2 || p in VACIAS) continue
            set += p
            if (p.length > 3 && p.endsWith("es")) set += p.dropLast(2)
            if (p.length > 3 && p.endsWith("s")) set += p.dropLast(1)
        }
        return set.take(40)
    }

    /**
     * La palabra con que se consulta al servidor: la más larga de lo escrito, en singular. Las demás
     * se filtran en el teléfono sobre lo que llegó.
     */
    fun palabraDeBusqueda(texto: String): String? {
        val p = normalizar(texto).split(" ").filter { it.length >= 2 && it !in VACIAS }
            .maxByOrNull { it.length } ?: return null
        return singular(p)
    }

    fun singular(p: String): String = when {
        p.length > 4 && p.endsWith("es") -> p.dropLast(2)
        p.length > 3 && p.endsWith("s") -> p.dropLast(1)
        else -> p
    }

    /** ¿El anuncio tiene todas las palabras de lo buscado? (en singular, contra sus palabras). */
    fun casa(anuncio: Anuncio, texto: String): Boolean {
        val propias = palabrasDe(anuncio.titulo, anuncio.categoria, anuncio.ciudad, anuncio.tipo).toSet() +
            palabrasDe(anuncio.descripcion, "", "", "").toSet()
        return normalizar(texto).split(" ").filter { it.length >= 2 && it !in VACIAS }
            .all { singular(it) in propias || it in propias }
    }

    /** Revisa el formulario antes de cobrar; null si está bien, o lo que falta en palabras del vendedor. */
    fun error(
        tipo: String, categoria: String, titulo: String, descripcion: String, precioCent: Long?,
        unidad: String, condicion: String, ciudad: String, fotos: Int
    ): String? = when {
        tipo != PRODUCTO && tipo != SERVICIO -> "Elija si es un producto o un servicio."
        categoria !in CATEGORIAS -> "Elija una categoría."
        titulo.trim().length < 4 -> "El título es muy corto."
        titulo.trim().length > MAX_TITULO -> "El título pasa de $MAX_TITULO letras."
        descripcion.trim().length > MAX_DESCRIPCION -> "La descripción pasa de $MAX_DESCRIPCION letras."
        precioCent == null || precioCent < 0 || precioCent > MAX_PRECIO_CENT -> "El precio no es válido."
        unidad !in UNIDADES -> "Elija la unidad del precio."
        tipo == PRODUCTO && condicion != NUEVO && condicion != USADO -> "Indique si es nuevo o usado."
        ciudad.trim().length < 2 -> "Indique la ciudad."
        ciudad.trim().length > MAX_CIUDAD -> "La ciudad es muy larga."
        fotos > MAX_FOTOS -> "Son $MAX_FOTOS fotos como máximo."
        else -> null
    }

    /** `15000` → `S/ 150.00`; 0 es "A tratar". */
    fun precio(precioCent: Long): String =
        if (precioCent <= 0L) "A tratar" else "S/ " + "%,.2f".format(Locale.US, precioCent / 100.0)

    /** "Hace 3 días", "Hoy", "Ayer". */
    fun hace(ms: Long, ahoraMs: Long): String {
        if (ms <= 0L) return ""
        val dias = ((ahoraMs - ms) / 86_400_000L).toInt()
        return when {
            dias <= 0 -> "Hoy"
            dias == 1 -> "Ayer"
            dias < 30 -> "Hace $dias días"
            dias < 60 -> "Hace un mes"
            else -> "Hace ${dias / 30} meses"
        }
    }

    /** "Vence en 12 días", "Vence mañana", "Venció". */
    fun vence(ms: Long, ahoraMs: Long): String {
        if (ms <= 0L) return ""
        val dias = Math.ceil((ms - ahoraMs) / 86_400_000.0).toInt()
        return when {
            dias <= 0 -> "Venció"
            dias == 1 -> "Vence mañana"
            else -> "Vence en $dias días"
        }
    }

    fun nombreEstado(estado: String): String = when (estado) {
        Anuncio.ESTADO_ACTIVO -> "Activo"
        Anuncio.ESTADO_PAUSADO -> "Pausado"
        Anuncio.ESTADO_RETIRADO -> "Retirado"
        Anuncio.ESTADO_VENCIDO -> "Vencido"
        Anuncio.ESTADO_REVISION -> "En revisión"
        Anuncio.ESTADO_BLOQUEADO -> "Bloqueado"
        else -> estado
    }

    /** Las reglas que se aceptan antes del primer anuncio (versión [REGLAS_VERSION]). */
    val TEXTO_REGLAS = """
        1. Solo productos y servicios del rubro: vidrio, aluminio, melamina, carpintería metálica, accesorios, herramientas, maquinaria, retazos e instalación.
        2. Nada robado, falsificado o ilegal, ni anuncios engañosos (precio o fotos que no son del producto).
        3. Fotos propias o con permiso; nada de fotos con la marca de otra empresa.
        4. Un anuncio por producto; no repetir el mismo anuncio.
        5. El trato, el pago y la entrega son entre comprador y vendedor; Crystal no interviene en la venta.
        6. Un anuncio que incumpla puede retirarse sin devolución.
    """.trimIndent()
}
