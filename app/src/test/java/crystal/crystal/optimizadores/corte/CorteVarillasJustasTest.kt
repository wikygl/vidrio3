package crystal.crystal.optimizadores.corte

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Lo que reportó el usuario el 2026-10-05 en el optimizador de varillas:
 *  - con 14 varillas avisaba que faltaban; con 15 alcanzaba… y usaba 14;
 *  - subir el nivel no cambiaba ni el tiempo ni el resultado;
 *  - las varillas ya no salían ordenadas por retazo;
 *  - combinaciones malas, con mucho retazo.
 * Los esperados salen de cómo están armados los casos (piezas que llenan la varilla justa), no del código.
 */
class CorteVarillasJustasTest {

    private fun optimizar(piezas: List<PiezaCorte>, varillas: Int, largo: Float, disco: Float, nivel: Int) =
        CorteOptimizer().optimizarCortesConConfiguracion(piezas, listOf(PiezaCorte(largo, varillas, "Varilla", true)), disco, nivel)

    private fun cortadas(r: List<CorteOptimizer.VarillaConReferencias>) = r.sumOf { it.varilla.cortes.size }

    /** Tres piezas que juntas llenan una varilla de 600 justa, con disco de 0.4: 250 + 200 + 149.2 + 2 × 0.4. */
    private val justas = listOf(
        PiezaCorte(250f, 14, "Hoja", true),
        PiezaCorte(200f, 14, "Marco", true),
        PiezaCorte(149.2f, 14, "Riel", true)
    )

    @Test
    fun conLasVarillasJustasAlcanza() {
        val r = optimizar(justas, 14, 600f, 0.4f, 5)
        assertEquals("con 14 varillas justas tienen que salir las 42 piezas", 42, cortadas(r))
        assertEquals(14, r.size)
        r.forEach { assertEquals("cada varilla se llena justa", 0f, it.varilla.restante, 0.01f) }
    }

    @Test
    fun conUnaDeMasUsaLasMismas() {
        val r = optimizar(justas, 15, 600f, 0.4f, 5)
        assertEquals(42, cortadas(r))
        assertEquals("sobra una varilla y no se usa", 14, r.size)
    }

    /**
     * Una lista de taller cualquiera: las varillas que salen con existencia de sobra tienen que
     * alcanzar si se dan justas. Era el aviso de faltante con 14 que con 15 salía en 14.
     */
    @Test
    fun loQueSaleConDeSobraAlcanzaConLasJustas() {
        val piezas = listOf(
            PiezaCorte(183.5f, 4, "Riel", true), PiezaCorte(176.2f, 6, "Jamba", true),
            PiezaCorte(142.8f, 8, "Hoja", true), PiezaCorte(98.4f, 6, "Traslape", true),
            PiezaCorte(71.3f, 10, "Cabezal", true), PiezaCorte(45.6f, 12, "Junquillo", true),
            PiezaCorte(28f, 9, "Tope", true)
        )
        for (nivel in listOf(1, 5)) {
            val conSobra = optimizar(piezas, 40, 600f, 0.3f, nivel)
            val total = piezas.sumOf { it.cantidad }
            assertEquals("con varillas de sobra salen todas (nivel $nivel)", total, cortadas(conSobra))
            val justasN = optimizar(piezas, conSobra.size, 600f, 0.3f, nivel)
            assertEquals("con ${conSobra.size} varillas también salen todas (nivel $nivel)", total, cortadas(justasN))
            assertEquals(conSobra.size, justasN.size)
        }
    }

    /** No puede usar más varillas que el mínimo que exige el largo total. */
    @Test
    fun usaLasVarillasMinimasCuandoEncajan() {
        // 330 + 270 = 600 y 210 + 190 + 200 = 600, con disco 0: 4 varillas sin retazo.
        val piezas = listOf(
            PiezaCorte(330f, 2, "A", true), PiezaCorte(270f, 2, "B", true),
            PiezaCorte(210f, 2, "C", true), PiezaCorte(190f, 2, "D", true), PiezaCorte(200f, 2, "E", true)
        )
        val r = optimizar(piezas, 10, 600f, 0f, 3)
        assertEquals(10, cortadas(r))
        assertEquals("encajan justas en 4 varillas", 4, r.size)
        assertEquals(0.0, r.sumOf { it.varilla.restante.toDouble() }, 0.01)
    }

    @Test
    fun salenOrdenadasDeMenosAMasRetazo() {
        val piezas = listOf(
            PiezaCorte(183.5f, 3, "Riel", true), PiezaCorte(142.8f, 5, "Hoja", true),
            PiezaCorte(71.3f, 7, "Cabezal", true), PiezaCorte(45.6f, 4, "Junquillo", true)
        )
        val r = optimizar(piezas, 20, 600f, 0.3f, 5)
        val retazos = r.map { it.varilla.restante }
        assertEquals("de menos a más retazo: $retazos", retazos.sorted(), retazos)
    }

    @Test
    fun masNivelNoDaPeorResultado() {
        val piezas = listOf(
            PiezaCorte(187.3f, 5, "Riel", true), PiezaCorte(163.9f, 7, "Jamba", true),
            PiezaCorte(121.4f, 9, "Hoja", true), PiezaCorte(88.7f, 11, "Traslape", true),
            PiezaCorte(52.1f, 13, "Junquillo", true)
        )
        val bajo = optimizar(piezas, 40, 600f, 0.3f, 1)
        val alto = optimizar(piezas, 40, 600f, 0.3f, 6)
        assertTrue("nivel 6 no puede usar más varillas que nivel 1", alto.size <= bajo.size)
        // El retazo de varilla gastada sale igual con las mismas varillas; con más nivel se junta más.
        if (alto.size == bajo.size) {
            val juntoAlto = alto.sumOf { it.varilla.restante.toDouble() * it.varilla.restante }
            val juntoBajo = bajo.sumOf { it.varilla.restante.toDouble() * it.varilla.restante }
            assertTrue("con más nivel el retazo queda igual o más junto", juntoAlto >= juntoBajo - 0.01)
        }
    }
}
