package crystal.crystal.optimizadores.planchas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La veta de la melamina corre a lo largo de la plancha (los 244 de una de 244 x 183). Las piezas
 * se escriben ancho x alto; con la veta a lo alto, el alto de cada pieza va a lo largo de la plancha.
 */
class VetaPlanchasTest {

    private val plancha = PlanchaStock("244x183", 2440, 1830, 10)
    // Una puerta de 37.9 x 223 y un lateral de 57.9 x 230, como salen del ropero (ancho x alto).
    private val piezas = listOf(
        PiezaPlancha("puerta", "Puerta", 379, 2230, 2),
        PiezaPlancha("lateral", "Lateral", 579, 2300, 2)
    )

    private fun optimizar(veta: VetaPlanchas, stock: List<PlanchaStock> = listOf(plancha)): ResultadoOptimizacionPlanchas {
        val (p, s) = VetaPlanchas.preparar(veta, piezas, stock)
        return VetaPlanchas.devolver(veta, OptimizadorPlanchas.optimizar(p, s, intensidad = IntensidadOptimizacionPlanchas.RAPIDO))
    }

    @Test
    fun a_lo_alto_el_alto_de_cada_pieza_va_a_lo_largo_de_la_plancha() {
        val r = optimizar(VetaPlanchas.ALTO)
        assertEquals(0, r.piezasSinUbicar.size)
        val cortes = r.planchas.flatMap { it.cortes }
        assertEquals(4, cortes.size)
        // En la plancha, el lado largo del corte va a lo ancho (2440), y se marcan como giradas
        // respecto de como se escribieron: leídas de vuelta son su ancho x alto.
        cortes.forEach { c ->
            assertTrue(c.rotada)
            assertTrue("${c.anchoMm}x${c.altoMm}", c.anchoMm > c.altoMm)
        }
        assertEquals(setOf(379 to 2230, 579 to 2300), cortes.map { it.altoMm to it.anchoMm }.toSet())
    }

    @Test
    fun la_plancha_escrita_de_pie_se_acuesta_para_que_la_veta_vaya_a_lo_largo() {
        // La misma plancha escrita 183 x 244: la veta sigue yendo por los 244.
        val r = optimizar(VetaPlanchas.ALTO, listOf(PlanchaStock("183x244", 1830, 2440, 10)))
        assertEquals(0, r.piezasSinUbicar.size)
        assertTrue(r.planchas.all { it.anchoMm == 2440 && it.altoMm == 1830 })
    }

    @Test
    fun a_lo_ancho_las_piezas_de_mas_de_183_de_alto_no_entran_y_vuelven_como_se_escribieron() {
        val r = optimizar(VetaPlanchas.ANCHO)
        assertTrue(r.planchas.flatMap { it.cortes }.none { it.rotada })
        // 223 y 230 de alto no caben en los 183 sin girarlas.
        assertEquals(4, r.piezasSinUbicar.size)
        assertTrue(r.piezasSinUbicar.all { it.anchoMm < it.altoMm })
    }

    @Test
    fun libre_se_giran_como_mejor_entran() {
        val r = optimizar(VetaPlanchas.LIBRE)
        assertEquals(0, r.piezasSinUbicar.size)
    }
}
