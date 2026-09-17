package crystal.crystal.taller

import android.graphics.PointF
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
    /**
     * Las esquinas de la unión tienen que quedar DONDE ESTÁN. El contorno se recuperaba muestreando
     * el camino cada 4 px y cada esquina se corría hasta 4 px (1.5 cm): 180 salía 178.3.
     */
    @Test
    fun unir_deja_las_esquinas_exactas() {
        val v = vista()
        // Medidas que no son múltiplo del paso de 4 px del muestreo viejo, para que se note.
        val a = v.agregarRectanguloParaPruebas(0f, 0f, 203f, 301f)
        val b = v.agregarRectanguloParaPruebas(203f, 201f, 405f, 301f)
        v.seleccionarParaPruebas(a, b)
        assertTrue("no se pudo unir", v.weldSelected())

        val unido = v.contornoGuardadoParaPruebas()
        assertEquals("la L tiene seis esquinas: $unido", 6, unido.size)
        val esperadas = listOf(0f to 0f, 203f to 0f, 203f to 201f, 405f to 201f, 405f to 301f, 0f to 301f)
        esperadas.forEach { (x, y) ->
            assertTrue(
                "falta la esquina ($x, $y) en $unido",
                unido.any { abs(it.first - x) < 0.1f && abs(it.second - y) < 0.1f }
            )
        }
    }

    /**
     * Dos rectángulos irregulares —lados de distinto largo, como los que salen de escribir 180 en
     * un lado y 181 en el otro— unidos siguen siendo irregulares: la unión no los aplana.
     */
    @Test
    fun unir_irregulares_no_los_aplana() {
        val v = vista()
        // El de la izquierda mide 300 por la izquierda y 303 por la derecha; el de la derecha
        // arranca en esa misma arista y su lado derecho mide 100 frente a 103 del izquierdo.
        val a = v.agregarCuadrilateroParaPruebas(
            PointF(0f, 0f), PointF(200f, 0f), PointF(200f, 303f), PointF(0f, 300f)
        )
        val b = v.agregarCuadrilateroParaPruebas(
            PointF(200f, 200f), PointF(400f, 200f), PointF(400f, 300f), PointF(200f, 303f)
        )
        v.seleccionarParaPruebas(a, b)
        assertTrue("no se pudo unir", v.weldSelected())

        val unido = v.contornoGuardadoParaPruebas()
        assertEquals("la unión tiene siete esquinas, con el pico de abajo: $unido", 7, unido.size)
        val esperadas = listOf(0f to 0f, 200f to 0f, 200f to 200f, 400f to 200f, 400f to 300f, 0f to 300f)
        esperadas.forEach { (x, y) ->
            assertTrue(
                "falta la esquina ($x, $y) en $unido",
                unido.any { abs(it.first - x) < 0.1f && abs(it.second - y) < 0.1f }
            )
        }
        // La arista de abajo va de (0,300) a (400,300) pasando por (200,303): no es recta, y así
        // tiene que quedar. Si se aplanara, (200,303) desaparecería.
        assertTrue(
            "el pico de abajo en (200, 303) se aplanó: $unido",
            unido.any { abs(it.first - 200f) < 0.1f && abs(it.second - 303f) < 0.1f }
        )
    }
}
