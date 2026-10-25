package crystal.crystal.taller

import crystal.crystal.Diseno.nova.ContornoEnTramos
import kotlin.math.abs

/**
 * Qué forma trae una medida de Medidas, dicha en palabras, para que cada calculadora la reconozca
 * (pedido del usuario, 2026-09-29: las demás calculadoras, "lo mismo que Nova", tienen que reconocer
 * las formas). Es la regla G-04 —la forma decide qué código se usa— del lado de la entrada: primero
 * se sabe qué es el vano; después cada calculadora dice qué hace con él.
 *
 * Con las reglas generales de docs/REGLAS_NOVA.md:
 * - un lado que se desvía hasta 2.5 es descuadre y el vano sigue siendo rectangular (N-06);
 * - cada escalón es un rectángulo propio (N-66);
 * - lo demás (lados inclinados, triángulos) es poligonal y se aproxima al rectángulo que lo
 *   contiene (N-68).
 *
 * Sin Android: se prueba en la PC (FormaDeLaMedidaTest).
 */
object FormaDeLaMedida {

    enum class Tipo { RECTANGULAR, DESCUADRE, ESCALONADO, POLIGONAL, CIRCULAR, ESQUINA, ARQUEADO }

    /**
     * Un arco del borde del vano (2026-10-21: un contorno de líneas y arcos unido en Medidas). No es
     * una pared curva —eso es la ventana curva, que dobla en la planta—: es el borde del vano visto de
     * frente, que en lugar de ser recto es un arco. [lado] es "izquierda", "derecha", "arriba" o
     * "abajo"; [haciaAfuera], si la panza sale del vano (lo normal) o entra en él.
     */
    data class ArcoDelVano(
        val lado: String, val cuerda: Float, val flecha: Float, val desarrollo: Float, val haciaAfuera: Boolean = true
    ) {
        /**
         * Cómo se llama en el taller: de medio punto (semicircular) si la flecha es media cuerda,
         * rebajado si es menos y de herradura si es más (pasa de media circunferencia).
         */
        val nombre: String get() {
            val mitad = cuerda / 2f
            return when {
                abs(flecha - mitad) <= maxOf(1f, cuerda * 0.02f) -> "de medio punto (semicircular)"
                flecha < mitad -> "rebajado"
                else -> "de herradura"
            }
        }

        val descripcion: String get() {
            val donde = when (lado) {
                "izquierda" -> "a la izquierda"
                "derecha" -> "a la derecha"
                else -> lado
            }
            val sentido = if (haciaAfuera) "" else ", entrando en el vano"
            return "arco $nombre $donde (cuerda ${num(cuerda)}, flecha ${num(flecha)}, desarrollo ${num(desarrollo)}$sentido)"
        }
    }

    fun arcosATexto(arcos: List<ArcoDelVano>): String = arcos.joinToString(";") {
        "${it.lado}:${num(it.cuerda)}:${num(it.flecha)}:${num(it.desarrollo)}:${if (it.haciaAfuera) 1 else 0}"
    }

    fun arcosDesdeTexto(texto: String): List<ArcoDelVano> = texto.split(";").mapNotNull { a ->
        val p = a.split(":")
        val cuerda = p.getOrNull(1)?.toFloatOrNull() ?: return@mapNotNull null
        val flecha = p.getOrNull(2)?.toFloatOrNull() ?: return@mapNotNull null
        val desarrollo = p.getOrNull(3)?.toFloatOrNull() ?: return@mapNotNull null
        ArcoDelVano(p[0], cuerda, flecha, desarrollo, p.getOrNull(4) != "0")
    }

    data class Forma(
        val tipo: Tipo,
        /** En palabras, con sus medidas: lo que se le muestra al vidriero. */
        val descripcion: String,
        /** Los rectángulos del vano escalonado, de izquierda a derecha (ancho × alto). */
        val escalones: List<Pair<Float, Float>> = emptyList()
    ) {
        /** El vano no es el rectángulo de su ancho por su alto. */
        val conForma: Boolean get() = tipo != Tipo.RECTANGULAR && tipo != Tipo.DESCUADRE
    }

    fun de(item: ColaCalculadoras.MedidaCalc): Forma {
        EsquinaMedida.desdeTexto(item.esquina)?.let { e ->
            val lados = e.lados.joinToString(" + ") { num(it.ancho) }
            val tipo = when (e.geometria) {
                "ncu" -> "curva"
                "nl" -> "en L"
                "nu" -> "en C"
                else -> "en serie"
            }
            val curvas = e.angulos.count { it.startsWith(EsquinaMedida.CURVA) }
            val conCurva = if (curvas > 0) ", $curvas esquina${if (curvas > 1) "s" else ""} en curva" else ""
            return Forma(Tipo.ESQUINA, "ventana $tipo: $lados$conCurva")
        }
        if (item.forma == "circular") return Forma(Tipo.CIRCULAR, "vano circular de ${num(item.ancho)} × ${num(item.alto)}")
        // El vano con un borde en arco (2026-10-21): se dice qué arco es, no "con lados inclinados".
        arcosDesdeTexto(item.arcos).takeIf { it.isNotEmpty() }?.let { arcos ->
            return Forma(Tipo.ARQUEADO, "arqueado de ${num(item.ancho)} × ${num(item.alto)}, " + arcos.joinToString(" y ") { it.descripcion })
        }
        val puntos = ContornoEnTramos.desdeTexto(item.contorno)
        if (puntos.size < 3) return Forma(Tipo.RECTANGULAR, "rectangular")
        return deContorno(puntos)
    }

    fun deContorno(puntos: List<Pair<Float, Float>>): Forma {
        val minX = puntos.minOf { it.first }; val maxX = puntos.maxOf { it.first }
        val minY = puntos.minOf { it.second }; val maxY = puntos.maxOf { it.second }
        // Cada vértice cerca de una esquina de la caja, y cada esquina con su vértice: el vano es su
        // rectángulo, con el descuadre de lo que más se aparta.
        val esquinas = listOf(minX to minY, maxX to minY, maxX to maxY, minX to maxY)
        fun distancia(p: Pair<Float, Float>, c: Pair<Float, Float>) = maxOf(abs(p.first - c.first), abs(p.second - c.second))
        val desvio = puntos.maxOf { p -> esquinas.minOf { c -> distancia(p, c) } }
        val esquinasDeLaCaja = esquinas.all { c -> puntos.any { p -> distancia(p, c) <= ContornoEnTramos.DESCUADRE_ESCALON } }
        if (desvio <= ContornoEnTramos.DESCUADRE_ESCALON && esquinasDeLaCaja) {
            return if (desvio < 0.3f) Forma(Tipo.RECTANGULAR, "rectangular")
                   else Forma(Tipo.DESCUADRE, "rectangular con descuadre de ${num(desvio)}")
        }
        val escalones = ContornoEnTramos.escalones(puntos, 0f)
        if (escalones != null && escalones.size > 1) {
            val lista = escalones.map { it.ancho to it.alto }
            return Forma(Tipo.ESCALONADO,
                "escalonado en ${lista.size} rectángulos: " + lista.joinToString(", ") { "${num(it.first)} × ${num(it.second)}" },
                lista)
        }
        // Un solo rectángulo que no es la caja: algo entra en él por un costado (una columna).
        if (escalones != null) return Forma(Tipo.POLIGONAL, "con un recorte en un costado (${puntos.size} lados)")
        return Forma(Tipo.POLIGONAL, "con lados inclinados (${puntos.size} lados)")
    }

    /**
     * El aviso de una calculadora que todavía no sabe calcular esa forma: qué es el vano y qué se
     * calcula en su lugar, para que nadie corte un rectángulo creyendo que es la forma.
     */
    fun aviso(forma: Forma, calculadora: String, ancho: Float, alto: Float): String =
        "La medida es ${forma.descripcion}.\n\n$calculadora todavía no calcula esa forma: calcula el " +
            "rectángulo que la contiene, ${num(ancho)} × ${num(alto)}.\n\nRevise el gráfico de la " +
            "medida antes de cortar."

    private fun num(v: Float): String =
        if (abs(v - Math.round(v)) < 0.05f) Math.round(v).toString()
        else String.format(java.util.Locale.US, "%.1f", v)
}
