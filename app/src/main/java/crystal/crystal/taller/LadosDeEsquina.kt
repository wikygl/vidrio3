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
    val puente: Float
) {
    val ancho: Float get() = maxOf(anchoAbajo, anchoArriba)
    val alto: Float get() = maxOf(altoIzq, altoDer)

    /** true si los dos anchos, o los dos altos, no dan lo mismo: hay descuadre que mirar. */
    val descuadrado: Boolean
        get() = kotlin.math.abs(anchoAbajo - anchoArriba) > 0.15f ||
            kotlin.math.abs(altoIzq - altoDer) > 0.15f
}

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
    val hayCurva: Boolean get() = angulos.any { it == CURVA }

    /** Las aristas que no son de 90°, redondeadas, para poder avisar de ellas. */
    val anguloDistinto: List<Float>
        get() = angulos.mapNotNull { it.toFloatOrNull() }
            .filter { kotlin.math.abs(kotlin.math.abs(it) - 90f) > 0.5f }

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

        /**
         * El texto con el que la medida viaja a la calculadora.
         *
         * `lado;lado;…@ángulo,ángulo,…` y cada lado `anchoAbajo,anchoArriba,altoIzq,altoDer,puente`.
         * Separadores elegidos para no chocar con los del contorno del vano, que viaja al lado.
         */
        fun aTexto(medida: EsquinaMedida): String {
            if (medida.lados.isEmpty()) return ""
            val lados = medida.lados.joinToString(";") { l ->
                listOf(l.anchoAbajo, l.anchoArriba, l.altoIzq, l.altoDer, l.puente)
                    .joinToString(",") { num(it) }
            }
            return lados + "@" + medida.angulos.joinToString(",")
        }

        fun desdeTexto(texto: String): EsquinaMedida? {
            val t = texto.trim()
            if (t.isBlank()) return null
            val lados = t.substringBefore("@").split(";").mapNotNull { trozo ->
                val n = trozo.split(",").map { it.trim().toFloatOrNull() ?: return@mapNotNull null }
                if (n.size < 5) null
                else LadoEsquina(n[0], n[1], n[2], n[3], n[4])
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
