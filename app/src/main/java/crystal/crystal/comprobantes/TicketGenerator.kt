package crystal.crystal.comprobantes

import android.content.Context
import android.util.Log
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.*
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.properties.HorizontalAlignment
import java.net.URL

/**
 * Generador de comprobantes de venta en formato PDF
 * Genera: BOLETA, FACTURA, TICKET/RECIBO
 */
class TicketGenerator(private val context: Context) {

    private val formatoMoneda = NumberFormat.getCurrencyInstance(Locale("es", "PE")).apply {
        currency = Currency.getInstance("PEN")
    }

    /**
     * Genera comprobante según el tipo seleccionado
     */
    fun generarComprobante(
        tipoComprobante: String, // "BOLETA", "FACTURA", "TICKET", "RECIBO"
        numeroComprobante: String,
        cliente: String,
        clienteDocumento: String = "",
        clienteDireccion: String = "",
        items: List<ItemVenta>,
        subtotal: Float,
        igv: Float,
        total: Float,
        empresa: DatosEmpresa,
        configuracion: ConfiguracionTicket,
        vendedor: String? = null,
        terminal: String? = null,
        formaPago: String = "EFECTIVO"
    ): File {

        // Crear directorio
        val comprobantesDir = File(context.getExternalFilesDir(null), "comprobantes")
        if (!comprobantesDir.exists()) {
            comprobantesDir.mkdirs()
        }

        // Nombre del archivo
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val nombreArchivo = "${tipoComprobante}_${numeroComprobante.replace("/", "-")}_$timestamp.pdf"
        val archivoDestino = File(comprobantesDir, nombreArchivo)

        // Tamaño de página
        val pageSize = when (configuracion.anchoPapel) {
            AnchoPapel.TERMICO_58MM -> PageSize(165f, 842f)
            AnchoPapel.TERMICO_80MM -> PageSize(227f, 842f)
            AnchoPapel.A4 -> PageSize.A4
        }

        // Crear documento
        val writer = PdfWriter(archivoDestino)
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc, pageSize)
        document.setMargins(20f, 10f, 20f, 10f)

        // Fuentes
        val fuenteNormal = PdfFontFactory.createFont(StandardFonts.HELVETICA)
        val fuenteNegrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)

        agregarLogo(document, empresa, configuracion)

        // ========== ENCABEZADO ==========

        // Nombre de empresa
        if (configuracion.mostrarNombreEmpresa) {
            document.add(
                Paragraph(empresa.nombreComercial ?: empresa.razonSocial)
                    .setFont(fuenteNegrita)
                    .setFontSize(configuracion.tamanoNombre.toFloat())
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        // RUC
        if (configuracion.mostrarRUC) {
            document.add(
                Paragraph("RUC: ${empresa.ruc}")
                    .setFont(fuenteNormal)
                    .setFontSize(9f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        // Dirección
        if (configuracion.mostrarDireccion) {
            document.add(
                Paragraph(empresa.direccion)
                    .setFont(fuenteNormal)
                    .setFontSize(9f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        // Teléfono
        if (configuracion.mostrarTelefono) {
            document.add(
                Paragraph("Tel: ${empresa.telefono}")
                    .setFont(fuenteNormal)
                    .setFontSize(9f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        // Email
        if (configuracion.mostrarEmail && !empresa.email.isNullOrEmpty()) {
            document.add(
                Paragraph(empresa.email)
                    .setFont(fuenteNormal)
                    .setFontSize(9f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        // Separador
        document.add(agregarSeparador())

        // ========== TIPO DE COMPROBANTE ==========
        document.add(
            Paragraph(tipoComprobante)
                .setFont(fuenteNegrita)
                .setFontSize(12f)
                .setTextAlignment(TextAlignment.CENTER)
        )

        document.add(
            Paragraph(numeroComprobante)
                .setFont(fuenteNegrita)
                .setFontSize(11f)
                .setTextAlignment(TextAlignment.CENTER)
        )

        document.add(agregarSeparador())

        // ========== FECHA Y HORA ==========
        val formatoFecha = when (configuracion.formatoFechaHora) {
            FormatoFechaHora.SOLO_FECHA -> SimpleDateFormat("dd/MM/yyyy", Locale("es", "PE"))
            FormatoFechaHora.SOLO_HORA -> SimpleDateFormat("HH:mm", Locale("es", "PE"))
            FormatoFechaHora.COMPLETO -> SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "PE"))
            FormatoFechaHora.COMPLETO_CON_SEGUNDOS -> SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("es", "PE"))
        }

        document.add(
            Paragraph("Fecha: ${formatoFecha.format(Date())}")
                .setFont(fuenteNormal)
                .setFontSize(9f)
        )

        // Vendedor
        if (configuracion.mostrarVendedor && !vendedor.isNullOrEmpty()) {
            document.add(
                Paragraph("Vendedor: $vendedor")
                    .setFont(fuenteNormal)
                    .setFontSize(9f)
            )
        }

        // Terminal
        if (configuracion.mostrarTerminal && !terminal.isNullOrEmpty()) {
            document.add(
                Paragraph("Terminal: $terminal")
                    .setFont(fuenteNormal)
                    .setFontSize(9f)
            )
        }

        // ========== DATOS DEL CLIENTE ==========
        document.add(
            Paragraph("Cliente: $cliente")
                .setFont(fuenteNormal)
                .setFontSize(9f)
        )

        // Si es FACTURA, mostrar más datos del cliente
        if (tipoComprobante == "FACTURA") {
            if (clienteDocumento.isNotEmpty()) {
                document.add(
                    Paragraph("RUC: $clienteDocumento")
                        .setFont(fuenteNormal)
                        .setFontSize(9f)
                )
            }
            if (clienteDireccion.isNotEmpty()) {
                document.add(
                    Paragraph("Dirección: $clienteDireccion")
                        .setFont(fuenteNormal)
                        .setFontSize(9f)
                )
            }
        }

        document.add(agregarSeparador())

        // ========== ITEMS ==========
        document.add(
            Paragraph("DETALLE")
                .setFont(fuenteNegrita)
                .setFontSize(10f)
        )

        // Tabla de items
        val numColumnas = if (configuracion.mostrarCodigo) 4 else 3
        val anchos = if (configuracion.mostrarCodigo)
            floatArrayOf(15f, 40f, 15f, 30f)
        else
            floatArrayOf(15f, 55f, 30f)

        val tablaItems = Table(UnitValue.createPercentArray(anchos))
        tablaItems.setWidth(UnitValue.createPercentValue(100f))

        // Encabezados
        if (configuracion.mostrarCodigo) {
            tablaItems.addHeaderCell(crearCeldaEncabezado("Cód.", fuenteNegrita))
        }
        tablaItems.addHeaderCell(crearCeldaEncabezado("Descripción", fuenteNegrita))
        tablaItems.addHeaderCell(crearCeldaEncabezado("Cant.", fuenteNegrita))
        tablaItems.addHeaderCell(crearCeldaEncabezado("Importe", fuenteNegrita))

        // Items
        items.forEach { item ->
            if (configuracion.mostrarCodigo) {
                tablaItems.addCell(crearCelda(item.codigo ?: "", fuenteNormal, 8f))
            }

            // Descripción
            var descripcion = item.nombre
            if (configuracion.mostrarDimensiones && item.ancho != null && item.alto != null) {
                descripcion += "\n${formatearDecimal(item.ancho)} x ${formatearDecimal(item.alto)}"
                if (!item.etiqueta.isNullOrEmpty()) {
                    descripcion += " - ${item.etiqueta}"
                }
            }

            tablaItems.addCell(crearCelda(descripcion, fuenteNormal, 8f))
            tablaItems.addCell(
                crearCelda("${formatearDecimal(item.cantidad)} ${item.unidad}", fuenteNormal, 8f, TextAlignment.CENTER)
            )
            // ✅ BIEN - Usa el precio directo (que ya es el total):
            tablaItems.addCell(
                crearCelda(formatoMoneda.format(item.precio), fuenteNormal, 8f, TextAlignment.RIGHT)
            )
        }

        document.add(tablaItems)
        document.add(agregarSeparador())

        // ========== TOTALES ==========

        // Subtotal
        if (configuracion.mostrarSubtotal && configuracion.desglosarIGV) {
            document.add(
                Paragraph("SUBTOTAL: ${formatoMoneda.format(subtotal)}")
                    .setFont(fuenteNormal)
                    .setFontSize(9f)
                    .setTextAlignment(TextAlignment.RIGHT)
            )
        }

        // IGV
        if (configuracion.mostrarIGV && configuracion.desglosarIGV) {
            document.add(
                Paragraph("IGV (18%): ${formatoMoneda.format(igv)}")
                    .setFont(fuenteNormal)
                    .setFontSize(9f)
                    .setTextAlignment(TextAlignment.RIGHT)
            )
        }

        // TOTAL
        document.add(
            Paragraph("TOTAL: ${formatoMoneda.format(total)}")
                .setFont(fuenteNegrita)
                .setFontSize(configuracion.tamanoTotal.toFloat())
                .setTextAlignment(TextAlignment.RIGHT)
        )

        // Forma de pago
        document.add(
            Paragraph("Forma de pago: $formaPago")
                .setFont(fuenteNormal)
                .setFontSize(9f)
                .setTextAlignment(TextAlignment.RIGHT)
        )

        document.add(agregarSeparador())

        // ========== PIE DE PÁGINA ==========

        // Mensaje de despedida
        if (configuracion.mostrarMensajeDespedida) {
            document.add(
                Paragraph(configuracion.mensajeDespedida)
                    .setFont(fuenteNormal)
                    .setFontSize(9f)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setItalic()
            )
        }

        // Texto personalizado
        if (configuracion.mostrarTextoPersonalizado && !configuracion.textoPersonalizado.isNullOrEmpty()) {
            document.add(
                Paragraph(configuracion.textoPersonalizado)
                    .setFont(fuenteNormal)
                    .setFontSize(8f)
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        // Cerrar documento
        document.close()

        return archivoDestino
    }

    // ========== FUNCIONES AUXILIARES ==========

    private fun agregarSeparador(): LineSeparator {
        return LineSeparator(SolidLine()).apply {
            setStrokeColor(ColorConstants.GRAY)
            setStrokeWidth(1f)
            setMarginTop(2f)
            setMarginBottom(2f)
        }
    }
    private fun agregarLogo(
        document: Document,
        empresa: DatosEmpresa,  // ← Cambiar parámetro
        configuracion: ConfiguracionTicket
    ) {
        if (!configuracion.mostrarLogo) {
            return
        }

        // ✅ AGREGAR ESTOS LOGS:
        Log.d("TicketGenerator", "=== AGREGAR LOGO ===")
        Log.d("TicketGenerator", "logoPath: ${empresa.logoPath}")
        Log.d("TicketGenerator", "logoUrl: ${empresa.logoUrl}")

        if (!empresa.logoPath.isNullOrEmpty()) {
            val archivo = File(empresa.logoPath)
            Log.d("TicketGenerator", "Archivo existe: ${archivo.exists()}")
            Log.d("TicketGenerator", "Usando archivo LOCAL")
        } else {
            Log.d("TicketGenerator", "logoPath vacío, descargando desde URL")
        }

        try {
            // Prioridad 1: Usar archivo local si existe
            val imageData = if (!empresa.logoPath.isNullOrEmpty() && File(empresa.logoPath).exists()) {
                ImageDataFactory.create(empresa.logoPath)  // ✅ Archivo local (RÁPIDO)
            } else if (!empresa.logoUrl.isNullOrEmpty()) {
                ImageDataFactory.create(URL(empresa.logoUrl))  // Fallback a URL (LENTO)
            } else {
                return
            }

            val logo = Image(imageData)

            val tamano = when (configuracion.tamanoLogo) {
                TamanoLogo.PEQUENO -> 40f
                TamanoLogo.MEDIANO -> 60f
                TamanoLogo.GRANDE -> 80f
            }
            logo.scaleToFit(tamano, tamano)

            val alineacion = when (configuracion.posicionLogo) {
                PosicionLogo.IZQUIERDA -> HorizontalAlignment.LEFT
                PosicionLogo.CENTRO -> HorizontalAlignment.CENTER
                PosicionLogo.DERECHA -> HorizontalAlignment.RIGHT
            }
            logo.setHorizontalAlignment(alineacion)

            document.add(logo)
            document.add(Paragraph().setMarginBottom(5f))

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun crearCeldaEncabezado(texto: String, fuente: com.itextpdf.kernel.font.PdfFont): Cell {
        return Cell()
            .add(Paragraph(texto).setFont(fuente).setFontSize(8f))
            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            .setTextAlignment(TextAlignment.CENTER)
            .setPadding(3f)
    }

    private fun crearCelda(
        texto: String,
        fuente: com.itextpdf.kernel.font.PdfFont,
        tamano: Float,
        alineacion: TextAlignment = TextAlignment.LEFT
    ): Cell {
        return Cell()
            .add(Paragraph(texto).setFont(fuente).setFontSize(tamano))
            .setTextAlignment(alineacion)
            .setPadding(2f)
    }

    private fun formatearDecimal(valor: Float): String {
        return if (valor % 1.0f == 0.0f) {
            valor.toInt().toString()
        } else {
            String.format("%.2f", valor)
        }
    }
}
