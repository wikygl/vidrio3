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

    // ───────────── Fases 2 y 3: los esperados salen de correr comun.js con los mismos datos ─────────────

    @Test
    fun las_zonas_de_un_servicio_entran_en_las_palabras_igual_que_en_el_servidor() {
        assertEquals(
            listOf("instalacion", "mamparas", "mampara", "servicios", "servicio", "huancayo", "tambo", "chilca"),
            TiendaReglas.palabrasDe("Instalación de mamparas", "Instalación y servicios", "Huancayo", "servicio", listOf("El Tambo", "Chilca"))
        )
        val a = anuncio("Instalación de mamparas", "Instalación y servicios", "Huancayo").copy(tipo = "servicio", zonas = listOf("Chilca"))
        assertTrue("quien atiende Chilca sale buscando Chilca", TiendaReglas.casa(a, "mampara chilca"))
    }

    @Test
    fun las_zonas_se_limpian_como_en_el_servidor() {
        assertEquals(listOf("El Tambo", "chilca", "Pilcomayo"), TiendaReglas.zonas("El Tambo,  chilca , Chilca, huancayo, Pilcomayo", "Huancayo"))
        assertEquals(emptyList<String>(), TiendaReglas.zonas("  ", "Lima"))
        assertNull("más de 5", TiendaReglas.zonas("a1, b2, c3, d4, e5, f6", "Lima"))
        assertNull("una muy larga", TiendaReglas.zonas("x".repeat(41), "Lima"))
    }

    @Test
    fun la_clave_de_ciudad_y_el_tema_de_pedidos_son_los_del_servidor() {
        assertEquals("cerro_de_pasco", TiendaReglas.claveCiudad("Cerro de Pasco"))
        assertEquals("nana", TiendaReglas.claveCiudad("Ñaña"))
        assertEquals("ancash_peru", TiendaReglas.claveCiudad("  Áncash, Perú! "))
        assertEquals("pedidos_san_juan_de_lurigancho", TiendaReglas.temaPedidos("San Juan de Lurigancho"))
        assertEquals("", TiendaReglas.temaPedidos(" "))
    }

    @Test
    fun las_tarifas_tienen_el_mismo_valor_por_defecto_y_tope_que_el_servidor() {
        assertEquals(TiendaReglas.Tarifas(), TiendaReglas.tarifas(null))
        val t = TiendaReglas.tarifas(
            mapOf("precio_cent" to 400L, "servicio_precio_cent" to 600L, "destacar_precio_cent" to 999999L,
                "proveedor_dias" to 15L, "pedido_max_talleres" to 5L)
        )
        assertEquals(400L to 30, t.publicar("producto"))
        assertEquals(600L to 30, t.publicar("servicio"))
        assertEquals("un tipeo no cobra de más", 200L, t.destacarCent)
        assertEquals(1000L, t.proveedorCent)
        assertEquals(15, t.proveedorDias)
        assertEquals(5, t.pedidoMaxTalleres)
        assertEquals("sin tarifa propia el servicio paga como producto", 400L, TiendaReglas.tarifas(mapOf("precio_cent" to 400L)).servicioCent)
    }

    @Test
    fun un_servicio_solo_se_cobra_por_trabajo_tiempo_o_medida() {
        fun error(unidad: String) = TiendaReglas.error("servicio", "Instalación y servicios", "Instalación de mamparas", "", 5000, unidad, "", "Huancayo", 0)
        assertNull(error("hora"))
        assertEquals("Un servicio se cobra por servicio, por hora, por día o por medida.", error("plancha"))
    }

    @Test
    fun lo_enviado_por_el_chat_dice_que_rubro_buscar() {
        assertEquals(listOf("Aluminio y perfiles", "Accesorios y herrajes"), TiendaReglas.rubrosDeLoEnviado("application/x-crystal-corte", "corte_1.crystal", false))
        assertEquals(listOf("Vidrio y espejos", "Melamina y madera"), TiendaReglas.rubrosDeLoEnviado(null, "plancha_obra.crystal", false))
        assertEquals(listOf("Vidrio y espejos"), TiendaReglas.rubrosDeLoEnviado(null, null, true))
        val p = { uid: String, rubros: List<String> -> Proveedor(uid, uid, "Lima", emptyList(), rubros, "", "", "activo", 0) }
        assertEquals(listOf("b", "a"), Proveedor.ordenarPara(listOf(p("a", listOf("Otros")), p("b", listOf("Vidrio y espejos"))), listOf("Vidrio y espejos")).map { it.uid })
    }

    @Test
    fun un_pedido_se_toma_si_esta_abierto_cabe_y_es_para_uno() {
        val base = PedidoCliente("p", "Ventana", "Huancayo", "", "", "Rosa", "abierto", 0, 10_000, 1, 3, listOf("t1"), "", "")
        assertTrue(base.disponiblePara("t2", 5_000))
        assertFalse("ya venció", base.disponiblePara("t2", 20_000))
        assertFalse("ya no cabe", base.copy(tomas = 3).disponiblePara("t2", 5_000))
        assertFalse("es para otro vendedor", base.copy(paraUid = "v").disponiblePara("t2", 5_000))
        assertTrue(base.copy(paraUid = "v").disponiblePara("v", 5_000))
        assertEquals("https://wa.me/51987654321", TomaPedido("Rosa", "51987654321").whatsapp)
    }

    private fun anuncio(titulo: String, categoria: String, ciudad: String) = Anuncio(
        id = "x", uid = "u", vendedorNombre = "", tipo = "producto", categoria = categoria, titulo = titulo,
        descripcion = "", precioCent = 0, unidad = "c/u", condicion = "nuevo", ciudad = ciudad,
        fotos = emptyList(), miniaturas = emptyList(), estado = "activo", publicadoEnMs = 0, venceEnMs = 0, vistas = 0
    )
}
