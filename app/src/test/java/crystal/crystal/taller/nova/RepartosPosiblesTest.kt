package crystal.crystal.taller.nova

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Los repartos que se ofrecen al arrastrar sobre el diseño, y que el elegido mande sobre todo lo
 * que se calcula por tramo.
 */
class RepartosPosiblesTest {

    @After
    fun limpiar() {
        NovaCalculos.repartoManual = null
    }

    @Test
    fun `con 10 divisiones hay cuatro repartos`() {
        assertEquals(
            listOf(listOf(5, 5), listOf(3, 4, 3), listOf(2, 3, 3, 2), listOf(2, 2, 2, 2, 2)),
            NovaCalculos.repartosPosibles(10)
        )
    }

    @Test
    fun `ningun reparto tiene tramos de mas de 5 ni de menos de 2`() {
        for (div in 1..20) {
            val opciones = NovaCalculos.repartosPosibles(div)
            assertTrue("div=$div sin opciones", opciones.isNotEmpty())
            assertTrue("div=$div más de 5 opciones", opciones.size <= 5)
            for (r in opciones) {
                assertEquals("div=$div reparto $r no suma", div, r.sum())
                assertTrue("div=$div reparto $r tiene un tramo de más de 5", r.all { it <= 5 })
                if (r.size > 1) assertTrue("div=$div reparto $r tiene un tramo de menos de 2", r.all { it >= 2 })
            }
            // El primero es siempre el automático: el menor número de tramos.
            assertEquals("div=$div", NovaCalculos.tramos(div * 60f, div), opciones.first().size)
        }
    }

    @Test
    fun `el reparto elegido manda en el dibujo y en las medidas`() {
        val ancho = 726f
        // Automático: dos tramos de 5, con un tramo largo.
        assertEquals(listOf(5, 5), NovaCalculos.gruposDivisionesPorTramo(ancho, 10))
        assertEquals(361.75f, NovaCalculos.mPuentes1(ancho, 10), 0.01f)

        // Elegido a mano: tres tramos, y el puente pasa a medir el tramo grande.
        NovaCalculos.repartoManual = listOf(3, 4, 3)
        assertEquals(listOf(3, 4, 3), NovaCalculos.gruposDivisionesPorTramo(ancho, 10))
        val medidas = NovaCalculos.medidasDeTramos(ancho, 10)
        assertEquals(3, medidas.size)
        assertEquals(medidas.max(), NovaCalculos.mPuentes1(ancho, 10), 0.01f)
        assertEquals(medidas.min(), NovaCalculos.mPuentes2(ancho, 10), 0.01f)
        // Tres tramos: dos U de mocheta por tramo.
        assertEquals(6, NovaCalculos.mochetaUParante(10, ancho))
        // Y el orden de módulos también se parte en tres.
        assertEquals(3, NovaCalculos.ordenDivisConParantes(10, ancho).split(";P;").size)
    }

    @Test
    fun `un reparto de otra medida no se aplica`() {
        NovaCalculos.repartoManual = listOf(3, 4, 3)   // suma 10
        assertEquals(listOf(4, 4), NovaCalculos.gruposDivisionesPorTramo(480f, 8))
    }
}
