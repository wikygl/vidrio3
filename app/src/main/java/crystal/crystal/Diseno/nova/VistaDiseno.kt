package crystal.crystal.Diseno.nova

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import crystal.crystal.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

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
enum class TipoSegmentoNs { PLANO, ALETA }
data class SegmentoNs(
    val tipo: TipoSegmentoNs,
    val anchoCm: Float,
    val franjas: List<FranjaNova>
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
    private var mochetaLateralDerechaCm: Float = 0f
    private var corteVerticalCm: Float? = null

    // ------------ Estado según paquete ------------
    private var modo: ModoEnsamble = ModoEnsamble.INA

    // APA: franjas reales (abajo→arriba)
    private var franjasAbajoArriba: List<FranjaNova> = emptyList()

    // INA: módulos de la franja S + alturas de mocheta local (cm) arriba/abajo
    private var sistemaModulos: List<TipoModulo> = listOf(TipoModulo.CORREDIZA)
    private var sistemaParantes: List<Int> = emptyList()
    private var alturaMochetaTopCm: Float = 0f
    private var alturaMochetaBottomCm: Float = 0f
    private var esquinaLConParante: Boolean = false
    private var esquinaRConParante: Boolean = false
    private var mochetaLDesdeModeloCm: Float = 0f
    private var mochetaRDesdeModeloCm: Float = 0f
    private var mochetaLModulos: List<TipoModulo> = emptyList()
    private var mochetaLParantes: List<Int> = emptyList()
    private var mochetaLFranjas: List<FranjaNova> = emptyList()
    private var mochetaRModulos: List<TipoModulo> = emptyList()
    private var mochetaRParantes: List<Int> = emptyList()
    private var mochetaRFranjas: List<FranjaNova> = emptyList()
    private var segmentosNs: List<SegmentoNs> = emptyList()
    private var modoArcoCurvo: Boolean = false
    private var flechaArcoCm: Float = 0f
    private var modoCircular: Boolean = false
    private enum class LadoAleta { IZQ, DER }
    private data class AletaPerspectiva(
        val bordeUnionTop: PointF,
        val bordeUnionBottom: PointF,
        val bordeExteriorTop: PointF,
        val bordeExteriorBottom: PointF
    )

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
    private val pAletaSombra2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C5D6EE")
        style = Paint.Style.FILL
        alpha = 210
    }

    // --- Selección de franja por toque ---
    var alClicFranja: ((Int) -> Unit)? = null
    var alClicFranjaTramo: ((Int, Int) -> Unit)? = null // (tramo, franja)
    var alDobleClicModulo: ((franja: Int, modulo: Int) -> Unit)? = null
    private var indiceFranjaResaltada: Int = -1
    private var indiceTramoResaltado: Int = -1
    private var indiceModuloResaltado: Int = -1
    private val rangosFranjaY = mutableListOf<Pair<Float, Float>>() // [top, bottom] por franja (abajo→arriba)
    private val rangosTramoX = mutableListOf<Pair<Float, Float>>() // [left, right] por tramo (izq→der)
    private var ultimaVentanaX0 = 0f
    private var ultimaVentanaX1 = 0f
    private var franjaConfirmadaPorToque: Int = -1
    private var tramoConfirmadoPorToque: Int = -1
    private var franjaDownIndex: Int = -1
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
    private val pResaltaModuloBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.azul)
        style = Paint.Style.STROKE
        strokeWidth = 5f
        alpha = 230
    }
    private val pResaltaModuloRelleno = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.celeste)
        style = Paint.Style.FILL
        alpha = 120
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

    fun resaltarFranja(indice: Int, tramo: Int = -1) {
        indiceFranjaResaltada = indice
        indiceTramoResaltado = tramo
        invalidate()
    }

    fun resaltarModulo(franja: Int, modulo: Int) {
        indiceFranjaResaltada = franja
        indiceModuloResaltado = modulo
        invalidate()
    }

    fun actualizarCorteVertical(corteCm: Float?) {
        corteVerticalCm = corteCm?.coerceAtLeast(0f)
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
        this.mochetaLateralCm = mochetaLateralCm.coerceAtLeast(0f)
        this.mochetaLateralDerechaCm = 0f

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
        if (this.mochetaLateralCm <= 0f && mochetaLDesdeModeloCm > 0f) {
            this.mochetaLateralCm = mochetaLDesdeModeloCm
        }
        if (this.mochetaLateralDerechaCm <= 0f && mochetaRDesdeModeloCm > 0f) {
            this.mochetaLateralDerechaCm = mochetaRDesdeModeloCm
        }

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
        esquinaLConParante = false
        esquinaRConParante = false
        mochetaLDesdeModeloCm = 0f
        mochetaRDesdeModeloCm = 0f
        mochetaLModulos = emptyList()
        mochetaLParantes = emptyList()
        mochetaLFranjas = emptyList()
        mochetaRModulos = emptyList()
        mochetaRParantes = emptyList()
        mochetaRFranjas = emptyList()
        segmentosNs = emptyList()
        modoArcoCurvo = false
        flechaArcoCm = 0f
        modoCircular = false
        val secciones = splitRespetandoParentesis(modelo.replace(" ", ""))
            .filter { it.isNotEmpty() }

        var segmentosNsExtra: List<SegmentoNs> = emptyList()
        val franjasParseadas = secciones.map { frag ->
            val low = frag.lowercase()
            require(low.startsWith("s(") || low.startsWith("s<") || low.startsWith("m(") || low.startsWith("m<")) {
                "Franja invalida: $frag"
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
            val circular = parsearIndicadorCircular(modTxt)
            if (tipo == TipoFranja.SISTEMA && circular.activo) {
                modoCircular = true
            }
            val arco = parsearIndicadorArco(circular.textoSinO)
            if (tipo == TipoFranja.SISTEMA && arco.activo) {
                modoArcoCurvo = true
                flechaArcoCm = arco.flechaCm
            }
            val nsData = parsearIndicadoresNs(arco.textoSinU)
            if (tipo == TipoFranja.SISTEMA && nsData.segmentos.isNotEmpty()) {
                segmentosNsExtra = nsData.segmentos.mapNotNull { convertirSegmentoNsTag(it) }
            }
            val lData = parsearIndicadorEsquina(nsData.textoSinTags, "l")
            val rData = parsearIndicadorEsquina(lData.textoSinL, "r")
            val modsSinLR = rData.textoSinL
            if (tipo == TipoFranja.SISTEMA && lData.tieneEsquina) {
                esquinaLConParante = true
                if (lData.mochetaCm > 0f) mochetaLDesdeModeloCm = max(mochetaLDesdeModeloCm, lData.mochetaCm)
                if (lData.modulosMocheta.isNotEmpty()) mochetaLModulos = lData.modulosMocheta
                if (lData.parantesMocheta.isNotEmpty()) mochetaLParantes = lData.parantesMocheta
                if (lData.franjasMocheta.isNotEmpty()) mochetaLFranjas = lData.franjasMocheta
            }
            if (tipo == TipoFranja.SISTEMA && rData.tieneEsquina) {
                esquinaRConParante = true
                if (rData.mochetaCm > 0f) mochetaRDesdeModeloCm = max(mochetaRDesdeModeloCm, rData.mochetaCm)
                if (rData.modulosMocheta.isNotEmpty()) mochetaRModulos = rData.modulosMocheta
                if (rData.parantesMocheta.isNotEmpty()) mochetaRParantes = rData.parantesMocheta
                if (rData.franjasMocheta.isNotEmpty()) mochetaRFranjas = rData.franjasMocheta
            }
            val (mods, parantes) = parsearModulosConParantes(modsSinLR)
            FranjaNova(tipo, mods, altura, parantes)
        }

        if (segmentosNsExtra.isNotEmpty()) {
            val baseAncho = if (anchoCm > 0f) anchoCm else 100f
            segmentosNs = listOf(SegmentoNs(TipoSegmentoNs.PLANO, baseAncho, franjasParseadas)) + segmentosNsExtra
        }

        return franjasParseadas
    }

    private data class SegmentoNsTag(
        val tipo: TipoSegmentoNs,
        val anchoCm: Float,
        val diseno: String
    )

    private data class ParseNsTagsResult(
        val textoSinTags: String,
        val segmentos: List<SegmentoNsTag>
    )

    private fun parsearIndicadoresNs(texto: String): ParseNsTagsResult {
        val regex = Regex("([ap])(?:<\\s*(-?\\d+(?:[.,]\\d+)?)\\s*>)?\\{([^}]*)\\}", RegexOption.IGNORE_CASE)
        val segmentos = mutableListOf<SegmentoNsTag>()
        for (m in regex.findAll(texto)) {
            val tipoTxt = m.groupValues.getOrNull(1).orEmpty().lowercase()
            val tipo = if (tipoTxt == "a") TipoSegmentoNs.ALETA else TipoSegmentoNs.PLANO
            val ancho = m.groupValues.getOrNull(2)?.replace(",", ".")?.toFloatOrNull() ?: 0f
            val diseno = m.groupValues.getOrNull(3).orEmpty()
            if (diseno.isNotBlank()) {
                segmentos.add(SegmentoNsTag(tipo = tipo, anchoCm = ancho, diseno = diseno))
            }
        }
        return ParseNsTagsResult(
            textoSinTags = texto.replace(regex, ""),
            segmentos = segmentos
        )
    }

    private data class ParseArcoResult(
        val textoSinU: String,
        val activo: Boolean,
        val flechaCm: Float
    )

    private data class ParseCircularResult(
        val textoSinO: String,
        val activo: Boolean
    )

    private fun parsearIndicadorCircular(texto: String): ParseCircularResult {
        val regex = Regex("o<\\s*(-?\\d+(?:[.,]\\d+)?)\\s*>", RegexOption.IGNORE_CASE)
        val m = regex.find(texto)
        return ParseCircularResult(
            textoSinO = texto.replace(regex, ""),
            activo = m != null
        )
    }

    private fun parsearIndicadorArco(texto: String): ParseArcoResult {
        val regex = Regex("u<\\s*(-?\\d+(?:[.,]\\d+)?)\\s*>", RegexOption.IGNORE_CASE)
        val m = regex.find(texto)
        val flecha = m?.groupValues?.getOrNull(1)?.replace(",", ".")?.toFloatOrNull()?.coerceAtLeast(0f) ?: 0f
        return ParseArcoResult(
            textoSinU = texto.replace(regex, ""),
            activo = m != null,
            flechaCm = flecha
        )
    }

    private fun convertirSegmentoNsTag(tag: SegmentoNsTag): SegmentoNs? {
        val franjas = parsearFranjasDesdeModeloCompleto(tag.diseno)
        if (franjas.isEmpty()) return null
        return SegmentoNs(tipo = tag.tipo, anchoCm = tag.anchoCm.coerceAtLeast(1f), franjas = franjas)
    }

    private data class ParseEsquinaResult(
        val textoSinL: String,
        val mochetaCm: Float,
        val tieneEsquina: Boolean,
        val modulosMocheta: List<TipoModulo>,
        val parantesMocheta: List<Int>,
        val franjasMocheta: List<FranjaNova>
    )

    private fun parsearIndicadorEsquina(texto: String, tag: String): ParseEsquinaResult {
        val regexL = Regex("$tag(?:<\\s*(-?\\d+(?:[.,]\\d+)?)\\s*>)?(?:\\(([^)]*)\\))?(?:\\{([^}]*)\\})?", RegexOption.IGNORE_CASE)
        var mocheta = 0f
        var tieneL = false
        var modulos: List<TipoModulo> = emptyList()
        var parantes: List<Int> = emptyList()
        var franjas: List<FranjaNova> = emptyList()
        for (match in regexL.findAll(texto)) {
            tieneL = true
            val valor = match.groupValues.getOrNull(1)?.replace(",", ".")?.toFloatOrNull() ?: 0f
            if (valor > 0f) mocheta = max(mocheta, valor)
            val patronMocheta = match.groupValues.getOrNull(2).orEmpty()
            if (patronMocheta.isNotBlank()) {
                val parsed = parsearModulosConParantes(patronMocheta)
                modulos = parsed.first
                parantes = parsed.second
            }
            val modeloCompleto = match.groupValues.getOrNull(3).orEmpty()
            if (modeloCompleto.isNotBlank()) {
                franjas = parsearFranjasDesdeModeloCompleto(modeloCompleto)
                val primeraSistema = franjas.firstOrNull { it.tipo == TipoFranja.SISTEMA }
                if (primeraSistema != null && primeraSistema.modulos.isNotEmpty()) {
                    modulos = primeraSistema.modulos
                    parantes = primeraSistema.parantePosiciones
                }
            }
        }
        if (tieneL && modulos.isEmpty()) {
            modulos = listOf(TipoModulo.FIJO)
            parantes = emptyList()
        }
        return ParseEsquinaResult(
            textoSinL = texto.replace(regexL, ""),
            mochetaCm = mocheta,
            tieneEsquina = tieneL,
            modulosMocheta = modulos,
            parantesMocheta = parantes,
            franjasMocheta = franjas
        )
    }

    private fun parsearFranjasDesdeModeloCompleto(modeloCompleto: String): List<FranjaNova> {
        val modelo = modeloCompleto.substringAfter(":", modeloCompleto)
        if (modelo.isBlank()) return emptyList()
        val secciones = splitRespetandoParentesis(modelo.replace(" ", ""))
            .filter { it.isNotEmpty() }
        return secciones.mapNotNull { frag ->
            val low = frag.lowercase()
            if (!(low.startsWith("s(") || low.startsWith("s<") || low.startsWith("m(") || low.startsWith("m<"))) {
                return@mapNotNull null
            }
            val tipo = if (low[0] == 's') TipoFranja.SISTEMA else TipoFranja.MOCHETA
            val altura: Float?
            val modTxt: String
            if (low.length > 1 && low[1] == '<') {
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
        val anchoTotalCm = if (segmentosNs.isNotEmpty()) {
            segmentosNs.sumOf { it.anchoCm.toDouble() }.toFloat().coerceAtLeast(1f)
        } else {
            anchoCm + mochetaLateralCm + mochetaLateralDerechaCm
        }
        val escala = min(anchoDisp / anchoTotalCm, altoDisp / altoCm)

        val x0 = (width  - (anchoTotalCm * escala)) / 2f
        val y0 = (height - (altoCm * escala)) / 2f
        val x1 = x0 + anchoTotalCm * escala
        val y1 = y0 + altoCm * escala
        val extraTopPx = if (modoArcoCurvo) {
            (flechaArcoCm.coerceAtLeast(0f) * escala).coerceAtLeast(0f)
        } else 0f

        val w = (x1 - x0 + 2 * paddingPx).toInt().coerceAtLeast(1)
        val h = (y1 - y0 + extraTopPx + 2 * paddingPx).toInt().coerceAtLeast(1)

        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        omitirFondoAlExportar = true
        c.translate(-(x0 - paddingPx), -((y0 - extraTopPx) - paddingPx))
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
        val anchoTotalCm = if (segmentosNs.isNotEmpty()) {
            segmentosNs.sumOf { it.anchoCm.toDouble() }.toFloat().coerceAtLeast(1f)
        } else {
            anchoCm + mochetaLateralCm + mochetaLateralDerechaCm
        }
        val escala = min(anchoDisp / anchoTotalCm, altoDisp / altoCm)

        val x0 = (width  - anchoTotalCm * escala) / 2f
        val y0 = (height - altoCm * escala) / 2f
        val x1 = x0 + anchoTotalCm * escala
        val y1 = y0 + altoCm * escala

        if (segmentosNs.isNotEmpty()) {
            rangosTramoX.clear()
            rangosTramoX.add(Pair(x0, x1))
            dibujarNs(canvas, x0, y0, y1, escala)
            dibujarCorteVerticalGlobal(canvas, x0, y0, y1, escala, anchoTotalCm)
            if (!omitirFondoAlExportar) {
                dibujarCotas(canvas, x0, y0, x1, y1, escala)
            }
            return
        }
        if (modoCircular) {
            rangosTramoX.clear()
            rangosTramoX.add(Pair(x0, x1))
            dibujarCircularNci(canvas, x0, y0, x1, y1, escala)
            dibujarCorteVerticalGlobal(canvas, x0, y0, y1, escala, anchoTotalCm)
            if (!omitirFondoAlExportar) {
                dibujarCotas(canvas, x0, y0, x1, y1, escala)
            }
            return
        }
        if (modoArcoCurvo) {
            rangosTramoX.clear()
            rangosTramoX.add(Pair(x0, x1))
            dibujarCurvoNcu(canvas, x0, y0, x1, y1, escala)
            dibujarCorteVerticalGlobal(canvas, x0, y0, y1, escala, anchoTotalCm)
            if (!omitirFondoAlExportar) {
                dibujarCotas(canvas, x0, y0, x1, y1, escala)
            }
            return
        }

        // Mocheta lateral izquierda/derecha
        val xVentIni = x0 + mochetaLateralCm * escala
        val xVentFin = x1 - mochetaLateralDerechaCm * escala
        val usaAletaPerspectivaIzq = mochetaLateralCm > 0f && mochetaLFranjas.isNotEmpty()
        val usaAletaPerspectivaDer = mochetaLateralDerechaCm > 0f && mochetaRFranjas.isNotEmpty()
        val usaAletaPerspectiva = usaAletaPerspectivaIzq || usaAletaPerspectivaDer

        // Marco exterior (7f): en NL con aleta en perspectiva, enmarcar solo la ventana principal
        if (usaAletaPerspectiva) {
            canvas.drawRect(RectF(xVentIni, y0, xVentFin, y1), pMarco)
        } else {
            canvas.drawRect(RectF(x0, y0, x1, y1), pMarco)
        }

        if (mochetaLateralCm > 0f) {
            if (mochetaLFranjas.isNotEmpty()) {
                if (modo == ModoEnsamble.APA) {
                    dibujarAletaPerspectivaAPA(
                        canvas = canvas,
                        franjas = mochetaLFranjas,
                        lado = LadoAleta.IZQ,
                        xUnion = xVentIni,
                        yTop = y0,
                        yBottom = y1,
                        anchoAletaPx = (xVentIni - x0).coerceAtLeast(1f),
                        escalaPxPorCm = escala
                    )
                } else {
                    dibujarAletaPerspectivaINA(
                        canvas = canvas,
                        franjas = mochetaLFranjas,
                        lado = LadoAleta.IZQ,
                        xUnion = xVentIni,
                        yTop = y0,
                        yBottom = y1,
                        anchoAletaPx = (xVentIni - x0).coerceAtLeast(1f),
                        escalaPxPorCm = escala
                    )
                }
            } else if (mochetaLModulos.isNotEmpty()) {
                canvas.drawRect(RectF(x0, y0, xVentIni, y1), pLinea)
                val n = mochetaLModulos.size
                val anchoModulo = (xVentIni - x0) / n.toFloat().coerceAtLeast(1f)
                val parantesSet = mochetaLParantes.toSet()
                for (i in 1 until n) {
                    if (i in parantesSet) continue
                    val xSep = x0 + i * anchoModulo
                    canvas.drawLine(xSep, y0, xSep, y1, pLinea)
                }
                val anchoParante = max(10f, 2.5f * escala)
                for (pos in mochetaLParantes) {
                    if (pos in 1 until n) {
                        val xPar = x0 + pos * anchoModulo
                        canvas.drawRect(RectF(xPar - anchoParante / 2, y0, xPar + anchoParante / 2, y1), pRellenoNegro)
                    }
                }
            } else {
                canvas.drawRect(RectF(x0, y0, xVentIni, y1), pLinea)
            }
        }

        // Guardar límites horizontales para hit-test
        if (mochetaLateralDerechaCm > 0f) {
            if (mochetaRFranjas.isNotEmpty()) {
                if (modo == ModoEnsamble.APA) {
                    dibujarAletaPerspectivaAPA(
                        canvas = canvas,
                        franjas = mochetaRFranjas,
                        lado = LadoAleta.DER,
                        xUnion = xVentFin,
                        yTop = y0,
                        yBottom = y1,
                        anchoAletaPx = (x1 - xVentFin).coerceAtLeast(1f),
                        escalaPxPorCm = escala
                    )
                } else {
                    dibujarAletaPerspectivaINA(
                        canvas = canvas,
                        franjas = mochetaRFranjas,
                        lado = LadoAleta.DER,
                        xUnion = xVentFin,
                        yTop = y0,
                        yBottom = y1,
                        anchoAletaPx = (x1 - xVentFin).coerceAtLeast(1f),
                        escalaPxPorCm = escala
                    )
                }
            } else if (mochetaRModulos.isNotEmpty()) {
                canvas.drawRect(RectF(xVentFin, y0, x1, y1), pLinea)
                val n = mochetaRModulos.size
                val anchoModulo = (x1 - xVentFin) / n.toFloat().coerceAtLeast(1f)
                val parantesSet = mochetaRParantes.toSet()
                for (i in 1 until n) {
                    if (i in parantesSet) continue
                    val xSep = xVentFin + i * anchoModulo
                    canvas.drawLine(xSep, y0, xSep, y1, pLinea)
                }
                val anchoParante = max(10f, 2.5f * escala)
                for (pos in mochetaRParantes) {
                    if (pos in 1 until n) {
                        val xPar = xVentFin + pos * anchoModulo
                        canvas.drawRect(RectF(xPar - anchoParante / 2, y0, xPar + anchoParante / 2, y1), pRellenoNegro)
                    }
                }
            } else {
                canvas.drawRect(RectF(xVentFin, y0, x1, y1), pLinea)
            }
        }
        ultimaVentanaX0 = xVentIni
        ultimaVentanaX1 = xVentFin

        // Limpiar rangos de franjas para este frame
        rangosFranjaY.clear()

        val anchoVentPx = xVentFin - xVentIni
        construirRangosTramoX(xVentIni, xVentFin, anchoVentPx)
        if (modo == ModoEnsamble.APA) {
            dibujarAPA(canvas, xVentIni, xVentFin, y1, anchoVentPx, escala)
        } else {
            dibujarINA(canvas, xVentIni, y0, xVentFin, y1, anchoVentPx, escala)
        }
        dibujarCorteVerticalGlobal(canvas, x0, y0, y1, escala, anchoTotalCm)

        // Dibujar cotas exteriores (solo si no es exportación)
        if (!omitirFondoAlExportar) {
            dibujarCotas(canvas, x0, y0, x1, y1, escala)
        }
    }

    private fun dibujarCorteVerticalGlobal(
        canvas: Canvas,
        x0: Float,
        y0: Float,
        y1: Float,
        escala: Float,
        anchoTotalCm: Float
    ) {
        val corte = corteVerticalCm ?: return
        if (corte <= 0f || corte >= anchoTotalCm) return
        val x = x0 + corte * escala
        val anchoParante = max(10f, 2.5f * escala)
        canvas.drawRect(RectF(x - anchoParante / 2f, y0, x + anchoParante / 2f, y1), pRellenoNegro)
    }

    private fun construirRangosTramoX(xIni: Float, xFin: Float, anchoVentPx: Float) {
        rangosTramoX.clear()
        val corte = corteVerticalCm
        if (corte != null && anchoCm > 0f) {
            val xCorte = xIni + ((corte / anchoCm).coerceIn(0f, 1f) * anchoVentPx)
            if (xCorte > xIni && xCorte < xFin) {
                rangosTramoX.add(Pair(xIni, xCorte))
                rangosTramoX.add(Pair(xCorte, xFin))
                return
            }
        }
        val cortesX = mutableListOf<Float>()
        if (modo == ModoEnsamble.APA) {
            val franjaSistema = franjasAbajoArriba.firstOrNull { it.tipo == TipoFranja.SISTEMA }
                ?: franjasAbajoArriba.firstOrNull()
            val n = franjaSistema?.modulos?.size ?: 0
            if (n > 0) {
                franjaSistema?.parantePosiciones.orEmpty()
                    .filter { it in 1 until n }
                    .forEach { pos -> cortesX.add(xIni + pos * (anchoVentPx / n.toFloat())) }
            }
        } else {
            val n = sistemaModulos.size
            if (n > 0) {
                sistemaParantes
                    .filter { it in 1 until n }
                    .forEach { pos -> cortesX.add(xIni + pos * (anchoVentPx / n.toFloat())) }
            }
        }
        val puntos = (listOf(xIni) + cortesX.distinct().sorted() + listOf(xFin))
        if (puntos.size < 2) {
            rangosTramoX.add(Pair(xIni, xFin))
            return
        }
        for (i in 0 until puntos.lastIndex) {
            rangosTramoX.add(Pair(puntos[i], puntos[i + 1]))
        }
    }

    private fun rangoHorizontalResaltado(xIni: Float, xFin: Float): Pair<Float, Float> {
        val tramo = indiceTramoResaltado
        if (tramo in rangosTramoX.indices) {
            return rangosTramoX[tramo]
        }
        return Pair(xIni, xFin)
    }

    private fun dibujarCircularNci(
        canvas: Canvas,
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        escalaPxPorCm: Float
    ) {
        val ancho = (x1 - x0).coerceAtLeast(1f)
        val alto = (y1 - y0).coerceAtLeast(1f)
        val diametro = min(ancho, alto)
        val cx = (x0 + x1) * 0.5f
        val cy = (y0 + y1) * 0.5f
        val r = (diametro * 0.5f).coerceAtLeast(1f)
        val left = cx - r
        val right = cx + r
        val top = cy - r
        val bottom = cy + r

        val pathCirculo = Path().apply { addOval(RectF(left, top, right, bottom), Path.Direction.CW) }

        var mods: List<TipoModulo> = listOf(TipoModulo.FIJO)
        var parantes: List<Int> = emptyList()
        var mTopCm = 0f
        var mBottomCm = 0f
        var mTopMods: List<TipoModulo> = emptyList()
        var mBottomMods: List<TipoModulo> = emptyList()
        var mTopParantes: List<Int> = emptyList()
        var mBottomParantes: List<Int> = emptyList()
        if (modo == ModoEnsamble.APA) {
            val idxS = franjasAbajoArriba.indexOfFirst { it.tipo == TipoFranja.SISTEMA }
            val franjaSistema = if (idxS >= 0) franjasAbajoArriba[idxS] else franjasAbajoArriba.firstOrNull()
            mods = franjaSistema?.modulos?.ifEmpty { listOf(TipoModulo.FIJO) } ?: listOf(TipoModulo.FIJO)
            parantes = franjaSistema?.parantePosiciones ?: emptyList()
            if (idxS >= 0) {
                val franjaTop = franjasAbajoArriba.withIndex()
                    .firstOrNull { it.index > idxS && it.value.tipo == TipoFranja.MOCHETA }?.value
                val franjaBottom = franjasAbajoArriba.withIndex()
                    .lastOrNull { it.index < idxS && it.value.tipo == TipoFranja.MOCHETA }?.value
                mTopMods = franjaTop?.modulos ?: emptyList()
                mBottomMods = franjaBottom?.modulos ?: emptyList()
                mTopParantes = franjaTop?.parantePosiciones ?: emptyList()
                mBottomParantes = franjaBottom?.parantePosiciones ?: emptyList()
            }
            val alturasFallback = if (franjasAbajoArriba.isNotEmpty()) distribuirAlturas(franjasAbajoArriba) else emptyList()
            if (idxS >= 0) {
                franjasAbajoArriba.forEachIndexed { i, f ->
                    if (f.tipo == TipoFranja.MOCHETA) {
                        val h = (f.alturaCm ?: alturasFallback.getOrElse(i) { 0f }).coerceAtLeast(0f)
                        if (i > idxS) mTopCm += h
                        if (i < idxS) mBottomCm += h
                    }
                }
            }
        } else {
            mods = sistemaModulos.ifEmpty { listOf(TipoModulo.FIJO) }
            parantes = sistemaParantes
            mTopCm = alturaMochetaTopCm.coerceAtLeast(0f)
            mBottomCm = alturaMochetaBottomCm.coerceAtLeast(0f)
        }

        val n = mods.size.coerceAtLeast(1)
        val anchoModulo = (diametro / n).coerceAtLeast(1f)
        val parantesSet = parantes.toSet()

        fun yRadioEnX(x: Float): Float {
            val dx = ((x - cx) / r).coerceIn(-1f, 1f)
            val inside = (1f - (dx * dx)).coerceAtLeast(0f)
            return r * sqrt(inside)
        }

        val yTopBase = top + anchoMarcoPx * 0.5f
        val yBottomBase = bottom - anchoMarcoPx * 0.5f
        val mTopPx = (mTopCm.coerceAtLeast(0f) * escalaPxPorCm).coerceAtMost((yBottomBase - yTopBase) * 0.4f)
        val mBottomPx = (mBottomCm.coerceAtLeast(0f) * escalaPxPorCm).coerceAtMost((yBottomBase - yTopBase) * 0.4f)
        val yPanelTop = (yTopBase + mTopPx).coerceAtMost(yBottomBase - 6f)
        val yPanelBottom = (yBottomBase - mBottomPx).coerceAtLeast(yTopBase + 6f)

        fun tramosC(): List<IntRange> {
            val res = mutableListOf<IntRange>()
            var i = 0
            while (i < n) {
                if (mods[i] == TipoModulo.CORREDIZA) {
                    val ini = i
                    var j = i + 1
                    while (j < n && mods[j] == TipoModulo.CORREDIZA) j++
                    res.add(ini..(j - 1))
                    i = j
                } else {
                    i++
                }
            }
            return res
        }

        rangosFranjaY.clear()
        rangosFranjaY.add(Pair(top, bottom))
        ultimaVentanaX0 = left
        ultimaVentanaX1 = right

        // Contenido interno recortado al círculo
        canvas.save()
        canvas.clipPath(pathCirculo)
        if (modo == ModoEnsamble.APA) {
            fun dibujarZonaModulos(
                yTopZona: Float,
                yBottomZona: Float,
                modsZona: List<TipoModulo>,
                parantesZona: List<Int>,
                zocaloEnCorrediza: Boolean
            ) {
                if (yBottomZona <= yTopZona) return
                val modsLocal = modsZona.ifEmpty { listOf(TipoModulo.FIJO) }
                val nLocal = modsLocal.size.coerceAtLeast(1)
                val anchoLocal = (diametro / nLocal).coerceAtLeast(1f)
                val parantesLocal = parantesZona.toSet()

                for (i in 1 until nLocal) {
                    if (i in parantesLocal) continue
                    val xSep = left + i * anchoLocal
                    val dy = yRadioEnX(xSep)
                    val yA = max(yTopZona, cy - dy)
                    val yB = min(yBottomZona, cy + dy)
                    if (yB > yA) {
                        canvas.drawLine(xSep, yA, xSep, yB, pLinea)
                    }
                }

                for (i in 0 until nLocal) {
                    val xM0 = left + i * anchoLocal
                    val xM1 = left + (i + 1) * anchoLocal
                    if (modsLocal[i] == TipoModulo.CORREDIZA && zocaloEnCorrediza) {
                        val yZBot = (yBottomZona - 2f).coerceAtLeast(yTopZona + 4f)
                        val yZTop = (yZBot - altoZocaloPx).coerceAtMost(yZBot)
                        canvas.drawRect(RectF(xM0, yZTop, xM1, yZBot), pRellenoNegro)
                        canvas.drawLine(xM0, yZTop, xM1, yZTop, pLinea)
                        dibujarReflejoVidrio(canvas, xM0, yTopZona, xM1, yZTop)
                    } else {
                        dibujarReflejoVidrio(canvas, xM0, yTopZona, xM1, yBottomZona)
                    }
                }
            }

            val puenteTopPx = if (mTopPx > 0f) altoPuentePx else 0f
            val puenteBottomPx = if (mBottomPx > 0f) altoPuentePx else 0f
            val yTopGlass = (yPanelTop + puenteTopPx).coerceAtMost(yPanelBottom - 4f)
            val yBottomGlass = (yPanelBottom - puenteBottomPx).coerceAtLeast(yPanelTop + 4f)
            if (puenteTopPx > 0f) {
                canvas.drawRect(RectF(left, yPanelTop, right, yTopGlass), pRellenoNegro)
            }
            if (puenteBottomPx > 0f) {
                canvas.drawRect(RectF(left, yBottomGlass, right, yPanelBottom), pRellenoNegro)
            }

            if (mTopPx > 0f) {
                dibujarZonaModulos(yTopBase, yPanelTop, mTopMods, mTopParantes, true)
            }
            dibujarZonaModulos(yTopGlass, yBottomGlass, mods, parantes, true)
            if (mBottomPx > 0f) {
                dibujarZonaModulos(yPanelBottom, yBottomBase, mBottomMods, mBottomParantes, true)
            }

            if (parantes.isNotEmpty()) {
                val anchoParante = max(10f, 2.5f * escalaPxPorCm)
                for (pos in parantes) {
                    if (pos in 1 until n) {
                        val xPar = left + pos * anchoModulo
                        canvas.drawRect(
                            RectF(xPar - anchoParante / 2, yTopBase, xPar + anchoParante / 2, yBottomBase),
                            pRellenoNegro
                        )
                    }
                }
            }
        } else {
            for (i in 1 until n) {
                val xSep = left + i * anchoModulo
                if (i in parantesSet) continue
                val izqC = mods[i - 1] == TipoModulo.CORREDIZA
                val derC = mods[i] == TipoModulo.CORREDIZA
                var yA = if (izqC && derC && mTopPx > 0f) yPanelTop else yTopBase
                var yB = if (izqC && derC && mBottomPx > 0f) yPanelBottom else yBottomBase
                val dy = yRadioEnX(xSep)
                yA = max(yA, cy - dy)
                yB = min(yB, cy + dy)
                if (yB > yA) {
                    canvas.drawLine(xSep, yA, xSep, yB, pLinea)
                }
            }

            val tramos = tramosC()
            if (mTopPx > 0f) {
                for (rango in tramos) {
                    val xL = left + rango.first * anchoModulo
                    val xR = left + (rango.last + 1) * anchoModulo
                    canvas.drawLine(xL, yTopBase, xR, yTopBase, pLinea)
                    canvas.drawLine(xL, yPanelTop, xR, yPanelTop, pLinea)
                    canvas.drawLine(xL, yTopBase, xL, yPanelTop, pLinea)
                    canvas.drawLine(xR, yTopBase, xR, yPanelTop, pLinea)
                }
            }
            if (mBottomPx > 0f) {
                for (rango in tramos) {
                    val xL = left + rango.first * anchoModulo
                    val xR = left + (rango.last + 1) * anchoModulo
                    canvas.drawLine(xL, yPanelBottom, xR, yPanelBottom, pLinea)
                    canvas.drawLine(xL, yBottomBase, xR, yBottomBase, pLinea)
                    canvas.drawLine(xL, yPanelBottom, xL, yBottomBase, pLinea)
                    canvas.drawLine(xR, yPanelBottom, xR, yBottomBase, pLinea)
                }
            }

            for (i in 0 until n) {
                val xM0 = left + i * anchoModulo
                val xM1 = left + (i + 1) * anchoModulo
                if (mods[i] == TipoModulo.CORREDIZA) {
                    val yTopMod = yPanelTop
                    val yBottomMod = yPanelBottom
                    val yZBot = (yBottomMod - 2f).coerceAtLeast(yTopMod + 4f)
                    val yZTop = (yZBot - altoZocaloPx).coerceAtMost(yZBot)
                    canvas.drawRect(RectF(xM0, yZTop, xM1, yZBot), pRellenoNegro)
                    canvas.drawLine(xM0, yZTop, xM1, yZTop, pLinea)
                    dibujarReflejoVidrio(canvas, xM0, yTopMod, xM1, yZTop)
                } else {
                    val yTopMod = yTopBase
                    val yBottomMod = yBottomBase
                    dibujarReflejoVidrio(canvas, xM0, yTopMod, xM1, yBottomMod)
                }
            }

            for (pos in parantes) {
                if (pos in 1 until n) {
                    val xPar = left + pos * anchoModulo
                    val dy = yRadioEnX(xPar)
                    canvas.drawLine(xPar, cy - dy, xPar, cy + dy, pLinea)
                }
            }
        }
        canvas.restore()

        // Contorno circular al final para limpiar los bordes del recorte
        canvas.drawOval(RectF(left, top, right, bottom), pMarco)
    }

    private fun dibujarNs(
        canvas: Canvas,
        xInicio: Float,
        yTop: Float,
        yBottom: Float,
        escalaPxPorCm: Float
    ) {
        if (segmentosNs.isEmpty()) return
        rangosFranjaY.clear()
        var xCursor = xInicio
        var yTopPlanoActual = yTop
        var yBottomPlanoActual = yBottom
        var escalaAcumulada = 1f

        segmentosNs.forEachIndexed { idx, segmento ->
            val escalaLocal = (escalaPxPorCm * escalaAcumulada).coerceAtLeast(0.0001f)
            val anchoNominalPx = (segmento.anchoCm * escalaLocal).coerceAtLeast(1f)
            val xIni = xCursor
            var xFin = xCursor + anchoNominalPx
            val anchoParante = max(10f, 2.5f * escalaLocal)
            if (idx > 0) {
                canvas.drawRect(
                    RectF(xIni - anchoParante / 2, yTopPlanoActual, xIni + anchoParante / 2, yBottomPlanoActual),
                    pRellenoNegro
                )
            }
            when (segmento.tipo) {
                TipoSegmentoNs.PLANO -> {
                    canvas.drawRect(RectF(xIni, yTopPlanoActual, xFin, yBottomPlanoActual), pMarco)
                    val anchoVentPx = xFin - xIni
                    if (modo == ModoEnsamble.APA) {
                        dibujarAPASoloFranja(
                            canvas = canvas,
                            franjas = segmento.franjas,
                            xIni = xIni,
                            xFin = xFin,
                            yBotTotal = yBottomPlanoActual,
                            anchoVentPx = anchoVentPx,
                            escalaPxPorCm = escalaLocal
                        )
                    } else {
                        dibujarINASoloFranja(
                            canvas = canvas,
                            franjas = segmento.franjas,
                            xIni = xIni,
                            yTopTotal = yTopPlanoActual,
                            xFin = xFin,
                            yBotTotal = yBottomPlanoActual,
                            anchoVentPx = anchoVentPx,
                            escalaPxPorCm = escalaLocal
                        )
                    }
                    ultimaVentanaX0 = xIni
                    ultimaVentanaX1 = xFin
                }
                TipoSegmentoNs.ALETA -> {
                    val perspectiva = calcularAletaPerspectiva(
                        lado = LadoAleta.DER,
                        xUnion = xIni,
                        yTop = yTopPlanoActual,
                        yBottom = yBottomPlanoActual,
                        anchoAletaPx = anchoNominalPx
                    )
                    if (modo == ModoEnsamble.APA) {
                        dibujarAletaPerspectivaAPA(
                            canvas = canvas,
                            franjas = segmento.franjas,
                            lado = LadoAleta.DER,
                            xUnion = xIni,
                            yTop = yTopPlanoActual,
                            yBottom = yBottomPlanoActual,
                            anchoAletaPx = anchoNominalPx,
                            escalaPxPorCm = escalaLocal
                        )
                    } else {
                        dibujarAletaPerspectivaINA(
                            canvas = canvas,
                            franjas = segmento.franjas,
                            lado = LadoAleta.DER,
                            xUnion = xIni,
                            yTop = yTopPlanoActual,
                            yBottom = yBottomPlanoActual,
                            anchoAletaPx = anchoNominalPx,
                            escalaPxPorCm = escalaLocal
                        )
                    }
                    val xExterior = (perspectiva.bordeExteriorTop.x + perspectiva.bordeExteriorBottom.x) * 0.5f
                    xFin = max(xIni + 1f, xExterior)
                    val alturaAntes = (yBottomPlanoActual - yTopPlanoActual).coerceAtLeast(1f)
                    val nuevoTop = perspectiva.bordeExteriorTop.y
                    val nuevoBottom = perspectiva.bordeExteriorBottom.y
                    val alturaDespues = (nuevoBottom - nuevoTop).coerceAtLeast(1f)
                    val ratio = (alturaDespues / alturaAntes).coerceIn(0.35f, 1f)
                    escalaAcumulada = (escalaAcumulada * ratio).coerceAtLeast(0.15f)
                    yTopPlanoActual = nuevoTop
                    yBottomPlanoActual = nuevoBottom
                }
            }
            xCursor = xFin
        }
    }

    private fun dibujarCurvoNcu(
        canvas: Canvas,
        x0: Float,
        y0: Float,
        x1: Float,
        y1: Float,
        escalaPxPorCm: Float
    ) {
        val pBaseInferior = Paint(pMarco)
        val flechaPx = (flechaArcoCm.coerceAtLeast(0f) * escalaPxPorCm).coerceIn(0f, (y1 - y0) * 0.35f)
        val altoTotal = (y1 - y0).coerceAtLeast(1f)
        val centroX = (x0 + x1) * 0.5f
        val medioAncho = ((x1 - x0) * 0.5f).coerceAtLeast(1f)

        fun curvaEnX(x: Float): Float {
            val u = ((x - centroX) / medioAncho).coerceIn(-1f, 1f)
            return (1f - (u * u)).coerceAtLeast(0f) * flechaPx
        }

        fun yCurva(yNominal: Float, x: Float): Float {
            val k = ((y1 - yNominal) / altoTotal).coerceIn(0f, 1f)
            val kMin = 0.72f // Curvatura inferior más cercana a la superior
            val factor = (kMin + ((1f - kMin) * k)).coerceIn(kMin, 1f)
            return yNominal - (curvaEnX(x) * factor)
        }

        fun dibujarLineaCurva(yNominal: Float, paint: Paint) {
            val pasos = 32
            var xPrev = x0
            var yPrev = yCurva(yNominal, xPrev)
            for (i in 1..pasos) {
                val t = i / pasos.toFloat()
                val x = x0 + (x1 - x0) * t
                val y = yCurva(yNominal, x)
                canvas.drawLine(xPrev, yPrev, x, y, paint)
                xPrev = x
                yPrev = y
            }
        }

        fun dibujarLineaCurvaTramo(yNominal: Float, xLeft: Float, xRight: Float, paint: Paint) {
            val pasos = 20
            var xPrev = xLeft
            var yPrev = yCurva(yNominal, xPrev)
            for (i in 1..pasos) {
                val t = i / pasos.toFloat()
                val x = xLeft + (xRight - xLeft) * t
                val y = yCurva(yNominal, x)
                canvas.drawLine(xPrev, yPrev, x, y, paint)
                xPrev = x
                yPrev = y
            }
        }

        fun dibujarBandaCurva(yTopNominal: Float, yBottomNominal: Float, fill: Paint, stroke: Paint? = null) {
            val pasos = 28
            val path = Path()
            path.moveTo(x0, yCurva(yTopNominal, x0))
            for (i in 1..pasos) {
                val t = i / pasos.toFloat()
                val x = x0 + (x1 - x0) * t
                path.lineTo(x, yCurva(yTopNominal, x))
            }
            for (i in pasos downTo 0) {
                val t = i / pasos.toFloat()
                val x = x0 + (x1 - x0) * t
                path.lineTo(x, yCurva(yBottomNominal, x))
            }
            path.close()
            canvas.drawPath(path, fill)
            if (stroke != null) canvas.drawPath(path, stroke)
        }

        fun dibujarBandaCurvaModulo(
            xLeft: Float,
            xRight: Float,
            yTopNominal: Float,
            yBottomNominal: Float,
            fill: Paint,
            stroke: Paint? = null
        ) {
            val pasos = 10
            val path = Path()
            path.moveTo(xLeft, yCurva(yTopNominal, xLeft))
            for (i in 1..pasos) {
                val t = i / pasos.toFloat()
                val x = xLeft + (xRight - xLeft) * t
                path.lineTo(x, yCurva(yTopNominal, x))
            }
            for (i in pasos downTo 0) {
                val t = i / pasos.toFloat()
                val x = xLeft + (xRight - xLeft) * t
                path.lineTo(x, yCurva(yBottomNominal, x))
            }
            path.close()
            canvas.drawPath(path, fill)
            if (stroke != null) canvas.drawPath(path, stroke)
        }

        if (modo == ModoEnsamble.INA && sistemaModulos.isNotEmpty()) {
            val insetMarco = anchoMarcoPx * 0.5f
            val yTopN = y0 + insetMarco
            val yBottomN = y1 - insetMarco
            val mTopPx = max(0f, alturaMochetaTopCm) * escalaPxPorCm
            val mBottomPx = max(0f, alturaMochetaBottomCm) * escalaPxPorCm
            val yPanelTop = yTopN + mTopPx
            val yPanelBottom = yBottomN - mBottomPx

            dibujarLineaCurva(yTopN, pLinea)
            dibujarLineaCurva(yBottomN, pLinea)
            canvas.drawLine(x0, yCurva(yTopN, x0), x0, yCurva(yBottomN, x0), pLinea)
            canvas.drawLine(x1, yCurva(yTopN, x1), x1, yCurva(yBottomN, x1), pLinea)

            val n = sistemaModulos.size
            val anchoModulo = (x1 - x0) / n.toFloat().coerceAtLeast(1f)
            val xs = FloatArray(n + 1) { i -> x0 + i * anchoModulo }

            for (i in 1 until n) {
                val izqC = sistemaModulos[i - 1] == TipoModulo.CORREDIZA
                val derC = sistemaModulos[i] == TipoModulo.CORREDIZA
                val yA = if (izqC && derC && mTopPx > 0f) yPanelTop else yTopN
                val yB = if (izqC && derC && mBottomPx > 0f) yPanelBottom else yBottomN
                val xSep = xs[i]
                canvas.drawLine(xSep, yCurva(yA, xSep), xSep, yCurva(yB, xSep), pLinea)
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
                    val xL = xs[r.first]
                    val xR = xs[r.last + 1]
                    dibujarLineaCurvaTramo(yTopN, xL, xR, pLinea)
                    dibujarLineaCurvaTramo(yPanelTop, xL, xR, pLinea)
                    canvas.drawLine(xL, yCurva(yTopN, xL), xL, yCurva(yPanelTop, xL), pLinea)
                    canvas.drawLine(xR, yCurva(yTopN, xR), xR, yCurva(yPanelTop, xR), pLinea)
                }
            }
            if (mBottomPx > 0f) {
                for (r in tramos) {
                    val xL = xs[r.first]
                    val xR = xs[r.last + 1]
                    dibujarLineaCurvaTramo(yPanelBottom, xL, xR, pLinea)
                    dibujarLineaCurvaTramo(yBottomN, xL, xR, pLinea)
                    canvas.drawLine(xL, yCurva(yPanelBottom, xL), xL, yCurva(yBottomN, xL), pLinea)
                    canvas.drawLine(xR, yCurva(yPanelBottom, xR), xR, yCurva(yBottomN, xR), pLinea)
                }
            }

            for (i in 0 until n) {
                val xM0 = xs[i]
                val xM1 = xs[i + 1]
                if (sistemaModulos[i] == TipoModulo.CORREDIZA) {
                    val yZBot = (yPanelBottom - 2f).coerceAtLeast(yPanelTop + 3f)
                    val yZTop = (yZBot - altoZocaloPx).coerceAtMost(yZBot)
                    dibujarBandaCurvaModulo(xM0, xM1, yZTop, yZBot, pRellenoNegro)
                    canvas.drawLine(xM0, yCurva(yZTop, xM0), xM1, yCurva(yZTop, xM1), pLinea)
                    dibujarReflejoVidrio(canvas, xM0, yCurva(yPanelTop, xM0), xM1, yCurva(yZTop, xM1))
                } else {
                    dibujarReflejoVidrio(canvas, xM0, yCurva(yTopN, xM0), xM1, yCurva(yBottomN, xM1))
                }
            }

            canvas.drawLine(x0, yCurva(y1, x0), x0, yCurva(y0, x0), pMarco)
            dibujarLineaCurva(y0, pMarco)
            canvas.drawLine(x1, yCurva(y0, x1), x1, yCurva(y1, x1), pMarco)
            dibujarLineaCurva(y1, pBaseInferior)

            ultimaVentanaX0 = x0
            ultimaVentanaX1 = x1
            return
        }

        val franjas = if (modo == ModoEnsamble.APA && franjasAbajoArriba.isNotEmpty()) {
            franjasAbajoArriba
        } else {
            val lista = mutableListOf<FranjaNova>()
            val modsSistema = sistemaModulos.ifEmpty { listOf(TipoModulo.FIJO) }
            val modsMochetaIna = List(modsSistema.size.coerceAtLeast(1)) { TipoModulo.FIJO }
            if (alturaMochetaBottomCm > 0f) {
                lista.add(FranjaNova(TipoFranja.MOCHETA, modsMochetaIna, alturaMochetaBottomCm, emptyList()))
            }
            if (modsSistema.isNotEmpty()) {
                // En INA curvo no mostramos parantes de sistema para mantener lectura limpia.
                lista.add(FranjaNova(TipoFranja.SISTEMA, modsSistema, null, emptyList()))
            }
            if (alturaMochetaTopCm > 0f) {
                lista.add(FranjaNova(TipoFranja.MOCHETA, modsMochetaIna, alturaMochetaTopCm, emptyList()))
            }
            lista
        }
        if (franjas.isEmpty()) return

        val alturas = distribuirAlturas(franjas)
        var yAbajo = y1
        val parantesX = mutableListOf<Float>()

        franjas.forEachIndexed { idx, franja ->
            val altoF = alturas.getOrElse(idx) { 0f } * escalaPxPorCm
            val yArriba = yAbajo - altoF
            val yTopNom = yArriba
            val yBottomNom = yAbajo

            rangosFranjaY.add(Pair(yCurva(yTopNom, centroX), yCurva(yBottomNom, centroX)))

            var hayMAbajo = false
            var hayMArriba = false
            if (modo == ModoEnsamble.APA && franja.tipo == TipoFranja.SISTEMA) {
                if (idx > 0 && franjas[idx - 1].tipo == TipoFranja.MOCHETA) {
                    hayMAbajo = true
                    dibujarBandaCurva(yBottomNom - altoPuentePx, yBottomNom, pRellenoNegro)
                }
                if (idx < franjas.lastIndex && franjas[idx + 1].tipo == TipoFranja.MOCHETA) {
                    hayMArriba = true
                    dibujarBandaCurva(yTopNom, yTopNom + altoPuentePx, pRellenoNegro)
                }
            }

            dibujarLineaCurva(yTopNom, pLinea)
            if (idx != 0) {
                dibujarLineaCurva(yBottomNom, pLinea)
            }
            canvas.drawLine(x0, yCurva(yTopNom, x0), x0, yCurva(yBottomNom, x0), pLinea)
            canvas.drawLine(x1, yCurva(yTopNom, x1), x1, yCurva(yBottomNom, x1), pLinea)

            val n = franja.modulos.size.coerceAtLeast(1)
            val anchoModulo = (x1 - x0) / n.toFloat()
            val parantesSet = franja.parantePosiciones.toSet()
            for (i in 1 until n) {
                if (i in parantesSet) continue
                val xSep = x0 + i * anchoModulo
                canvas.drawLine(xSep, yCurva(yTopNom, xSep), xSep, yCurva(yBottomNom, xSep), pLinea)
            }

            if (modo == ModoEnsamble.APA && franja.tipo == TipoFranja.SISTEMA && franja.parantePosiciones.isNotEmpty()) {
                for (pos in franja.parantePosiciones) {
                    if (pos in 1 until n) parantesX.add(x0 + pos * anchoModulo)
                }
            }

            val yZBotGlobalRaw = if (hayMAbajo) (yBottomNom - altoPuentePx) else yBottomNom
            // Separar zócalo de la base para que no se funda visualmente con el borde inferior.
            val yZBotGlobal = if (!hayMAbajo) {
                (yZBotGlobalRaw - 2f).coerceAtLeast(yTopNom + 3f)
            } else {
                yZBotGlobalRaw
            }
            val yTGlass = if (hayMArriba) (yTopNom + altoPuentePx) else yTopNom
            for (i in 0 until n) {
                if (franja.modulos[i] != TipoModulo.CORREDIZA) continue
                val xM0 = x0 + i * anchoModulo
                val xM1 = x0 + (i + 1) * anchoModulo
                val yZBotModulo = if (franja.tipo == TipoFranja.SISTEMA) yZBotGlobal else (yBottomNom - 2f).coerceAtLeast(yTopNom + 3f)
                val yZTop = yZBotModulo - altoZocaloPx
                dibujarBandaCurvaModulo(xM0, xM1, yZTop, yZBotModulo, pRellenoNegro)
                canvas.drawLine(xM0, yCurva(yZTop, xM0), xM1, yCurva(yZTop, xM1), pLinea)
                dibujarReflejoVidrio(
                    canvas,
                    xM0,
                    yCurva(yTGlass, xM0),
                    xM1,
                    yCurva(yZTop, xM1)
                )
            }

            yAbajo = yArriba
        }

        if (parantesX.isNotEmpty()) {
            val anchoParante = max(10f, 2.5f * escalaPxPorCm)
            for (xP in parantesX) {
                canvas.drawRect(
                    RectF(
                        xP - anchoParante / 2,
                        yCurva(y0, xP),
                        xP + anchoParante / 2,
                        yCurva(y1, xP)
                    ),
                    pRellenoNegro
                )
            }
        }

        // Contorno exterior curvo: marco estándar, base inferior más fina
        canvas.drawLine(x0, yCurva(y1, x0), x0, yCurva(y0, x0), pMarco)
        dibujarLineaCurva(y0, pMarco)
        canvas.drawLine(x1, yCurva(y0, x1), x1, yCurva(y1, x1), pMarco)
        dibujarLineaCurva(y1, pBaseInferior)

        ultimaVentanaX0 = x0
        ultimaVentanaX1 = x1
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

        // Pre-calcular posiciones X de los parantes desde la franja sistema
        val parantesXPx: List<Float> = run {
            val sist = franjasAbajoArriba.firstOrNull { it.tipo == TipoFranja.SISTEMA }
                ?: return@run emptyList()
            val n = sist.modulos.size.coerceAtLeast(1)
            val xsS = posicionesModulosAPA(xIni, xFin, anchoVentPx, n, sist.parantePosiciones)
            sist.parantePosiciones.filter { it in 1 until n }.distinct().sorted().map { xsS[it] }
        }

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
            if (n <= 0) {
                yAbajo = yArriba
                return@forEachIndexed
            }
            val xs = posicionesModulosConTramos(
                xIni = xIni,
                xFin = xFin,
                nModulos = n,
                parantePosiciones = franja.parantePosiciones,
                parantesXPx = parantesXPx
            )
            val parantesSet = franja.parantePosiciones.toSet()
            for (i in 1 until n) {
                if (i in parantesSet) continue
                val xSep = xs[i]
                canvas.drawLine(xSep, yTop, xSep, yBottom, pLinea)
            }
            canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pLinea)

            // Guardar posiciones de parantes solo desde franja sistema
            if (franja.tipo == TipoFranja.SISTEMA && franja.parantePosiciones.isNotEmpty()) {
                for (pos in franja.parantePosiciones) {
                    if (pos in 1 until n) {
                        parantesX.add(xs[pos])
                    }
                }
            }

            // Zócalos + Reflejo
            when (franja.tipo) {
                TipoFranja.SISTEMA -> {
                    val yZBotGlobal = if (hayMAbajo) (yBottom - altoPuentePx) else yBottom
                    val yTGlass     = if (hayMArriba) (yTop + altoPuentePx) else yTop
                    for (i in 0 until n) {
                        val xM0 = xs[i]
                        val xM1 = xs[i + 1]
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
                        val xM0 = xs[i]
                        val xM1 = xs[i + 1]
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
                val (xR0, xR1) = rangoHorizontalResaltado(xIni, xFin)
                canvas.drawRect(RectF(xR0, yTop, xR1, yBottom), pResaltaRelleno)
                canvas.drawRect(RectF(xR0, yTop, xR1, yBottom), pResaltaBorde)
            }
            // Resaltado del módulo seleccionado (doble click)
            if (idx == indiceFranjaResaltada && indiceModuloResaltado in 0 until n) {
                val xM0 = xs[indiceModuloResaltado]
                val xM1 = xs[indiceModuloResaltado + 1]
                canvas.drawRect(RectF(xM0, yTop, xM1, yBottom), pResaltaModuloRelleno)
                canvas.drawRect(RectF(xM0, yTop, xM1, yBottom), pResaltaModuloBorde)
            }

            yAbajo = yArriba
        }

        // Dibujar parantes a toda la altura (rectángulo de 2.5 cm, mínimo 10px)
        if (parantesX.isNotEmpty() && corteVerticalCm == null) {
            val yTopVentana = yBotTotal - altoCm * escalaPxPorCm
            val anchoParante = max(10f, 2.5f * escalaPxPorCm)
            for (x in parantesX) {
                canvas.drawRect(RectF(x - anchoParante / 2, yTopVentana, x + anchoParante / 2, yBotTotal), pRellenoNegro)
            }
        }
        if (esquinaLConParante && mochetaLateralCm > 0f) {
            val yTopVentana = yBotTotal - altoCm * escalaPxPorCm
            val anchoParante = max(10f, 2.5f * escalaPxPorCm)
            canvas.drawRect(
                RectF(xIni - anchoParante / 2, yTopVentana, xIni + anchoParante / 2, yBotTotal),
                pRellenoNegro
            )
        }
        if (esquinaRConParante && mochetaLateralDerechaCm > 0f) {
            val yTopVentana = yBotTotal - altoCm * escalaPxPorCm
            val anchoParante = max(10f, 2.5f * escalaPxPorCm)
            canvas.drawRect(
                RectF(xFin - anchoParante / 2, yTopVentana, xFin + anchoParante / 2, yBotTotal),
                pRellenoNegro
            )
        }
    }

    private fun dibujarAletaPerspectivaAPA(
        canvas: Canvas,
        franjas: List<FranjaNova>,
        lado: LadoAleta,
        xUnion: Float,
        yTop: Float,
        yBottom: Float,
        anchoAletaPx: Float,
        escalaPxPorCm: Float
    ) {
        if (franjas.isEmpty()) return
        val perspectiva = calcularAletaPerspectiva(
            lado = lado,
            xUnion = xUnion,
            yTop = yTop,
            yBottom = yBottom,
            anchoAletaPx = anchoAletaPx
        )
        dibujarQuad(
            canvas = canvas,
            a = perspectiva.bordeUnionTop,
            b = perspectiva.bordeExteriorTop,
            c = perspectiva.bordeExteriorBottom,
            d = perspectiva.bordeUnionBottom,
            fill = pFondo,
            stroke = pLinea
        )
        dibujarQuad(
            canvas = canvas,
            a = perspectiva.bordeUnionTop,
            b = perspectiva.bordeExteriorTop,
            c = perspectiva.bordeExteriorBottom,
            d = perspectiva.bordeUnionBottom,
            fill = pAletaSombra2,
            stroke = pLinea
        )

        val alturasCm = distribuirAlturas(franjas)
        val altoTotalCm = alturasCm.sum().coerceAtLeast(1f)
        var acumuladoCm = 0f
        val toleranciaPuenteT = (altoPuentePx / (yBottom - yTop).coerceAtLeast(1f)).coerceIn(0.005f, 0.12f)
        val toleranciaZocaloT = (altoZocaloPx / (yBottom - yTop).coerceAtLeast(1f)).coerceIn(0.005f, 0.12f)

        franjas.forEachIndexed { idx, franja ->
            val h = alturasCm.getOrElse(idx) { 0f }.coerceAtLeast(0f)
            val tBottom = (1f - (acumuladoCm / altoTotalCm)).coerceIn(0f, 1f)
            acumuladoCm += h
            val tTop = (1f - (acumuladoCm / altoTotalCm)).coerceIn(0f, 1f)

            val nearTop = lerp(perspectiva.bordeUnionTop, perspectiva.bordeUnionBottom, tTop)
            val farTop = lerp(perspectiva.bordeExteriorTop, perspectiva.bordeExteriorBottom, tTop)
            val nearBottom = lerp(perspectiva.bordeUnionTop, perspectiva.bordeUnionBottom, tBottom)
            val farBottom = lerp(perspectiva.bordeExteriorTop, perspectiva.bordeExteriorBottom, tBottom)

            canvas.drawLine(nearTop.x, nearTop.y, farTop.x, farTop.y, pLinea)
            canvas.drawLine(nearBottom.x, nearBottom.y, farBottom.x, farBottom.y, pLinea)

            val n = franja.modulos.size.coerceAtLeast(1)
            val parantesSet = franja.parantePosiciones.toSet()
            for (i in 1 until n) {
                val lambda = i / n.toFloat()
                val topPoint = lerp(nearTop, farTop, lambda)
                val bottomPoint = lerp(nearBottom, farBottom, lambda)
                if (i in parantesSet) {
                    val half = 0.014f
                    val p1 = lerp(nearTop, farTop, (lambda - half).coerceIn(0f, 1f))
                    val p2 = lerp(nearTop, farTop, (lambda + half).coerceIn(0f, 1f))
                    val p3 = lerp(nearBottom, farBottom, (lambda + half).coerceIn(0f, 1f))
                    val p4 = lerp(nearBottom, farBottom, (lambda - half).coerceIn(0f, 1f))
                    dibujarQuad(canvas, p1, p2, p3, p4, pRellenoNegro, pLinea)
                } else {
                    canvas.drawLine(topPoint.x, topPoint.y, bottomPoint.x, bottomPoint.y, pLinea)
                }
            }

            var tVidrioTop = tTop
            var tVidrioBottom = tBottom
            val hayMAbajo = franja.tipo == TipoFranja.SISTEMA && idx > 0 && franjas[idx - 1].tipo == TipoFranja.MOCHETA
            val hayMArriba = franja.tipo == TipoFranja.SISTEMA && idx < franjas.lastIndex && franjas[idx + 1].tipo == TipoFranja.MOCHETA
            if (hayMArriba) {
                val tPuenteTop = (tTop + toleranciaPuenteT).coerceAtMost(tBottom)
                dibujarBandaEntreT(canvas, perspectiva, tTop, tPuenteTop, pRellenoNegro)
                tVidrioTop = tPuenteTop
            }
            if (hayMAbajo) {
                val tPuenteBottom = (tBottom - toleranciaPuenteT).coerceAtLeast(tTop)
                dibujarBandaEntreT(canvas, perspectiva, tPuenteBottom, tBottom, pRellenoNegro)
                tVidrioBottom = tPuenteBottom
            }

            for (i in 0 until n) {
                val lambda0 = i / n.toFloat()
                val lambda1 = (i + 1) / n.toFloat()
                val isCorrediza = franja.modulos.getOrNull(i) == TipoModulo.CORREDIZA
                if (isCorrediza && tVidrioBottom > tVidrioTop) {
                    val tZTop = (tVidrioBottom - toleranciaZocaloT).coerceAtLeast(tVidrioTop)
                    val a = puntoEnAleta(perspectiva, tZTop, lambda0)
                    val b = puntoEnAleta(perspectiva, tZTop, lambda1)
                    val c = puntoEnAleta(perspectiva, tVidrioBottom, lambda1)
                    val d = puntoEnAleta(perspectiva, tVidrioBottom, lambda0)
                    dibujarQuad(canvas, a, b, c, d, pRellenoNegro, pLinea)
                }
            }
        }
    }

    private fun dibujarAletaPerspectivaINA(
        canvas: Canvas,
        franjas: List<FranjaNova>,
        lado: LadoAleta,
        xUnion: Float,
        yTop: Float,
        yBottom: Float,
        anchoAletaPx: Float,
        escalaPxPorCm: Float
    ) {
        if (franjas.isEmpty()) return
        val idxS = franjas.indexOfFirst { it.tipo == TipoFranja.SISTEMA }
        if (idxS < 0) return
        val sistema = franjas[idxS]
        val mods = sistema.modulos
        if (mods.isEmpty()) return

        val p = calcularAletaPerspectiva(
            lado = lado,
            xUnion = xUnion,
            yTop = yTop,
            yBottom = yBottom,
            anchoAletaPx = anchoAletaPx
        )

        dibujarQuad(
            canvas = canvas,
            a = p.bordeUnionTop,
            b = p.bordeExteriorTop,
            c = p.bordeExteriorBottom,
            d = p.bordeUnionBottom,
            fill = pAletaSombra2,
            stroke = pLinea
        )

        val alturas = distribuirAlturas(franjas)
        val altoTotal = alturas.sum().coerceAtLeast(1f)
        var mTopCm = 0f
        var mBottomCm = 0f
        franjas.forEachIndexed { i, f ->
            if (f.tipo == TipoFranja.MOCHETA) {
                val h = (f.alturaCm ?: alturas.getOrElse(i) { 0f }).coerceAtLeast(0f)
                if (i > idxS) mTopCm += h
                if (i < idxS) mBottomCm += h
            }
        }

        val tPanelTop = (mTopCm / altoTotal).coerceIn(0f, 1f)
        val tPanelBottom = (1f - (mBottomCm / altoTotal)).coerceIn(0f, 1f)
        val n = mods.size

        for (i in 1 until n) {
            val izqC = mods[i - 1] == TipoModulo.CORREDIZA
            val derC = mods[i] == TipoModulo.CORREDIZA
            val tA = if (izqC && derC && mTopCm > 0f) tPanelTop else 0f
            val tB = if (izqC && derC && mBottomCm > 0f) tPanelBottom else 1f
            val lambda = i / n.toFloat()
            val p1 = puntoEnAleta(p, tA, lambda)
            val p2 = puntoEnAleta(p, tB, lambda)
            canvas.drawLine(p1.x, p1.y, p2.x, p2.y, pLinea)
        }

        val tramos = tramosContinuosC(mods)
        if (mTopCm > 0f) {
            for (r in tramos) {
                val l0 = r.first / n.toFloat()
                val l1 = (r.last + 1) / n.toFloat()
                val a = puntoEnAleta(p, 0f, l0)
                val b = puntoEnAleta(p, 0f, l1)
                val c = puntoEnAleta(p, tPanelTop, l1)
                val d = puntoEnAleta(p, tPanelTop, l0)
                dibujarQuad(canvas, a, b, c, d, pAletaSombra2, pLinea)
            }
        }
        if (mBottomCm > 0f) {
            for (r in tramos) {
                val l0 = r.first / n.toFloat()
                val l1 = (r.last + 1) / n.toFloat()
                val a = puntoEnAleta(p, tPanelBottom, l0)
                val b = puntoEnAleta(p, tPanelBottom, l1)
                val c = puntoEnAleta(p, 1f, l1)
                val d = puntoEnAleta(p, 1f, l0)
                dibujarQuad(canvas, a, b, c, d, pAletaSombra2, pLinea)
            }
        }

        val toleranciaZocaloT = (altoZocaloPx / (yBottom - yTop).coerceAtLeast(1f)).coerceIn(0.005f, 0.12f)
        for (i in 0 until n) {
            if (mods[i] != TipoModulo.CORREDIZA) continue
            val l0 = i / n.toFloat()
            val l1 = (i + 1) / n.toFloat()
            val tZTop = (tPanelBottom - toleranciaZocaloT).coerceAtLeast(tPanelTop)
            val a = puntoEnAleta(p, tZTop, l0)
            val b = puntoEnAleta(p, tZTop, l1)
            val c = puntoEnAleta(p, tPanelBottom, l1)
            val d = puntoEnAleta(p, tPanelBottom, l0)
            dibujarQuad(canvas, a, b, c, d, pRellenoNegro, pLinea)
        }
    }

    private fun calcularAletaPerspectiva(
        lado: LadoAleta,
        xUnion: Float,
        yTop: Float,
        yBottom: Float,
        anchoAletaPx: Float
    ): AletaPerspectiva {
        val anguloFijo = 90f
        val factorProfundidad = (anguloFijo / 90f).coerceAtLeast(0.2f)
        val zPx = anchoAletaPx * factorProfundidad * 0.95f
        val xVp = if (lado == LadoAleta.IZQ) {
            xUnion - (anchoAletaPx * 1.35f)
        } else {
            xUnion + (anchoAletaPx * 1.35f)
        }
        val yVp = (yTop + yBottom) * 0.5f
        val focal = ((yBottom - yTop) * 2.2f).coerceIn(320f, 2200f)

        val unionTop = PointF(xUnion, yTop)
        val unionBottom = PointF(xUnion, yBottom)
        val extTop = proyectarPerspectiva(unionTop, xVp, yVp, zPx, focal)
        val extBottom = proyectarPerspectiva(unionBottom, xVp, yVp, zPx, focal)
        return AletaPerspectiva(
            bordeUnionTop = unionTop,
            bordeUnionBottom = unionBottom,
            bordeExteriorTop = extTop,
            bordeExteriorBottom = extBottom
        )
    }

    private fun proyectarPerspectiva(base: PointF, xVp: Float, yVp: Float, zPx: Float, focalPx: Float): PointF {
        val k = zPx / (zPx + focalPx.coerceAtLeast(80f))
        val kY = (k * 0.5f).coerceIn(0f, 1f)
        return PointF(
            base.x + ((xVp - base.x) * k),
            base.y + ((yVp - base.y) * kY)
        )
    }

    private fun lerp(a: PointF, b: PointF, t: Float): PointF {
        val clamped = t.coerceIn(0f, 1f)
        return PointF(
            a.x + ((b.x - a.x) * clamped),
            a.y + ((b.y - a.y) * clamped)
        )
    }

    private fun puntoEnAleta(p: AletaPerspectiva, t: Float, lambda: Float): PointF {
        val near = lerp(p.bordeUnionTop, p.bordeUnionBottom, t)
        val far = lerp(p.bordeExteriorTop, p.bordeExteriorBottom, t)
        return lerp(near, far, lambda)
    }

    private fun dibujarQuad(
        canvas: Canvas,
        a: PointF,
        b: PointF,
        c: PointF,
        d: PointF,
        fill: Paint,
        stroke: Paint
    ) {
        val path = Path().apply {
            moveTo(a.x, a.y)
            lineTo(b.x, b.y)
            lineTo(c.x, c.y)
            lineTo(d.x, d.y)
            close()
        }
        canvas.drawPath(path, fill)
        canvas.drawPath(path, stroke)
    }

    private fun dibujarBandaEntreT(
        canvas: Canvas,
        p: AletaPerspectiva,
        tTop: Float,
        tBottom: Float,
        fill: Paint
    ) {
        val a = lerp(p.bordeUnionTop, p.bordeUnionBottom, tTop)
        val b = lerp(p.bordeExteriorTop, p.bordeExteriorBottom, tTop)
        val c = lerp(p.bordeExteriorTop, p.bordeExteriorBottom, tBottom)
        val d = lerp(p.bordeUnionTop, p.bordeUnionBottom, tBottom)
        dibujarQuad(canvas, a, b, c, d, fill, pLinea)
    }


    private fun dibujarAPASoloFranja(
        canvas: Canvas,
        franjas: List<FranjaNova>,
        xIni: Float,
        xFin: Float,
        yBotTotal: Float,
        anchoVentPx: Float,
        escalaPxPorCm: Float
    ) {
        if (franjas.isEmpty()) return
        val alturasCm = distribuirAlturas(franjas)
        var yAbajo = yBotTotal
        val parantesX = mutableListOf<Float>()
        franjas.forEachIndexed { idx, franja ->
            val altoFpx = alturasCm.getOrElse(idx) { 0f } * escalaPxPorCm
            val yArriba = yAbajo - altoFpx
            val yTop = yArriba
            val yBottom = yAbajo

            var hayMAbajo = false
            var hayMArriba = false
            if (franja.tipo == TipoFranja.SISTEMA) {
                if (idx > 0 && franjas[idx - 1].tipo == TipoFranja.MOCHETA) {
                    hayMAbajo = true
                    canvas.drawRect(RectF(xIni, yBottom - altoPuentePx, xFin, yBottom), pRellenoNegro)
                }
                if (idx < franjas.lastIndex && franjas[idx + 1].tipo == TipoFranja.MOCHETA) {
                    hayMArriba = true
                    canvas.drawRect(RectF(xIni, yTop, xFin, yTop + altoPuentePx), pRellenoNegro)
                }
            }

            canvas.drawLine(xIni, yTop, xFin, yTop, pLinea)
            canvas.drawLine(xIni, yBottom, xFin, yBottom, pLinea)
            val n = franja.modulos.size.coerceAtLeast(1)
            val anchoModulo = anchoVentPx / n
            val parantesSet = franja.parantePosiciones.toSet()
            for (i in 1 until n) {
                if (i in parantesSet) continue
                val xSep = xIni + i * anchoModulo
                canvas.drawLine(xSep, yTop, xSep, yBottom, pLinea)
            }
            canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pLinea)
            if (franja.tipo == TipoFranja.SISTEMA && franja.parantePosiciones.isNotEmpty()) {
                for (pos in franja.parantePosiciones) {
                    if (pos in 1 until n) {
                        parantesX.add(xIni + pos * anchoModulo)
                    }
                }
            }

            when (franja.tipo) {
                TipoFranja.SISTEMA -> {
                    val yZBotGlobal = if (hayMAbajo) (yBottom - altoPuentePx) else yBottom
                    val yTGlass = if (hayMArriba) (yTop + altoPuentePx) else yTop
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
            yAbajo = yArriba
        }
        if (parantesX.isNotEmpty()) {
            val yTopVentana = yBotTotal - altoCm * escalaPxPorCm
            val anchoParante = max(10f, 2.5f * escalaPxPorCm)
            for (x in parantesX) {
                canvas.drawRect(RectF(x - anchoParante / 2, yTopVentana, x + anchoParante / 2, yBotTotal), pRellenoNegro)
            }
        }
    }

    private fun posicionesModulosAPA(
        xIni: Float,
        xFin: Float,
        anchoVentPx: Float,
        nModulos: Int,
        parantes: List<Int>
    ): FloatArray {
        val xs = FloatArray(nModulos + 1)
        for (i in 0..nModulos) xs[i] = xIni + i * (anchoVentPx / nModulos.toFloat())

        val corte = corteVerticalCm ?: return xs
        if (anchoCm <= 0f) return xs
        val cortes = parantes.filter { it in 1 until nModulos }.distinct().sorted()
        if (cortes.size != 1) return xs
        val idxCorte = cortes[0]

        val xCorte = xIni + ((corte / anchoCm).coerceIn(0f, 1f) * anchoVentPx)
        if (xCorte <= xIni || xCorte >= xFin) return xs

        val leftCount = idxCorte
        val rightCount = nModulos - idxCorte
        if (leftCount <= 0 || rightCount <= 0) return xs

        val wLeft = xCorte - xIni
        val wRight = xFin - xCorte
        if (wLeft <= 0f || wRight <= 0f) return xs

        xs[0] = xIni
        for (i in 1..leftCount) {
            xs[i] = xIni + (wLeft * i / leftCount.toFloat())
        }
        for (i in 1..rightCount) {
            xs[idxCorte + i] = xCorte + (wRight * i / rightCount.toFloat())
        }
        return xs
    }

    // Distribuye módulos de una franja respetando los límites en px de cada tramo.
    // parantesXPx: posiciones X (en px) de cada parante, en orden.
    private fun posicionesModulosConTramos(
        xIni: Float,
        xFin: Float,
        nModulos: Int,
        parantePosiciones: List<Int>,
        parantesXPx: List<Float>
    ): FloatArray {
        val xs = FloatArray(nModulos + 1)
        val cortes = parantePosiciones.filter { it in 1 until nModulos }.distinct().sorted()
        if (cortes.isEmpty() || parantesXPx.size < cortes.size) {
            // sin parantes: distribución uniforme
            for (i in 0..nModulos) xs[i] = xIni + i * ((xFin - xIni) / nModulos.toFloat())
            return xs
        }
        // límites de cada tramo en px
        val xLimites = listOf(xIni) + parantesXPx.take(cortes.size) + listOf(xFin)
        // límites de cada tramo en índices de módulo
        val mLimites = listOf(0) + cortes + listOf(nModulos)
        for (t in 0 until xLimites.lastIndex) {
            val x0 = xLimites[t]; val x1 = xLimites[t + 1]
            val m0 = mLimites[t]; val m1 = mLimites[t + 1]
            val count = (m1 - m0).coerceAtLeast(1)
            for (m in 0..count) {
                xs[m0 + m] = x0 + m * ((x1 - x0) / count.toFloat())
            }
        }
        return xs
    }

    private fun dibujarINASoloFranja(
        canvas: Canvas,
        franjas: List<FranjaNova>,
        xIni: Float,
        yTopTotal: Float,
        xFin: Float,
        yBotTotal: Float,
        anchoVentPx: Float,
        escalaPxPorCm: Float
    ) {
        if (franjas.isEmpty()) return
        val idxS = franjas.indexOfFirst { it.tipo == TipoFranja.SISTEMA }
        if (idxS < 0) return

        val sistema = franjas[idxS]
        val sistemaMods = sistema.modulos
        if (sistemaMods.isEmpty()) return

        val alturasFallback = distribuirAlturas(franjas)
        var mTopCm = 0f
        var mBottomCm = 0f
        franjas.forEachIndexed { i, f ->
            if (f.tipo == TipoFranja.MOCHETA) {
                val h = (f.alturaCm ?: alturasFallback.getOrElse(i) { 0f }).coerceAtLeast(0f)
                if (i > idxS) mTopCm += h
                if (i < idxS) mBottomCm += h
            }
        }

        val insetMarco = anchoMarcoPx * 0.5f
        val yTop = yTopTotal + insetMarco
        val yBottom = yBotTotal - insetMarco

        canvas.drawLine(xIni, yTop, xFin, yTop, pLinea)
        canvas.drawLine(xIni, yBottom, xFin, yBottom, pLinea)
        canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pLinea)

        val n = sistemaMods.size
        val anchoModulo = anchoVentPx / n
        val xs = FloatArray(n + 1) { i -> xIni + i * anchoModulo }

        val mTopPx = max(0f, mTopCm) * escalaPxPorCm
        val mBottomPx = max(0f, mBottomCm) * escalaPxPorCm
        val yPanelTop = yTop + mTopPx
        val yPanelBottom = yBottom - mBottomPx

        for (i in 1 until n) {
            val izqC = (sistemaMods[i - 1] == TipoModulo.CORREDIZA)
            val derC = (sistemaMods[i] == TipoModulo.CORREDIZA)
            val yA = if (izqC && derC && mTopPx > 0f) yPanelTop else yTop
            val yB = if (izqC && derC && mBottomPx > 0f) yPanelBottom else yBottom
            canvas.drawLine(xs[i], yA, xs[i], yB, pLinea)
        }

        val tramos = tramosContinuosC(sistemaMods, sistema.parantePosiciones)
        if (mTopPx > 0f) {
            for (r in tramos) {
                val x0 = xs[r.first]
                val x1 = xs[r.last + 1]
                canvas.drawLine(x0, yTop, x1, yTop, pLinea)
                canvas.drawLine(x0, yPanelTop, x1, yPanelTop, pLinea)
                canvas.drawLine(x0, yTop, x0, yPanelTop, pLinea)
                canvas.drawLine(x1, yTop, x1, yPanelTop, pLinea)
            }
        }
        if (mBottomPx > 0f) {
            for (r in tramos) {
                val x0 = xs[r.first]
                val x1 = xs[r.last + 1]
                canvas.drawLine(x0, yPanelBottom, x1, yPanelBottom, pLinea)
                canvas.drawLine(x0, yBottom, x1, yBottom, pLinea)
                canvas.drawLine(x0, yPanelBottom, x0, yBottom, pLinea)
                canvas.drawLine(x1, yPanelBottom, x1, yBottom, pLinea)
            }
        }

        for (i in 0 until n) {
            val x0 = xs[i]
            val x1 = xs[i + 1]
            if (sistemaMods[i] == TipoModulo.CORREDIZA) {
                val yZTop = (yPanelBottom - altoZocaloPx).coerceAtMost(yPanelBottom)
                canvas.drawRect(RectF(x0, yZTop, x1, yPanelBottom), pRellenoNegro)
                canvas.drawLine(x0, yZTop, x1, yZTop, pLinea)
                dibujarReflejoVidrio(canvas, x0, yPanelTop, x1, yZTop)
            } else {
                dibujarReflejoVidrio(canvas, x0, yTop, x1, yBottom)
            }
        }
    }

    private fun tramosContinuosC(
        modulos: List<TipoModulo>,
        parantesPosiciones: List<Int> = emptyList()
    ): List<IntRange> {
        val res = mutableListOf<IntRange>()
        val parantes = parantesPosiciones.toSet()
        var i = 0
        while (i < modulos.size) {
            if (modulos[i] == TipoModulo.CORREDIZA) {
                var ini = i
                var j = i + 1
                while (j < modulos.size && modulos[j] == TipoModulo.CORREDIZA) {
                    if (j in parantes) {
                        if (ini <= j - 1) res.add(ini..(j - 1))
                        ini = j
                    }
                    j++
                }
                if (ini <= j - 1) res.add(ini..(j - 1))
                i = j
            } else {
                i++
            }
        }
        return res
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

        // Tramos de C para mochetas, cortados también por parantes.
        val tramos = tramosContinuosC(sistemaModulos, sistemaParantes)

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
            val (xR0, xR1) = rangoHorizontalResaltado(xIni, xFin)
            canvas.drawRect(RectF(xR0, yTop, xR1, yBottom), pResaltaRelleno)
            canvas.drawRect(RectF(xR0, yTop, xR1, yBottom), pResaltaBorde)
        }
        // Resaltado del módulo seleccionado (doble click)
        if (indiceFranjaResaltada == 0 && indiceModuloResaltado in 0 until n) {
            val xM0 = xs[indiceModuloResaltado]
            val xM1 = xs[indiceModuloResaltado + 1]
            canvas.drawRect(RectF(xM0, yTop, xM1, yBottom), pResaltaModuloRelleno)
            canvas.drawRect(RectF(xM0, yTop, xM1, yBottom), pResaltaModuloBorde)
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
        val anchoCota = if (segmentosNs.isNotEmpty()) {
            segmentosNs.sumOf { it.anchoCm.toDouble() }.toFloat()
        } else {
            anchoCm + mochetaLateralCm + mochetaLateralDerechaCm
        }
        val textoAncho = fmt(anchoCota)
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
        val anchoTotalCm = anchoCm + mochetaLateralCm + mochetaLateralDerechaCm
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

        val xVentIni = x0 + mochetaLateralCm * escala
        val xVentFin = x1
        val usaAletaPerspectiva = mochetaLateralCm > 0f && mochetaLFranjas.isNotEmpty()

        // Marco exterior
        if (usaAletaPerspectiva) {
            rectStroke(xVentIni, y0, x1, y1, 7f)
        } else {
            rectStroke(x0, y0, x1, y1, 7f)
        }

        // Mocheta lateral izquierda (solo contorno)
        if (mochetaLateralCm > 0f && !usaAletaPerspectiva) rectStroke(x0, y0, xVentIni, y1, 3f)
        if (mochetaLateralCm > 0f && mochetaLFranjas.isNotEmpty()) {
            if (modo == ModoEnsamble.APA) {
                val alturasCmL = distribuirAlturas(mochetaLFranjas)
                var yAbajoL = y1
                val parantesXL = mutableListOf<Float>()
                mochetaLFranjas.forEachIndexed { idx, franja ->
                    val altoFpx = alturasCmL.getOrElse(idx) { 0f } * escala
                    val yArriba = yAbajoL - altoFpx
                    val yTop = yArriba
                    val yBottom = yAbajoL
                    var hayMAbajo = false
                    var hayMArriba = false
                    if (franja.tipo == TipoFranja.SISTEMA) {
                        if (idx > 0 && mochetaLFranjas[idx - 1].tipo == TipoFranja.MOCHETA) {
                            hayMAbajo = true
                            rectFill(x0, yBottom - altoPuentePx, xVentIni, yBottom)
                        }
                        if (idx < mochetaLFranjas.lastIndex && mochetaLFranjas[idx + 1].tipo == TipoFranja.MOCHETA) {
                            hayMArriba = true
                            rectFill(x0, yTop, xVentIni, yTop + altoPuentePx)
                        }
                    }
                    line(x0, yTop, xVentIni, yTop, 3f)
                    line(x0, yBottom, xVentIni, yBottom, 3f)
                    val n = franja.modulos.size.coerceAtLeast(1)
                    val anchoModulo = (xVentIni - x0) / n
                    val parantesSet = franja.parantePosiciones.toSet()
                    for (i in 1 until n) {
                        if (i in parantesSet) continue
                        val xSep = x0 + i * anchoModulo
                        line(xSep, yTop, xSep, yBottom, 3f)
                    }
                    rectStroke(x0, yTop, xVentIni, yBottom, 3f)
                    if (franja.tipo == TipoFranja.SISTEMA && franja.parantePosiciones.isNotEmpty()) {
                        for (pos in franja.parantePosiciones) {
                            if (pos in 1 until n) parantesXL.add(x0 + pos * anchoModulo)
                        }
                    }
                    when (franja.tipo) {
                        TipoFranja.SISTEMA -> {
                            val yZBotGlobal = if (hayMAbajo) (yBottom - altoPuentePx) else yBottom
                            val yTopGlass = if (hayMArriba) (yTop + altoPuentePx) else yTop
                            for (i in 0 until n) {
                                val x0m = x0 + i * anchoModulo
                                val x1m = x0 + (i + 1) * anchoModulo
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
                                val x0m = x0 + i * anchoModulo
                                val x1m = x0 + (i + 1) * anchoModulo
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
                    yAbajoL = yArriba
                }
                if (parantesXL.isNotEmpty()) {
                    val anchoParanteM = max(10f, 2.5f * escala)
                    for (xP in parantesXL) rectFill(xP - anchoParanteM / 2, y0, xP + anchoParanteM / 2, y1)
                }
            } else {
                val idxS = mochetaLFranjas.indexOfFirst { it.tipo == TipoFranja.SISTEMA }
                if (idxS >= 0) {
                    val sistema = mochetaLFranjas[idxS]
                    val mods = sistema.modulos
                    if (mods.isNotEmpty()) {
                        val alturasFallbackL = distribuirAlturas(mochetaLFranjas)
                        var mTopCm = 0f
                        var mBottomCm = 0f
                        mochetaLFranjas.forEachIndexed { i, f ->
                            if (f.tipo == TipoFranja.MOCHETA) {
                                val h = (f.alturaCm ?: alturasFallbackL.getOrElse(i) { 0f }).coerceAtLeast(0f)
                                if (i > idxS) mTopCm += h
                                if (i < idxS) mBottomCm += h
                            }
                        }
                        val yTop = y0 + 0.5f * 7f
                        val yBottom = y1 - 0.5f * 7f
                        line(x0, yTop, xVentIni, yTop, 3f)
                        line(x0, yBottom, xVentIni, yBottom, 3f)
                        rectStroke(x0, yTop, xVentIni, yBottom, 3f)

                        val n = mods.size
                        val anchoModulo = (xVentIni - x0) / n
                        val xs = FloatArray(n + 1) { i -> x0 + i * anchoModulo }
                        val mTopPx = max(0f, mTopCm) * escala
                        val mBottomPx = max(0f, mBottomCm) * escala
                        val yPanelTop = yTop + mTopPx
                        val yPanelBottom = yBottom - mBottomPx

                        for (i in 1 until n) {
                            val izqC = (mods[i - 1] == TipoModulo.CORREDIZA)
                            val derC = (mods[i] == TipoModulo.CORREDIZA)
                            val yA = if (izqC && derC && mTopPx > 0f) yPanelTop else yTop
                            val yB = if (izqC && derC && mBottomPx > 0f) yPanelBottom else yBottom
                            line(xs[i], yA, xs[i], yB, 3f)
                        }

                        val tramos = tramosContinuosC(mods)
                        if (mTopPx > 0f) {
                            for (r in tramos) {
                                val x0m = xs[r.first]
                                val x1m = xs[r.last + 1]
                                line(x0m, yTop, x1m, yTop, 3f)
                                line(x0m, yPanelTop, x1m, yPanelTop, 3f)
                                line(x0m, yTop, x0m, yPanelTop, 3f)
                                line(x1m, yTop, x1m, yPanelTop, 3f)
                            }
                        }
                        if (mBottomPx > 0f) {
                            for (r in tramos) {
                                val x0m = xs[r.first]
                                val x1m = xs[r.last + 1]
                                line(x0m, yPanelBottom, x1m, yPanelBottom, 3f)
                                line(x0m, yBottom, x1m, yBottom, 3f)
                                line(x0m, yPanelBottom, x0m, yBottom, 3f)
                                line(x1m, yPanelBottom, x1m, yBottom, 3f)
                            }
                        }

                        for (i in 0 until n) {
                            val x0m = xs[i]
                            val x1m = xs[i + 1]
                            if (mods[i] == TipoModulo.CORREDIZA) {
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
            }
        } else if (mochetaLateralCm > 0f && mochetaLModulos.isNotEmpty()) {
            val n = mochetaLModulos.size
            val anchoModulo = (xVentIni - x0) / n.toFloat().coerceAtLeast(1f)
            val parantesSet = mochetaLParantes.toSet()
            for (i in 1 until n) {
                if (i in parantesSet) continue
                val xSep = x0 + i * anchoModulo
                line(xSep, y0, xSep, y1, 3f)
            }
            val anchoParanteM = max(10f, 2.5f * escala)
            for (pos in mochetaLParantes) {
                if (pos in 1 until n) {
                    val xPar = x0 + pos * anchoModulo
                    rectFill(xPar - anchoParanteM / 2, y0, xPar + anchoParanteM / 2, y1)
                }
            }
        }
        val anchoVentPx = xVentFin - xVentIni

          if (modo == ModoEnsamble.APA) {
              val alturasCm = distribuirAlturas(franjasAbajoArriba)
              var yAbajo = y1

              // Pre-compute parante X positions from SISTEMA franja
              val parantesXsvg = mutableListOf<Float>()
              val sistFranjaIdx = franjasAbajoArriba.indexOfFirst { it.tipo == TipoFranja.SISTEMA }
              if (sistFranjaIdx >= 0) {
                  val sist = franjasAbajoArriba[sistFranjaIdx]
                  val nSist = sist.modulos.size.coerceAtLeast(1)
                  val anchoModSist = anchoVentPx / nSist
                  for (pos in sist.parantePosiciones) {
                      if (pos in 1 until nSist) parantesXsvg.add(xVentIni + pos * anchoModSist)
                  }
              }

              // Helper: compute per-tramo module X positions for mocheta franjas
              fun computeXsMocheta(nMods: Int, parantePosiciones: List<Int>): FloatArray {
                  val cortes = parantePosiciones.filter { it in 1 until nMods }.sorted()
                  val xLimits = mutableListOf(xVentIni)
                  xLimits.addAll(parantesXsvg.take(cortes.size))
                  xLimits.add(xVentFin)
                  val mLimits = mutableListOf(0)
                  mLimits.addAll(cortes)
                  mLimits.add(nMods)
                  val xs = FloatArray(nMods + 1)
                  for (t in 0 until xLimits.lastIndex) {
                      val tx0 = xLimits[t]; val tx1 = xLimits[t + 1]
                      val m0 = mLimits[t]; val m1 = mLimits[t + 1]
                      val count = (m1 - m0).coerceAtLeast(1)
                      for (m in 0..count) xs[m0 + m] = tx0 + m * ((tx1 - tx0) / count.toFloat())
                  }
                  return xs
              }

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

                val n = franja.modulos.size.coerceAtLeast(1)

                when (franja.tipo) {
                    TipoFranja.SISTEMA -> {
                        val anchoModulo = anchoVentPx / n
                        val parantesSet = franja.parantePosiciones.toSet()
                        for (i in 1 until n) {
                            if (i in parantesSet) continue
                            val xSep = xVentIni + i * anchoModulo
                            line(xSep, yTop, xSep, yBottom, 3f)
                        }
                        rectStroke(xVentIni, yTop, xVentFin, yBottom, 3f)
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
                        val xs = computeXsMocheta(n, franja.parantePosiciones)
                        val parantesSet = franja.parantePosiciones.toSet()
                        for (i in 1 until n) {
                            if (i in parantesSet) continue
                            line(xs[i], yTop, xs[i], yBottom, 3f)
                        }
                        rectStroke(xVentIni, yTop, xVentFin, yBottom, 3f)
                        for (i in 0 until n) {
                            val x0m = xs[i]
                            val x1m = xs[i + 1]
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
              if (esquinaLConParante && mochetaLateralCm > 0f) {
                  val anchoParante = max(10f, 2.5f * escala)
                  rectFill(xVentIni - anchoParante / 2, y0, xVentIni + anchoParante / 2, y1)
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

                val tramos = tramosContinuosC(sistemaModulos, sistemaParantes)

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
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                franjaDownIndex = -1
                if (x >= ultimaVentanaX0 && x <= ultimaVentanaX1) {
                    for (i in rangosFranjaY.indices) {
                        val (top, bottom) = rangosFranjaY[i]
                        if (y >= top && y <= bottom) {
                            franjaDownIndex = i
                            return true // consumir para recibir ACTION_UP
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                val i = franjaDownIndex
                if (i >= 0) {
                    val x = event.x
                    var tramo = -1
                    for (t in rangosTramoX.indices) {
                        val (x0, x1) = rangosTramoX[t]
                        if (x >= x0 && x <= x1) { tramo = t; break }
                    }
                    indiceFranjaResaltada = i
                    indiceTramoResaltado = tramo

                    if (i == franjaConfirmadaPorToque && tramo == tramoConfirmadoPorToque) {
                        // Toque en franja+tramo ya confirmados → seleccionar módulo
                        val n = if (modo == ModoEnsamble.APA)
                            franjasAbajoArriba.getOrNull(i)?.modulos?.size ?: 0
                        else
                            sistemaModulos.size
                        if (n > 0) {
                            val anchoVentPx = ultimaVentanaX1 - ultimaVentanaX0
                            val m = ((x - ultimaVentanaX0) / (anchoVentPx / n))
                                .toInt().coerceIn(0, n - 1)
                            indiceModuloResaltado = m
                            // franjaConfirmadaPorToque se mantiene: siguiente click también va a módulo
                            invalidate()
                            alDobleClicModulo?.invoke(i, m)
                            return true
                        }
                    } else {
                        // Primer toque (franja o tramo nuevos) → confirmar
                        franjaConfirmadaPorToque = i
                        tramoConfirmadoPorToque = tramo
                        indiceModuloResaltado = -1
                    }

                    invalidate()
                    alClicFranja?.invoke(i)
                    alClicFranjaTramo?.invoke(tramo, i)
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }
}



