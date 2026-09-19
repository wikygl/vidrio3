package crystal.crystal.taller.melamina

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

/**
 * El ropero pintado en centímetros sobre cualquier lienzo: la calculadora lo usa en su ficha y
 * el apunte de medidas lo usa dentro del dibujo. Quien llama dice dónde está la esquina de abajo
 * a la izquierda del mueble ([izqPx], [abajoPx]) y a cuántos píxeles va el centímetro ([escala]).
 *
 * Se pinta el alzado con lo que lleva cada cuerpo —colgador con ganchos, entrepaños, cajones con
 * tirador, la repisa del maletero—, con las puertas encima si se piden, o el 3D oblicuo con el
 * costado y el techo saliendo hacia arriba a la derecha. Las cotas las pone cada lienzo a su
 * manera: en el apunte se tocan para escribir, así que no van aquí.
 */
class RoperoDibujo(private val dp: Float) {

    val pTablero = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#D9B98C"); style = Paint.Style.FILL }
    private val pTableroLado = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#C4A473"); style = Paint.Style.FILL }
    private val pTableroTecho = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#E8CDA4"); style = Paint.Style.FILL }
    val pInterior = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F5EBDD"); style = Paint.Style.FILL }
    private val pBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#5A4632"); style = Paint.Style.STROKE; strokeWidth = 1.5f * dp }
    private val pLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#5A4632"); style = Paint.Style.STROKE; strokeWidth = 1f * dp }
    private val pTubo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#7A7A7A"); style = Paint.Style.STROKE; strokeWidth = 3f * dp; strokeCap = Paint.Cap.ROUND }
    private val pGancho = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#9A9A9A"); style = Paint.Style.STROKE; strokeWidth = 1.2f * dp }
    val pPuerta = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#CFAE7F"); style = Paint.Style.FILL }
    private val pPuertaCorrediza = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#B8D9B8"); style = Paint.Style.FILL; alpha = 225 }
    private val pTirador = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#333333"); style = Paint.Style.FILL }
    val pRotulo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#6D5A45"); textSize = 10f * dp; textAlign = Paint.Align.CENTER }
    private val pSeleccion = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1E88E5"); style = Paint.Style.STROKE; strokeWidth = 2.5f * dp }

    /** Profundidad en el papel del 3D oblicuo: por cada cm de fondo, cuánto se corre y cuánto sube. */
    val oblicuoX = 0.5f
    val oblicuoY = -0.35f

    private var izq = 0f
    private var abajo = 0f
    private var escala = 1f
    private fun x(cm: Float) = izq + cm * escala
    private fun y(cm: Float) = abajo - cm * escala

    /**
     * Pinta el ropero con la esquina de abajo a la izquierda en ([izqPx], [abajoPx]).
     * @param cuerpoResaltado el cuerpo marcado (el que se está editando), o -1.
     */
    fun dibujar(
        canvas: Canvas,
        r: Ropero,
        izqPx: Float,
        abajoPx: Float,
        escala: Float,
        mostrarPuertas: Boolean = false,
        en3d: Boolean = false,
        cuerpoResaltado: Int = -1,
        conRotulos: Boolean = true
    ) {
        this.izq = izqPx
        this.abajo = abajoPx
        this.escala = escala
        if (en3d) dibujarCaras3d(canvas, r)
        dibujarFrente(canvas, r, mostrarPuertas, cuerpoResaltado, conRotulos)
    }

    /** Las caras del fondo del mueble en el 3D oblicuo: el costado derecho y el techo. */
    private fun dibujarCaras3d(canvas: Canvas, r: Ropero) {
        val dx = r.fondoCm * oblicuoX * escala
        val dy = r.fondoCm * oblicuoY * escala
        val costado = Path().apply {
            moveTo(x(r.anchoCm), y(0f)); lineTo(x(r.anchoCm) + dx, y(0f) + dy)
            lineTo(x(r.anchoCm) + dx, y(r.altoCm) + dy); lineTo(x(r.anchoCm), y(r.altoCm)); close()
        }
        canvas.drawPath(costado, pTableroLado)
        canvas.drawPath(costado, pBorde)
        val techo = Path().apply {
            moveTo(x(0f), y(r.altoCm)); lineTo(x(0f) + dx, y(r.altoCm) + dy)
            lineTo(x(r.anchoCm) + dx, y(r.altoCm) + dy); lineTo(x(r.anchoCm), y(r.altoCm)); close()
        }
        canvas.drawPath(techo, pTableroTecho)
        canvas.drawPath(techo, pBorde)
    }

    private fun dibujarFrente(canvas: Canvas, r: Ropero, mostrarPuertas: Boolean, cuerpoResaltado: Int, conRotulos: Boolean) {
        val e = r.espesorCm
        // El hueco interior, claro, y encima el armazón.
        canvas.drawRect(x(0f), y(r.altoCm), x(r.anchoCm), y(0f), pInterior)
        canvas.drawRect(x(e), y(r.zocaloCm), x(r.anchoCm - e), y(0f), pTablero)
        canvas.drawRect(x(e), y(r.zocaloCm + e), x(r.anchoCm - e), y(r.zocaloCm), pTablero)
        canvas.drawRect(x(e), y(r.altoCm), x(r.anchoCm - e), y(r.altoCm - e), pTablero)
        canvas.drawRect(x(0f), y(r.altoCm), x(e), y(0f), pTablero)
        canvas.drawRect(x(r.anchoCm - e), y(r.altoCm), x(r.anchoCm), y(0f), pTablero)

        val pisoY = r.zocaloCm + e            // cara de arriba del piso
        val techoY = r.altoCm - e             // cara de abajo del techo
        var cx = e
        r.cuerpos.forEachIndexed { i, c ->
            val izqC = cx
            val derC = cx + c.anchoCm
            // Lo de dentro se queda dentro del cuerpo: los ganchos no asoman por las divisiones.
            canvas.save()
            canvas.clipRect(x(izqC), y(techoY), x(derC), y(pisoY))
            dibujarCuerpo(canvas, r, c, izqC, derC, pisoY, techoY)
            canvas.restore()
            if (i == cuerpoResaltado) {
                canvas.drawRect(x(izqC) + dp, y(techoY) + dp, x(derC) - dp, y(pisoY) - dp, pSeleccion)
            }
            if (i < r.cuerpos.size - 1) canvas.drawRect(x(derC), y(techoY), x(derC + e), y(pisoY), pTablero)
            cx = derC + e
        }
        if (conRotulos) {
            // El rótulo de cada cuerpo va en el zócalo, que ahí no tapa nada; solo si cabe.
            cx = e
            r.cuerpos.forEach { c ->
                if (pRotulo.measureText(c.tipo.etiqueta) < (c.anchoCm - 4f) * escala) {
                    canvas.drawText(c.tipo.etiqueta, x(cx + c.anchoCm / 2f), y(r.zocaloCm / 2f) + 4f * dp, pRotulo)
                }
                cx += c.anchoCm + e
            }
        }
        if (mostrarPuertas) dibujarPuertas(canvas, r)
        canvas.drawRect(x(0f), y(r.altoCm), x(r.anchoCm), y(0f), pBorde)
    }

    private fun dibujarCuerpo(canvas: Canvas, r: Ropero, c: Cuerpo, izqC: Float, derC: Float, pisoY: Float, techoY: Float) {
        val e = r.espesorCm
        var topeBajo = techoY
        if (r.maleteroCm > 0f) {
            val repisaY = techoY - r.maleteroCm
            canvas.drawRect(x(izqC), y(repisaY), x(derC), y(repisaY - e), pTablero)
            topeBajo = repisaY - e
        }
        val centroX = (izqC + derC) / 2f
        // Cajones, desde el piso.
        var baseY = pisoY
        for (k in 0 until c.cajonesEfectivos) {
            val arriba = baseY + r.altoCajonCm
            val rect = RectF(x(izqC + 0.4f), y(arriba - 0.4f), x(derC - 0.4f), y(baseY + 0.2f))
            canvas.drawRect(rect, pPuerta)
            canvas.drawRect(rect, pLinea)
            canvas.drawRect(x(centroX - 6f), y(baseY + r.altoCajonCm / 2f + 0.8f), x(centroX + 6f), y(baseY + r.altoCajonCm / 2f - 0.8f), pTirador)
            baseY = arriba
        }
        // El colgador: el tubo a 6 cm del tope, con unos ganchos.
        if (c.llevaTubo) {
            val tuboY = topeBajo - 6f
            canvas.drawLine(x(izqC + 2f), y(tuboY), x(derC - 2f), y(tuboY), pTubo)
            val largo = derC - izqC
            val ganchos = (largo / 12f).toInt().coerceIn(1, 8)
            for (g in 0 until ganchos) {
                val gx = izqC + largo * (g + 0.5f) / ganchos
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
                    if (ey < topeBajo - 6f - 45f) canvas.drawRect(x(izqC), y(ey + e), x(derC), y(ey), pTablero)
                }
            } else {
                val hasta = if (c.llevaTubo) topeBajo - 6f - 45f else topeBajo
                val paso = (hasta - baseY) / (entrepanos + 1)
                for (k in 1..entrepanos) {
                    val ey = baseY + paso * k
                    canvas.drawRect(x(izqC), y(ey + e), x(derC), y(ey), pTablero)
                }
            }
        }
    }

    private fun dibujarPuertas(canvas: Canvas, r: Ropero) {
        val e = r.espesorCm
        when (r.puertas) {
            TipoPuertas.SIN -> Unit
            TipoPuertas.BATIENTES -> {
                var cx = e
                r.cuerpos.forEach { c ->
                    val luz = c.anchoCm + e
                    val izqC = cx - e / 2f
                    val hojas = if (luz <= 60f) 1 else 2
                    val ancho = luz / hojas
                    val cortes = if (r.maleteroCm > 0f) listOf(r.zocaloCm to r.altoCm - r.maleteroCm - e, r.altoCm - r.maleteroCm - e to r.altoCm)
                    else listOf(r.zocaloCm to r.altoCm)
                    cortes.forEach { (y0, y1) ->
                        for (h in 0 until hojas) {
                            val px0 = izqC + h * ancho + 0.3f
                            val px1 = izqC + (h + 1) * ancho - 0.3f
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
                val ancho = (r.anchoInteriorCm + (hojas - 1) * 5f) / hojas
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
}
