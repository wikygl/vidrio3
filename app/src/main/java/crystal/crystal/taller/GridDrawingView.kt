package crystal.crystal.taller

// GridDrawingView.kt
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class GridDrawingView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var anchoTotal: Float = 150f
    private var altoTotal: Float = 180f

    private var anchosColumnas: MutableList<Float> = mutableListOf()
    private var alturasFilasPorColumna: MutableList<MutableList<Float>> = mutableListOf()

    private val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
    }

    private val textPaint = Paint().apply {
        color = Color.BLACK
        textSize = 30f
        textAlign = Paint.Align.CENTER
    }

    private val cotaPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 2f
    }

    // Métodos getter
    fun getAnchoTotal(): Float = anchoTotal
    fun getAltoTotal(): Float = altoTotal
    fun getAnchosColumnas(): List<Float> = anchosColumnas
    fun getAlturasFilasPorColumna(): List<List<Float>> = alturasFilasPorColumna

    private fun df(defo: Float): String {
        val resultado = if (defo % 1.0 == 0.0) {
            "%.0f".format(defo)
        } else {
            "%.1f".format(defo)
        }
        return resultado.replace(",", ".")
    }

    // Método para configurar los parámetros
    fun configurarParametros(
        anchoTotal: Float,
        altoTotal: Float,
        anchosColumnas: List<Float>,
        alturasFilasPorColumna: List<List<Float>>
    ) {
        this.anchoTotal = anchoTotal
        this.altoTotal = altoTotal

        this.anchosColumnas = anchosColumnas.toMutableList()
        this.alturasFilasPorColumna = alturasFilasPorColumna.map { it.toMutableList() }.toMutableList()

        invalidate() // Redibuja la vista con los nuevos parámetros
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // Ajustar el tamaño del texto
        textPaint.textSize = (viewWidth * 0.04f).coerceIn(12f, 30f)

        // Preparar el texto de las cotas usando la función df
        val anchoTextoTotal = df(anchoTotal)
        val altoTextoTotal = df(altoTotal)

        val fontMetrics = textPaint.fontMetrics
        val alturaTexto = fontMetrics.descent - fontMetrics.ascent

        // Porcentajes para los márgenes
        val marginPercentageWidth = 0.1f // 10% del ancho del View
        val marginPercentageHeight = 0.1f // 10% del alto del View

        // Márgenes mínimos y máximos
        val minMarginWidth = 20f
        val maxMarginWidth = 100f
        val minMarginHeight = 20f
        val maxMarginHeight = 100f

        // Cálculo de márgenes dinámicos
        var cotaMarginLeft = (viewWidth * marginPercentageWidth).coerceIn(minMarginWidth, maxMarginWidth)
        var cotaMarginRight = (viewWidth * marginPercentageWidth).coerceIn(minMarginWidth, maxMarginWidth)
        var cotaMarginTop = (viewHeight * marginPercentageHeight).coerceIn(minMarginHeight, maxMarginHeight)
        var cotaMarginBottom = (viewHeight * marginPercentageHeight).coerceIn(minMarginHeight, maxMarginHeight)

        // Asegurar que los márgenes son suficientes para el texto
        cotaMarginLeft = maxOf(cotaMarginLeft, alturaTexto + 20f)
        cotaMarginTop = maxOf(cotaMarginTop, alturaTexto + 50f)
        cotaMarginRight = maxOf(cotaMarginRight, alturaTexto + 20f)
        cotaMarginBottom = maxOf(cotaMarginBottom, alturaTexto + 50f)

        // Área disponible para la cuadrícula
        val availableWidth = viewWidth - cotaMarginLeft - cotaMarginRight
        val availableHeight = viewHeight - cotaMarginTop - cotaMarginBottom

        // Calcula los factores de escala
        val totalAnchoColumnas = anchosColumnas.sum()
        val scaleX = availableWidth / totalAnchoColumnas
        val scaleY = availableHeight / altoTotal
        val scale = minOf(scaleX, scaleY)

        // Calcula el ancho y alto reales de la cuadrícula
        val gridWidth = totalAnchoColumnas * scale
        val gridHeight = altoTotal * scale

        // Calcula las posiciones izquierda y superior para la cuadrícula
        val left = cotaMarginLeft + (availableWidth - gridWidth) / 2f
        val top = cotaMarginTop + (availableHeight - gridHeight) / 2f

        // Acumular posiciones para dibujar las líneas
        val posicionesX = mutableListOf<Float>()
        val posicionesYPorColumna = mutableListOf<MutableList<Float>>()

        var xPosition = left

        posicionesX.add(xPosition)

        // Dibuja las líneas verticales y las celdas
        for (i in anchosColumnas.indices) {
            val anchoColumna = anchosColumnas[i] * scale
            val alturasFilas = alturasFilasPorColumna[i]
            var yPosition = top

            val posicionesY = mutableListOf<Float>()
            posicionesY.add(yPosition)

            // Dibujar líneas horizontales dentro de la columna
            for (j in alturasFilas.indices) {
                val altoFila = alturasFilas[j] * scale
                canvas.drawLine(xPosition, yPosition, xPosition + anchoColumna, yPosition, paint)
                yPosition += altoFila
                posicionesY.add(yPosition)
            }
            // Dibujar la última línea horizontal de la columna
            canvas.drawLine(xPosition, yPosition, xPosition + anchoColumna, yPosition, paint)
            posicionesYPorColumna.add(posicionesY)

            // Dibujar la línea vertical de la columna
            canvas.drawLine(xPosition, top, xPosition, top + gridHeight, paint)
            xPosition += anchoColumna
            posicionesX.add(xPosition)
        }
        // Dibujar la última línea vertical
        canvas.drawLine(xPosition, top, xPosition, top + gridHeight, paint)

        // Dibujar cotas generales
        val cotaOffset = 20f // Desplazamiento para las cotas desde la cuadrícula

        // Cota del ancho total (debajo de la cuadrícula)
        var yCotaAnchoTotal = top + gridHeight + cotaOffset + alturaTexto
        // Verificar que yCotaAnchoTotal no exceda viewHeight
        if (yCotaAnchoTotal + alturaTexto > viewHeight) {
            yCotaAnchoTotal = viewHeight - alturaTexto - 10f
        }
        canvas.drawLine(left, yCotaAnchoTotal, left + gridWidth, yCotaAnchoTotal, cotaPaint)
        // Líneas de flecha
        canvas.drawLine(left, yCotaAnchoTotal - 10f, left, yCotaAnchoTotal + 10f, cotaPaint)
        canvas.drawLine(left + gridWidth, yCotaAnchoTotal - 10f, left + gridWidth, yCotaAnchoTotal + 10f, cotaPaint)
        // Texto de la cota
        canvas.drawText(anchoTextoTotal, left + gridWidth / 2, yCotaAnchoTotal + alturaTexto, textPaint)

        // Cota del alto total (a la izquierda de la cuadrícula)
        var xCotaAltoTotal = left - cotaOffset - alturaTexto
        // Verificar que xCotaAltoTotal no sea menor que 0
        if (xCotaAltoTotal - alturaTexto < 0f) {
            xCotaAltoTotal = alturaTexto + 10f
        }
        canvas.drawLine(xCotaAltoTotal, top, xCotaAltoTotal, top + gridHeight, cotaPaint)
        // Líneas de flecha
        canvas.drawLine(xCotaAltoTotal - 10f, top, xCotaAltoTotal + 10f, top, cotaPaint)
        canvas.drawLine(xCotaAltoTotal - 10f, top + gridHeight, xCotaAltoTotal + 10f, top + gridHeight, cotaPaint)
        // Texto de la cota
        canvas.save()
        canvas.rotate(-90f, xCotaAltoTotal - alturaTexto, top + gridHeight / 2)
        canvas.drawText(altoTextoTotal, xCotaAltoTotal - alturaTexto, top + gridHeight / 2 + alturaTexto / 2, textPaint)
        canvas.restore()

        // Dibujar cotas de columnas encima de la cuadrícula
        for (i in anchosColumnas.indices) {
            val xInicio = posicionesX[i]
            val xFin = posicionesX[i + 1]
            val xCentro = (xInicio + xFin) / 2

            // Línea de cota encima de la cuadrícula
            var yCota = top - cotaOffset
            // Asegurar que yCota no sea menor que 0
            if (yCota - alturaTexto < 0f) {
                yCota = alturaTexto + 10f
            }
            canvas.drawLine(xInicio, yCota, xFin, yCota, cotaPaint)
            // Líneas de flecha
            canvas.drawLine(xInicio, yCota - 10f, xInicio, yCota + 10f, cotaPaint)
            canvas.drawLine(xFin, yCota - 10f, xFin, yCota + 10f, cotaPaint)
            // Texto de la cota usando la función df
            val textoAnchoColumna = df(anchosColumnas[i])
            canvas.drawText(textoAnchoColumna, xCentro, yCota - 5f, textPaint)
        }

        // Dibujar cotas de filas a la derecha de la cuadrícula
        for (i in anchosColumnas.indices) {
            val posicionesY = posicionesYPorColumna[i]
            val alturasFilas = alturasFilasPorColumna[i]
            val xCotaBase = posicionesX[i + 1] + cotaOffset // A la derecha de la columna
            // Asegurar que xCotaBase no exceda viewWidth
            var xCota = xCotaBase
            if (xCota + alturaTexto > viewWidth) {
                xCota = viewWidth - alturaTexto - 10f
            }
            for (j in alturasFilas.indices) {
                val yInicio = posicionesY[j]
                val yFin = posicionesY[j + 1]
                val yCentro = (yInicio + yFin) / 2
                // Línea de cota
                canvas.drawLine(xCota, yInicio, xCota, yFin, cotaPaint)
                // Líneas de flecha
                canvas.drawLine(xCota - 10f, yInicio, xCota + 10f, yInicio, cotaPaint)
                canvas.drawLine(xCota - 10f, yFin, xCota + 10f, yFin, cotaPaint)
                // Texto de la cota usando la función df
                val textoAltoFila = df(alturasFilas[j])
                canvas.save()
                canvas.rotate(-90f, xCota + alturaTexto, yCentro)
                canvas.drawText(textoAltoFila, xCota + alturaTexto, yCentro + alturaTexto / 2, textPaint)
                canvas.restore()
            }
        }
    }
}







