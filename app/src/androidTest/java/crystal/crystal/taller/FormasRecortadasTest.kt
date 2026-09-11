package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

/**
 * Cortar y unir figuras en el apunte.
 *
 * Lo que se guarda de una figura es su contorno, y de ahí salen las medidas y los tramos de la
 * calculadora. Con dos cortes seguidos se guardaba solo el último: el dibujo se veía bien porque
 * usa el camino de verdad, pero el contorno se recalculaba desde las CAJAS de las dos figuras, y
 * la caja de una figura ya cortada vuelve a ser el rectángulo entero.
 */
@RunWith(AndroidJUnit4::class)
class FormasRecortadasTest {

    private fun vista() = SketchMedidasView(ApplicationProvider.getApplicationContext())

    /** Área del contorno por la fórmula del zapatero, en píxeles cuadrados. */
    private fun area(puntos: List<Pair<Float, Float>>): Float {
        if (puntos.size < 3) return 0f
        var suma = 0f
        for (i in puntos.indices) {
            val (x1, y1) = puntos[i]
            val (x2, y2) = puntos[(i + 1) % puntos.size]
            suma += x1 * y2 - x2 * y1
        }
        return abs(suma) / 2f
    }

    @Test
    fun dos_cortes_seguidos_se_guardan_los_dos() {
        val v = vista()
        // Un rectángulo de 400x300 con dos muescas: una arriba a la derecha y otra abajo a la
        // derecha, de 100x50 cada una.
        val base = v.agregarRectanguloParaPruebas(0f, 0f, 400f, 300f)
        val corteArriba = v.agregarRectanguloParaPruebas(300f, 0f, 400f, 50f)
        v.seleccionarParaPruebas(corteArriba, base)
        assertTrue("no se pudo cortar la primera vez", v.subtractSelected())

        val yaCortado = v.contornoGuardadoParaPruebas()
        assertEquals(
            "el primer corte no quedó guardado: $yaCortado",
            400f * 300f - 100f * 50f, area(yaCortado), 200f
        )

        val corteAbajo = v.agregarRectanguloParaPruebas(300f, 250f, 400f, 300f)
        v.seleccionarParaPruebas(corteAbajo, 0)
        assertTrue("no se pudo cortar la segunda vez", v.subtractSelected())

        val dosCortes = v.contornoGuardadoParaPruebas()
        assertEquals(
            "el segundo corte se comió al primero",
            400f * 300f - 2 * 100f * 50f,
            area(dosCortes),
            400f
        )
    }

    @Test
    fun unir_dos_rectangulos_guarda_la_forma_entera() {
        val v = vista()
        // Dos rectángulos que se tocan formando una L: 200x300 y 200x100 pegado abajo a la derecha.
        val a = v.agregarRectanguloParaPruebas(0f, 0f, 200f, 300f)
        val b = v.agregarRectanguloParaPruebas(200f, 200f, 400f, 300f)
        v.seleccionarParaPruebas(a, b)
        assertTrue("no se pudo unir", v.weldSelected())

        val unido = v.contornoGuardadoParaPruebas()
        assertEquals(
            "la unión no guarda las dos piezas: $unido",
            200f * 300f + 200f * 100f, area(unido), 400f
        )
        // Y la L tiene seis esquinas, no cuatro.
        assertTrue("la unión se guardó como un rectángulo: ${unido.size} puntos", unido.size >= 6)
    }
}
