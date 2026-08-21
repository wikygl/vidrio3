package crystal.crystal.Diseno

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView

// ImageView con zoom (pellizco) y paneo (arrastre) por matriz. Ajusta la imagen al entrar
// (fit-center) y limita el desplazamiento para que no se salga del lienzo.
@SuppressLint("ClickableViewAccessibility")
class PlanoZoomView(context: Context) : AppCompatImageView(context) {

    private val m = Matrix()
    private val valores = FloatArray(9)
    private var fitScale = 1f
    private var maxScale = 8f
    private val last = PointF()
    private var arrastrando = false

    private val scaleDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(d: ScaleGestureDetector): Boolean {
                val actual = escalaActual()
                var f = d.scaleFactor
                if (actual * f < fitScale) f = fitScale / actual
                if (actual * f > maxScale) f = maxScale / actual
                m.postScale(f, f, d.focusX, d.focusY)
                corregir(); imageMatrix = m
                return true
            }
        })

    init {
        scaleType = ScaleType.MATRIX
        setOnTouchListener { _, e ->
            scaleDetector.onTouchEvent(e)
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> { last.set(e.x, e.y); arrastrando = true }
                MotionEvent.ACTION_POINTER_DOWN -> arrastrando = false
                MotionEvent.ACTION_MOVE -> if (arrastrando && !scaleDetector.isInProgress) {
                    m.postTranslate(e.x - last.x, e.y - last.y)
                    corregir(); imageMatrix = m
                    last.set(e.x, e.y)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> arrastrando = false
            }
            parent?.requestDisallowInterceptTouchEvent(true)
            true
        }
    }

    fun setBitmapAjustado(bm: Bitmap) {
        setImageBitmap(bm)
        post { ajustar() }
    }

    private fun escalaActual(): Float { m.getValues(valores); return valores[Matrix.MSCALE_X] }

    private fun ajustar() {
        val d = drawable ?: return
        val vw = width.toFloat(); val vh = height.toFloat()
        val dw = d.intrinsicWidth.toFloat(); val dh = d.intrinsicHeight.toFloat()
        if (vw <= 0f || vh <= 0f || dw <= 0f || dh <= 0f) return
        fitScale = minOf(vw / dw, vh / dh) * 0.9f   // deja un margen de separación del lienzo
        maxScale = fitScale * 8f
        m.reset()
        m.postScale(fitScale, fitScale)
        m.postTranslate((vw - dw * fitScale) / 2f, (vh - dh * fitScale) / 2f)
        imageMatrix = m
    }

    private fun corregir() {
        val d = drawable ?: return
        m.getValues(valores)
        val scale = valores[Matrix.MSCALE_X]
        val tx = valores[Matrix.MTRANS_X]; val ty = valores[Matrix.MTRANS_Y]
        val cw = d.intrinsicWidth * scale; val ch = d.intrinsicHeight * scale
        val vw = width.toFloat(); val vh = height.toFloat()
        var dx = 0f; var dy = 0f
        if (cw <= vw) dx = (vw - cw) / 2f - tx
        else if (tx > 0) dx = -tx else if (tx < vw - cw) dx = vw - cw - tx
        if (ch <= vh) dy = (vh - ch) / 2f - ty
        else if (ty > 0) dy = -ty else if (ty < vh - ch) dy = vh - ch - ty
        m.postTranslate(dx, dy)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (drawable != null) ajustar()
    }
}
