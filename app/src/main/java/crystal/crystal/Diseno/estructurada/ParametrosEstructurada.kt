package crystal.crystal.Diseno.estructurada

data class ParametrosEstructurada(
    val anchoCm: Float = 150f,
    val altoCm: Float = 180f,
    val altoTransomCm: Float = 30f,
    val divisionesTransom: Int = 2,
    val divisionesCuerpo: Int = 3,
    val serie: String = "3825",
    val vista: String? = null
) {
    fun normalizar(): ParametrosEstructurada = copy(
        anchoCm = anchoCm.coerceAtLeast(40f),
        altoCm = altoCm.coerceAtLeast(60f),
        altoTransomCm = altoTransomCm.coerceIn(8f, (altoCm - 30f).coerceAtLeast(8f)),
        divisionesTransom = divisionesTransom.coerceIn(1, 8),
        divisionesCuerpo = divisionesCuerpo.coerceIn(1, 8)
    )
}
