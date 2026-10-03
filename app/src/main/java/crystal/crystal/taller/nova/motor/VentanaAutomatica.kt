package crystal.crystal.taller.nova.motor

/**
 * Arma la [VentanaNova] de Nova con ceil a partir de lo que se escribe en la pantalla: los lados
 * medidos, cómo terminan y los perfiles. Aquí viven los descuentos de aparente que antes estaban
 * repartidos en la pantalla (N-37, N-38, N-78):
 *
 * - Lado lateral libre (APA): el ancho pierde el perfil que lo cierra.
 * - Lado arriba o abajo libre (APA): el alto pierde el falso puente.
 * - Esquina en ángulo (APA): cada lado pierde su parte del esquinero, con reparto invertido.
 *
 * Inaparente no descuenta nada en el vidrio ni en sus U; su esquina suma el ancho de la U, y eso lo
 * hace el motor. Su estructura sí pierde el parante de esquina (N-75): el puente, el riel y la U
 * felpero llegan hasta el parante, y el vidrio pasa por delante.
 *
 * Una esquina resuelta con una curva (N-84, G-09) no descuenta ni aumenta: la curva es otro trozo de
 * la ventana, con su desarrollo, entre dos parantes.
 */
object VentanaAutomatica {

    data class LadoMedido(val ancho: Float, val alto: Float, val hoja: Float, val divisManual: Int = 0)

    data class Entrada(
        val acabado: Acabado,
        val modelo: ModeloMocheta,
        val modulacion: String,
        val lados: List<LadoMedido>,
        val u: PerfilU,
        val puente: Puente,
        val cruce: Float = 0.7f,
        val mochetaInferior: Float = 0f,
        val izquierda: Borde = Borde.MURO,
        val derecha: Borde = Borde.MURO,
        val libresVerticales: Int = 0,
        /** Perfil que cierra un lado libre (su ancho o su alto). */
        val perfilLibre: Float = 2.5f,
        /** Esquinero de las L y C; null = "ninguno" (ni descuento ni parante de esquina). */
        val esquinero: Puente? = null,
        /** Todas las esquinas entre lados son curvas (curva de esquina): ni aumento ni descuento. */
        val esquinaCurva: Boolean = false,
        /**
         * La curva de cada esquina, si esa esquina se resolvió con un arco (una por arista entre lados;
         * null = esquina en ángulo). La curva es un trozo más de la ventana, con su desarrollo de
         * ancho, entre dos parantes (N-84). Vacío = ninguna.
         */
        val curvas: List<LadoMedido?> = emptyList(),
        val repartoManual: List<Int>? = null,
        val corredizasPorTramoNfc: Int = 0
    )

    fun ventana(e: Entrada): VentanaNova {
        val apa = e.acabado == Acabado.APA
        val n = e.lados.size
        // Qué hay en cada arista entre dos lados: ¿una curva?
        val curvaEn = (0 until (n - 1).coerceAtLeast(0)).map { i -> e.esquinaCurva || e.curvas.getOrNull(i) != null }
        // El reparto del esquinero, solo en las aristas en ángulo. En aparente lo pierde el lado entero;
        // en inaparente solo su estructura (N-75).
        val anchosEsquina = descuentoEsquinas(e.lados.map { it.ancho }, e.esquinero, curvaEn)

        val lados = mutableListOf<LadoNova>()
        e.lados.forEachIndexed { i, l ->
            val izq = if (i == 0) e.izquierda else if (curvaEn[i - 1]) Borde.ESQUINA_CURVA else Borde.ESQUINA_ANGULO
            val der = if (i == n - 1) e.derecha else if (curvaEn[i]) Borde.ESQUINA_CURVA else Borde.ESQUINA_ANGULO
            val libresLaterales = listOf(izq, der).count { it == Borde.LIBRE }
            val esquina = anchosEsquina[i]
            // APA en esquina curva (dictado el 2026-10-02): cada pared descuenta entero el parante de
            // su punta de curva y el cálculo se hace con ese ancho útil; la curva conserva su desarrollo.
            val paranteCurva = e.esquinero?.altura ?: 0f
            val curvaLado = listOf(izq, der).count { it == Borde.ESQUINA_CURVA } * paranteCurva
            val anchoCalculo = l.ancho - (if (apa) esquina.first + esquina.second + curvaLado + libresLaterales * e.perfilLibre else 0f)
            val altoCalculo = l.alto - (if (apa) e.libresVerticales * e.perfilLibre else 0f)
            val lado = DisenoAutomatico.lado(DisenoAutomatico.Entrada(
                anchoMedido = l.ancho, anchoCalculo = anchoCalculo.coerceAtLeast(0f),
                anchoEstructura = if (apa) null else (anchoCalculo - esquina.first - esquina.second).coerceAtLeast(0f),
                alto = altoCalculo, altoMedido = l.alto, hoja = l.hoja,
                acabado = e.acabado, puente = e.puente, divisManual = l.divisManual,
                modulacion = e.modulacion, repartoManual = e.repartoManual,
                corredizasPorTramoNfc = e.corredizasPorTramoNfc,
                izquierda = izq, derecha = der
            ))
            lados.add(if (apa) lado else lado.copy(estructuraIzq = esquina.first, estructuraDer = esquina.second))
            // La curva de la arista que sigue: su propio trozo, con su desarrollo (N-08, N-84).
            if (i < n - 1) e.curvas.getOrNull(i)?.let { c ->
                lados.add(DisenoAutomatico.lado(DisenoAutomatico.Entrada(
                    anchoMedido = c.ancho, anchoCalculo = c.ancho,
                    alto = c.alto - (if (apa) e.libresVerticales * e.perfilLibre else 0f), altoMedido = c.alto,
                    hoja = c.hoja, acabado = e.acabado, puente = e.puente, divisManual = c.divisManual,
                    modulacion = e.modulacion, corredizasPorTramoNfc = e.corredizasPorTramoNfc,
                    izquierda = Borde.ESQUINA_CURVA, derecha = Borde.ESQUINA_CURVA
                )).copy(curvaDeEsquina = true))
            }
        }
        return VentanaNova(
            e.acabado, e.modelo, lados, e.u, e.puente, e.cruce, e.mochetaInferior,
            libresVerticales = e.libresVerticales, perfilLibre = e.perfilLibre,
            esquinero = if (n > 1) e.esquinero else null
        )
    }

    /**
     * N-38: en cada esquina de aparente los dos lados se reparten el esquinero. Si su medida normal
     * y la de esquina son iguales, cada lado descuenta esa; si no, reparto invertido: el lado más
     * ancho se lleva la parte menor. El lado del medio de una C descuenta sus dos esquinas.
     *
     * Devuelve, por lado, lo que pierde por su izquierda y por su derecha. Una arista curva
     * ([curva]) no descuenta nada (G-09).
     */
    fun descuentoEsquinas(anchos: List<Float>, esquinero: Puente?, curva: List<Boolean> = emptyList()): List<Pair<Float, Float>> {
        val izq = FloatArray(anchos.size)
        val der = FloatArray(anchos.size)
        if (esquinero != null) {
            val normal = esquinero.altura
            val deEsquina = esquinero.esquinero
            for (i in 0 until anchos.size - 1) {
                if (curva.getOrNull(i) == true) continue
                if (kotlin.math.abs(deEsquina - normal) < 0.001f) {
                    der[i] += normal; izq[i + 1] += normal
                } else {
                    val mayor = maxOf(normal, deEsquina)
                    val menor = minOf(normal, deEsquina)
                    if (anchos[i] >= anchos[i + 1]) { der[i] += menor; izq[i + 1] += mayor }
                    else { der[i] += mayor; izq[i + 1] += menor }
                }
            }
        }
        return anchos.indices.map { izq[it] to der[it] }
    }
}
