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

    /**
     * Líneas "medida = cantidad" a partir de las medidas EXACTAS, repartiendo el redondeo.
     *
     * Al truncar a un decimal cada pieza pierde hasta 0.099, y **siempre hacia abajo**: once
     * piezas de 59.47273 se escriben como 59.4 y dejan 8 mm sin cubrir al llegar al otro extremo.
     * Aquí ese resto se devuelve a las piezas de una en una, en décimas, empezando por las que
     * más perdieron: quedan 8 de 59.5 y 3 de 59.4, y la suma cierra.
     *
     * Se reparte **solo lo que cabe entero en décimas**, nunca de más, porque hay piezas que no
     * pueden salir más largas que el hueco. Lo que sobre queda por debajo.
     *
     * El reparto va en décimas enteras a propósito: con flotantes, `33.3f + 0.1f` da 33.399998 y
     * al truncar vuelve a 33.3, así que la décima devuelta se perdía sin avisar.
     *
     * Todavía no lo usa nadie: se dejó listo para los módulos donde las piezas van a tope y la
     * suma tiene que dar el ancho del hueco.
     */
    fun repartirRedondeo(medidasExactas: List<Float>): String {
        if (medidasExactas.isEmpty()) return ""
        val enDecimas = medidasExactas.map { it * 10.0 }
        val bases = enDecimas.map { kotlin.math.floor(it).toInt() }
        val perdidas = enDecimas.mapIndexed { i, exacta -> exacta - bases[i] }
        val aRepartir = kotlin.math.floor(perdidas.sum() + 1e-6).toInt()
            .coerceIn(0, medidasExactas.size)
        val suben = perdidas.withIndex()
            .sortedByDescending { it.value }
            .take(aRepartir)
            .map { it.index }
            .toSet()

        fun texto(decimas: Int): String =
            if (decimas % 10 == 0) (decimas / 10).toString() else "${decimas / 10}.${decimas % 10}"

        val conteo = linkedMapOf<String, Int>()
        for (i in bases.indices) {
            val clave = texto(bases[i] + if (i in suben) 1 else 0)
            conteo[clave] = (conteo[clave] ?: 0) + 1
        }
        return conteo.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }
}
