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

    /**
     * Caso real medido en obra (captura del 2026-08-28): de aquí salen los largos que la lista de
     * materiales imprime, incluido el paflón horizontal del puente que faltaba.
     */
    @Test
    fun `caso real cf-fc reparte 168 por tramo`() {
        val d = MamparaPaflonDescriptor(
            ancho = 344.8f, alto = 274.8f, altoHoja = 210f,
            divisiones = 4, bastidor = 8.25f, marco = 2.5f, nMochetas = 0,
            patron = "cf|fc"
        )
        val m = MamparaModulos.desde(d)
        assertEquals(2, m.tramos.size)
        assertEquals(1, m.nParantesTramo)
        assertEquals(2, m.nFijos)
        assertEquals(2, m.nCorredizas)
        // 75.8 por vidrio y 168 por tramo: son los números de la lista de materiales.
        assertEquals(75.8, m.vidrioAncho.toDouble(), 0.05)
        m.tramos.forEach { assertEquals(168.0, m.anchoTramo(it).toDouble(), 0.05) }
        // Y los dos tramos más su parante llenan el hueco útil exacto.
        val util = d.ancho - 2 * d.marco
        assertEquals(
            util.toDouble(),
            m.tramos.sumOf { m.anchoTramo(it).toDouble() } + m.nParantesTramo * MamparaModulos.P_ALT,
            0.05
        )
    }

    @Test
    fun `sin patron el tramo unico ocupa todo el hueco util`() {
        // El caso "normal" por divisiones: un solo tramo, y su paflón de puente mide el hueco
        // entero (339.8), que es la pieza que faltaba en esa variante.
        val d = MamparaPaflonDescriptor(
            ancho = 344.8f, alto = 274.8f, altoHoja = 210f,
            divisiones = 4, bastidor = 8.25f, marco = 2.5f, nMochetas = 0
        )
        val m = MamparaModulos.desde(d)
        assertEquals(1, m.tramos.size)
        assertEquals(339.8, m.anchoTramo(m.tramos.single()).toDouble(), 0.05)
    }

    // ===== Mampara desigual: anchos fijados a mano =====

    /**
     * El caso que pidió el usuario: cfc con las dos corredizas clavadas a 100, y el fijo del centro
     * absorbiendo lo que sobra. Es la razón de ser de la mampara desigual.
     */
    @Test
    fun `fijar las corredizas ensancha el fijo del centro`() {
        val d = MamparaPaflonDescriptor(
            ancho = 344.8f, alto = 274.8f, altoHoja = 210f,
            divisiones = 3, bastidor = 8.25f, marco = 2.5f, nMochetas = 0,
            patron = "c<100>fc<100>"
        )
        val m = MamparaModulos.desde(d)
        val mods = m.modulos
        assertEquals(3, mods.size)
        assertEquals(100.0, mods[0].ancho.toDouble(), 0.05)
        assertEquals(100.0, mods[2].ancho.toDouble(), 0.05)
        // cfc lleva 4 bastidores visibles: hueco 339.8 - 4*8.25 = 306.8 de vidrio.
        // Las corredizas se llevan 200, así que al fijo le quedan 106.8 y sale MÁS ancho que ellas.
        assertEquals(106.8, mods[1].ancho.toDouble(), 0.05)
        assertTrue(mods[1].ancho > mods[0].ancho)
        // Y el conjunto sigue llenando el hueco exacto: si no, el vidrio no entra en obra.
        assertEquals((d.ancho - 2 * d.marco).toDouble(), m.anchoTramo(m.tramos.single()).toDouble(), 0.05)
    }

    @Test
    fun `sin medidas fijadas el reparto sigue siendo parejo`() {
        val d = MamparaPaflonDescriptor(
            ancho = 344.8f, alto = 274.8f, altoHoja = 210f,
            divisiones = 3, bastidor = 8.25f, marco = 2.5f, nMochetas = 0,
            patron = "cfc"
        )
        val anchos = MamparaModulos.desde(d).modulos.map { it.ancho.toDouble() }
        anchos.forEach { assertEquals(306.8 / 3, it, 0.05) }
    }

    @Test
    fun `fijar solo uno reparte el resto entre los demas`() {
        val d = MamparaPaflonDescriptor(
            ancho = 344.8f, alto = 274.8f, altoHoja = 210f,
            divisiones = 3, bastidor = 8.25f, marco = 2.5f, nMochetas = 0,
            patron = "c<80>fc"
        )
        val mods = MamparaModulos.desde(d).modulos
        assertEquals(80.0, mods[0].ancho.toDouble(), 0.05)
        // Los otros dos se reparten 306.8 - 80 = 226.8
        assertEquals(113.4, mods[1].ancho.toDouble(), 0.05)
        assertEquals(113.4, mods[2].ancho.toDouble(), 0.05)
    }

    @Test
    fun `un ancho invalido invalida el patron entero`() {
        // Mejor caer al automático que colar un vidrio de cero a la lista de cortes.
        assertNull(MamparaModulos.patronManual("c<0>fc"))
        assertNull(MamparaModulos.patronManual("c<-5>fc"))
        assertNull(MamparaModulos.patronManual("c<abc>fc"))
        assertNull(MamparaModulos.patronManual("c<100fc"))
    }

    @Test
    fun `el patron con anchos va y vuelve intacto`() {
        val original = "c<100.0>fc<100.0>|f<50.5>c"
        val ida = MamparaModulos.patronManual(original)!!
        assertEquals(original, MamparaModulos.serializarPatron(ida))
        assertEquals(2, ida.size)
        assertEquals(100f, ida[0][0].ancho)
        assertNull(ida[0][1].ancho)
        assertEquals(50.5f, ida[1][0].ancho)
    }

    @Test
    fun `el descriptor conserva los anchos al archivar`() {
        val d = MamparaPaflonDescriptor(
            ancho = 344.8f, alto = 274.8f, altoHoja = 210f,
            divisiones = 3, bastidor = 8.25f, marco = 2.5f, nMochetas = 0,
            patron = "c<100.0>fc<100.0>"
        )
        assertEquals(d, MamparaPaflonDescriptor.parsear(d.serializar()))
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
