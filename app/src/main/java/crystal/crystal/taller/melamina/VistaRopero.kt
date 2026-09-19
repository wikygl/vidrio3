package crystal.crystal.taller.melamina

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

/**
 * El ropero dibujado: de frente, con lo que lleva cada cuerpo (colgador con sus ganchos,
 * entrepaños, cajones con su tirador, la repisa del maletero), con o sin puertas; o en 3D
 * oblicuo, con el fondo del mueble saliendo hacia arriba a la derecha.
 *
 * Todo se dibuja en centímetros del mueble y se escala al lienzo. Tocar un cuerpo avisa por
 * [alTocarCuerpo] con su índice: es como se abre lo que lleva dentro.
 */
class VistaRopero @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var ropero: Ropero = Ropero()
        set(value) { field = value; invalidate() }

    var mostrarPuertas: Boolean = false
        set(value) { field = value; invalidate() }

    var en3d: Boolean = false
        set(value) { field = value; invalidate() }

    var alTocarCuerpo: ((Int) -> Unit)? = null

    private val dp = resources.displayMetrics.density
    private val pTablero = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#D9B98C"); style = Paint.Style.FILL }
    private val pTableroLado = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#C4A473"); style = Paint.Style.FILL }
    private val pTableroTecho = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#E8CDA4"); style = Paint.Style.FILL }
    private val pInterior = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F5EBDD"); style = Paint.Style.FILL }
    private val pBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#5A4632"); style = Paint.Style.STROKE; strokeWidth = 1.5f * dp }
    private val pLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#5A4632"); style = Paint.Style.STROKE; strokeWidth = 1f * dp }
    private val pTubo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#7A7A7A"); style = Paint.Style.STROKE; strokeWidth = 3f * dp; strokeCap = Paint.Cap.ROUND }
    private val pGancho = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#9A9A9A"); style = Paint.Style.STROKE; strokeWidth = 1.2f * dp }
    private val pPuerta = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#CFAE7F"); style = Paint.Style.FILL }
    private val pPuertaCorrediza = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#B8D9B8"); style = Paint.Style.FILL; alpha = 215 }
    private val pTirador = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#333333"); style = Paint.Style.FILL }
    private val pCota = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); style = Paint.Style.STROKE; strokeWidth = 1f * dp }
    private val pTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); textSize = 11f * dp; textAlign = Paint.Align.CENTER }
    private val pRotulo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#6D5A45"); textSize = 10f * dp; textAlign = Paint.Align.CENTER }
    private val pSeleccion = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1E88E5"); style = Paint.Style.STROKE; strokeWidth = 2.5f * dp }

    /** El cuerpo resaltado (el último tocado), o -1. */
    var cuerpoResaltado: Int = -1
        set(value) { field = value; invalidate() }

    // La escala y el origen del último dibujo, para saber qué se tocó.
    private var escala = 1f
    private var origenX = 0f
    private var origenY = 0f
    /** Profundidad en el papel del 3D oblicuo: por cada cm de fondo, cuánto sube y se corre. */
    private val oblicuoX = 0.5f
    private val oblicuoY = -0.35f

    private fun x(cm: Float) = origenX + cm * escala
    private fun y(cm: Float) = origenY - cm * escala   // el alto sube

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val r = ropero
        val margen = 34f * dp
        val fondoPapelX = if (en3d) r.fondoCm * oblicuoX else 0f
        val fondoPapelY = if (en3d) -r.fondoCm * oblicuoY else 0f
        escala = min(
            (width - 2 * margen) / (r.anchoCm + fondoPapelX),
            (height - 2 * margen) / (r.altoCm + fondoPapelY)
        )
        origenX = (width - (r.anchoCm + fondoPapelX) * escala) / 2f
        origenY = height - (height - (r.altoCm + fondoPapelY) * escala) / 2f

        if (en3d) dibujarCaras3d(canvas)
        dibujarFrente(canvas)
        if (!en3d) dibujarCotas(canvas)
    }

    /** Las caras del fondo del mueble en el 3D oblicuo: el costado derecho y el techo. */
    private fun dibujarCaras3d(canvas: Canvas) {
        val r = ropero
        val dx = r.fondoCm * oblicuoX * escala
        val dy = r.fondoCm * oblicuoY * escala
        // Costado derecho.
        canvas.drawPath(Path().apply {
            moveTo(x(r.anchoCm), y(0f)); lineTo(x(r.anchoCm) + dx, y(0f) + dy)
            lineTo(x(r.anchoCm) + dx, y(r.altoCm) + dy); lineTo(x(r.anchoCm), y(r.altoCm)); close()
        }, pTableroLado)
        canvas.drawPath(Path().apply {
            moveTo(x(r.anchoCm), y(0f)); lineTo(x(r.anchoCm) + dx, y(0f) + dy)
            lineTo(x(r.anchoCm) + dx, y(r.altoCm) + dy); lineTo(x(r.anchoCm), y(r.altoCm)); close()
        }, pBorde)
        // Techo.
        canvas.drawPath(Path().apply {
            moveTo(x(0f), y(r.altoCm)); lineTo(x(0f) + dx, y(r.altoCm) + dy)
            lineTo(x(r.anchoCm) + dx, y(r.altoCm) + dy); lineTo(x(r.anchoCm), y(r.altoCm)); close()
        }, pTableroTecho)
        canvas.drawPath(Path().apply {
            moveTo(x(0f), y(r.altoCm)); lineTo(x(0f) + dx, y(r.altoCm) + dy)
            lineTo(x(r.anchoCm) + dx, y(r.altoCm) + dy); lineTo(x(r.anchoCm), y(r.altoCm)); close()
        }, pBorde)
    }

    private fun dibujarFrente(canvas: Canvas) {
        val r = ropero
        val e = r.espesorCm
        // El hueco interior, claro, y encima el armazón.
        canvas.drawRect(x(0f), y(r.altoCm), x(r.anchoCm), y(0f), pInterior)
        // Zócalo.
        canvas.drawRect(x(e), y(r.zocaloCm), x(r.anchoCm - e), y(0f), pTablero)
        // Piso y techo.
        canvas.drawRect(x(e), y(r.zocaloCm + e), x(r.anchoCm - e), y(r.zocaloCm), pTablero)
        canvas.drawRect(x(e), y(r.altoCm), x(r.anchoCm - e), y(r.altoCm - e), pTablero)
        // Laterales.
        canvas.drawRect(x(0f), y(r.altoCm), x(e), y(0f), pTablero)
        canvas.drawRect(x(r.anchoCm - e), y(r.altoCm), x(r.anchoCm), y(0f), pTablero)

        val pisoY = r.zocaloCm + e            // cara de arriba del piso
        val techoY = r.altoCm - e             // cara de abajo del techo
        var cx = e
        r.cuerpos.forEachIndexed { i, c ->
            val izq = cx
            val der = cx + c.anchoCm
            // Lo de dentro se queda dentro del cuerpo: los ganchos no asoman por las divisiones.
            canvas.save()
            canvas.clipRect(x(izq), y(techoY), x(der), y(pisoY))
            dibujarCuerpo(canvas, c, izq, der, pisoY, techoY)
            canvas.restore()
            if (i == cuerpoResaltado) {
                canvas.drawRect(x(izq) + dp, y(techoY) + dp, x(der) - dp, y(pisoY) - dp, pSeleccion)
            }
            // La división que sigue.
            if (i < r.cuerpos.size - 1) canvas.drawRect(x(der), y(techoY), x(der + e), y(pisoY), pTablero)
            cx = der + e
        }
        // El rótulo de cada cuerpo va en el zócalo, que ahí no tapa nada.
        cx = e
        r.cuerpos.forEach { c ->
            canvas.drawText(c.tipo.etiqueta, x(cx + c.anchoCm / 2f), y(r.zocaloCm / 2f) + 4f * dp, pRotulo)
            cx += c.anchoCm + e
        }
        if (mostrarPuertas) dibujarPuertas(canvas)
        canvas.drawRect(x(0f), y(r.altoCm), x(r.anchoCm), y(0f), pBorde)
    }

    private fun dibujarCuerpo(canvas: Canvas, c: Cuerpo, izq: Float, der: Float, pisoY: Float, techoY: Float) {
        val r = ropero
        val e = r.espesorCm
        var topeBajo = techoY
        // La repisa del maletero.
        if (r.maleteroCm > 0f) {
            val repisaY = techoY - r.maleteroCm
            canvas.drawRect(x(izq), y(repisaY), x(der), y(repisaY - e), pTablero)
            topeBajo = repisaY - e
        }
        val centroX = (izq + der) / 2f
        // Cajones, desde el piso.
        var baseY = pisoY
        val cajones = c.cajonesEfectivos
        for (k in 0 until cajones) {
            val arriba = baseY + r.altoCajonCm
            val rect = RectF(x(izq + 0.4f), y(arriba - 0.4f), x(der - 0.4f), y(baseY + 0.2f))
            canvas.drawRect(rect, pPuerta)
            canvas.drawRect(rect, pLinea)
            canvas.drawRect(x(centroX - 6f), y(baseY + r.altoCajonCm / 2f + 0.8f), x(centroX + 6f), y(baseY + r.altoCajonCm / 2f - 0.8f), pTirador)
            baseY = arriba
        }
        // El colgador: el tubo a 6 cm del tope, con unos ganchos.
        if (c.llevaTubo) {
            val tuboY = topeBajo - 6f
            canvas.drawLine(x(izq + 2f), y(tuboY), x(der - 2f), y(tuboY), pTubo)
            val largo = der - izq
            val ganchos = (largo / 12f).toInt().coerceIn(1, 8)
            for (g in 0 until ganchos) {
                val gx = izq + largo * (g + 0.5f) / ganchos
                canvas.drawLine(x(gx), y(tuboY), x(gx), y(tuboY - 4f), pGancho)
                canvas.drawLine(x(gx - 9f), y(tuboY - 9f), x(gx), y(tuboY - 4f), pGancho)
                canvas.drawLine(x(gx), y(tuboY - 4f), x(gx + 9f), y(tuboY - 9f), pGancho)
                canvas.drawLine(x(gx - 9f), y(tuboY - 9f), x(gx - 7f), y(tuboY - 40f), pGancho)
                canvas.drawLine(x(gx + 9f), y(tuboY - 9f), x(gx + 7f), y(tuboY - 40f), pGancho)
                canvas.drawLine(x(gx - 7f), y(tuboY - 40f), x(gx + 7f), y(tuboY - 40f), pGancho)
            }
        }
        // Entrepaños: repartidos en lo que queda. En el colgador van abajo, debajo de la ropa
        // (la repisa de los zapatos), cada 30 cm desde el piso.
        val entrepanos = c.entrepanosEfectivos
        if (entrepanos > 0) {
            if (c.tipo == TipoCuerpo.COLGAR) {
                for (k in 0 until entrepanos) {
                    val ey = baseY + 30f * (k + 1)
                    if (ey < topeBajo - 6f - 45f) canvas.drawRect(x(izq), y(ey + e), x(der), y(ey), pTablero)
                }
            } else {
                val desde = baseY
                val hasta = if (c.llevaTubo) topeBajo - 6f - 45f else topeBajo
                val paso = (hasta - desde) / (entrepanos + 1)
                for (k in 1..entrepanos) {
                    val ey = desde + paso * k
                    canvas.drawRect(x(izq), y(ey + e), x(der), y(ey), pTablero)
                }
            }
        }
    }

    private fun dibujarPuertas(canvas: Canvas) {
        val r = ropero
        val e = r.espesorCm
        when (r.puertas) {
            TipoPuertas.SIN -> Unit
            TipoPuertas.BATIENTES -> {
                var cx = e
                r.cuerpos.forEach { c ->
                    val luz = c.anchoCm + e
                    val izq = cx - e / 2f
                    val hojas = if (luz <= 60f) 1 else 2
                    val ancho = luz / hojas
                    val abajo = r.zocaloCm
                    val cortes = if (r.maleteroCm > 0f) listOf(abajo to r.altoCm - r.maleteroCm - e, r.altoCm - r.maleteroCm - e to r.altoCm) else listOf(abajo to r.altoCm)
                    cortes.forEach { (y0, y1) ->
                        for (h in 0 until hojas) {
                            val px0 = izq + h * ancho + 0.3f
                            val px1 = izq + (h + 1) * ancho - 0.3f
                            val rect = RectF(x(px0), y(y1 - 0.3f), x(px1), y(y0 + 0.3f))
                            canvas.drawRect(rect, pPuerta)
                            canvas.drawRect(rect, pLinea)
                            // El tirador junto al canto de abrir: en una hoja sola a la derecha; en dos, al medio.
                            val tx = if (hojas == 1) px1 - 4f else if (h == 0) px1 - 3f else px0 + 3f
                            val ty = (y0 + y1) / 2f
                            canvas.drawRect(x(tx - 0.8f), y(ty + 6f), x(tx + 0.8f), y(ty - 6f), pTirador)
                        }
                    }
                    cx += c.anchoCm + e
                }
            }
            TipoPuertas.CORREDIZAS -> {
                val hojas = RoperoCalculo.hojasCorredizas(r.anchoCm)
                val wi = r.anchoInteriorCm
                val ancho = (wi + (hojas - 1) * 5f) / hojas
                val y0 = r.zocaloCm + e + 1.5f
                val y1 = r.altoCm - e - 2f
                for (h in 0 until hojas) {
                    val px0 = e + h * (ancho - 5f)
                    val rect = RectF(x(px0), y(y1), x(px0 + ancho), y(y0))
                    // Las hojas alternas van un tono más oscuro: se ve cuál corre por delante.
                    pPuertaCorrediza.color = if (h % 2 == 0) Color.parseColor("#B8D9B8") else Color.parseColor("#9FC79F")
                    pPuertaCorrediza.alpha = 225
                    canvas.drawRect(rect, pPuertaCorrediza)
                    canvas.drawRect(rect, pLinea)
                    val tx = if (h % 2 == 0) px0 + ancho - 5f else px0 + 5f
                    canvas.drawRect(x(tx - 0.8f), y((y0 + y1) / 2f + 7f), x(tx + 0.8f), y((y0 + y1) / 2f - 7f), pTirador)
                }
            }
        }
    }

    private fun dibujarCotas(canvas: Canvas) {
        val r = ropero
        val sep = 14f * dp
        // Ancho total, arriba.
        val yA = y(r.altoCm) - sep
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
            canvas.drawText(fmt(c.anchoCm), x(cx + c.anchoCm / 2f), yB + 11 * dp, pTexto)
            cx += c.anchoCm + r.espesorCm
        }
    }

    private fun fmt(v: Float): String =
        if (v == v.toInt().toFloat()) v.toInt().toString() else String.format(java.util.Locale.US, "%.1f", v)

    private var xInicio = 0f
    private var yInicio = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> { xInicio = event.x; yInicio = event.y; return true }
            MotionEvent.ACTION_UP -> {
                if (kotlin.math.hypot(event.x - xInicio, event.y - yInicio) > 12 * dp) return true
                cuerpoEn(event.x)?.let { alTocarCuerpo?.invoke(it) }
                return true
            }
        }
        return super.onTouchEvent(event)
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
