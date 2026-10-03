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
        val corredizasPorTramoNfc: Int = 0
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
    fun conLados(base: Entrada, lista: List<LadoDeLista>): Entrada? {
        if (lista.none { !it.curvo }) return null
        if (lista.zipWithNext().any { (a, b) -> a.curvo && b.curvo }) return null
        val paredes = lista.indices.filter { !lista[it].curvo }
        val primera = paredes.first()
        val ultima = paredes.last()
        // Entre dos paredes seguidas, la curva que haya entre ellas.
        val curvas = (0 until paredes.size - 1).map { k -> lista.getOrNull(paredes[k] + 1)?.takeIf { it.curvo }?.medida }
        return base.copy(
            lados = paredes.map { lista[it].medida },
            curvas = curvas,
            curvaInicial = lista.getOrNull(primera - 1)?.takeIf { it.curvo }?.medida,
            curvaFinal = lista.getOrNull(ultima + 1)?.takeIf { it.curvo }?.medida
        )
    }

    /**
     * Un lado en cero con la curva en su esquina es la ventana que empieza (o acaba) en la curva
     * (2026-10-03): así se escribe en Nova y así llega de Medidas. La pared en cero no existe; la
     * curva pasa a ser la del principio (o la del final), con su parante y N-104/N-105.
     */
    fun conCurvasEnLasPuntas(entrada: Entrada): Entrada {
        var e = entrada
        if (e.lados.size >= 2 && e.lados.first().ancho <= 0.5f && e.curvaInicial == null) {
            e.curvas.firstOrNull()?.let { c -> e = e.copy(lados = e.lados.drop(1), curvas = e.curvas.drop(1), curvaInicial = c) }
        }
        val n = e.lados.size
        if (n >= 2 && e.lados.last().ancho <= 0.5f && e.curvaFinal == null) {
            e.curvas.getOrNull(n - 2)?.let { c ->
                e = e.copy(lados = e.lados.dropLast(1), curvas = e.curvas.take(n - 2), curvaFinal = c)
            }
        }
        return e
    }

    fun ventana(e: Entrada): VentanaNova {
        val apa = e.acabado == Acabado.APA
        val n = e.lados.size
        // Qué hay en cada arista entre dos lados: ¿una curva?
        val curvaEn = (0 until (n - 1).coerceAtLeast(0)).map { i -> e.esquinaCurva || e.curvas.getOrNull(i) != null }
        // El reparto del esquinero, solo en las aristas en ángulo. En aparente lo pierde el lado entero;
        // en inaparente solo su estructura (N-75).
        val anchosEsquina = descuentoEsquinas(e.lados.map { it.ancho }, e.esquinero, curvaEn)

        val lados = mutableListOf<LadoNova>()
        // La curva del principio: arranca en el borde izquierdo de la ventana y entra en la primera pared.
        e.curvaInicial?.let { lados.add(trozoCurvo(e, it, e.esquinero?.altura ?: 0f, Borde.ESQUINA_CURVA, izquierda = e.izquierda)) }
        e.lados.forEachIndexed { i, l ->
            val izq = if (i == 0) (if (e.curvaInicial != null) Borde.ESQUINA_CURVA else e.izquierda)
                else if (curvaEn[i - 1]) Borde.ESQUINA_CURVA else Borde.ESQUINA_ANGULO
            val der = if (i == n - 1) (if (e.curvaFinal != null) Borde.ESQUINA_CURVA else e.derecha)
                else if (curvaEn[i]) Borde.ESQUINA_CURVA else Borde.ESQUINA_ANGULO
            val libresLaterales = listOf(izq, der).count { it == Borde.LIBRE }
            val esquina = anchosEsquina[i]
            // Esquina curva (dictado el 2026-10-02). APA (N-104): cada pared descuenta entero el
            // parante de su punta de curva y se calcula con ese ancho útil; la curva conserva su
            // desarrollo. INA (N-105): el parante se reparte, media a la pared y media a la curva, y
            // solo en la estructura (puente, riel y lo que sale del puente); el vidrio pasa por delante.
            val paranteCurva = e.esquinero?.altura ?: 0f
            val curvaIzq = if (izq == Borde.ESQUINA_CURVA) paranteCurva else 0f
            val curvaDer = if (der == Borde.ESQUINA_CURVA) paranteCurva else 0f
            val pierdeIzq = esquina.first + curvaIzq / 2f
            val pierdeDer = esquina.second + curvaDer / 2f
            val anchoCalculo = l.ancho - (if (apa) esquina.first + esquina.second + curvaIzq + curvaDer + libresLaterales * e.perfilLibre else 0f)
            val altoCalculo = l.alto - (if (apa) e.libresVerticales * e.perfilLibre else 0f)
            val lado = DisenoAutomatico.lado(DisenoAutomatico.Entrada(
                anchoMedido = l.ancho, anchoCalculo = anchoCalculo.coerceAtLeast(0f),
                anchoEstructura = if (apa) null else (anchoCalculo - pierdeIzq - pierdeDer).coerceAtLeast(0f),
                alto = altoCalculo, altoMedido = l.alto, hoja = l.hoja,
                acabado = e.acabado, puente = e.puente, divisManual = l.divisManual,
                modulacion = e.modulacion, repartoManual = e.repartoManual,
                corredizasPorTramoNfc = e.corredizasPorTramoNfc,
                izquierda = izq, derecha = der
            ))
            lados.add(if (apa) lado else lado.copy(estructuraIzq = pierdeIzq, estructuraDer = pierdeDer))
            // La curva de la arista que sigue: su propio trozo, con su desarrollo (N-08, N-84).
            if (i < n - 1) e.curvas.getOrNull(i)?.let { lados.add(trozoCurvo(e, it, paranteCurva, Borde.ESQUINA_CURVA)) }
        }
        // La curva del final: entra desde la última pared y acaba en el borde derecho de la ventana.
        e.curvaFinal?.let { lados.add(trozoCurvo(e, it, e.esquinero?.altura ?: 0f, e.derecha)) }
        return VentanaNova(
            e.acabado, e.modelo, lados, e.u, e.puente, e.cruce, e.mochetaInferior,
            libresVerticales = e.libresVerticales, perfilLibre = e.perfilLibre,
            esquinero = if (n > 1 || e.curvaFinal != null || e.curvaInicial != null) e.esquinero else null
        )
    }

    /**
     * El trozo de una curva de esquina (N-84): su desarrollo, entre [izquierda] y [derecha]. Cada
     * punta es una esquina curva (da a una pared) o el borde de la ventana (la curva del principio o
     * la del final). La curva no descuenta nada en APA (N-104); en INA su estructura pierde media
     * parante en cada punta que da a una pared (N-105). Contra el vacío, como cualquier lado libre.
     */
    private fun trozoCurvo(e: Entrada, c: LadoMedido, parante: Float, derecha: Borde, izquierda: Borde = Borde.ESQUINA_CURVA): LadoNova {
        val apa = e.acabado == Acabado.APA
        val mediaParante = if (apa) 0f else parante / 2f
        val mediaIzq = if (izquierda == Borde.ESQUINA_CURVA) mediaParante else 0f
        val mediaDer = if (derecha == Borde.ESQUINA_CURVA) mediaParante else 0f
        val libre = if (apa) listOf(izquierda, derecha).count { it == Borde.LIBRE } * e.perfilLibre else 0f
        val anchoCalculo = (c.ancho - libre).coerceAtLeast(0f)
        return DisenoAutomatico.lado(DisenoAutomatico.Entrada(
            anchoMedido = c.ancho, anchoCalculo = anchoCalculo,
            anchoEstructura = if (apa) null else (anchoCalculo - mediaIzq - mediaDer).coerceAtLeast(0f),
            alto = c.alto - (if (apa) e.libresVerticales * e.perfilLibre else 0f), altoMedido = c.alto,
            hoja = c.hoja, acabado = e.acabado, puente = e.puente, divisManual = c.divisManual,
            modulacion = e.modulacion, corredizasPorTramoNfc = e.corredizasPorTramoNfc,
            izquierda = izquierda, derecha = derecha
        )).copy(curvaDeEsquina = true, estructuraIzq = mediaIzq, estructuraDer = mediaDer)
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
