package crystal.crystal.taller.nova.motor

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Las reglas dictadas por el usuario el 2026-09-25 que fijaban las pruebas del cálculo viejo
 * (`ReglasDictadasVidriosYPerfilesTest`, `PuenteDobleEscaladoTest`), pasadas al motor único el
 * 2026-09-30 al retirar aquel cálculo (fase 4). Mismos casos y mismos números, salvo donde una regla
 * posterior los cambió (anotado en cada prueba).
 *
 * Como todas las pruebas de corte: si una se pone en rojo es que el cálculo cambió; no se ajusta el
 * esperado sin una regla que lo diga.
 */
class ReglasDictadasEnElMotorTest {

    private val u13 = PerfilU(1f, 1.5f)
    private val u38 = PerfilU(1f, 1f)
    private val tubo = Puente("tubo 2⅜ x 1", 2.5f, 6f)
    private val multiple = Puente("Múltiple", 1.5f, 5f)

    private fun ventana(
        ancho: Float, alto: Float, hoja: Float, acabado: Acabado = Acabado.APA, mod: String = "nn",
        modelo: ModeloMocheta = ModeloMocheta.NORMAL, u: PerfilU = u13, puente: Puente = tubo,
        cruce: Float = 0.7f, divis: Int = 0
    ): MaterialesNova {
        val lado = DisenoAutomatico.lado(DisenoAutomatico.Entrada(anchoMedido = ancho, anchoCalculo = ancho, alto = alto,
            hoja = hoja, acabado = acabado, puente = puente, divisManual = divis, modulacion = mod))
        return MotorNova.calcular(VentanaNova(acabado, modelo, listOf(lado), u, puente, cruce))
    }

    private fun lineas(t: String) = t.lines().map { it.trim() }.filter { it.isNotBlank() }
    private fun vidrios(m: MaterialesNova) = lineas(MaterialesNova.textoVidrios(m.vidrios))

    // ---------- N-61: vidrio fijo ----------

    /** 145 en fcf, hoja 100, U-13: U de fijos (145 + 1.4) / 3 = 48.8 − 0.5 de pared; alto 100 − 1.7. */
    @Test
    fun n61_aparente_fcf_descuenta_la_pared() {
        assertTrue(vidrios(ventana(145f, 140f, 100f)).toString(), "48.3 x 98.3 = 2" in vidrios(ventana(145f, 140f, 100f)))
    }

    /** 300 en 5 (fcfcf) con U de fijos 60 (cruce 0): los de pared 59.5, el del centro va entero. */
    @Test
    fun n61_aparente_tramo_de_5_el_fijo_del_centro_va_entero() {
        val v = vidrios(ventana(300f, 140f, 100f, cruce = 0f))
        assertTrue(v.toString(), "59.5 x 98.3 = 2" in v && "60 x 98.3 = 1" in v)
    }

    /** N-87, N-91: full fijos y el fijo solo descuentan una gruña (0.4) por vidrio. */
    @Test
    fun n87_full_fijos_y_fijo_solo_descuentan_una_grunia() {
        assertTrue("49.6 x 98.3 = 3" in vidrios(ventana(150f, 140f, 100f, mod = "nff")))
        assertTrue("79.6 x 98.3 = 1" in vidrios(ventana(80f, 140f, 100f, divis = 1)))
    }

    /** 143 × 145 en INA, 3 hojas: (143 + 1.4) / 3 = 48.13 − 0.5 = 47.6; alto completo − 1.7 = 143.3. */
    @Test
    fun n61_inaparente_fcf_descuenta_la_pared() {
        assertTrue("47.6 x 143.3 = 2" in vidrios(ventana(143f, 145f, 100f, Acabado.INA)))
    }

    // ---------- N-14 y N-64: vidrio de mocheta de INA ----------

    /**
     * Ancho x alto (N-14). Alto: 145 − 100 + 1 = 46 (N-64). Ancho: lo que dejan las U de fijos menos
     * 0.3 a cada lado (N-64, dictado el 2026-09-25): 143 − 2 × 48.13 − 0.6 = 46.1. La prueba vieja
     * esperaba 46.2 (restaba 0.5 una vez), de antes de esa regla.
     */
    @Test
    fun n14_n64_mocheta_inaparente() {
        assertTrue(vidrios(ventana(143f, 145f, 100f, Acabado.INA)).toString(), "46.1 x 46 = 1" in vidrios(ventana(143f, 145f, 100f, Acabado.INA)))
    }

    // ---------- N-63: vidrio de mocheta de APA ----------

    /** Con tubo: mocheta − (u + 0.3). 140 − (100 + 2.5) = 37.5 → 35.7; ancho 145 − 0.5. */
    @Test
    fun n63_mocheta_aparente_con_tubo() {
        assertTrue("144.5 x 35.7 = 1" in vidrios(ventana(145f, 140f, 100f)))
    }

    /** Con Múltiple: mocheta − 0.5. 140 − (100 + 1.5) = 38.5 → 38. */
    @Test
    fun n63_mocheta_aparente_con_multiple() {
        assertTrue("144.5 x 38 = 1" in vidrios(ventana(145f, 140f, 100f, puente = multiple)))
    }

    // ---------- N-45: U de los costados de la mocheta (dictado de nuevo el 2026-10-01) ----------

    /**
     * Con Múltiple o gorrito el costado de la mocheta solo topa la U de arriba (el canal de abajo lo
     * trae el puente): mocheta − u. Con los demás puentes, U arriba y abajo: mocheta − 2u. 145 × 140,
     * hoja 100, U-13: Múltiple 38.5 − 1.5 = 37; tubo 37.5 − 3 = 34.5. Dos costados por mocheta.
     */
    @Test
    fun n45_u_de_los_costados_de_la_mocheta_segun_el_puente() {
        val conMultiple = lineas(MaterialesNova.texto(ventana(145f, 140f, 100f, puente = multiple).u))
        assertTrue(conMultiple.toString(), "37 = 2" in conMultiple)
        val conTubo = lineas(MaterialesNova.texto(ventana(145f, 140f, 100f, puente = tubo).u))
        assertTrue(conTubo.toString(), "34.5 = 2" in conTubo)
    }

    // ---------- N-56: tope, 1 por corrediza contra pared o parante ----------

    @Test
    fun n56_tope_por_corrediza_en_borde() {
        fun topes(ancho: Float, mod: String, divis: Int = 0) = ventana(ancho, 140f, 100f, mod = mod, divis = divis).tope.sumOf { it.cantidad }
        assertEquals(1, topes(120f, "nn"))   // fc
        assertEquals(0, topes(180f, "nn"))   // fcf
        assertEquals(0, topes(240f, "nn"))   // fccf
        assertEquals(2, topes(180f, "ncfc")) // cfc, el ejemplo dictado
        assertEquals(2, topes(180f, "n3c"))  // ccc
        // Doble corrediza: 2 hojas por N-20; con 4 escritas a mano (G-03) son cc P cc.
        assertEquals(2, topes(240f, "ncc"))
        assertEquals(4, topes(240f, "ncc", divis = 4))  // cc P cc
        assertEquals(0, topes(240f, "nff"))
    }

    @Test
    fun n56_tope_en_inaparente() {
        assertEquals(listOf("99.1 = 1"), lineas(MaterialesNova.texto(ventana(120f, 140f, 100f, Acabado.INA).tope)))
        assertEquals(emptyList<String>(), lineas(MaterialesNova.texto(ventana(180f, 140f, 100f, Acabado.INA).tope)))
    }

    // ---------- N-43: U parante de INA, solo los fijos de los bordes de la ventana ----------

    /** 12 hojas en tres tramos: solo los dos fijos de los muros llevan U parante, 145 − 3 = 142. */
    @Test
    fun n43_u_parante_inaparente_no_cuenta_los_parantes() {
        val u = lineas(MaterialesNova.texto(ventana(700f, 145f, 100f, Acabado.INA).u))
        assertEquals(u.toString(), "142 = 2", u.single { it.startsWith("142 =") })
        val u2 = lineas(MaterialesNova.texto(ventana(120f, 145f, 100f, Acabado.INA).u))
        assertEquals(u2.toString(), "142 = 1", u2.single { it.startsWith("142 =") })
    }

    // ---------- N-44: U parante de mocheta de INA ----------

    /** Alto 100, hoja 80, la corrediza de fc contra el muro: U-13 → 20; U-3/8 → 20.5; invertido 18.5. */
    @Test
    fun n44_u_parante_de_mocheta() {
        fun u(p: PerfilU, modelo: ModeloMocheta = ModeloMocheta.NORMAL) =
            lineas(MaterialesNova.texto(ventana(120f, 100f, 80f, Acabado.INA, modelo = modelo, u = p).u))
        assertTrue(u(u13).toString(), "20 = 1" in u(u13))
        assertTrue(u(u38).toString(), "20.5 = 1" in u(u38))
        assertTrue(u(u13, ModeloMocheta.INVERTIDO).toString(), "18.5 = 1" in u(u13, ModeloMocheta.INVERTIDO))
    }

    // ---------- N-35: bandera, dos corridas de puente; el parante no se duplica ----------

    @Test
    fun n35_bandera_duplica_la_corrida_de_puente_y_no_el_parante() {
        for (ancho in listOf(120f, 300f, 420f, 700f)) {
            val normal = ventana(ancho, 240f, 150f)
            val bandera = ventana(ancho, 240f, 150f, modelo = ModeloMocheta.BANDERA)
            assertEquals("ancho $ancho", 2 * normal.puente.sumOf { it.cantidad }, bandera.puente.sumOf { it.cantidad })
            assertEquals("ancho $ancho", normal.parante.sumOf { it.cantidad }, bandera.parante.sumOf { it.cantidad })
        }
    }
}
