package crystal.crystal.taller.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * `df1` trunca, así que cada pieza pierde hasta 0.099 y SIEMPRE hacia abajo. En una ventana de
 * once módulos eso son 8 mm que faltan al llegar al otro extremo. El reparto devuelve ese resto
 * en décimas, sin pasarse nunca de la medida exacta.
 */
class RedondeoRepartidoTest {

    private fun sumaDeLineas(texto: String): Float =
        texto.lines().filter { it.isNotBlank() }.sumOf { linea ->
            val (m, n) = linea.split("=").map { it.trim() }
            (m.toFloat() * n.toInt()).toDouble()
        }.toFloat()

    @Test
    fun `nunca se pasa de la suma exacta`() {
        for (piezas in 1..20) {
            for (medida in listOf(59.47273f, 58.636364f, 117.2f, 40.05f, 33.333f, 12.999f)) {
                val texto = NovaCalculos.lineasConRedondeoRepartido(List(piezas) { medida })
                val exacto = medida * piezas
                val repartido = sumaDeLineas(texto)
                assertTrue(
                    "piezas=$piezas medida=$medida repartido=$repartido exacto=$exacto",
                    repartido <= exacto + 0.001f
                )
                // Y no se queda corto por más de una décima: ese es todo el margen que queda.
                assertTrue(
                    "piezas=$piezas medida=$medida se queda corto $repartido vs $exacto",
                    exacto - repartido < 0.1f + 0.001f
                )
            }
        }
    }

    @Test
    fun `once piezas de 59_47 dejan de perder 8 milimetros`() {
        val texto = NovaCalculos.lineasConRedondeoRepartido(List(11) { 59.47273f })
        val exacto = 59.47273f * 11
        // Truncando las once: 59.4 x 11 = 653.4, ocho milímetros menos.
        assertEquals(653.4f, 59.4f * 11, 0.01f)
        assertTrue("faltan más de 0.1", exacto - sumaDeLineas(texto) < 0.11f)
    }

    @Test
    fun `si no hay nada que repartir todas quedan iguales`() {
        val texto = NovaCalculos.lineasConRedondeoRepartido(List(4) { 60f })
        assertEquals("60 = 4", texto)
    }

    @Test
    fun `la ventana de 650 con 11 divisiones`() {
        val u = NovaInaCalculos.calcularTextoU(650f, 160f, 114.2f, 1f, 11, 0f)
        // Dos fijos sueltos y dos rachas de dos: la racha suma en exacto y se trunca una vez.
        assertTrue("U inesperada:\n$u", u.contains("59.5 = 2"))
        assertTrue("U inesperada:\n$u", u.contains("118.9 = 2"))
        val otros = NovaInaCalculos.calcularOtrosAluminios(650f, 160f, 114.2f, 11, 0f)
        assertEquals("59.5 = 3\n59.4 = 2", otros.hache)
    }
}
