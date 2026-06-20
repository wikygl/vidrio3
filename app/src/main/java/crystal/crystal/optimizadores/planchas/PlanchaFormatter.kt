package crystal.crystal.optimizadores.planchas

class PlanchaFormatter {
    fun df1(v: Float): String {
        val s = if ("$v".endsWith(".0")) "$v".replace(".0", "") else "%.1f".format(v)
        return s.replace(",", ".")
    }

    fun toFloat(s: String) = s.toFloatOrNull()
    fun toInt(s: String) = s.toIntOrNull()
}