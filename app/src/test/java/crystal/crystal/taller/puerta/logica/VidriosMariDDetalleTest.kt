package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Medida individual de cada vidrio de "Mari d", para cortarlos uno por uno. El apilado —lo que se
 * compra— sigue verificado aparte en [VidriosMariDHolgurasTest].
 *
 * Lo que se lista es el rectángulo de MENOR ÁREA que envuelve a cada pieza. En los trapecios de las
 * esquinas gana la caja alineada al marco (dos de sus lados son las tapas del paflón); en los
 * paralelogramos del medio gana la caja girada al ángulo del corte, que es bastante más chica
 * (3523 cm² contra 4654 en el caso de 4 divisiones).
 *
 * Puerta 70 x 240, zócalo 1: paflón 48.1, parante interno 181.5, bastidor 8.25.
 */
class VidriosMariDDetalleTest {

    private val paflon = 48.1f
    private val paranteInterno = 181.5f
    private val bastidor = CalculosPuerta.BASTIDOR

    private fun detalle(nDiv: Int, angulo: Float = 45f, parante: Float = paranteInterno): String =
        CalculosPuerta.textoVidriosDetalleMariD(paflon, parante, nDiv, bastidor, angulo)

    private fun piezas(texto: String): Int =
        texto.lines().filter { it.isNotBlank() }.sumOf { it.substringAfterLast("=").trim().toInt() }

    @Test
    fun cuatroDivisiones45_dosEsquinasYDosParalelogramos() {
        // Caso de taller. Las esquinas caben en 48.1 x 48.65 (caja recta) y las del medio en
        // 102.4 x 34.4 (caja girada 45°); menos 0.4 de holgura en cada lado.
        assertEquals("47.7 x 48.2 = 2\n102 x 34 = 2", detalle(4))
    }

    @Test
    fun conZocaloTresYCuatroDivisiones_lasEsquinasSonTriangulos() {
        // Con parante 165 las diagonales no alcanzan las esquinas del paflón: las puntas quedan
        // triángulos de 44.5 de lado (44.1 con holgura) en vez de trapecios de ancho completo.
        assertEquals("44.1 x 44.1 = 2\n99.1 x 31.1 = 2", detalle(nDiv = 4, parante = 165.0f))
    }

    @Test
    fun sinDivisiones_esUnSoloVidrioRectangular() {
        assertEquals("47.7 x 181.1 = 1", detalle(1))
    }

    @Test
    fun elDetalleSiempreListaTantasPiezasComoDivisiones() {
        // Invariante: el recorte geométrico no puede perder ni inventar vidrios.
        assertEquals(4, piezas(detalle(4)))
        assertEquals(6, piezas(detalle(6)))
        assertEquals(7, piezas(detalle(7)))
        assertEquals(11, piezas(detalle(11)))
        assertEquals(6, piezas(detalle(6, angulo = 32f)))
        assertEquals(8, piezas(detalle(8, angulo = 60f)))
    }

    /**
     * La mocheta es un rectángulo común: no entra ni en el detalle ni en el apilado, va en su propia
     * fila. Si se colara en las dos listas saldría repetida en pantalla y contada doble al archivar.
     */
    @Test
    fun laMochetaNoSeMezclaConLasPiezasDiagonales() {
        // Con junquillo, la holgura del vidrio es 0.5: cada pieza baja 0.1 respecto de los 0.4 de antes.
        val detalleConMocheta = CalculosPuerta.textoVidriosDetalle(
            variante = "Mari d",
            paflon = paflon,
            bastidor = bastidor,
            nDiv = 4,
            paranteInterno = paranteInterno,
            angulo = 45f,
            jun = 1.2f
        )
        assertEquals("47.6 x 48.1 = 2\n101.9 x 33.9 = 2", detalleConMocheta)

        val apilado = CalculosPuerta.textoVidrios(
            variante = "Mari d",
            jun = 1.2f,
            paflon = paflon,
            bastidor = bastidor,
            nDiv = 4,
            paranteInterno = paranteInterno,
            marcoSuperior = 65.6f,
            mocheta = 30f,
            angulo = 45f
        )
        // Cuatro vidrios apilan, así que el alto baja 4 x 0.1 y el ancho, 0.1.
        assertEquals("47.6 x 144.5 = 1", apilado)

        assertEquals("65.1 x 29.5 = 1", CalculosPuerta.textoVidrioMocheta("Mari d", 65.6f, 30f, jun = 1.2f))
    }

    @Test
    fun sinMochetaNoHayFilaDeMocheta() {
        assertEquals("", CalculosPuerta.textoVidrioMocheta("Mari d", 65.6f, -1f))
    }

    @Test
    fun otrasVariantesNoTienenDetalleNiMochetaAparte() {
        assertEquals(
            "",
            CalculosPuerta.textoVidriosDetalle("Mari h", paflon, bastidor, 4, paranteInterno, 0f)
        )
        assertEquals("", CalculosPuerta.textoVidrioMocheta("Mari h", 65.6f, 30f))
    }
}
