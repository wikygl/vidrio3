package crystal.crystal.pos

import crystal.crystal.Listado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Los ambientes: la sala, el consultorio, el piso donde va cada ítem.
 *
 * Es lo que agrupa la proforma de una obra entera —una clínica, un hotel—, así que lo que se
 * comprueba aquí es el orden en que salen los sitios y que no se pierda ningún ítem por el camino.
 */
class AmbientesDeProformaTest {

    private fun item(producto: String, ambiente: String? = null) = Listado(
        escala = "m2", uni = "Metros", medi1 = 1f, medi2 = 1f, medi3 = 0f, canti = 1f,
        piescua = 11.1f, precio = 100f, costo = 100f, producto = producto, peri = 4f,
        metcua = 1f, metli = 0f, metcub = 0f, color = 0, uri = ""
    ).also { it.ambiente = ambiente }

    /** Los ambientes salen COMO SE MIDIERON, no en orden alfabético: así se recorre la obra. */
    @Test
    fun los_ambientes_salen_en_el_orden_en_que_se_midieron() {
        val lista = listOf(
            item("Ventana", "Sala sexto piso"),
            item("Mampara", "Sala sexto piso"),
            item("Puerta", "Sala de partos"),
            item("Ventana alta", "Admisión")
        )
        assertEquals(
            listOf("Sala sexto piso", "Sala de partos", "Admisión"),
            AmbientesDeProforma.enLaLista(lista)
        )
        val grupos = AmbientesDeProforma.agrupar(lista)
        assertEquals(3, grupos.size)
        assertEquals("Sala sexto piso", grupos[0].first)
        assertEquals("los dos ítems de la sala no quedaron juntos", 2, grupos[0].second.size)
        assertEquals("Puerta", grupos[1].second[0].producto)
    }

    /** Los ítems sin ambiente no se pierden: van juntos al final, sin encabezado. */
    @Test
    fun los_que_no_tienen_ambiente_van_al_final() {
        val lista = listOf(
            item("Ventana", "Sala de partos"),
            item("Espejo"),
            item("Puerta", "Sala de partos"),
            item("Repisa")
        )
        val grupos = AmbientesDeProforma.agrupar(lista)
        assertEquals(2, grupos.size)
        assertEquals("Sala de partos", grupos[0].first)
        assertEquals(2, grupos[0].second.size)
        assertEquals("los sueltos no fueron al final", AmbientesDeProforma.SIN_AMBIENTE, grupos[1].first)
        assertEquals(2, grupos[1].second.size)
        // Y no se perdió ninguno.
        assertEquals(lista.size, grupos.sumOf { it.second.size })
    }

    /** Sin ambientes, la proforma sale como salía: una sola tirada de ítems. */
    @Test
    fun sin_ambientes_todo_va_en_un_solo_grupo() {
        val lista = listOf(item("Ventana"), item("Puerta"))
        assertTrue(!AmbientesDeProforma.hayEnLaLista(lista))
        val grupos = AmbientesDeProforma.agrupar(lista)
        assertEquals(1, grupos.size)
        assertEquals(AmbientesDeProforma.SIN_AMBIENTE, grupos[0].first)
        assertEquals(2, grupos[0].second.size)
    }

    /** Se guarda limpio, y en blanco se lo quita: un ambiente vacío no es un ambiente. */
    @Test
    fun el_ambiente_se_guarda_limpio() {
        val uno = item("Ventana")
        AmbientesDeProforma.guardar(uno, "  Sala de partos  ")
        assertEquals("Sala de partos", uno.ambiente)
        AmbientesDeProforma.guardar(uno, "   ")
        assertNull(uno.ambiente)
        assertEquals("", AmbientesDeProforma.de(uno))
    }

    /** Un ítem puede llevar a la vez su ambiente y sus opciones: son dos cosas distintas. */
    @Test
    fun el_ambiente_y_las_opciones_conviven() {
        val uno = item("Ventana", "Sala de partos")
        OpcionesDeProforma.guardar(uno, listOf(OpcionDeProforma("Policarbonato", 95f)))
        assertEquals("Sala de partos", AmbientesDeProforma.de(uno))
        assertEquals(1, OpcionesDeProforma.de(uno).size)
    }
}
