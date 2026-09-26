package crystal.crystal.Diseno.nova

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Pedido del usuario (2026-09-26): girar la pantalla sin querer no puede borrar lo diseñado, y el
 * editor necesita deshacer y rehacer.
 */
@RunWith(AndroidJUnit4::class)
class DisenoNovaHistorialTest {

    private fun intentCon(paquete: String): Intent {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        return Intent(ctx, DisenoNovaActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            putExtra(DisenoNovaActivity.EXTRA_PAQUETE, paquete)
        }
    }

    private fun esperar(ms: Long = 600) {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
        Thread.sleep(ms)
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }

    private fun <T> enPantalla(esc: ActivityScenario<DisenoNovaActivity>, bloque: (DisenoNovaActivity) -> T): T {
        var resultado: T? = null
        val latch = CountDownLatch(1)
        esc.onActivity { act -> resultado = bloque(act); latch.countDown() }
        latch.await(5, TimeUnit.SECONDS)
        @Suppress("UNCHECKED_CAST")
        return resultado as T
    }

    @Test
    fun girar_no_borra_lo_disenado_y_se_puede_deshacer_y_rehacer() {
        val inicial = DisenoNova.nuevo("apa", 300f, 150f, altoHoja = 100f).aPaquete()
        ActivityScenario.launch<DisenoNovaActivity>(intentCon(inicial)).use { esc ->
            esperar()
            val original = enPantalla(esc) { it.paqueteParaPruebas() }
            enPantalla(esc) { a -> a.editarParaPruebas { d -> d.conPuenteCambiado(0, 90f) } }
            esperar()
            val editado = enPantalla(esc) { it.paqueteParaPruebas() }
            assertNotEquals(original, editado)

            // Girar: la misma pantalla, con lo editado.
            val antes = enPantalla(esc) { System.identityHashCode(it) }
            enPantalla(esc) { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE }
            esperar(1500)
            assertEquals(antes, enPantalla(esc) { System.identityHashCode(it) })
            assertEquals(editado, enPantalla(esc) { it.paqueteParaPruebas() })

            // Deshacer vuelve al diseño de antes; rehacer, al editado.
            enPantalla(esc) { it.deshacerParaPruebas() }
            esperar()
            assertEquals(original, enPantalla(esc) { it.paqueteParaPruebas() })
            enPantalla(esc) { it.rehacerParaPruebas() }
            esperar()
            assertEquals(editado, enPantalla(esc) { it.paqueteParaPruebas() })

            enPantalla(esc) { it.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED }
            esperar()
        }
    }
}
