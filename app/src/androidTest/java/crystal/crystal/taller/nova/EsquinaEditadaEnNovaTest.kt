package crystal.crystal.taller.nova

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.databinding.ActivityNovaCorredizaBinding
import crystal.crystal.taller.ColaCalculadoras
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Formas 13 (2026-09-29, hallado por el usuario): la L de 70 + la cara asimétrica (120 abajo, 189
 * arriba, alto 120) que viene de Medidas.
 * - La cara asimétrica tiene que llegar a Nova con su forma, no como el rectángulo de 189.
 * - Al volver de novaDiseño con la altura de puente editada en los dos lados, Calcular usa las dos.
 */
@RunWith(AndroidJUnit4::class)
class EsquinaEditadaEnNovaTest {

    private val formas13 = ColaCalculadoras.MedidaCalc(
        "Nova Corrediza", 259f, 138.4f, 1f, "Formas", "",
        esquina = "70,70,120,120,0;120,189,120,120,90@90"
    )

    private fun campo(o: Any, n: String) = o.javaClass.getDeclaredField(n).apply { isAccessible = true }
    private fun llamar(o: Any, n: String, vararg args: Any) =
        o.javaClass.getDeclaredMethod(n, *args.map { it.javaClass }.toTypedArray()).apply { isAccessible = true }.invoke(o, *args)

    private fun intent(): Intent {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        return Intent(ctx, NovaCorrediza::class.java).apply {
            putExtra("rcliente", "Formas"); putExtra("ancho", 259f); putExtra("alto", 138.4f)
            putExtra("cantidad", 1f); putExtra("producto", "Nova Corrediza")
            putExtra(ColaCalculadoras.EXTRA_DESDE_MEDIDAS, true)
            putExtra(ColaCalculadoras.EXTRA_BOCETO_PATH, "")
            putExtra(ColaCalculadoras.EXTRA_COLA, arrayListOf(formas13))
            putExtra(ColaCalculadoras.EXTRA_INDICE, 0)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    private fun <T> enPantalla(esc: ActivityScenario<NovaCorrediza>, bloque: (NovaCorrediza) -> T): T {
        var r: T? = null
        val latch = CountDownLatch(1)
        esc.onActivity { r = bloque(it); latch.countDown() }
        latch.await(20, TimeUnit.SECONDS)
        return r!!
    }

    @Test
    fun la_cara_asimetrica_llega_con_su_forma_y_los_dos_puentes_editados_calculan() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        if (!ProyectoManager.hayProyectoActivo()) ProyectoManager.setProyectoActivo(ctx, "revision")
        ActivityScenario.launch<NovaCorrediza>(intent()).use { esc ->
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            Thread.sleep(700)
            val paquete = enPantalla(esc) { a ->
                (campo(a, "binding").get(a) as ActivityNovaCorredizaBinding).btCalcular.performClick()
                ""
            }.let { Thread.sleep(1500); enPantalla(esc) { a -> campo(a, "ultimoPaquete").get(a) as String } }
            // La cara asimétrica, con su silueta (L<…>) en su tramo.
            val d = crystal.crystal.Diseno.nova.DisenoNova.desdePaquete(paquete)!!
            assertTrue("la cara asimétrica llegó sin forma: $paquete", d.tramos.any { it.contorno.size >= 3 })

            // Vuelve de novaDiseño con los dos puentes cambiados: 100 en el primer lado y 80 en el segundo.
            val editado = Regex("s<[0-9.]+>").findAll(paquete).map { it.value }.toList().let { ss ->
                var p = paquete
                p = p.replaceFirst(ss[0], "s<100>")
                p = p.replaceFirst(ss[1], "s<80>")
                p
            }
            val pf = enPantalla(esc) { a ->
                llamar(a, "recibirDisenoEditado", editado)
                val b = campo(a, "binding").get(a) as ActivityNovaCorredizaBinding
                b.btCalcular.performClick()
                ""
            }.let { Thread.sleep(1500); enPantalla(esc) { a -> (campo(a, "binding").get(a) as ActivityNovaCorredizaBinding).txPf.text.toString() } }
            assertTrue("portafelpa: $pf\n$editado", pf.lines().any { it.startsWith("98.4 = ") } && pf.lines().any { it.startsWith("78.4 = ") })
            // Y las referencias dicen la altura de puente de cada lado, no una sola.
            val refs = enPantalla(esc) { a ->
                val b = campo(a, "binding").get(a) as ActivityNovaCorredizaBinding
                b.txReferencias.text.toString() + "\n--detalle--\n" + (campo(a, "referenciasFull").get(a)?.toString() ?: "")
            }
            assertTrue(refs, refs.contains("An: 70") && refs.contains("An: 189"))
            assertTrue(refs, refs.contains("Altura de puente: 100") && refs.contains("Altura de puente: 80"))
        }
    }
}
