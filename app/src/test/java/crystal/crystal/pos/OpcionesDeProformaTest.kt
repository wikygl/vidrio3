package crystal.crystal.pos

import crystal.crystal.Listado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Las opciones de un ítem: el mismo producto medido, ofrecido con otro material.
 *
 * Lo que se comprueba aquí es lo que va a salir impreso en la proforma de elección: la cuenta del
 * precio de cada opción y que las opciones sobrevivan a guardarse y volver a leerse.
 */
class OpcionesDeProformaTest {

    /** Una ventana de 1.20 x 1.50, dos unidades, medida en metros cuadrados a 100 el m². */
    private fun ventana(cantidad: Float = 2f) = Listado(
        escala = "m2",
        uni = "Metros",
        medi1 = 1.20f,
        medi2 = 1.50f,
        medi3 = 0f,
        canti = cantidad,
        piescua = 1.20f * 1.50f * 11.1f * cantidad,
        precio = 100f,
        costo = 1.20f * 1.50f * 100f * cantidad,
        producto = "Ventana vidrio 6mm",
        peri = 5.4f,
        metcua = 1.20f * 1.50f * cantidad,
        metli = 0f,
        metcub = 0f,
        color = 0,
        uri = ""
    )

    @Test
    fun las_opciones_van_y_vuelven_del_texto() {
        val item = ventana()
        OpcionesDeProforma.guardar(
            item,
            listOf(
                OpcionDeProforma("Arenado laminado", 180f),
                OpcionDeProforma("Policarbonato", 95.5f)
            )
        )
        val leidas = OpcionesDeProforma.de(item)
        assertEquals(2, leidas.size)
        assertEquals("Arenado laminado", leidas[0].producto)
        assertEquals(180f, leidas[0].precio, 0.01f)
        assertEquals(95.5f, leidas[1].precio, 0.01f)
    }

    /** Un ítem sin opciones se guarda como se guardaba antes: sin nada. */
    @Test
    fun sin_opciones_no_se_escribe_nada() {
        val item = ventana()
        OpcionesDeProforma.guardar(item, emptyList())
        assertNull(item.opciones)
        assertTrue(OpcionesDeProforma.de(item).isEmpty())
        assertTrue(OpcionesDeProforma.desdeTexto(null).isEmpty())
        assertTrue(OpcionesDeProforma.desdeTexto("cualquier cosa").isEmpty())
    }

    /** La imagen de cada opción viaja con ella, y las de antes —sin imagen— se leen igual. */
    @Test
    fun la_imagen_va_con_su_opcion() {
        val item = ventana()
        OpcionesDeProforma.guardar(
            item,
            listOf(
                OpcionDeProforma("Arenado laminado", 180f, "/fotos/arenado.jpg"),
                OpcionDeProforma("Policarbonato", 95f)
            )
        )
        val leidas = OpcionesDeProforma.de(item)
        assertEquals("/fotos/arenado.jpg", leidas[0].imagen)
        assertEquals("la que no tiene imagen se inventó una", "", leidas[1].imagen)

        // Y una opción escrita antes de que hubiera imágenes se sigue leyendo.
        assertEquals(
            1,
            OpcionesDeProforma.desdeTexto("Vidrio templado|150").size
        )
        assertEquals("", OpcionesDeProforma.desdeTexto("Vidrio templado|150")[0].imagen)
    }

    /** Los separadores no pueden colarse en el nombre: al leerlo partiría por donde no es. */
    @Test
    fun el_nombre_no_parte_el_texto() {
        val item = ventana()
        OpcionesDeProforma.guardar(item, listOf(OpcionDeProforma("Vidrio 6|8; laminado", 120f)))
        val leidas = OpcionesDeProforma.de(item)
        assertEquals("no se quedó en una sola opción", 1, leidas.size)
        assertEquals(120f, leidas[0].precio, 0.01f)
    }

    /**
     * El precio de una opción sale con la MISMA cuenta que el costo de siempre: por la escala.
     *
     * 1.20 x 1.50 son 1.8 m², así que a 180 el m² una pieza cuesta 324, y las dos, 648. Calculado
     * de otra manera, la proforma diría un precio y el presupuesto otro.
     */
    @Test
    fun el_precio_sale_por_la_escala_del_item() {
        val item = ventana(cantidad = 2f)
        val opcion = OpcionDeProforma("Arenado laminado", 180f)
        assertEquals(324f, OpcionesDeProforma.precioUnitario(item, opcion), 0.5f)
        assertEquals(648f, OpcionesDeProforma.precioPorLaCantidad(item, opcion), 0.5f)
    }

    /** Y la del propio ítem es una opción más: su producto y su precio, con la misma cuenta. */
    @Test
    fun el_item_es_una_opcion_mas() {
        val item = ventana(cantidad = 2f)
        val propia = OpcionesDeProforma.comoEstaApuntado(item)
        assertEquals("Ventana vidrio 6mm", propia.producto)
        assertEquals(180f, OpcionesDeProforma.precioUnitario(item, propia), 0.5f)
        // Lo que ya costaba la línea entera: la cuenta no cambia por mirarla como opción.
        assertEquals(item.costo, OpcionesDeProforma.precioPorLaCantidad(item, propia), 0.5f)
    }

    /** En "uni" el precio ya es el de la unidad: no se multiplica por ninguna medida. */
    @Test
    fun por_unidad_el_precio_es_el_precio() {
        val item = ventana().apply { escala = "uni"; canti = 3f }
        val opcion = OpcionDeProforma("Policarbonato", 250f)
        assertEquals(250f, OpcionesDeProforma.precioUnitario(item, opcion), 0.01f)
        assertEquals(750f, OpcionesDeProforma.precioPorLaCantidad(item, opcion), 0.01f)
    }

    /** Y saber si la lista lleva opciones es lo que decide qué proforma cabe. */
    @Test
    fun se_sabe_si_la_lista_tiene_opciones() {
        assertTrue(!OpcionesDeProforma.hayEnLaLista(listOf(ventana(), ventana())))
        val conOpciones = listOf(ventana(), ventana().also {
            OpcionesDeProforma.guardar(it, listOf(OpcionDeProforma("Serie 80", 210f)))
        })
        assertTrue(OpcionesDeProforma.hayEnLaLista(conOpciones))
    }
}
