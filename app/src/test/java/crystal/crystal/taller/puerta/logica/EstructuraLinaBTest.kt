package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Estructura interior de "Lina b", que es lo que muestra su plano: el parante que separa las dos
 * columnas y los rellenos horizontales que caen detrás de cada junta de panel.
 *
 * Caso de taller: puerta 70 x 240, hoja 199, bastidor 8.25, relleno 3.8, gruma 0.8, 5 divisiones.
 *   hoja      = 70 - (2·2.2 + 1) = 64.6
 *   panel     = (70 - 4.4 - 0.8) / 4 = 16.2
 *   relleno   = (64.6 - (16.2 + 0.4)) - (8.25 + 1.9) = 37.85
 */
class EstructuraLinaBTest {

    private val med1 = 70f
    private val hH = 199f
    private val bastidor = 8.25f
    private val relleno = 3.8f
    private val gruna = 0.8f

    @Test
    fun elLargoDelRellenoSaleDelPanelYDelEjeDelParante() {
        assertEquals(16.2f, CalculosLina.panelAltoH(med1, 2.2f, gruna), 0.01f)
        assertEquals(37.85f, CalculosLina.largoRellenoB(med1, 2.2f, gruna, relleno, 0f, bastidor), 0.01f)
    }

    /**
     * El relleno llega hasta el eje del parante, así que el eje queda a 64.6 - 8.25 - 37.85 = 18.5
     * del borde de la hoja. La pieza arranca medio relleno antes, y desde el borde interior del
     * bastidor eso es 18.5 - 1.9 - 8.25 = 8.35.
     */
    @Test
    fun elParanteArrancaDondeTerminaElRelleno() {
        assertEquals(8.35f, CalculosLina.parantePosicionB(med1, 2.2f, gruna, relleno, 0f, bastidor), 0.01f)
    }

    /**
     * Con 5 divisiones hay 4 juntas. Los paneles miden (198 - 4·0.8) / 5 = 38.96, así que los
     * rellenos caen a 38.96, 78.72, 118.48 y 158.24 de la base.
     */
    @Test
    fun losRellenosCaenEnCadaJuntaDePanel() {
        val alturas = CalculosLina.alturasRellenoB(hH, divisiones = 5, gruna = gruna)
        assertEquals(4, alturas.size)
        assertEquals(38.96f, alturas[0], 0.01f)
        assertEquals(78.72f, alturas[1], 0.01f)
        assertEquals(118.48f, alturas[2], 0.01f)
        assertEquals(158.24f, alturas[3], 0.01f)
    }

    /** Todos caen dentro del vacío del bastidor, entre 8.25 y 189.75. */
    @Test
    fun ningunRellenoSeSaleDelVacio() {
        val alturas = CalculosLina.alturasRellenoB(hH, divisiones = 5, gruna = gruna)
        val alto = CalculosLina.altoBastidor(hH)
        alturas.forEach { a ->
            assert(a > bastidor && a + relleno < alto - bastidor) { "el relleno a $a se sale del vacío" }
        }
    }

    /** Una sola división no deja juntas, así que no hay rellenos horizontales. */
    @Test
    fun sinJuntasNoHayRellenos() {
        assertEquals(emptyList<Float>(), CalculosLina.alturasRellenoB(hH, divisiones = 1, gruna = gruna))
    }
}
