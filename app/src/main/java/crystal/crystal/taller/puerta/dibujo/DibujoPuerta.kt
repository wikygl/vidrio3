package crystal.crystal.taller.puerta.dibujo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import crystal.crystal.R

object DibujoPuerta {

    fun generarBitmapPuerta(
        context: Context,
        anchoPuertaCm: Float,
        altoPuertaCm: Float,
        anchoHojaCm: Float,
        altoHojaCm: Float,
        numeroZocalos: Int,
        numeroDivisiones: Int,
        anchoContenedor: Float,
        altoContenedor: Float,
        tipoDivision: String = "H",
        anguloGrados: Float = 0f,
        marcoCm: Float = 2.2f,
        bastidorCm: Float = 8.25f,
        marcoCmIzq: Float = marcoCm,
        marcoCmDer: Float = marcoCm,
        pisoCm: Float = 0f
    ): Bitmap {
        val factorEscala = minOf(anchoContenedor / anchoPuertaCm, altoContenedor / altoPuertaCm)
        val anchoPuertaPx = anchoPuertaCm * factorEscala
        val altoPuertaPx = altoPuertaCm * factorEscala
        val anchoHojaPx = anchoHojaCm * factorEscala
        val altoHojaPx = altoHojaCm * factorEscala
        val marcoPx = marcoCm * factorEscala
        val marcoIzqPx = marcoCmIzq * factorEscala
        val marcoDerPx = marcoCmDer * factorEscala

        val bmp = Bitmap.createBitmap(anchoContenedor.toInt(), altoContenedor.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.RED)

        val offsetX = (anchoContenedor - anchoPuertaPx) / 2f
        val offsetY = (altoContenedor - altoPuertaPx) / 2f
        canvas.save(); canvas.translate(offsetX, offsetY)

        val pinturaMarco = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.GRAY; style = Paint.Style.FILL }
        val pinturaPaflon = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ContextCompat.getColor(context, R.color.aluminio); style = Paint.Style.FILL }
        val pinturaInterior = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
        val pinturaLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; strokeWidth = 3f; style = Paint.Style.STROKE }

        // Marco externo (laterales + superior)
        dibujarMarcoExterno(canvas, anchoPuertaPx, altoPuertaPx, marcoIzqPx, marcoDerPx, marcoPx, pinturaMarco, pinturaLinea)

        // Posición de hoja: sube pisoCm sobre el fondo del marco
        val offsetHojaBottomPx = pisoCm * factorEscala
        val bottomHoja = altoPuertaPx - offsetHojaBottomPx
        // Zona piso visible (entre la hoja y el suelo)
        if (pisoCm > 1f) {
            canvas.drawRect(RectF(marcoIzqPx, bottomHoja, anchoPuertaPx - marcoDerPx, altoPuertaPx), pinturaInterior)
        }
        val topHoja = bottomHoja - altoHojaPx
        val leftHoja = marcoIzqPx + 0.5f * factorEscala
        val rectHoja = RectF(leftHoja, topHoja, leftHoja + anchoHojaPx, bottomHoja)

        val paflonPx = bastidorCm * factorEscala
        dibujarHojaCompleta(canvas, rectHoja, numeroZocalos, numeroDivisiones, paflonPx, pinturaMarco, pinturaPaflon, pinturaInterior, pinturaLinea, tipoDivision, anguloGrados)

        // Mocheta por encima de la hoja (0.5 cm gap + 2.5 cm altura)
        dibujarMocheta(canvas, anchoPuertaPx, marcoIzqPx, marcoDerPx, marcoPx, topHoja, factorEscala, pinturaMarco, pinturaInterior, pinturaLinea)

        canvas.restore()
        return bmp
    }

    private fun dibujarMarcoExterno(canvas: Canvas, anchoPuertaPx: Float, altoPuertaPx: Float, marcoIzqPx: Float, marcoDerPx: Float, marcoSupPx: Float, pMarco: Paint, pLinea: Paint) {
        val izq = RectF(0f, 0f, marcoIzqPx, altoPuertaPx)
        val der = RectF(anchoPuertaPx - marcoDerPx, 0f, anchoPuertaPx, altoPuertaPx)
        val sup = RectF(marcoIzqPx, 0f, anchoPuertaPx - marcoDerPx, marcoSupPx)
        canvas.drawRect(izq, pMarco); canvas.drawRect(izq, pLinea)
        canvas.drawRect(der, pMarco); canvas.drawRect(der, pLinea)
        canvas.drawRect(sup, pMarco); canvas.drawRect(sup, pLinea)
    }

    private fun dibujarMocheta(canvas: Canvas, anchoPuertaPx: Float, marcoIzqPx: Float, marcoDerPx: Float, marcoSupPx: Float, topHoja: Float, factorEscala: Float, pMarco: Paint, pInterior: Paint, pLinea: Paint) {
        val gapBelowFramePx = 0.5f * factorEscala
        val horizontalFrameHeightPx = 2.5f * factorEscala
        val yFrameBottom = topHoja - gapBelowFramePx
        val yFrameTop = yFrameBottom - horizontalFrameHeightPx
        val rectBlanco = RectF(marcoIzqPx, marcoSupPx, anchoPuertaPx - marcoDerPx, yFrameTop)
        canvas.drawRect(rectBlanco, pInterior); canvas.drawRect(rectBlanco, pLinea)
        val rectMarco = RectF(marcoIzqPx, yFrameTop, anchoPuertaPx - marcoDerPx, yFrameBottom)
        canvas.drawRect(rectMarco, pMarco); canvas.drawRect(rectMarco, pLinea)
    }

    private fun dibujarHojaCompleta(
        canvas: Canvas,
        hojaRect: RectF,
        numeroZocalos: Int,
        numeroDivisiones: Int,
        paflonPx: Float,
        pMarco: Paint,
        pPaflon: Paint,
        pInterior: Paint,
        pLinea: Paint,
        tipoDivision: String,
        anguloGrados: Float
    ) {
        canvas.save(); canvas.translate(hojaRect.left, hojaRect.top)
        val ancho = hojaRect.width(); val alto = hojaRect.height()
        dibujarBastidorHoja(canvas, ancho, alto, paflonPx, numeroZocalos, pMarco, pPaflon, pLinea)
        dibujarAreaInternaHoja(canvas, ancho, alto, paflonPx, numeroZocalos, numeroDivisiones, pInterior, pPaflon, pLinea, tipoDivision, anguloGrados)
        canvas.restore()
    }

    private fun dibujarBastidorHoja(canvas: Canvas, ancho: Float, alto: Float, paflonPx: Float, nZocalos: Int, pMarco: Paint, pPaflon: Paint, pLinea: Paint) {
        val rect = RectF(0f, 0f, ancho, alto)
        canvas.drawRect(rect, pMarco); canvas.drawRect(rect, pLinea)
        val izq = RectF(0f, 0f, paflonPx, alto)
        val der = RectF(ancho - paflonPx, 0f, ancho, alto)
        val sup = RectF(paflonPx, 0f, ancho - paflonPx, paflonPx)
        canvas.drawRect(izq, pPaflon); canvas.drawRect(izq, pLinea)
        canvas.drawRect(der, pPaflon); canvas.drawRect(der, pLinea)
        canvas.drawRect(sup, pPaflon); canvas.drawRect(sup, pLinea)
        repeat(nZocalos) { i ->
            val y = alto - (i + 1) * paflonPx
            val z = RectF(paflonPx, y, ancho - paflonPx, y + paflonPx)
            canvas.drawRect(z, pPaflon); canvas.drawRect(z, pLinea)
        }
    }

    private fun dibujarAreaInternaHoja(
        canvas: Canvas,
        ancho: Float,
        alto: Float,
        paflonPx: Float,
        nZocalos: Int,
        nDiv: Int,
        pInterior: Paint,
        pPaflon: Paint,
        pLinea: Paint,
        tipoDivision: String,
        anguloGrados: Float
    ) {
        val top = paflonPx
        val bottom = alto - nZocalos * paflonPx
        val right = ancho - paflonPx
        val rect = RectF(paflonPx, top, right, bottom)
        canvas.drawRect(rect, pInterior); canvas.drawRect(rect, pLinea)

        when (tipoDivision) {
            "V" -> if (nDiv > 1) {
                val barras = nDiv - 1
                val w = rect.width(); if (w > 0) {
                    val gapX = (w - barras * paflonPx) / (barras + 1)
                    var x = paflonPx + gapX
                    repeat(barras) {
                        val r = RectF(x, top, x + paflonPx, bottom)
                        canvas.drawRect(r, pPaflon); canvas.drawRect(r, pLinea)
                        x += paflonPx + gapX
                    }
                }
            }
            "D" -> if (nDiv > 1) {
                val barras = nDiv - 1
                val w = right - paflonPx
                val h = bottom - top
                canvas.save(); canvas.clipRect(rect)
                val cx = rect.centerX(); val cy = rect.centerY()
                canvas.translate(cx, cy); canvas.rotate(anguloGrados); canvas.translate(-cx, -cy)
                val thickness = paflonPx
                val gap = (h - barras * thickness) / (barras + 1)
                val diag = kotlin.math.sqrt(w * w + h * h)
                var y = top + gap
                repeat(barras) {
                    val r = RectF(paflonPx - diag, y, right + diag, y + thickness)
                    canvas.drawRect(r, pPaflon); canvas.drawRect(r, pLinea)
                    y += thickness + gap
                }
                canvas.restore()
            }
            else -> if (nDiv > 1) { // Horizontal
                val barras = nDiv - 1
                val h = bottom - top; if (h > 0) {
                    val gapY = (h - barras * paflonPx) / (barras + 1)
                    var y = top + gapY
                    repeat(barras) {
                        val r = RectF(paflonPx, y, right, y + paflonPx)
                        canvas.drawRect(r, pPaflon); canvas.drawRect(r, pLinea)
                        y += paflonPx + gapY
                    }
                }
            }
        }
    }

    fun generarBitmapTaly(
        context: Context,
        anchoPuertaCm: Float,
        altoPuertaCm: Float,
        anchoHojaCm: Float,
        altoHojaCm: Float,
        anchoContenedor: Float,
        altoContenedor: Float,
        numeroDivisiones: Int = 1,
        anguloGrados: Float = 0f,
        marcoCm: Float = 2.2f,
        bastidorCm: Float = 8.25f,
        maxVacioCm: Float = 20f,
        marcoCmIzq: Float = marcoCm,
        marcoCmDer: Float = marcoCm,
        nZocalo: Int = 1,
        pisoCm: Float = 0f
    ): Bitmap {
        val factorEscala = minOf(anchoContenedor / anchoPuertaCm, altoContenedor / altoPuertaCm)
        val anchoPuertaPx = anchoPuertaCm * factorEscala
        val altoPuertaPx = altoPuertaCm * factorEscala
        val anchoHojaPx = anchoHojaCm * factorEscala
        val altoHojaPx = altoHojaCm * factorEscala
        val marcoPx = marcoCm * factorEscala
        val marcoIzqPx = marcoCmIzq * factorEscala
        val marcoDerPx = marcoCmDer * factorEscala
        val paflonPx = bastidorCm * factorEscala
        val maxVacioPx = maxVacioCm * factorEscala

        val bmp = Bitmap.createBitmap(anchoContenedor.toInt(), altoContenedor.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.RED)

        val offsetX = (anchoContenedor - anchoPuertaPx) / 2f
        val offsetY = (altoContenedor - altoPuertaPx) / 2f
        canvas.save(); canvas.translate(offsetX, offsetY)

        val pinturaMarco    = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.GRAY; style = Paint.Style.FILL }
        val pinturaPaflon   = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ContextCompat.getColor(context, R.color.aluminio); style = Paint.Style.FILL }
        val pinturaInterior = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
        val pinturaLinea    = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; strokeWidth = 3f; style = Paint.Style.STROKE }

        dibujarMarcoExterno(canvas, anchoPuertaPx, altoPuertaPx, marcoIzqPx, marcoDerPx, marcoPx, pinturaMarco, pinturaLinea)

        val offsetHojaBottomPx = pisoCm * factorEscala
        val bottomHoja = altoPuertaPx - offsetHojaBottomPx
        val topHoja = bottomHoja - altoHojaPx
        val leftHoja = marcoIzqPx + 0.5f * factorEscala
        if (pisoCm > 1f) {
            canvas.drawRect(RectF(marcoIzqPx, bottomHoja, anchoPuertaPx - marcoDerPx, altoPuertaPx), pinturaInterior)
        }

        dibujarMocheta(canvas, anchoPuertaPx, marcoIzqPx, marcoDerPx, marcoPx, topHoja, factorEscala, pinturaMarco, pinturaInterior, pinturaLinea)

        canvas.save()
        canvas.translate(leftHoja, topHoja)
        dibujarHojaTaly(canvas, anchoHojaPx, altoHojaPx, paflonPx, maxVacioPx, numeroDivisiones, anguloGrados, nZocalo, pinturaMarco, pinturaPaflon, pinturaInterior, pinturaLinea)
        canvas.restore()

        canvas.restore()
        return bmp
    }

    private fun dibujarHojaTaly(
        canvas: Canvas,
        ancho: Float,
        alto: Float,
        paflonPx: Float,
        maxVacioPx: Float,
        nDiv: Int,
        anguloGrados: Float,
        nZocalo: Int,
        pMarco: Paint,
        pPaflon: Paint,
        pInterior: Paint,
        pLinea: Paint
    ) {
        val topY = paflonPx
        val botY = alto - paflonPx
        // bottomInterior: la altura hasta donde llegan los paflones laterales y el diseño interior
        // El zócalo ocupa desde bottomInterior hasta botY
        val bottomInterior = alto - nZocalo * paflonPx

        // Fondo gris
        canvas.drawRect(RectF(0f, 0f, ancho, alto), pMarco)
        // Interior blanco (hasta bottomInterior, el zócalo queda en gris/aluminio)
        canvas.drawRect(RectF(paflonPx, topY, ancho - paflonPx, bottomInterior), pInterior)

        // Bastidor: 4 lados
        val bastidor = listOf(
            RectF(0f, 0f, paflonPx, alto),
            RectF(ancho - paflonPx, 0f, ancho, alto),
            RectF(paflonPx, 0f, ancho - paflonPx, paflonPx),
            RectF(paflonPx, botY, ancho - paflonPx, alto)
        )
        for (r in bastidor) { canvas.drawRect(r, pPaflon) }

        // Paflones verticales interiores por pares — llegan hasta bottomInterior (no al zócalo)
        var leftX = paflonPx
        var rightX = ancho - paflonPx
        while (rightX - leftX > maxVacioPx && rightX - leftX >= paflonPx * 2f) {
            canvas.drawRect(RectF(leftX, topY, leftX + paflonPx, bottomInterior), pPaflon)
            canvas.drawRect(RectF(rightX - paflonPx, topY, rightX, bottomInterior), pPaflon)
            leftX += paflonPx
            rightX -= paflonPx
        }

        // Zócalo extra: igual al bastidor inferior, ancho completo del interior
        val nZocaloExtra = nZocalo - 1
        if (nZocaloExtra > 0) {
            repeat(nZocaloExtra) { i ->
                val y = bottomInterior + i * paflonPx
                canvas.drawRect(RectF(paflonPx, y, ancho - paflonPx, y + paflonPx), pPaflon)
            }
        }

        // topInner y botInner: horizontales fijos (no se rotan), botInner justo encima del zócalo
        val topInner = RectF(leftX, topY, rightX, topY + paflonPx)
        val botInner = RectF(leftX, bottomInterior - paflonPx, rightX, bottomInterior)
        canvas.drawRect(topInner, pPaflon)
        canvas.drawRect(botInner, pPaflon)

        // Zona de divisores: entre topInner y botInner
        val zoneTop = topY + paflonPx
        val zoneBot = bottomInterior - paflonPx
        val zoneH = zoneBot - zoneTop
        val divisiones = maxOf(1, nDiv)
        val barras = divisiones - 1

        if (barras > 0 && zoneH > 0f) {
            val gap = (zoneH - barras * paflonPx) / (barras + 1)
            val zoneW = rightX - leftX
            val diag = kotlin.math.sqrt(zoneW * zoneW + zoneH * zoneH)
            val cx = (leftX + rightX) / 2f
            val cy = (zoneTop + zoneBot) / 2f
            val zoneRect = RectF(leftX, zoneTop, rightX, zoneBot)

            canvas.save()
            canvas.clipRect(zoneRect)
            canvas.translate(cx, cy); canvas.rotate(anguloGrados); canvas.translate(-cx, -cy)
            var y = zoneTop + gap
            repeat(barras) {
                val bar = RectF(leftX - diag, y, rightX + diag, y + paflonPx)
                canvas.drawRect(bar, pPaflon)
                canvas.drawRect(bar, pLinea)
                y += paflonPx + gap
            }
            canvas.restore()
        }

        // ── Contornos ──
        canvas.drawRect(RectF(0f, 0f, ancho, alto), pLinea)
        for (r in bastidor) { canvas.drawRect(r, pLinea) }

        var lx = paflonPx; var rx = ancho - paflonPx
        while (rx - lx > maxVacioPx && rx - lx >= paflonPx * 2f) {
            canvas.drawRect(RectF(lx, topY, lx + paflonPx, bottomInterior), pLinea)
            canvas.drawRect(RectF(rx - paflonPx, topY, rx, bottomInterior), pLinea)
            lx += paflonPx; rx -= paflonPx
        }
        if (nZocaloExtra > 0) {
            repeat(nZocaloExtra) { i ->
                val y = bottomInterior + i * paflonPx
                canvas.drawRect(RectF(paflonPx, y, ancho - paflonPx, y + paflonPx), pLinea)
            }
        }
        canvas.drawRect(topInner, pLinea)
        canvas.drawRect(botInner, pLinea)

        if (anguloGrados == 0f && barras > 0 && zoneH > 0f) {
            val gap = (zoneH - barras * paflonPx) / (barras + 1)
            var sy = zoneTop + gap + paflonPx
            repeat(barras - 1) {
                canvas.drawRect(RectF(leftX, sy, rightX, sy + gap), pLinea)
                sy += gap + paflonPx
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Lina H  ────────────────────────────────────────────────────────────────
    // ════════════════════════════════════════════════════════════════════════

    fun generarBitmapLinaH(
        context: Context,
        anchoCm: Float,
        altoCm: Float,
        altoHojaCm: Float,
        nPaneles: Int,
        anchoContenedor: Float,
        altoContenedor: Float,
        marcoCm: Float = 2.2f,
        pisoCm: Float = 0f
    ): Bitmap {
        val factor   = minOf(anchoContenedor / anchoCm, altoContenedor / altoCm)
        val anchoPx  = anchoCm  * factor
        val altoPx   = altoCm   * factor
        val marcoPx  = marcoCm  * factor

        val bmp    = Bitmap.createBitmap(anchoContenedor.toInt(), altoContenedor.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.RED)

        val offsetX = (anchoContenedor - anchoPx) / 2f
        val offsetY = (altoContenedor  - altoPx)  / 2f
        canvas.save()
        canvas.translate(offsetX, offsetY)

        val pMarco    = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.GRAY; style = Paint.Style.FILL }
        val pPanel    = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ContextCompat.getColor(context, R.color.aluminio); style = Paint.Style.FILL }
        val pInterior = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
        val pLinea    = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; strokeWidth = 3f; style = Paint.Style.STROKE }
        val pLineaFin = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.DKGRAY; strokeWidth = 1.5f; style = Paint.Style.STROKE }

        // ── Laterales (alto completo) ─────────────────────────────────────
        val rIzq = RectF(0f, 0f, marcoPx, altoPx)
        val rDer = RectF(anchoPx - marcoPx, 0f, anchoPx, altoPx)
        for (r in listOf(rIzq, rDer)) { canvas.drawRect(r, pMarco); canvas.drawRect(r, pLinea) }

        // ── Sobre luz ─────────────────────────────────────────────────────
        val puenteCm = 3.5f
        val puentePx = puenteCm * factor
        val tieneSobreLuz = altoCm > altoHojaCm + marcoCm + puenteCm + 2f

        val topCuerpo: Float
        if (tieneSobreLuz) {
            val altoSLPx = (altoCm - altoHojaCm - puenteCm - marcoCm) * factor
            val rSup = RectF(marcoPx, 0f, anchoPx - marcoPx, marcoPx)
            canvas.drawRect(rSup, pMarco); canvas.drawRect(rSup, pLinea)
            // Sobre luz — blanco como vidrio
            val rSL = RectF(marcoPx, marcoPx, anchoPx - marcoPx, marcoPx + altoSLPx)
            canvas.drawRect(rSL, pInterior)
            canvas.drawRect(rSL, pLinea)
            // Puente
            val yPuente = marcoPx + altoSLPx
            val rPuente = RectF(marcoPx, yPuente, anchoPx - marcoPx, yPuente + puentePx)
            canvas.drawRect(rPuente, pMarco); canvas.drawRect(rPuente, pLinea)
            topCuerpo = yPuente + puentePx
        } else {
            val rSup = RectF(marcoPx, 0f, anchoPx - marcoPx, marcoPx)
            canvas.drawRect(rSup, pMarco); canvas.drawRect(rSup, pLinea)
            topCuerpo = marcoPx
        }

        // ── Cuerpo de la puerta ──────────────────────────────────────────
        val offsetPisoPx = pisoCm * factor
        if (pisoCm > 1f) {
            canvas.drawRect(RectF(marcoPx, altoPx - offsetPisoPx, anchoPx - marcoPx, altoPx), pInterior)
        }

        val cuerpoAncho = anchoPx - 2f * marcoPx
        val cuerpoBot   = altoPx - offsetPisoPx
        val cuerpoAlto  = cuerpoBot - topCuerpo
        val cuerpoLeft  = marcoPx

        val grumaPx    = 0.8f * factor
        val anchoIzqPx = (cuerpoAncho - grumaPx) / 4f
        val anchoDerPx = cuerpoAncho - anchoIzqPx - grumaPx

        // Columna izquierda — plancha ciega
        val rColIzq = RectF(cuerpoLeft, topCuerpo, cuerpoLeft + anchoIzqPx, cuerpoBot)
        canvas.drawRect(rColIzq, pPanel); canvas.drawRect(rColIzq, pLinea)

        // Columna derecha — fondo aluminio completo
        val n = maxOf(3, nPaneles)
        val xDer = cuerpoLeft + anchoIzqPx + grumaPx
        val altPanel = (cuerpoAlto - (n - 1) * grumaPx) / n
        val rColDer = RectF(xDer, topCuerpo, xDer + anchoDerPx, cuerpoBot)
        canvas.drawRect(rColDer, pPanel)

        // Líneas horizontales de gruma entre paneles
        for (i in 1 until n) {
            val yGruma = topCuerpo + i * (altPanel + grumaPx) - grumaPx
            canvas.drawLine(xDer, yGruma, xDer + anchoDerPx, yGruma, pLineaFin)
            canvas.drawLine(xDer, yGruma + grumaPx, xDer + anchoDerPx, yGruma + grumaPx, pLineaFin)
        }

        // Rectángulo de vidrio: desde bajo el panel superior hasta sobre el panel inferior
        val yVidTop = topCuerpo + altPanel + grumaPx
        val yVidBot = topCuerpo + (n - 1) * (altPanel + grumaPx)
        val vidAncho = anchoDerPx / 2f
        val xVidLeft = xDer + (anchoDerPx - vidAncho) / 2f
        val rVidExt = RectF(xVidLeft, yVidTop, xVidLeft + vidAncho, yVidBot)
        canvas.drawRect(rVidExt, pPanel)   // marco aluminio 3.8cm
        canvas.drawRect(rVidExt, pLinea)
        // Vidrio interior (inset 3.8cm)
        val framePx = 3.8f * factor
        val rVidInt = RectF(rVidExt.left + framePx, rVidExt.top + framePx, rVidExt.right - framePx, rVidExt.bottom - framePx)
        if (rVidInt.width() > 0f && rVidInt.height() > 0f) {
            canvas.drawRect(rVidInt, pInterior)
            canvas.drawRect(rVidInt, pLineaFin)
        }

        canvas.drawRect(rColDer, pLinea)

        canvas.restore()
        return bmp
    }

    // ════════════════════════════════════════════════════════════════════════
    // Base compartida: canal + sobre luz + piso + bastidor + zócalos
    // Usada por Adel, Mili, Jeny y cualquier puerta con bastidor de paflón.
    // ════════════════════════════════════════════════════════════════════════

    private data class BaseHoja(
        val bmp: Bitmap, val canvas: Canvas,
        val factor: Float, val bPx: Float,
        val innerLeft: Float, val innerTop: Float,
        val innerRight: Float, val contentBot: Float,
        val hojaRect: RectF,
        val pPanel: Paint, val pInterior: Paint,
        val pLinea: Paint, val pLineaFin: Paint
    )

    private fun crearBaseHoja(
        context: Context,
        anchoCm: Float, altoCm: Float, altoHojaCm: Float,
        anchoContenedor: Float, altoContenedor: Float,
        marcoCm: Float = 2.2f, pisoCm: Float = 0f, nZocalo: Int = 1,
        bastidorCm: Float = 8.25f,
        zocaloAlternado: Boolean = false  // pares=2.5cm, impares=bPx (solo Dora por ahora)
    ): BaseHoja {
        val factor  = minOf(anchoContenedor / anchoCm, altoContenedor / altoCm)
        val anchoPx = anchoCm * factor;  val altoPx = altoCm * factor
        val marcoPx = marcoCm * factor;  val bPx    = bastidorCm * factor

        val bmp    = Bitmap.createBitmap(anchoContenedor.toInt(), altoContenedor.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.RED)
        canvas.save()
        canvas.translate((anchoContenedor - anchoPx) / 2f, (altoContenedor - altoPx) / 2f)

        val pMarco    = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.GRAY; style = Paint.Style.FILL }
        val pPanel    = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = ContextCompat.getColor(context, R.color.aluminio); style = Paint.Style.FILL }
        val pInterior = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; style = Paint.Style.FILL }
        val pLinea    = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.BLACK; strokeWidth = 3f; style = Paint.Style.STROKE }
        val pLineaFin = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.DKGRAY; strokeWidth = 1.5f; style = Paint.Style.STROKE }

        // Laterales + marco superior
        for (r in listOf(RectF(0f, 0f, marcoPx, altoPx), RectF(anchoPx - marcoPx, 0f, anchoPx, altoPx))) {
            canvas.drawRect(r, pMarco); canvas.drawRect(r, pLinea)
        }

        // Sobre luz
        val puentePx = 3.5f * factor
        val topCuerpo: Float
        if (altoCm > altoHojaCm + marcoCm + 3.5f + 2f) {
            val altoSLPx = (altoCm - altoHojaCm - 3.5f - marcoCm) * factor
            val rSup = RectF(marcoPx, 0f, anchoPx - marcoPx, marcoPx)
            canvas.drawRect(rSup, pMarco); canvas.drawRect(rSup, pLinea)
            val rSL = RectF(marcoPx, marcoPx, anchoPx - marcoPx, marcoPx + altoSLPx)
            canvas.drawRect(rSL, pInterior); canvas.drawRect(rSL, pLinea)
            val yPuente = marcoPx + altoSLPx
            val rPuente = RectF(marcoPx, yPuente, anchoPx - marcoPx, yPuente + puentePx)
            canvas.drawRect(rPuente, pMarco); canvas.drawRect(rPuente, pLinea)
            topCuerpo = yPuente + puentePx
        } else {
            val rSup = RectF(marcoPx, 0f, anchoPx - marcoPx, marcoPx)
            canvas.drawRect(rSup, pMarco); canvas.drawRect(rSup, pLinea)
            topCuerpo = marcoPx
        }

        // Piso
        val offsetPisoPx = pisoCm * factor
        val cuerpoBot    = altoPx - offsetPisoPx
        val cuerpoAncho  = anchoPx - 2f * marcoPx
        if (pisoCm > 1f) canvas.drawRect(RectF(marcoPx, cuerpoBot, anchoPx - marcoPx, altoPx), pInterior)

        // Hoja + bastidor (4 lados)
        val xHoja = marcoPx
        canvas.drawRect(RectF(xHoja, topCuerpo, xHoja + cuerpoAncho, cuerpoBot), pPanel)
        for (r in listOf(
            RectF(xHoja,                    topCuerpo,       xHoja + cuerpoAncho, topCuerpo + bPx),
            RectF(xHoja,                    cuerpoBot - bPx, xHoja + cuerpoAncho, cuerpoBot),
            RectF(xHoja,                    topCuerpo,       xHoja + bPx,         cuerpoBot),
            RectF(xHoja + cuerpoAncho - bPx, topCuerpo,      xHoja + cuerpoAncho, cuerpoBot)
        )) { canvas.drawRect(r, pPanel); canvas.drawRect(r, pLinea) }

        // Área interior
        val innerLeft  = xHoja + bPx;  val innerTop  = topCuerpo + bPx
        val innerRight = xHoja + cuerpoAncho - bPx;  val innerBot = cuerpoBot - bPx

        // Zócalos extra (bastidor inferior ya cuenta como 1)
        // i se dibuja arriba→abajo; el más cercano al bastidor es i=extra-1 (posición 2 = par → 2.5 cm)
        val extra   = maxOf(0, nZocalo - 1)
        val smallPx = 2.5f * factor
        fun zH(i: Int) = if (zocaloAlternado && (extra - i) % 2 == 1) smallPx else bPx
        val extraH  = (0 until extra).fold(0f) { acc, i -> acc + zH(i) }
        val contentBot = innerBot - extraH
        var yZoc = contentBot
        repeat(extra) { i ->
            val h  = zH(i)
            canvas.drawRect(RectF(innerLeft, yZoc, innerRight, yZoc + h), pPanel)
            canvas.drawRect(RectF(innerLeft, yZoc, innerRight, yZoc + h), pLinea)
            yZoc += h
        }

        return BaseHoja(bmp, canvas, factor, bPx, innerLeft, innerTop, innerRight, contentBot,
            RectF(xHoja, topCuerpo, xHoja + cuerpoAncho, cuerpoBot),
            pPanel, pInterior, pLinea, pLineaFin)
    }

    // ════════════════════════════════════════════════════════════════════════
    // Adel (p1 / p2)  ────────────────────────────────────────────────────────
    // Estructura: columna izquierda (3/4) con paflones + columna derecha (1/4)
    // con N divisiones para vidrio. Variante = orientación de los paflones.
    // ════════════════════════════════════════════════════════════════════════

    fun generarBitmapAdel(
        context: Context,
        anchoCm: Float,
        altoCm: Float,
        altoHojaCm: Float,
        nDivisiones: Int,
        variante: String,
        nPaflones: Int = 3,
        anchoContenedor: Float,
        altoContenedor: Float,
        marcoCm: Float = 2.2f,
        pisoCm: Float = 0f,
        nZocalo: Int = 0
    ): Bitmap {
        val base       = crearBaseHoja(context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm, nZocalo)
        val grumaPx    = 0.8f * base.factor
        val innerAncho = base.innerRight - base.innerLeft
        val contentAlto = base.contentBot - base.innerTop

        val rightAncho = (innerAncho - base.bPx) / 4f
        val leftAncho  = innerAncho - base.bPx - rightAncho
        val xSep       = base.innerLeft + leftAncho
        val xRightCol  = xSep + base.bPx

        val rSep = RectF(xSep, base.innerTop, xSep + base.bPx, base.contentBot)
        base.canvas.drawRect(rSep, base.pPanel); base.canvas.drawRect(rSep, base.pLinea)

        when (variante) {
            "Adel p1" -> {
                var y = base.innerTop
                while (y < base.contentBot - 0.5f) {
                    val yBot = minOf(y + base.bPx, base.contentBot)
                    base.canvas.drawRect(RectF(base.innerLeft, y, base.innerLeft + leftAncho, yBot), base.pPanel)
                    base.canvas.drawRect(RectF(base.innerLeft, y, base.innerLeft + leftAncho, yBot), base.pLinea)
                    y += base.bPx + grumaPx
                }
            }
            "Adel p2" -> {
                val rVid = RectF(base.innerLeft, base.innerTop, base.innerLeft + leftAncho, base.contentBot)
                base.canvas.drawRect(rVid, base.pInterior); base.canvas.drawRect(rVid, base.pLineaFin)
            }
            "Adel p3" -> {
                var x = base.innerLeft
                while (x < base.innerLeft + leftAncho - 0.5f) {
                    val xRight = minOf(x + base.bPx, base.innerLeft + leftAncho)
                    base.canvas.drawRect(RectF(x, base.innerTop, xRight, base.contentBot), base.pPanel)
                    base.canvas.drawRect(RectF(x, base.innerTop, xRight, base.contentBot), base.pLinea)
                    x += base.bPx + grumaPx
                }
            }
        }

        val nDiv   = maxOf(1, nDivisiones)
        val altDiv = (contentAlto - (nDiv - 1) * base.bPx) / nDiv
        for (i in 0 until nDiv) {
            val yTop = base.innerTop + i * (altDiv + base.bPx)
            val rVid = RectF(xRightCol, yTop, xRightCol + rightAncho, yTop + altDiv)
            base.canvas.drawRect(rVid, base.pInterior); base.canvas.drawRect(rVid, base.pLineaFin)
            if (i < nDiv - 1) {
                val rBar = RectF(xRightCol, yTop + altDiv, xRightCol + rightAncho, yTop + altDiv + base.bPx)
                base.canvas.drawRect(rBar, base.pPanel); base.canvas.drawRect(rBar, base.pLinea)
            }
        }
        base.canvas.drawRect(RectF(xRightCol, base.innerTop, xRightCol + rightAncho, base.contentBot), base.pLinea)

        base.canvas.drawRect(base.hojaRect, base.pLinea)
        base.canvas.restore()
        return base.bmp
    }

    // ════════════════════════════════════════════════════════════════════════
    // Mili  ──────────────────────────────────────────────────────────────────
    // ════════════════════════════════════════════════════════════════════════

    fun generarBitmapMili(
        context: Context,
        anchoCm: Float,
        altoCm: Float,
        altoHojaCm: Float,
        anchoContenedor: Float,
        altoContenedor: Float,
        marcoCm: Float = 2.2f,
        pisoCm: Float = 0f,
        nZocalo: Int = 0
    ): Bitmap {
        val base      = crearBaseHoja(context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm, nZocalo)
        val divPx     = 3.8f * base.factor
        val innerAncho = base.innerRight - base.innerLeft
        val innerAlto  = base.contentBot - base.innerTop
        val colAncho   = (innerAncho - 2f * divPx) / 3f
        val rowH       = (innerAlto  - 2f * divPx) / 6f

        val xDiv1   = base.innerLeft  + colAncho
        val xDiv2   = base.innerLeft  + 2f * colAncho + divPx
        val yBarSup = base.innerTop   + rowH
        val yBarInf = base.contentBot - rowH - divPx

        base.canvas.drawRect(RectF(base.innerLeft, base.innerTop, base.innerRight, base.contentBot), base.pInterior)

        base.canvas.drawRect(RectF(xDiv1, base.innerTop,    xDiv1 + divPx, yBarInf),         base.pPanel)
        base.canvas.drawRect(RectF(xDiv1, base.innerTop,    xDiv1 + divPx, yBarInf),         base.pLinea)
        base.canvas.drawRect(RectF(xDiv2, yBarSup + divPx,  xDiv2 + divPx, base.contentBot), base.pPanel)
        base.canvas.drawRect(RectF(xDiv2, yBarSup + divPx,  xDiv2 + divPx, base.contentBot), base.pLinea)
        base.canvas.drawRect(RectF(xDiv1 + divPx, yBarSup, base.innerRight, yBarSup + divPx), base.pPanel)
        base.canvas.drawRect(RectF(xDiv1 + divPx, yBarSup, base.innerRight, yBarSup + divPx), base.pLinea)
        base.canvas.drawRect(RectF(base.innerLeft, yBarInf, xDiv2,          yBarInf + divPx), base.pPanel)
        base.canvas.drawRect(RectF(base.innerLeft, yBarInf, xDiv2,          yBarInf + divPx), base.pLinea)

        base.canvas.drawRect(base.hojaRect, base.pLinea)
        base.canvas.restore()
        return base.bmp
    }

    // ════════════════════════════════════════════════════════════════════════
    // Jeny  ──────────────────────────────────────────────────────────────────
    // Cuadrícula de perfiles de aluminio de 2.5 cm.
    // ════════════════════════════════════════════════════════════════════════

    fun generarBitmapJeny(
        context: Context,
        anchoCm: Float,
        altoCm: Float,
        altoHojaCm: Float,
        nCols: Int,
        nRows: Int,
        anchoContenedor: Float,
        altoContenedor: Float,
        marcoCm: Float = 2.2f,
        pisoCm: Float = 0f,
        nZocalo: Int = 1
    ): Bitmap {
        val base     = crearBaseHoja(context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm, nZocalo)
        val profPx   = 2.5f * base.factor
        val nc       = maxOf(1, nCols)
        val nr       = maxOf(1, nRows)
        val innerAncho = base.innerRight - base.innerLeft
        val innerAlto  = base.contentBot - base.innerTop

        base.canvas.drawRect(RectF(base.innerLeft, base.innerTop, base.innerRight, base.contentBot), base.pInterior)

        val colW = (innerAncho - (nc - 1) * profPx) / nc
        for (i in 0 until nc - 1) {
            val x = base.innerLeft + (i + 1) * colW + i * profPx
            val r = RectF(x, base.innerTop, x + profPx, base.contentBot)
            base.canvas.drawRect(r, base.pPanel); base.canvas.drawRect(r, base.pLinea)
        }

        val rowH = (innerAlto - (nr - 1) * profPx) / nr
        for (i in 0 until nr - 1) {
            val y = base.innerTop + (i + 1) * rowH + i * profPx
            val r = RectF(base.innerLeft, y, base.innerRight, y + profPx)
            base.canvas.drawRect(r, base.pPanel); base.canvas.drawRect(r, base.pLinea)
        }

        base.canvas.drawRect(base.hojaRect, base.pLinea)
        base.canvas.restore()
        return base.bmp
    }

    // ════════════════════════════════════════════════════════════════════════
    // Dora  ──────────────────────────────────────────────────────────────────
    // 3 columnas iguales; izquierda = cuadrícula 10 secciones (2.5cm); centro+der = 2 diagonales ±45°
    // ════════════════════════════════════════════════════════════════════════

    fun generarBitmapDora(
        context: Context,
        anchoCm: Float,
        altoCm: Float,
        altoHojaCm: Float,
        anchoContenedor: Float,
        altoContenedor: Float,
        marcoCm: Float = 2.2f,
        pisoCm: Float = 0f,
        nZocalo: Int = 1
    ): Bitmap {
        // Solo impar: el último zócalo siempre es paflon (8.25cm)
        val nZ   = nZocalo.let { if (it % 2 == 0) maxOf(1, it - 1) else it }
        val base        = crearBaseHoja(context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm, nZ, zocaloAlternado = true)
        val profPx      = 2.5f * base.factor
        val innerAncho  = base.innerRight - base.innerLeft
        val contentAlto = base.contentBot - base.innerTop

        val colW  = (innerAncho - 2f * base.bPx) / 3f
        val xDiv1 = base.innerLeft + colW
        val xCol2 = xDiv1 + base.bPx
        val xDiv2 = xCol2 + colW
        val xCol3 = xDiv2 + base.bPx

        // Fondo blanco solo hasta contentBot — los zócalos extra (contentBot→innerBot) quedan visibles
        base.canvas.drawRect(RectF(base.innerLeft, base.innerTop, base.innerRight, base.contentBot), base.pInterior)

        // Divisor vertical izquierdo (xDiv1)
        val rDiv1 = RectF(xDiv1, base.innerTop, xDiv1 + base.bPx, base.contentBot)
        base.canvas.drawRect(rDiv1, base.pPanel); base.canvas.drawRect(rDiv1, base.pLinea)

        // Columna izquierda: 10 secciones con divisores de 2.5 cm
        val nSec = 10
        val secH = (contentAlto - (nSec - 1) * profPx) / nSec
        for (i in 0 until nSec - 1) {
            val y = base.innerTop + (i + 1) * secH + i * profPx
            val r = RectF(base.innerLeft, y, xDiv1, y + profPx)
            base.canvas.drawRect(r, base.pPanel); base.canvas.drawRect(r, base.pLinea)
        }

        // Divisor vertical derecho (xDiv2)
        val rDiv2 = RectF(xDiv2, base.innerTop, xDiv2 + base.bPx, base.contentBot)
        base.canvas.drawRect(rDiv2, base.pPanel); base.canvas.drawRect(rDiv2, base.pLinea)

        // Columnas centro + derecha: dos paflones a ±45°, puntas tocándose en (xCol2, yMid).
        // diagLen = diagonal del clipRect para garantizar cobertura total independiente de proporciones.
        val yMid     = (base.innerTop + base.contentBot) / 2f
        val clipW    = base.innerRight - xCol2
        val clipH    = base.contentBot - base.innerTop
        val diagLen  = kotlin.math.sqrt((clipW * clipW + clipH * clipH).toDouble()).toFloat() + base.bPx
        val clipDiag = RectF(xCol2, base.innerTop, base.innerRight, base.contentBot)

        base.canvas.save()
        base.canvas.clipRect(clipDiag)
        base.canvas.translate(xCol2, yMid)
        base.canvas.rotate(-45f)
        val barUp = RectF(0f, -base.bPx, diagLen, 0f)   // borde inferior en y=0 → punta en yMid
        base.canvas.drawRect(barUp, base.pPanel); base.canvas.drawRect(barUp, base.pLinea)
        base.canvas.restore()

        base.canvas.save()
        base.canvas.clipRect(clipDiag)
        base.canvas.translate(xCol2, yMid)
        base.canvas.rotate(45f)
        val barDown = RectF(0f, 0f, diagLen, base.bPx)  // borde superior en y=0 → punta en yMid
        base.canvas.drawRect(barDown, base.pPanel); base.canvas.drawRect(barDown, base.pLinea)
        base.canvas.restore()

        base.canvas.drawRect(base.hojaRect, base.pLinea)
        base.canvas.restore()
        return base.bmp
    }

    // ════════════════════════════════════════════════════════════════════════
    // Tere  ──────────────────────────────────────────────────────────────────
    // Interior relleno completamente con paflones en horizontal.
    // ════════════════════════════════════════════════════════════════════════

    fun generarBitmapTere(
        context: Context,
        anchoCm: Float,
        altoCm: Float,
        altoHojaCm: Float,
        anchoContenedor: Float,
        altoContenedor: Float,
        marcoCm: Float = 2.2f,
        pisoCm: Float = 0f,
        nZocalo: Int = 1
    ): Bitmap {
        val base    = crearBaseHoja(context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm, nZocalo)
        val grumaPx = 0.5f * base.factor

        var y = base.innerTop
        while (y < base.contentBot - 0.5f) {
            val yBot = minOf(y + base.bPx, base.contentBot)
            val r = RectF(base.innerLeft, y, base.innerRight, yBot)
            base.canvas.drawRect(r, base.pPanel); base.canvas.drawRect(r, base.pLinea)
            y += base.bPx + grumaPx
        }

        base.canvas.drawRect(base.hojaRect, base.pLinea)
        base.canvas.restore()
        return base.bmp
    }

    private fun dibujarVidrio(canvas: Canvas, rect: RectF, paint: Paint) {
        val paso = maxOf(rect.width(), rect.height()) / 6f
        canvas.save()
        canvas.clipRect(rect)
        var x = rect.left - rect.height()
        while (x < rect.right + rect.height()) {
            canvas.drawLine(x, rect.top, x + rect.height(), rect.bottom, paint)
            x += paso
        }
        canvas.restore()
    }

    fun guardarBitmapEnCache(context: Context, bitmap: Bitmap) {
        try {
            val archivo = java.io.File(context.cacheDir, "imagen_puerta.png")
            java.io.FileOutputStream(archivo).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}