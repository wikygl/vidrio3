package crystal.crystal.casilla

/**
 * Fuente ÚNICA para leer los metadatos de producción (color de aluminio y tipo de vidrio) que los
 * módulos archivan junto a cada ventana como el sufijo `-MAT<alu:...;vid:...>`.
 *
 * Regla única (evita parches por módulo): el metadato puede venir bajo dos claves según el módulo
 * de origen —el diseño simbólico usa "DisenoSimbolicoV2" y los módulos de taller (muro cortina,
 * mampara paflón, nova, puertas) usan "MetadatosProduccion"—. Ambas comparten formato y guardan el
 * id de ventana en la posición 2. Cualquier lector (corte de varillas, corte de planchas, etc.)
 * debe usar este objeto para no volver a desincronizarse.
 */
object MetadatosProduccion {

    private val CLAVES = listOf("DisenoSimbolicoV2", "MetadatosProduccion")

    /** Mapa idVentana -> (colorAluminio, tipoVidrio). Solo incluye ventanas con algún dato. */
    fun mapaPorVentana(mapListas: Map<String, List<List<String>>>): Map<String, Pair<String, String>> {
        val resultado = mutableMapOf<String, Pair<String, String>>()
        CLAVES.forEach { clave ->
            mapListas[clave]?.forEach { lista ->
                val paquete = lista.getOrNull(0)?.trim() ?: return@forEach
                val ventana = lista.getOrElse(2) { "" }.ifBlank { null } ?: return@forEach
                val alu = extraerCampo(paquete, "alu")
                val vid = extraerCampo(paquete, "vid")
                if (alu.isNotBlank() || vid.isNotBlank()) {
                    resultado[ventana] = alu to vid
                }
            }
        }
        return resultado
    }

    /** Extrae un campo (p. ej. "alu" o "vid") del sufijo `-MAT<...>` de un paquete. */
    fun extraerCampo(paquete: String, campo: String): String {
        val mat = Regex("-MAT<([^>]*)>").find(paquete) ?: return ""
        return mat.groupValues[1].split(";")
            .find { it.startsWith("$campo:", ignoreCase = true) }
            ?.substringAfter(":")
            ?.replace("_", " ")
            ?.trim()
            ?.takeIf { it != "null" } ?: ""
    }
}
