package crystal.crystal.taller

import android.content.Intent
import android.content.res.ColorStateList
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.Button
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import crystal.crystal.clientes.Cliente
import crystal.crystal.databinding.ActivityMedidaBinding
import crystal.crystal.medicion.EstadoMedicion
import crystal.crystal.medicion.ItemMedicionObra
import crystal.crystal.medicion.MedicionViewModel
import crystal.crystal.medicion.UnidadMedida
import crystal.crystal.taller.nova.NovaCorrediza
import java.util.Locale
import java.util.UUID
import java.io.File
import kotlin.math.acos
import kotlin.math.abs
import android.graphics.Color

class MedidaActivity : AppCompatActivity() {
    companion object {
        private const val SISTEMA_NOVA = "nova"
        private const val APERTURA_CORREDIZA = "corrediza"
        private const val ALTURA_MURO_BASE_CM = 240f
        private const val COLOR_OBLIGATORIO_PENDIENTE = "#C62828"
        private const val COLOR_OBLIGATORIO_LISTO = "#2E7D32"
        private const val COLOR_NO_OBLIGATORIO = "#EF6C00"
    }

    private lateinit var binding: ActivityMedidaBinding
    private val medicionViewModel: MedicionViewModel by viewModels()

    private val itemsArchivados = mutableListOf<ItemMedicionObra>()
    private var itemIdEnEdicion: String? = null
    private val colaCalculoMasivo = ArrayDeque<ItemMedicionObra>()
    private var procesamientoMasivoActivo = false
    private var omitirAutoBorradorHastaMs: Long = 0L

    private var clienteSeleccionado: Cliente? = null
    private var categoria = ""
    private var sistema = ""
    private var tipoApertura = ""
    private var geometria = ""
    private var encuentros = ""
    private var modelo = ""
    private var acabado = ""
    private var especificaciones: Map<String, String> = emptyMap()
    private var accesorios: List<String> = emptyList()

    private var anchoProductoCm = 0f
    private var altoProductoCm = 0f
    private var alturaPuenteCm = 0f
    private var unidadMedida = UnidadMedida.CM
    private var actualizandoPanelMedidas = false
    private var alturaMuroActualCm = ALTURA_MURO_BASE_CM
    private val acopleLadosSeleccionados = linkedSetOf<RectanglesDrawingView.Side>()
    private var acopleProductoSeleccionado = RectanglesDrawingView.ProductType.PUERTA
    private var acopleAnchoBaseCm = 60f
    private var acopleAltoBaseCm = 100f
    private var esquineroLado1Cm = 30f
    private var esquineroLado2Cm = 40f
    private var esquineroHipotenusaCm = 50f
    private var esquineroUsar345 = true
    private var esquineroAletaAnchoCm = 40f
    private var esquineroAletaAltoCm = 90f
    private var esquineroAletaDirection = RectanglesDrawingView.AletaDirection.INTERIOR
    private var esquineroAletaSide = RectanglesDrawingView.AletaSide.RIGHT
    private var cTopRightLado1Cm = 30f
    private var cTopRightLado2Cm = 40f
    private var cTopRightHipotenusaCm = 50f
    private var cTopRightAletaAnchoCm = 40f
    private var cTopRightAletaAltoCm = 90f
    private var cTopLeftLado1Cm = 30f
    private var cTopLeftLado2Cm = 40f
    private var cTopLeftHipotenusaCm = 50f
    private var cTopLeftAletaAnchoCm = 40f
    private var cTopLeftAletaAltoCm = 90f
    private var curvoDesarrolloCm = 130f
    private var curvoCuerdaCm = 120f
    private var curvoFlechaCm: Float? = null
    private var curvoDirection = RectanglesDrawingView.AletaDirection.INTERIOR
    private var multiLadoCount = 2
    private var multiLadoSide = RectanglesDrawingView.AletaSide.RIGHT
    private var multiLadoDirection = RectanglesDrawingView.AletaDirection.INTERIOR
    private var multiLadoChain = true
    private data class MultiLadoAletaEstado(
        val side: RectanglesDrawingView.AletaSide,
        val direction: RectanglesDrawingView.AletaDirection,
        val lado1Cm: Float,
        val lado2Cm: Float,
        val hipotenusaCm: Float,
        val aletaAnchoCm: Float,
        val aletaAltoCm: Float
    )
    private val multiLadoAletas = mutableListOf<MultiLadoAletaEstado>()
    private val historialEstados = ArrayDeque<EstadoLienzoTemporal>()
    private var restaurandoEstadoTemporal = false

    private data class EstadoLienzoTemporal(
        val clienteNombre: String,
        val clienteDocumento: String,
        val clienteTelefono: String,
        val clienteDireccion: String,
        val producto: RectanglesDrawingView.ProductType,
        val geometria: String,
        val encuentro: String,
        val superior: String,
        val derecha: String,
        val inferior: String,
        val izquierda: String,
        val distPiso: String,
        val distTecho: String,
        val acopleSides: Set<RectanglesDrawingView.Side>,
        val acopleProducto: RectanglesDrawingView.ProductType,
        val acopleAncho: Float,
        val acopleAlto: Float,
        val esquineroLado1: Float,
        val esquineroLado2: Float,
        val esquineroHipotenusa: Float,
        val esquineroUsar345: Boolean,
        val esquineroAletaAncho: Float,
        val esquineroAletaAlto: Float,
        val esquineroAletaDirection: RectanglesDrawingView.AletaDirection,
        val esquineroAletaSide: RectanglesDrawingView.AletaSide,
        val cTopRightLado1: Float,
        val cTopRightLado2: Float,
        val cTopRightHipotenusa: Float,
        val cTopRightAletaAncho: Float,
        val cTopRightAletaAlto: Float,
        val cTopLeftLado1: Float,
        val cTopLeftLado2: Float,
        val cTopLeftHipotenusa: Float,
        val cTopLeftAletaAncho: Float,
        val cTopLeftAletaAlto: Float,
        val curvoDesarrollo: Float,
        val curvoCuerda: Float,
        val curvoFlecha: Float?,
        val curvoDirection: RectanglesDrawingView.AletaDirection,
        val multiLadoCount: Int,
        val multiLadoSide: RectanglesDrawingView.AletaSide,
        val multiLadoDirection: RectanglesDrawingView.AletaDirection,
        val multiLadoChain: Boolean,
        val multiLadoAletas: List<MultiLadoAletaEstado>
    )

    private data class ClienteLoteArchivo(
        val cliente: String,
        val productos: MutableList<ItemMedicionObra> = mutableListOf(),
        val actualizadoEn: Long = System.currentTimeMillis()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMedidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarModoLienzoTemporal()
        observarViewModel()
    }

    private fun configurarModoLienzoTemporal() {
        if (multiLadoAletas.isEmpty()) {
            multiLadoAletas.add(
                MultiLadoAletaEstado(
                    side = multiLadoSide,
                    direction = multiLadoDirection,
                    lado1Cm = esquineroLado1Cm,
                    lado2Cm = esquineroLado2Cm,
                    hipotenusaCm = esquineroHipotenusaCm,
                    aletaAnchoCm = esquineroAletaAnchoCm,
                    aletaAltoCm = esquineroAletaAltoCm
                )
            )
        }
        binding.lyVista.visibility = View.GONE
        binding.lyLienzoRectangulos.visibility = View.VISIBLE
        ocultarPanelesTemporales()
        inicializarCamposMedidasSiVacios()
        if (geometria.isBlank()) geometria = "plano"
        binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.PLANO)
        binding.rectanglesDrawingView.setEsquineroByHypotenuse345(esquineroHipotenusaCm)
        binding.rectanglesDrawingView.setEsquineroAletaSize(esquineroAletaAnchoCm, esquineroAletaAltoCm)
        binding.rectanglesDrawingView.setEsquineroAletaDirection(esquineroAletaDirection)
        binding.rectanglesDrawingView.setEsquineroAletaSide(esquineroAletaSide)
        binding.rectanglesDrawingView.setEsquineroVariant(esquineroVariantDesdeGeometria())
        binding.rectanglesDrawingView.setCTopAletaConfig(
            RectanglesDrawingView.AletaSide.RIGHT,
            RectanglesDrawingView.CTopAletaConfig(
                lado1Cm = cTopRightLado1Cm,
                lado2Cm = cTopRightLado2Cm,
                hipotenusaCm = cTopRightHipotenusaCm,
                anguloDeg = 0f,
                aletaAnchoCm = cTopRightAletaAnchoCm,
                aletaAltoCm = cTopRightAletaAltoCm
            )
        )
        binding.rectanglesDrawingView.setCTopAletaConfig(
            RectanglesDrawingView.AletaSide.LEFT,
            RectanglesDrawingView.CTopAletaConfig(
                lado1Cm = cTopLeftLado1Cm,
                lado2Cm = cTopLeftLado2Cm,
                hipotenusaCm = cTopLeftHipotenusaCm,
                anguloDeg = 0f,
                aletaAnchoCm = cTopLeftAletaAnchoCm,
                aletaAltoCm = cTopLeftAletaAltoCm
            )
        )
        binding.rectanglesDrawingView.setMultiLadoConfig(
            RectanglesDrawingView.MultiLadoConfig(
                count = multiLadoCount,
                side = multiLadoSide,
                direction = multiLadoDirection,
                chainSameDirection = multiLadoChain
            )
        )
        binding.rectanglesDrawingView.setMultiLadoAletas(
            multiLadoAletas.map {
                RectanglesDrawingView.MultiLadoAletaConfig(
                    side = it.side,
                    direction = it.direction,
                    lado1Cm = it.lado1Cm,
                    lado2Cm = it.lado2Cm,
                    hipotenusaCm = it.hipotenusaCm,
                    aletaAnchoCm = it.aletaAnchoCm,
                    aletaAltoCm = it.aletaAltoCm
                )
            }
        )
        binding.rectanglesDrawingView.setCurvoConfig(
            RectanglesDrawingView.CurvoConfig(
                desarrolloCm = curvoDesarrolloCm,
                cuerdaCm = curvoCuerdaCm,
                flechaCm = curvoFlechaCm,
                direction = curvoDirection,
                flechaEfectivaCm = 0f
            )
        )
        actualizarClienteHeaderDesdePanel()
        actualizarRectanguloDesdePanelMedidas()

        binding.btnControlCliente.setOnClickListener {
            alternarPanel(binding.panelClienteMedida)
        }
        binding.btnControlProductos.setOnClickListener {
            alternarPanel(binding.panelProductosMedida)
        }
        binding.btnControlDatosTecnicos.setOnClickListener {
            alternarPanel(binding.panelDatosTecnicosMedida)
        }
        binding.btnControlEncuentros.setOnClickListener {
            alternarPanel(binding.panelEncuentrosMedida)
        }
        binding.btnControlGeometria.setOnClickListener {
            alternarPanel(binding.panelGeometriaMedida)
        }
        binding.btnControlMedidas.setOnClickListener {
            alternarPanel(binding.panelMedidasPanel)
        }
        binding.btnSistemaPanel.setOnClickListener {
            mostrarDialogoSeleccionSimple(
                titulo = "Sistema / línea",
                opciones = arrayOf("nova", "serie 20", "plafones", "otros"),
                actual = sistema
            ) { seleccionado ->
                sistema = seleccionado
                actualizarResumenDatosTecnicos()
                actualizarClienteHeaderDesdePanel()
            }
        }
        binding.btnTipoPanel.setOnClickListener {
            mostrarDialogoSeleccionSimple(
                titulo = "Tipo / tipología",
                opciones = arrayOf("corrediza", "batiente", "pivotante", "fija"),
                actual = tipoApertura
            ) { seleccionado ->
                tipoApertura = seleccionado
                actualizarResumenDatosTecnicos()
                actualizarClienteHeaderDesdePanel()
            }
        }
        binding.btnModeloPanel.setOnClickListener {
            mostrarDialogoSeleccionSimple(
                titulo = "Modelo / modulación",
                opciones = arrayOf("normal", "invertido", "bandera", "compuesto"),
                actual = modelo
            ) { seleccionado ->
                modelo = seleccionado
                actualizarResumenDatosTecnicos()
                actualizarClienteHeaderDesdePanel()
            }
        }
        binding.btnAcabadoPanel.setOnClickListener {
            mostrarDialogoSeleccionSimple(
                titulo = "Acabado",
                opciones = arrayOf("aparente", "inaparente"),
                actual = acabado
            ) { seleccionado ->
                acabado = seleccionado
                actualizarResumenDatosTecnicos()
                actualizarClienteHeaderDesdePanel()
            }
        }
        binding.btnEspecificacionesPanel.setOnClickListener {
            mostrarDialogoEspecificaciones()
        }
        binding.btnAccesoriosPanel.setOnClickListener {
            mostrarDialogoAccesorios()
        }
        binding.btnArchivarMedida.setOnClickListener {
            intentarArchivarMedida()
        }
        binding.btnLimpiarLienzo.setOnClickListener {
            registrarEstadoParaDeshacer()
            ocultarPanelesTemporales()
            binding.rectanglesDrawingView.clearRectangles()
            inicializarCamposMedidasSiVacios()
            actualizarRectanguloDesdePanelMedidas()
        }
        binding.btnDeshacerAccion.setOnClickListener {
            deshacerUltimaAccion()
        }
        binding.tvClienteHeader.setOnClickListener {
            ocultarPanelesTemporales()
        }
        binding.lyLienzoRectangulos.setOnClickListener {
            ocultarPanelesTemporales()
        }

        val watcherCliente = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (restaurandoEstadoTemporal) return
                registrarEstadoParaDeshacer()
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                if (restaurandoEstadoTemporal) return
                actualizarClienteHeaderDesdePanel()
            }
        }

        binding.etClienteNombrePanel.addTextChangedListener(watcherCliente)
        binding.etClienteDocumentoPanel.addTextChangedListener(watcherCliente)
        binding.etClienteTelefonoPanel.addTextChangedListener(watcherCliente)
        binding.etClienteDireccionPanel.addTextChangedListener(watcherCliente)
        configurarCapturaUndoPorFoco(
            binding.etClienteNombrePanel,
            binding.etClienteDocumentoPanel,
            binding.etClienteTelefonoPanel,
            binding.etClienteDireccionPanel
        )

        binding.cbProductoVentanaPanel.setOnCheckedChangeListener { _, isChecked ->
            if (restaurandoEstadoTemporal) return@setOnCheckedChangeListener
            if (!isChecked) {
                actualizarClienteHeaderDesdePanel()
                return@setOnCheckedChangeListener
            }
            registrarEstadoParaDeshacer()
            binding.cbProductoPuertaPanel.isChecked = false
            binding.cbProductoMamparaPanel.isChecked = false
            binding.etDistPisoPanel.setText("90")
            binding.etDistTechoPanel.setText("0")
            actualizarClienteHeaderDesdePanel()
            actualizarRectanguloDesdePanelMedidas()
        }
        binding.cbProductoPuertaPanel.setOnCheckedChangeListener { _, isChecked ->
            if (restaurandoEstadoTemporal) return@setOnCheckedChangeListener
            if (!isChecked) {
                actualizarClienteHeaderDesdePanel()
                return@setOnCheckedChangeListener
            }
            registrarEstadoParaDeshacer()
            binding.cbProductoVentanaPanel.isChecked = false
            binding.cbProductoMamparaPanel.isChecked = false
            binding.etDistPisoPanel.setText("0")
            binding.etDistTechoPanel.setText("20")
            actualizarClienteHeaderDesdePanel()
            actualizarRectanguloDesdePanelMedidas()
        }
        binding.cbProductoMamparaPanel.setOnCheckedChangeListener { _, isChecked ->
            if (restaurandoEstadoTemporal) return@setOnCheckedChangeListener
            if (!isChecked) {
                actualizarClienteHeaderDesdePanel()
                return@setOnCheckedChangeListener
            }
            registrarEstadoParaDeshacer()
            binding.cbProductoVentanaPanel.isChecked = false
            binding.cbProductoPuertaPanel.isChecked = false
            binding.etDistPisoPanel.setText("0")
            binding.etDistTechoPanel.setText("20")
            actualizarClienteHeaderDesdePanel()
            actualizarRectanguloDesdePanelMedidas()
        }

        binding.btnEncuentroMuroPanel.setOnClickListener {
            if (!restaurandoEstadoTemporal) registrarEstadoParaDeshacer()
            encuentros = "muro"
            binding.rectanglesDrawingView.setEncounterType(RectanglesDrawingView.EncounterType.MURO)
            actualizarClienteHeaderDesdePanel()
            ocultarPanelesTemporales()
        }
        binding.btnEncuentroAcopladoPanel.setOnClickListener {
            mostrarDialogoAcopleLados()
        }
        binding.btnEncuentroIndependientePanel.setOnClickListener {
            if (!restaurandoEstadoTemporal) registrarEstadoParaDeshacer()
            encuentros = "independiente"
            binding.rectanglesDrawingView.setEncounterType(RectanglesDrawingView.EncounterType.INDEPENDIENTE)
            actualizarClienteHeaderDesdePanel()
            ocultarPanelesTemporales()
        }
        binding.btnEncuentroBordeLibrePanel.setOnClickListener {
            mostrarDialogoBordeLibreLados()
        }

        binding.btnGeometriaPlanoPanel.setOnClickListener {
            if (!restaurandoEstadoTemporal) registrarEstadoParaDeshacer()
            geometria = "plano"
            binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.PLANO)
            actualizarClienteHeaderDesdePanel()
            ocultarPanelesTemporales()
        }
        binding.btnGeometriaEsquineroPanel.setOnClickListener {
            mostrarDialogoGeometriaEsquinero()
        }
        binding.btnGeometriaCurvoPanel.setOnClickListener {
            mostrarDialogoGeometriaCurvo()
        }
        binding.btnGeometriaCTopPanel.setOnClickListener {
            mostrarDialogoGeometriaCTop()
        }
        binding.btnGeometriaMultiLadoPanel.setOnClickListener {
            mostrarDialogoAgregarAletaMultiLado()
        }

        val watcherMedidas = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (actualizandoPanelMedidas || restaurandoEstadoTemporal) return
                registrarEstadoParaDeshacer()
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                if (actualizandoPanelMedidas || restaurandoEstadoTemporal) return
                actualizarRectanguloDesdePanelMedidas()
            }
        }

        binding.etMedidaSuperiorPanel.addTextChangedListener(watcherMedidas)
        binding.etMedidaDerechaPanel.addTextChangedListener(watcherMedidas)
        binding.etMedidaInferiorPanel.addTextChangedListener(watcherMedidas)
        binding.etMedidaIzquierdaPanel.addTextChangedListener(watcherMedidas)
        binding.etDistPisoPanel.addTextChangedListener(watcherMedidas)
        binding.etDistTechoPanel.addTextChangedListener(watcherMedidas)
        configurarCapturaUndoPorFoco(
            binding.etMedidaSuperiorPanel,
            binding.etMedidaDerechaPanel,
            binding.etMedidaInferiorPanel,
            binding.etMedidaIzquierdaPanel,
            binding.etDistPisoPanel,
            binding.etDistTechoPanel
        )

        binding.rectanglesDrawingView.setOnTouchListener { _, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                ocultarPanelesTemporales()
            }
            false
        }

        binding.rectanglesDrawingView.onSideMeasurementChanged = { side, previousValue, value ->
            if (!restaurandoEstadoTemporal) {
                registrarEstadoParaDeshacerConCotaAnterior(side, previousValue)
            }
            val valorTexto = if (value % 1f == 0f) value.toInt().toString() else value.toString()
            when (side) {
                RectanglesDrawingView.Side.TOP -> binding.etMedidaSuperiorPanel.setText(valorTexto)
                RectanglesDrawingView.Side.RIGHT -> binding.etMedidaDerechaPanel.setText(valorTexto)
                RectanglesDrawingView.Side.BOTTOM -> binding.etMedidaInferiorPanel.setText(valorTexto)
                RectanglesDrawingView.Side.LEFT -> binding.etMedidaIzquierdaPanel.setText(valorTexto)
            }
        }
        binding.rectanglesDrawingView.onEsquineroAletaAnchoChanged = { previousValue, _ ->
            if (!restaurandoEstadoTemporal) {
                registrarEstadoParaDeshacerConAletaAnterior(previousAncho = previousValue, previousAlto = null)
            }
            if (geometria == "c_top") {
                val der = binding.rectanglesDrawingView.getCTopAletaConfig(RectanglesDrawingView.AletaSide.RIGHT)
                val izq = binding.rectanglesDrawingView.getCTopAletaConfig(RectanglesDrawingView.AletaSide.LEFT)
                cTopRightLado1Cm = der.lado1Cm
                cTopRightLado2Cm = der.lado2Cm
                cTopRightHipotenusaCm = der.hipotenusaCm
                cTopRightAletaAnchoCm = der.aletaAnchoCm
                cTopRightAletaAltoCm = der.aletaAltoCm
                cTopLeftLado1Cm = izq.lado1Cm
                cTopLeftLado2Cm = izq.lado2Cm
                cTopLeftHipotenusaCm = izq.hipotenusaCm
                cTopLeftAletaAnchoCm = izq.aletaAnchoCm
                cTopLeftAletaAltoCm = izq.aletaAltoCm
            } else {
                val cfg = binding.rectanglesDrawingView.getEsquineroConfig()
                esquineroLado1Cm = cfg.lado1Cm
                esquineroLado2Cm = cfg.lado2Cm
                esquineroHipotenusaCm = cfg.hipotenusaCm
                esquineroAletaAnchoCm = cfg.aletaAnchoCm
                esquineroAletaAltoCm = cfg.aletaAltoCm
                esquineroAletaDirection = cfg.aletaDirection
                esquineroAletaSide = cfg.aletaSide
                esquineroUsar345 = false
                if (esGeometriaEsquinero()) {
                    geometria = when (cfg.variant) {
                        RectanglesDrawingView.EsquineroVariant.ARCO -> "esquinero_arco"
                        RectanglesDrawingView.EsquineroVariant.ARCO_RECTO -> "esquinero_arco_recto"
                        RectanglesDrawingView.EsquineroVariant.RECTO -> "esquinero"
                    }
                }
            }
            actualizarClienteHeaderDesdePanel()
        }
        binding.rectanglesDrawingView.onEsquineroAletaAltoChanged = { previousValue, _ ->
            if (!restaurandoEstadoTemporal) {
                registrarEstadoParaDeshacerConAletaAnterior(previousAncho = null, previousAlto = previousValue)
            }
            if (geometria == "c_top") {
                val der = binding.rectanglesDrawingView.getCTopAletaConfig(RectanglesDrawingView.AletaSide.RIGHT)
                val izq = binding.rectanglesDrawingView.getCTopAletaConfig(RectanglesDrawingView.AletaSide.LEFT)
                cTopRightLado1Cm = der.lado1Cm
                cTopRightLado2Cm = der.lado2Cm
                cTopRightHipotenusaCm = der.hipotenusaCm
                cTopRightAletaAnchoCm = der.aletaAnchoCm
                cTopRightAletaAltoCm = der.aletaAltoCm
                cTopLeftLado1Cm = izq.lado1Cm
                cTopLeftLado2Cm = izq.lado2Cm
                cTopLeftHipotenusaCm = izq.hipotenusaCm
                cTopLeftAletaAnchoCm = izq.aletaAnchoCm
                cTopLeftAletaAltoCm = izq.aletaAltoCm
            } else {
                val cfg = binding.rectanglesDrawingView.getEsquineroConfig()
                esquineroLado1Cm = cfg.lado1Cm
                esquineroLado2Cm = cfg.lado2Cm
                esquineroHipotenusaCm = cfg.hipotenusaCm
                esquineroAletaAnchoCm = cfg.aletaAnchoCm
                esquineroAletaAltoCm = cfg.aletaAltoCm
                esquineroAletaDirection = cfg.aletaDirection
                esquineroAletaSide = cfg.aletaSide
                esquineroUsar345 = false
                if (esGeometriaEsquinero()) {
                    geometria = when (cfg.variant) {
                        RectanglesDrawingView.EsquineroVariant.ARCO -> "esquinero_arco"
                        RectanglesDrawingView.EsquineroVariant.ARCO_RECTO -> "esquinero_arco_recto"
                        RectanglesDrawingView.EsquineroVariant.RECTO -> "esquinero"
                    }
                }
            }
            actualizarClienteHeaderDesdePanel()
        }
        binding.rectanglesDrawingView.onMultiLadoAletaAnchoChanged = { index, previousValue, _ ->
            if (!restaurandoEstadoTemporal) {
                registrarEstadoParaDeshacerConMultiLadoAletaAnterior(index, previousAncho = previousValue, previousAlto = null)
            }
            val listaActual = binding.rectanglesDrawingView.getMultiLadoAletas().map {
                MultiLadoAletaEstado(
                    side = it.side,
                    direction = it.direction,
                    lado1Cm = it.lado1Cm,
                    lado2Cm = it.lado2Cm,
                    hipotenusaCm = it.hipotenusaCm,
                    aletaAnchoCm = it.aletaAnchoCm,
                    aletaAltoCm = it.aletaAltoCm
                )
            }
            multiLadoAletas.clear()
            multiLadoAletas.addAll(listaActual)
            multiLadoCount = multiLadoAletas.size.coerceAtLeast(1)
            actualizarClienteHeaderDesdePanel()
        }
        binding.rectanglesDrawingView.onMultiLadoAletaAltoChanged = { index, previousValue, _ ->
            if (!restaurandoEstadoTemporal) {
                registrarEstadoParaDeshacerConMultiLadoAletaAnterior(index, previousAncho = null, previousAlto = previousValue)
            }
            val listaActual = binding.rectanglesDrawingView.getMultiLadoAletas().map {
                MultiLadoAletaEstado(
                    side = it.side,
                    direction = it.direction,
                    lado1Cm = it.lado1Cm,
                    lado2Cm = it.lado2Cm,
                    hipotenusaCm = it.hipotenusaCm,
                    aletaAnchoCm = it.aletaAnchoCm,
                    aletaAltoCm = it.aletaAltoCm
                )
            }
            multiLadoAletas.clear()
            multiLadoAletas.addAll(listaActual)
            multiLadoCount = multiLadoAletas.size.coerceAtLeast(1)
            actualizarClienteHeaderDesdePanel()
        }

        actualizarResumenDatosTecnicos()
        actualizarEstadoBotonDeshacer()
    }

    private fun alternarPanel(panelObjetivo: View) {
        val estabaVisible = panelObjetivo.visibility == View.VISIBLE
        ocultarPanelesTemporales()
        if (estabaVisible) return
        panelObjetivo.visibility = View.VISIBLE
    }

    private fun ocultarPanelesTemporales() {
        val paneles = listOf(
            binding.panelClienteMedida,
            binding.panelProductosMedida,
            binding.panelDatosTecnicosMedida,
            binding.panelEncuentrosMedida,
            binding.panelGeometriaMedida,
            binding.panelMedidasPanel
        )
        paneles.forEach { panel ->
            panel.visibility = View.GONE
        }
        ocultarTecladoYFoco()
    }

    private fun ocultarTecladoYFoco() {
        currentFocus?.clearFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun esGeometriaEsquinero(valor: String = geometria): Boolean {
        return valor == "esquinero" || valor == "esquinero_arco" || valor == "esquinero_arco_recto"
    }

    private fun mostrarDialogoSeleccionSimple(
        titulo: String,
        opciones: Array<String>,
        actual: String,
        onSeleccion: (String) -> Unit
    ) {
        val idxActual = opciones.indexOfFirst { it.equals(actual, ignoreCase = true) }
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setSingleChoiceItems(opciones, idxActual) { dialog, which ->
                onSeleccion(opciones[which])
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEspecificaciones() {
        val colores = arrayOf("natural", "negro", "blanco", "champagne")
        val vidrios = arrayOf("crudo", "templado", "insulado", "laminado")
        val idxColor = colores.indexOfFirst {
            it.equals(especificaciones["colorAluminio"].orEmpty(), ignoreCase = true)
        }.coerceAtLeast(0)

        AlertDialog.Builder(this)
            .setTitle("Color aluminio")
            .setSingleChoiceItems(colores, idxColor) { dialogColor, whichColor ->
                val color = colores[whichColor]
                dialogColor.dismiss()
                val idxVidrio = vidrios.indexOfFirst {
                    it.equals(especificaciones["tipoVidrio"].orEmpty(), ignoreCase = true)
                }.coerceAtLeast(0)
                AlertDialog.Builder(this)
                    .setTitle("Tipo de vidrio")
                    .setSingleChoiceItems(vidrios, idxVidrio) { dialogVidrio, whichVidrio ->
                        val vidrio = vidrios[whichVidrio]
                        dialogVidrio.dismiss()
                        val cont = android.widget.LinearLayout(this).apply {
                            orientation = android.widget.LinearLayout.VERTICAL
                            setPadding(42, 18, 42, 0)
                        }
                        val etDetalleAl = android.widget.EditText(this).apply {
                            hint = "Detalle aluminio (opcional)"
                            setText(especificaciones["detalleAluminio"].orEmpty())
                            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                        }
                        val etDetalleVidrio = android.widget.EditText(this).apply {
                            hint = "Detalle vidrio (opcional)"
                            setText(especificaciones["detalleVidrio"].orEmpty())
                            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                        }
                        cont.addView(etDetalleAl)
                        cont.addView(etDetalleVidrio)
                        AlertDialog.Builder(this)
                            .setTitle("Detalles adicionales")
                            .setView(cont)
                            .setPositiveButton("Guardar") { _, _ ->
                                especificaciones = especificaciones.toMutableMap().apply {
                                    this["colorAluminio"] = color
                                    this["tipoVidrio"] = vidrio
                                    this["detalleAluminio"] = etDetalleAl.text?.toString().orEmpty().trim()
                                    this["detalleVidrio"] = etDetalleVidrio.text?.toString().orEmpty().trim()
                                }
                                actualizarResumenDatosTecnicos()
                                actualizarClienteHeaderDesdePanel()
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoAccesorios() {
        val opciones = arrayOf("clips", "felpa", "jaladera", "cerradura", "rodamientos")
        val marcados = BooleanArray(opciones.size) { idx ->
            accesorios.any { it.equals(opciones[idx], ignoreCase = true) }
        }
        AlertDialog.Builder(this)
            .setTitle("Accesorios")
            .setMultiChoiceItems(opciones, marcados) { _, which, isChecked ->
                marcados[which] = isChecked
            }
            .setPositiveButton("Aplicar") { _, _ ->
                accesorios = opciones.filterIndexed { index, _ -> marcados[index] }
                actualizarResumenDatosTecnicos()
                actualizarClienteHeaderDesdePanel()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun actualizarResumenDatosTecnicos() {
        binding.btnSistemaPanel.text = "Sistema: ${sistema.ifBlank { "sin seleccionar" }}"
        binding.btnTipoPanel.text = "Tipo: ${tipoApertura.ifBlank { "sin seleccionar" }}"
        binding.btnModeloPanel.text = "Modelo: ${modelo.ifBlank { "sin seleccionar" }}"
        binding.btnAcabadoPanel.text = "Acabado: ${acabado.ifBlank { "sin seleccionar" }}"
        val color = especificaciones["colorAluminio"].orEmpty()
        val vidrio = especificaciones["tipoVidrio"].orEmpty()
        val espec = listOfNotNull(
            color.takeIf { it.isNotBlank() }?.let { "Al:$it" },
            vidrio.takeIf { it.isNotBlank() }?.let { "V:$it" },
            especificaciones["detalleAluminio"]?.takeIf { it.isNotBlank() }?.let { "AlDet:${it.take(18)}" },
            especificaciones["detalleVidrio"]?.takeIf { it.isNotBlank() }?.let { "VDet:${it.take(18)}" }
        ).joinToString(" | ").ifBlank { "sin seleccionar" }
        binding.btnEspecificacionesPanel.text = "Especificaciones: $espec"
        val accTxt = if (accesorios.isEmpty()) "sin seleccionar" else accesorios.joinToString(", ")
        binding.btnAccesoriosPanel.text = "Accesorios: $accTxt"
        actualizarSemaforoBotones()
    }

    private fun actualizarSemaforoBotones() {
        // No obligatorios: anaranjado pendiente, verde al tratarse
        val clienteTratado = binding.etClienteNombrePanel.text?.toString()?.trim().orEmpty().isNotBlank() ||
            binding.etClienteDocumentoPanel.text?.toString()?.trim().orEmpty().isNotBlank() ||
            binding.etClienteTelefonoPanel.text?.toString()?.trim().orEmpty().isNotBlank() ||
            binding.etClienteDireccionPanel.text?.toString()?.trim().orEmpty().isNotBlank()
        val especificacionesTratadas = especificaciones.any { it.value.isNotBlank() }
        val accesoriosTratados = accesorios.isNotEmpty()
        pintarBoton(binding.btnControlCliente, if (clienteTratado) COLOR_OBLIGATORIO_LISTO else COLOR_NO_OBLIGATORIO)
        pintarBoton(binding.btnEspecificacionesPanel, if (especificacionesTratadas) COLOR_OBLIGATORIO_LISTO else COLOR_NO_OBLIGATORIO)
        pintarBoton(binding.btnAccesoriosPanel, if (accesoriosTratados) COLOR_OBLIGATORIO_LISTO else COLOR_NO_OBLIGATORIO)

        // Obligatorios
        val productoOk = categoria.isNotBlank()
        val encuentroOk = encuentros.isNotBlank()
        val geometriaOk = geometria.isNotBlank()
        val medidasOk = medidasPrincipalesValidas()
        val sistemaOk = sistema.isNotBlank()
        val tipoOk = tipoApertura.isNotBlank()
        val modeloOk = modelo.isNotBlank()
        val acabadoOk = acabado.isNotBlank()
        val datosTecnicosOk = sistemaOk && tipoOk && modeloOk && acabadoOk

        pintarBoton(binding.btnControlProductos, if (productoOk) COLOR_OBLIGATORIO_LISTO else COLOR_OBLIGATORIO_PENDIENTE)
        pintarBoton(binding.btnControlEncuentros, if (encuentroOk) COLOR_OBLIGATORIO_LISTO else COLOR_OBLIGATORIO_PENDIENTE)
        pintarBoton(binding.btnControlGeometria, if (geometriaOk) COLOR_OBLIGATORIO_LISTO else COLOR_OBLIGATORIO_PENDIENTE)
        pintarBoton(binding.btnControlMedidas, if (medidasOk) COLOR_OBLIGATORIO_LISTO else COLOR_OBLIGATORIO_PENDIENTE)
        pintarBoton(binding.btnControlDatosTecnicos, if (datosTecnicosOk) COLOR_OBLIGATORIO_LISTO else COLOR_OBLIGATORIO_PENDIENTE)

        pintarBoton(binding.btnSistemaPanel, if (sistemaOk) COLOR_OBLIGATORIO_LISTO else COLOR_OBLIGATORIO_PENDIENTE)
        pintarBoton(binding.btnTipoPanel, if (tipoOk) COLOR_OBLIGATORIO_LISTO else COLOR_OBLIGATORIO_PENDIENTE)
        pintarBoton(binding.btnModeloPanel, if (modeloOk) COLOR_OBLIGATORIO_LISTO else COLOR_OBLIGATORIO_PENDIENTE)
        pintarBoton(binding.btnAcabadoPanel, if (acabadoOk) COLOR_OBLIGATORIO_LISTO else COLOR_OBLIGATORIO_PENDIENTE)
    }

    private fun intentarArchivarMedida() {
        val faltantesObligatorios = mutableListOf<String>()
        if (categoria.isBlank()) faltantesObligatorios += "Producto"
        if (sistema.isBlank()) faltantesObligatorios += "Sistema"
        if (tipoApertura.isBlank()) faltantesObligatorios += "Tipo"
        if (modelo.isBlank()) faltantesObligatorios += "Modelo"
        if (acabado.isBlank()) faltantesObligatorios += "Acabado"
        if (encuentros.isBlank()) faltantesObligatorios += "Encuentros"
        if (geometria.isBlank()) faltantesObligatorios += "Geometría"
        if (!medidasPrincipalesValidas()) faltantesObligatorios += "Medidas"

        if (faltantesObligatorios.isNotEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Faltan datos obligatorios")
                .setMessage("Debes completar:\n- ${faltantesObligatorios.joinToString("\n- ")}")
                .setPositiveButton("Entendido", null)
                .show()
            return
        }

        val faltantesNoObligatorios = mutableListOf<String>()
        val clienteTratado = binding.etClienteNombrePanel.text?.toString()?.trim().orEmpty().isNotBlank() ||
            binding.etClienteDocumentoPanel.text?.toString()?.trim().orEmpty().isNotBlank() ||
            binding.etClienteTelefonoPanel.text?.toString()?.trim().orEmpty().isNotBlank() ||
            binding.etClienteDireccionPanel.text?.toString()?.trim().orEmpty().isNotBlank()
        if (!clienteTratado) faltantesNoObligatorios += "Cliente"
        if (!especificaciones.any { it.value.isNotBlank() }) faltantesNoObligatorios += "Especificaciones"
        if (accesorios.isEmpty()) faltantesNoObligatorios += "Accesorios"

        if (faltantesNoObligatorios.isNotEmpty()) {
            AlertDialog.Builder(this)
                .setTitle("Datos no obligatorios pendientes")
                .setMessage(
                    "Faltan estos datos no obligatorios:\n- ${faltantesNoObligatorios.joinToString("\n- ")}\n\n¿Deseas proceder de todos modos?"
                )
                .setPositiveButton("Proceder") { _, _ ->
                    archivarItemActual(EstadoMedicion.MEDIDO)
                    mostrarMensaje("Medida archivada")
                }
                .setNegativeButton("Cancelar", null)
                .show()
            return
        }

        archivarItemActual(EstadoMedicion.MEDIDO)
        mostrarMensaje("Medida archivada")
    }

    private fun pintarBoton(boton: View, colorHex: String) {
        val color = Color.parseColor(colorHex)
        (boton as? Button)?.apply {
            backgroundTintList = ColorStateList.valueOf(color)
            setTextColor(Color.WHITE)
        }
    }

    private fun esquineroVariantDesdeGeometria(valor: String = geometria): RectanglesDrawingView.EsquineroVariant {
        return when (valor) {
            "esquinero_arco" -> RectanglesDrawingView.EsquineroVariant.ARCO
            "esquinero_arco_recto" -> RectanglesDrawingView.EsquineroVariant.ARCO_RECTO
            else -> RectanglesDrawingView.EsquineroVariant.RECTO
        }
    }

    private fun actualizarClienteHeaderDesdePanel() {
        val productoTipo = when {
            binding.cbProductoVentanaPanel.isChecked -> RectanglesDrawingView.ProductType.VENTANA
            binding.cbProductoPuertaPanel.isChecked -> RectanglesDrawingView.ProductType.PUERTA
            binding.cbProductoMamparaPanel.isChecked -> RectanglesDrawingView.ProductType.MAMPARA
            else -> RectanglesDrawingView.ProductType.NONE
        }
        binding.rectanglesDrawingView.setProductType(productoTipo)

        val nombre = binding.etClienteNombrePanel.text?.toString()?.trim().orEmpty()
        val documento = binding.etClienteDocumentoPanel.text?.toString()?.trim().orEmpty()
        val telefono = binding.etClienteTelefonoPanel.text?.toString()?.trim().orEmpty()

        val cliente = buildString {
            if (nombre.isNotBlank()) append(nombre)
            if (documento.isNotBlank()) {
                if (isNotBlank()) append(" ")
                append("(Doc: $documento)")
            }
            if (telefono.isNotBlank()) {
                if (isNotBlank()) append(" ")
                append("Tel: $telefono")
            }
        }.ifBlank { "sin datos" }

        val producto = when {
            binding.cbProductoVentanaPanel.isChecked -> "Ventana"
            binding.cbProductoPuertaPanel.isChecked -> "Puerta"
            binding.cbProductoMamparaPanel.isChecked -> "Mampara"
            else -> "sin seleccionar"
        }
        categoria = when (producto) {
            "Ventana" -> "ventana"
            "Puerta" -> "puerta"
            "Mampara" -> "mampara"
            else -> ""
        }
        val encuentro = when (encuentros) {
            "muro" -> "Muro"
            "acoplado" -> "Acoplado"
            "independiente" -> "Independiente"
            "borde_libre" -> "Borde libre"
            else -> "sin seleccionar"
        }
        val geometriaTexto = when (geometria) {
            "plano" -> "Plano"
            "esquinero" -> "Esquinero (L)"
            "esquinero_arco" -> "Esquinero (L + arco)"
            "esquinero_arco_recto" -> "Esquinero (L + arco + recto)"
            "curvo" -> "Curvo (arco)"
            "c_top" -> "Top en C"
            "multi_lado" -> "Múltiple lado"
            else -> "Plano"
        }
        val detalleEsquinero = if (esGeometriaEsquinero()) {
            val cfg = binding.rectanglesDrawingView.getEsquineroConfig()
            val direccionAleta = if (cfg.aletaDirection == RectanglesDrawingView.AletaDirection.INTERIOR) "Int" else "Ext"
            val ladoAleta = if (cfg.aletaSide == RectanglesDrawingView.AletaSide.RIGHT) "Der" else "Izq"
            val tipo = when (cfg.variant) {
                RectanglesDrawingView.EsquineroVariant.ARCO -> "Arco"
                RectanglesDrawingView.EsquineroVariant.ARCO_RECTO -> "Arco+Recto"
                RectanglesDrawingView.EsquineroVariant.RECTO -> "Recto"
            }
            " | Tipo:$tipo Ang: ${formatearNumero(cfg.anguloDeg)}° (${formatearNumero(cfg.lado1Cm)}-${formatearNumero(cfg.lado2Cm)}-${formatearNumero(cfg.hipotenusaCm)}) Aleta:${formatearNumero(cfg.aletaAnchoCm)}x${formatearNumero(cfg.aletaAltoCm)} Dir:$direccionAleta Lado:$ladoAleta"
        } else if (geometria == "curvo") {
            val cfg = binding.rectanglesDrawingView.getCurvoConfig()
            val direccionCurvo = if (cfg.direction == RectanglesDrawingView.AletaDirection.INTERIOR) "Int" else "Ext"
            val flechaTexto = cfg.flechaCm?.let { formatearNumero(it) } ?: "~${formatearNumero(cfg.flechaEfectivaCm)}"
            " | Curvo D:${formatearNumero(cfg.desarrolloCm)} C:${formatearNumero(cfg.cuerdaCm)} F:$flechaTexto Dir:$direccionCurvo"
        } else if (geometria == "c_top") {
            val direccionAleta = if (esquineroAletaDirection == RectanglesDrawingView.AletaDirection.INTERIOR) "Int" else "Ext"
            val der = binding.rectanglesDrawingView.getCTopAletaConfig(RectanglesDrawingView.AletaSide.RIGHT)
            val izq = binding.rectanglesDrawingView.getCTopAletaConfig(RectanglesDrawingView.AletaSide.LEFT)
            " | Der:${formatearNumero(der.anguloDeg)}° ${formatearNumero(der.aletaAnchoCm)}x${formatearNumero(der.aletaAltoCm)}" +
                " Izq:${formatearNumero(izq.anguloDeg)}° ${formatearNumero(izq.aletaAnchoCm)}x${formatearNumero(izq.aletaAltoCm)} Dir:$direccionAleta"
        } else if (geometria == "multi_lado") {
            val ultimo = multiLadoAletas.lastOrNull()
            val angulosTexto = multiLadoAletas.mapIndexed { idx, a ->
                val ang = calcularAnguloTriangulo(a.lado1Cm, a.lado2Cm, a.hipotenusaCm)
                "${idx + 1}:${formatearNumero(ang)}°"
            }.joinToString(" ")
            val ultTxt = if (ultimo != null) {
                val l = if (ultimo.side == RectanglesDrawingView.AletaSide.RIGHT) "Der" else "Izq"
                val d = if (ultimo.direction == RectanglesDrawingView.AletaDirection.INTERIOR) "Int" else "Ext"
                " ult:$l/$d ${formatearNumero(ultimo.aletaAnchoCm)}x${formatearNumero(ultimo.aletaAltoCm)}"
            } else ""
            " | Multi: ${multiLadoAletas.size} aletas Ang:[${if (angulosTexto.isBlank()) "-" else angulosTexto}]$ultTxt"
        } else ""
        val acopleResumen = if (encuentros == "acoplado" && acopleLadosSeleccionados.isNotEmpty()) {
            val productoAcople = when (acopleProductoSeleccionado) {
                RectanglesDrawingView.ProductType.VENTANA -> "Ventana"
                RectanglesDrawingView.ProductType.PUERTA -> "Puerta"
                RectanglesDrawingView.ProductType.MAMPARA -> "Mampara"
                RectanglesDrawingView.ProductType.NONE -> "N/A"
            }
            val lados = acopleLadosSeleccionados.joinToString("/") { side ->
                when (side) {
                    RectanglesDrawingView.Side.TOP -> "Sup"
                    RectanglesDrawingView.Side.RIGHT -> "Der"
                    RectanglesDrawingView.Side.BOTTOM -> "Inf"
                    RectanglesDrawingView.Side.LEFT -> "Izq"
                }
            }
            " | Acople: $productoAcople [$lados] ${formatearNumero(acopleAnchoBaseCm)}x${formatearNumero(acopleAltoBaseCm)}"
        } else if (encuentros == "borde_libre" && acopleLadosSeleccionados.isNotEmpty()) {
            val lados = acopleLadosSeleccionados.joinToString("/") { side ->
                when (side) {
                    RectanglesDrawingView.Side.TOP -> "Sup"
                    RectanglesDrawingView.Side.RIGHT -> "Der"
                    RectanglesDrawingView.Side.BOTTOM -> "Inf"
                    RectanglesDrawingView.Side.LEFT -> "Izq"
                }
            }
            " | Borde libre: [$lados]"
        } else {
            ""
        }
        val colorAl = especificaciones["colorAluminio"].orEmpty().ifBlank { "-" }
        val tipoVidrio = especificaciones["tipoVidrio"].orEmpty().ifBlank { "-" }
        val accesoriosTxt = if (accesorios.isEmpty()) "-" else accesorios.joinToString(", ")
        binding.tvClienteHeader.text = construirHeaderNumeradoColor(
            listOf(
                "1. Cliente:" to cliente,
                "2. Producto:" to producto,
                "3. Sistema:" to sistema.ifBlank { "-" },
                "4. Tipo:" to tipoApertura.ifBlank { "-" },
                "5. Geometría:" to "$geometriaTexto$detalleEsquinero",
                "6. Encuentro:" to "$encuentro$acopleResumen",
                "7. Modelo:" to modelo.ifBlank { "-" },
                "8. Acabado:" to acabado.ifBlank { "-" },
                "9. Especificaciones:" to "aluminio=$colorAl, vidrio=$tipoVidrio",
                "10. Accesorios:" to accesoriosTxt
            )
        )
        actualizarSemaforoBotones()
    }

    private fun construirHeaderNumeradoColor(lineas: List<Pair<String, String>>): CharSequence {
        val colores = listOf(
            Color.parseColor("#0B4F6C"),
            Color.parseColor("#7A1E1E"),
            Color.parseColor("#1F6F3F"),
            Color.parseColor("#5C3C92"),
            Color.parseColor("#A04A00"),
            Color.parseColor("#004B8D"),
            Color.parseColor("#8A1538"),
            Color.parseColor("#0D5D56"),
            Color.parseColor("#6C4A00"),
            Color.parseColor("#3E4A61")
        )
        val sb = SpannableStringBuilder()
        lineas.forEachIndexed { index, (etiqueta, valor) ->
            val color = colores[index % colores.size]
            val prefijo = SpannableString("$etiqueta ")
            prefijo.setSpan(
                ForegroundColorSpan(color),
                0,
                prefijo.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            sb.append(prefijo)
            sb.append(valor)
            if (index < lineas.lastIndex) sb.append('\n')
        }
        return sb
    }

    private fun construirNotasTecnicasNumeradas(): String {
        val colorAl = especificaciones["colorAluminio"].orEmpty().ifBlank { "-" }
        val tipoVidrio = especificaciones["tipoVidrio"].orEmpty().ifBlank { "-" }
        val accesoriosTxt = if (accesorios.isEmpty()) "-" else accesorios.joinToString(", ")
        return buildString {
            appendLine("1. Categoria: ${categoria.ifBlank { "-" }}")
            appendLine("2. Sistema: ${sistema.ifBlank { "-" }}")
            appendLine("3. Tipo: ${tipoApertura.ifBlank { "-" }}")
            appendLine("4. Geometria: ${geometria.ifBlank { "-" }}")
            appendLine("5. Encuentros: ${encuentros.ifBlank { "-" }}")
            appendLine("6. Modelo: ${modelo.ifBlank { "-" }}")
            appendLine("7. Acabado: ${acabado.ifBlank { "-" }}")
            appendLine("8. Especificaciones: aluminio=$colorAl, vidrio=$tipoVidrio")
            appendLine("9. Accesorios: $accesoriosTxt")
            appendLine("10. Medidas cm: ancho=${formatearNumero(anchoProductoCm)}, alto=${formatearNumero(altoProductoCm)}, puente=${formatearNumero(alturaPuenteCm)}")
            append("11. Unidad: ${unidadMedida.name}")
        }
    }

    private fun actualizarRectanguloDesdePanelMedidas() {
        val superior = binding.etMedidaSuperiorPanel.text?.toString()?.toFloatOrNull()
        val derecha = binding.etMedidaDerechaPanel.text?.toString()?.toFloatOrNull()
        val inferior = binding.etMedidaInferiorPanel.text?.toString()?.toFloatOrNull()
        val izquierda = binding.etMedidaIzquierdaPanel.text?.toString()?.toFloatOrNull()
        val anchoCapturado = listOfNotNull(superior, inferior).firstOrNull { it > 0f } ?: 0f
        val altoCapturado = listOfNotNull(derecha, izquierda).firstOrNull { it > 0f } ?: 0f
        anchoProductoCm = anchoCapturado
        altoProductoCm = altoCapturado
        binding.rectanglesDrawingView.updateSideMeasurements(
            top = superior,
            right = derecha,
            bottom = inferior,
            left = izquierda
        )

        val productoTipo = when {
            binding.cbProductoVentanaPanel.isChecked -> RectanglesDrawingView.ProductType.VENTANA
            binding.cbProductoPuertaPanel.isChecked -> RectanglesDrawingView.ProductType.PUERTA
            binding.cbProductoMamparaPanel.isChecked -> RectanglesDrawingView.ProductType.MAMPARA
            else -> RectanglesDrawingView.ProductType.NONE
        }

        if (productoTipo == RectanglesDrawingView.ProductType.NONE) {
            return
        }

        val alturaAbertura = maxOf(derecha ?: 90f, izquierda ?: 90f).coerceAtLeast(1f)
        val alturaAcopleTotal = if (encuentros == "acoplado" && acopleLadosSeleccionados.isNotEmpty()) {
            val antepechoAcople = when (acopleProductoSeleccionado) {
                RectanglesDrawingView.ProductType.VENTANA -> 90f
                RectanglesDrawingView.ProductType.PUERTA, RectanglesDrawingView.ProductType.MAMPARA, RectanglesDrawingView.ProductType.NONE -> 0f
            }
            acopleAltoBaseCm + antepechoAcople
        } else {
            0f
        }
        val alturaMuroDinamica = maxOf(ALTURA_MURO_BASE_CM, alturaAbertura, alturaAcopleTotal)
        alturaMuroActualCm = alturaMuroDinamica
        val distPisoIngresada = binding.etDistPisoPanel.text?.toString()?.toFloatOrNull()
            ?: if (productoTipo == RectanglesDrawingView.ProductType.VENTANA) 90f else 0f

        val maxAntepecho = (alturaMuroDinamica - alturaAbertura).coerceAtLeast(0f)
        val antepechoNormalizado = distPisoIngresada.coerceIn(0f, maxAntepecho)
        val distTechoCalculada = (alturaMuroDinamica - (antepechoNormalizado + alturaAbertura)).coerceAtLeast(0f)

        actualizandoPanelMedidas = true
        setTextoSiCambio(binding.etDistPisoPanel, formatearNumero(antepechoNormalizado))
        setTextoSiCambio(binding.etDistTechoPanel, formatearNumero(distTechoCalculada))
        actualizandoPanelMedidas = false

        binding.rectanglesDrawingView.updateWallHeight(alturaMuroDinamica)

        binding.rectanglesDrawingView.updateClearances(
            sill = antepechoNormalizado,
            head = distTechoCalculada
        )
        alturaPuenteCm = distTechoCalculada
        actualizarClienteHeaderDesdePanel()
    }

    private fun medidasPrincipalesValidas(): Boolean {
        val superior = binding.etMedidaSuperiorPanel.text?.toString()?.toFloatOrNull() ?: 0f
        val inferior = binding.etMedidaInferiorPanel.text?.toString()?.toFloatOrNull() ?: 0f
        val derecha = binding.etMedidaDerechaPanel.text?.toString()?.toFloatOrNull() ?: 0f
        val izquierda = binding.etMedidaIzquierdaPanel.text?.toString()?.toFloatOrNull() ?: 0f
        val anchoOk = superior > 0f || inferior > 0f
        val altoOk = derecha > 0f || izquierda > 0f
        return anchoOk && altoOk
    }

    private fun mostrarDialogoAcopleLados() {
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 18, 32, 0)
        }

        val tvLados = android.widget.TextView(this).apply {
            text = "Lados de acople"
            textSize = 15f
        }
        container.addView(tvLados)

        val cbTop = android.widget.CheckBox(this).apply {
            text = "Superior"
            isChecked = acopleLadosSeleccionados.contains(RectanglesDrawingView.Side.TOP)
        }
        val cbRight = android.widget.CheckBox(this).apply {
            text = "Derecha"
            isChecked = acopleLadosSeleccionados.contains(RectanglesDrawingView.Side.RIGHT)
        }
        val cbBottom = android.widget.CheckBox(this).apply {
            text = "Inferior"
            isChecked = acopleLadosSeleccionados.contains(RectanglesDrawingView.Side.BOTTOM)
        }
        val cbLeft = android.widget.CheckBox(this).apply {
            text = "Izquierda"
            isChecked = acopleLadosSeleccionados.contains(RectanglesDrawingView.Side.LEFT)
        }
        container.addView(cbTop)
        container.addView(cbRight)
        container.addView(cbBottom)
        container.addView(cbLeft)

        val tvProducto = android.widget.TextView(this).apply {
            text = "Producto acoplado"
            textSize = 15f
            setPadding(0, 12, 0, 0)
        }
        container.addView(tvProducto)

        val radioGroup = android.widget.RadioGroup(this).apply {
            orientation = android.widget.RadioGroup.VERTICAL
        }
        val rbVentana = android.widget.RadioButton(this).apply {
            id = View.generateViewId()
            text = "Ventana"
        }
        val rbPuerta = android.widget.RadioButton(this).apply {
            id = View.generateViewId()
            text = "Puerta"
        }
        val rbMampara = android.widget.RadioButton(this).apply {
            id = View.generateViewId()
            text = "Mampara"
        }
        radioGroup.addView(rbVentana)
        radioGroup.addView(rbPuerta)
        radioGroup.addView(rbMampara)
        container.addView(radioGroup)

        val etAcopleAncho = android.widget.EditText(this).apply {
            hint = "Ancho base acople (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(acopleAnchoBaseCm))
        }
        val etAcopleAlto = android.widget.EditText(this).apply {
            hint = "Alto base acople (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(acopleAltoBaseCm))
        }
        container.addView(etAcopleAncho)
        container.addView(etAcopleAlto)

        when (acopleProductoSeleccionado) {
            RectanglesDrawingView.ProductType.VENTANA -> radioGroup.check(rbVentana.id)
            RectanglesDrawingView.ProductType.PUERTA -> radioGroup.check(rbPuerta.id)
            RectanglesDrawingView.ProductType.MAMPARA -> radioGroup.check(rbMampara.id)
            RectanglesDrawingView.ProductType.NONE -> radioGroup.check(rbPuerta.id)
        }

        AlertDialog.Builder(this)
            .setTitle("Configurar acople")
            .setView(container)
            .setPositiveButton("Aplicar") { _, _ ->
                if (!restaurandoEstadoTemporal) registrarEstadoParaDeshacer()
                acopleLadosSeleccionados.clear()
                if (cbTop.isChecked) acopleLadosSeleccionados.add(RectanglesDrawingView.Side.TOP)
                if (cbRight.isChecked) acopleLadosSeleccionados.add(RectanglesDrawingView.Side.RIGHT)
                if (cbBottom.isChecked) acopleLadosSeleccionados.add(RectanglesDrawingView.Side.BOTTOM)
                if (cbLeft.isChecked) acopleLadosSeleccionados.add(RectanglesDrawingView.Side.LEFT)

                acopleProductoSeleccionado = when (radioGroup.checkedRadioButtonId) {
                    rbVentana.id -> RectanglesDrawingView.ProductType.VENTANA
                    rbMampara.id -> RectanglesDrawingView.ProductType.MAMPARA
                    else -> RectanglesDrawingView.ProductType.PUERTA
                }
                acopleAnchoBaseCm = etAcopleAncho.text?.toString()?.toFloatOrNull()?.coerceAtLeast(1f) ?: acopleAnchoBaseCm
                acopleAltoBaseCm = etAcopleAlto.text?.toString()?.toFloatOrNull()?.coerceAtLeast(1f) ?: acopleAltoBaseCm

                encuentros = "acoplado"
                binding.rectanglesDrawingView.setAcopleConfiguration(
                    sides = acopleLadosSeleccionados,
                    productType = acopleProductoSeleccionado
                )
                binding.rectanglesDrawingView.setAcopleBaseSize(
                    widthCm = acopleAnchoBaseCm,
                    heightCm = acopleAltoBaseCm
                )
                actualizarRectanguloDesdePanelMedidas()
                actualizarClienteHeaderDesdePanel()
                ocultarPanelesTemporales()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoBordeLibreLados() {
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 18, 32, 0)
        }

        val tvLados = android.widget.TextView(this).apply {
            text = "Lados en borde libre"
            textSize = 15f
        }
        container.addView(tvLados)

        val cbTop = android.widget.CheckBox(this).apply {
            text = "Superior"
            isChecked = acopleLadosSeleccionados.contains(RectanglesDrawingView.Side.TOP)
        }
        val cbRight = android.widget.CheckBox(this).apply {
            text = "Derecha"
            isChecked = acopleLadosSeleccionados.contains(RectanglesDrawingView.Side.RIGHT)
        }
        val cbBottom = android.widget.CheckBox(this).apply {
            text = "Inferior"
            isChecked = acopleLadosSeleccionados.contains(RectanglesDrawingView.Side.BOTTOM)
        }
        val cbLeft = android.widget.CheckBox(this).apply {
            text = "Izquierda"
            isChecked = acopleLadosSeleccionados.contains(RectanglesDrawingView.Side.LEFT)
        }
        container.addView(cbTop)
        container.addView(cbRight)
        container.addView(cbBottom)
        container.addView(cbLeft)

        AlertDialog.Builder(this)
            .setTitle("Configurar borde libre")
            .setView(container)
            .setPositiveButton("Aplicar") { _, _ ->
                if (!restaurandoEstadoTemporal) registrarEstadoParaDeshacer()
                acopleLadosSeleccionados.clear()
                if (cbTop.isChecked) acopleLadosSeleccionados.add(RectanglesDrawingView.Side.TOP)
                if (cbRight.isChecked) acopleLadosSeleccionados.add(RectanglesDrawingView.Side.RIGHT)
                if (cbBottom.isChecked) acopleLadosSeleccionados.add(RectanglesDrawingView.Side.BOTTOM)
                if (cbLeft.isChecked) acopleLadosSeleccionados.add(RectanglesDrawingView.Side.LEFT)

                encuentros = "borde_libre"
                binding.rectanglesDrawingView.setBordeLibreSides(acopleLadosSeleccionados)
                actualizarRectanguloDesdePanelMedidas()
                actualizarClienteHeaderDesdePanel()
                ocultarPanelesTemporales()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoGeometriaEsquinero() {
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 18, 32, 0)
        }

        val tvAyuda = android.widget.TextView(this).apply {
            text = "Configura triángulo para hallar ángulo (Pitágoras 3-4-5) y la dirección de la aleta."
            textSize = 14f
        }
        val cbUsar345 = android.widget.CheckBox(this).apply {
            text = "Editar solo hipotenusa (mantener lado 1 y lado 2)"
            isChecked = esquineroUsar345
        }
        val etHipotenusa = android.widget.EditText(this).apply {
            hint = "Hipotenusa (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(formatearNumero(esquineroHipotenusaCm))
        }
        val etLado1 = android.widget.EditText(this).apply {
            hint = "Lado 1 (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(formatearNumero(esquineroLado1Cm))
        }
        val etLado2 = android.widget.EditText(this).apply {
            hint = "Lado 2 (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(formatearNumero(esquineroLado2Cm))
        }
        val rgDirection = android.widget.RadioGroup(this).apply {
            orientation = android.widget.RadioGroup.VERTICAL
        }
        val rbInterior = android.widget.RadioButton(this).apply {
            text = "Aleta hacia interior"
            id = View.generateViewId()
        }
        val rbExterior = android.widget.RadioButton(this).apply {
            text = "Aleta hacia exterior"
            id = View.generateViewId()
        }
        rgDirection.addView(rbInterior)
        rgDirection.addView(rbExterior)
        rgDirection.check(
            if (esquineroAletaDirection == RectanglesDrawingView.AletaDirection.INTERIOR) rbInterior.id else rbExterior.id
        )
        val rgSide = android.widget.RadioGroup(this).apply {
            orientation = android.widget.RadioGroup.VERTICAL
        }
        val rbDerecha = android.widget.RadioButton(this).apply {
            text = "Aleta al lado derecho"
            id = View.generateViewId()
        }
        val rbIzquierda = android.widget.RadioButton(this).apply {
            text = "Aleta al lado izquierdo"
            id = View.generateViewId()
        }
        rgSide.addView(rbDerecha)
        rgSide.addView(rbIzquierda)
        rgSide.check(
            if (esquineroAletaSide == RectanglesDrawingView.AletaSide.RIGHT) rbDerecha.id else rbIzquierda.id
        )
        val rgTipo = android.widget.RadioGroup(this).apply {
            orientation = android.widget.RadioGroup.VERTICAL
        }
        val rbTipoRecto = android.widget.RadioButton(this).apply {
            text = "L recto"
            id = View.generateViewId()
        }
        val rbTipoArco = android.widget.RadioButton(this).apply {
            text = "L con arco"
            id = View.generateViewId()
        }
        val rbTipoArcoRecto = android.widget.RadioButton(this).apply {
            text = "L con arco + recto"
            id = View.generateViewId()
        }
        rgTipo.addView(rbTipoRecto)
        rgTipo.addView(rbTipoArco)
        rgTipo.addView(rbTipoArcoRecto)
        rgTipo.check(
            when (esquineroVariantDesdeGeometria()) {
                RectanglesDrawingView.EsquineroVariant.ARCO -> rbTipoArco.id
                RectanglesDrawingView.EsquineroVariant.ARCO_RECTO -> rbTipoArcoRecto.id
                RectanglesDrawingView.EsquineroVariant.RECTO -> rbTipoRecto.id
            }
        )
        val tvTopCanvas = android.widget.TextView(this).apply {
            text = "Lienzo top: dibuja desde el centro hacia el cuadrante deseado."
            textSize = 13f
        }
        val topCanvas = TopPlanCanvasView(this).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                (220f * resources.displayMetrics.density).toInt()
            )
            setSelection(esquineroAletaSide, esquineroAletaDirection)
            onSelectionChanged = { sel ->
                rgSide.check(if (sel.side == RectanglesDrawingView.AletaSide.RIGHT) rbDerecha.id else rbIzquierda.id)
                rgDirection.check(if (sel.direction == RectanglesDrawingView.AletaDirection.INTERIOR) rbInterior.id else rbExterior.id)
            }
        }
        container.addView(tvAyuda)
        container.addView(cbUsar345)
        container.addView(etHipotenusa)
        container.addView(etLado1)
        container.addView(etLado2)
        container.addView(rgTipo)
        container.addView(tvTopCanvas)
        container.addView(topCanvas)
        container.addView(rgDirection)
        container.addView(rgSide)

        AlertDialog.Builder(this)
            .setTitle("Geometría esquinero")
            .setView(container)
            .setPositiveButton("Aplicar") { _, _ ->
                if (!restaurandoEstadoTemporal) registrarEstadoParaDeshacer()

                val hip = etHipotenusa.text?.toString()?.toFloatOrNull() ?: esquineroHipotenusaCm
                esquineroUsar345 = cbUsar345.isChecked
                val aplicado = if (esquineroUsar345) {
                    val h = hip.coerceAtLeast(1f)
                    val ok = binding.rectanglesDrawingView.setEsquineroByHypotenuse345(h)
                    if (ok) esquineroHipotenusaCm = h
                    ok
                } else {
                    val l1 = etLado1.text?.toString()?.toFloatOrNull() ?: esquineroLado1Cm
                    val l2 = etLado2.text?.toString()?.toFloatOrNull() ?: esquineroLado2Cm
                    val h = hip.coerceAtLeast(1f)
                    val ok = binding.rectanglesDrawingView.setEsquineroTriangle(l1, l2, h)
                    if (ok) {
                        esquineroLado1Cm = l1
                        esquineroLado2Cm = l2
                        esquineroHipotenusaCm = h
                    }
                    ok
                }

                if (!aplicado) {
                    Toast.makeText(this, "Triángulo inválido para calcular ángulo", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                esquineroAletaDirection = if (rgDirection.checkedRadioButtonId == rbExterior.id) {
                    RectanglesDrawingView.AletaDirection.EXTERIOR
                } else {
                    RectanglesDrawingView.AletaDirection.INTERIOR
                }
                esquineroAletaSide = if (rgSide.checkedRadioButtonId == rbIzquierda.id) {
                    RectanglesDrawingView.AletaSide.LEFT
                } else {
                    RectanglesDrawingView.AletaSide.RIGHT
                }
                val esquineroVariant = when (rgTipo.checkedRadioButtonId) {
                    rbTipoArco.id -> RectanglesDrawingView.EsquineroVariant.ARCO
                    rbTipoArcoRecto.id -> RectanglesDrawingView.EsquineroVariant.ARCO_RECTO
                    else -> RectanglesDrawingView.EsquineroVariant.RECTO
                }
                binding.rectanglesDrawingView.setEsquineroAletaDirection(esquineroAletaDirection)
                binding.rectanglesDrawingView.setEsquineroAletaSide(esquineroAletaSide)
                binding.rectanglesDrawingView.setEsquineroVariant(esquineroVariant)
                geometria = when (esquineroVariant) {
                    RectanglesDrawingView.EsquineroVariant.ARCO -> "esquinero_arco"
                    RectanglesDrawingView.EsquineroVariant.ARCO_RECTO -> "esquinero_arco_recto"
                    RectanglesDrawingView.EsquineroVariant.RECTO -> "esquinero"
                }
                binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.ESQUINERO)
                actualizarRectanguloDesdePanelMedidas()
                actualizarClienteHeaderDesdePanel()
                ocultarPanelesTemporales()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoGeometriaCurvo() {
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 18, 32, 0)
        }

        val tvInfo = android.widget.TextView(this).apply {
            text = "Ingresa desarrollo y cuerda. Flecha es opcional."
            textSize = 14f
            setPadding(0, 0, 0, 10)
        }
        container.addView(tvInfo)

        val etDesarrollo = android.widget.EditText(this).apply {
            hint = "Desarrollo (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(curvoDesarrolloCm))
        }
        val etCuerda = android.widget.EditText(this).apply {
            hint = "Cuerda (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(curvoCuerdaCm))
        }
        val etFlecha = android.widget.EditText(this).apply {
            hint = "Flecha opcional (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(curvoFlechaCm?.let { formatearNumero(it) }.orEmpty())
        }
        container.addView(etDesarrollo)
        container.addView(etCuerda)
        container.addView(etFlecha)

        val rgDirection = android.widget.RadioGroup(this).apply {
            orientation = android.widget.RadioGroup.VERTICAL
        }
        val rbInterior = android.widget.RadioButton(this).apply {
            id = View.generateViewId()
            text = "Dirección interior"
        }
        val rbExterior = android.widget.RadioButton(this).apply {
            id = View.generateViewId()
            text = "Dirección exterior"
        }
        rgDirection.addView(rbInterior)
        rgDirection.addView(rbExterior)
        rgDirection.check(if (curvoDirection == RectanglesDrawingView.AletaDirection.INTERIOR) rbInterior.id else rbExterior.id)
        container.addView(rgDirection)

        AlertDialog.Builder(this)
            .setTitle("Configurar geometría curvo")
            .setView(container)
            .setPositiveButton("Aplicar") { _, _ ->
                if (!restaurandoEstadoTemporal) registrarEstadoParaDeshacer()
                val desarrollo = etDesarrollo.text?.toString()?.toFloatOrNull()
                val cuerda = etCuerda.text?.toString()?.toFloatOrNull()
                val flecha = etFlecha.text?.toString()?.trim().orEmpty().ifBlank { null }?.toFloatOrNull()
                val direccion = if (rgDirection.checkedRadioButtonId == rbInterior.id) {
                    RectanglesDrawingView.AletaDirection.INTERIOR
                } else {
                    RectanglesDrawingView.AletaDirection.EXTERIOR
                }
                if (desarrollo == null || cuerda == null) {
                    Toast.makeText(this, "Ingresa desarrollo y cuerda válidos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val aplicada = binding.rectanglesDrawingView.setCurvoConfig(
                    RectanglesDrawingView.CurvoConfig(
                        desarrolloCm = desarrollo,
                        cuerdaCm = cuerda,
                        flechaCm = flecha,
                        direction = direccion,
                        flechaEfectivaCm = 0f
                    )
                )
                if (!aplicada) {
                    Toast.makeText(this, "Datos de curvo inválidos. Verifica que desarrollo sea mayor o igual a cuerda", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val cfg = binding.rectanglesDrawingView.getCurvoConfig()
                curvoDesarrolloCm = cfg.desarrolloCm
                curvoCuerdaCm = cfg.cuerdaCm
                curvoFlechaCm = cfg.flechaCm
                curvoDirection = cfg.direction
                geometria = "curvo"
                binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.CURVO)
                setTextoSiCambio(binding.etMedidaSuperiorPanel, formatearNumero(curvoCuerdaCm))
                setTextoSiCambio(binding.etMedidaInferiorPanel, formatearNumero(curvoCuerdaCm))
                actualizarRectanguloDesdePanelMedidas()
                actualizarClienteHeaderDesdePanel()
                ocultarPanelesTemporales()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoAgregarAletaMultiLado() {
        binding.rectanglesDrawingView.setMultiDialogTopGuideVisible(true)
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 18, 32, 0)
        }
        val isFirstAleta = geometria != "multi_lado" || multiLadoAletas.isEmpty()

        val tvAyuda = android.widget.TextView(this).apply {
            text = if (isFirstAleta) {
                "Primera aleta: usa interior/exterior. Las siguientes se generan desde el canto anterior. Si Lado 1, Lado 2 o Hipotenusa va en negativo, se usa ángulo suplementario (180-θ). Puedes tocar las cotas de la aleta para editar ancho/alto."
            } else {
                "Siguiente aleta: elige una sola dirección (arriba, abajo, derecha o izquierda) sobre el canto anterior. Si Lado 1, Lado 2 o Hipotenusa va en negativo, se usa ángulo suplementario (180-θ). Puedes tocar las cotas de la aleta para editar ancho/alto."
            }
            textSize = 14f
        }
        val cbUsar345 = android.widget.CheckBox(this).apply {
            text = "Editar solo hipotenusa (mantener lado 1 y lado 2)"
            isChecked = esquineroUsar345
        }
        val etHipotenusa = android.widget.EditText(this).apply {
            hint = "Hipotenusa (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(formatearNumero(esquineroHipotenusaCm))
        }
        val etLado1 = android.widget.EditText(this).apply {
            hint = "Lado 1 (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(formatearNumero(esquineroLado1Cm))
        }
        val etLado2 = android.widget.EditText(this).apply {
            hint = "Lado 2 (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(formatearNumero(esquineroLado2Cm))
        }
        val cbSuplementario = android.widget.CheckBox(this).apply {
            text = "Usar angulo suplementario (medida negativa)"
            isChecked = (esquineroLado1Cm < 0f || esquineroLado2Cm < 0f || esquineroHipotenusaCm < 0f)
        }
        val rgDirection = android.widget.RadioGroup(this).apply {
            orientation = android.widget.RadioGroup.VERTICAL
        }
        val lastCfg = multiLadoAletas.lastOrNull()
        val baseSideForNext = lastCfg?.side ?: esquineroAletaSide
        val baseDirectionForNext = lastCfg?.direction ?: esquineroAletaDirection
        val rbInterior = android.widget.RadioButton(this).apply {
            text = if (isFirstAleta) "Aleta hacia interior" else "Aleta hacia abajo"
            id = View.generateViewId()
        }
        val rbExterior = android.widget.RadioButton(this).apply {
            text = if (isFirstAleta) "Aleta hacia exterior" else "Aleta hacia arriba"
            id = View.generateViewId()
        }
        val rbDirDerecha = android.widget.RadioButton(this).apply {
            text = "Aleta hacia derecha"
            id = View.generateViewId()
        }
        val rbDirIzquierda = android.widget.RadioButton(this).apply {
            text = "Aleta hacia izquierda"
            id = View.generateViewId()
        }
        rgDirection.addView(rbInterior)
        rgDirection.addView(rbExterior)
        if (!isFirstAleta) {
            rgDirection.addView(rbDirDerecha)
            rgDirection.addView(rbDirIzquierda)
        }
        rgDirection.check(
            if (baseDirectionForNext == RectanglesDrawingView.AletaDirection.INTERIOR) rbInterior.id else rbExterior.id
        )
        val rgSide = android.widget.RadioGroup(this).apply {
            orientation = android.widget.RadioGroup.VERTICAL
        }
        val rbDerecha = android.widget.RadioButton(this).apply {
            text = "Aleta al lado derecho"
            id = View.generateViewId()
        }
        val rbIzquierda = android.widget.RadioButton(this).apply {
            text = "Aleta al lado izquierdo"
            id = View.generateViewId()
        }
        rgSide.addView(rbDerecha)
        rgSide.addView(rbIzquierda)
        rgSide.check(
            if (esquineroAletaSide == RectanglesDrawingView.AletaSide.RIGHT) rbDerecha.id else rbIzquierda.id
        )
        val tvTopCanvas = android.widget.TextView(this).apply {
            text = "Lienzo top: dibuja la direccion de la siguiente aleta."
            textSize = 13f
        }
        val topCanvas = TopPlanCanvasView(this).apply {
            layoutParams = android.widget.LinearLayout.LayoutParams(
                android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
                (220f * resources.displayMetrics.density).toInt()
            )
            setSelection(baseSideForNext, baseDirectionForNext)
            onSelectionChanged = { sel ->
                if (isFirstAleta) {
                    rgSide.check(if (sel.side == RectanglesDrawingView.AletaSide.RIGHT) rbDerecha.id else rbIzquierda.id)
                    rgDirection.check(if (sel.direction == RectanglesDrawingView.AletaDirection.INTERIOR) rbInterior.id else rbExterior.id)
                } else {
                    if (sel.horizontalDominant) {
                        rgDirection.check(if (sel.side == RectanglesDrawingView.AletaSide.RIGHT) rbDirDerecha.id else rbDirIzquierda.id)
                    } else {
                        rgDirection.check(if (sel.direction == RectanglesDrawingView.AletaDirection.INTERIOR) rbInterior.id else rbExterior.id)
                    }
                }
            }
        }
        container.addView(tvAyuda)
        container.addView(cbUsar345)
        container.addView(etHipotenusa)
        container.addView(etLado1)
        container.addView(etLado2)
        container.addView(cbSuplementario)
        container.addView(tvTopCanvas)
        container.addView(topCanvas)
        container.addView(rgDirection)
        if (isFirstAleta) {
            container.addView(rgSide)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Agregar aleta múltiple lado")
            .setView(container)
            .setPositiveButton("Aplicar") { _, _ ->
                if (!restaurandoEstadoTemporal) registrarEstadoParaDeshacer()

                val hip = etHipotenusa.text?.toString()?.toFloatOrNull() ?: esquineroHipotenusaCm
                esquineroUsar345 = cbUsar345.isChecked
                val (rawL1, rawL2, rawH) = if (esquineroUsar345) {
                    Triple(esquineroLado1Cm, esquineroLado2Cm, hip)
                } else {
                    val nL1 = etLado1.text?.toString()?.toFloatOrNull() ?: esquineroLado1Cm
                    val nL2 = etLado2.text?.toString()?.toFloatOrNull() ?: esquineroLado2Cm
                    Triple(nL1, nL2, hip)
                }
                val l1 = rawL1
                val l2 = rawL2
                val h = if (cbSuplementario.isChecked) -abs(rawH) else abs(rawH)

                val a1 = abs(l1)
                val a2 = abs(l2)
                val ah = abs(h)
                val trianguloValido = (a1 > 0f && a2 > 0f && ah > 0f) &&
                    (a1 + a2 > ah) && (a1 + ah > a2) && (a2 + ah > a1)
                if (!trianguloValido) {
                    Toast.makeText(this, "Triángulo inválido para calcular ángulo", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                esquineroLado1Cm = l1
                esquineroLado2Cm = l2
                esquineroHipotenusaCm = h
                if (isFirstAleta) {
                    esquineroAletaDirection = if (rgDirection.checkedRadioButtonId == rbExterior.id) {
                        RectanglesDrawingView.AletaDirection.EXTERIOR
                    } else {
                        RectanglesDrawingView.AletaDirection.INTERIOR
                    }
                    esquineroAletaSide = if (rgSide.checkedRadioButtonId == rbIzquierda.id) {
                        RectanglesDrawingView.AletaSide.LEFT
                    } else {
                        RectanglesDrawingView.AletaSide.RIGHT
                    }
                } else {
                    when (rgDirection.checkedRadioButtonId) {
                        rbExterior.id -> {
                            esquineroAletaDirection = RectanglesDrawingView.AletaDirection.EXTERIOR
                            esquineroAletaSide = baseSideForNext
                        }
                        rbInterior.id -> {
                            esquineroAletaDirection = RectanglesDrawingView.AletaDirection.INTERIOR
                            esquineroAletaSide = baseSideForNext
                        }
                        rbDirIzquierda.id -> {
                            esquineroAletaSide = RectanglesDrawingView.AletaSide.LEFT
                            esquineroAletaDirection = baseDirectionForNext
                        }
                        else -> {
                            esquineroAletaSide = RectanglesDrawingView.AletaSide.RIGHT
                            esquineroAletaDirection = baseDirectionForNext
                        }
                    }
                }

                if (geometria != "multi_lado") {
                    multiLadoAletas.clear()
                }
                multiLadoAletas.add(
                    MultiLadoAletaEstado(
                        side = esquineroAletaSide,
                        direction = esquineroAletaDirection,
                        lado1Cm = esquineroLado1Cm,
                        lado2Cm = esquineroLado2Cm,
                        hipotenusaCm = esquineroHipotenusaCm,
                        aletaAnchoCm = esquineroAletaAnchoCm,
                        aletaAltoCm = esquineroAletaAltoCm
                    )
                )
                multiLadoCount = multiLadoAletas.size
                multiLadoSide = multiLadoAletas.firstOrNull()?.side ?: esquineroAletaSide
                multiLadoDirection = multiLadoAletas.firstOrNull()?.direction ?: esquineroAletaDirection
                multiLadoChain = true

                binding.rectanglesDrawingView.setMultiLadoConfig(
                    RectanglesDrawingView.MultiLadoConfig(
                        count = multiLadoCount,
                        side = multiLadoSide,
                        direction = multiLadoDirection,
                        chainSameDirection = true
                    )
                )
                binding.rectanglesDrawingView.setMultiLadoAletas(
                    multiLadoAletas.map {
                        RectanglesDrawingView.MultiLadoAletaConfig(
                            side = it.side,
                            direction = it.direction,
                            lado1Cm = it.lado1Cm,
                            lado2Cm = it.lado2Cm,
                            hipotenusaCm = it.hipotenusaCm,
                            aletaAnchoCm = it.aletaAnchoCm,
                            aletaAltoCm = it.aletaAltoCm
                        )
                    }
                )
                geometria = "multi_lado"
                binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.MULTI_LADO)
                actualizarRectanguloDesdePanelMedidas()
                actualizarClienteHeaderDesdePanel()
                ocultarPanelesTemporales()
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.setOnShowListener {
            dialog.window?.setGravity(Gravity.BOTTOM)
            dialog.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
        dialog.setOnDismissListener {
            binding.rectanglesDrawingView.setMultiDialogTopGuideVisible(false)
        }
        dialog.show()
    }

    private fun mostrarDialogoGeometriaCTop() {
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 18, 32, 0)
        }

        val tvAyuda = android.widget.TextView(this).apply {
            text = "Configura triángulo para hallar ángulo de eje (Pitágoras 3-4-5) y dirección de aletas."
            textSize = 14f
        }
        val tvDer = android.widget.TextView(this).apply { text = "Aleta derecha"; textSize = 15f }
        val etDerL1 = android.widget.EditText(this).apply {
            hint = "Der - Lado 1 (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(cTopRightLado1Cm))
        }
        val etDerL2 = android.widget.EditText(this).apply {
            hint = "Der - Lado 2 (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(cTopRightLado2Cm))
        }
        val etDerHip = android.widget.EditText(this).apply {
            hint = "Der - Hipotenusa (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(cTopRightHipotenusaCm))
        }

        val tvIzq = android.widget.TextView(this).apply { text = "Aleta izquierda"; textSize = 15f }
        val etIzqL1 = android.widget.EditText(this).apply {
            hint = "Izq - Lado 1 (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(cTopLeftLado1Cm))
        }
        val etIzqL2 = android.widget.EditText(this).apply {
            hint = "Izq - Lado 2 (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(cTopLeftLado2Cm))
        }
        val etIzqHip = android.widget.EditText(this).apply {
            hint = "Izq - Hipotenusa (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(cTopLeftHipotenusaCm))
        }
        val rgDirection = android.widget.RadioGroup(this).apply {
            orientation = android.widget.RadioGroup.VERTICAL
        }
        val rbInterior = android.widget.RadioButton(this).apply {
            text = "Aletas hacia interior"
            id = View.generateViewId()
        }
        val rbExterior = android.widget.RadioButton(this).apply {
            text = "Aletas hacia exterior"
            id = View.generateViewId()
        }
        rgDirection.addView(rbInterior)
        rgDirection.addView(rbExterior)
        rgDirection.check(
            if (esquineroAletaDirection == RectanglesDrawingView.AletaDirection.INTERIOR) rbInterior.id else rbExterior.id
        )
        container.addView(tvAyuda)
        container.addView(tvDer)
        container.addView(etDerL1)
        container.addView(etDerL2)
        container.addView(etDerHip)
        container.addView(tvIzq)
        container.addView(etIzqL1)
        container.addView(etIzqL2)
        container.addView(etIzqHip)
        container.addView(rgDirection)

        AlertDialog.Builder(this)
            .setTitle("Geometría Top en C")
            .setView(container)
            .setPositiveButton("Aplicar") { _, _ ->
                if (!restaurandoEstadoTemporal) registrarEstadoParaDeshacer()

                val derConfig = RectanglesDrawingView.CTopAletaConfig(
                    lado1Cm = (etDerL1.text?.toString()?.toFloatOrNull() ?: cTopRightLado1Cm).coerceAtLeast(1f),
                    lado2Cm = (etDerL2.text?.toString()?.toFloatOrNull() ?: cTopRightLado2Cm).coerceAtLeast(1f),
                    hipotenusaCm = (etDerHip.text?.toString()?.toFloatOrNull() ?: cTopRightHipotenusaCm).coerceAtLeast(1f),
                    anguloDeg = 0f,
                    aletaAnchoCm = cTopRightAletaAnchoCm,
                    aletaAltoCm = cTopRightAletaAltoCm
                )
                val izqConfig = RectanglesDrawingView.CTopAletaConfig(
                    lado1Cm = (etIzqL1.text?.toString()?.toFloatOrNull() ?: cTopLeftLado1Cm).coerceAtLeast(1f),
                    lado2Cm = (etIzqL2.text?.toString()?.toFloatOrNull() ?: cTopLeftLado2Cm).coerceAtLeast(1f),
                    hipotenusaCm = (etIzqHip.text?.toString()?.toFloatOrNull() ?: cTopLeftHipotenusaCm).coerceAtLeast(1f),
                    anguloDeg = 0f,
                    aletaAnchoCm = cTopLeftAletaAnchoCm,
                    aletaAltoCm = cTopLeftAletaAltoCm
                )
                val okDer = binding.rectanglesDrawingView.setCTopAletaConfig(RectanglesDrawingView.AletaSide.RIGHT, derConfig)
                val okIzq = binding.rectanglesDrawingView.setCTopAletaConfig(RectanglesDrawingView.AletaSide.LEFT, izqConfig)
                if (!okDer || !okIzq) {
                    Toast.makeText(this, "Triángulo inválido en una de las aletas", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                cTopRightLado1Cm = derConfig.lado1Cm
                cTopRightLado2Cm = derConfig.lado2Cm
                cTopRightHipotenusaCm = derConfig.hipotenusaCm
                cTopLeftLado1Cm = izqConfig.lado1Cm
                cTopLeftLado2Cm = izqConfig.lado2Cm
                cTopLeftHipotenusaCm = izqConfig.hipotenusaCm
                esquineroAletaDirection = if (rgDirection.checkedRadioButtonId == rbExterior.id) {
                    RectanglesDrawingView.AletaDirection.EXTERIOR
                } else {
                    RectanglesDrawingView.AletaDirection.INTERIOR
                }
                binding.rectanglesDrawingView.setEsquineroAletaDirection(esquineroAletaDirection)
                geometria = "c_top"
                binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.C_TOP)
                actualizarRectanguloDesdePanelMedidas()
                actualizarClienteHeaderDesdePanel()
                ocultarPanelesTemporales()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoGeometriaMultiLado() {
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 18, 32, 0)
        }

        val tvAyuda = android.widget.TextView(this).apply {
            text = "Agrega o edita una aleta por índice. Si dos consecutivas tienen la misma dirección, se encadenan desde el canto anterior. Si Lado 1, Lado 2 o Hipotenusa va en negativo, se usa ángulo suplementario (180-θ). También puedes tocar las cotas en el dibujo para editar ancho/alto."
            textSize = 14f
        }
        val etIndex = android.widget.EditText(this).apply {
            hint = "Índice de aleta (1..n)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            setText((multiLadoAletas.size + 1).toString())
        }
        val cbEditar = android.widget.CheckBox(this).apply {
            text = "Editar índice existente (si no, agrega nuevo)"
            isChecked = false
        }
        val rgSide = android.widget.RadioGroup(this).apply { orientation = android.widget.RadioGroup.VERTICAL }
        val rbDer = android.widget.RadioButton(this).apply { id = View.generateViewId(); text = "Lado derecho" }
        val rbIzq = android.widget.RadioButton(this).apply { id = View.generateViewId(); text = "Lado izquierdo" }
        rgSide.addView(rbDer)
        rgSide.addView(rbIzq)
        rgSide.check(rbDer.id)

        val rgDirection = android.widget.RadioGroup(this).apply { orientation = android.widget.RadioGroup.VERTICAL }
        val rbInt = android.widget.RadioButton(this).apply { id = View.generateViewId(); text = "Dirección interior" }
        val rbExt = android.widget.RadioButton(this).apply { id = View.generateViewId(); text = "Dirección exterior" }
        rgDirection.addView(rbInt)
        rgDirection.addView(rbExt)
        rgDirection.check(rbInt.id)

        val etL1 = android.widget.EditText(this).apply {
            hint = "Lado 1 (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(formatearNumero(esquineroLado1Cm))
        }
        val etL2 = android.widget.EditText(this).apply {
            hint = "Lado 2 (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(formatearNumero(esquineroLado2Cm))
        }
        val etHip = android.widget.EditText(this).apply {
            hint = "Hipotenusa (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or
                android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL or
                android.text.InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(formatearNumero(esquineroHipotenusaCm))
        }
        val etAncho = android.widget.EditText(this).apply {
            hint = "Ancho aleta (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(esquineroAletaAnchoCm))
        }
        val etAlto = android.widget.EditText(this).apply {
            hint = "Alto aleta (cm)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatearNumero(esquineroAletaAltoCm))
        }

        container.addView(tvAyuda)
        container.addView(etIndex)
        container.addView(cbEditar)
        container.addView(rgSide)
        container.addView(rgDirection)
        container.addView(etL1)
        container.addView(etL2)
        container.addView(etHip)
        container.addView(etAncho)
        container.addView(etAlto)

        AlertDialog.Builder(this)
            .setTitle("Geometría múltiple lado")
            .setView(container)
            .setPositiveButton("Aplicar") { _, _ ->
                if (!restaurandoEstadoTemporal) registrarEstadoParaDeshacer()
                val idx = (etIndex.text?.toString()?.toIntOrNull() ?: 1).coerceAtLeast(1)
                val side = if (rgSide.checkedRadioButtonId == rbIzq.id) {
                    RectanglesDrawingView.AletaSide.LEFT
                } else {
                    RectanglesDrawingView.AletaSide.RIGHT
                }
                val direction = if (rgDirection.checkedRadioButtonId == rbExt.id) {
                    RectanglesDrawingView.AletaDirection.EXTERIOR
                } else {
                    RectanglesDrawingView.AletaDirection.INTERIOR
                }
                val item = MultiLadoAletaEstado(
                    side = side,
                    direction = direction,
                    lado1Cm = (etL1.text?.toString()?.toFloatOrNull() ?: esquineroLado1Cm),
                    lado2Cm = (etL2.text?.toString()?.toFloatOrNull() ?: esquineroLado2Cm),
                    hipotenusaCm = (etHip.text?.toString()?.toFloatOrNull() ?: esquineroHipotenusaCm),
                    aletaAnchoCm = (etAncho.text?.toString()?.toFloatOrNull() ?: esquineroAletaAnchoCm).coerceAtLeast(1f),
                    aletaAltoCm = (etAlto.text?.toString()?.toFloatOrNull() ?: esquineroAletaAltoCm).coerceAtLeast(1f)
                )
                val editMode = cbEditar.isChecked && idx <= multiLadoAletas.size
                if (editMode) {
                    multiLadoAletas[idx - 1] = item
                } else {
                    if (idx - 1 in 0 until multiLadoAletas.size) {
                        multiLadoAletas.add(idx - 1, item)
                    } else {
                        multiLadoAletas.add(item)
                    }
                }
                multiLadoCount = multiLadoAletas.size.coerceAtLeast(1)
                multiLadoSide = multiLadoAletas.firstOrNull()?.side ?: multiLadoSide
                multiLadoDirection = multiLadoAletas.firstOrNull()?.direction ?: multiLadoDirection
                multiLadoChain = true
                binding.rectanglesDrawingView.setMultiLadoConfig(
                    RectanglesDrawingView.MultiLadoConfig(
                        count = multiLadoCount,
                        side = multiLadoSide,
                        direction = multiLadoDirection,
                        chainSameDirection = multiLadoChain
                    )
                )
                binding.rectanglesDrawingView.setMultiLadoAletas(
                    multiLadoAletas.map {
                        RectanglesDrawingView.MultiLadoAletaConfig(
                            side = it.side,
                            direction = it.direction,
                            lado1Cm = it.lado1Cm,
                            lado2Cm = it.lado2Cm,
                            hipotenusaCm = it.hipotenusaCm,
                            aletaAnchoCm = it.aletaAnchoCm,
                            aletaAltoCm = it.aletaAltoCm
                        )
                    }
                )
                geometria = "multi_lado"
                binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.MULTI_LADO)
                actualizarRectanguloDesdePanelMedidas()
                actualizarClienteHeaderDesdePanel()
                ocultarPanelesTemporales()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setTextoSiCambio(editText: android.widget.EditText, nuevo: String) {
        if (editText.text?.toString() == nuevo) return
        editText.setText(nuevo)
        editText.setSelection(editText.text?.length ?: 0)
    }

    private fun inicializarCamposMedidasSiVacios() {
        actualizandoPanelMedidas = true
        if (binding.etMedidaSuperiorPanel.text?.toString().isNullOrBlank()) binding.etMedidaSuperiorPanel.setText("120")
        if (binding.etMedidaDerechaPanel.text?.toString().isNullOrBlank()) binding.etMedidaDerechaPanel.setText("90")
        if (binding.etMedidaInferiorPanel.text?.toString().isNullOrBlank()) binding.etMedidaInferiorPanel.setText("120")
        if (binding.etMedidaIzquierdaPanel.text?.toString().isNullOrBlank()) binding.etMedidaIzquierdaPanel.setText("90")
        if (binding.etDistPisoPanel.text?.toString().isNullOrBlank()) binding.etDistPisoPanel.setText("0")
        if (binding.etDistTechoPanel.text?.toString().isNullOrBlank()) binding.etDistTechoPanel.setText("0")
        actualizandoPanelMedidas = false
    }

    private fun formatearNumero(valor: Float): String {
        return if (valor % 1f == 0f) valor.toInt().toString() else String.format(Locale.US, "%.1f", valor)
    }

    private fun calcularAnguloTriangulo(lado1: Float, lado2: Float, hipotenusa: Float): Float {
        val usarSuplementario = lado1 < 0f || lado2 < 0f || hipotenusa < 0f
        val a = abs(lado1)
        val b = abs(lado2)
        val c = abs(hipotenusa)
        val den = (2f * a * b).coerceAtLeast(0.0001f)
        val num = (a * a) + (b * b) - (c * c)
        val cosTheta = (num / den).coerceIn(-1f, 1f)
        val anguloBase = Math.toDegrees(acos(cosTheta).toDouble()).toFloat()
        return if (usarSuplementario) 180f - anguloBase else anguloBase
    }

    private fun configurarCapturaUndoPorFoco(vararg campos: android.widget.EditText) {
        campos.forEach { campo ->
            campo.setOnFocusChangeListener { _, hasFocus ->
                if (!hasFocus || restaurandoEstadoTemporal) return@setOnFocusChangeListener
                registrarEstadoParaDeshacer()
            }
        }
    }

    private fun registrarEstadoParaDeshacer() {
        if (restaurandoEstadoTemporal) return
        val actual = capturarEstadoActual()
        if (historialEstados.lastOrNull() == actual) return
        historialEstados.addLast(actual)
        while (historialEstados.size > 100) {
            historialEstados.removeFirstOrNull()
        }
        actualizarEstadoBotonDeshacer()
    }

    private fun registrarEstadoParaDeshacerConCotaAnterior(
        side: RectanglesDrawingView.Side,
        previousValue: Float
    ) {
        if (restaurandoEstadoTemporal) return
        val anteriorTexto = formatearNumero(previousValue)
        val actual = capturarEstadoActual()
        val estadoAnterior = when (side) {
            RectanglesDrawingView.Side.TOP -> actual.copy(superior = anteriorTexto)
            RectanglesDrawingView.Side.RIGHT -> actual.copy(derecha = anteriorTexto)
            RectanglesDrawingView.Side.BOTTOM -> actual.copy(inferior = anteriorTexto)
            RectanglesDrawingView.Side.LEFT -> actual.copy(izquierda = anteriorTexto)
        }
        if (historialEstados.lastOrNull() == estadoAnterior) return
        historialEstados.addLast(estadoAnterior)
        while (historialEstados.size > 100) {
            historialEstados.removeFirstOrNull()
        }
        actualizarEstadoBotonDeshacer()
    }

    private fun registrarEstadoParaDeshacerConAletaAnterior(previousAncho: Float?, previousAlto: Float?) {
        if (restaurandoEstadoTemporal) return
        val actual = capturarEstadoActual()
        val estadoAnterior = actual.copy(
            esquineroAletaAncho = previousAncho ?: actual.esquineroAletaAncho,
            esquineroAletaAlto = previousAlto ?: actual.esquineroAletaAlto,
            esquineroUsar345 = false
        )
        if (historialEstados.lastOrNull() == estadoAnterior) return
        historialEstados.addLast(estadoAnterior)
        while (historialEstados.size > 100) {
            historialEstados.removeFirstOrNull()
        }
        actualizarEstadoBotonDeshacer()
    }

    private fun registrarEstadoParaDeshacerConMultiLadoAletaAnterior(
        index: Int,
        previousAncho: Float?,
        previousAlto: Float?
    ) {
        if (restaurandoEstadoTemporal) return
        val actual = capturarEstadoActual()
        if (index !in actual.multiLadoAletas.indices) return
        val listaAnterior = actual.multiLadoAletas.toMutableList()
        val item = listaAnterior[index]
        listaAnterior[index] = item.copy(
            aletaAnchoCm = previousAncho ?: item.aletaAnchoCm,
            aletaAltoCm = previousAlto ?: item.aletaAltoCm
        )
        val estadoAnterior = actual.copy(multiLadoAletas = listaAnterior)
        if (historialEstados.lastOrNull() == estadoAnterior) return
        historialEstados.addLast(estadoAnterior)
        while (historialEstados.size > 100) {
            historialEstados.removeFirstOrNull()
        }
        actualizarEstadoBotonDeshacer()
    }

    private fun deshacerUltimaAccion() {
        val actual = capturarEstadoActual()
        var estadoObjetivo: EstadoLienzoTemporal? = null
        while (historialEstados.isNotEmpty()) {
            val candidato = historialEstados.removeLastOrNull() ?: break
            if (candidato != actual) {
                estadoObjetivo = candidato
                break
            }
        }
        if (estadoObjetivo == null) {
            actualizarEstadoBotonDeshacer()
            return
        }
        aplicarEstado(estadoObjetivo)
        actualizarEstadoBotonDeshacer()
    }

    private fun actualizarEstadoBotonDeshacer() {
        val habilitado = historialEstados.isNotEmpty()
        binding.btnDeshacerAccion.isEnabled = habilitado
        binding.btnDeshacerAccion.alpha = if (habilitado) 1f else 0.5f
    }

    private fun capturarEstadoActual(): EstadoLienzoTemporal {
        val producto = when {
            binding.cbProductoVentanaPanel.isChecked -> RectanglesDrawingView.ProductType.VENTANA
            binding.cbProductoPuertaPanel.isChecked -> RectanglesDrawingView.ProductType.PUERTA
            binding.cbProductoMamparaPanel.isChecked -> RectanglesDrawingView.ProductType.MAMPARA
            else -> RectanglesDrawingView.ProductType.NONE
        }
        return EstadoLienzoTemporal(
            clienteNombre = binding.etClienteNombrePanel.text?.toString().orEmpty(),
            clienteDocumento = binding.etClienteDocumentoPanel.text?.toString().orEmpty(),
            clienteTelefono = binding.etClienteTelefonoPanel.text?.toString().orEmpty(),
            clienteDireccion = binding.etClienteDireccionPanel.text?.toString().orEmpty(),
            producto = producto,
            geometria = geometria,
            encuentro = encuentros,
            superior = binding.etMedidaSuperiorPanel.text?.toString().orEmpty(),
            derecha = binding.etMedidaDerechaPanel.text?.toString().orEmpty(),
            inferior = binding.etMedidaInferiorPanel.text?.toString().orEmpty(),
            izquierda = binding.etMedidaIzquierdaPanel.text?.toString().orEmpty(),
            distPiso = binding.etDistPisoPanel.text?.toString().orEmpty(),
            distTecho = binding.etDistTechoPanel.text?.toString().orEmpty(),
            acopleSides = acopleLadosSeleccionados.toSet(),
            acopleProducto = acopleProductoSeleccionado,
            acopleAncho = acopleAnchoBaseCm,
            acopleAlto = acopleAltoBaseCm,
            esquineroLado1 = esquineroLado1Cm,
            esquineroLado2 = esquineroLado2Cm,
            esquineroHipotenusa = esquineroHipotenusaCm,
            esquineroUsar345 = esquineroUsar345,
            esquineroAletaAncho = esquineroAletaAnchoCm,
            esquineroAletaAlto = esquineroAletaAltoCm,
            esquineroAletaDirection = esquineroAletaDirection,
            esquineroAletaSide = esquineroAletaSide,
            cTopRightLado1 = cTopRightLado1Cm,
            cTopRightLado2 = cTopRightLado2Cm,
            cTopRightHipotenusa = cTopRightHipotenusaCm,
            cTopRightAletaAncho = cTopRightAletaAnchoCm,
            cTopRightAletaAlto = cTopRightAletaAltoCm,
            cTopLeftLado1 = cTopLeftLado1Cm,
            cTopLeftLado2 = cTopLeftLado2Cm,
            cTopLeftHipotenusa = cTopLeftHipotenusaCm,
            cTopLeftAletaAncho = cTopLeftAletaAnchoCm,
            cTopLeftAletaAlto = cTopLeftAletaAltoCm,
            curvoDesarrollo = curvoDesarrolloCm,
            curvoCuerda = curvoCuerdaCm,
            curvoFlecha = curvoFlechaCm,
            curvoDirection = curvoDirection,
            multiLadoCount = multiLadoCount,
            multiLadoSide = multiLadoSide,
            multiLadoDirection = multiLadoDirection,
            multiLadoChain = multiLadoChain,
            multiLadoAletas = multiLadoAletas.toList()
        )
    }

    private fun aplicarEstado(estado: EstadoLienzoTemporal) {
        restaurandoEstadoTemporal = true
        actualizandoPanelMedidas = true

        binding.etClienteNombrePanel.setText(estado.clienteNombre)
        binding.etClienteDocumentoPanel.setText(estado.clienteDocumento)
        binding.etClienteTelefonoPanel.setText(estado.clienteTelefono)
        binding.etClienteDireccionPanel.setText(estado.clienteDireccion)

        binding.cbProductoVentanaPanel.isChecked = estado.producto == RectanglesDrawingView.ProductType.VENTANA
        binding.cbProductoPuertaPanel.isChecked = estado.producto == RectanglesDrawingView.ProductType.PUERTA
        binding.cbProductoMamparaPanel.isChecked = estado.producto == RectanglesDrawingView.ProductType.MAMPARA

        geometria = estado.geometria
        binding.etMedidaSuperiorPanel.setText(estado.superior)
        binding.etMedidaDerechaPanel.setText(estado.derecha)
        binding.etMedidaInferiorPanel.setText(estado.inferior)
        binding.etMedidaIzquierdaPanel.setText(estado.izquierda)
        binding.etDistPisoPanel.setText(estado.distPiso)
        binding.etDistTechoPanel.setText(estado.distTecho)

        encuentros = estado.encuentro
        acopleLadosSeleccionados.clear()
        acopleLadosSeleccionados.addAll(estado.acopleSides)
        acopleProductoSeleccionado = estado.acopleProducto
        acopleAnchoBaseCm = estado.acopleAncho
        acopleAltoBaseCm = estado.acopleAlto
        esquineroLado1Cm = estado.esquineroLado1
        esquineroLado2Cm = estado.esquineroLado2
        esquineroHipotenusaCm = estado.esquineroHipotenusa
        esquineroUsar345 = estado.esquineroUsar345
        esquineroAletaAnchoCm = estado.esquineroAletaAncho
        esquineroAletaAltoCm = estado.esquineroAletaAlto
        esquineroAletaDirection = estado.esquineroAletaDirection
        esquineroAletaSide = estado.esquineroAletaSide
        cTopRightLado1Cm = estado.cTopRightLado1
        cTopRightLado2Cm = estado.cTopRightLado2
        cTopRightHipotenusaCm = estado.cTopRightHipotenusa
        cTopRightAletaAnchoCm = estado.cTopRightAletaAncho
        cTopRightAletaAltoCm = estado.cTopRightAletaAlto
        cTopLeftLado1Cm = estado.cTopLeftLado1
        cTopLeftLado2Cm = estado.cTopLeftLado2
        cTopLeftHipotenusaCm = estado.cTopLeftHipotenusa
        cTopLeftAletaAnchoCm = estado.cTopLeftAletaAncho
        cTopLeftAletaAltoCm = estado.cTopLeftAletaAlto
        curvoDesarrolloCm = estado.curvoDesarrollo
        curvoCuerdaCm = estado.curvoCuerda
        curvoFlechaCm = estado.curvoFlecha
        curvoDirection = estado.curvoDirection
        multiLadoCount = estado.multiLadoCount
        multiLadoSide = estado.multiLadoSide
        multiLadoDirection = estado.multiLadoDirection
        multiLadoChain = estado.multiLadoChain
        multiLadoAletas.clear()
        multiLadoAletas.addAll(estado.multiLadoAletas)

        actualizandoPanelMedidas = false
        restaurandoEstadoTemporal = false

        actualizarRectanguloDesdePanelMedidas()
        when (geometria) {
            "esquinero", "esquinero_arco", "esquinero_arco_recto" -> {
                if (esquineroUsar345) {
                    binding.rectanglesDrawingView.setEsquineroByHypotenuse345(esquineroHipotenusaCm)
                } else {
                    binding.rectanglesDrawingView.setEsquineroTriangle(esquineroLado1Cm, esquineroLado2Cm, esquineroHipotenusaCm)
                }
                binding.rectanglesDrawingView.setEsquineroAletaSize(esquineroAletaAnchoCm, esquineroAletaAltoCm)
                binding.rectanglesDrawingView.setEsquineroAletaDirection(esquineroAletaDirection)
                binding.rectanglesDrawingView.setEsquineroAletaSide(esquineroAletaSide)
                binding.rectanglesDrawingView.setEsquineroVariant(esquineroVariantDesdeGeometria(geometria))
                binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.ESQUINERO)
            }
            "curvo" -> {
                binding.rectanglesDrawingView.setCurvoConfig(
                    RectanglesDrawingView.CurvoConfig(
                        desarrolloCm = curvoDesarrolloCm,
                        cuerdaCm = curvoCuerdaCm,
                        flechaCm = curvoFlechaCm,
                        direction = curvoDirection,
                        flechaEfectivaCm = 0f
                    )
                )
                binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.CURVO)
            }
            "c_top" -> {
                binding.rectanglesDrawingView.setCTopAletaConfig(
                    RectanglesDrawingView.AletaSide.RIGHT,
                    RectanglesDrawingView.CTopAletaConfig(
                        lado1Cm = cTopRightLado1Cm,
                        lado2Cm = cTopRightLado2Cm,
                        hipotenusaCm = cTopRightHipotenusaCm,
                        anguloDeg = 0f,
                        aletaAnchoCm = cTopRightAletaAnchoCm,
                        aletaAltoCm = cTopRightAletaAltoCm
                    )
                )
                binding.rectanglesDrawingView.setCTopAletaConfig(
                    RectanglesDrawingView.AletaSide.LEFT,
                    RectanglesDrawingView.CTopAletaConfig(
                        lado1Cm = cTopLeftLado1Cm,
                        lado2Cm = cTopLeftLado2Cm,
                        hipotenusaCm = cTopLeftHipotenusaCm,
                        anguloDeg = 0f,
                        aletaAnchoCm = cTopLeftAletaAnchoCm,
                        aletaAltoCm = cTopLeftAletaAltoCm
                    )
                )
                binding.rectanglesDrawingView.setEsquineroAletaDirection(esquineroAletaDirection)
                binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.C_TOP)
            }
            "multi_lado" -> {
                binding.rectanglesDrawingView.setMultiLadoConfig(
                    RectanglesDrawingView.MultiLadoConfig(
                        count = multiLadoCount,
                        side = multiLadoSide,
                        direction = multiLadoDirection,
                        chainSameDirection = multiLadoChain
                    )
                )
                binding.rectanglesDrawingView.setMultiLadoAletas(
                    multiLadoAletas.map {
                        RectanglesDrawingView.MultiLadoAletaConfig(
                            side = it.side,
                            direction = it.direction,
                            lado1Cm = it.lado1Cm,
                            lado2Cm = it.lado2Cm,
                            hipotenusaCm = it.hipotenusaCm,
                            aletaAnchoCm = it.aletaAnchoCm,
                            aletaAltoCm = it.aletaAltoCm
                        )
                    }
                )
                binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.MULTI_LADO)
            }
            else -> binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.PLANO)
        }
        when (encuentros) {
            "muro" -> binding.rectanglesDrawingView.setEncounterType(RectanglesDrawingView.EncounterType.MURO)
            "independiente" -> binding.rectanglesDrawingView.setEncounterType(RectanglesDrawingView.EncounterType.INDEPENDIENTE)
            "borde_libre" -> {
                if (acopleLadosSeleccionados.isNotEmpty()) {
                    binding.rectanglesDrawingView.setBordeLibreSides(acopleLadosSeleccionados)
                } else {
                    binding.rectanglesDrawingView.setEncounterType(RectanglesDrawingView.EncounterType.BORDE_LIBRE)
                }
            }
            "acoplado" -> {
                binding.rectanglesDrawingView.setAcopleConfiguration(acopleLadosSeleccionados, acopleProductoSeleccionado)
                binding.rectanglesDrawingView.setAcopleBaseSize(acopleAnchoBaseCm, acopleAltoBaseCm)
            }
            else -> binding.rectanglesDrawingView.setEncounterType(RectanglesDrawingView.EncounterType.MURO)
        }
        actualizarClienteHeaderDesdePanel()
    }

    override fun onResume() {
        super.onResume()
        continuarCalculoMasivoSiAplica()
    }

    override fun onPause() {
        super.onPause()
        guardarBorradorSiCorresponde()
    }

    private fun observarViewModel() {
        medicionViewModel.items.observe(this) { items ->
            itemsArchivados.clear()
            itemsArchivados.addAll(items)
            actualizarListaArchivados()
        }
        medicionViewModel.mensaje.observe(this) { msg ->
            if (!msg.isNullOrBlank()) mostrarMensaje(msg)
        }
    }

    private fun archivarItemActual(estadoDestino: EstadoMedicion) {
        val nombrePanel = binding.etClienteNombrePanel.text?.toString()?.trim().orEmpty()
        val documentoPanel = binding.etClienteDocumentoPanel.text?.toString()?.trim().orEmpty()
        val telefonoPanel = binding.etClienteTelefonoPanel.text?.toString()?.trim().orEmpty()
        val direccionPanel = binding.etClienteDireccionPanel.text?.toString()?.trim().orEmpty()
        val clienteNombreFinal = clienteSeleccionado?.nombreCompleto
            ?: nombrePanel.ifBlank { null }
        val clienteIdFinal = clienteSeleccionado?.id

        val numeroItem = itemIdEnEdicion
            ?.let { id -> itemsArchivados.firstOrNull { it.id == id }?.numero }
            ?: obtenerSiguienteNumeroCategoria(categoria)

        val now = System.currentTimeMillis()
        val item = ItemMedicionObra(
            id = itemIdEnEdicion ?: UUID.randomUUID().toString(),
            numero = numeroItem,
            clienteId = clienteIdFinal,
            clienteNombre = clienteNombreFinal,
            categoria = categoria,
            sistema = sistema,
            tipoApertura = tipoApertura,
            geometria = geometria,
            encuentros = encuentros,
            modelo = modelo,
            acabado = acabado,
            especificaciones = especificaciones,
            accesorios = accesorios,
            anchoCm = anchoProductoCm,
            altoCm = altoProductoCm,
            alturaPuenteCm = alturaPuenteCm,
            unidadCaptura = unidadMedida,
            ubicacionObra = direccionPanel.ifBlank { null },
            notasObra = construirNotasTecnicasNumeradas(),
            evidencias = emptyList(),
            estado = estadoDestino,
            pendienteSincronizar = true,
            medidoPor = clienteNombreFinal ?: "operario",
            dispositivoId = obtenerIdentificadorDispositivo(),
            creadoEn = now,
            actualizadoEn = now
        )

        medicionViewModel.guardar(item) { guardado ->
            if (estadoDestino != EstadoMedicion.BORRADOR) {
                guardarItemEnDescargas(guardado)
                // Evita que onPause cree un borrador duplicado justo después de archivar.
                omitirAutoBorradorHastaMs = System.currentTimeMillis() + 15000L
                prepararSiguienteProductoMismoCliente()
            }
        }
        itemIdEnEdicion = null
    }

    private fun prepararSiguienteProductoMismoCliente() {
        restaurandoEstadoTemporal = true
        actualizandoPanelMedidas = true

        binding.cbProductoVentanaPanel.isChecked = false
        binding.cbProductoPuertaPanel.isChecked = false
        binding.cbProductoMamparaPanel.isChecked = false

        binding.etMedidaSuperiorPanel.setText("")
        binding.etMedidaDerechaPanel.setText("")
        binding.etMedidaInferiorPanel.setText("")
        binding.etMedidaIzquierdaPanel.setText("")
        binding.etDistPisoPanel.setText("")
        binding.etDistTechoPanel.setText("")

        categoria = ""
        sistema = ""
        tipoApertura = ""
        geometria = ""
        encuentros = ""
        modelo = ""
        acabado = ""
        especificaciones = emptyMap()
        accesorios = emptyList()
        anchoProductoCm = 0f
        altoProductoCm = 0f
        alturaPuenteCm = 0f

        acopleLadosSeleccionados.clear()
        binding.rectanglesDrawingView.clearRectangles()
        binding.rectanglesDrawingView.setProductType(RectanglesDrawingView.ProductType.NONE)
        binding.rectanglesDrawingView.setGeometryType(RectanglesDrawingView.GeometryType.PLANO)
        binding.rectanglesDrawingView.setEncounterType(RectanglesDrawingView.EncounterType.MURO)

        actualizandoPanelMedidas = false
        restaurandoEstadoTemporal = false

        historialEstados.clear()
        actualizarEstadoBotonDeshacer()
        ocultarPanelesTemporales()
        actualizarResumenDatosTecnicos()
        actualizarClienteHeaderDesdePanel()
        mostrarMensaje("Producto archivado. Cliente mantenido para agregar otro producto.")
    }

    private fun guardarItemEnDescargas(item: ItemMedicionObra) {
        runCatching {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val crystalDir = File(downloadsDir, "Crystal")
            val medidasDir = File(crystalDir, "Medidas")
            if (!medidasDir.exists()) medidasDir.mkdirs()

            val cliente = (item.clienteNombre ?: "SinCliente")
                .replace(Regex("[^A-Za-z0-9_\\-]"), "_")
                .take(30)
            actualizarLoteClienteEnDescargas(item, medidasDir, cliente)
        }.onSuccess {
            mostrarMensaje("Guardado en lote del cliente")
        }.onFailure {
            mostrarMensaje("No se pudo guardar en Descargas: ${it.message}")
        }
    }

    private fun actualizarLoteClienteEnDescargas(
        item: ItemMedicionObra,
        medidasDir: File,
        clienteSanitizado: String
    ) {
        val loteJsonFile = File(medidasDir, "cliente_${clienteSanitizado}_lote.json")
        val loteTxtFile = File(medidasDir, "cliente_${clienteSanitizado}_lote.txt")

        val loteActual = runCatching {
            Gson().fromJson(loteJsonFile.readText(), ClienteLoteArchivo::class.java)
        }.getOrNull() ?: ClienteLoteArchivo(
            cliente = item.clienteNombre ?: "SinCliente",
            productos = mutableListOf()
        )

        val productos = loteActual.productos
        val index = productos.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            productos[index] = item
        } else {
            productos.add(item)
        }
        val ordenCategoria = mapOf("ventana" to 0, "puerta" to 1, "mampara" to 2)
        productos.sortWith(
            compareBy<ItemMedicionObra>(
                { ordenCategoria[it.categoria] ?: 99 },
                { it.numero },
                { it.actualizadoEn }
            )
        )

        val loteFinal = loteActual.copy(
            cliente = loteActual.cliente.ifBlank { item.clienteNombre ?: "SinCliente" },
            productos = productos,
            actualizadoEn = System.currentTimeMillis()
        )
        loteJsonFile.writeText(Gson().toJson(loteFinal))

        loteTxtFile.writeText(construirTextoLoteCliente(loteFinal))
    }

    private fun construirTextoLoteCliente(lote: ClienteLoteArchivo): String {
        val sb = StringBuilder()
        sb.appendLine("Cliente: ${lote.cliente}")
        sb.appendLine("Productos: ${lote.productos.size}")
        sb.appendLine()
        lote.productos.forEachIndexed { idx, p ->
            sb.appendLine("${idx + 1}) ${p.categoria}${p.numero}")
            sb.appendLine("   Sistema: ${p.sistema}")
            sb.appendLine("   Tipo: ${p.tipoApertura}")
            sb.appendLine("   Geometria: ${p.geometria}")
            sb.appendLine("   Encuentros: ${p.encuentros}")
            sb.appendLine("   Modelo: ${p.modelo}")
            sb.appendLine("   Acabado: ${p.acabado}")
            sb.appendLine("   Medidas cm: ancho=${formatearNumero(p.anchoCm)}, alto=${formatearNumero(p.altoCm)}, puente=${formatearNumero(p.alturaPuenteCm)}")
            if (!p.notasObra.isNullOrBlank()) {
                sb.appendLine("   Notas:")
                p.notasObra.lines().forEach { linea ->
                    if (linea.isNotBlank()) sb.appendLine("   - $linea")
                }
            }
            sb.appendLine()
        }
        return sb.toString().trimEnd()
    }

    private fun guardarBorradorSiCorresponde() {
        if (System.currentTimeMillis() < omitirAutoBorradorHastaMs) return
        if (categoria.isBlank()) return
        if (anchoProductoCm <= 0f && altoProductoCm <= 0f && alturaPuenteCm <= 0f) return
        archivarItemActual(EstadoMedicion.BORRADOR)
    }

    private fun obtenerSiguienteNumeroCategoria(cat: String): Int {
        return (itemsArchivados.filter { it.categoria == cat }.maxOfOrNull { it.numero } ?: 0) + 1
    }

    private fun actualizarListaArchivados() = Unit

    private fun calcularTodosLosItems() {
        if (itemsArchivados.isEmpty()) {
            mostrarMensaje("No hay ítems para enviar")
            return
        }
        val compatibles = itemsArchivados.filter(::esCalculable)
        if (compatibles.isEmpty()) {
            mostrarMensaje("No hay ítems compatibles con calculadora")
            return
        }
        colaCalculoMasivo.clear()
        colaCalculoMasivo.addAll(compatibles)
        procesamientoMasivoActivo = true
        lanzarSiguienteCalculoMasivo()
    }

    private fun esCalculable(item: ItemMedicionObra): Boolean {
        return item.sistema == SISTEMA_NOVA && item.tipoApertura == APERTURA_CORREDIZA
    }

    private fun continuarCalculoMasivoSiAplica() {
        if (!procesamientoMasivoActivo) return
        if (colaCalculoMasivo.isEmpty()) {
            procesamientoMasivoActivo = false
            mostrarMensaje("Envío masivo finalizado")
            return
        }
        lanzarSiguienteCalculoMasivo()
    }

    private fun lanzarSiguienteCalculoMasivo() {
        val item = colaCalculoMasivo.removeFirstOrNull() ?: run {
            procesamientoMasivoActivo = false
            mostrarMensaje("Envío masivo finalizado")
            return
        }
        irACalculadoraConItem(item)
    }

    private fun irACalculadoraConItem(item: ItemMedicionObra) {
        if (!esCalculable(item)) {
            mostrarMensaje("Calculadora no disponible para ${item.sistema}-${item.tipoApertura}")
            return
        }
        val intent = Intent(this, NovaCorrediza::class.java).apply {
            putExtra("clienteId", item.clienteId)
            putExtra("clienteNombre", item.clienteNombre)
            putExtra("categoria", item.categoria)
            putExtra("sistema", item.sistema)
            putExtra("tipo", item.tipoApertura)
            putExtra("geometria", item.geometria)
            putExtra("encuentros", item.encuentros)
            putExtra("modelo", item.modelo)
            putExtra("acabado", item.acabado)
            putExtra("ancho", item.anchoCm)
            putExtra("alto", item.altoCm)
            putExtra("hoja", item.alturaPuenteCm)
            putExtra("colorAluminio", item.especificaciones["colorAluminio"] ?: "natural")
            putExtra("tipoVidrio", item.especificaciones["tipoVidrio"] ?: "crudo")
            putExtra("numeroItem", item.numero)
        }
        startActivity(intent)
        medicionViewModel.cambiarEstado(item.id, EstadoMedicion.ENVIADO_A_DISENO)
    }

    private fun obtenerIdentificadorDispositivo(): String {
        val secureId = runCatching {
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        }.getOrNull()
        return secureId ?: "${Build.BRAND}-${Build.MODEL}"
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun String.toTitleCase(): String {
        return lowercase(Locale.getDefault()).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
}
