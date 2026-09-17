package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Extender, recortar y mover imantado desde el apunte: el toque elige la línea o la figura y el
 * resultado es lo que queda dibujado. La geometría en sí se prueba en EdicionLineasTest.
 */
@RunWith(AndroidJUnit4::class)
class EdicionLineasEnVistaTest {

    private fun vista() = SketchMedidasView(ApplicationProvider.getApplicationContext())

    private fun cerca(esperado: List<Float>, real: List<Float>) {
        assertEquals("la línea no es la esperada: $real", esperado.size, real.size)
        esperado.zip(real).forEach { (e, r) -> assertEquals(e, r, 0.5f) }
    }

    @Test
    fun extender_alarga_la_punta_tocada_hasta_el_rectangulo() {
        val v = vista()
        v.agregarRectanguloParaPruebas(300f, 0f, 500f, 400f)
        v.agregarLineaParaPruebas(50f, 200f, 150f, 200f)
        v.activarModoEdicion(SketchMedidasView.ModoEdicion.EXTENDER)
        // Se toca cerca de la punta derecha (150, 200).
        v.toqueDeEdicionParaPruebas(140f, 205f)
        cerca(listOf(50f, 200f, 300f, 200f), v.lineasParaPruebas().single())
    }

    @Test
    fun recortar_quita_el_trozo_tocado_entre_dos_bordes() {
        val v = vista()
        v.agregarRectanguloParaPruebas(300f, 0f, 500f, 400f)
        // Una línea que atraviesa el rectángulo de lado a lado.
        v.agregarLineaParaPruebas(100f, 200f, 700f, 200f)
        v.activarModoEdicion(SketchMedidasView.ModoEdicion.RECORTAR)
        // Se toca dentro del rectángulo: ese trozo desaparece y quedan los dos de fuera.
        v.toqueDeEdicionParaPruebas(400f, 203f)
        val lineas = v.lineasParaPruebas()
        assertEquals("tendrían que quedar dos trozos: $lineas", 2, lineas.size)
        cerca(listOf(100f, 200f, 300f, 200f), lineas[0])
        cerca(listOf(500f, 200f, 700f, 200f), lineas[1])
    }

    @Test
    fun mover_imantado_lleva_una_esquina_sobre_otra() {
        val v = vista()
        v.agregarRectanguloParaPruebas(0f, 0f, 100f, 100f)
        v.agregarLineaParaPruebas(300f, 300f, 400f, 300f)
        v.activarModoEdicion(SketchMedidasView.ModoEdicion.MOVER_IMAN)
        // Agarre cerca de la punta izquierda de la línea, destino cerca de la esquina inferior
        // derecha del rectángulo: la punta cae justo en (100, 100).
        v.toqueDeEdicionParaPruebas(305f, 296f)
        v.toqueDeEdicionParaPruebas(104f, 97f)
        cerca(listOf(100f, 100f, 200f, 100f), v.lineasParaPruebas().single())
    }
}
