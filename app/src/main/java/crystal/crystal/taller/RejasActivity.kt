package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityRejasBinding
import kotlin.math.ceil

/**
 * Rejas de tubo cuadrado: calcada de la calculadora de muro cortina (misma pantalla y flujo),
 * con las reglas de la reja: marco y tubos de 3.8, sin vidrio ni naves, y el reparto solo
 * (columnas y filas en 0) en tramos de 15 cm como mucho, redondeando hacia arriba.
 */
class RejasActivity : AppCompatActivity() {

    private lateinit var binding : ActivityRejasBinding
    private lateinit var controladorCola: ControladorColaMedidas

    private var anchoTotal: Float = 100f
    private var altoTotal: Float = 100f
    private var anchosColumnas: MutableList<Float> = mutableListOf()
    private var alturasFilasPorColumna: MutableList<MutableList<Float>> = mutableListOf()
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()

    // ==================== NUEVAS VARIABLES PARA SISTEMA DE PROYECTOS ====================
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback

    // Evitar “spam” de actualizaciones cuando el usuario escribe
    private var runnableActualizacion: Runnable? = null
    private var formaEditada: String = ""
    /** Tramo máximo al repartir solo: cada 15 cm (redondeando hacia arriba) va otro tubo. */
    private val MEDIDA_AUTOMATICA = 15f
    /** Lo de fábrica de la reja: tubo cuadrado de 3.8 para el marco y los tubos. */
    private val TUBO_REJA = 3.8f

    private var aplicandoResultadoEdicion = false
    private var metaColorAluminio: String = ""
    private var metaTipoVidrio: String = ""
    private var metaAcabadoSuperficial: String = ""
    private var metaObservaciones: String = ""

    private val editarGridLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode != RESULT_OK) return@registerForActivityResult
        val data = result.data ?: return@registerForActivityResult
        runnableActualizacion?.let { binding.rectanguloView.removeCallbacks(it) }
        runnableActualizacion = null
        val anchos = data.getFloatArrayExtra(EditGridActivity.RESULT_ANCHOS_COLUMNAS)
            ?.toMutableList()
            ?: return@registerForActivityResult
        val alturas = data.getStringExtra(EditGridActivity.RESULT_ALTURAS_FILAS)
            ?.split("|")
            ?.map { columna -> columna.split(",").mapNotNull { it.toFloatOrNull() }.toMutableList() }
            ?.filter { it.isNotEmpty() }
            ?.toMutableList()
            ?: return@registerForActivityResult
        data.getFloatExtra(EditGridActivity.RESULT_MARCO, Float.NaN)
            .takeIf { !it.isNaN() }
            ?.let { binding.etMarco.setText(df(it)) }
        data.getFloatExtra(EditGridActivity.RESULT_TUBO, Float.NaN)
            .takeIf { !it.isNaN() }
            ?.let { binding.etTubo.setText(df(it)) }
        data.getFloatExtra(EditGridActivity.RESULT_GRUNA, Float.NaN)
            .takeIf { !it.isNaN() }
            ?.let { binding.etGruna.setText(df(it)) }
        data.getStringExtra(EditGridActivity.RESULT_NAVES)
            ?.let { binding.etNaves.setText(it) }
        formaEditada = data.getStringExtra(EditGridActivity.RESULT_FORMA).orEmpty()
        actualizarGridDesdeDiseno(anchos, alturas)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRejasBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Tocar "Referencias y Cálculos" abre la calculadora flotante.
        crystal.crystal.calculadora.CalculadoraFlotante.instalarEnReferencias(this)

        // ==================== CONFIGURACIÓN DEL SISTEMA DE PROYECTOS ====================

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

        // ==================== CONFIGURACIÓN ORIGINAL ====================

        binding.med1.requestFocus()
        // La reja no lleva vidrio ni naves: esas filas no se enseñan.
        binding.lyVidrios.visibility = View.GONE
        binding.lyAlnMarco.visibility = View.GONE
        binding.lyAlnTubo.visibility = View.GONE
        binding.lyGruna.visibility = View.GONE
        binding.tvExp.visibility = View.GONE
        binding.lyNaves.visibility = View.GONE
        disenoInicial()
        configurarActualizacionAutomatica()

        // ==================== LISTENERS ====================

        binding.btnCalcular.setOnClickListener {
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener
            actualizarReferenciasYDatosEntrada()
            marcos()
            tubos()
        }

        binding.btDisenar.setOnClickListener {
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener
            actualizarDisenoAutomaticamente()
            binding.etTubo.requestFocus()
        }

        binding.btArchivar.setOnClickListener {
            // Candado de suscripción PRIMERO: bloquear antes del diálogo de metadatos, de avanzar la
            // numeración o de dar el toast de "archivado".
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeArchivar(),
                    "Archivar es una función de pago. Renueva para guardar tus proyectos.")) {
                return@setOnClickListener
            }
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener
            mostrarDialogoMetadatosProduccion {
                archivarMapas()
            }
        }

        binding.btArchivar.setOnLongClickListener {
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnLongClickListener true
            MapStorage.guardarMap(this, mapListas)
            Toast.makeText(this, "Map guardado en proyecto: ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            true
        }

        binding.rectanguloView.setOnClickListener { mostrarEditGridFragment() }
        binding.rectanguloView.setOnLongClickListener {
            startActivity(Intent(this, FichaActivity::class.java))
            true
        }

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.med1.setText(it.toString()) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.med2.setText(it.toString()) }

        controladorCola = ControladorColaMedidas(
            activity = this,
            claseActual = RejasActivity::class.java,
            etAncho = binding.med1,
            etAlto = binding.med2
        )
        controladorCola.inicializar()
    }

    // ==================== MENÚ DE OPCIONES ====================

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

    // ==================== RECIBIR PROYECTO DESDE MAINACTIVITY ====================

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

    // ==================== ACTUALIZACIÓN AUTOMÁTICA ====================

    private fun configurarActualizacionAutomatica() {
        val textWatcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                if (aplicandoResultadoEdicion) return
                // Cancelar runnable previo para no encolar muchas actualizaciones
                runnableActualizacion?.let { binding.rectanguloView.removeCallbacks(it) }
                runnableActualizacion = Runnable { actualizarDisenoAutomaticamente() }
                binding.rectanguloView.postDelayed(runnableActualizacion!!, 300)
            }
        }

        binding.med1.addTextChangedListener(textWatcher)   // Ancho total
        binding.med2.addTextChangedListener(textWatcher)   // Alto total
        binding.nCol.addTextChangedListener(textWatcher)   // Número de columnas
        binding.nFilas.addTextChangedListener(textWatcher) // Número de filas
        binding.etMarco.addTextChangedListener(textWatcher) // Grueso del marco (se ve en el dibujo)
        binding.etTubo.addTextChangedListener(textWatcher)  // Grueso del tubo
    }

    private fun actualizarDisenoAutomaticamente() {
        try {
            val ancho = binding.med1.text.toString().toFloatOrNull() ?: anchoTotal
            val alto  = binding.med2.text.toString().toFloatOrNull() ?: altoTotal
            if (ancho <= 0f || alto <= 0f) return

            // Columnas y filas en 0 significan "decídelo tú": se reparte cada medida en tramos de
            // 15 cm como mucho (ceil). Con un número escrito a mano manda ese, que para eso se escribió.
            val colum = binding.nCol.text.toString().toIntOrNull()
                ?.takeIf { it > 0 }
                ?: ceil(ancho / MEDIDA_AUTOMATICA).toInt().coerceAtLeast(1)
            val filas = binding.nFilas.text.toString().toIntOrNull()
                ?.takeIf { it > 0 }
                ?: ceil(alto / MEDIDA_AUTOMATICA).toInt().coerceAtLeast(1)

            // >>> SIN LÍMITE A 10: usamos exactamente lo que ingrese el usuario <<<
            anchoTotal = ancho
            altoTotal  = alto

            anchosColumnas = MutableList(colum) { ancho / colum }
            alturasFilasPorColumna = MutableList(colum) { MutableList(filas) { alto / filas } }

            binding.rectanguloView.configurarParametros(
                anchoTotal,
                altoTotal,
                anchosColumnas,
                alturasFilasPorColumna
            )
            pintarEstructura()
            formaEditada = ""

        } catch (_: Exception) {
            // evitar crash silenciosamente
        }
    }


    private fun disenoInicial(){
        // Consistente: 2 columnas y 2 filas por columna
        anchosColumnas = mutableListOf(anchoTotal / 2f, anchoTotal / 2f)
        alturasFilasPorColumna = mutableListOf(
            mutableListOf(altoTotal / 2f, altoTotal / 2f), // Columna 1
            mutableListOf(altoTotal / 2f, altoTotal / 2f)  // Columna 2
        )

        binding.rectanguloView.configurarParametros(
            anchoTotal,
            altoTotal,
            anchosColumnas,
            alturasFilasPorColumna
        )
        pintarEstructura()
        formaEditada = ""
    }

    /** La reja se ve como es: el marco y los tubos con su grueso (modo estructura), sin vidrio. */
    private fun pintarEstructura() {
        val marco = binding.etMarco.text.toString().toFloatOrNull() ?: TUBO_REJA
        val tubo = binding.etTubo.text.toString().toFloatOrNull() ?: TUBO_REJA
        binding.rectanguloView.setModoVisual(GridDrawingView.ModoVisual.ESTRUCTURA, marco, tubo, 0f)
    }

    private fun actualizarGridDesdeDiseno(
        anchosColumnas: List<Float>,
        alturasFilasPorColumna: List<List<Float>>
    ) {
        this.anchosColumnas = anchosColumnas.toMutableList()
        this.alturasFilasPorColumna = alturasFilasPorColumna.map { it.toMutableList() }.toMutableList()

        val anchoTotal = binding.rectanguloView.getAnchoTotal()
        val altoTotal = binding.rectanguloView.getAltoTotal()

        aplicandoResultadoEdicion = true
        try {
            binding.rectanguloView.configurarParametros(
                anchoTotal,
                altoTotal,
                this.anchosColumnas,
                this.alturasFilasPorColumna
            )
            binding.rectanguloView.importarForma(formaEditada)
            pintarEstructura()
        } finally {
            aplicandoResultadoEdicion = false
        }
    }

    private fun df(defo: Float): String {
        val resultado = if (defo % 1.0 == 0.0) {
            "%.0f".format(defo)
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    private fun mostrarEditGridFragment() {
        val gridDrawingView = binding.rectanguloView
        val intent = Intent(this, EditGridActivity::class.java).apply {
            putExtra(EditGridActivity.EXTRA_ANCHO_TOTAL, gridDrawingView.getAnchoTotal())
            putExtra(EditGridActivity.EXTRA_ALTO_TOTAL, gridDrawingView.getAltoTotal())
            putExtra(EditGridActivity.EXTRA_ANCHOS_COLUMNAS, gridDrawingView.getAnchosColumnas().toFloatArray())
            putExtra(EditGridActivity.EXTRA_MARCO, binding.etMarco.text.toString().toFloatOrNull() ?: TUBO_REJA)
            putExtra(EditGridActivity.EXTRA_TUBO, binding.etTubo.text.toString().toFloatOrNull() ?: TUBO_REJA)
            putExtra(EditGridActivity.EXTRA_GRUNA, binding.etGruna.text.toString().toFloatOrNull() ?: 0f)
            putExtra(EditGridActivity.EXTRA_NAVES, binding.etNaves.text.toString())
            putExtra(EditGridActivity.EXTRA_FORMA, formaEditada)
            putExtra(
                EditGridActivity.EXTRA_ALTURAS_FILAS,
                gridDrawingView.getAlturasFilasPorColumna().joinToString("|") { columna ->
                    columna.joinToString(",")
                }
            )
        }
        editarGridLauncher.launch(intent)
    }

    private fun diseno() {
        actualizarDisenoAutomaticamente()
    }

    private fun obtenerPrefijo(): String = "Rj"

    private fun etiquetaPaquete(prefijo: String, numero: Int): String {
        val cliente = binding.txC.text.toString().trim()
        return if (cliente.isNotBlank()) "$prefijo$numero, $cliente" else "$prefijo$numero"
    }

    private fun mostrarDialogoMetadatosProduccion(onContinuar: () -> Unit) {
        val pad = (16 * resources.displayMetrics.density).toInt()
        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, 0)
        }
        val etColor = EditText(this).apply {
            hint = "Pintura / acabado (ej: negro mate)"
            setText(metaColorAluminio)
        }
        val etVidrio = EditText(this).apply {
            hint = "Tubo (ej: cuadrado 1 1/2 x 1.5 mm)"
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
            .setTitle("Metadatos de produccion")
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

    private fun sufijoMetadatosProduccion(): String {
        return "-MAT<pintura:${metaColorAluminio.ifBlank { "null" }};" +
            "tubo:${metaTipoVidrio.ifBlank { "null" }};" +
            "acabado_sup:${metaAcabadoSuperficial.ifBlank { "null" }};" +
            "obs:${metaObservaciones.ifBlank { "null" }}>"
    }

    // ==================== ARCHIVAR MAPAS (PROYECTOS) ====================
    @RequiresApi(Build.VERSION_CODES.N)
    private fun archivarMapas() {
        actualizarReferenciasYDatosEntrada()
        marcos()
        tubos()

        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
            mapListas.clear()
            if (mapExistente != null) mapListas.putAll(mapExistente)
        }

        val prefijo = obtenerPrefijo()
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        var ultimoID = ""

        // Los números se reservan ANTES del bucle: dentro, nada se ha guardado todavía y
        // obtenerSiguienteContadorPorPrefijo devolvería el mismo para todas las copias.
        val numerosPaquete = ProyectoManager.reservarNumerosPorPrefijo(this, prefijo, cant)
        for (u in 1..cant) {
            val siguienteNumero = numerosPaquete[u - 1]
            val identificadorPaquete = etiquetaPaquete(prefijo, siguienteNumero)
            ultimoID = identificadorPaquete

            if (esValido(binding.lyReferencias)) {
                ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvReferencias, binding.txReferencias, mapListas, identificadorPaquete)
            }
            if (esValido(binding.ulayout)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txMarco, binding.tvMarco, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyAlnMarco)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txAlnMarco, binding.tvAlnMarco, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyAlnTubo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txAlnTubo, binding.tvAlnTubo, mapListas, identificadorPaquete)
            }
            if (esValido(binding.tuboLayout)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txTubo, binding.tvTubo, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyVidrios)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txVidrios, binding.tvVidrios, mapListas, identificadorPaquete)
            }
            val clienteTexto = binding.txC.text.toString().trim()
            if (clienteTexto.isNotBlank()) {
                mapListas.getOrPut("Cliente") { mutableListOf() }
                    .add(mutableListOf(clienteTexto, "", identificadorPaquete))
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
            if (esValido(binding.lyGrados)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvGrados, binding.txGrados, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTipo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.tvTipo, binding.txTipo, mapListas, identificadorPaquete)
            }
            mapListas.getOrPut("MetadatosProduccion") { mutableListOf() }
                .add(mutableListOf(sufijoMetadatosProduccion(), "", identificadorPaquete))

            ProyectoManager.actualizarContadorPorPrefijo(this, prefijo, siguienteNumero)
        }

        MapStorage.guardarMap(this, mapListas)
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)

        binding.tvPuntos.setText(mapListas.toString())
        val msg = if (cant > 1) {
            "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
        } else {
            "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        controladorCola.ofrecerSiguiente()
    }

    private fun esValido(ly: LinearLayout): Boolean {
        return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
    }

    // ==================== CÁLCULOS ====================

    @RequiresApi(Build.VERSION_CODES.N)
    private fun actualizarReferenciasYDatosEntrada() {
        val ancho = binding.med1.text.toString().toFloatOrNull() ?: anchoTotal
        val alto = binding.med2.text.toString().toFloatOrNull() ?: altoTotal
        val columnas = anchosColumnas.size.takeIf { it > 0 } ?: (binding.nCol.text.toString().toIntOrNull() ?: 0)
        val filas = alturasFilasPorColumna.maxOfOrNull { it.size } ?: (binding.nFilas.text.toString().toIntOrNull() ?: 0)
        binding.txReferencias.text = "Reja anch ${df(ancho)} x alt ${df(alto)}\nColumnas: $columnas\nFilas: $filas\nMarco y tubo ${binding.etMarco.text} / ${binding.etTubo.text}"
        binding.txAncho.text = "${df(ancho)} = 1"
        binding.txAlto.text = "${df(alto)} = 1"
        binding.txDivisiones.text = "$columnas = 1"
    }


    private fun marcos() {
        val marco = binding.etMarco.text.toString().toFloatOrNull() ?: 0f
        if (marco <= 0f) {
            Toast.makeText(this, "Por favor, ingrese un valor válido para el marco", Toast.LENGTH_SHORT).show()
            return
        }

        val medidasMap = linkedMapOf<String, Int>()
        fun agregar(largo: Float) {
            if (largo > 0f) {
                val k = df(largo)
                medidasMap[k] = medidasMap.getOrDefault(k, 0) + 1
            }
        }

        val cortes = binding.rectanguloView.obtenerCortesRectangulares()

        if (cortes.isEmpty()) {
            // Sin recortes: 4 marcos perimetrales (regla 2: descuento horizontal).
            agregar(altoTotal)
            agregar(altoTotal)
            agregar(anchoTotal - 2f * marco)
            agregar(anchoTotal - 2f * marco)
        } else {
            // Caso con un recorte rectangular en esquina (CASO 1 y CASO 2).
            // Reglas 1, 2 y 3:
            //  - Vertical opuesto al corte: extremo a extremo.
            //  - Vertical del lado del corte: truncado por el alto del corte.
            //  - Horizontal interior del corte: largo = ancho del corte.
            //  - Vertical interior del corte: largo = alto del corte + 1 marco.
            //  - Horizontal del lado del corte: ancho ventana − ancho corte − 2·marco.
            // El horizontal opuesto al corte se contabiliza como tubo (ver tubos()).
            val corte = cortes.first()
            val tocaIzq = corte.left <= 0.1f
            val tocaDer = corte.right >= anchoTotal - 0.1f
            val tocaArr = corte.top <= 0.1f
            val tocaAbj = corte.bottom >= altoTotal - 0.1f
            val anchoCorte = corte.width()
            val altoCorte = corte.height()

            val esEsquina = (tocaIzq || tocaDer) && (tocaArr || tocaAbj)
            if (esEsquina) {
                agregar(altoTotal)                              // vertical opuesto al corte
                agregar(altoTotal - altoCorte)                  // vertical del lado del corte
                agregar(anchoCorte)                             // horizontal interior del corte
                agregar(altoCorte + marco)                      // vertical interior del corte (regla 3)
                agregar(anchoTotal - anchoCorte - 2f * marco)   // horizontal del lado del corte
            } else {
                // Recorte no esquina: caemos al cálculo perimetral clásico.
                agregar(altoTotal); agregar(altoTotal)
                agregar(anchoTotal - 2f * marco); agregar(anchoTotal - 2f * marco)
            }
        }

        val builder = StringBuilder()
        for ((medida, cantidad) in medidasMap) {
            builder.append("$medida = $cantidad\n")
        }
        binding.tvMarco.text = builder.toString()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun tubos() {
        val marco = binding.etMarco.text.toString().toFloatOrNull() ?: 0f
        val tubo  = binding.etTubo.text.toString().toFloatOrNull()  ?: 0f

        if (marco <= 0f || tubo <= 0f) {
            Toast.makeText(this, "Por favor, ingrese valores válidos para el marco y el tubo", Toast.LENGTH_SHORT).show()
            return
        }

        val medidasCantidadMap = linkedMapOf<String, Int>()

        // Tubos verticales (parantes entre columnas): cada uno mide el largo
        // visible de la línea x = límite-de-columna entre marcos. Cuando un
        // recorte cruza la línea descontamos 1 marco por cada borde del
        // recorte, ya que ese borde lleva su propio marco.
        var xAcum = 0f
        for (colIndex in 0 until anchosColumnas.size - 1) {
            xAcum += anchosColumnas[colIndex]
            val largo = binding.rectanguloView.longitudVerticalVisibleCm(
                xAcum, marco, altoTotal - marco, marcoCorte = marco
            )
            if (largo > 0f) {
                val medida = df(largo)
                medidasCantidadMap[medida] = medidasCantidadMap.getOrDefault(medida, 0) + 1
            }
        }

        // Tubos horizontales por columna: cada uno mide el largo visible de
        // la línea y = límite-de-fila dentro del ancho útil de la columna.
        var xLeft = 0f
        for (colIndex in anchosColumnas.indices) {
            val anchuraColumna = anchosColumnas[colIndex]
            val xRight = xLeft + anchuraColumna
            val ajusteInicio = if (colIndex == 0) marco else tubo / 2f
            val ajusteFin    = if (colIndex == anchosColumnas.size - 1) marco else tubo / 2f
            val xMin = xLeft + ajusteInicio
            val xMax = xRight - ajusteFin
            val filas = alturasFilasPorColumna[colIndex]
            var yAcum = 0f
            for (filaIndex in 0 until filas.size - 1) {
                yAcum += filas[filaIndex]
                val largo = binding.rectanguloView.longitudHorizontalVisibleCm(
                    yAcum, xMin, xMax, marcoCorte = marco
                )
                if (largo > 0f) {
                    val medida = df(largo)
                    medidasCantidadMap[medida] = medidasCantidadMap.getOrDefault(medida, 0) + 1
                }
            }
            xLeft = xRight
        }

        // Si hay un recorte en esquina, el horizontal continuo del lado opuesto
        // al corte se contabiliza como tubo (longitud ancho ventana − 2·marco).
        val cortes = binding.rectanguloView.obtenerCortesRectangulares()
        if (cortes.isNotEmpty()) {
            val corte = cortes.first()
            val tocaIzq = corte.left <= 0.1f
            val tocaDer = corte.right >= anchoTotal - 0.1f
            val tocaArr = corte.top <= 0.1f
            val tocaAbj = corte.bottom >= altoTotal - 0.1f
            val esEsquina = (tocaIzq || tocaDer) && (tocaArr || tocaAbj)
            if (esEsquina) {
                val largoTuboOpuesto = anchoTotal - 2f * marco
                if (largoTuboOpuesto > 0f) {
                    val medida = df(largoTuboOpuesto)
                    medidasCantidadMap[medida] = medidasCantidadMap.getOrDefault(medida, 0) + 1
                }
            }
        }

        val builder = StringBuilder()
        for ((medida, cantidad) in medidasCantidadMap) {
            builder.append("$medida = $cantidad\n")
        }
        binding.tvTubo.text = builder.toString()
    }



    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            val perfiles = mapOf(
                "Marco" to ModoMasivoHelper.texto(binding.tvMarco),
                "Tubo" to ModoMasivoHelper.texto(binding.tvTubo),
                "Aln Marco" to ModoMasivoHelper.texto(binding.tvAlnMarco),
                "Aln Tubo" to ModoMasivoHelper.texto(binding.tvAlnTubo)
            ).filter { it.value.isNotBlank() }

            ModoMasivoHelper.devolverResultado(
                activity = this,
                calculadora = "Rejas",
                perfiles = perfiles,
                vidrios = "",
                accesorios = emptyMap(),
                referencias = ""
            )
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}
