package crystal.crystal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Recuadro para firmar con el dedo. Captura SOLO una imagen del trazo (evidencia de aceptación,
 * no es firma digital legal ni biometría).
 */
class FirmaView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val trazo = Path()
    private val pincel = Paint().apply {
        color = Color.BLACK
        strokeWidth = 6f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }
    private var hayFirma = false

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.WHITE)
        canvas.drawPath(trazo, pincel)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Evitar que el ScrollView robe el gesto mientras se firma.
                parent?.requestDisallowInterceptTouchEvent(true)
                trazo.moveTo(event.x, event.y)
                hayFirma = true
            }
            MotionEvent.ACTION_MOVE -> trazo.lineTo(event.x, event.y)
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parent?.requestDisallowInterceptTouchEvent(false)
                performClick()
            }
        }
        invalidate()
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun limpiar() {
        trazo.reset()
        hayFirma = false
        invalidate()
    }

    fun tieneFirma(): Boolean = hayFirma

    /** Exporta el trazo como imagen (fondo blanco). */
    fun exportarBitmap(): Bitmap {
        val bmp = Bitmap.createBitmap(
            width.coerceAtLeast(1), height.coerceAtLeast(1), Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)
        canvas.drawPath(trazo, pincel)
        return bmp
    }
}
