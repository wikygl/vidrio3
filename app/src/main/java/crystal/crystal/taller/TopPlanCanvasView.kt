package crystal.crystal.taller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.hypot

class TopPlanCanvasView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    data class Selection(
        val side: RectanglesDrawingView.AletaSide,
        val direction: RectanglesDrawingView.AletaDirection,
        val horizontalDominant: Boolean
    )

    var onSelectionChanged: ((Selection) -> Unit)? = null

    private val panelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(235, 248, 250, 252)
    }
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.rgb(120, 144, 156)
    }
    private val axisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.rgb(96, 125, 139)
    }
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(55, 71, 79)
    }
    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.rgb(46, 125, 50)
        strokeCap = Paint.Cap.ROUND
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(33, 33, 33)
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    private var vectorX = 0f
    private var vectorY = 0f
    private var hasVector = false
    private var currentSelection: Selection? = null

    fun setSelection(side: RectanglesDrawingView.AletaSide, direction: RectanglesDrawingView.AletaDirection) {
        val radius = 0.34f
        vectorX = if (side == RectanglesDrawingView.AletaSide.RIGHT) radius else -radius
        vectorY = if (direction == RectanglesDrawingView.AletaDirection.INTERIOR) radius else -radius
        hasVector = true
        currentSelection = Selection(side = side, direction = direction, horizontalDominant = false)
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val cx = width * 0.5f
        val cy = height * 0.5f
        val dx = event.x - cx
        val dy = event.y - cy
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val maxLen = (minOf(width, height) * 0.42f).coerceAtLeast(1f)
                val len = hypot(dx.toDouble(), dy.toDouble()).toFloat()
                if (len > 8f) {
                    val k = (maxLen / len).coerceAtMost(1f)
                    vectorX = dx * k
                    vectorY = dy * k
                    hasVector = true
                    invalidate()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                val len = hypot(dx.toDouble(), dy.toDouble()).toFloat()
                if (len > 8f) {
                    val side = if (dx >= 0f) {
                        RectanglesDrawingView.AletaSide.RIGHT
                    } else {
                        RectanglesDrawingView.AletaSide.LEFT
                    }
                    val direction = if (dy >= 0f) {
                        RectanglesDrawingView.AletaDirection.INTERIOR
                    } else {
                        RectanglesDrawingView.AletaDirection.EXTERIOR
                    }
                    val horizontalDominant = abs(dx) >= abs(dy)
                    val next = Selection(side = side, direction = direction, horizontalDominant = horizontalDominant)
                    currentSelection = next
                    onSelectionChanged?.invoke(next)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        canvas.drawRoundRect(3f, 3f, w - 3f, h - 3f, 18f, 18f, panelPaint)
        canvas.drawRoundRect(3f, 3f, w - 3f, h - 3f, 18f, 18f, borderPaint)

        val cx = w * 0.5f
        val cy = h * 0.5f
        canvas.drawLine(cx, 18f, cx, h - 18f, axisPaint)
        canvas.drawLine(18f, cy, w - 18f, cy, axisPaint)
        canvas.drawCircle(cx, cy, 7f, centerPaint)

        canvas.drawText("Exterior", cx, 30f, textPaint)
        canvas.drawText("Interior", cx, h - 14f, textPaint)
        canvas.drawText("Izquierda", 70f, cy - 10f, textPaint)
        canvas.drawText("Derecha", w - 70f, cy - 10f, textPaint)

        if (hasVector) {
            val tx = cx + vectorX
            val ty = cy + vectorY
            canvas.drawLine(cx, cy, tx, ty, arrowPaint)
            drawArrowHead(canvas, cx, cy, tx, ty)
        }
    }

    private fun drawArrowHead(canvas: Canvas, x0: Float, y0: Float, x1: Float, y1: Float) {
        val dx = x1 - x0
        val dy = y1 - y0
        val len = hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(0.001f)
        val ux = dx / len
        val uy = dy / len
        val nx = -uy
        val ny = ux
        val size = 18f
        val p1x = x1 - (ux * size) + (nx * size * 0.55f)
        val p1y = y1 - (uy * size) + (ny * size * 0.55f)
        val p2x = x1 - (ux * size) - (nx * size * 0.55f)
        val p2y = y1 - (uy * size) - (ny * size * 0.55f)
        val head = Path().apply {
            moveTo(x1, y1)
            lineTo(p1x, p1y)
            lineTo(p2x, p2y)
            close()
        }
        canvas.drawPath(head, arrowPaint)
    }
}
