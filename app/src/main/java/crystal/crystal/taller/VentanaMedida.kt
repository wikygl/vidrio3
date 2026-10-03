package crystal.crystal.taller

import kotlin.math.abs

/**
 * Un lado de la ventana medida: recto o curvo (2026-10-03).
 *
 * Una curva es un lado más, no una esquina: se mide igual que una pared, con su alto y su puente, y
 * en lugar del ancho lleva su desarrollo (lo que se corta) y su [flecha]. Así una L con la esquina
 * redondeada son tres lados (recto, curvo, recto) y la ventana que empieza o termina en curva no
 * necesita una pared de cero que le haga sitio.
 */
data class LadoDeVentana(
    /** Recto: su ancho. Curvo: su desarrollo. */
    val ancho: Float,
    val alto: Float,
    val puente: Float = 0f,
    /** 0 = las de la regla de los 60. */
    val divisiones: Int = 0,
    /** 0 = recto. Con signo: en más la panza va hacia afuera; en menos se mete hacia quien mira. */
    val flecha: Float = 0f,
    /** El descuadre: el ancho de arriba y el alto de la derecha, si no son los mismos. */
    val anchoArriba: Float = ancho,
    val altoDer: Float = alto
) {
    val esCurvo: Boolean get() = flecha != 0f

    /** La cuerda de la curva, de punta a punta; null en un lado recto. */
    val cuerda: Float? get() = if (esCurvo) ArcoEsquina.deDesarrolloYFlecha(ancho, abs(flecha))?.cuerda else null
}

/**
 * Lo que hay entre un lado y el siguiente.
 *
 * [grados] es el ángulo de la esquina, como siempre: 90 dobla en escuadra y 180 sigue de largo, que
 * es como entra una curva tangente. Entre una pared y una curva también puede haber ángulo: muchas
 * veces la curva no sale tangente (dictado por el usuario el 2026-10-03). Con signo: en menos dobla
 * hacia afuera.
 *
 * [parante]: si en la unión va parante. Entre dos curvas puede no ir, cuando se busca no cortar la
 * vista (templado).
 */
data class UnionDeLados(
    val grados: Float = 90f,
    val parante: Boolean = true
)

/**
 * La ventana medida como una lista de lados, rectos o curvos, con lo que hay entre cada dos
 * (2026-10-03). Reemplaza a las tres maneras que había de decir "aquí hay una curva" —el rótulo en
 * la arista con su banda, la pared curva y la curva en el sitio de una pared de cero—, que llevaban
 * por caminos distintos a la calculadora y se confundían.
 */
data class VentanaMedida(
    val lados: List<LadoDeVentana>,
    val uniones: List<UnionDeLados> = List((lados.size - 1).coerceAtLeast(0)) { UnionDeLados() }
) {
    init {
        require(uniones.size == (lados.size - 1).coerceAtLeast(0)) {
            "${lados.size} lados llevan ${lados.size - 1} uniones, no ${uniones.size}"
        }
    }

    val hayCurva: Boolean get() = lados.any { it.esCurvo }

    /** Los lados rectos: las paredes. */
    val paredes: List<LadoDeVentana> get() = lados.filter { !it.esCurvo }

    companion object {
        /** Marca del formato de texto, para no confundirlo con el de [EsquinaMedida]. */
        const val VERSION = "v2"

        /** Una unión que sigue de largo: la curva tangente a la pared. */
        val TANGENTE = UnionDeLados(180f)

        /**
         * La medida en el formato de antes ([EsquinaMedida]): paredes, y la curva escrita en la
         * arista entre dos de ellas (`c<desarrollo>|<cuerda>|<alto>|<puente>`), o detrás de la última
         * (la curva del final, 2026-10-03). Cada curva de arista pasa a ser un lado curvo entre dos
         * uniones que siguen de largo: es como se medían, siempre tangentes.
         */
        fun desdeEsquina(m: EsquinaMedida): VentanaMedida {
            val lados = mutableListOf<LadoDeVentana>()
            val uniones = mutableListOf<UnionDeLados>()
            fun agregar(lado: LadoDeVentana, union: UnionDeLados) {
                if (lados.isNotEmpty()) uniones.add(union)
                lados.add(lado)
            }
            m.lados.forEachIndexed { i, l ->
                val antes = if (i == 0) null else m.angulos.getOrNull(i - 1)
                val union = when {
                    // Antes, toda curva entraba y salía tangente: detrás de una curva de arista, y
                    // junto a una pared curva (el ángulo escrito ahí era el giro del arco, no una esquina).
                    i > 0 && (m.curvaDe(i - 1) != null || l.esCurva || m.lados[i - 1].esCurva) -> TANGENTE
                    else -> UnionDeLados(antes?.toFloatOrNull() ?: 90f)
                }
                agregar(
                    LadoDeVentana(
                        ancho = l.anchoAbajo, alto = l.altoIzq, puente = l.puente, flecha = l.flecha,
                        anchoArriba = l.anchoArriba, altoDer = l.altoDer
                    ),
                    union
                )
                // La curva de la arista que sigue (o la del final, detrás del último lado).
                m.curvaDe(i)?.let { c -> agregar(ladoDeCurva(c, l), TANGENTE) }
            }
            return VentanaMedida(lados, uniones)
        }

        /** La curva escrita en una arista, como un lado curvo. Sin alto ni puente, los de la pared de antes. */
        private fun ladoDeCurva(c: CurvaEsquina, antes: LadoEsquina): LadoDeVentana {
            val flecha = ArcoEsquina.deDesarrolloYCuerda(c.desarrollo, c.cuerda)?.flecha ?: 0f
            val alto = if (c.alto > 1f) c.alto else antes.alto
            return LadoDeVentana(
                ancho = c.desarrollo, alto = alto,
                puente = if (c.puente > 0.5f) c.puente else antes.puente,
                divisiones = c.divisiones,
                // Sin flecha que leer (una cuerda igual al desarrollo) no sería curva: se marca mínima.
                flecha = if (flecha > 0f) flecha else 0.1f
            )
        }

        /**
         * El texto con el que la medida viaja: `v2;lado/lado/…;union/union/…`. Cada lado
         * `ancho,alto,puente,divisiones,flecha,anchoArriba,altoDer`; cada unión sus grados, con una
         * `s` detrás si va sin parante.
         */
        fun aTexto(v: VentanaMedida): String =
            VERSION + ";" +
                v.lados.joinToString("/") { l ->
                    listOf(l.ancho, l.alto, l.puente, l.divisiones.toFloat(), l.flecha, l.anchoArriba, l.altoDer)
                        .joinToString(",") { num(it) }
                } + ";" +
                v.uniones.joinToString("/") { u -> num(u.grados) + (if (u.parante) "" else "s") }

        /** Null si el texto no es de este formato o no se puede leer. */
        fun desdeTexto(texto: String): VentanaMedida? {
            val partes = texto.trim().split(";")
            if (partes.size != 3 || partes[0] != VERSION || partes[1].isBlank()) return null
            val lados = partes[1].split("/").map { t ->
                val n = t.split(",").map { it.trim().toFloatOrNull() ?: return null }
                if (n.size < 2) return null
                LadoDeVentana(
                    ancho = n[0], alto = n[1], puente = n.getOrElse(2) { 0f }, divisiones = n.getOrElse(3) { 0f }.toInt(),
                    flecha = n.getOrElse(4) { 0f }, anchoArriba = n.getOrElse(5) { n[0] }, altoDer = n.getOrElse(6) { n[1] }
                )
            }
            val uniones = partes[2].split("/").filter { it.isNotBlank() }.map { t ->
                val sinParante = t.trim().endsWith("s")
                UnionDeLados(t.trim().removeSuffix("s").toFloatOrNull() ?: return null, parante = !sinParante)
            }
            if (uniones.size != lados.size - 1) return null
            return VentanaMedida(lados, uniones)
        }

        /** La medida en cualquiera de los dos formatos: el nuevo, o el de [EsquinaMedida]. */
        fun leer(texto: String): VentanaMedida? =
            desdeTexto(texto) ?: EsquinaMedida.desdeTexto(texto)?.let { desdeEsquina(it) }

        private fun num(v: Float): String =
            if (v == v.toLong().toFloat()) v.toLong().toString() else "%.2f".format(java.util.Locale.US, v).trimEnd('0').trimEnd('.')
    }
}
