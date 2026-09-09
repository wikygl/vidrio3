package crystal.crystal.taller.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.ceil

/**
 * El número de TRAMOS es el origen del que salen las cantidades que van por tramo (puentes, U de
 * mocheta parante…). Se cuenta, no sale de una tabla por número de divisiones.
 *
 * Dos límites, manda el que obligue a más tramos: 5 módulos por tramo y 360 cm de ancho por tramo.
 */
class TramosYUMochetaTest {

    @Test
    fun `manda el limite de 5 modulos`() {
        assertEquals(1, NovaCalculos.tramos(300f, 5))
        assertEquals(2, NovaCalculos.tramos(360f, 6))
        assertEquals(2, NovaCalculos.tramos(586f, 10))
        assertEquals(3, NovaCalculos.tramos(660f, 11))
        assertEquals(3, NovaCalculos.tramos(900f, 15))
    }

    @Test
    fun `manda el limite de 360 cuando las divisiones se fijan a mano`() {
        // 500 en dos divisiones son módulos de 250: no entran en un tramo.
        assertEquals(2, NovaCalculos.tramos(500f, 2))
        assertEquals(2, NovaCalculos.tramos(400f, 1))
        assertEquals(2, NovaCalculos.tramos(586f, 3))
        // Justo en el límite no hace falta partir.
        assertEquals(1, NovaCalculos.tramos(360f, 2))
        assertEquals(2, NovaCalculos.tramos(360.1f, 2))
    }

    @Test
    fun `con divisiones automaticas el limite de 360 nunca se activa`() {
        // Cinco módulos de 60 miden 300: el de módulos siempre llega antes.
        var ancho = 60f
        while (ancho <= 1200f) {
            val div = NovaCalculos.divisiones(ancho, 0)
            assertEquals(
                "ancho=$ancho div=$div",
                ceil(div / 5.0).toInt(),
                NovaCalculos.tramos(ancho, div)
            )
            ancho += 5f
        }
    }

    @Test
    fun `los tramos del diseno coinciden con la cuenta`() {
        var ancho = 60f
        while (ancho <= 1200f) {
            val div = NovaCalculos.divisiones(ancho, 0)
            val grupos = NovaCalculos.gruposDivisionesMochetaPorModelo(ancho, div, "nn")
            assertEquals("ancho=$ancho div=$div", NovaCalculos.tramos(ancho, div), grupos.size)
            assertEquals("los módulos deben sumar las divisiones", div, grupos.sum())
            assertTrue("ningún tramo pasa de 5 módulos", grupos.all { it <= 5 })
            ancho += 5f
        }
    }

    @Test
    fun `la U de mocheta parante son dos por tramo`() {
        for (div in 1..20) {
            val ancho = div * 60f
            assertEquals(
                "div=$div",
                2 * NovaCalculos.tramos(ancho, div),
                NovaCalculos.mochetaUParante(div, ancho)
            )
        }
    }

    @Test
    fun `la ventana de 586 con 10 divisiones lleva 4 U de mocheta parante`() {
        // Dos tramos de 5 módulos (Tl 291.7 P 2.5 Tl 291.7): 2 U por tramo.
        // La tabla vieja la metía entre las de tres tramos y pedía 6.
        assertEquals(10, NovaCalculos.divisiones(586f, 0))
        assertEquals(2, NovaCalculos.tramos(586f, 10))
        assertEquals(4, NovaCalculos.mochetaUParante(10, 586f))
    }
}
