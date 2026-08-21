package crystal.crystal.taller.puerta.logica

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Conteo de holguras del vidrio en "Mari d", verificado en taller sobre una puerta de 70 x 240.
 *
 * Los vidrios de este modelo son triángulos y paralelogramos. La app devuelve el rectángulo que los
 * envuelve, calculado como si se apilaran uno sobre otro. Los vidrios de las PUNTAS son triángulos
 * que encajan en el vacío que deja el de la punta opuesta: no agregan altura al apilado y por lo
 * tanto tampoco agregan su holgura de 0.4.
 *
 * El descuento NO es un "-2" fijo: cuantas más divisiones, más diagonales quedan enteras contra las
 * tapas y más vidrios encajan. Con 7 divisiones encajan 2; con 11, encajan 4. Por eso el conteo sale
 * de la geometría —las barras que no suman altura— y no de una constante.
 *
 * Todos los casos: paflón 48.1, parante interno 181.5 (zócalo 1), bastidor 8.25, ángulo 45°.
 */
class VidriosMariDHolgurasTest {

    private val paflon = 48.1f
    private val paranteInterno = 181.5f
    private val bastidor = CalculosPuerta.BASTIDOR

    private fun alto(nDiv: Int, angulo: Float = 45f, parante: Float = paranteInterno): String =
        CalculosPuerta.textoVidriosMariD(paflon, parante, nDiv, bastidor, angulo)

    @Test
    fun sieteDivisiones_encajanDosVidrios() {
        // El caso que se detectó en taller: daba 132 porque descontaba 0.4 x 7 en vez de 0.4 x 5.
        assertEquals("47.7 x 132.8 = 1", alto(7))
    }

    @Test
    fun onceDivisiones_encajanCuatroVidrios() {
        // Con más divisiones encajan más puntas: aquí son 4, no 2.
        assertEquals("47.7 x 108.7 = 1", alto(11))
    }

    @Test
    fun seisDivisiones_encajanDosVidrios() {
        assertEquals("47.7 x 144.9 = 1", alto(6))
    }

    @Test
    fun ochoDivisiones_encajanDosVidrios() {
        assertEquals("47.7 x 120.8 = 1", alto(8))
    }

    /**
     * Ángulo tendido (32°). Aquí vivía un segundo defecto, independiente del conteo de holguras:
     * dentro del descuento GEOMÉTRICO de cada diagonal había una corrección con forma de holgura
     * —`holgura * (1 - tan ángulo)`— que solo actuaba por debajo de 45°. Achicaba el descuento y
     * agrandaba el vidrio 0.3 cm.
     *
     * A 45° ese término valía cero, así que ningún caso de ese ángulo lo delataba. Se detectó
     * comparando la suma cruda de los espacios: la app daba 138.966 y la medida real —a mano y en
     * CorelDRAW— es 138.666. Quitado el término, el alto final queda en 137.1.
     */
    @Test
    fun anguloTendido_noLlevaCorreccionDeHolguraEnLaGeometria() {
        assertEquals("47.7 x 137.1 = 1", alto(nDiv = 6, angulo = 32f))
    }

    @Test
    fun conZocaloTresYCuatroDivisiones_encajanDosVidrios() {
        // Aquí las barras SÍ suman altura, pero los vidrios de las puntas miden 44.5 de ancho contra
        // los 48.1 de los del medio: son más cortos que la diagonal, así que al apilarlos se pierden
        // en el vacío y no suman. Se descuentan 2 holguras, no 4.
        //
        // Este caso es el que descarta contar "barras que no suman altura": ahí ninguna da cero.
        // Lo que decide es el ANCHO de cada vidrio.
        assertEquals("47.7 x 136.4 = 1", alto(nDiv = 4, parante = 165.0f))
    }
}
