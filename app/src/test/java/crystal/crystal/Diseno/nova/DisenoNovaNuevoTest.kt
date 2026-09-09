package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Crear un diseño desde cero. Antes se arrancaba con `Tl<ancho>(s(f))`: un tramo, un fijo y la
 * franja del sistema sin altura, así que había que armar la ventana entera a mano.
 */
class DisenoNovaNuevoTest {

    @Test
    fun `una ventana nueva sale con las divisiones y los tramos de la calculadora`() {
        // La misma medida de la ventana de Abel: 650 de ancho, puente a 114.2.
        val d = DisenoNova.nuevo(acabado = "ina", ancho = 650f, alto = 160f, altoHoja = 114.2f)
        assertEquals(11, d.nModulos)                       // ceil(650/60)
        assertEquals(3, d.nTramos)                         // 5 módulos por tramo
        assertEquals(listOf(4, 3, 4), d.tramos.map { it.nModulosSistema })
        // Y las medidas cierran el ancho de la ventana.
        val suma = d.tramos.sumOf { it.ancho.toDouble() }.toFloat() + d.nParantes * 2.5f
        assertEquals(650f, suma, 0.05f)
    }

    @Test
    fun `si la hoja no llega al alto se crea la mocheta`() {
        val d = DisenoNova.nuevo("ina", 650f, 160f, 114.2f)
        for (t in d.tramos) {
            assertEquals(1, t.mochetas.size)
            assertEquals(45.8f, t.mochetas[0].alto, 0.01f)   // 160 - 114.2
            assertEquals(114.2f, t.sistema!!.alto, 0.01f)
            // Ningún paño de mocheta pasa de 180.
            assertTrue(t.mochetas[0].modulos.all { (it.ancho ?: 0f) <= 180f })
        }
        // El tramo del medio es más angosto: le basta un paño.
        assertEquals(1, d.tramos[1].mochetas[0].modulos.size)
        assertEquals(2, d.tramos[0].mochetas[0].modulos.size)
    }

    @Test
    fun `sin mocheta la franja del sistema ocupa todo el alto`() {
        val d = DisenoNova.nuevo("apa", 240f, 150f, altoHoja = 150f)
        assertEquals(1, d.tramos[0].franjas.size)
        assertEquals(150f, d.tramos[0].sistema!!.alto, 0.01f)
    }

    @Test
    fun `el patron de fijos y corredizas es el clasico`() {
        // 4 divisiones: f c c f.
        val d = DisenoNova.nuevo("apa", 240f, 200f, 150f)
        assertEquals(4, d.nModulos)
        assertEquals(listOf('f', 'c', 'c', 'f'), d.tramos[0].sistema!!.modulos.map { it.tipo })
        assertEquals(2, d.nFijos)
        assertEquals(2, d.nCorredizas)
    }

    @Test
    fun `se pueden pedir las divisiones a mano`() {
        val d = DisenoNova.nuevo("apa", 240f, 200f, 150f, divisiones = 2)
        assertEquals(2, d.nModulos)
        assertEquals(1, d.nTramos)
        assertEquals(listOf('f', 'c'), d.tramos[0].sistema!!.modulos.map { it.tipo })
    }

    @Test
    fun `lo que se crea se puede escribir y volver a leer`() {
        val d = DisenoNova.nuevo("ina", 650f, 160f, 114.2f)
        val releido = DisenoNova.desdePaquete(d.aPaquete())!!
        assertEquals(d.nTramos, releido.nTramos)
        assertEquals(d.nModulos, releido.nModulos)
        assertEquals(
            d.tramos.map { t -> t.franjas.map { fr -> fr.modulos.map { it.tipo } } },
            releido.tramos.map { t -> t.franjas.map { fr -> fr.modulos.map { it.tipo } } }
        )
    }

    @Test
    fun `medidas absurdas no revientan`() {
        val d = DisenoNova.nuevo("apa", 0f, 0f, 0f)
        assertTrue(d.nTramos >= 1)
        assertTrue(d.nModulos >= 1)
    }
}
