package crystal.crystal.taller.mamparas

/**
 * Descriptor simbólico de una mampara paflón. Guarda solo los datos necesarios para volver a
 * dibujar el diseño (igual que [crystal.crystal.taller.puerta.PuertaDescriptor]); el dibujo se
 * regenera con [MamparaPaflonRender], sin guardar PNG. Así el archivado siempre se ve con la
 * última lógica de dibujo.
 *
 * Formato: `MPF1:ancho;alto;altoHoja;divisiones;bastidor;marco;nMochetas[;patron[;marcoInf]]`
 *
 * `patron` y `marcoInf` son opcionales y van al FINAL a propósito: los diseños archivados antes de
 * que existieran tienen siete campos y se siguen leyendo igual, cayendo al patrón automático y a
 * la mampara sin marco inferior.
 */
data class MamparaPaflonDescriptor(
    val ancho: Float,
    val alto: Float,
    /**
     * Altura de puente = alto de la hoja. Es una cota INTERNA: si se pide 210, la hoja mide 210
     * lleve o no marco inferior. Con marco inferior la hoja apoya sobre él y arranca un marco más
     * arriba, así que lo que se acorta es la mocheta.
     */
    val altoHoja: Float,
    val divisiones: Int,
    val bastidor: Float,
    val marco: Float,
    val nMochetas: Int,
    /**
     * Disposición elegida a mano: `fcf|cf` (tramos separados por `|`, f = fijo, c = corrediza).
     * Vacío = la calcula el automático. Cuando está, manda sobre [divisiones].
     */
    val patron: String = "",
    /**
     * Mampara con marco inferior. Por defecto la paflón no lo lleva: se apoya en el piso o en el
     * plato de ducha. Cuando lo lleva, la hoja apoya sobre el marco conservando su alto, de modo
     * que todo sube un marco y la mocheta de arriba es la que se acorta.
     */
    val marcoInferior: Boolean = false
) {
    fun serializar(): String = buildString {
        append("$PREFIJO$ancho;$alto;$altoHoja;$divisiones;$bastidor;$marco;$nMochetas")
        // El patrón se escribe aunque esté vacío cuando detrás va el marco inferior: si no, al
        // releer, el campo del marco caería en la posición del patrón.
        if (patron.isNotBlank() || marcoInferior) append(";$patron")
        if (marcoInferior) append(";1")
    }

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
                    patron = partes.getOrNull(7).orEmpty(),
                    marcoInferior = partes.getOrNull(8) == "1"
                )
            }.getOrNull()
        }
    }
}
