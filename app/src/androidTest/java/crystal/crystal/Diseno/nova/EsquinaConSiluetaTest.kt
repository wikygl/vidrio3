package crystal.crystal.Diseno.nova

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * La ventana de esquina cuya pared de frente no es un rectángulo: el dintel baja en diagonal
 * desde 60 hasta 40 con un pico a 145. La silueta viaja en el tramo de esa pared (`L<…>`), el
 * editor la abre por lados y en cada lado se ve, y al volver a armar la ventana no se pierde.
 */
@RunWith(AndroidJUnit4::class)
class EsquinaConSiluetaTest {

    private val silueta = "L<0/60|145/0|280/40|280/220|0/220>"
    private val enL = "{nova,apa,[280,220:Tl<280>($silueta;s(f;P;cc)) A<90> Tl<64.5>(s(f))]}"

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
    fun la_silueta_de_la_pared_llega_al_editor_y_vuelve_entera() {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = Intent(ctx, DisenoNovaActivity::class.java)
            .putExtra(DisenoNovaActivity.EXTRA_PAQUETE, enL)
            // La calculadora manda la aleta también como mocheta lateral: por lados no debe pintarse.
            .putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, 64.5f)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        ActivityScenario.launch<DisenoNovaActivity>(intent).use { esc ->
            esperar()
            assertEquals("no se abrió por lados", 2, en(esc) { it.ladosParaPruebas() })
            assertEquals("la aleta se pintaba como panel vacío al lado", 0f, en(esc) { it.mochetaLateralParaPruebas() }, 0.01f)

            // El primer lado, de frente, trae su silueta y la ve como su vano.
            val lado1 = DisenoNova.desdePaquete(en(esc) { it.paqueteParaPruebas() })!!
            assertEquals("la silueta no llegó al lado", 5, lado1.tramos[0].contorno.size)
            assertEquals("el parante marcado no llegó", listOf(0), lado1.tramos[0].sistema!!.parantes)
            val vano = en(esc) { it.contornoVanoParaPruebas() }
            assertEquals("el dibujo del lado no usa la silueta como vano: $vano", 5, vano.size)

            // El segundo lado es un rectángulo y no lleva nada.
            en(esc) { it.irAlLadoParaPruebas(1) }
            esperar(300)
            val lado2 = DisenoNova.desdePaquete(en(esc) { it.paqueteParaPruebas() })!!
            assertTrue("la pared recta no lleva silueta", lado2.tramos[0].contorno.isEmpty())
            assertTrue(en(esc) { it.contornoVanoParaPruebas() }.isEmpty())

            // Armada entera, el dibujo de la L sabe qué tramo tiene silueta.
            en(esc) { it.alternarVistaEnteraParaPruebas() }
            esperar(300)
            assertEquals(listOf(5, 0), en(esc) { it.siluetasDeTramoParaPruebas() })
            val bmp = en(esc) { it.dibujoParaPruebas() }
            assertTrue("el dibujo armado salió vacío", bmp.width > 10 && bmp.height > 10)

            // Y al enviarla vuelve entera, con la silueta en su tramo.
            en(esc) { it.alternarVistaEnteraParaPruebas() }
            esperar(300)
            val entera = en(esc) { it.paqueteDeLaEsquinaParaPruebas() }
            assertNotNull(entera)
            val d = DisenoNova.desdePaquete(entera!!)!!
            assertEquals(2, d.tramos.size)
            assertEquals("la silueta se perdió al armar: $entera", 5, d.tramos[0].contorno.size)
            assertTrue(entera.contains("L<0/60|145/0|280/40|280/220|0/220>"))
        }
    }
}
