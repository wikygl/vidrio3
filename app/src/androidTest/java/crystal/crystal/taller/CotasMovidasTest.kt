package crystal.crystal.taller

import android.os.SystemClock
import android.view.MotionEvent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Las cotas se pueden apartar con el dedo cuando estorban, y lo apartado se guarda con el apunte.
 */
@RunWith(AndroidJUnit4::class)
class CotasMovidasTest {

    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1600, android.view.View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    private fun toque(v: SketchMedidasView, accion: Int, x: Float, y: Float) {
        val t = SystemClock.uptimeMillis()
        val e = MotionEvent.obtain(t, t, accion, x, y, 0)
        v.onTouchEvent(e)
        e.recycle()
    }

    @Test
    fun arrastrar_una_cota_la_aparta_y_queda_guardado() {
        val v = vista()
        v.agregarRectanguloParaPruebas(200f, 300f, 600f, 600f)
        val antes = v.cotasDibujadasParaPruebas().firstOrNull { it.first == "RECT_TOP" }
        assertNotNull("el rectángulo no dibujó su cota de arriba", antes)
        val r = antes!!.second

        v.activarModoEdicion(SketchMedidasView.ModoEdicion.MOVER_COTA)
        val p0 = v.aPantallaParaPruebas(r.centerX(), r.centerY())
        val p1 = v.aPantallaParaPruebas(r.centerX(), r.centerY() - 120f)
        toque(v, MotionEvent.ACTION_DOWN, p0.x, p0.y)
        // Mantener el dedo: es lo que coge la cota.
        v.cogerCotaAhoraParaPruebas()
        toque(v, MotionEvent.ACTION_MOVE, p0.x, (p0.y + p1.y) / 2f)
        toque(v, MotionEvent.ACTION_MOVE, p1.x, p1.y)
        toque(v, MotionEvent.ACTION_UP, p1.x, p1.y)

        val despues = v.cotasDibujadasParaPruebas().first { it.first == "RECT_TOP" }.second
        assertEquals("la cota no subió lo arrastrado", r.centerY() - 120f, despues.centerY(), 3f)
        assertEquals("la cota no tenía que moverse de lado", r.centerX(), despues.centerX(), 3f)

        val json = v.exportEditableState()
        assertTrue("el apunte guardado no lleva la cota movida: $json", json.contains("\"ajustesCotas\":{\"RECT_TOP"))

        // Y al volver a cargarlo la cota sigue donde se dejó.
        val otra = vista()
        assertTrue(otra.loadEditableState(json))
        val cargada = otra.cotasDibujadasParaPruebas().first { it.first == "RECT_TOP" }.second
        assertEquals(despues.centerY(), cargada.centerY(), 3f)
    }

    /** Sin ningún modo puesto, el toque largo sobre una cota también la coge y la mueve. */
    @Test
    fun toque_largo_sin_modo_tambien_mueve_la_cota() {
        val v = vista()
        v.agregarRectanguloParaPruebas(200f, 300f, 600f, 600f)
        val r = v.cotasDibujadasParaPruebas().first { it.first == "RECT_LEFT" }.second
        val p0 = v.aPantallaParaPruebas(r.centerX(), r.centerY())
        val p1 = v.aPantallaParaPruebas(r.centerX() - 90f, r.centerY())
        toque(v, MotionEvent.ACTION_DOWN, p0.x, p0.y)
        v.cogerCotaAhoraParaPruebas()
        toque(v, MotionEvent.ACTION_MOVE, p1.x, p1.y)
        toque(v, MotionEvent.ACTION_UP, p1.x, p1.y)
        val despues = v.cotasDibujadasParaPruebas().first { it.first == "RECT_LEFT" }.second
        assertEquals("la cota no se fue a la izquierda", r.centerX() - 90f, despues.centerX(), 3f)
    }
}
