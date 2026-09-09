package crystal.crystal.taller.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.ceil

/**
 * El número de TRAMOS es el origen del que salen las cantidades que van por tramo (puentes, U de
 * mocheta parante…). Se cuenta, no sale de una tabla por número de divisiones.
 *
 * El único límite es de 5 módulos por tramo. El tamaño del módulo no parte la ventana: si el
 * vidriero pone dos divisiones en una ventana de mil, se respeta.
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
    fun `el ancho del modulo no parte la ventana`() {
        // Decisión del vidriero: pocas divisiones en una ventana muy ancha se respetan tal cual.
        // Partir por tamaño (se probó a 360) dejaba una ventana de 1000 con 5 divisiones en tres
        // tramos, uno de ellos de un solo módulo.
        assertEquals(1, NovaCalculos.tramos(1000f, 5))
        assertEquals(1, NovaCalculos.tramos(1000f, 2))
        assertEquals(1, NovaCalculos.tramos(500f, 2))
        assertEquals(1, NovaCalculos.tramos(400f, 1))
    }

    @Test
    fun `los tramos solo dependen del numero de modulos`() {
        var ancho = 60f
        while (ancho <= 1200f) {
            for (div in 1..20) {
                assertEquals(
                    "ancho=$ancho div=$div",
                    ceil(div / 5.0).toInt(),
                    NovaCalculos.tramos(ancho, div)
                )
            }
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
