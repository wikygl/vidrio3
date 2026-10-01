package crystal.crystal.taller

import android.app.Activity
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import crystal.crystal.casilla.ProyectoManager
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Revisión pedida el 2026-09-29, la misma que se hizo con Nova (RevisionMedidasNovaTest), para las
 * demás calculadoras: cada medida guardada en Medidas que no es Nova, por el camino del vidriero (el
 * apunte → la ficha de la cola → su calculadora → Calcular). No falla: deja en los archivos de la
 * app `revision_medidas_calculadoras.txt` con lo que llega (la ficha, con su forma) y lo que se ve en
 * pantalla después de calcular, para revisarlo calculadora por calculadora.
 */
@RunWith(AndroidJUnit4::class)
class RevisionMedidasCalculadorasTest {

    private val dir = File("/sdcard/Download/Crystal/Medidas")

    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1600, View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    private data class Medida(val proyecto: String, val producto: String, val archivo: File)

    private fun medidas(): List<Medida> {
        val out = mutableListOf<Medida>()
        dir.listFiles { f -> f.name.endsWith("_bocetos.json") }?.sortedBy { it.name }?.forEach { b ->
            val items = runCatching { JSONObject(b.readText()).getJSONArray("items") }.getOrNull() ?: return@forEach
            for (i in 0 until items.length()) {
                val it = items.getJSONObject(i)
                val producto = it.optString("producto")
                if (producto.contains("Nova", true)) continue
                val f = File(dir, it.optString("archivo"))
                if (f.exists()) out.add(Medida(b.name.removeSuffix("_bocetos.json"), producto, f))
            }
        }
        return out
    }

    /** Lo mismo que arma MedidaActivity.construirColaCalculadoras. */
    private fun ficha(m: Medida): ColaCalculadoras.MedidaCalc? {
        val v = vista()
        if (!v.loadEditableState(m.archivo.readText())) return null
        val medida = v.medidaPrincipal() ?: return null
        return ColaCalculadoras.MedidaCalc(
            producto = m.producto, ancho = medida.anchoCm, alto = medida.altoCm, cantidad = 1f,
            cliente = m.proyecto, bocetoArchivo = "",
            contorno = v.contornoPrincipalEnCm()?.let { crystal.crystal.Diseno.nova.ContornoEnTramos.aTexto(it) }.orEmpty(),
            esquina = v.esquinaPrincipalEnCm()?.let { EsquinaMedida.aTexto(it) }.orEmpty(),
            contornosLados = LadosLibres.contornosATexto(v.contornosDeLadosEnCm()),
            parantesLados = LadosLibres.parantesATexto(v.parantesDeLadosEnCm()),
            disenoRopero = v.roperoDelDibujo()?.aJson().orEmpty(),
            forma = if (v.esCircularPrincipal()) "circular" else ""
        )
    }

    private fun intentDe(destino: Class<*>, item: ColaCalculadoras.MedidaCalc): Intent {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        return Intent(ctx, destino).apply {
            putExtra("rcliente", item.cliente)
            putExtra("ancho", item.ancho)
            putExtra("alto", item.alto)
            putExtra("cantidad", item.cantidad)
            putExtra("producto", item.producto)
            putExtra(ColaCalculadoras.EXTRA_DESDE_MEDIDAS, true)
            putExtra(ColaCalculadoras.EXTRA_BOCETO_PATH, "")
            putExtra(ColaCalculadoras.EXTRA_COLA, arrayListOf(item))
            putExtra(ColaCalculadoras.EXTRA_INDICE, 0)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    private fun nombre(v: View): String =
        if (v.id == View.NO_ID) "" else runCatching { v.resources.getResourceEntryName(v.id) }.getOrDefault("")

    private fun todas(v: View, out: MutableList<View> = mutableListOf()): List<View> {
        out.add(v)
        if (v is ViewGroup) for (i in 0 until v.childCount) todas(v.getChildAt(i), out)
        return out
    }

    private fun visible(v: View): Boolean {
        var x: View? = v
        while (x != null) {
            if (x.visibility != View.VISIBLE) return false
            x = x.parent as? View
        }
        return true
    }

    private fun lectura(a: Activity): String {
        val sb = StringBuilder()
        val vistas = todas(a.window.decorView)
        val entradas = vistas.filterIsInstance<EditText>().filter { visible(it) && it.text.isNotBlank() }
        sb.append("  entradas: ").append(entradas.joinToString(" ") { "${nombre(it)}=${it.text}" }).append("\n")
        vistas.filter { it is TextView && it !is EditText && visible(it) }.map { it as TextView }
            .filter { it.text.isNotBlank() && it.text.length > 1 && (it.text.any { c -> c.isDigit() } || it.text.contains("\n")) }
            .forEach { t ->
                val texto = t.text.toString().trim()
                sb.append("  [${nombre(t)}]").append(if (texto.contains("\n")) "\n" + texto.lines().joinToString("\n") { "    $it" } else " $texto").append("\n")
            }
        return sb.toString()
    }

    private fun enPantalla(esc: ActivityScenario<*>, bloque: (Activity) -> String): String {
        var r = ""
        val latch = CountDownLatch(1)
        esc.onActivity { r = runCatching { bloque(it as Activity) }.getOrElse { e -> "  !! ${e.cause ?: e}\n" }; latch.countDown() }
        latch.await(20, TimeUnit.SECONDS)
        return r
    }

    @Test
    fun revisar_todas_las_medidas_de_las_demas_calculadoras() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        if (!ProyectoManager.hayProyectoActivo()) ProyectoManager.setProyectoActivo(ctx, "revision")
        val informe = StringBuilder()
        val vistos = mutableSetOf<String>()
        for (m in medidas()) {
            informe.append("==================== ${m.producto} · ${m.proyecto} / ${m.archivo.name}\n")
            val item = runCatching { ficha(m) }.getOrNull()
            if (item == null) { informe.append("  !! el apunte no dio medida\n"); continue }
            val clave = listOf(m.producto, item.ancho, item.alto, item.contorno, item.esquina, item.forma).joinToString("|")
            if (!vistos.add(clave)) { informe.append("  (igual a una medida anterior)\n"); continue }
            informe.append("  ficha: ${item.ancho} x ${item.alto} forma='${item.forma}' contorno='${item.contorno}' " +
                "esquina='${item.esquina}' ropero=${item.disenoRopero.isNotBlank()}\n")
            val destino = EnrutadorPresupuesto.destinoPara(m.producto)
            if (destino == null) { informe.append("  (sin calculadora para este producto)\n"); continue }
            informe.append("  calculadora: ${destino.simpleName}\n")
            val abierta = runCatching { ActivityScenario.launch<Activity>(intentDe(destino, item)) }
            val esc = abierta.getOrNull()
            if (esc == null) { informe.append("  !! no abrió: ${abierta.exceptionOrNull()}\n"); continue }
            esc.use {
                InstrumentationRegistry.getInstrumentation().waitForIdleSync()
                Thread.sleep(700)
                informe.append(enPantalla(esc) { a ->
                    val boton = todas(a.window.decorView).firstOrNull {
                        Regex("(?i)^bt(n)?_?calcular[a-z]?$").matches(nombre(it)) && visible(it)
                    }
                    if (boton == null) "  !! sin botón de calcular\n" else { boton.performClick(); "" }
                })
                InstrumentationRegistry.getInstrumentation().waitForIdleSync()
                Thread.sleep(1200)
                informe.append(enPantalla(esc) { lectura(it) })
            }
        }
        File(ctx.getExternalFilesDir(null), "revision_medidas_calculadoras.txt").writeText(informe.toString())
    }
}
