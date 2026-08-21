package crystal.crystal

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import java.io.ByteArrayOutputStream
import android.app.DatePickerDialog
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class ContratoActivity : AppCompatActivity() {

    // Datos del cliente (se editan por diálogo, no ocupan la pantalla).
    private var clNombre: String = ""
    private var clDoc: String = ""
    private var clDir: String = ""
    private var clTel: String = ""

    // Perfil de contratista seleccionado (se gestionan varios desde Ajustes).
    private var contratistaActual: DatosContratista? = null

    // Datos del recibo (se editan por diálogo desde Ajustes).
    private var montoRecibido: String = ""
    private var fechaSaldo: String = ""

    // Evidencia de aceptación (firma opcional + folio/hash).
    private var firmaBitmap: android.graphics.Bitmap? = null
    private var evidenciaActual: EvidenciaPresupuesto? = null

    // Selector de PDF para "Verificar PDF".
    private val seleccionarPdfVerificar =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            uri?.let { verificarPdf(it) }
        }
    private lateinit var chipCliente: TextView
    private lateinit var tvContrato: TextView
    private lateinit var tvCabeceraTitulo: TextView

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
        cargarContratistaInicial()
        intentarAutoCompletarCliente()
        actualizarChipCliente()
        generarDocumento()
        // Predictivo: si no llegó cliente, pedirlo de una vez.
        if (clNombre.isBlank()) chipCliente.post { dialogoCliente() }
    }

    @Suppress("DEPRECATION")
    private fun cargarExtras() {
        val recibida = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(EXTRA_LISTA, ArrayList::class.java)
        } else {
            intent.getSerializableExtra(EXTRA_LISTA) as? ArrayList<*>
        }
        lista = recibida?.filterIsInstance<Listado>().orEmpty()
        clNombre = intent.getStringExtra(EXTRA_CLIENTE).orEmpty()
        total = intent.getStringExtra(EXTRA_TOTAL).orEmpty().ifBlank { "0.0" }
        metros = intent.getStringExtra(EXTRA_METROS).orEmpty().ifBlank { "0.0" }
        pies = intent.getStringExtra(EXTRA_PIES).orEmpty().ifBlank { "0.0" }
    }

    private fun construirVista() {
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(ContextCompat.getColor(this@ContratoActivity, R.color.sombra))
        }
        contenedor.addView(cabecera())

        // Cliente: una sola línea; al tocarla se abre el diálogo de datos del cliente.
        chipCliente = TextView(this).apply {
            textSize = 15f
            setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.negro))
            background = ContextCompat.getDrawable(this@ContratoActivity, R.drawable.bg_control_panel)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(dp(12), dp(10), dp(12), dp(4)) }
            setOnClickListener { dialogoCliente() }
        }
        contenedor.addView(chipCliente)

        // Cuerpo: solo el texto del documento, ocupa el resto.
        val root = ScrollView(this).apply {
            isFillViewport = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
            )
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(4), dp(12), dp(12))
        }
        root.addView(content)

        val cardDoc = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = ContextCompat.getDrawable(this@ContratoActivity, R.drawable.bg_control_panel)
            setPadding(dp(16), dp(14), dp(16), dp(14))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        tvContrato = TextView(this).apply {
            textSize = 15f
            setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.negro))
        }
        cardDoc.addView(tvContrato)
        content.addView(cardDoc)
        contenedor.addView(root)

        // Acción primaria.
        val barraInferior = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(12), dp(6), dp(12), dp(10))
        }
        barraInferior.addView(boton("PDF / compartir") { generarPdfYCompartir() })
        contenedor.addView(barraInferior)

        setContentView(contenedor)
        actualizarChipCliente()
    }

    private fun actualizarChipCliente() {
        chipCliente.text = if (clNombre.isBlank()) {
            "Cliente: (toca para ingresar)"
        } else {
            "Cliente: $clNombre" + if (clDoc.isNotBlank()) "   ·   $clDoc" else ""
        }
    }

    // Si el nombre que llegó coincide con un cliente guardado, carga sus datos y firma.
    private fun intentarAutoCompletarCliente() {
        if (clNombre.isBlank()) return
        val c = ClienteContratoStore.listar(this).firstOrNull {
            it.nombre.equals(clNombre, ignoreCase = true) ||
                (clDoc.isNotBlank() && it.documento.equals(clDoc, ignoreCase = true))
        } ?: return
        if (clDoc.isBlank()) clDoc = c.documento
        if (clDir.isBlank()) clDir = c.direccion
        if (clTel.isBlank()) clTel = c.telefono
        if (firmaBitmap == null) firmaBitmap = ClienteContratoStore.base64ABitmap(c.firmaBase64)
    }

    // Barra de cabecera (azul de marca) con atrás, título y botón de ajustes.
    private fun cabecera(): LinearLayout {
        val barra = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(ContextCompat.getColor(this@ContratoActivity, R.color.color))
            setPadding(dp(6), dp(6), dp(6), dp(6))
            elevation = dp(4).toFloat()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(56)
            )
        }
        barra.addView(iconoBarra(R.drawable.ic_arrow_back) { finish() })
        tvCabeceraTitulo = TextView(this).apply {
            text = tituloDocumento()
            textSize = 19f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.blanco))
            setPadding(dp(8), 0, 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        barra.addView(tvCabeceraTitulo)
        barra.addView(iconoBarra(R.drawable.ajustes_c) { abrirAjustes() })
        return barra
    }

    private fun iconoBarra(res: Int, onClick: () -> Unit): ImageButton {
        return ImageButton(this).apply {
            setImageResource(res)
            setColorFilter(ContextCompat.getColor(this@ContratoActivity, R.color.blanco))
            background = null
            setPadding(dp(10), dp(10), dp(10), dp(10))
            layoutParams = LinearLayout.LayoutParams(dp(46), dp(46))
            setOnClickListener { onClick() }
        }
    }

    private fun abrirAjustes() {
        val cambioModo = if (modoDocumento == ModoDocumento.RECIBO) "Cambiar a Contrato" else "Cambiar a Recibo"
        val opciones = arrayOf(
            "Mis datos (contratista)",
            "Firma de aceptación",
            "Datos del recibo",
            cambioModo,
            "Marcar como aceptado",
            "Verificar PDF",
            "Respaldar evidencias",
            "Guardar como texto",
            "Abrir Baúl"
        )
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Ajustes")
            .setItems(opciones) { _, i ->
                when (i) {
                    0 -> mostrarPerfilesContratista()
                    1 -> mostrarDialogoFirma()
                    2 -> dialogoRecibo()
                    3 -> {
                        val nuevo = if (modoDocumento == ModoDocumento.RECIBO) ModoDocumento.CONTRATO else ModoDocumento.RECIBO
                        cambiarModo(nuevo)
                        if (nuevo == ModoDocumento.RECIBO && montoRecibido.isBlank()) dialogoRecibo()
                    }
                    4 -> marcarAceptado()
                    5 -> seleccionarPdfVerificar.launch(arrayOf("application/pdf"))
                    6 -> exportarEvidencias()
                    7 -> guardarDocumentoActual()
                    8 -> startActivity(Intent(this, BaulActivity::class.java))
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun dialogoRecibo() {
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(8))
        }
        val etMonto = campo("Monto recibido", montoRecibido.ifBlank { df2(parseImporte(total) / 2.0) })
        val etFecha = campo("Fecha para cancelar saldo", fechaSaldo)
        configurarSelectorFecha(etFecha)
        cont.addView(etMonto)
        cont.addView(etFecha)
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Datos del recibo")
            .setView(ScrollView(this).apply { addView(cont) })
            .setPositiveButton("Aceptar") { _, _ ->
                montoRecibido = etMonto.text.toString().trim()
                fechaSaldo = etFecha.text.toString().trim()
                if (modoDocumento != ModoDocumento.RECIBO) cambiarModo(ModoDocumento.RECIBO) else generarDocumento()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Firma del cliente en un diálogo (antes ocupaba espacio en el cuerpo). Se guarda en memoria
    // y se incrusta en el PDF al generar el documento.
    private fun mostrarDialogoFirma() {
        val firma = FirmaView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(180)
            )
            setBackgroundColor(0xFFEFEFEF.toInt())
        }
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(8), dp(16), dp(8))
            addView(TextView(this@ContratoActivity).apply {
                text = "Firme en el recuadro (opcional). Se incrustará en el PDF."
                setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.negro))
                setPadding(0, 0, 0, dp(6))
            })
            addView(firma)
            addView(boton("Limpiar") { firma.limpiar() }.apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { topMargin = dp(6) }
            })
            if (firmaBitmap != null) {
                addView(TextView(this@ContratoActivity).apply {
                    text = "Ya hay una firma guardada."
                    setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.verde))
                    setPadding(0, dp(6), 0, 0)
                })
            }
        }
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Firma de aceptación")
            .setView(ScrollView(this).apply { addView(cont) })
            .setPositiveButton("Guardar firma") { _, _ ->
                if (firma.tieneFirma()) {
                    firmaBitmap = firma.exportarBitmap()
                    Toast.makeText(this, "Firma guardada", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No se dibujó ninguna firma", Toast.LENGTH_SHORT).show()
                }
            }
            .setNeutralButton("Quitar firma") { _, _ ->
                firmaBitmap = null
                Toast.makeText(this, "Firma quitada", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun titulo(texto: String): TextView {
        return TextView(this).apply {
            text = texto
            textSize = 22f
            gravity = Gravity.CENTER
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setPadding(0, dp(4), 0, dp(14))
            setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.azul))
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

    // Tarjeta blanca redondeada con título, para agrupar cada sección.
    private fun tarjeta(tituloTexto: String): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            background = ContextCompat.getDrawable(this@ContratoActivity, R.drawable.bg_control_panel)
            elevation = dp(2).toFloat()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { setMargins(0, dp(6), 0, dp(6)) }
        }
        card.addView(TextView(this).apply {
            text = tituloTexto
            textSize = 16f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.azul))
            setPadding(0, 0, 0, dp(10))
        })
        return card
    }

    private fun campo(hint: String, valor: String = ""): EditText {
        return EditText(this).apply {
            setText(valor)
            this.hint = hint
            setSingleLine(true)
            textSize = 15f
            setPadding(dp(4), dp(10), dp(4), dp(10))
            setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.negro))
        }
    }

    // Convierte un EditText en selector de fecha: al tocarlo abre un calendario (no se teclea a mano).
    private fun configurarSelectorFecha(et: EditText) {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        et.isFocusable = false
        et.isClickable = true
        et.setOnClickListener {
            val cal = Calendar.getInstance()
            // Si ya hay una fecha escrita, preseleccionarla en el calendario.
            et.text?.toString()?.takeIf { it.isNotBlank() }?.let { txt ->
                runCatching { formato.parse(txt) }.getOrNull()?.let { cal.time = it }
            }
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    cal.set(year, month, day)
                    et.setText(formato.format(cal.time))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun boton(texto: String, onClick: (View) -> Unit): Button {
        return Button(this).apply {
            text = texto
            isAllCaps = false
            textSize = 13f
            setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.blanco))
            background = ContextCompat.getDrawable(this@ContratoActivity, R.drawable.fondo_boton_azul)
            setOnClickListener(onClick)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                setMargins(dp(4), dp(2), dp(4), dp(2))
            }
        }
    }

    // Sección de evidencia: firma opcional del cliente + QR de verificación + registro de aceptación.
    // Registro manual de aceptación (presencial / WhatsApp / llamada).
    private fun marcarAceptado() {
        val ev = evidenciaActual
        if (ev == null) {
            Toast.makeText(this, "Primero genera el documento (PDF)", Toast.LENGTH_SHORT).show()
            return
        }
        val canales = arrayOf("Presencial", "WhatsApp", "Llamada")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("¿Cómo aceptó el cliente?")
            .setItems(canales) { _, i ->
                val metodo = canales[i].uppercase(Locale.getDefault())
                runCatching { EvidenciaManager.marcarComoAceptado(this, ev, metodo) }
                    .onSuccess {
                        Toast.makeText(this, "Aceptación registrada ($metodo)", Toast.LENGTH_SHORT).show()
                        // Actualizar el respaldo en la nube con el estado ACEPTADO.
                        EvidenciaManager.subirEvidenciaFirestore(this, ev)
                    }
                    .onFailure {
                        Toast.makeText(this, "No se pudo registrar: ${it.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Verifica un PDF: recalcula su huella y la compara con las evidencias guardadas.
    private fun verificarPdf(uri: Uri) {
        runCatching {
            val hash = contentResolver.openInputStream(uri)?.use {
                EvidenciaManager.calcularHashStream(it)
            } ?: throw IllegalStateException("No se pudo leer el PDF")
            val ev = EvidenciaManager.buscarPorHash(this, hash)
            if (ev != null) {
                val aceptado = if (ev.estado == "ACEPTADO")
                    "\nAceptado: ${ev.fechaAceptacion} (${ev.metodoAceptacion})" else ""
                mostrarDialogo(
                    "✅ Documento íntegro",
                    "Coincide con una evidencia guardada:\n\n" +
                        "Folio: ${ev.idPresupuesto}\nCliente: ${ev.cliente}\n" +
                        "Total: S/ ${ev.total}\nGenerado: ${ev.fechaGeneracion}\n" +
                        "Estado: ${ev.estado}$aceptado"
                )
            } else {
                mostrarDialogo(
                    "❌ Sin coincidencia",
                    "Este PDF no coincide con ninguna evidencia guardada en este equipo. " +
                        "Pudo haber sido modificado, o no se generó aquí."
                )
            }
        }.onFailure {
            Toast.makeText(this, "No se pudo verificar: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Respalda todas las evidencias (JSON) en un .zip para compartir/guardar fuera del equipo.
    private fun exportarEvidencias() {
        val archivos = EvidenciaManager.archivosEvidencia(this)
        if (archivos.isEmpty()) {
            Toast.makeText(this, "No hay evidencias para respaldar", Toast.LENGTH_SHORT).show()
            return
        }
        runCatching {
            val dir = File(cacheDir, "respaldos").apply { mkdirs() }
            val sello = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val zip = File(dir, "evidencias_$sello.zip")
            ZipOutputStream(zip.outputStream()).use { zos ->
                archivos.forEach { f ->
                    zos.putNextEntry(ZipEntry(f.name))
                    f.inputStream().use { it.copyTo(zos) }
                    zos.closeEntry()
                }
            }
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", zip)
            startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "application/zip"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }, "Respaldar evidencias (${archivos.size})"))
        }.onFailure {
            Toast.makeText(this, "No se pudo respaldar: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarDialogo(titulo: String, mensaje: String) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensaje)
            .setPositiveButton("OK", null)
            .show()
    }

    // Carga el contratista predeterminado. Migra los datos antiguos (un solo perfil) si existían.
    private fun cargarContratistaInicial() {
        if (ContratistaStore.listar(this).isEmpty()) {
            val nombre = prefs.getString("usuario_nombre", "").orEmpty()
            if (nombre.isNotBlank()) {
                ContratistaStore.guardar(
                    this,
                    DatosContratista(
                        id = ContratistaStore.nuevoId(),
                        etiqueta = "Mis datos",
                        nombre = nombre,
                        documento = prefs.getString("usuario_documento", "").orEmpty(),
                        direccion = prefs.getString("usuario_direccion", "").orEmpty(),
                        telefono = prefs.getString("usuario_telefono", "").orEmpty(),
                        predeterminado = true
                    )
                )
            }
        }
        contratistaActual = ContratistaStore.predeterminado(this)
    }

    // ---- Diálogo de datos del cliente (se abre tocando el chip de cliente) ----
    private fun dialogoCliente() {
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(8))
        }
        val guardados = ClienteContratoStore.listar(this)
        val etN = AutoCompleteTextView(this).apply {
            setText(clNombre)
            hint = "Cliente"
            setSingleLine(true)
            textSize = 15f
            setPadding(dp(4), dp(10), dp(4), dp(10))
            setTextColor(ContextCompat.getColor(this@ContratoActivity, R.color.negro))
            threshold = 1
            setAdapter(ArrayAdapter(this@ContratoActivity, android.R.layout.simple_dropdown_item_1line, guardados.map { it.nombre }))
        }
        val etD = campo("DNI/RUC", clDoc)
        val etDir = campo("Dirección", clDir)
        val etT = campo("Teléfono", clTel)
        // Predictivo: al elegir un cliente sugerido, se completan documento, dirección, teléfono y firma.
        etN.setOnItemClickListener { _, _, pos, _ ->
            val nombre = etN.adapter.getItem(pos) as? String ?: return@setOnItemClickListener
            guardados.firstOrNull { it.nombre == nombre }?.let { c ->
                etD.setText(c.documento); etDir.setText(c.direccion); etT.setText(c.telefono)
                firmaBitmap = ClienteContratoStore.base64ABitmap(c.firmaBase64)
            }
        }
        listOf(etN, etD, etDir, etT).forEach { cont.addView(it) }
        val fila = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dp(8), 0, 0)
        }
        fila.addView(boton("Guardar") { guardarClienteDesde(etN, etD, etDir, etT) })
        fila.addView(boton("Cargar") { cargarClienteEnCampos(etN, etD, etDir, etT) })
        cont.addView(fila)
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Datos del cliente")
            .setView(ScrollView(this).apply { addView(cont) })
            .setPositiveButton("Aceptar") { _, _ ->
                clNombre = etN.text.toString().trim()
                clDoc = etD.text.toString().trim()
                clDir = etDir.text.toString().trim()
                clTel = etT.text.toString().trim()
                actualizarChipCliente()
                generarDocumento()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarClienteDesde(etN: EditText, etD: EditText, etDir: EditText, etT: EditText) {
        val nombre = etN.text.toString().trim()
        if (nombre.isBlank()) {
            Toast.makeText(this, "Ingresa al menos el nombre del cliente", Toast.LENGTH_SHORT).show()
            return
        }
        val cli = ClienteContrato(
            id = ClienteContratoStore.nuevoId(),
            nombre = nombre,
            documento = etD.text.toString().trim(),
            direccion = etDir.text.toString().trim(),
            telefono = etT.text.toString().trim(),
            firmaBase64 = ClienteContratoStore.bitmapABase64(firmaBitmap)
        )
        ClienteContratoStore.guardar(this, cli)
        val conFirma = if (cli.firmaBase64.isNotBlank()) " (con firma)" else ""
        Toast.makeText(this, "Cliente guardado$conFirma", Toast.LENGTH_SHORT).show()
    }

    private fun cargarClienteEnCampos(etN: EditText, etD: EditText, etDir: EditText, etT: EditText) {
        val clientes = ClienteContratoStore.listar(this)
        if (clientes.isEmpty()) {
            Toast.makeText(this, "No hay clientes guardados", Toast.LENGTH_SHORT).show()
            return
        }
        val etiquetas = clientes.map {
            it.nombre +
                (if (it.documento.isNotBlank()) " — ${it.documento}" else "") +
                (if (it.firmaBase64.isNotBlank()) "  ✔firma" else "")
        }.toTypedArray()
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Clientes guardados")
            .setItems(etiquetas) { _, i ->
                val c = clientes[i]
                val tieneFirma = c.firmaBase64.isNotBlank()
                val opciones = arrayOf(if (tieneFirma) "Cargar datos y firma" else "Cargar datos", "Eliminar")
                androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(c.nombre)
                    .setItems(opciones) { _, j ->
                        when (j) {
                            0 -> {
                                etN.setText(c.nombre); etD.setText(c.documento)
                                etDir.setText(c.direccion); etT.setText(c.telefono)
                                firmaBitmap = ClienteContratoStore.base64ABitmap(c.firmaBase64)
                                Toast.makeText(this, if (firmaBitmap != null) "Cliente y firma cargados" else "Cliente cargado", Toast.LENGTH_SHORT).show()
                            }
                            1 -> {
                                ClienteContratoStore.eliminar(this, c.id)
                                Toast.makeText(this, "Cliente eliminado", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    .show()
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    // ---- Gestión de perfiles de contratista (desde Ajustes) ----
    private fun mostrarPerfilesContratista() {
        val perfiles = ContratistaStore.listar(this)
        val etiquetas = perfiles.map {
            (if (it.predeterminado) "★ " else "") + it.etiqueta +
                (if (it.nombre.isNotBlank()) " — ${it.nombre}" else "")
        }.toTypedArray()
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Mis datos (contratista)")
            .setItems(etiquetas) { _, i -> accionesPerfil(perfiles[i]) }
            .setPositiveButton("Nuevo") { _, _ -> editarPerfil(null) }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    private fun accionesPerfil(p: DatosContratista) {
        val opciones = arrayOf("Usar en el documento", "Editar", "Establecer predeterminado", "Eliminar")
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(p.etiqueta)
            .setItems(opciones) { _, i ->
                when (i) {
                    0 -> { contratistaActual = p; generarDocumento(); Toast.makeText(this, "Usando: ${p.etiqueta}", Toast.LENGTH_SHORT).show() }
                    1 -> editarPerfil(p)
                    2 -> {
                        ContratistaStore.establecerPredeterminado(this, p.id)
                        contratistaActual = p; generarDocumento()
                        Toast.makeText(this, "Predeterminado: ${p.etiqueta}", Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        ContratistaStore.eliminar(this, p.id)
                        if (contratistaActual?.id == p.id) contratistaActual = ContratistaStore.predeterminado(this)
                        generarDocumento()
                        Toast.makeText(this, "Eliminado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    private fun editarPerfil(p: DatosContratista?) {
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(8))
        }
        val etEtiqueta = campo("Etiqueta (ej: Personal, Empresa)", p?.etiqueta ?: "")
        val etNombre = campo("Nombre o empresa", p?.nombre ?: "")
        val etDoc = campo("DNI/RUC", p?.documento ?: "")
        val etDir = campo("Dirección", p?.direccion ?: "")
        val etTel = campo("Teléfono", p?.telefono ?: "")
        listOf(etEtiqueta, etNombre, etDoc, etDir, etTel).forEach { cont.addView(it) }
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(if (p == null) "Nuevo contratista" else "Editar contratista")
            .setView(ScrollView(this).apply { addView(cont) })
            .setPositiveButton("Guardar") { _, _ ->
                val nuevo = DatosContratista(
                    id = p?.id ?: ContratistaStore.nuevoId(),
                    etiqueta = etEtiqueta.text.toString().ifBlank { "Sin nombre" },
                    nombre = etNombre.text.toString(),
                    documento = etDoc.text.toString(),
                    direccion = etDir.text.toString(),
                    telefono = etTel.text.toString(),
                    predeterminado = p?.predeterminado ?: false
                )
                ContratistaStore.guardar(this, nuevo)
                if (contratistaActual == null || contratistaActual?.id == nuevo.id) {
                    contratistaActual = nuevo
                    generarDocumento()
                }
                Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    @SuppressLint("SetTextI18n")
    private fun generarDocumento() {
        val resumen = generarResumen()
        tvContrato.text = when (modoDocumento) {
            ModoDocumento.CONTRATO -> generarTextoContrato(resumen)
            ModoDocumento.RECIBO -> generarTextoRecibo(resumen)
        }
    }

    private fun cambiarModo(modo: ModoDocumento) {
        modoDocumento = modo
        if (::tvCabeceraTitulo.isInitialized) tvCabeceraTitulo.text = tituloDocumento()
        generarDocumento()
    }

    private fun guardarDocumentoActual() {
        generarDocumento()
        val prefijo = if (modoDocumento == ModoDocumento.RECIBO) "recibo" else "contrato"
        val cliente = clNombre.ifBlank { "sin_cliente" }
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
        val cliente = clNombre.ifBlank { "sin_cliente" }
        val clienteArchivo = cliente.replace(Regex("[^A-Za-z0-9_-]"), "_")
        val fechaArchivo = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val pdfDir = File(cacheDir, "contratos_pdf").apply { mkdirs() }
        val pdfFile = File(pdfDir, "${prefijo}_${clienteArchivo}_$fechaArchivo.pdf")

        val folio = EvidenciaManager.generarIdPresupuesto()
        val firma = firmaBitmap

        runCatching {
            escribirPdf(pdfFile, tituloDocumento(), tvContrato.text.toString(), folio, firma)
        }.onSuccess {
            // Evidencia de aceptación: folio + hash del PDF + JSON + QR (mostrado, no incrustado).
            runCatching {
                val totalFloat = total.replace(",", ".").toFloatOrNull() ?: 0f
                val evidencia = EvidenciaManager.crearEvidenciaPresupuesto(
                    pdfFile, cliente, clTel, totalFloat, folio
                )
                EvidenciaManager.guardarEvidenciaJson(this, evidencia)
                evidenciaActual = evidencia
                // Respaldo en la nube (Firebase del usuario). Silencioso si no hay sesión.
                EvidenciaManager.subirEvidenciaFirestore(this, evidencia) { ok ->
                    if (ok) Toast.makeText(this, "Evidencia respaldada en la nube", Toast.LENGTH_SHORT).show()
                }
            }
            compartirPdf(pdfFile)
        }.onFailure {
            Toast.makeText(this, "No se pudo generar PDF: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun escribirPdf(file: File, titulo: String, texto: String, folio: String, firma: Bitmap?) {
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

        // Firma del cliente (opcional) incrustada como imagen.
        if (firma != null) {
            val baos = ByteArrayOutputStream()
            firma.compress(Bitmap.CompressFormat.PNG, 100, baos)
            document.add(Paragraph("Firma del cliente:").setFontSize(10f))
            document.add(Image(ImageDataFactory.create(baos.toByteArray())).setWidth(180f))
        }

        // Folio de evidencia (el hash y el QR se calculan/muestran aparte para no alterar el archivo).
        document.add(Paragraph(" "))
        document.add(Paragraph("Folio de evidencia: $folio").setFontSize(9f))

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

        return buildString {
            appendLine("Detalle:")
            porProducto.forEach { (producto, cantidad) ->
                appendLine("- $producto: ${df1(cantidad.toFloat())}")
            }
        }
    }

    private fun generarTextoContrato(resumen: String): String {
        val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val cliente = clNombre.ifBlank { "________________" }
        val clienteDoc = clDoc.ifBlank { "________________" }
        val clienteDir = clDir.ifBlank { "________________" }
        val clienteTel = clTel.ifBlank { "________________" }
        val c = contratistaActual
        val usuario = (c?.nombre).orEmpty().ifBlank { "________________" }
        val usuarioDoc = (c?.documento).orEmpty().ifBlank { "________________" }
        val usuarioDir = (c?.direccion).orEmpty().ifBlank { "________________" }
        val usuarioTel = (c?.telefono).orEmpty().ifBlank { "________________" }

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
            appendLine(resumen)
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
        val cliente = clNombre.ifBlank { "________________" }
        val clienteDoc = clDoc.ifBlank { "________________" }
        val usuario = (contratistaActual?.nombre).orEmpty().ifBlank { "________________" }
        val usuarioDoc = (contratistaActual?.documento).orEmpty().ifBlank { "________________" }
        val totalNumero = parseImporte(total)
        // Por defecto el adelanto es la mitad del total, hasta que se ingrese otro monto.
        val recibido = if (montoRecibido.isBlank()) totalNumero / 2.0 else parseImporte(montoRecibido)
        val saldo = (totalNumero - recibido).coerceAtLeast(0.0)
        val fechaSaldoTxt = fechaSaldo.ifBlank { "________________" }

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
            appendLine(resumen)
            appendLine("Monto total: S/ $total")
            appendLine("Monto recibido: S/ ${df2(recibido)}")
            appendLine("Saldo pendiente: S/ ${df2(saldo)}")
            appendLine("El saldo sera cancelado en fecha: $fechaSaldoTxt")
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
