package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.hypot

/**
 * El arco de la planta, puesto por su cuerda y su flecha. La `y` va hacia abajo, como en el
 * lienzo. El caso es el de siempre: cuerda 160, flecha 40 (radio 100, 106.3°, desarrollo 185.5).
 */
class ArcoDibujadoTest {

    private val arco = ArcoDibujado(0f, 0f, 160f, 0f, 40f)

    @Test
    fun la_panza_positiva_cae_arriba_del_papel_yendo_a_la_derecha() {
        val (x, y) = arco.apice()
        assertEquals(80f, x, 0.01f)
        assertEquals(-40f, y, 0.01f)
        val (_, yNeg) = arco.copy(flecha = -40f).apice()
        assertEquals(40f, yNeg, 0.01f)
    }

    @Test
    fun los_puntos_van_de_a_a_b_pasando_por_el_apice_y_todos_a_un_radio() {
        val p = arco.puntos(24)
        assertEquals(0f to 0f, p.first())
        assertEquals(160f to 0f, p.last())
        val centroY = 100f - 40f   // el centro está del lado contrario a la panza: y = +60
        p.forEach { (x, y) -> assertEquals("fuera del radio", 100f, hypot(x - 80f, y - centroY), 0.5f) }
        val medio = p[12]
        assertEquals(80f, medio.first, 0.5f)
        assertEquals(-40f, medio.second, 0.5f)
    }

    @Test
    fun el_giro_lleva_el_signo_de_la_flecha_y_los_rumbos_lo_reparten() {
        assertEquals(106.26f, arco.giroGrados, 0.05f)
        assertEquals(-106.26f, arco.copy(flecha = -40f).giroGrados, 0.05f)
        // Entra subiendo (rumbo negativo) y sale bajando, simétrico a la cuerda.
        assertEquals(Math.toRadians(-53.13), arco.rumboEntrada, 1e-3)
        assertEquals(Math.toRadians(53.13), arco.rumboSalida, 1e-3)
    }

    @Test
    fun recorrido_al_reves_la_panza_sigue_en_el_mismo_sitio() {
        val alReves = arco.invertido()
        val (x, y) = alReves.apice()
        assertEquals(80f, x, 0.01f)
        assertEquals(-40f, y, 0.01f)
        assertEquals(-arco.giroGrados, alReves.giroGrados, 0.01f)
    }

    @Test
    fun la_distancia_al_arco_es_cero_en_el_apice_y_grande_lejos() {
        assertTrue(arco.distanciaA(80f, -40f) < 0.5f)
        assertEquals(40f, arco.distanciaA(80f, 0f), 0.5f)
    }
}
