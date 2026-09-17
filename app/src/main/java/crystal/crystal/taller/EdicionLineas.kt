package crystal.crystal.taller

import kotlin.math.abs
import kotlin.math.hypot

/**
 * Extender y recortar líneas contra lo que ya hay dibujado, como en AutoCAD.
 *
 * Todo en píxeles del apunte y con pares (x, y), como [CotaAEscuadra]: la vista solo elige la
 * línea tocada y junta los bordes contra los que se corta —los lados de todas las demás figuras—,
 * y aquí se hace la geometría.
 *
 * - **Extender**: el extremo que se tocó se alarga por su misma recta hasta el primer borde que
 *   encuentra. Si en esa dirección no hay ninguno, no se toca.
 * - **Recortar**: se quita el trozo de línea que se tocó, entre los dos cruces con otros bordes
 *   que lo encierran. Si solo hay cruce por un lado, se quita desde ahí hasta la punta. Sin
 *   cruces, no hay nada que recortar.
 */
object EdicionLineas {

    /** Más cerca que esto, dos cruces son el mismo. */
    private const val NADA = 0.01f

    /**
     * Dónde corta la recta `p + t·d` al segmento a→b, como `t` en las unidades de [d] (que tiene
     * que venir unitario). Null si son paralelos o si el cruce cae fuera del segmento.
     */
    fun corteConSegmento(
        p: Pair<Float, Float>,
        d: Pair<Float, Float>,
        a: Pair<Float, Float>,
        b: Pair<Float, Float>
    ): Float? {
        val ex = b.first - a.first
        val ey = b.second - a.second
        val denominador = d.first * ey - d.second * ex
        if (abs(denominador) < 1e-6f) return null
        val wx = a.first - p.first
        val wy = a.second - p.second
        val t = (wx * ey - wy * ex) / denominador
        val u = (wx * d.second - wy * d.first) / denominador
        if (u < -1e-4f || u > 1f + 1e-4f) return null
        return t
    }

    /**
     * El punto nuevo del extremo [fin] de la línea inicio→fin al extenderlo hasta el borde más
     * cercano en su dirección, o null si por ahí no hay ningún borde.
     *
     * Para extender el otro extremo se llama con los puntos al revés.
     */
    fun extender(
        inicio: Pair<Float, Float>,
        fin: Pair<Float, Float>,
        bordes: List<Pair<Pair<Float, Float>, Pair<Float, Float>>>
    ): Pair<Float, Float>? {
        val largo = hypot(fin.first - inicio.first, fin.second - inicio.second)
        if (largo <= NADA) return null
        val d = ((fin.first - inicio.first) / largo) to ((fin.second - inicio.second) / largo)
        var mejor: Float? = null
        for ((a, b) in bordes) {
            val t = corteConSegmento(fin, d, a, b) ?: continue
            // Solo hacia delante, y no el borde que ya toca la punta.
            if (t <= NADA) continue
            if (mejor == null || t < mejor) mejor = t
        }
        val t = mejor ?: return null
        return (fin.first + d.first * t) to (fin.second + d.second * t)
    }

    /**
     * Lo que queda de la línea inicio→fin al quitar el trozo tocado en [toque] (0 = inicio,
     * 1 = fin, como fracción del largo). Devuelve los tramos que quedan —dos si el trozo estaba en
     * medio, uno si estaba en una punta— o null si la línea no cruza con nada y no hay qué quitar.
     */
    fun recortar(
        inicio: Pair<Float, Float>,
        fin: Pair<Float, Float>,
        toque: Float,
        bordes: List<Pair<Pair<Float, Float>, Pair<Float, Float>>>
    ): List<Pair<Pair<Float, Float>, Pair<Float, Float>>>? {
        val largo = hypot(fin.first - inicio.first, fin.second - inicio.second)
        if (largo <= NADA) return null
        val d = ((fin.first - inicio.first) / largo) to ((fin.second - inicio.second) / largo)
        // Los cruces, como fracción del largo, sin contar las propias puntas.
        val cruces = bordes.mapNotNull { (a, b) ->
            corteConSegmento(inicio, d, a, b)?.let { it / largo }
        }.filter { it > NADA / largo && it < 1f - NADA / largo }
        val antes = cruces.filter { it <= toque }.maxOrNull()
        val despues = cruces.filter { it >= toque }.minOrNull()
        if (antes == null && despues == null) return null
        fun en(t: Float) = (inicio.first + d.first * largo * t) to (inicio.second + d.second * largo * t)
        val quedan = mutableListOf<Pair<Pair<Float, Float>, Pair<Float, Float>>>()
        if (antes != null) quedan.add(inicio to en(antes))
        if (despues != null) quedan.add(en(despues) to fin)
        return quedan
    }

    /** El candidato más cercano a [punto] dentro de [radio], o null si ninguno llega. */
    fun imantar(
        punto: Pair<Float, Float>,
        candidatos: List<Pair<Float, Float>>,
        radio: Float
    ): Pair<Float, Float>? {
        val mejor = candidatos.minByOrNull { hypot(it.first - punto.first, it.second - punto.second) }
            ?: return null
        return if (hypot(mejor.first - punto.first, mejor.second - punto.second) <= radio) mejor else null
    }
}
