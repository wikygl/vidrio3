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

    companion object {
        // El plano de taller de siempre: fondo blanco, tableros gris oscuro, cajones gris, casilleros
        // en blanco. Contrasta con cualquier fondo y se distingue cada cosa de un vistazo.
        const val COLOR_TABLERO = "#5E5E5E"
        const val COLOR_TABLERO_LADO = "#474747"
        const val COLOR_TABLERO_TECHO = "#7A7A7A"
        const val COLOR_INTERIOR = "#FFFFFF"
        const val COLOR_CAJON = "#9A9A9A"
        const val COLOR_PUERTA = "#C9C9C9"
        const val COLOR_RIEL = "#D6D6D6"
        /** Más fino que esto un tablero no se ve: en la ficha chica el espesor real cae bajo el píxel. */
        const val GROSOR_MINIMO_DP = 2.5f
    }

    val pTablero = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor(COLOR_TABLERO); style = Paint.Style.FILL }
    private val pTableroLado = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor(COLOR_TABLERO_LADO); style = Paint.Style.FILL }
    private val pTableroTecho = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor(COLOR_TABLERO_TECHO); style = Paint.Style.FILL }
    val pInterior = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor(COLOR_INTERIOR); style = Paint.Style.FILL }
    private val pBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#3A3A3A"); style = Paint.Style.STROKE; strokeWidth = 1.2f * dp }
    private val pLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#4A4A4A"); style = Paint.Style.STROKE; strokeWidth = 1f * dp }
    private val pRiel = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor(COLOR_RIEL); style = Paint.Style.FILL }
    private val pGancho = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#8A8A8A"); style = Paint.Style.STROKE; strokeWidth = 1.3f * dp; strokeCap = Paint.Cap.ROUND }
    val pCajon = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor(COLOR_CAJON); style = Paint.Style.FILL }
    val pPuerta = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor(COLOR_PUERTA); style = Paint.Style.FILL }
    private val pPuertaCorrediza = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#BDBDBD"); style = Paint.Style.FILL; alpha = 230 }
    private val pTirador = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#3A3A3A"); style = Paint.Style.FILL }
    val pRotulo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F2F2F2"); textSize = 10f * dp; textAlign = Paint.Align.CENTER }
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

    /** El espesor con el que se PINTA el tablero: el real, pero nunca más fino que un trazo visible. */
    private fun espesorVisible(r: Ropero): Float = maxOf(r.espesorCm, GROSOR_MINIMO_DP * dp / escala)

    /**
     * Un tablero pintado con su grueso visible, centrado en el tablero real: si el real es más
     * fino que un trazo, se engorda hacia los dos lados sin mover nada de sitio.
     */
    private fun tablero(canvas: Canvas, r: Ropero, x0: Float, x1: Float, y0: Float, y1: Float) {
        val extra = (espesorVisible(r) - r.espesorCm) / 2f
        val horizontal = (x1 - x0) > (y1 - y0)
        if (horizontal) canvas.drawRect(x(x0), y(y1 + extra), x(x1), y(y0 - extra), pTablero)
        else canvas.drawRect(x(x0 - extra), y(y1), x(x1 + extra), y(y0), pTablero)
    }

    private fun dibujarFrente(canvas: Canvas, r: Ropero, mostrarPuertas: Boolean, cuerpoResaltado: Int, conRotulos: Boolean) {
        // Las posiciones van SIEMPRE con el espesor real: los cuerpos suman el ancho del hueco.
        val e = r.espesorCm
        // El hueco interior, claro, y encima el armazón.
        canvas.drawRect(x(0f), y(r.altoCm), x(r.anchoCm), y(0f), pInterior)
        canvas.drawRect(x(e), y(r.zocaloCm), x(r.anchoCm - e), y(0f), pTablero)
        tablero(canvas, r, e, r.anchoCm - e, r.zocaloCm, r.zocaloCm + e)
        tablero(canvas, r, e, r.anchoCm - e, r.altoCm - e, r.altoCm)
        tablero(canvas, r, 0f, e, 0f, r.altoCm)
        tablero(canvas, r, r.anchoCm - e, r.anchoCm, 0f, r.altoCm)

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
            if (i < r.cuerpos.size - 1) tablero(canvas, r, derC, derC + e, pisoY, techoY)
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
            tablero(canvas, r, izqC, derC, repisaY - e, repisaY)
            topeBajo = repisaY - e
        }
        val centroX = (izqC + derC) / 2f
        // Cajones, desde el piso.
        var baseY = pisoY
        for (k in 0 until c.cajonesEfectivos) {
            val arriba = baseY + r.altoCajonCm
            // El frente gris, un poco metido, con el tirador pegado al canto derecho, como en el plano.
            val rect = RectF(x(izqC + 1.5f), y(arriba - 1f), x(derC - 1.5f), y(baseY + 1f))
            canvas.drawRect(rect, pCajon)
            canvas.drawRect(rect, pLinea)
            val medio = baseY + r.altoCajonCm / 2f
            canvas.drawRect(x(derC - 3f), y(medio + 2.5f), x(derC - 1.5f), y(medio - 2.5f), pTirador)
            baseY = arriba
        }
        // El colgador: el riel claro a 6 cm del tope y los ganchos colgados, como en el plano.
        if (c.llevaTubo) {
            val rielY = topeBajo - 6f
            canvas.drawRect(x(izqC + 1f), y(rielY + 1.5f), x(derC - 1f), y(rielY - 1.5f), pRiel)
            val largo = derC - izqC
            val ganchos = (largo / 10f).toInt().coerceIn(1, 12)
            for (g in 0 until ganchos) {
                val gx = izqC + largo * (g + 0.5f) / ganchos
                // El gancho: el ojo sobre el riel, el cuello, y la percha como un trazo en punta.
                val ojo = RectF(x(gx - 2f), y(rielY + 3.5f), x(gx + 2f), y(rielY - 0.5f))
                canvas.drawArc(ojo, 200f, 260f, false, pGancho)
                canvas.drawLine(x(gx), y(rielY - 0.5f), x(gx), y(rielY - 4f), pGancho)
                canvas.drawLine(x(gx - 4f), y(rielY - 6f), x(gx + 4f), y(rielY - 6f), pGancho)
                canvas.drawLine(x(gx - 4f), y(rielY - 6f), x(gx), y(rielY - 4f), pGancho)
                canvas.drawLine(x(gx + 4f), y(rielY - 6f), x(gx), y(rielY - 4f), pGancho)
            }
        }
        // Entrepaños: repartidos en lo que queda. En el colgador van abajo, debajo de la ropa
        // (la repisa de los zapatos), cada 30 cm desde el piso.
        val entrepanos = c.entrepanosEfectivos
        if (entrepanos > 0) {
            if (c.tipo == TipoCuerpo.COLGAR) {
                for (k in 0 until entrepanos) {
                    val ey = baseY + 30f * (k + 1)
                    if (ey < topeBajo - 6f - 45f) tablero(canvas, r, izqC, derC, ey, ey + e)
                }
            } else {
                val hasta = if (c.llevaTubo) topeBajo - 6f - 45f else topeBajo
                val paso = (hasta - baseY) / (entrepanos + 1)
                for (k in 1..entrepanos) {
                    val ey = baseY + paso * k
                    tablero(canvas, r, izqC, derC, ey, ey + e)
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
                    pPuertaCorrediza.color = if (h % 2 == 0) Color.parseColor("#C9C9C9") else Color.parseColor("#ADADAD")
                    pPuertaCorrediza.alpha = 235
                    canvas.drawRect(rect, pPuertaCorrediza)
                    canvas.drawRect(rect, pLinea)
                    val tx = if (h % 2 == 0) px0 + ancho - 5f else px0 + 5f
                    canvas.drawRect(x(tx - 0.8f), y((y0 + y1) / 2f + 7f), x(tx + 0.8f), y((y0 + y1) / 2f - 7f), pTirador)
                }
            }
        }
    }
}
