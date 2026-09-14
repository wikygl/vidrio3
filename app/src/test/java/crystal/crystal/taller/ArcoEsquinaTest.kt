package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La curva de la ventana en esquina: desarrollo, cuerda y flecha.
 *
 * Con dos cualesquiera queda definida y la tercera sale sola, así que se comprueba que las tres
 * puertas de entrada llevan al mismo arco: lo que se escriba en una casilla tiene que dar lo mismo
 * que si se hubiera escrito en otra.
 */
class ArcoEsquinaTest {

    /** Un cuarto de círculo de radio 100: la esquina redondeada de toda la vida. */
    @Test
    fun un_cuarto_de_circulo_de_radio_100() {
        // Cuerda = 100·√2 = 141.42 ; flecha = 100 − 100/√2 = 29.29 ; desarrollo = 157.08
        val a = ArcoEsquina.deCuerdaYFlecha(141.42f, 29.29f)
        assertNotNull(a)
        assertEquals(100f, a!!.radio, 0.5f)
        assertEquals(90f, a.anguloGrados, 0.5f)
        assertEquals(157.1f, a.desarrollo, 0.5f)
    }

    @Test
    fun las_tres_puertas_dan_el_mismo_arco() {
        val porCuerda = ArcoEsquina.deCuerdaYFlecha(141.42f, 29.29f)!!
        val porDesarrollo = ArcoEsquina.deDesarrolloYFlecha(porCuerda.desarrollo, porCuerda.flecha)!!
        val porRecta = ArcoEsquina.deDesarrolloYCuerda(porCuerda.desarrollo, porCuerda.cuerda)!!

        assertEquals("el desarrollo no cuadra", porCuerda.desarrollo, porDesarrollo.desarrollo, 0.3f)
        assertEquals("la cuerda no cuadra", porCuerda.cuerda, porDesarrollo.cuerda, 0.3f)
        assertEquals("el ángulo no cuadra", porCuerda.anguloGrados, porDesarrollo.anguloGrados, 0.5f)

        assertEquals("la flecha no cuadra", porCuerda.flecha, porRecta.flecha, 0.3f)
        assertEquals("el radio no cuadra", porCuerda.radio, porRecta.radio, 0.5f)
    }

    @Test
    fun una_curva_suave_de_las_de_verdad() {
        // Una panza de 12 en una pared de 180 de cuerda: lo que se ve en una esquina redondeada.
        // A mano: R = (180²/4 + 12²) / (2·12) = 8244/24 = 343.5 ; media apertura = asin(90/343.5)
        // = 15.19°, así que dobla 30.4° y el desarrollo es 343.5 · 0.5303 = 182.1.
        val a = ArcoEsquina.deCuerdaYFlecha(180f, 12f)!!
        assertEquals(343.5f, a.radio, 1f)
        // El desarrollo es poco más que la cuerda: la curva casi no estira.
        assertEquals(182.1f, a.desarrollo, 0.5f)
        assertEquals(30.4f, a.anguloGrados, 0.5f)
    }

    @Test
    fun media_vuelta_larga() {
        // Con la flecha por encima del radio el arco se pasa de media circunferencia: un medio
        // punto de cuerda 100 y flecha 50 es justo media vuelta.
        val a = ArcoEsquina.deCuerdaYFlecha(100f, 50f)!!
        assertEquals(50f, a.radio, 0.5f)
        assertEquals(180f, a.anguloGrados, 1f)
        assertEquals(157.1f, a.desarrollo, 1f)
    }

    @Test
    fun lo_que_no_es_un_arco_no_se_inventa() {
        // Sin curva no hay arco.
        assertNull(ArcoEsquina.deCuerdaYFlecha(180f, 0f))
        // La cuerda nunca es más larga que el desarrollo, ni la flecha tampoco.
        assertNull(ArcoEsquina.deDesarrolloYCuerda(150f, 160f))
        assertNull(ArcoEsquina.deDesarrolloYFlecha(150f, 150f))
        // Y una medida en negativo no es una medida.
        assertNull(ArcoEsquina.deCuerdaYFlecha(-10f, 5f))
    }

    /**
     * El trozo de una curva que ya se sabe cómo va: del radio sale la panza que le toca.
     *
     * Es lo que hace falta para partir un arco en pedazos, o para estrenar un pedazo detrás de
     * otro sin que la curva se corte ahí.
     */
    @Test
    fun del_radio_sale_el_trozo_que_le_toca() {
        val entero = ArcoEsquina.deCuerdaYFlecha(200f, 30f)!!
        val trozo = ArcoEsquina.deDesarrolloYRadio(entero.desarrollo / 2f, entero.radio)!!

        assertEquals("el trozo no sigue el mismo radio", entero.radio, trozo.radio, 0.01f)
        assertEquals("no dobla la mitad que el entero", entero.anguloGrados / 2f, trozo.anguloGrados, 0.01f)
        // Media curva panza MENOS de la mitad: la flecha no se reparte a partes iguales.
        assertTrue("la panza del trozo salió igual o mayor que la entera", trozo.flecha < entero.flecha / 2f)

        // Y con su desarrollo y esa panza sale el mismo arco: las tres medidas cuadran.
        val rehecho = ArcoEsquina.deDesarrolloYFlecha(trozo.desarrollo, trozo.flecha)!!
        assertEquals(trozo.cuerda, rehecho.cuerda, 0.2f)
        assertEquals(trozo.radio, rehecho.radio, 0.5f)
    }

    /** Sin radio, o con uno que daría más de una vuelta, no hay trozo que sacar. */
    @Test
    fun un_radio_que_no_vale_no_da_trozo() {
        assertNull(ArcoEsquina.deDesarrolloYRadio(100f, 0f))
        assertNull(ArcoEsquina.deDesarrolloYRadio(0f, 100f))
        assertNull(ArcoEsquina.deDesarrolloYRadio(1000f, 10f))
    }
}
