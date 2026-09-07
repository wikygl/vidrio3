package crystal.crystal.taller.nova

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * En L y en C el ancho que se corta y se dibuja es el ÚTIL: la medida menos el descuento de
 * esquina (`NovaCorrediza.medidasCalculoNlApa`). Las divisiones automáticas salen de ese ancho,
 * no de la medida original, y el salto de 2 a 3 cae justo en 120 (60 por división).
 *
 * Caso real que lo destapó: lado de 122 en L. Sin descuento cuenta 3 divisiones (y de ahí
 * "fjs: 2; czs: 1"); con el descuento de esquina cuenta 2, que es lo que dibuja el diseño y lo
 * que sale en la lista de materiales. La referencia debe MOSTRAR 122 pero CONTAR sobre el útil.
 */
class DivisionesAnchoUtilTest {

    @Test
    fun `un lado de 122 cuenta 3 divisiones sin descuento y 2 con el descuento de esquina`() {
        assertEquals(3, NovaCalculos.divisiones(122f, 0))
        // Cualquier esquinero descuenta al menos ~2.5 y lo baja de 120.
        assertEquals(2, NovaCalculos.divisiones(122f - 2.5f, 0))
        assertEquals(2, NovaCalculos.divisiones(122f - 5f, 0))
    }

    @Test
    fun `el salto de 2 a 3 divisiones esta en 120`() {
        assertEquals(2, NovaCalculos.divisiones(119.9f, 0))
        assertEquals(2, NovaCalculos.divisiones(120f, 0))
        assertEquals(3, NovaCalculos.divisiones(120.1f, 0))
    }

    @Test
    fun `con divisiones manuales el ancho no manda`() {
        assertEquals(2, NovaCalculos.divisiones(122f, 2))
        assertEquals(2, NovaCalculos.divisiones(119.5f, 2))
    }

    @Test
    fun `los fijos y corredizas del lado de 122 cambian con el ancho util`() {
        // Lo que mostraba la referencia con la medida sin descontar...
        assertEquals(2, NovaCalculos.nFijos(122f, 3))
        assertEquals(1, NovaCalculos.nCorredizas(122f, 3))
        // ...y lo que corresponde al ancho útil, que es lo que se corta.
        assertEquals(1, NovaCalculos.nFijos(119.5f, 2))
        assertEquals(1, NovaCalculos.nCorredizas(119.5f, 2))
    }
}
