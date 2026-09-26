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
    fun inaparente_la_U_de_cada_borde_sigue_al_vidrio() {
        // Dictado el 2026-09-25, con el tramo 2 en `ccc`: arriba todo es vidrio seguido (los dos
        // tramos con mocheta arriba) → 370 = 1. Abajo, el fijo izquierdo solo (las corredizas lo
        // cortan, van sobre el riel) → 53.2; el fijo derecho, el parante y la mocheta inferior de la
        // bandera van seguidos → 53.2 + 2.5 + 157.5 = 213.2.
        val p = "{nova,ina,[370,140:Tl<210>(s<100>(f<52.5>c<52.5>c<52.5>f<52.5>);m<37.5>(f<105>f<105>)) P<2.5> " +
            "Tl<157.5>(m<17.5>(f<157.5>);s<100>(c<52.5>c<52.5>c<52.5>);m<17.5>(f<157.5>))]}"
        val m = MotorNova.calcular(DesdeDiseno.ventana(p, u13, tubo)!!)
        assertEquals(listOf("53.2 = 1", "213.2 = 1", "370 = 1"),
            t(m.u).lines().filter { it.startsWith("53.2") || it.startsWith("213.2") || it.startsWith("370") })
    }

    @Test
    fun el_tramo_de_solo_corredizas_cruza_igual_que_el_clasico() {
        // Hallado por el usuario el 2026-09-25: con el tramo 2 en `ccc` la U de fijos salía 52.7 y
        // en `fcf` 52.9. El cruce es del tramo: `ccc` cruza como `fcf` (2 cruces), en toda la
        // ventana 4 → (370 − 2.5 + 4 × 0.7) / 7 = 52.9.
        fun ventana(tramo2: String) = MotorNova.calcular(DesdeDiseno.ventana(
            "{nova,apa,[370,140:Tl<210>(s<100>(f<52.5>c<52.5>c<52.5>f<52.5>);m<37.5>(f<105>f<105>)) P<2.5> " +
                "Tl<157.5>(m<17.5>(f<157.5>);s<100>($tramo2);m<17.5>(f<157.5>))]}", u13, tubo)!!)
        val ccc = ventana("c<52.5>c<52.5>c<52.5>")
        val fcf = ventana("f<52.5>c<52.5>f<52.5>")
        assertEquals("52.9 = 5", t(ccc.hache))
        // La misma medida de U de fijos (la cantidad cambia: `fcf` tiene dos fijos más).
        assertEquals("52.9 = 2", t(ccc.u).lines().first())
        assertEquals("52.9 = 4", t(fcf.u).lines().first())
        // Es full corredizas (N-51, N-52b): 1 riel por corrediza y doble corrediza, 1 por cada 2,
        // aparte del fijo corredizo del tramo clásico.
        assertEquals("209.9 = 1\n157.4 = 3", t(ccc.riel))
        assertEquals("209.9 = 1", t(ccc.rielSuperior))
        assertEquals(RielSuperior.FIJO_CORREDIZO, ccc.nombreRielSuperior)
        assertEquals("157.4 = 2", t(ccc.dobleCorrediza))
        // Con el tramo clásico no cambia nada: 1 riel y fijo corredizo por tramo.
        assertEquals("209.9 = 1\n157.4 = 1", t(fcf.riel))
        assertEquals("209.9 = 1\n157.4 = 1", t(fcf.rielSuperior))
        assertEquals("", t(fcf.dobleCorrediza))
        // Y su vidrio es de full corredizas (N-59, sin el −1.4); los del tramo clásico lo llevan.
        // Mocheta con tubo: mocheta − (u + 0.3) (N-63): 37.5 − 1.8 = 35.7 y 17.5 − 1.8 = 15.7.
        assertEquals("52.4 x 98.3 = 2\n51.5 x 96.5 = 2\n52.9 x 96.5 = 3\n104.5 x 35.7 = 2\n157 x 15.7 = 2",
            MaterialesNova.textoVidrios(ccc.vidrios))
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
