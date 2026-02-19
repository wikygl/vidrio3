package crystal.crystal.Diseno.vitroven

data class VitroModuloToken(
    val tipo: Char,
    val medida: Float? = null
)

data class VitroSimbolicoParse(
    val modulos: List<VitroModuloToken>,
    val anchoReferencia: Float? = null,
    val altoReferencia: Float? = null,
    val segmentadoVertical: Boolean = false
)

object VitroSimbolicoParser {

    private val formatoCompletoRegex = Regex(
        pattern = """\{?\s*vitrov(?:e|é)n?\s*\[\s*([\d.,]+)\s*,\s*([\d.,]+)\s*:\s*([^\]]*)\]\s*\}?""",
        option = RegexOption.IGNORE_CASE
    )
    private val tokenRegex = Regex("""([fFvV])(?:\s*<\s*([\d.,]+)\s*>)?""")

    fun parse(textoCrudo: String): VitroSimbolicoParse {
        val texto = textoCrudo.trim()
        if (texto.isBlank()) return VitroSimbolicoParse(emptyList())

        val match = formatoCompletoRegex.find(texto)
        if (match != null) {
            val ancho = parseNumero(match.groupValues[1])
            val alto = parseNumero(match.groupValues[2])
            val cuerpo = match.groupValues[3]
            val modulos = parseModulos(cuerpo)
            val vertical = modulos.any { it.tipo.isUpperCase() }
            return VitroSimbolicoParse(
                modulos = modulos,
                anchoReferencia = ancho,
                altoReferencia = alto,
                segmentadoVertical = vertical
            )
        }

        val modulos = parseModulos(texto)
        val vertical = modulos.any { it.tipo.isUpperCase() }
        return VitroSimbolicoParse(
            modulos = modulos,
            segmentadoVertical = vertical
        )
    }

    private fun parseModulos(cuerpo: String): List<VitroModuloToken> {
        val tokens = tokenRegex.findAll(cuerpo).mapNotNull { m ->
            val tipo = m.groupValues[1].firstOrNull() ?: return@mapNotNull null
            val medida = parseNumero(m.groupValues[2])
            VitroModuloToken(tipo = tipo, medida = medida)
        }.toList()
        if (tokens.isNotEmpty()) return tokens

        return cuerpo.filter { it == 'f' || it == 'v' || it == 'F' || it == 'V' }
            .map { VitroModuloToken(it, null) }
    }

    private fun parseNumero(numero: String?): Float? {
        if (numero.isNullOrBlank()) return null
        return numero.replace(',', '.').toFloatOrNull()
    }
}
