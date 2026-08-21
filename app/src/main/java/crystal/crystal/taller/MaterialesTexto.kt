package crystal.crystal.taller

/**
 * Utilidades para los textos de despiece ("medida = cantidad").
 *
 * [agrupar] junta las líneas de igual medida sumando solo la cantidad (conservando el orden de
 * aparición). Las líneas que no tienen ese formato (p. ej. encabezados) se conservan tal cual.
 * Es la misma lógica que `combinarTextoCantidades` de NovaCorrediza, extraída para reutilizar.
 *
 * Ej.: "120.5 = 2\n120.5 = 3\n90 = 1"  ->  "120.5 = 5\n90 = 1".
 */
object MaterialesTexto {

    private val patron = Regex("^(.+?)\\s*=\\s*(\\d+)\\s*$")

    fun agrupar(vararg textos: String): String {
        val orden = mutableListOf<String>()
        val cantidades = linkedMapOf<String, Int>()
        val literales = mutableListOf<String>()

        for (texto in textos) {
            texto.lineSequence()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .forEach { linea ->
                    val match = patron.matchEntire(linea)
                    if (match == null) {
                        if (linea !in literales) literales.add(linea)
                    } else {
                        val medida = match.groupValues[1].trim()
                        val cantidad = match.groupValues[2].toIntOrNull() ?: 0
                        if (!cantidades.containsKey(medida)) orden.add(medida)
                        cantidades[medida] = (cantidades[medida] ?: 0) + cantidad
                    }
                }
        }

        val lineas = orden.map { "$it = ${cantidades[it] ?: 0}" }.toMutableList()
        lineas.addAll(literales)
        return lineas.joinToString("\n")
    }
}
