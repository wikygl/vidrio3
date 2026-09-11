package crystal.crystal.taller

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.RectF
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class SketchMedidasView @JvmOverloads constructor(
    context: android.content.Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private companion object {
        const val TEMPLATE_F1 = "F1"
        const val TEMPLATE_F2 = "F2"
        const val TEMPLATE_F3 = "F3"
        const val TEMPLATE_F4 = "F4"
        const val TEMPLATE_F5 = "F5"
        const val TEMPLATE_F6 = "F6"
        const val TEMPLATE_ROUNDED = "ROUNDED_CORNERS"
    }

    data class MedidaPrincipal(
        val anchoCm: Float,
        val altoCm: Float
    )

    enum class Tool {
        NONE, FREEHAND, RECTANGLE, TRIANGLE, CIRCLE, TEXT, LINE, ORTHO_LINE, SELECT
    }

    private enum class Axis {
        HORIZONTAL, VERTICAL
    }

    private enum class CotaType {
        WIDTH, HEIGHT, DIAMETER, LENGTH,
        RECT_TOP, RECT_RIGHT, RECT_BOTTOM, RECT_LEFT,
        COMPOSITE_SIDE,
        F5_DESARROLLO,
        F5_FLECHA,
        F6_RADIO,
        ROUNDED_RADIUS
    }

    private sealed class Element {
        data class Freehand(val path: Path) : Element()
        data class Composite(
            val path: Path,
            var widthCm: Float,
            var heightCm: Float,
            val contours: MutableList<MutableList<PointF>>,
            val sideCms: MutableList<MutableList<Float>>,
            val template: String? = null,
            var rotationDeg: Float = 0f,   // ángulo acumulado; para editar cotas en el marco local
            val bloqueados: MutableSet<Long> = mutableSetOf(),  // lados bloqueados (clave contorno+lado)
            var reflejado: Boolean = false  // reflejada horizontalmente (orden de vértices invertido)
        ) : Element()
        data class Shape(
            val tool: Tool,
            val rect: RectF,
            val start: PointF,
            val end: PointF,
            var widthCm: Float,
            var heightCm: Float,
            var diameterCm: Float,
            var lengthCm: Float,
            val topLeft: PointF,
            val topRight: PointF,
            val bottomRight: PointF,
            val bottomLeft: PointF,
            var topCm: Float,
            var rightCm: Float,
            var bottomCm: Float,
            var leftCm: Float,
            val cotaHint: String? = null
        ) : Element()
        data class Group(val children: MutableList<Element>) : Element()
        data class TextLabel(
            var text: String,
            var x: Float,
            var y: Float,
            var textSize: Float = 34f
        ) : Element()
        data class InfoBox(
            var text: String,
            val rect: RectF,
            var textSize: Float = 34f
        ) : Element()
        data class Symbol(
            val drawableName: String,
            val rect: RectF
        ) : Element()
    }

    private data class CotaHit(
        val elementIndex: Int,
        val type: CotaType,
        val rect: RectF,
        val contourIndex: Int? = null,
        val sideIndex: Int? = null
    )

    private data class RecurrenteF4Dims(
        val centralLeft: Float,
        val top: Float,
        val centralW: Float,
        val centralH: Float,
        val leftW: Float,
        val rightW: Float,
        val addonH: Float
    )

    private data class RecurrenteF5Dims(
        val left: Float,
        val top: Float,
        val rectW: Float,
        val rectH: Float,
        val flecha: Float
    )

    private data class RecurrenteF6Dims(
        val left: Float,
        val top: Float,
        val rectW: Float,
        val rectH: Float,
        val radius: Float
    )

    private data class RoundedCornerDims(
        val left: Float,
        val top: Float,
        val rectW: Float,
        val rectH: Float,
        val radii: MutableList<Float>
    )

    private val elementos = mutableListOf<Element>()
    private val rehacerElementos = mutableListOf<Element>()

    // ===== Historial de UNDO/REDO por snapshots (cada acción, no solo inserciones) =====
    private var estadoPrevio: List<Element> = emptyList()  // estado antes de la acción en curso
    private val historia = ArrayDeque<List<Element>>()
    private val futuro = ArrayDeque<List<Element>>()
    private val maxHistorial = 80

    // Long-press sobre una cota → bloquear/desbloquear ese lado (tap normal = editar).
    private var cotaPendiente: CotaHit? = null
    private var cotaDownXY = PointF()
    private var longPressFired = false
    private val longPressRunnable = Runnable {
        cotaPendiente?.let { longPressFired = true; alternarBloqueoLado(it) }
    }
    private val cotaHits = mutableListOf<CotaHit>()
    private val cotaTextRects = mutableListOf<RectF>()
    private val selectedIndices = mutableListOf<Int>()
    private val trazoActual = Path()
    private var startPoint = PointF()
    private var currentPoint = PointF()
    private var dragIndex: Int? = null
    private var lastDragPoint = PointF()
    private var movedSelection = false
    private var dibujando = false
    private var herramienta = Tool.NONE
    private var rectangleRoundedCorner: Int? = null
    private var rectangleRoundedRadiusCm: Float = 0f
    private var fondo: Bitmap? = null
    private var viewScale = 1f
    private var viewOffsetX = 0f
    private var viewOffsetY = 0f
    private var viewportGesture = false
    private var lastViewportFocus = PointF()
    private var lastViewportSpan = 0f
    private var panSinHerramienta = false
    private var lastPanPoint = PointF()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(30, 30, 30)
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val previewPaint = Paint(paint).apply {
        color = Color.rgb(21, 101, 192)
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(14f, 10f), 0f)
    }

    private val selectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(216, 27, 96)
        style = Paint.Style.STROKE
        strokeWidth = 3f
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 8f), 0f)
    }

    private val ladoBloqueadoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(229, 57, 53)   // rojo: lado bloqueado
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }

    private val cotaLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(80, 88, 96)
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
    }

    private val cotaTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(13, 71, 161)
        style = Paint.Style.FILL
        textSize = spToPx(12f)
        textAlign = Paint.Align.CENTER
    }

    private val cotaBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(210, 247, 244, 236)
        style = Paint.Style.FILL
    }

    private val sketchTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(30, 30, 30)
        style = Paint.Style.FILL
        textSize = 34f
    }

    private val fondoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cotaHits.clear()
        cotaTextRects.clear()
        canvas.save()
        canvas.translate(viewOffsetX, viewOffsetY)
        canvas.scale(viewScale, viewScale)
        drawBackground(canvas)
        elementos.forEachIndexed { index, element -> drawElement(canvas, index, element, true) }
        drawSelection(canvas)
        if (dibujando) {
            canvas.drawPath(trazoActual, if (herramienta == Tool.FREEHAND) paint else previewPaint)
        }
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount >= 2 || viewportGesture) {
            removeCallbacks(longPressRunnable); cotaPendiente = null
            manejarViewportGesture(event)
            return true
        }
        // Cota: tap = editar; mantener presionado = bloquear/desbloquear el lado.
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val p = screenToWorld(event.x, event.y)
                val hit = cotaEn(p.x, p.y)
                if (hit != null) {
                    cotaPendiente = hit
                    cotaDownXY = PointF(event.x, event.y)
                    longPressFired = false
                    postDelayed(longPressRunnable, 450L)
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (cotaPendiente != null) {
                    val slop = android.view.ViewConfiguration.get(context).scaledTouchSlop
                    if (kotlin.math.hypot((event.x - cotaDownXY.x).toDouble(), (event.y - cotaDownXY.y).toDouble()) > slop) {
                        removeCallbacks(longPressRunnable); cotaPendiente = null
                    }
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (cotaPendiente != null) {
                    removeCallbacks(longPressRunnable)
                    val h = cotaPendiente
                    cotaPendiente = null
                    if (!longPressFired && event.actionMasked == MotionEvent.ACTION_UP && h != null) editarCota(h)
                    return true
                }
            }
        }
        if (herramienta == Tool.NONE) {
            manejarPanSinHerramienta(event)
            return true
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val p = screenToWorld(event.x, event.y)
                if (herramienta == Tool.SELECT) {
                    manejarSeleccionDown(p.x, p.y)
                    return true
                }
                if (herramienta == Tool.TEXT) {
                    pedirTexto(p.x, p.y)
                    return true
                }
                parent?.requestDisallowInterceptTouchEvent(true)
                val inicio = if (herramienta == Tool.LINE || herramienta == Tool.ORTHO_LINE) {
                    snapLibre(p) ?: p
                } else {
                    p
                }
                startPoint = inicio
                currentPoint = inicio
                trazoActual.reset()
                trazoActual.moveTo(inicio.x, inicio.y)
                dibujando = true
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val p = screenToWorld(event.x, event.y)
                if (herramienta == Tool.SELECT) {
                    moverSeleccion(p.x, p.y)
                    return true
                }
                currentPoint = p
                actualizarTrazo(p.x, p.y)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val p = screenToWorld(event.x, event.y)
                if (herramienta == Tool.SELECT) {
                    finalizarMoverSeleccion()
                    return true
                }
                if (dibujando) {
                    currentPoint = p
                    actualizarTrazo(p.x, p.y)
                    crearElemento(p.x, p.y)?.let {
                        elementos.add(it)
                        registrarAccion()
                    }
                    trazoActual.reset()
                    dibujando = false
                    invalidate()
                }
                parent?.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return true
    }

    private fun manejarViewportGesture(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> iniciarViewportGesture(event)
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount >= 2) actualizarViewportGesture(event)
            }
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                viewportGesture = event.pointerCount > 2
                parent?.requestDisallowInterceptTouchEvent(false)
                if (!viewportGesture) {
                    dibujando = false
                    trazoActual.reset()
                }
            }
        }
    }

    private fun manejarPanSinHerramienta(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                panSinHerramienta = true
                lastPanPoint = PointF(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                if (panSinHerramienta) {
                    viewOffsetX += event.x - lastPanPoint.x
                    viewOffsetY += event.y - lastPanPoint.y
                    lastPanPoint = PointF(event.x, event.y)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                panSinHerramienta = false
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
    }

    private fun iniciarViewportGesture(event: MotionEvent) {
        parent?.requestDisallowInterceptTouchEvent(true)
        viewportGesture = true
        dibujando = false
        trazoActual.reset()
        lastViewportFocus = foco(event)
        lastViewportSpan = span(event)
    }

    private fun actualizarViewportGesture(event: MotionEvent) {
        val focus = foco(event)
        val newSpan = span(event)
        val spanAnterior = lastViewportSpan.takeIf { it > 1f } ?: newSpan
        val scaleFactor = (newSpan / spanAnterior).takeIf { it.isFinite() && it > 0f } ?: 1f
        zoomAt(scaleFactor, focus.x, focus.y)
        viewOffsetX += focus.x - lastViewportFocus.x
        viewOffsetY += focus.y - lastViewportFocus.y
        lastViewportFocus = focus
        lastViewportSpan = newSpan
        invalidate()
    }

    private fun foco(event: MotionEvent): PointF {
        var x = 0f
        var y = 0f
        for (i in 0 until event.pointerCount) {
            x += event.getX(i)
            y += event.getY(i)
        }
        return PointF(x / event.pointerCount, y / event.pointerCount)
    }

    private fun span(event: MotionEvent): Float {
        if (event.pointerCount < 2) return 0f
        val dx = event.getX(0) - event.getX(1)
        val dy = event.getY(0) - event.getY(1)
        return hypot(dx.toDouble(), dy.toDouble()).toFloat()
    }

    private fun screenToWorld(x: Float, y: Float): PointF {
        val scale = viewScale.coerceAtLeast(0.05f)
        return PointF((x - viewOffsetX) / scale, (y - viewOffsetY) / scale)
    }

    private fun zoomAt(factor: Float, screenX: Float, screenY: Float) {
        val oldScale = viewScale
        val newScale = (oldScale * factor).coerceIn(0.15f, 6f)
        if (abs(newScale - oldScale) < 0.0001f) return
        val worldBefore = screenToWorld(screenX, screenY)
        viewScale = newScale
        viewOffsetX = screenX - worldBefore.x * viewScale
        viewOffsetY = screenY - worldBefore.y * viewScale
    }

    fun zoomIn() {
        zoomAt(1.2f, width / 2f, height / 2f)
        invalidate()
    }

    fun zoomOut() {
        zoomAt(1f / 1.2f, width / 2f, height / 2f)
        invalidate()
    }

    fun fitContentInView() {
        val bounds = boundsForContent() ?: run {
            resetViewport()
            return
        }
        if (width <= 0 || height <= 0 || bounds.width() <= 1f || bounds.height() <= 1f) {
            resetViewport()
            return
        }
        val margin = 42f * resources.displayMetrics.density
        val availableW = (width - margin * 2f).coerceAtLeast(1f)
        val availableH = (height - margin * 2f).coerceAtLeast(1f)
        viewScale = minOf(availableW / bounds.width(), availableH / bounds.height()).coerceIn(0.15f, 6f)
        viewOffsetX = (width - bounds.width() * viewScale) / 2f - bounds.left * viewScale
        viewOffsetY = (height - bounds.height() * viewScale) / 2f - bounds.top * viewScale
        invalidate()
    }

    fun resetViewport() {
        viewScale = 1f
        viewOffsetX = 0f
        viewOffsetY = 0f
        invalidate()
    }

    fun setTool(tool: Tool) {
        herramienta = tool
        trazoActual.reset()
        dibujando = false
        dragIndex = null
        if (tool != Tool.SELECT) selectedIndices.clear()
        if (tool != Tool.RECTANGLE) clearRectangleRoundedCorner()
        invalidate()
    }

    fun setRectangleRoundedCorner(corner: Int?, radiusCm: Float = 0f) {
        rectangleRoundedCorner = corner?.takeIf { it in 0..3 }
        rectangleRoundedRadiusCm = radiusCm.coerceAtLeast(0f)
    }

    fun clearRectangleRoundedCorner() {
        rectangleRoundedCorner = null
        rectangleRoundedRadiusCm = 0f
    }

    fun insertarSimbolo(drawableName: String) {
        if (drawableIdForName(drawableName) == 0) return
        val size = symbolSize()
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val rect = posicionLibreParaSimbolo(RectF(
            center.x - size / 2f,
            center.y - size / 2f,
            center.x + size / 2f,
            center.y + size / 2f
        ))
        elementos.add(Element.Symbol(drawableName, rect))
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
    }

    fun insertarSimboloCentroDiseno(drawableName: String) {
        if (drawableIdForName(drawableName) == 0) return
        val bounds = boundsForDisenoBase() ?: boundsReferenciaBisagras()
        val size = symbolSize()
        val rect = posicionLibreParaSimbolo(RectF(
            bounds.centerX() - size / 2f,
            bounds.centerY() - size / 2f,
            bounds.centerX() + size / 2f,
            bounds.centerY() + size / 2f
        ))
        elementos.add(Element.Symbol(drawableName, rect))
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
    }

    fun insertarMichi(valor: String) {
        if (drawableIdForName("michi") == 0) return
        val bounds = boundsForDisenoBase() ?: boundsReferenciaBisagras()
        val size = symbolSize()
        val gap = 8f * resources.displayMetrics.density
        sketchTextPaint.textSize = 34f
        val textWidth = sketchTextPaint.measureText(valor).coerceAtLeast(1f)
        val groupWidth = size + gap + textWidth
        val centerY = bounds.bottom + size * 1.15f
        val left = bounds.centerX() - groupWidth / 2f
        val symbolRect = posicionLibreParaSimbolo(RectF(
            left,
            centerY - size / 2f,
            left + size,
            centerY + size / 2f
        ))
        val textX = symbolRect.right + gap
        val textY = symbolRect.centerY() + 12f
        elementos.add(Element.Symbol("michi", symbolRect))
        val symbolIndex = elementos.lastIndex
        elementos.add(Element.TextLabel(valor, textX, textY, 34f))
        selectedIndices.clear()
        selectedIndices.add(symbolIndex)
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
    }

    fun insertarSimboloInteriorExterior(drawableName: String) {
        if (drawableName !in setOf("interior", "exterior")) return
        if (drawableIdForName(drawableName) == 0) return
        elementos.removeAll { element ->
            element is Element.Symbol && element.drawableName in setOf("interior", "exterior")
        }
        selectedIndices.clear()
        val bounds = boundsForDisenoBase() ?: boundsReferenciaBisagras()
        val size = symbolSize()
        val margin = 12f * resources.displayMetrics.density
        val rect = posicionLibreParaSimbolo(RectF(
            bounds.right + margin,
            bounds.top,
            bounds.right + margin + size,
            bounds.top + size
        ))
        elementos.add(Element.Symbol(drawableName, rect))
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
    }

    fun insertarBisagras(izquierda: Boolean) {
        if (drawableIdForName("bisagra") == 0) return
        val bounds = boundsForIndices(selectedIndices)
            ?: boundsForContent()
            ?: boundsReferenciaBisagras()
        val size = hingeSize()
        val sideX = if (izquierda) bounds.left else bounds.right
        val posicionesY = listOf(
            bounds.top + bounds.height() * 0.18f,
            bounds.centerY(),
            bounds.top + bounds.height() * 0.82f
        )

        selectedIndices.clear()
        val ocupados = mutableListOf<RectF>()
        posicionesY.forEach { centerY ->
            val rect = posicionLibreParaSimbolo(RectF(
                sideX - size / 2f,
                centerY - size / 2f,
                sideX + size / 2f,
                centerY + size / 2f
            ), fixedX = true, occupied = ocupados)
            elementos.add(Element.Symbol("bisagra", rect))
            ocupados.add(RectF(rect))
            selectedIndices.add(elementos.lastIndex)
        }
        registrarAccion()
        invalidate()
    }

    fun insertarPlantillaVanoExacto() {
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val ancho = cmToPx(120f)
        val alto = cmToPx(180f)
        val left = center.x - ancho / 2f
        val top = center.y - alto / 2f
        val right = center.x + ancho / 2f
        val bottom = center.y + alto / 2f
        val midX = (left + right) / 2f
        val midY = (top + bottom) / 2f
        val labelSize = 24f

        val nuevos = listOf(
            crearShape(Tool.RECTANGLE, PointF(left, top), PointF(right, bottom)),
            crearShape(Tool.LINE, PointF(left, midY), PointF(right, midY)),
            crearShape(Tool.LINE, PointF(midX, top), PointF(midX, bottom)),
            crearShape(Tool.LINE, PointF(left, top), PointF(right, bottom)),
            crearShape(Tool.LINE, PointF(right, top), PointF(left, bottom)),
            Element.TextLabel("VANO EXACTO", left, top - 34f, labelSize),
            Element.TextLabel("ancho medio", right + 18f, midY, labelSize),
            Element.TextLabel("alto centro", midX + 18f, bottom + 30f, labelSize),
            Element.TextLabel("diagonales", right + 18f, top + 34f, labelSize)
        )
        agregarPlantilla(nuevos)
    }

    fun insertarPlantillaGraderia(pasos: Int = 3, pasoCm: Float = 30f, contrapasoCm: Float = 18f) {
        val cantidadPasos = pasos.coerceIn(1, 40)
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val huella = cmToPx(pasoCm.coerceAtLeast(1f))
        val contraHuella = cmToPx(contrapasoCm.coerceAtLeast(1f))
        val left = center.x - (huella * cantidadPasos) / 2f
        val bottom = center.y + (contraHuella * cantidadPasos) / 2f
        val nuevos = mutableListOf<Element>()
        var x = left
        var y = bottom
        val inicio = PointF(x, y)
        repeat(cantidadPasos) {
            val contraStart = PointF(x, y)
            val contraEnd = PointF(x, y - contraHuella)
            nuevos.add(crearShape(Tool.LINE, contraStart, contraEnd, "GRADA_CONTRAPASO"))
            y -= contraHuella
            val pasoStart = PointF(x, y)
            val pasoEnd = PointF(x + huella, y)
            nuevos.add(crearShape(Tool.LINE, pasoStart, pasoEnd, "GRADA_PASO"))
            x += huella
        }
        val fin = PointF(x, y)
        nuevos.add(crearShape(Tool.LINE, inicio, fin, "GRADA_TOTAL_COTA"))

        agregarPlantilla(nuevos)
    }

    fun insertarPlantillaProducto(
        anchoCm: Float,
        altoCm: Float,
        hojas: Int = 1,
        bisagra: String? = null,
        vista: String? = null,
        apertura: String? = null,
        etiqueta: String? = null
    ) {
        val ancho = anchoCm.coerceAtLeast(20f)
        val alto = altoCm.coerceAtLeast(20f)
        val anchoPx = cmToPx(ancho)
        val altoPx = cmToPx(alto)
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = center.x - anchoPx / 2f
        val top = center.y - altoPx / 2f
        val right = left + anchoPx
        val bottom = top + altoPx
        val rect = RectF(left, top, right, bottom)

        val nuevos = mutableListOf<Element>()
        nuevos.add(crearShape(Tool.RECTANGLE, PointF(left, top), PointF(right, bottom)))

        val divisiones = hojas.coerceIn(1, 6)
        if (divisiones > 1) {
            val paso = anchoPx / divisiones.toFloat()
            for (i in 1 until divisiones) {
                val x = left + paso * i
                nuevos.add(crearShape(Tool.LINE, PointF(x, top), PointF(x, bottom)))
            }
        }

        if (!etiqueta.isNullOrBlank()) {
            nuevos.add(Element.TextLabel(etiqueta, left, top - 28f, 30f))
        }

        val tieneSimboloBisagra = !bisagra.isNullOrBlank() && drawableIdForName("bisagra") != 0
        if (tieneSimboloBisagra) {
            val size = hingeSize()
            val esIzquierda = bisagra.equals("izquierda", ignoreCase = true)
            val sideX = if (esIzquierda) left else right
            val ys = listOf(
                top + altoPx * 0.18f,
                top + altoPx * 0.5f,
                top + altoPx * 0.82f
            )
            ys.forEach { cy ->
                nuevos.add(
                    Element.Symbol(
                        "bisagra",
                        RectF(sideX - size / 2f, cy - size / 2f, sideX + size / 2f, cy + size / 2f)
                    )
                )
            }
        }

        val drawableVista = vista?.lowercase()
        if (drawableVista in setOf("interior", "exterior") && drawableIdForName(drawableVista!!) != 0) {
            val size = symbolSize()
            val margin = 12f * resources.displayMetrics.density
            nuevos.add(
                Element.Symbol(
                    drawableVista,
                    RectF(right + margin, top, right + margin + size, top + size)
                )
            )
        }

        val drawableApertura = apertura?.lowercase()
        if (drawableApertura in setOf("adentro", "afuera") && drawableIdForName(drawableApertura!!) != 0) {
            val size = symbolSize()
            nuevos.add(
                Element.Symbol(
                    drawableApertura,
                    RectF(
                        rect.centerX() - size / 2f,
                        rect.centerY() - size / 2f,
                        rect.centerX() + size / 2f,
                        rect.centerY() + size / 2f
                    )
                )
            )
        }

        agregarPlantilla(nuevos)
    }

    fun insertarResultadoCompas(restanteCm: Float, angulo: Float, complemento: Float, inglete: Float) {
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val boxW = 190f * resources.displayMetrics.density
        val boxH = 118f * resources.displayMetrics.density
        val rect = RectF(
            center.x - boxW / 2f,
            center.y - boxH / 2f,
            center.x + boxW / 2f,
            center.y + boxH / 2f
        )
        val texto = listOf(
            "COMPAS",
            "Restante: ${formatCm(restanteCm)}",
            "Angulo: ${formatAngle(angulo)} deg",
            "90 - angulo: ${formatAngle(complemento)} deg",
            "Inglete: ${formatAngle(inglete)} deg"
        ).joinToString("\n")
        elementos.add(Element.InfoBox(texto, rect, spToPx(12f)))
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
    }

    private fun agregarPlantilla(nuevos: List<Element>) {
        val startIndex = elementos.size
        elementos.addAll(nuevos)
        selectedIndices.clear()
        nuevos.indices.forEach { selectedIndices.add(startIndex + it) }
        registrarAccion()
        invalidate()
    }

    private fun crearShape(tool: Tool, start: PointF, end: PointF, cotaHint: String? = null): Element.Shape {
        val rect = rectFrom(start, end.x, end.y)
        val lineLengthPx = hypot((end.x - start.x).toDouble(), (end.y - start.y).toDouble()).toFloat()
        val widthCm = pxToCm(rect.width())
        val heightCm = pxToCm(rect.height())
        return Element.Shape(
            tool = tool,
            rect = rect,
            start = PointF(start.x, start.y),
            end = PointF(end.x, end.y),
            widthCm = widthCm,
            heightCm = heightCm,
            diameterCm = pxToCm(maxOf(rect.width(), rect.height())),
            lengthCm = pxToCm(lineLengthPx),
            topLeft = PointF(rect.left, rect.top),
            topRight = PointF(rect.right, rect.top),
            bottomRight = PointF(rect.right, rect.bottom),
            bottomLeft = PointF(rect.left, rect.bottom),
            topCm = widthCm,
            rightCm = heightCm,
            bottomCm = widthCm,
            leftCm = heightCm,
            cotaHint = cotaHint
        )
    }

    private fun symbolSize(): Float = 28f * resources.displayMetrics.density

    private fun hingeSize(): Float = 22f * resources.displayMetrics.density

    private fun boundsForDisenoBase(): RectF? {
        var union: RectF? = null
        for (element in elementos) {
            if (element is Element.Symbol) continue
            val bounds = boundsForElement(element)
            if (union == null) {
                union = RectF(bounds)
            } else {
                union.union(bounds)
            }
        }
        return union ?: boundsForContent()
    }

    private fun posicionLibreParaSimbolo(
        original: RectF,
        fixedX: Boolean = false,
        occupied: List<RectF> = emptyList()
    ): RectF {
        if (!chocaConCotaOSimbolo(original, occupied)) return original
        val step = original.height().coerceAtLeast(18f)
        val candidates = mutableListOf<RectF>()
        for (level in 1..6) {
            val distance = step * level
            candidates.add(RectF(original).apply { offset(0f, -distance) })
            candidates.add(RectF(original).apply { offset(0f, distance) })
            if (!fixedX) {
                candidates.add(RectF(original).apply { offset(-distance, 0f) })
                candidates.add(RectF(original).apply { offset(distance, 0f) })
                candidates.add(RectF(original).apply { offset(-distance, -distance) })
                candidates.add(RectF(original).apply { offset(distance, -distance) })
                candidates.add(RectF(original).apply { offset(-distance, distance) })
                candidates.add(RectF(original).apply { offset(distance, distance) })
            }
        }
        return candidates.firstOrNull { !chocaConCotaOSimbolo(it, occupied) }
            ?: candidates.minByOrNull { areaChoqueSimbolo(it, occupied) }
            ?: original
    }

    private fun chocaConCotaOSimbolo(rect: RectF, occupied: List<RectF>): Boolean {
        val padded = RectF(rect).apply { inset(-6f, -6f) }
        return cotaTextRects.any { RectF.intersects(padded, it) } ||
            occupied.any { RectF.intersects(padded, it) }
    }

    private fun areaChoqueSimbolo(rect: RectF, occupied: List<RectF>): Float {
        val padded = RectF(rect).apply { inset(-6f, -6f) }
        return (cotaTextRects + occupied).sumOf { existing ->
            val left = maxOf(padded.left, existing.left)
            val top = maxOf(padded.top, existing.top)
            val right = minOf(padded.right, existing.right)
            val bottom = minOf(padded.bottom, existing.bottom)
            if (right > left && bottom > top) ((right - left) * (bottom - top)).toDouble() else 0.0
        }.toFloat()
    }

    private fun boundsReferenciaBisagras(): RectF {
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val alto = 180f * resources.displayMetrics.density
        val ancho = 96f * resources.displayMetrics.density
        return RectF(
            center.x - ancho / 2f,
            center.y - alto / 2f,
            center.x + ancho / 2f,
            center.y + alto / 2f
        )
    }

    private fun pedirTexto(x: Float, y: Float) {
        val input = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setSingleLine(true)
            hint = "Texto"
        }
        AlertDialog.Builder(context)
            .setTitle("Texto")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val texto = input.text?.toString()?.trim().orEmpty()
                if (texto.isNotBlank()) {
                    elementos.add(Element.TextLabel(texto, x, y, spToPx(12f)))
                    registrarAccion()
                    invalidate()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    fun weldSelected(): Boolean {
        if (selectedIndices.size != 2) return false
        val aIndex = selectedIndices[0]
        val bIndex = selectedIndices[1]
        val pathA = pathForElement(elementos.getOrNull(aIndex) ?: return false)
        val pathB = pathForElement(elementos.getOrNull(bIndex) ?: return false)
        val result = Path(pathA)
        result.op(pathB, Path.Op.UNION)
        reemplazarSeleccionCon(compositeFromPath(result))
        registrarAccion()
        return true
    }

    fun subtractSelected(): Boolean {
        if (selectedIndices.size != 2) return false
        val cutterIndex = selectedIndices[0]
        val baseIndex = selectedIndices[1]
        val cutterElement = elementos.getOrNull(cutterIndex) ?: return false
        val baseElement = elementos.getOrNull(baseIndex) ?: return false
        val cutter = pathForElement(cutterElement)
        val base = pathForElement(baseElement)
        val result = Path(base)
        result.op(cutter, Path.Op.DIFFERENCE)
        reemplazarSeleccionCon(compositeFromDifference(result, baseElement, cutterElement))
        registrarAccion()
        return true
    }

    fun duplicateSelected(): Boolean {
        if (selectedIndices.size != 1) return false
        val original = elementos.getOrNull(selectedIndices.first()) ?: return false
        val copy = cloneElement(original)
        val bounds = boundsForElement(original)
        translateElement(copy, bounds.width() + snapThresholdPx(), 0f)
        elementos.add(copy)
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
        return true
    }

    fun mirrorCopySelected(): Boolean {
        if (selectedIndices.size != 1) return false
        val original = elementos.getOrNull(selectedIndices.first()) ?: return false
        val bounds = boundsForElement(original)
        val copy = cloneElement(original)
        mirrorElementHorizontally(copy, bounds.centerX())
        val mirroredBounds = boundsForElement(copy)
        translateElement(copy, bounds.right + snapThresholdPx() - mirroredBounds.left, 0f)
        elementos.add(copy)
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
        return true
    }

    // Refleja la figura seleccionada EN SU SITIO (sin crear copia).
    fun mirrorSelectedInPlace(): Boolean {
        if (selectedIndices.size != 1) return false
        val element = elementos.getOrNull(selectedIndices.first()) ?: return false
        mirrorElementHorizontally(element, boundsForElement(element).centerX())
        registrarAccion()
        invalidate()
        return true
    }

    // Crea varias copias reflejadas en fila, contiguas hacia la derecha.
    fun mirrorCopiesSelected(count: Int): Boolean {
        if (selectedIndices.size != 1) return false
        val original = elementos.getOrNull(selectedIndices.first()) ?: return false
        val total = count.coerceIn(1, 20)
        var refBounds = boundsForElement(original)
        var lastIndex = selectedIndices.first()
        for (k in 0 until total) {
            val copy = cloneElement(original)
            mirrorElementHorizontally(copy, refBounds.centerX())
            val mb = boundsForElement(copy)
            translateElement(copy, refBounds.right + snapThresholdPx() - mb.left, 0f)
            elementos.add(copy)
            lastIndex = elementos.lastIndex
            refBounds = boundsForElement(copy)
        }
        selectedIndices.clear()
        selectedIndices.add(lastIndex)
        registrarAccion()
        invalidate()
        return true
    }

    fun groupSelected(): Boolean {
        if (selectedIndices.size < 2) return false
        val indices = selectedIndices.sorted()
        val children = indices.mapNotNull { index -> elementos.getOrNull(index)?.let { cloneElement(it) } }.toMutableList()
        if (children.size < 2) return false
        indices.sortedDescending().forEach { index ->
            if (index in elementos.indices) elementos.removeAt(index)
        }
        elementos.add(Element.Group(children))
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
        return true
    }

    fun ungroupSelected(): Boolean {
        if (selectedIndices.size != 1) return false
        val index = selectedIndices.first()
        val group = elementos.getOrNull(index) as? Element.Group ?: return false
        elementos.removeAt(index)
        val insertAt = index.coerceIn(0, elementos.size)
        val children = group.children.map { cloneElement(it) }
        elementos.addAll(insertAt, children)
        selectedIndices.clear()
        children.indices.forEach { selectedIndices.add(insertAt + it) }
        registrarAccion()
        invalidate()
        return true
    }

    fun deleteSelected(): Boolean {
        if (selectedIndices.isEmpty()) return false
        selectedIndices.sortedDescending().forEach { index ->
            if (index in elementos.indices) elementos.removeAt(index)
        }
        selectedIndices.clear()
        registrarAccion()
        invalidate()
        return true
    }

    fun scaleSelected(factor: Float): Boolean {
        if (selectedIndices.isEmpty() || factor <= 0f) return false
        val bounds = boundsForIndices(selectedIndices) ?: return false
        val pivot = PointF(bounds.centerX(), bounds.centerY())
        selectedIndices.forEach { index ->
            elementos.getOrNull(index)?.let { scaleElement(it, factor, pivot) }
        }
        registrarAccion()
        invalidate()
        return true
    }

    fun rotateSelected(degrees: Float = 90f): Boolean {
        if (selectedIndices.isEmpty()) return false
        val bounds = boundsForIndices(selectedIndices) ?: return false
        val pivot = PointF(bounds.centerX(), bounds.centerY())
        selectedIndices.forEach { index ->
            elementos.getOrNull(index)?.let { rotateElement(it, degrees, pivot) }
        }
        registrarAccion()
        invalidate()
        return true
    }

    fun redondearEsquinaSeleccionada(esquina: Int, radioCm: Float): Boolean {
        if (selectedIndices.size != 1 || esquina !in 0..3) return false
        val index = selectedIndices.first()
        val element = elementos.getOrNull(index) ?: return false
        val dims = roundedDimsFromElement(element) ?: return false
        val radius = cmToPx(radioCm).coerceIn(cmToPx(1f), minOf(dims.rectW, dims.rectH) / 2f)
        dims.radii[esquina] = radius
        val contour = rectContour(RectF(dims.left, dims.top, dims.left + dims.rectW, dims.top + dims.rectH))
        val contours = mutableListOf(contour)
        elementos[index] = Element.Composite(
            path = pathRoundedCorners(dims),
            widthCm = pxToCm(dims.rectW),
            heightCm = pxToCm(dims.rectH),
            contours = contours,
            sideCms = sideCmsRoundedCorners(dims),
            template = TEMPLATE_ROUNDED
        )
        registrarAccion()
        invalidate()
        return true
    }

    // Copia PROFUNDA de un elemento (Path, puntos, contornos…) para que el snapshot sea independiente.
    private fun copyElement(e: Element): Element = when (e) {
        is Element.Freehand -> Element.Freehand(Path(e.path))
        is Element.Composite -> Element.Composite(
            Path(e.path), e.widthCm, e.heightCm,
            e.contours.map { c -> c.map { PointF(it.x, it.y) }.toMutableList() }.toMutableList(),
            e.sideCms.map { it.toMutableList() }.toMutableList(),
            e.template, e.rotationDeg, e.bloqueados.toMutableSet(), e.reflejado
        )
        is Element.Shape -> Element.Shape(
            e.tool, RectF(e.rect), PointF(e.start.x, e.start.y), PointF(e.end.x, e.end.y),
            e.widthCm, e.heightCm, e.diameterCm, e.lengthCm,
            PointF(e.topLeft.x, e.topLeft.y), PointF(e.topRight.x, e.topRight.y),
            PointF(e.bottomRight.x, e.bottomRight.y), PointF(e.bottomLeft.x, e.bottomLeft.y),
            e.topCm, e.rightCm, e.bottomCm, e.leftCm, e.cotaHint
        )
        is Element.Group -> Element.Group(e.children.map { copyElement(it) }.toMutableList())
        is Element.TextLabel -> Element.TextLabel(e.text, e.x, e.y, e.textSize)
        is Element.InfoBox -> Element.InfoBox(e.text, RectF(e.rect), e.textSize)
        is Element.Symbol -> Element.Symbol(e.drawableName, RectF(e.rect))
    }

    private fun snapshot(): List<Element> = elementos.map { copyElement(it) }

    // Marca una acción YA realizada: guarda en el historial el estado que había ANTES, y toma como
    // nuevo "previo" el estado actual (post-mutación). Debe llamarse DESPUÉS de mutar los elementos.
    private fun registrarAccion() {
        historia.addLast(estadoPrevio)
        while (historia.size > maxHistorial) historia.removeFirst()
        estadoPrevio = snapshot()
        futuro.clear()
    }

    private fun restaurar(snap: List<Element>) {
        elementos.clear()
        elementos.addAll(snap.map { copyElement(it) })
        selectedIndices.clear()
        cotaHits.clear()
    }

    // Fija el estado actual como base del historial (p. ej. al cargar un sketch): undo no va antes de aquí.
    private fun establecerBaseline() {
        estadoPrevio = snapshot()
        historia.clear()
        futuro.clear()
    }

    fun undo() {
        if (historia.isEmpty()) return
        futuro.addLast(estadoPrevio)
        val prev = historia.removeLast()
        estadoPrevio = prev
        restaurar(prev)
        invalidate()
    }

    fun redo() {
        if (futuro.isEmpty()) return
        historia.addLast(estadoPrevio)
        val next = futuro.removeLast()
        estadoPrevio = next
        restaurar(next)
        invalidate()
    }

    fun clear() {
        elementos.clear()
        registrarAccion()
        cotaHits.clear()
        trazoActual.reset()
        fondo = null
        dibujando = false
        resetViewport()
        invalidate()
    }

    fun hasContent(): Boolean = elementos.isNotEmpty() || fondo != null

    fun hasDrawing(): Boolean = elementos.isNotEmpty()

    fun insertarRecurrenteF1() {
        val ancho = 160f
        val alto = 110f
        val corteAncho = 48f
        val corteAlto = 36f
        val left = maxOf(24f, screenToWorld(width * 0.5f, height * 0.5f).x - cmToPx(ancho) / 2f)
        val top = maxOf(24f, screenToWorld(width * 0.5f, height * 0.5f).y - cmToPx(alto) / 2f)
        elementos.add(crearRecurrenteF1(left, top, ancho, alto, corteAncho, corteAlto))
        registrarAccion()
        invalidate()
    }

    fun insertarRecurrenteF2() {
        val ancho = 160f
        val alto = 110f
        val corteAncho = 58f
        val corteAlto = 40f
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = maxOf(24f, center.x - cmToPx(ancho) / 2f)
        val top = maxOf(24f, center.y - cmToPx(alto) / 2f)
        elementos.add(crearRecurrenteF2(left, top, ancho, alto, corteAncho, corteAlto))
        registrarAccion()
        invalidate()
    }

    fun insertarRecurrenteF3() {
        val anchoSuperior = 170f
        val anchoInferior = 105f
        val alto = 105f
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = maxOf(24f, center.x - cmToPx(anchoSuperior) / 2f)
        val top = maxOf(24f, center.y - cmToPx(alto) / 2f)
        elementos.add(crearRecurrenteF3(left, top, anchoSuperior, anchoInferior, alto))
        registrarAccion()
        invalidate()
    }

    fun insertarRecurrenteF4(conIzquierdo: Boolean, conDerecho: Boolean) {
        val centralAncho = 75f
        val centralAlto = 150f
        val agregadoAncho = 34f
        val agregadoAlto = 34f
        val anchoTotal = centralAncho +
            (if (conIzquierdo) agregadoAncho else 0f) +
            (if (conDerecho) agregadoAncho else 0f)
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val centralLeft = maxOf(
            24f,
            center.x - cmToPx(anchoTotal) / 2f + if (conIzquierdo) cmToPx(agregadoAncho) else 0f
        )
        val top = maxOf(24f, center.y - cmToPx(centralAlto) / 2f)
        elementos.add(
            crearRecurrenteF4(
                centralLeft = centralLeft,
                top = top,
                centralAnchoCm = centralAncho,
                centralAltoCm = centralAlto,
                agregadoAnchoCm = agregadoAncho,
                agregadoAltoCm = agregadoAlto,
                conIzquierdo = conIzquierdo,
                conDerecho = conDerecho
            )
        )
        registrarAccion()
        invalidate()
    }

    fun insertarRecurrenteF5() {
        val rectAncho = 120f
        val rectAlto = 120f
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = maxOf(24f, center.x - cmToPx(rectAncho) / 2f)
        val top = maxOf(24f, center.y - cmToPx(rectAlto) / 2f)
        elementos.add(crearRecurrenteF5(left, top, rectAncho, rectAlto))
        registrarAccion()
        invalidate()
    }

    fun insertarRecurrenteF6(esquinas: Set<Int> = setOf(0, 1, 2, 3), radioCm: Float = 18f) {
        val rectAncho = 120f
        val rectAlto = 90f
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = maxOf(24f, center.x - cmToPx(rectAncho) / 2f)
        val top = maxOf(24f, center.y - cmToPx(rectAlto) / 2f)
        val rect = RectF(left, top, left + cmToPx(rectAncho), top + cmToPx(rectAlto))
        val element = if (esquinas.size >= 4) {
            crearRecurrenteF6(left, top, rectAncho, rectAlto, radioCm)
        } else {
            crearRectanguloConEsquinasRedondeadas(rect, esquinas, radioCm)
        }
        elementos.add(element)
        registrarAccion()
        invalidate()
    }

    /**
     * El contorno del vano, en centímetros y con la Y hacia abajo: el dintel es la Y más pequeña.
     *
     * Se devuelve el contorno de la figura MÁS GRANDE del apunte, que es el hueco de la ventana;
     * los puentes, símbolos y textos que haya alrededor no son vanos. Las coordenadas se dan
     * relativas a su propia esquina superior izquierda, para que no arrastren dónde estaba
     * dibujada.
     *
     * De aquí salen los tramos de la ventana: un vano con el alféizar subido en un trozo es, en el
     * diseño, dos tramos de distinto alto colgando del mismo dintel.
     */
    fun contornoPrincipalEnCm(): List<Pair<Float, Float>>? {
        val composites = elementos.filterIsInstance<Element.Composite>()
        val elegido = composites.maxByOrNull { c ->
            val b = boundsForElement(c)
            b.width() * b.height()
        } ?: return null
        val contorno = elegido.contours.firstOrNull()?.takeIf { it.size >= 4 } ?: return null
        val caja = boundsForElement(elegido)
        return contorno.map { p -> pxToCm(p.x - caja.left) to pxToCm(p.y - caja.top) }
    }

    /**
     * Medida mayor del apunte: el lado horizontal y el vertical más grandes.
     *
     * Se leen primero los centímetros que cada figura guarda, porque son independientes del equipo.
     * Reconstruirlos desde los píxeles falla cuando el apunte se dibujó en un celular con otra
     * densidad de pantalla: las coordenadas se guardan en píxeles de quien dibujó, así que un
     * rectángulo de 101 cm trazado a densidad 2.75 se leía como 158.7 cm en un equipo de 1.75
     * (todas las medidas escaladas por el mismo factor). El cálculo por píxeles queda de reserva
     * para apuntes sin figuras acotadas, como los que solo tienen trazo libre o una imagen de fondo.
     */
    fun medidaPrincipal(): MedidaPrincipal? {
        medidaPrincipalEnCm()?.let { return it }

        val segmentos = segmentosExistentes()
        val horizontal = segmentos
            .filter { (a, b) -> abs(a.y - b.y) <= maxOf(4f, abs(a.x - b.x) * 0.08f) }
            .maxOfOrNull { (a, b) -> pxToCm(abs(b.x - a.x)) }
            ?: 0f
        val vertical = segmentos
            .filter { (a, b) -> abs(a.x - b.x) <= maxOf(4f, abs(a.y - b.y) * 0.08f) }
            .maxOfOrNull { (a, b) -> pxToCm(abs(b.y - a.y)) }
            ?: 0f

        if (horizontal > 0f && vertical > 0f) return MedidaPrincipal(horizontal, vertical)

        val bounds = boundsForContent() ?: return null
        if (bounds.width() <= 1f || bounds.height() <= 1f) return null
        return MedidaPrincipal(pxToCm(bounds.width()), pxToCm(bounds.height()))
    }

    /**
     * Ajusta un boceto recién cargado a la densidad de ESTE equipo.
     *
     * Las coordenadas se guardan en píxeles del celular que dibujó, mientras que la medida real vive
     * en los centímetros acotados de cada figura. Si el apunte viene de un equipo con otra densidad,
     * el trazo queda a otra escala: se ve distinto y, sobre todo, cualquier edición recalcularía las
     * cotas con la densidad de aquí y CAMBIARÍA medidas reales (un rectángulo de 101 cm dibujado a
     * 2.75 pasaba a 158.7 cm en un equipo de 1.75, y al reeditarlo se encogía).
     *
     * Se deduce la densidad con que se dibujó comparando píxeles contra centímetros acotados, se
     * reescala el trazo y se reponen los centímetros originales tal cual: el dibujo se adapta, la
     * medida no se toca. El tamaño exacto del gráfico no importa; la medida en cm sí.
     */
    private fun normalizarDensidad(cargados: List<Element>) {
        val pxPorCmDibujo = pxPorCmDelDibujo(cargados) ?: return
        if (pxPorCmDibujo <= 0.01f) return
        val factor = resources.displayMetrics.density / pxPorCmDibujo
        if (!factor.isFinite() || factor <= 0f || abs(factor - 1f) < 0.01f) return
        val cotas = capturarCotas(cargados)
        val origen = PointF(0f, 0f)
        cargados.forEach { scaleElement(it, factor, origen) }
        restaurarCotas(cargados, cotas)
    }

    /** Píxeles por centímetro con que se dibujó el apunte (mediana de las figuras acotadas). */
    private fun pxPorCmDelDibujo(cargados: List<Element>): Float? {
        val muestras = mutableListOf<Float>()
        fun considerar(element: Element) {
            when (element) {
                is Element.Group -> element.children.forEach { considerar(it) }
                is Element.Shape -> when (element.tool) {
                    Tool.RECTANGLE, Tool.TRIANGLE, Tool.CIRCLE -> {
                        val ancho = abs(element.rect.width())
                        val alto = abs(element.rect.height())
                        if (element.widthCm > 0.5f && ancho > 1f) muestras += ancho / element.widthCm
                        if (element.heightCm > 0.5f && alto > 1f) muestras += alto / element.heightCm
                    }
                    Tool.LINE, Tool.ORTHO_LINE -> {
                        val largo = distancia(element.start, element.end)
                        if (element.lengthCm > 0.5f && largo > 1f) muestras += largo / element.lengthCm
                    }
                    Tool.NONE, Tool.FREEHAND, Tool.TEXT, Tool.SELECT -> Unit
                }
                else -> Unit
            }
        }
        cargados.forEach { considerar(it) }
        if (muestras.isEmpty()) return null
        return muestras.sorted()[muestras.size / 2]
    }

    private fun capturarCotas(cargados: List<Element>): List<FloatArray> {
        val cotas = mutableListOf<FloatArray>()
        fun recorrer(element: Element) {
            when (element) {
                is Element.Group -> element.children.forEach { recorrer(it) }
                is Element.Shape -> cotas += floatArrayOf(
                    element.widthCm, element.heightCm, element.diameterCm, element.lengthCm,
                    element.topCm, element.rightCm, element.bottomCm, element.leftCm
                )
                is Element.Composite -> cotas += floatArrayOf(element.widthCm, element.heightCm)
                else -> Unit
            }
        }
        cargados.forEach { recorrer(it) }
        return cotas
    }

    private fun restaurarCotas(cargados: List<Element>, cotas: List<FloatArray>) {
        var indice = 0
        fun recorrer(element: Element) {
            when (element) {
                is Element.Group -> element.children.forEach { recorrer(it) }
                is Element.Shape -> {
                    val c = cotas.getOrNull(indice++) ?: return
                    element.widthCm = c[0]; element.heightCm = c[1]
                    element.diameterCm = c[2]; element.lengthCm = c[3]
                    element.topCm = c[4]; element.rightCm = c[5]
                    element.bottomCm = c[6]; element.leftCm = c[7]
                }
                is Element.Composite -> {
                    val c = cotas.getOrNull(indice++) ?: return
                    element.widthCm = c[0]; element.heightCm = c[1]
                }
                else -> Unit
            }
        }
        cargados.forEach { recorrer(it) }
    }

    /** Mayor ancho y mayor alto según los centímetros acotados en las figuras, o null si no hay. */
    private fun medidaPrincipalEnCm(): MedidaPrincipal? {
        var horizontal = 0f
        var vertical = 0f

        fun considerar(element: Element) {
            when (element) {
                is Element.Group -> element.children.forEach { considerar(it) }
                is Element.Shape -> when (element.tool) {
                    Tool.RECTANGLE -> {
                        horizontal = maxOf(horizontal, element.widthCm, element.topCm, element.bottomCm)
                        vertical = maxOf(vertical, element.heightCm, element.leftCm, element.rightCm)
                    }
                    Tool.TRIANGLE, Tool.CIRCLE -> {
                        horizontal = maxOf(horizontal, element.widthCm)
                        vertical = maxOf(vertical, element.heightCm)
                    }
                    Tool.LINE, Tool.ORTHO_LINE -> {
                        // Una línea suma a lo ancho o a lo alto según hacia dónde corre.
                        if (abs(element.end.x - element.start.x) >= abs(element.end.y - element.start.y)) {
                            horizontal = maxOf(horizontal, element.lengthCm)
                        } else {
                            vertical = maxOf(vertical, element.lengthCm)
                        }
                    }
                    Tool.NONE, Tool.FREEHAND, Tool.TEXT, Tool.SELECT -> Unit
                }
                else -> Unit
            }
        }

        elementos.forEach { considerar(it) }
        return if (horizontal > 0f && vertical > 0f) MedidaPrincipal(horizontal, vertical) else null
    }

    fun loadBackground(file: File?) {
        fondo = file?.let { BitmapFactory.decodeFile(it.absolutePath) }
        elementos.clear()
        registrarAccion()
        cotaHits.clear()
        trazoActual.reset()
        dibujando = false
        resetViewport()
        invalidate()
    }

    fun exportEditableState(): String {
        val root = JSONObject()
        val items = JSONArray()
        elementos.forEach { element -> items.put(elementJson(element)) }
        root.put("version", 1)
        root.put("elements", items)
        return root.toString()
    }

    fun loadEditableState(json: String): Boolean {
        return runCatching {
            val root = JSONObject(json)
            val items = root.optJSONArray("elements") ?: JSONArray()
            val cargados = mutableListOf<Element>()
            for (i in 0 until items.length()) {
                val obj = items.getJSONObject(i)
                readElementJson(obj)?.let { cargados.add(it) }
            }
            normalizarDensidad(cargados)
            elementos.clear()
            elementos.addAll(cargados)
            establecerBaseline()   // el sketch cargado es la BASE del historial (undo no lo borra)
            cotaHits.clear()
            selectedIndices.clear()
            trazoActual.reset()
            fondo = null
            dibujando = false
            resetViewport()
            invalidate()
            true
        }.getOrDefault(false)
    }

    private fun pointJson(point: PointF): JSONArray = JSONArray()
        .put(point.x.toDouble())
        .put(point.y.toDouble())

    private fun readPoint(array: JSONArray): PointF {
        return PointF(
            array.optDouble(0, 0.0).toFloat(),
            array.optDouble(1, 0.0).toFloat()
        )
    }

    private fun rectJson(rect: RectF): JSONArray = JSONArray()
        .put(rect.left.toDouble())
        .put(rect.top.toDouble())
        .put(rect.right.toDouble())
        .put(rect.bottom.toDouble())

    private fun readRect(array: JSONArray): RectF {
        return RectF(
            array.optDouble(0, 0.0).toFloat(),
            array.optDouble(1, 0.0).toFloat(),
            array.optDouble(2, 0.0).toFloat(),
            array.optDouble(3, 0.0).toFloat()
        )
    }

    private fun pointsJson(points: List<PointF>): JSONArray {
        val array = JSONArray()
        points.forEach { array.put(pointJson(it)) }
        return array
    }

    private fun readPoints(array: JSONArray): MutableList<PointF> {
        val points = mutableListOf<PointF>()
        for (i in 0 until array.length()) {
            points.add(readPoint(array.optJSONArray(i) ?: JSONArray()))
        }
        return points
    }

    private fun contoursJson(contours: List<List<PointF>>): JSONArray {
        val array = JSONArray()
        contours.forEach { array.put(pointsJson(it)) }
        return array
    }

    private fun readContours(array: JSONArray): MutableList<MutableList<PointF>> {
        val contours = mutableListOf<MutableList<PointF>>()
        for (i in 0 until array.length()) {
            val contour = readPoints(array.optJSONArray(i) ?: JSONArray())
            if (contour.size >= 2) contours.add(contour)
        }
        return contours
    }

    private fun floatMatrixJson(values: List<List<Float>>): JSONArray {
        val array = JSONArray()
        values.forEach { row ->
            val rowArray = JSONArray()
            row.forEach { rowArray.put(it.toDouble()) }
            array.put(rowArray)
        }
        return array
    }

    private fun readFloatMatrix(array: JSONArray): MutableList<MutableList<Float>> {
        val result = mutableListOf<MutableList<Float>>()
        for (i in 0 until array.length()) {
            val row = array.optJSONArray(i) ?: JSONArray()
            val values = mutableListOf<Float>()
            for (j in 0 until row.length()) values.add(row.optDouble(j, 0.0).toFloat())
            result.add(values)
        }
        return result
    }

    private fun elementJson(element: Element): JSONObject {
        return when (element) {
            is Element.Freehand -> JSONObject()
                .put("type", "freehand")
                .put("points", pointsJson(pointsFromPath(element.path)))
            is Element.Composite -> JSONObject()
                .put("type", "composite")
                .put("template", element.template)
                .put("widthCm", element.widthCm)
                .put("heightCm", element.heightCm)
                .put("contours", contoursJson(element.contours))
                .put("sideCms", floatMatrixJson(element.sideCms))
                .put("rotationDeg", element.rotationDeg)
                .put("reflejado", element.reflejado)
            is Element.Shape -> JSONObject()
                .put("type", "shape")
                .put("tool", element.tool.name)
                .put("rect", rectJson(element.rect))
                .put("start", pointJson(element.start))
                .put("end", pointJson(element.end))
                .put("widthCm", element.widthCm)
                .put("heightCm", element.heightCm)
                .put("diameterCm", element.diameterCm)
                .put("lengthCm", element.lengthCm)
                .put("topLeft", pointJson(element.topLeft))
                .put("topRight", pointJson(element.topRight))
                .put("bottomRight", pointJson(element.bottomRight))
                .put("bottomLeft", pointJson(element.bottomLeft))
                .put("topCm", element.topCm)
                .put("rightCm", element.rightCm)
                .put("bottomCm", element.bottomCm)
                .put("leftCm", element.leftCm)
                .put("cotaHint", element.cotaHint)
            is Element.Group -> JSONObject()
                .put("type", "group")
                .put("children", JSONArray().apply {
                    element.children.forEach { put(elementJson(it)) }
                })
            is Element.TextLabel -> JSONObject()
                .put("type", "text")
                .put("text", element.text)
                .put("x", element.x)
                .put("y", element.y)
                .put("textSize", element.textSize)
            is Element.InfoBox -> JSONObject()
                .put("type", "infoBox")
                .put("text", element.text)
                .put("rect", rectJson(element.rect))
                .put("textSize", element.textSize)
            is Element.Symbol -> JSONObject()
                .put("type", "symbol")
                .put("drawableName", element.drawableName)
                .put("rect", rectJson(element.rect))
        }
    }

    private fun readElementJson(obj: JSONObject): Element? {
        return when (obj.optString("type")) {
            "freehand" -> {
                val points = readPoints(obj.optJSONArray("points") ?: JSONArray())
                if (points.size >= 2) Element.Freehand(pathFromPoints(points)) else null
            }
            "composite" -> {
                val contours = readContours(obj.optJSONArray("contours") ?: JSONArray())
                if (contours.isEmpty()) return null
                val sideCms = readFloatMatrix(obj.optJSONArray("sideCms") ?: JSONArray())
                    .ifEmpty { sideCmsForContours(contours) }
                val template = obj.optString("template").takeIf { it.isNotBlank() && it != "null" }
                val path = when (template) {
                    TEMPLATE_F5 -> pathRecurrenteF5(dimsRecurrenteF5(contours.first(), sideCms) ?: return null)
                    TEMPLATE_F6 -> pathRecurrenteF6(dimsRecurrenteF6(contours.first(), sideCms) ?: return null)
                    TEMPLATE_ROUNDED -> pathRoundedCorners(dimsRoundedCorners(contours.first(), sideCms) ?: return null)
                    else -> pathFromContours(contours)
                }
                val bounds = RectF()
                path.computeBounds(bounds, true)
                Element.Composite(
                    path = path,
                    widthCm = obj.optDouble("widthCm", pxToCm(bounds.width()).toDouble()).toFloat(),
                    heightCm = obj.optDouble("heightCm", pxToCm(bounds.height()).toDouble()).toFloat(),
                    contours = contours,
                    sideCms = sideCms,
                    template = template,
                    rotationDeg = obj.optDouble("rotationDeg", 0.0).toFloat(),
                    reflejado = obj.optBoolean("reflejado", false)
                )
            }
            "shape" -> {
                val rect = readRect(obj.optJSONArray("rect") ?: JSONArray())
                val tool = runCatching {
                    Tool.valueOf(obj.optString("tool", Tool.RECTANGLE.name))
                }.getOrDefault(Tool.RECTANGLE)
                Element.Shape(
                    tool = tool,
                    rect = rect,
                    start = readPoint(obj.optJSONArray("start") ?: JSONArray()),
                    end = readPoint(obj.optJSONArray("end") ?: JSONArray()),
                    widthCm = obj.optDouble("widthCm", pxToCm(rect.width()).toDouble()).toFloat(),
                    heightCm = obj.optDouble("heightCm", pxToCm(rect.height()).toDouble()).toFloat(),
                    diameterCm = obj.optDouble("diameterCm", pxToCm(maxOf(rect.width(), rect.height())).toDouble()).toFloat(),
                    lengthCm = obj.optDouble("lengthCm", pxToCm(hypot(rect.width().toDouble(), rect.height().toDouble()).toFloat()).toDouble()).toFloat(),
                    topLeft = readPoint(obj.optJSONArray("topLeft") ?: JSONArray()),
                    topRight = readPoint(obj.optJSONArray("topRight") ?: JSONArray()),
                    bottomRight = readPoint(obj.optJSONArray("bottomRight") ?: JSONArray()),
                    bottomLeft = readPoint(obj.optJSONArray("bottomLeft") ?: JSONArray()),
                    topCm = obj.optDouble("topCm", pxToCm(rect.width()).toDouble()).toFloat(),
                    rightCm = obj.optDouble("rightCm", pxToCm(rect.height()).toDouble()).toFloat(),
                    bottomCm = obj.optDouble("bottomCm", pxToCm(rect.width()).toDouble()).toFloat(),
                    leftCm = obj.optDouble("leftCm", pxToCm(rect.height()).toDouble()).toFloat(),
                    cotaHint = obj.optString("cotaHint").takeIf { it.isNotBlank() && it != "null" }
                )
            }
            "group" -> {
                val childrenJson = obj.optJSONArray("children") ?: JSONArray()
                val children = mutableListOf<Element>()
                for (i in 0 until childrenJson.length()) {
                    readElementJson(childrenJson.getJSONObject(i))?.let { children.add(it) }
                }
                if (children.isNotEmpty()) Element.Group(children) else null
            }
            "text" -> {
                val text = obj.optString("text").trim()
                if (text.isBlank()) {
                    null
                } else {
                    Element.TextLabel(
                        text = text,
                        x = obj.optDouble("x", 0.0).toFloat(),
                        y = obj.optDouble("y", 0.0).toFloat(),
                        textSize = obj.optDouble("textSize", 34.0).toFloat()
                    )
                }
            }
            "infoBox" -> {
                val text = obj.optString("text").trim()
                if (text.isBlank()) {
                    null
                } else {
                    Element.InfoBox(
                        text = text,
                        rect = readRect(obj.optJSONArray("rect") ?: JSONArray()),
                        textSize = obj.optDouble("textSize", spToPx(12f).toDouble()).toFloat()
                    )
                }
            }
            "symbol" -> {
                val drawableName = obj.optString("drawableName").trim()
                if (drawableName.isBlank() || drawableIdForName(drawableName) == 0) {
                    null
                } else {
                    Element.Symbol(
                        drawableName = drawableName,
                        rect = readRect(obj.optJSONArray("rect") ?: JSONArray())
                    )
                }
            }
            else -> null
        }
    }

    private fun pointsFromPath(path: Path): List<PointF> {
        val result = mutableListOf<PointF>()
        val measure = PathMeasure(path, false)
        val pos = FloatArray(2)
        do {
            val length = measure.length
            if (length <= 0f) continue
            var distance = 0f
            while (distance < length) {
                measure.getPosTan(distance, pos, null)
                addPointIfDistinct(result, PointF(pos[0], pos[1]))
                distance += 3f
            }
            measure.getPosTan(length, pos, null)
            addPointIfDistinct(result, PointF(pos[0], pos[1]))
        } while (measure.nextContour())
        return result
    }

    private fun pathFromPoints(points: List<PointF>): Path {
        return Path().apply {
            if (points.isEmpty()) return@apply
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
    }

    private fun pathFromContours(contours: List<List<PointF>>): Path {
        return Path().apply {
            contours.forEach { contour ->
                if (contour.isEmpty()) return@forEach
                moveTo(contour.first().x, contour.first().y)
                contour.drop(1).forEach { lineTo(it.x, it.y) }
                close()
            }
        }
    }

    fun exportBitmap(): Bitmap {
        val contentBounds = boundsForContent()
        if (contentBounds == null) {
            fondo?.let { return it.copy(Bitmap.Config.ARGB_8888, false) }
        }
        val bounds = contentBounds ?: RectF(0f, 0f, width.toFloat().coerceAtLeast(1f), height.toFloat().coerceAtLeast(1f))
        val margin = margenExportacion(bounds)
        val exportBounds = RectF(bounds).apply { inset(-margin, -margin) }
        val rawW = exportBounds.width().coerceAtLeast(1f)
        val rawH = exportBounds.height().coerceAtLeast(1f)
        val maxSide = 2400f
        val exportScale = minOf(1f, maxSide / maxOf(rawW, rawH))
        val outW = (rawW * exportScale).toInt().coerceAtLeast(1)
        val outH = (rawH * exportScale).toInt().coerceAtLeast(1)
        val out = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(0xFFF7F4EC.toInt(), PorterDuff.Mode.SRC)
        canvas.save()
        canvas.scale(exportScale, exportScale)
        canvas.translate(-exportBounds.left, -exportBounds.top)
        cotaTextRects.clear()
        elementos.forEachIndexed { index, element -> drawElement(canvas, index, element, false) }
        cotaTextRects.clear()
        canvas.restore()
        return out
    }

    private fun margenExportacion(bounds: RectF): Float {
        val base = 96f
        val proporcional = maxOf(bounds.width(), bounds.height()) * 0.025f
        return maxOf(base, proporcional).coerceAtMost(220f)
    }

    private fun crearElemento(x: Float, y: Float): Element? {
        if (!trazoValido(x, y)) return null
        if (herramienta == Tool.FREEHAND) return Element.Freehand(Path(trazoActual))

        val endPoint = puntoFinalHerramienta(x, y)
        val rect = rectFrom(startPoint, endPoint.x, endPoint.y)
        val lineLengthPx = hypot((endPoint.x - startPoint.x).toDouble(), (endPoint.y - startPoint.y).toDouble()).toFloat()
        val widthCm = pxToCm(rect.width())
        val heightCm = pxToCm(rect.height())
        if (herramienta == Tool.RECTANGLE && rectangleRoundedCorner != null && rectangleRoundedRadiusCm > 0f) {
            return crearRectanguloConEsquinaRedondeada(rect, rectangleRoundedCorner ?: 0, rectangleRoundedRadiusCm)
        }
        return Element.Shape(
            tool = herramienta,
            rect = rect,
            start = PointF(startPoint.x, startPoint.y),
            end = PointF(endPoint.x, endPoint.y),
            widthCm = widthCm,
            heightCm = heightCm,
            diameterCm = pxToCm(maxOf(rect.width(), rect.height())),
            lengthCm = pxToCm(lineLengthPx),
            topLeft = PointF(rect.left, rect.top),
            topRight = PointF(rect.right, rect.top),
            bottomRight = PointF(rect.right, rect.bottom),
            bottomLeft = PointF(rect.left, rect.bottom),
            topCm = widthCm,
            rightCm = heightCm,
            bottomCm = widthCm,
            leftCm = heightCm
        )
    }

    private fun drawElement(canvas: Canvas, index: Int, element: Element, collectHits: Boolean) {
        when (element) {
            is Element.Freehand -> canvas.drawPath(element.path, paint)
            is Element.TextLabel -> {
                sketchTextPaint.textSize = element.textSize
                canvas.drawText(element.text, element.x, element.y, sketchTextPaint)
            }
            is Element.InfoBox -> drawInfoBox(canvas, element)
            is Element.Symbol -> drawSymbol(canvas, element)
            is Element.Composite -> {
                canvas.drawPath(element.path, paint)
                drawCompositeCotas(canvas, index, element, collectHits)
            }
            is Element.Shape -> {
                val shapePath = pathForShape(element)
                if (element.cotaHint != "GRADA_TOTAL_COTA") {
                    canvas.drawPath(shapePath, paint)
                }
                drawCotas(canvas, index, element, collectHits)
            }
            is Element.Group -> {
                element.children.forEach { child -> drawElement(canvas, index, child, false) }
            }
        }
    }

    private fun drawInfoBox(canvas: Canvas, element: Element.InfoBox) {
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 30, 30)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(230, 247, 244, 236)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(element.rect, 8f, 8f, fillPaint)
        canvas.drawRoundRect(element.rect, 8f, 8f, borderPaint)
        sketchTextPaint.textSize = element.textSize
        sketchTextPaint.textAlign = Paint.Align.LEFT
        val lineHeight = element.textSize * 1.25f
        var y = element.rect.top + lineHeight
        element.text.lines().forEach { line ->
            canvas.drawText(line, element.rect.left + 12f, y, sketchTextPaint)
            y += lineHeight
        }
        sketchTextPaint.textAlign = Paint.Align.LEFT
    }

    private fun drawSymbol(canvas: Canvas, element: Element.Symbol) {
        val drawable = context.getDrawable(drawableIdForName(element.drawableName)) ?: return
        drawable.setBounds(
            element.rect.left.toInt(),
            element.rect.top.toInt(),
            element.rect.right.toInt(),
            element.rect.bottom.toInt()
        )
        drawable.draw(canvas)
    }

    private fun drawableIdForName(drawableName: String): Int {
        return resources.getIdentifier(drawableName, "drawable", context.packageName)
    }

    private fun drawSelection(canvas: Canvas) {
        for (index in selectedIndices) {
            val element = elementos.getOrNull(index) ?: continue
            val bounds = boundsForElement(element)
            bounds.inset(-10f, -10f)
            canvas.drawRect(bounds, selectionPaint)
        }
    }

    private fun manejarSeleccionDown(x: Float, y: Float) {
        parent?.requestDisallowInterceptTouchEvent(true)
        val hit = hitElement(x, y)
        if (hit == null) {
            selectedIndices.clear()
            dragIndex = null
            invalidate()
            return
        }
        if (hit !in selectedIndices) {
            selectedIndices.add(hit)
        }
        dragIndex = hit
        lastDragPoint = PointF(x, y)
        movedSelection = false
        invalidate()
    }

    private fun moverSeleccion(x: Float, y: Float) {
        val index = dragIndex ?: return
        if (index !in selectedIndices) return
        val dx = x - lastDragPoint.x
        val dy = y - lastDragPoint.y
        if (abs(dx) > 0.5f || abs(dy) > 0.5f) {
            selectedIndices.forEach { translateElement(it, dx, dy) }
            movedSelection = true
            lastDragPoint = PointF(x, y)
            invalidate()
        }
    }

    private fun finalizarMoverSeleccion() {
        if (movedSelection) {
            aplicarImanASeleccion()
            registrarAccion()   // el arrastre completo es UNA acción reversible
        }
        dragIndex = null
        movedSelection = false
        parent?.requestDisallowInterceptTouchEvent(false)
        invalidate()
    }

    private fun hitElement(x: Float, y: Float): Int? {
        val touchPad = 24f * resources.displayMetrics.density
        for (i in elementos.indices.reversed()) {
            val bounds = boundsForElement(elementos[i])
            bounds.inset(-touchPad, -touchPad)
            if (bounds.contains(x, y)) return i
        }
        return null
    }

    private fun reemplazarSeleccionCon(element: Element) {
        val sorted = selectedIndices.sortedDescending()
        sorted.forEach { index ->
            if (index in elementos.indices) elementos.removeAt(index)
        }
        elementos.add(element)
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        invalidate()
    }

    private fun compositeFromPath(path: Path): Element.Composite {
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val contours = contoursFromPath(path).ifEmpty { mutableListOf(rectContour(bounds)) }
        return Element.Composite(
            path = path,
            widthCm = pxToCm(bounds.width()),
            heightCm = pxToCm(bounds.height()),
            contours = contours,
            sideCms = sideCmsForContours(contours)
        )
    }

    private fun compositeFromDifference(path: Path, baseElement: Element, cutterElement: Element): Element.Composite {
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val contours = contoursFromRectDifference(boundsForElement(baseElement), boundsForElement(cutterElement))
            ?: contoursFromPath(path).ifEmpty { mutableListOf(rectContour(bounds)) }
        return Element.Composite(
            path = path,
            widthCm = pxToCm(bounds.width()),
            heightCm = pxToCm(bounds.height()),
            contours = contours,
            sideCms = sideCmsForContours(contours)
        )
    }

    private fun contoursFromPath(path: Path): MutableList<MutableList<PointF>> {
        val result = mutableListOf<MutableList<PointF>>()
        val measure = PathMeasure(path, true)
        val pos = FloatArray(2)
        do {
            val length = measure.length
            if (length <= 2f) continue

            val points = mutableListOf<PointF>()
            var distance = 0f
            while (distance < length) {
                measure.getPosTan(distance, pos, null)
                addPointIfDistinct(points, PointF(pos[0], pos[1]))
                distance += 1f
            }
            measure.getPosTan(length, pos, null)
            addPointIfDistinct(points, PointF(pos[0], pos[1]))

            val simplified = simplifyContour(points)
            if (simplified.size >= 3) result.add(simplified)
        } while (measure.nextContour())
        return result
    }

    private fun contoursFromRectDifference(base: RectF, cutter: RectF): MutableList<MutableList<PointF>>? {
        if (base.width() <= 1f || base.height() <= 1f || cutter.width() <= 1f || cutter.height() <= 1f) return null
        val cut = RectF(
            maxOf(base.left, cutter.left),
            maxOf(base.top, cutter.top),
            minOf(base.right, cutter.right),
            minOf(base.bottom, cutter.bottom)
        )
        if (cut.width() <= 1f || cut.height() <= 1f) return mutableListOf(rectContour(base))
        if (same(cut.left, base.left) && same(cut.top, base.top) && same(cut.right, base.right) && same(cut.bottom, base.bottom)) {
            return null
        }

        val touchesLeft = same(cut.left, base.left)
        val touchesTop = same(cut.top, base.top)
        val touchesRight = same(cut.right, base.right)
        val touchesBottom = same(cut.bottom, base.bottom)

        val contour = when {
            touchesLeft && touchesTop -> mutableListOf(
                PointF(cut.right, base.top),
                PointF(base.right, base.top),
                PointF(base.right, base.bottom),
                PointF(base.left, base.bottom),
                PointF(base.left, cut.bottom),
                PointF(cut.right, cut.bottom)
            )
            touchesRight && touchesTop -> mutableListOf(
                PointF(base.left, base.top),
                PointF(cut.left, base.top),
                PointF(cut.left, cut.bottom),
                PointF(base.right, cut.bottom),
                PointF(base.right, base.bottom),
                PointF(base.left, base.bottom)
            )
            touchesRight && touchesBottom -> mutableListOf(
                PointF(base.left, base.top),
                PointF(base.right, base.top),
                PointF(base.right, cut.top),
                PointF(cut.left, cut.top),
                PointF(cut.left, base.bottom),
                PointF(base.left, base.bottom)
            )
            touchesLeft && touchesBottom -> mutableListOf(
                PointF(base.left, base.top),
                PointF(base.right, base.top),
                PointF(base.right, base.bottom),
                PointF(cut.right, base.bottom),
                PointF(cut.right, cut.top),
                PointF(base.left, cut.top)
            )
            touchesTop -> mutableListOf(
                PointF(base.left, base.top),
                PointF(cut.left, base.top),
                PointF(cut.left, cut.bottom),
                PointF(cut.right, cut.bottom),
                PointF(cut.right, base.top),
                PointF(base.right, base.top),
                PointF(base.right, base.bottom),
                PointF(base.left, base.bottom)
            )
            touchesRight -> mutableListOf(
                PointF(base.left, base.top),
                PointF(base.right, base.top),
                PointF(base.right, cut.top),
                PointF(cut.left, cut.top),
                PointF(cut.left, cut.bottom),
                PointF(base.right, cut.bottom),
                PointF(base.right, base.bottom),
                PointF(base.left, base.bottom)
            )
            touchesBottom -> mutableListOf(
                PointF(base.left, base.top),
                PointF(base.right, base.top),
                PointF(base.right, base.bottom),
                PointF(cut.right, base.bottom),
                PointF(cut.right, cut.top),
                PointF(cut.left, cut.top),
                PointF(cut.left, base.bottom),
                PointF(base.left, base.bottom)
            )
            touchesLeft -> mutableListOf(
                PointF(base.left, base.top),
                PointF(base.right, base.top),
                PointF(base.right, base.bottom),
                PointF(base.left, base.bottom),
                PointF(base.left, cut.bottom),
                PointF(cut.right, cut.bottom),
                PointF(cut.right, cut.top),
                PointF(base.left, cut.top)
            )
            else -> return mutableListOf(rectContour(base), rectContour(cut))
        }

        return mutableListOf(simplifyContour(contour))
    }

    private fun rectContour(rect: RectF): MutableList<PointF> {
        return mutableListOf(
            PointF(rect.left, rect.top),
            PointF(rect.right, rect.top),
            PointF(rect.right, rect.bottom),
            PointF(rect.left, rect.bottom)
        )
    }

    private fun same(a: Float, b: Float): Boolean = abs(a - b) <= 2.5f

    private fun sideCmsForContours(contours: List<List<PointF>>): MutableList<MutableList<Float>> {
        return contours.map { contour ->
            contour.indices.map { i ->
                pxToCm(distancia(contour[i], contour[(i + 1) % contour.size]))
            }.toMutableList()
        }.toMutableList()
    }

    private fun addPointIfDistinct(points: MutableList<PointF>, point: PointF) {
        if (points.lastOrNull()?.let { distancia(it, point) < 1.5f } == true) return
        points.add(point)
    }

    private fun simplifyContour(points: List<PointF>): MutableList<PointF> {
        val contour = points.map { PointF(it.x, it.y) }.toMutableList()
        if (contour.size < 3) return contour
        if (distancia(contour.first(), contour.last()) < 2f) contour.removeAt(contour.lastIndex)

        var changed: Boolean
        do {
            changed = false
            if (contour.size < 3) break
            var i = 0
            while (i < contour.size) {
                val previous = contour[(i - 1 + contour.size) % contour.size]
                val current = contour[i]
                val next = contour[(i + 1) % contour.size]
                if (distancia(previous, current) < 3f || distancia(current, next) < 3f || areCollinear(previous, current, next)) {
                    contour.removeAt(i)
                    changed = true
                } else {
                    i++
                }
            }
        } while (changed)

        snapAlmostOrthogonal(contour)
        return contour
    }

    private fun areCollinear(a: PointF, b: PointF, c: PointF): Boolean {
        val abx = b.x - a.x
        val aby = b.y - a.y
        val bcx = c.x - b.x
        val bcy = c.y - b.y
        val cross = abs((abx * bcy) - (aby * bcx))
        val base = distancia(a, c).coerceAtLeast(1f)
        val dot = (abx * bcx) + (aby * bcy)
        return cross / base < 1.8f && dot >= 0f
    }

    private fun snapAlmostOrthogonal(points: MutableList<PointF>) {
        if (points.size < 2) return
        points.indices.forEach { i ->
            val a = points[i]
            val b = points[(i + 1) % points.size]
            val dx = abs(b.x - a.x)
            val dy = abs(b.y - a.y)
            if (dx < 2.5f && dy > dx) {
                b.x = a.x
            } else if (dy < 2.5f && dx > dy) {
                b.y = a.y
            }
        }
    }

    private fun pathForElement(element: Element): Path {
        return when (element) {
            is Element.Freehand -> Path(element.path)
            is Element.TextLabel -> Path().apply {
                addRect(boundsForText(element), Path.Direction.CW)
            }
            is Element.InfoBox -> Path().apply {
                addRect(element.rect, Path.Direction.CW)
            }
            is Element.Symbol -> Path().apply {
                addRect(element.rect, Path.Direction.CW)
            }
            is Element.Composite -> Path(element.path)
            is Element.Shape -> pathForShape(element)
            is Element.Group -> Path().apply {
                element.children.forEach { child -> addPath(pathForElement(child)) }
            }
        }
    }

    private fun boundsForText(element: Element.TextLabel): RectF {
        sketchTextPaint.textSize = element.textSize
        val width = sketchTextPaint.measureText(element.text).coerceAtLeast(1f)
        val top = element.y - element.textSize
        val bottom = element.y + element.textSize * 0.28f
        return RectF(element.x, top, element.x + width, bottom)
    }

    private fun boundsForElement(element: Element): RectF {
        val bounds = RectF()
        pathForElement(element).computeBounds(bounds, true)
        return bounds
    }

    private fun boundsForContent(): RectF? {
        var union: RectF? = null
        for (element in elementos) {
            val bounds = boundsForElement(element)
            if (union == null) {
                union = RectF(bounds)
            } else {
                union.union(bounds)
            }
        }
        return union
    }

    private fun translateElement(index: Int, dx: Float, dy: Float) {
        val element = elementos.getOrNull(index) ?: return
        translateElement(element, dx, dy)
    }

    private fun translateElement(element: Element, dx: Float, dy: Float) {
        when (element) {
            is Element.Freehand -> element.path.transform(Matrix().apply { setTranslate(dx, dy) })
            is Element.TextLabel -> {
                element.x += dx
                element.y += dy
            }
            is Element.InfoBox -> element.rect.offset(dx, dy)
            is Element.Symbol -> element.rect.offset(dx, dy)
            is Element.Composite -> {
                element.path.transform(Matrix().apply { setTranslate(dx, dy) })
                element.contours.flatten().forEach { it.offset(dx, dy) }
            }
            is Element.Shape -> {
                element.rect.offset(dx, dy)
                element.start.offset(dx, dy)
                element.end.offset(dx, dy)
                element.topLeft.offset(dx, dy)
                element.topRight.offset(dx, dy)
                element.bottomRight.offset(dx, dy)
                element.bottomLeft.offset(dx, dy)
            }
            is Element.Group -> element.children.forEach { child -> translateElement(child, dx, dy) }
        }
    }

    private fun scaleElement(element: Element, factor: Float, pivot: PointF) {
        val matrix = Matrix().apply { setScale(factor, factor, pivot.x, pivot.y) }
        when (element) {
            is Element.Freehand -> element.path.transform(matrix)
            is Element.TextLabel -> {
                scalePoint(element.x, element.y, factor, pivot).also {
                    element.x = it.x
                    element.y = it.y
                }
                element.textSize *= factor
            }
            is Element.InfoBox -> {
                matrix.mapRect(element.rect)
                element.textSize *= factor
            }
            is Element.Symbol -> matrix.mapRect(element.rect)
            is Element.Composite -> {
                element.path.transform(matrix)
                transformCompositePoints(element, matrix)
                rebuildCompositePath(element)
                refreshCompositeSides(element)
                val bounds = boundsForElement(element)
                element.widthCm = pxToCm(bounds.width())
                element.heightCm = pxToCm(bounds.height())
            }
            is Element.Shape -> {
                transformPoint(element.start, matrix)
                transformPoint(element.end, matrix)
                transformPoint(element.topLeft, matrix)
                transformPoint(element.topRight, matrix)
                transformPoint(element.bottomRight, matrix)
                transformPoint(element.bottomLeft, matrix)
                actualizarShapeDesdePuntos(element)
            }
            is Element.Group -> element.children.forEach { child -> scaleElement(child, factor, pivot) }
        }
    }

    private fun rotateElement(element: Element, degrees: Float, pivot: PointF) {
        val matrix = Matrix().apply { setRotate(degrees, pivot.x, pivot.y) }
        when (element) {
            is Element.Freehand -> element.path.transform(matrix)
            is Element.TextLabel -> {
                val point = PointF(element.x, element.y)
                transformPoint(point, matrix)
                element.x = point.x
                element.y = point.y
            }
            is Element.InfoBox -> matrix.mapRect(element.rect)
            is Element.Symbol -> matrix.mapRect(element.rect)
            is Element.Composite -> {
                element.path.transform(matrix)
                transformCompositePoints(element, matrix)
                rebuildCompositePath(element)
                refreshCompositeSides(element)
                val bounds = boundsForElement(element)
                element.widthCm = pxToCm(bounds.width())
                element.heightCm = pxToCm(bounds.height())
                element.rotationDeg = ((element.rotationDeg + degrees) % 360f + 360f) % 360f
            }
            is Element.Shape -> {
                transformPoint(element.start, matrix)
                transformPoint(element.end, matrix)
                transformPoint(element.topLeft, matrix)
                transformPoint(element.topRight, matrix)
                transformPoint(element.bottomRight, matrix)
                transformPoint(element.bottomLeft, matrix)
                actualizarShapeDesdePuntos(element)
            }
            is Element.Group -> element.children.forEach { child -> rotateElement(child, degrees, pivot) }
        }
    }

    private fun scalePoint(x: Float, y: Float, factor: Float, pivot: PointF): PointF {
        return PointF(
            pivot.x + (x - pivot.x) * factor,
            pivot.y + (y - pivot.y) * factor
        )
    }

    private fun transformPoint(point: PointF, matrix: Matrix) {
        val values = floatArrayOf(point.x, point.y)
        matrix.mapPoints(values)
        point.x = values[0]
        point.y = values[1]
    }

    private fun actualizarShapeDesdePuntos(shape: Element.Shape) {
        shape.rect.set(
            minOf(shape.start.x, shape.end.x, shape.topLeft.x, shape.topRight.x, shape.bottomRight.x, shape.bottomLeft.x),
            minOf(shape.start.y, shape.end.y, shape.topLeft.y, shape.topRight.y, shape.bottomRight.y, shape.bottomLeft.y),
            maxOf(shape.start.x, shape.end.x, shape.topLeft.x, shape.topRight.x, shape.bottomRight.x, shape.bottomLeft.x),
            maxOf(shape.start.y, shape.end.y, shape.topLeft.y, shape.topRight.y, shape.bottomRight.y, shape.bottomLeft.y)
        )
        shape.widthCm = pxToCm(shape.rect.width())
        shape.heightCm = pxToCm(shape.rect.height())
        shape.diameterCm = pxToCm(maxOf(shape.rect.width(), shape.rect.height()))
        shape.lengthCm = pxToCm(distancia(shape.start, shape.end))
        shape.topCm = pxToCm(distancia(shape.topLeft, shape.topRight))
        shape.rightCm = pxToCm(distancia(shape.topRight, shape.bottomRight))
        shape.bottomCm = pxToCm(distancia(shape.bottomLeft, shape.bottomRight))
        shape.leftCm = pxToCm(distancia(shape.topLeft, shape.bottomLeft))
    }

    private fun cloneElement(element: Element): Element {
        return when (element) {
            is Element.Freehand -> Element.Freehand(Path(element.path))
            is Element.TextLabel -> Element.TextLabel(
                text = element.text,
                x = element.x,
                y = element.y,
                textSize = element.textSize
            )
            is Element.InfoBox -> Element.InfoBox(
                text = element.text,
                rect = RectF(element.rect),
                textSize = element.textSize
            )
            is Element.Symbol -> Element.Symbol(
                drawableName = element.drawableName,
                rect = RectF(element.rect)
            )
            is Element.Composite -> Element.Composite(
                path = Path(element.path),
                widthCm = element.widthCm,
                heightCm = element.heightCm,
                contours = element.contours.map { contour ->
                    contour.map { PointF(it.x, it.y) }.toMutableList()
                }.toMutableList(),
                sideCms = element.sideCms.map { it.toMutableList() }.toMutableList(),
                template = element.template,
                rotationDeg = element.rotationDeg,
                reflejado = element.reflejado
            )
            is Element.Shape -> Element.Shape(
                tool = element.tool,
                rect = RectF(element.rect),
                start = PointF(element.start.x, element.start.y),
                end = PointF(element.end.x, element.end.y),
                widthCm = element.widthCm,
                heightCm = element.heightCm,
                diameterCm = element.diameterCm,
                lengthCm = element.lengthCm,
                topLeft = PointF(element.topLeft.x, element.topLeft.y),
                topRight = PointF(element.topRight.x, element.topRight.y),
                bottomRight = PointF(element.bottomRight.x, element.bottomRight.y),
                bottomLeft = PointF(element.bottomLeft.x, element.bottomLeft.y),
                topCm = element.topCm,
                rightCm = element.rightCm,
                bottomCm = element.bottomCm,
                leftCm = element.leftCm,
                cotaHint = element.cotaHint
            )
            is Element.Group -> Element.Group(element.children.map { cloneElement(it) }.toMutableList())
        }
    }

    private fun mirrorElementHorizontally(element: Element, axisX: Float) {
        val matrix = Matrix().apply { setScale(-1f, 1f, axisX, 0f) }
        when (element) {
            is Element.Freehand -> element.path.transform(matrix)
            is Element.TextLabel -> {
                val bounds = boundsForText(element)
                element.x = axisX - (bounds.right - axisX)
            }
            is Element.InfoBox -> {
                val oldLeft = element.rect.left
                val oldRight = element.rect.right
                element.rect.left = axisX - (oldRight - axisX)
                element.rect.right = axisX - (oldLeft - axisX)
            }
            is Element.Symbol -> {
                val oldLeft = element.rect.left
                val oldRight = element.rect.right
                element.rect.left = axisX - (oldRight - axisX)
                element.rect.right = axisX - (oldLeft - axisX)
            }
            is Element.Composite -> {
                element.path.transform(matrix)
                transformCompositePoints(element, matrix)
                element.contours.forEach { it.reverse() }
                // Reflejar invierte el sentido de la rotación: así el ángulo guardado sigue siendo
                // el que hay que des-rotar para normalizar (importa al combinar rotar + reflejar).
                element.rotationDeg = ((360f - element.rotationDeg) % 360f + 360f) % 360f
                element.reflejado = !element.reflejado
                rebuildCompositePath(element)
                refreshCompositeSides(element)
            }
            is Element.Shape -> {
                mirrorPoint(element.start, axisX)
                mirrorPoint(element.end, axisX)
                mirrorPoint(element.topLeft, axisX)
                mirrorPoint(element.topRight, axisX)
                mirrorPoint(element.bottomRight, axisX)
                mirrorPoint(element.bottomLeft, axisX)
                val oldTopLeft = PointF(element.topLeft.x, element.topLeft.y)
                val oldBottomLeft = PointF(element.bottomLeft.x, element.bottomLeft.y)
                element.topLeft.x = element.topRight.x.also { element.topRight.x = oldTopLeft.x }
                element.topLeft.y = element.topRight.y.also { element.topRight.y = oldTopLeft.y }
                element.bottomLeft.x = element.bottomRight.x.also { element.bottomRight.x = oldBottomLeft.x }
                element.bottomLeft.y = element.bottomRight.y.also { element.bottomRight.y = oldBottomLeft.y }
                element.rect.set(
                    minOf(element.topLeft.x, element.topRight.x, element.bottomRight.x, element.bottomLeft.x),
                    minOf(element.topLeft.y, element.topRight.y, element.bottomRight.y, element.bottomLeft.y),
                    maxOf(element.topLeft.x, element.topRight.x, element.bottomRight.x, element.bottomLeft.x),
                    maxOf(element.topLeft.y, element.topRight.y, element.bottomRight.y, element.bottomLeft.y)
                )
            }
            is Element.Group -> element.children.forEach { child -> mirrorElementHorizontally(child, axisX) }
        }
    }

    private fun mirrorPoint(point: PointF, axisX: Float) {
        point.x = axisX - (point.x - axisX)
    }

    private fun pathForShape(shape: Element.Shape): Path {
        return Path().apply {
            when (shape.tool) {
                Tool.RECTANGLE -> {
                    moveTo(shape.topLeft.x, shape.topLeft.y)
                    lineTo(shape.topRight.x, shape.topRight.y)
                    lineTo(shape.bottomRight.x, shape.bottomRight.y)
                    lineTo(shape.bottomLeft.x, shape.bottomLeft.y)
                    close()
                }
                Tool.TRIANGLE -> {
                    moveTo(shape.rect.centerX(), shape.rect.top)
                    lineTo(shape.rect.right, shape.rect.bottom)
                    lineTo(shape.rect.left, shape.rect.bottom)
                    close()
                }
                Tool.CIRCLE -> addOval(shape.rect, Path.Direction.CW)
                Tool.LINE, Tool.ORTHO_LINE -> {
                    moveTo(shape.start.x, shape.start.y)
                    lineTo(shape.end.x, shape.end.y)
                }
                Tool.NONE, Tool.FREEHAND, Tool.TEXT, Tool.SELECT -> Unit
            }
        }
    }

    private fun drawCotas(canvas: Canvas, index: Int, shape: Element.Shape, collectHits: Boolean) {
        when (shape.tool) {
            Tool.RECTANGLE -> {
                drawHorizontalCota(
                    canvas = canvas,
                    index = index,
                    type = CotaType.RECT_TOP,
                    left = shape.topLeft.x,
                    right = shape.topRight.x,
                    yBase = shape.topLeft.y,
                    value = shape.topCm,
                    collectHits = collectHits,
                    preferOutsideAbove = true
                )
                drawVerticalCota(
                    canvas = canvas,
                    index = index,
                    type = CotaType.RECT_RIGHT,
                    xBase = shape.topRight.x,
                    top = shape.topRight.y,
                    bottom = shape.bottomRight.y,
                    value = shape.rightCm,
                    collectHits = collectHits,
                    preferOutsideRight = true
                )
                drawHorizontalCota(
                    canvas = canvas,
                    index = index,
                    type = CotaType.RECT_BOTTOM,
                    left = shape.bottomLeft.x,
                    right = shape.bottomRight.x,
                    yBase = shape.bottomLeft.y,
                    value = shape.bottomCm,
                    collectHits = collectHits,
                    preferOutsideAbove = false
                )
                drawVerticalCota(
                    canvas = canvas,
                    index = index,
                    type = CotaType.RECT_LEFT,
                    xBase = shape.topLeft.x,
                    top = shape.topLeft.y,
                    bottom = shape.bottomLeft.y,
                    value = shape.leftCm,
                    collectHits = collectHits,
                    preferOutsideRight = false
                )
            }
            Tool.TRIANGLE -> {
                drawHorizontalCota(canvas, index, CotaType.WIDTH, shape.rect.left, shape.rect.right, shape.rect.bottom, shape.widthCm, collectHits)
                drawVerticalCota(canvas, index, CotaType.HEIGHT, shape.rect.centerX(), shape.rect.top, shape.rect.bottom, shape.heightCm, collectHits)
            }
            Tool.CIRCLE -> {
                drawHorizontalCota(canvas, index, CotaType.DIAMETER, shape.rect.left, shape.rect.right, shape.rect.centerY(), shape.diameterCm, collectHits)
            }
            Tool.LINE, Tool.ORTHO_LINE -> {
                if (shape.cotaHint == "GRADA_TOTAL_COTA") {
                    drawCotaTotalGraderia(canvas, index, shape, collectHits)
                    return
                }
                val midX = (shape.start.x + shape.end.x) / 2f
                val midY = (shape.start.y + shape.end.y) / 2f
                canvas.drawLine(shape.start.x, shape.start.y, shape.end.x, shape.end.y, cotaLinePaint)
                val offset = 18f
                val (labelX, labelY) = when (shape.cotaHint) {
                    "GRADA_PASO" -> midX to (midY + offset)
                    "GRADA_CONTRAPASO" -> (minOf(shape.start.x, shape.end.x) - offset) to midY
                    else -> midX to (midY - offset)
                }
                if (shape.cotaHint == "GRADA_PASO" || shape.cotaHint == "GRADA_CONTRAPASO") {
                    drawCotaTextFija(canvas, index, CotaType.LENGTH, labelX, labelY, shape.lengthCm, collectHits)
                } else {
                    drawCotaText(canvas, index, CotaType.LENGTH, labelX, labelY, shape.lengthCm, collectHits)
                }
            }
            Tool.NONE, Tool.FREEHAND, Tool.TEXT, Tool.SELECT -> Unit
        }
    }

    private fun drawCotaTotalGraderia(canvas: Canvas, index: Int, shape: Element.Shape, collectHits: Boolean) {
        val dx = shape.end.x - shape.start.x
        val dy = shape.end.y - shape.start.y
        val len = hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(1f)
        var nx = -dy / len
        var ny = dx / len
        if (ny < 0f) {
            nx = -nx
            ny = -ny
        }
        val offset = 42f
        val ax = shape.start.x + nx * offset
        val ay = shape.start.y + ny * offset
        val bx = shape.end.x + nx * offset
        val by = shape.end.y + ny * offset
        canvas.drawLine(ax, ay, bx, by, cotaLinePaint)
        canvas.drawLine(ax - nx * 8f, ay - ny * 8f, ax + nx * 8f, ay + ny * 8f, cotaLinePaint)
        canvas.drawLine(bx - nx * 8f, by - ny * 8f, bx + nx * 8f, by + ny * 8f, cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.LENGTH,
            cx = (ax + bx) / 2f,
            cy = (ay + by) / 2f + 14f,
            value = shape.lengthCm,
            collectHits = collectHits
        )
    }

    private fun drawCotaTextFija(
        canvas: Canvas,
        index: Int,
        type: CotaType,
        cx: Float,
        cy: Float,
        value: Float,
        collectHits: Boolean
    ) {
        val text = formatCm(value)
        val widthText = cotaTextPaint.measureText(text)
        val fontMetrics = cotaTextPaint.fontMetrics
        val padH = 4f * resources.displayMetrics.density
        val padV = 2f * resources.displayMetrics.density
        val textHeight = fontMetrics.descent - fontMetrics.ascent
        val hit = RectF(
            cx - widthText / 2f - padH,
            cy - textHeight / 2f - padV,
            cx + widthText / 2f + padH,
            cy + textHeight / 2f + padV
        )
        val drawCx = hit.centerX()
        val drawCy = hit.centerY() - (fontMetrics.ascent + fontMetrics.descent) / 2f
        canvas.drawRoundRect(hit, 6f, 6f, cotaBgPaint)
        canvas.drawText(text, drawCx, drawCy, cotaTextPaint)
        cotaTextRects.add(RectF(hit))
        if (collectHits) cotaHits.add(CotaHit(index, type, hit))
    }

    private fun drawCompositeCotas(canvas: Canvas, index: Int, composite: Element.Composite, collectHits: Boolean) {
        if (composite.template == TEMPLATE_F5) {
            drawF5Cotas(canvas, index, composite, collectHits)
            return
        }
        if (composite.template == TEMPLATE_F6) {
            drawF6Cotas(canvas, index, composite, collectHits)
            return
        }
        if (composite.template == TEMPLATE_ROUNDED) {
            drawRoundedCornerCotas(canvas, index, composite, collectHits)
            return
        }
        val bounds = boundsForElement(composite)
        composite.contours.forEachIndexed { contourIndex, contour ->
            if (contour.size < 2) return@forEachIndexed
            contour.indices.forEach { sideIndex ->
                val a = contour[sideIndex]
                val b = contour[(sideIndex + 1) % contour.size]
                val value = composite.sideCms
                    .getOrNull(contourIndex)
                    ?.getOrNull(sideIndex)
                    ?: pxToCm(distancia(a, b))
                drawCompositeSideCota(
                    canvas = canvas,
                    index = index,
                    contourIndex = contourIndex,
                    sideIndex = sideIndex,
                    a = a,
                    b = b,
                    bounds = bounds,
                    value = value,
                    collectHits = collectHits
                )
            }
        }
        if (composite.template == TEMPLATE_F4) {
            drawF4CotaTotalInterior(canvas, index, composite, collectHits)
        }
    }

    private fun drawF5Cotas(canvas: Canvas, index: Int, composite: Element.Composite, collectHits: Boolean) {
        val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val ellipseH = dims.flecha * 2f
        val topArc = RectF(
            dims.left,
            dims.top - ellipseH / 2f - 24f,
            dims.left + dims.rectW,
            dims.top + ellipseH / 2f - 24f
        )
        canvas.drawArc(topArc, 180f, 180f, false, cotaLinePaint)
        canvas.drawLine(dims.left, dims.top - 8f, dims.left, dims.top - 32f, cotaLinePaint)
        canvas.drawLine(dims.left + dims.rectW, dims.top - 8f, dims.left + dims.rectW, dims.top - 32f, cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.F5_DESARROLLO,
            cx = dims.left + dims.rectW / 2f,
            cy = topArc.top - 8f,
            value = pxToCm(longitudMediaElipsePx(dims.rectW, dims.flecha)),
            collectHits = collectHits
        )

        drawHorizontalCota(
            canvas = canvas,
            index = index,
            type = CotaType.WIDTH,
            left = dims.left,
            right = dims.left + dims.rectW,
            yBase = dims.top + dims.rectH,
            value = pxToCm(dims.rectW),
            collectHits = collectHits,
            preferOutsideAbove = false
        )

        val centerX = dims.left + dims.rectW / 2f
        val chordY = dims.top + dims.rectH
        val arcCenterY = chordY - dims.flecha
        canvas.drawLine(centerX, chordY, centerX, arcCenterY, cotaLinePaint)
        canvas.drawLine(centerX - 8f, chordY, centerX + 8f, chordY, cotaLinePaint)
        canvas.drawLine(centerX - 8f, arcCenterY, centerX + 8f, arcCenterY, cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.F5_FLECHA,
            cx = centerX + 28f,
            cy = (chordY + arcCenterY) / 2f,
            value = pxToCm(dims.flecha),
            collectHits = collectHits,
            angleDegrees = -90f
        )

        drawVerticalCota(
            canvas = canvas,
            index = index,
            type = CotaType.HEIGHT,
            xBase = dims.left,
            top = dims.top,
            bottom = dims.top + dims.rectH,
            value = pxToCm(dims.rectH),
            collectHits = collectHits,
            preferOutsideRight = false
        )
    }

    private fun drawRoundedCornerCotas(canvas: Canvas, index: Int, composite: Element.Composite, collectHits: Boolean) {
        val dims = dimsRoundedCorners(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        drawHorizontalCota(
            canvas = canvas,
            index = index,
            type = CotaType.WIDTH,
            left = dims.left,
            right = dims.left + dims.rectW,
            yBase = dims.top + dims.rectH,
            value = pxToCm(dims.rectW),
            collectHits = collectHits,
            preferOutsideAbove = false
        )
        drawVerticalCota(
            canvas = canvas,
            index = index,
            type = CotaType.HEIGHT,
            xBase = dims.left,
            top = dims.top,
            bottom = dims.top + dims.rectH,
            value = pxToCm(dims.rectH),
            collectHits = collectHits,
            preferOutsideRight = false
        )
        dims.radii.forEachIndexed { corner, radius ->
            if (radius > 0f) drawRoundedCornerRadiusCota(canvas, index, corner, dims, radius, collectHits)
        }
    }

    private fun drawRoundedCornerRadiusCota(
        canvas: Canvas,
        index: Int,
        corner: Int,
        dims: RoundedCornerDims,
        radius: Float,
        collectHits: Boolean
    ) {
        val (cx, cy) = when (corner) {
            0 -> dims.left + radius to dims.top + radius
            1 -> dims.left + dims.rectW - radius to dims.top + radius
            2 -> dims.left + dims.rectW - radius to dims.top + dims.rectH - radius
            else -> dims.left + radius to dims.top + dims.rectH - radius
        }
        val labelX = when (corner) {
            0, 3 -> cx + 36f
            else -> cx - 36f
        }
        val labelY = when (corner) {
            0, 1 -> cy + 4f
            else -> cy - 18f
        }
        canvas.drawLine(cx, cy, labelX, labelY, cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.ROUNDED_RADIUS,
            cx = labelX,
            cy = labelY,
            value = pxToCm(radius),
            collectHits = collectHits,
            sideIndex = corner
        )
    }

    private fun drawF6Cotas(canvas: Canvas, index: Int, composite: Element.Composite, collectHits: Boolean) {
        val dims = dimsRecurrenteF6(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        drawHorizontalCota(
            canvas = canvas,
            index = index,
            type = CotaType.WIDTH,
            left = dims.left,
            right = dims.left + dims.rectW,
            yBase = dims.top + dims.rectH,
            value = pxToCm(dims.rectW),
            collectHits = collectHits,
            preferOutsideAbove = false
        )
        drawVerticalCota(
            canvas = canvas,
            index = index,
            type = CotaType.HEIGHT,
            xBase = dims.left,
            top = dims.top,
            bottom = dims.top + dims.rectH,
            value = pxToCm(dims.rectH),
            collectHits = collectHits,
            preferOutsideRight = false
        )

        val arcBounds = RectF(dims.left, dims.top, dims.left + dims.radius * 2f, dims.top + dims.radius * 2f)
        canvas.drawArc(arcBounds, 180f, 90f, false, cotaLinePaint)
        canvas.drawLine(dims.left, dims.top + dims.radius, dims.left + dims.radius, dims.top + dims.radius, cotaLinePaint)
        canvas.drawLine(dims.left + dims.radius, dims.top, dims.left + dims.radius, dims.top + dims.radius, cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.F6_RADIO,
            cx = dims.left + dims.radius + 34f,
            cy = dims.top + dims.radius,
            value = pxToCm(dims.radius),
            collectHits = collectHits
        )
    }

    private fun drawCompositeSideCota(
        canvas: Canvas,
        index: Int,
        contourIndex: Int,
        sideIndex: Int,
        a: PointF,
        b: PointF,
        bounds: RectF,
        value: Float,
        collectHits: Boolean
    ) {
        if (distancia(a, b) < 5f) return
        // Resaltar el borde si el lado está bloqueado.
        (elementos.getOrNull(index) as? Element.Composite)?.let {
            if (ladoBloqueado(it, contourIndex, sideIndex)) canvas.drawLine(a.x, a.y, b.x, b.y, ladoBloqueadoPaint)
        }
        val dx = b.x - a.x
        val dy = b.y - a.y
        if (abs(dx) > 4f && abs(dy) > 4f) {
            drawDiagonalCompositeCota(
                canvas = canvas,
                index = index,
                contourIndex = contourIndex,
                sideIndex = sideIndex,
                a = a,
                b = b,
                bounds = bounds,
                value = value,
                collectHits = collectHits
            )
        } else if (abs(dx) >= abs(dy)) {
            val y = (if ((a.y + b.y) / 2f < bounds.centerY()) minOf(a.y, b.y) - 20f else maxOf(a.y, b.y) + 34f)
                .coerceIn(32f, height - 12f)
            canvas.drawLine(a.x, y, b.x, y, cotaLinePaint)
            canvas.drawLine(a.x, y - 8f, a.x, y + 8f, cotaLinePaint)
            canvas.drawLine(b.x, y - 8f, b.x, y + 8f, cotaLinePaint)
            drawCotaText(
                canvas = canvas,
                index = index,
                type = CotaType.COMPOSITE_SIDE,
                cx = (a.x + b.x) / 2f,
                cy = y - 8f,
                value = value,
                collectHits = collectHits,
                contourIndex = contourIndex,
                sideIndex = sideIndex
            )
        } else {
            val x = (if ((a.x + b.x) / 2f < bounds.centerX()) minOf(a.x, b.x) - 28f else maxOf(a.x, b.x) + 28f)
                .coerceIn(40f, width - 40f)
            canvas.drawLine(x, a.y, x, b.y, cotaLinePaint)
            canvas.drawLine(x - 8f, a.y, x + 8f, a.y, cotaLinePaint)
            canvas.drawLine(x - 8f, b.y, x + 8f, b.y, cotaLinePaint)
            drawCotaText(
                canvas = canvas,
                index = index,
                type = CotaType.COMPOSITE_SIDE,
                cx = x,
                cy = (a.y + b.y) / 2f,
                value = value,
                collectHits = collectHits,
                contourIndex = contourIndex,
                sideIndex = sideIndex
            )
        }
    }

    private fun drawDiagonalCompositeCota(
        canvas: Canvas,
        index: Int,
        contourIndex: Int,
        sideIndex: Int,
        a: PointF,
        b: PointF,
        bounds: RectF,
        value: Float,
        collectHits: Boolean
    ) {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val len = hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(1f)
        var nx = -dy / len
        var ny = dx / len
        val midX = (a.x + b.x) / 2f
        val midY = (a.y + b.y) / 2f
        val offset = 30f
        val outA = distancia(PointF(midX + nx * offset, midY + ny * offset), PointF(bounds.centerX(), bounds.centerY()))
        val outB = distancia(PointF(midX - nx * offset, midY - ny * offset), PointF(bounds.centerX(), bounds.centerY()))
        if (outB > outA) {
            nx = -nx
            ny = -ny
        }
        val ax = a.x + nx * offset
        val ay = a.y + ny * offset
        val bx = b.x + nx * offset
        val by = b.y + ny * offset
        canvas.drawLine(ax, ay, bx, by, cotaLinePaint)
        canvas.drawLine(ax - nx * 8f, ay - ny * 8f, ax + nx * 8f, ay + ny * 8f, cotaLinePaint)
        canvas.drawLine(bx - nx * 8f, by - ny * 8f, bx + nx * 8f, by + ny * 8f, cotaLinePaint)
        var angle = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
        if (angle > 90f) angle -= 180f
        if (angle < -90f) angle += 180f
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.COMPOSITE_SIDE,
            cx = (ax + bx) / 2f,
            cy = (ay + by) / 2f - 8f,
            value = value,
            collectHits = collectHits,
            contourIndex = contourIndex,
            sideIndex = sideIndex,
            angleDegrees = angle
        )
    }

    private fun drawF4CotaTotalInterior(
        canvas: Canvas,
        index: Int,
        composite: Element.Composite,
        collectHits: Boolean
    ) {
        val contour = composite.contours.firstOrNull() ?: return
        val dims = dimsRecurrenteF4(contour) ?: return
        if (dims.leftW <= 0f || dims.rightW <= 0f) return
        val x = dims.centralLeft + dims.centralW / 2f
        val top = dims.top
        val bottom = dims.top + dims.centralH
        canvas.drawLine(x, top, x, bottom, cotaLinePaint)
        canvas.drawLine(x - 8f, top, x + 8f, top, cotaLinePaint)
        canvas.drawLine(x - 8f, bottom, x + 8f, bottom, cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.HEIGHT,
            cx = x,
            cy = (top + bottom) / 2f,
            value = pxToCm(dims.centralH),
            collectHits = collectHits,
            angleDegrees = -90f
        )
    }

    private fun drawHorizontalCota(
        canvas: Canvas,
        index: Int,
        type: CotaType,
        left: Float,
        right: Float,
        yBase: Float,
        value: Float,
        collectHits: Boolean,
        preferOutsideAbove: Boolean? = null
    ) {
        val y = when (preferOutsideAbove) {
            true -> if (yBase < 58f) yBase + 28f else yBase - 20f
            false -> if (yBase > height - 58f) yBase - 28f else yBase + 34f
            null -> if (yBase < 58f) yBase + 28f else yBase - 20f
        }
        canvas.drawLine(left, y, right, y, cotaLinePaint)
        canvas.drawLine(left, y - 8f, left, y + 8f, cotaLinePaint)
        canvas.drawLine(right, y - 8f, right, y + 8f, cotaLinePaint)
        drawCotaText(canvas, index, type, (left + right) / 2f, y - 8f, value, collectHits)
    }

    private fun drawVerticalCota(
        canvas: Canvas,
        index: Int,
        type: CotaType,
        xBase: Float,
        top: Float,
        bottom: Float,
        value: Float,
        collectHits: Boolean,
        preferOutsideRight: Boolean? = null
    ) {
        val x = when (preferOutsideRight) {
            true -> if (xBase > width - 70f) xBase - 34f else xBase + 28f
            false -> if (xBase < 70f) xBase + 34f else xBase - 28f
            null -> if (xBase > width - 70f) xBase - 34f else xBase + 28f
        }
        canvas.drawLine(x, top, x, bottom, cotaLinePaint)
        canvas.drawLine(x - 8f, top, x + 8f, top, cotaLinePaint)
        canvas.drawLine(x - 8f, bottom, x + 8f, bottom, cotaLinePaint)
        drawCotaText(canvas, index, type, x, (top + bottom) / 2f, value, collectHits)
    }

    private fun drawCotaText(
        canvas: Canvas,
        index: Int,
        type: CotaType,
        cx: Float,
        cy: Float,
        value: Float,
        collectHits: Boolean,
        contourIndex: Int? = null,
        sideIndex: Int? = null,
        angleDegrees: Float = 0f
    ) {
        val text = formatCm(value)
        val widthText = cotaTextPaint.measureText(text)
        val fontMetrics = cotaTextPaint.fontMetrics
        val padH = 4f * resources.displayMetrics.density
        val padV = 2f * resources.displayMetrics.density
        val textHeight = fontMetrics.descent - fontMetrics.ascent
        val original = RectF(
            cx - widthText / 2f - padH,
            cy - textHeight / 2f - padV,
            cx + widthText / 2f + padH,
            cy + textHeight / 2f + padV
        )
        val hit = posicionLibreParaCota(original)
        val drawCx = hit.centerX()
        val drawCy = hit.centerY() - (fontMetrics.ascent + fontMetrics.descent) / 2f
        if (abs(angleDegrees) > 0.1f) {
            canvas.save()
            canvas.rotate(angleDegrees, hit.centerX(), hit.centerY())
            canvas.drawRoundRect(hit, 6f, 6f, cotaBgPaint)
            canvas.drawText(text, drawCx, drawCy, cotaTextPaint)
            canvas.restore()
        } else {
            canvas.drawRoundRect(hit, 6f, 6f, cotaBgPaint)
            canvas.drawText(text, drawCx, drawCy, cotaTextPaint)
        }
        cotaTextRects.add(RectF(hit))
        if (collectHits) cotaHits.add(CotaHit(index, type, hit, contourIndex, sideIndex))
    }

    private fun posicionLibreParaCota(original: RectF): RectF {
        if (!chocaConCota(original)) return original
        val step = 38f
        val candidates = mutableListOf<RectF>()
        for (level in 1..5) {
            val distance = step * level
            candidates.add(RectF(original).apply { offset(0f, -distance) })
            candidates.add(RectF(original).apply { offset(0f, distance) })
            candidates.add(RectF(original).apply { offset(-distance, 0f) })
            candidates.add(RectF(original).apply { offset(distance, 0f) })
            candidates.add(RectF(original).apply { offset(-distance, -distance) })
            candidates.add(RectF(original).apply { offset(distance, -distance) })
            candidates.add(RectF(original).apply { offset(-distance, distance) })
            candidates.add(RectF(original).apply { offset(distance, distance) })
        }
        return candidates.firstOrNull { !chocaConCota(it) } ?: candidates.minByOrNull { areaChoqueCotas(it) } ?: original
    }

    private fun chocaConCota(rect: RectF): Boolean {
        val padded = RectF(rect).apply { inset(-6f, -6f) }
        return cotaTextRects.any { RectF.intersects(padded, it) }
    }

    private fun areaChoqueCotas(rect: RectF): Float {
        val padded = RectF(rect).apply { inset(-6f, -6f) }
        return cotaTextRects.sumOf { existing ->
            val left = maxOf(padded.left, existing.left)
            val top = maxOf(padded.top, existing.top)
            val right = minOf(padded.right, existing.right)
            val bottom = minOf(padded.bottom, existing.bottom)
            if (right > left && bottom > top) ((right - left) * (bottom - top)).toDouble() else 0.0
        }.toFloat()
    }

    private fun cotaEn(x: Float, y: Float): CotaHit? {
        return cotaHits.lastOrNull { it.rect.contains(x, y) }
    }

    private fun editarCota(hit: CotaHit) {
        val element = elementos.getOrNull(hit.elementIndex) ?: return
        val actual = when (hit.type) {
            CotaType.WIDTH -> when (element) {
                is Element.Shape -> element.widthCm
                is Element.Composite -> element.widthCm
                is Element.Freehand -> return
                is Element.Group -> return
                is Element.TextLabel -> return
                is Element.InfoBox -> return
                is Element.Symbol -> return
            }
            CotaType.HEIGHT -> when (element) {
                is Element.Shape -> element.heightCm
                is Element.Composite -> element.heightCm
                is Element.Freehand -> return
                is Element.Group -> return
                is Element.TextLabel -> return
                is Element.InfoBox -> return
                is Element.Symbol -> return
            }
            CotaType.RECT_TOP -> (element as? Element.Shape)?.topCm ?: return
            CotaType.RECT_RIGHT -> (element as? Element.Shape)?.rightCm ?: return
            CotaType.RECT_BOTTOM -> (element as? Element.Shape)?.bottomCm ?: return
            CotaType.RECT_LEFT -> (element as? Element.Shape)?.leftCm ?: return
            CotaType.DIAMETER -> (element as? Element.Shape)?.diameterCm ?: return
            CotaType.LENGTH -> (element as? Element.Shape)?.lengthCm ?: return
            CotaType.COMPOSITE_SIDE -> {
                val composite = element as? Element.Composite ?: return
                val contourIndex = hit.contourIndex ?: return
                val sideIndex = hit.sideIndex ?: return
                composite.sideCms.getOrNull(contourIndex)?.getOrNull(sideIndex) ?: return
            }
            CotaType.F5_DESARROLLO -> {
                val composite = element as? Element.Composite ?: return
                val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
                pxToCm(longitudMediaElipsePx(dims.rectW, dims.flecha))
            }
            CotaType.F5_FLECHA -> {
                val composite = element as? Element.Composite ?: return
                val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
                pxToCm(dims.flecha)
            }
            CotaType.F6_RADIO -> {
                val composite = element as? Element.Composite ?: return
                val dims = dimsRecurrenteF6(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
                pxToCm(dims.radius)
            }
            CotaType.ROUNDED_RADIUS -> {
                val composite = element as? Element.Composite ?: return
                val dims = dimsRoundedCorners(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
                val corner = hit.sideIndex ?: return
                pxToCm(dims.radii.getOrNull(corner) ?: return)
            }
        }
        val input = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or
                InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(formatCm(actual))
            setSelectAllOnFocus(true)
        }
        AlertDialog.Builder(context)
            .setTitle("Editar cota (negativo = izquierda/abajo)")
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                val nuevo = input.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                if (nuevo != null && nuevo != 0f) {
                    aplicarNuevaCota(element, hit, nuevo)
                    registrarAccion()
                    invalidate()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun ladoKey(contour: Int, side: Int): Long = contour.toLong() * 100000L + side.toLong()

    private fun ladoBloqueado(composite: Element.Composite, contour: Int, side: Int): Boolean =
        composite.bloqueados.contains(ladoKey(contour, side))

    // Long-press sobre una cota de lado → bloquear/desbloquear ese lado (queda inmune a ediciones).
    private fun alternarBloqueoLado(hit: CotaHit) {
        val el = elementos.getOrNull(hit.elementIndex) as? Element.Composite ?: run {
            Toast.makeText(context, "Solo se bloquean lados de formas recurrentes", Toast.LENGTH_SHORT).show()
            return
        }
        val c = hit.contourIndex ?: 0
        val s = hit.sideIndex ?: run {
            Toast.makeText(context, "Ese no es un lado individual", Toast.LENGTH_SHORT).show()
            return
        }
        val key = ladoKey(c, s)
        if (el.bloqueados.remove(key)) {
            Toast.makeText(context, "Lado desbloqueado 🔓", Toast.LENGTH_SHORT).show()
        } else {
            el.bloqueados.add(key)
            Toast.makeText(context, "Lado bloqueado 🔒 (no se moverá al editar otros)", Toast.LENGTH_SHORT).show()
        }
        registrarAccion()
        invalidate()
    }

    // Aplica una matriz a un Composite (path + contornos + reconstrucción + lados). Se usa para
    // des-rotar/re-rotar al editar cotas de formas rotadas, sin deformarlas.
    private fun transformarComposite(composite: Element.Composite, matrix: Matrix, actualizarCm: Boolean) {
        composite.path.transform(matrix)
        transformCompositePoints(composite, matrix)
        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
        if (actualizarCm) {
            val b = boundsForElement(composite)
            composite.widthCm = pxToCm(b.width())
            composite.heightCm = pxToCm(b.height())
        }
    }

    // Plantillas poligonales cuya edición lee diferencias de vértices con signo (sensibles a la
    // reflexión). Las paramétricas (F5/F6/redondeados) se editan por límites y no lo necesitan.
    private val TEMPLATES_POLIGONALES = setOf(TEMPLATE_F1, TEMPLATE_F2, TEMPLATE_F3, TEMPLATE_F4)

    // El espejo hace flip + reverse (que conserva el signo del área), así que la reflexión no se
    // puede detectar por geometría: se rastrea con la bandera `reflejado` que alterna el espejo.
    private fun compositeReflejado(element: Element.Composite): Boolean =
        element.template in TEMPLATES_POLIGONALES && element.reflejado

    private fun aplicarNuevaCota(element: Element, hit: CotaHit, valueCm: Float) {
        when (element) {
            is Element.Shape -> aplicarNuevaCotaShape(element, hit.type, valueCm, hit.elementIndex)
            is Element.Composite -> {
                val ang = element.rotationDeg
                val reflejado = compositeReflejado(element)
                if (ang == 0f && !reflejado) {
                    aplicarNuevaCotaComposite(element, hit, valueCm)
                } else {
                    // La lógica de edición asume ejes de pantalla y el orden canónico de vértices.
                    // Normalizo: des-roto y, si está reflejada, la des-reflejo (esto invierte el orden,
                    // así que remapeo el lado tocado). Edito en canónico y vuelvo a reflejar/rotar en el
                    // mismo lugar: se respeta la posición actual (rotada/reflejada) sin deformar.
                    val c0 = boundsForElement(element).let { PointF(it.centerX(), it.centerY()) }
                    if (ang != 0f) {
                        transformarComposite(element, Matrix().apply { setRotate(-ang, c0.x, c0.y) }, actualizarCm = true)
                    }
                    var hitCanonico = hit
                    val axisX = boundsForElement(element).centerX()
                    if (reflejado) {
                        mirrorElementHorizontally(element, axisX)  // des-reflejar (eje fijo)
                        val n = element.contours.getOrNull(hit.contourIndex ?: 0)?.size
                        val s = hit.sideIndex
                        if (n != null && s != null) {
                            hitCanonico = hit.copy(sideIndex = ((n - 2 - s) % n + n) % n)
                        }
                    }
                    aplicarNuevaCotaComposite(element, hitCanonico, valueCm)
                    if (reflejado) {
                        mirrorElementHorizontally(element, axisX)  // volver a reflejar (mismo eje)
                    }
                    if (ang != 0f) {
                        val c1 = boundsForElement(element).let { PointF(it.centerX(), it.centerY()) }
                        transformarComposite(element, Matrix().apply {
                            postTranslate(c0.x - c1.x, c0.y - c1.y) // re-centrar en el mismo punto
                            postRotate(ang, c0.x, c0.y)             // volver a rotar
                        }, actualizarCm = false)
                    }
                }
            }
            is Element.Freehand -> Unit
            is Element.Group -> Unit
            is Element.TextLabel -> Unit
            is Element.InfoBox -> Unit
            is Element.Symbol -> Unit
        }
    }

    private fun aplicarNuevaCotaShape(shape: Element.Shape, type: CotaType, valueCm: Float, elementIndex: Int? = null) {
        when (type) {
            CotaType.WIDTH -> {
                shape.widthCm = valueCm
                val newWidth = cmToPx(valueCm)
                shape.rect.right = shape.rect.left + newWidth
                shape.start.x = shape.rect.left
                shape.start.y = shape.rect.top
                shape.end.x = shape.rect.right
                shape.end.y = shape.rect.bottom
                if (shape.tool == Tool.CIRCLE) {
                    shape.diameterCm = valueCm
                    shape.heightCm = valueCm
                    shape.rect.bottom = shape.rect.top + newWidth
                }
            }
            CotaType.HEIGHT -> {
                shape.heightCm = valueCm
                val newHeight = cmToPx(valueCm)
                shape.rect.bottom = shape.rect.top + newHeight
                shape.start.x = shape.rect.left
                shape.start.y = shape.rect.top
                shape.end.x = shape.rect.right
                shape.end.y = shape.rect.bottom
            }
            CotaType.RECT_TOP -> {
                shape.topCm = valueCm
                shape.topRight.x = shape.topLeft.x + cmToPx(valueCm)
                actualizarBoundsRectangulo(shape)
            }
            CotaType.RECT_RIGHT -> {
                shape.rightCm = valueCm
                fijarBaseRectangulo(shape)
                shape.topRight.y = shape.bottomRight.y - cmToPx(valueCm)
                actualizarBoundsRectangulo(shape)
            }
            CotaType.RECT_BOTTOM -> {
                shape.bottomCm = valueCm
                shape.bottomRight.x = shape.bottomLeft.x + cmToPx(valueCm)
                actualizarBoundsRectangulo(shape)
            }
            CotaType.RECT_LEFT -> {
                shape.leftCm = valueCm
                fijarBaseRectangulo(shape)
                shape.topLeft.y = shape.bottomLeft.y - cmToPx(valueCm)
                actualizarBoundsRectangulo(shape)
            }
            CotaType.DIAMETER -> {
                shape.diameterCm = valueCm
                shape.widthCm = valueCm
                shape.heightCm = valueCm
                val centerX = shape.rect.centerX()
                val centerY = shape.rect.centerY()
                val size = cmToPx(valueCm)
                shape.rect.set(
                    centerX - size / 2f,
                    centerY - size / 2f,
                    centerX + size / 2f,
                    centerY + size / 2f
                )
                shape.start.x = shape.rect.left
                shape.start.y = shape.rect.top
                shape.end.x = shape.rect.right
                shape.end.y = shape.rect.bottom
            }
            CotaType.LENGTH -> {
                if (shape.cotaHint == "GRADA_PASO" || shape.cotaHint == "GRADA_CONTRAPASO") {
                    aplicarNuevaCotaGraderia(elementIndex ?: return, valueCm)
                    return
                }
                shape.lengthCm = valueCm
                val dx = shape.end.x - shape.start.x
                val dy = shape.end.y - shape.start.y
                val currentLength = hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(1f)
                val angle = kotlin.math.atan2(dy.toDouble(), dx.toDouble())
                val newLength = cmToPx(valueCm)
                shape.end.x = shape.start.x + (cos(angle) * newLength).toFloat()
                shape.end.y = shape.start.y + (sin(angle) * newLength).toFloat()
                shape.rect.set(
                    minOf(shape.start.x, shape.end.x),
                    minOf(shape.start.y, shape.end.y),
                    maxOf(shape.start.x, shape.end.x),
                    maxOf(shape.start.y, shape.end.y)
                )
                if (currentLength > 0f) Unit
            }
            CotaType.COMPOSITE_SIDE,
            CotaType.F5_DESARROLLO,
            CotaType.F5_FLECHA,
            CotaType.F6_RADIO,
            CotaType.ROUNDED_RADIUS -> Unit
        }
    }

    private fun aplicarNuevaCotaGraderia(elementIndex: Int, valueCm: Float) {
        val edited = elementos.getOrNull(elementIndex) as? Element.Shape ?: return
        if (edited.cotaHint !in setOf("GRADA_PASO", "GRADA_CONTRAPASO")) return

        val startIndex = buscarInicioBloqueGraderia(elementIndex)
        val endIndex = buscarFinBloqueGraderia(elementIndex)
        val tramoIndices = (startIndex..endIndex).filter { index ->
            (elementos.getOrNull(index) as? Element.Shape)?.cotaHint in setOf("GRADA_PASO", "GRADA_CONTRAPASO")
        }
        if (tramoIndices.isEmpty()) return

        val editedTramo = tramoIndices.indexOf(elementIndex).takeIf { it >= 0 } ?: return
        val lengthsCm = tramoIndices.map { index ->
            val shape = elementos[index] as Element.Shape
            if (index == elementIndex) valueCm else shape.lengthCm
        }
        val hints = tramoIndices.map { index -> (elementos[index] as Element.Shape).cotaHint.orEmpty() }
        val origin = PointF(
            (elementos[tramoIndices.first()] as Element.Shape).start.x,
            (elementos[tramoIndices.first()] as Element.Shape).start.y
        )

        var x = origin.x
        var y = origin.y
        tramoIndices.forEachIndexed { tramoIndex, elementPosition ->
            val shape = elementos[elementPosition] as Element.Shape
            val lengthPx = cmToPx(lengthsCm[tramoIndex])
            val start = PointF(x, y)
            val end = if (hints[tramoIndex] == "GRADA_CONTRAPASO") {
                PointF(x, y - lengthPx)
            } else {
                PointF(x + lengthPx, y)
            }
            setShapeLine(shape, start, end, lengthsCm[tramoIndex])
            x = end.x
            y = end.y
        }

        val total = (startIndex..endIndex).firstNotNullOfOrNull { index ->
            (elementos.getOrNull(index) as? Element.Shape)?.takeIf { it.cotaHint == "GRADA_TOTAL_COTA" }
        }
        total?.let {
            val totalStart = PointF(origin.x, origin.y)
            val totalEnd = PointF(x, y)
            setShapeLine(it, totalStart, totalEnd, pxToCm(distancia(totalStart, totalEnd)))
        }

        selectedIndices.clear()
        selectedIndices.add(tramoIndices[editedTramo])
    }

    private fun buscarInicioBloqueGraderia(fromIndex: Int): Int {
        var index = fromIndex
        while (index > 0 && esElementoGraderia(elementos.getOrNull(index - 1))) index--
        return index
    }

    private fun buscarFinBloqueGraderia(fromIndex: Int): Int {
        var index = fromIndex
        while (index < elementos.lastIndex && esElementoGraderia(elementos.getOrNull(index + 1))) index++
        return index
    }

    private fun esElementoGraderia(element: Element?): Boolean {
        return (element as? Element.Shape)?.cotaHint in setOf("GRADA_PASO", "GRADA_CONTRAPASO", "GRADA_TOTAL_COTA")
    }

    private fun setShapeLine(shape: Element.Shape, start: PointF, end: PointF, lengthCm: Float) {
        shape.start.x = start.x
        shape.start.y = start.y
        shape.end.x = end.x
        shape.end.y = end.y
        shape.lengthCm = lengthCm
        shape.rect.set(
            minOf(start.x, end.x),
            minOf(start.y, end.y),
            maxOf(start.x, end.x),
            maxOf(start.y, end.y)
        )
        shape.widthCm = pxToCm(shape.rect.width())
        shape.heightCm = pxToCm(shape.rect.height())
        shape.diameterCm = pxToCm(maxOf(shape.rect.width(), shape.rect.height()))
        shape.topLeft.x = shape.rect.left
        shape.topLeft.y = shape.rect.top
        shape.topRight.x = shape.rect.right
        shape.topRight.y = shape.rect.top
        shape.bottomRight.x = shape.rect.right
        shape.bottomRight.y = shape.rect.bottom
        shape.bottomLeft.x = shape.rect.left
        shape.bottomLeft.y = shape.rect.bottom
        shape.topCm = shape.widthCm
        shape.rightCm = shape.heightCm
        shape.bottomCm = shape.widthCm
        shape.leftCm = shape.heightCm
    }

    private fun aplicarNuevaCotaComposite(composite: Element.Composite, hit: CotaHit, valueCmRaw: Float) {
        // El signo (dirección) SOLO aplica a lados individuales; el resto usa el valor absoluto.
        val valueCm = if (hit.type == CotaType.COMPOSITE_SIDE) valueCmRaw else kotlin.math.abs(valueCmRaw)
        val bounds = boundsForElement(composite)
        when (hit.type) {
            CotaType.WIDTH -> {
                if (composite.template == TEMPLATE_F5) {
                    aplicarMedidasRecurrenteF5(composite, anchoCm = valueCm, altoCm = null)
                    return
                }
                if (composite.template == TEMPLATE_F6) {
                    aplicarMedidasRecurrenteF6(composite, anchoCm = valueCm, altoCm = null)
                    return
                }
                if (composite.template == TEMPLATE_ROUNDED) {
                    aplicarMedidasRoundedCorners(composite, anchoCm = valueCm, altoCm = null)
                    return
                }
                val current = bounds.width().coerceAtLeast(1f)
                val scale = cmToPx(valueCm) / current
                val matrix = Matrix().apply { setScale(scale, 1f, bounds.left, bounds.bottom) }
                composite.path.transform(matrix)
                transformCompositePoints(composite, matrix)
                composite.widthCm = valueCm
                refreshCompositeSides(composite)
            }
            CotaType.HEIGHT -> {
                if (composite.template == TEMPLATE_F5) {
                    aplicarMedidasRecurrenteF5(composite, anchoCm = null, altoCm = valueCm)
                    return
                }
                if (composite.template == TEMPLATE_F6) {
                    aplicarMedidasRecurrenteF6(composite, anchoCm = null, altoCm = valueCm)
                    return
                }
                if (composite.template == TEMPLATE_ROUNDED) {
                    aplicarMedidasRoundedCorners(composite, anchoCm = null, altoCm = valueCm)
                    return
                }
                if (composite.template == TEMPLATE_F4) {
                    aplicarAlturaTotalRecurrenteF4(composite, valueCm)
                    return
                }
                val current = bounds.height().coerceAtLeast(1f)
                val scale = cmToPx(valueCm) / current
                val matrix = Matrix().apply { setScale(1f, scale, bounds.left, bounds.bottom) }
                composite.path.transform(matrix)
                transformCompositePoints(composite, matrix)
                composite.heightCm = valueCm
                refreshCompositeSides(composite)
            }
            CotaType.COMPOSITE_SIDE -> {
                aplicarNuevaCotaCompositeSide(composite, hit, valueCm)
            }
            CotaType.F5_DESARROLLO -> {
                if (composite.template == TEMPLATE_F5) {
                    aplicarDesarrolloRecurrenteF5(composite, valueCm)
                }
            }
            CotaType.F5_FLECHA -> {
                if (composite.template == TEMPLATE_F5) {
                    aplicarFlechaRecurrenteF5(composite, valueCm)
                }
            }
            CotaType.F6_RADIO -> {
                if (composite.template == TEMPLATE_F6) {
                    aplicarRadioRecurrenteF6(composite, valueCm)
                }
            }
            CotaType.ROUNDED_RADIUS -> {
                if (composite.template == TEMPLATE_ROUNDED) {
                    aplicarRadioRoundedCorner(composite, hit.sideIndex ?: return, valueCm)
                }
            }
            CotaType.RECT_TOP,
            CotaType.RECT_RIGHT,
            CotaType.RECT_BOTTOM,
            CotaType.RECT_LEFT,
            CotaType.DIAMETER,
            CotaType.LENGTH -> Unit
        }
    }

    private fun aplicarNuevaCotaCompositeSide(composite: Element.Composite, hit: CotaHit, valueCm: Float) {
        val contourIndex = hit.contourIndex ?: return
        val sideIndex = hit.sideIndex ?: return
        if (composite.template == TEMPLATE_F1 && contourIndex == 0) {
            aplicarNuevaCotaRecurrenteF1(composite, sideIndex, valueCm)
            return
        }
        // F2 (forma en "U"): los tres lados paralelos a la base (2 armes de arriba + fondo del
        // corte) suman la base. Editar la base reparte el cambio entre los tres; editar un
        // paralelo lo fija y reparte su diferencia entre los otros aún libres.
        if (composite.template == TEMPLATE_F2) {
            aplicarNuevaCotaRecurrenteF2(composite, contourIndex, sideIndex, valueCm)
            return
        }
        if (composite.template == TEMPLATE_F3) {
            aplicarNuevaCotaRecurrenteF3(composite, contourIndex, sideIndex, valueCm)
            return
        }
        if (composite.template == TEMPLATE_F4) {
            aplicarNuevaCotaRecurrenteF4(composite, contourIndex, sideIndex, valueCm)
            return
        }
        val contour = composite.contours.getOrNull(contourIndex) ?: return
        val n = contour.size
        if (n < 2 || sideIndex !in contour.indices) return

        // Si el propio lado está bloqueado, no se edita.
        if (ladoBloqueado(composite, contourIndex, sideIndex)) {
            Toast.makeText(context, "Ese lado está bloqueado 🔒", Toast.LENGTH_SHORT).show()
            return
        }
        val i0 = sideIndex
        val i1 = (sideIndex + 1) % n
        val p0 = contour[i0]; val p1 = contour[i1]
        val horizontal = abs(p1.x - p0.x) >= abs(p1.y - p0.y)
        val negativo = valueCm < 0f
        val largo = cmToPx(abs(valueCm)).coerceAtLeast(cmToPx(0.1f))

        // El SIGNO decide la dirección (absoluta): horizontal +→derecha / -→izquierda;
        // vertical +→arriba / -→abajo. Se mueve ese vértice y se ancla el opuesto.
        val idxMover: Int
        val idxAncla: Int
        if (horizontal) {
            val der = if (p0.x >= p1.x) i0 else i1
            val izq = if (der == i0) i1 else i0
            if (!negativo) { idxMover = der; idxAncla = izq } else { idxMover = izq; idxAncla = der }
        } else {
            val arriba = if (p0.y <= p1.y) i0 else i1
            val abajo = if (arriba == i0) i1 else i0
            if (!negativo) { idxMover = arriba; idxAncla = abajo } else { idxMover = abajo; idxAncla = arriba }
        }

        // Bloqueo: el vértice a mover está pinchado si su OTRO lado vecino está bloqueado.
        val moverPinchado =
            (idxMover == i0 && ladoBloqueado(composite, contourIndex, (sideIndex - 1 + n) % n)) ||
            (idxMover == i1 && ladoBloqueado(composite, contourIndex, (sideIndex + 1) % n))
        if (moverPinchado) {
            Toast.makeText(context, "Ese lado empuja hacia un lado bloqueado. Usa el signo contrario.", Toast.LENGTH_SHORT).show()
            return
        }

        val ancla = contour[idxAncla]
        val movil = contour[idxMover]
        if (horizontal) {
            val dir = if (movil.x >= ancla.x) 1f else -1f
            movil.x = ancla.x + dir * largo
            movil.y = ancla.y
        } else {
            val dir = if (movil.y >= ancla.y) 1f else -1f
            movil.y = ancla.y + dir * largo
            movil.x = ancla.x
        }
        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
        val bnds = boundsForElement(composite)
        composite.widthCm = pxToCm(bnds.width())
        composite.heightCm = pxToCm(bnds.height())
    }

    private fun crearRecurrenteF1(
        left: Float,
        top: Float,
        anchoCm: Float,
        altoCm: Float,
        corteAnchoCm: Float,
        corteAltoCm: Float
    ): Element.Composite {
        val totalW = cmToPx(anchoCm)
        val totalH = cmToPx(altoCm)
        val cutW = cmToPx(corteAnchoCm).coerceIn(cmToPx(5f), totalW - cmToPx(5f))
        val cutH = cmToPx(corteAltoCm).coerceIn(cmToPx(5f), totalH - cmToPx(5f))
        val contour = contourRecurrenteF1(left, top, totalW, totalH, cutW, cutH)
        val contours = mutableListOf(contour)
        return Element.Composite(
            path = pathFromContours(contours),
            widthCm = pxToCm(totalW),
            heightCm = pxToCm(totalH),
            contours = contours,
            sideCms = sideCmsForContours(contours),
            template = TEMPLATE_F1
        )
    }

    // F1 (rectángulo con esquina superior derecha cortada). Misma lógica de paralelos que F2, en
    // dos grupos independientes:
    //   - horizontal: paralelos {0 = tramo superior, 2 = fondo del corte}; completo = 4 (ancho).
    //   - vertical:   paralelos {1 = vertical del corte, 3 = lado bajo el corte}; completo = 5 (alto).
    // Editar el completo reparte proporcionalmente entre sus paralelos; editar un paralelo lo fija y
    // reparte al otro; una vez ambos fijados, el editado toma su valor y la figura se deforma.
    private fun aplicarNuevaCotaRecurrenteF1(composite: Element.Composite, sideIndex: Int, valueCm: Float) {
        val contour = composite.contours.firstOrNull()?.takeIf { it.size == 6 } ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val nuevo = cmToPx(kotlin.math.abs(valueCm)).coerceAtLeast(minSize)

        fun corto() = Toast.makeText(context, "Ese cambio dejaría un lado demasiado corto.", Toast.LENGTH_SHORT).show()
        fun fijado(lado: Int) {
            composite.bloqueados.add(ladoKey(0, lado))
            Toast.makeText(context, "Lado fijado 🔒", Toast.LENGTH_SHORT).show()
        }

        when (sideIndex) {
            0, 2 -> {
                val actuales = floatArrayOf(contour[1].x - contour[0].x, contour[3].x - contour[2].x)
                val idx = if (sideIndex == 0) 0 else 1
                val res = resolverParaleloEditado(actuales, idx, nuevo,
                    { i -> !ladoBloqueado(composite, 0, if (i == 0) 0 else 2) }, minSize) ?: return corto()
                aplicarHorizontalF1(contour, res.valores, res.irregular)
                fijado(sideIndex)
            }
            4 -> {
                val actuales = floatArrayOf(contour[1].x - contour[0].x, contour[3].x - contour[2].x)
                val v = distribuirProporcional(actuales, nuevo, minSize) ?: return corto()
                aplicarHorizontalF1(contour, v, irregular = false)
            }
            1, 3 -> {
                val actuales = floatArrayOf(contour[2].y - contour[1].y, contour[4].y - contour[3].y)
                val idx = if (sideIndex == 1) 0 else 1
                val res = resolverParaleloEditado(actuales, idx, nuevo,
                    { i -> !ladoBloqueado(composite, 0, if (i == 0) 1 else 3) }, minSize) ?: return corto()
                aplicarVerticalF1(contour, res.valores, res.irregular)
                fijado(sideIndex)
            }
            5 -> {
                val actuales = floatArrayOf(contour[2].y - contour[1].y, contour[4].y - contour[3].y)
                val v = distribuirProporcional(actuales, nuevo, minSize) ?: return corto()
                aplicarVerticalF1(contour, v, irregular = false)
            }
            else -> return
        }

        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
        val b = boundsForElement(composite)
        composite.widthCm = pxToCm(b.width())
        composite.heightCm = pxToCm(b.height())
    }

    // Recoloca los vértices horizontales de F1 (x). Con regular=true la base inferior sigue al total;
    // con regular=false la base queda fija y el costado derecho se inclina (deformación).
    private fun aplicarHorizontalF1(contour: MutableList<PointF>, valores: FloatArray, irregular: Boolean) {
        val left = contour[0].x
        val innerX = left + valores[0]        // fin del tramo superior / vertical del corte
        val outerX = innerX + valores[1]      // fin del fondo del corte
        contour[1].x = innerX
        contour[2].x = innerX
        contour[3].x = outerX
        if (!irregular) contour[4].x = outerX // base inferior derecha sigue al total
        // contour[0].x y contour[5].x (izquierda) se mantienen
    }

    // Recoloca los vértices verticales de F1 (y). Con regular=true la base inferior sigue al total;
    // con regular=false el lado izquierdo queda fijo y la base se inclina (deformación).
    private fun aplicarVerticalF1(contour: MutableList<PointF>, valores: FloatArray, irregular: Boolean) {
        val top = contour[0].y
        val cutY = top + valores[0]           // altura del fondo del corte
        val bottom = cutY + valores[1]
        contour[2].y = cutY
        contour[3].y = cutY
        contour[4].y = bottom                 // lado derecho inferior
        if (!irregular) contour[5].y = bottom // base izquierda sigue al total
        // contour[0].y y contour[1].y (borde superior) se mantienen
    }

    private fun contourRecurrenteF1(
        left: Float,
        top: Float,
        totalW: Float,
        totalH: Float,
        cutW: Float,
        cutH: Float
    ): MutableList<PointF> {
        val right = left + totalW
        val bottom = top + totalH
        val cutX = right - cutW
        val cutY = top + cutH
        return mutableListOf(
            PointF(left, top),
            PointF(cutX, top),
            PointF(cutX, cutY),
            PointF(right, cutY),
            PointF(right, bottom),
            PointF(left, bottom)
        )
    }

    private fun crearRecurrenteF2(
        left: Float,
        top: Float,
        anchoCm: Float,
        altoCm: Float,
        corteAnchoCm: Float,
        corteAltoCm: Float
    ): Element.Composite {
        val totalW = cmToPx(anchoCm)
        val totalH = cmToPx(altoCm)
        val cutW = cmToPx(corteAnchoCm).coerceIn(cmToPx(5f), totalW - cmToPx(10f))
        val cutH = cmToPx(corteAltoCm).coerceIn(cmToPx(5f), totalH - cmToPx(10f))
        val contours = contoursRecurrenteF2(left, top, totalW, totalH, cutW, cutH)
        return Element.Composite(
            path = pathFromContours(contours),
            widthCm = pxToCm(totalW),
            heightCm = pxToCm(totalH),
            contours = contours,
            sideCms = sideCmsForContours(contours),
            template = TEMPLATE_F2
        )
    }

    // Índices de lado (contorno de 8 puntos) de los tres lados paralelos a la base:
    //   0 = arme izquierdo (arriba)   2 = fondo del corte   4 = arme derecho (arriba)
    // Su suma es siempre igual a la base (lado 6). La base es el lado 6; los verticales
    // del corte son 1 y 3; los laterales (alto) son 5 y 7.
    private val F2_LADOS_PARALELOS = intArrayOf(0, 2, 4)

    private fun aplicarNuevaCotaRecurrenteF2(
        composite: Element.Composite,
        contourIndex: Int,
        sideIndex: Int,
        valueCm: Float
    ) {
        if (contourIndex != 0) return
        val contour = composite.contours.getOrNull(0)?.takeIf { it.size == 8 } ?: return
        // Mínimo mínimo: todos los lados aceptan medidas menores a 5; solo se evita 0/negativo.
        val minSize = cmToPx(0.1f)
        val top = contour[0].y
        val nuevo = cmToPx(kotlin.math.abs(valueCm)).coerceAtLeast(minSize)

        // Edición por mutación directa de vértices: así se conserva la geometría aunque la forma
        // haya quedado irregular (parte superior más ancha que la base).
        when (sideIndex) {
            0, 2, 4 -> if (!editarLadoParaleloF2(composite, sideIndex, nuevo, minSize)) return
            1, 3 -> {
                // Verticales del corte (se introducen hacia la forma), paralelos entre sí.
                // Si ninguno fue editado aún, ambos se mueven juntos (corte simétrico); una vez uno
                // fue editado, el otro puede tomar una profundidad distinta (fondo del corte inclinado).
                val totalH = contour[7].y - top
                val cutH = nuevo.coerceAtMost(totalH - minSize)
                val notchY = top + cutH
                val juntos = !ladoBloqueado(composite, 0, 1) && !ladoBloqueado(composite, 0, 3)
                if (sideIndex == 1 || juntos) contour[2].y = notchY
                if (sideIndex == 3 || juntos) contour[3].y = notchY
                composite.bloqueados.add(ladoKey(0, sideIndex))
            }
            5, 7 -> {
                // Alto (laterales): mueve la base inferior sin tocar la parte superior ni el corte.
                val cutH = contour[2].y - top
                val totalH = nuevo.coerceAtLeast(cutH + minSize)
                val bottomY = top + totalH
                contour[6].y = bottomY
                contour[7].y = bottomY
            }
            6 -> if (!editarBaseF2(composite, nuevo, minSize)) return
            else -> return
        }

        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
        val bnds = boundsForElement(composite)
        composite.widthCm = pxToCm(bnds.width())
        composite.heightCm = pxToCm(bnds.height())
    }

    // Edición de un lado paralelo a la base (armes de arriba o fondo del corte).
    //  - Si queda algún OTRO paralelo libre: fija el editado, reparte su diferencia entre los
    //    libres y la base inferior no cambia (la U se mantiene recta).
    //  - Si los otros dos ya están fijados: se permite igualmente; el editado toma su valor, los
    //    otros no se tocan y la base inferior queda FIJA, de modo que el total de arriba crece y
    //    la forma queda irregular (el lado editado sobresale, ese costado queda inclinado).
    // El lado editado siempre queda fijado. Un lado fijado sigue siendo editable directamente;
    // "fijado" solo significa que no se moverá al repartir la edición de otro lado.
    // ---- Lógica de paralelos reutilizable (extraída de F2, usada también por F1 y las demás) ----
    // Un grupo de lados "paralelos" normalmente suma un lado completo (ancho o alto). Al editar uno:
    //   - si queda algún otro paralelo libre, se reparte la diferencia entre los libres a partes
    //     iguales y el total no cambia (forma recta);
    //   - si los demás ya están fijados, el editado toma su valor y el total crece o mengua
    //     (forma deformada / irregular).
    private class ParaleloEdit(val valores: FloatArray, val irregular: Boolean)

    private fun resolverParaleloEditado(
        actuales: FloatArray,
        indice: Int,
        nuevo: Float,
        libre: (Int) -> Boolean,
        minSize: Float
    ): ParaleloEdit? {
        val valores = actuales.copyOf()
        val libres = actuales.indices.filter { it != indice && libre(it) }
        val irregular = libres.isEmpty()
        if (irregular) {
            valores[indice] = nuevo
        } else {
            val reparto = (nuevo - valores[indice]) / libres.size
            valores[indice] = nuevo
            libres.forEach { valores[it] = valores[it] - reparto }
        }
        return if (valores.any { it < minSize }) null else ParaleloEdit(valores, irregular)
    }

    // Al editar el lado "completo": los paralelos crecen proporcionalmente para sumar el nuevo total.
    private fun distribuirProporcional(actuales: FloatArray, nuevoTotal: Float, minSize: Float): FloatArray? {
        val suma = actuales.sum()
        if (suma <= 0f) return null
        val escala = nuevoTotal / suma
        val v = FloatArray(actuales.size) { actuales[it] * escala }
        return if (v.any { it < minSize }) null else v
    }

    private fun editarLadoParaleloF2(
        composite: Element.Composite,
        sideIndex: Int,
        nuevoPx: Float,
        minSize: Float
    ): Boolean {
        val contour = composite.contours[0]
        val actuales = floatArrayOf(
            contour[1].x - contour[0].x,   // paralelo 0 -> lado 0 (arme izq)
            contour[4].x - contour[1].x,   // paralelo 1 -> lado 2 (fondo del corte)
            contour[5].x - contour[4].x    // paralelo 2 -> lado 4 (arme der)
        )
        val p = when (sideIndex) { 0 -> 0; 2 -> 1; else -> 2 }
        val res = resolverParaleloEditado(actuales, p, nuevoPx,
            { i -> !ladoBloqueado(composite, 0, F2_LADOS_PARALELOS[i]) }, minSize)
        if (res == null) {
            Toast.makeText(context, "Ese cambio dejaría un lado demasiado corto.", Toast.LENGTH_SHORT).show()
            return false
        }
        val v = res.valores
        val bottomLeft = contour[7].x
        val bottomRight = contour[6].x
        if (!res.irregular) {
            // Reparto: la base inferior se mantiene, forma recta.
            reconstruirTopF2(contour, bottomLeft, v[0], v[1], v[2], regular = true)
        } else {
            // Todos los demás fijados: crece el total de arriba y la base inferior no cambia.
            val total = v[0] + v[1] + v[2]
            val leftTopX = when (sideIndex) {
                0 -> bottomRight - total                            // arme izq: sobresale a la izquierda
                4 -> bottomLeft                                     // arme der: sobresale a la derecha
                else -> (bottomLeft + bottomRight) / 2f - total / 2f // fondo: centrado
            }
            reconstruirTopF2(contour, leftTopX, v[0], v[1], v[2], regular = false)
        }
        composite.bloqueados.add(ladoKey(0, sideIndex))
        Toast.makeText(context, "Lado fijado 🔒", Toast.LENGTH_SHORT).show()
        return true
    }

    // Edición de la base: reparte el cambio entre los paralelos libres (o los tres si no hay
    // libres) y devuelve la forma a una U recta con ese ancho inferior.
    private fun editarBaseF2(composite: Element.Composite, nuevaBasePx: Float, minSize: Float): Boolean {
        val contour = composite.contours[0]
        val valores = floatArrayOf(
            contour[1].x - contour[0].x,
            contour[4].x - contour[1].x,
            contour[5].x - contour[4].x
        )
        val nuevaBase = nuevaBasePx.coerceAtLeast(minSize * 3f)
        val delta = nuevaBase - valores.sum()
        val libres = (0..2).filter { !ladoBloqueado(composite, 0, F2_LADOS_PARALELOS[it]) }
        val destino = if (libres.isEmpty()) listOf(0, 1, 2) else libres
        val reparto = delta / destino.size
        destino.forEach { valores[it] = valores[it] + reparto }
        if (valores.any { it < minSize }) {
            Toast.makeText(context, "Esa base dejaría un lado demasiado corto.", Toast.LENGTH_SHORT).show()
            return false
        }
        reconstruirTopF2(contour, contour[7].x, valores[0], valores[1], valores[2], regular = true)
        return true
    }

    // Recoloca la parte superior (vértices 0..5) a partir de un origen izquierdo y los tres anchos
    // paralelos, conservando la altura del corte (y de p2/p3). Con regular=true la base inferior
    // (p6/p7) sigue a la parte superior; con regular=false la base queda intacta (forma irregular).
    private fun reconstruirTopF2(
        contour: MutableList<PointF>,
        leftTopX: Float,
        armeIzq: Float,
        fondo: Float,
        armeDer: Float,
        regular: Boolean
    ) {
        val topY = contour[0].y
        val notchLeft = leftTopX + armeIzq
        val notchRight = notchLeft + fondo
        val topRight = notchRight + armeDer
        contour[0].x = leftTopX;   contour[0].y = topY
        contour[1].x = notchLeft;  contour[1].y = topY
        contour[2].x = notchLeft   // y (fondo del corte) se conserva
        contour[3].x = notchRight
        contour[4].x = notchRight; contour[4].y = topY
        contour[5].x = topRight;   contour[5].y = topY
        if (regular) {
            contour[7].x = leftTopX
            contour[6].x = topRight
        }
    }

    private fun contoursRecurrenteF2(
        left: Float,
        top: Float,
        totalW: Float,
        totalH: Float,
        cutW: Float,
        cutH: Float
    ): MutableList<MutableList<PointF>> {
        val armeIzq = ((totalW - cutW) / 2f).coerceAtLeast(cmToPx(5f))
        val armeDer = (totalW - cutW - armeIzq).coerceAtLeast(cmToPx(5f))
        return contoursRecurrenteF2Asimetrico(left, top, armeIzq, cutW, armeDer, cutH, totalH)
    }

    private fun contoursRecurrenteF2Asimetrico(
        left: Float,
        top: Float,
        armeIzq: Float,
        fondo: Float,
        armeDer: Float,
        cutH: Float,
        totalH: Float
    ): MutableList<MutableList<PointF>> {
        val notchLeft = left + armeIzq
        val notchRight = notchLeft + fondo
        val right = notchRight + armeDer
        val bottom = top + totalH
        val notchBottom = top + cutH
        return mutableListOf(
            mutableListOf(
                PointF(left, top),
                PointF(notchLeft, top),
                PointF(notchLeft, notchBottom),
                PointF(notchRight, notchBottom),
                PointF(notchRight, top),
                PointF(right, top),
                PointF(right, bottom),
                PointF(left, bottom)
            )
        )
    }

    private fun crearRecurrenteF3(
        left: Float,
        top: Float,
        anchoSuperiorCm: Float,
        anchoInferiorCm: Float,
        altoCm: Float
    ): Element.Composite {
        val topW = cmToPx(anchoSuperiorCm)
        val bottomW = cmToPx(anchoInferiorCm).coerceAtMost(topW - cmToPx(5f))
        val totalH = cmToPx(altoCm)
        val contours = mutableListOf(contourRecurrenteF3(left, top, topW, bottomW, totalH))
        return Element.Composite(
            path = pathFromContours(contours),
            widthCm = pxToCm(topW),
            heightCm = pxToCm(totalH),
            contours = contours,
            sideCms = sideCmsForContours(contours),
            template = TEMPLATE_F3
        )
    }

    private fun aplicarNuevaCotaRecurrenteF3(
        composite: Element.Composite,
        contourIndex: Int,
        sideIndex: Int,
        valueCm: Float
    ) {
        if (contourIndex != 0) return
        val contour = composite.contours.getOrNull(0)?.takeIf { it.size == 4 } ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val left = contour.minOf { it.x }
        val top = contour.minOf { it.y }
        var topW = (contour[1].x - contour[0].x).coerceAtLeast(minSize * 2f)
        var bottomW = (contour[2].x - contour[3].x).coerceAtLeast(minSize)
        var totalH = (contour[3].y - contour[0].y).coerceAtLeast(minSize)
        val nuevo = cmToPx(valueCm).coerceAtLeast(minSize)

        when (sideIndex) {
            0 -> topW = nuevo.coerceAtLeast(bottomW + minSize)
            1 -> {
                val diagonal = nuevo
                val diferencia = (topW - bottomW).coerceAtLeast(minSize)
                if (diagonal > diferencia + minSize) {
                    totalH = kotlin.math.sqrt((diagonal * diagonal - diferencia * diferencia).toDouble()).toFloat()
                } else {
                    totalH = minSize
                    val nuevaDiferencia = kotlin.math.sqrt((diagonal * diagonal - totalH * totalH).coerceAtLeast(minSize * minSize).toDouble()).toFloat()
                    bottomW = (topW - nuevaDiferencia)
                        .coerceAtLeast(minSize)
                }
            }
            2 -> bottomW = nuevo.coerceAtMost(topW - minSize)
            3 -> totalH = nuevo
            else -> return
        }

        val updated = contourRecurrenteF3(left, top, topW, bottomW, totalH)
        contour.clear()
        contour.addAll(updated)
        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
    }

    private fun contourRecurrenteF3(
        left: Float,
        top: Float,
        topW: Float,
        bottomW: Float,
        totalH: Float
    ): MutableList<PointF> {
        val bottom = top + totalH
        return mutableListOf(
            PointF(left, top),
            PointF(left + topW, top),
            PointF(left + bottomW, bottom),
            PointF(left, bottom)
        )
    }

    private fun crearRecurrenteF4(
        centralLeft: Float,
        top: Float,
        centralAnchoCm: Float,
        centralAltoCm: Float,
        agregadoAnchoCm: Float,
        agregadoAltoCm: Float,
        conIzquierdo: Boolean,
        conDerecho: Boolean
    ): Element.Composite {
        val centralW = cmToPx(centralAnchoCm)
        val centralH = cmToPx(centralAltoCm)
        val leftW = if (conIzquierdo) cmToPx(agregadoAnchoCm) else 0f
        val rightW = if (conDerecho) cmToPx(agregadoAnchoCm) else 0f
        val addonH = cmToPx(agregadoAltoCm).coerceAtMost(centralH - cmToPx(5f))
        val contours = mutableListOf(contourRecurrenteF4(centralLeft, top, centralW, centralH, leftW, rightW, addonH))
        return Element.Composite(
            path = pathFromContours(contours),
            widthCm = pxToCm(centralW + leftW + rightW),
            heightCm = pxToCm(centralH),
            contours = contours,
            sideCms = sideCmsForContours(contours),
            template = TEMPLATE_F4
        )
    }

    private fun aplicarNuevaCotaRecurrenteF4(
        composite: Element.Composite,
        contourIndex: Int,
        sideIndex: Int,
        valueCm: Float
    ) {
        if (contourIndex != 0) return
        val dims = dimsRecurrenteF4(composite.contours.getOrNull(0) ?: return) ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        var centralW = dims.centralW.coerceAtLeast(minSize)
        var centralH = dims.centralH.coerceAtLeast(minSize * 2f)
        var leftW = dims.leftW
        var rightW = dims.rightW
        var addonH = dims.addonH.coerceIn(minSize, centralH - minSize)
        val nuevo = cmToPx(valueCm).coerceAtLeast(minSize)

        when {
            leftW > 0f && rightW > 0f -> when (sideIndex) {
                0 -> centralW = (nuevo - leftW - rightW).coerceAtLeast(minSize)
                1, 7 -> addonH = nuevo.coerceAtMost(centralH - minSize)
                2 -> rightW = nuevo
                3, 5 -> centralH = (addonH + nuevo).coerceAtLeast(addonH + minSize)
                4 -> centralW = nuevo
                6 -> leftW = nuevo
                else -> return
            }
            leftW > 0f -> when (sideIndex) {
                0 -> centralW = (nuevo - leftW).coerceAtLeast(minSize)
                1 -> centralH = nuevo.coerceAtLeast(addonH + minSize)
                2 -> centralW = nuevo
                3 -> centralH = (addonH + nuevo).coerceAtLeast(addonH + minSize)
                4 -> leftW = nuevo
                5 -> addonH = nuevo.coerceAtMost(centralH - minSize)
                else -> return
            }
            rightW > 0f -> when (sideIndex) {
                0 -> centralW = (nuevo - rightW).coerceAtLeast(minSize)
                1 -> addonH = nuevo.coerceAtMost(centralH - minSize)
                2 -> rightW = nuevo
                3 -> centralH = (addonH + nuevo).coerceAtLeast(addonH + minSize)
                4 -> centralW = nuevo
                5 -> centralH = nuevo.coerceAtLeast(addonH + minSize)
                else -> return
            }
            else -> return
        }

        addonH = addonH.coerceAtMost(centralH - minSize)
        val contour = composite.contours.first()
        contour.clear()
        contour.addAll(contourRecurrenteF4(dims.centralLeft, dims.top, centralW, centralH, leftW, rightW, addonH))
        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
    }

    private fun aplicarAlturaTotalRecurrenteF4(composite: Element.Composite, valueCm: Float) {
        val dims = dimsRecurrenteF4(composite.contours.getOrNull(0) ?: return) ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val centralH = cmToPx(valueCm).coerceAtLeast(dims.addonH + minSize)
        val addonH = dims.addonH.coerceAtMost(centralH - minSize)
        val contour = composite.contours.first()
        contour.clear()
        contour.addAll(
            contourRecurrenteF4(
                centralLeft = dims.centralLeft,
                top = dims.top,
                centralW = dims.centralW,
                centralH = centralH,
                leftW = dims.leftW,
                rightW = dims.rightW,
                addonH = addonH
            )
        )
        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
    }

    private fun dimsRecurrenteF4(contour: List<PointF>): RecurrenteF4Dims? {
        if (contour.size == 8) {
            val centralLeft = contour[6].x
            val centralRight = contour[3].x
            return RecurrenteF4Dims(
                centralLeft = centralLeft,
                top = contour[0].y,
                centralW = centralRight - centralLeft,
                centralH = contour[5].y - contour[0].y,
                leftW = centralLeft - contour[0].x,
                rightW = contour[1].x - centralRight,
                addonH = contour[3].y - contour[0].y
            )
        }
        if (contour.size == 6 && contour[0].x < contour[5].x) {
            val centralLeft = contour[3].x
            val centralRight = contour[1].x
            return RecurrenteF4Dims(
                centralLeft = centralLeft,
                top = contour[0].y,
                centralW = centralRight - centralLeft,
                centralH = contour[3].y - contour[0].y,
                leftW = centralLeft - contour[0].x,
                rightW = 0f,
                addonH = contour[4].y - contour[0].y
            )
        }
        if (contour.size == 6) {
            val centralLeft = contour[5].x
            val centralRight = contour[3].x
            return RecurrenteF4Dims(
                centralLeft = centralLeft,
                top = contour[0].y,
                centralW = centralRight - centralLeft,
                centralH = contour[4].y - contour[0].y,
                leftW = 0f,
                rightW = contour[1].x - centralRight,
                addonH = contour[2].y - contour[0].y
            )
        }
        return null
    }

    private fun contourRecurrenteF4(
        centralLeft: Float,
        top: Float,
        centralW: Float,
        centralH: Float,
        leftW: Float,
        rightW: Float,
        addonH: Float
    ): MutableList<PointF> {
        val centralRight = centralLeft + centralW
        val bottom = top + centralH
        val shoulderY = top + addonH
        return when {
            leftW > 0f && rightW > 0f -> mutableListOf(
                PointF(centralLeft - leftW, top),
                PointF(centralRight + rightW, top),
                PointF(centralRight + rightW, shoulderY),
                PointF(centralRight, shoulderY),
                PointF(centralRight, bottom),
                PointF(centralLeft, bottom),
                PointF(centralLeft, shoulderY),
                PointF(centralLeft - leftW, shoulderY)
            )
            leftW > 0f -> mutableListOf(
                PointF(centralLeft - leftW, top),
                PointF(centralRight, top),
                PointF(centralRight, bottom),
                PointF(centralLeft, bottom),
                PointF(centralLeft, shoulderY),
                PointF(centralLeft - leftW, shoulderY)
            )
            rightW > 0f -> mutableListOf(
                PointF(centralLeft, top),
                PointF(centralRight + rightW, top),
                PointF(centralRight + rightW, shoulderY),
                PointF(centralRight, shoulderY),
                PointF(centralRight, bottom),
                PointF(centralLeft, bottom)
            )
            else -> rectContour(RectF(centralLeft, top, centralRight, bottom))
        }
    }

    private fun crearRecurrenteF5(
        left: Float,
        top: Float,
        anchoCm: Float,
        altoCm: Float
    ): Element.Composite {
        val rectW = cmToPx(anchoCm)
        val rectH = cmToPx(altoCm)
        val contour = rectContour(RectF(left, top, left + rectW, top + rectH))
        val contours = mutableListOf(contour)
        val dims = RecurrenteF5Dims(left, top, rectW, rectH, rectW / 4f)
        return Element.Composite(
            path = pathRecurrenteF5(dims),
            widthCm = pxToCm(rectW),
            heightCm = pxToCm(rectH),
            contours = contours,
            sideCms = sideCmsRecurrenteF5(dims),
            template = TEMPLATE_F5
        )
    }

    private fun aplicarMedidasRecurrenteF5(composite: Element.Composite, anchoCm: Float?, altoCm: Float?) {
        val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val newW = anchoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectW
        val newH = altoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectH
        val centerX = dims.left + dims.rectW / 2f
        val left = centerX - newW / 2f
        val newFlecha = dims.flecha.coerceAtMost(newW / 2f)
        val newDims = RecurrenteF5Dims(left, dims.top, newW, newH, newFlecha)
        val contour = composite.contours.first()
        contour.clear()
        contour.addAll(rectContour(RectF(left, dims.top, left + newW, dims.top + newH)))
        composite.path.set(pathRecurrenteF5(newDims))
        composite.widthCm = pxToCm(newW)
        composite.heightCm = pxToCm(newH)
        setSideCmsRecurrenteF5(composite, newDims)
    }

    private fun aplicarDesarrolloRecurrenteF5(composite: Element.Composite, desarrolloCm: Float) {
        val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val desired = cmToPx(desarrolloCm).coerceAtLeast(cmToPx(1f))
        val minFlecha = cmToPx(1f)
        val maxFlecha = (dims.rectW / 2f).coerceAtLeast(minFlecha)
        var low = minFlecha
        var high = maxFlecha
        repeat(32) {
            val mid = (low + high) / 2f
            if (longitudMediaElipsePx(dims.rectW, mid) < desired) low = mid else high = mid
        }
        aplicarFlechaRecurrenteF5(composite, pxToCm((low + high) / 2f))
    }

    private fun aplicarFlechaRecurrenteF5(composite: Element.Composite, flechaCm: Float) {
        val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val newFlecha = cmToPx(flechaCm).coerceIn(cmToPx(1f), dims.rectW / 2f)
        val newDims = dims.copy(flecha = newFlecha)
        composite.path.set(pathRecurrenteF5(newDims))
        setSideCmsRecurrenteF5(composite, newDims)
    }

    private fun dimsRecurrenteF5(
        contour: List<PointF>,
        sideCms: List<List<Float>> = emptyList()
    ): RecurrenteF5Dims? {
        if (contour.size < 4) return null
        val left = contour.minOf { it.x }
        val top = contour.minOf { it.y }
        val right = contour.maxOf { it.x }
        val bottom = contour.maxOf { it.y }
        if (right <= left || bottom <= top) return null
        val rectW = right - left
        val flechaCm = sideCms.getOrNull(0)?.getOrNull(2)?.takeIf { it > 0f }
        val flecha = flechaCm?.let { cmToPx(it) } ?: rectW / 4f
        return RecurrenteF5Dims(left, top, rectW, bottom - top, flecha.coerceIn(cmToPx(1f), rectW / 2f))
    }

    private fun pathRecurrenteF5(dims: RecurrenteF5Dims): Path {
        val rect = RectF(dims.left, dims.top, dims.left + dims.rectW, dims.top + dims.rectH)
        val ellipseH = dims.flecha * 2f
        val topOval = RectF(dims.left, dims.top - ellipseH / 2f, dims.left + dims.rectW, dims.top + ellipseH / 2f)
        val bottomOval = RectF(dims.left, dims.top + dims.rectH - ellipseH / 2f, dims.left + dims.rectW, dims.top + dims.rectH + ellipseH / 2f)
        val result = Path().apply { addRect(rect, Path.Direction.CW) }
        val topPath = Path().apply { addOval(topOval, Path.Direction.CW) }
        val bottomPath = Path().apply { addOval(bottomOval, Path.Direction.CW) }
        result.op(topPath, Path.Op.UNION)
        result.op(bottomPath, Path.Op.DIFFERENCE)
        return result
    }

    private fun longitudMediaElipsePx(width: Float, flecha: Float): Float {
        val a = width / 2f
        val b = flecha
        val full = Math.PI * (3.0 * (a + b) - kotlin.math.sqrt(((3f * a + b) * (a + 3f * b)).toDouble()))
        return (full / 2.0).toFloat()
    }

    private fun sideCmsRecurrenteF5(dims: RecurrenteF5Dims): MutableList<MutableList<Float>> {
        return mutableListOf(
            mutableListOf(
                pxToCm(dims.rectW),
                pxToCm(dims.rectH),
                pxToCm(dims.flecha)
            )
        )
    }

    private fun setSideCmsRecurrenteF5(composite: Element.Composite, dims: RecurrenteF5Dims) {
        composite.sideCms.clear()
        composite.sideCms.addAll(sideCmsRecurrenteF5(dims))
    }

    private fun crearRecurrenteF6(
        left: Float,
        top: Float,
        anchoCm: Float,
        altoCm: Float,
        radioCm: Float
    ): Element.Composite {
        val rectW = cmToPx(anchoCm)
        val rectH = cmToPx(altoCm)
        val radius = cmToPx(radioCm).coerceIn(cmToPx(1f), minOf(rectW, rectH) / 2f)
        val contour = rectContour(RectF(left, top, left + rectW, top + rectH))
        val contours = mutableListOf(contour)
        val dims = RecurrenteF6Dims(left, top, rectW, rectH, radius)
        return Element.Composite(
            path = pathRecurrenteF6(dims),
            widthCm = pxToCm(rectW),
            heightCm = pxToCm(rectH),
            contours = contours,
            sideCms = sideCmsRecurrenteF6(dims),
            template = TEMPLATE_F6
        )
    }

    private fun aplicarMedidasRecurrenteF6(composite: Element.Composite, anchoCm: Float?, altoCm: Float?) {
        val dims = dimsRecurrenteF6(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val newW = anchoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectW
        val newH = altoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectH
        val centerX = dims.left + dims.rectW / 2f
        val left = centerX - newW / 2f
        val newRadius = dims.radius.coerceAtMost(minOf(newW, newH) / 2f)
        setRecurrenteF6(composite, RecurrenteF6Dims(left, dims.top, newW, newH, newRadius))
    }

    private fun aplicarRadioRecurrenteF6(composite: Element.Composite, radioCm: Float) {
        val dims = dimsRecurrenteF6(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val radius = cmToPx(radioCm).coerceIn(cmToPx(1f), minOf(dims.rectW, dims.rectH) / 2f)
        setRecurrenteF6(composite, dims.copy(radius = radius))
    }

    private fun setRecurrenteF6(composite: Element.Composite, dims: RecurrenteF6Dims) {
        val contour = composite.contours.first()
        contour.clear()
        contour.addAll(rectContour(RectF(dims.left, dims.top, dims.left + dims.rectW, dims.top + dims.rectH)))
        composite.path.set(pathRecurrenteF6(dims))
        composite.widthCm = pxToCm(dims.rectW)
        composite.heightCm = pxToCm(dims.rectH)
        setSideCmsRecurrenteF6(composite, dims)
    }

    private fun dimsRecurrenteF6(
        contour: List<PointF>,
        sideCms: List<List<Float>> = emptyList()
    ): RecurrenteF6Dims? {
        if (contour.size < 4) return null
        val left = contour.minOf { it.x }
        val top = contour.minOf { it.y }
        val right = contour.maxOf { it.x }
        val bottom = contour.maxOf { it.y }
        if (right <= left || bottom <= top) return null
        val rectW = right - left
        val rectH = bottom - top
        val radiusCm = sideCms.getOrNull(0)?.getOrNull(2)?.takeIf { it > 0f }
        val radius = radiusCm?.let { cmToPx(it) } ?: minOf(rectW, rectH) * 0.18f
        return RecurrenteF6Dims(left, top, rectW, rectH, radius.coerceIn(cmToPx(1f), minOf(rectW, rectH) / 2f))
    }

    private fun pathRecurrenteF6(dims: RecurrenteF6Dims): Path {
        return Path().apply {
            addRoundRect(
                RectF(dims.left, dims.top, dims.left + dims.rectW, dims.top + dims.rectH),
                dims.radius,
                dims.radius,
                Path.Direction.CW
            )
        }
    }

    private fun sideCmsRecurrenteF6(dims: RecurrenteF6Dims): MutableList<MutableList<Float>> {
        return mutableListOf(
            mutableListOf(
                pxToCm(dims.rectW),
                pxToCm(dims.rectH),
                pxToCm(dims.radius)
            )
        )
    }

    private fun setSideCmsRecurrenteF6(composite: Element.Composite, dims: RecurrenteF6Dims) {
        composite.sideCms.clear()
        composite.sideCms.addAll(sideCmsRecurrenteF6(dims))
    }

    private fun roundedDimsFromElement(element: Element): RoundedCornerDims? {
        return when (element) {
            is Element.Shape -> {
                if (element.tool != Tool.RECTANGLE) return null
                val bounds = boundsForElement(element)
                RoundedCornerDims(bounds.left, bounds.top, bounds.width(), bounds.height(), mutableListOf(0f, 0f, 0f, 0f))
            }
            is Element.Composite -> when (element.template) {
                TEMPLATE_ROUNDED -> dimsRoundedCorners(element.contours.firstOrNull() ?: return null, element.sideCms)
                TEMPLATE_F6 -> {
                    val dims = dimsRecurrenteF6(element.contours.firstOrNull() ?: return null, element.sideCms) ?: return null
                    RoundedCornerDims(
                        dims.left,
                        dims.top,
                        dims.rectW,
                        dims.rectH,
                        mutableListOf(dims.radius, dims.radius, dims.radius, dims.radius)
                    )
                }
                else -> {
                    val contour = element.contours.firstOrNull()?.takeIf { it.size == 4 } ?: return null
                    val bounds = RectF()
                    pathFromContours(listOf(contour)).computeBounds(bounds, true)
                    RoundedCornerDims(bounds.left, bounds.top, bounds.width(), bounds.height(), mutableListOf(0f, 0f, 0f, 0f))
                }
            }
            is Element.Freehand -> null
            is Element.Group -> null
            is Element.TextLabel -> null
            is Element.InfoBox -> null
            is Element.Symbol -> null
        }
    }

    private fun crearRectanguloConEsquinaRedondeada(rect: RectF, corner: Int, radioCm: Float): Element.Composite {
        return crearRectanguloConEsquinasRedondeadas(rect, setOf(corner), radioCm)
    }

    private fun crearRectanguloConEsquinasRedondeadas(rect: RectF, corners: Set<Int>, radioCm: Float): Element.Composite {
        val maxRadius = minOf(rect.width(), rect.height()) / 2f
        val radii = mutableListOf(0f, 0f, 0f, 0f)
        val radius = cmToPx(radioCm).coerceIn(cmToPx(1f), maxRadius)
        corners.filter { it in 0..3 }.forEach { corner -> radii[corner] = radius }
        val dims = RoundedCornerDims(rect.left, rect.top, rect.width(), rect.height(), radii)
        val contours = mutableListOf(rectContour(rect))
        return Element.Composite(
            path = pathRoundedCorners(dims),
            widthCm = pxToCm(rect.width()),
            heightCm = pxToCm(rect.height()),
            contours = contours,
            sideCms = sideCmsRoundedCorners(dims),
            template = TEMPLATE_ROUNDED
        )
    }

    private fun aplicarMedidasRoundedCorners(composite: Element.Composite, anchoCm: Float?, altoCm: Float?) {
        val dims = dimsRoundedCorners(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val newW = anchoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectW
        val newH = altoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectH
        val centerX = dims.left + dims.rectW / 2f
        val left = centerX - newW / 2f
        val maxRadius = minOf(newW, newH) / 2f
        val updated = RoundedCornerDims(
            left = left,
            top = dims.top,
            rectW = newW,
            rectH = newH,
            radii = dims.radii.map { it.coerceAtMost(maxRadius) }.toMutableList()
        )
        setRoundedCorners(composite, updated)
    }

    private fun aplicarRadioRoundedCorner(composite: Element.Composite, corner: Int, radioCm: Float) {
        if (corner !in 0..3) return
        val dims = dimsRoundedCorners(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val maxRadius = minOf(dims.rectW, dims.rectH) / 2f
        dims.radii[corner] = cmToPx(radioCm).coerceIn(cmToPx(1f), maxRadius)
        setRoundedCorners(composite, dims)
    }

    private fun setRoundedCorners(composite: Element.Composite, dims: RoundedCornerDims) {
        val contour = composite.contours.first()
        contour.clear()
        contour.addAll(rectContour(RectF(dims.left, dims.top, dims.left + dims.rectW, dims.top + dims.rectH)))
        composite.path.set(pathRoundedCorners(dims))
        composite.widthCm = pxToCm(dims.rectW)
        composite.heightCm = pxToCm(dims.rectH)
        setSideCmsRoundedCorners(composite, dims)
    }

    private fun dimsRoundedCorners(
        contour: List<PointF>,
        sideCms: List<List<Float>> = emptyList()
    ): RoundedCornerDims? {
        if (contour.size < 4) return null
        val left = contour.minOf { it.x }
        val top = contour.minOf { it.y }
        val right = contour.maxOf { it.x }
        val bottom = contour.maxOf { it.y }
        if (right <= left || bottom <= top) return null
        val rectW = right - left
        val rectH = bottom - top
        val maxRadius = minOf(rectW, rectH) / 2f
        val stored = sideCms.getOrNull(0).orEmpty()
        val radii = (0..3).map { i ->
            stored.getOrNull(i + 2)?.let { cmToPx(it) }?.coerceIn(0f, maxRadius) ?: 0f
        }.toMutableList()
        return RoundedCornerDims(left, top, rectW, rectH, radii)
    }

    private fun pathRoundedCorners(dims: RoundedCornerDims): Path {
        val r = dims.radii.map { it.coerceIn(0f, minOf(dims.rectW, dims.rectH) / 2f) }
        val left = dims.left
        val top = dims.top
        val right = dims.left + dims.rectW
        val bottom = dims.top + dims.rectH
        return Path().apply {
            moveTo(left + r[0], top)
            lineTo(right - r[1], top)
            if (r[1] > 0f) quadTo(right, top, right, top + r[1]) else lineTo(right, top)
            lineTo(right, bottom - r[2])
            if (r[2] > 0f) quadTo(right, bottom, right - r[2], bottom) else lineTo(right, bottom)
            lineTo(left + r[3], bottom)
            if (r[3] > 0f) quadTo(left, bottom, left, bottom - r[3]) else lineTo(left, bottom)
            lineTo(left, top + r[0])
            if (r[0] > 0f) quadTo(left, top, left + r[0], top) else lineTo(left, top)
            close()
        }
    }

    private fun sideCmsRoundedCorners(dims: RoundedCornerDims): MutableList<MutableList<Float>> {
        return mutableListOf(
            mutableListOf(
                pxToCm(dims.rectW),
                pxToCm(dims.rectH),
                pxToCm(dims.radii[0]),
                pxToCm(dims.radii[1]),
                pxToCm(dims.radii[2]),
                pxToCm(dims.radii[3])
            )
        )
    }

    private fun setSideCmsRoundedCorners(composite: Element.Composite, dims: RoundedCornerDims) {
        composite.sideCms.clear()
        composite.sideCms.addAll(sideCmsRoundedCorners(dims))
    }

    private fun rebuildCompositePath(composite: Element.Composite) {
        if (composite.template == TEMPLATE_F5) {
            val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
            composite.path.set(pathRecurrenteF5(dims))
            composite.widthCm = pxToCm(dims.rectW)
            composite.heightCm = pxToCm(dims.rectH)
            setSideCmsRecurrenteF5(composite, dims)
            return
        }
        if (composite.template == TEMPLATE_F6) {
            val dims = dimsRecurrenteF6(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
            composite.path.set(pathRecurrenteF6(dims))
            composite.widthCm = pxToCm(dims.rectW)
            composite.heightCm = pxToCm(dims.rectH)
            setSideCmsRecurrenteF6(composite, dims)
            return
        }
        if (composite.template == TEMPLATE_ROUNDED) {
            val dims = dimsRoundedCorners(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
            composite.path.set(pathRoundedCorners(dims))
            composite.widthCm = pxToCm(dims.rectW)
            composite.heightCm = pxToCm(dims.rectH)
            setSideCmsRoundedCorners(composite, dims)
            return
        }
        composite.path.reset()
        for (contour in composite.contours) {
            if (contour.size < 2) continue
            composite.path.moveTo(contour.first().x, contour.first().y)
            for (i in 1 until contour.size) {
                composite.path.lineTo(contour[i].x, contour[i].y)
            }
            composite.path.close()
        }
        val bounds = boundsForElement(composite)
        composite.widthCm = pxToCm(bounds.width())
        composite.heightCm = pxToCm(bounds.height())
    }

    private fun transformCompositePoints(composite: Element.Composite, matrix: Matrix) {
        val values = FloatArray(2)
        composite.contours.forEach { contour ->
            contour.forEach { point ->
                values[0] = point.x
                values[1] = point.y
                matrix.mapPoints(values)
                point.x = values[0]
                point.y = values[1]
            }
        }
    }

    private fun refreshCompositeSides(composite: Element.Composite) {
        composite.sideCms.clear()
        composite.sideCms.addAll(sideCmsForContours(composite.contours))
    }

    private fun actualizarBoundsRectangulo(shape: Element.Shape) {
        shape.rect.set(
            minOf(shape.topLeft.x, shape.topRight.x, shape.bottomRight.x, shape.bottomLeft.x),
            minOf(shape.topLeft.y, shape.topRight.y, shape.bottomRight.y, shape.bottomLeft.y),
            maxOf(shape.topLeft.x, shape.topRight.x, shape.bottomRight.x, shape.bottomLeft.x),
            maxOf(shape.topLeft.y, shape.topRight.y, shape.bottomRight.y, shape.bottomLeft.y)
        )
        shape.widthCm = maxOf(shape.topCm, shape.bottomCm)
        shape.heightCm = maxOf(shape.leftCm, shape.rightCm)
        shape.start.x = shape.rect.left
        shape.start.y = shape.rect.top
        shape.end.x = shape.rect.right
        shape.end.y = shape.rect.bottom
    }

    private fun fijarBaseRectangulo(shape: Element.Shape) {
        val baseY = maxOf(shape.bottomLeft.y, shape.bottomRight.y)
        shape.bottomLeft.y = baseY
        shape.bottomRight.y = baseY
    }

    private fun actualizarTrazo(x: Float, y: Float) {
        when (herramienta) {
            Tool.NONE -> Unit
            Tool.FREEHAND -> trazoActual.lineTo(x, y)
            Tool.RECTANGLE -> {
                trazoActual.reset()
                trazoActual.addRect(rectFrom(startPoint, x, y), Path.Direction.CW)
            }
            Tool.TRIANGLE -> {
                val r = rectFrom(startPoint, x, y)
                trazoActual.reset()
                trazoActual.moveTo(r.centerX(), r.top)
                trazoActual.lineTo(r.right, r.bottom)
                trazoActual.lineTo(r.left, r.bottom)
                trazoActual.close()
            }
            Tool.CIRCLE -> {
                trazoActual.reset()
                trazoActual.addOval(rectFrom(startPoint, x, y), Path.Direction.CW)
            }
            Tool.LINE, Tool.ORTHO_LINE -> {
                val endPoint = puntoFinalHerramienta(x, y)
                trazoActual.reset()
                trazoActual.moveTo(startPoint.x, startPoint.y)
                trazoActual.lineTo(endPoint.x, endPoint.y)
            }
            Tool.TEXT, Tool.SELECT -> Unit
        }
    }

    private fun trazoValido(x: Float, y: Float): Boolean {
        if (herramienta == Tool.NONE) return false
        if (herramienta == Tool.FREEHAND) return true
        if (herramienta == Tool.TEXT || herramienta == Tool.SELECT) return false
        return abs(x - startPoint.x) >= 12f || abs(y - startPoint.y) >= 12f
    }

    private fun rectFrom(start: PointF, x: Float, y: Float): RectF {
        return RectF(
            minOf(start.x, x),
            minOf(start.y, y),
            maxOf(start.x, x),
            maxOf(start.y, y)
        )
    }

    private fun puntoFinalHerramienta(x: Float, y: Float): PointF {
        return when (herramienta) {
            Tool.LINE -> snapLibre(PointF(x, y)) ?: PointF(x, y)
            Tool.ORTHO_LINE -> {
                val axis = if (abs(x - startPoint.x) >= abs(y - startPoint.y)) Axis.HORIZONTAL else Axis.VERTICAL
                val ortho = when (axis) {
                    Axis.HORIZONTAL -> PointF(x, startPoint.y)
                    Axis.VERTICAL -> PointF(startPoint.x, y)
                }
                snapOrtogonal(ortho, axis) ?: ortho
            }
            Tool.TEXT, Tool.SELECT -> PointF(x, y)
            else -> PointF(x, y)
        }
    }

    private fun snapLibre(point: PointF): PointF? {
        var mejor: PointF? = null
        var mejorDist = snapThresholdPx()
        for ((a, b) in segmentosExistentes()) {
            val candidate = puntoMasCercanoEnSegmento(point, a, b)
            val dist = distancia(point, candidate)
            if (dist < mejorDist) {
                mejorDist = dist
                mejor = candidate
            }
        }
        return mejor
    }

    private fun snapOrtogonal(point: PointF, axis: Axis): PointF? {
        var mejor: PointF? = null
        var mejorDist = snapThresholdPx()
        for ((a, b) in segmentosExistentes()) {
            val candidate = when (axis) {
                Axis.HORIZONTAL -> interseccionConHorizontal(point.y, a, b)
                Axis.VERTICAL -> interseccionConVertical(point.x, a, b)
            } ?: puntoMasCercanoEnSegmento(point, a, b).let {
                when (axis) {
                    Axis.HORIZONTAL -> PointF(it.x, point.y)
                    Axis.VERTICAL -> PointF(point.x, it.y)
                }
            }
            val dist = distancia(point, candidate)
            if (dist < mejorDist) {
                mejorDist = dist
                mejor = candidate
            }
        }
        return mejor
    }

    private fun aplicarImanASeleccion() {
        if (selectedIndices.isEmpty()) return
        val exclude = selectedIndices.toSet()
        val segmentosDestino = segmentosExistentes(exclude)
        if (segmentosDestino.isEmpty()) return

        val deltaLados = deltaAlineacionPorLados(segmentosDestino)
        if (deltaLados != null) {
            selectedIndices.forEach { translateElement(it, deltaLados.x, deltaLados.y) }
            return
        }

        var mejorDelta: PointF? = null
        var mejorDist = snapThresholdPx()
        for (p in puntosDeElementos(selectedIndices)) {
            for ((a, b) in segmentosDestino) {
                val candidate = puntoMasCercanoEnSegmento(p, a, b)
                val dist = distancia(p, candidate)
                if (dist < mejorDist) {
                    mejorDist = dist
                    mejorDelta = PointF(candidate.x - p.x, candidate.y - p.y)
                }
            }
        }
        mejorDelta?.let { delta ->
            selectedIndices.forEach { translateElement(it, delta.x, delta.y) }
        }
    }

    private fun deltaAlineacionPorLados(segmentosDestino: List<Pair<PointF, PointF>>): PointF? {
        val bounds = boundsForIndices(selectedIndices) ?: return null
        val threshold = snapThresholdPx()
        var mejorDx: Float? = null
        var mejorDy: Float? = null
        var mejorDxDist = threshold
        var mejorDyDist = threshold

        for ((a, b) in segmentosDestino) {
            if (abs(a.x - b.x) < 1f && rangosSeCruzan(bounds.top, bounds.bottom, minOf(a.y, b.y), maxOf(a.y, b.y), threshold)) {
                val destinoX = (a.x + b.x) / 2f
                listOf(bounds.left, bounds.right).forEach { origenX ->
                    val delta = destinoX - origenX
                    val dist = abs(delta)
                    if (dist < mejorDxDist) {
                        mejorDxDist = dist
                        mejorDx = delta
                    }
                }
            }
            if (abs(a.y - b.y) < 1f && rangosSeCruzan(bounds.left, bounds.right, minOf(a.x, b.x), maxOf(a.x, b.x), threshold)) {
                val destinoY = (a.y + b.y) / 2f
                listOf(bounds.top, bounds.bottom).forEach { origenY ->
                    val delta = destinoY - origenY
                    val dist = abs(delta)
                    if (dist < mejorDyDist) {
                        mejorDyDist = dist
                        mejorDy = delta
                    }
                }
            }
        }

        if (mejorDx == null && mejorDy == null) return null
        return PointF(mejorDx ?: 0f, mejorDy ?: 0f)
    }

    private fun boundsForIndices(indices: List<Int>): RectF? {
        var union: RectF? = null
        for (index in indices) {
            val element = elementos.getOrNull(index) ?: continue
            val bounds = boundsForElement(element)
            if (union == null) {
                union = RectF(bounds)
            } else {
                union.union(bounds)
            }
        }
        return union
    }

    private fun rangosSeCruzan(aStart: Float, aEnd: Float, bStart: Float, bEnd: Float, tolerancia: Float): Boolean {
        return maxOf(aStart, bStart) <= minOf(aEnd, bEnd) + tolerancia
    }

    private fun puntosDeElementos(indices: List<Int>): List<PointF> {
        return indices.flatMap { index ->
            val element = elementos.getOrNull(index) ?: return@flatMap emptyList()
            puntosDeElemento(element)
        }
    }

    private fun puntosDeElemento(element: Element): List<PointF> {
        return when (element) {
            is Element.Freehand -> puntosBounds(boundsForElement(element))
            is Element.Composite -> element.contours.flatten().ifEmpty { puntosBounds(boundsForElement(element)) }
            is Element.Group -> element.children.flatMap { puntosDeElemento(it) }.ifEmpty { puntosBounds(boundsForElement(element)) }
            is Element.TextLabel -> puntosBounds(boundsForElement(element))
            is Element.InfoBox -> puntosBounds(element.rect)
            is Element.Symbol -> puntosBounds(element.rect)
            is Element.Shape -> when (element.tool) {
                Tool.RECTANGLE -> listOf(element.topLeft, element.topRight, element.bottomRight, element.bottomLeft)
                Tool.TRIANGLE -> listOf(
                    PointF(element.rect.centerX(), element.rect.top),
                    PointF(element.rect.right, element.rect.bottom),
                    PointF(element.rect.left, element.rect.bottom)
                )
                Tool.CIRCLE -> puntosBounds(element.rect)
                Tool.LINE, Tool.ORTHO_LINE -> listOf(element.start, element.end)
                Tool.NONE, Tool.FREEHAND, Tool.TEXT, Tool.SELECT -> puntosBounds(boundsForElement(element))
            }
        }.map { PointF(it.x, it.y) }
    }

    private fun puntosBounds(rect: RectF): List<PointF> {
        return listOf(
            PointF(rect.left, rect.top),
            PointF(rect.right, rect.top),
            PointF(rect.right, rect.bottom),
            PointF(rect.left, rect.bottom),
            PointF(rect.centerX(), rect.centerY())
        )
    }

    private fun segmentosExistentes(excludeIndices: Set<Int> = emptySet()): List<Pair<PointF, PointF>> {
        val segmentos = mutableListOf<Pair<PointF, PointF>>()
        for ((index, element) in elementos.withIndex()) {
            if (index in excludeIndices) continue
            when (element) {
                is Element.Freehand -> segmentos += segmentosBounds(boundsForElement(element))
                is Element.TextLabel -> segmentos += segmentosBounds(boundsForElement(element))
                is Element.InfoBox -> segmentos += segmentosBounds(element.rect)
                is Element.Symbol -> segmentos += segmentosBounds(element.rect)
                is Element.Composite -> {
                    val compositeSegments = segmentosContours(element.contours)
                    segmentos += compositeSegments.ifEmpty { segmentosBounds(boundsForElement(element)) }
                }
                is Element.Group -> element.children.forEach { child -> segmentos += segmentosDeElemento(child) }
                is Element.Shape -> when (element.tool) {
                    Tool.RECTANGLE -> {
                        segmentos += element.topLeft to element.topRight
                        segmentos += element.topRight to element.bottomRight
                        segmentos += element.bottomRight to element.bottomLeft
                        segmentos += element.bottomLeft to element.topLeft
                    }
                    Tool.TRIANGLE -> {
                        val top = PointF(element.rect.centerX(), element.rect.top)
                        val right = PointF(element.rect.right, element.rect.bottom)
                        val left = PointF(element.rect.left, element.rect.bottom)
                        segmentos += top to right
                        segmentos += right to left
                        segmentos += left to top
                    }
                    Tool.LINE, Tool.ORTHO_LINE -> {
                        segmentos += element.start to element.end
                    }
                    Tool.CIRCLE -> {
                        segmentos += segmentosOvalo(element.rect)
                    }
                    Tool.NONE, Tool.FREEHAND, Tool.TEXT, Tool.SELECT -> Unit
                }
            }
        }
        return segmentos
    }

    private fun segmentosDeElemento(element: Element): List<Pair<PointF, PointF>> {
        return when (element) {
            is Element.Freehand -> segmentosBounds(boundsForElement(element))
            is Element.TextLabel -> segmentosBounds(boundsForElement(element))
            is Element.InfoBox -> segmentosBounds(element.rect)
            is Element.Symbol -> segmentosBounds(element.rect)
            is Element.Composite -> segmentosContours(element.contours).ifEmpty { segmentosBounds(boundsForElement(element)) }
            is Element.Group -> element.children.flatMap { segmentosDeElemento(it) }
            is Element.Shape -> when (element.tool) {
                Tool.RECTANGLE -> listOf(
                    element.topLeft to element.topRight,
                    element.topRight to element.bottomRight,
                    element.bottomRight to element.bottomLeft,
                    element.bottomLeft to element.topLeft
                )
                Tool.TRIANGLE -> {
                    val top = PointF(element.rect.centerX(), element.rect.top)
                    val right = PointF(element.rect.right, element.rect.bottom)
                    val left = PointF(element.rect.left, element.rect.bottom)
                    listOf(top to right, right to left, left to top)
                }
                Tool.LINE, Tool.ORTHO_LINE -> listOf(element.start to element.end)
                Tool.CIRCLE -> segmentosOvalo(element.rect)
                Tool.NONE, Tool.FREEHAND, Tool.TEXT, Tool.SELECT -> emptyList()
            }
        }
    }

    private fun segmentosBounds(rect: RectF): List<Pair<PointF, PointF>> {
        val tl = PointF(rect.left, rect.top)
        val tr = PointF(rect.right, rect.top)
        val br = PointF(rect.right, rect.bottom)
        val bl = PointF(rect.left, rect.bottom)
        return listOf(tl to tr, tr to br, br to bl, bl to tl)
    }

    private fun segmentosContours(contours: List<List<PointF>>): List<Pair<PointF, PointF>> {
        return contours.flatMap { contour ->
            if (contour.size < 2) {
                emptyList()
            } else {
                contour.indices.map { i -> contour[i] to contour[(i + 1) % contour.size] }
            }
        }
    }

    private fun segmentosOvalo(rect: RectF): List<Pair<PointF, PointF>> {
        val puntos = (0 until 16).map { i ->
            val a = (Math.PI * 2.0 * i) / 16.0
            PointF(
                rect.centerX() + (cos(a) * rect.width() / 2f).toFloat(),
                rect.centerY() + (sin(a) * rect.height() / 2f).toFloat()
            )
        }
        return puntos.indices.map { i -> puntos[i] to puntos[(i + 1) % puntos.size] }
    }

    private fun puntoMasCercanoEnSegmento(p: PointF, a: PointF, b: PointF): PointF {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val len2 = (dx * dx) + (dy * dy)
        if (len2 <= 0.001f) return PointF(a.x, a.y)
        val t = (((p.x - a.x) * dx) + ((p.y - a.y) * dy)) / len2
        val tc = t.coerceIn(0f, 1f)
        return PointF(a.x + (dx * tc), a.y + (dy * tc))
    }

    private fun interseccionConHorizontal(y: Float, a: PointF, b: PointF): PointF? {
        val dy = b.y - a.y
        if (abs(dy) < 0.001f) {
            if (abs(y - a.y) > snapThresholdPx()) return null
            return PointF(((a.x + b.x) / 2f).coerceIn(minOf(a.x, b.x), maxOf(a.x, b.x)), y)
        }
        val t = (y - a.y) / dy
        if (t !in 0f..1f) return null
        return PointF(a.x + ((b.x - a.x) * t), y)
    }

    private fun interseccionConVertical(x: Float, a: PointF, b: PointF): PointF? {
        val dx = b.x - a.x
        if (abs(dx) < 0.001f) {
            if (abs(x - a.x) > snapThresholdPx()) return null
            return PointF(x, ((a.y + b.y) / 2f).coerceIn(minOf(a.y, b.y), maxOf(a.y, b.y)))
        }
        val t = (x - a.x) / dx
        if (t !in 0f..1f) return null
        return PointF(x, a.y + ((b.y - a.y) * t))
    }

    private fun distancia(a: PointF, b: PointF): Float {
        return hypot((a.x - b.x).toDouble(), (a.y - b.y).toDouble()).toFloat()
    }

    private fun snapThresholdPx(): Float = 26f * resources.displayMetrics.density

    private fun pxToCm(px: Float): Float {
        val dp = px / resources.displayMetrics.density
        // Redondear, no truncar: truncando, un ida y vuelta cm → px → cm perdía hasta 1 mm en cada
        // edición (143.6 volvía como 143.5) y la medida se iba corriendo sola.
        return Math.round(dp * 10f) / 10f
    }

    private fun cmToPx(cm: Float): Float {
        return cm * resources.displayMetrics.density
    }

    private fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
    }

    private fun formatCm(value: Float): String {
        return if (value % 1f == 0f) {
            value.toInt().toString()
        } else {
            String.format(Locale.US, "%.1f", value)
        }
    }

    private fun formatAngle(value: Float): String {
        return if (abs(value % 1f) < 0.05f) {
            value.toInt().toString()
        } else {
            String.format(Locale.US, "%.1f", value)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        val bitmap = fondo ?: return
        val scale = minOf(width / bitmap.width.toFloat(), height / bitmap.height.toFloat())
        val drawW = bitmap.width * scale
        val drawH = bitmap.height * scale
        val left = (width - drawW) / 2f
        val top = (height - drawH) / 2f
        canvas.drawBitmap(bitmap, null, RectF(left, top, left + drawW, top + drawH), fondoPaint)
    }
}
