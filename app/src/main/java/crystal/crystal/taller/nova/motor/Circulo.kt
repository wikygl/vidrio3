package crystal.crystal.taller.nova.motor

import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * El vano circular (N-01, sección 11 de las reglas): el círculo inscrito en [ancho] × [alto], o la
 * elipse si no son iguales. Las alturas [y] se miden desde arriba, como en el resto del motor.
 *
 * Dictado para un círculo de 180 × 180: mocheta + puente es una **flecha**, y la cuerda trazada en
 * esa flecha es la medida del puente; las U de los bordes son arcos.
 */
class Circulo(val ancho: Float, val alto: Float) {
    private val a = ancho / 2.0
    private val b = alto / 2.0

    /** La cuerda horizontal a la altura [y] (desde arriba): 180 × 180 con flecha 25.7 → 126. */
    fun cuerda(y: Float): Float {
        val k = ((b - y) / b).coerceIn(-1.0, 1.0)
        return (2 * a * sqrt(1 - k * k)).toFloat()
    }

    /**
     * Lo más alto que llega el círculo entre [x0] y [x1] (desde la izquierda): el alto del rectángulo
     * que contiene a un módulo. En 180 × 180, el fijo de 0 a 60 llega a 169.7.
     */
    fun altoEntre(x0: Float, x1: Float): Float {
        val x = a.coerceIn(x0.toDouble(), x1.toDouble())
        val k = ((x - a) / a).coerceIn(-1.0, 1.0)
        return (2 * b * sqrt(1 - k * k)).toFloat()
    }

    /** Lo que queda fuera del círculo a cada lado de la cuerda a la altura [y]. */
    fun fueraEn(y: Float): Float = (ancho - cuerda(y)) / 2f

    /** El ángulo del punto del borde derecho a la altura [y] (0 en el centro, π/2 arriba). */
    private fun angulo(y: Float): Double = asin(((b - y) / b).coerceIn(-1.0, 1.0))

    /** El arco de arriba entre los dos extremos de la cuerda a la altura [y]. */
    fun arcoArriba(y: Float): Float { val t = angulo(y); return arco(t, PI - t) }

    /** El arco de abajo entre los dos extremos de la cuerda a la altura [y]. */
    fun arcoAbajo(y: Float): Float { val t = angulo(y); return arco(PI - t, 2 * PI + t) }

    /** El arco de un costado entre las alturas [arriba] y [abajo]. */
    fun arcoLado(arriba: Float, abajo: Float): Float = arco(angulo(abajo), angulo(arriba))

    /** La circunferencia (INA: la U es el perímetro, = 1). */
    fun perimetro(): Float = arco(0.0, 2 * PI)

    /** Largo del borde entre dos ángulos, x = a·cos t, y = b·sin t (Simpson; exacto en el círculo). */
    private fun arco(desde: Double, hasta: Double): Float {
        if (hasta <= desde) return 0f
        val n = 2000
        val h = (hasta - desde) / n
        fun f(t: Double) = sqrt(a * a * sin(t) * sin(t) + b * b * cos(t) * cos(t))
        var s = f(desde) + f(hasta)
        for (i in 1 until n) s += f(desde + i * h) * (if (i % 2 == 1) 4 else 2)
        return (s * h / 3).toFloat()
    }
}
