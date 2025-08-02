package crystal.crystal.taller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min

class GridDrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // ----- Parámetros de dibujo -----
    private var anchoTotal: Float = 150f
    private var altoTotal: Float = 180f
    private var anchosColumnas = mutableListOf<Float>()
    private var alturasFilasPorColumna = mutableListOf<MutableList<Float>>()
    private var bastidorH: Float = 0f
    private var bastidorM: Float = 0f
    private var agujeroX: Float = 5f

    // ----- Pinturas -----
    private val paint = Paint().apply { color = Color.BLACK; strokeWidth = 2f }
    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }
    private val cotaPaint = Paint().apply { color = Color.RED; strokeWidth = 2f }

    // ----- Getters públicos -----
    fun getAnchoTotal()             = anchoTotal
    fun getAltoTotal()              = altoTotal
    fun getAnchosColumnas()         = anchosColumnas as List<Float>
    fun getAlturasFilasPorColumna() = alturasFilasPorColumna as List<List<Float>>

    private fun df(value: Float): String =
        if (value % 1.0 == 0.0) "%.0f".format(value) else "%.1f".format(value)

    /**
     * Configura todos los parámetros de dibujo.
     */
    fun configurarParametros(
        anchoTotal: Float,
        altoTotal: Float,
        anchosColumnas: List<Float>,

        alturasFilasPorColumna: List<List<Float>>,
        bastidorH: Float = 0f,
        bastidorM: Float = 0f,
        agujeroX: Float = 5f
    ) {
        this.anchoTotal = anchoTotal
        this.altoTotal  = altoTotal
        this.anchosColumnas = anchosColumnas.toMutableList()
        this.alturasFilasPorColumna = alturasFilasPorColumna.map { it.toMutableList() }.toMutableList()
        this.bastidorH = bastidorH
        this.bastidorM = bastidorM
        this.agujeroX  = agujeroX
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 1) Calculamos métricas generales
        val m = calcularMetrics()
        // 2) Dibujamos cuadrícula y obtenemos posiciones
        val (posX, posYCols) = dibujarCuadricula(canvas, m)
        // 3) Dibujamos agujeros y cotas dinámicas
        dibujarAgujerosYCotas(canvas, m)
        // 4) Dibujamos cotas generales de la cuadrícula
        dibujarCotasGenerales(canvas, m, posX, posYCols)
        // 5) En el futuro: dibujarEntalles(canvas, m), dibujarChapas(canvas, m), etc.
    }

    // ----- Estructura de datos internas -----
    private data class Metrics(
        val viewWidth: Float,
        val viewHeight: Float,
        val left: Float,
        val top: Float,
        val gridWidth: Float,
        val gridHeight: Float,
        val scale: Float,
        val baseY: Float,
        val alturaTxt: Float
    )

    /**
     * Calcula márgenes, escala y posiciones base.
     */
    private fun calcularMetrics(): Metrics {
        val vw = width.toFloat()
        val vh = height.toFloat()
        // Ajuste de texto
        textPaint.textSize = (vw * 0.04f).coerceIn(12f, 30f)
        val fm = textPaint.fontMetrics
        val hTxt = fm.descent - fm.ascent
        // Márgenes dinámicos
        val pctW = 0.1f; val pctH = 0.1f
        var mL = (vw * pctW).coerceIn(20f, 100f)
        var mR = (vw * pctW).coerceIn(20f, 100f)
        var mT = (vh * pctH).coerceIn(20f, 100f)
        var mB = (vh * pctH).coerceIn(20f, 100f)
        mL = max(mL, hTxt + 20f); mR = max(mR, hTxt + 20f)
        mT = max(mT, hTxt + 50f); mB = max(mB, hTxt + 50f)
        // Área utilizable
        val availW = vw - mL - mR
        val availH = vh - mT - mB
        // Escala
        val totalCols = anchosColumnas.sum()
        val scaleX = availW / totalCols
        val scaleY = availH / altoTotal
        val scale  = min(scaleX, scaleY)
        // Posición centraday dimensiones
        val gW = totalCols * scale
        val gH = altoTotal * scale
        val left = mL + (availW - gW) / 2f
        val top  = mT + (availH - gH) / 2f
        val baseY = top + gH
        return Metrics(vw, vh, left, top, gW, gH, scale, baseY, hTxt)
    }

    /**
     * Dibuja las líneas de la cuadrícula y retorna las listas de posiciones X y Y.
     */
    private fun dibujarCuadricula(
        canvas: Canvas,
        m: Metrics
    ): Pair<List<Float>, List<List<Float>>> {
        val posX = mutableListOf<Float>()
        val posYcols = mutableListOf<MutableList<Float>>()
        var x = m.left
        posX.add(x)
        anchosColumnas.forEachIndexed { i, anchoCm ->
            val wPx = anchoCm * m.scale
            val filasCm = alturasFilasPorColumna[i]
            var y = m.top
            val posY = mutableListOf(y)
            // horizontales
            filasCm.forEach { hCm ->
                canvas.drawLine(x, y, x + wPx, y, paint)
                y += hCm * m.scale
                posY.add(y)
            }
            // última horizontal
            canvas.drawLine(x, y, x + wPx, y, paint)
            posYcols.add(posY)
            // línea vertical
            canvas.drawLine(x, m.top, x, m.top + m.gridHeight, paint)
            x += wPx
            posX.add(x)
        }
        // última vertical
        canvas.drawLine(x, m.top, x, m.top + m.gridHeight, paint)
        return posX to posYcols
    }

    /**
     * Dibuja los círculos de agujero y sus cotas dinámicas.
     */
    private fun dibujarAgujerosYCotas(
        canvas: Canvas,
        m: Metrics
    ) {
        val radioPx = 3.6f * m.scale / 2f
        val cx = m.left + agujeroX * m.scale
        // Distancias en cm
        val izq    = agujeroX
        val der    = anchoTotal - agujeroX
        val menor  = min(izq, der)
        val xLado  = if (izq <= der) m.left else m.left + m.gridWidth
        val sepPx  = radioPx + 10f
        if (bastidorH > 0f) {
            if (bastidorM > 0f) {
                val h1 = bastidorH - bastidorM/2f
                val h2 = bastidorH + bastidorM/2f
                val y1 = m.baseY - h1*m.scale
                val y2 = m.baseY - h2*m.scale
                // círculos
                canvas.drawCircle(cx, y1, radioPx, paint)
                canvas.drawCircle(cx, y2, radioPx, paint)
                // cotas verticales
                dibujarCotaVertical(canvas, cx, m.baseY, y1, df(h1), m.alturaTxt, radioPx + 20f)
                dibujarCotaVertical(canvas, cx, y1, y2, df(bastidorM), m.alturaTxt, radioPx + 60f)
                // cotas horizontales
                dibujarCotaHorizontal(canvas, cx, y1 - sepPx, xLado, df(menor))
                dibujarCotaHorizontal(canvas, cx, y2 - sepPx, xLado, df(menor))
            } else {
                val h = bastidorH
                val y = m.baseY - h*m.scale
                canvas.drawCircle(cx, y, radioPx, paint)
                dibujarCotaVertical(canvas, cx, m.baseY, y, df(h), m.alturaTxt, radioPx + 20f)
                dibujarCotaHorizontal(canvas, cx, y - sepPx, xLado, df(min(izq, der)))
            }
        }
    }

    /**
     * Dibuja una cota vertical con marcas y texto.
     */
    private fun dibujarCotaVertical(
        canvas: Canvas,
        x: Float,
        yStart: Float,
        yEnd: Float,
        texto: String,
        alturaTxt: Float,
        offsetX: Float
    ) {
        val xC = x + offsetX
        canvas.drawLine(xC, yStart, xC, yEnd, cotaPaint)
        canvas.drawLine(xC - 10f, yStart, xC + 10f, yStart, cotaPaint)
        canvas.drawLine(xC - 10f, yEnd,   xC + 10f, yEnd,   cotaPaint)
        canvas.drawText(texto, xC + 10f + alturaTxt/2, (yStart + yEnd)/2 + alturaTxt/2, textPaint)
    }

    /**
     * Dibuja una cota horizontal con marcas y texto.
     */
    private fun dibujarCotaHorizontal(
        canvas: Canvas,
        xStart: Float,
        y: Float,
        xEnd: Float,
        texto: String
    ) {
        canvas.drawLine(xStart, y, xEnd, y, cotaPaint)
        canvas.drawLine(xStart, y - 10f, xStart, y + 10f, cotaPaint)
        canvas.drawLine(xEnd,   y - 10f, xEnd,   y + 10f, cotaPaint)
        canvas.drawText(texto, (xStart + xEnd) / 2, y - 10f, textPaint)
    }

    /**
     * Dibuja las cotas generales (ancho y alto total, columnas, filas).
     */
    private fun dibujarCotasGenerales(
        canvas: Canvas,
        m: Metrics,
        posicionesX: List<Float>,
        posicionesY: List<List<Float>>
    ) {
        val cOff = 20f
        // Cota ancho total (abajo)
        var yCotaA = m.top + m.gridHeight + cOff + m.alturaTxt
        if (yCotaA + m.alturaTxt > m.viewHeight)
            yCotaA = m.viewHeight - m.alturaTxt - 10f
        canvas.drawLine(m.left, yCotaA, m.left + m.gridWidth, yCotaA, cotaPaint)
        canvas.drawLine(m.left, yCotaA - 10f, m.left, yCotaA + 10f, cotaPaint)
        canvas.drawLine(m.left + m.gridWidth, yCotaA - 10f, m.left + m.gridWidth, yCotaA + 10f, cotaPaint)
        canvas.drawText(df(anchoTotal), m.left + m.gridWidth/2, yCotaA + m.alturaTxt, textPaint)
        // Cota alto total (izquierda)
        var xCotaB = m.left - cOff - m.alturaTxt
        if (xCotaB - m.alturaTxt < 0f)
            xCotaB = m.alturaTxt + 10f
        canvas.drawLine(xCotaB, m.top, xCotaB, m.top + m.gridHeight, cotaPaint)
        canvas.drawLine(xCotaB - 10f, m.top, xCotaB + 10f, m.top, cotaPaint)
        canvas.drawLine(xCotaB - 10f, m.top + m.gridHeight, xCotaB + 10f, m.top + m.gridHeight, cotaPaint)
        canvas.save()
        canvas.rotate(-90f, xCotaB - m.alturaTxt, m.top + m.gridHeight/2)
        canvas.drawText(df(altoTotal), xCotaB - m.alturaTxt, m.top + m.gridHeight/2 + m.alturaTxt/2, textPaint)
        canvas.restore()
        // Cotas de columnas arriba
        for (i in anchosColumnas.indices) {
            val xi = posicionesX[i]
            val xf = posicionesX[i + 1]
            var yC = m.top - cOff
            if (yC - m.alturaTxt < 0f) yC = m.alturaTxt + 10f
            canvas.drawLine(xi, yC, xf, yC, cotaPaint)
            canvas.drawLine(xi, yC - 10f, xi, yC + 10f, cotaPaint)
            canvas.drawLine(xf, yC - 10f, xf, yC + 10f, cotaPaint)
            canvas.drawText(df(anchosColumnas[i]), (xi + xf)/2, yC - 5f, textPaint)
        }
        // Cotas de filas a la derecha
        for (i in anchosColumnas.indices) {
            val ys = posicionesY[i]
            var xC = posicionesX[i + 1] + cOff
            if (xC + m.alturaTxt > m.viewWidth) xC = m.viewWidth - m.alturaTxt - 10f
            for (j in 0 until ys.size - 1) {
                val y0 = ys[j]
                val y1 = ys[j + 1]
                canvas.drawLine(xC, y0, xC, y1, cotaPaint)
                canvas.drawLine(xC - 10f, y0, xC + 10f, y0, cotaPaint)
                canvas.drawLine(xC - 10f, y1, xC + 10f, y1, cotaPaint)
                canvas.save()
                canvas.rotate(-90f, xC + m.alturaTxt, (y0 + y1) / 2)
                canvas.drawText(df(alturasFilasPorColumna[i][j]), xC + m.alturaTxt, (y0 + y1)/2 + m.alturaTxt/2, textPaint)
                canvas.restore()
            }
        }
    }
}









