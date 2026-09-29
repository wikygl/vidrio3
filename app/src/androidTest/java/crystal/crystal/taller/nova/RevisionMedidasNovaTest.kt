package crystal.crystal.taller.nova

import android.content.Intent
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.databinding.ActivityNovaCorredizaBinding
import crystal.crystal.taller.ColaCalculadoras
import crystal.crystal.taller.EsquinaMedida
import crystal.crystal.taller.LadosLibres
import crystal.crystal.taller.SketchMedidasView
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Revisión pedida el 2026-09-29: cada medida de Nova Corrediza guardada en Medidas, por el mismo
 * camino que usa el vidriero (el apunte → la ficha de la cola → la calculadora → Calcular). No
 * falla: deja en los archivos de la app `revision_medidas_nova.txt` con lo que se ve en pantalla
 * (materiales, referencias, paquete del diseño) en aparente y en inaparente, para revisarlo contra
 * docs/REGLAS_NOVA.md.
 *
 * Solo corre en un celular que tenga las medidas (`Descargas/Crystal/Medidas`); en los demás no
 * encuentra nada y escribe el informe vacío.
 */
@RunWith(AndroidJUnit4::class)
class RevisionMedidasNovaTest {

    private val dir = File("/sdcard/Download/Crystal/Medidas")

    private fun campo(o: Any, n: String) = o.javaClass.getDeclaredField(n).apply { isAccessible = true }
    private fun llamar(o: Any, n: String) = o.javaClass.getDeclaredMethod(n).apply { isAccessible = true }.invoke(o)

    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(1600, View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    /** Las medidas de Nova de cada proyecto de Medidas, sin repetir el mismo archivo. */
    private fun medidasNova(): List<Pair<String, File>> {
        val out = mutableListOf<Pair<String, File>>()
        dir.listFiles { f -> f.name.endsWith("_bocetos.json") }?.sortedBy { it.name }?.forEach { b ->
            val items = runCatching { JSONObject(b.readText()).getJSONArray("items") }.getOrNull() ?: return@forEach
            for (i in 0 until items.length()) {
                val it = items.getJSONObject(i)
                if (!it.optString("producto").contains("Nova", true)) continue
                val f = File(dir, it.optString("archivo"))
                if (f.exists()) out.add(b.name.removeSuffix("_bocetos.json") to f)
            }
        }
        return out
    }

    /** Lo mismo que arma MedidaActivity.construirColaCalculadoras para la cola. */
    private fun ficha(cliente: String, f: File): ColaCalculadoras.MedidaCalc? {
        val v = vista()
        if (!v.loadEditableState(f.readText())) return null
        val medida = v.medidaPrincipal() ?: return null
        return ColaCalculadoras.MedidaCalc(
            producto = "Nova Corrediza", ancho = medida.anchoCm, alto = medida.altoCm, cantidad = 1f,
            cliente = cliente, bocetoArchivo = "",
            contorno = v.contornoPrincipalEnCm()?.let { crystal.crystal.Diseno.nova.ContornoEnTramos.aTexto(it) }.orEmpty(),
            esquina = v.esquinaPrincipalEnCm()?.let { EsquinaMedida.aTexto(it) }.orEmpty(),
            contornosLados = LadosLibres.contornosATexto(v.contornosDeLadosEnCm()),
            parantesLados = LadosLibres.parantesATexto(v.parantesDeLadosEnCm()),
            forma = if (v.esCircularPrincipal()) "circular" else ""
        )
    }

    private fun intentDe(item: ColaCalculadoras.MedidaCalc): Intent {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        return Intent(ctx, NovaCorrediza::class.java).apply {
            putExtra("rcliente", item.cliente)
            putExtra("ancho", item.ancho)
            putExtra("alto", item.alto)
            putExtra("cantidad", 1f)
            putExtra("producto", item.producto)
            putExtra(ColaCalculadoras.EXTRA_DESDE_MEDIDAS, true)
            putExtra(ColaCalculadoras.EXTRA_BOCETO_PATH, "")
            putExtra(ColaCalculadoras.EXTRA_COLA, arrayListOf(item))
            putExtra(ColaCalculadoras.EXTRA_INDICE, 0)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    private fun fila(nombre: String, tx: TextView, contenedor: View?): String {
        val t = tx.text?.toString().orEmpty().trim()
        if (t.isBlank()) return ""
        val oculto = contenedor != null && contenedor.visibility != View.VISIBLE
        return "  [$nombre${if (oculto) " (oculto)" else ""}]\n" + t.lines().joinToString("\n") { "    $it" } + "\n"
    }

    private fun enPantalla(esc: ActivityScenario<NovaCorrediza>, bloque: (NovaCorrediza) -> String): String {
        var r = ""
        val latch = CountDownLatch(1)
        esc.onActivity { r = runCatching { bloque(it) }.getOrElse { e -> "!! ${e.cause ?: e}\n" }; latch.countDown() }
        latch.await(20, TimeUnit.SECONDS)
        return r
    }

    private fun lectura(a: NovaCorrediza): String {
        val b = campo(a, "binding").get(a) as ActivityNovaCorredizaBinding
        val sb = StringBuilder()
        sb.append("  estado: texto=${campo(a, "texto").get(a)} modulacion=${campo(a, "textoModelo").get(a)} " +
            "remate=${campo(a, "modeloRemate").get(a)} acabado=${campo(a, "tipoNova").get(a)} " +
            "puente=${campo(a, "puente").get(a)} (${campo(a, "tubo").get(a)}) U=${b.etU.text} cruce=${b.etCruce.text} " +
            "ancho=${b.etAncho.text} alto=${b.etAlto.text} hoja=${b.etHoja.text} partes=${b.etPartes.text} " +
            "desiguales=${(campo(a, "modulosDesiguales").get(a) as List<*>).size}\n")
        sb.append("  paquete: ${campo(a, "ultimoPaquete").get(a)}\n")
        // ¿El motor pudo? aplicarMotor se calla si falla y deja lo de las rutas viejas.
        val motor = runCatching {
            val v = if ((campo(a, "modulosDesiguales").get(a) as List<*>).isNotEmpty()) llamar(a, "ventanaMotorDesigual") else llamar(a, "ventanaMotorAutomatica")
            if (v == null) "sin ventana" else { crystal.crystal.taller.nova.motor.MotorNova.calcular(v as crystal.crystal.taller.nova.motor.VentanaNova); "ok" }
        }.getOrElse { "FALLÓ: ${it.cause ?: it}" }
        sb.append("  motor: $motor\n")
        sb.append(fila("U", b.txU, null))
        sb.append(fila(b.tvP.text.toString(), b.txP, b.mulLayout))
        sb.append(fila("riel", b.txR, b.lyRiel))
        sb.append(fila(b.tvFc.text.toString(), b.txFc, b.fcLayout))
        sb.append(fila(b.tvUf.text.toString(), b.txUf, b.lyUf))
        sb.append(fila("portafelpa", b.txPf, b.lyPf))
        sb.append(fila("tee", b.txTe, b.tLayout))
        sb.append(fila("tope", b.txTo, b.lyTo))
        sb.append(fila("hache", b.txH, b.lyH))
        sb.append(fila(b.tvT.text.toString(), b.txT, b.lyTubo))
        sb.append(fila("falso puente", b.txFalsoPuente, b.lyFalsoPuente))
        sb.append(fila("vidrios", b.txV, null))
        sb.append(fila("referencias", b.txReferencias, null))
        return sb.toString()
    }

    @Test
    fun revisar_todas_las_medidas_de_nova() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        if (!ProyectoManager.hayProyectoActivo()) ProyectoManager.setProyectoActivo(ctx, "revision")
        val informe = StringBuilder()
        val vistos = mutableSetOf<String>()
        for ((proyecto, f) in medidasNova()) {
            val item = runCatching { ficha(proyecto, f) }.getOrNull()
            informe.append("==================== $proyecto / ${f.name}\n")
            if (item == null) { informe.append("  !! el apunte no dio medida\n"); continue }
            val clave = listOf(item.ancho, item.alto, item.contorno, item.esquina, item.contornosLados, item.forma).joinToString("|")
            if (!vistos.add(clave)) { informe.append("  (igual a una medida anterior)\n"); continue }
            informe.append("  ficha: ${item.ancho} x ${item.alto} forma='${item.forma}' contorno='${item.contorno}' " +
                "esquina='${item.esquina}' lados='${item.contornosLados}' parantes='${item.parantesLados}'\n")
            for (acabado in listOf(NovaCorrediza.TipoNova.APA, NovaCorrediza.TipoNova.INA)) {
                informe.append(" --- $acabado\n")
                ActivityScenario.launch<NovaCorrediza>(intentDe(item)).use { esc ->
                    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
                    Thread.sleep(700)
                    informe.append(enPantalla(esc) { a ->
                        val b = campo(a, "binding").get(a) as ActivityNovaCorredizaBinding
                        // Como el vidriero: tocar el título pasa de aparente a inaparente (y cambia el
                        // acabado del diseño cargado). El puente de inaparente es el tubo 2⅜ (N-03).
                        if (acabado == NovaCorrediza.TipoNova.INA && campo(a, "tipoNova").get(a) != acabado) {
                            b.tvTitulo.performClick()
                            campo(a, "puente").set(a, "tubo 2⅜ x 1"); campo(a, "tubo").set(a, 2.5f)
                        }
                        if ((campo(a, "texto").get(a) as String).isEmpty()) campo(a, "texto").set(a, "nn")
                        b.btCalcular.performClick()
                        ""
                    })
                    InstrumentationRegistry.getInstrumentation().waitForIdleSync()
                    Thread.sleep(1500)
                    informe.append(enPantalla(esc) { lectura(it) })
                }
            }
        }
        File(ctx.getExternalFilesDir(null), "revision_medidas_nova.txt").writeText(informe.toString())
    }
}
