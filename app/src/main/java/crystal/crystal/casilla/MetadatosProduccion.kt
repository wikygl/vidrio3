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

    // Ventana de aluminio archivaba el color y el vidrio en dos listas sueltas en vez del sufijo
    // -MAT<...>. Se leen también, porque hay proyectos ya archivados así: sin esto, sus piezas se
    // quedaban sin color y desaparecían de las listas de corte con color de los demás productos.
    private const val CLAVE_ALU_LEGADO = "Color aluminio"
    private const val CLAVE_VID_LEGADO = "Tipo vidrio"

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
        // Las listas sueltas solo rellenan lo que falte: el sufijo -MAT<...> manda siempre.
        fun legado(clave: String, aplicar: (Pair<String, String>, String) -> Pair<String, String>) {
            mapListas[clave]?.forEach { lista ->
                val valor = lista.getOrNull(0)?.trim().orEmpty()
                val ventana = lista.getOrElse(2) { "" }.ifBlank { null } ?: return@forEach
                if (valor.isBlank()) return@forEach
                resultado[ventana] = aplicar(resultado[ventana] ?: ("" to ""), valor)
            }
        }
        legado(CLAVE_ALU_LEGADO) { actual, valor -> if (actual.first.isBlank()) valor to actual.second else actual }
        legado(CLAVE_VID_LEGADO) { actual, valor -> if (actual.second.isBlank()) actual.first to valor else actual }
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
