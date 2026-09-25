package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Las herramientas de novaDiseño con el vano real de Formas (medida 1): 338.3 × 144.5, con el
 * escalón a la izquierda (104.6 de ancho, dintel a 67.3) y el recorte abajo a la derecha (82 de
 * ancho, alféizar a 109).
 */
class HerramientasDisenoTest {

    private val vano = listOf(
        0f to 144.5f, 256.3f to 144.5f, 256.3f to 109.4f, 338.3f to 108.5f,
        338.3f to 0f, 104.6f to 0.1f, 104.6f to 67.3f, 0f to 67.3f
    )

    /** Lo que deja hoy el botón Limpiar: un rectángulo con la silueta encima. */
    private fun rectangular() = DisenoNova(
        "apa", 338.3f, 144.5f,
        listOf(NovaTramo(338.3f, listOf(NovaFranja(true, 0f, listOf(NovaModulo('f', 338.3f))))))
    ).conContornoVano(vano)

    private fun r1(x: Float) = Math.round(x * 10) / 10f

    @Test
    fun el_iman_pega_a_menos_de_3_cm_y_suelta_lo_demas() {
        val guias = HerramientasDiseno.guiasVerticales(rectangular())
        assertEquals(104.6f, HerramientasDiseno.pegar(102.5f, guias).valor, 0.01f)
        assertEquals(104.6f, HerramientasDiseno.pegar(102.5f, guias).guia!!, 0.01f)
        assertNull(HerramientasDiseno.pegar(99f, guias).guia)
        // Apagado, no pega nada.
        assertNull(HerramientasDiseno.pegar(104f, guias, activo = false).guia)
    }

    @Test
    fun limpiar_con_forma_deja_un_tramo_por_escalon() {
        val d = HerramientasDiseno.limpioConForma("apa", 338.3f, 144.5f, vano)
        assertEquals(3, d.tramos.size)
        // Cada tramo deja medio parante a cada lado del corte: los parantes quedan con el eje justo
        // en los bordes del vano, 104.6 y 256.3.
        assertEquals(103.35f, d.tramos[0].ancho, 0.01f)
        assertEquals(149.2f, d.tramos[1].ancho, 0.01f)
        assertEquals(80.75f, d.tramos[2].ancho, 0.01f)
        val ejes = HerramientasDiseno.ejesDeParantes(d)
        assertEquals(104.6f, ejes[0], 0.01f)
        assertEquals(256.3f, ejes[1], 0.01f)
        // El escalón: cuelga 67.3 del dintel y mide 77.2.
        assertEquals(67.3f, d.tramos[0].caida, 0.1f)
        assertEquals(77.2f, d.tramos[0].alto, 0.1f)
        // El del medio va de dintel a alféizar: no anota nada.
        assertEquals(0f, d.tramos[1].alto, 0f)
    }

    @Test
    fun parante_en_el_borde_del_escalon_parte_el_tramo_y_toma_la_forma() {
        val d = HerramientasDiseno.conParanteEn(rectangular(), 104.6f)
        assertEquals(2, d.tramos.size)
        // Eje del parante en 104.6: el tramo de la izquierda llega a 104.6 − 1.25.
        assertEquals(103.35f, d.tramos[0].ancho, 0.01f)
        assertEquals(232.45f, d.tramos[1].ancho, 0.01f)
        assertEquals(104.6f, HerramientasDiseno.ejesDeParantes(d)[0], 0.01f)
        // El de la izquierda es el escalón: 77.2 de alto colgando 67.3.
        assertEquals(67.3f, d.tramos[0].caida, 0.1f)
        assertEquals(77.2f, d.tramos[0].alto, 0.1f)
    }

    @Test
    fun mover_el_parante_cambia_solo_sus_dos_tramos() {
        val d = HerramientasDiseno.conParanteEn(rectangular(), 104.6f)
        val m = HerramientasDiseno.conParanteMovido(d, 0, 150f)
        assertEquals(150f, HerramientasDiseno.ejesDeParantes(m)[0], 0.01f)
        assertEquals(d.tramos.sumOf { it.ancho.toDouble() }, m.tramos.sumOf { it.ancho.toDouble() }, 0.01)
        // No deja un tramo más chico que el mínimo.
        assertEquals(d, HerramientasDiseno.conParanteMovido(d, 0, 2f))
    }

    @Test
    fun puente_aqui_crea_la_mocheta_del_lado_que_toca() {
        val d = HerramientasDiseno.conParanteEn(rectangular(), 104.6f)
        // En el tramo grande, un puente a la altura del escalón (67.3 desde el dintel): queda en la
        // mitad de arriba, así que el sistema va abajo (77.2) y la mocheta arriba (67.3).
        val p = HerramientasDiseno.conPuenteEn(d, 1, 67.3f)
        val franjas = p.tramos[1].franjas
        assertTrue(franjas[0].esSistema)
        assertEquals(77.2f, franjas[0].alto, 0.1f)
        assertEquals(67.3f, franjas[1].alto, 0.1f)
        assertEquals(listOf(67.3f), HerramientasDiseno.limitesDePuente(p, 1).map { r1(it) })
        // Un puente bajo (a 110 del dintel) en el mismo tramo sin mocheta: invertido.
        val inv = HerramientasDiseno.conPuenteEn(d, 1, 110f)
        assertTrue(!inv.tramos[1].franjas[0].esSistema)
        assertEquals(34.5f, inv.tramos[1].franjas[0].alto, 0.1f)
    }

    @Test
    fun mover_el_puente_y_pegarlo_a_otro() {
        val d = HerramientasDiseno.conParanteEn(rectangular(), 104.6f)
        val p = HerramientasDiseno.conPuenteEn(d, 1, 67.3f)
        val m = HerramientasDiseno.conPuenteMovido(p, 1, 0, 60f)
        assertEquals(60f, HerramientasDiseno.limitesDePuente(m, 1)[0], 0.01f)
        // El borde del escalón (67.3) es una guía horizontal: a 65 se pega.
        val guias = HerramientasDiseno.guiasHorizontales(m, sinPuente = 1 to 0)
        assertEquals(67.3f, HerramientasDiseno.pegar(65f, guias).valor, 0.01f)
    }

    @Test
    fun cada_franja_se_reparte_el_vano_que_tiene_por_dentro() {
        assertTrue(ContornoEnTramos.esEscalonado(vano))
        // Dos puentes, uno a 66 (un poco encima del escalón) y otro a 110 (un poco debajo del
        // recorte): la franja del medio tiene los 338.3 enteros. Midiendo en sus bordes salía
        // 256.3, cinco módulos de 51.2.
        assertEquals(338.3f, ContornoEnTramos.anchoDelVanoEnFranja(vano, 66f, 110f, 0f, 338.3f), 0.01f)
        // La mocheta de arriba empieza en el escalón, no en el canto del rectángulo.
        val arriba = ContornoEnTramos.trozoDelVanoEnFranja(vano, 0f, 66f, 0f, 338.3f)!!
        assertEquals(104.6f, arriba.first, 0.01f)
        assertEquals(338.3f, arriba.second, 0.01f)
        // Y la de abajo acaba en el recorte.
        val abajo = ContornoEnTramos.trozoDelVanoEnFranja(vano, 110f, 144.5f, 0f, 338.3f)!!
        assertEquals(0f, abajo.first, 0.01f)
        assertEquals(256.3f, abajo.second, 0.01f)
        // Dentro de un tramo, recortado a su trozo.
        assertEquals(151.7f, ContornoEnTramos.anchoDelVanoEnFranja(vano, 0f, 66f, 0f, 256.3f), 0.01f)
    }

    @Test
    fun el_rectangulo_del_sistema_de_un_tramo_que_cruza_el_recorte() {
        val d = HerramientasDiseno.conParanteEn(rectangular(), 104.6f)
        // El tramo grande pasa por encima del recorte de abajo a la derecha: su sistema tiene que
        // quedar por encima de 109 (N-66).
        val (techo, piso) = HerramientasDiseno.rectanguloDelSistema(d, 1)!!
        assertEquals(0.1f, techo, 0.2f)
        assertTrue(piso < 110f && piso > 108f)
    }
}
