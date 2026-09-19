package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import crystal.crystal.Diseno.nova.DisenoNova
import crystal.crystal.Diseno.nova.PlantaDelDiseno
import crystal.crystal.Diseno.nova.VolumenDelDiseno
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Medidas 3D con una pared curva: el arco de la planta (cuerda 160, flecha 40 → desarrollo
 * 185.5) sigue a la pared recta de la frontal, y llega al paquete como un tramo curvo con su
 * panza `Q<40>` y su altura, que el volumen factea.
 */
@RunWith(AndroidJUnit4::class)
class ArcoEnPlantaTest {

    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1600, android.view.View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    private fun armar(v: SketchMedidasView) {
        // Frontal: una pared recta de 200 x 300 (px).
        v.agregarRectanguloParaPruebas(100f, 100f, 300f, 400f)
        // Superior: esa pared y, a continuación, el arco con la panza hacia arriba del papel (afuera).
        v.cambiarAVista(SketchMedidasView.Vista.SUPERIOR)
        v.agregarLineaParaPruebas(100f, 500f, 300f, 500f)
        val cuerda = v.cmAPixelesParaPruebas(160f)
        v.agregarArcoParaPruebas(300f, 500f, 300f + cuerda, 500f, flechaCm = 40f, alturaCm = 150f)
        v.cambiarAVista(SketchMedidasView.Vista.FRONTAL)
    }

    @Test
    fun el_arco_de_la_planta_llega_al_paquete_como_tramo_curvo_con_su_altura() {
        val v = vista()
        armar(v)
        val paquete = v.paqueteDeVistasParaVolumen()
        assertNotNull("sin paquete", paquete)
        val diseno = DisenoNova.desdePaquete(paquete!!)!!
        assertEquals(2, diseno.tramos.size)
        val curvo = diseno.tramos[1]
        assertEquals("la panza no llegó", 40f, curvo.flecha, 0.2f)
        assertEquals("el ancho del tramo curvo es su desarrollo", 185.5f, curvo.ancho, 0.5f)
        assertEquals("la altura escrita en el arco", 150f, curvo.alto, 0.5f)
        assertEquals("la pared recta no se curvó", 0f, diseno.tramos[0].flecha, 0f)

        val volumen = VolumenDelDiseno.de(PlantaDelDiseno.de(diseno, PlantaDelDiseno.panzaDelPaquete(paquete)))
        assertTrue("el volumen no facetó la curva", volumen.caras.count { it.esCurva } >= 4)
        // La curva sigue a la recta sin doblar en la unión (sale tangente) y su cuerda mide 160.
        val planta = PlantaDelDiseno.de(diseno)
        val pared = planta.paredes[1]
        val cuerda = kotlin.math.hypot(pared.hasta.x - pared.desde.x, pared.hasta.y - pared.desde.y)
        assertEquals(160f, cuerda, 1f)
        // Panza hacia arriba del papel: el arco dobla en positivo (el volumen la saca hacia -y).
        assertTrue("la panza tenía que quedar hacia afuera (-y)", volumen.caras.filter { it.esCurva }.any { it.abajoIzq.y < -5f })
    }

    @Test
    fun desde_fuera_la_panza_cambia_de_signo() {
        val v = vista()
        armar(v)
        v.vistaInterior = false
        val diseno = DisenoNova.desdePaquete(v.paqueteDeVistasParaVolumen()!!)!!
        val curvo = diseno.tramos.first { it.flecha != 0f }
        assertEquals(-40f, curvo.flecha, 0.2f)
    }
}
