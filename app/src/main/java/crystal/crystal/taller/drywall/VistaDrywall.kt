package crystal.crystal.taller.drywall

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.floor
import kotlin.math.min

/**
 * El drywall dibujado como se arma: la estructura (rieles arriba y abajo, parantes cada
 * separación; en el cielo raso el perímetro con portantes y cruzados) o el emplanchado (las
 * planchas de 1.22 x 2.44 puestas de pie con sus juntas). Cotas del largo y el alto en metros.
 */
class VistaDrywall @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var drywall: Drywall = Drywall()
        set(value) { field = value; invalidate() }

    /** Estructura (perfiles) o planchas. */
    var mostrarPlanchas: Boolean = false
        set(value) { field = value; invalidate() }

    private val dp = resources.displayMetrics.density
    private val pFondo = Paint().apply { color = Color.WHITE; style = Paint.Style.FILL }
    private val pPerfil = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(90, 100, 110); style = Paint.Style.STROKE }
    private val pRiel = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(50, 60, 70); style = Paint.Style.STROKE }
    private val pPlancha = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(232, 226, 210); style = Paint.Style.FILL }
    private val pJunta = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(150, 140, 120); style = Paint.Style.STROKE; strokeWidth = 1.2f * dp }
    private val pBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(58, 58, 58); style = Paint.Style.STROKE; strokeWidth = 1.5f * dp }
    private val pCota = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); style = Paint.Style.STROKE; strokeWidth = 1f * dp }
    private val pTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); textSize = 10f * dp; textAlign = Paint.Align.CENTER }
    private val pVano = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(60, 30, 136, 229); style = Paint.Style.FILL }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val d = drywall
        val largo = d.largoM.coerceAtLeast(0.3f)
        val alto = d.altoM.coerceAtLeast(0.3f)
        val margen = 22f * dp
        val escala = min((width - 2 * margen) / largo, (height - 2 * margen) / alto)
        val izq = (width - largo * escala) / 2f
        val abajo = height - (height - alto * escala) / 2f
        fun x(m: Float) = izq + m * escala
        fun y(m: Float) = abajo - m * escala
        canvas.drawRect(x(0f), y(alto), x(largo), y(0f), pFondo)

        if (mostrarPlanchas) {
            // Planchas de pie, 1.22 de ancho por 2.44 de alto, con sus juntas.
            var px = 0f
            while (px < largo - 0.001f) {
                val px1 = min(px + 1.22f, largo)
                var py = 0f
                while (py < alto - 0.001f) {
                    val py1 = min(py + 2.44f, alto)
                    canvas.drawRect(x(px), y(py1), x(px1), y(py), pPlancha)
                    canvas.drawRect(x(px), y(py1), x(px1), y(py), pJunta)
                    py = py1
                }
                px = px1
            }
        } else if (d.tipo == TipoDrywall.CIELO_RASO) {
            // Visto desde abajo: el ángulo perimetral, los portantes cada 1.22 y los cruzados cada 0.61.
            pRiel.strokeWidth = 3f * dp
            pPerfil.strokeWidth = 2f * dp
            canvas.drawRect(x(0f), y(alto), x(largo), y(0f), pRiel)
            val portantes = floor(alto / 1.22f).toInt() + 1
            for (i in 1 until portantes) canvas.drawLine(x(0f), y(i * 1.22f), x(largo), y(i * 1.22f), pRiel)
            val cruzados = floor(largo / 0.61f).toInt() + 1
            for (i in 1 until cruzados) canvas.drawLine(x(i * 0.61f), y(0f), x(i * 0.61f), y(alto), pPerfil)
        } else {
            // Rieles arriba y abajo y parantes cada separación (el último pegado al final).
            val perfil = d.perfil.anchoMm / 1000f
            pRiel.strokeWidth = (perfil * escala).coerceAtLeast(3f * dp)
            pPerfil.strokeWidth = (perfil * escala).coerceAtLeast(2f * dp)
            canvas.drawLine(x(0f), y(perfil / 2f), x(largo), y(perfil / 2f), pRiel)
            canvas.drawLine(x(0f), y(alto - perfil / 2f), x(largo), y(alto - perfil / 2f), pRiel)
            val sep = d.separacionCm / 100f
            val posiciones = floor(largo / sep).toInt() + 1
            for (i in 0 until posiciones) {
                val px = min(i * sep + perfil / 2f, largo - perfil / 2f)
                canvas.drawLine(x(px), y(perfil), x(px), y(alto - perfil), pPerfil)
            }
            canvas.drawLine(x(largo - perfil / 2f), y(perfil), x(largo - perfil / 2f), y(alto - perfil), pPerfil)
            // Los vanos, como una mancha: no se sabe dónde van, solo cuánto son.
            if (d.vanosM2 > 0f && d.vanos > 0) {
                val cada = d.vanosM2 / d.vanos
                val ladoV = min(kotlin.math.sqrt(cada), alto * 0.8f)
                val anchoV = cada / ladoV
                for (v in 0 until d.vanos) {
                    val vx = (v + 1) * largo / (d.vanos + 1) - anchoV / 2f
                    canvas.drawRect(x(vx), y(ladoV), x(vx + anchoV), y(0f), pVano)
                }
            }
        }
        canvas.drawRect(x(0f), y(alto), x(largo), y(0f), pBorde)

        // Cotas en metros: el largo arriba y el alto a la izquierda.
        val yA = y(alto) - 8 * dp
        canvas.drawLine(x(0f), yA, x(largo), yA, pCota)
        canvas.drawText("${DrywallCalculo.fmt(d.largoM)} m", x(largo / 2f), yA - 3 * dp, pTexto)
        val xA = x(0f) - 8 * dp
        canvas.drawLine(xA, y(0f), xA, y(alto), pCota)
        canvas.save()
        canvas.rotate(-90f, xA - 3 * dp, y(alto / 2f))
        canvas.drawText("${DrywallCalculo.fmt(d.altoM)} m", xA - 3 * dp, y(alto / 2f), pTexto)
        canvas.restore()
    }
}
