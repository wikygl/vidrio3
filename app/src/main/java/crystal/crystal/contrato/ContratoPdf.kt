package crystal.crystal.contrato

import android.content.Context
import android.graphics.Bitmap
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.events.Event
import com.itextpdf.kernel.events.IEventHandler
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Div
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.element.Text
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import crystal.crystal.pdf.PdfGenerator
import crystal.crystal.pos.MembreteDeProforma
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * El contrato y el recibo de adelanto en PDF.
 *
 * Llevan el mismo membrete y el mismo sello de agua que la proforma —los pone [PdfGenerator], que
 * es quien sabe hacerlo—, el folio en el pie de cada hoja y las firmas lado a lado, cada una sobre
 * su línea con el nombre y el documento de quien firma.
 */
class ContratoPdf(
    private val contexto: Context,
    private val membrete: MembreteDeProforma?
) {

    /** El contrato de fabricación e instalación. */
    fun contrato(
        archivo: File,
        d: DatosDelContrato,
        firmaCliente: Bitmap?,
        firmaContratista: Bitmap?
    ) = escribir(archivo, d.folio) { doc, negrita ->
        titulo(doc, negrita, ClausulasContrato.TITULO, d)
        doc.add(parrafo(ClausulasContrato.encabezado(d)))
        ClausulasContrato.clausulas(d).forEach { c ->
            doc.add(
                parrafo("")
                    .add(Text("${c.ordinal}. — ${c.titulo}. ").setFont(negrita))
                    .add(Text(c.texto))
            )
            if (c.llevaDetalle) doc.add(tablaDetalle(d, negrita))
        }
        doc.add(parrafo(ClausulasContrato.cierre(d)).setMarginTop(6f))
        doc.add(
            firmas(
                negrita,
                Firma("EL CLIENTE", d.cliente, firmaCliente),
                Firma("EL CONTRATISTA", d.contratista, firmaContratista)
            )
        )
    }

    /** El recibo de adelanto: quién pagó, cuánto, por qué y cuánto falta. */
    fun recibo(
        archivo: File,
        d: DatosDelContrato,
        firmaCliente: Bitmap?,
        firmaContratista: Bitmap?
    ) = escribir(archivo, d.folio) { doc, negrita ->
        titulo(doc, negrita, ClausulasContrato.TITULO_RECIBO, d)
        val parrafos = ClausulasContrato.recibo(d)
        doc.add(parrafo(parrafos[0]))
        doc.add(tablaDetalle(d, negrita))
        parrafos.drop(1).forEach { doc.add(parrafo(it)) }
        doc.add(
            firmas(
                negrita,
                Firma("ENTREGA (EL CLIENTE)", d.cliente, firmaCliente),
                Firma("RECIBE (EL CONTRATISTA)", d.contratista, firmaContratista)
            )
        )
    }

    private fun escribir(
        archivo: File,
        folio: String,
        cuerpo: (Document, PdfFont) -> Unit
    ): File {
        val pdfDoc = PdfDocument(PdfWriter(archivo))
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, PieDeHoja(folio))
        val doc = Document(pdfDoc, PageSize.A4)
        doc.setMargins(36f, 40f, 44f, 40f)
        // El membrete y el sello son los de la proforma: el mismo papel de la misma tienda.
        val cabecera = PdfGenerator(contexto).also { it.membrete = membrete }
        membrete?.takeIf { it.haySello }?.let { cabecera.ponerSello(pdfDoc, it.selloRuta) }
        cabecera.ponerMembrete(doc)
        val negrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        cuerpo(doc, negrita)
        doc.close()
        return archivo
    }

    private fun titulo(doc: Document, negrita: PdfFont, texto: String, d: DatosDelContrato) {
        doc.add(
            Paragraph(texto).setFont(negrita).setFontSize(15f)
                .setTextAlignment(TextAlignment.CENTER).setMarginTop(8f).setMarginBottom(0f)
        )
        doc.add(
            Paragraph("Folio ${d.folio}   ·   ${ClausulasContrato.fechaCorta(d.fecha)}")
                .setFontSize(9f).setFontColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER).setMarginTop(0f).setMarginBottom(8f)
        )
    }

    private fun parrafo(texto: String): Paragraph =
        Paragraph(texto).setFontSize(10f).setTextAlignment(TextAlignment.JUSTIFIED)
            .setMultipliedLeading(1.15f).setMarginBottom(5f)

    /** Los trabajos en una tabla: número, descripción, medidas, cantidad e importe, y el total. */
    private fun tablaDetalle(d: DatosDelContrato, negrita: PdfFont): Table {
        val tabla = Table(UnitValue.createPercentArray(floatArrayOf(6f, 50f, 18f, 8f, 18f)))
            .setWidth(UnitValue.createPercentValue(100f)).setMarginTop(2f).setMarginBottom(8f)
        val gris = ColorConstants.LIGHT_GRAY
        fun cabecera(t: String, alinear: TextAlignment = TextAlignment.LEFT) = tabla.addHeaderCell(
            Cell().add(Paragraph(t).setFont(negrita).setFontSize(9f)).setTextAlignment(alinear)
                .setBackgroundColor(gris).setPadding(4f)
        )
        cabecera("N°", TextAlignment.CENTER)
        cabecera("Descripción")
        cabecera("Medidas", TextAlignment.CENTER)
        cabecera("Cant.", TextAlignment.CENTER)
        cabecera("Importe S/", TextAlignment.RIGHT)

        fun celda(t: String, alinear: TextAlignment = TextAlignment.LEFT) =
            Cell().add(Paragraph(t).setFontSize(9f)).setTextAlignment(alinear).setPadding(4f)

        var ambiente = ""
        ClausulasContrato.detalle(d.lista).forEach { f ->
            // El ambiente encabeza sus ítems, como en la proforma: en una obra entera se lee por salas.
            if (f.ambiente.isNotEmpty() && f.ambiente != ambiente) {
                ambiente = f.ambiente
                tabla.addCell(
                    Cell(1, 5).add(Paragraph(f.ambiente).setFont(negrita).setFontSize(9f))
                        .setPadding(4f).setBackgroundColor(ColorConstants.WHITE)
                )
            }
            tabla.addCell(celda(f.numero.toString(), TextAlignment.CENTER))
            tabla.addCell(celda(f.descripcion))
            tabla.addCell(celda(f.medidas, TextAlignment.CENTER))
            tabla.addCell(celda(f.cantidad, TextAlignment.CENTER))
            tabla.addCell(celda(ClausulasContrato.soles(f.importe), TextAlignment.RIGHT))
        }
        tabla.addCell(
            Cell(1, 4).add(Paragraph("TOTAL").setFont(negrita).setFontSize(10f))
                .setTextAlignment(TextAlignment.RIGHT).setPadding(4f)
        )
        tabla.addCell(
            Cell().add(Paragraph(ClausulasContrato.soles(d.total)).setFont(negrita).setFontSize(10f))
                .setTextAlignment(TextAlignment.RIGHT).setPadding(4f)
        )
        return tabla
    }

    private class Firma(val rol: String, val parte: ParteDelContrato, val imagen: Bitmap?)

    /**
     * Las dos firmas lado a lado y juntas en la misma hoja: cada una sobre su línea, y debajo quién
     * firma y con qué documento. Si se firmó en el celular, la firma va encima de su línea.
     */
    private fun firmas(negrita: PdfFont, izquierda: Firma, derecha: Firma): Div {
        val tabla = Table(UnitValue.createPercentArray(floatArrayOf(46f, 8f, 46f)))
            .setWidth(UnitValue.createPercentValue(100f))
        fun celda(f: Firma): Cell {
            val cell = Cell().setBorder(Border.NO_BORDER).setPadding(0f)
            val imagen = f.imagen?.let { bmp ->
                val bytes = ByteArrayOutputStream().also { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
                Image(ImageDataFactory.create(bytes.toByteArray())).scaleToFit(170f, 62f)
            }
            if (imagen != null) cell.add(imagen) else cell.add(Paragraph(" ").setHeight(62f))
            val p = f.parte
            val lineas = buildList {
                add(Paragraph(f.rol).setFont(negrita).setFontSize(9f))
                if (p.esEmpresa && p.representante.isNotBlank()) {
                    add(Paragraph(p.representante.trim()).setFontSize(9f))
                    add(Paragraph("DNI N° ${p.documentoRepresentante.trim().ifEmpty { BLANCO }}").setFontSize(9f))
                    add(Paragraph("p. ${p.nombre.trim()} — ${p.identificacion()}").setFontSize(8f))
                } else {
                    add(Paragraph(p.nombre.trim().ifEmpty { BLANCO }).setFontSize(9f))
                    add(Paragraph(p.identificacion()).setFontSize(9f))
                }
            }
            // La línea de firma es el borde de arriba del bloque del nombre.
            val bloque = Div().setBorderTop(SolidBorder(ColorConstants.BLACK, 0.8f)).setPaddingTop(2f)
            lineas.forEach { bloque.add(it.setMarginTop(0f).setMarginBottom(0f)) }
            cell.add(bloque)
            return cell
        }
        tabla.addCell(celda(izquierda))
        tabla.addCell(Cell().setBorder(Border.NO_BORDER))
        tabla.addCell(celda(derecha))
        return Div().add(tabla).setKeepTogether(true).setMarginTop(28f)
    }

    /** El folio y el número de página al pie de cada hoja: una hoja suelta se sabe de qué contrato es. */
    private class PieDeHoja(private val folio: String) : IEventHandler {
        override fun handleEvent(event: Event) {
            val e = event as PdfDocumentEvent
            val pagina = e.page
            val caja = pagina.pageSize
            val numero = e.document.getPageNumber(pagina)
            val lienzo = PdfCanvas(pagina.newContentStreamAfter(), pagina.resources, e.document)
            com.itextpdf.layout.Canvas(lienzo, caja).use { c ->
                c.setFontSize(8f).setFontColor(ColorConstants.GRAY)
                c.showTextAligned("Folio $folio", caja.left + 40f, caja.bottom + 22f, TextAlignment.LEFT)
                c.showTextAligned("Página $numero", caja.right - 40f, caja.bottom + 22f, TextAlignment.RIGHT)
            }
        }
    }
}
