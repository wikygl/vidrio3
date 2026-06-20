package crystal.crystal

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ContratoActivity : AppCompatActivity() {

    private lateinit var etClienteNombre: EditText
    private lateinit var etClienteDocumento: EditText
    private lateinit var etClienteDireccion: EditText
    private lateinit var etClienteTelefono: EditText
    private lateinit var etUsuarioNombre: EditText
    private lateinit var etUsuarioDocumento: EditText
    private lateinit var etUsuarioDireccion: EditText
    private lateinit var etUsuarioTelefono: EditText
    private lateinit var etMontoRecibido: EditText
    private lateinit var etFechaSaldo: EditText
    private lateinit var reciboLayout: LinearLayout
    private lateinit var tvResumen: TextView
    private lateinit var tvContrato: TextView

    private val prefs by lazy { getSharedPreferences("ContratoPrefs", MODE_PRIVATE) }
    private var lista: List<Listado> = emptyList()
    private var total: String = "0.0"
    private var metros: String = "0.0"
    private var pies: String = "0.0"
    private var modoDocumento = ModoDocumento.CONTRATO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cargarExtras()
        construirVista()
        cargarDatosUsuario()
        generarDocumento()
    }

    @Suppress("DEPRECATION")
    private fun cargarExtras() {
        val recibida = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_LISTA, ArrayList::class.java)
        } else {
            intent.getSerializableExtra(EXTRA_LISTA) as? ArrayList<*>
        }
        lista = recibida?.filterIsInstance<Listado>().orEmpty()
        total = intent.getStringExtra(EXTRA_TOTAL).orEmpty().ifBlank { "0.0" }
        metros = intent.getStringExtra(EXTRA_METROS).orEmpty().ifBlank { "0.0" }
        pies = intent.getStringExtra(EXTRA_PIES).orEmpty().ifBlank { "0.0" }
    }

    private fun construirVista() {
        val root = ScrollView(this)
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(16), dp(16), dp(24))
        }
        root.addView(content)

        content.addView(titulo("Contrato de obra"))
        content.addView(filaModos())

        content.addView(seccion("Datos del cliente"))
        etClienteNombre = campo("Cliente", intent.getStringExtra(EXTRA_CLIENTE).orEmpty())
        etClienteDocumento = campo("DNI/RUC")
        etClienteDireccion = campo("Direccion")
        etClienteTelefono = campo("Telefono")
        content.addView(etClienteNombre)
        content.addView(etClienteDocumento)
        content.addView(etClienteDireccion)
        content.addView(etClienteTelefono)

        content.addView(seccion("Mis datos"))
        etUsuarioNombre = campo("Nombre o empresa")
        etUsuarioDocumento = campo("DNI/RUC")
        etUsuarioDireccion = campo("Direccion")
        etUsuarioTelefono = campo("Telefono")
        content.addView(etUsuarioNombre)
        content.addView(etUsuarioDocumento)
        content.addView(etUsuarioDireccion)
        content.addView(etUsuarioTelefono)

        reciboLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            visibility = View.GONE
        }
        reciboLayout.addView(seccion("Datos del recibo"))
        etMontoRecibido = campo("Monto recibido")
        etFechaSaldo = campo("Fecha para cancelar saldo")
        reciboLayout.addView(etMontoRecibido)
        reciboLayout.addView(etFechaSaldo)
        content.addView(reciboLayout)

        val filaBotones = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, dp(12))
        }
        filaBotones.addView(boton("Guardar mis datos") {
            guardarDatosUsuario()
        })
        filaBotones.addView(boton("Guardar documento") {
            guardarDocumentoActual()
        })
        filaBotones.addView(boton("PDF / compartir") {
            generarPdfYCompartir()
        })
        content.addView(filaBotones)

        content.addView(seccion("Resumen del presupuesto"))
        tvResumen = TextView(this).apply {
            textSize = 15f
            setPadding(0, dp(4), 0, dp(12))
        }
        content.addView(tvResumen)

        content.addView(seccion("Contrato"))
        tvContrato = TextView(this).apply {
            textSize = 15f
            setPadding(dp(12), dp(12), dp(12), dp(12))
            setTextColor(ContextCompat.getColor(this@ContratoActivity, android.R.color.black))
            setBackgroundColor(0xFFF5F5F5.toInt())
        }
        content.addView(tvContrato)

        setContentView(root)
    }

    private fun filaModos(): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(12))
            addView(boton("Contrato") {
                cambiarModo(ModoDocumento.CONTRATO)
            })
            addView(boton("Recibo") {
                cambiarModo(ModoDocumento.RECIBO)
            })
            addView(boton("Baul") {
                startActivity(Intent(this@ContratoActivity, BaulActivity::class.java))
            })
        }
    }

    private fun titulo(texto: String): TextView {
        return TextView(this).apply {
            text = texto
            textSize = 22f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, dp(14))
            setTextColor(ContextCompat.getColor(this@ContratoActivity, android.R.color.black))
        }
    }

    private fun seccion(texto: String): TextView {
        return TextView(this).apply {
            text = texto
            textSize = 17f
            setPadding(0, dp(12), 0, dp(6))
            setTextColor(ContextCompat.getColor(this@ContratoActivity, android.R.color.black))
        }
    }

    private fun campo(hint: String, valor: String = ""): EditText {
        return EditText(this).apply {
            setText(valor)
            this.hint = hint
            setSingleLine(true)
            textSize = 15f
        }
    }

    private fun boton(texto: String, onClick: (View) -> Unit): Button {
        return Button(this).apply {
            text = texto
            setOnClickListener(onClick)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(dp(4), 0, dp(4), 0)
            }
        }
    }

    private fun guardarDatosUsuario() {
        prefs.edit()
            .putString("usuario_nombre", etUsuarioNombre.text.toString())
            .putString("usuario_documento", etUsuarioDocumento.text.toString())
            .putString("usuario_direccion", etUsuarioDireccion.text.toString())
            .putString("usuario_telefono", etUsuarioTelefono.text.toString())
            .apply()
        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
        generarDocumento()
    }

    private fun cargarDatosUsuario() {
        etUsuarioNombre.setText(prefs.getString("usuario_nombre", "").orEmpty())
        etUsuarioDocumento.setText(prefs.getString("usuario_documento", "").orEmpty())
        etUsuarioDireccion.setText(prefs.getString("usuario_direccion", "").orEmpty())
        etUsuarioTelefono.setText(prefs.getString("usuario_telefono", "").orEmpty())
    }

    @SuppressLint("SetTextI18n")
    private fun generarDocumento() {
        val resumen = generarResumen()
        tvResumen.text = resumen
        tvContrato.text = when (modoDocumento) {
            ModoDocumento.CONTRATO -> generarTextoContrato(resumen)
            ModoDocumento.RECIBO -> generarTextoRecibo(resumen)
        }
    }

    private fun cambiarModo(modo: ModoDocumento) {
        modoDocumento = modo
        reciboLayout.visibility = if (modo == ModoDocumento.RECIBO) View.VISIBLE else View.GONE
        generarDocumento()
    }

    private fun guardarDocumentoActual() {
        generarDocumento()
        val prefijo = if (modoDocumento == ModoDocumento.RECIBO) "recibo" else "contrato"
        val cliente = etClienteNombre.text.toString().ifBlank { "sin_cliente" }
        val clienteArchivo = cliente.replace(Regex("[^A-Za-z0-9_-]"), "_")
        val fechaArchivo = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val archivo = File(filesDir, "${prefijo}_${clienteArchivo}_$fechaArchivo.txt")

        runCatching {
            archivo.writeText(tvContrato.text.toString())
        }.onSuccess {
            Toast.makeText(this, "Guardado en Baul: ${archivo.name}", Toast.LENGTH_LONG).show()
        }.onFailure {
            Toast.makeText(this, "No se pudo guardar: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generarPdfYCompartir() {
        generarDocumento()
        val prefijo = if (modoDocumento == ModoDocumento.RECIBO) "recibo" else "contrato"
        val cliente = etClienteNombre.text.toString().ifBlank { "sin_cliente" }
        val clienteArchivo = cliente.replace(Regex("[^A-Za-z0-9_-]"), "_")
        val fechaArchivo = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val pdfDir = File(cacheDir, "contratos_pdf").apply { mkdirs() }
        val pdfFile = File(pdfDir, "${prefijo}_${clienteArchivo}_$fechaArchivo.pdf")

        runCatching {
            escribirPdf(pdfFile, tituloDocumento(), tvContrato.text.toString())
        }.onSuccess {
            compartirPdf(pdfFile)
        }.onFailure {
            Toast.makeText(this, "No se pudo generar PDF: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun escribirPdf(file: File, titulo: String, texto: String) {
        val writer = PdfWriter(file)
        val pdfDoc = PdfDocument(writer)
        val document = Document(pdfDoc, PageSize.A4)
        document.setMargins(36f, 36f, 36f, 36f)

        val tituloFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        document.add(
            Paragraph(titulo)
                .setFont(tituloFont)
                .setFontSize(18f)
                .setTextAlignment(TextAlignment.CENTER)
        )

        texto.lines().forEach { linea ->
            document.add(
                Paragraph(linea.ifBlank { " " })
                    .setFontSize(11f)
            )
        }

        document.close()
    }

    private fun compartirPdf(file: File) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Compartir documento"))
    }

    private fun tituloDocumento(): String {
        return if (modoDocumento == ModoDocumento.RECIBO) {
            "Recibo de adelanto"
        } else {
            "Contrato de obra"
        }
    }

    private fun generarResumen(): String {
        val porProducto = lista
            .groupBy { it.producto.ifBlank { "Producto sin nombre" } }
            .mapValues { entry -> entry.value.sumOf { it.canti.toDouble() } }
            .toSortedMap()

        val categorias = lista
            .groupBy { categoriaProducto(it.producto) }
            .mapValues { entry -> entry.value.sumOf { it.canti.toDouble() } }
            .toSortedMap()

        return buildString {
            appendLine("Total productos: ${df1(lista.sumOf { it.canti.toDouble() }.toFloat())}")
            appendLine("Metros cuadrados: $metros")
            appendLine("Pies cuadrados: $pies")
            appendLine("Importe total: S/ $total")
            appendLine()
            appendLine("Por categoria:")
            categorias.forEach { (categoria, cantidad) ->
                appendLine("- $categoria: ${df1(cantidad.toFloat())}")
            }
            appendLine()
            appendLine("Detalle:")
            porProducto.forEach { (producto, cantidad) ->
                appendLine("- $producto: ${df1(cantidad.toFloat())}")
            }
        }
    }

    private fun generarTextoContrato(resumen: String): String {
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val cliente = etClienteNombre.text.toString().ifBlank { "________________" }
        val clienteDoc = etClienteDocumento.text.toString().ifBlank { "________________" }
        val clienteDir = etClienteDireccion.text.toString().ifBlank { "________________" }
        val clienteTel = etClienteTelefono.text.toString().ifBlank { "________________" }
        val usuario = etUsuarioNombre.text.toString().ifBlank { "________________" }
        val usuarioDoc = etUsuarioDocumento.text.toString().ifBlank { "________________" }
        val usuarioDir = etUsuarioDireccion.text.toString().ifBlank { "________________" }
        val usuarioTel = etUsuarioTelefono.text.toString().ifBlank { "________________" }

        return buildString {
            appendLine("CONTRATO DE FABRICACION E INSTALACION")
            appendLine()
            appendLine("Fecha: $fecha")
            appendLine()
            appendLine("Contratante: $cliente")
            appendLine("Documento: $clienteDoc")
            appendLine("Direccion: $clienteDir")
            appendLine("Telefono: $clienteTel")
            appendLine()
            appendLine("Contratista: $usuario")
            appendLine("Documento: $usuarioDoc")
            appendLine("Direccion: $usuarioDir")
            appendLine("Telefono: $usuarioTel")
            appendLine()
            appendLine("Objeto del contrato:")
            appendLine("El contratista se compromete a fabricar y/o instalar los productos detallados en el presupuesto generado en Crystal.")
            appendLine()
            appendLine("Resumen del presupuesto:")
            appendLine(resumen)
            appendLine()
            appendLine("Monto pactado: S/ $total")
            appendLine()
            appendLine("Condiciones:")
            appendLine("1. Las medidas y cantidades corresponden al presupuesto aceptado por el cliente.")
            appendLine("2. Cualquier cambio de medidas, modelo, color, accesorios o instalacion puede modificar el precio.")
            appendLine("3. Los plazos, adelantos y saldos se completaran segun acuerdo entre las partes.")
            appendLine()
            appendLine("Firma del cliente: ______________________________")
            appendLine()
            appendLine("Firma del contratista: __________________________")
        }
    }

    private fun generarTextoRecibo(resumen: String): String {
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val cliente = etClienteNombre.text.toString().ifBlank { "________________" }
        val clienteDoc = etClienteDocumento.text.toString().ifBlank { "________________" }
        val usuario = etUsuarioNombre.text.toString().ifBlank { "________________" }
        val usuarioDoc = etUsuarioDocumento.text.toString().ifBlank { "________________" }
        val recibido = parseImporte(etMontoRecibido.text.toString())
        val totalNumero = parseImporte(total)
        val saldo = (totalNumero - recibido).coerceAtLeast(0.0)
        val fechaSaldo = etFechaSaldo.text.toString().ifBlank { "________________" }

        return buildString {
            appendLine("RECIBO DE ADELANTO")
            appendLine()
            appendLine("Fecha: $fecha")
            appendLine()
            appendLine("Recibi de: $cliente")
            appendLine("Documento: $clienteDoc")
            appendLine()
            appendLine("La suma de: S/ ${df2(recibido)}")
            appendLine("Por concepto de adelanto por fabricacion, y/o instalacion de lo detallado en el presupuesto de Crystal.")
            appendLine()
            appendLine("Resumen del presupuesto:")
            appendLine(resumen)
            appendLine()
            appendLine("Monto total: S/ $total")
            appendLine("Monto recibido: S/ ${df2(recibido)}")
            appendLine("Saldo pendiente: S/ ${df2(saldo)}")
            appendLine("El saldo sera cancelado en fecha: $fechaSaldo")
            appendLine()
            appendLine("Recibido por: $usuario")
            appendLine("Documento: $usuarioDoc")
            appendLine()
            appendLine("Firma de quien entrega: _________________________")
            appendLine()
            appendLine("Firma de quien recibe: __________________________")
        }
    }

    private fun categoriaProducto(producto: String): String {
        val p = producto.lowercase(Locale.ROOT)
        return when {
            "vidrio" in p -> "Vidrios"
            "ventana" in p || "nova" in p || "vitro" in p -> "Ventanas"
            "puerta" in p -> "Puertas"
            "mampara" in p -> "Mamparas"
            "muro" in p -> "Muro cortina"
            "baranda" in p -> "Barandas"
            else -> "Otros"
        }
    }

    private fun df1(value: Float): String {
        val text = if ("$value".endsWith(".0")) {
            "$value".replace(".0", "")
        } else {
            "%.1f".format(Locale.US, value)
        }
        return text.replace(",", ".")
    }

    private fun df2(value: Double): String {
        return "%.2f".format(Locale.US, value)
    }

    private fun parseImporte(texto: String): Double {
        return texto
            .replace("S/", "")
            .replace("S", "")
            .replace(",", ".")
            .trim()
            .toDoubleOrNull() ?: 0.0
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    private enum class ModoDocumento {
        CONTRATO,
        RECIBO
    }

    companion object {
        const val EXTRA_CLIENTE = "contrato_cliente"
        const val EXTRA_LISTA = "contrato_lista"
        const val EXTRA_TOTAL = "contrato_total"
        const val EXTRA_METROS = "contrato_metros"
        const val EXTRA_PIES = "contrato_pies"
    }
}
