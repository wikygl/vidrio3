package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Cotas del plano de Taly. La regla vertical va desde la base de la hoja: el zócalo, el horizontal
 * que cierra el vacío abajo, las divisiones y el que lo cierra arriba. La horizontal marca dónde
 * empieza y termina el vidrio, y el ancho interior.
 *
 * Caso: puerta 70 x 240, paflón 48.1, parante 198, zócalo 1.
 *   el vacío del vidrio arranca a (1+1)·8.25 = 16.5 de la base y llega a 198 - 2·8.25 = 181.5
 */
class CotasTalyTest {

    private val bastidor = CalculosPuerta.BASTIDOR
    private val parante = 198f
    private val paflon = 48.1f

    private fun cotas(nDiv: Int, angulo: Float, nZocalo: Int = 1) =
        CalculosPuerta.cotasPanosTaly(
            parante, nZocalo, nDiv, bastidor,
            CalculosPuerta.zonaVidrioTaly(paflon, bastidor).anchoVidrio, angulo
        )

    /** Sin divisiones solo se marca el marco del vacío: zócalo, abajo y arriba. */
    @Test
    fun sinDivisionesSoloElMarcoDelVacio() {
        val (izq, der) = cotas(nDiv = 1, angulo = 0f)
        assertEquals(listOf(8.25f, 16.5f, 181.5f), izq)
        assertEquals(emptyList<Float>(), der)
    }

    /**
     * Cuatro divisiones sin ángulo: el vacío mide 165 y lleva 3 barras, así que los tramos son
     * (165 - 3·8.25) / 4 = 35.0625 y las barras arrancan a 16.5 + 35.06, + 43.31 y + 43.31.
     */
    @Test
    fun conDivisionesRectasSalenLasBarras() {
        val (izq, der) = cotas(nDiv = 4, angulo = 0f)
        assertEquals(6, izq.size)
        assertEquals(8.25f, izq[0], 0.01f)
        assertEquals(16.5f, izq[1], 0.01f)
        assertEquals(51.56f, izq[2], 0.01f)
        assertEquals(94.88f, izq[3], 0.01f)
        assertEquals(138.19f, izq[4], 0.01f)
        assertEquals(181.5f, izq[5], 0.01f)
        assertEquals(emptyList<Float>(), der)
    }

    /** Con ángulo, cada diagonal corta los dos parantes a distinta altura: dos reglas. */
    @Test
    fun conAnguloLasDivisionesSeRepartenEnDosReglas() {
        val (izq, der) = cotas(nDiv = 4, angulo = 45f)
        assert(izq.size > 3) { "la regla izquierda quedó sin cruces: $izq" }
        assert(der.isNotEmpty()) { "la regla derecha quedó vacía" }
        // El marco del vacío sigue estando en la izquierda.
        assert(izq.contains(8.25f) && izq.contains(16.5f))
    }

    /**
     * Horizontales: el vidrio de esta puerta queda en 15.1 con dos pares de paflón, así que arranca
     * a 16.5 del borde interior y termina a 31.6, sobre un ancho interior de 48.1.
     */
    @Test
    fun cotasHorizontalesDelVidrio() {
        val h = CalculosPuerta.cotasParantesTaly(paflon, bastidor)
        assertEquals(3, h.size)
        assertEquals(16.5f, h[0], 0.01f)
        assertEquals(31.6f, h[1], 0.01f)
        assertEquals(48.1f, h[2], 0.01f)
    }
}
