package crystal.crystal.taller.puerta.dibujo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
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
        interiorCm: Float = bastidorCm, // aluminio de los divisores de adentro (Viky)
        marcoCmIzq: Float = marcoCm,
        marcoCmDer: Float = marcoCm,
        pisoCm: Float = 0f,
        puenteCm: Float = 2.5f,
        etiquetas: Map<String, String>? = null, // plano Mari h: rótulos (cm) por región
        cotas: List<Float>? = null, // plano Mari h/d: cotas verticales (cm) en el parante izquierdo
        cotasH: List<Float>? = null, // plano Mari v: cotas acumuladas (cm) horizontales desde la izquierda
        cotasDer: List<Float>? = null, // plano Mari d: cotas verticales (cm) en el parante derecho
        cotasSup: List<Float>? = null, // plano Mari d: cruces (cm, desde la izq.) en el paflon superior
        cotasInf: List<Float>? = null, // plano Mari d: cruces (cm, desde la izq.) en el paflon inferior
        alturaPuente: Float? = null, // plano: alto de hoja (cm), rotulado en el tope de la hoja
        escalaInterna: Float = 1f // plano: < 1 encoge el dibujo para dejar margen a las cotas en ambos lados
    ): Bitmap {
        val factorEscala = minOf(anchoContenedor / anchoPuertaCm, altoContenedor / altoPuertaCm) * escalaInterna
        val anchoPuertaPx = anchoPuertaCm * factorEscala
        val altoPuertaPx = altoPuertaCm * factorEscala
        val anchoHojaPx = anchoHojaCm * factorEscala
        val altoHojaPx = altoHojaCm * factorEscala
        val marcoPx = marcoCm * factorEscala
        val marcoIzqPx = marcoCmIzq * factorEscala
        val marcoDerPx = marcoCmDer * factorEscala

        val bmp = Bitmap.createBitmap(anchoContenedor.toInt(), altoContenedor.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(if (etiquetas != null || cotas != null || cotasH != null) Color.WHITE else Color.RED)

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
        val interiorPx = interiorCm * factorEscala
        dibujarHojaCompleta(canvas, rectHoja, numeroZocalos, numeroDivisiones, paflonPx, interiorPx, pinturaMarco, pinturaPaflon, pinturaInterior, pinturaLinea, tipoDivision, anguloGrados)

        // Mocheta por encima de la hoja (0.5 cm gap + 2.5 cm altura)
        dibujarMocheta(canvas, anchoPuertaPx, marcoIzqPx, marcoDerPx, marcoPx, topHoja, factorEscala, pinturaMarco, pinturaInterior, pinturaLinea, puenteCm)

        // Plano Mari h: rótulos de medidas originales (cm) sobre cada pieza/franja.
        val et = etiquetas
        if (et != null && tipoDivision != "V" && tipoDivision != "D") {
            val ts = altoContenedor * 0.016f
            val pTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK; textSize = ts; textAlign = Paint.Align.CENTER
            }
            val pBg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; alpha = 200 }
            fun lab(texto: String, cx: Float, cy: Float, ang: Float) {
                val w = pTxt.measureText(texto)
                canvas.save(); canvas.rotate(ang, cx, cy)
                canvas.drawRect(cx - w / 2 - 3f, cy - ts * 0.85f, cx + w / 2 + 3f, cy + ts * 0.25f, pBg)
                canvas.drawText(texto, cx, cy, pTxt)
                canvas.restore()
            }
            val intLeft = rectHoja.left + paflonPx
            val intRight = rectHoja.right - paflonPx
            val intTop = rectHoja.top + paflonPx
            val intBottom = rectHoja.bottom - numeroZocalos * paflonPx
            val barras = (numeroDivisiones - 1).coerceAtLeast(0)
            val gapY = (intBottom - intTop - barras * paflonPx) / (barras + 1)
            val cxMid = (intLeft + intRight) / 2f
            et["gen"]?.let { lab(it, cxMid, rectHoja.top - ts * 0.5f, 0f) }
            et["ancho"]?.let { lab(it, cxMid, intTop + gapY * 0.5f, 0f) }
            // Alto de cada franja (vertical, pegado a la arista izquierda interior)
            var y = intTop
            repeat(barras + 1) {
                et["secAlto"]?.let { lab(it, intLeft + ts * 0.9f, y + gapY / 2f, -90f) }
                y += gapY + paflonPx
            }
            et["parante"]?.let { lab(it, rectHoja.left + paflonPx * 0.5f, (intTop + intBottom) / 2f, -90f) }
            et["zoc"]?.let { lab(it, cxMid, intBottom + numeroZocalos * paflonPx / 2f, 0f) }
        }

        // Plano Mari h: columna de COTAS ACUMULADAS desde la base (el ensayo de paños), en el
        // margen izquierdo, con guía hacia cada paflon. Son las medidas útiles para marcar/mecanizar.
        cotas?.let { dibujarCotasVerticales(canvas, it, altoContenedor, factorEscala, bottomHoja, marcoIzqPx) }

        // Plano Mari v: fila de COTAS ACUMULADAS horizontales desde la izquierda, debajo de la hoja,
        // con guía hacia cada paflon vertical.
        cotasH?.let {
            val xIzq = marcoIzqPx + 0.5f * factorEscala + bastidorCm * factorEscala // borde interior izq.
            val ts = altoContenedor * 0.018f
            dibujarCotasHorizontales(canvas, it, altoContenedor, factorEscala, xIzq, altoPuertaPx + ts * 1.8f, bottomHoja)
        }

        // Plano Mari d: columna de cotas en el parante DERECHO (cruces de las diagonales).
        cotasDer?.let {
            dibujarCotasVerticalesDerecha(canvas, it, altoContenedor, factorEscala, bottomHoja, anchoPuertaPx, anchoPuertaPx - marcoDerPx)
        }

        // Plano Mari d: cruces de las diagonales con los paflones SUPERIOR e INFERIOR (rótulos
        // horizontales, posición desde el borde interior izquierdo).
        val ctS = cotasSup; val ctI = cotasInf
        if ((ctS != null && ctS.isNotEmpty()) || (ctI != null && ctI.isNotEmpty())) {
            val ts = altoContenedor * 0.018f
            fun fmt(v: Float) = crystal.crystal.taller.puerta.logica.CalculosPuerta.df1(v)
            val pTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK; textSize = ts; textAlign = Paint.Align.CENTER
            }
            val pBg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; alpha = 205 }
            fun etiq(texto: String, cx: Float, cy: Float) {
                val w = pTxt.measureText(texto)
                canvas.drawRect(cx - w / 2 - 3f, cy - ts * 0.85f, cx + w / 2 + 3f, cy + ts * 0.25f, pBg)
                canvas.drawText(texto, cx, cy, pTxt)
            }
            val xInt = rectHoja.left + paflonPx           // borde interior izquierdo
            val intTop = rectHoja.top + paflonPx
            val intBot = rectHoja.bottom - numeroZocalos * paflonPx

            // Cuando dos diagonales cruzan cerca, sus rótulos son más anchos que la separación y se
            // encimaban hasta volverse ilegibles. Se reparten en filas: cada rótulo va a la primera
            // fila donde no choque con el anterior, alejándose del dibujo (arriba los de la tapa
            // superior, abajo los de la inferior). Con separación suficiente todos quedan en una
            // sola fila, igual que antes.
            fun etiquetasEscalonadas(valores: List<Float>, yBase: Float, haciaArriba: Boolean) {
                if (valores.isEmpty()) return
                val paso = ts * 1.15f
                val separacion = ts * 0.25f
                val ocupadoHasta = mutableListOf<Float>()   // borde derecho del último de cada fila
                valores.sorted().forEach { c ->
                    val texto = fmt(c)
                    val cx = xInt + c * factorEscala
                    val mitad = pTxt.measureText(texto) / 2f + 3f
                    var fila = 0
                    while (fila < ocupadoHasta.size && (cx - mitad) < ocupadoHasta[fila] + separacion) fila++
                    if (fila == ocupadoHasta.size) ocupadoHasta.add(cx + mitad) else ocupadoHasta[fila] = cx + mitad
                    val cy = if (haciaArriba) yBase - fila * paso else yBase + fila * paso
                    etiq(texto, cx, cy)
                }
            }

            etiquetasEscalonadas(ctS.orEmpty(), intTop - ts * 0.2f, haciaArriba = true)
            etiquetasEscalonadas(ctI.orEmpty(), intBot + ts * 0.9f, haciaArriba = false)
        }

        // Plano: alto de hoja (lo que Referencias llama "Alto hoja"), sobre el tope de la hoja.
        val aP = alturaPuente
        if (aP != null) {
            val ts = altoContenedor * 0.019f
            val pTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK; textSize = ts; textAlign = Paint.Align.CENTER
            }
            val pBg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; alpha = 210 }
            // Es una medida de la hoja, no una cota: un decimal.
            val texto = "Alto hoja ${crystal.crystal.taller.puerta.logica.CalculosPuerta.df1(aP)}"
            val cx = leftHoja + anchoHojaPx / 2f
            val cy = topHoja - ts * 0.5f
            val w = pTxt.measureText(texto)
            canvas.drawRect(cx - w / 2 - 4f, cy - ts * 0.85f, cx + w / 2 + 4f, cy + ts * 0.25f, pBg)
            canvas.drawText(texto, cx, cy, pTxt)
        }

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

    private fun dibujarMocheta(canvas: Canvas, anchoPuertaPx: Float, marcoIzqPx: Float, marcoDerPx: Float, marcoSupPx: Float, topHoja: Float, factorEscala: Float, pMarco: Paint, pInterior: Paint, pLinea: Paint, puenteCm: Float = 2.5f) {
        val gapBelowFramePx = 0.5f * factorEscala
        val horizontalFrameHeightPx = puenteCm * factorEscala
        val yFrameBottom = topHoja - gapBelowFramePx
        val yFrameTop = yFrameBottom - horizontalFrameHeightPx
        val rectBlanco = RectF(marcoIzqPx, marcoSupPx, anchoPuertaPx - marcoDerPx, yFrameTop)
        canvas.drawRect(rectBlanco, pInterior); canvas.drawRect(rectBlanco, pLinea)
        val rectMarco = RectF(marcoIzqPx, yFrameTop, anchoPuertaPx - marcoDerPx, yFrameBottom)
        canvas.drawRect(rectMarco, pMarco); canvas.drawRect(rectMarco, pLinea)
    }

    /**
     * Regla de cotas acumuladas desde la base, en el margen izquierdo, con una guía fina hasta la
     * pieza. Dos decimales para que no descuadre por redondeo. La comparten Mari, Viky y Adel.
     */
    private fun dibujarCotasVerticales(
        canvas: Canvas, cotas: List<Float>, altoContenedor: Float, factor: Float,
        yBase: Float, xGuiaHasta: Float
    ) {
        if (cotas.isEmpty()) return
        val ts = altoContenedor * 0.018f
        fun fmt(v: Float) = crystal.crystal.taller.puerta.logica.CalculosPuerta.df1(v)
        val pTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK; textSize = ts; textAlign = Paint.Align.RIGHT
        }
        val pLn = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK; strokeWidth = maxOf(2f, ts * 0.09f); style = Paint.Style.STROKE
        }
        val pGuia = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(120, 0, 0, 0); strokeWidth = 1.5f; style = Paint.Style.STROKE
        }
        val xLine = -ts * 1.4f
        canvas.drawLine(xLine, yBase, xLine, yBase - (cotas.max()) * factor, pLn)
        canvas.drawLine(xLine - ts * 0.45f, yBase, xLine + ts * 0.45f, yBase, pLn)   // marca 0
        canvas.drawText("0", xLine - ts * 0.6f, yBase + ts * 0.32f, pTxt)
        for (c in cotas) {
            val y = yBase - c * factor
            canvas.drawLine(xLine - ts * 0.45f, y, xLine + ts * 0.45f, y, pLn)
            canvas.drawLine(xLine + ts * 0.45f, y, xGuiaHasta, y, pGuia)
            canvas.drawText(fmt(c), xLine - ts * 0.6f, y + ts * 0.32f, pTxt)
        }
    }

    /**
     * Rótulo de la medida de una pieza, encima de ella. Va en rojo y en negrita, sobre un fondo
     * blanco, para que se distinga de las reglas de cotas: estas dicen DÓNDE va cada pieza y el
     * rótulo dice CUÁNTO mide, que es otra cosa y el técnico no debería tener que restar dos cotas
     * para deducirla.
     */
    private fun dibujarRotuloPieza(
        canvas: Canvas, texto: String, cx: Float, cy: Float, anguloGrados: Float, ts: Float
    ) {
        val pTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(200, 30, 30); textSize = ts; textAlign = Paint.Align.CENTER
            typeface = Typeface.DEFAULT_BOLD
        }
        val pFondo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; alpha = 225 }
        val ancho = pTxt.measureText(texto)
        canvas.save()
        canvas.rotate(anguloGrados, cx, cy)
        canvas.drawRect(cx - ancho / 2 - 4f, cy - ts * 0.85f, cx + ancho / 2 + 4f, cy + ts * 0.28f, pFondo)
        canvas.drawText(texto, cx, cy, pTxt)
        canvas.restore()
    }

    /**
     * Cota corta sobre una pieza: la línea entre los dos puntos, un tope en cada extremo y el valor
     * al medio, en rojo y en negrita. Es para medir DENTRO de una pieza suelta —de una esquina a
     * donde va un tubo—, no desde la hoja: eso lo hacen las reglas del margen.
     */
    private fun dibujarCotaPieza(
        canvas: Canvas, texto: String, x1: Float, y1: Float, x2: Float, y2: Float, ts: Float
    ) {
        val rojo = Color.rgb(200, 30, 30)
        val pLn = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = rojo; strokeWidth = maxOf(2f, ts * 0.1f); style = Paint.Style.STROKE
        }
        val esVertical = kotlin.math.abs(y2 - y1) > kotlin.math.abs(x2 - x1)
        canvas.drawLine(x1, y1, x2, y2, pLn)
        val tope = ts * 0.35f
        if (esVertical) {
            canvas.drawLine(x1 - tope, y1, x1 + tope, y1, pLn)
            canvas.drawLine(x2 - tope, y2, x2 + tope, y2, pLn)
        } else {
            canvas.drawLine(x1, y1 - tope, x1, y1 + tope, pLn)
            canvas.drawLine(x2, y2 - tope, x2, y2 + tope, pLn)
        }
        dibujarRotuloPieza(canvas, texto, (x1 + x2) / 2f, (y1 + y2) / 2f + ts * 0.35f, if (esVertical) -90f else 0f, ts)
    }

    /** La misma regla vertical, pero en el margen derecho: sirve para un segundo juego de alturas. */
    private fun dibujarCotasVerticalesDerecha(
        canvas: Canvas, cotas: List<Float>, altoContenedor: Float, factor: Float,
        yBase: Float, anchoPuertaPx: Float, xGuiaDesde: Float
    ) {
        if (cotas.isEmpty()) return
        val ts = altoContenedor * 0.018f
        fun fmt(v: Float) = crystal.crystal.taller.puerta.logica.CalculosPuerta.df1(v)
        val pTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK; textSize = ts; textAlign = Paint.Align.LEFT
        }
        val pLn = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK; strokeWidth = maxOf(2f, ts * 0.09f); style = Paint.Style.STROKE
        }
        val pGuia = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(120, 0, 0, 0); strokeWidth = 1.5f; style = Paint.Style.STROKE
        }
        val xLine = anchoPuertaPx + ts * 1.4f
        canvas.drawLine(xLine, yBase, xLine, yBase - (cotas.max()) * factor, pLn)
        canvas.drawLine(xLine - ts * 0.45f, yBase, xLine + ts * 0.45f, yBase, pLn)   // marca 0
        canvas.drawText("0", xLine + ts * 0.6f, yBase + ts * 0.32f, pTxt)
        for (c in cotas) {
            val y = yBase - c * factor
            canvas.drawLine(xLine - ts * 0.45f, y, xLine + ts * 0.45f, y, pLn)
            canvas.drawLine(xGuiaDesde, y, xLine - ts * 0.45f, y, pGuia)
            canvas.drawText(fmt(c), xLine + ts * 0.6f, y + ts * 0.32f, pTxt)
        }
    }

    /**
     * La misma regla pero horizontal, bajo la hoja. Con `desdeDerecha` se mide desde el borde
     * interior derecho hacia la izquierda: sirve cuando las cotas no llegan ni a la mitad y del lado
     * izquierdo se juntan con el cero de la regla vertical.
     */
    private fun dibujarCotasHorizontales(
        canvas: Canvas, cotas: List<Float>, altoContenedor: Float, factor: Float,
        xIzq: Float, yDim: Float, yGuiaHasta: Float, desdeDerecha: Boolean = false, xDer: Float = xIzq
    ) {
        if (cotas.isEmpty()) return
        val ts = altoContenedor * 0.018f
        fun fmt(v: Float) = crystal.crystal.taller.puerta.logica.CalculosPuerta.df1(v)
        val pTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK; textSize = ts; textAlign = Paint.Align.CENTER
        }
        val pLn = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK; strokeWidth = maxOf(2f, ts * 0.09f); style = Paint.Style.STROKE
        }
        val pGuia = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(120, 0, 0, 0); strokeWidth = 1.5f; style = Paint.Style.STROKE
        }
        val xCero = if (desdeDerecha) xDer else xIzq
        val signo = if (desdeDerecha) -1f else 1f
        canvas.drawLine(xCero, yDim, xCero + signo * (cotas.max()) * factor, yDim, pLn)
        canvas.drawLine(xCero, yDim - ts * 0.45f, xCero, yDim + ts * 0.45f, pLn)    // marca 0
        canvas.drawText("0", xCero, yDim + ts * 1.2f, pTxt)

        // Los rótulos que no entran uno al lado del otro bajan a un segundo renglón, alternando. Sin
        // esto, dos cotas cercanas se imprimen encimadas y no se lee ninguna de las dos.
        var derechaOcupada = xCero + signo * pTxt.measureText("0") / 2f
        var renglonBajo = false
        for (c in cotas) {
            val x = xCero + signo * c * factor
            canvas.drawLine(x, yDim - ts * 0.45f, x, yDim + ts * 0.45f, pLn)
            canvas.drawLine(x, yDim - ts * 0.45f, x, yGuiaHasta, pGuia)

            val texto = fmt(c)
            val medio = pTxt.measureText(texto) / 2f
            val pisa = if (desdeDerecha) x + medio > derechaOcupada - ts * 0.3f else x - medio < derechaOcupada + ts * 0.3f
            renglonBajo = if (pisa) !renglonBajo else false
            canvas.drawText(texto, x, yDim + (if (renglonBajo) ts * 2.4f else ts * 1.2f), pTxt)
            if (!renglonBajo) derechaOcupada = x + signo * medio
        }
    }

    private fun dibujarHojaCompleta(
        canvas: Canvas,
        hojaRect: RectF,
        numeroZocalos: Int,
        numeroDivisiones: Int,
        paflonPx: Float,
        interiorPx: Float,
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
        dibujarAreaInternaHoja(canvas, ancho, alto, paflonPx, interiorPx, numeroZocalos, numeroDivisiones, pInterior, pPaflon, pLinea, tipoDivision, anguloGrados)
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
        interiorPx: Float,
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
            // En las tres divisiones de Mari —vertical, diagonal y horizontal— el divisor es el
            // aluminio del INTERIOR, no el bastidor. Mientras los dos sean el mismo perfil el dibujo
            // sale igual que siempre; si el vidriero cambia el interior, los paños se agrandan.
            "V" -> if (nDiv > 1) {
                val barras = nDiv - 1
                val w = rect.width(); if (w > 0) {
                    val gapX = (w - barras * interiorPx) / (barras + 1)
                    var x = paflonPx + gapX
                    repeat(barras) {
                        val r = RectF(x, top, x + interiorPx, bottom)
                        canvas.drawRect(r, pPaflon); canvas.drawRect(r, pLinea)
                        x += interiorPx + gapX
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
                val thickness = interiorPx
                val rad = Math.toRadians(anguloGrados.toDouble())
                val alcance = ((w * kotlin.math.abs(kotlin.math.sin(rad).toFloat())) +
                        (h * kotlin.math.abs(kotlin.math.cos(rad).toFloat()))) / 2f
                val gap = ((alcance * 2f) - barras * thickness) / (barras + 1)
                val diag = kotlin.math.sqrt(w * w + h * h)
                var y = cy - alcance + gap
                repeat(barras) {
                    val r = RectF(paflonPx - diag, y, right + diag, y + thickness)
                    canvas.drawRect(r, pPaflon); canvas.drawRect(r, pLinea)
                    y += thickness + gap
                }
                canvas.restore()
            }
            "VICKY" -> {
                // Como Mari h pero con un divisor vertical que separa una columna de CUADRADOS
                // (ancho = alto de sección) de una columna alta única (sin divisiones). Los
                // divisores usan el aluminio del interior, que puede ser más delgado que el bastidor.
                val barras = (nDiv - 1).coerceAtLeast(0)
                val h = bottom - top
                if (nDiv > 0 && h > 0) {
                    val gapY = (h - barras * interiorPx) / (barras + 1)  // lado del cuadrado
                    val xVert = paflonPx + gapY                          // borde der. de la columna de cuadrados
                    // Divisores horizontales SOLO en la columna de cuadrados (izquierda)
                    var y = top + gapY
                    repeat(barras) {
                        val r = RectF(paflonPx, y, xVert, y + interiorPx)
                        canvas.drawRect(r, pPaflon); canvas.drawRect(r, pLinea)
                        y += interiorPx + gapY
                    }
                    // Divisor vertical (del bastidor inferior al superior)
                    if (xVert + interiorPx <= right) {
                        val rv = RectF(xVert, top, xVert + interiorPx, bottom)
                        canvas.drawRect(rv, pPaflon); canvas.drawRect(rv, pLinea)
                    }
                }
            }
            "VICKYC" -> {
                // Cuadrícula pareja: un paflón vertical al medio y nDiv filas iguales, con los
                // divisores horizontales cortados por columna.
                val barras = (nDiv - 1).coerceAtLeast(0)
                val w = right - paflonPx
                val h = bottom - top
                if (nDiv > 0 && w > interiorPx && h > 0) {
                    val anchoCol = (w - interiorPx) / 2f
                    val xVert = paflonPx + anchoCol
                    val rv = RectF(xVert, top, xVert + interiorPx, bottom)
                    canvas.drawRect(rv, pPaflon); canvas.drawRect(rv, pLinea)

                    val gapY = (h - barras * interiorPx) / (barras + 1)
                    var y = top + gapY
                    repeat(barras) {
                        val izq = RectF(paflonPx, y, xVert, y + interiorPx)
                        val der = RectF(xVert + interiorPx, y, right, y + interiorPx)
                        canvas.drawRect(izq, pPaflon); canvas.drawRect(izq, pLinea)
                        canvas.drawRect(der, pPaflon); canvas.drawRect(der, pLinea)
                        y += interiorPx + gapY
                    }
                }
            }
            else -> if (nDiv > 1) { // Horizontal
                val barras = nDiv - 1
                val h = bottom - top; if (h > 0) {
                    val gapY = (h - barras * interiorPx) / (barras + 1)
                    var y = top + gapY
                    repeat(barras) {
                        val r = RectF(paflonPx, y, right, y + interiorPx)
                        canvas.drawRect(r, pPaflon); canvas.drawRect(r, pLinea)
                        y += interiorPx + gapY
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
        pisoCm: Float = 0f,
        puenteCm: Float = 2.5f,
        cotas: List<Float>? = null,     // plano: regla vertical izquierda (desde la base)
        cotasDer: List<Float>? = null,  // plano: regla vertical derecha (cruces del otro parante)
        cotasH: List<Float>? = null,    // plano: regla horizontal (desde el borde interior izquierdo)
        escalaInterna: Float = 1f       // plano: deja margen para las reglas
    ): Bitmap {
        val esPlano = cotas != null || cotasH != null
        val factorEscala = minOf(anchoContenedor / anchoPuertaCm, altoContenedor / altoPuertaCm) * escalaInterna
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
        canvas.drawColor(if (esPlano) Color.WHITE else Color.RED)

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

        dibujarMocheta(canvas, anchoPuertaPx, marcoIzqPx, marcoDerPx, marcoPx, topHoja, factorEscala, pinturaMarco, pinturaInterior, pinturaLinea, puenteCm)

        canvas.save()
        canvas.translate(leftHoja, topHoja)
        // El reparto de los parantes interiores sale del mismo cálculo que la lista de materiales.
        val zona = crystal.crystal.taller.puerta.logica.CalculosPuerta.zonaVidrioTaly(
            anchoHojaCm - 2f * bastidorCm, bastidorCm
        )
        dibujarHojaTaly(canvas, anchoHojaPx, altoHojaPx, paflonPx, zona.paresPaflon, zona.paresTubo, zona.tubo * factorEscala, numeroDivisiones, anguloGrados, nZocalo, pinturaMarco, pinturaPaflon, pinturaInterior, pinturaLinea)
        canvas.restore()

        cotas?.let {
            dibujarCotasVerticales(canvas, it, altoContenedor, factorEscala, bottomHoja, marcoIzqPx)
        }
        cotasDer?.let {
            dibujarCotasVerticalesDerecha(canvas, it, altoContenedor, factorEscala, bottomHoja, anchoPuertaPx, anchoPuertaPx - marcoDerPx)
        }
        cotasH?.let {
            val ts = altoContenedor * 0.018f
            dibujarCotasHorizontales(canvas, it, altoContenedor, factorEscala, leftHoja + paflonPx, altoPuertaPx + ts * 1.8f, bottomHoja)
        }

        canvas.restore()
        return bmp
    }

    private fun dibujarHojaTaly(
        canvas: Canvas,
        ancho: Float,
        alto: Float,
        paflonPx: Float,
        paresPaflon: Int,
        paresTubo: Int,
        tuboPx: Float,
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
        // Parantes interiores: los pares de paflón y, pegado al vidrio, el par de tubo si lo lleva.
        var leftX = paflonPx
        var rightX = ancho - paflonPx
        fun parInterior(grosor: Float) {
            canvas.drawRect(RectF(leftX, topY, leftX + grosor, bottomInterior), pPaflon)
            canvas.drawRect(RectF(rightX - grosor, topY, rightX, bottomInterior), pPaflon)
            leftX += grosor
            rightX -= grosor
        }
        repeat(paresPaflon) { parInterior(paflonPx) }
        repeat(paresTubo) { parInterior(tuboPx) }

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
            val zoneW = rightX - leftX
            val diag = kotlin.math.sqrt(zoneW * zoneW + zoneH * zoneH)
            val cx = (leftX + rightX) / 2f
            val cy = (zoneTop + zoneBot) / 2f
            val zoneRect = RectF(leftX, zoneTop, rightX, zoneBot)

            // Las barras se reparten sobre el ALCANCE de la zona girada, como en Mari d, no sobre su
            // alto sin girar: así los tramos salen iguales de verdad. Repartiéndolas en y y girando
            // el conjunto después, el recorte se comía las de los extremos y los vidrios de las
            // puntas quedaban de otro tamaño que los del medio. A 0° las dos cuentas dan lo mismo.
            val rad = Math.toRadians(anguloGrados.toDouble())
            val alcance = ((zoneW * kotlin.math.abs(kotlin.math.sin(rad).toFloat())) +
                (zoneH * kotlin.math.abs(kotlin.math.cos(rad).toFloat()))) / 2f
            val gap = ((alcance * 2f) - barras * paflonPx) / (barras + 1)

            canvas.save()
            canvas.clipRect(zoneRect)
            canvas.translate(cx, cy); canvas.rotate(anguloGrados); canvas.translate(-cx, -cy)
            var y = cy - alcance + gap
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
        fun contornoPar(grosor: Float) {
            canvas.drawRect(RectF(lx, topY, lx + grosor, bottomInterior), pLinea)
            canvas.drawRect(RectF(rx - grosor, topY, rx, bottomInterior), pLinea)
            lx += grosor; rx -= grosor
        }
        repeat(paresPaflon) { contornoPar(paflonPx) }
        repeat(paresTubo) { contornoPar(tuboPx) }
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
        pisoCm: Float = 0f,
        grumaCm: Float = 0.8f,
        panelDelgadoCm: Float = 0f,
        mostrarVidrioCentral: Boolean = true,
        puenteCm: Float = 3.5f,
        panelCompleto: Boolean = false, // "Lina c": una sola plancha, sin columnas ni gruma
        bastidorCm: Float = 8.25f,      // bastidor de la hoja, en paflón
        estructuraCm: Float = 3.8f,     // aluminio de la estructura interior (elegible: 8.25/3.8/5)
        travesanos: Int = 0,            // plano: travesaños de la contraplacada, en vez de la plancha
        cotas: List<Float>? = null,     // plano: regla vertical (desde la base de la hoja)
        escalaInterna: Float = 1f       // plano: deja margen para la regla
    ): Bitmap {
        val esPlano = cotas != null || travesanos > 0
        val factor   = minOf(anchoContenedor / anchoCm, altoContenedor / altoCm) * escalaInterna
        val anchoPx  = anchoCm  * factor
        val altoPx   = altoCm   * factor
        val marcoPx  = marcoCm  * factor

        val bmp    = Bitmap.createBitmap(anchoContenedor.toInt(), altoContenedor.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(if (esPlano) Color.WHITE else Color.RED)

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

        // "Lina c" y nada más. La plancha cubre la hoja entera —es lo que mide su lista— así que la
        // puerta se ve lisa y el bastidor queda tapado.
        //
        // En el plano se hace al revés: la plancha no se dibuja y se muestran el bastidor y sus
        // travesaños, la estructura de la contraplacada, que es lo que el técnico ya no puede ver una
        // vez armada. El resto de las variantes de Lina sigue por el camino de siempre, intacto.
        if (panelCompleto) {
            val estructuraPx = estructuraCm * factor
            if (travesanos > 0) {
                val bPx = bastidorCm * factor
                val cuadro = RectF(cuerpoLeft, topCuerpo, cuerpoLeft + cuerpoAncho, cuerpoBot)
                for (r in listOf(
                    RectF(cuadro.left, cuadro.top, cuadro.left + bPx, cuadro.bottom),
                    RectF(cuadro.right - bPx, cuadro.top, cuadro.right, cuadro.bottom),
                    RectF(cuadro.left + bPx, cuadro.top, cuadro.right - bPx, cuadro.top + bPx),
                    RectF(cuadro.left + bPx, cuadro.bottom - bPx, cuadro.right - bPx, cuadro.bottom)
                )) { canvas.drawRect(r, pPanel); canvas.drawRect(r, pLinea) }

                val vacioTop = cuadro.top + bPx
                val vacioBot = cuadro.bottom - bPx
                val tramo = (vacioBot - vacioTop - travesanos * estructuraPx) / (travesanos + 1)
                repeat(travesanos) { i ->
                    val y = vacioTop + (i + 1) * tramo + i * estructuraPx
                    val r = RectF(cuadro.left + bPx, y, cuadro.right - bPx, y + estructuraPx)
                    canvas.drawRect(r, pPanel); canvas.drawRect(r, pLinea)
                }
            } else {
                val rPanel = RectF(cuerpoLeft, topCuerpo, cuerpoLeft + cuerpoAncho, cuerpoBot)
                canvas.drawRect(rPanel, pPanel); canvas.drawRect(rPanel, pLinea)
            }
            cotas?.let {
                dibujarCotasVerticales(canvas, it, altoContenedor, factor, cuerpoBot, marcoPx)
            }
            canvas.restore()
            return bmp
        }

        val grumaPx    = grumaCm * factor
        val anchoIzqPx = if (panelDelgadoCm > 0f) {
            (panelDelgadoCm * factor).coerceIn(0f, (cuerpoAncho - grumaPx).coerceAtLeast(0f))
        } else {
            (cuerpoAncho - grumaPx) / 4f
        }
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
        if (mostrarVidrioCentral) {
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
        }

        canvas.drawRect(rColDer, pLinea)

        canvas.restore()
        return bmp
    }

    /**
     * Plano técnico del interior de una Lina: el bastidor de la hoja con su estructura —el parante
     * interior, si lo lleva, y los rellenos horizontales—, sin dibujar las planchas. Es lo que el
     * técnico ya no puede ver una vez atornilladas.
     */
    fun generarBitmapLinaEstructura(
        context: Context,
        anchoCm: Float,
        altoCm: Float,
        altoHojaCm: Float,
        anchoContenedor: Float,
        altoContenedor: Float,
        marcoCm: Float = 2.2f,
        pisoCm: Float = 0f,
        puenteCm: Float = 2.5f,
        bastidorCm: Float = 8.25f,
        rellenoCm: Float = 3.8f,
        paranteDesdeIzq: Float? = null,      // cm desde el borde interior; null = sin parante interior
        alturasRelleno: List<Float> = emptyList(), // cm desde la base de la hoja
        cotas: List<Float>? = null,
        cotasH: List<Float>? = null,
        escalaInterna: Float = 0.84f
    ): Bitmap {
        val base = crearBaseHoja(
            context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm,
            nZocalo = 1, bastidorCm = bastidorCm, puenteCm = puenteCm,
            fondo = Color.WHITE, escalaInterna = escalaInterna
        )
        val rellenoPx = rellenoCm * base.factor

        // El vacío del bastidor, en blanco: acá no hay planchas que dibujar.
        val vacio = RectF(base.innerLeft, base.innerTop, base.innerRight, base.contentBot)
        base.canvas.drawRect(vacio, base.pInterior); base.canvas.drawRect(vacio, base.pLinea)

        val xParante = paranteDesdeIzq?.let { base.innerLeft + it * base.factor }
        xParante?.let {
            val r = RectF(it, base.innerTop, it + rellenoPx, base.contentBot)
            base.canvas.drawRect(r, base.pPanel); base.canvas.drawRect(r, base.pLinea)
        }

        // Los rellenos entran entre el parante interior y el bastidor, no lo cruzan.
        val xDesde = xParante?.plus(rellenoPx) ?: base.innerLeft
        alturasRelleno.forEach { altura ->
            val y = base.hojaRect.bottom - altura * base.factor - rellenoPx
            if (y > base.innerTop && y + rellenoPx < base.contentBot) {
                val r = RectF(xDesde, y, base.innerRight, y + rellenoPx)
                base.canvas.drawRect(r, base.pPanel); base.canvas.drawRect(r, base.pLinea)
            }
        }

        cotas?.let {
            dibujarCotasVerticales(base.canvas, it, altoContenedor, base.factor, base.hojaRect.bottom, marcoCm * base.factor)
        }
        cotasH?.let {
            val ts = altoContenedor * 0.018f
            dibujarCotasHorizontales(base.canvas, it, altoContenedor, base.factor, base.innerLeft, altoCm * base.factor + ts * 1.8f, base.hojaRect.bottom)
        }

        base.canvas.restore()
        return base.bmp
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
        zocaloAlternado: Boolean = false,  // pares=inoxCm, impares=bPx (solo Dora por ahora)
        puenteCm: Float = 2.5f,
        inoxCm: Float = 2.5f,              // tubo inox de los zócalos pares (solo Dora)
        fondo: Int = Color.RED,            // fondo del lienzo (blanco en modo plano)
        escalaInterna: Float = 1f          // < 1 encoge el dibujo para dejar margen a las cotas
    ): BaseHoja {
        val factor  = minOf(anchoContenedor / anchoCm, altoContenedor / altoCm) * escalaInterna
        val anchoPx = anchoCm * factor;  val altoPx = altoCm * factor
        val marcoPx = marcoCm * factor;  val bPx    = bastidorCm * factor

        val bmp    = Bitmap.createBitmap(anchoContenedor.toInt(), altoContenedor.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(fondo)
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
        val puentePx = puenteCm * factor
        val topCuerpo: Float
        if (altoCm > altoHojaCm + marcoCm + puenteCm + 2f) {
            val altoSLPx = (altoCm - altoHojaCm - puenteCm - marcoCm) * factor
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
        val smallPx = inoxCm * factor
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
        anchoContenedor: Float,
        altoContenedor: Float,
        marcoCm: Float = 2.2f,
        pisoCm: Float = 0f,
        nZocalo: Int = 0,
        puenteCm: Float = 2.5f,
        cotas: List<Float>? = null,   // plano: regla vertical (alturas acumuladas desde la base)
        cotasH: List<Float>? = null,  // plano: regla horizontal (columnas, desde el borde interior)
        escalaInterna: Float = 1f     // plano: deja margen para las dos reglas
    ): Bitmap {
        // Sin gruma: en Adel los paflones del relleno van pegados uno contra otro. La gruma es de
        // Lina. Con 0.8 de separación el relleno no cerraba y la última pieza salía cortada.
        val esPlano    = cotas != null || cotasH != null
        val base       = crearBaseHoja(
            context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm, nZocalo,
            puenteCm = puenteCm, fondo = if (esPlano) Color.WHITE else Color.RED, escalaInterna = escalaInterna
        )
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
                    y += base.bPx
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
                    x += base.bPx
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

        cotas?.let {
            dibujarCotasVerticales(base.canvas, it, altoContenedor, base.factor, base.hojaRect.bottom, marcoCm * base.factor)
        }
        cotasH?.let {
            val ts = altoContenedor * 0.018f
            val yDim = altoCm * base.factor + ts * 1.8f
            dibujarCotasHorizontales(base.canvas, it, altoContenedor, base.factor, base.innerLeft, yDim, base.hojaRect.bottom)
        }

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
        nZocalo: Int = 0,
        puenteCm: Float = 2.5f,
        cotas: List<Float>? = null,   // plano: regla vertical (alturas acumuladas desde la base)
        cotasH: List<Float>? = null,  // plano: regla horizontal (desde el borde interior izquierdo)
        escalaInterna: Float = 1f     // plano: deja margen para las dos reglas
    ): Bitmap {
        val esPlano   = cotas != null || cotasH != null
        val base      = crearBaseHoja(
            context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm, nZocalo,
            puenteCm = puenteCm, fondo = if (esPlano) Color.WHITE else Color.RED, escalaInterna = escalaInterna
        )
        val divPx     = crystal.crystal.taller.puerta.logica.CalculosPuerta.TUBO_MILI * base.factor
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

        cotas?.let {
            dibujarCotasVerticales(base.canvas, it, altoContenedor, base.factor, base.hojaRect.bottom, marcoCm * base.factor)
        }
        cotasH?.let {
            val ts = altoContenedor * 0.018f
            dibujarCotasHorizontales(base.canvas, it, altoContenedor, base.factor, base.innerLeft, altoCm * base.factor + ts * 1.8f, base.hojaRect.bottom)
        }

        base.canvas.restore()
        return base.bmp
    }

    // ════════════════════════════════════════════════════════════════════════
    // Jeny  ──────────────────────────────────────────────────────────────────
    // Cuadrícula de perfiles de aluminio de 2.5 cm.
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Jeny: el cuerpo se divide en paños horizontales como Mari h —con paflón de bastidor— y encima
     * del vidrio de CADA paño va una rejilla de perfil delgado, que se repite igual en todos.
     *
     * El dibujo de la rejilla cambia con la variante: "Jeny" es cuadrícula pareja, "Jeny c" la lleva
     * intercalada (una acostada menos en las columnas pares, que quedan corridas entre las otras) y
     * "Jeny r" no es cuadrícula sino un recuadro suspendido con seis amarres al bastidor.
     */
    fun generarBitmapJeny(
        context: Context,
        anchoCm: Float,
        altoCm: Float,
        altoHojaCm: Float,
        nDivisiones: Int,
        rejillaCols: Int,
        rejillaFilas: Int,
        variante: String = "Jeny",
        anchoContenedor: Float,
        altoContenedor: Float,
        marcoCm: Float = 2.2f,
        pisoCm: Float = 0f,
        nZocalo: Int = 1,
        puenteCm: Float = 2.5f,
        cotas: List<Float>? = null,     // plano: regla vertical izquierda (desde la base)
        cotasDer: List<Float>? = null,  // plano: regla vertical derecha (el segundo juego de alturas)
        cotasH: List<Float>? = null,    // plano: regla horizontal desde el borde interior izquierdo
        cotasHDer: List<Float>? = null, // plano: regla horizontal desde el borde interior derecho
        rotulos: Map<String, String>? = null, // plano: medidas rotuladas sobre la pieza
        escalaInterna: Float = 1f       // plano: deja margen para las reglas
    ): Bitmap {
        val esPlano  = cotas != null || cotasH != null
        val base     = crearBaseHoja(
            context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm, nZocalo,
            puenteCm = puenteCm, fondo = if (esPlano) Color.WHITE else Color.RED, escalaInterna = escalaInterna
        )
        val profPx   = crystal.crystal.taller.puerta.logica.CalculosPuerta.REJILLA_JENY * base.factor
        val nDiv     = maxOf(1, nDivisiones)
        val nc       = maxOf(1, rejillaCols)
        val nr       = maxOf(1, rejillaFilas)
        val innerAlto = base.contentBot - base.innerTop

        base.canvas.drawRect(RectF(base.innerLeft, base.innerTop, base.innerRight, base.contentBot), base.pInterior)

        val innerAncho = base.innerRight - base.innerLeft
        val panoAlto = (innerAlto - (nDiv - 1) * base.bPx) / nDiv

        fun pieza(r: RectF) {
            base.canvas.drawRect(r, base.pPanel); base.canvas.drawRect(r, base.pLinea)
        }

        for (i in 0 until nDiv) {
            val top = base.innerTop + i * (panoAlto + base.bPx)
            val bot = top + panoAlto

            if (variante == "Jeny r") {
                // Recuadro suspendido: proporción fija del paño, con seis amarres al bastidor.
                val anchoRec = innerAncho * crystal.crystal.taller.puerta.logica.CalculosPuerta.ANCHO_RECUADRO_JENY
                val altoRec = panoAlto * crystal.crystal.taller.puerta.logica.CalculosPuerta.ALTO_RECUADRO_JENY
                val xIni = base.innerLeft + (innerAncho - anchoRec) / 2f
                val yIni = top + (panoAlto - altoRec) / 2f
                val xFin = xIni + anchoRec
                val yFin = yIni + altoRec

                // Marco cortado a 45°: los cuatro lados enteros, encontrándose en las esquinas.
                pieza(RectF(xIni, yIni, xIni + profPx, yFin))
                pieza(RectF(xFin - profPx, yIni, xFin, yFin))
                pieza(RectF(xIni, yIni, xFin, yIni + profPx))
                pieza(RectF(xIni, yFin - profPx, xFin, yFin))

                // Amarres. Al lado se le restan los tubos y lo que queda se reparte en tramos
                // iguales: el de pie al medio del ancho, y los dos del costado en tercios del alto.
                val xEje = base.innerLeft + (innerAncho - profPx) / 2f
                pieza(RectF(xEje, top, xEje + profPx, yIni))
                pieza(RectF(xEje, yFin, xEje + profPx, bot))
                val tramo = (altoRec - 2f * profPx) / 3f
                val desdeLaEsquina = listOf(tramo, (2f * tramo) + profPx)   // hacia arriba
                desdeLaEsquina.forEach { d ->
                    val y = yFin - d - profPx
                    pieza(RectF(base.innerLeft, y, xIni, y + profPx))
                    pieza(RectF(xFin, y, base.innerRight, y + profPx))
                }

                // Cotas de la propia pieza: desde la esquina INFERIOR IZQUIERDA del recuadro hasta
                // el canto de cada tubo. El recuadro es una pieza suelta hasta que se monta, así que
                // sus marcas se miden sobre sí mismo; las reglas del margen miden desde la hoja.
                rotulos?.let { r ->
                    val ts = altoContenedor * 0.017f
                    // Las dos del costado van adentro y anidadas; la del ancho va por FUERA, bajo el
                    // recuadro, porque las tres arrancan en la misma esquina y adentro se pisaban.
                    listOf("amarreBajo", "amarreAlto").forEachIndexed { i, clave ->
                        r[clave]?.let {
                            val x = xIni + profPx + ts * (1.4f + i * 2.1f)
                            dibujarCotaPieza(base.canvas, it, x, yFin, x, yFin - desdeLaEsquina[i], ts)
                        }
                    }
                    r["amarreEje"]?.let {
                        val y = minOf(yFin + ts * 1.5f, bot - ts * 0.4f)
                        dibujarCotaPieza(base.canvas, it, xIni, y, xEje, y, ts)
                    }
                }
            } else {
                // Rejilla del paño: (columnas - 1) barras de pie enteras...
                val celdaAncho = (innerAncho - (nc - 1) * profPx) / nc
                for (c in 0 until nc - 1) {
                    val x = base.innerLeft + (c + 1) * celdaAncho + c * profPx
                    pieza(RectF(x, top, x + profPx, bot))
                }
                // ...y las acostadas dentro de cada columna. En "Jeny c" las pares llevan una menos,
                // y al repartirse solas quedan corridas entre las de las impares.
                for (c in 0 until nc) {
                    val esPar = (c + 1) % 2 == 0
                    val barras = if (variante == "Jeny c" && esPar) nr - 2 else nr - 1
                    if (barras <= 0) continue
                    val xIni = base.innerLeft + c * (celdaAncho + profPx)
                    val xFin = xIni + celdaAncho
                    val celdaAlto = (panoAlto - barras * profPx) / (barras + 1)
                    for (f in 0 until barras) {
                        val y = top + (f + 1) * celdaAlto + f * profPx
                        pieza(RectF(xIni, y, xFin, y + profPx))
                    }
                }
            }

            // Paflón divisor entre paños
            if (i < nDiv - 1) {
                pieza(RectF(base.innerLeft, bot, base.innerRight, bot + base.bPx))
            }
        }

        base.canvas.drawRect(base.hojaRect, base.pLinea)

        cotas?.let {
            dibujarCotasVerticales(base.canvas, it, altoContenedor, base.factor, base.hojaRect.bottom, marcoCm * base.factor)
        }
        cotasDer?.let {
            val anchoPuertaPx = anchoCm * base.factor
            dibujarCotasVerticalesDerecha(
                base.canvas, it, altoContenedor, base.factor, base.hojaRect.bottom,
                anchoPuertaPx, anchoPuertaPx - marcoCm * base.factor
            )
        }
        val tsH = altoContenedor * 0.018f
        val yDimH = altoCm * base.factor + tsH * 1.8f
        cotasH?.let {
            dibujarCotasHorizontales(base.canvas, it, altoContenedor, base.factor, base.innerLeft, yDimH, base.hojaRect.bottom)
        }
        // Segunda regla, medida desde el parante derecho, para las cotas que del lado izquierdo se
        // amontonarían contra el cero de la otra. Cada una queda sola en su mitad.
        cotasHDer?.let {
            dibujarCotasHorizontales(
                base.canvas, it, altoContenedor, base.factor, base.innerLeft, yDimH, base.hojaRect.bottom,
                desdeDerecha = true, xDer = base.innerRight
            )
        }

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
        nZocalo: Int = 1,
        puenteCm: Float = 2.5f,
        nInoxIzq: Int = 9,  // inox (divisores horizontales) de la columna izquierda
        inoxCm: Float = 2.5f, // grosor del tubo inox (zócalos pares + divisores izquierdos)
        etiquetas: Map<String, String>? = null, // plano: rótulos (cm) sobre cada pieza
        cotas: List<Float>? = null,   // plano: regla vertical (acumuladas desde la base)
        cotasH: List<Float>? = null,  // plano: regla horizontal (desde el borde interior izquierdo)
        escalaInterna: Float = 1f     // plano: deja margen para las reglas
    ): Bitmap {
        // Solo impar: el último zócalo siempre es paflon (8.25cm)
        val nZ   = nZocalo.let { if (it % 2 == 0) maxOf(1, it - 1) else it }
        val base        = crearBaseHoja(context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm, nZ, zocaloAlternado = true, puenteCm = puenteCm, inoxCm = inoxCm, fondo = if (etiquetas != null) Color.WHITE else Color.RED, escalaInterna = escalaInterna)
        val profPx      = inoxCm * base.factor
        val innerAncho  = base.innerRight - base.innerLeft
        val contentAlto = base.contentBot - base.innerTop

        val colW  = (innerAncho - 2f * base.bPx) / 3f
        val xDiv1 = base.innerLeft + colW
        val xCol2 = xDiv1 + base.bPx
        val xDiv2 = xCol2 + colW
        val xCol3 = xDiv2 + base.bPx

        // Fondo blanco solo hasta contentBot — los zócalos extra (contentBot→innerBot) quedan visibles
        base.canvas.drawRect(RectF(base.innerLeft, base.innerTop, base.innerRight, base.contentBot), base.pInterior)

        // Columnas centro + derecha: dos paflones a ±45°, puntas tocándose en (xCol2, yMid).
        // Se dibujan ANTES de los divisores verticales para que el VERTICAL corte al diagonal.
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

        // Divisores verticales (xDiv1, xDiv2) — sobre los diagonales → el vertical corta al diagonal
        val rDiv1 = RectF(xDiv1, base.innerTop, xDiv1 + base.bPx, base.contentBot)
        base.canvas.drawRect(rDiv1, base.pPanel); base.canvas.drawRect(rDiv1, base.pLinea)
        val rDiv2 = RectF(xDiv2, base.innerTop, xDiv2 + base.bPx, base.contentBot)
        base.canvas.drawRect(rDiv2, base.pPanel); base.canvas.drawRect(rDiv2, base.pLinea)

        // Columna izquierda (no cortada por el diagonal): nInoxIzq divisores de 2.5 cm (inox),
        // controlados por "divisiones". nSec secciones = nInoxIzq + 1.
        val nSec = nInoxIzq + 1
        val secH = (contentAlto - (nSec - 1) * profPx) / nSec
        for (i in 0 until nSec - 1) {
            val y = base.innerTop + (i + 1) * secH + i * profPx
            val r = RectF(base.innerLeft, y, xDiv1, y + profPx)
            base.canvas.drawRect(r, base.pPanel); base.canvas.drawRect(r, base.pLinea)
        }

        base.canvas.drawRect(base.hojaRect, base.pLinea)

        // Plano: rótulos de medidas (cm) sobre cada pieza. Los valores vienen ya calculados
        // (exactos) desde la Activity; aquí solo se posicionan en el centro de cada región.
        val et = etiquetas
        if (et != null) {
            val ts = altoContenedor * 0.016f
            val pTxt = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK; textSize = ts; textAlign = Paint.Align.CENTER
            }
            val pBg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.WHITE; alpha = 200 }
            // Rota el texto alrededor de su centro para pegarse a la pieza (vertical 90°, diagonal 45°).
            fun lab(texto: String, cx: Float, cy: Float, ang: Float) {
                val w = pTxt.measureText(texto)
                base.canvas.save()
                base.canvas.rotate(ang, cx, cy)
                base.canvas.drawRect(cx - w / 2 - 3f, cy - ts * 0.85f, cx + w / 2 + 3f, cy + ts * 0.25f, pBg)
                base.canvas.drawText(texto, cx, cy, pTxt)
                base.canvas.restore()
            }
            val cxL = (base.innerLeft + xDiv1) / 2f
            val cxC = (xCol2 + xDiv2) / 2f
            val cxR = (xCol3 + base.innerRight) / 2f
            val cxAll = (base.innerLeft + base.innerRight) / 2f
            // Cada arista vertical está sobre una línea: A=borde izq. del centro, B=divisor (cara izq.),
            // C=divisor (cara der.), D=bastidor. La diagonal cruza cada línea a una altura distinta.
            val xA = xCol2; val xB = xDiv2; val xC = xCol3; val xD = base.innerRight
            fun crossUp(xl: Float) = yMid - (xl - xCol2)   // diagonal que sube
            fun crossDn(xl: Float) = yMid + (xl - xCol2)   // diagonal que baja
            val off = ts * 0.8f                            // separa el rótulo hacia adentro del vacío
            fun midTop(xl: Float) = (base.innerTop + crossUp(xl)) / 2f
            fun midBot(xl: Float) = (crossDn(xl) + base.contentBot) / 2f
            val centros = mapOf(
                "gen" to (cxAll to base.innerTop - ts * 0.4f),
                "colL" to (cxL to base.innerTop + ts * 1.2f),
                "colC" to (cxC to base.innerTop + ts * 1.2f),
                "colR" to (cxR to base.innerTop + ts * 1.2f),
                // Tramos verticales superiores (pegados a su arista)
                "lA_t" to (xA + off to midTop(xA)),
                "lB_t" to (xB - off to midTop(xB)),
                "lC_t" to (xC + off to midTop(xC)),
                "lD_t" to (xD - off to midTop(xD)),
                // Tramos verticales inferiores (simétricos)
                "lA_b" to (xA + off to midBot(xA)),
                "lB_b" to (xB - off to midBot(xB)),
                "lC_b" to (xC + off to midBot(xC)),
                "lD_b" to (xD - off to midBot(xD)),
                // Tramos del medio (entre las dos diagonales)
                "lB_m" to (xB - off to yMid),
                "lC_m" to (xC + off to yMid),
                "lD_m" to (xD - off to yMid),
                "diag" to ((xCol2 + xDiv2) / 2f to yMid - colW * 0.5f),
                "secc" to (cxL to base.innerTop + secH / 2f),
                "inox" to (cxL to base.innerTop + secH + profPx / 2f),
                "parante" to (base.innerLeft - base.bPx / 2f to (base.innerTop + base.contentBot) / 2f),
                "div" to (xDiv1 + base.bPx / 2f to base.innerTop + (yMid - base.innerTop) * 0.5f),
                "zoc" to (cxAll to (base.contentBot + base.hojaRect.bottom) / 2f)
            )
            // Piezas verticales 90°, diagonal 45°, el resto horizontal.
            val vert = -90f
            val angulos = mapOf(
                "lA_t" to vert, "lB_t" to vert, "lC_t" to vert, "lD_t" to vert,
                "lA_b" to vert, "lB_b" to vert, "lC_b" to vert, "lD_b" to vert,
                "lB_m" to vert, "lC_m" to vert, "lD_m" to vert,
                "secc" to vert, "inox" to vert, "parante" to vert, "div" to vert,
                "diag" to -45f
            )
            et.forEach { (k, v) -> centros[k]?.let { (cx, cy) -> lab(v, cx, cy, angulos[k] ?: 0f) } }
        }

        cotas?.let {
            dibujarCotasVerticales(base.canvas, it, altoContenedor, base.factor, base.hojaRect.bottom, marcoCm * base.factor)
        }
        cotasH?.let {
            val ts = altoContenedor * 0.018f
            dibujarCotasHorizontales(base.canvas, it, altoContenedor, base.factor, base.innerLeft, altoCm * base.factor + ts * 1.8f, base.hojaRect.bottom)
        }

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
        nZocalo: Int = 1,
        puenteCm: Float = 2.5f
    ): Bitmap {
        val base    = crearBaseHoja(context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm, nZocalo, puenteCm = puenteCm)
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

    fun generarBitmapTere6(
        context: Context,
        anchoCm: Float,
        altoCm: Float,
        altoHojaCm: Float,
        anchoContenedor: Float,
        altoContenedor: Float,
        marcoCm: Float = 2.2f,
        pisoCm: Float = 0f,
        nZocalo: Int = 1,
        cantidadTubos: Int,
        puenteCm: Float = 2.5f,
        tuboCm: Float = 6f  // relleno del interior; editable desde el diálogo de variantes
    ): Bitmap {
        val base = crearBaseHoja(context, anchoCm, altoCm, altoHojaCm, anchoContenedor, altoContenedor, marcoCm, pisoCm, nZocalo, puenteCm = puenteCm)
        val tuboPx = tuboCm * base.factor
        val grumaPx = 0.5f * base.factor
        val marcoInternoPx = 2f * base.factor
        val pMarcoInterno = Paint(base.pPanel).apply {
            color = oscurecerColor(color, 0.86f)
        }

        val marcoInterno = RectF(base.innerLeft, base.innerTop, base.innerRight, base.contentBot)
        base.canvas.drawRect(marcoInterno, pMarcoInterno)
        base.canvas.drawRect(marcoInterno, base.pLinea)

        val areaTubos = RectF(
            base.innerLeft + marcoInternoPx,
            base.innerTop + marcoInternoPx,
            base.innerRight - marcoInternoPx,
            base.contentBot - marcoInternoPx
        )

        var y = areaTubos.top
        repeat(cantidadTubos.coerceAtLeast(1)) {
            val yBot = minOf(y + tuboPx, areaTubos.bottom)
            val r = RectF(areaTubos.left, y, areaTubos.right, yBot)
            base.canvas.drawRect(r, base.pPanel)
            base.canvas.drawRect(r, base.pLinea)
            y += tuboPx + grumaPx
        }

        base.canvas.drawRect(base.hojaRect, base.pLinea)
        base.canvas.restore()
        return base.bmp
    }

    private fun oscurecerColor(color: Int, factor: Float): Int {
        return Color.rgb(
            (Color.red(color) * factor).toInt().coerceIn(0, 255),
            (Color.green(color) * factor).toInt().coerceIn(0, 255),
            (Color.blue(color) * factor).toInt().coerceIn(0, 255)
        )
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

    const val ARCHIVO_PLANO = "plano_puerta.png"

    fun guardarPlanoEnCache(context: Context, bitmap: Bitmap) {
        try {
            val archivo = java.io.File(context.cacheDir, ARCHIVO_PLANO)
            java.io.FileOutputStream(archivo).use { out -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, out) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
