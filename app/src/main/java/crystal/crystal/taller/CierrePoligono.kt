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

    /**
     * Una cota a escuadra escrita: de la esquina [nodo] a la recta del lado [lado] (o de su
     * prolongación), a [distancia]. Es la medida de apoyo —el ancho total, el alto total, a qué
     * altura queda un corte— que ata la forma cuando los lados solos no la determinan.
     */
    data class Escuadra(val nodo: Int, val lado: Int, val distancia: Float)

    data class Resultado(
        val dirX: FloatArray,
        val dirY: FloatArray,
        val largo: FloatArray,
        val incoherentes: List<Incoherente>,
        /** Cotas a escuadra que no cuadran con lo demás; [Incoherente.lado] es su índice en la lista dada. */
        val escuadrasIncoherentes: List<Incoherente> = emptyList()
    )

    /** Más cerca que esto, un vector es cero. */
    private const val NADA = 0.01f

    /** ¿Ese lado corre por un eje? (el imán lo dejó horizontal o vertical) */
    fun esRecto(dx: Float, dy: Float): Boolean = abs(dx) > 0.999f || abs(dy) > 0.999f

    /**
     * Direcciones y largos que cierran el polígono, o null si no hay ningún inclinado (entonces
     * el cierre se reparte entre los rectos de cada eje, que es otro problema).
     *
     * Con [escuadras], las cotas de apoyo también cuentan: entonces todos los inclinados son
     * incógnita (ángulo y largo) y se ajustan por mínimos cuadrados para que la figura cierre y
     * las medidas escritas se cumplan; ver [ajustarConEscuadras].
     *
     * @param dirX,dirY dirección unitaria de cada lado tal como está dibujado.
     * @param largo largo de cada lado: el escrito si lo hay, el dibujado si no.
     * @param tolerancia diferencia de largo a partir de la cual un lado se señala como incoherente.
     * @param declarado qué largos son escritos (los demás son solo del dibujo); null = todos.
     */
    fun resolver(
        dirX: FloatArray,
        dirY: FloatArray,
        largo: FloatArray,
        tolerancia: Float,
        escuadras: List<Escuadra> = emptyList(),
        declarado: BooleanArray? = null
    ): Resultado? {
        val n = largo.size
        val inclinados = (0 until n).filter { !esRecto(dirX[it], dirY[it]) }
        if (inclinados.isEmpty()) return null
        if (escuadras.isNotEmpty()) {
            return ajustarConEscuadras(dirX, dirY, largo, inclinados, escuadras, tolerancia, declarado)
        }
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

    // ===================== Con cotas a escuadra: mínimos cuadrados =====================
    // Con los lados solos, tres o más inclinados no están determinados; las cotas de apoyo son
    // lo que los ata. Aquí todos los inclinados son incógnita (ángulo y largo) y se buscan los
    // valores que cumplen a la vez el cierre y las medidas escritas. Cada condición es un residuo
    // con su peso: el cierre pesa mucho (la figura tiene que cerrar sí o sí); las escuadras y
    // los largos escritos pesan lo mismo entre sí (son medidas); el largo dibujado de un lado sin
    // medida pesa poco, y el ángulo del boceto casi nada: solo decide cuando hay varias
    // soluciones, para que salga la parecida a lo que se dibujó. Es Levenberg–Marquardt con el
    // jacobiano por diferencias: son pocas incógnitas y sale en un suspiro.

    private const val PESO_CIERRE = 100.0
    private const val PESO_MEDIDA = 1.0
    private const val PESO_DIBUJADO = 0.1
    private const val PESO_ANGULO = 0.02

    private fun ajustarConEscuadras(
        dirX: FloatArray,
        dirY: FloatArray,
        largo: FloatArray,
        inclinados: List<Int>,
        escuadras: List<Escuadra>,
        tolerancia: Float,
        declarado: BooleanArray?
    ): Resultado {
        val n = largo.size
        val m = inclinados.size
        val indices = escuadras.indices.filter { escuadras[it].nodo in 0 until n && escuadras[it].lado in 0 until n }
        val validas = indices.map { escuadras[it] }
        val ang0 = DoubleArray(m) { Math.atan2(dirY[inclinados[it]].toDouble(), dirX[inclinados[it]].toDouble()) }
        val lar0 = DoubleArray(m) { largo[inclinados[it]].toDouble() }
        val esMedida = BooleanArray(m) { declarado?.get(inclinados[it]) ?: true }

        // Incógnitas: [ángulo_0..ángulo_m-1, largo_0..largo_m-1].
        fun vertices(x: DoubleArray): Array<DoubleArray> {
            val v = Array(n + 1) { DoubleArray(2) }
            var px = 0.0
            var py = 0.0
            var k = 0
            for (i in 0 until n) {
                v[i][0] = px; v[i][1] = py
                if (k < m && inclinados[k] == i) {
                    px += Math.cos(x[k]) * x[m + k]
                    py += Math.sin(x[k]) * x[m + k]
                    k++
                } else {
                    px += dirX[i] * largo[i]
                    py += dirY[i] * largo[i]
                }
            }
            v[n][0] = px; v[n][1] = py
            return v
        }
        fun distanciaEscuadra(v: Array<DoubleArray>, e: Escuadra): Double {
            val a = v[e.lado]
            val b = v[(e.lado + 1) % n]
            val p = v[e.nodo]
            val ex = b[0] - a[0]
            val ey = b[1] - a[1]
            val l = Math.hypot(ex, ey)
            if (l < NADA) return 0.0
            return abs(ex * (p[1] - a[1]) - ey * (p[0] - a[0])) / l
        }
        fun residuos(x: DoubleArray): DoubleArray {
            val v = vertices(x)
            val r = DoubleArray(2 + validas.size + 2 * m)
            r[0] = v[n][0] * PESO_CIERRE
            r[1] = v[n][1] * PESO_CIERRE
            validas.forEachIndexed { k, e -> r[2 + k] = (distanciaEscuadra(v, e) - e.distancia) * PESO_MEDIDA }
            for (k in 0 until m) {
                r[2 + validas.size + k] = (x[m + k] - lar0[k]) * (if (esMedida[k]) PESO_MEDIDA else PESO_DIBUJADO)
                r[2 + validas.size + m + k] = (x[k] - ang0[k]) * lar0[k] * PESO_ANGULO
            }
            return r
        }

        val x = DoubleArray(2 * m) { if (it < m) ang0[it] else lar0[it - m] }
        var r = residuos(x)
        var coste = r.sumOf { it * it }
        var lambda = 1e-3
        repeat(80) {
            // Jacobiano por diferencias centradas.
            val jac = Array(r.size) { DoubleArray(2 * m) }
            for (j in 0 until 2 * m) {
                val h = if (j < m) 1e-5 else 1e-3
                val xp = x.copyOf().also { it[j] += h }
                val xm = x.copyOf().also { it[j] -= h }
                val rp = residuos(xp)
                val rm = residuos(xm)
                for (i in r.indices) jac[i][j] = (rp[i] - rm[i]) / (2 * h)
            }
            val jtj = Array(2 * m) { DoubleArray(2 * m) }
            val jtr = DoubleArray(2 * m)
            for (i in r.indices) for (a in 0 until 2 * m) {
                jtr[a] += jac[i][a] * r[i]
                for (b in 0 until 2 * m) jtj[a][b] += jac[i][a] * jac[i][b]
            }
            var mejoro = false
            var intentos = 0
            while (!mejoro && intentos < 8) {
                val sistema = Array(2 * m) { a -> DoubleArray(2 * m) { b -> jtj[a][b] + (if (a == b) lambda * (jtj[a][a] + 1e-9) else 0.0) } }
                val delta = resolverLineal(sistema, DoubleArray(2 * m) { -jtr[it] })
                val xn = DoubleArray(2 * m) { x[it] + delta[it] }
                for (k in 0 until m) xn[m + k] = xn[m + k].coerceAtLeast(NADA.toDouble())
                val rn = residuos(xn)
                val costeN = rn.sumOf { it * it }
                if (costeN < coste) {
                    for (i in x.indices) x[i] = xn[i]
                    r = rn
                    val bajada = coste - costeN
                    coste = costeN
                    lambda = (lambda / 3).coerceAtLeast(1e-12)
                    mejoro = true
                    if (bajada < 1e-12) return@repeat
                } else {
                    lambda *= 10
                    intentos++
                }
            }
            if (!mejoro) return@repeat
        }

        val outX = dirX.copyOf()
        val outY = dirY.copyOf()
        val outL = largo.copyOf()
        val incoherentes = mutableListOf<Incoherente>()
        for (k in 0 until m) {
            val i = inclinados[k]
            outX[i] = Math.cos(x[k]).toFloat()
            outY[i] = Math.sin(x[k]).toFloat()
            outL[i] = x[m + k].toFloat()
            if (esMedida[k] && abs(outL[i] - largo[i]) > tolerancia) incoherentes.add(Incoherente(i, largo[i], outL[i]))
        }
        val v = vertices(x)
        val escuadrasMal = validas.mapIndexedNotNull { k, e ->
            val d = distanciaEscuadra(v, e).toFloat()
            if (abs(d - e.distancia) > tolerancia) Incoherente(indices[k], e.distancia, d) else null
        }
        return Resultado(outX, outY, outL, incoherentes, escuadrasMal)
    }

    /** Gauss con pivote parcial; el sistema es de dos incógnitas por inclinado, nada más. */
    private fun resolverLineal(a: Array<DoubleArray>, b: DoubleArray): DoubleArray {
        val n = b.size
        val m = Array(n) { a[it].copyOf() }
        val v = b.copyOf()
        for (c in 0 until n) {
            var p = c
            for (f in c + 1 until n) if (abs(m[f][c]) > abs(m[p][c])) p = f
            if (p != c) { val t = m[p]; m[p] = m[c]; m[c] = t; val tv = v[p]; v[p] = v[c]; v[c] = tv }
            val piv = m[c][c]
            if (abs(piv) < 1e-15) continue
            for (f in c + 1 until n) {
                val fct = m[f][c] / piv
                if (fct == 0.0) continue
                for (k in c until n) m[f][k] -= fct * m[c][k]
                v[f] -= fct * v[c]
            }
        }
        val x = DoubleArray(n)
        for (c in n - 1 downTo 0) {
            var s = v[c]
            for (k in c + 1 until n) s -= m[c][k] * x[k]
            x[c] = if (abs(m[c][c]) < 1e-15) 0.0 else s / m[c][c]
        }
        return x
    }
}
