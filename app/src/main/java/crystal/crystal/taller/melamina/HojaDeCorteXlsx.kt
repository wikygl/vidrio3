package crystal.crystal.taller.melamina

import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * La hoja de corte como un Excel de verdad (.xlsx), para abrirla en Excel, Hojas de cálculo de
 * Google o WPS sin que se junte todo en una celda (el Excel en español separa con punto y coma) ni
 * se rompan la ñ y las tildes. El CSV por comas queda para las máquinas.
 *
 * Tiene la misma forma que la hoja del taller: un bloque por melamina con su título en negrita a lo
 * ancho, el encabezado, una fila por pieza (cantidad, ancho, alto y vetas como números) y la
 * cantidad total con su SUMA; un renglón en blanco entre bloques.
 *
 * Un .xlsx es un zip con unas pocas hojas XML: se escribe a mano, sin librerías, con los textos en
 * la misma celda (inlineStr) para no llevar la tabla de textos compartidos.
 */
object HojaDeCorteXlsx {

    /** Los estilos del libro: 0 normal, 1 negrita (encabezado y total), 2 título de bloque. */
    private const val NORMAL = 0
    private const val NEGRITA = 1
    private const val TITULO = 2

    fun generar(muebles: List<RoperoProduccion.Mueble>, nombreHoja: String = "Corte"): ByteArray {
        val filas = StringBuilder()
        val combinadas = mutableListOf<String>()
        val ultimaColumna = letra(RoperoProduccion.COLUMNAS.lastIndex)
        var r = 0
        fun fila(celdas: String) { r++; filas.append("<row r=\"$r\">").append(celdas).append("</row>") }

        RoperoProduccion.bloquesDeCorte(muebles).forEach { (material, fichas) ->
            // El título del bloque, a lo ancho de todas las columnas.
            fila(texto(0, r + 1, material, TITULO))
            combinadas.add("A$r:$ultimaColumna$r")
            fila(RoperoProduccion.COLUMNAS.mapIndexed { i, t -> texto(i, r + 1, t, NEGRITA) }.joinToString(""))
            val primera = r + 1
            fichas.forEach { f ->
                val c = listOf(
                    numero(0, r + 1, f.pieza.cantidad.toString()),
                    numero(1, r + 1, RoperoProduccion.fmt(f.anchoCm)),
                    numero(2, r + 1, RoperoProduccion.fmt(f.altoCm)),
                    if (f.vetas.isNotBlank()) numero(3, r + 1, f.vetas) else "",
                    texto(4, r + 1, f.canto), texto(5, r + 1, f.ranura), texto(6, r + 1, f.router),
                    texto(7, r + 1, f.etiqueta), texto(8, r + 1, f.pieza.nombre), texto(9, r + 1, f.codigo)
                )
                fila(c.joinToString(""))
            }
            val ultima = r
            // El total, como en la hoja del taller: la SUMA de las cantidades (con su valor ya puesto).
            val total = fichas.sumOf { it.pieza.cantidad }
            fila("<c r=\"A${r + 1}\" s=\"$NEGRITA\"><f>SUM(A$primera:A$ultima)</f><v>$total</v></c>")
            r++   // el renglón en blanco entre bloques
        }

        val hoja = buildString {
            append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
            append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">""")
            // Anchos a ojo de las columnas: números angostos, canto y etiqueta anchos, el código ancho.
            append("<cols>")
            listOf(9, 8, 8, 8, 24, 22, 10, 34, 24, 44).forEachIndexed { i, w -> append("<col min=\"${i + 1}\" max=\"${i + 1}\" width=\"$w\" customWidth=\"1\"/>") }
            append("</cols>")
            append("<sheetData>").append(filas).append("</sheetData>")
            if (combinadas.isNotEmpty()) {
                append("<mergeCells count=\"${combinadas.size}\">")
                combinadas.forEach { append("<mergeCell ref=\"$it\"/>") }
                append("</mergeCells>")
            }
            append("</worksheet>")
        }

        val salida = ByteArrayOutputStream()
        ZipOutputStream(salida).use { zip ->
            fun archivo(nombre: String, contenido: String) {
                zip.putNextEntry(ZipEntry(nombre)); zip.write(contenido.toByteArray(Charsets.UTF_8)); zip.closeEntry()
            }
            archivo("[Content_Types].xml", """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
<Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
<Default Extension="xml" ContentType="application/xml"/>
<Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
<Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
<Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
</Types>""")
            archivo("_rels/.rels", """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>""")
            archivo("xl/workbook.xml", """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
<sheets><sheet name="${xml(nombreHoja.take(31))}" sheetId="1" r:id="rId1"/></sheets>
</workbook>""")
            archivo("xl/_rels/workbook.xml.rels", """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
<Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>""")
            archivo("xl/styles.xml", """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
<fonts count="3"><font><sz val="11"/><name val="Calibri"/></font><font><b/><sz val="11"/><name val="Calibri"/></font><font><b/><sz val="13"/><name val="Calibri"/></font></fonts>
<fills count="2"><fill><patternFill patternType="none"/></fill><fill><patternFill patternType="gray125"/></fill></fills>
<borders count="1"><border><left/><right/><top/><bottom/><diagonal/></border></borders>
<cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>
<cellXfs count="3"><xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/><xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1"/><xf numFmtId="0" fontId="2" fillId="0" borderId="0" xfId="0" applyFont="1"/></cellXfs>
</styleSheet>""")
            archivo("xl/worksheets/sheet1.xml", hoja)
        }
        return salida.toByteArray()
    }

    /** La letra de la columna [i] (desde 0): A, B… (hasta la Z alcanza). */
    private fun letra(i: Int): String = ('A' + i).toString()

    private fun texto(col: Int, fila: Int, t: String, estilo: Int = NORMAL): String =
        if (t.isEmpty()) "" else "<c r=\"${letra(col)}$fila\" t=\"inlineStr\"${if (estilo != NORMAL) " s=\"$estilo\"" else ""}><is><t>${xml(t)}</t></is></c>"

    private fun numero(col: Int, fila: Int, v: String): String = "<c r=\"${letra(col)}$fila\"><v>$v</v></c>"

    private fun xml(t: String): String =
        t.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
}
