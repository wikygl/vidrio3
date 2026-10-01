package crystal.crystal.taller

import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Una medida con varias figuras sueltas (pedido del usuario el 2026-10-01). La de Fide trae tres
 * rectángulos: 214.5 × 157.5, 204 × 21 y 200 × 23. El apunte se parte en tres, cada uno con su
 * medida; una ventana de esquina, con sus paredes, sigue siendo una sola.
 */
@RunWith(AndroidJUnit4::class)
class FigurasDeUnaMedidaTest {

    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1600, View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    private fun asset(n: String) =
        InstrumentationRegistry.getInstrumentation().context.assets.open(n).bufferedReader().readText()

    @Test
    fun la_medida_de_fide_se_parte_en_sus_tres_rectangulos() {
        val v = vista()
        assertTrue(v.loadEditableState(asset("fide_tres_figuras.json")))
        val estados = v.estadosPorFigura()
        assertEquals(3, estados.size)
        val medidas = estados.map { e ->
            val w = vista(); assertTrue(w.loadEditableState(e))
            w.medidaPrincipal()!!.let { it.anchoCm to it.altoCm }
        }.sortedByDescending { it.second }
        assertEquals(214.5f to 157.5f, medidas[0])
        assertEquals(200f to 23f, medidas[1])
        assertEquals(204f to 21f, medidas[2])
    }

    /** No falla: deja en los archivos de la app qué medidas del celular tienen varias figuras. */
    @Test
    fun informe_de_las_medidas_con_varias_figuras() {
        val dir = java.io.File("/sdcard/Download/Crystal/Medidas")
        val sb = StringBuilder()
        dir.listFiles { f -> f.name.endsWith(".json") && !f.name.endsWith("_bocetos.json") }?.sortedBy { it.name }?.forEach { f ->
            val v = vista()
            if (!runCatching { v.loadEditableState(f.readText()) }.getOrDefault(false)) return@forEach
            val estados = v.estadosPorFigura()
            if (estados.size < 2) return@forEach
            val medidas = estados.map { e -> vista().also { it.loadEditableState(e) }.medidaPrincipal()?.let { "${it.anchoCm} x ${it.altoCm}" } }
            sb.append("${f.name}: ${estados.size} figuras -> $medidas\n")
        }
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        java.io.File(ctx.getExternalFilesDir(null), "medidas_con_varias_figuras.txt").writeText(sb.toString())
    }

    @Test
    fun una_ventana_de_esquina_no_se_parte() {
        val v = vista()
        assertTrue(v.loadEditableState(asset("formas_13_esquina.json")))
        assertTrue(v.estadosPorFigura().isEmpty())
    }
}
