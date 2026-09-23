package crystal.crystal.taller.melamina

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import kotlin.math.min

/**
 * Una pieza sola dibujada para el taller, en un bitmap que se mira con zoom:
 * - la de CORTE con sus cantos donde van (de qué tapacanto y de qué color), la veta y la ranura;
 * - el TABLERO con sus marcas en negro (la cara de lo que llega, medida desde el canto de
 *   referencia) y sus agujeros en rosado (el centro del espesor, y a lo hondo desde el frente),
 *   como en el plano de marcas de siempre.
 */
object PiezaTallerDibujo {

    private val NEGRO = Color.parseColor("#212121")
    private val ROSADO = Color.parseColor("#E91E63")
    private val AZUL = Color.parseColor("#0D47A1")
    private val CANTO = mapOf(
        TipoCanto.FINO to Color.parseColor("#616161"),
        TipoCanto.FINO_COLOR to Color.parseColor("#EF6C00"),
        TipoCanto.GRUESO to Color.parseColor("#C62828")
    )

    private fun texto(tam: Float, color: Int, negrita: Boolean = false, centro: Boolean = false) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = tam; this.color = color
        typeface = if (negrita) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        if (centro) textAlign = Paint.Align.CENTER
    }

    private fun trazo(ancho: Float, color: Int) = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE; strokeWidth = ancho; this.color = color }

    /** El rectángulo de la pieza ajustado al lienzo, dejando sitio a los lados para las cotas. */
    private fun encajar(w: Int, h: Int, ancho: Float, alto: Float, margenX: Float, margenArriba: Float, margenAbajo: Float): Pair<RectF, Float> {
        val k = min((w - 2 * margenX) / ancho, (h - margenArriba - margenAbajo) / alto)
        val pw = ancho * k
        val ph = alto * k
        val x0 = (w - pw) / 2f
        val y0 = margenArriba + ((h - margenArriba - margenAbajo) - ph) / 2f
        return RectF(x0, y0, x0 + pw, y0 + ph) to k
    }

    // ==================== Corte ====================

    fun corte(f: RoperoProduccion.FichaCorte, w: Int = 1200, h: Int = 1500): Bitmap {
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        val p = f.pieza
        c.drawText("N° ${f.numero} · ${p.nombre} × ${p.cantidad}", 40f, 70f, texto(46f, NEGRO, negrita = true))
        c.drawText("${RoperoProduccion.fmt(f.anchoCm)} x ${RoperoProduccion.fmt(f.altoCm)} · ${f.material}", 40f, 125f, texto(36f, AZUL))
        val (rf, k) = encajar(w, h, f.anchoCm, f.altoCm, 150f, 200f, 260f)
        c.drawRect(rf, Paint().apply { color = Color.parseColor(if (p.material.name.contains("COLOR")) "#E6D3B3" else "#F5F5F5") })
        c.drawRect(rf, trazo(2f, NEGRO))
        val pc = trazo(14f, CANTO.getValue(p.canto)).apply { strokeCap = Paint.Cap.BUTT }
        // Con ranura (las piezas de la caja), el canto va arriba, en el borde que se ve, y la ranura abajo.
        if (p.cantosEnAncho >= 1) { val yc = if (p.ranuraCm > 0f && p.cantosEnAncho == 1) rf.top else rf.bottom; c.drawLine(rf.left, yc, rf.right, yc, pc) }
        if (p.cantosEnAncho >= 2) c.drawLine(rf.left, rf.top, rf.right, rf.top, pc)
        if (p.cantosEnAlto >= 1) c.drawLine(rf.left, rf.top, rf.left, rf.bottom, pc)
        if (p.cantosEnAlto >= 2) c.drawLine(rf.right, rf.top, rf.right, rf.bottom, pc)
        // La ranura: a su distancia del canto de abajo, a lo largo del ancho.
        if (p.ranuraCm > 0f) {
            val y = rf.bottom - p.ranuraCm * k
            c.drawLine(rf.left, y, rf.right, y, trazo(4f, AZUL).apply { pathEffect = DashPathEffect(floatArrayOf(18f, 10f), 0f) })
            c.drawText("ranura a ${RoperoProduccion.fmt(p.ranuraCm)} del canto de abajo", rf.left + 10f, y - 12f, texto(30f, AZUL))
        }
        // La veta, a lo alto.
        if (f.vetas.isNotBlank()) {
            val x = rf.centerX(); val y0 = rf.centerY() - rf.height() * 0.3f; val y1 = rf.centerY() + rf.height() * 0.3f
            val pv = trazo(4f, Color.parseColor("#8D6E63"))
            c.drawLine(x, y0, x, y1, pv)
            c.drawPath(Path().apply { moveTo(x - 14f, y0 + 22f); lineTo(x, y0); lineTo(x + 14f, y0 + 22f); moveTo(x - 14f, y1 - 22f); lineTo(x, y1); lineTo(x + 14f, y1 - 22f) }, pv)
            c.drawText("veta", x + 16f, rf.centerY(), texto(30f, Color.parseColor("#8D6E63")))
        }
        c.drawText(RoperoProduccion.fmt(f.anchoCm), rf.centerX(), rf.bottom + 50f, texto(36f, AZUL, centro = true))
        c.save(); c.rotate(-90f, rf.left - 30f, rf.centerY())
        c.drawText(RoperoProduccion.fmt(f.altoCm), rf.left - 30f, rf.centerY(), texto(36f, AZUL, centro = true)); c.restore()
        var y = h - 190f
        listOf("Canto: ${f.canto.ifBlank { "sin canto" }}", "Etiqueta: ${f.etiqueta}", "Código: ${f.codigo}").forEach { t ->
            c.drawText(t, 40f, y, texto(32f, NEGRO)); y += 50f
        }
        return bmp
    }

    // ==================== Marcas y agujeros ====================

    fun tablero(t: RoperoProduccion.Tablero): Bitmap {
        // Un tablero vertical con marcas en sus dos caras (una división) se dibuja dos veces, cara
        // por cara, para marcar una, darle la vuelta y marcar la otra.
        val caras = if (t.vertical) t.marcas.map { it.cara }.distinct().filter { it.startsWith("cara") }.sorted() else emptyList()
        val dosCaras = caras.size == 2
        val w = if (dosCaras) 1700 else 1200
        val h = 1800
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        c.drawColor(Color.WHITE)
        c.drawText("${t.nombre} · ${t.zona}", 40f, 64f, texto(42f, NEGRO, negrita = true))
        c.drawText("${RoperoProduccion.fmt(t.ancho)} x ${RoperoProduccion.fmt(t.alto)} · marcas ${t.referencia}", 40f, 112f, texto(28f, AZUL))
        c.drawText("negro: marca (cara de lo que llega)   rosado: agujero", 40f, 152f, texto(28f, Color.DKGRAY))
        if (!t.vertical) { horizontal(c, t, w, h); return bmp }
        if (dosCaras) {
            val mitad = w / 2
            caras.forEachIndexed { i, cara ->
                c.save(); c.translate((i * mitad).toFloat(), 0f)
                vertical(c, t, t.marcas.filter { it.cara == cara }, mitad, h, "$cara (vista de esa cara)")
                c.restore()
            }
        } else vertical(c, t, t.marcas, w, h, caras.firstOrNull()?.let { "$it (vista de esa cara)" }.orEmpty())
        return bmp
    }

    /** Un tablero vertical: las marcas a lo alto desde su canto de abajo, el frente a la izquierda. */
    private fun vertical(c: Canvas, t: RoperoProduccion.Tablero, marcas: List<RoperoProduccion.Marca>, w: Int, h: Int, titulo: String) {
        val (rf, k) = encajar(w, h, t.ancho, t.alto, 190f, 230f, 90f)
        if (titulo.isNotBlank()) c.drawText(titulo, rf.centerX(), 205f, texto(30f, NEGRO, negrita = true, centro = true))
        c.drawRect(rf, Paint().apply { color = Color.parseColor("#F5F5F5") })
        c.drawRect(rf, trazo(3f, NEGRO))
        val esPuerta = marcas.any { it.tipo == RoperoProduccion.TipoMarca.CAZOLETA }
        // Mirando la cara izquierda de un tablero, el frente del mueble queda a la derecha; la derecha, a la izquierda.
        val frenteDerecha = titulo.startsWith("cara izquierda")
        fun fx(hondo: Float) = if (frenteDerecha) rf.right - hondo * k else rf.left + hondo * k
        if (!esPuerta) c.drawText("frente", if (frenteDerecha) rf.right - 70f else rf.left, rf.bottom + 76f, texto(26f, Color.GRAY))
        marcas.forEach { m ->
            val y = rf.bottom - m.a * k
            when (m.tipo) {
                RoperoProduccion.TipoMarca.CAZOLETA -> {
                    val izq = m.nota.contains("izquierdo")
                    val cx = if (izq) rf.left + m.hondo.first() * k else rf.right - m.hondo.first() * k
                    c.drawCircle(cx, y, maxOf(1.75f * k, 8f), trazo(3f, ROSADO))
                    c.drawCircle(cx, y, 3f, Paint().apply { color = ROSADO })
                }
                RoperoProduccion.TipoMarca.RIEL ->
                    c.drawLine(rf.left, y, rf.right, y, trazo(3f, NEGRO).apply { pathEffect = DashPathEffect(floatArrayOf(14f, 8f), 0f) })
                RoperoProduccion.TipoMarca.BISAGRA -> {
                    val bx = fx(m.hondo.firstOrNull() ?: RoperoProduccion.BASE_DESDE_FRENTE_CM)
                    c.drawRect(bx - 1.2f * k, y - RoperoProduccion.MEDIA_BISAGRA_CM * k, bx + 1.2f * k, y + RoperoProduccion.MEDIA_BISAGRA_CM * k, trazo(3f, ROSADO))
                }
                RoperoProduccion.TipoMarca.UNION -> {
                    c.drawLine(rf.left, y, rf.right, y, trazo(3f, NEGRO))
                    m.agujero?.let { ag -> val ya = rf.bottom - ag * k; m.hondo.forEach { hx -> c.drawCircle(fx(hx), ya, 7f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ROSADO }) } }
                }
            }
        }
        // Las cotas: la marca a la izquierda (negro) y el agujero a la derecha (rosado); las que
        // quedan juntas se abren y una línea guía las lleva a su sitio.
        apilar(c, marcas.map { (rf.bottom - it.a * k) to "${RoperoProduccion.fmt(it.a)} ${abreviar(it)}".trim() }, rf.left - 16f, rf.left, texto(30f, NEGRO).apply { textAlign = Paint.Align.RIGHT }, NEGRO)
        apilar(c, marcas.mapNotNull { m -> m.agujero?.let { (rf.bottom - it * k) to RoperoProduccion.fmt(it) } }, rf.right + 16f, rf.right, texto(30f, ROSADO), ROSADO)
        marcas.firstOrNull { it.tipo == RoperoProduccion.TipoMarca.UNION }?.let { m ->
            m.hondo.forEach { hx -> c.drawText(RoperoProduccion.fmt(hx), fx(hx), rf.bottom + 40f, texto(28f, ROSADO, centro = true)) }
        }
        marcas.firstOrNull { it.tipo == RoperoProduccion.TipoMarca.CAZOLETA }?.let { m ->
            c.drawText("cazoletas ${m.nota}", 40f, rf.bottom + 60f, texto(28f, ROSADO))
        }
    }

    /**
     * Pone cada texto a la altura que le toca y, si se pisa con el de abajo, lo sube lo justo, con
     * una línea guía hasta su marca. [x] es donde va el texto y [xBorde] el borde de la pieza.
     */
    private fun apilar(c: Canvas, etiquetas: List<Pair<Float, String>>, x: Float, xBorde: Float, p: Paint, color: Int) {
        var libre = Float.MAX_VALUE
        val guia = trazo(1.5f, color)
        etiquetas.sortedByDescending { it.first }.forEach { (y, t) ->
            val yt = minOf(y, libre - 34f)
            if (yt < y - 2f) {
                val xs = if (p.textAlign == Paint.Align.RIGHT) x + 4f else x - 4f
                c.drawLine(xBorde, y, (xBorde + xs) / 2f, y, guia)
                c.drawLine((xBorde + xs) / 2f, y, xs, yt - 10f, guia)
            }
            c.drawText(t, x, yt + 10f, p)
            libre = yt
        }
    }

    /** Un tablero echado (piso, techo, repisa): las marcas a lo ancho desde su punta izquierda, el frente abajo. */
    private fun horizontal(c: Canvas, t: RoperoProduccion.Tablero, w: Int, h: Int) {
        val (rf, k) = encajar(w, h, t.ancho, t.alto, 120f, 260f, 200f)
        c.drawRect(rf, Paint().apply { color = Color.parseColor("#F5F5F5") })
        c.drawRect(rf, trazo(3f, NEGRO))
        c.drawText("frente", rf.left, rf.bottom + 36f, texto(26f, Color.GRAY))
        t.marcas.forEach { m ->
            val x = rf.left + m.a * k
            c.drawLine(x, rf.top, x, rf.bottom, trazo(3f, NEGRO))
            c.drawText("${RoperoProduccion.fmt(m.a)} (${m.cara})", x, rf.top - 14f, texto(30f, NEGRO, centro = true))
            m.agujero?.let { ag ->
                val xa = rf.left + ag * k
                m.hondo.forEach { hy -> c.drawCircle(xa, rf.bottom - hy * k, 7f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ROSADO }) }
                c.drawText(RoperoProduccion.fmt(ag), xa, rf.bottom + 72f, texto(30f, ROSADO, centro = true))
                m.hondo.forEach { hy -> c.drawText(RoperoProduccion.fmt(hy), rf.right + 16f, rf.bottom - hy * k + 10f, texto(26f, ROSADO)) }
            }
        }
    }

    private fun abreviar(m: RoperoProduccion.Marca): String = when (m.tipo) {
        RoperoProduccion.TipoMarca.UNION -> if (m.cara.contains("izquierda")) "◂" else if (m.cara.contains("derecha")) "▸" else ""
        RoperoProduccion.TipoMarca.RIEL -> "riel"
        RoperoProduccion.TipoMarca.BISAGRA -> "bis."
        RoperoProduccion.TipoMarca.CAZOLETA -> "caz."
    }
}
