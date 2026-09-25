package crystal.crystal.taller.nova

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Las reglas que el usuario dictó el 2026-09-25 en "Revisión de reglas" (docs/REGLAS_NOVA.md) y
 * que cambiaron medidas o cantidades de la lista de corte. Cada prueba lleva el número de la regla.
 *
 * Como todas las pruebas de corte, si una se pone en rojo es que el cálculo cambió: no se ajusta el
 * esperado sin una regla nueva que lo diga.
 */
class ReglasDictadasVidriosYPerfilesTest {

    // ---------- N-61: vidrio fijo, colindancia con pared o parante 0.5 ----------

    @Test
    fun n61_colindancia_por_lado() {
        // Pared 0.5, otro fijo 0.2, corrediza 0.
        assertEquals(0.5f, NovaCalculos.descuentoColindanciaFijo(listOf('f', 'c', 'f'), 0), 0f)
        assertEquals(0.7f, NovaCalculos.descuentoColindanciaFijo(listOf('f', 'f', 'c'), 0), 1e-6f)
        assertEquals(0f, NovaCalculos.descuentoColindanciaFijo(listOf('c', 'f', 'c'), 1), 0f)
    }

    @Test
    fun n61_aparente_fcf_descuenta_la_pared() {
        // Hoja 100 con U 1.5: alto de vidrio 100 − 1.7 = 98.3. Ancho: U de fijos − 0.5.
        val v = NovaVidriosHelper.calcularVidriosFijos(145f, 48.8f, 100f, 1.5f, 2, 3, "aparente", "nn")
        assertEquals("48.3 x 98.3 = 2", v)
    }

    @Test
    fun n61_aparente_tramo_de_5_el_fijo_del_centro_va_entero() {
        val v = NovaVidriosHelper.calcularVidriosFijos(300f, 60f, 100f, 1.5f, 3, 5, "aparente", "nn")
        assertEquals("59.5 x 98.3 = 2\n60 x 98.3 = 1", v)
    }

    @Test
    fun n87_full_fijos_y_fijo_solo_descuentan_una_grunia() {
        // Full fijos: cada vidrio − 0.4 (gruña, N-91). Un solo fijo es full fijos de uno.
        assertEquals("49.6 x 98.3 = 3", NovaVidriosHelper.calcularVidriosFijos(150f, 50f, 100f, 1.5f, 3, 3, "aparente", "nff"))
        assertEquals("79.6 x 98.3 = 1", NovaVidriosHelper.calcularVidriosFijos(80f, 80f, 100f, 1.5f, 1, 1, "aparente", "nn"))
    }

    @Test
    fun n61_inaparente_fcf_descuenta_la_pared() {
        // 143 × 145, 3 hojas, cruce 0.7: U de fijos (143 + 1.4) / 3 = 48.13 → vidrio 47.6.
        // Alto en INA: alto completo − 1.7 = 143.3.
        assertEquals("47.6 x 143.3 = 2", NovaInaCalculos.vidrioFijo(143f, 145f, 1.5f, 3, 0.7f))
    }

    // ---------- N-14: vidrio de mocheta de INA en ancho x alto ----------

    @Test
    fun n14_mocheta_inaparente_sale_ancho_por_alto() {
        // Mocheta 145 − 100 = 45 → vidrio de arriba 45 + 1 = 46 de alto (N-64).
        // Ancho: 143 − 2 × 48.13 − 0.5 = 46.2.
        val v = NovaInaCalculos.vidrioMocheta(143f, 145f, 100f, 100f, 3, 0.7f)
        assertEquals("46.2 x 46 = 1", v)
    }

    // ---------- N-63: vidrio de mocheta de APA ----------

    @Test
    fun n63_mocheta_aparente_con_tubo_es_mocheta_menos_u_mas_03() {
        // Mocheta 140 − (100 + 2.5) = 37.5 → 37.5 − 1.5 + 0.3 = 36.3. Ancho 145 − 0.5.
        val v = NovaVidriosHelper.calcularVidrioMochetaAparente(145f, 140f, 100f, 3, 2.5f, "tubo 2⅜ x 1", 1.5f)
        assertEquals("144.5 x 36.3 = 1", v)
    }

    @Test
    fun n63_mocheta_aparente_con_multiple_es_mocheta_menos_05() {
        // Mocheta 140 − (100 + 1.5) = 38.5 → 38.
        val v = NovaVidriosHelper.calcularVidrioMochetaAparente(145f, 140f, 100f, 3, 1.5f, "Múltiple", 1.5f)
        assertEquals("144.5 x 38 = 1", v)
    }

    // ---------- N-56: ángulo tope, 1 por corrediza contra pared o parante ----------

    @Test
    fun n56_tope_por_corrediza_en_borde() {
        assertEquals(1, NovaCalculos.anguloTopeCantidad(120f, 2, "nn"))   // fc
        assertEquals(0, NovaCalculos.anguloTopeCantidad(180f, 3, "nn"))   // fcf
        assertEquals(0, NovaCalculos.anguloTopeCantidad(240f, 4, "nn"))   // fccf
        assertEquals(2, NovaCalculos.anguloTopeCantidad(180f, 3, "ncfc")) // cfc, el ejemplo dictado
        assertEquals(2, NovaCalculos.anguloTopeCantidad(180f, 3, "n3c"))  // ccc
        assertEquals(4, NovaCalculos.anguloTopeCantidad(240f, 4, "ncc"))  // cc P cc
        assertEquals(0, NovaCalculos.anguloTopeCantidad(240f, 4, "nff"))
    }

    @Test
    fun n56_tope_en_la_lista_de_inaparente() {
        val r = NovaInaCalculos.calcularOtrosAluminios(120f, 140f, 100f, 2, 0f)
        assertEquals("99.1 = 1", r.angTope)
        val r3 = NovaInaCalculos.calcularOtrosAluminios(180f, 140f, 100f, 3, 0f)
        assertEquals("", r3.angTope)
    }

    // ---------- N-43: U parante de INA, solo fijos en los bordes de la ventana ----------

    @Test
    fun n43_u_parante_inaparente_no_cuenta_los_parantes() {
        // 12 hojas en tres tramos (fccf P fccf P fccf): los fijos junto a los parantes no llevan U
        // parante en INA; solo los dos de los muros. U parante = 145 − 2 × 1.5 = 142.
        val u = NovaInaCalculos.calcularTextoU(700f, 145f, 100f, 1.5f, 12, 0f)
        val lineaParante = u.lines().single { it.startsWith("142 =") }
        assertEquals("142 = 2", lineaParante)
        // 2 hojas (fc): un solo fijo contra el muro.
        val u2 = NovaInaCalculos.calcularTextoU(120f, 145f, 100f, 1.5f, 2, 0f)
        assertEquals("142 = 1", u2.lines().single { it.startsWith("142 =") })
    }

    // ---------- N-44: U parante de mocheta de INA ----------

    @Test
    fun n44_u_parante_de_mocheta() {
        // El ejemplo dictado: alto 100, hoja 80. U-13 (1.5): el −1.5 y el +1.5 se anulan → 20.
        assertEquals("20", NovaCalculos.df1(NovaInaCalculos.uParante2(100f, 80f, 1.5f)))
        // U-3/8 (1): (20 − 1) + 1.5 = 20.5.
        assertEquals("20.5", NovaCalculos.df1(NovaInaCalculos.uParante2(100f, 80f, 1f)))
        // Invertido: sin el +1.5 → 20 − 1.5 = 18.5.
        assertEquals("18.5", NovaCalculos.df1(NovaInaCalculos.uParante2(100f, 80f, 1.5f, invertido = true)))
    }
}
