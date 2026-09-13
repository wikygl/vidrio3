package crystal.crystal.taller

/**
 * Un lado de la ventana de esquina, tal como se midió en el apunte.
 *
 * Viene con sus dos anchos y sus dos altos porque en obra la pared no está a plomo: eso es el
 * descuadre, y se apunta lado por lado. La calculadora trabaja con un número, así que manda el
 * MAYOR —la ventana tiene que tapar el hueco— y el vidriero, que recibe la imagen de la medida
 * junto al cálculo, decide si esa es la que quiere.
 */
data class LadoEsquina(
    val anchoAbajo: Float,
    val anchoArriba: Float,
    val altoIzq: Float,
    val altoDer: Float,
    val puente: Float,
    /**
     * Su panza, si este "lado" es en realidad la pared curva: 0 en una pared recta.
     *
     * Una curva no suele ser un lado —es la esquina, y va entre dos paredes—, pero cuando la
     * pared a la que sustituye mide cero la curva ocupa su sitio: la ventana empieza (o acaba) en
     * ella y entonces sí es una pared más, con su desarrollo por ancho.
     */
    val flecha: Float = 0f
) {
    val esCurva: Boolean get() = flecha > 0f

    val ancho: Float get() = maxOf(anchoAbajo, anchoArriba)
    val alto: Float get() = maxOf(altoIzq, altoDer)

    /** true si los dos anchos, o los dos altos, no dan lo mismo: hay descuadre que mirar. */
    val descuadrado: Boolean
        get() = kotlin.math.abs(anchoAbajo - anchoArriba) > 0.15f ||
            kotlin.math.abs(altoIzq - altoDer) > 0.15f
}

/**
 * La curva con la que se resuelve una esquina, con lo que hace falta para dibujarla y cortarla.
 *
 * El [desarrollo] es el aluminio estirado —lo que se corta— y la [cuerda] lo recto de punta a
 * punta; de las dos sale la flecha y cuánto dobla. [alto] y [puente] son los de su propio paño,
 * que en el apunte se miden como los de cualquier pared.
 */
data class CurvaEsquina(
    val desarrollo: Float,
    val cuerda: Float,
    val alto: Float,
    val puente: Float
)

/**
 * La ventana de esquina del apunte: sus paredes en el orden en que se recorren y lo que hay en
 * cada arista entre una y la siguiente.
 *
 * La CANTIDAD de lados es la que elige la geometría —2 en L, 3 en C, más en serie—, que es la
 * misma regla con la que ya trabaja el paquete simbólico de Nova.
 */
data class EsquinaMedida(
    val lados: List<LadoEsquina>,
    /** Una entrada por arista: los grados, o [CURVA] si esa esquina se resolvió con un arco. */
    val angulos: List<String>
) {
    val hayCurva: Boolean get() = angulos.any { it.startsWith(CURVA) }

    /** Las aristas que no son de 90°, para poder avisar de ellas. Una curva no es un ángulo. */
    val anguloDistinto: List<Float>
        get() = angulos.mapNotNull { it.toFloatOrNull() }
            .filter { kotlin.math.abs(kotlin.math.abs(it) - 90f) > 0.5f }

    /** La curva de esa arista, si se resolvió con uno; null si doblan en punta. */
    fun curvaDe(arista: Int): CurvaEsquina? {
        val t = angulos.getOrNull(arista)?.takeIf { it.startsWith(CURVA) } ?: return null
        val n = t.removePrefix(CURVA).split("|").map { it.trim().toFloatOrNull() ?: return null }
        if (n.size < 4 || n[0] <= 0f || n[1] <= 0f) return null
        return CurvaEsquina(n[0], n[1], n[2], n[3])
    }

    /** Los grados de esa arista, si dobla en punta. */
    fun gradosDe(arista: Int): Float? = angulos.getOrNull(arista)?.toFloatOrNull()

    /** La geometría de Nova que le toca; null si con un solo lado no hay esquina que armar. */
    val geometria: String?
        get() = when {
            lados.size == 2 -> "nl"
            lados.size == 3 -> "nu"
            lados.size > 3 -> "ns"
            else -> null
        }

    companion object {
        /** Marca de arista resuelta con una curva, en lugar de los grados. */
        const val CURVA = "c"

        /** El texto de una arista curva: `c<desarrollo>|<cuerda>|<alto>|<puente>`. */
        fun textoDeCurva(c: CurvaEsquina): String =
            CURVA + listOf(c.desarrollo, c.cuerda, c.alto, c.puente).joinToString("|") { num(it) }


        /**
         * El texto con el que la medida viaja a la calculadora.
         *
         * `lado;lado;…@ángulo,ángulo,…` y cada lado `anchoAbajo,anchoArriba,altoIzq,altoDer,puente`.
         * Separadores elegidos para no chocar con los del contorno del vano, que viaja al lado.
         */
        fun aTexto(medida: EsquinaMedida): String {
            if (medida.lados.isEmpty()) return ""
            val lados = medida.lados.joinToString(";") { l ->
                // La panza va al final y solo si la hay: así el texto de una ventana de paredes
                // rectas sigue siendo el mismo de siempre.
                val numeros = listOf(l.anchoAbajo, l.anchoArriba, l.altoIzq, l.altoDer, l.puente) +
                    (if (l.esCurva) listOf(l.flecha) else emptyList())
                numeros.joinToString(",") { num(it) }
            }
            return lados + "@" + medida.angulos.joinToString(",")
        }

        fun desdeTexto(texto: String): EsquinaMedida? {
            val t = texto.trim()
            if (t.isBlank()) return null
            val lados = t.substringBefore("@").split(";").mapNotNull { trozo ->
                val n = trozo.split(",").map { it.trim().toFloatOrNull() ?: return@mapNotNull null }
                if (n.size < 5) null
                else LadoEsquina(n[0], n[1], n[2], n[3], n[4], n.getOrNull(5) ?: 0f)
            }
            if (lados.isEmpty()) return null
            val angulos = t.substringAfter("@", "").split(",")
                .map { it.trim() }.filter { it.isNotBlank() }
            return EsquinaMedida(lados, angulos)
        }

        private fun num(v: Float): String =
            if (kotlin.math.abs(v - v.toInt()) < 0.05f) v.toInt().toString()
            else String.format(java.util.Locale.US, "%.1f", v)
    }
}
