package crystal.crystal.optimizadores

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Dibuja un vidrio curvo como un segmento de cilindro (que es lo que fisicamente es una
 * lamina de vidrio doblada), a partir de sus medidas: desarrollo (longitud del arco),
 * cuerda, flecha y largo (altura del panel).
 *
 * Estrategia de dibujo:
 *  - cuerda  -> ancho horizontal del panel.
 *  - largo   -> altura vertical (la cara del vidrio).
 *  - flecha  -> curvatura; los bordes superior e inferior se ven como arcos achatados
 *               (la profundidad real se reduce por escorzo: una circunferencia horizontal
 *               vista de frente se proyecta como elipse). El arco es SIMETRICO, sin sesgo.
 *  - La curvatura se hace evidente con generatrices verticales que se agrupan hacia los
 *    extremos (donde la superficie gira y se aleja del observador). Esa es la clave para
 *    que se lea como superficie curva rigida y no como tela.
 *  - desarrollo -> longitud real del arco; se rotula sobre el borde superior.
 *
 * El arco se construye con el radio geometrico (R = C^2/8F + F/2). Sin largo se dibuja
 * solo el perfil del arco (corte transversal).
 */
class VistaVidrioCurvo(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val pincelBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeJoin = Paint.Join.ROUND
    }
    private val pincelCanto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(40, 40, 40)
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }
    private val pincelGeneratriz = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(150, 170, 185)
        style = Paint.Style.STROKE
        strokeWidth = 1.6f
    }
    private val pincelSuperficie = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(214, 233, 244) // cara de vidrio opaca
        style = Paint.Style.FILL
    }
    private val pincelCota = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val pincelTexto = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }

    private var desarrollo = 0f
    private var cuerda = 0f
    private var flecha = 0f
    private var largo = 0f
    private val muestrasArco = 96
    private val generatrices = 9

    // Escorzo de la profundidad (flecha) al proyectar el arco horizontal como elipse.
    private val escorzo = 0.42f

    fun actualizarMedidas(desarrollo: Float, cuerda: Float, flecha: Float, largo: Float) {
        this.desarrollo = desarrollo
        this.cuerda = cuerda
        this.flecha = flecha
        this.largo = largo
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (cuerda <= 0f || flecha <= 0f) return
        if (largo > 0f) dibujarPanel(canvas) else dibujarPerfil(canvas)
    }

    // ----- Panel: segmento de cilindro (vidrio curvo con altura = largo) -----

    private fun dibujarPanel(canvas: Canvas) {
        val margen = 24f
        val margenSuperior = 92f
        val margenInferior = 84f
        val margenLateral = 96f

        val arco = construirArcoModelo() // (u a lo largo de la cuerda, profundidad = comba)
        if (arco.isEmpty()) return

        val anchoModelo = cuerda
        val altoModelo = largo + flecha * escorzo

        val anchoDisp = (width - margen * 2f - margenLateral).coerceAtLeast(1f)
        val altoDisp = (height - margen * 2f - margenSuperior - margenInferior).coerceAtLeast(1f)
        val escala = min(
            anchoDisp / anchoModelo.coerceAtLeast(1f),
            altoDisp / altoModelo.coerceAtLeast(1f)
        )

        val combaY = flecha * escorzo * escala
        val largoPx = largo * escala
        val cuerdaPx = cuerda * escala

        val altoTotalPx = largoPx + combaY
        val x0 = margen + margenLateral + (anchoDisp - cuerdaPx).coerceAtLeast(0f) / 2f
        val yTop = margen + margenSuperior + (altoDisp - altoTotalPx).coerceAtLeast(0f) / 2f

        // Borde superior: arco simetrico achatado (los extremos arriba, el centro baja hacia
        // el observador). Borde inferior: el mismo arco desplazado hacia abajo por el largo.
        val bordeSuperior = arco.map { PointF(x0 + it.u * escala, yTop + it.profundidad * escorzo * escala) }
        val bordeInferior = bordeSuperior.map { PointF(it.x, it.y + largoPx) }

        // 1) Superficie opaca del vidrio.
        val superficie = Path().apply {
            moveTo(bordeSuperior.first().x, bordeSuperior.first().y)
            for (p in bordeSuperior) lineTo(p.x, p.y)
            for (i in bordeInferior.indices.reversed()) lineTo(bordeInferior[i].x, bordeInferior[i].y)
            close()
        }
        canvas.drawPath(superficie, pincelSuperficie)

        // 2) Generatrices verticales (se agrupan hacia los extremos -> sensacion de curva).
        for (k in 1 until generatrices) {
            val idx = (k.toFloat() / generatrices * arco.lastIndex).toInt().coerceIn(0, arco.lastIndex)
            canvas.drawLine(bordeSuperior[idx].x, bordeSuperior[idx].y, bordeInferior[idx].x, bordeInferior[idx].y, pincelGeneratriz)
        }

        // 3) Cantos verticales de los extremos.
        canvas.drawLine(bordeSuperior.first().x, bordeSuperior.first().y, bordeInferior.first().x, bordeInferior.first().y, pincelCanto)
        canvas.drawLine(bordeSuperior.last().x, bordeSuperior.last().y, bordeInferior.last().x, bordeInferior.last().y, pincelCanto)

        // 4) Bordes superior e inferior.
        canvas.drawPath(rutaDesdePuntos(bordeSuperior), pincelBorde)
        canvas.drawPath(rutaDesdePuntos(bordeInferior), pincelBorde)

        val idxPico = arco.indices.maxByOrNull { arco[it].profundidad } ?: (arco.size / 2)
        dibujarCotasPanel(canvas, bordeSuperior, bordeInferior, yTop, cuerdaPx, combaY, largoPx, idxPico)
    }

    private fun dibujarCotasPanel(
        canvas: Canvas,
        bordeSuperior: List<PointF>,
        bordeInferior: List<PointF>,
        yTop: Float,
        cuerdaPx: Float,
        combaY: Float,
        largoPx: Float,
        idxPico: Int
    ) {
        val izqInf = bordeInferior.first()
        val derInf = bordeInferior.last()

        // Cuerda: span horizontal entre los extremos (a profundidad 0).
        canvas.drawLine(izqInf.x, izqInf.y + 26f, derInf.x, derInf.y + 26f, pincelCota)
        canvas.drawText("Cuerda: ${fmt(cuerda)}", (izqInf.x + derInf.x) / 2f, izqInf.y + 56f, pincelTexto)

        // Largo: canto vertical izquierdo.
        canvas.drawLine(izqInf.x - 20f, yTop, izqInf.x - 20f, yTop + largoPx, pincelCota)
        pincelTexto.textAlign = Paint.Align.RIGHT
        canvas.drawText("Largo: ${fmt(largo)}", izqInf.x - 26f, yTop + largoPx / 2f, pincelTexto)
        pincelTexto.textAlign = Paint.Align.CENTER

        // Flecha: comba del borde superior (de la cuerda del borde al pico, escorzada).
        val centroX = (bordeSuperior.first().x + bordeSuperior.last().x) / 2f
        val pico = bordeSuperior[idxPico]
        canvas.drawLine(centroX, yTop, pico.x, pico.y, pincelCota)
        canvas.drawText("F: ${fmt(flecha)}", pico.x + 42f, pico.y - 4f, pincelTexto)

        // Desarrollo: sobre el borde superior, desplazado hacia arriba.
        val rutaDesarrollo = Path(rutaDesdePuntos(bordeSuperior)).apply { offset(0f, -30f) }
        canvas.drawPath(rutaDesarrollo, pincelCota)
        canvas.drawText("Desarrollo: ${fmt(desarrollo)}", centroX, yTop - 38f, pincelTexto)
    }

    // ----- Perfil 2D (solo el arco, sin largo) -----

    private fun dibujarPerfil(canvas: Canvas) {
        val margen = 24f
        val margenSuperior = 92f
        val margenInferior = 84f

        val arco = construirArcoModelo()
        if (arco.isEmpty()) return

        val anchoDisp = (width - margen * 2f).coerceAtLeast(1f)
        val altoDisp = (height - margen * 2f - margenSuperior - margenInferior).coerceAtLeast(1f)
        val escala = min(anchoDisp / cuerda.coerceAtLeast(1f), altoDisp / flecha.coerceAtLeast(1f))

        val cuerdaPx = cuerda * escala
        val flechaPx = flecha * escala
        val x0 = margen + (anchoDisp - cuerdaPx).coerceAtLeast(0f) / 2f
        val yBase = margen + margenSuperior + flechaPx + (altoDisp - flechaPx).coerceAtLeast(0f) / 2f

        val puntos = arco.map { PointF(x0 + it.u * escala, yBase - it.profundidad * escala) }
        val ruta = rutaDesdePuntos(puntos)
        canvas.drawPath(ruta, pincelBorde)
        canvas.drawLine(puntos.first().x, puntos.first().y, puntos.last().x, puntos.last().y, pincelCanto)

        canvas.drawLine(x0, yBase + 24f, x0 + cuerdaPx, yBase + 24f, pincelCota)
        canvas.drawText("Cuerda: ${fmt(cuerda)}", x0 + cuerdaPx / 2f, yBase + 54f, pincelTexto)
        canvas.drawLine(x0 + cuerdaPx / 2f, yBase, x0 + cuerdaPx / 2f, yBase - flechaPx, pincelCota)
        canvas.drawText("F: ${fmt(flecha)}", x0 + cuerdaPx / 2f - 44f, yBase - flechaPx / 2f, pincelTexto)
        val rutaDes = Path(ruta).apply { offset(0f, -30f) }
        canvas.drawPath(rutaDes, pincelCota)
        canvas.drawText("Desarrollo: ${fmt(desarrollo)}", x0 + cuerdaPx / 2f, yBase - flechaPx - 44f, pincelTexto)
    }

    // ----- Geometria -----

    private data class PuntoArco(val u: Float, val profundidad: Float)

    /** Puntos del arco: u recorre la cuerda (0..C) y profundidad es la comba (0 en extremos, F en el centro). */
    private fun construirArcoModelo(): List<PuntoArco> {
        val radio = radioArco(cuerda, flecha)
        val mediaCuerda = cuerda / 2f
        val centroY = radio - flecha
        return (0..muestrasArco).map { i ->
            val t = i.toFloat() / muestrasArco
            val x = -mediaCuerda + cuerda * t
            val profundidad = sqrt((radio * radio - x * x).coerceAtLeast(0f)) - centroY
            PuntoArco(u = x + mediaCuerda, profundidad = profundidad.coerceAtLeast(0f))
        }
    }

    private fun radioArco(cuerda: Float, flecha: Float): Float =
        (cuerda * cuerda / (8f * flecha)) + (flecha / 2f)

    private fun rutaDesdePuntos(puntos: List<PointF>): Path = Path().apply {
        if (puntos.isEmpty()) return@apply
        moveTo(puntos.first().x, puntos.first().y)
        for (i in 1 until puntos.size) lineTo(puntos[i].x, puntos[i].y)
    }

    private fun fmt(valor: Float): String =
        if (valor % 1f == 0f) valor.toInt().toString() else String.format("%.2f", valor)
}
