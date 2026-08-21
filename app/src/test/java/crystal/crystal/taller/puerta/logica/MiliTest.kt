package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Mili: un molinete de tubos de 3.8 —dos verticales y dos horizontales girados 180° entre sí— parte
 * el vacío en tres columnas y seis filas, dejando cinco paños: dos altos a los costados, dos anchos
 * arriba y abajo, y uno al medio.
 *
 * Caso de taller: puerta 70 x 240, marco 2.2, zócalo 1.
 *   paflón = 48.1 · parante interno = 198 - 2·8.25 = 181.5
 *   columna = (48.1 - 2·3.8) / 3 = 13.5 · fila = (181.5 - 2·3.8) / 6 = 28.9833
 */
class MiliTest {

    private val paflon = 48.1f
    private val paranteInterno = 181.5f
    private val junki = 1.2f
    private val marcoSup = 65.6f

    @Test
    fun elMolineteRepartElVacioEnTresColumnasYSeisFilas() {
        assertEquals(13.5f, CalculosPuerta.anchoColumnaMili(paflon), 0.001f)
        assertEquals(28.9833f, CalculosPuerta.altoFilaMili(paranteInterno), 0.001f)
    }

    /**
     * El paflón de ancho completo se cuenta por zócalos: el bastidor superior más uno por zócalo. Las
     * divisiones no aportan, porque el molinete se arma con tubo y va en su propio renglón.
     */
    @Test
    fun paflonesDelMarcoDeLaHoja() {
        assertEquals("198 = 2\n48.1 = 2", CalculosPuerta.textoPaflonMili(paflon, parante = 198f, nZocalo = 1))
        assertEquals("198 = 2\n48.1 = 4", CalculosPuerta.textoPaflonMili(paflon, parante = 198f, nZocalo = 3))
    }

    /**
     * Los verticales llegan del bastidor de un extremo a la barra del otro: 181.5 - 28.98 - 3.8. Los
     * horizontales cruzan dos columnas y el tubo del medio: 2·13.5 + 3.8.
     */
    @Test
    fun tubosInternos() {
        assertEquals("148.7 = 2\n30.8 = 2", CalculosPuerta.textoTubosMili(paflon, paranteInterno))
    }

    /**
     * Cinco paños: 13.5 x 148.72 (×2), 30.8 x 28.98 (×2) y 13.5 x 115.93 el del medio. Los anchos de
     * 13.5 se suman en un solo renglón: 4 de los altos + 2 del centro.
     */
    @Test
    fun junquillosDeLosCincoPanos() {
        assertEquals(
            "13.5 = 6\n146.3 = 4\n30.8 = 4\n26.6 = 4\n113.5 = 2",
            CalculosPuerta.textoJunkillosMili(paflon, paranteInterno, junki, mocheta = -1f, marcoSup = marcoSup)
        )
    }

    @Test
    fun vidriosDeLosCincoPanos() {
        assertEquals(
            "13 x 148.2 = 2\n30.3 x 28.5 = 2\n13 x 115.4 = 1",
            CalculosPuerta.textoVidriosMili(paflon, paranteInterno, junki, mocheta = -1f, marcoSup = marcoSup)
        )
    }

    @Test
    fun conMochetaSeAgregaSuParYSuVidrio() {
        assertEquals(
            "13.5 = 6\n146.3 = 4\n30.8 = 4\n26.6 = 4\n113.5 = 2\n65.6 = 2\n27.6 = 2",
            CalculosPuerta.textoJunkillosMili(paflon, paranteInterno, junki, mocheta = 30f, marcoSup = marcoSup)
        )
        assertEquals(
            "13 x 148.2 = 2\n30.3 x 28.5 = 2\n13 x 115.4 = 1\n65.1 x 29.5 = 1",
            CalculosPuerta.textoVidriosMili(paflon, paranteInterno, junki, mocheta = 30f, marcoSup = marcoSup)
        )
    }

    /**
     * Cotas del plano. Las verticales van desde la base: el zócalo (8.25), el arranque del tubo
     * inferior (8.25 + 28.98) y el del superior (8.25 + 181.5 - 28.98 - 3.8). Las horizontales, desde
     * el borde interior izquierdo: los dos tubos verticales y el ancho interior.
     */
    @Test
    fun cotasDelPlano() {
        val cotas = CalculosPuerta.cotasPanosMili(parante = 198f, nZocalo = 1, bastidor = CalculosPuerta.BASTIDOR)
        assertEquals(3, cotas.size)
        assertEquals(8.25f, cotas[0], 0.001f)
        assertEquals(37.2333f, cotas[1], 0.001f)
        assertEquals(156.9667f, cotas[2], 0.001f)

        val cotasH = CalculosPuerta.cotasColumnasMili(paflon)
        assertEquals(3, cotasH.size)
        assertEquals(13.5f, cotasH[0], 0.001f)
        assertEquals(30.8f, cotasH[1], 0.001f)
        assertEquals(48.1f, cotasH[2], 0.001f)
    }

    /**
     * Los cinco paños y los cuatro tubos tienen que llenar exactamente el vacío. Es la comprobación
     * que dice si el reparto del molinete está bien planteado.
     */
    @Test
    fun losPanosYLosTubosLlenanElVacio() {
        val col = CalculosPuerta.anchoColumnaMili(paflon)
        val fila = CalculosPuerta.altoFilaMili(paranteInterno)
        val tubo = CalculosPuerta.TUBO_MILI
        val altoLargo = paranteInterno - fila - tubo
        val anchoLargo = 2f * col + tubo
        val areaPanos = 2f * (col * altoLargo) +
            2f * (anchoLargo * fila) +
            col * (paranteInterno - 2f * fila - 2f * tubo)
        val areaTubos = 2f * (tubo * altoLargo) + 2f * (tubo * anchoLargo)
        assertEquals(paflon * paranteInterno, areaPanos + areaTubos, 0.05f)
    }
}
