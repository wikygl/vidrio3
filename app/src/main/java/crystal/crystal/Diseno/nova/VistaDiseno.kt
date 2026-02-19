package crystal.crystal.Diseno.nova

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import crystal.crystal.R
import kotlin.math.max
import kotlin.math.min

private var omitirFondoAlExportar = false
enum class TipoModulo { FIJO, CORREDIZA }
enum class TipoFranja { MOCHETA, SISTEMA }
enum class ModoEnsamble { INA, APA }

data class FranjaNova(
    val tipo: TipoFranja,
    val modulos: List<TipoModulo>,
    val alturaCm: Float? = null,
    val parantePosiciones: List<Int> = emptyList()
)

class VistaDiseno @JvmOverloads constructor(
    contexto: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(contexto, attrs, defStyle) {

    // ------------ Parámetros globales (cm) ------------
    private var anchoCm: Float = 150f
    private var altoCm: Float  = 120f
    private var mochetaLateralCm: Float = 0f

    // ------------ Estado según paquete ------------
    private var modo: ModoEnsamble = ModoEnsamble.INA

    // APA: franjas reales (abajo→arriba)
    private var franjasAbajoArriba: List<FranjaNova> = emptyList()

    // INA: módulos de la franja S + alturas de mocheta local (cm) arriba/abajo
    private var sistemaModulos: List<TipoModulo> = listOf(TipoModulo.CORREDIZA)
    private var sistemaParantes: List<Int> = emptyList()
    private var alturaMochetaTopCm: Float = 0f
    private var alturaMochetaBottomCm: Float = 0f

    // ------------ Estética / grosores ------------
    private val margenPx = 55f   // margen para cotas
    private val anchoMarcoPx   = 7f   // contorno exterior
    private val anchoLineaPx   = 4f   // resto de líneas
    private val anchoReflejoPx = 1.5f // rayas de "reflejo"

    private val altoPuentePx = 12f    // banda en APA (junta m↔s)
    private val altoZocaloPx = 12f    // zócalo bajo cada ‘c’

    private val colorNegro = ContextCompat.getColor(context, android.R.color.black)

    private val pMarco = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorNegro; style = Paint.Style.STROKE; strokeWidth = anchoMarcoPx
    }
    private val pLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorNegro; style = Paint.Style.STROKE; strokeWidth = anchoLineaPx
    }
    private val pReflejo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorNegro; style = Paint.Style.STROKE; strokeWidth = anchoReflejoPx
    }
    private val pFondo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.blanco); style = Paint.Style.FILL
    }
    private val pRellenoNegro = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorNegro; style = Paint.Style.FILL
    }

    // --- Selección de franja por toque ---
    var alClicFranja: ((Int) -> Unit)? = null
    private var indiceFranjaResaltada: Int = -1
    private val rangosFranjaY = mutableListOf<Pair<Float, Float>>() // [top, bottom] por franja (abajo→arriba)
    private var ultimaVentanaX0 = 0f
    private var ultimaVentanaX1 = 0f
    private val pResaltaBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.azul)
        style = Paint.Style.STROKE
        strokeWidth = 5f
        alpha = 190
    }
    private val pResaltaRelleno = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.celeste)
        style = Paint.Style.FILL
        alpha = 50
    }
    private val pTextoCota = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorNegro
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }
    private val pLineaCota = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorNegro
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }

    fun resaltarFranja(indice: Int) {
        indiceFranjaResaltada = indice
        invalidate()
    }

    // ================== API PRINCIPAL ==================
    fun actualizarDesdePaquete(
        paquete: String,
        anchoCm: Float,
        altoCm: Float,
        mochetaLateralCm: Float = 0f
    ) {
        // 1) Medidas que llegan (pueden ser 0 si se tomarán del paquete)
        this.anchoCm = anchoCm
        this.altoCm = altoCm
        this.mochetaLateralCm = mochetaLateralCm

        // 2) Parseo de cabecera
        val (clase, tipo, modeloCrudo, dims) = parsearPaqueteConDimensiones(paquete)
        require(clase == "nova") { "Clase no soportada: $clase" }
        this.modo = if (tipo == "apa") ModoEnsamble.APA else ModoEnsamble.INA

        // Si el paquete trae [ancho,alto], respétalos
        dims?.let { (aw, ah) ->
            if (aw > 0f) this.anchoCm = aw
            if (ah > 0f) this.altoCm  = ah
        }

        // 3) Parseo del modelo (abajo → arriba)
        val franjas = parsearModeloConAlturas(modeloCrudo)

        // 4) Estado según modo
        if (modo == ModoEnsamble.APA) {
            // APA: se dibuja tal cual, las alturas AUTO se resolverán en el reparto
            franjasAbajoArriba = franjas
        } else {
            // INA: tomar módulos del sistema y sumar mocheta top/bottom.
            val idxS = franjas.indexOfFirst { it.tipo == TipoFranja.SISTEMA }
            require(idxS >= 0) { "Modelo INA requiere al menos una franja s(...)." }

            sistemaModulos = franjas[idxS].modulos
            sistemaParantes = franjas[idxS].parantePosiciones

            // Fallback para franjas con altura AUTO (sin <...>): mismo reparto que APA
            val alturasFallback = distribuirAlturas(franjas)

            alturaMochetaTopCm = 0f
            alturaMochetaBottomCm = 0f

            franjas.forEachIndexed { i, f ->
                if (f.tipo == TipoFranja.MOCHETA) {
                    val h = (f.alturaCm ?: alturasFallback[i]).coerceAtLeast(0f)
                    if (i > idxS) alturaMochetaTopCm += h
                    if (i < idxS) alturaMochetaBottomCm += h
                }
            }
        }

        // 5) Redibujar
        invalidate()
    }


    // ================== PARSEO ==================
    private data class Dimensiones(val ancho: Float, val alto: Float)
    private data class Quadruple<A,B,C,D>(val a: A, val b: B, val c: C, val d: D)

    private fun parsearPaqueteConDimensiones(texto: String): Quadruple<String,String,String,Dimensiones?> {
        val contenido = texto.trim().removePrefix("{").removeSuffix("}")
        val partes = contenido.split(",").map { it.trim() }
        require(partes.size >= 2) { "Paquete inválido: faltan clase/tipo" }
        val clase = partes[0].lowercase()
        val tipo  = partes[1].lowercase()
        val resto = contenido.substringAfter("$tipo,").trim()

        return if (resto.startsWith("[")) {
            val dentro = resto.removePrefix("[").substringBeforeLast("]")
            val idx = dentro.indexOf(":")
            require(idx > 0) { "Falta ':' después de [ancho,alto]" }
            val dimsTxt = dentro.substring(0, idx)
            val modeloTxt = dentro.substring(idx + 1)

            val (aw, ah) = dimsTxt.split(",").map { it.trim().replace(",", ".") }
            Quadruple(clase, tipo, modeloTxt, Dimensiones(aw.toFloat(), ah.toFloat()))
        } else {
            val modeloTxt = partes.subList(2, partes.size).joinToString(",")
            Quadruple(clase, tipo, modeloTxt, null)
        }
    }

    private fun parsearModeloConAlturas(modelo: String): List<FranjaNova> {
        val secciones = splitRespetandoParentesis(modelo.replace(" ", ""))
            .filter { it.isNotEmpty() }

        return secciones.map { frag ->
            val low = frag.lowercase()
            require(low.startsWith("s(") || low.startsWith("s<") || low.startsWith("m(") || low.startsWith("m<")) {
                "Franja inválida: $frag"
            }
            val tipo = if (low[0] == 's') TipoFranja.SISTEMA else TipoFranja.MOCHETA
            val altura: Float?
            val modTxt: String
            if (low[1] == '<') {
                val cmTxt = low.substringAfter("<").substringBefore(">").replace(",", ".")
                altura = cmTxt.toFloatOrNull()
                modTxt = low.substringAfter(">").substringAfter("(").substringBeforeLast(")")
            } else {
                altura = null
                modTxt = low.substringAfter("(").substringBeforeLast(")")
            }
            val (mods, parantes) = parsearModulosConParantes(modTxt)
            FranjaNova(tipo, mods, altura, parantes)
        }
    }

    private fun parsearModulosConParantes(texto: String): Pair<List<TipoModulo>, List<Int>> {
        val modulos = mutableListOf<TipoModulo>()
        val parantes = mutableListOf<Int>()
        val partes = texto.split(Regex(";?p;?", RegexOption.IGNORE_CASE))
        for ((idx, parte) in partes.withIndex()) {
            for (ch in parte.lowercase()) {
                when (ch) {
                    'f' -> modulos.add(TipoModulo.FIJO)
                    'c' -> modulos.add(TipoModulo.CORREDIZA)
                }
            }
            if (idx < partes.lastIndex) {
                parantes.add(modulos.size)
            }
        }
        if (modulos.isEmpty()) modulos.add(TipoModulo.FIJO)
        return Pair(modulos, parantes)
    }

    private fun splitRespetandoParentesis(texto: String): List<String> {
        val result = mutableListOf<String>()
        var depth = 0
        val current = StringBuilder()
        for (ch in texto) {
            when {
                ch == '(' -> { depth++; current.append(ch) }
                ch == ')' -> { depth--; current.append(ch) }
                ch == ';' && depth == 0 -> {
                    if (current.isNotEmpty()) result.add(current.toString())
                    current.clear()
                }
                else -> current.append(ch)
            }
        }
        if (current.isNotEmpty()) result.add(current.toString())
        return result
    }

    // ================= Export helpers =================
    fun exportarSoloDisenoBitmap(paddingPx: Int = 0): Bitmap {
        val anchoDisp = width - 2 * margenPx
        val altoDisp  = height - 2 * margenPx
        val anchoTotalCm = anchoCm + mochetaLateralCm
        val escala = min(anchoDisp / anchoTotalCm, altoDisp / altoCm)

        val x0 = (width  - (anchoTotalCm * escala)) / 2f
        val y0 = (height - (altoCm * escala)) / 2f
        val x1 = x0 + anchoTotalCm * escala
        val y1 = y0 + altoCm * escala

        val w = (x1 - x0 + 2 * paddingPx).toInt().coerceAtLeast(1)
        val h = (y1 - y0 + 2 * paddingPx).toInt().coerceAtLeast(1)

        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        omitirFondoAlExportar = true
        c.translate(-(x0 - paddingPx), -(y0 - paddingPx))
        draw(c) // reutiliza onDraw (no pinta fondo por el flag)
        omitirFondoAlExportar = false
        return bmp
    }

    // ================== DIBUJO ==================
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!omitirFondoAlExportar) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), pFondo)
        }
        if (anchoCm <= 0f || altoCm <= 0f) return

        val anchoDisp = width - 2 * margenPx
        val altoDisp  = height - 2 * margenPx
        val anchoTotalCm = anchoCm + mochetaLateralCm
        val escala = min(anchoDisp / anchoTotalCm, altoDisp / altoCm)

        val x0 = (width  - anchoTotalCm * escala) / 2f
        val y0 = (height - altoCm * escala) / 2f
        val x1 = x0 + anchoTotalCm * escala
        val y1 = y0 + altoCm * escala

        // Marco exterior (7f)
        canvas.drawRect(RectF(x0, y0, x1, y1), pMarco)

        // Mocheta lateral izquierda
        val xVentIni = x0 + mochetaLateralCm * escala
        if (mochetaLateralCm > 0f) canvas.drawRect(RectF(x0, y0, xVentIni, y1), pLinea)

        // Guardar límites horizontales para hit-test
        ultimaVentanaX0 = xVentIni
        ultimaVentanaX1 = x1

        // Limpiar rangos de franjas para este frame
        rangosFranjaY.clear()

        val anchoVentPx = x1 - xVentIni
        if (modo == ModoEnsamble.APA) {
            dibujarAPA(canvas, xVentIni, x1, y1, anchoVentPx, escala)
        } else {
            dibujarINA(canvas, xVentIni, y0, x1, y1, anchoVentPx, escala)
        }

        // Dibujar cotas exteriores (solo si no es exportación)
        if (!omitirFondoAlExportar) {
            dibujarCotas(canvas, x0, y0, x1, y1, escala)
        }
    }

    // ---- APA: respeta alturas por franja, puentes, zócalos y REFLEJO en cada vidrio ----
    private fun dibujarAPA(
        canvas: Canvas,
        xIni: Float, xFin: Float,
        yBotTotal: Float, anchoVentPx: Float,
        escalaPxPorCm: Float
    ) {
        if (franjasAbajoArriba.isEmpty()) return

        val alturasCm = distribuirAlturas(franjasAbajoArriba)
        var yAbajo = yBotTotal
        val parantesX = mutableListOf<Float>()

        franjasAbajoArriba.forEachIndexed { idx, franja ->
            val altoFpx = alturasCm[idx] * escalaPxPorCm
            val yArriba = yAbajo - altoFpx
            val yTop = yArriba
            val yBottom = yAbajo

            // Registrar rango (abajo→arriba, almacenamos [top,bottom])
            rangosFranjaY.add(Pair(yTop, yBottom))

            // Puentes M↔S (debajo)
            var hayMAbajo = false
            var hayMArriba = false
            if (franja.tipo == TipoFranja.SISTEMA) {
                if (idx > 0 && franjasAbajoArriba[idx - 1].tipo == TipoFranja.MOCHETA) {
                    hayMAbajo = true
                    canvas.drawRect(RectF(xIni, yBottom - altoPuentePx, xFin, yBottom), pRellenoNegro)
                }
                if (idx < franjasAbajoArriba.lastIndex && franjasAbajoArriba[idx + 1].tipo == TipoFranja.MOCHETA) {
                    hayMArriba = true
                    canvas.drawRect(RectF(xIni, yTop, xFin, yTop + altoPuentePx), pRellenoNegro)
                }
            }

            // Contornos y separadores
            canvas.drawLine(xIni, yTop,    xFin, yTop,    pLinea)
            canvas.drawLine(xIni, yBottom, xFin, yBottom, pLinea)

            val n = franja.modulos.size
            val anchoModulo = anchoVentPx / n
            val parantesSet = franja.parantePosiciones.toSet()
            for (i in 1 until n) {
                if (i in parantesSet) continue
                val xSep = xIni + i * anchoModulo
                canvas.drawLine(xSep, yTop, xSep, yBottom, pLinea)
            }
            canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pLinea)

            // Guardar posiciones de parantes (desde la franja sistema)
            if (franja.parantePosiciones.isNotEmpty()) {
                for (pos in franja.parantePosiciones) {
                    if (pos in 1 until n) {
                        parantesX.add(xIni + pos * anchoModulo)
                    }
                }
            }

            // Zócalos + Reflejo
            when (franja.tipo) {
                TipoFranja.SISTEMA -> {
                    val yZBotGlobal = if (hayMAbajo) (yBottom - altoPuentePx) else yBottom
                    val yTGlass     = if (hayMArriba) (yTop + altoPuentePx) else yTop
                    for (i in 0 until n) {
                        val xM0 = xIni + i * anchoModulo
                        val xM1 = xIni + (i + 1) * anchoModulo
                        if (franja.modulos[i] == TipoModulo.CORREDIZA) {
                            val yZTop = yZBotGlobal - altoZocaloPx
                            canvas.drawRect(RectF(xM0, yZTop, xM1, yZBotGlobal), pRellenoNegro)
                            canvas.drawLine(xM0, yZTop, xM1, yZTop, pLinea)
                            dibujarReflejoVidrio(canvas, xM0, yTGlass, xM1, yZTop)
                        } else {
                            dibujarReflejoVidrio(canvas, xM0, yTGlass, xM1, yZBotGlobal)
                        }
                    }
                }
                TipoFranja.MOCHETA -> {
                    for (i in 0 until n) {
                        val xM0 = xIni + i * anchoModulo
                        val xM1 = xIni + (i + 1) * anchoModulo
                        if (franja.modulos[i] == TipoModulo.CORREDIZA) {
                            val yZTop = yBottom - altoZocaloPx
                            canvas.drawRect(RectF(xM0, yZTop, xM1, yBottom), pRellenoNegro)
                            canvas.drawLine(xM0, yZTop, xM1, yZTop, pLinea)
                            dibujarReflejoVidrio(canvas, xM0, yTop, xM1, yZTop)
                        } else {
                            dibujarReflejoVidrio(canvas, xM0, yTop, xM1, yBottom)
                        }
                    }
                }
            }

            // Resaltado de la franja activa
            if (idx == indiceFranjaResaltada) {
                canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pResaltaRelleno)
                canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pResaltaBorde)
            }

            yAbajo = yArriba
        }

        // Dibujar parantes a toda la altura (rectángulo de 2.5 cm, mínimo 10px)
        if (parantesX.isNotEmpty()) {
            val yTopVentana = yBotTotal - altoCm * escalaPxPorCm
            val anchoParante = max(10f, 2.5f * escalaPxPorCm)
            for (x in parantesX) {
                canvas.drawRect(RectF(x - anchoParante / 2, yTopVentana, x + anchoParante / 2, yBotTotal), pRellenoNegro)
            }
        }
    }

    // Reparte alturas para cualquier lista de franjas (S y M)
// Replica la lógica que tenías en distribuirAlturasAPA()
    private fun distribuirAlturas(franjas: List<FranjaNova>): MutableList<Float> {
        val n = franjas.size
        val alturas = MutableList(n) { 0f }
        if (n == 0) return alturas

        var sumaExp = 0f
        var hayExp = false
        for (i in 0 until n) {
            val h = franjas[i].alturaCm
            if (h != null && h > 0f) {
                alturas[i] = h
                sumaExp += h
                hayExp = true
            }
        }

        val idxS = (0 until n).filter { franjas[it].tipo == TipoFranja.SISTEMA }
        val idxM = (0 until n).filter { franjas[it].tipo == TipoFranja.MOCHETA }

        // Sin alturas explícitas → 5/7 para S y 2/7 para M (o uniforme si solo hay un tipo)
        if (!hayExp) {
            if (idxS.isEmpty() || idxM.isEmpty()) {
                val cuota = altoCm / n
                for (i in 0 until n) alturas[i] = cuota
            } else {
                val totalS = altoCm * (5f / 7f)
                val totalM = altoCm - totalS
                val cuotaS = if (idxS.isNotEmpty()) totalS / idxS.size else 0f
                val cuotaM = if (idxM.isNotEmpty()) totalM / idxM.size else 0f
                idxS.forEach { alturas[it] = cuotaS }
                idxM.forEach { alturas[it] = cuotaM }
            }
            return alturas
        }

        // Suma de explícitas excede → escalar proporcionalmente
        val toler = 0.5f
        if (sumaExp > altoCm + toler) {
            val k = altoCm / sumaExp
            for (i in 0 until n) if (alturas[i] > 0f) alturas[i] *= k
            return alturas
        }

        // Repartir resto entre franjas sin altura explícita
        val resto = (altoCm - sumaExp).coerceAtLeast(0f)
        val ceros = (0 until n).filter { alturas[it] == 0f }
        val cuota = if (ceros.isNotEmpty()) resto / ceros.size else 0f
        ceros.forEach { alturas[it] = cuota }
        return alturas
    }

    // ---- INA: mocheta local (arriba/abajo) con tramos de C continuos; reflejo y zócalo ----
    private fun dibujarINA(
        canvas: Canvas,
        xIni: Float, yTopTotal: Float,
        xFin: Float, yBotTotal: Float,
        anchoVentPx: Float, escalaPxPorCm: Float
    ) {
        if (sistemaModulos.isEmpty()) return

        val insetMarco = anchoMarcoPx * 0.5f
        val yTop = yTopTotal + insetMarco
        val yBottom = yBotTotal - insetMarco

        // Registrar único rango de “franja” (el sistema)
        rangosFranjaY.add(Pair(yTop, yBottom))

        // Contenedor del sistema
        canvas.drawLine(xIni, yTop,    xFin, yTop,    pLinea)
        canvas.drawLine(xIni, yBottom, xFin, yBottom, pLinea)
        canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pLinea)

        val n = sistemaModulos.size
        if (n <= 0) return
        val anchoModulo = anchoVentPx / n
        val xs = FloatArray(n + 1) { i -> xIni + i * anchoModulo }

        val mTopPx    = max(0f, alturaMochetaTopCm)    * escalaPxPorCm
        val mBottomPx = max(0f, alturaMochetaBottomCm) * escalaPxPorCm
        val yPanelTop    = yTop + mTopPx
        val yPanelBottom = yBottom - mBottomPx

        // Separadores que no cruzan mochetas si hay C a ambos lados
        // En INA, parantes se dibujan como líneas (no como rectángulos)
        for (i in 1 until n) {
            val izqC = (sistemaModulos[i - 1] == TipoModulo.CORREDIZA)
            val derC = (sistemaModulos[i]     == TipoModulo.CORREDIZA)
            val yA = if (izqC && derC && mTopPx > 0f) yPanelTop else yTop
            val yB = if (izqC && derC && mBottomPx > 0f) yPanelBottom else yBottom
            canvas.drawLine(xs[i], yA, xs[i], yB, pLinea)
        }

        // Parantes en INA: solo línea (va por detrás del vidrio)
        // No se dibujan como rectángulo, el usuario intuye que va un parante

        // Tramos contiguos de C para mochetas continuas
        fun tramosC(): List<IntRange> {
            val res = mutableListOf<IntRange>()
            var i = 0
            while (i < n) {
                if (sistemaModulos[i] == TipoModulo.CORREDIZA) {
                    val ini = i
                    var j = i + 1
                    while (j < n && sistemaModulos[j] == TipoModulo.CORREDIZA) j++
                    res.add(ini..(j - 1))
                    i = j
                } else i++
            }
            return res
        }
        val tramos = tramosC()

        if (mTopPx > 0f) {
            for (r in tramos) {
                val x0 = xs[r.first]
                val x1 = xs[r.last + 1]
                canvas.drawLine(x0, yTop,      x1, yTop,      pLinea)
                canvas.drawLine(x0, yPanelTop, x1, yPanelTop, pLinea)
                canvas.drawLine(x0, yTop,      x0, yPanelTop, pLinea)
                canvas.drawLine(x1, yTop,      x1, yPanelTop, pLinea)
            }
        }
        if (mBottomPx > 0f) {
            for (r in tramos) {
                val x0 = xs[r.first]
                val x1 = xs[r.last + 1]
                canvas.drawLine(x0, yPanelBottom, x1, yPanelBottom, pLinea)
                canvas.drawLine(x0, yBottom,      x1, yBottom,      pLinea)
                canvas.drawLine(x0, yPanelBottom, x0, yBottom,      pLinea)
                canvas.drawLine(x1, yPanelBottom, x1, yBottom,      pLinea)
            }
        }

        // Zócalos + reflejos por módulo
        for (i in 0 until n) {
            val x0 = xs[i]
            val x1 = xs[i + 1]
            if (sistemaModulos[i] == TipoModulo.CORREDIZA) {
                val yZTop = (yPanelBottom - altoZocaloPx).coerceAtMost(yPanelBottom)
                canvas.drawRect(RectF(x0, yZTop, x1, yPanelBottom), pRellenoNegro)
                canvas.drawLine(x0, yZTop, x1, yZTop, pLinea)
                dibujarReflejoVidrio(canvas, x0, yPanelTop, x1, yZTop)
            } else {
                dibujarReflejoVidrio(canvas, x0, yTop, x1, yBottom)
            }
        }

        // Resaltado (si el índice 0 está activo)
        if (indiceFranjaResaltada == 0) {
            canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pResaltaRelleno)
            canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pResaltaBorde)
        }
    }

    // Reflejo: tres líneas cortas, paralelas y centradas
    private fun dibujarReflejoVidrio(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        val w = right - left
        val h = bottom - top
        val s = min(w, h)
        if (s < 12f) return

        val cx = (left + right) / 2f
        val cy = (top + bottom) / 2f

        val lenMax = s * 0.35f
        val lengths = floatArrayOf(
            lenMax * 0.4f,  // corto
            lenMax * 0.7f,  // medio
            lenMax * 1.2f   // largo
        )
        val gap = s * 0.06f

        // Dirección "/" y su perpendicular
        val dx = 0.7f; val dy = -1f
        val nx = 0.7f; val ny =  0.3f

        for (i in 0..2) {
            val offset = (i - 1) * gap
            val cx_i = cx + nx * offset
            val cy_i = cy + ny * offset
            val half = lengths[i] / 2f
            val x0 = cx_i - dx * half
            val y0 = cy_i - dy * half
            val x1 = cx_i + dx * half
            val y1 = cy_i + dy * half
            canvas.drawLine(x0, y0, x1, y1, pReflejo)
        }
    }

    // ================= COTAS =================
    private fun dibujarCotas(canvas: Canvas, x0: Float, y0: Float, x1: Float, y1: Float, escala: Float) {
        val offsetH = 40f // separación horizontal
        val offsetV = 35f // separación vertical
        val flechaLen = 12f // longitud de las flechas
        val espacioTexto = 50f // espacio necesario para el texto rotado

        // Formato de número
        fun fmt(v: Float) = if (v % 1 == 0f) v.toInt().toString() else "%.1f".format(v).replace(",", ".")

        // Cota horizontal (ancho) - abajo
        val yLineaH = y1 + offsetH
        val xCentroH = (x0 + x1) / 2
        val textoAncho = fmt(anchoCm + mochetaLateralCm)
        val anchoTexto = pTextoCota.measureText(textoAncho)
        // Línea interrumpida por el texto
        canvas.drawLine(x0, yLineaH, xCentroH - anchoTexto/2 - 5, yLineaH, pLineaCota)
        canvas.drawLine(xCentroH + anchoTexto/2 + 5, yLineaH, x1, yLineaH, pLineaCota)
        // Flechas
        canvas.drawLine(x0, yLineaH, x0 + flechaLen, yLineaH - flechaLen/2, pLineaCota)
        canvas.drawLine(x0, yLineaH, x0 + flechaLen, yLineaH + flechaLen/2, pLineaCota)
        canvas.drawLine(x1, yLineaH, x1 - flechaLen, yLineaH - flechaLen/2, pLineaCota)
        canvas.drawLine(x1, yLineaH, x1 - flechaLen, yLineaH + flechaLen/2, pLineaCota)
        // Texto centrado en la línea
        canvas.drawText(textoAncho, xCentroH, yLineaH + 8f, pTextoCota)

        // Cota vertical (alto) - SIEMPRE fuera del diseño
        val espacioDisponibleDer = width - x1 - margenPx
        val espacioDisponibleIzq = x0 - margenPx
        val xLineaV = if (espacioDisponibleDer >= espacioDisponibleIzq) {
            // Preferir derecha si hay más o igual espacio
            x1 + offsetV
        } else {
            // Poner a la izquierda
            x0 - offsetV
        }

        val yCentroV = (y0 + y1) / 2
        val textoAlto = fmt(altoCm)
        val anchoTextoAlto = pTextoCota.measureText(textoAlto)
        // Línea interrumpida por el texto
        canvas.drawLine(xLineaV, y0, xLineaV, yCentroV - anchoTextoAlto/2 - 5, pLineaCota)
        canvas.drawLine(xLineaV, yCentroV + anchoTextoAlto/2 + 5, xLineaV, y1, pLineaCota)
        // Flechas
        canvas.drawLine(xLineaV, y0, xLineaV - flechaLen/2, y0 + flechaLen, pLineaCota)
        canvas.drawLine(xLineaV, y0, xLineaV + flechaLen/2, y0 + flechaLen, pLineaCota)
        canvas.drawLine(xLineaV, y1, xLineaV - flechaLen/2, y1 - flechaLen, pLineaCota)
        canvas.drawLine(xLineaV, y1, xLineaV + flechaLen/2, y1 - flechaLen, pLineaCota)
        // Texto rotado, centrado en la línea
        canvas.save()
        canvas.rotate(-90f, xLineaV, yCentroV)
        canvas.drawText(textoAlto, xLineaV, yCentroV + 8f, pTextoCota)
        canvas.restore()
    }

    // ================= SVG: exporta solo el diseño (recortado) =================
    fun exportarSoloDisenoSVG(paddingPx: Int = 0): String {
        val anchoDisp = width - 2 * margenPx
        val altoDisp  = height - 2 * margenPx
        val anchoTotalCm = anchoCm + mochetaLateralCm
        val escala = min(anchoDisp / anchoTotalCm, altoDisp / altoCm)

        val x0 = (width  - (anchoTotalCm * escala)) / 2f
        val y0 = (height - (altoCm * escala)) / 2f
        val x1 = x0 + anchoTotalCm * escala
        val y1 = y0 + altoCm * escala

        val W = (x1 - x0 + 2 * paddingPx).coerceAtLeast(2f)
        val H = (y1 - y0 + 2 * paddingPx).coerceAtLeast(2f)

        // Offset para llevar coords de la vista al SVG recortado
        val offX = -x0 + paddingPx
        val offY = -y0 + paddingPx

        fun ox(x: Float) = x + offX
        fun oy(y: Float) = y + offY

        val sb = StringBuilder()
        sb.append("""<svg xmlns="http://www.w3.org/2000/svg" width="$W" height="$H" viewBox="0 0 $W $H" shape-rendering="crispEdges">""")

        fun line(xa: Float, ya: Float, xb: Float, yb: Float, w: Float) {
            sb.append("""<line x1="${ox(xa)}" y1="${oy(ya)}" x2="${ox(xb)}" y2="${oy(yb)}" stroke="#000" stroke-width="$w"/>""")
        }
        fun rectStroke(l: Float, t: Float, r: Float, b: Float, w: Float) {
            sb.append("""<rect x="${ox(l)}" y="${oy(t)}" width="${(r - l)}" height="${(b - t)}" fill="none" stroke="#000" stroke-width="$w"/>""")
        }
        fun rectFill(l: Float, t: Float, r: Float, b: Float) {
            sb.append("""<rect x="${ox(l)}" y="${oy(t)}" width="${(r - l)}" height="${(b - t)}" fill="#000"/>""")
        }

        // Marco exterior
        rectStroke(x0, y0, x1, y1, 7f)

        // Mocheta lateral izquierda (solo contorno)
        val xVentIni = x0 + mochetaLateralCm * escala
        val xVentFin = x1
        if (mochetaLateralCm > 0f) rectStroke(x0, y0, xVentIni, y1, 3f)
        val anchoVentPx = xVentFin - xVentIni

          if (modo == ModoEnsamble.APA) {
              val alturasCm = distribuirAlturas(franjasAbajoArriba)
              var yAbajo = y1
              val parantesXsvg = mutableListOf<Float>()
              franjasAbajoArriba.forEachIndexed { idx, franja ->
                val altoFpx = alturasCm[idx] * escala
                val yArriba = yAbajo - altoFpx
                val yTop = yArriba
                val yBottom = yAbajo

                var hayMAbajo = false
                var hayMArriba = false
                if (franja.tipo == TipoFranja.SISTEMA) {
                    if (idx > 0 && franjasAbajoArriba[idx - 1].tipo == TipoFranja.MOCHETA) {
                        hayMAbajo = true
                        rectFill(xVentIni, yBottom - altoPuentePx, xVentFin, yBottom)
                    }
                    if (idx < franjasAbajoArriba.lastIndex && franjasAbajoArriba[idx + 1].tipo == TipoFranja.MOCHETA) {
                        hayMArriba = true
                        rectFill(xVentIni, yTop, xVentFin, yTop + altoPuentePx)
                    }
                }

                line(xVentIni, yTop,    xVentFin, yTop,    3f)
                line(xVentIni, yBottom, xVentFin, yBottom, 3f)
                  val n = franja.modulos.size
                  val anchoModulo = anchoVentPx / n
                  val parantesSet = franja.parantePosiciones.toSet()
                  for (i in 1 until n) {
                      if (i in parantesSet) continue
                      val xSep = xVentIni + i * anchoModulo
                      line(xSep, yTop, xSep, yBottom, 3f)
                  }
                  rectStroke(xVentIni, yTop, xVentFin, yBottom, 3f)

                  // Guardar posiciones de parantes (desde la franja sistema)
                  if (franja.parantePosiciones.isNotEmpty()) {
                      for (pos in franja.parantePosiciones) {
                          if (pos in 1 until n) {
                              parantesXsvg.add(xVentIni + pos * anchoModulo)
                          }
                      }
                  }

                when (franja.tipo) {
                    TipoFranja.SISTEMA -> {
                        val yZBotGlobal = if (hayMAbajo) (yBottom - altoPuentePx) else yBottom
                        val yTopGlass   = if (hayMArriba) (yTop + altoPuentePx) else yTop
                        for (i in 0 until n) {
                            val x0m = xVentIni + i * anchoModulo
                            val x1m = xVentIni + (i + 1) * anchoModulo
                            if (franja.modulos[i] == TipoModulo.CORREDIZA) {
                                val yZTop = yZBotGlobal - altoZocaloPx
                                rectFill(x0m, yZTop, x1m, yZBotGlobal)
                                line(x0m, yZTop, x1m, yZTop, 3f)
                                reflejoSVG(sb, x0m, yTopGlass, x1m, yZTop, offX, offY)
                            } else {
                                reflejoSVG(sb, x0m, yTopGlass, x1m, yZBotGlobal, offX, offY)
                            }
                        }
                    }
                    TipoFranja.MOCHETA -> {
                        for (i in 0 until n) {
                            val x0m = xVentIni + i * anchoModulo
                            val x1m = xVentIni + (i + 1) * anchoModulo
                            if (franja.modulos[i] == TipoModulo.CORREDIZA) {
                                val yZTop = yBottom - altoZocaloPx
                                rectFill(x0m, yZTop, x1m, yBottom)
                                line(x0m, yZTop, x1m, yZTop, 3f)
                                reflejoSVG(sb, x0m, yTop, x1m, yZTop, offX, offY)
                            } else {
                                reflejoSVG(sb, x0m, yTop, x1m, yBottom, offX, offY)
                            }
                        }
                    }
                }
                  yAbajo = yArriba
              }

              // Parantes SVG a toda la altura (rectángulo de 2.5 cm, mínimo 10px)
              if (parantesXsvg.isNotEmpty()) {
                  val anchoParante = max(10f, 2.5f * escala)
                  for (xP in parantesXsvg) {
                      rectFill(xP - anchoParante / 2, y0, xP + anchoParante / 2, y1)
                  }
              }
          } else {
              val yTop = y0 + 0.5f * 7f
              val yBottom = y1 - 0.5f * 7f

            line(xVentIni, yTop,    xVentFin, yTop,    3f)
            line(xVentIni, yBottom, xVentFin, yBottom, 3f)
            rectStroke(xVentIni, yTop, xVentFin, yBottom, 3f)

              val n = sistemaModulos.size
              if (n > 0) {
                  val anchoModulo = anchoVentPx / n
                  val xs = FloatArray(n + 1) { i -> xVentIni + i * anchoModulo }

                  // Parantes en INA: solo línea (va por detrás del vidrio)
                  // No se dibujan como rectángulo en el SVG

                val mTopPx    = max(0f, alturaMochetaTopCm)    * escala
                val mBottomPx = max(0f, alturaMochetaBottomCm) * escala
                val yPanelTop    = yTop + mTopPx
                val yPanelBottom = yBottom - mBottomPx

                  // En INA, parantes se dibujan como líneas (no como rectángulos)
                  for (i in 1 until n) {
                      val izqC = (sistemaModulos[i - 1] == TipoModulo.CORREDIZA)
                      val derC = (sistemaModulos[i]     == TipoModulo.CORREDIZA)
                      val yA = if (izqC && derC && mTopPx > 0f) yPanelTop else yTop
                      val yB = if (izqC && derC && mBottomPx > 0f) yPanelBottom else yBottom
                      line(xs[i], yA, xs[i], yB, 3f)
                  }

                fun tramosC(): List<IntRange> {
                    val res = mutableListOf<IntRange>()
                    var i = 0
                    while (i < n) {
                        if (sistemaModulos[i] == TipoModulo.CORREDIZA) {
                            val ini = i
                            var j = i + 1
                            while (j < n && sistemaModulos[j] == TipoModulo.CORREDIZA) j++
                            res.add(ini..(j - 1))
                            i = j
                        } else i++
                    }
                    return res
                }
                val tramos = tramosC()

                if (mTopPx > 0f) {
                    for (r in tramos) {
                        val x0m = xs[r.first]
                        val x1m = xs[r.last + 1]
                        line(x0m, yTop,      x1m, yTop,      3f)
                        line(x0m, yPanelTop, x1m, yPanelTop, 3f)
                        line(x0m, yTop,      x0m, yPanelTop, 3f)
                        line(x1m, yTop,      x1m, yPanelTop, 3f)
                    }
                }
                if (mBottomPx > 0f) {
                    for (r in tramos) {
                        val x0m = xs[r.first]
                        val x1m = xs[r.last + 1]
                        line(x0m, yPanelBottom, x1m, yPanelBottom, 3f)
                        line(x0m, yBottom,      x1m, yBottom,      3f)
                        line(x0m, yPanelBottom, x0m, yBottom,      3f)
                        line(x1m, yPanelBottom, x1m, yBottom,      3f)
                    }
                }

                for (i in 0 until n) {
                    val x0m = xs[i]
                    val x1m = xs[i + 1]
                    if (sistemaModulos[i] == TipoModulo.CORREDIZA) {
                        val yZTop = (yPanelBottom - altoZocaloPx).coerceAtMost(yPanelBottom)
                        rectFill(x0m, yZTop, x1m, yPanelBottom)
                        line(x0m, yZTop, x1m, yZTop, 3f)
                        reflejoSVG(sb, x0m, yPanelTop, x1m, yZTop, offX, offY)
                    } else {
                        reflejoSVG(sb, x0m, yTop, x1m, yBottom, offX, offY)
                    }
                }
            }
        }

        sb.append("</svg>")
        return sb.toString()
    }

    // ================= SVG: rayitas de reflejo (idénticas al canvas) =================
    private fun reflejoSVG(
        sb: StringBuilder,
        left: Float, top: Float, right: Float, bottom: Float,
        offX: Float, offY: Float
    ) {
        val w = right - left
        val h = bottom - top
        val s = min(w, h)
        if (s < 12f) return

        val cx = (left + right) / 2f
        val cy = (top + bottom) / 2f

        val lenMax = s * 0.35f
        val lengths = floatArrayOf(
            lenMax * 0.4f,  // corto
            lenMax * 0.7f,  // medio
            lenMax * 1.2f   // largo
        )
        val gap = s * 0.06f

        val dx = 0.7f; val dy = -1f   // dirección “/”
        val nx = 0.7f; val ny =  0.3f // perpendicular para separar

        fun L(x0: Float, y0: Float, x1: Float, y1: Float) {
            sb.append("""<line x1="${x0 + offX}" y1="${y0 + offY}" x2="${x1 + offX}" y2="${y1 + offY}" stroke="#000" stroke-width="1"/>""")
        }

        for (i in 0..2) {
            val offset = (i - 1) * gap
            val cxI = cx + nx * offset
            val cyI = cy + ny * offset
            val half = lengths[i] / 2f
            val x0 = cxI - dx * half
            val y0 = cyI - dy * half
            val x1 = cxI + dx * half
            val y1 = cyI + dy * half
            L(x0, y0, x1, y1)
        }
    }

    // ================== Toque / selección ==================
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            val x = event.x
            val y = event.y
            if (x >= ultimaVentanaX0 && x <= ultimaVentanaX1) {
                for (i in rangosFranjaY.indices) {
                    val (top, bottom) = rangosFranjaY[i]
                    if (y >= top && y <= bottom) {
                        indiceFranjaResaltada = i
                        invalidate()
                        alClicFranja?.invoke(i)
                        return true
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }
}
