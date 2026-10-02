package crystal.crystal.taller.nova

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import crystal.crystal.Diseno.nova.DisenoNova
import crystal.crystal.databinding.ActivityNovaCorredizaBinding
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Forma poligonal, primer paso (2026-09-25): lo que vuelve de novaDiseño se conserva en Nova.
 *
 * Con un vano de contorno (el escalonado real de 446.3 × 160), el diseño editado a mano volvía a
 * Nova y el siguiente Calcular lo rehacía desde el contorno si la hoja del diseño editado no era
 * la con que se armó: `rehacerDisenoSiCambioLaHoja` (hoy `rehacerDesdeLaFormaSiCambio`) comparaba contra la hoja vieja.
 */
@RunWith(AndroidJUnit4::class)
class DisenoEditadoSeConservaTest {

    private val escalonado = "0,0;446.3,0;446.3,106.2;280.3,106.2;280.3,160;0,160"

    private fun campo(o: Any, n: String) = o.javaClass.getDeclaredField(n).apply { isAccessible = true }
    private fun llamar(o: Any, n: String, vararg args: Any) =
        o.javaClass.getDeclaredMethod(n, *args.map { it.javaClass }.toTypedArray())
            .apply { isAccessible = true }.invoke(o, *args)

    @Test
    fun lo_editado_en_novaDiseno_sobrevive_al_calcular() {
        ActivityScenario.launch(NovaCorrediza::class.java).use { sc ->
            sc.onActivity { a ->
                val b = campo(a, "binding").get(a) as ActivityNovaCorredizaBinding
                campo(a, "texto").set(a, "nn")
                campo(a, "textoModelo").set(a, "nn")
                b.etHoja.setText("0")
                assertEquals(true, llamar(a, "cargarDisenoDelContorno", escalonado))
                val delContorno = campo(a, "ultimoPaquete").get(a) as String

                // Lo que haría el vidriero en novaDiseño: el puente de cada tramo a 90.
                var d = DisenoNova.desdePaquete(delContorno)!!
                d.tramos.indices.forEach { i -> d = d.conPuenteCambiado(i, 90f) }
                val editado = d.aPaquete()
                assertNotEquals(delContorno, editado)

                llamar(a, "recibirDisenoEditado", editado)
                llamar(a, "rehacerDesdeLaFormaSiCambio")   // lo primero que hace Calcular
                assertEquals(editado, campo(a, "ultimoPaquete").get(a))

                // Pedido del usuario (2026-09-26): cambiar la U o el cruce no suelta el diseño (antes la
                // forma se volvía rectangular al calcular).
                b.etU.setText("1")
                b.etCruce.setText("0.5")
                assertEquals(editado, campo(a, "ultimoPaquete").get(a))
                assertNotEquals(0, (campo(a, "modulosDesiguales").get(a) as List<*>).size)
                assertEquals(true, llamar(a, "confirmarMedidasDelDiseno"))
                // Cambiar el ancho tampoco lo suelta en silencio: Calcular pregunta (no sigue).
                val anchoAntes = b.etAncho.text.toString()
                b.etAncho.setText("300")
                assertEquals(false, llamar(a, "confirmarMedidasDelDiseno"))
                assertNotEquals(0, (campo(a, "modulosDesiguales").get(a) as List<*>).size)
                b.etAncho.setText(anchoAntes)

                // Si después el vidriero escribe otra hoja, el vano sí se rehace desde el contorno.
                b.etHoja.setText("80")
                llamar(a, "rehacerDesdeLaFormaSiCambio")
                assertNotEquals(editado, campo(a, "ultimoPaquete").get(a))
            }
        }
    }
}
