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

    /** El diseño del usuario (2026-09-26): dos tramos con el parante centrado en el escalón (104.6). */
    private fun dosTramosEnElEscalon(): DisenoNova {
        val a = 104.6f - 1.25f
        val b = 338.3f - 2.5f - a
        return HerramientasDiseno.conFormaDelVano(DisenoNova(
            "apa", 338.3f, 144.5f,
            listOf(
                NovaTramo(a, listOf(NovaFranja(true, 0f, listOf(NovaModulo('f', a / 2), NovaModulo('c', a / 2))))),
                NovaTramo(b, listOf(NovaFranja(true, 0f, List(4) { NovaModulo('c', b / 4) })))
            )
        ).conContornoVano(vano))
    }

    @Test
    fun el_parante_en_un_escalon_se_deja_dentro_o_fuera() {
        // Dictado el 2026-09-26: centrado en la esquina del escalón solo si el usuario lo decide.
        val d = dosTramosEnElEscalon()
        assertEquals(104.6f, HerramientasDiseno.escalonDelParante(d, 0)!!, 0.01f)
        val op = HerramientasDiseno.opcionesDelParante(d, 0).associateBy { it.lado }

        // Dentro (del lado del tramo bajo): parante 77.2, tramo 1 102.1 × 77.2, tramo 2 233.7 × 109.4
        // (el alféizar del recorte mide 109.4 y 108.5: descuadre, manda la mayor, N-06).
        val dentro = op.getValue(HerramientasDiseno.LadoParante.DENTRO)
        assertEquals(77.2f, r1(dentro.largoParante), 0.01f)
        assertEquals(102.1f, r1(dentro.anchoIzq), 0.01f)
        assertEquals(77.2f, r1(dentro.altoIzq), 0.01f)
        assertEquals(233.7f, r1(dentro.anchoDer), 0.01f)
        assertEquals(109.4f, r1(dentro.altoDer), 0.01f)

        // Fuera (del lado del tramo alto): el parante va del dintel al piso, 144.4.
        val fuera = op.getValue(HerramientasDiseno.LadoParante.FUERA)
        assertEquals(144.4f, r1(fuera.largoParante), 0.11f)
        assertEquals(104.6f, r1(fuera.anchoIzq), 0.01f)
        assertEquals(231.2f, r1(fuera.anchoDer), 0.01f)

        // Centrado pisa los dos lados: mide lo del lado alto.
        val centrado = op.getValue(HerramientasDiseno.LadoParante.CENTRADO)
        assertEquals(fuera.largoParante, centrado.largoParante, 0.01f)

        // Lo mismo después de pasar por el paquete, que guarda los anchos truncados (103.35 → 103.3):
        // en el celular el tramo 2 salía 233.6.
        val leido = DisenoNova.desdePaquete(d.aPaquete())!!
        val dentroLeido = HerramientasDiseno.opcionesDelParante(leido, 0)
            .first { it.lado == HerramientasDiseno.LadoParante.DENTRO }
        assertEquals("233.7", crystal.crystal.taller.nova.NovaCalculos.df1(dentroLeido.anchoDer))
        assertEquals("102.1", crystal.crystal.taller.nova.NovaCalculos.df1(dentroLeido.anchoIzq))

        // Aplicado dentro, el parante ya no está en el escalón y no se vuelve a preguntar.
        val hecho = HerramientasDiseno.conParanteEnEscalon(d, 0, dentro)
        assertEquals(102.1f, r1(hecho.tramos[0].ancho), 0.01f)
        assertEquals(233.7f, r1(hecho.tramos[1].ancho), 0.01f)
        assertNull(HerramientasDiseno.escalonDelParante(hecho, 0))
    }

    @Test
    fun el_parante_fuera_del_escalon_se_dibuja_de_dintel_a_piso() {
        // Pedido del usuario (2026-10-18): elegido fuera, el parante llega al piso del tramo alto y
        // no deja un hueco entre el tramo y el vano. El dibujo lo lleva de donde a donde dice esto.
        val d = dosTramosEnElEscalon()
        val op = HerramientasDiseno.opcionesDelParante(d, 0).associateBy { it.lado }
        val fuera = HerramientasDiseno.conParanteEnEscalon(d, 0, op.getValue(HerramientasDiseno.LadoParante.FUERA))
        val (arribaF, abajoF) = HerramientasDiseno.bordesDelParante(fuera, 0)!!
        assertEquals(0.1f, r1(arribaF), 0.11f)
        assertEquals(144.5f, r1(abajoF), 0.01f)
        assertEquals(HerramientasDiseno.largoDelParante(fuera, 0)!!, abajoF - arribaF, 0.001f)

        // Dentro (del lado del tramo bajo), solo lo del escalón: del dintel bajo (67.3) al piso.
        val dentro = HerramientasDiseno.conParanteEnEscalon(d, 0, op.getValue(HerramientasDiseno.LadoParante.DENTRO))
        val (arribaD, abajoD) = HerramientasDiseno.bordesDelParante(dentro, 0)!!
        assertEquals(67.3f, r1(arribaD), 0.01f)
        assertEquals(144.5f, r1(abajoD), 0.01f)
    }

    /**
     * El diseño del usuario (2026-09-26), parante dentro: tramo 2 invertido, con la mocheta abajo
     * (35.1, con el puente dentro) y el sistema arriba (109.4), f c c f.
     */
    private fun formasConElParanteDentro(): DisenoNova {
        val base = dosTramosEnElEscalon()
        val dentro = HerramientasDiseno.opcionesDelParante(base, 0).first { it.lado == HerramientasDiseno.LadoParante.DENTRO }
        val d0 = HerramientasDiseno.conParanteEnEscalon(base, 0, dentro)
        val t2 = d0.tramos[1]
        return d0.copy(tramos = listOf(d0.tramos[0], t2.copy(franjas = listOf(
            NovaFranja(false, 35.1f, listOf(NovaModulo('f', t2.ancho))),
            NovaFranja(true, 109.4f, List(4) { NovaModulo(if (it == 0 || it == 3) 'f' else 'c', t2.ancho / 4) })
        ))))
    }

    @Test
    fun primero_se_identifica_el_tramo_entero() {
        // Dictado el 2026-09-26: el tramo 2 de Formas es un invertido con la mocheta recortada. Por
        // ser invertido no admite Múltiple (N-03): aunque en Nova esté elegido, su puente es el tubo.
        val d = formasConElParanteDentro()
        assertEquals(HerramientasDiseno.ModeloTramo.SIN_PUENTE, HerramientasDiseno.modeloDeTramo(d, 0))
        assertEquals(HerramientasDiseno.ModeloTramo.INVERTIDO, HerramientasDiseno.modeloDeTramo(d, 1))
        val multiple = HerramientasDiseno.PuenteTramo("Múltiple", 1.5f)
        assertNull(HerramientasDiseno.puenteDeTramo(d, 0, multiple))
        assertEquals(HerramientasDiseno.TUBO_POR_DEFECTO, HerramientasDiseno.puenteDeTramo(d, 1, multiple))
        // Un puente que el invertido sí admite se respeta (G-03).
        val paflon = HerramientasDiseno.PuenteTramo("paflon 1½", 3.8f)
        assertEquals(paflon, HerramientasDiseno.puenteDeTramo(d, 1, paflon))
    }

    @Test
    fun el_puente_en_un_escalon_se_deja_dentro_o_fuera() {
        // Dictado el 2026-09-26: dentro, el puente entero entra en la mocheta; el sistema queda un
        // rectángulo limpio de 233.7 × 109.4 y el puente mide lo que la mocheta, 151.7.
        val d = formasConElParanteDentro()
        val e = HerramientasDiseno.escalonDelPuente(d, 1, 0)!!
        assertEquals(109.4f, r1(e.y), 0.01f)
        assertTrue(e.angostoAbajo)
        val op = HerramientasDiseno.opcionesDelPuente(d, 1, 0).associateBy { it.lado }
        val dentro = op.getValue(HerramientasDiseno.LadoParante.DENTRO)
        assertEquals(151.7f, r1(dentro.largoPuente), 0.01f)
        assertEquals(233.7f, r1(dentro.sistema!!.first), 0.01f)
        assertEquals(109.4f, r1(dentro.sistema!!.second), 0.01f)
        assertEquals(151.7f, r1(dentro.mocheta!!.first), 0.01f)
        assertEquals(32.6f, r1(dentro.mocheta!!.second), 0.01f)   // 35.1 − 2.5 (N-31)
        // Fuera, el puente queda sobre el escalón: el sistema baja a 106.9 y el puente mide 233.7.
        val fuera = op.getValue(HerramientasDiseno.LadoParante.FUERA)
        assertEquals(233.7f, r1(fuera.largoPuente), 0.01f)
        assertEquals(106.9f, r1(fuera.sistema!!.second), 0.01f)
        assertEquals(35.1f, r1(fuera.mocheta!!.second), 0.01f)
        val aplicado = HerramientasDiseno.conPuenteEnEscalon(d, 1, 0, fuera)
        assertNull(HerramientasDiseno.escalonDelPuente(aplicado, 1, 0))
    }

    @Test
    fun cada_franja_es_un_rectangulo_dentro_del_vano() {
        // La mocheta del tramo 2 solo llega hasta el recorte: 151.7, no 233.7.
        val d = formasConElParanteDentro()
        val mocheta = HerramientasDiseno.rectanguloDeFranja(d, 1, 0)!!
        assertEquals(151.7f, r1(mocheta.first), 0.01f)
        assertEquals(35.1f, r1(mocheta.second), 0.01f)
        val sistema = HerramientasDiseno.rectanguloDeFranja(d, 1, 1)!!
        assertEquals(233.7f, r1(sistema.first), 0.01f)
        assertEquals(109.4f, r1(sistema.second), 0.01f)
        // El tramo 1 es un rectángulo solo: 102.1 × 77.2.
        assertEquals(77.2f, r1(HerramientasDiseno.altoDelSistema(d, 0)!!), 0.01f)
    }

    @Test
    fun el_descuadre_del_vano_se_detecta() {
        // Formas: el alféizar del recorte va de 109.4 a 108.5 (0.9). El dintel baja 0.1 en 233.7:
        // eso es el trazo del apunte, no un descuadre. Los escalones de verdad (35.1, 67.3) tampoco.
        val d = HerramientasDiseno.descuadres(vano)
        assertEquals(1, d.size)
        assertEquals(0.9f, r1(d[0].diferencia), 0.01f)
        assertEquals(256.3f to 109.4f, d[0].desde)
    }

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
