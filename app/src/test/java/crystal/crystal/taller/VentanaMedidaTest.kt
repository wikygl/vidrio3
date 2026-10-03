package crystal.crystal.taller

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** La ventana medida como lista de lados, donde la curva es un lado más (2026-10-03). */
class VentanaMedidaTest {

    private fun pared(ancho: Float, flecha: Float = 0f) = LadoEsquina(ancho, ancho, 120f, 120f, 90f, flecha)

    /** La L con la esquina curva de antes: pared, curva en la arista, pared → tres lados. */
    @Test
    fun la_l_con_esquina_curva_son_tres_lados() {
        val vieja = EsquinaMedida(listOf(pared(150f), pared(120f)), listOf(EsquinaMedida.textoDeCurva(CurvaEsquina(90f, 81f, 120f, 90f))))
        val v = VentanaMedida.desdeEsquina(vieja)
        assertEquals(listOf(150f, 90f, 120f), v.lados.map { it.ancho })
        assertEquals(listOf(false, true, false), v.lados.map { it.esCurvo })
        assertEquals("entra y sale tangente", listOf(180f, 180f), v.uniones.map { it.grados })
        assertEquals(81f, v.lados[1].cuerda!!, 0.1f)
    }

    /** La que termina en curva (la medida del usuario): 150, curva 74, 180, curva 135. */
    @Test
    fun la_que_termina_en_curva_lleva_la_curva_como_ultimo_lado() {
        val vieja = EsquinaMedida(
            listOf(pared(150f), pared(180f)),
            listOf(
                EsquinaMedida.textoDeCurva(CurvaEsquina(74f, 66.5f, 120f, 90f)),
                EsquinaMedida.textoDeCurva(CurvaEsquina(135f, 120f, 120f, 90f))
            )
        )
        val v = VentanaMedida.desdeEsquina(vieja)
        assertEquals(listOf(150f, 74f, 180f, 135f), v.lados.map { it.ancho })
        assertEquals(listOf(false, true, false, true), v.lados.map { it.esCurvo })
        assertEquals(3, v.uniones.size)
    }

    /**
     * La que empieza en curva (el error del usuario, 2026-10-03): Medidas la mandaba con la curva en
     * el sitio de la pared de cero. Es una curva y una pared, y la curva sigue siendo la curva.
     */
    @Test
    fun la_que_empieza_en_curva_es_una_curva_y_una_pared() {
        val vieja = EsquinaMedida(listOf(pared(90f, flecha = 18f), pared(150f)), listOf("90"))
        val v = VentanaMedida.desdeEsquina(vieja)
        assertEquals(listOf(true, false), v.lados.map { it.esCurvo })
        assertEquals(90f, v.lados[0].ancho)
        assertEquals("la curva entra tangente", 180f, v.uniones.single().grados)
    }

    /** El texto nuevo va y vuelve, con el ángulo de una curva que no es tangente y una unión sin parante. */
    @Test
    fun el_texto_va_y_vuelve() {
        val v = VentanaMedida(
            listOf(
                LadoDeVentana(150f, 120f, 90f, divisiones = 3),
                LadoDeVentana(80f, 120f, 90f, flecha = 12.5f),
                LadoDeVentana(80f, 120f, 90f, flecha = -10f)
            ),
            listOf(UnionDeLados(135f), UnionDeLados(180f, parante = false))
        )
        val texto = VentanaMedida.aTexto(v)
        assertTrue(texto, texto.startsWith("v2;"))
        assertEquals(v, VentanaMedida.desdeTexto(texto))
        assertEquals(v, VentanaMedida.leer(texto))
        assertFalse(VentanaMedida.desdeTexto(texto)!!.uniones[1].parante)
    }

    /** Lo guardado con el formato de antes se sigue leyendo. */
    @Test
    fun lo_guardado_antes_se_sigue_leyendo() {
        val v = VentanaMedida.leer("150,150,120,120,90;120,120,120,120,90@90")!!
        assertEquals(listOf(150f, 120f), v.lados.map { it.ancho })
        assertEquals(90f, v.uniones.single().grados)
        assertNull(VentanaMedida.desdeTexto("150,150,120,120,90;120,120,120,120,90@90"))
    }
}
