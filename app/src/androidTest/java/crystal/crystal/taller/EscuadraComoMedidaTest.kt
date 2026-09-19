package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Las cotas a escuadra como medidas de apoyo de un polígono libre.
 *
 * El caso es el polígono de prueba P5: un solo ángulo recto y tres lados inclinados (50, 150 y
 * 100), que con los lados solos no queda determinado. Con el ancho total (150) y el alto total
 * (160) medidos a escuadra, tiene que salir exacto: C en (150, 40) y D en (60, 160) contados
 * desde la esquina recta A. Aquí el eje y va hacia abajo, así que A está abajo a la izquierda.
 */
@RunWith(AndroidJUnit4::class)
class EscuadraComoMedidaTest {

    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1600, android.view.View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    private fun cerca(nombre: String, esperado: Pair<Float, Float>, real: Pair<Float, Float>) {
        assertEquals("$nombre en x", esperado.first, real.first, 0.3f)
        assertEquals("$nombre en y", esperado.second, real.second, 0.3f)
    }

    @Test
    fun p5_con_ancho_y_alto_a_escuadra_sale_exacto() {
        val v = vista()
        // El boceto, a dedo: las inclinaciones no son las reales.
        v.agregarPoligonoLibreParaPruebas(
            300f, 300f,
            0f to 160f, 120f to 160f, 160f to 130f, 70f to 10f, 0f to 80f
        )
        val caja = v.cajaDelCompositeParaPruebas()
        val contorno = v.contornoDelCompositeParaPruebas()
        val tiron = v.cmAPixelesParaPruebas(40f)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            // Ancho total: de C hacia la izquierda, al lado EA.
            val cx = caja.first + v.cmAPixelesParaPruebas(contorno[2].first)
            val cy = caja.second + v.cmAPixelesParaPruebas(contorno[2].second)
            v.cotaAEscuadraParaPruebas(cx + 6f, cy + 6f, cx - tiron, cy)
            // Alto total: de D hacia abajo, al lado AB.
            val dx = caja.first + v.cmAPixelesParaPruebas(contorno[3].first)
            val dy = caja.second + v.cmAPixelesParaPruebas(contorno[3].second)
            v.cotaAEscuadraParaPruebas(dx + 6f, dy + 6f, dx, dy + tiron)
        }
        assertEquals("faltan cotas a escuadra", 2, v.cuantasCotasAEscuadraParaPruebas())

        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            v.escribirEscuadraParaPruebas(150f, 0)
            v.escribirEscuadraParaPruebas(160f, 1)
            v.anotarLadoParaPruebas(0, 120f)
            v.anotarLadoParaPruebas(1, 50f)
            v.anotarLadoParaPruebas(2, 150f)
            v.anotarLadoParaPruebas(3, 100f)
            v.anotarLadoParaPruebas(4, 80f)
        }

        val ahora = v.contornoDelCompositeParaPruebas()
        assertEquals(5, ahora.size)
        cerca("A", 0f to 160f, ahora[0])
        cerca("B", 120f to 160f, ahora[1])
        cerca("C", 150f to 120f, ahora[2])
        cerca("D", 60f to 0f, ahora[3])
        cerca("E", 0f to 80f, ahora[4])
        // Y las escuadras dicen lo escrito, porque la forma las cumple.
        assertEquals(150f, v.medidaAEscuadraParaPruebas()!!, 0.3f)
    }
}
