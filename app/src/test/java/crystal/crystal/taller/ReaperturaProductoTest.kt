package crystal.crystal.taller

import crystal.crystal.casilla.EdicionProducto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Qué calculadora y qué medidas le tocan a un producto archivado al volver a abrirlo desde Productos. */
class ReaperturaProductoTest {

    @Test
    fun cada_prefijo_va_a_su_calculadora() {
        assertEquals("Ropero Melamina", ReaperturaProducto.destinoDe("Rm4")?.nombre)
        assertEquals("Puerta", ReaperturaProducto.destinoDe("P10, abel")?.nombre)
        assertEquals("Pivot", ReaperturaProducto.destinoDe("PV2")?.nombre)
        assertEquals("Mampara Paflón", ReaperturaProducto.destinoDe("MP1, Jorge")?.nombre)
        assertEquals("Nova Corrediza", ReaperturaProducto.destinoDe("Vni1")?.nombre)
        assertEquals("Vitroven", ReaperturaProducto.destinoDe("Vit3")?.nombre)
        assertNull(ReaperturaProducto.destinoDe("Xyz1"))
    }

    @Test
    fun el_numero_que_se_reutiliza_es_el_del_mismo_prefijo() {
        assertEquals(4, EdicionProducto.numeroPara("Rm4", "Rm"))
        assertEquals(10, EdicionProducto.numeroPara("P10, abel", "P"))
        // Otro prefijo no se toca: una Pivot no reemplaza a la puerta P2.
        assertNull(EdicionProducto.numeroPara("PV2", "P"))
        assertNull(EdicionProducto.numeroPara("Rm4", "Rj"))
    }

    @Test
    fun las_medidas_salen_del_paquete_o_de_las_referencias() {
        val nova = listOf("DisenoSimbolicoV2" to listOf("C<Jorge>-M<214.8,241.9,136,null,null,1>-P<V,n,i,c,1>" to ""))
        assertEquals(214.8f to 241.9f, ReaperturaProducto.medidasDe(nova))
        val mampara = listOf("DisenoMampara" to listOf("MPF1:213.8;276.5;210.0;4" to ""))
        assertEquals(213.8f to 276.5f, ReaperturaProducto.medidasDe(mampara))
        for ((texto, esperado) in listOf(
            "anch 32 x alt 270\nColumnas: 1" to (32f to 270f),
            "An: 204.2  x  Al: 51.7\nAltura de puente: sin puente" to (204.2f to 51.7f),
            "Ancho 213.8   ·   Alto 276.5" to (213.8f to 276.5f)
        )) assertEquals(texto, esperado, ReaperturaProducto.medidasDe(listOf("Referencias" to listOf(texto to ""))))
    }
}
