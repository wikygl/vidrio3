package crystal.crystal.taller

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.RequiresApi
import java.lang.Float.min

class RectanguloView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private var width: Float = 0f
    private var height: Float = 0f
    var maxWidth: Float = 0f
    var maxHeight: Float = 0f
    private var scale: Float = 1f

    @SuppressLint("DrawAllocation")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calcular el espesor del borde en píxeles
        val bordeThickness = context.resources.displayMetrics.density

        // Calcular el factor de escala relativo en función del lado más largo del rectángulo y el espacio disponible
        val scale = min(width / maxWidth, height / maxHeight)

        // Calcular las dimensiones del rectángulo escalado
        val rectWidth = maxWidth * scale
        val rectHeight = maxHeight * scale

        // Calcular las coordenadas del rectángulo escalado dentro del espacio asignado
        val rectanguloLeft = (width - rectWidth) / 2
        val rectanguloTop = (height - rectHeight) / 2
        val rectanguloRight = rectanguloLeft + rectWidth
        val rectanguloBottom = rectanguloTop + rectHeight

        // Configurar el estilo de la pintura
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = bordeThickness
        paint.color = 0xFF000000.toInt() // Color negro (ARGB)

        // Dibujar el rectángulo escalado en el lienzo
        canvas.drawRect(
            rectanguloLeft,
            rectanguloTop,
            rectanguloRight,
            rectanguloBottom,
            paint
        )

        // Mostrar las cotas de ancho y alto
        val textPaint = Paint().apply {
            color = 0xFF000000.toInt()
            textSize = spToPx(10f)
            textAlign = Paint.Align.CENTER
        }

        // Cota de ancho
        val cotaAnchoY = rectanguloBottom + dpToPx(20f)
        canvas.drawText(df(maxWidth), (rectanguloLeft + rectanguloRight) / 2, cotaAnchoY - dpToPx(10f), textPaint)

        // Cota de alto
        val cotaAltoX = rectanguloRight + dpToPx(18f)
        canvas.drawText(df(maxHeight), cotaAltoX - dpToPx(10f), (rectanguloTop + rectanguloBottom) / 2, textPaint)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setHeight(alto: Float) {
        this.height = alto
        updateScale()
        invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setWidth(ancho: Float) {
        this.width = ancho
        updateScale()
        invalidate()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateScale() {
        if (maxWidth > 0 && maxHeight > 0) {
            val scaleX = width / maxWidth
            val scaleY = height / maxHeight
            scale = min(scaleX, scaleY)
        }
    }

    private fun df(defo: Float): String {
        val resultado = if ("$defo".endsWith(".0")) {
            "$defo".replace(".0", "")
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }

    private fun spToPx(sp: Float): Float {
        return sp * context.resources.displayMetrics.scaledDensity
    }
}
