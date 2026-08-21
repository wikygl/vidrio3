package crystal.crystal.red.interop

data class ParsedMeasuresMessage(
    val productName: String,
    val items: List<ParsedMeasureLine>
)

data class ParsedMeasureLine(
    val productName: String,
    val width: Float,
    val height: Float,
    val quantity: Float
)

object MeasuresMessageCodec {

    private const val NUMBER_PATTERN = """\d+(?:\s*[.,]\s*\d+)?"""
    private const val CONNECTOR_PATTERN = """(?:x|X|×|por)"""

    // Acepta texto extra tras la cantidad (código de paquete y/o cliente), p. ej.
    // "66.5 x 70.8 = 2 Vna7 lucia" -> ese resto se toma como nombre/variante del producto de la línea.
    private val qtyLastRegex = Regex(
        """^\s*($NUMBER_PATTERN)\s*$CONNECTOR_PATTERN\s*($NUMBER_PATTERN)\s*(?:=|igual|-|->)\s*($NUMBER_PATTERN)\s*(.*)$""",
        RegexOption.IGNORE_CASE
    )
    private val qtyFirstRegex = Regex(
        """^\s*($NUMBER_PATTERN)\s*(?:-|->|=|igual)\s*($NUMBER_PATTERN)\s*$CONNECTOR_PATTERN\s*($NUMBER_PATTERN)\s*(.*)$""",
        RegexOption.IGNORE_CASE
    )
    private val variantRegex = Regex("""^(?:[a-zA-Z]{1,3}\s*\d+|[a-zA-Z])$""")

    fun isMeasuresFormat(message: String): Boolean = parse(message) != null

    fun parse(message: String): ParsedMeasuresMessage? {
        val lines = normalizedLines(message)
        if (lines.size < 2) return null

        val parsedItems = mutableListOf<ParsedMeasureLine>()
        val baseProductParts = mutableListOf<String>()
        var currentVariant: String? = null
        var justCompletedMeasure = false

        for (line in lines) {
            val parsedMeasure = parseMeasureLine(line)
            if (parsedMeasure != null) {
                // Si la línea trae texto tras la cantidad (código/cliente), se usa como variante de
                // esa línea; si no, se hereda la variante actual.
                val variantLinea = parsedMeasure.trailing.takeIf { it.isNotBlank() }
                    ?.let { cleanVariantLabel(it) }
                    ?: currentVariant
                val productName = buildProductName(baseProductParts, variantLinea)
                if (productName.isBlank()) return null
                parsedItems += ParsedMeasureLine(
                    productName = productName,
                    width = parsedMeasure.width,
                    height = parsedMeasure.height,
                    quantity = parsedMeasure.quantity
                )
                justCompletedMeasure = true
                continue
            }

            if (shouldUseAsVariant(line, baseProductParts)) {
                currentVariant = cleanVariantLabel(line)
                justCompletedMeasure = false
                continue
            }

            if (justCompletedMeasure) {
                baseProductParts.clear()
                currentVariant = null
            }
            baseProductParts += cleanProductLabel(line)
            justCompletedMeasure = false
        }

        if (parsedItems.isEmpty()) return null

        val distinctProducts = parsedItems.map { it.productName }.distinct()
        val summaryName = when {
            distinctProducts.size == 1 -> distinctProducts.first()
            baseProductParts.isNotEmpty() ->
                "${baseProductParts.joinToString(" ").trim()} (${distinctProducts.size} variantes)"
            else -> "Medidas multiples"
        }

        return ParsedMeasuresMessage(
            productName = summaryName,
            items = parsedItems
        )
    }

    private data class LineaMedida(
        val width: Float,
        val height: Float,
        val quantity: Float,
        val trailing: String
    )

    private fun parseMeasureLine(line: String): LineaMedida? {
        qtyLastRegex.matchEntire(line)?.let { match ->
            val width = match.groupValues[1].normalizeNumber().toFloatOrNull() ?: return null
            val height = match.groupValues[2].normalizeNumber().toFloatOrNull() ?: return null
            val quantity = match.groupValues[3].normalizeNumber().toFloatOrNull() ?: return null
            return LineaMedida(width, height, quantity, match.groupValues[4].trim())
        }

        qtyFirstRegex.matchEntire(line)?.let { match ->
            val quantity = match.groupValues[1].normalizeNumber().toFloatOrNull() ?: return null
            val width = match.groupValues[2].normalizeNumber().toFloatOrNull() ?: return null
            val height = match.groupValues[3].normalizeNumber().toFloatOrNull() ?: return null
            return LineaMedida(width, height, quantity, match.groupValues[4].trim())
        }

        return null
    }

    private fun shouldUseAsVariant(line: String, baseProductParts: List<String>): Boolean {
        if (baseProductParts.isEmpty()) return false
        return variantRegex.matches(line)
    }

    private fun buildProductName(baseProductParts: List<String>, variant: String?): String {
        val base = baseProductParts.joinToString(" ").trim()
        val suffix = variant?.takeIf { it.isNotBlank() }
        return when {
            base.isNotBlank() && suffix != null -> "$base, $suffix"
            base.isNotBlank() -> base
            suffix != null -> suffix
            else -> ""
        }
    }

    private fun normalizedLines(message: String): List<String> {
        return message
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .trim()
            .split("\n")
            .map { cleanLine(it) }
            .filter { it.isNotBlank() }
    }

    private fun cleanLine(value: String): String {
        return value
            .replace(Regex("""(?<=\d)[.,]\s+(?=\d)"""), ".")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun cleanProductLabel(value: String): String {
        return cleanLine(value)
            .replace(Regex("""(?<=[A-Za-z])(?=\d)"""), " ")
            .replace(Regex("""(?<=\d)(?=[A-Za-z])"""), " ")
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun cleanVariantLabel(value: String): String {
        return cleanLine(value)
            .replace(Regex("""\s+"""), " ")
            .trim()
    }

    private fun String.normalizeNumber(): String {
        return replace(Regex("""(?<=\d)[.,]\s+(?=\d)"""), ".")
            .replace(",", ".")
            .replace(" ", "")
    }
}
