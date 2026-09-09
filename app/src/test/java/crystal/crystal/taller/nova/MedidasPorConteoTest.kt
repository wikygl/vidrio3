package crystal.crystal.taller.nova

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Las medidas y las cantidades que van por tramo se CUENTAN sobre el reparto de módulos; no salen
 * de tablas por número de divisiones. Este test fija el resultado del cambio y, sobre todo, los
 * casos donde las tablas antiguas fallaban.
 */
class MedidasPorConteoTest {

    private val anchos = listOf(120f, 240f, 300f, 420f, 586f, 600f, 720f, 900f)

    @Test
    fun `la medida del puente es el ancho del tramo`() {
        for (ancho in anchos) for (div in 1..20) {
            val medidas = NovaCalculos.medidasDeTramos(ancho, div)
            val grupos = NovaCalculos.gruposDivisionesPorTramo(ancho, div)
            assertEquals("ancho=$ancho div=$div", grupos.size, medidas.size)
            // Los tramos, con sus parantes, suman el ancho de la ventana.
            val suma = medidas.sum() + (grupos.size - 1) * 2.5f
            assertEquals("ancho=$ancho div=$div", ancho.toDouble(), suma.toDouble(), 0.01)
            assertEquals("ancho=$ancho div=$div", medidas.max(), NovaCalculos.mPuentes1(ancho, div), 0.01f)
        }
    }

    @Test
    fun `la segunda medida solo existe si el reparto es desigual`() {
        // 14 se reparte [5,4,5]: hay una segunda medida, la del tramo de 4.
        val ancho = 600f
        val medidas = NovaCalculos.medidasDeTramos(ancho, 14)
        assertEquals(listOf(5, 4, 5), NovaCalculos.gruposDivisionesPorTramo(ancho, 14))
        assertEquals(medidas.max(), NovaCalculos.mPuentes1(ancho, 14), 0.01f)
        assertEquals(medidas.min(), NovaCalculos.mPuentes2(ancho, 14), 0.01f)
        assertTrue(NovaCalculos.mPuentes2(ancho, 14) > 0f)

        // 10 se reparte [5,5]: todos iguales, no hay segunda medida.
        assertEquals(listOf(5, 5), NovaCalculos.gruposDivisionesPorTramo(ancho, 10))
        assertEquals(0f, NovaCalculos.mPuentes2(ancho, 10), 0.01f)
    }

    @Test
    fun `con 10 divisiones el puente mide el tramo completo`() {
        // La tabla aparente daba 178.5 y 238 para 600 de ancho, como si hubiera más tramos.
        val ancho = 600f
        assertEquals(2, NovaCalculos.tramos(ancho, 10))
        assertEquals(298.75f, NovaCalculos.mPuentes1(ancho, 10, "apa"), 0.01f)
        assertEquals(0f, NovaCalculos.mPuentes2(ancho, 10, "apa"), 0.01f)
    }

    @Test
    fun `el acabado no cambia la medida del tramo`() {
        for (ancho in anchos) for (div in 1..20) {
            assertEquals(
                "ancho=$ancho div=$div",
                NovaCalculos.mPuentes1(ancho, div, "apa"),
                NovaCalculos.mPuentes1(ancho, div, "ina"),
                0.01f
            )
        }
    }

    @Test
    fun `la U parante de fijos cuenta los fijos de los extremos de cada tramo`() {
        // Con 7 divisiones el reparto es fccf | fcf: dos tramos que empiezan y acaban en fijo.
        // La tabla antigua devolvía 2 sin mirar los tramos.
        assertEquals(4, NovaCalculos.fijoUParante(7, 300f))
        assertEquals(4, NovaCalculos.fijoUParante(9, 300f))
        assertEquals(4, NovaCalculos.fijoUParante(10, 300f))
        assertEquals(6, NovaCalculos.fijoUParante(11, 300f))
        assertEquals(6, NovaCalculos.fijoUParante(15, 300f))
        // Dos divisiones son fijo + corrediza: solo un extremo es fijo.
        assertEquals(1, NovaCalculos.fijoUParante(2, 300f))
    }
}
