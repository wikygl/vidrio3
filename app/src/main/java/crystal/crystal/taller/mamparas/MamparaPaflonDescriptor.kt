package crystal.crystal.taller.mamparas

/**
 * Descriptor simbólico de una mampara paflón. Guarda solo los datos necesarios para volver a
 * dibujar el diseño (igual que [crystal.crystal.taller.puerta.PuertaDescriptor]); el dibujo se
 * regenera con [MamparaPaflonRender], sin guardar PNG. Así el archivado siempre se ve con la
 * última lógica de dibujo.
 *
 * Formato: `MPF1:ancho;alto;altoHoja;divisiones;bastidor;marco;nMochetas[;patron]`
 *
 * `patron` es opcional y va al FINAL a propósito: los diseños archivados antes de que existiera
 * tienen siete campos y se siguen leyendo igual, cayendo al patrón automático.
 */
data class MamparaPaflonDescriptor(
    val ancho: Float,
    val alto: Float,
    val altoHoja: Float,
    val divisiones: Int,
    val bastidor: Float,
    val marco: Float,
    val nMochetas: Int,
    /**
     * Disposición elegida a mano: `fcf|cf` (tramos separados por `|`, f = fijo, c = corrediza).
     * Vacío = la calcula el automático. Cuando está, manda sobre [divisiones].
     */
    val patron: String = ""
) {
    fun serializar(): String =
        "$PREFIJO$ancho;$alto;$altoHoja;$divisiones;$bastidor;$marco;$nMochetas" +
            if (patron.isNotBlank()) ";$patron" else ""

    companion object {
        /** Clave bajo la que se archiva en el mapa (la misma que reconoce el recycler). */
        const val CLAVE = "DisenoMampara"
        private const val PREFIJO = "MPF1:"

        fun parsear(raw: String): MamparaPaflonDescriptor? {
            val s = raw.trim()
            if (!s.startsWith(PREFIJO)) return null
            val partes = s.removePrefix(PREFIJO).split(";")
            if (partes.size < 7) return null
            return runCatching {
                MamparaPaflonDescriptor(
                    ancho = partes[0].toFloat(),
                    alto = partes[1].toFloat(),
                    altoHoja = partes[2].toFloat(),
                    divisiones = partes[3].toInt(),
                    bastidor = partes[4].toFloat(),
                    marco = partes[5].toFloat(),
                    nMochetas = partes[6].toInt(),
                    // Los descriptores de siete campos son los de antes del patrón manual.
                    patron = partes.getOrNull(7).orEmpty()
                )
            }.getOrNull()
        }
    }
}
