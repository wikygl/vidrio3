package crystal.crystal.taller

import android.graphics.PointF
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import crystal.crystal.Diseno.nova.DisenoNova
import crystal.crystal.Diseno.nova.PlantaDelDiseno
import crystal.crystal.Diseno.nova.VolumenDelDiseno
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * El caso de la prueba del usuario: un trapecio de frente (178.8 arriba, 160 en el canto derecho)
 * y a la derecha una pared de 72 con una muesca arriba (160 en el canto que toca a la frontal y
 * 196 en el de atrás). En 3D las dos paredes comparten el canto: el mismo punto del espacio, a
 * 160 de alto, para las dos.
 */
@RunWith(AndroidJUnit4::class)
class PerspectivaDeVistasTest {

    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1600, android.view.View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    @Test
    fun las_dos_paredes_comparten_el_canto_a_la_misma_altura() {
        val v = vista()
        val k = v.cmAPixelesParaPruebas(1f)
        // Frontal: el trapecio, con el canto derecho de 160.
        v.agregarCuadrilateroParaPruebas(
            PointF(0f, 0f), PointF(178.8f * k, 0f), PointF(178.8f * k, 160f * k), PointF(65f * k, 160f * k)
        )
        // Derecha: 72 de ancho, 160 en el canto de la izquierda y 196 en el de la derecha.
        v.cambiarAVista(SketchMedidasView.Vista.DERECHA)
        val a = v.agregarRectanguloParaPruebas(0f, -160f * k, 48f * k, 0f)
        val b = v.agregarRectanguloParaPruebas(48f * k, -196f * k, 72f * k, 0f)
        v.seleccionarParaPruebas(a, b)
        assertTrue(v.weldSelected())
        v.cambiarAVista(SketchMedidasView.Vista.FRONTAL)

        val paquete = v.paqueteDeVistasParaVolumen()!!
        val d = DisenoNova.desdePaquete(paquete)!!
        assertEquals("dos paredes: $paquete", 2, d.tramos.size)
        val planta = PlantaDelDiseno.de(d)
        assertEquals(listOf(160f, 196f), planta.paredes.map { it.altoCm })
        val volumen = VolumenDelDiseno.de(planta)
        val frontal = volumen.caras.first { it.indiceTramo == 0 }
        val derecha = volumen.caras.first { it.indiceTramo == 1 }
        // El canto compartido: el final de la frontal es el arranque de la derecha, en el suelo.
        assertEquals(frontal.abajoDer.x, derecha.abajoIzq.x, 0.01f)
        assertEquals(frontal.abajoDer.y, derecha.abajoIzq.y, 0.01f)
        // Y la silueta de la derecha, en ese canto, sube 160: lo mismo que la frontal.
        val silueta = d.tramos[1].contorno
        assertTrue("la derecha va sin silueta: $paquete", silueta.size >= 3)
        val altoSilueta = silueta.maxOf { it.second }
        val enElCanto = silueta.filter { kotlin.math.abs(it.first) < 0.5f }
        val altoEnElCanto = altoSilueta - enElCanto.minOf { it.second }
        assertEquals("el canto de la derecha tiene que medir 160: $paquete", 160f, altoEnElCanto, 1f)
        assertEquals(160f, frontal.arribaDer.z, 0.5f)
    }

    /**
     * El apunte real de la prueba del usuario: el trapecio de frente es una F3 reflejada con sus
     * tres medidas rectas escritas (178.8, 160 y 113.8) pero sin rehacer —la diagonal no se mide—,
     * así que el dibujo medía 104.7 de alto mientras la cota decía 160, y en 3D la frontal salía
     * más baja que la pared de al lado. Con las tres rectas la figura se rehace y las dos paredes
     * comparten el canto a 160.
     */
    @Test
    fun el_trapecio_reflejado_se_rehace_con_sus_tres_medidas() {
        val v = vista()
        val json = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().context.assets
            .open("vistas_3d.json").bufferedReader().readText()
        assertTrue(v.loadEditableState(json))
        assertTrue("no tenía nada anotado que rehacer", v.rehacerConLoAnotadoParaPruebas())
        val contorno = v.contornoDelCompositeParaPruebas()
        val alto = contorno.maxOf { it.second } - contorno.minOf { it.second }
        assertEquals("el trapecio no se rehizo a 160", 160f, alto, 1f)
        assertEquals(178.8f, contorno.maxOf { it.first } - contorno.minOf { it.first }, 1f)
        val paquete = v.paqueteDeVistasParaVolumen()!!
        val d = DisenoNova.desdePaquete(paquete)!!
        val planta = PlantaDelDiseno.de(d)
        assertEquals("las dos paredes a 160: $paquete", listOf(160f, 160f), planta.paredes.map { Math.round(it.altoCm).toFloat() })
    }
}
