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
        val altoDer = r.altoDeCuerpo(r.cuerpos.size - 1)
        val costado = Path().apply {
            moveTo(x(r.anchoCm), y(0f)); lineTo(x(r.anchoCm) + dx, y(0f) + dy)
            lineTo(x(r.anchoCm) + dx, y(altoDer) + dy); lineTo(x(r.anchoCm), y(altoDer)); close()
        }
        canvas.drawPath(costado, pTableroLado)
        canvas.drawPath(costado, pBorde)
        // El techo, cuerpo por cuerpo con su alto.
        val e = r.espesorCm
        val n = r.cuerpos.size
        RoperoGeometria.cuerposX(r).forEachIndexed { i, (izq, der) ->
            val alto = r.altoDeCuerpo(i)
            val x0 = if (i == 0) 0f else izq - e / 2f
            val x1 = if (i == n - 1) r.anchoCm else der + e / 2f
            val techo = Path().apply {
                moveTo(x(x0), y(alto)); lineTo(x(x0) + dx, y(alto) + dy)
                lineTo(x(x1) + dx, y(alto) + dy); lineTo(x(x1), y(alto)); close()
            }
            canvas.drawPath(techo, pTableroTecho)
            canvas.drawPath(techo, pBorde)
        }
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
        // El hueco interior, claro, cuerpo por cuerpo hasta su alto, y encima el armazón: cada
        // lado con su altura (bajo una escalera el techo baja por tramos).
        val n = r.cuerpos.size
        val cuerposX = RoperoGeometria.cuerposX(r)
        cuerposX.forEachIndexed { i, (izq, der) ->
            val alto = r.altoDeCuerpo(i)
            canvas.drawRect(x(izq - e), y(alto), x(der + e), y(0f), pInterior)
        }
        canvas.drawRect(x(e), y(r.zocaloCm), x(r.anchoCm - e), y(0f), pTablero)
        tablero(canvas, r, e, r.anchoCm - e, r.zocaloCm, r.zocaloCm + e)
        cuerposX.forEachIndexed { i, (izq, der) ->
            val alto = r.altoDeCuerpo(i)
            // El techo de este cuerpo: hasta la mitad de cada división, y hasta el lateral en las puntas.
            val x0 = if (i == 0) e else izq - e / 2f
            val x1 = if (i == n - 1) r.anchoCm - e else der + e / 2f
            tablero(canvas, r, x0, x1, alto - e, alto)
        }
        tablero(canvas, r, 0f, e, 0f, r.altoDeCuerpo(0))
        tablero(canvas, r, r.anchoCm - e, r.anchoCm, 0f, r.altoDeCuerpo(n - 1))

        val pisoY = RoperoGeometria.pisoY(r)
        val elementos = RoperoGeometria.elementos(r)
        cuerposX.forEachIndexed { i, (izqC, derC) ->
            val techoY = RoperoGeometria.techoY(r, i)
            // Lo de dentro se queda dentro del cuerpo: los ganchos no asoman por las divisiones.
            canvas.save()
            canvas.clipRect(x(izqC), y(techoY), x(derC), y(pisoY))
            dibujarCuerpo(canvas, r, i, elementos)
            canvas.restore()
            if (i == cuerpoResaltado) {
                canvas.drawRect(x(izqC) + dp, y(techoY) + dp, x(derC) - dp, y(pisoY) - dp, pSeleccion)
            }
            // La división sube hasta el techo más alto de los dos cuerpos que separa.
            if (i < n - 1) tablero(canvas, r, derC, derC + e, pisoY, maxOf(techoY, RoperoGeometria.techoY(r, i + 1)))
        }
        if (conRotulos) {
            // El rótulo de cada cuerpo va en el zócalo, que ahí no tapa nada; solo si cabe.
            cuerposX.forEachIndexed { i, (izq, der) ->
                val c = r.cuerpos[i]
                if (pRotulo.measureText(c.tipo.etiqueta) < (c.anchoCm - 4f) * escala) {
                    canvas.drawText(c.tipo.etiqueta, x((izq + der) / 2f), y(r.zocaloCm / 2f) + 4f * dp, pRotulo)
                }
            }
        }
        if (mostrarPuertas) dibujarPuertas(canvas, r)
        // El contorno: por arriba sigue el alto de cada cuerpo.
        val contorno = Path().apply {
            moveTo(x(0f), y(0f))
            cuerposX.forEachIndexed { i, (izq, der) ->
                val alto = r.altoDeCuerpo(i)
                val x0 = if (i == 0) 0f else izq - e / 2f
                val x1 = if (i == n - 1) r.anchoCm else der + e / 2f
                lineTo(x(x0), y(alto)); lineTo(x(x1), y(alto))
            }
            lineTo(x(r.anchoCm), y(0f)); close()
        }
        canvas.drawPath(contorno, pBorde)
    }

    /**
     * Lo de dentro de un cuerpo, donde lo pone [RoperoGeometria]: así lo que se pinta es lo que
     * se toca en la pantalla de diseño y lo que se corta.
     */
    private fun dibujarCuerpo(canvas: Canvas, r: Ropero, i: Int, elementos: List<ElementoRopero>) {
        val e = r.espesorCm
        elementos.filter { it.cuerpo == i }.forEach { el ->
            when (el.tipo) {
                TipoElemento.CUERPO, TipoElemento.CASILLERO -> Unit
                TipoElemento.REPISA_MALETERO, TipoElemento.ENTREPANO, TipoElemento.TAPA_CAJONES -> tablero(canvas, r, el.x0, el.x1, el.y0, el.y1)
                TipoElemento.CAJON -> {
                    // El frente gris, un poco metido, con el tirador pegado al canto derecho, como en el plano.
                    val rect = RectF(x(el.x0 + 1.5f), y(el.y1 - 1f), x(el.x1 - 1.5f), y(el.y0 + 1f))
                    canvas.drawRect(rect, pCajon)
                    canvas.drawRect(rect, pLinea)
                    val medio = (el.y0 + el.y1) / 2f
                    canvas.drawRect(x(el.x1 - 3f), y(medio + 2.5f), x(el.x1 - 1.5f), y(medio - 2.5f), pTirador)
                }
                TipoElemento.TUBO -> {
                    // El colgador: el riel claro y los ganchos colgados, como en el plano.
                    val rielY = (el.y0 + el.y1) / 2f
                    canvas.drawRect(x(el.x0 + 1f), y(rielY + 1.5f), x(el.x1 - 1f), y(rielY - 1.5f), pRiel)
                    val largo = el.x1 - el.x0
                    val ganchos = (largo / 10f).toInt().coerceIn(1, 12)
                    for (g in 0 until ganchos) {
                        val gx = el.x0 + largo * (g + 0.5f) / ganchos
                        // El gancho: el ojo sobre el riel, el cuello, y la percha como un trazo en punta.
                        val ojo = RectF(x(gx - 2f), y(rielY + 3.5f), x(gx + 2f), y(rielY - 0.5f))
                        canvas.drawArc(ojo, 200f, 260f, false, pGancho)
                        canvas.drawLine(x(gx), y(rielY - 0.5f), x(gx), y(rielY - 4f), pGancho)
                        canvas.drawLine(x(gx - 4f), y(rielY - 6f), x(gx + 4f), y(rielY - 6f), pGancho)
                        canvas.drawLine(x(gx - 4f), y(rielY - 6f), x(gx), y(rielY - 4f), pGancho)
                        canvas.drawLine(x(gx + 4f), y(rielY - 6f), x(gx), y(rielY - 4f), pGancho)
                    }
                }
            }
        }
        if (e <= 0f) return
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
                    val hojas = RoperoCalculo.hojasBatientes(c, e)
                    val ancho = luz / hojas
                    val alto = r.altoDeCuerpo(r.cuerpos.indexOf(c))
                    val cortes = if (r.maleteroCm > 0f) listOf(r.zocaloCm to alto - r.maleteroCm - e, alto - r.maleteroCm - e to alto)
                    else listOf(r.zocaloCm to alto)
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
                val hojas = RoperoCalculo.hojasCorredizas(r)
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
