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
        /**
         * La curva con la que TERMINA la ventana, después del último lado (2026-10-03: pared, esquina
         * curva, pared y la curva del final). Es una esquina curva sin pared detrás: la pared descuenta
         * su parante como en cualquier esquina curva (N-104, N-105) y la curva acaba contra el borde
         * derecho de la ventana. Null = ninguna.
         */
        val curvaFinal: LadoMedido? = null,
        /** La curva con la que EMPIEZA la ventana, antes del primer lado: la misma regla al revés. */
        val curvaInicial: LadoMedido? = null,
        val repartoManual: List<Int>? = null,
        val corredizasPorTramoNfc: Int = 0,
        /**
         * El quiebre de cada curva al entrar y al salir, en grados, en el orden en que aparecen en la
         * ventana (la del principio, las de las esquinas, la del final). 0 = entra en tangente. Se
         * calcula del ángulo entre la pared y la cuerda (ArcoEsquina.quiebreConAnguloDeCuerda).
         */
        val quiebres: List<Pair<Float, Float>> = emptyList()
    )

    /** Un lado de la lista de la ventana (2026-10-03): recto o curvo, en orden. */
    data class LadoDeLista(val medida: LadoMedido, val curvo: Boolean)

    /**
     * La entrada desde la lista de lados, donde la curva es un lado más: las paredes son los rectos,
     * la curva entre dos de ellas es la de su esquina y la de una punta es la del principio o la del
     * final. Las reglas no cambian (N-104, N-105): en cada punta de curva va un parante. Null si la
     * lista no tiene paredes (la ventana curva va por su camino) o lleva dos curvas seguidas, que
     * todavía no tienen regla.
     */
    fun conLados(base: Entrada, lista: List<LadoDeLista>, uniones: List<Float> = emptyList()): Entrada? {
        if (lista.none { !it.curvo }) return null
        if (lista.zipWithNext().any { (a, b) -> a.curvo && b.curvo }) return null
        val paredes = lista.indices.filter { !lista[it].curvo }
        val primera = paredes.first()
        val ultima = paredes.last()
        // Entre dos paredes seguidas, la curva que haya entre ellas.
        val curvas = (0 until paredes.size - 1).map { k -> lista.getOrNull(paredes[k] + 1)?.takeIf { it.curvo }?.medida }
        // El quiebre de cada curva, de los grados de sus uniones (180 = sigue de largo, tangente).
        fun quiebre(union: Int) = uniones.getOrNull(union)?.let { 180f - kotlin.math.abs(it) } ?: 0f
        val quiebres = lista.indices.filter { lista[it].curvo }.map { i -> (if (i > 0) quiebre(i - 1) else 0f) to (if (i < lista.lastIndex) quiebre(i) else 0f) }
        return base.copy(
            lados = paredes.map { lista[it].medida },
            curvas = curvas,
            quiebres = quiebres,
            curvaInicial = lista.getOrNull(primera - 1)?.takeIf { it.curvo }?.medida,
            curvaFinal = lista.getOrNull(ultima + 1)?.takeIf { it.curvo }?.medida
        )
    }

    // `conCurvasEnLasPuntas` traducía el lado en cero con la curva en su esquina (2026-10-03). Desde el
    // 2026-10-19 no hay lados en cero: la curva de una punta llega como un lado curvo (conLados).

    fun ventana(e: Entrada): VentanaNova {
        val apa = e.acabado == Acabado.APA
        val n = e.lados.size
        // Los trozos de la ventana en orden: las paredes y las curvas (N-84), cada curva con su desarrollo.
        // Hasta el 2026-10-19 las paredes y las curvas se armaban por caminos separados y la unión de una
        // pared con una curva era siempre esquina curva; ahora cada unión dice lo que es.
        val trozos = buildList {
            e.curvaInicial?.let { add(it to true) }
            e.lados.forEachIndexed { i, l ->
                add(l to false)
                if (i < n - 1) e.curvas.getOrNull(i)?.let { add(it to true) }
            }
            e.curvaFinal?.let { add(it to true) }
        }
        // El número de cada curva en el orden de la ventana, para leer su quiebre.
        val ordenDeCurva = IntArray(trozos.size) { -1 }
        var curvasVistas = 0
        trozos.forEachIndexed { j, t -> if (t.second) ordenDeCurva[j] = curvasVistas++ }
        // Qué hay en cada unión: esquina curva (true) o en ángulo. Dos paredes seguidas doblan en ángulo,
        // salvo la esquina curva de toda la ventana. Una pared y una curva: esquina curva si la curva entra
        // en tangente, y en ángulo —lo mismo que en la L, con su esquinero— si entra con un quiebre de
        // [QUIEBRE_ESQUINA] o más (dictado el 2026-10-19).
        val enCurva = (0 until (trozos.size - 1).coerceAtLeast(0)).map { j ->
            val antesCurva = trozos[j].second
            val despuesCurva = trozos[j + 1].second
            when {
                !antesCurva && !despuesCurva -> e.esquinaCurva
                despuesCurva -> quiebreDe(e, ordenDeCurva[j + 1]).first < QUIEBRE_ESQUINA
                else -> quiebreDe(e, ordenDeCurva[j]).second < QUIEBRE_ESQUINA
            }
        }
        // El reparto del esquinero, solo en las uniones en ángulo. En aparente lo pierde el trozo entero;
        // en inaparente solo su estructura (N-75).
        val anchosEsquina = descuentoEsquinas(trozos.map { it.first.ancho }, e.esquinero, enCurva)
        val paranteCurva = e.esquinero?.altura ?: 0f

        val lados = trozos.mapIndexed { j, (l, esCurva) ->
            val izq = if (j == 0) e.izquierda else if (enCurva[j - 1]) Borde.ESQUINA_CURVA else Borde.ESQUINA_ANGULO
            val der = if (j == trozos.lastIndex) e.derecha else if (enCurva[j]) Borde.ESQUINA_CURVA else Borde.ESQUINA_ANGULO
            val libresLaterales = listOf(izq, der).count { it == Borde.LIBRE }
            val esquina = anchosEsquina[j]
            // Esquina curva (dictado el 2026-10-02). APA (N-104): cada pared descuenta entero el
            // parante de su punta de curva y se calcula con ese ancho útil; la curva conserva su
            // desarrollo. INA (N-105): el parante se reparte, media a la pared y media a la curva, y
            // solo en la estructura (puente, riel y lo que sale del puente); el vidrio pasa por delante.
            val curvaIzq = if (izq == Borde.ESQUINA_CURVA) paranteCurva else 0f
            val curvaDer = if (der == Borde.ESQUINA_CURVA) paranteCurva else 0f
            val pierdeIzq = esquina.first + curvaIzq / 2f
            val pierdeDer = esquina.second + curvaDer / 2f
            // Lo que pierde en aparente: la pared, el parante entero de su punta de curva; la curva, nada.
            val curvaEnAncho = if (esCurva) 0f else curvaIzq + curvaDer
            val anchoCalculo = l.ancho - (if (apa) esquina.first + esquina.second + curvaEnAncho + libresLaterales * e.perfilLibre else 0f)
            val altoCalculo = l.alto - (if (apa) e.libresVerticales * e.perfilLibre else 0f)
            val lado = DisenoAutomatico.lado(DisenoAutomatico.Entrada(
                anchoMedido = l.ancho, anchoCalculo = anchoCalculo.coerceAtLeast(0f),
                anchoEstructura = if (apa) null else (anchoCalculo - pierdeIzq - pierdeDer).coerceAtLeast(0f),
                alto = altoCalculo, altoMedido = l.alto, hoja = l.hoja,
                acabado = e.acabado, puente = e.puente, divisManual = l.divisManual,
                modulacion = e.modulacion, repartoManual = if (esCurva) null else e.repartoManual,
                corredizasPorTramoNfc = e.corredizasPorTramoNfc,
                izquierda = izq, derecha = der
            ))
            when {
                esCurva -> lado.copy(curvaDeEsquina = true, estructuraIzq = if (apa) 0f else pierdeIzq, estructuraDer = if (apa) 0f else pierdeDer)
                apa -> lado
                else -> lado.copy(estructuraIzq = pierdeIzq, estructuraDer = pierdeDer)
            }
        }
        return VentanaNova(
            e.acabado, e.modelo, lados, e.u, e.puente, e.cruce, e.mochetaInferior,
            libresVerticales = e.libresVerticales, perfilLibre = e.perfilLibre,
            esquinero = if (n > 1 || e.curvaFinal != null || e.curvaInicial != null) e.esquinero else null
        )
    }

    /** El quiebre de entrada y de salida de la curva número [orden] de la ventana, en grados (0 = tangente). */
    private fun quiebreDe(e: Entrada, orden: Int): Pair<Float, Float> =
        e.quiebres.getOrNull(orden)?.let { kotlin.math.abs(it.first) to kotlin.math.abs(it.second) } ?: (0f to 0f)

    /**
     * Desde cuánto el quiebre entre una pared y una curva es una esquina en ángulo, con su parante de
     * esquina como en la L, y no una tangente imperfecta (dictado el 2026-10-19: 5°, lo mismo con que
     * Medidas lee un dibujo como recto o en escuadra).
     */
    const val QUIEBRE_ESQUINA = 5f

    // El trozo de cada curva se armaba aparte en `trozoCurvo` hasta el 2026-10-19; ahora sale del mismo
    // camino que las paredes, en ventana(). Está en el historial de git.

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
