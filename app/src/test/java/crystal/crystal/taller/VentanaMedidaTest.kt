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
        // Detrás de la curva la plantilla escribía lo que gira el arco: esa curva sale en tangente.
        val giro = ArcoEsquina.deDesarrolloYFlecha(90f, 18f)!!.anguloGrados
        val vieja = EsquinaMedida(listOf(pared(90f, flecha = 18f), pared(150f)), listOf("%.1f".format(java.util.Locale.US, giro)))
        val v = VentanaMedida.desdeEsquina(vieja)
        assertEquals(listOf(true, false), v.lados.map { it.esCurvo })
        assertEquals(90f, v.lados[0].ancho)
        assertEquals("la curva entra tangente", 180f, v.uniones.single().grados)
        // Desde el 2026-10-19 va el ángulo de la unión: 150 es un quiebre de 30.
        val conQuiebre = VentanaMedida.desdeEsquina(EsquinaMedida(listOf(pared(90f, flecha = 18f), pared(150f)), listOf("150")))
        assertEquals(150f, conQuiebre.uniones.single().grados)
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

    /**
     * Los pliegues del diseño: la L con un cuarto de círculo tangente da `A<90>` detrás de la curva,
     * como siempre; la curva que no sale tangente lleva su pliegue delante, y el de detrás suma lo
     * que giró esa unión.
     */
    @Test
    fun los_pliegues_del_diseno() {
        val cuarto = ArcoEsquina.deDesarrolloYCuerda(157.1f, 141.4f)!!.flecha
        val tangente = VentanaMedida(
            listOf(LadoDeVentana(150f, 120f), LadoDeVentana(157.1f, 120f, flecha = cuarto), LadoDeVentana(120f, 120f)),
            listOf(UnionDeLados(180f), UnionDeLados(180f))
        )
        val p = tangente.plieguesDelDiseno()
        assertNull(p[0]); assertNull("la curva tangente no lleva pliegue", p[1])
        assertEquals(90f, p[2]!!, 0.5f)

        // La curva entra a 135° (gira 45) y sale tangente: detrás, la esquina entera gira 45 + 90 = 135.
        val conAngulo = tangente.copy(uniones = listOf(UnionDeLados(135f), UnionDeLados(180f)))
        val q = conAngulo.plieguesDelDiseno()
        assertEquals(135f, q[1]!!, 0.01f)
        assertEquals(45f, q[2]!!, 0.5f)

        // Entre dos paredes, su esquina.
        assertEquals(90f, VentanaMedida(listOf(LadoDeVentana(150f, 120f), LadoDeVentana(120f, 120f))).plieguesDelDiseno()[1]!!, 0.01f)
    }

    /** La planta: la L de 150 + 120 dobla en escuadra hacia quien mira; el alféizar y el dintel viajan. */
    @Test
    fun la_planta_y_los_extremos() {
        val ele = VentanaMedida(listOf(LadoDeVentana(150f, 120f), LadoDeVentana(120f, 120f)), alfeizar = 90f, dintel = 15f)
        val p = ele.planta()
        assertEquals(150f, p[0].last().first, 0.01f)
        assertEquals(0f, p[0].last().second, 0.01f)
        assertEquals(150f, p[1].last().first, 0.01f)
        assertEquals(120f, p[1].last().second, 0.01f)
        val vuelta = VentanaMedida.desdeTexto(VentanaMedida.aTexto(ele))!!
        assertEquals(90f, vuelta.alfeizar)
        assertEquals(15f, vuelta.dintel)
        // Un cuarto de círculo de 157.1 tangente cierra la escuadra: acaba a 100 y 100 del principio.
        val cuarto = ArcoEsquina.deDesarrolloYCuerda(157.1f, 141.4f)!!.flecha
        val curva = VentanaMedida(listOf(LadoDeVentana(157.1f, 120f, flecha = cuarto))).planta().single().last()
        assertEquals(100f, curva.first, 0.5f)
        assertEquals(100f, curva.second, 0.5f)
    }

    /** Lo guardado con el formato de antes se sigue leyendo. */
    @Test
    fun lo_guardado_antes_se_sigue_leyendo() {
        val v = VentanaMedida.leer("150,150,120,120,90;120,120,120,120,90@90")!!
        assertEquals(listOf(150f, 120f), v.lados.map { it.ancho })
        assertEquals(90f, v.uniones.single().grados)
        assertNull(VentanaMedida.desdeTexto("150,150,120,120,90;120,120,120,120,90@90"))
    }

    /**
     * Lo guardado con una pared de cero (la ventana que empezaba o acababa en curva, antes del
     * 2026-10-19) se lee sin ella: la curva pasa a ser un lado curvo y no queda ningún lado de 0.
     */
    @Test
    fun la_pared_de_cero_de_antes_no_es_un_lado() {
        val empieza = EsquinaMedida(
            listOf(LadoEsquina(0f, 0f, 120f, 120f, 90f), LadoEsquina(150f, 150f, 120f, 120f, 90f)),
            listOf(EsquinaMedida.textoDeCurva(CurvaEsquina(90f, 81f, 120f, 90f)))
        )
        val v1 = VentanaMedida.desdeEsquina(empieza)
        assertEquals(listOf(true, false), v1.lados.map { it.esCurvo })
        assertTrue(v1.lados.none { it.ancho <= 0.5f })
        val acaba = EsquinaMedida(
            listOf(LadoEsquina(170f, 170f, 105f, 105f, 75f), LadoEsquina(0f, 0f, 105f, 105f, 75f)),
            listOf(EsquinaMedida.textoDeCurva(CurvaEsquina(58.6f, 58.2f, 105f, 75f)))
        )
        val v2 = VentanaMedida.desdeEsquina(acaba)
        assertEquals(listOf(false, true), v2.lados.map { it.esCurvo })
        assertEquals(1, v2.uniones.size)
    }
}
