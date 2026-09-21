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

    /** Avisa con lo que se tocó dentro del mueble (cajón, repisa, tubo, cuerpo…), o null si se tocó fuera. */
    var alTocarElemento: ((ElementoRopero?) -> Unit)? = null

    /** Avisa cuando se toca la cota de alto de un trozo (casillero, colgador, maletero, espacio de cajones): para escribirle el alto. */
    var alTocarCota: ((ElementoRopero) -> Unit)? = null

    /** Dónde quedó pintada cada cota de alto, para saber cuál se tocó. */
    private val cotasDeTrozos = mutableListOf<Pair<android.graphics.RectF, ElementoRopero>>()

    /** Las cotas de cada cosa de dentro: el alto de cada cajón y la altura de cada repisa y del tubo. */
    var conCotasDeElementos: Boolean = false
        set(value) { field = value; invalidate() }

    /** El elemento marcado en azul (el que se está editando en la pantalla de diseño). */
    var elementoResaltado: ElementoRopero? = null
        set(value) { field = value; invalidate() }

    /** Avisa cuando el dedo arrastra por el lienzo, en cm del mueble (la pantalla de diseño lo usa para mover repisas y cajones). */
    var alArrastrar: ((ElementoRopero, Float, Float) -> Unit)? = null
    var alSoltarArrastre: (() -> Unit)? = null

    private val dp = resources.displayMetrics.density
    private val dibujo = RoperoDibujo(dp)
    private val pCota = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); style = Paint.Style.STROKE; strokeWidth = 1f * dp }
    private val pTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); textSize = 11f * dp; textAlign = Paint.Align.CENTER }
    private val pFondoCota = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL; alpha = 220 }
    private val pResalte = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1E88E5"); style = Paint.Style.STROKE; strokeWidth = 2.5f * dp }

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
        // Las filas de cotas de columnas, de gruesas a finas, van debajo del mueble: sitio para ellas.
        filasDeColumnas = if (en3d) emptyList() else filasDeColumnas(r)
        val extraAbajo = (filasDeColumnas.size - 1).coerceAtLeast(0) * FILA_DP * dp
        val extraArriba = if (filasDeColumnas.isNotEmpty()) FILA_DP * dp else 0f
        val fondoPapelX = if (en3d) r.fondoCm * dibujo.oblicuoX else 0f
        val fondoPapelY = if (en3d) -r.fondoCm * dibujo.oblicuoY else 0f
        val altoMayor = r.altoMayorCm
        escala = min(
            (width - 2 * margen) / (r.anchoCm + fondoPapelX),
            (height - 2 * margen - extraAbajo - extraArriba) / (altoMayor + fondoPapelY)
        )
        origenX = (width - (r.anchoCm + fondoPapelX) * escala) / 2f
        origenY = height - extraAbajo - (height - extraAbajo - extraArriba - (altoMayor + fondoPapelY) * escala) / 2f
        dibujo.dibujar(canvas, r, origenX, origenY, escala, mostrarPuertas, en3d, cuerpoResaltado)
        elementoResaltado?.let { el ->
            canvas.drawRect(x(el.x0) - 2 * dp, y(el.y1) - 2 * dp, x(el.x1) + 2 * dp, y(el.y0) + 2 * dp, pResalte)
        }
        if (!en3d) dibujarCotas(canvas)
        if (!en3d && conCotasDeElementos) dibujarCotasDeElementos(canvas)
    }

    private fun dibujarCotas(canvas: Canvas) {
        val r = ropero
        val sep = 14f * dp
        // Ancho total, arriba (más arriba aún si hay una fila de cuerpos encima del mueble).
        val yA = y(r.altoMayorCm) - sep - (if (r.cuerpos.any { it.partes.isNotEmpty() }) 16 * dp else 0f)
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
        // Abajo, cada cuerpo; y si hay cuerpos con casilleros partidos en columnas, abajo van las
        // columnas, una fila por nivel (las gruesas primero, las más finas más abajo), y los
        // cuerpos enteros suben a una fila encima del mueble: el conjunto por arriba y cada parte
        // por abajo, y cada cota se toca para elegir lo suyo.
        cotasDeCuerpos.clear()
        val yB = y(0f) + sep
        if (filasDeColumnas.isEmpty()) {
            RoperoGeometria.cuerposX(r).forEachIndexed { i, (izq, der) -> cotaDeAncho(canvas, izq, der, yB, RoperoGeometria.cuerpo(r, i)) }
        } else {
            // Abajo, las columnas de verdad (las últimas de cada partición); arriba, los cuerpos, que son la suma.
            filasDeColumnas[0].forEach { col -> cotaDeAncho(canvas, col.x0, col.x1, yB, col) }
            val yC = y(r.altoMayorCm) - sep
            RoperoGeometria.cuerposX(r).forEachIndexed { i, (izq, der) -> cotaDeAncho(canvas, izq, der, yC, RoperoGeometria.cuerpo(r, i), arriba = true) }
        }
    }

    /** Alto de cada fila de cotas de columnas. */
    private val FILA_DP = 18f

    private var filasDeColumnas: List<List<ElementoRopero>> = emptyList()

    /**
     * La fila de columnas de abajo: las columnas hoja de cada cuerpo (bajando por el casillero
     * partido más bajo de cada uno, hasta las que ya no están partidas), o el cuerpo entero si no
     * está partido. Vacío si nada está partido.
     */
    private fun filasDeColumnas(r: Ropero): List<List<ElementoRopero>> {
        val elementos = RoperoGeometria.elementos(r)
        var algunaPartida = false
        fun hojas(el: ElementoRopero): List<ElementoRopero> {
            val c = r.cuerpoEn(el.cuerpo, el.ruta)
            val k = c?.partes?.keys?.minOrNull() ?: return listOf(el)
            val columnas = elementos.filter { it.tipo == TipoElemento.COLUMNA && it.cuerpo == el.cuerpo && it.ruta == el.ruta + listOf(k, it.indice) }
            if (columnas.isEmpty()) return listOf(el)
            if (columnas.size > 1) algunaPartida = true
            return columnas.flatMap { hojas(it) }
        }
        val fila = r.cuerpos.indices.mapNotNull { RoperoGeometria.cuerpo(r, it) }.flatMap { hojas(it) }
        return if (algunaPartida) listOf(fila) else emptyList()
    }

    /** Dónde quedó pintada cada cota de ancho (de un cuerpo o una columna), para saber cuál se tocó. */
    private val cotasDeCuerpos = mutableListOf<Pair<android.graphics.RectF, ElementoRopero>>()

    /** Una cota de ancho entre [x0] y [x1] (cm) a la altura [yPx], con su número si cabe, que se toca para elegir [el]. */
    private fun cotaDeAncho(canvas: Canvas, x0: Float, x1: Float, yPx: Float, el: ElementoRopero?, arriba: Boolean = false) {
        canvas.drawLine(x(x0), yPx, x(x1), yPx, pCota)
        canvas.drawLine(x(x0), yPx - 4 * dp, x(x0), yPx + 4 * dp, pCota)
        canvas.drawLine(x(x1), yPx - 4 * dp, x(x1), yPx + 4 * dp, pCota)
        // El número solo si cabe en su tramo: en la ficha chica se pisaban unos con otros.
        val texto = fmt(x1 - x0)
        if (pTexto.measureText(texto) < (x1 - x0) * escala - 2 * dp) canvas.drawText(texto, x((x0 + x1) / 2f), if (arriba) yPx - 3 * dp else yPx + 11 * dp, pTexto)
        if (el != null) cotasDeCuerpos.add(android.graphics.RectF(x(x0), yPx - 10 * dp, x(x1), yPx + 16 * dp) to el)
    }

    /** Dentro del mueble: el alto de cada cajón, y a qué altura del piso va cada repisa y el tubo. */
    private fun dibujarCotasDeElementos(canvas: Canvas) {
        val r = ropero
        val piso = RoperoGeometria.pisoY(r)
        val chico = Paint(pTexto).apply { textSize = 9f * dp; textAlign = Paint.Align.LEFT }
        val derecha = Paint(pTexto).apply { textSize = 9f * dp; textAlign = Paint.Align.RIGHT }
        cotasDeTrozos.clear()
        RoperoGeometria.elementos(r).forEach { el ->
            when (el.tipo) {
                // El cajón: el alto de su caja y, si el frente es más alto (espacio fijo), el del frente entre paréntesis.
                TipoElemento.CAJON -> {
                    val c = r.cuerpoEn(el.cuerpo, el.ruta)
                    val hueco = RoperoGeometria.huecoDe(r, el.cuerpo, el.ruta)
                    val caja = if (c != null && hueco != null) RoperoGeometria.altosDeCajas(r, c, hueco).getOrNull(el.indice) else null
                    val frente = el.y1 - el.y0
                    val texto = if (caja != null && kotlin.math.abs(caja - frente) > 0.05f) fmt(caja) + " (" + fmt(frente) + ")" else fmt(frente)
                    canvas.drawText(texto, x(el.x0) + 3 * dp, y((el.y0 + el.y1) / 2f) + 3 * dp, chico)
                }
                // La altura de repisas y tubo desde el piso, solo donde cabe (en las columnas angostas se pisaban con las cotas).
                TipoElemento.ENTREPANO -> if ((el.x1 - el.x0) * escala > 70 * dp) canvas.drawText("↑" + fmt(el.y0 - piso), x(el.x0) + 3 * dp, y(el.y1) - 2 * dp, chico)
                TipoElemento.TUBO -> if ((el.x1 - el.x0) * escala > 70 * dp) canvas.drawText("↑" + fmt((el.y0 + el.y1) / 2f - piso), x(el.x0) + 3 * dp, y(el.y1) - 2 * dp, chico)
                // El alto libre de cada trozo: una cota vertical pegada al canto derecho, que se toca para escribirla.
                TipoElemento.CASILLERO, TipoElemento.COLGADOR, TipoElemento.MALETERO, TipoElemento.ZONA_CAJONES -> {
                    val cx = x(el.x1) - 9 * dp
                    val y0 = y(el.y0); val y1 = y(el.y1)
                    if (y0 - y1 < 12 * dp) return@forEach
                    canvas.drawLine(cx, y1 + 1 * dp, cx, y0 - 1 * dp, pCota)
                    canvas.drawLine(cx - 3 * dp, y1 + 1 * dp, cx + 3 * dp, y1 + 1 * dp, pCota)
                    canvas.drawLine(cx - 3 * dp, y0 - 1 * dp, cx + 3 * dp, y0 - 1 * dp, pCota)
                    val texto = fmt(el.y1 - el.y0)
                    val ty = (y0 + y1) / 2f + 3 * dp
                    val ancho = derecha.measureText(texto)
                    // Un fondo blanco bajo el número, para que se lea sobre los cajones y el tubo.
                    canvas.drawRect(cx - 4 * dp - ancho - 2 * dp, ty - 9 * dp, cx - 2 * dp, ty + 2 * dp, pFondoCota)
                    canvas.drawText(texto, cx - 3 * dp, ty, derecha)
                    cotasDeTrozos.add(android.graphics.RectF(cx - 3 * dp - ancho - 6 * dp, ty - 14 * dp, cx + 6 * dp, ty + 6 * dp) to el)
                }
                else -> Unit
            }
        }
    }

    /** La cota de alto de qué trozo hay bajo ese punto del lienzo, si hay una. */
    fun cotaDeTrozoEn(px: Float, py: Float): ElementoRopero? =
        if (en3d || !conCotasDeElementos) null else cotasDeTrozos.firstOrNull { it.first.contains(px, py) }?.second

    /** Al décimo, sin el .0 de los enteros: 20.000002 sale "20", 20.05 sale "20.1". */
    private fun fmt(v: Float): String {
        val d = kotlin.math.round(v * 10f) / 10f
        return if (d == d.toInt().toFloat()) d.toInt().toString() else String.format(java.util.Locale.US, "%.1f", d)
    }

    private var xInicio = 0f
    private var yInicio = 0f
    private var arrastrando: ElementoRopero? = null
    private var movido = false
    // La pulsación larga se detecta aquí, porque al quedarse con el toque la vista no deja que
    // Android la detecte sola: es lo que abre la pantalla de diseño desde la ficha.
    private var pulsacionLarga = false
    private val avisarPulsacionLarga = Runnable { pulsacionLarga = true; performLongClick() }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                xInicio = event.x; yInicio = event.y; movido = false; pulsacionLarga = false
                removeCallbacks(avisarPulsacionLarga)
                if (isLongClickable) postDelayed(avisarPulsacionLarga, 450L)
                // Solo se arrastra lo que se puede mover: repisas y cajones, y solo si alguien escucha.
                arrastrando = if (alArrastrar != null) elementoEn(event.x, event.y)?.takeIf { it.tipo == TipoElemento.ENTREPANO || it.tipo == TipoElemento.CAJON } else null
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (kotlin.math.hypot(event.x - xInicio, event.y - yInicio) > 12 * dp) removeCallbacks(avisarPulsacionLarga)
                val el = arrastrando ?: return true
                if (!movido && kotlin.math.hypot(event.x - xInicio, event.y - yInicio) > 8 * dp) { movido = true; removeCallbacks(avisarPulsacionLarga) }
                if (movido) alArrastrar?.invoke(el, (event.x - origenX) / escala, (origenY - event.y) / escala)
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                removeCallbacks(avisarPulsacionLarga)
                if (pulsacionLarga) { arrastrando = null; return true }
                if (movido) { alSoltarArrastre?.invoke(); arrastrando = null; return true }
                arrastrando = null
                if (kotlin.math.hypot(event.x - xInicio, event.y - yInicio) > 12 * dp) return true
                cuerpoEn(event.x)?.let { alTocarCuerpo?.invoke(it) }
                // En la cota de un cuerpo o columna se elige entero; dentro del mueble, lo que haya.
                val deCota = elementoEnCota(event.x, event.y)
                val cotaDeTrozo = cotaDeTrozoEn(event.x, event.y)
                if (deCota != null) alTocarElemento?.invoke(deCota)
                else if (cotaDeTrozo != null && alTocarCota != null) alTocarCota?.invoke(cotaDeTrozo)
                else alTocarElemento?.invoke(elementoEn(event.x, event.y))
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    /** Qué hay bajo ese punto del lienzo, en el dibujo de frente; null fuera del mueble o en 3D. */
    fun elementoEn(px: Float, py: Float): ElementoRopero? {
        if (en3d) return null
        return RoperoGeometria.elementoEn(ropero, (px - origenX) / escala, (origenY - py) / escala)
    }

    /** El cuerpo o la columna cuya cota de ancho está bajo ese punto; null si ahí no hay cota. */
    fun elementoEnCota(px: Float, py: Float): ElementoRopero? =
        if (en3d) null else cotasDeCuerpos.firstOrNull { it.first.contains(px, py) }?.second

    /** Dónde cae en el lienzo la cota del cuerpo [i]: para las pruebas. */
    @androidx.annotation.VisibleForTesting
    fun puntoDeCotaDeCuerpo(i: Int): Pair<Float, Float> {
        val rect = cotasDeCuerpos.firstOrNull { it.second.tipo == TipoElemento.CUERPO && it.second.cuerpo == i }?.first ?: return 0f to 0f
        return rect.centerX() to rect.centerY()
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
