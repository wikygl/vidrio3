package crystal.crystal.taller.puerta

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import crystal.crystal.taller.MedidaActivity
import crystal.crystal.R
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityPuertaPanoBinding
import crystal.crystal.taller.ModoMasivoHelper
import crystal.crystal.taller.nova.NovaUIHelper.esValido
import crystal.crystal.taller.puerta.datos.PuertaRepositorio
import crystal.crystal.taller.puerta.dibujo.DibujoPuerta
import crystal.crystal.taller.puerta.logica.CalculosLina
import crystal.crystal.taller.puerta.logica.CalculosPuerta
import crystal.crystal.taller.puerta.logica.PlanoRotado
import crystal.crystal.taller.puerta.modelos.Puerta
import crystal.crystal.taller.puerta.modelos.Variante
import crystal.crystal.taller.puerta.ui.VariantesAdapter

class PuertasActivity : AppCompatActivity() {

    private data class AjusteTere6(
        val hPuente: Float,
        val mocheta: Float,
        val parante: Float,
        val paranteInterno: Float,
        val cantidadTubos: Int,
        val altoPila: Float
    )

    private data class MaterialInternoLina(
        val nombre: String,
        val anchoCm: Float
    )

    // Constantes (conservadas del original)
    private val hojaRef = CalculosPuerta.HOJA_REF
    private val marco = CalculosPuerta.MARCO
    private val bastidor = CalculosPuerta.BASTIDOR
    private val unoMedio = CalculosPuerta.UNO_MEDIO

    // Estado
    private var clienteActual: String = ""
    private var indicePuerta = 0
    private var puertaActual: Puerta? = null
    private var varianteSeleccionada: String = "Mari h"

    private lateinit var binding: ActivityPuertaPanoBinding
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback

    // Mapa de listas (persistido por proyecto)
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()

    // Variable para controlar el primer click en archivar
    private var primerClickArchivarRealizado = false
    // Tipo de ventana seleccionado desde el panel de formas
    private var tipoVentanaPanel: String = ""
    // Conexión a ventana en costados
    private var ventanaIzquierda: Boolean = false
    private var ventanaDerecha: Boolean = false

    // Metadatos de producción (color aluminio / tipo vidrio)
    private var metaColorAluminio: String = ""
    private var metaTipoVidrio: String = ""
    private var metaAcabadoSuperficial: String = ""
    private var metaObservaciones: String = ""
    private val materialesInternosLina = listOf(
        MaterialInternoLina("Paflon 8.25", 8.25f),
        MaterialInternoLina("Tubo 3.8", 3.8f),
        MaterialInternoLina("Tubo 5", 5f)
    )
    private var materialInternoLinaIndex = 0

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuertaPanoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ==================== Sistema de Proyectos ====================
        ProyectoManager.inicializarDesdeStorage(this)
        proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
            context = this,
            textViewProyecto = binding.tvProyectoActivo,
            activity = this
        )
        ProyectoUIHelper.configurarVisorProyectoActivo(this, binding.tvProyectoActivo)
        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoGestionProyectos(this, proyectoCallback)
        }
        procesarIntentProyecto(intent)

        // ==================== Inicialización UI ====================
        indicePuerta = 0
        inicializarClienteYTipos()
        configurarListenersUI()
        mostrarVariantes()

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etMed1.setText(it.toString()) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etMed2.setText(it.toString()) }
    }

    // ---------------------- Menú Proyecto ----------------------
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let { ProyectoUIHelper.agregarOpcionesMenuProyecto(it) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val manejado = ProyectoUIHelper.manejarSeleccionMenu(
            context = this,
            itemId = item.itemId,
            callback = proyectoCallback
        ) {
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
        }
        return if (manejado) true else super.onOptionsItemSelected(item)
    }

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

    // ---------------------- Inicialización de cliente y puerta ----------------------
    @SuppressLint("SetTextI18n")
    private fun inicializarClienteYTipos() {
        binding.lyCliente.visibility = View.GONE
        intent.extras?.getString("rcliente")?.let { clienteActual = it }
        actualizarPuertaYTitulo()
        binding.tvTitulo.setOnClickListener {
            binding.lyCliente.visibility = View.VISIBLE
            binding.clienteEditxt.setText(clienteActual)
            binding.btGo.setOnClickListener {
                clienteActual = binding.clienteEditxt.text.toString()
                actualizarPuertaYTitulo()
                binding.lyCliente.visibility = View.GONE
                // Actualizar el TextView del cliente para archivado
                binding.txCliente.text = clienteActual
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun actualizarPuertaYTitulo() {
        val lista = PuertaRepositorio.listaPuertas
        if (lista.isNotEmpty()) {
            puertaActual = lista[indicePuerta]
            // Sincronizar varianteSeleccionada con la primera variante del nuevo modelo
            val primeraVariante = variantesParaPuerta(puertaActual?.nombre ?: "").firstOrNull()
            if (primeraVariante != null) varianteSeleccionada = primeraVariante.nombre
            val imagen = when (puertaActual?.nombre) {
                "Mari" -> R.drawable.ic_pp2
                "Dora" -> R.drawable.pdora
                "Adel" -> R.drawable.padelina
                "Mili" -> R.drawable.pmili
                "jeny" -> R.drawable.pjenny
                "Taly" -> R.drawable.pthalia
                "Viky" -> R.drawable.pvicky
                "Lina" -> R.drawable.pjalina
                "Tere" -> R.drawable.ptere
                else -> R.drawable.pjenny
            }
            binding.ivModelo.setImageResource(imagen)
            binding.tvTitulo.text = "Puerta ${puertaActual?.nombre}${if (clienteActual.isNotEmpty()) " ($clienteActual)" else ""}"
        }
        actualizarVisibilidades()
    }

    private fun actualizarVisibilidades() {
        val nombre = puertaActual?.nombre ?: ""
        binding.lyAD.visibility = if (nombre == "Viky" || nombre == "Adel") View.VISIBLE else View.GONE
        binding.lyMarcoVar.visibility = if (nombre == "Lina") View.VISIBLE else View.GONE
        binding.lyPanelDelgadoLina.visibility = if (nombre == "Lina" && varianteSeleccionada in setOf("Lina h", "Lina b")) View.VISIBLE else View.GONE
        if (nombre == "Lina") {
            binding.txZocalo.text = "Gruña"
            binding.etZocalo.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            val valor = binding.etZocalo.text?.toString()?.trim().orEmpty()
            if (valor.isBlank() || valor == "0" || valor == "1") binding.etZocalo.setText("0.8")
        } else {
            binding.txZocalo.text = getString(R.string.z_calo)
            binding.etZocalo.inputType = android.text.InputType.TYPE_CLASS_NUMBER
            if (binding.etZocalo.text?.toString()?.trim() == "0.8") binding.etZocalo.setText("1")
        }
    }

    private fun materialInternoLina(): MaterialInternoLina =
        materialesInternosLina[materialInternoLinaIndex.coerceIn(materialesInternosLina.indices)]

    private fun abrirDialogoMaterialInternoLina() {
        if (puertaActual?.nombre != "Lina") return

        val contenedor = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(32, 16, 32, 0)
        }
        val spinner = android.widget.Spinner(this).apply {
            adapter = android.widget.ArrayAdapter(
                this@PuertasActivity,
                android.R.layout.simple_spinner_dropdown_item,
                materialesInternosLina.map { it.nombre }
            )
            setSelection(materialInternoLinaIndex.coerceIn(materialesInternosLina.indices))
        }
        contenedor.addView(spinner)

        AlertDialog.Builder(this)
            .setTitle("Material interno")
            .setView(contenedor)
            .setPositiveButton("OK") { _, _ ->
                materialInternoLinaIndex = spinner.selectedItemPosition.coerceIn(materialesInternosLina.indices)
                if (varianteSeleccionada.startsWith("Lina")) ejecutarCalculoCompleto()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ---------------------- Listeners de UI ----------------------
    private fun configurarListenersUI() {
        configurarNavegacionEnterEditTexts()

        binding.btModeloPrev.setOnClickListener {
            val lista = PuertaRepositorio.listaPuertas
            if (lista.isNotEmpty()) {
                indicePuerta = (indicePuerta - 1 + lista.size) % lista.size
                actualizarPuertaYTitulo()
                renderizarModeloActual()
            }
        }

        binding.btModeloNext.setOnClickListener {
            val lista = PuertaRepositorio.listaPuertas
            if (lista.isNotEmpty()) {
                indicePuerta = (indicePuerta + 1) % lista.size
                actualizarPuertaYTitulo()
                renderizarModeloActual()
            }
        }

        binding.ivModelo.setOnClickListener {
            abrirDialogoVariantes()
        }

        binding.ivModelo.setOnLongClickListener {
            val intent = Intent(this, MedidaActivity::class.java)
            startActivity(intent)
            true
        }

        binding.lyDivi.setOnClickListener {
            if (puertaActual?.nombre == "Lina") abrirDialogoMaterialInternoLina()
        }
        binding.txDivi.setOnClickListener {
            if (puertaActual?.nombre == "Lina") abrirDialogoMaterialInternoLina()
        }
        binding.btCalcular.setOnClickListener {
            ejecutarCalculoCompleto()
        }

        binding.btArchivar.setOnClickListener {
            // Validar que se hayan ingresado nuevos datos antes de archivar
            if (binding.etMed1.text.toString().isEmpty() || binding.etMed1.text.toString() == "") {
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!primerClickArchivarRealizado) {
                // Primera vez - mostrar diálogo
                DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                    override fun onProyectoSeleccionado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true

                        // CARGAR Map existente del proyecto seleccionado
                        val mapExistente = MapStorage.cargarProyecto(this@PuertasActivity, nombreProyecto)
                        if (mapExistente != null) {
                            mapListas.clear()
                            mapListas.putAll(mapExistente)
                        }

                        mostrarDialogoMetadatosProduccion {
                            archivarMapas()
                            limpiarMedidasYEnfocarAncho()
                        }
                    }

                    override fun onProyectoCreado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true

                        mostrarDialogoMetadatosProduccion {
                            archivarMapas()
                            limpiarMedidasYEnfocarAncho()
                        }
                    }

                    override fun onProyectoEliminado(nombreProyecto: String) {
                        ProyectoUIHelper.actualizarVisorProyectoActivo(this@PuertasActivity, binding.tvProyectoActivo)
                    }
                })
            } else {
                // Verificar que hay proyecto activo antes de archivar directamente
                if (!ProyectoManager.hayProyectoActivo()) {
                    // Si no hay proyecto, resetear y mostrar diálogo de nuevo
                    primerClickArchivarRealizado = false
                    Toast.makeText(this, "No hay proyecto activo. Selecciona uno.", Toast.LENGTH_SHORT).show()

                    // Volver a mostrar diálogo
                    DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                        override fun onProyectoSeleccionado(nombreProyecto: String) {
                            primerClickArchivarRealizado = true

                            // CARGAR Map existente del proyecto seleccionado
                            val mapExistente = MapStorage.cargarProyecto(this@PuertasActivity, nombreProyecto)
                            if (mapExistente != null) {
                                mapListas.clear()
                                mapListas.putAll(mapExistente)
                            }

                            mostrarDialogoMetadatosProduccion {
                                archivarMapas()
                                limpiarMedidasYEnfocarAncho()
                            }
                        }

                        override fun onProyectoCreado(nombreProyecto: String) {
                            primerClickArchivarRealizado = true

                            mostrarDialogoMetadatosProduccion {
                                archivarMapas()
                                limpiarMedidasYEnfocarAncho()
                            }
                        }

                        override fun onProyectoEliminado(nombreProyecto: String) {
                            ProyectoUIHelper.actualizarVisorProyectoActivo(this@PuertasActivity, binding.tvProyectoActivo)
                        }
                    })
                    return@setOnClickListener
                }

                // Ya hay proyecto activo y no es la primera vez
                // CARGAR Map del proyecto activo actual antes de archivar
                val proyectoActivo = ProyectoManager.getProyectoActivo()
                if (proyectoActivo != null) {
                    val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
                    mapListas.clear()
                    if (mapExistente != null) {
                        mapListas.putAll(mapExistente)
                    }
                }

                mostrarDialogoMetadatosProduccion {
                    archivarMapas()
                    limpiarMedidasYEnfocarAncho()
                }
            }
        }

        binding.btArchivar.setOnLongClickListener {
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnLongClickListener true
            MapStorage.guardarMap(this, mapListas)
            Toast.makeText(this, "Map guardado en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            true
        }
    }

    private fun configurarNavegacionEnterEditTexts() {
        val campos = listOf(
            binding.etMed1,
            binding.etMed2,
            binding.etHoja,
            binding.etMarcoVar,
            binding.etDivi,
            binding.etJunki,
            binding.etPiso,
            binding.etZocalo,
            binding.etPanelDelgadoLina,
            binding.etAD,
            binding.etAngulo
        )

        campos.forEach { campo ->
            campo.imeOptions = EditorInfo.IME_ACTION_NEXT
            campo.setSingleLine(true)
            campo.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) (view as? EditText)?.moverCursorAlFinal()
            }
            campo.setOnEditorActionListener { view, actionId, event ->
                val enterFisico = event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP
                val accionSiguiente = actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE
                if (!enterFisico && !accionSiguiente) return@setOnEditorActionListener false

                val actual = campos.indexOf(view)
                val siguiente = campos
                    .drop(actual + 1)
                    .firstOrNull { it.isShown && it.isEnabled }

                if (siguiente != null) {
                    siguiente.requestFocus()
                    siguiente.moverCursorAlFinal()
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun EditText.moverCursorAlFinal() {
        post {
            setSelection(text?.length ?: 0)
        }
    }

    private fun limpiarMedidasYEnfocarAncho() {
        binding.etMed1.setText("")
        binding.etMed2.setText("")
        binding.etMed1.requestFocus()
        binding.etMed1.moverCursorAlFinal()
    }

    // ---------------------- Calcular y renderizar ----------------------
    @SuppressLint("SetTextI18n")
    private fun ejecutarCalculoLina() {
        val ancho = binding.etMed1.text.toString().toFloat()
        val alto  = binding.etMed2.text.toString().toFloat()
        val hHoja = binding.etHoja.text.toString().toFloatOrNull() ?: 0f
        val marcoVar = binding.etMarcoVar.text.toString().toFloatOrNull() ?: 2.2f
        val nDivLina = binding.etDivi.text.toString().toIntOrNull()?.takeIf { it > 0 } ?: 5
        val gruna = binding.etZocalo.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: 0.8f
        val junki = binding.etJunki.text.toString().toFloatOrNull() ?: 0f
        val piso = binding.etPiso.text.toString().toFloatOrNull() ?: 0f
        val panelDelgadoLina = binding.etPanelDelgadoLina.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: 0f
        val materialInterno = materialInternoLina()

        val esPlegado = varianteSeleccionada == "Lina p"
        val hH = if (varianteSeleccionada == "Lina b") {
            CalculosLina.hojaHConPiso(alto, hHoja, piso, esPlegado, marcoVar)
        } else {
            CalculosLina.hojaH(alto, hHoja, esPlegado, marcoVar)
        }
        val hV = CalculosLina.hojaV(ancho, marcoVar)
        val refV = CalculosLina.panelRefV(hH)
        binding.tvTope.text = CalculosLina.topeComun(ancho, hH, marcoVar)

        when (varianteSeleccionada) {
            "Lina h" -> {
                val refH = CalculosLina.panelRefH_h(ancho, marcoVar, gruna, panelDelgadoLina)
                binding.tvMarco.text  = CalculosLina.canal(ancho, alto, marcoVar)
                binding.tvTubo.text   = CalculosLina.tuboPuente(ancho, marcoVar)
                binding.tvPaflon.text = CalculosLina.tres(hH, ancho, refH, marcoVar)
                binding.txMel.text = "Interno"
                binding.tvMel.text = ""
                binding.tvJunki.text  = CalculosLina.junquilloMocheta(ancho, alto, hH, marcoVar, junki)
                val vid = CalculosLina.vidrioH(hH, ancho, alto, marcoVar)
                binding.tvMela.text = CalculosLina.panelH(hH, refV, refH, ancho, marcoVar, gruna, panelDelgadoLina)
                binding.tvVidrios.text = vid
                binding.lyTubo.visibility = View.VISIBLE
            }
            "Lina b" -> {
                binding.tvMarco.text  = CalculosLina.canal(ancho, alto, marcoVar)
                binding.tvTubo.text   = CalculosLina.tuboPuente(ancho, marcoVar)
                binding.tvPaflon.text = CalculosLina.paflonBBastidor(hH, ancho, marcoVar, piso)
                binding.txMel.text = "Interno ${materialInterno.nombre}"
                binding.tvMel.text = CalculosLina.paflonBInterno(hH, ancho, marcoVar, nDivLina, gruna, materialInterno.anchoCm, piso, panelDelgadoLina)
                binding.tvJunki.text  = CalculosLina.junquilloMocheta(ancho, alto, hH, marcoVar, junki)
                val vid = CalculosLina.vidrioH(hH, ancho, alto, marcoVar)
                binding.tvMela.text = CalculosLina.panelB(hH, ancho, marcoVar, nDivLina, gruna, piso, panelDelgadoLina)
                binding.tvVidrios.text = vid
                binding.lyTubo.visibility = View.VISIBLE
            }
            "Lina p" -> {
                val refH = CalculosLina.panelRefH_p(hV)
                binding.tvMarco.text  = "${CalculosLina.marcoPlegado(ancho, alto)}\n${CalculosLina.contraMarco(ancho, alto, marcoVar)}"
                binding.tvTubo.text   = ""
                binding.tvPaflon.text = "${CalculosLina.bandejas(hH, ancho, alto, marcoVar, hV)}\n${CalculosLina.fierroTresDos(hH, ancho, marcoVar, hV)}\n${CalculosLina.platina(hH)}"
                binding.txMel.text = "Interno"
                binding.tvMel.text = ""
                binding.tvJunki.text  = CalculosLina.junquilloMocheta(ancho, alto, hH, marcoVar, junki)
                binding.tvMela.text = "${CalculosLina.panelP(hH, ancho, alto, refV, refH)}\n${CalculosLina.plancha(hH, ancho, alto, marcoVar, hV)}"
                binding.tvVidrios.text = ""
                binding.lyTubo.visibility = View.GONE
            }
        }

        val mochetaLina = alto - (hH + marcoVar + 2.5f)
        binding.txRefe.text = if (varianteSeleccionada == "Lina b") {
            CalculosPuerta.referen(ancho, alto, hH, mochetaLina)
        } else {
            "anch ${CalculosPuerta.df1(ancho)} x alt ${CalculosPuerta.df1(alto)}\nAlto hoja = ${CalculosPuerta.df1(hH)}"
        }
        binding.txCliente.text = clienteActual
        binding.tvEnsayo.text  = ""
        binding.tvEnsayo2.text = ""
        renderizarModeloActual()
    }

    private fun ejecutarCalculoCompleto() {
        try {
            if (varianteSeleccionada.startsWith("Lina")) {
                ejecutarCalculoLina()
                return
            }
            // Entradas
            val ancho = binding.etMed1.text.toString().toFloat()
            val alto = binding.etMed2.text.toString().toFloat()
            val nZocalos = binding.etZocalo.text.toString().toIntOrNull() ?: 0
            val nDiv = binding.etDivi.text.toString().toIntOrNull() ?: 0
            val junki = binding.etJunki.text.toString().toFloatOrNull() ?: 0f
            val piso = binding.etPiso.text.toString().toFloatOrNull() ?: 0f
            val hHoja = binding.etHoja.text.toString().toFloatOrNull() ?: 0f
            val angulo = binding.etAngulo.text.toString().toFloatOrNull() ?: 0f
            binding.tvMela.text = ""
            binding.tvMel.text = ""

            // Marco lateral según conexión a ventana (canal=2.2, tubo=2.5)
            val tuboW = 2.5f
            val marcoIzq = if (ventanaIzquierda) tuboW else marco
            val marcoDer = if (ventanaDerecha) tuboW else marco
            val totalMarco = marcoIzq + marcoDer

            // Cálculos base
            var hPuente = CalculosPuerta.hPuente(alto, hHoja, piso, hojaRef, marco)
            var mocheta = CalculosPuerta.mocheta(alto, hPuente, marco)
            val marcoSup = ancho - totalMarco
            var tubo = if (mocheta > 0f) CalculosPuerta.df1(marcoSup) else ""
            val paflon = ((ancho - totalMarco) - 1f) - (2f * bastidor)
            val nZ = CalculosPuerta.nZocalo(nZocalos)
            var parante = CalculosPuerta.parante(hPuente, piso)
            var paranteInt = CalculosPuerta.paranteInterno(parante, nZ, bastidor)
            val ajusteTere6 = if (varianteSeleccionada == "Tere 6") {
                calcularAjusteTere6(alto, hPuente, piso, nZ).also {
                    hPuente = it.hPuente
                    mocheta = it.mocheta
                    parante = it.parante
                    paranteInt = it.paranteInterno
                    tubo = if (mocheta > 0f) CalculosPuerta.df1(marcoSup) else ""
                }
            } else {
                null
            }
            val divisTam = CalculosPuerta.divisiones(parante, nZ, nDiv.toFloat(), bastidor)
            val nPfvcal = CalculosPuerta.nPfvcal(nDiv)
            val nPaflones = CalculosPuerta.nPaflones(nDiv, nZocalos)
            val zocalo = CalculosPuerta.zocalo(nZ, bastidor)

            // Salidas UI (materiales)
            val countMarcos = (if (!ventanaIzquierda) 1 else 0) + (if (!ventanaDerecha) 1 else 0)
            binding.tvMarco.text = if (countMarcos > 0)
                "${CalculosPuerta.df1(alto)} = $countMarcos\n${CalculosPuerta.df1(marcoSup)} = 1"
            else
                "${CalculosPuerta.df1(marcoSup)} = 1"
            binding.tvTope.text = "${CalculosPuerta.df1(marcoSup)} = 1\n${CalculosPuerta.df1(hPuente)} = 2"
            val countTubos = (if (ventanaIzquierda) 1 else 0) + (if (ventanaDerecha) 1 else 0)
            binding.tvTubo.text = buildString {
                if (tubo.isNotEmpty()) append("$tubo = 1")
                if (countTubos > 0) {
                    if (tubo.isNotEmpty()) append("\n")
                    append("${CalculosPuerta.df1(alto)} = $countTubos")
                }
                if (ajusteTere6 != null) {
                    if (isNotEmpty()) append("\n")
                    append("${CalculosPuerta.df1(paflon)} = ${ajusteTere6.cantidadTubos}")
                }
            }

            // Paflones según variante
            binding.tvPaflon.text = deduplicar(when (varianteSeleccionada) {
                "Tere 6" -> "${CalculosPuerta.df1(paflon)} = ${nZ + 1}\n${CalculosPuerta.df1(parante)} = 2"
                "Mari h" -> "${CalculosPuerta.df1(paflon)} = $nPaflones\n${CalculosPuerta.df1(parante)} = 2"
                "Mari v" -> "${CalculosPuerta.df1(paflon)} = ${nZ}\n${CalculosPuerta.df1(parante)} = 2\n${CalculosPuerta.df1(paranteInt)} = ${nDiv - 1}"
                "Mari d" -> {
                    val diagonales = CalculosPuerta.textoPaflonesMariD(paflon, paranteInt, nDiv, bastidor, angulo)
                    buildString {
                        append("${CalculosPuerta.df1(paflon)} = ${nZ + 1}")
                        if (diagonales.isNotBlank()) append("\n$diagonales")
                        append("\n${CalculosPuerta.df1(parante)} = 2")
                    }
                }
                "Taly h", "Taly d" -> {
                    // Pares de paflones laterales hasta vacío ≤ 20 cm
                    var gapTaly = paflon
                    var paresTaly = 0
                    while (gapTaly > 20f && gapTaly >= bastidor * 2f) { paresTaly++; gapTaly -= 2f * bastidor }
                    val altInterno = paranteInt
                    val nDivisores = maxOf(0, nDiv - 1)
                    buildString {
                        append("${CalculosPuerta.df1(parante)} = 2\n")
                        append("${CalculosPuerta.df1(paflon)} = 2")
                        if (paresTaly > 0) append("\n${CalculosPuerta.df1(altInterno)} = ${paresTaly * 2}")
                        append("\n${CalculosPuerta.df1(gapTaly)} = 2")  // topInner + botInner
                        if (nZ > 1) append("\n${CalculosPuerta.df1(gapTaly)} = ${nZ - 1}")  // zócalo extra
                        if (nDivisores > 0) {
                            val divLen = if (varianteSeleccionada == "Taly d" && angulo != 0f) {
                                val rad = Math.toRadians(angulo.toDouble())
                                // Barra cortada vertical: longitud = (gap + bastidor*sin) / cos
                                val rawLen = (gapTaly / Math.cos(rad) + bastidor * Math.tan(rad)).toFloat()
                                val ceiled = kotlin.math.ceil(rawLen * 10).toInt() / 10f
                                if (ceiled == ceiled.toLong().toFloat()) ceiled.toLong().toString()
                                else "%.1f".format(ceiled).replace(",", ".")
                            } else {
                                CalculosPuerta.df1(gapTaly)
                            }
                            append("\n$divLen = $nDivisores")
                        }
                    }
                }
                else -> "${CalculosPuerta.df1(paflon)} = $nPaflones\n${CalculosPuerta.df1(parante)} = 2"
            })

            // Junkillos y vidrios
            binding.tvJunki.text = if (varianteSeleccionada == "Mari d") {
                CalculosPuerta.textoJunkillosMariD(paflon, paranteInt, nDiv, bastidor, junki, angulo)
            } else if (varianteSeleccionada == "Taly h" || varianteSeleccionada == "Taly d") {
                var gapTalyJ = paflon
                while (gapTalyJ > 20f && gapTalyJ >= bastidor * 2f) { gapTalyJ -= 2f * bastidor }
                val zoneHcm = paranteInt - 2f * bastidor
                val barrasJ = maxOf(0, nDiv - 1)
                val gapSeccion = if (nDiv > 0) (zoneHcm - barrasJ * bastidor) / nDiv else zoneHcm

                fun ceilFmt(v: Double): String {
                    val c = kotlin.math.ceil(v * 10).toInt() / 10f
                    return if (c == c.toLong().toFloat()) c.toLong().toString()
                    else "%.1f".format(c).replace(",", ".")
                }

                buildString {
                    if (varianteSeleccionada == "Taly h") {
                        // Horizontales (top/bot de cada sección), sin ángulo
                        append("${CalculosPuerta.df1(gapTalyJ - 2f * junki)} = ${nDiv * 2}\n")
                        // Parantes de cada sección
                        append("${CalculosPuerta.df1(gapSeccion - 2f * junki)} = ${nDiv * 2}")
                    } else { // Taly d
                        val rad = Math.toRadians(angulo.toDouble())
                        val extraJ = junki.toDouble() * Math.tan(rad)
                        // Horizontales top/bot (sin ángulo), solo 2 (extremos del vacío)
                        append("${CalculosPuerta.df1(gapTalyJ - 2f * junki)} = 2\n")
                        // Diagonales a lo largo de los divisores: (nDiv-1) divisores × 2 caras
                        if (nDiv > 1) {
                            val diagJ = gapTalyJ / Math.cos(rad) + extraJ
                            append("${ceilFmt(diagJ)} = ${(nDiv - 1) * 2}\n")
                        }
                        // Parantes extremos: 2 largos (contra el bastidor) + resto con adición tangencial
                        append("${CalculosPuerta.df1(gapSeccion - 2f * junki)} = 2\n")
                        // Parantes cortos (extremos interiores) + ambos lados de secciones medias
                        val countConExtra = nDiv * 2 - 2
                        if (countConExtra > 0) {
                            append("${ceilFmt(gapSeccion - 2.0 * junki + extraJ)} = $countConExtra")
                        }
                    }
                    // Mocheta
                    if (mocheta > 0f) {
                        if (isNotEmpty()) append("\n")
                        append("${CalculosPuerta.df1(marcoSup)} = 2\n")
                        append("${CalculosPuerta.df1(mocheta - 2f * junki)} = 2")
                    }
                }
            } else {
                CalculosPuerta.textoJunkillos(varianteSeleccionada, junki, mocheta, nPfvcal, paflon, bastidor, nDiv, paranteInt, marcoSup)
            }
            binding.tvVidrios.text = if (varianteSeleccionada == "Taly h" || varianteSeleccionada == "Taly d") {
                var gapTalyV = paflon
                while (gapTalyV > 20f && gapTalyV >= bastidor * 2f) { gapTalyV -= 2f * bastidor }
                val zoneHv = paranteInt - 2f * bastidor
                val barrasV = maxOf(0, nDiv - 1)
                val gapSeccionV = if (nDiv > 0) (zoneHv - barrasV * bastidor) / nDiv else zoneHv
                val holgura = 0.5f
                val anchVf = gapTalyV - 2f * junki - holgura
                val altVBase = gapSeccionV - 2f * junki - holgura
                val altVf = if (varianteSeleccionada == "Taly d" && angulo != 0f) {
                    val rad = Math.toRadians(angulo.toDouble())
                    altVBase + anchVf * Math.tan(rad).toFloat()
                } else altVBase
                "${CalculosPuerta.df1(anchVf)} x ${CalculosPuerta.df1(altVf)} = $nDiv"
            } else {
                CalculosPuerta.textoVidrios(varianteSeleccionada, junki, paflon, divisTam, bastidor, nDiv, paranteInt, marcoSup, mocheta, angulo)
            }

            // Referencias
            binding.txRefe.text = CalculosPuerta.referen(ancho, alto, hPuente, mocheta)
            binding.lyTubo.visibility = if (binding.tvTubo.text.isNullOrBlank()) View.GONE else View.VISIBLE

            // Texto ensayo de paños (como original)
            binding.tvEnsayo.text = if (ajusteTere6 != null) {
                "Tubo 6 = ${ajusteTere6.cantidadTubos}\nPila = ${CalculosPuerta.df1(ajusteTere6.altoPila)}"
            } else {
                CalculosPuerta.textoPanos(zocalo, nPfvcal, divisTam, bastidor)
            }
            binding.tvEnsayo2.text = if (ajusteTere6 != null) {
                ""
            } else {
                CalculosPuerta.textoResumenArray(
                    alto,
                    marcoSup,
                    tubo,
                    paflon,
                    parante,
                    zocalo,
                    nDiv,
                    hPuente,
                    CalculosPuerta.partesV(paflon, unoMedio),
                    CalculosPuerta.parteH(divisTam, unoMedio),
                    CalculosPuerta.vidrioM(marcoSup, mocheta, junki)
                )
            }

            // Actualizar el TextView del cliente con el valor actual
            binding.txCliente.text = clienteActual

            // Dibujo
            renderizarModeloActual()

        } catch (e: Exception) {
            Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calcularAjusteTere6(alto: Float, hPuenteBase: Float, piso: Float, nZ: Int): AjusteTere6 {
        val tubo6 = 6f
        val gruma = 0.5f
        val paranteBase = CalculosPuerta.parante(hPuenteBase, piso)
        val interiorBase = (CalculosPuerta.paranteInterno(paranteBase, nZ, bastidor)).coerceAtLeast(tubo6)

        fun altoPila(cantidad: Int): Float {
            val c = cantidad.coerceAtLeast(1)
            return (c * tubo6) + ((c - 1) * gruma)
        }

        val cantidadSuperior = kotlin.math.ceil(((interiorBase + gruma) / (tubo6 + gruma)).toDouble()).toInt().coerceAtLeast(1)
        val pilaSuperior = altoPila(cantidadSuperior)
        val hPuenteSuperior = hPuenteBase + (pilaSuperior - interiorBase)
        val maxHoja = alto - marco

        val usarSuperior = hPuenteSuperior <= maxHoja + 0.05f
        val cantidadFinal = if (usarSuperior) {
            cantidadSuperior
        } else {
            kotlin.math.floor(((interiorBase + gruma) / (tubo6 + gruma)).toDouble()).toInt().coerceAtLeast(1)
        }
        val pilaFinal = altoPila(cantidadFinal)
        val hPuenteFinal = if (usarSuperior) {
            hPuenteSuperior
        } else {
            hPuenteBase - (interiorBase - pilaFinal)
        }
        val paranteFinal = CalculosPuerta.parante(hPuenteFinal, piso)
        val mochetaFinal = CalculosPuerta.mocheta(alto, hPuenteFinal, marco)

        return AjusteTere6(
            hPuente = hPuenteFinal,
            mocheta = mochetaFinal,
            parante = paranteFinal,
            paranteInterno = CalculosPuerta.paranteInterno(paranteFinal, nZ, bastidor),
            cantidadTubos = cantidadFinal,
            altoPila = pilaFinal
        )
    }

    private fun renderizarModeloActual() {
        // Solo los modelos con renderizado implementado generan bitmap
        val nombreModelo = puertaActual?.nombre ?: return
        if (nombreModelo != "Mari" && nombreModelo != "Taly" && nombreModelo != "Lina" && nombreModelo != "Adel" && nombreModelo != "Mili" && nombreModelo != "jeny" && nombreModelo != "Dora" && nombreModelo != "Tere") return
        val anchoPuertaCm = binding.etMed1.text.toString().toFloatOrNull() ?: return
        val altoPuertaCm = binding.etMed2.text.toString().toFloatOrNull() ?: return
        val nZocalos = binding.etZocalo.text.toString().toIntOrNull() ?: 0
        val nDiv = binding.etDivi.text.toString().toIntOrNull() ?: 0
        val tipoDivision = when (varianteSeleccionada) {
            "Mari v" -> "V"
            "Mari d" -> "D"
            else -> "H"
        }

        // Hoja — ancho depende del tipo de marco lateral (canal 2.2 o tubo 2.5)
        val marcoIzqRender = if (ventanaIzquierda) 2.5f else marco
        val marcoDerRender = if (ventanaDerecha) 2.5f else marco
        val anchoHojaCm = anchoPuertaCm - (marcoIzqRender + marcoDerRender + 1f)
        var altoHojaCm = CalculosPuerta.hPuente(
            altoPuertaCm,
            binding.etHoja.text.toString().toFloatOrNull() ?: 0f,
            binding.etPiso.text.toString().toFloatOrNull() ?: 0f,
            hojaRef,
            marco
        )
        val nZRender = CalculosPuerta.nZocalo(nZocalos)
        val ajusteTere6 = if (nombreModelo == "Tere" && varianteSeleccionada == "Tere 6") {
            calcularAjusteTere6(altoPuertaCm, altoHojaCm, binding.etPiso.text.toString().toFloatOrNull() ?: 0f, nZRender).also {
                altoHojaCm = it.hPuente
            }
        } else {
            null
        }

        val anchoContenedor = anchoPuertaCm * 3
        val altoContenedor = altoPuertaCm * 3
        val angulo = binding.etAngulo.text.toString().toFloatOrNull() ?: 0f
        val pisoG = binding.etPiso.text.toString().toFloatOrNull() ?: 0f
        // Gap visual = hPuente - parante (ya incluye la holgura correcta)
        val gapPisoCm = altoHojaCm - CalculosPuerta.parante(altoHojaCm, pisoG)

        val bmp: Bitmap = if (nombreModelo == "Tere" && varianteSeleccionada == "Tere 6" && ajusteTere6 != null) {
            DibujoPuerta.generarBitmapTere6(
                context = this,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = altoHojaCm,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = nZRender,
                cantidadTubos = ajusteTere6.cantidadTubos
            )
        } else if (nombreModelo == "Tere") {
            DibujoPuerta.generarBitmapTere(
                context = this,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = altoHojaCm,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = nZRender
            )
        } else if (nombreModelo == "Dora") {
            DibujoPuerta.generarBitmapDora(
                context = this,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = altoHojaCm,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = CalculosPuerta.nZocalo(nZocalos)
            )
        } else if (nombreModelo == "jeny") {
            val nCols = nDiv.takeIf { it >= 1 } ?: 3
            DibujoPuerta.generarBitmapJeny(
                context = this,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = altoHojaCm,
                nCols = nCols,
                nRows = nCols,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = CalculosPuerta.nZocalo(nZocalos)
            )
        } else if (nombreModelo == "Mili") {
            DibujoPuerta.generarBitmapMili(
                context = this,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = altoHojaCm,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = CalculosPuerta.nZocalo(nZocalos)
            )
        } else if (nombreModelo == "Adel") {
            val hHojaAdel = CalculosLina.hojaH(altoPuertaCm, binding.etHoja.text.toString().toFloatOrNull() ?: 0f, false)
            val nDivAdel  = nDiv.takeIf { it >= 1 } ?: 3        // divisiones de vidrio (columna derecha)
            val nPafAdel  = binding.etAD.text.toString().toIntOrNull() ?: 3  // paflones (columna izquierda)
            DibujoPuerta.generarBitmapAdel(
                context = this,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = hHojaAdel,
                nDivisiones = nDivAdel,
                variante = varianteSeleccionada,
                nPaflones = nPafAdel,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                pisoCm = gapPisoCm,
                nZocalo = CalculosPuerta.nZocalo(nZocalos)
            )
        } else if (nombreModelo == "Lina") {
            val marcoLina = binding.etMarcoVar.text.toString().toFloatOrNull() ?: 2.2f
            val grunaLina = binding.etZocalo.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: 0.8f
            val panelDelgadoLina = binding.etPanelDelgadoLina.text.toString().toFloatOrNull()?.takeIf { it > 0f } ?: 0f
            val hHojaInputLina = binding.etHoja.text.toString().toFloatOrNull() ?: 0f
            val hHojaLina = if (varianteSeleccionada == "Lina b") {
                CalculosLina.hojaHConPiso(altoPuertaCm, hHojaInputLina, pisoG, false, marcoLina)
            } else {
                CalculosLina.hojaH(altoPuertaCm, hHojaInputLina, varianteSeleccionada == "Lina p", marcoLina)
            }
            val pisoLina = if (varianteSeleccionada == "Lina b") {
                hHojaLina - CalculosPuerta.parante(hHojaLina, pisoG)
            } else {
                gapPisoCm
            }
            val nPanelesLina = nDiv.takeIf { it >= 3 } ?: 5
            DibujoPuerta.generarBitmapLinaH(
                context = this,
                anchoCm = anchoPuertaCm,
                altoCm = altoPuertaCm,
                altoHojaCm = hHojaLina,
                nPaneles = nPanelesLina,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                marcoCm = marcoLina,
                pisoCm = pisoLina,
                grumaCm = grunaLina,
                panelDelgadoCm = panelDelgadoLina,
                mostrarVidrioCentral = varianteSeleccionada != "Lina b"
            )
        } else if (varianteSeleccionada.startsWith("Taly")) {
            DibujoPuerta.generarBitmapTaly(
                context = this,
                anchoPuertaCm = anchoPuertaCm,
                altoPuertaCm = altoPuertaCm,
                anchoHojaCm = anchoHojaCm,
                altoHojaCm = altoHojaCm,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                numeroDivisiones = nDiv,
                anguloGrados = if (varianteSeleccionada == "Taly d") angulo else 0f,
                marcoCmIzq = marcoIzqRender,
                marcoCmDer = marcoDerRender,
                nZocalo = CalculosPuerta.nZocalo(nZocalos),
                pisoCm = gapPisoCm
            )
        } else {
            DibujoPuerta.generarBitmapPuerta(
                context = this,
                anchoPuertaCm = anchoPuertaCm,
                altoPuertaCm = altoPuertaCm,
                anchoHojaCm = anchoHojaCm,
                altoHojaCm = altoHojaCm,
                numeroZocalos = CalculosPuerta.nZocalo(nZocalos),
                numeroDivisiones = nDiv,
                anchoContenedor = anchoContenedor,
                altoContenedor = altoContenedor,
                tipoDivision = tipoDivision,
                anguloGrados = angulo,
                marcoCmIzq = marcoIzqRender,
                marcoCmDer = marcoDerRender,
                pisoCm = gapPisoCm
            )
        }
        binding.ivModelo.setImageBitmap(bmp)
        DibujoPuerta.guardarBitmapEnCache(this, bmp)
    }

    private fun prepararPlanoRotadoYResumen(
        paflon: Float,
        paranteInterno: Float,
        nDiv: Int,
        bastidor: Float,
        angulo: Float
    ): String {
        val datos = PlanoRotado.generarDatosPlano(paflon, paranteInterno, nDiv, bastidor)
        val rotado = PlanoRotado.rotarDatosPlano(datos, angulo, paflon, paranteInterno)
        val texto = PlanoRotado.obtenerTextoDistancias(rotado)
        return PlanoRotado.agruparMedidasDesdeTexto(texto)
    }

    // ---------------------- Variantes ----------------------
    private fun variantesParaPuerta(nombrePuerta: String): List<Variante> = when (nombrePuerta) {
        "Mari" -> listOf(
            Variante("Mari h", R.drawable.ic_pp2),
            Variante("Mari v", R.drawable.mariv),
            Variante("Mari d", R.drawable.marid)
        )
        "Adel" -> listOf(
            Variante("Adel p1", R.drawable.padelina),
            Variante("Adel v", R.drawable.padelinz),
            Variante("Adel p2", R.drawable.pvicky),
            Variante("Adel p3", R.drawable.padelinx)
        )
        "Taly" -> listOf(
            Variante("Taly h", R.drawable.pthalia),
            Variante("Taly d", R.drawable.ptalyd)
        )
        "Lina" -> listOf(
            Variante("Lina h", R.drawable.pjalina),
            Variante("Lina b", R.drawable.pjose),
            Variante("Lina p", R.drawable.pjosed)
        )
        "Mili" -> listOf(Variante("Mili", R.drawable.pmili))
        "jeny" -> listOf(Variante("Jeny", R.drawable.pjenny))
        "Dora" -> listOf(Variante("Dora", R.drawable.pdora))
        "Tere" -> listOf(
            Variante("Tere", R.drawable.ptere),
            Variante("Tere 6", R.drawable.tere6)
        )
        "Viky" -> listOf(Variante("Variante Única", R.drawable.pvicky))
        else -> emptyList()
    }

    private fun abrirDialogoVariantes() {
        val variantes = variantesParaPuerta(puertaActual?.nombre ?: "")
        if (variantes.isEmpty()) return

        val dp36 = (36 * resources.displayMetrics.density).toInt()

        // Fila: [cbIzq] [puertaventa] [cbDer] — máx 36dp de alto
        val filaVentana = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(24, 4, 24, 4)
        }
        val cbIzq = android.widget.CheckBox(this).apply { text = "Izq"; isChecked = ventanaIzquierda }
        val ivVentana = android.widget.ImageView(this).apply {
            setImageResource(R.drawable.puertaventa)
            layoutParams = android.widget.LinearLayout.LayoutParams(0, dp36, 1f)
            scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
        }
        val cbDer = android.widget.CheckBox(this).apply { text = "Der"; isChecked = ventanaDerecha }
        filaVentana.addView(cbIzq)
        filaVentana.addView(ivVentana)
        filaVentana.addView(cbDer)

        val rv = androidx.recyclerview.widget.RecyclerView(this).apply {
            layoutManager = GridLayoutManager(this@PuertasActivity, 2)
            setPadding(16, 8, 16, 16)
        }
        val contenedor = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            addView(filaVentana)
            addView(rv)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Variantes — ${puertaActual?.nombre}")
            .setView(contenedor)
            .setPositiveButton("OK") { _, _ ->
                ventanaIzquierda = cbIzq.isChecked
                ventanaDerecha = cbDer.isChecked
            }
            .setNegativeButton("Cerrar", null)
            .create()

        rv.adapter = VariantesAdapter(variantes) { v ->
            varianteSeleccionada = v.nombre
            binding.ivModelo.setImageResource(v.imagen)
            ventanaIzquierda = cbIzq.isChecked
            ventanaDerecha = cbDer.isChecked
            actualizarVisibilidades()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun mostrarVariantes() {

        // Panel derecho: selector de tipo de ventana por forma
        binding.txMedCant.setOnClickListener {
            binding.lyPanelModelos.visibility =
                if (binding.lyPanelModelos.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        data class ModeloPanelP(val drawableRes: Int, val tipo: String)
        val items = listOf(
            binding.itemVplano  to ModeloPanelP(R.drawable.vplano,  "plano"),
            binding.itemVenl    to ModeloPanelP(R.drawable.venl,    "en_l"),
            binding.itemVenc    to ModeloPanelP(R.drawable.venc,    "en_c"),
            binding.itemVcurvo  to ModeloPanelP(R.drawable.vcurvo,  "curvo"),
            binding.itemVserie  to ModeloPanelP(R.drawable.vserie,  "serie")
        )
        for ((view, modelo) in items) {
            view.setOnClickListener {
                tipoVentanaPanel = modelo.tipo
                binding.ivModelo.setImageResource(modelo.drawableRes)
                binding.lyPanelModelos.visibility = View.GONE
            }
        }
    }

    // ==================== FUNCIONES PARA ARCHIVAR ====================
    private fun obtenerPrefijo(): String {
        return "P" // Puertas - prefijo para identificar cálculos de puertas
    }

    private fun escaparCampoArchivo(raw: String): String {
        return raw
            .replace("\n", " / ")
            .replace("\r", " ")
            .replace("-", "_")
            .replace("<", "(")
            .replace(">", ")")
            .trim()
    }

    private fun etiquetaPaquete(prefijo: String, numero: Int): String {
        val cliente = clienteActual.ifBlank {
            binding.txCliente.text?.toString()?.trim().orEmpty()
        }.ifBlank {
            ProyectoManager.getProyectoActivo().orEmpty()
        }.ifBlank {
            "sin cliente"
        }
        return "$prefijo$numero, $cliente"
    }

    private fun sufijoMetadatosProduccion(): String {
        return "-MAT<alu:${metaColorAluminio.ifBlank { "null" }};" +
            "vid:${metaTipoVidrio.ifBlank { "null" }};" +
            "acabado_sup:${metaAcabadoSuperficial.ifBlank { "null" }};" +
            "obs:${metaObservaciones.ifBlank { "null" }}>"
    }

    private fun disenoSimbolicoV2(numeroProducto: Int): String {
        val cliente = escaparCampoArchivo(
            clienteActual.ifBlank {
                binding.txCliente.text?.toString()?.trim().orEmpty()
            }.ifBlank {
                ProyectoManager.getProyectoActivo().orEmpty()
            }.ifBlank {
                "sin cliente"
            }
        )
        val ancho = binding.etMed1.text?.toString()?.toFloatOrNull() ?: 0f
        val alto = binding.etMed2.text?.toString()?.toFloatOrNull() ?: 0f
        val hoja = binding.etHoja.text?.toString()?.toFloatOrNull() ?: 0f
        val cantidad = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        val modelo = escaparCampoArchivo("${puertaActual?.nombre.orEmpty()} ${varianteSeleccionada}".trim()).ifBlank { "x" }
        return buildString {
            append("C<").append(cliente).append(">")
            append("-M<").append(CalculosPuerta.df1(ancho)).append(",").append(CalculosPuerta.df1(alto)).append(",").append(CalculosPuerta.df1(hoja))
            append(",null,null,").append(cantidad).append(">")
            append("-P<P,p,a,p,").append(numeroProducto).append(">")
            append("-G<p,r,m,").append(modelo).append(">")
            append(sufijoMetadatosProduccion())
        }
    }

    private fun mostrarDialogoMetadatosProduccion(onContinuar: () -> Unit) {
        val pad = (16 * resources.displayMetrics.density).toInt()
        val contenedor = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(pad, pad, pad, 0)
        }
        val etColor = android.widget.EditText(this).apply {
            hint = "Color aluminio (ej: negro)"
            setText(metaColorAluminio)
        }
        val etVidrio = android.widget.EditText(this).apply {
            hint = "Tipo vidrio (ej: incoloro 6mm)"
            setText(metaTipoVidrio)
        }
        val etAcabadoSup = android.widget.EditText(this).apply {
            hint = "Acabado superficial (opcional)"
            setText(metaAcabadoSuperficial)
        }
        val etObs = android.widget.EditText(this).apply {
            hint = "Observaciones (opcional)"
            setText(metaObservaciones)
        }
        contenedor.addView(etColor)
        contenedor.addView(etVidrio)
        contenedor.addView(etAcabadoSup)
        contenedor.addView(etObs)

        AlertDialog.Builder(this)
            .setTitle("Metadatos de producción")
            .setView(contenedor)
            .setPositiveButton("Guardar y archivar") { _, _ ->
                metaColorAluminio = etColor.text?.toString()?.trim().orEmpty()
                metaTipoVidrio = etVidrio.text?.toString()?.trim().orEmpty()
                metaAcabadoSuperficial = etAcabadoSup.text?.toString()?.trim().orEmpty()
                metaObservaciones = etObs.text?.toString()?.trim().orEmpty()
                onContinuar()
            }
            .setNeutralButton("Omitir") { _, _ -> onContinuar() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun deduplicar(texto: String): String {
        val regex = Regex("""^(.+?)\s*=\s*(\d+)$""")
        val orden = mutableListOf<String>()
        val sumas = linkedMapOf<String, Int>()
        for (linea in texto.lines()) {
            val m = regex.matchEntire(linea.trim())
            if (m != null) {
                val clave = m.groupValues[1].trim()
                val cant = m.groupValues[2].toInt()
                if (clave !in sumas) orden.add(clave)
                sumas[clave] = (sumas[clave] ?: 0) + cant
            } else {
                val k = "\u0000$linea"
                orden.add(k)
                sumas[k] = 0
            }
        }
        return orden.joinToString("\n") { k ->
            if (k.startsWith("\u0000")) k.drop(1) else "$k = ${sumas[k]}"
        }
    }

    private fun archivarMapas() {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
            mapListas.clear()
            if (mapExistente != null) {
                mapListas.putAll(mapExistente)
            }
        }

        val prefijo = obtenerPrefijo()
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        var ultimoID = ""

        for (u in 1..cant) {
            val siguienteNumero = ProyectoManager.obtenerSiguienteContadorPorPrefijo(this, prefijo)
            val identificadorPaquete = etiquetaPaquete(prefijo, siguienteNumero)
            ultimoID = identificadorPaquete

            if (esValido(binding.lyClienteData)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvCliente, binding.txCliente, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyMed1)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txMed1, binding.etMed1, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyAlto)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txMed2, binding.etMed2, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyReferencias)) {
                ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvReferencias, binding.txRefe, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyMarco)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txMarco, binding.tvMarco, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTubo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txTubo, binding.tvTubo, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyPaflon)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txPaflon, binding.tvPaflon, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyJunki)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txJunki, binding.tvJunki, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTope)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txTope, binding.tvTope, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyMela)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txMela, binding.tvMela, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyMel)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txMel, binding.tvMel, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyVidrios)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txVidrios, binding.tvVidrios, mapListas, identificadorPaquete)
            }
            val paqueteV2 = disenoSimbolicoV2(siguienteNumero)
            mapListas.getOrPut("DisenoSimbolicoV2") { mutableListOf() }
                .add(mutableListOf(paqueteV2, "", identificadorPaquete))

            ProyectoManager.actualizarContadorPorPrefijo(this, prefijo, siguienteNumero)
        }

        MapStorage.guardarMap(this, mapListas)
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)

        val msg = if (cant > 1) "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
                  else "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            val perfiles = mapOf(
                "Marco" to ModoMasivoHelper.texto(binding.tvMarco),
                "Tubo" to ModoMasivoHelper.texto(binding.tvTubo),
                "Paflón" to ModoMasivoHelper.texto(binding.tvPaflon)
            ).filter { it.value.isNotBlank() }

            val accesorios = mapOf(
                "Tope" to ModoMasivoHelper.texto(binding.tvTope),
                "Junquillo" to ModoMasivoHelper.texto(binding.tvJunki)
            ).filter { it.value.isNotBlank() }

            ModoMasivoHelper.devolverResultado(
                activity = this,
                calculadora = "Puerta",
                perfiles = perfiles,
                vidrios = ModoMasivoHelper.texto(binding.tvVidrios),
                accesorios = accesorios,
                referencias = ModoMasivoHelper.texto(binding.txRefe)
            )
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}
