package crystal.crystal.taller

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import crystal.crystal.Diseno.nova.ContornoEnTramos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * El vano en triángulo, desde el apunte hasta el diseño de la calculadora.
 *
 * El triángulo se dibuja con SU herramienta, así que no es una figura recortada: no tenía contorno
 * guardado y llegaba a Nova como el rectángulo de su caja —el dibujo se veía, pero el cálculo salía
 * recto—. Y donde el vano se cierra la hoja no entra: la corrediza se queda en el rectángulo que
 * cabe y las puntas van con fijos.
 */
@RunWith(AndroidJUnit4::class)
class VanoTrianguloTest {

    private fun vista() = SketchMedidasView(ApplicationProvider.getApplicationContext())

    @Test
    fun el_triangulo_de_la_herramienta_manda_su_contorno() {
        val v = vista()
        v.agregarTrianguloInvertidoParaPruebas(0f, 0f, 400f, 320f)

        val contorno = v.contornoPrincipalEnCm()
        assertNotNull("el triángulo no mandó contorno: llega como un rectángulo", contorno)
        assertEquals("un triángulo tiene tres vértices", 3, contorno!!.size)
        // La base va arriba (y = 0) y la punta abajo, en el medio.
        val arriba = contorno.filter { it.second <= 0.5f }
        assertEquals("la base no quedó arriba: $contorno", 2, arriba.size)
        val punta = contorno.first { it.second > 0.5f }
        val ancho = contorno.maxOf { it.first }
        assertEquals("la punta no cae en el centro", ancho / 2f, punta.first, ancho * 0.02f)
    }

    @Test
    fun el_vano_triangular_llega_con_la_corrediza_en_su_rectangulo() {
        val v = vista()
        v.agregarTrianguloInvertidoParaPruebas(0f, 0f, 400f, 320f)
        val contorno = v.contornoPrincipalEnCm()!!

        val medida = v.medidaPrincipal()!!
        val d = ContornoEnTramos.disenoDesdeContorno(contorno, "apa", altoHoja = medida.altoCm * 0.7f)
        assertNotNull(d)
        assertTrue("el vano triangular se leyó como un rectángulo", d!!.esIrregular)
        // Un tramo (2026-10-19, "cuanto más simple mejor"; antes, un tramo por punta y otro en el
        // medio): el sistema en el rectángulo donde la hoja entra entera, las puntas fijos.
        assertEquals("el vano no salió en un tramo: ${d.aPaquete()}", 1, d.nTramos)
        assertTrue("el vano triangular se quedó sin corrediza: ${d.aPaquete()}", d.nCorredizas >= 1)
        // Y la corrediza va en el medio, no en las puntas.
        val mods = d.tramos[0].sistema!!.modulos
        assertTrue("la corrediza cayó en una punta: ${d.aPaquete()}", mods.first().esFijo && mods.last().esFijo)
    }
}
