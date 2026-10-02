package crystal.crystal.taller.nova.motor

import crystal.crystal.casilla.ListaCasilla
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** El curvado (N-102, dictado el 2026-10-01): cuerda, flecha, ampliación y lo que se archiva. */
class CurvadoNovaTest {

    private fun lado(ancho: Float, radios: List<Float>, tramos: List<Float> = listOf(ancho)) = LadoNova(
        anchoMedido = ancho, anchoCalculo = ancho, alto = 120f, altoMedido = 120f, altoHoja = 120f,
        tramos = tramos.map { Tramo(listOf(Modulo(false, it))) },
        izquierda = Borde.MURO, derecha = Borde.MURO, radios = radios
    )

    /**
     * Un puente de 108 en una curva de radio 100: medio ángulo 108 / 200 = 0.54 rad.
     * Cuerda 2 × 100 × sen 0.54 = 102.83; flecha 100 × (1 − cos 0.54) = 14.23; ampliación 108 + 10.
     */
    @Test
    fun cuerda_flecha_y_ampliacion_de_un_arco() {
        val c = CurvadoNova.de(108f, 100f)!!
        assertEquals(102.83f, c.cuerda, 0.01f)
        assertEquals(14.23f, c.flecha, 0.01f)
        assertEquals(118f, c.ampliado, 0.001f)
        // La panza hacia adentro no cambia la cuerda ni la flecha.
        assertEquals(c, CurvadoNova.de(108f, -100f))
        assertNull(CurvadoNova.de(108f, 0f))
    }

    /** Lo horizontal toma la curva del lado; lo vertical no se curva y queda igual. */
    @Test
    fun en_un_lado_curvo_solo_se_curva_lo_horizontal_y_los_vidrios() {
        val m = CurvadoNova.curvar(
            MaterialesNova(
                puente = listOf(Pieza(108f, 1), Pieza(108f, 1)),
                parante = listOf(Pieza(97f, 2, Orientacion.VERTICAL)),
                vidrios = listOf(Vidrio(60.4f, 126.9f, 2), Vidrio(60.4f, 126.9f, 2))
            ),
            lado(108f, listOf(100f))
        )
        assertTrue(m.hayCurva)
        assertEquals("108 = 2\nC = 102.8\nF = 14.2\nAmpliación 118 = 2", CurvadoNova.texto(m.puente))
        assertEquals("97 = 2", CurvadoNova.texto(m.parante))
        // El vidrio: 60.4 en radio 100, medio ángulo 0.302 rad → cuerda 59.49, flecha 4.53. Sin ampliación.
        assertEquals("60.4 x 126.9 = 4\nC = 59.4\nF = 4.5", CurvadoNova.textoVidrios(m.vidrios))
        // La vista corta no cambia.
        assertEquals("108 = 2", MaterialesNova.texto(m.puente))
    }

    @Test
    fun un_lado_recto_no_se_curva() {
        val m = CurvadoNova.curvar(MaterialesNova(puente = listOf(Pieza(108f, 1))), lado(108f, emptyList()))
        assertFalse(m.hayCurva)
        assertEquals("108 = 1", CurvadoNova.texto(m.puente))
        assertEquals("108 = 1", CurvadoNova.textoArchivo(m.puente))
    }

    /**
     * Una S de dos arcos de 100, cada uno su tramo: radio 100 hacia afuera y 80 hacia adentro. El
     * puente de cada tramo toma su arco; la U de lado a lado pasa por los dos y se curva por trozos.
     */
    @Test
    fun en_una_s_cada_pieza_toma_el_arco_donde_va() {
        val l = lado(200f, listOf(100f, -80f), tramos = listOf(100f, 100f))
        val m = CurvadoNova.curvar(
            MaterialesNova(
                puente = listOf(Pieza(100f, 1, tramo = 0), Pieza(100f, 1, tramo = 1)),
                u = listOf(Pieza(200f, 1))
            ),
            l
        )
        // 100 en radio 100: medio ángulo 0.5 → C 95.88, F 12.24; en radio 80: 0.625 → C 93.6, F 15.1.
        assertEquals(
            "100 = 1\nC = 95.8\nF = 12.2\nAmpliación 110 = 1\n100 = 1\nC = 93.6\nF = 15.1\nAmpliación 110 = 1",
            CurvadoNova.texto(m.puente)
        )
        assertEquals(
            "200 = 1\nArco 1: 100 (hacia afuera)\nC = 95.8\nF = 12.2\nArco 2: 100 (hacia adentro)\nC = 93.6\nF = 15.1\nAmpliación 210 = 1",
            CurvadoNova.texto(m.u)
        )
    }

    /** Se archiva lo curvo con su ampliación y la marca; lo recto, como siempre. */
    @Test
    fun se_archiva_con_la_ampliacion_y_la_marca() {
        val m = CurvadoNova.curvar(
            MaterialesNova(
                u = listOf(Pieza(108f, 2), Pieza(97f, 2, Orientacion.VERTICAL)),
                vidrios = listOf(Vidrio(60.4f, 126.9f, 4))
            ),
            lado(108f, listOf(100f))
        )
        assertEquals("118 = 2 curvo C102.8 F14.2 afuera\n97 = 2", CurvadoNova.textoArchivo(m.u))
        assertEquals("60.4 x 126.9 = 4 curvo C59.4 F4.5 afuera", CurvadoNova.textoArchivoVidrios(m.vidrios))
        // Sin caras se dice, para que el curvador sepa que lo puede voltear.
        assertEquals("60.4 x 126.9 = 4 curvo C59.4 F4.5 afuera [sin caras]", CurvadoNova.textoArchivoVidrios(m.vidrios, CurvadoNova.SIN_CARAS))
    }

    /**
     * Una S de reflejante, con el espejo hacia afuera (desde adentro se ve el color): en el arco que
     * sale el espejo queda convexo, en el que entra cóncavo. Son dos piezas distintas aunque midan lo
     * mismo, y no se suman. Con el espejo hacia adentro, al revés.
     */
    @Test
    fun en_una_s_la_cara_especial_queda_convexa_en_un_arco_y_concava_en_el_otro() {
        val m = CurvadoNova.curvar(
            MaterialesNova(vidrios = listOf(Vidrio(60f, 120f, 2, tramo = 0), Vidrio(60f, 120f, 2, tramo = 1))),
            lado(200f, listOf(100f, -100f), tramos = listOf(100f, 100f))
        )
        val espejoAfuera = CurvadoNova.Caras("espejo", haciaAdentro = false)
        assertEquals(
            "60 x 120 = 2 curvo C59.1 F4.4 afuera [espejo convexa]\n60 x 120 = 2 curvo C59.1 F4.4 adentro [espejo cóncava]",
            CurvadoNova.textoArchivoVidrios(m.vidrios, espejoAfuera)
        )
        val espejoAdentro = CurvadoNova.Caras("espejo", haciaAdentro = true)
        assertEquals(
            "60 x 120 = 2 curvo C59.1 F4.4 afuera [espejo cóncava]\n60 x 120 = 2 curvo C59.1 F4.4 adentro [espejo convexa]",
            CurvadoNova.textoArchivoVidrios(m.vidrios, espejoAdentro)
        )
    }

    /** La marca llega a la lista archivada en su lugar, sin romper la cantidad. */
    @Test
    fun la_lista_archivada_lleva_la_marca() {
        assertTrue(ListaCasilla.esCurva(listOf("118", "2", "Vna5", "pipa", ListaCasilla.MARCA_CURVO)))
        assertFalse(ListaCasilla.esCurva(listOf("97", "2", "Vna5")))
    }
}
