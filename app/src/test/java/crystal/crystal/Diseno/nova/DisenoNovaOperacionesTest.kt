package crystal.crystal.Diseno.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Las operaciones del diseño como funciones puras sobre el modelo: partir y unir tramos, agregar
 * y quitar módulos, cambiar el tipo. Hoy esas reglas están metidas dentro de los diálogos,
 * mezcladas con cirugía de texto sobre el paquete.
 */
class DisenoNovaOperacionesTest {

    // Ventana de Abel: 650 x 160, inaparente, tres tramos [4,3,4] con su mocheta.
    private val abel = DisenoNova.desdePaquete(
        "{nova,ina,[650,160:Tl<234.5>(s<114.2>(f<58.6>c<58.6>c<58.6>f<58.6>);m<43.3>(f<117.2>f<117.2>))" +
            " P<2.5> Tl<175.9>(s<114.2>(f<58.6>c<58.6>f<58.6>);m<43.3>(f<175.9>))" +
            " P<2.5> Tl<234.5>(s<114.2>(f<58.6>c<58.6>c<58.6>f<58.6>);m<43.3>(f<117.2>f<117.2>))]}"
    )!!

    private val unTramo = DisenoNova.desdePaquete(
        "{nova,apa,[240,200:Tl<240>(s<150>(f<60>c<60>c<60>f<60>);m<50>(f<120>f<120>))]}"
    )!!

    /** Los anchos de los tramos, más sus parantes, tienen que dar el ancho de la ventana. */
    private fun cierraElAncho(d: DisenoNova): Boolean {
        val suma = d.tramos.sumOf { it.ancho.toDouble() }.toFloat() + d.nParantes * 2.5f
        return kotlin.math.abs(suma - d.ancho) < 0.05f
    }

    @Test
    fun `partir un tramo lo convierte en dos y parte todas sus franjas`() {
        // Tramo 0: sistema f c c f y mocheta de dos paños. Se parte después del 2º módulo.
        val d = unTramo.conTramoPartido(0, despuesDelModulo = 1)
        assertEquals(2, d.nTramos)
        assertEquals(listOf(2, 2), d.tramos.map { it.nModulosSistema })
        // La mocheta se parte con el tramo: un paño en cada uno.
        assertEquals(1, d.tramos[0].mochetas[0].modulos.size)
        assertEquals(1, d.tramos[1].mochetas[0].modulos.size)
        // No se pierde ni se inventa ningún módulo de sistema.
        assertEquals(unTramo.nModulos, d.nModulos)
        assertTrue(cierraElAncho(d))
    }

    @Test
    fun `una mocheta de un solo pano se duplica al partir`() {
        // El tramo del medio de Abel tiene la mocheta corrida: un paño para todo el tramo.
        assertEquals(1, abel.tramos[1].mochetas[0].modulos.size)
        val d = abel.conTramoPartido(1, despuesDelModulo = 0)
        assertEquals(4, d.nTramos)
        assertEquals(1, d.tramos[1].mochetas[0].modulos.size)
        assertEquals(1, d.tramos[2].mochetas[0].modulos.size)
        assertTrue(cierraElAncho(d))
    }

    @Test
    fun `unir dos tramos deshace la particion`() {
        val partido = unTramo.conTramoPartido(0, despuesDelModulo = 1)
        val unido = partido.conTramosUnidos(0)
        assertEquals(1, unido.nTramos)
        assertEquals(unTramo.nModulos, unido.nModulos)
        assertEquals(
            unTramo.tramos[0].sistema!!.modulos.map { it.tipo },
            unido.tramos[0].sistema!!.modulos.map { it.tipo }
        )
        assertTrue(cierraElAncho(unido))
    }

    @Test
    fun `no se puede partir por donde no queda nada a un lado`() {
        assertSame(unTramo, unTramo.conTramoPartido(0, despuesDelModulo = 3))  // el último
        assertSame(unTramo, unTramo.conTramoPartido(0, despuesDelModulo = -1))
        assertSame(unTramo, unTramo.conTramoPartido(9, despuesDelModulo = 1))  // tramo que no existe
    }

    @Test
    fun `agregar y quitar modulos reparte los anchos otra vez`() {
        val con = unTramo.conModuloAgregado(0, 0, despuesDe = 1, tipo = 'c')
        assertEquals(5, con.nModulos)
        assertEquals(listOf('f', 'c', 'c', 'c', 'f'), con.tramos[0].sistema!!.modulos.map { it.tipo })
        // Cinco módulos en 240: 48 cada uno.
        assertEquals(48f, con.tramos[0].sistema!!.modulos[0].ancho!!, 0.01f)
        assertTrue(cierraElAncho(con))

        val sin = con.conModuloQuitado(0, 0, indice = 2)
        assertEquals(4, sin.nModulos)
        assertEquals(60f, sin.tramos[0].sistema!!.modulos[0].ancho!!, 0.01f)
    }

    @Test
    fun `siempre queda al menos un modulo en la franja`() {
        var d = unTramo
        repeat(10) { d = d.conModuloQuitado(0, 0, indice = 0) }
        assertEquals(1, d.tramos[0].sistema!!.modulos.size)
    }

    @Test
    fun `cambiar el tipo no mueve las medidas`() {
        val d = unTramo.conTipoCambiado(0, 0, indice = 0)
        assertEquals('c', d.tramos[0].sistema!!.modulos[0].tipo)
        assertEquals(unTramo.nModulos, d.nModulos)
        assertEquals(
            unTramo.tramos[0].sistema!!.modulos.map { it.ancho },
            d.tramos[0].sistema!!.modulos.map { it.ancho }
        )
    }

    @Test
    fun `el reparto de anchos cierra el ancho de la ventana`() {
        for (d in listOf(abel, unTramo)) {
            val r = d.conAnchosRepartidos()
            assertTrue("no cierra: ${r.aPaquete()}", cierraElAncho(r))
            // Y dentro de cada tramo, los módulos de cada franja suman el ancho del tramo.
            for (t in r.tramos) {
                for (fr in t.franjas) {
                    val suma = fr.modulos.sumOf { (it.ancho ?: 0f).toDouble() }.toFloat()
                    assertEquals("franja de ${t.ancho}", t.ancho, suma, 0.05f)
                }
            }
        }
    }

    @Test
    fun `las operaciones se pueden encadenar y el paquete no deriva`() {
        val d = abel
            .conTramoPartido(0, despuesDelModulo = 1)
            .conModuloAgregado(0, 0, despuesDe = 0, tipo = 'f')
            .conTipoCambiado(1, 0, indice = 0)
            .conTramosUnidos(0)
        assertTrue(cierraElAncho(d))

        // El paquete lleva un decimal, así que un ancho de 53.75 se guarda como 53.7: el modelo
        // releído no es idéntico al de memoria, y no tiene por qué serlo. Lo que NO puede pasar
        // es que siga perdiendo en cada vuelta.
        val texto1 = d.aPaquete()
        val texto2 = DisenoNova.desdePaquete(texto1)!!.aPaquete()
        val texto3 = DisenoNova.desdePaquete(texto2)!!.aPaquete()
        assertEquals(texto2, texto3)

        // Y la geometría —tramos, módulos y tipos— sobrevive intacta.
        val releido = DisenoNova.desdePaquete(texto1)!!
        assertEquals(d.nTramos, releido.nTramos)
        assertEquals(d.nModulos, releido.nModulos)
        assertEquals(
            d.tramos.map { t -> t.franjas.map { fr -> fr.modulos.map { it.tipo } } },
            releido.tramos.map { t -> t.franjas.map { fr -> fr.modulos.map { it.tipo } } }
        )
    }
}
