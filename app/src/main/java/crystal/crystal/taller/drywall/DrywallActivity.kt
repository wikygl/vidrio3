package crystal.crystal.taller.drywall

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
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
        // La unidad que se eligió la última vez.
        unidad = runCatching { Unidad.valueOf(getSharedPreferences(PREFS, MODE_PRIVATE).getString(CLAVE_UNIDAD, null)!!) }.getOrDefault(Unidad.M)
        Drywall.desdeJson(getSharedPreferences(PREFS, MODE_PRIVATE).getString(CLAVE_ULTIMO, null))?.let { drywall = it }
        // Abierto desde Productos para editar uno archivado: ese, tal como se guardó.
        crystal.crystal.casilla.EdicionProducto.disenoDe(this)?.let { Drywall.desdeJson(it) }?.let { drywall = it }
        volcarEnPantalla()

        // Pre-carga desde presupuesto (en metros; si llega en cm, se pasa).
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho.setText(enCasilla(enMetros(it))) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAlto.setText(enCasilla(enMetros(it))) }

        controladorCola = ControladorColaMedidas(
            activity = this,
            claseActual = DrywallActivity::class.java,
            etAncho = binding.etAncho,
            etAlto = binding.etAlto,
            formato = { enCasilla(enMetros(it)) }
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
        binding.spForma.setSelection(FormaDrywall.values().indexOf(drywall.forma))
        binding.spUnidad.setSelection(unidad.ordinal)
        binding.vistaDrywall.unidad = unidad.sufijo to unidad.enM
        binding.etAncho.setText(enCasilla(drywall.largoM))
        binding.etAlto.setText(enCasilla(drywall.altoM))
        binding.etVanos.setText(df(drywall.vanosM2))
        binding.etNumVanos.setText(drywall.vanos.toString())
        binding.spSeparacion.setSelection(separaciones.indexOfFirst { kotlin.math.abs(it - drywall.separacionCm) < 0.1f }.coerceAtLeast(0))
        binding.spPlancha.setSelection(PlanchaDrywall.values().indexOf(drywall.plancha))
        binding.spPerfil.setSelection(PerfilDrywall.values().indexOf(drywall.perfil))
        binding.etEsquinas.setText(drywall.esquinas.toString())
        binding.cbLana.isChecked = drywall.conLana
        binding.etFondo.setText(enCasilla(drywall.lado2M))
        binding.etLado3.setText(enCasilla(drywall.lado3M))
        binding.vistaDrywall.drywall = drywall
        ajustarCasillas(drywall.tipo, drywall.forma)
        cargando = false
    }

    private val tipoElegido: TipoDrywall
        get() = TipoDrywall.values().getOrElse(binding.spTipo.selectedItemPosition) { TipoDrywall.TABIQUE }
    private val formaElegida: FormaDrywall
        get() = FormaDrywall.values().getOrElse(binding.spForma.selectedItemPosition) { FormaDrywall.RECTA }

    /**
     * Las casillas de la forma (2026-10-11): cada tramo con su nombre. En la C, el frente y los dos
     * lados que salen del muro; en la curva, el desarrollo y la flecha. El cielo raso no tiene forma.
     */
    private fun ajustarCasillas(tipo: TipoDrywall, forma: FormaDrywall) {
        val esCielo = tipo == TipoDrywall.CIELO_RASO
        val f = if (esCielo) FormaDrywall.RECTA else forma
        binding.lyForma.visibility = if (esCielo) View.GONE else View.VISIBLE
        binding.tvEtiquetaLargo.text = when (f) {
            FormaDrywall.RECTA -> "Largo (${unidad.sufijo})"
            FormaDrywall.L, FormaDrywall.S -> "Lado 1 (${unidad.sufijo})"
            FormaDrywall.C -> "Frente (${unidad.sufijo})"
            FormaDrywall.CURVA -> "Desarrollo (${unidad.sufijo})"
        }
        binding.lyFondo.visibility = if (f == FormaDrywall.RECTA) View.GONE else View.VISIBLE
        binding.tvEtiquetaLado2.text = when (f) {
            FormaDrywall.C -> "Lado izq. (${unidad.sufijo})"
            FormaDrywall.CURVA -> "Flecha (${unidad.sufijo})"
            else -> "Lado 2 (${unidad.sufijo})"
        }
        binding.lyLado3.visibility = if (f == FormaDrywall.C || f == FormaDrywall.S) View.VISIBLE else View.GONE
        binding.tvEtiquetaLado3.text = if (f == FormaDrywall.C) "Lado der. (${unidad.sufijo})" else "Lado 3 (${unidad.sufijo})"
        binding.tvEtiquetaAlto.text = "Alto (${unidad.sufijo})"
    }

    // ==================== LA UNIDAD ====================

    /** La unidad en que se escriben las medidas (pedido del usuario, 2026-10-11). El cálculo sigue en metros. */
    private enum class Unidad(val sufijo: String, val enM: Float, val decimales: Int) {
        M("m", 1f, 3), CM("cm", 0.01f, 1), MM("mm", 0.001f, 0)
    }

    private var unidad = Unidad.M

    /** Las casillas que son medidas de largo (los vanos van siempre en m²). */
    private val casillasDeMedida get() = listOf(binding.etAncho, binding.etAlto, binding.etFondo, binding.etLado3)

    /** Una medida en m, escrita en la unidad elegida: 0.18 m → "0.18", "18" o "180". */
    private fun enCasilla(m: Float): String =
        java.math.BigDecimal((m / unidad.enM).toDouble()).setScale(unidad.decimales, java.math.RoundingMode.HALF_UP)
            .stripTrailingZeros().toPlainString()

    /** Lo que dice una casilla de medida, pasado a m. */
    private fun medidaM(et: EditText): Float? = et.text.toString().replace(',', '.').toFloatOrNull()?.times(unidad.enM)

    /** Cambia la unidad sin cambiar las medidas: lo escrito se pasa a la nueva unidad. */
    private fun cambiarUnidad(nueva: Unidad) {
        val medidas = casillasDeMedida.map { medidaM(it) }
        unidad = nueva
        cargando = true
        casillasDeMedida.zip(medidas).forEach { (et, m) -> if (m != null) et.setText(enCasilla(m)) }
        cargando = false
        ajustarCasillas(tipoElegido, formaElegida)
        binding.vistaDrywall.unidad = unidad.sufijo to unidad.enM
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(CLAVE_UNIDAD, unidad.name).apply()
    }

    /** Lo más chico que se acepta, en metros: 5 cm. Antes era 30 cm y un lado de 0.18 no pasaba. */
    private val MEDIDA_MINIMA_M = 0.05f

    /** El drywall que dicen las casillas, o null si todavía no dicen medidas válidas. */
    private fun drywallDesdeCasillas(): Drywall? {
        val largo = medidaM(binding.etAncho) ?: return null
        val alto = medidaM(binding.etAlto) ?: return null
        if (largo < MEDIDA_MINIMA_M || alto < MEDIDA_MINIMA_M) return null
        val tipo = tipoElegido
        val forma = if (tipo == TipoDrywall.CIELO_RASO) FormaDrywall.RECTA else formaElegida
        val lado2 = medidaM(binding.etFondo) ?: 0f
        val lado3 = medidaM(binding.etLado3) ?: 0f
        if (forma != FormaDrywall.RECTA && lado2 < MEDIDA_MINIMA_M) return null
        if ((forma == FormaDrywall.C || forma == FormaDrywall.S) && lado3 < MEDIDA_MINIMA_M) return null
        return Drywall(
            tipo = tipo,
            forma = forma,
            lado2M = if (forma == FormaDrywall.RECTA) drywall.lado2M else lado2,
            lado3M = if (forma == FormaDrywall.C || forma == FormaDrywall.S) lado3 else drywall.lado3M,
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
        binding.spForma.adapter = desplegable(FormaDrywall.values().map { it.etiqueta })
        binding.spSeparacion.adapter = desplegable(listOf("40.6 cm (16\")", "61 cm (24\")"))
        binding.spPlancha.adapter = desplegable(PlanchaDrywall.values().map { it.etiqueta })
        binding.spPerfil.adapter = desplegable(PerfilDrywall.values().map { it.etiqueta })
        binding.spUnidad.adapter = desplegable(Unidad.values().map { it.sufijo })
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
        listOf(binding.etAncho, binding.etAlto, binding.etVanos, binding.etNumVanos, binding.etEsquinas, binding.etFondo, binding.etLado3)
            .forEach { it.addTextChangedListener(alEscribir) }
        val alElegir = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent === binding.spTipo || parent === binding.spForma) {
                    ajustarCasillas(tipoElegido, formaElegida)
                    // Al cambiar de forma o de tipo, las esquinas con esquinero de esa forma (se pueden cambiar).
                    if (!cargando) binding.etEsquinas.setText(Drywall.esquinasDeFabrica(tipoElegido, formaElegida).toString())
                }
                if (!cargando) redibujarDesdeCasillas()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
        listOf(binding.spTipo, binding.spForma, binding.spSeparacion, binding.spPlancha, binding.spPerfil).forEach { it.onItemSelectedListener = alElegir }
        binding.cbLana.setOnCheckedChangeListener { _, _ -> if (!cargando) redibujarDesdeCasillas() }
        binding.spUnidad.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                Unidad.values().getOrNull(position)?.takeIf { it != unidad }?.let { cambiarUnidad(it) }
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) = Unit
        }
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
            Toast.makeText(this, "Ingrese datos válidos (en ${unidad.sufijo})", Toast.LENGTH_SHORT).show()
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
        // Cada fila, como en las demás calculadoras: el nombre del material (la lista del proyecto) y
        // sus piezas `medida = cantidad` en cm, que es lo que se archiva y leen los optimizadores.
        binding.tvPlanchas.text = m.planchas.nombre
        binding.txPlanchas.text = m.piezasPlancha
        fun filaPerfil(ly: View, tv: TextView, tx: TextView, fila: FilaDeCorte?) {
            ly.visibility = if (fila == null) View.GONE else View.VISIBLE
            tv.text = fila?.nombre.orEmpty()
            tx.text = fila?.lineas.orEmpty()
        }
        val angulo = m.filasPerfil.firstOrNull { it.nombre.startsWith("Ángulo") }
        val riel = m.filasPerfil.firstOrNull { it.nombre.startsWith("Riel") }
        val parante = m.filasPerfil.firstOrNull { it.nombre.startsWith("Parante") }
        filaPerfil(binding.lyPerfiles, binding.tvPerfiles, binding.txPerfiles, parante)
        filaPerfil(binding.lyRieles, binding.tvRieles, binding.txRieles, riel)
        filaPerfil(binding.lyAngulo, binding.tvAngulo, binding.txAngulo, angulo)
        binding.txFijaciones.text = m.lineasArchivables(m.fijaciones)
        binding.txAcabado.text = m.lineasArchivables(m.acabado)
        binding.txReferencias.text = m.referencias
        // El despiece (dónde va cada pieza y cómo se corta) y, debajo, de dónde sale cada cantidad.
        val detalle = (listOf(m.planchas) + m.perfiles + m.fijaciones + m.acabado)
            .filter { it.nota.isNotBlank() }
            .joinToString("\n") { "${it.nombre}: ${it.linea} (${it.nota})" }
        binding.tvCorte.text = if (m.despiece.isBlank()) "Detalle" else "Despiece"
        binding.txCorte.text = if (m.despiece.isBlank()) detalle else "${m.despiece}\n\nDETALLE\n$detalle"
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
        // Se archiva lo que dicen las casillas ahora: si se cambió una medida después de Calcular,
        // las filas y el diseño archivados no deben quedar de dos cálculos distintos.
        if (!calcular()) return
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
                fila(binding.lyRieles, binding.tvRieles, binding.txRieles),
                fila(binding.lyAngulo, binding.tvAngulo, binding.txAngulo),
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
            ModoMasivoHelper.texto(binding.tvPerfiles) to ModoMasivoHelper.texto(binding.txPerfiles),
            ModoMasivoHelper.texto(binding.tvRieles) to ModoMasivoHelper.texto(binding.txRieles),
            ModoMasivoHelper.texto(binding.tvAngulo) to ModoMasivoHelper.texto(binding.txAngulo)
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
        // Lo que dicen las casillas ahora, no un cálculo viejo.
        if (!calcular()) return
        val m = materiales ?: return
        val texto = buildString {
            append(m.referencias).append("\n\n")
            append(m.planchas.nombre.uppercase()).append(" (cm)\n").append(m.piezasPlancha).append("\n\n")
            m.filasPerfil.forEach { append(it.nombre.uppercase()).append(" (cm)\n").append(it.lineas).append("\n\n") }
            append("FIJACIONES\n").append(m.lineasArchivables(m.fijaciones)).append("\n\n")
            append("ACABADO\n").append(m.lineasArchivables(m.acabado))
            if (m.despiece.isNotBlank()) append("\n\nDESPIECE\n").append(m.despiece)
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
        private const val CLAVE_UNIDAD = "unidad"
        /** El prefijo de sus paquetes en el proyecto: Drywall. */
        const val PREFIJO = "Dw"
        /** La lista del proyecto donde viaja el drywall como paquete (JSON). */
        const val CLAVE_DISENO = "DisenoDrywall"
    }
}
