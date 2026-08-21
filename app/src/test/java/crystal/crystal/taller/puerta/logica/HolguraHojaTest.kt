package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * El juego entre la hoja y el marco. Era un centímetro fijo dentro de la fórmula del ancho de hoja;
 * ahora se ingresa, porque no todos los marcos ni todas las bisagras piden el mismo.
 *
 * Puerta de 70 con marco 2.2 y bastidor de paflón: el vano libre son 65.6 y de ahí salen los
 * paflones de arriba y abajo del bastidor.
 */
class HolguraHojaTest {

    private val bastidor = CalculosPuerta.BASTIDOR
    private val marco = CalculosPuerta.MARCO

    @Test
    fun elCentimetroDeSiempreEsElValorPorOmision() {
        assertEquals(64.6f, CalculosPuerta.anchoHoja(70f, marco, marco), 0.001f)
        assertEquals(48.1f, CalculosPuerta.paflon(70f, marco, bastidor), 0.001f)
    }

    /** Caso del taller: con medio centímetro de juego el paflón pasa de 48.1 a 48.6. */
    @Test
    fun conMedioCentimetroLaHojaGanaMedio() {
        assertEquals(48.6f, CalculosPuerta.paflon(70f, marco, bastidor, holgura = 0.5f), 0.001f)
    }

    @Test
    fun sinJuegoLaHojaLlenaElVano() {
        assertEquals(65.6f, CalculosPuerta.anchoHoja(70f, marco, marco, holgura = 0f), 0.001f)
        assertEquals(49.1f, CalculosPuerta.paflon(70f, marco, bastidor, holgura = 0f), 0.001f)
    }

    /**
     * Cuando la puerta se pega a una ventana, ese lado no lleva canal sino tubo de 2.5: los dos
     * marcos entran por separado y la holgura se descuenta una sola vez.
     */
    @Test
    fun losDosMarcosPuedenSerDistintos() {
        assertEquals(64.9f, CalculosPuerta.anchoHoja(70f, marco, 2.5f, holgura = 0.4f), 0.001f)
    }
}
