package crystal.crystal.calculadora

import java.util.Locale

/**
 * La aritmética de la calculadora: evaluar la expresión escrita, darle formato al resultado y saber
 * cuándo se puede cerrar un paréntesis.
 *
 * Vivía dentro de [crystal.crystal.VendePapa]. Se sacó aquí cuando la calculadora flotante empezó a
 * necesitar lo mismo: dos evaluadores distintos acabarían dando dos resultados distintos para la
 * misma cuenta, que es lo último que puede pasar en un taller.
 */
object Expresion {

    /**
     * Evalúa la expresión. Lanza [IllegalArgumentException] con un mensaje en claro cuando está mal
     * escrita (paréntesis sin cerrar, división entre cero…), para poder mostrárselo al usuario.
     */
    fun evaluar(raw: String): Double = Parser(raw).parse()

    /** Como [evaluar] pero devuelve null en vez de lanzar; para cuando no hay que avisar de nada. */
    fun evaluarONulo(raw: String): Double? = runCatching { evaluar(raw) }.getOrNull()

    /** Dos decimales como mucho, sin ceros de relleno y siempre con punto. */
    fun formatear(value: Double): String {
        val redondeado = if (kotlin.math.abs(value) < 0.0000001) 0.0 else value
        val texto = if (redondeado % 1.0 == 0.0) {
            redondeado.toLong().toString()
        } else {
            "%.2f".format(Locale.US, redondeado).trimEnd('0').trimEnd('.')
        }
        return texto.replace(",", ".")
    }

    /** Solo se cierra un paréntesis si hay uno abierto y lo anterior es algo que se puede cerrar. */
    fun puedeCerrarParentesis(texto: String): Boolean {
        if (texto.isBlank()) return false
        val abiertos = texto.count { it == '(' }
        val cerrados = texto.count { it == ')' }
        return abiertos > cerrados && (texto.last().isDigit() || texto.last() == ')' || texto.last() == '%')
    }

    fun esOperador(c: Char): Boolean = c == '+' || c == '-' || c == '*' || c == '/'

    private class Parser(raw: String) {
        private val input = raw
            .replace(",", ".")
            .replace("x", "*", ignoreCase = true)
            .replace("÷", "/")
            .replace("×", "*")
        private var pos = 0

        fun parse(): Double {
            val value = parseExpression()
            skipSpaces()
            if (pos != input.length) {
                throw IllegalArgumentException("Operacion invalida")
            }
            if (!value.isFinite()) {
                throw IllegalArgumentException("Resultado invalido")
            }
            return value
        }

        private fun parseExpression(): Double {
            var value = parseTerm()
            while (true) {
                skipSpaces()
                value = when {
                    match('+') -> value + parseTerm()
                    match('-') -> value - parseTerm()
                    else -> return value
                }
            }
        }

        private fun parseTerm(): Double {
            var value = parseFactor()
            while (true) {
                skipSpaces()
                value = when {
                    match('*') -> value * parseFactor()
                    match('/') -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw IllegalArgumentException("No se puede dividir entre cero")
                        value / divisor
                    }
                    else -> return value
                }
            }
        }

        private fun parseFactor(): Double {
            skipSpaces()
            var value = when {
                match('+') -> parseFactor()
                match('-') -> -parseFactor()
                match('(') -> {
                    val inner = parseExpression()
                    if (!match(')')) throw IllegalArgumentException("Falta cerrar parentesis")
                    inner
                }
                else -> parseNumber()
            }

            while (true) {
                skipSpaces()
                if (match('%')) {
                    value /= 100.0
                } else {
                    return value
                }
            }
        }

        private fun parseNumber(): Double {
            skipSpaces()
            val start = pos
            var hasPoint = false
            while (pos < input.length) {
                val c = input[pos]
                when {
                    c.isDigit() -> pos++
                    c == '.' && !hasPoint -> {
                        hasPoint = true
                        pos++
                    }
                    else -> break
                }
            }
            if (start == pos) throw IllegalArgumentException("Numero esperado")
            return input.substring(start, pos).toDoubleOrNull()
                ?: throw IllegalArgumentException("Numero invalido")
        }

        private fun match(char: Char): Boolean {
            skipSpaces()
            if (pos < input.length && input[pos] == char) {
                pos++
                return true
            }
            return false
        }

        private fun skipSpaces() {
            while (pos < input.length && input[pos].isWhitespace()) pos++
        }
    }
}
