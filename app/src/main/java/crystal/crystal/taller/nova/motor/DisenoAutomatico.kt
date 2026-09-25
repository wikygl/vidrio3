package crystal.crystal.taller.nova.motor

import crystal.crystal.taller.nova.NovaCalculos
import kotlin.math.ceil

/**
 * Nova con ceil: arma los tramos y módulos de un lado con las reglas automáticas (sección 5 de
 * docs/REGLAS_NOVA.md). El resultado es un [LadoNova] igual al que trae un diseño hecho a mano, así
 * que desde aquí los dos caminos se calculan igual.
 *
 * No lee el estado guardado en NovaCalculos (reparto manual, corredizas por tramo): lo recibe.
 */
object DisenoAutomatico {

    data class Entrada(
        val anchoMedido: Float,
        /** Ancho con los descuentos de aparente ya hechos (lados libres, esquinero). */
        val anchoCalculo: Float,
        val alto: Float,
        val altoMedido: Float = alto,
        /** Altura de hoja escrita; 0 = 5/7 del alto (N-29). */
        val hoja: Float,
        val acabado: Acabado,
        val puente: Puente,
        val divisManual: Int = 0,
        val modulacion: String = "nn",
        val repartoManual: List<Int>? = null,
        val corredizasPorTramoNfc: Int = 0,
        val izquierda: Borde = Borde.MURO,
        val derecha: Borde = Borde.MURO
    )

    /**
     * Cuántas hojas. Lo que escribió el usuario manda (N-16); si no, doble corrediza 2, triple
     * corrediza 3 y ncfc 3 (N-20), y el resto la regla de los 60 sobre la medida que se midió
     * (N-15, N-17).
     */
    fun divisiones(anchoMedido: Float, divisManual: Int, modulacion: String = "nn"): Int = when {
        divisManual > 0 -> divisManual
        modulacion == "ncc" -> 2
        modulacion == "n3c" || modulacion == "ncfc" -> 3
        else -> NovaCalculos.divisiones(anchoMedido, 0)
    }

    /**
     * Cuántos módulos lleva cada tramo (N-24, N-25, N-26, N-19). El automático no pasa de 5 por
     * tramo; un reparto elegido a mano manda si suma las divisiones.
     */
    fun grupos(
        divisiones: Int,
        modulacion: String,
        acabado: Acabado,
        repartoManual: List<Int>? = null,
        corredizasPorTramoNfc: Int = 0
    ): List<Int> {
        if (divisiones <= 0) return emptyList()
        val base = repartoManual?.takeIf { it.isNotEmpty() && it.sum() == divisiones }
            ?: NovaCalculos.repartirDivisionesEnTramos(divisiones, ceil(divisiones / 5.0).toInt().coerceAtLeast(1))
        return when (modulacion) {
            "ncc" -> cadaN(divisiones, 2)
            "n3c" -> cadaN(divisiones, 3)
            "nfc" -> if (corredizasPorTramoNfc > 1) cadaN(divisiones, corredizasPorTramoNfc) else base
            // ncfc en INA sigue siendo un solo tramo hasta que se dicten las limitaciones de INA (N-26).
            "ncfc" -> if (acabado == Acabado.INA) listOf(divisiones) else base
            else -> base
        }
    }

    /** Fijo o corrediza de cada módulo de un tramo de [n] módulos (N-22, N-23). */
    fun patron(n: Int, modulacion: String): List<Boolean> = when (modulacion) {
        "nff" -> List(n) { false }
        "nfc", "ncc", "n3c" -> List(n) { true }
        "ncfc" -> letras(NovaCalculos.ordenDivisCfc(n, n.toFloat()))
        else -> letras(NovaCalculos.ordenDivis(n, n.toFloat()))
    }

    fun lado(e: Entrada): LadoNova {
        val divisiones = divisiones(e.anchoMedido, e.divisManual, e.modulacion)
        val grupos = grupos(divisiones, e.modulacion, e.acabado, e.repartoManual, e.corredizasPorTramoNfc)
        // ncfc en INA: un tramo, pero con el patrón armado por los grupos del clásico (cffc…).
        val patrones: List<List<Boolean>> = if (e.modulacion == "ncfc" && e.acabado == Acabado.INA) {
            val base = grupos(divisiones, "nn", e.acabado, e.repartoManual)
            listOf(base.flatMap { patron(it, "ncfc") })
        } else {
            grupos.map { patron(it, e.modulacion) }
        }
        // N-27: los parantes entre tramos ocupan ancho; el resto se reparte entre las hojas.
        val anchoUtil = e.anchoCalculo - e.puente.anchoParante * (patrones.size - 1).coerceAtLeast(0)
        val w = if (divisiones > 0) anchoUtil / divisiones else anchoUtil
        val tramos = patrones.map { p -> Tramo(p.map { Modulo(it, w) }) }
        return LadoNova(
            anchoMedido = e.anchoMedido,
            anchoCalculo = e.anchoCalculo,
            alto = e.alto,
            altoMedido = e.altoMedido,
            altoHoja = NovaCalculos.altoHoja(e.alto, e.hoja),
            tramos = tramos,
            izquierda = e.izquierda,
            derecha = e.derecha,
            modulacion = e.modulacion
        )
    }

    private fun cadaN(divisiones: Int, n: Int): List<Int> {
        val grupos = mutableListOf<Int>()
        var resto = divisiones
        while (resto > 0) {
            val t = minOf(n, resto)
            grupos.add(t)
            resto -= t
        }
        return grupos
    }

    private fun letras(orden: String): List<Boolean> =
        Regex("[fc](?=<)").findAll(orden).map { it.value[0] == 'c' }.toList()
}
