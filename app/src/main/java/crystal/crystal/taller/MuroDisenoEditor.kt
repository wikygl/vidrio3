package crystal.crystal.taller

import android.annotation.SuppressLint
import android.graphics.PointF
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import crystal.crystal.R

/**
 * Editor del diseño del muro, incrustado en la propia calculadora.
 *
 * Era una actividad aparte (`EditGridActivity`): se salía de la calculadora a diseñar y se volvía
 * con el resultado por un launcher. Ahora el dibujo vive en la pantalla de Muro y esto lo gobierna
 * desde dentro, así que se ve el efecto en los cálculos sin ir y volver.
 *
 * Trabaja sobre vistas que le entrega quien la hospeda; no conoce ningún layout concreto.
 */
class MuroDisenoEditor(
    private val act: AppCompatActivity,
    private val gridDrawingView: GridDrawingView,
    private val selectionPanel: LinearLayout,
    private val secondaryPanel: LinearLayout,
    /** Se llama en cada cambio del diseño: la calculadora recalcula con lo nuevo. */
    private val onCambio: () -> Unit
) {



    private var anchoTotal: Float = 150f
    private var altoTotal: Float = 180f
    private var marco: Float = 2.5f
    private var tubo: Float = 3.8f
    private var gruna: Float = 0f
    private var modoVisual = GridDrawingView.ModoVisual.GRILLA
    private var accionPoligono = AccionPoligono.NINGUNA
    private var ultimoXPoligono = 0f
    private var ultimoYPoligono = 0f
    private var textoPuntosPoligono = "0,0;40,0;40,30;0,30"
    private var anchoPoligono = 40f
    private var altoPoligono = 30f
    private var mostrarEditorPoligono = false
    private val naves = linkedMapOf<Pair<Int, Int>, String>()
    private lateinit var anchosColumnas: MutableList<Float>
    private lateinit var alturasFilasPorColumna: MutableList<MutableList<Float>>
    private lateinit var filasEditadasPorColumna: MutableList<MutableList<Boolean>>

    private var filaSeleccionada = -1
    private var columnaSeleccionada = -1
    private var moduloColumnaSeleccionada = -1
    private var moduloFilaSeleccionada = -1

    private enum class AccionPoligono { NINGUNA, DIBUJAR, MOVER }


    /** Carga el diseño desde la calculadora. Sustituye a los extras del intent de la actividad. */
    fun cargar(
        anchoTotal: Float,
        altoTotal: Float,
        marco: Float,
        tubo: Float,
        gruna: Float,
        naves: String,
        anchosColumnas: FloatArray?,
        alturasFilas: String,
        forma: String
    ) {
        this.anchoTotal = anchoTotal
        this.altoTotal = altoTotal
        this.marco = marco
        this.tubo = tubo
        this.gruna = gruna
        cargarNaves(naves)
        this.anchosColumnas = anchosColumnas?.toMutableList() ?: mutableListOf(anchoTotal)
        this.alturasFilasPorColumna = leerAlturasFilas(alturasFilas)
            ?: MutableList(this.anchosColumnas.size) { mutableListOf(altoTotal) }
        this.formaInicial = forma
        normalizarDatos()
        normalizarFilasEditadas()
        configurarVista()
    }

    /** Lo que la calculadora necesita leer del diseño después de cada cambio. */
    fun anchosColumnas(): FloatArray = anchosColumnas.toFloatArray()
    fun alturasFilas(): String = serializarAlturasFilas()
    fun naves(): String = serializarNaves()
    fun forma(): String = gridDrawingView.exportarForma()
    fun marco(): Float = marco
    fun tubo(): Float = tubo
    fun gruna(): Float = gruna

    private var formaInicial: String = ""
    private fun leerAlturasFilas(serializado: String): MutableList<MutableList<Float>>? {
        if (serializado.isBlank()) return null
        return serializado.split("|")
            .map { columna ->
                columna.split(",")
                    .mapNotNull { it.toFloatOrNull() }
                    .toMutableList()
            }
            .filter { it.isNotEmpty() }
            .toMutableList()
            .takeIf { it.isNotEmpty() }
    }

    private fun serializarAlturasFilas(): String {
        return alturasFilasPorColumna.joinToString("|") { columna ->
            columna.joinToString(",")
        }
    }

    private fun cargarNaves(texto: String) {
        naves.clear()
        val regex = Regex("""c(\d+)\s*,\s*f(\d+)(?:\s*=\s*([^;]+))?""", RegexOption.IGNORE_CASE)
        texto.split(";")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .forEachIndexed { index, item ->
                val match = regex.matchEntire(item) ?: return@forEachIndexed
                val columna = match.groupValues[1].toIntOrNull()?.minus(1) ?: return@forEachIndexed
                val fila = match.groupValues[2].toIntOrNull()?.minus(1) ?: return@forEachIndexed
                val identificador = match.groupValues.getOrNull(3)?.trim().orEmpty().ifBlank { "N${index + 1}" }
                naves[columna to fila] = identificador
            }
    }

    private fun serializarNaves(): String {
        return naves.entries.joinToString(";") { (posicion, identificador) ->
            "c${posicion.first + 1},f${posicion.second + 1}=$identificador"
        }
    }

    private fun df(valor: Float): String {
        val resultado = if (valor % 1.0 == 0.0) "%.0f".format(valor) else "%.1f".format(valor)
        return resultado.replace(",", ".")
    }

    private fun dp(value: Int): Int = (value * act.resources.displayMetrics.density).toInt()

    @SuppressLint("ClickableViewAccessibility")
    private fun configurarVista() {
        selectionPanel.orientation = LinearLayout.VERTICAL
        selectionPanel.gravity = Gravity.START
        selectionPanel.removeAllViews()
        secondaryPanel.removeAllViews()
        updateGrid()
        gridDrawingView.importarForma(formaInicial)

        gridDrawingView.setOnTouchListener { _, event ->
            if (accionPoligono != AccionPoligono.NINGUNA) {
                manejarArrastrePoligono(event)
            } else if (event.action == MotionEvent.ACTION_UP) {
                manejarClickGrid(event.x, event.y)
            }
            true
        }

        renderPanelSeleccion()
    }

    private fun manejarArrastrePoligono(event: MotionEvent) {
        when (accionPoligono) {
            AccionPoligono.DIBUJAR -> when (event.action) {
                MotionEvent.ACTION_DOWN -> gridDrawingView.iniciarPoligono(event.x, event.y)
                MotionEvent.ACTION_MOVE -> gridDrawingView.agregarPuntoPoligono(event.x, event.y)
                MotionEvent.ACTION_UP -> gridDrawingView.agregarPuntoPoligono(event.x, event.y)
            }
            AccionPoligono.MOVER -> when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    ultimoXPoligono = event.x
                    ultimoYPoligono = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    gridDrawingView.moverPoligono(event.x - ultimoXPoligono, event.y - ultimoYPoligono)
                    ultimoXPoligono = event.x
                    ultimoYPoligono = event.y
                }
            }
            AccionPoligono.NINGUNA -> Unit
        }
    }

    fun cambiarModo(modo: GridDrawingView.ModoVisual) {
        modoVisual = modo
        gridDrawingView.setModoVisual(modoVisual, marco, tubo, gruna)
        actualizarSeleccionVisual()
        renderPanelSeleccion()
    }

    private fun manejarClickGrid(x: Float, y: Float) {
        val celda = gridDrawingView.detectarCelda(x, y)
        if (celda == null) {
            limpiarSeleccion()
            return
        }
        if (filaSeleccionada != celda.fila) {
            filaSeleccionada = celda.fila
            columnaSeleccionada = -1
            moduloColumnaSeleccionada = -1
            moduloFilaSeleccionada = -1
        } else if (columnaSeleccionada != celda.columna) {
            columnaSeleccionada = celda.columna
            moduloColumnaSeleccionada = -1
            moduloFilaSeleccionada = -1
        } else {
            moduloColumnaSeleccionada = celda.columna
            moduloFilaSeleccionada = celda.fila
        }
        actualizarSeleccionVisual()
        renderPanelSeleccion()
    }

    private fun limpiarSeleccion() {
        filaSeleccionada = -1
        columnaSeleccionada = -1
        moduloColumnaSeleccionada = -1
        moduloFilaSeleccionada = -1
        actualizarSeleccionVisual()
        renderPanelSeleccion()
    }

    private fun actualizarSeleccionVisual() {
        gridDrawingView.actualizarSeleccion(
            filaSeleccionada,
            columnaSeleccionada,
            moduloColumnaSeleccionada,
            moduloFilaSeleccionada
        )
    }

    @SuppressLint("SetTextI18n")
    private fun renderPanelSeleccion() {
        selectionPanel.removeAllViews()
        secondaryPanel.removeAllViews()

        val titulo = TextView(act).apply {
            textSize = 14f
            setPadding(dp(8), dp(4), dp(8), dp(2))
            text = when {
                moduloColumnaSeleccionada >= 0 -> tituloModulo()
                columnaSeleccionada >= 0 -> tituloColumna()
                filaSeleccionada >= 0 -> tituloFila()
                else -> tituloSinSeleccion()
            }
        }
        selectionPanel.addView(titulo)

        when {
            moduloColumnaSeleccionada >= 0 -> renderEditorModulo()
            columnaSeleccionada >= 0 -> renderEditorColumna()
            filaSeleccionada >= 0 -> renderEditorFila()
        }
        if (modoVisual == GridDrawingView.ModoVisual.ESTRUCTURA) {
            renderEditorEspesores()
        }
        if (modoVisual == GridDrawingView.ModoVisual.VIDRIO) {
            renderEditorGruna()
        }
        renderAccesoPoligono()
    }

    private fun renderEditorFila() {
        val fila = filaSeleccionada
        val columnaReferencia = alturasFilasPorColumna.indexOfFirst { fila in it.indices }.coerceAtLeast(0)
        val altoReferencia = valorAltoVisible(columnaReferencia, fila)
        val inputAlto = nuevoInput(altoReferencia)
        selectionPanel.addView(filaControles(labelAlto(), inputAlto) {
            aplicarAltoVisibleAFilaGlobal(fila, inputAlto.valorFloat() ?: return@filaControles)
        })
        secondaryPanel.addView(filaBotones(
            nuevoBoton("+ fila") { agregarFilaAColumnas(fila) },
            nuevoBoton("- fila") { eliminarFilaDeColumnas(fila) }
        ))
    }

    private fun renderEditorColumna() {
        val columna = columnaSeleccionada
        val inputAncho = nuevoInput(valorAnchoVisible(columna))
        selectionPanel.addView(filaControles(labelAncho(), inputAncho) {
            aplicarAnchoVisibleColumna(columna, inputAncho.valorFloat() ?: return@filaControles)
        })
        secondaryPanel.addView(filaBotones(
            nuevoBoton("+ col") { agregarColumna(columna) },
            nuevoBoton("- col") { eliminarColumna(columna) },
            nuevoBoton("+ fila") { agregarFilaEnColumna(columna) },
            nuevoBoton("- fila") { eliminarFilaEnColumna(columna) }
        ))
    }

    private fun renderEditorModulo() {
        val columna = moduloColumnaSeleccionada
        val fila = moduloFilaSeleccionada
        val inputAncho = nuevoInput(valorAnchoVisible(columna))
        val inputAlto = nuevoInput(valorAltoVisible(columna, fila))
        selectionPanel.addView(filaControles(labelAncho(), inputAncho) {
            aplicarAnchoVisibleColumna(columna, inputAncho.valorFloat() ?: return@filaControles)
        })
        selectionPanel.addView(filaControles(labelAlto(), inputAlto) {
            aplicarAltoVisibleFilaColumna(columna, fila, inputAlto.valorFloat() ?: return@filaControles)
        })
        secondaryPanel.addView(filaBotones(
            nuevoBoton("+ col") { agregarColumna(columna) },
            nuevoBoton("- col") { eliminarColumna(columna) },
            nuevoBoton("+ fila") { agregarFilaEnColumna(columna) },
            nuevoBoton("- fila") { eliminarFilaEnColumna(columna) }
        ))
        renderEditorNave(columna, fila)
    }

    private fun renderEditorNave(columna: Int, fila: Int) {
        val posicion = columna to fila
        val inputId = nuevoInputTexto(naves[posicion] ?: siguienteIdentificadorNave())
        selectionPanel.addView(filaControles("Nave", inputId) {
            val identificador = inputId.text.toString().trim().ifBlank { siguienteIdentificadorNave() }
            naves[posicion] = identificador
            refrescarDespuesDeCambio()
        })
        val boton = if (naves.containsKey(posicion)) {
            nuevoBoton("Quitar nave") {
                naves.remove(posicion)
                refrescarDespuesDeCambio()
            }
        } else {
            nuevoBoton("Marcar nave") {
                val identificador = inputId.text.toString().trim().ifBlank { siguienteIdentificadorNave() }
                naves[posicion] = identificador
                refrescarDespuesDeCambio()
            }
        }
        secondaryPanel.addView(filaBotones(boton))
    }

    private fun renderEditorEspesores() {
        val inputMarco = nuevoInput(marco)
        val inputTubo = nuevoInput(tubo)
        selectionPanel.addView(filaControles("Marco", inputMarco) {
            marco = inputMarco.valorFloat() ?: return@filaControles
            refrescarDespuesDeCambio()
        })
        selectionPanel.addView(filaControles("Tubo", inputTubo) {
            tubo = inputTubo.valorFloat() ?: return@filaControles
            refrescarDespuesDeCambio()
        })
    }

    private fun renderEditorGruna() {
        val inputGruna = nuevoInput(gruna)
        selectionPanel.addView(filaControles("Gruna", inputGruna) {
            gruna = inputGruna.valorFloatCeroPermitido() ?: return@filaControles
            refrescarDespuesDeCambio()
        })
    }

    private fun renderAccesoPoligono() {
        if (!mostrarEditorPoligono) {
            secondaryPanel.addView(filaBotones(
                nuevoBoton("Forma") {
                    mostrarEditorPoligono = true
                    renderPanelSeleccion()
                }
            ))
            return
        }
        renderEditorPoligono()
    }

    private fun renderEditorPoligono() {
        val estado = when (accionPoligono) {
            AccionPoligono.NINGUNA -> "Forma: editar"
            AccionPoligono.DIBUJAR -> "Forma: dibujar poligono"
            AccionPoligono.MOVER -> "Forma: mover poligono"
        }
        selectionPanel.addView(TextView(act).apply {
            text = estado
            textSize = 13f
            setPadding(dp(8), dp(4), dp(8), dp(1))
        })
        secondaryPanel.addView(filaBotones(
            nuevoBoton("Dibujar") {
                accionPoligono = AccionPoligono.DIBUJAR
                renderPanelSeleccion()
            },
            nuevoBoton("Mover") {
                accionPoligono = AccionPoligono.MOVER
                renderPanelSeleccion()
            },
            nuevoBoton("Soldar") {
                aplicarPoligono(GridDrawingView.OperacionPoligono.SOLDAR)
            },
            nuevoBoton("Recortar") {
                aplicarPoligono(GridDrawingView.OperacionPoligono.RECORTAR)
            }
        ))
        secondaryPanel.addView(filaBotones(
            nuevoBoton("Limpiar pol") {
                gridDrawingView.limpiarPoligono()
                accionPoligono = AccionPoligono.NINGUNA
                renderPanelSeleccion()
            },
            nuevoBoton("Editar") {
                accionPoligono = AccionPoligono.NINGUNA
                mostrarEditorPoligono = false
                renderPanelSeleccion()
            },
            nuevoBoton("Reset forma") {
                gridDrawingView.resetForma()
                accionPoligono = AccionPoligono.NINGUNA
                mostrarEditorPoligono = false
                renderPanelSeleccion()
            }
        ))
        val inputPuntos = nuevoInputTexto(textoPuntosPoligono).apply {
            layoutParams = LinearLayout.LayoutParams(dp(220), dp(38)).apply {
                marginEnd = dp(6)
            }
        }
        selectionPanel.addView(filaControles("Puntos", inputPuntos) {
            textoPuntosPoligono = inputPuntos.text.toString()
            val puntos = parsearPuntosPoligono(textoPuntosPoligono)
            if (!gridDrawingView.setPoligonoCm(puntos)) {
                Toast.makeText(act, "Use minimo 3 puntos: x,y;x,y;x,y", Toast.LENGTH_SHORT).show()
            } else {
                actualizarMedidasPoligonoDesdePuntos(puntos)
                renderPanelSeleccion()
            }
        })

        val inputAncho = nuevoInput(anchoPoligono)
        val inputAlto = nuevoInput(altoPoligono)
        selectionPanel.addView(filaControles("Ancho", inputAncho) {
            actualizarMedidasDesdeInputs(inputAncho, inputAlto) ?: return@filaControles
            generarRectanguloPoligono()
            renderPanelSeleccion()
        })
        selectionPanel.addView(filaControles("Alto", inputAlto) {
            actualizarMedidasDesdeInputs(inputAncho, inputAlto) ?: return@filaControles
            generarRectanguloPoligono()
            renderPanelSeleccion()
        })
        secondaryPanel.addView(filaBotones(
            nuevoBoton("Tri der") {
                actualizarMedidasDesdeInputs(inputAncho, inputAlto) ?: return@nuevoBoton
                textoPuntosPoligono = "0,0;${df(anchoPoligono)},${df(altoPoligono)};0,${df(altoPoligono)}"
                gridDrawingView.setPoligonoCm(parsearPuntosPoligono(textoPuntosPoligono))
                renderPanelSeleccion()
            },
            nuevoBoton("Tri izq") {
                actualizarMedidasDesdeInputs(inputAncho, inputAlto) ?: return@nuevoBoton
                textoPuntosPoligono = "0,${df(altoPoligono)};${df(anchoPoligono)},0;${df(anchoPoligono)},${df(altoPoligono)}"
                gridDrawingView.setPoligonoCm(parsearPuntosPoligono(textoPuntosPoligono))
                renderPanelSeleccion()
            }
        ))
    }

    private fun actualizarMedidasDesdeInputs(inputAncho: EditText, inputAlto: EditText): Boolean? {
        anchoPoligono = inputAncho.valorFloat() ?: return null
        altoPoligono = inputAlto.valorFloat() ?: return null
        return true
    }

    private fun generarRectanguloPoligono() {
        textoPuntosPoligono = "0,0;${df(anchoPoligono)},0;${df(anchoPoligono)},${df(altoPoligono)};0,${df(altoPoligono)}"
        gridDrawingView.setPoligonoCm(parsearPuntosPoligono(textoPuntosPoligono))
    }

    private fun actualizarMedidasPoligonoDesdePuntos(puntos: List<PointF>) {
        if (puntos.isEmpty()) return
        val minX = puntos.minOf { it.x }
        val maxX = puntos.maxOf { it.x }
        val minY = puntos.minOf { it.y }
        val maxY = puntos.maxOf { it.y }
        anchoPoligono = (maxX - minX).coerceAtLeast(0f)
        altoPoligono = (maxY - minY).coerceAtLeast(0f)
    }

    private fun parsearPuntosPoligono(texto: String): List<PointF> {
        return texto.split(";")
            .mapNotNull { punto ->
                val partes = punto.trim().split(",")
                if (partes.size != 2) return@mapNotNull null
                val x = partes[0].trim().toFloatOrNull()
                val y = partes[1].trim().toFloatOrNull()
                if (x == null || y == null) null else PointF(x, y)
            }
    }

    private fun aplicarPoligono(operacion: GridDrawingView.OperacionPoligono) {
        if (!gridDrawingView.aplicarPoligono(operacion)) {
            Toast.makeText(act, "Dibuja un poligono con al menos 3 puntos", Toast.LENGTH_SHORT).show()
            return
        }
        accionPoligono = AccionPoligono.NINGUNA
        mostrarEditorPoligono = false
        renderPanelSeleccion()
    }

    private fun filaControles(label: String, input: EditText, onApply: () -> Unit): LinearLayout {
        return LinearLayout(act).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(8), dp(2), dp(8), dp(2))
            addView(TextView(act).apply {
                text = label
                textSize = 13f
                layoutParams = LinearLayout.LayoutParams(dp(54), LinearLayout.LayoutParams.WRAP_CONTENT)
            })
            addView(input)
            addView(nuevoBoton("Aplicar", onApply))
        }
    }

    private fun filaBotones(vararg botones: Button): LinearLayout {
        return LinearLayout(act).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(dp(8), dp(2), dp(8), dp(4))
            botones.forEach { addView(it) }
        }
    }

    private fun nuevoInput(valor: Float): EditText {
        return EditText(act).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(df(valor))
            textSize = 13f
            gravity = Gravity.CENTER
            minHeight = 0
            setPadding(dp(4), 0, dp(4), 0)
            layoutParams = LinearLayout.LayoutParams(dp(80), dp(38)).apply {
                marginEnd = dp(6)
            }
        }
    }

    private fun nuevoInputTexto(valor: String): EditText {
        return EditText(act).apply {
            inputType = InputType.TYPE_CLASS_TEXT
            setText(valor)
            textSize = 13f
            gravity = Gravity.CENTER
            minHeight = 0
            setPadding(dp(4), 0, dp(4), 0)
            layoutParams = LinearLayout.LayoutParams(dp(80), dp(38)).apply {
                marginEnd = dp(6)
            }
        }
    }

    private fun nuevoBoton(texto: String, accion: () -> Unit): Button {
        return Button(act).apply {
            text = texto
            minHeight = 0
            minWidth = 0
            textSize = 12f
            setPadding(dp(6), 0, dp(6), 0)
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(36)).apply {
                marginEnd = dp(4)
            }
            setOnClickListener { accion() }
        }
    }

    private fun EditText.valorFloat(): Float? {
        val valor = text.toString().replace(",", ".").toFloatOrNull()
        if (valor == null || valor <= 0f) {
            Toast.makeText(act, "El valor debe ser mayor que cero", Toast.LENGTH_SHORT).show()
            return null
        }
        return valor
    }

    private fun EditText.valorFloatCeroPermitido(): Float? {
        val valor = text.toString().replace(",", ".").toFloatOrNull()
        if (valor == null || valor < 0f) {
            Toast.makeText(act, "El valor no puede ser negativo", Toast.LENGTH_SHORT).show()
            return null
        }
        return valor
    }

    private fun tituloSinSeleccion(): String {
        return when (modoVisual) {
            GridDrawingView.ModoVisual.GRILLA -> "Seleccione una fila"
            GridDrawingView.ModoVisual.ESTRUCTURA -> "Estructura: marco ${df(marco)}, tubo ${df(tubo)}"
            GridDrawingView.ModoVisual.VIDRIO -> "Vidrios: gruna ${df(gruna)}"
        }
    }

    private fun tituloFila(): String {
        val nombre = when (modoVisual) {
            GridDrawingView.ModoVisual.GRILLA -> "Fila"
            GridDrawingView.ModoVisual.ESTRUCTURA -> "Interior fila"
            GridDrawingView.ModoVisual.VIDRIO -> "Vidrio fila"
        }
        return "$nombre ${filaVisibleGlobal(filaSeleccionada)}"
    }

    private fun tituloColumna(): String {
        val valor = valorAnchoVisible(columnaSeleccionada)
        val nombre = when (modoVisual) {
            GridDrawingView.ModoVisual.GRILLA -> "Columna"
            GridDrawingView.ModoVisual.ESTRUCTURA -> "Interior columna"
            GridDrawingView.ModoVisual.VIDRIO -> "Vidrio columna"
        }
        return "$nombre ${columnaSeleccionada + 1}: ${df(valor)}"
    }

    private fun tituloModulo(): String {
        val columna = moduloColumnaSeleccionada
        val fila = moduloFilaSeleccionada
        val filaVisible = filaVisibleEnColumna(columna, fila)
        val ancho = valorAnchoVisible(columna)
        val alto = valorAltoVisible(columna, fila)
        val nombre = when (modoVisual) {
            GridDrawingView.ModoVisual.GRILLA -> "Modulo"
            GridDrawingView.ModoVisual.ESTRUCTURA -> "Interior"
            GridDrawingView.ModoVisual.VIDRIO -> "Vidrio"
        }
        val identificadorNave = naves[columna to fila]?.let { " - Nave $it" }.orEmpty()
        return "$nombre C${columna + 1} F$filaVisible: ${df(ancho)} x ${df(alto)}$identificadorNave"
    }

    private fun siguienteIdentificadorNave(): String {
        var index = naves.size + 1
        while (naves.containsValue("N$index")) {
            index++
        }
        return "N$index"
    }

    private fun labelAncho(): String {
        return when (modoVisual) {
            GridDrawingView.ModoVisual.GRILLA -> "Ancho"
            GridDrawingView.ModoVisual.ESTRUCTURA -> "An. int"
            GridDrawingView.ModoVisual.VIDRIO -> "Vidrio A"
        }
    }

    private fun labelAlto(): String {
        return when (modoVisual) {
            GridDrawingView.ModoVisual.GRILLA -> "Alto"
            GridDrawingView.ModoVisual.ESTRUCTURA -> "Al. int"
            GridDrawingView.ModoVisual.VIDRIO -> "Vidrio H"
        }
    }

    private fun valorAnchoVisible(columna: Int): Float {
        return when (modoVisual) {
            GridDrawingView.ModoVisual.GRILLA -> anchosColumnas[columna]
            GridDrawingView.ModoVisual.VIDRIO -> (anchosColumnas[columna] - gruna).coerceAtLeast(0f)
            GridDrawingView.ModoVisual.ESTRUCTURA -> (anchosColumnas[columna] - ajusteAnchoEstructura(columna)).coerceAtLeast(0f)
        }
    }

    private fun valorAltoVisible(columna: Int, fila: Int): Float {
        return when (modoVisual) {
            GridDrawingView.ModoVisual.GRILLA -> alturasFilasPorColumna[columna][fila]
            GridDrawingView.ModoVisual.VIDRIO -> (alturasFilasPorColumna[columna][fila] - gruna).coerceAtLeast(0f)
            GridDrawingView.ModoVisual.ESTRUCTURA -> {
                (alturasFilasPorColumna[columna][fila] - ajusteAltoEstructura(columna, fila)).coerceAtLeast(0f)
            }
        }
    }

    private fun aplicarAnchoVisibleColumna(columna: Int, valorVisible: Float) {
        val valorReal = when (modoVisual) {
            GridDrawingView.ModoVisual.GRILLA -> valorVisible
            GridDrawingView.ModoVisual.VIDRIO -> valorVisible + gruna
            GridDrawingView.ModoVisual.ESTRUCTURA -> valorVisible + ajusteAnchoEstructura(columna)
        }
        aplicarAnchoColumna(columna, valorReal)
    }

    private fun aplicarAltoVisibleAFilaGlobal(fila: Int, valorVisible: Float) {
        alturasFilasPorColumna.indices.forEach { columna ->
            if (fila in alturasFilasPorColumna[columna].indices) {
                aplicarAltoVisibleFilaColumna(columna, fila, valorVisible, refrescar = false)
            }
        }
        refrescarDespuesDeCambio()
    }

    private fun aplicarAltoVisibleFilaColumna(
        columna: Int,
        fila: Int,
        valorVisible: Float,
        refrescar: Boolean = true
    ) {
        val valorReal = when (modoVisual) {
            GridDrawingView.ModoVisual.GRILLA -> valorVisible
            GridDrawingView.ModoVisual.VIDRIO -> valorVisible + gruna
            GridDrawingView.ModoVisual.ESTRUCTURA -> valorVisible + ajusteAltoEstructura(columna, fila)
        }
        aplicarAltoFilaColumna(columna, fila, valorReal, refrescar)
    }

    private fun ajusteAnchoEstructura(columna: Int): Float {
        val inicio = if (columna == 0) marco else tubo / 2f
        val fin = if (columna == anchosColumnas.lastIndex) marco else tubo / 2f
        return inicio + fin
    }

    private fun ajusteAltoEstructura(columna: Int, fila: Int): Float {
        val filas = alturasFilasPorColumna[columna]
        val inicio = if (fila == 0) marco else tubo / 2f
        val fin = if (fila == filas.lastIndex) marco else tubo / 2f
        return inicio + fin
    }

    private fun aplicarAnchoColumna(columna: Int, valor: Float) {
        if (valor >= anchoTotal && anchosColumnas.size > 1) {
            Toast.makeText(act, "El ancho excede el total", Toast.LENGTH_SHORT).show()
            return
        }
        anchosColumnas[columna] = valor
        distribuirAnchoRestante(columna)
        refrescarDespuesDeCambio()
    }

    private fun distribuirAnchoRestante(columnaFija: Int) {
        val restante = anchoTotal - anchosColumnas[columnaFija]
        val otras = anchosColumnas.indices.filter { it != columnaFija }
        if (otras.isEmpty()) {
            anchosColumnas[columnaFija] = anchoTotal
            return
        }
        val ancho = (restante / otras.size).coerceAtLeast(1f)
        otras.forEach { anchosColumnas[it] = ancho }
    }

    private fun aplicarAltoAFilaGlobal(fila: Int, valor: Float) {
        alturasFilasPorColumna.indices.forEach { columna ->
            if (fila in alturasFilasPorColumna[columna].indices) {
                aplicarAltoFilaColumna(columna, fila, valor, refrescar = false)
            }
        }
        refrescarDespuesDeCambio()
    }

    private fun aplicarAltoFilaColumna(columna: Int, fila: Int, valor: Float, refrescar: Boolean = true) {
        val estabaEditada = filasEditadasPorColumna[columna][fila]
        filasEditadasPorColumna[columna][fila] = true
        val totalEditado = alturasFilasPorColumna[columna].indices
            .filter { filasEditadasPorColumna[columna][it] }
            .sumOf { index ->
                if (index == fila) valor.toDouble() else alturasFilasPorColumna[columna][index].toDouble()
            }
            .toFloat()
        val filasLibres = alturasFilasPorColumna[columna].size - filasEditadasPorColumna[columna].count { it }
        if (totalEditado > altoTotal || (totalEditado >= altoTotal && filasLibres > 0)) {
            Toast.makeText(act, "El alto excede el total", Toast.LENGTH_SHORT).show()
            filasEditadasPorColumna[columna][fila] = estabaEditada
            return
        }
        alturasFilasPorColumna[columna][fila] = valor
        distribuirAltoRestante(columna)
        if (refrescar) {
            refrescarDespuesDeCambio()
        }
    }

    private fun distribuirAltoRestante(columna: Int) {
        val filas = alturasFilasPorColumna[columna]
        val editadas = filasEditadasPorColumna[columna]
        val filasLibres = filas.indices.filter { !editadas[it] }
        if (filasLibres.isEmpty()) {
            return
        }
        val totalEditado = filas.indices
            .filter { editadas[it] }
            .sumOf { filas[it].toDouble() }
            .toFloat()
        val restante = (altoTotal - totalEditado).coerceAtLeast(0f)
        val alto = restante / filasLibres.size
        filasLibres.forEach { filas[it] = alto }
    }

    private fun agregarColumna(desdeColumna: Int) {
        val insertIndex = (desdeColumna + 1).coerceIn(0, anchosColumnas.size)
        val filasBase = alturasFilasPorColumna.getOrNull(desdeColumna)?.toMutableList() ?: mutableListOf(altoTotal)
        anchosColumnas.add(insertIndex, anchoTotal / (anchosColumnas.size + 1))
        alturasFilasPorColumna.add(insertIndex, filasBase)
        filasEditadasPorColumna.add(insertIndex, MutableList(filasBase.size) { false })
        desplazarNavesPorColumna(insertIndex, 1)
        repartirAnchosIguales()
        columnaSeleccionada = insertIndex
        moduloColumnaSeleccionada = -1
        moduloFilaSeleccionada = -1
        refrescarDespuesDeCambio()
    }

    private fun eliminarColumna(columna: Int) {
        if (anchosColumnas.size <= 1) {
            Toast.makeText(act, "Debe existir al menos una columna", Toast.LENGTH_SHORT).show()
            return
        }
        anchosColumnas.removeAt(columna)
        alturasFilasPorColumna.removeAt(columna)
        filasEditadasPorColumna.removeAt(columna)
        eliminarODesplazarNavesPorColumna(columna)
        repartirAnchosIguales()
        columnaSeleccionada = columna.coerceAtMost(anchosColumnas.lastIndex)
        moduloColumnaSeleccionada = -1
        moduloFilaSeleccionada = -1
        refrescarDespuesDeCambio()
    }

    private fun agregarFilaAColumnas(desdeFila: Int) {
        alturasFilasPorColumna.indices.forEach { columna ->
            val insertIndex = (desdeFila + 1).coerceIn(0, alturasFilasPorColumna[columna].size)
            alturasFilasPorColumna[columna].add(insertIndex, altoTotal / (alturasFilasPorColumna[columna].size + 1))
            filasEditadasPorColumna[columna].add(insertIndex, false)
            desplazarNavesPorFila(columna, insertIndex, 1)
            repartirAltosIguales(columna)
        }
        filaSeleccionada = (desdeFila + 1).coerceAtLeast(0)
        columnaSeleccionada = -1
        moduloColumnaSeleccionada = -1
        moduloFilaSeleccionada = -1
        refrescarDespuesDeCambio()
    }

    private fun eliminarFilaDeColumnas(fila: Int) {
        var eliminado = false
        alturasFilasPorColumna.indices.forEach { columna ->
            val filas = alturasFilasPorColumna[columna]
            if (filas.size > 1 && fila in filas.indices) {
                filas.removeAt(fila)
                filasEditadasPorColumna[columna].removeAt(fila)
                eliminarODesplazarNavesPorFila(columna, fila)
                repartirAltosIguales(columna)
                eliminado = true
            }
        }
        if (!eliminado) {
            Toast.makeText(act, "Debe existir al menos una fila", Toast.LENGTH_SHORT).show()
            return
        }
        filaSeleccionada = fila.coerceAtMost(maxFilaExistente())
        columnaSeleccionada = -1
        moduloColumnaSeleccionada = -1
        moduloFilaSeleccionada = -1
        refrescarDespuesDeCambio()
    }

    private fun agregarFilaEnColumna(columna: Int) {
        val filas = alturasFilasPorColumna[columna]
        val insertIndex = (filaSeleccionada + 1).coerceIn(0, filas.size)
        filas.add(insertIndex, altoTotal / (filas.size + 1))
        filasEditadasPorColumna[columna].add(insertIndex, false)
        desplazarNavesPorFila(columna, insertIndex, 1)
        repartirAltosIguales(columna)
        moduloFilaSeleccionada = -1
        refrescarDespuesDeCambio()
    }

    private fun eliminarFilaEnColumna(columna: Int) {
        val filas = alturasFilasPorColumna[columna]
        if (filas.size <= 1) {
            Toast.makeText(act, "La columna debe tener al menos una fila", Toast.LENGTH_SHORT).show()
            return
        }
        val fila = filaSeleccionada.coerceIn(0, filas.lastIndex)
        filas.removeAt(fila)
        filasEditadasPorColumna[columna].removeAt(fila)
        eliminarODesplazarNavesPorFila(columna, fila)
        repartirAltosIguales(columna)
        filaSeleccionada = fila.coerceAtMost(filas.lastIndex)
        moduloFilaSeleccionada = -1
        refrescarDespuesDeCambio()
    }

    private fun repartirAnchosIguales() {
        val ancho = anchoTotal / anchosColumnas.size
        anchosColumnas.indices.forEach { anchosColumnas[it] = ancho }
    }

    private fun repartirAltosIguales(columna: Int) {
        val filas = alturasFilasPorColumna[columna]
        val alto = altoTotal / filas.size
        filas.indices.forEach { filas[it] = alto }
        filasEditadasPorColumna[columna] = MutableList(filas.size) { false }
    }

    private fun maxFilaExistente(): Int {
        return alturasFilasPorColumna.maxOfOrNull { it.lastIndex } ?: -1
    }

    private fun desplazarNavesPorColumna(columnaDesde: Int, delta: Int) {
        val actualizadas = linkedMapOf<Pair<Int, Int>, String>()
        naves.forEach { (posicion, identificador) ->
            val nuevaColumna = if (posicion.first >= columnaDesde) posicion.first + delta else posicion.first
            actualizadas[nuevaColumna to posicion.second] = identificador
        }
        naves.clear()
        naves.putAll(actualizadas)
    }

    private fun eliminarODesplazarNavesPorColumna(columnaEliminada: Int) {
        val actualizadas = linkedMapOf<Pair<Int, Int>, String>()
        naves.forEach { (posicion, identificador) ->
            when {
                posicion.first < columnaEliminada -> actualizadas[posicion] = identificador
                posicion.first > columnaEliminada -> actualizadas[(posicion.first - 1) to posicion.second] = identificador
            }
        }
        naves.clear()
        naves.putAll(actualizadas)
    }

    private fun desplazarNavesPorFila(columna: Int, filaDesde: Int, delta: Int) {
        val actualizadas = linkedMapOf<Pair<Int, Int>, String>()
        naves.forEach { (posicion, identificador) ->
            val nuevaPosicion = if (posicion.first == columna && posicion.second >= filaDesde) {
                posicion.first to posicion.second + delta
            } else {
                posicion
            }
            actualizadas[nuevaPosicion] = identificador
        }
        naves.clear()
        naves.putAll(actualizadas)
    }

    private fun eliminarODesplazarNavesPorFila(columna: Int, filaEliminada: Int) {
        val actualizadas = linkedMapOf<Pair<Int, Int>, String>()
        naves.forEach { (posicion, identificador) ->
            when {
                posicion.first != columna -> actualizadas[posicion] = identificador
                posicion.second < filaEliminada -> actualizadas[posicion] = identificador
                posicion.second > filaEliminada -> actualizadas[posicion.first to posicion.second - 1] = identificador
            }
        }
        naves.clear()
        naves.putAll(actualizadas)
    }

    private fun normalizarNaves() {
        val invalidas = naves.keys.filter { (columna, fila) ->
            columna !in anchosColumnas.indices || fila !in alturasFilasPorColumna.getOrNull(columna).orEmpty().indices
        }
        invalidas.forEach { naves.remove(it) }
    }

    private fun filaVisibleGlobal(filaInterna: Int): Int {
        return (maxFilaExistente() + 1 - filaInterna).coerceAtLeast(1)
    }

    private fun filaVisibleEnColumna(columna: Int, filaInterna: Int): Int {
        val cantidadFilas = alturasFilasPorColumna.getOrNull(columna)?.size ?: 0
        return (cantidadFilas - filaInterna).coerceAtLeast(1)
    }

    private fun normalizarDatos() {
        if (anchosColumnas.isEmpty()) {
            anchosColumnas = mutableListOf(anchoTotal)
        }
        while (alturasFilasPorColumna.size < anchosColumnas.size) {
            alturasFilasPorColumna.add(mutableListOf(altoTotal))
        }
        while (alturasFilasPorColumna.size > anchosColumnas.size) {
            alturasFilasPorColumna.removeAt(alturasFilasPorColumna.lastIndex)
        }
        alturasFilasPorColumna.indices.forEach { columna ->
            if (alturasFilasPorColumna[columna].isEmpty()) {
                alturasFilasPorColumna[columna].add(altoTotal)
            }
        }
    }

    private fun normalizarFilasEditadas() {
        if (!::filasEditadasPorColumna.isInitialized) {
            filasEditadasPorColumna = MutableList(alturasFilasPorColumna.size) { columna ->
                MutableList(alturasFilasPorColumna[columna].size) { false }
            }
            return
        }
        while (filasEditadasPorColumna.size < alturasFilasPorColumna.size) {
            val columna = filasEditadasPorColumna.size
            filasEditadasPorColumna.add(MutableList(alturasFilasPorColumna[columna].size) { false })
        }
        while (filasEditadasPorColumna.size > alturasFilasPorColumna.size) {
            filasEditadasPorColumna.removeAt(filasEditadasPorColumna.lastIndex)
        }
        alturasFilasPorColumna.indices.forEach { columna ->
            while (filasEditadasPorColumna[columna].size < alturasFilasPorColumna[columna].size) {
                filasEditadasPorColumna[columna].add(false)
            }
            while (filasEditadasPorColumna[columna].size > alturasFilasPorColumna[columna].size) {
                filasEditadasPorColumna[columna].removeAt(filasEditadasPorColumna[columna].lastIndex)
            }
        }
    }

    private fun refrescarDespuesDeCambio() {
        normalizarDatos()
        normalizarFilasEditadas()
        normalizarNaves()
        filaSeleccionada = filaSeleccionada.coerceAtMost(maxFilaExistente())
        columnaSeleccionada = columnaSeleccionada.coerceAtMost(anchosColumnas.lastIndex)
        if (moduloColumnaSeleccionada !in anchosColumnas.indices ||
            moduloFilaSeleccionada !in alturasFilasPorColumna.getOrNull(moduloColumnaSeleccionada).orEmpty().indices
        ) {
            moduloColumnaSeleccionada = -1
            moduloFilaSeleccionada = -1
        }
        updateGrid()
        actualizarSeleccionVisual()
        renderPanelSeleccion()
    }

    private fun updateGrid() {
        gridDrawingView.configurarParametros(anchoTotal, altoTotal, anchosColumnas, alturasFilasPorColumna)
        gridDrawingView.setNaves(naves)
        gridDrawingView.setModoVisual(modoVisual, marco, tubo, gruna)
    }

    /** Avisa a la calculadora de que el diseño cambió, para que recalcule con lo nuevo. */
    private fun devolverResultado() {
        onCambio()
    }
}
