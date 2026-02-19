package crystal.crystal.optimizadores

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class ArcDrawingView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val glassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#40ADD8E6") // Celeste translÃºcido (vidrio)
        style = Paint.Style.FILL
    }

    private val edgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val dimensionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private var d = 0f; private var c = 0f; private var f = 0f; private var l = 0f

    fun updateData(des: Float, cue: Float, fle: Float, lar: Float) {
        this.d = des; this.c = cue; this.f = fle; this.l = lar
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (c <= 0 || f <= 0) return

        // --- Escala y ProyecciÃ³n ---
        val padding = 24f
        val labelExtraTop = 60f
        val labelExtraBottom = 60f
        val labelExtraRight = 80f

        val isoDepth = l * 0.5f
        val modelWidth = c + isoDepth * 0.866f
        val modelHeight = (2f * f) + isoDepth * 0.5f

        val availableW = (width - padding * 2f - labelExtraRight).coerceAtLeast(1f)
        val availableH = (height - padding * 2f - labelExtraTop - labelExtraBottom).coerceAtLeast(1f)

        val scale = min(availableW / modelWidth, availableH / modelHeight)
        val scC = c * scale
        val scF = f * scale
        val scL = l * scale * 0.5f // Profundidad isomÃ©trica

        // Punto de origen (Frente Izquierda)
        val dx = scL * 0.866f
        val dy = -scL * 0.5f

        val x0 = padding
        val y0 = padding + labelExtraTop + (2f * scF) + (-dy)

        // DefiniciÃ³n de RectÃ¡ngulos para los Arcos (Frontal y Trasero)
        val rectFront = RectF(x0, y0 - scF * 2, x0 + scC, y0)
        val rectBack = RectF(x0 + dx, y0 + dy - scF * 2, x0 + scC + dx, y0 + dy)

        // 1. Dibujar la cara superior (superficie curva)
        val glassPath = Path().apply {
            moveTo(x0, y0)
            arcTo(rectFront, 180f, 180f)
            lineTo(x0 + scC + dx, y0 + dy)
            arcTo(rectBack, 0f, -180f)
            close()
        }
        canvas.drawPath(glassPath, glassPaint)

        // 2. Dibujar Aristas (Bordes visibles)
        canvas.drawArc(rectFront, 180f, 180f, false, edgePaint) // Arco frontal
        canvas.drawArc(rectBack, 180f, 180f, false, edgePaint)  // Arco trasero
        canvas.drawLine(x0, y0, x0 + dx, y0 + dy, edgePaint)    // Lateral izq
        canvas.drawLine(x0 + scC, y0, x0 + scC + dx, y0 + dy, edgePaint) // Lateral der

        // 3. COTAS TÃ‰CNICAS
        // Cuerda (LÃ­nea base frontal)
        canvas.drawLine(x0, y0 + 20f, x0 + scC, y0 + 20f, dimensionPaint)
        canvas.drawText("Cuerda: $c", x0 + scC/2, y0 + 50f, textPaint)

        // Largo (Profundidad)
        canvas.drawLine(x0 + scC + 10f, y0, x0 + scC + dx + 10f, y0 + dy, dimensionPaint)
        canvas.drawText("L: $l", x0 + scC + dx + 30f, y0 + dy + 30f, textPaint)

        // Flecha (Altura mÃ¡xima)
        canvas.drawLine(x0 + scC/2, y0 - scF, x0 + scC/2, y0, dimensionPaint)
        canvas.drawText("F: $f", x0 + scC/2 - 40f, y0 - scF/2, textPaint)

        // Desarrollo (Cota curva sobre el cristal)
        val rectLabel = RectF(x0, y0 - scF * 2 - 30f, x0 + scC, y0 - 30f)
        canvas.drawArc(rectLabel, 190f, 160f, false, dimensionPaint)
        canvas.drawText("Desarrollo: $d", x0 + scC/2, y0 - scF * 2 - 50f, textPaint)
    }
}
