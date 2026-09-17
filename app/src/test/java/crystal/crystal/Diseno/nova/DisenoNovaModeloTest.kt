package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * El modelo tiene que leer y volver a escribir los paquetes REALES sin perder nada. Los dos
 * primeros son ventanas de verdad, tal como salieron de la app.
 */
class DisenoNovaModeloTest {

    // Ventana de Jorge: 385.9 x 246.8, aparente, doble puente (mocheta arriba y abajo).
    private val jorge =
        "{nova,apa,[385.9,246.8:Tl<219>(m<48.4>(f<109.5>f<109.5>);s<150>(f<54.7>c<54.7>c<54.7>f<54.7>);m<48.4>(f<109.5>f<109.5>))" +
            " P<2.5> Tl<164.3>(m<48.4>(f<164.3>);s<150>(f<54.7>c<54.7>f<54.7>);m<48.4>(f<164.3>))]}"

    // Ventana de Abel: 650 x 160, inaparente, remate normal, tres tramos [4,3,4].
    private val abel =
        "{nova,ina,[650,160:Tl<234.5>(s<114.2>(f<58.6>c<58.6>c<58.6>f<58.6>);m<43.3>(f<117.2>f<117.2>))" +
            " P<2.5> Tl<175.9>(s<114.2>(f<58.6>c<58.6>f<58.6>);m<43.3>(f<175.9>))" +
            " P<2.5> Tl<234.5>(s<114.2>(f<58.6>c<58.6>c<58.6>f<58.6>);m<43.3>(f<117.2>f<117.2>))]}"

    @Test
    fun `lee la ventana de dos tramos con doble puente`() {
        val d = DisenoNova.desdePaquete(jorge)
        assertNotNull(d); d!!
        assertEquals("apa", d.acabado)
        assertEquals(385.9f, d.ancho, 0.01f)
        assertEquals(246.8f, d.alto, 0.01f)
        assertEquals(2, d.nTramos)
        // Doble puente: mocheta, sistema y mocheta en cada tramo.
        assertEquals(3, d.tramos[0].franjas.size)
        assertEquals(2, d.tramos[0].mochetas.size)
        assertEquals(4, d.tramos[0].nModulosSistema)
        assertEquals(3, d.tramos[1].nModulosSistema)
        // El sistema del primer tramo es f c c f.
        assertEquals(listOf('f', 'c', 'c', 'f'), d.tramos[0].sistema!!.modulos.map { it.tipo })
        assertEquals(7, d.nModulos)
        assertEquals(4, d.nFijos)
        assertEquals(3, d.nCorredizas)
    }

    @Test
    fun `lee la ventana de tres tramos`() {
        val d = DisenoNova.desdePaquete(abel)
        assertNotNull(d); d!!
        assertEquals("ina", d.acabado)
        assertEquals(3, d.nTramos)
        assertEquals(11, d.nModulos)
        assertEquals(listOf(4, 3, 4), d.tramos.map { it.nModulosSistema })
        assertEquals(2, d.nParantes)
        // El ancho útil descuenta los dos parantes.
        assertEquals(645f, d.anchoUtil(), 0.01f)
        // La mocheta del tramo del medio es un solo paño de todo el ancho del tramo.
        assertEquals(1, d.tramos[1].mochetas[0].modulos.size)
    }

    @Test
    fun `lo que lee lo vuelve a escribir igual`() {
        for (paquete in listOf(jorge, abel)) {
            val d = DisenoNova.desdePaquete(paquete)!!
            val ida = d.aPaquete()
            val vuelta = DisenoNova.desdePaquete(ida)
            assertNotNull("no se pudo releer:\n$ida", vuelta)
            // Comparar el modelo, no el texto: los espacios pueden variar.
            assertEquals(d, vuelta)
        }
    }

    @Test
    fun `el pliegue se queda con la pared que dobla`() {
        val conGiro = "{nova,apa,[300,200:Tl<148.7>(s<150>(f<74.3>c<74.3>)) A<90> Tl<148.7>(s<150>(f<74.3>c<74.3>))]}"
        val d = DisenoNova.desdePaquete(conGiro)
        assertNotNull(d); d!!
        assertEquals(2, d.nTramos)
        // El pliegue NO es una etiqueta del conjunto: es un sitio, dice entre qué dos paredes
        // dobla la ventana. Guardado en la lista suelta se reescribía al final del paquete, o
        // sea en ninguna parte, y la ventana en L salía rectangular en cuanto se editaba.
        assertEquals(emptyList<String>(), d.etiquetas)
        assertEquals("A<90>", d.tramos[1].pliegue)
    }

    @Test
    fun `un tramo suelto sin bloque T tambien se lee`() {
        val simple = "{nova,ina,[120,150:Tl<120>(s<100>(f<60>c<60>);m<50>(f<120>))]}"
        val d = DisenoNova.desdePaquete(simple)
        assertNotNull(d); d!!
        assertEquals(1, d.nTramos)
        assertEquals(0, d.nParantes)
        assertEquals(120f, d.anchoUtil(), 0.01f)
    }

    @Test
    fun `lo que no es un paquete de nova devuelve null`() {
        assertNull(DisenoNova.desdePaquete(""))
        assertNull(DisenoNova.desdePaquete("cualquier cosa"))
        assertNull(DisenoNova.desdePaquete("{nova,apa,[sin dos puntos]}"))
    }

    @Test
    fun `los modulos sin ancho se admiten`() {
        val sinAnchos = "{nova,apa,[240,200:Tl<240>(s<150>(fccf))]}"
        val d = DisenoNova.desdePaquete(sinAnchos)
        assertNotNull(d); d!!
        assertEquals(4, d.nModulos)
        assertTrue(d.tramos[0].sistema!!.modulos.all { it.ancho == null })
        // Y al escribirlo salen sin medida, no con cero.
        assertTrue(d.aPaquete().contains("(fccf)"))
    }

    /**
     * La pared de una esquina que no es un rectángulo viaja con su silueta en su tramo (`L<…>`),
     * relativa a esa pared, y sobrevive a la ida y vuelta, al reparto de anchos y a partir la
     * pared con un parante (se queda en el primer trozo).
     */
    @Test
    fun la_silueta_de_una_pared_viaja_en_su_tramo() {
        val l = "{nova,apa,[280,220: Tl<280>(L<0/60|145/0|280/40|280/220|0/220>;s(f c c)) A<90> Tl<64.5>(s(f))]}"
        val d = DisenoNova.desdePaquete(l)
        assertNotNull(d)
        assertEquals(2, d!!.tramos.size)
        assertEquals(5, d.tramos[0].contorno.size)
        assertEquals(145f to 0f, d.tramos[0].contorno[1])
        assertTrue("la pared recta no lleva silueta", d.tramos[1].contorno.isEmpty())
        assertTrue(d.doblaEnEsquina)

        val ida = d.aPaquete()
        assertTrue("el tag no se escribe: $ida", ida.contains("L<0/60|145/0|280/40|280/220|0/220>"))
        assertEquals(d, DisenoNova.desdePaquete(ida))

        val repartido = d.conAnchosRepartidos()
        assertEquals(5, repartido.tramos[0].contorno.size)
        val partido = d.conTramoPartido(0, 1)
        assertEquals(3, partido.tramos.size)
        assertEquals(5, partido.tramos[0].contorno.size)
        assertTrue("el segundo trozo no lleva la silueta", partido.tramos[1].contorno.isEmpty())
        assertEquals(5, partido.conTramosUnidos(0).tramos[0].contorno.size)
    }

    /**
     * Lo que trae el apunte de una esquina armada sobre figuras se pone por lado: la silueta en
     * el primer tramo del lado y los parantes en la frontera de hojas más cercana a donde se
     * marcaron. En una ventana de 280 con tres hojas de 93.3, un parante marcado a 100 cae
     * entre la primera y la segunda.
     */
    @Test
    fun lo_medido_de_cada_pared_se_pone_en_su_lado() {
        val l = "{nova,apa,[280,220: Tl<280>(s(f c c)) A<90> Tl<64.5>(s(f))]}"
        val d = DisenoNova.desdePaquete(l)!!
        val silueta = listOf(0f to 60f, 145f to 0f, 280f to 40f, 280f to 220f, 0f to 220f)
        val con = d.conSiluetasYParantesPorLado(listOf(silueta, null), listOf(listOf(100f), emptyList()))
        assertEquals(silueta, con.tramos[0].contorno)
        assertTrue(con.tramos[1].contorno.isEmpty())
        assertEquals(listOf(0), con.tramos[0].sistema!!.parantes)
        assertTrue(con.tramos[1].sistema!!.parantes.isEmpty())
        assertTrue("el parante no se escribe: ${con.aPaquete()}", con.aPaquete().contains("f;P;cc"))
        assertEquals(con, DisenoNova.desdePaquete(con.aPaquete()))
        // Sin nada que poner, el diseño no se toca.
        assertEquals(d, d.conSiluetasYParantesPorLado(listOf(null, null), listOf(emptyList(), emptyList())))
    }

    /**
     * Cada pared con su alto: la de al lado, más baja, cuelga más abajo del dintel y mide lo suyo;
     * la ventana mide lo que la pared más alta. El 3D sube cada una hasta su alto.
     */
    @Test
    fun cada_lado_con_su_alto() {
        val l = "{nova,apa,[280,221.9: Tl<280>(s(f c c)) A<90> Tl<64.5>(s(f))]}"
        val d = DisenoNova.desdePaquete(l)!!
        val con = d.conAltosDeLado(listOf(221.9f, 162f))
        assertEquals(221.9f, con.alto, 0.01f)
        assertEquals("el primer lado no cambia", 0f, con.tramos[0].alto, 0.01f)
        assertEquals(162f, con.tramos[1].alto, 0.01f)
        assertEquals("cuelga lo que le falta", 59.9f, con.tramos[1].caida, 0.01f)
        val planta = PlantaDelDiseno.de(con)
        assertEquals(listOf(221.9f, 162f), planta.paredes.map { it.altoCm })
        // Si la segunda es la alta, la ventana crece hasta ella y la primera es la que cuelga.
        val alReves = d.conAltosDeLado(listOf(221.9f, 250f))
        assertEquals(250f, alReves.alto, 0.01f)
        assertEquals(221.9f, alReves.tramos[0].alto, 0.01f)
        assertEquals(0f, alReves.tramos[1].alto, 0.01f)
        // Iguales: no se toca nada.
        assertEquals(d, d.conAltosDeLado(listOf(221.9f, 221.9f)))
    }
}
