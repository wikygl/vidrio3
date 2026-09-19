package crystal.crystal.taller

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Un arco puesto en el apunte por su cuerda —de [ax],[ay] a [bx],[by]— y su [flecha].
 *
 * La flecha lleva signo: en más, la panza cae a la izquierda de quien va de A a B (con la `y`
 * hacia abajo, como en el lienzo: yendo hacia la derecha, hacia ARRIBA del papel); en menos, a la
 * derecha. En la planta, arriba del papel es lejos de quien mira, así que un arco de izquierda a
 * derecha con flecha positiva es la panza hacia afuera —la ventana curva de siempre— y con
 * negativa la curva que se mete hacia el que mira.
 *
 * El signo de la flecha es también el del giro: con la panza a la izquierda, la pared va
 * doblando hacia la derecha (rumbo creciente), que es lo que [crystal.crystal.Diseno.nova.PlantaDelDiseno]
 * entiende por curva positiva. Todo en píxeles del apunte; la cuenta de arcos vive en [ArcoEsquina].
 */
data class ArcoDibujado(val ax: Float, val ay: Float, val bx: Float, val by: Float, val flecha: Float) {

    val cuerda: Float get() = hypot(bx - ax, by - ay)

    /** Rumbo de la cuerda, en radianes, con la `y` hacia abajo. */
    val rumboCuerda: Double get() = atan2((by - ay).toDouble(), (bx - ax).toDouble())

    /** El arco con sus tres medidas; null si es una recta (sin flecha o sin cuerda). */
    val arco: ArcoEsquina? get() = ArcoEsquina.deCuerdaYFlecha(cuerda, abs(flecha))

    /** Cuánto dobla de punta a punta, con el signo del giro; 0 si es recta. */
    val giroGrados: Float get() = (arco?.anguloGrados ?: 0f) * signo

    val signo: Float get() = if (flecha < 0f) -1f else 1f

    /** Rumbo con el que entra la pared en A y con el que sale en B, en radianes. */
    val rumboEntrada: Double get() = rumboCuerda - Math.toRadians(giroGrados / 2.0)
    val rumboSalida: Double get() = rumboCuerda + Math.toRadians(giroGrados / 2.0)

    /** La normal hacia la panza: la izquierda de A→B, con la `y` hacia abajo. */
    private fun normal(): Pair<Float, Float> {
        val c = cuerda
        if (c < 1e-6f) return 0f to -1f
        return ((by - ay) / c) to (-(bx - ax) / c)
    }

    /** El punto más salido de la panza. */
    fun apice(): Pair<Float, Float> {
        val (nx, ny) = normal()
        return ((ax + bx) / 2f + nx * flecha) to ((ay + by) / 2f + ny * flecha)
    }

    /**
     * Los puntos del arco de A a B, [trozos] cuerdas iguales. Si es recta, solo A y B.
     */
    fun puntos(trozos: Int = 24): List<Pair<Float, Float>> {
        val a = arco ?: return listOf(ax to ay, bx to by)
        val n = trozos.coerceAtLeast(1)
        val giro = Math.toRadians(giroGrados.toDouble())
        val paso = giro / n
        // Cada trocito es una cuerda del mismo radio: 2·R·sen(paso/2), avanzando por el rumbo
        // de entrada más medio paso cada vez.
        val cuerdita = 2.0 * a.radio * sin(abs(paso) / 2.0)
        var rumbo = rumboEntrada
        var x = ax.toDouble()
        var y = ay.toDouble()
        val salen = mutableListOf(ax to ay)
        repeat(n) {
            val dir = rumbo + paso / 2.0
            x += cos(dir) * cuerdita
            y += sin(dir) * cuerdita
            salen.add(x.toFloat() to y.toFloat())
            rumbo += paso
        }
        // El cierre exacto en B, que el redondeo no lo deje a un pelo.
        salen[salen.lastIndex] = bx to by
        return salen
    }

    /** Lo lejos que está un punto del arco: la menor distancia a sus trocitos. */
    fun distanciaA(x: Float, y: Float, trozos: Int = 24): Float {
        val p = puntos(trozos)
        var mejor = Float.MAX_VALUE
        for (i in 0 until p.size - 1) {
            val (x1, y1) = p[i]
            val (x2, y2) = p[i + 1]
            val dx = x2 - x1
            val dy = y2 - y1
            val l2 = (dx * dx + dy * dy).coerceAtLeast(1e-6f)
            val t = (((x - x1) * dx + (y - y1) * dy) / l2).coerceIn(0f, 1f)
            val d = hypot(x - (x1 + dx * t), y - (y1 + dy * t))
            if (d < mejor) mejor = d
        }
        return mejor
    }

    /** El mismo arco recorrido al revés: la panza queda al otro lado de la marcha. */
    fun invertido(): ArcoDibujado = ArcoDibujado(bx, by, ax, ay, -flecha)
}
