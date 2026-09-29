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
 * Un apunte dibujado en un celular se abre con sus medidas en otro de distinta densidad (pedido del
 * usuario, 2026-09-28): el escalonado de 420.6 × 117.9 dibujado en el Xiaomi (440 dpi) llegaba a
 * novaDiseño en el moto (280 dpi) como 660.9 × 185.2, todo por 440 / 280. La densidad del dibujo
 * se deducía de rectángulos, triángulos, círculos y líneas, y no de las formas cerradas: un apunte
 * con solo una forma no se corregía.
 */
@RunWith(AndroidJUnit4::class)
class DensidadDelApunteTest {

    private val ctx get() = ApplicationProvider.getApplicationContext<android.content.Context>()

    private fun vista() = SketchMedidasView(ctx).apply {
        measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1920, android.view.View.MeasureSpec.EXACTLY)
        )
        layout(0, 0, 1080, 1920)
    }

    /** El mismo apunte con los píxeles de un equipo [k] veces más denso: las medidas no cambian. */
    private fun dibujadoEnOtroEquipo(json: String, k: Float): String {
        fun escalar(v: Any?) {
            when (v) {
                is JSONObject -> {
                    if (v.optString("type") == "composite") {
                        val contornos = v.getJSONArray("contours")
                        for (c in 0 until contornos.length()) {
                            val puntos = contornos.getJSONArray(c)
                            for (p in 0 until puntos.length()) {
                                val xy = puntos.getJSONArray(p)
                                puntos.put(p, JSONArray().put(xy.getDouble(0) * k).put(xy.getDouble(1) * k))
                            }
                        }
                    }
                    v.keys().forEach { escalar(v.get(it)) }
                }
                is JSONArray -> for (i in 0 until v.length()) escalar(v.get(i))
            }
        }
        return JSONObject(json).also { raiz ->
            escalar(raiz)
            // Un apunte nuevo trae su escala: la del otro equipo. Uno viejo no la trae.
            if (raiz.has("pxPorCm")) raiz.put("pxPorCm", raiz.getDouble("pxPorCm") * k)
        }.toString()
    }

    @Test
    fun la_forma_dibujada_en_otro_equipo_conserva_sus_medidas() = comprobar(conEscala = true)

    /** Un apunte de antes del 2026-09-28, sin su escala: se deduce de los lados de la forma. */
    @Test
    fun el_apunte_viejo_dibujado_en_otro_equipo_conserva_sus_medidas() = comprobar(conEscala = false)

    private fun comprobar(conEscala: Boolean) {
        var json = ""
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val v = vista()
            v.agregarPoligonoLibreParaPruebas(
                40f, 40f,
                0f to 0f, 420.6f to 0f, 420.6f to 71.9f, 278.7f to 71.9f, 278.7f to 117.9f, 0f to 117.9f
            )
            json = v.exportEditableState()
        }
        if (!conEscala) json = JSONObject(json).apply { remove("pxPorCm") }.toString()
        assertTrue("no se guardó como forma: $json", json.contains("\"composite\""))
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val otro = vista()
            assertTrue(otro.loadEditableState(dibujadoEnOtroEquipo(json, 440f / 280f)))
            val contorno = otro.contornoPrincipalEnCm()!!
            val ancho = contorno.maxOf { it.first } - contorno.minOf { it.first }
            val alto = contorno.maxOf { it.second } - contorno.minOf { it.second }
            assertEquals("el ancho cambió al abrirlo en otro equipo: $contorno", 420.6f, ancho, 0.3f)
            assertEquals("el alto cambió al abrirlo en otro equipo: $contorno", 117.9f, alto, 0.3f)
        }
    }
}
