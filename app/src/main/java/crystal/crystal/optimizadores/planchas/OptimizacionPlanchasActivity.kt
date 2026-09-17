package crystal.crystal.optimizadores.planchas

import android.app.AlertDialog
import android.app.Dialog
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import crystal.crystal.R
import crystal.crystal.databinding.ActivityOptimizacionPlanchasBinding
import crystal.crystal.red.ListChatActivity
import crystal.crystal.red.interop.ChatInteropIntents

class OptimizacionPlanchasActivity : AppCompatActivity() {

    companion object {
        const val MIME_PLANCHA_CRYSTAL = "application/vnd.crystal.plancha+json"
        const val EXTENSION_PLANCHA_CRYSTAL = "crystalplancha"
        const val FORMAT_PLANCHA_CRYSTAL = "crystal.plancha"
    }

    private lateinit var binding: ActivityOptimizacionPlanchasBinding

    private var lista = mutableListOf<ItemListaPlanchas>()
    private var lista2 = mutableListOf<PlanchaDisponible>()

    private val formatter = PlanchaFormatter()
    private lateinit var dataManager: PlanchaDataManager
    private lateinit var listManager: PlanchaListManager

    private var espesorDiscoCm = 0f
    private var restringirRotacion = false
    private var proyectoOptimizadorActual: String = ""
    private var nombreListaActual: String = ""
    private var bloqueandoCargaSpinner = false
    private var spinnerConProyectosOptimizador = false
    private var spinnerProyectoKeys: List<String> = emptyList()

    private data class ProyectoPlanchas(
        val items: MutableList<ItemListaPlanchas>,
        val planchas: MutableList<PlanchaDisponible>,
        val nivel: Int,
        val espesorDiscoCm: Float = 0f
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptimizacionPlanchasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataManager = PlanchaDataManager(this)
        listManager = PlanchaListManager(this)

        recuperarDatos()
        actualizarListaItems()
        actualizarListaPlanchas()
        configurarEntradaPlanchas()
        manejarIntentEntrada(intent)
        cargarPiezasDesdeIntent(intent)

        binding.listadoTxt.setOnClickListener {
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.avanzadoActivo(),
                    "Guardar proyectos del optimizador es una función de pago.")) return@setOnClickListener
            mostrarDialogoGuardarProyectoOptimizador()
        }
        binding.listadoTxt.setOnLongClickListener {
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.avanzadoActivo(),
                    "Abrir proyectos guardados es una función de pago.")) return@setOnLongClickListener true
            mostrarDialogoAbrirProyectoOptimizador()
            true
        }

        binding.tvNivel.text = nivelTexto(binding.sbNivel.progress)
        binding.sbNivel.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvNivel.text = nivelTexto(progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        binding.tbDisco.setOnClickListener {
            mostrarDialogoEspesorDisco()
        }

        // Long click en Añadir: recuperar vidrios de proyectos archivados
        binding.btAnadir.setOnLongClickListener {
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.avanzadoActivo(),
                    "Cargar listas guardadas es una función de pago.")) return@setOnLongClickListener true
            if (listManager.hayListasDisponibles()) {
                spinnerConProyectosOptimizador = false
                spinnerProyectoKeys = emptyList()
                listManager.poblarSpinnerConDatosGuardados(binding.spCortes)
            } else {
                Toast.makeText(this, "No hay proyectos archivados disponibles", Toast.LENGTH_SHORT).show()
            }
            true
        }

        // Spinner: al seleccionar una lista, cargar sus vidrios
        binding.spCortes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (bloqueandoCargaSpinner) return
                val nombre = parent.getItemAtPosition(position).toString()
                if (spinnerConProyectosOptimizador) {
                    val nombreProyecto = spinnerProyectoKeys.getOrNull(position) ?: nombre
                    abrirProyectoOptimizador(nombreProyecto, actualizarSpinner = false)
                } else {
                    cargarListaEnForm(nombre)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Long click en spinner: unir varias listas
        binding.spCortes.setOnLongClickListener {
            val adapter = binding.spCortes.adapter ?: return@setOnLongClickListener true
            val count = adapter.count
            if (count == 0) {
                Toast.makeText(this, "Primero carga las listas disponibles", Toast.LENGTH_SHORT).show()
                return@setOnLongClickListener true
            }
            val nombres = (0 until count).map { adapter.getItem(it).toString() }
            val seleccionados = BooleanArray(count) { false }
            AlertDialog.Builder(this)
                .setTitle("Unir listas")
                .setMultiChoiceItems(nombres.toTypedArray(), seleccionados) { _, i, checked ->
                    seleccionados[i] = checked
                }
                .setPositiveButton("Unir") { _, _ ->
                    val elegidas = nombres.filterIndexed { i, _ -> seleccionados[i] }
                    if (elegidas.size < 2) {
                        Toast.makeText(this, "Selecciona al menos 2 listas para unir", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    val combinadas = elegidas.flatMap { listManager.cargarLista(it) }.toMutableList()
                    if (combinadas.isNotEmpty()) {
                        lista.clear()
                        lista.addAll(combinadas)
                        actualizarListaItems()
                        dataManager.guardarItems(lista)
                        Toast.makeText(this, "${elegidas.size} listas unidas — ${combinadas.size} vidrios", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
            true
        }

        binding.btAnadir.setOnClickListener { anadir() }

        binding.btLimpiar.setOnClickListener {
            lista.clear()
            actualizarListaItems()
            dataManager.guardarItems(lista)
            limpiarCamposEntrada()
        }

        binding.btOpti.setOnClickListener { optimizar() }
        binding.btOpti.setOnLongClickListener {
            compartirContextoPlanchas()
            true
        }

        // Botón de mensajería (cabecera): desplegable con ir a chat / enviar Crystal / enviar SVC.
        binding.imageButton.setOnClickListener { mostrarMenuMensajeriaPlanchas() }

        // Agregar plancha disponible
        binding.btAgregar.setOnClickListener { abrirDialogoPlancha() }

        // Ajustes (edición en masa): seleccionar medidas iguales, etc.
        // Tercer botón de la cabecera (icono de calculadora): la misma calculadora flotante que
        // se abre desde "Referencias y Cálculos" en las calculadoras de taller.
        binding.micro.setOnClickListener {
            crystal.crystal.calculadora.CalculadoraFlotante.alternar(this)
        }
        binding.btnCatalogo.setOnClickListener { mostrarMenuAjustesPlanchas() }

        // Eliminar planchas disponibles
        binding.btEliminar.setOnClickListener {
            lista2.clear()
            actualizarListaPlanchas()
            dataManager.guardarPlanchasDisp(lista2)
        }

        // Click en item de lista: toggle activo + cambiar modo según tipo
        binding.listCorte.setOnItemClickListener { _, _, pos, _ ->
            lista[pos] = lista[pos].copy(activo = !lista[pos].activo)
            actualizarListaItems()
            dataManager.guardarItems(lista)
        }

        // Long click: diálogo editar/eliminar ítem
        binding.listCorte.setOnItemLongClickListener { _, _, pos, _ ->
            mostrarDialogoEditarItem(pos)
            true
        }

        // Click en plancha disponible: toggle activa
        binding.listaPerfil.setOnItemClickListener { _, _, pos, _ ->
            lista2[pos] = lista2[pos].copy(activa = !lista2[pos].activa)
            actualizarListaPlanchas()
            dataManager.guardarPlanchasDisp(lista2)
        }

        // Long click plancha disponible: diálogo editar/eliminar
        binding.listaPerfil.setOnItemLongClickListener { _, _, pos, _ ->
            mostrarDialogoEditarPlancha(pos)
            true
        }
    }

    override fun onResume() {
        super.onResume()
        procesarDescuentosAutomaticos()
    }

    override fun onPause() {
        super.onPause()
        dataManager.guardarDatos(lista, lista2)
        dataManager.guardarEspesorDisco(espesorDiscoCm)
    }

    private fun procesarDescuentosAutomaticos() {
        val piezasEjecutadas = dataManager.recuperarPiezasEjecutadas()
        if (piezasEjecutadas.isEmpty()) return

        piezasEjecutadas.forEach { pieza ->
            descontarPiezaEjecutada(pieza)
        }
        lista.removeAll { it.cantidad <= 0 }
        actualizarListaItems()
        dataManager.guardarItems(lista)
        dataManager.limpiarPiezasEjecutadas()

        val total = piezasEjecutadas.sumOf { it.cantidad }
        Toast.makeText(this, "Se descontaron $total piezas ejecutadas", Toast.LENGTH_LONG).show()
    }

    private fun descontarPiezaEjecutada(piezaEjecutada: PiezaPlanchaEjecutada) {
        var cantidadRestante = piezaEjecutada.cantidad

        while (cantidadRestante > 0) {
            val index = indicePiezaParaDescontar(piezaEjecutada)
            if (index == -1) return

            val piezaActual = lista[index]
            val descontar = minOf(cantidadRestante, piezaActual.cantidad)
            lista[index] = piezaActual.copy(cantidad = piezaActual.cantidad - descontar)
            cantidadRestante -= descontar
        }
    }

    private fun indicePiezaParaDescontar(piezaEjecutada: PiezaPlanchaEjecutada): Int {
        val exacta = lista.indexOfFirst { pieza ->
            mismaMedida(pieza.ancho, piezaEjecutada.ancho) &&
                mismaMedida(pieza.alto, piezaEjecutada.alto) &&
                pieza.info == piezaEjecutada.info
        }
        if (exacta != -1) return exacta

        val rotada = lista.indexOfFirst { pieza ->
            mismaMedida(pieza.ancho, piezaEjecutada.alto) &&
                mismaMedida(pieza.alto, piezaEjecutada.ancho) &&
                pieza.info == piezaEjecutada.info
        }
        if (rotada != -1) return rotada

        return lista.indexOfFirst { it.info == piezaEjecutada.info }
    }

    private fun mismaMedida(a: Float, b: Float): Boolean =
        abs(a - b) < 0.05f

    private fun configurarEntradaPlanchas() {
        actualizarTextoDisco()
        binding.tbDisco.setBackgroundColor(getColor(R.color.celeste))
        binding.lyAlto.visibility = View.VISIBLE
        binding.tvLabelAncho.text = "Ancho"
        binding.tvLabelRef.text = "Referencia"
    }

    private fun actualizarTextoDisco() {
        binding.tbDisco.text = String.format(Locale.US, "%.1f", espesorDiscoCm)
    }

    private fun mostrarDialogoEspesorDisco() {
        val input = EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(String.format(Locale.US, "%.1f", espesorDiscoCm))
            selectAll()
        }
        AlertDialog.Builder(this)
            .setTitle("Espesor de disco")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                espesorDiscoCm = (formatter.toFloat(input.text.toString()) ?: 0f).coerceAtLeast(0f)
                actualizarTextoDisco()
                dataManager.guardarEspesorDisco(espesorDiscoCm)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ─── Añadir ítem ────────────────────────────────────────────────────────

    private fun anadir() {
        val anchoStr = binding.etMedida.text.toString()
        val altoStr = binding.etAlto.text.toString()
        val cantStr = binding.etCant.text.toString()
        val refStr = binding.etRefe.text.toString()
        val ancho = formatter.toFloat(anchoStr)
        val alto = formatter.toFloat(altoStr)
        val cant = formatter.toInt(cantStr)

        if (ancho == null || alto == null || cant == null || refStr.isBlank()) {
            Toast.makeText(this, "Completa ancho, alto, cantidad e info", Toast.LENGTH_SHORT).show()
            return
        }
        lista.add(ItemListaPlanchas(
            ancho = ancho,
            alto = alto,
            cantidad = cant,
            info = refStr
        ))

        actualizarListaItems()
        dataManager.guardarItems(lista)
        limpiarCamposEntrada()
        binding.etMedida.requestFocus()
    }

    // ─── Optimización ───────────────────────────────────────────────────────

    private fun optimizar() {
        // Optimizar con medidas manuales es GRATIS; importar/cargar/guardar es de pago (gateado aparte).
        val vidrios = lista.filter { it.activo && it.ancho > 0f && it.alto > 0f }
        val planchasActivas = lista2.filter { it.activa && it.ancho > 0f && it.alto > 0f }

        if (vidrios.isEmpty()) {
            Toast.makeText(this, "No hay vidrios seleccionados para optimizar", Toast.LENGTH_SHORT).show()
            return
        }
        if (planchasActivas.isEmpty()) {
            Toast.makeText(this, "No hay planchas disponibles seleccionadas", Toast.LENGTH_SHORT).show()
            return
        }

        val piezas = vidrios.map { v ->
            PiezaPlancha(
                id = "p-${System.nanoTime()}",
                descripcion = v.info,
                anchoMm = (v.ancho * 10).toInt(),
                altoMm = (v.alto * 10).toInt(),
                cantidad = v.cantidad,
                // Si el usuario restringe la rotación, ninguna pieza puede rotar; si no, todas pueden.
                rotacionPermitida = !restringirRotacion
            )
        }

        // INVENTARIO de material del que se puede cortar. Toda la lista es stock: cada fila aporta
        // sus unidades y ninguna manda por estar primera (antes la primera fila era la "plancha
        // base", con planchas ilimitadas, y el resto retazos; el orden de escritura cambiaba el
        // resultado y podía reportar más cortes faltantes al agregar material).
        //
        // La medida de mayor área es la plancha entera (lo que se compra); todo lo menor se marca
        // como retazo, material sobrante que conviene gastar primero.
        val areaMayor = planchasActivas.maxOf { it.ancho.toDouble() * it.alto }
        val stock = planchasActivas.map { p ->
            PlanchaStock(
                nombre = p.nombre.ifBlank { "Plancha" },
                anchoMm = (p.ancho * 10).toInt(),   // mm si ingresaron en cm
                altoMm = (p.alto * 10).toInt(),
                cantidad = p.cantidad.coerceAtLeast(1),
                esRetazo = p.ancho.toDouble() * p.alto < areaMayor
            )
        }

        val intensidad = when (binding.sbNivel.progress) {
            0 -> IntensidadOptimizacionPlanchas.RAPIDO
            2 -> IntensidadOptimizacionPlanchas.PROFUNDO
            else -> IntensidadOptimizacionPlanchas.NORMAL
        }
        val espesorDiscoMm = (espesorDiscoCm * 10f).toInt().coerceAtLeast(0)

        binding.btOpti.isEnabled = false
        binding.btOpti.text = "Calculando..."

        Thread {
            // Los cortes que no entran salen del propio cálculo (no de un recorte posterior): el
            // optimizador solo abre unidades que existen en el inventario.
            val resultado = OptimizadorPlanchas.optimizar(
                piezas = piezas,
                stock = stock,
                intensidad = intensidad,
                separacionCorteMm = espesorDiscoMm
            )

            runOnUiThread {
                binding.btOpti.isEnabled = true
                binding.btOpti.text = getString(R.string.calcular)

                dataManager.guardarResultado(resultado)

                val sinUbicar = resultado.piezasSinUbicar.size
                if (sinUbicar > 0) {
                    Toast.makeText(
                        this,
                        "No alcanza el material disponible: faltan $sinUbicar corte(s). " +
                            "Se muestran abajo.",
                        Toast.LENGTH_LONG
                    ).show()
                }

                startActivity(Intent(this, ResultadoPlanchasActivity::class.java))
            }
        }.start()
    }

    // ─── Diálogos ────────────────────────────────────────────────────────────

    private fun abrirDialogoPlancha() {
        val d = Dialog(this)
        d.setContentView(R.layout.dialogo_plancha)
        val lyTxt = d.findViewById<LinearLayout>(R.id.lyTxt)
        val etAncho = d.findViewById<EditText>(R.id.etdAncho)
        val etAlto = d.findViewById<EditText>(R.id.etdAlto)
        val etCant = d.findViewById<EditText>(R.id.etdCant)
        val etNombre = d.findViewById<EditText>(R.id.etdProducto)
        val btAgregar = d.findViewById<Button>(R.id.btDiAgregar)
        val btnEli = d.findViewById<Button>(R.id.btn_dialogo_eliminar)
        val btnEdi = d.findViewById<Button>(R.id.btn_dialogo_editar)

        btAgregar.visibility = View.VISIBLE
        btnEli.visibility = View.GONE
        btnEdi.visibility = View.GONE
        lyTxt.visibility = View.VISIBLE

        d.show()
        d.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        btAgregar.setOnClickListener {
            val ancho = formatter.toFloat(etAncho.text.toString())
            val alto = formatter.toFloat(etAlto.text.toString())
            val cant = formatter.toInt(etCant.text.toString())
            val nombre = etNombre.text.toString().ifBlank { "Plancha" }

            if (ancho == null || alto == null || cant == null) {
                Toast.makeText(this, "Completa ancho, alto y cantidad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lista2.add(PlanchaDisponible(ancho, alto, cant, nombre))
            actualizarListaPlanchas()
            dataManager.guardarPlanchasDisp(lista2)
            d.dismiss()
        }
    }

    private fun mostrarDialogoEditarItem(pos: Int) {
        val d = Dialog(this)
        d.setContentView(R.layout.dialogo_plancha)

        val lyTxt = d.findViewById<LinearLayout>(R.id.lyTxt)
        val etAncho = d.findViewById<EditText>(R.id.etdAncho)
        val etAlto = d.findViewById<EditText>(R.id.etdAlto)
        val etCant = d.findViewById<EditText>(R.id.etdCant)
        val etNombre = d.findViewById<EditText>(R.id.etdProducto)
        val btAgregar = d.findViewById<Button>(R.id.btDiAgregar)
        val btnOk = d.findViewById<Button>(R.id.btnDiaOk)
        val btnEli = d.findViewById<Button>(R.id.btn_dialogo_eliminar)
        val btnEdi = d.findViewById<Button>(R.id.btn_dialogo_editar)

        btAgregar.visibility = View.GONE
        btnOk.visibility = View.GONE
        lyTxt.visibility = View.GONE

        val item = lista[pos]
        etAncho.setText(formatter.df1(item.ancho))
        etAlto.setText(formatter.df1(item.alto))
        etNombre.setText(item.info)
        etCant.setText(item.cantidad.toString())

        btnEdi.setOnClickListener {
            btnOk.visibility = View.VISIBLE
            btnEli.visibility = View.GONE
            btnEdi.visibility = View.GONE
            lyTxt.visibility = View.VISIBLE
        }

        btnEli.setOnClickListener {
            lista.removeAt(pos)
            actualizarListaItems()
            dataManager.guardarItems(lista)
            d.dismiss()
        }

        btnOk.setOnClickListener {
            val ancho = formatter.toFloat(etAncho.text.toString())
            val alto = formatter.toFloat(etAlto.text.toString())
            val cant = formatter.toInt(etCant.text.toString())
            if (ancho == null || alto == null || cant == null) {
                Toast.makeText(this, "Completa los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lista[pos] = item.copy(ancho = ancho, alto = alto, cantidad = cant, info = etNombre.text.toString())
            actualizarListaItems()
            dataManager.guardarItems(lista)
            d.dismiss()
        }

        d.show()
    }

    private fun mostrarDialogoEditarPlancha(pos: Int) {
        val d = Dialog(this)
        d.setContentView(R.layout.dialogo_plancha)

        val lyTxt = d.findViewById<LinearLayout>(R.id.lyTxt)
        val etAncho = d.findViewById<EditText>(R.id.etdAncho)
        val etAlto = d.findViewById<EditText>(R.id.etdAlto)
        val etCant = d.findViewById<EditText>(R.id.etdCant)
        val etNombre = d.findViewById<EditText>(R.id.etdProducto)
        val btAgregar = d.findViewById<Button>(R.id.btDiAgregar)
        val btnOk = d.findViewById<Button>(R.id.btnDiaOk)
        val btnEli = d.findViewById<Button>(R.id.btn_dialogo_eliminar)
        val btnEdi = d.findViewById<Button>(R.id.btn_dialogo_editar)

        btAgregar.visibility = View.GONE
        btnOk.visibility = View.GONE
        lyTxt.visibility = View.GONE

        val p = lista2[pos]
        etAncho.setText(formatter.df1(p.ancho))
        etAlto.setText(formatter.df1(p.alto))
        etCant.setText(p.cantidad.toString())
        etNombre.setText(p.nombre)

        btnEdi.setOnClickListener {
            btnOk.visibility = View.VISIBLE
            btnEli.visibility = View.GONE
            btnEdi.visibility = View.GONE
            lyTxt.visibility = View.VISIBLE
        }

        btnEli.setOnClickListener {
            lista2.removeAt(pos)
            actualizarListaPlanchas()
            dataManager.guardarPlanchasDisp(lista2)
            d.dismiss()
        }

        btnOk.setOnClickListener {
            val ancho = formatter.toFloat(etAncho.text.toString())
            val alto = formatter.toFloat(etAlto.text.toString())
            val cant = formatter.toInt(etCant.text.toString())
            if (ancho == null || alto == null || cant == null) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lista2[pos] = PlanchaDisponible(ancho, alto, cant, etNombre.text.toString(), p.activa)
            actualizarListaPlanchas()
            dataManager.guardarPlanchasDisp(lista2)
            d.dismiss()
        }

        d.show()
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private fun cargarListaEnForm(nombreLista: String) {
        val cargados = listManager.cargarLista(nombreLista)
        if (cargados.isNotEmpty()) {
            lista.clear()
            lista.addAll(soloPiezasPlanchas(cargados))
            nombreListaActual = nombreLista
            actualizarListaItems()
            dataManager.guardarItems(lista)
        }
    }

    private fun recuperarDatos() {
        val (items, planchas) = dataManager.recuperarDatos()
        lista = soloPiezasPlanchas(items).toMutableList()
        lista2 = planchas
        espesorDiscoCm = dataManager.recuperarEspesorDisco()
        restringirRotacion = dataManager.recuperarRestringirRotacion()
    }

    private fun soloPiezasPlanchas(items: List<ItemListaPlanchas>): List<ItemListaPlanchas> =
        items.filter { it.ancho > 0f && it.alto > 0f }

    private fun prefsProyectosOptimizador() =
        getSharedPreferences("OptimizadorProyectos", Context.MODE_PRIVATE)

    private fun nombresProyectosOptimizador(): MutableSet<String> {
        return prefsProyectosOptimizador()
            .getStringSet("planchas_nombres", emptySet())
            ?.toMutableSet()
            ?: mutableSetOf()
    }

    private fun mostrarDialogoGuardarProyectoOptimizador() {
        val input = EditText(this).apply {
            hint = "Nombre del proyecto"
            setText(nombreListaActual.ifBlank { proyectoOptimizadorActual.ifBlank { "Corte planchas" } })
            selectAll()
        }
        AlertDialog.Builder(this)
            .setTitle("Guardar proyecto")
            .setView(input)
            .setPositiveButton("Guardar presente") { _, _ ->
                val nombre = input.text.toString().trim()
                if (nombre.isBlank()) {
                    Toast.makeText(this, "Ingrese un nombre", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                guardarProyectoOptimizador(nombre)
            }
            .setNeutralButton("Guardar todas") { _, _ ->
                guardarTodasLasListasOptimizador(input.text.toString().trim())
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarProyectoOptimizador(nombre: String) {
        val proyecto = ProyectoPlanchas(
            items = lista,
            planchas = lista2,
            nivel = binding.sbNivel.progress,
            espesorDiscoCm = espesorDiscoCm
        )
        val prefs = prefsProyectosOptimizador()
        val nombres = nombresProyectosOptimizador().apply { add(nombre) }
        prefs.edit()
            .putStringSet("planchas_nombres", nombres)
            .putString("planchas_$nombre", Gson().toJson(proyecto))
            .apply()
        proyectoOptimizadorActual = nombre
        actualizarTituloProyectoOptimizador()
        Toast.makeText(this, "Proyecto guardado: $nombre", Toast.LENGTH_SHORT).show()
    }

    private fun guardarTodasLasListasOptimizador(nombre: String) {
        val adapter = binding.spCortes.adapter
        val count = adapter?.count ?: 0
        if (count == 0) {
            Toast.makeText(this, "Primero carga las listas con click largo en Añadir", Toast.LENGTH_SHORT).show()
            return
        }
        val nombres = (0 until count).map { adapter.getItem(it).toString() }
        var guardadas = 0
        var paqueteGuardado = ""
        val prefs = prefsProyectosOptimizador()
        val nombresGuardados = nombresProyectosOptimizador()

        nombres.forEach { nombreLista ->
            val itemsLista = soloPiezasPlanchas(listManager.cargarLista(nombreLista)).toMutableList()
            if (itemsLista.isEmpty()) return@forEach
            val paquete = paqueteDesdeItems(itemsLista, nombre)
            if (paqueteGuardado.isBlank()) paqueteGuardado = paquete
            val nombreProyecto = nombreListaConPaquete(paquete, nombreLista, guardadas + 1)
            val proyecto = ProyectoPlanchas(
                items = itemsLista,
                planchas = lista2,
                nivel = binding.sbNivel.progress,
                espesorDiscoCm = espesorDiscoCm
            )
            nombresGuardados.add(nombreProyecto)
            prefs.edit()
                .putString("planchas_$nombreProyecto", Gson().toJson(proyecto))
                .apply()
            guardadas++
        }

        if (guardadas == 0) {
            Toast.makeText(this, "No hay listas validas para guardar", Toast.LENGTH_SHORT).show()
            return
        }
        prefs.edit()
            .putStringSet("planchas_nombres", nombresGuardados)
            .apply()
        proyectoOptimizadorActual = ""
        actualizarTituloProyectoOptimizador()
        val paqueteMensaje = paqueteGuardado.ifBlank { "Sin paquete" }
        Toast.makeText(this, "Paquete $paqueteMensaje: $guardadas listas de corte", Toast.LENGTH_SHORT).show()
    }

    private fun nombreListaConPaquete(paquete: String, nombreLista: String, indice: Int): String {
        val base = nombreLista.trim().ifBlank { "Lista $indice" }
        val prefijo = paquete.trim().ifBlank { "Paquete" }
        return "$prefijo: $base"
    }

    private fun mostrarDialogoAbrirProyectoOptimizador() {
        val nombres = nombresProyectosOptimizador().sorted()
        if (nombres.isEmpty()) {
            Toast.makeText(this, "No hay proyectos guardados", Toast.LENGTH_SHORT).show()
            return
        }
        val paquetes = nombres.groupBy { paqueteDesdeNombreGuardado(it) }.toSortedMap()
        val etiquetas = paquetes.map { (paquete, listas) -> "Paquete $paquete: ${listas.size} listas de corte" }
        AlertDialog.Builder(this)
            .setTitle("Abrir proyecto")
            .setItems(etiquetas.toTypedArray()) { _, which ->
                val paquete = paquetes.keys.elementAt(which)
                mostrarDialogoListasDelPaquete(paquete, paquetes.getValue(paquete).sorted())
            }
            .setNeutralButton("Cargar paquetes") { _, _ ->
                poblarSpinnerProyectosOptimizador(nombres)
            }
            .setNegativeButton("Eliminar") { _, _ ->
                mostrarDialogoEliminarPaquetes(paquetes)
            }
            .show()
    }

    private fun mostrarDialogoListasDelPaquete(paquete: String, nombres: List<String>) {
        val etiquetas = nombres.map { listaDesdeNombreGuardado(it) }
        AlertDialog.Builder(this)
            .setTitle("Paquete $paquete")
            .setItems(etiquetas.toTypedArray()) { _, which ->
                abrirProyectoOptimizador(nombres[which])
            }
            .setNeutralButton("Abrir todas") { _, _ ->
                abrirPaqueteOptimizador(paquete, nombres)
            }
            .setNegativeButton("Eliminar") { _, _ ->
                mostrarDialogoEliminarListasDelPaquete(paquete, nombres)
            }
            .show()
    }

    private fun mostrarDialogoEliminarPaquetes(paquetes: Map<String, List<String>>) {
        val nombresPaquete = paquetes.keys.toList()
        val etiquetas = nombresPaquete.map { paquete -> "Paquete $paquete: ${paquetes[paquete]?.size ?: 0} listas" }
        val seleccionados = BooleanArray(nombresPaquete.size)
        AlertDialog.Builder(this)
            .setTitle("Eliminar paquetes")
            .setMultiChoiceItems(etiquetas.toTypedArray(), seleccionados) { _, which, checked ->
                seleccionados[which] = checked
            }
            .setPositiveButton("Eliminar") { _, _ ->
                val claves = nombresPaquete
                    .filterIndexed { index, _ -> seleccionados[index] }
                    .flatMap { paquetes[it].orEmpty() }
                eliminarProyectosOptimizador(claves)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarDialogoEliminarListasDelPaquete(paquete: String, nombres: List<String>) {
        val etiquetas = nombres.map { listaDesdeNombreGuardado(it) }
        val seleccionados = BooleanArray(nombres.size)
        AlertDialog.Builder(this)
            .setTitle("Eliminar de $paquete")
            .setMultiChoiceItems(etiquetas.toTypedArray(), seleccionados) { _, which, checked ->
                seleccionados[which] = checked
            }
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProyectosOptimizador(nombres.filterIndexed { index, _ -> seleccionados[index] })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProyectosOptimizador(nombresEliminar: List<String>) {
        if (nombresEliminar.isEmpty()) {
            Toast.makeText(this, "No seleccionaste nada para eliminar", Toast.LENGTH_SHORT).show()
            return
        }
        val nombresActuales = nombresProyectosOptimizador()
        val prefs = prefsProyectosOptimizador()
        val editor = prefs.edit()
        nombresEliminar.forEach { nombre ->
            nombresActuales.remove(nombre)
            editor.remove("planchas_$nombre")
        }
        editor.putStringSet("planchas_nombres", nombresActuales).apply()
        if (nombresEliminar.contains(proyectoOptimizadorActual) ||
            nombresEliminar.any { paqueteDesdeNombreGuardado(it) == proyectoOptimizadorActual }
        ) {
            proyectoOptimizadorActual = ""
            actualizarTituloProyectoOptimizador()
        }
        Toast.makeText(this, "Eliminados: ${nombresEliminar.size}", Toast.LENGTH_SHORT).show()
    }

    private fun abrirPaqueteOptimizador(paquete: String, nombres: List<String>) {
        val proyectos = nombres.mapNotNull { nombre ->
            val json = prefsProyectosOptimizador().getString("planchas_$nombre", null)
            try {
                if (json.isNullOrBlank()) null else Gson().fromJson(json, ProyectoPlanchas::class.java)
            } catch (_: Exception) {
                null
            }
        }
        if (proyectos.isEmpty()) {
            Toast.makeText(this, "No se pudo abrir el paquete", Toast.LENGTH_SHORT).show()
            return
        }
        bloqueandoCargaSpinner = true
        try {
            lista = soloPiezasPlanchas(proyectos.flatMap { it.items }).toMutableList()
            lista2 = proyectos.first().planchas.toMutableList()
            espesorDiscoCm = proyectos.first().espesorDiscoCm
            binding.sbNivel.progress = proyectos.first().nivel.coerceIn(0, binding.sbNivel.max)
            binding.tvNivel.text = nivelTexto(binding.sbNivel.progress)
            proyectoOptimizadorActual = paquete
            nombreListaActual = paquete
            configurarEntradaPlanchas()
            actualizarListaItems()
            actualizarListaPlanchas()
            dataManager.guardarDatos(lista, lista2)
            dataManager.guardarEspesorDisco(espesorDiscoCm)
            actualizarTituloProyectoOptimizador()
            poblarSpinnerProyectosOptimizador(nombres)
        } finally {
            binding.spCortes.post { bloqueandoCargaSpinner = false }
        }
        Toast.makeText(this, "Paquete abierto: $paquete", Toast.LENGTH_SHORT).show()
    }

    private fun abrirProyectoOptimizador(nombre: String, actualizarSpinner: Boolean = true) {
        val json = prefsProyectosOptimizador().getString("planchas_$nombre", null)
        if (json.isNullOrBlank()) {
            Toast.makeText(this, "No se pudo abrir el proyecto", Toast.LENGTH_SHORT).show()
            return
        }
        val proyecto = try {
            Gson().fromJson(json, ProyectoPlanchas::class.java)
        } catch (_: Exception) {
            null
        }
        if (proyecto == null) {
            Toast.makeText(this, "Proyecto invalido", Toast.LENGTH_SHORT).show()
            return
        }
        bloqueandoCargaSpinner = true
        try {
            lista = soloPiezasPlanchas(proyecto.items).toMutableList()
            lista2 = proyecto.planchas.toMutableList()
            espesorDiscoCm = proyecto.espesorDiscoCm
            binding.sbNivel.progress = proyecto.nivel.coerceIn(0, binding.sbNivel.max)
            binding.tvNivel.text = nivelTexto(binding.sbNivel.progress)
            proyectoOptimizadorActual = nombre
            nombreListaActual = nombre
            configurarEntradaPlanchas()
            actualizarListaItems()
            actualizarListaPlanchas()
            dataManager.guardarDatos(lista, lista2)
            dataManager.guardarEspesorDisco(espesorDiscoCm)
            actualizarTituloProyectoOptimizador()
            if (actualizarSpinner) {
                val paquete = paqueteDesdeNombreGuardado(nombre)
                val nombresPaquete = nombresProyectosOptimizador()
                    .filter { paqueteDesdeNombreGuardado(it) == paquete }
                    .sorted()
                poblarSpinnerProyectosOptimizador(nombresPaquete, nombre)
            }
        } finally {
            binding.spCortes.post { bloqueandoCargaSpinner = false }
        }
        Toast.makeText(this, "Proyecto abierto: $nombre", Toast.LENGTH_SHORT).show()
    }

    private fun poblarSpinnerProyectosOptimizador(
        nombres: List<String> = nombresProyectosOptimizador().sorted(),
        seleccionar: String? = null
    ) {
        if (nombres.isEmpty()) {
            Toast.makeText(this, "No hay proyectos guardados", Toast.LENGTH_SHORT).show()
            return
        }
        bloqueandoCargaSpinner = true
        spinnerConProyectosOptimizador = true
        spinnerProyectoKeys = nombres
        val etiquetas = nombres.map { listaDesdeNombreGuardado(it) }
        val adapter = ArrayAdapter(this, R.layout.lista_spinner, etiquetas)
        adapter.setDropDownViewResource(R.layout.lista_spinner)
        binding.spCortes.adapter = adapter
        val index = seleccionar?.let { nombres.indexOf(it) } ?: -1
        if (index >= 0) binding.spCortes.setSelection(index, false)
        binding.spCortes.post { bloqueandoCargaSpinner = false }
        Toast.makeText(this, "Listas guardadas cargadas en el spinner", Toast.LENGTH_SHORT).show()
    }

    private fun paqueteDesdeItems(items: List<ItemListaPlanchas>, fallback: String): String {
        return items.asSequence()
            .map { extraerPaqueteDesdeReferencia(it.info) }
            .firstOrNull { it.isNotBlank() }
            ?: fallback.trim().ifBlank { "Sin paquete" }
    }

    private fun extraerPaqueteDesdeReferencia(referencia: String): String {
        val limpio = referencia.trim().trim('(', ')')
        if (limpio.isBlank()) return ""
        val candidato = if (limpio.contains(",")) {
            limpio.substringAfterLast(",").trim().substringBefore(" ")
        } else {
            limpio.substringAfterLast(" ", "").trim()
        }
        return candidato.trim().trim(')', '(')
    }

    private fun paqueteDesdeNombreGuardado(nombre: String): String {
        return if (nombre.contains(":")) {
            nombre.substringBefore(":").trim().ifBlank { "Sin paquete" }
        } else {
            "Guardados anteriores"
        }
    }

    private fun listaDesdeNombreGuardado(nombre: String): String {
        return if (nombre.contains(":")) {
            nombre.substringAfter(":").trim().ifBlank { nombre }
        } else {
            nombre
        }
    }

    private fun actualizarTituloProyectoOptimizador() {
        binding.tvIdVende.text = if (proyectoOptimizadorActual.isBlank()) {
            "Corte Planchas"
        } else {
            "Corte Planchas - $proyectoOptimizadorActual"
        }
    }

    private fun actualizarListaItems() {
        binding.listCorte.adapter = PlanchaListaAdapter(this, lista, formatter)
    }

    // ===== Edición en masa (como en corte de varillas): medidas iguales =====
    private fun mostrarMenuAjustesPlanchas() {
        android.widget.PopupMenu(this, binding.btnCatalogo).apply {
            menu.add("Seleccionar medidas iguales")
            menu.add("Descargar SVC (CSV)")
            menu.add(0, 1, 2, "Restringir rotación (no rotar piezas)").apply {
                isCheckable = true
                isChecked = restringirRotacion
            }
            setOnMenuItemClickListener { item ->
                when {
                    item.itemId == 1 -> {
                        restringirRotacion = !restringirRotacion
                        dataManager.guardarRestringirRotacion(restringirRotacion)
                        Toast.makeText(
                            this@OptimizacionPlanchasActivity,
                            if (restringirRotacion) "Rotación restringida: las piezas NO se rotarán"
                            else "Rotación permitida: las piezas pueden rotar para acomodar mejor",
                            Toast.LENGTH_SHORT
                        ).show()
                        true
                    }
                    item.title == "Seleccionar medidas iguales" -> { mostrarDialogoMedidasIgualesPlanchas(); true }
                    item.title == "Descargar SVC (CSV)" -> { descargarSvcPlanchas(); true }
                    else -> false
                }
            }
            show()
        }
    }

    private data class GrupoMedidaIgualPlancha(val ancho: Float, val alto: Float, val indices: List<Int>)

    private fun mostrarDialogoMedidasIgualesPlanchas() {
        val grupos = lista.indices
            .groupBy { "${formatter.df1(lista[it].ancho)} x ${formatter.df1(lista[it].alto)}" }
            .mapNotNull { (_, indices) ->
                if (indices.size < 2) return@mapNotNull null
                val item = lista[indices.first()]
                GrupoMedidaIgualPlancha(item.ancho, item.alto, indices)
            }
            .sortedWith(compareBy({ it.ancho }, { it.alto }))

        if (grupos.isEmpty()) {
            Toast.makeText(this, "No hay grupos de medidas iguales", Toast.LENGTH_SHORT).show()
            return
        }

        val etiquetasGrupos = grupos.map { g ->
            val filas = g.indices.size
            val uni = g.indices.sumOf { lista[it].cantidad }
            val estado = when {
                g.indices.all { lista[it].activo } -> "activas"
                g.indices.none { lista[it].activo } -> "inactivas"
                else -> "mixtas"
            }
            "${formatter.df1(g.ancho)} x ${formatter.df1(g.alto)} - $filas filas, $uni uni ($estado)"
        }
        val etiquetas = listOf("Todos los grupos") + etiquetasGrupos
        val seleccionados = BooleanArray(etiquetas.size)

        fun gruposElegidos(): List<GrupoMedidaIgualPlancha> =
            if (seleccionados.firstOrNull() == true) grupos
            else grupos.filterIndexed { index, _ -> seleccionados[index + 1] }

        AlertDialog.Builder(this)
            .setTitle("Medidas iguales")
            .setMultiChoiceItems(etiquetas.toTypedArray(), seleccionados) { _, which, checked ->
                seleccionados[which] = checked
                if (which == 0) {
                    for (i in 1 until seleccionados.size) seleccionados[i] = checked
                }
            }
            .setPositiveButton("Activar/Desactivar") { _, _ ->
                val elegidos = gruposElegidos()
                if (elegidos.isEmpty()) Toast.makeText(this, "No seleccionaste grupos", Toast.LENGTH_SHORT).show()
                else aplicarToggleGruposMedidaPlanchas(elegidos)
            }
            .setNeutralButton("Editar medidas") { _, _ ->
                val elegidos = gruposElegidos()
                if (elegidos.isEmpty()) Toast.makeText(this, "No seleccionaste grupos", Toast.LENGTH_SHORT).show()
                else mostrarDialogoEditarGruposPlanchas(elegidos)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Edición en masa de los grupos elegidos: sumar/restar al ancho, alto y/o cantidad. */
    private fun mostrarDialogoEditarGruposPlanchas(grupos: List<GrupoMedidaIgualPlancha>) {
        val pad = (16 * resources.displayMetrics.density).toInt()
        val cont = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(pad, pad / 2, pad, 0)
        }
        fun fila(label: String, decimal: Boolean): android.widget.EditText {
            val row = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
            }
            row.addView(android.widget.TextView(this).apply {
                text = label
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            })
            val et = android.widget.EditText(this).apply {
                hint = "0"
                inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_SIGNED or
                    (if (decimal) android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL else 0)
                layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }
            row.addView(et)
            cont.addView(row)
            return et
        }
        val etDAncho = fila("Ancho ±", true)
        val etDAlto = fila("Alto ±", true)
        val etDCant = fila("Cantidad ±", false)

        AlertDialog.Builder(this)
            .setTitle("Editar ${grupos.sumOf { it.indices.size }} filas")
            .setView(cont)
            .setPositiveButton("Aplicar") { _, _ ->
                val dAncho = etDAncho.text?.toString()?.trim()?.replace(",", ".")?.toFloatOrNull() ?: 0f
                val dAlto = etDAlto.text?.toString()?.trim()?.replace(",", ".")?.toFloatOrNull() ?: 0f
                val dCant = etDCant.text?.toString()?.trim()?.toIntOrNull() ?: 0
                if (dAncho == 0f && dAlto == 0f && dCant == 0) {
                    Toast.makeText(this, "Sin cambios", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                grupos.forEach { g ->
                    g.indices.forEach { i ->
                        val item = lista[i]
                        lista[i] = item.copy(
                            ancho = (item.ancho + dAncho).coerceAtLeast(0.1f),
                            alto = (item.alto + dAlto).coerceAtLeast(0.1f),
                            cantidad = (item.cantidad + dCant).coerceAtLeast(1)
                        )
                    }
                }
                actualizarListaItems()
                dataManager.guardarItems(lista)
                Toast.makeText(this, "Medidas actualizadas", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun aplicarToggleGruposMedidaPlanchas(grupos: List<GrupoMedidaIgualPlancha>) {
        grupos.forEach { g ->
            // Si ninguna está activa, activar todo el grupo; si hay alguna activa, desactivar.
            val activar = g.indices.none { lista[it].activo }
            g.indices.forEach { i -> lista[i] = lista[i].copy(activo = activar) }
        }
        actualizarListaItems()
        dataManager.guardarItems(lista)
    }

    private fun actualizarListaPlanchas() {
        binding.listaPerfil.adapter = PlanchaDispAdapter(this, lista2, formatter)
    }

    private fun compartirContextoPlanchas() {
        val texto = crearTextoCompartirPlanchas()
        if (texto.isBlank()) {
            Toast.makeText(this, "No hay medidas para compartir", Toast.LENGTH_SHORT).show()
            return
        }

        val proyecto = proyectoOptimizadorActual.ifBlank { nombreListaActual }.ifBlank { "planchas" }
        val nombreArchivo = "Planchas_${sanitizarNombreArchivo(proyecto)}_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        }.$EXTENSION_PLANCHA_CRYSTAL"
        val shareDir = File(cacheDir, "planchasshare").apply { mkdirs() }
        val file = File(shareDir, nombreArchivo)

        runCatching {
            file.writeText(construirPaquetePlanchaCrystal(texto, proyecto).toString(2))
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val intent = Intent(this, ListChatActivity::class.java).apply {
                putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_URI, uri.toString())
                putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_NAME, file.name)
                putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_MIME, MIME_PLANCHA_CRYSTAL)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                clipData = ClipData.newUri(contentResolver, "plancha_crystal", uri)
            }
            startActivity(intent)
        }.onFailure {
            Toast.makeText(this, "No se pudo compartir formato Crystal: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarMenuMensajeriaPlanchas() {
        android.widget.PopupMenu(this, binding.imageButton).apply {
            menu.add(0, 1, 0, "Ir a chat")
            menu.add(0, 2, 1, "Enviar formato Crystal")
            menu.add(0, 3, 2, "Enviar medidas como texto")
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        startActivity(Intent(this@OptimizacionPlanchasActivity, ListChatActivity::class.java))
                        true
                    }
                    2 -> { compartirContextoPlanchas(); true }
                    3 -> { enviarMedidasTextoPlanchas(); true }
                    else -> false
                }
            }
            show()
        }
    }

    // Envía las medidas ACTIVAS como TEXTO por la mensajería de Crystal (queda como mensaje, para
    // copiar y pegar). Solo va a Crystal, no al compartir general de Android.
    private fun enviarMedidasTextoPlanchas() {
        val texto = crearTextoMedidasActivasPlanchas()
        if (texto.isBlank()) {
            Toast.makeText(this, "No hay medidas activas para enviar", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, ListChatActivity::class.java).apply {
            putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_TEXT, texto)
        }
        startActivity(intent)
    }

    /**
     * Texto simple de las medidas ACTIVAS para copiar/pegar. Encabeza con el material (nombre de la
     * plancha activa) y una línea por vidrio: "ancho x alto = cantidad info".
     *
     * Ejemplo:
     *   bronce Polarizado
     *   67.4 x 153.4 = 2 Adan,Vna1
     *   66.4 x 107 = 3 Adan,Vna2
     */
    private fun crearTextoMedidasActivasPlanchas(): String {
        val vidrios = soloPiezasPlanchas(lista).filter { it.activo }
        if (vidrios.isEmpty()) return ""
        // El encabezado es el nombre de la lista cargada (p. ej. "Vidrios [arenado]"), que ya trae
        // el tipo de vidrio. Solo si no hay lista/proyecto se usa el nombre de la plancha.
        val material = nombreListaActual
            .ifBlank { proyectoOptimizadorActual }
            .ifBlank { (lista2.firstOrNull { it.activa } ?: lista2.firstOrNull())?.nombre?.trim().orEmpty() }
        return buildString {
            if (material.isNotBlank()) appendLine(material)
            vidrios.forEach { item ->
                appendLine("${formatter.df1(item.ancho)} x ${formatter.df1(item.alto)} = ${item.cantidad} ${item.info}")
            }
        }.trim()
    }

    /** Carpeta pública Descargas/Crystal/svc (con reserva al almacenamiento de la app). */
    private fun carpetaSvc(): File {
        val publico = File(
            android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS),
            "Crystal/svc"
        )
        return if (publico.exists() || publico.mkdirs()) publico
        else File(getExternalFilesDir(null), "Crystal/svc").apply { mkdirs() }
    }

    // Descarga la lista de cortes activa como CSV en Descargas/Crystal/svc para cargarla en otras
    // apps de optimización. Una pieza por línea: alto,ancho,cantidad,etiqueta,seleccionado
    // (seleccionado = 1 si la pieza está activa, 0 si no).
    private fun descargarSvcPlanchas() {
        val piezas = soloPiezasPlanchas(lista)
        if (piezas.isEmpty()) {
            Toast.makeText(this, "No hay medidas para descargar", Toast.LENGTH_SHORT).show()
            return
        }
        val csv = buildString {
            piezas.forEach { item ->
                append(formatter.df1(item.alto)).append(',')
                append(formatter.df1(item.ancho)).append(',')
                append(item.cantidad).append(',')
                append(item.info).append(',')
                append(if (item.activo) 1 else 0)
                append('\n')
            }
        }

        val proyecto = proyectoOptimizadorActual.ifBlank { nombreListaActual }.ifBlank { "planchas" }
        val nombreArchivo = "Planchas_${sanitizarNombreArchivo(proyecto)}_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        }.csv"
        val file = File(carpetaSvc(), nombreArchivo)

        runCatching {
            file.writeText(csv)
            // Registrar en MediaStore: sin esto el archivo existe en disco pero los exploradores
            // y apps que leen vía MediaStore (Files, Descargas) no lo muestran hasta un re-escaneo.
            android.media.MediaScannerConnection.scanFile(
                this, arrayOf(file.absolutePath), arrayOf("text/csv"), null
            )
            Toast.makeText(this, "Guardado en ${file.parentFile?.name}/${file.name}", Toast.LENGTH_LONG).show()
        }.onFailure {
            Toast.makeText(this, "No se pudo descargar SVC: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun construirPaquetePlanchaCrystal(texto: String, proyecto: String): JSONObject {
        val piezas = JSONArray().also { arr ->
            lista.forEach { item ->
                arr.put(
                    JSONObject()
                        .put("ancho", item.ancho)
                        .put("alto", item.alto)
                        .put("cantidad", item.cantidad)
                        .put("info", item.info)
                        .put("activa", item.activo)
                )
            }
        }
        val planchas = JSONArray().also { arr ->
            lista2.forEach { plancha ->
                arr.put(
                    JSONObject()
                        .put("ancho", plancha.ancho)
                        .put("alto", plancha.alto)
                        .put("cantidad", plancha.cantidad)
                        .put("nombre", plancha.nombre)
                        .put("activa", plancha.activa)
                )
            }
        }

        return JSONObject()
            .put("format", FORMAT_PLANCHA_CRYSTAL)
            .put("version", 1)
            .put("exportedAt", System.currentTimeMillis())
            .put("proyecto", proyecto)
            .put("notas", texto)
            .put("nivel", binding.sbNivel.progress)
            .put("espesorDiscoCm", espesorDiscoCm)
            .put("planchasOptimizador", JSONObject().put("piezas", piezas).put("planchas", planchas))
    }

    private fun sanitizarNombreArchivo(valor: String): String =
        valor.trim()
            .replace(Regex("[^A-Za-z0-9_-]+"), "_")
            .trim('_')
            .ifBlank { "planchas" }

    private fun manejarIntentEntrada(intent: Intent?) {
        if (intent == null) return
        val uri = when (intent.action) {
            Intent.ACTION_SEND -> obtenerStreamCompartido(intent)
            Intent.ACTION_VIEW -> intent.data
            else -> null
        } ?: return

        runCatching {
            val texto = contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }.orEmpty()
            val root = JSONObject(texto)
            if (root.optString("format") != FORMAT_PLANCHA_CRYSTAL) return
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.avanzadoActivo(),
                    "Importar una Plancha Crystal es una función de pago.")) return
            confirmarImportarPlanchaCrystal(root)
            intent.action = null
            intent.data = null
            intent.removeExtra(Intent.EXTRA_STREAM)
        }.onFailure {
            Toast.makeText(this, "No se pudo abrir Corte Plancha Crystal: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerStreamCompartido(intent: Intent): Uri? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(Intent.EXTRA_STREAM)
        }
    }

    private fun confirmarImportarPlanchaCrystal(root: JSONObject) {
        val proyecto = root.optString("proyecto").ifBlank { "planchas" }
        val data = root.optJSONObject("planchasOptimizador") ?: JSONObject()
        val piezas = data.optJSONArray("piezas")?.length() ?: 0
        val planchas = data.optJSONArray("planchas")?.length() ?: 0

        AlertDialog.Builder(this)
            .setTitle("Corte Plancha Crystal")
            .setMessage("Proyecto: $proyecto\nPiezas: $piezas\nPlanchas: $planchas\n\nDeseas abrir este corte?")
            .setPositiveButton("Abrir") { _, _ -> importarPlanchaCrystal(root) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun importarPlanchaCrystal(root: JSONObject) {
        runCatching {
            val proyecto = root.optString("proyecto").ifBlank { "Corte Plancha Crystal" }
            val data = root.optJSONObject("planchasOptimizador") ?: JSONObject()
            val piezasJson = data.optJSONArray("piezas") ?: JSONArray()
            val planchasJson = data.optJSONArray("planchas") ?: JSONArray()

            val piezas = mutableListOf<ItemListaPlanchas>()
            for (i in 0 until piezasJson.length()) {
                piezasJson.optJSONObject(i)?.toItemListaPlanchas()?.let { piezas.add(it) }
            }

            val planchas = mutableListOf<PlanchaDisponible>()
            for (i in 0 until planchasJson.length()) {
                planchasJson.optJSONObject(i)?.toPlanchaDisponible()?.let { planchas.add(it) }
            }

            if (piezas.isEmpty() && planchas.isEmpty()) {
                Toast.makeText(this, "El archivo no contiene corte de planchas", Toast.LENGTH_SHORT).show()
                return
            }

            lista = piezas
            lista2 = planchas
            espesorDiscoCm = root.optDouble("espesorDiscoCm", espesorDiscoCm.toDouble()).toFloat().coerceAtLeast(0f)
            binding.sbNivel.progress = root.optInt("nivel", binding.sbNivel.progress).coerceIn(0, binding.sbNivel.max)
            binding.tvNivel.text = nivelTexto(binding.sbNivel.progress)
            proyectoOptimizadorActual = ""
            nombreListaActual = proyecto
            configurarEntradaPlanchas()
            actualizarListaItems()
            actualizarListaPlanchas()
            dataManager.guardarDatos(lista, lista2)
            dataManager.guardarEspesorDisco(espesorDiscoCm)
            actualizarTituloProyectoOptimizador()
            Toast.makeText(this, "Corte Plancha Crystal abierto", Toast.LENGTH_SHORT).show()
        }.onFailure {
            Toast.makeText(this, "No se pudo importar corte de planchas: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun JSONObject.toItemListaPlanchas(): ItemListaPlanchas? {
        val ancho = optDouble("ancho", Double.NaN).takeIf { !it.isNaN() }?.toFloat() ?: return null
        val alto = optDouble("alto", Double.NaN).takeIf { !it.isNaN() }?.toFloat() ?: return null
        val cantidad = optInt("cantidad", 0)
        val info = optString("info")
        if (ancho <= 0f || alto <= 0f || cantidad <= 0 || info.isBlank()) return null
        return ItemListaPlanchas(ancho = ancho, alto = alto, cantidad = cantidad, info = info, activo = optBoolean("activa", true))
    }

    private fun JSONObject.toPlanchaDisponible(): PlanchaDisponible? {
        val ancho = optDouble("ancho", Double.NaN).takeIf { !it.isNaN() }?.toFloat() ?: return null
        val alto = optDouble("alto", Double.NaN).takeIf { !it.isNaN() }?.toFloat() ?: return null
        val cantidad = optInt("cantidad", 0)
        val nombre = optString("nombre")
        if (ancho <= 0f || alto <= 0f || cantidad <= 0 || nombre.isBlank()) return null
        return PlanchaDisponible(ancho, alto, cantidad, nombre, optBoolean("activa", true))
    }

    private fun crearTextoCompartirPlanchas(): String {
        val ancho = binding.etMedida.text?.toString()?.trim().orEmpty()
        val alto = binding.etAlto.text?.toString()?.trim().orEmpty()
        val cantidad = binding.etCant.text?.toString()?.trim().orEmpty()
        val referencia = binding.etRefe.text?.toString()?.trim().orEmpty()
        val hayFormulario = ancho.isNotEmpty() || alto.isNotEmpty() || cantidad.isNotEmpty() || referencia.isNotEmpty()

        if (lista.isEmpty() && lista2.isEmpty() && !hayFormulario) return ""

        return buildString {
            appendLine("Crystal - Optimizacion de corte de planchas")
            val proyecto = proyectoOptimizadorActual.ifBlank { nombreListaActual }
            if (proyecto.isNotBlank()) appendLine("Proyecto: $proyecto")
            appendLine("Nivel: ${nivelTexto(binding.sbNivel.progress)}")
            appendLine("Espesor disco: ${formatter.df1(espesorDiscoCm)} cm")

            if (hayFormulario) {
                appendLine()
                appendLine("Medida en formulario:")
                appendLine("Ancho: ${ancho.ifBlank { "-" }} cm")
                appendLine("Alto: ${alto.ifBlank { "-" }} cm")
                appendLine("Cantidad: ${cantidad.ifBlank { "-" }}")
                appendLine("Referencia: ${referencia.ifBlank { "-" }}")
            }

            val vidrios = soloPiezasPlanchas(lista)

            if (vidrios.isNotEmpty()) {
                appendLine()
                appendLine("Vidrios:")
                vidrios.forEachIndexed { index, item ->
                    val estado = if (item.activo) "" else " [inactivo]"
                    appendLine("${index + 1}. ${formatter.df1(item.ancho)} x ${formatter.df1(item.alto)} = ${item.cantidad} (${item.info})$estado")
                }
            }

            if (lista2.isNotEmpty()) {
                appendLine()
                appendLine("Planchas y retazos:")
                lista2.forEachIndexed { index, plancha ->
                    val estado = if (plancha.activa) "" else " [inactiva]"
                    appendLine("${index + 1}. ${plancha.nombre}: ${formatter.df1(plancha.ancho)} x ${formatter.df1(plancha.alto)} cm, cant ${plancha.cantidad}$estado")
                }
            }
        }.trim()
    }

    // Recibe medidas de vidrio (ya convertidas a cm) desde MainActivity. Si ya hay una lista cargada
    // (queda guardada en preferences entre sesiones), pregunta qué hacer en vez de sumar en silencio:
    // sumar, reemplazar, o revisar antes lo que hay.
    private fun cargarPiezasDesdeIntent(intent: Intent?) {
        val json = intent?.getStringExtra("piezas_planchas_json") ?: return
        val entrantes = runCatching { leerPiezasEntrantes(json) }.getOrElse {
            Toast.makeText(this, "No se pudieron cargar las medidas: ${it.message}", Toast.LENGTH_SHORT).show()
            return
        }
        if (entrantes.isEmpty()) return
        intent.removeExtra("piezas_planchas_json")

        if (lista.isEmpty()) {
            aplicarPiezasEntrantes(entrantes, reemplazar = false)
            return
        }
        preguntarComoCargarPiezas(entrantes)
    }

    private fun leerPiezasEntrantes(json: String): List<ItemListaPlanchas> {
        val arr = JSONArray(json)
        val piezas = mutableListOf<ItemListaPlanchas>()
        for (idx in 0 until arr.length()) {
            val o = arr.optJSONObject(idx) ?: continue
            val ancho = o.optDouble("a", 0.0).toFloat()
            val alto = o.optDouble("h", 0.0).toFloat()
            if (ancho <= 0f || alto <= 0f) continue
            piezas.add(
                ItemListaPlanchas(
                    ancho = ancho,
                    alto = alto,
                    cantidad = o.optInt("c", 1).coerceAtLeast(1),
                    info = o.optString("r", "-")
                )
            )
        }
        return piezas
    }

    private fun preguntarComoCargarPiezas(entrantes: List<ItemListaPlanchas>) {
        val uniActual = lista.sumOf { it.cantidad }
        val uniEntrantes = entrantes.sumOf { it.cantidad }
        // Todo el detalle va en el título y en las opciones: un AlertDialog con setMessage descarta
        // la lista de setItems, así que no se pueden usar los dos a la vez.
        val opciones = arrayOf(
            "Sumar a lo que hay  →  ${lista.size + entrantes.size} filas",
            "Reemplazar la lista actual  →  ${entrantes.size} filas",
            "Ver la lista actual"
        )
        AlertDialog.Builder(this)
            .setTitle(
                "Llegan ${entrantes.size} fila(s) / $uniEntrantes uni\n" +
                    "Ya hay ${lista.size} fila(s) / $uniActual uni"
            )
            .setItems(opciones) { _, cual ->
                when (cual) {
                    0 -> aplicarPiezasEntrantes(entrantes, reemplazar = false)
                    1 -> confirmarReemplazarLista(entrantes)
                    2 -> mostrarListaActual { preguntarComoCargarPiezas(entrantes) }
                }
            }
            .setNegativeButton("Descartar lo que llega", null)
            .show()
    }

    private fun confirmarReemplazarLista(entrantes: List<ItemListaPlanchas>) {
        AlertDialog.Builder(this)
            .setTitle("Reemplazar la lista")
            .setMessage("Se borran las ${lista.size} fila(s) que hay ahora y quedan solo las ${entrantes.size} que llegan.")
            .setPositiveButton("Reemplazar") { _, _ ->
                aplicarPiezasEntrantes(entrantes, reemplazar = true)
            }
            .setNegativeButton("Volver") { _, _ -> preguntarComoCargarPiezas(entrantes) }
            .show()
    }

    /** Muestra las medidas que hay ahora en la lista, para decidir con la información a la vista. */
    private fun mostrarListaActual(alCerrar: () -> Unit) {
        val filas = lista.map { item ->
            val estado = if (item.activo) "" else "  [inactiva]"
            "${item.textoMostrar(formatter)}$estado"
        }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Lista actual (${lista.size} filas, ${lista.sumOf { it.cantidad }} uni)")
            .setItems(filas, null)
            .setPositiveButton("Volver") { _, _ -> alCerrar() }
            .setOnCancelListener { alCerrar() }
            .show()
    }

    private fun aplicarPiezasEntrantes(entrantes: List<ItemListaPlanchas>, reemplazar: Boolean) {
        if (reemplazar) lista.clear()
        lista.addAll(entrantes)
        dataManager.guardarItems(lista)
        actualizarListaItems()
        val accion = if (reemplazar) "reemplazaron" else "cargaron"
        Toast.makeText(
            this,
            "Se $accion ${entrantes.size} medida(s) en planchas (cm)",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun limpiarCamposEntrada() {
        binding.etMedida.setText("")
        binding.etAlto.setText("")
        binding.etCant.setText("")
        binding.etRefe.setText("")
    }

    private fun nivelTexto(progress: Int) = when (progress) {
        0 -> "Rápido"
        2 -> "Profundo"
        else -> "Normal"
    }
}
