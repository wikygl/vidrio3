package crystal.crystal.taller.melamina

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Dónde cae cada cosa dentro del ropero de prueba (240 x 240 x 60, zócalo 10, dos cuerpos), y
 * lo fino de la pantalla de diseño: el alto de cada cajón, la altura de cada repisa, las hojas.
 */
class RoperoGeometriaTest {

    private val base = Ropero(anchoCm = 240f, altoCm = 240f, fondoCm = 60f, zocaloCm = 10f)
        .conCuerposIguales(2)
        .conCuerpo(0, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 3))
        .conCuerpo(1, Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 2))

    @Test
    fun los_cajones_se_apilan_desde_el_piso_con_su_alto() {
        val cajones = RoperoGeometria.elementos(base).filter { it.tipo == TipoElemento.CAJON }
        assertEquals(3, cajones.size)
        assertEquals(11.8f, cajones[0].y0, 0.01f)     // piso: zócalo 10 + 1.8
        assertEquals(31.8f, cajones[0].y1, 0.01f)
        assertEquals(51.8f, cajones[1].y1, 0.01f)
        // Un cajón con otro alto empuja a los de arriba.
        val conAlto = base.conCuerpo(0, base.cuerpos[0].conAltoDeCajon(0, 30f, 20f))
        val c2 = RoperoGeometria.elementos(conAlto).filter { it.tipo == TipoElemento.CAJON }
        assertEquals(41.8f, c2[0].y1, 0.01f)
        assertEquals(61.8f, c2[1].y1, 0.01f)
        assertEquals(listOf(30f, 20f, 20f), conAlto.cuerpos[0].altosDeCajones(20f))
    }

    @Test
    fun las_repisas_se_reparten_y_se_pueden_poner_a_mano() {
        val repisas = RoperoGeometria.elementos(base).filter { it.tipo == TipoElemento.ENTREPANO }
        assertEquals(2, repisas.size)
        // Interior 226.4 menos dos repisas de 1.8 = 222.8, en tres huecos iguales de 74.27:
        // la primera a 74.27 del piso, la segunda a 74.27 + 1.8 + 74.27 = 150.33.
        assertEquals(74.27f, repisas[0].y0 - RoperoGeometria.pisoY(base), 0.05f)
        assertEquals(150.33f, repisas[1].y0 - RoperoGeometria.pisoY(base), 0.05f)
        val c = base.cuerpos[1]
        val aMano = base.conCuerpo(1, c.conAlturaDeEntrepano(0, 40f, RoperoGeometria.alturasDeEntrepanos(base, c, RoperoGeometria.huecoDeCuerpo(base, 1))))
        val r2 = RoperoGeometria.elementos(aMano).filter { it.tipo == TipoElemento.ENTREPANO }
        assertEquals(40f, r2[0].y0 - RoperoGeometria.pisoY(aMano), 0.05f)
        assertEquals(150.33f, r2[1].y0 - RoperoGeometria.pisoY(aMano), 0.05f)
    }

    @Test
    fun los_casilleros_son_los_huecos_entre_repisas_y_se_tocan() {
        val cas = RoperoGeometria.elementos(base).filter { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 1 }
        // Dos repisas: tres casilleros, del piso a la primera, entre las dos, y de la segunda al techo.
        assertEquals(3, cas.size)
        val piso = RoperoGeometria.pisoY(base)
        assertEquals(piso, cas[0].y0, 0.01f)
        assertEquals(piso + 74.27f, cas[0].y1, 0.05f)
        assertEquals(piso + 74.27f + 1.8f, cas[1].y0, 0.05f)
        // Los tres casilleros con el mismo alto libre.
        cas.forEach { assertEquals(74.27f, it.y1 - it.y0, 0.05f) }
        assertEquals(RoperoGeometria.techoY(base, 1), cas[2].y1, 0.01f)
        // Sobre los cajones queda un casillero solo (sin repisas); y bajo el dedo, en medio del hueco, sale el casillero.
        val sobreCajones = RoperoGeometria.elementos(base).filter { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 0 }
        assertEquals(1, sobreCajones.size)
        assertEquals(73.6f, sobreCajones[0].y0, 0.01f)   // sobre la tapa
        val tocado = RoperoGeometria.elementoEn(base, 180f, 150f)
        assertEquals(TipoElemento.CASILLERO, tocado!!.tipo)
        assertEquals(1, tocado.indice)
    }

    @Test
    fun cada_lado_puede_tener_su_alto() {
        val escalera = base.conCuerpo(1, base.cuerpos[1].copy(altoCm = 200f))
        assertTrue(escalera.altosDesiguales)
        assertEquals(240f, escalera.altoMayorCm, 0.01f)
        assertEquals(240f - 1.8f, RoperoGeometria.techoY(escalera, 0), 0.01f)
        assertEquals(200f - 1.8f, RoperoGeometria.techoY(escalera, 1), 0.01f)
        val cuerpos = RoperoGeometria.elementos(escalera).filter { it.tipo == TipoElemento.CUERPO }
        assertEquals(198.2f, cuerpos[1].y1, 0.01f)
        // Los materiales: cada lateral a su alto, la división al mayor, el techo por cuerpos.
        val m = RoperoCalculo.calcular(escalera)
        val laterales = m.piezas.filter { it.nombre == "Lateral" }.map { it.altoMm }.sorted()
        assertEquals(listOf(2000, 2400), laterales)
        val division = m.piezas.first { it.nombre == "División" }
        assertEquals(2400 - 100 - 36, division.altoMm)
        val techos = m.piezas.filter { it.nombre == "Techo" }
        assertEquals(2, techos.sumOf { it.cantidad })
        // Cada trozo: 117.3 de cuerpo + media división = 118.2; los dos suman el ancho interior.
        assertEquals(236.4f, RoperoCalculo.tramosDeTechoPorCuerpo(escalera).sum(), 0.05f)
        // Un alto menor de 30 no cuenta.
        assertTrue(!base.conCuerpo(1, base.cuerpos[1].copy(altoCm = 10f)).altosDesiguales)
    }

    @Test
    fun sobre_los_cajones_va_una_tapa_y_lo_de_arriba_arranca_encima() {
        val tapa = RoperoGeometria.elementos(base).first { it.tipo == TipoElemento.TAPA_CAJONES && it.cuerpo == 0 }
        assertEquals(71.8f, tapa.y0, 0.01f)      // tres cajones de 20 sobre el piso
        assertEquals(73.6f, tapa.y1, 0.01f)
        assertEquals(1, RoperoCalculo.calcular(base).piezas.filter { it.nombre == "Tapa de cajones" }.sumOf { it.cantidad })
        // En un mixto las repisas se reparten desde la cara de arriba de la tapa.
        val mixto = base.conCuerpo(0, Cuerpo(tipo = TipoCuerpo.MIXTO, cajones = 2, entrepanos = 1))
        val repisa = RoperoGeometria.elementos(mixto).first { it.tipo == TipoElemento.ENTREPANO && it.cuerpo == 0 }
        val piso = RoperoGeometria.pisoY(mixto)
        val desde = 40f + 1.8f
        val hasta = RoperoGeometria.tuboY(mixto, RoperoGeometria.huecoDeCuerpo(mixto, 0)) - RoperoGeometria.ROPA_COLGADA_CM - piso
        assertEquals(desde + (hasta - desde - 1.8f) / 2f, repisa.y0 - piso, 0.05f)
        // Sin cajones no hay tapa.
        assertTrue(RoperoGeometria.elementos(base).none { it.tipo == TipoElemento.TAPA_CAJONES && it.cuerpo == 1 })
    }

    @Test
    fun poner_el_alto_de_un_casillero_reparte_los_de_arriba() {
        // Cuerpo 2: dos repisas, tres casilleros de 74.27. El de abajo a 30 (zapatillas): la
        // primera repisa a 30, y las otras dos... la segunda se reparte con lo que queda:
        // 226.4 - 30 - 1.8 = 194.6 libres para dos casilleros y una repisa: (194.6 - 1.8) / 2 = 96.4.
        fun conAlto(r0: Ropero, k: Int, alto: Float) = r0.conCuerpo(1, RoperoGeometria.conAltoDeCasillero(r0, r0.cuerpos[1], RoperoGeometria.huecoDeCuerpo(r0, 1), k, alto))
        val r = conAlto(base, 0, 30f)
        val cas = RoperoGeometria.elementos(r).filter { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 1 }
        assertEquals(30f, cas[0].y1 - cas[0].y0, 0.05f)
        assertEquals(96.4f, cas[1].y1 - cas[1].y0, 0.05f)
        assertEquals(96.4f, cas[2].y1 - cas[2].y0, 0.05f)
        // Ahora el segundo a 50: el primero se queda en 30 y el de arriba se lleva el resto.
        val r2 = conAlto(r, 1, 50f)
        val cas2 = RoperoGeometria.elementos(r2).filter { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 1 }
        assertEquals(30f, cas2[0].y1 - cas2[0].y0, 0.05f)
        assertEquals(50f, cas2[1].y1 - cas2[1].y0, 0.05f)
        assertEquals(226.4f - 30f - 50f - 3.6f, cas2[2].y1 - cas2[2].y0, 0.05f)
        // El de arriba del todo baja la repisa de debajo y no toca las demás.
        val r3 = conAlto(r2, 2, 60f)
        val cas3 = RoperoGeometria.elementos(r3).filter { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 1 }
        assertEquals(60f, cas3[2].y1 - cas3[2].y0, 0.05f)
        assertEquals(30f, cas3[0].y1 - cas3[0].y0, 0.05f)
    }

    @Test
    fun la_cota_de_alto_de_cada_trozo_se_escribe() {
        // El espacio de cajones (3 de 20) puesto a 90: tres de 30.
        val zona = RoperoGeometria.elementos(base).first { it.tipo == TipoElemento.ZONA_CAJONES && it.cuerpo == 0 }
        assertEquals(60f, zona.y1 - zona.y0, 0.01f)
        val r = RoperoGeometria.conAltoDeTrozo(base, zona, 90f)
        assertEquals(listOf(30f, 30f, 30f), r.cuerpos[0].altosDeCajones(20f))
        // El maletero a 50.
        val conMal = base.copy(maleteroCm = 40f)
        val mal = RoperoGeometria.elementos(conMal).first { it.tipo == TipoElemento.MALETERO }
        assertEquals(50f, RoperoGeometria.conAltoDeTrozo(conMal, mal, 50f).maleteroCm, 0.01f)
        // El colgador de un mixto sin repisas: lo que sobra va a los cajones.
        val mixto = base.conCuerpo(0, Cuerpo(tipo = TipoCuerpo.MIXTO, cajones = 2))
        val colgador = RoperoGeometria.elementos(mixto).first { it.tipo == TipoElemento.COLGADOR && it.cuerpo == 0 }
        val r2 = RoperoGeometria.conAltoDeTrozo(mixto, colgador, 150f)
        val colgador2 = RoperoGeometria.elementos(r2).first { it.tipo == TipoElemento.COLGADOR && it.cuerpo == 0 }
        assertEquals(150f, colgador2.y1 - colgador2.y0, 0.05f)
        // La zona de cajones no se toca por dentro (los cajones van primero), pero existe para su cota.
        assertEquals(TipoElemento.CAJON, RoperoGeometria.elementoEn(base, 50f, 20f)!!.tipo)
    }

    @Test
    fun un_casillero_se_parte_en_columnas_cada_una_con_lo_suyo() {
        // El cuerpo 1 (117.3): dos cajones abajo y, encima, el casillero único partido en dos
        // columnas: la izquierda con 3 repisas, la derecha con 2 cajones. Como el plano del taller.
        val e = 1.8f
        val sinRepisas = base.conCuerpo(0, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 2))
        val cas = RoperoGeometria.casilleros(sinRepisas, sinRepisas.cuerpos[0], RoperoGeometria.huecoDeCuerpo(sinRepisas, 0))
        assertEquals(1, cas.size)
        assertEquals(11.8f + 40f + e, cas[0].y0, 0.01f)
        var c = sinRepisas.cuerpos[0].conCasilleroPartido(0, 2, cas[0].ancho, e)
        c = c.conCuerpoEn(listOf(0, 0), Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 3))
        c = c.conCuerpoEn(listOf(0, 1), Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 2))
        val r = sinRepisas.conCuerpo(0, c)
        // Las dos columnas se reparten 117.3 - 1.8 = 115.5: 57.75 cada una.
        val columnas = RoperoGeometria.columnasDeCasillero(r, r.cuerpos[0], cas[0], 0)
        assertEquals(2, columnas.size)
        assertEquals(57.75f, columnas[0].ancho, 0.01f)
        assertEquals(cas[0].x0 + 57.75f + e, columnas[1].x0, 0.01f)
        // Lo de cada columna sale con su ruta: 3 repisas en la izquierda, 2 cajones en la derecha, y la división entre ellas.
        val els = RoperoGeometria.elementos(r)
        assertEquals(3, els.count { it.tipo == TipoElemento.ENTREPANO && it.ruta == listOf(0, 0) })
        assertEquals(2, els.count { it.tipo == TipoElemento.CAJON && it.ruta == listOf(0, 1) })
        assertEquals(1, els.count { it.tipo == TipoElemento.DIVISION_COLUMNA && it.cuerpo == 0 })
        // Los cajones de la columna arrancan en el piso del casillero, no en el del ropero.
        val cajonCol = els.first { it.tipo == TipoElemento.CAJON && it.ruta == listOf(0, 1) && it.indice == 0 }
        assertEquals(cas[0].y0, cajonCol.y0, 0.01f)
        // Bajo el dedo, dentro de la columna, sale lo de la columna antes que el casillero grande.
        val tocado = RoperoGeometria.elementoEn(r, cas[0].x0 + 10f, cas[0].y0 + 5f)
        assertEquals(TipoElemento.CASILLERO, tocado!!.tipo)
        assertEquals(listOf(0, 0), tocado.ruta)
        assertEquals(TipoElemento.CAJON, RoperoGeometria.elementoEn(r, columnas[1].x0 + 10f, cas[0].y0 + 5f)!!.tipo)
        // Por ruta se llega al cuerpo y a su hueco.
        assertEquals(TipoCuerpo.CAJONES, r.cuerpoEn(0, listOf(0, 1))!!.tipo)
        assertEquals(columnas[1].x0, RoperoGeometria.huecoDe(r, 0, listOf(0, 1))!!.x0, 0.01f)
        // Y en los materiales: la división del casillero, los entrepaños de 57.75 y los cajones de la columna.
        val m = RoperoCalculo.calcular(r)
        assertEquals(1, m.piezas.filter { it.nombre == "División de casillero" }.sumOf { it.cantidad })
        assertEquals(3, m.piezas.filter { it.nombre == "Entrepaño" && it.anchoMm == 578 }.sumOf { it.cantidad })
        assertEquals(4, m.piezas.filter { it.nombre == "Frente cajón" }.sumOf { it.cantidad })
    }

    @Test
    fun el_ancho_escrito_se_fija_y_los_demas_se_reparten() {
        val tres = base.conCuerposIguales(3)     // 236.4 - 3.6 = 232.8 libres: 77.6 cada uno
        val a = tres.conAnchoDeCuerpo(0, 50f)
        assertTrue(a.cuerpos[0].anchoFijo)
        assertEquals(91.4f, a.cuerpos[1].anchoCm, 0.01f)
        // Al escribir el segundo, el primero no se mueve: el resto va al tercero.
        val b = a.conAnchoDeCuerpo(1, 60f)
        assertEquals(50f, b.cuerpos[0].anchoCm, 0.01f)
        assertEquals(60f, b.cuerpos[1].anchoCm, 0.01f)
        assertEquals(122.8f, b.cuerpos[2].anchoCm, 0.01f)
    }

    @Test
    fun el_ancho_de_una_columna_se_respeta_y_sube_al_cuerpo() {
        // Cuerpo 1 (117.3) con el casillero partido en dos de 57.75. La columna 1 a 70: la 2 se
        // queda en 57.75, el cuerpo pasa a 70 + 1.8 + 57.75 = 129.55 y el otro cuerpo absorbe el resto.
        val e = 1.8f
        val sinRepisas = base.conCuerpo(0, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 2))
        val cas = RoperoGeometria.casilleros(sinRepisas, sinRepisas.cuerpos[0], RoperoGeometria.huecoDeCuerpo(sinRepisas, 0))[0]
        val r0 = sinRepisas.conCuerpo(0, sinRepisas.cuerpos[0].conCasilleroPartido(0, 2, cas.ancho, e))
        val r = r0.conAnchoEn(0, listOf(0, 0), 70f)
        val columnas = r.cuerpos[0].columnasDe(0)
        assertEquals(70f, columnas[0].anchoCm, 0.01f)
        assertTrue(columnas[0].anchoFijo)
        assertEquals(57.75f, columnas[1].anchoCm, 0.01f)
        assertEquals(129.55f, r.cuerpos[0].anchoCm, 0.01f)
        assertEquals(236.4f - e - 129.55f, r.cuerpos[1].anchoCm, 0.01f)
        // Y en el dibujo las columnas miden eso mismo (sin escalar).
        assertEquals(70f, RoperoGeometria.huecoDe(r, 0, listOf(0, 0))!!.ancho, 0.01f)
        // Al escribir el cuerpo entero (arriba), las columnas se reparten: la fijada se queda, la otra cede.
        val r2 = r.conAnchoDeCuerpo(0, 100f)
        assertEquals(70f, r2.cuerpos[0].columnasDe(0)[0].anchoCm, 0.01f)
        assertEquals(100f - e - 70f, r2.cuerpos[0].columnasDe(0)[1].anchoCm, 0.01f)
    }

    @Test
    fun puertas_propias_por_cuerpo_y_por_casillero() {
        // Como el cuerpo verde del plano: dos casilleros, cada uno con sus corredizas interiores;
        // el otro cuerpo con las batientes del ropero.
        val r = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 1, puertasPropias = TipoPuertas.CORREDIZAS, puertasPorCasillero = true))
        val puertas = RoperoPuertas.de(r)
        val corredizas = puertas.hojas.filter { it.clase == ClaseDePuerta.HOJA_CORREDIZA }
        assertEquals(4, corredizas.size)     // dos por casillero
        assertEquals(4, puertas.rielesCm.size)
        val cas = RoperoGeometria.casilleros(r, r.cuerpos[1], RoperoGeometria.huecoDeCuerpo(r, 1))
        // Cada hoja: (117.3 + 5) / 2 = 61.15 de ancho, y el alto del casillero menos 3.5.
        assertEquals(61.15f, corredizas[0].ancho, 0.01f)
        assertEquals(cas[0].alto - 3.5f, corredizas[0].alto, 0.01f)
        assertEquals(2, puertas.hojas.count { it.clase == ClaseDePuerta.PUERTA })   // las dos hojas del cuerpo 1
        // Sin puertas propias en un cuerpo: no lleva; y con corredizas propias, lo de dentro pierde el carril.
        val sin = base.conCuerpo(1, base.cuerpos[1].copy(puertasPropias = TipoPuertas.SIN))
        assertEquals(2, RoperoPuertas.de(sin).hojas.size)
        val m = RoperoCalculo.calcular(r)
        assertTrue(m.piezas.filter { it.nombre == "Entrepaño" }.any { it.altoMm == 517 })
    }

    @Test
    fun unir_dos_casilleros_a_lo_alto_quita_la_repisa() {
        // Cuerpo 2: dos repisas, tres casilleros. Unir el de abajo con el del medio: queda una repisa, la de arriba, donde estaba.
        val antes = RoperoGeometria.elementos(base).filter { it.tipo == TipoElemento.ENTREPANO && it.cuerpo == 1 }
        val cas = RoperoGeometria.elementos(base).filter { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 1 }
        val (r, motivo) = RoperoUnion.unir(base, cas[0], cas[1])
        assertEquals("", motivo)
        val repisas = RoperoGeometria.elementos(r!!).filter { it.tipo == TipoElemento.ENTREPANO && it.cuerpo == 1 }
        assertEquals(1, repisas.size)
        assertEquals(antes[1].y0, repisas[0].y0, 0.01f)
        val unidos = RoperoGeometria.elementos(r).filter { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 1 }
        assertEquals(2, unidos.size)
        assertEquals(cas[0].y0, unidos[0].y0, 0.01f)
        assertEquals(cas[1].y1, unidos[0].y1, 0.01f)
        // No se unen dos que no están pegados, ni cosas que no son celdas.
        assertTrue(RoperoUnion.unir(base, cas[0], cas[2]).first == null)
        val cajon = RoperoGeometria.elementos(base).first { it.tipo == TipoElemento.CAJON }
        assertTrue(RoperoUnion.unir(base, cas[0], cajon).first == null)
        // Desunir a lo alto le pone la repisa al medio.
        val vuelto = RoperoUnion.desunirAlto(r, unidos[0])
        assertEquals(2, RoperoGeometria.elementos(vuelto).count { it.tipo == TipoElemento.ENTREPANO && it.cuerpo == 1 })
    }

    @Test
    fun unir_los_cajones_de_dos_cuerpos_los_hace_a_todo_lo_ancho() {
        // Dos cuerpos de cajones iguales (3 de 20): unidos, un cuerpo de 236.4 con tres cajones a
        // todo lo ancho y encima el casillero partido en las dos columnas de antes.
        val dos = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 3))
        val zonas = RoperoGeometria.elementos(dos).filter { it.tipo == TipoElemento.ZONA_CAJONES }
        assertEquals(2, zonas.size)
        val (r, motivo) = RoperoUnion.unir(dos, zonas[0], zonas[1])
        assertEquals("", motivo)
        assertEquals(1, r!!.cuerpos.size)
        assertEquals(236.4f, r.cuerpos[0].anchoCm, 0.01f)
        assertEquals(3, r.cuerpos[0].cajonesEfectivos)
        val els = RoperoGeometria.elementos(r)
        val cajones = els.filter { it.tipo == TipoElemento.CAJON && it.ruta.isEmpty() }
        assertEquals(3, cajones.size)
        assertEquals(236.4f, cajones[0].x1 - cajones[0].x0, 0.01f)
        // Arriba, dos columnas de 117.3 con la división entre ellas, que arranca sobre la tapa.
        val division = els.first { it.tipo == TipoElemento.DIVISION_COLUMNA }
        assertEquals(11.8f + 60f + 1.8f, division.y0, 0.01f)
        assertEquals(2, r.cuerpos[0].columnasDe(0).size)
        assertEquals(117.3f, RoperoGeometria.huecoDe(r, 0, listOf(0, 0))!!.ancho, 0.01f)
        // Los materiales: un solo cuerpo, la tapa de 236.4 y una división de casillero.
        val m = RoperoCalculo.calcular(r)
        assertEquals(2364, m.piezas.first { it.nombre == "Tapa de cajones" }.anchoMm)
        assertEquals(1, m.piezas.filter { it.nombre == "División de casillero" }.sumOf { it.cantidad })
        assertTrue(m.piezas.none { it.nombre == "División" })
    }

    @Test
    fun unir_dos_casilleros_vecinos_a_lo_ancho_corta_la_division_en_ese_tramo() {
        // Dos cuerpos de casilleros con dos repisas cada uno, a la misma altura. Unir los del medio.
        val dos = base.conCuerpo(0, Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 2))
        val medioIzq = RoperoGeometria.elementos(dos).first { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 0 && it.indice == 1 }
        val medioDer = RoperoGeometria.elementos(dos).first { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 1 && it.indice == 1 }
        val (r, motivo) = RoperoUnion.unir(dos, medioIzq, medioDer)
        assertEquals("", motivo)
        assertEquals(1, r!!.cuerpos.size)
        val c = r.cuerpos[0]
        // Dos repisas (bajo y sobre la celda unida), el casillero de abajo y el de arriba partidos, el del medio no.
        assertEquals(2, c.entrepanosEfectivos)
        assertEquals(2, c.columnasDe(0).size)
        assertEquals(0, c.columnasDe(1).size)
        assertEquals(2, c.columnasDe(2).size)
        val els = RoperoGeometria.elementos(r)
        val medio = els.first { it.tipo == TipoElemento.CASILLERO && it.ruta.isEmpty() && it.indice == 1 }
        assertEquals(medioIzq.y0, medio.y0, 0.05f)
        assertEquals(medioIzq.y1, medio.y1, 0.05f)
        assertEquals(236.4f, medio.x1 - medio.x0, 0.01f)
        // Las divisiones de columna van solo abajo y arriba, no en el tramo unido.
        val divisiones = els.filter { it.tipo == TipoElemento.DIVISION_COLUMNA }
        assertEquals(2, divisiones.size)
        assertTrue(divisiones.none { it.y0 < medio.y1 && it.y1 > medio.y0 })
        // Desunir a lo ancho vuelve a partir el del medio con las columnas de su vecino.
        val vuelto = RoperoUnion.desunirAncho(r, medio)
        assertEquals(2, vuelto.cuerpos[0].columnasDe(1).size)
        // Con distinta altura no se unen.
        val desiguales = dos.conCuerpo(0, Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 3))
        val aIzq = RoperoGeometria.elementos(desiguales).first { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 0 && it.indice == 1 }
        assertTrue(RoperoUnion.unir(desiguales, aIzq, medioDer).first == null)
    }

    @Test
    fun unir_los_cajones_con_el_casillero_de_encima_quita_la_tapa_y_a_lo_ancho_los_ensancha() {
        // A lo alto: el cuerpo 1 (3 cajones) y su casillero de encima: sin tapa, el casillero baja hasta los cajones.
        val zona = RoperoGeometria.elementos(base).first { it.tipo == TipoElemento.ZONA_CAJONES && it.cuerpo == 0 }
        val encima = RoperoGeometria.elementos(base).first { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 0 && it.indice == 0 }
        assertEquals(zona.y1 + 1.8f, encima.y0, 0.01f)
        val (r, motivo) = RoperoUnion.unir(base, zona, encima)
        assertEquals("", motivo)
        assertTrue(!r!!.cuerpos[0].tapaSobreCajones)
        val els = RoperoGeometria.elementos(r)
        assertTrue(els.none { it.tipo == TipoElemento.TAPA_CAJONES && it.cuerpo == 0 })
        assertEquals(zona.y1, els.first { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 0 && it.indice == 0 }.y0, 0.01f)
        assertTrue(RoperoCalculo.calcular(r).piezas.none { it.nombre == "Tapa de cajones" })
        // Desunir a lo alto sobre ese casillero devuelve la tapa.
        val vuelto = RoperoUnion.desunirAlto(r, els.first { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 0 && it.indice == 0 })
        assertTrue(vuelto.cuerpos[0].tapaSobreCajones)
        // A lo ancho: los cajones (60 de alto) con un casillero vecino de la misma altura: cajones a todo lo ancho.
        val vecino = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 1, alturasEntrepanosCm = listOf(60f)))
        val z = RoperoGeometria.elementos(vecino).first { it.tipo == TipoElemento.ZONA_CAJONES && it.cuerpo == 0 }
        val cas = RoperoGeometria.elementos(vecino).first { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 1 && it.indice == 0 }
        assertEquals(z.y1, cas.y1, 0.01f)
        val (ancho, motivo2) = RoperoUnion.unir(vecino, z, cas)
        assertEquals("", motivo2)
        assertEquals(1, ancho!!.cuerpos.size)
        assertEquals(3, ancho.cuerpos[0].cajonesEfectivos)
        assertEquals(236.4f, RoperoGeometria.elementos(ancho).first { it.tipo == TipoElemento.CAJON }.let { it.x1 - it.x0 }, 0.01f)
    }

    @Test
    fun casilleros_mas_cajones_reparte_las_repisas_sobre_la_tapa() {
        val r = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES_CASILLEROS, cajones = 2, entrepanos = 2))
        val c = r.cuerpos[1]
        assertEquals(2, c.cajonesEfectivos)
        assertEquals(2, c.entrepanosEfectivos)
        assertTrue(!c.llevaTubo)
        val els = RoperoGeometria.elementos(r).filter { it.cuerpo == 1 }
        assertEquals(2, els.count { it.tipo == TipoElemento.CAJON })
        assertEquals(1, els.count { it.tipo == TipoElemento.TAPA_CAJONES })
        // Tres casilleros iguales sobre la tapa: (226.4 - 40 - 1.8 - 3.6) / 3 = 60.33.
        val cas = els.filter { it.tipo == TipoElemento.CASILLERO }
        assertEquals(3, cas.size)
        assertEquals(11.8f + 40f + 1.8f, cas[0].y0, 0.01f)
        cas.forEach { assertEquals(60.33f, it.y1 - it.y0, 0.05f) }
        val m = RoperoCalculo.calcular(r)
        assertEquals(2, m.piezas.filter { it.nombre == "Entrepaño" && it.anchoMm == 1173 }.sumOf { it.cantidad })
        assertEquals(5, m.piezas.filter { it.nombre == "Frente cajón" }.sumOf { it.cantidad })   // 3 del cuerpo 1 y 2 de este
    }

    @Test
    fun una_columna_que_solo_es_reparto_se_aplana_y_sus_columnas_son_vecinas() {
        // Cuerpo 1 (117.3, sin repisas) partido en [30 | 85.5]; la de 85.5 partida a su vez en [40 | 43.7].
        val e = 1.8f
        val liso = base.conCuerpo(0, Cuerpo(tipo = TipoCuerpo.ENTREPANOS))
        val cas = RoperoGeometria.casilleros(liso, liso.cuerpos[0], RoperoGeometria.huecoDeCuerpo(liso, 0))[0]
        var c = liso.cuerpos[0].conCasilleroPartido(0, 2, cas.ancho, e)
        var r = liso.conCuerpo(0, c).conAnchoEn(0, listOf(0, 0), 30f)
        c = r.cuerpos[0]
        val envoltorio = c.columnasDe(0)[1].conCasilleroPartido(0, 2, 85.5f, e)
        r = r.conCuerpoEn(0, listOf(0, 1), envoltorio)
        assertTrue(r.cuerpos[0].columnasDe(0)[1].esSoloReparto)
        // Aplanado: tres columnas hermanas, que suman lo mismo.
        val plano = r.aplanado()
        val columnas = plano.cuerpos[0].columnasDe(0)
        assertEquals(3, columnas.size)
        assertEquals(30f, columnas[0].anchoCm, 0.01f)
        assertEquals(117.3f, columnas.sumOf { it.anchoCm.toDouble() }.toFloat() + 2 * e, 0.05f)
        // Y ahora las dos primeras se pueden unir (antes estaban en niveles distintos).
        val els = RoperoGeometria.elementos(plano)
        val a = els.first { it.tipo == TipoElemento.CASILLERO && it.ruta == listOf(0, 0) }
        val b = els.first { it.tipo == TipoElemento.CASILLERO && it.ruta == listOf(0, 1) }
        val (unido, motivo) = RoperoUnion.unir(plano, a, b)
        assertEquals("", motivo)
        assertEquals(2, unido!!.aplanado().cuerpos[0].columnasDe(0).size)
        // Una columna con repisas no se aplana.
        assertTrue(!Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 1).conCasilleroPartido(0, 2, 60f, e).esSoloReparto)
    }

    @Test
    fun los_cajones_no_se_salen_de_su_hueco() {
        // Tres cajones de 20 en un hueco de 51.8 (con tapa quedan 50): se encogen a 16.67 cada uno.
        val c = Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 3)
        val h = Hueco(0f, 60f, 0f, 51.8f)
        val altos = RoperoGeometria.altosDeCajones(base, c, h)
        assertEquals(3, altos.size)
        altos.forEach { assertEquals(50f / 3f, it, 0.01f) }
        assertEquals(50f, RoperoGeometria.topeDeCajones(base, c, h), 0.01f)
        // Sin tapa, dos cajones de 30 en 51.8: 25.9 cada uno.
        val sinTapa = Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 2, altosCajonesCm = listOf(30f, 30f), tapaSobreCajones = false)
        RoperoGeometria.altosDeCajones(base, sinTapa, h).forEach { assertEquals(25.9f, it, 0.01f) }
        // Si caben, se quedan como están.
        assertEquals(listOf(20f, 20f), RoperoGeometria.altosDeCajones(base, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 2), h))
    }

    @Test
    fun colgador_con_casilleros_reparte_las_repisas_bajo_la_ropa() {
        val r = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.COLGAR_CASILLEROS, entrepanos = 2))
        val c = r.cuerpos[1]
        assertTrue(c.llevaTubo)
        assertEquals(2, c.entrepanosEfectivos)
        val piso = RoperoGeometria.pisoY(r)
        val hasta = RoperoGeometria.tuboY(r, RoperoGeometria.huecoDeCuerpo(r, 1)) - RoperoGeometria.ROPA_COLGADA_CM - piso
        val repisas = RoperoGeometria.alturasDeEntrepanos(r, c, RoperoGeometria.huecoDeCuerpo(r, 1))
        val libre = (hasta - 2 * 1.8f) / 3f
        assertEquals(libre, repisas[0], 0.05f)
        assertEquals(2 * libre + 1.8f, repisas[1], 0.05f)
        val m = RoperoCalculo.calcular(r)
        assertEquals(1, m.accesorios.count { it.nombre == "Tubo colgador" })
        assertEquals(2, m.piezas.filter { it.nombre == "Entrepaño" }.sumOf { it.cantidad })
    }

    @Test
    fun el_maletero_se_toca_aparte_del_cuerpo() {
        val r = base.copy(maleteroCm = 40f)
        // El cuerpo acaba en la repisa del maletero (238.2 - 40 - 1.8 = 196.4); encima, el maletero.
        val cuerpo = RoperoGeometria.elementos(r).first { it.tipo == TipoElemento.CUERPO && it.cuerpo == 0 }
        assertEquals(196.4f, cuerpo.y1, 0.01f)
        val arriba = RoperoGeometria.elementoEn(r, 50f, 220f)
        assertEquals(TipoElemento.MALETERO, arriba!!.tipo)
        assertEquals(0, arriba.cuerpo)
        assertEquals(RoperoGeometria.techoY(r), arriba.y1, 0.01f)
        // Con compartimentos propios, cada uno es un maletero: el de la derecha con el índice 1.
        val propio = r.copy(maleteroCuerpos = 2)
        val der = RoperoGeometria.elementoEn(propio, 200f, 220f)
        assertEquals(TipoElemento.MALETERO, der!!.tipo)
        assertEquals(1, der.indice)
        assertEquals(2, RoperoGeometria.elementos(propio).count { it.tipo == TipoElemento.MALETERO })
    }

    @Test
    fun se_encuentra_lo_que_hay_bajo_el_dedo() {
        val cajon = RoperoGeometria.elementoEn(base, 50f, 20f)
        assertNotNull(cajon)
        assertEquals(TipoElemento.CAJON, cajon!!.tipo)
        assertEquals(0, cajon.indice)
        // Sobre los cajones se toca el casillero, nunca el cuerpo entero (ese va por su cota).
        val sobre = RoperoGeometria.elementoEn(base, 50f, 150f)
        assertEquals(TipoElemento.CASILLERO, sobre!!.tipo)
        assertEquals(0, sobre.cuerpo)
        assertEquals(TipoElemento.CUERPO, RoperoGeometria.cuerpo(base, 0)!!.tipo)
        // En un colgador, la parte de la ropa es el colgador, y las repisas de debajo hacen casilleros.
        val colgador = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.COLGAR, entrepanos = 1))
        assertEquals(TipoElemento.COLGADOR, RoperoGeometria.elementoEn(colgador, 180f, 150f)!!.tipo)
        assertEquals(TipoElemento.CASILLERO, RoperoGeometria.elementoEn(colgador, 180f, 20f)!!.tipo)
        assertTrue(RoperoGeometria.elementoEn(base, 300f, 20f) == null)
    }

    @Test
    fun cada_cajon_se_corta_con_su_alto_y_las_hojas_van_a_mano() {
        val conAlto = base.conCuerpo(0, base.cuerpos[0].conAltoDeCajon(0, 30f, 20f))
        val m = RoperoCalculo.calcular(conAlto)
        val frentes = m.piezas.filter { it.nombre == "Frente cajón" }
        assertEquals(setOf(295, 195), frentes.map { it.altoMm }.toSet())   // menos 0.09 de canto fino
        assertEquals(3, frentes.sumOf { it.cantidad })
        // Una hoja a mano en un cuerpo de 117: dos tocaban.
        val unaHoja = base.conCuerpo(1, base.cuerpos[1].copy(hojasBatientes = 1))
        assertEquals(1, RoperoCalculo.hojasBatientes(unaHoja.cuerpos[1], 1.8f))
        assertEquals(2, RoperoCalculo.hojasBatientes(base.cuerpos[1], 1.8f))
        // Corredizas a mano: 4 hojas donde tocaban 2.
        assertEquals(4, RoperoCalculo.hojasCorredizas(base.copy(hojasCorredizas = 4)))
        assertEquals(2, RoperoCalculo.hojasCorredizas(base))
        assertEquals("Tapacanto 22 x 2 mm", RoperoCalculo.nombreTapacanto(base.copy(tapacantoGrosorMm = 2f)))
    }
}
