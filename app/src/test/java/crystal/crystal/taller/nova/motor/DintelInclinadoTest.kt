package crystal.crystal.taller.nova.motor

import crystal.crystal.Diseno.nova.ContornoEnTramos
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * El dintel inclinado (dictado el 2026-10-01, Fide: 214.5 de ancho, 180 a la izquierda y 148.8 a la
 * derecha; el dintel mide 216.8). La mocheta lleva arriba el dintel y cada costado su propia medida;
 * antes salía como la de un rectángulo: 214.5 arriba y los dos costados de 47.4.
 */
class DintelInclinadoTest {

    private val fide = listOf(0f to 0f, 214.5f to 31.2f, 214.5f to 180f, 0f to 180f)
    private val u13 = PerfilU(1f, 1.5f)
    private val multiple = Puente("Multiple", 2.5f, 2.5f)

    private fun materiales(puente: Puente = multiple, acabado: String = "apa"): MaterialesNova {
        val paquete = ContornoEnTramos.disenoDesdeContorno(fide, acabado, 0f)!!.aPaquete()
        return MotorNova.calcular(DesdeDiseno.ventana(paquete, u13, puente, 0.7f)!!)
    }

    /**
     * Mocheta: 51.4 en su lado alto, menos el Múltiple (2.5) = 48.9.
     * - Arriba, el dintel: √(214.5² + 31.2²) = 216.76 (con Múltiple la de abajo la trae el puente).
     * - Cada costado por su canto más largo bajo la U inclinada, menos una U: izquierdo 47.4;
     *   derecho, donde el dintel sube 0.22 en el ancho de la U, 16.4.
     * El sistema (U de fijos 53.9, U parante 126.8) no cambia: queda entero bajo el punto más bajo.
     */
    @Test
    fun la_mocheta_lleva_el_dintel_y_cada_costado_su_medida() {
        assertEquals("53.9 = 2\n216.7 = 1\n126.8 = 2\n47.4 = 1\n16.4 = 1", MaterialesNova.texto(materiales().u))
    }

    /**
     * El tee mide lo que baja el dintel donde queda (dictado el 2026-10-01): con dos paños, en el
     * medio, el lado alto menos la mitad de lo que baja: 48.9 − 1.5 − 31.2 / 2 = 31.8.
     */
    @Test
    fun el_tee_mide_lo_que_baja_el_dintel_donde_queda() {
        assertEquals("31.8 = 1", MaterialesNova.texto(materiales().tee))
    }

    /** Con un puente que no es Múltiple la mocheta lleva también su U de abajo, recta. */
    @Test
    fun sin_multiple_va_tambien_la_u_de_abajo_recta() {
        val tubo = Puente("tubo 2⅜ x 1", 2.5f, 6f)
        val u = MaterialesNova.texto(materiales(puente = tubo).u).lines()
        assertEquals(true, u.contains("214.5 = 1"))
        assertEquals(true, u.contains("216.7 = 1"))
    }

    /**
     * Los vidrios de mocheta: cada paño es el rectángulo que lo contiene (N-99), el mismo ancho y el
     * alto de su lado más alto. El de la derecha arranca donde el dintel ya bajó 15.6: 48.4 − 15.6.
     */
    @Test
    fun los_vidrios_de_mocheta_tienen_el_mismo_ancho_y_distinto_alto() {
        assertEquals(
            "53.4 x 126.8 = 2\n52.5 x 125 = 2\n106.7 x 48.4 = 1\n106.7 x 32.8 = 1",
            MaterialesNova.textoVidrios(materiales().vidrios)
        )
    }

    /**
     * INA (2026-10-01): lo que llega al dintel mide lo suyo en su lugar.
     * - U parante, por su canto más largo bajo la U inclinada: 180 − 3 = 177 a la izquierda (el
     *   canto de la pared); a la derecha el dintel sube 0.22 en el ancho de la U: 180 − 3 − 30.98 = 146.0.
     * - U de arriba: por el dintel, 216.7. La de abajo, la de cada fijo (53.9).
     * - Fijos: el de la izquierda 180 − 1.7 = 178.3; el de la derecha empieza en 160.9, donde el dintel
     *   bajó 23.4: 180 − 23.4 − 1.7 = 154.9.
     * - Mocheta: sobre las corredizas (de 53.6 a 160.9), el lado alto baja 7.8: 52.4 − 7.8 = 44.6.
     */
    @Test
    fun en_ina_cada_pieza_que_llega_al_dintel_mide_lo_suyo() {
        val m = materiales(acabado = "ina")
        assertEquals("53.9 = 2\n216.7 = 1\n177 = 1\n146 = 1", MaterialesNova.texto(m.u))
        assertEquals("53.4 x 178.3 = 1\n53.4 x 154.9 = 1\n52.5 x 125 = 2\n105.9 x 44.6 = 1", MaterialesNova.textoVidrios(m.vidrios))
    }

    // ==================== EL ALFÉIZAR INCLINADO (el vano de Fide al revés) ====================

    /** 261.6 de ancho, 172 a la izquierda y 115.2 a la derecha: el dintel recto, el alféizar sube. */
    private val alReves = listOf(0f to 0f, 261.6f to 0f, 261.6f to 115.2f, 0f to 172f)

    private fun materialesAlReves(acabado: String = "apa"): MaterialesNova {
        val paquete = ContornoEnTramos.disenoDesdeContorno(alReves, acabado, 0f)!!.aPaquete()
        return MotorNova.calcular(DesdeDiseno.ventana(paquete, u13, multiple, 0.7f)!!)
    }

    /**
     * El sistema es un rectángulo (N-66): cuelga del dintel recto y la mocheta va abajo, invertido
     * (dictado el 2026-10-01). Antes iba abajo, sobre el alféizar inclinado. El sistema mide lo que
     * mide el lado bajo (115.2) y el invertido no admite Múltiple: lleva tubo (N-03).
     */
    @Test
    fun con_el_alfeizar_inclinado_el_sistema_cuelga_del_dintel() {
        val paquete = ContornoEnTramos.disenoDesdeContorno(alReves, "apa", 0f)!!.aPaquete()
        val m = paquete.indexOf("m<")
        val s = paquete.indexOf("s<")
        assertEquals("la mocheta no quedó abajo: $paquete", true, m in 0 until s)
        assertEquals(true, paquete.contains("s<115.2>"))
        val v = DesdeDiseno.ventana(paquete, u13, multiple, 0.7f)!!
        assertEquals(ModeloMocheta.INVERTIDO, v.modelo)
        assertEquals("tubo 2⅜ x 1", v.lados[0].tramos[0].puente?.nombre)
    }

    /**
     * La mocheta de abajo, como la de arriba bajo el dintel: abajo la U inclinada (267.6), arriba la
     * recta sobre el puente (con tubo va); el costado izquierdo 51.3 y el derecho, en punta, no lleva;
     * los dos paños con el mismo ancho y distinto alto; el tee en el medio, 22.9.
     */
    @Test
    fun la_mocheta_de_abajo_sigue_al_alfeizar() {
        val m = materialesAlReves()
        // El puente y la U recta de la mocheta, por su canto largo: el ancho entero, 261.6 (medidos
        // 0.05 adentro de la pendiente daban 261.3; corregido por el usuario el 2026-10-01).
        assertEquals("261.6 = 1", MaterialesNova.texto(m.puente))
        assertEquals("52.8 = 3\n261.6 = 1\n267.6 = 1\n113.5 = 2\n51.3 = 1", MaterialesNova.texto(m.u))
        // Cada paño, la mitad de 261.6 − 0.5; mismo ancho, distinto alto.
        assertEquals(
            "52.3 x 113.5 = 2\n52.8 x 113.5 = 1\n51.4 x 111.7 = 2\n130.3 x 52.5 = 1\n130.3 x 24.1 = 1",
            MaterialesNova.textoVidrios(m.vidrios)
        )
        assertEquals("22.9 = 1", MaterialesNova.texto(m.tee))
    }

    /**
     * INA: la U de abajo por el alféizar (267.6); la U parante de cada costado por su canto largo (169
     * y 112.5); cada fijo con el alto de su lado más alto (170.3, 147.5, 124.8) y la mocheta de abajo,
     * el rectángulo que la contiene (44.9 y 22.2).
     */
    @Test
    fun en_ina_lo_que_llega_al_alfeizar_mide_lo_suyo() {
        val m = materialesAlReves("ina")
        assertEquals("267.6 = 1\n169 = 1\n112.5 = 1", MaterialesNova.texto(m.u))
        assertEquals(
            "52.3 x 170.3 = 1\n52.8 x 147.5 = 1\n52.3 x 124.8 = 1\n51.4 x 111.7 = 2\n50.8 x 44.9 = 1\n50.8 x 22.2 = 1",
            MaterialesNova.textoVidrios(m.vidrios)
        )
    }

    // ==================== LOS DOS BORDES INCLINADOS (2026-10-02) ====================

    /** 88.4 de ancho, 174.2 a la izquierda y 114.4 a la derecha: el dintel baja 32.7, el alféizar sube 27.1. */
    private val dosBordes = listOf(0f to 0f, 88.4f to 32.7f, 88.4f to 147.1f, 0f to 174.2f)

    private fun paqueteDosBordes(acabado: String) = ContornoEnTramos.disenoDesdeContorno(dosBordes, acabado, 0f)!!.aPaquete()

    /**
     * Sin borde recto donde apoyar el sistema (un rectángulo, N-66) va en bandera: mocheta abajo con lo
     * que sube el alféizar (27.1), el sistema (114.4, lo que mide el lado bajo) y arriba el resto (32.7).
     * La bandera no admite Múltiple: tubo (N-03).
     */
    @Test
    fun con_los_dos_bordes_inclinados_va_en_bandera() {
        val paquete = paqueteDosBordes("apa")
        assertEquals(true, paquete.contains("m<27.1>(f<88.4>);s<114.4>(f<44.2>c<44.2>);m<32.7>(f<88.4>)"))
        val v = DesdeDiseno.ventana(paquete, u13, multiple, 0.7f)!!
        assertEquals(ModeloMocheta.BANDERA, v.modelo)
        assertEquals("tubo 2⅜ x 1", v.lados[0].tramos[0].puente?.nombre)
    }

    /**
     * Cada mocheta con su U inclinada (94.2 arriba, 92.4 abajo) y la recta sobre su puente (88.4 = 2);
     * el costado alto de cada una con su medida (27.2 y 21.6) y el que llega en punta, sin U.
     * En INA no queda la astilla de 0.5 en la punta.
     */
    @Test
    fun las_dos_mochetas_siguen_su_borde() {
        val apa = MotorNova.calcular(DesdeDiseno.ventana(paqueteDosBordes("apa"), u13, multiple, 0.7f)!!)
        assertEquals("44.5 = 1\n88.4 = 2\n94.2 = 1\n92.4 = 1\n112.7 = 1\n27.2 = 1\n21.6 = 1", MaterialesNova.texto(apa.u))
        assertEquals("88.4 = 2", MaterialesNova.texto(apa.puente))
        assertEquals("44 x 112.7 = 1\n43.1 x 110.9 = 1\n87.9 x 28.4 = 1\n87.9 x 22.8 = 1", MaterialesNova.textoVidrios(apa.vidrios))
        val ina = MotorNova.calcular(DesdeDiseno.ventana(paqueteDosBordes("ina"), u13, multiple, 0.7f)!!)
        assertEquals("92.4 = 1\n94.2 = 1\n171.2 = 1", MaterialesNova.texto(ina.u))
    }
}
