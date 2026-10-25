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
        // 1 al arranque + ceil(300 / 40.6) = 9 parantes, de un tramo (2.4 < 3.00). Antes 8: floor(300 / 40.6) + 1
        // dejaba fuera el del final, que el dibujo sí ponía (corregido el 2026-10-11).
        assertEquals(9f, m.cantidad("Parante 89"), 0.01f)
        // Rieles: 2 x 3 = 6 m → 2 de 3.00.
        assertEquals(2f, m.cantidad("Riel 89"), 0.01f)
        // Cada 30 cm en cada perfil que toca la pieza (el usuario, 2026-10-10; antes 30 por plancha = 180):
        // por cara, 2 tiras de 1.22 con 4 perfiles y 1 de 0.56 con 3, 9 tornillos cada uno = 99.
        assertEquals(198f, m.cantidad("Tornillo drywall"), 0.01f)
        assertEquals(36f, m.cantidad("Tornillo wafer"), 0.01f)        // 4 x 9
        assertEquals(10f, m.cantidad("Anclaje"), 0.01f)               // 6 m / 0.6
        // Cinta: 2 juntas verticales de 2.4 y la unión con el muro en las 2 puntas, por cara = 19.2 m
        // → 1 rollo. Masilla: 14.4 x 1.12 = 16.1 kg.
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
        // 9 posiciones + 2 refuerzos del vano.
        assertEquals(11f, m.cantidad("Parante 89"), 0.01f)
        // Rieles: 6 m + 1 m de dintel = 7 m → 3 de 3.00.
        assertEquals(3f, m.cantidad("Riel 89"), 0.01f)
        assertTrue(m.referencias.contains("menos 1.6 m² de 1 vano"))
    }

    @Test
    fun alto_mayor_de_3_05_empalma_parantes_y_suma_junta_horizontal() {
        val alto = tabique.copy(altoM = 3.2f, separacionCm = 61f)
        val m = DrywallCalculo.calcular(alto)
        // 1 + ceil(300 / 61) = 6 posiciones, cada una con su varilla entera; los 6 empalmes de
        // 0.15 + 0.30 de traslape (2.7 m) salen de 1 varilla más: 7. Antes 10: cada empalme pedía
        // una varilla entera (2026-10-11).
        assertEquals(7f, m.cantidad("Parante 89"), 0.01f)
        // Cinta: 2 verticales x 3.2 + 1 horizontal x 3 + la unión con el muro en las 2 puntas
        // (2 x 3.2) = 15.8 m por cara, 31.6 m → 1 rollo. Antes 18.8: sin la unión con el muro.
        assertEquals(1f, m.cantidad("Cinta"), 0.01f)
        assertTrue(m.acabado.first { it.nombre.startsWith("Cinta") }.nota.startsWith("31.6 m"))
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
        assertEquals(2f, m.cantidad("Ángulo esquinero"), 0.01f)     // 2 esquinas x ceil(2.4 / 2.44)
        assertEquals(1f, m.cantidad("Lana"), 0.01f)          // 7.2 m² en un rollo de 15
        assertEquals("6 und", m.planchas.linea)
        assertEquals("16.13 kg", m.acabado.first { it.nombre.startsWith("Masilla") }.linea)
    }
}
