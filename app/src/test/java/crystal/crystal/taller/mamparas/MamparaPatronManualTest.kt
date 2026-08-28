package crystal.crystal.taller.mamparas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El patrón elegido a mano tiene que MANDAR sobre el automático.
 *
 * Sin esto no hay editor que valga: `NovaCalculos.ordenDivis` devuelve una única disposición por
 * número de divisiones (para 3 siempre `fcf`), así que pedir `fcc` no servía de nada.
 */
class MamparaPatronManualTest {

    private fun descriptor(divisiones: Int, patron: String = "") = MamparaPaflonDescriptor(
        ancho = 300f, alto = 210f, altoHoja = 199f,
        divisiones = divisiones, bastidor = 2.5f, marco = 2.5f, nMochetas = 0,
        patron = patron
    )

    @Test
    fun `sin patron manda el automatico`() {
        val m = MamparaModulos.desde(descriptor(3))
        assertEquals("fcf", m.tramos.single().map { it.tipo }.joinToString(""))
    }

    @Test
    fun `el patron manual gana al automatico`() {
        // El automático daría "fcf"; se pide "fcc" y eso es lo que debe salir.
        val m = MamparaModulos.desde(descriptor(3, "fcc"))
        assertEquals("fcc", m.tramos.single().map { it.tipo }.joinToString(""))
        assertEquals(2, m.nCorredizas)
        assertEquals(1, m.nFijos)
    }

    @Test
    fun `el patron define los tramos y su parante`() {
        val m = MamparaModulos.desde(descriptor(4, "fc|cf"))
        assertEquals(2, m.tramos.size)
        // Entre tramos va un parante estructural de paflón: dos tramos, un parante.
        assertEquals(1, m.nParantesTramo)
        assertEquals(4, m.nModulos)
    }

    @Test
    fun `el patron manda tambien sobre el numero de divisiones`() {
        // El descriptor dice 3, el patrón describe 5: manda el patrón, o el usuario vería una
        // mampara distinta de la que acaba de dibujar.
        val m = MamparaModulos.desde(descriptor(3, "fcfcf"))
        assertEquals(5, m.nModulos)
    }

    @Test
    fun `una cadena invalida cae al automatico en vez de romper`() {
        assertNull(MamparaModulos.patronManual("fxf"))
        assertNull(MamparaModulos.patronManual("fc||cf"))
        assertNull(MamparaModulos.patronManual(""))
        assertNull(MamparaModulos.patronManual(null))
        // Y el cálculo sigue funcionando con esa cadena inválida.
        val m = MamparaModulos.desde(descriptor(3, "fxf"))
        assertEquals("fcf", m.tramos.single().map { it.tipo }.joinToString(""))
    }

    @Test
    fun `los anchos reparten el hueco util entre los modulos del patron`() {
        val d = descriptor(3, "fcc")
        val m = MamparaModulos.desde(d)
        val util = d.ancho - 2 * d.marco
        val ocupado = m.tramos.sumOf { m.anchoTramo(it).toDouble() }
        // Lo dibujado tiene que caber exactamente en el hueco: si no, el vidrio no entra en obra.
        assertEquals(util.toDouble(), ocupado, 0.05)
    }

    @Test
    fun `el descriptor conserva el patron al serializar`() {
        val d = descriptor(3, "fcc")
        val vuelta = MamparaPaflonDescriptor.parsear(d.serializar())
        assertEquals("fcc", vuelta?.patron)
        assertEquals(d, vuelta)
    }

    @Test
    fun `un descriptor viejo sin patron se sigue leyendo`() {
        // Los archivados antes del editor tienen siete campos: deben abrirse en automático, no
        // fallar. Es lo que protege los diseños ya guardados.
        val viejo = "MPF1:300.0;210.0;199.0;3;2.5;2.5;0"
        val d = MamparaPaflonDescriptor.parsear(viejo)
        assertTrue(d != null)
        assertEquals("", d!!.patron)
        assertEquals("fcf", MamparaModulos.desde(d).tramos.single().map { it.tipo }.joinToString(""))
    }
}
