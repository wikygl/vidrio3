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
     */
    fun optimizarCortesConConfiguracion(
        piezasRequeridas: List<PiezaCorte>,
        varillasDisponibles: List<PiezaCorte>,
        grosorDisco: Float
    ): List<VarillaConReferencias> {

        val tiempoInicio = System.currentTimeMillis()
        DebugHelper.logInicioOptimizacionInteligente()

        Log.d("CorteOptimizer", "🚀 FÓRMULA 1 + MANEJO DE CORTES LARGOS + REDISTRIBUCIÓN")
        Log.d("CorteOptimizer", "🔧 GROSOR DISCO: ${grosorDisco}cm (MODELO CONSISTENTE)")

        // PASO NUEVO: Detectar y manejar cortes más largos que varillas disponibles
        val (piezasAjustadas, varillasCompletas) = manejarCortesLargos(piezasRequeridas, varillasDisponibles, grosorDisco)

        Log.d("CortesLargos", "Piezas después del tratamiento: ${piezasAjustadas.size}")
        Log.d("CortesLargos", "Varillas completas generadas: ${varillasCompletas.size}")

        // PASO 1: Aplicar Fórmula 1 con las piezas ajustadas
        val resultadoInicial = formula1VarillaMinimaCorteMayor(piezasAjustadas, varillasDisponibles, grosorDisco)

        // PASO 2: Combinar varillas completas + resultado de optimización
        val resultadoCombinado = combinarResultados(varillasCompletas, resultadoInicial.varillasUsadas)

        Log.d("Redistribucion", "\n=== INICIANDO POST-PROCESAMIENTO ===")
        Log.d("Redistribucion", "Resultado combinado: ${resultadoCombinado.size} varillas")

        // PASO 3: Redistribuir cortes para optimizar retazos
        val resultadoOptimizado = redistribuirCortesInteligente(
            resultadoCombinado.toMutableList(),
            grosorDisco
        )

        val tiempoTotal = System.currentTimeMillis() - tiempoInicio
        Log.d("CorteOptimizer", "✅ Optimización completada en ${tiempoTotal}ms")

        return resultadoOptimizado
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

    /**
     * FUNCIÓN COMPLETAMENTE NUEVA: Post-procesamiento directo y simple
     */
    private fun redistribuirCortesInteligente(
        varillasOriginales: MutableList<VarillaConReferencias>,
        grosorDisco: Float
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

        } while (huboEliminacion && intentos < 10)

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

    /**
     * FUNCIÓN ORIGINAL - COMENTADA para mantener compatibilidad
     */
    fun optimizarCortes(
        piezasRequeridas: List<PiezaCorte>,
        varillasDisponibles: List<PiezaCorte>,
        grosorDisco: Float
    ): List<VarillaConReferencias> {
        // Redirigir a la función con configuración
        return optimizarCortesConConfiguracion(piezasRequeridas, varillasDisponibles, grosorDisco)
    }

    // ========== FÓRMULA 1 ESPECÍFICA MEJORADA - ACTIVA ==========

    /**
     * 🔧 FÓRMULA 1 COMPLETAMENTE REESCRITA CON MODELO CONSISTENTE
     */
    private fun formula1VarillaMinimaCorteMayor(
        piezasRequeridas: List<PiezaCorte>,
        varillasDisponibles: List<PiezaCorte>,
        grosorDisco: Float
    ): ResultadoInterno {

        val varillasConCantidad = varillasDisponibles.map {
            VarillaConCantidad(it.longitud, it.cantidad, it.referencia)
        }.sortedBy { it.longitud }.toMutableList()

        val cortesConCantidad = piezasRequeridas.map {
            CorteConCantidad(it.longitud, it.cantidad, it.referencia)
        }.sortedByDescending { it.longitud }.toMutableList()

        val resultado = mutableListOf<VarillaConReferencias>()
        var iteracion = 0

        // CONFIGURACIÓN DE OPTIMIZACIÓN
        val MAX_OPCIONES_LLENADO = 15
        val TIMEOUT_ITERACION_MS = 2000L
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
            val opcionesLlenado = generarOpcionesLlenadoConsistente(
                corteMayor,
                cortesConCantidad.filter { it.cantidad > 0 },
                varillaSeleccionada.longitud,
                grosorDisco,
                MAX_OPCIONES_LLENADO,
                TIMEOUT_ITERACION_MS
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

            // PASO 5: Evaluar opciones contra condicionantes
            var opcionSeleccionada: OpcionLlenadoFormula1? = null
            var condicionanteUsado = "SIN RESTRICCIÓN"

            for ((index, limite) in condicionantes.withIndex()) {
                opcionSeleccionada = opcionesLlenado.firstOrNull { it.retazo <= limite }

                if (opcionSeleccionada != null) {
                    condicionanteUsado = "CONDICIONANTE ${index + 1} (≤${limite}cm)"
                    break
                }
            }

            // Si no cumple condicionantes, usar la mejor opción
            if (opcionSeleccionada == null) {
                opcionSeleccionada = opcionesLlenado.minByOrNull { it.retazo }
                condicionanteUsado = "MEJOR OPCIÓN (sin restricción)"
            }

            if (opcionSeleccionada == null) {
                Log.d("Formula1Consistente", "❌ No hay opciones válidas")
                break
            }

            // PASO 6: Crear varilla resultado
            val nuevaVarilla = crearVarillaConsistente(
                varillaSeleccionada,
                opcionSeleccionada,
                grosorDisco
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

    /**
     * 🔧 GENERA OPCIONES DE LLENADO CON MODELO CONSISTENTE
     */
    private fun generarOpcionesLlenadoConsistente(
        corteMayorObligatorio: CorteConCantidad,
        cortesDisponibles: List<CorteConCantidad>,
        longitudVarilla: Float,
        grosorDisco: Float,
        maxOpciones: Int,
        timeoutMs: Long
    ): List<OpcionLlenadoFormula1> {

        val tiempoInicio = System.currentTimeMillis()
        val opciones = mutableListOf<OpcionLlenadoFormula1>()

        // OPCIÓN 1: Solo el corte mayor (siempre incluir si es válida)
        val opcionSolo = listOf(corteMayorObligatorio)
        if (cabeEnVarilla(opcionSolo, longitudVarilla, grosorDisco)) {
            val retazoSolo = calcularRetazo(opcionSolo, longitudVarilla, grosorDisco)
            opciones.add(OpcionLlenadoFormula1(
                cortesUsados = opcionSolo,
                retazo = retazoSolo,
                metodologia = "SOLO CORTE MAYOR"
            ))
            Log.d("Formula1Debug", "✅ Opción solo corte: retazo ${String.format("%.2f", retazoSolo)}cm")
        } else {
            Log.e("Formula1Error", "❌ El corte mayor ${corteMayorObligatorio.longitud}cm no cabe en varilla ${longitudVarilla}cm")
            return emptyList()
        }

        // OPCIONES 2+: Agregar más cortes greedily
        val cortesOrdenados = cortesDisponibles.filter {
            it.longitud != corteMayorObligatorio.longitud || it.referencia != corteMayorObligatorio.referencia
        }.sortedByDescending { it.longitud }

        for (startIndex in 0 until minOf(8, cortesOrdenados.size)) {
            if (System.currentTimeMillis() - tiempoInicio > timeoutMs) break
            if (opciones.size >= maxOpciones) break

            val cortesUsados = mutableListOf(corteMayorObligatorio)

            // Agregar cortes greedily desde startIndex
            for (i in startIndex until cortesOrdenados.size) {
                val corte = cortesOrdenados[i]
                if (corte.cantidad <= 0) continue

                // Verificar disponibilidad
                val vecesUsado = cortesUsados.count {
                    it.longitud == corte.longitud && it.referencia == corte.referencia
                }

                if (vecesUsado >= corte.cantidad) continue

                // Probar agregar este corte
                val cortesConNuevo = cortesUsados + corte

                if (cabeEnVarilla(cortesConNuevo, longitudVarilla, grosorDisco)) {
                    cortesUsados.add(corte)
                    Log.d("Formula1Debug", "   ✅ Agregado: ${corte.longitud}cm")
                } else {
                    Log.d("Formula1Debug", "   ❌ No cabe: ${corte.longitud}cm")
                }
            }

            if (cortesUsados.size > 1) {
                val retazoCalculado = calcularRetazo(cortesUsados, longitudVarilla, grosorDisco)

                Log.d("Formula1Debug", "🎯 Opción desde índice $startIndex:")
                Log.d("Formula1Debug", "   Cortes: ${cortesUsados.map { "${it.longitud}(${it.referencia})" }}")
                Log.d("Formula1Debug", "   Retazo: ${String.format("%.2f", retazoCalculado)}cm")

                opciones.add(OpcionLlenadoFormula1(
                    cortesUsados = cortesUsados.toList(),
                    retazo = retazoCalculado,
                    metodologia = "GREEDY DESDE ÍNDICE $startIndex"
                ))
            }
        }

        Log.d("Formula1Debug", "📊 ${opciones.size} opciones válidas generadas")
        return opciones.take(maxOpciones)
    }

    /**
     * 🔧 CREA VARILLA RESULTADO CON MODELO CONSISTENTE
     */
    private fun crearVarillaConsistente(
        varillaSeleccionada: VarillaConCantidad,
        opcionSeleccionada: OpcionLlenadoFormula1,
        grosorDisco: Float
    ): VarillaConReferencias {

        // Usar el retazo ya calculado correctamente en la opción
        val retazoCalculado = opcionSeleccionada.retazo

        Log.d("CrearVarillaConsistente", "📏 CREANDO VARILLA:")
        Log.d("CrearVarillaConsistente", "   Longitud: ${varillaSeleccionada.longitud}cm")
        Log.d("CrearVarillaConsistente", "   Cortes: ${opcionSeleccionada.cortesUsados.map { "${it.longitud}(${it.referencia})" }}")
        Log.d("CrearVarillaConsistente", "   Retazo: ${String.format("%.2f", retazoCalculado)}cm")

        // VALIDACIÓN FINAL
        if (retazoCalculado < 0) {
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