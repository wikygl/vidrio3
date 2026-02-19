package crystal.crystal.taller.nova

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
    private var puente: String = "Múltiple"
    private var diseno: String = ""
    private var texto: String = ""
    private var otros: Boolean = false
    private var contadorLado = 1
    private var maxLados = -1
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var ultimoPaquete: String = ""
    private var primerClickArchivarRealizado = false
    private var spinnerListo = false

    // Estado para divisiones desiguales (cargado desde DisenoNova)
    private var modulosDesiguales: List<ModuloDesigual> = emptyList()
    private var parantesDesiguales: List<Int> = emptyList()
    private var mochetaDesigual: List<ModuloDesigual> = emptyList()
    private var altoHojaDesigual: Float = 0f

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
        binding.etDivi2.backgroundTintList = tintList
        binding.etPuente2.backgroundTintList = tintList
        binding.etFlecha.backgroundTintList = tintList
        binding.etNmochetas.backgroundTintList = tintList

        // Spinner de tubo: solo visible en aparente
        binding.lySpinner.visibility = View.GONE // siempre oculto por defecto, se muestra con longclick
        // tLayout (Tee): solo aparente
        binding.tLayout.visibility = if (tipoNova == TipoNova.APA) View.VISIBLE else View.GONE
        // lyUf (U felpero): solo inaparente
        binding.lyUf.visibility = if (tipoNova == TipoNova.INA) View.VISIBLE else View.GONE
        // El spinner de tubo aplica a las tres novas
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

                if (modulosDesiguales.isNotEmpty()) {
                    calcularDesigual()
                } else {
                    binding.txPr.text = divisiones().toString()
                    uTexto()
                    otrosAluminios()
                    vidriosTexto()
                    referencias()

                    val intent = Intent(this, DisenoNovaActivity::class.java).apply {
                        putExtra(DisenoNovaActivity.EXTRA_PAQUETE, disenoSimbolico())
                        putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
                        putExtra(DisenoNovaActivity.EXTRA_OUTPUT_FORMAT, "svg")
                        putExtra(DisenoNovaActivity.EXTRA_RET_PADDING_PX, 4)
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

        binding.txPr.text = modulosDesiguales.size.toString()

        // U perfiles
        binding.tvU.text = when (us) {
            1f -> "u-3/8"; 1.5f -> "u-13"; else -> "u-${NovaCalculos.df1(us)}"
        }
        binding.txU.text = NovaCalculosDesiguales.calcularU(
            modulosDesiguales, parantesDesiguales, ancho, alto, altoHoja, us, cruce, tipoCalculo, tubo
        )

        // Puentes y Rieles
        val pr = NovaCalculosDesiguales.calcularPuentesYRieles(
            modulosDesiguales, parantesDesiguales, ancho, alto, altoHoja, tipoCalculo
        )
        binding.txP.text = pr.puentes
        binding.mulLayout.visibility = if (modulosDesiguales.size > 1) View.VISIBLE else View.GONE
        binding.txR.text = pr.rieles
        binding.lyRiel.visibility = if (modulosDesiguales.count { it.tipo == 'c' } > 0) View.VISIBLE else View.GONE

        // Vidrios
        binding.txV.text = NovaCalculosDesiguales.calcularVidrios(
            modulosDesiguales, mochetaDesigual, parantesDesiguales,
            ancho, alto, altoHoja, us, cruce, tipoCalculo, tubo
        )

        // Otros perfiles
        val otros = NovaCalculosDesiguales.calcularOtros(
            modulosDesiguales, parantesDesiguales, ancho, alto, altoHoja, us, cruce, tipoCalculo, tubo
        )
        binding.txPf.text = otros.portafelpa
        binding.lyPf.visibility = if (otros.portafelpa.isNotBlank()) View.VISIBLE else View.GONE
        binding.txH.text = otros.hache
        binding.lyH.visibility = if (otros.hache.isNotBlank()) View.VISIBLE else View.GONE
        binding.txTo.text = otros.tope
        binding.lyTo.visibility = if (otros.tope.isNotBlank()) View.VISIBLE else View.GONE
        binding.txT.text = otros.tuboTxt
        binding.lyTubo.visibility = if (otros.tuboTxt.isNotBlank()) View.VISIBLE else View.GONE

        if (tipoNova == TipoNova.APA) {
            binding.txTe.text = otros.tee
            binding.tLayout.visibility = if (otros.tee.isNotBlank()) View.VISIBLE else View.GONE
            binding.lyUf.visibility = View.GONE
        } else {
            binding.txUf.text = pr.rieles
            binding.lyUf.visibility = if (pr.rieles.isNotBlank()) View.VISIBLE else View.GONE
            binding.tLayout.visibility = View.GONE
            binding.txTe.text = ""
        }

        // Referencias
        val nFijos = modulosDesiguales.count { it.tipo == 'f' }
        val nCorredizas = modulosDesiguales.count { it.tipo == 'c' }
        binding.txReferencias.text = NovaUIHelper.generarReferencias(
            ancho, alto, altoHoja, modulosDesiguales.size, nFijos, nCorredizas,
            if (alto > altoHoja) 1 else 0
        )

        // Diseño visual: usar ultimoPaquete (no regenerar)
        if (ultimoPaquete.isNotBlank()) {
            val intent = Intent(this, DisenoNovaActivity::class.java).apply {
                putExtra(DisenoNovaActivity.EXTRA_PAQUETE, ultimoPaquete)
                putExtra(DisenoNovaActivity.EXTRA_HEADLESS, true)
                putExtra(DisenoNovaActivity.EXTRA_OUTPUT_FORMAT, "svg")
                putExtra(DisenoNovaActivity.EXTRA_RET_PADDING_PX, 4)
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
        }
        binding.btNovar.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novair)
            texto = "nr"; diseno = "novair"; otros = false
        }
        binding.btNovaP2.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.nova2p)
            texto = "np"; diseno = "nova2p"; otros = false
        }
        binding.btNovacc.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novacc)
            texto = "ncc"; diseno = "novacc"; otros = true
        }
        binding.btNova3c.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.nova3c)
            texto = "n3c"; diseno = "nova3c"; otros = true
        }
        binding.btNovacfc.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novacfc)
            texto = "ncfc"; diseno = "novacfc"; otros = true
        }
        binding.btNoval.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.noval)
            texto = "nl"; diseno = "noval"
            binding.lyAncho2.visibility = View.VISIBLE
            binding.lyDivi2.visibility = View.VISIBLE
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
            binding.lyFlecha.visibility = View.VISIBLE; otros = true
        }
        binding.btNovaci.setOnClickListener {
            binding.ivDiseno.visibility = View.VISIBLE
            binding.svModelos.visibility = View.GONE
            binding.ivDiseno.setImageResource(R.drawable.novaci)
            texto = "nci"; diseno = "novaci"; otros = true
        }
        binding.ivDiseno.setOnClickListener {
            binding.ivDiseno.visibility = View.GONE
            binding.svModelos.visibility = View.VISIBLE
            binding.lyAncho2.visibility = View.GONE
            binding.lyDivi2.visibility = View.GONE
            binding.lyFlecha.visibility = View.GONE
            binding.tvMedidas.text = "Medidas y Cantidad"
            binding.btAgregar.visibility = View.GONE
            contadorLado = 1; otros = false
        }
    }

    // ==================== FUNCIONES DE CÁLCULO ====================
    private fun df1(defo: Float): String {
        return if (defo % 1 == 0f) defo.toInt().toString()
        else "%.1f".format(defo).replace(",", ".")
    }

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

    private fun anchMota(): Int {
        val tipoCalculo = if (tipoNova == TipoNova.APA) "apa" else "ina"
        val mP = NovaCalculos.mPuentes1(ancho(), divisiones(), tipoCalculo)
        return NovaCalculos.anchMota(mP)
    }

    // ==================== U TEXTO ====================
    @SuppressLint("SetTextI18n")
    private fun uTexto() {
        val ancho = binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f
        val alto = binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f
        val hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        val us = binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f
        val divisManual = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
        val cruce = cruce()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)

        if (tipoNova == TipoNova.APA) {
            // === APARENTE: lógica original de NovaApa ===
            val nFijos = NovaCalculos.nFijos(divisiones)
            val fijoUParante = NovaCalculos.fijoUParante(divisiones)
            val mochetaUParante = NovaCalculos.mochetaUParante(divisiones)
            val nPuentes = NovaCalculos.nPuentes(divisiones)
            val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruce, "apa")
            val uParante = altoHoja - (2*us)
            val altoMocheta = NovaCalculos.altoMocheta(alto, altoHoja, tubo)
            val uMocheta = altoMocheta - us
            val mPuentes1 = NovaCalculos.mPuentes1(ancho, divisiones, "apa")
            val uSuperior = mPuentes1
            val mPuentes2 = NovaCalculos.mPuentes2(ancho, divisiones, "apa")
            val uSuperior2 = mPuentes2
            val valorFinal = if (puente == "Múltiple" || puente == "gorrito") 1 else 2

            binding.tvU.text = when (us) {
                1f -> "u-3/8"; 1.5f -> "u-13"; else -> "u-${NovaCalculos.df1(us)}"
            }

            binding.txU.text = if (alto > altoHoja && us != 0F) {
                when {
                    divisiones == 1 -> "${NovaCalculos.df1(uFijos)} = 2\n${NovaCalculos.df1(uParante)} = 2\n${NovaCalculos.df1(uMocheta)} = $mochetaUParante"
                    divisiones == 10 || divisiones == 14 -> "${NovaCalculos.df1(uFijos)} = $nFijos\n${NovaCalculos.df1(uParante)} = $fijoUParante\n${NovaCalculos.df1(uMocheta)} = $mochetaUParante\n${NovaCalculos.df1(uSuperior)} = ${nPuentes - 1}\n${NovaCalculos.df1(uSuperior2)} = ${(nPuentes - 2) * valorFinal}"
                    else -> "${NovaCalculos.df1(uFijos)} = $nFijos\n${NovaCalculos.df1(uParante)} = $fijoUParante\n${NovaCalculos.df1(uMocheta)} = $mochetaUParante\n${NovaCalculos.df1(uSuperior)} = ${nPuentes * valorFinal}"
                }
            } else if (alto > altoHoja && us == 0F) {
                when {
                    divisiones == 1 -> "${NovaCalculos.df1(uFijos)} = 2"
                    divisiones == 10 || divisiones == 14 -> "${NovaCalculos.df1(uFijos)} = $nFijos\n${NovaCalculos.df1(uSuperior)} = ${nPuentes - 1}\n${NovaCalculos.df1(uSuperior2)} = ${nPuentes - 2}"
                    else -> "${NovaCalculos.df1(uFijos)} = $nFijos\n${NovaCalculos.df1(uSuperior)} = $nPuentes"
                }
            } else if (alto <= altoHoja && us != 0F) {
                when {
                    divisiones == 1 -> "${NovaCalculos.df1(uFijos)} = 2\n${NovaCalculos.df1(uParante)} = 2"
                    else -> "${NovaCalculos.df1(uFijos)} = $nFijos\n${NovaCalculos.df1(uParante)} = $fijoUParante"
                }
            } else {
                when {
                    divisiones == 1 -> "${NovaCalculos.df1(uFijos)} = 2"
                    else -> "${NovaCalculos.df1(uFijos)} = $nFijos"
                }
            }
        } else {
            // === INAPARENTE/PIVOTANTE: lógica de vidrio3/NovaIna ===
            binding.tvU.text = when (us) {
                1f -> "u-3/8"; 1.5f -> "u-13"; else -> "u-${NovaInaCalculos.df1(us)}"
            }
            binding.txU.text = NovaInaCalculos.calcularTextoU(
                ancho = ancho,
                alto = alto,
                hoja = hoja,
                us = us,
                divisiones = divisiones,
                cruceExacto = binding.etCruce.text?.toString()?.toFloatOrNull() ?: 0f
            )
        }
    }

    // ==================== OTROS ALUMINIOS ====================
    @SuppressLint("SetTextI18n")
    private fun otrosAluminios() {
        val ancho = binding.etAncho.text?.toString()?.toFloatOrNull() ?: 0f
        val alto = binding.etAlto.text?.toString()?.toFloatOrNull() ?: 0f
        val hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        val divisManual = binding.etPartes.text?.toString()?.toIntOrNull() ?: 0
        val cruce = cruce()

        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val altoHoja = NovaCalculos.altoHoja(alto, hoja)
        val nCorredizas = NovaCalculos.nCorredizas(divisiones)
        val nPuentes = NovaCalculos.nPuentes(divisiones)
        val portafelpa = NovaCalculos.portafelpa(altoHoja)
        val divDePortas = NovaCalculos.divDePortas(divisiones, nCorredizas)

        // Tipo de ventana para fórmulas
        val tipoCalculo = if (tipoNova == TipoNova.APA) "apa" else "ina"
        val mPuentes1 = NovaCalculos.mPuentes1(ancho, divisiones, tipoCalculo)
        val mPuentes2 = NovaCalculos.mPuentes2(ancho, divisiones, tipoCalculo)
        val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruce, tipoCalculo)

        if (tipoNova == TipoNova.APA) {
            // === APARENTE: lógica original NovaApa ===
            // MÚLTIPLE/PUENTE
            binding.txP.text = when {
                divisiones in 6..12 && divisiones % 2 == 0 -> "${NovaCalculos.df1(mPuentes1)} = " +
                        "$nPuentes\n${NovaCalculos.df1(alto)} = ${nPuentes - 1}"
                divisiones == 14 -> "${NovaCalculos.df1(mPuentes1)} = ${nPuentes - 1}" +
                        "\n${NovaCalculos.df1(mPuentes2)} = ${nPuentes - 2}\n${NovaCalculos.df1(alto)} = ${nPuentes - 1}"
                divisiones == 1 -> ""
                else -> "${NovaCalculos.df1(mPuentes1)} = $nPuentes"
            }
            binding.mulLayout.visibility = if (divisiones != 1) View.VISIBLE else View.GONE

            // RIEL
            binding.txR.text = if (divisiones == 1 || nCorredizas == 0) ""
            else NovaPerfilesHelper.calcularRieles(alto, hoja, ancho, mPuentes1, mPuentes2, nPuentes, divisiones)
            binding.lyRiel.visibility = if (divisiones != 1 && nCorredizas > 0) View.VISIBLE else View.GONE

            // TUBO
            binding.txT.text = if (alto > altoHoja) "${NovaCalculos.df1(ancho)} = 1" else ""
            binding.lyTubo.visibility = if (alto > altoHoja) View.VISIBLE else View.GONE

            // PORTAFELPA
            binding.txPf.text = if (divisiones != 1){"${NovaCalculos.df1(portafelpa)} = $divDePortas"}else{""}
            binding.lyPf.visibility = if (divisiones != 1) View.VISIBLE else View.GONE

            // TEE
            val altoMocheta = NovaCalculos.altoMocheta(alto, altoHoja, tubo)
            val us = binding.etU.text?.toString()?.toFloatOrNull() ?: 1.5f
            val te = altoMocheta - us
            binding.txTe.text = if (te > 0) "${NovaCalculos.df1(te)} = $nCorredizas" else ""
            binding.tLayout.visibility = if (te > 0) View.VISIBLE else View.GONE

            // ÁNGULO TOPE
            binding.txTo.text = if (divisiones == 2) "${NovaCalculos.df1(altoHoja - 0.9f)} = 1" else ""
            binding.lyTo.visibility = if (divisiones == 2) View.VISIBLE else View.GONE

            // HACHE
            binding.txH.text = if (nCorredizas > 0) "${NovaCalculos.df1(uFijos)} = $nCorredizas" else ""
            binding.lyH.visibility = if (nCorredizas > 0) View.VISIBLE else View.GONE

            // Ocultar lyUf en aparente
            binding.lyUf.visibility = View.GONE
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

            // PUENTE
            binding.txT.text = resultado.puentes
            binding.txP.text = resultado.puentes

            // RIEL
            binding.txR.text = resultado.rieles

            // U FELPERO
            binding.txUf.text = resultado.uFelpero

            // HACHE
            binding.txH.text = resultado.hache

            // ÁNGULO TOPE
            binding.txTo.text = resultado.angTope
            binding.lyTo.visibility = if (resultado.mostrarAngTope) View.VISIBLE else View.GONE

            // PORTAFELPA
            binding.txPf.text = resultado.portafelpa

            // Visibilidad inaparente
            binding.lyH.visibility = if (resultado.mostrarHache) View.VISIBLE else View.GONE
            binding.lyUf.visibility = if (resultado.mostrarUFelpero) View.VISIBLE else View.GONE
            binding.tLayout.visibility = View.GONE

            // TEE vacío en ina
            binding.txTe.text = ""
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
            val nFijos = NovaCalculos.nFijos(divisiones)
            val nCorredizas = NovaCalculos.nCorredizas(divisiones)
            val uFijos = NovaCalculos.uFijos(ancho, divisiones, cruce, "apa")

            val vidriosFijos = NovaVidriosHelper.calcularVidriosFijos(uFijos, altoHoja, us, nFijos, divisiones, "aparente")
            val vidrioCorre = NovaVidriosHelper.calcularVidriosCorredizos(uFijos, altoHoja, nCorredizas)
            val vidrioMocheta = calcularVidrioMochetaAparente(ancho, alto, hoja, altoHoja, divisiones, uFijos)

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
                cruceExacto = cruceExacto
            )
        }
    }

    private fun calcularVidrioMochetaAparente(ancho: Float, alto: Float, hoja: Float, altoHoja: Float, divisiones: Int, uFijos: Float): String {
        val altoMocheta = NovaCalculos.altoMocheta(alto, altoHoja, tubo)
        val us = binding.etU.text.toString().toFloat()
        val holgura = 0.36f
        val vAltoMocheta = if (puente == "Múltiple" || puente == "gorrito") altoMocheta - holgura
        else altoMocheta - us - holgura
        val mPuentes1 = NovaCalculos.mPuentes1(ancho, divisiones, "apa")
        val anchMota = NovaCalculos.anchMota(mPuentes1)
        val nVidriosMoch = if (divisiones % 2 == 0 && divisiones >= 6) anchMota * 2
        else if (divisiones == 12) anchMota * 3
        else anchMota
        val vAnchoMocheta = (mPuentes1 - ((nVidriosMoch + 1) * 0.36f)) / anchMota

        return if (divisiones == 1) "${df1(vAnchoMocheta)} x ${df1(vAltoMocheta)} = $anchMota"
        else "${df1(vAnchoMocheta)} x ${df1(vAltoMocheta)} = $nVidriosMoch"
    }

    private fun calcularVidrioMochetaInaparente(
        ancho: Float, alto: Float, hoja: Float, altoHoja: Float,
        divisiones: Int, nFijos: Int, uFijos: Float, nCorredizas: Int
    ): String {
        val mas1 = NovaCalculos.df1((alto - altoHoja) + 1).toFloat()
        val axnfxuf = NovaCalculos.df1(((ancho - (nFijos * uFijos))) - 0.6f).toFloat()
        val axnfxuf2 = NovaCalculos.df1(((ancho - (nFijos * uFijos)) / 2) - 0.6f).toFloat()
        val axnfxuivDiseno = NovaCalculos.df1(((ancho - (nFijos * uFijos)) / 3) - 0.6f).toFloat()
        val axnfxufn = NovaCalculos.df1(((ancho - (nFijos * uFijos)) / nCorredizas) - 0.6f).toFloat()

        return when {
            divisiones <= 1 || alto <= hoja -> ""
            divisiones == 4 -> "${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxuf)} = 1"
            divisiones == 8 -> "${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxuf2)} = 2"
            divisiones == 12 -> "${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxuivDiseno)} = 3"
            divisiones == 14 -> "${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxuf2)} = 1\n${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxuf)} = 4"
            else -> "${NovaCalculos.df1(mas1)} x ${NovaCalculos.df1(axnfxufn)} = $nCorredizas"
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
        val puntosU = if (divisiones > 4) puntosU() else ""

        binding.txReferencias.text = NovaUIHelper.generarReferencias(
            ancho, alto, altoHoja, divisiones, nFijos, nCorredizas, siNoMoch, puntosU
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
        val diseno = NovaUIHelper.generarDiseno(ancho, alto, altoHoja, divisiones, siNoMoch = 1, texto, anchMota())

        return when (tipoNova) {
            TipoNova.APA -> "{nova,apa,[$diseno]}"
            TipoNova.INA -> "{nova,ina,[$diseno]}"
            TipoNova.PIV -> "{nova,piv,[$diseno]}"
        }
    }

    private fun puntosU(): String {
        val ancho = binding.etAncho.text.toString().toFloat()
        val divisManual = binding.etPartes.text.toString().toInt()
        val cruce = cruce()
        val divisiones = NovaCalculos.divisiones(ancho, divisManual)
        val tipoCalculo = if (tipoNova == TipoNova.APA) "apa" else "ina"
        val partes = NovaCalculos.uFijos(ancho, divisiones, cruce, tipoCalculo)

        val punto1 = NovaCalculos.df1((partes * 2) - cruce * 2).toFloat()
        val punto2 = NovaCalculos.df1((partes * 4) - cruce * 4).toFloat()
        val punto3 = NovaCalculos.df1((partes * 6) - cruce * 6).toFloat()

        return when (divisiones) {
            5, 6 -> NovaCalculos.df1(((partes * 2) - cruce * 2))
            8, 12 -> NovaCalculos.df1(((partes * 3) - cruce * 2))
            7, 10, 14 -> "${NovaCalculos.df1(punto1)}_${NovaCalculos.df1(punto2)}"
            9, 11, 13, 15 -> "${NovaCalculos.df1(punto1)}_${NovaCalculos.df1(punto2)}_${NovaCalculos.df1(punto3)}"
            else -> ""
        }
    }

    // ==================== SPINNER TUBO ====================
    private fun spinnerTubo() {
        val spinnerOptions = listOf(
            SpinnerTubos(R.drawable.ma_multi, "Múltiple", 1.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo 2 x 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo 2\u215C x 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo.c 1\u00BD", 3.8f),
            SpinnerTubos(R.drawable.ma_multi, "tubo.c 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "paflon 1\u00BD", 3.8f),
            SpinnerTubos(R.drawable.ma_multi, "paflon 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo 2 x 2", 5.0f),
            SpinnerTubos(R.drawable.ma_multi, "gorrito", 2.5f)
        )
        val adapter = AdaptadorSpinner(this, spinnerOptions)
        spinnerListo = false
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (!spinnerListo) {
                    spinnerListo = true
                    return
                }
                val selectedOption = spinnerOptions[position]
                tubo = selectedOption.valor
                puente = selectedOption.text
                binding.tvP.text = selectedOption.text
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

    data class SpinnerTubos(val imageResId: Int, val text: String, val valor: Float)

    class AdaptadorSpinner(
        context: Context, private val options: List<SpinnerTubos>
    ) : ArrayAdapter<SpinnerTubos>(context, 0, options) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View = createItemView(position, convertView, parent)
        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View = createItemView(position, convertView, parent)
        private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.mo_tubo, parent, false)
            view.findViewById<ImageView>(R.id.imgTubo).setImageResource(options[position].imageResId)
            view.findViewById<TextView>(R.id.txNombre).text = options[position].text
            return view
        }
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

        // Modo manual: archivar aquí
        archivarMapas()
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
        Toast.makeText(this, "Archivado", Toast.LENGTH_SHORT).show()
        binding.etAncho.setText("")
        binding.etAlto.setText("")
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
        ModoMasivoHelper.devolverResultado(
            activity = this,
            calculadora = calculadora,
            perfiles = perfiles,
            vidrios = ModoMasivoHelper.texto(binding.txV),
            accesorios = accesorios.filter { it.value.isNotBlank() },
            referencias = ModoMasivoHelper.texto(binding.txReferencias),
            disenoPaquete = paqueteDiseno
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
        binding.txPr.text = mapListas.toString()
        val msg = if (cant > 1) "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
                  else "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
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
