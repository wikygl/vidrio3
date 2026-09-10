package crystal.crystal.Diseno.nova

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * El dibujo tiene que aceptar lo que escribe el modelo.
 *
 * `actualizarVista` se traga el fallo y dibuja un diseño de emergencia de un solo fijo, así que un
 * paquete rechazado se ve en pantalla como "no pasa nada" aunque el diseño interno sea correcto.
 * Estas pruebas miran justo eso, que es lo que las anteriores no veían.
 */
@RunWith(AndroidJUnit4::class)
class VistaAceptaModeloTest {

    private fun escenario(): ActivityScenario<DisenoNovaActivity> {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val i = Intent(ctx, DisenoNovaActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        return ActivityScenario.launch(i)
    }

    private fun esperar(ms: Long = 500) {
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

    @Test
    fun la_vista_acepta_lo_que_escribe_el_modelo() {
        val casos = linkedMapOf(
            "recién creado inaparente" to DisenoNova.nuevo("ina", 150f, 120f, 120f),
            "recién creado aparente" to DisenoNova.nuevo("apa", 240f, 200f, 200f),
            "con mocheta" to DisenoNova.nuevo("ina", 650f, 160f, 114.2f),
            "tres tramos" to DisenoNova.nuevo("ina", 650f, 160f, 114.2f),
            "tras agregar un módulo" to DisenoNova.nuevo("ina", 150f, 120f, 120f)
                .conModuloAgregado(0, 0, 0, 'c'),
            "tras partir un tramo" to DisenoNova.nuevo("apa", 240f, 200f, 150f)
                .conTramoPartido(0, 1)
        )
        escenario().use { esc ->
            esperar()
            val problemas = mutableListOf<String>()
            for ((nombre, d) in casos) {
                val paquete = d.aPaquete()
                val error = en(esc) { it.vistaRechazaParaPruebas(paquete) }
                if (error != null) problemas.add("$nombre -> $error\n    $paquete")
            }
            assertNull(
                "la vista rechaza el diseño del modelo:\n" + problemas.joinToString("\n"),
                problemas.takeIf { it.isNotEmpty() }?.joinToString("\n")
            )
        }
    }
}
