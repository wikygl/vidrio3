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
    val parantePosiciones: List<Int> = emptyList(),
    val anchosMod: List<Float> = emptyList()  // cm por módulo; vacío = distribución igual
)
enum class TipoSegmentoNs { PLANO, ALETA }
data class SegmentoNs(
    val tipo: TipoSegmentoNs,
    val anchoCm: Float,
    val franjas: List<FranjaNova>,
    /** Alto propio del tramo en cm; 0 = el de la ventana. Es la ventana escalonada. */
    val altoCm: Float = 0f,
    /** Lo que baja el dintel de este tramo; 0 = arranca en el dintel de la ventana. */
    val caidaCm: Float = 0f,
    /** Las mismas dos medidas del lado derecho; null = iguales que las del izquierdo. */
    val altoDerCm: Float? = null,
    val caidaDerCm: Float? = null,
    /**
     * La panza de este tramo en cm; 0 = pared recta.
     *
     * Es la pared curva de una esquina: un paño más entre paños rectos, al revés que el arco de
     * `U<>`, que curva la ventana entera.
     */
    val flechaCm: Float = 0f,
    /**
     * La silueta de la pared que arranca en este tramo, en cm relativos a su esquina de arriba a
     * la izquierda; vacía = la pared es el rectángulo de sus medidas. Es el `L<…>` del tramo.
     */
    val contornoCm: List<Pair<Float, Float>> = emptyList()
) {
    val altoDerecho: Float get() = altoDerCm ?: altoCm
    val caidaDerecha: Float get() = caidaDerCm ?: caidaCm

    /** Con los dos lados distintos el tramo es un cuadrilátero, no un rectángulo. */
    val esInclinado: Boolean
        get() = kotlin.math.abs(altoDerecho - altoCm) > 0.05f ||
            kotlin.math.abs(caidaDerecha - caidaCm) > 0.05f
}

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
    // Encuentro: lados que colindan con vacío (orden ARBL: 1=colinda, 0=vacío).
    private var encuentroVacio: String = "1111"
    // Dirección de la geometría compuesta (aleta): "adentro" (acercándose) | "afuera".
    private var direccion: String = "adentro"
    private var corteVerticalCm: Float? = null

    // ------------ Estado según paquete ------------
    private var modo: ModoEnsamble = ModoEnsamble.INA

    // APA: franjas reales (abajo→arriba)
    private var franjasAbajoArriba: List<FranjaNova> = emptyList()

    // INA: módulos de la franja S + alturas de mocheta local (cm) arriba/abajo
    private var sistemaModulos: List<TipoModulo> = listOf(TipoModulo.CORREDIZA)
    private var sistemaParantes: List<Int> = emptyList()
    private var sistemaAnchos: List<Float> = emptyList()  // cm por módulo; vacío = reparto igual
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
    // (xIni, xFin, franjas) por cada segmento PLANO en rangosTramoX
    private val segmentosPlanoInfo = mutableListOf<Triple<Float, Float, List<FranjaNova>>>()
    private var modoArcoCurvo: Boolean = false
    private var flechaArcoCm: Float = 0f
    private var modoCircular: Boolean = false
    private var modoCUSimétrico: Boolean = false  // true para "nu" C/U simétrica (primera aleta va IZQ)
    private enum class LadoAleta { IZQ, DER }
    private data class AletaPerspectiva(
        val bordeUnionTop: PointF,
        val bordeUnionBottom: PointF,
        val bordeExteriorTop: PointF,
        val bordeExteriorBottom: PointF
    )

    // ------------ Estética / grosores ------------
    // Margen para las cotas. Con las cotas de cada lado del vano hacen falta dos filas por lado
    // (la del lado y la total, apartada), y con 55 px los números se salían de la pantalla.
    private val margenPx: Float get() = if (contornoConLados()) 120f else 55f
    private val anchoMarcoPx   = 7f   // contorno exterior
    private val anchoLineaPx   = 4f   // resto de líneas
    private val anchoReflejoPx = 1.5f // rayas de "reflejo"

    private var altoPuentePx = 12f    // banda en APA (junta m↔s); proporcional, se fija en onDraw
    private val altoZocaloPx = 12f    // zócalo bajo cada ‘c’

    // Encuentro vacío: recuadro punteado en el margen exterior del lado al vacío.
    private val vacioGapPx = 4f

    private val colorNegro = ContextCompat.getColor(context, android.R.color.black)

    private val pMarco = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorNegro; style = Paint.Style.STROKE; strokeWidth = anchoMarcoPx
    }
    private val pLinea = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorNegro; style = Paint.Style.STROKE; strokeWidth = anchoLineaPx
    }
    private val pLineaIna = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = colorNegro; style = Paint.Style.STROKE; strokeWidth = 1.5f
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
    /** Borra lo que el dibujo saca fuera del vano, cuando el fondo va transparente (exportación). */
    private val pBorrarVano = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR)
    }
    /** La silueta del vano en cm, desde el tag `V<…>`. Vacía = el vano es el rectángulo de siempre. */
    private var contornoVanoCm: List<Pair<Float, Float>> = emptyList()
    private val pVacioRelleno = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#33000000"); style = Paint.Style.FILL
    }
    private val pVacioBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CC000000"); style = Paint.Style.STROKE
        strokeWidth = 3.5f
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(12f, 7f), 0f)
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
    /** Pulsación larga sobre una franja: (tramo, franja). La usa el mando de franjas. */
    var alClicLargoFranja: ((tramo: Int, franja: Int) -> Unit)? = null
    /** Toque en el papel, fuera del dibujo: con algo elegido, es soltarlo. */
    var alTocarFuera: (() -> Unit)? = null
    private var indiceFranjaResaltada: Int = -1
    private var indiceTramoResaltado: Int = -1
    private var indiceModuloResaltado: Int = -1
    private val rangosFranjaY = mutableListOf<Pair<Float, Float>>() // [top, bottom] por franja (abajo→arriba)
    private val rangosTramoX = mutableListOf<Pair<Float, Float>>() // [left, right] por tramo (izq→der)
    // Las bandas de cada franja, tramo a tramo. Antes solo se guardaban las del primer tramo y
    // valian para todos: con franjas distintas por tramo, la de arriba del segundo no se podia
    // tocar porque su banda no existia.
    private val rangosFranjaPorTramo = mutableListOf<MutableList<Pair<Float, Float>>>()
    /** Dónde cae el dibujo dentro de la vista, para poder pegarle cosas al lado. */
    private val rectDiseno = RectF()
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

    /** ¿Hay algo elegido ahora mismo en el dibujo? */
    fun haySeleccion(): Boolean =
        indiceFranjaResaltada >= 0 || indiceModuloResaltado >= 0 || franjaConfirmadaPorToque >= 0

    /**
     * Suelta lo que estuviera elegido, incluidas las confirmaciones del toque.
     *
     * Sin esto la selección se quedaba pegada: una vez tocado un módulo, cada toque siguiente
     * elegía otro módulo y no había forma de volver a empezar.
     */
    fun limpiarSeleccion() {
        indiceFranjaResaltada = -1
        indiceTramoResaltado = -1
        indiceModuloResaltado = -1
        franjaConfirmadaPorToque = -1
        tramoConfirmadoPorToque = -1
        invalidate()
    }

    fun actualizarCorteVertical(corteCm: Float?) {
        corteVerticalCm = corteCm?.coerceAtLeast(0f)
        invalidate()
    }

    /** Lados que colindan con vacío (orden ARBL: 1=colinda, 0=vacío). */
    fun setEncuentroVacio(v: String?) {
        encuentroVacio = (v?.takeIf { it.isNotBlank() } ?: "1111").padEnd(4, '1')
        invalidate()
    }

    /** Dirección de la geometría compuesta: "adentro" (aleta hacia el observador) | "afuera". */
    fun setDireccion(v: String?) {
        direccion = if (v.equals("afuera", ignoreCase = true)) "afuera" else "adentro"
        invalidate()
    }

    /** Grosor del recuadro de vacío: proporcional a la ventana (perceptible a cualquier escala). */
    private fun grosorVacioPx(x0: Float, y0: Float, x1: Float, y1: Float): Float =
        (minOf(x1 - x0, y1 - y0) * 0.28f).coerceIn(40f, 160f)

    /**
     * Dibuja un recuadro punteado de "espacio vacío" en el margen exterior de cada lado
     * marcado como vacío (encuentroVacio, orden Arriba-Derecha-Abajo-Izquierda).
     * En exportación usa el grosor completo (el recorte reserva el espacio); en la vista
     * interactiva lo limita al espacio disponible para no salirse de la pantalla.
     */
    private fun dibujarEncuentroVacio(canvas: Canvas, x0: Float, y0: Float, x1: Float, y1: Float) {
        val v = encuentroVacio.padEnd(4, '1')
        val g = vacioGapPx
        val t0 = grosorVacioPx(x0, y0, x1, y1)
        fun grosor(disponible: Float): Float =
            if (omitirFondoAlExportar) t0 else minOf(t0, disponible - g).coerceAtLeast(8f)
        fun caja(l: Float, top: Float, r: Float, b: Float) {
            val rect = RectF(l, top, r, b)
            canvas.drawRect(rect, pVacioRelleno)
            canvas.drawRect(rect, pVacioBorde)
        }
        if (v[0] == '0') { val t = grosor(y0); caja(x0, y0 - g - t, x1, y0 - g) }                       // arriba
        if (v[1] == '0') { val t = grosor(width - x1); caja(x1 + g, y0, x1 + g + t, y1) }               // derecha
        if (v[2] == '0') { val t = grosor(height - y1); caja(x0, y1 + g, x1, y1 + g + t) }              // abajo
        if (v[3] == '0') { val t = grosor(x0); caja(x0 - g - t, y0, x0 - g, y1) }                       // izquierda

        // En el borde que colinda con el vacío se refuerza con parante (lados) o puente
        // (arriba/abajo): una barra del grosor del perfil (≈2.5 cm), dibujada sobre la ventana.
        val barra = altoPuentePx
        if (v[0] == '0') canvas.drawRect(RectF(x0, y0, x1, y0 + barra), pRellenoNegro)            // puente arriba
        if (v[2] == '0') canvas.drawRect(RectF(x0, y1 - barra, x1, y1), pRellenoNegro)            // puente abajo
        if (v[3] == '0') canvas.drawRect(RectF(x0, y0, x0 + barra, y1), pRellenoNegro)            // parante izquierda
        if (v[1] == '0') canvas.drawRect(RectF(x1 - barra, y0, x1, y1), pRellenoNegro)            // parante derecha
    }

    // ================== API PRINCIPAL ==================
    fun actualizarDesdePaquete(
        paquete: String?,
        anchoCm: Float,
        altoCm: Float,
        mochetaLateralCm: Float = 0f
    ) {
        val paqueteSeguro = paquete?.trim()
            ?.takeIf { it.isNotEmpty() && !it.equals("null", ignoreCase = true) }
            ?: PAQUETE_NOVA_FALLBACK

        // 1) Medidas que llegan (pueden ser 0 si se tomarán del paquete)
        this.anchoCm = anchoCm
        this.altoCm = altoCm
        this.mochetaLateralCm = mochetaLateralCm.coerceAtLeast(0f)
        this.mochetaLateralDerechaCm = 0f

        // 2) Parseo de cabecera
        val (clase, tipo, modeloCrudo, dims) = parsearPaqueteConDimensiones(paqueteSeguro)
        require(clase == "nova") { "Clase no soportada: $clase" }
        this.modo = if (tipo == "apa") ModoEnsamble.APA else ModoEnsamble.INA

        // Si el paquete trae [ancho,alto], respétalos
        dims?.let { (aw, ah) ->
            if (aw > 0f) this.anchoCm = aw
            if (ah > 0f) this.altoCm  = ah
        }

        // 3) Parseo del modelo (abajo → arriba)
        // El contorno del vano se saca AQUÍ y una sola vez: `parsearModeloConAlturas` se llama a
        // sí mismo para el tramo único, y con el contorno dentro la segunda pasada lo borraba.
        contornoVanoCm = RE_VANO_PAQUETE.find(modeloCrudo)
            ?.let { ContornoEnTramos.desdeEtiqueta(it.value) }
            .orEmpty()
        val franjas = parsearModeloConAlturas(RE_VANO_PAQUETE.replace(modeloCrudo, ""))
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
            sistemaAnchos = franjas[idxS].anchosMod

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
            val sinBracketInicial = resto.removePrefix("[")
            val idxCierre = sinBracketInicial.lastIndexOf(']')
            require(idxCierre >= 0) { "Paquete inválido: falta ']'" }
            val dentro = sinBracketInicial.substring(0, idxCierre)
            val idx = dentro.indexOf(":")
            require(idx > 0) { "Falta ':' después de [ancho,alto]" }
            val dimsTxt = dentro.substring(0, idx)
            val modeloTxt = dentro.substring(idx + 1)

            val dims = dimsTxt.split(",").map { it.trim().replace(",", ".") }
            require(dims.size >= 2) { "Paquete inválido: faltan ancho/alto" }
            val aw = dims[0]
            val ah = dims[1]
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
        modoCUSimétrico = false

        // El contorno del vano ya se leyó en `actualizarDesdePaquete`; aquí solo se quita de en
        // medio, que no es una franja ni un tramo.
        val sinVano = RE_VANO_PAQUETE.replace(modelo, "")

        // Nuevo formato T<> o A<90>T<> (ns en serie): delegar a parsearConTramos
        val modeloNorm = sinVano.replace(" ", "")
        if (modeloNorm.lowercase().let { it.startsWith("t") || it.startsWith("a<") }) {
            return parsearConTramos(modeloNorm)
        }

        val secciones = splitRespetandoParentesis(modeloNorm)
            .filter { it.isNotEmpty() }
            // Los tags del tramo —`H<>` con su alto, `D<>` con lo que baja el dintel— no son
            // franjas: se saltan aquí o el parser los rechaza y el dibujo se cae.
            .filter { val c = it.first().lowercaseChar(); c == 's' || c == 'm' }

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
            val (mods, parantes, anchos) = parsearModulosConParantes(modsSinLR)
            FranjaNova(tipo, mods, altura, parantes, anchos)
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
                // anchos (parsed.third) no aplica para mocheta lateral simple
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
        val modeloSinEsp = modelo.replace(" ", "")
        // Nuevo formato T<>: desenvuelve el primer bloque T<>() y parsea su contenido
        if (modeloSinEsp.lowercase().startsWith("t")) {
            val openParen = modeloSinEsp.indexOf('(')
            if (openParen > 0) {
                var depth = 0
                for (j in openParen until modeloSinEsp.length) {
                    when (modeloSinEsp[j]) {
                        '(' -> depth++
                        ')' -> { depth--; if (depth == 0) return parsearFranjasDesdeModeloCompleto(modeloSinEsp.substring(openParen + 1, j)) }
                    }
                }
            }
        }
        val secciones = splitRespetandoParentesis(modeloSinEsp)
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
            val (mods, parantes, anchos) = parsearModulosConParantes(modTxt)
            FranjaNova(tipo, mods, altura, parantes, anchos)
        }
    }

    private fun parsearModulosConParantes(texto: String): Triple<List<TipoModulo>, List<Int>, List<Float>> {
        val modulos = mutableListOf<TipoModulo>()
        val parantes = mutableListOf<Int>()
        val anchos  = mutableListOf<Float>()
        val reModulo = Regex("""([fcFC])\s*(?:<\s*(-?\d+(?:[.,]\d+)?)\s*>|\(\s*(-?\d+(?:[.,]\d+)?)\s*\))?""")
        val partes = texto.split(Regex(";?p;?", RegexOption.IGNORE_CASE))
        for ((idx, parte) in partes.withIndex()) {
            reModulo.findAll(parte).forEach { m ->
                val tipo = if (m.groupValues[1].lowercase() == "c") TipoModulo.CORREDIZA else TipoModulo.FIJO
                val wRaw = m.groupValues[2].ifBlank { m.groupValues[3] }
                val w = wRaw.replace(',', '.').toFloatOrNull()?.takeIf { it > 0f } ?: 0f
                modulos.add(tipo)
                anchos.add(w)
            }
            if (idx < partes.lastIndex) parantes.add(modulos.size)
        }
        if (modulos.isEmpty()) { modulos.add(TipoModulo.FIJO); anchos.add(0f) }
        return Triple(modulos, parantes, anchos)
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

    // ================= Parseo nuevo formato T<> =================

    /**
     * Divide el cuerpo del modelo (sin espacios) en elementos top-level:
     * T<w>(...), A<angulo>, P<medida>
     */
    private fun splitTopLevelElementos(cuerpo: String): List<String> {
        val result = mutableListOf<String>()
        var i = 0
        while (i < cuerpo.length) {
            when (cuerpo[i].lowercaseChar()) {
                'a', 'p' -> {
                    if (i + 1 < cuerpo.length && cuerpo[i + 1] == '<') {
                        val end = cuerpo.indexOf('>', i + 2)
                        if (end > i) {
                            result.add(cuerpo.substring(i, end + 1))
                            i = end + 1
                        } else i++
                    } else i++
                }
                't' -> {
                    val openParen = cuerpo.indexOf('(', i)
                    if (openParen < 0) { i++; continue }
                    var depth = 0
                    var j = openParen
                    while (j < cuerpo.length) {
                        when (cuerpo[j]) {
                            '(' -> depth++
                            ')' -> { depth--; if (depth == 0) break }
                        }
                        j++
                    }
                    result.add(cuerpo.substring(i, j + 1))
                    i = j + 1
                }
                else -> i++
            }
        }
        return result
    }

    /**
     * Parser para el nuevo formato con tramos: "Tb<w>(s<h>(mods);m<hm>(mods)) P<2.5> Tb<w2>(...) A<90> Tb<w3>(...)"
     * Popula segmentosNs cuando hay más de un tramo o hay separadores A<>.
     * Retorna las franjas del primer bloque T<>.
     * Para un único bloque T<>, delega al parser completo (arc/circular/esquinas).
     */
    /**
     * Un tag de tramo trae una medida o dos: `160` vale para los dos lados y `160,140` es
     * izquierda y derecha, que es como se describe un tramo inclinado.
     */
    private fun medidaIzq(tag: String?): Float =
        tag?.split(",")?.firstOrNull()?.trim()?.replace(",", ".")?.toFloatOrNull() ?: 0f

    private fun medidaDer(tag: String?): Float? =
        tag?.split(",")?.getOrNull(1)?.trim()?.replace(",", ".")?.toFloatOrNull()

    private fun parsearConTramos(cuerpo: String): List<FranjaNova> {
        val elementos = splitTopLevelElementos(cuerpo)

        // Extraer todos los bloques T<> con su tipo de segmento y contenido
        data class BloqueT(val tipo: TipoSegmentoNs, val ancho: Float, val contenido: String)
        val bloques = mutableListOf<BloqueT>()
        var tipoSig = TipoSegmentoNs.PLANO

        for (elem in elementos) {
            val low = elem.lowercase()
            when {
                low.startsWith("a<") -> tipoSig = TipoSegmentoNs.ALETA
                low.startsWith("p<") -> tipoSig = TipoSegmentoNs.PLANO
                low.startsWith("t")  -> {
                    val anchoStr = low.substringAfter("<").substringBefore(">")
                    val anchoTramo = anchoStr.replace(",", ".").toFloatOrNull() ?: anchoCm.coerceAtLeast(1f)
                    val openParen = elem.indexOf('(')
                    val contenido = if (openParen >= 0) {
                        var depth = 0
                        var closeParen = elem.lastIndex
                        for (j in openParen until elem.length) {
                            when (elem[j]) {
                                '(' -> depth++
                                ')' -> { depth--; if (depth == 0) { closeParen = j; break } }
                            }
                        }
                        elem.substring(openParen + 1, closeParen)
                    } else ""
                    bloques.add(BloqueT(tipoSig, anchoTramo.coerceAtLeast(1f), contenido))
                    tipoSig = TipoSegmentoNs.PLANO
                }
            }
        }

        // Caso simple: un solo bloque T<> → delegar al parser completo (maneja arc/circular/etc.).
        // Salvo que ese tramo traiga sus propias medidas: un vano de una sola banda pero inclinado
        // o escalonado necesita el camino de segmentos, que es el que sabe dibujarlo.
        if (bloques.size == 1) {
            val b = bloques[0]
            // Un lado suelto que trae su silueta (`L<…>`, el editor abre la esquina lado a lado):
            // con un solo tramo, esa silueta es la del vano entero.
            if (contornoVanoCm.isEmpty()) {
                RE_CONTORNO_TRAMO.find(b.contenido)?.let { m ->
                    contornoVanoCm = siluetaAlAncho(ContornoEnTramos.desdeEtiqueta(m.value), b.ancho)
                }
            }
            val tieneMedidas = RE_ALTO_TRAMO.containsMatchIn(b.contenido) ||
                RE_CAIDA_TRAMO.containsMatchIn(b.contenido)
            if (!tieneMedidas) return parsearModeloConAlturas(b.contenido)
            val franjas = parsearFranjasDesdeModeloCompleto(b.contenido)
            val alto = RE_ALTO_TRAMO.find(b.contenido)?.groupValues?.get(1)
            val caida = RE_CAIDA_TRAMO.find(b.contenido)?.groupValues?.get(1)
            segmentosNs = listOf(
                SegmentoNs(
                    b.tipo, b.ancho, franjas,
                    altoCm = medidaIzq(alto), caidaCm = medidaIzq(caida),
                    altoDerCm = medidaDer(alto), caidaDerCm = medidaDer(caida)
                )
            )
            return franjas
        }

        // Si algún bloque contiene tag de arco (U<>): fusionar tramos en uno curvo
        val arcoResult = parsearIndicadorArco(bloques.firstOrNull()?.contenido ?: "")
        if (arcoResult.activo) {
            modoArcoCurvo = true
            flechaArcoCm = arcoResult.flechaCm
            val mergedSisMods = mutableListOf<TipoModulo>()
            val mergedParantes = mutableListOf<Int>()
            var altoSistema: Float? = null
            val mergedMochMods = mutableListOf<TipoModulo>()
            var altoMocheta: Float? = null
            for (bloque in bloques) {
                val fbs = parsearFranjasDesdeModeloCompleto(bloque.contenido)
                val sis = fbs.firstOrNull { it.tipo == TipoFranja.SISTEMA }
                if (sis != null) {
                    if (mergedSisMods.isNotEmpty()) mergedParantes.add(mergedSisMods.size)
                    mergedSisMods.addAll(sis.modulos)
                    if (altoSistema == null) altoSistema = sis.alturaCm
                }
                val moch = fbs.firstOrNull { it.tipo == TipoFranja.MOCHETA }
                if (moch != null) {
                    mergedMochMods.addAll(moch.modulos)
                    if (altoMocheta == null) altoMocheta = moch.alturaCm
                }
            }
            val result = mutableListOf<FranjaNova>()
            if (mergedSisMods.isNotEmpty()) result.add(FranjaNova(TipoFranja.SISTEMA, mergedSisMods, altoSistema, mergedParantes))
            if (mergedMochMods.isNotEmpty() && altoMocheta != null) result.add(FranjaNova(TipoFranja.MOCHETA, mergedMochMods, altoMocheta, emptyList()))
            return result.ifEmpty { listOf(FranjaNova(TipoFranja.SISTEMA, listOf(TipoModulo.FIJO), null, emptyList())) }
        }

        // Múltiples bloques → construir segmentosNs
        val segs = mutableListOf<SegmentoNs>()
        var primerFranjas: List<FranjaNova> = emptyList()
        for (bloque in bloques) {
            // `Q<29.3>` dentro del tramo: su panza, la de una pared curva de esquina. Se saca
            // antes de leer las franjas, que si no el parser se encuentra un tag que no es suyo.
            val flecha = RE_CURVA_TRAMO.find(bloque.contenido)
                ?.groupValues?.get(1)?.replace(",", ".")?.toFloatOrNull() ?: 0f
            val contenido = if (flecha > 0f) RE_CURVA_TRAMO.replace(bloque.contenido, "")
            else bloque.contenido
            val franjas = parsearFranjasDesdeModeloCompleto(contenido)
            // `H<106.2>` dentro del tramo: su alto propio, el de la ventana escalonada.
            val altoTramo = RE_ALTO_TRAMO.find(contenido)?.groupValues?.get(1)
            val caidaTramo = RE_CAIDA_TRAMO.find(contenido)?.groupValues?.get(1)
            // `L<…>`: la silueta de la pared que arranca en este tramo, si no es un rectángulo.
            val silueta = RE_CONTORNO_TRAMO.find(contenido)
                ?.let { ContornoEnTramos.desdeEtiqueta(it.value) }.orEmpty()
                .let { siluetaAlAncho(it, bloque.ancho) }
            segs.add(
                SegmentoNs(
                    bloque.tipo, bloque.ancho, franjas,
                    altoCm = medidaIzq(altoTramo), caidaCm = medidaIzq(caidaTramo),
                    altoDerCm = medidaDer(altoTramo), caidaDerCm = medidaDer(caidaTramo),
                    flechaCm = flecha,
                    contornoCm = silueta
                )
            )
            if (primerFranjas.isEmpty()) primerFranjas = franjas
        }

        // De frente va el paño MÁS ANCHO, y el resto en perspectiva: es el que más se ve y el que
        // le da la escala al dibujo. Yendo de frente el primero, una ventana que empieza por su
        // lado corto —o por su curva— salía casi entera escorzada y no se entendía.
        //
        // Los paños que quedan ANTES del de frente doblan hacia la izquierda; de eso se encarga
        // `modoCUSimétrico`, que es lo que ya hacía la ventana en C, donde el de frente es el del
        // medio. Con más de tres paños se deja el zigzag de la serie, que es otra lectura.
        // La serie ("ns") se reconoce porque su paquete arranca con un pliegue, así que su primer
        // paño ya viene de aleta: esa lleva su propio zigzag y se deja como está. Lo demás —la L,
        // la C, la ventana de esquina— se lee alrededor de su paño de frente.
        val dobla = segs.any { it.tipo == TipoSegmentoNs.ALETA }
        val esSerie = segs.firstOrNull()?.tipo == TipoSegmentoNs.ALETA
        if (dobla && !esSerie) {
            // Y de frente va el más ancho de lo que SE VE: una pared curva ocupa 2/π de su
            // desarrollo, así que no se lleva el sitio de frente solo por ser larga. Las curvas
            // son la esquina, no la cara de la ventana.
            val frontal = segs.indices.maxByOrNull {
                if (segs[it].flechaCm > 0f) segs[it].anchoCm * ANCHO_VISTO_DE_LA_CURVA
                else segs[it].anchoCm
            } ?: 0
            for (i in segs.indices) {
                segs[i] = segs[i].copy(
                    tipo = if (i == frontal) TipoSegmentoNs.PLANO else TipoSegmentoNs.ALETA
                )
            }
            modoCUSimétrico = frontal > 0
        }
        // Patrón "ns" (en serie): paquete con A<90> inicial → todos bloques como ALETA.
        // Reclasificar alternando: par=ALETA (perspectiva), impar=PLANO (frontal).
        else if (segs.size >= 2 && segs.all { it.tipo == TipoSegmentoNs.ALETA }) {
            for (i in segs.indices) {
                segs[i] = segs[i].copy(
                    tipo = if (i % 2 == 0) TipoSegmentoNs.ALETA else TipoSegmentoNs.PLANO
                )
            }
        }

        segmentosNs = segs
        return primerFranjas.ifEmpty {
            listOf(FranjaNova(TipoFranja.SISTEMA, emptyList(), null, emptyList()))
        }
    }

    /**
     * Ancho visual efectivo del dibujo en cm.
     * PLANO: ancho nominal completo.
     * ALETA (IZQ o DER): ancho visual comprimido por perspectiva con el mismo cap
     * que usa calcularAletaPerspectiva (z ≤ h*0.5 → fracción visual ≤ 25%).
     * Ambos lados usan la misma fórmula simétrica — sin distinción IZQ/DER —
     * para que anchoTotalCm sea exactamente lo que ocupa el dibujo en pantalla.
     */
    /**
     * Lo que sobra del ancho de la ventana después de sumar sus tramos, en cm: es el sitio de los
     * parantes. El reparto del modelo descuenta 2.5 por cada parante (`anchoUtil`), así que dos
     * tramos de 112.2 en una ventana de 226.9 dejan 2.5 entre los dos. Dibujando los tramos
     * pegados, ese trozo quedaba al final como una franja delgada fuera del marco —y con la
     * silueta del vano encima se veía—. Solo vale para tramos planos y rectos: en una ventana
     * que dobla o curva el ancho que se ve no es el que mide.
     */
    private fun huecoDeParantesCm(): Float {
        if (segmentosNs.size < 2) return 0f
        if (segmentosNs.any { it.tipo != TipoSegmentoNs.PLANO || it.flechaCm > 0f }) return 0f
        val suma = segmentosNs.sumOf { it.anchoCm.toDouble() }.toFloat()
        return (anchoCm - suma).coerceAtLeast(0f) / (segmentosNs.size - 1)
    }

    private fun anchoEfectivoCm(): Float {
        val h = altoCm.coerceAtLeast(1f)
        return if (segmentosNs.isNotEmpty()) {
            huecoDeParantesCm() * (segmentosNs.size - 1) + segmentosNs.sumOf { seg ->
                if (seg.flechaCm > 0f) {
                    // La pared curva no ocupa lo que mide: gira, y de un cuarto de vuelta se ve
                    // 2/π de su desarrollo. Contándola entera, el dibujo se salía por la derecha.
                    (seg.anchoCm * ANCHO_VISTO_DE_LA_CURVA).toDouble()
                } else if (seg.tipo == TipoSegmentoNs.PLANO) {
                    seg.anchoCm.toDouble()
                } else {
                    val w = seg.anchoCm
                    val z = (w * 0.95f).coerceAtMost(h * 0.5f)  // mismo cap que calcularAletaPerspectiva
                    val focal = h * 2.2f
                    (1.35f * z * w / (z + focal)).toDouble()
                }
            }.toFloat().coerceAtLeast(1f)
        } else {
            (anchoCm + mochetaLateralCm + mochetaLateralDerechaCm).coerceAtLeast(1f)
        }
    }

    // ================= Export helpers =================
    fun exportarSoloDisenoBitmap(paddingPx: Int = 0): Bitmap {
        val anchoDisp = width - 2 * margenPx
        val altoDisp  = height - 2 * margenPx
        val anchoTotalCm = anchoEfectivoCm()
        val escala = min(anchoDisp / anchoTotalCm, altoDisp / altoCm)

        val x0 = (width  - (anchoTotalCm * escala)) / 2f
        val y0 = (height - (altoCm * escala)) / 2f
        val x1 = x0 + anchoTotalCm * escala
        val y1 = y0 + altoCm * escala
        // Reservar arriba SOLO lo que el arco realmente ocupa al dibujarse (mismo tope
        // que flechaPx en dibujarCurvoNcu). Si no, una flecha grande deja una franja
        // vacía enorme encima y la ventana se ve diminuta.
        val extraTopPx = if (modoArcoCurvo) {
            val altoVentanaPx = (y1 - y0).coerceAtLeast(1f)
            (flechaArcoCm.coerceAtLeast(0f) * escala).coerceIn(0f, altoVentanaPx * 0.35f)
        } else 0f

        // Reservar margen para los recuadros de "vacío" en los lados que correspondan,
        // si no quedarían recortados del bitmap exportado.
        val ev = encuentroVacio.padEnd(4, '1')
        val banda = vacioGapPx + grosorVacioPx(x0, y0, x1, y1)
        val vacIzq = if (ev[3] == '0') banda else 0f
        val vacDer = if (ev[1] == '0') banda else 0f
        // En "adentro" la aleta diverge (sobresale arriba y abajo): reservar ese desborde
        // para que no se recorte en el bitmap exportado. La aleta de L/C/serie viene por
        // segmentosNs; la de mocheta lateral por mochetaL/RFranjas.
        val tieneAleta = mochetaLFranjas.isNotEmpty() || mochetaRFranjas.isNotEmpty() ||
            segmentosNs.isNotEmpty()
        val overAleta = if (direccion != "afuera" && tieneAleta) (y1 - y0) * 0.12f else 0f
        val vacAbajo = maxOf(if (ev[2] == '0') banda else 0f, overAleta)
        val reservaTop = maxOf(extraTopPx, if (ev[0] == '0') banda else 0f, overAleta)

        val w = (x1 - x0 + vacIzq + vacDer + 2 * paddingPx).toInt().coerceAtLeast(1)
        val h = (y1 - y0 + reservaTop + vacAbajo + 2 * paddingPx).toInt().coerceAtLeast(1)

        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        omitirFondoAlExportar = true
        c.translate(-(x0 - paddingPx - vacIzq), -((y0 - reservaTop) - paddingPx))
        draw(c) // reutiliza onDraw (no pinta fondo por el flag)
        omitirFondoAlExportar = false
        return bmp
    }

    // ================== DIBUJO ==================

    /**
     * Calcula n+1 posiciones X (px) para los bordes de los módulos.
     * Si [anchosMod] tiene n valores > 0 los usa proporcionalmente al total;
     * de lo contrario distribuye uniformemente.
     */
    private fun calcularPosicionesX(
        xIni: Float, anchoVentPx: Float, n: Int, anchosMod: List<Float>
    ): FloatArray {
        val pos = FloatArray(n + 1)
        pos[0] = xIni
        pos[n] = xIni + anchoVentPx
        if (n <= 1) return pos
        return if (anchosMod.size == n && anchosMod.all { it > 0f }) {
            val total = anchosMod.sum().coerceAtLeast(0.001f)
            var acum = 0f
            for (i in 0 until n - 1) {
                acum += anchosMod[i]
                pos[i + 1] = xIni + (acum / total) * anchoVentPx
            }
            pos
        } else {
            val ancho = anchoVentPx / n
            for (i in 1 until n) pos[i] = xIni + i * ancho
            pos
        }
    }

    /** Las cotas se apuntan mientras se dibuja y se pintan al final, después de la silueta. */
    private data class Cotas(val x0: Float, val y0: Float, val x1: Float, val y1: Float, val escala: Float)
    private var cotasPendientes: Cotas? = null

    /**
     * La silueta de una pared de la esquina, apuntada mientras se dibuja su tramo y aplicada al
     * final: en cm relativos a su esquina de arriba a la izquierda, que en el papel está en
     * ([x0], [y0]) con esa [escala].
     */
    private data class SiluetaDePared(
        val contornoCm: List<Pair<Float, Float>>,
        val x0: Float,
        val y0: Float,
        val escala: Float
    )
    private val siluetasPendientes = mutableListOf<SiluetaDePared>()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cotasPendientes = null
        siluetasPendientes.clear()
        dibujarContenido(canvas)
        // La silueta del vano manda sobre el reparto: lo que el dibujo saca fuera del hueco medido
        // se recorta y el borde de la forma se traza encima.
        enmascararVano(canvas)
        // Y en una ventana de esquina, cada pared con su propia silueta se recorta igual.
        siluetasPendientes.forEach { enmascararPared(canvas, it) }
        // Las cotas van DESPUÉS de recortar: la del salto de un escalón se dibuja justo en el
        // trozo de vano que no existe, y el recorte se la llevaba por delante.
        cotasPendientes?.let { dibujarCotas(canvas, it.x0, it.y0, it.x1, it.y1, it.escala) }
        if (!omitirFondoAlExportar) {
            siluetasPendientes.forEach { s ->
                val anchoLado = s.contornoCm.maxOf { it.first }
                val altoLado = s.contornoCm.maxOf { it.second }
                dibujarCotasDePoligono(canvas, s.contornoCm, s.x0, s.y0, s.escala, anchoLado, altoLado)
            }
        }
    }

    /** Recorta el dibujo de una pared con su silueta y traza su borde, como [enmascararVano]. */
    private fun enmascararPared(canvas: Canvas, s: SiluetaDePared) {
        val anchoPx = s.contornoCm.maxOf { it.first } * s.escala
        val altoPx = s.contornoCm.maxOf { it.second } * s.escala
        if (anchoPx <= 1f || altoPx <= 1f) return
        val silueta = android.graphics.Path().apply {
            s.contornoCm.forEachIndexed { i, (xCm, yCm) ->
                val px = s.x0 + xCm * s.escala
                val py = s.y0 + yCm * s.escala
                if (i == 0) moveTo(px, py) else lineTo(px, py)
            }
            close()
        }
        val borde = anchoMarcoPx
        val fuera = android.graphics.Path().apply {
            addRect(
                RectF(s.x0 - borde, s.y0 - borde, s.x0 + anchoPx + borde, s.y0 + altoPx + borde),
                android.graphics.Path.Direction.CW
            )
            op(silueta, android.graphics.Path.Op.DIFFERENCE)
        }
        canvas.drawPath(fuera, if (omitirFondoAlExportar) pBorrarVano else pFondo)
        canvas.drawPath(silueta, pMarco)
    }

    /**
     * Recorta el dibujo con la silueta del vano y traza su borde.
     *
     * La forma se guarda aparte del reparto (`V<…>` en el paquete), así que el diseño puede tener
     * un solo tramo rectangular y aun así verse el hueco de verdad: el triángulo, el escalón o el
     * dintel caído. Sin esto, limpiar el diseño para dibujarlo a mano devolvía un rectángulo.
     */
    private fun enmascararVano(canvas: Canvas) {
        if (contornoVanoCm.size < 3 || anchoCm <= 0f || altoCm <= 0f) return
        // El arco y el círculo traen su propio contorno: recortarlos con el polígono los partiría.
        if (modoArcoCurvo || modoCircular) return
        val anchoDisp = width - 2 * margenPx
        val altoDisp = height - 2 * margenPx
        val anchoTotalCm = anchoEfectivoCm()
        if (anchoDisp <= 0f || altoDisp <= 0f) return
        val escala = min(anchoDisp / anchoTotalCm, altoDisp / altoCm)
        val x0 = (width - anchoTotalCm * escala) / 2f
        val y0 = (height - altoCm * escala) / 2f
        val silueta = android.graphics.Path().apply {
            contornoVanoCm.forEachIndexed { i, (xCm, yCm) ->
                val px = x0 + xCm * escala
                val py = y0 + yCm * escala
                if (i == 0) moveTo(px, py) else lineTo(px, py)
            }
            close()
        }
        // Un pelo más que el rectángulo del vano: el marco se traza centrado en el borde, así que
        // media línea cae fuera y si no se pasa de ahí el rectángulo viejo sigue viéndose.
        val borde = anchoMarcoPx
        val fuera = android.graphics.Path().apply {
            addRect(
                RectF(
                    x0 - borde, y0 - borde,
                    x0 + anchoTotalCm * escala + borde, y0 + altoCm * escala + borde
                ),
                android.graphics.Path.Direction.CW
            )
            op(silueta, android.graphics.Path.Op.DIFFERENCE)
        }
        // Al exportar el fondo es transparente: ahí se BORRA en vez de pintar de blanco, o el
        // recorte saldría como un bloque opaco sobre el papel.
        canvas.drawPath(fuera, if (omitirFondoAlExportar) pBorrarVano else pFondo)
        canvas.drawPath(silueta, pMarco)
    }

    private fun dibujarContenido(canvas: Canvas) {
        if (!omitirFondoAlExportar) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), pFondo)
        }
        if (anchoCm <= 0f || altoCm <= 0f) return

        val anchoDisp = width - 2 * margenPx
        val altoDisp  = height - 2 * margenPx
        val anchoTotalCm = anchoEfectivoCm()
        val escala = min(anchoDisp / anchoTotalCm, altoDisp / altoCm)
        // Medio puente: en cada frontera dibujan las DOS franjas que se tocan —una hacia abajo y
        // otra hacia arriba—, así que lo que se ve es el doble de esto.
        altoPuentePx = max(5f, 1.1f * escala)

        val x0 = (width  - anchoTotalCm * escala) / 2f
        val y0 = (height - altoCm * escala) / 2f
        val x1 = x0 + anchoTotalCm * escala
        val y1 = y0 + altoCm * escala
        // El rectángulo que ocupa el dibujo: lo usa el mando para ponerse justo debajo.
        rectDiseno.set(x0, y0, x1, y1)

        // Los rangos del toque son de ESTE dibujo. Sin limpiarlos, un diseño con tramos dejaba los
        // suyos puestos y el siguiente —sin tramos, como el que queda al limpiar— seguía
        // preguntándoles: decían que la franja de arriba no tenía módulos, así que el segundo
        // toque no elegía ninguno y parecía que la selección no llegaba.
        if (segmentosNs.isEmpty()) segmentosPlanoInfo.clear()

        if (segmentosNs.isNotEmpty()) {
            rangosTramoX.clear()
            segmentosPlanoInfo.clear()
            dibujarNs(canvas, x0, y0, y1, escala)
            dibujarCorteVerticalGlobal(canvas, x0, y0, y1, escala, anchoTotalCm)
            dibujarEncuentroVacio(canvas, x0, y0, x1, y1)
            if (!omitirFondoAlExportar) {
                cotasPendientes = Cotas(x0, y0, x1, y1, escala)
            }
            return
        }
        if (modoCircular) {
            rangosTramoX.clear()
            rangosTramoX.add(Pair(x0, x1))
            dibujarCircularNci(canvas, x0, y0, x1, y1, escala)
            dibujarCorteVerticalGlobal(canvas, x0, y0, y1, escala, anchoTotalCm)
            dibujarEncuentroVacio(canvas, x0, y0, x1, y1)
            if (!omitirFondoAlExportar) {
                cotasPendientes = Cotas(x0, y0, x1, y1, escala)
            }
            return
        }
        if (modoArcoCurvo) {
            rangosTramoX.clear()
            rangosTramoX.add(Pair(x0, x1))
            dibujarCurvoNcu(canvas, x0, y0, x1, y1, escala)
            dibujarCorteVerticalGlobal(canvas, x0, y0, y1, escala, anchoTotalCm)
            dibujarEncuentroVacio(canvas, x0, y0, x1, y1)
            if (!omitirFondoAlExportar) {
                cotasPendientes = Cotas(x0, y0, x1, y1, escala)
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
        rangosFranjaPorTramo.clear()

        val anchoVentPx = xVentFin - xVentIni
        construirRangosTramoX(xVentIni, xVentFin, anchoVentPx)
        if (modo == ModoEnsamble.APA) {
            dibujarAPA(canvas, xVentIni, xVentFin, y1, anchoVentPx, escala)
        } else {
            dibujarINA(canvas, xVentIni, y0, xVentFin, y1, anchoVentPx, escala)
        }
        dibujarCorteVerticalGlobal(canvas, x0, y0, y1, escala, anchoTotalCm)
        dibujarEncuentroVacio(canvas, x0, y0, x1, y1)

        // Dibujar cotas exteriores (solo si no es exportación)
        if (!omitirFondoAlExportar) {
            cotasPendientes = Cotas(x0, y0, x1, y1, escala)
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
        if (modo == ModoEnsamble.INA) {
            canvas.drawLine(x, y0, x, y1, pLineaIna)
            return
        }
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

        rangosFranjaPorTramo.clear()
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
        rangosFranjaPorTramo.clear()
        var xCursor = xInicio
        var yTopPlanoActual = yTop
        var yBottomPlanoActual = yBottom
        var escalaAcumulada = 1f
        val huecoParanteCm = huecoDeParantesCm()

        segmentosNs.forEachIndexed { idx, segmento ->
            val escalaLocal = (escalaPxPorCm * escalaAcumulada).coerceAtLeast(0.0001f)
            val anchoNominalPx = (segmento.anchoCm * escalaLocal).coerceAtLeast(1f)
            // El sitio del parante entre tramo y tramo: lo que el reparto descontó del ancho.
            val huecoPx = if (idx > 0) huecoParanteCm * escalaLocal else 0f
            xCursor += huecoPx
            val xIni = xCursor
            var xFin = xCursor + anchoNominalPx
            val anchoParante = max(10f, 2.5f * escalaLocal)
            // Cada tramo cuelga del dintel con SU alto: el que no llega tan abajo es el escalón,
            // el trozo de vano donde el alféizar sube.
            fun yArribaDe(seg: SegmentoNs?, derecha: Boolean = false): Float {
                val c = if (derecha) (seg?.caidaDerecha ?: 0f) else (seg?.caidaCm ?: 0f)
                return if (c > 0f) (yTopPlanoActual + c * escalaLocal).coerceAtMost(yBottomPlanoActual)
                else yTopPlanoActual
            }
            fun yAbajoDe(seg: SegmentoNs?, derecha: Boolean = false): Float {
                val h = if (derecha) (seg?.altoDerecho ?: 0f) else (seg?.altoCm ?: 0f)
                return if (h > 0f) (yArribaDe(seg, derecha) + h * escalaLocal).coerceAtMost(yBottomPlanoActual)
                else yBottomPlanoActual
            }
            val yArribaTramo = yArribaDe(segmento)
            val yAbajoTramo = yAbajoDe(segmento)
            if (idx > 0) {
                // El parante solo existe donde los dos tramos se tocan: hasta donde llega el más corto.
                val vecino = segmentosNs.getOrNull(idx - 1)
                val yAbajoParante = min(yAbajoTramo, yAbajoDe(vecino))
                val yArribaParante = max(yArribaTramo, yArribaDe(vecino))
                // Con hueco, el parante lo llena; sin hueco (tramos que suman el ancho entero,
                // como los que salen del contorno) va centrado en la frontera, como siempre.
                val xParante = xIni - huecoPx / 2f
                val medioParante = max(anchoParante, huecoPx) / 2f
                if (modo == ModoEnsamble.INA) {
                    canvas.drawLine(xParante, yArribaParante, xParante, yAbajoParante, pLineaIna)
                } else {
                    canvas.drawRect(
                        RectF(xParante - medioParante, yArribaParante, xParante + medioParante, yAbajoParante),
                        pRellenoNegro
                    )
                }
            }
            // Una pared curva se dibuja curva vaya donde vaya: aunque le toque ir en perspectiva,
            // no es un pliegue —no dobla de golpe, gira— y por el camino de la aleta salía plana.
            // Su giro ya la pone en perspectiva ella sola.
            val tipoDeDibujo =
                if (segmento.flechaCm > 0f) TipoSegmentoNs.PLANO else segmento.tipo
            when (tipoDeDibujo) {

                TipoSegmentoNs.PLANO -> {
                    val esPrimerPlano = segmentosPlanoInfo.isEmpty()
                    // Una pared que gira en escuadra NO ocupa de frente lo que mide: se va
                    // acortando hasta quedarse de canto. Su desarrollo es lo que se corta; lo que
                    // se dibuja es su sombra, que de un cuarto de vuelta sale 2/π de lo medido.
                    // Dibujándola a lo ancho entero, 90 cm de curva ocupaban más que 114 cm de
                    // pared puesta en perspectiva, y el dibujo no se entendía.
                    val curvo = segmento.flechaCm > 0f &&
                        anchoNominalPx > 2f && yAbajoTramo - yArribaTramo > 2f
                    // Hacia dónde gira: siempre alejándose del paño que va de frente, que es el
                    // que manda en el dibujo. La curva que está ANTES del frontal viene de canto y
                    // se abre hacia él; la que está después se va de canto.
                    val frontal = segmentosNs.indexOfFirst { it.tipo == TipoSegmentoNs.PLANO }
                    val haciaLaDerecha =
                        if (frontal >= 0 && idx != frontal) idx > frontal
                        else idx < segmentosNs.lastIndex
                    // Lo que mide el paño estirado, que es como se dibuja antes de girarlo.
                    val xFinPano = xIni + anchoNominalPx
                    if (curvo) xFin = xIni + anchoNominalPx * ANCHO_VISTO_DE_LA_CURVA
                    rangosTramoX.add(Pair(xIni, xFin))
                    // La pared que arranca aquí con su silueta medida: se recorta al final, con
                    // todo dibujado, porque la silueta abarca el lado entero aunque lo partan.
                    if (segmento.contornoCm.size >= 3 && !curvo) {
                        siluetasPendientes.add(
                            SiluetaDePared(segmento.contornoCm, xIni, yArribaTramo, escalaLocal)
                        )
                    }
                    val segIdx = segmentosPlanoInfo.size
                    segmentosPlanoInfo.add(Triple(xIni, xFin, segmento.franjas))
                    val anchoVentPx = xFinPano - xIni
                    // Con los dos lados distintos el tramo es un cuadrilátero: se recorta el dibujo
                    // con su silueta y las franjas de dentro salen cortadas por la inclinación, que
                    // es lo que hace el vidrio al seguir la forma.
                    // Y una pared curva de esquina se dibuja como lo que es: el paño que gira. Su
                    // silueta se estrecha hacia la punta donde la pared ya se va de canto, con los
                    // rieles curvados, y de ahí sale la pared siguiente en perspectiva. No es la
                    // panza de `U<>`, que es la ventana curva entera, con sus dos cantos de frente.
                    // El paño que gira no se recorta: se DOBLA entero. Se dibuja aparte, como si
                    // fuera de frente, y se pega tira a tira encogiendo hacia la punta por donde
                    // la pared se va de canto. Así el riel, el travesaño de la mocheta, los
                    // parantes y el vidrio giran todos juntos; recortando la silueta, las líneas
                    // de dentro se quedaban rectas y el paño salía partido.
                    val capa = if (curvo) android.graphics.Bitmap.createBitmap(
                        anchoNominalPx.toInt().coerceAtLeast(1),
                        (yAbajoTramo - yArribaTramo).toInt().coerceAtLeast(1),
                        android.graphics.Bitmap.Config.ARGB_8888
                    ) else null
                    val lienzo = if (capa != null) {
                        Canvas(capa).also { it.translate(-xIni, -yArribaTramo) }
                    } else canvas
                    val recorte = when {
                        curvo -> null
                        segmento.esInclinado -> android.graphics.Path().apply {
                            moveTo(xIni, yArribaTramo)
                            lineTo(xFin, yArribaDe(segmento, derecha = true))
                            lineTo(xFin, yAbajoDe(segmento, derecha = true))
                            lineTo(xIni, yAbajoTramo)
                            close()
                        }
                        else -> null
                    }
                    if (recorte != null) {
                        canvas.save()
                        canvas.clipPath(recorte)
                    }
                    lienzo.drawRect(RectF(xIni, yArribaTramo, xFinPano, yAbajoTramo), pMarco)
                    if (modo == ModoEnsamble.APA) {
                        dibujarAPASoloFranja(
                            canvas = lienzo,
                            franjas = segmento.franjas,
                            xIni = xIni,
                            xFin = xFinPano,
                            yBotTotal = yAbajoTramo,
                            anchoVentPx = anchoVentPx,
                            escalaPxPorCm = escalaLocal,
                            populateRangosFranjas = esPrimerPlano,
                            segmentoIndex = segIdx,
                            altoTramoCm = segmento.altoCm
                        )
                    } else {
                        dibujarINASoloFranja(
                            canvas = lienzo,
                            franjas = segmento.franjas,
                            xIni = xIni,
                            yTopTotal = yArribaTramo,
                            xFin = xFinPano,
                            yBotTotal = yAbajoTramo,
                            anchoVentPx = anchoVentPx,
                            escalaPxPorCm = escalaLocal,
                            populateRangosFranjas = esPrimerPlano,
                            segmentoIndex = segIdx,
                            altoTramoCm = segmento.altoCm
                        )
                    }
                    if (recorte != null) {
                        canvas.restore()
                        // El contorno del cuadrilátero se dibuja fuera del recorte para que se vea
                        // entero, incluida la línea inclinada.
                        canvas.drawPath(recorte, pMarco)
                    }
                    if (capa != null) {
                        pegarPanoQueGira(
                            canvas, capa, xIni, xFin, yArribaTramo, yAbajoTramo, haciaLaDerecha
                        )
                        capa.recycle()
                    }
                    ultimaVentanaX0 = xIni
                    ultimaVentanaX1 = xFin
                }
                TipoSegmentoNs.ALETA -> {
                    // Aleta izquierda (IZQ) solo en el patrón C/U simétrico ("nu") para el primer segmento
                    // Todo lo que queda ANTES del paño de frente dobla hacia la izquierda, no
                    // solo el primero: en una C con las esquinas curvas, delante del centro hay
                    // una pared y su curva, y con el segundo doblando a la derecha la ventana se
                    // abría al revés y no parecía una C.
                    val indiceFrontal = segmentosNs.indexOfFirst { it.tipo == TipoSegmentoNs.PLANO }
                    val esAletaIzq = indiceFrontal > 0 && idx < indiceFrontal

                    val ladoAleta = if (esAletaIzq) LadoAleta.IZQ else LadoAleta.DER
                    // Para IZQ: xUnion = xIni + ancho visual efectivo (igual que el borde exterior DER).
                    // Así el trapecio ocupa [xIni, xUnion] sin espacio vacío a la izquierda.
                    val xUnionAleta = if (esAletaIzq) {
                        val alturaActual = (yBottomPlanoActual - yTopPlanoActual).coerceAtLeast(1f)
                        // Mismo cap que calcularAletaPerspectiva para que xUnion coincida
                        // exactamente con el exterior del trapecio → sin espacio vacío a la izquierda
                        val zPxEst = (anchoNominalPx * 0.95f).coerceAtMost(alturaActual * 0.5f)
                        val focalEst = (alturaActual * 2.2f).coerceIn(320f, 2200f)
                        val kEst = zPxEst / (zPxEst + focalEst)
                        xIni + (1.35f * anchoNominalPx * kEst).coerceAtLeast(1f)
                    } else xIni
                    // Detrás de una pared curva, la aleta arranca DONDE LA CURVA DEJÓ EL PAÑO. La
                    // curva entrega su canto ya girado, más alto que el frente; recogiéndolo a la
                    // altura del frente quedaba un escalón y la pared parecía metida detrás de la
                    // curva en vez de seguirla.
                    // La curva entrega (y recoge) su canto ya girado, más alto que el frente. La
                    // pared que se pega a ella tiene que arrancar a esa altura, venga la curva
                    // detrás o delante: si no, en la unión quedaba un escalón del 16% y esa pared
                    // con su curva parecían otra ventana pegada a un lado.
                    val siguiente = segmentosNs.getOrNull(idx + 1)?.flechaCm ?: 0f
                    val anterior = segmentosNs.getOrNull(idx - 1)?.flechaCm ?: 0f
                    val vieneDeCurva = anterior > 0f || (esAletaIzq && siguiente > 0f)
                    val centroAleta = (yTopPlanoActual + yBottomPlanoActual) * 0.5f
                    val medioAleta = (yBottomPlanoActual - yTopPlanoActual) * 0.5f *
                        (if (vieneDeCurva) altoEnLaPunta else 1f)
                    val yTopAleta = centroAleta - medioAleta
                    val yBottomAleta = centroAleta + medioAleta
                    val perspectiva = calcularAletaPerspectiva(
                        lado = ladoAleta,
                        xUnion = xUnionAleta,
                        yTop = yTopAleta,
                        yBottom = yBottomAleta,
                        anchoAletaPx = anchoNominalPx
                    )
                    if (modo == ModoEnsamble.APA) {
                        dibujarAletaPerspectivaAPA(
                            canvas = canvas,
                            franjas = segmento.franjas,
                            lado = ladoAleta,
                            xUnion = xUnionAleta,
                            yTop = yTopAleta,
                            yBottom = yBottomAleta,
                            anchoAletaPx = anchoNominalPx,
                            escalaPxPorCm = escalaLocal
                        )
                    } else {
                        dibujarAletaPerspectivaINA(
                            canvas = canvas,
                            franjas = segmento.franjas,
                            lado = ladoAleta,
                            xUnion = xUnionAleta,
                            yTop = yTopAleta,
                            yBottom = yBottomAleta,
                            anchoAletaPx = anchoNominalPx,
                            escalaPxPorCm = escalaLocal
                        )
                    }
                    if (esAletaIzq) {
                        // Aleta izquierda: xCursor avanza hasta la unión (borde der del espacio nominal)
                        xFin = xUnionAleta
                        // No se actualiza escala ni Y: el panel central usa el mismo rango vertical
                    } else {
                        val xExterior = (perspectiva.bordeExteriorTop.x + perspectiva.bordeExteriorBottom.x) * 0.5f
                        xFin = max(xIni + 1f, xExterior)
                        val alturaAntes = (yBottomAleta - yTopAleta).coerceAtLeast(1f)
                        val nuevoTop = perspectiva.bordeExteriorTop.y
                        val nuevoBottom = perspectiva.bordeExteriorBottom.y
                        val alturaDespues = (nuevoBottom - nuevoTop).coerceAtLeast(1f)
                        val ratio = (alturaDespues / alturaAntes).coerceIn(0.35f, 1f)
                        escalaAcumulada = (escalaAcumulada * ratio).coerceAtLeast(0.15f)
                        yTopPlanoActual = nuevoTop
                        yBottomPlanoActual = nuevoBottom
                    }
                }
            }
            xCursor = xFin
        }
    }

    /**
     * La silueta de una pared curva vista de frente, para recortar su paño.
     *
     * Los dos rieles se comban hacia arriba y los cantos se quedan en su sitio: es la pared que
     * se va de la vertical. El de abajo comba menos que el de arriba, que es como se ve una
     * curva mirada desde la altura de los ojos —el mismo criterio que el curvo de toda la
     * ventana—.
     */
    /**
     * Cuánto mide de alto la punta por donde gira el paño, comparada con lo que mide de frente.
     *
     * Gira del mismo lado que su aleta, y eso lo decide [direccion] igual que en
     * `proyectarPerspectiva`: "adentro" —lo normal— la pared se acerca al que mira y CRECE;
     * "afuera" se aleja y mengua. Yendo al revés que la aleta, la esquina se leía como si cada
     * mitad de la ventana se mirara desde un lado distinto.
     */
    private val altoEnLaPunta: Float
        get() = if (direccion == "afuera") 0.84f else 1.16f

    /**
     * Qué parte de una pared curva se ve de frente: un cuarto de vuelta se ve 2/π de lo que mide.
     *
     * El desarrollo es lo que se corta; esto es lo que ocupa en el papel. Sale de sumar el coseno
     * del giro a lo largo del arco: al principio la pared está de cara y se ve entera, al final
     * está de canto y no se ve nada.
     */
    private val ANCHO_VISTO_DE_LA_CURVA = (2.0 / Math.PI).toFloat()

    /**
     * Cuánto se ha visto del paño hasta [u], de 0 a 1, repartido como se ve y no a partes iguales.
     *
     * Es la integral del coseno del giro: `sen(u·π/2)`. Los trozos de cerca, todavía de cara, se
     * llevan casi todo el sitio; los de la punta, ya de canto, se aprietan.
     */
    private fun anchoVistoHasta(u: Float, haciaLaDerecha: Boolean): Float {
        val t = u.coerceIn(0f, 1f)
        val visto = kotlin.math.sin(t * (Math.PI.toFloat() / 2f))
        return if (haciaLaDerecha) visto else 1f - kotlin.math.sin((1f - t) * (Math.PI.toFloat() / 2f))
    }


    /** Lo que crece o mengua el paño en cada punto, de 1 de frente a [altoEnLaPunta] en la punta. */
    private fun altoAlGirar(t: Float): Float {
        // Cuarto de círculo: al principio apenas gira y al final se va de golpe, que es como se
        // escorza una esquina redondeada.
        val giro = 1f - kotlin.math.cos(t.coerceIn(0f, 1f) * (Math.PI.toFloat() / 2f))
        return 1f + (altoEnLaPunta - 1f) * giro
    }

    /**
     * Pega el paño de una pared curva doblado: tira a tira, cada una encogida lo que le toca.
     *
     * Se dibuja de frente en su propia capa y se pega girando, así que todo lo que lleva dentro
     * —rieles, travesaños, parantes y vidrio— gira con él.
     */
    private fun pegarPanoQueGira(
        canvas: Canvas,
        capa: android.graphics.Bitmap,
        x0: Float,
        x1: Float,
        yTop: Float,
        yBottom: Float,
        haciaLaDerecha: Boolean
    ) {
        val tiras = 120
        val centro = (yTop + yBottom) * 0.5f
        val medio = (yBottom - yTop) * 0.5f
        val src = android.graphics.Rect()
        val dst = RectF()
        for (i in 0 until tiras) {
            val u0 = i / tiras.toFloat()
            val u1 = (i + 1) / tiras.toFloat()
            val sx0 = (capa.width * u0).toInt()
            val sx1 = (capa.width * u1).toInt().coerceAtLeast(sx0 + 1).coerceAtMost(capa.width)
            if (sx0 >= capa.width) break
            src.set(sx0, 0, sx1, capa.height)
            val t = (u0 + u1) * 0.5f
            val giro = if (haciaLaDerecha) t else 1f - t
            val k = altoAlGirar(giro)
            dst.set(
                x0 + (x1 - x0) * anchoVistoHasta(u0, haciaLaDerecha),
                centro - medio * k,
                x0 + (x1 - x0) * anchoVistoHasta(u1, haciaLaDerecha),
                centro + medio * k
            )
            canvas.drawBitmap(capa, src, dst, null)
        }
        // Y el contorno encima, para que los rieles curvados se lean limpios.
        canvas.drawPath(siluetaDeEsquinaCurva(x0, x1, yTop, yBottom, haciaLaDerecha), pMarco)
    }

    private fun siluetaDeEsquinaCurva(

        x0: Float,
        x1: Float,
        yTop: Float,
        yBottom: Float,
        haciaLaDerecha: Boolean
    ): android.graphics.Path {
        val pasos = 24
        val alto = (yBottom - yTop).coerceAtLeast(1f)
        val centro = (yTop + yBottom) * 0.5f
        // `u` recorre el paño estirado; lo que se ve de él no va parejo, así que la silueta se
        // dibuja sobre el mismo reparto con el que se pega.
        fun px(u: Float): Float = x0 + (x1 - x0) * anchoVistoHasta(u, haciaLaDerecha)
        fun medioAlto(u: Float): Float =
            (alto * 0.5f) * altoAlGirar(if (haciaLaDerecha) u else 1f - u)
        return android.graphics.Path().apply {
            moveTo(px(0f), centro - medioAlto(0f))
            for (i in 1..pasos) {
                val u = i / pasos.toFloat()
                lineTo(px(u), centro - medioAlto(u))
            }
            for (i in pasos downTo 0) {
                val u = i / pasos.toFloat()
                lineTo(px(u), centro + medioAlto(u))
            }
            close()
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
            registrarFranjaDeTramo(0, yCurva(yTopNom, centroX), yCurva(yBottomNom, centroX))

            // Cada frontera entre franjas lleva su puente: una banda gruesa, no una linea fina. Antes
            // solo se pintaba donde el sistema tocaba una mocheta; en el diseno a mano hay franjas
            // seguidas del mismo tipo, y ahi tambien hay puente.
            val hayMAbajo = idx > 0
            val hayMArriba = idx < franjas.lastIndex
            if (hayMAbajo) dibujarBandaCurva(yBottomNom - altoPuentePx, yBottomNom, pRellenoNegro)
            if (hayMArriba) dibujarBandaCurva(yTopNom, yTopNom + altoPuentePx, pRellenoNegro)

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
            // También como banda del tramo 0: así el toque encuentra las franjas por tramo en
            // los dibujos de una sola ventana, igual que en los de varios tramos.
            registrarFranjaDeTramo(0, yTop, yBottom)

            // Puentes M↔S (debajo)
            // Cada frontera entre franjas lleva su puente: una banda gruesa, no una linea fina. Antes
            // solo se pintaba donde el sistema tocaba una mocheta; en el diseno a mano hay franjas
            // seguidas del mismo tipo, y ahi tambien hay puente.
            val hayMAbajo = idx > 0
            val hayMArriba = idx < franjasAbajoArriba.lastIndex
            if (hayMAbajo) canvas.drawRect(RectF(xIni, yBottom - altoPuentePx, xFin, yBottom), pRellenoNegro)
            if (hayMArriba) canvas.drawRect(RectF(xIni, yTop, xFin, yTop + altoPuentePx), pRellenoNegro)

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
                parantesXPx = parantesXPx,
                anchosMod = franja.anchosMod
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
            // Cada frontera entre franjas lleva su puente, del tipo que sean.
            val hayMAbajo = idx > 0
            val hayMArriba = idx < franjas.lastIndex
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
        val alturaPanel = (yBottom - yTop).coerceAtLeast(1f)
        // Cap: garantiza que la aleta siempre aparezca claramente en perspectiva
        // sin importar que tan ancha sea. k_max ≈ 0.185 → fracción visual ≤ 25%.
        val zPx = (anchoAletaPx * factorProfundidad * 0.95f).coerceAtMost(alturaPanel * 0.5f)
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
        // "afuera": el borde exterior converge al centro (se aleja). "adentro" (default):
        // diverge (top sube, bottom baja) dando la sensación de acercarse al observador.
        val signoY = if (direccion == "afuera") 1f else -1f
        return PointF(
            base.x + ((xVp - base.x) * k),
            base.y + ((yVp - base.y) * kY * signoY)
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
        escalaPxPorCm: Float,
        populateRangosFranjas: Boolean = false,
        segmentoIndex: Int = -1,
        altoTramoCm: Float = 0f
    ) {
        if (franjas.isEmpty()) return
        val alturasCm = distribuirAlturas(franjas, if (altoTramoCm > 0f) altoTramoCm else altoCm)
        var yAbajo = yBotTotal
        val parantesX = mutableListOf<Float>()
        franjas.forEachIndexed { idx, franja ->
            val altoFpx = alturasCm.getOrElse(idx) { 0f } * escalaPxPorCm
            val yArriba = yAbajo - altoFpx
            val yTop = yArriba
            val yBottom = yAbajo
            if (populateRangosFranjas) rangosFranjaY.add(Pair(yTop, yBottom))
            registrarFranjaDeTramo(segmentoIndex, yTop, yBottom)

            // Cada frontera entre franjas lleva su puente: una banda gruesa, no una linea fina. Antes
            // solo se pintaba donde el sistema tocaba una mocheta; en el diseno a mano hay franjas
            // seguidas del mismo tipo, y ahi tambien hay puente.
            val hayMAbajo = idx > 0
            val hayMArriba = idx < franjas.lastIndex
            if (hayMAbajo) canvas.drawRect(RectF(xIni, yBottom - altoPuentePx, xFin, yBottom), pRellenoNegro)
            if (hayMArriba) canvas.drawRect(RectF(xIni, yTop, xFin, yTop + altoPuentePx), pRellenoNegro)

            canvas.drawLine(xIni, yTop, xFin, yTop, pLinea)
            canvas.drawLine(xIni, yBottom, xFin, yBottom, pLinea)
            val n = franja.modulos.size.coerceAtLeast(1)
            val xPos = calcularPosicionesX(xIni, anchoVentPx, n, franja.anchosMod)
            val parantesSet = franja.parantePosiciones.toSet()
            for (i in 1 until n) {
                if (i in parantesSet) continue
                canvas.drawLine(xPos[i], yTop, xPos[i], yBottom, pLinea)
            }
            canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pLinea)
            if (franja.tipo == TipoFranja.SISTEMA && franja.parantePosiciones.isNotEmpty()) {
                for (pos in franja.parantePosiciones) {
                    if (pos in 1 until n) parantesX.add(xPos[pos])
                }
            }

            when (franja.tipo) {
                TipoFranja.SISTEMA -> {
                    val yZBotGlobal = if (hayMAbajo) (yBottom - altoPuentePx) else yBottom
                    val yTGlass = if (hayMArriba) (yTop + altoPuentePx) else yTop
                    for (i in 0 until n) {
                        val xM0 = xPos[i]; val xM1 = xPos[i + 1]
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
                        val xM0 = xPos[i]; val xM1 = xPos[i + 1]
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

            // Resaltado de franja activa (en modo segmentos)
            val tramoOkApa = segmentoIndex < 0 || indiceTramoResaltado < 0 || segmentoIndex == indiceTramoResaltado
            if (idx == indiceFranjaResaltada && tramoOkApa) {
                canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pResaltaRelleno)
                canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pResaltaBorde)
            }
            if (idx == indiceFranjaResaltada && tramoOkApa && indiceModuloResaltado in 0 until n) {
                val xM0 = xPos[indiceModuloResaltado]
                val xM1 = xPos[indiceModuloResaltado + 1]
                canvas.drawRect(RectF(xM0, yTop, xM1, yBottom), pResaltaModuloRelleno)
                canvas.drawRect(RectF(xM0, yTop, xM1, yBottom), pResaltaModuloBorde)
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
        parantesXPx: List<Float>,
        anchosMod: List<Float> = emptyList()
    ): FloatArray {
        val xs = FloatArray(nModulos + 1)
        val cortes = parantePosiciones.filter { it in 1 until nModulos }.distinct().sorted()
        if (cortes.isEmpty() || parantesXPx.size < cortes.size) {
            // sin parantes: repartir por ancho de módulo (<w>); uniforme si no hay anchos.
            return calcularPosicionesX(xIni, xFin - xIni, nModulos, anchosMod)
        }
        // límites de cada tramo en px
        val xLimites = listOf(xIni) + parantesXPx.take(cortes.size) + listOf(xFin)
        // límites de cada tramo en índices de módulo
        val mLimites = listOf(0) + cortes + listOf(nModulos)
        for (t in 0 until xLimites.lastIndex) {
            val x0 = xLimites[t]; val x1 = xLimites[t + 1]
            val m0 = mLimites[t]; val m1 = mLimites[t + 1]
            val count = (m1 - m0).coerceAtLeast(1)
            // dentro de cada tramo, repartir por el ancho de sus propios módulos.
            val anchosTramo = if (anchosMod.size == nModulos) anchosMod.subList(m0, m1) else emptyList()
            val xsTramo = calcularPosicionesX(x0, x1 - x0, count, anchosTramo)
            for (m in 0..count) xs[m0 + m] = xsTramo[m]
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
        escalaPxPorCm: Float,
        populateRangosFranjas: Boolean = false,
        segmentoIndex: Int = -1,
        altoTramoCm: Float = 0f
    ) {
        if (franjas.isEmpty()) return
        val idxS = franjas.indexOfFirst { it.tipo == TipoFranja.SISTEMA }
        if (idxS < 0) return

        val sistema = franjas[idxS]
        val sistemaMods = sistema.modulos
        if (sistemaMods.isEmpty()) return

        val alturasFallback = distribuirAlturas(franjas, if (altoTramoCm > 0f) altoTramoCm else altoCm)
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
        if (populateRangosFranjas) rangosFranjaY.add(Pair(yTop, yBottom))
        // Una banda por franja, no una sola para el tramo entero: sin esto, en INA no se puede
        // seleccionar una mocheta por separado.
        run {
            var yAbajoBanda = yBottom
            franjas.forEachIndexed { i, f ->
                val hCm = (f.alturaCm ?: alturasFallback.getOrElse(i) { 0f }).coerceAtLeast(0f)
                val hPx = hCm * escalaPxPorCm
                registrarFranjaDeTramo(segmentoIndex, yAbajoBanda - hPx, yAbajoBanda)
                yAbajoBanda -= hPx
            }
        }

        canvas.drawLine(xIni, yTop, xFin, yTop, pLinea)
        canvas.drawLine(xIni, yBottom, xFin, yBottom, pLinea)
        canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pLinea)

        val n = sistemaMods.size
        val xs = calcularPosicionesX(xIni, anchoVentPx, n, sistema.anchosMod)

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

        // Resaltado de franja activa (en modo segmentos)
        val tramoOkIna = segmentoIndex < 0 || indiceTramoResaltado < 0 || segmentoIndex == indiceTramoResaltado
        if (indiceFranjaResaltada == 0 && tramoOkIna) {
            canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pResaltaRelleno)
            canvas.drawRect(RectF(xIni, yTop, xFin, yBottom), pResaltaBorde)
        }
        if (indiceFranjaResaltada == 0 && tramoOkIna && indiceModuloResaltado in 0 until n) {
            val xM0 = xs[indiceModuloResaltado]
            val xM1 = xs[indiceModuloResaltado + 1]
            canvas.drawRect(RectF(xM0, yTop, xM1, yBottom), pResaltaModuloRelleno)
            canvas.drawRect(RectF(xM0, yTop, xM1, yBottom), pResaltaModuloBorde)
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
    /**
     * Reparte el alto entre las franjas. [altoTotal] es el del TRAMO: en una ventana escalonada
     * no es el de la ventana, y midiendo contra ella las franjas del tramo bajo se estiraban hasta
     * llenar los 160 y el tramo se dibujaba fuera de su sitio.
     */
    private fun distribuirAlturas(
        franjas: List<FranjaNova>,
        altoTotal: Float = altoCm
    ): MutableList<Float> {
        val altoCm = if (altoTotal > 0f) altoTotal else this.altoCm
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

        // Sin alturas explícitas: con DOS franjas manda la proporción de siempre —5/7 el sistema
        // y 2/7 la mocheta—. Con tres o más esa regla deja mochetas raquíticas, así que el alto
        // se reparte en partes iguales; si solo hay un tipo de franja, también.
        if (!hayExp) {
            if (idxS.isEmpty() || idxM.isEmpty() || n > 2) {
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
        if (ceros.isNotEmpty()) {
            val cuota = resto / ceros.size
            ceros.forEach { alturas[it] = cuota }
        } else if (sumaExp > 0f && resto > 0.5f) {
            // Todas las franjas tienen altura explícita pero no llenan el alto: el puente no
            // es una franja, así que su espacio quedaba como hueco arriba. Escalamos para
            // llenar y que la mocheta apoye sobre el puente (sin vacío superior).
            val k = altoCm / sumaExp
            for (i in 0 until n) alturas[i] *= k
        }
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
        // Respeta el ancho por módulo (<w>) en vez de repartir igual; vacío = reparto igual.
        val xs = calcularPosicionesX(xIni, anchoVentPx, n, sistemaAnchos)

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
            canvas.drawLine(xs[i], yA, xs[i], yB, pLineaIna)
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
        // Con un vano que no es rectángulo, cada lado lleva su cota pegada a la forma y las dos
        // totales se apartan para no montarse encima.
        val conLados = contornoConLados()
        val aparte = if (conLados) 34f else 0f
        val offsetH = 40f + aparte // separación horizontal
        val offsetV = 35f + aparte // separación vertical
        val flechaLen = 12f // longitud de las flechas
        val espacioTexto = 50f // espacio necesario para el texto rotado

        // Formato de número
        fun fmt(v: Float) = if (v % 1 == 0f) v.toInt().toString() else "%.1f".format(v).replace(",", ".")

        // Cota horizontal (ancho) - abajo
        val yLineaH = y1 + offsetH
        val xCentroH = (x0 + x1) / 2
        val anchoCota = anchoCm + mochetaLateralCm + mochetaLateralDerechaCm
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

        dibujarCotasEscalon(canvas, y0, y1, escala, aparte, conLados)
        if (conLados) dibujarCotasDelContorno(canvas, x0, y0, escala)
    }

    /**
     * ¿El vano tiene lados que contar además del ancho y el alto? Un rectángulo liso no: sus
     * cuatro lados ya están dichos con las dos cotas de siempre. El arco y el círculo tampoco,
     * que traen su propio contorno curvo.
     */
    /**
     * La silueta de una pared, ajustada al ancho del tramo que la lleva. Se mide en obra con el
     * ancho real y el tramo puede venir con el ancho ÚTIL (descontado el esquinero): se estira o
     * encoge a lo ancho lo que haga falta para que llene el tramo, y nada más.
     */
    private fun siluetaAlAncho(silueta: List<Pair<Float, Float>>, anchoTramo: Float): List<Pair<Float, Float>> {
        if (silueta.size < 3 || anchoTramo <= 0f) return silueta
        val anchoSilueta = silueta.maxOf { it.first }
        if (anchoSilueta <= 0f || kotlin.math.abs(anchoSilueta - anchoTramo) < 0.05f) return silueta
        val k = anchoTramo / anchoSilueta
        return silueta.map { (x, y) -> x * k to y }
    }

    private fun contornoConLados(): Boolean {
        // Una pared de la esquina con su propia silueta también lleva las cotas de sus lados.
        if (segmentosNs.any { it.contornoCm.size >= 3 && it.flechaCm <= 0f }) return true
        if (contornoVanoCm.size < 3 || modoArcoCurvo || modoCircular) return false
        if (contornoVanoCm.size != 4) return true
        val izq = contornoVanoCm.minOf { it.first }
        val der = contornoVanoCm.maxOf { it.first }
        val arriba = contornoVanoCm.minOf { it.second }
        val abajo = contornoVanoCm.maxOf { it.second }
        fun cerca(a: Float, b: Float) = kotlin.math.abs(a - b) < 0.05f
        return contornoVanoCm.any { (x, y) ->
            !((cerca(x, izq) || cerca(x, der)) && (cerca(y, arriba) || cerca(y, abajo)))
        }
    }

    /**
     * La cota de cada lado del vano, pegada por fuera de la forma, como la muestra el apunte de
     * medidas. Sin ellas el escalón o la muesca se ven pero no se sabe de cuánto es cada trozo, y
     * es justo lo que hace falta para repartir el diseño a mano. Un lado que mide lo mismo que el
     * ancho o el alto total no se repite: ya está en la cota total.
     */
    private fun dibujarCotasDelContorno(canvas: Canvas, x0: Float, y0: Float, escala: Float) =
        dibujarCotasDePoligono(canvas, contornoVanoCm, x0, y0, escala, anchoEfectivoCm(), altoCm)

    /**
     * La cota de cada lado de un polígono en cm, puesto en el papel en ([x0], [y0]) con esa
     * [escala]. Un lado que mide lo mismo que [anchoTotal] o [altoTotal] no se repite: ya está en
     * la cota total. Sirve para el vano entero y para la silueta de cada pared de una esquina.
     */
    private fun dibujarCotasDePoligono(
        canvas: Canvas,
        contorno: List<Pair<Float, Float>>,
        x0: Float,
        y0: Float,
        escala: Float,
        anchoTotal: Float,
        altoTotal: Float
    ) {
        if (contorno.size < 3) return
        val puntos = contorno.map { (xCm, yCm) -> PointF(x0 + xCm * escala, y0 + yCm * escala) }
        val n = puntos.size
        // Orientación del recorrido, para saber hacia qué lado queda "fuera" en cada tramo.
        var doble = 0f
        for (i in 0 until n) {
            val a = puntos[i]
            val b = puntos[(i + 1) % n]
            doble += a.x * b.y - b.x * a.y
        }
        val signo = if (doble >= 0f) 1f else -1f
        val separacion = 24f
        val flecha = 10f
        fun fmt(v: Float) = if (v % 1 == 0f) v.toInt().toString() else "%.1f".format(v).replace(",", ".")

        for (i in 0 until n) {
            val a = puntos[i]
            val b = puntos[(i + 1) % n]
            val (axCm, ayCm) = contorno[i]
            val (bxCm, byCm) = contorno[(i + 1) % n]
            val medidaCm = sqrt((bxCm - axCm) * (bxCm - axCm) + (byCm - ayCm) * (byCm - ayCm))
            val dx = b.x - a.x
            val dy = b.y - a.y
            val largo = sqrt(dx * dx + dy * dy)
            if (largo < 1f || medidaCm < 0.05f) continue
            val horizontal = kotlin.math.abs(dx) >= kotlin.math.abs(dy)
            // Lo que ya dice la cota total no se repite.
            if (horizontal && kotlin.math.abs(medidaCm - anchoTotal) < 0.05f) continue
            if (!horizontal && kotlin.math.abs(medidaCm - altoTotal) < 0.05f) continue

            val ux = dx / largo
            val uy = dy / largo
            val nx = signo * dy / largo
            val ny = -signo * dx / largo
            val ax = a.x + nx * separacion
            val ay = a.y + ny * separacion
            val bx = b.x + nx * separacion
            val by = b.y + ny * separacion
            // Línea de cota con sus dos llamadas desde las esquinas y las flechas.
            canvas.drawLine(a.x, a.y, a.x + nx * (separacion + 6f), a.y + ny * (separacion + 6f), pLineaCota)
            canvas.drawLine(b.x, b.y, b.x + nx * (separacion + 6f), b.y + ny * (separacion + 6f), pLineaCota)
            canvas.drawLine(ax, ay, bx, by, pLineaCota)
            canvas.drawLine(ax, ay, ax + ux * flecha + nx * flecha / 2f, ay + uy * flecha + ny * flecha / 2f, pLineaCota)
            canvas.drawLine(ax, ay, ax + ux * flecha - nx * flecha / 2f, ay + uy * flecha - ny * flecha / 2f, pLineaCota)
            canvas.drawLine(bx, by, bx - ux * flecha + nx * flecha / 2f, by - uy * flecha + ny * flecha / 2f, pLineaCota)
            canvas.drawLine(bx, by, bx - ux * flecha - nx * flecha / 2f, by - uy * flecha - ny * flecha / 2f, pLineaCota)

            // El número siempre derecho, al lado de fuera de la línea: encima o debajo si el lado
            // es horizontal, a un costado si es vertical.
            val texto = fmt(medidaCm)
            val anchoTexto = pTextoCota.measureText(texto)
            val mx = (ax + bx) / 2f
            val my = (ay + by) / 2f
            val hueco = if (horizontal) 16f else 8f + anchoTexto / 2f
            val tx = mx + nx * hueco
            val ty = my + ny * (if (horizontal) 16f else 8f) + 10f
            canvas.drawText(texto, tx, ty, pTextoCota)
        }
    }

    /**
     * Las cotas que solo tiene una ventana escalonada: el ancho de cada tramo y lo que sube el
     * alféizar en cada salto.
     *
     * Sin ellas el plano no sirve para el taller: se ve la forma, pero no de cuánto es cada trozo.
     * En una ventana recta no se dibuja nada de esto, que ya está dicho con el ancho y el alto.
     */
    private fun dibujarCotasEscalon(
        canvas: Canvas, y0: Float, y1: Float, escala: Float, aparte: Float = 0f, conLados: Boolean = false
    ) {
        val planos = segmentosNs.filter { it.tipo == TipoSegmentoNs.PLANO }
        if (planos.none { it.altoCm > 0f }) return
        if (rangosTramoX.size < planos.size) return

        fun fmt(v: Float) = if (v % 1 == 0f) v.toInt().toString() else "%.1f".format(v).replace(",", ".")
        val flecha = 10f
        val yFila = y1 + 78f + aparte

        planos.forEachIndexed { i, seg ->
            val (xIni, xFin) = rangosTramoX[i]
            // Ancho del tramo, en una segunda fila debajo del ancho total.
            val texto = fmt(seg.anchoCm)
            val ancho = pTextoCota.measureText(texto)
            val centro = (xIni + xFin) / 2f
            canvas.drawLine(xIni, yFila, centro - ancho / 2 - 5f, yFila, pLineaCota)
            canvas.drawLine(centro + ancho / 2 + 5f, yFila, xFin, yFila, pLineaCota)
            canvas.drawLine(xIni, yFila, xIni + flecha, yFila - flecha / 2, pLineaCota)
            canvas.drawLine(xIni, yFila, xIni + flecha, yFila + flecha / 2, pLineaCota)
            canvas.drawLine(xFin, yFila, xFin - flecha, yFila - flecha / 2, pLineaCota)
            canvas.drawLine(xFin, yFila, xFin - flecha, yFila + flecha / 2, pLineaCota)
            canvas.drawText(texto, centro, yFila + 8f, pTextoCota)
        }

        // El salto de cada escalón, en su propia esquina: arriba si lo que baja es el dintel,
        // abajo si lo que sube es el alféizar. Un tramo recortado por los dos lados lleva las dos.
        // Con las cotas de los lados del vano ya puestas, el salto está dicho ahí.
        if (conLados) return
        fun arribaDe(seg: SegmentoNs) =
            if (seg.caidaCm > 0f) (y0 + seg.caidaCm * escala).coerceAtMost(y1) else y0
        fun abajoDe(seg: SegmentoNs) =
            if (seg.altoCm > 0f) (arribaDe(seg) + seg.altoCm * escala).coerceAtMost(y1) else y1

        fun cota(x: Float, ya: Float, yb: Float, medida: Float) {
            if (kotlin.math.abs(ya - yb) < 2f) return
            val yA = min(ya, yb)
            val yB = max(ya, yb)
            canvas.drawLine(x, yA, x, yB, pLineaCota)
            canvas.drawLine(x, yA, x - flecha / 2, yA + flecha, pLineaCota)
            canvas.drawLine(x, yA, x + flecha / 2, yA + flecha, pLineaCota)
            canvas.drawLine(x, yB, x - flecha / 2, yB - flecha, pLineaCota)
            canvas.drawLine(x, yB, x + flecha / 2, yB - flecha, pLineaCota)
            canvas.drawText(fmt(kotlin.math.abs(medida)), x + 6f, (yA + yB) / 2f, pTextoCota)
        }

        for (i in 0 until planos.size - 1) {
            val x = rangosTramoX[i].second + 18f
            cota(x, arribaDe(planos[i]), arribaDe(planos[i + 1]), planos[i].caidaCm - planos[i + 1].caidaCm)
            val altoA = planos[i].caidaCm + (planos[i].altoCm.takeIf { it > 0f } ?: (altoCm - planos[i].caidaCm))
            val altoB = planos[i + 1].caidaCm + (planos[i + 1].altoCm.takeIf { it > 0f } ?: (altoCm - planos[i + 1].caidaCm))
            cota(x, abajoDe(planos[i]), abajoDe(planos[i + 1]), altoA - altoB)
        }
    }

    // ================= SVG: exporta solo el diseño (recortado) =================
    fun exportarSoloDisenoSVG(paddingPx: Int = 0): String {
        // Multi-tramo o multi-cara: el SVG manual no cubre segmentosNs → usar bitmap embebido.
        // También cuando hay lados al vacío (encuentro): el SVG vectorial no los dibuja.
        if (segmentosNs.isNotEmpty() || encuentroVacio.padEnd(4, '1').contains('0')) {
            val bmp = exportarSoloDisenoBitmap(paddingPx)
            val stream = java.io.ByteArrayOutputStream()
            bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream)
            val b64 = android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.NO_WRAP)
            val W = bmp.width; val H = bmp.height
            return """<svg xmlns="http://www.w3.org/2000/svg" width="$W" height="$H" viewBox="0 0 $W $H"><image href="data:image/png;base64,$b64" width="$W" height="$H"/></svg>"""
        }

        val anchoDisp = width - 2 * margenPx
        val altoDisp  = height - 2 * margenPx
        val anchoTotalCm = anchoCm + mochetaLateralCm + mochetaLateralDerechaCm
        val escala = min(anchoDisp / anchoTotalCm, altoDisp / altoCm)
        altoPuentePx = max(5f, 1.1f * escala)

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
                    // Cada frontera entre franjas lleva su puente: una banda gruesa, no una linea fina. Antes
                    // solo se pintaba donde el sistema tocaba una mocheta; en el diseno a mano hay franjas
                    // seguidas del mismo tipo, y ahi tambien hay puente.
                    val hayMAbajo = idx > 0
                    val hayMArriba = idx < mochetaLFranjas.lastIndex
                    if (hayMAbajo) rectFill(x0, yBottom - altoPuentePx, xVentIni, yBottom)
                    if (hayMArriba) rectFill(x0, yTop, xVentIni, yTop + altoPuentePx)
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

                // Cada frontera entre franjas lleva su puente: una banda gruesa, no una linea fina. Antes
                // solo se pintaba donde el sistema tocaba una mocheta; en el diseno a mano hay franjas
                // seguidas del mismo tipo, y ahi tambien hay puente.
                val hayMAbajo = idx > 0
                val hayMArriba = idx < franjasAbajoArriba.lastIndex
                if (hayMAbajo) rectFill(xVentIni, yBottom - altoPuentePx, xVentFin, yBottom)
                if (hayMArriba) rectFill(xVentIni, yTop, xVentFin, yTop + altoPuentePx)

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
                      line(xs[i], yA, xs[i], yB, 1.5f)
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

    /** Guarda la banda de una franja dentro de su tramo, para poder tocarla por separado. */
    private fun registrarFranjaDeTramo(indiceTramo: Int, top: Float, bottom: Float) {
        if (indiceTramo < 0) return
        while (rangosFranjaPorTramo.size <= indiceTramo) rangosFranjaPorTramo.add(mutableListOf())
        rangosFranjaPorTramo[indiceTramo].add(Pair(top, bottom))
    }

    /** La banda (arriba, abajo) de una franja dentro de su tramo, en coordenadas de la vista. */
    fun bandaDeFranja(indiceTramo: Int, indiceFranja: Int): Pair<Float, Float>? =
        rangosFranjaPorTramo.getOrNull(indiceTramo)?.getOrNull(indiceFranja)

    /** El rectángulo que ocupa el dibujo dentro de la vista. */
    fun rectanguloDiseno(): RectF = RectF(rectDiseno)

    /** El ancho (izquierda, derecha) de un tramo en pantalla. */
    fun anchoDeTramo(indiceTramo: Int): Pair<Float, Float>? = rangosTramoX.getOrNull(indiceTramo)

    /**
     * Las bandas de cada franja de un tramo, de abajo arriba. Es lo que mira el toque para saber
     * qué franja se ha tocado; las pruebas lo usan para no depender de coordenadas a ojo.
     */
    @androidx.annotation.VisibleForTesting
    fun bandasDeFranjaParaPruebas(indiceTramo: Int): List<Pair<Float, Float>> =
        rangosFranjaPorTramo.getOrNull(indiceTramo)?.toList() ?: emptyList()

    /** Cuántos segmentos multi-tramo ve el dibujo, y el modelo que le llegó. */
    @androidx.annotation.VisibleForTesting
    /** Cuál de los paños se dibuja de frente y cuáles en perspectiva. */
    fun tramosFrontalesParaPruebas(): List<Boolean> =
        segmentosNs.map { it.tipo == TipoSegmentoNs.PLANO }

    /** La panza de cada tramo, en cm: 0 los rectos. */

    fun flechasDeTramoParaPruebas(): List<Float> = segmentosNs.map { it.flechaCm }

    /** Cuántos vértices tiene la silueta de cada tramo: 0 los que son rectángulo. */
    fun siluetasDeTramoParaPruebas(): List<Int> = segmentosNs.map { it.contornoCm.size }

    /** La silueta del vano que quedó puesta, en cm; vacía si el vano es el rectángulo. */
    fun contornoVanoParaPruebas(): List<Pair<Float, Float>> = contornoVanoCm

    fun diagnosticoParaPruebas(): String
 = "segmentos=${segmentosNs.size} franjas=${franjasAbajoArriba.size} modo=$modo"

    /** El ancho en pantalla de cada tramo, para las pruebas. */
    @androidx.annotation.VisibleForTesting
    fun anchosDeTramoParaPruebas(): List<Pair<Float, Float>> = rangosTramoX.toList()

    /** El tramo cuyo ancho contiene esa x, o -1 si el toque cae fuera. */
    private fun tramoEnX(x: Float): Int {
        for (t in rangosTramoX.indices) {
            val (x0, x1) = rangosTramoX[t]
            if (x >= x0 && x <= x1) return t
        }
        return -1
    }

    /** La pulsación larga sobre una franja abre su mando; el UP posterior ya no selecciona. */
    private var largoYaDisparado = false
    private val avisoLargo = Runnable {
        val i = franjaDownIndex
        if (i >= 0) {
            largoYaDisparado = true
            alClicLargoFranja?.invoke(tramoEnX(xDown), i)
        }
    }
    private var xDown = 0f



    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_CANCEL -> {
                removeCallbacks(avisoLargo)
                largoYaDisparado = false
            }
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                franjaDownIndex = -1
                // Para multi-tramo usar el rango total; para ventana simple usar ultimaVentanaX0/X1
                val xMin = if (segmentosPlanoInfo.isNotEmpty()) segmentosPlanoInfo.first().first else ultimaVentanaX0
                val xMax = if (segmentosPlanoInfo.isNotEmpty()) segmentosPlanoInfo.last().second else ultimaVentanaX1
                if (x >= xMin && x <= xMax) {
                    // Primero el tramo donde cae el dedo, y dentro de él sus franjas: cada tramo tiene
                    // las suyas. Si ese tramo no tiene bandas guardadas se usan las generales, que es
                    // lo que hacen los dibujos de un solo tramo.
                    val bandas = rangosFranjaPorTramo.getOrNull(tramoEnX(x))
                        ?.takeIf { it.isNotEmpty() } ?: rangosFranjaY
                    for (i in bandas.indices) {
                        val (top, bottom) = bandas[i]
                        if (y >= top && y <= bottom) {
                            franjaDownIndex = i
                            xDown = x
                            largoYaDisparado = false
                            removeCallbacks(avisoLargo)
                            postDelayed(avisoLargo, android.view.ViewConfiguration.getLongPressTimeout().toLong())
                            return true // consumir para recibir ACTION_UP
                        }
                    }
                }
                // Fuera del dibujo y con algo elegido: el toque se consume para poder soltarlo en
                // el UP. Sin nada elegido no se toca, para no tragarse gestos que no son nuestros.
                if (haySeleccion()) {
                    franjaDownIndex = TOQUE_FUERA
                    removeCallbacks(avisoLargo)
                    largoYaDisparado = false
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                removeCallbacks(avisoLargo)
                if (largoYaDisparado) {
                    largoYaDisparado = false
                    return true
                }
                val i = franjaDownIndex
                if (i == TOQUE_FUERA) {
                    franjaDownIndex = -1
                    limpiarSeleccion()
                    alTocarFuera?.invoke()
                    return true
                }
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
                        val segInfo = segmentosPlanoInfo.getOrNull(tramo.coerceAtLeast(0))
                        val n = if (segInfo != null) {
                            if (modo == ModoEnsamble.APA) segInfo.third.getOrNull(i)?.modulos?.size ?: 0
                            else segInfo.third.firstOrNull { it.tipo == TipoFranja.SISTEMA }?.modulos?.size ?: 0
                        } else {
                            if (modo == ModoEnsamble.APA) franjasAbajoArriba.getOrNull(i)?.modulos?.size ?: 0
                            else sistemaModulos.size
                        }
                        if (n > 0) {
                            val x0Tramo = segInfo?.first ?: ultimaVentanaX0
                            val x1Tramo = segInfo?.second ?: ultimaVentanaX1
                            val anchoVentPx = x1Tramo - x0Tramo
                            val m = ((x - x0Tramo) / (anchoVentPx / n))
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

    companion object {
        private const val PAQUETE_NOVA_FALLBACK = "{nova,ina,[150,120:s(f)]}"
        /** Marca de que el dedo bajó fuera del dibujo, teniendo algo elegido. */
        private const val TOQUE_FUERA = -2
        /** El alto propio de un tramo dentro de su bloque: `H<106.2>`. */
        private val RE_ALTO_TRAMO = Regex("""[hH]\s*<\s*([\d.,-]+)\s*>""")
        /** Lo que baja el dintel de ese tramo: `D<20>`. */
        private val RE_CAIDA_TRAMO = Regex("""[dD]\s*<\s*([\d.,-]+)\s*>""")
        /**
         * La panza de un tramo curvo: `Q<29.3>`, la flecha en centímetros.
         *
         * Es SOLO de ese tramo, al revés que `U<>`, que curva la ventana entera: una pared curva
         * de una esquina es un paño más entre paños rectos.
         */
        private val RE_CURVA_TRAMO = Regex("""[qQ]\s*<\s*([\d.,]+)\s*>""")
        /** La silueta de la pared de un tramo: `L<x/y|x/y|…>` (la silueta del Lado). */
        private val RE_CONTORNO_TRAMO = Regex("""[lL]\s*<[^>]*>""")
        /** El contorno del vano: `V<x/y|x/y|…>`, en centímetros. */
        private val RE_VANO_PAQUETE = Regex("""[vV]<[\d./|,\s-]*>""")
    }
}



