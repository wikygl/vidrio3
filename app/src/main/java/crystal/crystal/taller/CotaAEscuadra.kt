package crystal.crystal.taller

import kotlin.math.hypot

/**
 * Una cota tomada A ESCUADRA: desde la esquina de un corte hasta el lado de enfrente.
 *
 * Es la medida que se toma cuando una forma tiene un corte —un rectángulo al que le falta un
 * trozo, el escalón de un alféizar que sube— y lo que hace falta saber no es el largo de un lado
 * sino a qué altura queda ese corte contra la pared de enfrente. Se mide a escuadra, que es como
 * se mide en obra: la cinta perpendicular al lado, no en diagonal.
 *
 * [nodo] es la esquina desde la que se mide y [ladoOpuesto] el lado al que llega la escuadra. Al
 * escribir otra medida se mueve LA ESQUINA, ella sola: los dos lados que salen de ella la siguen y
 * se quedan como queden. En obra es lo que pasa —una punta del corte está más arriba que la otra—,
 * y moviendo la arista entera no había manera de apuntar un corte torcido.
 */
data class MedidaAEscuadra(
    val nodo: Int,
    val ladoOpuesto: Int,
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
     * TODOS los lados a los que se llega a escuadra desde esa esquina, del más cerca al más lejos.
     *
     * Una esquina de corte casi nunca tiene un solo lado de enfrente: la del escalón de un alféizar
     * tiene el suelo debajo y el costado al lado, y las dos son medidas buenas. Cuál se quiere lo
     * dice el que mide, no el programa, así que aquí salen todas y el dibujo deja elegir.
     *
     * Vale un lado que no toca la esquina y al que la perpendicular llega DENTRO del lado, no por
     * su prolongación ni justo en una punta: en la punta, la escuadra vuelve por el canto de la
     * propia esquina y eso no es una medida. En un rectángulo, por eso, no hay ninguna.
     */
    fun candidatasDesdeNodo(contorno: List<Pair<Float, Float>>, nodo: Int): List<MedidaAEscuadra> {
        if (contorno.size < 3 || nodo !in contorno.indices) return emptyList()
        val n = contorno.size
        val p = contorno[nodo]
        val salen = mutableListOf<MedidaAEscuadra>()
        for (lado in contorno.indices) {
            // Los lados que tocan la esquina no cuentan: desde ellos no hay nada que medir.
            if (lado == nodo || siguiente(lado, n) == nodo) continue
            val a = contorno[lado]
            val b = contorno[siguiente(lado, n)]
            val pie = pieDePerpendicular(p, a, b) ?: continue
            val d = distancia(p, pie)
            if (d <= NADA) continue
            salen.add(MedidaAEscuadra(nodo = nodo, ladoOpuesto = lado, distanciaCm = d, pie = pie))
        }
        return salen.sortedBy { it.distanciaCm }
    }

    /**
     * La cota a escuadra que sale de esa esquina, o null si desde ahí no se alcanza ningún lado.
     *
     * De las que hay manda la más corta, que es la que uno mediría con la cinta si no dice otra
     * cosa. Para elegir a mano están [candidatasDesdeNodo] y [haciaDonde].
     */
    fun desdeNodo(contorno: List<Pair<Float, Float>>, nodo: Int): MedidaAEscuadra? =
        candidatasDesdeNodo(contorno, nodo).firstOrNull()

    /**
     * De las candidatas de esa esquina, la que cae hacia donde se arrastró el dedo.
     *
     * Se compara la dirección del arrastre con la dirección de cada cota —de la esquina a su pie—,
     * y gana la que apunta más parecido. Sin arrastre, o arrastrando casi nada, manda la más corta.
     */
    fun haciaDonde(
        contorno: List<Pair<Float, Float>>,
        nodo: Int,
        arrastre: Pair<Float, Float>,
        minimo: Float = 0f
    ): MedidaAEscuadra? {
        val salen = candidatasDesdeNodo(contorno, nodo)
        if (salen.isEmpty()) return null
        val largo = hypot(arrastre.first, arrastre.second)
        if (largo <= minimo || largo <= NADA) return salen.first()
        val u = (arrastre.first / largo) to (arrastre.second / largo)
        val p = contorno[nodo]
        return salen.maxByOrNull {
            val v = normalizar((it.pie.first - p.first) to (it.pie.second - p.second))
            u.first * v.first + u.second * v.second
        }
    }

    /**
     * Mueve LA ESQUINA hasta que la medida sea [nuevaCm], y devuelve el contorno nuevo.
     *
     * Se mueve solo ella, por la misma recta de la cota —a escuadra del lado de enfrente—, y los
     * dos lados que salen de esa esquina la siguen. Los demás vértices se quedan donde estaban, así
     * que el otro extremo del corte no se entera: un corte que en obra está más alto de un lado que
     * del otro se apunta tal como es. Empujando la arista entera, como se hacía antes, el corte
     * subía siempre a plomo y no había manera de apuntarlo torcido.
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
        // Hacia dónde empujar: por la recta de la cota, alejándose del lado de enfrente.
        val ux = (p.first - medida.pie.first) / largo
        val uy = (p.second - medida.pie.second) / largo
        val avance = nuevaCm - medida.distanciaCm
        return contorno.mapIndexed { i, punto ->
            if (i == medida.nodo) (punto.first + ux * avance) to (punto.second + uy * avance)
            else punto
        }
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

    private fun distancia(a: Pair<Float, Float>, b: Pair<Float, Float>) =
        hypot(b.first - a.first, b.second - a.second)

    private fun normalizar(v: Pair<Float, Float>): Pair<Float, Float> {
        val n = hypot(v.first, v.second).coerceAtLeast(NADA)
        return (v.first / n) to (v.second / n)
    }
}
