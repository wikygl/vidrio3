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
        // Canto fino arriba y abajo, que va al piso (2026-10-12): 240 - 0.09 = 239.9 (antes 240, con uno solo).
        assertEquals(2399, lateral.altoMm)
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
        assertEquals(2399, pieza(m, "División pasante")!!.altoMm)   // también va al piso: canto abajo (antes 2400)
        // Uno de 170 no la necesita: piso entero y la división entre piso y techo.
        val angosto = RoperoCalculo.calcular(base.conHueco(170f, 240f, 60f))
        assertEquals(1664, pieza(angosto, "Piso")!!.anchoMm)
        assertEquals(2264, pieza(angosto, "División")!!.altoMm)
        // El zócalo va delante, de punta a punta como las puertas (240, en dos trozos de 120 que se
        // atornillan al canto de la pasante), y llega a medio piso (10.9): de alto 10.9 - 0.3 de
        // canto - 0.15 de gruña = 10.45 → 10 al medio centímetro hacia abajo.
        assertEquals(100, pieza(m, "Zócalo")!!.altoMm)
        // Cada tramo lleva el canto de 3 mm en su punta del mueble, no en la junta (2026-10-12): 119.7 (antes 120).
        assertEquals(1197, pieza(m, "Zócalo")!!.anchoMm)
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
        // Gruña solo entre hojas (2026-10-11): 3 x 0.3 / 4 = 0.225 por hoja → 59.2 no cabe por 1 mm y
        // 59.1 deja 3 mm (más de 2): gruña de 2.5 → 59.2 y sobra 0.5 mm. Antes 59.1.
        assertEquals(592, puerta.anchoMm)
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
        // Sin gruña en el canto del mueble (2026-10-11): 120 - 0.15 - 0.6 = 119.25 → 119.3 no cabe, 119.2. Antes 119.1.
        assertTrue(fs.all { it.anchoMm == 1192 })
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
        // 57.8 + 57.8 + 0.6 + 0.6 de gruña = 117.4: no cabían por 1 mm. Ahora 57.7 y sobra 1 mm (antes 57.8).
        assertEquals(577, puerta.anchoMm)
        assertEquals(1837, puerta.altoMm)
        // La del maletero: 40 - 0.3 - 0.6 = 39.1, en el hueco de 117.3.
        assertEquals(391, pieza(m, "Puerta maletero")!!.altoMm)
        assertEquals(577, pieza(m, "Puerta maletero")!!.anchoMm)   // antes 57.8
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
        // Sin gruña arriba: la lleva entera la puerta de encima (2026-10-12; antes 80.75, media gruña abajo de la tapa).
        assertEquals(7f + 80.9f, frentes.maxOf { it.y1 }, 0.01f)
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
        // Sin gruña en los cantos del mueble (2026-10-11; antes 0.15 a cada punta).
        assertEquals(0f, hojas.minOf { it.x0 }, 0.01f)
        assertEquals(155f, hojas.maxOf { it.x1 }, 0.01f)
        // Y el zócalo de delante, en su mismo plano, de punta a punta.
        assertEquals(listOf(155f), RoperoCalculo.tramosDeZocalo(r))
    }

    @Test
    fun los_frentes_interiores_dejan_medio_centimetro_y_la_caja_queda_mas_baja() {
        // El ropero del usuario (2026-10-11): tres cajones tras la puerta en un espacio de 65 y uno
        // solo en 18.4 con caja de 18. Cuatro gruñas de 0.5 en 65: 65 - 2 = 63 / 3 = 21 terminados,
        // 20.9 cortados con el canto fino (antes 21.2, con 2 mm); la caja de 20 ya es más baja.
        // El de 18.4: 18.4 - 1 = 17.4 → 17.3 cortado (antes 17.9), y la caja de 18 baja a 17.
        val r = base.conCuerpo(0, Cuerpo(tipo = TipoCuerpo.MIXTO, cajones = 3, altoCajonesFijoCm = 65f, altosCajonesCm = listOf(20f, 20f, 20f)))
            .conCuerpo(1, Cuerpo(tipo = TipoCuerpo.MIXTO, cajones = 1, altoCajonesFijoCm = 18.4f, altosCajonesCm = listOf(18f)))
        val m = RoperoCalculo.calcular(r)
        val frentes = m.piezas.filter { it.nombre == "Frente cajón" }.groupBy { it.altoMm }.mapValues { (_, p) -> p.sumOf { it.cantidad } }
        assertEquals(mapOf(209 to 3, 173 to 1), frentes)
        val laterales = m.piezas.filter { it.nombre == "Lateral cajón" }.groupBy { it.altoMm }.mapValues { (_, p) -> p.sumOf { it.cantidad } }
        assertEquals(mapOf(199 to 6, 169 to 2), laterales)   // canto arriba y abajo: - 0.09 (antes 200 y 170)
        val cajas = m.piezas.filter { it.nombre == "Frente y trasera de caja" }.groupBy { it.altoMm }.mapValues { (_, p) -> p.sumOf { it.cantidad } }
        assertEquals(mapOf(199 to 6, 169 to 2), cajas)
        // El ropero de 157 del Xiaomi: cuerpos de 49.93, frentes de 49.93 - 3.6 = 46.33, cortados 46.2 (antes 49.4).
        val r157 = r.conHueco(157f, 242f, 62f).conCuerposIguales(3)
        assertTrue(RoperoCalculo.calcular(r157).piezas.filter { it.nombre == "Frente cajón" }.all { it.anchoMm == 462 })
        // El zócalo de punta a punta, con el canto de 3 mm en sus dos puntas (2026-10-12): 157 - 0.6 = 156.4 (antes 157).
        assertEquals(1564, pieza(RoperoCalculo.calcular(r157), "Zócalo")!!.anchoMm)
        // Y los dos suplementos de cada cuerpo con cajones, del alto de su espacio.
        assertEquals(mapOf(650 to 2, 184 to 2), m.piezas.filter { it.nombre == "Suplemento de riel" }.groupBy { it.altoMm }.mapValues { (_, p) -> p.sumOf { it.cantidad } })
    }

    @Test
    fun a_lo_alto_cuadra_exacto_sin_gruna_en_los_cantos() {
        // El ropero del usuario (2026-10-12): 242 de alto, zócalo 8, maletero 30. Zócalo con canto
        // arriba y abajo (va al piso), puertas con la gruña de 3 mm abajo y nada arriba:
        // 6.5 + 202.2 + 30.9 + 6 cantos de 0.3 + 2 gruñas de 0.3 = 242.
        val r = base.copy(zocaloCm = 8f, maleteroCm = 30f, maleteroCuerpos = 2, maleteroHojas = 1).conHueco(157f, 242f, 62f).conCuerposIguales(3)
        val m = RoperoCalculo.calcular(r)
        val zocalo = pieza(m, "Zócalo")!!
        assertEquals(65, zocalo.altoMm)                      // 8 - 0.9 - 0.6 (antes 8 - 0.9 - 0.3 - 0.15 → 6.5 también, por el redondeo)
        assertEquals(1564, zocalo.anchoMm)
        assertEquals(2, zocalo.cantosEnAncho)               // arriba y abajo
        assertEquals(2022, pieza(m, "Puerta")!!.altoMm)
        assertEquals(309, pieza(m, "Puerta maletero")!!.altoMm)
        val hojas = RoperoPuertas.de(r).hojas
        val puerta = hojas.first { it.clase == ClaseDePuerta.PUERTA }
        val maletero = hojas.first { it.clase == ClaseDePuerta.PUERTA_MALETERO }
        assertEquals(7.1f + 0.3f, puerta.y0, 0.01f)         // el zócalo termina en 7.1 y la gruña
        assertEquals(210.2f, puerta.y1, 0.01f)              // hasta la cara de arriba de la repisa del maletero
        assertEquals(210.5f, maletero.y0, 0.01f)
        assertEquals(242f, maletero.y1, 0.01f)              // hasta el canto del mueble, sin gruña
        // Los laterales, también con canto abajo: 242 - 0.09.
        assertEquals(2419, pieza(m, "Lateral")!!.altoMm)
    }

    @Test
    fun la_gruna_va_entre_hojas_y_no_en_los_cantos_del_mueble() {
        // El ropero del usuario (2026-10-11): 157 de ancho, tres cuerpos de 49.9, una hoja cada
        // uno, canto de 3 mm. Gruña 0.3 solo entre hojas (2 x 0.3), repartida por igual: cada
        // hoja pierde 0.2 → 51.8 = 2 y 50.9 = 1 cortadas, y sobra 1 mm. Antes 51.7 = 2 y 50.8 = 1.
        val r = base.conHueco(157f, 230f, 60f).conCuerposIguales(3)
        val puertas = RoperoCalculo.calcular(r).piezas.filter { it.nombre == "Puerta" }
        assertEquals(mapOf(518 to 2, 509 to 1), puertas.groupBy { it.anchoMm }.mapValues { (_, p) -> p.sumOf { it.cantidad } })
        val hojas = RoperoPuertas.de(r).hojas.sortedBy { it.x0 }
        assertEquals(0f, hojas.first().x0, 0.001f)
        assertEquals(157f, hojas.last().x1, 0.01f)
        // Las juntas: 0.3 de gruña y medio milímetro de lo que sobra en cada una.
        assertEquals(0.35f, hojas[1].x0 - hojas[0].x1, 0.001f)
        assertEquals(0.35f, hojas[2].x0 - hojas[1].x1, 0.001f)
    }

    @Test
    fun los_cajones_traen_frente_caja_fondo_y_rieles() {
        val conCajones = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 3))
        val m = RoperoCalculo.calcular(conCajones)
        val frente = pieza(m, "Frente cajón")!!
        assertEquals(3, frente.cantidad)
        // El hueco menos dos melaminas (2026-10-11): 117.3 - 3.6 - 0.09 de canto fino → 113.6 (antes 116.8, que no abría).
        assertEquals(1136, frente.anchoMm)
        // Gruña de 0.5 entre frentes interiores (2026-10-11): 20 - 4 x 0.5 / 3 = 19.33 - 0.09 → 19.2 (antes 19.5, 20 - 0.4 - 0.09).
        assertEquals(192, frente.altoMm)
        val lateral = pieza(m, "Lateral cajón")!!
        assertEquals(6, lateral.cantidad)
        assertEquals(529, lateral.anchoMm)   // fondo útil 57.9 menos 5
        // La caja de 20 escrita no puede pasar su frente de 19.33: baja a 19 (2026-10-11; antes 20).
        assertEquals(189, lateral.altoMm)   // canto arriba y abajo (2026-10-12): 19 - 0.09 (antes 190, con uno)
        // Canto en todo lado que no toca otra melamina (2026-10-12): el lateral en U (delante topa con
        // el frente), frente y trasera en II (sus puntas topan con los laterales).
        assertEquals("U", RoperoProduccion.cantoProveedor(conCajones, lateral).second)
        assertEquals("II", RoperoProduccion.cantoProveedor(conCajones, pieza(m, "Frente y trasera de caja")!!).second)
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
        // Con los sistemas Ducasse (2026-10-12): traslape 3, D-25 el vano menos 0.7 de alto y D-52 menos 1.2.
        // Antes, a ojo: (236.4 + 5) / 2 - 0.6 = 120.1, 226.4 - 3.5 - 0.6 = 222.3, 8 de carril y dos rieles.
        val m = RoperoCalculo.calcular(base.copy(puertas = TipoPuertas.CORREDIZAS))
        val hoja = pieza(m, "Hoja corrediza")!!
        assertEquals(2, hoja.cantidad)
        assertEquals(1191, hoja.anchoMm)   // (236.4 + 3) / 2 - 0.6 de canto grueso
        assertEquals(2251, hoja.altoMm)    // 226.4 - 0.7 - 0.6
        // Los entrepaños pierden lo de las ranuras: 59.7 - (2 x 2.3 + 0.15) = 54.95, menos el canto fino.
        assertEquals(549, pieza(m, "Entrepaño")!!.altoMm)
        assertEquals("", m.lineasConLargo("Riel corredizo"))   // el D-25 no lleva riel
        assertEquals(1, m.accesorios.first { it.nombre.startsWith("Sistema corredizo Ducasse D-25") }.cantidad)
        // Piso y techo con sus dos ranuras: en el Excel del proveedor, "R 2.3 Y 4.6".
        assertEquals(listOf(2.3f, 4.6f), pieza(m, "Piso")!!.ranurasCorredizaCm.map { kotlin.math.round(it * 10f) / 10f })
        assertEquals("R 2.3 Y 4.6", RoperoProduccion.filaProveedor(base, pieza(m, "Techo")!!).obs)
        // D-52: la hoja 1.2 menos que el vano, riel de 2E + 9 = 4.5 y el kit de 3 m (el hueco pasa de 2 m).
        val d52 = RoperoCalculo.calcular(base.copy(puertas = TipoPuertas.CORREDIZAS, sistemaCorredizo = SistemaCorredizo.D52))
        assertEquals(1191, pieza(d52, "Hoja corrediza")!!.anchoMm)
        assertEquals(2246, pieza(d52, "Hoja corrediza")!!.altoMm)   // 226.4 - 1.2 - 0.6
        assertEquals(552, pieza(d52, "Entrepaño")!!.altoMm)          // 59.7 - 4.5
        assertTrue(pieza(d52, "Piso")!!.ranurasCorredizaCm.isEmpty())
        assertEquals(1, d52.accesorios.first { it.nombre == "Kit clóset Ducasse D-52 de 3 m (2 hojas)" }.cantidad)
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
        assertEquals(2399, pieza(m, "División pasante")!!.altoMm)   // también va al piso: canto abajo (antes 2400)
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
        assertTrue(m.lineasDePiezas(MaterialPlancha.MELAMINA_18_COLOR).lines().contains("57.9x239.9 = 2"))   // antes 240: canto también abajo
        // En la blanca, de ese largo, solo la pasante del medio: sus dos caras quedan por dentro.
        assertTrue(lineas.contains("57.9x239.9 = 1"))
        assertTrue(m.lineasDePiezas(MaterialPlancha.MELAMINA_18_COLOR).lines().contains("59.2x228.2 = 4"))   // antes 59.1 (gruña también en los cantos)
        // Los cantos de los laterales van en el fino de color, no en el blanco.
        // Su frente, su canto de arriba y el de abajo, que va al piso (2026-10-12; antes "58 = 2").
        assertEquals(listOf("240 = 2", "58 = 4"), m.lineasDeTapacantoColor().lines())
        assertTrue(m.lineasDeTapacanto().lines().none { it == "240 = 2" })
        // Todo del mismo color: no hay lista aparte.
        assertTrue(RoperoCalculo.calcular(base.copy(interiorBlanco = false)).tapacantoColor.isEmpty())
        assertTrue(m.referencias.contains("Ropero empotrado 240 x 240 x 60"))
    }

}
