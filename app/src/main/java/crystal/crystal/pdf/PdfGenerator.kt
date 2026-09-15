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

/**
 * Arma las proformas en PDF.
 *
 * Armar el documento solo necesita un CONTEXTO —leer imágenes y escribir el archivo—; la pantalla
 * hace falta únicamente para compartirlo. Por eso se puede construir con un contexto a secas, que
 * es lo que permite generar una proforma en una prueba y mirarla, sin levantar la pantalla
 * principal con su autenticación.
 */
class PdfGenerator private constructor(
    private val contexto: android.content.Context,
    private val activity: AppCompatActivity?
) {
    constructor(activity: AppCompatActivity) : this(activity, activity)

    /** Solo para armar documentos: sin pantalla no se puede compartir, pero sí escribir el PDF. */
    constructor(contexto: android.content.Context) : this(contexto, null)

    /**
     * Quién hace la proforma y qué se ve al fondo. Lo pone la pantalla antes de generar; sin ello,
     * la proforma sale como salía —sin membrete y sin sello—, que es lo que había.
     */
    var membrete: crystal.crystal.pos.MembreteDeProforma? = null
    var cliente: crystal.crystal.pos.ClienteDeLaProforma? = null

    /**
     * El membrete: el logo de la tienda, sus datos y quién midió.
     *
     * Va arriba del todo y antes del título, que es donde se mira primero para saber de quién es el
     * papel. Los datos son los mismos que ya usa el ticket: una tienda no tiene dos direcciones.
     */
    private fun ponerMembrete(document: Document) {
        val m = membrete ?: return
        if (!m.hayMembrete) return
        val negrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        val tabla = Table(UnitValue.createPercentArray(floatArrayOf(22f, 78f)))
        tabla.setWidth(UnitValue.createPercentValue(100f))

        val celdaLogo = Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(2f)
        m.logo?.let { crearImagenPdf(it, 110f, 90f) }?.let { celdaLogo.add(it) }
        tabla.addCell(celdaLogo)

        val datos = Cell().setBorder(com.itextpdf.layout.borders.Border.NO_BORDER).setPadding(4f)
        m.empresa?.let { e ->
            datos.add(
                Paragraph(e.nombreComercial ?: e.razonSocial)
                    .setFont(negrita).setFontSize(15f).setBold().setMarginBottom(0f)
            )
            val lineas = listOfNotNull(
                e.ruc.takeIf { it.isNotBlank() }?.let { "RUC: $it" },
                e.direccion.takeIf { it.isNotBlank() },
                e.telefono.takeIf { it.isNotBlank() }?.let { "Tel. $it" },
                e.email?.takeIf { it.isNotBlank() }
            )
            if (lineas.isNotEmpty()) {
                datos.add(
                    Paragraph(lineas.joinToString("   ·   "))
                        .setFontSize(10f).setFontColor(ColorConstants.DARK_GRAY).setMarginTop(0f)
                )
            }
        }
        if (m.tecnico.isNotBlank()) {
            datos.add(
                Paragraph("Atendido por: ${m.tecnico}")
                    .setFontSize(10f).setFontColor(ColorConstants.DARK_GRAY).setMarginTop(0f)
            )
        }
        tabla.addCell(datos)
        document.add(tabla)

        val raya = com.itextpdf.layout.element.LineSeparator(
            com.itextpdf.kernel.pdf.canvas.draw.SolidLine()
        )
        raya.setStrokeColor(ColorConstants.GRAY)
        raya.setStrokeWidth(1f)
        document.add(raya)
    }

    /** Los datos del cliente, debajo del título: a quién va dirigida y cómo encontrarlo. */
    private fun ponerCliente(document: Document) {
        val c = cliente ?: return
        if (!c.hayDatos) return
        document.add(
            Paragraph(c.lineas().joinToString("   ·   "))
                .setFontSize(10f).setFontColor(ColorConstants.DARK_GRAY)
        )
    }

    /** La nota del pie: validez, forma de pago, lo que la tienda quiera dejar dicho. */
    private fun ponerNota(document: Document) {
        val nota = membrete?.nota.orEmpty()
        if (nota.isBlank()) return
        document.add(
            Paragraph(nota).setFontSize(9f).setFontColor(ColorConstants.GRAY).setMarginTop(10f)
        )
    }

    /**
     * El sello de agua, al fondo de CADA hoja y por debajo de lo escrito.
     *
     * Se dibuja en el canvas de fondo de la página y con la tinta muy rebajada, así que no estorba
     * la lectura: se ve la marca de la tienda detrás de los números. Vale PNG, JPG y SVG —el SVG se
     * rasteriza antes, que iText no lo dibuja—.
     */
    private fun ponerSello(pdfDoc: PdfDocument, ruta: String) {
        val bitmap = rasterizarSello(ruta) ?: return
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        bitmap.recycle()
        val datos = runCatching { ImageDataFactory.create(stream.toByteArray()) }.getOrNull() ?: return
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, SelloDeAgua(datos))
    }

    /** El sello como mapa de bits: un SVG se dibuja primero, y lo demás se lee como imagen. */
    private fun rasterizarSello(ruta: String): Bitmap? = runCatching {
        if (ruta.substringBefore('?').endsWith(".svg", ignoreCase = true)) {
            val texto = contexto.contentResolver.openInputStream(Uri.parse(ruta))
                ?.use { it.readBytes().toString(Charsets.UTF_8) } ?: return@runCatching null
            val svg = com.caverock.androidsvg.SVG.getFromString(texto)
            val lado = 900
            svg.documentWidth.takeIf { it > 0f } ?: svg.setDocumentWidth(lado.toFloat())
            svg.documentHeight.takeIf { it > 0f } ?: svg.setDocumentHeight(lado.toFloat())
            val bmp = Bitmap.createBitmap(lado, lado, Bitmap.Config.ARGB_8888)
            android.graphics.Canvas(bmp).drawPicture(
                svg.renderToPicture(lado, lado),
                android.graphics.Rect(0, 0, lado, lado)
            )
            bmp
        } else {
            decodificarImagenReducida(Uri.parse(ruta), 1200, 1200)
        }
    }.onFailure { android.util.Log.e("PdfGenerator", "No se pudo leer el sello: $ruta", it) }
        .getOrNull()

    /** Estampa la imagen del sello centrada en cada hoja, en claro y por detrás de lo escrito. */
    private class SelloDeAgua(
        private val imagen: com.itextpdf.io.image.ImageData
    ) : IEventHandler {
        override fun handleEvent(event: Event) {
            val docEvent = event as PdfDocumentEvent
            val pagina = docEvent.page
            val caja = pagina.pageSize
            val lienzo = PdfCanvas(pagina.newContentStreamBefore(), pagina.resources, docEvent.document)
            val estado = com.itextpdf.kernel.pdf.extgstate.PdfExtGState().setFillOpacity(0.07f)
            lienzo.saveState().setExtGState(estado)
            val lado = minOf(caja.width, caja.height) * 0.6f
            com.itextpdf.layout.Canvas(lienzo, caja).use { canvas ->
                canvas.add(
                    Image(imagen).apply {
                        setAutoScale(true)
                        setMaxWidth(lado)
                        setMaxHeight(lado)
                        setFixedPosition(
                            docEvent.document.getPageNumber(pagina),
                            (caja.width - lado) / 2f,
                            (caja.height - lado) / 2f,
                            UnitValue.createPointValue(lado)
                        )
                    }
                )
            }
            lienzo.restoreState()
        }
    }

    fun generarYCompartir(cliente: String, lista: List<Listado>, precioTotal: String) {
        // Candado (Fase 3): exportar PDF es de pago. Con el cobro apagado no bloquea.
        val pantalla = activity ?: return
        if (!crystal.crystal.Suscripcion.exigir(pantalla, crystal.crystal.Suscripcion.puedeExportarPdf(),
                "Exportar/compartir PDF es una función de pago.")) return
        if (lista.isEmpty()) {
            Toast.makeText(pantalla, "La lista vacia", Toast.LENGTH_SHORT).show()
            return
        }

        val listaSnapshot = lista.map { it.copy() }
        Toast.makeText(pantalla, "Generando PDF...", Toast.LENGTH_SHORT).show()

        pantalla.lifecycleScope.launch {
            val pdfFile = withContext(Dispatchers.IO) {
                runCatching { generar(cliente, listaSnapshot, precioTotal) }
                    .onFailure { android.util.Log.e("PdfGenerator", "No se pudo armar la proforma", it) }
                    .getOrNull()
            }

            if (pantalla.isFinishing || pantalla.isDestroyed) return@launch

            if (pdfFile != null && pdfFile.exists() && pdfFile.length() > 0) {
                val uri = FileProvider.getUriForFile(pantalla, "${pantalla.packageName}.fileprovider", pdfFile)
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "application/pdf"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                Toast.makeText(pantalla, "PDF generado", Toast.LENGTH_SHORT).show()
                pantalla.startActivity(Intent.createChooser(shareIntent, "Compartir archivo"))
            } else {
                Toast.makeText(pantalla, "Error al generar el archivo PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * La proforma de ELECCIÓN: cada medida una vez, con su imagen, y debajo sus opciones.
     *
     * Es la que pide un cliente que quiere el mismo producto en varios materiales —vidrio arenado
     * laminado, policarbonato, serie 80— para escoger uno. Por eso NO lleva total: sumar las
     * opciones sería cobrarlas todas. Cada una dice lo que cuesta una pieza y, si se pidió más de
     * una, lo que costarían todas con ese material.
     */
    fun generarOpcionesYCompartir(cliente: String, lista: List<Listado>) {
        val pantalla = activity ?: return
        if (!crystal.crystal.Suscripcion.exigir(pantalla, crystal.crystal.Suscripcion.puedeExportarPdf(),
                "Exportar/compartir PDF es una función de pago.")) return
        if (lista.isEmpty()) {
            Toast.makeText(pantalla, "La lista vacia", Toast.LENGTH_SHORT).show()
            return
        }
        val listaSnapshot = lista.map { it.copy() }
        Toast.makeText(pantalla, "Generando PDF...", Toast.LENGTH_SHORT).show()

        pantalla.lifecycleScope.launch {
            val pdfFile = withContext(Dispatchers.IO) {
                runCatching { generarOpciones(cliente, listaSnapshot) }
                    .onFailure { android.util.Log.e("PdfGenerator", "No se pudo armar la proforma de opciones", it) }
                    .getOrNull()
            }
            if (pantalla.isFinishing || pantalla.isDestroyed) return@launch
            if (pdfFile != null && pdfFile.exists() && pdfFile.length() > 0) {
                val uri = FileProvider.getUriForFile(pantalla, "${pantalla.packageName}.fileprovider", pdfFile)
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_STREAM, uri)
                    type = "application/pdf"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                Toast.makeText(pantalla, "PDF generado", Toast.LENGTH_SHORT).show()
                pantalla.startActivity(Intent.createChooser(shareIntent, "Compartir archivo"))
            } else {
                Toast.makeText(pantalla, "Error al generar el archivo PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun generarOpciones(cliente: String, lista: List<Listado>): File? {
        val pdfFile = File(contexto.getExternalFilesDir(null), nombreDeArchivo("Proforma_de_opciones", cliente))
        val pdfDoc = PdfDocument(PdfWriter(pdfFile))
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, PageNumeration())
        val document = Document(pdfDoc, PageSize.A4)
        document.setMargins(36f, 36f, 36f, 36f)

        membrete?.selloRuta?.takeIf { membrete?.haySello == true }?.let { ponerSello(pdfDoc, it) }

        val negrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        ponerMembrete(document)
        document.add(
            Paragraph("Proforma de $cliente").setFont(negrita).setFontSize(27f).setBold()
        )
        ponerCliente(document)
        document.add(
            Paragraph(
                "Cada ítem se ofrece en varios materiales. Los precios no se suman: " +
                    "se elige una opción por ítem."
            ).setFontSize(11f).setFontColor(ColorConstants.DARK_GRAY)
        )
        document.add(Paragraph("\n"))

        if (lista.isEmpty()) {
            document.close()
            return null
        }

        var itemNum = 0
        // Por ambientes: la sala, el consultorio, el piso. Una obra entera se lee por sitios —"sala
        // de partos" con lo suyo, "sexto piso" con lo suyo—, no como una lista corrida de medidas.
        for ((ambiente, items) in crystal.crystal.pos.AmbientesDeProforma.agrupar(lista)) {
        items.forEachIndexed { cual, item ->
            itemNum++
            // TODO el ítem va en un solo bloque que el PDF no puede partir: su número, su medida y
            // su tabla de opciones caen juntos en la misma hoja. Añadiéndolos sueltos, el corte de
            // página caía en medio y el ítem salía recortado, con media tabla en la hoja siguiente.
            //
            // El margen de abajo hace de separación: un párrafo en blanco puesto aparte se quedaba
            // solo al principio de la hoja siguiente.
            val bloque = com.itextpdf.layout.element.Div()
                .setKeepTogether(true)
                .setMarginBottom(14f)
            // Y el nombre de la sala va DENTRO del bloque de su primer ítem, no suelto encima:
            // suelto saltaba de página él solo y dejaba una hoja con dos palabras y nada más.
            if (cual == 0 && ambiente.isNotEmpty()) {
                bloque.add(
                    Paragraph(ambiente)
                        .setFont(negrita).setFontSize(19f).setBold()
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setMarginTop(6f)
                )
            }
            bloque.add(
                Paragraph("Ítem $itemNum").setFont(negrita).setFontSize(16f).setBold()
            )

            // La medida, en una línea: aquí las imágenes van con cada opción, en su recuadro, así
            // que arriba no se repite ninguna.
            val textoMedidas = when (item.escala) {
                "p2", "m2" -> "Ancho: ${df1(item.medi1)}   Alto: ${df1(item.medi2)}   Cantidad: ${df1(item.canti)}"
                "ml" -> "Metros: ${df1(item.medi1)}   Cantidad: ${df1(item.canti)}"
                "m3" -> "Ancho: ${df1(item.medi1)}   Alto: ${df1(item.medi2)}   Fondo: ${df1(item.medi3)}   Cantidad: ${df1(item.canti)}"
                "uni" -> "Cantidad: ${df1(item.canti)}"
                else -> ""
            }
            bloque.add(Paragraph(textoMedidas).setFontSize(12f))

            // Las opciones: la del propio ítem primero —es la que se apuntó al medir— y detrás las
            // demás, en el orden en que se escribieron.
            val opciones = listOf(crystal.crystal.pos.OpcionesDeProforma.comoEstaApuntado(item)) +
                crystal.crystal.pos.OpcionesDeProforma.de(item)
            val tablaOpciones = Table(UnitValue.createPercentArray(floatArrayOf(8f, 30f, 32f, 30f)))
            tablaOpciones.setWidth(UnitValue.createPercentValue(100f))
            opciones.forEachIndexed { orden, opcion ->
                val letra = ('A' + orden).toString()
                tablaOpciones.addCell(
                    Cell().add(Paragraph(letra).setFont(negrita).setBold())
                        .setPadding(6f)
                )
                tablaOpciones.addCell(
                    Cell().add(Paragraph(opcion.producto)).setPadding(6f)
                )
                // La imagen de la opción, en SU recuadro: un arenado laminado y un policarbonato no
                // se parecen en nada, y el cliente elige mirando. La de la primera es la del ítem,
                // y va aquí como las demás: enseñándola solo arriba, esa opción parecía no tener.
                val celdaFoto = Cell().setPadding(6f)
                val deDondeSale = if (orden == 0) item.uri else opcion.imagen
                if (deDondeSale.isNotBlank()) {
                    val foto = crearImagenPdf(primeraImagen(deDondeSale), 130f, 78f)
                    // Si la imagen no se puede leer se dice, en vez de dejar el hueco callado: así
                    // se sabe que a esa opción le falta su foto y hay que volver a anexarla.
                    if (foto != null) celdaFoto.add(foto)
                    else celdaFoto.add(
                        Paragraph("Imagen no disponible")
                            .setFontSize(9f).setFontColor(ColorConstants.GRAY)
                    )
                }
                tablaOpciones.addCell(celdaFoto)
                val unidad = crystal.crystal.pos.OpcionesDeProforma.precioUnitario(item, opcion)
                val todas = crystal.crystal.pos.OpcionesDeProforma.precioPorLaCantidad(item, opcion)
                val texto = if (item.canti > 1f) {
                    "S/ ${df2(unidad)} c/u\nS/ ${df2(todas)} por ${df1(item.canti)}"
                } else {
                    "S/ ${df2(unidad)}"
                }
                tablaOpciones.addCell(
                    Cell().add(Paragraph(texto).setFont(negrita).setBold())
                        .setTextAlignment(TextAlignment.RIGHT).setPadding(6f)
                )
            }
            tablaOpciones.setKeepTogether(true)
            bloque.add(tablaOpciones)

            val separator = com.itextpdf.layout.element.LineSeparator(
                com.itextpdf.kernel.pdf.canvas.draw.SolidLine()
            )
            separator.setStrokeColor(ColorConstants.GRAY)
            separator.setStrokeWidth(1f)
            bloque.add(separator)
            document.add(bloque)
        }
        }

        // Y aquí NO va el total: es una proforma de elección.
        ponerNota(document)
        document.close()
        return pdfFile
    }

    fun generar(cliente: String, lista: List<Listado>, precioTotal: String): File? {
        val pdfFile = File(contexto.getExternalFilesDir(null), nombreDeArchivo("Presupuesto", cliente))

        val writer = PdfWriter(pdfFile)
        val pdfDoc = PdfDocument(writer)

        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, PageNumeration())

        val document = Document(pdfDoc, PageSize.A4)
        document.setMargins(36f, 36f, 36f, 36f)

        membrete?.selloRuta?.takeIf { membrete?.haySello == true }?.let { ponerSello(pdfDoc, it) }

        val tituloFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        ponerMembrete(document)
        val titulo = Paragraph("Proforma de $cliente")
            .setFont(tituloFont)
            .setFontSize(27f)
            .setBold()
        document.add(titulo)
        ponerCliente(document)

        if (lista.isEmpty()) {
            document.close()
            return null
        }

        var itemNum = 0

        // Por ambientes, si se apuntaron: la sala, el consultorio, el piso. Sin ellos sale como
        // salía, en una sola tirada.
        for ((ambiente, items) in crystal.crystal.pos.AmbientesDeProforma.agrupar(lista)) {
        items.forEachIndexed { cual, item ->
            itemNum++

            val itemTitleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
            val tituloItemParagraph = Paragraph("Ítem $itemNum: ${item.producto}")
                .setFont(itemTitleFont)
                .setFontSize(16f)
                .setBold()

            // El ítem entero en un bloque que no se puede partir: su título, su medida, su costo y
            // su imagen caen juntos en la misma hoja. Sueltos, el corte de página caía en medio. La
            // separación va como margen: un párrafo en blanco aparte se quedaba solo en la hoja
            // siguiente, y con él la hoja quedaba casi vacía.
            val bloque = com.itextpdf.layout.element.Div()
                .setKeepTogether(true)
                .setMarginBottom(14f)
            // El nombre de la sala va DENTRO del bloque de su primer ítem: suelto encima saltaba de
            // página él solo y dejaba una hoja con dos palabras y nada más.
            if (cual == 0 && ambiente.isNotEmpty()) {
                bloque.add(
                    Paragraph(ambiente)
                        .setFont(itemTitleFont).setFontSize(19f).setBold()
                        .setFontColor(ColorConstants.DARK_GRAY)
                        .setMarginTop(6f)
                )
            }
            bloque.add(tituloItemParagraph)

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
            bloque.add(table)

            val separator = com.itextpdf.layout.element.LineSeparator(
                com.itextpdf.kernel.pdf.canvas.draw.SolidLine()
            )
            separator.setStrokeColor(ColorConstants.GRAY)
            separator.setStrokeWidth(1f)
            bloque.add(separator)
            document.add(bloque)
        }
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

        ponerNota(document)

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

    /**
     * Una imagen para el documento, o null si no se pudo leer. NUNCA revienta: ninguna imagen puede
     * llevarse por delante una proforma —el vidriero necesita el papel aunque le falte una foto—.
     */
    private fun crearImagenPdf(anexo: String, maxWidth: Float, maxHeight: Float): Image? =
        runCatching { armarImagenPdf(anexo, maxWidth, maxHeight) }
            .onFailure { android.util.Log.e("PdfGenerator", "No se pudo poner la imagen: $anexo", it) }
            .getOrNull()

    private fun armarImagenPdf(anexo: String, maxWidth: Float, maxHeight: Float): Image? {
        val imageUri = Uri.parse(anexo)
        val bitmap = decodificarImagenReducida(imageUri) ?: return null
        val stream = ByteArrayOutputStream()
        val formato = if (bitmap.hasAlpha()) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        val calidad = if (formato == Bitmap.CompressFormat.PNG) 100 else 78
        bitmap.compress(formato, calidad, stream)
        bitmap.recycle()
        val imageData = ImageDataFactory.create(stream.toByteArray())
        return Image(imageData).apply {
            // `scaleToFit` y no `setAutoScale`: el autoescalado estira la foto al ANCHO de su celda
            // y se salta el alto que se le pide, así que una foto vertical salía de 230 puntos con
            // 88 pedidos. Un ítem con tres opciones ocupaba una hoja entera, no cabía donde tocaba
            // y saltaba de página dejando la anterior casi vacía. Esto respeta las dos medidas.
            scaleToFit(maxWidth, maxHeight)
        }
    }

    /**
     * El nombre del archivo, con el del cliente limpio de lo que no cabe en un nombre.
     *
     * Un cliente se llama "Clínica Bilbao S.A.C. / sede 2" y esa barra es un directorio que no
     * existe: el archivo no se podía crear y la proforma fallaba entera sin decir por qué.
     */
    private fun nombreDeArchivo(que: String, cliente: String): String {
        val limpio = cliente.trim()
            .replace(Regex("""[\\/:*?"<>|\r\n\t]"""), " ")
            .replace(Regex(" +"), " ")
            .trim()
            .take(60)
        return if (limpio.isEmpty()) "$que.pdf" else "${que}_$limpio.pdf"
    }

    /**
     * La primera imagen de un anexo. El ítem puede llevar varias —van una por línea— y en el
     * recuadro de su opción cabe una: la primera, que es la que lo representa.
     */
    private fun primeraImagen(anexo: String): String =
        extraerImagenesAnexo(anexo).firstOrNull() ?: anexo

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
            val source = ImageDecoder.createSource(contexto.contentResolver, imageUri)
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

    /**
     * Ojo: todo va dentro de un `runCatching`, como en los otros dos decodificadores.
     *
     * `openInputStream` REVIENTA cuando la imagen ya no se puede leer —una de la galería anexada en
     * otra sesión, una foto borrada del teléfono—, y estando suelto se llevaba por delante la
     * proforma entera: una sola imagen mala y no salía el PDF. Devolviendo null, esa imagen se
     * salta y el documento se arma igual.
     */
    private fun decodificarConBitmapFactory(
        imageUri: Uri,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? = runCatching {
        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        contexto.contentResolver.openInputStream(imageUri)?.use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        } ?: return@runCatching null

        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null

        val opciones = BitmapFactory.Options().apply {
            inSampleSize = calcularInSampleSize(bounds.outWidth, bounds.outHeight, maxWidth, maxHeight)
            inPreferredConfig = Bitmap.Config.RGB_565
        }

        contexto.contentResolver.openInputStream(imageUri)?.use { input ->
            BitmapFactory.decodeStream(input, null, opciones)
        }
    }.getOrNull()

    private fun decodificarConGlide(
        imageUri: Uri,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? {
        return runCatching {
            Glide.with(contexto.applicationContext)
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
