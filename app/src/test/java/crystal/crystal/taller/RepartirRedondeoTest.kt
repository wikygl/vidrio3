package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Al truncar a un decimal cada pieza pierde hasta 0.099 y siempre hacia abajo, así que el error
 * se acumula: once piezas de 59.47273 dejan 8 mm sin cubrir. El reparto devuelve ese resto en
 * décimas, sin pasarse nunca de la medida exacta.
 */
class RepartirRedondeoTest {

    private fun suma(texto: String): Float =
        texto.lines().filter { it.isNotBlank() }.sumOf { linea ->
            val (m, n) = linea.split("=").map { it.trim() }
            (m.toFloat() * n.toInt()).toDouble()
        }.toFloat()

    @Test
    fun `nunca se pasa de la suma exacta y no deja más de una décima`() {
        for (piezas in 1..20) {
            for (medida in listOf(59.47273f, 58.636364f, 33.333f, 12.999f, 117.2f, 40.05f, 7.07f)) {
                val texto = MaterialesTexto.repartirRedondeo(List(piezas) { medida })
                val exacto = medida * piezas
                val repartido = suma(texto)
                assertTrue(
                    "se pasa: piezas=$piezas medida=$medida -> $repartido > $exacto",
                    repartido <= exacto + 0.001f
                )
                assertTrue(
                    "se queda corto: piezas=$piezas medida=$medida -> $repartido vs $exacto",
                    exacto - repartido < 0.1f + 0.001f
                )
                // La cantidad de piezas no cambia, solo su medida.
                val total = texto.lines().filter { it.isNotBlank() }
                    .sumOf { it.split("=")[1].trim().toInt() }
                assertEquals("piezas=$piezas medida=$medida", piezas, total)
            }
        }
    }

    @Test
    fun `once piezas de 59_47 recuperan los 8 milimetros`() {
        val texto = MaterialesTexto.repartirRedondeo(List(11) { 59.47273f })
        assertEquals("59.5 = 8\n59.4 = 3", texto)
        // Truncando las once serían 59.4 x 11 = 653.4, ocho milímetros menos.
        assertTrue(suma(texto) > 653.4f)
    }

    @Test
    fun `el caso que falla con flotantes`() {
        // 33.3f + 0.1f da 33.399998, que al truncar vuelve a 33.3: por eso se reparte en décimas
        // enteras. Cuatro piezas de 33.333 pierden 0.132, o sea una décima entera.
        assertEquals("33.4 = 1\n33.3 = 3", MaterialesTexto.repartirRedondeo(List(4) { 33.333f }))
    }

    @Test
    fun `si no hay nada que repartir quedan todas iguales`() {
        assertEquals("60 = 4", MaterialesTexto.repartirRedondeo(List(4) { 60f }))
        assertEquals("", MaterialesTexto.repartirRedondeo(emptyList()))
    }

    @Test
    fun `medidas distintas se agrupan por su valor`() {
        val texto = MaterialesTexto.repartirRedondeo(listOf(50.05f, 50.05f, 30.02f))
        assertEquals(3, texto.lines().sumOf { it.split("=")[1].trim().toInt() })
    }
}
