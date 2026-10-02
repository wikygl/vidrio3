package crystal.crystal.optimizadores

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * El vidrio curvo visto en planta, para ver y corregir sus caras (pedido del usuario el 2026-10-01).
 *
 * - Abajo el **interior**, arriba el **exterior**: se mira como se mide, desde adentro.
 * - El arco con la panza hacia donde la tiene el vidrio: hacia afuera sube, hacia adentro baja.
 * - Encima del arco, del lado de afuera, el nombre de la cara que queda afuera (espejo o color). Al
 *   tocarlo pasa a la otra ([alAlternar]); la clasificación (cóncava o convexa) cambia con ella.
 *
 * La flecha se dibuja en su proporción con la cuerda, con un mínimo para que la panza se vea siempre.
 */
class VistaCaraCurvo(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var vidrio: ListaVidriosCurvos.VidrioCurvo? = null

    /** Se llama al tocar el nombre de la cara de afuera. */
    var alAlternar: (() -> Unit)? = null

    /** Dónde quedó el nombre de la cara de afuera, para saber si se lo tocó. */
    private val zonaCara = RectF()

    private val dp = resources.displayMetrics.density

    private val pFondoExterior = Paint().apply { color = Color.rgb(232, 244, 252) }
    private val pFondoInterior = Paint().apply { color = Color.rgb(250, 245, 236) }
    private val pZona = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(96, 125, 139); textSize = 11f * dp; isFakeBoldText = true
    }
    private val pEspejo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(120, 130, 140); style = Paint.Style.STROKE; strokeWidth = 3.5f * dp; strokeCap = Paint.Cap.ROUND
    }
    private val pColor = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(21, 158, 194); style = Paint.Style.STROKE; strokeWidth = 3.5f * dp; strokeCap = Paint.Cap.ROUND
    }
    private val pVidrio = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(200, 228, 240); style = Paint.Style.STROKE; strokeWidth = 7f * dp; strokeCap = Paint.Cap.BUTT
    }
    private val pCara = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE; textSize = 14f * dp; isFakeBoldText = true; textAlign = Paint.Align.CENTER
    }
    private val pBotonCara = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(21, 158, 194) }
    private val pTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(55, 71, 79); textSize = 12f * dp; textAlign = Paint.Align.CENTER
    }

    fun mostrar(v: ListaVidriosCurvos.VidrioCurvo?) {
        vidrio = v
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val w = width.toFloat()
        val h = height.toFloat()
        val medio = h / 2f
        canvas.drawRect(0f, 0f, w, medio, pFondoExterior)
        canvas.drawRect(0f, medio, w, h, pFondoInterior)
        canvas.drawText("EXTERIOR", 8f * dp, 16f * dp, pZona)
        canvas.drawText("INTERIOR", 8f * dp, h - 8f * dp, pZona)

        val v = vidrio ?: run {
            canvas.drawText("Toque largo en un vidrio de la lista", w / 2f, medio + 4f * dp, pTexto)
            zonaCara.setEmpty()
            return
        }

        // El arco: la cuerda a lo ancho, la flecha en su proporción (con un mínimo para que se vea).
        val haciaAfuera = v.panza != "adentro"
        val cuerda = v.cuerda ?: v.desarrollo
        val flecha = v.flecha ?: 0f
        val ancho = w * 0.7f
        val flechaPx = (if (cuerda > 0f) ancho * flecha / cuerda else 0f).coerceIn(10f * dp, h * 0.22f)
        val x0 = (w - ancho) / 2f
        val x1 = x0 + ancho
        val yPuntas = medio + (if (haciaAfuera) flechaPx / 2f else -flechaPx / 2f)
        val yPanza = yPuntas + (if (haciaAfuera) -flechaPx else flechaPx)
        // La cara de afuera es la línea de arriba del vidrio y la de adentro, la de abajo.
        val arcoAfuera = arco(x0, x1, yPuntas, yPanza, -3f * dp)
        val arcoAdentro = arco(x0, x1, yPuntas, yPanza, 3f * dp)

        canvas.drawPath(arco(x0, x1, yPuntas, yPanza, 0f), pVidrio)
        val especial = ListaVidriosCurvos.caraEspecial(v)
        val afuera = ListaVidriosCurvos.caraAfuera(v)
        if (especial != null && afuera != null) {
            // La cara de afuera y la de adentro, cada una con su color: espejo gris, color celeste.
            val espejoAfuera = afuera == especial
            canvas.drawPath(arcoAfuera, if (espejoAfuera) pEspejo else pColor)
            canvas.drawPath(arcoAdentro, if (espejoAfuera) pColor else pEspejo)
        }

        // El nombre de la cara de afuera, encima del arco: se toca para cambiarla.
        val nombre = (afuera ?: "sin caras").uppercase()
        val anchoTexto = pCara.measureText(nombre) + 24f * dp
        val yTexto = min(yPuntas, yPanza) - 14f * dp
        zonaCara.set(w / 2f - anchoTexto / 2f, yTexto - 20f * dp, w / 2f + anchoTexto / 2f, yTexto + 6f * dp)
        canvas.drawRoundRect(zonaCara, 13f * dp, 13f * dp, pBotonCara)
        canvas.drawText(nombre, w / 2f, yTexto, pCara)

        // La clasificación, del lado de adentro, debajo del arco.
        val clasificacion = when {
            especial == null -> "El vidrio se puede voltear · toque para darle caras"
            ListaVidriosCurvos.esConvexa(v) == true -> "$especial CONVEXA"
            else -> "$especial CÓNCAVA"
        }
        val yClas = max(yPuntas, yPanza) + 22f * dp
        canvas.drawText("${v.ventana} · panza hacia ${if (haciaAfuera) "afuera" else "adentro"} · $clasificacion", w / 2f, yClas, pTexto)
    }

    /**
     * El arco de [x0] a [x1] por las puntas a la altura [yPuntas] y la panza en [yPanza], corrido
     * [corrimiento] en vertical (para la cara de afuera y la de adentro).
     */
    private fun arco(x0: Float, x1: Float, yPuntas: Float, yPanza: Float, corrimiento: Float): Path {
        val c = (x1 - x0) / 2f
        val f = abs(yPanza - yPuntas)
        val r = (c * c + f * f) / (2f * f)
        val arriba = yPanza < yPuntas
        val cx = (x0 + x1) / 2f
        val cy = if (arriba) yPanza + r else yPanza - r
        val medioAngulo = asin((c / r).coerceIn(-1f, 1f))
        val path = Path()
        val pasos = 40
        for (i in 0..pasos) {
            val a = -medioAngulo + 2f * medioAngulo * i / pasos
            val x = cx + r * sin(a)
            val y = if (arriba) cy - r * cos(a) else cy + r * cos(a)
            if (i == 0) path.moveTo(x, y + corrimiento) else path.lineTo(x, y + corrimiento)
        }
        return path
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (vidrio == null) return false
        if (event.action == MotionEvent.ACTION_DOWN) return zonaCara.contains(event.x, event.y)
        if (event.action == MotionEvent.ACTION_UP && zonaCara.contains(event.x, event.y)) {
            performClick()
            alAlternar?.invoke()
            return true
        }
        return false
    }

    override fun performClick(): Boolean = super.performClick()
}
