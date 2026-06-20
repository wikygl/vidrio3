package crystal.crystal.datos

import java.text.Normalizer
import java.util.Locale

object ProductSearch {
    private val stopWords = setOf(
        "de", "del", "la", "el", "los", "las", "y", "en", "para", "con",
        "producto", "productos", "servicio", "servicios", "medida", "medidas"
    )

    fun buscarSimilares(productos: List<Product>, consulta: String, limite: Int = 10): List<Product> {
        val consultas = listOf(consulta)
        return buscarSimilares(productos, consultas, limite)
    }

    fun buscarSimilares(productos: List<Product>, consultas: List<String>, limite: Int = 10): List<Product> {
        val queryTokens = consultas
            .flatMap { tokens(it) }
            .distinct()

        if (queryTokens.isEmpty()) return emptyList()

        return productos
            .mapNotNull { producto ->
                val nameTokens = tokens(producto.nombre)
                val score = scoreProducto(nameTokens, queryTokens, producto.nombre, consultas)
                if (score <= 0) null else producto to score
            }
            .sortedWith(compareByDescending<Pair<Product, Int>> { it.second }.thenBy { it.first.nombre })
            .take(limite)
            .map { it.first }
    }

    private fun scoreProducto(
        nameTokens: List<String>,
        queryTokens: List<String>,
        nombre: String,
        consultas: List<String>
    ): Int {
        val normalizedName = normalize(nombre)
        val normalizedQueries = consultas.map { normalize(it) }.filter { it.isNotBlank() }

        var score = 0
        if (normalizedQueries.any { normalizedName.contains(it) }) score += 100

        for (query in queryTokens) {
            for (name in nameTokens) {
                score += when {
                    name == query -> 30
                    name.startsWith(query) || query.startsWith(name) -> 16
                    name.contains(query) || query.contains(name) -> 10
                    else -> 0
                }
            }
        }
        return score
    }

    private fun tokens(value: String): List<String> {
        return normalize(value)
            .split(Regex("[^a-z0-9]+"))
            .map { it.trim() }
            .filter { it.length >= 2 && it !in stopWords && !it.all(Char::isDigit) }
    }

    private fun normalize(value: String): String {
        val sinAcentos = Normalizer.normalize(value.lowercase(Locale.ROOT), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
        return sinAcentos
            .replace(Regex("(?<=[a-z])(?=\\d)"), " ")
            .replace(Regex("(?<=\\d)(?=[a-z])"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}
