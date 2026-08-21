package crystal.crystal.optimizadores.corte

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Regresión del caso real "Isabel": varillas de 595 cm que quedaban a medio llenar (10 piezas de 28
 * y 312 cm de retazo, cuando entran 21).
 *
 * La causa eran las FILAS REPETIDAS: el algoritmo identifica un corte por "longitud + referencia",
 * así que dos filas de 28x10 con la misma referencia compartían clave y el llenado se topaba en la
 * cantidad de una sola fila. Partir la misma pieza en varias filas no puede cambiar el resultado.
 */
class CorteDuplicadosTest {

    private val varillas = listOf(PiezaCorte(595f, 4, "Junkillo", true))
    private val grosor = 0.3f

    private val unaFila = listOf(
        PiezaCorte(62.8f, 1, "P1 Isabel Med,Ang.tope", true),
        PiezaCorte(28f, 20, "P1 Isabel Med,Junkillo", true),
        PiezaCorte(9f, 1, "P1 Isabel Med,Junkillo", true)
    )

    private val filasRepetidas = listOf(
        PiezaCorte(62.8f, 1, "P1 Isabel Med,Ang.tope", true),
        PiezaCorte(28f, 10, "P1 Isabel Med,Junkillo", true),
        PiezaCorte(28f, 10, "P1 Isabel Med,Junkillo", true),
        PiezaCorte(9f, 1, "P1 Isabel Med,Junkillo", true)
    )

    private fun optimizar(piezas: List<PiezaCorte>, nivel: Int) =
        CorteOptimizer().optimizarCortesConConfiguracion(piezas, varillas, grosor, nivel)

    @Test
    fun lasFilasRepetidasDanElMismoResultadoQueUnaSola() {
        for (nivel in listOf(1, 5, 10)) {
            val esperado = optimizar(unaFila, nivel)
            val obtenido = optimizar(filasRepetidas, nivel)

            assertEquals(
                "partir 28x20 en dos filas de 10 no debe cambiar el numero de varillas (nivel $nivel)",
                esperado.size, obtenido.size
            )
            assertEquals(
                "debe cortar todas las piezas igual que con una sola fila (nivel $nivel)",
                esperado.sumOf { it.varilla.cortes.size },
                obtenido.sumOf { it.varilla.cortes.size }
            )
            val retazoEsperado = esperado.sumOf { it.varilla.restante.toDouble() }
            val retazoObtenido = obtenido.sumOf { it.varilla.restante.toDouble() }
            assertEquals(
                "el retazo total debe ser el mismo (nivel $nivel)",
                retazoEsperado, retazoObtenido, 0.5
            )
        }
    }

    @Test
    fun laPrimeraVarillaSeLlenaCompleta() {
        // Antes del arreglo la primera varilla se quedaba en 12 cortes y 239.9 cm de retazo.
        val r = optimizar(filasRepetidas, 5)
        val primera = r.maxByOrNull { it.varilla.cortes.size }?.varilla
            ?: error("no se genero ninguna varilla")
        assertTrue(
            "la varilla mas llena debe superar los 12 cortes, fueron ${primera.cortes.size}",
            primera.cortes.size > 12
        )
        assertTrue(
            "su retazo debe ser mucho menor que los 239.9 cm del error, fue ${primera.restante}",
            primera.restante < 50f
        )
    }
}
