package crystal.crystal.optimizadores.planchas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View

class PlanchaVistaCorte @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var plancha: PlanchaOptimizada? = null
    private var zoom = 1.0f
    private var anchoBasePx = 0

    val paleta = listOf(
        Color.parseColor("#4DD0E1"),
        Color.parseColor("#81C784"),
        Color.parseColor("#FFB74D"),
        Color.parseColor("#F48FB1"),
        Color.parseColor("#CE93D8"),
        Color.parseColor("#80CBC4"),
        Color.parseColor("#FFCC80"),
        Color.parseColor("#EF9A9A"),
        Color.parseColor("#B39DDB"),
        Color.parseColor("#80DEEA"),
        Color.parseColor("#A5D6A7"),
        Color.parseColor("#FFF176"),
    )

    private val pintaFondo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ECEFF1")
        style = Paint.Style.FILL
    }
    private val pintaBordePlancha = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#37474F")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val pintaRelleno = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val pintaBordeCorte = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#455A64")
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
    }
    private val pintaNumero = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1A237E")
        style = Paint.Style.FILL
        isFakeBoldText = true
        typeface = Typeface.DEFAULT_BOLD
    }
    private val pintaSombraNum = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL_AND_STROKE
        isFakeBoldText = true
        typeface = Typeface.DEFAULT_BOLD
    }
    private val pintaEtiq = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#546E7A")
        textSize = 24f
    }

    fun setPlancha(p: PlanchaOptimizada) {
        plancha = p
        requestLayout()
        invalidate()
    }

    fun setZoom(nuevoZoom: Float, nuevoAnchoBasePx: Int) {
        zoom = nuevoZoom.coerceIn(0.75f, 2.0f)
        anchoBasePx = nuevoAnchoBasePx.coerceAtLeast(1)
        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val p = plancha
        if (p == null || p.anchoMm <= 0 || p.altoMm <= 0) {
            setMeasuredDimension(0, 0)
            return
        }
        val anchoMedido = MeasureSpec.getSize(widthMeasureSpec)
        val anchoBase = if (anchoBasePx > 0) anchoBasePx else anchoMedido
        val w = (anchoBase * zoom).toInt().coerceAtLeast(1)
        val ratio = p.altoMm.toFloat() / p.anchoMm.toFloat()
        val planchaH = (w * ratio).toInt().coerceIn(80, (w * 2.5f).toInt())
        // Altura total: plancha + etiqueta inferior
        setMeasuredDimension(w, planchaH + 40)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val p = plancha ?: return

        val pad = 12f
        val etiqH = 40f
        val disponW = (width - pad * 2).coerceAtLeast(1f)
        val disponH = (height - pad - etiqH).coerceAtLeast(1f)

        val escalaX = disponW / p.anchoMm
        val escalaY = disponH / p.altoMm
        val escala = minOf(escalaX, escalaY)

        val pw = p.anchoMm * escala
        val ph = p.altoMm * escala
        val ox = pad + (disponW - pw) / 2f
        val oy = pad

        // Fondo plancha
        canvas.drawRect(ox, oy, ox + pw, oy + ph, pintaFondo)

        // Cortes con número
        p.cortes.forEachIndexed { i, corte ->
            pintaRelleno.color = paleta[i % paleta.size]
            val cx = ox + corte.xMm * escala
            val cy = oy + corte.yMm * escala
            val cw = corte.anchoMm * escala
            val ch = corte.altoMm * escala

            canvas.drawRect(cx, cy, cx + cw, cy + ch, pintaRelleno)
            canvas.drawRect(cx, cy, cx + cw, cy + ch, pintaBordeCorte)

            dibujarNumero(canvas, i + 1, cx, cy, cw, ch)
        }

        // Borde plancha encima
        canvas.drawRect(ox, oy, ox + pw, oy + ph, pintaBordePlancha)

        // Etiqueta de medidas
        val anchoC = fmt(p.anchoMm / 10f)
        val altoC = fmt(p.altoMm / 10f)
        pintaEtiq.textSize = 24f
        canvas.drawText("$anchoC × $altoC cm", ox, oy + ph + 30f, pintaEtiq)
    }

    private fun dibujarNumero(
        canvas: Canvas, num: Int,
        rx: Float, ry: Float, rw: Float, rh: Float
    ) {
        val texto = num.toString()
        // Tamaño del número adaptado al espacio disponible, mínimo visible
        val ts = (minOf(rw, rh) * 0.55f).coerceIn(14f, 48f)
        pintaNumero.textSize = ts
        pintaSombraNum.textSize = ts
        pintaSombraNum.strokeWidth = ts * 0.18f

        val tw = pintaNumero.measureText(texto)
        val tx = rx + (rw - tw) / 2f
        val ty = ry + (rh + ts * 0.75f) / 2f

        // Halo blanco para legibilidad sobre cualquier color
        canvas.drawText(texto, tx, ty, pintaSombraNum)
        canvas.drawText(texto, tx, ty, pintaNumero)
    }

    private fun fmt(v: Float): String =
        if (v % 1f == 0f) v.toInt().toString()
        else "%.1f".format(v).replace(",", ".")
}
