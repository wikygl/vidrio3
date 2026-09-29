package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * El trapecio de la captura del usuario (2026-09-28): una cota a escuadra escrita en 198.3 pero
 * dibujada del largo del lado de 148.7. Al volver a abrirlo, todo el apunte salía por 198.3 / 148.7.
 */
@RunWith(AndroidJUnit4::class)
class CotaIncoherenteTest {
    private val ctx get() = ApplicationProvider.getApplicationContext<android.content.Context>()
    private val d get() = ctx.resources.displayMetrics.density
    private fun vista() = SketchMedidasView(ctx).apply {
        measure(android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.EXACTLY))
        layout(0, 0, 1080, 1920)
    }

    @Test
    fun la_cota_escrita_que_no_coincide_no_reescala_el_apunte() {
        var json = ""
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val v = vista()
            v.agregarPoligonoLibreParaPruebas(40f, 40f, 0f to 0f, 209.2f to 0f, 209.2f to 148.7f, 96.1f to 148.7f)
            val x = 40f + 96.1f * d
            assertTrue(v.cotaAEscuadraParaPruebas(x, 40f + 148.7f * d, x, 40f))
            json = v.exportEditableState()
        }
        // La cota escrita en 198.3 sin que el dibujo cambie, como en el apunte del usuario.
        val raiz = JSONObject(json)
        val elementos = raiz.getJSONArray("elements")
        var cambiadas = 0
        for (i in 0 until elementos.length()) {
            val e = elementos.getJSONObject(i)
            if (e.optString("type") == "shape" && e.has("lengthCm")) {
                e.put("lengthCm", 198.3); e.put("medidaEscrita", 198.3); cambiadas++
            }
        }
        assertEquals("tipos: " + (0 until elementos.length()).joinToString { elementos.getJSONObject(it).let { o -> o.optString("type") + "/" + o.optString("tool") + "/" + o.optString("cotaHint") } }, 1, cambiadas)
        // Con la escala guardada (apunte nuevo) y sin ella (apunte de antes del 2026-09-28).
        val nuevo = raiz.toString()
        val viejo = JSONObject(nuevo).apply { remove("pxPorCm") }.toString()
        for ((cual, apunte) in listOf("nuevo" to nuevo, "viejo" to viejo)) InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val otra = vista()
            assertTrue(otra.loadEditableState(apunte))
            val c = otra.contornoPrincipalEnCm()!!
            val ancho = c.maxOf { it.first } - c.minOf { it.first }
            assertEquals("apunte $cual: se reescaló por la cota: $c", 209.2f, ancho, 0.3f)
        }
    }
}
