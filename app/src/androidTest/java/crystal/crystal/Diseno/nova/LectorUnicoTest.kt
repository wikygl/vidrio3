package crystal.crystal.Diseno.nova

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import crystal.crystal.taller.nova.NovaCalculos
import crystal.crystal.taller.nova.NovaUIHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * C-13, un solo lector del diseño (N-81): lo que el dibujo lee de cada paquete, en una foto por
 * paquete. Sirve para comparar el lector propio del dibujo con el del modelo antes de cambiarlo:
 * si las fotos coinciden, el dibujo sale igual. Deja `lector_dibujo.txt` en los archivos de la app.
 */
@RunWith(AndroidJUnit4::class)
class LectorUnicoTest {

    private val ctx get() = ApplicationProvider.getApplicationContext<android.content.Context>()

    /** Los paquetes de las pruebas y los de la calculadora en todas sus combinaciones. */
    private fun corpus(): List<String> {
        val deLasPruebas = InstrumentationRegistry.getInstrumentation().context.assets
            .open("paquetes_corpus.txt").bufferedReader().readLines().filter { it.isNotBlank() }
        val deLaCalculadora = mutableListOf<String>()
        for (acabado in listOf("apa", "ina")) for (remate in listOf("nn", "nr", "np"))
            for (mod in listOf("nn", "nfc", "nff", "ncfc", "ncc", "n3c"))
                for (ancho in listOf(100f, 199.9f, 330f, 560f))
                    for ((alto, hoja) in listOf(145f to 100f, 210f to 210f)) {
                        runCatching {
                            NovaUIHelper.generarPaqueteSimbolico(
                                acabado, ancho, alto, NovaCalculos.altoHoja(alto, hoja),
                                NovaCalculos.divisiones(ancho, 0), mod, 0f, remate
                            )
                        }.getOrNull()?.let { deLaCalculadora += it }
                    }
        return (deLasPruebas + deLaCalculadora).distinct()
    }

    private fun foto(p: String): String {
        val v = VistaDiseno(ctx)
        return runCatching { v.actualizarDesdePaquete(p, 0f, 0f, 0f); v.estadoLeidoParaPruebas() }
            .getOrElse { "ERROR ${it.javaClass.simpleName}" }
    }

    /**
     * El dibujo lee cada paquete igual que la referencia, que es lo que leía su lector propio antes
     * de retirarlo (2026-09-28). Solo se corrigió en la referencia un caso en que el viejo estaba
     * mal: el arco `U<25>` de un tramo con `H<160>`, que no marcaba. Si esta prueba falla, el
     * dibujo cambió: hay que ver por qué antes de tocar la referencia.
     */
    @Test
    fun el_dibujo_lee_igual_que_la_referencia() {
        val referencia = mutableMapOf<String, String>()
        InstrumentationRegistry.getInstrumentation().context.assets.open("lector_dibujo_referencia.txt")
            .bufferedReader().readText().split("\n\n").map { it.trim() }.filter { it.startsWith("### ") }
            .forEach { bloque -> referencia[bloque.lines().first().removePrefix("### ")] = bloque.lines().drop(1).joinToString("\n") }
        val distintos = mutableListOf<String>()
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            corpus().forEach { p ->
                val esperado = referencia[p] ?: return@forEach
                val leido = foto(p)
                if (leido != esperado) distintos += "$p\n  esperado: ${esperado.lines().zip(leido.lines()).filter { (a, b) -> a != b }}"
            }
        }
        assertEquals("paquetes que el dibujo lee distinto:\n" + distintos.joinToString("\n"), 0, distintos.size)
        assertTrue("la referencia no cubre el corpus: ${referencia.size}", referencia.size >= 300)
    }
}
