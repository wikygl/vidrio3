package crystal.crystal.Diseno.estructurada

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import crystal.crystal.R

class VistaEstructurada @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var parametros: ParametrosEstructurada = ParametrosEstructurada()

    private val colorAluminio = ContextCompat.getColor(context, R.color.aluminio)
    private val pPerfil = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorAluminio
        style = Paint.Style.FILL
    }
    private val pLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val pVidrio = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#DCEAF1")
        style = Paint.Style.FILL
    }
    private val pVidrioReflejo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#80FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 1.2f
    }
    private val pTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }
    fun aplicar(nuevo: ParametrosEstructurada) {
        parametros = nuevo.normalizar()
        invalidate()
    }

    fun obtenerParametros(): ParametrosEstructurada = parametros

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (width <= 0 || height <= 0) return

        val params = parametros
        val margenH = 12f
        val margenV = 12f
        val anchoDisp = (width - margenH * 2f).coerceAtLeast(50f)
        val altoDisp = (height - margenV * 2f).coerceAtLeast(50f)
        val aspecto = params.anchoCm / params.altoCm
        val (w, h) = if (aspecto > anchoDisp / altoDisp) {
            anchoDisp to anchoDisp / aspecto
        } else {
            altoDisp * aspecto to altoDisp
        }
        val left = (width - w) / 2f
        val top = (height - h) / 2f
        val right = left + w
        val bottom = top + h
        val transomY = top + h * (params.altoTransomCm / params.altoCm)

        canvas.drawRect(left, top, right, bottom, pVidrio)

        val divT = params.divisionesTransom.coerceAtLeast(1)
        val divC = params.divisionesCuerpo.coerceAtLeast(1)

        val grosorMarco = (minOf(w, h) * 0.04f).coerceIn(5f, 14f)
        val grosorMontante = grosorMarco * 0.7f
        val grosorTransom = grosorMarco * 0.85f

        dibujarReflejos(canvas, left, top, right, transomY, divT)
        dibujarReflejos(canvas, left, transomY, right, bottom, divC)

        dibujarEtiquetasPanos(canvas, "T", divT, left, top, right, transomY)
        dibujarEtiquetasPanos(canvas, "P", divC, left, transomY, right, bottom)

        if (divT > 1) {
            val paso = w / divT.toFloat()
            for (i in 1 until divT) {
                val cx = left + paso * i
                dibujarPerfil(canvas, cx - grosorMontante / 2f, top, cx + grosorMontante / 2f, transomY)
            }
        }
        if (divC > 1) {
            val paso = w / divC.toFloat()
            for (i in 1 until divC) {
                val cx = left + paso * i
                dibujarPerfil(canvas, cx - grosorMontante / 2f, transomY, cx + grosorMontante / 2f, bottom)
            }
        }

        dibujarPerfil(canvas, left, transomY - grosorTransom / 2f, right, transomY + grosorTransom / 2f)

        dibujarPerfil(canvas, left, top, left + grosorMarco, bottom)
        dibujarPerfil(canvas, right - grosorMarco, top, right, bottom)
        dibujarPerfil(canvas, left, top, right, top + grosorMarco)
        dibujarPerfil(canvas, left, bottom - grosorMarco, right, bottom)
    }

    private fun dibujarPerfil(canvas: Canvas, x1: Float, y1: Float, x2: Float, y2: Float) {
        val r = RectF(x1, y1, x2, y2)
        canvas.drawRect(r, pPerfil)
        canvas.drawRect(r, pLinea)
    }

    private fun dibujarReflejos(
        canvas: Canvas,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        divisiones: Int
    ) {
        val pasos = divisiones.coerceAtLeast(1)
        val ancho = (x2 - x1) / pasos
        for (i in 0 until pasos) {
            val px1 = x1 + ancho * i + 6f
            val px2 = x1 + ancho * (i + 1) - 6f
            val py1 = y1 + 6f
            val py2 = y2 - 6f
            canvas.drawLine(px1, py2, (px1 + px2) / 2f, py1, pVidrioReflejo)
        }
    }

    private fun dibujarEtiquetasPanos(
        canvas: Canvas,
        prefijo: String,
        divisiones: Int,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float
    ) {
        val pasos = divisiones.coerceAtLeast(1)
        val paso = (x2 - x1) / pasos
        val cy = (y1 + y2) / 2f + 10f
        for (i in 0 until pasos) {
            val cx = x1 + paso * (i + 0.5f)
            canvas.drawText("$prefijo${i + 1}", cx, cy, pTexto)
        }
    }

    fun exportarBitmap(): Bitmap {
        val bmp = Bitmap.createBitmap(
            width.coerceAtLeast(1),
            height.coerceAtLeast(1),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)
        draw(canvas)
        return bmp
    }
}