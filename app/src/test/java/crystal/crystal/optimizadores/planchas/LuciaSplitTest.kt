package crystal.crystal.optimizadores.planchas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regresión del optimizador de planchas con el caso real "Lucía" (43 piezas, plancha 330×214 cm).
 * El split de dirección mixta debe lograr 4 planchas (antes topaba en 5 con split fijo), con todas
 * las piezas ubicadas. Se usa el nivel Rápido para que el test sea veloz (~1 s).
 */
class LuciaSplitTest {
    private data class P(val w: Int, val h: Int, val n: Int, val desc: String)
    private val lucia = listOf(
        P(722, 1088, 4, "Vna1/Vna2"), P(542, 1040, 1, "MC1"), P(536, 933, 2, "Vna5"),
        P(526, 915, 1, "Vna5"), P(1075, 456, 4, "Vna1/Vna2"), P(390, 794, 1, "MC2"),
        P(1060, 1710, 1, "Vna6"), P(924, 1070, 3, "MC3"), P(924, 554, 3, "MC3"),
        P(390, 783, 1, "Vna3"), P(390, 785, 1, "Vna4"), P(919, 233, 6, "MC4"),
        P(919, 1070, 3, "MC4"), P(712, 1070, 2, "Vna1/Vna2"), P(623, 1183, 2, "Vna6"),
        P(613, 1165, 1, "Vna6"), P(926, 501, 2, "Vna6"), P(1595, 321, 1, "Vna5"),
        P(542, 261, 2, "MC1"), P(542, 1579, 2, "MC1")
    )
    private fun piezas() = lucia.mapIndexed { i, p -> PiezaPlancha("p$i", p.desc, p.w, p.h, p.n) }

    @Test
    fun lucia4Planchas() {
        val r = OptimizadorPlanchas.optimizar(
            piezas = piezas(),
            // Stock holgado de la plancha de compra: el optimizador debe usar solo 4 unidades.
            stock = listOf(PlanchaStock("Plancha", 3300, 2140, cantidad = 10)),
            intensidad = IntensidadOptimizacionPlanchas.RAPIDO
        )
        assertEquals("no debe quedar ninguna pieza sin ubicar", 0, r.piezasSinUbicar.size)
        assertTrue("debe lograr 4 planchas o menos (era 5 con split fijo), fueron ${r.planchas.size}",
            r.planchas.size <= 4)
    }
}
