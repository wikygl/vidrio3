package crystal.crystal.Diseno.nova

import crystal.crystal.taller.nova.NovaCalculos

/**
 * El diseño de una ventana Nova como MODELO, no como cadena de texto.
 *
 * Hoy el diseño vive en la cadena del paquete: cada edición la parsea, la reescribe y la vuelve a
 * parsear, y las reglas quedan repartidas en la cirugía de texto. Esta es la misma estrategia de
 * `MamparaModulos`: un objeto que es la ÚNICA fuente de la geometría, del que salen tanto el
 * dibujo como los conteos, y donde la cadena es solo la forma de guardarlo.
 *
 * Formato de la cadena (ver `docs/REGLAS_NOVA.md` §13.3):
 *
 * ```
 * {nova,<acabado>,[<ancho>,<alto>: Tl<w>(m<h>(f<w>f<w>);s<h>(f<w>c<w>)) P<2.5> Tl<w>(…) ]}
 * ```
 *
 * - Tramos separados por `P<2.5>`; el parante es vertical y parte el tramo ENTERO, con todas sus
 *   franjas. `A<90>` separa lados en L, C y serie.
 * - Franjas dentro del tramo, separadas por `;`: `s<alto>` sistema, `m<alto>` mocheta.
 * - Módulos dentro de la franja: `f<ancho>` fijo, `c<ancho>` corrediza. Un `;P;` dentro de una
 *   franja es un parante que parte solo esa franja, que el formato admite aunque el parante
 *   normal parta el tramo completo.
 *
 * Todavía no lo usa nadie: se construye primero, con sus pruebas de ida y vuelta contra paquetes
 * reales, para poder cambiar la pantalla encima sin romper lo que ya funciona.
 */

/** Un módulo: fijo o corrediza. [ancho] nulo = "el que salga" al repartir el tramo. */
data class NovaModulo(val tipo: Char, val ancho: Float? = null) {
    val esFijo: Boolean get() = tipo != 'c'
}

/** Una franja horizontal del tramo: la del sistema o una de mocheta. */
data class NovaFranja(
    val esSistema: Boolean,
    val alto: Float,
    val modulos: List<NovaModulo>
) {
    val nFijos: Int get() = modulos.count { it.esFijo }
    val nCorredizas: Int get() = modulos.count { !it.esFijo }
}

/**
 * Un tramo: el trozo de ventana entre dos parantes, con TODAS sus franjas. Un parante parte el
 * tramo entero, así que las franjas de un tramo empiezan y acaban juntas.
 */
data class NovaTramo(val ancho: Float, val franjas: List<NovaFranja>) {
    val sistema: NovaFranja? get() = franjas.firstOrNull { it.esSistema }
    val mochetas: List<NovaFranja> get() = franjas.filter { !it.esSistema }
    val nModulosSistema: Int get() = sistema?.modulos?.size ?: 0
}

/** El diseño completo. [etiquetas] son los tags sueltos del paquete (`A<90>`, `U<…>`, `O<1>`). */
data class DisenoNova(
    val acabado: String,
    val ancho: Float,
    val alto: Float,
    val tramos: List<NovaTramo>,
    val etiquetas: List<String> = emptyList()
) {
    val nTramos: Int get() = tramos.size
    val nModulos: Int get() = tramos.sumOf { it.nModulosSistema }
    val nFijos: Int get() = tramos.sumOf { t -> t.sistema?.nFijos ?: 0 }
    val nCorredizas: Int get() = tramos.sumOf { t -> t.sistema?.nCorredizas ?: 0 }

    /** Parantes entre tramos: uno menos que los tramos. */
    val nParantes: Int get() = (tramos.size - 1).coerceAtLeast(0)

    /** El ancho que queda para los módulos, descontando los parantes entre tramos. */
    fun anchoUtil(anchoParante: Float = 2.5f): Float =
        (ancho - nParantes * anchoParante).coerceAtLeast(0f)

    fun aPaquete(): String {
        val cuerpo = tramos.joinToString(" $SEPARADOR_TRAMO ") { tramo ->
            val franjas = tramo.franjas.joinToString(";") { fr ->
                val cabeza = if (fr.esSistema) "s" else "m"
                val mods = fr.modulos.joinToString("") { m ->
                    if (m.ancho != null) "${m.tipo}<${df(m.ancho)}>" else m.tipo.toString()
                }
                "$cabeza<${df(fr.alto)}>($mods)"
            }
            "Tl<${df(tramo.ancho)}>($franjas)"
        }
        val tags = if (etiquetas.isEmpty()) "" else " " + etiquetas.joinToString(" ")
        return "{nova,$acabado,[${df(ancho)},${df(alto)}:$cuerpo$tags]}"
    }

    companion object {
        const val SEPARADOR_TRAMO = "P<2.5>"

        private fun df(v: Float) = NovaCalculos.df1(v)

        private val RE_CABECERA = Regex("""\{nova\s*,\s*([a-z]+)\s*,\s*\[(.*)]}""", RegexOption.IGNORE_CASE)
        private val RE_FRANJA = Regex("""^([smSM])\s*<\s*([\d.,-]+)\s*>""")
        private val RE_MODULO = Regex("""([fcFC])\s*(?:<\s*([\d.,-]+)\s*>)?""")
        private val RE_TRAMO = Regex("""^t[a-z]?\s*<\s*([\d.,-]+)\s*>""", RegexOption.IGNORE_CASE)
        private val RE_ETIQUETA = Regex("""^[AUO]<[^>]*>$""", RegexOption.IGNORE_CASE)

        private fun num(s: String): Float = s.replace(",", ".").toFloatOrNull() ?: 0f

        /** Devuelve null si la cadena no es un paquete de Nova. */
        fun desdePaquete(paquete: String): DisenoNova? {
            val m = RE_CABECERA.find(paquete.trim()) ?: return null
            val acabado = m.groupValues[1].lowercase()
            val dentro = m.groupValues[2]
            val idx = dentro.indexOf(':')
            if (idx < 0) return null
            val dims = dentro.substring(0, idx).split(",")
            if (dims.size < 2) return null
            val ancho = num(dims[0])
            val alto = num(dims[1])

            val tramos = mutableListOf<NovaTramo>()
            val etiquetas = mutableListOf<String>()
            for (trozo in partirPorTramos(dentro.substring(idx + 1))) {
                val t = trozo.trim()
                if (t.isEmpty()) continue
                if (RE_ETIQUETA.matches(t)) { etiquetas.add(t); continue }
                val cab = RE_TRAMO.find(t)
                val anchoTramo = cab?.groupValues?.get(1)?.let { num(it) } ?: ancho
                val interior = interiorDeParentesis(t) ?: continue
                val franjas = partirNivelSuperior(interior, ';').mapNotNull { franjaDesdeTexto(it) }
                if (franjas.isNotEmpty()) tramos.add(NovaTramo(anchoTramo, franjas))
            }
            if (tramos.isEmpty()) return null
            return DisenoNova(acabado, ancho, alto, tramos, etiquetas)
        }

        private fun franjaDesdeTexto(texto: String): NovaFranja? {
            val t = texto.trim()
            val cab = RE_FRANJA.find(t) ?: return null
            val esSistema = cab.groupValues[1].lowercase() == "s"
            val alto = num(cab.groupValues[2])
            val interior = interiorDeParentesis(t) ?: return null
            val modulos = RE_MODULO.findAll(interior).map { mm ->
                val tipo = mm.groupValues[1].lowercase().first()
                val ancho = mm.groupValues[2].takeIf { it.isNotBlank() }?.let { num(it) }
                NovaModulo(tipo, ancho)
            }.toList()
            if (modulos.isEmpty()) return null
            return NovaFranja(esSistema, alto, modulos)
        }

        /** Corta el cuerpo por los separadores de tramo, respetando los paréntesis anidados. */
        private fun partirPorTramos(cuerpo: String): List<String> {
            val out = mutableListOf<String>()
            val sb = StringBuilder()
            var prof = 0
            var i = 0
            while (i < cuerpo.length) {
                val c = cuerpo[i]
                when {
                    c == '(' -> { prof++; sb.append(c) }
                    c == ')' -> { prof--; sb.append(c) }
                    prof == 0 && cuerpo.startsWith(SEPARADOR_TRAMO, i) -> {
                        out.add(sb.toString()); sb.clear(); i += SEPARADOR_TRAMO.length - 1
                    }
                    prof == 0 && c == ' ' -> { out.add(sb.toString()); sb.clear() }
                    else -> sb.append(c)
                }
                i++
            }
            out.add(sb.toString())
            return out.filter { it.isNotBlank() }
        }

        private fun partirNivelSuperior(texto: String, sep: Char): List<String> {
            val out = mutableListOf<String>()
            val sb = StringBuilder()
            var prof = 0
            for (c in texto) {
                when {
                    c == '(' -> { prof++; sb.append(c) }
                    c == ')' -> { prof--; sb.append(c) }
                    c == sep && prof == 0 -> { out.add(sb.toString()); sb.clear() }
                    else -> sb.append(c)
                }
            }
            out.add(sb.toString())
            return out.filter { it.isNotBlank() }
        }

        /** El contenido del primer paréntesis equilibrado, o null si no hay. */
        private fun interiorDeParentesis(texto: String): String? {
            val abre = texto.indexOf('(')
            if (abre < 0) return null
            var prof = 0
            for (i in abre until texto.length) {
                when (texto[i]) {
                    '(' -> prof++
                    ')' -> { prof--; if (prof == 0) return texto.substring(abre + 1, i) }
                }
            }
            return null
        }
    }
}
