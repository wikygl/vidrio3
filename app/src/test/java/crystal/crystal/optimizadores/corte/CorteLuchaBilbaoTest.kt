package crystal.crystal.optimizadores.corte

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El caso real del 2026-10-05 (paflón de Lucha y Bilbao, varillas de 595, disco 0.3): el usuario
 * sabe que salen en 14 varillas. El optimizador pedía 15 y, con 14, avisaba que faltaba una pieza.
 */
class CorteLuchaBilbaoTest {

    private val piezas = listOf(
        PiezaCorte(67.8f, 6, "MP1 Lucha,Paflón", true), PiezaCorte(210f, 2, "MP1 Lucha,Paflón", true),
        PiezaCorte(207.9f, 4, "MP1 Lucha,Paflón", true), PiezaCorte(304.4f, 1, "MP1 Lucha,Paflón", true),
        PiezaCorte(54f, 1, "MP1 Lucha,Paflón", true),
        PiezaCorte(57.1f, 6, "MP2 Lucha,Paflón", true), PiezaCorte(210f, 2, "MP2 Lucha,Paflón", true),
        PiezaCorte(207.9f, 4, "MP2 Lucha,Paflón", true), PiezaCorte(261.4f, 1, "MP2 Lucha,Paflón", true),
        PiezaCorte(54.6f, 1, "MP2 Lucha,Paflón", true),
        PiezaCorte(198f, 2, "P1 Lucha,Paflón", true), PiezaCorte(97.7f, 3, "P1 Lucha,Paflón", true),
        PiezaCorte(173.3f, 1, "P1 Lucha,Paflón", true), PiezaCorte(22.4f, 4, "P1 Lucha,Paflón", true),
        PiezaCorte(72.9f, 6, "MP1 bilbao,Paflón", true), PiezaCorte(210f, 2, "MP1 bilbao,Paflón", true),
        PiezaCorte(205f, 4, "MP1 bilbao,Paflón", true), PiezaCorte(324.5f, 1, "MP1 bilbao,Paflón", true),
        PiezaCorte(55.5f, 1, "MP1 bilbao,Paflón", true),
        PiezaCorte(220.8f, 2, "P1 bilbao,Paflón", true), PiezaCorte(66.3f, 2, "P1 bilbao,Paflón", true),
        PiezaCorte(204.3f, 1, "P1 bilbao,Paflón", true)
    )
    private val total = piezas.sumOf { it.cantidad }

    private fun optimizar(varillas: Int, nivel: Int) = CorteOptimizer().optimizarCortesConConfiguracion(
        piezas, listOf(PiezaCorte(595f, varillas, "e", true)), 0.3f, nivel
    )

    @Test
    fun salenEnCatorceVarillas() {
        for (nivel in listOf(1, 5)) {
            val r = optimizar(15, nivel)
            assertEquals("todas las piezas (nivel $nivel)", total, r.sumOf { it.varilla.cortes.size })
            assertTrue("en 14 varillas o menos, salieron ${r.size} (nivel $nivel)", r.size <= 14)
        }
    }

    @Test
    fun conCatorceVarillasAlcanza() {
        val r = optimizar(14, 3)
        assertEquals("con 14 varillas salen todas", total, r.sumOf { it.varilla.cortes.size })
    }

    @Test
    fun ningunaVarillaSePasa() {
        optimizar(15, 3).forEach { v ->
            val gastado = v.varilla.cortes.sum() + (v.varilla.cortes.size - 1) * 0.3f
            assertTrue("varilla con ${v.varilla.cortes} se pasa: $gastado", gastado <= 595.001f)
            assertEquals(595f - gastado, v.varilla.restante, 0.01f)
        }
    }
}

class CorteLuchaBilbaoNivelesTest {
    private val piezas = listOf(
        PiezaCorte(67.8f, 6, "a", true), PiezaCorte(210f, 2, "a", true), PiezaCorte(207.9f, 4, "a", true),
        PiezaCorte(304.4f, 1, "a", true), PiezaCorte(54f, 1, "a", true), PiezaCorte(57.1f, 6, "b", true),
        PiezaCorte(210f, 2, "b", true), PiezaCorte(207.9f, 4, "b", true), PiezaCorte(261.4f, 1, "b", true),
        PiezaCorte(54.6f, 1, "b", true), PiezaCorte(198f, 2, "c", true), PiezaCorte(97.7f, 3, "c", true),
        PiezaCorte(173.3f, 1, "c", true), PiezaCorte(22.4f, 4, "c", true), PiezaCorte(72.9f, 6, "d", true),
        PiezaCorte(210f, 2, "d", true), PiezaCorte(205f, 4, "d", true), PiezaCorte(324.5f, 1, "d", true),
        PiezaCorte(55.5f, 1, "d", true), PiezaCorte(220.8f, 2, "e", true), PiezaCorte(66.3f, 2, "e", true),
        PiezaCorte(204.3f, 1, "e", true)
    )

    private fun optimizar(nivel: Int) = CorteOptimizer().optimizarCortesConConfiguracion(
        piezas, listOf(PiezaCorte(595f, 15, "e", true)), 0.3f, nivel
    )

    /** Cada nivel hace lo del anterior y algo más: el retazo mayor nunca sale más chico. */
    @Test
    fun masNivelJuntaIgualOMasElRetazo() {
        var anterior = 0f
        for (nivel in 1..4) {
            val r = optimizar(nivel)
            val mayor = r.maxOf { it.varilla.restante }
            assertTrue("nivel $nivel: retazo mayor $mayor, el anterior era $anterior", mayor >= anterior - 0.01f)
            anterior = mayor
        }
    }

    /** La misma lista da lo mismo dos veces: la búsqueda es por intentos, no por reloj. */
    @Test
    fun laMismaListaDaLoMismo() {
        val a = optimizar(3).map { it.varilla.cortes.sorted() }.sortedBy { it.toString() }
        val b = optimizar(3).map { it.varilla.cortes.sorted() }.sortedBy { it.toString() }
        assertEquals(a, b)
    }
}
