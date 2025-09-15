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
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }
    private val cotaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    // ----- Getters públicos -----
    fun getAnchoTotal()             = anchoTotal
    fun getAltoTotal()              = altoTotal
    fun getAnchosColumnas()         = anchosColumnas as List<Float>
    fun getAlturasFilasPorColumna() = alturasFilasPorColumna as List<List<Float>>

    private fun df(value: Float): String =
        if (value % 1.0 == 0.0) "%.0f".format(value) else "%.1f".format(value)

    /**
     * Configura todos los parámetros de dibujo (sin límites de cantidad).
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
        // 1) Métricas generales
        val m = calcularMetrics()

        // Grosor dinámico según escala
        val grosor = (m.scale * 1.0f).coerceIn(0.5f, 2f)
        paint.strokeWidth = grosor
        cotaPaint.strokeWidth = max(0.75f, grosor * 0.9f)

        // 2) Cuadrícula + posiciones
        val (posX, posYCols) = dibujarCuadricula(canvas, m)
        // 3) Agujeros y cotas del bastidor
        dibujarAgujerosYCotas(canvas, m)
        // 4) Cotas generales
        dibujarCotasGenerales(canvas, m, posX, posYCols)
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
        val alturaTxt: Float,
        // Bandas reservadas para cotas (evitan recortes)
        val bandaIzq: Float,
        val bandaDer: Float,
        val bandaArr: Float,
        val bandaAbj: Float
    )


    /**
     * Calcula márgenes, escala y posiciones base.
     */
    private fun calcularMetrics(): Metrics {
        val vw = width.toFloat().coerceAtLeast(1f)
        val vh = height.toFloat().coerceAtLeast(1f)

        // Tamaño de texto: mínimo 8f; el resto proporcional
        val tsPropuesto = min(vw, vh) * 0.035f
        textPaint.textSize = max(8f, tsPropuesto)
        val fm = textPaint.fontMetrics
        val hTxt = fm.descent - fm.ascent

        // Márgenes exteriores mínimos (del borde de la vista a cualquier cosa)
        val mLext = max(hTxt * 0.25f + 4f, vw * 0.02f)
        val mRext = max(hTxt * 0.25f + 4f, vw * 0.02f)
        val mText = max(hTxt * 0.25f + 8f, vh * 0.02f)
        val mBext = max(hTxt * 0.25f + 8f, vh * 0.02f)

        // Offset “agradable” entre la rejilla y las cotas (ni pegado ni lejos)
        val cOff = max(10f, hTxt * 0.35f)

        // Bandas reservadas para las cotas (garantizan que no se recorten)
        // - Izquierda: texto rotado (ancho ~ hTxt) + marcas (~10) + separación (cOff)
        val bandaIzq = hTxt + 10f + cOff
        // - Derecha: cotas de filas con texto rotado a la derecha
        val bandaDer = hTxt + 10f + cOff
        // - Arriba: cotas de columnas con texto normal
        val bandaArr = hTxt + 10f + cOff
        // - Abajo: cota de ancho total con texto
        val bandaAbj = hTxt + 10f + cOff

        // Área disponible **para la rejilla** descontando bandas + márgenes exteriores
        val availW = (vw - mLext - mRext - bandaIzq - bandaDer).coerceAtLeast(1f)
        val availH = (vh - mText - mBext - bandaArr - bandaAbj).coerceAtLeast(1f)

        // Escala para la rejilla (con un pequeño factor de seguridad para evitar roces)
        val totalCols = anchosColumnas.sum().coerceAtLeast(1e-3f)
        val scaleX = availW / totalCols
        val scaleY = availH / max(altoTotal, 1e-3f)
        val scaleRaw = min(scaleX, scaleY)
        val factorSeguridad = 0.96f // encoge un poco la rejilla para que siempre “respire”
        val scale = scaleRaw * factorSeguridad

        // Dimensiones finales de la rejilla
        val gW = totalCols * scale
        val gH = altoTotal * scale

        // Posición de la rejilla: centrada dentro del “cajón” útil
        val left = mLext + bandaIzq + (availW - gW) / 2f
        val top  = mText + bandaArr + (availH - gH) / 2f
        val baseY = top + gH

        return Metrics(
            vw, vh, left, top, gW, gH, scale, baseY, hTxt,
            bandaIzq, bandaDer, bandaArr, bandaAbj
        )
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
            val filasCm = alturasFilasPorColumna.getOrNull(i) ?: emptyList<Float>()
            var y = m.top
            val posY = mutableListOf(y)

            // horizontales internas
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
        val radioPx = max(1f, 3.6f * m.scale / 2f)
        val cx = m.left + agujeroX * m.scale

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
     * Cota vertical con marcas y texto.
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
     * Cota horizontal con marcas y texto.
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
     * Dibuja las cotas generales (ancho y alto total, columnas, filas),
     * pegadas al diseño y con guardas para no salir de la vista.
     */
    private fun dibujarCotasGenerales(
        canvas: Canvas,
        m: Metrics,
        posicionesX: List<Float>,
        posicionesY: List<List<Float>>
    ) {
        // Offset moderado (más que antes), pero con bandas ya reservadas no habrá recortes
        val cOff = max(10f, m.alturaTxt * 0.35f)

        // ---------- Cota de ANCHO total (abajo), dentro de la banda inferior ----------
        val yCotaA = m.top + m.gridHeight + cOff
        canvas.drawLine(m.left, yCotaA, m.left + m.gridWidth, yCotaA, cotaPaint)
        canvas.drawLine(m.left, yCotaA - 10f, m.left, yCotaA + 10f, cotaPaint)
        canvas.drawLine(m.left + m.gridWidth, yCotaA - 10f, m.left + m.gridWidth, yCotaA + 10f, cotaPaint)
        canvas.drawText(df(anchoTotal), m.left + m.gridWidth / 2f, yCotaA + m.alturaTxt, textPaint)

        // ---------- Cota de ALTO total (izquierda), dentro de la banda izquierda ----------
        val xCotaB = m.left - cOff
        canvas.drawLine(xCotaB, m.top, xCotaB, m.top + m.gridHeight, cotaPaint)
        canvas.drawLine(xCotaB - 10f, m.top, xCotaB + 10f, m.top, cotaPaint)
        canvas.drawLine(xCotaB - 10f, m.top + m.gridHeight, xCotaB + 10f, m.top + m.gridHeight, cotaPaint)
        canvas.save()
        canvas.rotate(-90f, xCotaB - m.alturaTxt, m.top + m.gridHeight / 2f)
        canvas.drawText(df(altoTotal), xCotaB - m.alturaTxt, m.top + m.gridHeight / 2f + m.alturaTxt / 2f, textPaint)
        canvas.restore()

        // ---------- Cotas de columnas (arriba), dentro de la banda superior ----------
        val yC = m.top - cOff
        for (i in anchosColumnas.indices) {
            val xi = posicionesX[i]
            val xf = posicionesX[i + 1]
            canvas.drawLine(xi, yC, xf, yC, cotaPaint)
            canvas.drawLine(xi, yC - 10f, xi, yC + 10f, cotaPaint)
            canvas.drawLine(xf, yC - 10f, xf, yC + 10f, cotaPaint)
            canvas.drawText(df(anchosColumnas[i]), (xi + xf) / 2f, yC - 5f, textPaint)
        }

        // ---------- Cotas de filas (derecha), dentro de la banda derecha ----------
        for (i in anchosColumnas.indices) {
            val ys = posicionesY[i]
            val xC = posicionesX[i + 1] + cOff
            for (j in 0 until ys.size - 1) {
                val y0 = ys[j]
                val y1 = ys[j + 1]
                canvas.drawLine(xC, y0, xC, y1, cotaPaint)
                canvas.drawLine(xC - 10f, y0, xC + 10f, y0, cotaPaint)
                canvas.drawLine(xC - 10f, y1, xC + 10f, y1, cotaPaint)
                canvas.save()
                canvas.rotate(-90f, xC + m.alturaTxt, (y0 + y1) / 2f)
                canvas.drawText(df(alturasFilasPorColumna[i][j]), xC + m.alturaTxt, (y0 + y1) / 2f + m.alturaTxt / 2f, textPaint)
                canvas.restore()
            }
        }
    }


}
