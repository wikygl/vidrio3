package crystal.crystal.optimizadores.planchas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Con la rotación bloqueada (la veta de la melamina manda) ninguna pieza puede salir girada.
 * El paso que reacomoda las planchas poco aprovechadas (solo en Normal y Profundo) rearmaba las
 * piezas con la rotación permitida a la fuerza, y en ese paso el bloqueo no valía.
 */
class RotacionBloqueadaTest {

    @Test
    fun con_la_rotacion_bloqueada_ninguna_pieza_sale_girada() {
        val medidas = listOf(Triple(500, 1200, 6), Triple(300, 800, 8), Triple(1100, 400, 5), Triple(450, 1500, 3))
        val piezas = medidas.mapIndexed { i, (w, h, n) -> PiezaPlancha("p$i", "pieza $i", w, h, n, rotacionPermitida = false) }
        val res = OptimizadorPlanchas.optimizar(
            piezas, listOf(PlanchaStock("244x183", 2440, 1830, 20)),
            intensidad = IntensidadOptimizacionPlanchas.NORMAL
        )
        assertEquals(0, res.piezasSinUbicar.size)
        val cortes = res.planchas.flatMap { it.cortes }
        assertEquals(medidas.sumOf { it.third }, cortes.size)
        assertTrue("alguna pieza salió girada", cortes.none { it.rotada })
        // Cada corte con el ancho y el alto de su pieza, tal como se escribió.
        val porPieza = piezas.associateBy { it.id }
        cortes.forEach { c ->
            val p = porPieza.getValue(c.piezaId.substringBeforeLast('-'))
            assertEquals(p.anchoMm, c.anchoMm)
            assertEquals(p.altoMm, c.altoMm)
        }
    }
}
