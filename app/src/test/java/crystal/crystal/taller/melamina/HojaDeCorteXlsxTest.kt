package crystal.crystal.taller.melamina

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

/**
 * La hoja de corte en .xlsx: tiene que ser un libro válido (sus partes), con la ñ y las tildes tal
 * cual, los números como números, el título de cada melamina a lo ancho y el total con su SUMA.
 */
class HojaDeCorteXlsxTest {

    private val ropero = Ropero(anchoCm = 155f, altoCm = 230f, fondoCm = 60f, zocaloCm = 7f, colorExterior = "Cedro")
        .conCuerposIguales(2)
        .let { it.conCuerpo(1, Cuerpo(tipo = TipoCuerpo.CAJONES, cajones = 4, altosCajonesCm = List(4) { 18f }, altoCajonesFijoCm = 80f, cajonesALaVista = true)) }

    private fun partes(bytes: ByteArray): Map<String, String> {
        val salen = mutableMapOf<String, String>()
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var e = zip.nextEntry
            while (e != null) { salen[e.name] = zip.readBytes().toString(Charsets.UTF_8); e = zip.nextEntry }
        }
        return salen
    }

    @Test
    fun el_libro_tiene_sus_partes_y_la_hoja_como_la_del_taller() {
        val bytes = HojaDeCorteXlsx.generar(listOf(RoperoProduccion.Mueble("Rm4", ropero)), "melas")
        val p = partes(bytes)
        listOf("[Content_Types].xml", "_rels/.rels", "xl/workbook.xml", "xl/_rels/workbook.xml.rels", "xl/styles.xml", "xl/worksheets/sheet1.xml")
            .forEach { assertTrue("falta $it", it in p) }
        val hoja = p.getValue("xl/worksheets/sheet1.xml")
        // La ñ y las tildes, tal cual (UTF-8): "cajonería", "Zócalo".
        assertTrue(hoja.contains("cajonería"))
        assertTrue(hoja.contains("Zócalo"))
        // El primer bloque: su título a lo ancho (A1:J1) y el encabezado en la fila 2.
        assertTrue(hoja.contains("<mergeCell ref=\"A1:J1\"/>"))
        assertTrue(hoja.contains("<c r=\"A1\" t=\"inlineStr\" s=\"2\"><is><t>melamina Blanco 18mm</t></is></c>"))
        assertTrue(hoja.contains("<c r=\"A2\" t=\"inlineStr\" s=\"1\"><is><t>cantidad</t></is></c>"))
        // Los números como números, y el total con su SUMA.
        assertTrue(hoja.contains("<c r=\"B3\"><v>"))
        assertTrue(Regex("<f>SUM\\(A3:A\\d+\\)</f>").containsMatchIn(hoja))
        assertTrue(p.getValue("xl/workbook.xml").contains("name=\"melas\""))
        // Para mirarlo en Excel, Hojas de cálculo o WPS.
        java.io.File("build/hoja_de_corte_prueba.xlsx").writeBytes(bytes)
    }

    @Test
    fun el_canto_como_lo_pide_el_proveedor() {
        // Los tres casos de la captura del usuario (2026-10-12), y los demás que dictó.
        val r = Ropero()
        fun canto(ancho: Int, alto: Int, enAncho: Int, enAlto: Int, tipo: TipoCanto = TipoCanto.FINO) =
            RoperoProduccion.cantoProveedor(r, PiezaMelamina("x", ancho, alto, 1, MaterialPlancha.MELAMINA_18, cantosEnAncho = enAncho, cantosEnAlto = enAlto, canto = tipo))
        // 930 x 100 con canto en los dos de 100 (los cortos): primero van los cortos.
        assertEquals("D;1|D;1||" to "II CORT", canto(930, 100, 0, 2))
        assertEquals("D;1|D;1||" to "II CORT", canto(100, 930, 2, 0))   // da igual cómo venga: se comparan los lados
        assertEquals("||D;1|D;1" to "II", canto(732, 100, 2, 0))
        assertEquals("3 MM;3|3 MM;3|3 MM;3|3 MM;3" to "O 3MM", canto(681, 475, 2, 2, TipoCanto.GRUESO))
        assertEquals("||D;1|" to "I", canto(549, 200, 1, 0))
        assertEquals("D;1|||" to "I CORT", canto(549, 200, 0, 1))
        assertEquals("D;1|D;1|D;1|" to "C", canto(600, 300, 1, 2))
        assertEquals("D;1||D;1|D;1" to "U", canto(600, 300, 2, 1))
        assertEquals("D;1||D;1|" to "L", canto(600, 300, 1, 1))
        assertEquals("" to "", canto(600, 300, 0, 0))
        // La fila: el lado largo primero, en mm; la ranura del cajón en OBS; sin veta, (T_N).
        val f = RoperoProduccion.filaProveedor(r, PiezaMelamina("Lateral cajón", 549, 200, 2, MaterialPlancha.MELAMINA_18, cantosEnAncho = 1, ranuraCm = 1.8f))
        assertEquals(RoperoProduccion.FilaProveedor("R 1.8", 549, 200, 2, "(T_N)", "||D;1|", "I"), f)
    }

    @Test
    fun la_hoja_del_proveedor_va_una_por_melamina() {
        val p = partes(HojaDeCorteXlsx.generarProveedor(listOf(RoperoProduccion.Mueble("Rm4", ropero))))
        val libro = p.getValue("xl/workbook.xml")
        assertTrue(libro.contains("name=\"melamina Blanco 18mm\""))
        assertTrue(libro.contains("name=\"melamina Cedro 18mm\""))
        val hoja = p.getValue("xl/worksheets/sheet1.xml")
        assertTrue(hoja.contains("<c r=\"A1\" t=\"inlineStr\" s=\"1\"><is><t>OBS</t></is></c>"))
        assertTrue(hoja.contains("<t>Ajuste de canto</t>"))
        assertTrue(hoja.contains("<c r=\"B2\"><v>"))
        // Cada hoja con su parte y su relación.
        val hojas = Regex("<sheet ").findAll(libro).count()
        (1..hojas).forEach { assertTrue("falta la hoja $it", "xl/worksheets/sheet$it.xml" in p) }
        java.io.File("build/hoja_proveedor_prueba.xlsx").writeBytes(HojaDeCorteXlsx.generarProveedor(listOf(RoperoProduccion.Mueble("Rm4", ropero))))
    }

    @Test
    fun los_textos_con_signos_de_xml_no_rompen_el_libro() {
        val conSignos = ropero.copy(colorExterior = "Roble & <Nogal>")
        val hoja = partes(HojaDeCorteXlsx.generar(listOf(RoperoProduccion.Mueble("Rm4", conSignos)))).getValue("xl/worksheets/sheet1.xml")
        assertTrue(hoja.contains("Roble &amp; &lt;Nogal&gt;"))
        assertEquals(false, hoja.contains("<Nogal>"))
    }
}
