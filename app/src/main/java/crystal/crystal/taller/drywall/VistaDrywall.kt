package crystal.crystal.taller.drywall

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

/**
 * El drywall dibujado como se arma: la estructura (rieles arriba y abajo, parantes cada
 * separación; en el cielo raso el perímetro con portantes y cruzados) o el emplanchado (las
 * planchas de 1.22 x 2.44 puestas de pie con sus juntas). Cotas en metros.
 *
 * Tabique y forro se dibujan desplegados, tramo tras tramo, con los dobleces a trazos; si no es
 * recto, arriba a la derecha va la forma vista en planta (2026-10-11).
 */
class VistaDrywall @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var drywall: Drywall = Drywall()
        set(value) { field = value; invalidate() }

    /** La unidad de las cotas del alzado (la elegida para las medidas): sufijo y cuánto es en m. */
    var unidad: Pair<String, Float> = "m" to 1f
        set(value) { field = value; invalidate() }
    private fun medida(m: Float): String = DrywallCalculo.fmt(m / unidad.second)

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
    private val pEsquina = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E65100"); style = Paint.Style.STROKE; strokeWidth = 1.5f * dp
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(6f * dp, 4f * dp), 0f)
    }
    private val pMuro = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(120, 120, 120); style = Paint.Style.FILL }
    private val pPlanta = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(58, 58, 58); style = Paint.Style.STROKE; strokeWidth = 2f * dp; strokeJoin = Paint.Join.ROUND }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val d = drywall
        val esCielo = d.tipo == TipoDrywall.CIELO_RASO
        val conPlanta = !esCielo && d.forma != FormaDrywall.RECTA
        val largo = d.desarrolloM.coerceAtLeast(0.05f)
        val alto = d.altoM.coerceAtLeast(0.05f)
        val margen = 22f * dp
        // Con la planta: si el alzado es angosto (un cajón), la planta va a su derecha; si no, arriba.
        var zona = RectF(0f, 0f, width.toFloat(), height.toFloat())
        var zonaPlanta: RectF? = null
        if (conPlanta) {
            if (largo / alto < 1f) {
                val corte = width * 0.36f
                zonaPlanta = RectF(corte, 0f, width.toFloat(), height.toFloat())
                zona = RectF(0f, 0f, corte, height.toFloat())
            } else {
                val corte = height * 0.42f
                zonaPlanta = RectF(0f, 0f, width.toFloat(), corte)
                zona = RectF(0f, corte, width.toFloat(), height.toFloat())
            }
        }
        val escala = min((zona.width() - 2 * margen) / largo, (zona.height() - 2 * margen) / alto)
        val izq = zona.left + (zona.width() - largo * escala) / 2f
        val abajo = zona.bottom - (zona.height() - alto * escala) / 2f
        fun x(m: Float) = izq + m * escala
        fun y(m: Float) = abajo - m * escala
        canvas.drawRect(x(0f), y(alto), x(largo), y(0f), pFondo)

        if (esCielo) {
            if (mostrarPlanchas) dibujarPlanchas(canvas, listOf(largo), alto, ::x, ::y)
            else {
                // Visto desde abajo: el ángulo perimetral, los portantes cada 1.22 y los cruzados cada 0.61.
                pRiel.strokeWidth = 3f * dp
                pPerfil.strokeWidth = 2f * dp
                canvas.drawRect(x(0f), y(alto), x(largo), y(0f), pRiel)
                val portantes = floor(alto / 1.22f).toInt() + 1
                for (i in 1 until portantes) canvas.drawLine(x(0f), y(i * 1.22f), x(largo), y(i * 1.22f), pRiel)
                val cruzados = floor(largo / 0.61f).toInt() + 1
                for (i in 1 until cruzados) canvas.drawLine(x(i * 0.61f), y(0f), x(i * 0.61f), y(alto), pPerfil)
            }
        } else {
            dibujarMuro(canvas, d, alto, escala, ::x, ::y)
        }
        canvas.drawRect(x(0f), y(alto), x(largo), y(0f), pBorde)

        // Cotas en la unidad elegida: el largo (el desarrollo, si dobla) arriba y el alto a la izquierda.
        val yA = y(alto) - 8 * dp
        canvas.drawLine(x(0f), yA, x(largo), yA, pCota)
        canvas.drawText("${medida(d.desarrolloM)} ${unidad.first}", x(largo / 2f), yA - 3 * dp, pTexto)
        val xA = x(0f) - 8 * dp
        canvas.drawLine(xA, y(0f), xA, y(alto), pCota)
        canvas.save()
        canvas.rotate(-90f, xA - 3 * dp, y(alto / 2f))
        canvas.drawText("${medida(d.altoM)} ${unidad.first}", xA - 3 * dp, y(alto / 2f), pTexto)
        canvas.restore()
        zonaPlanta?.let { dibujarPlanta(canvas, d, it) }
    }

    /** Las planchas de pie de cada tramo: tiras de 1.22 como mucho, juntas cada 2.44. */
    private fun dibujarPlanchas(canvas: Canvas, tramos: List<Float>, alto: Float, x: (Float) -> Float, y: (Float) -> Float) {
        var inicio = 0f
        tramos.forEach { ancho ->
            var px = inicio
            val fin = inicio + ancho
            while (px < fin - 0.001f) {
                val px1 = min(px + 1.22f, fin)
                var py = 0f
                while (py < alto - 0.001f) {
                    val py1 = min(py + 2.44f, alto)
                    canvas.drawRect(x(px), y(py1), x(px1), y(py), pPlancha)
                    canvas.drawRect(x(px), y(py1), x(px1), y(py), pJunta)
                    py = py1
                }
                px = px1
            }
            inicio = fin
        }
    }

    /**
     * Tabique o forro desplegado: los tramos uno tras otro con los dobleces a trazos. En estructura,
     * los rieles y los parantes como los cuenta el cálculo (el del arranque y, en cada tramo, los de
     * la separación hasta su punta); en planchas, las tiras de cada tramo.
     */
    private fun dibujarMuro(canvas: Canvas, d: Drywall, alto: Float, escala: Float, x: (Float) -> Float, y: (Float) -> Float) {
        val tramos = d.tramos
        val cortes = tramos.runningFold(0f) { a, b -> a + b }
        val largo = cortes.last()
        if (mostrarPlanchas) {
            dibujarPlanchas(canvas, tramos, alto, x, y)
        } else {
            val perfil = d.perfil.anchoMm / 1000f
            pRiel.strokeWidth = (perfil * escala).coerceAtLeast(3f * dp)
            pPerfil.strokeWidth = (perfil * escala).coerceAtLeast(2f * dp)
            canvas.drawLine(x(0f), y(perfil / 2f), x(largo), y(perfil / 2f), pRiel)
            canvas.drawLine(x(0f), y(alto - perfil / 2f), x(largo), y(alto - perfil / 2f), pRiel)
            // Los verticales del despiece: el riel contra el muro más oscuro que los parantes; y los amarres.
            val despiece = DrywallCalculo.despiece(d)
            despiece.amarresM.forEach { h -> canvas.drawLine(x(0f), y(h), x(largo), y(h), pRiel) }
            despiece.verticales.filter { it.tramo >= 0 }.forEach { v ->
                val p = cortes[v.tramo] + v.enM * tramos[v.tramo] / despiece.armazonM[v.tramo].coerceAtLeast(0.01f)
                val px = p.coerceIn(perfil / 2f, largo - perfil / 2f)
                canvas.drawLine(x(px), y(perfil), x(px), y(alto - perfil), if (v.esRiel) pRiel else pPerfil)
            }
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
        // Los dobleces a trazos y, abajo, lo que mide cada tramo si cabe.
        for (i in 1 until cortes.size - 1) canvas.drawLine(x(cortes[i]), y(0f), x(cortes[i]), y(alto), pEsquina)
        if (tramos.size > 1) tramos.forEachIndexed { i, t ->
            val texto = medida(t)
            if (pTexto.measureText(texto) < (x(cortes[i + 1]) - x(cortes[i])) - 2f * dp) {
                canvas.drawText(texto, x(cortes[i] + t / 2f), y(0f) + 12f * dp, pTexto)
            }
        }
    }

    private val pPlanchaPlanta = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(244, 170, 180); style = Paint.Style.FILL }
    private val pPerfilPlanta = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(160, 150, 210); style = Paint.Style.FILL }
    private val pContorno = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(58, 58, 58); style = Paint.Style.STROKE; strokeWidth = 0.8f * dp }

    /**
     * El corte en planta, como lo dibuja el usuario (2026-10-10): la pared en gris (en la C), la
     * plancha de cada tramo con su ancho de corte en cm (en la C los lados tapan el canto del
     * frente), el riel contra el muro y los parantes. La curva, con su flecha.
     */
    private fun dibujarPlanta(canvas: Canvas, d: Drywall, zona: RectF) {
        if (d.forma == FormaDrywall.CURVA) {
            val arco = crystal.crystal.taller.ArcoEsquina.deDesarrolloYFlecha(d.largoM, d.lado2M) ?: return
            val cajaAncho = zona.width() - 30f * dp; val cajaAlto = zona.height() - 30f * dp
            val k = min(cajaAncho / arco.cuerda.coerceAtLeast(0.01f), cajaAlto / (2f * arco.flecha).coerceAtLeast(0.01f))
            val cuerda = arco.cuerda * k; val flecha = arco.flecha * k
            val x0 = zona.centerX() - cuerda / 2f; val y0 = zona.top + 12f * dp
            val p = Path().apply { moveTo(x0, y0); quadTo(x0 + cuerda / 2f, y0 + 2f * flecha, x0 + cuerda, y0) }
            canvas.drawPath(p, pPlanta)
            canvas.drawText("flecha ${DrywallCalculo.fmt(d.lado2M * 100f)}", zona.centerX(), y0 + flecha + 14f * dp, pTexto)
            return
        }
        // La cara de afuera de la plancha, recorriendo los tramos (y hacia abajo del papel).
        val puntos: List<Pair<Float, Float>> = when (d.forma) {
            FormaDrywall.L -> listOf(0f to 0f, d.largoM to 0f, d.largoM to d.lado2M)
            FormaDrywall.C -> listOf(0f to 0f, 0f to d.lado2M, d.largoM to d.lado2M, d.largoM to (d.lado2M - d.lado3M))
            FormaDrywall.S -> listOf(0f to 0f, d.largoM to 0f, d.largoM to d.lado2M, d.largoM + d.lado3M to d.lado2M)
            else -> return
        }
        val despiece = DrywallCalculo.despiece(d)
        val e = despiece.espesorM
        val perfil = d.perfil.anchoMm / 1000f
        val mx = puntos.minOf { it.first }; val my = puntos.minOf { it.second }
        val ancho = puntos.maxOf { it.first } - mx
        val hondo = puntos.maxOf { it.second } - my
        val holgura = 26f * dp
        val k = min((zona.width() - 2 * holgura) / ancho.coerceAtLeast(0.01f), (zona.height() - 2 * holgura) / hondo.coerceAtLeast(0.01f))
        val ox = zona.centerX() - ancho * k / 2f
        val oy = zona.centerY() - hondo * k / 2f
        fun px(m: Float) = ox + (m - mx) * k
        fun py(m: Float) = oy + (m - my) * k
        // Para dentro: en la C hacia el centro del cajón; en la L y la S, a la izquierda del recorrido.
        val cx = puntos.map { it.first }.average().toFloat(); val cy = puntos.map { it.second }.average().toFloat()
        val tramos = puntos.zipWithNext()
        val direcciones = tramos.map { (a, b) ->
            val l = kotlin.math.hypot(b.first - a.first, b.second - a.second).coerceAtLeast(0.0001f)
            ((b.first - a.first) / l) to ((b.second - a.second) / l)
        }
        val normales = tramos.mapIndexed { i, (a, b) ->
            val (ux, uy) = direcciones[i]
            var n = -uy to ux
            if (d.forma == FormaDrywall.C) {
                val mxT = (a.first + b.first) / 2f; val myT = (a.second + b.second) / 2f
                if ((cx - mxT) * n.first + (cy - myT) * n.second < 0f) n = uy to -ux
            }
            n
        }
        // Un rectángulo en metros: a lo largo del tramo i de [desde] a [hasta], y hacia dentro de [h0] a [h1].
        fun pieza(i: Int, desde: Float, hasta: Float, h0: Float, h1: Float, paint: Paint) {
            val (ax, ay) = tramos[i].first
            val (ux, uy) = direcciones[i]; val (nx, ny) = normales[i]
            val xs = listOf(desde, hasta).flatMap { s -> listOf(h0, h1).map { h -> ax + ux * s + nx * h } }
            val ys = listOf(desde, hasta).flatMap { s -> listOf(h0, h1).map { h -> ay + uy * s + ny * h } }
            val r = RectF(px(xs.min()), py(ys.min()), px(xs.max()), py(ys.max()))
            canvas.drawRect(r, paint); canvas.drawRect(r, pContorno)
        }
        if (d.forma == FormaDrywall.C) canvas.drawRect(px(mx) - 10f * dp, py(my) - 8f * dp, px(mx + ancho) + 10f * dp, py(my), pMuro)
        val grueso = (e * k).coerceAtLeast(2.5f * dp) / k
        val caras = if (d.tipo == TipoDrywall.TABIQUE) listOf(0f, grueso + perfil) else listOf(0f)
        tramos.forEachIndexed { i, (a, b) ->
            val largoT = kotlin.math.hypot(b.first - a.first, b.second - a.second)
            val tapaInicio = when (d.forma) { FormaDrywall.C, FormaDrywall.S, FormaDrywall.L -> i == 1; else -> false }
            val tapaFin = (d.forma == FormaDrywall.C || d.forma == FormaDrywall.S) && i == 1
            val desde = if (tapaInicio) grueso else 0f
            val hasta = largoT - if (tapaFin) grueso else 0f
            caras.forEach { h -> pieza(i, desde, hasta, h, h + grueso, pPlanchaPlanta) }
            // El ancho de corte de la plancha, por fuera del tramo.
            val (nx, ny) = normales[i]
            val sx = px((a.first + b.first) / 2f) - nx * 11f * dp
            val sy = py((a.second + b.second) / 2f) - ny * 11f * dp + pTexto.textSize / 3f
            val anchoCorte = largoT - DrywallCalculo.planchasQueLaTapan(d, i) * e
            canvas.drawText(DrywallCalculo.fmt(anchoCorte * 100f), sx, sy, pTexto)
        }
        // Los perfiles, del grueso de su ala a lo largo y de su ancho hacia dentro, dentro del armazón.
        val ala = (0.035f * k).coerceAtLeast(4f * dp) / k
        val fondo = (perfil * k).coerceAtLeast(4f * dp) / k
        despiece.verticales.filter { it.tramo >= 0 }.forEach { v ->
            val i = v.tramo
            val largoT = kotlin.math.hypot(tramos[i].second.first - tramos[i].first.first, tramos[i].second.second - tramos[i].first.second)
            val inicio = if (i > 0) grueso else 0f
            val fin = largoT - if (i < tramos.lastIndex) grueso else 0f
            val enT = inicio + v.enM / despiece.armazonM[i].coerceAtLeast(0.01f) * (fin - inicio)
            val desde = (enT - ala / 2f).coerceIn(inicio, (fin - ala).coerceAtLeast(inicio))
            pieza(i, desde, desde + ala, grueso, grueso + fondo, pPerfilPlanta)
        }
    }
}
