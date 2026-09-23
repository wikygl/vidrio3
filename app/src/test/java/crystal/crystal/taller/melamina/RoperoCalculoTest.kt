package crystal.crystal.taller.melamina

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El ropero de prueba: hueco 240 x 240 x 60, melamina 18, zócalo 11.8 (del suelo a la cara de arriba del piso: el piso, de 10 a 11.8, es parte de él), dos cuerpos iguales
 * (colgador y entrepaños con 4 repisas), puertas batientes y fondo de nordex.
 *
 * Interior: ancho 240 - 3.6 = 236.4; cada cuerpo (236.4 - 1.8) / 2 = 117.3; alto interior
 * 240 - 10 - 3.6 = 226.4; fondo del armazón 60 - 0.3 de nordex - 1.8 de las batientes, que van
 * encima = 57.9 (con corredizas o batientes interiores, 59.7). Las piezas van con el tapacanto
 * DESCONTADO: 0.45 mm por canto de dentro y 3 mm por canto de fuera (puertas, zócalo).
 */
class RoperoCalculoTest {

    private val base = Ropero(
        anchoCm = 240f, altoCm = 240f, fondoCm = 60f, espesorMm = 18, zocaloCm = 11.8f,
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
        assertEquals(579, lateral.anchoMm)     // fondo menos el nordex y la puerta de delante
        assertEquals(2400, lateral.altoMm)
        // Con las puertas por dentro (interiores o corredizas) el armazón llega hasta delante: 59.7.
        assertEquals(597, pieza(RoperoCalculo.calcular(base.copy(puertasInteriores = true)), "Lateral")!!.anchoMm)
        assertEquals(597, pieza(RoperoCalculo.calcular(base.copy(puertas = TipoPuertas.CORREDIZAS)), "Lateral")!!.anchoMm)
        // 236.4 de piso pasaría de 180: la división del medio es pasante (del suelo arriba, 240) y
        // piso y techo van en dos trozos de 117.3 que llegan a su cara.
        val techo = pieza(m, "Techo")!!
        assertEquals(2, techo.cantidad)
        assertEquals(1173, techo.anchoMm)
        assertEquals(579, techo.altoMm)
        assertEquals(1173, pieza(m, "Piso")!!.anchoMm)
        assertNull(pieza(m, "División"))
        assertEquals(2400, pieza(m, "División pasante")!!.altoMm)
        // Uno de 170 no la necesita: piso entero y la división entre piso y techo.
        val angosto = RoperoCalculo.calcular(base.conHueco(170f, 240f, 60f))
        assertEquals(1664, pieza(angosto, "Piso")!!.anchoMm)
        assertEquals(2264, pieza(angosto, "División")!!.altoMm)
        // El zócalo va delante, de punta a punta como las puertas (240, en dos trozos de 120 que se
        // atornillan al canto de la pasante), y llega a medio piso (10.9): de alto 10.9 - 0.3 de
        // canto - 0.15 de gruña = 10.45 → 10 al medio centímetro hacia abajo.
        assertEquals(100, pieza(m, "Zócalo")!!.altoMm)
        assertEquals(1200, pieza(m, "Zócalo")!!.anchoMm)
        assertEquals(2, pieza(m, "Zócalo")!!.cantidad)
        assertEquals(5.5f, RoperoCalculo.altoZocalo(base.copy(zocaloCm = 7f)), 0.01f)   // 7 - 0.9 - 0.3 - 0.15 = 5.65
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
        assertEquals(579, entrepano.altoMm)
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
        // Cada cuerpo tapa 117.3 + 0.9 de división + 1.8 del lateral entero = 120: dos hojas de 60 - 0.3 = 59.7, del zócalo al
        // techo: de medio piso (10.9; la otra mitad la tapa el zócalo, y en ella topa la puerta) a 240
        // = 229.1 - 0.3 = 228.8; canteadas a la vuelta con 3 mm se cortan 0.6 menos: 59.1 x 228.2.
        assertEquals(4, puerta.cantidad)
        assertEquals(591, puerta.anchoMm)
        assertEquals(2282, puerta.altoMm)
        // 228.8 de alto (más de 1.60): cuatro bisagras por hoja (hasta 1 m, dos; de 1 a 1.60, tres).
        assertEquals(4 * 4, m.accesorios.first { it.nombre.startsWith("Bisagra") }.cantidad)
        assertEquals(4, m.accesorios.first { it.nombre == "Tirador" }.cantidad)
        assertNull(pieza(m, "Puerta maletero"))
    }

    @Test
    fun con_maletero_las_puertas_se_partan_y_hay_repisa_por_cuerpo() {
        val m = RoperoCalculo.calcular(base.copy(maleteroCm = 40f))
        assertEquals(2, pieza(m, "Repisa maletero")!!.cantidad)
        // Abajo: de medio piso (10.9) a la cara de arriba de la repisa (198.2) = 187.3 - 0.3 = 187, menos 0.6 de canto = 186.4; arriba: 40 + 1.8 - 0.3 - 0.6 = 40.9.
        assertEquals(1864, pieza(m, "Puerta")!!.altoMm)
        assertEquals(409, pieza(m, "Puerta maletero")!!.altoMm)
        assertEquals(4, pieza(m, "Puerta maletero")!!.cantidad)
    }

    @Test
    fun el_maletero_con_sus_compartimentos_va_de_lateral_a_lateral() {
        val r = base.copy(maleteroCm = 40f, maleteroCuerpos = 2)
        assertTrue(r.maleteroPropio)
        val m = RoperoCalculo.calcular(r)
        // La repisa va de lateral a lateral, partida en la pasante del medio (dos de 117.3); la
        // división del maletero cae justo en ella, así que la pasante hace de las dos y no hay
        // divisiones sueltas.
        val repisa = pieza(m, "Repisa maletero")!!
        assertEquals(2, repisa.cantidad)
        assertEquals(1173, repisa.anchoMm)
        assertNull(pieza(m, "División maletero"))
        assertNull(pieza(m, "División"))
        assertEquals(1, pieza(m, "División pasante")!!.cantidad)
        // Los compartimentos del maletero parten el interior en dos de 117.3, y sus puertas van
        // por compartimento (dos hojas cada uno, que pasa de 60).
        val xs = RoperoGeometria.maleterosX(r)
        assertEquals(2, xs.size)
        assertEquals(117.3f, xs[0].second - xs[0].first, 0.05f)
        assertEquals(4, pieza(m, "Puerta maletero")!!.cantidad)
        // En uno de 170 (sin pasante), la división de abajo llega solo hasta la repisa:
        // 240 - 11.8 - 1.8 - 40 - 1.8 = 184.6 (menos el canto: 184.55 → 1846), y el maletero lleva la suya.
        val angosto = RoperoCalculo.calcular(base.conHueco(170f, 240f, 60f).copy(maleteroCm = 40f, maleteroCuerpos = 2))
        assertEquals(1846, pieza(angosto, "División")!!.altoMm)
        assertEquals(400, pieza(angosto, "División maletero")!!.altoMm)
        // Con tres cuerpos abajo y tres arriba, la pasante cae en una de las del maletero.
        val tres = base.conCuerposIguales(3).copy(maleteroCm = 40f, maleteroCuerpos = 3)
        assertEquals(3, RoperoGeometria.maleterosX(tres).size)
        assertEquals(1, pieza(RoperoCalculo.calcular(tres), "División maletero")!!.cantidad)
    }

    @Test
    fun cajones_a_la_vista_con_frentes_en_el_plano_de_las_puertas() {
        val r = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 3, cajonesALaVista = true))
        val c = r.cuerpos[1]
        // De medio piso (10.9; la otra mitad la tapa el zócalo) a media tapa (71.8 + 0.9 =
        // 72.7): 61.8 repartidos en tres frentes iguales de 20.6.
        val frentes = RoperoPuertas.frentesALaVista(r, c, RoperoGeometria.huecoDeCuerpo(r, 1))
        assertEquals(3, frentes.size)
        assertEquals(10.9f, frentes[0].first, 0.01f)
        assertEquals(31.5f, frentes[0].second, 0.01f)
        assertEquals(72.7f, frentes[2].second, 0.01f)
        val m = RoperoCalculo.calcular(r)
        // Tan anchos como la puerta del cuerpo (tapa el lateral entero): 117.3 + 0.9 + 1.8 - 0.3 - 0.6 = 119.1; altos: los tres 20.6 - 0.3 - 0.6 = 19.7.
        val fs = m.piezas.filter { it.nombre == "Frente cajón" }
        assertEquals(3, fs.sumOf { it.cantidad })
        assertTrue(fs.all { it.anchoMm == 1191 })
        assertEquals(setOf(197), fs.map { it.altoMm }.toSet())
        // El caso del usuario, un casillero de 80 con cuatro cajones: los frentes van de medio piso
        // (10.9) a media tapa (92.7), 81.8 / 4 - 0.9 = 19.55; el último acaba en 80.75 sobre el piso.
        val cuatro = RoperoCalculo.calcular(base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 4, cajonesALaVista = true)))
        val f4 = cuatro.piezas.filter { it.nombre == "Frente cajón" }
        assertEquals(4, f4.sumOf { it.cantidad })
        assertEquals(setOf(196), f4.map { it.altoMm }.toSet())
        // La puerta del cuerpo arranca sobre media tapa: 240 - 72.7 - 0.3 - 0.6 = 166.4.
        val (desde, hasta) = RoperoPuertas.puertaBaja(r, r.cuerpos[1], RoperoGeometria.huecoDeCuerpo(r, 1))
        assertEquals(72.7f, desde, 0.01f)
        assertEquals(240f, hasta, 0.01f)
        assertTrue(m.piezas.filter { it.nombre == "Puerta" }.any { it.altoMm == 1664 })
        // Y el otro cuerpo, sin cajones, sigue con la puerta entera.
        assertTrue(m.piezas.filter { it.nombre == "Puerta" }.any { it.altoMm == 2282 })
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
        // El zócalo metido bajo el piso: entre laterales, partido en la pasante (dos de 117.3), y de su alto, sin canto.
        assertEquals(1173, pieza(m, "Zócalo")!!.anchoMm)
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
    fun el_zocalo_llega_a_la_cara_de_arriba_del_piso() {
        // El Rm4 del usuario: 155 x 230, zócalo 7, un cuerpo con puerta entera y otro con cuatro
        // cajones de 18 a la vista en un casillero de 80 y puerta encima. El piso es parte del
        // zócalo: va de 5.2 a 7, y el casillero arranca en 7.
        val r = base.copy(zocaloCm = 7f).conHueco(155f, 230f, 60f).conCuerposIguales(2)
            .let { it.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 4, altosCajonesCm = List(4) { 18f }, altoCajonesFijoCm = 80f, cajonesALaVista = true)) }
        assertEquals(7f, RoperoGeometria.pisoY(r), 0.01f)
        val m = RoperoCalculo.calcular(r)
        val puertas = m.piezas.filter { it.nombre == "Puerta" }.map { it.altoMm }.toSet()
        // La puerta entera, de medio piso (6.1) a 230: 223.9 - 0.3 - 0.6 = 223.
        assertTrue("puertas: $puertas", 2230 in puertas)
        // Los frentes, de 6.1 a media tapa (87.9): 81.8 / 4 - 0.9 = 19.55; el último acaba en 80.75
        // sobre el piso (87.75 del suelo), sin pasar la media tapa menos la media gruña.
        assertEquals(196, pieza(m, "Frente cajón")!!.altoMm)
        val frentes = RoperoPuertas.de(r).hojas.filter { it.clase == ClaseDePuerta.FRENTE_CAJON }
        assertEquals(7f + 80.75f, frentes.maxOf { it.y1 }, 0.01f)
        // La puerta de encima, de media tapa a 230: 142.1 - 0.9 = 141.2.
        assertTrue("puertas: $puertas", 1412 in puertas)
        // El zócalo de delante, de 155, llega a medio piso: 6.1 - 0.3 - 0.15 → 5.5 de melamina.
        assertEquals(55, pieza(m, "Zócalo")!!.altoMm)
        // Metido (puertas por dentro), el zócalo es lo que queda bajo el piso: 7 - 1.8 = 5.2.
        val metido = RoperoCalculo.calcular(r.copy(zocaloDelante = false, puertasInteriores = true))
        assertEquals(52, pieza(metido, "Zócalo")!!.altoMm)
    }

    @Test
    fun las_batientes_de_encima_tapan_el_lateral_entero() {
        // El ropero de 155 del usuario: dos cuerpos de 74.8, dos hojas cada uno. Las cuatro hojas
        // cubren los 155 enteros: 155 / 4 - 0.3 de gruña = 38.45, y con el canto de 3 mm a la
        // vuelta se corta a 37.85 (antes tapaban medio lateral y salían de 37.4).
        val r = base.conHueco(155f, 230f, 60f).conCuerposIguales(2)
        val puerta = pieza(RoperoCalculo.calcular(r), "Puerta")!!
        assertEquals(4, puerta.cantidad)
        assertEquals(379, puerta.anchoMm)
        val hojas = RoperoPuertas.de(r).hojas
        assertEquals(0.15f, hojas.minOf { it.x0 }, 0.01f)
        assertEquals(155f - 0.15f, hojas.maxOf { it.x1 }, 0.01f)
        // Y el zócalo de delante, en su mismo plano, de punta a punta.
        assertEquals(listOf(155f), RoperoCalculo.tramosDeZocalo(r))
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
        assertEquals(529, lateral.anchoMm)   // fondo útil 57.9 menos 5
        assertEquals(200, lateral.altoMm)    // la caja de 20 escrita (- 0.045 de canto fino)
        // El fondo, de MDF o nordex de 3, entra en la ranura de las cuatro piezas de la caja (a 1.8 del canto
        // de abajo): lo de dentro de la caja más 0.7 por lado. Caja interior de 117.3 - 2.6 de
        // rieles - 3.6 = 111.1; por dentro 107.5 → fondo 108.9; de fondo 52.9 - 3.6 + 1.4 = 50.7.
        val fondoCajon = pieza(m, "Fondo cajón")!!
        assertEquals(MaterialPlancha.NORDEX_3, fondoCajon.material)   // MDF o nordex de 3, como el fondo
        assertEquals(1089, fondoCajon.anchoMm)
        assertEquals(507, fondoCajon.altoMm)
        assertEquals(1.8f, pieza(m, "Lateral cajón")!!.ranuraCm, 0.001f)
        assertEquals(1.8f, pieza(m, "Frente y trasera de caja")!!.ranuraCm, 0.001f)
        // Con más de 90 por dentro, un refuerzo de melamina de canto bajo el fondo, al centro.
        val refuerzo = pieza(m, "Refuerzo fondo cajón")!!
        assertEquals(3, refuerzo.cantidad)
        assertEquals(493, refuerzo.anchoMm)
        assertEquals(18, refuerzo.altoMm)
        // A la vista, la caja solo pierde los rieles: 114.7 - 3.6 + 1.4 = 112.5.
        val aLaVista = RoperoCalculo.calcular(conCajones.conCuerpo(1, conCajones.cuerpos[1].copy(cajonesALaVista = true)))
        assertEquals(1125, pieza(aLaVista, "Fondo cajón")!!.anchoMm)
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
    fun con_300_de_ancho_y_tres_cuerpos_van_dos_pasantes() {
        // 300 de ancho, tres cuerpos de 97.6 (interior 296.4): con una sola pasante un trozo
        // pasaría de 180 (el zócalo de 199.7), así que van las dos divisiones del suelo arriba y
        // piso y techo salen en tres de 97.6. Nunca dos piezas juntas a tope.
        val ancho = base.conHueco(300f, 240f, 60f).conCuerposIguales(3)
        assertEquals(listOf(0, 1), RoperoGeometria.divisionesPasantes(ancho))
        val tramos = RoperoCalculo.tramosDeAncho(ancho)
        assertEquals(3, tramos.size)
        tramos.forEach { assertEquals(97.6f, it, 0.05f) }
        val m = RoperoCalculo.calcular(ancho)
        assertEquals(3, m.piezas.filter { it.nombre == "Techo" }.sumOf { it.cantidad })
        assertEquals(2, pieza(m, "División pasante")!!.cantidad)
        assertEquals(2400, pieza(m, "División pasante")!!.altoMm)
        assertNull(pieza(m, "División"))
        // El zócalo de delante se parte en el centro de las pasantes, en cuyo canto se atornilla.
        assertEquals(300f, RoperoCalculo.tramosDeZocalo(ancho).sum(), 0.05f)
        assertTrue(RoperoCalculo.tramosDeZocalo(ancho).all { it <= RoperoGeometria.TRAMO_MAXIMO_CM })
    }

    @Test
    fun la_pasante_va_lo_mas_centrada_posible_y_ningun_trozo_pasa_de_180() {
        // El Rm3 del usuario: 300 x 238, zócalo 7, cinco cuerpos de 58 y 57.6. Antes la pasante
        // caía en la cuarta división: piso y techo de 236.6 y un zócalo de 239.3, que con la veta
        // a lo alto no entran atravesados en los 183 de la plancha. Ahora cae en una de las del
        // medio (el centro, 150, está dentro del cuerpo del medio): trozos de 117.4 y 177.2, y el
        // zócalo de delante en 120.1 y 179.9.
        val anchos = listOf(58f, 57.6f, 58f, 57.6f, 58f)
        val r = base.copy(zocaloCm = 7f).conHueco(300f, 238f, 60f).copy(cuerpos = anchos.map { Cuerpo(anchoCm = it, tipo = TipoCuerpo.ENTREPANOS, entrepanos = 3) })
        val pasantes = RoperoGeometria.divisionesPasantes(r)
        assertEquals(1, pasantes.size)
        assertTrue("pasante en $pasantes", pasantes.single() in 1..2)
        val piso = RoperoCalculo.tramosDeAncho(r).sortedDescending()
        assertEquals(177.2f, piso[0], 0.05f)
        assertEquals(117.4f, piso[1], 0.05f)
        val zocalo = RoperoCalculo.tramosDeZocalo(r)
        assertTrue("zócalo $zocalo", zocalo.all { it <= RoperoGeometria.TRAMO_MAXIMO_CM })
        assertEquals(300f, zocalo.sum(), 0.05f)
        val m = RoperoCalculo.calcular(r)
        for (nombre in listOf("Piso", "Techo", "Zócalo")) {
            assertTrue(nombre, m.piezas.filter { it.nombre == nombre }.all { it.anchoMm <= 1800 })
        }
        // Y uno de 155 no lleva pasante.
        assertTrue(RoperoGeometria.divisionesPasantes(base.conHueco(155f, 230f, 60f).conCuerposIguales(2)).isEmpty())
    }

    @Test
    fun con_maletero_propio_la_pasante_cae_en_una_division_del_maletero() {
        // 300 de ancho, seis cuerpos abajo y el maletero en dos: la pasante va en la tercera
        // división (centro a 150), la misma del maletero. Piso, techo y repisa del maletero en dos
        // de 147.3; divisiones: 4 normales, 1 pasante y ninguna suelta en el maletero.
        val r = base.conHueco(300f, 240f, 60f).conCuerposIguales(6).copy(maleteroCm = 40f, maleteroCuerpos = 2)
        assertEquals(listOf(2), RoperoGeometria.divisionesPasantes(r))
        val m = RoperoCalculo.calcular(r)
        for (nombre in listOf("Piso", "Techo", "Repisa maletero")) {
            assertEquals(nombre, listOf(1473, 1473), m.piezas.filter { it.nombre == nombre }.flatMap { p -> List(p.cantidad) { p.anchoMm } })
        }
        assertEquals(4, pieza(m, "División")!!.cantidad)
        assertEquals(1, pieza(m, "División pasante")!!.cantidad)
        assertNull(pieza(m, "División maletero"))
        assertEquals(2, RoperoGeometria.maleterosX(r).size)
    }

    @Test
    fun planchas_tapacanto_y_lineas_para_archivar() {
        val m = RoperoCalculo.calcular(base)
        // El interior en blanco y lo visible (las cuatro puertas, el zócalo y los dos laterales, que se
        // ven por fuera) en la de color.
        val melamina = m.planchasEstimadas[MaterialPlancha.MELAMINA_18]!!
        assertTrue("planchas de melamina blanca fuera de rango: $melamina", melamina in 2..4)
        val color = m.planchasEstimadas[MaterialPlancha.MELAMINA_18_COLOR]!!
        assertTrue("planchas de melamina de color fuera de rango: $color", color in 3..4)
        assertEquals(4, m.piezasDe(MaterialPlancha.MELAMINA_18_COLOR).first { it.nombre == "Puerta" }.cantidad)
        assertEquals(MaterialPlancha.MELAMINA_18_COLOR, m.piezas.first { it.nombre == "Zócalo" }.material)
        assertTrue(m.piezas.filter { it.nombre == "Lateral" }.all { it.material == MaterialPlancha.MELAMINA_18_COLOR })
        // El zócalo metido también se ve entero desde fuera: de color.
        assertEquals(MaterialPlancha.MELAMINA_18_COLOR, RoperoCalculo.calcular(base.copy(zocaloDelante = false)).piezas.first { it.nombre == "Zócalo" }.material)
        // Todo del mismo color: una sola melamina.
        val unColor = RoperoCalculo.calcular(base.copy(interiorBlanco = false))
        assertTrue(unColor.piezas.none { it.material == MaterialPlancha.MELAMINA_18_COLOR })
        // El fondo de 240 x 230 no cabe en una plancha de 244 x 183: va partido en dos trozos de 119 x 230.
        assertEquals(2, pieza(m, "Fondo")!!.cantidad)
        assertEquals(2, m.planchasEstimadas[MaterialPlancha.NORDEX_3])
        // El fino por dentro (techo, piso, división, entrepaños: unos 13 m), el fino de color de los
        // laterales (su frente y su canto de arriba) y el grueso por fuera (cuatro puertas a la vuelta y el zócalo: unos 25 m).
        assertTrue("tapacanto fino: ${m.tapacantoTotalM}", m.tapacantoTotalM in 10f..20f)
        val grueso = m.tapacantoPuertas.entries.sumOf { (l, n) -> l * n } / 100f
        assertTrue("tapacanto de puertas: $grueso", grueso in 20f..30f)
        assertTrue(m.lineasDeTapacantoPuertas().lines().contains("228 = 8"))
        val lineas = m.lineasDePiezas(MaterialPlancha.MELAMINA_18).lines()
        assertTrue(m.lineasDePiezas(MaterialPlancha.MELAMINA_18_COLOR).lines().contains("57.9x240 = 2"))
        // En la blanca, de ese largo, solo la pasante del medio: sus dos caras quedan por dentro.
        assertTrue(lineas.contains("57.9x240 = 1"))
        assertTrue(m.lineasDePiezas(MaterialPlancha.MELAMINA_18_COLOR).lines().contains("59.1x228.2 = 4"))
        // Los cantos de los laterales van en el fino de color, no en el blanco.
        assertEquals(listOf("240 = 2", "58 = 2"), m.lineasDeTapacantoColor().lines())
        assertTrue(m.lineasDeTapacanto().lines().none { it == "240 = 2" })
        // Todo del mismo color: no hay lista aparte.
        assertTrue(RoperoCalculo.calcular(base.copy(interiorBlanco = false)).tapacantoColor.isEmpty())
        assertTrue(m.referencias.contains("Ropero empotrado 240 x 240 x 60"))
    }

}
