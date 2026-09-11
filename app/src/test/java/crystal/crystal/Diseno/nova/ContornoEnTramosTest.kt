package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El vano tal como se dibuja en la medida, convertido en tramos.
 *
 * El caso de verdad es la medida de la foto: 446.3 de ancho, dintel corrido, y el alféizar que
 * sube 53.8 en los 166 finales.
 */
class ContornoEnTramosTest {

    /** El contorno de la medida real, en cm y con la Y hacia abajo (0 es el dintel). */
    private val escalonada = listOf(
        0f to 0f,
        446.3f to 0f,
        446.3f to 106.2f,
        280.3f to 106.2f,
        280.3f to 160f,
        0f to 160f
    )

    private val recta = listOf(
        0f to 0f,
        300f to 0f,
        300f to 200f,
        0f to 200f
    )

    @Test
    fun `un vano recto da un solo tramo`() {
        val bandas = ContornoEnTramos.bandas(recta)
        assertEquals(1, bandas.size)
        assertEquals(300f, bandas[0].anchoCm, 0.05f)
        assertEquals(200f, bandas[0].altoCm, 0.05f)
    }

    @Test
    fun `el vano escalonado da dos tramos con sus medidas`() {
        val bandas = ContornoEnTramos.bandas(escalonada)
        assertEquals(2, bandas.size)
        assertEquals(280.3f, bandas[0].anchoCm, 0.05f)
        assertEquals(160f, bandas[0].altoCm, 0.05f)
        assertEquals(166f, bandas[1].anchoCm, 0.05f)
        assertEquals(106.2f, bandas[1].altoCm, 0.05f)
        // Y las dos suman el ancho del vano.
        assertEquals(446.3f, bandas.sumOf { it.anchoCm.toDouble() }.toFloat(), 0.05f)
    }

    @Test
    fun `el diseño sale con el dintel corrido y el escalón marcado`() {
        val d = ContornoEnTramos.disenoDesdeContorno(escalonada, acabado = "apa", altoHoja = 110f)
        assertNotNull(d)
        assertEquals(2, d!!.nTramos)
        assertEquals("el alto de la ventana no es el del tramo más alto", 160f, d.alto, 0.05f)
        assertEquals(160f, d.altoDeTramo(0), 0.05f)
        assertEquals(106.2f, d.altoDeTramo(1), 0.05f)
        assertTrue("no quedó marcada como escalonada", d.esEscalonada)
        // El tramo alto no lleva alto propio: cuelga del dintel y llega al suelo como la ventana.
        assertEquals(0f, d.tramos[0].alto, 0.01f)
        // Y cada tramo tiene módulos de verdad, repartidos con la regla de los 60.
        assertTrue(d.tramos[0].nModulosSistema >= 1)
        assertTrue(d.tramos[1].nModulosSistema >= 1)
    }

    @Test
    fun `el diseño del vano escalonado se escribe y se relee igual`() {
        val d = ContornoEnTramos.disenoDesdeContorno(escalonada)!!
        val ida = d.aPaquete()
        val vuelta = DisenoNova.desdePaquete(ida)!!
        assertEquals(ida, vuelta.aPaquete())
        assertEquals(106.2f, vuelta.altoDeTramo(1), 0.05f)
    }

    @Test
    fun `un contorno que no es un vano no da nada`() {
        assertTrue(ContornoEnTramos.bandas(listOf(0f to 0f, 10f to 0f)).isEmpty())
    }

    @Test
    fun `el escalón se lee igual dibujado al revés`() {
        // Mismo vano, recorrido en el otro sentido: las bandas salen iguales.
        val alReves = escalonada.reversed()
        val bandas = ContornoEnTramos.bandas(alReves)
        assertEquals(2, bandas.size)
        assertEquals(160f, bandas[0].altoCm, 0.05f)
        assertEquals(106.2f, bandas[1].altoCm, 0.05f)
    }

    @Test
    fun `el contorno va y vuelve como texto`() {
        val texto = ContornoEnTramos.aTexto(escalonada)
        assertTrue("no parece un contorno: $texto", texto.startsWith("0,0;446.3,0;"))
        val vuelta = ContornoEnTramos.desdeTexto(texto)
        assertEquals(escalonada.size, vuelta.size)
        val bandas = ContornoEnTramos.bandas(vuelta)
        assertEquals(2, bandas.size)
        assertEquals(106.2f, bandas[1].altoCm, 0.05f)
    }

    @Test
    fun `un texto roto no tumba nada`() {
        assertTrue(ContornoEnTramos.desdeTexto("").isEmpty())
        assertTrue(ContornoEnTramos.desdeTexto("cualquier cosa").isEmpty())
        // Un punto suelto mal escrito se descarta y los demás se leen.
        assertEquals(2, ContornoEnTramos.desdeTexto("0,0;mal;10,5").size)
    }
}
