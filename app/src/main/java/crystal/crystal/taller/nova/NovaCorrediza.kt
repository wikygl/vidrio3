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
    /** Puente predeterminado cuando el elegido no se admite (N-03). */
    private val puentePorDefecto = "tubo 2\u215C x 1"
    private var diseno: String = ""
    private var texto: String = ""
    private var otros: Boolean = false
    // Referencias compactables (en L / en C / serie): texto completo + estado de expansión.
    private var referenciasFull: String = ""
    private var referenciasColapsable = false
    private var referenciasExpandida = false
    // Detalle de cada material (curvado de Nova curva): la vista corta es la que se archiva.
    private var materialesCompactos: Map<TextView, String> = emptyMap()
    private var materialesDetalle: Map<TextView, String> = emptyMap()
    private var materialesArchivo: Map<TextView, String> = emptyMap()
    private var materialesDesplegados = false
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
    /**
     * Las caras del vidrio curvo, dichas al archivar (2026-10-01): null si no se preguntó (no hay
     * vidrio curvo, o se omitió el diálogo). Con ellas se archiva si la cara especial queda cóncava
     * o convexa en cada vidrio.
     */
    private var metaCaras: crystal.crystal.taller.nova.motor.CurvadoNova.Caras? = null
    /** Los materiales del último cálculo, para rehacer lo que se archiva con las caras. */
    private var ultimosMateriales: crystal.crystal.taller.nova.motor.MaterialesNova? = null
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

    /**
     * Los lados de la L, la C y la serie, en lista: rectos o curvos, con su unión (2026-10-03). Es la
     * única fuente de sus medidas; al calcular se lee en [listaActual].
     */
    private val listaLados by lazy {
        ListaDeLadosNova(
            binding.lyListaLados,
            tinte = { binding.etAncho.backgroundTintList },
            colorTitulo = { binding.tvMedidas.currentTextColor }
        )
    }

    /** La lista leída en el último cálculo. */
    private var listaActual: crystal.crystal.taller.VentanaMedida? = null

    /** La lista leída al calcular o, si todavía no se calculó, la de la pantalla; null fuera de L, C y serie. */
    private fun listaVigente(): crystal.crystal.taller.VentanaMedida? =
        if (!modoLista) null else listaActual ?: listaLados.leer().medida?.takeIf { it.paredes.isNotEmpty() }

    /** L, C y serie se escriben en la lista de lados. */
    private val modoLista: Boolean get() = texto == "nl" || texto == "nu" || texto == "ns"
    private var indiceNs = 0
    private var materialesNlArchivables: EstadoMateriales? = null

    // Medidas que vienen de MedidaActivity (cola de calculadoras).
    private var bocetoOriginalDrawable: Drawable? = null   // gráfico de la medida original
    private var bocetoOriginalRuta: String = ""            // su archivo, para pasarlo a novaDiseño
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
            // Lo que se ve ahora es el diseño, no el apunte original. Sin esto, si el dibujo llegaba
            // mientras se mostraba el apunte (al cargar una medida con forma), Calcular "volvía al
            // diseño" poniendo la imagen guardada de antes: el rectángulo (Fide, 2026-10-01).
            mostrandoBocetoOriginal = false
            disenoCalculadoGuardado = binding.ivDiseno.drawable
        }
    }

    private val lanzarDisenoInteractivo = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == RESULT_OK) {
            val paquete = res.data?.getStringExtra(DisenoNovaActivity.RESULT_PAQUETE) ?: return@registerForActivityResult
            recibirDisenoEditado(paquete)
        }
    }

    /**
     * El diseño que vuelve de novaDiseño. Es ahora EL diseño: su hoja y su medida pasan a ser la
     * referencia del vano con forma. Sin esto, si la hoja del diseño editado no era la con que se
     * armó desde el contorno, el siguiente Calcular rehacía el vano desde el contorno
     * ([rehacerDesdeLaFormaSiCambio]) y lo editado se perdía. Si después el vidriero escribe otra
     * hoja u otras divisiones, sí se rehace, como siempre.
     */
    private fun recibirDisenoEditado(paquete: String) {
        cargarDesdePaqueteDiseno(paquete)
        if (contornoMedida.isNotBlank()) {
            hojaDelDiseno = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
            medidaDelDiseno = (binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f) to
                (binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f)
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
        configurarRecalculoDeMaterial()
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
            // El diseño editado lleva el acabado en su cabecera ({nova,apa,…}) y de ahí lo leen el
            // motor y el dibujo: sin cambiarlo ahí, el título cambiaba de color y la ventana seguía
            // calculándose en el acabado anterior.
            if (ultimoPaquete.isNotBlank()) ultimoPaquete = conAcabado(ultimoPaquete, tipoPaquete())
            // El diseño y los materiales en el acabado nuevo (ver [alCambiarAcabado]).
            alCambiarAcabado()
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
        binding.tvDetalleMateriales.setOnClickListener {
            materialesDesplegados = !materialesDesplegados
            mostrarMateriales()
        }

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAlto.setText(df1(it)) }

        // Los gestos sobre el diseño (arrastrar para elegir el reparto de tramos) valen siempre,
        // vengan las medidas de MedidaActivity o se escriban a mano aquí.
        configurarArrastreDiseno()
        // Cola de medidas que viene de MedidaActivity: mostrar el gráfico original.
        inicializarDesdeMedidas()
        // Abierta desde Productos para editar una ventana archivada: su diseño, tal como se guardó.
        crystal.crystal.casilla.EdicionProducto.disenoDe(this)?.let { cargarDesdePaqueteDiseno(it) }
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
        binding.etDesarrollo.backgroundTintList = tintList
        binding.etAltoCurva.backgroundTintList = tintList
        binding.etPuenteCurva.backgroundTintList = tintList
        binding.etDiviCurva.backgroundTintList = tintList
        listOf(binding.etFlechaE2, binding.etCuerdaE2, binding.etDesarrolloE2, binding.etAltoCurvaE2, binding.etPuenteCurvaE2, binding.etDiviCurvaE2)
            .forEach { it.backgroundTintList = tintList }
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
                // L, C y serie: los lados salen de la lista (2026-10-03).
                if (modoLista && !prepararLista()) return@setOnClickListener
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
                // L, C y serie ya se leyeron de la lista (prepararLista): cada lado y cada curva tienen
                // sus casillas, y lo que falte lo avisa la lista. Las validaciones de los campos fijos
                // de cada geometría y la esquina curva escrita aparte se retiraron el 2026-10-03.

                // El vano con forma se rehace desde ella si cambió el alto de hoja o las divisiones.
                rehacerDesdeLaFormaSiCambio()
                // Con un diseño cargado, medidas cambiadas no lo sueltan en silencio: se pregunta.
                if (!confirmarMedidasDelDiseno()) return@setOnClickListener

                // Fase 4 del motor único (2026-09-30): materiales, referencias y diseño salen del
                // motor. Las rutas viejas (uTexto, otrosAluminios, vidriosTexto, referencias,
                // calcularMaterialesNl/Nu, calcularDesigual) se retiraron; quedan en
                // taller/nova/viejo/RutasViejasNova.kt para consulta y en la etiqueta git
                // `rutas-viejas-nova` para volver a correrlas.
                materialesNlArchivables = null
                if (modulosDesiguales.isNotEmpty()) {
                    aplicarMotor()
                } else {
                    actualizarTxPrConDisenoV2()
                    aplicarMotor()

                    ultimoPaquete = disenoSimbolico()
                    if (texto in setOf("nl", "nu", "ns") && ancho() > 400f) {
                        Toast.makeText(this,
                            "En ventanas grandes la perspectiva puede no ser exacta",
                            Toast.LENGTH_LONG).show()
                    }
                    val intent = Intent(this, DisenoNovaActivity::class.java).apply {
                        putExtra(DisenoNovaActivity.EXTRA_PAQUETE, ultimoPaquete)
                        putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
                        putExtra(DisenoNovaActivity.EXTRA_VISTA_EXTERIOR, vistaExterior)
                        putExtra(DisenoNovaActivity.EXTRA_ANCHO_PX, binding.ivDiseno.width)
                        putExtra(DisenoNovaActivity.EXTRA_ALTO_PX, binding.ivDiseno.height)
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


    /** G-02: el nombre de la U según su alto (1 → u-3/8, 1.5 → u-13, 1.2 → u-1/2, 1.9 → u-3/4). */
    private fun nombreDeLaU(): String {
        val alto = binding.etU.text?.toString()?.replace(",", ".")?.toFloatOrNull() ?: 1.5f
        fun es(v: Float) = kotlin.math.abs(alto - v) < 0.01f
        return when {
            es(1f) -> "u-3/8"
            es(1.5f) -> getString(R.string.u_13)
            es(1.2f) -> "u-1/2"
            es(1.9f) -> "u-3/4"
            else -> "u ${df1(alto)}"
        }
    }

    /** G-02: el ancho de la U según su alto (U-3/8 1 × 1, U-13 1 × 1.5, U-1/2 1.2 × 1.2, U-3/4 1.2 × 1.9). */
    private fun perfilUMotor(): crystal.crystal.taller.nova.motor.PerfilU {
        val alto = binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f
        val ancho = when {
            kotlin.math.abs(alto - 1.2f) < 0.01f || kotlin.math.abs(alto - 1.9f) < 0.01f -> 1.2f
            else -> 1f
        }
        return crystal.crystal.taller.nova.motor.PerfilU(ancho, alto)
    }

    private fun puenteMotor(): crystal.crystal.taller.nova.motor.Puente {
        val op = NovaSpinnerData.obtenerOpcionesTubo().firstOrNull { it.text == puente }
        return crystal.crystal.taller.nova.motor.Puente(puente, tubo, op?.valorEsquina ?: tubo)
    }

    private fun bordeMotor(vacio: Boolean) =
        if (vacio) crystal.crystal.taller.nova.motor.Borde.LIBRE else crystal.crystal.taller.nova.motor.Borde.MURO

    private fun ventanaMotorAutomatica(): crystal.crystal.taller.nova.motor.VentanaNova {
        val actual = MedidaNl(
            ancho = arcoCurvo(binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f),
            alto = binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f,
            hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f,
            divisManual = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
        )
        val lados = medidasDeLaGeometria().ifEmpty { listOf(actual) }
        // El encuentro (N-78) solo se marca en la ventana plana: ARBL, 0 = al vacío.
        val e = if (esGeometriaPlana()) metaEncuentro.padEnd(4, '1') else "1111"
        val esquinero = opcionEsquinero()?.let { crystal.crystal.taller.nova.motor.Puente(it.text, it.valor, it.valorEsquina) }
        // Las esquinas que la medida resolvió con una curva: la curva es otro trozo de la ventana, con
        // su desarrollo, entre dos parantes (N-84). Antes no entraba en el cálculo y sus hojas, su
        // puente y sus vidrios faltaban en la lista (hallado el 2026-09-29, Ele 150 + curva 90 + 120).
        val medidaEsquina = esquinaDeLaMedida?.takeIf { it.lados.size == lados.size && lados.size > 1 }
        val curvas = medidaEsquina?.let { m ->
            (0 until lados.size - 1).map { arista ->
                m.curvaDe(arista)?.let { c -> crystal.crystal.taller.nova.motor.VentanaAutomatica.LadoMedido(c.desarrollo, c.alto, c.puente, c.divisiones) }
            }
        }.orEmpty()
        val entrada = crystal.crystal.taller.nova.motor.VentanaAutomatica.Entrada(
            acabado = if (tipoNova == TipoNova.APA) crystal.crystal.taller.nova.motor.Acabado.APA else crystal.crystal.taller.nova.motor.Acabado.INA,
            // Circular (sección 11): en automático el modelo pasa a bandera.
            modelo = if (textoModelo == "nci") crystal.crystal.taller.nova.motor.ModeloMocheta.BANDERA else modeloMotor(),
            modulacion = if (textoModelo == "nci" || textoModelo.isBlank()) "nn" else textoModelo,
            lados = lados.map { crystal.crystal.taller.nova.motor.VentanaAutomatica.LadoMedido(it.ancho, it.alto, it.hoja, it.divisManual) },
            u = perfilUMotor(), puente = puenteMotor(), cruce = cruce(),
            mochetaInferior = mochetaInferiorDoblePuente(),
            izquierda = bordeMotor(e[3] == '0'), derecha = bordeMotor(e[1] == '0'),
            libresVerticales = ladosVaciosVerticales(), perfilLibre = valorParanteVacio(),
            esquinero = esquinero, curvas = curvas,
            // La curva con la que termina la ventana: la arista de después del último lado (2026-10-03).
            curvaFinal = medidaEsquina?.curvaDe(lados.size - 1)?.let { c ->
                crystal.crystal.taller.nova.motor.VentanaAutomatica.LadoMedido(c.desarrollo, c.alto, c.puente, c.divisiones)
            },
            repartoManual = NovaCalculos.repartoManual, corredizasPorTramoNfc = corredizasPorTramo
        )
        // L, C y serie: los lados de la lista, donde la curva es un lado más (2026-10-03).
        val va = crystal.crystal.taller.nova.motor.VentanaAutomatica
        val deLaLista = listaVigente()?.let { lista ->
            va.conLados(entrada, lista.lados.map { l -> crystal.crystal.taller.nova.motor.VentanaAutomatica.LadoDeLista(crystal.crystal.taller.nova.motor.VentanaAutomatica.LadoMedido(l.ancho, l.alto, l.puente, l.divisiones), l.esCurvo) })
                ?: error("dos curvas seguidas todavía no tienen regla")
        }
        return va.ventana(deLaLista ?: entrada).copy(circular = textoModelo == "nci")
    }

    private fun modeloMotor() = when (modeloRemate) {
        "nr" -> crystal.crystal.taller.nova.motor.ModeloMocheta.INVERTIDO
        "np" -> crystal.crystal.taller.nova.motor.ModeloMocheta.BANDERA
        else -> crystal.crystal.taller.nova.motor.ModeloMocheta.NORMAL
    }

    /** Nova desigual por el mismo motor: el diseño lo lee el lector único (N-81). */
    private fun ventanaMotorDesigual(): crystal.crystal.taller.nova.motor.VentanaNova? {
        if (ultimoPaquete.isBlank()) return null
        val esquinero = opcionEsquinero()?.let { crystal.crystal.taller.nova.motor.Puente(it.text, it.valor, it.valorEsquina) }
        return crystal.crystal.taller.nova.motor.DesdeDiseno.ventana(
            ultimoPaquete, perfilUMotor(), puenteMotor(), cruce(), esquinero = esquinero,
            modulacion = textoModelo.takeIf { it.isNotBlank() && it != "nci" }
        )
    }

    /**
     * Las referencias, desde la misma ventana que calculó el motor (ReferenciasNova), más lo que la
     * medida dice de su curva: el arco equivalente, o cada arco de una S.
     */
    private fun referenciasDelMotor(v: crystal.crystal.taller.nova.motor.VentanaNova): String {
        // Una esquina leída del diseño trae los anchos útiles; se muestran los que se midieron.
        val paredes = v.lados.count { !it.curvaDeEsquina }
        val medidos = (listaVigente()?.paredes?.map { it.ancho } ?: esquinaDeLaMedida?.lados?.map { it.ancho })
            ?.takeIf { it.size == paredes && paredes > 1 }
        val base = crystal.crystal.taller.nova.motor.ReferenciasNova.texto(v, medidos)
        if (texto != "ncu") return base
        arcosDeLaMedida()?.let { return "$base\n${resumenDeVariosArcos(it)}" }
        val l = v.lados.first()
        return geometriaCurva(l.anchoCalculo, l.divisiones)?.let { "$base\n${resumenGeometriaCurva(it)}" } ?: base
    }

    /** La esquina de la medida, con la silueta de sus caras, leída por el lector único (N-81). */
    private fun ventanaMotorConSiluetas(): crystal.crystal.taller.nova.motor.VentanaNova? {
        val esquinero = opcionEsquinero()?.let { crystal.crystal.taller.nova.motor.Puente(it.text, it.valor, it.valorEsquina) }
        return crystal.crystal.taller.nova.motor.DesdeDiseno.ventana(
            disenoSimbolico(), perfilUMotor(), puenteMotor(), cruce(), esquinero = esquinero,
            modulacion = textoModelo.takeIf { it.isNotBlank() && it != "nci" }
        )
    }

    /** Reemplaza los materiales de la pantalla por los del motor, si está encendido. */
    /**
     * La ventana que calcula el motor, venga de donde venga: el diseño editado, la esquina con
     * alguna cara que no es rectángulo (su silueta, `L<…>`, hallado el 2026-09-29 en Formas 13), o
     * Nova con ceil.
     */
    private fun ventanaDelMotor(): crystal.crystal.taller.nova.motor.VentanaNova? = when {
        modulosDesiguales.isNotEmpty() -> ventanaMotorDesigual()
        texto in setOf("nl", "nu", "ns") && siluetasDeLaMedida.any { it != null } -> ventanaMotorConSiluetas()
        else -> ventanaMotorAutomatica()
    }

    /**
     * Materiales y referencias del motor único (fase 4, 2026-09-30). Ya no hay cálculo de respaldo:
     * si el motor no puede con un caso, la pantalla lo dice y no muestra materiales. Antes se
     * quedaba en silencio con lo de las rutas viejas, y la misma ventana podía salir con uno u otro.
     */
    private fun aplicarMotor() {
        val calculo = runCatching {
            val v = conSusCurvas(ventanaDelMotor() ?: error("no hay ventana que calcular"))
            v to crystal.crystal.taller.nova.motor.MotorNova.calcular(v)
        }
        val (v, m) = calculo.getOrElse { e ->
            aplicarMateriales(EstadoMateriales("", "", "", "", "", "", "", "", "", "",
                "El motor todavía no calcula este caso.\n(${e.message ?: e.javaClass.simpleName})"))
            binding.lyUf.visibility = View.GONE
            binding.lyFalsoPuente.visibility = View.GONE
            prepararDetalleMateriales(null)
            aplicarVistaReferencias()
            Toast.makeText(this, "Este caso todavía no lo calcula el motor: no hay materiales", Toast.LENGTH_LONG).show()
            return
        }
        val t = crystal.crystal.taller.nova.motor.MaterialesNova
        val estado = EstadoMateriales(
            u = t.texto(m.u), p = t.texto(m.puente), r = t.texto(m.riel), fc = t.texto(m.rielSuperior),
            pf = t.texto(m.portafelpa), te = t.texto(m.tee), tope = t.texto(m.tope), hache = t.texto(m.hache),
            tubo = t.texto(m.parante), vidrio = t.textoVidrios(m.vidrios),
            referencias = referenciasDelMotor(v)
        )
        aplicarMateriales(estado)
        aplicarVistaReferencias()
        when (m.nombreRielSuperior) {
            crystal.crystal.taller.nova.motor.RielSuperior.U_FELPERO -> binding.tvFc.setText(R.string.u_felpero)
            crystal.crystal.taller.nova.motor.RielSuperior.FIJO_CORREDIZO -> binding.tvFc.setText(R.string.fijo_corre)
            crystal.crystal.taller.nova.motor.RielSuperior.DOBLE_CORREDIZA -> binding.tvFc.text = m.nombreRielSuperior.etiqueta
        }
        // Diseño con tramos clásicos y tramos de solo corredizas: la doble corrediza de estos va en
        // su propia fila (lyUf, que ya no se usaba), porque la fila del riel superior la ocupa el
        // fijo corredizo o la U felpero de los otros. Se archiva igual que las demás filas.
        val doble = t.texto(m.dobleCorrediza)
        binding.tvUf.text = crystal.crystal.taller.nova.motor.RielSuperior.DOBLE_CORREDIZA.etiqueta
        binding.txUf.text = doble
        binding.lyUf.visibility = if (doble.isNotBlank()) View.VISIBLE else View.GONE
        val falso = t.texto(m.falsoPuente)
        binding.txFalsoPuente.text = falso
        binding.lyFalsoPuente.visibility = if (falso.isNotBlank()) View.VISIBLE else View.GONE
        prepararDetalleMateriales(m)
    }

    /**
     * Qué lados se curvan y con qué radio (N-102); no cambia ninguna medida.
     * - Nova curva: el radio de su curva. En una S, cada arco es su tramo y lleva el suyo, con el
     *   signo de su panza; si los tramos ya no son los de los arcos (se cambiaron las divisiones),
     *   el de la curva equivalente.
     * - L, C y serie: la curva con que se resolvió una esquina, con su desarrollo y su cuerda.
     */
    private fun conSusCurvas(v: crystal.crystal.taller.nova.motor.VentanaNova): crystal.crystal.taller.nova.motor.VentanaNova {
        if (texto == "ncu" && v.lados.size == 1) {
            val l = v.lados[0]
            val arcos = arcosDeLaMedida()
            val radios = if (arcos != null && arcos.size == l.tramos.size) {
                arcos.map { a ->
                    val r = crystal.crystal.taller.ArcoEsquina.deDesarrolloYFlecha(a.ancho, kotlin.math.abs(a.flecha))?.radio ?: 0f
                    if (a.flecha < 0f) -r else r
                }
            } else {
                val desarrollo = binding.etAncho.text?.toString()?.replace(",", ".")?.toFloatOrNull() ?: 0f
                // La panza, mirando desde adentro (como se mide): un arco de Medidas con la flecha
                // negativa se mete hacia quien mira, hacia adentro. Escrita a mano, hacia afuera.
                val haciaAdentro = esquinaDeLaMedida?.lados?.singleOrNull()?.let { it.esCurva && it.flecha < 0f } == true
                listOfNotNull(geometriaCurva(desarrollo, 1)?.takeIf { it.flecha > 0f }?.radio?.let { if (haciaAdentro) -it else it })
            }
            return v.copy(lados = listOf(l.copy(radios = radios)))
        }
        // La lista de lados (2026-10-03): el motor deja sus lados en el mismo orden, cada curva con
        // el radio de su desarrollo y su flecha; en menos, la panza hacia adentro.
        listaVigente()?.let { lista ->
            if (v.lados.size != lista.lados.size) return v
            return v.copy(lados = v.lados.mapIndexed { i, l ->
                val lado = lista.lados[i]
                if (!l.curvaDeEsquina || !lado.esCurvo) return@mapIndexed l
                val r = crystal.crystal.taller.ArcoEsquina.deDesarrolloYFlecha(lado.ancho, kotlin.math.abs(lado.flecha))?.radio
                    ?: return@mapIndexed l
                l.copy(radios = listOf(if (lado.flecha < 0f) -r else r))
            })
        }
        val medida = esquinaDeLaMedida ?: return v
        val curvas = medida.lados.indices.mapNotNull { medida.curvaDe(it) }
        if (curvas.isEmpty()) return v
        return v.copy(lados = v.lados.map { l ->
            if (!l.curvaDeEsquina) return@map l
            val c = curvas.firstOrNull { kotlin.math.abs(it.desarrollo - l.anchoMedido) < 0.05f } ?: return@map l
            val r = crystal.crystal.taller.ArcoEsquina.deDesarrolloYCuerda(c.desarrollo, c.cuerda)?.radio ?: return@map l
            l.copy(radios = listOf(r))
        })
    }

    /**
     * El detalle de cada material, que se ve con el ▼ de "Cálculos", y lo que se archiva. Por ahora
     * el detalle es el curvado (CurvadoNova, N-102): debajo de cada pieza curva, su cuerda, su flecha
     * y, en los perfiles, la ampliación para la roladora. Se archiva lo curvo con su ampliación y la
     * marca "curvo". Sin curva no hay detalle, el ▼ no aparece y se archiva lo de la pantalla.
     */
    private fun prepararDetalleMateriales(m: crystal.crystal.taller.nova.motor.MaterialesNova?) {
        ultimosMateriales = m
        val b = binding
        val curvado = crystal.crystal.taller.nova.motor.CurvadoNova
        val listas = if (m == null || !m.hayCurva) emptyList() else listOf(
            b.txU to m.u, b.txP to m.puente, b.txR to m.riel, b.txFc to m.rielSuperior,
            b.txUf to m.dobleCorrediza, b.txPf to m.portafelpa, b.txTe to m.tee, b.txTo to m.tope,
            b.txH to m.hache, b.txT to m.parante, b.txFalsoPuente to m.falsoPuente
        )
        val vidrios = if (m == null || !m.hayCurva) null else m.vidrios
        val detalles = listas.associate { (tv, piezas) -> tv to curvado.texto(piezas) } +
            listOfNotNull(vidrios?.let { b.txV to curvado.textoVidrios(it) })
        val archivo = listas.associate { (tv, piezas) -> tv to curvado.textoArchivo(piezas) } +
            listOfNotNull(vidrios?.let { b.txV to curvado.textoArchivoVidrios(it) })
        materialesCompactos = detalles.keys.associateWith { it.text.toString() }
        materialesDetalle = detalles
            .filter { (tv, detalle) -> detalle.isNotBlank() && detalle != materialesCompactos[tv] }
        materialesArchivo = archivo.filter { (tv, t) -> t != materialesCompactos[tv] }
        binding.tvDetalleMateriales.visibility = if (materialesDetalle.isEmpty()) View.GONE else View.VISIBLE
        mostrarMateriales()
    }

    private fun mostrarMateriales() {
        val desplegar = materialesDesplegados && materialesDetalle.isNotEmpty()
        binding.tvDetalleMateriales.text = if (desplegar) "▲" else "▼"
        for (tv in materialesCompactos.keys) {
            tv.text = materialesDetalle[tv]?.takeIf { desplegar } ?: materialesCompactos[tv]
        }
    }

    /**
     * Lo que se archiva o se devuelve, en pantalla mientras se lee: la lista corta, con lo curvo
     * ampliado y marcado (`118 = 1 curvo`). Después se vuelve a [mostrarMateriales].
     */
    private fun mostrarMaterialesParaArchivar() {
        // Los vidrios curvos, con las caras que se dijeron al archivar (cóncava o convexa).
        val vidrios = ultimosMateriales?.takeIf { it.hayCurva && metaCaras != null }
            ?.let { crystal.crystal.taller.nova.motor.CurvadoNova.textoArchivoVidrios(it.vidrios, metaCaras) }
        for (tv in materialesCompactos.keys) {
            tv.text = vidrios?.takeIf { tv === binding.txV } ?: materialesArchivo[tv] ?: materialesCompactos[tv]
        }
    }

    // ==================== CÁLCULO DESIGUAL ====================
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












    private fun aplicarMateriales(estado: EstadoMateriales) {
        binding.txU.text = estado.u
        // El nombre va con la U con que se calculó: estaba fijo en "u-13" y con una U de 1 la
        // lista (y lo archivado, que toma este nombre) seguía diciendo u-13.
        binding.tvU.text = nombreDeLaU()
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

        binding.btLadoRecto.setOnClickListener { listaLados.agregar() }
        binding.btLadoCurva.setOnClickListener { listaLados.agregar(curvo = true) }
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
            mostrarLadoActual()
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
                        mostrarLadoActual()
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
                        binding.tvLado2.visibility = View.VISIBLE
                        binding.etAncho.setText("")
                        binding.etAlto.setText("")
                        binding.etHoja.setText("")
                        binding.etPartes.setText("0")
                        contadorLado = 3
                        mostrarLadoActual()
                        binding.btAgregar.visibility = View.GONE
                        binding.btAgregar.isEnabled = false
                    }
                }
            } else if (texto == "ns") {
                ladosNs.add(medida)
                binding.etAncho.setText("")
                binding.etAlto.setText("")
                binding.etHoja.setText("")
                binding.etPartes.setText("0")
                contadorLado += 1
                mostrarLadoActual()
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
        // Desde dónde se mira la imagen de una ventana que dobla (2026-10-02): desde adentro, como
        // se mide; desde afuera si el cliente la quiere ver así.
        dlg.dgRgVista.check(if (vistaExterior) dlg.dgRbVistaAfuera.id else dlg.dgRbVistaAdentro.id)
        dlg.dgRgVista.setOnCheckedChangeListener { _, checkedId ->
            vistaExterior = checkedId == dlg.dgRbVistaAfuera.id
            if (ultimoPaquete.isNotBlank()) redibujarDiseno()
        }

        dlg.dgRgAcabado.setOnCheckedChangeListener { _, checkedId ->
            tipoNova = when (checkedId) {
                R.id.dgRbIna -> TipoNova.INA
                R.id.dgRbPiv -> TipoNova.PIV
                else -> TipoNova.APA
            }
            // Igual que al tocar el título: el diseño cargado lleva el acabado en su cabecera y de ahí
            // lo lee el motor. Desde aquí no se cambiaba y un vano con forma elegido en inaparente
            // se seguía calculando en aparente (hallado el 2026-09-29).
            if (ultimoPaquete.isNotBlank()) ultimoPaquete = conAcabado(ultimoPaquete, tipoPaquete())
            // El diseño y los materiales en el acabado nuevo (ver [alCambiarAcabado]).
            alCambiarAcabado()
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
            "nl", "nu", "ns" -> {
                // L, C y serie: los lados en lista, rectos o curvos, con su unión (2026-10-03). Antes
                // cada geometría tenía sus campos fijos y la curva iba en bloques aparte.
                activarLista(if (tipo == "nu") 3 else 2)
                otros = true
            }
            "ncu" -> {
                binding.lyFlecha.visibility = View.VISIBLE
                binding.lyCuerda.visibility = View.VISIBLE
                binding.btAgregar.visibility = View.GONE
                binding.btAgregar.isEnabled = false; otros = true
                mostrarLadoActual()
            }
            else -> {
                binding.btAgregar.visibility = View.GONE
                binding.btAgregar.isEnabled = false; otros = false
                mostrarLadoActual()
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
    /**
     * El cruce: 0.7 por defecto o el que escriba el usuario, el mismo en aparente e inaparente
     * (N-41, N-71, N-83). Antes inaparente pasaba por la tabla vieja de divisiones de
     * NovaInaCalculos, que daba lo mismo pero fallaba con el campo de divisiones vacío (C-15).
     */
    private fun cruce(): Float {
        val exacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
        return if (exacto == 0f) 0.7f else exacto
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




    /** El mismo paquete de diseño con otro acabado en la cabecera: `{nova,apa,[…` → `{nova,ina,[…`. */
    private fun conAcabado(paquete: String, acabado: String): String =
        Regex("""^(\s*\{\s*nova\s*,\s*)[a-zA-Z]+(\s*,)""").replace(paquete) { "${it.groupValues[1]}$acabado${it.groupValues[2]}" }

    private fun tipoPaquete(): String = when (tipoNova) {
        TipoNova.APA -> "apa"
        TipoNova.INA -> "ina"
        TipoNova.PIV -> "piv"
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


    /** Modulaciones cuyo ángulo tope no sale del orden clásico (ver NovaCalculos.anguloTopeCantidad). */
    private val modulacionesConTopePropio = setOf("ncfc", "nfc", "ncc", "n3c")


    private fun esModeloDoblePuente(): Boolean {
        return modeloRemate == "np"
    }

    private fun actualizarVisibilidadMochetaInferior() {
        // También cuando la bandera sale de la forma del vano (2026-10-02).
        val banderaDeLaForma = mochetaInfDelDiseno != null && contornoMedida.isNotBlank()
        binding.lyMochetaInf.visibility = if (esModeloDoblePuente() || banderaDeLaForma) View.VISIBLE else View.GONE
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
        // Invertido (nr) nunca admite Múltiple, en cualquier geometría/forma/acabado. Tampoco un
        // diseño editado con algún tramo invertido o en bandera (N-03): el modelo de la ventana es el
        // del primer tramo y no lo delataba.
        return esPuenteMultiple(textoPuente) &&
                (tipoNova == TipoNova.INA || esModeloDoblePuente() || modeloRemate == "nr" ||
                    disenoConMochetaAbajo())
    }

    /** ¿El diseño editado tiene algún tramo con mocheta debajo del sistema (invertido o bandera)? */
    private fun disenoConMochetaAbajo(): Boolean {
        if (modulosDesiguales.isEmpty() || ultimoPaquete.isBlank()) return false
        val d = runCatching { crystal.crystal.Diseno.nova.DisenoNova.desdePaquete(ultimoPaquete) }.getOrNull()
            ?: return false
        return d.tramos.any { t -> t.franjas.indexOfFirst { it.esSistema } > 0 }
    }

    private fun puentePredeterminadoSinMultiple(): String {
        // INA ncfc: por defecto paflon 1 (uno de los permitidos).
        if (esIncfc()) return "paflon 1"
        // Full corredizas: el predeterminado respeta las corredizas por tramo y no usa múltiple/gorrito.
        if (textoModelo == "nfc") {
            val maxCorr = maxCorredizasPorTramoNfc()
            if (maxCorr > 4) return "paflon 1"
        }
        // N-03: el predeterminado es tubo 2⅜ x 1 en todos los casos. Hasta el 2026-09-25 el doble
        // puente y las full corredizas de hasta 2 por tramo usaban tubo 2 x 1:
        //     return if (modeloRemate == "nr" || tipoNova == TipoNova.INA) puenteInaDefault else "tubo 2 x 1"
        return puentePorDefecto
    }

    private fun aplicarPuentePredeterminadoSinMultiple() {
        tubo = 2.5f
        puente = puentePredeterminadoSinMultiple()
        binding.tvP.text = puente
        escribirValorPuente(tubo)
        sincronizarSpinnerPuente(puente)
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
    /**
     * Vuelve a dibujar el diseño cargado ([ultimoPaquete]) sin abrir el editor. Se llama al cargarlo y
     * al cambiar el acabado: el dibujo de INA no es el de APA (los fijos van de piso a techo), y antes
     * el cambio solo tocaba la cabecera del paquete y el dibujo se quedaba en el acabado anterior
     * (2026-10-01, Fide en INA).
     */
    private fun redibujarDiseno() {
        if (ultimoPaquete.isBlank()) return
        val intentRender = Intent(this, DisenoNovaActivity::class.java).apply {
            putExtra(DisenoNovaActivity.EXTRA_PAQUETE, ultimoPaquete)
            putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
            putExtra(DisenoNovaActivity.EXTRA_VISTA_EXTERIOR, vistaExterior)
            putExtra(DisenoNovaActivity.EXTRA_ANCHO_PX, binding.ivDiseno.width)
            putExtra(DisenoNovaActivity.EXTRA_ALTO_PX, binding.ivDiseno.height)
            putExtra(DisenoNovaActivity.EXTRA_OUTPUT_FORMAT, if (texto == "nl" || texto == "nu" || texto == "ns" || texto == "ncu" || texto == "nci" || ultimoPaquete.contains("P<2.5>") || modulosDesiguales.isNotEmpty()) "png" else "svg")
            putExtra(DisenoNovaActivity.EXTRA_RET_PADDING_PX, 4)
            putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, mochetaLateralDisenoNl())
            putExtra(DisenoNovaActivity.EXTRA_ENCUENTRO_VACIO, metaEncuentro)
            putExtra(DisenoNovaActivity.EXTRA_DIRECCION, metaDireccion)
            putExtra(DisenoNovaActivity.EXTRA_US_CM, binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f)
        }
        lanzarDiseno.launch(intentRender)
    }

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
            // Las medidas con que queda el diseño: si luego se cambian, Calcular pregunta.
            medidasDelDiseno = medidasEnPantalla()
            // Un tramo invertido o en bandera no admite Múltiple (N-03): se pasa al preestablecido,
            // como cuando se elige el modelo con su botón.
            if (puenteMultipleNoPermitido(puente)) aplicarPuentePredeterminadoSinMultiple()
            binding.textView28.setText(R.string.referencias_y_c_lculos)
            val tipoMsg = if (modulosDesiguales.isNotEmpty()) " (desigual)" else ""
            Toast.makeText(this, "Diseño cargado: ${df1(ancho)} x ${df1(alto)}$tipoMsg", Toast.LENGTH_SHORT).show()

            redibujarDiseno()

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

    /**
     * El diseño ya no se suelta al escribir (pedido del usuario el 2026-09-26): cambiar la U de una
     * ventana con forma soltaba el diseño y el siguiente Calcular la volvía rectangular.
     *
     * - U y cruce son datos de material, no de forma: el diseño se conserva y el motor calcula con
     *   los nuevos.
     * - Ancho, alto, hoja y divisiones sí cambian la forma, pero no se suelta en silencio: al
     *   calcular, si ya no son los del diseño, se pregunta ([confirmarMedidasDelDiseno]).
     *
     * Antes el mismo watcher, en los seis campos, llamaba a [limpiarEstadoDesigual].
     */
    private fun configurarLimpiezaDesigual() {
        val watcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                // El reparto elegido a mano era para esa medida: con otra vuelve el automático.
                if (!cargandoDiseno) NovaCalculos.repartoManual = null
            }
        }
        binding.etPartes.addTextChangedListener(watcher)
        binding.etHoja.addTextChangedListener(watcher)
        binding.etAncho.addTextChangedListener(watcher)
        binding.etAlto.addTextChangedListener(watcher)
    }

    private val recalculoDeMaterial = Runnable {
        if (ultimoPaquete.isNotBlank() && !cargandoDiseno) binding.btCalcular.performClick()
    }

    /**
     * La U del marco y el cruce son datos de material: si ya se calculó, cambiarlos rehace el
     * cálculo solo, como el puente ([recalcularSiYaFueCalculado]). Antes se leían únicamente al
     * tocar Calcular y la lista de U se quedaba con la medida vieja (2026-10-01). Se espera a que
     * el vidriero termine de escribir: "1.5" pasa por "1." y no vale recalcular con cada tecla.
     */
    private fun configurarRecalculoDeMaterial() {
        // El cruce en 0 vale (es "el de siempre", [cruce]); una U de 0 no.
        fun watcher(admiteCero: Boolean) = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                binding.btCalcular.removeCallbacks(recalculoDeMaterial)
                val valor = s?.toString()?.trim()?.replace(",", ".")?.toFloatOrNull() ?: return
                if (valor < 0f || (valor == 0f && !admiteCero) || ultimoPaquete.isBlank()) return
                binding.btCalcular.postDelayed(recalculoDeMaterial, 700)
            }
        }
        binding.etU.addTextChangedListener(watcher(admiteCero = false))
        binding.etCruce.addTextChangedListener(watcher(admiteCero = true))
    }

    /** Las medidas con que llegó el diseño (ancho, alto, hoja, divisiones), para saber si se tocaron. */
    private var medidasDelDiseno: List<String>? = null

    private fun medidasEnPantalla(): List<String> =
        listOf(binding.etAncho, binding.etAlto, binding.etHoja, binding.etPartes).map { it.text?.toString()?.trim().orEmpty() }

    /** Vuelve a poner en los campos las medidas con que quedó el diseño, sin soltarlo. */
    private fun volverALasMedidasDelDiseno(delDiseno: List<String>) {
        cargandoDiseno = true
        try {
            listOf(binding.etAncho, binding.etAlto, binding.etHoja, binding.etPartes)
                .forEachIndexed { i, et -> et.setText(delDiseno[i]) }
        } finally {
            cargandoDiseno = false
        }
    }

    /**
     * Antes de calcular con un diseño cargado: si el ancho, el alto, la hoja o las divisiones ya no
     * son los del diseño, pregunta si se conserva el diseño (vuelven sus medidas) o se descarta y se
     * calcula el automático; en un vano con forma, si se abre novaDiseño. Devuelve true si se puede
     * seguir calculando ya.
     */
    private fun confirmarMedidasDelDiseno(): Boolean {
        val delDiseno = medidasDelDiseno ?: return true
        if (modulosDesiguales.isEmpty()) return true
        val ahora = medidasEnPantalla()
        val nombres = listOf("ancho", "alto", "hoja", "divisiones")
        val cambios = nombres.indices.filter { !mismaMedida(delDiseno[it], ahora[it]) }
            .map { "${nombres[it]}: ${delDiseno[it].ifBlank { "vacío" }} → ${ahora[it].ifBlank { "vacío" }}" }
        if (cambios.isEmpty()) return true
        // El ancho o el alto de un vano con lados desiguales (más de 2.5, N-06: solo entonces viaja el
        // contorno de Medidas) no se editan en Nova: se cambian en novaDiseño, como los anchos
        // desiguales o el reparto entre tramos (dictado el 2026-10-01). La hoja y las divisiones sí se
        // cambian aquí y el diseño ya se rehízo desde la forma ([rehacerDesdeLaFormaSiCambio]). Antes
        // la única salida era "descartar", que calculaba la ventana automática, y la automática no
        // conoce el contorno: salía rectangular (Fide). Un vano dentro del descuadre es rectangular y
        // se edita normal.
        val anchoOAltoCambiado = !mismaMedida(delDiseno[0], ahora[0]) || !mismaMedida(delDiseno[1], ahora[1])
        if (contornoMedida.isNotBlank() && anchoOAltoCambiado) {
            AlertDialog.Builder(this)
                .setTitle("El vano tiene forma")
                .setMessage(cambios.joinToString("\n") +
                    "\n\nSus lados son desiguales: el ancho y el alto no se editan aquí, se cambian en novaDiseño.")
                .setPositiveButton("Abrir novaDiseño") { _, _ ->
                    volverALasMedidasDelDiseno(delDiseno)
                    abrirDisenoInteractivo()
                }
                .setNegativeButton("Conservar el diseño") { _, _ ->
                    volverALasMedidasDelDiseno(delDiseno)
                    binding.btCalcular.performClick()
                }
                .setNeutralButton("Cancelar", null)
                .show()
            return false
        }
        AlertDialog.Builder(this)
            .setTitle("Las medidas ya no son las del diseño")
            .setMessage(cambios.joinToString("\n") +
                "\n\nSi descarta el diseño, se calcula la ventana automática (rectangular).")
            .setPositiveButton("Conservar el diseño") { _, _ ->
                cargandoDiseno = true
                try {
                    listOf(binding.etAncho, binding.etAlto, binding.etHoja, binding.etPartes)
                        .forEachIndexed { i, et -> et.setText(delDiseno[i]) }
                } finally {
                    cargandoDiseno = false
                }
                binding.btCalcular.performClick()
            }
            .setNegativeButton("Descartar y calcular automático") { _, _ ->
                limpiarEstadoDesigual()
                medidasDelDiseno = null
                binding.btCalcular.performClick()
            }
            .setNeutralButton("Cancelar", null)
            .show()
        return false
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

    /**
     * El diseño que se archiva: el editado a mano si lo hay (N-72, N-89), y si no el automático.
     * Antes siempre se regeneraba el automático desde los campos, y lo editado —anchos desiguales,
     * una bandera en un solo tramo, un tramo sin puente— se perdía al archivar.
     */
    private fun disenoParaArchivar(): String =
        if (modulosDesiguales.isNotEmpty() && ultimoPaquete.isNotBlank()) ultimoPaquete else disenoSimbolico()

    /** El diseño editado que manda (desigual, compuesto, formas), o null si la ventana es automática. */
    private fun disenoCargado(): crystal.crystal.Diseno.nova.DisenoNova? {
        if (modulosDesiguales.isEmpty() || ultimoPaquete.isBlank()) return null
        return runCatching { crystal.crystal.Diseno.nova.DisenoNova.desdePaquete(ultimoPaquete) }.getOrNull()
    }

    /**
     * El remate de la ventana según su diseño (primero se identifica la ventana entera, dictado el
     * 2026-09-26): "polis" tiene sus dos tramos invertidos y es "nr" aunque la pantalla diga normal.
     * Null si no hay diseño, o si sus tramos no son todos iguales (compuesto).
     */
    private fun remateDelDiseno(): String? {
        val d = disenoCargado() ?: return null
        return when (crystal.crystal.Diseno.nova.HerramientasDiseno.modeloDeLaVentana(d)) {
            crystal.crystal.Diseno.nova.HerramientasDiseno.ModeloTramo.NORMAL,
            crystal.crystal.Diseno.nova.HerramientasDiseno.ModeloTramo.SIN_PUENTE -> "nn"
            crystal.crystal.Diseno.nova.HerramientasDiseno.ModeloTramo.INVERTIDO -> "nr"
            crystal.crystal.Diseno.nova.HerramientasDiseno.ModeloTramo.BANDERA -> "np"
            else -> null
        }
    }

    /** ¿Los tramos del diseño editado tienen franjas distintas? Entonces es compuesto (N-89). */
    private fun esDisenoCompuesto(): Boolean {
        if (modulosDesiguales.isEmpty() || ultimoPaquete.isBlank()) return false
        val d = crystal.crystal.Diseno.nova.DisenoNova.desdePaquete(ultimoPaquete) ?: return false
        return d.tramos.map { t -> t.franjas.map { it.esSistema } }.distinct().size > 1
    }

    /**
     * La ventana del motor con la que se está escribiendo el diseño (ver [disenoSimbolico]): de ahí
     * salen también los tramos de la curva de una esquina ([tramoDeCurva]).
     */
    private var ventanaEscrita: crystal.crystal.taller.nova.motor.VentanaNova? = null

    /**
     * El diseño simbólico (el paquete de novaDiseño, del dibujo y del archivo). Desde la fase 4 del
     * motor único (2026-09-30) el reparto —tramos, hojas, alturas— sale de la MISMA ventana que
     * calcula el motor (EscritorNova); antes lo decidía NovaUIHelper.generarTramosConsolidado por su
     * cuenta. Los adornos de la medida (esquina, panza, círculo, silueta) se ponen aquí como siempre.
     */
    private fun disenoSimbolico(): String {
        val escritor = crystal.crystal.taller.nova.motor.EscritorNova
        val vMotor = ventanaMotorAutomatica().also { ventanaEscrita = it }
        // L, C y serie: desde la lista de lados, por un solo camino (2026-10-03).
        listaVigente()?.let { return disenoDeLaLista(vMotor, it) }
        val paredes = vMotor.lados.filter { !it.curvaDeEsquina }
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
                val tramosBase = escritor.tramos(vMotor, paredes[0], consolidado = true)
                val tramosAleta = escritor.tramos(vMotor, paredes[1], consolidado = true)
                val cabecera = "${NovaCalculos.df1(primeraC.ancho)},${NovaCalculos.df1(primeraC.alto)}:"
                val esquina = esquinaEnElDiseno(0, primeraC.alto, primeraC.hoja)
                val base = conPanzaDelLado(0, tramosBase)
                val aleta = conPanzaDelLado(1, tramosAleta)
                val final = curvaFinalEnElDiseno(1, aletaC.alto, aletaC.hoja)
                return conLoQueTrajoLaMedida("{nova,${tipoPaquete()},[$cabecera$base $esquina $aleta$final]}")
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
                val tramosIzq = escritor.tramos(vMotor, paredes[0], consolidado = true)
                val tramosCentro = escritor.tramos(vMotor, paredes[1], consolidado = true)
                val tramosDer = escritor.tramos(vMotor, paredes[2], consolidado = true)
                val cabecera = "${NovaCalculos.df1(centroC.ancho)},${NovaCalculos.df1(centroC.alto)}:"
                val esq1 = esquinaEnElDiseno(0, izqC.alto, izqC.hoja)
                val izqP = conPanzaDelLado(0, tramosIzq)
                val centroP = conPanzaDelLado(1, tramosCentro)
                val derP = conPanzaDelLado(2, tramosDer)
                val esq2 = esquinaEnElDiseno(1, centroC.alto, centroC.hoja)
                val final = curvaFinalEnElDiseno(2, derC.alto, derC.hoja)
                return conLoQueTrajoLaMedida("{nova,${tipoPaquete()},[$cabecera$izqP $esq1 $centroP $esq2 $derP$final]}")
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
                val tramosBase = escritor.tramos(vMotor, paredes[0], consolidado = true)
                val partes = mutableListOf(conPanzaDelLado(0, tramosBase))
                for (i in 1 until lados.size) {
                    val lado = lados[i]
                    val divisLado = NovaCalculos.divisiones(lado.ancho, lado.divisManual, "nn")
                    val altoHojaLado = NovaCalculos.altoHoja(lado.alto, lado.hoja)
                    val tramosLado = paredes.getOrNull(i)?.let { escritor.tramos(vMotor, it, consolidado = true) } ?: continue
                    partes.add(esquinaEnElDiseno(i - 1, lados[i - 1].alto, lados[i - 1].hoja))
                    partes.add(conPanzaDelLado(i, tramosLado))
                }
                val cabecera = "${NovaCalculos.df1(base.ancho)},${NovaCalculos.df1(base.alto)}:"
                return conLoQueTrajoLaMedida("{nova,${tipoPaquete()},[${cabecera}A<90> ${partes.joinToString(" ")}]}")
            }
        }
        if (texto == "ncu") {
            // Varios arcos medidos (una S): cada uno su tramo con su panza. Ver disenoDeVariosArcos.
            arcosDeLaMedida()?.let { return disenoDeVariosArcos(it, vMotor) }
            // Arco de la curva (ver arcoCurvo). El tag U<...> aplica la flecha al dibujo.
            return escritor.paquetePlano(vMotor, tipoPaquete(), consolidado = true, tag = "U<${df1(flechaCurvaDesdeCampos())}>")
        }
        if (texto == "nci") {
            return escritor.paquetePlano(vMotor, tipoPaquete(), consolidado = true, tag = "O<1>")
        }

        // Patrón = textoModelo (modulación); remate = modeloRemate. Ejes independientes.
        return escritor.paquetePlano(vMotor, tipoPaquete())
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
        // Con un diseño cargado, la forma sale de él: un vano con silueta, escalón o inclinado es
        // poligonal (N-01b). "polis" salía `r` (rectangular) con su viga y su recorte.
        val formaDelDiseno = disenoCargado()?.takeIf { it.esIrregular }?.let { "p" }
        val forma = formaDelDiseno ?: when (metaForma) {
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
        // El "modelo" del paquete refleja el REMATE (eje independiente de la modulación). Un diseño
        // con tramos de modelos distintos es compuesto (N-89).
        // Con un diseño cargado, el modelo sale de sus tramos ("polis": invertido, `i`; salía `n`).
        val modelo = if (esDisenoCompuesto()) "x" else when (remateDelDiseno() ?: modeloRemate) {
            "nn" -> "n"
            "nr" -> "i"
            "np" -> "b"
            else -> "x"
        }
        val disenoTecnico = try { disenoParaArchivar() } catch (_: Exception) { "" }
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
        // L, C y serie: las paredes de la lista de lados (2026-10-03); las curvas van en el diseño.
        listaVigente()?.let { lista ->
            return lista.paredes.map { MedidaNl(it.ancho, it.alto, it.puente, it.divisiones) }
        }
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
        esquinaEscrita = false
        curvasCargadas.fill(null)
        desactivarLista()
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
        binding.tvLado2.visibility = View.GONE
        binding.tvEsquinaCurva.visibility = View.GONE
        binding.lyDesarrollo.visibility = View.GONE
        binding.etDesarrollo.setText("")
        binding.lyAltoCurva.visibility = View.GONE
        binding.lyPuenteCurva.visibility = View.GONE
        binding.etAltoCurva.setText("")
        binding.etPuenteCurva.setText("")
        binding.lyDiviCurva.visibility = View.GONE
        binding.etDiviCurva.setText("0")
        binding.tvEsquinaCurvaE2.visibility = View.GONE
        listOf(binding.lyFlechaE2, binding.lyCuerdaE2, binding.lyDesarrolloE2, binding.lyAltoCurvaE2, binding.lyPuenteCurvaE2, binding.lyDiviCurvaE2)
            .forEach { it.visibility = View.GONE }
        listOf(binding.etFlechaE2, binding.etCuerdaE2, binding.etDesarrolloE2, binding.etAltoCurvaE2, binding.etPuenteCurvaE2)
            .forEach { it.setText("") }
        binding.etDiviCurvaE2.setText("0")
    }

    /**
     * Al pasar de aparente a inaparente o al revés (2026-10-02: el diseño no cambiaba): un diseño
     * cargado se dibuja de nuevo en el acabado nuevo, y si ya se había calculado se vuelve a calcular
     * solo, como al cambiar el puente o la U, para que materiales y dibujo sean del acabado nuevo.
     */
    private fun alCambiarAcabado() {
        if (modulosDesiguales.isNotEmpty()) redibujarDiseno()
        if (ultimoPaquete.isNotBlank()) binding.btCalcular.post { binding.btCalcular.performClick() }
    }

    /** La imagen de una ventana que dobla se ve desde afuera (por defecto, desde adentro). */
    private var vistaExterior = false

    /** La esquina de [esquinaDeLaMedida] la armaron las casillas de esquina curva, no Medidas. */
    private var esquinaEscrita = false

    /** Las casillas de una esquina curva: la 1 (entre los lados 1 y 2) y, en C, la 2 (2026-10-03). */
    private class CasillasCurva(
        val flecha: EditText, val cuerda: EditText, val desarrollo: EditText,
        val alto: EditText, val puente: EditText, val divisiones: EditText
    ) {
        /** Las medidas del arco y del paño; las divisiones no, que no cambian el arco. */
        fun textos(): List<String> = listOf(flecha, cuerda, desarrollo, alto, puente).map { it.text?.toString()?.trim().orEmpty() }
    }

    private val casillasCurva by lazy {
        listOf(
            CasillasCurva(binding.etFlecha, binding.etCuerda, binding.etDesarrollo, binding.etAltoCurva, binding.etPuenteCurva, binding.etDiviCurva),
            CasillasCurva(binding.etFlechaE2, binding.etCuerdaE2, binding.etDesarrolloE2, binding.etAltoCurvaE2, binding.etPuenteCurvaE2, binding.etDiviCurvaE2)
        )
    }

    /**
     * La lista de lados de la L, la C o la serie, con [paredes] lados rectos para empezar; el primero
     * con lo que hubiera escrito arriba. Las casillas de arriba (ancho, alto, puente, divisiones) se
     * esconden: son las de la ventana plana, y al calcular repiten el último lado de la lista.
     */
    private fun activarLista(paredes: Int) {
        fun num(et: EditText) = et.text?.toString()?.trim()?.replace(",", ".")?.toFloatOrNull()
        val ancho = num(binding.etAncho)?.takeIf { it > 0f }
        val alto = num(binding.etAlto)?.takeIf { it > 0f }
        listaLados.limpiar()
        repeat(paredes) { i ->
            val primero = if (i == 0 && ancho != null && alto != null)
                crystal.crystal.taller.LadoDeVentana(ancho, alto, num(binding.etHoja) ?: 0f) else null
            listaLados.agregar(primero)
        }
        filasDeArriba(View.GONE)
        binding.lyListaLados.visibility = View.VISIBLE
        binding.lyBotonesLados.visibility = View.VISIBLE
        binding.btAgregar.visibility = View.GONE
        binding.btAgregar.isEnabled = false
        binding.tvLadoActual.visibility = View.GONE
    }

    private fun desactivarLista() {
        listaActual = null
        binding.lyListaLados.visibility = View.GONE
        binding.lyBotonesLados.visibility = View.GONE
        filasDeArriba(View.VISIBLE)
    }

    private fun filasDeArriba(visibilidad: Int) {
        binding.ancho.visibility = visibilidad
        binding.alto.visibility = visibilidad
        (binding.etHoja.parent as? View)?.visibility = visibilidad
        (binding.etPartes.parent as? View)?.visibility = visibilidad
    }

    /**
     * Lee la lista antes de calcular: la deja en [listaActual], nombra la geometría por sus paredes
     * (2 en L, 3 en C, más en serie) y repite el último lado recto en las casillas de arriba, que
     * es donde las buscan el resto de la pantalla y el archivado. False si falta algo (lo avisa).
     */
    private fun prepararLista(): Boolean {
        val lectura = listaLados.leer()
        val lista = lectura.medida ?: run {
            Toast.makeText(this, lectura.error ?: "Revise los lados", Toast.LENGTH_LONG).show()
            return false
        }
        val paredes = lista.paredes
        if (paredes.isEmpty()) {
            Toast.makeText(this, "Una ventana toda curva va en la geometría Curvo", Toast.LENGTH_LONG).show()
            return false
        }
        listaActual = lista
        texto = when (paredes.size) { 3 -> "nu"; 1, 2 -> "nl"; else -> "ns" }
        nombreGeometria = when (texto) { "nl" -> "En L"; "nu" -> "En C"; else -> "Serie" }
        diseno = when (texto) { "nl" -> "noval"; "nu" -> "novau"; else -> "novas" }
        actualizarNombreModelo()
        val ultimo = paredes.last()
        binding.etAncho.setText(df1(ultimo.ancho))
        binding.etAlto.setText(df1(ultimo.alto))
        binding.etHoja.setText(df1(ultimo.puente))
        binding.etPartes.setText(ultimo.divisiones.toString())
        return true
    }

    /**
     * El diseño desde la lista de lados (2026-10-03), por un solo camino para la L, la C y la serie.
     * Cada lado es su paño con los tramos que calculó el motor; la curva lleva su panza (`Q<>`).
     * Entre dos paredes, su esquina `A<grados>`. Una curva tangente se pega a la pared de antes sin
     * pliegue, y una que no sale tangente lleva el suyo. Detrás de una curva el pliegue es la esquina
     * entera desde antes de la curva (así lo lee la planta): lo que giró la unión de entrada, el arco
     * y la unión de salida.
     */
    private fun disenoDeLaLista(v: crystal.crystal.taller.nova.motor.VentanaNova, lista: crystal.crystal.taller.VentanaMedida): String {
        val pliegues = lista.plieguesDelDiseno()
        val partes = mutableListOf<String>()
        lista.lados.forEachIndexed { i, lado ->
            val l = v.lados.getOrNull(i) ?: return@forEachIndexed
            var tramos = crystal.crystal.taller.nova.motor.EscritorNova.tramos(v, l, consolidado = true)
            if (lado.esCurvo) tramos = conTagEnElSistema(tramos, "Q<${df1(lado.flecha)}>")
            pliegues[i]?.let { partes.add("A<${df1(it)}>") }
            partes.add(tramos)
        }
        val primera = lista.paredes.first()
        val cabecera = "${df1(primera.ancho)},${df1(primera.alto)}:"
        return conLoQueTrajoLaMedida("{nova,${tipoPaquete()},[$cabecera${partes.joinToString(" ")}]}")
    }

    /** Los bloques de esquina curva: uno en L; en C, uno por esquina, cada uno con su número. */
    @SuppressLint("SetTextI18n")
    private fun mostrarBloquesDeEsquina(cuantos: Int) {
        listOf(binding.tvEsquinaCurva, binding.lyFlecha, binding.lyCuerda, binding.lyDesarrollo,
            binding.lyAltoCurva, binding.lyPuenteCurva, binding.lyDiviCurva).forEach { it.visibility = View.VISIBLE }
        // El segundo bloque: en la C, su segunda esquina; en la L, la curva con la que termina la
        // ventana después del lado 2 (2026-10-03).
        listOf(binding.tvEsquinaCurvaE2, binding.lyFlechaE2, binding.lyCuerdaE2, binding.lyDesarrolloE2,
            binding.lyAltoCurvaE2, binding.lyPuenteCurvaE2, binding.lyDiviCurvaE2).forEach { it.visibility = View.VISIBLE }
        binding.tvEsquinaCurva.text = if (cuantos >= 2) "Esquina curva 1" else "Esquina curva"
        binding.tvEsquinaCurvaE2.text = if (cuantos >= 2) "Esquina curva 2" else "Curva final"
    }

    /** Por esquina: lo que mostraban sus casillas al cargarla de Medidas, y la curva tal cual vino. */
    private val curvasCargadas = arrayOfNulls<Pair<List<String>, CurvaEsquina>>(2)

    /** Los lados ya agregados de la L o la C, antes del que está en los campos; null si faltan. */
    private fun ladosAgregados(): List<MedidaNl>? = when (texto) {
        "nl" -> listOfNotNull(obtenerPrimeraMedidaNlEditable()).takeIf { it.size == 1 }
        "nu" -> listOfNotNull(obtenerPrimeraMedidaNuEditable(), obtenerSegundaMedidaNuEditable()).takeIf { it.size == 2 }
        else -> null
    }

    /**
     * Las esquinas de la L y la C escritas a mano (2026-10-02; la C el 2026-10-03): con las casillas
     * de una esquina vacías, en ángulo; con algún valor, curva. Se arma como la esquina curva que
     * llega de Medidas (una arista `c<desarrollo>|<cuerda>|<alto>|<puente>` en [esquinaDeLaMedida]),
     * y desde ahí el motor la calcula como un lado más (N-84) y el diseño la dibuja con su arco: es
     * el mismo camino. Si la esquina vino de Medidas, sus casillas ya la muestran (ver
     * [mostrarCurvaDeMedidas]) y lo que digan al calcular manda: se cambian solo las esquinas y los
     * lados de Medidas se quedan.
     */
    private fun aplicarEsquinaEscrita() {
        val previos = ladosAgregados() ?: return
        fun num(et: EditText) = et.text?.toString()?.trim()?.replace(",", ".")?.toFloatOrNull() ?: 0f
        // Vacías, el alto y el puente del lado de antes de la esquina.
        val curvas = previos.mapIndexed { i, l -> curvaDeLaEsquina(i, l.alto, l.hoja) }
        // En la L el segundo bloque es la curva con la que termina la ventana, después del lado 2
        // (2026-10-03); vacía, el alto y el puente del lado 2. En la C su curva del final no tiene
        // casillas todavía: la que traiga Medidas se queda.
        val final = if (texto == "nl") curvaDeLaEsquina(1, num(binding.etAlto), num(binding.etHoja)) else null
        val deMedidas = esquinaDeLaMedida?.takeIf { !esquinaEscrita && it.lados.size == previos.size + 1 }
        if (deMedidas != null) {
            // Vacías, en ángulo: el que trajo Medidas si era un ángulo, en escuadra si era la curva.
            val angulos = curvas.mapIndexed { i, curva ->
                curva?.let { EsquinaMedida.textoDeCurva(it) }
                    ?: deMedidas.angulos.getOrNull(i)?.takeIf { deMedidas.curvaDe(i) == null } ?: "90"
            }
            val despues = if (texto == "nl") listOfNotNull(final?.let { EsquinaMedida.textoDeCurva(it) })
            else deMedidas.angulos.drop(angulos.size)
            esquinaDeLaMedida = deMedidas.copy(angulos = angulos + despues)
            return
        }
        if (curvas.all { it == null } && final == null) {
            if (esquinaEscrita) {
                esquinaDeLaMedida = null
                esquinaEscrita = false
            }
            return
        }
        fun lado(ancho: Float, alto: Float, puente: Float) = crystal.crystal.taller.LadoEsquina(ancho, ancho, alto, alto, puente)
        esquinaDeLaMedida = EsquinaMedida(
            previos.map { lado(it.ancho, it.alto, it.hoja) } + lado(num(binding.etAncho), num(binding.etAlto), num(binding.etHoja)),
            curvas.map { c -> c?.let { EsquinaMedida.textoDeCurva(it) } ?: "90" } + listOfNotNull(final?.let { EsquinaMedida.textoDeCurva(it) })
        )
        esquinaEscrita = true
    }

    /**
     * La curva de la esquina [i] según sus casillas. Sin tocar desde que las llenó Medidas: la curva
     * tal cual vino, sin el redondeo a un decimal de lo que se muestra.
     */
    private fun curvaDeLaEsquina(i: Int, alto: Float, puente: Float): CurvaEsquina? {
        val casillas = casillasCurva[i]
        val escrita = curvaEscrita(casillas, alto, puente) ?: return null
        val cargada = curvasCargadas[i]?.takeIf { it.first == casillas.textos() }?.second ?: return escrita
        return cargada.copy(
            alto = if (cargada.alto > 1f) cargada.alto else escrita.alto,
            puente = if (cargada.puente > 0.5f) cargada.puente else escrita.puente,
            divisiones = escrita.divisiones
        )
    }

    /**
     * La curva de unas casillas de esquina curva; null si están vacías. Con dos de las tres medidas
     * (flecha, cuerda, desarrollo) el arco queda dicho. Con una sola, la curva dobla lo que dobla la
     * esquina, 90°: un cuarto de círculo.
     */
    private fun curvaEscrita(casillas: CasillasCurva, alto: Float, puente: Float): CurvaEsquina? {
        fun valor(et: EditText) = et.text?.toString()?.trim()?.replace(",", ".")?.toFloatOrNull()?.takeIf { it > 0f }
        val f = valor(casillas.flecha)
        val c = valor(casillas.cuerda)
        val d = valor(casillas.desarrollo)
        val a = crystal.crystal.taller.ArcoEsquina
        val arco = when {
            d != null && c != null -> a.deDesarrolloYCuerda(d, c)
            d != null && f != null -> a.deDesarrolloYFlecha(d, f)
            c != null && f != null -> a.deCuerdaYFlecha(c, f)
            else -> null
        }
        val (desarrollo, cuerda) = arco?.let { it.desarrollo to it.cuerda } ?: run {
            val raiz2 = kotlin.math.sqrt(2.0).toFloat()
            val radio = when {
                d != null -> d / (Math.PI / 2).toFloat()
                c != null -> c / raiz2
                f != null -> f / (1f - kotlin.math.cos(Math.PI / 4).toFloat())
                else -> return null
            }
            (radio * (Math.PI / 2).toFloat()) to (radio * raiz2)
        }
        // El paño curvo con su propio alto y puente; vacíos, los del lado de antes. Sus divisiones, 0 = la regla.
        val divisiones = casillas.divisiones.text?.toString()?.trim()?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        return CurvaEsquina(desarrollo, cuerda, valor(casillas.alto) ?: alto, valor(casillas.puente) ?: puente, divisiones)
    }

    /**
     * Las esquinas de la L o la C que trae Medidas, en sus casillas (2026-10-02; la C el 2026-10-03):
     * flecha, cuerda, desarrollo, y el alto, el puente y las divisiones del paño curvo. Antes la curva
     * entraba al cálculo sin verse en Nova. Una esquina en ángulo deja sus casillas vacías.
     */
    private fun mostrarCurvaDeMedidas(medida: EsquinaMedida) {
        casillasCurva.forEachIndexed { i, casillas ->
            val c = medida.curvaDe(i)
            val arco = c?.let { crystal.crystal.taller.ArcoEsquina.deDesarrolloYCuerda(it.desarrollo, it.cuerda) }
            casillas.flecha.setText(arco?.let { df1(it.flecha) }.orEmpty())
            casillas.cuerda.setText(c?.let { df1(it.cuerda) }.orEmpty())
            casillas.desarrollo.setText(c?.let { df1(it.desarrollo) }.orEmpty())
            casillas.alto.setText(c?.alto?.takeIf { it > 1f }?.let { df1(it) }.orEmpty())
            casillas.puente.setText(c?.puente?.takeIf { it > 0.5f }?.let { df1(it) }.orEmpty())
            casillas.divisiones.setText((c?.divisiones ?: 0).toString())
            curvasCargadas[i] = c?.let { casillas.textos() to it }
        }
    }

    /**
     * El lado que se está escribiendo en L, C y serie, encima de sus casillas (2026-10-02). Antes iba
     * en el título fijo ("Medidas y Cantidad\nLado 2") y al desplazar quedaba junto a otro lado; y
     * "Otros datos\nLado 1" junto al selector de diseño no decía nada.
     */
    @SuppressLint("SetTextI18n")
    private fun mostrarLadoActual() {
        binding.tvMedidas.setText(R.string.medidas_y_cantidad)
        val compuesta = texto == "nl" || texto == "nu" || texto == "ns"
        binding.tvLadoActual.text = "Lado $contadorLado"
        binding.tvLadoActual.visibility = if (compuesta) View.VISIBLE else View.GONE
    }

    private fun mochetaLateralDisenoNl(): Float {
        if (texto != "nl") return 0f
        if (modoLista) return listaActual?.paredes?.takeIf { it.size == 2 }?.last()?.ancho ?: 0f
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
        val medida = esquinaDeLaMedida ?: return paquete
        // Con una esquina curva el paño del arco es un lado más en el diseño y los lados del apunte
        // ya no van uno a uno con los tramos: no se toca nada.
        if (medida.hayCurva) return paquete
        // Si en la lista se agregaron o quitaron lados, los del apunte ya no son estos.
        listaVigente()?.let { if (it.hayCurva || it.paredes.size != medida.lados.size) return paquete }
        // Los altos de cada pared, tal como se midieron: la pared más baja cuelga más abajo del
        // dintel, y el 3D la sube hasta su alto. Sin esto las dos paredes salían igual de altas.
        val altos = medida.lados.map { it.alto }
        val distintos = altos.any { kotlin.math.abs(it - altos.first()) > 0.05f }
        val hayQuePoner = distintos || siluetasDeLaMedida.any { it != null } || parantesDeLaMedida.any { it.isNotEmpty() }
        if (!hayQuePoner) return paquete
        val diseno = runCatching { crystal.crystal.Diseno.nova.DisenoNova.desdePaquete(paquete) }.getOrNull()
            ?: return paquete
        return conCarasDeSuSilueta(diseno.conAltosDeLado(if (distintos) altos else emptyList()), medida)
            .conSiluetasYParantesPorLado(siluetasDeLaMedida, parantesDeLaMedida)
            .aPaquete()
    }

    /**
     * Cada cara de la esquina que no es rectángulo toma el diseño que sugiere su contorno, con las
     * reglas del vano con forma (N-101: cada escalón un tramo, la corrediza donde entra entera, el
     * tramo inclinado como su rectángulo contenedor), ajustado al ancho de la cara (en aparente, el
     * útil). Hallado el 2026-09-30: la cara conservaba el reparto de siempre (sistema a 5/7, hojas
     * iguales) y en la L de Ele la hoja de 116 no cabía bajo el dintel que a la izquierda deja 109.1.
     * La misma pared sola ya salía bien por este camino.
     */
    private fun conCarasDeSuSilueta(
        d: crystal.crystal.Diseno.nova.DisenoNova, medida: crystal.crystal.taller.EsquinaMedida
    ): crystal.crystal.Diseno.nova.DisenoNova {
        if (siluetasDeLaMedida.none { it != null }) return d
        val lados = d.separarEnLados().toMutableList()
        if (lados.size != siluetasDeLaMedida.size) return d
        var cambio = false
        siluetasDeLaMedida.forEachIndexed { i, silueta ->
            if (silueta == null || silueta.size < 3) return@forEachIndexed
            val lado = lados[i]
            val medidoLado = medida.lados.getOrNull(i) ?: return@forEachIndexed
            // Solo una silueta que cierra con las medidas de su cara (con el descuadre de margen): una
            // de 197.7 de alto en una pared de 162 no describe esa pared.
            val anchoS = silueta.maxOf { it.first } - silueta.minOf { it.first }
            val altoS = silueta.maxOf { it.second } - silueta.minOf { it.second }
            if (kotlin.math.abs(altoS - medidoLado.alto) > 2.5f || kotlin.math.abs(anchoS - medidoLado.ancho) > 2.5f) return@forEachIndexed
            val hoja = medidoLado.puente
            // Lo que la cara cuelga del dintel de la ventana (una pared más baja, conAltosDeLado).
            val base = lado.tramos.firstOrNull() ?: return@forEachIndexed
            val caidaLado = base.caida
            val altoLado = if (base.alto > 0f) base.alto else 0f
            val sugerido = runCatching {
                crystal.crystal.Diseno.nova.ContornoEnTramos.disenoDesdeContorno(silueta, d.acabado, hoja)
            }.getOrNull() ?: return@forEachIndexed
            val suma = sugerido.tramos.sumOf { it.ancho.toDouble() }.toFloat()
            val parantes = 2.5f * (sugerido.tramos.size - 1)
            if (suma <= 0f) return@forEachIndexed
            val k = ((lado.ancho - parantes) / suma).coerceAtLeast(0f)
            lados[i] = lado.copy(tramos = sugerido.tramos.map { t ->
                t.copy(
                    ancho = t.ancho * k,
                    franjas = t.franjas.map { f -> f.copy(modulos = f.modulos.map { m -> m.copy(ancho = m.ancho?.times(k)) }) },
                    // El alto y la caída del contorno son de la cara; la cara puede colgar del dintel.
                    caida = t.caida + caidaLado,
                    alto = if (t.alto > 0f) t.alto else altoLado
                )
            })
            cambio = true
        }
        return if (cambio) d.conLados(lados, d.esquinaDeCadaLado()) else d
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

        // L, C y serie: la lista vuelve a empezar, vacía, con las paredes de la que se archivó.
        if (!modoLista) return
        activarLista(if (listaActual?.paredes?.size == 3) 3 else 2)
        listaActual = null
        materialesNlArchivables = null
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
        // Un círculo (o una elipse) de Medidas entra como forma circular (sección 11).
        if (actual != null && aplicarFormaDeMedida(actual.forma)) return
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
        // El alto del puente elegido: el diálogo del puente en un escalón lo usa (dentro / fuera).
        intent.putExtra(DisenoNovaActivity.EXTRA_PUENTE_CM, tubo)
        intent.putExtra(DisenoNovaActivity.EXTRA_PUENTE_NOMBRE, puente)
        // La medida original, para verla desde el editor con todas sus cotas.
        if (bocetoOriginalRuta.isNotBlank()) intent.putExtra(DisenoNovaActivity.EXTRA_BOCETO_ORIGINAL, bocetoOriginalRuta)
        lanzarDisenoInteractivo.launch(intent)
    }

    /** Muestra la medida original en grande, con zoom (la misma ventana que usa novaDiseño). */
    private fun mostrarDialogoBocetoOriginal() {
        crystal.crystal.taller.DialogoMedidaOriginal.mostrar(this, bocetoOriginalDrawable)
    }

    private fun cargarYMostrarBocetoOriginal(path: String) {
        // La ruta viaja a novaDiseño, que también la muestra (su chip "medida original").
        bocetoOriginalRuta = path
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
        // La geometría de la medida anterior (curva, L, C o serie) no vale para esta: si la nueva
        // es plana se quedaba en la curva, con su flecha y su reparto (Pipa, 2026-10-01). Se vuelve
        // a plano; si la nueva es otra esquina, cargarEsquinaDeMedida la pone de nuevo.
        if (texto in textosGeometria) {
            seleccionarDesdePanel(R.drawable.vplano, "nn")
            binding.etFlecha.setText("")
            binding.etCuerda.setText("")
        }
        NovaCalculos.repartoManual = null
        binding.etAncho.setText(df1(item.ancho))
        binding.etAlto.setText(df1(item.alto))
        cargarYMostrarBocetoOriginal(item.bocetoArchivo)
        binding.etAncho.requestFocus()
        // Una ventana de esquina trae sus lados: se arma su geometría en vez de un vano plano.
        if (cargarEsquinaDeMedida(item.esquina, item.contornosLados, item.parantesLados)) return
        if (aplicarFormaDeMedida(item.forma)) return
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
            // La esquina del botón v.esquina no trae siluetas: una cara asimétrica (Formas 13, 120
            // abajo y 189 arriba) se saca de sus propias medidas, o llegaba como su rectángulo mayor.
            .takeIf { s -> s.any { it != null } } ?: medida.siluetasAsimetricas()
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
            // Cada arco gira con el signo de su panza: en una S los dos giros se restan y la
            // curva equivalente es casi recta, que es lo que es para los materiales (se cortan
            // por el desarrollo). El dibujo con sus dos panzas sale aparte, arco por arco.
            val giroTotal = kotlin.math.abs(medida.lados.sumOf { lado ->
                val signo = if (lado.flecha < 0f) -1.0 else 1.0
                (crystal.crystal.taller.ArcoEsquina
                    .deDesarrolloYFlecha(lado.ancho, kotlin.math.abs(lado.flecha))?.anguloGrados ?: 0f).toDouble() * signo
            })
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
        // L, C y serie: la lista con los lados de la medida, la curva como un lado más (2026-10-03).
        // Antes la curva del principio llegaba en el sitio de la pared de cero y Nova la tomaba por
        // el lado 1, con las casillas de la curva vacías.
        listaLados.cargar(crystal.crystal.taller.VentanaMedida.desdeEsquina(medida))
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
    /**
     * La curva con la que termina la ventana, detrás del último lado (2026-10-03): su paño con su
     * panza, sin pliegue detrás porque no viene otra pared. Vacío si no hay.
     */
    private fun curvaFinalEnElDiseno(arista: Int, altoRef: Float, puenteRef: Float): String {
        val medida = esquinaDeLaMedida ?: return ""
        val curva = medida.curvaDe(arista) ?: return ""
        val orden = (0 until arista).count { k -> medida.curvaDe(k) != null }
        return " " + tramoDeCurva(curva, orden, altoRef, puenteRef)
    }

    private fun esquinaEnElDiseno(arista: Int, altoRef: Float, puenteRef: Float): String {
        val medida = esquinaDeLaMedida ?: return "A<90>"
        // La curva es un paño más Y un pliegue: la pared entra en ella de frente y sale girada,
        // así que detrás de su paño va el `A<>` que pone en perspectiva a la pared siguiente. Sin
        // él la L salía como tres paños en fila, de frente, y no se leía la esquina.
        medida.curvaDe(arista)?.let {
            // Lo que gira lo dice el propio arco: es el ángulo entre sus dos puntas.
            val grados = crystal.crystal.taller.ArcoEsquina
                .deDesarrolloYCuerda(it.desarrollo, it.cuerda)?.anguloGrados ?: 90f
            // La curva de esta esquina es la que va en su orden entre las curvas: dos curvas del mismo
            // desarrollo (una C) no se confunden.
            val orden = (0 until arista).count { k -> medida.curvaDe(k) != null }
            return tramoDeCurva(it, orden, altoRef, puenteRef) + " A<${df1(grados)}>"
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
        return if (flecha != 0f) conTagEnElSistema(tramos, "Q<${df1(flecha)}>") else tramos
    }

    /**
     * Los arcos de la medida cuando la ventana curva trae más de uno (una S, dos panzas seguidas);
     * null si la curva es de un arco o no vino del apunte.
     */
    private fun arcosDeLaMedida(): List<crystal.crystal.taller.LadoEsquina>? =
        esquinaDeLaMedida?.lados?.takeIf { l -> l.size >= 2 && l.all { it.esCurva } }

    /**
     * El diseño de una ventana curva de varios arcos: cada arco es un tramo con su desarrollo y
     * su panza con signo (`Q<-30>` se mete hacia quien mira), sin pliegue entre ellos porque salen
     * tangentes. Los módulos se reparten entre los arcos a lo que le toca a cada uno por su
     * desarrollo, con la unión de los arcos como parante; los materiales siguen saliendo del
     * desarrollo entero, que es lo que se corta.
     */
    private fun disenoDeVariosArcos(
        arcos: List<crystal.crystal.taller.LadoEsquina>,
        v: crystal.crystal.taller.nova.motor.VentanaNova
    ): String {
        // Los tramos son los del motor (el reparto que sembraron los arcos, con sus parantes); cada
        // uno lleva la panza de su arco. Antes los tramos del dibujo medían lo que cada arco y los
        // materiales repartían módulos iguales: 139.1 + 139.1 dibujado, 165.4 + 110.2 calculado.
        val l = v.lados.first()
        val partes = crystal.crystal.taller.nova.motor.EscritorNova.tramosPorSeparado(v, l).mapIndexed { i, tramo ->
            val flecha = arcos.getOrNull(i)?.flecha ?: 0f
            if (flecha != 0f) conTagEnElSistema(tramo, "Q<${df1(flecha)}>") else tramo
        }
        return "{nova,${tipoPaquete()},[${df1(l.anchoMedido)},${df1(l.altoMedido)}:${partes.joinToString(" P<2.5> ")}]}"
    }

    /** Las medidas de cada arco de la curva de varios arcos, para las referencias. */
    private fun resumenDeVariosArcos(arcos: List<crystal.crystal.taller.LadoEsquina>): String =
        arcos.mapIndexed { i, arco ->
            val a = crystal.crystal.taller.ArcoEsquina.deDesarrolloYFlecha(arco.ancho, kotlin.math.abs(arco.flecha))
            val lado = if (arco.flecha < 0f) "hacia adentro" else "hacia afuera"
            "Arco ${i + 1} -> Desarrollo: ${df1(arco.ancho)}; Cuerda: ${df1(a?.cuerda ?: arco.ancho)}; Flecha: ${df1(kotlin.math.abs(arco.flecha))} ($lado)"
        }.joinToString("\n")

    private fun tramoDeCurva(curva: CurvaEsquina, orden: Int, altoRef: Float, puenteRef: Float): String {

        val alto = if (curva.alto > 1f) curva.alto else altoRef
        val puente = if (curva.puente > 0.5f) curva.puente else puenteRef
        val flecha = crystal.crystal.taller.ArcoEsquina
            .deDesarrolloYCuerda(curva.desarrollo, curva.cuerda)?.flecha ?: 0f
        // Sus tramos son los del lado curvo que calculó el motor (la curva es un lado más, N-84).
        val v = ventanaEscrita ?: error("sin ventana del motor para la curva")
        val ladoCurvo = v.lados.filter { it.curvaDeEsquina }.getOrNull(orden)
            ?.takeIf { kotlin.math.abs(it.anchoMedido - curva.desarrollo) < 0.05f }
            ?: error("el motor no tiene la curva de ${curva.desarrollo}")
        val tramos = crystal.crystal.taller.nova.motor.EscritorNova.tramos(v, ladoCurvo, consolidado = true)
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

    /**
     * La forma que trae la medida de Medidas: un círculo o una elipse pone la forma circular, como
     * elegirla en el diálogo; cualquier otra medida la quita. Devuelve true si quedó circular.
     */
    private fun aplicarFormaDeMedida(forma: String): Boolean {
        if (forma == "circular") {
            seleccionarModelo("circular", R.drawable.novaci, "nci")
            Toast.makeText(this, "Vano circular: revisa el gráfico y calcula", Toast.LENGTH_SHORT).show()
            return true
        }
        salirDeCircular()
        return false
    }

    @JvmOverloads
    private fun cargarDisenoDelContorno(contorno: String, divisiones: Int = 0, mochetaInferior: Float = 0f): Boolean {

        // Cada medida trae el suyo: el de la anterior no vale para esta.
        contornoMedida = ""
        mochetaInfDelDiseno = null
        actualizarVisibilidadMochetaInferior()
        val puntos = ContornoEnTramos.desdeTexto(contorno)
        if (puntos.size < 3) return false
        val acabado = when (tipoNova) {
            TipoNova.APA -> "apa"
            TipoNova.PIV -> "piv"
            else -> "ina"
        }
        val hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        // La mocheta inferior escrita es la de la regla de bandera (N-34, sin el puente); la franja
        // del diseño lleva el puente dentro.
        val franjaInferior = if (mochetaInferior > 0f) mochetaInferior + alturaPuenteBandera() else 0f
        val diseno = runCatching {
            ContornoEnTramos.disenoDesdeContorno(puntos, acabado, hoja, divisiones = divisiones, mochetaInferior = franjaInferior)
        }.getOrNull() ?: return false
        if (!diseno.esIrregular) return false
        cargarDesdePaqueteDiseno(diseno.aPaquete())
        contornoMedida = contorno
        hojaDelDiseno = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        medidaDelDiseno = diseno.ancho to diseno.alto
        // Si la forma quedó en bandera, su mocheta inferior va a la casilla, que aparece (dictado el
        // 2026-10-02: así se tiene más control). Cambiarla rehace el diseño ([rehacerDesdeLaFormaSiCambio]).
        diseno.tramos.firstOrNull { t -> t.franjas.map { it.esSistema } == listOf(false, true, false) }?.let { t ->
            cargandoDiseno = true
            try {
                binding.etMochetaInf.setText(df1((t.franjas[0].alto - alturaPuenteBandera()).coerceAtLeast(0f)))
            } finally {
                cargandoDiseno = false
            }
            mochetaInfDelDiseno = binding.etMochetaInf.text?.toString()?.trim().orEmpty()
        }
        actualizarVisibilidadMochetaInferior()
        return true
    }

    /** La mocheta inferior de la bandera que salió de la forma del vano, como quedó en la casilla; null si no hay. */
    private var mochetaInfDelDiseno: String? = null

    /** El puente de una bandera: no admite Múltiple y lleva tubo (N-03); con otro puente, el suyo. */
    private fun alturaPuenteBandera(): Float = if (esPuenteMultiple()) 2.5f else puenteMotor().altura

    /**
     * Rehace el diseño del vano con forma cuando el vidriero escribe otro alto de hoja u otra
     * cantidad de divisiones: las dos se cambian en Nova (dictado el 2026-10-01). Lo demás —el ancho
     * o el alto de un vano con lados desiguales, anchos desiguales, el reparto entre varios tramos—
     * se hace en novaDiseño ([confirmarMedidasDelDiseno]).
     *
     * En un vano con forma la hoja no es un dato más: es la que dice DÓNDE cabe la corrediza —el
     * rectángulo del medio de un triángulo, lo que queda bajo un dintel caído— y con ella cambia el
     * reparto entero. Las divisiones, con hojas iguales (N-16), mandan donde la forma da un solo grupo
     * de hojas; en un vano de varios tramos se quedan las de la regla de los 60 y se avisa.
     *
     * Solo se rehace si el ancho y el alto siguen siendo los del diseño. Antes solo miraba la hoja,
     * contra un valor guardado aparte, y cambiar las divisiones acababa en "descartar": rectangular.
     */
    private fun rehacerDesdeLaFormaSiCambio() {
        if (contornoMedida.isBlank()) return
        val delDiseno = medidasDelDiseno ?: return
        val ahora = medidasEnPantalla()
        if (!mismaMedida(delDiseno[0], ahora[0]) || !mismaMedida(delDiseno[1], ahora[1])) return
        // La mocheta inferior de una bandera que salió de la forma también se cambia aquí (2026-10-02).
        val infDelDiseno = mochetaInfDelDiseno
        val infAhora = binding.etMochetaInf.text?.toString()?.trim().orEmpty()
        val infCambiada = infDelDiseno != null && !mismaMedida(infDelDiseno, infAhora)
        if (mismaMedida(delDiseno[2], ahora[2]) && mismaMedida(delDiseno[3], ahora[3]) && !infCambiada) return
        val pedidas = ahora[3].toIntOrNull() ?: 0
        val inferior = if (infDelDiseno != null) infAhora.replace(",", ".").toFloatOrNull() ?: 0f else 0f
        val contorno = contornoMedida
        if (!cargarDisenoDelContorno(contorno, pedidas, inferior)) {
            contornoMedida = contorno
            Toast.makeText(this, "No se pudo rehacer el diseño desde la forma del vano", Toast.LENGTH_LONG).show()
            return
        }
        // Si la pedida sacaba al sistema del rectángulo donde cabe, quedó en el límite: se dice.
        val quedo = mochetaInfDelDiseno
        if (infCambiada && inferior > 0f && quedo != null && !mismaMedida(quedo, df1(inferior))) {
            Toast.makeText(
                this,
                "La mocheta inferior queda en $quedo: con ${df1(inferior)} el sistema no cabe entero en su rectángulo",
                Toast.LENGTH_LONG
            ).show()
        }
        val quedaron = modulosDesiguales.size
        if (pedidas > 0 && quedaron != pedidas) {
            Toast.makeText(
                this,
                "Vano de varios tramos: el reparto entre tramos se hace en novaDiseño. Quedaron $quedaron (regla de los 60)",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /** Dos medidas de los campos son la misma (a la décima), o el mismo texto si no son números. */
    private fun mismaMedida(a: String, b: String): Boolean {
        val x = a.replace(",", ".").toFloatOrNull()
        val y = b.replace(",", ".").toFloatOrNull()
        return if (x != null && y != null) kotlin.math.abs(x - y) <= 0.05f else a == b
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

        // Vidrio curvo: ¿tiene caras? (dictado el 2026-10-01). Un reflejante tiene espejo por una
        // cara y color por la otra: al curvarlo, una queda cóncava y la otra convexa, y en una S cambia
        // de un arco al otro. Sin caras el vidrio se voltea y no pasa nada. Con caras se pregunta cuál
        // se ve desde adentro, y con eso cada vidrio se archiva con su cara cóncava o convexa.
        val hayVidrioCurvo = ultimosMateriales?.vidrios?.any { it.curva.isNotEmpty() } == true
        val anteriores = metaCaras
        val cbCaras = android.widget.CheckBox(this).apply {
            text = "El vidrio curvo tiene caras (reflejante, espejo, low-e…)"
            isChecked = anteriores?.tiene == true
        }
        val etCara = EditText(this).apply {
            hint = "Cara especial (ej: espejo)"
            setText(anteriores?.nombre?.takeIf { it.isNotBlank() } ?: "espejo")
        }
        val tvDesdeAdentro = TextView(this).apply { text = "Desde adentro se ve:" }
        val rgLado = android.widget.RadioGroup(this)
        val rbEspecial = android.widget.RadioButton(this).apply { id = View.generateViewId(); text = "la cara especial" }
        val rbOtra = android.widget.RadioButton(this).apply { id = View.generateViewId(); text = "la otra cara" }
        rgLado.addView(rbEspecial)
        rgLado.addView(rbOtra)
        rgLado.check(if (anteriores?.tiene == true && !anteriores.haciaAdentro) rbOtra.id else rbEspecial.id)
        fun mostrarCaras() {
            val v = if (cbCaras.isChecked) View.VISIBLE else View.GONE
            etCara.visibility = v; tvDesdeAdentro.visibility = v; rgLado.visibility = v
        }
        if (hayVidrioCurvo) {
            contenedor.addView(cbCaras)
            contenedor.addView(etCara)
            contenedor.addView(tvDesdeAdentro)
            contenedor.addView(rgLado)
            cbCaras.setOnCheckedChangeListener { _, _ -> mostrarCaras() }
            mostrarCaras()
        }

        AlertDialog.Builder(this)
            .setTitle("Metadatos para Diseño Simbólico V2")
            .setView(android.widget.ScrollView(this).apply { addView(contenedor) })
            .setPositiveButton("Guardar y archivar") { _, _ ->
                metaColorAluminio = etColor.text?.toString()?.trim().orEmpty()
                metaTipoVidrio = etVidrio.text?.toString()?.trim().orEmpty()
                metaAcabadoSuperficial = etAcabadoSup.text?.toString()?.trim().orEmpty()
                metaObservaciones = etObs.text?.toString()?.trim().orEmpty()
                metaCaras = when {
                    !hayVidrioCurvo -> null
                    !cbCaras.isChecked -> crystal.crystal.taller.nova.motor.CurvadoNova.SIN_CARAS
                    else -> crystal.crystal.taller.nova.motor.CurvadoNova.Caras(
                        nombre = etCara.text?.toString()?.trim().orEmpty().ifBlank { "especial" },
                        haciaAdentro = rgLado.checkedRadioButtonId == rbEspecial.id
                    )
                }
                onContinuar()
            }
            .setNeutralButton("Omitir") { _, _ ->
                metaCaras = null
                onContinuar()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun devolverResultadoMasivo() {
        mostrarMaterialesParaArchivar()
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
        val paqueteDiseno = try { disenoParaArchivar() } catch (_: Exception) { "" }
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
        // Se archiva la lista corta, con lo curvo ampliado y marcado (N-102), aunque se vea el detalle.
        mostrarMaterialesParaArchivar()

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
            // Doble corrediza aparte (diseño con tramos clásicos y de solo corredizas; ver aplicarMotor).
            if (esValido(binding.lyUf) && binding.txUf.text.isNotBlank()) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvUf, binding.txUf, mapListas, identificadorPaquete)
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
                val paquete = disenoParaArchivar()
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
        mostrarMateriales()

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


