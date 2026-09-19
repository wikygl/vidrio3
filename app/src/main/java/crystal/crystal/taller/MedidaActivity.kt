package crystal.crystal.taller


import android.annotation.SuppressLint
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
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlin.math.abs
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import crystal.crystal.MainActivity
import crystal.crystal.R
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
        binding.btnGuardarMedida.setOnClickListener { soltarHerramientaActiva(); guardarProyecto() }
        binding.btnArchivarMedida.setOnClickListener { soltarHerramientaActiva(); mostrarDialogoProductoParaArchivar() }
        binding.btnAbrirMedida.setOnClickListener { soltarHerramientaActiva(); mostrarGuardados() }
        binding.btnEnviarMedida.setOnClickListener { soltarHerramientaActiva(); enviarAMainActivity() }
        binding.btnLimpiarMedida.setOnClickListener { soltarHerramientaActiva(); confirmarLimpiar() }
        binding.btnDeshacerMedida.setOnClickListener { binding.sketchMedidas.undo() }
        binding.btnZoomMenosMedida.setOnClickListener { binding.sketchMedidas.zoomOut() }
        binding.btnZoomMasMedida.setOnClickListener { binding.sketchMedidas.zoomIn() }
        binding.btnAjustarMedida.setOnClickListener { binding.sketchMedidas.fitContentInView() }
        binding.btnPdfMedida.setOnClickListener { soltarHerramientaActiva(); generarYCompartirPdfMedida() }
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
        // Mover y Nulo sueltan TODO lo que esté puesto: la herramienta del enum y también la cota
        // a escuadra, que vive aparte (no es una Tool) y solo se apaga con cancelarCotaAEscuadra().
        binding.btnMoverMedida.setOnClickListener { soltarHerramientaActiva() }
        binding.btnNuloMedida.setOnClickListener { soltarHerramientaActiva() }
        binding.btnLimpiaMedida.setOnClickListener {
            soltarHerramientaActiva()
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
        binding.btnArchivoCortes.setOnClickListener {
            ocultarPanelesFlotantes()
            enviarLadosACortes()
        }
        binding.btnArchivoEnviarCalc.setOnClickListener {
            ocultarPanelesFlotantes()
            elegirClienteParaCalculadoras()
        }
        binding.btnHerramientaLapiz.setOnClickListener {
            ocultarPanelesFlotantes()
            seleccionarHerramienta(SketchMedidasView.Tool.FREEHAND)
        }
        binding.btnHerramientaFormas.setOnClickListener { togglePanelFormas() }
        binding.btnFormaLapiz.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.FREEHAND) }
        binding.btnFormaLapizIman.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.MAGNET_PEN) }
        binding.btnFormaRectangulo.setOnClickListener {
            binding.sketchMedidas.clearRectangleRoundedCorner()
            seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.RECTANGLE)
        }
        binding.btnFormaTriangulo.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.TRIANGLE) }
        binding.btnFormaCirculo.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.CIRCLE) }
        binding.btnFormaTexto.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.TEXT) }
        binding.btnFormaLinea.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.LINE) }
        binding.btnFormaLinea90.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.ORTHO_LINE) }
        binding.btnFormaArco.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.ARCO) }
        binding.btnFormaArco90.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.ARCO_90) }
        binding.btnFormaNodos.setOnClickListener { seleccionarHerramientaDesdePanel(SketchMedidasView.Tool.NODO) }
        binding.btnFormaPolilinea.setOnClickListener { alternarModoEdicion(SketchMedidasView.ModoEdicion.POLILINEA) }
        binding.btnEngraBisagra.setOnClickListener { mostrarDialogoBisagra() }
        binding.btnEngraAdentro.setOnClickListener { insertarSimboloCentro("adentro") }
        binding.btnEngraAfuera.setOnClickListener { insertarSimboloCentro("afuera") }
        binding.btnEngraMichi.setOnClickListener { mostrarDialogoMichi() }
        binding.btnEngraInterior.setOnClickListener { insertarInteriorExterior("interior") }
        binding.btnEngraExterior.setOnClickListener { insertarInteriorExterior("exterior") }
        binding.btnFuncionVano.setOnClickListener { mostrarDialogoPlantillaVano() }
        // Las cotas se quedan puestas para poner varias seguidas, así que este botón es el
        // interruptor: si hay una prendida la apaga; si no, pregunta cuál poner. Hay que mirar
        // ANTES de cerrar los paneles, porque cerrarlos ya la apaga.
        binding.btnFuncionEscuadra.setOnClickListener {
            val estaba = binding.sketchMedidas.eligiendoCotaAEscuadra
            ocultarPanelesFlotantes()
            if (estaba) {
                Toast.makeText(this, "Cota: apagada", Toast.LENGTH_SHORT).show()
            } else {
                mostrarDialogoCotas()
            }
        }
        binding.btnHerramientaSeleccion.setOnClickListener {
            ocultarPanelesFlotantes()
            seleccionarHerramienta(SketchMedidasView.Tool.SELECT)
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
        // Extender, recortar y mover imantado son modos: se quedan puestos para encadenar varias
        // operaciones y los suelta Nulo, otra herramienta u otro panel. El mismo botón los apaga.
        binding.btnExtenderPanelMedida.setOnClickListener { alternarModoEdicion(SketchMedidasView.ModoEdicion.EXTENDER) }
        binding.btnRecortarPanelMedida.setOnClickListener { alternarModoEdicion(SketchMedidasView.ModoEdicion.RECORTAR) }
        binding.btnMoverImanPanelMedida.setOnClickListener { alternarModoEdicion(SketchMedidasView.ModoEdicion.MOVER_IMAN) }
        // Copiar, cortar y pegar: entre vistas también (lo dibujado por error en la frontal se
        // corta y se pega en la planta, sin volverlo a dibujar).
        binding.btnCopiarPanelMedida.setOnClickListener {
            if (binding.sketchMedidas.copiarSeleccion()) mostrar("Copiado. Ve a la vista donde va y toca Pegar")
            else mostrar("Selecciona primero qué copiar (Selec)")
        }
        binding.btnCortarPortapapelesPanelMedida.setOnClickListener {
            if (binding.sketchMedidas.cortarSeleccion()) mostrar("Cortado. Ve a la vista donde va y toca Pegar")
            else mostrar("Selecciona primero qué cortar (Selec)")
        }
        binding.btnPegarPanelMedida.setOnClickListener {
            if (binding.sketchMedidas.pegar()) {
                ocultarPanelesFlotantes()
                seleccionarHerramienta(SketchMedidasView.Tool.SELECT)
                mostrar("Pegado y seleccionado: arrástralo a su sitio")
            } else mostrar("No hay nada copiado")
        }
        binding.btnEstiloLineaPanelMedida.setOnClickListener { mostrarDialogoEstiloLinea() }
        binding.btn3dPanelMedida.setOnClickListener { alternarMedidas3d() }
        binding.sketchMedidas.alCambiarDibujo = { refrescarPerspectivaGenerada() }
        binding.btnTabFormasBasicas.setOnClickListener { seleccionarTabFormas(true) }
        binding.btnTabFormasRecurrentes.setOnClickListener { seleccionarTabFormas(false) }
        binding.btnPlantillaPuerta.setOnClickListener { insertarPuertaEstandar() }
        binding.btnPlantillaPuerta.setOnLongClickListener {
            mostrarDialogoPlantilla("Puerta", incluyeBisagra = true, incluyeApertura = true, incluyeVista = true)
            true
        }
        binding.btnPlantillaVentana.setOnClickListener { insertarVentanaEstandar() }
        binding.btnPlantillaVentana.setOnLongClickListener {
            mostrarDialogoPlantilla("Ventana", incluyeBisagra = true, incluyeApertura = true, incluyeVista = true)
            true
        }
        binding.btnPlantillaVentanaEsquina.setOnClickListener { insertarVentanaEsquinaEstandar() }
        binding.btnPlantillaVentanaCurva.setOnClickListener { insertarVentanaCurvaEstandar() }
        binding.btnPlantillaRopero.setOnClickListener { insertarRoperoEstandar() }
        binding.btnPlantillaRopero.setOnLongClickListener { mostrarDialogoRopero(); true }
        binding.btnPlantillaMampara.setOnClickListener { insertarMamparaEstandar() }
        binding.btnPlantillaMampara.setOnLongClickListener {
            mostrarDialogoPlantilla("Mampara", incluyeBisagra = false, incluyeApertura = false, incluyeVista = true, hojasPorDefecto = 2)
            true
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
        cerrarPanelesAlTocarElDibujo()
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

    private fun hayPanelAbierto(): Boolean = listOf(
        binding.panelSuperiorMedida,
        binding.panelFormasMedida,
        binding.panelEngraMedida,
        binding.panelArchivoMedida,
        binding.panelOperacionesMedida,
        binding.panelPlantillasMedida
    ).any { it.visibility == View.VISIBLE }

    /**
     * Tocar el dibujo cierra el panel que estuviera abierto.
     *
     * El panel tapa media pantalla y hasta ahora solo se iba volviendo a pulsar su botón; lo que
     * sale natural es apartarlo tocando fuera. Ese primer toque solo cierra —no dibuja ni
     * selecciona— para no dejar un trazo suelto por quitarse el panel de encima.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun cerrarPanelesAlTocarElDibujo() {
        binding.sketchMedidas.setOnTouchListener { _, evento ->
            if (evento.actionMasked == MotionEvent.ACTION_DOWN && hayPanelAbierto()) {
                ocultarPanelesFlotantes()
                true
            } else {
                false
            }
        }
    }

    private fun ocultarPanelesFlotantes() {
        binding.panelSuperiorMedida.visibility = View.GONE
        binding.panelFormasMedida.visibility = View.GONE
        binding.panelEngraMedida.visibility = View.GONE
        binding.panelArchivoMedida.visibility = View.GONE
        binding.panelOperacionesMedida.visibility = View.GONE
        binding.panelPlantillasMedida.visibility = View.GONE
        soltarHerramientaActiva()
    }

    /**
     * Irse a otro botón apaga la herramienta que estuviera activa. El lápiz (normal o imán) se
     * quedaba prendido al abrir plantillas u otro panel, y el siguiente toque en la pantalla
     * dibujaba sin que nadie lo pidiera. Los botones de herramienta cierran los paneles antes de
     * activarse, así que este apagado no los pisa.
     */
    private fun soltarHerramientaActiva() {
        if (herramientaActual != SketchMedidasView.Tool.NONE) {
            seleccionarHerramienta(SketchMedidasView.Tool.NONE)
        }
        // La cota a escuadra también se queda puesta, y mientras lo está se come los toques del
        // lienzo. Irse a otro botón la suelta, igual que suelta el lápiz.
        binding.sketchMedidas.cancelarCotaAEscuadra()
        binding.sketchMedidas.cancelarModoEdicion()
    }

    /**
     * Grosor y trazo de las líneas seleccionadas. Se elige con dos filas de opciones y se aplica
     * al aceptar; el diálogo arranca con el estilo que ya tiene la primera figura elegida.
     */
    private fun mostrarDialogoEstiloLinea() {
        val actual = binding.sketchMedidas.estiloDeLaSeleccion()
        if (actual == null) {
            mostrar("Selecciona primero una línea (herramienta Selec)")
            return
        }
        val dp = resources.displayMetrics.density
        val grosores = listOf("Fina" to 0.6f, "Normal" to 1f, "Gruesa" to 2f, "Muy gruesa" to 3f)
        val trazos = listOf(
            "Continuo" to EstiloDeLinea.CONTINUO,
            "Punteado" to EstiloDeLinea.PUNTEADO,
            "Trazos" to EstiloDeLinea.TRAZOS,
            "Trazo y punto" to EstiloDeLinea.TRAZO_PUNTO
        )
        val contenido = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((20 * dp).toInt(), (8 * dp).toInt(), (20 * dp).toInt(), 0)
        }
        fun grupo(titulo: String, opciones: List<String>, elegida: Int): RadioGroup {
            contenido.addView(TextView(this).apply {
                text = titulo
                setPadding(0, (10 * dp).toInt(), 0, (2 * dp).toInt())
            })
            val grupo = RadioGroup(this).apply { orientation = RadioGroup.VERTICAL }
            opciones.forEachIndexed { i, nombre ->
                grupo.addView(RadioButton(this).apply {
                    id = View.generateViewId()
                    text = nombre
                    isChecked = i == elegida
                })
            }
            contenido.addView(grupo)
            return grupo
        }
        val grosorElegido = grosores.indexOfFirst { abs(it.second - actual.first) < 0.05f }.coerceAtLeast(0)
        val trazoElegido = trazos.indexOfFirst { it.second == actual.second }.coerceAtLeast(0)
        val grupoGrosor = grupo("Grosor", grosores.map { it.first }, grosorElegido)
        val grupoTrazo = grupo("Trazo", trazos.map { it.first }, trazoElegido)
        AlertDialog.Builder(this)
            .setTitle("Estilo de línea")
            .setView(contenido)
            .setPositiveButton("Aplicar") { _, _ ->
                val g = grupoGrosor.indexOfChild(grupoGrosor.findViewById(grupoGrosor.checkedRadioButtonId))
                val t = grupoTrazo.indexOfChild(grupoTrazo.findViewById(grupoTrazo.checkedRadioButtonId))
                binding.sketchMedidas.aplicarEstiloASeleccion(
                    grosores.getOrNull(g)?.second, trazos.getOrNull(t)?.second
                )
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Qué cota poner. Las dos salen de una esquina y llegan a escuadra a un lado: la normal, al
     * lado de enfrente; la de prolongación, también al lado que se queda corto, con una sombra
     * del lado hasta donde cae la escuadra.
     */
    private fun mostrarDialogoCotas() {
        val opciones = arrayOf(
            "A escuadra · de una esquina al lado de enfrente",
            "A la prolongación · al lado que no llega (con sombra)",
            "Mover o cambiar de estilo una cota"
        )
        AlertDialog.Builder(this)
            .setTitle("Cotas")
            .setItems(opciones) { _, cual ->
                if (cual == 2) binding.sketchMedidas.activarModoEdicion(SketchMedidasView.ModoEdicion.MOVER_COTA)
                else binding.sketchMedidas.activarCotaAEscuadra(prolongacion = cual == 1)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ==================== MEDIDAS 3D: LAS VISTAS DEL APUNTE ====================
    // El apunte de siempre es la frontal. Con el botón 3D aparece la tira de vistas —frontal,
    // superior, izquierda, derecha y perspectiva— como las ventanas de un programa de 3D: la que
    // está en el lienzo va resaltada, las demás enseñan su miniatura y se tocan para pasar a
    // ellas. De todas juntas sale la ventana de esquina al enviarla.

    private var modo3d = false
    private val celdasDeVista = HashMap<SketchMedidasView.Vista, Pair<View, android.widget.ImageView>>()

    private fun alternarMedidas3d() {
        ocultarPanelesFlotantes()
        if (modo3d && binding.sketchMedidas.tieneVistas()) {
            AlertDialog.Builder(this)
                .setTitle("Cerrar las vistas")
                .setMessage("Las vistas superior, laterales y perspectiva se conservan guardadas. ¿Quitarlas del apunte?")
                .setPositiveButton("Solo ocultar") { _, _ -> ponerModo3d(false) }
                .setNegativeButton("Quitarlas") { _, _ ->
                    binding.sketchMedidas.quitarVistas()
                    ponerModo3d(false)
                }
                .setNeutralButton("Cancelar", null)
                .show()
            return
        }
        ponerModo3d(!modo3d)
    }

    private fun ponerModo3d(activo: Boolean) {
        modo3d = activo
        binding.panelVistasMedida.visibility = if (activo) View.VISIBLE else View.GONE
        if (activo) {
            if (binding.filaVistasMedida.childCount == 0) armarTiraDeVistas()
            refrescarTiraDeVistas()
        } else if (binding.sketchMedidas.vistaActiva != SketchMedidasView.Vista.FRONTAL) {
            binding.sketchMedidas.cambiarAVista(SketchMedidasView.Vista.FRONTAL)
        }
    }

    private fun armarTiraDeVistas() {
        val dp = resources.displayMetrics.density
        val fila = binding.filaVistasMedida
        SketchMedidasView.Vista.values().forEach { v ->
            val celda = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = android.view.Gravity.CENTER_HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setPadding((2 * dp).toInt(), 0, (2 * dp).toInt(), 0)
            }
            val rotulo = TextView(this).apply {
                text = v.rotulo
                textSize = 9f
                setTextColor(Color.parseColor("#455A64"))
                maxLines = 1
            }
            val miniatura = android.widget.ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams((56 * dp).toInt(), (44 * dp).toInt())
                scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
                setBackgroundResource(R.drawable.bg_control_panel)
                contentDescription = "Vista ${v.rotulo}"
            }
            celda.addView(rotulo)
            celda.addView(miniatura)
            celda.setOnClickListener { irAVista(v) }
            fila.addView(celda)
            celdasDeVista[v] = celda to miniatura
        }
        // Desde dónde se mira: dentro (lo normal) o fuera. Decide qué pared es la izquierda.
        val celdaVista = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = android.view.Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val rotuloVista = TextView(this).apply {
            text = "Se mira desde"
            textSize = 9f
            setTextColor(Color.parseColor("#455A64"))
            maxLines = 1
        }
        val botonVista = Button(this).apply {
            id = View.generateViewId()
            textSize = 11f
            isAllCaps = false
            minHeight = 0
            minimumHeight = 0
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (44 * dp).toInt())
            setOnClickListener {
                binding.sketchMedidas.vistaInterior = !binding.sketchMedidas.vistaInterior
                refrescarTiraDeVistas()
            }
        }
        celdaVista.addView(rotuloVista)
        celdaVista.addView(botonVista)
        fila.addView(celdaVista)
        botonInteriorExterior = botonVista
    }

    private var botonInteriorExterior: Button? = null

    private fun irAVista(v: SketchMedidasView.Vista) {
        ocultarPanelesFlotantes()
        binding.sketchMedidas.cambiarAVista(v)
        binding.sketchMedidas.post { binding.sketchMedidas.fitContentInView() }
        refrescarTiraDeVistas()
    }

    /** Las miniaturas al día y la vista del lienzo resaltada; el aviso si es la perspectiva. */
    private fun refrescarTiraDeVistas() {
        val activa = binding.sketchMedidas.vistaActiva
        val dp = resources.displayMetrics.density
        val generada = perspectivaGenerada()
        celdasDeVista.forEach { (v, celda) ->
            val (contenedor, miniatura) = celda
            val bmp = if (v == SketchMedidasView.Vista.PERSPECTIVA && generada != null) {
                bitmapDelVolumen(generada, (56 * dp).toInt(), (44 * dp).toInt())
            } else {
                binding.sketchMedidas.miniaturaDeVista(v, (56 * dp).toInt(), (44 * dp).toInt())
            }
            miniatura.setImageBitmap(bmp)
            contenedor.alpha = if (v == activa) 1f else 0.6f
            miniatura.setBackgroundColor(if (v == activa) Color.parseColor("#B3E5FC") else Color.parseColor("#F7F4EC"))
        }
        val enPerspectiva = activa == SketchMedidasView.Vista.PERSPECTIVA
        binding.tvAvisoPerspectiva.visibility = if (enPerspectiva && generada == null) View.VISIBLE else View.GONE
        botonInteriorExterior?.text = if (binding.sketchMedidas.vistaInterior) "Interior" else "Exterior"
        mostrarPerspectivaGenerada(if (enPerspectiva) generada else null)
        refrescarPerspectivaRopero(enPerspectiva)
    }

    /**
     * Solo lo que depende del dibujo que acaba de cambiar: la perspectiva generada (superpuesta
     * y en su miniatura). Las demás miniaturas se rehacen al cambiar de vista, que es caro.
     */
    private var refrescoPendiente = false
    private fun refrescarPerspectivaGenerada() {
        if (!modo3d || refrescoPendiente) return
        refrescoPendiente = true
        binding.sketchMedidas.post {
            refrescoPendiente = false
            val generada = perspectivaGenerada()
            val dp = resources.displayMetrics.density
            celdasDeVista[SketchMedidasView.Vista.PERSPECTIVA]?.second?.setImageBitmap(
                if (generada != null) bitmapDelVolumen(generada, (56 * dp).toInt(), (44 * dp).toInt())
                else binding.sketchMedidas.miniaturaDeVista(SketchMedidasView.Vista.PERSPECTIVA, (56 * dp).toInt(), (44 * dp).toInt())
            )
            val enPerspectiva = binding.sketchMedidas.vistaActiva == SketchMedidasView.Vista.PERSPECTIVA
            mostrarPerspectivaGenerada(if (enPerspectiva) generada else null)
            refrescarPerspectivaRopero(enPerspectiva)
        }
    }

    // ---- El ropero de melamina en perspectiva: su 3D oblicuo sobre la vista Perspectiva ----

    private var roperoGenerado: crystal.crystal.taller.melamina.VistaRopero? = null

    /**
     * Si el apunte tiene un ropero (su plantilla en la frontal) y se está en la vista Perspectiva,
     * se enseña en 3D encima del lienzo, con sus puertas o su interior según se dejó en la
     * frontal; la miniatura de la pestaña también. Sin ropero, o en otra vista, se quita.
     */
    private fun refrescarPerspectivaRopero(enPerspectiva: Boolean) {
        val ropero = binding.sketchMedidas.roperoDelDibujo()
        if (ropero != null) {
            val dp = resources.displayMetrics.density
            celdasDeVista[SketchMedidasView.Vista.PERSPECTIVA]?.second?.setImageBitmap(bitmapDelRopero(ropero, (56 * dp).toInt(), (44 * dp).toInt()))
        }
        if (ropero == null || !enPerspectiva || binding.sketchMedidas.vistaTieneDibujo(SketchMedidasView.Vista.PERSPECTIVA)) {
            roperoGenerado?.visibility = View.GONE
            return
        }
        val vista = roperoGenerado ?: crystal.crystal.taller.melamina.VistaRopero(this).also { nueva ->
            val lp = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(0, 0).apply {
                topToTop = binding.sketchMedidas.id
                bottomToBottom = binding.sketchMedidas.id
                startToStart = binding.sketchMedidas.id
                endToEnd = binding.sketchMedidas.id
            }
            binding.layoutPrincipal.addView(nueva, lp)
            nueva.elevation = 2 * resources.displayMetrics.density
            nueva.setBackgroundColor(android.graphics.Color.parseColor("#F7F4EC"))
            nueva.en3d = true
            // Tocar el mueble en 3D alterna puertas e interior, que es lo que se quiere comparar.
            nueva.alTocarCuerpo = { nueva.mostrarPuertas = !nueva.mostrarPuertas }
            roperoGenerado = nueva
        }
        vista.ropero = ropero
        vista.mostrarPuertas = ropero.verPuertas
        vista.visibility = View.VISIBLE
        vista.bringToFront()
    }

    private fun bitmapDelRopero(ropero: crystal.crystal.taller.melamina.Ropero, ancho: Int, alto: Int): android.graphics.Bitmap {
        val v = crystal.crystal.taller.melamina.VistaRopero(this)
        v.ropero = ropero
        v.en3d = true
        v.mostrarPuertas = ropero.verPuertas
        v.measure(
            View.MeasureSpec.makeMeasureSpec(ancho * 4, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(alto * 4, View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, ancho * 4, alto * 4)
        val grande = android.graphics.Bitmap.createBitmap(ancho * 4, alto * 4, android.graphics.Bitmap.Config.ARGB_8888)
        v.draw(android.graphics.Canvas(grande))
        return android.graphics.Bitmap.createScaledBitmap(grande, ancho, alto, true)
    }

    // ---- La perspectiva generada: la ventana que va saliendo de las vistas, en 3D ----
    // Mientras la vista Perspectiva no tenga dibujo a mano, enseña la ventana que sale de la
    // planta y las alzadas, y se va poniendo al día con cada trazo. Así se ve lo que se está
    // haciendo. Quien quiera dibujarla a mano toca "dibujar a mano" y la generada se quita.

    private var volumenGenerado: crystal.crystal.Diseno.nova.VistaVolumenNova? = null
    private var chipPerspectiva: TextView? = null
    private var perspectivaAMano = false

    /** El paquete de la ventana que sale de las vistas, si hay planta y la perspectiva está vacía. */
    private fun perspectivaGenerada(): String? {
        if (perspectivaAMano) return null
        if (binding.sketchMedidas.vistaTieneDibujo(SketchMedidasView.Vista.PERSPECTIVA)) return null
        return binding.sketchMedidas.paqueteDeVistasParaVolumen()
    }

    private fun bitmapDelVolumen(paquete: String, ancho: Int, alto: Int): android.graphics.Bitmap? {
        val v = crystal.crystal.Diseno.nova.VistaVolumenNova(this)
        if (!v.mostrar(paquete)) return null
        v.measure(
            View.MeasureSpec.makeMeasureSpec(ancho * 4, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(alto * 4, View.MeasureSpec.EXACTLY)
        )
        v.layout(0, 0, ancho * 4, alto * 4)
        val grande = android.graphics.Bitmap.createBitmap(ancho * 4, alto * 4, android.graphics.Bitmap.Config.ARGB_8888)
        v.draw(android.graphics.Canvas(grande))
        return android.graphics.Bitmap.createScaledBitmap(grande, ancho, alto, true)
    }

    private fun mostrarPerspectivaGenerada(paquete: String?) {
        if (paquete == null) {
            volumenGenerado?.visibility = View.GONE
        } else {
            val vista = volumenGenerado ?: crystal.crystal.Diseno.nova.VistaVolumenNova(this).also { nueva ->
                val lp = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(0, 0).apply {
                    topToTop = binding.sketchMedidas.id
                    bottomToBottom = binding.sketchMedidas.id
                    startToStart = binding.sketchMedidas.id
                    endToEnd = binding.sketchMedidas.id
                }
                binding.layoutPrincipal.addView(nueva, lp)
                nueva.elevation = 2 * resources.displayMetrics.density
                // Tocar una pared la cambia de lado de la esquina: hacia quien mira o hacia afuera.
                nueva.alTocarPared = { tramo ->
                    val motivo = binding.sketchMedidas.invertirPliegueDelTramo(tramo)
                    if (motivo != null) {
                        android.widget.Toast.makeText(this, motivo, android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        refrescarPerspectivaGenerada()
                        android.widget.Toast.makeText(this, "Pared cambiada de lado", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
                volumenGenerado = nueva
            }
            // Al ponerla al día no se le cambia el punto de vista al usuario: se conserva lo girado.
            val giro = vista.giroGrados
            val elevacion = vista.elevacionGrados
            val zoom = vista.zoom
            val despX = vista.desplazamientoX
            val despY = vista.desplazamientoY
            val yaMirada = vista.visibility == View.VISIBLE
            vista.mostrar(paquete)
            if (yaMirada) {
                vista.giroGrados = giro
                vista.elevacionGrados = elevacion
                vista.zoom = zoom
                vista.desplazamientoX = despX
                vista.desplazamientoY = despY
            }
            vista.visibility = View.VISIBLE
        }
        refrescarChipGenerado(paquete != null)
        refrescarChipProyeccion(paquete != null)
    }

    /** Isométrica o perspectiva, a elegir sobre la misma vista generada. */
    private var chipProyeccion: TextView? = null

    private fun refrescarChipProyeccion(visible: Boolean) {
        val volumen = volumenGenerado
        if (!visible || volumen == null) {
            chipProyeccion?.visibility = View.GONE
            return
        }
        val dp = resources.displayMetrics.density
        val chip = chipProyeccion ?: TextView(this).apply {
            textSize = 11f
            setTextColor(Color.parseColor("#0D47A1"))
            setBackgroundResource(R.drawable.bg_control_panel)
            setPadding((10 * dp).toInt(), (6 * dp).toInt(), (10 * dp).toInt(), (6 * dp).toInt())
            elevation = 3 * dp
            val lp = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomToBottom = binding.sketchMedidas.id
                endToEnd = binding.sketchMedidas.id
                bottomMargin = (8 * dp).toInt()
                rightMargin = (8 * dp).toInt()
            }
            binding.layoutPrincipal.addView(this, lp)
            chipProyeccion = this
            setOnClickListener {
                volumenGenerado?.let { v -> v.perspectiva = !v.perspectiva }
                refrescarChipProyeccion(true)
            }
        }
        chip.text = if (volumen.perspectiva) "◉ perspectiva · pellizca para acercar" else "▱ isométrica · toca para perspectiva"
        chip.visibility = View.VISIBLE
    }

    /**
     * El rótulo de lo generado en la vista de ahora: en la perspectiva, que se gira y que un toque
     * pasa a dibujar a mano; en la planta, que un toque la convierte en líneas para editarla; en
     * una lateral, dónde está el canto con la frontal. Sin nada generado, no se ve.
     */
    private fun refrescarChipGenerado(conPerspectiva: Boolean) {
        val vista = binding.sketchMedidas.vistaActiva
        val texto: String? = when {
            vista == SketchMedidasView.Vista.PERSPECTIVA && conPerspectiva ->
                "◰ perspectiva generada · arrastra para girar · toca una pared lateral para cambiarla de lado · pellizca para acercar · toca aquí para dibujar a mano"
            vista == SketchMedidasView.Vista.SUPERIOR && binding.sketchMedidas.enseniaAlgoGenerado() ->
                "planta generada de las alzadas · toca aquí para editarla a mano"
            (vista == SketchMedidasView.Vista.IZQUIERDA || vista == SketchMedidasView.Vista.DERECHA) &&
                binding.sketchMedidas.enseniaAlgoGenerado() ->
                "dibuja esta pared de frente: arranca en el canto con la frontal, en el cero"
            else -> null
        }
        if (texto == null) {
            chipPerspectiva?.visibility = View.GONE
            return
        }
        val dp = resources.displayMetrics.density
        val chip = chipPerspectiva ?: TextView(this).apply {
            textSize = 11f
            setTextColor(Color.parseColor("#455A64"))
            setBackgroundResource(R.drawable.bg_control_panel)
            setPadding((10 * dp).toInt(), (6 * dp).toInt(), (10 * dp).toInt(), (6 * dp).toInt())
            elevation = 3 * dp
            val lpChip = androidx.constraintlayout.widget.ConstraintLayout.LayoutParams(
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT,
                androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topToTop = binding.sketchMedidas.id
                startToStart = binding.sketchMedidas.id
                endToEnd = binding.sketchMedidas.id
                topMargin = (8 * dp).toInt()
            }
            binding.layoutPrincipal.addView(this, lpChip)
            chipPerspectiva = this
        }
        chip.text = texto
        chip.visibility = View.VISIBLE
        chip.setOnClickListener {
            when (binding.sketchMedidas.vistaActiva) {
                SketchMedidasView.Vista.PERSPECTIVA -> {
                    perspectivaAMano = true
                    refrescarTiraDeVistas()
                }
                SketchMedidasView.Vista.SUPERIOR -> {
                    if (binding.sketchMedidas.materializarPlantaGenerada()) {
                        binding.sketchMedidas.post { binding.sketchMedidas.fitContentInView() }
                        refrescarTiraDeVistas()
                    }
                }
                else -> Unit
            }
        }
    }

    /** El mismo botón prende el modo y lo apaga; hay que mirar ANTES de cerrar los paneles. */
    private fun alternarModoEdicion(modo: SketchMedidasView.ModoEdicion) {
        val estaba = binding.sketchMedidas.modoEdicionActivo == modo
        ocultarPanelesFlotantes()
        if (estaba) return
        binding.sketchMedidas.activarModoEdicion(modo)
    }

    private fun seleccionarHerramientaDesdePanel(tool: SketchMedidasView.Tool) {
        ocultarPanelesFlotantes()
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

    /**
     * La puerta se dibuja de un toque con lo de siempre: 90 x 240, puente a 200, bisagras a la
     * izquierda, vista interior, abre hacia adentro y michi de 0.5. El diálogo de la plantilla
     * (medidas de entrada y hojas) sigue estando en la pulsación larga.
     */
    private fun insertarPuertaEstandar() {
        ocultarPanelesFlotantes()
        binding.sketchMedidas.insertarPlantillaPuerta()
        productoActual = "Puerta"
        actualizarPanelInformacion()
    }

    /**
     * La ventana también se dibuja de un toque: 150 x 120 con su puente, una cota de alto por
     * dentro cada 120 cm de ancho y el alfeizar en 90. El diálogo de siempre queda en la pulsación
     * larga.
     */
    private fun insertarVentanaEstandar() {
        ocultarPanelesFlotantes()
        binding.sketchMedidas.insertarPlantillaVentana()
        productoActual = "Ventana"
        actualizarPanelInformacion()
    }

    /**
     * Ventana que dobla en esquina, en desarrollo: dos tramos (150 y 120) con su arista a 90°, la
     * cota de alto clavada en la esquina y el ancho de cada tramo al pie.
     */
    private fun insertarVentanaEsquinaEstandar() {
        ocultarPanelesFlotantes()
        binding.sketchMedidas.insertarPlantillaVentanaEsquina()
        productoActual = "Ventana"
        actualizarPanelInformacion()
    }

    /**
     * Ventana curva, en desarrollo: dos tramos de 90 con su arco, que el vidriero parte en los
     * que haga falta y acota uno a uno.
     *
     * Se parte porque una ventana curva de obra casi nunca es el arco de un círculo perfecto: cada
     * trozo lleva su propio desarrollo y su cuerda, medidos contra la pared.
     */
    private fun insertarVentanaCurvaEstandar() {
        ocultarPanelesFlotantes()
        binding.sketchMedidas.insertarPlantillaVentanaCurva()
        productoActual = "Ventana"
        actualizarPanelInformacion()
    }

    /** La mampara se toma como la ventana; cambian las medidas: 210 x 240 con el puente a 200. */
    private fun insertarMamparaEstandar() {
        ocultarPanelesFlotantes()
        binding.sketchMedidas.insertarPlantillaMampara()
        productoActual = "Mampara"
        actualizarPanelInformacion()
    }

    /**
     * El ropero de melamina, de un toque: 240 x 240 x 60 con dos cuerpos. Después todo se edita
     * tocando sus cotas y sus rótulos en el dibujo (el hueco, los cuerpos, lo que lleva cada uno,
     * las puertas), y en la vista Perspectiva del 3D se ve el mueble en volumen.
     */
    private fun insertarRoperoEstandar(ropero: crystal.crystal.taller.melamina.Ropero = crystal.crystal.taller.melamina.Ropero()) {
        ocultarPanelesFlotantes()
        binding.sketchMedidas.insertarPlantillaRopero(ropero)
        productoActual = "Ropero"
        actualizarPanelInformacion()
        refrescarPerspectivaGenerada()
    }

    /** La pulsación larga en Ropero: el hueco, los cuerpos y las puertas de una vez, como en las demás plantillas. */
    private fun mostrarDialogoRopero() {
        val dp = resources.displayMetrics.density
        fun campo(rotulo: String, valor: String) = android.widget.EditText(this).apply {
            hint = rotulo
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(valor)
            setSelectAllOnFocus(true)
        }
        val etAncho = campo("Ancho del hueco (cm)", "240")
        val etAlto = campo("Alto (cm)", "240")
        val etFondo = campo("Fondo (cm)", "60")
        val etCuerpos = campo("Cuerpos", "2")
        val etMaletero = campo("Maletero (cm, 0 = sin)", "0")
        val tipos = crystal.crystal.taller.melamina.TipoPuertas.values()
        val spPuertas = android.widget.Spinner(this).apply {
            adapter = android.widget.ArrayAdapter(this@MedidaActivity, android.R.layout.simple_spinner_dropdown_item, tipos.map { it.etiqueta })
            setSelection(tipos.indexOf(crystal.crystal.taller.melamina.TipoPuertas.BATIENTES))
        }
        val caja = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding((20 * dp).toInt(), (8 * dp).toInt(), (20 * dp).toInt(), 0)
            addView(etAncho); addView(etAlto); addView(etFondo); addView(etCuerpos); addView(etMaletero)
            addView(TextView(this@MedidaActivity).apply { text = "Puertas"; textSize = 12f })
            addView(spPuertas)
        }
        fun num(et: android.widget.EditText, porDefecto: Float) = et.text.toString().replace(",", ".").toFloatOrNull() ?: porDefecto
        AlertDialog.Builder(this)
            .setTitle("Ropero de melamina")
            .setView(caja)
            .setPositiveButton("Insertar") { _, _ ->
                val ropero = crystal.crystal.taller.melamina.Ropero(
                    anchoCm = num(etAncho, 240f), altoCm = num(etAlto, 240f), fondoCm = num(etFondo, 60f),
                    maleteroCm = num(etMaletero, 0f).coerceIn(0f, 120f),
                    puertas = tipos[spPuertas.selectedItemPosition.coerceIn(0, tipos.lastIndex)]
                ).conCuerposIguales(num(etCuerpos, 2f).toInt().coerceIn(1, 8))
                insertarRoperoEstandar(ropero)
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
            SketchMedidasView.Tool.ARCO -> "Arco"
            SketchMedidasView.Tool.ARCO_90 -> "Arco 90"
            SketchMedidasView.Tool.MAGNET_PEN -> "Lapiz iman"
            SketchMedidasView.Tool.NODO -> "Nodos"
        }
        actualizarEstadoIconosFormas(tool)
    }

    private fun actualizarEstadoIconosFormas(tool: SketchMedidasView.Tool) {
        val estados = listOf(
            binding.btnFormaLapiz to (tool == SketchMedidasView.Tool.FREEHAND),
            binding.btnFormaLapizIman to (tool == SketchMedidasView.Tool.MAGNET_PEN),
            binding.btnFormaRectangulo to (tool == SketchMedidasView.Tool.RECTANGLE),
            binding.btnFormaTriangulo to (tool == SketchMedidasView.Tool.TRIANGLE),
            binding.btnFormaCirculo to (tool == SketchMedidasView.Tool.CIRCLE),
            binding.btnFormaTexto to (tool == SketchMedidasView.Tool.TEXT),
            binding.btnFormaLinea to (tool == SketchMedidasView.Tool.LINE),
            binding.btnFormaLinea90 to (tool == SketchMedidasView.Tool.ORTHO_LINE),
            binding.btnFormaArco to (tool == SketchMedidasView.Tool.ARCO),
            binding.btnFormaArco90 to (tool == SketchMedidasView.Tool.ARCO_90),
            binding.btnFormaNodos to (tool == SketchMedidasView.Tool.NODO)
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
            SketchMedidasView.Tool.ORTHO_LINE,
            SketchMedidasView.Tool.ARCO,
            SketchMedidasView.Tool.ARCO_90,
            SketchMedidasView.Tool.MAGNET_PEN,
            SketchMedidasView.Tool.NODO
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
        val opciones = arrayOf("Reflejar la misma", "Hacer una copia", "Varias copias")
        AlertDialog.Builder(this)
            .setTitle("Espejo")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> if (!binding.sketchMedidas.mirrorSelectedInPlace()) mostrar("Selecciona una figura")
                    1 -> if (!binding.sketchMedidas.mirrorCopySelected()) mostrar("Selecciona una figura")
                    2 -> pedirCantidadCopiasEspejo()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun pedirCantidadCopiasEspejo() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = "Cantidad (1-20)"
            setText("2")
            setSelectAllOnFocus(true)
        }
        AlertDialog.Builder(this)
            .setTitle("Varias copias")
            .setView(input)
            .setPositiveButton("Crear") { _, _ ->
                val n = input.text?.toString()?.trim()?.toIntOrNull()?.coerceIn(1, 20) ?: 1
                if (!binding.sketchMedidas.mirrorCopiesSelected(n)) mostrar("Selecciona una figura")
            }
            .setNegativeButton("Cancelar", null)
            .show()
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
        preguntarSiCorrige { sobrescribir ->
            conProductoDeLaMedida(sobrescribir) { producto ->
                guardarApunte(producto, sobrescribir)?.let { texto ->
                    onGuardado?.invoke(texto)
                }
            }
        }
    }

    private fun mostrarDialogoProductoParaArchivar() {
        conProyectoDestino { archivarConProducto() }
    }

    private fun archivarConProducto() {
        if (!validarLienzoParaMedida()) return
        preguntarSiCorrige { sobrescribir ->
            conProductoDeLaMedida(sobrescribir) { producto ->
                archivarMedida(producto, sobrescribir)
            }
        }
    }

    /**
     * Al corregir, el producto ya lo trae la medida archivada: se reutiliza y no se vuelve a
     * preguntar. Si no, se mira si el propio dibujo lo declara —una puerta puesta desde su
     * plantilla ya se sabe lo que es—. Solo se pregunta cuando ni una cosa ni la otra.
     */
    private fun conProductoDeLaMedida(sobrescribir: Boolean, accion: (String) -> Unit) {
        val yaTiene = if (sobrescribir) bocetoActualEnIndice()?.producto?.trim().orEmpty() else ""
        if (yaTiene.isNotBlank()) {
            accion(yaTiene)
            return
        }
        val delDibujo = binding.sketchMedidas.productoDelDibujo()
        if (!delDibujo.isNullOrBlank()) {
            productoActual = delDibujo
            actualizarPanelInformacion()
            accion(delDibujo)
            return
        }
        mostrarDialogoProducto(accion)
    }

    /**
     * Si el dibujo que hay en el lienzo salió de una medida YA archivada, pregunta si se la está
     * corrigiendo o si es otra distinta.
     *
     * Hasta ahora solo se podía añadir: guardar siempre creaba una medida más, así que para
     * corregir una había que dibujarla de nuevo y borrar la vieja. Se pregunta en vez de decidirlo
     * solo porque las dos cosas son legítimas —corregir la que se abrió, o partir de ella para una
     * parecida— y equivocarse hacia el lado de sobrescribir borraría trabajo hecho.
     */
    private fun preguntarSiCorrige(accion: (sobrescribir: Boolean) -> Unit) {
        val destino = bocetoActualDesdeArchivo
        val txt = archivoActualTxt
        val archivada = destino != null && txt != null &&
            cargarIndiceBocetos(txt.parentFile, txt.nameWithoutExtension).any { it.archivo == destino }
        if (!archivada) {
            accion(false)
            return
        }
        val opciones = arrayOf(
            "Corregir esta medida\nReemplaza el dibujo y la descripción de la que abriste.",
            "Guardar como otra medida\nDeja la anterior como está y añade una nueva al proyecto."
        )
        AlertDialog.Builder(this)
            .setTitle("Esta medida ya está guardada")
            .setItems(opciones) { _, cual -> accion(cual == 0) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarProyecto() {
        conProyectoDestino {
            val notas = binding.etNotasMedida.text?.toString()?.trim().orEmpty()
            if (!binding.sketchMedidas.hasDrawing()) {
                if (notas.isBlank()) {
                    mostrar("Dibuja una medida antes de guardar")
                    return@conProyectoDestino
                }
                guardarTextoProyecto(productoActual.ifBlank { "Proyecto medidas" }, notas)
                return@conProyectoDestino
            }
            mostrarDialogoProductoParaGuardar()
        }
    }

    /**
     * Decide a qué proyecto va la medida cuando todavía no hay cliente puesto.
     *
     * Escribir otra vez el mismo nombre no reabre el proyecto: crea uno nuevo al lado, y la medida
     * se queda separada de las que ya se tomaron para ese cliente. Por eso se pregunta: o se suma a
     * un proyecto que ya existe —y entonces se elige de la lista— o se empieza uno nuevo con su
     * nombre. Con el cliente ya puesto no se pregunta nada: se sigue donde se estaba.
     */
    private fun conProyectoDestino(accion: () -> Unit) {
        if (clienteActual().isNotBlank()) {
            accion()
            return
        }
        val proyectos = proyectosGuardados()
        if (proyectos.isEmpty()) {
            pedirClienteNuevo(accion)
            return
        }
        val opciones = arrayOf(
            "Agregar a un proyecto\nSe suma a las medidas que ese proyecto ya tiene.",
            "Crear un proyecto nuevo\nSe pide el nombre del cliente."
        )
        AlertDialog.Builder(this)
            .setTitle("¿A dónde va esta medida?")
            .setItems(opciones) { _, cual ->
                if (cual == 0) elegirProyectoDestino(proyectos, accion) else pedirClienteNuevo(accion)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun proyectosGuardados(): List<File> = obtenerDirectorioMedidas()
        .listFiles { file -> file.extension.equals("txt", ignoreCase = true) }
        ?.sortedByDescending { it.lastModified() }
        .orEmpty()

    private fun elegirProyectoDestino(archivos: List<File>, accion: () -> Unit) {
        val nombres = archivos.map { it.nameWithoutExtension }
        AlertDialog.Builder(this)
            .setTitle("Agregar a un proyecto")
            .setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)) { _, cual ->
                if (adjuntarAProyecto(archivos[cual])) accion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun pedirClienteNuevo(accion: () -> Unit) {
        val input = EditText(this).apply {
            hint = "Cliente"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            setSingleLine(true)
        }
        AlertDialog.Builder(this)
            .setTitle("Proyecto nuevo")
            .setView(input)
            .setPositiveButton("Continuar") { _, _ ->
                val nombre = input.text?.toString()?.trim().orEmpty()
                if (nombre.isBlank()) {
                    mostrar("Ingresa el nombre del cliente")
                    return@setPositiveButton
                }
                binding.etClienteMedida.setText(nombre)
                // Proyecto nuevo de verdad: no se queda pegado a ningún archivo abierto antes.
                archivoActualTxt = null
                bocetoActualDesdeArchivo = null
                actualizarPanelInformacion()
                accion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Apunta el trabajo a un proyecto ya guardado sin tocar el lienzo: se traen su cliente y sus
     * notas, y lo que está dibujado ahora entra como una medida MÁS de ese proyecto.
     */
    private fun adjuntarAProyecto(txtFile: File): Boolean = runCatching {
        val texto = txtFile.readText()
        binding.etClienteMedida.setText(
            extraerCampo(texto, "Cliente").ifBlank { txtFile.nameWithoutExtension }
        )
        binding.etNotasMedida.setText(extraerNotas(texto))
        archivoActualTxt = txtFile
        bocetoActualDesdeArchivo = null
        actualizarPanelInformacion()
        mostrar("Se agrega a ${txtFile.nameWithoutExtension}")
        true
    }.getOrElse {
        mostrar("No se pudo abrir el proyecto: ${it.message}")
        false
    }

    private fun guardarApunte(producto: String, sobrescribir: Boolean = false): String? {
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
        return guardarTextoProyecto(producto, textoGenerado, sobrescribir)?.also {
            binding.etNotasMedida.setText(it)
        }
    }

    private fun archivarMedida(producto: String, sobrescribir: Boolean = false) {
        if (!validarLienzoParaMedida()) return
        val notasActuales = binding.etNotasMedida.text?.toString()?.trim().orEmpty()
        // Al corregir una medida, su línea vieja sale de las notas antes de escribir la nueva: si
        // no, el proyecto acabaría con las dos, la corregida y la que ya no vale.
        val notas = if (sobrescribir) quitarBloque(notasActuales, descripcionBocetoActual()) else notasActuales
        val medida = construirBloqueMedida(producto) ?: run {
            mostrar("No se pudo leer ancho y alto del dibujo")
            return
        }
        val textoArchivado = unirBloques(notas, medida)
        guardarTextoProyecto(producto, textoArchivado, sobrescribir)?.let {
            binding.etNotasMedida.setText(it)
            binding.etInfoProductoMedida.setText("")
            binding.sketchMedidas.clear()
            bocetoActualDesdeArchivo = null
            seleccionarHerramienta(SketchMedidasView.Tool.NONE)
            mostrar("Medida archivada")
        }
    }

    private fun guardarTextoProyecto(
        producto: String,
        texto: String,
        sobrescribir: Boolean = false
    ): String? {
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
                val descripcion = construirBloqueMedida(producto) ?: productoConInfoActual(producto)
                // Corrección de una medida ya archivada: se reescribe SU archivo y su entrada del
                // índice, así que la corregida ocupa el lugar de la vieja en vez de sumarse.
                val corregido = bocetoActualDesdeArchivo
                    ?.takeIf { sobrescribir }
                    ?.let { nombre -> bocetos.indexOfFirst { it.archivo == nombre }.takeIf { it >= 0 } }
                val sketchFile = if (corregido != null) {
                    File(dir, bocetos[corregido].archivo)
                } else {
                    File(dir, "${baseName}_${System.currentTimeMillis()}.json")
                }
                sketchFile.writeText(binding.sketchMedidas.exportEditableState())
                val entrada = BocetoProyecto(
                    archivo = sketchFile.name,
                    producto = productoConInfoActual(producto),
                    descripcion = descripcion,
                    fecha = System.currentTimeMillis()
                )
                if (corregido != null) bocetos[corregido] = entrada else bocetos.add(entrada)
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
        val dialogo = AlertDialog.Builder(this)
            .setTitle("Abrir medidas (toque largo: eliminar)")
            .setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)) { _, which ->
                abrirApunte(archivos[which])
            }
            .setNeutralButton("Eliminar varias") { _, _ -> mostrarDialogoEliminarProyectos(archivos) }
            .setNegativeButton("Cancelar", null)
            .create()
        dialogo.show()
        dialogo.listView.setOnItemLongClickListener { _, _, posicion, _ ->
            dialogo.dismiss()
            confirmarEliminarProyectos(listOf(archivos[posicion]))
            true
        }
    }

    /** Selección múltiple para limpiar de una vez los proyectos viejos o ya terminados. */
    private fun mostrarDialogoEliminarProyectos(archivos: List<File>) {
        val nombres = archivos.map { it.nameWithoutExtension }.toTypedArray()
        val marcadas = BooleanArray(archivos.size)
        AlertDialog.Builder(this)
            .setTitle("Eliminar medidas guardadas")
            .setMultiChoiceItems(nombres, marcadas) { _, cual, marcada -> marcadas[cual] = marcada }
            .setPositiveButton("Eliminar") { _, _ ->
                val elegidos = archivos.filterIndexed { indice, _ -> marcadas[indice] }
                if (elegidos.isEmpty()) mostrar("No marcaste ningun proyecto")
                else confirmarEliminarProyectos(elegidos)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarEliminarProyectos(archivos: List<File>) {
        val detalle = archivos.joinToString("\n") { "• ${it.nameWithoutExtension}" }
        AlertDialog.Builder(this)
            .setTitle(if (archivos.size == 1) "Eliminar proyecto" else "Eliminar ${archivos.size} proyectos")
            .setMessage("Se borran el texto guardado, el indice y todos los dibujos. No se puede deshacer.\n\n$detalle")
            .setPositiveButton("Eliminar") { _, _ ->
                val borrados = archivos.count { eliminarProyectoMedidas(it) }
                mostrar(if (borrados == 1) "Proyecto eliminado" else "Eliminados: $borrados")
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Borra el .txt del proyecto, su índice de bocetos y cada dibujo asociado. */
    private fun eliminarProyectoMedidas(txtFile: File): Boolean = runCatching {
        val dir = txtFile.parentFile ?: obtenerDirectorioMedidas()
        val baseName = txtFile.nameWithoutExtension
        cargarIndiceBocetos(dir, baseName).forEach { boceto ->
            File(dir, boceto.archivo).takeIf { it.exists() }?.delete()
        }
        File(dir, nombreIndiceBocetos(baseName)).takeIf { it.exists() }?.delete()
        // Dibujos sueltos que nunca llegaron al índice: se guardan como "<baseName>_<fecha>.json".
        dir.listFiles { f -> f.name.startsWith("${baseName}_") && f.extension.equals("json", true) }
            ?.forEach { it.delete() }
        txtFile.delete()
        if (archivoActualTxt?.absolutePath == txtFile.absolutePath) limpiarProyectoEnPantalla()
        true
    }.getOrElse {
        mostrar("No se pudo eliminar ${txtFile.nameWithoutExtension}: ${it.message}")
        false
    }

    private fun limpiarProyectoEnPantalla() {
        archivoActualTxt = null
        productoActual = ""
        bocetoActualDesdeArchivo = null
        binding.etInfoProductoMedida.setText("")
        binding.etNotasMedida.setText("")
        actualizarPanelInformacion()
        binding.sketchMedidas.clear()
        seleccionarHerramienta(SketchMedidasView.Tool.NONE)
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
                    mostrarSelectorBocetos(
                        bocetos, txtFile.parentFile, "Elige medida", txtFile.nameWithoutExtension
                    )
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
        mostrarSelectorBocetos(
            bocetos, txtFile.parentFile, "Medidas archivadas", txtFile.nameWithoutExtension
        )
    }

    private fun mostrarSelectorBocetos(
        bocetos: List<BocetoProyecto>,
        dir: File?,
        titulo: String,
        baseName: String
    ) {
        val opciones = bocetos.mapIndexed { index, boceto -> etiquetaBoceto(index, boceto) }.toTypedArray()
        val dialogo = AlertDialog.Builder(this)
            .setTitle("$titulo (toque largo: cambiar o eliminar)")
            .setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, opciones)) { _, which ->
                cargarBocetoEnLienzo(bocetos[which], dir)
            }
            .setNeutralButton("Lienzo limpio") { _, _ ->
                limpiarLienzoActual()
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialogo.show()
        dialogo.listView.setOnItemLongClickListener { _, _, posicion, _ ->
            dialogo.dismiss()
            mostrarAccionesBoceto(bocetos, posicion, dir, baseName, titulo)
            true
        }
    }

    /**
     * Acciones sobre una medida archivada. Cambiar el producto es lo que decide a qué calculadora se
     * envía después (sirve cuando durante la venta se pasa de Nova a Ventana Aluminio, por ejemplo);
     * eliminar saca del proyecto las medidas viejas o ya terminadas.
     */
    private fun mostrarAccionesBoceto(
        bocetos: List<BocetoProyecto>,
        posicion: Int,
        dir: File?,
        baseName: String,
        tituloOrigen: String
    ) {
        val boceto = bocetos.getOrNull(posicion) ?: return
        AlertDialog.Builder(this)
            .setTitle(etiquetaBoceto(posicion, boceto))
            .setItems(arrayOf("Cambiar producto", "Eliminar esta medida")) { _, cual ->
                when (cual) {
                    0 -> cambiarProductoBoceto(bocetos, posicion, dir, baseName, tituloOrigen)
                    1 -> confirmarEliminarBoceto(bocetos, posicion, dir, baseName, tituloOrigen)
                }
            }
            .setNegativeButton("Volver") { _, _ ->
                mostrarSelectorBocetos(bocetos, dir, tituloOrigen, baseName)
            }
            .show()
    }

    private fun confirmarEliminarBoceto(
        bocetos: List<BocetoProyecto>,
        posicion: Int,
        dir: File?,
        baseName: String,
        tituloOrigen: String
    ) {
        val boceto = bocetos.getOrNull(posicion) ?: return
        AlertDialog.Builder(this)
            .setTitle("Eliminar medida")
            .setMessage(
                "Se borrara «${etiquetaBoceto(posicion, boceto)}» de este proyecto, con su dibujo. " +
                    "No se puede deshacer."
            )
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarBoceto(bocetos, posicion, dir, baseName, tituloOrigen)
            }
            .setNegativeButton("Cancelar") { _, _ ->
                mostrarSelectorBocetos(bocetos, dir, tituloOrigen, baseName)
            }
            .show()
    }

    private fun eliminarBoceto(
        bocetos: List<BocetoProyecto>,
        posicion: Int,
        dir: File?,
        baseName: String,
        tituloOrigen: String
    ) {
        val boceto = bocetos.getOrNull(posicion) ?: return
        val destino = dir ?: obtenerDirectorioMedidas()
        val restantes = bocetos.toMutableList().apply { removeAt(posicion) }
        val borrado = runCatching {
            File(destino, boceto.archivo).takeIf { it.exists() }?.delete()
            guardarIndiceBocetos(destino, baseName, restantes)
            quitarBloqueDeNotas(File(destino, "$baseName.txt"), boceto.descripcion, baseName)
        }
        if (borrado.isFailure) {
            mostrar("No se pudo eliminar la medida: ${borrado.exceptionOrNull()?.message}")
            return
        }
        // Si era la medida que estaba en el lienzo, no tiene sentido seguir editando algo borrado.
        if (bocetoActualDesdeArchivo == boceto.archivo) {
            bocetoActualDesdeArchivo = null
            binding.sketchMedidas.clear()
        }
        mostrar("Medida eliminada")
        if (restantes.isNotEmpty()) mostrarSelectorBocetos(restantes, dir, tituloOrigen, baseName)
    }

    /**
     * Quita del .txt el bloque de texto que se archivó junto con esa medida. Solo actúa si el bloque
     * está tal cual quedó guardado: si las notas se editaron a mano, se dejan intactas.
     */
    private fun quitarBloqueDeNotas(txtFile: File, bloque: String, baseName: String) {
        val objetivo = bloque.trim()
        if (objetivo.isBlank() || !txtFile.exists()) return
        val texto = txtFile.readText()
        val notas = extraerNotas(texto)
        if (!notas.contains(objetivo)) return
        val nuevas = notas.split("\n\n")
            .filter { it.trim() != objetivo }
            .joinToString("\n\n")
            .trim()
        txtFile.writeText(
            construirTextoArchivo(
                extraerCampo(texto, "Cliente"),
                extraerCampo(texto, "Producto"),
                nuevas,
                extraerCampo(texto, "Boceto"),
                nombreIndiceBocetos(baseName)
            )
        )
        if (archivoActualTxt?.absolutePath == txtFile.absolutePath) {
            binding.etNotasMedida.setText(nuevas)
            actualizarPanelInformacion()
        }
    }

    private fun cambiarProductoBoceto(
        bocetos: List<BocetoProyecto>,
        posicion: Int,
        dir: File?,
        baseName: String,
        tituloOrigen: String
    ) {
        val boceto = bocetos.getOrNull(posicion) ?: return
        val opciones = productosMedida.toMutableList().apply { add("Otro (escribir)") }
        AlertDialog.Builder(this)
            .setTitle("Producto actual: ${boceto.producto.ifBlank { "sin producto" }}")
            .setItems(opciones.toTypedArray()) { _, cual ->
                if (cual == opciones.lastIndex) {
                    pedirProductoLibre(boceto.producto) { texto ->
                        aplicarProductoBoceto(bocetos, posicion, dir, baseName, tituloOrigen, texto, true)
                    }
                } else {
                    aplicarProductoBoceto(bocetos, posicion, dir, baseName, tituloOrigen, opciones[cual], false)
                }
            }
            .setNegativeButton("Volver") { _, _ ->
                mostrarSelectorBocetos(bocetos, dir, tituloOrigen, baseName)
            }
            .show()
    }

    private fun pedirProductoLibre(actual: String, onListo: (String) -> Unit) {
        val input = EditText(this).apply {
            setText(actual)
            setSelection(text.length)
        }
        AlertDialog.Builder(this)
            .setTitle("Nombre del producto")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val texto = input.text?.toString()?.trim().orEmpty()
                if (texto.isBlank()) mostrar("El nombre no puede quedar vacio") else onListo(texto)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun aplicarProductoBoceto(
        bocetos: List<BocetoProyecto>,
        posicion: Int,
        dir: File?,
        baseName: String,
        tituloOrigen: String,
        nuevoProducto: String,
        reemplazarTodo: Boolean
    ) {
        val boceto = bocetos.getOrNull(posicion) ?: return
        val productoFinal = if (reemplazarTodo) nuevoProducto
                            else reemplazarProductoBase(boceto.producto, nuevoProducto)
        val actualizados = bocetos.toMutableList().apply {
            this[posicion] = boceto.copy(producto = productoFinal)
        }
        val guardado = runCatching {
            guardarIndiceBocetos(dir ?: obtenerDirectorioMedidas(), baseName, actualizados)
        }
        if (guardado.isFailure) {
            mostrar("No se pudo guardar el producto: ${guardado.exceptionOrNull()?.message}")
            return
        }
        // Si la medida editada es la que está en el lienzo, el panel debe reflejar el cambio.
        if (bocetoActualDesdeArchivo == boceto.archivo) {
            productoActual = nuevoProducto
            actualizarPanelInformacion()
        }
        mostrar(
            if (EnrutadorPresupuesto.destinoPara(productoFinal) == null)
                "Producto: $productoFinal (sin calculadora asociada)"
            else "Producto: $productoFinal"
        )
        mostrarSelectorBocetos(actualizados, dir, tituloOrigen, baseName)
    }

    /**
     * Cambia solo el nombre del producto y conserva la información que lo acompaña: el índice guarda
     * "Nova Corrediza Vna1", así que reemplazar la cadena entera perdería el "Vna1".
     */
    private fun reemplazarProductoBase(actual: String, nuevo: String): String {
        val texto = actual.trim()
        val base = productosMedida
            .filter { !it.equals("Otro", ignoreCase = true) }
            .sortedByDescending { it.length }
            .firstOrNull { texto.startsWith(it, ignoreCase = true) }
            ?: return nuevo
        val resto = texto.substring(base.length).trim()
        return listOf(nuevo, resto).filter { it.isNotBlank() }.joinToString(" ")
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
            // Un apunte con vistas abre con su tira puesta.
            if (binding.sketchMedidas.tieneVistas()) ponerModo3d(true)
            bocetoActualDesdeArchivo = nombreBoceto?.takeIf { it.isNotBlank() } ?: file.name
            binding.sketchMedidas.post { binding.sketchMedidas.fitContentInView() }
        }
        return cargado
    }

    private fun enviarAMainActivity() {
        // Candado (Fase 3): enviar las medidas al presupuesto es de pago.
        if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.avanzadoActivo(),
                "Enviar las medidas al presupuesto es una función de pago.")) return
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

    // ==================== ENVIAR A CALCULADORAS ====================
    // Elige un cliente de los guardados y manda sus medidas archivadas a la calculadora que
    // corresponde a cada una según su clasificación (nova -> Nova Corrediza, puerta -> Puertas, etc.).

    private fun elegirClienteParaCalculadoras() {
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
            .setTitle("Enviar a calculadoras")
            .setAdapter(ArrayAdapter(this, android.R.layout.simple_list_item_1, nombres)) { _, which ->
                enviarProyectoACalculadoras(archivos[which])
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun enviarProyectoACalculadoras(txtFile: File) {
        val dir = txtFile.parentFile ?: obtenerDirectorioMedidas()
        val bocetos = cargarIndiceBocetos(dir, txtFile.nameWithoutExtension)
        // Enviable = tiene calculadora asociada y conserva su archivo de boceto.
        val enviables = bocetos.filter {
            EnrutadorPresupuesto.destinoPara(it.producto) != null && File(dir, it.archivo).exists()
        }
        if (enviables.isEmpty()) {
            mostrar("Ninguna medida se pudo enviar a una calculadora")
            return
        }
        if (enviables.size == 1) {
            lanzarColaCalculadoras(txtFile, enviables)
            return
        }
        mostrarSelectorMedidasParaCalculadoras(txtFile, enviables, bocetos.size - enviables.size)
    }

    /**
     * Deja elegir qué medidas del paquete se mandan a las calculadoras. Vienen todas marcadas: se
     * destildan las que se quieren obviar (medida repetida, que no se cotiza ahora, etc.).
     *
     * Antes se enviaba el paquete completo y la cola solo avanzaba al archivar, así que la única
     * forma de llegar a la segunda medida era archivar la primera.
     */
    private fun mostrarSelectorMedidasParaCalculadoras(
        txtFile: File,
        enviables: List<BocetoProyecto>,
        sinCalculadora: Int
    ) {
        val opciones = enviables.mapIndexed { index, boceto -> etiquetaBoceto(index, boceto) }.toTypedArray()
        val marcadas = BooleanArray(enviables.size) { true }
        var btEnviar: android.widget.Button? = null

        fun refrescarBotonEnviar() {
            val n = marcadas.count { it }
            btEnviar?.text = if (n > 0) "Enviar ($n)" else "Enviar"
            btEnviar?.isEnabled = n > 0
        }

        val dialogo = AlertDialog.Builder(this)
            .setTitle("Que medidas enviar")
            .setMultiChoiceItems(opciones, marcadas) { _, which, checked ->
                marcadas[which] = checked
                refrescarBotonEnviar()
            }
            .setPositiveButton("Enviar") { _, _ ->
                val elegidas = enviables.filterIndexed { index, _ -> marcadas[index] }
                if (elegidas.isEmpty()) mostrar("No marcaste ninguna medida")
                else lanzarColaCalculadoras(txtFile, elegidas)
            }
            .setNeutralButton("Ninguna", null)
            .setNegativeButton("Cancelar", null)
            .create()

        dialogo.show()

        btEnviar = dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
        refrescarBotonEnviar()
        // "Ninguna" desmarca todo sin cerrar el diálogo (para enviar solo una de muchas): se le pone
        // el listener después de show() justo para que no lo cierre.
        dialogo.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
            for (i in marcadas.indices) {
                marcadas[i] = false
                dialogo.listView.setItemChecked(i, false)
            }
            refrescarBotonEnviar()
        }
        if (sinCalculadora > 0) {
            mostrar("$sinCalculadora medida(s) sin calculadora quedaron fuera")
        }
    }

    private fun lanzarColaCalculadoras(txtFile: File, seleccionadas: List<BocetoProyecto>) {
        val cola = construirColaCalculadoras(txtFile, seleccionadas)
        if (cola.isEmpty()) {
            mostrar("Ninguna medida se pudo enviar a una calculadora")
            return
        }
        if (!ColaCalculadoras.lanzar(this, cola, 0)) {
            mostrar("No se pudo abrir la calculadora")
        }
    }

    // Carga cada boceto en el lienzo para leer su medida mayor real (medidaPrincipal toma el mayor
    // segmento horizontal y vertical) y renderiza su gráfico original a PNG para mostrarlo en ivDiseno.
    // Solo procesa las medidas [bocetos] que se eligieron enviar: renderizar es caro y las obviadas
    // no tienen por qué costar tiempo.
    private fun construirColaCalculadoras(
        txtFile: File,
        bocetos: List<BocetoProyecto>
    ): List<ColaCalculadoras.MedidaCalc> {
        val dir = txtFile.parentFile ?: obtenerDirectorioMedidas()
        val texto = runCatching { txtFile.readText() }.getOrDefault("")
        val cliente = extraerCampo(texto, "Cliente").ifBlank { txtFile.nameWithoutExtension }
        if (bocetos.isEmpty()) return emptyList()

        val estadoActual = if (binding.sketchMedidas.hasDrawing()) binding.sketchMedidas.exportEditableState() else null
        val graficosDir = File(cacheDir, "medidascalc").apply { mkdirs() }
        val resultado = mutableListOf<ColaCalculadoras.MedidaCalc>()

        try {
            bocetos.forEachIndexed { index, boceto ->
                if (EnrutadorPresupuesto.destinoPara(boceto.producto) == null) return@forEachIndexed
                val file = File(dir, boceto.archivo)
                if (!file.exists()) return@forEachIndexed

                binding.sketchMedidas.clear()
                val cargado = if (file.extension.equals("json", ignoreCase = true)) {
                    binding.sketchMedidas.loadEditableState(file.readText())
                } else {
                    binding.sketchMedidas.loadBackground(file)
                    true
                }
                if (!cargado) return@forEachIndexed
                val medida = binding.sketchMedidas.medidaPrincipal() ?: return@forEachIndexed

                val png = File(graficosDir, "orig_${System.currentTimeMillis()}_$index.png")
                val rutaGrafico = runCatching {
                    binding.sketchMedidas.exportBitmap().let { bmp ->
                        png.outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
                    }
                    png.absolutePath
                }.getOrDefault("")

                // El contorno del vano viaja con la medida: un vano escalonado no cabe en el ancho
                // y el alto, y la calculadora lo necesita para armar los tramos.
                val contorno = binding.sketchMedidas.contornoPrincipalEnCm()
                    ?.let { crystal.crystal.Diseno.nova.ContornoEnTramos.aTexto(it) }
                    .orEmpty()
                resultado.add(
                    ColaCalculadoras.MedidaCalc(
                        producto = boceto.producto,
                        ancho = medida.anchoCm,
                        alto = medida.altoCm,
                        cantidad = 1f,
                        cliente = cliente,
                        bocetoArchivo = rutaGrafico,
                        contorno = contorno,
                        // Y si es una ventana de esquina, sus lados: la calculadora los necesita
                        // para armarla en L, en C o en serie, que con el ancho total no se sabe.
                        esquina = binding.sketchMedidas.esquinaPrincipalEnCm()
                            ?.let { EsquinaMedida.aTexto(it) }
                            .orEmpty(),
                        // Y si esa esquina se armó sobre figuras dibujadas a mano, la forma de cada
                        // pared y sus parantes, que el ancho y el alto no cuentan.
                        contornosLados = LadosLibres.contornosATexto(binding.sketchMedidas.contornosDeLadosEnCm()),
                        parantesLados = LadosLibres.parantesATexto(binding.sketchMedidas.parantesDeLadosEnCm()),
                        // Y el ropero de melamina entero, si la medida es un ropero.
                        disenoRopero = binding.sketchMedidas.roperoDelDibujo()?.aJson().orEmpty()
                    )
                )
            }
        } finally {
            binding.sketchMedidas.clear()
            if (estadoActual != null) binding.sketchMedidas.loadEditableState(estadoActual)
            if (modo3d) refrescarTiraDeVistas()
            binding.sketchMedidas.post { binding.sketchMedidas.fitContentInView() }
        }
        return resultado
    }

    /**
     * La escoba borra, pero hay dos cosas muy distintas que borrar y confundirlas cuesta trabajo:
     * el dibujo que se tiene delante, o el apunte entero.
     *
     * Lo primero es lo que se hace a cada rato —terminar una medida y empezar la siguiente del
     * MISMO proyecto— y hasta ahora solo estaba en el panel de Archivo, donde no se encontraba. Va
     * primero y dicho con todas sus letras; el apunte nuevo queda debajo y sigue pidiendo
     * confirmación, que ahí sí se pierde el cliente y las notas.
     */
    private fun confirmarLimpiar() {
        val proyecto = archivoActualTxt?.nameWithoutExtension
        val seguirEn = clienteActual().ifBlank { proyecto.orEmpty() }
        val opciones = arrayOf(
            "Limpiar el lienzo\n" +
                if (seguirEn.isNotBlank()) {
                    "Borra el dibujo para tomar otra medida. Se conservan el cliente y las medidas " +
                        "ya archivadas: la siguiente se suma a $seguirEn."
                } else {
                    "Borra solo el dibujo. Las notas y el cliente se conservan."
                },
            "Limpiar todo\n" +
                "Borra el dibujo, las notas y el cliente: empieza un apunte nuevo."
        )
        AlertDialog.Builder(this)
            .setTitle("Limpiar apunte")
            .setItems(opciones) { _, cual ->
                if (cual == 0) confirmarLimpiarLienzoActual() else confirmarLimpiarTodo()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun confirmarLimpiarTodo() {
        AlertDialog.Builder(this)
            .setTitle("Limpiar todo")
            .setMessage("Se borrara el dibujo y las notas actuales, y el apunte deja de estar unido a este proyecto.")
            .setPositiveButton("Limpiar") { _, _ -> limpiarProyectoEnPantalla() }
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
        // Se dice a dónde va lo que se dibuje ahora: el lienzo en blanco no deja ver que se sigue
        // dentro del proyecto, y esa duda es la que hacía empezar de cero sin necesidad.
        val seguirEn = clienteActual()
        mostrar(
            if (archivoActualTxt != null && seguirEn.isNotBlank()) {
                "Lienzo limpio. La siguiente medida se suma a $seguirEn"
            } else {
                "Lienzo limpio"
            }
        )
    }

    /**
     * Manda al optimizador de corte cada lado del perímetro de lo que hay dibujado.
     *
     * Un apunte con varias figuras es una lista de piezas: lo que se acotó lado por lado es
     * justamente lo que hay que cortar, y hasta ahora había que volver a teclearlo en Cortes. Las
     * medidas iguales de una misma figura se juntan en una fila con su cantidad, que es como se
     * lee una lista de corte.
     */
    private fun enviarLadosACortes() {
        val lados = binding.sketchMedidas.ladosDelPerimetro()
        if (lados.isEmpty()) {
            mostrar("No hay figuras con medidas para cortar")
            return
        }
        val filas = lados
            .groupBy { Math.round(it.cm * 10f) / 10f to it.etiqueta }
            .toList()
            .sortedWith(compareBy({ it.first.second }, { -it.first.first }))
        val arreglo = JSONArray()
        filas.forEach { (clave, iguales) ->
            arreglo.put(
                JSONObject()
                    .put("l", clave.first.toDouble())
                    .put("c", iguales.size)
                    .put("r", clave.second.ifBlank { "-" })
            )
        }
        startActivity(
            Intent(this, crystal.crystal.optimizadores.corte.CorteActivity::class.java)
                .putExtra("piezas_cortes_json", arreglo.toString())
                // El apunte está en centímetros y en centímetros se lee en el taller.
                .putExtra("piezas_cortes_escala", "CENTIMETRO")
        )
        mostrar("Enviando ${lados.size} lado(s) a cortes")
    }

    private fun mostrarOpcionesCompartirMedida() {
        // Candado (Fase 3): compartir medidas es de pago. Se bloquea AQUÍ (antes del intent) para que
        // no se escape compartiendo por otras redes; el candado de servidor solo cubre crystal-crystal.
        if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.avanzadoActivo(),
                "Compartir medidas es una función de pago.")) return
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

    /** Quita del texto el bloque de una medida; los bloques van separados por una línea en blanco. */
    private fun quitarBloque(texto: String, bloque: String): String {
        val objetivo = bloque.trim()
        if (objetivo.isBlank()) return texto
        return texto.split("\n\n")
            .filter { it.trim() != objetivo }
            .joinToString("\n\n")
            .trim()
    }

    /** La medida archivada que se tiene abierta en el lienzo, tal y como está hoy en el índice. */
    private fun bocetoActualEnIndice(): BocetoProyecto? {
        val destino = bocetoActualDesdeArchivo ?: return null
        val txt = archivoActualTxt ?: return null
        return cargarIndiceBocetos(txt.parentFile, txt.nameWithoutExtension)
            .firstOrNull { it.archivo == destino }
    }

    /** La línea con la que está archivada hoy la medida que se tiene abierta en el lienzo. */
    private fun descripcionBocetoActual(): String = bocetoActualEnIndice()?.descripcion.orEmpty()

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
