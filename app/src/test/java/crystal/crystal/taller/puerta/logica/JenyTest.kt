package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Jeny: el cuerpo es una Mari h —paños con bastidor de paflón, junquillo y vidrio— y encima del
 * vidrio de cada paño va una rejilla de 2.5, montada por delante, que se lista aparte.
 *
 * Caso de taller: puerta 70 x 240, marco 2.2, zócalo 1.
 *   paflón = 48.1 · parante interno = 181.5
 *   con 3 columnas, cada una mide (48.1 - 2·2.5) / 3 = 14.3667
 */
class JenyTest {

    private val paflon = 48.1f
    private val paranteInterno = 181.5f

    private fun rejilla(variante: String, nDiv: Int, cols: Int, filas: Int, altoPano: Float = paranteInterno) =
        CalculosPuerta.textoRejillaJeny(variante, paflon, altoPano, nDiv, cols, filas)

    @Test
    fun laColumnaEsElVacioRepartidoEntreLasPiezasDePie() {
        assertEquals(14.3667f, CalculosPuerta.anchoCeldaJeny(paflon, 3), 0.001f)
        assertEquals(48.1f, CalculosPuerta.anchoCeldaJeny(paflon, 1), 0.001f)
    }

    /**
     * Cuadrícula pareja: 2 piezas de pie y, con 4 filas, 3 acostadas en cada una de las 3 columnas.
     */
    @Test
    fun rejillaDeLaVarianteBase() {
        assertEquals("181.5 = 2\n14.4 = 9", rejilla("Jeny", nDiv = 1, cols = 3, filas = 4))
    }

    /**
     * Intercalada: las columnas impares llevan 3 acostadas y la par 2 — 3 + 2 + 3 = 8.
     */
    @Test
    fun rejillaIntercaladaDeJenyC() {
        assertEquals("181.5 = 2\n14.4 = 8", rejilla("Jeny c", nDiv = 1, cols = 3, filas = 4))
    }

    /** Con dos paños se repite todo: las piezas de pie miden el alto del paño, no el del vacío. */
    @Test
    fun conDosPanosSeRepiteLaRejillaEnCadaUno() {
        // Dos paños de 86.6 (181.5 menos el paflón divisor, entre dos).
        assertEquals("86.6 = 4\n14.4 = 18", rejilla("Jeny", nDiv = 2, cols = 3, filas = 4, altoPano = 86.625f))
    }

    /**
     * "Jeny r" no usa columnas ni filas: el recuadro es proporción fija del paño —cinco octavos del
     * ancho y siete décimos del alto—, que es la del diseño.
     *   recuadro          = 48.1·5/8 = 30.06  x  181.5·7/10 = 127.05
     *   amarres de pie    = (181.5 - 127.05) / 2 = 27.23
     *   amarres acostados = (48.1 - 30.06) / 2 = 9.02
     *
     * Los cuatro lados del recuadro van enteros: se cortan a 45° y se encuentran en la esquina, así
     * que el acostado mide 30.06 y no 30.06 menos los dos perfiles de pie.
     */
    @Test
    fun recuadroDeJenyR() {
        assertEquals(30.0625f, CalculosPuerta.anchoRecuadroJenyR(paflon), 0.001f)
        assertEquals(127.05f, CalculosPuerta.altoRecuadroJenyR(paranteInterno), 0.001f)
        assertEquals(
            "127 = 2\n30.1 = 2\n27.2 = 2\n9 = 4",
            rejilla("Jeny r", nDiv = 1, cols = 3, filas = 4)
        )
    }

    @Test
    fun elRecuadroSeRepiteEnCadaPano() {
        // Dos paños de 86.625: el recuadro y los amarres se calculan sobre el paño, no sobre el
        // vacío, y las cantidades se duplican.
        assertEquals(
            "60.6 = 4\n30.1 = 4\n13 = 4\n9 = 8",
            rejilla("Jeny r", nDiv = 2, cols = 3, filas = 4, altoPano = 86.625f)
        )
    }

    /**
     * Junquillo: el acostado va a tope (48.1) y el de pie es el alto del paño menos los dos
     * junquillos. Con junquillo en 0, el alto del paño tal cual.
     */
    @Test
    fun junquillosPorPano() {
        assertEquals(
            "86.6 = 4\n48.1 = 4",
            CalculosPuerta.textoJunkillosJeny(paflon, 86.625f, nDiv = 2, jun = 0f, mocheta = -1f, marcoSup = 65.6f)
        )
        assertEquals(
            "84.2 = 4\n48.1 = 4",
            CalculosPuerta.textoJunkillosJeny(paflon, 86.625f, nDiv = 2, jun = 1.2f, mocheta = -1f, marcoSup = 65.6f)
        )
    }

    @Test
    fun junquillosConMocheta() {
        assertEquals(
            "86.6 = 4\n48.1 = 4\n65.6 = 2\n36.3 = 2",
            CalculosPuerta.textoJunkillosJeny(paflon, 86.625f, nDiv = 2, jun = 0f, mocheta = 36.3f, marcoSup = 65.6f)
        )
    }

    /**
     * Cotas horizontales del plano: dónde arranca cada pieza de pie y el ancho interior. Con 3
     * columnas, la primera a 14.37 y la segunda a 14.37 + 2.5 + 14.37 = 31.23.
     */
    @Test
    fun cotasDelPlano() {
        val (cuadricula, sinSegunda) = CalculosPuerta.cotasRejillaJeny("Jeny", paflon, cols = 3)
        assertEquals(3, cuadricula.size)
        assertEquals(14.3667f, cuadricula[0], 0.001f)
        assertEquals(31.2333f, cuadricula[1], 0.001f)
        assertEquals(48.1f, cuadricula[2], 0.001f)
        assertEquals(emptyList<Float>(), sinSegunda)

        // En "Jeny r" va una a cada lado: desde el parante izquierdo, dónde arranca el recuadro
        // —(48.1 - 30.06) / 2 = 9.02—; desde el derecho, el tubo de pie: (48.1 - 2.5) / 2 = 22.8.
        val (izquierda, derecha) = CalculosPuerta.cotasRejillaJeny("Jeny r", paflon, cols = 3)
        assertEquals(1, izquierda.size)
        assertEquals(9.01875f, izquierda[0], 0.001f)
        assertEquals(1, derecha.size)
        assertEquals(22.8f, derecha[0], 0.001f)
    }

    /**
     * Alturas de las acostadas, desde la base. Un paño de 181.5 sobre un zócalo de 8.25: con 4 filas
     * son 3 piezas repartidas en 4 tramos de (181.5 - 3·2.5) / 4 = 43.5, o sea a 43.5, 89.5 y 135.5
     * del arranque del paño.
     */
    @Test
    fun cotasDeLasAcostadas() {
        val (izq, der) = CalculosPuerta.cotasAcostadasJeny(
            "Jeny", parante = 198f, nZocalo = 1, nDiv = 1, filas = 4, bastidor = CalculosPuerta.BASTIDOR
        )
        assertEquals(listOf(51.75f, 97.75f, 143.75f), izq.map { kotlin.math.round(it * 100f) / 100f })
        // En la variante base todas las columnas van a la misma altura: no hace falta segunda regla.
        assertEquals(emptyList<Float>(), der)
    }

    /** En "Jeny c" las pares llevan una menos y caen entre las otras, así que van en la otra regla. */
    @Test
    fun lasParesVanEnLaReglaDerecha() {
        val (izq, der) = CalculosPuerta.cotasAcostadasJeny(
            "Jeny c", parante = 198f, nZocalo = 1, nDiv = 1, filas = 4, bastidor = CalculosPuerta.BASTIDOR
        )
        assertEquals(3, izq.size)
        assertEquals(2, der.size)
        // Intercaladas: 51.75 < 67.08 < 97.75 < 128.42 < 143.75
        assertEquals(67.08f, der[0], 0.01f)
        assertEquals(128.42f, der[1], 0.01f)
    }

    /**
     * Dónde se amarra cada tubo, medido desde la esquina inferior izquierda del recuadro hasta el
     * canto del tubo. Al lado se le restan los tubos que lleva y lo que queda se reparte en tramos
     * iguales. Con un paño de 86.625, el recuadro mide 30.06 x 60.64:
     *   ancho, un tubo:   (30.06 - 2.5) / 2 = 13.78
     *   alto, dos tubos:  (60.64 - 5) / 3 = 18.55  y  18.55 + 2.5 + 18.55 = 39.6
     */
    @Test
    fun amarresMedidosDesdeLaEsquinaDelRecuadro() {
        val amarres = CalculosPuerta.amarresJenyR(paflon, altoPano = 86.625f)
        assertEquals(13.781f, amarres.dePie, 0.01f)
        assertEquals(18.546f, amarres.costadoBajo, 0.01f)
        assertEquals(39.592f, amarres.costadoAlto, 0.01f)
    }

    @Test
    fun sinColumnasNiFilasNoHayRejilla() {
        assertEquals("", rejilla("Jeny", nDiv = 1, cols = 1, filas = 1))
    }
}
