package crystal.crystal.taller.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El paquete simbólico tiene la forma `{nova,<acabado>,[<ancho>,<alto>:<tramos>]}`, y la cadena V2
 * se queda solo con `<tramos>` (el sistema y el acabado ya van en `P<>`, las medidas en `M<>`).
 * Ese recorte se hace partiendo por el PRIMER `:`, así que depende de una invariante: los tramos
 * no pueden contener dos puntos. Si algún día los llevan, esto salta antes de que la cadena
 * archivada salga cortada por la mitad.
 */
class PaqueteSimbolicoFormaTest {

    private val tipos = listOf("apa", "ina", "piv")
    private val modelos = listOf("nn", "nfc", "nff", "ncfc", "ncc", "n3c")
    private val remates = listOf("nn", "nr", "np")

    /** Mismo recorte que `NovaCorrediza.tramosDelPaquete`. */
    private fun tramosDelPaquete(paquete: String): String {
        val dentro = paquete.substringAfter("[", "").substringBeforeLast("]", "")
        if (dentro.isBlank()) return ""
        return dentro.substringAfter(":", dentro).trim()
    }

    @Test
    fun `el paquete siempre trae cabecera y corchetes, y los tramos no llevan dos puntos`() {
        var casos = 0
        for (tipo in tipos) for (ancho in listOf(120f, 187.4f, 385.9f)) for (alto in listOf(150f, 246.8f)) {
            for (divisiones in listOf(1, 2, 4, 7)) for (modelo in modelos) for (remate in remates) {
                val altoHoja = NovaCalculos.altoHoja(alto, alto * 0.6f)
                val paquete = NovaUIHelper.generarPaqueteSimbolico(
                    tipo, ancho, alto, altoHoja, divisiones, modelo, 0f, remate
                )
                casos++
                assertTrue("sin corchetes: $paquete", paquete.contains("[") && paquete.contains("]"))
                assertTrue("no empieza por {nova: $paquete", paquete.startsWith("{nova,"))

                val tramos = tramosDelPaquete(paquete)
                assertTrue("tramos vacíos en: $paquete", tramos.isNotEmpty())
                assertTrue("los tramos llevan ':' y el recorte los partiría: $tramos", !tramos.contains(":"))
                assertTrue("los tramos deben empezar por un tramo: $tramos", tramos.startsWith("T"))
            }
        }
        assertTrue(casos > 0)
    }

    @Test
    fun `el recorte quita la cabecera y deja los tramos tal cual`() {
        val paquete = "{nova,apa,[385.9,246.8:Tl<219>(s<150>(f<54.7>c<54.7>)) P<2.5> Tl<164.3>(s<150>(f<54.7>))]}"
        assertEquals(
            "Tl<219>(s<150>(f<54.7>c<54.7>)) P<2.5> Tl<164.3>(s<150>(f<54.7>))",
            tramosDelPaquete(paquete)
        )
    }

    @Test
    fun `un paquete vacío o raro no revienta el recorte`() {
        assertEquals("", tramosDelPaquete(""))
        assertEquals("", tramosDelPaquete("sin corchetes"))
    }
}
