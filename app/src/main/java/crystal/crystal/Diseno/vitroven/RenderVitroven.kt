package crystal.crystal.Diseno.vitroven

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import kotlin.math.max
import kotlin.math.min
import kotlin.math.tan

class RenderVitroven {

    companion object {
        private const val ANGULO_PARALelOGRAMO_GRADOS = 18f
    }

    fun renderizarBitmap(
        anchoBitmap: Int,
        altoBitmap: Int,
        parametros: ParametrosVitroven
    ): Bitmap {
        val w = anchoBitmap.coerceAtLeast(1)
        val h = altoBitmap.coerceAtLeast(1)
        val wf = w.toFloat()
        val hf = h.toFloat()
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.TRANSPARENT)

        val anchoTotalCm = parametros.anchoTotalCm.coerceAtLeast(1f)
        val altoTotalCm = parametros.altoTotalCm.coerceAtLeast(1f)
        val clips = parametros.clips.coerceAtLeast(0)
        val clasificacion = parametros.clasificacion.lowercase()
        val direccionVertical = parametros.direccionVertical
        val densidad = android.content.res.Resources.getSystem().displayMetrics.density

        val padding = min(wf, hf) * 0.08f
        val escala = min(
            (wf - (padding * 2f)) / anchoTotalCm,
            (hf - (padding * 2f)) / altoTotalCm
        ).coerceAtLeast(0.1f)

        val anchoTotalPx = anchoTotalCm * escala
        val altoTotalPx = altoTotalCm * escala

        val x0 = (wf - anchoTotalPx) / 2f
        val y0 = (hf - altoTotalPx) / 2f
        val x1 = x0 + anchoTotalPx
        val y1 = y0 + altoTotalPx

        val grosorParanteBase = min(anchoTotalPx * 0.03f, altoTotalPx * 0.22f)
        val grosorParante = grosorParanteBase.coerceAtLeast(1.2f)
        val gutter = max(2f, anchoTotalPx * 0.015f)

        val pParante = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#1B1B1B")
            style = Paint.Style.FILL
        }
        val pFijo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F7F7F7")
            style = Paint.Style.FILL
        }
        val pClip = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#F7F7F7")
            style = Paint.Style.FILL
        }
        val pBordeFijo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#2A2A2A")
            style = Paint.Style.STROKE
            strokeWidth = max(1.3f, anchoTotalPx * 0.006f)
        }
        val pBrilloFijo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#5A5A5A")
            style = Paint.Style.STROKE
            strokeWidth = max(0.9f, anchoTotalPx * 0.0032f)
        }
        val pBordeClip = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#2A2A2A")
            style = Paint.Style.STROKE
            strokeWidth = max(1.3f, anchoTotalPx * 0.006f)
        }
        val pBordeMarco = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#171717")
            style = Paint.Style.STROKE
            strokeWidth = max(1.3f, anchoTotalPx * 0.005f)
        }

        fun dibujarFijo(l: Float, t: Float, r: Float, b: Float) {
            if (r <= l || b <= t) return
            canvas.drawRect(l, t, r, b, pFijo)
            canvas.drawRect(l, t, r, b, pBordeFijo)

            // Brillo del vidrio: centrado, inclinado de abajo hacia arriba y con 3 longitudes.
            val wRect = r - l
            val hRect = b - t
            val cx = (l + r) / 2f
            val cy = (t + b) / 2f
            val angulo = (52f * Math.PI / 180.0).toFloat()
            val tanAng = tan(angulo)

            val largo1 = max(5f, min(wRect * 0.38f, hRect * 0.46f))
            val largo2 = largo1 * 0.56f
            val largo3 = largo1 * 0.25f
            val separacion = max(3.2f, pBrilloFijo.strokeWidth * 3.8f)
            val offsets = floatArrayOf(-separacion, 0f, separacion)
            val largos = floatArrayOf(largo1, largo2, largo3)

            for (i in 0..2) {
                val len = largos[i]
                val offY = offsets[i]
                val dx = len / 2f
                val dy = dx * tanAng
                canvas.drawLine(
                    cx - dx,
                    cy + dy + offY,
                    cx + dx,
                    cy - dy + offY,
                    pBrilloFijo
                )
            }
        }

        fun dibujarParas(
            areaL: Float,
            areaT: Float,
            areaR: Float,
            areaB: Float,
            clipsArea: Int,
            paranteIzqDebajo: Boolean,
            paranteDerEncima: Boolean
        ) {
            val anchoArea = areaR - areaL
            val altoArea = areaB - areaT
            if (anchoArea <= 0f || altoArea <= 0f) return

            // Escala local del parante para no invadir módulos pequeños.
            val grosorParanteLocal = min(grosorParante, min(anchoArea, altoArea) * 0.28f)
                .coerceAtLeast(0.9f)

            val paranteIzqL = areaL
            val paranteIzqR = areaL + grosorParanteLocal
            val paranteDerL = areaR - grosorParanteLocal
            val paranteDerR = areaR
            val paranteSupT = areaT
            val paranteSupB = areaT + grosorParanteLocal
            val paranteInfT = areaB - grosorParanteLocal
            val paranteInfB = areaB

            if (paranteIzqDebajo) {
                if (!direccionVertical) {
                    canvas.drawRect(paranteIzqL, areaT, paranteIzqR, areaB, pParante)
                } else {
                    canvas.drawRect(areaL, paranteSupT, areaR, paranteSupB, pParante)
                }
            }

            val clipL = if (!direccionVertical) paranteIzqR else areaL
            val clipR = if (!direccionVertical) paranteDerL else areaR
            val clipTopBase = if (!direccionVertical) areaT else paranteSupB
            val clipBottomBase = if (!direccionVertical) areaB else paranteInfT
            if (clipR <= clipL || clipBottomBase <= clipTopBase) return

            val margenVertical = if (direccionVertical) 0f
            else max(2f * densidad, (clipBottomBase - clipTopBase) * 0.035f)
            val areaTopUtil = clipTopBase + margenVertical
            val areaBottomUtil = clipBottomBase - margenVertical
            val altoUtil = areaBottomUtil - areaTopUtil
            if (altoUtil <= 0f) return

            val separacionBasePx = 9f * densidad
            val altoClipMinPx = 4f * densidad
            val altoClipBasePx = max(8f * densidad, altoUtil * 0.09f)

            val separacionClipsPx: Float
            val altoClip: Float
            if (clipsArea <= 0) {
                separacionClipsPx = separacionBasePx
                altoClip = altoClipBasePx
            } else {
                val altoIdealConSepBase = (altoUtil - ((clipsArea - 1) * separacionBasePx)) / clipsArea
                if (altoIdealConSepBase >= altoClipMinPx) {
                    separacionClipsPx = separacionBasePx
                    altoClip = min(altoClipBasePx, altoIdealConSepBase)
                } else {
                    altoClip = altoClipMinPx
                    separacionClipsPx = if (clipsArea > 1) {
                        ((altoUtil - (clipsArea * altoClip)) / (clipsArea - 1)).coerceAtLeast(0f)
                    } else {
                        0f
                    }
                }
            }

            if (!direccionVertical) {
                for (i in 0 until clipsArea) {
                    val clipB = areaBottomUtil - (i * (altoClip + separacionClipsPx))
                    val clipTCalculado = clipB - altoClip
                    if (clipB <= areaTopUtil) break
                    val clipT = max(clipTCalculado, areaTopUtil)

                    val anguloRad = (ANGULO_PARALelOGRAMO_GRADOS * Math.PI / 180.0).toFloat()
                    val inclinacionBase = altoClip * tan(anguloRad)
                    val inclinacion = min(inclinacionBase, (clipR - clipL) * 0.45f)
                    val clipRBase = clipR - inclinacion
                    if (clipRBase <= clipL) continue

                    val paralelogramo = Path().apply {
                        moveTo(clipL, clipB)
                        lineTo(clipRBase, clipB)
                        lineTo(clipRBase + inclinacion, clipT)
                        lineTo(clipL + inclinacion, clipT)
                        close()
                    }

                    canvas.drawPath(paralelogramo, pClip)
                    canvas.drawPath(paralelogramo, pBordeClip)
                }
            } else {
                val margenHorizontal = max(2f * densidad, (clipR - clipL) * 0.035f)
                val areaLeftUtil = clipL + margenHorizontal
                val areaRightUtil = clipR - margenHorizontal
                val anchoUtil = areaRightUtil - areaLeftUtil
                if (anchoUtil <= 0f) return

                val anchoClipMinPx = 4f * densidad
                val anchoClipBasePx = max(8f * densidad, anchoUtil * 0.09f)
                val separacionBaseXPx = 9f * densidad

                val separacionXPx: Float
                val anchoClip: Float
                if (clipsArea <= 0) {
                    separacionXPx = separacionBaseXPx
                    anchoClip = anchoClipBasePx
                } else {
                    val anchoIdealConSepBase = (anchoUtil - ((clipsArea - 1) * separacionBaseXPx)) / clipsArea
                    if (anchoIdealConSepBase >= anchoClipMinPx) {
                        separacionXPx = separacionBaseXPx
                        anchoClip = min(anchoClipBasePx, anchoIdealConSepBase)
                    } else {
                        anchoClip = anchoClipMinPx
                        separacionXPx = if (clipsArea > 1) {
                            ((anchoUtil - (clipsArea * anchoClip)) / (clipsArea - 1)).coerceAtLeast(0f)
                        } else {
                            0f
                        }
                    }
                }

                for (i in 0 until clipsArea) {
                    val clipLx = areaLeftUtil + (i * (anchoClip + separacionXPx))
                    val clipRxCalculado = clipLx + anchoClip
                    if (clipLx >= areaRightUtil) break
                    val clipRx = min(clipRxCalculado, areaRightUtil)

                    val anguloRad = (ANGULO_PARALelOGRAMO_GRADOS * Math.PI / 180.0).toFloat()
                    val inclinacionBase = anchoClip * tan(anguloRad)
                    val inclinacion = min(inclinacionBase, (areaBottomUtil - areaTopUtil) * 0.45f)
                    val clipTopDiag = areaTopUtil + inclinacion
                    if (clipTopDiag >= areaBottomUtil) continue

                    val paralelogramo = Path().apply {
                        moveTo(clipLx, areaBottomUtil)
                        lineTo(clipRx, areaBottomUtil)
                        lineTo(clipRx, clipTopDiag)
                        lineTo(clipLx, areaTopUtil)
                        close()
                    }

                    canvas.drawPath(paralelogramo, pClip)
                    canvas.drawPath(paralelogramo, pBordeClip)
                }
            }

            if (paranteDerEncima) {
                if (!direccionVertical) {
                    canvas.drawRect(paranteDerL, areaT, paranteDerR, areaB, pParante)
                } else {
                    canvas.drawRect(areaL, paranteInfT, areaR, paranteInfB, pParante)
                }
            }
        }

        val simbolicoParse = VitroSimbolicoParser.parse(parametros.disenoSimbolico)
        val simbolico = simbolicoParse.modulos.map { it.tipo.lowercaseChar() }
        val simbolicoVertical = simbolicoParse.segmentadoVertical

        if (simbolico.isNotEmpty()) {
            val n = simbolico.size
            if (simbolicoVertical) {
                val medidas = simbolicoParse.modulos.map { token ->
                    token.medida ?: (altoTotalCm / n.toFloat())
                }
                val totalMedidas = medidas.sum().coerceAtLeast(0.1f)
                var cursor = y0
                for (i in 0 until n) {
                    val altoSeg = altoTotalPx * (medidas[i] / totalMedidas)
                    val t = cursor
                    val b = if (i == n - 1) y1 else (cursor + altoSeg)
                    cursor = b
                    when (simbolico[i]) {
                        'f' -> {
                            val inT = if (i > 0) t + gutter else t
                            val inB = if (i < n - 1) b - gutter else b
                            dibujarFijo(x0, inT, x1, inB)
                        }
                        'v' -> dibujarParas(x0, t, x1, b, clips, true, true)
                    }
                }
            } else {
                val medidas = simbolicoParse.modulos.map { token ->
                    token.medida ?: (anchoTotalCm / n.toFloat())
                }
                val totalMedidas = medidas.sum().coerceAtLeast(0.1f)
                var cursor = x0
                for (i in 0 until n) {
                    val anchoSeg = anchoTotalPx * (medidas[i] / totalMedidas)
                    val l = cursor
                    val r = if (i == n - 1) x1 else (cursor + anchoSeg)
                    cursor = r
                    when (simbolico[i]) {
                        'f' -> {
                            val inL = if (i > 0) l + gutter else l
                            val inR = if (i < n - 1) r - gutter else r
                            dibujarFijo(inL, y0, inR, y1)
                        }
                        'v' -> dibujarParas(l, y0, r, y1, clips, true, true)
                    }
                }
            }
        } else when (clasificacion) {
            "vv" -> {
                val mitad = x0 + (anchoTotalPx / 2f)
                dibujarParas(x0, y0, mitad + (grosorParante / 2f), y1, clips, true, true)
                dibujarParas(mitad - (grosorParante / 2f), y0, x1, y1, clips, true, true)
            }
            "vf" -> {
                val mitad = x0 + (anchoTotalPx / 2f)
                dibujarParas(x0, y0, mitad + (grosorParante / 2f), y1, clips, true, true)
                dibujarFijo(mitad + gutter, y0, x1, y1)
            }
            "fvf" -> {
                val tercio = anchoTotalPx / 3f
                val l1 = x0
                val r1 = x0 + tercio
                val l2 = r1
                val r2 = l2 + tercio
                val l3 = r2
                val r3 = x1
                dibujarFijo(l1, y0, r1 - gutter, y1)
                dibujarParas(l2, y0, r2, y1, clips, true, true)
                dibujarFijo(l3 + gutter, y0, r3, y1)
            }
            "vb" -> {
                val divisor = y0 + (altoTotalPx * 0.55f)
                dibujarParas(x0, y0, x1, divisor, clips, true, true)
                dibujarFijo(x0 + gutter, divisor + gutter, x1 - gutter, y1)
            }
            "bvm" -> {
                val altoBanda = altoTotalPx * 0.2f
                val topB = y0 + altoBanda
                val botT = y1 - altoBanda
                dibujarFijo(x0 + gutter, y0, x1 - gutter, topB - gutter)
                dibujarParas(x0, topB, x1, botT, clips, true, true)
                dibujarFijo(x0 + gutter, botT + gutter, x1 - gutter, y1)
            }
            else -> {
                // "v" y fallback
                dibujarParas(x0, y0, x1, y1, clips, true, true)
            }
        }

        canvas.drawRect(x0, y0, x1, y1, pBordeMarco)
        return bmp
    }
}
