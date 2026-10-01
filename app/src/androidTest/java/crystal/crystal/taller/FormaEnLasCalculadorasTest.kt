package crystal.crystal.taller

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.widget.EditText
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import crystal.crystal.taller.venAl.VentanaAl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Las calculadoras que no son Nova reconocen la forma de la medida (2026-09-29). La ventana de Pipa
 * (182.4 × 70.8, el dintel baja 10.8 en los últimos 26 cm) llega a Ventana Aluminio: avisa que es
 * escalonada y, si el vidriero lo pide, se parte en sus dos rectángulos, 156 × 70.8 y 26.1 × 60.
 */
@RunWith(AndroidJUnit4::class)
class FormaEnLasCalculadorasTest {

    private fun intentDe(destino: Class<*>, cola: List<ColaCalculadoras.MedidaCalc>): Intent {
        val ctx = ApplicationProvider.getApplicationContext<android.content.Context>()
        val item = cola.first()
        return Intent(ctx, destino).apply {
            putExtra("rcliente", item.cliente); putExtra("ancho", item.ancho); putExtra("alto", item.alto)
            putExtra("cantidad", item.cantidad); putExtra("producto", item.producto)
            putExtra(ColaCalculadoras.EXTRA_DESDE_MEDIDAS, true)
            putExtra(ColaCalculadoras.EXTRA_BOCETO_PATH, "")
            putExtra(ColaCalculadoras.EXTRA_COLA, ArrayList(cola))
            putExtra(ColaCalculadoras.EXTRA_INDICE, 0)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    private val pipa = ColaCalculadoras.MedidaCalc(
        "Ventana", 182.4f, 70.8f, 1f, "Pipa", "",
        contorno = "0,0;156,0;156,10.8;182.1,10.8;182.1,70.8;0,70.8"
    )

    @Test
    fun la_ventana_escalonada_avisa_y_se_parte_en_sus_rectangulos() {
        ControladorColaMedidas.ultimoAvisoDeForma = null
        ActivityScenario.launch<Activity>(intentDe(VentanaAl::class.java, listOf(pipa))).use { esc ->
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            val aviso = ControladorColaMedidas.ultimoAvisoDeForma
            assertNotNull("no avisó de la forma", aviso)
            esc.onActivity { aviso!!.getButton(DialogInterface.BUTTON_NEGATIVE).performClick() }
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            esc.onActivity { a ->
                val cola = ColaCalculadoras.cola(a)
                assertEquals("la cola no se partió", 2, cola.size)
                assertEquals(156f, cola[0].ancho, 0.05f)
                assertEquals(26.1f, cola[1].ancho, 0.05f)
                assertEquals(60f, cola[1].alto, 0.05f)
                assertTrue(cola.all { it.contorno.isBlank() })
                val ancho = a.findViewById<EditText>(crystal.crystal.R.id.etAncho).text.toString()
                assertEquals("156", ancho)
            }
        }
    }

    @Test
    fun una_medida_rectangular_no_avisa() {
        ControladorColaMedidas.ultimoAvisoDeForma = null
        ActivityScenario.launch<Activity>(intentDe(VentanaAl::class.java, listOf(pipa.copy(contorno = "")))).use {
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            assertNull(ControladorColaMedidas.ultimoAvisoDeForma)
        }
    }
}
