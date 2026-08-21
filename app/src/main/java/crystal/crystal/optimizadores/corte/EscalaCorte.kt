package crystal.crystal.optimizadores.corte

import android.content.Context

/**
 * Escala de medida para el optimizador de varillas.
 * El cálculo interno SIEMPRE es en cm; la escala solo afecta entrada y visualización.
 * factorACm = cuántos cm equivale 1 unidad de esta escala.
 */
enum class EscalaCorte(val sigla: String, val etiqueta: String, val factorACm: Float) {
    METRO("m", "Metros (m)", 100f),
    CENTIMETRO("cm", "Centímetros (cm)", 1f),
    MILIMETRO("mm", "Milímetros (mm)", 0.1f),
    PULGADA("in", "Pulgadas (in)", 2.54f);

    /** Convierte un valor en esta escala a cm. */
    fun aCm(valor: Float): Float = valor * factorACm

    /** Convierte un valor en cm a esta escala. */
    fun desdeCm(cm: Float): Float = cm / factorACm

    companion object {
        private const val PREFS = "corte_prefs"
        private const val KEY = "escala_corte"

        fun cargar(ctx: Context): EscalaCorte {
            val nombre = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY, CENTIMETRO.name)
            return entries.firstOrNull { it.name == nombre } ?: CENTIMETRO
        }

        fun guardar(ctx: Context, escala: EscalaCorte) {
            ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY, escala.name).apply()
        }
    }
}
