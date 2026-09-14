package crystal.crystal.taller

import kotlin.math.abs
import kotlin.math.hypot

/**
 * Una cota tomada A ESCUADRA: desde la esquina de un corte hasta el lado de enfrente.
 *
 * Es la medida que se toma cuando una forma tiene un corte —un rectángulo al que le falta un
 * trozo, el escalón de un alféizar que sube— y lo que hace falta saber no es el largo de un lado
 * sino a qué altura queda ese corte contra la pared de enfrente. Se mide a escuadra, que es como
 * se mide en obra: la cinta perpendicular al lado, no en diagonal.
 *
 * [nodo] es la esquina desde la que se mide; [ladoOpuesto] el lado al que llega la escuadra, y
 * [aristaQueEmpuja] el lado que se mueve al escribir otra medida —el que va paralelo al de
 * enfrente, porque es el que de verdad decide esa distancia—.
 */
data class MedidaAEscuadra(
    val nodo: Int,
    val ladoOpuesto: Int,
    val aristaQueEmpuja: Int,
    val distanciaCm: Float,
    val pie: Pair<Float, Float>
)

object CotaAEscuadra {

    /** Más cerca que esto, dos puntos son el mismo. */
    private const val NADA = 0.01f

    /**
     * La esquina del contorno más cercana a [punto], si hay alguna dentro de [radio].
     *
     * Es el imán: el dedo no acierta un vértice, así que se elige el que tenga más cerca.
     */
    fun nodoMasCerca(
        contorno: List<Pair<Float, Float>>,
        punto: Pair<Float, Float>,
        radio: Float
    ): Int? {
        if (contorno.isEmpty()) return null
        val cual = contorno.indices.minByOrNull { distancia(contorno[it], punto) } ?: return null
        return if (distancia(contorno[cual], punto) <= radio) cual else null
    }

    /**
     * La cota a escuadra que sale de esa esquina, o null si desde ahí no se alcanza ningún lado.
     *
     * El lado de enfrente es el más cercano de los que se pueden alcanzar A ESCUADRA: los que no
     * tocan la esquina y a los que la perpendicular llega dentro del lado, no por su prolongación.
     * De los que quedan manda el más cercano, que es el que uno mediría con la cinta.
     */
    fun desdeNodo(contorno: List<Pair<Float, Float>>, nodo: Int): MedidaAEscuadra? {
        if (contorno.size < 3 || nodo !in contorno.indices) return null
        val n = contorno.size
        val p = contorno[nodo]
        val aristas = listOf(anterior(nodo, n), nodo)   // las dos que salen de la esquina
        var mejor: MedidaAEscuadra? = null
        for (lado in contorno.indices) {
            // Los lados que tocan la esquina no cuentan: desde ellos no hay nada que medir.
            if (lado == nodo || siguiente(lado, n) == nodo) continue
            val a = contorno[lado]
            val b = contorno[siguiente(lado, n)]
            val pie = pieDePerpendicular(p, a, b) ?: continue
            val d = distancia(p, pie)
            if (d <= NADA) continue
            // Solo vale el lado al que se le puede EMPUJAR una de las dos aristas de la esquina,
            // o sea uno que vaya paralelo a ella. Contra un lado que no es paralelo a ninguna, la
            // escuadra mediría algo que no se puede cambiar sin torcer la forma.
            val arista = aristas.maxByOrNull { paralelismo(contorno, it, a, b) } ?: continue
            if (paralelismo(contorno, arista, a, b) < 0.99f) continue
            if (mejor == null || d < mejor.distanciaCm) {
                mejor = MedidaAEscuadra(
                    nodo = nodo,
                    ladoOpuesto = lado,
                    aristaQueEmpuja = arista,
                    distanciaCm = d,
                    pie = pie
                )
            }
        }
        return mejor
    }

    /**
     * Mueve la arista de la cota hasta que la medida sea [nuevaCm], y devuelve el contorno nuevo.
     *
     * La arista se empuja ENTERA y a escuadra del lado de enfrente —hacia arriba, hacia abajo o de
     * lado, según cómo caiga—, que es lo que pasa en la realidad: el corte sube o baja, no se
     * tuerce. Los demás vértices se quedan donde estaban.
     */
    fun conDistancia(
        contorno: List<Pair<Float, Float>>,
        medida: MedidaAEscuadra,
        nuevaCm: Float
    ): List<Pair<Float, Float>> {
        if (contorno.size < 3 || nuevaCm <= 0f) return contorno
        val p = contorno.getOrNull(medida.nodo) ?: return contorno
        val largo = distancia(p, medida.pie)
        if (largo <= NADA) return contorno
        // Hacia dónde empujar: por la misma recta de la cota, alejándose del lado de enfrente.
        val ux = (p.first - medida.pie.first) / largo
        val uy = (p.second - medida.pie.second) / largo
        val avance = nuevaCm - medida.distanciaCm
        val cuales = setOf(
            medida.aristaQueEmpuja,
            siguiente(medida.aristaQueEmpuja, contorno.size)
        )
        return contorno.mapIndexed { i, punto ->
            if (i in cuales) (punto.first + ux * avance) to (punto.second + uy * avance)
            else punto
        }
    }

    /** Cuánto se parecen en dirección la arista [lado] y el lado a→b: 1 es paralelo del todo. */
    private fun paralelismo(
        contorno: List<Pair<Float, Float>>,
        lado: Int,
        a: Pair<Float, Float>,
        b: Pair<Float, Float>
    ): Float {
        val n = contorno.size
        val d = normalizar(
            (contorno[siguiente(lado, n)].first - contorno[lado].first) to
                (contorno[siguiente(lado, n)].second - contorno[lado].second)
        )
        val o = normalizar((b.first - a.first) to (b.second - a.second))
        return abs(d.first * o.first + d.second * o.second)
    }

    /** Dónde cae la perpendicular desde [p] al lado a→b; null si cae fuera del lado. */
    private fun pieDePerpendicular(
        p: Pair<Float, Float>,
        a: Pair<Float, Float>,
        b: Pair<Float, Float>
    ): Pair<Float, Float>? {
        val vx = b.first - a.first
        val vy = b.second - a.second
        val largo2 = vx * vx + vy * vy
        if (largo2 <= NADA) return null
        val t = ((p.first - a.first) * vx + (p.second - a.second) * vy) / largo2
        // Tiene que caer DENTRO del lado, y no en una de sus puntas: en la punta, la escuadra
        // vuelve por el canto de la que ya es la propia esquina, y eso no es una medida.
        if (t < 0.01f || t > 0.99f) return null
        return (a.first + vx * t) to (a.second + vy * t)
    }

    private fun siguiente(i: Int, n: Int) = (i + 1) % n
    private fun anterior(i: Int, n: Int) = (i - 1 + n) % n

    private fun distancia(a: Pair<Float, Float>, b: Pair<Float, Float>) =
        hypot(b.first - a.first, b.second - a.second)

    private fun normalizar(v: Pair<Float, Float>): Pair<Float, Float> {
        val n = hypot(v.first, v.second).coerceAtLeast(NADA)
        return (v.first / n) to (v.second / n)
    }
}
