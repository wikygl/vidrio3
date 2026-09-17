package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Extender y recortar líneas contra los bordes de lo dibujado, sin pantalla.
 *
 * El caso de todas las pruebas es un rectángulo de 100 × 60 con la esquina en (0, 0): sus cuatro
 * lados son los bordes, y una línea suelta se alarga o se corta contra ellos.
 */
class EdicionLineasTest {

    private val rectangulo = listOf(
        (0f to 0f) to (100f to 0f),
        (100f to 0f) to (100f to 60f),
        (100f to 60f) to (0f to 60f),
        (0f to 60f) to (0f to 0f)
    )

    private fun cerca(esperado: Pair<Float, Float>, real: Pair<Float, Float>?) {
        assertNotNull("no salió punto", real)
        assertEquals(esperado.first, real!!.first, 0.01f)
        assertEquals(esperado.second, real.second, 0.01f)
    }

    @Test
    fun extender_llega_al_primer_borde_en_su_direccion() {
        // Una línea horizontal que va de (20, 30) a (50, 30): su punta derecha llega al lado
        // derecho, en x = 100, y no al de más allá.
        cerca(100f to 30f, EdicionLineas.extender(20f to 30f, 50f to 30f, rectangulo))
    }

    @Test
    fun extender_el_otro_extremo_es_llamar_al_reves() {
        cerca(0f to 30f, EdicionLineas.extender(50f to 30f, 20f to 30f, rectangulo))
    }

    @Test
    fun extender_sin_borde_delante_no_hace_nada() {
        // Apunta hacia arriba desde fuera del rectángulo: por ahí no hay nada.
        assertNull(EdicionLineas.extender(150f to 100f, 150f to 80f, rectangulo))
    }

    @Test
    fun extender_no_cuenta_el_borde_que_ya_toca() {
        // La punta está justo sobre el lado izquierdo: tiene que seguir hasta el derecho.
        cerca(100f to 30f, EdicionLineas.extender(-20f to 30f, 0f to 30f, rectangulo))
    }

    @Test
    fun recortar_en_medio_deja_dos_trozos() {
        // Una línea que atraviesa el rectángulo de lado a lado, de (-20, 30) a (120, 30). Se toca
        // en el medio: queda lo de fuera a cada lado.
        val quedan = EdicionLineas.recortar(-20f to 30f, 120f to 30f, 0.5f, rectangulo)
        assertNotNull(quedan)
        assertEquals(2, quedan!!.size)
        cerca(-20f to 30f, quedan[0].first)
        cerca(0f to 30f, quedan[0].second)
        cerca(100f to 30f, quedan[1].first)
        cerca(120f to 30f, quedan[1].second)
    }

    @Test
    fun recortar_en_una_punta_quita_solo_esa_punta() {
        // La misma línea tocada en el sobrante de la derecha (más allá de x = 100): se quita ese
        // trozo y el resto sigue entero.
        val toque = (110f + 20f) / 140f
        val quedan = EdicionLineas.recortar(-20f to 30f, 120f to 30f, toque, rectangulo)
        assertNotNull(quedan)
        assertEquals(1, quedan!!.size)
        cerca(-20f to 30f, quedan[0].first)
        cerca(100f to 30f, quedan[0].second)
    }

    @Test
    fun recortar_sin_cruces_no_hay_nada_que_quitar() {
        assertNull(EdicionLineas.recortar(200f to 30f, 250f to 30f, 0.5f, rectangulo))
    }

    @Test
    fun imantar_coge_el_mas_cercano_si_esta_a_tiro() {
        val esquinas = rectangulo.map { it.first }
        cerca(100f to 0f, EdicionLineas.imantar(96f to 3f, esquinas, 10f))
        assertNull(EdicionLineas.imantar(50f to 30f, esquinas, 10f))
    }
}
