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
        marcoCmDer: Float = marcoCm
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

        // Posición de hoja (a 0.5 cm del marco izquierdo)
        val offsetHojaBottomPx = 1f * factorEscala
        val bottomHoja = altoPuertaPx - offsetHojaBottomPx
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
        marcoCmDer: Float = marcoCm
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

        val offsetHojaBottomPx = 1f * factorEscala
        val bottomHoja = altoPuertaPx - offsetHojaBottomPx
        val topHoja = bottomHoja - altoHojaPx
        val leftHoja = marcoIzqPx + 0.5f * factorEscala

        dibujarMocheta(canvas, anchoPuertaPx, marcoIzqPx, marcoDerPx, marcoPx, topHoja, factorEscala, pinturaMarco, pinturaInterior, pinturaLinea)

        canvas.save()
        canvas.translate(leftHoja, topHoja)
        dibujarHojaTaly(canvas, anchoHojaPx, altoHojaPx, paflonPx, maxVacioPx, numeroDivisiones, anguloGrados, pinturaMarco, pinturaPaflon, pinturaInterior, pinturaLinea)
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
        pMarco: Paint,
        pPaflon: Paint,
        pInterior: Paint,
        pLinea: Paint
    ) {
        val topY = paflonPx
        val botY = alto - paflonPx

        // Fondo gris
        canvas.drawRect(RectF(0f, 0f, ancho, alto), pMarco)
        // Interior blanco
        canvas.drawRect(RectF(paflonPx, topY, ancho - paflonPx, botY), pInterior)

        // Bastidor: 4 lados
        val bastidor = listOf(
            RectF(0f, 0f, paflonPx, alto),
            RectF(ancho - paflonPx, 0f, ancho, alto),
            RectF(paflonPx, 0f, ancho - paflonPx, paflonPx),
            RectF(paflonPx, botY, ancho - paflonPx, alto)
        )
        for (r in bastidor) { canvas.drawRect(r, pPaflon) }

        // Paflones verticales interiores por pares desde los bordes hasta vacío ≤ maxVacioPx
        var leftX = paflonPx
        var rightX = ancho - paflonPx
        while (rightX - leftX > maxVacioPx && rightX - leftX >= paflonPx * 2f) {
            canvas.drawRect(RectF(leftX, topY, leftX + paflonPx, botY), pPaflon)
            canvas.drawRect(RectF(rightX - paflonPx, topY, rightX, botY), pPaflon)
            leftX += paflonPx
            rightX -= paflonPx
        }

        // topInner y botInner: horizontales fijos (no se rotan)
        val topInner = RectF(leftX, topY, rightX, topY + paflonPx)
        val botInner = RectF(leftX, botY - paflonPx, rightX, botY)
        canvas.drawRect(topInner, pPaflon)
        canvas.drawRect(botInner, pPaflon)

        // Divisores interiores en la zona central — estrategia Mari d:
        // gap = (zoneH - barras * paflonPx) / (barras + 1) → espacios iguales arriba/medio/abajo
        val zoneTop = topY + paflonPx
        val zoneBot = botY - paflonPx
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

        // Contornos paflones verticales interiores
        var lx = paflonPx; var rx = ancho - paflonPx
        while (rx - lx > maxVacioPx && rx - lx >= paflonPx * 2f) {
            canvas.drawRect(RectF(lx, topY, lx + paflonPx, botY), pLinea)
            canvas.drawRect(RectF(rx - paflonPx, topY, rx, botY), pLinea)
            lx += paflonPx; rx -= paflonPx
        }

        canvas.drawRect(topInner, pLinea)
        canvas.drawRect(botInner, pLinea)

        // Contornos secciones de vidrio (sin rotación, Taly h)
        if (anguloGrados == 0f && barras > 0 && zoneH > 0f) {
            val gap = (zoneH - barras * paflonPx) / (barras + 1)
            var sy = zoneTop + gap + paflonPx
            repeat(barras - 1) {
                canvas.drawRect(RectF(leftX, sy, rightX, sy + gap), pLinea)
                sy += gap + paflonPx
            }
        }
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