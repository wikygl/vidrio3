package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Pedido del usuario (2026-09-26) en MedidaActivity:
 * 1. Un contorno cerrado a mano con extender y recortar se queda en líneas sueltas: tiene que pasar
 *    a ser una forma, como al unir nodos.
 * 2. Una forma escalada se archivaba con el tamaño de antes: la medida del apunte salía de las cotas
 *    de apoyo (a escuadra) y no de la forma. El boceto de Formas (338.3 × 144.5) se archivaba como
 *    "256.4 x 109.4", que eran sus dos cotas interiores.
 */
@RunWith(AndroidJUnit4::class)
class ApunteFormasTest {

    private val ctx get() = ApplicationProvider.getApplicationContext<android.content.Context>()
    private val d get() = ctx.resources.displayMetrics.density

    private fun vista() = SketchMedidasView(ctx).apply {
        measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.EXACTLY)
        )
        layout(0, 0, 1080, 1920)
    }

    private fun enPantalla(bloque: () -> Unit) = InstrumentationRegistry.getInstrumentation().runOnMainSync(bloque)

    @Test
    fun extender_y_recortar_cierran_la_forma() = enPantalla {
        val v = vista()
        v.agregarLineaParaPruebas(100f, 100f, 600f, 100f)   // arriba: sobra de 100 a 200
        v.agregarLineaParaPruebas(600f, 100f, 600f, 600f)
        v.agregarLineaParaPruebas(600f, 600f, 200f, 600f)
        v.agregarLineaParaPruebas(200f, 600f, 200f, 160f)   // izquierda: no llega arriba
        v.activarModoEdicion(SketchMedidasView.ModoEdicion.EXTENDER)
        v.toqueDeEdicionParaPruebas(200f, 170f)
        v.activarModoEdicion(SketchMedidasView.ModoEdicion.RECORTAR)
        v.toqueDeEdicionParaPruebas(150f, 102f)
        assertEquals(emptyList<List<Float>>(), v.lineasParaPruebas())
        assertTrue(v.exportEditableState().contains("\"composite\""))
        assertEquals(4, v.contornoPrincipalEnCm()!!.size)
    }

    @Test
    fun la_forma_escalada_se_archiva_con_su_tamano() = enPantalla {
        val v = vista()
        // El escalonado de Formas, con una cota de apoyo de 109.4 dentro.
        val i = v.agregarPoligonoLibreParaPruebas(
            100f, 100f,
            0f to 67.3f, 104.6f to 67.3f, 104.6f to 0f, 338.3f to 0f,
            338.3f to 108.5f, 256.3f to 109.4f, 256.3f to 144.5f, 0f to 144.5f
        )
        val x = 100f + 256.3f * d
        v.cotaAEscuadraParaPruebas(x, 100f + 109.4f * d, x, 100f)
        assertEquals(1, v.cuantasCotasAEscuadraParaPruebas())
        val antes = v.medidaPrincipal()!!
        assertEquals(338.3f, antes.anchoCm, 0.3f)
        assertEquals(144.5f, antes.altoCm, 0.3f)

        v.seleccionarParaPruebas(i)
        v.scaleSelected(1.5f)
        val escalada = v.medidaPrincipal()!!
        assertEquals(338.3f * 1.5f, escalada.anchoCm, 0.5f)
        assertEquals(144.5f * 1.5f, escalada.altoCm, 0.5f)

        // Y al archivar (se guarda y se vuelve a cargar para mandarlo a la calculadora).
        val v2 = vista()
        v2.loadEditableState(v.exportEditableState())
        val recargada = v2.medidaPrincipal()!!
        assertEquals(escalada.anchoCm, recargada.anchoCm, 0.2f)
        assertEquals(escalada.altoCm, recargada.altoCm, 0.2f)
    }
}
