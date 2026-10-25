package crystal.crystal.taller

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * La escena de Medidas 3D en perspectiva: todo lo dibujado en las vistas, junto. Un dedo la gira,
 * dos la acercan y la mueven, y un toque elige una pieza (se ve de qué vista es y lo que mide; un
 * toque en el rótulo lleva a editarla en su vista). Arriba, botones para mirarla de frente, desde
 * arriba, de lado o en isométrica, y para pasar de perspectiva a proyección paralela.
 */
class VistaEscena3d @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var escena: Escena3d? = null
        private set

    /** Cuánto se giró alrededor del eje vertical, y cuánto se mira desde arriba, en grados. */
    var giroGrados = 30f
    var elevacionGrados = 22f
    var zoom = 1f
    var desplazamientoX = 0f
    var desplazamientoY = 0f
    var perspectiva = true
        set(v) { field = v; invalidate() }

    /** El nombre de la vista de la que salió una pieza (por su ordinal). */
    var nombreDeVista: (Int) -> String = { "Vista" }

    /** Ir a editar esa pieza en su vista. */
    var alEditar: ((vista: Int, indice: Int) -> Unit)? = null

    /** Lo que mide una pieza, ya escrito (por ejemplo "120 × 210 cm"), si quien la arma lo sabe decir mejor. */
    var describir: ((vista: Int, indice: Int) -> String?)? = null

    private var elegido: Pair<Int, Int>? = null

    private val dp = resources.displayMetrics.density

    /** Sin botones, ejes ni rótulos: para la miniatura de la tira de vistas. */
    var conControles = true

    /** Dónde empieza la columna de botones, para no quedar debajo de un aviso de arriba (dp). */
    var margenSuperiorDp = 8f

    fun mostrar(nueva: Escena3d) {
        val primera = escena == null
        escena = nueva
        if (elegido != null && nueva.caras.none { it.vista == elegido!!.first && it.indice == elegido!!.second } &&
            nueva.aristas.none { it.vista == elegido!!.first && it.indice == elegido!!.second }) elegido = null
        if (primera) encuadrar()
        invalidate()
    }

    fun encuadrar() {
        zoom = 1f
        desplazamientoX = 0f
        desplazamientoY = 0f
        invalidate()
    }

    // ---- La proyección ----

    private var cx = 0f
    private var cy = 0f
    private var cz = 0f
    private var radio = 100f

    private fun prepararCentro() {
        val (min, max) = escena?.limites() ?: (P3(0f, 0f, 0f) to P3(100f, 100f, 100f))
        cx = (min.x + max.x) / 2f
        cy = (min.y + max.y) / 2f
        cz = (min.z + max.z) / 2f
        radio = max(50f, sqrt((max.x - min.x).let { it * it } + (max.y - min.y).let { it * it } + (max.z - min.z).let { it * it }) / 2f)
    }

    /** Lo girado: x a la derecha, y hacia arriba, z lejos de quien mira. */
    private fun girar(p: P3): FloatArray {
        val t = Math.toRadians(giroGrados.toDouble())
        val f = Math.toRadians(elevacionGrados.toDouble())
        val dx = p.x - cx
        val dy = p.y - cy
        val dz = p.z - cz
        val x1 = (dx * cos(t) + dz * sin(t)).toFloat()
        val z1 = (-dx * sin(t) + dz * cos(t)).toFloat()
        val y2 = (dy * cos(f) + z1 * sin(f)).toFloat()
        val z2 = (-dy * sin(f) + z1 * cos(f)).toFloat()
        return floatArrayOf(x1, y2, z2)
    }

    private fun escala() = min(width, height) * 0.40f / radio * zoom

    private fun aPantalla(g: FloatArray): PointF {
        val d = radio * 3f
        val k = if (perspectiva) d / max(d * 0.15f, d + g[2]) else 1f
        val s = escala()
        return PointF(width / 2f + desplazamientoX + g[0] * s * k, height / 2f + desplazamientoY - g[1] * s * k)
    }

    // ---- El dibujo ----

    private val fondo = Color.parseColor("#F7F4EC")
    private val caraPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val aristaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; color = Color.parseColor("#37474F"); strokeCap = Paint.Cap.ROUND
    }
    private val elegidaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; color = Color.rgb(230, 81, 0); strokeCap = Paint.Cap.ROUND
    }
    private val rejillaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; color = Color.argb(70, 120, 144, 156)
    }
    private val textoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#37474F") }
    private val pildoraFondo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(235, 255, 255, 255) }
    private val pildoraActiva = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#B3E5FC") }
    private val pildoraBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE; color = Color.rgb(21, 101, 192)
    }

    private class Pildora(val texto: String, val accion: () -> Unit, val activa: Boolean = false) {
        val rect = RectF()
    }

    private var pildoras: List<Pildora> = emptyList()
    private var pildoraEditar: RectF? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(fondo)
        val e = escena
        prepararCentro()
        if (e == null || e.vacia) {
            textoPaint.textSize = 14f * dp
            textoPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("Dibuja en la frontal, la planta o las laterales:", width / 2f, height / 2f - 10f * dp, textoPaint)
            canvas.drawText("todo se ve aquí junto, en 3D.", width / 2f, height / 2f + 12f * dp, textoPaint)
            if (conControles) dibujarPildoras(canvas)
            return
        }
        if (conControles) dibujarRejilla(canvas, e)
        val luz = floatArrayOf(-0.35f, 0.55f, -0.75f).let { v -> val n = sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]); FloatArray(3) { v[it] / n } }
        // De lo más lejano a lo más cercano: lo de delante tapa (a medias, es vidrio) lo de atrás.
        val caras = e.caras.map { c -> c to c.puntos.map { girar(it) } }
            .sortedByDescending { (_, g) -> g.sumOf { it[2].toDouble() } / g.size }
        caras.forEach { (c, g) ->
            val n = normal(g)
            val brillo = 0.55f + 0.45f * abs(n[0] * luz[0] + n[1] * luz[1] + n[2] * luz[2])
            val esElegida = elegido == (c.vista to c.indice)
            caraPaint.color = if (esElegida) Color.argb(150, 255, 167, 38)
            else Color.argb(105, (110 * brillo).toInt(), (185 * brillo).toInt(), (225 * brillo).toInt().coerceAtMost(255))
            val camino = Path()
            g.forEachIndexed { i, q -> val p = aPantalla(q); if (i == 0) camino.moveTo(p.x, p.y) else camino.lineTo(p.x, p.y) }
            camino.close()
            canvas.drawPath(camino, caraPaint)
        }
        aristaPaint.strokeWidth = 1.8f * dp
        elegidaPaint.strokeWidth = 3.2f * dp
        e.aristas.forEach { a ->
            val p = aPantalla(girar(a.a))
            val q = aPantalla(girar(a.b))
            canvas.drawLine(p.x, p.y, q.x, q.y, if (elegido == (a.vista to a.indice)) elegidaPaint else aristaPaint)
        }
        if (!conControles) return
        dibujarEjes(canvas)
        dibujarPildoras(canvas)
        dibujarRotuloElegido(canvas, e)
    }

    private fun normal(g: List<FloatArray>): FloatArray {
        if (g.size < 3) return floatArrayOf(0f, 0f, 1f)
        // Newell: vale aunque la cara tenga muchos puntos.
        var nx = 0f; var ny = 0f; var nz = 0f
        for (i in g.indices) {
            val a = g[i]; val b = g[(i + 1) % g.size]
            nx += (a[1] - b[1]) * (a[2] + b[2])
            ny += (a[2] - b[2]) * (a[0] + b[0])
            nz += (a[0] - b[0]) * (a[1] + b[1])
        }
        val l = sqrt(nx * nx + ny * ny + nz * nz).takeIf { it > 1e-6f } ?: return floatArrayOf(0f, 0f, 1f)
        return floatArrayOf(nx / l, ny / l, nz / l)
    }

    /** El suelo: una rejilla bajo la escena, de paso redondo según lo que mide. */
    private fun dibujarRejilla(canvas: Canvas, e: Escena3d) {
        val (min, max) = e.limites() ?: return
        val lado = max(max.x - min.x, max.z - min.z)
        val paso = listOf(10f, 20f, 50f, 100f, 200f, 500f).firstOrNull { lado / it <= 12f } ?: 1000f
        val margen = paso
        val x0 = (Math.floor(((min.x - margen) / paso).toDouble()) * paso).toFloat()
        val x1 = (Math.ceil(((max.x + margen) / paso).toDouble()) * paso).toFloat()
        val z0 = (Math.floor(((min.z - margen) / paso).toDouble()) * paso).toFloat()
        val z1 = (Math.ceil(((max.z + margen) / paso).toDouble()) * paso).toFloat()
        val y = min.y
        rejillaPaint.strokeWidth = 1f * dp
        var x = x0
        while (x <= x1 + 0.01f) {
            val a = aPantalla(girar(P3(x, y, z0))); val b = aPantalla(girar(P3(x, y, z1)))
            canvas.drawLine(a.x, a.y, b.x, b.y, rejillaPaint)
            x += paso
        }
        var z = z0
        while (z <= z1 + 0.01f) {
            val a = aPantalla(girar(P3(x0, y, z))); val b = aPantalla(girar(P3(x1, y, z)))
            canvas.drawLine(a.x, a.y, b.x, b.y, rejillaPaint)
            z += paso
        }
        textoPaint.textSize = 10f * dp
        textoPaint.textAlign = Paint.Align.LEFT
        canvas.drawText("cuadro de ${paso.toInt()} cm", 10f * dp, height - 10f * dp, textoPaint)
    }

    /** Los ejes, en una esquina: X a lo ancho, Y arriba, Z al fondo. */
    private fun dibujarEjes(canvas: Canvas) {
        val origen = PointF(34f * dp, height - 46f * dp)
        val largo = 22f * dp
        val ejes = listOf(
            Triple(P3(1f, 0f, 0f), Color.rgb(211, 47, 47), "X"),
            Triple(P3(0f, 1f, 0f), Color.rgb(56, 142, 60), "Y"),
            Triple(P3(0f, 0f, 1f), Color.rgb(25, 118, 210), "Z")
        )
        val t = Math.toRadians(giroGrados.toDouble())
        val f = Math.toRadians(elevacionGrados.toDouble())
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { strokeWidth = 2.5f * dp; strokeCap = Paint.Cap.ROUND; textSize = 10f * dp }
        ejes.forEach { (v, color, nombre) ->
            val x1 = (v.x * cos(t) + v.z * sin(t)).toFloat()
            val z1 = (-v.x * sin(t) + v.z * cos(t)).toFloat()
            val y2 = (v.y * cos(f) + z1 * sin(f)).toFloat()
            p.color = color
            val fin = PointF(origen.x + x1 * largo, origen.y - y2 * largo)
            canvas.drawLine(origen.x, origen.y, fin.x, fin.y, p)
            canvas.drawText(nombre, fin.x + 2f * dp, fin.y - 2f * dp, p)
        }
    }

    private fun armarPildoras(): List<Pildora> = listOf(
        Pildora("Frente", { irA(0f, 0f) }),
        Pildora("Arriba", { irA(0f, 89f) }),
        Pildora("Izq.", { irA(-90f, 0f) }),
        Pildora("Der.", { irA(90f, 0f) }),
        Pildora("3D", { irA(30f, 22f) }),
        Pildora(if (perspectiva) "Perspectiva" else "Paralela", { perspectiva = !perspectiva }, activa = perspectiva),
        Pildora("Encuadrar", { encuadrar() })
    )

    private fun dibujarPildoras(canvas: Canvas) {
        pildoras = armarPildoras()
        textoPaint.textSize = 12f * dp
        textoPaint.textAlign = Paint.Align.LEFT
        pildoraBorde.strokeWidth = 1.2f * dp
        // En columna, pegados a la derecha: arriba suele ir el aviso de la pantalla y abajo el rótulo.
        val alto = 30f * dp
        val m = 6f * dp
        val ancho = pildoras.maxOf { textoPaint.measureText(it.texto) } + 18f * dp
        var y = margenSuperiorDp * dp
        pildoras.forEach { p ->
            p.rect.set(width - m - ancho, y, width - m, y + alto)
            canvas.drawRoundRect(p.rect, alto / 2f, alto / 2f, if (p.activa) pildoraActiva else pildoraFondo)
            canvas.drawRoundRect(p.rect, alto / 2f, alto / 2f, pildoraBorde)
            canvas.drawText(p.texto, p.rect.left + 9f * dp, p.rect.centerY() - (textoPaint.descent() + textoPaint.ascent()) / 2f, textoPaint)
            y += alto + m
        }
    }

    /** De qué vista es la pieza elegida y lo que mide; tocarlo lleva a editarla. */
    private fun dibujarRotuloElegido(canvas: Canvas, e: Escena3d) {
        pildoraEditar = null
        val (vista, indice) = elegido ?: return
        val medidas = describir?.invoke(vista, indice) ?: e.medidasDe(vista, indice)?.let { (x, y, z) ->
            listOf(x, y, z).filter { it > 0.4f }.joinToString(" × ") { formato(it) } + " cm"
        } ?: return
        val texto = "${nombreDeVista(vista)} · $medidas · ✎ editar"
        textoPaint.textSize = 13f * dp
        textoPaint.textAlign = Paint.Align.LEFT
        val ancho = min(width - 12f * dp, textoPaint.measureText(texto) + 24f * dp)
        val alto = 36f * dp
        val r = RectF((width - ancho) / 2f, height - alto - 10f * dp, (width + ancho) / 2f, height - 10f * dp)
        canvas.drawRoundRect(r, alto / 2f, alto / 2f, pildoraFondo)
        canvas.drawRoundRect(r, alto / 2f, alto / 2f, elegidaPaint.apply { strokeWidth = 1.5f * dp })
        canvas.drawText(texto, r.left + 12f * dp, r.centerY() - (textoPaint.descent() + textoPaint.ascent()) / 2f, textoPaint)
        pildoraEditar = r
    }

    private fun formato(v: Float): String =
        if (abs(v - Math.round(v)) < 0.05f) Math.round(v).toString() else String.format(java.util.Locale.US, "%.1f", v)

    private var animacion: ValueAnimator? = null

    /** Gira hasta ese punto de vista, sin saltos. */
    private fun irA(giro: Float, elevacion: Float) {
        animacion?.cancel()
        val g0 = giroGrados
        var dg = (giro - g0) % 360f
        if (dg > 180f) dg -= 360f
        if (dg < -180f) dg += 360f
        val e0 = elevacionGrados
        animacion = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 280
            addUpdateListener {
                val k = it.animatedValue as Float
                giroGrados = g0 + dg * k
                elevacionGrados = e0 + (elevacion - e0) * k
                invalidate()
            }
            start()
        }
    }

    // ---- Los toques ----

    private val escalador = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            zoom = (zoom * detector.scaleFactor).coerceIn(0.2f, 12f)
            invalidate()
            return true
        }
    })

    private var bajada: PointF? = null
    private var ultimo: PointF? = null
    private var focoPrevio: PointF? = null
    private var conDosDedos = false
    private var ultimoToque = 0L

    override fun onTouchEvent(event: MotionEvent): Boolean {
        escalador.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                animacion?.cancel()
                bajada = PointF(event.x, event.y)
                ultimo = PointF(event.x, event.y)
                conDosDedos = false
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                conDosDedos = true
                focoPrevio = foco(event)
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount >= 2) {
                    val f = foco(event)
                    focoPrevio?.let { desplazamientoX += f.x - it.x; desplazamientoY += f.y - it.y }
                    focoPrevio = f
                    invalidate()
                } else if (!conDosDedos) {
                    val u = ultimo ?: return true
                    giroGrados -= (event.x - u.x) * 0.45f
                    elevacionGrados = (elevacionGrados + (event.y - u.y) * 0.45f).coerceIn(-89f, 89f)
                    ultimo = PointF(event.x, event.y)
                    invalidate()
                }
            }
            MotionEvent.ACTION_POINTER_UP -> focoPrevio = null
            MotionEvent.ACTION_UP -> {
                val b = bajada
                val esToque = b != null && !conDosDedos && hypot(event.x - b.x, event.y - b.y) < 10f * dp
                if (esToque) tocar(event.x, event.y)
                bajada = null
                ultimo = null
                focoPrevio = null
            }
            MotionEvent.ACTION_CANCEL -> { bajada = null; ultimo = null; focoPrevio = null }
        }
        return true
    }

    private fun foco(event: MotionEvent): PointF {
        var x = 0f; var y = 0f
        for (i in 0 until event.pointerCount) { x += event.getX(i); y += event.getY(i) }
        return PointF(x / event.pointerCount, y / event.pointerCount)
    }

    private fun tocar(x: Float, y: Float) {
        pildoras.firstOrNull { it.rect.contains(x, y) }?.let { it.accion(); invalidate(); return }
        pildoraEditar?.takeIf { it.contains(x, y) }?.let {
            elegido?.let { (v, i) -> alEditar?.invoke(v, i) }
            return
        }
        val ahora = System.currentTimeMillis()
        val dobleToque = ahora - ultimoToque < 300
        ultimoToque = ahora
        val elegidoAntes = elegido
        elegido = piezaEn(x, y)
        if (elegido == null && elegidoAntes == null && dobleToque) encuadrar()
        invalidate()
    }

    /** La pieza bajo el dedo: la cara más cercana que lo contiene, o una arista a tiro. */
    private fun piezaEn(x: Float, y: Float): Pair<Int, Int>? {
        val e = escena ?: return null
        val caras = e.caras.map { c -> c to c.puntos.map { girar(it) } }
            .sortedBy { (_, g) -> g.sumOf { it[2].toDouble() } / g.size }
        caras.forEach { (c, g) ->
            val pts = g.map { aPantalla(it) }
            if (contiene(pts, x, y)) return c.vista to c.indice
        }
        var mejor: Pair<Int, Int>? = null
        var mejorDist = 16f * dp
        e.aristas.forEach { a ->
            val p = aPantalla(girar(a.a)); val q = aPantalla(girar(a.b))
            val d = distanciaASegmento(x, y, p, q)
            if (d < mejorDist) { mejorDist = d; mejor = a.vista to a.indice }
        }
        return mejor
    }

    private fun contiene(pts: List<PointF>, x: Float, y: Float): Boolean {
        var dentro = false
        var j = pts.lastIndex
        for (i in pts.indices) {
            val a = pts[i]; val b = pts[j]
            if ((a.y > y) != (b.y > y) && x < (b.x - a.x) * (y - a.y) / (b.y - a.y) + a.x) dentro = !dentro
            j = i
        }
        return dentro
    }

    private fun distanciaASegmento(x: Float, y: Float, a: PointF, b: PointF): Float {
        val vx = b.x - a.x; val vy = b.y - a.y
        val l2 = vx * vx + vy * vy
        val t = if (l2 < 1e-4f) 0f else (((x - a.x) * vx + (y - a.y) * vy) / l2).coerceIn(0f, 1f)
        return hypot(x - (a.x + vx * t), y - (a.y + vy * t))
    }
}
