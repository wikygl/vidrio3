package crystal.crystal.optimizadores.planchas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Caso real "Mirta": 28 cortes contra un inventario que NO alcanza (1 plancha de 77.7×107 cm y
 * cuatro retazos). Cubre los dos defectos del modelo anterior de "plancha base":
 *
 *  - agregar un retazo al inventario subía los cortes faltantes de 17 a 19;
 *  - poner un retazo como primera fila de la lista hacía que el cálculo se derrumbara, porque esa
 *    fila pasaba a ser la medida de las planchas nuevas y cualquier pieza más grande anulaba el
 *    intento completo.
 *
 * Se usa el nivel Rápido para que los tests sean veloces.
 */
class InventarioPlanchasTest {

    private data class P(val w: Int, val h: Int, val n: Int)

    // Medidas en mm (la app las ingresa en cm).
    private val mirta = listOf(
        P(183, 663, 5), P(183, 310, 2), P(420, 600, 3),
        P(390, 105, 1), P(380, 105, 1), P(370, 105, 1),
        P(390, 480, 1), P(390, 393, 1), P(380, 480, 1), P(380, 393, 1),
        P(355, 460, 1), P(370, 393, 1),
        P(270, 333, 2), P(270, 315, 1), P(493, 333, 2),
        P(385, 333, 2), P(385, 315, 1), P(470, 305, 1)
    )

    private val totalCortes = mirta.sumOf { it.n }

    private fun piezas() = mirta.mapIndexed { i, p -> PiezaPlancha("p$i", "Mirta", p.w, p.h, p.n) }

    private val planchaEntera = PlanchaStock("77.7x107", 777, 1070, 1)
    private val retazos = listOf(
        PlanchaStock("66x51", 660, 510, 1, esRetazo = true),
        PlanchaStock("24.5x50", 245, 500, 1, esRetazo = true),
        PlanchaStock("17x58", 170, 580, 1, esRetazo = true)
    )
    private val retazoExtra = PlanchaStock("28x36", 280, 360, 1, esRetazo = true)

    private fun correr(stock: List<PlanchaStock>) = OptimizadorPlanchas.optimizar(
        piezas = piezas(),
        stock = stock,
        intensidad = IntensidadOptimizacionPlanchas.RAPIDO
    )

    /** Todo corte ubicado debe salir de una unidad que exista en el inventario. */
    private fun verificarCoherencia(r: ResultadoOptimizacionPlanchas, stock: List<PlanchaStock>) {
        val colocados = r.planchas.sumOf { it.cortes.size }
        assertEquals(
            "cada corte debe estar ubicado o reportado como faltante, sin perderse ninguno",
            totalCortes, colocados + r.piezasSinUbicar.size
        )
        val disponibles = stock.sumOf { it.cantidad }
        r.planchas.groupingBy { it.anchoMm to it.altoMm }.eachCount().forEach { (medida, usadas) ->
            val tope = stock.filter { (it.anchoMm to it.altoMm) == medida }.sumOf { it.cantidad }
            assertTrue(
                "no se puede usar más material del que hay: ${medida.first}x${medida.second} " +
                        "usadas $usadas de $tope",
                usadas <= tope
            )
        }
        assertTrue("no debe inventar unidades", r.planchas.size <= disponibles)
    }

    @Test
    fun agregarUnRetazoNuncaAumentaLosFaltantes() {
        val sinExtra = correr(listOf(planchaEntera) + retazos)
        val conExtra = correr(listOf(planchaEntera) + retazos + retazoExtra)

        verificarCoherencia(sinExtra, listOf(planchaEntera) + retazos)
        verificarCoherencia(conExtra, listOf(planchaEntera) + retazos + retazoExtra)

        assertTrue(
            "con más material no pueden faltar más cortes: " +
                    "${sinExtra.piezasSinUbicar.size} sin el retazo extra, " +
                    "${conExtra.piezasSinUbicar.size} con él",
            conExtra.piezasSinUbicar.size <= sinExtra.piezasSinUbicar.size
        )
    }

    @Test
    fun elOrdenDeLaListaNoCambiaLoQueSePuedeCortar() {
        val soloPlancha = correr(listOf(planchaEntera))
        val retazoPrimero = correr(retazos + retazoExtra + planchaEntera)

        verificarCoherencia(retazoPrimero, retazos + retazoExtra + listOf(planchaEntera))

        assertTrue(
            "la plancha entera debe usarse aunque esté escrita al final de la lista",
            retazoPrimero.planchas.any { it.anchoMm == 777 && it.altoMm == 1070 }
        )
        assertTrue(
            "con la plancha más los retazos nunca se puede cortar menos que con la plancha sola: " +
                    "${retazoPrimero.planchas.sumOf { it.cortes.size }} contra " +
                    "${soloPlancha.planchas.sumOf { it.cortes.size }}",
            retazoPrimero.planchas.sumOf { it.cortes.size } >=
                    soloPlancha.planchas.sumOf { it.cortes.size }
        )
    }
}
