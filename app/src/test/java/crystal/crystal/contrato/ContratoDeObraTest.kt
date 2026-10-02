package crystal.crystal.contrato

import crystal.crystal.Listado
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

/**
 * El contrato de obra: el monto en letras, el detalle de los trabajos y lo que dicen las cláusulas.
 */
class ContratoDeObraTest {

    @Test
    fun el_monto_en_letras() {
        assertEquals("TRES MIL QUINIENTOS CON 00/100 SOLES", MontoEnLetras.soles(3500.0))
        assertEquals("MIL SETECIENTOS CINCUENTA CON 50/100 SOLES", MontoEnLetras.soles(1750.5))
        assertEquals("CIEN CON 00/100 SOLES", MontoEnLetras.soles(100.0))
        assertEquals("CIENTO UNO CON 00/100 SOLES", MontoEnLetras.soles(101.0))
        assertEquals("CERO CON 99/100 SOLES", MontoEnLetras.soles(0.99))
        assertEquals("veintiún mil quinientos veintiuno", MontoEnLetras.letras(21521))
        assertEquals("treinta y un mil", MontoEnLetras.letras(31000))
        assertEquals("un millón doscientos mil", MontoEnLetras.letras(1_200_000))
        assertEquals("dos millones", MontoEnLetras.letras(2_000_000))
        assertEquals("dieciséis", MontoEnLetras.letras(16))
        assertEquals("doce", MontoEnLetras.letras(12))
        // Los céntimos se redondean, no se cortan: 0.1 + 0.2 no deja 29 céntimos.
        assertEquals("CERO CON 30/100 SOLES", MontoEnLetras.soles(0.1 + 0.2))
    }

    private fun item(producto: String, costo: Float, ambiente: String? = null, escala: String = "m2") =
        Listado(
            escala = escala, uni = "Centímetros", medi1 = 120f, medi2 = 150f, medi3 = 0f,
            canti = 1f, piescua = 0f, precio = 0f, costo = costo, producto = producto,
            peri = 0f, metcua = 0f, metli = 0f, metcub = 0f, color = 0, uri = "",
            ambiente = ambiente
        )

    private fun contrato(
        lista: List<Listado> = listOf(item("Ventana corrediza", 1500f), item("Puerta", 2000f)),
        cliente: ParteDelContrato = ParteDelContrato("Clínica Bilbao", "20123456789", "Av. Real 100"),
        adelanto: Double = 1750.0,
        fechaSaldo: String = "",
        condiciones: CondicionesContrato = CondicionesContrato()
    ) = DatosDelContrato(
        fecha = Calendar.getInstance().apply { set(2026, Calendar.OCTOBER, 1) }.time,
        folio = "CRY-1",
        cliente = cliente,
        contratista = ParteDelContrato("Juan Pérez", "41888817", "Jr. San Pablo 200", "928878578"),
        direccionObra = "",
        lista = lista,
        plazoDias = 15,
        adelanto = adelanto,
        fechaSaldo = fechaSaldo,
        observaciones = "",
        condiciones = condiciones
    )

    /**
     * Cada ítem va en su fila con su medida, aunque dos se llamen igual (y aunque uno traiga un
     * espacio de más): juntarlos por nombre perdía que eran dos medidas distintas.
     */
    @Test
    fun el_detalle_va_item_por_item() {
        val filas = ClausulasContrato.detalle(
            listOf(item("División melamina", 900f), item("División melamina ", 900f))
        )
        assertEquals(2, filas.size)
        assertEquals("División melamina", filas[1].descripcion)
        assertEquals("120 × 150 cm", filas[0].medidas)
        assertEquals(2, filas[1].numero)
    }

    /** Por ambientes, en el orden de la proforma: el número del ítem es el mismo en los dos papeles. */
    @Test
    fun el_detalle_sigue_los_ambientes() {
        val filas = ClausulasContrato.detalle(
            listOf(item("A", 1f, "Sala"), item("B", 1f, "Cocina"), item("C", 1f, "Sala"))
        )
        assertEquals(listOf("Sala", "Sala", "Cocina"), filas.map { it.ambiente })
        assertEquals(listOf("A", "C", "B"), filas.map { it.descripcion })
    }

    @Test
    fun el_total_es_la_suma_y_el_saldo_lo_que_falta() {
        val d = contrato()
        assertEquals(3500.0, d.total, 0.001)
        assertEquals(1750.0, d.saldo, 0.001)
        val pago = ClausulasContrato.clausulas(d).first { it.titulo == "FORMA DE PAGO" }.texto
        assertTrue(pago.contains("S/ 1,750.00 (MIL SETECIENTOS CINCUENTA CON 00/100 SOLES)"))
        assertTrue(pago.contains("a la entrega e instalación"))
        val precio = ClausulasContrato.clausulas(d).first { it.titulo == "PRECIO" }.texto
        assertTrue(precio.contains("S/ 3,500.00 (TRES MIL QUINIENTOS CON 00/100 SOLES)"))
    }

    @Test
    fun el_saldo_con_fecha_y_el_pago_completo() {
        val conFecha = ClausulasContrato.clausulas(contrato(fechaSaldo = "15/10/2026"))
            .first { it.titulo == "FORMA DE PAGO" }.texto
        assertTrue(conFecha.contains("a más tardar el 15/10/2026"))
        val todo = ClausulasContrato.clausulas(contrato(adelanto = 3500.0))
            .first { it.titulo == "FORMA DE PAGO" }.texto
        assertTrue(todo.contains("íntegro del precio a la firma"))
    }

    /** Una empresa firma por medio de su representante; una persona, ella misma. */
    @Test
    fun la_empresa_firma_por_su_representante() {
        val empresa = ParteDelContrato("Clínica Bilbao S.A.C.", "20123456789", "Av. Real 100", "", "Ana Ruiz", "40000000")
        assertTrue(empresa.esEmpresa)
        assertEquals("RUC N° 20123456789", empresa.identificacion())
        assertTrue(ClausulasContrato.encabezado(contrato(cliente = empresa)).contains("representada por Ana Ruiz"))
        assertEquals("Ana Ruiz p. Clínica Bilbao S.A.C.", ClausulasContrato.firmante(empresa))

        val persona = ParteDelContrato("Luis", "10412345678")
        assertFalse("el RUC 10 es de una persona", persona.esEmpresa)
        assertEquals("DNI N° 41888817", ParteDelContrato("x", "41888817").identificacion())
        assertEquals("RUC N° 20123456789", ParteDelContrato("x", "RUC 20123456789").identificacion())
    }

    @Test
    fun lo_que_falta_se_dice() {
        val faltan = ClausulasContrato.faltantes(
            contrato(cliente = ParteDelContrato("Clínica Bilbao", "20123456789", ""))
        )
        assertTrue(faltan.contains("Representante del cliente"))
        assertTrue(faltan.contains("Dirección de la obra"))
        assertTrue(ClausulasContrato.faltantes(
            contrato(cliente = ParteDelContrato("Luis", "41888817", "Jr. Lima 1"))
        ).isEmpty())
    }

    /** Las cláusulas van numeradas sin hueco aunque falte una; sin garantía no sale la de garantía. */
    @Test
    fun las_clausulas_se_numeran_seguidas() {
        val sin = ClausulasContrato.clausulas(contrato(condiciones = CondicionesContrato(garantiaMeses = 0)))
        assertFalse(sin.any { it.titulo == "GARANTÍA" })
        assertEquals("PRIMERA", sin.first().ordinal)
        assertTrue(sin.first().llevaDetalle)
        assertEquals("JURISDICCIÓN", sin.last().titulo)
        val con = ClausulasContrato.clausulas(contrato())
        assertTrue(con.first { it.titulo == "GARANTÍA" }.texto.contains("doce (12) meses"))
    }

    @Test
    fun el_igv_y_la_ciudad() {
        val d = contrato(condiciones = CondicionesContrato(igv = Igv.INCLUIDO, ciudad = "Huancayo"))
        assertTrue(ClausulasContrato.clausulas(d).first { it.titulo == "PRECIO" }.texto.contains("incluye el IGV"))
        assertTrue(ClausulasContrato.cierre(d).contains("en Huancayo, el 1 de octubre de 2026"))
        assertTrue(ClausulasContrato.clausulas(d).last().texto.contains("de Huancayo"))
    }
}
