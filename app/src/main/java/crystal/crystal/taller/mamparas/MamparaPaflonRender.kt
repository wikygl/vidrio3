package crystal.crystal.taller.mamparas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import crystal.crystal.R
import kotlin.math.ceil

/**
 * Regenera el dibujo de una mampara paflón a partir de su [MamparaPaflonDescriptor]. La geometría
 * (tramos y módulos) sale del diseño simbólico [MamparaModulos], la misma fuente que usa el cálculo
 * de materiales, para que dibujo y materiales nunca se desincronicen.
 */
object MamparaPaflonRender {

    fun dibujar(context: Context, d: MamparaPaflonDescriptor): Bitmap? {
        val anchoCm = d.ancho
        val altoCm = d.alto
        if (anchoCm <= 0f || altoCm <= 0f) return null
        val altoHojaCm = d.altoHoja
        val m = MamparaModulos.desde(d)
        // Marco inferior: opcional. Sin él la hoja apoya en el piso, que es lo normal en paflón.
        // Con él la hoja apoya sobre el marco y conserva su alto, así que sube un marco entero:
        // lo que se acorta es la mocheta de arriba.
        val marcoInfCm = if (d.marcoInferior) d.marco else 0f
        val hayPuente = (altoCm - (altoHojaCm + MamparaModulos.P_ALT + d.marco + marcoInfCm)) > 0f

        val bmpW = (anchoCm * 3f).toInt().coerceAtLeast(600)
        val bmpH = (altoCm * 3f).toInt().coerceAtLeast(600)
        val escala = minOf(bmpW / anchoCm, bmpH / altoCm)
        val w = anchoCm * escala
        val h = altoCm * escala
        val x0 = (bmpW - w) / 2f
        val y0 = (bmpH - h) / 2f
        val marcoPx = d.marco * escala
        val bastidorPx = m.bastidor * escala
        val rielPx = MamparaModulos.P_ALT * escala
        val paranteTramoPx = m.paranteTramo * escala
        val altoHojaPx = altoHojaCm * escala
        val marcoInfPx = marcoInfCm * escala

        val bitmap = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        c.drawColor(Color.WHITE)

        val pMarco = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(120, 120, 120); style = Paint.Style.FILL }
        val pPaflon = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ContextCompat.getColor(context, R.color.aluminio); style = Paint.Style.FILL }
        // Vidrio en blanco (sin relleno de color), con líneas diagonales de brillo, como en Nova.
        val pVidrio = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
        val pLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; style = Paint.Style.STROKE; strokeWidth = 2.5f }
        val pLineaFina = Paint(pLinea).apply { strokeWidth = 1.5f }

        fun rect(r: RectF, paint: Paint) {
            c.drawRect(r, paint)
            c.drawRect(r, pLinea)
        }

        // Brillo del vidrio: 3 diagonales "/" (corta, media, larga) centradas, como en Nova.
        fun reflejoVidrio(left: Float, top: Float, right: Float, bottom: Float) {
            val s = minOf(right - left, bottom - top)
            if (s < 12f) return
            val cx = (left + right) / 2f
            val cy = (top + bottom) / 2f
            val lenMax = s * 0.35f
            val lengths = floatArrayOf(lenMax * 0.4f, lenMax * 0.7f, lenMax * 1.2f)
            val gap = s * 0.06f
            val dx = 0.7f; val dy = -1f
            val nx = 0.7f; val ny = 0.3f
            for (i in 0..2) {
                val offset = (i - 1) * gap
                val cxi = cx + nx * offset
                val cyi = cy + ny * offset
                val half = lengths[i] / 2f
                c.drawLine(cxi - dx * half, cyi - dy * half, cxi + dx * half, cyi + dy * half, pLineaFina)
            }
        }

        c.drawRect(RectF(x0, y0, x0 + w, y0 + h), pLinea)
        rect(RectF(x0, y0, x0 + marcoPx, y0 + h), pMarco)
        rect(RectF(x0 + w - marcoPx, y0, x0 + w, y0 + h), pMarco)
        rect(RectF(x0 + marcoPx, y0, x0 + w - marcoPx, y0 + marcoPx), pMarco)
        // Marco inferior, solo si el diseño lo pide: la paflón normal se apoya en el piso.
        if (d.marcoInferior) rect(RectF(x0 + marcoPx, y0 + h - marcoPx, x0 + w - marcoPx, y0 + h), pMarco)

        // La hoja apoya sobre el marco inferior cuando lo hay, y conserva su alto: altoHoja es
        // una cota interna, no una distancia al piso.
        val hojaBottom = y0 + h - marcoInfPx
        // Sin puente, altoHoja = alto - marcos, así que hojaTop cae justo bajo el marco (sin banda
        // intermedia). El tope mínimo es y0+marcoPx (no +rielPx, que dejaba un puente pegado al marco).
        val hojaTop = (hojaBottom - altoHojaPx).coerceAtLeast(y0 + marcoPx)
        val vanoLeft = x0 + marcoPx
        val vanoRight = x0 + w - marcoPx
        // Sin puente no hay riel superior visible: las hojas (parante de paflón) llegan al marco.
        val panelAreaTop = if (hayPuente) hojaTop + rielPx else hojaTop
        val panelAreaBottom = hojaBottom

        fun diviMocheta(xCm: Float): Int {
            if (xCm <= 0f) return 1
            return if (d.nMochetas == 0) ceil(xCm / 180.0).toInt().coerceAtLeast(1) else d.nMochetas
        }

        // Puente (mochetas) propio del tramo, dentro de [xIni, xFin].
        fun dibujarPuente(xIni: Float, xFin: Float) {
            if (!hayPuente || hojaTop <= y0 + marcoPx + rielPx) return
            val mTop = y0 + marcoPx
            val mBottom = hojaTop
            val nMo = diviMocheta((xFin - xIni) / escala).coerceAtLeast(1)
            val mW = ((xFin - xIni) - (nMo - 1) * rielPx) / nMo
            var x = xIni
            repeat(nMo) { idx ->
                val v = RectF(x, mTop, x + mW, mBottom)
                c.drawRect(v, pVidrio)
                reflejoVidrio(v.left, v.top, v.right, v.bottom)
                c.drawRect(v, pLineaFina)
                if (idx < nMo - 1) rect(RectF(x + mW, mTop, x + mW + rielPx, mBottom), pPaflon)
                x += mW + rielPx
            }
        }

        // Hojas (fijos/corredizas) de un tramo a partir de xIni.
        fun dibujarHojas(xIni: Float, tramo: List<ModuloMampara>) {
            var x = xIni
            // Corrediza en el extremo izquierdo: conserva su parante (móvil, se separa).
            if (tramo.firstOrNull()?.esFijo == false) {
                rect(RectF(x, panelAreaTop, x + bastidorPx, panelAreaBottom), pPaflon)
                x += bastidorPx
            }
            for (idx in tramo.indices) {
                val esF = tramo[idx].esFijo
                val glass = RectF(x, panelAreaTop, x + tramo[idx].ancho * escala, panelAreaBottom)
                c.drawRect(glass, pVidrio)
                reflejoVidrio(glass.left, glass.top, glass.right, glass.bottom)
                rect(RectF(glass.left, panelAreaBottom - bastidorPx, glass.right, panelAreaBottom), pPaflon)
                if (!esF) rect(RectF(glass.left, panelAreaTop, glass.right, panelAreaTop + bastidorPx), pPaflon)
                c.drawRect(glass, pLinea)
                x = glass.right
                if (idx < tramo.size - 1) {
                    val nB = if (esF != tramo[idx + 1].esFijo) 1 else 2
                    repeat(nB) { k ->
                        rect(RectF(x + k * bastidorPx, panelAreaTop, x + (k + 1) * bastidorPx, panelAreaBottom), pPaflon)
                    }
                    x += nB * bastidorPx
                } else if (!esF) {
                    rect(RectF(x, panelAreaTop, x + bastidorPx, panelAreaBottom), pPaflon)
                    x += bastidorPx
                }
            }
        }

        if (m.vidrioAncho > 0f) {
            var x = vanoLeft
            m.tramos.forEachIndexed { tIdx, tramo ->
                val xFin = x + m.anchoTramo(tramo) * escala
                dibujarPuente(x, xFin)
                // Riel superior solo si hay puente (separa puente de hojas). Sin puente, el parante
                // de la hoja toca el marco directamente.
                if (hayPuente) rect(RectF(x, hojaTop, xFin, hojaTop + rielPx), pPaflon)
                dibujarHojas(x, tramo)
                x = xFin
                // Parante estructural de paflón de piso a techo entre tramos.
                if (tIdx < m.tramos.size - 1) {
                    rect(RectF(x, y0 + marcoPx, x + paranteTramoPx, hojaBottom), pPaflon)
                    x += paranteTramoPx
                }
            }
        }
        return bitmap
    }
}
