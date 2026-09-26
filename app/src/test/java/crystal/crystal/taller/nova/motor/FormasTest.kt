package crystal.crystal.taller.nova.motor

import crystal.crystal.Diseno.nova.DisenoNova
import crystal.crystal.Diseno.nova.HerramientasDiseno
import crystal.crystal.Diseno.nova.NovaFranja
import crystal.crystal.Diseno.nova.NovaModulo
import crystal.crystal.Diseno.nova.NovaTramo
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Forma poligonal, primer caso (dictado por el usuario el 2026-09-26): el escalonado de Formas,
 * 338.3 × 144.5, en aparente. Primero se identifica cada tramo entero y después se parte en
 * rectángulos (N-96):
 *
 * - tramo 1: 102.1 × 77.2, `f c`, sin puente; el parante va dentro del escalón (N-94) y mide 77.2;
 * - tramo 2: invertido con la mocheta recortada, `f c c f`. Por ser invertido lleva tubo aunque la
 *   ventana tenga Múltiple (N-03). Su sistema es 233.7 × 109.4 y su mocheta 151.7 × 35.1; el puente
 *   entra en la mocheta y mide 151.7 (N-97). El riel sale del ancho del sistema: 233.6.
 */
class FormasTest {

    private val vano = listOf(
        0f to 144.5f, 256.3f to 144.5f, 256.3f to 109.4f, 338.3f to 108.5f,
        338.3f to 0f, 104.6f to 0.1f, 104.6f to 67.3f, 0f to 67.3f
    )

    /** El diseño del usuario, tal como queda en novaDiseño: parante dentro, puente dentro. */
    private fun formas(): String {
        val a = 104.6f - 1.25f
        val b = 338.3f - 2.5f - a
        val base = HerramientasDiseno.conFormaDelVano(DisenoNova("apa", 338.3f, 144.5f, listOf(
            NovaTramo(a, listOf(NovaFranja(true, 0f, listOf(NovaModulo('f', a / 2), NovaModulo('c', a / 2))))),
            NovaTramo(b, listOf(NovaFranja(true, 0f, List(4) { NovaModulo(if (it == 0 || it == 3) 'f' else 'c', b / 4) })))
        )).conContornoVano(vano))
        val dentro = HerramientasDiseno.opcionesDelParante(base, 0).first { it.lado == HerramientasDiseno.LadoParante.DENTRO }
        val d = HerramientasDiseno.conParanteEnEscalon(base, 0, dentro)
        val t2 = d.tramos[1]
        return d.copy(tramos = listOf(d.tramos[0], t2.copy(franjas = listOf(
            NovaFranja(false, 35.1f, listOf(NovaModulo('f', t2.ancho))),
            NovaFranja(true, 109.4f, List(4) { NovaModulo(if (it == 0 || it == 3) 'f' else 'c', t2.ancho / 4) })
        )))).aPaquete()
    }

    private fun t(p: List<Pieza>) = MaterialesNova.texto(p).lines().sorted()

    /**
     * "polis" (archivado por el usuario el 2026-09-26 en el moto g04, tal cual): 506.5 × 189.7, dos
     * tramos invertidos de 252. El techo baja 39.2 sobre los primeros 61.8 (una viga) y el alféizar
     * sube a 133.6 desde 302.9. El fijo de la izquierda queda bajo la viga: 94.4 de hoja y no 133.6.
     * Dictado: el fijo corredizo solo llega hasta donde empieza el corte (190.2 − 0.06); en la cara
     * horizontal del corte, sobre el fijo, va una U; la corrediza de al lado colinda con la viga como
     * con una pared y lleva tope completo (133.6 − 0.9).
     */
    @Test
    fun polis_la_viga_sobre_el_fijo_parte_el_sistema_por_modulos() {
        val paquete = "{nova,apa,[506.5,189.7:Tl<252>(m<56.1>(f<126>f<126>);s<133.6>(f<62>c<63.3>c<63.3>f<63.3>))" +
            "P<2.5>Tl<252>(m<56.1>(f<252>);s<133.6>(f<63>c<63>c<63>f<63>))" +
            "V<0/39.2|0/189.7|302.9/189.7|302.9/133.6|506.5/133.2|506.5/0|61.8/0|61.8/39.2>]}"
        val v = DesdeDiseno.ventana(paquete, PerfilU(1f, 1f), Puente("tubo 2⅜ x 1", 2.5f, 6f))!!
        val m = MotorNova.calcular(v)

        // U: la del corte (62.3, igual que su U de fijo) y la U parante del fijo bajo la viga (93.2).
        assertEquals(listOf("132.4 = 3", "252 = 2", "48.4 = 2", "51.6 = 4", "62.3 = 2", "63.3 = 2", "63.6 = 1", "93.2 = 1"), t(m.u))
        assertEquals(listOf("252 = 1", "48.4 = 1"), t(m.puente))
        assertEquals(listOf("251.9 = 2"), t(m.riel))
        assertEquals(listOf("190.1 = 1", "251.9 = 1"), t(m.rielSuperior))
        assertEquals(listOf("132 = 11", "92.8 = 1"), t(m.portafelpa))
        assertEquals(listOf("132.7 = 1"), t(m.tope))
        assertEquals(listOf("63.3 = 2", "63.6 = 2"), t(m.hache))
        assertEquals(listOf("189.7 = 1"), t(m.parante))
        assertEquals(listOf("51.6 = 1"), t(m.tee))
        assertEquals(
            listOf("125.5 x 52.3 = 2", "47.9 x 52.3 = 1", "61.8 x 93.2 = 1", "61.9 x 130.1 = 2",
                "62.2 x 130.1 = 2", "62.8 x 132.4 = 2", "63.1 x 132.4 = 1"),
            MaterialesNova.textoVidrios(m.vidrios).lines().sorted()
        )
    }

    @Test
    fun el_escalonado_de_formas_en_aparente() {
        val multiple = Puente("Múltiple", 1.5f, 5f)
        val v = DesdeDiseno.ventana(formas(), PerfilU(1f, 1.5f), multiple)!!
        val m = MotorNova.calcular(v)

        assertEquals(listOf("107.7 = 2", "151.7 = 2", "29.6 = 2", "51.4 = 1", "58.7 = 2", "75.5 = 1"), t(m.u))
        assertEquals(listOf("151.7 = 1"), t(m.puente))
        assertEquals(listOf("102 = 1", "233.6 = 1"), t(m.riel))
        assertEquals(listOf("102 = 1", "233.6 = 1"), t(m.rielSuperior))
        assertEquals(RielSuperior.FIJO_CORREDIZO, m.nombreRielSuperior)
        assertEquals(listOf("107.8 = 6", "75.6 = 3"), t(m.portafelpa))
        assertEquals(listOf("76.3 = 1"), t(m.tope))
        assertEquals(listOf("51.4 = 1", "58.7 = 2"), t(m.hache))
        assertEquals(listOf("77.2 = 1"), t(m.parante))
        assertEquals(listOf(""), t(m.tee))
        assertEquals(
            listOf("151.2 x 30.8 = 1", "50 x 73.7 = 1", "50.9 x 75.5 = 1", "57.3 x 105.9 = 2", "58.2 x 107.7 = 2"),
            MaterialesNova.textoVidrios(m.vidrios).lines().sorted()
        )
    }
}
