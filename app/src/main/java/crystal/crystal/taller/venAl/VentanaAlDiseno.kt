package crystal.crystal.taller.venAl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import crystal.crystal.R
import crystal.crystal.taller.nova.NovaCalculos
import kotlin.math.ceil

/** Datos para dibujar una ventana de aluminio (familia serie 84 y serie 3825). */
data class VentanaAlDescriptor(
    val ancho: Float,
    val alto: Float,
    val altoHoja: Float,
    val divisiones: Int,
    val serie: String,
    val marco: Float,
    val nMochetas: Int,
    // Solo aplica a series con marco opcional (3825): si es false, se dibuja sin el marco externo
    // (2.2) y sin puente. En serie 84 se ignora (su marco es parte de la ventana).
    val conMarco: Boolean = true,
    // Solo serie 20: patrón elegido en el diálogo (fc, cc, cfc, fccf, cccc). Define el diseño.
    val patron: String = ""
) {
    /** Codifica el descriptor para archivarlo (y regenerar el diseño en la ficha). */
    fun serializar(): String =
        listOf(ancho, alto, altoHoja, divisiones, serie, marco, nMochetas, conMarco, patron)
            .joinToString("|")

    companion object {
        fun parsear(texto: String): VentanaAlDescriptor? {
            val p = texto.split("|")
            if (p.size < 9) return null
            return try {
                VentanaAlDescriptor(
                    ancho = p[0].toFloat(), alto = p[1].toFloat(), altoHoja = p[2].toFloat(),
                    divisiones = p[3].toInt(), serie = p[4], marco = p[5].toFloat(),
                    nMochetas = p[6].toInt(), conMarco = p[7].toBoolean(), patron = p[8]
                )
            } catch (e: Exception) {
                null
            }
        }
    }
}

/**
 * Dibujo de la ventana de aluminio (serie 84 y serie 84 económica): marco en todo el contorno,
 * puente (transom) arriba dividido en mochetas, y módulos fijos/corredizas abajo. El orden de
 * fijos (f) y corredizas (c) y los parantes de tramo (P) se toman de la misma lógica que Nova
 * (`NovaCalculos.ordenDivisConParantes`): 1=f, 2=fc, 3=fcf, 4=fccf, 5=fcfcf, 6=fcf·P·fcf, …
 *
 * Regla de parantes: el parante de una corrediza no se dibuja en el lado que colinda con un fijo
 * (queda detrás del parante del fijo, que lo tapa); sí se dibuja cuando colinda con otra corrediza
 * o con el marco. Los fijos dibujan siempre sus parantes. Vidrio en blanco con líneas diagonales
 * de brillo (estilo Nova).
 *
 * Medidas por serie (cm): parante y zócalo del módulo. El marco viene del descriptor (etMarco,
 * por defecto 2.2) y el puente es 2.5.
 */
object VentanaAlRender {

    private const val PUENTE = 2.5f

    // pierna = parante del módulo; marcoPropio = marco intrínseco de la ventana (0 = no tiene uno
    // aparte; el marco externo hace de marco). marcoOpcional = el marco externo (2.2) + puente se
    // pueden quitar vía diálogo (true en 3825; en 84 el marco siempre va, es parte de la ventana).
    private data class Dims(
        val pierna: Float,
        val zocalo: Float,
        val marcoPropio: Float,
        val marcoOpcional: Boolean
    )

    private fun dimsDe(serie: String): Dims? {
        val s = serie.lowercase()
        return when {
            s.contains("84") && s.contains("eco") -> Dims(3f, 4f, 0f, false)    // Clásica serie 84 económica
            s.contains("84") -> Dims(4f, 4.5f, 0f, false)                       // Clásica serie 84
            s.contains("3825") -> Dims(2.3f, 3.3f, 0.5f, true)                  // Serie 3825
            else -> null
        }
    }

    // Serie 20 / 25: mismo tipo de diseño (patrón elegido en el diálogo), distintas medidas.
    private data class Serie20Like(
        val marcoSup: Float,
        val marcoInf: Float,
        val marcoLat: Float,
        val pierna: Float,        // pierna en otro contexto (cruce/centro)
        val piernaMarco: Float,   // pierna que colinda con el marco lateral
        val traslape: Float,      // cruce
        val cabezal: Float,       // zócalo superior
        val zocalo: Float         // zócalo inferior
    )

    private fun medidasSerie20Like(serie: String): Serie20Like? {
        val s = serie.lowercase()
        return when {
            s.contains("serie 20") -> Serie20Like(2.1f, 1.0f, 1.8f, 4.4f, 3.4f, 2.7f, 4.8f, 4.8f)
            s.contains("serie 25") -> Serie20Like(2.7f, 1.2f, 2.3f, 5.1f, 4.1f, 3.8f, 4.4f, 6.5f)
            // Serie 62 europea: marco 3.2 uniforme; la misma pierna (6.2) hace de zócalo y cabezal.
            // No hay traslape distinto (todos los miembros 6.2) ni pierna reducida contra el marco.
            s.contains("serie 62") -> Serie20Like(3.2f, 3.2f, 3.2f, 6.2f, 6.2f, 6.2f, 6.2f, 6.2f)
            else -> null
        }
    }

    /** Indica si esta serie ya tiene diseño propio (si no, se usa el preview genérico). */
    fun soportaSerie(serie: String): Boolean = dimsDe(serie) != null || medidasSerie20Like(serie) != null

    /** True si la serie permite elegir marco externo + puente (3825); false si el marco es fijo (84). */
    fun serieUsaMarcoOpcional(serie: String): Boolean = dimsDe(serie)?.marcoOpcional == true

    /** True si la serie elige su diseño por patrón en el diálogo (serie 20 / 25). */
    fun serieUsaPatron(serie: String): Boolean = medidasSerie20Like(serie) != null

    /**
     * Reparte `divisiones` (= ceil(ancho/60), como Nova) en tramos de 2–4 módulos, lo más parejo y
     * simétrico posible (prefiere tramos de 3). Devuelve el patrón de cada tramo: 2→fc, 3→cfc,
     * 4→fccf. Entre tramos va un paflón (parante de 2.5, a todo el alto). Ej.: 5→[cfc,fc], 6→[cfc,cfc].
     */
    fun tramosDe(divisiones: Int, pat3: String): List<String> {
        val n = divisiones.coerceAtLeast(2)
        val t = ceil(n / 4.0).toInt().coerceAtLeast(1)
        val base = n / t
        val extra = n % t
        return (0 until t).map { i ->
            when (if (i < extra) base + 1 else base) { 2 -> "fc"; 3 -> pat3; else -> "fccf" }
        }
    }

    fun tramosSerie25(divisiones: Int): List<String> = tramosDe(divisiones, "cfc")

    /** Patrón de 3 módulos según la serie (serie 62 usa fcc; el resto cfc). */
    fun pat3De(serie: String): String = if (serie.lowercase().contains("serie 62")) "fcc" else "cfc"

    /**
     * Imagen del orden de colocación de módulos para un patrón. Busca, en orden: específica de la
     * serie (`orden_<serie>_<patron>`), genérica del patrón (`orden_<patron>`) y, mientras no existan
     * las imágenes reales, un provisional (`nova<patron>` o `novacc`). Devuelve 0 si no hay ninguna.
     * Se usa desde el diálogo de modulación y desde la ficha (misma imagen en ambos lados).
     */
    fun imagenOrdenModulos(context: Context, serie: String, patron: String): Int {
        val s = serie.lowercase().filter { it.isLetterOrDigit() }   // "Serie 3825" -> "serie3825"
        val p = patron.lowercase()
        val candidatos = listOf("orden_${s}_$p", "orden_$p", "nova$p", "novacc")
        for (nombre in candidatos) {
            val id = context.resources.getIdentifier(nombre, "drawable", context.packageName)
            if (id != 0) return id
        }
        return 0
    }

    /**
     * Patrones de módulos por tramo de un diseño archivado, para mostrar su orden de colocación.
     * Serie 84 devuelve lista vacía (no aplica). En 3825 el patrón viene por tramo (unido por comas);
     * en serie 20/25/62, "tramos"/vacío se reparte automáticamente y un patrón simple es un solo tramo.
     */
    fun patronesPorTramo(d: VentanaAlDescriptor): List<String> {
        val dims = dimsDe(d.serie)
        if (dims != null && !dims.marcoOpcional) return emptyList()   // serie 84: sin orden por imagen
        return if (d.patron.isBlank() || d.patron.equals("tramos", ignoreCase = true))
            tramosDe(d.divisiones.coerceAtLeast(2), pat3De(d.serie))
        else d.patron.split(",")
    }

    /** Patrones disponibles por serie (el primero, "tramos", reparte por divisiones automáticamente). */
    fun patronesDe(serie: String): Array<String> = when {
        serie.lowercase().contains("serie 62") -> arrayOf("tramos", "fc", "cc", "fcc", "ccc", "fccf", "cccc")
        else -> arrayOf("tramos", "fc", "cc", "cfc", "fccf", "cccc")   // serie 20 / 25
    }

    fun dibujar(context: Context, d: VentanaAlDescriptor): Bitmap? {
        medidasSerie20Like(d.serie)?.let { return dibujarSerie20Like(d, it) }
        val dims = dimsDe(d.serie) ?: return null
        val ancho = d.ancho
        val alto = d.alto
        if (ancho <= 0f || alto <= 0f) return null

        val bmpW = (ancho * 3f).toInt().coerceAtLeast(600)
        val bmpH = (alto * 3f).toInt().coerceAtLeast(600)
        val escala = minOf(bmpW / ancho, bmpH / alto)
        val w = ancho * escala
        val h = alto * escala
        val x0 = (bmpW - w) / 2f
        val y0 = (bmpH - h) / 2f
        // Marco externo (2.2): siempre en 84; en 3825 solo si conMarco. Sin él no hay puente.
        val tieneMarcoExterno = !dims.marcoOpcional || d.conMarco
        val marcoExtPx = if (tieneMarcoExterno) d.marco * escala else 0f
        val marcoPropioPx = dims.marcoPropio * escala
        val paPx = dims.pierna * escala
        val zoPx = dims.zocalo * escala
        val puentePx = PUENTE * escala

        val bitmap = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        c.drawColor(Color.WHITE)

        val pMarco = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(120, 120, 120); style = Paint.Style.FILL }
        // Gris visible para parantes/zócalos/puente: el `aluminio` (#CCCCCC) queda demasiado claro
        // y, sobre todo en parantes simples a escala de preview, parece que no se dibuja.
        val pPerfil = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(150, 150, 150); style = Paint.Style.FILL }
        val pVidrio = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
        val pLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; style = Paint.Style.STROKE; strokeWidth = 2.5f }
        val pLineaFina = Paint(pLinea).apply { strokeWidth = 1.5f }

        fun rect(r: RectF, paint: Paint) {
            c.drawRect(r, paint)
            c.drawRect(r, pLinea)
        }

        // Brillo del vidrio: 3 diagonales "/" centradas (estilo Nova).
        fun reflejo(left: Float, top: Float, right: Float, bottom: Float) {
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

        // Contorno exterior.
        c.drawRect(RectF(x0, y0, x0 + w, y0 + h), pLinea)

        // Marco externo (gris oscuro, 2.2) cuando corresponde y, dentro, el marco propio (0.5) si la
        // serie lo tiene (3825). El vano (área útil) queda por dentro de ambos.
        if (marcoExtPx > 0f) {
            rect(RectF(x0, y0, x0 + marcoExtPx, y0 + h), pMarco)
            rect(RectF(x0 + w - marcoExtPx, y0, x0 + w, y0 + h), pMarco)
            rect(RectF(x0 + marcoExtPx, y0, x0 + w - marcoExtPx, y0 + marcoExtPx), pMarco)
            rect(RectF(x0 + marcoExtPx, y0 + h - marcoExtPx, x0 + w - marcoExtPx, y0 + h), pMarco)
        }
        if (marcoPropioPx > 0f) {
            val a = x0 + marcoExtPx
            val b = y0 + marcoExtPx
            val r = x0 + w - marcoExtPx
            val bo = y0 + h - marcoExtPx
            rect(RectF(a, b, a + marcoPropioPx, bo), pPerfil)
            rect(RectF(r - marcoPropioPx, b, r, bo), pPerfil)
            rect(RectF(a + marcoPropioPx, b, r - marcoPropioPx, b + marcoPropioPx), pPerfil)
            rect(RectF(a + marcoPropioPx, bo - marcoPropioPx, r - marcoPropioPx, bo), pPerfil)
        }

        val marcoTotal = marcoExtPx + marcoPropioPx
        val vanoLeft = x0 + marcoTotal
        val vanoRight = x0 + w - marcoTotal
        val vanoTop = y0 + marcoTotal
        val vanoBottom = y0 + h - marcoTotal
        val altoHojaPx = d.altoHoja * escala
        // Sin marco externo (3825 sin marco) no hay puente: las hojas ocupan todo el alto.
        val hojaTop = if (tieneMarcoExterno) (vanoBottom - altoHojaPx).coerceAtLeast(vanoTop) else vanoTop

        // Módulos: 'f' = fijo, 'c' = corrediza, 'P' = paflón (parante de tramo, a todo el alto).
        val items = mutableListOf<Char>()
        if (dims.marcoOpcional) {
            // Serie 3825: siempre por tramos. `patron` trae los patrones por tramo unidos por comas
            // (o "tramos"/vacío = reparto automático por tamaño). Un paflón separa cada tramo.
            val patronesTramos = if (d.patron.isBlank() || d.patron.equals("tramos", ignoreCase = true))
                                     tramosDe(d.divisiones.coerceAtLeast(2), pat3De(d.serie))
                                 else d.patron.split(",")
            patronesTramos.forEachIndexed { idx, pat ->
                if (idx > 0) items.add('P')
                pat.forEach { ch -> if (ch == 'f' || ch == 'c') items.add(ch) }
            }
        } else {
            // Serie 84: orden f/c y parantes de tramo con la misma lógica que Nova.
            val orden = NovaCalculos.ordenDivisConParantes(d.divisiones.coerceAtLeast(1), ancho)
            orden.split(";P;").forEachIndexed { idx, tramo ->
                if (idx > 0) items.add('P')
                Regex("[fc](?=<)").findAll(tramo).forEach { items.add(it.value[0]) }
            }
        }
        if (items.none { it == 'f' || it == 'c' }) items.add('f')

        // ¿Se dibuja la PIERNA (parante del módulo) de `actual` en el lado que colinda con `vecino`?
        // 'M' = marco, 'P' = parante de tramo (divisor de aluminio de 2.5 a todo el alto).
        // Una corrediza oculta su pierna solo cuando colinda con un fijo (ahí la cubre la pierna del
        // fijo). Junto a otra corrediza, al marco o al parante de tramo (que funciona como el marco),
        // la pierna sí se dibuja. El fijo dibuja su pierna siempre.
        fun dibujaPierna(actual: Char, vecino: Char): Boolean = when (vecino) {
            'f' -> actual == 'f'   // corrediza junto a fijo: la oculta; en cualquier otro caso, se dibuja
            else -> true           // marco, parante de tramo u otra corrediza
        }

        // El parante de tramo (P) es de 2.5 cm (PUENTE) y recorre todo el alto del vano; los
        // módulos (fijos/corredizas) reparten el ancho restante por igual.
        val nParantesTramo = items.count { it == 'P' }
        val modCount = items.count { it == 'f' || it == 'c' }.coerceAtLeast(1)
        val modW = ((vanoRight - vanoLeft) - nParantesTramo * puentePx) / modCount

        // Posición [left,right] de cada item a lo ancho del vano.
        val bounds = ArrayList<Pair<Float, Float>>(items.size)
        run {
            var cx = vanoLeft
            for (it in items) {
                val wItem = if (it == 'P') puentePx else modW
                bounds.add(cx to cx + wItem)
                cx += wItem
            }
        }

        // Tramos = corridas contiguas de módulos entre parantes de tramo.
        val tramos = ArrayList<Pair<Float, Float>>()
        run {
            var ini = -1
            for (k in items.indices) {
                if (items[k] != 'P') { if (ini < 0) ini = k }
                if ((items[k] == 'P' || k == items.lastIndex) && ini >= 0) {
                    val fin = if (items[k] == 'P') k - 1 else k
                    tramos.add(bounds[ini].first to bounds[fin].second)
                    ini = -1
                }
            }
        }

        // Puente (transom) por tramo: mochetas de vidrio con separadores de 2.5 y la barra
        // horizontal del puente al pie. Cada tramo lleva su propio puente; el parante de tramo
        // los separa en todo el alto.
        if (tieneMarcoExterno && hojaTop - vanoTop > puentePx) {
            val mochetaBottom = hojaTop - puentePx
            for ((tl, tr) in tramos) {
                val anchoTramoCm = (tr - tl) / escala
                val nMo = if (tramos.size == 1 && d.nMochetas > 0) d.nMochetas
                          else NovaCalculos.anchMota(anchoTramoCm)   // ceil(ancho/180), mín 1
                val mW = ((tr - tl) - (nMo - 1) * puentePx) / nMo
                var mx = tl
                repeat(nMo) { idx ->
                    val v = RectF(mx, vanoTop, mx + mW, mochetaBottom)
                    c.drawRect(v, pVidrio)
                    reflejo(v.left, v.top, v.right, v.bottom)
                    c.drawRect(v, pLineaFina)
                    if (idx < nMo - 1) rect(RectF(mx + mW, vanoTop, mx + mW + puentePx, mochetaBottom), pPerfil)
                    mx += mW + puentePx
                }
                rect(RectF(tl, mochetaBottom, tr, hojaTop), pPerfil)
            }
        }

        // Módulos (fijos/corredizas) en la sección de hojas.
        for (k in items.indices) {
            if (items[k] == 'P') continue
            val (left, right) = bounds[k]
            val mod = RectF(left, hojaTop, right, vanoBottom)
            if (mod.width() <= 0f || mod.height() <= zoPx * 2f) continue

            val it = items[k]
            val vecinoIzq = if (k == 0) 'M' else items[k - 1]
            val vecinoDer = if (k == items.size - 1) 'M' else items[k + 1]
            val piernaIzq = dibujaPierna(it, vecinoIzq)
            val piernaDer = dibujaPierna(it, vecinoDer)

            c.drawRect(mod, pVidrio)
            reflejo(mod.left, mod.top, mod.right, mod.bottom)
            if (piernaIzq) rect(RectF(left, hojaTop, left + paPx, vanoBottom), pPerfil)
            if (piernaDer) rect(RectF(right - paPx, hojaTop, right, vanoBottom), pPerfil)
            // Zócalos arriba/abajo: llegan hasta la pierna dibujada, o hasta el borde del módulo
            // cuando esa pierna se omite (ahí la cubre la pierna del fijo vecino).
            val zL = if (piernaIzq) left + paPx else left
            val zR = if (piernaDer) right - paPx else right
            rect(RectF(zL, hojaTop, zR, hojaTop + zoPx), pPerfil)
            rect(RectF(zL, vanoBottom - zoPx, zR, vanoBottom), pPerfil)
            c.drawRect(mod, pLinea)
        }

        // Parantes de tramo: 2.5 cm, en TODO el alto del vano (cruzan puente y hojas). Se dibujan
        // al final para que queden continuos por encima del puente.
        for (k in items.indices) {
            if (items[k] != 'P') continue
            val (left, right) = bounds[k]
            rect(RectF(left, vanoTop, right, vanoBottom), pPerfil)
        }
        return bitmap
    }

    // Miembros verticales (pierna/traslape) de serie 20 / 25 según el patrón, de izquierda a
    // derecha. Cada entrada es (anchoCm, esCentroDoble): piernaMarco a los extremos (colinda con el
    // marco lateral), traslape en los cruces y, en fccf/cccc, DOS piernas al centro (pierna+pierna).
    private fun miembrosSerie20(patron: String, m: Serie20Like): List<Pair<Float, Boolean>> {
        return when (patron.lowercase()) {
            "cc"   -> listOf(m.piernaMarco to false, m.traslape to false, m.piernaMarco to false)
            // 3 módulos: dos cruces internos.
            "cfc", "fcf", "ccc", "fcc" ->
                listOf(m.piernaMarco to false, m.traslape to false, m.traslape to false, m.piernaMarco to false)
            "fccf",
            "cccc" -> listOf(m.piernaMarco to false, m.traslape to false, (m.pierna * 2f) to true, m.traslape to false, m.piernaMarco to false)
            else   -> listOf(m.piernaMarco to false, m.traslape to false, m.piernaMarco to false)   // fc (predeterminado)
        }
    }

    /**
     * Diseño propio de serie 20 / 25 (mismo tipo, distintas medidas en `m`): marco en los 4 lados,
     * sin puente. Los miembros verticales (piernas a los extremos, traslapes en los cruces, y en
     * fccf/cccc dos piernas al centro) salen del patrón elegido en el diálogo. Cada panel lleva
     * cabezal (zócalo superior) y zócalo inferior, que pueden ser distintos.
     */
    private fun dibujarSerie20Like(d: VentanaAlDescriptor, m: Serie20Like): Bitmap? {
        val ancho = d.ancho
        val alto = d.alto
        if (ancho <= 0f || alto <= 0f) return null

        val bmpW = (ancho * 3f).toInt().coerceAtLeast(600)
        val bmpH = (alto * 3f).toInt().coerceAtLeast(600)
        val escala = minOf(bmpW / ancho, bmpH / alto)
        val w = ancho * escala
        val h = alto * escala
        val x0 = (bmpW - w) / 2f
        val y0 = (bmpH - h) / 2f

        val bitmap = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888)
        val c = Canvas(bitmap)
        c.drawColor(Color.WHITE)

        val pMarco = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(120, 120, 120); style = Paint.Style.FILL }
        val pPerfil = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(150, 150, 150); style = Paint.Style.FILL }
        val pVidrio = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
        val pLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; style = Paint.Style.STROKE; strokeWidth = 2.5f }
        val pLineaFina = Paint(pLinea).apply { strokeWidth = 1.5f }

        // Opción del diálogo: "tramos" reparte por divisiones (ceil 60) en tramos de 2–4 módulos, cada
        // uno una ventana completa separada por paflón de 2.5 a todo el alto. Cualquier otro patrón =
        // ventana simple.
        if (!d.patron.equals("tramos", ignoreCase = true)) {
            dibujarVentana25Like(c, x0, y0, w, h, d.patron, m, escala, pMarco, pPerfil, pVidrio, pLinea, pLineaFina)
            return bitmap
        }
        val div = d.divisiones.coerceAtLeast(2)   // etPartes si el usuario lo ingresa, o auto (ceil 60)
        val tramos = tramosDe(div, pat3De(d.serie))
        val nPaflon = tramos.size - 1
        val paflonPx = 2.5f * escala
        val moduleWPx = (w - nPaflon * paflonPx) / div
        var x = x0
        for ((idx, pat) in tramos.withIndex()) {
            val hojas = when (pat) { "cfc", "fcc" -> 3; "fccf", "cccc" -> 4; else -> 2 }
            val tw = hojas * moduleWPx
            dibujarVentana25Like(c, x, y0, tw, h, pat, m, escala, pMarco, pPerfil, pVidrio, pLinea, pLineaFina)
            x += tw
            if (idx < nPaflon) {
                val pf = RectF(x, y0, x + paflonPx, y0 + h)
                c.drawRect(pf, pMarco); c.drawRect(pf, pLinea)
                x += paflonPx
            }
        }
        return bitmap
    }

    /** Dibuja una ventana serie 20/25 (marco en 4 lados + miembros + paneles) en el rect dado. */
    private fun dibujarVentana25Like(
        c: Canvas, left: Float, top: Float, wLocal: Float, hLocal: Float,
        patron: String, m: Serie20Like, escala: Float,
        pMarco: Paint, pPerfil: Paint, pVidrio: Paint, pLinea: Paint, pLineaFina: Paint
    ) {
        fun rect(r: RectF, paint: Paint) {
            c.drawRect(r, paint)
            c.drawRect(r, pLinea)
        }
        fun reflejo(l: Float, t: Float, r: Float, b: Float) {
            val s = minOf(r - l, b - t)
            if (s < 12f) return
            val cx = (l + r) / 2f
            val cy = (t + b) / 2f
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

        val marcoSupPx = m.marcoSup * escala
        val marcoInfPx = m.marcoInf * escala
        val marcoLatPx = m.marcoLat * escala
        val cabezalPx = m.cabezal * escala   // zócalo superior del panel
        val zocaloPx = m.zocalo * escala     // zócalo inferior del panel

        // Contorno + marco en los 4 lados.
        c.drawRect(RectF(left, top, left + wLocal, top + hLocal), pLinea)
        rect(RectF(left, top, left + marcoLatPx, top + hLocal), pMarco)
        rect(RectF(left + wLocal - marcoLatPx, top, left + wLocal, top + hLocal), pMarco)
        rect(RectF(left + marcoLatPx, top, left + wLocal - marcoLatPx, top + marcoSupPx), pMarco)
        rect(RectF(left + marcoLatPx, top + hLocal - marcoInfPx, left + wLocal - marcoLatPx, top + hLocal), pMarco)

        val contentLeft = left + marcoLatPx
        val contentRight = left + wLocal - marcoLatPx
        val contentTop = top + marcoSupPx
        val contentBottom = top + hLocal - marcoInfPx

        val miembros = miembrosSerie20(patron, m)
        val nPaneles = (miembros.size - 1).coerceAtLeast(1)
        val sumMiembrosPx = miembros.sumOf { it.first.toDouble() }.toFloat() * escala
        val panelW = (((contentRight - contentLeft) - sumMiembrosPx) / nPaneles).coerceAtLeast(0f)

        var x = contentLeft
        for (i in miembros.indices) {
            val (anchoCm, doble) = miembros[i]
            val mwPx = anchoCm * escala
            if (doble) {
                val half = mwPx / 2f
                rect(RectF(x, contentTop, x + half, contentBottom), pPerfil)
                rect(RectF(x + half, contentTop, x + mwPx, contentBottom), pPerfil)
            } else {
                rect(RectF(x, contentTop, x + mwPx, contentBottom), pPerfil)
            }
            x += mwPx
            if (i < miembros.size - 1) {
                val gl = x
                val gr = x + panelW
                c.drawRect(RectF(gl, contentTop, gr, contentBottom), pVidrio)
                reflejo(gl, contentTop, gr, contentBottom)
                // Cabezal (zócalo superior) y zócalo inferior del panel.
                rect(RectF(gl, contentTop, gr, contentTop + cabezalPx), pPerfil)
                rect(RectF(gl, contentBottom - zocaloPx, gr, contentBottom), pPerfil)
                c.drawRect(RectF(gl, contentTop, gr, contentBottom), pLineaFina)
                x = gr
            }
        }
    }
}
