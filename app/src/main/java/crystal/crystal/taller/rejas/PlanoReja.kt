package crystal.crystal.taller.rejas

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.min

/**
 * El plano de la reja (2026-10-10), como el de Puertas: para saber dónde va cada pieza.
 *
 * - Cada tubo dibujado con su grueso y rotulado con su largo (el de la lista de corte).
 * - Reglas con la posición de cada parante y travesaño, acumulada desde la cara interior del marco
 *   donde se suelda: abajo y a la derecha las de la hoja (o del paño, en la fija); arriba y a la
 *   derecha, más alto, las de la mocheta.
 * - Las medidas generales y, en la puerta, la altura del puente.
 * - Arriba, una cabecera con los datos, como en el plano de Puertas.
 *
 * Todo sale de [RejaCalculo.todos]: el plano y la lista de corte son la misma cuenta.
 */
object PlanoReja {

    /** Un hueco con tubos dentro: de dónde a dónde van sus caras interiores (cm, y hacia arriba). */
    private data class Pano(val nombre: String, val x0: Float, val y0: Float, val x1: Float, val y1: Float, val sufijo: String)

    private val TUBOS_DE_MARCO = setOf("Marco", "Marco hoja", "Puente")

    fun dibujar(r: RejaCalculo.Reja, titulo: String): Bitmap {
        val tubos = RejaCalculo.todos(r)
        val k = min(2200f / r.altoCm, 2200f / r.anchoCm)          // px por cm
        val izq = 260f; val der = 380f; val arriba = 260f; val abajo = 260f
        val w = (r.anchoCm * k + izq + der).toInt()
        val h = (r.altoCm * k + arriba + abajo).toInt()
        val dibujo = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(dibujo)
        c.drawColor(Color.WHITE)
        fun x(cm: Float) = izq + cm * k
        fun y(cm: Float) = arriba + (r.altoCm - cm) * k

        val pRelleno = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(236, 236, 236); style = Paint.Style.FILL }
        val pBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; style = Paint.Style.STROKE; strokeWidth = 2.5f }
        val pRotulo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; textAlign = Paint.Align.CENTER }
        val pCota = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); style = Paint.Style.STROKE; strokeWidth = 2f }
        val pCotaTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#1565C0"); textSize = 30f; textAlign = Paint.Align.CENTER }

        // Los tubos: primero los de marco, encima lo de dentro.
        val ordenados = tubos.sortedBy { if (it.nombre in TUBOS_DE_MARCO) 0 else 1 }
        ordenados.forEach { t ->
            val grueso = (if (t.nombre in TUBOS_DE_MARCO) r.marcoCm else r.tuboCm) * k
            val forma = rectanguloDelTubo(x(t.x0), y(t.y0), x(t.x1), y(t.y1), grueso, t.a45)
            c.drawPath(forma, pRelleno)
            c.drawPath(forma, pBorde)
        }
        // El largo de cada tubo, a lo largo de él; si no cabe dentro, no se rotula (está en la lista).
        ordenados.forEach { t ->
            val grueso = (if (t.nombre in TUBOS_DE_MARCO) r.marcoCm else r.tuboCm) * k
            val texto = RejaCalculo.fmt(t.largo)
            pRotulo.textSize = (grueso * 0.72f).coerceIn(14f, 40f)
            val largoPx = t.largo * k
            if (pRotulo.measureText(texto) > largoPx * 0.9f) return@forEach
            val cx = (x(t.x0) + x(t.x1)) / 2f
            val cy = (y(t.y0) + y(t.y1)) / 2f
            var angulo = Math.toDegrees(atan2((y(t.y1) - y(t.y0)).toDouble(), (x(t.x1) - x(t.x0)).toDouble())).toFloat()
            if (angulo > 90f) angulo -= 180f
            if (angulo < -90f) angulo += 180f
            c.save()
            c.rotate(angulo, cx, cy)
            c.drawText(texto, cx, cy + pRotulo.textSize * 0.36f, pRotulo)
            c.restore()
        }

        // Las medidas generales: el ancho arriba del todo y el alto a la izquierda.
        val yAncho = arriba - 200f
        reglaHorizontal(c, x(0f), x(r.anchoCm), yAncho, listOf(r.anchoCm), r.anchoCm, k, pCota, pCotaTexto, soloTotal = true)
        reglaVertical(c, izq - 200f, y(0f), listOf(r.altoCm), k, pCota, pCotaTexto, soloTotal = true)

        // Los huecos y sus reglas.
        val panos = panos(r)
        panos.forEach { p ->
            val dentro = tubos.filter { it.nombre !in TUBOS_DE_MARCO && it.nombre.endsWith(p.sufijo) && (p.sufijo.isNotEmpty() || !it.nombre.endsWith("mocheta")) }
            val t = r.tuboCm
            // Parantes: su cara izquierda desde la cara interior izquierda del hueco.
            val xs = dentro.filter { abs(it.x0 - it.x1) < 0.01f }.map { it.x0 - t / 2f - p.x0 }
                .filter { it > 0.05f }.map { redondear(it) }.distinct().sorted()
            // Travesaños: su cara de abajo desde la cara interior de abajo del hueco.
            val ys = dentro.filter { abs(it.y0 - it.y1) < 0.01f }.map { it.y0 - t / 2f - p.y0 }
                .filter { it > 0.05f }.map { redondear(it) }.distinct().sorted()
            val esMocheta = p.sufijo.isNotEmpty()
            if (xs.isNotEmpty()) {
                val yRegla = if (esMocheta) arriba - 90f else y(0f) + 90f
                reglaHorizontal(c, x(p.x0), x(p.x1), yRegla, xs + (p.x1 - p.x0), p.x1 - p.x0, k, pCota, pCotaTexto)
            }
            if (ys.isNotEmpty()) {
                reglaVertical(c, x(r.anchoCm) + 110f, y(p.y0), ys + (p.y1 - p.y0), k, pCota, pCotaTexto)
            }
        }

        // La altura del puente, a la izquierda, junto al alto.
        if (r.tipo == TipoReja.PUERTA) {
            reglaVertical(c, izq - 90f, y(0f), listOf(RejaCalculo.alturaPuente(r)), k, pCota, pCotaTexto, soloTotal = true)
        }
        return conCabecera(dibujo, titulo, datos(r))
    }

    /** Los huecos que llevan tubos: el paño de la fija, o la hoja y la mocheta de la puerta. */
    private fun panos(r: RejaCalculo.Reja): List<Pano> {
        val m = r.marcoCm
        if (r.tipo == TipoReja.FIJA) return listOf(Pano("Paño", m, m, r.anchoCm - m, r.altoCm - m, ""))
        val hoja = RejaCalculo.hoja(r)
        val dx = m + r.holguraCm / 2f
        val dy = r.holguraCm / 2f
        val salen = mutableListOf(Pano("Hoja", dx + m, dy + m, dx + hoja.anchoCm - m, dy + hoja.altoCm - m, ""))
        if (RejaCalculo.conSobreluz(r)) {
            val puente = RejaCalculo.alturaPuente(r)
            salen.add(Pano("Mocheta", m, puente + m, r.anchoCm - m, r.altoCm - m, "mocheta"))
        }
        return salen
    }

    private fun redondear(v: Float) = Math.round(v * 10f) / 10f

    /** El tubo como rectángulo: su eje de punta a punta y su grueso a cada lado; a 45° se le añade medio grueso en las puntas. */
    private fun rectanguloDelTubo(x0: Float, y0: Float, x1: Float, y1: Float, grueso: Float, a45: Boolean): Path {
        val dx = x1 - x0; val dy = y1 - y0
        val largo = kotlin.math.sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val ux = dx / largo; val uy = dy / largo
        val nx = -uy * grueso / 2f; val ny = ux * grueso / 2f
        // Los rectos van justo de punta a punta; los de 45°, con las puntas cortadas en diagonal no se dibujan: se alarga medio grueso.
        val e = if (a45) grueso / 2f else 0f
        val ax = x0 - ux * e; val ay = y0 - uy * e
        val bx = x1 + ux * e; val by = y1 + uy * e
        return Path().apply {
            moveTo(ax + nx, ay + ny); lineTo(bx + nx, by + ny); lineTo(bx - nx, by - ny); lineTo(ax - nx, ay - ny); close()
        }
    }

    /**
     * Una regla horizontal de [desde] a [hasta] (px) a la altura [yRegla], con una marca en cada
     * posición (cm desde el arranque) rotulada con lo acumulado. Si dos rótulos se pisarían, el
     * segundo baja a otro renglón.
     */
    private fun reglaHorizontal(
        c: Canvas, desde: Float, hasta: Float, yRegla: Float, posiciones: List<Float>, total: Float,
        k: Float, pLinea: Paint, pTexto: Paint, soloTotal: Boolean = false
    ) {
        c.drawLine(desde, yRegla, hasta, yRegla, pLinea)
        c.drawLine(desde, yRegla - 14f, desde, yRegla + 14f, pLinea)
        val marcas = if (soloTotal) listOf(total) else posiciones
        var finAnterior = Float.NEGATIVE_INFINITY
        var renglon = 0
        marcas.forEach { cm ->
            val xm = desde + cm * k
            c.drawLine(xm, yRegla - 14f, xm, yRegla + 14f, pLinea)
            val texto = RejaCalculo.fmt(cm)
            val ancho = pTexto.measureText(texto)
            val xTexto = if (soloTotal) (desde + hasta) / 2f else xm
            renglon = if (xTexto - ancho / 2f < finAnterior + 6f) (renglon + 1) % 2 else 0
            c.drawText(texto, xTexto, yRegla - 22f - renglon * (pTexto.textSize + 4f), pTexto)
            finAnterior = xTexto + ancho / 2f
        }
    }

    /** Lo mismo en vertical: [abajoPx] es el arranque (el suelo o la cara de abajo del hueco). */
    private fun reglaVertical(
        c: Canvas, xRegla: Float, abajoPx: Float, posiciones: List<Float>,
        k: Float, pLinea: Paint, pTexto: Paint, soloTotal: Boolean = false
    ) {
        val tope = posiciones.maxOrNull() ?: return
        c.drawLine(xRegla, abajoPx, xRegla, abajoPx - tope * k, pLinea)
        c.drawLine(xRegla - 14f, abajoPx, xRegla + 14f, abajoPx, pLinea)
        var finAnterior = Float.POSITIVE_INFINITY
        var columna = 0
        posiciones.forEach { cm ->
            val ym = abajoPx - cm * k
            c.drawLine(xRegla - 14f, ym, xRegla + 14f, ym, pLinea)
            val texto = RejaCalculo.fmt(cm)
            val alto = pTexto.textSize
            val yTexto = if (soloTotal) abajoPx - cm * k / 2f else ym
            columna = if (yTexto + alto / 2f > finAnterior - 4f) (columna + 1) % 2 else 0
            val xTexto = xRegla + 24f + columna * (pTexto.measureText("000.0") + 10f)
            if (soloTotal) {
                c.save()
                c.rotate(-90f, xRegla - 18f, yTexto)
                c.drawText(texto, xRegla - 18f, yTexto, pTexto)
                c.restore()
            } else {
                val alineado = Paint(pTexto).apply { textAlign = Paint.Align.LEFT }
                c.drawText(texto, xTexto, yTexto + alto * 0.35f, alineado)
            }
            finAnterior = yTexto - alto / 2f
        }
    }

    /** Los renglones de la cabecera: medidas, tubos, modelo y, en la puerta, hoja, puente y mocheta. */
    private fun datos(r: RejaCalculo.Reja): List<String> {
        val f = RejaCalculo::fmt
        val salen = mutableListOf(
            "Reja ${r.tipo.etiqueta.lowercase()} ${f(r.anchoCm)} x ${f(r.altoCm)}   Marco ${f(r.marcoCm)}   Tubo ${f(r.tuboCm)}",
            "Modelo: ${r.modelo.etiqueta}, paso ${f(r.pasoCm)}" + (if (r.intermedios > 0) ", ${r.intermedios} intermedios" else "")
        )
        if (r.tipo == TipoReja.PUERTA) {
            val hoja = RejaCalculo.hoja(r)
            salen.add("Hoja ${f(hoja.anchoCm)} x ${f(hoja.altoCm)}   Puente a ${f(RejaCalculo.alturaPuente(r))}   Holgura ${f(r.holguraCm)}")
            if (RejaCalculo.conSobreluz(r)) {
                val m = r.mocheta
                salen.add(
                    "Mocheta: " + when {
                        m == null -> "como la hoja"
                        m.modelo == null -> "sin relleno"
                        else -> "${m.modelo.etiqueta}, paso ${f(m.pasoCm)}" + (if (m.intermedios > 0) ", ${m.intermedios} intermedios" else "")
                    }
                )
            } else salen.add("Sin mocheta: la hoja llega al cabezal")
        }
        salen.add("Reglas: posición de cada tubo desde la cara interior del marco (hoja abajo, mocheta arriba)")
        return salen
    }

    /** La cabecera arriba, como en el plano de Puertas: el título en negrita y los datos debajo. */
    private fun conCabecera(dibujo: Bitmap, titulo: String, datos: List<String>): Bitmap {
        val pBold = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; textSize = 56f; typeface = Typeface.DEFAULT_BOLD }
        val pTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; textSize = 38f }
        var pad = 28f
        val maxW = maxOf(pBold.measureText(titulo), datos.maxOf { pTxt.measureText(it) })
        if (maxW + 2f * pad > dibujo.width) {
            val factor = dibujo.width / (maxW + 2f * pad)
            pBold.textSize *= factor; pTxt.textSize *= factor; pad *= factor
        }
        val lineaTitulo = pBold.textSize * 1.4f
        val linea = pTxt.textSize * 1.45f
        val alto = (pad * 2f + lineaTitulo + linea * datos.size).toInt()
        val out = Bitmap.createBitmap(dibujo.width, dibujo.height + alto, Bitmap.Config.ARGB_8888)
        val c = Canvas(out)
        c.drawColor(Color.WHITE)
        var y = pad + pBold.textSize
        c.drawText(titulo, pad, y, pBold)
        y += lineaTitulo
        datos.forEach { c.drawText(it, pad, y, pTxt); y += linea }
        c.drawLine(0f, alto.toFloat(), dibujo.width.toFloat(), alto.toFloat(), Paint().apply { color = Color.LTGRAY; strokeWidth = 2f })
        c.drawBitmap(dibujo, 0f, alto.toFloat(), null)
        return out
    }
}
