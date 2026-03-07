package crystal.crystal.taller.nova

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.Diseno.nova.DisenoNovaActivity
import crystal.crystal.R
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityNovaCorredizaBinding
import crystal.crystal.taller.ModoMasivoHelper
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
    private var contadorLado = 1
    private var maxLados = -1
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var ultimoPaquete: String = ""
    private var primerClickArchivarRealizado = false
    private var spinnerListo = false
    private var metaColorAluminio: String = ""
    private var metaTipoVidrio: String = ""
    private var metaAcabadoSuperficial: String = ""
    private var metaObservaciones: String = ""

    // Estado para divisiones desiguales (cargado desde DisenoNova)
    private var modulosDesiguales: List<ModuloDesigual> = emptyList()
    private var parantesDesiguales: List<Int> = emptyList()
    private var mochetaDesigual: List<ModuloDesigual> = emptyList()
    private var altoHojaDesigual: Float = 0f
    private var primeraMedidaNl: MedidaNl? = null
    private var primeraMedidaNu: MedidaNl? = null
    private var segundaMedidaNu: MedidaNl? = null
    private val ladosNs: MutableList<MedidaNl> = mutableListOf()

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

    @RequiresApi(Build.VERSION_CODES.M)
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

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNovaCorredizaBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.fcLayout.visibility = View.GONE

        // Estado inicial aparente
        actualizarModo()
        calcular()
        metaColorAluminio = intent.getStringExtra("color_aluminio")?.trim().orEmpty()
        metaTipoVidrio = intent.getStringExtra("tipo_vidrio")?.trim().orEmpty()

        // Toggle modo al tocar título (cicla APA -> INA -> PIV -> APA)
        binding.tvTitulo.setOnClickListener {
            tipoNova = when (tipoNova) {
                TipoNova.APA -> TipoNova.INA
                TipoNova.INA -> TipoNova.PIV
                TipoNova.PIV -> TipoNova.APA
            }
            actualizarModo()
        }

        binding.btArchivar.setOnClickListener {
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
            val intent = Intent(this, DisenoNovaActivity::class.java)
            if (ultimoPaquete.isNotBlank()) {
                intent.putExtra(DisenoNovaActivity.EXTRA_PAQUETE, ultimoPaquete)
            }
            intent.putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, mochetaLateralDisenoNl())
            lanzarDisenoInteractivo.launch(intent)
            true
        }

        binding.txHpuente.setOnLongClickListener {
            mostrarLySpinner()
            true
        }

        binding.btOk.setOnClickListener {
            ocultarLySpinner()
        }

        binding.textView28.setOnLongClickListener {
            val texto = binding.textView28.text?.toString().orEmpty()
            if (texto.isBlank()) {
                Toast.makeText(this, "No hay texto para copiar", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("paquete_nova", texto))
            Toast.makeText(this, "Texto copiado", Toast.LENGTH_SHORT).show()
            true
        }
        binding.txPr.setOnLongClickListener {
            val texto = binding.txPr.text?.toString().orEmpty()
            if (texto.isBlank()) {
                Toast.makeText(this, "No hay texto para copiar", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText("diseno_simbolico_v2", texto))
            Toast.makeText(this, "Texto copiado", Toast.LENGTH_SHORT).show()
            true
        }

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAlto.setText(df1(it)) }
    }

    // ==================== TOGGLE MODO ====================
    @RequiresApi(Build.VERSION_CODES.M)
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
        binding.etNmochetas.backgroundTintList = tintList

        // Spinner de tubo: solo visible en aparente
        binding.lySpinner.visibility = View.GONE // siempre oculto por defecto, se muestra con longclick
        // tLayout (Tee): solo aparente
        binding.tLayout.visibility = if (tipoNova == TipoNova.APA) View.VISIBLE else View.GONE
        // lyUf (U felpero): solo inaparente
        binding.lyUf.visibility = if (tipoNova == TipoNova.INA) View.VISIBLE else View.GONE
        // El spinner de tubo aplica a las tres novas
        if (tipoNova == TipoNova.INA && esPuenteMultiple()) {
            puente = puenteInaDefault
            tubo = 2.5f
            binding.tvP.text = puente
            sincronizarSpinnerPuenteInaDefault()
        }
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
            androidx.appcompat.app.AlertDialog.Builder(this)
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
                if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener
                if (texto.isEmpty() || texto.isBlank()) texto = "nn"
                if (texto == "ns") {
                    asegurarCamposActivosDesdeUltimoNs()
                }

                if (modulosDesiguales.isNotEmpty()) {
                    calcularDesigual()
                } else {
                    actualizarTxPrConDisenoV2()
                    uTexto()
                    otrosAluminios()
                    vidriosTexto()
                    referencias()

                    val intent = Intent(this, DisenoNovaActivity::class.java).apply {
                        putExtra(DisenoNovaActivity.EXTRA_PAQUETE, disenoSimbolico())
                        putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
                        putExtra(DisenoNovaActivity.EXTRA_OUTPUT_FORMAT, if (texto == "nl" || texto == "nu" || texto == "ns" || texto == "ncu" || texto == "nci") "png" else "svg")
                        putExtra(DisenoNovaActivity.EXTRA_RET_PADDING_PX, 4)
                        putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, mochetaLateralDisenoNl())
                    }
                    lanzarDiseno.launch(intent)
                    ultimoPaquete = disenoSimbolico()
                    binding.textView28.text = ultimoPaquete
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // ==================== CÁLCULO DESIGUAL ====================
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
            modulosDesiguales, parantesDesiguales, ancho, alto, altoHoja, us, cruce, tipoCalculo, tubo, puente
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
          if (mostrarFcDesigual || usarFcComoUFelpero(alto, altoHoja)) {
              val textoFcDesigual = if (textoTramosDesigual.isNotBlank()) textoTramosUnitDesigual else pr.rieles
              binding.txFc.text = textoFcDesigual
              val usarTextoUFelpero = (tipoNova == TipoNova.INA) && usarFcComoUFelpero(alto, altoHoja)
              binding.tvFc.setText(if (usarTextoUFelpero) R.string.u_felpero else R.string.fijo_corre)
              binding.fcLayout.visibility = if (textoFcDesigual.isNotBlank()) View.VISIBLE else View.GONE
          } else {
              binding.fcLayout.visibility = View.GONE
          }

        // Vidrios
        binding.txV.text = NovaCalculosDesiguales.calcularVidrios(
            modulosDesiguales, mochetaDesigual, parantesDesiguales,
            ancho, alto, altoHoja, us, cruce, tipoCalculo, tubo, texto
        )

        // Otros perfiles
        val otros = NovaCalculosDesiguales.calcularOtros(
            modulosDesiguales, parantesDesiguales, ancho, alto, altoHoja, us, cruce, tipoCalculo, tubo, puente, texto
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
                if (mostrarFcIna || usarFcComoUFelpero(alto, altoHoja)) {
                    binding.txFc.text = textoTramosUnitInaDesigual
                    binding.tvFc.setText(if (usarFcComoUFelpero(alto, altoHoja)) R.string.u_felpero else R.string.fijo_corre)
                    binding.fcLayout.visibility = View.VISIBLE
                } else {
                    binding.fcLayout.visibility = View.GONE
                }
            }
            val textoUfDesigual = if (textoTramosUnitInaDesigual.isNotBlank()) textoTramosUnitInaDesigual else pr.rieles
            binding.txUf.text = textoUfDesigual
            binding.lyUf.visibility = if (textoUfDesigual.isNotBlank()) View.VISIBLE else View.GONE
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
            alturaPuente = tubo
        )

        // Diseño visual: usar ultimoPaquete (no regenerar)
        if (ultimoPaquete.isNotBlank()) {
            val intent = Intent(this, DisenoNovaActivity::class.java).apply {
                putExtra(DisenoNovaActivity.EXTRA_PAQUETE, ultimoPaquete)
                putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
                putExtra(DisenoNovaActivity.EXTRA_OUTPUT_FORMAT, if (texto == "nl" || texto == "nu" || texto == "ns" || texto == "ncu" || texto == "nci") "png" else "svg")
                putExtra(DisenoNovaActivity.EXTRA_RET_PADDING_PX, 4)
                putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, mochetaLateralDisenoNl())
            }
            lanzarDiseno.launch(intent)
            binding.textView28.text = ultimoPaquete
        }
    }

    // ==================== MODELOS ====================
    @SuppressLint("SetTextI18n")
    private fun modelos() {
        binding.ivDiseno.visibility = View.VISIBLE
        binding.svModelos.visibility = View.GONE
        binding.ivDiseno.setImageResource(R.drawable.ic_fichad3a)
        texto = "nn"
        diseno = "ic_fichad3a"
        otros = false

        binding.btNovan.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.ic_fichad3a)
            texto = "nn"; diseno = "ic_fichad3a"; otros = false
            limpiarEstadoNl()
        }
        binding.btNovar.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novair)
            texto = "nr"; diseno = "novair"; otros = false
            limpiarEstadoNl()
            puente = "tubo 2 x 1"
            tubo = 2.5f
            binding.tvP.text = puente
        }
        binding.btNovaP2.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.nova2p)
            texto = "np"; diseno = "nova2p"; otros = false
            limpiarEstadoNl()
            puente = puenteNpDefault
            tubo = 2.5f
            binding.tvP.text = puente
            val idxNp = indiceSpinnerPuente(puenteNpDefault)
            if (idxNp >= 0 && binding.spinner.selectedItemPosition != idxNp) {
                binding.spinner.setSelection(idxNp)
            }
        }
        binding.btNovacc.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novacc)
            texto = "ncc"; diseno = "novacc"; otros = true
            limpiarEstadoNl()
        }
        binding.btNova3c.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.nova3c)
            texto = "n3c"; diseno = "nova3c"; otros = true
            limpiarEstadoNl()
        }
        binding.btNovacfc.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novacfc)
            texto = "ncfc"; diseno = "novacfc"; otros = true
            limpiarEstadoNl()
        }
        binding.btNoval.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.noval)
            texto = "nl"; diseno = "noval"
            limpiarEstadoNl()
            binding.lyFlecha.visibility = View.VISIBLE
            maxLados = 2; contadorLado = 1
            binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
            binding.btAgregar.visibility = View.VISIBLE
            binding.btAgregar.isEnabled = true; otros = true
        }
        binding.btNovau.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novau)
            texto = "nu"; diseno = "novau"
            limpiarEstadoNl()
            maxLados = 3; contadorLado = 1
            binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
            binding.btAgregar.visibility = View.VISIBLE
            binding.btAgregar.isEnabled = true; otros = true
        }
        binding.btNovas.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novas)
            texto = "ns"; diseno = "novas"
            limpiarEstadoNl()
            maxLados = -1; contadorLado = 1
            binding.tvMedidas.text = "Medidas y Cantidad\nLado$contadorLado"
            binding.btAgregar.visibility = View.VISIBLE
            binding.btAgregar.isEnabled = true; otros = true
        }
        binding.btNovacu.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novacu)
            texto = "ncu"; diseno = "novacu"
            limpiarEstadoNl()
            binding.lyFlecha.visibility = View.VISIBLE; otros = true
        }
        binding.btNovaci.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novaci)
            texto = "nci"; diseno = "novaci"; otros = true
            limpiarEstadoNl()
        }
        binding.btNovav.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.bvacio)
            texto = "nv"; diseno = "bvacio"; otros = false
            limpiarEstadoNl()
        }
        binding.btAgregar.setOnClickListener {
            if (texto != "nl" && texto != "nu" && texto != "ns") return@setOnClickListener
            val ancho = binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f
            val alto = binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f
            val hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
            val divisManual = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
            if (ancho <= 0f || alto <= 0f) {
                Toast.makeText(this, "Ingrese ancho y alto válidos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
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
                binding.etHoja.setText("")
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
        binding.ivDiseno.setOnClickListener {
            binding.ivDiseno.visibility = View.GONE
            binding.svModelos.visibility = View.VISIBLE
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
            binding.tvMedidas.text = "Medidas y Cantidad"
            binding.btAgregar.visibility = View.GONE
            binding.btAgregar.isEnabled = false
            limpiarEstadoNl()
            binding.txDatos.setText(R.string.otros_datos)
            contadorLado = 1; otros = false
        }
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
            NovaCalculos.divisiones(ancho, divis)
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

    private fun tipoCalculoPrincipal(): String = if (tipoNova == TipoNova.APA) "apa" else "ina"

    private fun valorParanteUFijosApa(): Float {
        val p = puente.lowercase().trim()
        val esMultiple = p.contains("multi") || p.contains("múlt") || p.contains("mÃºlt")
        val esGorrito = p.contains("gorrito") || p.contains("ltiple")
        return if (esMultiple || esGorrito) 2.5f else tubo
    }

    private fun esPuenteMultipleOGorrito(): Boolean {
        val p = puente.lowercase().trim()
        return p.contains("multi") || p.contains("múlt") || p.contains("mÃºlt") || p.contains("gorrito") || p.contains("ltiple")
    }


    private fun tipoPaquete(): String = when (tipoNova) {
        TipoNova.APA -> "apa"
        TipoNova.INA -> "ina"
        TipoNova.PIV -> "piv"
    }

    private fun mostrarFijoCorredizo(alto: Float, altoHoja: Float): Boolean {
        return (altoHoja >= alto) || (texto == "nr")
    }

    private fun esPuenteMultiple(): Boolean {
        val p = puente.lowercase().trim()
        return p.contains("multi") || p.contains("múlt") || p.contains("mÃºlt") || p.contains("ltiple")
    }

    private fun esPuenteMultiple(textoPuente: String): Boolean {
        val p = textoPuente.lowercase().trim()
        return p.contains("multi") || p.contains("múlt") || p.contains("mÃºlt") || p.contains("ltiple")
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

    private fun sincronizarSpinnerPuenteInaDefault() {
        val idx = indiceSpinnerPuente(puenteInaDefault)
        if (idx >= 0 && binding.spinner.selectedItemPosition != idx) {
            binding.spinner.setSelection(idx)
        }
    }

    private fun usarFcComoUFelpero(alto: Float, altoHoja: Float): Boolean {
        return !esPuenteMultiple() && altoHoja < alto && texto != "nr"
    }

    private fun modeloNpVetaMultiple(): Boolean = texto == "np"

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
        val ancho = entrada.ancho
        val alto = entrada.alto
        val hoja = entrada.hoja
        val us = binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f
        val divisManual = entrada.divisManual
        val cruce = entrada.cruce

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)

        if (tipoNova == TipoNova.APA) {
            // === APARENTE ===
            val nFijos = NovaCalculos.nFijos(divisiones)
            val fijoUParante = if (divisiones == 2) 1 else NovaCalculos.fijoUParante(divisiones, ancho)
            val factorMochetas = if (texto == "np") 2 else 1
            val mochetaUParante = NovaCalculos.mochetaUParante(divisiones, ancho) * factorMochetas
            val nPuentes = NovaCalculos.nPuentesEfectivos(ancho, divisiones)
            val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruce, "apa", valorParanteUFijosApa())
            val uParante = altoHoja - (2*us)
            val altoMocheta = if (texto == "np") {
                NovaInaCalculos.alturasMochetasPorModelo(
                    modelo = texto,
                    alto = alto,
                    altoHoja = altoHoja,
                    alturaPuente = tubo
                ).superior
            } else {
                NovaCalculos.altoMocheta(alto, altoHoja, tubo)
            }
              val uMocheta = altoMocheta - (2f * us)
            val mPuentes1 = NovaCalculos.mPuentes1(ancho, divisiones, "apa")
            val uSuperior = mPuentes1
            val mPuentes2 = NovaCalculos.mPuentes2(ancho, divisiones, "apa")
            val uSuperior2 = mPuentes2

              binding.tvU.text = NovaPerfilesHelper.obtenerEtiquetaU(us)
              // Se usa "nn" para conservar exactamente el comportamiento previo de esta pantalla.
              val textoBaseU = NovaPerfilesHelper.generarTextoU(
                  texto = "nn",
                  alto = alto,
                  hoja = altoHoja,
                  us = us,
                divisiones = divisiones,
                uFijos = uFijos,
                uParante = uParante,
                uMocheta = uMocheta,
                uSuperior = uSuperior,
                uSuperior2 = uSuperior2,
                nFijos = nFijos,
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
              val textoUMochetaTramos = escalarCantidadesTexto(textoUMochetaTramosBase, factorMochetas)

              val patronUSuperior1 = Regex("^${Regex.escape(NovaCalculos.df1(uSuperior))}\\s*=\\s*\\d+\\s*$")
              val patronUSuperior2 = Regex("^${Regex.escape(NovaCalculos.df1(uSuperior2))}\\s*=\\s*\\d+\\s*$")
              val baseSinAnchoIgual = textoBaseU
                  .lineSequence()
                  .map { it.trimEnd() }
                  .filter {
                      it.isNotBlank() &&
                              !patronUSuperior1.matches(it.trim()) &&
                              !patronUSuperior2.matches(it.trim())
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
              var textoFinalU = baseSinAnchoIgual.joinToString("\n")
              if (divisiones == 1) {
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

              binding.txU.text = textoFinalU
        } else {
            // === INAPARENTE/PIVOTANTE: lógica de vidrio3/NovaIna ===
            binding.tvU.text = NovaPerfilesHelper.obtenerEtiquetaU(us)
            val divisionesU = if (tipoNova == TipoNova.INA && texto == "np") 1 else divisiones
            binding.txU.text = NovaInaCalculos.calcularTextoU(
                ancho = ancho,
                alto = alto,
                hoja = hoja,
                us = us,
                divisiones = divisionesU,
                cruceExacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
            )
        }
    }

    // ==================== OTROS ALUMINIOS ====================
    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    private fun otrosAluminios() {
        val entrada = leerEntradasCalculo()
        val ancho = entrada.ancho
        val alto = entrada.alto
        val hoja = entrada.hoja
        val divisManual = entrada.divisManual
        val cruce = entrada.cruce

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val nCorredizas = NovaCalculos.nCorredizas(divisiones)
        val nPuentes = NovaCalculos.nPuentesEfectivos(ancho, divisiones)
        val portafelpa = NovaCalculos.portafelpa(altoHoja)
        val divDePortas = NovaCalculos.divDePortas(divisiones, nCorredizas)

        // Tipo de ventana para fórmulas
        val tipoCalculo = tipoCalculoPrincipal()
        val mPuentes1 = NovaCalculos.mPuentes1(ancho, divisiones, tipoCalculo)
        val mPuentes2 = NovaCalculos.mPuentes2(ancho, divisiones, tipoCalculo)
        val paranteApa = if (tipoNova == TipoNova.APA) valorParanteUFijosApa() else 2.5f
        val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruce, tipoCalculo, paranteApa)

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
                      modelo = texto
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
              val factorMochetas = if (texto == "np") 2 else 1
              val textoTramosUnit = escalarCantidadesTexto(textoTramosUnitBase, factorMochetas)

              if (textoTramos.isNotBlank()) {
                  binding.txP.text = textoTramosUnit
                  binding.txR.text = textoTramosUnit
                  binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else View.VISIBLE
                  binding.lyRiel.visibility = View.VISIBLE
              } else {
                  binding.txP.text = resultado.puentes
                  binding.mulLayout.visibility = if (altoHoja >= alto) View.GONE else if (resultado.mostrarPuentes) View.VISIBLE else View.GONE
                  binding.txR.text = resultado.rieles
                  binding.lyRiel.visibility = if (resultado.mostrarRieles) View.VISIBLE else View.GONE
              }
              val mostrarFc = mostrarFijoCorredizo(alto, altoHoja)
              if (mostrarFc || usarFcComoUFelpero(alto, altoHoja)) {
                  val textoFc = if (textoTramos.isNotBlank()) textoTramosUnit else resultado.rieles
                  binding.txFc.text = textoFc
                  val usarTextoUFelpero = (tipoNova == TipoNova.INA) && usarFcComoUFelpero(alto, altoHoja)
                  binding.tvFc.setText(if (usarTextoUFelpero) R.string.u_felpero else R.string.fijo_corre)
                  binding.fcLayout.visibility = if (textoFc.isNotBlank()) View.VISIBLE else View.GONE
              } else {
                  binding.fcLayout.visibility = View.GONE
              }
                  val nParantesDiseno = when (texto) {
                      "ncc" -> NovaCalculos.nParantesCadaDosCorredizas(divisiones)
                      "n3c" -> NovaCalculos.nParantesCadaTresCorredizas(divisiones)
                      "ncfc" -> NovaCalculos.nParantesCadaTresCorredizas(divisiones)
                      else -> NovaCalculos.nParantesDiseno(ancho, divisiones)
                  }
                  val textoParantes = if (nParantesDiseno > 0) "${NovaCalculos.df1(alto)} = $nParantesDiseno" else ""
                  binding.txT.text = textoParantes
                  binding.lyTubo.visibility = if (textoParantes.isNotBlank()) View.VISIBLE else View.GONE
            binding.txPf.text = resultado.portafelpaTxt
            binding.lyPf.visibility = if (resultado.mostrarPortafelpa) View.VISIBLE else View.GONE
            binding.txTe.text = resultado.teeTxt
            binding.tLayout.visibility = if (resultado.mostrarTee) View.VISIBLE else View.GONE
            binding.txTo.text = resultado.topeTxt
            binding.lyTo.visibility = if (resultado.mostrarTope) View.VISIBLE else View.GONE
            binding.txH.text = resultado.hacheTxt
            binding.lyH.visibility = if (resultado.mostrarHache) View.VISIBLE else View.GONE

            // Ocultar lyUf en aparente
            binding.lyUf.visibility = View.GONE
            if (divisiones == 1) {
                binding.txR.text = ""
                binding.txFc.text = ""
                binding.txPf.text = ""
                binding.txTe.text = ""
                binding.lyRiel.visibility = View.GONE
                binding.fcLayout.visibility = View.GONE
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
                cruceExacto = cruceExacto
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

              // PUENTE
              if (textoTramos.isNotBlank()) {
                  binding.txP.text = textoTramosUnit
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
            if (mostrarFc || usarFcComoUFelpero(alto, altoHoja)) {
                val textoFc = if (textoTramos.isNotBlank()) textoTramosUnit else resultado.rieles
                binding.txFc.text = textoFc
                val usarTextoUFelpero = (tipoNova == TipoNova.INA) && usarFcComoUFelpero(alto, altoHoja)
                binding.tvFc.setText(if (usarTextoUFelpero) R.string.u_felpero else R.string.fijo_corre)
                binding.fcLayout.visibility = if (textoFc.isNotBlank()) View.VISIBLE else View.GONE
            } else {
                binding.fcLayout.visibility = View.GONE
            }

            // U FELPERO
            val textoUf = if (textoTramos.isNotBlank()) textoTramosUnit else resultado.uFelpero
            binding.txUf.text = textoUf

            // HACHE
            binding.txH.text = resultado.hache

            // ÁNGULO TOPE
            binding.txTo.text = resultado.angTope
            binding.lyTo.visibility = if (resultado.mostrarAngTope) View.VISIBLE else View.GONE

            // PORTAFELPA
            binding.txPf.text = resultado.portafelpa

              // Visibilidad inaparente
              binding.lyH.visibility = if (resultado.mostrarHache) View.VISIBLE else View.GONE
              binding.lyUf.visibility = if (textoUf.isNotBlank()) View.VISIBLE else View.GONE
              binding.tLayout.visibility = View.GONE
              val nParantesDiseno = when (texto) {
                  "ncc" -> NovaCalculos.nParantesCadaDosCorredizas(divisiones)
                  "n3c" -> NovaCalculos.nParantesCadaTresCorredizas(divisiones)
                  "ncfc" -> NovaCalculos.nParantesCadaTresCorredizas(divisiones)
                  else -> NovaCalculos.nParantesDiseno(ancho, divisiones)
              }
              val textoParantes = if (nParantesDiseno > 0) "${NovaCalculos.df1(alto)} = $nParantesDiseno" else ""
              binding.txT.text = textoParantes
              binding.lyTubo.visibility = if (textoParantes.isNotBlank()) View.VISIBLE else View.GONE

              // TEE vacío en ina
              binding.txTe.text = ""
              if (divisiones == 1) {
                  binding.txR.text = ""
                binding.txFc.text = ""
                binding.txUf.text = ""
                binding.txPf.text = ""
                binding.lyRiel.visibility = View.GONE
                  binding.fcLayout.visibility = View.GONE
                  binding.lyUf.visibility = View.GONE
                  binding.tLayout.visibility = View.GONE
                  binding.lyPf.visibility = View.GONE
                  binding.lyTubo.visibility = View.GONE
              }
          }
      }

    // ==================== VIDRIOS ====================
    @SuppressLint("SetTextI18n")
    private fun vidriosTexto() {
        val ancho = ancho()
        val alto = alto()
        val hoja = altoHoja()
        val us = binding.etU.text.toString().toFloat()
        val divisManual = divisiones()
        val cruce = cruce()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)

        if (tipoNova == TipoNova.APA) {
            // === APARENTE: lógica original ===
          val nFijos = when (texto) {
              "ncc", "n3c" -> 0
              "ncfc" -> NovaCalculos.nFijos(divisiones, "ncfc")
              else -> NovaCalculos.nFijos(divisiones)
          }
          val nCorredizas = when (texto) {
              "ncc", "n3c" -> divisiones
              "ncfc" -> NovaCalculos.nCorredizas(divisiones, "ncfc")
              else -> NovaCalculos.nCorredizas(divisiones)
          }
            val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruce, "apa", valorParanteUFijosApa())

            val vidriosFijos = NovaVidriosHelper.calcularVidriosFijos(ancho, uFijos, altoHoja, us, nFijos, divisiones, "aparente")
            val vidrioCorre = NovaVidriosHelper.calcularVidriosCorredizos(uFijos, altoHoja, nCorredizas)
              val vidrioMocheta = NovaVidriosHelper.calcularVidrioMochetaAparente(
                  ancho = ancho,
                  alto = alto,
                  altoHoja = altoHoja,
                  divisiones = divisiones,
                  tubo = tubo,
                  puente = puente,
                  us = us,
                  modelo = texto
              )

            binding.txV.text = if (hoja < alto) {
                if (divisiones > 1) "$vidriosFijos\n$vidrioCorre\n$vidrioMocheta"
                else "$vidriosFijos\n$vidrioMocheta"
            } else {
                if (divisiones > 1) "$vidriosFijos\n$vidrioCorre"
                else vidriosFijos
            }
        } else {
            // === INAPARENTE/PIVOTANTE: lógica de vidrio3/NovaIna ===
            val cruceExacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
              binding.txV.text = NovaInaCalculos.calcularVidrios(
                  ancho = ancho,
                  alto = alto,
                  hoja = hoja,
                  us = us,
                  divisiones = divisiones,
                  cruceExacto = cruceExacto,
                  modelo = texto,
                  alturaPuente = tubo
              )
          }
      }

    // ==================== REFERENCIAS ====================
    private fun referencias() {
        val ancho = binding.etAncho.text.toString().toFloat()
        val alto = binding.etAlto.text.toString().toFloat()
        val hoja = binding.etHoja.text.toString().toFloat()
        val divisManual = binding.etPartes.text.toString().toInt()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val siNoMoch = NovaCalculos.siNoMoch(alto, hoja)
        val nFijos = NovaCalculos.nFijos(divisiones)
        val nCorredizas = NovaCalculos.nCorredizas(divisiones)
        val tipoCalculo = tipoCalculoPrincipal()
        val puntosU = if (divisiones > 4) {
            NovaUIHelper.generarPuntosU(ancho, divisManual, cruce(), tipoCalculo)
        } else {
            ""
        }

        binding.txReferencias.text = NovaUIHelper.generarReferencias(
            ancho, alto, altoHoja, divisiones, nFijos, nCorredizas, siNoMoch, puntosU,
            modelo = texto,
            alturaPuente = tubo
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    private fun cargarDesdePaqueteDiseno(paquete: String) {
        try {
            val t = paquete.replace(" ", "")
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
            val dentro = resto.removePrefix("[").substringBeforeLast("]")
            val idxColon = dentro.indexOf(":")
            if (idxColon < 0) return
            val dimsTxt = dentro.substring(0, idxColon)
            val dims = dimsTxt.split(",")
            val ancho = dims.getOrNull(0)?.replace(",", ".")?.toFloatOrNull() ?: return
            val alto = dims.getOrNull(1)?.replace(",", ".")?.toFloatOrNull() ?: return

            binding.etAncho.setText(df1(ancho))
            binding.etAlto.setText(df1(alto))

            // Buscar franja sistema para extraer altoHoja y divisiones
            val cuerpoTxt = dentro.substring(idxColon + 1)
            val secciones = splitRespetandoParentesis(cuerpoTxt)

            // Limpiar estado desigual previo
            modulosDesiguales = emptyList()
            parantesDesiguales = emptyList()
            mochetaDesigual = emptyList()
            altoHojaDesigual = 0f

            for (sec in secciones) {
                val low = sec.trim().lowercase()
                if (low.startsWith("s")) {
                    // Extraer altoHoja
                    if (low.length > 1 && low[1] == '<') {
                        val ah = low.substringAfter("<").substringBefore(">")
                            .replace(",", ".").toFloatOrNull() ?: 0f
                        if (ah > 0f) {
                            binding.etHoja.setText(df1(ah))
                            altoHojaDesigual = ah
                        }
                    }

                    // Extraer módulos con sus anchos y posiciones de parantes
                    val interior = low.substringAfter("(").substringBeforeLast(")")
                    val modsParsed = mutableListOf<ModuloDesigual>()
                    val parantesPos = mutableListOf<Int>()
                    val regexMod = Regex("([fc])(?:<([^>]+)>)?")
                    // Dividir por ;P; para detectar parantes
                    val gruposTxt = interior.split(";p;")
                    var idx = 0
                    for ((gi, grupo) in gruposTxt.withIndex()) {
                        val matches = regexMod.findAll(grupo)
                        for (m in matches) {
                            val tipo = m.groupValues[1][0]
                            val anchoMod = m.groupValues[2].replace(",", ".").toFloatOrNull()
                                ?: (ancho / interior.count { it == 'f' || it == 'c' })
                            modsParsed.add(ModuloDesigual(tipo, anchoMod))
                            idx++
                        }
                        if (gi < gruposTxt.size - 1) {
                            parantesPos.add(idx) // parante after this index
                        }
                    }

                    val divs = modsParsed.size
                    if (divs > 0) binding.etPartes.setText(divs.toString())

                    // Detectar desigualdad: si todos los anchos son iguales (±0.5cm) → normal
                    if (modsParsed.size > 1) {
                        val anchoProm = modsParsed.map { it.ancho }.average().toFloat()
                        val esDesigual = modsParsed.any { kotlin.math.abs(it.ancho - anchoProm) > 0.5f }
                        if (esDesigual) {
                            modulosDesiguales = modsParsed
                            parantesDesiguales = parantesPos
                        }
                    }
                    break
                }
            }

            // Extraer mocheta desigual si hay módulos desiguales
            if (modulosDesiguales.isNotEmpty()) {
                for (sec in secciones) {
                    val low = sec.trim().lowercase()
                    if (low.startsWith("m")) {
                        val interior = low.substringAfter("(").substringBeforeLast(")")
                        val regexMod = Regex("([fc])(?:<([^>]+)>)?")
                        val mochParsed = mutableListOf<ModuloDesigual>()
                        val matches = regexMod.findAll(interior)
                        for (m in matches) {
                            val tipo = m.groupValues[1][0]
                            val anchoMod = m.groupValues[2].replace(",", ".").toFloatOrNull()
                                ?: (ancho / interior.count { it == 'f' || it == 'c' }.coerceAtLeast(1))
                            mochParsed.add(ModuloDesigual(tipo, anchoMod))
                        }
                        if (mochParsed.isNotEmpty()) {
                            mochetaDesigual = mochParsed
                        }
                        break
                    }
                }
            }

            // Guardar paquete y actualizar diseño
            ultimoPaquete = paquete
            binding.textView28.text = paquete
            val tipoMsg = if (modulosDesiguales.isNotEmpty()) " (desigual)" else ""
            Toast.makeText(this, "Diseño cargado: ${df1(ancho)} x ${df1(alto)}$tipoMsg", Toast.LENGTH_SHORT).show()

            // Relanzar render headless para actualizar la imagen
            val intentRender = Intent(this, DisenoNovaActivity::class.java).apply {
                putExtra(DisenoNovaActivity.EXTRA_PAQUETE, ultimoPaquete)
                putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
                putExtra(DisenoNovaActivity.EXTRA_OUTPUT_FORMAT, if (texto == "nl" || texto == "nu" || texto == "ns" || texto == "ncu" || texto == "nci") "png" else "svg")
                putExtra(DisenoNovaActivity.EXTRA_RET_PADDING_PX, 4)
                putExtra(DisenoNovaActivity.EXTRA_MOCHETA_LATERAL_CM, mochetaLateralDisenoNl())
            }
            lanzarDiseno.launch(intentRender)

        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar diseño: ${e.message}", Toast.LENGTH_SHORT).show()
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
    private fun disenoSimbolico(): String {
        val ancho = binding.etAncho.text.toString().toFloat()
        val alto = binding.etAlto.text.toString().toFloat()
        val hoja = binding.etHoja.text.toString().toFloat()
        val divisManual = binding.etPartes.text.toString().toInt()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)

        if (texto == "nl") {
            val primera = obtenerPrimeraMedidaNlEditable()
            if (primera != null) {
                val divisA = NovaCalculos.divisiones(primera.ancho, primera.divisManual, "nn")
                val divisB = NovaCalculos.divisiones(ancho, divisManual, "nn")
                val altoHojaA = NovaCalculos.altoHoja(primera.alto, primera.hoja)
                val altoHojaB = NovaCalculos.altoHoja(alto, hoja)
                val disenoPrincipal = NovaUIHelper.generarDiseno(
                    ancho = primera.ancho,
                    alto = primera.alto,
                    altoHoja = altoHojaA,
                    divisiones = divisA,
                    siNoMoch = 1,
                    texto = "nn"
                )
                val disenoAleta = NovaUIHelper.generarDiseno(
                    ancho = ancho,
                    alto = alto,
                    altoHoja = altoHojaB,
                    divisiones = divisB,
                    siNoMoch = 1,
                    texto = "nn"
                )
                val disenoNl = insertarAletaEnPrimerSistema(
                    disenoBase = disenoPrincipal,
                    tagsAleta = listOf(construirTagAleta("L", ancho, disenoAleta))
                )
                return "{nova,${tipoPaquete()},[$disenoNl]}"
            }
        }
        if (texto == "nu") {
            val ladoIzq = obtenerPrimeraMedidaNuEditable()
            val ladoCentro = obtenerSegundaMedidaNuEditable()
            if (ladoIzq != null && ladoCentro != null) {
                val ladoDer = MedidaNl(ancho, alto, hoja, divisManual)
                val divisIzq = NovaCalculos.divisiones(ladoIzq.ancho, ladoIzq.divisManual, "nn")
                val divisCentro = NovaCalculos.divisiones(ladoCentro.ancho, ladoCentro.divisManual, "nn")
                val divisDer = NovaCalculos.divisiones(ladoDer.ancho, ladoDer.divisManual, "nn")
                val altoHojaIzq = NovaCalculos.altoHoja(ladoIzq.alto, ladoIzq.hoja)
                val altoHojaCentro = NovaCalculos.altoHoja(ladoCentro.alto, ladoCentro.hoja)
                val altoHojaDer = NovaCalculos.altoHoja(ladoDer.alto, ladoDer.hoja)
                val disenoIzq = NovaUIHelper.generarDiseno(
                    ancho = ladoIzq.ancho,
                    alto = ladoIzq.alto,
                    altoHoja = altoHojaIzq,
                    divisiones = divisIzq,
                    siNoMoch = 1,
                    texto = "nn"
                )
                val disenoCentro = NovaUIHelper.generarDiseno(
                    ancho = ladoCentro.ancho,
                    alto = ladoCentro.alto,
                    altoHoja = altoHojaCentro,
                    divisiones = divisCentro,
                    siNoMoch = 1,
                    texto = "nn"
                )
                val disenoDer = NovaUIHelper.generarDiseno(
                    ancho = ladoDer.ancho,
                    alto = ladoDer.alto,
                    altoHoja = altoHojaDer,
                    divisiones = divisDer,
                    siNoMoch = 1,
                    texto = "nn"
                )
                val conAmbas = insertarAletaEnPrimerSistema(
                    disenoBase = disenoCentro,
                    tagsAleta = listOf(
                        construirTagAleta("L", ladoIzq.ancho, disenoIzq),
                        construirTagAleta("R", ladoDer.ancho, disenoDer)
                    )
                )
                return "{nova,${tipoPaquete()},[$conAmbas]}"
            }
        }
        if (texto == "ns") {
            val lados = mutableListOf<MedidaNl>()
            lados.addAll(ladosNs)
            if (ancho > 0f && alto > 0f) {
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
                val disenoBase = NovaUIHelper.generarDiseno(
                    ancho = base.ancho,
                    alto = base.alto,
                    altoHoja = altoHojaBase,
                    divisiones = divisBase,
                    siNoMoch = 1,
                    texto = "nn"
                )
                val tags = mutableListOf<String>()
                for (i in 1 until lados.size) {
                    val lado = lados[i]
                    val divisLado = NovaCalculos.divisiones(lado.ancho, lado.divisManual, "nn")
                    val altoHojaLado = NovaCalculos.altoHoja(lado.alto, lado.hoja)
                    val disenoLado = NovaUIHelper.generarDiseno(
                        ancho = lado.ancho,
                        alto = lado.alto,
                        altoHoja = altoHojaLado,
                        divisiones = divisLado,
                        siNoMoch = 1,
                        texto = "nn"
                    )
                    val tagTipo = if (((i + 1) % 2) == 0) "A" else "P"
                    tags.add(construirTagAleta(tagTipo, lado.ancho, disenoLado))
                }
                val disenoNs = insertarAletaEnPrimerSistema(disenoBase = disenoBase, tagsAleta = tags)
                return "{nova,${tipoPaquete()},[$disenoNs]}"
            }
        }
        if (texto == "ncu") {
            val disenoBase = NovaUIHelper.generarDiseno(
                ancho = ancho,
                alto = alto,
                altoHoja = altoHoja,
                divisiones = divisiones,
                siNoMoch = 1,
                texto = "nn"
            )
            val disenoCurvo = insertarTagSimpleEnPrimerSistema(disenoBase, "U<10>")
            return "{nova,${tipoPaquete()},[$disenoCurvo]}"
        }
        if (texto == "nci") {
            val disenoBase = NovaUIHelper.generarDiseno(
                ancho = ancho,
                alto = alto,
                altoHoja = altoHoja,
                divisiones = divisiones,
                siNoMoch = 1,
                texto = "np"
            )
            val disenoCircular = insertarTagSimpleEnPrimerSistema(disenoBase, "O<1>")
            return "{nova,${tipoPaquete()},[$disenoCircular]}"
        }

        return NovaUIHelper.generarPaqueteSimbolico(tipoPaquete(), ancho, alto, altoHoja, divisiones, texto)
    }

    private fun disenoSimbolicoV2(numeroProductoAuto: Int? = null): String {
        val clienteTxt = escaparCampoV2(binding.txC.text?.toString()?.trim().orEmpty().ifBlank { "sin cliente" })
        val anchoTxt = dfV2(binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f)
        val altoTxt = dfV2(binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f)
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
        val forma = if (texto == "nci") "c" else "r"
        val encuentro = if (texto == "nv") "a" else "m"
        val modelo = when (texto) {
            "nn" -> "n"
            "nr" -> "i"
            "np" -> "b"
            else -> "x"
        }
        val disenoTecnico = try { disenoSimbolico() } catch (_: Exception) { "" }
        val tramo = "L{${escaparCampoV2(disenoTecnico.ifBlank { "null" })}}"
        val aluminio = escaparCampoV2(metaColorAluminio.ifBlank { "null" })
        val vidrios = escaparCampoV2(metaTipoVidrio.ifBlank { "null" })
        val accesorios = escaparCampoV2(textoMetadatosProduccionV2())
        return buildString {
            append("C<").append(clienteTxt).append(">")
            append("-M<").append(anchoTxt).append(",").append(altoTxt).append(",").append(hpTxt)
            append(",null,null,").append(cantidad).append(">")
            append("-P<V,n,").append(acabado).append(",c,").append(numeroProducto).append(">")
            append("-G<").append(volumen).append(",").append(forma).append(",").append(encuentro).append(",").append(modelo).append(">")
            append("-T<").append(tramo).append(">")
            append("-MAT<alu:").append(aluminio).append(";vid:").append(vidrios).append(">")
            append("-ACC<").append(accesorios).append(">")
        }
    }

    private fun textoMetadatosProduccionV2(): String {
        val pares = listOf(
            "acabado_sup" to metaAcabadoSuperficial.trim(),
            "obs" to metaObservaciones.trim()
        ).filter { it.second.isNotBlank() }
        if (pares.isEmpty()) return "null"
        return pares.joinToString(" | ") { "${it.first}:${it.second}" }
    }

    private fun obtenerCantidadPreferida(): Int {
        return intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
    }

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

    private fun escaparCampoV2(raw: String): String {
        return raw
            .replace("\n", " / ")
            .replace("\r", " ")
            .replace("-", "_")
            .replace("<", "(")
            .replace(">", ")")
            .trim()
    }

    private fun mismaMedida(a: MedidaNl, b: MedidaNl): Boolean {
        fun casiIgual(x: Float, y: Float): Boolean = kotlin.math.abs(x - y) < 0.001f
        return casiIgual(a.ancho, b.ancho) &&
            casiIgual(a.alto, b.alto) &&
            casiIgual(a.hoja, b.hoja) &&
            a.divisManual == b.divisManual
    }

    private fun limpiarEstadoNl() {
        primeraMedidaNl = null
        primeraMedidaNu = null
        segundaMedidaNu = null
        ladosNs.clear()
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
        binding.txDatos.setText(R.string.otros_datos)
    }

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

    private fun insertarTagSimpleEnPrimerSistema(
        disenoBase: String,
        tag: String
    ): String {
        if (tag.isBlank()) return disenoBase
        val idx = disenoBase.indexOf(':')
        if (idx <= 0 || idx >= disenoBase.lastIndex) return disenoBase
        val cabecera = disenoBase.substring(0, idx)
        val cuerpo = disenoBase.substring(idx + 1)
        val patronSistema = Regex("s(?:<[^>]*>)?\\([^)]*\\)", RegexOption.IGNORE_CASE)
        val match = patronSistema.find(cuerpo) ?: return disenoBase
        val segmento = match.value
        val posCierre = segmento.lastIndexOf(')')
        if (posCierre <= 0) return disenoBase
        val segmentoNuevo = segmento.substring(0, posCierre) + tag + segmento.substring(posCierre)
        val cuerpoNuevo = cuerpo.replaceRange(match.range, segmentoNuevo)
        return "$cabecera:$cuerpoNuevo"
    }

    // ==================== SPINNER TUBO ====================
    private fun spinnerTubo() {
        val spinnerOptions = NovaSpinnerData.obtenerOpcionesTubo()
        val adapter = NovaSpinnerData.AdaptadorSpinner(this, spinnerOptions)
        spinnerListo = false
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!spinnerListo) {
                    spinnerListo = true
                    return
                }
                val selectedOption = spinnerOptions[position]
                if (modeloNpVetaMultiple() && esPuenteMultiple(selectedOption.text)) {
                    tubo = 2.5f
                    puente = puenteNpDefault
                    val idxNp = indiceSpinnerPuente(puenteNpDefault)
                    if (idxNp >= 0 && binding.spinner.selectedItemPosition != idxNp) {
                        binding.spinner.setSelection(idxNp)
                    }
                } else if (tipoNova == TipoNova.INA && esPuenteMultiple(selectedOption.text)) {
                    tubo = 2.5f
                    puente = puenteInaDefault
                    sincronizarSpinnerPuenteInaDefault()
                } else {
                    tubo = selectedOption.valor
                    puente = selectedOption.text
                }
                binding.tvP.text = puente
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    // ==================== SPINNER HELPERS ====================
    private fun mostrarLySpinner() {
        binding.lySpinner.alpha = 0f
        binding.lySpinner.visibility = View.VISIBLE
        binding.lySpinner.animate().alpha(1f).setDuration(300).start()
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
            binding.etAncho.setText("")
            binding.etAlto.setText("")
        }
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
                "Tope" to ModoMasivoHelper.texto(binding.txTo),
                "U felpero" to ModoMasivoHelper.texto(binding.txUf)
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
            referencias = ModoMasivoHelper.texto(binding.txReferencias),
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
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        var ultimoID = ""

        for (u in 1..cant) {
            val siguienteNumero = ProyectoManager.obtenerSiguienteContadorPorPrefijo(this, prefijo)
            val identificadorPaquete = "${prefijo}${siguienteNumero}"
            ultimoID = identificadorPaquete

            if (esValido(binding.lyReferencias)) {
                ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvReferencias, binding.txReferencias, mapListas, identificadorPaquete)
            }
            if (esValido(binding.u13layout)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvU, binding.txU, mapListas, identificadorPaquete)
            }
            if (esValido(binding.mulLayout)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvP, binding.txP, mapListas, identificadorPaquete)
            }
            if (esValido(binding.fcLayout)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvFc, binding.txFc, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyRiel)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvR, binding.txR, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTubo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvT, binding.txT, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyPf)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvPf, binding.txPf, mapListas, identificadorPaquete)
            }
            if (tipoNova == TipoNova.APA && esValido(binding.tLayout)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvTe, binding.txTe, mapListas, identificadorPaquete)
            }
            if (tipoNova != TipoNova.APA && esValido(binding.lyUf)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvUf, binding.txUf, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvTo, binding.txTo, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyH)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvH, binding.txH, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyVidrios)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvV, binding.txV, mapListas, identificadorPaquete)
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

        MapStorage.guardarMap(this, mapListas)
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
        val msg = if (cant > 1) "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
                  else "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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


