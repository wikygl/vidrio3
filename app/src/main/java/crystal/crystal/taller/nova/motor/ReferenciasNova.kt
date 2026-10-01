package crystal.crystal.taller.nova.motor

import crystal.crystal.taller.nova.NovaCalculos

/**
 * Las referencias de Nova escritas desde la misma ventana que calcula el motor (fase 4 del motor
 * único, 2026-09-30). Antes las armaba el cálculo viejo (`NovaUIHelper.generarReferencias`) con sus
 * propias cuentas.
 *
 * Mismo formato de siempre, un bloque por lado (N-76, N-77):
 * ```
 * An: 214.5  x  Al: 157.5
 * Altura de puente: 112.5
 * mocheta sup: 43.5
 * Divisiones: 4 -> fjs: 2;czs: 2
 * Puntos: …            (con más de 4 divisiones)
 * ```
 * `An:` y `Al:` son la medida medida; la mocheta es el espacio (N-31), igual en aparente e
 * inaparente. La primera línea es la que se relee al reabrir un producto (ReaperturaProducto).
 */
object ReferenciasNova {

    /**
     * [anchosMedidos]: la medida de cada pared tal como se midió, si no es la del lado (una esquina
     * leída del diseño trae el ancho útil). La curva de una esquina no lleva bloque propio.
     */
    fun texto(v: VentanaNova, anchosMedidos: List<Float>? = null): String {
        val paredes = v.lados.filter { !it.curvaDeEsquina }
        return paredes.mapIndexed { i, l -> bloque(v, l, anchosMedidos?.getOrNull(i)) }.joinToString("\n")
    }

    private fun bloque(v: VentanaNova, l: LadoNova, anchoMedido: Float?): String {
        val lineas = mutableListOf("An: ${df(anchoMedido ?: l.anchoMedido)}  x  Al: ${df(l.altoMedido)}")
        val verticales = l.tramos.map { MotorNova.verticalDe(v, l, it) }
        val conPuente = verticales.filter { it.hayMocheta }
        if (conPuente.isEmpty()) {
            lineas.add("Altura de puente: sin puente")
        } else {
            // Un diseño compuesto puede llevar el puente de cada tramo a otra altura: se dicen todas.
            val alturas = conPuente.map { df(it.altoHoja) }.distinct()
            lineas.add("Altura de puente: ${alturas.joinToString(" | ")}")
            val vt = conPuente.first()
            vt.mochetas.filter { !it.first }.forEach { lineas.add("mocheta inf: ${df(it.second)}") }
            vt.mochetas.filter { it.first }.forEach { lineas.add("mocheta sup: ${df(it.second)}") }
        }
        val modulos = l.modulos
        val divisiones = modulos.size
        lineas.add("Divisiones: $divisiones -> fjs: ${modulos.count { !it.corrediza }};czs: ${modulos.count { it.corrediza }}")
        puntos(divisiones, MotorNova.uDeFijo(v, l), v.cruce)?.let { lineas.add("Puntos: $it") }
        return lineas.joinToString("\n")
    }

    /**
     * Los puntos de marca sobre la U de fijos cuando la ventana tiene más de 4 divisiones, con la
     * misma cuenta de siempre (`NovaUIHelper.generarPuntosU`), sobre la U de fijo del motor.
     */
    fun puntos(divisiones: Int, partes: Float, cruce: Float): String? {
        if (divisiones <= 4 || partes <= 0f) return null
        val p1 = df(partes * 2 - cruce * 2)
        val p2 = df(partes * 4 - cruce * 4)
        val p3 = df(partes * 6 - cruce * 6)
        return when (divisiones) {
            5, 6 -> p1
            8, 12 -> df(partes * 3 - cruce * 2)
            7, 10, 14 -> "${p1}_$p2"
            9, 11, 13, 15 -> "${p1}_${p2}_$p3"
            else -> null
        }
    }

    private fun df(v: Float): String = NovaCalculos.df1(v)
}
