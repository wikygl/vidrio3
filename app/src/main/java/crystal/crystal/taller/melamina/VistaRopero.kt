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
        val fondoPapelX = if (en3d) r.fondoCm * dibujo.oblicuoX else 0f
        val fondoPapelY = if (en3d) -r.fondoCm * dibujo.oblicuoY else 0f
        val altoMayor = r.altoMayorCm
        escala = min(
            (width - 2 * margen) / (r.anchoCm + fondoPapelX),
            (height - 2 * margen) / (altoMayor + fondoPapelY)
        )
        origenX = (width - (r.anchoCm + fondoPapelX) * escala) / 2f
        origenY = height - (height - (altoMayor + fondoPapelY) * escala) / 2f
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
        // Ancho total, arriba.
        val yA = y(r.altoMayorCm) - sep
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
        // Cada cuerpo, abajo.
        val yB = y(0f) + sep
        var cx = r.espesorCm
        r.cuerpos.forEach { c ->
            canvas.drawLine(x(cx), yB, x(cx + c.anchoCm), yB, pCota)
            canvas.drawLine(x(cx), yB - 4 * dp, x(cx), yB + 4 * dp, pCota)
            canvas.drawLine(x(cx + c.anchoCm), yB - 4 * dp, x(cx + c.anchoCm), yB + 4 * dp, pCota)
            // El número solo si cabe en su tramo: en la ficha chica se pisaban unos con otros.
            val texto = fmt(c.anchoCm)
            if (pTexto.measureText(texto) < c.anchoCm * escala - 2 * dp) canvas.drawText(texto, x(cx + c.anchoCm / 2f), yB + 11 * dp, pTexto)
            cx += c.anchoCm + r.espesorCm
        }
    }

    /** Dentro del mueble: el alto de cada cajón, y a qué altura del piso va cada repisa y el tubo. */
    private fun dibujarCotasDeElementos(canvas: Canvas) {
        val r = ropero
        val piso = RoperoGeometria.pisoY(r)
        val chico = Paint(pTexto).apply { textSize = 9f * dp; textAlign = Paint.Align.LEFT }
        RoperoGeometria.elementos(r).forEach { el ->
            when (el.tipo) {
                TipoElemento.CAJON -> canvas.drawText(fmt(el.y1 - el.y0), x(el.x0) + 3 * dp, y((el.y0 + el.y1) / 2f) + 3 * dp, chico)
                TipoElemento.ENTREPANO -> canvas.drawText("↑" + fmt(el.y0 - piso), x(el.x0) + 3 * dp, y(el.y1) - 2 * dp, chico)
                TipoElemento.TUBO -> canvas.drawText("↑" + fmt((el.y0 + el.y1) / 2f - piso), x(el.x0) + 3 * dp, y(el.y1) - 2 * dp, chico)
                else -> Unit
            }
        }
    }

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
                // En la cota de un cuerpo se elige el cuerpo entero; dentro del mueble, lo que haya.
                val cuerpoDeCota = cuerpoEnCota(event.x, event.y)
                if (cuerpoDeCota != null) alTocarElemento?.invoke(RoperoGeometria.cuerpo(ropero, cuerpoDeCota))
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

    /** Qué cuerpo tiene su cota (la de abajo, con su ancho) bajo ese punto; null si el punto no está en la franja de las cotas. */
    fun cuerpoEnCota(px: Float, py: Float): Int? {
        if (en3d) return null
        val yB = y(0f) + 14f * dp
        if (py < yB - 10f * dp || py > yB + 22f * dp) return null
        return cuerpoEn(px)
    }

    /** Dónde cae en el lienzo la cota del cuerpo [i]: para las pruebas. */
    @androidx.annotation.VisibleForTesting
    fun puntoDeCotaDeCuerpo(i: Int): Pair<Float, Float> {
        var cx = ropero.espesorCm
        ropero.cuerpos.take(i).forEach { cx += it.anchoCm + ropero.espesorCm }
        return x(cx + ropero.cuerpos[i].anchoCm / 2f) to y(0f) + 14f * dp
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
