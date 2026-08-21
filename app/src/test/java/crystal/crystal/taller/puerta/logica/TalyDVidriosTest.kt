package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * "Taly d" reparte sus vidrios diagonales con la misma geometría que "Mari d": cada pieza sale del
 * recorte real de la zona, no de una sola medida para todas.
 *
 * Antes se calculaba un único tamaño —el alto de la sección más ancho·tan(ángulo)— y se repetía
 * nDiv veces, así que las piezas de las puntas, que son triángulos, salían del mismo tamaño que los
 * paralelogramos del medio.
 *
 * La zona de vidrio de Taly no es el paflón entero: se le suman pares de paflones a los costados
 * hasta que lo que queda baja de 20. Para paflón 48.1 y bastidor 8.25 quedan 31.6, y con parante
 * interno 181.5 la zona mide 31.6 x 165. La holgura es la de Taly: 2 junquillos más 0.5.
 */
class TalyDVidriosTest {

    private val zonaAncho = 31.6f
    private val zonaAlto = 165f
    private val bastidor = CalculosPuerta.BASTIDOR
    private val holgura = 2f * 1.2f + 0.5f   // junquillo 1.2

    private fun detalle(nDiv: Int, angulo: Float) =
        CalculosPuerta.textoVidriosDetalleMariD(zonaAncho, zonaAlto, nDiv, bastidor, angulo, holgura)

    private fun apilado(nDiv: Int, angulo: Float) =
        CalculosPuerta.textoVidriosMariD(zonaAncho, zonaAlto, nDiv, bastidor, angulo, holgura)

    private fun piezas(texto: String): Int =
        texto.lines().filter { it.isNotBlank() }.sumOf { it.substringAfterLast("=").trim().toInt() }

    /** Con ángulo, las piezas dejan de ser todas iguales: las puntas son más chicas. */
    @Test
    fun lasPiezasDejanDeSerTodasIguales() {
        val texto = detalle(nDiv = 4, angulo = 45f)
        assertEquals(4, piezas(texto))
        // Al menos dos medidas distintas: las puntas contra las del medio.
        assert(texto.lines().size >= 2) { "esperaba varias medidas, salió: $texto" }
    }

    /** El recorte no puede perder ni inventar vidrios, en ningún ángulo. */
    @Test
    fun siempreSalenTantasPiezasComoDivisiones() {
        assertEquals(3, piezas(detalle(nDiv = 3, angulo = 45f)))
        assertEquals(5, piezas(detalle(nDiv = 5, angulo = 32f)))
        assertEquals(6, piezas(detalle(nDiv = 6, angulo = 60f)))
    }

    /** El apilado es el rectángulo del que se cortan todas: una sola entrada. */
    @Test
    fun elApiladoEsUnSoloRectangulo() {
        assertEquals(1, piezas(apilado(nDiv = 4, angulo = 45f)))
    }

    /** Sin divisiones es un vidrio rectangular con la holgura de Taly: 31.6 - 2.9 y 165 - 2.9. */
    @Test
    fun sinDivisionesEsUnRectangulo() {
        assertEquals("28.7 x 162.1 = 1", detalle(nDiv = 1, angulo = 45f))
    }
}
