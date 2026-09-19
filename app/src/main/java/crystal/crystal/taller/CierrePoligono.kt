package crystal.crystal.taller

import kotlin.math.abs
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * Cierra un polígono con las medidas escritas, calculando la dirección de los lados inclinados.
 *
 * En el apunte los lados rectos (horizontales y verticales) tienen la dirección exacta porque el
 * imán los enderezó; los inclinados llevan el ángulo con que se trazaron a dedo, que nadie midió.
 * Si se recorre el contorno con esos ángulos y las medidas reales, el polígono no cierra y el
 * error acaba repartido entre los lados rectos, que eran los correctos. Aquí se invierte la
 * incógnita: las medidas mandan, los rectos no se tocan y lo que se calcula es hacia dónde van
 * los inclinados.
 *
 * - **Un inclinado**: la suma de los rectos dice el vector que tiene que cerrar; ahí queda su
 *   dirección y su largo. Si el largo escrito difiere del que cierra, ese lado es el incoherente.
 * - **Dos inclinados**: el vector de cierre y los dos largos dan dos soluciones (dos círculos);
 *   se elige la que más se parece al boceto. Si no alcanzan o sobran, se señalan los dos.
 * - **Tres o más**: no está determinado con solo los lados. Se conservan las inclinaciones del
 *   boceto en todos menos en los dos más largos, que cierran.
 *
 * Todo en píxeles del apunte, con la `y` hacia abajo como en el lienzo (aquí da igual).
 */
object CierrePoligono {

    /** Un lado cuya medida escrita no deja cerrar, con la que sí cerraría. */
    data class Incoherente(val lado: Int, val escrito: Float, val correcto: Float)

    data class Resultado(
        val dirX: FloatArray,
        val dirY: FloatArray,
        val largo: FloatArray,
        val incoherentes: List<Incoherente>
    )

    /** Más cerca que esto, un vector es cero. */
    private const val NADA = 0.01f

    /** ¿Ese lado corre por un eje? (el imán lo dejó horizontal o vertical) */
    fun esRecto(dx: Float, dy: Float): Boolean = abs(dx) > 0.999f || abs(dy) > 0.999f

    /**
     * Direcciones y largos que cierran el polígono, o null si no hay ningún inclinado (entonces
     * el cierre se reparte entre los rectos de cada eje, que es otro problema).
     *
     * @param dirX,dirY dirección unitaria de cada lado tal como está dibujado.
     * @param largo largo de cada lado: el escrito si lo hay, el dibujado si no.
     * @param tolerancia diferencia de largo a partir de la cual un lado se señala como incoherente.
     */
    fun resolver(dirX: FloatArray, dirY: FloatArray, largo: FloatArray, tolerancia: Float): Resultado? {
        val n = largo.size
        val inclinados = (0 until n).filter { !esRecto(dirX[it], dirY[it]) }
        if (inclinados.isEmpty()) return null
        val outX = dirX.copyOf()
        val outY = dirY.copyOf()
        val outL = largo.copyOf()
        val incoherentes = mutableListOf<Incoherente>()

        // Los que cierran: el único, o los dos más largos. El resto conserva su inclinación.
        val libres = inclinados.sortedByDescending { largo[it] }.take(2)
        var vx = 0f
        var vy = 0f
        for (i in 0 until n) if (i !in libres) {
            vx -= dirX[i] * largo[i]
            vy -= dirY[i] * largo[i]
        }
        val d = hypot(vx, vy)

        if (libres.size == 1) {
            val a = libres[0]
            if (d < NADA) return Resultado(outX, outY, outL, incoherentes)
            outX[a] = vx / d
            outY[a] = vy / d
            if (abs(largo[a] - d) > tolerancia) incoherentes.add(Incoherente(a, largo[a], d))
            outL[a] = d
            return Resultado(outX, outY, outL, incoherentes)
        }

        val a = libres[0]
        val b = libres[1]
        var la = largo[a]
        var lb = largo[b]
        when {
            // Los demás ya cierran solos: los dos van y vuelven por la misma recta, con la
            // inclinación del boceto (cualquiera vale) y el mismo largo.
            d < NADA -> {
                if (abs(la - lb) > tolerancia) {
                    val medio = (la + lb) / 2f
                    incoherentes.add(Incoherente(a, la, medio))
                    incoherentes.add(Incoherente(b, lb, medio))
                    la = medio; lb = medio
                }
                outX[b] = -dirX[a]; outY[b] = -dirY[a]
            }
            // No alcanzan: los dos estirados por la recta del cierre, a proporción.
            d > la + lb + tolerancia -> {
                val f = d / (la + lb)
                incoherentes.add(Incoherente(a, la, la * f))
                incoherentes.add(Incoherente(b, lb, lb * f))
                la *= f; lb *= f
                outX[a] = vx / d; outY[a] = vy / d
                outX[b] = vx / d; outY[b] = vy / d
            }
            // Sobran: uno de ellos vuelve sobre la recta del otro y el largo lo pone el cierre.
            d < abs(la - lb) - tolerancia -> {
                if (la > lb) {
                    incoherentes.add(Incoherente(a, la, lb + d)); la = lb + d
                    outX[a] = vx / d; outY[a] = vy / d
                    outX[b] = -vx / d; outY[b] = -vy / d
                } else {
                    incoherentes.add(Incoherente(b, lb, la + d)); lb = la + d
                    outX[b] = vx / d; outY[b] = vy / d
                    outX[a] = -vx / d; outY[a] = -vy / d
                }
            }
            else -> {
                // Dos círculos: |P| = la desde el origen, |P - V| = lb. Dos cortes, simétricos
                // respecto a la recta del cierre; el que más se parece al boceto es el bueno.
                val ux = vx / d
                val uy = vy / d
                val along = ((la * la - lb * lb + d * d) / (2f * d)).coerceIn(-la, la)
                val h = sqrt((la * la - along * along).coerceAtLeast(0f))
                var mejor = Float.NEGATIVE_INFINITY
                for (signo in floatArrayOf(1f, -1f)) {
                    val px = ux * along - uy * h * signo
                    val py = uy * along + ux * h * signo
                    val ax = px / la
                    val ay = py / la
                    val bx = (vx - px) / lb
                    val by = (vy - py) / lb
                    val parecido = ax * dirX[a] + ay * dirY[a] + bx * dirX[b] + by * dirY[b]
                    if (parecido > mejor) {
                        mejor = parecido
                        outX[a] = ax; outY[a] = ay
                        outX[b] = bx; outY[b] = by
                    }
                }
            }
        }
        outL[a] = la
        outL[b] = lb
        return Resultado(outX, outY, outL, incoherentes)
    }
}
