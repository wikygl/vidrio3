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
 * [nodo] es la esquina desde la que se mide y [ladoOpuesto] el lado al que llega la escuadra. Al
 * escribir otra medida se mueve LA ESQUINA, ella sola: los dos lados que salen de ella la siguen y
 * se quedan como queden. En obra es lo que pasa —una punta del corte está más arriba que la otra—,
 * y moviendo la arista entera no había manera de apuntar un corte torcido.
 */
data class MedidaAEscuadra(
    val nodo: Int,
    val ladoOpuesto: Int,
    val distanciaCm: Float,
    val pie: Pair<Float, Float>,
    /**
     * El pie cayó FUERA del lado, sobre su prolongación: el lado no llega hasta la escuadra. Para
     * que la cota tenga dónde apoyarse se dibuja una sombra del lado desde [desde] hasta el pie.
     */
    val prolongado: Boolean = false,
    /** La punta del lado desde la que sale la sombra, cuando [prolongado]. */
    val desde: Pair<Float, Float>? = null
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
    fun candidatasDesdeNodo(
        contorno: List<Pair<Float, Float>>,
        nodo: Int,
        conProlongacion: Boolean = false,
        bordesExtra: List<Pair<Pair<Float, Float>, Pair<Float, Float>>> = emptyList()
    ): List<MedidaAEscuadra> {
        if (contorno.size < 3 || nodo !in contorno.indices) return emptyList()
        val n = contorno.size
        val p = contorno[nodo]
        val salen = mutableListOf<MedidaAEscuadra>()
        fun probar(lado: Int, a: Pair<Float, Float>, b: Pair<Float, Float>) {
            val (pie, t) = pieDePerpendicular(p, a, b, conProlongacion) ?: return
            val d = distancia(p, pie)
            if (d <= NADA) return
            val fuera = t < 0.01f || t > 0.99f
            salen.add(
                MedidaAEscuadra(
                    nodo = nodo, ladoOpuesto = lado, distanciaCm = d, pie = pie,
                    prolongado = fuera,
                    desde = if (!fuera) null else if (t < 0.01f) a else b
                )
            )
        }
        for (lado in contorno.indices) {
            // Los lados que tocan la esquina no cuentan: desde ellos no hay nada que medir.
            if (lado == nodo || siguiente(lado, n) == nodo) continue
            probar(lado, contorno[lado], contorno[siguiente(lado, n)])
        }
        // Lo suelto que haya alrededor —una línea, otra figura— también se mide: van con índice
        // negativo, que no es lado de la forma.
        bordesExtra.forEachIndexed { k, (a, b) -> probar(-1 - k, a, b) }
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
     * Sin arrastre, o arrastrando casi nada, manda la más corta. Con arrastre cuentan solo las que
     * salen hacia donde va el dedo, y entre ellas:
     * - [porRecorrido] (poniendo la cota): el dedo va pasando líneas. Gana la primera que el dedo
     *   NO ha pasado todavía —la que tiene más cerca por delante—; pasadas todas, la última. Así
     *   se elige entre varias líneas en la misma dirección estirando más o menos.
     * - Sin [porRecorrido] (volviendo a medir una cota puesta): la que apunta igual y mide lo más
     *   parecido a lo que la cota ya medía, para que la cota no se salte sola a otra línea.
     */
    fun haciaDonde(
        contorno: List<Pair<Float, Float>>,
        nodo: Int,
        arrastre: Pair<Float, Float>,
        minimo: Float = 0f,
        conProlongacion: Boolean = false,
        bordesExtra: List<Pair<Pair<Float, Float>, Pair<Float, Float>>> = emptyList(),
        porRecorrido: Boolean = false
    ): MedidaAEscuadra? {
        val salen = candidatasDesdeNodo(contorno, nodo, conProlongacion, bordesExtra)
        if (salen.isEmpty()) return null
        val largo = hypot(arrastre.first, arrastre.second)
        if (largo <= minimo || largo <= NADA) return salen.first()
        val u = (arrastre.first / largo) to (arrastre.second / largo)
        val p = contorno[nodo]
        fun rumbo(m: MedidaAEscuadra): Float {
            val v = normalizar((m.pie.first - p.first) to (m.pie.second - p.second))
            return u.first * v.first + u.second * v.second
        }
        val mejorRumbo = salen.maxOf { rumbo(it) }
        // Las que van hacia donde va el dedo (a menos de unos 25°); si ninguna, la que más se acerca.
        val enRumbo = salen.filter { rumbo(it) >= 0.9f }.ifEmpty { salen.filter { rumbo(it) >= mejorRumbo - 1e-4f } }
        return if (porRecorrido) {
            enRumbo.firstOrNull { it.distanciaCm >= largo } ?: enRumbo.last()
        } else {
            enRumbo.minByOrNull { abs(it.distanciaCm - largo) }
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

    /**
     * Dónde cae la perpendicular desde [p] al lado a→b, con la fracción `t` del lado en que cae.
     * Null si cae fuera del lado, salvo [conProlongacion], que la admite sobre su prolongación:
     * es la cota al lado que no llega, la que necesita la sombra.
     */
    private fun pieDePerpendicular(
        p: Pair<Float, Float>,
        a: Pair<Float, Float>,
        b: Pair<Float, Float>,
        conProlongacion: Boolean = false
    ): Pair<Pair<Float, Float>, Float>? {
        val vx = b.first - a.first
        val vy = b.second - a.second
        val largo2 = vx * vx + vy * vy
        if (largo2 <= NADA) return null
        val t = ((p.first - a.first) * vx + (p.second - a.second) * vy) / largo2
        // Tiene que caer DENTRO del lado, y no en una de sus puntas: en la punta, la escuadra
        // vuelve por el canto de la que ya es la propia esquina, y eso no es una medida.
        if (!conProlongacion && (t < 0.01f || t > 0.99f)) return null
        return ((a.first + vx * t) to (a.second + vy * t)) to t
    }

    private fun siguiente(i: Int, n: Int) = (i + 1) % n

    private fun distancia(a: Pair<Float, Float>, b: Pair<Float, Float>) =
        hypot(b.first - a.first, b.second - a.second)

    private fun normalizar(v: Pair<Float, Float>): Pair<Float, Float> {
        val n = hypot(v.first, v.second).coerceAtLeast(NADA)
        return (v.first / n) to (v.second / n)
    }
}
