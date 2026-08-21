package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Cotas del plano de Dora, acumuladas desde un origen — no la medida de cada pieza repetida.
 *
 * Vertical: dónde termina la pila de zócalos, el arranque de cada inox de la columna izquierda y el
 * cierre del vacío arriba. Horizontal: dónde arranca cada divisor de columna y el ancho interior.
 *
 * Caso: puerta 70 x 240, paflón 48.1, parante 198, zócalo 1, inox 2.5, 9 divisores.
 */
class CotasDoraTest {

    private val bastidor = CalculosPuerta.BASTIDOR
    private val parante = 198f
    private val paflon = 48.1f
    private val inox = 2.5f

    /** Las tres columnas miden (48.1 - 2·8.25) / 3 = 10.53, así que los divisores caen a 10.5 y 29.3. */
    @Test
    fun horizontalesAcumuladasNoRepetidas() {
        val h = CalculosPuerta.cotasColumnasDora(paflon, bastidor)
        assertEquals(3, h.size)
        assertEquals(10.533f, h[0], 0.01f)
        assertEquals(29.317f, h[1], 0.01f)
        assertEquals(48.1f, h[2], 0.01f)
        // Lo que se veía antes era la misma medida tres veces; ahora cada marca avanza.
        assert(h[0] < h[1] && h[1] < h[2])
    }

    /**
     * Con zócalo 1 la pila mide 8.25 y el vacío llega hasta 198 - 8.25 - 8.25 = 181.5. Los 9 inox
     * reparten ese vacío en 10 secciones de (181.5 - 9·2.5) / 10 = 15.9.
     */
    @Test
    fun verticalesDesdeLaPilaDeZocalos() {
        val v = CalculosPuerta.cotasPanosDora(parante, nZocalo = 1, nInoxIzq = 9, bastidor = bastidor, inox = inox)
        assertEquals(11, v.size)              // pila + 9 divisores + cierre
        assertEquals(8.25f, v.first(), 0.01f)
        assertEquals(24.15f, v[1], 0.01f)     // 8.25 + 15.9
        assertEquals(42.55f, v[2], 0.01f)     // + 15.9 + 2.5
        assertEquals(189.75f, v.last(), 0.01f)
    }

    /** Siempre crecen: es una regla acumulada, no una lista de medidas. */
    @Test
    fun siempreAvanzan() {
        val v = CalculosPuerta.cotasPanosDora(parante, nZocalo = 3, nInoxIzq = 5, bastidor = bastidor, inox = inox)
        v.zipWithNext().forEach { (a, b) -> assert(b > a) { "la cota $b no avanza sobre $a" } }
    }
}
