package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.casilla.DialogosProyecto
import crystal.crystal.casilla.ListaCasilla
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager
import crystal.crystal.casilla.ProyectoUIHelper
import crystal.crystal.databinding.ActivityDivisionBanoBinding

class DivisionBanoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDivisionBanoBinding
    private lateinit var proyectoCallback: DialogosProyecto.ProyectoCallback
    private val mapListas = mutableMapOf<String, MutableList<MutableList<String>>>()
    private var primerClickArchivarRealizado = false
    private data class EntradaCalculo(
        val ancho: Float,
        val alto: Float,
        val nCubiculos: Int,
        val profundidad: Float,
        val anchoPuerta: Float,
        val alturaDesague: Float,
        val uMarco: Float
    )

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDivisionBanoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ProyectoManager.inicializarDesdeStorage(this)
        proyectoCallback = ProyectoUIHelper.crearCallbackConActualizacionUI(
            context = this,
            textViewProyecto = binding.tvProyectoActivo,
            activity = this
        )
        ProyectoUIHelper.configurarVisorProyectoActivo(this, binding.tvProyectoActivo)
        procesarIntentProyecto(intent)

        configurarCalcular()
        configurarArchivar()
        configurarCliente()

        // Pre-carga desde presupuesto
        intent.getFloatExtra("ancho", -1f).let { if (it > 0) binding.etAncho.setText(df1(it)) }
        intent.getFloatExtra("alto", -1f).let { if (it > 0) binding.etAltoTotal.setText(df1(it)) }
    }

    // ==================== OBTENER VALORES ====================

    private fun leerEntradaCalculo(): EntradaCalculo {
        return EntradaCalculo(
            ancho = binding.etAncho.text.toString().toFloatOrNull() ?: 0f,
            alto = binding.etAltoTotal.text.toString().toFloatOrNull() ?: 0f,
            nCubiculos = binding.etCantidadCubiculos.text.toString().toIntOrNull() ?: 0,
            profundidad = binding.etProfundidad.text.toString().toFloatOrNull() ?: 0f,
            anchoPuerta = binding.etAnchoPuerta.text.toString().toFloatOrNull() ?: 0f,
            alturaDesague = binding.etAlturaDesague.text.toString().toFloatOrNull() ?: 0f,
            uMarco = binding.etMedidaAluminio.text.toString().toFloatOrNull() ?: 1.5f
        )
    }

    private fun refrescarProyectoActivoUI() {
        ProyectoUIHelper.actualizarVisorProyectoActivo(this, binding.tvProyectoActivo)
    }

    // ==================== CALCULAR ====================

    @SuppressLint("SetTextI18n")
    private fun configurarCalcular() {
        binding.btCalcular.setOnClickListener {
            try {
                if (!ProyectoUIHelper.verificarProyectoActivo(this, proyectoCallback)) return@setOnClickListener

                val entrada = leerEntradaCalculo()

                if (
                    entrada.ancho <= 0 || entrada.alto <= 0 || entrada.nCubiculos <= 0 ||
                    entrada.profundidad <= 0 || entrada.anchoPuerta <= 0 || entrada.alturaDesague <= 0
                ) {
                    Toast.makeText(this, "Ingrese datos válidos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val resultado = calcular(entrada.ancho, entrada.alto, entrada.nCubiculos)
                mostrarResultados(
                    r = resultado,
                    ancho = entrada.ancho,
                    alto = entrada.alto,
                    profundidad = entrada.profundidad,
                    nCubiculos = entrada.nCubiculos,
                    anchoPuerta = entrada.anchoPuerta,
                    alturaDesague = entrada.alturaDesague,
                    tubo = entrada.uMarco
                )
                mostrarReferencias(
                    ancho = entrada.ancho,
                    alto = entrada.alto,
                    profundidad = entrada.profundidad,
                    nCubiculos = entrada.nCubiculos,
                    anchoPuerta = entrada.anchoPuerta,
                    alturaDesague = entrada.alturaDesague,
                    medidaAluminio = entrada.uMarco
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Ingrese dato válido", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarResultados(
        r: Resultado,
        ancho: Float,
        alto: Float,
        profundidad: Float,
        nCubiculos: Int,
        anchoPuerta: Float,
        alturaDesague: Float,
        tubo: Float
    ) {
        val df = ::df1
        val anchoCubiculo = calcularAnchoCubiculo(
            anchoTotal = ancho,
            nCubiculos = nCubiculos,
            anchoParante = tubo
        )

        val qtyAltoMenosTubo = if (anchoPuerta >= anchoCubiculo) nCubiculos else nCubiculos * 2
        val mostrarLineaCubiculoPuerta = anchoPuerta < anchoCubiculo

        binding.tvParante.text = "Tubo"
        binding.txParante.text = buildString {
            append("${df(ancho - tubo)} = 1")
            append("\n${df(alto)} = ${nCubiculos + 1}")
            append("\n${df(alto - tubo)} = $qtyAltoMenosTubo")
            append("\n${df(alto - alturaDesague - tubo - 0.5f)} = ${nCubiculos * 2}")
            append("\n${df(profundidad - (2 * tubo))} = ${nCubiculos * 2}")
            append("\n${df(anchoPuerta - ((2 * tubo) + 1))} = ${nCubiculos * 2}")
            if (mostrarLineaCubiculoPuerta) {
                append("\n${df(anchoCubiculo - (anchoPuerta + tubo))} = $nCubiculos")
            }
        }
        binding.tvRielSup.text = "Tope"
        binding.txRielSup.text = buildString {
            append("${df(alto - (alturaDesague - tubo))} = ${nCubiculos * 2}")
            append("\n${df(anchoPuerta)} = $nCubiculos")
        }
        binding.txRielInf.text = "${df(r.rielInferiorMedida)} = 1"

        val qtyJunkilloAlto = if (anchoPuerta < anchoCubiculo) nCubiculos * 4 else nCubiculos * 2
        binding.tvU.text = "Junkillo"
        binding.txU.text = buildString {
            append("${df(alto - (alturaDesague + (2 * tubo)))} = $qtyJunkilloAlto")
            append("\n${df(alto - (alturaDesague + (3 * tubo) + 0.5f))} = ${nCubiculos * 2}")
            append("\n${df(profundidad - (2 * tubo))} = ${nCubiculos * 2}")
            append("\n${df(anchoPuerta - ((2 * tubo) + 1))} = ${nCubiculos * 2}")
            if (anchoPuerta < anchoCubiculo) {
                append("\n${df(anchoCubiculo - (anchoPuerta + tubo))} = ${nCubiculos * 2}")
            }
        }
        binding.txFelpa.text = "${df(r.felpaMetros)} mts"
        binding.tvV.text = "Panel"
        binding.txV.text = buildString {
            append("${df(alto - (alturaDesague + (2 * tubo) + 0.5f))} X ${df(profundidad - ((2 * tubo) + 0.5f))} = $nCubiculos")
            append("\n${df(anchoPuerta - ((2 * tubo) + 1 + 0.5f))} X ${df(alto - (alturaDesague + (3 * tubo) + 0.5f + 0.5f))} = $nCubiculos")
            if (anchoPuerta < anchoCubiculo) {
                append("\n${df(alto - (alturaDesague + (2 * tubo) + 0.5f))} X ${df(anchoCubiculo - (anchoPuerta + tubo + 0.5f))} = $nCubiculos")
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarReferencias(
        ancho: Float,
        alto: Float,
        profundidad: Float,
        nCubiculos: Int,
        anchoPuerta: Float,
        alturaDesague: Float,
        medidaAluminio: Float
    ) {
        val df = ::df1
        val anchoCubiculo = calcularAnchoCubiculo(
            anchoTotal = ancho,
            nCubiculos = nCubiculos,
            anchoParante = medidaAluminio
        )
        val profundidadCubiculo = profundidad - (2 * medidaAluminio)
        binding.txReferencias.text = "A:${df(ancho)} H:${df(alto)} P:${df(profundidad)} " +
                "C:$nCubiculos Puerta:${df(anchoPuerta)} D:${df(alturaDesague)} Tubo:${df(medidaAluminio)} " +
                "Cubículo:${df(anchoCubiculo)} x ${df(profundidadCubiculo)}"
    }

    // ==================== ARCHIVAR ====================
    private fun callbackSeleccionProyectoParaArchivar(): DialogosProyecto.ProyectoCallback {
        return object : DialogosProyecto.ProyectoCallback {
            override fun onProyectoSeleccionado(nombreProyecto: String) {
                primerClickArchivarRealizado = true
                val mapExistente = MapStorage.cargarProyecto(this@DivisionBanoActivity, nombreProyecto)
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
            if (binding.etAncho.text.toString().isEmpty()) {
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
        Toast.makeText(this, "Archivado", Toast.LENGTH_SHORT).show()
        binding.etAncho.setText("")
    }

    private fun procesarLineaArchivado(
        contenedor: View,
        titulo: TextView,
        texto: TextView,
        identificadorPaquete: String,
        esReferencia: Boolean = false
    ) {
        if (contenedor.visibility == View.GONE) return
        if (esReferencia) {
            ListaCasilla.procesarReferenciasConPrefijo(this, titulo, texto, mapListas, identificadorPaquete)
        } else {
            ListaCasilla.procesarArchivarConPrefijo(this, titulo, texto, mapListas, identificadorPaquete)
        }
    }

    private fun archivarMapas() {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            val mapExistente = MapStorage.cargarProyecto(this, proyectoActivo)
            mapListas.clear()
            if (mapExistente != null) mapListas.putAll(mapExistente)
        }

        val prefijo = "Db"
        val cant = intent.getFloatExtra("cantidad", 1f).toInt().coerceAtLeast(1)
        var ultimoID = ""

        for (u in 1..cant) {
            val siguienteNumero = ProyectoManager.obtenerSiguienteContadorPorPrefijo(this, prefijo)
            val identificadorPaquete = "$prefijo$siguienteNumero"
            ultimoID = identificadorPaquete

            procesarLineaArchivado(binding.lyReferencias, binding.tvReferencias, binding.txReferencias, identificadorPaquete, esReferencia = true)
            procesarLineaArchivado(binding.lyParante, binding.tvParante, binding.txParante, identificadorPaquete)
            procesarLineaArchivado(binding.lyRielSup, binding.tvRielSup, binding.txRielSup, identificadorPaquete)
            procesarLineaArchivado(binding.lyRielInf, binding.tvRielInf, binding.txRielInf, identificadorPaquete)
            procesarLineaArchivado(binding.lyU, binding.tvU, binding.txU, identificadorPaquete)
            procesarLineaArchivado(binding.lyFelpa, binding.tvFelpa, binding.txFelpa, identificadorPaquete)
            procesarLineaArchivado(binding.lyVidrios, binding.tvV, binding.txV, identificadorPaquete)

            ProyectoManager.actualizarContadorPorPrefijo(this, prefijo, siguienteNumero)
        }

        MapStorage.guardarMap(this, mapListas)
        refrescarProyectoActivoUI()
        val msg = if (cant > 1) "Archivadas $cant unidades en proyecto: ${ProyectoManager.getProyectoActivo()}"
                  else "Datos archivados como $ultimoID en proyecto: ${ProyectoManager.getProyectoActivo()}"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    private fun devolverResultadoMasivo() {
        val perfiles = mapOf(
            ModoMasivoHelper.texto(binding.tvParante) to ModoMasivoHelper.texto(binding.txParante),
            ModoMasivoHelper.texto(binding.tvRielSup) to ModoMasivoHelper.texto(binding.txRielSup),
            ModoMasivoHelper.texto(binding.tvRielInf) to ModoMasivoHelper.texto(binding.txRielInf),
            ModoMasivoHelper.texto(binding.tvU) to ModoMasivoHelper.texto(binding.txU)
        ).filter { it.value.isNotBlank() }
        val accesorios = mapOf(
            "Felpa" to ModoMasivoHelper.texto(binding.txFelpa)
        ).filter { it.value.isNotBlank() }
        ModoMasivoHelper.devolverResultado(
            activity = this,
            calculadora = "Division Baño",
            perfiles = perfiles,
            vidrios = ModoMasivoHelper.texto(binding.txV),
            accesorios = accesorios,
            referencias = ModoMasivoHelper.texto(binding.txReferencias)
        )
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
            androidx.appcompat.app.AlertDialog.Builder(this)
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
            onProyectoCambiado = {
                refrescarProyectoActivoUI()
            }
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

    // ==================== CALCULOS ====================

    data class Resultado(
        val anchoPanelVidrio: Float,
        val altoPanelVidrio: Float,
        val numPaneles: Int,
        val paranteMedida: Float,
        val paranteQty: Int,
        val rielSuperiorMedida: Float,
        val rielInferiorMedida: Float,
        val uMedida: Float,
        val uQty: Int,
        val felpaMetros: Float
    )

    private fun calcularAnchoCubiculo(
        anchoTotal: Float,
        nCubiculos: Int,
        anchoParante: Float,
        parantesExtra: Int = 0
    ): Float {
        if (nCubiculos <= 0) return 0f
        val totalParantes = nCubiculos + parantesExtra
        return (anchoTotal - totalParantes * anchoParante) / nCubiculos
    }

    private fun calcular(anchoTotal: Float, alto: Float, numPaneles: Int): Resultado {
        val numParantes = numPaneles + 1
        val anchoPanelBruto = calcularAnchoCubiculo(
            anchoTotal = anchoTotal,
            nCubiculos = numPaneles,
            anchoParante = ANCHO_PARANTE,
            parantesExtra = 1
        )
        val anchoPanelVidrio = anchoPanelBruto - 2 * HOLGURA
        val altoPanelVidrio = alto - ALTO_RIEL - ALTO_RIEL - 2 * HOLGURA

        val paranteMedida = alto
        val rielSuperior = anchoTotal
        val rielInferior = anchoTotal
        val uMedida = anchoPanelBruto
        val uQty = numPaneles * 2

        val perimetroPorPanel = 2 * (anchoPanelBruto + alto)
        val felpaMetros = perimetroPorPanel * numPaneles / 100f

        return Resultado(
            anchoPanelVidrio = anchoPanelVidrio,
            altoPanelVidrio = altoPanelVidrio,
            numPaneles = numPaneles,
            paranteMedida = paranteMedida,
            paranteQty = numParantes,
            rielSuperiorMedida = rielSuperior,
            rielInferiorMedida = rielInferior,
            uMedida = uMedida,
            uQty = uQty,
            felpaMetros = felpaMetros
        )
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
        private const val ANCHO_PARANTE = 3.8f
        private const val ALTO_RIEL = 3f
        private const val HOLGURA = 0.5f
    }
}








