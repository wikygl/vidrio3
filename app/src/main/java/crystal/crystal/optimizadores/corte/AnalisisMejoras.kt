package crystal.crystal.optimizadores.corte

/**
 * Clase para analizar y comparar mejoras en la optimización* */

object AnalisisMejoras {

    data class EstadisticasOptimizacion(
        val algoritmoUsado: String,
        val eficienciaTotal: Float,
        val totalRetazos: Float,
        val varillasUsadas: Int,
        val promedioEficienciaPorVarilla: Float,
        val retazoMenor: Float,
        val retazoMayor: Float,
        val varillasConEficienciaAlta: Int, // >90%
        val varillasConEficienciaMedia: Int, // 75-90%
        val varillasConEficienciaBaja: Int // <75%
    )

    /**
     * Analiza los resultados de optimización y genera estadísticas detalladas
     */
    fun analizarResultados(resultado: CorteOptimizer.ResultadoAlgoritmo): EstadisticasOptimizacion {
        val varillasUsadas = resultado.varillasUsadas

        if (varillasUsadas.isEmpty()) {
            return EstadisticasOptimizacion(
                algoritmoUsado = resultado.nombreAlgoritmo,
                eficienciaTotal = 0f,
                totalRetazos = 0f,
                varillasUsadas = 0,
                promedioEficienciaPorVarilla = 0f,
                retazoMenor = 0f,
                retazoMayor = 0f,
                varillasConEficienciaAlta = 0,
                varillasConEficienciaMedia = 0,
                varillasConEficienciaBaja = 0
            )
        }

        // Calcular eficiencias por varilla
        val eficienciasPorVarilla = varillasUsadas.map { varilla ->
            val longitudUsada = varilla.varilla.longitud - varilla.varilla.restante
            (longitudUsada / varilla.varilla.longitud) * 100f
        }

        // Categorizar varillas por eficiencia
        val eficienciaAlta = eficienciasPorVarilla.count { it >= 90f }
        val eficienciaMedia = eficienciasPorVarilla.count { it in 75f..89.99f }
        val eficienciaBaja = eficienciasPorVarilla.count { it < 75f }

        // Análisis de retazos
        val retazos = varillasUsadas.map { it.varilla.restante }
        val retazoMenor = retazos.minOrNull() ?: 0f
        val retazoMayor = retazos.maxOrNull() ?: 0f

        return EstadisticasOptimizacion(
            algoritmoUsado = resultado.nombreAlgoritmo,
            eficienciaTotal = resultado.porcentajeEficiencia,
            totalRetazos = resultado.totalRetazos,
            varillasUsadas = varillasUsadas.size,
            promedioEficienciaPorVarilla = eficienciasPorVarilla.average().toFloat(),
            retazoMenor = retazoMenor,
            retazoMayor = retazoMayor,
            varillasConEficienciaAlta = eficienciaAlta,
            varillasConEficienciaMedia = eficienciaMedia,
            varillasConEficienciaBaja = eficienciaBaja
        )
    }

    /**
     * Compara dos resultados de optimización
     */
    fun compararResultados(anterior: EstadisticasOptimizacion, nuevo: EstadisticasOptimizacion): String {
        val mejoras = mutableListOf<String>()
        val empeoramientos = mutableListOf<String>()

        // Comparar eficiencia total
        val diferenciaEficiencia = nuevo.eficienciaTotal - anterior.eficienciaTotal
        if (diferenciaEficiencia > 0.1f) {
            mejoras.add("Eficiencia mejoró ${String.format("%.2f", diferenciaEficiencia)}%")
        } else if (diferenciaEficiencia < -0.1f) {
            empeoramientos.add("Eficiencia empeoró ${String.format("%.2f", kotlin.math.abs(diferenciaEficiencia))}%")
        }

        // Comparar retazos totales
        val diferenciaRetazos = anterior.totalRetazos - nuevo.totalRetazos
        if (diferenciaRetazos > 1f) {
            mejoras.add("Retazos reducidos en ${String.format("%.1f", diferenciaRetazos)}cm")
        } else if (diferenciaRetazos < -1f) {
            empeoramientos.add("Retazos aumentaron en ${String.format("%.1f", kotlin.math.abs(diferenciaRetazos))}cm")
        }

        // Comparar cantidad de varillas
        val diferenciaVarillas = anterior.varillasUsadas - nuevo.varillasUsadas
        if (diferenciaVarillas > 0) {
            mejoras.add("${diferenciaVarillas} varilla(s) menos utilizadas")
        } else if (diferenciaVarillas < 0) {
            empeoramientos.add("${kotlin.math.abs(diferenciaVarillas)} varilla(s) adicionales necesarias")
        }

        // Comparar distribución de eficiencias
        val mejorDistribucion = nuevo.varillasConEficienciaAlta > anterior.varillasConEficienciaAlta
        if (mejorDistribucion) {
            val diferencia = nuevo.varillasConEficienciaAlta - anterior.varillasConEficienciaAlta
            mejoras.add("$diferencia varilla(s) adicionales con eficiencia >90%")
        }

        // Generar reporte
        val reporte = StringBuilder()
        reporte.append("🔍 COMPARACIÓN DE ALGORITMOS:\n")
        reporte.append("Anterior: ${anterior.algoritmoUsado}\n")
        reporte.append("Nuevo: ${nuevo.algoritmoUsado}\n\n")

        if (mejoras.isNotEmpty()) {
            reporte.append("✅ MEJORAS:\n")
            mejoras.forEach { reporte.append("• $it\n") }
            reporte.append("\n")
        }

        if (empeoramientos.isNotEmpty()) {
            reporte.append("⚠️ EMPEORAMIENTOS:\n")
            empeoramientos.forEach { reporte.append("• $it\n") }
            reporte.append("\n")
        }

        if (mejoras.isEmpty() && empeoramientos.isEmpty()) {
            reporte.append("➡️ Resultados similares\n\n")
        }

        // Estadísticas detalladas
        reporte.append("📊 ESTADÍSTICAS DETALLADAS:\n")
        reporte.append("Eficiencia: ${String.format("%.2f", anterior.eficienciaTotal)}% → ${String.format("%.2f", nuevo.eficienciaTotal)}%\n")
        reporte.append("Retazos: ${String.format("%.1f", anterior.totalRetazos)}cm → ${String.format("%.1f", nuevo.totalRetazos)}cm\n")
        reporte.append("Varillas: ${anterior.varillasUsadas} → ${nuevo.varillasUsadas}\n")

        return reporte.toString()
    }

    /**
     * Genera un reporte detallado de un resultado
     */
    fun generarReporteDetallado(estadisticas: EstadisticasOptimizacion): String {
        val reporte = StringBuilder()

        reporte.append("📈 REPORTE DE OPTIMIZACIÓN\n")
        reporte.append("Algoritmo: ${estadisticas.algoritmoUsado}\n\n")

        reporte.append("🎯 EFICIENCIA GLOBAL:\n")
        reporte.append("• Total: ${String.format("%.2f", estadisticas.eficienciaTotal)}%\n")
        reporte.append("• Promedio por varilla: ${String.format("%.2f", estadisticas.promedioEficienciaPorVarilla)}%\n\n")

        reporte.append("🔢 ESTADÍSTICAS DE VARILLAS:\n")
        reporte.append("• Total utilizadas: ${estadisticas.varillasUsadas}\n")
        reporte.append("• Eficiencia alta (>90%): ${estadisticas.varillasConEficienciaAlta}\n")
        reporte.append("• Eficiencia media (75-90%): ${estadisticas.varillasConEficienciaMedia}\n")
        reporte.append("• Eficiencia baja (<75%): ${estadisticas.varillasConEficienciaBaja}\n\n")

        reporte.append("♻️ ANÁLISIS DE RETAZOS:\n")
        reporte.append("• Total: ${String.format("%.1f", estadisticas.totalRetazos)}cm\n")
        reporte.append("• Retazo menor: ${String.format("%.1f", estadisticas.retazoMenor)}cm\n")
        reporte.append("• Retazo mayor: ${String.format("%.1f", estadisticas.retazoMayor)}cm\n")

        // Evaluación de calidad
        val evaluacion = when {
            estadisticas.eficienciaTotal >= 95f -> "🏆 EXCELENTE"
            estadisticas.eficienciaTotal >= 90f -> "🥇 MUY BUENA"
            estadisticas.eficienciaTotal >= 85f -> "🥈 BUENA"
            estadisticas.eficienciaTotal >= 80f -> "🥉 REGULAR"
            else -> "⚠️ MEJORABLE"
        }

        reporte.append("\n🏅 EVALUACIÓN: $evaluacion\n")

        return reporte.toString()
    }

    /**
     * Calcula el ahorro económico estimado
     */
    fun calcularAhorroEconomico(
        anterior: EstadisticasOptimizacion,
        nuevo: EstadisticasOptimizacion,
        costoPorVarilla: Float = 100f // Costo promedio por varilla
    ): String {
        val varillasAhorradas = anterior.varillasUsadas - nuevo.varillasUsadas
        val ahorroEnVarillas = varillasAhorradas * costoPorVarilla

        val materialAhorrado = anterior.totalRetazos - nuevo.totalRetazos
        val porcentajeMaterialAhorrado = if (anterior.totalRetazos > 0) {
            (materialAhorrado / anterior.totalRetazos) * 100f
        } else 0f

        return buildString {
            append("💰 ANÁLISIS ECONÓMICO:\n")
            if (varillasAhorradas > 0) {
                append("• Varillas ahorradas: $varillasAhorradas\n")
                append("• Ahorro estimado: $${String.format("%.2f", ahorroEnVarillas)}\n")
            }
            if (materialAhorrado > 0) {
                append("• Material ahorrado: ${String.format("%.1f", materialAhorrado)}cm\n")
                append("• Reducción desperdicio: ${String.format("%.1f", porcentajeMaterialAhorrado)}%\n")
            }
            if (varillasAhorradas <= 0 && materialAhorrado <= 0) {
                append("• Sin ahorros significativos en esta optimización\n")
            }
        }
    }
}