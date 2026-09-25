package crystal.crystal.taller.nova.motor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Diseño compuesto (N-89): cada tramo con su modelo y su altura de hoja. La ventana es la de Dora
 * de "diseño simbólico nova.txt", editada: el primer tramo normal, el segundo en bandera con
 * mochetas de 20 y full corredizas.
 */
class CompuestoTest {

    private val u13 = PerfilU(1f, 1.5f)
    private val tubo = Puente("tubo 2⅜ x 1", 2.5f, 6f)

    private fun dora(acabado: String) =
        "{nova,$acabado,[370,140:" +
            "Tl<210>(s<100>(f<52.5>c<52.5>c<52.5>f<52.5>);m<40>(f<105>f<105>)) P<2.5> " +
            "Tl<157.5>(m<20>(f<157.5>);s<100>(c<52.5>c<52.5>c<52.5>);m<20>(f<157.5>))]}"

    private fun t(p: List<Pieza>) = MaterialesNova.texto(p)

    @Test
    fun cada_tramo_con_sus_mochetas_y_sus_puentes() {
        val v = DesdeDiseno.ventana(dora("apa"), u13, tubo)
        assertNotNull(v)
        val m = MotorNova.calcular(v!!)
        // Puente: 1 corrida en el tramo normal, 2 en el de bandera (N-35, N-49).
        assertEquals("210 = 1\n157.5 = 2", t(m.puente))
        // Mochetas como espacio: tramo 1, 140 − 100 − 2.5 = 37.5; tramo 2, dos de 20 − 2.5 = 17.5.
        // U vertical de mocheta (APA): espacio − 2 × 1.5, dos por mocheta.
        val verticales = t(m.u).lines().filter { it.startsWith("34.5 =") || it.startsWith("14.5 =") }
        assertEquals(listOf("34.5 = 2", "14.5 = 4"), verticales)
    }

    @Test
    fun inaparente_la_U_superior_e_inferior_van_de_lado_a_lado() {
        // Dictado 2026-09-25: la U superior e inferior albergan el vidrio que va delante de la
        // estructura; no las afectan los tramos. Hay mocheta arriba (los dos tramos) y abajo (el de
        // bandera): superior e inferior de 370.
        val v = DesdeDiseno.ventana(dora("ina"), u13, tubo)
        val m = MotorNova.calcular(v!!)
        assertEquals(listOf("370 = 2"), t(m.u).lines().filter { it.startsWith("370 =") })
        assertEquals(emptyList<String>(), t(m.u).lines().filter { it.startsWith("210 =") || it.startsWith("157.5 =") })
    }

    @Test
    fun dos_mochetas_seguidas_llevan_un_tubo_que_pierde_la_de_arriba() {
        // Sistema de 100 y dos mochetas de 30 encima, en 160: cada división es un tubo (2 corridas);
        // la de abajo pierde el puente que la separa del sistema y la de arriba el tubo: 27.5 cada una.
        val p = "{nova,apa,[200,160:Tl<200>(s<100>(f<100>c<100>);m<30>(f<200>);m<30>(f<200>))]}"
        val m = MotorNova.calcular(DesdeDiseno.ventana(p, u13, tubo)!!)
        assertEquals("200 = 2", t(m.puente))
        // U vertical de mocheta (APA): 27.5 − 3 = 24.5, dos por mocheta.
        assertEquals(listOf("24.5 = 4"), t(m.u).lines().filter { it.startsWith("24.5 =") })
    }

    @Test
    fun la_mocheta_con_corrediza_es_otra_franja_de_sistema() {
        // N-88 y dictado 2026-09-25: la mocheta de 60 con una corrediza pasa a ser sistema; entre
        // las dos va un puente normal, que pierde la de arriba: 60 − 2.5 = 57.5 de hoja.
        val p = "{nova,apa,[200,160:Tl<200>(s<100>(f<100>c<100>);m<60>(c<100>f<100>))]}"
        val m = MotorNova.calcular(DesdeDiseno.ventana(p, u13, tubo)!!)
        assertEquals("200 = 1", t(m.puente))
        assertEquals("199.9 = 2", t(m.riel))                       // un riel por sistema
        assertEquals("98.4 = 3\n55.9 = 3", t(m.portafelpa))       // 100 − 1.6 y 57.5 − 1.6
    }
}
