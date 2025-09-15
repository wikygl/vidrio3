package crystal.crystal.optimizadores.corte

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Modelo de datos para pasar resultados entre Activities
 */
@Parcelize
data class ResultadoOptimizacion(
    val varillasUsadas: List<VarillaResultado>,
    val totalBarrasUsadas: Int,
    val totalCortes: Int,
    val cortesErroneos: Int = 0
) : Parcelable

@Parcelize
data class VarillaResultado(
    val longitudVarilla: Float,
    val cantidadVarillas: Int, // Cuántas varillas de esta medida se usaron
    val cortes: List<Float>,
    val cortesConReferencias: List<CorteConReferencia>, // NUEVO: cortes con sus referencias
    val retazo: Float,
    val porcentajeRetazo: Float,
    var cortada: Boolean = false // NUEVO: solo para cambiar el background
) : Parcelable {

    /**
     * Calcula el porcentaje de material utilizado
     */
    val porcentajeUtilizado: Float
        get() = 100f - porcentajeRetazo

    /**
     * Calcula la longitud total utilizada
     */
    val longitudUtilizada: Float
        get() = cortes.sum()
}

@Parcelize
data class CorteConReferencia(
    val longitud: Float,
    val referencia: String
) : Parcelable