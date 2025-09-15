package crystal.crystal.optimizadores.corte

/**
 * Data class para reemplazar Triple y agregar estado de cortado
 */
data class PiezaCorte(
    val longitud: Float,
    val cantidad: Int,
    val referencia: String,
    var cortada: Boolean = true // true = entra en optimización, false = no entra
)