package crystal.crystal.taller.nova

import android.content.Intent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import crystal.crystal.R
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.databinding.ActivityNovaCorredizaBinding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * La esquina de la L escrita a mano (dictado el 2026-10-02): con las casillas de esquina curva
 * vacías la esquina es en ángulo; con el desarrollo escrito, curva, por el mismo camino que la
 * esquina curva que llega de Medidas. Y el lado que se escribe se nombra junto a sus casillas.
 */
@RunWith(AndroidJUnit4::class)
class EsquinaCurvaEscritaTest {

    private fun campo(o: Any, n: String) = o.javaClass.getDeclaredField(n).apply { isAccessible = true }

    private fun <T> enPantalla(esc: ActivityScenario<NovaCorrediza>, bloque: (NovaCorrediza, ActivityNovaCorredizaBinding) -> T): T {
        var r: T? = null
        val latch = CountDownLatch(1)
        esc.onActivity { r = bloque(it, campo(it, "binding").get(it) as ActivityNovaCorredizaBinding); latch.countDown() }
        latch.await(20, TimeUnit.SECONDS)
        return r!!
    }

    private fun intent(): Intent {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        return Intent(ctx, NovaCorrediza::class.java).apply {
            putExtra("rcliente", "revision"); putExtra("cantidad", 1f); putExtra("producto", "Nova Corrediza")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    @Test
    fun la_esquina_vacia_es_en_angulo_y_con_desarrollo_es_curva() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        if (!ProyectoManager.hayProyectoActivo()) ProyectoManager.setProyectoActivo(ctx, "revision")
        ActivityScenario.launch<NovaCorrediza>(intent()).use { esc ->
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            Thread.sleep(700)
            enPantalla(esc) { _, b ->
                b.itemVenl.performClick()
                b.etAncho.setText("150"); b.etAlto.setText("100"); b.etHoja.setText("80")
                b.btAgregar.performClick()
                b.etAncho.setText("120"); b.etAlto.setText("100"); b.etHoja.setText("80")
            }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            // El lado que se escribe va con sus casillas; el título queda limpio y "Otros datos" no está.
            enPantalla(esc) { a, b ->
                assertEquals("Lado 2", b.tvLadoActual.text.toString())
                assertEquals(View.VISIBLE, b.tvLadoActual.visibility)
                assertEquals(a.getString(R.string.medidas_y_cantidad), b.tvMedidas.text.toString())
                assertEquals(View.GONE, b.txDatos.visibility)
                assertEquals(View.VISIBLE, b.tvEsquinaCurva.visibility)
                assertEquals(View.VISIBLE, b.lyDesarrollo.visibility)
            }
            val captura = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
            java.io.FileOutputStream(java.io.File(ctx.getExternalFilesDir(null), "esquina_curva_escrita.png")).use {
                captura?.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
            }

            // Vacías: en ángulo.
            enPantalla(esc) { _, b -> b.etFlecha.setText(""); b.etCuerda.setText(""); b.etDesarrollo.setText(""); b.btCalcular.performClick() }
            Thread.sleep(1500)
            val enAngulo = enPantalla(esc) { a, _ -> campo(a, "ultimoPaquete").get(a) as String }
            assertTrue("no salió en ángulo: $enAngulo", enAngulo.contains("A<90>"))
            assertFalse("salió curva sin casillas: $enAngulo", enAngulo.contains("Q<"))

            // Con el desarrollo: curva, un cuarto de círculo de 90 de desarrollo.
            enPantalla(esc) { _, b -> b.etDesarrollo.setText("90"); b.btCalcular.performClick() }
            Thread.sleep(1500)
            val curva = enPantalla(esc) { a, b -> (campo(a, "ultimoPaquete").get(a) as String) to b.txP.text.toString() }
            assertTrue("no salió curva: ${curva.first}", curva.first.contains("Q<"))
            // El puente de la curva: su desarrollo, 90 (N-84).
            assertTrue("puente: ${curva.second}", curva.second.lines().any { it.startsWith("90 = ") })
            // La imagen de Nova, en perspectiva con sus piezas (2026-10-02): se guarda para mirarla.
            Thread.sleep(2500)
            InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()?.let { bmp ->
                java.io.FileOutputStream(java.io.File(ctx.getExternalFilesDir(null), "esquina_curva_perspectiva.png")).use {
                    bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
                }
            }

            // Y al vaciarlas vuelve al ángulo.
            enPantalla(esc) { _, b -> b.etDesarrollo.setText(""); b.btCalcular.performClick() }
            Thread.sleep(1500)
            val otraVez = enPantalla(esc) { a, _ -> campo(a, "ultimoPaquete").get(a) as String }
            assertFalse("se quedó curva: $otraVez", otraVez.contains("Q<"))
            // Y otra vez curva, para que la última imagen que queda sea la de la esquina curva.
            enPantalla(esc) { _, b -> b.etDesarrollo.setText("90"); b.btCalcular.performClick() }
            Thread.sleep(2500)
            // Desde afuera, la opción del diálogo (por defecto se mira desde adentro).
            enPantalla(esc) { a, _ ->
                campo(a, "vistaExterior").set(a, true)
                a.javaClass.getDeclaredMethod("redibujarDiseno").apply { isAccessible = true }.invoke(a)
                Unit
            }
            Thread.sleep(2500)
        }
    }
}
