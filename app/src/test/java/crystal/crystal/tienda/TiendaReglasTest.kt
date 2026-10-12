package crystal.crystal.tienda

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Lo que la app tiene que decir IGUAL que el servidor (`functions/tienda/comun.js`): las palabras de
 * búsqueda, el precio vigente y la validación. Si se separan, el servidor rechaza anuncios que la app
 * dio por buenos, o una búsqueda deja de encontrar lo que hay.
 */
class TiendaReglasTest {

    /** Los esperados salen de correr `palabrasDe` del servidor con los mismos datos. */
    @Test
    fun las_palabras_son_las_mismas_que_arma_el_servidor() {
        assertEquals(
            listOf("ventanas", "ventana", "corredizas", "corrediza", "aluminio", "puertas", "puerta", "hechas", "hecha", "huancayo", "producto"),
            TiendaReglas.palabrasDe("Ventanas corredizas de aluminio", "Puertas y ventanas hechas", "Huancayo", "producto")
        )
        assertEquals(
            listOf("instalacion", "mamparas", "mampara", "banos", "bano", "servicios", "servicio", "tambo"),
            TiendaReglas.palabrasDe("Instalación de mamparas en baños", "Instalación y servicios", "El Tambo", "servicio")
        )
        assertEquals(
            listOf("vidrio", "templado", "8mm", "espejos", "espejo", "biselados", "biselado", "lima", "producto"),
            TiendaReglas.palabrasDe("Vidrio Templado 8mm, ESPEJOS biselados!", "Vidrio y espejos", "Lima", "producto")
        )
    }

    /** Se busca con la palabra más larga en singular; "ventanas" encuentra "ventana" y al revés. */
    @Test
    fun la_busqueda_casa_en_singular_y_en_plural() {
        assertEquals("corrediza", TiendaReglas.palabraDeBusqueda("ventanas corredizas"))
        assertEquals("red", TiendaReglas.palabraDeBusqueda("redes"))
        assertNull(TiendaReglas.palabraDeBusqueda("de la"))
        val a = anuncio("Ventana corrediza de aluminio", "Puertas y ventanas hechas", "Huancayo")
        assertTrue(TiendaReglas.casa(a, "ventanas"))
        assertTrue(TiendaReglas.casa(a, "Ventana  ALUMINIO"))
        assertTrue(TiendaReglas.casa(a, "huancayo"))
        assertFalse(TiendaReglas.casa(a, "ventana melamina"))
    }

    @Test
    fun el_precio_del_panel_se_respeta_solo_si_es_razonable() {
        assertEquals(300L to 30, TiendaReglas.precioVigente(null, null))
        assertEquals(500L to 15, TiendaReglas.precioVigente(500, 15))
        assertEquals(0L to 30, TiendaReglas.precioVigente(0, 30))
        assertEquals(300L to 30, TiendaReglas.precioVigente(300000, 0))
    }

    @Test
    fun la_validacion_dice_lo_mismo_que_el_servidor() {
        fun error(titulo: String = "Vidrio templado 8 mm", precio: Long? = 15000, condicion: String = "nuevo", tipo: String = "producto") =
            TiendaReglas.error(tipo, "Vidrio y espejos", titulo, "", precio, "m²", condicion, "Huancayo", 1)
        assertNull(error())
        assertEquals("El título es muy corto.", error(titulo = "ab"))
        assertEquals("El precio no es válido.", error(precio = -1))
        assertEquals("Indique si es nuevo o usado.", error(condicion = ""))
        assertNull("un servicio no lleva nuevo/usado", error(condicion = "", tipo = "servicio"))
    }

    @Test
    fun el_precio_escrito_pasa_a_centimos() {
        assertEquals(15000L, TiendaRepositorio.aCentimos("150"))
        assertEquals(15050L, TiendaRepositorio.aCentimos("150.5"))
        assertEquals(15050L, TiendaRepositorio.aCentimos("150,50"))
        assertEquals(150000L, TiendaRepositorio.aCentimos("1,500.00"))
        assertEquals(1L, TiendaRepositorio.aCentimos("0.005"))
        assertNull(TiendaRepositorio.aCentimos("abc"))
        assertNull(TiendaRepositorio.aCentimos(""))
    }

    @Test
    fun el_precio_se_lee_en_soles_o_a_tratar() {
        assertEquals("S/ 1,500.00", TiendaReglas.precio(150000))
        assertEquals("A tratar", TiendaReglas.precio(0))
    }

    private fun anuncio(titulo: String, categoria: String, ciudad: String) = Anuncio(
        id = "x", uid = "u", vendedorNombre = "", tipo = "producto", categoria = categoria, titulo = titulo,
        descripcion = "", precioCent = 0, unidad = "c/u", condicion = "nuevo", ciudad = ciudad,
        fotos = emptyList(), miniaturas = emptyList(), estado = "activo", publicadoEnMs = 0, venceEnMs = 0, vistas = 0
    )
}
