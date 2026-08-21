package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Variante "Viky c": el ancho interior se parte en DOS columnas iguales con un paflón vertical al
 * medio y el alto interior en nDiv filas iguales, así que todas las celdas miden lo mismo.
 *
 * Caso de referencia: puerta 90 x 240 con zócalo 1 y 5 divisiones.
 *   paflón (ancho interior) = (90 - 2·2.2) - 1 - 2·8.25 = 68.1
 *   parante = 198 · parante interno = 198 - 2·8.25 = 181.5
 *   ancho de columna = (68.1 - 8.25) / 2 = 29.925
 *   alto de fila     = (181.5 - 4·8.25) / 5 = 29.7
 */
class VikyCTest {

    private val paflon = 68.1f
    private val parante = 198f
    private val paranteInterno = 181.5f
    private val bastidor = CalculosPuerta.BASTIDOR
    private val nDiv = 5
    private val junki = 1.2f

    @Test
    fun laCeldaEsElInteriorPartidoEnDosColumnasYNDivFilas() {
        assertEquals(29.925f, CalculosPuerta.anchoColumnaVikyC(paflon, bastidor), 0.001f)
        assertEquals(29.7f, CalculosPuerta.altoPano(paranteInterno, nDiv, bastidor), 0.001f)
    }

    /** Marco de la hoja: 2 parantes y los horizontales de ancho completo (bastidor + zócalos). */
    @Test
    fun paflonesConZocaloUno() {
        assertEquals(
            "198 = 2\n68.1 = 2",
            CalculosPuerta.textoPaflonVikyC(paflon, parante, nZocalo = 1, nDiv = nDiv)
        )
    }

    @Test
    fun elZocaloAgregaHorizontalesDeAnchoCompleto() {
        // Con 3 zócalos son 4 horizontales de ancho completo, no 2.
        assertEquals(
            "198 = 2\n68.1 = 4",
            CalculosPuerta.textoPaflonVikyC(paflon, parante, nZocalo = 3, nDiv = nDiv)
        )
    }

    /** Interior: 1 divisor vertical de bastidor a bastidor y 2 horizontales por cada nivel. */
    @Test
    fun piezasDelInterior() {
        assertEquals(
            "181.5 = 1\n29.9 = 8",
            CalculosPuerta.textoInteriorVikyC(paflon, paranteInterno, nDiv, interior = bastidor)
        )
    }

    /**
     * Con un aluminio más delgado adentro (tubo de 3.8) las celdas crecen: el divisor vertical se
     * come 3.8 en vez de 8.25 y cada fila reparte 4 divisores de 3.8.
     *   ancho de columna = (68.1 - 3.8) / 2 = 32.15
     *   alto de fila     = (181.5 - 4·3.8) / 5 = 33.26
     */
    @Test
    fun conTuboDelgadoAdentroLasCeldasCrecen() {
        val tubo = 3.8f
        assertEquals(32.15f, CalculosPuerta.anchoColumnaVikyC(paflon, tubo), 0.001f)
        assertEquals(33.26f, CalculosPuerta.altoPano(paranteInterno, nDiv, tubo), 0.001f)
        assertEquals(
            // 32.15 se imprime 32.1: en float el valor cae apenas por debajo del medio punto.
            "181.5 = 1\n32.1 = 8",
            CalculosPuerta.textoInteriorVikyC(paflon, paranteInterno, nDiv, interior = tubo)
        )
        // El marco de la hoja no cambia: sigue siendo bastidor.
        assertEquals(
            "198 = 2\n68.1 = 2",
            CalculosPuerta.textoPaflonVikyC(paflon, parante, nZocalo = 1, nDiv = nDiv)
        )
        assertEquals(
            "31.6 x 32.8 = 10",
            CalculosPuerta.textoVidriosVikyC(paflon, paranteInterno, nDiv, tubo, junki, mocheta = -1f, marcoSup = 85.6f)
        )
    }

    /** Por celda: el par horizontal a tope y el par vertical descontando los dos junquillos. */
    @Test
    fun junquillosPorCelda() {
        assertEquals(
            "29.9 = 20\n27.3 = 20",
            CalculosPuerta.textoJunkillosVikyC(paflon, paranteInterno, nDiv, bastidor, junki, mocheta = -1f, marcoSup = 85.6f)
        )
    }

    @Test
    fun junquillosConMocheta() {
        assertEquals(
            "29.9 = 20\n27.3 = 20\n85.6 = 2\n27.6 = 2",
            CalculosPuerta.textoJunkillosVikyC(paflon, paranteInterno, nDiv, bastidor, junki, mocheta = 30f, marcoSup = 85.6f)
        )
    }

    /** Diez vidrios iguales: la celda menos 0.4 de holgura en cada lado. */
    @Test
    fun vidriosTodosIguales() {
        assertEquals(
            "29.4 x 29.2 = 10",
            CalculosPuerta.textoVidriosVikyC(paflon, paranteInterno, nDiv, bastidor, junki, mocheta = -1f, marcoSup = 85.6f)
        )
    }

    @Test
    fun vidriosConMocheta() {
        assertEquals(
            // La mocheta usa vidrioM, que descuenta 0.4 en cada lado (no los junquillos).
            "29.4 x 29.2 = 10\n85.1 x 29.5 = 1",
            CalculosPuerta.textoVidriosVikyC(paflon, paranteInterno, nDiv, bastidor, junki, mocheta = 30f, marcoSup = 85.6f)
        )
    }

    @Test
    fun conUnaSolaDivisionNoHayDivisoresHorizontales() {
        assertEquals(
            "181.5 = 1",
            CalculosPuerta.textoInteriorVikyC(paflon, paranteInterno, nDiv = 1, interior = bastidor)
        )
        assertEquals(
            "29.4 x 181 = 2",
            CalculosPuerta.textoVidriosVikyC(paflon, paranteInterno, 1, bastidor, junki, mocheta = -1f, marcoSup = 85.6f)
        )
    }
}
