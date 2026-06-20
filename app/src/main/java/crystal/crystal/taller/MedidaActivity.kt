package crystal.crystal.taller

import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import crystal.crystal.MainActivity
import crystal.crystal.databinding.ActivityMedidaBinding
import crystal.crystal.red.ListChatActivity
import crystal.crystal.red.interop.ChatInteropIntents
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MedidaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMedidaBinding
    private val formatoFecha = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    private var archivoActualTxt: File? = null
    private var herramientaActual = SketchMedidasView.Tool.NONE
    private var productoActual: String = ""
    private var bocetoActualDesdeArchivo: String? = null

    companion object {
        const val MIME_MEDIDAS_CRYSTAL = "application/vnd.crystal.medidas+json"
        const val EXTENSION_MEDIDAS_CRYSTAL = "crystalmedidas"
        const val EXTRA_PRODUCTO = "crystal.medida.producto"
        const val EXTRA_CLIENTE = "crystal.medida.cliente"
        const val EXTRA_MEDI1 = "crystal.medida.medi1"
        const val EXTRA_MEDI2 = "crystal.medida.medi2"
        const val EXTRA_MEDI3 = "crystal.medida.medi3"
        const val EXTRA_CANTIDAD = "crystal.medida.cantidad"
        const val EXTRA_UNIDAD = "crystal.medida.unidad"
        const val EXTRA_LISTA = "crystal.medida.lista"
        const val EXTRA_INDICE_INICIAL = "crystal.medida.indiceInicial"
    }


    private data class BocetoProyecto(
        val archivo: String,
        val producto: String,
        val descripcion: String,
        val fecha: Long
    )

    private data class PaginaPdfMedida(
        val producto: String,
        val descripcion: String,
        val bitmap: Bitmap
    )

    private val productosMedida = arrayOf(
        "Ventana",
        "Nova Corrediza",
        "Vitroven",
        "Ventana Aluminio",
        "Pivot Aluminio",
        "Puerta",
        "Puerta Ducha",
        "Mampara",
        "Mampara FC",
        "Mampara Paflon",
        "Mampara Vidrio",
        "Muro Cortina",
        "Division Bano",
        "Reja",
        "Baranda",
        "Curvo",
        "Otro"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarPantalla()
        configurarAcciones()
        manejarIntentEntrada(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        manejarIntentEntrada(intent)
    }

    private fun configurarPantalla() {
        supportActionBar?.title = "Medidas"
        binding.sketchMedidas.setBackgroundColor(0xFFF7F4EC.toInt())
        configurarPanelInformacion()
    }

    private fun configurarPanelInformacion() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                actualizarPanelInformacion()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        }
        binding.etClienteMedida.addTextChangedListener(watcher)
        binding.etInfoProductoMedida.addTextChangedListener(watcher)
        binding.etNotasMedida.addTextChangedListener(watcher)
        actualizarPanelInformacion()
    }

    private fun actualizarPanelInformacion() {
        val cliente = clienteActual().ifBlank { "sin cliente" }
        val productoBase = productoActual.ifBlank { "sin producto" }
        val infoProducto = binding.etInfoProductoMedida.text?.toString()?.trim().orEmpty()
        val producto = listOf(productoBase, infoProducto)
            .filter { it.isNotBlank() }
            .joinToString(" - ")
        val resumen = resumenNotasArchivadas(binding.etNotasMedida.text?.toString().orEmpty())

        binding.tvInfoClienteMedida.text = "Cliente: $cliente"
        binding.tvInfoProductoMedida.text = "Producto: $producto"
        binding.tvInfoArchivoMedida.text = "Medidas: $resumen"
    }

    private fun resumenNotasArchivadas(texto: String): String {
        val lineas = texto.lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toList()
        if (lineas.isEmpty()) return "sin archivar"
        val resumen = lineas.takeLast(4).joinToString("  |  ")
        return if (lineas.size > 4) "... $resumen" else resumen
    }

    private fun configurarAcciones() {
        binding.btnGuardarMedida.setOnClickListener { guardarProyecto() }
        binding.btnArchivarMedida.setOnClickListener { mostrarDialogoProductoParaArchivar() }
        binding.btnAbrirMedida.setOnClickListener { mostrarGuardados() }
        binding.btnEnviarMedida.setOnClickListener { enviarAMainActivity() }
        binding.btnLimpiarMedida.setOnClickListener { confirmarLimpiar() }
        binding.btnDeshacerMedida.setOnClickListener { binding.sketchMedidas.undo() }
        binding.btnZoomMenosMedida.setOnClickListener { binding.sketchMedidas.zoomOut() }
        binding.btnZoomMasMedida.setOnClickListener { binding.sketchMedidas.zoomIn() }
        binding.btnAjustarMedida.setOnClickListener { binding.sketchMedidas.fitContentInView() }
        binding.btnPdfMedida.setOnClickListener { generarYCompartirPdfMedida() }
        binding.btProject.setOnClickListener { togglePanelProyecto() }
        binding.btArchivar.setOnClickListener { togglePanelArchivo() }
        binding.btFormas.setOnClickListener { togglePanelFormas() }
        binding.btEngra.setOnClickListener { togglePanelEngra() }
        binding.btPlantillas.setOnClickListener { togglePanelPlantillas() }
        binding.btEdicion.setOnClickListener { togglePanelOperaciones() }
        binding.btSelec.setOnClickListener {
            ocultarPanelesFlotantes()
            seleccionarHerramienta(SketchMedidasView.Tool.SELECT)
        }
        binding.btEliminar.setOnClickListener {
            ocultarPanelesFlotantes()
            eliminarSeleccion()
        }
        binding.btnAjusteVistaPanel.setOnClickListener { binding.sketchMedidas.fitContentInView() }
        binding.btnAtrasMedida.setOnClickListener { binding.sketchMedidas.undo() }
        binding.btnAdelanteMedida.setOnClickListener { binding.sketchMedidas.redo() }
        binding.btnZoomMasPanel.setOnClickListener { binding.sketchMedidas.zoomIn() }
        binding.btnZoomMenosPanel.setOnClickListener { binding.sketchMedidas.zoomOut() }
        binding.btnMoverMedida.setOnClickListener { seleccionarHerramientaDesdePanelAjustes(SketchMedidasView.Tool.NONE) }
        binding.btnNuloMedida.setOnClickListener { seleccionarHerramientaDesdePanelAjustes(SketchMedidasView.Tool.NONE) }
        binding.btnLimpiaMedida.setOnClickListener {
            confirmarLimpiar()
        }
        binding.btnArchivoArchivar.setOnClickListener {
            ocultarPanelesFlotantes()
            mostrarDialogoProductoParaArchivar()
        }
        binding.btnArchivoAbrir.setOnClickListener {
            ocultarPanelesFlotantes()
            mostrarGuardados()
        }
        binding.btnArchivoVerBocetos.setOnClickListener {
            ocultarPanelesFlotantes()
            mostrarSelectorBocetosProyectoActual()
        }
        binding.btnArchivoLimpiarLienzo.setOnClickListener {
            ocultarPanelesFlotantes()
            confirmarLimpiarLienzoActual()
        }
        binding.btnArchivoCompartir.setOnClickListener {
            ocultarPanelesFlotantes()
            mostrarOpcionesCompartirMedida()
        }
        binding.btnArchivoCalcul.setOnClickListener {
            ocultarPanelesFlotantes()
            enviarAMainActivity()
        }
        binding.btnHerramientaLapiz.setOnClickListener {
            seleccionarHerramienta(SketchMedidasView.Tool.FREEHAND)
            ocultarPanelesFlotantes()
        }
        binding.btnHerramientaFormas.setOnClickListener { togglePanelFormas() }
        binding.btnFormaLapiz.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.FREEHAND) }
        binding.btnFormaRectangulo.setOnClickListener {
            binding.sketchMedidas.clearRectangleRoundedCorner()
            seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.RECTANGLE)
        }
        binding.btnFormaTriangulo.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.TRIANGLE) }
        binding.btnFormaCirculo.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.CIRCLE) }
        binding.btnFormaTexto.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.TEXT) }
        binding.btnFormaLinea.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.LINE) }
        binding.btnFormaLinea90.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.ORTHO_LINE) }
        binding.btnEngraBisagra.setOnClickListener { mostrarDialogoBisagra() }
        binding.btnEngraAdentro.setOnClickListener { insertarSimboloCentro("adentro") }
        binding.btnEngraAfuera.setOnClickListener { insertarSimboloCentro("afuera") }
        binding.btnEngraMichi.setOnClickListener { mostrarDialogoMichi() }
        binding.btnEngraInterior.setOnClickListener { insertarInteriorExterior("interior") }
        binding.btnEngraExterior.setOnClickListener { insertarInteriorExterior("exterior") }
        binding.btnFuncionVano.setOnClickListener { mostrarDialogoPlantillaVano() }
        binding.btnHerramientaSeleccion.setOnClickListener {
            seleccionarHerramienta(SketchMedidasView.Tool.SELECT)
            ocultarPanelesFlotantes()
        }
        binding.btnSoldarMedida.setOnClickListener { soldarSeleccion() }
        binding.btnRestarMedida.setOnClickListener { cortarSeleccion() }
        binding.btnSoldarPanelMedida.setOnClickListener { soldarSeleccion() }
        binding.btnCortarPanelMedida.setOnClickListener { cortarSeleccion() }
        binding.btnEspejoPanelMedida.setOnClickListener { copiarEspejoSeleccion() }
        binding.btnDuplicarPanelMedida.setOnClickListener { duplicarSeleccion() }
        binding.btnRotarPanelMedida.setOnClickListener { rotarSeleccion() }
        binding.btnEscalaPanelMedida.setOnClickListener { mostrarDialogoEscalaSeleccion() }
        binding.btnCompasPanelMedida.setOnClickListener { mostrarDialogoCompas() }
        binding.btnAgruparPanelMedida.setOnClickListener { agruparSeleccion() }
        binding.btnDesagruparPanelMedida.setOnClickListener { desagruparSeleccion() }
        binding.btnTabFormasBasicas.setOnClickListener { seleccionarTabFormas(true) }
        binding.btnTabFormasRecurrentes.setOnClickListener { seleccionarTabFormas(false) }
        binding.btnPlantillaPuerta.setOnClickListener {
            mostrarDialogoPlantilla("Puerta", incluyeBisagra = true, incluyeApertura = true, incluyeVista = true)
        }
        binding.btnPlantillaVentana.setOnClickListener {
            mostrarDialogoPlantilla("Ventana", incluyeBisagra = true, incluyeApertura = true, incluyeVista = true)
        }
        binding.btnPlantillaMampara.setOnClickListener {
            mostrarDialogoPlantilla("Mampara", incluyeBisagra = false, incluyeApertura = false, incluyeVista = true, hojasPorDefecto = 2)
        }
        binding.btnPlantillaCorrediza.setOnClickListener {
            mostrarDialogoPlantilla("Corrediza", incluyeBisagra = false, incluyeApertura = false, incluyeVista = true, hojasPorDefecto = 2)
        }
        binding.btnPlantillaDucha.setOnClickListener {
            mostrarDialogoPlantilla("Puerta Ducha", incluyeBisagra = true, incluyeApertura = true, incluyeVista = false)
        }
        binding.btnPlantillaBaranda.setOnClickListener {
            mostrarDialogoPlantilla("Baranda", incluyeBisagra = false, incluyeApertura = false, incluyeVista = false)
        }
        binding.btnPlantillaReja.setOnClickListener {
            mostrarDialogoPlantilla("Reja", incluyeBisagra = false, incluyeApertura = false, incluyeVista = false)
        }
        binding.btnPlantillaMuro.setOnClickListener {
            mostrarDialogoPlantilla("Muro Cortina", incluyeBisagra = false, incluyeApertura = false, incluyeVista = true, hojasPorDefecto = 3)
        }
        binding.btnRecurrenteF1.setOnClickListener {
            ocultarPanelesFlotantes()
            binding.sketchMedidas.insertarRecurrenteF1()
        }
        binding.btnRecurrenteF2.setOnClickListener {
            ocultarPanelesFlotantes()
            binding.sketchMedidas.insertarRecurrenteF2()
        }
        binding.btnRecurrenteF3.setOnClickListener {
            ocultarPanelesFlotantes()
            binding.sketchMedidas.insertarRecurrenteF3()
        }
        binding.btnRecurrenteF4.setOnClickListener { mostrarOpcionesRecurrenteF4() }
        binding.btnRecurrenteF5.setOnClickListener {
            ocultarPanelesFlotantes()
            binding.sketchMedidas.insertarRecurrenteF5()
        }
        binding.btnRecurrenteF6.setOnClickListener { mostrarOpcionesRecurrenteF6() }
        seleccionarHerramienta(herramientaActual)
    }

    private fun togglePanelProyecto() {
        val mostrar = binding.panelSuperiorMedida.visibility != View.VISIBLE
        ocultarPanelesFlotantes()
        binding.panelSuperiorMedida.visibility = if (mostrar) View.VISIBLE else View.GONE
        if (mostrar) binding.etClienteMedida.requestFocus()
    }

    private fun togglePanelFormas() {
        val mostrar = binding.panelFormasMedida.visibility != View.VISIBLE
        ocultarPanelesFlotantes()
        binding.panelFormasMedida.visibility = if (mostrar) View.VISIBLE else View.GONE
        if (mostrar) seleccionarTabFormas(true)
    }

    private fun seleccionarTabFormas(basicas: Boolean) {
        binding.contenidoFormasBasicas.visibility = if (basicas) View.VISIBLE else View.GONE
        binding.contenidoFormasRecurrentes.visibility = if (basicas) View.GONE else View.VISIBLE
        binding.btnTabFormasBasicas.isSelected = basicas
        binding.btnTabFormasRecurrentes.isSelected = !basicas
        binding.btnTabFormasBasicas.alpha = if (basicas) 1f else 0.65f
        binding.btnTabFormasRecurrentes.alpha = if (basicas) 0.65f else 1f
    }

    private fun togglePanelEngra() {
        val mostrar = binding.panelEngraMedida.visibility != View.VISIBLE
        ocultarPanelesFlotantes()
        binding.panelEngraMedida.visibility = if (mostrar) View.VISIBLE else View.GONE
    }

    private fun togglePanelArchivo() {
        val mostrar = binding.panelArchivoMedida.visibility != View.VISIBLE
        ocultarPanelesFlotantes()
        binding.panelArchivoMedida.visibility = if (mostrar) View.VISIBLE else View.GONE
    }

    private fun togglePanelOperaciones() {
        val mostrar = binding.panelOperacionesMedida.visibility != View.VISIBLE
        ocultarPanelesFlotantes()
        binding.panelOperacionesMedida.visibility = if (mostrar) View.VISIBLE else View.GONE
    }

    private fun togglePanelPlantillas() {
        val mostrar = binding.panelPlantillasMedida.visibility != View.VISIBLE
        ocultarPanelesFlotantes()
        binding.panelPlantillasMedida.visibility = if (mostrar) View.VISIBLE else View.GONE
    }

    private fun ocultarPanelesFlotantes() {
        binding.panelSuperiorMedida.visibility = View.GONE
        binding.panelFormasMedida.visibility = View.GONE
        binding.panelEngraMedida.visibility = View.GONE
        binding.panelArchivoMedida.visibility = View.GONE
        binding.panelOperacionesMedida.visibility = View.GONE
        binding.panelPlantillasMedida.visibility = View.GONE
    }

    private fun seleccionarHerramientaDesdePanel(tool: SketchMedidasView.Tool) {
        seleccionarHerramienta(tool)
        ocultarPanelesFlotantes()
    }

    private fun seleccionarHerramientaDesdePanelAjustes(tool: SketchMedidasView.Tool) {
        seleccionarHerramienta(tool)
    }

    private fun insertarSimboloEngra(drawableName: String) {
        ocultarPanelesFlotantes()
        binding.sketchMedidas.insertarSimbolo(drawableName)
    }

    private fun insertarSimboloCentro(drawableName: String) {
        ocultarPanelesFlotantes()
        binding.sketchMedidas.insertarSimboloCentroDiseno(drawableName)
    }

    private fun insertarInteriorExterior(drawableName: String) {
        ocultarPanelesFlotantes()
        binding.sketchMedidas.insertarSimboloInteriorExterior(drawableName)
    }

    private fun mostrarDialogoMichi() {
        ocultarPanelesFlotantes()
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setSingleLine(true)
            hint = "Valor"
        }
        AlertDialog.Builder(this)
            .setTitle("Michi")
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                val valor = input.text?.toString()?.trim().orEmpty()
                if (valor.isNotBlank()) {
                    binding.sketchMedidas.insertarMichi(valor)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoBisagra() {
        ocultarPanelesFlotantes()
        val opciones = arrayOf("Izquierda", "Derecha")
        AlertDialog.Builder(this)
            .setTitle("Bisagra")
            .setItems(opciones) { _, which ->
                binding.sketchMedidas.insertarBisagras(izquierda = which == 0)
            }
            .show()
    }

    private fun mostrarDialogoPlantillaVano() {
        ocultarPanelesFlotantes()
        val opciones = arrayOf("Puerta / ventana", "Gradería")
        AlertDialog.Builder(this)
            .setTitle("Diseño exacto")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> binding.sketchMedidas.insertarPlantillaVanoExacto()
                    1 -> mostrarDialogoGraderia()
                }
            }
            .show()
    }

    private fun mostrarDialogoGraderia() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 12, 36, 0)
        }
        val etLargo = EditText(this).apply {
            hint = "Largo total cm"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setSingleLine(true)
        }
        val etPasos = EditText(this).apply {
            hint = "Cantidad de pasos"
            inputType = InputType.TYPE_CLASS_NUMBER
            setSingleLine(true)
        }
        layout.addView(etLargo)
        layout.addView(etPasos)

        AlertDialog.Builder(this)
            .setTitle("Gradería")
            .setView(layout)
            .setPositiveButton("Crear") { _, _ ->
                val largo = etLargo.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                val pasosIngresados = etPasos.text?.toString()?.trim()?.toIntOrNull()?.coerceAtLeast(1)
                val pasos = pasosIngresados
                    ?: largo?.let { kotlin.math.ceil((it / 30f).toDouble()).toInt().coerceAtLeast(1) }
                    ?: 3
                val alturaTotal = pasos * 18f
                val paso = when {
                    largo != null && largo > alturaTotal -> {
                        val avanceTotal = kotlin.math.sqrt(
                            ((largo * largo) - (alturaTotal * alturaTotal)).toDouble()
                        ).toFloat()
                        (avanceTotal / pasos).coerceAtLeast(1f)
                    }
                    largo != null -> {
                        mostrar("El largo diagonal debe ser mayor a ${formatoMedida(alturaTotal)}")
                        30f
                    }
                    pasosIngresados != null -> 30f
                    else -> 30f
                }
                binding.sketchMedidas.insertarPlantillaGraderia(
                    pasos = pasos,
                    pasoCm = paso,
                    contrapasoCm = 18f
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoPlantilla(
        producto: String,
        incluyeBisagra: Boolean,
        incluyeApertura: Boolean,
        incluyeVista: Boolean,
        hojasPorDefecto: Int = 1,
        anchoCmInicial: String? = null,
        altoCmInicial: String? = null
    ) {
        ocultarPanelesFlotantes()
        val densidad = resources.displayMetrics.density
        val pad = (16 * densidad).toInt()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad / 2, pad, 0)
        }

        fun crearEditMedida(textoHint: String, valorDefecto: String): EditText {
            return EditText(this).apply {
                hint = textoHint
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                setSingleLine(true)
                setText(valorDefecto)
                setSelectAllOnFocus(true)
            }
        }

        val (defAncho, defAlto) = medidasPorDefecto(producto)
        val etAncho = crearEditMedida("Ancho cm", anchoCmInicial?.takeIf { it.isNotBlank() } ?: defAncho)
        val etAlto = crearEditMedida("Alto cm", altoCmInicial?.takeIf { it.isNotBlank() } ?: defAlto)
        val etHojas = EditText(this).apply {
            hint = "Hojas (1-6)"
            inputType = InputType.TYPE_CLASS_NUMBER
            setSingleLine(true)
            setText(hojasPorDefecto.toString())
        }

        layout.addView(etAncho)
        layout.addView(etAlto)
        layout.addView(etHojas)

        val spBisagra = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MedidaActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("Sin bisagra", "Izquierda", "Derecha")
            )
        }
        if (incluyeBisagra) {
            layout.addView(crearEtiqueta("Bisagra"))
            layout.addView(spBisagra)
        }

        val spApertura = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MedidaActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("Sin apertura", "Adentro", "Afuera")
            )
        }
        if (incluyeApertura) {
            layout.addView(crearEtiqueta("Apertura"))
            layout.addView(spApertura)
        }

        val spVista = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MedidaActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("Sin vista", "Interior", "Exterior")
            )
        }
        if (incluyeVista) {
            layout.addView(crearEtiqueta("Vista"))
            layout.addView(spVista)
        }

        AlertDialog.Builder(this)
            .setTitle("Plantilla: $producto")
            .setView(layout)
            .setPositiveButton("Insertar") { _, _ ->
                val ancho = etAncho.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                val alto = etAlto.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                if (ancho == null || alto == null || ancho <= 0f || alto <= 0f) {
                    mostrar("Ingrese ancho y alto válidos")
                    return@setPositiveButton
                }
                val hojas = etHojas.text?.toString()?.toIntOrNull()?.coerceIn(1, 6) ?: 1
                val bisagra = if (incluyeBisagra) {
                    when (spBisagra.selectedItemPosition) {
                        1 -> "izquierda"
                        2 -> "derecha"
                        else -> null
                    }
                } else null
                val apertura = if (incluyeApertura) {
                    when (spApertura.selectedItemPosition) {
                        1 -> "adentro"
                        2 -> "afuera"
                        else -> null
                    }
                } else null
                val vista = if (incluyeVista) {
                    when (spVista.selectedItemPosition) {
                        1 -> "interior"
                        2 -> "exterior"
                        else -> null
                    }
                } else null
                val etiqueta = buildString {
                    append(producto.uppercase())
                    append("  ")
                    append(formatoMedida(ancho))
                    append(" x ")
                    append(formatoMedida(alto))
                    if (hojas > 1) append("  (${hojas} hojas)")
                }
                binding.sketchMedidas.insertarPlantillaProducto(
                    anchoCm = ancho,
                    altoCm = alto,
                    hojas = hojas,
                    bisagra = bisagra,
                    vista = vista,
                    apertura = apertura,
                    etiqueta = etiqueta
                )
                productoActual = producto
                actualizarPanelInformacion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun crearEtiqueta(texto: String): TextView {
        val densidad = resources.displayMetrics.density
        return TextView(this).apply {
            text = texto
            textSize = 12f
            setPadding(0, (6 * densidad).toInt(), 0, 0)
        }
    }

    private fun aplicarMedidaEntrante(
        producto: String,
        cliente: String,
        medi1: Float,
        medi2: Float,
        medi3: Float,
        cantidad: Float,
        unidad: String
    ) {
        if (cliente.isNotBlank()) {
            binding.etClienteMedida.setText(cliente)
        }
        val factor = factorConversionAUnidadCm(unidad)
        val anchoCm = if (medi1 > 0f) medi1 * factor else 0f
        val altoCm = if (medi2 > 0f) medi2 * factor else 0f

        val (tipo, incluyeBisagra, incluyeApertura, incluyeVista, hojasDef) = mapearProductoPlantilla(producto)
        if (tipo.isBlank()) {
            productoActual = producto.ifBlank { productoActual }
            actualizarPanelInformacion()
            mostrar("Producto sin plantilla: ${producto.ifBlank { "?" }}")
            return
        }

        productoActual = producto
        actualizarPanelInformacion()

        val anchoStr = if (anchoCm > 0f) formatoMedidaPlano(anchoCm) else null
        val altoStr = if (altoCm > 0f) formatoMedidaPlano(altoCm) else null
        mostrarDialogoPlantilla(
            producto = tipo,
            incluyeBisagra = incluyeBisagra,
            incluyeApertura = incluyeApertura,
            incluyeVista = incluyeVista,
            hojasPorDefecto = hojasDef,
            anchoCmInicial = anchoStr,
            altoCmInicial = altoStr
        )
    }

    private fun procesarListaPendientes(
        listaItems: List<crystal.crystal.Listado>,
        cliente: String
    ) {
        if (cliente.isNotBlank()) {
            binding.etClienteMedida.setText(cliente)
        }
        val clienteNombre = cliente.ifBlank { clienteActual() }.ifBlank { "cliente" }
        val dir = obtenerDirectorioMedidas()
        val baseName = "${formatoFecha.format(Date())}_${sanitizarNombre(clienteNombre)}"
        val txtFile = File(dir, "$baseName.txt")

        val bocetos = mutableListOf<BocetoProyecto>()
        var saltados = 0
        listaItems.forEachIndexed { index, item ->
            val cfg = mapearProductoPlantilla(item.producto)
            if (cfg.tipo.isBlank()) {
                saltados++
                return@forEachIndexed
            }
            val factor = factorConversionAUnidadCm(item.uni)
            val defAnchoAlto = medidasPorDefecto(cfg.tipo.lowercase())
            val anchoCm = if (item.medi1 > 0f) item.medi1 * factor else defAnchoAlto.first.toFloat()
            val altoCm = if (item.medi2 > 0f) item.medi2 * factor else defAnchoAlto.second.toFloat()

            binding.sketchMedidas.clear()
            val etiqueta = buildString {
                append(cfg.tipo.uppercase())
                append("  ")
                append(formatoMedida(anchoCm))
                append(" x ")
                append(formatoMedida(altoCm))
                if (cfg.hojasDef > 1) append("  (${cfg.hojasDef} hojas)")
            }
            binding.sketchMedidas.insertarPlantillaProducto(
                anchoCm = anchoCm,
                altoCm = altoCm,
                hojas = cfg.hojasDef,
                bisagra = null,
                vista = null,
                apertura = null,
                etiqueta = etiqueta
            )

            val sketchName = "${baseName}_${index + 1}_${System.currentTimeMillis()}.json"
            runCatching {
                File(dir, sketchName).writeText(binding.sketchMedidas.exportEditableState())
                bocetos.add(
                    BocetoProyecto(
                        archivo = sketchName,
                        producto = item.producto,
                        descripcion = etiqueta,
                        fecha = System.currentTimeMillis()
                    )
                )
            }.onFailure { saltados++ }
        }

        binding.sketchMedidas.clear()
        if (bocetos.isEmpty()) {
            mostrar("Ninguna medida pudo generarse")
            return
        }

        guardarIndiceBocetos(dir, baseName, bocetos)
        val notas = listaItems.joinToString("\n") { "${it.producto} - ${construirResumenMedida(it)}" }
        val productoInicial = bocetos.first().producto
        txtFile.writeText(
            construirTextoArchivo(
                cliente = clienteNombre,
                producto = productoInicial,
                notas = notas,
                bocetoName = bocetos.first().archivo,
                indiceBocetosName = nombreIndiceBocetos(baseName)
            )
        )
        archivoActualTxt = txtFile
        productoActual = productoInicial
        binding.etNotasMedida.setText(notas)
        cargarBocetoEnLienzo(bocetos.first(), dir)
        actualizarPanelInformacion()

        val mensaje = if (saltados > 0) {
            "${bocetos.size} medidas listas. $saltados sin plantilla. Abre la lista para ver cada una."
        } else {
            "${bocetos.size} medidas listas. Abre la lista para ver cada una."
        }
        mostrar(mensaje)
    }

    private fun factorConversionAUnidadCm(unidad: String): Float = when (unidad.lowercase()) {
        "p2", "pies" -> 30.48f
        "m2", "m", "ml", "m3", "metros" -> 100f
        else -> 1f
    }

    private fun construirResumenMedida(item: crystal.crystal.Listado): String {
        val partes = mutableListOf<String>()
        if (item.medi1 > 0f) partes.add(formatoMedidaPlano(item.medi1))
        if (item.medi2 > 0f) partes.add(formatoMedidaPlano(item.medi2))
        if (item.medi3 > 0f) partes.add(formatoMedidaPlano(item.medi3))
        val medidasTxt = partes.joinToString(" x ").ifBlank { "sin medidas" }
        return "$medidasTxt ${item.uni}"
    }

    private data class PlantillaCfg(
        val tipo: String,
        val incluyeBisagra: Boolean,
        val incluyeApertura: Boolean,
        val incluyeVista: Boolean,
        val hojasDef: Int
    )

    private fun mapearProductoPlantilla(producto: String): PlantillaCfg {
        val p = producto.lowercase().trim()
        return when {
            p.contains("ducha") -> PlantillaCfg("Puerta Ducha", true, true, false, 1)
            p.contains("puerta") -> PlantillaCfg("Puerta", true, true, true, 1)
            p.contains("nova") || p.contains("corrediza") -> PlantillaCfg("Corrediza", false, false, true, 2)
            p.contains("mampara") || p.contains("division bano") || p.contains("división baño") -> PlantillaCfg("Mampara", false, false, true, 2)
            p.contains("muro") -> PlantillaCfg("Muro Cortina", false, false, true, 3)
            p.contains("baranda") -> PlantillaCfg("Baranda", false, false, false, 1)
            p.contains("reja") -> PlantillaCfg("Reja", false, false, false, 1)
            p.contains("pivot") || p.contains("ventana") || p.contains("vitroven") -> PlantillaCfg("Ventana", true, true, true, 1)
            else -> PlantillaCfg("", false, false, false, 1)
        }
    }

    private fun formatoMedidaPlano(valor: Float): String {
        val redondeado = Math.round(valor * 10f) / 10f
        return if (redondeado % 1f == 0f) redondeado.toInt().toString() else redondeado.toString()
    }

    private fun medidasPorDefecto(producto: String): Pair<String, String> {
        return when (producto.lowercase()) {
            "puerta" -> "90" to "210"
            "ventana" -> "120" to "110"
            "mampara" -> "160" to "200"
            "corrediza" -> "180" to "110"
            "puerta ducha" -> "80" to "190"
            "baranda" -> "200" to "100"
            "reja" -> "150" to "150"
            "muro cortina" -> "300" to "240"
            else -> "120" to "120"
        }
    }

    private fun seleccionarHerramienta(tool: SketchMedidasView.Tool) {
        herramientaActual = tool
        binding.sketchMedidas.setTool(tool)
        binding.btnHerramientaLapiz.isSelected = tool == SketchMedidasView.Tool.FREEHAND
        binding.btnHerramientaFormas.isSelected = tool in herramientasForma()
        binding.btnHerramientaLapiz.alpha = if (tool == SketchMedidasView.Tool.FREEHAND) 1f else 0.65f
        binding.btnHerramientaFormas.alpha = if (tool in herramientasForma()) 1f else 0.65f
        binding.btnHerramientaSeleccion.alpha = if (tool == SketchMedidasView.Tool.SELECT) 1f else 0.65f
        binding.btnHerramientaFormas.text = when (tool) {
            SketchMedidasView.Tool.NONE -> "Formas"
            SketchMedidasView.Tool.FREEHAND -> "Formas"
            SketchMedidasView.Tool.SELECT -> "Formas"
            SketchMedidasView.Tool.RECTANGLE -> "Rectangulo"
            SketchMedidasView.Tool.TRIANGLE -> "Triangulo"
            SketchMedidasView.Tool.CIRCLE -> "Circulo"
            SketchMedidasView.Tool.TEXT -> "Texto"
            SketchMedidasView.Tool.LINE -> "Linea"
            SketchMedidasView.Tool.ORTHO_LINE -> "Linea 90"
        }
        actualizarEstadoIconosFormas(tool)
    }

    private fun actualizarEstadoIconosFormas(tool: SketchMedidasView.Tool) {
        val estados = listOf(
            binding.btnFormaLapiz to (tool == SketchMedidasView.Tool.FREEHAND),
            binding.btnFormaRectangulo to (tool == SketchMedidasView.Tool.RECTANGLE),
            binding.btnFormaTriangulo to (tool == SketchMedidasView.Tool.TRIANGLE),
            binding.btnFormaCirculo to (tool == SketchMedidasView.Tool.CIRCLE),
            binding.btnFormaTexto to (tool == SketchMedidasView.Tool.TEXT),
            binding.btnFormaLinea to (tool == SketchMedidasView.Tool.LINE),
            binding.btnFormaLinea90 to (tool == SketchMedidasView.Tool.ORTHO_LINE)
        )
        estados.forEach { (view, activo) ->
            view.isSelected = activo
            view.alpha = 1f
        }
    }

    private fun herramientasForma(): Set<SketchMedidasView.Tool> {
        return setOf(
            SketchMedidasView.Tool.RECTANGLE,
            SketchMedidasView.Tool.TRIANGLE,
            SketchMedidasView.Tool.CIRCLE,
            SketchMedidasView.Tool.TEXT,
            SketchMedidasView.Tool.LINE,
            SketchMedidasView.Tool.ORTHO_LINE
        )
    }

    private fun soldarSeleccion() {
        ocultarPanelesFlotantes()
        if (!binding.sketchMedidas.weldSelected()) {
            mostrar("Selecciona dos formas para soldar")
        }
    }

    private fun cortarSeleccion() {
        ocultarPanelesFlotantes()
        if (!binding.sketchMedidas.subtractSelected()) {
            mostrar("Selecciona primero la forma a cortar y luego la forma base")
        }
    }

    private fun copiarEspejoSeleccion() {
        ocultarPanelesFlotantes()
        if (!binding.sketchMedidas.mirrorCopySelected()) {
            mostrar("Selecciona una figura")
        }
    }

    private fun duplicarSeleccion() {
        ocultarPanelesFlotantes()
        if (!binding.sketchMedidas.duplicateSelected()) {
            mostrar("Selecciona una figura")
        }
    }

    private fun rotarSeleccion() {
        ocultarPanelesFlotantes()
        if (!binding.sketchMedidas.rotateSelected()) {
            mostrar("Selecciona una figura")
        }
    }

    private fun agruparSeleccion() {
        ocultarPanelesFlotantes()
        if (!binding.sketchMedidas.groupSelected()) {
            mostrar("Selecciona dos o mas figuras")
        }
    }

    private fun desagruparSeleccion() {
        ocultarPanelesFlotantes()
        if (!binding.sketchMedidas.ungroupSelected()) {
            mostrar("Selecciona un grupo")
        }
    }

    private fun eliminarSeleccion() {
        if (!binding.sketchMedidas.deleteSelected()) {
            mostrar("Selecciona una figura")
        }
    }

    private fun mostrarDialogoEscalaSeleccion() {
        ocultarPanelesFlotantes()
        val opciones = arrayOf("Reducir 50%", "Reducir 75%", "Aumentar 125%", "Aumentar 150%")
        val factores = floatArrayOf(0.5f, 0.75f, 1.25f, 1.5f)
        AlertDialog.Builder(this)
            .setTitle("Escala")
            .setItems(opciones) { _, which ->
                if (!binding.sketchMedidas.scaleSelected(factores[which])) {
                    mostrar("Selecciona una figura")
                }
            }
            .show()
    }

    private fun mostrarDialogoCompas() {
        ocultarPanelesFlotantes()
        val input = EditText(this).apply {
            hint = "Restante cm"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setSingleLine(true)
        }
        AlertDialog.Builder(this)
            .setTitle("Compas")
            .setView(input)
            .setPositiveButton("Calcular") { _, _ ->
                val restante = input.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                if (restante == null || restante <= 0f || restante >= 120f) {
                    mostrar("Ingresa un restante mayor que 0 y menor que 120")
                    return@setPositiveButton
                }
                val lado = 60f
                val cosValor = (((lado * lado) + (lado * lado) - (restante * restante)) / (2f * lado * lado))
                    .coerceIn(-1f, 1f)
                val angulo = Math.toDegrees(kotlin.math.acos(cosValor.toDouble())).toFloat()
                val complemento = 90f - angulo
                val ingleteBase = angulo / 2f
                val inglete = if (ingleteBase > 45f) 90f - ingleteBase else ingleteBase
                binding.sketchMedidas.insertarResultadoCompas(restante, angulo, complemento, inglete)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarOpcionesRectangulo() {
        val opciones = arrayOf(
            "Normal",
            "Redondear superior izquierda",
            "Redondear superior derecha",
            "Redondear inferior derecha",
            "Redondear inferior izquierda"
        )
        AlertDialog.Builder(this)
            .setTitle("Rectangulo")
            .setItems(opciones) { _, which ->
                if (which == 0) {
                    binding.sketchMedidas.clearRectangleRoundedCorner()
                    seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.RECTANGLE)
                } else {
                    pedirRadioRectangulo(which - 1)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarOpcionesRecurrenteF6() {
        ocultarPanelesFlotantes()
        val opciones = arrayOf(
            "Superior izquierda",
            "Superior derecha",
            "Inferior derecha",
            "Inferior izquierda"
        )
        val seleccionadas = booleanArrayOf(true, true, true, true)
        AlertDialog.Builder(this)
            .setTitle("F6")
            .setMultiChoiceItems(opciones, seleccionadas) { _, which, checked ->
                seleccionadas[which] = checked
            }
            .setPositiveButton("Aceptar") { _, _ ->
                val esquinas = seleccionadas.indices
                    .filter { index -> seleccionadas[index] }
                    .toSet()
                if (esquinas.isEmpty()) {
                    mostrar("Elige al menos una esquina")
                    return@setPositiveButton
                }
                pedirRadioRecurrenteF6(esquinas)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun pedirRadioRecurrenteF6(esquinas: Set<Int>) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Radio"
            setText("18")
            setSelectAllOnFocus(true)
        }
        AlertDialog.Builder(this)
            .setTitle("Radio")
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                val radio = input.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                if (radio == null || radio <= 0f) {
                    mostrar("Radio invalido")
                    return@setPositiveButton
                }
                binding.sketchMedidas.insertarRecurrenteF6(esquinas, radio)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun pedirRadioRectangulo(esquina: Int) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Radio"
            setText("10")
            setSelectAllOnFocus(true)
        }
        AlertDialog.Builder(this)
            .setTitle("Radio")
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                val radio = input.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                if (radio == null || radio <= 0f) {
                    mostrar("Radio invalido")
                    return@setPositiveButton
                }
                binding.sketchMedidas.setRectangleRoundedCorner(esquina, radio)
                seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.RECTANGLE)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoRedondearEsquina() {
        ocultarPanelesFlotantes()
        val esquinas = arrayOf("Superior izquierda", "Superior derecha", "Inferior derecha", "Inferior izquierda")
        AlertDialog.Builder(this)
            .setTitle("Redondear esquina")
            .setItems(esquinas) { _, which -> pedirRadioRedondeo(which) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun pedirRadioRedondeo(esquina: Int) {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            hint = "Radio"
            setText("10")
            setSelectAllOnFocus(true)
        }
        AlertDialog.Builder(this)
            .setTitle("Radio")
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                val radio = input.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                if (radio == null || radio <= 0f) {
                    mostrar("Radio invalido")
                    return@setPositiveButton
                }
                if (!binding.sketchMedidas.redondearEsquinaSeleccionada(esquina, radio)) {
                    mostrar("Selecciona una figura rectangular")
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cerrarPanelRecurrente() {
        ocultarPanelesFlotantes()
    }

    private fun mostrarOpcionesRecurrenteF4() {
        ocultarPanelesFlotantes()
        val opciones = arrayOf("Ambos agregados", "Solo izquierdo", "Solo derecho")
        AlertDialog.Builder(this)
            .setTitle("F4")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> binding.sketchMedidas.insertarRecurrenteF4(conIzquierdo = true, conDerecho = true)
                    1 -> binding.sketchMedidas.insertarRecurrenteF4(conIzquierdo = true, conDerecho = false)
                    2 -> binding.sketchMedidas.insertarRecurrenteF4(conIzquierdo = false, conDerecho = true)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoProducto(onProducto: (String) -> Unit) {
        val selected = productosMedida.indexOf(productoActual).takeIf { it >= 0 } ?: 0
        AlertDialog.Builder(this)
            .setTitle("Producto")
            .setSingleChoiceItems(productosMedida, selected) { dialog, which ->
                val producto = productosMedida[which]
                dialog.dismiss()
                onProducto(producto)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoProductoParaGuardar(onGuardado: ((String) -> Unit)? = null) {
        mostrarDialogoProducto { producto ->
            guardarApunte(producto)?.let { texto ->
                onGuardado?.invoke(texto)
            }
        }
    }

    private fun mostrarDialogoProductoParaArchivar() {
        if (!validarLienzoParaMedida()) return
        mostrarDialogoProducto { producto ->
            archivarMedida(producto)
        }
    }

    private fun guardarProyecto() {
        val notas = binding.etNotasMedida.text?.toString()?.trim().orEmpty()
        if (!binding.sketchMedidas.hasDrawing()) {
            if (notas.isBlank()) {
                mostrar("Dibuja una medida antes de guardar")
                return
            }
            guardarTextoProyecto(productoActual.ifBlank { "Proyecto medidas" }, notas)
            return
        }
        mostrarDialogoProductoParaGuardar()
    }

    private fun guardarApunte(producto: String): String? {
        val cliente = clienteActual()
        val notas = binding.etNotasMedida.text?.toString()?.trim().orEmpty()

        if (cliente.isBlank()) {
            mostrar("Ingresa el nombre del cliente")
            return null
        }
        if (!binding.sketchMedidas.hasDrawing()) {
            mostrar("Dibuja una medida antes de guardar")
            return null
        }

        val textoGenerado = construirTextoMedidas(producto, notas)
        return guardarTextoProyecto(producto, textoGenerado)?.also {
            binding.etNotasMedida.setText(it)
        }
    }

    private fun archivarMedida(producto: String) {
        if (!validarLienzoParaMedida()) return
        val notas = binding.etNotasMedida.text?.toString()?.trim().orEmpty()
        val medida = construirBloqueMedida(producto) ?: run {
            mostrar("No se pudo leer ancho y alto del dibujo")
            return
        }
        val textoArchivado = unirBloques(notas, medida)
        guardarTextoProyecto(producto, textoArchivado)?.let {
            binding.etNotasMedida.setText(it)
            binding.etInfoProductoMedida.setText("")
            binding.sketchMedidas.clear()
            bocetoActualDesdeArchivo = null
            seleccionarHerramienta(SketchMedidasView.Tool.NONE)
            mostrar("Medida archivada")
        }
    }

    private fun guardarTextoProyecto(producto: String, texto: String): String? {
        val cliente = clienteActual()
        if (cliente.isBlank()) {
            mostrar("Ingresa el nombre del cliente")
            return null
        }
        if (texto.isBlank()) {
            mostrar("No hay medidas para guardar")
            return null
        }
        return runCatching {
            val dir = obtenerDirectorioMedidas()
            val baseName = archivoActualTxt?.nameWithoutExtension
                ?: "${formatoFecha.format(Date())}_${sanitizarNombre(cliente)}"
            val txtFile = File(dir, "$baseName.txt")
            val bocetos = cargarIndiceBocetos(dir, baseName).toMutableList()
            var ultimoBoceto = bocetos.lastOrNull()?.archivo.orEmpty()

            if (binding.sketchMedidas.hasDrawing()) {
                val sketchFile = File(dir, "${baseName}_${System.currentTimeMillis()}.json")
                val descripcion = construirBloqueMedida(producto) ?: productoConInfoActual(producto)
                sketchFile.writeText(binding.sketchMedidas.exportEditableState())
                bocetos.add(
                    BocetoProyecto(
                        archivo = sketchFile.name,
                        producto = productoConInfoActual(producto),
                        descripcion = descripcion,
                        fecha = System.currentTimeMillis()
                    )
                )
                guardarIndiceBocetos(dir, baseName, bocetos)
                ultimoBoceto = sketchFile.name
                bocetoActualDesdeArchivo = sketchFile.name
            }

            txtFile.writeText(construirTextoArchivo(cliente, producto, texto, ultimoBoceto, nombreIndiceBocetos(baseName)))
            archivoActualTxt = txtFile
            productoActual = producto
            binding.etNotasMedida.setText(texto)
            actualizarPanelInformacion()
            mostrar("Guardado en Crystal/Medidas")
            texto
        }.getOrElse {
            mostrar("No se pudo guardar: ${it.message}")
            null
        }
    }

    private fun mostrarGuardados() {
        val archivos = obtenerDirectorioMedidas()
            .listFiles { file -> file.extension.equals("txt", ignoreCase = true) }
            ?.sortedByDescending { it.lastModified() }
            .orEmpty()

        if (archivos.isEmpty()) {
            mostrar("No hay medidas guardadas")
            return
        }

        val nombres = archivos.map { it.nameWithoutExtension }
        AlertDialog.Builder(this)
            .setTitle("Abrir medidas")
            .setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)) { _, which ->
                abrirApunte(archivos[which])
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun abrirApunte(txtFile: File) {
        runCatching {
            val texto = txtFile.readText()
            binding.etClienteMedida.setText(extraerCampo(texto, "Cliente").ifBlank { txtFile.nameWithoutExtension })
            productoActual = extraerCampo(texto, "Producto")
            binding.etInfoProductoMedida.setText("")
            binding.etNotasMedida.setText(extraerNotas(texto))
            actualizarPanelInformacion()
            archivoActualTxt = txtFile

            val bocetoName = extraerCampo(texto, "Boceto")
            val bocetos = cargarIndiceBocetos(txtFile.parentFile, txtFile.nameWithoutExtension)
            binding.sketchMedidas.clear()

            when {
                bocetos.size > 1 -> {
                    bocetoActualDesdeArchivo = null
                    mostrarSelectorBocetos(bocetos, txtFile.parentFile, "Elige medida")
                }
                bocetos.size == 1 -> {
                    cargarBocetoEnLienzo(bocetos.first(), txtFile.parentFile)
                }
                else -> {
                    val bocetoFile = if (bocetoName.isNotBlank()) File(txtFile.parentFile, bocetoName) else null
                    cargarArchivoBocetoEnLienzo(
                        bocetoFile?.takeIf { it.exists() }
                            ?: File(txtFile.parentFile, "${txtFile.nameWithoutExtension}.json").takeIf { it.exists() },
                        bocetoName
                    )
                }
            }
            mostrar("Medidas abiertas")
        }.onFailure {
            mostrar("No se pudo abrir: ${it.message}")
        }
    }

    private fun mostrarSelectorBocetosProyectoActual() {
        val txtFile = archivoActualTxt ?: run {
            mostrar("Abre un proyecto primero")
            return
        }
        val bocetos = cargarIndiceBocetos(txtFile.parentFile, txtFile.nameWithoutExtension)
        if (bocetos.isEmpty()) {
            mostrar("Este proyecto no tiene medidas archivadas")
            return
        }
        mostrarSelectorBocetos(bocetos, txtFile.parentFile, "Medidas archivadas")
    }

    private fun mostrarSelectorBocetos(bocetos: List<BocetoProyecto>, dir: File?, titulo: String) {
        val opciones = bocetos.mapIndexed { index, boceto -> etiquetaBoceto(index, boceto) }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, opciones)) { _, which ->
                cargarBocetoEnLienzo(bocetos[which], dir)
            }
            .setNeutralButton("Lienzo limpio") { _, _ ->
                limpiarLienzoActual()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun etiquetaBoceto(index: Int, boceto: BocetoProyecto): String {
        val medida = boceto.descripcion
            .lineSequence()
            .map { it.trim() }
            .firstOrNull { it.contains(" x ", ignoreCase = true) }
            .orEmpty()
        val producto = boceto.producto.ifBlank { "Medida ${index + 1}" }
        return listOf("${index + 1}. $producto", medida)
            .filter { it.isNotBlank() }
            .joinToString(" - ")
    }

    private fun cargarBocetoEnLienzo(boceto: BocetoProyecto, dir: File?): Boolean {
        val file = File(dir ?: obtenerDirectorioMedidas(), boceto.archivo)
        val cargado = cargarArchivoBocetoEnLienzo(file.takeIf { it.exists() }, boceto.archivo)
        if (cargado) {
            actualizarPanelInformacion()
            mostrar("Medida mostrada")
        } else {
            mostrar("No se pudo mostrar la medida")
        }
        return cargado
    }

    private fun cargarArchivoBocetoEnLienzo(file: File?, nombreBoceto: String? = null): Boolean {
        binding.sketchMedidas.clear()
        bocetoActualDesdeArchivo = null
        if (file == null || !file.exists()) return false

        val cargado = if (file.extension.equals("json", ignoreCase = true)) {
            binding.sketchMedidas.loadEditableState(file.readText())
        } else {
            binding.sketchMedidas.loadBackground(file)
            true
        }
        if (cargado) {
            bocetoActualDesdeArchivo = nombreBoceto?.takeIf { it.isNotBlank() } ?: file.name
            binding.sketchMedidas.post { binding.sketchMedidas.fitContentInView() }
        }
        return cargado
    }

    private fun enviarAMainActivity() {
        val notas = binding.etNotasMedida.text?.toString()?.trim().orEmpty()
        if (notas.isBlank()) {
            mostrarDialogoProductoParaGuardar { textoGenerado ->
                enviarTextoAMainActivity(textoGenerado)
            }
            return
        }
        enviarTextoAMainActivity(notas)
    }

    private fun enviarTextoAMainActivity(texto: String) {
        startActivity(
            Intent(this, MainActivity::class.java).apply {
                putExtra(ChatInteropIntents.EXTRA_IMPORT_MEASURES_TEXT, texto)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        )
    }

    private fun confirmarLimpiar() {
        AlertDialog.Builder(this)
            .setTitle("Limpiar apunte")
            .setMessage("Se borrara el dibujo y las notas actuales.")
            .setPositiveButton("Limpiar") { _, _ ->
                archivoActualTxt = null
                productoActual = ""
                bocetoActualDesdeArchivo = null
                binding.etInfoProductoMedida.setText("")
                binding.etNotasMedida.setText("")
                actualizarPanelInformacion()
                binding.sketchMedidas.clear()
                seleccionarHerramienta(SketchMedidasView.Tool.NONE)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarLimpiarLienzoActual() {
        if (!binding.sketchMedidas.hasContent()) {
            limpiarLienzoActual()
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Limpiar lienzo")
            .setMessage("Se borrara solo el dibujo visible. El cliente y las medidas archivadas se conservan.")
            .setPositiveButton("Limpiar") { _, _ ->
                limpiarLienzoActual()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun limpiarLienzoActual() {
        bocetoActualDesdeArchivo = null
        binding.etInfoProductoMedida.setText("")
        binding.sketchMedidas.clear()
        seleccionarHerramienta(SketchMedidasView.Tool.NONE)
        actualizarPanelInformacion()
        mostrar("Lienzo limpio")
    }

    private fun mostrarOpcionesCompartirMedida() {
        val opciones = arrayOf("Compartir PDF", "Compartir formato Crystal")
        AlertDialog.Builder(this)
            .setTitle("Compartir medidas")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> generarYCompartirPdfMedida()
                    1 -> generarYCompartirArchivoMedidasCrystal()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun generarYCompartirArchivoMedidasCrystal() {
        val paquete = construirPaqueteMedidasCrystal() ?: return
        val cliente = paquete.optString("cliente").ifBlank { "cliente" }
        val nombreArchivo = "Medidas_${sanitizarNombre(cliente)}_${formatoFecha.format(Date())}.$EXTENSION_MEDIDAS_CRYSTAL"
        val shareDir = File(cacheDir, "medidasshare").apply { mkdirs() }
        val file = File(shareDir, nombreArchivo)

        runCatching {
            file.writeText(paquete.toString(2))
            compartirArchivoMedidasCrystal(file, cliente)
        }.onFailure {
            mostrar("No se pudo compartir formato Crystal: ${it.message}")
        }
    }

    private fun construirPaqueteMedidasCrystal(): JSONObject? {
        val cliente = clienteActual()
        if (cliente.isBlank()) {
            mostrar("Ingresa el nombre del cliente")
            return null
        }

        val notas = binding.etNotasMedida.text?.toString()?.trim().orEmpty()
        val txtFile = archivoActualTxt
        val dir = txtFile?.parentFile ?: obtenerDirectorioMedidas()
        val bocetos = txtFile?.let { cargarIndiceBocetos(it.parentFile, it.nameWithoutExtension) }.orEmpty()
        val estadoActual = if (binding.sketchMedidas.hasDrawing()) binding.sketchMedidas.exportEditableState() else null
        val items = JSONArray()

        bocetos.forEach { boceto ->
            val state = if (boceto.archivo == bocetoActualDesdeArchivo && estadoActual != null) {
                estadoActual
            } else {
                File(dir, boceto.archivo).takeIf { it.exists() }?.readText().orEmpty()
            }
            if (state.isNotBlank()) {
                items.put(
                    JSONObject()
                        .put("archivo", boceto.archivo)
                        .put("producto", boceto.producto)
                        .put("descripcion", boceto.descripcion)
                        .put("fecha", boceto.fecha)
                        .put("state", state)
                )
            }
        }

        val dibujoActualSinArchivo = estadoActual != null &&
            (bocetoActualDesdeArchivo == null || bocetos.none { it.archivo == bocetoActualDesdeArchivo })
        if (dibujoActualSinArchivo) {
            val producto = productoConInfoActual(productoActual.ifBlank { "Diseno actual" })
            items.put(
                JSONObject()
                    .put("archivo", "actual.json")
                    .put("producto", producto)
                    .put("descripcion", construirBloqueMedida(productoActual.ifBlank { "Diseno actual" }) ?: producto)
                    .put("fecha", System.currentTimeMillis())
                    .put("state", estadoActual)
            )
        }

        if (items.length() == 0 && notas.isBlank()) {
            mostrar("No hay medidas para compartir")
            return null
        }

        return JSONObject()
            .put("format", "crystal.medidas")
            .put("version", 1)
            .put("exportedAt", System.currentTimeMillis())
            .put("cliente", cliente)
            .put("producto", productoActual)
            .put("infoProducto", binding.etInfoProductoMedida.text?.toString()?.trim().orEmpty())
            .put("notas", notas)
            .put("currentBoceto", bocetoActualDesdeArchivo.orEmpty())
            .put("items", items)
    }

    private fun compartirArchivoMedidasCrystal(file: File, cliente: String) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        val intent = Intent(this, ListChatActivity::class.java).apply {
            putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_URI, uri.toString())
            putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_NAME, file.name)
            putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_MIME, MIME_MEDIDAS_CRYSTAL)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = ClipData.newUri(contentResolver, "medidas_crystal", uri)
        }
        startActivity(intent)
    }

    private fun manejarIntentEntrada(intent: Intent?) {
        if (intent == null) return

        if (intent.hasExtra(EXTRA_LISTA)) {
            val cliente = intent.getStringExtra(EXTRA_CLIENTE).orEmpty()
            @Suppress("UNCHECKED_CAST", "DEPRECATION")
            val listaRecibida = intent.getSerializableExtra(EXTRA_LISTA) as? ArrayList<crystal.crystal.Listado>
            intent.removeExtra(EXTRA_LISTA)
            intent.removeExtra(EXTRA_CLIENTE)
            intent.removeExtra(EXTRA_INDICE_INICIAL)
            if (listaRecibida != null && listaRecibida.isNotEmpty()) {
                procesarListaPendientes(listaRecibida, cliente)
                return
            }
        }

        if (intent.hasExtra(EXTRA_PRODUCTO)) {
            val producto = intent.getStringExtra(EXTRA_PRODUCTO).orEmpty()
            val cliente = intent.getStringExtra(EXTRA_CLIENTE).orEmpty()
            val medi1 = intent.getFloatExtra(EXTRA_MEDI1, 0f)
            val medi2 = intent.getFloatExtra(EXTRA_MEDI2, 0f)
            val medi3 = intent.getFloatExtra(EXTRA_MEDI3, 0f)
            val cantidad = intent.getFloatExtra(EXTRA_CANTIDAD, 0f)
            val unidad = intent.getStringExtra(EXTRA_UNIDAD).orEmpty()
            aplicarMedidaEntrante(producto, cliente, medi1, medi2, medi3, cantidad, unidad)
            intent.removeExtra(EXTRA_PRODUCTO)
            intent.removeExtra(EXTRA_CLIENTE)
            intent.removeExtra(EXTRA_MEDI1)
            intent.removeExtra(EXTRA_MEDI2)
            intent.removeExtra(EXTRA_MEDI3)
            intent.removeExtra(EXTRA_CANTIDAD)
            intent.removeExtra(EXTRA_UNIDAD)
            return
        }

        val uri = when (intent.action) {
            Intent.ACTION_SEND -> obtenerStreamCompartido(intent)
            Intent.ACTION_VIEW -> intent.data
            else -> null
        } ?: return

        runCatching {
            val texto = contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }.orEmpty()
            val root = JSONObject(texto)
            if (root.optString("format") != "crystal.medidas") return
            confirmarImportarMedidasCrystal(root)
            intent.action = null
            intent.data = null
            intent.removeExtra(Intent.EXTRA_STREAM)
        }.onFailure {
            mostrar("No se pudo abrir medidas Crystal: ${it.message}")
        }
    }

    private fun obtenerStreamCompartido(intent: Intent): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_STREAM)
        }
    }

    private fun confirmarImportarMedidasCrystal(root: JSONObject) {
        val cliente = root.optString("cliente").ifBlank { "sin cliente" }
        val cantidad = root.optJSONArray("items")?.length() ?: 0
        AlertDialog.Builder(this)
            .setTitle("Medidas Crystal")
            .setMessage("Cliente: $cliente\nDisenos: $cantidad\n\nDeseas abrir estas medidas?")
            .setPositiveButton("Abrir") { _, _ -> importarMedidasCrystal(root) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun importarMedidasCrystal(root: JSONObject) {
        runCatching {
            val cliente = root.optString("cliente").ifBlank { "cliente" }
            val producto = root.optString("producto").ifBlank { "Proyecto medidas" }
            val infoProducto = root.optString("infoProducto")
            val notas = root.optString("notas")
            val currentBoceto = root.optString("currentBoceto")
            val items = root.optJSONArray("items") ?: JSONArray()
            if (items.length() == 0 && notas.isBlank()) {
                mostrar("El archivo no contiene medidas")
                return
            }

            val dir = obtenerDirectorioMedidas()
            val baseName = "${formatoFecha.format(Date())}_${sanitizarNombre(cliente)}"
            val bocetos = mutableListOf<BocetoProyecto>()
            var ultimoBoceto = ""
            var bocetoParaMostrar: BocetoProyecto? = null

            for (i in 0 until items.length()) {
                val item = items.optJSONObject(i) ?: continue
                val state = item.optString("state")
                if (state.isBlank()) continue
                val originalName = item.optString("archivo")
                val sketchName = "${baseName}_${i + 1}_${System.currentTimeMillis()}.json"
                File(dir, sketchName).writeText(state)
                val boceto = BocetoProyecto(
                    archivo = sketchName,
                    producto = item.optString("producto").ifBlank { producto },
                    descripcion = item.optString("descripcion"),
                    fecha = item.optLong("fecha", System.currentTimeMillis())
                )
                bocetos.add(boceto)
                ultimoBoceto = sketchName
                if (originalName == currentBoceto) bocetoParaMostrar = boceto
            }

            if (bocetos.isNotEmpty()) {
                guardarIndiceBocetos(dir, baseName, bocetos)
            }

            val txtFile = File(dir, "$baseName.txt")
            txtFile.writeText(construirTextoArchivo(cliente, producto, notas, ultimoBoceto, nombreIndiceBocetos(baseName)))

            archivoActualTxt = txtFile
            productoActual = producto
            binding.etClienteMedida.setText(cliente)
            binding.etInfoProductoMedida.setText(infoProducto)
            binding.etNotasMedida.setText(notas)
            actualizarPanelInformacion()

            val bocetoVisible = bocetoParaMostrar ?: bocetos.firstOrNull()
            if (bocetoVisible != null) {
                cargarBocetoEnLienzo(bocetoVisible, dir)
            } else {
                binding.sketchMedidas.clear()
                bocetoActualDesdeArchivo = null
            }
            mostrar("Medidas abiertas")
        }.onFailure {
            mostrar("No se pudo importar: ${it.message}")
        }
    }

    private fun generarYCompartirPdfMedida() {
        val cliente = clienteActual()
        if (cliente.isBlank()) {
            mostrar("Ingresa el nombre del cliente")
            return
        }

        val nombreArchivo = "Medidas_${sanitizarNombre(cliente)}_${formatoFecha.format(Date())}.pdf"
        val pdfFile = File(obtenerDirectorioMedidas(), nombreArchivo)

        runCatching {
            val paginas = construirPaginasPdfMedida()
            if (paginas.isEmpty()) {
                mostrar("No hay diseños para compartir")
                return
            }
            escribirPdfMedida(pdfFile, cliente, paginas)
            compartirPdfMedida(pdfFile)
        }.onFailure {
            mostrar("No se pudo generar PDF: ${it.message}")
        }
    }

    private fun construirPaginasPdfMedida(): List<PaginaPdfMedida> {
        val dir = obtenerDirectorioMedidas()
        val txtFile = archivoActualTxt
        val bocetos = txtFile?.let { cargarIndiceBocetos(it.parentFile, it.nameWithoutExtension) }.orEmpty()
        val estadoActual = binding.sketchMedidas.exportEditableState()
        val tieneDibujoActual = binding.sketchMedidas.hasDrawing()
        val bitmapActual = if (binding.sketchMedidas.hasContent()) binding.sketchMedidas.exportBitmap() else null
        val paginas = mutableListOf<PaginaPdfMedida>()

        try {
            for (boceto in bocetos) {
                val file = File(txtFile?.parentFile ?: dir, boceto.archivo)
                if (!file.exists()) continue
                val cargado = if (file.extension.equals("json", ignoreCase = true)) {
                    binding.sketchMedidas.loadEditableState(file.readText())
                } else {
                    binding.sketchMedidas.loadBackground(file)
                    true
                }
                if (cargado) {
                    paginas.add(
                        PaginaPdfMedida(
                            producto = boceto.producto.ifBlank { "Medidas tecnicas" },
                            descripcion = boceto.descripcion,
                            bitmap = binding.sketchMedidas.exportBitmap()
                        )
                    )
                }
            }

            val incluirActual = tieneDibujoActual &&
                (bocetos.isEmpty() || bocetoActualDesdeArchivo == null || bocetos.none { it.archivo == bocetoActualDesdeArchivo })
            if (incluirActual) {
                binding.sketchMedidas.loadEditableState(estadoActual)
                paginas.add(
                    PaginaPdfMedida(
                        producto = productoConInfoActual(productoActual.ifBlank { "Diseño actual" }),
                        descripcion = construirBloqueMedida(productoActual.ifBlank { "Diseño actual" })
                            ?: binding.etNotasMedida.text?.toString()?.trim().orEmpty(),
                        bitmap = binding.sketchMedidas.exportBitmap()
                    )
                )
            } else if (bocetos.isEmpty() && bitmapActual != null) {
                paginas.add(
                    PaginaPdfMedida(
                        producto = productoConInfoActual(productoActual.ifBlank { "Medidas tecnicas" }),
                        descripcion = binding.etNotasMedida.text?.toString()?.trim().orEmpty(),
                        bitmap = bitmapActual
                    )
                )
            }
        } finally {
            if (tieneDibujoActual) {
                binding.sketchMedidas.loadEditableState(estadoActual)
            } else {
                binding.sketchMedidas.clear()
            }
            binding.sketchMedidas.post { binding.sketchMedidas.fitContentInView() }
        }

        return paginas
    }

    private fun escribirPdfMedida(file: File, cliente: String, paginas: List<PaginaPdfMedida>) {
        val pageWidth = 595
        val pageHeight = 842
        val margin = 36f
        val pdf = PdfDocument()
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(28, 28, 28)
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(35, 35, 35)
            textSize = 11f
        }
        val labelPaint = Paint(textPaint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }

        paginas.forEachIndexed { index, pagina ->
            val page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, index + 1).create())
            val canvas = page.canvas
            canvas.drawColor(Color.WHITE)

            var y = margin
            canvas.drawText("Medidas tecnicas", margin, y, titlePaint)
            y += 24f
            canvas.drawText("Cliente: $cliente", margin, y, labelPaint)
            y += 16f
            canvas.drawText("Producto: ${pagina.producto}", margin, y, textPaint)
            y += 16f
            canvas.drawText("Diseño: ${index + 1}/${paginas.size}", margin, y, textPaint)
            y += 16f
            canvas.drawText("Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(Date())}", margin, y, textPaint)
            y += 20f

            val maxImageW = pageWidth - margin * 2f
            val maxImageH = if (pagina.descripcion.isBlank()) pageHeight - y - margin else 520f
            val imageScale = minOf(maxImageW / pagina.bitmap.width, maxImageH / pagina.bitmap.height)
                .coerceAtMost(1.45f)
                .coerceAtLeast(0.05f)
            val drawW = pagina.bitmap.width * imageScale
            val drawH = pagina.bitmap.height * imageScale
            val left = margin + (maxImageW - drawW) / 2f
            val dest = RectF(left, y, left + drawW, y + drawH)
            canvas.drawBitmap(pagina.bitmap, null, dest, imagePaint)
            y = dest.bottom + 22f

            if (pagina.descripcion.isNotBlank() && y < pageHeight - margin) {
                canvas.drawText("Medidas / apuntes", margin, y, labelPaint)
                y += 16f
                drawWrappedText(canvas, pagina.descripcion, margin, y, pageWidth - margin * 2f, pageHeight - margin, textPaint, 14f)
            }

            pdf.finishPage(page)
        }

        file.outputStream().use { pdf.writeTo(it) }
        pdf.close()
    }

    private fun generarYCompartirPdfMedidaLegacy() {
        if (!binding.sketchMedidas.hasContent()) {
            mostrar("No hay diseño para compartir")
            return
        }
        val cliente = clienteActual()
        if (cliente.isBlank()) {
            mostrar("Ingresa el nombre del cliente")
            return
        }

        val notas = binding.etNotasMedida.text?.toString()?.trim().orEmpty()
        val producto = productoConInfoActual(productoActual.ifBlank { "Medidas tecnicas" })
        val nombreArchivo = "Medidas_${sanitizarNombre(cliente)}_${formatoFecha.format(Date())}.pdf"
        val pdfFile = File(obtenerDirectorioMedidas(), nombreArchivo)

        runCatching {
            escribirPdfMedida(pdfFile, cliente, producto, notas, binding.sketchMedidas.exportBitmap())
            compartirPdfMedida(pdfFile)
        }.onFailure {
            mostrar("No se pudo generar PDF: ${it.message}")
        }
    }

    private fun escribirPdfMedida(file: File, cliente: String, producto: String, notas: String, boceto: Bitmap) {
        val pageWidth = 595
        val pageHeight = 842
        val margin = 36f
        val pdf = PdfDocument()
        val page = pdf.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create())
        val canvas = page.canvas
        canvas.drawColor(Color.WHITE)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(28, 28, 28)
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(35, 35, 35)
            textSize = 11f
        }
        val labelPaint = Paint(textPaint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val imagePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { isFilterBitmap = true }

        var y = margin
        canvas.drawText("Medidas tecnicas", margin, y, titlePaint)
        y += 24f
        canvas.drawText("Cliente: $cliente", margin, y, labelPaint)
        y += 16f
        canvas.drawText("Producto: $producto", margin, y, textPaint)
        y += 16f
        canvas.drawText("Fecha: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).format(Date())}", margin, y, textPaint)
        y += 20f

        val maxImageW = pageWidth - margin * 2f
        val maxImageH = if (notas.isBlank()) pageHeight - y - margin else 520f
        val imageScale = minOf(maxImageW / boceto.width, maxImageH / boceto.height)
            .coerceAtMost(1.45f)
            .coerceAtLeast(0.05f)
        val drawW = boceto.width * imageScale
        val drawH = boceto.height * imageScale
        val left = margin + (maxImageW - drawW) / 2f
        val dest = RectF(left, y, left + drawW, y + drawH)
        canvas.drawBitmap(boceto, null, dest, imagePaint)
        y = dest.bottom + 22f

        if (notas.isNotBlank() && y < pageHeight - margin) {
            canvas.drawText("Apuntes", margin, y, labelPaint)
            y += 16f
            drawWrappedText(canvas, notas, margin, y, pageWidth - margin * 2f, pageHeight - margin, textPaint, 14f)
        }

        pdf.finishPage(page)
        file.outputStream().use { pdf.writeTo(it) }
        pdf.close()
    }

    private fun drawWrappedText(
        canvas: android.graphics.Canvas,
        text: String,
        x: Float,
        startY: Float,
        maxWidth: Float,
        bottom: Float,
        paint: Paint,
        lineHeight: Float
    ): Float {
        var y = startY
        text.lines().forEach { paragraph ->
            var line = ""
            paragraph.split(Regex("\\s+")).filter { it.isNotBlank() }.forEach { word ->
                val candidate = if (line.isBlank()) word else "$line $word"
                if (paint.measureText(candidate) <= maxWidth) {
                    line = candidate
                } else {
                    if (y > bottom) return y
                    canvas.drawText(line, x, y, paint)
                    y += lineHeight
                    line = word
                }
            }
            if (line.isNotBlank()) {
                if (y > bottom) return y
                canvas.drawText(line, x, y, paint)
                y += lineHeight
            }
            y += lineHeight / 2f
        }
        return y
    }

    private fun compartirPdfMedida(file: File) {
        val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = ClipData.newUri(contentResolver, "medidas_pdf", uri)
        }
        startActivity(Intent.createChooser(intent, "Compartir medidas"))
    }

    private fun nombreIndiceBocetos(baseName: String): String = "${baseName}_bocetos.json"

    private fun cargarIndiceBocetos(dir: File?, baseName: String): List<BocetoProyecto> {
        val file = File(dir ?: obtenerDirectorioMedidas(), nombreIndiceBocetos(baseName))
        if (!file.exists()) return emptyList()
        return runCatching {
            val root = JSONObject(file.readText())
            val items = root.optJSONArray("items") ?: JSONArray()
            val result = mutableListOf<BocetoProyecto>()
            for (i in 0 until items.length()) {
                val item = items.optJSONObject(i) ?: continue
                result.add(
                    BocetoProyecto(
                        archivo = item.optString("archivo"),
                        producto = item.optString("producto"),
                        descripcion = item.optString("descripcion"),
                        fecha = item.optLong("fecha", 0L)
                    )
                )
            }
            result.filter { it.archivo.isNotBlank() }
        }.getOrDefault(emptyList())
    }

    private fun guardarIndiceBocetos(dir: File, baseName: String, bocetos: List<BocetoProyecto>) {
        val items = JSONArray()
        bocetos.forEach { boceto ->
            items.put(
                JSONObject()
                    .put("archivo", boceto.archivo)
                    .put("producto", boceto.producto)
                    .put("descripcion", boceto.descripcion)
                    .put("fecha", boceto.fecha)
            )
        }
        val root = JSONObject()
            .put("version", 1)
            .put("items", items)
        File(dir, nombreIndiceBocetos(baseName)).writeText(root.toString())
    }

    private fun construirTextoArchivo(
        cliente: String,
        producto: String,
        notas: String,
        bocetoName: String,
        indiceBocetosName: String
    ): String {
        return buildString {
            appendLine("Cliente: $cliente")
            appendLine("Producto: $producto")
            appendLine("Fecha: ${Date()}")
            appendLine("Boceto: $bocetoName")
            appendLine("Bocetos: $indiceBocetosName")
            appendLine()
            appendLine("Notas:")
            appendLine(notas)
        }.trimEnd()
    }

    private fun construirTextoMedidas(producto: String, notas: String): String {
        val generado = construirBloqueMedida(producto) ?: producto

        val notasLimpias = notas.trim()
        if (notasLimpias.isBlank()) return generado
        if (notasLimpias.contains(generado, ignoreCase = true)) return notasLimpias
        return unirBloques(generado, notasLimpias)
    }

    private fun construirBloqueMedida(producto: String): String? {
        return binding.sketchMedidas.medidaPrincipal()?.let { medida ->
            val productoConInfo = productoConInfoActual(producto)
            "${productoConInfo}\n${formatoMedida(medida.anchoCm)} x ${formatoMedida(medida.altoCm)} = 1"
        }
    }

    private fun productoConInfoActual(producto: String): String {
        val info = binding.etInfoProductoMedida.text?.toString()?.trim().orEmpty()
        return listOf(producto.trim(), info)
            .filter { it.isNotBlank() }
            .joinToString(" ")
    }

    private fun unirBloques(primero: String, segundo: String): String {
        return listOf(primero.trim(), segundo.trim())
            .filter { it.isNotBlank() }
            .joinToString("\n\n")
    }

    private fun validarLienzoParaMedida(): Boolean {
        if (clienteActual().isBlank()) {
            mostrar("Ingresa el nombre del cliente")
            return false
        }
        if (!binding.sketchMedidas.hasDrawing()) {
            mostrar("Dibuja una medida antes de archivar")
            return false
        }
        return true
    }

    private fun formatoMedida(valor: Float): String {
        return if (valor % 1f == 0f) {
            valor.toInt().toString()
        } else {
            String.format(Locale.US, "%.1f", valor)
        }
    }

    private fun extraerCampo(texto: String, campo: String): String {
        return texto.lineSequence()
            .firstOrNull { it.startsWith("$campo:", ignoreCase = true) }
            ?.substringAfter(":")
            ?.trim()
            .orEmpty()
    }

    private fun extraerNotas(texto: String): String {
        return texto.substringAfter("Notas:", missingDelimiterValue = "")
            .trim()
    }

    private fun clienteActual(): String {
        return binding.etClienteMedida.text?.toString()?.trim().orEmpty()
    }

    private fun obtenerDirectorioMedidas(): File {
        val publicDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Crystal/Medidas"
        )
        if (publicDir.exists() || publicDir.mkdirs()) return publicDir

        return File(getExternalFilesDir(null), "Crystal/Medidas").apply { mkdirs() }
    }

    private fun sanitizarNombre(nombre: String): String {
        return nombre
            .replace(Regex("[^A-Za-z0-9_\\-]"), "_")
            .trim('_')
            .ifBlank { "cliente" }
            .take(40)
    }

    private fun mostrar(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
}
