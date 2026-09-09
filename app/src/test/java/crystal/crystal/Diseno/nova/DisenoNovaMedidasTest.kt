package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Cambiar la medida de la ventana tiene que rehacer el diseño. Si no, los tramos se quedan con el
 * ancho viejo y la pantalla acaba devolviendo el ancho anterior al recalcular desde ellos.
 */
class DisenoNovaMedidasTest {

    private fun sumaConParantes(d: DisenoNova) =
        d.tramos.sumOf { it.ancho.toDouble() }.toFloat() + d.nParantes * 2.5f

    @Test
    fun `al ensanchar la ventana los tramos se reparten el ancho nuevo`() {
        val original = DisenoNova.nuevo("apa", 150f, 200f, altoHoja = 200f)
        assertEquals(150f, sumaConParantes(original), 0.05f)

        // Lo que hace la pantalla: se cambia el ancho de la cabecera y se reparte.
        val ancho = original.copy(ancho = 300f).conAnchosRepartidos()
        assertEquals(300f, ancho.ancho, 0.01f)
        assertEquals(300f, sumaConParantes(ancho), 0.05f)
        // Los módulos siguen siendo los mismos; solo cambian de medida.
        assertEquals(original.nModulos, ancho.nModulos)
        assertTrue(ancho.tramos[0].sistema!!.modulos[0].ancho!! > original.tramos[0].sistema!!.modulos[0].ancho!!)
    }

    @Test
    fun `al angostarla tambien`() {
        val original = DisenoNova.nuevo("ina", 650f, 160f, 114.2f)
        val angosta = original.copy(ancho = 400f).conAnchosRepartidos()
        assertEquals(400f, sumaConParantes(angosta), 0.05f)
        assertEquals(original.nTramos, angosta.nTramos)
        // Y dentro de cada tramo las franjas siguen cerrando su ancho.
        for (t in angosta.tramos) {
            for (fr in t.franjas) {
                val suma = fr.modulos.sumOf { (it.ancho ?: 0f).toDouble() }.toFloat()
                assertEquals(t.ancho, suma, 0.05f)
            }
        }
    }

    @Test
    fun `el ancho no se recalcula desde tramos viejos`() {
        // El caso del fallo: ancho nuevo, tramos sin repartir. Si alguien sumara los tramos para
        // deducir el ancho, saldría el viejo.
        val sinRepartir = DisenoNova.nuevo("apa", 150f, 200f, 200f).copy(ancho = 300f)
        assertEquals(150f, sumaConParantes(sinRepartir), 0.05f)   // así estaba el fallo
        val repartido = sinRepartir.conAnchosRepartidos()
        assertEquals(300f, sumaConParantes(repartido), 0.05f)
    }
}
