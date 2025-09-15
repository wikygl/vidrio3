package crystal.crystal.optimizadores.corte

/**
 * Configuraciones para ajustar el comportamiento de la optimización
 */
object ConfiguracionOptimizacion {

    // Límites de combinaciones para evitar explosión computacional
    const val MAX_COMBINACIONES_POR_VARILLA = 100
    const val MAX_PIEZAS_POR_COMBINACION = 4

    // Iteraciones del algoritmo genético
    const val ITERACIONES_GENETICO = 10

    // Umbrales de eficiencia
    private const val EFICIENCIA_ALTA = 90f
    private const val EFICIENCIA_MEDIA = 75f

    // Configuración de timeouts
    const val TIMEOUT_ALGORITMO_MS = 5000L // 5 segundos máximo por algoritmo

    // Pesos para la función de evaluación (suma debe ser 1.0)
    private const val PESO_EFICIENCIA = 0.7f
    private const val PESO_NUMERO_VARILLAS = 0.2f
    private const val PESO_DISTRIBUCION_RETAZOS = 0.1f

    /**
     * Función de evaluación ponderada que considera múltiples factores
     */
    fun evaluarSolucion(
        eficiencia: Float,
        numeroVarillas: Int,
        varillasOriginales: Int,
        retazos: List<Float>
    ): Float {
        // Normalizar número de varillas (menos es mejor)
        val factorVarillas = 1f - (numeroVarillas.toFloat() / varillasOriginales.toFloat())

        // Evaluar distribución de retazos (retazos más uniformes son mejores)
        val promedioRetazos = retazos.average().toFloat()
        val varianzaRetazos = retazos.map { (it - promedioRetazos) * (it - promedioRetazos) }.average().toFloat()
        val factorDistribucion = 1f / (1f + varianzaRetazos / 100f) // Normalizar

        return (eficiencia / 100f) * PESO_EFICIENCIA +
                factorVarillas * PESO_NUMERO_VARILLAS +
                factorDistribucion * PESO_DISTRIBUCION_RETAZOS
    }

    /**
     * Determina si una solución es aceptable
     */
    fun esSolucionAceptable(eficiencia: Float): Boolean {
        return eficiencia >= EFICIENCIA_MEDIA
    }

    /**
     * Categoriza la calidad de una solución
     */
    fun categorizarSolucion(eficiencia: Float): String {
        return when {
            eficiencia >= 95f -> "EXCELENTE"
            eficiencia >= EFICIENCIA_ALTA -> "MUY BUENA"
            eficiencia >= 85f -> "BUENA"
            eficiencia >= EFICIENCIA_MEDIA -> "ACEPTABLE"
            else -> "NECESITA MEJORA"
        }
    }
}