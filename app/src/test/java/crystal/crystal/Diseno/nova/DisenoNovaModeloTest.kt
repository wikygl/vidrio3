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
    fun `las etiquetas sueltas se conservan`() {
        val conGiro = "{nova,apa,[300,200:Tl<148.7>(s<150>(f<74.3>c<74.3>)) A<90> Tl<148.7>(s<150>(f<74.3>c<74.3>))]}"
        val d = DisenoNova.desdePaquete(conGiro)
        assertNotNull(d); d!!
        assertEquals(2, d.nTramos)
        assertEquals(listOf("A<90>"), d.etiquetas)
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
}
