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

    // Serie 84: diseño hecho a mano en DialogoDiseno84 (tramos, hojas y franjas), en la forma que
    // lee Calculo84.leerDiseno. Vacío = automático. Cambiar la medida lo devuelve al automático,
    // como en la mampara paflón: describe otra ventana.
    private var diseno84 = ""

    // Metadatos de producción (se piden al archivar, como en Nova).
    private var metaColorAluminio = ""
    private var metaTipoVidrio = ""

    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()

    private var primerClickArchivarRealizado = false
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback
    private var cliente: String = ""

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

        // Fila de depuración (mostraba números sueltos con el rótulo "Vidrios"); no se archiva.
        binding.lyPruebas.visibility = View.GONE

        // Serie 84: cambiar la medida, las divisiones o el alto de hoja invalida el diseño hecho a
        // mano (describe otra ventana); el automático vuelve a mandar, igual que en la mampara.
        listOf(binding.etAncho, binding.etAlto, binding.etPartes, binding.etAltohoja).forEach { campo ->
            campo.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun afterTextChanged(s: android.text.Editable?) {
                    if (diseno84.isNotBlank()) {
                        diseno84 = ""
                        Toast.makeText(this@VentanaAl, "Cambió la medida: el diseño vuelve al automático.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }

        binding.btnCalcularE.setOnClickListener {
            controladorCola.onCalcular()
            if (!calcular()) {
                Toast.makeText(this, "Escribe el ancho y el alto", Toast.LENGTH_SHORT).show()
            }
        }
        // La navegación por la lista de ventanas ahora es con clic en el título (txVentana).
        // Con medidas escritas se recalcula: si no, quedaban a la vista (y se archivaban) los
        // materiales de la serie anterior bajo el nombre de la nueva.
        binding.txVentana.setOnClickListener {
            actualizarVentana()
            if (!calcular()) renderizarPreviewEstructurada()
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
        val marcoVal = num(binding.etMarco) ?: 2.2f
        val hoja = altoHojaParaDescriptor(alto)
        val nMo = num(binding.etMocheta)?.toInt() ?: 0
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
    // Trunca a un decimal, como todas las calculadoras (N-10). La de antes redondeaba: un vidrio de
    // 54.66 salía 54.7, un milímetro más largo.
    private fun df1(defo: Float): String = NovaCalculos.df1(defo)

    // FUNCIONES ALUMINIOS

    /** Calcula la serie elegida. False si falta el ancho o el alto (no toca nada). */
    @SuppressLint("SetTextI18n")
    private fun calcular(): Boolean {
        if ((num(binding.etAncho) ?: 0f) <= 0f || (num(binding.etAlto) ?: 0f) <= 0f) return false
        if (esSerieCorrediza()) return calcularSerieCorrediza()
        calcularSerieClasica()
        return true
    }

    // Cálculo de materiales de las series clásicas (84 / 3825 / etc.). Se reutiliza desde el botón
    // Calcular y desde el diálogo Con/Sin marco, para que el toggle recalcule los materiales y no
    // solo la vista.
    @SuppressLint("SetTextI18n")
    private fun calcularSerieClasica() {
        if (serieActual?.nombre == "Serie 3825") { calcularSerie3825(); return }
        val r = calculo84() ?: return
        restaurarLayoutClasicas()
        fun texto(l: List<String>) = l.joinToString("\n")
        binding.txParante.text = "Pierna"   // se llama pierna (no confundir con el parante de tramos)
        binding.tvMarco.text = texto(r.marco)
        binding.tvRielS.text = texto(r.rielSup)
        binding.tvRielI.text = texto(r.rielInf)
        binding.tvParante.text = texto(r.pierna)
        binding.tvZocalo.text = texto(r.zocalo)
        binding.tvTope.text = texto(r.tope)
        // Puente, separadores de mocheta y parante de tramo: el mismo tubo de 2.5. Sin puente
        // (G-06) solo queda el parante de tramo, si hay más de un tramo.
        binding.tvTubo.text = texto(r.tubo)
        binding.tvJunki.text = texto(r.junquillo)
        binding.tvVidriosR.text = texto(r.vidrios)
        binding.tvRiel.text = ""
        binding.tvJamba.text = ""
        binding.tvCabezal.text = ""
        binding.tvTraslapo.text = ""
        binding.tvAdaptador.text = ""

        binding.tvReferencias.text = "Ancho = ${binding.etAncho.text}, Alto = ${binding.etAlto.text}\n" +
                "Div=${r.divisiones} -> Fjs=${r.nFijos} -> Crzas=${r.nCorredizas}\n" +
                referenciasFranjas84(r) +
                (if (r.tramos.size > 1) "\nMódulos: " + r.tramos.mapIndexed { i, p -> "T${i + 1}:$p" }.joinToString("  ") else "") +
                (if (r.manual) "\nDiseño a mano" else "")
        r.aviso?.let { Toast.makeText(this, it, Toast.LENGTH_LONG).show() }

        // Ocultar layouts si sus TextViews están vacíos
        actualizarVisibilidadLayouts()
        renderizarPreviewEstructurada()
    }

    /** Serie 84 y 84 económica con lo escrito en las casillas. Null si falta el ancho o el alto. */
    private fun calculo84(): Calculo84.Resultado? = calculo84Entrada()?.let(Calculo84::calcular)

    private fun calculo84Entrada(): Calculo84.Entrada? {
        val ancho = num(binding.etAncho)?.takeIf { it > 0f } ?: return null
        val alto = num(binding.etAlto)?.takeIf { it > 0f } ?: return null
        return (
            Calculo84.Entrada(
                ancho = ancho, alto = alto, marco = marcoEfectivo(),
                altoHoja = num(binding.etAltohoja) ?: 0f,
                divisiones = num(binding.etPartes)?.toInt() ?: 0,
                junquillo = num(binding.etJunki) ?: 0f,
                mochetas = num(binding.etMocheta)?.toInt() ?: 0,
                pierna = serieActual?.medida?.toFloatOrNull() ?: 3.5f,
                diseno = diseno84
            )
        )
    }

    /**
     * Alto de hoja y mochetas para Referencias. Con un solo modelo de franjas (lo de siempre) se
     * escribe como antes; si los tramos difieren, uno por tramo.
     */
    private fun referenciasFranjas84(r: Calculo84.Resultado): String {
        fun deTramo(g: Calculo84.TramoResuelto): String =
            if (!g.llevaPuente) "Sin puente"
            else "hHoja = ${df1(g.altoHoja)}" +
                g.franjas.filter { !it.esSistema }.let { m ->
                    if (g.franjas.size == 2 && g.franjas[0].esSistema) "" else " (mochetas: ${m.joinToString(", ") { df1(it.alto ?: 0f) }})"
                }
        val textos = r.geometria.map(::deTramo)
        return if (textos.distinct().size == 1) textos[0]
        else textos.mapIndexed { i, t -> "T${i + 1}: $t" }.joinToString("\n")
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
    // comas); en modo simple, el patrón único; en la 84, su diseño a mano (vacío = automático); en
    // las demás series, patron20.
    private fun patronParaDescriptor(): String = when {
        serieActual?.nombre == "Serie 3825" && patron20.equals("tramos", ignoreCase = true) ->
            tramosEfectivos3825().joinToString(",")
        esSerie84() -> diseno84
        else -> patron20
    }

    private fun esSerie84(): Boolean = serieActual?.nombre?.contains("84") == true

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
            // El tope de la 84 se veía en pantalla y no se archivaba; las series corredizas usan
            // también esta fila.
            if (esValido(binding.lyTope)) {
                ListaCasilla.procesarArchivarConPrefijo(this, binding.txTope, binding.tvTope, mapListas, identificadorPaquete)
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
        // Las etiquetas son el nombre de la lista con que se archiva: si quedaba la de otra serie
        // (la 62 pone "Parante" en la fila del zócalo, la 3825 "Tubo puente"), el zócalo de la 84
        // se archivaba como parante.
        binding.txMarco.setText(crystal.crystal.R.string.marco)
        binding.txParante.setText(crystal.crystal.R.string.parante)
        binding.txZocalo.setText(crystal.crystal.R.string.z_calo)
        binding.txRiel.setText(crystal.crystal.R.string.riel)
        binding.txRielS.setText(crystal.crystal.R.string.riel_sup)
        binding.txRielI.setText(crystal.crystal.R.string.riel_inf)
        binding.txJunki.setText(crystal.crystal.R.string.junkillo)
        binding.txTope.setText(crystal.crystal.R.string.ang_tope)
        binding.txTubo.setText(crystal.crystal.R.string.tubo)
        binding.txJamba.setText(crystal.crystal.R.string.jamba)
        binding.txTraslape.setText(crystal.crystal.R.string.traslapo)
        binding.txCabezal.setText(crystal.crystal.R.string.cabezal)
        binding.txAdaptador.setText(crystal.crystal.R.string.adaptador)
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
        binding.lyTubo.visibility = if (binding.tvTubo.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyCabezal.visibility = if (binding.tvCabezal.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyTraslapo.visibility = if (binding.tvTraslapo.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyAdaptador.visibility = if (binding.tvAdaptador.text.isNullOrBlank()) View.GONE else View.VISIBLE
        binding.lyVidrios.visibility = View.VISIBLE
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
        return num(binding.etMarco) ?: 2.2f
    }

    private fun anchUtil(): Float = (num(binding.etAncho) ?: 0f) - 2 * marcoEfectivo()

    private fun altoUtil(): Float = (num(binding.etAlto) ?: 0f) - 2 * marcoEfectivo()

    private fun divMocheta(p1: Float): Float {
        val n = NovaCalculos.anchMota(p1)          // nº de submódulos = ceil(p1/180), mín 1
        return (p1 - (n - 1) * 2.5f) / n            // ancho útil, restando el tubo (2.5) entre submódulos
    }

    // Alto de hoja y divisiones con la misma regla que la serie 84 (Calculo84): hoja 0 = 5/7 del
    // alto útil y nunca mayor que él; divisiones 0 = regla de los 60.
    private fun altoHoja(): Float = Calculo84.altoHoja(altoUtil(), num(binding.etAltohoja) ?: 0f)

    private fun divisiones(): Int =
        Calculo84.divisiones(num(binding.etAncho) ?: 0f, num(binding.etPartes)?.toInt() ?: 0)

    /** Número escrito en un campo (acepta coma decimal); null si está vacío o no es número. */
    private fun num(et: EditText): Float? = et.text.toString().trim().replace(",", ".").toFloatOrNull()

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            // Solo incluir perfiles cuyos layouts sean visibles
            val perfiles = mutableMapOf<String, String>()
            // Con el rótulo de cada fila, que es el nombre con que se archiva (antes la pierna de la
            // 84 volvía como "Parante" y el marco no volvía).
            fun agregar(ly: LinearLayout, tx: android.widget.TextView, tv: android.widget.TextView) {
                if (esValido(ly)) perfiles[tx.text.toString()] = ModoMasivoHelper.texto(tv)
            }
            agregar(binding.lyMarco, binding.txMarco, binding.tvMarco)
            agregar(binding.lyParante, binding.txParante, binding.tvParante)
            agregar(binding.lyZocalo, binding.txZocalo, binding.tvZocalo)
            agregar(binding.lyRiel, binding.txRiel, binding.tvRiel)
            agregar(binding.lyRielS, binding.txRielS, binding.tvRielS)
            agregar(binding.lyRielI, binding.txRielI, binding.tvRielI)
            agregar(binding.lyJamba, binding.txJamba, binding.tvJamba)
            agregar(binding.lyTubo, binding.txTubo, binding.tvTubo)
            agregar(binding.lyCabezal, binding.txCabezal, binding.tvCabezal)
            agregar(binding.lyTraslapo, binding.txTraslape, binding.tvTraslapo)
            agregar(binding.lyAdaptador, binding.txAdaptador, binding.tvAdaptador)

            val accesorios = mutableMapOf<String, String>()
            if (esSerieCorrediza()) {
                // En las corredizas estas dos filas llevan otros perfiles, con su nombre.
                agregar(binding.lyTope, binding.txTope, binding.tvTope)
                agregar(binding.lyJunki, binding.txJunki, binding.tvJunki)
            } else {
                if (esValido(binding.lyTope)) accesorios["Tope"] = ModoMasivoHelper.texto(binding.tvTope)
                if (esValido(binding.lyJunki)) accesorios["Junquillo"] = ModoMasivoHelper.texto(binding.tvJunki)
            }

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
        box.addView(android.widget.TextView(this).apply { text = VentanaAlRender.AVISO_ZOOM })
        patrones.forEachIndexed { i, pat ->
            if (patrones.size > 1) box.addView(android.widget.TextView(this).apply {
                text = "Tramo ${i + 1}: $pat"
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                setPadding(0, (8 * dens).toInt(), 0, (2 * dens).toInt())
            })
            val resId = VentanaAlRender.imagenOrdenModulos(this, serie, pat)
            if (resId != 0) box.addView(VentanaAlRender.vistaImagenOrden(this, resId)) else box.addView(android.widget.TextView(this).apply { text = "(sin imagen para $pat)" })
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
                    // Con medidas se recalcula: si no, quedaban (y se archivaban) los materiales del
                    // modelo anterior junto al dibujo del nuevo.
                    if (!calcular()) renderizarPreviewEstructurada()
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
            // Serie 84: tramos, hojas y franjas, como en la mampara paflón. Sin medidas no hay qué
            // repartir: solo la cantidad.
            else -> {
                val entrada = calculo84Entrada()
                if (entrada == null) {
                    Toast.makeText(this, "Escribe el ancho y el alto para diseñar los tramos y franjas", Toast.LENGTH_SHORT).show()
                    crystal.crystal.taller.OpcionesUI.mostrar(
                        this, "Opciones", cantidadProducto
                    ) { cant -> cantidadProducto = cant }
                } else {
                    DialogoDiseno84.mostrar(this, entrada, cantidadProducto) { diseno, cant ->
                        diseno84 = diseno
                        cantidadProducto = cant
                        calcular()
                    }
                }
            }
        }
    }

    /** Texto de referencia según la opción del diálogo: divisiones si es "tramos", o el patrón. */
    private fun refOpcion(): String =
        if (patron20.equals("tramos", ignoreCase = true)) "Divisiones = ${divisiones()}"
        else "Patrón = $patron20"

    /** Filas de material de la pantalla, en el orden en que se llenan con las series corredizas. */
    private fun filasMaterial(): List<Triple<LinearLayout, android.widget.TextView, android.widget.TextView>> = listOf(
        Triple(binding.lyMarco, binding.txMarco, binding.tvMarco),
        Triple(binding.lyParante, binding.txParante, binding.tvParante),
        Triple(binding.lyZocalo, binding.txZocalo, binding.tvZocalo),
        Triple(binding.lyRiel, binding.txRiel, binding.tvRiel),
        Triple(binding.lyRielS, binding.txRielS, binding.tvRielS),
        Triple(binding.lyRielI, binding.txRielI, binding.tvRielI),
        Triple(binding.lyJamba, binding.txJamba, binding.tvJamba),
        Triple(binding.lyTubo, binding.txTubo, binding.tvTubo),
        Triple(binding.lyCabezal, binding.txCabezal, binding.tvCabezal),
        Triple(binding.lyTraslapo, binding.txTraslape, binding.tvTraslapo),
        Triple(binding.lyAdaptador, binding.txAdaptador, binding.tvAdaptador),
        Triple(binding.lyTope, binding.txTope, binding.tvTope),
        Triple(binding.lyJunki, binding.txJunki, binding.tvJunki)
    )

    private fun esSerieCorrediza(): Boolean = CalculoCorredizas.serie(serieActual?.nombre ?: "") != null

    /**
     * Series 20, 25, 42, 46, 62 y 80 (`CalculoCorredizas`, con las hojas del proveedor). Cada lista va
     * en una fila, con su nombre de rótulo (es el nombre con que se archiva). Si el modelo elegido no
     * existe en esta serie (se eligió en otra), vuelve a "tramos".
     */
    @SuppressLint("SetTextI18n")
    private fun calcularSerieCorrediza(): Boolean {
        val serie = serieActual?.nombre ?: return false
        val a = num(binding.etAncho) ?: return false
        val h = num(binding.etAlto) ?: return false
        if (patron20 !in CalculoCorredizas.patrones(serie)) {
            Toast.makeText(this, "La $serie no tiene el modelo $patron20: se reparte por tramos.", Toast.LENGTH_SHORT).show()
            patron20 = "tramos"
        }
        val r = CalculoCorredizas.calcular(serie, a, h, patron20, divisiones()) ?: return false
        restaurarLayoutClasicas()
        val filas = filasMaterial()
        filas.forEachIndexed { i, (ly, tx, tv) ->
            val lista = r.listas.getOrNull(i)
            tx.text = lista?.first ?: ""
            tv.text = lista?.second?.joinToString("\n") ?: ""
            ly.visibility = if (lista != null) View.VISIBLE else View.GONE
        }
        (binding.lyMarco.parent as? LinearLayout)?.let { cont ->
            filas.forEachIndexed { i, (ly, _, _) ->
                cont.removeView(ly)
                cont.addView(ly, i)
            }
        }
        binding.tvVidriosR.text = r.vidrios.joinToString("\n")
        binding.lyVidrios.visibility = View.VISIBLE
        binding.tvReferencias.text = "Ancho = ${binding.etAncho.text}, Alto = ${binding.etAlto.text}\n" +
                "$serie -> ${refOpcion()}" +
                "\nMódulos: " + r.tramos.mapIndexed { i, p -> "T${i + 1}:$p" }.joinToString("  ")
        renderizarPreviewEstructurada()
        return true
    }

    private fun renderizarPreviewEstructurada() {
        val ancho = binding.etAncho.text?.toString()?.replace(",", ".")?.toFloatOrNull() ?: 0f
        val alto = binding.etAlto.text?.toString()?.replace(",", ".")?.toFloatOrNull() ?: 0f
        if (ancho <= 0f || alto <= 0f) return

        // Serie 84 / 84 eco.: diseño propio (marco en todo el contorno, puente y módulos
        // con bastidor completo). El resto de series usa el preview simbólico (estructurada).
        val serieNombre = serieActual?.nombre ?: ""
        if (VentanaAlRender.soportaSerie(serieNombre)) {
            val marcoVal = num(binding.etMarco) ?: 2.2f
            val hoja = altoHojaParaDescriptor(alto)
            val nMo = num(binding.etMocheta)?.toInt() ?: 0
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

val listaSeries = listOf(
    Serie("Clásica serie 84 eco.", "3", "8"),
    Serie("Clásica serie 84", "3.5", "8"),
    Serie("Serie 20", "7", "8."),
    Serie("Serie 3825", "3.9", "8."),
    Serie("Serie 25", "4.4", "8"),
    Serie("Serie 62 europea", "8", "8"),
    // Hojas del proveedor del 2026-10-16 (CalculoCorredizas): VL42 / ML42, VL46 / ML46 y ML48.
    Serie("Serie 42", "0", "0"),
    Serie("Serie 42 mampara", "0", "0"),
    Serie("Serie 46", "0", "0"),
    Serie("Serie 46 mampara", "0", "0"),
    Serie("Serie 80", "0", "0")
)


//serie 20 2 hojas
// vidrio = anccho/2 -5 x alto - 10
//zócalo y cabezal = ancho/2 - 6.4
//riel sup e inf = ancho- 1.2
//parante, traslape = alto - 2.7