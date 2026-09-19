package crystal.crystal.taller.melamina

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El ropero de prueba: hueco 240 x 240 x 60, melamina 18, zócalo 10, dos cuerpos iguales
 * (colgador y entrepaños con 4 repisas), puertas batientes y fondo de nordex.
 *
 * Interior: ancho 240 - 3.6 = 236.4; cada cuerpo (236.4 - 1.8) / 2 = 117.3; alto interior
 * 240 - 10 - 3.6 = 226.4; fondo del armazón 59.7.
 */
class RoperoCalculoTest {

    private val base = Ropero(
        anchoCm = 240f, altoCm = 240f, fondoCm = 60f, espesorMm = 18, zocaloCm = 10f,
        cuerpos = listOf(Cuerpo(tipo = TipoCuerpo.COLGAR), Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 4)),
        puertas = TipoPuertas.BATIENTES, conFondo = true
    ).conCuerposIguales(2)

    private fun pieza(m: MaterialesRopero, nombre: String) = m.piezas.firstOrNull { it.nombre == nombre }

    @Test
    fun los_cuerpos_se_reparten_el_ancho_interior_con_las_divisiones() {
        assertEquals(236.4f, base.anchoInteriorCm, 0.01f)
        assertEquals(117.3f, base.cuerpos[0].anchoCm, 0.01f)
        assertEquals(117.3f, base.cuerpos[1].anchoCm, 0.01f)
        val ajustado = base.conAnchoDeCuerpo(0, 80f)
        assertEquals(80f, ajustado.cuerpos[0].anchoCm, 0.01f)
        assertEquals(236.4f - 1.8f - 80f, ajustado.cuerpos[1].anchoCm, 0.01f)
    }

    @Test
    fun el_armazon_sale_con_sus_medidas() {
        val m = RoperoCalculo.calcular(base)
        val lateral = pieza(m, "Lateral")!!
        assertEquals(2, lateral.cantidad)
        assertEquals(597, lateral.anchoMm)     // fondo menos el nordex
        assertEquals(2400, lateral.altoMm)
        val techo = pieza(m, "Techo")!!
        assertEquals(2364, techo.anchoMm)
        assertEquals(597, techo.altoMm)
        assertEquals(2364, pieza(m, "Piso")!!.anchoMm)
        val division = pieza(m, "División")!!
        assertEquals(1, division.cantidad)
        assertEquals(2264, division.altoMm)
        assertEquals(100, pieza(m, "Zócalo")!!.altoMm)
        val fondo = pieza(m, "Fondo")!!
        assertEquals(MaterialPlancha.NORDEX_3, fondo.material)
        assertEquals(1191, fondo.anchoMm)   // 117.3 + 1.8: hasta la mitad de la división
        assertEquals(2300, fondo.altoMm)
    }

    @Test
    fun entrepanos_y_tubo_de_cada_cuerpo() {
        val m = RoperoCalculo.calcular(base)
        val entrepano = pieza(m, "Entrepaño")!!
        assertEquals(4, entrepano.cantidad)
        assertEquals(1173, entrepano.anchoMm)
        assertEquals(597, entrepano.altoMm)
        val tubo = m.accesorios.first { it.nombre == "Tubo colgador" }
        assertEquals(117.3f, tubo.largoCm, 0.01f)
        assertEquals("117 = 1", m.lineasConLargo("Tubo colgador"))
    }

    @Test
    fun puertas_batientes_dos_por_cuerpo_ancho_con_sus_bisagras() {
        val m = RoperoCalculo.calcular(base)
        val puerta = pieza(m, "Puerta")!!
        // Cada cuerpo tapa 117.3 + 1.8 = 119.1: dos hojas de 59.55 - 0.3, del zócalo al techo.
        assertEquals(4, puerta.cantidad)
        assertEquals(593, puerta.anchoMm)
        assertEquals(2297, puerta.altoMm)
        assertEquals(5 * 4, m.accesorios.first { it.nombre.startsWith("Bisagra") }.cantidad)
        assertEquals(4, m.accesorios.first { it.nombre == "Tirador" }.cantidad)
        assertNull(pieza(m, "Puerta maletero"))
    }

    @Test
    fun con_maletero_las_puertas_se_partan_y_hay_repisa_por_cuerpo() {
        val m = RoperoCalculo.calcular(base.copy(maleteroCm = 40f))
        assertEquals(2, pieza(m, "Repisa maletero")!!.cantidad)
        // Abajo: 240 - 10 - 40 - 1.8 - 0.3 = 187.9; arriba: 40 + 1.8 - 0.3 = 41.5.
        assertEquals(1879, pieza(m, "Puerta")!!.altoMm)
        assertEquals(415, pieza(m, "Puerta maletero")!!.altoMm)
        assertEquals(4, pieza(m, "Puerta maletero")!!.cantidad)
    }

    @Test
    fun los_cajones_traen_frente_caja_fondo_y_rieles() {
        val conCajones = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 3))
        val m = RoperoCalculo.calcular(conCajones)
        val frente = pieza(m, "Frente cajón")!!
        assertEquals(3, frente.cantidad)
        assertEquals(1169, frente.anchoMm)
        assertEquals(196, frente.altoMm)
        val lateral = pieza(m, "Lateral cajón")!!
        assertEquals(6, lateral.cantidad)
        assertEquals(547, lateral.anchoMm)   // fondo útil 59.7 menos 5
        assertEquals(160, lateral.altoMm)    // 20 - 4
        val fondoCajon = pieza(m, "Fondo cajón")!!
        assertEquals(MaterialPlancha.NORDEX_3, fondoCajon.material)
        assertEquals(1147, fondoCajon.anchoMm)  // 117.3 - 2.6
        val riel = m.accesorios.first { it.nombre.startsWith("Riel de cajón") }
        assertEquals("Riel de cajón 50 cm (par)", riel.nombre)
        assertEquals(3, riel.cantidad)
    }

    @Test
    fun corredizas_dos_hojas_montadas_y_fondo_util_menor() {
        val m = RoperoCalculo.calcular(base.copy(puertas = TipoPuertas.CORREDIZAS))
        val hoja = pieza(m, "Hoja corrediza")!!
        assertEquals(2, hoja.cantidad)
        assertEquals(1207, hoja.anchoMm)   // (236.4 + 5) / 2
        assertEquals(2229, hoja.altoMm)    // 226.4 - 3.5
        // Los entrepaños pierden el carril de las hojas: 59.7 - 8.
        assertEquals(517, pieza(m, "Entrepaño")!!.altoMm)
        assertEquals("236 = 2", m.lineasConLargo("Riel corredizo"))
        assertEquals(3, RoperoCalculo.hojasCorredizas(300f))
    }

    @Test
    fun planchas_tapacanto_y_lineas_para_archivar() {
        val m = RoperoCalculo.calcular(base)
        val melamina = m.planchasEstimadas[MaterialPlancha.MELAMINA_18]!!
        assertTrue("planchas de melamina fuera de rango: $melamina", melamina in 4..6)
        // El fondo de 240 x 230 no cabe en una plancha de 244 x 183: va partido en dos trozos de 119 x 230.
        assertEquals(2, pieza(m, "Fondo")!!.cantidad)
        assertEquals(2, m.planchasEstimadas[MaterialPlancha.NORDEX_3])
        assertTrue(m.tapacantoTotalM > 30f)
        val lineas = m.lineasDePiezas(MaterialPlancha.MELAMINA_18).lines()
        assertTrue(lineas.contains("59.7x240 = 2"))
        assertTrue(lineas.contains("59.3x229.7 = 4"))
        assertNotNull(m.lineasDeTapacanto().lines().firstOrNull { it == "240 = 2" })
        assertTrue(m.referencias.contains("Ropero empotrado 240 x 240 x 60"))
    }

}
