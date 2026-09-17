package crystal.crystal.taller.nova

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.Diseno.nova.ContornoEnTramos
import crystal.crystal.Diseno.nova.DisenoNovaActivity
import crystal.crystal.R
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityNovaCorredizaBinding
import crystal.crystal.taller.CurvaEsquina
import crystal.crystal.taller.EsquinaMedida
import crystal.crystal.taller.LadosLibres
import crystal.crystal.taller.ColaCalculadoras
import crystal.crystal.taller.ModoMasivoHelper
import crystal.crystal.taller.NavegadorCola
import crystal.crystal.taller.nova.NovaUIHelper.esValido

class NovaCorrediza : AppCompatActivity() {

    enum class TipoNova { APA, INA, PIV }
    data class ModuloDesigual(val tipo: Char, val ancho: Float)

    private var tipoNova = TipoNova.APA
    private var cliente: String = ""
    private var tubo: Float = 1.5f
    private var puente: String = "M\u00FAltiple"
    private val puenteInaDefault = "tubo 2\u215C x 1"
    private val puenteNpDefault = "tubo 2 x 1"
    private var diseno: String = ""
    private var texto: String = ""
    private var otros: Boolean = false
    // Referencias compactables (en L / en C / serie): texto completo + estado de expansión.
    private var referenciasFull: String = ""
    private var referenciasColapsable = false
    private var referenciasExpandida = false
    // Nombres para tvNombreModelo (geometría + modelo, colores distintos)
    private var nombreGeometria: String = "Plano"
    private var nombreModelo: String = "normal"
    // Modulación: patrón de hojas del sistema (nn/ncfc/nff/nfc). Eje independiente.
    private var textoModelo: String = "nn"
    /** Imagen del modelo que se está mostrando; hace falta para poder volver a él. */
    private var drawableModelo: Int = R.drawable.ic_fichad3a
    /**
     * Modulación (nombre, imagen, token) de antes de pasar a circular.
     *
     * La forma circular no es una modulación, pero se implementa cambiando el modelo a `nci`
     * porque de ahí lo leen el dibujo y las franjas. Al volver a rectangular hay que devolver la
     * modulación que había: sin esto, `metaForma` volvía a "plano" pero el modelo se quedaba en
     * `nci` y el diseño seguía saliendo circular.
     */
    private var modulacionPrevia: Triple<String, Int, String>? = null
    // Full corredizas: cada cuántas corredizas va el parante que arma otro tramo.
    // 0 = automático (reparto balanceado como el clásico); >1 = forzar corte cada N.
    private var corredizasPorTramo: Int = 0
    // Remate (mochetas): nn=normal, nr=invertido, np=doble puente. Eje independiente.
    private var modeloRemate: String = "nn"
    private var nombreRemate: String = "normal"
    /**
     * Cuántas ventanas iguales lleva este producto. Llega de MedidaActivity y se puede cambiar en
     * el diálogo de opciones. Al archivar se guardan tantas copias como diga, cada una con su
     * propio número correlativo (cantidad 2 sobre la ventana 1 deja Vna2 y Vna3, y la siguiente
     * empieza en Vna4), de modo que el material queda multiplicado por esa cantidad.
     */
    private var cantidadProducto: Int = 1
    private var contadorLado = 1
    private var maxLados = -1
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var ultimoPaquete: String = ""
    private var primerClickArchivarRealizado = false
    private var spinnerListo = false
    private var actualizandoValorPuenteDesdeCodigo = false
    private var valorPuenteEditadoManualmente = false
    private var metaColorAluminio: String = ""
    private var metaTipoVidrio: String = ""
    private var metaAcabadoSuperficial: String = ""
    private var metaObservaciones: String = ""
    // Encuentro: lados que colindan (1) o quedan al vacío (0), orden Arriba-Derecha-Abajo-Izquierda.
    private var metaEncuentro: String = "1111"
    // Forma (vista frontal): plano | circular | poligonal.
    private var metaForma: String = "plano"
    // Esquinero (perfil en la esquina de geometrías compuestas): texto de opción o "ninguno".
    private var metaEsquinero: String = "tubo 2⅜ x 1"
    // Dirección de la geometría compuesta (por ahora solo afecta el dibujo): adentro | afuera.
    private var metaDireccion: String = "adentro"
    // Parante que cierra un lado al vacío (encuentro desmarcado). Es un "final", así que se elige
    // entre todos los perfiles MENOS gorrito y múltiple (esos son puentes entre paños, no cierres).
    private var metaParanteVacio: String = "tubo 2⅜ x 1"

    // Estado para divisiones desiguales (cargado desde DisenoNova)
    private var modulosDesiguales: List<ModuloDesigual> = emptyList()
    private var parantesDesiguales: List<Int> = emptyList()
    private var mochetaDesigual: List<ModuloDesigual> = emptyList()
    private var altoHojaDesigual: Float = 0f
    /** El contorno del vano de la medida en curso, con lo que se armó su diseño. */
    private var contornoMedida: String = ""
    private var hojaDelDiseno: Float = 0f
    private var medidaDelDiseno: Pair<Float, Float> = 0f to 0f
    // true mientras se cargan campos desde un diseño (evita que los TextWatchers que limpian
    // el estado desigual se disparen por los setText programáticos de la carga).
    private var cargandoDiseno = false
    /** La ventana de esquina que llegó del apunte: sus lados y lo que hay en cada arista. */
    private var esquinaDeLaMedida: EsquinaMedida? = null
    /**
     * Lo que además trajo una esquina armada sobre figuras: la silueta de cada pared que no es
     * rectángulo y los parantes marcados en cada una, por lado. Vacíos en las demás.
     */
    private var siluetasDeLaMedida: List<List<Pair<Float, Float>>?> = emptyList()
    private var parantesDeLaMedida: List<List<Float>> = emptyList()
    private var primeraMedidaNl: MedidaNl? = null

    private var primeraMedidaNu: MedidaNl? = null
    private var segundaMedidaNu: MedidaNl? = null
    private val ladosNs: MutableList<MedidaNl> = mutableListOf()
    // Navegación de serie: tras calcular se puede recorrer/editar cada lado de la serie.
    private var navegandoNs = false
    private var indiceNs = 0
    private var materialesNlArchivables: EstadoMateriales? = null

    // Medidas que vienen de MedidaActivity (cola de calculadoras).
    private var bocetoOriginalDrawable: Drawable? = null   // gráfico de la medida original
    private var disenoCalculadoGuardado: Drawable? = null   // diseño mostrado antes de ver el original
    private var mostrandoBocetoOriginal = false
    private var arrastreDownX = 0f
    private var arrastreDownY = 0f

    private data class MedidaNl(
        val ancho: Float,
        val alto: Float,
        val hoja: Float,
        val divisManual: Int
    )

    private val lanzarDiseno = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == RESULT_OK) {
            val uri = res.data?.data ?: return@registerForActivityResult
            if (uri.toString().endsWith(".svg")) {
                val input = contentResolver.openInputStream(uri)!!
                val svg = com.caverock.androidsvg.SVG.getFromInputStream(input)
                val picture = svg.renderToPicture()
                val drawable = android.graphics.drawable.PictureDrawable(picture)
                binding.ivDiseno.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                binding.ivDiseno.scaleType = ImageView.ScaleType.FIT_CENTER
                binding.ivDiseno.setImageDrawable(drawable)
            } else {
                binding.ivDiseno.setImageURI(uri)
            }
        }
    }

    private val lanzarDisenoInteractivo = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == RESULT_OK) {
            val paquete = res.data?.getStringExtra(DisenoNovaActivity.RESULT_PAQUETE) ?: return@registerForActivityResult
            cargarDesdePaqueteDiseno(paquete)
        }
    }

    private lateinit var binding: ActivityNovaCorredizaBinding
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaCorredizaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Tocar "Referencias y Cálculos" abre la calculadora flotante.
        crystal.crystal.calculadora.CalculadoraFlotante.instalarEnReferencias(this)
        cantidadProducto = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        // El reparto de tramos vive en NovaCalculos y lo comparten todas las ventanas: se limpia
        // al entrar para que no se arrastre el de la ventana anterior.
        NovaCalculos.repartoManual = null

        ProyectoManager.inicializarDesdeStorage(this)
        proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
            context = this,
            textViewProyecto = binding.tvProyectoActivo,
            activity = this
        )
        ProyectoUIHelper.configurarVisorProyectoActivo(this, binding.tvProyectoActivo)
        procesarIntentProyecto(intent)

        texto = "nn"
        diseno = "ic_fichad3a"
        otros = false
        modelos()
        configurarCliente()
        spinnerTubo()
        configurarLimpiezaDesigual()
        binding.fcLayout.visibility = View.GONE

        // Estado inicial aparente
        actualizarModo()
        calcular()
        metaColorAluminio = intent.getStringExtra("color_aluminio")?.trim().orEmpty()
        metaTipoVidrio = intent.getStringExtra("tipo_vidrio")?.trim().orEmpty()

        // Toggle modo al tocar título (cicla APA -> INA -> APA). Pivotante oculto en v1: se salta PIV.
        binding.tvTitulo.setOnClickListener {
            tipoNova = when (tipoNova) {
                TipoNova.APA -> TipoNova.INA
                TipoNova.INA -> TipoNova.APA
                TipoNova.PIV -> TipoNova.APA
            }
            actualizarModo()
        }

        binding.btArchivar.setOnClickListener {
            // Candado de suscripción PRIMERO: si es de pago y está bloqueado, mostrar la invitación
            // y salir sin diálogo de metadatos, sin avanzar la numeración ni el toast de "archivado".
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeArchivar(),
                    "Archivar es una función de pago. Renueva para guardar tus proyectos.")) {
                return@setOnClickListener
            }
            if (binding.etAncho.text.toString().isEmpty()) {
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación de seguridad: las medidas deben coincidir con las referencias
            val anchoET = binding.etAncho.text.toString()
            val altoET = binding.etAlto.text.toString()
            val referencias = binding.txReferencias.text.toString()
            if (!referencias.contains(anchoET) || !referencias.contains(altoET)) {
                Toast.makeText(this, "Las medidas no coinciden con las referencias. Recalcula.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!primerClickArchivarRealizado) {
                DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                    override fun onProyectoSeleccionado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true
                        binding.txC.text = nombreProyecto
                        val mapExistente = MapStorage.cargarProyecto(this@NovaCorrediza, nombreProyecto)
                        if (mapExistente != null) {
                            mapListas.clear()
                            mapListas.putAll(mapExistente)
                        }
                        ejecutarArchivado()
                    }
                    override fun onProyectoCreado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true
                        binding.txC.text = nombreProyecto
                        ejecutarArchivado()
                    }
                    override fun onProyectoEliminado(nombreProyecto: String) {
                        ProyectoUIHelper.actualizarVisorProyectoActivo(this@NovaCorrediza, binding.tvProyectoActivo)
                    }
                })
            } else {
                if (!ProyectoManager.hayProyectoActivo()) {
                    primerClickArchivarRealizado = false
                    Toast.makeText(this, "No hay proyecto activo. Selecciona uno.", Toast.LENGTH_SHORT).show()
                    DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                        override fun onProyectoSeleccionado(nombreProyecto: String) {
                            primerClickArchivarRealizado = true
                            binding.txC.text = nombreProyecto
                            val mapExistente = MapStorage.cargarProyecto(this@NovaCorrediza, nombreProyecto)
                            if (mapExistente != null) {
                                mapListas.clear()
                                mapListas.putAll(mapExistente)
                            }
                            ejecutarArchivado()
                        }
                        override fun onProyectoCreado(nombreProyecto: String) {
                            primerClickArchivarRealizado = true
                            binding.txC.text = nombreProyecto
                            ejecutarArchivado()
                        }
                        override fun onProyectoEliminado(nombreProyecto: String) {
                            ProyectoUIHelper.actualizarVisorProyectoActivo(this@NovaCorrediza, binding.tvProyectoActivo)
                        }
                    })
                    return@setOnClickListener
                }
                // Asegurar que txC tenga el proyecto activo antes de archivar
                binding.txC.text = ProyectoManager.getProyectoActivo() ?: ""
                ejecutarArchivado()
            }
        }

        binding.btCalcular.setOnLongClickListener {
            val mapListas = MapStorage.cargarMap(this)
            if (!mapListas.isNullOrEmpty()) {
                binding.etCruce.setText(mapListas.toString())
                Toast.makeText(this, "Map cargado del proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No se encontró un Map guardado en el proyecto activo", Toast.LENGTH_SHORT).show()
            }
            true
        }

        binding.btArchivar.setOnLongClickListener {
            if (!ProyectoManager.hayProyectoActivo()) {
                Toast.makeText(this, "No hay proyecto activo para guardar", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            MapStorage.guardarMap(this, mapListas)
            Toast.makeText(this, "Map guardado en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            true
        }

        binding.ivDiseno.setOnLongClickListener {
            abrirDisenoInteractivo()
            true
        }

        binding.txHpuente.setOnLongClickListener {
            mostrarLySpinner()
            true
        }

        binding.btOk.setOnClickListener {
            if (aplicarValorManualPuente()) {
                ocultarLySpinner()
            }
        }

        hacerCopiable(binding.textView28, "paquete_nova")
        hacerCopiable(binding.txPr, "diseno_simbolico_v2")
        // La U se copia con un toque: es la lista que más se consulta y se pasa a otro lado.
        hacerCopiable(binding.txU, "u_nova", conToque = true)

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAlto.setText(df1(it)) }

        // Los gestos sobre el diseño (arrastrar para elegir el reparto de tramos) valen siempre,
        // vengan las medidas de MedidaActivity o se escriban a mano aquí.
        configurarArrastreDiseno()
        // Cola de medidas que viene de MedidaActivity: mostrar el gráfico original.
        inicializarDesdeMedidas()
    }

    // ==================== TOGGLE MODO ====================
    @SuppressLint("SetTextI18n")
    private fun actualizarModo() {
        binding.tvTitulo.text = when (tipoNova) {
            TipoNova.APA -> "Nova Aparente ▸"
            TipoNova.INA -> "Nova Inaparente ▸"
            TipoNova.PIV -> "Nova Pivotante ▸"
        }

        // Indicador visual: color y backgrounds según tipo
        val colorTint = when (tipoNova) {
            TipoNova.APA -> R.color.color
            TipoNova.INA -> R.color.fucsia
            TipoNova.PIV -> R.color.verde
        }
        val cornerBg = when (tipoNova) {
            TipoNova.APA -> R.drawable.cornere
            TipoNova.INA -> R.drawable.corneref
            TipoNova.PIV -> R.drawable.cornerev
        }
        val cornerBg2 = when (tipoNova) {
            TipoNova.APA -> R.drawable.corner
            TipoNova.INA -> R.drawable.cornerf
            TipoNova.PIV -> R.drawable.cornerv
        }
        binding.lyPanelIzquierdo.setBackgroundResource(cornerBg2)
        binding.linearLayout21.setBackgroundResource(cornerBg2)
        binding.fichaTLayout.setBackgroundResource(cornerBg)
        binding.lyReferencias.setBackgroundResource(cornerBg)
        binding.tvTitulo.backgroundTintList = resources.getColorStateList(colorTint, theme)
        binding.textView28.backgroundTintList = resources.getColorStateList(colorTint, theme)
        binding.tvMedidas.setTextColor(resources.getColor(colorTint, theme))
        binding.textView33.setTextColor(resources.getColor(colorTint, theme))
        binding.txDatos.setTextColor(resources.getColor(colorTint, theme))

        // Cursores de los EditText
        val tintList = resources.getColorStateList(colorTint, theme)
        binding.etAncho.backgroundTintList = tintList
        binding.etAlto.backgroundTintList = tintList
        binding.etHoja.backgroundTintList = tintList
        binding.etPartes.backgroundTintList = tintList
        binding.etU.backgroundTintList = tintList
        binding.etCruce.backgroundTintList = tintList
        binding.etAncho2.backgroundTintList = tintList
        binding.etAlto2.backgroundTintList = tintList
        binding.etDivi2.backgroundTintList = tintList
        binding.etPuente2.backgroundTintList = tintList
        binding.etAncho3.backgroundTintList = tintList
        binding.etAlto3.backgroundTintList = tintList
        binding.etDivi3.backgroundTintList = tintList
        binding.etPuente3.backgroundTintList = tintList
        binding.etFlecha.backgroundTintList = tintList
        binding.etCuerda.backgroundTintList = tintList
        binding.etNmochetas.backgroundTintList = tintList
        binding.etMochetaInf.backgroundTintList = tintList

        // Spinner de tubo: solo visible en aparente
        binding.lySpinner.visibility = View.GONE // siempre oculto por defecto, se muestra con longclick
        // tLayout (Tee): solo aparente
        binding.tLayout.visibility = if (tipoNova == TipoNova.APA) View.VISIBLE else View.GONE
        // lyUf queda sin uso; fcLayout asume el rol de U felpero/Fijo-corre en inaparente.
        binding.lyUf.visibility = View.GONE
        // El spinner de tubo aplica a las tres novas
        if (puenteMultipleNoPermitido(puente)) {
            aplicarPuentePredeterminadoSinMultiple()
        }
        actualizarVisibilidadMochetaInferior()
    }

    // ==================== MENÚ ====================
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let { ProyectoUIHelper.agregarOpcionesMenuProyecto(it) }
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val manejado = ProyectoUIHelper.manejarSeleccionMenu(
            context = this,
            itemId = item.itemId,
            callback = proyectoCallback,
            onProyectoCambiado = {
                ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            }
        )
        return if (manejado) true else super.onOptionsItemSelected(item)
    }

    // ==================== PROYECTO ====================
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        procesarIntentProyecto(intent)
    }
    override fun onResume() {
        super.onResume()
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
    }
    private fun procesarIntentProyecto(intent: Intent) {
        val nombreProyecto = intent.getStringExtra("proyecto_nombre")
        val crearNuevo = intent.getBooleanExtra("crear_proyecto", false)
        val descripcionProyecto = intent.getStringExtra("proyecto_descripcion") ?: ""
        if (crearNuevo && !nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.crearProyecto(this, nombreProyecto, descripcionProyecto)) {
                ProyectoManager.setProyectoActivo(this, nombreProyecto)
                ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
                Toast.makeText(this, "Proyecto '$nombreProyecto' creado y activado", Toast.LENGTH_SHORT).show()
            }
        } else if (!nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.existeProyecto(this, nombreProyecto)) {
                ProyectoManager.setProyectoActivo(this, nombreProyecto)
                ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
                Toast.makeText(this, "Proyecto '$nombreProyecto' activado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ==================== CLIENTE ====================
    @SuppressLint("SetTextI18n")
    private fun configurarCliente() {
        val clienteNombre = intent.extras?.getString("rcliente")
        cliente = clienteNombre ?: ""

        // Si ya hay proyecto activo, usar ese nombre en txC
        val proyectoActual = ProyectoManager.getProyectoActivo() ?: ""
        if (proyectoActual.isNotEmpty()) {
            binding.txC.text = proyectoActual
            primerClickArchivarRealizado = true
        }

        // Si no viene cliente en el intent, no mostrar diálogos
        if (cliente.isEmpty()) return

        // Si ya hay proyecto activo que contiene el nombre del cliente, no preguntar
        if (proyectoActual.contains(cliente, ignoreCase = true)) return

        val callbackCliente = object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                binding.txC.text = nombreProyecto
                ProyectoUIHelper.actualizarVisorProyectoActivo(this@NovaCorrediza, binding.tvProyectoActivo)
            }
            override fun onProyectoCreado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                binding.txC.text = nombreProyecto
                ProyectoUIHelper.actualizarVisorProyectoActivo(this@NovaCorrediza, binding.tvProyectoActivo)
            }
            override fun onProyectoEliminado(nombreProyecto: String) {
                ProyectoUIHelper.actualizarVisorProyectoActivo(this@NovaCorrediza, binding.tvProyectoActivo)
            }
        }

        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackCliente, cliente)
        } else {
            AlertDialog.Builder(this)
                .setTitle("Cliente: $cliente")
                .setMessage("Proyecto activo: \"$proyectoActual\".\n¿Qué deseas hacer?")
                .setPositiveButton("Mantener") { d, _ ->
                    primerClickArchivarRealizado = true
                    binding.txC.text = proyectoActual
                    d.dismiss()
                }
                .setNegativeButton("Crear nuevo") { _, _ ->
                    DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackCliente, cliente)
                }
                .setCancelable(false)
                .show()
        }
    }

    // ==================== CALCULAR ====================
    @SuppressLint("SetTextI18n")
    private fun calcular() {
        binding.btCalcular.setOnClickListener {
            try {
                // Si se estaba consultando el gráfico original de MedidaActivity, volver al diseño.
                restaurarDisenoSiMostrandoOriginal()
                if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener
                // Si no hay modelo seleccionado, forzar selección abriendo el diálogo de opciones
                if (texto.isEmpty()) {
                    abrirDialogoOpcionesNova()
                    return@setOnClickListener
                }
                if (texto.isBlank()) texto = "nn"
                // Full corredizas: asegurar el agrupamiento por tramo elegido por el usuario.
                NovaCalculos.corredizasPorTramoNfc = corredizasPorTramo
                // ncfc/nfc en INA = un solo tramo (sin parantes que dividan).
                NovaCalculos.ncfcUnTramo = (tipoNova == TipoNova.INA && textoModelo == "ncfc")
                NovaCalculos.nfcUnTramo = (tipoNova == TipoNova.INA && textoModelo == "nfc")
                // Full corredizas inaparente: máximo 6 divisiones.
                if (NovaCalculos.nfcUnTramo) {
                    val anchoNfc = arcoCurvo(binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f)
                    val divM = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
                    if (NovaCalculos.divisiones(anchoNfc, divM) > 6) {
                        Toast.makeText(this, "Full corredizas inaparente: máximo 6 divisiones.", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                }
                // Si el puente actual no está permitido (p. ej. INA ncfc), pasar al predeterminado.
                if (puenteMultipleNoPermitido(puente)) aplicarPuentePredeterminadoSinMultiple()
                if (texto == "ns") {
                    asegurarCamposActivosDesdeUltimoNs()
                }

                // Validar medidas completas según geometría
                if (texto == "nu" && (primeraMedidaNu == null || segundaMedidaNu == null)) {
                    val faltantes = (if (primeraMedidaNu == null) 1 else 0) + (if (segundaMedidaNu == null) 1 else 0)
                    val medIngresadas = 3 - faltantes
                    Toast.makeText(this,
                        "En C requiere 3 medidas. Ingresadas: $medIngresadas/3. Usa 'Agregar' para cada lado.",
                        Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                if (texto == "nl" && primeraMedidaNl == null) {
                    Toast.makeText(this,
                        "En L requiere 2 medidas. Ingresada: 1/2. Usa 'Agregar' para el primer lado.",
                        Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // El vano con forma se rehace si cambió el alto de hoja: es el dato que decide dónde
                // cabe la corrediza.
                rehacerDisenoSiCambioLaHoja()

                if (modulosDesiguales.isNotEmpty()) {
                    calcularDesigual()
                } else {
                    actualizarTxPrConDisenoV2()
                    if (texto == "nl") {
                        calcularMaterialesNl()
                    } else if (texto == "nu") {
                        calcularMaterialesNu()
                    } else {
                        materialesNlArchivables = null
                        uTexto()
                        otrosAluminios()
                        vidriosTexto()
                        referencias()
                    }
                    aplicarVistaReferencias()
                    // En serie, habilitar la navegación/edición de los lados ya ingresados.
                    if (texto == "ns") activarNavegacionNs()

                    ultimoPaquete = disenoSimbolico()
                    if (texto in setOf("nl", "nu", "ns") && ancho() > 400f) {
                        Toast.makeText(this,
                            "En ventanas grandes la perspectiva puede no ser exacta",
                            Toast.LENGTH_LONG).show()
                    }
                    val intent = Intent(this, DisenoNovaActivity::class.java).apply {
                        putExtra(DisenoNovaActivity.EXTRA_PAQUETE, ultimoPaquete)
                        putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
                        putExtra(DisenoNovaActivity.EXTRA_OUTPUT_FORMAT, if (texto == "nl" || texto == "nu" || texto == "ns" || texto == "ncu" || texto == "nci" || ultimoPaquete.contains("P<2.5>") || modulosDesiguales.isNotEmpty()) "png" else "svg")
                        putExtra(DisenoNovaActivity.EXTRA_RET_PADDING_PX, 4)
                        putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, mochetaLateralDisenoNl())
                        putExtra(DisenoNovaActivity.EXTRA_ENCUENTRO_VACIO, metaEncuentro)
                putExtra(DisenoNovaActivity.EXTRA_DIRECCION, metaDireccion)
                        putExtra(DisenoNovaActivity.EXTRA_DIRECCION, metaDireccion)
                        putExtra(DisenoNovaActivity.EXTRA_US_CM, binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f)
                    }
                    lanzarDiseno.launch(intent)
                    binding.textView28.setText(R.string.referencias_y_c_lculos)
                }
            } catch (_: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ==================== CÁLCULO DESIGUAL ====================
    private data class EstadoCamposCalculo(
        val ancho: String,
        val alto: String,
        val hoja: String,
        val divis: String
    )

    private data class EstadoMateriales(
        val u: String,
        val p: String,
        val r: String,
        val fc: String,
        val pf: String,
        val te: String,
        val tope: String,
        val hache: String,
        val tubo: String,
        val vidrio: String,
        val referencias: String
    )

    private fun calcularMaterialesNl() {
        val primera = obtenerPrimeraMedidaNlEditable() ?: run {
            uTexto()
            otrosAluminios()
            vidriosTexto()
            referencias()
            return
        }
        val camposLado2 = capturarCamposCalculo()
        val lado2Medida = MedidaNl(
            ancho = camposLado2.ancho.toFloatOrNull() ?: 0f,
            alto = camposLado2.alto.toFloatOrNull() ?: 0f,
            hoja = camposLado2.hoja.toFloatOrNull() ?: 0f,
            divisManual = camposLado2.divis.toIntOrNull() ?: 0
        )
        val (primeraCalculo, lado2Calculo) = medidasCalculoNlApa(primera, lado2Medida)

        // Los campos llevan el ancho ÚTIL (con el descuento de esquina) para que los conteos y el
        // corte coincidan con el dibujo; la medida ORIGINAL solo se muestra en la referencia.
        aplicarMedidaEnCampos(conDivisionesDeLaMedidaReal(lado2Medida, lado2Calculo))
        uTexto()
        otrosAluminios()
        vidriosTexto()
        referencias(anchoRealMostrar = lado2Medida.ancho)
        val lado2 = capturarMateriales()

        aplicarMedidaEnCampos(conDivisionesDeLaMedidaReal(primera, primeraCalculo))
        uTexto()
        otrosAluminios()
        vidriosTexto()
        referencias(anchoRealMostrar = primera.ancho)
        val lado1 = capturarMateriales()

        val referenciasLado2 = lado2.referencias
        val referenciasLado1 = lado1.referencias

        restaurarCamposCalculo(camposLado2)
        val combinados = combinarMateriales(lado1, lado2)
        val altoParanteEsquina = maxOf(primera.alto, lado2Medida.alto)
        val combinadosConEsquina = combinados.copy(
            tubo = if (opcionEsquinero() != null) combinarTextoCantidades(
                combinados.tubo,
                "${df1(altoParanteEsquina)} = 1"
            ) else combinados.tubo,
            referencias = combinarReferenciasOriginalesNl(referenciasLado1, referenciasLado2)
        )
        val combinadosFiltrados = filtrarMaterialesValorCero(combinadosConEsquina)
        materialesNlArchivables = combinadosFiltrados
        aplicarMateriales(combinadosFiltrados)
    }

    private fun calcularMaterialesNu() {
        val ladoIzq = obtenerPrimeraMedidaNuEditable()
        val ladoCentro = obtenerSegundaMedidaNuEditable()
        if (ladoIzq == null || ladoCentro == null) {
            materialesNlArchivables = null
            uTexto()
            otrosAluminios()
            vidriosTexto()
            referencias()
            return
        }

        val camposActuales = capturarCamposCalculo()
        val ladoDer = MedidaNl(
            ancho = camposActuales.ancho.toFloatOrNull() ?: 0f,
            alto = camposActuales.alto.toFloatOrNull() ?: 0f,
            hoja = camposActuales.hoja.toFloatOrNull() ?: 0f,
            divisManual = camposActuales.divis.toIntOrNull() ?: 0
        )

        // Descuentos de esquina (solo aplican en APA). En C hay dos esquinas:
        // izquierda↔centro y centro↔derecha, por lo que el centro descuenta ambas.
        val (izqCalculo, centroEsquinaIzq) = medidasCalculoNlApa(ladoIzq, ladoCentro)
        val (centroEsquinaDer, derCalculo) = medidasCalculoNlApa(ladoCentro, ladoDer)
        val descuentoCentro = (ladoCentro.ancho - centroEsquinaIzq.ancho) +
            (ladoCentro.ancho - centroEsquinaDer.ancho)
        val centroCalculo = ladoCentro.copy(
            ancho = (ladoCentro.ancho - descuentoCentro).coerceAtLeast(0f)
        )

        // Los campos llevan el ancho ÚTIL (con los descuentos de esquina) para que los conteos y
        // el corte coincidan con el dibujo; la medida ORIGINAL solo se muestra en la referencia.
        aplicarMedidaEnCampos(conDivisionesDeLaMedidaReal(ladoDer, derCalculo))
        uTexto()
        otrosAluminios()
        vidriosTexto()
        referencias(anchoRealMostrar = ladoDer.ancho)
        val matDer = capturarMateriales()

        aplicarMedidaEnCampos(conDivisionesDeLaMedidaReal(ladoCentro, centroCalculo))
        uTexto()
        otrosAluminios()
        vidriosTexto()
        referencias(anchoRealMostrar = ladoCentro.ancho)
        val matCentro = capturarMateriales()

        aplicarMedidaEnCampos(conDivisionesDeLaMedidaReal(ladoIzq, izqCalculo))
        uTexto()
        otrosAluminios()
        vidriosTexto()
        referencias(anchoRealMostrar = ladoIzq.ancho)
        val matIzq = capturarMateriales()

        val referenciasDer = matDer.referencias
        val referenciasCentro = matCentro.referencias
        val referenciasIzq = matIzq.referencias

        restaurarCamposCalculo(camposActuales)

        val combinados = combinarMateriales(combinarMateriales(matIzq, matCentro), matDer)
        // Dos parantes de esquina (una por cada esquina de la C).
        val altoParanteEsquinaIzq = maxOf(ladoIzq.alto, ladoCentro.alto)
        val altoParanteEsquinaDer = maxOf(ladoCentro.alto, ladoDer.alto)
        val combinadosConEsquina = combinados.copy(
            tubo = if (opcionEsquinero() != null) combinarTextoCantidades(
                combinados.tubo,
                "${df1(altoParanteEsquinaIzq)} = 1",
                "${df1(altoParanteEsquinaDer)} = 1"
            ) else combinados.tubo,
            referencias = combinarReferenciasOriginalesNl(
                referenciasIzq, referenciasCentro, referenciasDer
            )
        )
        val combinadosFiltrados = filtrarMaterialesValorCero(combinadosConEsquina)
        materialesNlArchivables = combinadosFiltrados
        aplicarMateriales(combinadosFiltrados)
    }

    private fun combinarReferenciasOriginalesNl(vararg referencias: String): String {
        return referencias
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString("\n")
    }

    // Descuento de esquina (APA e INA). El perfil lo define el ESQUINERO (no el puente);
    // "ninguno" = sin descuento. Reparto INVERTIDO: la ventana más ancha recibe la parte
    // MENOR del aluminio y la más angosta la MAYOR.
    private fun medidasCalculoNlApa(primera: MedidaNl, segunda: MedidaNl): Pair<MedidaNl, MedidaNl> {
        // En INA no se descuenta NADA: ni el parante ni los puentes. Es la misma regla que ya
        // cumplen `descuentoAnchoLateralApa` y `descuentoAltoVertical`, y el descuento de esquina
        // era el único que se la saltaba (entró en 64c2251 aplicándose a los dos tipos; en
        // 932353b no existía). El encuentro en ángulo de INA se resuelve SUMANDO el espesor de
        // una pata, no restando: mientras ese valor no esté definido, INA usa la medida tal cual.
        if (tipoNova != TipoNova.APA) return primera to segunda

        // APA: el descuento de esquina reduce el ancho; los paños de mocheta y los Tee se
        // calculan sobre ese ancho ÚTIL ya descontado, no sobre el original. Cuántas hojas lleva
        // el lado NO sale de aquí (ver conDivisionesDeLaMedidaReal).
        val opcion = opcionEsquinero() ?: return primera to segunda
        val valorNormal = opcion.valor
        val valorEsquina = opcion.valorEsquina

        val descuentoPrimera: Float
        val descuentoSegunda: Float
        if (kotlin.math.abs(valorEsquina - valorNormal) < 0.001f) {
            descuentoPrimera = valorNormal
            descuentoSegunda = valorNormal
        } else {
            val mayor = maxOf(valorNormal, valorEsquina)
            val menor = minOf(valorNormal, valorEsquina)
            if (primera.ancho >= segunda.ancho) {
                descuentoPrimera = menor   // más ancha → parte menor
                descuentoSegunda = mayor
            } else {
                descuentoPrimera = mayor
                descuentoSegunda = menor
            }
        }

        return primera.copy(ancho = (primera.ancho - descuentoPrimera).coerceAtLeast(0f)) to
            segunda.copy(ancho = (segunda.ancho - descuentoSegunda).coerceAtLeast(0f))
    }

    /**
     * CUÁNTAS hojas lleva un lado sale de la regla general (60 cm por división) sobre la medida
     * que se MIDIÓ, no sobre el ancho útil: el descuento de esquina cambia las MEDIDAS de corte,
     * no la cantidad de hojas. Un lado de 122 pasa de 120, así que son 3 divisiones aunque el
     * útil quede en 119 y algo. Si en esa ventana corresponden 2, el vidriero las escribe a mano.
     *
     * Devuelve la medida de cálculo (ancho útil) con esa cantidad ya fijada como divisManual, para
     * que todos los caminos que la reciben cuenten igual.
     */
    private fun conDivisionesDeLaMedidaReal(original: MedidaNl, calculo: MedidaNl): MedidaNl =
        calculo.copy(divisManual = NovaCalculos.divisiones(original.ancho, original.divisManual))

    private fun capturarCamposCalculo(): EstadoCamposCalculo = EstadoCamposCalculo(
        ancho = binding.etAncho.text?.toString().orEmpty(),
        alto = binding.etAlto.text?.toString().orEmpty(),
        hoja = binding.etHoja.text?.toString().orEmpty(),
        divis = binding.etPartes.text?.toString().orEmpty()
    )

    private fun restaurarCamposCalculo(estado: EstadoCamposCalculo) {
        binding.etAncho.setText(estado.ancho)
        binding.etAlto.setText(estado.alto)
        binding.etHoja.setText(estado.hoja)
        binding.etPartes.setText(estado.divis)
    }

    private fun aplicarMedidaEnCampos(medida: MedidaNl) {
        binding.etAncho.setText(df1(medida.ancho))
        binding.etAlto.setText(df1(medida.alto))
        binding.etHoja.setText(df1(medida.hoja))
        binding.etPartes.setText(medida.divisManual.toString())
    }

    private fun capturarMateriales(): EstadoMateriales = EstadoMateriales(
        u = binding.txU.text?.toString().orEmpty(),
        p = binding.txP.text?.toString().orEmpty(),
        r = binding.txR.text?.toString().orEmpty(),
        fc = binding.txFc.text?.toString().orEmpty(),
        pf = binding.txPf.text?.toString().orEmpty(),
        te = binding.txTe.text?.toString().orEmpty(),
        tope = binding.txTo.text?.toString().orEmpty(),
        hache = binding.txH.text?.toString().orEmpty(),
        tubo = binding.txT.text?.toString().orEmpty(),
        vidrio = binding.txV.text?.toString().orEmpty(),
        referencias = binding.txReferencias.text?.toString().orEmpty()
    )

    private fun combinarMateriales(a: EstadoMateriales, b: EstadoMateriales): EstadoMateriales = EstadoMateriales(
        u = combinarTextoCantidades(a.u, b.u),
        p = combinarTextoCantidades(a.p, b.p),
        r = combinarTextoCantidades(a.r, b.r),
        fc = combinarTextoCantidades(a.fc, b.fc),
        pf = combinarTextoCantidades(a.pf, b.pf),
        te = combinarTextoCantidades(a.te, b.te),
        tope = combinarTextoCantidades(a.tope, b.tope),
        hache = combinarTextoCantidades(a.hache, b.hache),
        tubo = combinarTextoCantidades(a.tubo, b.tubo),
        vidrio = combinarTextoCantidades(a.vidrio, b.vidrio),
        referencias = listOf(a.referencias, b.referencias)
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString("\n")
    )

    private fun combinarTextoCantidades(vararg textos: String): String {
        val orden = mutableListOf<String>()
        val cantidades = linkedMapOf<String, Int>()
        val literales = mutableListOf<String>()
        val patron = Regex("^(.+?)\\s*=\\s*(\\d+)\\s*$")

        for (texto in textos) {
            texto.lineSequence()
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .forEach { linea ->
                    val match = patron.matchEntire(linea)
                    if (match == null) {
                        if (linea !in literales) literales.add(linea)
                    } else {
                        val medida = match.groupValues[1].trim()
                        val cantidad = match.groupValues[2].toIntOrNull() ?: 0
                        if (!cantidades.containsKey(medida)) orden.add(medida)
                        cantidades[medida] = (cantidades[medida] ?: 0) + cantidad
                    }
                }
        }

        val lineas = mutableListOf<String>()
        orden.forEach { medida -> lineas.add("$medida = ${cantidades[medida] ?: 0}") }
        lineas.addAll(literales)
        return lineas.joinToString("\n")
    }

    private fun filtrarMaterialesValorCero(estado: EstadoMateriales): EstadoMateriales = estado.copy(
        u = filtrarLineasValorCero(estado.u),
        p = filtrarLineasValorCero(estado.p),
        r = filtrarLineasValorCero(estado.r),
        fc = filtrarLineasValorCero(estado.fc),
        pf = filtrarLineasValorCero(estado.pf),
        te = filtrarLineasValorCero(estado.te),
        tope = filtrarLineasValorCero(estado.tope),
        hache = filtrarLineasValorCero(estado.hache),
        tubo = filtrarLineasValorCero(estado.tubo),
        vidrio = filtrarLineasValorCero(estado.vidrio)
    )

    private fun filtrarLineasValorCero(texto: String): String {
        return texto.lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() && !lineaTieneValorCero(it) }
            .joinToString("\n")
    }

    private fun lineaTieneValorCero(linea: String): Boolean {
        if (!linea.contains("=")) {
            val valorUnico = linea.replace(",", ".").toFloatOrNull()
            return valorUnico != null && kotlin.math.abs(valorUnico) < 0.0001f
        }
        val medida = linea.substringBefore("=").trim()
        val cantidad = linea.substringAfter("=", "").trim().toIntOrNull()
        if (cantidad == 0) return true

        val numeros = Regex("""-?\d+(?:[.,]\d+)?""").findAll(medida)
            .mapNotNull { it.value.replace(",", ".").toFloatOrNull() }
            .toList()
        return numeros.isNotEmpty() && numeros.any { kotlin.math.abs(it) < 0.0001f }
    }

    private fun mostrarFijoCorre(textoResultado: String, etiquetaResId: Int = R.string.fijo_corre) {
        val textoLimpio = filtrarLineasValorCero(textoResultado)
        binding.txFc.text = textoLimpio
        binding.tvFc.setText(etiquetaResId)
        binding.fcLayout.visibility = if (textoLimpio.isNotBlank()) View.VISIBLE else View.GONE
    }

    private fun ocultarFijoCorre() {
        binding.txFc.text = ""
        binding.fcLayout.visibility = View.GONE
    }

    private fun aplicarMateriales(estado: EstadoMateriales) {
        binding.txU.text = estado.u
        binding.txP.text = estado.p
        binding.txR.text = estado.r
        binding.txFc.text = estado.fc
        binding.txPf.text = estado.pf
        binding.txTe.text = estado.te
        binding.txTo.text = estado.tope
        binding.txH.text = estado.hache
        binding.txT.text = estado.tubo
        binding.txV.text = estado.vidrio
        binding.txReferencias.text = estado.referencias

        binding.mulLayout.visibility = if (estado.p.isNotBlank()) View.VISIBLE else View.GONE
        binding.lyRiel.visibility = if (estado.r.isNotBlank()) View.VISIBLE else View.GONE
        binding.fcLayout.visibility = if (estado.fc.isNotBlank()) View.VISIBLE else View.GONE
        binding.lyPf.visibility = if (estado.pf.isNotBlank()) View.VISIBLE else View.GONE
        binding.tLayout.visibility = if (estado.te.isNotBlank()) View.VISIBLE else View.GONE
        binding.lyTo.visibility = if (estado.tope.isNotBlank()) View.VISIBLE else View.GONE
        binding.lyH.visibility = if (estado.hache.isNotBlank()) View.VISIBLE else View.GONE
        binding.lyTubo.visibility = if (estado.tubo.isNotBlank()) View.VISIBLE else View.GONE
        // Mostrar el perfil del esquinero junto al parante para no confundir qué es.
        binding.tvT.text = opcionEsquinero()?.let { "Parante (${it.text})" } ?: getString(R.string.parante)
    }

    @SuppressLint("SetTextI18n")
    private fun calcularDesigual() {
        val ancho = ancho()
        val alto = alto()
        val altoHoja = if (altoHojaDesigual > 0f) altoHojaDesigual else altoHoja()
        val us = binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f
        val tipoCalculo = when (tipoNova) {
            TipoNova.APA -> "apa"
            TipoNova.INA -> "ina"
            TipoNova.PIV -> "piv"
        }
        val cruce = when (tipoNova) {
            TipoNova.APA -> {
                val exacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
                if (exacto == 0f) 0.7f else exacto
            }
            else -> {
                val exacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
                NovaInaCalculos.cruce(exacto, modulosDesiguales.size)
            }
        }

        actualizarTxPrConDisenoV2()

        // U perfiles
        binding.tvU.text = NovaPerfilesHelper.obtenerEtiquetaU(us)
        binding.txU.text = NovaCalculosDesiguales.calcularU(
            modulosDesiguales,
            parantesDesiguales,
            ancho,
            alto,
            altoHoja,
            us,
            cruce,
            tipoCalculo,
            tubo,
            puente,
            incluirUFijos = !esModeloInvertidoIna()
        )

          // Puentes y Rieles
          val pr = NovaCalculosDesiguales.calcularPuentesYRieles(
              modulosDesiguales, parantesDesiguales, ancho, alto, altoHoja, tipoCalculo
          )
          val textoTramosDesigual = if (tipoNova == TipoNova.APA && alto > altoHoja) {
              NovaCalculosDesiguales.textoUAnchoMochetaPorTramos(
                  modulosDesiguales, parantesDesiguales, ancho, tipoCalculo, tubo, puente
              )
          } else ""
          val textoTramosUnitDesigual = if (tipoNova == TipoNova.APA && alto > altoHoja) {
              NovaCalculosDesiguales.textoTramosUnitario(
                  modulosDesiguales, parantesDesiguales, ancho, tipoCalculo, tubo, puente
              )
          } else ""
          if (textoTramosDesigual.isNotBlank()) {
              binding.txP.text = textoTramosUnitDesigual
              binding.txR.text = textoTramosUnitDesigual
              binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else View.VISIBLE
              binding.lyRiel.visibility = View.VISIBLE
          } else {
              binding.txP.text = pr.puentes
              binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else if (modulosDesiguales.size > 1) View.VISIBLE else View.GONE
              binding.txR.text = pr.rieles
              binding.lyRiel.visibility = if (modulosDesiguales.count { it.tipo == 'c' } > 0) View.VISIBLE else View.GONE
          }

          val mostrarFcDesigual = mostrarFijoCorredizo(alto, altoHoja)
          if (mostrarFcDesigual) {
              val textoFcDesigual = if (textoTramosDesigual.isNotBlank()) textoTramosUnitDesigual else pr.rieles
              mostrarFijoCorre(textoFcDesigual)
          } else {
              ocultarFijoCorre()
          }

        // Vidrios
        binding.txV.text = NovaCalculosDesiguales.calcularVidrios(
            modulosDesiguales, mochetaDesigual, parantesDesiguales,
            ancho, alto, altoHoja, us, cruce, tipoCalculo, tubo, texto,
            mochetaInferior = mochetaInferiorDoblePuente(),
            mochetaInvertidaIna = esModeloInvertidoIna(),
            puente = puente
        )

        // Otros perfiles
        val otros = NovaCalculosDesiguales.calcularOtros(
            modulosDesiguales, parantesDesiguales, ancho, alto, altoHoja, us, cruce, tipoCalculo, tubo, puente, texto,
            mochetaInferior = mochetaInferiorDoblePuente(),
            mocheta = mochetaDesigual
        )
        binding.txPf.text = otros.portafelpa
        binding.lyPf.visibility = if (otros.portafelpa.isNotBlank()) View.VISIBLE else View.GONE
        binding.txH.text = otros.hache
        binding.lyH.visibility = if (otros.hache.isNotBlank()) View.VISIBLE else View.GONE
        binding.txTo.text = otros.tope
        binding.lyTo.visibility = if (otros.tope.isNotBlank()) View.VISIBLE else View.GONE
          val nParantesDes = parantesDesiguales.size
          val textoParantesDes = if (nParantesDes > 0) "${df1(alto)} = $nParantesDes" else ""
          binding.txT.text = textoParantesDes
          binding.lyTubo.visibility = if (textoParantesDes.isNotBlank()) View.VISIBLE else View.GONE

        if (tipoNova == TipoNova.APA) {
            binding.txTe.text = otros.tee
            binding.tLayout.visibility = if (otros.tee.isNotBlank()) View.VISIBLE else View.GONE
            binding.lyUf.visibility = View.GONE
        } else {
            val textoTramosUnitInaDesigual = if (tipoNova == TipoNova.INA && alto > altoHoja) {
                NovaCalculosDesiguales.textoTramosUnitario(
                    modulosDesiguales, parantesDesiguales, ancho, tipoCalculo, tubo, puente
                )
            } else {
                ""
            }
            if (textoTramosUnitInaDesigual.isNotBlank()) {
                binding.txP.text = textoTramosUnitInaDesigual
                binding.txR.text = textoTramosUnitInaDesigual
                binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else View.VISIBLE
                binding.lyRiel.visibility = View.VISIBLE

                val mostrarFcIna = mostrarFijoCorredizo(alto, altoHoja)
                if (mostrarFcIna) {
                    val mostrarUfEnFc = mostrarUFelperoIna(alto, altoHoja) || usarFcComoUFelpero(alto, altoHoja)
                    mostrarFijoCorre(
                        textoTramosUnitInaDesigual,
                        if (mostrarUfEnFc) R.string.u_felpero else R.string.fijo_corre
                    )
                } else {
                    ocultarFijoCorre()
                }
            }
            if (esModeloInvertidoIna()) {
                val textoFcInvertido = textoFijoCorreInaInvertido(ancho)
                mostrarFijoCorre(textoFcInvertido)
            }
            binding.txUf.text = ""
            binding.lyUf.visibility = View.GONE
            binding.tLayout.visibility = View.GONE
            val nParantesDiseno = NovaCalculos.nParantesDiseno(ancho, modulosDesiguales.size)
            val textoParantes = if (nParantesDiseno > 0) "${NovaCalculos.df1(alto)} = $nParantesDiseno" else ""
            binding.txT.text = textoParantes
            binding.lyTubo.visibility = if (textoParantes.isNotBlank()) View.VISIBLE else View.GONE
            binding.txTe.text = ""
        }

        // Referencias
        val nFijos = modulosDesiguales.count { it.tipo == 'f' }
        val nCorredizas = modulosDesiguales.count { it.tipo == 'c' }
        binding.txReferencias.text = NovaUIHelper.generarReferencias(
            ancho, alto, altoHoja, modulosDesiguales.size, nFijos, nCorredizas,
            if (alto > altoHoja) 1 else 0,
            modelo = texto,
            alturaPuente = tubo,
            mochetaInferior = mochetaInferiorDoblePuente(),
            remate = modeloRemate,
            descontarPuentes = tipoNova == TipoNova.APA
        )

        // Diseño visual: usar ultimoPaquete (no regenerar)
        if (ultimoPaquete.isNotBlank()) {
            val intent = Intent(this, DisenoNovaActivity::class.java).apply {
                putExtra(DisenoNovaActivity.EXTRA_PAQUETE, ultimoPaquete)
                putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
                putExtra(DisenoNovaActivity.EXTRA_OUTPUT_FORMAT, if (texto == "nl" || texto == "nu" || texto == "ns" || texto == "ncu" || texto == "nci" || ultimoPaquete.contains("P<2.5>") || modulosDesiguales.isNotEmpty()) "png" else "svg")
                putExtra(DisenoNovaActivity.EXTRA_RET_PADDING_PX, 4)
                putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, mochetaLateralDisenoNl())
                putExtra(DisenoNovaActivity.EXTRA_ENCUENTRO_VACIO, metaEncuentro)
                putExtra(DisenoNovaActivity.EXTRA_DIRECCION, metaDireccion)
                putExtra(DisenoNovaActivity.EXTRA_US_CM, binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f)
            }
            lanzarDiseno.launch(intent)
            binding.textView28.setText(R.string.referencias_y_c_lculos)
        }
    }

    // ==================== MODELOS ====================
    @SuppressLint("SetTextI18n")
    private fun modelos() {
        // Estado inicial: modelo normal aplicado, geometría la gestiona el panel
        seleccionarModelo("normal", R.drawable.ic_fichad3a, "nn")
        diseno = "ic_fichad3a"; otros = false

        // El scroll actualiza diseno/otros/visual y, si la geometría es plana, también texto
        binding.btNovan.setOnClickListener {
            seleccionarModelo("normal", R.drawable.ic_fichad3a, "nn")
            diseno = "ic_fichad3a"; otros = false
        }
        binding.btNovar.setOnClickListener {
            seleccionarModelo("invertido", R.drawable.novair, "nr")
            diseno = "novair"; otros = false
            aplicarPuentePredeterminadoSinMultiple()
        }
        binding.btNovaP2.setOnClickListener {
            seleccionarModelo("doble puente", R.drawable.nova2p, "np")
            diseno = "nova2p"; otros = false
            aplicarPuentePredeterminadoSinMultiple()
        }
        binding.btNovacc.setOnClickListener {
            seleccionarModelo("doble corrediza", R.drawable.novacc, "ncc")
            diseno = "novacc"; otros = true
        }
        binding.btNova3c.setOnClickListener {
            seleccionarModelo("triple corrediza", R.drawable.nova3c, "n3c")
            diseno = "nova3c"; otros = true
        }
        binding.btNovacfc.setOnClickListener {
            seleccionarModelo("cor. fijo cor.", R.drawable.novacfc, "ncfc")
            diseno = "novacfc"; otros = true
        }
        // btNoval, btNovau, btNovas, btNovacu migrados al panel de geometría.
        binding.btNovaff.setOnClickListener {
            seleccionarModelo("fijo fijo", R.drawable.novaff, "nff")
            diseno = "novaff"; otros = true
        }
        binding.btNovaci.setOnClickListener {
            seleccionarModelo("circular", R.drawable.novaci, "nci")
            diseno = "novaci"; otros = true
        }
        binding.btNovav.setOnClickListener {
            seleccionarModelo("con acople", R.drawable.bvacio, "nv")
            diseno = "bvacio"; otros = false
        }

        // Panel derecho: selector de tipo de ventana por forma
        val togglePanelModelos = View.OnClickListener {
            binding.lyPanelModelos.visibility =
                if (binding.lyPanelModelos.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        binding.ivModeloEspecial.setOnClickListener(togglePanelModelos)
        binding.tvMedidas.setOnClickListener(togglePanelModelos)

        binding.itemVplano.setOnClickListener  { seleccionarDesdePanel(R.drawable.vplano,  "nn")  }
        binding.itemVenl.setOnClickListener    { seleccionarDesdePanel(R.drawable.venl,    "nl")  }
        binding.itemVenc.setOnClickListener    { seleccionarDesdePanel(R.drawable.venc,    "nu")  }
        binding.itemVcurvo.setOnClickListener  { seleccionarDesdePanel(R.drawable.vcurvo,  "ncu") }
        binding.itemVserie.setOnClickListener  { seleccionarDesdePanel(R.drawable.vserie,  "ns")  }

        binding.btAgregar.setOnClickListener {
            if (texto != "nl" && texto != "nu" && texto != "ns") return@setOnClickListener
            // En serie, tras calcular el botón pasa a "Navegar": recorre/edita cada lado.
            if (texto == "ns" && navegandoNs) {
                navegarSiguienteNs()
                return@setOnClickListener
            }
            val ancho = binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f
            val alto = binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f
            val hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
            val divisManual = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
            if (ancho <= 0f || alto <= 0f) {
                Toast.makeText(this, "Ingrese ancho y alto válidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            agregarLado(ancho, alto, hoja, divisManual)
            // Tras agregar, devolver el foco a la primera medida (med1) para la siguiente.
            binding.etAncho.requestFocus()
        }
        binding.ivDiseno.setOnClickListener {
            // Estrategia tipo Puertas: un diálogo con todas las opciones agrupadas.
            abrirDialogoOpcionesNova()
        }
    }

    /**
     * Guarda un lado de una geometría compuesta y deja los campos listos para el siguiente.
     *
     * Es lo que hace el botón "Agregar", aparte: así un lado se puede meter también sin tocar la
     * pantalla, que es como llegan los de una ventana de esquina medida en el apunte.
     */
    private fun agregarLado(ancho: Float, alto: Float, hoja: Float, divisManual: Int) {
        if (texto == "nl") {
            primeraMedidaNl = MedidaNl(ancho, alto, hoja, divisManual)
            binding.etAncho2.setText(df1(ancho))
            binding.etAlto2.setText(df1(alto))
            binding.etPuente2.setText(df1(hoja))
            binding.etDivi2.setText(divisManual.toString())
            binding.lyAncho2.visibility = View.VISIBLE
            binding.lyAlto2.visibility = View.VISIBLE
            binding.lyPuente2.visibility = View.VISIBLE
            binding.lyDivi2.visibility = View.VISIBLE
            binding.tvLado1.visibility = View.VISIBLE
            binding.etAncho.setText("")
            binding.etAlto.setText("")
            binding.etPartes.setText("0")
            binding.btAgregar.visibility = View.GONE
            binding.btAgregar.isEnabled = false
            contadorLado = 2
            binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
            actualizarTxDatosLadoNl(1)
        } else {
            val medida = MedidaNl(ancho, alto, hoja, divisManual)
            if (texto == "nu") {
                when (contadorLado) {
                    1 -> {
                        primeraMedidaNu = medida
                        binding.etAncho2.setText(df1(ancho))
                        binding.etAlto2.setText(df1(alto))
                        binding.etPuente2.setText(df1(hoja))
                        binding.etDivi2.setText(divisManual.toString())
                        binding.lyAncho2.visibility = View.VISIBLE
                        binding.lyAlto2.visibility = View.VISIBLE
                        binding.lyPuente2.visibility = View.VISIBLE
                        binding.lyDivi2.visibility = View.VISIBLE
                        binding.tvLado1.visibility = View.VISIBLE
                        binding.etAncho.setText("")
                        binding.etAlto.setText("")
                        binding.etHoja.setText("")
                        binding.etPartes.setText("0")
                        contadorLado = 2
                        binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
                        actualizarTxDatosLadoNl(1)
                    }
                    2 -> {
                        segundaMedidaNu = medida
                        binding.etAncho3.setText(df1(ancho))
                        binding.etAlto3.setText(df1(alto))
                        binding.etPuente3.setText(df1(hoja))
                        binding.etDivi3.setText(divisManual.toString())
                        binding.lyAncho3.visibility = View.VISIBLE
                        binding.lyAlto3.visibility = View.VISIBLE
                        binding.lyPuente3.visibility = View.VISIBLE
                        binding.lyDivi3.visibility = View.VISIBLE
                        binding.etAncho.setText("")
                        binding.etAlto.setText("")
                        binding.etHoja.setText("")
                        binding.etPartes.setText("0")
                        contadorLado = 3
                        binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
                        binding.btAgregar.visibility = View.GONE
                        binding.btAgregar.isEnabled = false
                        actualizarTxDatosLadoNl(2)
                    }
                }
            } else if (texto == "ns") {
                ladosNs.add(medida)
                binding.etAncho.setText("")
                binding.etAlto.setText("")
                binding.etHoja.setText("")
                binding.etPartes.setText("0")
                contadorLado += 1
                binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
                actualizarTxDatosLadoNl(ladosNs.size)
            }
        }
    }

    /**
     * Diálogo con todas las opciones de la ventana Nova (estrategia tipo Puertas).
     * Reutiliza la lógica existente: geometría -> seleccionarDesdePanel; modelo/modulación/
     * circular -> seleccionarModelo; acabado -> ciclo de tipoNova (actualizarModo).
     * Encuentro y forma poligonal solo guardan el dato (metaEncuentro / metaForma).
     */
    private fun abrirDialogoOpcionesNova() {
        val dlg = crystal.crystal.databinding.DialogOpcionesNovaBinding.inflate(layoutInflater)

        // Grupos de imágenes y resaltado de la opción activa.
        // Geometría se marca por nombreGeometria; modelo/modulación/otros por textoModelo.
        val geoViews = listOf(
            "Plano" to dlg.dgGeoPlano, "En L" to dlg.dgGeoL, "En C" to dlg.dgGeoC,
            "Curvo" to dlg.dgGeoCurvo, "Serie" to dlg.dgGeoSerie
        )
        // Eje remate (se marca por modeloRemate) y eje modulación/otros (por textoModelo).
        val remateViews = listOf(
            "nn" to dlg.dgModNormal, "nr" to dlg.dgModInvertido, "np" to dlg.dgModDoblePuente
        )
        val modulacionViews = listOf(
            "nn" to dlg.dgModulNormal, "nfc" to dlg.dgModulDoble,
            "ncfc" to dlg.dgModulCfc, "nff" to dlg.dgModulFull
        )
        val padSel = (4 * resources.displayMetrics.density).toInt()
        fun marcar(iv: ImageView, sel: Boolean) {
            iv.setBackgroundResource(if (sel) R.drawable.bg_opcion_seleccionada else 0)
            if (sel) iv.setPadding(padSel, padSel, padSel, padSel) else iv.setPadding(0, 0, 0, 0)
        }
        fun refrescarSeleccion() {
            geoViews.forEach { (nombre, iv) -> marcar(iv, nombre == nombreGeometria) }
            remateViews.forEach { (tok, iv) -> marcar(iv, tok == modeloRemate) }
            modulacionViews.forEach { (tok, iv) -> marcar(iv, tok == textoModelo) }
        }

        // Esquinero + Dirección: solo en geometrías compuestas (en L / en C / serie).
        // Se recalcula al abrir y cada vez que cambia la geometría.
        fun actualizarSeccionCompuesta() {
            val esCompuesta = texto == "nl" || texto == "nu" || texto == "ns"
            dlg.dgSeccionCompuesta.visibility = if (esCompuesta) View.VISIBLE else View.GONE
            if (!esCompuesta) return
            val opciones = NovaSpinnerData.obtenerOpcionesTubo()
                .filterNot { it.text.contains("ltiple", ignoreCase = true) }
            if (!metaEsquinero.equals("ninguno", ignoreCase = true) && opciones.none { it.text == metaEsquinero }) {
                metaEsquinero = (opciones.firstOrNull { it.valorEsquina == 6f } ?: opciones.first()).text
            }
            dlg.dgEsquineroContainer.removeAllViews()
            val tamPx = (56 * resources.displayMetrics.density).toInt()
            val esquineroViews = mutableListOf<Pair<String, View>>()
            fun marcarEsquinero() {
                esquineroViews.forEach { (txt, v) ->
                    v.setBackgroundResource(if (txt == metaEsquinero) R.drawable.bg_opcion_seleccionada else 0)
                }
            }
            val anchoItemPx = (78 * resources.displayMetrics.density).toInt()
            // Cada opción: imagen (si tiene) + texto del nombre, para diferenciarlas.
            fun crearItemEsquinero(textoOp: String, imgRes: Int?): View {
                val cont = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    gravity = android.view.Gravity.CENTER_HORIZONTAL
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        anchoItemPx, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also { it.setMargins(padSel, padSel, padSel, padSel) }
                    setPadding(padSel, padSel, padSel, padSel)
                    setOnClickListener { metaEsquinero = textoOp; marcarEsquinero() }
                }
                if (imgRes != null) {
                    cont.addView(ImageView(this).apply {
                        layoutParams = android.widget.LinearLayout.LayoutParams(tamPx, tamPx)
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        setImageResource(imgRes)
                        contentDescription = textoOp
                    })
                }
                cont.addView(android.widget.TextView(this).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        anchoItemPx, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = textoOp
                    textSize = 13f
                    gravity = android.view.Gravity.CENTER
                    maxLines = 2
                })
                return cont
            }
            for (op in opciones) {
                val item = crearItemEsquinero(op.text, op.imageResId)
                dlg.dgEsquineroContainer.addView(item)
                esquineroViews.add(op.text to item)
            }
            val itemNinguno = crearItemEsquinero("ninguno", null)
            dlg.dgEsquineroContainer.addView(itemNinguno)
            esquineroViews.add("ninguno" to itemNinguno)
            marcarEsquinero()
            if (metaDireccion == "afuera") dlg.dgRbAfuera.isChecked = true else dlg.dgRbAdentro.isChecked = true
            dlg.dgRgDireccion.setOnCheckedChangeListener { _, checkedId ->
                metaDireccion = if (checkedId == R.id.dgRbAfuera) "afuera" else "adentro"
            }
        }

        // Parante del lado al vacío: solo en geometría plana y si algún lado quedó desmarcado.
        // Ofrece todos los perfiles MENOS gorrito y múltiple (son puentes entre paños, no cierres).
        fun actualizarSeccionParanteVacio() {
            val algunLadoVacio = !dlg.dgCbTop.isChecked || !dlg.dgCbRight.isChecked ||
                !dlg.dgCbBottom.isChecked || !dlg.dgCbLeft.isChecked
            val mostrar = esGeometriaPlana() && algunLadoVacio
            dlg.dgSeccionParanteVacio.visibility = if (mostrar) View.VISIBLE else View.GONE
            if (!mostrar) return
            val opciones = NovaSpinnerData.obtenerOpcionesTubo().filterNot {
                it.text.contains("ltiple", ignoreCase = true) || it.text.contains("gorrito", ignoreCase = true)
            }
            if (opciones.none { it.text == metaParanteVacio }) {
                metaParanteVacio = (opciones.firstOrNull { it.valorEsquina == 6f } ?: opciones.first()).text
            }
            dlg.dgParanteVacioContainer.removeAllViews()
            val tamPx = (56 * resources.displayMetrics.density).toInt()
            val anchoItemPx = (78 * resources.displayMetrics.density).toInt()
            val vistas = mutableListOf<Pair<String, View>>()
            fun marcar() {
                vistas.forEach { (txt, v) ->
                    v.setBackgroundResource(if (txt == metaParanteVacio) R.drawable.bg_opcion_seleccionada else 0)
                }
            }
            for (op in opciones) {
                val cont = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    gravity = android.view.Gravity.CENTER_HORIZONTAL
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        anchoItemPx, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also { it.setMargins(padSel, padSel, padSel, padSel) }
                    setPadding(padSel, padSel, padSel, padSel)
                    setOnClickListener { metaParanteVacio = op.text; marcar() }
                }
                cont.addView(ImageView(this).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(tamPx, tamPx)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    setImageResource(op.imageResId)
                    contentDescription = op.text
                })
                cont.addView(android.widget.TextView(this).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        anchoItemPx, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = op.text
                    textSize = 13f
                    gravity = android.view.Gravity.CENTER
                    maxLines = 2
                })
                dlg.dgParanteVacioContainer.addView(cont)
                vistas.add(op.text to cont)
            }
            marcar()
        }

        // --- Geometría (vista top) ---
        dlg.dgGeoPlano.setOnClickListener  { seleccionarDesdePanel(R.drawable.vplano, "nn"); refrescarSeleccion(); actualizarSeccionCompuesta(); actualizarSeccionParanteVacio() }
        dlg.dgGeoL.setOnClickListener      { seleccionarDesdePanel(R.drawable.venl,   "nl"); refrescarSeleccion(); actualizarSeccionCompuesta(); actualizarSeccionParanteVacio() }
        dlg.dgGeoC.setOnClickListener      { seleccionarDesdePanel(R.drawable.venc,   "nu"); refrescarSeleccion(); actualizarSeccionCompuesta(); actualizarSeccionParanteVacio() }
        dlg.dgGeoCurvo.setOnClickListener  { seleccionarDesdePanel(R.drawable.vcurvo, "ncu"); refrescarSeleccion(); actualizarSeccionCompuesta(); actualizarSeccionParanteVacio() }
        dlg.dgGeoSerie.setOnClickListener  { seleccionarDesdePanel(R.drawable.vserie, "ns"); refrescarSeleccion(); actualizarSeccionCompuesta(); actualizarSeccionParanteVacio() }

        // --- Acabado (refleja y fija tipoNova; el ciclo por título sigue intacto) ---
        when (tipoNova) {
            TipoNova.APA -> dlg.dgRbApa.isChecked = true
            TipoNova.INA -> dlg.dgRbIna.isChecked = true
            TipoNova.PIV -> dlg.dgRbPiv.isChecked = true
        }
        dlg.dgRbPiv.visibility = View.GONE   // Nova pivotante oculto en v1
        dlg.dgRgAcabado.setOnCheckedChangeListener { _, checkedId ->
            tipoNova = when (checkedId) {
                R.id.dgRbIna -> TipoNova.INA
                R.id.dgRbPiv -> TipoNova.PIV
                else -> TipoNova.APA
            }
            actualizarModo()
        }

        // --- Encuentro: marcar según metaEncuentro (orden Arriba-Derecha-Abajo-Izquierda) ---
        val enc = metaEncuentro.padEnd(4, '1')
        dlg.dgCbTop.isChecked    = enc.getOrElse(0) { '1' } == '1'
        dlg.dgCbRight.isChecked  = enc.getOrElse(1) { '1' } == '1'
        dlg.dgCbBottom.isChecked = enc.getOrElse(2) { '1' } == '1'
        dlg.dgCbLeft.isChecked   = enc.getOrElse(3) { '1' } == '1'
        // Muro (ladrillos) en el lado marcado; desaparece al desmarcar (ese lado = vacío).
        fun bindMuroEncuentro(cb: android.widget.CheckBox, muro: View) {
            muro.visibility = if (cb.isChecked) View.VISIBLE else View.GONE
            cb.setOnCheckedChangeListener { _, isChecked ->
                muro.visibility = if (isChecked) View.VISIBLE else View.GONE
                // Al desmarcar un lado aparece la franja del parante; al remarcarlo desaparece.
                actualizarSeccionParanteVacio()
            }
        }
        bindMuroEncuentro(dlg.dgCbTop, dlg.dgMuroTop)
        bindMuroEncuentro(dlg.dgCbRight, dlg.dgMuroRight)
        bindMuroEncuentro(dlg.dgCbBottom, dlg.dgMuroBottom)
        bindMuroEncuentro(dlg.dgCbLeft, dlg.dgMuroLeft)
        actualizarSeccionParanteVacio()

        // --- Modelo (remate) — eje independiente; no toca la modulación ---
        dlg.dgModNormal.setOnClickListener      { seleccionarRemate("normal", "nn"); refrescarSeleccion() }
        dlg.dgModInvertido.setOnClickListener   { seleccionarRemate("invertido", "nr"); refrescarSeleccion() }
        dlg.dgModDoblePuente.setOnClickListener { seleccionarRemate("doble puente", "np"); refrescarSeleccion() }

        // --- Modulación (arreglo de hojas) ---
        // La sección "corredizas por tramo" solo aplica a full corredizas (nfc).
        fun actualizarSeccionNfc() {
            dlg.dgNfcTramoSection.visibility = if (textoModelo == "nfc") View.VISIBLE else View.GONE
        }
        dlg.dgModulNormal.setOnClickListener { seleccionarModelo("normal", R.drawable.ic_fichad3a, "nn"); refrescarSeleccion(); actualizarSeccionNfc() }
        // Se abandonan doble (ncc) y triple corrediza (n3c). dgModulDoble pasa a "full corredizas"
        // (todo corredizas, lo contrario a full fijos); dgModulTriple se oculta.
        dlg.dgModulDoble.setOnClickListener  { seleccionarModelo("full corredizas", R.drawable.novacc, "nfc"); refrescarSeleccion(); actualizarSeccionNfc() }
        dlg.dgModulTriple.visibility = View.GONE
        dlg.dgModulCfc.setOnClickListener    { seleccionarModelo("cor. fijo cor.", R.drawable.novacfc, "ncfc"); refrescarSeleccion(); actualizarSeccionNfc() }
        dlg.dgModulFull.setOnClickListener   { seleccionarModelo("fijo fijo", R.drawable.novaff, "nff"); refrescarSeleccion(); actualizarSeccionNfc() }

        // Selector de corredizas por tramo (Auto/2/3/4/5). Auto = reparto del clásico.
        when (corredizasPorTramo) {
            2 -> dlg.dgNfcTramo2.isChecked = true
            3 -> dlg.dgNfcTramo3.isChecked = true
            4 -> dlg.dgNfcTramo4.isChecked = true
            5 -> dlg.dgNfcTramo5.isChecked = true
            else -> dlg.dgNfcTramoAuto.isChecked = true
        }
        dlg.dgRgNfcTramo.setOnCheckedChangeListener { _, checkedId ->
            corredizasPorTramo = when (checkedId) {
                R.id.dgNfcTramo2 -> 2
                R.id.dgNfcTramo3 -> 3
                R.id.dgNfcTramo4 -> 4
                R.id.dgNfcTramo5 -> 5
                else -> 0   // Auto
            }
            NovaCalculos.corredizasPorTramoNfc = corredizasPorTramo
        }
        actualizarSeccionNfc()

        // --- Puente (mismo selector que el spinner txHpuente; para todos los modelos) ---
        run {
            // Invertido / doble puente / INA no admiten Múltiple: se oculta de la lista.
            val opcionesPuente = NovaSpinnerData.obtenerOpcionesTubo()
                .filterNot { puenteMultipleNoPermitido(it.text) }
            val anchoItem = (78 * resources.displayMetrics.density).toInt()
            val tamImg = (52 * resources.displayMetrics.density).toInt()
            dlg.dgPuenteContainer.removeAllViews()
            val vistasPuente = mutableListOf<Pair<String, View>>()
            fun marcarPuente() {
                vistasPuente.forEach { (txt, v) ->
                    v.setBackgroundResource(if (txt == puente) R.drawable.bg_opcion_seleccionada else 0)
                }
            }
            for (op in opcionesPuente) {
                val cont = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    gravity = android.view.Gravity.CENTER_HORIZONTAL
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        anchoItem, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also { it.setMargins(padSel, padSel, padSel, padSel) }
                    setPadding(padSel, padSel, padSel, padSel)
                    setOnClickListener { aplicarPuenteDesdeOpcion(op); marcarPuente() }
                }
                cont.addView(ImageView(this).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(tamImg, tamImg)
                    scaleType = ImageView.ScaleType.FIT_CENTER
                    setImageResource(op.imageResId)
                    contentDescription = op.text
                })
                cont.addView(android.widget.TextView(this).apply {
                    layoutParams = android.widget.LinearLayout.LayoutParams(
                        anchoItem, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    text = op.text
                    textSize = 13f
                    gravity = android.view.Gravity.CENTER
                    maxLines = 2
                })
                dlg.dgPuenteContainer.addView(cont)
                vistasPuente.add(op.text to cont)
            }
            marcarPuente()
        }

        actualizarSeccionCompuesta()

        // --- Forma (vista frontal) ---
        when (metaForma) {
            "circular"  -> dlg.dgRbFormaCircular.isChecked = true
            "poligonal" -> dlg.dgRbFormaPoligonal.isChecked = true
            else        -> dlg.dgRbFormaPlano.isChecked = true
        }
        dlg.dgRgForma.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                // seleccionarModelo y salirDeCircular ya dejan `metaForma` en su sitio.
                R.id.dgRbFormaCircular -> seleccionarModelo("circular", R.drawable.novaci, "nci")
                R.id.dgRbFormaPoligonal -> {
                    salirDeCircular()
                    metaForma = "poligonal" // solo dato por ahora
                }
                else -> salirDeCircular()
            }
        }

        // Reflejar la selección actual al abrir el diálogo.
        refrescarSeleccion()
        dlg.dgCantidad.setText(cantidadProducto.toString())

        AlertDialog.Builder(this)
            .setTitle("Opciones de la ventana")
            .setView(dlg.root)
            .setPositiveButton("Listo") { _, _ ->
                // Cuántas ventanas iguales: en blanco o 0 vale 1.
                cantidadProducto = dlg.dgCantidad.text?.toString()?.trim()?.toIntOrNull()
                    ?.coerceAtLeast(1) ?: 1
                // Guardar el encuentro (solo dato): 1 = colinda, 0 = al vacío.
                metaEncuentro = buildString {
                    append(if (dlg.dgCbTop.isChecked) '1' else '0')
                    append(if (dlg.dgCbRight.isChecked) '1' else '0')
                    append(if (dlg.dgCbBottom.isChecked) '1' else '0')
                    append(if (dlg.dgCbLeft.isChecked) '1' else '0')
                }
            }
            .show()
    }

    /** Actualiza tvNombreModelo mostrando geometría y modelo con colores distintos. */
    private fun actualizarNombreModelo() {
        val colorGeo   = ContextCompat.getColor(this, R.color.color)
        val colorMod   = ContextCompat.getColor(this, R.color.naranja)
        val ssb = SpannableStringBuilder()
        val g = nombreGeometria
        ssb.append(g)
        ssb.setSpan(ForegroundColorSpan(colorGeo), 0, g.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.append("  ")
        val start = ssb.length
        ssb.append(nombreModelo)
        ssb.setSpan(ForegroundColorSpan(colorMod), start, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        // Remate (cuando no es normal): se combina con la modulación.
        if (modeloRemate != "nn") {
            ssb.append(" · ")
            val r = ssb.length
            ssb.append(nombreRemate)
            ssb.setSpan(ForegroundColorSpan(colorGeo), r, ssb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.tvNombreModelo.text = ssb
    }

    // Tipos de texto que pertenecen a la geometría — el scroll no los sobreescribe
    private val textosGeometria = setOf("nl", "nu", "ns", "ncu")

    /**
     * Selecciona un modelo del scroll.
     * Si la geometría activa es "plano" (texto no está en textosGeometria),
     * el modelo sí actualiza [texto] para que disenoSimbolico() genere el diseño
     * correcto (ej. "nr" → mochetas abajo). Con geometría compuesta (nl/nu/ns/ncu)
     * [texto] permanece intacto.
     */
    private fun seleccionarModelo(nombre: String, drawableRes: Int, textoMod: String = "") {
        // Entrar en circular guarda la modulación de partida, para poder devolverla al salir.
        // Salir por cualquier otra vía (elegir otra modulación) la da por gastada.
        if (textoMod == "nci" && textoModelo != "nci") {
            modulacionPrevia = Triple(nombreModelo, drawableModelo, textoModelo)
        } else if (textoMod.isNotBlank() && textoMod != "nci" && textoModelo == "nci") {
            modulacionPrevia = null
        }
        nombreModelo = nombre
        actualizarNombreModelo()
        if (textoMod.isNotBlank()) {
            textoModelo = textoMod                              // siempre se guarda el modelo
            if (texto !in textosGeometria) texto = textoMod    // solo sobreescribe texto si geometría es plana
            // La FORMA va pegada al modelo, porque circular se implementa como el modelo `nci`:
            // entrar en él es ponerse circular y elegir cualquier otra modulación es dejar de
            // serlo. La forma poligonal no se toca: esa sí es independiente.
            metaForma = when {
                textoMod == "nci" -> "circular"
                metaForma == "circular" -> "plano"
                else -> metaForma
            }
        }
        // Si la modulación restringe el puente actual (p. ej. INA ncfc), pasar al permitido.
        if (puenteMultipleNoPermitido(puente)) aplicarPuentePredeterminadoSinMultiple()
        drawableModelo = drawableRes
        binding.ivDiseno.setImageResource(drawableRes)
        binding.ivDiseno.visibility = View.VISIBLE
        binding.svModelos.visibility = View.GONE
        mostrandoBocetoOriginal = false
        actualizarVisibilidadMochetaInferior()
    }

    /**
     * Devuelve la modulación que había antes de pasar a circular. Si no se guardó ninguna (por
     * ejemplo, se entró a la pantalla ya en circular), vuelve al modelo normal, que es el de
     * arranque. Sin esto el modelo se quedaba clavado en `nci` y el diseño seguía saliendo
     * circular aunque la forma dijera rectangular.
     */
    private fun salirDeCircular() {
        if (textoModelo != "nci") return
        val previa = modulacionPrevia ?: Triple("normal", R.drawable.ic_fichad3a, "nn")
        modulacionPrevia = null
        seleccionarModelo(previa.first, previa.second, previa.third)
    }

    /**
     * Selección del eje REMATE (mochetas), independiente de la modulación (patrón de hojas).
     * No toca [texto]/[textoModelo]; solo fija [modeloRemate] para que el remate (invertido,
     * doble puente) se combine con cualquier modulación.
     */
    private fun seleccionarRemate(nombre: String, token: String) {
        modeloRemate = token
        nombreRemate = nombre
        // Invertido/doble puente no admiten Múltiple: si lo estaba, pasar al preestablecido.
        if (puenteMultipleNoPermitido(puente)) {
            aplicarPuentePredeterminadoSinMultiple()
        }
        actualizarNombreModelo()
        actualizarVisibilidadMochetaInferior()
    }

    /**
     * Selección desde el panel de geometría (vista top).
     * Actualiza ivModeloEspecial, aplica la lógica del tipo (nl/nu/ns/ncu/nn)
     * y cierra el panel. Independiente del scroll svModelos.
     */
    private fun seleccionarDesdePanel(drawableRes: Int, tipo: String) {
        // Imagen de geometría en ivModeloEspecial
        binding.ivModeloEspecial.setImageResource(drawableRes)

        // Lógica equivalente al botón del scroll que representa este tipo
        texto = tipo
        limpiarEstadoNl()
        val (disenoRes, nombreMod) = when (tipo) {
            "nl"  -> R.drawable.noval   to "En L"
            "nu"  -> R.drawable.novau   to "En C"
            "ns"  -> R.drawable.novas   to "Serie"
            "ncu" -> R.drawable.novacu  to "Curvo"
            else  -> R.drawable.ic_fichad3a to "Plano"
        }
        nombreGeometria = nombreMod
        actualizarNombreModelo()
        binding.ivDiseno.setImageResource(disenoRes)
        binding.ivDiseno.visibility = View.VISIBLE
        binding.svModelos.visibility = View.GONE
        mostrandoBocetoOriginal = false
        diseno = when (tipo) {
            "nl"  -> "noval"
            "nu"  -> "novau"
            "ns"  -> "novas"
            "ncu" -> "novacu"
            else  -> "ic_fichad3a"
        }

        when (tipo) {
            "nl" -> {
                binding.lyFlecha.visibility = View.VISIBLE
                binding.lyCuerda.visibility = View.VISIBLE
                maxLados = 2; contadorLado = 1
                binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
                binding.btAgregar.visibility = View.VISIBLE
                binding.btAgregar.isEnabled = true; otros = true
            }
            "nu" -> {
                maxLados = 3; contadorLado = 1
                binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
                binding.btAgregar.visibility = View.VISIBLE
                binding.btAgregar.isEnabled = true; otros = true
            }
            "ns" -> {
                maxLados = -1; contadorLado = 1
                binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
                binding.btAgregar.visibility = View.VISIBLE
                binding.btAgregar.isEnabled = true; otros = true
            }
            "ncu" -> {
                binding.lyFlecha.visibility = View.VISIBLE
                binding.lyCuerda.visibility = View.VISIBLE
                binding.btAgregar.visibility = View.GONE
                binding.btAgregar.isEnabled = false; otros = true
            }
            else -> {
                binding.btAgregar.visibility = View.GONE
                binding.btAgregar.isEnabled = false; otros = false
            }
        }

        binding.lyPanelModelos.visibility = View.GONE
        actualizarVisibilidadMochetaInferior()
    }

    private fun asegurarCamposActivosDesdeUltimoNs() {
        if (ladosNs.isEmpty()) return
        val anchoVacio = binding.etAncho.text.isNullOrBlank()
        val altoVacio = binding.etAlto.text.isNullOrBlank()
        if (!anchoVacio && !altoVacio) return
        val ultimo = ladosNs.last()
        binding.etAncho.setText(df1(ultimo.ancho))
        binding.etAlto.setText(df1(ultimo.alto))
        binding.etHoja.setText(df1(ultimo.hoja))
        binding.etPartes.setText(ultimo.divisManual.toString())
    }

    // ==================== NAVEGACIÓN DE SERIE (ns) ====================
    // Tras calcular una serie, btAgregar pasa a "Navegar": al tocarlo carga en los campos
    // (med1) cada lado guardado, uno por clic, mostrando en el botón el lado actual. Así se
    // pueden revisar y editar medidas ya ingresadas (antes quedaban ocultas e inaccesibles).

    /** Activa el modo navegar al terminar el cálculo de serie. Consolida la medida en curso. */
    private fun activarNavegacionNs() {
        if (navegandoNs) {
            // Persistir la edición del lado que se está mostrando.
            guardarCamposEnLadoNs(indiceNs)
        } else {
            val a = binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f
            val al = binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f
            val h = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
            val d = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
            if (a > 0f && al > 0f) {
                val actual = MedidaNl(a, al, h, d)
                val ultimo = ladosNs.lastOrNull()
                if (ultimo == null || !mismaMedida(ultimo, actual)) {
                    ladosNs.add(actual)
                } else {
                    ladosNs[ladosNs.lastIndex] = actual
                }
            }
            if (ladosNs.isEmpty()) return
            navegandoNs = true
            indiceNs = ladosNs.lastIndex
        }
        binding.btAgregar.visibility = View.VISIBLE
        binding.btAgregar.isEnabled = true
        etiquetaNavegarNs()
    }

    /** Avanza cíclicamente al siguiente lado de la serie, guardando lo editado del actual. */
    private fun navegarSiguienteNs() {
        if (ladosNs.isEmpty()) return
        guardarCamposEnLadoNs(indiceNs)
        indiceNs = (indiceNs + 1) % ladosNs.size
        cargarLadoNsEnCampos(indiceNs)
        etiquetaNavegarNs()
        binding.etAncho.requestFocus()
    }

    private fun guardarCamposEnLadoNs(indice: Int) {
        if (indice !in ladosNs.indices) return
        val a = binding.etAncho.text?.toString()?.toFloatOrNull() ?: return
        val al = binding.etAlto.text?.toString()?.toFloatOrNull() ?: return
        if (a <= 0f || al <= 0f) return
        val h = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        val d = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
        ladosNs[indice] = MedidaNl(a, al, h, d)
    }

    private fun cargarLadoNsEnCampos(indice: Int) {
        val m = ladosNs.getOrNull(indice) ?: return
        binding.etAncho.setText(df1(m.ancho))
        binding.etAlto.setText(df1(m.alto))
        binding.etHoja.setText(df1(m.hoja))
        binding.etPartes.setText(m.divisManual.toString())
    }

    private fun etiquetaNavegarNs() {
        binding.btAgregar.text = "Navegar (Lado ${indiceNs + 1}/${ladosNs.size})"
    }

    // ==================== FUNCIONES DE CÁLCULO ====================
    private fun df1(defo: Float): String = NovaCalculos.df1(defo)
    private fun cruce(): Float {
        val exacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
        return when (tipoNova) {
            TipoNova.APA -> {
                // Aparente: cruce fijo 0.7f
                if (exacto == 0f) 0.7f else exacto
            }
            TipoNova.INA, TipoNova.PIV -> {
                // Inaparente/Pivotante: usar NovaInaCalculos.cruce (lógica de vidrio3)
                val ancho = binding.etAncho.text.toString().toFloat()
                val divisManual = binding.etPartes.text.toString().toInt()
                val divisiones = NovaInaCalculos.divisiones(ancho, divisManual)
                NovaInaCalculos.cruce(exacto, divisiones)
            }
        }
    }
    private fun ancho(): Float = binding.etAncho.text.toString().toFloat()
    private fun alto(): Float = binding.etAlto.text.toString().toFloat()
    private fun altoHoja(): Float {
        val alto = alto()
        val hoja = binding.etHoja.text.toString().toFloat()
        val corre = if (hoja >= alto) alto else hoja
        return if (hoja == 0f) alto / 7 * 5 else corre
    }
    private fun divisiones(): Int {
        return try {
            val anchoTexto = binding.etAncho.text?.toString() ?: ""
            val ancho = if (anchoTexto.isEmpty()) 120f else anchoTexto.toFloatOrNull() ?: 120f
            val divisTexto = binding.etPartes.text?.toString() ?: ""
            val divis = if (divisTexto.isEmpty()) 0 else divisTexto.toIntOrNull() ?: 0
            NovaCalculos.divisiones(arcoCurvo(ancho), divis)
        } catch (e: Exception) { 1 }
    }

    private data class EntradasCalculo(
        val ancho: Float,
        val alto: Float,
        val hoja: Float,
        val divisManual: Int,
        val cruce: Float
    )

    private fun leerEntradasCalculo(): EntradasCalculo {
        val ancho = binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f
        val alto = binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f
        val hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        val divisManual = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
        return EntradasCalculo(ancho, alto, hoja, divisManual, cruce())
    }

    private data class GeometriaCurva(
        val cuerda: Float,
        val flecha: Float,
        val radio: Float,
        val angulo: Float,
        val arco: Float,
        val cuerdaParte: Float,
        val flechaParte: Float,
        val arcoParte: Float
    )

    private fun flechaCurvaDesdeCampos(): Float {
        return binding.etFlecha.text
            ?.toString()
            ?.trim()
            ?.replace(",", ".")
            ?.toFloatOrNull()
            ?.coerceAtLeast(0f)
            ?: 0f
    }

    // Cuerda introducida manualmente (vano real). 0 si no se indicó.
    private fun cuerdaManualCurva(): Float {
        if (texto != "ncu") return 0f
        return binding.etCuerda.text
            ?.toString()
            ?.trim()
            ?.replace(",", ".")
            ?.toFloatOrNull()
            ?.coerceAtLeast(0f)
            ?: 0f
    }

    // Arco de la curva: lo que se reparte, se cuenta y se corta. Si se indica cuerda
    // (+ flecha) el arco se calcula de forma exacta con cuerda + flecha; si no, el
    // 'ancho' introducido ya es el arco. El total no depende del número de partes.
    private fun arcoCurvo(anchoEntrada: Float): Float =
        geometriaCurva(anchoEntrada, 1)?.arco ?: anchoEntrada

    // Geometría de la curva. Dos formas de obtenerla:
    //   - Si hay cuerda (+ flecha): arco calculado con la fórmula clásica cuerda+flecha
    //     (alternativa más exacta cuando se conoce el vano y se elige la flecha).
    //   - Si no: el 'ancho' introducido ES el arco, y la cuerda se deriva por la inversa.
    private fun geometriaCurva(anchoEntrada: Float, divisiones: Int): GeometriaCurva? {
        if (texto != "ncu") return null
        val flecha = flechaCurvaDesdeCampos()
        val cuerdaManual = cuerdaManualCurva()
        val partes = divisiones.coerceAtLeast(1)

        // Sin flecha no hay curva: el arco es el propio ancho (o la cuerda si se indicó).
        if (flecha <= 0f) {
            val base = (if (cuerdaManual > 0f) cuerdaManual else anchoEntrada).coerceAtLeast(0f)
            if (base <= 0f) return null
            val parte = base / partes
            return GeometriaCurva(base, 0f, 0f, 0f, base, parte, 0f, parte)
        }

        val radioD: Double
        val anguloD: Double
        val arco: Float
        if (cuerdaManual > 0f) {
            // cuerda + flecha -> radio -> arco
            radioD = (flecha / 2.0) + (cuerdaManual.toDouble() * cuerdaManual) / (8.0 * flecha)
            val ratio = (cuerdaManual / (2.0 * radioD)).coerceIn(-1.0, 1.0)
            anguloD = 2.0 * kotlin.math.asin(ratio)
            arco = (radioD * anguloD).toFloat()
        } else {
            // arco (ancho) + flecha -> radio (inversa). El semiángulo phi cumple
            // (1 - cos phi) / (2*phi) = flecha/arco, creciente en phi ∈ (0, PI/2];
            // su máximo (semicírculo) vale 1/PI.
            arco = anchoEntrada.coerceAtLeast(0f)
            if (arco <= 0f) return null
            val ratio = (flecha / arco).toDouble()
            val phi = if (ratio >= 1.0 / Math.PI) {
                Math.PI / 2.0
            } else {
                var lo = 1e-6
                var hi = Math.PI / 2.0
                repeat(80) {
                    val mid = (lo + hi) / 2.0
                    val g = (1.0 - kotlin.math.cos(mid)) / (2.0 * mid)
                    if (g < ratio) lo = mid else hi = mid
                }
                (lo + hi) / 2.0
            }
            radioD = arco / (2.0 * phi)
            anguloD = 2.0 * phi
        }

        val radio = radioD.toFloat()
        val angulo = anguloD.toFloat()
        val cuerda = (2.0 * radioD * kotlin.math.sin(anguloD / 2.0)).toFloat()
        val anguloParte = anguloD / partes
        val cuerdaParte = (2.0 * radioD * kotlin.math.sin(anguloParte / 2.0)).toFloat()
        val flechaParte = (radioD * (1.0 - kotlin.math.cos(anguloParte / 2.0))).toFloat()

        return GeometriaCurva(
            cuerda = cuerda,
            flecha = flecha,
            radio = radio,
            angulo = angulo,
            arco = arco,
            cuerdaParte = cuerdaParte,
            flechaParte = flechaParte,
            arcoParte = arco / partes
        )
    }

    private fun resumenGeometriaCurva(geometria: GeometriaCurva): String {
        return "Curvo total -> Arco: ${df1(geometria.arco)}; Cuerda: ${df1(geometria.cuerda)}; Flecha: ${df1(geometria.flecha)}\n" +
            "Curvo/div -> Arco: ${df1(geometria.arcoParte)}; Cuerda: ${df1(geometria.cuerdaParte)}; Flecha: ${df1(geometria.flechaParte)}"
    }

    private fun tipoCalculoPrincipal(): String = if (tipoNova == TipoNova.APA) "apa" else "ina"

    private fun valorParanteUFijosApa(): Float {
        val p = puente.lowercase().trim()
        val esMultiple = p.contains("multi") || p.contains("múlt") || p.contains("múlt")
        val esGorrito = p.contains("gorrito") || p.contains("ltiple")
        return if (esMultiple || esGorrito) 2.5f else tubo
    }

    private fun esGeometriaPlana(): Boolean =
        texto != "nl" && texto != "nu" && texto != "ns" && texto != "ncu" && texto != "nci"

    /** Opción de tubo del esquinero; null si es "ninguno" (sin tubo ni descuento en esquina). */
    private fun opcionEsquinero(): NovaSpinnerData.SpinnerTubos? {
        if (metaEsquinero.equals("ninguno", ignoreCase = true)) return null
        val ops = NovaSpinnerData.obtenerOpcionesTubo()
        return ops.firstOrNull { it.text == metaEsquinero } ?: ops.getOrNull(2) // default tubo 2⅜x1
    }

    /** Nº de lados laterales (izquierda/derecha) al vacío, solo en geometría plana. */
    private fun ladosVaciosLaterales(): Int {
        if (!esGeometriaPlana()) return 0
        val e = metaEncuentro.padEnd(4, '1')
        var n = 0
        if (e[1] == '0') n++  // derecha
        if (e[3] == '0') n++  // izquierda
        return n
    }

    /**
     * APA: cada lado lateral al vacío añade un parante que se descuenta del ancho del sistema
     * (2.5 cm si el puente es Múltiple, o el valor del spinner). En INA no hay descuento.
     */
    /** Ancho del perfil elegido para cerrar un lado al vacío (franja "Parante (lado al vacío)"). */
    private fun valorParanteVacio(): Float =
        NovaSpinnerData.obtenerOpcionesTubo().firstOrNull { it.text == metaParanteVacio }?.valor
            ?: valorParanteUFijosApa()

    private fun descuentoAnchoLateralApa(): Float =
        if (tipoNova == TipoNova.APA) ladosVaciosLaterales() * valorParanteVacio() else 0f

    /** Nº de lados verticales (arriba/abajo) al vacío, solo en geometría plana. */
    private fun ladosVaciosVerticales(): Int {
        if (!esGeometriaPlana()) return 0
        val e = metaEncuentro.padEnd(4, '1')
        var n = 0
        if (e[0] == '0') n++  // arriba
        if (e[2] == '0') n++  // abajo
        return n
    }

    /**
     * APA: sin pared arriba/abajo se añade un falso puente que se descuenta del alto.
     * En INA NO se descuenta (solo se suma la cantidad de puentes).
     */
    private fun descuentoAltoVertical(): Float =
        if (tipoNova == TipoNova.APA) ladosVaciosVerticales() * valorParanteVacio() else 0f

    /**
     * Corridas horizontales de puente: 1 en el remate normal y 2 en doble puente (una bajo la
     * mocheta superior y otra sobre la inferior). Se multiplica con [escalarCantidadesTexto].
     */
    private fun filasDePuente(): Int = if (esModeloDoblePuente()) 2 else 1

    private fun esPuenteMultipleOGorrito(): Boolean {
        val p = puente.lowercase().trim()
        return p.contains("multi") || p.contains("múlt") || p.contains("múlt") || p.contains("gorrito") || p.contains("ltiple")
    }


    private fun tipoPaquete(): String = when (tipoNova) {
        TipoNova.APA -> "apa"
        TipoNova.INA -> "ina"
        TipoNova.PIV -> "piv"
    }

    private fun mostrarFijoCorredizo(alto: Float, altoHoja: Float): Boolean {
        return (altoHoja >= alto) || (modeloRemate == "nr") || usarFcComoUFelpero(alto, altoHoja)
    }

    private fun esPuenteMultiple(): Boolean {
        val p = puente.lowercase().trim()
        return p.contains("multi") || p.contains("múlt") || p.contains("múlt") || p.contains("ltiple")
    }

    private fun esPuenteMultiple(textoPuente: String): Boolean {
        val p = textoPuente.lowercase().trim()
        return p.contains("multi") || p.contains("múlt") || p.contains("múlt") || p.contains("ltiple")
    }
    private fun indiceSpinnerPuente(textoPuente: String): Int {
        val adapter = binding.spinner.adapter ?: return -1
        for (i in 0 until adapter.count) {
            val item = adapter.getItem(i)
            if (item is NovaSpinnerData.SpinnerTubos && item.text.equals(textoPuente, ignoreCase = true)) {
                return i
            }
        }
        return -1
    }

    private fun sincronizarSpinnerPuente(textoPuente: String) {
        val idx = indiceSpinnerPuente(textoPuente)
        if (idx >= 0 && binding.spinner.selectedItemPosition != idx) {
            binding.spinner.setSelection(idx)
        }
    }

    private fun esModeloInvertidoApa(): Boolean {
        return tipoNova == TipoNova.APA && modeloRemate == "nr"
    }

    private fun esModeloInvertidoIna(): Boolean {
        return tipoNova == TipoNova.INA && modeloRemate == "nr"
    }

    private fun esModeloDoblePuente(): Boolean {
        return modeloRemate == "np"
    }

    private fun actualizarVisibilidadMochetaInferior() {
        binding.lyMochetaInf.visibility = if (esModeloDoblePuente()) View.VISIBLE else View.GONE
    }

    private fun mochetaInferiorDoblePuente(): Float {
        if (!esModeloDoblePuente()) return 0f
        return binding.etMochetaInf.text
            ?.toString()
            ?.trim()
            ?.replace(",", ".")
            ?.toFloatOrNull()
            ?.coerceAtLeast(0f)
            ?: 0f
    }

    private fun textoUMochetaDoblePuente(alturasMochetas: List<Float>, us: Float, cantidadBase: Int): String {
        if (alturasMochetas.isEmpty() || cantidadBase <= 0) return ""
        val lineas = linkedMapOf<String, Int>()
        for (altura in alturasMochetas) {
            val key = NovaCalculos.df1(altura - (2f * us))
            lineas[key] = (lineas[key] ?: 0) + cantidadBase
        }
        return lineas.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    // INA ncfc se trata como un solo tramo; para compensar la resistencia estructural solo se
    // permiten estos puentes (paflon 1, paflon 1½, cuadrado 2). El resto se bloquea.
    private val puentesPermitidosIncfc = setOf("paflon 1", "paflon 1½", "tubo 2 x 2")
    private fun esIncfc(): Boolean = tipoNova == TipoNova.INA && textoModelo == "ncfc"

    // Full corredizas (cualquier remate): según las corredizas POR TRAMO se exige un puente más
    // resistente. >2 por tramo → 2⅜ o paflones; >4 por tramo → solo paflones.
    private val puentesPaflon = setOf("paflon 1", "paflon 1½")
    private val puentesDesde2tresOctavos = setOf("tubo 2⅜ x 1", "paflon 1", "paflon 1½")

    /** Máximo de corredizas en un tramo (el tramo más exigente decide el puente) para full corredizas. */
    private fun maxCorredizasPorTramoNfc(): Int {
        val divisManual = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
        val anchoRaw = binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f
        if (anchoRaw <= 0f) return 0
        val ancho = arcoCurvo(anchoRaw)
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        return NovaCalculos.gruposDivisionesMochetaPorModelo(ancho, divisiones, "nfc").maxOrNull() ?: 0
    }

    private fun puenteMultipleNoPermitido(textoPuente: String): Boolean {
        // INA ncfc: solo se permiten paflon 1, paflon 1½ y cuadrado 2 (tubo 2 x 2).
        if (esIncfc()) return textoPuente !in puentesPermitidosIncfc
        // Full corredizas (cualquier remate): el puente depende de las corredizas POR TRAMO.
        if (textoModelo == "nfc") {
            val maxCorrTramo = maxCorredizasPorTramoNfc()
            if (maxCorrTramo > 4) return textoPuente !in puentesPaflon
            if (maxCorrTramo > 2) return textoPuente !in puentesDesde2tresOctavos
            // Hasta 2 corredizas por tramo: solo se bloquea Múltiple y gorrito.
            return esPuenteMultiple(textoPuente) || textoPuente.lowercase().contains("gorrito")
        }
        // Invertido (nr) nunca admite Múltiple, en cualquier geometría/forma/acabado.
        return esPuenteMultiple(textoPuente) &&
                (tipoNova == TipoNova.INA || esModeloDoblePuente() || modeloRemate == "nr")
    }

    private fun puentePredeterminadoSinMultiple(): String {
        // INA ncfc: por defecto paflon 1 (uno de los permitidos).
        if (esIncfc()) return "paflon 1"
        // Full corredizas: el predeterminado respeta las corredizas por tramo y no usa múltiple/gorrito.
        if (textoModelo == "nfc") {
            val maxCorr = maxCorredizasPorTramoNfc()
            if (maxCorr > 4) return "paflon 1"
            if (maxCorr > 2) return puenteInaDefault   // tubo 2⅜ x 1
            return puenteNpDefault                     // tubo 2 x 1 (no múltiple/gorrito)
        }
        // Invertido: preestablecido tubo 2⅜ x 1. INA: igual. Resto (doble puente): tubo 2 x 1.
        return if (modeloRemate == "nr" || tipoNova == TipoNova.INA) puenteInaDefault else puenteNpDefault
    }

    private fun aplicarPuentePredeterminadoSinMultiple() {
        tubo = 2.5f
        puente = puentePredeterminadoSinMultiple()
        binding.tvP.text = puente
        escribirValorPuente(tubo)
        sincronizarSpinnerPuente(puente)
    }

    private fun usarFcComoUFelpero(alto: Float, altoHoja: Float): Boolean {
        return !esPuenteMultiple() && altoHoja < alto && texto != "nr"
    }

    private fun mostrarUFelperoIna(alto: Float, altoHoja: Float): Boolean {
        return esPuenteMultiple() && altoHoja < alto
    }

    private fun textoFijoCorreInaInvertido(ancho: Float): String {
        return "${NovaCalculos.df1(ancho)} = 1"
    }

    /**
     * Suma [cantidad] a la línea cuya medida coincide con [medida] ("medida = N");
     * si no existe esa línea, añade una nueva. Usado para el falso puente con perfil
     * elegido (se suma a la cantidad del puente mostrado, mismo perfil y medida = ancho).
     */
    private fun agregarPuenteFalso(texto: String, medida: String, cantidad: Int): String {
        if (cantidad <= 0) return texto
        val lineas = texto.lines().toMutableList()
        val patron = Regex("^\\s*${Regex.escape(medida)}\\s*=\\s*(\\d+)\\s*$")
        for (i in lineas.indices) {
            val m = patron.matchEntire(lineas[i])
            if (m != null) {
                val n = (m.groupValues[1].toIntOrNull() ?: 0) + cantidad
                lineas[i] = "$medida = $n"
                return lineas.joinToString("\n")
            }
        }
        val base = if (texto.isBlank()) "" else "$texto\n"
        return "$base$medida = $cantidad"
    }

    private fun escalarCantidadesTexto(texto: String, factor: Int): String {
        if (factor <= 1 || texto.isBlank()) return texto
        val patron = Regex("^(.*=\\s*)(\\d+)\\s*$")
        return texto.lines().joinToString("\n") { linea ->
            val m = patron.matchEntire(linea.trim())
            if (m != null) {
                val prefijo = m.groupValues[1]
                val cantidad = m.groupValues[2].toIntOrNull() ?: return@joinToString linea
                "$prefijo${cantidad * factor}"
            } else {
                linea
            }
        }
    }

    // ==================== U TEXTO ====================
    @SuppressLint("SetTextI18n")
    private fun uTexto() {
        val entrada = leerEntradasCalculo()
        val alto = entrada.alto - descuentoAltoVertical()
        val hoja = entrada.hoja
        val us = binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f
        val divisManual = entrada.divisManual
        val cruce = entrada.cruce

        // Arco de la curva: lo que se reparte/cuenta/corta. Si hay cuerda + flecha se
        // calcula con esos (más exacto); si no, el 'ancho' introducido ya es el arco.
        val ancho = arcoCurvo(entrada.ancho) - descuentoAnchoLateralApa()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val mochetaInferior = mochetaInferiorDoblePuente()

        if (tipoNova == TipoNova.APA) {
            // === APARENTE ===
            val nFijos = when {
                textoModelo == "nff" -> divisiones
                // Full corredizas: no hay fijos → U de fijos = 0.
                textoModelo == "nfc" -> 0
                // ncfc: cantidad real de fijos del patrón invertido (no la del clásico).
                textoModelo == "ncfc" -> NovaCalculos.nFijos(divisiones, "ncfc")
                else -> NovaCalculos.nFijos(ancho, divisiones)
            }
            // Full fijos con 2 divisiones: sin corredizas no hay tope; esa posición se reemplaza
            // con U, por lo que la U parante son 2 (no 1 como en la lógica de corrediza).
            val fijoUParante = when {
                textoModelo == "nff" && divisiones == 2 -> 2
                // Full corredizas: no hay fijos → no hay U parante de fijo.
                textoModelo == "nfc" -> 0
                // ncfc: U parante solo por lados del fijo que colindan con pared o parante.
                textoModelo == "ncfc" -> NovaCalculos.uParanteFijosNcfc(ancho, divisiones)
                divisiones == 2 -> 1
                else -> NovaCalculos.fijoUParante(divisiones, ancho)
            }
            val alturasDoblePuente = if (esModeloDoblePuente()) {
                NovaInaCalculos.alturasMochetasPorModelo(
                    modelo = modeloRemate,
                    alto = alto,
                    altoHoja = altoHoja,
                    alturaPuente = tubo,
                    mochetaInferior = mochetaInferior
                ).lista()
            } else {
                emptyList()
            }
            val factorMochetas = if (esModeloDoblePuente()) alturasDoblePuente.size.coerceAtLeast(1) else 1
            val mochetaUParanteBase = NovaCalculos.mochetaUParante(divisiones, ancho)
            val mochetaUParante = mochetaUParanteBase * factorMochetas
            val nPuentes = NovaCalculos.nPuentesEfectivos(ancho, divisiones)
            val cruceModelo = if (textoModelo == "nff") 0f else cruce
            val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruceModelo, "apa", valorParanteUFijosApa())
            // Full fijos: la U de fijos horizontal es el MARCO continuo del tramo (arriba y abajo),
            // no se reparte entre paños ni descuenta el parante interno; solo descuenta el puente
            // entre tramos. Por eso vale el ancho completo del tramo (ej. ancho 120 con 2 paños →
            // 120, no 117.5). Son 2 piezas por tramo (una arriba y otra abajo).
            val nTramosNff = nPuentes.coerceAtLeast(1)
            val uFijosTexto = if (textoModelo == "nff") (ancho - (nTramosNff - 1) * 2.5f) / nTramosNff else uFijos
            val nFijosTextoU = when {
                textoModelo == "nff" -> 2 * nTramosNff
                // ncfc: 1 U por módulo fijo (la línea se reemplaza luego por corridas de fijos).
                else -> nFijos
            }
            val uParante = altoHoja - (2*us)
            val altoMocheta = if (esModeloDoblePuente()) {
                alturasDoblePuente.firstOrNull() ?: 0f
            } else {
                NovaCalculos.altoMocheta(alto, altoHoja, tubo)
            }
              val uMocheta = altoMocheta - (2f * us)
            val mPuentes1 = NovaCalculos.mPuentes1(ancho, divisiones, "apa")
            val mPuentes2 = NovaCalculos.mPuentes2(ancho, divisiones, "apa")

            binding.tvU.text = NovaPerfilesHelper.obtenerEtiquetaU(us)
              // Se usa "nn" para conservar exactamente el comportamiento previo de esta pantalla.
              val textoBaseU = NovaPerfilesHelper.generarTextoU(
                  texto = "nn",
                  alto = alto,
                  hoja = altoHoja,
                  us = us,
                divisiones = divisiones,
                uFijos = uFijosTexto,
                uParante = uParante,
                uMocheta = uMocheta,
                  uSuperior = mPuentes1,
                  uSuperior2 = mPuentes2,
                nFijos = nFijosTextoU,
                fijoUParante = fijoUParante,
                mochetaUParante = mochetaUParante,
                  nPuentes = nPuentes,
                  puente = puente
              )

              val textoUMochetaTramosBase = if (alto > altoHoja && us != 0f) {
                  NovaCalculos.textoUMochetaPorTramosAparente(ancho, divisiones, puente, tubo, texto)
              } else {
                  ""
              }
              val textoUMochetaTramosEscalado = escalarCantidadesTexto(textoUMochetaTramosBase, factorMochetas)
              // Full fijos: la U de la franja sistema tiene la MISMA medida por tramo que la
              // mocheta y se SUMA a esa línea (la línea propia de U de fijos se filtra por
              // coincidir con mPuentes1). El Múltiple incorpora su U en la línea del puente, así
              // que la franja aporta solo 1/tramo con Múltiple y 2/tramo en lo demás (gorrito
              // incluido). Con la mocheta (1/tramo en Múltiple y gorrito; 2/tramo en lo demás)
              // el total por tramo queda: Múltiple 2, gorrito 3, otros 4.
              val textoUMochetaTramos = if (textoModelo == "nff" && textoUMochetaTramosBase.isNotBlank()) {
                  val franjaPorTramo = if (esPuenteMultiple()) 1 else 2
                  val franjaTramos = escalarCantidadesTexto(
                      NovaCalculos.textoTramosUnitarioAparente(ancho, divisiones, puente, tubo, texto), franjaPorTramo
                  )
                  combinarTextoCantidades(textoUMochetaTramosEscalado, franjaTramos)
              } else {
                  textoUMochetaTramosEscalado
              }

              // Este filtro quita la línea de U de fijos cuando su valor coincide con el puente
              // (mPuentes1/2), porque CON mocheta esa U se reincorpora fundida en la línea de
              // mocheta por tramos. SIN mocheta no hay nada que la reincorpore, así que filtrarla
              // la haría desaparecer (dejaría solo el alto). Por eso solo se filtra si hay mocheta.
              val hayMocheta = alto > altoHoja
              val patronUSuperior1 = Regex("^${Regex.escape(NovaCalculos.df1(mPuentes1))}\\s*=\\s*\\d+\\s*$")
              val patronUSuperior2 = Regex("^${Regex.escape(NovaCalculos.df1(mPuentes2))}\\s*=\\s*\\d+\\s*$")
              val baseSinAnchoIgual = textoBaseU
                  .lineSequence()
                  .map { it.trimEnd() }
                  .filter {
                      it.isNotBlank() &&
                              (!hayMocheta ||
                                      (!patronUSuperior1.matches(it.trim()) &&
                                              !patronUSuperior2.matches(it.trim())))
                  }
                  .toMutableList()

              if (textoUMochetaTramos.isNotBlank()) {
                  val idxParante = baseSinAnchoIgual.indexOfFirst { it == "${NovaCalculos.df1(uParante)} = $fijoUParante" }
                  val lineasMocheta = textoUMochetaTramos.lines().filter { it.isNotBlank() }
                  if (idxParante >= 0) {
                      baseSinAnchoIgual.addAll(idxParante + 1, lineasMocheta)
                  } else {
                      baseSinAnchoIgual.addAll(lineasMocheta)
                  }
              }
              val textoUMochetasDoble = if (esModeloDoblePuente()) {
                  textoUMochetaDoblePuente(alturasDoblePuente, us, mochetaUParanteBase)
              } else {
                  ""
              }
              if (textoUMochetasDoble.isNotBlank() && textoUMochetasDoble.lines().size > 1) {
                  val patronUMocheta = Regex("^${Regex.escape(NovaCalculos.df1(uMocheta))}\\s*=\\s*\\d+\\s*$")
                  val idxUMocheta = baseSinAnchoIgual.indexOfFirst { patronUMocheta.matches(it.trim()) }
                  val lineasUMocheta = textoUMochetasDoble.lines().filter { it.isNotBlank() }
                  if (idxUMocheta >= 0) {
                      baseSinAnchoIgual.removeAt(idxUMocheta)
                      baseSinAnchoIgual.addAll(idxUMocheta, lineasUMocheta)
                  } else {
                      baseSinAnchoIgual.addAll(lineasUMocheta)
                  }
              }
              var textoFinalU = baseSinAnchoIgual.joinToString("\n")
              // Full fijos con un solo paño: la U ya sale correcta desde la base como dos medidas
              // (ancho = 2 y alto = 2, con su descuento). Esta corrección es del modelo clásico "nn"
              // (aplasta la U a una sola línea uFijos = 4); aplicarla a "nff" borraría el alto y
              // dejaría solo "U fijos". Por eso se excluye full fijos.
              if (divisiones == 1 && textoModelo != "nff") {
                  val cantidadUFijos = if (esPuenteMultipleOGorrito()) 2 else 4
                  val lineaUFijos = "${NovaCalculos.df1(uFijos)} = $cantidadUFijos"
                  val patronLineaUFijos = Regex("^${Regex.escape(NovaCalculos.df1(uFijos))}\\s*=\\s*\\d+\\s*$")
                  val lineas = textoFinalU.lines().filter { it.isNotBlank() }.toMutableList()
                  val idx = lineas.indexOfFirst { patronLineaUFijos.matches(it.trim()) }
                  if (idx >= 0) {
                      lineas[idx] = lineaUFijos
                  } else {
                      lineas.add(0, lineaUFijos)
                  }
                  textoFinalU = lineas.joinToString("\n")
              }
              if (textoModelo == "ncfc") {
                  // Reemplazar la línea de U de fijos por corridas: fijos adyacentes suman su medida.
                  val runText = NovaCalculos.uFijosRunsNcfc(ancho, divisiones, uFijos)
                  val patronUFijo = Regex("^${Regex.escape(NovaCalculos.df1(uFijos))}\\s*=\\s*\\d+\\s*$")
                  val lineas = textoFinalU.lines().toMutableList()
                  val idx = lineas.indexOfFirst { patronUFijo.matches(it.trim()) }
                  if (idx >= 0) {
                      lineas.removeAt(idx)
                      if (runText.isNotBlank()) lineas.addAll(idx, runText.lines())
                  } else if (runText.isNotBlank()) {
                      lineas.add(0, runText)
                  }
                  textoFinalU = lineas.joinToString("\n")
                  // La U parante puede dar 0 (fijo que no colinda con pared/parante); no mostrarla.
                  textoFinalU = filtrarLineasValorCero(textoFinalU)
              }
              // Full corredizas: la U son las U horizontales (mismo valor que los puentes, por
              // tramo) MÁS la U parante de mocheta (vertical) = 2 por tramo. Sin mocheta no van.
              if (textoModelo == "nfc") {
                  textoFinalU = if (alto > altoHoja && us != 0f) {
                      val nTramosNfc = NovaCalculos.gruposDivisionesMochetaPorModelo(ancho, divisiones, "nfc").size
                      val vertical = "${NovaCalculos.df1(uMocheta)} = ${2 * nTramosNfc}"
                      if (textoUMochetaTramos.isNotBlank()) "$textoUMochetaTramos\n$vertical" else vertical
                  } else ""
              }

              binding.txU.text = textoFinalU
        } else {
            // === INAPARENTE/PIVOTANTE: lógica de vidrio3/NovaIna ===
            binding.tvU.text = NovaPerfilesHelper.obtenerEtiquetaU(us)
            val divisionesU = if (tipoNova == TipoNova.INA && esModeloDoblePuente()) 1 else divisiones
            if (textoModelo == "nfc") {
                // Full corredizas INA: la U son solo las del marco de mocheta, con las mismas
                // reglas que el resto de INA: la superior es uSuperior(ancho) = 1 (arriba) y los
                // costados son uParante2 = 2. Sin mocheta (altoHoja>=alto) no va.
                binding.txU.text = if (alto > altoHoja && us != 0f) {
                    val superior = NovaInaCalculos.uSuperior(ancho)
                    val vertical = NovaInaCalculos.uParante2(alto, altoHoja, us)
                    "${NovaCalculos.df1(superior)} = 1\n${NovaCalculos.df1(vertical)} = 2"
                } else ""
            } else {
                binding.txU.text = NovaInaCalculos.calcularTextoU(
                    ancho = ancho,
                    alto = alto,
                    hoja = hoja,
                    us = us,
                    divisiones = divisionesU,
                    cruceExacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f,
                    incluirUFijos = !esModeloInvertidoIna(),
                    esFullFijos = textoModelo == "nff",
                    esCfc = textoModelo == "ncfc"
                )
            }
        }
    }

    // ==================== OTROS ALUMINIOS ====================
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    private fun otrosAluminios() {
        // En plano el parante no es el esquinero: etiqueta por defecto.
        binding.tvT.text = getString(R.string.parante)
        val entrada = leerEntradasCalculo()
        val alto = entrada.alto - descuentoAltoVertical()
        val hoja = entrada.hoja
        val divisManual = entrada.divisManual
        val cruce = entrada.cruce

        // Arco de la curva: lo que se reparte/cuenta/corta. Si hay cuerda + flecha se
        // calcula con esos (más exacto); si no, el 'ancho' introducido ya es el arco.
        val ancho = arcoCurvo(entrada.ancho) - descuentoAnchoLateralApa()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val nCorredizas = if (textoModelo == "nff") 0 else NovaCalculos.nCorredizas(ancho, divisiones)
        // Falso puente (APA, sin pared arriba/abajo): si el puente es Múltiple es un "tubo 2.5"
        // aparte; con otro perfil (p. ej. paflón) se suma a la cantidad del puente elegido.
        val nFalsoPuente = if (tipoNova == TipoNova.APA) ladosVaciosVerticales() else 0
        val falsoPuenteEsTubo = esPuenteMultipleOGorrito()
        val nPuentes = NovaCalculos.nPuentesEfectivos(ancho, divisiones)
        val portafelpa = NovaCalculos.portafelpa(altoHoja)
        val divDePortas = NovaCalculos.divDePortas(divisiones, nCorredizas)

        // Tipo de ventana para fórmulas
        val tipoCalculo = tipoCalculoPrincipal()
        val mPuentes1 = NovaCalculos.mPuentes1(ancho, divisiones, tipoCalculo)
        val mPuentes2 = NovaCalculos.mPuentes2(ancho, divisiones, tipoCalculo)
        val paranteApa = if (tipoNova == TipoNova.APA) valorParanteUFijosApa() else 2.5f
        val cruceModelo = if (textoModelo == "nff") 0f else cruce
        val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruceModelo, tipoCalculo, paranteApa)

          if (tipoNova == TipoNova.APA) {
              val us = binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f
                val resultado = NovaPerfilesHelper.calcularOtrosAparente(
                  ancho = ancho,
                  alto = alto,
                  hoja = hoja,
                altoHoja = altoHoja,
                divisiones = divisiones,
                nCorredizas = nCorredizas,
                nPuentes = nPuentes,
                mPuentes1 = mPuentes1,
                mPuentes2 = mPuentes2,
                portafelpa = portafelpa,
                  divDePortas = divDePortas,
                    uFijos = uFijos,
                    tubo = tubo,
                      us = us,
                      puente = puente,
                      modelo = texto,
                      mochetaInferior = mochetaInferiorDoblePuente(),
                      remate = modeloRemate
                )

                val textoTramos = if (alto > altoHoja) {
                    NovaCalculos.textoUMochetaPorTramosAparente(ancho, divisiones, puente, tubo, texto)
                } else {
                    ""
                }
                val textoTramosUnitBase = if (alto > altoHoja) {
                    NovaCalculos.textoTramosUnitarioAparente(ancho, divisiones, puente, tubo, texto)
                } else {
                    ""
                }
              // Original (932353b), cuando "np" era un valor de `texto` y el bloque era uno solo
              // para APA e INA:
              //     val factorMochetas = if (texto == "np") 2 else 1
              //     val textoTramosUnit = escalarCantidadesTexto(textoTramosUnitBase, factorMochetas)
              //     binding.txP.text = textoTramosUnit
              //     binding.txR.text = textoTramosUnit   // el riel también iba x2
              // Cambió dos veces: el remate salió a su propio eje (`modeloRemate`, por eso
              // `esModeloDoblePuente()` y no `texto`) y el riel volvió a uno por tramo (va en el
              // puente de abajo; el felpero en el de arriba). El x2 queda solo para el puente.
              val textoTramosUnitPuente = escalarCantidadesTexto(textoTramosUnitBase, filasDePuente())
              val textoTramosUnitPerfiles = textoTramosUnitBase

              if (textoTramos.isNotBlank()) {
                  binding.txP.text = textoTramosUnitPuente
                  binding.txR.text = textoTramosUnitPerfiles
                  binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else View.VISIBLE
                  binding.lyRiel.visibility = View.VISIBLE
              } else {
                  binding.txP.text = resultado.puentes
                  binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else if (resultado.mostrarPuentes) View.VISIBLE else View.GONE
                  binding.txR.text = resultado.rieles
                  binding.lyRiel.visibility = if (resultado.mostrarRieles) View.VISIBLE else View.GONE
              }
              // Falso puente con perfil elegido (no Múltiple): medida = ancho.
              if (nFalsoPuente > 0 && !falsoPuenteEsTubo) {
                  binding.txP.text = if (alto > altoHoja) {
                      // Hay puente real: se suma el falso a la cantidad del puente mostrado.
                      agregarPuenteFalso(binding.txP.text?.toString().orEmpty(), NovaCalculos.df1(ancho), nFalsoPuente)
                  } else {
                      // No hay puente real (altoHoja >= alto): se muestra solo el falso puente.
                      "${NovaCalculos.df1(ancho)} = $nFalsoPuente"
                  }
                  binding.mulLayout.visibility = View.VISIBLE
              }
              val mostrarFc = mostrarFijoCorredizo(alto, altoHoja)
              if (mostrarFc) {
                  val textoFc = if (textoTramos.isNotBlank()) textoTramosUnitPerfiles else resultado.rieles
                  mostrarFijoCorre(textoFc)
              } else {
                  ocultarFijoCorre()
              }
              // ncfc: riel y u felpera / fijo-corredizo = 1 pieza por tramo, medida = ancho del tramo.
              if (textoModelo == "ncfc") {
                  val nCorrNcfc = NovaCalculos.nCorredizas(divisiones, "ncfc")
                  if (nCorrNcfc > 0) {
                      val textoTramoNcfc = NovaCalculos.anchosTramoNcfc(ancho, divisiones)
                          .groupingBy { NovaCalculos.df1(it) }.eachCount()
                          .entries.joinToString("\n") { "${it.key} = ${it.value}" }
                      binding.txR.text = textoTramoNcfc
                      binding.lyRiel.visibility = View.VISIBLE
                      mostrarFijoCorre(textoTramoNcfc)
                  } else {
                      binding.txR.text = ""
                      binding.lyRiel.visibility = View.GONE
                      ocultarFijoCorre()
                  }
              }
              // Full corredizas: el riel es 1 por cada corrediza; por tramo la medida es el ancho
              // del tramo y la cantidad = corredizas de ese tramo.
              if (textoModelo == "nfc") {
                  val rielNfc = NovaCalculos.rielPorCorredizasNfc(ancho, divisiones, puente, tubo)
                  binding.txR.text = rielNfc
                  binding.lyRiel.visibility = if (rielNfc.isNotBlank()) View.VISIBLE else View.GONE
                  // Riel superior = doble-c: 1 cada 2 corredizas (⌈/2⌉), misma medida por tramo.
                  val dobleC = NovaCalculos.rielSuperiorNfc(ancho, divisiones, puente, tubo)
                  if (dobleC.isNotBlank()) {
                      mostrarFijoCorre(dobleC)
                      binding.tvFc.text = "doble-c"
                  } else {
                      ocultarFijoCorre()
                  }
              }
                  val nParantesDiseno = (when (textoModelo) {
                      "ncc" -> NovaCalculos.nParantesCadaDosCorredizas(divisiones)
                      "n3c" -> NovaCalculos.nParantesCadaTresCorredizas(divisiones)
                      "ncfc" -> NovaCalculos.nParantesDiseno(ancho, divisiones)
                      // Full corredizas: parantes = nº de tramos − 1 (según corredizas por tramo).
                      "nfc" -> (NovaCalculos.gruposDivisionesMochetaPorModelo(ancho, divisiones, "nfc").size - 1).coerceAtLeast(0)
                      else -> NovaCalculos.nParantesDiseno(ancho, divisiones)
                  }) + ladosVaciosLaterales()  // APA: +1 parante por lado lateral al vacío
                  val textoParantes = if (nParantesDiseno > 0) "${NovaCalculos.df1(entrada.alto)} = $nParantesDiseno" else ""
                  binding.txT.text = textoParantes
                  binding.lyTubo.visibility = if (textoParantes.isNotBlank()) View.VISIBLE else View.GONE
            if (textoModelo == "nfc") {
                // Full corredizas: 2 portafelpas por corrediza (todo es corrediza).
                val cantPf = 2 * NovaCalculos.nCorredizas(divisiones, "nfc")
                binding.txPf.text = if (cantPf > 0) "${NovaCalculos.df1(portafelpa)} = $cantPf" else ""
                binding.lyPf.visibility = if (cantPf > 0) View.VISIBLE else View.GONE
            } else if (textoModelo == "ncfc") {
                // ncfc: 2 portafelpas por corrediza + 1 por cada lado de fijo que colinda con corrediza.
                val nCorrNcfc = NovaCalculos.nCorredizas(divisiones, "ncfc")
                val cantPf = 2 * nCorrNcfc + NovaCalculos.portafelpaFijosNcfc(ancho, divisiones)
                binding.txPf.text = if (cantPf > 0) "${NovaCalculos.df1(portafelpa)} = $cantPf" else ""
                binding.lyPf.visibility = if (cantPf > 0) View.VISIBLE else View.GONE
            } else {
                binding.txPf.text = resultado.portafelpaTxt
                binding.lyPf.visibility = if (resultado.mostrarPortafelpa) View.VISIBLE else View.GONE
            }
            binding.txTe.text = resultado.teeTxt
            binding.tLayout.visibility = if (resultado.mostrarTee) View.VISIBLE else View.GONE
            if (textoModelo == "ncfc") {
                // ncfc: ángulo tope = 1 por cada corrediza que toca el inicio o el final del tramo.
                val nTope = NovaCalculos.anguloTopeCorredizasNcfc(ancho, divisiones)
                binding.txTo.text = if (nTope > 0) "${NovaCalculos.df1(altoHoja - 0.9f)} = $nTope" else ""
                binding.lyTo.visibility = if (nTope > 0) View.VISIBLE else View.GONE
            } else if (textoModelo == "nfc") {
                // Full corredizas: ángulo tope a cada lado de cada tramo (todos los bordes son
                // corredizas) → 2 por tramo.
                val nTope = 2 * NovaCalculos.gruposDivisionesMochetaPorModelo(ancho, divisiones, "nfc").size
                binding.txTo.text = if (nTope > 0) "${NovaCalculos.df1(altoHoja - 0.9f)} = $nTope" else ""
                binding.lyTo.visibility = if (nTope > 0) View.VISIBLE else View.GONE
            } else {
                binding.txTo.text = resultado.topeTxt
                binding.lyTo.visibility = if (resultado.mostrarTope) View.VISIBLE else View.GONE
            }
            if (textoModelo == "ncfc" || textoModelo == "nfc") {
                // H = 1 por cada módulo corrediza (misma medida que la H existente).
                val nCorr = NovaCalculos.nCorredizas(divisiones, textoModelo)
                binding.txH.text = if (nCorr > 0) "${NovaCalculos.df1(uFijos)} = $nCorr" else ""
                binding.lyH.visibility = if (nCorr > 0) View.VISIBLE else View.GONE
            } else {
                binding.txH.text = resultado.hacheTxt
                binding.lyH.visibility = if (resultado.mostrarHache) View.VISIBLE else View.GONE
            }

            // Ocultar lyUf en aparente
            binding.lyUf.visibility = View.GONE
            if (divisiones == 1) {
                // Sin corredizas (1 división) no hay "fijo corrediza": la U ya lo reemplaza.
                binding.txR.text = ""
                binding.txPf.text = ""
                binding.txTe.text = ""
                binding.lyRiel.visibility = View.GONE
                ocultarFijoCorre()
                binding.lyUf.visibility = View.GONE
                binding.tLayout.visibility = View.GONE
                binding.lyPf.visibility = View.GONE
            }
        } else {
            // === INAPARENTE/PIVOTANTE: lógica de vidrio3/NovaIna ===
            val cruceExacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
            val resultado = NovaInaCalculos.calcularOtrosAluminios(
                ancho = ancho,
                alto = alto,
                hoja = hoja,
                divisiones = divisiones,
                cruceExacto = cruceExacto,
                puentesExtra = ladosVaciosVerticales(),
                remate = modeloRemate
            )

                val textoTramos = if (alto > altoHoja) {
                  NovaCalculos.textoUMochetaPorTramosAparente(ancho, divisiones, puente, tubo, texto)
                } else {
                    ""
                }
                val textoTramosUnit = if (alto > altoHoja) {
                  NovaCalculos.textoTramosUnitarioAparente(ancho, divisiones, puente, tubo, texto)
                } else {
                    ""
                }

              // PUENTE. Mismo criterio que la rama aparente: el x2 del doble puente sale de
              // `escalarCantidadesTexto` (ver el original de 932353b comentado allá arriba).
              if (textoTramos.isNotBlank()) {
                  binding.txP.text = escalarCantidadesTexto(textoTramosUnit, filasDePuente())
                  binding.txR.text = textoTramosUnit
                  binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else View.VISIBLE
                  binding.lyRiel.visibility = View.VISIBLE
              } else {
                  binding.txP.text = resultado.puentes
                  binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else View.VISIBLE
                  binding.txR.text = resultado.rieles
                  binding.lyRiel.visibility = if (resultado.rieles.isNotBlank()) View.VISIBLE else View.GONE
              }

            val mostrarFc = mostrarFijoCorredizo(alto, altoHoja)
            if (mostrarFc) {
                val textoFc = when {
                    esModeloInvertidoIna() -> textoFijoCorreInaInvertido(ancho)
                    textoTramos.isNotBlank() -> textoTramosUnit
                    else -> resultado.rieles
                }
                val mostrarUfEnFc = mostrarUFelperoIna(alto, altoHoja) || usarFcComoUFelpero(alto, altoHoja)
                val textoUf = if (textoTramos.isNotBlank()) textoTramosUnit else resultado.uFelpero
                val textoPrincipal = if (!esModeloInvertidoIna() && mostrarUfEnFc) textoUf else textoFc
                mostrarFijoCorre(
                    textoPrincipal,
                    if (!esModeloInvertidoIna() && mostrarUfEnFc) R.string.u_felpero else R.string.fijo_corre
                )
            } else {
                ocultarFijoCorre()
            }

            // ncfc INA = un solo tramo: puente, U felpera y riel son al ANCHO TOTAL (1 c/u). El
            // descuento de parante lateral por encuentro sin check ya está incluido en `ancho`.
            if (textoModelo == "ncfc") {
                val medidaTotal = "${NovaCalculos.df1(ancho)} = 1"
                // Riel (siempre que haya corredizas) al ancho total.
                binding.txR.text = medidaTotal
                binding.lyRiel.visibility = View.VISIBLE
                // U felpera al ancho total.
                mostrarFijoCorre(medidaTotal, R.string.u_felpero)
                // Puente al ancho total, solo si existe puente (hay mocheta: altoHoja < alto).
                if (altoHoja < alto) {
                    binding.txP.text = medidaTotal
                    binding.mulLayout.visibility = View.VISIBLE
                } else {
                    binding.mulLayout.visibility = View.GONE
                }
            }

            // Full corredizas INA (un tramo): riel = 1 por corrediza; doble-c (era u felpera) =
            // 1 cada 2 corredizas; puente al ancho total. Medidas por tramo (= ancho, un solo tramo).
            if (textoModelo == "nfc") {
                val rielNfc = NovaCalculos.rielPorCorredizasNfc(ancho, divisiones, puente, tubo)
                binding.txR.text = rielNfc
                binding.lyRiel.visibility = if (rielNfc.isNotBlank()) View.VISIBLE else View.GONE
                val dobleC = NovaCalculos.rielSuperiorNfc(ancho, divisiones, puente, tubo)
                if (dobleC.isNotBlank()) {
                    mostrarFijoCorre(dobleC)
                    binding.tvFc.text = "doble-c"
                } else ocultarFijoCorre()
                if (altoHoja < alto) {
                    binding.txP.text = "${NovaCalculos.df1(ancho)} = 1"
                    binding.mulLayout.visibility = View.VISIBLE
                } else binding.mulLayout.visibility = View.GONE
            }

            // U FELPERO
            binding.txUf.text = ""

            // HACHE
            if (textoModelo == "ncfc" || textoModelo == "nfc") {
                // H = 1 por cada módulo corrediza (misma medida = hache inaparente).
                val nCorr = NovaCalculos.nCorredizas(divisiones, textoModelo)
                val hacheIna = NovaInaCalculos.hache(ancho, divisiones, NovaInaCalculos.cruce(cruceExacto, divisiones))
                binding.txH.text = if (nCorr > 0) "${NovaCalculos.df1(hacheIna)} = $nCorr" else ""
            } else {
                binding.txH.text = resultado.hache
            }

            // ÁNGULO TOPE
            if (textoModelo == "nfc") {
                // Full corredizas: ángulo tope a cada costado (un tramo) → 2.
                binding.txTo.text = "${NovaCalculos.df1(altoHoja - 0.9f)} = 2"
                binding.lyTo.visibility = View.VISIBLE
            } else {
                binding.txTo.text = resultado.angTope
                binding.lyTo.visibility = if (resultado.mostrarAngTope) View.VISIBLE else View.GONE
            }

            // PORTAFELPA
            if (textoModelo == "ncfc") {
                // Contada por módulos sobre el patrón real: 2 por corrediza + 1 por cada lado de
                // fijo que colinda con corrediza.
                val cantPf = NovaCalculos.portafelpaTotalNcfc(ancho, divisiones)
                binding.txPf.text = if (cantPf > 0) "${NovaCalculos.df1(NovaCalculos.portafelpa(altoHoja))} = $cantPf" else ""
                binding.lyPf.visibility = if (cantPf > 0) View.VISIBLE else View.GONE
            } else if (textoModelo == "nfc") {
                // Full corredizas: 2 portafelpas por corrediza.
                val cantPf = 2 * NovaCalculos.nCorredizas(divisiones, "nfc")
                binding.txPf.text = if (cantPf > 0) "${NovaCalculos.df1(NovaCalculos.portafelpa(altoHoja))} = $cantPf" else ""
                binding.lyPf.visibility = if (cantPf > 0) View.VISIBLE else View.GONE
            } else {
                binding.txPf.text = resultado.portafelpa
            }

              // Visibilidad inaparente
              binding.lyH.visibility = if (textoModelo == "ncfc" || textoModelo == "nfc") {
                  if (binding.txH.text.isNotBlank()) View.VISIBLE else View.GONE
              } else if (resultado.mostrarHache) View.VISIBLE else View.GONE
              binding.lyUf.visibility = View.GONE
              binding.tLayout.visibility = View.GONE
              val nParantesDiseno = (when (textoModelo) {
                  "ncc" -> NovaCalculos.nParantesCadaDosCorredizas(divisiones)
                  "n3c" -> NovaCalculos.nParantesCadaTresCorredizas(divisiones)
                  // ncfc/nfc en INA es un solo tramo: no hay parantes que dividan.
                  "ncfc", "nfc" -> 0
                  else -> NovaCalculos.nParantesDiseno(ancho, divisiones)
              }) + ladosVaciosLaterales()  // INA: +1 parante por lado lateral al vacío
              val textoParantes = if (nParantesDiseno > 0) "${NovaCalculos.df1(entrada.alto)} = $nParantesDiseno" else ""
              binding.txT.text = textoParantes
              binding.lyTubo.visibility = if (textoParantes.isNotBlank()) View.VISIBLE else View.GONE

              // TEE vacío en ina
              binding.txTe.text = ""
              if (divisiones == 1) {
                binding.txR.text = ""
                binding.txFc.text = ""
                binding.txPf.text = ""
                binding.lyRiel.visibility = View.GONE
                  binding.fcLayout.visibility = View.GONE
                  binding.lyUf.visibility = View.GONE
                  binding.tLayout.visibility = View.GONE
                  binding.lyPf.visibility = View.GONE
                  binding.lyTubo.visibility = View.GONE
              }
          }

        // Full fijos (nff): no hay corredizas, así que se oculta todo lo relacionado a ellas
        // (riel, fijo corrediza, U felpero, H, portafelpa, tope). El Tee es de mochetas (no de
        // corredizas) y el puente sigue su lógica principal (se muestra si altoHoja < alto).
        if (textoModelo == "nff") {
            binding.txR.text = ""
            binding.txPf.text = ""
            binding.txH.text = ""
            binding.txTo.text = ""
            ocultarFijoCorre()
            binding.lyRiel.visibility = View.GONE
            binding.lyUf.visibility = View.GONE
            binding.lyH.visibility = View.GONE
            binding.lyPf.visibility = View.GONE
            binding.lyTo.visibility = View.GONE
        }

        // Falso puente como pieza aparte "tubo 2.5" solo cuando el puente es Múltiple;
        // con otro perfil ya se sumó a la cantidad del puente (nPuentes), sin línea aparte.
        if (nFalsoPuente > 0 && falsoPuenteEsTubo) {
            binding.txFalsoPuente.text = "${NovaCalculos.df1(ancho)} = $nFalsoPuente"
            binding.lyFalsoPuente.visibility = View.VISIBLE
        } else {
            binding.lyFalsoPuente.visibility = View.GONE
        }
      }

    // ==================== VIDRIOS ====================
    @SuppressLint("SetTextI18n")
    private fun vidriosTexto() {
        val alto = alto() - descuentoAltoVertical()
        val hoja = altoHoja()
        val us = binding.etU.text.toString().toFloat()
        val divisManual = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
        val cruce = cruce()

        // Arco de la curva (ver arcoCurvo): cuerda + flecha si se indican, si no el ancho.
        val ancho = arcoCurvo(binding.etAncho.text.toString().toFloat()) - descuentoAnchoLateralApa()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)

        if (tipoNova == TipoNova.APA) {
            // === APARENTE: lógica original ===
          val nFijos = when {
              textoModelo == "nff" -> divisiones
              textoModelo == "nfc" -> 0   // full corredizas: sin fijos
              textoModelo == "ncfc" -> NovaCalculos.nFijos(divisiones, "ncfc")
              else -> NovaCalculos.nFijos(ancho, divisiones)
          }
          // ncfc: la cantidad de vidrios de corrediza usa el conteo del patrón invertido.
          val nCorredizas = when {
              textoModelo == "nff" -> 0
              textoModelo == "nfc" -> divisiones   // full corredizas: todas son corredizas
              textoModelo == "ncfc" -> NovaCalculos.nCorredizas(divisiones, "ncfc")
              else -> NovaCalculos.nCorredizas(ancho, divisiones)
          }
            val cruceModelo = if (textoModelo == "nff") 0f else cruce
            val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruceModelo, "apa", valorParanteUFijosApa())

            // Usar textoModelo (modulación) y no texto (geometría): en L/C/serie texto es "nl"/etc
            // y el vidrio de fijos necesita la modulación para aplicar la lógica de full fijos.
            // Full corredizas: sin vidrio de fijo.
            val vidriosFijos = if (textoModelo == "nfc") "" else
                NovaVidriosHelper.calcularVidriosFijos(ancho, uFijos, altoHoja, us, nFijos, divisiones, "aparente", textoModelo)
            // Full corredizas: cada corrediza tiene ancho = uFijos (= H); cantidad = nº de corredizas.
            val vidrioCorre = if (textoModelo == "nfc")
                "${NovaCalculos.df1(uFijos)} x ${NovaCalculos.df1(altoHoja - 3.5f)} = $nCorredizas"
            else NovaVidriosHelper.calcularVidriosCorredizos(uFijos, altoHoja, nCorredizas)
              val vidrioMocheta = NovaVidriosHelper.calcularVidrioMochetaAparente(
                  ancho = ancho,
                  alto = alto,
                  altoHoja = altoHoja,
                  divisiones = divisiones,
                  tubo = tubo,
                  puente = puente,
                  us = us,
                  // modelo = modulación (patrón); el remate (np/nr) va aparte para combinarlos.
                  modelo = textoModelo,
                  mochetaInferior = mochetaInferiorDoblePuente(),
                  remate = modeloRemate
              )

            // Full fijos: sin corredizas no se muestra el vidrio de corrediza (el del -1.4).
            val mostrarVidrioCorre = textoModelo != "nff" && divisiones > 1
            val partesVidrio = mutableListOf(vidriosFijos)
            if (mostrarVidrioCorre) partesVidrio.add(vidrioCorre)
            if (hoja < alto) partesVidrio.add(vidrioMocheta)
            binding.txV.text = partesVidrio.filter { it.isNotBlank() }.joinToString("\n")
        } else {
            // === INAPARENTE/PIVOTANTE: lógica de vidrio3/NovaIna ===
            val cruceExacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
            if (textoModelo == "nfc") {
                // Full corredizas INA: cada corrediza = uFijos (= H) de ancho, cantidad = nº de
                // corredizas (igual que en APA), más la mocheta.
                val cruceIna = NovaInaCalculos.cruce(cruceExacto, divisiones)
                val uFijosIna = NovaInaCalculos.uFijos(ancho, divisiones, cruceIna)
                val corr = "${NovaCalculos.df1(uFijosIna)} x ${NovaCalculos.df1(altoHoja - 3.5f)} = $divisiones"
                val mocheta = NovaInaCalculos.vidrioMocheta(
                    ancho = ancho, alto = alto, hoja = hoja, altoHoja = altoHoja,
                    divisiones = divisiones, cruce = cruceIna, modelo = "nfc",
                    alturaPuente = tubo, mochetaInferior = mochetaInferiorDoblePuente(),
                    mochetaInvertidaIna = esModeloInvertidoIna(), remate = modeloRemate
                )
                binding.txV.text = listOf(corr, mocheta).filter { it.isNotBlank() }.joinToString("\n")
            } else {
              binding.txV.text = NovaInaCalculos.calcularVidrios(
                  ancho = ancho,
                  alto = alto,
                  hoja = hoja,
                  us = us,
                  divisiones = divisiones,
                  cruceExacto = cruceExacto,
                  // modelo = modulación (patrón); el remate (np/nr) va aparte para combinarlos.
                  modelo = textoModelo,
                  alturaPuente = tubo,
                  mochetaInferior = mochetaInferiorDoblePuente(),
                  mochetaInvertidaIna = esModeloInvertidoIna(),
                  remate = modeloRemate
              )
            }
          }
      }

    // ==================== REFERENCIAS ====================
    /**
     * [anchoRealMostrar] = medida que se midió, SOLO para la línea "An:" de la referencia. En L y
     * en C los campos ya traen el ancho útil (con el descuento de esquina de `medidasCalculoNlApa`,
     * que es el que se corta y se dibuja) y la medida original se pasa por aquí. Sin este
     * parámetro habría que volver a llamar a `referencias()` con la medida original, y entonces
     * las divisiones, los fijos y las corredizas saldrían del ancho sin descontar: un lado de 122
     * cuenta 3 divisiones sin descuento y 2 con él, que es lo que dibuja y corta el resto.
     */
    private fun referencias(anchoRealMostrar: Float? = null) {
        val anchoEntrada = binding.etAncho.text.toString().toFloat()
        val altoReal = binding.etAlto.text.toString().toFloat()
        val alto = altoReal - descuentoAltoVertical()
        val hoja = binding.etHoja.text.toString().toFloat()
        val divisManual = binding.etPartes.text.toString().toInt()

        // Arco de la curva (ver arcoCurvo): cuerda + flecha si se indican, si no el ancho.
        // anchoReal = medida sin descuento de encuentro (lo que se muestra/archiva en referencia);
        // ancho = útil (con descuento) para los cálculos de corte.
        val anchoCampo = arcoCurvo(anchoEntrada)
        val anchoReal = anchoRealMostrar ?: anchoCampo
        val ancho = anchoCampo - descuentoAnchoLateralApa()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val siNoMoch = NovaCalculos.siNoMoch(alto, hoja)
        val nFijos = NovaCalculos.nFijos(ancho, divisiones)
        val nCorredizas = NovaCalculos.nCorredizas(ancho, divisiones)
        val tipoCalculo = tipoCalculoPrincipal()
        val puntosU = if (divisiones > 4) {
            NovaUIHelper.generarPuntosU(ancho, divisManual, cruce(), tipoCalculo)
        } else {
            ""
        }

        val referenciasBase = NovaUIHelper.generarReferencias(
            ancho, alto, altoHoja, divisiones, nFijos, nCorredizas, siNoMoch, puntosU,
            modelo = texto,
            alturaPuente = tubo,
            mochetaInferior = mochetaInferiorDoblePuente(),
            remate = modeloRemate,
            descontarPuentes = tipoNova == TipoNova.APA,
            anchoReal = anchoReal,
            altoReal = altoReal
        )
        val geometria = geometriaCurva(ancho, divisiones)
        binding.txReferencias.text = if (geometria != null) {
            "$referenciasBase\n${resumenGeometriaCurva(geometria)}"
        } else {
            referenciasBase
        }
    }

    // ============ VISTA COMPACTABLE DE REFERENCIAS (en L / en C / serie) ============
    // En geometrías compuestas la información ocupa mucho espacio y tapa los cálculos.
    // Se muestra solo "An x Al" de cada ventana (color distinto por ventana) y, al tocar,
    // se despliega toda la información; al volver a tocar, se contrae.

    /**
     * Toma el texto completo que ya está en txReferencias y, según la geometría, lo deja
     * plano (plano/curvo) o lo convierte en una vista compactable con color por ventana
     * (en L / en C / serie). Llamar al final del cálculo, una sola vez.
     */
    private fun aplicarVistaReferencias() {
        val full = binding.txReferencias.text?.toString().orEmpty()
        referenciasFull = full
        binding.txReferencias.tag = full
        val compuesta = texto == "nl" || texto == "nu" || texto == "ns"
        if (!compuesta) {
            referenciasColapsable = false
            binding.txReferencias.setOnClickListener(null)
            binding.txReferencias.isClickable = false
            return
        }
        referenciasColapsable = true
        referenciasExpandida = false
        binding.txReferencias.isClickable = true
        binding.txReferencias.setOnClickListener {
            referenciasExpandida = !referenciasExpandida
            renderReferencias()
        }
        renderReferencias()
    }

    private fun renderReferencias() {
        binding.txReferencias.text = if (referenciasColapsable && !referenciasExpandida) {
            construirReferenciasColapsadas(referenciasFull)
        } else if (referenciasColapsable) {
            colorearMedidasReferencias(referenciasFull, conPie = true)
        } else {
            referenciasFull
        }
    }

    /** Color por ventana: mismo tono, distinta oscuridad (más claro a más oscuro). */
    private fun colorVentana(indice: Int, total: Int): Int {
        val h = 210f
        val s = 0.85f
        val v = if (total <= 1) 0.55f else (0.78f - (indice.toFloat() / (total - 1)) * 0.40f)
        return Color.HSVToColor(floatArrayOf(h, s, v))
    }

    private fun esLineaMedida(linea: String) = linea.trimStart().startsWith("An:")

    /** Vista contraída: solo las medidas (An x Al) de cada ventana, con color por ventana. */
    private fun construirReferenciasColapsadas(full: String): SpannableStringBuilder {
        val medidas = full.split("\n").filter { esLineaMedida(it) }
        val sb = SpannableStringBuilder()
        medidas.forEachIndexed { i, m ->
            val ini = sb.length
            sb.append(m.trim())
            sb.setSpan(ForegroundColorSpan(colorVentana(i, medidas.size)), ini, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            sb.setSpan(StyleSpan(Typeface.BOLD), ini, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            sb.append("\n")
        }
        val ini = sb.length
        sb.append("▼ toca para ver el detalle")
        sb.setSpan(ForegroundColorSpan(Color.GRAY), ini, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        sb.setSpan(RelativeSizeSpan(0.85f), ini, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    /** Vista expandida: texto completo con las líneas de medida coloreadas por ventana. */
    private fun colorearMedidasReferencias(full: String, conPie: Boolean): SpannableStringBuilder {
        val lineas = full.split("\n")
        val total = lineas.count { esLineaMedida(it) }
        val sb = SpannableStringBuilder()
        var idx = 0
        lineas.forEachIndexed { i, l ->
            val ini = sb.length
            sb.append(l)
            if (esLineaMedida(l)) {
                sb.setSpan(ForegroundColorSpan(colorVentana(idx, total)), ini, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                sb.setSpan(StyleSpan(Typeface.BOLD), ini, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                idx++
            }
            if (i < lineas.size - 1) sb.append("\n")
        }
        if (conPie) {
            val ini = sb.length
            sb.append("\n▲ toca para contraer")
            sb.setSpan(ForegroundColorSpan(Color.GRAY), ini, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            sb.setSpan(RelativeSizeSpan(0.85f), ini, sb.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        return sb
    }

    @SuppressLint("SetTextI18n")
    private fun cargarDesdePaqueteDiseno(paquete: String?) {
        cargandoDiseno = true
        try {
            val paqueteSeguro = paquete?.trim()
                ?.takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }
                ?: return
            val t = paqueteSeguro.replace(" ", "")
            val contenido = t.trim().removePrefix("{").removeSuffix("}")
            val cabParts = contenido.split(",")
            if (cabParts.size < 3) return

            // Tipo de ventana
            val tipoTxt = cabParts[1].lowercase()
            tipoNova = when (tipoTxt) {
                "apa" -> TipoNova.APA
                "piv" -> TipoNova.PIV
                else -> TipoNova.INA
            }
            actualizarModo()

            // Dimensiones
            val resto = contenido.substringAfter("$tipoTxt,").trim()
            if (!resto.startsWith("[")) return
            val sinBracketInicial = resto.removePrefix("[")
            val idxCierre = sinBracketInicial.lastIndexOf(']')
            if (idxCierre < 0) return
            val dentro = sinBracketInicial.substring(0, idxCierre)
            val idxColon = dentro.indexOf(":")
            if (idxColon < 0) return
            val dimsTxt = dentro.substring(0, idxColon)
            val dims = dimsTxt.split(",")
            val ancho = dims.getOrNull(0)?.replace(",", ".")?.toFloatOrNull() ?: return
            val alto = dims.getOrNull(1)?.replace(",", ".")?.toFloatOrNull() ?: return

            // En una ventana de esquina las medidas de los lados ya están puestas y no se tocan:
            // ver esDisenoDeEsquina.
            if (!esDisenoDeEsquina(paqueteSeguro)) {
                binding.etAncho.setText(df1(ancho))
                binding.etAlto.setText(df1(alto))
            }

            // Buscar franja sistema para extraer altoHoja y divisiones
            val cuerpoRaw = dentro.substring(idxColon + 1)
            // Limpiar estado desigual previo
            modulosDesiguales = emptyList()
            parantesDesiguales = emptyList()
            mochetaDesigual = emptyList()
            altoHojaDesigual = 0f

            // ---- Parseo multi-tramo ----
            // Extraer TODOS los bloques de tramo Tl<W>(...) como pares (anchoTramo, interior).
            // Si el cuerpo no está en forma de bloques T, se trata como un único tramo.
            val bloquesTramo: List<Pair<Float, String>> = run {
                val s = cuerpoRaw.replace(" ", "")
                if (!s.lowercase().startsWith("t")) return@run listOf(ancho to s)
                val out = mutableListOf<Pair<Float, String>>()
                var i = 0
                while (i < s.length) {
                    if (s[i].lowercaseChar() == 't') {
                        var j = i + 1
                        if (j < s.length && s[j].lowercaseChar() in 'a'..'z' && s[j] != '<') j++
                        var wTramo = ancho
                        if (j < s.length && s[j] == '<') {
                            val gt = s.indexOf('>', j)
                            if (gt < 0) break
                            wTramo = s.substring(j + 1, gt).replace(",", ".").toFloatOrNull() ?: ancho
                            j = gt + 1
                        }
                        if (j < s.length && s[j] == '(') {
                            var depth = 0; var close = j
                            for (p in j until s.length) {
                                when (s[p]) {
                                    '(' -> depth++
                                    ')' -> { depth--; if (depth == 0) { close = p; break } }
                                }
                            }
                            out.add(wTramo to s.substring(j + 1, close))
                            i = close + 1
                            continue
                        }
                    }
                    i++
                }
                if (out.isEmpty()) listOf(ancho to s) else out
            }

            val regexMod = Regex("([fc])(?:<([^>]+)>)?")
            val modsAll = mutableListOf<ModuloDesigual>()
            val parantesAll = mutableListOf<Int>()
            val mochAll = mutableListOf<ModuloDesigual>()
            // Alto de la franja de sistema de cada tramo, y si ese tramo lleva corrediza.
            val altosSistema = mutableListOf<Pair<Float, Boolean>>()

            for ((bi, bloque) in bloquesTramo.withIndex()) {
                val wTramo = bloque.first
                val franjasBloque = splitRespetandoParentesis(bloque.second)
                val sFranja = franjasBloque.firstOrNull { it.trim().lowercase().startsWith("s") }
                val mFranja = franjasBloque.firstOrNull { it.trim().lowercase().startsWith("m") }

                // El alto de hoja del bloque: el de SU franja de sistema. Se anotan todos y al
                // final manda el del tramo que lleva corrediza; ver [elegirAltoHoja].
                if (sFranja != null) {
                    val low = sFranja.trim().lowercase()
                    if (low.length > 1 && low[1] == '<') {
                        val ah = low.substringAfter("<").substringBefore(">").replace(",", ".").toFloatOrNull() ?: 0f
                        if (ah > 0f) altosSistema.add(ah to low.substringAfter("(").contains('c'))
                    }
                }

                // Módulos del sistema (grupos ;P; internos = parantes dentro del bloque)
                if (sFranja != null) {
                    val interior = sFranja.trim().lowercase().substringAfter("(").substringBeforeLast(")")
                    val nMods = interior.count { it == 'f' || it == 'c' }.coerceAtLeast(1)
                    val grupos = interior.split(";p;")
                    val inicioBloque = modsAll.size
                    for ((gi, grupo) in grupos.withIndex()) {
                        for (m in regexMod.findAll(grupo)) {
                            val tipo = m.groupValues[1][0]
                            val w = m.groupValues[2].replace(",", ".").toFloatOrNull() ?: (wTramo / nMods)
                            modsAll.add(ModuloDesigual(tipo, w))
                        }
                        // Parante tras cada grupo, salvo el último grupo del último bloque.
                        val esUltimoGrupoGlobal = (bi == bloquesTramo.lastIndex && gi == grupos.lastIndex)
                        if (!esUltimoGrupoGlobal) parantesAll.add(modsAll.size)
                    }
                    // El ancho del TRAMO es lo autoritativo: re-derivar el ancho de cada módulo
                    // desde wTramo conservando su proporción, a precisión completa. Así módulos
                    // iguales dan exactamente wTramo/divisiones (U uniforme) y la suma cierra
                    // exacta sin arrastrar redondeos del df1 del paquete.
                    reDerivarDesdeTramo(modsAll, inicioBloque, modsAll.size, wTramo)
                }

                // Mocheta del bloque
                if (mFranja != null) {
                    val interior = mFranja.trim().lowercase().substringAfter("(").substringBeforeLast(")")
                    val nMoch = interior.count { it == 'f' || it == 'c' }.coerceAtLeast(1)
                    val inicioMoch = mochAll.size
                    for (m in regexMod.findAll(interior)) {
                        val tipo = m.groupValues[1][0]
                        val w = m.groupValues[2].replace(",", ".").toFloatOrNull() ?: (wTramo / nMoch)
                        mochAll.add(ModuloDesigual(tipo, w))
                    }
                    reDerivarDesdeTramo(mochAll, inicioMoch, mochAll.size, wTramo)
                }
            }

            elegirAltoHoja(altosSistema)?.let {
                binding.etHoja.setText(df1(it))
                altoHojaDesigual = it
            }

            val divs = modsAll.size
            if (divs > 0) binding.etPartes.setText(divs.toString())

            // Todo lo que llega aquí proviene de diseñoNova: se trata SIEMPRE con el motor
            // desigual usando los anchos exactos del diseño (iguales o no). Sin umbral de 0.5.
            if (modsAll.isNotEmpty()) {
                modulosDesiguales = modsAll
                parantesDesiguales = parantesAll
                mochetaDesigual = mochAll
            }

            // Guardar paquete y actualizar diseño
            ultimoPaquete = paqueteSeguro
            binding.textView28.setText(R.string.referencias_y_c_lculos)
            val tipoMsg = if (modulosDesiguales.isNotEmpty()) " (desigual)" else ""
            Toast.makeText(this, "Diseño cargado: ${df1(ancho)} x ${df1(alto)}$tipoMsg", Toast.LENGTH_SHORT).show()

            // Relanzar render headless para actualizar la imagen
            val intentRender = Intent(this, DisenoNovaActivity::class.java).apply {
                putExtra(DisenoNovaActivity.EXTRA_PAQUETE, ultimoPaquete)
                putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
                putExtra(DisenoNovaActivity.EXTRA_OUTPUT_FORMAT, if (texto == "nl" || texto == "nu" || texto == "ns" || texto == "ncu" || texto == "nci" || ultimoPaquete.contains("P<2.5>") || modulosDesiguales.isNotEmpty()) "png" else "svg")
                putExtra(DisenoNovaActivity.EXTRA_RET_PADDING_PX, 4)
                putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, mochetaLateralDisenoNl())
                putExtra(DisenoNovaActivity.EXTRA_ENCUENTRO_VACIO, metaEncuentro)
                putExtra(DisenoNovaActivity.EXTRA_DIRECCION, metaDireccion)
                putExtra(DisenoNovaActivity.EXTRA_US_CM, binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f)
            }
            lanzarDiseno.launch(intentRender)

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar diseño: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            cargandoDiseno = false
        }
    }

    /**
     * El alto de hoja de la ventana entre los de todos sus tramos.
     *
     * La hoja es una sola y la manda el tramo donde va la corrediza. Tomar el del PRIMER tramo
     * dejaba la ventana con el alto de un tramo sin hoja —en un triángulo, el de la punta, que
     * puede medir un palmo— y con eso el cálculo salía disparatado.
     */
    private fun elegirAltoHoja(altos: List<Pair<Float, Boolean>>): Float? {
        if (altos.isEmpty()) return null
        val conCorrediza = altos.filter { it.second }.maxOfOrNull { it.first }
        return conCorrediza ?: altos.maxOf { it.first }
    }

    /**
     * Suelta el estado de divisiones desiguales (cargado de diseñoNova) cuando el usuario
     * edita manualmente los campos base. Así el siguiente Calcular vuelve a la lógica normal
     * con los datos nuevos, sin necesidad de salir y reentrar a NovaCorrediza.
     */
    private fun limpiarEstadoDesigual() {
        if (cargandoDiseno || modulosDesiguales.isEmpty()) return
        modulosDesiguales = emptyList()
        parantesDesiguales = emptyList()
        mochetaDesigual = emptyList()
        altoHojaDesigual = 0f
    }

    /** Al editar manualmente cualquier campo base se suelta el estado desigual. */
    private fun configurarLimpiezaDesigual() {
        val watcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                limpiarEstadoDesigual()
                // El reparto elegido a mano era para esa medida: con otra vuelve el automático.
                NovaCalculos.repartoManual = null
            }
        }
        binding.etPartes.addTextChangedListener(watcher)
        binding.etHoja.addTextChangedListener(watcher)
        binding.etAncho.addTextChangedListener(watcher)
        binding.etAlto.addTextChangedListener(watcher)
        binding.etCruce.addTextChangedListener(watcher)
        binding.etU.addTextChangedListener(watcher)
    }

    /**
     * Re-deriva el ancho de los módulos [start, end) desde el ancho del tramo [wTramo],
     * conservando su proporción y a precisión completa. El tramo es lo autoritativo: módulos
     * iguales quedan exactamente wTramo/n y su suma cierra exacta (sin arrastrar el df1 del
     * paquete, que es lo que partía la U en 46.2 / 46.3).
     */
    private fun reDerivarDesdeTramo(mods: MutableList<ModuloDesigual>, start: Int, end: Int, wTramo: Float) {
        if (start >= end || wTramo <= 0f) return
        val suma = (start until end).sumOf { mods[it].ancho.toDouble() }.toFloat()
        if (suma <= 0.05f) {
            val n = end - start
            for (i in start until end) mods[i] = mods[i].copy(ancho = wTramo / n)
        } else {
            for (i in start until end) mods[i] = mods[i].copy(ancho = wTramo * mods[i].ancho / suma)
        }
    }

    private fun splitRespetandoParentesis(texto: String): List<String> {
        val result = mutableListOf<String>()
        var depth = 0
        val current = StringBuilder()
        for (ch in texto) {
            when {
                ch == '(' -> { depth++; current.append(ch) }
                ch == ')' -> { depth--; current.append(ch) }
                ch == ';' && depth == 0 -> {
                    if (current.isNotEmpty()) result.add(current.toString())
                    current.clear()
                }
                else -> current.append(ch)
            }
        }
        if (current.isNotEmpty()) result.add(current.toString())
        return result
    }
    /** El paquete del diseño tal como sale para el editor, para probar la cadena entera. */
    @androidx.annotation.VisibleForTesting
    fun disenoSimbolicoParaPruebas(): String = disenoSimbolico()

    private fun disenoSimbolico(): String {
        val ancho = binding.etAncho.text.toString().toFloat()
        val alto = binding.etAlto.text.toString().toFloat()
        val hoja = binding.etHoja.text.toString().toFloat()
        val divisManual = binding.etPartes.text.toString().toInt()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val mochetaInferior = mochetaInferiorDoblePuente()

        if (texto == "nl") {
            val primera = obtenerPrimeraMedidaNlEditable()
            if (primera != null) {
                // Aplicar el descuento de esquina (mismo que materiales) para que el dibujo use
                // el ancho ÚTIL: divisiones y paños de mocheta coinciden con la lista de corte.
                val aletaMedida = MedidaNl(ancho, alto, hoja, divisManual)
                val (primeraC, aletaC) = medidasCalculoNlApa(primera, aletaMedida)
                // Cuántas hojas: regla de los 60 sobre la medida MEDIDA (ver
                // conDivisionesDeLaMedidaReal). El ancho útil solo reparte las medidas.
                val divisA = NovaCalculos.divisiones(primera.ancho, primera.divisManual, "nn")
                val divisB = NovaCalculos.divisiones(aletaMedida.ancho, aletaMedida.divisManual, "nn")
                val altoHojaA = NovaCalculos.altoHoja(primeraC.alto, primeraC.hoja)
                val altoHojaB = NovaCalculos.altoHoja(aletaC.alto, aletaC.hoja)
                val tramosBase = NovaUIHelper.generarTramosConsolidado(primeraC.ancho, primeraC.alto, altoHojaA, divisA, NovaCalculos.siNoMoch(primeraC.alto, primeraC.hoja), textoModelo, mochetaInferior, modeloRemate)
                val tramosAleta = NovaUIHelper.generarTramosConsolidado(aletaC.ancho, aletaC.alto, altoHojaB, divisB, NovaCalculos.siNoMoch(aletaC.alto, aletaC.hoja), textoModelo, mochetaInferior, modeloRemate)
                val cabecera = "${NovaCalculos.df1(primeraC.ancho)},${NovaCalculos.df1(primeraC.alto)}:"
                val esquina = esquinaEnElDiseno(0, primeraC.alto, primeraC.hoja)
                val base = conPanzaDelLado(0, tramosBase)
                val aleta = conPanzaDelLado(1, tramosAleta)
                return conLoQueTrajoLaMedida("{nova,${tipoPaquete()},[$cabecera$base $esquina $aleta]}")
            }
        }
        if (texto == "nu") {
            val ladoIzq = obtenerPrimeraMedidaNuEditable()
            val ladoCentro = obtenerSegundaMedidaNuEditable()
            if (ladoIzq != null && ladoCentro != null) {
                val ladoDer = MedidaNl(ancho, alto, hoja, divisManual)
                // Descuento de esquina (igual que materiales): dos esquinas, el centro descuenta
                // ambas. El dibujo usa el ancho ÚTIL para que coincida con la lista de corte.
                val (izqC, centroEsqIzq) = medidasCalculoNlApa(ladoIzq, ladoCentro)
                val (centroEsqDer, derC) = medidasCalculoNlApa(ladoCentro, ladoDer)
                val descuentoCentro = (ladoCentro.ancho - centroEsqIzq.ancho) +
                    (ladoCentro.ancho - centroEsqDer.ancho)
                val centroC = ladoCentro.copy(
                    ancho = (ladoCentro.ancho - descuentoCentro).coerceAtLeast(0f)
                )
                // Cuántas hojas: regla de los 60 sobre la medida MEDIDA (ver
                // conDivisionesDeLaMedidaReal). El ancho útil solo reparte las medidas.
                val divisIzq = NovaCalculos.divisiones(ladoIzq.ancho, ladoIzq.divisManual, "nn")
                val divisCentro = NovaCalculos.divisiones(ladoCentro.ancho, ladoCentro.divisManual, "nn")
                val divisDer = NovaCalculos.divisiones(ladoDer.ancho, ladoDer.divisManual, "nn")
                val altoHojaIzq = NovaCalculos.altoHoja(izqC.alto, izqC.hoja)
                val altoHojaCentro = NovaCalculos.altoHoja(centroC.alto, centroC.hoja)
                val altoHojaDer = NovaCalculos.altoHoja(derC.alto, derC.hoja)
                val tramosIzq = NovaUIHelper.generarTramosConsolidado(izqC.ancho, izqC.alto, altoHojaIzq, divisIzq, NovaCalculos.siNoMoch(izqC.alto, izqC.hoja), textoModelo, mochetaInferior, modeloRemate)
                val tramosCentro = NovaUIHelper.generarTramosConsolidado(centroC.ancho, centroC.alto, altoHojaCentro, divisCentro, NovaCalculos.siNoMoch(centroC.alto, centroC.hoja), textoModelo, mochetaInferior, modeloRemate)
                val tramosDer = NovaUIHelper.generarTramosConsolidado(derC.ancho, derC.alto, altoHojaDer, divisDer, NovaCalculos.siNoMoch(derC.alto, derC.hoja), textoModelo, mochetaInferior, modeloRemate)
                val cabecera = "${NovaCalculos.df1(centroC.ancho)},${NovaCalculos.df1(centroC.alto)}:"
                val esq1 = esquinaEnElDiseno(0, izqC.alto, izqC.hoja)
                val izqP = conPanzaDelLado(0, tramosIzq)
                val centroP = conPanzaDelLado(1, tramosCentro)
                val derP = conPanzaDelLado(2, tramosDer)
                val esq2 = esquinaEnElDiseno(1, centroC.alto, centroC.hoja)
                return conLoQueTrajoLaMedida("{nova,${tipoPaquete()},[$cabecera$izqP $esq1 $centroP $esq2 $derP]}")
            }
        }
        if (texto == "ns") {
            val lados = mutableListOf<MedidaNl>()
            lados.addAll(ladosNs)
            // En modo navegar, ladosNs ya contiene toda la serie (los campos se guardaron
            // en el lado actual antes de construir); no anexar la medida en curso.
            if (!navegandoNs && ancho > 0f && alto > 0f) {
                val actual = MedidaNl(ancho = ancho, alto = alto, hoja = hoja, divisManual = divisManual)
                val ultimo = lados.lastOrNull()
                if (ultimo == null || !mismaMedida(ultimo, actual)) {
                    lados.add(actual)
                }
            }
            if (lados.isNotEmpty()) {
                val base = lados.first()
                val divisBase = NovaCalculos.divisiones(base.ancho, base.divisManual, "nn")
                val altoHojaBase = NovaCalculos.altoHoja(base.alto, base.hoja)
                val tramosBase = NovaUIHelper.generarTramosConsolidado(base.ancho, base.alto, altoHojaBase, divisBase, NovaCalculos.siNoMoch(base.alto, base.hoja), textoModelo, mochetaInferior, modeloRemate)
                val partes = mutableListOf(conPanzaDelLado(0, tramosBase))
                for (i in 1 until lados.size) {
                    val lado = lados[i]
                    val divisLado = NovaCalculos.divisiones(lado.ancho, lado.divisManual, "nn")
                    val altoHojaLado = NovaCalculos.altoHoja(lado.alto, lado.hoja)
                    val tramosLado = NovaUIHelper.generarTramosConsolidado(lado.ancho, lado.alto, altoHojaLado, divisLado, NovaCalculos.siNoMoch(lado.alto, lado.hoja), textoModelo, mochetaInferior, modeloRemate)
                    partes.add(esquinaEnElDiseno(i - 1, lados[i - 1].alto, lados[i - 1].hoja))
                    partes.add(conPanzaDelLado(i, tramosLado))
                }
                val cabecera = "${NovaCalculos.df1(base.ancho)},${NovaCalculos.df1(base.alto)}:"
                return conLoQueTrajoLaMedida("{nova,${tipoPaquete()},[${cabecera}A<90> ${partes.joinToString(" ")}]}")
            }
        }
        if (texto == "ncu") {
            // Arco de la curva (ver arcoCurvo). El tag U<...> aplica la flecha al dibujo.
            val arco = arcoCurvo(ancho)
            val divisionesCurvas = NovaCalculos.divisiones(arco, divisManual)
            val disenoBase = NovaUIHelper.generarDisenoConsolidado(
                ancho = arco,
                alto = alto,
                altoHoja = altoHoja,
                divisiones = divisionesCurvas,
                siNoMoch = NovaCalculos.siNoMoch(alto, hoja),
                texto = textoModelo,
                mochetaInferior = mochetaInferior,
                remate = modeloRemate
            )
            val disenoCurvo = insertarTagSimpleEnPrimerSistema(disenoBase, "U<${df1(flechaCurvaDesdeCampos())}>")
            return "{nova,${tipoPaquete()},[$disenoCurvo]}"
        }
        if (texto == "nci") {
            val disenoBase = NovaUIHelper.generarDisenoConsolidado(
                ancho = ancho,
                alto = alto,
                altoHoja = altoHoja,
                divisiones = divisiones,
                siNoMoch = NovaCalculos.siNoMoch(alto, hoja),
                texto = "np",
                mochetaInferior = mochetaInferior
            )
            val disenoCircular = insertarTagSimpleEnPrimerSistema(disenoBase, "O<1>")
            return "{nova,${tipoPaquete()},[$disenoCircular]}"
        }

        // Patrón = textoModelo (modulación); remate = modeloRemate. Ejes independientes.
        return NovaUIHelper.generarPaqueteSimbolico(tipoPaquete(), ancho, alto, altoHoja, divisiones, textoModelo, mochetaInferior, modeloRemate)
    }

    private fun disenoSimbolicoV2(numeroProductoAuto: Int? = null): String {
        val clienteTxt = escaparCampoV2(binding.txC.text?.toString()?.trim().orEmpty().ifBlank { "sin cliente" })
        // Un ancho y un alto por LADO: la cantidad de anchos es la que dice si la geometría se
        // resuelve en L (2), en C (3) o en serie (más). Ver medidasDeLaGeometria.
        val lados = medidasDeLaGeometria()
        val anchosTxt = lados.joinToString(",") { dfV2(it.ancho) }
        val altosTxt = lados.joinToString(",") { dfV2(it.alto) }
        val hpTxt = dfV2(binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f)
        val cantidad = obtenerCantidadPreferida()
        val numeroProducto = numeroProductoPreferido(numeroProductoAuto)
        val acabado = when (tipoNova) {
            TipoNova.APA -> "a"
            TipoNova.INA -> "i"
            TipoNova.PIV -> "p"
        }
        val volumen = when (texto) {
            "nl", "nu", "ns" -> "e"
            "ncu" -> "c"
            else -> "p"
        }
        val forma = when (metaForma) {
            "circular"  -> "c"
            "poligonal" -> "p"
            else        -> if (texto == "nci") "c" else "r"
        }
        // Encuentro: si algún lado quedó al vacío se codifica (ARBL: 1=colinda, 0=vacío);
        // si los 4 colindan, se conserva el valor histórico (a/m).
        val encuentro = when {
            metaEncuentro != "1111" -> metaEncuentro
            texto == "nv" -> "a"
            else -> "m"
        }
        // El "modelo" del paquete refleja el REMATE (eje independiente de la modulación).
        val modelo = when (modeloRemate) {
            "nn" -> "n"
            "nr" -> "i"
            "np" -> "b"
            else -> "x"
        }
        val disenoTecnico = try { disenoSimbolico() } catch (_: Exception) { "" }
        val tramo = escaparCampoV2(tramosDelPaquete(disenoTecnico).ifBlank { "null" })
        val aluminio = escaparCampoV2(metaColorAluminio.ifBlank { "null" })
        val vidrios = escaparCampoV2(metaTipoVidrio.ifBlank { "null" })
        val accesorios = escaparCampoV2(textoMetadatosProduccionV2())
        val mecanismo = if (tipoNova == TipoNova.PIV) "p" else "c"
        return crystal.crystal.taller.PaqueteV2.construir(
            cliente = clienteTxt,
            producto = "V,n,$acabado,$mecanismo,$numeroProducto",
            geometria = "$volumen,$forma,$encuentro,$modelo",
            medidas = "$anchosTxt,$altosTxt,$hpTxt,null,null,$cantidad",
            diseno = tramo,
            sufijos = "-MAT<alu:$aluminio;vid:$vidrios>-ACC<$accesorios>"
        )
    }

    /**
     * Los lados de la ventana, en el mismo orden en que los dibuja el diseño. La CANTIDAD es
     * significativa: 1 lado = plano, 2 = en L, 3 = en C, más = serie. Quien lea la cadena decide
     * con eso, sin necesidad de otro campo.
     */
    private fun medidasDeLaGeometria(): List<MedidaNl> {
        val anchoCampo = arcoCurvo(binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f)
        val actual = MedidaNl(
            ancho = anchoCampo,
            alto = binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f,
            hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f,
            divisManual = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
        )
        return when (texto) {
            "nl" -> listOfNotNull(obtenerPrimeraMedidaNlEditable(), actual)
            "nu" -> listOfNotNull(
                obtenerPrimeraMedidaNuEditable(), obtenerSegundaMedidaNuEditable(), actual
            )
            "ns" -> {
                val lados = ladosNs.toMutableList()
                // En modo navegar, ladosNs ya trae la serie completa (mismo criterio que
                // disenoSimbolico): no anexar la medida en curso.
                if (!navegandoNs && actual.ancho > 0f && actual.alto > 0f) {
                    val ultimo = lados.lastOrNull()
                    if (ultimo == null || !mismaMedida(ultimo, actual)) lados.add(actual)
                }
                lados.ifEmpty { listOf(actual) }
            }
            else -> listOf(actual)
        }
    }

    /**
     * El paquete legado es `{nova,<acabado>,[<ancho>,<alto>:<tramos>]}`. En la cadena V2 el sistema
     * y el acabado ya van en `P<>` y las medidas en `M<>`, así que `T<>` lleva SOLO los tramos, sin
     * repetir la cabecera. El paquete completo se sigue usando tal cual donde hace falta: es lo que
     * viaja a DisenoNovaActivity para dibujar.
     */
    private fun tramosDelPaquete(paquete: String): String {
        val dentro = paquete.substringAfter("[", "").substringBeforeLast("]", "")
        if (dentro.isBlank()) return ""
        // La cabecera es "<ancho>,<alto>:" y es lo único que lleva dos puntos.
        return dentro.substringAfter(":", dentro).trim()
    }

    private fun textoMetadatosProduccionV2(): String {
        val pares = listOf(
            "acabado_sup" to metaAcabadoSuperficial.trim(),
            "obs" to metaObservaciones.trim()
        ).filter { it.second.isNotBlank() }
        if (pares.isEmpty()) return "null"
        return pares.joinToString(" | ") { "${it.first}:${it.second}" }
    }

    private fun obtenerCantidadPreferida(): Int = cantidadProducto

    private fun numeroProductoPreferido(numeroProductoAuto: Int? = null): Int {
        val clavesNumero = listOf("numero_producto", "numero", "n_producto", "item_numero", "id_producto")
        for (k in clavesNumero) {
            val valorInt = intent.getIntExtra(k, -1)
            if (valorInt > 0) return valorInt
            val valorStr = intent.getStringExtra(k)?.trim()
            val desdeStr = valorStr?.toIntOrNull()
            if (desdeStr != null && desdeStr > 0) return desdeStr
        }
        return (numeroProductoAuto ?: 1).coerceAtLeast(1)
    }

    private fun dfV2(v: Float): String {
        val redondeado = ((v * 10f).toInt()) / 10f
        return if (redondeado % 1f == 0f) redondeado.toInt().toString()
        else "%.1f".format(redondeado).replace(",", ".")
    }

    private fun escaparCampoV2(raw: String): String = crystal.crystal.taller.PaqueteV2.escapar(raw)

    /**
     * Deja el texto de [vista] copiable al portapapeles. Con pulsación larga siempre; con
     * [conToque] también con un toque simple, para los textos que no hacen otra cosa al tocarlos.
     */
    private fun hacerCopiable(vista: android.widget.TextView, etiqueta: String, conToque: Boolean = false) {
        fun copiar(): Boolean {
            val texto = vista.text?.toString().orEmpty()
            if (texto.isBlank()) {
                Toast.makeText(this, "No hay texto para copiar", Toast.LENGTH_SHORT).show()
                return true
            }
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText(etiqueta, texto))
            Toast.makeText(this, "Texto copiado", Toast.LENGTH_SHORT).show()
            return true
        }
        vista.setOnLongClickListener { copiar() }
        if (conToque) vista.setOnClickListener { copiar() }
    }

    private fun mismaMedida(a: MedidaNl, b: MedidaNl): Boolean {
        fun casiIgual(x: Float, y: Float): Boolean = kotlin.math.abs(x - y) < 0.001f
        return casiIgual(a.ancho, b.ancho) &&
            casiIgual(a.alto, b.alto) &&
            casiIgual(a.hoja, b.hoja) &&
            a.divisManual == b.divisManual
    }

    private fun limpiarEstadoNl() {
        esquinaDeLaMedida = null
        siluetasDeLaMedida = emptyList()
        parantesDeLaMedida = emptyList()
        materialesNlArchivables = null
        primeraMedidaNl = null
        primeraMedidaNu = null
        segundaMedidaNu = null
        ladosNs.clear()
        navegandoNs = false
        indiceNs = 0
        binding.btAgregar.text = getString(R.string.agregar)
        binding.etAncho2.setText("")
        binding.etAlto2.setText("")
        binding.etPuente2.setText("")
        binding.etDivi2.setText("0")
        binding.etAncho3.setText("")
        binding.etAlto3.setText("")
        binding.etPuente3.setText("")
        binding.etDivi3.setText("0")
        binding.lyAncho2.visibility = View.GONE
        binding.lyAlto2.visibility = View.GONE
        binding.lyPuente2.visibility = View.GONE
        binding.lyDivi2.visibility = View.GONE
        binding.tvLado1.visibility = View.GONE
        binding.lyAncho3.visibility = View.GONE
        binding.lyAlto3.visibility = View.GONE
        binding.lyPuente3.visibility = View.GONE
        binding.lyDivi3.visibility = View.GONE
        binding.lyFlecha.visibility = View.GONE
        binding.lyCuerda.visibility = View.GONE
        binding.txDatos.setText(R.string.otros_datos)
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarTxDatosLadoNl(lado: Int) {
        binding.txDatos.text = "Otros datos\nLado $lado"
    }

    private fun mochetaLateralDisenoNl(): Float {
        if (texto != "nl") return 0f
        if (obtenerPrimeraMedidaNlEditable() == null) return 0f
        return binding.etAncho.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0f) ?: 0f
    }

    private fun obtenerPrimeraMedidaNlEditable(): MedidaNl? {
        val base = primeraMedidaNl ?: return null
        val ancho = binding.etAncho2.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0f) ?: base.ancho
        val alto = binding.etAlto2.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0f) ?: base.alto
        val hoja = binding.etPuente2.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0f) ?: base.hoja
        val divis = binding.etDivi2.text?.toString()?.toIntOrNull()?.coerceAtLeast(0) ?: base.divisManual
        if (ancho <= 0f || alto <= 0f) return base
        return MedidaNl(ancho = ancho, alto = alto, hoja = hoja, divisManual = divis)
    }

    private fun obtenerPrimeraMedidaNuEditable(): MedidaNl? {
        val base = primeraMedidaNu ?: return null
        val ancho = binding.etAncho2.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0f) ?: base.ancho
        val alto = binding.etAlto2.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0f) ?: base.alto
        val hoja = binding.etPuente2.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0f) ?: base.hoja
        val divis = binding.etDivi2.text?.toString()?.toIntOrNull()?.coerceAtLeast(0) ?: base.divisManual
        if (ancho <= 0f || alto <= 0f) return base
        return MedidaNl(ancho = ancho, alto = alto, hoja = hoja, divisManual = divis)
    }

    private fun obtenerSegundaMedidaNuEditable(): MedidaNl? {
        val base = segundaMedidaNu ?: return null
        val ancho = binding.etAncho3.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0f) ?: base.ancho
        val alto = binding.etAlto3.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0f) ?: base.alto
        val hoja = binding.etPuente3.text?.toString()?.toFloatOrNull()?.coerceAtLeast(0f) ?: base.hoja
        val divis = binding.etDivi3.text?.toString()?.toIntOrNull()?.coerceAtLeast(0) ?: base.divisManual
        if (ancho <= 0f || alto <= 0f) return base
        return MedidaNl(ancho = ancho, alto = alto, hoja = hoja, divisManual = divis)
    }

    private fun construirTagAleta(
        lado: String,
        anchoAleta: Float,
        disenoAleta: String
    ): String = "${lado.uppercase()}<${NovaCalculos.df1(anchoAleta)}>{$disenoAleta}"

    private fun insertarAletaEnPrimerSistema(
        disenoBase: String,
        tagsAleta: List<String>
    ): String {
        if (tagsAleta.isEmpty()) return disenoBase
        val idx = disenoBase.indexOf(':')
        if (idx <= 0 || idx >= disenoBase.lastIndex) return disenoBase
        val cabecera = disenoBase.substring(0, idx)
        val cuerpo = disenoBase.substring(idx + 1)
        val patronSistema = Regex("s(?:<[^>]*>)?\\([^)]*\\)", RegexOption.IGNORE_CASE)
        val match = patronSistema.find(cuerpo) ?: return disenoBase
        val segmento = match.value
        val posCierre = segmento.lastIndexOf(')')
        if (posCierre <= 0) return disenoBase
        val tags = tagsAleta.joinToString("")
        val segmentoNuevo = segmento.substring(0, posCierre) + tags + segmento.substring(posCierre)
        val cuerpoNuevo = cuerpo.replaceRange(match.range, segmentoNuevo)
        return "$cabecera:$cuerpoNuevo"
    }

    /**
     * Mete un tag suelto (el arco, el círculo) dentro de la franja de sistema.
     *
     * Va sobre los TRAMOS pelados, sin cabecera: el paño de una pared curva es un tramo más del
     * diseño y no tiene cabecera propia, así que el camino que buscaba los dos puntos devolvía el
     * texto tal cual y la curva se dibujaba recta.
     */
    /**
     * Le pone al paquete de la esquina lo que el apunte midió de cada pared: la silueta de las que
     * no son rectángulo (`L<…>` en su tramo) y los parantes marcados (`;P;` entre hojas). Solo
     * cuando la medida trajo algo: el paquete de siempre no se toca.
     */
    private fun conLoQueTrajoLaMedida(paquete: String): String {
        if (siluetasDeLaMedida.all { it == null } && parantesDeLaMedida.all { it.isEmpty() }) return paquete
        val diseno = runCatching { crystal.crystal.Diseno.nova.DisenoNova.desdePaquete(paquete) }.getOrNull()
            ?: return paquete
        return diseno.conSiluetasYParantesPorLado(siluetasDeLaMedida, parantesDeLaMedida).aPaquete()
    }

    private fun conTagEnElSistema(tramos: String, tag: String): String {
        if (tag.isBlank()) return tramos
        val patronSistema = Regex("s(?:<[^>]*>)?\\([^)]*\\)", RegexOption.IGNORE_CASE)
        val match = patronSistema.find(tramos) ?: return tramos
        val segmento = match.value
        val posCierre = segmento.lastIndexOf(')')
        if (posCierre <= 0) return tramos
        val segmentoNuevo = segmento.substring(0, posCierre) + tag + segmento.substring(posCierre)
        return tramos.replaceRange(match.range, segmentoNuevo)
    }

    private fun insertarTagSimpleEnPrimerSistema(
        disenoBase: String,
        tag: String
    ): String {
        if (tag.isBlank()) return disenoBase
        val idx = disenoBase.indexOf(':')
        if (idx <= 0 || idx >= disenoBase.lastIndex) return disenoBase
        val cabecera = disenoBase.substring(0, idx)
        val cuerpo = disenoBase.substring(idx + 1)
        val cuerpoNuevo = conTagEnElSistema(cuerpo, tag)
        if (cuerpoNuevo == cuerpo) return disenoBase
        return "$cabecera:$cuerpoNuevo"
    }

    // ==================== SPINNER TUBO ====================
    private fun spinnerTubo() {
        val spinnerOptions = NovaSpinnerData.obtenerOpcionesTubo()
        val adapter = NovaSpinnerData.AdaptadorSpinner(this, spinnerOptions)
        spinnerListo = false
        binding.spinner.adapter = adapter
        binding.editTextNumberDecimal.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!actualizandoValorPuenteDesdeCodigo) {
                    valorPuenteEditadoManualmente = true
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!spinnerListo) {
                    spinnerListo = true
                    return
                }
                val tuboAnterior = tubo
                val puenteAnterior = puente
                val selectedOption = spinnerOptions[position]
                if (puenteMultipleNoPermitido(selectedOption.text)) {
                    aplicarPuentePredeterminadoSinMultiple()
                } else {
                    tubo = selectedOption.valor
                    puente = selectedOption.text
                    binding.tvP.text = puente
                    escribirValorPuente(tubo)
                }
                recalcularSiYaFueCalculado(tuboAnterior, puenteAnterior)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    /** Aplica un puente elegido desde el diálogo de opciones (misma lógica que el spinner). */
    private fun aplicarPuenteDesdeOpcion(op: NovaSpinnerData.SpinnerTubos) {
        if (puenteMultipleNoPermitido(op.text)) {
            aplicarPuentePredeterminadoSinMultiple()
        } else {
            tubo = op.valor
            puente = op.text
            binding.tvP.text = puente
            escribirValorPuente(tubo)
        }
        actualizarVisibilidadMochetaInferior()
    }

    // ==================== SPINNER HELPERS ====================
    private fun mostrarLySpinner() {
        escribirValorPuente(tubo)
        binding.lySpinner.alpha = 0f
        binding.lySpinner.visibility = View.VISIBLE
        binding.lySpinner.animate().alpha(1f).setDuration(300).start()
    }

    private fun aplicarValorManualPuente(): Boolean {
        val valorManual = binding.editTextNumberDecimal.text
            ?.toString()
            ?.trim()
            ?.replace(",", ".")
            ?.toFloatOrNull()

        if (valorManual == null || valorManual <= 0f) {
            Toast.makeText(this, "Ingrese valor válido", Toast.LENGTH_SHORT).show()
            return false
        }

        val valorAnterior = tubo
        val esIngresoManual = valorPuenteEditadoManualmente
        tubo = valorManual
        if (esIngresoManual) {
            puente = "tubo: ${df1(tubo)}"
        }
        escribirValorPuente(tubo)
        binding.tvP.text = puente
        if (esIngresoManual) {
            recalcularSiYaFueCalculado(valorAnterior, "")
        }
        return true
    }

    private fun recalcularSiYaFueCalculado(tuboAnterior: Float, puenteAnterior: String) {
        val cambioTubo = kotlin.math.abs(tuboAnterior - tubo) > 0.001f
        val cambioPuente = puenteAnterior.isNotEmpty() && puenteAnterior != puente
        if (ultimoPaquete.isNotBlank() && (cambioTubo || cambioPuente)) {
            binding.btCalcular.post { binding.btCalcular.performClick() }
        }
    }

    private fun escribirValorPuente(valor: Float) {
        actualizandoValorPuenteDesdeCodigo = true
        binding.editTextNumberDecimal.setText(df1(valor))
        binding.editTextNumberDecimal.setSelection(binding.editTextNumberDecimal.text?.length ?: 0)
        actualizandoValorPuenteDesdeCodigo = false
        valorPuenteEditadoManualmente = false
    }

    private fun ocultarLySpinner() {
        binding.lySpinner.animate().alpha(0f).setDuration(300)
            .withEndAction { binding.lySpinner.visibility = View.GONE }.start()
    }

    // ==================== ARCHIVAR ====================
    private fun obtenerPrefijo(): String = when (tipoNova) {
        TipoNova.APA -> "Vna"
        TipoNova.INA -> "Vni"
        TipoNova.PIV -> "Vnp"
    }

    private fun ejecutarArchivado() {
        // Si es modo masivo, solo devolver resultado (Taller se encarga de archivar)
        if (ModoMasivoHelper.esModoMasivo(this)) {
            devolverResultadoMasivo()
            return
        }

        // Modo manual: permitir metadatos de producción antes de archivar
        mostrarDialogoMetadatosProduccion {
            archivarMapas()
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            Toast.makeText(this, "Archivado", Toast.LENGTH_SHORT).show()
            limpiarMedidasTrasArchivar()
            // Si vino de MedidaActivity, ofrecer la siguiente medida de la cola.
            ofrecerSiguienteMedidaCola()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun limpiarMedidasTrasArchivar() {
        binding.etAncho.setText("")
        binding.etAlto.setText("")

        if (texto != "nl") return

        primeraMedidaNl = null
        materialesNlArchivables = null
        binding.etHoja.setText("")
        binding.etPartes.setText("0")
        binding.etAncho2.setText("")
        binding.etAlto2.setText("")
        binding.etPuente2.setText("")
        binding.etDivi2.setText("0")
        binding.lyAncho2.visibility = View.GONE
        binding.lyAlto2.visibility = View.GONE
        binding.lyPuente2.visibility = View.GONE
        binding.lyDivi2.visibility = View.GONE
        binding.tvLado1.visibility = View.GONE
        contadorLado = 1
        maxLados = 2
        binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
        binding.txDatos.setText(R.string.otros_datos)
        binding.btAgregar.visibility = View.VISIBLE
        binding.btAgregar.isEnabled = true
    }

    // ==================== COLA DE MEDIDAS (desde MedidaActivity) ====================

    // Navegación de la cola (chip flotante, omitir, editar producto): la misma que usan el resto de
    // calculadoras vía ControladorColaMedidas. Nova conserva su propio manejo del gráfico original.
    private val navegadorCola by lazy {
        NavegadorCola(this, NovaCorrediza::class.java, { df1(it) }) { item, indice ->
            cargarMedidaCola(item, indice)
        }
    }

    private fun inicializarDesdeMedidas() {
        if (!ColaCalculadoras.desdeMedidas(this)) return
        cargarYMostrarBocetoOriginal(ColaCalculadoras.bocetoPath(this))
        navegadorCola.instalarChip()
        // La PRIMERA medida no pasa por el navegador: sus datos llegan en el intent. El contorno
        // no viaja ahí, así que se aplica aquí o el vano escalonado entraría como un rectángulo.
        val actual = ColaCalculadoras.cola(this).getOrNull(ColaCalculadoras.indice(this))
        if (actual != null && cargarEsquinaDeMedida(actual.esquina, actual.contornosLados, actual.parantesLados)) return
        if (actual != null && cargarDisenoDelContorno(actual.contorno)) {
            Toast.makeText(
                this,
                "Vano con forma: el diseño trae sus tramos, pero el cálculo aún no cuenta las piezas del escalón",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Gestos sobre ivDiseno. Se instalan siempre; los que miran la medida original de
     * MedidaActivity no hacen nada si no hay ninguna.
     * - doble toque -> diálogo grande con la medida original (con zoom)
     * - toque simple -> menú de opciones (igual que siempre)
     * - toque largo -> DisenoNova (igual que siempre)
     * - arrastre horizontal -> recorre los repartos de módulos en tramos
     * - arrastre vertical -> alterna en el thumbnail entre diseño y medida original
     * Al fijar este OnTouchListener reemplazamos los listeners de click/long-click nativos, por eso
     * el detector replica esas acciones.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun configurarArrastreDiseno() {
        val umbral = 60f * resources.displayMetrics.density
        val detector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent) = true
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                abrirDialogoOpcionesNova()
                return true
            }
            override fun onDoubleTap(e: MotionEvent): Boolean {
                mostrarDialogoBocetoOriginal()
                return true
            }
            override fun onLongPress(e: MotionEvent) {
                abrirDisenoInteractivo()
            }
        })
        binding.ivDiseno.setOnTouchListener { _, event ->
            detector.onTouchEvent(event)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    arrastreDownX = event.x
                    arrastreDownY = event.y
                }
                MotionEvent.ACTION_UP -> {
                    val dx = event.x - arrastreDownX
                    val dy = event.y - arrastreDownY
                    val horizontal = kotlin.math.abs(dx) > umbral &&
                        kotlin.math.abs(dx) > kotlin.math.abs(dy) * 1.5f
                    val vertical = kotlin.math.abs(dy) > umbral &&
                        kotlin.math.abs(dy) > kotlin.math.abs(dx) * 1.5f
                    when {
                        // Arrastre horizontal: recorrer los repartos de módulos en tramos.
                        // Hacia la izquierda avanza, hacia la derecha vuelve.
                        horizontal -> cambiarReparto(if (dx < 0) 1 else -1)
                        // El boceto original pasó al arrastre vertical, que estaba libre.
                        vertical -> alternarBocetoOriginal()
                    }
                }
            }
            true
        }
    }

    /**
     * Recorre los repartos posibles de los módulos en tramos. Con 10 divisiones son [5,5],
     * [3,4,3], [2,3,3,2] y [2,2,2,2,2]: el vidriero elige el que le convenga en obra en vez de
     * quedarse con el tramo más largo.
     *
     * El reparto elegido manda sobre todo lo demás —dibujo, puentes, U, vidrios— porque todos
     * salen de `gruposDivisionesPorTramo`.
     */
    private fun cambiarReparto(paso: Int) {
        val div = divisiones()
        val opciones = NovaCalculos.repartosPosibles(div)
        if (opciones.size <= 1) {
            Toast.makeText(this, "Con $div divisiones solo hay un reparto", Toast.LENGTH_SHORT).show()
            return
        }
        val actual = NovaCalculos.repartoManualPara(div)
        val desde = opciones.indexOfFirst { it == actual }.takeIf { it >= 0 } ?: 0
        val nuevo = (desde + paso).coerceIn(0, opciones.lastIndex)
        if (nuevo == desde && actual != null) return
        val reparto = opciones[nuevo]
        NovaCalculos.repartoManual = reparto
        Toast.makeText(
            this,
            "Tramos: ${reparto.joinToString(" + ")}  (${nuevo + 1}/${opciones.size})",
            Toast.LENGTH_SHORT
        ).show()
        binding.btCalcular.performClick()
    }

    private fun abrirDisenoInteractivo() {
        val intent = Intent(this, DisenoNovaActivity::class.java)
        if (ultimoPaquete.isNotBlank()) {
            intent.putExtra(DisenoNovaActivity.EXTRA_PAQUETE, ultimoPaquete)
        }
        intent.putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, mochetaLateralDisenoNl())
        intent.putExtra(DisenoNovaActivity.EXTRA_ENCUENTRO_VACIO, metaEncuentro)
        intent.putExtra(DisenoNovaActivity.EXTRA_DIRECCION, metaDireccion)
        intent.putExtra(DisenoNovaActivity.EXTRA_US_CM, binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f)
        lanzarDisenoInteractivo.launch(intent)
    }

    /** Muestra la medida original en grande, con pellizco para zoom y arrastre para desplazar. */
    @SuppressLint("ClickableViewAccessibility")
    private fun mostrarDialogoBocetoOriginal() {
        val original = bocetoOriginalDrawable ?: run {
            Toast.makeText(this, "No hay medida original para mostrar", Toast.LENGTH_SHORT).show()
            return
        }
        val imagen = ImageView(this).apply {
            setImageDrawable(original)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setBackgroundColor(Color.WHITE)
            minimumHeight = (resources.displayMetrics.heightPixels * 0.6f).toInt()
        }

        var escala = 1f
        val zoom = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(d: ScaleGestureDetector): Boolean {
                escala = (escala * d.scaleFactor).coerceIn(1f, 6f)
                imagen.scaleX = escala
                imagen.scaleY = escala
                if (escala == 1f) {
                    imagen.translationX = 0f
                    imagen.translationY = 0f
                }
                return true
            }
        })
        var ultimoX = 0f
        var ultimoY = 0f
        imagen.setOnTouchListener { _, e ->
            zoom.onTouchEvent(e)
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    ultimoX = e.rawX
                    ultimoY = e.rawY
                }
                MotionEvent.ACTION_MOVE -> if (escala > 1f && e.pointerCount == 1) {
                    imagen.translationX += e.rawX - ultimoX
                    imagen.translationY += e.rawY - ultimoY
                    ultimoX = e.rawX
                    ultimoY = e.rawY
                }
            }
            true
        }

        AlertDialog.Builder(this)
            .setTitle("Medida original")
            .setView(imagen)
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun cargarYMostrarBocetoOriginal(path: String) {
        val bmp = if (path.isNotBlank()) BitmapFactory.decodeFile(path) else null
        bocetoOriginalDrawable = bmp?.let { BitmapDrawable(resources, it) }
        if (bocetoOriginalDrawable == null) {
            mostrandoBocetoOriginal = false
            return
        }
        disenoCalculadoGuardado = binding.ivDiseno.drawable
        mostrarBocetoOriginal()
    }

    private fun mostrarBocetoOriginal() {
        val d = bocetoOriginalDrawable ?: return
        binding.ivDiseno.scaleType = ImageView.ScaleType.FIT_CENTER
        binding.ivDiseno.setImageDrawable(d)
        binding.ivDiseno.visibility = View.VISIBLE
        mostrandoBocetoOriginal = true
    }

    private fun restaurarDisenoSiMostrandoOriginal() {
        if (!mostrandoBocetoOriginal) return
        disenoCalculadoGuardado?.let { binding.ivDiseno.setImageDrawable(it) }
        mostrandoBocetoOriginal = false
    }

    private fun alternarBocetoOriginal() {
        if (bocetoOriginalDrawable == null) return
        if (mostrandoBocetoOriginal) {
            restaurarDisenoSiMostrandoOriginal()
        } else {
            disenoCalculadoGuardado = binding.ivDiseno.drawable
            mostrarBocetoOriginal()
        }
    }

    /** Tras archivar, ofrece abrir la siguiente medida de la cola (si viene de MedidaActivity). */
    private fun ofrecerSiguienteMedidaCola() {
        navegadorCola.ofrecerSiguiente()
    }

    /** Repuebla los campos con la siguiente medida (misma calculadora) y muestra su gráfico original. */
    @SuppressLint("SetTextI18n")
    private fun cargarMedidaCola(item: ColaCalculadoras.MedidaCalc, indice: Int) {
        intent.putExtra(ColaCalculadoras.EXTRA_INDICE, indice)
        intent.putExtra(ColaCalculadoras.EXTRA_BOCETO_PATH, item.bocetoArchivo)
        // Igual que el controlador compartido: el archivado debe usar la cantidad y el producto de
        // la medida que se acaba de cargar, no los de la que entró al abrir la pantalla.
        intent.putExtra("cantidad", item.cantidad)
        intent.putExtra("producto", item.producto)
        binding.etAncho.setText(df1(item.ancho))
        binding.etAlto.setText(df1(item.alto))
        cargarYMostrarBocetoOriginal(item.bocetoArchivo)
        binding.etAncho.requestFocus()
        // Una ventana de esquina trae sus lados: se arma su geometría en vez de un vano plano.
        if (cargarEsquinaDeMedida(item.esquina, item.contornosLados, item.parantesLados)) return
        val escalonada = cargarDisenoDelContorno(item.contorno)
        Toast.makeText(
            this,
            if (escalonada) "Vano con forma: el diseño trae sus tramos, pero el cálculo aún no cuenta las piezas del escalón"
            else "Medida cargada: revisa el gráfico y calcula",
            Toast.LENGTH_SHORT
        ).show()
    }

    /**
     * Arma el diseño desde el contorno del vano, cuando la medida trae uno que no es un rectángulo.
     *
     * Un vano recto se describe con el ancho y el alto y no necesita nada de esto; uno con el
     * alféizar subido en un trozo, o con un lado inclinado, no: son tramos de distinto alto
     * colgando del mismo dintel, y eso hay que traerlo hecho o el vidriero lo arma a mano cada vez.
     *
     * Devuelve true si el diseño se cargó desde el contorno.
     */
    /**
     * Arma la geometría desde los lados de una ventana de esquina medida en el apunte.
     *
     * La CANTIDAD de lados es la que manda —2 en L, 3 en C, más en serie—, que es la misma regla
     * con la que ya trabaja el paquete simbólico. Los lados entran como si se hubieran escrito a
     * mano con "Agregar", y el último se queda en los campos, que es donde la pantalla lo busca al
     * calcular.
     *
     * Devuelve true si la medida traía una esquina y quedó puesta.
     */
    /**
     * ¿El diseño que vuelve del editor es una ventana de esquina?
     *
     * Si lo es, sus medidas de lado NO se tocan. Dos razones: el ancho de la cabecera de una
     * ventana de esquina es el del PRIMER lado, no el de la ventana —escribirlo en el campo del
     * ancho ponía la medida de un lado en el otro, y el lado 2 salía con los 147.5 del lado 1—; y
     * los anchos que el diseño lleva dentro son los ÚTILES, ya descontados el parante y el
     * esquinero, mientras que en los campos va lo que se midió. Los descuentos se hacen al
     * calcular, no en lo que el vidriero tiene escrito.
     *
     * Lo que el editor sí trae de vuelta —los módulos, las franjas, los altos— entra por el
     * camino de siempre, que es el que arma el diseño desigual.
     */
    private fun esDisenoDeEsquina(paquete: String): Boolean =
        runCatching { crystal.crystal.Diseno.nova.DisenoNova.desdePaquete(paquete) }
            .getOrNull()?.doblaEnEsquina == true

    private fun cargarEsquinaDeMedida(texto: String, contornosLados: String = "", parantesLados: String = ""): Boolean {

        val medida = EsquinaMedida.desdeTexto(texto) ?: return false
        val geo = medida.geometria ?: return false
        val dibujo = when (geo) {
            "nl" -> R.drawable.venl
            "nu" -> R.drawable.venc
            "ncu" -> R.drawable.vcurvo
            else -> R.drawable.vserie
        }
        seleccionarDesdePanel(dibujo, geo)
        esquinaDeLaMedida = medida
        // Lo que trajo además la esquina armada sobre figuras: DESPUÉS de elegir la geometría, que
        // elegirla limpia el estado de la L y se llevaba esto por delante.
        siluetasDeLaMedida = LadosLibres.contornosDesdeTexto(contornosLados)
        parantesDeLaMedida = LadosLibres.parantesDesdeTexto(parantesLados)
        // Lo que traiga la medida SIEMBRA el diseño; la última palabra es de Nova. El reparto a
        // mano de la ventana anterior no tiene nada que decir sobre esta.
        NovaCalculos.repartoManual = null
        // La ventana curva no tiene lados que agregar: es UNA ventana con su arco. Lo que la
        // describe es su desarrollo —lo que se corta— y su cuerda, que van a sus casillas.
        if (geo == "ncu") {
            val primera = medida.lados.first()
            // El ancho de una ventana curva es su DESARROLLO entero, sumando todos sus arcos: es
            // lo que se corta. Y la flecha, la del arco equivalente a todos ellos —el mismo
            // desarrollo girando lo mismo—, no la del primero: dándole la cuerda de UN arco y el
            // desarrollo de la ventana entera, la calculadora rehacía el arco desde esa cuerda y
            // se quedaba con un arco solo, que es de donde salían los 89 en vez de los 180.
            val desarrollo = medida.lados.sumOf { it.ancho.toDouble() }.toFloat()
            val giroTotal = medida.lados.sumOf { lado ->
                (crystal.crystal.taller.ArcoEsquina
                    .deDesarrolloYFlecha(lado.ancho, lado.flecha)?.anguloGrados ?: 0f).toDouble()
            }
            binding.etAncho.setText(df1(desarrollo))
            binding.etAlto.setText(df1(primera.alto))
            binding.etHoja.setText(df1(primera.puente))
            binding.etPartes.setText("0")
            binding.etFlecha.setText(df1(flechaDeTodaLaCurva(desarrollo, giroTotal.toFloat())))
            // La cuerda se deja en blanco a propósito: escrita, manda sobre el ancho y la
            // calculadora rehace el arco desde ella. Aquí el arco YA es el desarrollo medido.
            binding.etCuerda.setText("")
            // Si la curva se midió partida, sus trozos son el punto de PARTIDA de los tramos: se
            // siembran como reparto a mano. Y con eso queda dicho lo demás solo, porque un reparto
            // a mano solo vale mientras las divisiones sean las mismas: en cuanto en Nova se
            // cambian —se acepta el parante que sugiere, o se le quita uno— el reparto deja de
            // valer y manda otra vez la regla de Nova. La medida siembra; Nova decide.
            if (medida.lados.size > 1) {
                val divisiones = NovaCalculos.divisiones(desarrollo, 0)
                NovaCalculos.repartoManual =
                    repartoSegunLosArcos(medida.lados.map { it.ancho }, divisiones)
            }
            avisarDeLaEsquina(medida)
            return true
        }
        contadorLado = 1
        binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
        medida.lados.dropLast(1).forEach { agregarLado(it.ancho, it.alto, it.puente, 0) }
        val ultimo = medida.lados.last()
        binding.etAncho.setText(df1(ultimo.ancho))
        binding.etAlto.setText(df1(ultimo.alto))
        binding.etHoja.setText(df1(ultimo.puente))
        binding.etPartes.setText("0")
        avisarDeLaEsquina(medida)
        return true
    }

    /**
     * Lo que va entre un lado y el siguiente en el diseño.
     *
     * Si la esquina dobla en punta es un pliegue, `A<grados>`. Si la resolvieron con una curva no
     * hay pliegue: la pared no dobla CONTRA la curva, entra en ella. Lo que va es un paño más, el
     * del desarrollo —que es el aluminio que se corta— con su tag de arco.
     */
    private fun esquinaEnElDiseno(arista: Int, altoRef: Float, puenteRef: Float): String {
        val medida = esquinaDeLaMedida ?: return "A<90>"
        // La curva es un paño más Y un pliegue: la pared entra en ella de frente y sale girada,
        // así que detrás de su paño va el `A<>` que pone en perspectiva a la pared siguiente. Sin
        // él la L salía como tres paños en fila, de frente, y no se leía la esquina.
        medida.curvaDe(arista)?.let {
            // Lo que gira lo dice el propio arco: es el ángulo entre sus dos puntas.
            val grados = crystal.crystal.taller.ArcoEsquina
                .deDesarrolloYCuerda(it.desarrollo, it.cuerda)?.anguloGrados ?: 90f
            return tramoDeCurva(it, altoRef, puenteRef) + " A<${df1(grados)}>"
        }
        val grados = medida.gradosDe(arista) ?: return "A<90>"
        return "A<${df1(kotlin.math.abs(grados))}>"
    }

    /**
     * El paño de una pared curva, como un tramo más del diseño.
     *
     * Su ancho es el DESARROLLO: el aluminio va curvado, pero lo que se corta es lo que mide
     * estirado. El tag `U<flecha>` dice cuánta panza hace, que es lo que el dibujo necesita.
     * Si el apunte no trajo alto o puente para la curva se usan los de la pared de al lado, que
     * es contra la que se encuentra.
     */
    /**
     * Le pone su panza al tramo de un lado que ES la pared curva.
     *
     * Pasa cuando la pared a la que sustituye mide cero: la ventana empieza (o acaba) en la
     * curva, y entonces la curva no es la esquina entre dos paredes sino una pared más. Sin esto
     * salía dibujada recta.
     */
    private fun conPanzaDelLado(indice: Int, tramos: String): String {
        val flecha = esquinaDeLaMedida?.lados?.getOrNull(indice)?.flecha ?: 0f
        return if (flecha > 0f) conTagEnElSistema(tramos, "Q<${df1(flecha)}>") else tramos
    }

    private fun tramoDeCurva(curva: CurvaEsquina, altoRef: Float, puenteRef: Float): String {

        val alto = if (curva.alto > 1f) curva.alto else altoRef
        val puente = if (curva.puente > 0.5f) curva.puente else puenteRef
        val flecha = crystal.crystal.taller.ArcoEsquina
            .deDesarrolloYCuerda(curva.desarrollo, curva.cuerda)?.flecha ?: 0f
        val divis = NovaCalculos.divisiones(curva.desarrollo, 0, "nn")
        val tramos = NovaUIHelper.generarTramosConsolidado(
            curva.desarrollo, alto, NovaCalculos.altoHoja(alto, puente), divis,
            NovaCalculos.siNoMoch(alto, puente), textoModelo,
            mochetaInferiorDoblePuente(), modeloRemate
        )
        return conTagEnElSistema(tramos, "Q<${df1(flecha)}>")
    }

    /**
     * Reparte los módulos entre los arcos medidos, a lo que le toca a cada uno por su desarrollo.
     *
     * Cada arco se queda con al menos uno —un trozo de ventana sin módulos no es nada— y entre
     * todos suman exactamente los que Nova dice que lleva la ventana: el reparto a mano cambia
     * dónde caen los parantes, no cuántas divisiones hay.
     */
    private fun repartoSegunLosArcos(anchos: List<Float>, divisiones: Int): List<Int>? {
        if (anchos.size < 2 || divisiones < anchos.size) return null
        val total = anchos.sum().takeIf { it > 0f } ?: return null
        val reparto = anchos.map { (divisiones * it / total).toInt().coerceAtLeast(1) }.toMutableList()
        var sobran = divisiones - reparto.sum()
        // Lo que sobre o falte por redondear va al arco más largo, que es el que menos lo nota.
        var vuelta = 0
        while (sobran != 0 && vuelta < 1000) {
            val cual = if (sobran > 0) {
                anchos.indices.maxByOrNull { anchos[it] / reparto[it] } ?: 0
            } else {
                anchos.indices.filter { reparto[it] > 1 }.minByOrNull { anchos[it] / reparto[it] }
                    ?: return null
            }
            reparto[cual] += if (sobran > 0) 1 else -1
            sobran = divisiones - reparto.sum()
            vuelta++
        }
        return if (reparto.sum() == divisiones) reparto else null
    }

    /**
     * La flecha del arco que equivale a toda la ventana curva.

     *
     * Varios arcos seguidos, cada uno con su panza, hacen en conjunto una curva sola: la que tiene
     * ese mismo desarrollo y gira lo que giran todos juntos. Con un arco solo devuelve su propia
     * flecha, que es lo mismo por definición.
     */
    private fun flechaDeTodaLaCurva(desarrolloCm: Float, giroGrados: Float): Float {
        if (desarrolloCm <= 0f || giroGrados <= 0.01f) return 0f
        val giro = Math.toRadians(giroGrados.toDouble())
        val radio = desarrolloCm / giro
        return (radio * (1.0 - kotlin.math.cos(giro / 2.0))).toFloat()
    }

    /**
     * Lo que el vidriero tiene que mirar antes de calcular

: qué se armó y qué se dio por supuesto.
     *
     * El apunte sabe más que la calculadora —el descuadre de cada pared, el ángulo real, la esquina
     * curva—, así que en vez de callarse lo que se ha simplificado se dice, y el gráfico de la
     * medida original está a un toque para comprobarlo.
     */
    private fun avisarDeLaEsquina(medida: EsquinaMedida) {
        val partes = mutableListOf(
            when {
                medida.lados.all { it.esCurva } && medida.lados.size == 1 -> "curva"
                medida.lados.all { it.esCurva } -> "curva, de ${medida.lados.size} arcos"
                medida.lados.size == 2 -> "en L"
                medida.lados.size == 3 -> "en C"
                else -> "en serie de ${medida.lados.size} lados"
            }
        )
        if (medida.hayCurva) {
            partes.add("la pared curva entra en el diseño con su desarrollo, pero sus materiales todavía no se cuentan")
        }
        val torcidos = medida.anguloDistinto
        if (torcidos.isNotEmpty()) {
            partes.add(
                "dobla ${torcidos.joinToString(" y ") { df1(kotlin.math.abs(it)) }}°: " +
                    "el esquinero se descuenta como si fuera de 90"
            )
        }
        if (medida.lados.any { it.descuadrado }) {
            partes.add("hay descuadre: se tomó la medida MAYOR de cada lado, revise el gráfico")
        }
        Toast.makeText(this, "Ventana de esquina ${partes.joinToString(". ")}", Toast.LENGTH_LONG).show()
    }

    private fun cargarDisenoDelContorno(contorno: String): Boolean {

        // Cada medida trae el suyo: el de la anterior no vale para esta.
        contornoMedida = ""
        val puntos = ContornoEnTramos.desdeTexto(contorno)
        if (puntos.size < 3) return false
        val acabado = when (tipoNova) {
            TipoNova.APA -> "apa"
            TipoNova.PIV -> "piv"
            else -> "ina"
        }
        val hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        val diseno = runCatching {
            ContornoEnTramos.disenoDesdeContorno(puntos, acabado, hoja)
        }.getOrNull() ?: return false
        if (!diseno.esIrregular) return false
        cargarDesdePaqueteDiseno(diseno.aPaquete())
        contornoMedida = contorno
        hojaDelDiseno = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        medidaDelDiseno = diseno.ancho to diseno.alto
        return true
    }

    /**
     * Rehace el diseño del vano cuando el vidriero escribe otro alto de hoja.
     *
     * En un vano con forma la hoja no es un dato más: es la que dice DÓNDE cabe la corrediza —el
     * rectángulo del medio de un triángulo, lo que queda bajo un dintel caído— y con ella cambia el
     * reparto entero. Escribirla soltaba el diseño como cualquier otra edición a mano y la ventana
     * volvía a salir rectangular, justo al dar el dato que hacía falta.
     *
     * Solo se rehace si el ancho y el alto siguen siendo los del vano: con otra medida en los campos
     * el vidriero está en otra cosa y su diseño no se toca.
     */
    private fun rehacerDisenoSiCambioLaHoja() {
        if (contornoMedida.isBlank()) return
        val hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        if (kotlin.math.abs(hoja - hojaDelDiseno) <= 0.05f) return
        val ancho = binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f
        val alto = binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f
        if (kotlin.math.abs(ancho - medidaDelDiseno.first) > 0.15f ||
            kotlin.math.abs(alto - medidaDelDiseno.second) > 0.15f
        ) return
        cargarDisenoDelContorno(contornoMedida)
    }

    private fun mostrarDialogoMetadatosProduccion(onContinuar: () -> Unit) {
        val pad = (16 * resources.displayMetrics.density).toInt()
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, 0)
        }
        val etColor = EditText(this).apply {
            hint = "Color aluminio (ej: negro)"
            setText(metaColorAluminio)
        }
        val etVidrio = EditText(this).apply {
            hint = "Tipo vidrio (ej: incoloro 6mm)"
            setText(metaTipoVidrio)
        }
        val etAcabadoSup = EditText(this).apply {
            hint = "Acabado superficial (opcional)"
            setText(metaAcabadoSuperficial)
        }
        val etObs = EditText(this).apply {
            hint = "Observaciones (opcional)"
            setText(metaObservaciones)
        }
        contenedor.addView(etColor)
        contenedor.addView(etVidrio)
        contenedor.addView(etAcabadoSup)
        contenedor.addView(etObs)

        AlertDialog.Builder(this)
            .setTitle("Metadatos para Diseño Simbólico V2")
            .setView(contenedor)
            .setPositiveButton("Guardar y archivar") { _, _ ->
                metaColorAluminio = etColor.text?.toString()?.trim().orEmpty()
                metaTipoVidrio = etVidrio.text?.toString()?.trim().orEmpty()
                metaAcabadoSuperficial = etAcabadoSup.text?.toString()?.trim().orEmpty()
                metaObservaciones = etObs.text?.toString()?.trim().orEmpty()
                onContinuar()
            }
            .setNeutralButton("Omitir") { _, _ ->
                onContinuar()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun devolverResultadoMasivo() {
        val calculadora = when (tipoNova) {
            TipoNova.APA -> "Nova Aparente"
            TipoNova.INA -> "Nova Inaparente"
            TipoNova.PIV -> "Nova Pivotante"
        }
        val accesorios = if (tipoNova == TipoNova.APA) {
            mapOf(
                "Portafelpa" to ModoMasivoHelper.texto(binding.txPf),
                "Tope" to ModoMasivoHelper.texto(binding.txTo),
                "Tee" to ModoMasivoHelper.texto(binding.txTe)
            )
        } else {
            mapOf(
                "Portafelpa" to ModoMasivoHelper.texto(binding.txPf),
                "Tope" to ModoMasivoHelper.texto(binding.txTo)
            )
        }
        val perfiles = mapOf(
            ModoMasivoHelper.texto(binding.tvU) to ModoMasivoHelper.texto(binding.txU),
            ModoMasivoHelper.texto(binding.tvP) to ModoMasivoHelper.texto(binding.txP),
            ModoMasivoHelper.texto(binding.tvFc) to ModoMasivoHelper.texto(binding.txFc),
            ModoMasivoHelper.texto(binding.tvR) to ModoMasivoHelper.texto(binding.txR),
            ModoMasivoHelper.texto(binding.tvT) to ModoMasivoHelper.texto(binding.txT),
            ModoMasivoHelper.texto(binding.tvH) to ModoMasivoHelper.texto(binding.txH)
        ).filter { it.value.isNotBlank() }
        val paqueteDiseno = try { disenoSimbolico() } catch (_: Exception) { "" }
        val paqueteDisenoV2 = try { disenoSimbolicoV2() } catch (_: Exception) { "" }
        ModoMasivoHelper.devolverResultado(
            activity = this,
            calculadora = calculadora,
            perfiles = perfiles,
            vidrios = ModoMasivoHelper.texto(binding.txV),
            accesorios = accesorios.filter { it.value.isNotBlank() },
            referencias = if (referenciasColapsable) referenciasFull else ModoMasivoHelper.texto(binding.txReferencias),
            disenoPaquete = paqueteDiseno,
            disenoSimbolicoV2 = paqueteDisenoV2
        )
    }

    private fun archivarMapas() {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
            mapListas.clear()
            if (mapExistente != null) mapListas.putAll(mapExistente)
        }

        val prefijo = obtenerPrefijo()
        val cant = cantidadProducto
        var ultimoID = ""

        // Si la vista de referencias está contraída, archivar el texto completo (sin spans).
        if (referenciasColapsable) binding.txReferencias.text = referenciasFull

        // Los números se reservan ANTES del bucle: dentro, nada se ha guardado todavía y
        // obtenerSiguienteContadorPorPrefijo devolvería el mismo para todas las copias.
        val numerosPaquete = ProyectoManager.reservarNumerosPorPrefijo(this, prefijo, cant)
        for (u in 1..cant) {
            val siguienteNumero = numerosPaquete[u - 1]
            val identificadorPaquete = "${prefijo}${siguienteNumero}"
            ultimoID = identificadorPaquete
            val matNl = if (texto == "nl") materialesNlArchivables else null

            if (esValido(binding.lyReferencias)) {
                ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvReferencias, binding.txReferencias, mapListas, identificadorPaquete)
            }
            if (esValido(binding.u13layout)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvU, txArchivableNl(binding.txU, matNl?.u), mapListas, identificadorPaquete)
            }
            if (esValido(binding.mulLayout)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvP, txArchivableNl(binding.txP, matNl?.p), mapListas, identificadorPaquete)
            }
            if (esValido(binding.fcLayout)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvFc, txArchivableNl(binding.txFc, matNl?.fc), mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyRiel)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvR, txArchivableNl(binding.txR, matNl?.r), mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTubo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvT, txArchivableNl(binding.txT, matNl?.tubo), mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyFalsoPuente)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvFalsoPuente, binding.txFalsoPuente, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyPf)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvPf, txArchivableNl(binding.txPf, matNl?.pf), mapListas, identificadorPaquete)
            }
            if (tipoNova == TipoNova.APA && esValido(binding.tLayout)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvTe, txArchivableNl(binding.txTe, matNl?.te), mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvTo, txArchivableNl(binding.txTo, matNl?.tope), mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyH)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvH, txArchivableNl(binding.txH, matNl?.hache), mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyVidrios)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvV, txArchivableNl(binding.txV, matNl?.vidrio), mapListas, identificadorPaquete)
            }
            // Cliente: archivar directamente (no usa formato "valor = cantidad")
            val clienteTexto = binding.txC.text.toString()
            if (clienteTexto.isNotBlank()) {
                val entradaCliente = mutableListOf(clienteTexto, "", identificadorPaquete)
                mapListas.getOrPut("Cliente") { mutableListOf() }.add(entradaCliente)
            }
            if (esValido(binding.lyAncho)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvAncho, binding.txAncho, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyAlto)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvAlto, binding.txAlto, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyPuente)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvPuente, binding.txPuente, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyDivisiones)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvDivisiones, binding.txDivisiones, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyFijos)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvFijos, binding.txFijos, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyCorredizas)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvCorredizas, binding.txCorredizas, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyDiseno)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvDiseno, binding.txDiseno, mapListas, identificadorPaquete)
            }
            try {
                val paquete = disenoSimbolico()
                if (paquete.isNotBlank()) {
                    val entrada = mutableListOf(paquete, "", identificadorPaquete)
                    mapListas.getOrPut("DisenoPaquete") { mutableListOf() }.add(entrada)
                }
            } catch (_: Exception) { }
            try {
                val paqueteV2 = disenoSimbolicoV2(siguienteNumero)
                if (paqueteV2.isNotBlank()) {
                    val entradaV2 = mutableListOf(paqueteV2, "", identificadorPaquete)
                    mapListas.getOrPut("DisenoSimbolicoV2") { mutableListOf() }.add(entradaV2)
                }
            } catch (_: Exception) { }
            if (esValido(binding.lyGrados)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvGrados, binding.txGrados, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTipo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvTipo, binding.txTipo, mapListas, identificadorPaquete)
            }

            ProyectoManager.actualizarContadorPorPrefijo(this, prefijo, siguienteNumero)
        }

        // Restaurar la vista compactable tras archivar.
        if (referenciasColapsable) renderReferencias()

        MapStorage.guardarMap(this, mapListas)
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
        val msg = if (cant > 1) "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
                  else "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun txArchivableNl(original: TextView, textoTotal: String?): TextView {
        if (texto != "nl" || textoTotal == null) return original
        return TextView(this).apply { text = textoTotal }
    }

    private fun actualizarTxPrConDisenoV2(numeroProductoAuto: Int? = null) {
        binding.txPr.text = try {
            disenoSimbolicoV2(numeroProductoAuto)
        } catch (_: Exception) {
            ""
        }
    }

    // ==================== BACK ====================
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            devolverResultadoMasivo()
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}


