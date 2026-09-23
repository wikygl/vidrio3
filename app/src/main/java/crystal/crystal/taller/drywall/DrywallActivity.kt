package crystal.crystal.taller.drywall

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityDrywallBinding
import crystal.crystal.taller.ControladorColaMedidas
import crystal.crystal.taller.ModoMasivoHelper

/**
 * Drywall: tabiques, forros y cielos rasos. Misma pantalla y mismo flujo que las demás
 * calculadoras del taller (calcada de la del ropero): lo que se arma y sus medidas a la
 * izquierda con la ficha dibujada arriba, "Referencias y Cálculos" a la derecha con una fila
 * por familia de material, y abajo Compartir, Archivar y Calcular. Las medidas van en metros,
 * como se habla en obra.
 */
class DrywallActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDrywallBinding
    private lateinit var controladorCola: ControladorColaMedidas
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var primerClickArchivarRealizado = false

    private var drywall = Drywall()
    private var materiales: MaterialesDrywall? = null

    /** Las separaciones entre parantes que se usan: 16" y 24". */
    private val separaciones = listOf(40.6f, 61f)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDrywallBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Tocar "Referencias y Cálculos" abre la calculadora flotante.
        crystal.crystal.calculadora.CalculadoraFlotante.instalarEnReferencias(this)

        ProyectoManager.inicializarDesdeStorage(this)
        proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
            context = this,
            textViewProyecto = binding.tvProyectoActivo,
            activity = this
        )
        ProyectoUIHelper.configurarVisorProyectoActivo(this, binding.tvProyectoActivo)
        procesarIntentProyecto(intent)

        configurarOpciones()
        configurarDibujo()
        configurarCalcular()
        configurarArchivar()
        configurarCompartir()
        configurarCliente()

        // Lo último que se armó, para seguir donde se quedó.
        Drywall.desdeJson(getSharedPreferences(PREFS, MODE_PRIVATE).getString(CLAVE_ULTIMO, null))?.let { drywall = it }
        // Abierto desde Productos para editar uno archivado: ese, tal como se guardó.
        crystal.crystal.casilla.EdicionProducto.disenoDe(this)?.let { Drywall.desdeJson(it) }?.let { drywall = it }
        volcarEnPantalla()

        // Pre-carga desde presupuesto (en metros; si llega en cm, se pasa).
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho.setText(df(enMetros(it))) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAlto.setText(df(enMetros(it))) }

        controladorCola = ControladorColaMedidas(
            activity = this,
            claseActual = DrywallActivity::class.java,
            etAncho = binding.etAncho,
            etAlto = binding.etAlto,
            formato = { df(enMetros(it)) }
        )
        controladorCola.inicializar()
    }

    /** Las medidas de la cola y del presupuesto llegan en cm; aquí se trabaja en metros. */
    private fun enMetros(v: Float): Float = if (v > 20f) v / 100f else v

    // ==================== LAS CASILLAS ====================

    private var cargando = false

    private fun volcarEnPantalla() {
        cargando = true
        binding.spTipo.setSelection(TipoDrywall.values().indexOf(drywall.tipo))
        binding.etAncho.setText(df(drywall.largoM))
        binding.etAlto.setText(df(drywall.altoM))
        binding.etVanos.setText(df(drywall.vanosM2))
        binding.etNumVanos.setText(drywall.vanos.toString())
        binding.spSeparacion.setSelection(separaciones.indexOfFirst { kotlin.math.abs(it - drywall.separacionCm) < 0.1f }.coerceAtLeast(0))
        binding.spPlancha.setSelection(PlanchaDrywall.values().indexOf(drywall.plancha))
        binding.spPerfil.setSelection(PerfilDrywall.values().indexOf(drywall.perfil))
        binding.etEsquinas.setText(drywall.esquinas.toString())
        binding.cbLana.isChecked = drywall.conLana
        binding.vistaDrywall.drywall = drywall
        cargando = false
    }

    /** El drywall que dicen las casillas, o null si todavía no dicen medidas válidas. */
    private fun drywallDesdeCasillas(): Drywall? {
        val largo = binding.etAncho.text.toString().toFloatOrNull() ?: return null
        val alto = binding.etAlto.text.toString().toFloatOrNull() ?: return null
        if (largo < 0.3f || alto < 0.3f) return null
        return Drywall(
            tipo = TipoDrywall.values()[binding.spTipo.selectedItemPosition.coerceIn(0, TipoDrywall.values().lastIndex)],
            largoM = largo,
            altoM = alto,
            vanosM2 = binding.etVanos.text.toString().toFloatOrNull() ?: 0f,
            vanos = binding.etNumVanos.text.toString().toIntOrNull() ?: 0,
            separacionCm = separaciones[binding.spSeparacion.selectedItemPosition.coerceIn(0, 1)],
            plancha = PlanchaDrywall.values()[binding.spPlancha.selectedItemPosition.coerceIn(0, PlanchaDrywall.values().lastIndex)],
            perfil = PerfilDrywall.values()[binding.spPerfil.selectedItemPosition.coerceIn(0, PerfilDrywall.values().lastIndex)],
            esquinas = binding.etEsquinas.text.toString().toIntOrNull() ?: 0,
            conLana = binding.cbLana.isChecked
        )
    }

    private fun redibujarDesdeCasillas() {
        val nuevo = drywallDesdeCasillas() ?: return
        drywall = nuevo
        binding.vistaDrywall.drywall = drywall
    }

    private fun refrescarProyectoActivoUI() {
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
    }

    // ==================== OPCIONES Y DIBUJO ====================

    private fun configurarOpciones() {
        fun desplegable(opciones: List<String>) = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, opciones)
        binding.spTipo.adapter = desplegable(TipoDrywall.values().map { it.etiqueta })
        binding.spSeparacion.adapter = desplegable(listOf("40.6 cm (16\")", "61 cm (24\")"))
        binding.spPlancha.adapter = desplegable(PlanchaDrywall.values().map { it.etiqueta })
        binding.spPerfil.adapter = desplegable(PerfilDrywall.values().map { it.etiqueta })
    }

    private fun configurarDibujo() {
        binding.btVerEstructura.setOnClickListener { binding.vistaDrywall.mostrarPlanchas = false }
        binding.btVerPlanchas.setOnClickListener { binding.vistaDrywall.mostrarPlanchas = true }
        // El dibujo sigue a las casillas sin esperar a Calcular (que es el que saca los materiales).
        val alEscribir = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) = Unit
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) = Unit
            override fun afterTextChanged(s: android.text.Editable?) { if (!cargando) redibujarDesdeCasillas() }
        }
        listOf(binding.etAncho, binding.etAlto, binding.etVanos, binding.etNumVanos, binding.etEsquinas).forEach { it.addTextChangedListener(alEscribir) }
        val alElegir = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) { if (!cargando) redibujarDesdeCasillas() }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        listOf(binding.spTipo, binding.spSeparacion, binding.spPlancha, binding.spPerfil).forEach { it.onItemSelectedListener = alElegir }
        binding.cbLana.setOnCheckedChangeListener { _, _ -> if (!cargando) redibujarDesdeCasillas() }
    }

    // ==================== CALCULAR ====================

    private fun configurarCalcular() {
        binding.btCalcular.setOnClickListener {
            try {
                controladorCola.onCalcular()
                if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener
                calcular()
            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun calcular(): Boolean {
        val nuevo = drywallDesdeCasillas()
        if (nuevo == null) {
            Toast.makeText(this, "Ingrese datos válidos (en metros)", Toast.LENGTH_SHORT).show()
            return false
        }
        drywall = nuevo
        binding.vistaDrywall.drywall = drywall
        val m = DrywallCalculo.calcular(drywall)
        materiales = m
        mostrarResultados(m)
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(CLAVE_ULTIMO, drywall.aJson()).apply()
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarResultados(m: MaterialesDrywall) {
        binding.tvPlanchas.text = m.planchas.nombre
        binding.txPlanchas.text = m.planchas.linea
        binding.txPerfiles.text = m.lineas(m.perfiles)
        binding.txFijaciones.text = m.lineas(m.fijaciones)
        binding.txAcabado.text = m.lineas(m.acabado)
        binding.txReferencias.text = m.referencias
        // El detalle: de dónde sale cada cantidad.
        binding.txCorte.text = (listOf(m.planchas) + m.perfiles + m.fijaciones + m.acabado)
            .filter { it.nota.isNotBlank() }
            .joinToString("\n") { "${it.nombre}: ${it.linea} (${it.nota})" }
    }

    // ==================== ARCHIVAR ====================

    private fun callbackSeleccionProyectoParaArchivar(): DialogosProyecto.ProyectoCallback {
        return object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                val mapExistente = MapStorage.cargarProyecto(this@DrywallActivity, nombreProyecto)
                if (mapExistente != null) {
                    mapListas.clear()
                    mapListas.putAll(mapExistente)
                }
                ejecutarArchivado()
            }

            override fun onProyectoCreado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                ejecutarArchivado()
            }

            override fun onProyectoEliminado(nombreProyecto: String) {
                refrescarProyectoActivoUI()
            }
        }
    }

    private fun abrirDialogoSeleccionProyectoParaArchivar() {
        DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, callbackSeleccionProyectoParaArchivar())
    }

    private fun configurarArchivar() {
        binding.btArchivar.setOnClickListener {
            // Candado de suscripción PRIMERO: bloquear antes de avanzar numeración o dar el toast.
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeArchivar(),
                    "Archivar es una función de pago. Renueva para guardar tus proyectos.")) {
                return@setOnClickListener
            }
            if (binding.etAncho.text.toString().isEmpty() || materiales == null) {
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!primerClickArchivarRealizado) {
                abrirDialogoSeleccionProyectoParaArchivar()
            } else {
                if (!ProyectoManager.hayProyectoActivo()) {
                    primerClickArchivarRealizado = false
                    Toast.makeText(this, "No hay proyecto activo. Selecciona uno.", Toast.LENGTH_SHORT).show()
                    abrirDialogoSeleccionProyectoParaArchivar()
                    return@setOnClickListener
                }
                ejecutarArchivado()
            }
        }
    }

    private fun ejecutarArchivado() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            devolverResultadoMasivo()
            return
        }
        archivarMapas()
        refrescarProyectoActivoUI()
        binding.etAncho.setText("")
        controladorCola.ofrecerSiguiente()
    }

    private fun archivarMapas() {
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        val cliente = intent.extras?.getString("rcliente").orEmpty()
        val paquete = drywall.aJson()
        fun fila(ly: View, tv: TextView, tx: TextView) = ListaCasilla.ItemArchivable(tv, tx, ly.visibility != View.GONE)
        val ultimoID = ListaCasilla.archivarEnProyectoActivo(
            context = this,
            mapListas = mapListas,
            prefijo = PREFIJO,
            cantidad = cant,
            referencias = fila(binding.lyReferencias, binding.tvReferencias, binding.txReferencias),
            items = listOf(
                fila(binding.lyPlanchas, binding.tvPlanchas, binding.txPlanchas),
                fila(binding.lyPerfiles, binding.tvPerfiles, binding.txPerfiles),
                fila(binding.lyFijaciones, binding.tvFijaciones, binding.txFijaciones),
                fila(binding.lyAcabado, binding.tvAcabado, binding.txAcabado)
            ),
            cliente = cliente.ifBlank { null },
            paquetesPorNumero = { mapOf(CLAVE_DISENO to paquete) }
        )
        refrescarProyectoActivoUI()
        val msg = if (cant > 1) "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
                  else "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun devolverResultadoMasivo() {
        val perfiles = mapOf(
            ModoMasivoHelper.texto(binding.tvPlanchas) to ModoMasivoHelper.texto(binding.txPlanchas),
            ModoMasivoHelper.texto(binding.tvPerfiles) to ModoMasivoHelper.texto(binding.txPerfiles)
        ).filter { it.value.isNotBlank() }
        val accesorios = mapOf(
            "Fijaciones drywall" to ModoMasivoHelper.texto(binding.txFijaciones),
            "Acabado drywall" to ModoMasivoHelper.texto(binding.txAcabado)
        ).filter { it.value.isNotBlank() }
        ModoMasivoHelper.devolverResultado(
            activity = this,
            calculadora = "Drywall",
            perfiles = perfiles,
            vidrios = "",
            accesorios = accesorios,
            referencias = ModoMasivoHelper.texto(binding.txReferencias),
            disenoPaquete = drywall.aJson()
        )
    }

    // ==================== COMPARTIR ====================

    private fun configurarCompartir() {
        binding.btCompartir.setOnClickListener { compartir() }
    }

    private fun compartir() {
        val m = materiales ?: run { Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show(); return }
        val texto = buildString {
            append(m.referencias).append("\n\n")
            append("PLANCHAS\n").append(m.planchas.nombre).append(": ").append(m.planchas.linea).append("\n\n")
            append("PERFILES\n").append(m.lineas(m.perfiles)).append("\n\n")
            append("FIJACIONES\n").append(m.lineas(m.fijaciones)).append("\n\n")
            append("ACABADO\n").append(m.lineas(m.acabado))
        }
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Drywall: ${drywall.tipo.etiqueta}")
            putExtra(Intent.EXTRA_TEXT, texto)
        }, "Compartir materiales"))
    }

    // ==================== CLIENTE ====================

    private fun callbackProyectoCliente(): DialogosProyecto.ProyectoCallback {
        return object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                refrescarProyectoActivoUI()
            }

            override fun onProyectoCreado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                refrescarProyectoActivoUI()
            }

            override fun onProyectoEliminado(nombreProyecto: String) {
                refrescarProyectoActivoUI()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun configurarCliente() {
        val cliente = intent.extras?.getString("rcliente") ?: ""
        val proyectoActual = ProyectoManager.getProyectoActivo() ?: ""
        if (proyectoActual.isNotEmpty()) primerClickArchivarRealizado = true
        if (cliente.isEmpty()) return
        if (proyectoActual.contains(cliente, ignoreCase = true)) return
        val callbackCliente = callbackProyectoCliente()
        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackCliente, cliente)
        } else {
            AlertDialog.Builder(this)
                .setTitle("Cliente: $cliente")
                .setMessage("Proyecto activo: \"$proyectoActual\".\n¿Qué deseas hacer?")
                .setPositiveButton("Mantener") { d, _ -> primerClickArchivarRealizado = true; d.dismiss() }
                .setNegativeButton("Crear nuevo") { _, _ -> DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackCliente, cliente) }
                .setCancelable(false)
                .show()
        }
    }

    // ==================== PROYECTO ====================

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let { ProyectoUIHelper.agregarOpcionesMenuProyecto(it) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val manejado = ProyectoUIHelper.manejarSeleccionMenu(
            context = this,
            itemId = item.itemId,
            callback = proyectoCallback,
            onProyectoCambiado = { refrescarProyectoActivoUI() }
        )
        return if (manejado) true else super.onOptionsItemSelected(item)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        procesarIntentProyecto(intent)
    }

    override fun onResume() {
        super.onResume()
        refrescarProyectoActivoUI()
    }

    private fun procesarIntentProyecto(intent: Intent) {
        val nombreProyecto = intent.getStringExtra("proyecto_nombre")
        val crearNuevo = intent.getBooleanExtra("crear_proyecto", false)
        val descripcionProyecto = intent.getStringExtra("proyecto_descripcion") ?: ""
        if (crearNuevo && !nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.crearProyecto(this, nombreProyecto, descripcionProyecto)) {
                ProyectoManager.setProyectoActivo(this, nombreProyecto)
                refrescarProyectoActivoUI()
                Toast.makeText(this, "Proyecto '$nombreProyecto' creado y activado", Toast.LENGTH_SHORT).show()
            }
        } else if (!nombreProyecto.isNullOrEmpty()) {
            if (MapStorage.existeProyecto(this, nombreProyecto)) {
                ProyectoManager.setProyectoActivo(this, nombreProyecto)
                refrescarProyectoActivoUI()
                Toast.makeText(this, "Proyecto '$nombreProyecto' activado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun df(v: Float): String = DrywallCalculo.fmt(v)

    // ==================== BACK ====================

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            devolverResultadoMasivo()
            return
        }
        super.onBackPressed()
    }

    companion object {
        private const val PREFS = "drywall"
        private const val CLAVE_ULTIMO = "ultimo"
        /** El prefijo de sus paquetes en el proyecto: Drywall. */
        const val PREFIJO = "Dw"
        /** La lista del proyecto donde viaja el drywall como paquete (JSON). */
        const val CLAVE_DISENO = "DisenoDrywall"
    }
}
