package crystal.crystal.optimizadores.corte

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import crystal.crystal.R
import crystal.crystal.databinding.ActivityCorteBinding

/**
 * Activity principal refactorizada - delega responsabilidades a clases especializadas
 * Mantiene exactamente la misma funcionalidad que el código original
 */
class CorteActivity: AppCompatActivity() {

    private var lista = mutableListOf<PiezaCorte>()
    private var lista2 = mutableListOf<PiezaCorte>()

    private lateinit var binding: ActivityCorteBinding

    // Clases especializadas
    private lateinit var dataManager: CorteDataManager
    private lateinit var optimizer: CorteOptimizer
    private lateinit var listManager: CorteListManager
    private lateinit var formatter: CorteFormatter

    // NUEVO: Variable para almacenar estadísticas anteriores
    private var ultimasEstadisticas: AnalisisMejoras.EstadisticasOptimizacion? = null

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

        binding.etMedida.requestFocus()

        binding.btAnadir.setOnClickListener {
            try {
                anadir()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al agregar pieza: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }

        binding.btAnadir.setOnLongClickListener {
            // Llamar a la función para poblar el Spinner usando la clase especializada
            if (listManager.hayListasDisponibles()) {
                listManager.poblarSpinnerConDatosGuardados(binding.spCortes)
            }
            true
        }

        binding.btLimpiar.setOnClickListener {
            limpiarPiezas()
        }

        // CORREGIDO: Método de optimización
        binding.btOpti.setOnClickListener {
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

                        // NUEVO: Usar la configuración para timeouts y parámetros
                        val resultadoOptimizado = optimizer.optimizarCortesConConfiguracion(
                            piezasParaOptimizar,
                            varillasDisponibles,
                            grosorDisco
                        )

                        val tiempoTotal = System.currentTimeMillis() - tiempoInicio

                        // DEBUG: Log del resultado de optimización
                        DebugHelper.logVarillasUsadas(resultadoOptimizado)

                        // CORREGIDO: Volver al hilo principal para actualizar UI Y NAVEGAR
                        runOnUiThread {
                            ocultarIndicadorPensamiento()
                            // AHORA SÍ navegar con el resultado correcto
                            mostrarResultadoOptimizadoCorregido(resultadoOptimizado, tiempoTotal)
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

        binding.btAgregar.setOnClickListener {
            abrirDialogoCortes()
        }

        binding.btEliminar.setOnClickListener {
            limpiarVarillas()
        }

        binding.spCortes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val nombreSeleccionado = parent.getItemAtPosition(position).toString()
                cargarListaSeleccionada(nombreSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Opcional: Manejar el caso en que no se selecciona nada
            }
        }
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

    // === NUEVAS FUNCIONES PARA DESCUENTOS AUTOMÁTICOS ===

    /**
     * Procesa los descuentos automáticos basados en los cortes ejecutados
     */
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
            lista.add(PiezaCorte(medi, cant, refe, true)) // true por defecto
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
        tiempoOptimizacion: Long
    ) {
        // Generar resultado estructurado
        val resultadoEstructurado = optimizer.generarResultadoEstructurado(varillasUsadas)

        // DEBUG: Log del resultado estructurado
        DebugHelper.logResultadoEstructurado(resultadoEstructurado)

        // Guardar las varillas usadas
        dataManager.guardarVarillasUsadas(varillasUsadas)

        // NUEVO: Mostrar análisis detallado con AnalisisMejoras
        mostrarAnalisisMejorasDetallado(resultadoEstructurado, tiempoOptimizacion)

        // AHORA SÍ navegar a la nueva Activity con el resultado final
        val intent = Intent(this, ResultadoOptimizacionActivity::class.java)
        intent.putExtra("resultado_optimizacion", resultadoEstructurado)
        startActivity(intent)

        // También guardar como texto para mantener compatibilidad
        val textoResultado = generarTextoResultadoLegacy(varillasUsadas)
        binding.tvResultado.setText(textoResultado)
        dataManager.guardarResultado(textoResultado)
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
            actualizar()
            dataManager.guardarPiezas(lista)
        }
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
                    lista2.add(PiezaCorte(med1, cant, producto, true)) // true por defecto
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

                etdMed1.setText(pieza.longitud.toString())
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
                        lista[position] = PiezaCorte(nuevaLongitud, nuevaCantidad, nuevaReferencia, lista[position].cortada)
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

                etdMed1.setText(pieza.longitud.toString())
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
                        lista2[position] = PiezaCorte(nuevaLongitud, nuevaCantidad, nuevaReferencia, lista2[position].cortada)
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