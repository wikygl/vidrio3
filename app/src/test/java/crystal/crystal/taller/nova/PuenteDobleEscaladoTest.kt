package crystal.crystal.taller.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El doble puente (`np`) lleva DOS corridas de puente, y esa duplicación se hace de dos maneras
 * distintas porque los dos textos del puente son distintos:
 *
 * 1. Puente "por tramos" (`NovaCalculos.textoTramosUnitarioAparente`): solo medidas horizontales,
 *    así que se puede multiplicar la cantidad de TODAS las líneas (lo hace
 *    `NovaCorrediza.escalarCantidadesTexto`).
 * 2. Puente de `NovaInaCalculos.puentes`: además de las horizontales trae la línea del PARANTE
 *    VERTICAL entre tramos ("alto = n"), que va de piso a techo y NO se duplica. Por eso ahí la
 *    duplicación se hace dentro de la función (parámetro `filasPuente`) y no con un escalador de
 *    texto: un escalador ciego duplicaría también el parante y compraría de más.
 *
 * Estos tests fijan esa frontera. Si se rompen, el doble puente está duplicando lo que no debe.
 */
class PuenteDobleEscaladoTest {

    private val anchos = listOf(80f, 120f, 150f, 187.4f, 240f, 300f, 420f, 512.5f)
    private val puentes = listOf("Múltiple", "gorrito", "Tubo", "")
    private val modelos = listOf("nn", "nfc", "nff", "ncfc", "nl", "nu", "nci")

    /** Copia de `NovaCorrediza.escalarCantidadesTexto` (es privada de la Activity). */
    private fun escalarCantidadesTexto(texto: String, factor: Int): String {
        if (factor <= 1 || texto.isBlank()) return texto
        val patron = Regex("^(.*=\\s*)(\\d+)\\s*$")
        return texto.lines().joinToString("\n") { linea ->
            val m = patron.matchEntire(linea.trim())
            if (m != null) {
                val prefijo = m.groupValues[1]
                val cantidad = m.groupValues[2].toIntOrNull() ?: return@joinToString linea
                "$prefijo${cantidad * factor}"
            } else {
                linea
            }
        }
    }

    @Test
    fun `el puente por tramos solo trae medidas horizontales, por eso se puede escalar entero`() {
        var conTexto = 0
        for (ancho in anchos) for (divisiones in 1..14) for (puente in puentes) for (modelo in modelos) {
            val texto = NovaCalculos.textoTramosUnitarioAparente(ancho, divisiones, puente, 2.5f, modelo)
            if (texto.isBlank()) continue
            conTexto++
            for (linea in texto.lines()) {
                val partes = linea.split("=")
                assertTrue("línea inesperada '$linea' (ancho=$ancho div=$divisiones)", partes.size == 2)
                assertTrue("cantidad no entera en '$linea'", partes[1].trim().toIntOrNull() != null)
            }
        }
        assertTrue(conTexto > 0)
    }

    @Test
    fun `en el puente de Ina el parante vertical no se duplica y las horizontales si`() {
        for (ancho in anchos) for (divisiones in 1..14) {
            val alto = 240f
            val simple = NovaInaCalculos.puentes(alto, ancho, divisiones, puentesExtra = 0, filasPuente = 1)
            val doble = NovaInaCalculos.puentes(alto, ancho, divisiones, puentesExtra = 0, filasPuente = 2)
            val lineasSimple = simple.lines().filter { it.isNotBlank() }
            val lineasDoble = doble.lines().filter { it.isNotBlank() }
            assertEquals("cambia el número de líneas (ancho=$ancho div=$divisiones)", lineasSimple.size, lineasDoble.size)

            val medidaAlto = NovaCalculos.df1(alto)
            for (i in lineasSimple.indices) {
                val (medS, cantS) = lineasSimple[i].split("=").map { it.trim() }
                val (medD, cantD) = lineasDoble[i].split("=").map { it.trim() }
                assertEquals("la medida no debe cambiar", medS, medD)
                if (medS == medidaAlto) {
                    // Parante vertical entre tramos: uno solo, de piso a techo.
                    assertEquals("el parante vertical se duplicó (ancho=$ancho div=$divisiones)", cantS, cantD)
                } else {
                    assertEquals(
                        "la corrida horizontal no se duplicó (ancho=$ancho div=$divisiones)",
                        (cantS.toInt() * 2).toString(), cantD
                    )
                }
            }
        }
    }

    @Test
    fun `escalarCantidadesTexto da lo mismo que el helper conFilasDePuente que reemplazó`() {
        // `conFilasDePuente` (eliminado) hacía el x2 con split("="); esto deja constancia de que
        // sobre el texto real del puente por tramos las dos versiones daban lo mismo.
        fun conFilasDePuente(texto: String): String {
            if (texto.isBlank()) return texto
            return texto.lines().joinToString("\n") { linea ->
                val partes = linea.split("=")
                if (partes.size != 2) return@joinToString linea
                val n = partes[1].trim().toIntOrNull() ?: return@joinToString linea
                "${partes[0].trim()} = ${n * 2}"
            }
        }
        for (ancho in anchos) for (divisiones in 1..14) for (puente in puentes) for (modelo in modelos) {
            for (spinner in listOf(2.5f, 3.8f, 5f)) {
                val texto = NovaCalculos.textoTramosUnitarioAparente(ancho, divisiones, puente, spinner, modelo)
                assertEquals(
                    "ancho=$ancho div=$divisiones puente=$puente modelo=$modelo",
                    conFilasDePuente(texto), escalarCantidadesTexto(texto, 2)
                )
            }
        }
    }
}
