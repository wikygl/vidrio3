package crystal.crystal.Diseno.nova

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * La L de Porla (2026-10-21): la frontal de 164 con un costado circular, colgada 32 del dintel, y la
 * derecha de 164 en la esquina y 196 en la punta. El editor se cerraba al poner la cota del arco y el
 * lado inclinado bajaba su silueta 32 de más. Deja las imágenes en `files/prueba_*.png`.
 */
@RunWith(AndroidJUnit4::class)
class EsquinaCircularInclinadaTest {

    /** El costado izquierdo de la frontal, en arco: cuerda 164 y flecha 15, en 12 trozos. */
    private fun siluetaCircular(): String {
        val c = 164.0; val f = 15.0
        val r = (c * c / 4 + f * f) / (2 * f)
        val cx = r; val cy = c / 2
        val medio = Math.asin(c / 2 / r)
        val arco = (0..12).map { k ->
            val t = Math.PI - medio + 2 * medio * k / 12
            (cx + r * Math.cos(t)) to (cy - r * Math.sin(t))
        }
        val puntos = listOf(arco.first().first to 0.0, 185.1 to 0.0, 185.1 to 164.0) + arco.reversed().drop(0)
        return "L<" + puntos.distinct().joinToString("|") { (x, y) -> "%.1f/%.1f".format(java.util.Locale.US, x, y) } + ">"
    }

    private fun paquete() = "{nova,apa,[185.1,196:Tl<185.1>(H<164>;D<32>;${siluetaCircular()};s<117.1>(fccf);m<46.8>(ff)) A<90> " +
        "Tl<243.5>(H<164,196>;D<32,0>;L<0/32|246/0|246/196|0/196>;s<140>(fcfcf);m<56>(ff))]}"

    private fun esperar(ms: Long = 600) {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Thread.sleep(ms)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun <T> en(esc: ActivityScenario<DisenoNovaActivity>, bloque: (DisenoNovaActivity) -> T): T {
        var r: T? = null
        val latch = CountDownLatch(1)
        esc.onActivity { r = bloque(it); latch.countDown() }
        latch.await(5, TimeUnit.SECONDS)
        @Suppress("UNCHECKED_CAST")
        return r as T
    }

    private fun guardar(esc: ActivityScenario<DisenoNovaActivity>, nombre: String) {
        en(esc) { a ->
            val bmp = a.dibujoParaPruebas()
            File(a.filesDir, "prueba_$nombre.png").outputStream().use { bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it) }
        }
    }

    @Test
    fun la_l_circular_e_inclinada_se_abre_y_cada_lado_queda_en_su_sitio() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = Intent(ctx, DisenoNovaActivity::class.java)
            .putExtra(DisenoNovaActivity.EXTRA_PAQUETE, paquete())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        ActivityScenario.launch<DisenoNovaActivity>(intent).use { esc ->
            esperar()
            assertEquals("no se abrió por lados", 2, en(esc) { it.ladosParaPruebas() })
            guardar(esc, "lado1")

            en(esc) { it.irAlLadoParaPruebas(1) }
            esperar()
            val vano = en(esc) { it.contornoVanoParaPruebas() }
            assertTrue("el lado inclinado no trae su silueta: $vano", vano.size >= 4)
            assertEquals("la silueta se sale por debajo: $vano", 196f, vano.maxOf { it.second }, 0.5f)
            assertEquals("la esquina no cuelga sus 32: $vano", 32f, vano.first().second, 0.5f)
            guardar(esc, "lado2")

            en(esc) { it.alternarVistaEnteraParaPruebas() }
            esperar()
            guardar(esc, "entera")
        }
    }
}
