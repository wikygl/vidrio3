package crystal.crystal.taller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Region
import android.graphics.RegionIterator
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
    private var modoVisual = ModoVisual.GRILLA
    private var marcoVisual: Float = 2.5f
    private var tuboVisual: Float = 3.8f
    private var grunaVisual: Float = 0f
    private val naves = linkedMapOf<Pair<Int, Int>, String>()
    private val formaCm = Path()
    private val poligonoActualCm = mutableListOf<PointF>()
    private val operacionesForma = mutableListOf<FormaOperacion>()
    private var formaInicializada = false
    private var formaModificada = false
    private var formaAnchoCm = anchoTotal
    private var formaAltoCm = altoTotal

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
    private val rowSelectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(60, 255, 193, 7)
        style = Paint.Style.FILL
    }
    private val columnSelectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(55, 33, 150, 243)
        style = Paint.Style.FILL
    }
    private val moduleSelectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(95, 76, 175, 80)
        style = Paint.Style.FILL
    }
    private val selectionBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(25, 118, 210)
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }
    private val aluminumPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(92, 102, 112)
        style = Paint.Style.FILL
    }
    private val aluminumLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(32, 40, 48)
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }
    private val aluminumBoundaryPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(92, 102, 112)
        strokeCap = Paint.Cap.SQUARE
        strokeJoin = Paint.Join.MITER
        style = Paint.Style.STROKE
    }
    private val glassPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(120, 105, 190, 225)
        style = Paint.Style.FILL
    }
    private val glassLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(18, 105, 145)
        strokeWidth = 1.5f
        style = Paint.Style.STROKE
    }
    private val navePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(198, 40, 40)
        style = Paint.Style.FILL
    }
    private val naveTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 22f
        textAlign = Paint.Align.CENTER
    }
    private val formaFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(18, 33, 150, 243)
        style = Paint.Style.FILL
    }
    private val formaOutlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(21, 101, 192)
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }
    private val poligonoPreviewPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(216, 67, 21)
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }
    private val poligonoPreviewFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(45, 216, 67, 21)
        style = Paint.Style.FILL
    }
    private var selectedRow = -1
    private var selectedColumn = -1
    private var selectedModuleColumn = -1
    private var selectedModuleRow = -1

    // ----- Getters públicos -----
    fun getAnchoTotal()             = anchoTotal
    fun getAltoTotal()              = altoTotal
    fun getAnchosColumnas()         = anchosColumnas as List<Float>
    fun getAlturasFilasPorColumna() = alturasFilasPorColumna as List<List<Float>>

    fun isFormaModificada(): Boolean = formaModificada

    /**
     * Devuelve los bounds (en cm) de la porción visible de una celda tras
     * aplicar la forma (recortes/soldaduras). Retorna null si la celda quedó
     * totalmente fuera de la forma.
     */
    fun obtenerBoundsVisibleCm(columna: Int, fila: Int): RectF? {
        if (columna !in anchosColumnas.indices) return null
        val filas = alturasFilasPorColumna.getOrNull(columna) ?: return null
        if (fila !in filas.indices) return null

        var xCm0 = 0f
        for (i in 0 until columna) xCm0 += anchosColumnas[i]
        val xCm1 = xCm0 + anchosColumnas[columna]
        var yCm0 = 0f
        for (j in 0 until fila) yCm0 += filas[j]
        val yCm1 = yCm0 + filas[fila]

        if (!formaModificada) return RectF(xCm0, yCm0, xCm1, yCm1)

        val cellPath = Path().apply { addRect(xCm0, yCm0, xCm1, yCm1, Path.Direction.CW) }
        cellPath.op(formaCm, Path.Op.INTERSECT)
        if (cellPath.isEmpty) return null

        val bounds = RectF()
        cellPath.computeBounds(bounds, true)
        if (bounds.isEmpty) return null

        // Path.op puede dejar ruido numérico de hasta ~2 mm en los bordes del
        // bounding box. Clampeamos al rectángulo original de la celda, hacemos
        // snap a vértices conocidos (bordes de columna/fila y puntos de los
        // recortes) con tolerancia 2 mm y redondeamos a 0.01 cm.
        val xVerts = verticesEnEje(xCm0, xCm1, ejeX = true)
        val yVerts = verticesEnEje(yCm0, yCm1, ejeX = false)
        val left = ajustarACm(snapAVertice(bounds.left.coerceIn(xCm0, xCm1), xVerts))
        val top = ajustarACm(snapAVertice(bounds.top.coerceIn(yCm0, yCm1), yVerts))
        val right = ajustarACm(snapAVertice(bounds.right.coerceIn(xCm0, xCm1), xVerts))
        val bottom = ajustarACm(snapAVertice(bounds.bottom.coerceIn(yCm0, yCm1), yVerts))

        val tol = 0.05f
        val esCompleta = kotlin.math.abs(left - xCm0) <= tol &&
            kotlin.math.abs(top - yCm0) <= tol &&
            kotlin.math.abs(right - xCm1) <= tol &&
            kotlin.math.abs(bottom - yCm1) <= tol
        return if (esCompleta) RectF(xCm0, yCm0, xCm1, yCm1) else RectF(left, top, right, bottom)
    }

    private fun ajustarACm(v: Float): Float = kotlin.math.round(v * 100f) / 100f

    private fun snapAVertice(v: Float, vertices: List<Float>, tol: Float = 0.2f): Float {
        return vertices.firstOrNull { kotlin.math.abs(it - v) <= tol } ?: v
    }

    private fun verticesEnEje(min: Float, max: Float, ejeX: Boolean): List<Float> {
        val out = mutableListOf<Float>()
        if (ejeX) {
            var x = 0f
            out.add(0f)
            for (a in anchosColumnas) { x += a; out.add(x) }
        } else {
            out.add(0f)
            alturasFilasPorColumna.forEach { filas ->
                var y = 0f
                for (h in filas) { y += h; out.add(y) }
            }
        }
        operacionesForma.forEach { op ->
            op.puntos.forEach { p ->
                val v = if (ejeX) p.x else p.y
                if (v in min..max) out.add(v)
            }
        }
        return out
    }

    /**
     * Devuelve los recortes activos representables como rectángulos alineados
     * a ejes (en cm). Útil para que las calculadoras de marcos/tubos puedan
     * razonar sobre la geometría del corte.
     */
    fun obtenerCortesRectangulares(): List<RectF> {
        val out = mutableListOf<RectF>()
        operacionesForma.forEach { op ->
            if (op.operacion != OperacionPoligono.RECORTAR) return@forEach
            val r = rectanguloAlineado(op.puntos) ?: return@forEach
            out.add(r)
        }
        return out
    }

    private fun rectanguloAlineado(puntos: List<PointF>): RectF? {
        if (puntos.size != 4) return null
        val xs = puntos.map { it.x }
        val ys = puntos.map { it.y }
        val xMin = xs.min(); val xMax = xs.max()
        val yMin = ys.min(); val yMax = ys.max()
        val tol = 0.1f
        val xsAlineadas = xs.all { kotlin.math.abs(it - xMin) <= tol || kotlin.math.abs(it - xMax) <= tol }
        val ysAlineadas = ys.all { kotlin.math.abs(it - yMin) <= tol || kotlin.math.abs(it - yMax) <= tol }
        return if (xsAlineadas && ysAlineadas && xMax - xMin > tol && yMax - yMin > tol) {
            RectF(xMin, yMin, xMax, yMax)
        } else null
    }

    /**
     * Largo total visible (en cm) sobre una línea vertical en x=xCm acotada
     * a [yMin, yMax]. Si la línea queda dividida por un recorte, suma las
     * porciones visibles. Si se pasa marcoCorte > 0, descuenta 1 marco por
     * cada borde interior del recorte que la línea atraviese (modela el
     * marco que se coloca en el perímetro del recorte).
     */
    fun longitudVerticalVisibleCm(
        xCm: Float,
        yMin: Float,
        yMax: Float,
        marcoCorte: Float = 0f
    ): Float {
        if (yMax <= yMin) return 0f
        if (!formaModificada) return yMax - yMin
        return medirEje(xCm, yMin, yMax, vertical = true, marcoCorte = marcoCorte)
    }

    fun longitudHorizontalVisibleCm(
        yCm: Float,
        xMin: Float,
        xMax: Float,
        marcoCorte: Float = 0f
    ): Float {
        if (xMax <= xMin) return 0f
        if (!formaModificada) return xMax - xMin
        return medirEje(yCm, xMin, xMax, vertical = false, marcoCorte = marcoCorte)
    }

    private fun medirEje(
        coordFija: Float,
        ejeMin: Float,
        ejeMax: Float,
        vertical: Boolean,
        marcoCorte: Float = 0f
    ): Float {
        // Rasterizamos a 0.01 cm de precisión usando Region. Para cortes
        // alineados a múltiplos de 0.01 cm el resultado es exacto.
        val esc = 100f
        val fijaPx = kotlin.math.round(coordFija * esc).toInt()
        val minPx = kotlin.math.round(ejeMin * esc).toInt()
        val maxPx = kotlin.math.round(ejeMax * esc).toInt()
        if (maxPx <= minPx) return 0f

        val pathScaled = Path(formaCm).apply {
            transform(Matrix().apply { setScale(esc, esc) })
        }
        val boundsForma = RectF()
        pathScaled.computeBounds(boundsForma, true)
        if (boundsForma.isEmpty) return 0f
        val padding = esc.toInt()
        val clip = Region(
            kotlin.math.floor(boundsForma.left).toInt() - padding,
            kotlin.math.floor(boundsForma.top).toInt() - padding,
            kotlin.math.ceil(boundsForma.right).toInt() + padding,
            kotlin.math.ceil(boundsForma.bottom).toInt() + padding
        )
        val regionForma = Region().apply { setPath(pathScaled, clip) }

        // Sondeamos la franja a ambos lados de la línea para que un eje
        // que cae justo sobre el borde de un recorte no se reporte como tapado.
        val segsA = obtenerSegmentos(regionForma, fijaPx - 1, minPx, maxPx, vertical)
        val segsB = obtenerSegmentos(regionForma, fijaPx, minPx, maxPx, vertical)
        val totalA = segsA.sumOf { it[1] - it[0] }
        val totalB = segsB.sumOf { it[1] - it[0] }
        val segs = if (totalA >= totalB) segsA else segsB
        val totalPx = if (totalA >= totalB) totalA else totalB

        val tol = 1
        var bordesInteriores = 0
        for (seg in segs) {
            if (seg[0] > minPx + tol) bordesInteriores++
            if (seg[1] < maxPx - tol) bordesInteriores++
        }
        val ajustePx = (bordesInteriores * marcoCorte * esc).toInt()
        return ((totalPx - ajustePx) / esc).coerceAtLeast(0f)
    }

    private fun obtenerSegmentos(
        regionForma: Region,
        fijaPx: Int,
        minPx: Int,
        maxPx: Int,
        vertical: Boolean
    ): List<IntArray> {
        val regionLinea = if (vertical) {
            Region(fijaPx, minPx, fijaPx + 1, maxPx)
        } else {
            Region(minPx, fijaPx, maxPx, fijaPx + 1)
        }
        regionLinea.op(regionForma, Region.Op.INTERSECT)
        val out = mutableListOf<IntArray>()
        val it = RegionIterator(regionLinea)
        val rect = Rect()
        while (it.next(rect)) {
            val s = if (vertical) rect.top else rect.left
            val e = if (vertical) rect.bottom else rect.right
            out.add(intArrayOf(s, e))
        }
        out.sortBy { it[0] }
        return out
    }

    data class GridCell(val columna: Int, val fila: Int)
    enum class ModoVisual { GRILLA, ESTRUCTURA, VIDRIO }
    enum class OperacionPoligono { SOLDAR, RECORTAR }
    private data class FormaOperacion(
        val operacion: OperacionPoligono,
        val puntos: List<PointF>
    )

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
        this.anchosColumnas = anchosColumnas.toMutableList()
        this.alturasFilasPorColumna = alturasFilasPorColumna.map { it.toMutableList() }.toMutableList()
        this.anchoTotal = anchoTotal
        this.altoTotal = altoTotal
        this.bastidorH = bastidorH
        this.bastidorM = bastidorM
        this.agujeroX  = agujeroX
        val cambioMedidas = formaAnchoCm != this.anchoTotal || formaAltoCm != this.altoTotal
        if (!formaInicializada || (cambioMedidas && !formaModificada)) {
            resetForma()
        } else if (cambioMedidas) {
            ajustarFormaANuevasMedidas()
        }
        invalidate()
    }

    fun setModoVisual(modo: ModoVisual, marco: Float, tubo: Float, gruna: Float) {
        modoVisual = modo
        marcoVisual = marco.coerceAtLeast(0f)
        tuboVisual = tubo.coerceAtLeast(0f)
        grunaVisual = gruna.coerceAtLeast(0f)
        invalidate()
    }

    fun getModoVisual(): ModoVisual = modoVisual

    fun setNaves(naves: Map<Pair<Int, Int>, String>) {
        this.naves.clear()
        this.naves.putAll(naves)
        invalidate()
    }

    fun resetForma() {
        formaCm.reset()
        formaCm.addRect(0f, 0f, anchoTotal, altoTotal, Path.Direction.CW)
        formaInicializada = true
        formaModificada = false
        formaAnchoCm = anchoTotal
        formaAltoCm = altoTotal
        operacionesForma.clear()
        poligonoActualCm.clear()
        invalidate()
    }

    fun iniciarPoligono(x: Float, y: Float) {
        poligonoActualCm.clear()
        agregarPuntoPoligono(x, y)
    }

    fun agregarPuntoPoligono(x: Float, y: Float) {
        val punto = puntoVistaACm(x, y)
        val ultimo = poligonoActualCm.lastOrNull()
        if (ultimo == null || kotlin.math.hypot((punto.x - ultimo.x).toDouble(), (punto.y - ultimo.y).toDouble()) > 0.5) {
            poligonoActualCm.add(punto)
            invalidate()
        }
    }

    fun moverPoligono(deltaXpx: Float, deltaYpx: Float) {
        if (poligonoActualCm.isEmpty()) return
        val m = calcularMetrics()
        val dx = deltaXpx / m.scale
        val dy = deltaYpx / m.scale
        poligonoActualCm.forEach { punto ->
            punto.x += dx
            punto.y += dy
        }
        invalidate()
    }

    fun limpiarPoligono() {
        poligonoActualCm.clear()
        invalidate()
    }

    fun setPoligonoCm(puntos: List<PointF>): Boolean {
        if (puntos.size < 3) return false
        poligonoActualCm.clear()
        poligonoActualCm.addAll(puntos.map { PointF(it.x, it.y) })
        invalidate()
        return true
    }

    private fun ajustarFormaANuevasMedidas() {
        if (formaAnchoCm <= 0f || formaAltoCm <= 0f) {
            resetForma()
            return
        }
        val sx = anchoTotal / formaAnchoCm
        val sy = altoTotal / formaAltoCm
        formaCm.transform(Matrix().apply { setScale(sx, sy) })
        poligonoActualCm.forEach { punto ->
            punto.x *= sx
            punto.y *= sy
        }
        operacionesForma.replaceAll { item ->
            FormaOperacion(
                item.operacion,
                item.puntos.map { punto -> PointF(punto.x * sx, punto.y * sy) }
            )
        }
        formaAnchoCm = anchoTotal
        formaAltoCm = altoTotal
    }

    fun aplicarPoligono(operacion: OperacionPoligono): Boolean {
        if (poligonoActualCm.size < 3) {
            invalidate()
            return false
        }
        val puntos = poligonoActualCm.map { PointF(it.x, it.y) }
        aplicarPoligonoInterno(operacion, puntos)
        operacionesForma.add(FormaOperacion(operacion, puntos))
        return true
    }

    private fun aplicarPoligonoInterno(operacion: OperacionPoligono, puntos: List<PointF>) {
        val poligono = Path().apply {
            moveTo(puntos.first().x, puntos.first().y)
            puntos.drop(1).forEach { lineTo(it.x, it.y) }
            close()
        }
        val op = when (operacion) {
            OperacionPoligono.SOLDAR -> Path.Op.UNION
            OperacionPoligono.RECORTAR -> Path.Op.DIFFERENCE
        }
        formaCm.op(poligono, op)
        formaModificada = true
        poligonoActualCm.clear()
        invalidate()
    }

    fun exportarForma(): String {
        return operacionesForma.joinToString("|") { item ->
            val prefijo = when (item.operacion) {
                OperacionPoligono.SOLDAR -> "S"
                OperacionPoligono.RECORTAR -> "R"
            }
            "$prefijo:${item.puntos.joinToString(";") { "${df(it.x)},${df(it.y)}" }}"
        }
    }

    fun importarForma(serializado: String) {
        resetForma()
        if (serializado.isBlank()) return
        serializado.split("|")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .forEach { item ->
                val partes = item.split(":", limit = 2)
                if (partes.size != 2) return@forEach
                val operacion = when (partes[0]) {
                    "S" -> OperacionPoligono.SOLDAR
                    "R" -> OperacionPoligono.RECORTAR
                    else -> return@forEach
                }
                val puntos = partes[1].split(";")
                    .mapNotNull { punto ->
                        val xy = punto.split(",", limit = 2)
                        if (xy.size != 2) return@mapNotNull null
                        val x = xy[0].toFloatOrNull()
                        val y = xy[1].toFloatOrNull()
                        if (x == null || y == null) null else PointF(x, y)
                    }
                if (puntos.size >= 3) {
                    aplicarPoligonoInterno(operacion, puntos)
                    operacionesForma.add(FormaOperacion(operacion, puntos))
                }
            }
    }

    fun actualizarSeleccion(
        fila: Int,
        columna: Int,
        moduloColumna: Int,
        moduloFila: Int
    ) {
        selectedRow = fila
        selectedColumn = columna
        selectedModuleColumn = moduloColumna
        selectedModuleRow = moduloFila
        invalidate()
    }

    fun detectarCelda(x: Float, y: Float): GridCell? {
        val m = calcularMetrics()
        if (!regionForma(m).contains(x.toInt(), y.toInt())) {
            return null
        }
        if (x < m.left || x > m.left + m.gridWidth || y < m.top || y > m.top + m.gridHeight) {
            return null
        }

        var xActual = m.left
        anchosColumnas.forEachIndexed { columna, anchoCm ->
            val xSiguiente = xActual + anchoCm * m.scale
            if (x <= xSiguiente || columna == anchosColumnas.lastIndex) {
                var yActual = m.top
                val filas = alturasFilasPorColumna.getOrNull(columna).orEmpty()
                filas.forEachIndexed { fila, altoCm ->
                    val ySiguiente = yActual + altoCm * m.scale
                    if (y <= ySiguiente || fila == filas.lastIndex) {
                        return GridCell(columna, fila)
                    }
                    yActual = ySiguiente
                }
                return null
            }
            xActual = xSiguiente
        }
        return null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 1) Métricas generales
        val m = calcularMetrics()

        // Grosor dinámico según escala
        val grosor = (m.scale * 1.0f).coerceIn(0.5f, 2f)
        paint.strokeWidth = grosor
        cotaPaint.strokeWidth = max(0.75f, grosor * 0.9f)

        val formaPx = formaEnPx(m)

        canvas.save()
        canvas.clipPath(formaPx)
        val (posX, posYCols) = dibujarCuadricula(canvas, m)
        when (modoVisual) {
            ModoVisual.GRILLA -> dibujarAgujerosYCotas(canvas, m)
            ModoVisual.ESTRUCTURA -> dibujarEstructura(canvas, m, posX, posYCols)
            ModoVisual.VIDRIO -> dibujarVidrios(canvas, m, posX, posYCols)
        }
        dibujarSeleccion(canvas, m, posX, posYCols)
        dibujarNaves(canvas, posX, posYCols)
        canvas.restore()

        if (formaModificada) {
            canvas.drawPath(formaPx, formaOutlinePaint)
        }
        dibujarPoligonoPreview(canvas, m)
        if (modoVisual == ModoVisual.GRILLA) {
            dibujarCotasGenerales(canvas, m, posX, posYCols)
        }
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

    private data class CeldaVisible(
        val rectPx: RectF,
        val anchoCm: Float,
        val altoCm: Float
    )

    private fun formaEnPx(m: Metrics): Path {
        return Path(formaCm).apply {
            transform(Matrix().apply {
                setScale(m.scale, m.scale)
                postTranslate(m.left, m.top)
            })
        }
    }

    private fun regionForma(m: Metrics): Region {
        val clip = Region(0, 0, width, height)
        return Region().apply {
            setPath(formaEnPx(m), clip)
        }
    }

    private fun puntoVistaACm(x: Float, y: Float): PointF {
        val m = calcularMetrics()
        return PointF((x - m.left) / m.scale, (y - m.top) / m.scale)
    }

    private fun dibujarPoligonoPreview(canvas: Canvas, m: Metrics) {
        if (poligonoActualCm.size < 2) return
        val preview = Path().apply {
            moveTo(poligonoActualCm.first().x, poligonoActualCm.first().y)
            poligonoActualCm.drop(1).forEach { lineTo(it.x, it.y) }
            if (poligonoActualCm.size >= 3) {
                close()
            }
        }
        preview.transform(Matrix().apply {
            setScale(m.scale, m.scale)
            postTranslate(m.left, m.top)
        })
        if (poligonoActualCm.size >= 3) {
            canvas.drawPath(preview, poligonoPreviewFillPaint)
        }
        canvas.drawPath(preview, poligonoPreviewPaint)
    }


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

    private fun dibujarSeleccion(
        canvas: Canvas,
        m: Metrics,
        posicionesX: List<Float>,
        posicionesY: List<List<Float>>
    ) {
        if (selectedRow >= 0) {
            for (columna in posicionesY.indices) {
                val ys = posicionesY[columna]
                if (selectedRow < ys.size - 1) {
                    canvas.drawRect(
                        RectF(posicionesX[columna], ys[selectedRow], posicionesX[columna + 1], ys[selectedRow + 1]),
                        rowSelectionPaint
                    )
                }
            }
        }

        if (selectedColumn >= 0 && selectedColumn < posicionesX.size - 1) {
            canvas.drawRect(
                RectF(posicionesX[selectedColumn], m.top, posicionesX[selectedColumn + 1], m.top + m.gridHeight),
                columnSelectionPaint
            )
        }

        if (selectedModuleColumn >= 0 && selectedModuleRow >= 0) {
            val ys = posicionesY.getOrNull(selectedModuleColumn)
            if (ys != null && selectedModuleColumn < posicionesX.size - 1 && selectedModuleRow < ys.size - 1) {
                val rect = RectF(
                    posicionesX[selectedModuleColumn],
                    ys[selectedModuleRow],
                    posicionesX[selectedModuleColumn + 1],
                    ys[selectedModuleRow + 1]
                )
                canvas.drawRect(rect, moduleSelectionPaint)
                canvas.drawRect(rect, selectionBorderPaint)
            }
        }
    }

    private fun dibujarNaves(
        canvas: Canvas,
        posicionesX: List<Float>,
        posicionesY: List<List<Float>>
    ) {
        naves.forEach { (posicion, identificador) ->
            val columna = posicion.first
            val fila = posicion.second
            val ys = posicionesY.getOrNull(columna) ?: return@forEach
            if (columna >= posicionesX.lastIndex || fila >= ys.lastIndex) return@forEach

            val x0 = posicionesX[columna]
            val x1 = posicionesX[columna + 1]
            val y0 = ys[fila]
            val y1 = ys[fila + 1]
            val radius = min(x1 - x0, y1 - y0).coerceIn(24f, 44f) / 2f
            val cx = x0 + radius + 6f
            val cy = y0 + radius + 6f

            naveTextPaint.textSize = (radius * 0.9f).coerceAtLeast(12f)
            canvas.drawCircle(cx, cy, radius, navePaint)
            dibujarTextoCentradoConPaint(canvas, identificador, cx, cy, naveTextPaint)
        }
    }

    private fun dibujarEstructura(
        canvas: Canvas,
        m: Metrics,
        posicionesX: List<Float>,
        posicionesY: List<List<Float>>
    ) {
        val marcoPx = marcoVisual * m.scale
        val tuboPx = tuboVisual * m.scale
        val left = m.left
        val top = m.top
        val right = m.left + m.gridWidth
        val bottom = m.top + m.gridHeight

        if (marcoPx > 0f) {
            aluminumBoundaryPaint.strokeWidth = marcoPx * 2f
            canvas.drawPath(formaEnPx(m), aluminumBoundaryPaint)
        }

        if (tuboPx > 0f) {
            for (i in 1 until posicionesX.lastIndex) {
                val x = posicionesX[i]
                canvas.drawRect(RectF(x - tuboPx / 2f, top + marcoPx, x + tuboPx / 2f, bottom - marcoPx), aluminumPaint)
            }

            for (columna in posicionesY.indices) {
                val ys = posicionesY[columna]
                val ajusteInicio = if (columna == 0) marcoPx else tuboPx / 2f
                val ajusteFin = if (columna == posicionesY.lastIndex) marcoPx else tuboPx / 2f
                val x0 = posicionesX[columna] + ajusteInicio
                val x1 = posicionesX[columna + 1] - ajusteFin
                for (fila in 1 until ys.lastIndex) {
                    val y = ys[fila]
                    canvas.drawRect(RectF(x0, y - tuboPx / 2f, x1, y + tuboPx / 2f), aluminumPaint)
                }
            }
        }

        canvas.drawPath(formaEnPx(m), aluminumLinePaint)
        dibujarTextoCentrado(canvas, "Estructura", left + (right - left) / 2f, top - m.alturaTxt * 0.5f)
    }

    private fun dibujarVidrios(
        canvas: Canvas,
        m: Metrics,
        posicionesX: List<Float>,
        posicionesY: List<List<Float>>
    ) {
        val insetPx = grunaVisual * m.scale / 2f
        for (columna in posicionesY.indices) {
            val ys = posicionesY[columna]
            for (fila in 0 until ys.size - 1) {
                val visible = celdaVisible(columna, fila, posicionesX, ys, m) ?: continue
                val rect = RectF(
                    visible.rectPx.left + insetPx,
                    visible.rectPx.top + insetPx,
                    visible.rectPx.right - insetPx,
                    visible.rectPx.bottom - insetPx
                )
                if (rect.width() <= 0f || rect.height() <= 0f) continue
                canvas.drawRect(rect, glassPaint)
                canvas.drawRect(rect, glassLinePaint)
                val anchoVidrio = (visible.anchoCm - grunaVisual).coerceAtLeast(0f)
                val altoVidrio = (visible.altoCm - grunaVisual).coerceAtLeast(0f)
                dibujarTextoVidrio(canvas, "${df(anchoVidrio)} x ${df(altoVidrio)}", rect)
            }
        }
    }

    private fun celdaVisible(
        columna: Int,
        fila: Int,
        posicionesX: List<Float>,
        ys: List<Float>,
        m: Metrics
    ): CeldaVisible? {
        if (columna >= posicionesX.lastIndex || fila >= ys.lastIndex) return null
        val boundsCm = obtenerBoundsVisibleCm(columna, fila) ?: return null
        val rectPx = RectF(
            m.left + boundsCm.left * m.scale,
            m.top + boundsCm.top * m.scale,
            m.left + boundsCm.right * m.scale,
            m.top + boundsCm.bottom * m.scale
        )
        return CeldaVisible(
            rectPx = rectPx,
            anchoCm = boundsCm.width(),
            altoCm = boundsCm.height()
        )
    }

    private fun dibujarTextoCentrado(canvas: Canvas, texto: String, x: Float, yCentro: Float) {
        val baseline = yCentro - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(texto, x, baseline, textPaint)
    }

    private fun dibujarTextoVidrio(canvas: Canvas, texto: String, rect: RectF) {
        val originalSize = textPaint.textSize
        val anchoTexto = textPaint.measureText(texto).coerceAtLeast(1f)
        val escalaAncho = (rect.width() * 0.9f) / anchoTexto
        val maxPorAlto = rect.height() * 0.45f
        val tamPropuesto = min(originalSize, originalSize * escalaAncho)
        val tamFinal = min(tamPropuesto, maxPorAlto)
        val pisoLegible = max(12f, originalSize * 0.55f)

        if (tamFinal < pisoLegible) {
            // Módulo demasiado pequeño para meter el rótulo dentro: lo dibujo
            // afuera (a la derecha) con una línea guía y tamaño legible.
            textPaint.textSize = pisoLegible
            val xTexto = rect.right + pisoLegible * 0.6f
            val yTexto = rect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f
            canvas.drawLine(rect.right, rect.centerY(), xTexto - 2f, rect.centerY(), glassLinePaint)
            val alignPrev = textPaint.textAlign
            textPaint.textAlign = Paint.Align.LEFT
            canvas.drawText(texto, xTexto, yTexto, textPaint)
            textPaint.textAlign = alignPrev
            textPaint.textSize = originalSize
            return
        }

        textPaint.textSize = tamFinal
        val baseline = rect.centerY() - (textPaint.descent() + textPaint.ascent()) / 2f
        canvas.drawText(texto, rect.centerX(), baseline, textPaint)
        textPaint.textSize = originalSize
    }

    private fun dibujarTextoCentradoConPaint(canvas: Canvas, texto: String, x: Float, yCentro: Float, paint: Paint) {
        val baseline = yCentro - (paint.descent() + paint.ascent()) / 2f
        canvas.drawText(texto, x, baseline, paint)
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
