package crystal.crystal.optimizadores.corte

import android.util.Log

class CorteOptimizer {

    /**
     * Clase Varilla - exactamente como en el código original
     */
    data class Varilla(
        val longitud: Float,
        val cortes: MutableList<Float> = mutableListOf(),
        var restante: Float = longitud
    )

    /**
     * Clase auxiliar para mantener varillas con referencias
     */
    data class VarillaConReferencias(
        val varilla: Varilla,
        val referencias: MutableList<String>
    )

    /**
     * Resultado interno para comparar estrategias
     */
    data class ResultadoInterno(
        val varillasUsadas: List<VarillaConReferencias>,
        val eficiencia: Float,
        val totalRetazos: Float,
        val algoritmoUsado: String,
        val combinacionesExploradas: Int
    )

    /**
     * Data class para resultados de algoritmo compatible con AnalisisMejoras
     */
    data class ResultadoAlgoritmo(
        val varillasUsadas: List<VarillaConReferencias>,
        val porcentajeEficiencia: Float,
        val totalRetazos: Float,
        val nombreAlgoritmo: String
    )

    /**
     * Data classes auxiliares para manejar cantidades en Fórmula 1
     */
    private data class VarillaConCantidad(
        val longitud: Float,
        var cantidad: Int,
        val referencia: String
    )

    private data class CorteConCantidad(
        val longitud: Float,
        var cantidad: Int,
        val referencia: String
    )

    /**
     * Data class para análisis de varillas
     */
    private data class AnalisisVarilla(
        val indice: Int,
        val varilla: VarillaConReferencias,
        val eficiencia: Float,
        val retazo: Float,
        val cantidadCortes: Int
    )

    /**
     * Data class para opciones de llenado de Fórmula 1
     */
    private data class OpcionLlenadoFormula1(
        val cortesUsados: List<CorteConCantidad>,
        val retazo: Float,
        val metodologia: String
    )

    private data class CortePlano(
        val longitud: Float,
        val referencia: String
    )

    private data class BinReempaque(
        val longitud: Float,
        val cortes: MutableList<Float> = mutableListOf(),
        val referencias: MutableList<String> = mutableListOf(),
        var restante: Float = longitud
    )

    // ========== MODELO CONSISTENTE DE GROSOR DE DISCO ==========

    /**
     * 🎯 FUNCIÓN CLAVE: Calcula espacio total necesario CONSISTENTEMENTE
     * MODELO: [Corte1][Grosor][Corte2][Grosor][Corte3]...[CorteN][Retazo]
     * FÓRMULA: EspacioNecesario = SumaCortes + (CantidadCortes - 1) × GrosorDisco
     */
    private fun calcularEspacioTotalNecesario(cortes: List<CorteConCantidad>, grosorDisco: Float): Float {
        if (cortes.isEmpty()) return 0f

        val sumaCortes = cortes.sumOf { it.longitud.toDouble() }.toFloat()
        val cantidadCortes = cortes.size
        val espacioGrosor = if (cantidadCortes > 1) (cantidadCortes - 1) * grosorDisco else 0f

        return sumaCortes + espacioGrosor
    }

    /**
     * 🎯 FUNCIÓN CLAVE: Verifica si una lista de cortes cabe en una varilla
     */
    private fun cabeEnVarilla(cortes: List<CorteConCantidad>, longitudVarilla: Float, grosorDisco: Float): Boolean {
        val espacioNecesario = calcularEspacioTotalNecesario(cortes, grosorDisco)
        return espacioNecesario <= longitudVarilla
    }

    /**
     * 🎯 FUNCIÓN CLAVE: Calcula retazo CONSISTENTEMENTE
     */
    private fun calcularRetazo(cortes: List<CorteConCantidad>, longitudVarilla: Float, grosorDisco: Float): Float {
        val espacioNecesario = calcularEspacioTotalNecesario(cortes, grosorDisco)
        return longitudVarilla - espacioNecesario
    }

    // ========== FUNCIONES PRINCIPALES ACTIVAS ==========

    /**
     * FUNCIÓN PRINCIPAL MEJORADA - Maneja cortes más largos que varillas disponibles
     * @param nivel 1–20: controla la profundidad de búsqueda. Mayor nivel = mejor resultado, más lento.
     */
    fun optimizarCortesConConfiguracion(
        piezasRequeridas: List<PiezaCorte>,
        varillasDisponibles: List<PiezaCorte>,
        grosorDisco: Float,
        nivel: Int = 5
    ): List<VarillaConReferencias> {
        val nivelClamped = nivel.coerceIn(1, 20)

        val tiempoInicio = System.currentTimeMillis()
        DebugHelper.logInicioOptimizacionInteligente()

        Log.d("CorteOptimizer", "🚀 FÓRMULA 1 + MANEJO DE CORTES LARGOS + REDISTRIBUCIÓN")
        Log.d("CorteOptimizer", "🔧 GROSOR DISCO: ${grosorDisco}cm (MODELO CONSISTENTE)")

        // PASO NUEVO: Detectar y manejar cortes más largos que varillas disponibles
        val (piezasAjustadas, varillasCompletas) = manejarCortesLargos(piezasRequeridas, varillasDisponibles, grosorDisco)

        Log.d("CortesLargos", "Piezas después del tratamiento: ${piezasAjustadas.size}")
        Log.d("CortesLargos", "Varillas completas generadas: ${varillasCompletas.size}")

        // PASO 1: Aplicar Fórmula 1 con las piezas ajustadas
        val resultadoInicial = formula1VarillaMinimaCorteMayor(piezasAjustadas, varillasDisponibles, grosorDisco, nivelClamped)

        // PASO 2: Combinar varillas completas + resultado de optimización
        val resultadoCombinado = combinarResultados(varillasCompletas, resultadoInicial.varillasUsadas)

        Log.d("Redistribucion", "\n=== INICIANDO POST-PROCESAMIENTO ===")
        Log.d("Redistribucion", "Resultado combinado: ${resultadoCombinado.size} varillas")

        // PASO 3: Redistribuir cortes para optimizar retazos
        val resultadoOptimizado = redistribuirCortesInteligente(
            resultadoCombinado.toMutableList(),
            grosorDisco,
            nivelClamped
        )
        val resultadoReparado = repararResultadoFinal(
            resultadoOptimizado,
            grosorDisco,
            nivelClamped
        )

        val tiempoTotal = System.currentTimeMillis() - tiempoInicio
        Log.d("CorteOptimizer", "✅ Optimización completada en ${tiempoTotal}ms")

        return resultadoReparado
    }

    /**
     * NUEVA FUNCIÓN: Maneja cortes más largos que las varillas disponibles
     * Retorna: Pair(piezasAjustadas, varillasCompletas)
     */
    private fun manejarCortesLargos(
        piezasRequeridas: List<PiezaCorte>,
        varillasDisponibles: List<PiezaCorte>,
        grosorDisco: Float
    ): Pair<List<PiezaCorte>, List<VarillaConReferencias>> {

        // Encontrar la varilla más larga disponible
        val varillaMaxima = varillasDisponibles.maxByOrNull { it.longitud }?.longitud ?: 0f

        if (varillaMaxima <= 0f) {
            Log.w("CortesLargos", "No hay varillas disponibles")
            return Pair(piezasRequeridas, emptyList())
        }

        Log.d("CortesLargos", "=== DETECTANDO CORTES LARGOS ===")
        Log.d("CortesLargos", "Varilla máxima disponible: ${varillaMaxima}cm")

        val piezasAjustadas = mutableListOf<PiezaCorte>()
        val varillasCompletas = mutableListOf<VarillaConReferencias>()
        var contadorEspecial = 1

        piezasRequeridas.forEach { pieza ->
            // Verificar si el corte cabe en la varilla máxima (considerando grosor mínimo)
            val espacioNecesarioMinimo = pieza.longitud + grosorDisco // Un corte solo

            if (espacioNecesarioMinimo > varillaMaxima) {
                Log.d("CortesLargos", "🔧 TRATANDO: ${pieza.longitud}cm (${pieza.referencia}) cantidad=${pieza.cantidad}")

                // Procesar cada cantidad individualmente
                repeat(pieza.cantidad) {
                    val referenciaEspecial = "CE${contadorEspecial}" // CE = Corte Especial

                    // Dividir el corte largo
                    val (varillasNecesarias, resto) = dividirCorteLargo(pieza.longitud, varillaMaxima, grosorDisco)

                    Log.d("CortesLargos", "   Referencia especial: $referenciaEspecial")
                    Log.d("CortesLargos", "   Varillas completas necesarias: $varillasNecesarias")
                    Log.d("CortesLargos", "   Resto: ${resto}cm")

                    // Crear varillas completas
                    repeat(varillasNecesarias) {
                        val corteCompleto = varillaMaxima - grosorDisco // Máximo que cabe con grosor
                        val varillaCompleta = VarillaConReferencias(
                            Varilla(varillaMaxima, mutableListOf(corteCompleto), grosorDisco),
                            mutableListOf(referenciaEspecial)
                        )
                        varillasCompletas.add(varillaCompleta)
                    }

                    // Agregar el resto a piezas ajustadas (si existe)
                    if (resto > 0f) {
                        piezasAjustadas.add(PiezaCorte(resto, 1, referenciaEspecial, pieza.cortada))
                    }

                    contadorEspecial++
                }

            } else {
                // Pieza normal, no necesita tratamiento
                piezasAjustadas.add(pieza)
            }
        }

        Log.d("CortesLargos", "=== RESULTADO DEL TRATAMIENTO ===")
        Log.d("CortesLargos", "Piezas ajustadas: ${piezasAjustadas.size}")
        Log.d("CortesLargos", "Varillas completas: ${varillasCompletas.size}")

        return Pair(piezasAjustadas, varillasCompletas)
    }

    /**
     * Divide un corte largo en varillas completas + resto
     */
    private fun dividirCorteLargo(longitudCorte: Float, longitudVarilla: Float, grosorDisco: Float): Pair<Int, Float> {
        val longitudUtilPorVarilla = longitudVarilla - grosorDisco // Espacio útil por varilla
        val varillasCompletas = (longitudCorte / longitudUtilPorVarilla).toInt()
        val resto = longitudCorte - (varillasCompletas * longitudUtilPorVarilla)

        return Pair(varillasCompletas, resto)
    }

    /**
     * Combina varillas completas (de cortes largos) con resultado de optimización
     */
    private fun combinarResultados(
        varillasCompletas: List<VarillaConReferencias>,
        varillasOptimizadas: List<VarillaConReferencias>
    ): List<VarillaConReferencias> {

        val resultado = mutableListOf<VarillaConReferencias>()

        // Agregar primero las varillas completas (tienen prioridad visual)
        resultado.addAll(varillasCompletas)

        // Luego agregar las varillas optimizadas
        resultado.addAll(varillasOptimizadas)

        return resultado
    }

    private fun redistribuirCortesInteligente(
        varillasOriginales: MutableList<VarillaConReferencias>,
        grosorDisco: Float,
        nivel: Int = 5
    ): List<VarillaConReferencias> {

        Log.d("Redistribucion", "🎯 NUEVO ENFOQUE: Post-procesamiento directo y simple")

        // PASO 1: Crear lista limpia solo con varillas que tienen cortes
        val varillasLimpias = varillasOriginales.filter {
            it.varilla.cortes.isNotEmpty()
        }.toMutableList()

        Log.d("Redistribucion", "📊 Estado inicial:")
        varillasLimpias.forEachIndexed { index, varilla ->
            val eficiencia = ((varilla.varilla.longitud - varilla.varilla.restante) / varilla.varilla.longitud) * 100f
            Log.d("Redistribucion", "   Varilla $index: ${varilla.varilla.cortes.size} cortes, retazo ${String.format("%.1f", varilla.varilla.restante)}cm, eficiencia ${String.format("%.1f", eficiencia)}%")
        }

        // PASO 2: ALGORITMO SIMPLE - Eliminar varillas "tontas"
        var eliminaciones = 0
        var intentos = 0

        do {
            intentos++
            var huboEliminacion = false

            Log.d("Redistribucion", "\n--- Intento de limpieza $intentos ---")

            // Buscar varillas candidatas a eliminar (con pocos cortes y/o baja eficiencia)
            for (i in varillasLimpias.indices.reversed()) { // Revisar al revés para eliminar sin problemas de índices
                val varilla = varillasLimpias[i]
                val eficiencia = ((varilla.varilla.longitud - varilla.varilla.restante) / varilla.varilla.longitud) * 100f

                // Criterios de eliminación muy simples:
                val esIneficiente = eficiencia < 40f  // Menos de 40% de uso
                val tienePocosCortes = varilla.varilla.cortes.size <= 2  // 2 cortes o menos
                val retazoGigante = varilla.varilla.restante > varilla.varilla.longitud * 0.7f  // Más de 70% sin usar

                if (esIneficiente || (tienePocosCortes && retazoGigante)) {
                    Log.d("Redistribucion", "🎯 Candidata a eliminar - Varilla $i:")
                    Log.d("Redistribucion", "   Eficiencia: ${String.format("%.1f", eficiencia)}%")
                    Log.d("Redistribucion", "   Cortes: ${varilla.varilla.cortes.size}")
                    Log.d("Redistribucion", "   Retazo: ${String.format("%.1f", varilla.varilla.restante)}cm")
                    Log.d("Redistribucion", "   Cortes a reubicar: ${varilla.varilla.cortes.zip(varilla.referencias) { c, r -> "${c}(${r})" }}")

                    // Intentar reubicar TODOS los cortes de esta varilla
                    val cortesReubicados = reubicarTodosLosCortes(varilla, varillasLimpias, i, grosorDisco)

                    if (cortesReubicados) {
                        Log.d("Redistribucion", "   ✅ VARILLA $i ELIMINADA EXITOSAMENTE")
                        varillasLimpias.removeAt(i)
                        eliminaciones++
                        huboEliminacion = true
                        break // Solo una eliminación por intento para mantener control
                    } else {
                        Log.d("Redistribucion", "   ❌ No se pudieron reubicar todos los cortes")
                    }
                }
            }

        } while (huboEliminacion && intentos < nivel * 2)

        Log.d("Redistribucion", "\n=== RESULTADO FINAL ===")
        Log.d("Redistribucion", "🎉 Varillas eliminadas: $eliminaciones")
        Log.d("Redistribucion", "🎉 Varillas finales: ${varillasLimpias.size}")
        Log.d("Redistribucion", "🎉 Intentos de limpieza: $intentos")

        // Estado final
        Log.d("Redistribucion", "\n📊 Estado final:")
        varillasLimpias.forEachIndexed { index, varilla ->
            val eficiencia = ((varilla.varilla.longitud - varilla.varilla.restante) / varilla.varilla.longitud) * 100f
            Log.d("Redistribucion", "   Varilla $index: ${varilla.varilla.cortes.size} cortes, retazo ${String.format("%.1f", varilla.varilla.restante)}cm, eficiencia ${String.format("%.1f", eficiencia)}%")
        }

        if (eliminaciones > 0) {
            Log.d("Redistribucion", "💰 AHORRO: $eliminaciones varillas = DINERO REAL AHORRADO")
        }

        return varillasLimpias
    }

    /**
     * Función auxiliar: Intenta reubicar todos los cortes de una varilla en otras varillas
     */
    private fun reubicarTodosLosCortes(
        varillaOrigen: VarillaConReferencias,
        todasLasVarillas: MutableList<VarillaConReferencias>,
        indiceOrigen: Int,
        grosorDisco: Float
    ): Boolean {

        val cortesAMover = varillaOrigen.varilla.cortes.zip(varillaOrigen.referencias)
        val movimientos = mutableListOf<Triple<Int, Float, String>>() // (índice_destino, corte, referencia)

        Log.d("Redistribucion", "   🔍 Intentando reubicar ${cortesAMover.size} cortes...")

        // Para cada corte, buscar dónde ponerlo
        for ((corte, referencia) in cortesAMover) {
            var corteSituado = false

            // Buscar en todas las demás varillas
            for (j in todasLasVarillas.indices) {
                if (j == indiceOrigen) continue // No en la misma varilla

                val varillaDestino = todasLasVarillas[j]
                val espacioNecesario = corte + grosorDisco // Corte + espacio para el disco

                if (varillaDestino.varilla.restante >= espacioNecesario) {
                    Log.d("Redistribucion", "     ✅ Corte ${corte}(${referencia}) → Varilla $j (retazo disponible: ${String.format("%.1f", varillaDestino.varilla.restante)}cm)")
                    movimientos.add(Triple(j, corte, referencia))
                    corteSituado = true
                    break
                }
            }

            if (!corteSituado) {
                Log.d("Redistribucion", "     ❌ Corte ${corte}(${referencia}) no se pudo situar en ninguna varilla")
                return false // Si no podemos situar un corte, falla todo
            }
        }

        // Si llegamos aquí, todos los cortes se pueden situar
        Log.d("Redistribucion", "   ✅ Todos los cortes se pueden reubicar. Ejecutando movimientos...")

        // Ejecutar todos los movimientos
        for ((indiceDestino, corte, referencia) in movimientos) {
            val varillaDestino = todasLasVarillas[indiceDestino]

            // Agregar el corte
            varillaDestino.varilla.cortes.add(corte)
            varillaDestino.referencias.add(referencia)

            // Actualizar retazo
            varillaDestino.varilla.restante -= (corte + grosorDisco)

            Log.d("Redistribucion", "     ✅ Movido ${corte}(${referencia}) → Varilla $indiceDestino")
        }

        return true
    }

    /**
     * Calcula el espacio necesario para una lista de cortes considerando grosor de disco
     */
    private fun calcularEspacioNecesario(cortes: List<Float>, grosorDisco: Float): Float {
        if (cortes.isEmpty()) return 0f
        return cortes.sum() + (cortes.size - 1) * grosorDisco
    }

    private fun repararResultadoFinal(
        varillas: List<VarillaConReferencias>,
        grosorDisco: Float,
        nivel: Int
    ): List<VarillaConReferencias> {
        var resultadoActual = varillas.filter { it.varilla.cortes.isNotEmpty() }
        if (resultadoActual.size <= 1) return resultadoActual

        var mejoras = 0
        val maxMejoras = nivel.coerceIn(1, 20)

        while (mejoras < maxMejoras) {
            val reparado = intentarReempacarEnMenosVarillas(resultadoActual, grosorDisco, nivel)
            if (reparado == null || reparado.size >= resultadoActual.size) break
            if (!conservaMismosCortes(resultadoActual, reparado)) {
                Log.w("ReparacionCortes", "Reempaque descartado: no conserva los mismos cortes")
                break
            }

            Log.d(
                "ReparacionCortes",
                "Mejora aceptada: ${resultadoActual.size} -> ${reparado.size} varillas"
            )
            resultadoActual = reparado
            mejoras++
        }

        return resultadoActual
    }

    private fun intentarReempacarEnMenosVarillas(
        varillas: List<VarillaConReferencias>,
        grosorDisco: Float,
        nivel: Int
    ): List<VarillaConReferencias>? {
        if (varillas.size <= 1) return null

        val cortes = extraerCortesPlanos(varillas)
        if (cortes.isEmpty()) return null

        val cantidadDestino = varillas.size - 1
        val longitudesDestino = varillas
            .map { it.varilla.longitud }
            .sortedDescending()
            .take(cantidadDestino)

        if (longitudesDestino.isEmpty()) return null

        val maxLongitudDestino = longitudesDestino.maxOrNull() ?: return null
        if (cortes.any { it.longitud > maxLongitudDestino + 0.001f }) return null

        val espacioMinimo = cortes.sumOf { it.longitud.toDouble() }.toFloat() +
            (cortes.size - cantidadDestino).coerceAtLeast(0) * grosorDisco
        val espacioDisponible = longitudesDestino.sum()
        if (espacioMinimo > espacioDisponible + 0.001f) return null

        val rapido = reempacarBestFitDescendente(cortes, longitudesDestino, grosorDisco)
        if (rapido != null) {
            Log.d("ReparacionCortes", "Reempaque rapido encontro ${rapido.size} varillas")
            return rapido
        }

        val profundo = reempacarBacktrackingLimitado(cortes, longitudesDestino, grosorDisco, nivel)
        if (profundo != null) {
            Log.d("ReparacionCortes", "Reempaque profundo encontro ${profundo.size} varillas")
        }
        return profundo
    }

    private fun extraerCortesPlanos(varillas: List<VarillaConReferencias>): List<CortePlano> {
        val cortes = mutableListOf<CortePlano>()
        varillas.forEach { varilla ->
            varilla.varilla.cortes.zip(varilla.referencias) { corte, referencia ->
                cortes.add(CortePlano(corte, referencia))
            }
        }
        return cortes
    }

    private fun reempacarBestFitDescendente(
        cortes: List<CortePlano>,
        longitudesDestino: List<Float>,
        grosorDisco: Float
    ): List<VarillaConReferencias>? {
        val bins = longitudesDestino.map { BinReempaque(it) }.toMutableList()
        val cortesOrdenados = ordenarCortesParaReempaque(cortes)

        for (corte in cortesOrdenados) {
            val destino = bins.indices
                .mapNotNull { indice ->
                    val retazo = calcularRetazoAlAgregar(bins[indice], corte.longitud, grosorDisco)
                    retazo?.let { indice to it }
                }
                .minByOrNull { it.second }
                ?.first
                ?: return null

            agregarCorteBin(bins[destino], corte, grosorDisco)
        }

        return binsAResultado(bins)
    }

    private fun reempacarBacktrackingLimitado(
        cortes: List<CortePlano>,
        longitudesDestino: List<Float>,
        grosorDisco: Float,
        nivel: Int
    ): List<VarillaConReferencias>? {
        val bins = longitudesDestino.map { BinReempaque(it) }.toMutableList()
        val cortesOrdenados = ordenarCortesParaReempaque(cortes)
        val nodos = intArrayOf(0)
        val nivelControlado = nivel.coerceIn(1, 20)
        val maxNodos = nivelControlado * 2500
        val tiempoFin = System.currentTimeMillis() + nivelControlado * 250L

        fun buscar(indiceCorte: Int): Boolean {
            if (indiceCorte >= cortesOrdenados.size) return true
            if (nodos[0] >= maxNodos || System.currentTimeMillis() > tiempoFin) return false

            nodos[0]++
            val corte = cortesOrdenados[indiceCorte]
            val candidatos = bins.indices
                .mapNotNull { indice ->
                    val retazo = calcularRetazoAlAgregar(bins[indice], corte.longitud, grosorDisco)
                    retazo?.let { indice to it }
                }
                .sortedBy { it.second }

            val estadosProbados = HashSet<String>()
            for ((indiceBin, _) in candidatos) {
                val bin = bins[indiceBin]
                val estado = estadoBinParaPoda(bin)
                if (!estadosProbados.add(estado)) continue

                agregarCorteBin(bin, corte, grosorDisco)
                if (buscar(indiceCorte + 1)) return true
                quitarUltimoCorteBin(bin, grosorDisco)
            }

            return false
        }

        return if (buscar(0)) binsAResultado(bins) else null
    }

    private fun ordenarCortesParaReempaque(cortes: List<CortePlano>): List<CortePlano> {
        return cortes.sortedWith(
            compareByDescending<CortePlano> { it.longitud }
                .thenBy { it.referencia }
        )
    }

    private fun calcularRetazoAlAgregar(
        bin: BinReempaque,
        corte: Float,
        grosorDisco: Float
    ): Float? {
        val cortes = bin.cortes + corte
        val retazo = bin.longitud - calcularEspacioNecesario(cortes, grosorDisco)
        return if (retazo >= -0.001f) retazo.coerceAtLeast(0f) else null
    }

    private fun agregarCorteBin(
        bin: BinReempaque,
        corte: CortePlano,
        grosorDisco: Float
    ) {
        bin.cortes.add(corte.longitud)
        bin.referencias.add(corte.referencia)
        bin.restante = bin.longitud - calcularEspacioNecesario(bin.cortes, grosorDisco)
    }

    private fun quitarUltimoCorteBin(bin: BinReempaque, grosorDisco: Float) {
        if (bin.cortes.isEmpty()) return
        bin.cortes.removeAt(bin.cortes.lastIndex)
        bin.referencias.removeAt(bin.referencias.lastIndex)
        bin.restante = bin.longitud - calcularEspacioNecesario(bin.cortes, grosorDisco)
    }

    private fun binsAResultado(bins: List<BinReempaque>): List<VarillaConReferencias> {
        return bins
            .filter { it.cortes.isNotEmpty() }
            .map { bin ->
                VarillaConReferencias(
                    Varilla(
                        longitud = bin.longitud,
                        cortes = bin.cortes.toMutableList(),
                        restante = bin.restante.coerceAtLeast(0f)
                    ),
                    bin.referencias.toMutableList()
                )
            }
    }

    private fun estadoBinParaPoda(bin: BinReempaque): String {
        val restanteCent = Math.round(bin.restante * 100f)
        return "${bin.longitud}|${bin.cortes.size}|$restanteCent"
    }

    private fun conservaMismosCortes(
        original: List<VarillaConReferencias>,
        reempacado: List<VarillaConReferencias>
    ): Boolean {
        return conteoCortes(original) == conteoCortes(reempacado)
    }

    private fun conteoCortes(varillas: List<VarillaConReferencias>): Map<String, Int> {
        val conteo = HashMap<String, Int>()
        varillas.forEach { varilla ->
            varilla.varilla.cortes.zip(varilla.referencias) { corte, referencia ->
                val key = "${Math.round(corte * 1000f)}|$referencia"
                conteo[key] = conteo.getOrDefault(key, 0) + 1
            }
        }
        return conteo
    }

    fun optimizarCortes(
        piezasRequeridas: List<PiezaCorte>,
        varillasDisponibles: List<PiezaCorte>,
        grosorDisco: Float,
        nivel: Int = 5
    ): List<VarillaConReferencias> =
        optimizarCortesConConfiguracion(piezasRequeridas, varillasDisponibles, grosorDisco, nivel)

    // ========== FÓRMULA 1 ESPECÍFICA MEJORADA - ACTIVA ==========

    private fun formula1VarillaMinimaCorteMayor(
        piezasRequeridas: List<PiezaCorte>,
        varillasDisponibles: List<PiezaCorte>,
        grosorDisco: Float,
        nivel: Int = 5
    ): ResultadoInterno {

        val varillasConCantidad = varillasDisponibles.map {
            VarillaConCantidad(it.longitud, it.cantidad, it.referencia)
        }.sortedBy { it.longitud }.toMutableList()

        val cortesConCantidad = piezasRequeridas.map {
            CorteConCantidad(it.longitud, it.cantidad, it.referencia)
        }.sortedByDescending { it.longitud }.toMutableList()

        val resultado = mutableListOf<VarillaConReferencias>()
        var iteracion = 0

        // CONFIGURACIÓN DE OPTIMIZACIÓN según nivel
        val MAX_OPCIONES_LLENADO = nivel * 3
        val TIMEOUT_ITERACION_MS = nivel.toLong() * 300L
        val condicionantes = listOf(0f, 1f, 2f, 3f, 5f, 8f, 12f, 20f, 30f)

        Log.d("Formula1Consistente", "=== FÓRMULA 1 CON MODELO CONSISTENTE ===")
        Log.d("Formula1Consistente", "🔧 GROSOR DISCO: ${grosorDisco}cm")
        Log.d("Formula1Consistente", "📐 MODELO: [Corte1][Grosor][Corte2][Grosor]...[CorteN][Retazo]")
        Log.d("Formula1Consistente", "Varillas: ${varillasConCantidad.map { "${it.longitud}=${it.cantidad}" }}")
        Log.d("Formula1Consistente", "Cortes: ${cortesConCantidad.map { "${it.longitud}=${it.cantidad}" }}")

        while (cortesConCantidad.any { it.cantidad > 0 } && varillasConCantidad.any { it.cantidad > 0 }) {
            iteracion++
            val tiempoInicio = System.currentTimeMillis()

            Log.d("Formula1Consistente", "\n--- ITERACIÓN $iteracion ---")

            // PASO 1: Tomar varilla menor disponible
            val varillaMinor = varillasConCantidad.firstOrNull { it.cantidad > 0 }
            if (varillaMinor == null) {
                Log.d("Formula1Consistente", "❌ No hay más varillas")
                break
            }

            // PASO 2: Tomar corte mayor disponible
            val corteMayor = cortesConCantidad.firstOrNull { it.cantidad > 0 }
            if (corteMayor == null) {
                Log.d("Formula1Consistente", "❌ No hay más cortes")
                break
            }

            Log.d("Formula1Consistente", "📏 Varilla menor: ${varillaMinor.longitud}cm")
            Log.d("Formula1Consistente", "✂️ Corte mayor: ${corteMayor.longitud}cm")

            // PASO 3: Verificar si corte cabe en varilla menor
            var varillaSeleccionada = varillaMinor

            // 🎯 VERIFICACIÓN CONSISTENTE: Un solo corte necesita su espacio + grosor mínimo
            if (!cabeEnVarilla(listOf(corteMayor), varillaMinor.longitud, grosorDisco)) {
                Log.d("Formula1Consistente", "⚠️ Corte ${corteMayor.longitud}cm no cabe en varilla ${varillaMinor.longitud}cm")

                // Buscar siguiente varilla menor que quepa
                val varillaQueQuepa = varillasConCantidad.firstOrNull {
                    it.cantidad > 0 && cabeEnVarilla(listOf(corteMayor), it.longitud, grosorDisco)
                }

                if (varillaQueQuepa == null) {
                    Log.d("Formula1Consistente", "❌ No hay varillas para corte ${corteMayor.longitud}cm")
                    corteMayor.cantidad--
                    if (corteMayor.cantidad <= 0) {
                        cortesConCantidad.remove(corteMayor)
                    }
                    continue
                }

                varillaSeleccionada = varillaQueQuepa
                Log.d("Formula1Consistente", "✅ Siguiente varilla que quepa: ${varillaSeleccionada.longitud}cm")
            }

            // PASO 4: Generar opciones de llenado
            val opcionesLlenado = generarOpcionesLlenadoBacktrack(
                corteMayor,
                cortesConCantidad.filter { it.cantidad > 0 },
                varillaSeleccionada.longitud,
                grosorDisco,
                MAX_OPCIONES_LLENADO,
                TIMEOUT_ITERACION_MS,
                nivel
            )

            if (opcionesLlenado.isEmpty()) {
                Log.d("Formula1Consistente", "❌ No se pueden generar opciones de llenado")
                varillaSeleccionada.cantidad--
                if (varillaSeleccionada.cantidad <= 0) {
                    varillasConCantidad.remove(varillaSeleccionada)
                }
                continue
            }

            Log.d("Formula1Consistente", "🔍 ${opcionesLlenado.size} opciones válidas generadas")

            val opcionesValidas = opcionesLlenado.mapNotNull {
                normalizarOpcionLlenado(it, varillaSeleccionada.longitud, grosorDisco)
            }

            if (opcionesValidas.isEmpty()) {
                Log.d("Formula1Consistente", "No quedan opciones que quepan tras recalcular retazo")
                varillaSeleccionada.cantidad--
                if (varillaSeleccionada.cantidad <= 0) {
                    varillasConCantidad.remove(varillaSeleccionada)
                }
                continue
            }

            // PASO 5: Evaluar opciones contra condicionantes
            var opcionSeleccionada: OpcionLlenadoFormula1? = null
            var condicionanteUsado = "SIN RESTRICCIÓN"

            for ((index, limite) in condicionantes.withIndex()) {
                opcionSeleccionada = opcionesValidas.firstOrNull { it.retazo <= limite }

                if (opcionSeleccionada != null) {
                    condicionanteUsado = "CONDICIONANTE ${index + 1} (≤${limite}cm)"
                    break
                }
            }

            // Si no cumple condicionantes, usar la mejor opción
            if (opcionSeleccionada == null) {
                opcionSeleccionada = opcionesValidas.minByOrNull { it.retazo }
                condicionanteUsado = "MEJOR OPCIÓN (sin restricción)"
            }

            if (opcionSeleccionada == null) {
                Log.d("Formula1Consistente", "❌ No hay opciones válidas")
                break
            }

            // PASO 6: Crear varilla resultado
            val nuevaVarilla = crearVarillaConsistente(
                varillaSeleccionada,
                opcionSeleccionada
            )

            resultado.add(nuevaVarilla)

            // PASO 7: Descontar cantidades
            for (corteUsado in opcionSeleccionada.cortesUsados) {
                val corteEnLista = cortesConCantidad.find {
                    it.longitud == corteUsado.longitud && it.referencia == corteUsado.referencia
                }
                corteEnLista?.cantidad = (corteEnLista?.cantidad ?: 0) - 1
            }

            varillaSeleccionada.cantidad--
            if (varillaSeleccionada.cantidad <= 0) {
                varillasConCantidad.remove(varillaSeleccionada)
            }
            cortesConCantidad.removeAll { it.cantidad <= 0 }

            Log.d("Formula1Consistente", "✅ Varilla asignada:")
            Log.d("Formula1Consistente", "   Criterio: $condicionanteUsado")
            Log.d("Formula1Consistente", "   Cortes: ${opcionSeleccionada.cortesUsados.map { "${it.longitud}(${it.referencia})" }}")
            Log.d("Formula1Consistente", "   Retazo: ${String.format("%.2f", opcionSeleccionada.retazo)}cm")

            // Timeout de seguridad
            if (System.currentTimeMillis() - tiempoInicio > 15000) {
                Log.d("Formula1Consistente", "⏱️ Timeout general alcanzado")
                break
            }
        }

        val eficiencia = calcularEficiencia(resultado)
        val retazos = resultado.sumOf { it.varilla.restante.toDouble() }.toFloat()

        Log.d("Formula1Consistente", "\n=== RESULTADO FINAL ===")
        Log.d("Formula1Consistente", "✅ Eficiencia: ${String.format("%.2f", eficiencia)}%")
        Log.d("Formula1Consistente", "✅ Retazos: ${String.format("%.1f", retazos)}cm")
        Log.d("Formula1Consistente", "✅ Varillas: ${resultado.size}")

        return ResultadoInterno(
            resultado, eficiencia, retazos,
            "Fórmula 1 Modelo Consistente", iteracion
        )
    }

    private fun normalizarOpcionLlenado(
        opcion: OpcionLlenadoFormula1,
        longitudVarilla: Float,
        grosorDisco: Float
    ): OpcionLlenadoFormula1? {
        val retazoRecalculado = calcularRetazo(opcion.cortesUsados, longitudVarilla, grosorDisco)
        return when {
            retazoRecalculado < -0.001f -> {
                Log.w(
                    "Formula1Consistente",
                    "Opcion descartada por retazo negativo: ${String.format("%.3f", retazoRecalculado)}cm"
                )
                null
            }
            else -> opcion.copy(retazo = retazoRecalculado.coerceAtLeast(0f))
        }
    }

    /**
     * Genera opciones de llenado usando backtracking con presupuesto controlado por nivel.
     * Explora todas las combinaciones válidas hasta agotar nodos o tiempo.
     * Devuelve las mejores (menor retazo) hasta maxOpciones.
     */
    private fun generarOpcionesLlenadoBacktrack(
        corteMayorObligatorio: CorteConCantidad,
        cortesDisponibles: List<CorteConCantidad>,
        longitudVarilla: Float,
        grosorDisco: Float,
        maxOpciones: Int,
        timeoutMs: Long,
        nivel: Int
    ): List<OpcionLlenadoFormula1> {

        if (!cabeEnVarilla(listOf(corteMayorObligatorio), longitudVarilla, grosorDisco)) {
            Log.e("Backtrack", "Corte ${corteMayorObligatorio.longitud}cm no cabe en varilla ${longitudVarilla}cm")
            return emptyList()
        }

        val espacioInicial = longitudVarilla -
            calcularEspacioTotalNecesario(listOf(corteMayorObligatorio), grosorDisco)

        val resultados = mutableListOf<OpcionLlenadoFormula1>()
        resultados.add(OpcionLlenadoFormula1(
            listOf(corteMayorObligatorio),
            espacioInicial,
            "SOLO"
        ))

        // Nodos máximos a explorar según nivel: 1→200, 10→2000, 20→4000
        val maxNodos = nivel * 200
        val tiempoFin = System.currentTimeMillis() + timeoutMs
        val contador = intArrayOf(0)

        // El mapa de usados arranca con el corte obligatorio ya consumido
        val usados = HashMap<String, Int>()
        usados["${corteMayorObligatorio.longitud}_${corteMayorObligatorio.referencia}"] = 1

        val cortesOrdenados = cortesDisponibles.sortedByDescending { it.longitud }
        val cortesActuales = mutableListOf(corteMayorObligatorio)

        buscarOpcionesBacktrack(
            cortesActuales, usados, espacioInicial,
            cortesOrdenados, 0,
            grosorDisco, longitudVarilla,
            resultados, contador, maxNodos, tiempoFin
        )

        Log.d("Backtrack", "nivel=$nivel nodos=${contador[0]} opciones=${resultados.size}")

        // Devolver las maxOpciones con menor retazo
        return resultados.sortedBy { it.retazo }.take(maxOpciones)
    }

    /**
     * DFS con poda: agrega cortes a cortesActuales respetando cantidades.
     * "desde" evita duplicados (solo avanzamos en el índice, no retrocedemos).
     */
    private fun buscarOpcionesBacktrack(
        cortesActuales: MutableList<CorteConCantidad>,
        usados: HashMap<String, Int>,
        espacioRestante: Float,
        cortesOrdenados: List<CorteConCantidad>,
        desde: Int,
        grosorDisco: Float,
        longitudVarilla: Float,
        resultados: MutableList<OpcionLlenadoFormula1>,
        contador: IntArray,
        maxNodos: Int,
        tiempoFin: Long
    ) {
        if (contador[0] >= maxNodos || System.currentTimeMillis() > tiempoFin) return

        for (i in desde until cortesOrdenados.size) {
            val corte = cortesOrdenados[i]
            if (corte.cantidad <= 0) continue

            val key = "${corte.longitud}_${corte.referencia}"
            val vecesUsado = usados.getOrDefault(key, 0)
            if (vecesUsado >= corte.cantidad) continue

            // grosorDisco adicional por el nuevo corte
            val espacioNecesario = corte.longitud + grosorDisco
            if (espacioNecesario > espacioRestante + 0.001f) continue

            contador[0]++

            cortesActuales.add(corte)
            usados[key] = vecesUsado + 1
            val nuevoEspacio = espacioRestante - espacioNecesario

            val retazo = longitudVarilla -
                calcularEspacioTotalNecesario(cortesActuales, grosorDisco)
            resultados.add(OpcionLlenadoFormula1(cortesActuales.toList(), retazo, "BT"))

            buscarOpcionesBacktrack(
                cortesActuales, usados, nuevoEspacio,
                cortesOrdenados, i,   // desde=i permite reutilizar mismo tipo (multi-cantidad)
                grosorDisco, longitudVarilla,
                resultados, contador, maxNodos, tiempoFin
            )

            cortesActuales.removeAt(cortesActuales.size - 1)
            usados[key] = vecesUsado
        }
    }

    /**
     * 🔧 CREA VARILLA RESULTADO CON MODELO CONSISTENTE
     */
    private fun crearVarillaConsistente(
        varillaSeleccionada: VarillaConCantidad,
        opcionSeleccionada: OpcionLlenadoFormula1
    ): VarillaConReferencias {

        // Usar el retazo ya calculado correctamente en la opción
        val retazoCalculado = opcionSeleccionada.retazo.coerceAtLeast(0f)

        Log.d("CrearVarillaConsistente", "📏 CREANDO VARILLA:")
        Log.d("CrearVarillaConsistente", "   Longitud: ${varillaSeleccionada.longitud}cm")
        Log.d("CrearVarillaConsistente", "   Cortes: ${opcionSeleccionada.cortesUsados.map { "${it.longitud}(${it.referencia})" }}")
        Log.d("CrearVarillaConsistente", "   Retazo: ${String.format("%.2f", retazoCalculado)}cm")

        // VALIDACIÓN FINAL
        if (opcionSeleccionada.retazo < -0.001f) {
            Log.e("CrearVarillaConsistente", "❌ ERROR CRÍTICO: Retazo negativo ${retazoCalculado}cm")
            throw Exception("Error crítico: retazo negativo calculado")
        }

        val nuevaVarilla = VarillaConReferencias(
            Varilla(varillaSeleccionada.longitud, mutableListOf(), retazoCalculado),
            mutableListOf()
        )

        for (corte in opcionSeleccionada.cortesUsados) {
            nuevaVarilla.varilla.cortes.add(corte.longitud)
            nuevaVarilla.referencias.add(corte.referencia)
        }

        return nuevaVarilla
    }

    // ========== FUNCIONES AUXILIARES NECESARIAS - ACTIVAS ==========

    private fun calcularEficiencia(varillasUsadas: List<VarillaConReferencias>): Float {
        if (varillasUsadas.isEmpty()) return 0f

        val totalUsado = varillasUsadas.sumOf { (it.varilla.longitud - it.varilla.restante).toDouble() }
        val totalDisponible = varillasUsadas.sumOf { it.varilla.longitud.toDouble() }

        return ((totalUsado / totalDisponible) * 100).toFloat()
    }

    /**
     * Genera el resultado optimizado en formato estructurado
     */
    fun generarResultadoEstructurado(
        varillasUsadas: List<VarillaConReferencias>
    ): ResultadoOptimizacion {

        val varillasResultado = mutableListOf<VarillaResultado>()
        var totalCortes = 0

        varillasUsadas.forEach { varillaConRef ->
            val varilla = varillaConRef.varilla
            val referencias = varillaConRef.referencias

            if (varilla.cortes.size != referencias.size) {
                throw Exception("Error interno: cortes y referencias no coinciden")
            }

            val cortesConReferencias = varilla.cortes.zip(referencias) { corte, referencia ->
                CorteConReferencia(corte, referencia)
            }

            val retazo = varilla.restante
            val porcentajeRetazo = (retazo / varilla.longitud) * 100f

            totalCortes += varilla.cortes.size

            val varillaResultado = VarillaResultado(
                longitudVarilla = varilla.longitud,
                cantidadVarillas = 1,
                cortes = varilla.cortes,
                cortesConReferencias = cortesConReferencias,
                retazo = retazo,
                porcentajeRetazo = porcentajeRetazo,
                cortada = false
            )

            varillasResultado.add(varillaResultado)
        }

        return ResultadoOptimizacion(
            varillasUsadas = varillasResultado,
            totalBarrasUsadas = varillasUsadas.size,
            totalCortes = totalCortes,
            cortesErroneos = 0
        )
    }

    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }
}
