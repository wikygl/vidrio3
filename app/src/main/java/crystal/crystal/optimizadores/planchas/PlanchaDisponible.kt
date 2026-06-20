package crystal.crystal.optimizadores.planchas

data class PlanchaDisponible(
    val ancho: Float,
    val alto: Float,
    val cantidad: Int,
    val nombre: String,
    var activa: Boolean = true
)