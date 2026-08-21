package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Adel: un separador vertical parte el vacío en la columna de relleno (izquierda, 3/4) y la de
 * divisiones de vidrio (derecha, 1/4).
 *
 * Caso de taller: puerta 70 x 240 con el marco preestablecido de 2.2 y la altura de puente
 * preestablecida de 199.
 *   paflón  = (70 - 2·2.2) - 1 - 2·8.25 = 48.1
 *   parante = 199 - 1 = 198
 */
class AdelTest {

    private val paflon = 48.1f
    private val parante = 198f
    private val bastidor = CalculosPuerta.BASTIDOR

    private fun paranteInterno(nZocalo: Int) =
        CalculosPuerta.paranteInterno(parante, nZocalo, bastidor)

    private fun paflones(nZocalo: Int, nDiv: Int, variante: String = "Adel p1") =
        CalculosPuerta.textoPaflonAdel(variante, paflon, parante, paranteInterno(nZocalo), nZocalo, nDiv, bastidor)

    /**
     * El conteo del paflón de ancho completo es por ZÓCALOS, no por divisiones: con 3 zócalos son 4
     * piezas —el bastidor superior y los tres zócalos— y no 6. Los horizontales que generan las
     * divisiones son de otra medida (van solo en la columna derecha) y por eso van aparte, y el
     * relleno apilado de la izquierda es más corto todavía: 48.1 sin la columna de divisiones ni el
     * separador. En 165 de alto entran 20 piezas justas.
     */
    @Test
    fun conTresZocalosYCincoDivisiones() {
        assertEquals("198 = 2\n48.1 = 4\n165 = 1\n10 = 4\n29.9 = 20", paflones(nZocalo = 3, nDiv = 5))
    }

    @Test
    fun conTresZocalosYTresDivisiones() {
        assertEquals("198 = 2\n48.1 = 4\n165 = 1\n10 = 2\n29.9 = 20", paflones(nZocalo = 3, nDiv = 3))
    }

    @Test
    fun conUnZocaloSonDosHorizontalesDeAnchoCompleto() {
        // El vacío crece a 181.5 y entran 22 apilados.
        assertEquals("198 = 2\n48.1 = 2\n181.5 = 1\n10 = 2\n29.9 = 22", paflones(nZocalo = 1, nDiv = 3))
    }

    @Test
    fun sinDivisionesNoHayBarrasEnLaColumnaDerecha() {
        assertEquals("198 = 2\n48.1 = 2\n181.5 = 1\n29.9 = 22", paflones(nZocalo = 1, nDiv = 1))
    }

    /**
     * p3 lleva el mismo relleno pero de pie: las piezas miden el alto del vacío y entran las que
     * quepan en el ancho de la columna — 29.89 / 8.25 = 3 enteras. Salen con el mismo largo que el
     * separador, así que en pantalla se suman en un solo renglón (165 = 4).
     */
    @Test
    fun p3RellenaLaColumnaConPaflonesDePie() {
        assertEquals("198 = 2\n48.1 = 4\n165 = 1\n10 = 4\n165 = 3", paflones(nZocalo = 3, nDiv = 5, variante = "Adel p3"))
    }

    /** En "v" y "p2" la columna izquierda es un vidrio entero: no aporta piezas. */
    @Test
    fun lasVariantesDeVidrioNoLlevanRelleno() {
        val esperado = "198 = 2\n48.1 = 4\n165 = 1\n10 = 4"
        assertEquals(esperado, paflones(nZocalo = 3, nDiv = 5, variante = "Adel v"))
        assertEquals(esperado, paflones(nZocalo = 3, nDiv = 5, variante = "Adel p2"))
    }

    // ---------------- Junquillos ----------------

    private val junki = 1.2f

    private fun junquillos(nZocalo: Int, nDiv: Int, variante: String = "Adel p1", mocheta: Float = -1f) =
        CalculosPuerta.textoJunkillosAdel(variante, paflon, paranteInterno(nZocalo), nDiv, bastidor, junki, mocheta, marcoSup = 65.6f)

    /**
     * Cinco divisiones de 10 x 26.4: por cada vidrio, dos junquillos horizontales a tope y dos
     * verticales descontando los dos junquillos (26.4 - 2·1.2 = 24).
     */
    @Test
    fun junquillosDeLasDivisiones() {
        assertEquals(26.4f, CalculosPuerta.altoPano(paranteInterno(3), 5, bastidor), 0.001f)
        assertEquals("10 = 10\n24 = 10", junquillos(nZocalo = 3, nDiv = 5))
    }

    /** En p3 los vidrios son los mismos: el relleno de la izquierda no lleva junquillos. */
    @Test
    fun p3LlevaLosMismosJunquillosQueP1() {
        assertEquals("10 = 10\n24 = 10", junquillos(nZocalo = 3, nDiv = 5, variante = "Adel p3"))
    }

    /** En "v" y "p2" se suma el vidrio entero de la columna izquierda: 29.89 x 165. */
    @Test
    fun lasVariantesDeVidrioSumanElPanoGrande() {
        assertEquals(
            "10 = 10\n24 = 10\n29.9 = 2\n162.6 = 2",
            junquillos(nZocalo = 3, nDiv = 5, variante = "Adel v")
        )
    }

    @Test
    fun conMochetaSeAgreganSusDosPares() {
        assertEquals(
            "10 = 10\n24 = 10\n65.6 = 2\n27.6 = 2",
            junquillos(nZocalo = 3, nDiv = 5, mocheta = 30f)
        )
    }

    // ---------------- Vidrios ----------------

    private fun vidrios(nZocalo: Int, nDiv: Int, variante: String = "Adel p1", mocheta: Float = -1f) =
        CalculosPuerta.textoVidriosAdel(variante, paflon, paranteInterno(nZocalo), nDiv, bastidor, junki, mocheta, marcoSup = 65.6f)

    /** Cinco vidrios iguales: el vacío de 10 x 26.4 menos 0.4 de holgura en cada lado. */
    @Test
    fun vidriosDeLasDivisiones() {
        assertEquals("9.5 x 25.9 = 5", vidrios(nZocalo = 3, nDiv = 5))
        assertEquals("9.5 x 25.9 = 5", vidrios(nZocalo = 3, nDiv = 5, variante = "Adel p3"))
    }

    /** En "v" y "p2" se suma el paño grande de la izquierda: 29.89 x 165 menos la holgura. */
    @Test
    fun lasVariantesDeVidrioSumanElPanoGrandeAlListado() {
        assertEquals("9.5 x 25.9 = 5\n29.4 x 164.5 = 1", vidrios(nZocalo = 3, nDiv = 5, variante = "Adel v"))
        assertEquals("9.5 x 25.9 = 5\n29.4 x 164.5 = 1", vidrios(nZocalo = 3, nDiv = 5, variante = "Adel p2"))
    }

    @Test
    fun conMochetaSeAgregaSuVidrio() {
        assertEquals("9.5 x 25.9 = 5\n65.1 x 29.5 = 1", vidrios(nZocalo = 3, nDiv = 5, mocheta = 30f))
    }

    /** Las dos columnas reparten el vacío que deja el separador: un cuarto y tres cuartos. */
    @Test
    fun anchoDeLasDosColumnas() {
        assertEquals(9.9625f, CalculosPuerta.anchoDivisionesAdel(paflon, bastidor), 0.001f)
        assertEquals(29.8875f, CalculosPuerta.anchoRellenoAdel(paflon, bastidor), 0.001f)
        assertEquals(
            paflon - bastidor,
            CalculosPuerta.anchoDivisionesAdel(paflon, bastidor) + CalculosPuerta.anchoRellenoAdel(paflon, bastidor),
            0.001f
        )
    }
}
