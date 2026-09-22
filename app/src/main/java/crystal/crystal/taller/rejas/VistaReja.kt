package crystal.crystal.taller.rejas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/**
 * La reja pintada tal como se corta: cada tubo de [RejaCalculo.todos] con su grueso, sobre el
 * hueco en blanco, con las cotas del ancho y el alto. Sirve para los modelos que la grilla no
 * sabe dibujar (rombos, espina); la cuadrícula y los barrotes siguen en la grilla editable.
 */
class VistaReja @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var reja: RejaCalculo.Reja = RejaCalculo.Reja(100f, 160f)
        set(value) { field = value; invalidate() }

    private val dp = resources.displayMetrics.density
    private val pTubo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(60, 60, 60); style = Paint.Style.STROKE; strokeCap = Paint.Cap.BUTT }
    private val pTuboDiagonal = Paint(pTubo).apply { strokeCap = Paint.Cap.SQUARE }
    private val pFondo = Paint().apply { color = Color.WHITE; style = Paint.Style.FILL }
    private val pCota = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); style = Paint.Style.STROKE; strokeWidth = 1f * dp }
    private val pTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); textSize = 10f * dp; textAlign = Paint.Align.CENTER }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r = reja
        val margen = 22f * dp
        val escala = min((width - 2 * margen) / r.anchoCm, (height - 2 * margen) / r.altoCm)
        val izq = (width - r.anchoCm * escala) / 2f
        val abajo = height - (height - r.altoCm * escala) / 2f
        fun x(cm: Float) = izq + cm * escala
        fun y(cm: Float) = abajo - cm * escala
        canvas.drawRect(x(0f), y(r.altoCm), x(r.anchoCm), y(0f), pFondo)
        RejaCalculo.todos(r).forEach { t ->
            val p = if (t.a45) pTuboDiagonal else pTubo
            p.strokeWidth = (if (t.nombre == "Marco") r.marcoCm else r.tuboCm) * escala
            canvas.drawLine(x(t.x0), y(t.y0), x(t.x1), y(t.y1), p)
        }
        // Las cotas: el ancho arriba y el alto a la izquierda.
        val yA = y(r.altoCm) - 8 * dp
        canvas.drawLine(x(0f), yA, x(r.anchoCm), yA, pCota)
        canvas.drawText(RejaCalculo.fmt(r.anchoCm), x(r.anchoCm / 2f), yA - 3 * dp, pTexto)
        val xA = x(0f) - 8 * dp
        canvas.drawLine(xA, y(0f), xA, y(r.altoCm), pCota)
        canvas.save()
        canvas.rotate(-90f, xA - 3 * dp, y(r.altoCm / 2f))
        canvas.drawText(RejaCalculo.fmt(r.altoCm), xA - 3 * dp, y(r.altoCm / 2f), pTexto)
        canvas.restore()
    }
}
