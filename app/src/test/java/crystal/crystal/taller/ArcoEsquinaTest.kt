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

    /**
     * El quiebre en la unión con una pared recta, desde el ángulo entre la pared y la cuerda (dictado
     * por el usuario el 2026-10-19). Cuarto de círculo de radio 60: en tangente la cuerda se desvía
     * 45° (135° con la pared); con un quiebre de 30°, 75° (105°). La curva de su prueba (58.6 de
     * desarrollo, 58.2 de cuerda) gira 23.2°: en tangente la cuerda forma 168.4° con la pared.
     */
    @Test
    fun el_quiebre_sale_del_angulo_entre_la_pared_y_la_cuerda() {
        val cuarto = ArcoEsquina.deDesarrolloYCuerda((Math.PI * 30).toFloat(), (60 * Math.sqrt(2.0)).toFloat())!!
        assertEquals(45f, cuarto.desvioDeLaCuerdaEnTangente, 0.1f)
        assertEquals(0f, cuarto.quiebreConAnguloDeCuerda(135f), 0.1f)
        assertEquals(180f, cuarto.anguloEnLaUnion(135f), 0.1f)
        assertEquals(30f, cuarto.quiebreConAnguloDeCuerda(105f), 0.1f)
        assertEquals(150f, cuarto.anguloEnLaUnion(105f), 0.1f)

        val prueba = ArcoEsquina.deDesarrolloYCuerda(58.6f, 58.2f)!!
        assertEquals(11.6f, prueba.desvioDeLaCuerdaEnTangente, 0.05f)
        assertEquals(0f, prueba.quiebreConAnguloDeCuerda(168.4f), 0.05f)
        assertEquals(13.4f, prueba.quiebreConAnguloDeCuerda(155f), 0.05f)
    }

    /**
     * Dos curvas seguidas (2026-10-20): el ángulo se mide entre sus cuerdas. Dos cuartos de círculo en
     * tangente forman 90° entre las cuerdas; con 80°, un quiebre de 10° (unión de 170°).
     */
    @Test
    fun entre_dos_curvas_el_angulo_es_entre_sus_cuerdas() {
        val cuarto = ArcoEsquina.deDesarrolloYCuerda((Math.PI * 30).toFloat(), (60 * Math.sqrt(2.0)).toFloat())!!
        assertEquals(180f, ArcoEsquina.anguloEnLaUnionDeCuerdas(cuarto, cuarto, 90f), 0.1f)
        assertEquals(170f, ArcoEsquina.anguloEnLaUnionDeCuerdas(cuarto, cuarto, 80f), 0.1f)
        assertEquals(80f, ArcoEsquina.anguloDeCuerdasConUnion(cuarto, cuarto, 170f), 0.1f)
    }
}
