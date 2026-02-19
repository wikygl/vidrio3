package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityMuroBinding

class Muro : AppCompatActivity(), EditGridFragment.OnGridUpdatedListener {

    private lateinit var binding : ActivityMuroBinding

    private var anchoTotal: Float = 100f
    private var altoTotal: Float = 100f
    private var anchosColumnas: MutableList<Float> = mutableListOf()
    private var alturasFilasPorColumna: MutableList<MutableList<Float>> = mutableListOf()
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()

    // ==================== NUEVAS VARIABLES PARA SISTEMA DE PROYECTOS ====================
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback

    // Evitar “spam” de actualizaciones cuando el usuario escribe
    private var runnableActualizacion: Runnable? = null

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMuroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ==================== CONFIGURACIÓN DEL SISTEMA DE PROYECTOS ====================

        ProyectoManager.inicializarDesdeStorage(this)

        proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
            context = this,
            textViewProyecto = binding.tvProyectoActivo,
            activity = this
        )

        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoGestionProyectos(this, proyectoCallback)
        }

        procesarIntentProyecto(intent)

        // ==================== CONFIGURACIÓN ORIGINAL ====================

        binding.med1.requestFocus()
        disenoInicial()
        configurarActualizacionAutomatica()

        // ==================== LISTENERS ====================

        binding.btnCalcular.setOnClickListener {
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener
            vidrios()
            marcos()
            tubos()
            calcularAluminioNaves()
        }

        binding.btDisenar.setOnClickListener {
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener
            actualizarDisenoAutomaticamente()
            binding.etTubo.requestFocus()
        }

        binding.btArchivar.setOnClickListener {
            if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener
            archivarMapas()
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
    }

    private fun actualizarDisenoAutomaticamente() {
        try {
            val ancho = binding.med1.text.toString().toFloatOrNull() ?: anchoTotal
            val alto  = binding.med2.text.toString().toFloatOrNull() ?: altoTotal
            val colum = binding.nCol.text.toString().toIntOrNull()  ?: 3
            val filas = binding.nFilas.text.toString().toIntOrNull() ?: 2

            // Validaciones básicas
            if (ancho <= 0f || alto <= 0f || colum <= 0 || filas <= 0) return

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

        } catch (_: Exception) {
            // evitar crash silenciosamente
        }
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
    }

    private fun actualizarDisenoAutomaticamente() {
        try {
            val ancho = binding.med1.text.toString().toFloatOrNull() ?: anchoTotal
            val alto  = binding.med2.text.toString().toFloatOrNull() ?: altoTotal
            val colum = binding.nCol.text.toString().toIntOrNull()  ?: 3
            val filas = binding.nFilas.text.toString().toIntOrNull() ?: 2

            // Validaciones básicas
            if (ancho <= 0f || alto <= 0f || colum <= 0 || filas <= 0) return

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
    }

    override fun onGridUpdated(
        anchosColumnas: List<Float>,
        alturasFilasPorColumna: List<List<Float>>
    ) {
        this.anchosColumnas = anchosColumnas.toMutableList()
        this.alturasFilasPorColumna = alturasFilasPorColumna.map { it.toMutableList() }.toMutableList()

        val anchoTotal = binding.rectanguloView.getAnchoTotal()
        val altoTotal = binding.rectanguloView.getAltoTotal()

        binding.rectanguloView.configurarParametros(
            anchoTotal,
            altoTotal,
            this.anchosColumnas,
            this.alturasFilasPorColumna
        )
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
        val anchoTotal = gridDrawingView.getAnchoTotal()
        val altoTotal = gridDrawingView.getAltoTotal()
        val anchosColumnas = gridDrawingView.getAnchosColumnas()
        val alturasFilasPorColumna = gridDrawingView.getAlturasFilasPorColumna()

        val fragment = EditGridFragment.newInstance(
            anchoTotal,
            altoTotal,
            anchosColumnas,
            alturasFilasPorColumna
        )
        fragment.show(supportFragmentManager, "EditGridFragment")
    }

    private fun diseno() {
        actualizarDisenoAutomaticamente()
    }

    // ==================== ARCHIVAR MAPAS (PROYECTOS) ====================
    private fun archivarMapas() {
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)

        for (u in 1..cant) {
            ListaCasilla.incrementarContadorVentanas(this)

            if (esValido(binding.lyReferencias)) {
                ListaCasilla.procesarReferencias(this, binding.tvReferencias, binding.txReferencias, mapListas)
            }
            if (esValido(binding.ulayout)) {
                ListaCasilla.procesarArchivar(this, binding.tvMarco, binding.txMarco, mapListas)
            }
            if (esValido(binding.lyAlnMarco)) {
                ListaCasilla.procesarArchivar(this, binding.tvAlnMarco, binding.txAlnMarco, mapListas)
            }
            if (esValido(binding.lyAlnTubo)) {
                ListaCasilla.procesarArchivar(this, binding.tvAlnTubo, binding.txAlnTubo, mapListas)
            }
            if (esValido(binding.tuboLayout)) {
                ListaCasilla.procesarArchivar(this, binding.tvTubo, binding.txTubo, mapListas)
            }
            if (esValido(binding.lyVidrios)) {
                ListaCasilla.procesarArchivar(this, binding.tvVidrios, binding.txVidrios, mapListas)
            }
            if (esValido(binding.lyClient)) {
                ListaCasilla.procesarArchivar(this, binding.tvC, binding.txC, mapListas)
            }
            if (esValido(binding.lyAncho)) {
                ListaCasilla.procesarArchivar(this, binding.tvAncho, binding.txAncho, mapListas)
            }
            if (esValido(binding.lyAlto)) {
                ListaCasilla.procesarArchivar(this, binding.tvAlto, binding.txAlto, mapListas)
            }
            if (esValido(binding.lyPuente)) {
                ListaCasilla.procesarArchivar(this, binding.tvPuente, binding.txPuente, mapListas)
            }
            if (esValido(binding.lyDivisiones)) {
                ListaCasilla.procesarArchivar(this, binding.tvDivisiones, binding.txDivisiones, mapListas)
            }
            if (esValido(binding.lyFijos)) {
                ListaCasilla.procesarArchivar(this, binding.tvFijos, binding.txFijos, mapListas)
            }
            if (esValido(binding.lyCorredizas)) {
                ListaCasilla.procesarArchivar(this, binding.tvCorredizas, binding.txCorredizas, mapListas)
            }
            if (esValido(binding.lyDiseno)) {
                ListaCasilla.procesarArchivar(this, binding.tvDiseno, binding.txDiseno, mapListas)
            }
            if (esValido(binding.lyGrados)) {
                ListaCasilla.procesarArchivar(this, binding.tvGrados, binding.txGrados, mapListas)
            }
            if (esValido(binding.lyTipo)) {
                ListaCasilla.procesarArchivar(this, binding.tvTipo, binding.txTipo, mapListas)
            }
        }

        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)

        binding.tvPuntos.setText(mapListas.toString())
        val msg = if (cant > 1) "Archivadas $cant unidades" else "Datos agregados al proyecto: ${ProyectoManager.getProyectoActivo()}"
        println(msg)
        println(mapListas)
    }

    private fun esValido(ly: LinearLayout): Boolean {
        return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
    }

    // ==================== CÁLCULOS ====================

    @RequiresApi(Build.VERSION_CODES.N)
    private fun vidrios() {
        val gruna = binding.etGruna.text.toString().toFloatOrNull() ?: 0f
        val medidasCantidadMap = mutableMapOf<String, Int>()

        for (colIndex in anchosColumnas.indices) {
            val anchoColumna = anchosColumnas[colIndex]
            val alturasFilas = alturasFilasPorColumna[colIndex]
            for (filaAltura in alturasFilas) {
                val anchoVidrio = (anchoColumna - gruna).coerceAtLeast(0f)
                val altoVidrio  = (filaAltura   - gruna).coerceAtLeast(0f)
                val medida = "${df(anchoVidrio)} x ${df(altoVidrio)}"
                medidasCantidadMap[medida] = medidasCantidadMap.getOrDefault(medida, 0) + 1
            }
        }

        val builder = StringBuilder()
        for ((medida, cantidad) in medidasCantidadMap) {
            builder.append("$medida = $cantidad\n")
        }
        binding.tvVidrios.text = builder.toString()
    }

    private fun marcos() {
        val marco = binding.etMarco.text.toString().toFloatOrNull() ?: 0f
        if (marco <= 0f) {
            Toast.makeText(this, "Por favor, ingrese un valor válido para el marco", Toast.LENGTH_SHORT).show()
            return
        }

        val medidasCantidadMap = mutableMapOf<String, Int>()

        val alturaMarco = altoTotal
        val medidaVertical = df(alturaMarco)
        medidasCantidadMap[medidaVertical] = 2 // izquierda y derecha

        val anchoMarco = anchoTotal - (2 * marco)
        val medidaHorizontal = df(anchoMarco)
        medidasCantidadMap[medidaHorizontal] = 2 // arriba y abajo

        val builder = StringBuilder()
        for ((medida, cantidad) in medidasCantidadMap) {
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

        val medidasCantidadMap = mutableMapOf<String, Int>()

        // Tubos verticales (entre columnas)
        val alturaTuboVertical = altoTotal - (2 * marco)
        val cantidadTubosVerticales = (anchosColumnas.size - 1).coerceAtLeast(0)
        if (cantidadTubosVerticales > 0 && alturaTuboVertical > 0) {
            val medidaVertical = df(alturaTuboVertical)
            medidasCantidadMap[medidaVertical] = cantidadTubosVerticales
        }

        // Tubos horizontales por columna
        for (colIndex in anchosColumnas.indices) {
            val anchuraColumna = anchosColumnas[colIndex]
            val filasEnColumna = alturasFilasPorColumna[colIndex]

            val cantidadTubosHorizontales = (filasEnColumna.size - 1).coerceAtLeast(0)
            if (cantidadTubosHorizontales > 0) {
                val ajusteInicio = if (colIndex == 0) marco else tubo / 2f
                val ajusteFin    = if (colIndex == anchosColumnas.size - 1) marco else tubo / 2f
                val anchoTuboHorizontal = anchuraColumna - ajusteInicio - ajusteFin

                if (anchoTuboHorizontal > 0f) {
                    val medidaHorizontal = df(anchoTuboHorizontal)
                    val cantidadExistente = medidasCantidadMap.getOrDefault(medidaHorizontal, 0)
                    medidasCantidadMap[medidaHorizontal] = cantidadExistente + cantidadTubosHorizontales
                }
            }
        }

        val builder = StringBuilder()
        for ((medida, cantidad) in medidasCantidadMap) {
            builder.append("$medida = $cantidad\n")
        }
        binding.tvTubo.text = builder.toString()
    }

    private fun parseNavesInput(input: String): List<Pair<Int, Int>> {
        val navesList = mutableListOf<Pair<Int, Int>>()
        val entries = input.split(";")
        for (entry in entries) {
            val parts = entry.split(",")
            if (parts.size == 2) {
                val colPart = parts[0].trim()
                val rowPart = parts[1].trim()
                if (colPart.startsWith("c") && rowPart.startsWith("f")) {
                    val colStr = colPart.substring(1)
                    val rowStr = rowPart.substring(1)
                    val colIndex = colStr.toIntOrNull()?.minus(1)
                    val rowIndex = rowStr.toIntOrNull()?.minus(1)
                    if (colIndex != null && rowIndex != null) {
                        navesList.add(Pair(colIndex, rowIndex))
                    }
                }
            }
        }
        return navesList
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun calcularAluminioNaves() {
        val marco = binding.etMarco.text.toString().toFloatOrNull() ?: 0f
        val tubo  = binding.etTubo.text.toString().toFloatOrNull()  ?: 0f

        if (marco <= 0f || tubo <= 0f) {
            Toast.makeText(this, "Por favor, ingrese valores válidos para el marco y el tubo", Toast.LENGTH_SHORT).show()
            return
        }

        val navesInput = binding.etNaves.text.toString()
        val navesList = parseNavesInput(navesInput)

        if (navesList.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese posiciones válidas para las naves", Toast.LENGTH_SHORT).show()
            return
        }

        val alnMarcoMedidasCantidadMap = mutableMapOf<String, Int>()
        val alnTuboMedidasCantidadMap  = mutableMapOf<String, Int>()

        for ((colIndex, rowIndex) in navesList) {
            if (colIndex in anchosColumnas.indices && rowIndex in alturasFilasPorColumna[colIndex].indices) {
                val anchuraColumna = anchosColumnas[colIndex]
                val alturaFila     = alturasFilasPorColumna[colIndex][rowIndex]

                // AlnMarco verticales (2)
                val ajusteInicioV = if (rowIndex == 0) marco else tubo / 2f
                val ajusteFinV    = if (rowIndex == alturasFilasPorColumna[colIndex].size - 1) marco else tubo / 2f
                val altoAlnMarcoVertical = alturaFila - ajusteInicioV - ajusteFinV
                val medidaAltoVertical = df(altoAlnMarcoVertical)
                alnMarcoMedidasCantidadMap[medidaAltoVertical] = alnMarcoMedidasCantidadMap.getOrDefault(medidaAltoVertical, 0) + 2

                // AlnMarco horizontales (2)
                val ajusteInicioH = if (colIndex == 0) marco else tubo / 2f
                val ajusteFinH    = if (colIndex == anchosColumnas.size - 1) marco else tubo / 2f
                val anchoAlnMarcoHorizontal = anchuraColumna - ajusteInicioH - ajusteFinH
                val medidaAnchoHorizontal = df(anchoAlnMarcoHorizontal)
                alnMarcoMedidasCantidadMap[medidaAnchoHorizontal] = alnMarcoMedidasCantidadMap.getOrDefault(medidaAnchoHorizontal, 0) + 2

                // AlnTubo = AlnMarco - 2
                val altoAlnTuboVertical      = (altoAlnMarcoVertical - 2f).coerceAtLeast(0f)
                val anchoAlnTuboHorizontal   = (anchoAlnMarcoHorizontal - 2f).coerceAtLeast(0f)
                val medidaAltoTuboVertical   = df(altoAlnTuboVertical)
                val medidaAnchoTuboHorizontal= df(anchoAlnTuboHorizontal)

                alnTuboMedidasCantidadMap[medidaAltoTuboVertical]    = alnTuboMedidasCantidadMap.getOrDefault(medidaAltoTuboVertical, 0) + 2
                alnTuboMedidasCantidadMap[medidaAnchoTuboHorizontal] = alnTuboMedidasCantidadMap.getOrDefault(medidaAnchoTuboHorizontal, 0) + 2

            } else {
                Toast.makeText(this, "La posición c${colIndex + 1},f${rowIndex + 1} no es válida", Toast.LENGTH_SHORT).show()
            }
        }

        val builderAlnMarco = StringBuilder()
        for ((medida, cantidad) in alnMarcoMedidasCantidadMap) {
            builderAlnMarco.append("$medida = $cantidad\n")
        }
        binding.tvAlnMarco.text = builderAlnMarco.toString()

        val builderAlnTubo = StringBuilder()
        for ((medida, cantidad) in alnTuboMedidasCantidadMap) {
            builderAlnTubo.append("$medida = $cantidad\n")
        }
        binding.tvAlnTubo.text = builderAlnTubo.toString()
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
                calculadora = "Muro Cortina",
                perfiles = perfiles,
                vidrios = ModoMasivoHelper.texto(binding.tvVidrios),
                accesorios = emptyMap(),
                referencias = ""
            )
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}
