package crystal.crystal.taller.melamina

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityRoperoBinding
import crystal.crystal.optimizadores.planchas.OptimizacionPlanchasActivity
import crystal.crystal.taller.ColaCalculadoras
import crystal.crystal.taller.ControladorColaMedidas
import crystal.crystal.taller.ModoMasivoHelper
import org.json.JSONArray
import org.json.JSONObject

/**
 * Ropero empotrado de melamina. Misma pantalla y mismo flujo que las demás calculadoras del
 * taller: el hueco y sus opciones a la izquierda con la ficha dibujada arriba, "Referencias y
 * Cálculos" a la derecha con una fila por material (`medida = cantidad`), y abajo Archivar y
 * Calcular. Lo que la distingue: el dibujo se toca para decir qué lleva cada cuerpo.
 */
class RoperoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoperoBinding
    private lateinit var controladorCola: ControladorColaMedidas
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var primerClickArchivarRealizado = false

    /** El ropero que se está armando; los cuerpos viven aquí, no en las casillas. */
    private var ropero = Ropero()

    /** La pantalla de diseño devuelve el ropero afinado; se toma tal cual y se calcula. */
    private val lanzarDiseno = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == RESULT_OK) {
            val nuevo = Ropero.desdeJson(res.data?.getStringExtra(DisenoRoperoActivity.RESULT_ROPERO)) ?: return@registerForActivityResult
            ropero = nuevo
            volcarEnPantalla()
            calcular()
        }
    }
    private var materiales: MaterialesRopero? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoperoBinding.inflate(layoutInflater)
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
        configurarPlanchasYCompartir()
        configurarCliente()

        // El último ropero que se armó, para seguir donde se quedó.
        Ropero.desdeJson(getSharedPreferences(PREFS, MODE_PRIVATE).getString(CLAVE_ULTIMO, null))?.let { ropero = it }
        volcarEnPantalla()

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAlto.setText(df1(it)) }

        controladorCola = ControladorColaMedidas(
            activity = this,
            claseActual = RoperoActivity::class.java,
            etAncho = binding.etAncho,
            etAlto = binding.etAlto,
            formato = ::df1
        )
        controladorCola.inicializar()
        cargarRoperoDeLaMedida()
    }

    /**
     * La medida que llega de MedidaActivity puede traer el ropero entero (diseñado en el apunte con
     * su plantilla): se toma tal cual y se calcula, en vez de quedarse con el ancho y el alto.
     */
    private fun cargarRoperoDeLaMedida() {
        // Abierto desde Productos para editar un ropero archivado: viene entero, como se guardó.
        crystal.crystal.casilla.EdicionProducto.disenoDe(this)?.let { Ropero.desdeJson(it) }?.let { archivado ->
            ropero = archivado
            volcarEnPantalla()
            calcular()
            return
        }
        if (!ColaCalculadoras.desdeMedidas(this)) return
        val json = ColaCalculadoras.cola(this).getOrNull(ColaCalculadoras.indice(this))?.disenoRopero
        val deLaMedida = Ropero.desdeJson(json) ?: return
        ropero = deLaMedida
        volcarEnPantalla()
        calcular()
    }

    // ==================== OBTENER VALORES ====================

    private data class EntradaCalculo(
        val ancho: Float,
        val alto: Float,
        val fondo: Float,
        val cuerpos: Int,
        val zocalo: Float,
        val maletero: Float,
        val puertas: TipoPuertas,
        val espesorMm: Int,
        val conFondo: Boolean
    )

    private fun leerEntradaCalculo(): EntradaCalculo = EntradaCalculo(
        ancho = binding.etAncho.text.toString().toFloatOrNull() ?: 0f,
        alto = binding.etAlto.text.toString().toFloatOrNull() ?: 0f,
        fondo = binding.etFondo.text.toString().toFloatOrNull() ?: 0f,
        cuerpos = binding.etCuerpos.text.toString().toIntOrNull() ?: 0,
        zocalo = binding.etZocalo.text.toString().toFloatOrNull() ?: 0f,
        maletero = binding.etMaletero.text.toString().toFloatOrNull() ?: 0f,
        puertas = TipoPuertas.values()[binding.spPuertas.selectedItemPosition.coerceIn(0, TipoPuertas.values().lastIndex)],
        espesorMm = if (binding.spEspesor.selectedItemPosition == 1) 15 else 18,
        conFondo = binding.cbFondo.isChecked
    )

    /** Mientras se vuelcan las casillas no hay que redibujar a medio camino. */
    private var cargando = false

    /** Las casillas a partir del ropero: al abrir, y al volver de la cola de medidas. */
    private fun volcarEnPantalla() {
        cargando = true
        binding.etAncho.setText(df1(ropero.anchoCm))
        binding.etAlto.setText(df1(ropero.altoCm))
        binding.etFondo.setText(df1(ropero.fondoCm))
        binding.etCuerpos.setText(ropero.cuerpos.size.toString())
        binding.etZocalo.setText(df1(ropero.zocaloCm))
        binding.etMaletero.setText(if (ropero.maleteroCm > 0f) df1(ropero.maleteroCm) else "")
        binding.spPuertas.setSelection(TipoPuertas.values().indexOf(ropero.puertas))
        binding.spEspesor.setSelection(if (ropero.espesorMm <= 15) 1 else 0)
        binding.cbFondo.isChecked = ropero.conFondo
        binding.vistaRopero.ropero = ropero
        cargando = false
    }

    /**
     * Rehace el ropero con lo que hay en las casillas y lo redibuja; si las casillas están a
     * medias (vacías, en cero) se deja como está, sin avisos: es lo que pasa mientras se escribe.
     */
    private fun redibujarDesdeCasillas() {
        val nuevo = roperoDesdeCasillas() ?: return
        ropero = nuevo
        binding.vistaRopero.ropero = ropero
    }

    /** El ropero que dicen las casillas, o null si todavía no dicen un hueco válido. */
    private fun roperoDesdeCasillas(): Ropero? {
        val entrada = leerEntradaCalculo()
        if (entrada.ancho < 30f || entrada.alto < 30f || entrada.fondo < 20f || entrada.cuerpos <= 0) return null
        var nuevo = ropero.copy(
            zocaloCm = entrada.zocalo.coerceIn(0f, 30f),
            maleteroCm = entrada.maletero.coerceIn(0f, 120f),
            puertas = entrada.puertas,
            espesorMm = entrada.espesorMm,
            conFondo = entrada.conFondo
        )
        val huecoCambio = entrada.ancho != nuevo.anchoCm || entrada.alto != nuevo.altoCm ||
            entrada.fondo != nuevo.fondoCm || entrada.espesorMm != ropero.espesorMm
        if (huecoCambio) nuevo = nuevo.conHueco(entrada.ancho, entrada.alto, entrada.fondo)
        if (entrada.cuerpos != nuevo.cuerpos.size) nuevo = nuevo.conCuerposIguales(entrada.cuerpos)
        return nuevo
    }

    private fun refrescarProyectoActivoUI() {
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
    }

    // ==================== OPCIONES Y DIBUJO ====================

    private fun configurarOpciones() {
        binding.spPuertas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, TipoPuertas.values().map { it.etiqueta })
        binding.spEspesor.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("18 mm", "15 mm"))
    }

    private fun configurarDibujo() {
        binding.vistaRopero.alTocarCuerpo = { i -> editarCuerpo(i) }
        binding.btVerInterior.setOnClickListener { binding.vistaRopero.mostrarPuertas = false; binding.vistaRopero.en3d = false }
        binding.btVerPuertas.setOnClickListener { binding.vistaRopero.mostrarPuertas = true; binding.vistaRopero.en3d = false }
        binding.btVer3d.setOnClickListener { binding.vistaRopero.mostrarPuertas = false; binding.vistaRopero.en3d = true }
        // Producción: corte con código y marcas y agujeros de cada tablero, con buscador y CSV.
        binding.btPiezas.setOnClickListener {
            ProduccionRoperoActivity.abrir(this, ropero, intent.getStringExtra(crystal.crystal.casilla.EdicionProducto.EXTRA_ID).orEmpty())
        }
        // El plano técnico: conjunto con cotas, despiece con cantos y veta, y lista de corte; a PDF.
        binding.btPlano.setOnClickListener {
            PlanoRoperoActivity.abrir(
                this, ropero,
                codigo = intent.getStringExtra(crystal.crystal.casilla.EdicionProducto.EXTRA_ID).orEmpty(),
                proyecto = ProyectoManager.getProyectoActivo().orEmpty(),
                cliente = intent.extras?.getString("rcliente").orEmpty()
            )
        }
        // El dibujo sigue a las casillas: al cambiar el hueco, los cuerpos, el zócalo, el maletero
        // o las puertas se rehace solo, sin esperar a Calcular (que es el que saca los materiales).
        val alEscribir = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) = Unit
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) = Unit
            override fun afterTextChanged(s: android.text.Editable?) { if (!cargando) redibujarDesdeCasillas() }
        }
        listOf(binding.etAncho, binding.etAlto, binding.etFondo, binding.etCuerpos, binding.etZocalo, binding.etMaletero).forEach { it.addTextChangedListener(alEscribir) }
        val alElegir = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) { if (!cargando) redibujarDesdeCasillas() }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        binding.spPuertas.onItemSelectedListener = alElegir
        binding.spEspesor.onItemSelectedListener = alElegir
        binding.cbFondo.setOnCheckedChangeListener { _, _ -> if (!cargando) redibujarDesdeCasillas() }
    }

    /**
     * El cuerpo tocado: qué lleva, cuántos entrepaños o cajones, y su ancho. Cambiar el ancho
     * reparte lo que sobre o falte entre los demás cuerpos. Al aceptar se recalcula todo.
     */
    private fun editarCuerpo(indice: Int) {
        val c = ropero.cuerpos.getOrNull(indice) ?: return
        binding.vistaRopero.cuerpoResaltado = indice
        val dp = resources.displayMetrics.density
        val spTipo = Spinner(this).apply {
            adapter = ArrayAdapter(this@RoperoActivity, android.R.layout.simple_spinner_dropdown_item, TipoCuerpo.values().map { it.etiqueta })
            setSelection(TipoCuerpo.values().indexOf(c.tipo))
        }
        fun campo(rotulo: String, valor: String, decimal: Boolean) = EditText(this).apply {
            hint = rotulo
            inputType = if (decimal) InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL else InputType.TYPE_CLASS_NUMBER
            setText(valor)
            setSelectAllOnFocus(true)
        }
        val etAnchoCuerpo = campo("Ancho interior del cuerpo (cm)", df1(c.anchoCm), true)
        val etEntrepanos = campo("Casilleros (repisas)", c.entrepanos.toString(), false)
        val etCajones = campo("Cajones", c.cajones.toString(), false)
        val etAltoCajon = campo("Alto de cada cajón (cm)", df1(ropero.altoCajonCm), true)
        val caja = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((20 * dp).toInt(), (8 * dp).toInt(), (20 * dp).toInt(), 0)
            addView(TextView(this@RoperoActivity).apply { text = "Qué lleva"; textSize = 12f })
            addView(spTipo); addView(etAnchoCuerpo); addView(etEntrepanos); addView(etCajones); addView(etAltoCajon)
        }
        val quitarResalte = { binding.vistaRopero.cuerpoResaltado = -1 }
        AlertDialog.Builder(this)
            .setTitle("Cuerpo ${indice + 1}")
            .setView(caja)
            .setPositiveButton("Aceptar") { _, _ ->
                val tipo = TipoCuerpo.values()[spTipo.selectedItemPosition.coerceIn(0, TipoCuerpo.values().lastIndex)]
                val nuevo = Cuerpo(
                    anchoCm = c.anchoCm,
                    tipo = tipo,
                    entrepanos = etEntrepanos.text.toString().toIntOrNull() ?: 0,
                    cajones = etCajones.text.toString().toIntOrNull() ?: 0
                )
                ropero = ropero.conCuerpo(indice, nuevo)
                    .copy(altoCajonCm = (etAltoCajon.text.toString().toFloatOrNull() ?: ropero.altoCajonCm).coerceIn(8f, 60f))
                val ancho = etAnchoCuerpo.text.toString().toFloatOrNull() ?: c.anchoCm
                if (kotlin.math.abs(ancho - c.anchoCm) > 0.05f) ropero = ropero.conAnchoDeCuerpo(indice, ancho)
                quitarResalte()
                calcular()
            }
            .setNegativeButton("Cancelar") { _, _ -> quitarResalte() }
            .setOnCancelListener { quitarResalte() }
            .show()
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

    /**
     * Arma el ropero con las casillas y saca sus materiales. Si cambió el hueco o la cantidad de
     * cuerpos, los cuerpos se reparten de nuevo; si no, se respeta lo que se tocó en el dibujo.
     */
    private fun calcular(): Boolean {
        val nuevo = roperoDesdeCasillas()
        if (nuevo == null) {
            Toast.makeText(this, "Ingrese datos válidos", Toast.LENGTH_SHORT).show()
            return false
        }
        ropero = nuevo
        binding.vistaRopero.ropero = ropero

        val m = RoperoCalculo.calcular(ropero)
        materiales = m
        mostrarResultados(m)
        mostrarReferencias(m)
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(CLAVE_ULTIMO, ropero.aJson()).apply()
        return true
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarResultados(m: MaterialesRopero) {
        val melamina = RoperoCalculo.melaminaInterior(ropero)
        val melColor = RoperoCalculo.melaminaVisible(ropero)
        binding.tvMelamina.text = if (ropero.interiorBlanco) "${melamina.etiqueta} blanca" else melamina.etiqueta
        binding.txMelamina.text = m.lineasDePiezas(melamina)
        binding.tvMelaminaColor.text = melColor.etiqueta
        ponerFila(binding.lyMelaminaColor, binding.txMelaminaColor, if (melColor == melamina) "" else m.lineasDePiezas(melColor))
        // El MDF o nordex de 3: el fondo del mueble (si es de 3) y los fondos de los cajones, juntos.
        binding.tvNordex.text = MaterialPlancha.NORDEX_3.etiqueta
        ponerFila(binding.lyNordex, binding.txNordex, m.lineasDePiezas(MaterialPlancha.NORDEX_3))
        // Si el fondo del mueble va de 5.5, en su fila.
        binding.tvMdf.text = MaterialPlancha.MDF_55.etiqueta
        ponerFila(binding.lyMdf, binding.txMdf, m.lineasDePiezas(MaterialPlancha.MDF_55))
        binding.tvTapacanto.text = RoperoCalculo.nombreTapacanto(ropero)
        binding.txTapacanto.text = m.lineasDeTapacanto()
        binding.tvTapacantoColor.text = RoperoCalculo.nombreTapacantoColor(ropero)
        ponerFila(binding.lyTapacantoColor, binding.txTapacantoColor, m.lineasDeTapacantoColor())
        binding.tvTapacantoPuertas.text = RoperoCalculo.nombreTapacantoPuertas(ropero)
        ponerFila(binding.lyTapacantoPuertas, binding.txTapacantoPuertas, m.lineasDeTapacantoPuertas())
        ponerFila(binding.lyTubo, binding.txTubo, m.lineasConLargo("Tubo colgador"))
        ponerFila(binding.lyRiel, binding.txRiel, m.lineasConLargo("Riel corredizo"))
        binding.txAccesorios.text = m.lineasDeAccesorios()
        binding.txCorte.text = m.listaDeCorte()
    }

    /** Una fila de material que solo se ve cuando tiene algo (el tubo, el riel corredizo). */
    private fun ponerFila(fila: View, texto: TextView, lineas: String) {
        texto.text = lineas
        fila.visibility = if (lineas.isBlank()) View.GONE else View.VISIBLE
    }

    private fun mostrarReferencias(m: MaterialesRopero) {
        binding.txReferencias.text = m.referencias
    }

    // ==================== ARCHIVAR ====================

    private fun callbackSeleccionProyectoParaArchivar(): DialogosProyecto.ProyectoCallback {
        return object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                val mapExistente = MapStorage.cargarProyecto(this@RoperoActivity, nombreProyecto)
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
        // Antes de archivar, lo que necesita la producción: los colores y si llevan veta.
        pedirColores { archivarMapas(); despuesDeArchivar() }
    }

    /**
     * Los colores del ropero para la hoja de corte: la melamina de fuera y la de dentro, si cada
     * una tiene veta (el cedro sí; el blanco, el azul o el celeste no), y el tapacanto de fuera,
     * que si no se escribe es del color de la melamina de fuera.
     */
    private fun pedirColores(alContinuar: () -> Unit) {
        val pad = (16 * resources.displayMetrics.density).toInt()
        fun campo(pista: String, valor: String) = EditText(this).apply { hint = pista; setText(valor); setSingleLine() }
        fun marca(t: String, v: Boolean) = android.widget.CheckBox(this).apply { text = t; isChecked = v }
        val etFuera = campo("Color de la melamina de fuera (ej: Cedro)", ropero.colorExterior)
        val cbVetaFuera = marca("La de fuera tiene veta", ropero.vetaExterior)
        val etCanto = campo("Color del tapacanto de fuera (vacío = el mismo)", ropero.colorTapacanto)
        val etDentro = campo("Color de la melamina de dentro", ropero.colorInterior)
        val cbVetaDentro = marca("La de dentro tiene veta", ropero.vetaInterior)
        val caja = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, 0)
            listOf(etFuera, cbVetaFuera, etCanto, etDentro, cbVetaDentro).forEach { addView(it) }
        }
        AlertDialog.Builder(this)
            .setTitle("Colores para producción")
            .setView(caja)
            .setPositiveButton("Guardar y archivar") { _, _ ->
                ropero = ropero.copy(
                    colorExterior = etFuera.text.toString().trim(),
                    vetaExterior = cbVetaFuera.isChecked,
                    colorTapacanto = etCanto.text.toString().trim(),
                    colorInterior = etDentro.text.toString().trim().ifBlank { "Blanco" },
                    vetaInterior = cbVetaDentro.isChecked
                )
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(CLAVE_ULTIMO, ropero.aJson()).apply()
                alContinuar()
            }
            .setNeutralButton("Omitir") { _, _ -> alContinuar() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Lo que sigue a archivar: el proyecto al día, las casillas limpias y la siguiente medida de la cola. */
    private fun despuesDeArchivar() {
        refrescarProyectoActivoUI()
        binding.etAncho.setText("")
        controladorCola.ofrecerSiguiente()
    }

    private fun archivarMapas() {
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        val cliente = intent.extras?.getString("rcliente").orEmpty()
        val paquete = ropero.aJson()
        fun fila(ly: View, tv: TextView, tx: TextView) = ListaCasilla.ItemArchivable(tv, tx, ly.visibility != View.GONE)
        val ultimoID = ListaCasilla.archivarEnProyectoActivo(
            context = this,
            mapListas = mapListas,
            prefijo = PREFIJO,
            cantidad = cant,
            referencias = fila(binding.lyReferencias, binding.tvReferencias, binding.txReferencias),
            items = listOf(
                fila(binding.lyMelamina, binding.tvMelamina, binding.txMelamina),
                fila(binding.lyMelaminaColor, binding.tvMelaminaColor, binding.txMelaminaColor),
                fila(binding.lyNordex, binding.tvNordex, binding.txNordex),
                fila(binding.lyMdf, binding.tvMdf, binding.txMdf),
                fila(binding.lyTapacanto, binding.tvTapacanto, binding.txTapacanto),
                fila(binding.lyTapacantoColor, binding.tvTapacantoColor, binding.txTapacantoColor),
                fila(binding.lyTapacantoPuertas, binding.tvTapacantoPuertas, binding.txTapacantoPuertas),
                fila(binding.lyTubo, binding.tvTubo, binding.txTubo),
                fila(binding.lyRiel, binding.tvRiel, binding.txRiel),
                fila(binding.lyAccesorios, binding.tvAccesorios, binding.txAccesorios)
            ),
            cliente = cliente.ifBlank { null },
            // El ropero entero viaja como paquete: la ficha lo redibuja desde ahí.
            paquetesPorNumero = { mapOf(CLAVE_DISENO to paquete) }
        )
        refrescarProyectoActivoUI()
        val msg = if (cant > 1) "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
                  else "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun devolverResultadoMasivo() {
        val perfiles = mapOf(
            ModoMasivoHelper.texto(binding.tvMelamina) to ModoMasivoHelper.texto(binding.txMelamina),
            ModoMasivoHelper.texto(binding.tvMelaminaColor) to ModoMasivoHelper.texto(binding.txMelaminaColor),
            ModoMasivoHelper.texto(binding.tvNordex) to ModoMasivoHelper.texto(binding.txNordex),
            ModoMasivoHelper.texto(binding.tvMdf) to ModoMasivoHelper.texto(binding.txMdf),
            ModoMasivoHelper.texto(binding.tvTapacanto) to ModoMasivoHelper.texto(binding.txTapacanto),
            ModoMasivoHelper.texto(binding.tvTapacantoColor) to ModoMasivoHelper.texto(binding.txTapacantoColor),
            ModoMasivoHelper.texto(binding.tvTapacantoPuertas) to ModoMasivoHelper.texto(binding.txTapacantoPuertas),
            ModoMasivoHelper.texto(binding.tvTubo) to ModoMasivoHelper.texto(binding.txTubo),
            ModoMasivoHelper.texto(binding.tvRiel) to ModoMasivoHelper.texto(binding.txRiel)
        ).filter { it.value.isNotBlank() }
        val accesorios = mapOf(
            "Accesorios melamina" to ModoMasivoHelper.texto(binding.txAccesorios)
        ).filter { it.value.isNotBlank() }
        ModoMasivoHelper.devolverResultado(
            activity = this,
            calculadora = "Ropero Melamina",
            perfiles = perfiles,
            vidrios = "",
            accesorios = accesorios,
            referencias = ModoMasivoHelper.texto(binding.txReferencias),
            disenoPaquete = ropero.aJson()
        )
    }

    // ==================== PLANCHAS Y COMPARTIR ====================

    private fun configurarPlanchasYCompartir() {
        // Como en Nova: la pulsación larga en la ficha abre el diseño.
        binding.vistaRopero.setOnLongClickListener { abrirDiseno(); true }
        binding.btPlanchas.setOnClickListener { mandarAlOptimizador() }
        binding.btCompartir.setOnClickListener { compartir() }
    }

    /** La pantalla de diseño, con el ropero como está en las casillas. */
    private fun abrirDiseno() {
        roperoDesdeCasillas()?.let { ropero = it }
        lanzarDiseno.launch(Intent(this, DisenoRoperoActivity::class.java).putExtra(DisenoRoperoActivity.EXTRA_ROPERO, ropero.aJson()))
    }

    /** Las piezas al optimizador de planchas, en cm y con su nombre, para el corte real. */
    private fun mandarAlOptimizador() {
        val m = materiales ?: run { Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show(); return }
        val arr = JSONArray()
        m.piezas.forEach { p ->
            arr.put(JSONObject().apply {
                put("a", p.anchoMm / 10.0); put("h", p.altoMm / 10.0); put("c", p.cantidad)
                put("r", "${p.nombre} · ${p.material.etiqueta}")
            })
        }
        startActivity(Intent(this, OptimizacionPlanchasActivity::class.java).putExtra("piezas_planchas_json", arr.toString()))
        Toast.makeText(this, "Piezas enviadas. Pon las planchas disponibles (244 x 183) y optimiza.", Toast.LENGTH_LONG).show()
    }

    private fun compartir() {
        val m = materiales ?: run { Toast.makeText(this, "Haz nuevo cálculo", Toast.LENGTH_SHORT).show(); return }
        val texto = buildString {
            append(m.referencias).append("\n\n")
            append("LISTA DE CORTE (cm)\n").append(m.listaDeCorte()).append("\n\n")
            append("TAPACANTO INTERIOR (cm = cantidad)\n").append(m.lineasDeTapacanto()).append("\n\n")
            if (m.tapacantoColor.isNotEmpty()) append("TAPACANTO FINO DE COLOR (cm = cantidad)\n").append(m.lineasDeTapacantoColor()).append("\n\n")
            if (m.tapacantoPuertas.isNotEmpty()) append("TAPACANTO PUERTAS (cm = cantidad)\n").append(m.lineasDeTapacantoPuertas()).append("\n\n")
            m.nombresConLargo().forEach { append(it.uppercase()).append(" (cm = cantidad)\n").append(m.lineasConLargo(it)).append("\n\n") }
            append("ACCESORIOS\n").append(m.lineasDeAccesorios())
        }
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Ropero de melamina")
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
        val clienteNombre = intent.extras?.getString("rcliente")
        val cliente = clienteNombre ?: ""
        val proyectoActual = ProyectoManager.getProyectoActivo() ?: ""
        if (proyectoActual.isNotEmpty()) {
            primerClickArchivarRealizado = true
        }
        if (cliente.isEmpty()) return
        if (proyectoActual.contains(cliente, ignoreCase = true)) return

        val callbackCliente = callbackProyectoCliente()

        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackCliente, cliente)
        } else {
            AlertDialog.Builder(this)
                .setTitle("Cliente: $cliente")
                .setMessage("Proyecto activo: \"$proyectoActual\".\n¿Qué deseas hacer?")
                .setPositiveButton("Mantener") { d, _ ->
                    primerClickArchivarRealizado = true
                    d.dismiss()
                }
                .setNegativeButton("Crear nuevo") { _, _ ->
                    DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackCliente, cliente)
                }
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

    private fun df1(v: Float): String {
        return if (v % 1 == 0f) v.toInt().toString()
        else "%.1f".format(v).replace(",", ".")
    }

    // ==================== BACK ====================

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        if (ModoMasivoHelper.esModoMasivo(this)) {
            devolverResultadoMasivo()
            return
        }
        super.onBackPressed()
    }

    companion object {
        private const val PREFS = "ropero_melamina"
        private const val CLAVE_ULTIMO = "ultimo"
        /** El prefijo de sus paquetes en el proyecto: Ropero de Melamina. */
        const val PREFIJO = "Rm"
        /** La lista del proyecto donde viaja el ropero como paquete (JSON), para redibujarlo en la ficha. */
        const val CLAVE_DISENO = "DisenoRopero"
    }
}
