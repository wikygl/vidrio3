package crystal.crystal.pdf

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope

import com.bumptech.glide.Glide
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PdfGenerator(private val activity: AppCompatActivity) {

    fun generarYCompartir(cliente: String, lista: List<Listado>, precioTotal: String) {
        if (lista.isEmpty()) {
            Toast.makeText(activity, "La lista vacia", Toast.LENGTH_SHORT).show()
            return
        }

        val listaSnapshot = lista.map { it.copy() }
        Toast.makeText(activity, "Generando PDF...", Toast.LENGTH_SHORT).show()

        activity.lifecycleScope.launch {
            val pdfFile = withContext(Dispatchers.IO) {
                runCatching { generar(cliente, listaSnapshot, precioTotal) }.getOrNull()
            }

            if (activity.isFinishing || activity.isDestroyed) return@launch

            if (pdfFile != null && pdfFile.exists() && pdfFile.length() > 0) {
                val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", pdfFile)
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "application/pdf"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                Toast.makeText(activity, "PDF generado", Toast.LENGTH_SHORT).show()
                activity.startActivity(Intent.createChooser(shareIntent, "Compartir archivo"))
            } else {
                Toast.makeText(activity, "Error al generar el archivo PDF", Toast.LENGTH_SHORT).show()
            }
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

            val imageCell = crearCeldaImagenes(anexo)
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

        return pdfFile
    }

    private fun crearCeldaImagenes(anexo: String): Cell {
        val imagenes = extraerImagenesAnexo(anexo).take(5)
        if (imagenes.isEmpty()) {
            return Cell().add(Paragraph(""))
        }

        if (imagenes.size == 1) {
            return crearCeldaImagen(imagenes.first())
        }

        val cell = Cell()
        cell.add(
            Paragraph("Opciones de mismo costo a escoger:")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(10f)
                .setBold()
        )

        val tablaImagenes = Table(UnitValue.createPercentArray(floatArrayOf(1f, 1f)))
        tablaImagenes.setWidth(UnitValue.createPercentValue(100f))

        imagenes.forEachIndexed { index, imageUri ->
            val opcionCell = Cell()
            opcionCell.setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
            opcionCell.setPadding(4f)
            opcionCell.add(
                Paragraph("Opcion ${index + 1}")
                    .setFontSize(9f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
            crearImagenPdf(imageUri, maxWidth = 125f, maxHeight = 115f)?.let { image ->
                opcionCell.add(image)
            } ?: opcionCell.add(
                Paragraph("Imagen no disponible")
                    .setFontSize(8f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
            tablaImagenes.addCell(opcionCell)
        }

        if (imagenes.size % 2 != 0) {
            tablaImagenes.addCell(
                Cell()
                    .setBorder(com.itextpdf.layout.borders.Border.NO_BORDER)
                    .setPadding(4f)
            )
        }

        cell.add(tablaImagenes)
        return cell
    }

    private fun crearCeldaImagen(anexo: String): Cell {
        return try {
            crearImagenPdf(anexo, maxWidth = 270f, maxHeight = 270f)?.let { image ->
                Cell().add(image)
            } ?: Cell().add(Paragraph("Imagen no disponible"))
        } catch (e: Exception) {
            e.printStackTrace()
            Cell().add(Paragraph("Imagen no disponible"))
        }
    }

    private fun crearImagenPdf(anexo: String, maxWidth: Float, maxHeight: Float): Image? {
        val imageUri = Uri.parse(anexo)
        val bitmap = decodificarImagenReducida(imageUri) ?: return null
        val stream = ByteArrayOutputStream()
        val formato = if (bitmap.hasAlpha()) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        val calidad = if (formato == Bitmap.CompressFormat.PNG) 100 else 78
        bitmap.compress(formato, calidad, stream)
        bitmap.recycle()
        val imageData = ImageDataFactory.create(stream.toByteArray())
        return Image(imageData).apply {
            setAutoScale(true)
            setMaxWidth(maxWidth)
            setMaxHeight(maxHeight)
        }
    }

    private fun extraerImagenesAnexo(anexo: String): List<String> {
        return anexo
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()
    }

    private fun decodificarImagenReducida(
        imageUri: Uri,
        maxWidth: Int = 1400,
        maxHeight: Int = 1400
    ): Bitmap? {
        decodificarConImageDecoder(imageUri, maxWidth, maxHeight)?.let { return it }
        decodificarConBitmapFactory(imageUri, maxWidth, maxHeight)?.let { return it }
        return decodificarConGlide(imageUri, maxWidth, maxHeight)
    }

    private fun decodificarConImageDecoder(
        imageUri: Uri,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) return null

        return runCatching {
            val source = ImageDecoder.createSource(activity.contentResolver, imageUri)
            ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                val width = info.size.width
                val height = info.size.height
                val scale = minOf(
                    maxWidth.toFloat() / width.toFloat(),
                    maxHeight.toFloat() / height.toFloat(),
                    1f
                )
                decoder.setTargetSize(
                    (width * scale).toInt().coerceAtLeast(1),
                    (height * scale).toInt().coerceAtLeast(1)
                )
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        }.getOrNull()
    }

    private fun decodificarConBitmapFactory(
        imageUri: Uri,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? {
        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        activity.contentResolver.openInputStream(imageUri)?.use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        } ?: return null

        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        val opciones = BitmapFactory.Options().apply {
            inSampleSize = calcularInSampleSize(bounds.outWidth, bounds.outHeight, maxWidth, maxHeight)
            inPreferredConfig = Bitmap.Config.RGB_565
        }

        return activity.contentResolver.openInputStream(imageUri)?.use { input ->
            BitmapFactory.decodeStream(input, null, opciones)
        }
    }

    private fun decodificarConGlide(
        imageUri: Uri,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? {
        return runCatching {
            Glide.with(activity.applicationContext)
                .asBitmap()
                .load(imageUri)
                .submit(maxWidth, maxHeight)
                .get()
        }.getOrNull()
    }

    private fun calcularInSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
            inSampleSize *= 2
        }
        return inSampleSize.coerceAtLeast(1)
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
