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

    /**
     * "polis" ventana 2 (archivada por el usuario el 2026-09-26, tal cual): 229.5 × 223.4, bandera,
     * `f c f`, U-13. Una columna entra desde la pared izquierda (36.4 × 97.2) a media altura del fijo
     * de la izquierda. Dictado: la U sigue la pared; las horizontales tienen preferencia (G-01) y en la
     * esquina saliente suman el alto de la U (36.4 + 1.5 = 37.9; 36.1 + 1.5 = 37.6); la cara de la
     * columna, 97.2; la pared de arriba 17.4 − 1.7 = 15.7 y la de abajo 21.4 − 3 = 18.4.
     */
    @Test
    fun polis_2_la_columna_en_el_costado_del_fijo() {
        val paquete = "{nova,apa,[229.5,223.4:Tl<229.5>(m<43.7>(f<114.7>f<114.7>);s<136>(f<76.5>c<76.5>f<76.5>);" +
            "m<43.7>(f<114.7>f<114.7>))V<0/0|229.5/0.6|229.5/223.4|0.3/223.4|0.3/158.3|36.4/158.3|36.4/61.1|0/61.1>]}"
        val v = DesdeDiseno.ventana(paquete, PerfilU(1f, 1.5f), Puente("tubo 2⅜ x 1", 2.5f, 6f))!!
        val m = MotorNova.calcular(v)

        assertEquals(
            listOf("134.3 = 1", "15.7 = 1", "18.4 = 1", "229.2 = 2", "229.5 = 2", "37.6 = 1", "37.9 = 1",
                "38.2 = 4", "76.9 = 2", "97.2 = 1"),
            t(m.u)
        )
        // Lo demás, como estaba archivado.
        assertEquals(listOf("229.2 = 1", "229.5 = 1"), t(m.puente))
        assertEquals(listOf("229.4 = 1"), t(m.riel))
        assertEquals(listOf("229.4 = 1"), t(m.rielSuperior))
        assertEquals(listOf("134.4 = 4"), t(m.portafelpa))
        assertEquals(listOf("38.2 = 2"), t(m.tee))
        assertEquals(listOf("76.9 = 1"), t(m.hache))
        assertEquals(
            listOf("114.1 x 39.4 = 2", "114.2 x 39.4 = 2", "75.5 x 132.5 = 1", "76.4 x 134.3 = 2"),
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

    /**
     * "polis" Vna4 (archivada por el usuario el 2026-09-26): el trapecio de 208.6 × 156.8, el lado
     * izquierdo inclinado (de x 0 arriba a x 75.4 abajo), U-3/8, Múltiple, sistema 112 abajo (`f c f`)
     * y mocheta arriba. La geometría se mide módulo por módulo. Dictado:
     * - el fijo contra el lado inclinado lleva el vidrio entero (el rectángulo que lo contiene) y se
     *   corta en obra: 56.4 × 110.8, no 78 × 35.1 ("recortado");
     * - su U: primero la de abajo (3.1, "como 2 o 3 cm") y sobre ella la diagonal (124.3 − 1 = 123.2);
     * - la mocheta: U de arriba 208.6 (la de abajo la trae el Múltiple), la de su lado inclinado en
     *   diagonal (48.1 − 1 = 47) y el vidrio por el rectángulo mayor (208.6 en dos paños);
     * - el riel: 133.2 + 1.2 × tan (lo que sube el alto del riel por la pendiente) − 0.06 = 133.7;
     * - sin tope: la corrediza no toca ninguna pared.
     */
    @Test
    fun vna4_el_lado_inclinado_se_mide_modulo_por_modulo() {
        val p = "{nova,apa,[208.6,156.8:Tl<208.6>(s<112>(f<78>c<65.2>f<65.2>);m<44.8>(f<104.3>f<104.3>))" +
            "V<0/0|208.6/0|208.6/156.8|75.4/156.8>]}"
        val m = MotorNova.calcular(DesdeDiseno.ventana(p, PerfilU(1f, 1f), Puente("Múltiple", 1.5f, 5f))!!)
        assertEquals(listOf("110.8 = 1", "123.2 = 1", "208.6 = 1", "3.1 = 1", "41.3 = 1", "47 = 1", "65.7 = 1"), t(m.u))
        assertEquals(listOf("187.7 = 1"), t(m.puente))
        assertEquals(listOf("133.7 = 1"), t(m.riel))
        assertEquals(listOf("110.4 = 4"), t(m.portafelpa))
        assertEquals(listOf(""), t(m.tope))
        assertEquals(listOf("42.3 = 1"), t(m.tee))
        assertEquals(listOf("65.7 = 1"), t(m.hache))
        assertEquals(
            listOf("103.8 x 42.8 = 2", "56.4 x 110.8 = 1", "64.3 x 108.5 = 1", "65.2 x 110.8 = 1"),
            MaterialesNova.textoVidrios(m.vidrios).lines().sorted()
        )
    }
}
