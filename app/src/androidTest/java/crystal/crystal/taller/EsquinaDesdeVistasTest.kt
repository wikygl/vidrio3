package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Medidas 3D: la ventana de esquina que sale de las vistas. La planta (vista superior) da las
 * paredes y los ángulos; la frontal da la forma de la pared de frente; la pared de al lado, sin
 * vista lateral, es un rectángulo del largo de la planta por el alto del canto que la toca.
 */
@RunWith(AndroidJUnit4::class)
class EsquinaDesdeVistasTest {

    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1600, android.view.View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    private fun px(v: SketchMedidasView, cm: Float) = v.cmAPixelesParaPruebas(cm)

    /** Frontal: una L de 280 x 300 (300 x 300 más 80 x 200 abajo a la derecha) con un parante a 100. */
    private fun armar(v: SketchMedidasView) {
        val a = v.agregarRectanguloParaPruebas(100f, 100f, 300f, 400f)
        val b = v.agregarRectanguloParaPruebas(300f, 200f, 380f, 400f)
        v.seleccionarParaPruebas(a, b)
        assertTrue(v.weldSelected())
        val parante = v.agregarLineaParaPruebas(200f, 110f, 200f, 390f)
        v.seleccionarParaPruebas(parante)
        assertTrue(v.aplicarEstiloASeleccion(2f, null))
        // Superior: la planta, 280 hacia la derecha y luego 64.5 hacia abajo (hacia quien mira: el rincón).
        v.cambiarAVista(SketchMedidasView.Vista.SUPERIOR)
        v.agregarLineaParaPruebas(100f, 500f, 380f, 500f)
        v.agregarLineaParaPruebas(380f, 500f, 380f, 564.5f)
        v.cambiarAVista(SketchMedidasView.Vista.FRONTAL)
    }

    @Test
    fun la_planta_y_la_frontal_arman_la_l() {
        val v = vista()
        armar(v)
        assertTrue("con planta, la esquina sale de las vistas", v.esquinaSaleDeLasVistas())
        val esquina = v.esquinaPrincipalEnCm()
        assertNotNull(esquina)
        assertEquals(2, esquina!!.lados.size)
        assertEquals(280f, px(v, esquina.lados[0].ancho), 2f)
        assertEquals(300f, px(v, esquina.lados[0].alto), 2f)
        assertEquals("la pared de al lado mide lo que la planta", 64.5f, px(v, esquina.lados[1].ancho), 2f)
        assertEquals("y el alto del canto que la toca", 200f, px(v, esquina.lados[1].alto), 2f)
        assertEquals(listOf("90"), esquina.angulos)
        assertEquals("nl", esquina.geometria)

        val contornos = v.contornosDeLadosEnCm()
        assertEquals(6, contornos[0]!!.size)
        assertNull(contornos[1])
        val parantes = v.parantesDeLadosEnCm()
        assertEquals(1, parantes[0].size)
        assertEquals(100f, px(v, parantes[0][0]), 2f)
        assertTrue(parantes[1].isEmpty())
    }

    @Test
    fun desde_fuera_se_lee_al_reves_y_el_angulo_cambia_de_signo() {
        val v = vista()
        armar(v)
        v.vistaInterior = false
        val esquina = v.esquinaPrincipalEnCm()!!
        assertEquals("desde fuera la primera pared es la de 64.5", 64.5f, px(v, esquina.lados[0].ancho), 2f)
        assertEquals(listOf("-90"), esquina.angulos)
    }

    @Test
    fun las_vistas_se_guardan_y_vuelven() {
        val v = vista()
        armar(v)
        val json = v.exportEditableState()
        assertTrue("las vistas no se guardan: $json", json.contains("\"vistas\""))
        val otra = vista()
        assertTrue(otra.loadEditableState(json))
        assertTrue(otra.tieneVistas())
        assertEquals(SketchMedidasView.Vista.FRONTAL, otra.vistaActiva)
        assertTrue(otra.vistaTieneDibujo(SketchMedidasView.Vista.SUPERIOR))
        assertEquals(2, otra.esquinaPrincipalEnCm()!!.lados.size)
        // Limpiar se lleva todas las vistas.
        otra.clear()
        assertTrue(!otra.tieneVistas())
    }

    @Test
    fun la_polilinea_encadena_lineas_rectas() {
        val v = vista()
        v.activarModoEdicion(SketchMedidasView.ModoEdicion.POLILINEA)
        v.toqueDeEdicionParaPruebas(100f, 500f)
        v.toqueDeEdicionParaPruebas(380f, 503f)   // casi horizontal: se endereza
        v.toqueDeEdicionParaPruebas(377f, 600f)   // casi vertical: se endereza
        v.toqueDeEdicionParaPruebas(377f, 600f)   // otra vez encima: termina
        val lineas = v.lineasParaPruebas()
        assertEquals(2, lineas.size)
        assertEquals(listOf(100f, 500f, 380f, 500f), lineas[0])
        assertEquals(listOf(380f, 500f, 380f, 600f), lineas[1])
    }

    /** La perspectiva generada: la ventana de las vistas sale como paquete y el 3D la arma. */
    @Test
    fun la_perspectiva_se_genera_de_las_vistas() {
        val v = vista()
        armar(v)
        val paquete = v.paqueteDeVistasParaVolumen()
        assertNotNull("sin paquete no hay perspectiva", paquete)
        val d = crystal.crystal.Diseno.nova.DisenoNova.desdePaquete(paquete!!)!!
        assertEquals(2, d.tramos.size)
        assertEquals("A<90>", d.tramos[1].pliegue)
        assertEquals("la pared de frente va con su forma", 6, d.tramos[0].contorno.size)
        assertEquals("y con su parante", listOf(0), d.tramos[0].sistema!!.parantes)
        assertTrue("la de al lado cuelga del dintel", d.tramos[1].caida > 0f)
        val volumen = crystal.crystal.Diseno.nova.VistaVolumenNova(ApplicationProvider.getApplicationContext())
        assertTrue("el 3D no arma la ventana: $paquete", volumen.mostrar(paquete))
    }

    /**
     * Sin dibujar la planta: la frontal y la derecha ya arman la L a 90 —todo arranca en el mismo
     * cero—, la perspectiva sale con las dos paredes y la planta generada se puede volver líneas.
     */
    @Test
    fun la_frontal_y_la_derecha_arman_la_l_sin_planta() {
        val v = vista()
        v.agregarRectanguloParaPruebas(100f, 100f, 380f, 400f)
        // Con solo la frontal hay perspectiva (una pared) pero no esquina.
        assertNotNull(v.paqueteDeVistasParaVolumen())
        assertTrue(!v.esquinaSaleDeLasVistas())
        v.cambiarAVista(SketchMedidasView.Vista.DERECHA)
        assertTrue("la lateral vacía enseña el canto con la frontal", v.enseniaAlgoGenerado())
        v.agregarRectanguloParaPruebas(0f, -162f, 64.5f, 0f)
        v.cambiarAVista(SketchMedidasView.Vista.FRONTAL)
        assertTrue(v.esquinaSaleDeLasVistas())
        val esquina = v.esquinaPrincipalEnCm()!!
        assertEquals(2, esquina.lados.size)
        assertEquals(280f, px(v, esquina.lados[0].ancho), 2f)
        assertEquals(64.5f, px(v, esquina.lados[1].ancho), 2f)
        assertEquals(162f, px(v, esquina.lados[1].alto), 2f)
        assertEquals(listOf("90"), esquina.angulos)
        val d = crystal.crystal.Diseno.nova.DisenoNova.desdePaquete(v.paqueteDeVistasParaVolumen()!!)!!
        assertEquals(2, d.tramos.size)
        assertEquals("A<90>", d.tramos[1].pliegue)
        // La planta generada, hecha líneas: 280 a la derecha y 64.5 hacia abajo (el rincón).
        v.cambiarAVista(SketchMedidasView.Vista.SUPERIOR)
        assertTrue(v.enseniaAlgoGenerado())
        assertTrue(v.materializarPlantaGenerada())
        val lineas = v.lineasParaPruebas()
        assertEquals(2, lineas.size)
        assertEquals(px(v, 280f / v.cmAPixelesParaPruebas(1f)), lineas[0][2], 2f)
        assertTrue("la segunda baja hacia quien mira", lineas[1][3] > lineas[1][1])
        assertTrue(v.plantaEstaDibujada())
    }
}
