package crystal.crystal.taller.rejas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Reja de puerta (2026-10-10): como la calculadora de puertas. Los esperados salen de las fórmulas
 * de Puerta: hoja = ancho − 2 marcos − holgura; alto de hoja = puente − holgura; sobreluz = alto −
 * (puente + marco + puente de tubo); con alto > 210 y puente en 0, el puente va a 199.
 */
class RejaPuertaTest {

    // 90 x 240, marco y tubo 3.8, holgura 1, puente escrito a 200.
    private val puerta = RejaCalculo.Reja(
        anchoCm = 90f, altoCm = 240f, modelo = ModeloReja.BARROTES_VERTICALES,
        tipo = TipoReja.PUERTA, puenteCm = 200f, holguraCm = 1f
    )

    private fun largos(tubos: List<TuboReja>, nombre: String) = tubos.filter { it.nombre == nombre }.map { RejaCalculo.fmt(it.largo) }.sorted()

    @Test
    fun el_marco_fijo_son_dos_jambas_y_el_cabezal_sin_umbral() {
        val tubos = RejaCalculo.todos(puerta)
        // Jambas de 240; el cabezal entre ellas, 90 − 2 × 3.8 = 82.4. Abajo no lleva tubo.
        assertEquals(listOf("240", "240", "82.4"), largos(tubos, "Marco"))
    }

    @Test
    fun el_puente_va_de_jamba_a_jamba_a_la_altura_escrita() {
        val tubos = RejaCalculo.todos(puerta)
        assertEquals(listOf("82.4"), largos(tubos, "Puente"))
        val puente = tubos.first { it.nombre == "Puente" }
        // El eje a 200 + medio tubo: la cara de abajo del puente queda a 200.
        assertEquals(201.9f, puente.y0, 0.01f)
        assertTrue(RejaCalculo.conSobreluz(puerta))
    }

    @Test
    fun la_hoja_mide_como_en_puertas() {
        val h = RejaCalculo.hoja(puerta)
        assertEquals(90f - 2 * 3.8f - 1f, h.anchoCm, 0.01f)   // 81.4
        assertEquals(200f - 1f, h.altoCm, 0.01f)              // 199
        // Su marco: dos de 199 y dos de 81.4 − 2 × 3.8 = 73.8.
        assertEquals(listOf("199", "199", "73.8", "73.8"), largos(RejaCalculo.todos(puerta), "Marco hoja"))
    }

    @Test
    fun la_sobreluz_se_rellena_entre_el_puente_y_el_cabezal() {
        // Sobreluz libre: 240 − (200 + 3.8 + 3.8) = 32.4. Los barrotes de la sobreluz miden eso.
        val tubos = RejaCalculo.todos(puerta)
        val arriba = tubos.filter { it.nombre == "Parante mocheta" }
        assertTrue("la sobreluz lleva barrotes", arriba.isNotEmpty())
        arriba.forEach { assertEquals(32.4f, it.largo, 0.01f) }
        // Y los de la hoja, entre las caras de su marco: 199 − 2 × 3.8 = 191.4.
        val abajo = tubos.filter { it.nombre == "Parante" && it.y0 < 200f }
        assertTrue(abajo.isNotEmpty())
        abajo.forEach { assertEquals(191.4f, it.largo, 0.01f) }
    }

    @Test
    fun con_el_puente_en_cero_va_a_199_como_en_puertas() {
        assertEquals(199f, RejaCalculo.alturaPuente(puerta.copy(puenteCm = 0f)), 0.01f)
    }

    @Test
    fun sin_sitio_para_la_sobreluz_la_hoja_llega_al_cabezal() {
        // Alto 199: Puerta deja el puente en alto − marco = 195.2 y no hay sobreluz.
        val baja = puerta.copy(altoCm = 199f, puenteCm = 0f)
        assertFalse(RejaCalculo.conSobreluz(baja))
        assertEquals(195.2f, RejaCalculo.alturaPuente(baja), 0.01f)
        assertTrue(RejaCalculo.todos(baja).none { it.nombre == "Puente" })
        assertEquals(195.2f - 1f, RejaCalculo.hoja(baja).altoCm, 0.01f)
    }

    @Test
    fun la_fija_sigue_igual() {
        val fija = puerta.copy(tipo = TipoReja.FIJA)
        assertEquals(RejaCalculo.marco(fija) + RejaCalculo.interior(fija), RejaCalculo.todos(fija))
    }

    /** La mocheta con su propio diseño, aparte de la hoja (pedido del usuario, 2026-10-10). */
    @Test
    fun la_mocheta_tiene_su_propio_diseno() {
        // Hoja de barrotes verticales; mocheta en cuadrícula de paso 10: lleva travesaños, la hoja no.
        val r = puerta.copy(mocheta = DisenoMocheta(ModeloReja.CUADRICULA, pasoCm = 10f))
        val tubos = RejaCalculo.todos(r)
        assertTrue("la mocheta en cuadrícula lleva travesaños", tubos.any { it.nombre == "Travesaño mocheta" })
        assertTrue("la hoja de barrotes sin intermedios no lleva travesaños", tubos.none { it.nombre == "Travesaño" })
        // 82.4 de ancho libre en tramos de 10 como mucho: 9 tramos, 8 parantes de 32.4 (el alto libre).
        val parantes = tubos.filter { it.nombre == "Parante mocheta" }
        assertEquals(8, parantes.size)
        parantes.forEach { assertEquals(32.4f, it.largo, 0.01f) }
        // 32.4 en tramos de 10: 4 tramos, 3 travesaños por cada uno de los 9 tramos de ancho.
        assertEquals(27, tubos.count { it.nombre == "Travesaño mocheta" })
    }

    @Test
    fun la_mocheta_sin_relleno_no_lleva_tubos() {
        val tubos = RejaCalculo.todos(puerta.copy(mocheta = DisenoMocheta(null)))
        assertTrue(tubos.none { it.nombre.endsWith("mocheta") })
        // El puente sigue: la mocheta está, solo que vacía.
        assertEquals(listOf("82.4"), largos(tubos, "Puente"))
    }
}
