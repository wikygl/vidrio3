package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Taly reparte parantes interiores de a pares desde los costados hasta que el vidrio que queda entra
 * en rango: entre 9 y 18 de ancho. Por debajo de 9 sale una tira inútil y por encima de 18 el paño
 * queda demasiado abierto.
 *
 * Con parantes de paflón el vacío baja de a 16.5, un salto grueso que a veces cae fuera de la
 * ventana. Cuando pasa, los parantes se rehacen con tubo de 3.8: al bajar de a 7.6 el reparto
 * siempre aterriza dentro, a costa de llevar más piezas.
 */
class ZonaVidrioTalyTest {

    private val bastidor = CalculosPuerta.BASTIDOR

    private fun zona(paflon: Float) = CalculosPuerta.zonaVidrioTaly(paflon, bastidor)

    /** Puerta 70: paflón 48.1 → 31.6 → 15.1, que está en rango, así que van todos de paflón. */
    @Test
    fun conPaflonCuandoElVidrioEntraEnRango() {
        val z = zona(48.1f)
        assertEquals(15.1f, z.anchoVidrio, 0.01f)
        assertEquals(2, z.paresPaflon)
        assertEquals(0, z.paresTubo)
    }

    /**
     * Puerta 90: paflón 68.1 → 51.6 → 35.1 → 18.6, que pasa de 18. Se agrega UN par de tubos pegado
     * al vidrio y baja a 11; los tres pares de paflón se quedan como están.
     */
    @Test
    fun agregaUnParDeTuboCuandoSePasaDe18() {
        val z = zona(68.1f)
        assertEquals(11f, z.anchoVidrio, 0.01f)
        assertEquals(3, z.paresPaflon)
        assertEquals(1, z.paresTubo)
    }

    /**
     * Cuando el paflón deja el vidrio por debajo de 9, el último par se cambia por el de tubo: el
     * vacío sube 8.9. Con paflón 41: 41 → 24.5 → 8, fuera de rango; con el cambio queda 16.9.
     */
    @Test
    fun cambiaElUltimoParCuandoNoLlegaA9() {
        val z = zona(41f)
        assertEquals(16.9f, z.anchoVidrio, 0.01f)
        assertEquals(1, z.paresPaflon)
        assertEquals(1, z.paresTubo)
    }

    /** Con cualquier ancho de puerta, el reparto tiene que terminar dentro de la ventana. */
    @Test
    fun elVidrioSiempreTerminaEnRango() {
        var ancho = 60f
        while (ancho <= 200f) {
            val paflon = CalculosPuerta.paflon(ancho, CalculosPuerta.MARCO, bastidor)
            if (paflon > 9f) {
                val z = zona(paflon)
                assert(z.anchoVidrio in 9f..18.001f) {
                    "puerta $ancho: paflón $paflon dejó vidrio de ${z.anchoVidrio} " +
                        "con ${z.paresPaflon} pares de paflón y ${z.paresTubo} de tubo"
                }
            }
            ancho += 1f
        }
    }

    /** Los parantes contados tienen que consumir exactamente lo que le falta al vidrio. */
    @Test
    fun losParantesConsumenLoQueNoEsVidrio() {
        listOf(48.1f, 68.1f, 41f, 88.1f).forEach { paflon ->
            val z = zona(paflon)
            val consumido = z.paresPaflon * 2f * bastidor + z.paresTubo * 2f * z.tubo
            assertEquals("paflón $paflon", paflon, z.anchoVidrio + consumido, 0.01f)
        }
    }
}
