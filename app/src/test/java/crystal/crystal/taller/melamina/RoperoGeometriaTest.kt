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
        val aMano = base.conCuerpo(1, c.conAlturaDeEntrepano(0, 40f, RoperoGeometria.alturasDeEntrepanos(base, c)))
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
        // El de los cajones no tiene casilleros; y bajo el dedo, en medio del hueco, sale el casillero.
        assertTrue(RoperoGeometria.elementos(base).none { it.tipo == TipoElemento.CASILLERO && it.cuerpo == 0 })
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
        val hasta = RoperoGeometria.tuboY(mixto, 0) - RoperoGeometria.ROPA_COLGADA_CM - piso
        assertEquals(desde + (hasta - desde - 1.8f) / 2f, repisa.y0 - piso, 0.05f)
        // Sin cajones no hay tapa.
        assertTrue(RoperoGeometria.elementos(base).none { it.tipo == TipoElemento.TAPA_CAJONES && it.cuerpo == 1 })
    }

    @Test
    fun colgador_con_casilleros_reparte_las_repisas_bajo_la_ropa() {
        val r = base.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.COLGAR_CASILLEROS, entrepanos = 2))
        val c = r.cuerpos[1]
        assertTrue(c.llevaTubo)
        assertEquals(2, c.entrepanosEfectivos)
        val piso = RoperoGeometria.pisoY(r)
        val hasta = RoperoGeometria.tuboY(r, 1) - RoperoGeometria.ROPA_COLGADA_CM - piso
        val repisas = RoperoGeometria.alturasDeEntrepanos(r, c)
        val libre = (hasta - 2 * 1.8f) / 3f
        assertEquals(libre, repisas[0], 0.05f)
        assertEquals(2 * libre + 1.8f, repisas[1], 0.05f)
        val m = RoperoCalculo.calcular(r)
        assertEquals(1, m.accesorios.count { it.nombre == "Tubo colgador" })
        assertEquals(2, m.piezas.filter { it.nombre == "Entrepaño" }.sumOf { it.cantidad })
    }

    @Test
    fun se_encuentra_lo_que_hay_bajo_el_dedo() {
        val cajon = RoperoGeometria.elementoEn(base, 50f, 20f)
        assertNotNull(cajon)
        assertEquals(TipoElemento.CAJON, cajon!!.tipo)
        assertEquals(0, cajon.indice)
        val cuerpo = RoperoGeometria.elementoEn(base, 50f, 150f)
        assertEquals(TipoElemento.CUERPO, cuerpo!!.tipo)
        assertEquals(0, cuerpo.cuerpo)
        assertTrue(RoperoGeometria.elementoEn(base, 300f, 20f) == null)
    }

    @Test
    fun cada_cajon_se_corta_con_su_alto_y_las_hojas_van_a_mano() {
        val conAlto = base.conCuerpo(0, base.cuerpos[0].conAltoDeCajon(0, 30f, 20f))
        val m = RoperoCalculo.calcular(conAlto)
        val frentes = m.piezas.filter { it.nombre == "Frente cajón" }
        assertEquals(setOf(296, 196), frentes.map { it.altoMm }.toSet())
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
