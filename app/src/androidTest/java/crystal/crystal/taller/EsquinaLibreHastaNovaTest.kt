package crystal.crystal.taller

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import crystal.crystal.Diseno.nova.DisenoNova
import crystal.crystal.taller.nova.NovaCorrediza
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * La cadena entera con el apunte real de la ventana de esquina de Formas (la pared de frente con
 * el dintel en diagonal, ya convertida): lo que sale del apunte para la cola, y lo que la
 * calculadora arma con eso. La pared de frente tiene que llegar con su silueta, no como rectángulo.
 */
@RunWith(AndroidJUnit4::class)
class EsquinaLibreHastaNovaTest {

    private fun vista(): SketchMedidasView {
        val v = SketchMedidasView(ApplicationProvider.getApplicationContext())
        v.measure(
            android.view.View.MeasureSpec.makeMeasureSpec(1080, android.view.View.MeasureSpec.EXACTLY),
            android.view.View.MeasureSpec.makeMeasureSpec(1600, android.view.View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, 1080, 1600)
        return v
    }

    private fun apunte(): String =
        InstrumentationRegistry.getInstrumentation().context.assets.open("esquina_formas.json")
            .bufferedReader().readText()

    @Test
    fun el_apunte_real_manda_la_silueta_de_la_pared_de_frente() {
        val v = vista()
        assertTrue(v.loadEditableState(apunte()))
        val esquina = v.esquinaPrincipalEnCm()
        assertNotNull("el apunte no se lee como esquina", esquina)
        assertEquals(2, esquina!!.lados.size)
        val contornos = v.contornosDeLadosEnCm()
        assertEquals("un contorno por lado", 2, contornos.size)
        assertNotNull("la pared de frente tiene que llevar su silueta", contornos[0])
        assertEquals("la silueta es el pentágono", 5, contornos[0]!!.size)
        val texto = LadosLibres.contornosATexto(contornos)
        assertTrue("el texto de la cola va vacío: '$texto'", texto.isNotBlank())
    }

    @Test
    fun la_calculadora_arma_la_l_con_la_silueta() {
        val v = vista()
        assertTrue(v.loadEditableState(apunte()))
        val esquina = EsquinaMedida.aTexto(v.esquinaPrincipalEnCm()!!)
        val contornos = LadosLibres.contornosATexto(v.contornosDeLadosEnCm())
        val parantes = LadosLibres.parantesATexto(v.parantesDeLadosEnCm())
        val medida = v.medidaPrincipal()!!
        val item = ColaCalculadoras.MedidaCalc(
            producto = "Nova Corrediza", ancho = medida.anchoCm, alto = medida.altoCm, cantidad = 1f,
            cliente = "Formas", bocetoArchivo = "", contorno = "", esquina = esquina,
            contornosLados = contornos, parantesLados = parantes
        )
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = Intent(ctx, NovaCorrediza::class.java).apply {
            putExtra("rcliente", "Formas")
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
        ActivityScenario.launch<NovaCorrediza>(intent).use { esc ->
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            Thread.sleep(800)
            var paquete = ""
            val latch = CountDownLatch(1)
            esc.onActivity { paquete = runCatching { it.disenoSimbolicoParaPruebas() }.getOrElse { "ERROR: $it" }; latch.countDown() }
            latch.await(10, TimeUnit.SECONDS)
            assertTrue("la calculadora no armó el paquete: $paquete", paquete.startsWith("{nova"))
            val d = DisenoNova.desdePaquete(paquete)
            assertNotNull("el paquete no se lee: $paquete", d)
            assertTrue("no es una esquina: $paquete", d!!.doblaEnEsquina)
            assertEquals("la pared de frente llegó sin silueta: $paquete", 5, d.tramos[0].contorno.size)
            // Y cada pared con su alto: la de al lado es más baja y cuelga del dintel.
            assertTrue("la pared de al lado no lleva su alto: $paquete", d.tramos[1].alto > 0f && d.tramos[1].alto < d.alto - 1f)
            assertEquals(d.alto - d.tramos[1].alto, d.tramos[1].caida, 0.1f)
        }
    }
}
