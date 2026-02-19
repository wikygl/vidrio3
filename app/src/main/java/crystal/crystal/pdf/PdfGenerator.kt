package crystal.crystal.pdf

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider

import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.events.Event
import com.itextpdf.kernel.events.IEventHandler
import com.itextpdf.kernel.events.PdfDocumentEvent
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue

import crystal.crystal.Listado

import java.io.ByteArrayOutputStream
import java.io.File

class PdfGenerator(private val activity: AppCompatActivity) {

    fun generarYCompartir(cliente: String, lista: List<Listado>, precioTotal: String) {
        val pdfFile = generar(cliente, lista, precioTotal) ?: return

        if (pdfFile.exists() && pdfFile.length() > 0) {
            val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", pdfFile)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "application/pdf"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            activity.startActivity(Intent.createChooser(shareIntent, "Compartir archivo"))
        } else {
            Toast.makeText(activity, "Error al generar el archivo PDF", Toast.LENGTH_SHORT).show()
        }
    }

    fun generar(cliente: String, lista: List<Listado>, precioTotal: String): File? {
        val pdfFileName = "Presupuesto_${cliente}.pdf"
        val pdfFile = File(activity.getExternalFilesDir(null), pdfFileName)

        val writer = PdfWriter(pdfFile)
        val pdfDoc = PdfDocument(writer)

        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, PageNumeration())

        val document = Document(pdfDoc, PageSize.A4)
        document.setMargins(36f, 36f, 36f, 36f)

        val tituloFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        val titulo = Paragraph("Proforma $cliente")
            .setFont(tituloFont)
            .setFontSize(27f)
            .setBold()
        document.add(titulo)

        if (lista.isEmpty()) {
            Toast.makeText(activity, "La lista vacía", Toast.LENGTH_SHORT).show()
            document.close()
            return null
        }

        var itemNum = 0

        for (item in lista) {
            itemNum++

            val itemTitleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
            val tituloItemParagraph = Paragraph("Ítem $itemNum: ${item.producto}")
                .setFont(itemTitleFont)
                .setFontSize(16f)
                .setBold()

            document.add(tituloItemParagraph)

            val table = Table(UnitValue.createPercentArray(floatArrayOf(50f, 50f)))
            table.setWidth(UnitValue.createPercentValue(100f))

            val ancho = item.medi1
            val alto = item.medi2
            val fondo = item.medi3
            val cantidad = item.canti
            val costo = item.costo
            val anexo = item.uri

            val textoMedidas = when (item.escala) {
                "p2", "m2" -> "Ancho: ${df1(ancho)}\nAlto: ${df1(alto)}\nCantidad: ${df1(cantidad)}"
                "ml" -> "Metros: ${df1(ancho)}\nCantidad: ${df1(cantidad)}"
                "m3" -> "Ancho: ${df1(ancho)}\nAlto: ${df1(alto)}\nFondo: ${df1(fondo)}\nCantidad: ${df1(cantidad)}"
                "uni" -> "Cantidad: ${df1(cantidad)}"
                else -> ""
            }

            val costoFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
            val parrafoCosto = Paragraph("\nCosto: ${df2(costo)}")
                .setFont(costoFont)
                .setBold()

            val cellTexto = Cell()
            cellTexto.add(Paragraph(textoMedidas))
            cellTexto.add(parrafoCosto)
            cellTexto.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            cellTexto.setPadding(9f)
            table.addCell(cellTexto)

            val imageCell = crearCeldaImagen(anexo)
            imageCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            imageCell.setPadding(9f)
            table.addCell(imageCell)

            table.setKeepTogether(true)
            document.add(table)

            val separator = com.itextpdf.layout.element.LineSeparator(
                com.itextpdf.kernel.pdf.canvas.draw.SolidLine()
            )
            separator.setStrokeColor(ColorConstants.GRAY)
            separator.setStrokeWidth(1f)
            document.add(separator)

            document.add(Paragraph("\n"))
        }

        val tituloTotal = "Precio total: S/.$precioTotal"

        val tablaTotalBox = Table(1)
        tablaTotalBox.setWidth(UnitValue.createPercentValue(100f))

        val cellTotal = Cell()
            .add(Paragraph(tituloTotal)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(16f)
                .setBold()
                .setTextAlignment(TextAlignment.RIGHT)
            )
        cellTotal.setBorder(SolidBorder(ColorConstants.BLACK, 1f))
        cellTotal.setPadding(10f)

        tablaTotalBox.addCell(cellTotal)
        document.add(tablaTotalBox)

        document.close()

        Toast.makeText(activity, "PDF generado: ${pdfFile.absolutePath}", Toast.LENGTH_LONG).show()
        return pdfFile
    }

    private fun crearCeldaImagen(anexo: String): Cell {
        if (anexo.isEmpty()) {
            return Cell().add(Paragraph(""))
        }

        return try {
            val imageUri = Uri.parse(anexo)
            val bitmap = BitmapFactory.decodeStream(activity.contentResolver.openInputStream(imageUri))

            if (bitmap != null) {
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                val imageData = ImageDataFactory.create(stream.toByteArray())
                val image = Image(imageData)
                image.setAutoScale(true)
                image.setMaxWidth(270f)
                image.setMaxHeight(270f)
                Cell().add(image)
            } else {
                Cell().add(Paragraph("Imagen no disponible"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Cell().add(Paragraph("Imagen no disponible"))
        }
    }

    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    private fun df2(defo: Float): String {
        return "%.2f".format(defo).replace(".", ",")
    }

    private class PageNumeration : IEventHandler {
        override fun handleEvent(event: Event) {
            val docEvent = event as PdfDocumentEvent
            val pdfDoc = docEvent.document
            val page = docEvent.page
            val pageNumber = pdfDoc.getPageNumber(page)

            val pageSize = page.pageSize
            val pdfCanvas = PdfCanvas(page.newContentStreamBefore(), page.resources, pdfDoc)

            val canvas = com.itextpdf.layout.Canvas(pdfCanvas, pageSize)

            canvas.showTextAligned(
                "Página $pageNumber",
                pageSize.right - 36f,
                pageSize.bottom + 18f,
                TextAlignment.RIGHT
            )

            canvas.close()
        }
    }
}
