package crystal.crystal.taller.melamina

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
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
import org.json.JSONArray
import org.json.JSONObject

/**
 * Ropero empotrado de melamina: se apunta el hueco, se reparte en cuerpos tocando el dibujo, y
 * salen la lista de corte, los cantos, los accesorios y las planchas.
 *
 * Lo que sale se archiva en el proyecto activo como cualquier calculadora (listas por material
 * con líneas `medida = cantidad`, referencias y el diseño del ropero como paquete), se manda al
 * optimizador de planchas para el corte real, o se comparte como texto.
 */
class RoperoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoperoBinding
    private var ropero = Ropero()
    private var materiales: MaterialesRopero? = null
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var cargando = false

    /** Las listas de material tal como se archivan: título (nombre de lista) y líneas. */
    private val listasArchivables = mutableListOf<ListaCasilla.ItemArchivable>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRoperoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Ropero de melamina"

        ropero = Ropero.desdeJson(getSharedPreferences(PREFS, MODE_PRIVATE).getString(CLAVE_ULTIMO, null)) ?: Ropero()
        configurarEntradas()
        configurarBotones()
        volcarRoperoEnPantalla()
        recalcular()
        refrescarProyectoActivoUI()
        configurarCliente()
    }

    // ==================== Entradas ====================

    private fun configurarEntradas() {
        binding.spPuertas.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, TipoPuertas.values().map { it.etiqueta })
        binding.spEspesor.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listOf("18 mm", "15 mm"))
        val alCambiar = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) { if (!cargando) leerEntradasYRecalcular() }
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
        binding.spPuertas.onItemSelectedListener = alCambiar
        binding.spEspesor.onItemSelectedListener = alCambiar
        binding.cbFondo.setOnCheckedChangeListener { _, _ -> if (!cargando) leerEntradasYRecalcular() }
        listOf(binding.etAncho, binding.etAlto, binding.etFondo, binding.etZocalo, binding.etMaletero, binding.etCuerpos).forEach { et ->
            et.setOnFocusChangeListener { _, tiene -> if (!tiene && !cargando) leerEntradasYRecalcular() }
        }
        binding.vistaRopero.alTocarCuerpo = { i -> editarCuerpo(i) }
    }

    private fun volcarRoperoEnPantalla() {
        cargando = true
        binding.etAncho.setText(fmt(ropero.anchoCm))
        binding.etAlto.setText(fmt(ropero.altoCm))
        binding.etFondo.setText(fmt(ropero.fondoCm))
        binding.etZocalo.setText(fmt(ropero.zocaloCm))
        binding.etMaletero.setText(fmt(ropero.maleteroCm))
        binding.etCuerpos.setText(ropero.cuerpos.size.toString())
        binding.spPuertas.setSelection(TipoPuertas.values().indexOf(ropero.puertas))
        binding.spEspesor.setSelection(if (ropero.espesorMm <= 15) 1 else 0)
        binding.cbFondo.isChecked = ropero.conFondo
        binding.vistaRopero.ropero = ropero
        cargando = false
    }

    private fun num(et: EditText, porDefecto: Float): Float =
        et.text?.toString()?.trim()?.replace(",", ".")?.toFloatOrNull() ?: porDefecto

    /** Lee las casillas y rehace el ropero: si cambió el hueco o la cantidad de cuerpos, se reparten de nuevo. */
    private fun leerEntradasYRecalcular() {
        val ancho = num(binding.etAncho, ropero.anchoCm)
        val alto = num(binding.etAlto, ropero.altoCm)
        val fondo = num(binding.etFondo, ropero.fondoCm)
        val cuerpos = binding.etCuerpos.text?.toString()?.trim()?.toIntOrNull()?.coerceIn(1, 8) ?: ropero.cuerpos.size
        var nuevo = ropero.copy(
            zocaloCm = num(binding.etZocalo, ropero.zocaloCm).coerceIn(0f, 30f),
            maleteroCm = num(binding.etMaletero, ropero.maleteroCm).coerceIn(0f, 120f),
            puertas = TipoPuertas.values()[binding.spPuertas.selectedItemPosition.coerceIn(0, TipoPuertas.values().lastIndex)],
            espesorMm = if (binding.spEspesor.selectedItemPosition == 1) 15 else 18,
            conFondo = binding.cbFondo.isChecked
        )
        val huecoCambio = ancho != nuevo.anchoCm || alto != nuevo.altoCm || fondo != nuevo.fondoCm || nuevo.espesorMm != ropero.espesorMm
        if (huecoCambio) nuevo = nuevo.conHueco(ancho, alto, fondo)
        if (cuerpos != nuevo.cuerpos.size) nuevo = nuevo.conCuerposIguales(cuerpos)
        ropero = nuevo
        binding.vistaRopero.ropero = ropero
        recalcular()
    }

    // ==================== El cuerpo tocado ====================

    private fun editarCuerpo(indice: Int) {
        val c = ropero.cuerpos.getOrNull(indice) ?: return
        binding.vistaRopero.cuerpoResaltado = indice
        val dp = resources.displayMetrics.density
        val spTipo = android.widget.Spinner(this).apply {
            adapter = ArrayAdapter(this@RoperoActivity, android.R.layout.simple_spinner_dropdown_item, TipoCuerpo.values().map { it.etiqueta })
            setSelection(TipoCuerpo.values().indexOf(c.tipo))
        }
        fun campo(rotulo: String, valor: String, decimal: Boolean) = EditText(this).apply {
            hint = rotulo
            inputType = if (decimal) InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL else InputType.TYPE_CLASS_NUMBER
            setText(valor)
            setSelectAllOnFocus(true)
        }
        val etAncho = campo("Ancho interior del cuerpo (cm)", fmt(c.anchoCm), true)
        val etEntrepanos = campo("Entrepaños", c.entrepanos.toString(), false)
        val etCajones = campo("Cajones", c.cajones.toString(), false)
        val etAltoCajon = campo("Alto de cada cajón (cm)", fmt(ropero.altoCajonCm), true)
        val caja = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((20 * dp).toInt(), (8 * dp).toInt(), (20 * dp).toInt(), 0)
            addView(TextView(this@RoperoActivity).apply { text = "Qué lleva"; textSize = 12f })
            addView(spTipo); addView(etAncho); addView(etEntrepanos); addView(etCajones); addView(etAltoCajon)
        }
        AlertDialog.Builder(this)
            .setTitle("Cuerpo ${indice + 1}")
            .setView(caja)
            .setPositiveButton("Aceptar") { _, _ ->
                val tipo = TipoCuerpo.values()[spTipo.selectedItemPosition.coerceIn(0, TipoCuerpo.values().lastIndex)]
                val nuevo = Cuerpo(
                    anchoCm = c.anchoCm,
                    tipo = tipo,
                    entrepanos = etEntrepanos.text?.toString()?.toIntOrNull() ?: 0,
                    cajones = etCajones.text?.toString()?.toIntOrNull() ?: 0
                )
                ropero = ropero.conCuerpo(indice, nuevo)
                    .copy(altoCajonCm = num(etAltoCajon, ropero.altoCajonCm).coerceIn(8f, 60f))
                val ancho = num(etAncho, c.anchoCm)
                if (kotlin.math.abs(ancho - c.anchoCm) > 0.05f) ropero = ropero.conAnchoDeCuerpo(indice, ancho)
                binding.vistaRopero.cuerpoResaltado = -1
                binding.vistaRopero.ropero = ropero
                recalcular()
            }
            .setNegativeButton("Cancelar") { _, _ -> binding.vistaRopero.cuerpoResaltado = -1 }
            .setOnCancelListener { binding.vistaRopero.cuerpoResaltado = -1 }
            .show()
    }

    // ==================== Cálculo y listas ====================

    @SuppressLint("SetTextI18n")
    private fun recalcular() {
        val m = RoperoCalculo.calcular(ropero)
        materiales = m
        binding.txReferencias.text = m.referencias
        binding.txCorte.text = m.listaDeCorte()
        binding.lyMateriales.removeAllViews()
        listasArchivables.clear()
        MaterialPlancha.values().forEach { mat ->
            val lineas = m.lineasDePiezas(mat)
            if (lineas.isNotBlank()) agregarLista(mat.etiqueta, lineas)
        }
        val tapa = if (ropero.espesorMm <= 15) "Tapacanto 19 mm" else "Tapacanto 22 mm"
        agregarLista(tapa, m.lineasDeTapacanto())
        m.nombresConLargo().forEach { nombre -> agregarLista(nombre, m.lineasConLargo(nombre)) }
        agregarLista("Accesorios melamina", m.lineasDeAccesorios())
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString(CLAVE_ULTIMO, ropero.aJson()).apply()
    }

    private fun agregarLista(titulo: String, lineas: String) {
        if (lineas.isBlank()) return
        val dp = resources.displayMetrics.density
        val tvTitulo = TextView(this).apply {
            text = titulo
            setTextColor(android.graphics.Color.BLACK)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, (6 * dp).toInt(), 0, 0)
        }
        val tvLineas = TextView(this).apply {
            text = lineas
            textSize = 12f
            setTextColor(android.graphics.Color.parseColor("#333333"))
        }
        binding.lyMateriales.addView(tvTitulo)
        binding.lyMateriales.addView(tvLineas)
        listasArchivables.add(ListaCasilla.ItemArchivable(tvTitulo, tvLineas, true))
    }

    // ==================== Botones ====================

    private fun configurarBotones() {
        binding.btCalcular.setOnClickListener { leerEntradasYRecalcular(); Toast.makeText(this, "Calculado", Toast.LENGTH_SHORT).show() }
        binding.btPuertas.setOnClickListener {
            binding.vistaRopero.mostrarPuertas = !binding.vistaRopero.mostrarPuertas
            binding.btPuertas.text = if (binding.vistaRopero.mostrarPuertas) "Ver interior" else "Ver puertas"
        }
        binding.bt3d.setOnClickListener {
            binding.vistaRopero.en3d = !binding.vistaRopero.en3d
            binding.bt3d.text = if (binding.vistaRopero.en3d) "Ver de frente" else "Ver en 3D"
        }
        binding.btArchivar.setOnClickListener { archivar() }
        binding.btOptimizar.setOnClickListener { mandarAlOptimizador() }
        binding.btCompartir.setOnClickListener { compartir() }
    }

    private fun archivar() {
        if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeArchivar(),
                "Archivar es una función de pago. Renueva para guardar tus proyectos.")) return
        leerEntradasYRecalcular()
        if (!ProyectoManager.hayProyectoActivo()) {
            Toast.makeText(this, "No hay proyecto activo. Selecciona uno.", Toast.LENGTH_SHORT).show()
            DialogosProyecto.mostrarDialogoSeleccionarParaArchivar(this, callbackArchivar())
            return
        }
        ejecutarArchivado()
    }

    private fun callbackArchivar() = object : DialogosProyecto.ProyectoCallback {
        override fun onProyectoSeleccionado(nombreProyecto: String) {
            MapStorage.cargarProyecto(this@RoperoActivity, nombreProyecto)?.let { mapListas.clear(); mapListas.putAll(it) }
            ejecutarArchivado()
        }
        override fun onProyectoCreado(nombreProyecto: String) = ejecutarArchivado()
        override fun onProyectoEliminado(nombreProyecto: String) = refrescarProyectoActivoUI()
    }

    private fun ejecutarArchivado() {
        val cliente = intent.extras?.getString("rcliente").orEmpty()
        val paquete = ropero.aJson()
        val id = ListaCasilla.archivarEnProyectoActivo(
            context = this,
            mapListas = mapListas,
            prefijo = PREFIJO,
            cantidad = 1,
            referencias = ListaCasilla.ItemArchivable(binding.tvReferenciasTitulo, binding.txReferencias, true),
            items = listasArchivables,
            cliente = cliente.ifBlank { null },
            paquetesPorNumero = { mapOf(CLAVE_DISENO to paquete) }
        )
        refrescarProyectoActivoUI()
        Toast.makeText(this, "Archivado como $id en ${ProyectoManager.getProyectoActivo()}", Toast.LENGTH_SHORT).show()
    }

    /** Las piezas de melamina al optimizador de planchas, en cm, con su nombre de referencia. */
    private fun mandarAlOptimizador() {
        val m = materiales ?: return
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
        val m = materiales ?: return
        val texto = buildString {
            append(m.referencias).append("\n\n")
            append("LISTA DE CORTE (cm)\n").append(m.listaDeCorte()).append("\n\n")
            append("TAPACANTO (cm = cantidad)\n").append(m.lineasDeTapacanto()).append("\n\n")
            m.nombresConLargo().forEach { append(it.uppercase()).append(" (cm = cantidad)\n").append(m.lineasConLargo(it)).append("\n\n") }
            append("ACCESORIOS\n").append(m.lineasDeAccesorios())
        }
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Ropero de melamina")
            putExtra(Intent.EXTRA_TEXT, texto)
        }, "Compartir materiales"))
    }

    // ==================== Proyecto y cliente ====================

    private fun refrescarProyectoActivoUI() {
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
    }

    private fun configurarCliente() {
        val cliente = intent.extras?.getString("rcliente").orEmpty()
        val proyectoActual = ProyectoManager.getProyectoActivo().orEmpty()
        if (cliente.isBlank() || proyectoActual.contains(cliente, ignoreCase = true)) return
        if (!ProyectoManager.hayProyectoActivo()) {
            DialogosProyecto.mostrarDialogoCrearProyecto(this, callbackArchivarSilencioso(), cliente)
        }
    }

    private fun callbackArchivarSilencioso() = object : DialogosProyecto.ProyectoCallback {
        override fun onProyectoSeleccionado(nombreProyecto: String) = refrescarProyectoActivoUI()
        override fun onProyectoCreado(nombreProyecto: String) = refrescarProyectoActivoUI()
        override fun onProyectoEliminado(nombreProyecto: String) = refrescarProyectoActivoUI()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let { ProyectoUIHelper.agregarOpcionesMenuProyecto(it) }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val manejado = ProyectoUIHelper.manejarSeleccionMenu(
            context = this,
            itemId = item.itemId,
            callback = callbackArchivarSilencioso(),
            onProyectoCambiado = { refrescarProyectoActivoUI() }
        )
        return if (manejado) true else super.onOptionsItemSelected(item)
    }

    private fun fmt(v: Float): String =
        if (v == v.toInt().toFloat()) v.toInt().toString() else String.format(java.util.Locale.US, "%.1f", v)

    companion object {
        private const val PREFS = "ropero_melamina"
        private const val CLAVE_ULTIMO = "ultimo"
        /** El prefijo de sus paquetes en el proyecto: Ropero de Melamina. */
        const val PREFIJO = "Rm"
        /** La lista del proyecto donde viaja el ropero como paquete (JSON), para redibujarlo en la ficha. */
        const val CLAVE_DISENO = "DisenoRopero"
    }
}
