package crystal.crystal.Diseno.vitroven

data class ParametrosVitroven(
    val anchoTotalCm: Float,
    val altoTotalCm: Float,
    val clips: Int,
    val clasificacion: String = "v",
    val disenoSimbolico: String = "",
    val direccionVertical: Boolean = false
)
