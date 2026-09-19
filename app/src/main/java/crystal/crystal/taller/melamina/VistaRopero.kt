package crystal.crystal.taller.melamina

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

/**
 * El ropero dibujado en una vista propia (la ficha de la calculadora, la perspectiva del apunte):
 * lo encuadra, lo pinta con [RoperoDibujo] y le pone las cotas del hueco y de los cuerpos.
 * Tocar un cuerpo avisa por [alTocarCuerpo] con su índice.
 */
class VistaRopero @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var ropero: Ropero = Ropero()
        set(value) { field = value; invalidate() }

    var mostrarPuertas: Boolean = false
        set(value) { field = value; invalidate() }

    var en3d: Boolean = false
        set(value) { field = value; invalidate() }

    /** El cuerpo resaltado (el último tocado), o -1. */
    var cuerpoResaltado: Int = -1
        set(value) { field = value; invalidate() }

    var alTocarCuerpo: ((Int) -> Unit)? = null

    private val dp = resources.displayMetrics.density
    private val dibujo = RoperoDibujo(dp)
    private val pCota = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); style = Paint.Style.STROKE; strokeWidth = 1f * dp }
    private val pTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); textSize = 11f * dp; textAlign = Paint.Align.CENTER }

    // La escala y el origen del último dibujo, para saber qué se tocó.
    private var escala = 1f
    private var origenX = 0f
    private var origenY = 0f

    private fun x(cm: Float) = origenX + cm * escala
    private fun y(cm: Float) = origenY - cm * escala

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r = ropero
        val margen = 34f * dp
        val fondoPapelX = if (en3d) r.fondoCm * dibujo.oblicuoX else 0f
        val fondoPapelY = if (en3d) -r.fondoCm * dibujo.oblicuoY else 0f
        escala = min(
            (width - 2 * margen) / (r.anchoCm + fondoPapelX),
            (height - 2 * margen) / (r.altoCm + fondoPapelY)
        )
        origenX = (width - (r.anchoCm + fondoPapelX) * escala) / 2f
        origenY = height - (height - (r.altoCm + fondoPapelY) * escala) / 2f
        dibujo.dibujar(canvas, r, origenX, origenY, escala, mostrarPuertas, en3d, cuerpoResaltado)
        if (!en3d) dibujarCotas(canvas)
    }

    private fun dibujarCotas(canvas: Canvas) {
        val r = ropero
        val sep = 14f * dp
        // Ancho total, arriba.
        val yA = y(r.altoCm) - sep
        canvas.drawLine(x(0f), yA, x(r.anchoCm), yA, pCota)
        canvas.drawLine(x(0f), yA - 4 * dp, x(0f), yA + 4 * dp, pCota)
        canvas.drawLine(x(r.anchoCm), yA - 4 * dp, x(r.anchoCm), yA + 4 * dp, pCota)
        canvas.drawText(fmt(r.anchoCm), x(r.anchoCm / 2f), yA - 3 * dp, pTexto)
        // Alto, a la izquierda.
        val xA = x(0f) - sep
        canvas.drawLine(xA, y(0f), xA, y(r.altoCm), pCota)
        canvas.drawLine(xA - 4 * dp, y(0f), xA + 4 * dp, y(0f), pCota)
        canvas.drawLine(xA - 4 * dp, y(r.altoCm), xA + 4 * dp, y(r.altoCm), pCota)
        canvas.save()
        canvas.rotate(-90f, xA - 4 * dp, y(r.altoCm / 2f))
        canvas.drawText(fmt(r.altoCm), xA - 4 * dp, y(r.altoCm / 2f), pTexto)
        canvas.restore()
        // Cada cuerpo, abajo.
        val yB = y(0f) + sep
        var cx = r.espesorCm
        r.cuerpos.forEach { c ->
            canvas.drawLine(x(cx), yB, x(cx + c.anchoCm), yB, pCota)
            canvas.drawLine(x(cx), yB - 4 * dp, x(cx), yB + 4 * dp, pCota)
            canvas.drawLine(x(cx + c.anchoCm), yB - 4 * dp, x(cx + c.anchoCm), yB + 4 * dp, pCota)
            canvas.drawText(fmt(c.anchoCm), x(cx + c.anchoCm / 2f), yB + 11 * dp, pTexto)
            cx += c.anchoCm + r.espesorCm
        }
    }

    private fun fmt(v: Float): String =
        if (v == v.toInt().toFloat()) v.toInt().toString() else String.format(java.util.Locale.US, "%.1f", v)

    private var xInicio = 0f
    private var yInicio = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { xInicio = event.x; yInicio = event.y; return true }
            MotionEvent.ACTION_UP -> {
                if (kotlin.math.hypot(event.x - xInicio, event.y - yInicio) > 12 * dp) return true
                cuerpoEn(event.x)?.let { alTocarCuerpo?.invoke(it) }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /** Qué cuerpo cae bajo esa x del lienzo; null fuera del mueble. */
    fun cuerpoEn(px: Float): Int? {
        val cm = (px - origenX) / escala
        var cx = ropero.espesorCm
        ropero.cuerpos.forEachIndexed { i, c ->
            if (cm >= cx - ropero.espesorCm / 2f && cm <= cx + c.anchoCm + ropero.espesorCm / 2f) return i
            cx += c.anchoCm + ropero.espesorCm
        }
        return null
    }
}
