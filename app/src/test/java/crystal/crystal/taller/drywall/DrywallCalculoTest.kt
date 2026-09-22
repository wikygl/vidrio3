package crystal.crystal.taller.drywall

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** El tabique de prueba: 3 x 2.4 m, parantes de 89 cada 40.6, yeso 1/2", dos caras. */
class DrywallCalculoTest {

    private val tabique = Drywall(largoM = 3f, altoM = 2.4f)

    private fun MaterialesDrywall.cantidad(nombreEmpiezaPor: String): Float =
        (perfiles + fijaciones + acabado + planchas).first { it.nombre.startsWith(nombreEmpiezaPor) }.cantidad

    @Test
    fun el_tabique_saca_planchas_parantes_rieles_y_tornillos() {
        val m = DrywallCalculo.calcular(tabique)
        // 7.2 m² x 2 caras = 14.4 m²; / 2.98 x 1.10 = 5.3 → 6 planchas.
        assertEquals(6f, m.planchas.cantidad, 0.01f)
        // floor(300 / 40.6) + 1 = 8 parantes, de un tramo (2.4 < 3.05).
        assertEquals(8f, m.cantidad("Parante 89"), 0.01f)
        // Rieles: 2 x 3 = 6 m → 2 de 3.05.
        assertEquals(2f, m.cantidad("Riel 89"), 0.01f)
        assertEquals(180f, m.cantidad("Tornillo drywall"), 0.01f)     // 30 x 6
        assertEquals(32f, m.cantidad("Tornillo wafer"), 0.01f)        // 4 x 8
        assertEquals(10f, m.cantidad("Anclaje"), 0.01f)               // 6 m / 0.6
        // Cinta: 2 juntas verticales de 2.4 por cara = 9.6 m → 1 rollo. Masilla: 14.4 x 1.12 = 16.1 kg.
        assertEquals(1f, m.cantidad("Cinta"), 0.01f)
        assertEquals(14.4f * 28f / 25f, m.cantidad("Masilla"), 0.05f)
        assertTrue(m.acabado.none { it.nombre.startsWith("Esquinero") || it.nombre.startsWith("Lana") })
        assertTrue(m.referencias.startsWith("Tabique (dos caras): 3 x 2.4 m = 7.2 m²"))
    }

    @Test
    fun el_forro_es_una_cara_y_los_vanos_descuentan_plancha_y_suman_refuerzos() {
        val forro = tabique.copy(tipo = TipoDrywall.FORRO, vanosM2 = 1.6f, vanos = 1)
        val m = DrywallCalculo.calcular(forro)
        // (7.2 - 1.6) x 1 cara = 5.6 m² x 1.1 / 2.98 = 2.07 → 3 planchas.
        assertEquals(3f, m.planchas.cantidad, 0.01f)
        // 8 posiciones + 2 refuerzos del vano.
        assertEquals(10f, m.cantidad("Parante 89"), 0.01f)
        // Rieles: 6 m + 1 m de dintel = 7 m → 3 de 3.05.
        assertEquals(3f, m.cantidad("Riel 89"), 0.01f)
        assertTrue(m.referencias.contains("menos 1.6 m² de 1 vano"))
    }

    @Test
    fun alto_mayor_de_3_05_empalma_parantes_y_suma_junta_horizontal() {
        val alto = tabique.copy(altoM = 3.2f, separacionCm = 61f)
        val m = DrywallCalculo.calcular(alto)
        // floor(300 / 61) + 1 = 5 posiciones x 2 tramos = 10 parantes.
        assertEquals(10f, m.cantidad("Parante 89"), 0.01f)
        // Cinta: 2 verticales x 3.2 + 1 horizontal x 3 = 9.4 m por cara, 18.8 m → 1 rollo.
        assertEquals(1f, m.cantidad("Cinta"), 0.01f)
        assertTrue(m.acabado.first { it.nombre.startsWith("Cinta") }.nota.startsWith("18.8 m"))
    }

    @Test
    fun el_cielo_raso_lleva_angulo_portantes_cruzados_y_alambre() {
        val cielo = Drywall(tipo = TipoDrywall.CIELO_RASO, largoM = 4f, altoM = 3f, plancha = PlanchaDrywall.YESO_9)
        val m = DrywallCalculo.calcular(cielo)
        // 12 m² x 1.1 / 2.98 = 4.4 → 5 planchas de 3/8.
        assertEquals(5f, m.planchas.cantidad, 0.01f)
        assertTrue(m.planchas.nombre.contains("3/8"))
        // Perímetro 14 m → 5 ángulos. Portantes: floor(3 / 1.22) + 1 = 3 de 4 m = 12 m → 4 rieles.
        assertEquals(5f, m.cantidad("Ángulo"), 0.01f)
        assertEquals(4f, m.cantidad("Riel 89"), 0.01f)
        // Cruzados: floor(4 / 0.61) + 1 = 7 de 3 m = 21 m → 7 parantes.
        assertEquals(7f, m.cantidad("Parante 89"), 0.01f)
        // Colgadores: ceil(4 / 1.22) x ceil(3 / 1.22) = 4 x 3 = 12 → 12 m de alambre y 12 clavos.
        assertEquals(12f, m.cantidad("Alambre"), 0.01f)
        assertEquals(12f, m.cantidad("Clavo con ojal"), 0.01f)
        assertTrue(m.fijaciones.none { it.nombre.startsWith("Anclaje") })
    }

    @Test
    fun fibrocemento_esquinas_y_lana() {
        val d = tabique.copy(plancha = PlanchaDrywall.FIBRO_8, esquinas = 2, conLana = true)
        val m = DrywallCalculo.calcular(d)
        assertTrue(m.fijaciones.any { it.nombre.startsWith("Tornillo punta broca") })
        assertEquals(2f, m.cantidad("Esquinero"), 0.01f)     // 2 esquinas x ceil(2.4 / 2.44)
        assertEquals(1f, m.cantidad("Lana"), 0.01f)          // 7.2 m² en un rollo de 15
        assertEquals("6 und", m.planchas.linea)
        assertEquals("16.13 kg", m.acabado.first { it.nombre.startsWith("Masilla") }.linea)
    }
}
