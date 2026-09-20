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
        // Interior 226.4 entre tres: a 75.5 y 150.9 del piso.
        assertEquals(75.47f, repisas[0].y0 - RoperoGeometria.pisoY(base), 0.05f)
        assertEquals(150.93f, repisas[1].y0 - RoperoGeometria.pisoY(base), 0.05f)
        val c = base.cuerpos[1]
        val aMano = base.conCuerpo(1, c.conAlturaDeEntrepano(0, 40f, RoperoGeometria.alturasDeEntrepanos(base, c)))
        val r2 = RoperoGeometria.elementos(aMano).filter { it.tipo == TipoElemento.ENTREPANO }
        assertEquals(40f, r2[0].y0 - RoperoGeometria.pisoY(aMano), 0.05f)
        assertEquals(150.93f, r2[1].y0 - RoperoGeometria.pisoY(aMano), 0.05f)
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
