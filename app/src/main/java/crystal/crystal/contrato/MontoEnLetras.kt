package crystal.crystal.contrato

/**
 * Un importe escrito en letras, como se pone en un contrato: `TRES MIL QUINIENTOS CON 00/100 SOLES`.
 *
 * En letras porque el número solo se altera con un trazo —un 3500 se vuelve 8500— y las palabras
 * no: si las dos formas no coinciden, se nota.
 */
object MontoEnLetras {

    private val HASTA_VEINTINUEVE = arrayOf(
        "cero", "uno", "dos", "tres", "cuatro", "cinco", "seis", "siete", "ocho", "nueve",
        "diez", "once", "doce", "trece", "catorce", "quince", "dieciséis", "diecisiete",
        "dieciocho", "diecinueve", "veinte", "veintiuno", "veintidós", "veintitrés",
        "veinticuatro", "veinticinco", "veintiséis", "veintisiete", "veintiocho", "veintinueve"
    )
    private val DECENAS = arrayOf(
        "", "", "", "treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa"
    )
    private val CENTENAS = arrayOf(
        "", "ciento", "doscientos", "trescientos", "cuatrocientos", "quinientos", "seiscientos",
        "setecientos", "ochocientos", "novecientos"
    )

    /** `3500.0` → `TRES MIL QUINIENTOS CON 00/100 SOLES`. Los céntimos van en número, como se usa. */
    fun soles(monto: Double): String {
        val centimos = Math.round(monto.coerceAtLeast(0.0) * 100)
        val enteros = centimos / 100
        val resto = centimos % 100
        return "${letras(enteros).uppercase()} CON ${"%02d".format(resto)}/100 SOLES"
    }

    /** El número entero en palabras, en minúsculas: `1521` → `mil quinientos veintiuno`. */
    fun letras(n: Long): String {
        if (n == 0L) return "cero"
        val millones = n / 1_000_000
        val miles = ((n / 1000) % 1000).toInt()
        val unidades = (n % 1000).toInt()
        val partes = mutableListOf<String>()
        if (millones > 0) {
            partes.add(if (millones == 1L) "un millón" else "${apocope(letras(millones))} millones")
        }
        if (miles > 0) {
            // "mil", no "un mil": así se dice y así se escribe.
            partes.add(if (miles == 1) "mil" else "${apocope(hastaNovecientos(miles))} mil")
        }
        if (unidades > 0) partes.add(hastaNovecientos(unidades))
        return partes.joinToString(" ")
    }

    private fun hastaNovecientos(n: Int): String {
        if (n == 100) return "cien"
        val centena = CENTENAS[n / 100]
        val r = n % 100
        val resto = when {
            r == 0 -> ""
            r < 30 -> HASTA_VEINTINUEVE[r]
            r % 10 == 0 -> DECENAS[r / 10]
            else -> "${DECENAS[r / 10]} y ${HASTA_VEINTINUEVE[r % 10]}"
        }
        return listOf(centena, resto).filter { it.isNotEmpty() }.joinToString(" ")
    }

    /** Delante de "mil" o "millones" el uno se acorta: veintiún mil, treinta y un mil. */
    private fun apocope(s: String): String = when {
        s.endsWith("veintiuno") -> s.dropLast("veintiuno".length) + "veintiún"
        s.endsWith("uno") -> s.dropLast(1)
        else -> s
    }
}
