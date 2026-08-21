package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Las divisiones de Mari se arman con el aluminio del INTERIOR, no con el del bastidor. Mientras los
 * dos sean el mismo perfil —paflón 8.25, que es lo de siempre— nada cambia; cuando el vidriero pone
 * tubo adentro, los paños crecen y junquillos, vidrios y cotas tienen que crecer con ellos.
 *
 * Puerta 70 x 240, zócalo 1: paflón 48.1, parante 198, parante interno 181.5, junquillo 1.2.
 */
class InteriorMariTest {

    private val paflon = 48.1f
    private val parante = 198f
    private val paranteInterno = 181.5f
    private val junki = 1.2f
    private val bastidor = CalculosPuerta.BASTIDOR
    private val tubo = 3.8f

    /** Con el interior igual al bastidor sale exactamente lo mismo que antes de separarlos. */
    @Test
    fun conInteriorIgualAlBastidorNadaCambia() {
        val conDefecto = CalculosPuerta.textoJunkillos(
            "Mari h", junki, mocheta = -1f, nPfvcal = 5, paflon = paflon, bastidor = bastidor,
            nDiv = 4, paranteInterno = paranteInterno, marcoSuperior = 65.6f
        )
        val explicito = CalculosPuerta.textoJunkillos(
            "Mari h", junki, mocheta = -1f, nPfvcal = 5, paflon = paflon, bastidor = bastidor,
            nDiv = 4, paranteInterno = paranteInterno, marcoSuperior = 65.6f, interior = bastidor
        )
        assertEquals(explicito, conDefecto)
        assertEquals(
            CalculosPuerta.cotasPanos(parante, 1, 4, bastidor),
            CalculosPuerta.cotasPanos(parante, 1, 4, bastidor, bastidor)
        )
    }

    /**
     * Mari h con tubo de 3.8 adentro: el vacío de 181.5 pierde 3 tubos en vez de 3 paflones, así que
     * cada paño pasa de 40.5 a 42.525. El junquillo mide ese alto menos los dos suyos y el vidrio,
     * ese alto menos la holgura de 0.5 que le toca por llevar junquillo.
     */
    @Test
    fun mariHConTuboAdentro() {
        assertEquals(42.525f, CalculosPuerta.altoPano(paranteInterno, 4, tubo), 0.001f)

        assertEquals(
            "40.1 = 8\n48.1 = 8",
            CalculosPuerta.textoJunkillos(
                "Mari h", junki, mocheta = -1f, nPfvcal = 5, paflon = paflon, bastidor = bastidor,
                nDiv = 4, paranteInterno = paranteInterno, marcoSuperior = 65.6f, interior = tubo
            )
        )
        assertEquals(
            "47.6 x 42 = 4",
            CalculosPuerta.textoVidrios(
                "Mari h", junki, paflon, bastidor, nDiv = 4, paranteInterno = paranteInterno,
                marcoSuperior = 65.6f, mocheta = -1f, interior = tubo
            )
        )
    }

    /**
     * Mari v: el ancho se reparte igual, pero descontando tubos. (48.1 - 3·3.8) / 4 = 9.175 contra
     * los 5.575 que dejaban los paflones.
     */
    @Test
    fun mariVConTuboAdentro() {
        assertEquals(
            "9.2 = 8\n179.1 = 8",
            CalculosPuerta.textoJunkillos(
                "Mari v", junki, mocheta = -1f, nPfvcal = 5, paflon = paflon, bastidor = bastidor,
                nDiv = 4, paranteInterno = paranteInterno, marcoSuperior = 65.6f, interior = tubo
            )
        )
        assertEquals(
            "8.7 x 181 = 4",
            CalculosPuerta.textoVidrios(
                "Mari v", junki, paflon, bastidor, nDiv = 4, paranteInterno = paranteInterno,
                marcoSuperior = 65.6f, mocheta = -1f, interior = tubo
            )
        )
    }

    /**
     * Las cotas del plano miden lo mismo que se corta: el zócalo sigue siendo de bastidor y de ahí
     * en adelante cada línea sube un paño más un tubo.
     */
    @Test
    fun cotasDelPlanoConTuboAdentro() {
        val cotas = CalculosPuerta.cotasPanos(parante, nZocalo = 1, nDiv = 4, bastidor = bastidor, interior = tubo)
        assertEquals(5, cotas.size)
        assertEquals(8.25f, cotas[0], 0.001f)
        assertEquals(50.775f, cotas[1], 0.001f)   // zócalo + un paño
        assertEquals(97.1f, cotas[2], 0.001f)     // + tubo + paño
        assertEquals(143.425f, cotas[3], 0.001f)
        assertEquals(189.75f, cotas[4], 0.001f)   // = parante - un paño
    }
}
