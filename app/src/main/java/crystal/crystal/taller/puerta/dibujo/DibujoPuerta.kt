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
        bastidorCm: Float = 8.25f
    ): Bitmap {
        val factorEscala = minOf(anchoContenedor / anchoPuertaCm, altoContenedor / altoPuertaCm)
        val anchoPuertaPx = anchoPuertaCm * factorEscala
        val altoPuertaPx = altoPuertaCm * factorEscala
        val anchoHojaPx = anchoHojaCm * factorEscala
        val altoHojaPx = altoHojaCm * factorEscala
        val marcoPx = marcoCm * factorEscala

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
        dibujarMarcoExterno(canvas, anchoPuertaPx, altoPuertaPx, marcoPx, pinturaMarco, pinturaLinea)

        // Posición de hoja (a 1 cm del piso)
        val offsetHojaBottomPx = 1f * factorEscala
        val bottomHoja = altoPuertaPx - offsetHojaBottomPx
        val topHoja = bottomHoja - altoHojaPx
        val leftHoja = (anchoPuertaPx - anchoHojaPx) / 2f
        val rectHoja = RectF(leftHoja, topHoja, leftHoja + anchoHojaPx, bottomHoja)

        val paflonPx = bastidorCm * factorEscala
        dibujarHojaCompleta(canvas, rectHoja, numeroZocalos, numeroDivisiones, paflonPx, pinturaMarco, pinturaPaflon, pinturaInterior, pinturaLinea, tipoDivision, anguloGrados)

        // Mocheta por encima de la hoja (0.5 cm gap + 2.5 cm altura)
        dibujarMocheta(canvas, anchoPuertaPx, marcoPx, topHoja, factorEscala, pinturaMarco, pinturaInterior, pinturaLinea)

        canvas.restore()
        return bmp
    }

    private fun dibujarMarcoExterno(canvas: Canvas, anchoPuertaPx: Float, altoPuertaPx: Float, marcoPx: Float, pMarco: Paint, pLinea: Paint) {
        val izq = RectF(0f, 0f, marcoPx, altoPuertaPx)
        val der = RectF(anchoPuertaPx - marcoPx, 0f, anchoPuertaPx, altoPuertaPx)
        val sup = RectF(marcoPx, 0f, anchoPuertaPx - marcoPx, marcoPx)
        canvas.drawRect(izq, pMarco); canvas.drawRect(izq, pLinea)
        canvas.drawRect(der, pMarco); canvas.drawRect(der, pLinea)
        canvas.drawRect(sup, pMarco); canvas.drawRect(sup, pLinea)
    }

    private fun dibujarMocheta(canvas: Canvas, anchoPuertaPx: Float, marcoPx: Float, topHoja: Float, factorEscala: Float, pMarco: Paint, pInterior: Paint, pLinea: Paint) {
        val gapBelowFramePx = 0.5f * factorEscala
        val horizontalFrameHeightPx = 2.5f * factorEscala
        val yFrameBottom = topHoja - gapBelowFramePx
        val yFrameTop = yFrameBottom - horizontalFrameHeightPx
        val rectBlanco = RectF(marcoPx, marcoPx, anchoPuertaPx - marcoPx, yFrameTop)
        canvas.drawRect(rectBlanco, pInterior); canvas.drawRect(rectBlanco, pLinea)
        val rectMarco = RectF(marcoPx, yFrameTop, anchoPuertaPx - marcoPx, yFrameBottom)
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
                val totalH = h
                val gap = (totalH - (barras * thickness)) / (barras + 1)
                var y = top + gap
                repeat(barras) {
                    val leftBar = paflonPx - w
                    val rightBar = right + w
                    val r = RectF(leftBar, y, rightBar, y + thickness)
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

    fun guardarBitmapEnCache(context: Context, bitmap: Bitmap) {
        try {
            val archivo = java.io.File(context.cacheDir, "imagen_puerta.png")
            java.io.FileOutputStream(archivo).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}