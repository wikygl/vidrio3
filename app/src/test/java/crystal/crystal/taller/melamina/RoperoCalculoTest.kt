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
 * 240 - 10 - 3.6 = 226.4; fondo del armazón 59.7. Las piezas van con el tapacanto
 * DESCONTADO: 0.45 mm por canto de dentro y 3 mm por canto de fuera (puertas, zócalo).
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
        // El zócalo va delante, de medio lateral a medio lateral (238.2), y de alto 10 - 0.3 de
        // canto - 0.15 de gruña = 9.55 → 9.5 al medio centímetro hacia abajo.
        assertEquals(95, pieza(m, "Zócalo")!!.altoMm)
        assertEquals(2382, pieza(m, "Zócalo")!!.anchoMm)
        assertEquals(6.5f, RoperoCalculo.altoZocalo(base.copy(zocaloCm = 7f)), 0.01f)
        // El fondo partido en la división: cada trozo lleva medio espesor de división y su lateral,
        // y entre los dos suman el ancho entero (240).
        val fondos = m.piezas.filter { it.nombre == "Fondo" }
        assertEquals(MaterialPlancha.NORDEX_3, fondos.first().material)
        assertEquals(2400, fondos.sumOf { it.anchoMm * it.cantidad })
        assertEquals(1200, fondos.first().anchoMm)   // 117.3 + 1.8 + 0.9
        assertEquals(2300, fondos.first().altoMm)
    }

    @Test
    fun entrepanos_y_tubo_de_cada_cuerpo() {
        val m = RoperoCalculo.calcular(base)
        val entrepano = pieza(m, "Entrepaño")!!
        assertEquals(4, entrepano.cantidad)
        assertEquals(1173, entrepano.anchoMm)
        assertEquals(597, entrepano.altoMm)
        // Los soportes de dos colgadores van sumados en una sola fila.
        val dosColgadores = RoperoCalculo.calcular(base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.COLGAR)))
        assertTrue(dosColgadores.lineasDeAccesorios().lines().contains("Soporte de tubo = 4"))
        val tubo = m.accesorios.first { it.nombre == "Tubo colgador" }
        assertEquals(117.3f, tubo.largoCm, 0.01f)
        assertEquals("117 = 1", m.lineasConLargo("Tubo colgador"))
    }

    @Test
    fun puertas_batientes_dos_por_cuerpo_ancho_con_sus_bisagras() {
        val m = RoperoCalculo.calcular(base)
        val puerta = pieza(m, "Puerta")!!
        // Cada cuerpo tapa 117.3 + 1.8 = 119.1: dos hojas de 59.55 - 0.3 = 59.25, del zócalo al
        // techo (229.7); canteadas a la vuelta con 3 mm se cortan 0.6 menos: 58.65 x 229.1.
        assertEquals(4, puerta.cantidad)
        assertEquals(587, puerta.anchoMm)
        assertEquals(2291, puerta.altoMm)
        assertEquals(5 * 4, m.accesorios.first { it.nombre.startsWith("Bisagra") }.cantidad)
        assertEquals(4, m.accesorios.first { it.nombre == "Tirador" }.cantidad)
        assertNull(pieza(m, "Puerta maletero"))
    }

    @Test
    fun con_maletero_las_puertas_se_partan_y_hay_repisa_por_cuerpo() {
        val m = RoperoCalculo.calcular(base.copy(maleteroCm = 40f))
        assertEquals(2, pieza(m, "Repisa maletero")!!.cantidad)
        // Abajo: 240 - 10 - 40 - 1.8 - 0.3 = 187.9, menos 0.6 de canto = 187.3; arriba: 40 + 1.8 - 0.3 - 0.6 = 40.9.
        assertEquals(1873, pieza(m, "Puerta")!!.altoMm)
        assertEquals(409, pieza(m, "Puerta maletero")!!.altoMm)
        assertEquals(4, pieza(m, "Puerta maletero")!!.cantidad)
    }

    @Test
    fun el_maletero_con_sus_compartimentos_va_de_lateral_a_lateral() {
        val r = base.copy(maleteroCm = 40f, maleteroCuerpos = 2)
        assertTrue(r.maleteroPropio)
        val m = RoperoCalculo.calcular(r)
        // Una repisa entera (236.4), una división del maletero de 40 de alto, y la división de
        // abajo solo hasta la repisa: 226.4 - 40 - 1.8 = 184.6 (menos el canto: 184.55 → 1846).
        val repisa = pieza(m, "Repisa maletero")!!
        assertEquals(1, repisa.cantidad)
        assertEquals(2364, repisa.anchoMm)
        assertEquals(1, pieza(m, "División maletero")!!.cantidad)
        assertEquals(400, pieza(m, "División maletero")!!.altoMm)
        assertEquals(1846, pieza(m, "División")!!.altoMm)
        // Los compartimentos del maletero parten el interior en dos de 117.3, y sus puertas van
        // por compartimento (dos hojas cada uno, que pasa de 60).
        val xs = RoperoGeometria.maleterosX(r)
        assertEquals(2, xs.size)
        assertEquals(117.3f, xs[0].second - xs[0].first, 0.05f)
        assertEquals(4, pieza(m, "Puerta maletero")!!.cantidad)
        // Al centro del ancho: con tres cuerpos abajo y dos arriba, la división del maletero cae al medio.
        val tres = base.conCuerposIguales(3).copy(maleteroCm = 40f, maleteroCuerpos = 2)
        assertEquals(120f, RoperoGeometria.maleterosX(tres)[0].second + 0.9f, 0.05f)
    }

    @Test
    fun cajones_a_la_vista_con_frentes_en_el_plano_de_las_puertas() {
        val r = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 3, cajonesALaVista = true))
        val c = r.cuerpos[1]
        // Del zócalo (10) al primer tope (31.8), luego 20 cada uno, y el último hasta media tapa (71.8 + 0.9).
        val frentes = RoperoPuertas.frentesALaVista(r, c, RoperoGeometria.huecoDeCuerpo(r, 1))
        assertEquals(3, frentes.size)
        assertEquals(10f, frentes[0].first, 0.01f)
        assertEquals(31.8f, frentes[0].second, 0.01f)
        assertEquals(72.7f, frentes[2].second, 0.01f)
        val m = RoperoCalculo.calcular(r)
        // Tan anchos como la puerta del cuerpo: 117.3 + 1.8 - 0.3 - 0.6 = 118.2; altos: 21.8, 20 y 20.9 menos 0.3 y 0.6.
        val fs = m.piezas.filter { it.nombre == "Frente cajón" }
        assertEquals(3, fs.sumOf { it.cantidad })
        assertTrue(fs.all { it.anchoMm == 1182 })
        assertEquals(setOf(209, 191, 200), fs.map { it.altoMm }.toSet())
        // La puerta del cuerpo arranca sobre media tapa: 240 - 72.7 - 0.3 - 0.6 = 166.4.
        val (desde, hasta) = RoperoPuertas.puertaBaja(r, r.cuerpos[1], RoperoGeometria.huecoDeCuerpo(r, 1))
        assertEquals(72.7f, desde, 0.01f)
        assertEquals(240f, hasta, 0.01f)
        assertTrue(m.piezas.filter { it.nombre == "Puerta" }.any { it.altoMm == 1664 })
        // Y el otro cuerpo, sin cajones, sigue con la puerta entera.
        assertTrue(m.piezas.filter { it.nombre == "Puerta" }.any { it.altoMm == 2291 })
    }

    @Test
    fun puertas_interiores_dentro_de_cada_hueco_y_zocalo_bajo_el_piso() {
        val r = base.copy(puertasInteriores = true, zocaloDelante = false, maleteroCm = 40f)
        val m = RoperoCalculo.calcular(r)
        // Cada puerta dentro de su hueco de 117.3 (dos hojas de 58.65 - 0.3 - 0.6 = 57.75), del
        // piso (11.8) a la repisa del maletero (196.4): 184.6 - 0.3 - 0.6 = 183.7.
        val puerta = pieza(m, "Puerta")!!
        assertEquals(4, puerta.cantidad)
        assertEquals(578, puerta.anchoMm)
        assertEquals(1837, puerta.altoMm)
        // La del maletero: 40 - 0.3 - 0.6 = 39.1, en el hueco de 117.3.
        assertEquals(391, pieza(m, "Puerta maletero")!!.altoMm)
        assertEquals(578, pieza(m, "Puerta maletero")!!.anchoMm)
        // El zócalo metido bajo el piso: entre laterales (236.4) y de su alto, sin canto.
        assertEquals(2364, pieza(m, "Zócalo")!!.anchoMm)
        assertEquals(100, pieza(m, "Zócalo")!!.altoMm)
        // Con cajones a la vista e interiores: los frentes en el hueco (117.3 - 0.3 - 0.6) y del piso al tope de los cajones.
        val conCajones = r.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 2, cajonesALaVista = true))
        val frentes = RoperoPuertas.frentesALaVista(conCajones, conCajones.cuerpos[1], RoperoGeometria.huecoDeCuerpo(conCajones, 1))
        assertEquals(11.8f, frentes[0].first, 0.01f)
        assertEquals(51.8f, frentes[1].second, 0.01f)
        assertEquals(1164, pieza(RoperoCalculo.calcular(conCajones), "Frente cajón")!!.anchoMm)
        assertEquals(51.8f + 1.8f, RoperoPuertas.puertaBaja(conCajones, conCajones.cuerpos[1], RoperoGeometria.huecoDeCuerpo(conCajones, 1)).first, 0.01f)
    }

    @Test
    fun los_cajones_traen_frente_caja_fondo_y_rieles() {
        val conCajones = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 3))
        val m = RoperoCalculo.calcular(conCajones)
        val frente = pieza(m, "Frente cajón")!!
        assertEquals(3, frente.cantidad)
        assertEquals(1168, frente.anchoMm)   // 117.3 - 0.4 - 0.09 de canto fino
        assertEquals(195, frente.altoMm)     // 20 - 0.4 - 0.09
        val lateral = pieza(m, "Lateral cajón")!!
        assertEquals(6, lateral.cantidad)
        assertEquals(547, lateral.anchoMm)   // fondo útil 59.7 menos 5
        assertEquals(160, lateral.altoMm)    // 20 - 4
        val fondoCajon = pieza(m, "Fondo cajón")!!
        assertEquals(MaterialPlancha.NORDEX_3, fondoCajon.material)
        assertEquals(1111, fondoCajon.anchoMm)  // 117.3 - 2.6 de rieles - 3.6 (dos melaminas, por ser cajón interior)
        // A la vista, la caja solo pierde los rieles.
        val aLaVista = RoperoCalculo.calcular(conCajones.conCuerpo(1, conCajones.cuerpos[1].copy(cajonesALaVista = true)))
        assertEquals(1147, pieza(aLaVista, "Fondo cajón")!!.anchoMm)
        val riel = m.accesorios.first { it.nombre.startsWith("Riel de cajón") }
        assertEquals("Riel de cajón 50 cm (par)", riel.nombre)
        assertEquals(3, riel.cantidad)
    }

    @Test
    fun corredizas_dos_hojas_montadas_y_fondo_util_menor() {
        val m = RoperoCalculo.calcular(base.copy(puertas = TipoPuertas.CORREDIZAS))
        val hoja = pieza(m, "Hoja corrediza")!!
        assertEquals(2, hoja.cantidad)
        assertEquals(1201, hoja.anchoMm)   // (236.4 + 5) / 2 - 0.6 de canto grueso
        assertEquals(2223, hoja.altoMm)    // 226.4 - 3.5 - 0.6
        // Los entrepaños pierden el carril de las hojas: 59.7 - 8.
        assertEquals(517, pieza(m, "Entrepaño")!!.altoMm)
        assertEquals("236 = 2", m.lineasConLargo("Riel corredizo"))
        assertEquals(3, RoperoPuertas.hojasCorredizasPorAncho(300f))
    }

    @Test
    fun el_techo_de_mas_de_244_se_parte_en_el_centro_de_una_division() {
        // 300 de ancho, tres cuerpos de 97.6: cortes posibles en 98.5 y 197.9 (interior 296.4).
        val ancho = base.conHueco(300f, 240f, 60f).conCuerposIguales(3)
        val tramos = RoperoCalculo.tramosDeAncho(ancho)
        assertEquals(2, tramos.size)
        assertEquals(197.9f, tramos[0], 0.05f)
        assertEquals(98.5f, tramos[1], 0.05f)
        val m = RoperoCalculo.calcular(ancho)
        assertEquals(2, m.piezas.count { it.nombre == "Techo" })
        assertTrue(m.piezas.filter { it.nombre == "Techo" }.all { it.anchoMm <= 2440 })
        // Y el de 240 sigue entero.
        assertEquals(listOf(236.4f), RoperoCalculo.tramosDeAncho(base))
    }

    @Test
    fun planchas_tapacanto_y_lineas_para_archivar() {
        val m = RoperoCalculo.calcular(base)
        // El interior en blanco y lo visible (las cuatro puertas y el zócalo) en la de color.
        val melamina = m.planchasEstimadas[MaterialPlancha.MELAMINA_18]!!
        assertTrue("planchas de melamina blanca fuera de rango: $melamina", melamina in 3..5)
        assertEquals(2, m.planchasEstimadas[MaterialPlancha.MELAMINA_18_COLOR])
        assertEquals(4, m.piezasDe(MaterialPlancha.MELAMINA_18_COLOR).first { it.nombre == "Puerta" }.cantidad)
        assertEquals(MaterialPlancha.MELAMINA_18_COLOR, m.piezas.first { it.nombre == "Zócalo" }.material)
        // Todo del mismo color: una sola melamina.
        val unColor = RoperoCalculo.calcular(base.copy(interiorBlanco = false))
        assertTrue(unColor.piezas.none { it.material == MaterialPlancha.MELAMINA_18_COLOR })
        // El fondo de 240 x 230 no cabe en una plancha de 244 x 183: va partido en dos trozos de 119 x 230.
        assertEquals(2, pieza(m, "Fondo")!!.cantidad)
        assertEquals(2, m.planchasEstimadas[MaterialPlancha.NORDEX_3])
        // El fino por dentro (laterales, techo, piso, división, entrepaños: unos 18 m) y el
        // grueso por fuera (cuatro puertas a la vuelta y el zócalo: unos 25 m).
        assertTrue(m.tapacantoTotalM in 15f..25f)
        val grueso = m.tapacantoPuertas.entries.sumOf { (l, n) -> l * n } / 100f
        assertTrue("tapacanto de puertas: $grueso", grueso in 20f..30f)
        assertTrue(m.lineasDeTapacantoPuertas().lines().contains("229 = 8"))
        val lineas = m.lineasDePiezas(MaterialPlancha.MELAMINA_18).lines()
        assertTrue(lineas.contains("59.7x240 = 2"))
        assertTrue(m.lineasDePiezas(MaterialPlancha.MELAMINA_18_COLOR).lines().contains("58.7x229.1 = 4"))
        assertNotNull(m.lineasDeTapacanto().lines().firstOrNull { it == "240 = 2" })
        assertTrue(m.referencias.contains("Ropero empotrado 240 x 240 x 60"))
    }

}
