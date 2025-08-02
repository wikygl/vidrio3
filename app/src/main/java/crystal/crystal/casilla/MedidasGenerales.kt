package crystal.crystal.casilla

data class MedidasGenerales(
    val ancho: Float?        = null,
    val alto: Float?         = null,
    val hPuente: Float?      = null,
    val divisiones: Int?     = null,
    val u: Float?            = null,
    val cruce: Float?        = null,
    val ancho2: Float?       = null,
    val divi2: Int?          = null,
    val puente2: Float?      = null,
    val flecha: Int?         = null
) {
    /**
     * Devuelve un Map con solo las propiedades que no sean nulas.
     */
    fun toMap(): Map<String, Any> = buildMap {
        ancho     ?.let { put("ancho", it) }
        alto      ?.let { put("alto", it) }
        hPuente   ?.let { put("hPuente", it) }
        divisiones?.let { put("divisiones", it) }
        u         ?.let { put("u", it) }
        cruce     ?.let { put("cruce", it) }
        ancho2    ?.let { put("ancho2", it) }
        divi2     ?.let { put("divi2", it) }
        puente2   ?.let { put("puente2", it) }
        flecha    ?.let { put("flecha", it) }
    }
}
