package crystal.crystal.taller.nova

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * En L y en C conviven dos anchos: la medida que se MIDIÓ y el ancho ÚTIL (esa medida menos el
 * descuento de esquina que reparte `NovaCorrediza.medidasCalculoNlApa`). Cada uno manda en una
 * cosa distinta:
 *
 * - **Cuántas hojas** lleva el lado: regla general de 60 cm por división sobre la medida MEDIDA.
 *   Un lado de 122 pasa de 120, así que son 3 divisiones. Si en esa ventana corresponden 2, las
 *   escribe el vidriero a mano.
 * - **Qué medida tiene cada pieza**: el ancho útil, porque el parante de esquina ya se comió su
 *   parte del aluminio.
 *
 * El error que destapó esto: los materiales contaban sobre el útil (122 - esquina < 120 -> 2
 * divisiones) mientras el resto contaba sobre la medida real. Los números de abajo son los del
 * caso real reportado.
 */
class DivisionesAnchoUtilTest {

    @Test
    fun `un lado de 122 lleva 3 divisiones por la regla de los 60`() {
        assertEquals(3, NovaCalculos.divisiones(122f, 0))
    }

    @Test
    fun `contar sobre el ancho util daria 2, y por eso no se cuenta ahi`() {
        // Cualquier esquinero descuenta lo suficiente para bajar de 120: si la cuenta saliera del
        // ancho útil, el mismo lado de 122 pasaría a 2 hojas sin que nadie lo pidiera.
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
    fun `las divisiones manuales mandan sobre el ancho`() {
        // Es la salida del vidriero cuando el reparto automático no es el que quiere.
        assertEquals(2, NovaCalculos.divisiones(122f, 2))
        assertEquals(2, NovaCalculos.divisiones(119.5f, 2))
    }

    @Test
    fun `fijos y corredizas del lado de 122 con sus 3 divisiones`() {
        assertEquals(2, NovaCalculos.nFijos(122f, 3))
        assertEquals(1, NovaCalculos.nCorredizas(122f, 3))
    }
}
