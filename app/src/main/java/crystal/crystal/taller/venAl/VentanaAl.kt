package crystal.crystal.taller.venAl

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.taller.ControladorColaMedidas
import crystal.crystal.Diseno.estructurada.ParametrosEstructurada
import crystal.crystal.Diseno.estructurada.VistaEstructurada
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityVentanaAlBinding
import crystal.crystal.taller.ModoMasivoHelper
import crystal.crystal.taller.nova.NovaCalculos


class VentanaAl : AppCompatActivity() {

    private var serieActual: Serie? = null
    private var indices = 0

    // Opción del diálogo de diseño (v1): para series con marco opcional (3825), si va con marco
    // externo (2.2) + puente o sin él. En serie 84 se ignora (su marco es parte de la ventana).
    private var conMarco = true

    // Opción elegida en el diálogo: "tramos" (reparto por divisiones) o un patrón fijo (ventana
    // simple: fc, cc, cfc/fcc, fccf, cccc). Predeterminado "tramos". (Serie 20/25/62.)
    private var patron20 = "tramos"

    /**
     * Cuántas ventanas iguales lleva este producto. Llega de MedidaActivity y se puede cambiar en
     * el diálogo de opciones. Al archivar se guarda una copia por unidad, numeradas seguidas.
     */
    private var cantidadProducto: Int = 1

    // Serie 3825: siempre por tramos. Patrón elegido por tramo (índice = nº de tramo). Cada tramo
    // puede cambiar entre los patrones de su tamaño (2 módulos: fc/cc; 3: cfc; 4: fccf/cccc). Vacío
    // = automático. Al recalcular se conserva la elección solo en los tramos cuyo tamaño coincide.
    private val patronesTramo3825 = mutableListOf<String>()

    // Metadatos de producción (se piden al archivar, como en Nova).
    private var metaColorAluminio = ""
    private var metaTipoVidrio = ""

    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()

    private var primerClickArchivarRealizado = false
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback
    private var cliente: String = ""

    // Datos estructurados de Serie 3825 (para uso en otras vistas/exportaciones).
    // El campo `corte` no se renderiza en esta actividad.
    private var rielSup3825: MedidaPerfil? = null
    private var rielInf3825: MedidaPerfil? = null
    private var marcoFijo3825: MedidaPerfil? = null
    private var marcoMovil3825: MedidaPerfil? = null
    private var paranteFijo3825: MedidaPerfil? = null
    private var enganche3825: MedidaPerfil? = null
    private var zocalo3825: MedidaPerfil? = null

    private lateinit var binding : ActivityVentanaAlBinding
    private lateinit var controladorCola: ControladorColaMedidas
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityVentanaAlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        cantidadProducto = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
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
        configurarCliente()

        binding.btnCalcularE.setOnClickListener {
            controladorCola.onCalcular()
            val nombreSerie = serieActual?.nombre
            if (nombreSerie == "Serie 25" || nombreSerie == "Serie 20") {
                calcularSerieTramos(nombreSerie == "Serie 25")
                binding.tvReferencias.text = "Ancho = ${binding.etAncho.text}, Alto = ${binding.etAlto.text}\n" +
                        "$nombreSerie -> ${refOpcion()}" + modulacionTramosTexto()
                renderizarPreviewEstructurada()
                return@setOnClickListener
            }
            if (nombreSerie == "Serie 62 europea") {
                calcularSerie62()
                binding.tvReferencias.text = "Ancho = ${binding.etAncho.text}, Alto = ${binding.etAlto.text}\n" +
                        "$nombreSerie -> ${refOpcion()}" + modulacionTramosTexto()
                renderizarPreviewEstructurada()
                return@setOnClickListener
            }
            calcularSerieClasica()
        }
        // La navegación por la lista de ventanas ahora es con clic en el título (txVentana).
        binding.txVentana.setOnClickListener {
            actualizarVentana()
            renderizarPreviewEstructurada()
        }

        // Clic en el diseño: abre el diálogo de opciones (estilo Nova).
        binding.ivModelo.setOnClickListener {
            mostrarDialogoOpciones()
        }

        // Igual que en puerta y mampara paflón: con click largo sobre el diseño se abre
        // DisenoActivity para verlo en grande (con zoom y paneo), reusando el plano cacheado.
        binding.ivModelo.setOnLongClickListener {
            val bmp = (binding.ivModelo.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
            if (bmp == null) {
                Toast.makeText(this, "Calcule primero para ver el diseño", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            crystal.crystal.taller.puerta.dibujo.DibujoPuerta.guardarPlanoEnCache(this, bmp)
            val intent = Intent(this, crystal.crystal.Diseno.DisenoActivity::class.java).apply {
                putExtra(crystal.crystal.Diseno.DisenoActivity.EXTRA_PLANO, true)
                putExtra(
                    crystal.crystal.Diseno.DisenoActivity.EXTRA_PLANO_TITULO,
                    serieActual?.nombre ?: "Ventana de aluminio"
                )
            }
            startActivity(intent)
            true
        }

        // Al iniciar se muestra el primer elemento y se fija como serie activa; así el primer clic
        // en txVentana salta directamente al segundo elemento (el primero ya está a la vista).
        if (savedInstanceState == null) {
            actualizarVentana()
        } else if (listaSeries.isNotEmpty()) {
            // Tras muerte de proceso, el texto de txVentana se restaura solo pero serieActual
            // (campo transitorio) se pierde; sin reconstruirla, el diseño cae al preview genérico
            // de "no serie 84". indices guardado apunta al SIGUIENTE elemento, así que la serie
            // mostrada es la anterior.
            val size = listaSeries.size
            val savedIndex = savedInstanceState.getInt("currentIndex", 0)
            indices = savedIndex
            serieActual = listaSeries[((savedIndex - 1) % size + size) % size]
            binding.txVentana.text = serieActual?.nombre
        }

        binding.btArchivar.setOnClickListener {
            // Candado de suscripción: archivar es función de pago (Fase 3). Con el cobro apagado no bloquea.
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeArchivar(),
                    "Archivar es una función de pago. Renueva para guardar tus proyectos.")) {
                return@setOnClickListener
            }
            if (binding.etAncho.text.toString().isEmpty()) {
                Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validación: las medidas deben coincidir con las referencias
            val anchoET = binding.etAncho.text.toString()
            val altoET = binding.etAlto.text.toString()
            val referencias = binding.tvReferencias.text.toString()
            if (!referencias.contains(anchoET) || !referencias.contains(altoET)) {
                Toast.makeText(this, "Las medidas no coinciden con las referencias. Recalcula.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!primerClickArchivarRealizado) {
                DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, object : DialogosProyecto.ProyectoCallback {
                    override fun onProyectoSeleccionado(nombreProyecto: String) {
                        primerClickArchivarRealizado = true
                        binding.txC.text = nombreProyecto
                        val mapExistente = MapStorage.cargarProyecto(this@VentanaAl, nombreProyecto)
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
                        ProyectoUIHelper.actualizarVisorProyectoActivo(this@VentanaAl, binding.tvProyectoActivo)
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
                            val mapExistente = MapStorage.cargarProyecto(this@VentanaAl, nombreProyecto)
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
                            ProyectoUIHelper.actualizarVisorProyectoActivo(this@VentanaAl, binding.tvProyectoActivo)
                        }
                    })
                    return@setOnClickListener
                }
                binding.txC.text = ProyectoManager.getProyectoActivo() ?: ""
                ejecutarArchivado()
            }
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

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAlto.setText(df1(it)) }

        controladorCola = ControladorColaMedidas(
            activity = this,
            claseActual = VentanaAl::class.java,
            etAncho = binding.etAncho,
            etAlto = binding.etAlto,
            ivDiseno = binding.ivModelo,
            onToqueSimple = { binding.ivModelo.performClick() },
            onToqueLargo = { binding.ivModelo.performLongClick() },
            formato = ::df1
        )
        controladorCola.inicializar()
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

        val proyectoActual = ProyectoManager.getProyectoActivo() ?: ""
        if (proyectoActual.isNotEmpty()) {
            binding.txC.text = proyectoActual
            primerClickArchivarRealizado = true
        }

        if (cliente.isEmpty()) return
        if (proyectoActual.contains(cliente, ignoreCase = true)) return

        val callbackCliente = object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                binding.txC.text = nombreProyecto
                ProyectoUIHelper.actualizarVisorProyectoActivo(this@VentanaAl, binding.tvProyectoActivo)
            }
            override fun onProyectoCreado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                binding.txC.text = nombreProyecto
                ProyectoUIHelper.actualizarVisorProyectoActivo(this@VentanaAl, binding.tvProyectoActivo)
            }
            override fun onProyectoEliminado(nombreProyecto: String) {
                ProyectoUIHelper.actualizarVisorProyectoActivo(this@VentanaAl, binding.tvProyectoActivo)
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

    // ==================== ARCHIVAR ====================
    private fun obtenerPrefijo(): String = "Va"

    private fun ejecutarArchivado() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            // Modo masivo: la respuesta se devuelve en onBackPressed; aquí basta con archivar.
            archivarMapas()
            return
        }
        mostrarDialogoMetadatos {
            archivarMapas()
            ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
            Toast.makeText(this, "Archivado", Toast.LENGTH_SHORT).show()
            binding.etAncho.setText("")
            binding.etAlto.setText("")
            controladorCola.ofrecerSiguiente()
        }
    }

    /** Diálogo de metadatos antes de archivar: color de aluminio y tipo de vidrio (como en Nova). */
    private fun mostrarDialogoMetadatos(onContinuar: () -> Unit) {
        val pad = (16 * resources.displayMetrics.density).toInt()
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, 0)
        }
        val etColor = EditText(this).apply { hint = "Color aluminio (ej: negro)"; setText(metaColorAluminio) }
        val etVidrio = EditText(this).apply { hint = "Tipo vidrio (ej: incoloro 6mm)"; setText(metaTipoVidrio) }
        cont.addView(etColor)
        cont.addView(etVidrio)
        AlertDialog.Builder(this)
            .setTitle("Datos de la ventana")
            .setView(cont)
            .setPositiveButton("Guardar y archivar") { _, _ ->
                metaColorAluminio = etColor.text?.toString()?.trim().orEmpty()
                metaTipoVidrio = etVidrio.text?.toString()?.trim().orEmpty()
                onContinuar()
            }
            .setNeutralButton("Omitir") { _, _ -> onContinuar() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Descriptor del diseño actual (para archivarlo y regenerarlo en la ficha). Null si la serie
     *  no tiene diseño propio. */
    private fun construirDescriptorVentanaAl(): VentanaAlDescriptor? {
        val serieNombre = serieActual?.nombre ?: return null
        if (!VentanaAlRender.soportaSerie(serieNombre)) return null
        val ancho = binding.etAncho.text.toString().replace(",", ".").toFloatOrNull() ?: return null
        val alto = binding.etAlto.text.toString().replace(",", ".").toFloatOrNull() ?: return null
        val marcoVal = binding.etMarco.text.toString().toFloatOrNull() ?: 2.2f
        val hoja = altoHojaParaDescriptor(alto)
        val nMo = binding.etMocheta.text.toString().toIntOrNull() ?: 0
        return VentanaAlDescriptor(
            ancho = ancho, alto = alto, altoHoja = hoja,
            divisiones = divisiones().coerceAtLeast(1), serie = serieNombre,
            marco = marcoVal, nMochetas = nMo, conMarco = conMarco, patron = patronParaDescriptor()
        )
    }

    // FUNCIONES PARA RECUPERAR ESTADO DE MODELO
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIndex",indices)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        indices = savedInstanceState.getInt("currentIndex", 0)
    }

    // FUNCIONES REDONDEOS
    private fun df1(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    // FUNCIONES ALUMINIOS

    // Cálculo de materiales de las series clásicas (84 / 3825 / etc.). Se reutiliza desde el botón
    // Calcular y desde el diálogo Con/Sin marco, para que el toggle recalcule los materiales y no
    // solo la vista.
    @SuppressLint("SetTextI18n")
    private fun calcularSerieClasica() {
        if (serieActual?.nombre == "Serie 3825") { calcularSerie3825(); return }
        restaurarLayoutClasicas()
        marco()
        parante()
        zocalo()
        vidrios()
        riel()
        tope()
        junkillo()
        puente()
        divMocheta(mPuentes1())
        nMocheta()
        binding.tvReferencias.text = "Ancho = ${binding.etAncho.text}, Alto = ${binding.etAlto.text}\n" +
                "Div=${divisiones()} -> Fjs=${nFijos()} -> Crzas=${nCorredizas()}\n" +
                "hHoja = ${df1(altoHoja())}"
        binding.tvPruebas.text = divisiones().toString()

        // Ocultar layouts si sus TextViews están vacíos
        actualizarVisibilidadLayouts()
        renderizarPreviewEstructurada()
    }

    /**
     * Serie 3825: usa el conjunto de patrones y el mecanismo de reparto en tramos de la serie 20,
     * pero con las fórmulas propias de la 3825. Siempre por tramos: reparto automático (ceil(ancho/60))
     * en tramos con paflón a todo el alto entre ellos; cada tramo puede cambiar entre los patrones de
     * su tamaño (ver `patronesTramo3825`). Puede tener puente cuando altoHoja < altoUtil (lógica de
     * mocheta). Regla fccf/cccc (por tramo): una pierna de corrediza se vuelve "Pierna c/aleta".
     */
    // Nº de módulos de un patrón (para saber a qué tamaño de tramo pertenece).
    private fun hojasPatron(p: String) = when (p) { "cfc" -> 3; "fccf", "cccc" -> 4; else -> 2 }

    // Patrones disponibles para un tramo según su nº de módulos (2: fc/cc; 3: cfc; 4: fccf/cccc).
    private fun patronesDeTamano(hojas: Int) = when (hojas) {
        3 -> listOf("cfc")
        4 -> listOf("fccf", "cccc")
        else -> listOf("fc", "cc")
    }

    // Patrones efectivos por tramo: el automático por tamaño, salvo que el usuario haya elegido otro
    // del mismo tamaño para ese tramo (se conserva solo donde el tamaño coincide).
    private fun tramosEfectivos3825(): List<String> {
        val div = divisiones().coerceAtLeast(2)
        val base = VentanaAlRender.tramosSerie25(div)
        return base.mapIndexed { i, def ->
            val sel = patronesTramo3825.getOrNull(i)
            if (sel != null && hojasPatron(sel) == hojasPatron(def)) sel else def
        }
    }

    // Patrón que se pasa al dibujo. En 3825 modo tramos va la lista de patrones por tramo (unida por
    // comas); en modo simple, el patrón único; en las demás series, patron20.
    private fun patronParaDescriptor(): String =
        if (serieActual?.nombre == "Serie 3825" && patron20.equals("tramos", ignoreCase = true))
            tramosEfectivos3825().joinToString(",")
        else patron20

    // Alto de hoja para el descriptor. En 3825 modo simple (sin puente) la hoja ocupa todo el alto útil.
    private fun altoHojaParaDescriptor(alto: Float): Float =
        if (serieActual?.nombre == "Serie 3825" && !patron20.equals("tramos", ignoreCase = true))
            runCatching { altoUtil() }.getOrDefault(alto)
        else runCatching { altoHoja() }.getOrDefault(alto)

    @SuppressLint("SetTextI18n")
    private fun calcularSerie3825() {
        if (binding.etAncho.text.toString().replace(",", ".").toFloatOrNull() == null) return
        val hRaw = binding.etAlto.text.toString().replace(",", ".").toFloatOrNull() ?: return
        val sinMarco = !conMarco
        val anchU = anchUtil()
        val altoU = altoUtil()

        // Modulación general (patron20): "tramos" = reparto por divisiones (con posible override por
        // tramo y puente); un patrón simple = un solo cuerpo, sin puente y a todo el alto.
        val esTramos = patron20.equals("tramos", ignoreCase = true)
        val hHoja = if (esTramos) altoHoja() else altoU
        val mochetaActiva = esTramos && altoHoja() < altoU

        val div = if (esTramos) divisiones().coerceAtLeast(2) else hojasPatron(patron20)
        val tramos = if (esTramos) tramosEfectivos3825() else listOf(patron20)
        if (esTramos) { patronesTramo3825.clear(); patronesTramo3825.addAll(tramos) }  // estado efectivo
        val nPaflon = tramos.size - 1
        val moduleW = (anchU - nPaflon * 2.5f) / div

        val marcoSerieLat = if (sinMarco) altoU - 1.0f else hHoja - 1.0f
        val jun = binding.etJunki.text.toString().toFloatOrNull() ?: 0f
        val tuboMocheta = altoU - (hHoja + 2.5f)

        val rieS = LinkedHashMap<String, Int>(); val rieI = LinkedHashMap<String, Int>()
        val jam = LinkedHashMap<String, Int>();  val pie = LinkedHashMap<String, Int>()
        val tras = LinkedHashMap<String, Int>(); val ale = LinkedHashMap<String, Int>()
        val zoc = LinkedHashMap<String, Int>();  val marH = LinkedHashMap<String, Int>()
        val tubo = LinkedHashMap<String, Int>(); val junk = LinkedHashMap<String, Int>()
        val vid = LinkedHashMap<String, Int>()
        fun add(m: LinkedHashMap<String, Int>, k: String, q: Int) { if (q > 0) m[k] = (m[k] ?: 0) + q }

        for (pat in tramos) {
            val hojas = when (pat) { "cfc" -> 3; "fccf", "cccc" -> 4; else -> 2 }
            val nFijoT = pat.count { it == 'f' }
            val nCorrT = pat.count { it == 'c' }
            val tw = hojas * moduleW

            // Rieles: uno superior y uno inferior por tramo.
            add(rieS, df1(tw), 1)
            add(rieI, df1(tw), 1)
            // Marco externo horizontal (sup + inf) dividido por tramo, igual que el puente. Las jambas
            // (verticales) van a todo el alto y se agregan aparte.
            if (!sinMarco) add(marH, df1(tw), 2)
            // Marco serie: 2 laterales por tramo.
            add(jam, df1(marcoSerieLat), 2)
            // Piernas y traslapes con los mismos conteos que la serie 20, pero con el largo propio de
            // la 3825 (fijo = altoHoja−1.2, corrediza = altoHoja−2):
            //  · corrediza                         → 1 pierna + 1 traslape
            //  · fijo extremo (toca el marco)      → 1 pierna + 1 traslape
            //  · fijo interno (cruce a ambos lados) → 2 traslapes (0 piernas)
            // Resultado: fc 2/2, cc 2/2, cfc 2/4, fccf 4/4, cccc 4/4 (= serie 20).
            for (i in pat.indices) {
                val esFijo = pat[i] == 'f'
                val len = df1(if (esFijo) hHoja - 1.2f else hHoja - 2f)
                if (esFijo && i != 0 && i != pat.length - 1) {
                    add(tras, len, 2)                 // fijo interno: 2 traslapes
                } else {
                    add(pie, len, 1)                  // pierna + traslape
                    add(tras, len, 1)
                }
            }
            // En fccf/cccc, una pierna de corrediza pasa a pierna c/aleta.
            if (pat == "fccf" || pat == "cccc") {
                val largoCorr = df1(hHoja - 2f)
                val q = pie[largoCorr] ?: 0
                if (q > 1) pie[largoCorr] = q - 1 else pie.remove(largoCorr)
                add(ale, largoCorr, 1)
            }
            // Zócalo del tramo. fccf lleva un miembro central doble (pierna+pierna), por eso suma un
            // separador de 2.4 extra: ((anchoÚtil − 1.4) − 2.4·6)/4 en vez de ·5.
            val nSepZoc = if (pat == "fccf" || pat == "cccc") hojas + 2 else hojas + 1
            val zT = ((tw - 1.4f) - nSepZoc * 2.4f) / hojas
            add(zoc, df1(zT), hojas * 2)
            // Vidrios de hoja.
            val anchoVidrio = zT + 1.5f
            add(vid, "${df1(anchoVidrio)} x ${df1((hHoja - 1.2f) - 4.5f)}", nFijoT)
            add(vid, "${df1(anchoVidrio)} x ${df1((hHoja - 2f) - 4.5f)}", nCorrT)
            // Mocheta (solo en tramos con puente), lógica de regla 180 por tramo.
            if (mochetaActiva) {
                val nSub = NovaCalculos.anchMota(tw)           // submódulos = ceil(tw/180), mín 1
                val anchoSub = divMocheta(tw)                  // ancho del submódulo, ya descontado el tubo de 2.5
                val nTubos = (nSub - 1).coerceAtLeast(0)
                add(tubo, df1(tw), 1)                          // puente horizontal
                if (nTubos > 0) add(tubo, df1(tuboMocheta), nTubos)
                add(junk, df1(anchoSub), nSub * 2)             // junquillos horizontales
                add(junk, df1(tuboMocheta - 2f * jun), nSub * 2) // junquillos verticales
                // Vidrio de mocheta: submódulo y alto de mocheta, ambos con la misma holgura 0.4
                // (el alto ya la traía: (altoU−altoHoja)−2.9 = tuboMocheta − 0.4).
                add(vid, "${df1(anchoSub - 0.4f)} x ${df1((altoU - hHoja) - 2.9f)}", nSub)
            }
        }

        fun fmt(m: LinkedHashMap<String, Int>): String =
            m.entries.joinToString("\n") { "${it.key} = ${it.value}" }

        // Marco externo (a nivel ventana): solo si lleva marco.
        if (sinMarco) {
            binding.txMarco.text = "Marco"; binding.tvMarco.text = ""
        } else {
            binding.txMarco.text = "Marco ${binding.etMarco.text}"
            // 2 jambas a todo el alto (piso a techo) + marco sup/inf dividido por tramo.
            binding.tvMarco.text = "${df1(hRaw)} = 2\n${fmt(marH)}"
        }
        binding.txTubo.text = "Tubo puente"; binding.tvTubo.text = fmt(tubo)
        binding.txJamba.text = "Marco Serie"; binding.tvJamba.text = fmt(jam)
        // En cccc (4 corredizas) el perfil es "doble riel"; se refleja en la etiqueta.
        val rielDoble = tramos.all { it == "cccc" }
        binding.txRielI.text = if (rielDoble) "Doble Riel Inf." else "Riel Inf."; binding.tvRielI.text = fmt(rieI)
        binding.txRielS.text = if (rielDoble) "Doble Riel Sup." else "Riel Sup."; binding.tvRielS.text = fmt(rieS)
        binding.txParante.text = "Pierna"; binding.tvParante.text = fmt(pie)
        binding.txTraslape.text = "Traslape"; binding.tvTraslapo.text = fmt(tras)
        binding.txAdaptador.text = "Pierna c/aleta"; binding.tvAdaptador.text = fmt(ale)
        binding.txZocalo.text = "Zócalo"; binding.tvZocalo.text = fmt(zoc)
        // La etiqueta ES el nombre con el que se archiva la lista, así que tiene que ser la misma
        // que usan las demás pantallas (@string/junkillo). Poniendo "Junquillo" a mano, el
        // junquillo de esta serie acababa en una lista aparte: no aparecía junto al de los otros
        // productos en corte de varillas ni sumaba con ellos.
        binding.txJunki.text = getString(crystal.crystal.R.string.junkillo); binding.tvJunki.text = fmt(junk)
        binding.txCabezal.text = "Parante"; binding.tvCabezal.text = if (nPaflon > 0) "${df1(hRaw)} = $nPaflon" else ""
        binding.tvVidriosR.text = fmt(vid)

        binding.tvReferencias.text = "Ancho = ${binding.etAncho.text}, Alto = ${binding.etAlto.text}\n" +
                "Serie 3825 -> ${refOpcion()}" +
                "\nMódulos: " + tramos.mapIndexed { i, p -> "T${i + 1}:$p" }.joinToString("  ") +
                (if (mochetaActiva) "\nhHoja = ${df1(hHoja)}" else "")
        binding.tvPruebas.text = divisiones().toString()

        // Visibilidad de filas para la 3825.
        binding.lyMarco.visibility = if (binding.tvMarco.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyTubo.visibility = if (tubo.isEmpty()) View.GONE else View.VISIBLE
        binding.lyJamba.visibility = View.VISIBLE
        binding.lyRielI.visibility = View.VISIBLE
        binding.lyRielS.visibility = View.VISIBLE
        binding.lyParante.visibility = View.VISIBLE
        binding.lyTraslapo.visibility = if (tras.isEmpty()) View.GONE else View.VISIBLE
        binding.lyAdaptador.visibility = if (ale.isEmpty()) View.GONE else View.VISIBLE
        binding.lyZocalo.visibility = View.VISIBLE
        binding.lyJunki.visibility = if (junk.isEmpty()) View.GONE else View.VISIBLE
        binding.lyCabezal.visibility = if (nPaflon > 0) View.VISIBLE else View.GONE
        binding.lyVidrios.visibility = View.VISIBLE
        binding.lyRiel.visibility = View.GONE
        binding.lyTope.visibility = View.GONE

        // Orden de presentación: Marco, Tubo puente, Marco serie, Riel inf, Riel sup, Pierna,
        // Traslape, Pierna c/aleta, Zócalo, Junquillo, Paflón.
        (binding.lyMarco.parent as? LinearLayout)?.let { cont ->
            listOf(
                binding.lyMarco, binding.lyTubo, binding.lyJamba, binding.lyRielI, binding.lyRielS,
                binding.lyParante, binding.lyTraslapo, binding.lyAdaptador, binding.lyZocalo,
                binding.lyJunki, binding.lyCabezal
            ).forEachIndexed { i, v ->
                cont.removeView(v)
                cont.addView(v, i)
            }
        }

        renderizarPreviewEstructurada()
    }

    @SuppressLint("SetTextI18n")
    private fun parante() {
        if (serieActual?.nombre == "Serie 3825") {
            val pf = altoHoja() - 1.2f
            val en = altoHoja() - 2f
            paranteFijo3825 = MedidaPerfil(pf, nFijos() * 2)
            enganche3825 = MedidaPerfil(en, nCorredizas() * 2)
            binding.tvParante.text = if (divisiones() == 0) {
                "${df1(en)} = ${nCorredizas() * 2}"
            } else {
                "${df1(pf)} = ${nFijos() * 2}\n${df1(en)} = ${nCorredizas() * 2}"
            }
            return
        }
        binding.txParante.text = "Pierna"   // se llama pierna (no confundir con el parante de tramos)
        val pe = paran() + 1.4f
        binding.tvParante.text = if (divisiones() == 0) {
            "${df1(paran())} = ${nCorredizas() * 2}"
        } else {
            "${df1(pe)} = ${nFijos() * 2}\n${df1(paran())} = ${nCorredizas() * 2}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun marco(){
        if (serieActual?.nombre == "Serie 3825") {
            val alto = binding.etAlto.text.toString().toFloat()
            val sinMarco = !conMarco               // lo decide el diálogo (Con/Sin marco), no el grosor
            val anchU = anchUtil()                 // ancho - 2 * marco (0 si sin marco)
            val altoU = altoUtil()                 // alto  - 2 * marco
            // Sin marco: el Marco Serie cubre toda la altura interior.
            // Con marco: solo recorre la zona del alto de hoja (la mocheta usa la lógica de Clásica).
            val marcoSerieLat = if (sinMarco) altoU - 1.0f else altoHoja() - 1.0f
            val esPar = divisiones() % 2 == 0

            rielSup3825 = MedidaPerfil(anchU, 1)
            rielInf3825 = MedidaPerfil(anchU, 1)
            if (esPar) {
                marcoFijo3825 = MedidaPerfil(marcoSerieLat, 1, corte = "un lado en 4ª")
                marcoMovil3825 = MedidaPerfil(marcoSerieLat, 1, corte = "un lado en 4ª")
            } else {
                marcoFijo3825 = MedidaPerfil(marcoSerieLat, 2, corte = "un lado en 4ª")
                marcoMovil3825 = null
            }

            if (sinMarco) {
                binding.txMarco.text = "Marco"
                binding.tvMarco.text = ""
            } else {
                binding.txMarco.text = "Marco ${binding.etMarco.text}"
                binding.tvMarco.text = "${df1(alto)} = 2\n${df1(anchU)} = 2"
            }
            binding.txJamba.text = "Marco Serie"
            binding.tvJamba.text = "${df1(marcoSerieLat)} = 2"
            binding.tvRielS.text = "${df1(anchU)} = 1"
            binding.tvRielI.text = "${df1(anchU)} = 1"
            return
        }
        val alto = binding.etAlto.text.toString().toFloat()
        binding.txMarco.text = "Marco"
        // Verticales (jambas) = alto completo; horizontales (sup + inf) se parten por tramo (mismo
        // valor del puente), 2 por tramo (superior + inferior).
        binding.tvMarco.text = "${df1(alto)} = 2\n${df1(anchoTramo84())} = ${2 * nTramos84()}"
        binding.txJamba.text = "Jamba"
        binding.tvJamba.text = ""
        binding.tvRielS.text = ""
        binding.tvRielI.text = ""
        rielSup3825 = null
        rielInf3825 = null
        marcoFijo3825 = null
        marcoMovil3825 = null
    }
    @SuppressLint("SetTextI18n")
    private fun zocalo() {
        if (serieActual?.nombre == "Serie 3825") {
            val div = divisiones().coerceAtLeast(1)
            val anchU = anchUtil()
            val z = ((anchU - 1.4f) - (div + 1) * 2.4f) / div
            zocalo3825 = MedidaPerfil(z, div * 2)
            binding.tvZocalo.text = "${df1(z)} = ${div * 2}"
            return
        }
        zocalo3825 = null
        // Verificar queserieActual no sea nula
       serieActual?.let { serieActual ->
            val z = zoc()  // Se asume que zoc() retorna un Float
            // Utiliza la medida de la serie actual para el cálculo
            val adjustedZ = z - (2 * serieActual.medida.toFloat())

            binding.tvZocalo.text = "${df1(adjustedZ)} = ${divisiones() * 2}"
        } ?: run {
            // Manejar el caso en queserieActual sea nula
            binding.tvZocalo.text = "Error: No se ha seleccionado una serie"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun riel(){
        if (serieActual?.nombre == "Serie 3825") {
            // En Serie 3825 los rieles van en lyRielS / lyRielI (ya cargados en marco()).
            binding.tvRiel.text = ""
            return
        }
        // Riel superior e inferior: 1 de cada uno por tramo (ancho del tramo).
        val nT = nTramos84()
        val tramoW = anchoTramo84()
        binding.tvRiel.text = ""            // se reemplaza por riel sup/inf
        binding.txRielS.text = "Riel Sup."
        binding.tvRielS.text = "${df1(tramoW)} = $nT"
        binding.txRielI.text = "Riel Inf."
        binding.tvRielI.text = "${df1(tramoW)} = $nT"
    }
    
    @SuppressLint("SetTextI18n")
    private fun tope(){
        if (divisiones() == 0) {
            binding.tvTope.text = "${df1(altoUtil())} = 2"
            return
        }
        // Ángulo tope: 1 por tramo cuya modulación es fccf; en el resto, 1.
        val nFccf = nTramosFccf()
        binding.tvTope.text = "${df1(paran())} = ${if (nFccf > 0) nFccf else 1}"
    }

    private fun junkillo() {
        val jun = binding.etJunki.text.toString().toFloatOrNull() ?: 0f

        if (serieActual?.nombre == "Serie 3825") {
            // En Serie 3825 el junquillo aplica únicamente a las mochetas.
            if (altoHoja() >= altoUtil()) {
                binding.tvJunki.text = ""
                return
            }
            val tuboMocheta = altoUtil() - (altoHoja() + 2.5f)
            val mP1 = mPuentes1()
            val nSub1 = NovaCalculos.anchMota(mP1)         // ceil(mP1 / 180), mín. 1
            val anchoSub1 = if (nSub1 > 0) mP1 / nSub1 else mP1
            // Por cada panel de mocheta: 2 junquillos horizontales (sup+inf) y 2 verticales (izq+der).
            val qtyPanelesPrinc = nPuentes() * nSub1 * 2

            val lineas = mutableListOf<String>()
            if (qtyPanelesPrinc > 0) {
                lineas.add("${df1(anchoSub1)} = $qtyPanelesPrinc")
                lineas.add("${df1(tuboMocheta - (2 * jun))} = $qtyPanelesPrinc")
            }
            if (divisiones() == 10 || divisiones() == 14) {
                val mP2 = mPuentes2()
                if (mP2 > 0f) {
                    val nSub2 = NovaCalculos.anchMota(mP2)
                    val anchoSub2 = if (nSub2 > 0) mP2 / nSub2 else mP2
                    val qtySec = nSub2 * 2
                    if (qtySec > 0) lineas.add("${df1(anchoSub2)} = $qtySec")
                }
            }
            binding.tvJunki.text = lineas.joinToString("\n")
            return
        }

        val tuboMocheta = altoUtil() - (altoHoja() + 2.5f)
        val nSub1 = nTuboMocheta(mPuentes1()) + 1   // submódulos de mocheta (regla 180)
        // Junquillos: 2 horizontales (ancho del submódulo) + 2 verticales (alto de mocheta) por
        // submódulo, en cada puente.
        binding.tvJunki.text = if (divisiones() == 10 || divisiones() == 14) {
            "${df1(divMocheta(mPuentes1()))} = ${nSub1 * 2 * nPuentes()}\n" +
                    "${df1(divMocheta(mPuentes2()))} = ${(nTuboMocheta(mPuentes2()) + 1) * 2}\n" +
                    "${df1(tuboMocheta - (2 * jun))} = ${nSub1 * 2 * nPuentes()}"
        } else {
            "${df1(divMocheta(mPuentes1()))} = ${nSub1 * 2 * nPuentes()}\n" +
                    "${df1(tuboMocheta - (2 * jun))} = ${nSub1 * 2 * nPuentes()}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun puente() {
        val tuboMocheta = altoUtil() - (altoHoja() + 2.5f)

        if (serieActual?.nombre == "Serie 3825") {
            // En Serie 3825 los tubos verticales de mocheta se calculan con la regla de 180.
            val mP1 = mPuentes1()
            val nTubosMocheta = (NovaCalculos.anchMota(mP1) - 1).coerceAtLeast(0)
            val lineas = mutableListOf<String>()
            if (nPuentes() > 0) {
                lineas.add("${df1(mP1)} = ${nPuentes()}")
            }
            val qtyTubos = nPuentes() * nTubosMocheta
            if (qtyTubos > 0) {
                lineas.add("${df1(tuboMocheta)} = $qtyTubos")
            }
            binding.tvTubo.text = lineas.joinToString("\n")
            binding.lyTubo.visibility = if (altoHoja() >= altoUtil()) View.GONE else View.VISIBLE
            binding.tvPruebas.text = mP1.toString()
            binding.txPruebas.text = mPuentes2().toString()
            return
        }

        val baseTubo = when {
            (divisiones() == 10 || (divisiones() == 14 && nTuboMocheta(mPuentes2()) == 0)) -> {
                "${df1(mPuentes1())} = ${nPuentes() - 1}\n" +
                        "${df1(mPuentes2())} = ${nPuentes() - 2}\n" +
                        "${df1(tuboMocheta)} = ${nPuentes() * nTuboMocheta(mPuentes1())}"
            }
            (divisiones() == 10 || (divisiones() == 14 && nTuboMocheta(mPuentes2()) != 0)) -> {
                "${df1(mPuentes1())} = ${nPuentes() - 1}\n" +
                        "${df1(mPuentes2())} = ${nPuentes() - 2}"
            }
            (divisiones() == 10 || (divisiones() == 14 && nTuboMocheta(mPuentes1()) == 0)) -> {
                "${df1(mPuentes1())} = ${nPuentes()}"
            }
            else -> {
                "${df1(mPuentes1())} = ${nPuentes()}\n" +
                        "${df1(tuboMocheta)} = ${nPuentes() * nTuboMocheta(mPuentes1())}"
            }
        }
        // Parante de tramo (2.5, a todo el alto = de piso a techo) si hay más de un tramo.
        val nParantes = nTramos84() - 1
        val altoVent = binding.etAlto.text.toString().toFloatOrNull() ?: altoUtil()
        binding.tvTubo.text = if (nParantes > 0) "$baseTubo\n${df1(altoVent)} = $nParantes" else baseTubo

        binding.lyTubo.visibility = if (altoHoja() >= altoUtil()) View.GONE else View.VISIBLE
        binding.tvPruebas.text = mPuentes1().toString()
        binding.txPruebas.text = mPuentes2().toString()
    }

    // FUNCIONES VIDRIOS

    private fun vidrios() {
        if (serieActual?.nombre == "Serie 3825") {
            val div = divisiones().coerceAtLeast(1)
            val anchU = anchUtil()
            val zoc3825 = ((anchU - 1.4f) - (div + 1) * 2.4f) / div
            val anchoVidrio = zoc3825 + 1.5f
            val altoFijo = (altoHoja() - 1.2f) - 4.5f
            val altoCorr = (altoHoja() - 2f) - 4.5f
            binding.tvVidriosR.text = if (altoHoja() <= altoUtil()) {
                "${df1(anchoVidrio)} x ${df1(altoFijo)} = ${nFijos()}\n" +
                        "${df1(anchoVidrio)} x ${df1(altoCorr)} = ${nCorredizas()}\n" +
                        "${df1((altoUtil() - altoHoja()) - 2.9f)} x ${df1(divMocheta(mPuentes1()))} = ${nPuentes()}"
            } else {
                "${df1(anchoVidrio)} x ${df1(altoFijo)} = ${nFijos()}\n" +
                        "${df1(anchoVidrio)} x ${df1(altoCorr)} = ${nCorredizas()}"
            }
            return
        }
        val medida = serieActual?.medida?.toFloatOrNull() ?: 3f
        val ancho = (zoc() - 2 * medida) + 1.2f   // ancho de vidrio = zócalo + 1.2
        val alto = paran() - 8.0f                 // corrediza: pierna corrediza − 8
        val ale = (paran() + 1.4f) - 8.0f         // fijo: pierna fijo (paran+1.4) − 8
        binding.tvVidriosR.text = if (altoHoja() <= altoUtil()) {
            "${df1(ancho)} x ${df1(ale)} = ${nFijos()}\n" +
                    "${df1(ancho)} x ${df1(alto)} = ${nCorredizas()}\n" +
                    "${df1((altoUtil() - altoHoja()) - 2.9f)} x ${df1(divMocheta(mPuentes1()) - 0.5f)} = ${(nTuboMocheta(mPuentes1()) + 1) * nPuentes()}"
        } else {
            "${df1(ancho)} x ${df1(ale)} = ${nFijos()}\n" +
                    "${df1(ancho)} x ${df1(alto)} = ${nCorredizas()}"
        }
    }

    //  CAMBIOS DE SERIE
    @SuppressLint("SetTextI18n")
    private fun actualizarVentana() {
        if (listaSeries.isNotEmpty()) {
            // Asigna la serie actual
           serieActual = listaSeries[indices]
            binding.txVentana.text = "${serieActual?.nombre}"
            binding.txRiel.text = when (serieActual?.nombre) {
                "Clásica serie 84 eco." -> "Riel"
                "Clásica serie 84" -> "Riel"
                "Serie 20" -> "Riel Sup."
                "Serie 3825" -> "D. Riel Sup."
                "Serie 25" -> "D. Riel Sup."
                "Serie 62 europea" -> "D. Riel Sup."
                else -> ""
            }

            // Incrementar el índice para la próxima selección (si es necesario)
            indices = (indices + 1) % listaSeries.size
        } else {
            binding.txVentana.text = "Sin ventanas disponibles"
        }
    }

    private fun visibleVentana(){
        val series = listaSeries[indices]
        val nombre = series.nombre
        when (nombre){
            "Clásica" -> {
                binding.lyMarco.visibility = View.GONE
                binding.lyJamba.visibility = View.GONE
                binding.lyTubo.visibility = View.GONE
                binding.lyParante.visibility = View.GONE
                binding.lyRielI.visibility = View.GONE
                binding.lyZocalo.visibility = View.GONE
                binding.lyRielS.visibility = View.GONE
                binding.lyTraslapo.visibility=View.GONE
                binding.lyCabezal.visibility=View.GONE
                binding.lyAdaptador.visibility=View.GONE
            }
            "Serie 20"-> {
                binding.lyMarco.visibility = View.GONE
                binding.lyJunki.visibility = View.GONE
                binding.lyTubo.visibility = View.GONE
                binding.lyParante.visibility = View.GONE
                binding.lyRiel.visibility = View.GONE
                binding.lyZocalo.visibility = View.GONE
                binding.lyTope.visibility = View.GONE
            }
        "Serie 3825",
        "Serie 35",
        "Serie 62 europea"->{}
        }
    }

    //   FUNCIONES SERIE 2O

    //FUNCIONES DE ARCHIVO
    private fun archivarMapas() {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
            mapListas.clear()
            if (mapExistente != null) mapListas.putAll(mapExistente)
        }

        val prefijo = obtenerPrefijo()
        val cant = cantidadProducto

        // Los números se reservan ANTES del bucle: dentro, nada se ha guardado todavía y
        // obtenerSiguienteContadorPorPrefijo devolvería el mismo para todas las copias.
        val numerosPaquete = ProyectoManager.reservarNumerosPorPrefijo(this, prefijo, cant)
        for (u in 1..cant) {
            val siguienteNumero = numerosPaquete[u - 1]
            val identificadorPaquete = "${prefijo}${siguienteNumero}"

            // En VentanaAl la convención es: tx = etiqueta, tv = datos.
            // procesarArchivar* espera (label, data).
            if (esValido(binding.lyMarco)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txMarco, binding.tvMarco, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyParante)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txParante, binding.tvParante, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyZocalo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txZocalo, binding.tvZocalo, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyRiel)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txRiel, binding.tvRiel, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyRielS)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txRielS, binding.tvRielS, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyRielI)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txRielI, binding.tvRielI, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyJamba)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txJamba, binding.tvJamba, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTubo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txTubo, binding.tvTubo, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyJunki)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txJunki, binding.tvJunki, mapListas, identificadorPaquete)
            }
            // Serie 25: cabezal, traslape y adaptador de hoja.
            if (esValido(binding.lyCabezal)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txCabezal, binding.tvCabezal, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyTraslapo)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txTraslape, binding.tvTraslapo, mapListas, identificadorPaquete)
            }
            if (esValido(binding.lyAdaptador)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txAdaptador, binding.tvAdaptador, mapListas, identificadorPaquete)
            }
            // Vidrios: la fila no tiene etiqueta de título; se archiva bajo el nombre "Vidrios".
            if (binding.tvVidriosR.text.isNotBlank()) {
                val lblVidrios = android.widget.TextView(this).apply { text = "Vidrios" }
                ListaCasilla.procesarArchivarConPrefijo(this, lblVidrios, binding.tvVidriosR, mapListas, identificadorPaquete)
            }
            // Referencias: bajo el nombre "Referencias" para que la ficha las muestre.
            if (binding.tvReferencias.text.isNotBlank()) {
                ListaCasilla.procesarReferenciasConPrefijo(this, binding.tvReferencias, binding.tvReferencias, mapListas, identificadorPaquete)
            }

            // Cliente: archivar directamente (no usa formato "valor = cantidad")
            val clienteTexto = binding.txC.text.toString()
            if (clienteTexto.isNotBlank()) {
                val entradaCliente = mutableListOf(clienteTexto, "", identificadorPaquete)
                mapListas.getOrPut("Cliente") { mutableListOf() }.add(entradaCliente)
            }

            // Metadatos (color/vidrio) y diseño de la ventana de aluminio (para la ficha).
            if (metaColorAluminio.isNotBlank()) {
                mapListas.getOrPut("Color aluminio") { mutableListOf() }
                    .add(mutableListOf(metaColorAluminio, "", identificadorPaquete))
            }
            if (metaTipoVidrio.isNotBlank()) {
                mapListas.getOrPut("Tipo vidrio") { mutableListOf() }
                    .add(mutableListOf(metaTipoVidrio, "", identificadorPaquete))
            }
            // Los mismos datos en la clave canónica `-MAT<alu:...;vid:...>`. Las dos de arriba solo
            // las lee la ficha; el corte de varillas lee ESTA (MetadatosProduccion.mapaPorVentana),
            // y sin ella las listas salían con el nombre pelado, sin el color ni el vidrio que se
            // escriben en el diálogo de archivar.
            metadatosProduccionPaquete()?.let { paquete ->
                mapListas.getOrPut("MetadatosProduccion") { mutableListOf() }
                    .add(mutableListOf(paquete, "", identificadorPaquete))
            }
            construirDescriptorVentanaAl()?.let { desc ->
                mapListas.getOrPut("DisenoVentanaAl") { mutableListOf() }
                    .add(mutableListOf(desc.serializar(), "", identificadorPaquete))
            }
        }

        if (proyectoActivo != null) {
            MapStorage.guardarMap(this, mapListas)
        }
    }
    /**
     * Metadatos de la ventana en el formato que lee [crystal.crystal.casilla.MetadatosProduccion]:
     * `-MAT<alu:...;vid:...>`. Null cuando no se escribió ninguno de los dos.
     */
    private fun metadatosProduccionPaquete(): String? {
        val alu = escaparCampoMat(metaColorAluminio.ifBlank { "null" })
        val vid = escaparCampoMat(metaTipoVidrio.ifBlank { "null" })
        if (alu == "null" && vid == "null") return null
        return "-MAT<alu:$alu;vid:$vid>"
    }

    /** Sanea el texto para no romper el formato -MAT<...> (mismo criterio que Nova y Vitroven). */
    private fun escaparCampoMat(raw: String): String {
        return raw
            .replace("\n", " / ")
            .replace("\r", " ")
            .replace("-", "_")
            .replace("<", "(")
            .replace(">", ")")
            .replace(";", ",")
            .trim()
    }

    // Función para verificar si un Layout es visible o tiene estado GONE
    private fun esValido(ly: LinearLayout): Boolean {
        return ly.visibility == View.VISIBLE || ly.visibility == View.INVISIBLE
    }

    // Deshace los cambios de serie 20/25 (etiqueta "Pierna", filas cabezal/traslape/adaptador y el
    // reordenamiento) al volver a las series clásicas (84 / 3825 / etc.), para que no arrastren
    // textos/orden que no les corresponden.
    private fun restaurarLayoutClasicas() {
        binding.txParante.text = "Parante"
        binding.lyCabezal.visibility = View.GONE
        binding.lyTraslapo.visibility = View.GONE
        binding.lyAdaptador.visibility = View.GONE
        (binding.lyMarco.parent as? LinearLayout)?.let { cont ->
            listOf(
                binding.lyMarco, binding.lyParante, binding.lyZocalo, binding.lyRiel,
                binding.lyJunki, binding.lyTope, binding.lyTubo, binding.lyJamba,
                binding.lyRielI, binding.lyRielS, binding.lyTraslapo, binding.lyCabezal, binding.lyAdaptador
            ).forEachIndexed { i, v ->
                cont.removeView(v)
                cont.addView(v, i)
            }
        }
    }

    // Oculta layouts si el TextView de resultado está vacío
    private fun actualizarVisibilidadLayouts() {
        binding.lyMarco.visibility = if (binding.tvMarco.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyParante.visibility = if (binding.tvParante.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyZocalo.visibility = if (binding.tvZocalo.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyRiel.visibility = if (binding.tvRiel.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyRielS.visibility = if (binding.tvRielS.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyRielI.visibility = if (binding.tvRielI.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyJamba.visibility = if (binding.tvJamba.text.isNullOrBlank()) View.GONE else View.VISIBLE
        // lyTubo ya se maneja en puente() según condición de altura
        binding.lyJunki.visibility = if (binding.tvJunki.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyTope.visibility = if (binding.tvTope.text.isNullOrBlank()) View.GONE else View.VISIBLE
    }

    //   FUNCIONES GENERALES
    // Marco efectivo por lado para los cálculos. `etMarco` es el GROSOR del marco (por defecto 2.2);
    // `conMarco` (del diálogo de ivModelo) indica SI la ventana lleva marco externo. En series con
    // marco opcional (3825), "Sin marco" hace que el cálculo nazca de la medida original (descuento 0);
    // en series con marco fijo (84) siempre descuenta.
    private fun marcoEfectivo(): Float {
        if (VentanaAlRender.serieUsaMarcoOpcional(serieActual?.nombre ?: "") && !conMarco) return 0f
        return binding.etMarco.text.toString().toFloatOrNull() ?: 2.2f
    }

    private fun anchUtil(): Float {
        val ancho = binding.etAncho.text.toString().toFloat()
        return ancho - (2 * marcoEfectivo())
    }

    private fun altoUtil(): Float {
        val alto = binding.etAlto.text.toString().toFloat()
        return alto - (2 * marcoEfectivo())
    }

    private fun zoc(): Float {
        val div = divisiones()
        val cruce = when (div) {
            2, 3, 5, 7, 9, 11, 13, 15 -> div - 1
            4, 6, 10 -> div - 2
            8, 12 -> div / 2
            14 -> div - 4
            else -> div
        }
        val partes = ((anchUtil() - ((nPuentes() - 1) * 2.5f)) + (cruce * 3.2f)) / div
        return if (div == 1) anchUtil() else partes
    }

    private fun paran(): Float {
        return altoHoja() - 1.4f
    }
    private fun nTuboMocheta(p1: Float): Int {
        return (NovaCalculos.anchMota(p1) - 1).coerceAtLeast(0)   // nº de tubos = submódulos − 1 (regla 180)
    }

    private fun nFijos():Int {
        return when (divisiones()){
            1 -> 1  2 -> 1
            3 -> 2  4 -> 2
            5 -> 3
            6 -> 4  7 -> 4
            8 -> 4
            9 -> 5
            10 ->6  11 -> 6  12 -> 6
            13 ->7  14 ->8   15 -> 8
            else -> 0
        }
    }
    private fun nCorredizas():Int {
        return when (divisiones()){
            1 -> 0
            2 -> 1   3 -> 1
            4 -> 2   5 -> 2   6 -> 2
            7 -> 3
            8 -> 4   9 -> 4   10 -> 4
            11-> 5
            12-> 6   13-> 6   14 -> 6
            15-> 7
            else -> 0
        }
    }
    private fun nPuentes(): Int {
        return when (divisiones()) {
            1, 2, 3, 4, 5 -> 1
            6, 8 -> 2
            7, 9, 11, 13, 15 -> 1
            10, 12, 14 -> 3
            else -> 0
        }
    }

    private fun mPuentes1(): Float {
        val ancho = anchUtil()
        return when (divisiones()) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> ancho
            6, 8 -> (ancho - 2.5f) / 2
            10 -> ((ancho - (2 * 2.5f)) / divisiones()) * 3
            12 -> (ancho - (2 * 2.5f)) / 3
            14 -> ((ancho - (2 * 2.5f)) / divisiones()) * 5
            else -> 0f
        }
    }

    private fun mPuentes2(): Float {
        val ancho = anchUtil()
        return when (divisiones()) {
            10, 14 -> ((ancho - (2 * 2.5f)) / divisiones()) * 4
            else -> 0f
        }
    }
    private fun divMocheta(p1: Float): Float {
        val n = NovaCalculos.anchMota(p1)          // nº de submódulos = ceil(p1/180), mín 1
        return (p1 - (n - 1) * 2.5f) / n            // ancho útil, restando el tubo (2.5) entre submódulos
    }

    // Tramos de serie 84 (según el orden con parantes de Nova). Ancho útil de cada tramo (uniforme).
    private fun nTramos84(): Int =
        NovaCalculos.ordenDivisConParantes(divisiones(), anchUtil()).split(";P;").size

    private fun anchoTramo84(): Float {
        val nT = nTramos84().coerceAtLeast(1)
        return (anchUtil() - (nT - 1) * 2.5f) / nT
    }

    // Nº de tramos cuyo patrón es fccf (para el ángulo tope).
    private fun nTramosFccf(): Int =
        NovaCalculos.ordenDivisConParantes(divisiones(), anchUtil()).split(";P;").count { seg ->
            Regex("[fc](?=<)").findAll(seg).joinToString("") { it.value } == "fccf"
        }
    private fun nMocheta() {
        val n = binding.etMocheta.text.toString().toFloat()
        val xn = if (n == 0f) { // Reemplazado ColumnText.GLOBAL_SPACE_CHAR_RATIO por 0f
            nTuboMocheta(mPuentes1()).toString()
        } else {
            n.toString()
        }
        binding.txPruebas.text = xn
    }
    private fun altoHoja(): Float {
        val alto = altoUtil()
        val hoja = binding.etAltohoja.text.toString().toFloat()
        val corre = if (hoja > alto) alto else hoja
        return if (hoja == 0f) { // Reemplazado ColumnText.GLOBAL_SPACE_CHAR_RATIO por 0f
            (alto / 7) * 5
        } else {
            corre
        }
    }

    private fun divisiones(): Int {
        val ancho = binding.etAncho.text.toString().toFloatOrNull() ?: 0f
        val divis = binding.etPartes.text.toString().toIntOrNull() ?: 0
        return if (divis == 0) {
            ((ancho / 60f).toInt() + if (ancho % 60f > 0f) 1 else 0)
        } else {
            divis
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            // Solo incluir perfiles cuyos layouts sean visibles
            val perfiles = mutableMapOf<String, String>()
            if (esValido(binding.lyParante)) perfiles["Parante"] = ModoMasivoHelper.texto(binding.tvParante)
            if (esValido(binding.lyZocalo)) perfiles["Zócalo"] = ModoMasivoHelper.texto(binding.tvZocalo)
            if (esValido(binding.lyRiel)) perfiles["Riel"] = ModoMasivoHelper.texto(binding.tvRiel)
            if (esValido(binding.lyRielS)) perfiles["Riel Sup."] = ModoMasivoHelper.texto(binding.tvRielS)
            if (esValido(binding.lyRielI)) perfiles["Riel Inf."] = ModoMasivoHelper.texto(binding.tvRielI)
            if (esValido(binding.lyJamba)) perfiles[binding.txJamba.text.toString()] = ModoMasivoHelper.texto(binding.tvJamba)
            if (esValido(binding.lyTubo)) perfiles["Tubo"] = ModoMasivoHelper.texto(binding.tvTubo)

            val accesorios = mutableMapOf<String, String>()
            if (esValido(binding.lyTope)) accesorios["Tope"] = ModoMasivoHelper.texto(binding.tvTope)
            if (esValido(binding.lyJunki)) accesorios["Junquillo"] = ModoMasivoHelper.texto(binding.tvJunki)

            ModoMasivoHelper.devolverResultado(
                activity = this,
                calculadora = "Ventana Aluminio",
                perfiles = perfiles,
                vidrios = ModoMasivoHelper.texto(binding.tvVidriosR),
                accesorios = accesorios,
                referencias = ModoMasivoHelper.texto(binding.tvReferencias)
            )
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }

    // Muestra en un diálogo la imagen del orden de colocación de módulos. Si hay varios patrones (uno
    // por tramo) los apila etiquetados. Provisional mientras se cargan las imágenes reales; reutiliza
    // el mismo resolvedor que la ficha.
    private fun mostrarImagenOrden(serie: String, patrones: List<String>) {
        if (patrones.isEmpty()) return
        val dens = resources.displayMetrics.density
        val pad = (12 * dens).toInt()
        val box = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(pad, pad, pad, pad)
        }
        patrones.forEachIndexed { i, pat ->
            if (patrones.size > 1) box.addView(android.widget.TextView(this).apply {
                text = "Tramo ${i + 1}: $pat"
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                setPadding(0, (8 * dens).toInt(), 0, (2 * dens).toInt())
            })
            val resId = VentanaAlRender.imagenOrdenModulos(this, serie, pat)
            if (resId != 0) box.addView(android.widget.ImageView(this).apply {
                setImageResource(resId); adjustViewBounds = true
            }) else box.addView(android.widget.TextView(this).apply { text = "(sin imagen para $pat)" })
        }
        AlertDialog.Builder(this)
            .setTitle("Orden de módulos")
            .setView(android.widget.ScrollView(this).apply { addView(box) })
            .setPositiveButton("Cerrar", null)
            .show()
    }

    // Selector de modulación reutilizable (para todas las series con modulación). Cada patrón va en su
    // fila con un RadioButton y, si no es "tramos", un botón "Imagen" que muestra su orden de módulos
    // ("tramos" no lleva imagen porque deriva de los modelos). Selección única manual; `onCambio` se
    // dispara al cambiar. Devuelve (vista, función que da el patrón elegido).
    private fun construirSelectorModulacion(
        patrones: Array<String>, seleccion: String, serie: String, onCambio: (String) -> Unit
    ): Pair<android.view.View, () -> String> {
        val cont = android.widget.LinearLayout(this).apply { orientation = android.widget.LinearLayout.VERTICAL }
        val radios = mutableListOf<Pair<String, android.widget.RadioButton>>()
        patrones.forEach { pat ->
            val fila = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }
            val rb = android.widget.RadioButton(this)
            rb.text = pat
            rb.isChecked = pat == seleccion
            rb.setOnClickListener {
                radios.forEach { (_, o) -> o.isChecked = (o === rb) }   // exclusión manual
                onCambio(pat)
            }
            radios.add(pat to rb)
            fila.addView(rb, android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
            if (!pat.equals("tramos", ignoreCase = true)) {
                fila.addView(android.widget.Button(this).apply {
                    text = "Imagen"
                    setOnClickListener { mostrarImagenOrden(serie, listOf(pat)) }
                })
            }
            cont.addView(fila)
        }
        if (radios.none { it.second.isChecked }) radios.firstOrNull()?.second?.isChecked = true
        return cont to { radios.firstOrNull { it.second.isChecked }?.first ?: patrones[0] }
    }

    /**
     * Diálogo de opciones del diseño (v1, estilo Nova). Por ahora una banda: con/sin marco externo,
     * que solo aplica a series con marco opcional (3825); en serie 84 el marco es parte de la ventana,
     * así que esa opción no existe.
     */
    private fun mostrarDialogoOpciones() {
        val serie = serieActual?.nombre ?: ""
        when {
            // Serie 20/25/62: elegir el patrón (fc, cc, cfc, fccf, cccc), con botón "Imagen" por patrón.
            VentanaAlRender.serieUsaPatron(serie) -> {
                val opciones = VentanaAlRender.patronesDe(serie)
                val (vista, getSel) = construirSelectorModulacion(opciones, patron20, serie) {}
                crystal.crystal.taller.OpcionesUI.mostrar(
                    this, "Tipo de ventana", cantidadProducto, vista
                ) { cant ->
                    cantidadProducto = cant
                    patron20 = getSel()
                    renderizarPreviewEstructurada()
                }
            }
            // Serie 3825: un solo diálogo — marco externo (Con/Sin) y la modulación POR TRAMO (un
            // selector por cada tramo, limitado a los patrones de su tamaño). Todo con botones de opción.
            VentanaAlRender.serieUsaMarcoOpcional(serie) -> {
                val dens = resources.displayMetrics.density
                val pad = (16 * dens).toInt()
                val cont = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                    setPadding(pad, pad, pad, 0)
                }
                fun titulo(t: String) = android.widget.TextView(this).apply {
                    text = t
                    setPadding(0, (10 * dens).toInt(), 0, (2 * dens).toInt())
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                }
                // Marco externo.
                cont.addView(titulo("Marco externo"))
                val rgMarco = android.widget.RadioGroup(this)
                val rbConMarco = android.widget.RadioButton(this).apply { text = "Con marco"; id = View.generateViewId() }
                val rbSinMarco = android.widget.RadioButton(this).apply { text = "Sin marco"; id = View.generateViewId() }
                rgMarco.addView(rbConMarco); rgMarco.addView(rbSinMarco)
                rgMarco.check(if (conMarco) rbConMarco.id else rbSinMarco.id)
                cont.addView(rgMarco)

                // Modulación general: "tramos" (reparto automático, con override por tramo) o un patrón
                // simple. Cada patrón (no "tramos") lleva su botón "Imagen".
                val patrones = VentanaAlRender.patronesDe(serie)   // tramos, fc, cc, cfc, fccf, cccc
                cont.addView(titulo("Modulación"))
                var refrescarPerTramo: (() -> Unit)? = null
                val (selVista, getSelGen) = construirSelectorModulacion(patrones, patron20, serie) {
                    refrescarPerTramo?.invoke()
                }
                cont.addView(selVista)

                // Modulación por tramo (override; solo visible al elegir "tramos"; necesita medidas).
                // No lleva imagen porque los tramos derivan de los modelos de modulación.
                val hayMedidas = binding.etAncho.text.toString().toFloatOrNull() != null &&
                        binding.etAlto.text.toString().toFloatOrNull() != null
                val perTramoBox = android.widget.LinearLayout(this).apply {
                    orientation = android.widget.LinearLayout.VERTICAL
                }
                val rgPorTramo = mutableListOf<android.widget.RadioGroup>()
                val opcionesTramo = mutableListOf<List<String>>()
                if (hayMedidas) {
                    perTramoBox.addView(titulo("Modulación por tramo"))
                    tramosEfectivos3825().forEachIndexed { i, patTramo ->
                        val opts = patronesDeTamano(hojasPatron(patTramo))
                        opcionesTramo.add(opts)
                        perTramoBox.addView(android.widget.TextView(this).apply {
                            text = "Tramo ${i + 1}"
                            setPadding(0, (6 * dens).toInt(), 0, 0)
                        })
                        val rg = android.widget.RadioGroup(this).apply {
                            orientation = android.widget.LinearLayout.HORIZONTAL
                        }
                        opts.forEach { op ->
                            val rb = android.widget.RadioButton(this).apply {
                                text = op; id = View.generateViewId()
                                setPadding(paddingLeft, paddingTop, (16 * dens).toInt(), paddingBottom)
                            }
                            rg.addView(rb)
                            if (op == patTramo) rg.check(rb.id)
                        }
                        rgPorTramo.add(rg)
                        perTramoBox.addView(rg)
                    }
                } else {
                    perTramoBox.addView(android.widget.TextView(this).apply {
                        text = "Ingresa medidas y calcula para elegir la modulación por tramo."
                        setPadding(0, (6 * dens).toInt(), 0, 0)
                    })
                }
                cont.addView(perTramoBox)
                refrescarPerTramo = {
                    perTramoBox.visibility =
                        if (getSelGen().equals("tramos", ignoreCase = true)) View.VISIBLE else View.GONE
                }
                refrescarPerTramo?.invoke()

                crystal.crystal.taller.OpcionesUI.mostrar(
                    this, "Opciones de diseño", cantidadProducto, cont
                ) { cant ->
                    cantidadProducto = cant
                    conMarco = rgMarco.checkedRadioButtonId == rbConMarco.id
                    patron20 = getSelGen()
                    if (patron20.equals("tramos", ignoreCase = true) && hayMedidas) {
                        val elegidos = rgPorTramo.mapIndexed { i, rg ->
                            val idx = rg.indexOfChild(rg.findViewById(rg.checkedRadioButtonId)).coerceAtLeast(0)
                            opcionesTramo[i].getOrElse(idx) { opcionesTramo[i][0] }
                        }
                        patronesTramo3825.clear(); patronesTramo3825.addAll(elegidos)
                    }
                    if (hayMedidas) calcularSerieClasica() else renderizarPreviewEstructurada()
                }
            }
            // Serie 84: el marco es parte de la ventana, pero la cantidad se elige igual que en
            // todas las demás calculadoras.
            else -> crystal.crystal.taller.OpcionesUI.mostrar(
                this, "Opciones", cantidadProducto
            ) { cant -> cantidadProducto = cant }
        }
    }

    /**
     * Materiales y vidrio de serie 25 (riel Convencional / doble riel) según el patrón elegido.
     * Medidas de la plantilla en mm, convertidas a cm (÷10). Cada corrediza aporta un bastidor
     * (cabezal + zócalo + pierna + traslape), por eso esas cantidades = nº de corredizas. El
     * adaptador de hoja va (×1) cuando hay corredizas contiguas.
     */
    @SuppressLint("SetTextI18n")
    private fun calcularSerieTramos(s25: Boolean) {
        val a = binding.etAncho.text.toString().replace(",", ".").toFloatOrNull() ?: return
        val h = binding.etAlto.text.toString().replace(",", ".").toFloatOrNull() ?: return

        // Divisiones por ancho (ceil 60, como Nova) repartidas en tramos de 2–4 módulos. Cada tramo
        // es una ventana completa (jamba, rieles, bastidores); entre tramos va un paflón a todo el alto.
        // "tramos" = reparto por divisiones; otro patrón = ventana simple (un solo cuerpo).
        val esTramos = patron20.equals("tramos", ignoreCase = true)
        val div = if (esTramos) divisiones().coerceAtLeast(2)
                  else when (patron20) { "cfc", "fcc", "fcf", "ccc" -> 3; "fccf", "cccc" -> 4; else -> 2 }
        val tramos = if (esTramos) VentanaAlRender.tramosSerie25(div) else listOf(patron20)
        val nPaflon = tramos.size - 1
        val moduleW = (a - nPaflon * 2.5f) / div       // ancho de cada módulo (descontando paflones)
        // Descuentos por serie (plantilla en mm → cm). s25 = serie 25; si no, serie 20.
        val rielDed = if (s25) 1.6f else 1.2f
        val rielInfQty = 1   // 1 riel inferior por ventana (igual que el superior)
        val vertical = h - (if (s25) 3.5f else 2.5f)   // pierna / traslape
        val adaptMed = h - (if (s25) 3.5f else 4.3f)   // adaptador de hoja
        val adaptQty = if (s25) 1 else 2
        val altoVid = h - (if (s25) 12.5f else 9.8f)

        // Acumuladores por perfil: medida (df1) -> cantidad total (se agrupan medidas iguales).
        val cab = LinkedHashMap<String, Int>(); val zoc = LinkedHashMap<String, Int>()
        val pie = LinkedHashMap<String, Int>(); val tra = LinkedHashMap<String, Int>()
        val rieS = LinkedHashMap<String, Int>(); val rieI = LinkedHashMap<String, Int>()
        val jam = LinkedHashMap<String, Int>(); val ada = LinkedHashMap<String, Int>()
        val vid = LinkedHashMap<String, Int>()
        fun add(m: LinkedHashMap<String, Int>, medida: String, q: Int) {
            if (q > 0) m[medida] = (m[medida] ?: 0) + q
        }

        for (pat in tramos) {
            val hojas = when (pat) { "cfc" -> 3; "fccf", "cccc" -> 4; else -> 2 }
            val nCorr = when (pat) { "cc", "cfc", "fccf" -> 2; "cccc" -> 4; else -> 1 }
            val nFijo = when (pat) { "fc", "cfc" -> 1; "fccf" -> 2; else -> 0 }
            val tw = hojas * moduleW                     // ancho del tramo
            val bs = tw / hojas
            val cabCorr = bs + when (pat) { "cfc" -> if (s25) 0f else -0.1f; "fccf", "cccc" -> if (s25) 0.8f else 0.4f; else -> if (s25) 0.4f else 0.1f }
            val cabFijo = bs + when (pat) { "cfc" -> if (s25) 4.2f else 2.9f; "fccf" -> if (s25) 0.8f else 0.4f; else -> if (s25) 0.4f else 0.1f }
            val zocCorr = bs + when (pat) { "cfc" -> if (s25) -0.2f else -0.5f; "fccf", "cccc" -> if (s25) 0.6f else 0f; else -> if (s25) 0.2f else -0.3f }
            val zocFijo = bs + when (pat) { "cfc" -> if (s25) 4.2f else 2.9f; "fccf" -> if (s25) 0.8f else 0.4f; else -> if (s25) 0.4f else 0.1f }
            val piernaQty = when (pat) { "fccf", "cccc" -> 4; else -> 2 }
            val traslapeQty = when (pat) { "cfc", "fccf", "cccc" -> 4; else -> 2 }
            // Vidrio: ancho por hoja (descuento por nº de hojas y serie); el fijo de cfc va aparte.
            val vidCorr = bs - when (hojas) { 2 -> if (s25) 6.4f else 5.2f; 3 -> if (s25) 6.8f else 5.4f; else -> if (s25) 6.0f else 4.9f }
            val vidFijoCfc = tw / 3f - (if (s25) 1.3f else 0.6f)
            val vidCccc = tw / 4f + (if (s25) -6.0f else 4.9f)   // serie 20 cccc: A/4 + 4.9

            add(rieS, df1(tw - rielDed), 1)
            add(rieI, df1(tw - rielDed), rielInfQty)
            add(jam, df1(h), if (s25) 2 else nCorr)
            add(cab, df1(cabCorr), nCorr); if (nFijo > 0) add(cab, df1(cabFijo), nFijo)
            add(zoc, df1(zocCorr), nCorr); if (nFijo > 0) add(zoc, df1(zocFijo), nFijo)
            add(pie, df1(vertical), piernaQty)
            add(tra, df1(vertical), traslapeQty)
            if (pat == "fccf" || pat == "fcf") add(ada, df1(adaptMed), adaptQty)
            when (pat) {
                "cfc" -> {
                    add(vid, "${df1(vidCorr)} x ${df1(altoVid)}", 2)
                    add(vid, "${df1(vidFijoCfc)} x ${df1(altoVid)}", 1)
                }
                "cccc" -> add(vid, "${df1(vidCccc)} x ${df1(altoVid)}", 4)
                "fccf" -> add(vid, "${df1(vidCorr)} x ${df1(altoVid)}", 4)
                else -> add(vid, "${df1(vidCorr)} x ${df1(altoVid)}", 2)   // fc, cc
            }
        }

        fun fmt(m: LinkedHashMap<String, Int>): String =
            m.entries.joinToString("\n") { "${it.key} = ${it.value}" }

        binding.txRielS.text = "Riel Sup."; binding.tvRielS.text = fmt(rieS)
        binding.txRielI.text = "Riel Inf."; binding.tvRielI.text = fmt(rieI)
        binding.txJamba.text = "Jamba"; binding.tvJamba.text = fmt(jam)
        binding.txCabezal.text = "Cabezal"; binding.tvCabezal.text = fmt(cab)
        binding.txZocalo.text = "Zócalo"; binding.tvZocalo.text = fmt(zoc)
        binding.txParante.text = "Pierna"; binding.tvParante.text = fmt(pie)
        binding.txTraslape.text = "Traslape"; binding.tvTraslapo.text = fmt(tra)
        binding.txAdaptador.text = "Adaptador de hoja"; binding.tvAdaptador.text = fmt(ada)
        binding.tvVidriosR.text = fmt(vid)
        binding.txMarco.text = "Parante"
        binding.tvMarco.text = if (nPaflon > 0) "${df1(h)} = $nPaflon" else ""

        // Visibilidad: solo lo que usa serie 25.
        binding.lyRielS.visibility = View.VISIBLE
        binding.lyRielI.visibility = View.VISIBLE
        binding.lyCabezal.visibility = View.VISIBLE
        binding.lyZocalo.visibility = View.VISIBLE
        binding.lyParante.visibility = View.VISIBLE
        binding.lyTraslapo.visibility = View.VISIBLE
        binding.lyAdaptador.visibility = if (ada.isNotEmpty()) View.VISIBLE else View.GONE
        binding.lyJamba.visibility = View.VISIBLE
        binding.lyMarco.visibility = if (nPaflon > 0) View.VISIBLE else View.GONE
        binding.lyRiel.visibility = View.GONE
        binding.lyTope.visibility = View.GONE
        binding.lyTubo.visibility = View.GONE
        binding.lyJunki.visibility = View.GONE

        // Orden de presentación para serie 25: jamba, riel inf, riel sup, pierna, traslape,
        // zócalo, cabezal, adaptador y paflón (parante de tramo) al final.
        (binding.lyJamba.parent as? LinearLayout)?.let { cont ->
            listOf(
                binding.lyJamba, binding.lyRielI, binding.lyRielS, binding.lyParante,
                binding.lyTraslapo, binding.lyZocalo, binding.lyCabezal, binding.lyAdaptador,
                binding.lyMarco
            ).forEachIndexed { i, v ->
                cont.removeView(v)
                cont.addView(v, i)
            }
        }
    }

    /**
     * Materiales de serie 62 europea (VL48) para el patrón elegido (ventana simple, sin tramos).
     * Marco = 2 horizontales (ancho) + 2 verticales (alto). Bastidor (marco de hoja) = 2 horizontales
     * + 2 verticales por módulo. Adaptador = 1 solo en fccf. Cortaviento ignorado. Medidas mm → cm.
     */
    /** Texto de referencia según la opción del diálogo: divisiones si es "tramos", o el patrón. */
    private fun refOpcion(): String =
        if (patron20.equals("tramos", ignoreCase = true)) "Divisiones = ${divisiones()}"
        else "Patrón = $patron20"

    /** Modelos por tramo para las referencias: "\nMódulos: T1:fc  T2:fcc". Vacío si no aplica. */
    private fun modulacionTramosTexto(): String {
        val serie = serieActual?.nombre ?: ""
        val esTramos = patron20.equals("tramos", ignoreCase = true)
        val tramos: List<String> = when {
            serie == "Serie 3825" -> tramosEfectivos3825()
            serie == "Serie 62 europea" ->
                if (esTramos) VentanaAlRender.tramosDe(divisiones().coerceAtLeast(2), "fcc") else listOf(patron20)
            VentanaAlRender.serieUsaPatron(serie) ->
                if (esTramos) VentanaAlRender.tramosSerie25(divisiones().coerceAtLeast(2)) else listOf(patron20)
            else -> emptyList()
        }
        return if (tramos.isEmpty()) "" else "\nMódulos: " + tramos.mapIndexed { i, p -> "T${i + 1}:$p" }.joinToString("  ")
    }

    @SuppressLint("SetTextI18n")
    private fun calcularSerie62() {
        val a = binding.etAncho.text.toString().replace(",", ".").toFloatOrNull() ?: return
        val h = binding.etAlto.text.toString().replace(",", ".").toFloatOrNull() ?: return
        restaurarLayoutClasicas()

        val esTramos = patron20.equals("tramos", ignoreCase = true)
        val div = if (esTramos) divisiones().coerceAtLeast(2)
                  else when (patron20) { "fcc", "ccc" -> 3; "fccf", "cccc" -> 4; else -> 2 }
        val tramos = if (esTramos) VentanaAlRender.tramosDe(div, "fcc") else listOf(patron20)
        val nPaflon = tramos.size - 1
        val moduleW = (a - nPaflon * 2.5f) / div

        val marco = LinkedHashMap<String, Int>(); val bast = LinkedHashMap<String, Int>()
        val ada = LinkedHashMap<String, Int>(); val vid = LinkedHashMap<String, Int>()
        val tras = LinkedHashMap<String, Int>()
        fun add(m: LinkedHashMap<String, Int>, med: String, q: Int) { if (q > 0) m[med] = (m[med] ?: 0) + q }

        for (pat in tramos) {
            val hojas = when (pat) { "fcc", "ccc" -> 3; "fccf", "cccc" -> 4; else -> 2 }
            val tw = hojas * moduleW
            val bastHorizOff = when (pat) { "fcc", "ccc" -> 2.6f; "fccf", "cccc" -> 1.9f; else -> 0.8f }
            val vidOff = when (pat) { "fcc", "ccc" -> 7.7f; "fccf", "cccc" -> 8.4f; else -> 9.5f }
            // Marco por ventana/tramo: 2 horizontales (ancho) + 2 verticales (alto).
            add(marco, df1(tw), 2); add(marco, df1(h), 2)
            // Bastidor por módulo: 2 horizontales + 2 verticales.
            add(bast, df1(tw / hojas + bastHorizOff), 2 * hojas)
            add(bast, df1(h - 5.0f), 2 * hojas)
            // Adaptador de hoja: 1 por tramo en fccf y cccc.
            if (pat == "fccf" || pat == "cccc") add(ada, df1(h - 5.0f), 1)
            // Traslape de hoja (Excel S62): en todos los patrones, largo = alto − 50 mm. Cantidad por
            // patrón: 2 hojas (fc/cc) → 2; 3 y 4 hojas (fcc/ccc/fccf/cccc) → 4.
            val nTras = when (pat) { "fc", "cc" -> 2; "fcc", "ccc", "fccf", "cccc" -> 4; else -> 0 }
            if (nTras > 0) add(tras, df1(h - 5.0f), nTras)
            add(vid, "${df1(tw / hojas - vidOff)} x ${df1(h - 15.4f)}", hojas)
        }
        fun fmt(m: LinkedHashMap<String, Int>): String = m.entries.joinToString("\n") { "${it.key} = ${it.value}" }

        // En fcc/ccc (3 corredizas) el marco es de triple corrediza; se refleja en la etiqueta.
        val marcoTriple = tramos.all { it == "fcc" || it == "ccc" }
        binding.txMarco.text = if (marcoTriple) "Marco triple corrediza" else "Marco"
        binding.tvMarco.text = fmt(marco)
        binding.txParante.text = "Bastidor"; binding.tvParante.text = fmt(bast)
        binding.txAdaptador.text = "Adaptador de hoja"; binding.tvAdaptador.text = fmt(ada)
        binding.txTraslape.text = "Traslape hoja"; binding.tvTraslapo.text = fmt(tras)
        binding.tvVidriosR.text = fmt(vid)
        binding.txZocalo.text = "Parante"
        binding.tvZocalo.text = if (nPaflon > 0) "${df1(h)} = $nPaflon" else ""

        binding.lyMarco.visibility = View.VISIBLE
        binding.lyParante.visibility = View.VISIBLE
        binding.lyAdaptador.visibility = if (ada.isNotEmpty()) View.VISIBLE else View.GONE
        binding.lyTraslapo.visibility = if (tras.isNotEmpty()) View.VISIBLE else View.GONE
        binding.lyZocalo.visibility = if (nPaflon > 0) View.VISIBLE else View.GONE
        binding.lyRiel.visibility = View.GONE
        binding.lyRielS.visibility = View.GONE
        binding.lyRielI.visibility = View.GONE
        binding.lyJamba.visibility = View.GONE
        binding.lyTubo.visibility = View.GONE
        binding.lyJunki.visibility = View.GONE
        binding.lyTope.visibility = View.GONE
    }

    private fun renderizarPreviewEstructurada() {
        val ancho = binding.etAncho.text?.toString()?.replace(",", ".")?.toFloatOrNull() ?: 0f
        val alto = binding.etAlto.text?.toString()?.replace(",", ".")?.toFloatOrNull() ?: 0f
        if (ancho <= 0f || alto <= 0f) return

        // Serie 84 / 84 eco.: diseño propio (marco en todo el contorno, puente y módulos
        // con bastidor completo). El resto de series usa el preview simbólico (estructurada).
        val serieNombre = serieActual?.nombre ?: ""
        if (VentanaAlRender.soportaSerie(serieNombre)) {
            val marcoVal = binding.etMarco.text.toString().toFloatOrNull() ?: 2.2f
            val hoja = altoHojaParaDescriptor(alto)
            val nMo = binding.etMocheta.text.toString().toIntOrNull() ?: 0
            val descriptor = VentanaAlDescriptor(
                ancho = ancho,
                alto = alto,
                altoHoja = hoja,
                divisiones = divisiones().coerceAtLeast(1),
                serie = serieNombre,
                marco = marcoVal,
                nMochetas = nMo,
                conMarco = conMarco,
                patron = patronParaDescriptor()
            )
            VentanaAlRender.dibujar(this, descriptor)?.let {
                binding.ivModelo.setImageBitmap(it)
                return
            }
        }

        val divsCuerpo = divisiones().coerceAtLeast(1)
        val hojaAlto = runCatching { altoHoja() }.getOrDefault(0f)
        val altoTransom = (alto - hojaAlto).let { if (it <= 8f) alto * 0.18f else it }
        val serie = serieActual?.nombre?.removePrefix("Serie ")?.trim() ?: "3825"

        val params = ParametrosEstructurada(
            anchoCm = ancho,
            altoCm = alto,
            altoTransomCm = altoTransom,
            divisionesTransom = NovaCalculos.anchMota(ancho),
            divisionesCuerpo = divsCuerpo,
            serie = serie,
            vista = null
        )

        val viewW = binding.ivModelo.width.takeIf { it > 0 } ?: (resources.displayMetrics.density * 220).toInt()
        val viewH = binding.ivModelo.height.takeIf { it > 0 } ?: (resources.displayMetrics.density * 90).toInt()
        val vista = VistaEstructurada(this).apply {
            aplicar(params)
            measure(
                View.MeasureSpec.makeMeasureSpec(viewW, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(viewH, View.MeasureSpec.EXACTLY)
            )
            layout(0, 0, viewW, viewH)
        }
        val bitmap = Bitmap.createBitmap(viewW, viewH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        vista.draw(canvas)
        binding.ivModelo.setImageBitmap(bitmap)
    }
}

data class Serie(val nombre: String, val medida: String, val zocalo: String)

/**
 * Medida estructurada de un perfil. `corte` describe un corte especial
 * (p. ej. "un lado en 4ª") y se conserva para usos posteriores; esta
 * actividad no lo muestra al usuario.
 */
data class MedidaPerfil(
    val medida: Float,
    val cantidad: Int,
    val corte: String? = null
)

val listaSeries = listOf(
    Serie("Clásica serie 84 eco.", "3", "8"),
    Serie("Clásica serie 84", "3.5", "8"),
    Serie("Serie 20", "7", "8."),
    Serie("Serie 3825", "3.9", "8."),
    Serie("Serie 25", "4.4", "8"),
    Serie("Serie 62 europea", "8", "8")
)


//serie 20 2 hojas
// vidrio = anccho/2 -5 x alto - 10
//zócalo y cabezal = ancho/2 - 6.4
//riel sup e inf = ancho- 1.2
//parante, traslape = alto - 2.7