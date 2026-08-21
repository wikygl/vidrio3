package crystal.crystal.optimizadores.corte

import android.app.AlertDialog
import android.content.ClipData
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.gson.Gson
import crystal.crystal.R
import crystal.crystal.databinding.ActivityCorteBinding
import crystal.crystal.red.ListChatActivity
import crystal.crystal.red.interop.ChatInteropIntents
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Activity principal refactorizada - delega responsabilidades a clases especializadas
 * Mantiene exactamente la misma funcionalidad que el código original
 */
class CorteActivity: AppCompatActivity() {

    companion object {
        const val MIME_CORTE_CRYSTAL = "application/vnd.crystal.corte+json"
        const val EXTENSION_CORTE_CRYSTAL = "crystalcorte"
        const val FORMAT_CORTE_CRYSTAL = "crystal.corte"
    }

    private var lista = mutableListOf<PiezaCorte>()
    private var lista2 = mutableListOf<PiezaCorte>()

    private lateinit var binding: ActivityCorteBinding

    // Clases especializadas
    private lateinit var dataManager: CorteDataManager
    private lateinit var optimizer: CorteOptimizer
    private lateinit var listManager: CorteListManager
    private lateinit var formatter: CorteFormatter

    private var ultimasEstadisticas: AnalisisMejoras.EstadisticasOptimizacion? = null
    private var nombreListaActual: String = ""
    private var proyectoOptimizadorActual: String = ""
    private var bloqueandoCargaSpinner = false
    private var spinnerConProyectosOptimizador = false
    private var spinnerProyectoKeys: List<String> = emptyList()

    private data class ProyectoCorte(
        val piezas: MutableList<PiezaCorte>,
        val varillas: MutableList<PiezaCorte>,
        val resultado: String,
        val grosorDisco: String,
        val nivel: Int
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCorteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar clases especializadas
        dataManager = CorteDataManager(this)
        optimizer = CorteOptimizer()
        listManager = CorteListManager(this)
        formatter = CorteFormatter(this)

        // NUEVO: Recuperar estadísticas anteriores
        ultimasEstadisticas = recuperarEstadisticasParaComparacion()

        // Inicializamos los métodos para mostrar diálogos y actualizar listas
        recuperarDatos()
        mostrarDialogo()
        mostrarDialogo2()
        actualizar()
        actualizar2()
        actualizarLabelEscala()
        manejarIntentEntrada(intent)
        cargarPiezasDesdeIntent(intent)

        binding.etMedida.requestFocus()
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

        binding.tvNivel.text = (binding.sbNivel.progress + 1).toString()
        binding.sbNivel.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.tvNivel.text = (progress + 1).toString()
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        binding.btAnadir.setOnClickListener {
            try {
                anadir()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al agregar pieza: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.btAnadir.setOnLongClickListener {
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.avanzadoActivo(),
                    "Cargar listas guardadas es una función de pago.")) return@setOnLongClickListener true
            // Llamar a la función para poblar el Spinner usando la clase especializada
            if (listManager.hayListasDisponibles()) {
                spinnerConProyectosOptimizador = false
                spinnerProyectoKeys = emptyList()
                listManager.poblarSpinnerConDatosGuardados(binding.spCortes)
            }
            true
        }

        binding.btLimpiar.setOnClickListener {
            limpiarPiezas()
        }

        // CORREGIDO: Método de optimización
        binding.btOpti.setOnClickListener {
            // Optimizar con medidas ingresadas a mano es GRATIS. Lo de pago es importar/cargar/guardar
            // piezas (proyectos guardados, listas guardadas, entrada por intent), gateado en su punto.
            try {
                // Filtrar solo las piezas que están marcadas como true (entran en optimización)
                val piezasParaOptimizar = lista.filter { it.cortada }
                val varillasDisponibles = lista2.filter { it.cortada }

                if (piezasParaOptimizar.isEmpty()) {
                    Toast.makeText(this, "No hay piezas seleccionadas para optimizar", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // CORREGIDO: Mostrar que está "pensando" sin navegar aún
                mostrarIndicadorPensamiento(piezasParaOptimizar.size)

                val grosorDisco = binding.etGrosor.text.toString().toFloatOrNull()
                    ?: binding.etGrosor.hint.toString().toFloat()

                // DEBUG: Log de entrada
                DebugHelper.logPiezasRequeridas(piezasParaOptimizar)

                // CORREGIDO: Ejecutar optimización en hilo separado
                Thread {
                    try {
                        val tiempoInicio = System.currentTimeMillis()

                        val nivel = binding.sbNivel.progress + 1
                        val resultadoOptimizado = optimizer.optimizarCortesConConfiguracion(
                            piezasParaOptimizar,
                            varillasDisponibles,
                            grosorDisco,
                            nivel
                        )

                        val tiempoTotal = System.currentTimeMillis() - tiempoInicio

                        // DEBUG: Log del resultado de optimización
                        DebugHelper.logVarillasUsadas(resultadoOptimizado)

                        // CORREGIDO: Volver al hilo principal para actualizar UI Y NAVEGAR
                        runOnUiThread {
                            ocultarIndicadorPensamiento()
                            // AHORA SÍ navegar con el resultado correcto
                            mostrarResultadoOptimizadoCorregido(resultadoOptimizado, piezasParaOptimizar, tiempoTotal)
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            ocultarIndicadorPensamiento()
                            Toast.makeText(this@CorteActivity, "Error en optimización: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }.start()

            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        binding.btOpti.setOnLongClickListener {
            compartirContextoCorte()
            true
        }

        // Botón de mensajería (cabecera): desplegable con ir a chat / enviar Crystal / enviar texto.
        binding.imageButton.setOnClickListener { mostrarMenuMensajeriaCorte() }

        binding.tvResultado.setOnClickListener {
            abrirUltimoResultadoGuardado(mostrarAvisoSiNoExiste = true)
        }

        binding.btAjustes.setOnClickListener {
            mostrarMenuAjustes()
        }

        binding.btAgregar.setOnClickListener {
            abrirDialogoCortes()
        }

        binding.btEliminar.setOnClickListener {
            limpiarVarillas()
        }

        binding.spCortes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (bloqueandoCargaSpinner) return
                val nombreSeleccionado = parent.getItemAtPosition(position).toString()
                if (spinnerConProyectosOptimizador) {
                    val nombreProyecto = spinnerProyectoKeys.getOrNull(position) ?: nombreSeleccionado
                    abrirProyectoOptimizador(nombreProyecto, actualizarSpinner = false)
                } else {
                    cargarListaSeleccionada(nombreSeleccionado)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

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
                        nombreListaActual = elegidas.joinToString(" + ")
                        actualizar()
                        dataManager.guardarPiezas(lista)
                        Toast.makeText(this, "${elegidas.size} listas unidas — ${combinadas.size} piezas", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
            true
        }

        mostrarAvisoResultadoGuardado()
    }

    override fun onResume() {
        super.onResume()
        // NUEVO: Procesar descuentos de cortes ejecutados
        procesarDescuentosAutomaticos()
        // Verificar si hay varillas cortadas y marcar las piezas correspondientes (funcionalidad existente)
        verificarVarillasCortadas()
    }

    override fun onPause() {
        super.onPause()
        guardarDatos() // Guardar datos al pausar la actividad
    }

    private fun mostrarAvisoResultadoGuardado() {
        val resultado = dataManager.recuperarResultadoOptimizacion() ?: return
        if (resultado.varillasUsadas.isEmpty()) return

        val nombre = dataManager.recuperarNombreListaResultadoOptimizacion()
        val detalle = if (nombre.isBlank()) {
            "${resultado.totalBarrasUsadas} barras, ${resultado.totalCortes} cortes."
        } else {
            "$nombre\n${resultado.totalBarrasUsadas} barras, ${resultado.totalCortes} cortes."
        }

        AlertDialog.Builder(this)
            .setTitle("Resultado guardado")
            .setMessage("Hay una optimizacion anterior disponible.\n\n$detalle\n\nDeseas abrirla?")
            .setPositiveButton("Abrir") { _, _ -> abrirUltimoResultadoGuardado() }
            .setNegativeButton("Ahora no", null)
            .show()
    }

    private fun abrirUltimoResultadoGuardado(mostrarAvisoSiNoExiste: Boolean = false) {
        val resultado = dataManager.recuperarResultadoOptimizacion()
        if (resultado == null || resultado.varillasUsadas.isEmpty()) {
            if (mostrarAvisoSiNoExiste) {
                Toast.makeText(this, "No hay resultado guardado", Toast.LENGTH_SHORT).show()
            }
            return
        }

        startActivity(Intent(this, ResultadoOptimizacionActivity::class.java).apply {
            putExtra("resultado_optimizacion", resultado)
            putExtra("nombre_lista", dataManager.recuperarNombreListaResultadoOptimizacion())
        })
    }

    // === NUEVAS FUNCIONES PARA DESCUENTOS AUTOMÁTICOS ===

    /**
     * Procesa los descuentos automáticos basados en los cortes ejecutados
     */
    private fun mostrarMenuAjustes() {
        PopupMenu(this, binding.btAjustes).apply {
            menu.add("Seleccionar medidas iguales")
            menu.add("Escala de medida")
            setOnMenuItemClickListener { item ->
                when (item.title.toString()) {
                    "Seleccionar medidas iguales" -> {
                        mostrarDialogoMedidasIguales()
                        true
                    }
                    "Escala de medida" -> {
                        mostrarDialogoEscala()
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun mostrarDialogoEscala() {
        val escalas = EscalaCorte.entries.toTypedArray()
        val nombres = escalas.map { it.etiqueta }.toTypedArray()
        var seleccion = escalas.indexOf(formatter.escala).coerceAtLeast(0)
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Escala (actual: ${formatter.escala.sigla})")
            .setSingleChoiceItems(nombres, seleccion) { _, which -> seleccion = which }
            .setPositiveButton("Cambiar de escala") { _, _ -> cambiarEscala(escalas[seleccion]) }
            .setNeutralButton("Elegir escala") { _, _ -> reinterpretarEscala(escalas[seleccion]) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // "Cambiar de escala": cambia la unidad y CONVIERTE conservando el tamaño real (200 cm → 2 m).
    private fun cambiarEscala(nueva: EscalaCorte) {
        formatter.escala = nueva
        EscalaCorte.guardar(this, nueva)
        actualizarLabelEscala()
        actualizar()
        actualizar2()
        Toast.makeText(this, "Escala: ${nueva.etiqueta} (tamaño conservado)", Toast.LENGTH_SHORT).show()
    }

    // "Elegir escala": REINTERPRETA. Detecta la escala actual y mantiene el número, cambiando la unidad
    // (200 cm → 200 m). Recalcula los valores almacenados (en cm).
    private fun reinterpretarEscala(nueva: EscalaCorte) {
        val anterior = formatter.escala
        if (nueva == anterior) { cambiarEscala(nueva); return }
        val factor = nueva.factorACm / anterior.factorACm
        for (i in lista.indices) lista[i] = lista[i].copy(longitud = lista[i].longitud * factor)
        for (i in lista2.indices) lista2[i] = lista2[i].copy(longitud = lista2[i].longitud * factor)
        formatter.escala = nueva
        EscalaCorte.guardar(this, nueva)
        dataManager.guardarPiezas(lista)
        dataManager.guardarVarillas(lista2)
        actualizarLabelEscala()
        actualizar()
        actualizar2()
        Toast.makeText(this, "Reinterpretado a ${nueva.etiqueta} (número conservado)", Toast.LENGTH_SHORT).show()
    }

    private fun actualizarLabelEscala() {
        binding.tvMedidasCant.text = "${getString(R.string.medidas_y_cantidades)} (${formatter.escala.sigla})"
    }

    private data class GrupoMedidaIgual(
        val longitud: Float,
        val indices: List<Int>
    )

    private fun mostrarDialogoMedidasIguales() {
        val grupos = lista.indices
            .groupBy { claveMedida(lista[it].longitud) }
            .mapNotNull { (_, indices) ->
                if (indices.size < 2) return@mapNotNull null
                GrupoMedidaIgual(
                    longitud = lista[indices.first()].longitud,
                    indices = indices
                )
            }
            .sortedBy { it.longitud }

        if (grupos.isEmpty()) {
            Toast.makeText(this, "No hay grupos de medidas iguales", Toast.LENGTH_SHORT).show()
            return
        }

        val etiquetasGrupos = grupos.map { grupo ->
            val cantidadPiezas = grupo.indices.size
            val cantidadUnidades = grupo.indices.sumOf { lista[it].cantidad }
            val estado = when {
                grupo.indices.all { lista[it].cortada } -> "activas"
                grupo.indices.none { lista[it].cortada } -> "inactivas"
                else -> "mixtas"
            }
            "${formatter.mostrarConSigla(grupo.longitud)} - $cantidadPiezas filas, $cantidadUnidades uni ($estado)"
        }
        val etiquetas = listOf("Todos los grupos") + etiquetasGrupos
        val seleccionados = BooleanArray(etiquetas.size)

        AlertDialog.Builder(this)
            .setTitle("Medidas iguales")
            .setMultiChoiceItems(etiquetas.toTypedArray(), seleccionados) { _, which, checked ->
                seleccionados[which] = checked
                if (which == 0) {
                    for (i in 1 until seleccionados.size) seleccionados[i] = checked
                }
            }
            .setPositiveButton("Aplicar") { _, _ ->
                val gruposElegidos = if (seleccionados.firstOrNull() == true) {
                    grupos
                } else {
                    grupos.filterIndexed { index, _ -> seleccionados[index + 1] }
                }

                if (gruposElegidos.isEmpty()) {
                    Toast.makeText(this, "No seleccionaste grupos", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                aplicarToggleGruposMedida(gruposElegidos)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun aplicarToggleGruposMedida(grupos: List<GrupoMedidaIgual>) {
        var filasAfectadas = 0
        grupos.forEach { grupo ->
            val activar = grupo.indices.none { lista[it].cortada }
            grupo.indices.forEach { index ->
                lista[index] = lista[index].copy(cortada = activar)
                filasAfectadas++
            }
        }

        actualizar()
        dataManager.guardarPiezas(lista)
        Toast.makeText(this, "Grupos actualizados: ${grupos.size}. Filas: $filasAfectadas", Toast.LENGTH_SHORT).show()
    }

    private fun claveMedida(longitud: Float): Int =
        (longitud * 1000f).roundToInt()

    private fun procesarDescuentosAutomaticos() {
        val cortesEjecutados = dataManager.recuperarCortesEjecutados()

        if (cortesEjecutados.isNotEmpty()) {
            DebugHelper.logInicioDescuentos()
            DebugHelper.logCortesEjecutados(cortesEjecutados)

            // Procesar cada varilla cortada
            cortesEjecutados.forEach { varillaCortada ->
                procesarDescuentosDeVarilla(varillaCortada)
            }

            // Limpiar piezas con cantidad 0
            lista.removeAll { it.cantidad <= 0 }

            // DEBUG: Log de la lista después de descuentos
            DebugHelper.logListaDespuesDescuento(lista)

            // Actualizar la UI
            actualizar()
            dataManager.guardarPiezas(lista)

            // Limpiar los cortes ejecutados ya procesados
            dataManager.limpiarCortesEjecutados()

            // Mostrar mensaje informativo
            val totalCortes = cortesEjecutados.sumOf { it.cortesEjecutados.sumOf { corte -> corte.cantidad } }
            DebugHelper.logFinDescuentos(totalCortes)
            Toast.makeText(this, "Se descontaron $totalCortes cortes ejecutados", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Procesa los descuentos de una varilla específica
     */
    private fun procesarDescuentosDeVarilla(varillaCortada: VarillaCortada) {
        varillaCortada.cortesEjecutados.forEach { corteEjecutado ->
            descontarPieza(corteEjecutado.longitud, corteEjecutado.referencia, corteEjecutado.cantidad)
        }
    }

    /**
     * Descuenta una cantidad específica de una pieza por longitud y referencia
     */
    private fun descontarPieza(longitud: Float, referencia: String, cantidadADescontar: Int) {
        // Buscar la pieza que coincida con longitud y referencia
        val piezaIndex = lista.indexOfFirst { pieza ->
            pieza.longitud == longitud && pieza.referencia == referencia
        }

        if (piezaIndex != -1) {
            val piezaActual = lista[piezaIndex]
            val nuevaCantidad = maxOf(0, piezaActual.cantidad - cantidadADescontar)

            // Actualizar la pieza con la nueva cantidad
            lista[piezaIndex] = piezaActual.copy(cantidad = nuevaCantidad)

            // Debug: Log del descuento
            DebugHelper.logDescuento(longitud, referencia, piezaActual.cantidad, cantidadADescontar, nuevaCantidad)
        } else {
            // Si no se encuentra la pieza exacta, buscar por referencia solamente
            descontarPorReferenciaSolamente(referencia, cantidadADescontar)
        }
    }

    /**
     * Descuenta por referencia cuando no se encuentra coincidencia exacta de longitud
     */
    private fun descontarPorReferenciaSolamente(referencia: String, cantidadADescontar: Int) {
        val piezasConReferencia = lista.filter { it.referencia == referencia }

        if (piezasConReferencia.isNotEmpty()) {
            var cantidadRestante = cantidadADescontar

            // Descontar desde la primera pieza que encuentre con esa referencia
            for (i in lista.indices) {
                if (cantidadRestante <= 0) break

                val pieza = lista[i]
                if (pieza.referencia == referencia) {
                    val descontarDeEstaPieza = minOf(cantidadRestante, pieza.cantidad)
                    val nuevaCantidad = pieza.cantidad - descontarDeEstaPieza

                    lista[i] = pieza.copy(cantidad = nuevaCantidad)
                    cantidadRestante -= descontarDeEstaPieza

                    // Debug: Log del descuento por referencia
                    DebugHelper.logDescuentoPorReferencia(pieza.longitud, referencia, pieza.cantidad, descontarDeEstaPieza, nuevaCantidad)
                }
            }
        }
    }

    // === FUNCIONES DE DATOS (delegadas a DataManager) ===

    private fun guardarDatos() {
        dataManager.guardarDatos(lista, lista2, binding.tvResultado.text.toString())
    }

    private fun recuperarDatos() {
        val (piezas, varillas, textoResultado) = dataManager.recuperarDatos()
        lista = piezas
        lista2 = varillas
        binding.tvResultado.setText(textoResultado)
    }

    private fun prefsProyectosOptimizador() =
        getSharedPreferences("OptimizadorProyectos", Context.MODE_PRIVATE)

    private fun nombresProyectosOptimizador(): MutableSet<String> {
        return prefsProyectosOptimizador()
            .getStringSet("corte_nombres", emptySet())
            ?.toMutableSet()
            ?: mutableSetOf()
    }

    private fun mostrarDialogoGuardarProyectoOptimizador() {
        val input = EditText(this).apply {
            hint = "Nombre del proyecto"
            setText(nombreListaActual.ifBlank { proyectoOptimizadorActual.ifBlank { "Corte perfiles" } })
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
        val proyecto = ProyectoCorte(
            piezas = lista,
            varillas = lista2,
            resultado = binding.tvResultado.text.toString(),
            grosorDisco = binding.etGrosor.text.toString(),
            nivel = binding.sbNivel.progress
        )
        val prefs = prefsProyectosOptimizador()
        val nombres = nombresProyectosOptimizador().apply { add(nombre) }
        prefs.edit()
            .putStringSet("corte_nombres", nombres)
            .putString("corte_$nombre", Gson().toJson(proyecto))
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
            val piezasLista = listManager.cargarLista(nombreLista).toMutableList()
            if (piezasLista.isEmpty()) return@forEach
            val paquete = paqueteDesdePiezas(piezasLista, nombre)
            if (paqueteGuardado.isBlank()) paqueteGuardado = paquete
            val nombreProyecto = nombreListaConPaquete(paquete, nombreLista, guardadas + 1)
            val proyecto = ProyectoCorte(
                piezas = piezasLista,
                varillas = lista2,
                resultado = "",
                grosorDisco = binding.etGrosor.text.toString(),
                nivel = binding.sbNivel.progress
            )
            nombresGuardados.add(nombreProyecto)
            prefs.edit()
                .putString("corte_$nombreProyecto", Gson().toJson(proyecto))
                .apply()
            guardadas++
        }

        if (guardadas == 0) {
            Toast.makeText(this, "No hay listas validas para guardar", Toast.LENGTH_SHORT).show()
            return
        }
        prefs.edit()
            .putStringSet("corte_nombres", nombresGuardados)
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
            editor.remove("corte_$nombre")
        }
        editor.putStringSet("corte_nombres", nombresActuales).apply()
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
            val json = prefsProyectosOptimizador().getString("corte_$nombre", null)
            try {
                if (json.isNullOrBlank()) null else Gson().fromJson(json, ProyectoCorte::class.java)
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
            lista = proyectos.flatMap { it.piezas }.toMutableList()
            lista2 = proyectos.first().varillas.toMutableList()
            binding.tvResultado.setText("")
            binding.etGrosor.setText(proyectos.first().grosorDisco)
            binding.sbNivel.progress = proyectos.first().nivel.coerceIn(0, binding.sbNivel.max)
            binding.tvNivel.text = (binding.sbNivel.progress + 1).toString()
            proyectoOptimizadorActual = paquete
            nombreListaActual = paquete
            actualizar()
            actualizar2()
            guardarDatos()
            actualizarTituloProyectoOptimizador()
            poblarSpinnerProyectosOptimizador(nombres)
        } finally {
            binding.spCortes.post { bloqueandoCargaSpinner = false }
        }
        Toast.makeText(this, "Paquete abierto: $paquete", Toast.LENGTH_SHORT).show()
    }

    private fun abrirProyectoOptimizador(nombre: String, actualizarSpinner: Boolean = true) {
        val json = prefsProyectosOptimizador().getString("corte_$nombre", null)
        if (json.isNullOrBlank()) {
            Toast.makeText(this, "No se pudo abrir el proyecto", Toast.LENGTH_SHORT).show()
            return
        }
        val proyecto = try {
            Gson().fromJson(json, ProyectoCorte::class.java)
        } catch (_: Exception) {
            null
        }
        if (proyecto == null) {
            Toast.makeText(this, "Proyecto invalido", Toast.LENGTH_SHORT).show()
            return
        }
        bloqueandoCargaSpinner = true
        try {
            lista = proyecto.piezas.toMutableList()
            lista2 = proyecto.varillas.toMutableList()
            binding.tvResultado.setText(proyecto.resultado)
            binding.etGrosor.setText(proyecto.grosorDisco)
            binding.sbNivel.progress = proyecto.nivel.coerceIn(0, binding.sbNivel.max)
            binding.tvNivel.text = (binding.sbNivel.progress + 1).toString()
            proyectoOptimizadorActual = nombre
            nombreListaActual = nombre
            actualizar()
            actualizar2()
            guardarDatos()
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

    private fun paqueteDesdePiezas(piezas: List<PiezaCorte>, fallback: String): String {
        return piezas.asSequence()
            .map { extraerPaqueteDesdeReferencia(it.referencia) }
            .firstOrNull { it.isNotBlank() }
            ?: fallback.trim().ifBlank { "Sin paquete" }
    }

    private fun extraerPaqueteDesdeReferencia(referencia: String): String {
        val limpio = referencia.trim().trim('(', ')')
        if (limpio.isBlank()) return ""
        val sinLista = quitarListaDesdeReferencia(limpio)
        val candidato = if (sinLista.contains(",")) {
            sinLista.substringAfterLast(",").trim().substringBefore(" ")
        } else {
            sinLista.substringAfterLast(" ", "").trim()
        }
        return candidato.trim().trim(')', '(')
    }

    private fun quitarListaDesdeReferencia(referencia: String): String {
        val partes = referencia.split(",").map { it.trim() }
        if (partes.size < 3) return referencia

        val listasConocidas = listManager.nombresListasDisponibles()
            .map { it.substringBefore("[").trim() }
            .filter { it.isNotBlank() }
            .toSet()
        val ultimaParte = partes.last()

        return if (ultimaParte in listasConocidas) {
            partes.dropLast(1).joinToString(", ")
        } else {
            referencia
        }
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
            "Corte Varillas"
        } else {
            "Corte Varillas - $proyectoOptimizadorActual"
        }
    }

    // === FUNCIONES DE FORMATO (delegadas a Formatter) ===

    private fun actualizar() {
        formatter.actualizarListaPiezas(lista, binding.listCorte)
    }

    private fun actualizar2() {
        formatter.actualizarListaVarillas(lista2, binding.listaPerfil)
    }

    // === FUNCIONES DE LÓGICA DE NEGOCIO ===

    private fun anadir() {
        val medi = formatter.convertirAFloat(binding.etMedida.text.toString())
        val cant = formatter.convertirAInt(binding.etCant.text.toString())
        val refe = binding.etRefe.text.toString()

        if (medi != null && cant != null && refe.isNotEmpty()) {
            lista.add(PiezaCorte(formatter.escala.aCm(medi), cant, refe, true)) // guardar en cm
            actualizar()
            dataManager.guardarPiezas(lista)

            binding.etMedida.setText("")
            binding.etCant.setText("")
            binding.etRefe.setText("")
            binding.etMedida.requestFocus()
        } else {
            Toast.makeText(this, "Olvidaste ingresar datos", Toast.LENGTH_LONG).show()
        }
    }

    private fun limpiarPiezas() {
        lista.clear()
        actualizar()
        dataManager.guardarPiezas(lista)

        binding.etMedida.setText("")
        binding.etCant.setText("")
        binding.etRefe.setText("")
        binding.etMedida.requestFocus()
    }

    private fun limpiarVarillas() {
        lista2.clear()
        actualizar2()
        dataManager.guardarVarillas(lista2)
    }

    // === NUEVAS FUNCIONES PARA INDICADOR DE PROGRESO ===

    /**
     * Muestra indicador de que el algoritmo está "pensando"
     */
    private fun mostrarIndicadorPensamiento(numeroPiezas: Int) {
        val tiempoEstimado = when {
            numeroPiezas <= 10 -> "2-3 segundos"
            numeroPiezas <= 20 -> "3-6 segundos"
            numeroPiezas <= 30 -> "5-8 segundos"
            else -> "8-15 segundos"
        }

        // Deshabilitar botón de optimizar
        binding.btOpti.isEnabled = false
        binding.btOpti.text = "🧠 Pensando..."

        // Mostrar Toast de inicio
        Toast.makeText(
            this,
            "🚀 Iniciando optimización exhaustiva\n" +
                    "⏱️ Tiempo estimado: $tiempoEstimado\n" +
                    "🔍 Explorando millones de combinaciones...",
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Oculta indicador de pensamiento
     */
    private fun ocultarIndicadorPensamiento() {
        binding.btOpti.isEnabled = true
        binding.btOpti.text = "Calcular" // Texto directo en lugar de string resource
    }

    // NUEVO: Método corregido que navega DESPUÉS de la optimización
    private fun mostrarResultadoOptimizadoCorregido(
        varillasUsadas: List<CorteOptimizer.VarillaConReferencias>,
        piezasSolicitadas: List<PiezaCorte>,
        tiempoOptimizacion: Long
    ) {
        // Generar resultado estructurado
        val resultadoBase = optimizer.generarResultadoEstructurado(varillasUsadas)
        val faltantes = calcularCortesFaltantes(piezasSolicitadas, resultadoBase)
        val resultadoEstructurado = resultadoBase.copy(
            cortesErroneos = faltantes.sumOf { it.cantidad },
            cortesFaltantes = faltantes
        )

        // DEBUG: Log del resultado estructurado
        DebugHelper.logResultadoEstructurado(resultadoEstructurado)

        // Guardar las varillas usadas
        dataManager.guardarVarillasUsadas(varillasUsadas)
        dataManager.guardarResultadoOptimizacion(resultadoEstructurado, nombreListaActual)

        // NUEVO: Mostrar análisis detallado con AnalisisMejoras
        mostrarAnalisisMejorasDetallado(resultadoEstructurado, tiempoOptimizacion)

        // AHORA SÍ navegar a la nueva Activity con el resultado final
        val intent = Intent(this, ResultadoOptimizacionActivity::class.java)
        intent.putExtra("resultado_optimizacion", resultadoEstructurado)
        intent.putExtra("nombre_lista", nombreListaActual)
        startActivity(intent)

        // También guardar como texto para mantener compatibilidad
        val textoResultado = generarTextoResultadoLegacy(varillasUsadas)
        binding.tvResultado.setText(textoResultado)
        dataManager.guardarResultado(textoResultado)
    }

    private fun calcularCortesFaltantes(
        piezasSolicitadas: List<PiezaCorte>,
        resultado: ResultadoOptimizacion
    ): List<CorteFaltante> {
        val cortados = mutableMapOf<String, Int>()
        resultado.varillasUsadas
            .flatMap { it.cortesConReferencias }
            .forEach { corte ->
                val key = claveCorte(corte.longitud, corte.referencia)
                cortados[key] = (cortados[key] ?: 0) + 1
            }

        return piezasSolicitadas.mapNotNull { pieza ->
            val cortadosDePieza = cortados[claveCorte(pieza.longitud, pieza.referencia)] ?: 0
            val faltan = pieza.cantidad - cortadosDePieza
            if (faltan > 0) {
                CorteFaltante(pieza.longitud, pieza.referencia, faltan)
            } else {
                null
            }
        }
    }

    private fun claveCorte(longitud: Float, referencia: String): String {
        val longitudNormalizada = (longitud * 1000f).roundToInt()
        return "$longitudNormalizada|${referencia.trim()}"
    }

    /**
     * NUEVO: Método que incluye análisis detallado con AnalisisMejoras
     */
    private fun mostrarAnalisisMejorasDetallado(resultado: ResultadoOptimizacion, tiempoMs: Long = 0) {
        // Convertir resultado a formato para AnalisisMejoras
        val resultadoAlgoritmo = convertirAResultadoAlgoritmo(resultado, tiempoMs)

        // Generar estadísticas actuales
        val estadisticasActuales = AnalisisMejoras.analizarResultados(resultadoAlgoritmo)

        // Generar reporte detallado
        val reporteDetallado = AnalisisMejoras.generarReporteDetallado(estadisticasActuales)

        // Si hay estadísticas anteriores, comparar
        val reporteComparacion = ultimasEstadisticas?.let { anteriores ->
            AnalisisMejoras.compararResultados(anteriores, estadisticasActuales)
        }

        // Calcular ahorro económico si hay estadísticas anteriores
        val reporteEconomico = ultimasEstadisticas?.let { anteriores ->
            AnalisisMejoras.calcularAhorroEconomico(anteriores, estadisticasActuales)
        }

        // Mostrar análisis completo en Toast (versión resumida)
        val tiempoTexto = if (tiempoMs > 0) {
            "⏱️ Tiempo: ${tiempoMs/1000.0}s\n"
        } else ""

        val mensaje = buildString {
            append("🎯 ¡Optimización completada!\n")
            append(tiempoTexto)
            append("🧠 Algoritmo: ${estadisticasActuales.algoritmoUsado}\n")
            append("📊 Eficiencia: ${String.format("%.1f", estadisticasActuales.eficienciaTotal)}%\n")
            append("📦 Varillas: ${estadisticasActuales.varillasUsadas}\n")
            append("🏆 Eficiencia alta (>90%): ${estadisticasActuales.varillasConEficienciaAlta}\n")
            append("♻️ Retazos: ${String.format("%.1f", estadisticasActuales.totalRetazos)}cm\n")

            // Agregar evaluación de calidad
            val evaluacion = when {
                estadisticasActuales.eficienciaTotal >= 95f -> "🏆 EXCELENTE"
                estadisticasActuales.eficienciaTotal >= 90f -> "🥇 MUY BUENA"
                estadisticasActuales.eficienciaTotal >= 85f -> "🥈 BUENA"
                estadisticasActuales.eficienciaTotal >= 80f -> "🥉 REGULAR"
                else -> "⚠️ MEJORABLE"
            }
            append("🏅 Calidad: $evaluacion")
        }

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()

        // Logs detallados para debugging
        Log.d("AnalisisMejoras", reporteDetallado)

        reporteComparacion?.let {
            Log.d("AnalisisMejoras", "=== COMPARACIÓN ===")
            Log.d("AnalisisMejoras", it)
        }

        reporteEconomico?.let {
            Log.d("AnalisisMejoras", "=== ANÁLISIS ECONÓMICO ===")
            Log.d("AnalisisMejoras", it)
        }

        // Guardar estadísticas actuales para próxima comparación
        ultimasEstadisticas = estadisticasActuales

        // Guardar las estadísticas en SharedPreferences para persistencia
        guardarEstadisticasParaComparacion(estadisticasActuales)
    }

    /**
     * Convierte ResultadoOptimizacion a formato CorteOptimizer.ResultadoAlgoritmo
     */
    private fun convertirAResultadoAlgoritmo(
        resultado: ResultadoOptimizacion,
        tiempoMs: Long
    ): CorteOptimizer.ResultadoAlgoritmo {

        // Convertir VarillaResultado a VarillaConReferencias
        val varillasUsadas = resultado.varillasUsadas.map { varillaResultado ->
            val varilla = CorteOptimizer.Varilla(
                longitud = varillaResultado.longitudVarilla,
                cortes = varillaResultado.cortes.toMutableList(),
                restante = varillaResultado.retazo
            )

            val referencias = varillaResultado.cortesConReferencias.map { it.referencia }.toMutableList()

            CorteOptimizer.VarillaConReferencias(varilla, referencias)
        }

        // Calcular eficiencia total
        val materialUsado = varillasUsadas.sumOf {
            (it.varilla.longitud - it.varilla.restante).toDouble()
        }.toFloat()
        val materialTotal = varillasUsadas.sumOf {
            it.varilla.longitud.toDouble()
        }.toFloat()

        val eficiencia = if (materialTotal > 0) {
            (materialUsado / materialTotal) * 100f
        } else 0f

        val totalRetazos = varillasUsadas.sumOf { it.varilla.restante.toDouble() }.toFloat()

        // Determinar el algoritmo usado basado en tiempo y calidad
        val algoritmoUsado = when {
            tiempoMs > 8000 -> "Optimización Exhaustiva Multi-Estrategia"
            tiempoMs > 3000 -> "Optimización Avanzada"
            eficiencia >= 90f -> "Algoritmo Inteligente"
            else -> "Algoritmo Estándar"
        }

        return CorteOptimizer.ResultadoAlgoritmo(
            varillasUsadas = varillasUsadas,
            porcentajeEficiencia = eficiencia,
            totalRetazos = totalRetazos,
            nombreAlgoritmo = algoritmoUsado
        )
    }

    /**
     * Guarda las estadísticas para comparación futura
     */
    private fun guardarEstadisticasParaComparacion(estadisticas: AnalisisMejoras.EstadisticasOptimizacion) {
        val gson = Gson()
        val estadisticasJson = gson.toJson(estadisticas)

        val sharedPref = getSharedPreferences("CortePreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("ultimas_estadisticas", estadisticasJson)
            apply()
        }
    }

    /**
     * Recupera las estadísticas guardadas para comparación
     */
    private fun recuperarEstadisticasParaComparacion(): AnalisisMejoras.EstadisticasOptimizacion? {
        val sharedPref = getSharedPreferences("CortePreferences", Context.MODE_PRIVATE)
        val estadisticasJson = sharedPref.getString("ultimas_estadisticas", null)

        return if (estadisticasJson != null) {
            try {
                val gson = Gson()
                gson.fromJson(estadisticasJson, AnalisisMejoras.EstadisticasOptimizacion::class.java)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    /**
     * Verifica qué varillas fueron cortadas y marca las piezas correspondientes
     */
    private fun verificarVarillasCortadas() {
        val varillasUsadas = dataManager.recuperarVarillasUsadas()
        val varillasEstadoCortadas = dataManager.recuperarEstadosVarillas()

        if (varillasUsadas.isEmpty() || varillasEstadoCortadas.isEmpty()) return

        // Para cada varilla que fue marcada como cortada
        varillasEstadoCortadas.forEachIndexed { index, cortada ->
            if (cortada && index < varillasUsadas.size) {
                val varillaUsada = varillasUsadas[index]

                // Marcar cada pieza de esta varilla como cortada = false (ya no entra en optimización)
                varillaUsada.referencias.forEach { referencia ->
                    lista.filter { pieza -> pieza.referencia == referencia }.forEach { pieza ->
                        pieza.cortada = false
                    }
                }
            }
        }

        // Actualizar la UI
        actualizar()
        dataManager.guardarPiezas(lista)
    }

    /**
     * Genera texto resultado para compatibilidad con versión anterior
     */
    private fun generarTextoResultadoLegacy(varillasUsadas: List<CorteOptimizer.VarillaConReferencias>): String {
        val resultadoString = StringBuilder()

        for ((index, varillaConRef) in varillasUsadas.withIndex()) {
            val varilla = varillaConRef.varilla
            val longitudFormateada = formatter.df1(varilla.longitud)
            val cortesFormateados = varilla.cortes.joinToString(", ") { formatter.df1(it) }
            val retazoFormateado = formatter.df1(varilla.restante)

            resultadoString.append("Varilla ${index + 1} (Longitud $longitudFormateada cm):\n")
            resultadoString.append("  Cortes: $cortesFormateados\n")
            resultadoString.append("  Retazo: $retazoFormateado cm\n\n")
        }

        return resultadoString.toString()
    }

    private fun cargarListaSeleccionada(nombreLista: String) {
        val listaSeleccionada = listManager.cargarLista(nombreLista)
        if (listaSeleccionada.isNotEmpty()) {
            lista.clear()
            lista.addAll(listaSeleccionada)
            nombreListaActual = nombreLista
            actualizar()
            dataManager.guardarPiezas(lista)
        }
    }

    private fun mostrarMenuMensajeriaCorte() {
        android.widget.PopupMenu(this, binding.imageButton).apply {
            menu.add(0, 1, 0, "Ir a chat")
            menu.add(0, 2, 1, "Enviar formato Crystal")
            menu.add(0, 3, 2, "Enviar medidas como texto")
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    1 -> {
                        startActivity(Intent(this@CorteActivity, ListChatActivity::class.java))
                        true
                    }
                    2 -> { compartirContextoCorte(); true }
                    3 -> { enviarMedidasTextoCorte(); true }
                    else -> false
                }
            }
            show()
        }
    }

    // Envía las medidas ACTIVAS como TEXTO por la mensajería de Crystal (queda como mensaje, para
    // copiar y pegar). Solo va a Crystal, no al compartir general de Android.
    private fun enviarMedidasTextoCorte() {
        val texto = crearTextoMedidasActivasCorte()
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
     * Texto simple de las piezas ACTIVAS para copiar/pegar. Encabeza con el nombre de la lista o
     * proyecto (que suele traer el perfil) y una línea por corte: "longitud = cantidad referencia".
     *
     * Ejemplo:
     *   Junkillo [madera]
     *   62.8 cm = 1 P1 Isabel Med,Ang.tope
     *   28 cm = 20 P1 Isabel Med,Junkillo
     */
    private fun crearTextoMedidasActivasCorte(): String {
        val piezas = lista.filter { it.cortada && it.longitud > 0f }
        if (piezas.isEmpty()) return ""
        val encabezado = nombreListaActual
            .ifBlank { proyectoOptimizadorActual }
            .ifBlank { lista2.firstOrNull { it.cortada }?.referencia?.trim().orEmpty() }
        return buildString {
            if (encabezado.isNotBlank()) appendLine(encabezado)
            piezas.forEach { pieza ->
                appendLine("${formatter.mostrarConSigla(pieza.longitud)} = ${pieza.cantidad} ${pieza.referencia}")
            }
        }.trim()
    }

    private fun compartirContextoCorte() {
        val texto = crearTextoCompartirCorte()
        if (texto.isBlank()) {
            Toast.makeText(this, "No hay medidas para compartir", Toast.LENGTH_SHORT).show()
            return
        }

        val proyecto = proyectoOptimizadorActual.ifBlank { nombreListaActual }.ifBlank { "corte" }
        val nombreArchivo = "Cortes_${sanitizarNombreArchivo(proyecto)}_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        }.$EXTENSION_CORTE_CRYSTAL"
        val shareDir = File(cacheDir, "cortesshare").apply { mkdirs() }
        val file = File(shareDir, nombreArchivo)

        runCatching {
            file.writeText(construirPaqueteCorteCrystal(texto, proyecto).toString(2))
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val intent = Intent(this, ListChatActivity::class.java).apply {
                putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_URI, uri.toString())
                putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_NAME, file.name)
                putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_MIME, MIME_CORTE_CRYSTAL)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                clipData = ClipData.newUri(contentResolver, "corte_crystal", uri)
            }
            startActivity(intent)
        }.onFailure {
            Toast.makeText(this, "No se pudo compartir formato Crystal: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun construirPaqueteCorteCrystal(texto: String, proyecto: String): JSONObject {
        val piezas = JSONArray().also { arr ->
            lista.forEach { pieza ->
                arr.put(
                    JSONObject()
                        .put("longitud", pieza.longitud)
                        .put("cantidad", pieza.cantidad)
                        .put("referencia", quitarListaDesdeReferencia(pieza.referencia))
                        .put("activa", pieza.cortada)
                )
            }
        }
        val varillas = JSONArray().also { arr ->
            lista2.forEach { varilla ->
                arr.put(
                    JSONObject()
                        .put("longitud", varilla.longitud)
                        .put("cantidad", varilla.cantidad)
                        .put("referencia", varilla.referencia)
                        .put("activa", varilla.cortada)
                )
            }
        }

        return JSONObject()
            .put("format", FORMAT_CORTE_CRYSTAL)
            .put("version", 1)
            .put("exportedAt", System.currentTimeMillis())
            .put("cliente", proyecto)
            .put("producto", "Lista de cortes")
            .put("infoProducto", "Corte lineal")
            .put("notas", texto)
            .put("corte", JSONObject().put("piezas", piezas).put("varillas", varillas))
    }

    private fun sanitizarNombreArchivo(valor: String): String =
        valor.trim()
            .replace(Regex("[^A-Za-z0-9_-]+"), "_")
            .trim('_')
            .ifBlank { "corte" }

    // Recibe piezas en metro lineal (ya convertidas a cm) desde MainActivity y las carga en la lista.
    // Recibe medidas en metro lineal desde MainActivity. Si ya hay una lista cargada (queda guardada
    // en preferences entre sesiones), pregunta qué hacer en vez de sumar en silencio: sumar,
    // reemplazar, o revisar antes lo que hay.
    private fun cargarPiezasDesdeIntent(intent: Intent?) {
        val json = intent?.getStringExtra("piezas_cortes_json") ?: return
        val entrantes = runCatching {
            val arr = org.json.JSONArray(json)
            val piezas = mutableListOf<PiezaCorte>()
            for (idx in 0 until arr.length()) {
                val o = arr.optJSONObject(idx) ?: continue
                val cm = o.optDouble("l", 0.0).toFloat()
                if (cm > 0f) piezas.add(PiezaCorte(cm, o.optInt("c", 1), o.optString("r", "-"), true))
            }
            piezas
        }.getOrElse {
            Toast.makeText(this, "No se pudieron cargar las medidas: ${it.message}", Toast.LENGTH_SHORT).show()
            return
        }
        if (entrantes.isEmpty()) return
        intent.removeExtra("piezas_cortes_json")

        if (lista.isEmpty()) {
            aplicarPiezasEntrantes(entrantes, reemplazar = false)
            return
        }
        preguntarComoCargarPiezas(entrantes)
    }

    private fun preguntarComoCargarPiezas(entrantes: List<PiezaCorte>) {
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

    private fun confirmarReemplazarLista(entrantes: List<PiezaCorte>) {
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
        val filas = lista.map { pieza ->
            val estado = if (pieza.cortada) "" else "  [inactiva]"
            "${formatter.mostrarConSigla(pieza.longitud)} = ${pieza.cantidad} (${pieza.referencia})$estado"
        }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Lista actual (${lista.size} filas, ${lista.sumOf { it.cantidad }} uni)")
            .setItems(filas, null)
            .setPositiveButton("Volver") { _, _ -> alCerrar() }
            .setOnCancelListener { alCerrar() }
            .show()
    }

    private fun aplicarPiezasEntrantes(entrantes: List<PiezaCorte>, reemplazar: Boolean) {
        // Vienen en metro lineal → mostrar en metros
        formatter.escala = EscalaCorte.METRO
        EscalaCorte.guardar(this, EscalaCorte.METRO)
        if (reemplazar) lista.clear()
        lista.addAll(entrantes)
        dataManager.guardarPiezas(lista)
        actualizar()
        actualizarLabelEscala()
        val accion = if (reemplazar) "reemplazaron" else "cargaron"
        Toast.makeText(
            this,
            "Se $accion ${entrantes.size} medida(s) en cortes (metros)",
            Toast.LENGTH_SHORT
        ).show()
    }

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
            if (root.optString("format") != FORMAT_CORTE_CRYSTAL) return
            if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.avanzadoActivo(),
                    "Importar un Corte Crystal es una función de pago.")) return
            confirmarImportarCorteCrystal(root)
            intent.action = null
            intent.data = null
            intent.removeExtra(Intent.EXTRA_STREAM)
        }.onFailure {
            Toast.makeText(this, "No se pudo abrir Corte Crystal: ${it.message}", Toast.LENGTH_SHORT).show()
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

    private fun confirmarImportarCorteCrystal(root: JSONObject) {
        val proyecto = root.optString("cliente").ifBlank { "corte" }
        val corte = root.optJSONObject("corte") ?: JSONObject()
        val piezas = corte.optJSONArray("piezas")?.length() ?: 0
        val varillas = corte.optJSONArray("varillas")?.length() ?: 0

        AlertDialog.Builder(this)
            .setTitle("Corte Crystal")
            .setMessage("Proyecto: $proyecto\nPiezas: $piezas\nVarillas: $varillas\n\nDeseas abrir este corte?")
            .setPositiveButton("Abrir") { _, _ -> importarCorteCrystal(root) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun importarCorteCrystal(root: JSONObject) {
        runCatching {
            val proyecto = root.optString("cliente").ifBlank { "Corte Crystal" }
            val corte = root.optJSONObject("corte") ?: JSONObject()
            val piezasJson = corte.optJSONArray("piezas") ?: JSONArray()
            val varillasJson = corte.optJSONArray("varillas") ?: JSONArray()

            val piezas = mutableListOf<PiezaCorte>()
            for (i in 0 until piezasJson.length()) {
                piezasJson.optJSONObject(i)?.toPiezaCorte()?.let { piezas.add(it) }
            }

            val varillas = mutableListOf<PiezaCorte>()
            for (i in 0 until varillasJson.length()) {
                varillasJson.optJSONObject(i)?.toPiezaCorte()?.let { varillas.add(it) }
            }

            if (piezas.isEmpty() && varillas.isEmpty()) {
                Toast.makeText(this, "El archivo no contiene cortes", Toast.LENGTH_SHORT).show()
                return
            }

            lista = piezas
            lista2 = varillas
            nombreListaActual = proyecto
            proyectoOptimizadorActual = ""
            actualizar()
            actualizar2()
            guardarDatos()
            actualizarTituloProyectoOptimizador()
            Toast.makeText(this, "Corte Crystal abierto", Toast.LENGTH_SHORT).show()
        }.onFailure {
            Toast.makeText(this, "No se pudo importar corte: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun JSONObject.toPiezaCorte(): PiezaCorte? {
        val longitud = optDouble("longitud", Double.NaN).takeIf { !it.isNaN() }?.toFloat() ?: return null
        val cantidad = optInt("cantidad", 0)
        val referencia = optString("referencia")
        if (longitud <= 0f || cantidad <= 0 || referencia.isBlank()) return null
        return PiezaCorte(longitud, cantidad, referencia, optBoolean("activa", true))
    }

    private fun crearTextoCompartirCorte(): String {
        val medida = binding.etMedida.text?.toString()?.trim().orEmpty()
        val cantidad = binding.etCant.text?.toString()?.trim().orEmpty()
        val referencia = binding.etRefe.text?.toString()?.trim().orEmpty()
        val hayFormulario = medida.isNotEmpty() || cantidad.isNotEmpty() || referencia.isNotEmpty()

        if (lista.isEmpty() && lista2.isEmpty() && !hayFormulario) return ""

        return buildString {
            appendLine("Crystal - Optimizacion de corte lineal")
            val proyecto = proyectoOptimizadorActual.ifBlank { nombreListaActual }
            if (proyecto.isNotBlank()) appendLine("Proyecto: $proyecto")
            appendLine("Grosor disco: ${binding.etGrosor.text?.toString()?.ifBlank { binding.etGrosor.hint } ?: binding.etGrosor.hint} cm")
            appendLine("Nivel: ${binding.sbNivel.progress + 1}")

            if (hayFormulario) {
                appendLine()
                appendLine("Medida en formulario:")
                appendLine("Medida: ${medida.ifBlank { "-" }} ${formatter.sigla}")
                appendLine("Cantidad: ${cantidad.ifBlank { "-" }}")
                appendLine("Referencia: ${referencia.ifBlank { "-" }}")
            }

            if (lista.isNotEmpty()) {
                appendLine()
                appendLine("Piezas:")
                lista.forEachIndexed { index, pieza ->
                    val estado = if (pieza.cortada) "" else " [inactiva]"
                    appendLine("${index + 1}. ${formatter.mostrarConSigla(pieza.longitud)} = ${pieza.cantidad} (${pieza.referencia})$estado")
                }
            }

            if (lista2.isNotEmpty()) {
                appendLine()
                appendLine("Varillas y retazos:")
                lista2.forEachIndexed { index, varilla ->
                    val estado = if (varilla.cortada) "" else " [inactiva]"
                    appendLine("${index + 1}. ${formatter.mostrarConSigla(varilla.longitud)} = ${varilla.cantidad} (${varilla.referencia})$estado")
                }
            }
        }.trim()
    }

    // === FUNCIONES DE DIÁLOGOS (mantenidas igual que el original) ===

    private fun abrirDialogoCortes() {
        try {
            val dialogoCortes = Dialog(this)
            dialogoCortes.setContentView(R.layout.dialogo_cortes)

            val etdMed1: EditText = dialogoCortes.findViewById(R.id.etdMed1)
            val etdCant: EditText = dialogoCortes.findViewById(R.id.etdCant)
            val etdProducto: EditText = dialogoCortes.findViewById(R.id.etdProducto)
            val btDiAgregar: Button = dialogoCortes.findViewById(R.id.btDiAgregar)
            val btnDiaEli: Button = dialogoCortes.findViewById(R.id.btn_dialogo_eliminar)
            val btnDiaEdi: Button = dialogoCortes.findViewById(R.id.btn_dialogo_editar)
            val lyTxt: LinearLayout = dialogoCortes.findViewById(R.id.lyTxt)

            btDiAgregar.visibility = View.VISIBLE
            btnDiaEli.visibility = View.GONE
            btnDiaEdi.visibility = View.GONE
            lyTxt.visibility = View.VISIBLE

            etdMed1.text = null
            etdCant.text = null
            etdProducto.text = null

            dialogoCortes.show()
            dialogoCortes.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

            btDiAgregar.setOnClickListener {
                val med1 = formatter.convertirAFloat(etdMed1.text.toString())
                val cant = formatter.convertirAInt(etdCant.text.toString())
                val producto = etdProducto.text.toString()

                if (med1 != null && cant != null && producto.isNotEmpty()) {
                    lista2.add(PiezaCorte(formatter.escala.aCm(med1), cant, producto, true)) // guardar en cm
                    actualizar2()
                    dataManager.guardarVarillas(lista2)
                    dialogoCortes.dismiss()
                } else {
                    Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al agregar varilla: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarDialogo() {
        // Click simple para toggle del estado cortada
        binding.listCorte.setOnItemClickListener { _, _, position, _ ->
            lista[position] = lista[position].copy(cortada = !lista[position].cortada)
            actualizar()
            dataManager.guardarPiezas(lista)
        }

        // Click largo para abrir diálogo de editar/eliminar
        binding.listCorte.setOnItemLongClickListener { _, _, position, _ ->
            try {
                val dialogo = Dialog(this)
                dialogo.setContentView(R.layout.dialogo_cortes)

                val lyTxt: LinearLayout = dialogo.findViewById(R.id.lyTxt)
                val etdMed1: EditText = dialogo.findViewById(R.id.etdMed1)
                val etdCant: EditText = dialogo.findViewById(R.id.etdCant)
                val etdProducto: EditText = dialogo.findViewById(R.id.etdProducto)
                val btnDiaOk: Button = dialogo.findViewById(R.id.btnDiaOk)
                val btnDiaEli: Button = dialogo.findViewById(R.id.btn_dialogo_eliminar)
                val btnDiaEdi: Button = dialogo.findViewById(R.id.btn_dialogo_editar)
                val btDiAgregar: Button = dialogo.findViewById(R.id.btDiAgregar)

                btDiAgregar.visibility = View.GONE
                btnDiaOk.visibility = View.GONE
                lyTxt.visibility = View.GONE

                val pieza = lista[position]

                etdMed1.setText(formatter.mostrar(pieza.longitud))
                etdCant.setText(pieza.cantidad.toString())
                etdProducto.setText(pieza.referencia)

                btnDiaEdi.setOnClickListener {
                    btnDiaOk.visibility = View.VISIBLE
                    btnDiaEli.visibility = View.GONE
                    btnDiaEdi.visibility = View.GONE
                    lyTxt.visibility = View.VISIBLE
                }

                btnDiaEli.setOnClickListener {
                    lista.removeAt(position)
                    actualizar()
                    dataManager.guardarPiezas(lista)
                    dialogo.dismiss()
                }

                btnDiaOk.setOnClickListener {
                    val nuevaLongitud = formatter.convertirAFloat(etdMed1.text.toString())
                    val nuevaCantidad = formatter.convertirAInt(etdCant.text.toString())
                    val nuevaReferencia = etdProducto.text.toString()

                    if (nuevaLongitud != null && nuevaCantidad != null && nuevaReferencia.isNotEmpty()) {
                        lista[position] = PiezaCorte(formatter.escala.aCm(nuevaLongitud), nuevaCantidad, nuevaReferencia, lista[position].cortada)
                        actualizar()
                        dataManager.guardarPiezas(lista)
                        dialogo.dismiss()
                    } else {
                        Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show()
                    }
                }

                dialogo.show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al mostrar diálogo: ${e.message}", Toast.LENGTH_LONG).show()
            }
            true // Importante: retornar true para indicar que el evento fue manejado
        }
    }

    private fun mostrarDialogo2() {
        binding.listaPerfil.setOnItemClickListener { _, _, position, _ ->
            // Click simple para toggle del estado
            lista2[position] = lista2[position].copy(cortada = !lista2[position].cortada)
            actualizar2()
            dataManager.guardarVarillas(lista2)
        }

        binding.listaPerfil.setOnItemLongClickListener { _, _, position, _ ->
            try {
                val dialogo = Dialog(this)
                dialogo.setContentView(R.layout.dialogo_cortes)

                val lyTxt: LinearLayout = dialogo.findViewById(R.id.lyTxt)
                val etdMed1: EditText = dialogo.findViewById(R.id.etdMed1)
                val etdCant: EditText = dialogo.findViewById(R.id.etdCant)
                val etdProducto: EditText = dialogo.findViewById(R.id.etdProducto)
                val btnDiaOk: Button = dialogo.findViewById(R.id.btnDiaOk)
                val btnDiaEli: Button = dialogo.findViewById(R.id.btn_dialogo_eliminar)
                val btnDiaEdi: Button = dialogo.findViewById(R.id.btn_dialogo_editar)
                val btDiAgregar: Button = dialogo.findViewById(R.id.btDiAgregar)

                btDiAgregar.visibility = View.GONE
                btnDiaOk.visibility = View.GONE
                lyTxt.visibility = View.GONE

                val pieza = lista2[position]

                etdMed1.setText(formatter.mostrar(pieza.longitud))
                etdCant.setText(pieza.cantidad.toString())
                etdProducto.setText(pieza.referencia)

                btnDiaEdi.setOnClickListener {
                    btnDiaOk.visibility = View.VISIBLE
                    btnDiaEli.visibility = View.GONE
                    btnDiaEdi.visibility = View.GONE
                    lyTxt.visibility = View.VISIBLE
                }

                btnDiaEli.setOnClickListener {
                    lista2.removeAt(position)
                    actualizar2()
                    dataManager.guardarVarillas(lista2)
                    dialogo.dismiss()
                }

                btnDiaOk.setOnClickListener {
                    val nuevaLongitud = formatter.convertirAFloat(etdMed1.text.toString())
                    val nuevaCantidad = formatter.convertirAInt(etdCant.text.toString())
                    val nuevaReferencia = etdProducto.text.toString()

                    if (nuevaLongitud != null && nuevaCantidad != null && nuevaReferencia.isNotEmpty()) {
                        lista2[position] = PiezaCorte(formatter.escala.aCm(nuevaLongitud), nuevaCantidad, nuevaReferencia, lista2[position].cortada)
                        actualizar2()
                        dataManager.guardarVarillas(lista2)
                        dialogo.dismiss()
                    } else {
                        Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_LONG).show()
                    }
                }

                dialogo.show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al mostrar diálogo: ${e.message}", Toast.LENGTH_LONG).show()
            }
            true
        }
    }
}
