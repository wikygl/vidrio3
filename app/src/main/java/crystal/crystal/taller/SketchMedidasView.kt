package crystal.crystal.taller

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.RectF
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class SketchMedidasView @JvmOverloads constructor(
    context: android.content.Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private companion object {
        const val TEMPLATE_F1 = "F1"
        const val TEMPLATE_F2 = "F2"
        const val TEMPLATE_F3 = "F3"
        const val TEMPLATE_F4 = "F4"
        const val TEMPLATE_F5 = "F5"
        const val TEMPLATE_F6 = "F6"
        const val TEMPLATE_ROUNDED = "ROUNDED_CORNERS"

        /** Polígono trazado con el lápiz magnético. Se acota y se edita como una forma recurrente. */
        const val TEMPLATE_LIBRE = "LIBRE"

        // ===== Plantilla de puerta =====
        /** El rectángulo de la puerta: manda sobre el puente y sobre los símbolos que lo rodean. */
        const val PUERTA_MARCO = "PUERTA_MARCO"

        /** La línea que cruza el marco de canto a canto (el travesaño, a 200 de piso). */
        const val PUERTA_PUENTE = "PUERTA_PUENTE"

        /** Etiqueta con la medida del michi: se toca para cambiarla, igual que una cota. */
        const val ROL_MICHI = "MICHI"

        /** Rótulo de la puerta ("PUERTA 90 x 240"): se rehace cuando cambia la medida del marco. */
        const val ROL_TITULO_PUERTA = "TITULO_PUERTA"

        /** La línea que parte el vano en dos hojas. */
        const val PUERTA_DIVISION = "PUERTA_DIVISION"

        /** "N° de puertas 1": se toca para pasar de una hoja a dos y volver. */
        const val ROL_HOJAS = "HOJAS_PUERTA"

        val SIMBOLOS_APERTURA = setOf("adentro", "afuera")

        // ===== Plantilla de ventana =====
        /** El rectángulo de la ventana. Manda igual que el marco de la puerta. */
        const val VENTANA_MARCO = "VENTANA_MARCO"

        /** La mampara se toma como la ventana; solo cambian las medidas y que no lleva alfeizar. */
        const val MAMPARA_MARCO = "MAMPARA_MARCO"

        /**
         * Cota de alto tomada por dentro del vano. En un vano ancho el alto del centro no es el de
         * los cantos, y es esa diferencia la que hay que anotar.
         */
        const val VENTANA_ALTO = "VENTANA_ALTO"

        /** Un alto intermedio por cada tramo de 120 cm de ancho. */
        const val TRAMO_ALTO_CM = 120f

        /** "Alfeizar 90": la altura del antepecho, se toca para escribirla. */
        const val ROL_ALFEIZAR = "ALFEIZAR"

        /** "Cotas de alto 1": se toca para añadir una y se mantiene pulsada para quitarla. */
        const val ROL_ALTOS = "ALTOS_VENTANA"
        /**
         * La curva de un TRAMO, en una ventana curva: `⌒ desarrollo|cuerda`.
         *
         * Va centrada en su tramo, y de ahí se sabe de cuál es. No es lo mismo que la curva de una
         * arista: allí la curva es la esquina entre dos paredes rectas; aquí la pared ENTERA es la
         * que va curvada, y una ventana curva es una fila de ellas, cada una con su arco.
         */
        const val ROL_CURVA = "CURVA_TRAMO"

        /** Lo que lleva delante el rótulo de una arista cuando la esquina es curva. */
        const val MARCA_CURVA = "⌒"

        /**
         * La cota tomada A ESCUADRA: de la esquina de un corte al lado de enfrente.
         *
         * Se guarda como una línea suelta que va de la esquina al pie de la escuadra. Al dibujarla
         * se vuelve a buscar su esquina en el contorno de ahora, así que sigue a la forma cuando
         * esta cambia en vez de quedarse clavada donde se puso.
         */
        const val COTA_ESCUADRA = "COTA_ESCUADRA"

        // ===== Ventana de esquina =====
        /**
         * Ventana que dobla. El marco es el DESARROLLO: los tramos estirados uno al lado del otro,
         * como si se abriera la esquina. En perspectiva no hay manera de acotar ni de editar.
         */
        const val ESQUINA_MARCO = "ESQUINA_MARCO"

        /** La arista donde la ventana dobla; lleva su ángulo escrito al lado. */
        const val ESQUINA_QUIEBRE = "ESQUINA_QUIEBRE"

        /**
         * El borde derecho de la banda que ocupa una pared curva en el desarrollo.
         *
         * No es una esquina: ahí no dobla nada, el giro lo hace la curva. Solo cierra el trozo de
         * desarrollo que hay que cortar, así que cuenta como tramo pero no pide ángulo.
         */
        const val ESQUINA_CURVA_FIN = "ESQUINA_CURVA_FIN"

        /** Cota de alto clavada en una arista: es donde los altos suelen diferir (dos paredes). */
        const val VENTANA_ALTO_ESQUINA = "VENTANA_ALTO_ESQUINA"

        /** "Esquina 90°": el ángulo de una arista, se toca para escribirlo. */
        const val ROL_ESQUINA = "ESQUINA_ANGULO"

        /** "N° de tramos 2": se toca para añadir o quitar un tramo de la ventana. */
        const val ROL_TRAMOS = "TRAMOS_ESQUINA"

        val MARCOS_PLANTILLA = setOf(PUERTA_MARCO, VENTANA_MARCO, MAMPARA_MARCO, ESQUINA_MARCO)

        /** Orden de los rótulos anclados arriba: el identificador manda y los contadores le siguen. */
        val ORDEN_ROTULOS = listOf(ROL_TITULO_PUERTA, ROL_TRAMOS, ROL_HOJAS, ROL_ALTOS)

        /** Rótulos que se colocan al pie del marco, centrados. */
        val ROLES_AL_PIE = setOf(ROL_MICHI, ROL_ALFEIZAR)

        /** Símbolos que acompañan al marco y se recolocan con él. */
        val SIMBOLOS_PUERTA = setOf("bisagra", "interior", "exterior", "adentro", "afuera", "michi")

        /** Lo que cambia de estado con un toque: el lado de la bisagra, la vista y la apertura. */
        val SIMBOLOS_ALTERNABLES = mapOf(
            "interior" to "exterior",
            "exterior" to "interior",
            "adentro" to "afuera",
            "afuera" to "adentro"
        )
    }

    data class MedidaPrincipal(
        val anchoCm: Float,
        val altoCm: Float
    )

    enum class Tool {
        NONE, FREEHAND, RECTANGLE, TRIANGLE, CIRCLE, TEXT, LINE, ORTHO_LINE, SELECT,
        /** Lápiz que endereza lo dibujado: ver [crearTrazoImantado]. */
        MAGNET_PEN,
        /**
         * Mueve una esquina suelta en vez de la figura entera, y une el nodo que se suelta encima
         * de otro. Es la forma de rematar a mano lo que el lápiz magnético dejó casi bien.
         */
        NODO
    }

    private enum class Axis {
        HORIZONTAL, VERTICAL
    }

    private enum class CotaType {
        WIDTH, HEIGHT, DIAMETER, LENGTH,
        RECT_TOP, RECT_RIGHT, RECT_BOTTOM, RECT_LEFT,
        COMPOSITE_SIDE,
        F5_DESARROLLO,
        F5_FLECHA,
        F6_RADIO,
        ROUNDED_RADIUS,
        /** Altura del puente sobre el piso de la puerta. */
        PUERTA_ALTURA,
        /** Ancho de cada hoja en la puerta de dos: lo que se le quita a una lo gana la otra. */
        PUERTA_HOJA_IZQ,
        PUERTA_HOJA_DER,
        /** Ancho de un tramo de la ventana de esquina: el total es la suma de los tramos. */
        ESQUINA_TRAMO,
        /**
         * El mismo ancho, pero tocado en la planta: ahí el tramo es una pared entera, así que se
         * mueven sus dos lados —el de arriba y el de abajo— y el descuadre que tuviera se conserva.
         */
        ESQUINA_TRAMO_PLANTA,
        /** Lado de arriba de un tramo: con los altos desiguales va inclinado y mide más que el ancho. */
        ESQUINA_TRAMO_ARRIBA,

        /** De la esquina de un corte al lado de enfrente, a escuadra. Empuja esa arista. */
        A_ESCUADRA
    }

    private sealed class Element {
        data class Freehand(val path: Path) : Element()
        data class Composite(
            val path: Path,
            var widthCm: Float,
            var heightCm: Float,
            val contours: MutableList<MutableList<PointF>>,
            val sideCms: MutableList<MutableList<Float>>,
            val template: String? = null,
            var rotationDeg: Float = 0f,   // ángulo acumulado; para editar cotas en el marco local
            val bloqueados: MutableSet<Long> = mutableSetOf(),  // lados bloqueados (clave contorno+lado)
            var reflejado: Boolean = false,  // reflejada horizontalmente (orden de vértices invertido)
            /**
             * Medidas que el usuario escribió en las cotas, por lado (clave contorno+lado).
             *
             * Las formas recurrentes se insertan con una proporción cualquiera y las medidas reales
             * casi nunca se le parecen. Aplicando cada una en cuanto se escribe, los lados se
             * reparten entre sí y la figura se deforma tanto que las siguientes cotas ya no se
             * pueden ni tocar. Por eso se anotan aquí y la forma se rehace de una sola vez, con
             * TODAS las medidas juntas, en cuanto está completa.
             */
            val declarados: MutableMap<Long, Float> = LinkedHashMap()
        ) : Element()
        data class Shape(
            val tool: Tool,
            val rect: RectF,
            val start: PointF,
            val end: PointF,
            var widthCm: Float,
            var heightCm: Float,
            var diameterCm: Float,
            var lengthCm: Float,
            val topLeft: PointF,
            val topRight: PointF,
            val bottomRight: PointF,
            val bottomLeft: PointF,
            var topCm: Float,
            var rightCm: Float,
            var bottomCm: Float,
            var leftCm: Float,
            val cotaHint: String? = null,
            /**
             * Puente al que se le escribió su propio largo. Desde entonces esa medida es suya y no
             * se vuelve a estirar sola cuando cambia el ancho del marco.
             */
            var largoFijado: Boolean = false
        ) : Element()
        data class Group(val children: MutableList<Element>) : Element()
        data class TextLabel(
            var text: String,
            var x: Float,
            var y: Float,
            var textSize: Float = 34f,
            /**
             * Título de una medida traída del presupuesto o de una plantilla ("Mampara 214 x 273").
             * Se coloca solo encima del dibujo y se mantiene a esa distancia aunque la medida
             * cambie de tamaño; antes se quedaba clavado donde nació y el dibujo se le montaba.
             */
            val titulo: Boolean = false,
            /** El usuario lo arrastró: desde entonces manda su sitio y ya no se recoloca solo. */
            var movida: Boolean = false,
            /**
             * Papel dentro de una plantilla ([ROL_MICHI]). Un texto con papel no es un rótulo
             * cualquiera: se toca para editar su valor y la plantilla lo recoloca con lo demás.
             */
            val rol: String? = null
        ) : Element()
        data class InfoBox(
            var text: String,
            val rect: RectF,
            var textSize: Float = 34f
        ) : Element()
        data class Symbol(
            val drawableName: String,
            val rect: RectF,
            /**
             * Dibujado en espejo. La flecha de la apertura nace en un punto y acaba en punta: la
             * punta va siempre del lado de las bisagras, así que con las bisagras a la derecha hay
             * que darle la vuelta.
             */
            var reflejado: Boolean = false
        ) : Element()
    }

    private data class CotaHit(
        val elementIndex: Int,
        val type: CotaType,
        val rect: RectF,
        val contourIndex: Int? = null,
        val sideIndex: Int? = null
    )

    private data class RecurrenteF4Dims(
        val centralLeft: Float,
        val top: Float,
        val centralW: Float,
        val centralH: Float,
        val leftW: Float,
        val rightW: Float,
        val addonH: Float
    )

    private data class RecurrenteF5Dims(
        val left: Float,
        val top: Float,
        val rectW: Float,
        val rectH: Float,
        val flecha: Float
    )

    private data class RecurrenteF6Dims(
        val left: Float,
        val top: Float,
        val rectW: Float,
        val rectH: Float,
        val radius: Float
    )

    private data class RoundedCornerDims(
        val left: Float,
        val top: Float,
        val rectW: Float,
        val rectH: Float,
        val radii: MutableList<Float>
    )

    private val elementos = mutableListOf<Element>()
    private val rehacerElementos = mutableListOf<Element>()

    // ===== Historial de UNDO/REDO por snapshots (cada acción, no solo inserciones) =====
    private var estadoPrevio: List<Element> = emptyList()  // estado antes de la acción en curso
    private val historia = ArrayDeque<List<Element>>()
    private val futuro = ArrayDeque<List<Element>>()
    private val maxHistorial = 80

    // Long-press sobre una cota → bloquear/desbloquear ese lado (tap normal = editar).
    private var cotaPendiente: CotaHit? = null
    private var cotaDownXY = PointF()
    private var longPressFired = false
    private val longPressRunnable = Runnable {
        cotaPendiente?.let { longPressFired = true; alternarBloqueoLado(it) }
    }

    // Piezas de plantilla que responden al toque con el lienzo en mano (sin herramienta): los
    // símbolos cambian de estado, el michi abre su medida y el puente se quita manteniéndolo.
    private var piezaPendiente: Int? = null
    private var piezaDownXY = PointF()
    private var piezaLongPressFired = false
    private val piezaLongPressRunnable = Runnable {
        piezaPendiente?.let { piezaLongPressFired = true; mantenerPieza(it) }
    }
    private val cotaHits = mutableListOf<CotaHit>()
    private val cotaTextRects = mutableListOf<RectF>()

    // Sitios que ocupan los símbolos en este fotograma. Las cotas los esquivan: una bisagra o una
    // apertura encima del número lo dejaba ilegible, justo en el lado donde hay que leerlo.
    private val simboloRects = mutableListOf<RectF>()
    private val selectedIndices = mutableListOf<Int>()
    private val trazoActual = Path()
    private var startPoint = PointF()
    private var currentPoint = PointF()
    private var dragIndex: Int? = null
    private var lastDragPoint = PointF()
    private var movedSelection = false
    private var dibujando = false
    private var herramienta = Tool.NONE
    private var rectangleRoundedCorner: Int? = null
    private var rectangleRoundedRadiusCm: Float = 0f
    private var fondo: Bitmap? = null
    private var viewScale = 1f
    private var viewOffsetX = 0f
    private var viewOffsetY = 0f
    private var viewportGesture = false
    private var lastViewportFocus = PointF()
    private var lastViewportSpan = 0f
    private var panSinHerramienta = false
    private var lastPanPoint = PointF()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(30, 30, 30)
        style = Paint.Style.STROKE
        strokeWidth = 5f
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val previewPaint = Paint(paint).apply {
        color = Color.rgb(21, 101, 192)
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(14f, 10f), 0f)
    }

    private val selectionPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(216, 27, 96)
        style = Paint.Style.STROKE
        strokeWidth = 3f
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 8f), 0f)
    }

    private val ladoBloqueadoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(229, 57, 53)   // rojo: lado bloqueado
        style = Paint.Style.STROKE
        strokeWidth = 8f
        strokeCap = Paint.Cap.ROUND
    }

    private val cotaLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(80, 88, 96)
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
    }

    private val cotaTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(13, 71, 161)
        style = Paint.Style.FILL
        textSize = spToPx(12f)
        textAlign = Paint.Align.CENTER
    }

    // Cota ya escrita por el usuario pero que la figura todavía no respeta (faltan medidas). Se
    // pinta distinto para que se vea de un vistazo qué falta por anotar.
    private val cotaTextPendientePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(198, 93, 7)
        style = Paint.Style.FILL
        textSize = spToPx(12f)
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    // Guía de la cota que no cupo en su sitio y hubo que apartar: la une con el lado al que
    // pertenece. Sin ella, dos números apartados quedan flotando y no se sabe cuál es de cuál.
    private val cotaGuiaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(150, 158, 166)
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
    }

    private val cotaBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(210, 247, 244, 236)
        style = Paint.Style.FILL
    }

    private val sketchTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(30, 30, 30)
        style = Paint.Style.FILL
        textSize = 34f
    }

    /** Los dibujitos que acompañan a un rótulo, como el alféizar. */
    private val iconoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(30, 30, 30)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    private val fondoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        cotaHits.clear()
        cotaTextRects.clear()
        recogerSimbolos()
        prepararEscalaCotas()
        recolocarBandasSiCambioElZoom()
        reubicarTitulos()
        canvas.save()
        canvas.translate(viewOffsetX, viewOffsetY)
        canvas.scale(viewScale, viewScale)
        drawBackground(canvas)
        elementos.forEachIndexed { index, element -> drawElement(canvas, index, element, true) }
        dibujarPlantasDeEsquina(canvas, true)
        dibujarCotasAEscuadra(canvas, true)
        dibujarImanEscuadra(canvas)
        drawSelection(canvas)
        drawNodos(canvas)
        if (dibujando) {
            canvas.drawPath(trazoActual, if (herramienta == Tool.FREEHAND) paint else previewPaint)
        }
        canvas.restore()
        dibujarAvisoEscuadra(canvas)
    }

    /**
     * El cartel de que la cota a escuadra sigue puesta, clavado arriba de la pantalla.
     *
     * Como la herramienta ya no se apaga al poner una cota, tiene que verse que está prendida: si
     * no, uno toca el dibujo esperando moverlo y lo que sale es otra cota. Va fuera del zoom, en
     * píxeles de pantalla, y no sale en lo que se exporta.
     */
    private fun dibujarAvisoEscuadra(canvas: Canvas) {
        if (!eligiendoEscuadra) return
        val texto = "Cota a escuadra · toca una esquina y arrastra"
        avisoTextoPaint.textSize = spToPx(12f)
        val ancho = avisoTextoPaint.measureText(texto)
        val alto = avisoTextoPaint.fontSpacing
        val margen = spToPx(8f)
        val caja = RectF(
            (width - ancho) / 2f - margen,
            margen,
            (width + ancho) / 2f + margen,
            margen + alto + margen
        )
        canvas.drawRoundRect(caja, margen, margen, avisoFondoPaint)
        canvas.drawText(
            texto, caja.left + margen,
            caja.top + margen - avisoTextoPaint.fontMetrics.top / 1.25f,
            avisoTextoPaint
        )
    }

    private val avisoFondoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E600AFEF"); style = Paint.Style.FILL
    }
    private val avisoTextoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
    }

    /**
     * Las cotas se dibujan sobre el lienzo del apunte, que va escalado por el zoom. Sin compensar,
     * un número de 12sp se ve gigante encima de una figura chica muy ampliada, y se aparta tanto de
     * su lado que ya no se sabe de cuál es —justo lo que pasaba con las formas pequeñas—. Todo lo
     * de la cota (texto, marcas, holguras, separaciones) se mide en píxeles de PANTALLA: se ve
     * igual de grande esté la figura ampliada o no.
     */
    private fun prepararEscalaCotas() {
        escalaCota = 1f / viewScale.coerceAtLeast(0.0001f)
        cotaTextPaint.textSize = ce(spToPx(12f))
        cotaTextPendientePaint.textSize = ce(spToPx(12f))
        cotaLinePaint.strokeWidth = ce(2.5f)
        cotaGuiaPaint.strokeWidth = ce(1.5f)
        ladoBloqueadoPaint.strokeWidth = ce(8f)
    }

    private var escalaCota = 1f

    /** Convierte una medida pensada en píxeles de pantalla al tamaño que hay que dibujar. */
    private fun ce(v: Float) = v * escalaCota

    private fun mundoAPantallaX(x: Float) = x * viewScale + viewOffsetX
    private fun mundoAPantallaY(y: Float) = y * viewScale + viewOffsetY
    private fun pantallaAMundoX(x: Float) = (x - viewOffsetX) / viewScale
    private fun pantallaAMundoY(y: Float) = (y - viewOffsetY) / viewScale

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.pointerCount >= 2 || viewportGesture) {
            removeCallbacks(longPressRunnable); cotaPendiente = null
            removeCallbacks(piezaLongPressRunnable); piezaPendiente = null
            manejarViewportGesture(event)
            return true
        }
        // Esperando esquina para una cota a escuadra: el dedo apunta y el lienzo NO se mueve. Se
        // come los tres eventos —bajar, arrastrar y levantar— porque si no, al arrastrar para
        // afinar la puntería el dibujo se iba con el dedo y no había manera de acertar la esquina.
        // La cota se pone al LEVANTAR, así se puede corregir sin soltar.
        if (eligiendoEscuadra) {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    parent?.requestDisallowInterceptTouchEvent(true)
                    apuntandoEscuadra = screenToWorld(event.x, event.y)
                    arrastrandoEscuadra = screenToWorld(event.x, event.y)
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    parent?.requestDisallowInterceptTouchEvent(true)
                    arrastrandoEscuadra = screenToWorld(event.x, event.y)
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    val desde = apuntandoEscuadra
                    val hasta = screenToWorld(event.x, event.y)
                    apuntandoEscuadra = null
                    arrastrandoEscuadra = null
                    if (desde != null) ponerCotaAEscuadra(desde, hasta)
                    invalidate()
                    return true
                }
                MotionEvent.ACTION_CANCEL -> {
                    apuntandoEscuadra = null
                    arrastrandoEscuadra = null
                    invalidate()
                    return true
                }
            }
        }
        // Cota: tap = editar; mantener presionado = bloquear/desbloquear el lado.
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val p = screenToWorld(event.x, event.y)
                val hit = cotaEn(p.x, p.y)
                if (hit != null) {
                    cotaPendiente = hit
                    cotaDownXY = PointF(event.x, event.y)
                    longPressFired = false
                    postDelayed(longPressRunnable, 450L)
                    return true
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (cotaPendiente != null) {
                    val slop = android.view.ViewConfiguration.get(context).scaledTouchSlop
                    if (kotlin.math.hypot((event.x - cotaDownXY.x).toDouble(), (event.y - cotaDownXY.y).toDouble()) > slop) {
                        removeCallbacks(longPressRunnable); cotaPendiente = null
                    }
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (cotaPendiente != null) {
                    removeCallbacks(longPressRunnable)
                    val h = cotaPendiente
                    cotaPendiente = null
                    if (!longPressFired && event.actionMasked == MotionEvent.ACTION_UP && h != null) editarCota(h)
                    return true
                }
            }
        }
        // Piezas de plantilla: tap = cambiar de estado (lado de la bisagra, vista, apertura, michi),
        // mantener = quitar el puente. Vale con el lienzo en mano y también con la herramienta de
        // selección: quitar la selección deja esa herramienta puesta, y desde ahí los símbolos se
        // habían quedado mudos.
        if ((herramienta == Tool.NONE || herramienta == Tool.SELECT) && manejarPiezaPlantilla(event)) {
            return true
        }
        // Nodos: se agarra la esquina y se arrastra sola. Si el dedo no cae en ninguna, el lienzo se
        // mueve como siempre, para poder acercarse a la que se busca.
        if (herramienta == Tool.NODO) {
            if (manejarNodos(event)) return true
            manejarPanSinHerramienta(event)
            return true
        }
        if (herramienta == Tool.NONE) {
            manejarPanSinHerramienta(event)
            return true
        }
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val p = screenToWorld(event.x, event.y)
                if (herramienta == Tool.SELECT) {
                    manejarSeleccionDown(p.x, p.y)
                    return true
                }
                if (herramienta == Tool.TEXT) {
                    pedirTexto(p.x, p.y)
                    return true
                }
                parent?.requestDisallowInterceptTouchEvent(true)
                val inicio = if (herramienta == Tool.LINE || herramienta == Tool.ORTHO_LINE) {
                    snapLibre(p) ?: p
                } else {
                    p
                }
                startPoint = inicio
                currentPoint = inicio
                trazoActual.reset()
                trazoActual.moveTo(inicio.x, inicio.y)
                dibujando = true
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val p = screenToWorld(event.x, event.y)
                if (herramienta == Tool.SELECT) {
                    moverSeleccion(p.x, p.y)
                    return true
                }
                currentPoint = p
                actualizarTrazo(p.x, p.y)
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val p = screenToWorld(event.x, event.y)
                if (herramienta == Tool.SELECT) {
                    finalizarMoverSeleccion()
                    return true
                }
                if (dibujando) {
                    currentPoint = p
                    actualizarTrazo(p.x, p.y)
                    if (herramienta == Tool.MAGNET_PEN) {
                        val imantado = crearTrazoImantado()
                        if (imantado.isNotEmpty()) {
                            elementos.addAll(imantado)
                            registrarAccion()
                        }
                    } else crearElemento(p.x, p.y)?.let {
                        elementos.add(it)
                        registrarAccion()
                    }
                    trazoActual.reset()
                    dibujando = false
                    invalidate()
                }
                parent?.requestDisallowInterceptTouchEvent(false)
                return true
            }
        }
        return true
    }

    private fun manejarViewportGesture(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_POINTER_DOWN -> iniciarViewportGesture(event)
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount >= 2) actualizarViewportGesture(event)
            }
            MotionEvent.ACTION_POINTER_UP,
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> {
                viewportGesture = event.pointerCount > 2
                parent?.requestDisallowInterceptTouchEvent(false)
                if (!viewportGesture) {
                    dibujando = false
                    trazoActual.reset()
                }
            }
        }
    }

    private fun manejarPanSinHerramienta(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent?.requestDisallowInterceptTouchEvent(true)
                panSinHerramienta = true
                lastPanPoint = PointF(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                if (panSinHerramienta) {
                    viewOffsetX += event.x - lastPanPoint.x
                    viewOffsetY += event.y - lastPanPoint.y
                    lastPanPoint = PointF(event.x, event.y)
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                panSinHerramienta = false
                parent?.requestDisallowInterceptTouchEvent(false)
            }
        }
    }

    private fun iniciarViewportGesture(event: MotionEvent) {
        parent?.requestDisallowInterceptTouchEvent(true)
        viewportGesture = true
        dibujando = false
        trazoActual.reset()
        lastViewportFocus = foco(event)
        lastViewportSpan = span(event)
    }

    private fun actualizarViewportGesture(event: MotionEvent) {
        val focus = foco(event)
        val newSpan = span(event)
        val spanAnterior = lastViewportSpan.takeIf { it > 1f } ?: newSpan
        val scaleFactor = (newSpan / spanAnterior).takeIf { it.isFinite() && it > 0f } ?: 1f
        zoomAt(scaleFactor, focus.x, focus.y)
        viewOffsetX += focus.x - lastViewportFocus.x
        viewOffsetY += focus.y - lastViewportFocus.y
        lastViewportFocus = focus
        lastViewportSpan = newSpan
        invalidate()
    }

    private fun foco(event: MotionEvent): PointF {
        var x = 0f
        var y = 0f
        for (i in 0 until event.pointerCount) {
            x += event.getX(i)
            y += event.getY(i)
        }
        return PointF(x / event.pointerCount, y / event.pointerCount)
    }

    private fun span(event: MotionEvent): Float {
        if (event.pointerCount < 2) return 0f
        val dx = event.getX(0) - event.getX(1)
        val dy = event.getY(0) - event.getY(1)
        return hypot(dx.toDouble(), dy.toDouble()).toFloat()
    }

    private fun screenToWorld(x: Float, y: Float): PointF {
        val scale = viewScale.coerceAtLeast(0.05f)
        return PointF((x - viewOffsetX) / scale, (y - viewOffsetY) / scale)
    }

    private fun zoomAt(factor: Float, screenX: Float, screenY: Float) {
        val oldScale = viewScale
        val newScale = (oldScale * factor).coerceIn(0.15f, 6f)
        if (abs(newScale - oldScale) < 0.0001f) return
        val worldBefore = screenToWorld(screenX, screenY)
        viewScale = newScale
        viewOffsetX = screenX - worldBefore.x * viewScale
        viewOffsetY = screenY - worldBefore.y * viewScale
    }

    fun zoomIn() {
        zoomAt(1.2f, width / 2f, height / 2f)
        invalidate()
    }

    fun zoomOut() {
        zoomAt(1f / 1.2f, width / 2f, height / 2f)
        invalidate()
    }

    fun fitContentInView() {
        val bounds = boundsForContent() ?: run {
            resetViewport()
            return
        }
        if (width <= 0 || height <= 0 || bounds.width() <= 1f || bounds.height() <= 1f) {
            resetViewport()
            return
        }
        val margin = 42f * resources.displayMetrics.density
        val availableW = (width - margin * 2f).coerceAtLeast(1f)
        val availableH = (height - margin * 2f).coerceAtLeast(1f)
        viewScale = minOf(availableW / bounds.width(), availableH / bounds.height()).coerceIn(0.15f, 6f)
        viewOffsetX = (width - bounds.width() * viewScale) / 2f - bounds.left * viewScale
        viewOffsetY = (height - bounds.height() * viewScale) / 2f - bounds.top * viewScale
        invalidate()
    }

    fun resetViewport() {
        viewScale = 1f
        viewOffsetX = 0f
        viewOffsetY = 0f
        invalidate()
    }

    fun setTool(tool: Tool) {
        herramienta = tool
        trazoActual.reset()
        dibujando = false
        dragIndex = null
        // Con el lienzo en mano la selección se CONSERVA: los botones de edición (rotar, escalar,
        // duplicar, soldar...) sueltan la herramienta antes de actuar, y si al soltarla se perdía
        // lo seleccionado, ninguna de esas acciones llegaba a encontrar su figura. Solo se limpia
        // al coger una herramienta de dibujo, donde lo que venga es trazo nuevo.
        if (tool != Tool.SELECT && tool != Tool.NONE) selectedIndices.clear()
        if (tool != Tool.RECTANGLE) clearRectangleRoundedCorner()
        invalidate()
    }

    fun setRectangleRoundedCorner(corner: Int?, radiusCm: Float = 0f) {
        rectangleRoundedCorner = corner?.takeIf { it in 0..3 }
        rectangleRoundedRadiusCm = radiusCm.coerceAtLeast(0f)
    }

    fun clearRectangleRoundedCorner() {
        rectangleRoundedCorner = null
        rectangleRoundedRadiusCm = 0f
    }

    fun insertarSimbolo(drawableName: String) {
        if (drawableIdForName(drawableName) == 0) return
        val size = symbolSize()
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val rect = posicionLibreParaSimbolo(RectF(
            center.x - size / 2f,
            center.y - size / 2f,
            center.x + size / 2f,
            center.y + size / 2f
        ))
        elementos.add(Element.Symbol(drawableName, rect))
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
    }

    fun insertarSimboloCentroDiseno(drawableName: String) {
        if (drawableIdForName(drawableName) == 0) return
        val bounds = boundsForDisenoBase() ?: boundsReferenciaBisagras()
        val size = symbolSize()
        val rect = posicionLibreParaSimbolo(RectF(
            bounds.centerX() - size / 2f,
            bounds.centerY() - size / 2f,
            bounds.centerX() + size / 2f,
            bounds.centerY() + size / 2f
        ))
        elementos.add(Element.Symbol(drawableName, rect))
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
    }

    fun insertarMichi(valor: String) {
        if (drawableIdForName("michi") == 0) return
        val bounds = boundsForDisenoBase() ?: boundsReferenciaBisagras()
        val size = symbolSize()
        val gap = 8f * resources.displayMetrics.density
        sketchTextPaint.textSize = 34f
        val textWidth = sketchTextPaint.measureText(valor).coerceAtLeast(1f)
        val groupWidth = size + gap + textWidth
        val centerY = bounds.bottom + size * 1.15f
        val left = bounds.centerX() - groupWidth / 2f
        val symbolRect = posicionLibreParaSimbolo(RectF(
            left,
            centerY - size / 2f,
            left + size,
            centerY + size / 2f
        ))
        val textX = symbolRect.right + gap
        val textY = symbolRect.centerY() + 12f
        elementos.add(Element.Symbol("michi", symbolRect))
        val symbolIndex = elementos.lastIndex
        elementos.add(Element.TextLabel(valor, textX, textY, 34f, rol = ROL_MICHI))
        selectedIndices.clear()
        selectedIndices.add(symbolIndex)
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
    }

    fun insertarSimboloInteriorExterior(drawableName: String) {
        if (drawableName !in setOf("interior", "exterior")) return
        if (drawableIdForName(drawableName) == 0) return
        elementos.removeAll { element ->
            element is Element.Symbol && element.drawableName in setOf("interior", "exterior")
        }
        selectedIndices.clear()
        val bounds = boundsForDisenoBase() ?: boundsReferenciaBisagras()
        val size = symbolSize()
        val margin = 12f * resources.displayMetrics.density
        val rect = posicionLibreParaSimbolo(RectF(
            bounds.right + margin,
            bounds.top,
            bounds.right + margin + size,
            bounds.top + size
        ))
        elementos.add(Element.Symbol(drawableName, rect))
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
    }

    fun insertarBisagras(izquierda: Boolean) {
        if (drawableIdForName("bisagra") == 0) return
        val bounds = boundsForIndices(selectedIndices)
            ?: boundsForContent()
            ?: boundsReferenciaBisagras()
        val size = hingeSize()
        val sideX = if (izquierda) bounds.left else bounds.right
        val posicionesY = listOf(
            bounds.top + bounds.height() * 0.18f,
            bounds.centerY(),
            bounds.top + bounds.height() * 0.82f
        )

        selectedIndices.clear()
        val ocupados = mutableListOf<RectF>()
        posicionesY.forEach { centerY ->
            val rect = posicionLibreParaSimbolo(RectF(
                sideX - size / 2f,
                centerY - size / 2f,
                sideX + size / 2f,
                centerY + size / 2f
            ), fixedX = true, occupied = ocupados)
            elementos.add(Element.Symbol("bisagra", rect))
            ocupados.add(RectF(rect))
            selectedIndices.add(elementos.lastIndex)
        }
        registrarAccion()
        invalidate()
    }

    fun insertarPlantillaVanoExacto() {
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val ancho = cmToPx(120f)
        val alto = cmToPx(180f)
        val left = center.x - ancho / 2f
        val top = center.y - alto / 2f
        val right = center.x + ancho / 2f
        val bottom = center.y + alto / 2f
        val midX = (left + right) / 2f
        val midY = (top + bottom) / 2f
        val labelSize = 24f

        val nuevos = listOf(
            crearShape(Tool.RECTANGLE, PointF(left, top), PointF(right, bottom)),
            crearShape(Tool.LINE, PointF(left, midY), PointF(right, midY)),
            crearShape(Tool.LINE, PointF(midX, top), PointF(midX, bottom)),
            crearShape(Tool.LINE, PointF(left, top), PointF(right, bottom)),
            crearShape(Tool.LINE, PointF(right, top), PointF(left, bottom)),
            Element.TextLabel("VANO EXACTO", left, top - 34f, labelSize),
            Element.TextLabel("ancho medio", right + 18f, midY, labelSize),
            Element.TextLabel("alto centro", midX + 18f, bottom + 30f, labelSize),
            Element.TextLabel("diagonales", right + 18f, top + 34f, labelSize)
        )
        agregarPlantilla(nuevos)
    }

    fun insertarPlantillaGraderia(pasos: Int = 3, pasoCm: Float = 30f, contrapasoCm: Float = 18f) {
        val cantidadPasos = pasos.coerceIn(1, 40)
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val huella = cmToPx(pasoCm.coerceAtLeast(1f))
        val contraHuella = cmToPx(contrapasoCm.coerceAtLeast(1f))
        val left = center.x - (huella * cantidadPasos) / 2f
        val bottom = center.y + (contraHuella * cantidadPasos) / 2f
        val nuevos = mutableListOf<Element>()
        var x = left
        var y = bottom
        val inicio = PointF(x, y)
        repeat(cantidadPasos) {
            val contraStart = PointF(x, y)
            val contraEnd = PointF(x, y - contraHuella)
            nuevos.add(crearShape(Tool.LINE, contraStart, contraEnd, "GRADA_CONTRAPASO"))
            y -= contraHuella
            val pasoStart = PointF(x, y)
            val pasoEnd = PointF(x + huella, y)
            nuevos.add(crearShape(Tool.LINE, pasoStart, pasoEnd, "GRADA_PASO"))
            x += huella
        }
        val fin = PointF(x, y)
        nuevos.add(crearShape(Tool.LINE, inicio, fin, "GRADA_TOTAL_COTA"))

        agregarPlantilla(nuevos)
    }

    fun insertarPlantillaProducto(
        anchoCm: Float,
        altoCm: Float,
        hojas: Int = 1,
        bisagra: String? = null,
        vista: String? = null,
        apertura: String? = null,
        etiqueta: String? = null
    ) {
        val ancho = anchoCm.coerceAtLeast(20f)
        val alto = altoCm.coerceAtLeast(20f)
        val anchoPx = cmToPx(ancho)
        val altoPx = cmToPx(alto)
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = center.x - anchoPx / 2f
        val top = center.y - altoPx / 2f
        val right = left + anchoPx
        val bottom = top + altoPx
        val rect = RectF(left, top, right, bottom)

        val nuevos = mutableListOf<Element>()
        nuevos.add(crearShape(Tool.RECTANGLE, PointF(left, top), PointF(right, bottom)))

        val divisiones = hojas.coerceIn(1, 6)
        if (divisiones > 1) {
            val paso = anchoPx / divisiones.toFloat()
            for (i in 1 until divisiones) {
                val x = left + paso * i
                nuevos.add(crearShape(Tool.LINE, PointF(x, top), PointF(x, bottom)))
            }
        }

        if (!etiqueta.isNullOrBlank()) {
            nuevos.add(Element.TextLabel(etiqueta, left, top - 28f, 30f, titulo = true))
        }

        val tieneSimboloBisagra = !bisagra.isNullOrBlank() && drawableIdForName("bisagra") != 0
        if (tieneSimboloBisagra) {
            val size = hingeSize()
            val esIzquierda = bisagra.equals("izquierda", ignoreCase = true)
            val sideX = if (esIzquierda) left else right
            val ys = listOf(
                top + altoPx * 0.18f,
                top + altoPx * 0.5f,
                top + altoPx * 0.82f
            )
            ys.forEach { cy ->
                nuevos.add(
                    Element.Symbol(
                        "bisagra",
                        RectF(sideX - size / 2f, cy - size / 2f, sideX + size / 2f, cy + size / 2f)
                    )
                )
            }
        }

        val drawableVista = vista?.lowercase()
        if (drawableVista in setOf("interior", "exterior") && drawableIdForName(drawableVista!!) != 0) {
            val size = symbolSize()
            val margin = 12f * resources.displayMetrics.density
            nuevos.add(
                Element.Symbol(
                    drawableVista,
                    RectF(right + margin, top, right + margin + size, top + size)
                )
            )
        }

        val drawableApertura = apertura?.lowercase()
        if (drawableApertura in setOf("adentro", "afuera") && drawableIdForName(drawableApertura!!) != 0) {
            val size = symbolSize()
            nuevos.add(
                Element.Symbol(
                    drawableApertura,
                    RectF(
                        rect.centerX() - size / 2f,
                        rect.centerY() - size / 2f,
                        rect.centerX() + size / 2f,
                        rect.centerY() + size / 2f
                    )
                )
            )
        }

        agregarPlantilla(nuevos)
    }

    // ===================== Plantilla de puerta =====================
    //
    // La puerta es el dibujo de todos los días y siempre empieza igual: 90 x 240, puente a 200,
    // bisagras a la izquierda, vista interior, abre hacia adentro y michi de 0.5. Se dibuja entera
    // de un toque y lo que no coincida se corrige EN EL DIBUJO: cada cota se toca para escribir su
    // medida y cada símbolo cambia de estado tocándolo. El marco manda: si cambia, el puente y los
    // símbolos lo siguen.

    fun insertarPlantillaPuerta(
        anchoCm: Float = 90f,
        altoCm: Float = 240f,
        puenteCm: Float = 200f,
        michiCm: Float = 0.5f
    ) {
        val ancho = anchoCm.coerceAtLeast(20f)
        val alto = altoCm.coerceAtLeast(20f)
        val anchoPx = cmToPx(ancho)
        val altoPx = cmToPx(alto)
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = center.x - anchoPx / 2f
        val top = center.y - altoPx / 2f
        val right = left + anchoPx
        val bottom = top + altoPx
        val yPuente = bottom - cmToPx(puenteCm.coerceIn(1f, alto - 1f))

        val nuevos = mutableListOf<Element>()
        nuevos.add(crearShape(Tool.RECTANGLE, PointF(left, top), PointF(right, bottom), PUERTA_MARCO))
        nuevos.add(crearShape(Tool.LINE, PointF(left, yPuente), PointF(right, yPuente), PUERTA_PUENTE))
        // Los símbolos nacen sin sitio: se los da colocarPiezasDelMarco, la misma rutina que los
        // recoloca después, para que la puerta se arme igual al insertarla que al reeditarla.
        repeat(3) { nuevos.add(Element.Symbol("bisagra", RectF())) }
        nuevos.add(Element.Symbol("interior", RectF()))
        nuevos.add(Element.Symbol("adentro", RectF()))
        nuevos.add(Element.Symbol("michi", RectF()))
        nuevos.removeAll { it is Element.Symbol && drawableIdForName(it.drawableName) == 0 }
        nuevos.add(Element.TextLabel(formatCm(michiCm), 0f, 0f, 34f, rol = ROL_MICHI))
        // El rótulo va marcado como título: se mantiene solo por encima del dibujo, a su distancia,
        // pase lo que pase con la puerta, y dice siempre la medida que el marco tiene ahora.
        nuevos.add(
            Element.TextLabel(
                text = rotuloMarco(PUERTA_MARCO, ancho, alto),
                x = left,
                y = top - 28f,
                textSize = 54f,
                titulo = true,
                rol = ROL_TITULO_PUERTA
            )
        )
        nuevos.add(
            Element.TextLabel(
                text = textoHojas(1),
                x = left,
                y = top - 28f,
                textSize = 50f,
                titulo = true,
                rol = ROL_HOJAS
            )
        )

        // Se colocan las piezas ANTES de dar la acción por hecha: así el historial guarda la puerta
        // ya armada y deshacer no la deja con los símbolos en el origen.
        val marcoIndex = elementos.size
        elementos.addAll(nuevos)
        colocarPiezasDelMarco(marcoIndex, (marcoIndex + 2 until elementos.size).toList())
        selectedIndices.clear()
        (marcoIndex until elementos.size).forEach { selectedIndices.add(it) }
        registrarAccion()
        invalidate()
    }

    /**
     * El producto que el propio dibujo declara, cuando no hay duda posible.
     *
     * Una puerta insertada desde su plantilla ya dice lo que es: preguntarlo otra vez al guardar
     * era un paso de más. Lo que se dibujó a mano no declara nada y se sigue preguntando.
     */
    // ===================== Plantilla de ventana =====================
    //
    // Misma idea que la puerta: se dibuja entera y se corrige en el dibujo. Lo suyo es el ancho —los
    // vanos son largos— y ahí el alto de los cantos no dice nada del alto del centro, así que la
    // ventana nace con una cota de alto por dentro cada 120 cm de ancho. Debajo, el alfeizar.

    fun insertarPlantillaVentana(
        anchoCm: Float = 150f,
        altoCm: Float = 120f,
        puenteCm: Float = 90f,
        alfeizarCm: Float = 90f
    ) = insertarMarcoConAltos(VENTANA_MARCO, anchoCm, altoCm, puenteCm, alfeizarCm)

    /**
     * La mampara se toma igual que la ventana —puente y cotas de alto por dentro— y solo cambian
     * las medidas: 210 x 240 con el puente a 200. No lleva alfeizar: arranca del piso.
     */
    fun insertarPlantillaMampara(
        anchoCm: Float = 210f,
        altoCm: Float = 240f,
        puenteCm: Float = 200f
    ) = insertarMarcoConAltos(MAMPARA_MARCO, anchoCm, altoCm, puenteCm, alfeizarCm = null)


    /**
     * Ventana CURVA, en desarrollo: la pared entera va curvada y la ventana es una fila de arcos.
     *
     * No es la ventana de esquina con una curva en el rincón: aquí no hay tramos rectos. Se parte
     * en las divisiones que haga falta porque una ventana curva de obra casi nunca es el arco de
     * un círculo perfecto —cada trozo lleva su propio desarrollo, su cuerda y su flecha, que es
     * como se mide en la pared—.
     *
     * En la alzada cada tramo mide su DESARROLLO, que es lo que se corta; en la planta ocupa su
     * cuerda y gira lo que diga su arco.
     */
    fun insertarPlantillaVentanaCurva(
        tramosCm: List<Float> = listOf(180f),
        altoCm: Float = 160f,
        puenteCm: Float = 120f,
        alfeizarCm: Float = 90f,
        flechaCm: Float = 12f
    ) {
        val tramos = tramosCm.map { it.coerceAtLeast(20f) }.ifEmpty { listOf(180f) }
        val ancho = tramos.sum()
        val alto = altoCm.coerceAtLeast(20f)
        val anchoPx = cmToPx(ancho)
        val altoPx = cmToPx(alto)
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = center.x - anchoPx / 2f
        val top = center.y - altoPx / 2f
        val bottom = top + altoPx
        val yPuente = bottom - cmToPx(puenteCm.coerceIn(1f, alto - 1f))

        val nuevos = mutableListOf<Element>()
        nuevos.add(crearShape(Tool.RECTANGLE, PointF(left, top), PointF(left + anchoPx, bottom), ESQUINA_MARCO))
        var xPuente = left
        tramos.forEach { tramo ->
            val der = xPuente + cmToPx(tramo)
            nuevos.add(crearShape(Tool.LINE, PointF(xPuente, yPuente), PointF(der, yPuente), PUERTA_PUENTE))
            xPuente = der
        }
        // Entre tramo y tramo hay arista, pero NO ángulo: dos arcos seguidos se encuentran sin
        // doblar en punta, el giro lo hace cada uno a lo suyo. Por eso aquí no se pone rótulo de
        // ángulo: uno de 180° no dice nada y llenaba el papel de números que no son medidas.
        var x = left
        tramos.dropLast(1).forEach { tramo ->
            x += cmToPx(tramo)
            nuevos.add(crearShape(Tool.LINE, PointF(x, top), PointF(x, bottom), ESQUINA_QUIEBRE))
            nuevos.add(crearShape(Tool.LINE, PointF(x, bottom), PointF(x, top), VENTANA_ALTO_ESQUINA))
        }
        // Y la curva de cada tramo: su desarrollo es lo que mide el tramo, y la cuerda sale de la
        // flecha con la que se arranca. Se toca para escribir las de verdad, medidas en la pared.
        var xCurva = left
        tramos.forEach { tramo ->
            val arco = ArcoEsquina.deDesarrolloYFlecha(tramo, flechaCm.coerceAtLeast(0.1f))
            nuevos.add(
                Element.TextLabel(
                    text = textoCurva(tramo, arco?.cuerda ?: tramo),
                    x = xCurva + cmToPx(tramo) / 2f,
                    y = bottom,
                    textSize = 44f,
                    rol = ROL_CURVA
                )
            )
            xCurva += cmToPx(tramo)
        }
        nuevos.add(
            Element.TextLabel(
                text = formatCm(alfeizarCm),
                x = left,
                y = bottom,
                textSize = 44f,
                rol = ROL_ALFEIZAR
            )
        )
        nuevos.add(
            Element.TextLabel(
                text = rotuloMarco(ESQUINA_MARCO, ancho, alto, curva = true),
                x = left,
                y = top - 28f,
                textSize = 54f,
                titulo = true,
                rol = ROL_TITULO_PUERTA
            )
        )
        nuevos.add(
            Element.TextLabel(
                text = textoTramos(tramos.size),
                x = left,
                y = top - 28f,
                textSize = 50f,
                titulo = true,
                rol = ROL_TRAMOS
            )
        )
        var xContador = left
        tramos.forEach { tramo ->
            nuevos.add(
                Element.TextLabel(
                    text = textoAltos(0),
                    x = xContador + cmToPx(tramo) / 2f,
                    y = bottom,
                    textSize = 44f,
                    rol = ROL_ALTOS
                )
            )
            xContador += cmToPx(tramo)
        }

        val marcoIndex = elementos.size
        elementos.addAll(nuevos)
        ajustarAltosAutomaticos(marcoIndex)
        sincronizarMarco(marcoIndex)
        selectedIndices.clear()
        (marcoIndex until elementos.size).forEach { selectedIndices.add(it) }
        registrarAccion()
        invalidate()
    }


    /**
     * La curva de un tramo: su desarrollo y su cuerda, que es como se mide una curva en la pared.
     *
     * De las dos sale la flecha y cuánto gira, así que con eso la planta ya sabe dibujarla. Se
     * pide el desarrollo porque es lo que se corta, y la cuerda porque es lo que se mide con la
     * cinta de punta a punta; la flecha se enseña calculada, para poder comprobarla contra la
     * pared.
     */
    private fun editarCurvaDeTramo(index: Int) {
        val etiqueta = elementos.getOrNull(index) as? Element.TextLabel ?: return
        val actual = esquinaDesdeTexto(etiqueta.text).arco
        val dp = resources.displayMetrics.density
        val cont = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((20 * dp).toInt(), (12 * dp).toInt(), (20 * dp).toInt(), 0)
        }
        val etDesarrollo = EditText(context).apply {
            hint = "Desarrollo (lo que se corta)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(actual?.let { formatCm(it.desarrollo) } ?: "")
            setSelectAllOnFocus(true)
        }
        // La flecha y la cuerda son la misma cosa dicha de dos maneras, y en la pared se mide la
        // que se puede: la flecha con una regla contra la cuerda, o la cuerda de punta a punta si
        // se llega. Se escribe cualquiera de las dos y la otra sale sola.
        val etFlecha = EditText(context).apply {
            hint = "Flecha (la panza)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(actual?.let { formatCm(it.flecha) } ?: "")
        }
        val etCuerda = EditText(context).apply {
            hint = "Cuerda (de punta a punta)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(actual?.let { formatCm(it.cuerda) } ?: "")
        }
        val aviso = TextView(context).apply {
            textSize = 13f
            setPadding(0, (8 * dp).toInt(), 0, 0)
        }
        fun leer(et: EditText) = et.text?.toString()?.replace(",", ".")?.toFloatOrNull()
        // Cuál de las dos mandó la última vez que se escribió: la que tocó el vidriero es la que
        // vale, y la otra es la que se recalcula.
        var mandaLaFlecha = false
        var recalculando = false

        /** El arco con lo que hay escrito ahora, o null si todavía no da uno. */
        fun arcoDeAhora(): ArcoEsquina? {
            val d = leer(etDesarrollo) ?: return null
            val f = leer(etFlecha)
            val c = leer(etCuerda)
            return when {
                mandaLaFlecha && f != null -> ArcoEsquina.deDesarrolloYFlecha(d, f)
                c != null -> ArcoEsquina.deDesarrolloYCuerda(d, c)
                f != null -> ArcoEsquina.deDesarrolloYFlecha(d, f)
                else -> null
            }
        }
        fun repasar() {
            if (recalculando) return
            val arco = arcoDeAhora()
            if (arco != null) {
                recalculando = true
                // Se rellena la otra, la que no se está escribiendo, para que las tres se vean.
                if (mandaLaFlecha) etCuerda.setText(formatCm(arco.cuerda))
                else etFlecha.setText(formatCm(arco.flecha))
                recalculando = false
            }
            aviso.text = when {
                arco != null -> "Radio ${formatCm(arco.radio)} · dobla ${formatCm(arco.anguloGrados)}°"
                leer(etDesarrollo) == null -> "Escribe el desarrollo, y la flecha o la cuerda."
                leer(etFlecha) == null && leer(etCuerda) == null ->
                    "Falta la flecha o la cuerda: con una basta."
                else -> "La flecha y la cuerda tienen que caber en el desarrollo."
            }
        }
        fun vigilar(et: EditText, laFlecha: Boolean?) {
            et.addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
                override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {
                    if (!recalculando && laFlecha != null) mandaLaFlecha = laFlecha
                    repasar()
                }
                override fun afterTextChanged(s: android.text.Editable?) {}
            })
        }
        vigilar(etDesarrollo, null)
        vigilar(etFlecha, true)
        vigilar(etCuerda, false)
        repasar()
        cont.addView(etDesarrollo)
        cont.addView(etFlecha)
        cont.addView(etCuerda)
        cont.addView(aviso)

        AlertDialog.Builder(context)
            .setTitle("Curva del tramo")
            .setView(cont)
            .setPositiveButton("Aceptar") { _, _ ->
                val d = leer(etDesarrollo)
                val arco = arcoDeAhora()
                if (d == null || arco == null) {
                    Toast.makeText(
                        context,
                        "La curva necesita su desarrollo y su flecha o su cuerda, " +
                            "y las dos más cortas que el desarrollo.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }
                val c = arco.cuerda
                etiqueta.text = textoCurva(d, c)
                // El tramo mide su desarrollo: es lo que se corta y lo que se ve en la alzada.
                marcoDePieza(index)?.let { marco ->
                    val tramo = etiquetasCurva(marco).indexOf(index)
                    if (tramo >= 0) aplicarAnchoTramoEnPlanta(marco, tramo, d)
                    sincronizarMarco(marco, reinterpolarAltos = false)
                }
                registrarAccion()
                invalidate()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    /** El rótulo de la curva de un tramo: su desarrollo y su cuerda. */
    private fun textoCurva(desarrolloCm: Float, cuerdaCm: Float): String =
        "$MARCA_CURVA ${formatCm(desarrolloCm)}|${formatCm(cuerdaCm)}"
    /**
     * Ventana que dobla en esquina, dibujada en desarrollo: los tramos estirados uno junto a otro y
     * la arista marcada entre ellos, con su ángulo al lado.
     *
     * Cada tramo se mide contra SU pared, así que aquí el ancho total no manda: es la suma de los
     * tramos y crece o encoge con ellos —al revés que las hojas de una puerta, que se reparten un
     * vano fijo—. Los anchos llegan hasta la arista; lo que se lleve el parante de esquina se
     * descuenta después, al cortar.
     */
    fun insertarPlantillaVentanaEsquina(
        tramosCm: List<Float> = listOf(150f, 120f),
        altoCm: Float = 120f,
        puenteCm: Float = 90f,
        alfeizarCm: Float = 90f,
        anguloGrados: Float = 90f
    ) {
        val tramos = tramosCm.map { it.coerceAtLeast(20f) }.ifEmpty { listOf(150f, 120f) }
        val ancho = tramos.sum()
        val alto = altoCm.coerceAtLeast(20f)
        val anchoPx = cmToPx(ancho)
        val altoPx = cmToPx(alto)
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = center.x - anchoPx / 2f
        val top = center.y - altoPx / 2f
        val bottom = top + altoPx
        val yPuente = bottom - cmToPx(puenteCm.coerceIn(1f, alto - 1f))

        val nuevos = mutableListOf<Element>()
        nuevos.add(crearShape(Tool.RECTANGLE, PointF(left, top), PointF(left + anchoPx, bottom), ESQUINA_MARCO))
        // Un puente por tramo: el travesaño de una pared no tiene por qué ir a la altura del de la
        // otra, y así cada uno se sube, se baja o se quita por su cuenta.
        var xPuente = left
        tramos.forEach { tramo ->
            val der = xPuente + cmToPx(tramo)
            nuevos.add(crearShape(Tool.LINE, PointF(xPuente, yPuente), PointF(der, yPuente), PUERTA_PUENTE))
            xPuente = der
        }
        // Una arista entre tramo y tramo, con su ángulo y su cota de alto clavada encima.
        var x = left
        tramos.dropLast(1).forEach { tramo ->
            x += cmToPx(tramo)
            nuevos.add(crearShape(Tool.LINE, PointF(x, top), PointF(x, bottom), ESQUINA_QUIEBRE))
            nuevos.add(crearShape(Tool.LINE, PointF(x, bottom), PointF(x, top), VENTANA_ALTO_ESQUINA))
            nuevos.add(
                Element.TextLabel(
                    text = textoEsquina(anguloGrados),
                    x = x,
                    y = top,
                    textSize = 44f,
                    rol = ROL_ESQUINA
                )
            )
        }
        nuevos.add(
            Element.TextLabel(
                text = formatCm(alfeizarCm),
                x = left,
                y = bottom,
                textSize = 44f,
                rol = ROL_ALFEIZAR
            )
        )
        nuevos.add(
            Element.TextLabel(
                text = rotuloMarco(ESQUINA_MARCO, ancho, alto),
                x = left,
                y = top - 28f,
                textSize = 54f,
                titulo = true,
                rol = ROL_TITULO_PUERTA
            )
        )
        nuevos.add(
            Element.TextLabel(
                text = textoTramos(tramos.size),
                x = left,
                y = top - 28f,
                textSize = 50f,
                titulo = true,
                rol = ROL_TRAMOS
            )
        )
        // Un contador de cotas de alto por tramo, dentro de cada uno.
        var xContador = left
        tramos.forEach { tramo ->
            nuevos.add(
                Element.TextLabel(
                    text = textoAltos(0),
                    x = xContador + cmToPx(tramo) / 2f,
                    y = bottom,
                    textSize = 44f,
                    rol = ROL_ALTOS
                )
            )
            xContador += cmToPx(tramo)
        }

        val marcoIndex = elementos.size
        elementos.addAll(nuevos)
        // Las cotas de alto se reparten por tramo: cada uno pide las suyas por su propio ancho.
        ajustarAltosAutomaticos(marcoIndex)
        sincronizarMarco(marcoIndex)
        selectedIndices.clear()
        (marcoIndex until elementos.size).forEach { selectedIndices.add(it) }
        registrarAccion()
        invalidate()
    }

    // Cortos a propósito: con cuatro tramos, un rótulo con todas sus letras se monta con el de al
    // lado. Lo que es cada uno lo dice el sitio: el ángulo va bajo su arista y el contador bajo su
    // tramo.
    private fun textoEsquina(grados: Float): String = "${formatCm(grados)}°"

    private fun textoTramos(cuantos: Int): String = "−     N° de tramos  $cuantos     +"

    /** Las aristas de un marco de esquina, de izquierda a derecha. */
    private fun quiebresDelMarco(marcoIndex: Int): List<Int> {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return emptyList()
        return elementos.indices
            .filter { i ->
                val q = elementos.getOrNull(i) as? Element.Shape
                q != null && q.cotaHint == ESQUINA_QUIEBRE &&
                    q.start.x >= marco.rect.left - cmToPx(2f) && q.start.x <= marco.rect.right + cmToPx(2f) &&
                    maxOf(q.start.y, q.end.y) > marco.rect.top && minOf(q.start.y, q.end.y) < marco.rect.bottom
            }
            .sortedBy { (elementos[it] as Element.Shape).start.x }
    }

    /**
     * Las esquinas de arriba del desarrollo: los dos cantos del marco y el techo de cada arista.
     * Entre punto y punto va el lado superior de un tramo, que es lo que se corta de cabezal.
     */
    private fun puntosArriba(marcoIndex: Int): List<PointF> {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return emptyList()
        // Las bandas de pared curva cortan el cabezal igual que una arista: sin contarlas, la cota
        // de arriba juntaba la curva con la pared de al lado en una sola medida.
        val cortes = (quiebresDelMarco(marcoIndex) + bandasDeCurva(marcoIndex))
            .map { (elementos[it] as Element.Shape).start.let { p -> PointF(p.x, p.y) } }
            .sortedBy { it.x }
        return listOf(PointF(marco.topLeft.x, marco.topLeft.y)) + cortes +
            listOf(PointF(marco.topRight.x, marco.topRight.y))
    }

    /** Los cortes en x que separan los tramos: cantos del marco incluidos. */
    // ==================== VISTA EN PLANTA DE LA VENTANA DE ESQUINA ====================
    // La alzada dice cómo es cada pared; la planta dice cómo se doblan entre ellas. Los altos que
    // se miden dentro de cada tramo se leen aquí, sobre el punto donde se tomaron, y en la alzada
    // queda solo su marca: con cuatro tramos, el desarrollo se llenaba de números y no había forma
    // de saber cuál era de dónde.

    /**
     * Lo que baja la planta por debajo de la alzada.
     *
     * Tiene que dejar pasar lo que ya vive ahí abajo —la cota del ancho, el alféizar y el contador
     * de altos de cada tramo—, o la planta se les monta encima.
     */
    private val huecoPlantaPx get() = maxOf(cmToPx(55f), 300f)

    /** La pared curva de la planta: el mismo trazo que las rectas, pero sin relleno. */
    private val cotaArcoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(30, 30, 30)
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    /** El punto donde se tomó un alto, marcado sobre la línea de la planta. */
    private val cotaPuntoPlantaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1565C0")
        style = Paint.Style.FILL
    }

    /** ¿Esa cota de alto pertenece a una ventana de esquina, que lleva su número en la planta? */
    private fun altoDeVentanaEsquina(altoIndex: Int): Boolean =
        elementos.indices.any { esMarcoEsquina(it) && altoIndex in altosDelMarco(it, null) }

    /** ¿Ese elemento es un marco de ventana de esquina con aristas? */
    private fun esMarcoEsquina(index: Int): Boolean {
        val s = elementos.getOrNull(index) as? Element.Shape ?: return false
        if (s.cotaHint != ESQUINA_MARCO) return false
        // Una de esquina se reconoce porque dobla en algún sitio, o sea porque tiene aristas. Una
        // ventana curva puede ser un solo arco, sin ninguna: lo que la delata es su curva.
        return quiebresDelMarco(index).isNotEmpty() || etiquetasCurva(index).isNotEmpty()
    }

    /**
     * Lo que hay en una arista: o la pared dobla en punta con su ángulo, o la doblan con una curva
     * que va de la esquina de un tramo a la del otro.
     *
     * [grados] es cuánto dobla la pared en total, venga del ángulo escrito o del arco, y su SIGNO
     * el sentido: en más hacia adentro, en menos hacia afuera.
     */
    private data class EsquinaVentana(val grados: Float, val arco: ArcoEsquina?)

    /**
     * Los rótulos de curva de los tramos, EN EL ORDEN DE SUS TRAMOS: el primero es el del tramo de
     * más a la izquierda de la alzada.
     *
     * Van por el orden en que se crearon, igual que los ángulos de las aristas y por lo mismo: su
     * sitio en la pantalla lo pone la banda de rótulos, no lo que son.
     */
    private fun etiquetasCurva(marcoIndex: Int): List<Int> {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return emptyList()
        val zona = zonaDelMarco(marco)
        return elementos.indices.filter { i ->
            val t = elementos.getOrNull(i) as? Element.TextLabel
            t != null && t.rol == ROL_CURVA && zona.contains(t.x, t.y)
        }
    }

    /** La curva de cada tramo, por su número. Vacío en una ventana que no es curva. */
    private fun curvasDeTramo(marcoIndex: Int): Map<Int, EsquinaVentana> {
        val out = HashMap<Int, EsquinaVentana>()
        etiquetasCurva(marcoIndex).forEachIndexed { tramo, i ->
            val t = elementos[i] as Element.TextLabel
            val curva = esquinaDesdeTexto(t.text)
            if (curva.arco != null) out[tramo] = curva
        }
        return out
    }

    /** ¿Esta ventana es curva? Lo es si alguno de sus tramos lleva su propio arco. */
    private fun esMarcoCurvo(marcoIndex: Int): Boolean = curvasDeTramo(marcoIndex).isNotEmpty()

    /** Lo que dice el rótulo de cada arista, de izquierda a derecha. */

    private fun esquinasDeVentana(marcoIndex: Int): List<EsquinaVentana> =
        etiquetasEsquina(marcoIndex).map { i ->
            esquinaDesdeTexto((elementos[i] as Element.TextLabel).text)
        }

    /**
     * Lee el rótulo de una arista: `90°` es una esquina en punta y `⌒ 157|141.4` una curva, con su
     * desarrollo y su cuerda. El menos delante, en las dos, es que dobla hacia afuera.
     */
    private fun esquinaDesdeTexto(texto: String): EsquinaVentana {
        val t = texto.trim()
        val afuera = t.startsWith("-") || t.startsWith("−")
        val signo = if (afuera) -1f else 1f
        if (t.contains(MARCA_CURVA)) {
            val numeros = t.split("|", "/").mapNotNull { trozo ->
                trozo.filter { it.isDigit() || it == '.' || it == ',' }.replace(",", ".").toFloatOrNull()
            }
            val arco = if (numeros.size >= 2) {
                ArcoEsquina.deDesarrolloYCuerda(numeros[0], numeros[1])
            } else null
            if (arco != null) return EsquinaVentana(signo * arco.anguloGrados, arco)
        }
        val valor = t.filter { it.isDigit() || it == '.' || it == ',' }.replace(",", ".")
            .toFloatOrNull()?.coerceIn(1f, 359f) ?: 90f
        return EsquinaVentana(signo * valor, null)
    }

    /** Cuánto dobla cada arista, con su signo. Es lo que la planta necesita para girar. */
    private fun angulosDeEsquina(marcoIndex: Int): List<Float> =
        esquinasDeVentana(marcoIndex).map { it.grados }

    /**
     * El recorrido de la ventana visto desde arriba: un punto por borde de tramo, doblando en cada
     * arista por su ángulo.
     *
     * Arranca bajo el canto izquierdo de la alzada y va hacia la derecha; en cada arista gira lo
     * que le falta al ángulo para ser una pared recta, así que 180° sigue de largo y 90° dobla en
     * escuadra. El giro va hacia abajo, que es como se mira una planta puesta bajo su alzada.
     */
    /**
     * La planta armada: cada pared con sus dos puntas, y lo que hay en cada esquina.
     *
     * Una esquina en punta junta el final de una pared con el principio de la siguiente; una
     * esquina curva las separa, porque entre las dos va el arco: de la esquina de un lado a la del
     * otro, con su desarrollo y su cuerda.
     */
    private data class PlantaDeEsquina(
        val paredes: List<Pair<PointF, PointF>>,
        val curvas: Map<Int, ArcoEnPlanta>
    )

    /** Un arco de la planta ya colocado: sus dos puntas, su panza y hacia dónde la echa. */
    private data class ArcoEnPlanta(
        val arco: ArcoEsquina,
        val desde: PointF,
        val hasta: PointF,
        val sentido: Float
    )

    private fun plantaDeEsquina(marcoIndex: Int): PlantaDeEsquina {
        val vacia = PlantaDeEsquina(emptyList(), emptyMap())
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return vacia
        val bordes = bordesDeTramos(marcoIndex)
        if (bordes.size < 2) return vacia
        val esquinas = esquinasDeVentana(marcoIndex)

        // Qué hay en cada borde de dentro: una esquina con su ángulo, o el fin de la banda que una
        // pared curva ocupa en el desarrollo. Las esquinas van en orden con los quiebres.
        val quiebresX = quiebresDelMarco(marcoIndex).map { (elementos[it] as Element.Shape).end.x }
        val esquinaEnBorde = HashMap<Int, EsquinaVentana>()
        for (b in 1 until bordes.size - 1) {
            val cual = quiebresX.indexOfFirst { abs(it - bordes[b]) < 0.5f }
            if (cual >= 0) esquinas.getOrNull(cual)?.let { esquinaEnBorde[b] = it }
        }

        // En una ventana CURVA la curva no está en la arista sino en el tramo: la pared entera va
        // curvada y la ventana es una fila de ellas. Cada una gira a lo largo de su propio arco.
        val curvasPropias = curvasDeTramo(marcoIndex)

        val paredes = mutableListOf<Pair<PointF, PointF>>()
        val curvas = mutableMapOf<Int, ArcoEnPlanta>()
        var x = marco.rect.left
        var y = marco.rect.bottom + huecoPlantaPx
        var dir = 0.0 // radianes; 0 = hacia la derecha
        for (t in 0 until bordes.size - 1) {
            val desde = PointF(x, y)
            val propia = curvasPropias[t]?.arco
            if (propia != null) {
                val sentido = if ((curvasPropias[t]?.grados ?: 0f) < 0f) -1.0 else 1.0
                val giro = Math.toRadians(propia.anguloGrados.toDouble())
                val rumbo = dir + sentido * giro / 2.0
                val cuerda = cmToPx(propia.cuerda)
                x += (cuerda * cos(rumbo)).toFloat()
                y += (cuerda * sin(rumbo)).toFloat()
                val hasta = PointF(x, y)
                paredes.add(desde to hasta)
                curvas[t] = ArcoEnPlanta(propia, desde, hasta, sentido.toFloat())
                dir += sentido * giro
                continue
            }
            // ¿Este tramo es la banda de una curva? Lo es si su borde izquierdo es la esquina que la
            // abre. Entonces en planta no avanza su desarrollo, sino su cuerda, y gira el arco.
            val laCurva = esquinaEnBorde[t]?.arco
            if (laCurva != null && esquinaEnBorde[t + 1] == null) {
                val sentido = if ((esquinaEnBorde[t]?.grados ?: 0f) < 0f) -1.0 else 1.0
                val giro = Math.toRadians(laCurva.anguloGrados.toDouble())
                val rumbo = dir + sentido * giro / 2.0
                val cuerda = cmToPx(laCurva.cuerda)
                x += (cuerda * cos(rumbo)).toFloat()
                y += (cuerda * sin(rumbo)).toFloat()
                val hasta = PointF(x, y)
                paredes.add(desde to hasta)
                curvas[t] = ArcoEnPlanta(laCurva, desde, hasta, sentido.toFloat())
                dir += sentido * giro
                continue
            }
            val largo = bordes[t + 1] - bordes[t]
            x += (largo * cos(dir)).toFloat()
            y += (largo * sin(dir)).toFloat()
            paredes.add(desde to PointF(x, y))

            // Y en su borde derecho, si ahí hay esquina en punta, lo que dobla.
            val esquina = esquinaEnBorde[t + 1] ?: continue
            if (esquina.arco != null) continue // la curva gira en su banda, no aquí
            val sentido = if (esquina.grados < 0f) -1.0 else 1.0
            dir += sentido * Math.toRadians((180f - abs(esquina.grados)).toDouble())
        }

        // Doblando hacia afuera la planta sube, y ahí arriba están los rótulos de la alzada: se
        // baja entera lo que haga falta para que nada quede por encima de su sitio.
        val arranque = marco.rect.bottom + huecoPlantaPx
        val todos = paredes.flatMap { listOf(it.first, it.second) } +
            curvas.values.flatMap { listOf(it.desde, it.hasta) }
        val sube = arranque - (todos.minOfOrNull { it.y } ?: arranque)
        if (sube > 0.5f) todos.forEach { it.y += sube }
        return PlantaDeEsquina(paredes, curvas)
    }

    /** Las puntas de la planta en fila, como se recorren. */
    private fun recorridoPlanta(marcoIndex: Int): List<PointF> {
        val planta = plantaDeEsquina(marcoIndex)
        if (planta.paredes.isEmpty()) return emptyList()
        // La pared curva es una pared como las otras: su punta de llegada ya la pone su propio
        // tramo, y añadirla otra vez metía un punto de más en el recorrido.
        val puntos = mutableListOf(planta.paredes.first().first)
        planta.paredes.forEach { puntos.add(it.second) }
        return puntos
    }

    /** Dibuja la planta de cada ventana de esquina del apunte. */
    private fun dibujarPlantasDeEsquina(canvas: Canvas, collectHits: Boolean) {
        elementos.indices.filter { esMarcoEsquina(it) }.forEach { marcoIndex ->
            dibujarPlantaEsquina(canvas, marcoIndex, collectHits)
        }
    }

    private fun dibujarPlantaEsquina(canvas: Canvas, marcoIndex: Int, collectHits: Boolean) {
        val planta = plantaDeEsquina(marcoIndex)
        if (planta.paredes.isEmpty()) return
        val bordes = bordesDeTramos(marcoIndex)
        val angulos = angulosDeEsquina(marcoIndex)

        // Cada pared, y en las esquinas curvas el arco que las une, con su panza.
        // Las rectas con su trazo; la curva lo pone su arco, que si no salía con la cuerda dibujada.
        planta.paredes.forEachIndexed { i, (a, b) ->
            if (!planta.curvas.containsKey(i)) canvas.drawLine(a.x, a.y, b.x, b.y, cotaLinePaint)
        }
        planta.curvas.forEach { (arista, curva) ->
            canvas.drawPath(
                caminoDeArco(curva.desde, curva.hasta, cmToPx(curva.arco.flecha), curva.sentido),
                cotaArcoPaint
            )
            // El desarrollo y la cuerda no se pintan aquí: los dice su rótulo, que es el que se
            // toca para cambiarlos y ya se coloca junto a la panza.
            if (arista < 0) return@forEach
        }
        // Y el ancho de cada pared, sobre su lado.
        planta.paredes.forEachIndexed { i, (desde, hasta) ->
            // Una pared de cero es un punto, y aun asÃ­ lleva su cota: es por donde se le vuelve a
            // dar medida (en la alzada no se acota, que ahÃ­ no hay nada que medir). Se pone donde
            // estarÃ­a la pared si midiera, siguiendo a su vecina, que encima del punto el 0 no se
            // leÃ­a ni se podÃ­a tocar.
            val (a, b) = if (distancia(desde, hasta) >= 1f) desde to hasta
            else sitioDeParedDeCero(planta.paredes, i)
            val medio = PointF((a.x + b.x) / 2f, (a.y + b.y) / 2f)
            val grados = Math.toDegrees(
                kotlin.math.atan2((b.y - a.y).toDouble(), (b.x - a.x).toDouble())
            ).toFloat()
            val fuera = perpendicular(a, b, ce(20f) * sentidoDeTramo(angulos, i))
            // El ancho del tramo, tocable: aquí el tramo es una pared entera, así que al cambiarlo
            // se mueven sus DOS lados a la vez y se ve en la alzada. Los lados por separado —el
            // descuadre— se apuntan allí, cada uno con su cota.
            drawCotaText(
                canvas = canvas,
                index = marcoIndex,
                type = CotaType.ESQUINA_TRAMO_PLANTA,
                cx = medio.x + fuera.x,
                cy = medio.y + fuera.y,
                value = pxToCm(bordes[i + 1] - bordes[i]),
                collectHits = collectHits,
                sideIndex = i,
                angleDegrees = if (grados > 90f || grados < -90f) grados + 180f else grados
            )
        }
        // El ángulo no se pinta aquí: es un rótulo de verdad —se toca para cambiarlo— y se coloca
        // en su esquina de la planta desde `colocarBandaRotulos`.

        // Los altos: un punto donde se tomó cada uno, con su medida por dentro de la pared. Es lo
        // que se quita de la alzada, que se queda solo con la marca.
        altosDelMarco(marcoIndex, null).forEach { i ->
            val alto = elementos[i] as Element.Shape
            val tramo = tramoDeX(bordes, alto.start.x)
            val (a, b) = planta.paredes.getOrNull(tramo) ?: return@forEach
            val izq = bordes.getOrNull(tramo) ?: return@forEach
            val der = bordes.getOrNull(tramo + 1) ?: return@forEach
            val k = if (der - izq > 0.01f) ((alto.start.x - izq) / (der - izq)).coerceIn(0f, 1f) else 0f
            val p = PointF(a.x + (b.x - a.x) * k, a.y + (b.y - a.y) * k)
            val dentro = perpendicular(a, b, -ce(20f) * sentidoDeTramo(angulos, tramo))
            canvas.drawCircle(p.x, p.y, ce(5f), cotaPuntoPlantaPaint)
            drawCotaText(
                canvas = canvas,
                index = i,
                type = CotaType.LENGTH,
                cx = p.x + dentro.x,
                cy = p.y + dentro.y,
                value = alto.lengthCm,
                collectHits = collectHits
            )
        }
    }

    /**
     * Dónde va el rótulo del ángulo de cada arista: por fuera de su esquina en la planta.
     *
     * Por fuera y no por dentro porque dentro ya está el alto que se midió justo en la arista.
     */
    private fun verticesDeAnguloEnPlanta(marcoIndex: Int): List<PointF> {
        val planta = plantaDeEsquina(marcoIndex)
        if (planta.paredes.size < 2) return emptyList()
        val angulos = angulosDeEsquina(marcoIndex)
        val bordes = bordesDeTramos(marcoIndex)
        // Cada arista está en un borde del desarrollo, y la pared que empieza ahí es su curva si la
        // tiene. Sin esto, al meter la banda de la curva los rótulos se iban al vértice equivocado:
        // los tramos dejaron de ir uno por arista.
        val quiebresX = quiebresDelMarco(marcoIndex).map { (elementos[it] as Element.Shape).end.x }
        return quiebresX.indices.map { arista ->
            val borde = bordes
                .indexOfFirst { abs(it - quiebresX[arista]) < 0.5f }
                .coerceIn(1, planta.paredes.size - 1)
            val fuera = cmToPx(20f) * if ((angulos.getOrNull(arista) ?: 90f) < 0f) -1f else 1f
            val curva = planta.curvas[borde]
            if (curva != null) {
                // En una esquina curva, junto a su panza: ahí es donde se lee la curva.
                val medio = PointF(
                    (curva.desde.x + curva.hasta.x) / 2f, (curva.desde.y + curva.hasta.y) / 2f
                )
                val hacia = perpendicular(
                    curva.desde, curva.hasta, cmToPx(curva.arco.flecha) * curva.sentido + fuera
                )
                PointF(medio.x + hacia.x, medio.y + hacia.y)
            } else {
                // En punta: hacia el lado contrario al que dobla, que es por donde hay sitio.
                val vertice = planta.paredes[borde - 1].second
                val a = perpendicular(planta.paredes[borde - 1].first, vertice, fuera)
                val b = perpendicular(vertice, planta.paredes[borde].second, fuera)
                PointF(vertice.x + (a.x + b.x) / 2f, vertice.y + (a.y + b.y) / 2f)
            }
        }
    }

    /** Hacia qué lado queda el "fuera" de un tramo: lo marca la esquina con la que se encuentra. */
    private fun sentidoDeTramo(angulos: List<Float>, tramo: Int): Float {
        val suyo = angulos.getOrNull(tramo) ?: angulos.getOrNull(tramo - 1) ?: return 1f
        return if (suyo < 0f) -1f else 1f
    }

    /** Dónde cae en la planta un punto que en la alzada está en esa x. */
    private fun puntoEnPlanta(puntos: List<PointF>, bordes: List<Float>, x: Float): PointF? {
        if (puntos.size < 2 || bordes.size < 2) return null
        for (t in 0 until bordes.size - 1) {
            val izq = bordes[t]
            val der = bordes[t + 1]
            if (x < izq - 0.5f || x > der + 0.5f) continue
            val a = puntos.getOrNull(t) ?: return null
            val b = puntos.getOrNull(t + 1) ?: return null
            val k = if (der - izq > 0.01f) ((x - izq) / (der - izq)).coerceIn(0f, 1f) else 0f
            return PointF(a.x + (b.x - a.x) * k, a.y + (b.y - a.y) * k)
        }
        return null
    }

    /**
     * El camino de una pared curva entre sus dos puntas, con la panza hacia fuera.
     *
     * Se dibuja con una cuadrática, cuyo punto de control va al doble de la flecha: así la curva
     * pasa justo por la panza medida, que es la medida que se tomó en obra.
     */
    private fun caminoDeArco(desde: PointF, hasta: PointF, flechaPx: Float, sentido: Float): Path {
        val medio = PointF((desde.x + hasta.x) / 2f, (desde.y + hasta.y) / 2f)
        val hacia = perpendicular(desde, hasta, flechaPx * 2f * sentido)
        return Path().apply {
            moveTo(desde.x, desde.y)
            quadTo(medio.x + hacia.x, medio.y + hacia.y, hasta.x, hasta.y)
        }
    }

    /** Un desplazamiento perpendicular al lado, hacia el lado de fuera de la planta. */
    private fun perpendicular(a: PointF, b: PointF, largo: Float): PointF {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val n = kotlin.math.hypot(dx, dy).coerceAtLeast(0.001f)
        return PointF(dy / n * largo, -dx / n * largo)
    }

    /**
     * DÃ³nde poner la cota de una pared que mide cero: siguiendo a la vecina que sÃ­ mide, del lado
     * por el que la pared crecerÃ­a. Devuelve el tramito de apoyo, en su sentido.
     */
    private fun sitioDeParedDeCero(
        paredes: List<Pair<PointF, PointF>>,
        i: Int
    ): Pair<PointF, PointF> {
        val punto = paredes[i].first
        val siguiente = (i + 1 until paredes.size)
            .firstOrNull { distancia(paredes[it].first, paredes[it].second) >= 1f }
        val anterior = (i - 1 downTo 0)
            .firstOrNull { distancia(paredes[it].first, paredes[it].second) >= 1f }
        val vecina = paredes.getOrNull(siguiente ?: anterior ?: -1) ?: return punto to punto
        val dx = vecina.second.x - vecina.first.x
        val dy = vecina.second.y - vecina.first.y
        val n = kotlin.math.hypot(dx, dy).coerceAtLeast(0.001f)
        val largo = ce(30f)
        val fuera = PointF(punto.x - dx / n * largo, punto.y - dy / n * largo)
        // Delante de la vecina de despuÃ©s, detrÃ¡s de la de antes: siempre por fuera de la ventana.
        return if (siguiente != null) fuera to PointF(punto.x, punto.y)
        else PointF(punto.x, punto.y) to PointF(punto.x + dx / n * largo, punto.y + dy / n * largo)
    }

    private fun bordesDeTramos(marcoIndex: Int): List<Float> {
        val marco = (elementos.getOrNull(marcoIndex) as? Element.Shape) ?: return emptyList()
        // La banda de una pared curva cuenta como un tramo más: ocupa su desarrollo, que es lo que
        // hay que cortar. Su borde derecho no es una esquina —no dobla nada, lo hace la curva— así
        // que no entra en los quiebres ni pide rótulo de ángulo.
        val cortes = (
            quiebresDelMarco(marcoIndex).map { (elementos[it] as Element.Shape).end.x } +
                bandasDeCurva(marcoIndex).map { (elementos[it] as Element.Shape).end.x }
            ).sorted()
        return listOf(marco.bottomLeft.x) + cortes + listOf(marco.bottomRight.x)
    }

    /** Los bordes derechos de las bandas de pared curva de ese marco, de izquierda a derecha. */
    private fun bandasDeCurva(marcoIndex: Int): List<Int> {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return emptyList()
        return elementos.indices
            .filter { i ->
                val q = elementos.getOrNull(i) as? Element.Shape
                q != null && q.cotaHint == ESQUINA_CURVA_FIN &&
                    q.start.x >= marco.rect.left - cmToPx(2f) && q.start.x <= marco.rect.right + cmToPx(2f) &&
                    maxOf(q.start.y, q.end.y) > marco.rect.top && minOf(q.start.y, q.end.y) < marco.rect.bottom
            }
            .sortedBy { (elementos[it] as Element.Shape).start.x }
    }

    private fun insertarMarcoConAltos(
        hint: String,
        anchoCm: Float,
        altoCm: Float,
        puenteCm: Float,
        alfeizarCm: Float?
    ) {
        val ancho = anchoCm.coerceAtLeast(20f)
        val alto = altoCm.coerceAtLeast(20f)
        val anchoPx = cmToPx(ancho)
        val altoPx = cmToPx(alto)
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = center.x - anchoPx / 2f
        val top = center.y - altoPx / 2f
        val right = left + anchoPx
        val bottom = top + altoPx
        val yPuente = bottom - cmToPx(puenteCm.coerceIn(1f, alto - 1f))

        val nuevos = mutableListOf<Element>()
        nuevos.add(crearShape(Tool.RECTANGLE, PointF(left, top), PointF(right, bottom), hint))
        nuevos.add(crearShape(Tool.LINE, PointF(left, yPuente), PointF(right, yPuente), PUERTA_PUENTE))
        repeat(altosAutomaticos(ancho)) {
            nuevos.add(crearShape(Tool.LINE, PointF(left, bottom), PointF(left, top), VENTANA_ALTO))
        }
        if (alfeizarCm != null) {
            nuevos.add(
                Element.TextLabel(
                    text = formatCm(alfeizarCm),
                    x = left,
                    y = bottom,
                    textSize = 44f,
                    rol = ROL_ALFEIZAR
                )
            )
        }
        nuevos.add(
            Element.TextLabel(
                text = rotuloMarco(hint, ancho, alto),
                x = left,
                y = top - 28f,
                textSize = 54f,
                titulo = true,
                rol = ROL_TITULO_PUERTA
            )
        )
        nuevos.add(
            Element.TextLabel(
                text = textoAltos(altosAutomaticos(ancho)),
                x = left + anchoPx / 2f,
                y = bottom,
                textSize = 44f,
                rol = ROL_ALTOS
            )
        )

        val marcoIndex = elementos.size
        elementos.addAll(nuevos)
        sincronizarMarco(marcoIndex)
        selectedIndices.clear()
        (marcoIndex until elementos.size).forEach { selectedIndices.add(it) }
        registrarAccion()
        invalidate()
    }

    /** Una cota de alto por cada tramo entero de 120 cm: 150 lleva una, 250 lleva dos. */
    private fun altosAutomaticos(anchoCm: Float): Int =
        (anchoCm / TRAMO_ALTO_CM).toInt().coerceIn(0, 6)

    /**
     * El contador se toca por sus extremos: el "−" de la izquierda quita una cota y el "+" de la
     * derecha añade otra. Los signos van a la vista porque quitar estaba solo en la pulsación
     * larga y así no lo encuentra nadie.
     */
    private fun textoAltos(cuantas: Int): String = "−  $cuantas  +"

    /** Los contadores de cotas de alto: uno por tramo, en el orden de los tramos. */
    private fun contadoresAltos(marcoIndex: Int): List<Int> {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return emptyList()
        val zona = zonaDelMarco(marco)
        return elementos.indices
            .filter { i ->
                val t = elementos.getOrNull(i) as? Element.TextLabel
                t != null && t.rol == ROL_ALTOS && zona.contains(t.x, t.y)
            }
            .sortedBy { (elementos[it] as Element.TextLabel).x }
    }

    /**
     * Cada tramo lleva SU contador, dentro de él y al pie: las cotas de alto se añaden y se quitan
     * al tramo que se toca, no a la ventana entera.
     */
    private fun colocarContadoresAltos(marcoIndex: Int) {
        val grupos = altosLibresPorTramo(marcoIndex)
        contadoresAltos(marcoIndex).forEachIndexed { tramo, i ->
            (elementos[i] as Element.TextLabel).text = textoAltos(grupos.getOrNull(tramo).orEmpty().size)
        }
    }

    /**
     * La banda de rótulos bajo el marco: los ángulos bajo sus aristas, los contadores bajo sus
     * tramos y el alfeizar al final. Cada fila se reparte en dos renglones si sus rótulos no caben
     * de una: con cuatro tramos, todo en línea se monta.
     */
    private fun colocarBandaRotulos(marcoIndex: Int) {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
        val bordes = bordesDeTramos(marcoIndex)
        // Por debajo del total del ancho, que va un renglón más abajo que los anchos de los
        // tramos: con tres trozos el número del total caía justo sobre el contador del medio.
        var y = marco.bottomLeft.y + ce(140f)

        val angulos = etiquetasEsquina(marcoIndex)
        if (angulos.isNotEmpty()) {
            // El ángulo se lee en la planta, en su esquina, que es donde significa algo. Bajo la
            // alzada solo estorbaba, y con tres o cuatro tramos se montaba con los contadores.
            val enPlanta = verticesDeAnguloEnPlanta(marcoIndex)
            if (enPlanta.size == angulos.size) {
                angulos.forEachIndexed { orden, i ->
                    val etiqueta = elementos.getOrNull(i) as? Element.TextLabel ?: return@forEachIndexed
                    sketchTextPaint.textSize = tamanoTexto(etiqueta)
                    etiqueta.x = enPlanta[orden].x - sketchTextPaint.measureText(etiqueta.text) / 2f
                    etiqueta.y = enPlanta[orden].y
                }
            } else {
                val centros = quiebresDelMarco(marcoIndex).map { (elementos[it] as Element.Shape).end.x }
                y = colocarFilaRotulos(angulos, centros, y) + ce(26f)
            }
        }

        // Las curvas de los tramos, cada una bajo el suyo: son las que se tocan para escribir el
        // desarrollo y la cuerda medidos en la pared.
        val curvas = etiquetasCurva(marcoIndex)
        if (curvas.isNotEmpty() && bordes.size >= 2) {
            val centros = (0 until bordes.size - 1).map { (bordes[it] + bordes[it + 1]) / 2f }
            // Un renglón de sobra: los rótulos de curva son largos y se parten en dos filas, y con
            // la separación de siempre los contadores se les montaban encima.
            y = colocarFilaRotulos(curvas, centros, y) + ce(46f)
        }

        val contadores = contadoresAltos(marcoIndex)
            .filterNot { (elementos[it] as Element.TextLabel).titulo }
        if (contadores.isNotEmpty() && bordes.size >= 2) {
            val centros = (0 until bordes.size - 1).map { (bordes[it] + bordes[it + 1]) / 2f }
            y = colocarFilaRotulos(contadores, centros, y) + ce(26f)
        }

        etiquetaDelMarco(marcoIndex, ROL_ALFEIZAR)?.let { i ->
            val alfeizar = elementos[i] as Element.TextLabel
            // Solo la medida: de qué es lo dice su dibujito, que va pegado a la izquierda. El
            // rótulo largo y centrado se comía el medio del papel.
            alfeizar.text = soloMedida(alfeizar.text)
            sketchTextPaint.textSize = tamanoTexto(alfeizar)
            // A un lado, no en medio: alineado con el canto izquierdo, dejando sitio al dibujito.
            alfeizar.x = marco.rect.left + tamanoTexto(alfeizar) * 1.4f
            alfeizar.y = y + tamanoTexto(alfeizar) * 0.9f
        }
    }

    /** Centra cada rótulo en su x; el que no cabe pasa al segundo renglón. Devuelve la y usada. */
    private fun colocarFilaRotulos(indices: List<Int>, centros: List<Float>, baseY: Float): Float {
        val derecha = floatArrayOf(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY)
        var usada = baseY
        indices.forEachIndexed { orden, i ->
            val etiqueta = elementos.getOrNull(i) as? Element.TextLabel ?: return@forEachIndexed
            val tamano = tamanoTexto(etiqueta)
            sketchTextPaint.textSize = tamano
            val ancho = sketchTextPaint.measureText(etiqueta.text)
            val x = (centros.getOrNull(orden) ?: return@forEachIndexed) - ancho / 2f
            val hueco = ce(10f)
            val renglon = when {
                x >= derecha[0] + hueco -> 0
                x >= derecha[1] + hueco -> 1
                else -> 0
            }
            etiqueta.x = x
            etiqueta.y = baseY + renglon * tamano * 1.3f
            derecha[renglon] = x + ancho
            usada = maxOf(usada, etiqueta.y)
        }
        return usada
    }

    /** Las cotas de alto interiores de un marco, ordenadas como están en el dibujo. */
    private fun altosDelMarco(marcoIndex: Int): List<Int> = altosDelMarco(marcoIndex, null)

    /** Las cotas de alto interiores: las libres, las clavadas en aristas, o ambas si [hint] es null. */
    private fun altosDelMarco(marcoIndex: Int, hint: String?): List<Int> {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return emptyList()
        return elementos.indices.filter { i ->
            val a = elementos.getOrNull(i) as? Element.Shape
            a != null && (if (hint != null) a.cotaHint == hint else a.cotaHint in setOf(VENTANA_ALTO, VENTANA_ALTO_ESQUINA)) &&
                a.start.x >= marco.rect.left - cmToPx(2f) && a.start.x <= marco.rect.right + cmToPx(2f) &&
                a.start.y >= marco.rect.top - cmToPx(2f) && a.start.y <= marco.rect.bottom + cmToPx(2f)
        }
    }

    /** Las que se añaden y se quitan con el contador; las de arista van con su esquina. */
    private fun altosLibres(marcoIndex: Int): List<Int> = altosDelMarco(marcoIndex, VENTANA_ALTO)

    /**
     * Reparte las cotas de alto a lo ancho del vano y les da su altura.
     *
     * La que nadie ha escrito sigue al dibujo (el alto que le toca entre un canto y el otro); la que
     * se escribió a mano manda, y es la que quiebra la línea de arriba: ahí el vano mide eso.
     */
    private fun repartirAltos(marcoIndex: Int, reinterpolar: Boolean = true) {
        val marco = (elementos.getOrNull(marcoIndex) as? Element.Shape) ?: return
        // La cota clavada en una arista no se reparte: va donde dobla la ventana.
        val aristas = quiebresDelMarco(marcoIndex).map { (elementos[it] as Element.Shape).end.x }
        altosDelMarco(marcoIndex, VENTANA_ALTO_ESQUINA).forEachIndexed { orden, i ->
            colocarAlto(i, aristas.getOrNull(orden) ?: marco.rect.centerX(), marco, reinterpolar)
        }
        // Cada tramo reparte LAS SUYAS: son paredes distintas, y una cota del tramo de al lado no
        // dice nada de esta.
        val bordes = bordesDeTramos(marcoIndex)
        altosLibresPorTramo(marcoIndex).forEachIndexed { tramo, enElTramo ->
            val izq = bordes.getOrNull(tramo) ?: marco.rect.left
            val ancho = (bordes.getOrNull(tramo + 1) ?: marco.rect.right) - izq
            enElTramo.forEachIndexed { orden, i ->
                colocarAlto(i, izq + ancho * (orden + 1f) / (enElTramo.size + 1f), marco, reinterpolar)
            }
        }
    }

    /** Las cotas de alto libres, agrupadas por el tramo en el que están hoy. */
    private fun altosLibresPorTramo(marcoIndex: Int): List<List<Int>> {
        val bordes = bordesDeTramos(marcoIndex)
        if (bordes.size < 2) return emptyList()
        val grupos = List(bordes.size - 1) { mutableListOf<Int>() }
        altosLibres(marcoIndex).forEach { i ->
            val x = (elementos[i] as Element.Shape).start.x
            val tramo = (bordes.indexOfLast { it <= x + 0.5f }).coerceIn(0, bordes.size - 2)
            grupos[tramo].add(i)
        }
        return grupos
    }

    private fun colocarAlto(altoIndex: Int, x: Float, marco: Element.Shape, reinterpolar: Boolean = true) {
        val alto = elementos.getOrNull(altoIndex) as? Element.Shape ?: return
        // Sin reinterpolar, la cota conserva SU altura: cambiar el ancho de un tramo no puede
        // moverle el alto a la arista de al lado, que es una medida ya tomada de otra pared.
        val yTecho = if (alto.largoFijado || !reinterpolar) {
            marco.rect.bottom - cmToPx(alto.lengthCm)
        } else {
            interpolarTecho(marco, x)
        }
        alto.start.set(x, marco.rect.bottom)
        alto.end.set(x, yTecho)
        alto.rect.set(x, minOf(yTecho, marco.rect.bottom), x, maxOf(yTecho, marco.rect.bottom))
        alto.lengthCm = pxToCm(marco.rect.bottom - yTecho)
    }

    /** El techo que le toca a esa x según cómo caiga la línea de arriba entre los dos cantos. */
    private fun interpolarTecho(marco: Element.Shape, x: Float): Float {
        val izq = marco.topLeft
        val der = marco.topRight
        val ancho = der.x - izq.x
        if (abs(ancho) < 1f) return izq.y
        val t = ((x - izq.x) / ancho).coerceIn(0f, 1f)
        return izq.y + (der.y - izq.y) * t
    }

    /**
     * La línea de arriba del vano, quebrada por las alturas que se hayan escrito dentro.
     *
     * Un vano de obra casi nunca tiene el dintel a nivel; el dibujo tiene que enseñar esa caída, no
     * un rectángulo perfecto que contradiga las cotas.
     */
    private fun pathMarcoVentana(marcoIndex: Int, shape: Element.Shape): Path? {
        // La línea de arriba pasa por las aristas de esquina —ahí dobla— y por los altos que se
        // hayan escrito dentro de cada tramo.
        val puntos = quiebresDelMarco(marcoIndex).map { (elementos[it] as Element.Shape).start } +
            altosLibres(marcoIndex)
                .map { elementos[it] as Element.Shape }
                .filter { it.largoFijado }
                .map { it.end }
        val quiebres = puntos.sortedBy { it.x }
        if (quiebres.isEmpty()) return null
        return Path().apply {
            moveTo(shape.topLeft.x, shape.topLeft.y)
            quiebres.forEach { lineTo(it.x, it.y) }
            lineTo(shape.topRight.x, shape.topRight.y)
            lineTo(shape.bottomRight.x, shape.bottomRight.y)
            lineTo(shape.bottomLeft.x, shape.bottomLeft.y)
            close()
        }
    }

    /** Un lado del dibujo con su medida: una pieza a cortar. */
    data class LadoMedido(val cm: Float, val etiqueta: String)

    /**
     * Todos los lados del perímetro de todas las figuras, con su medida.
     *
     * Es la lista de corte del apunte: lo que hay dibujado son perfiles, y cada lado es una pieza.
     * Quedan fuera las líneas que solo sirven para acotar (las cotas de alto por dentro del vano) y
     * los trazos a mano alzada, que no tienen medida que dar.
     */
    fun ladosDelPerimetro(): List<LadoMedido> {
        val lados = mutableListOf<LadoMedido>()
        var figura = 0
        fun recorrer(element: Element, index: Int) {
            when (element) {
                is Element.Group -> element.children.forEach { recorrer(it, index) }
                is Element.Shape -> {
                    figura++
                    lados += ladosDeShape(element, index, figura)
                }
                is Element.Composite -> {
                    figura++
                    lados += ladosDeComposite(element, figura)
                }
                else -> Unit
            }
        }
        elementos.forEachIndexed { index, element -> recorrer(element, index) }
        return lados.filter { it.cm >= 0.5f }
    }

    private fun ladosDeShape(shape: Element.Shape, index: Int, figura: Int): List<LadoMedido> {
        // Las cotas de alto son medidas, no piezas; la cota total de la gradería tampoco se corta.
        if (shape.cotaHint in setOf(VENTANA_ALTO, VENTANA_ALTO_ESQUINA, "GRADA_TOTAL_COTA")) return emptyList()
        val nombre = nombreFigura(shape, figura)
        fun lado(a: PointF, b: PointF) = LadoMedido(pxToCm(distancia(a, b)), nombre)
        return when (shape.tool) {
            Tool.RECTANGLE -> {
                // En los marcos de plantilla la línea de arriba va quebrada por las aristas y por
                // los altos escritos: cada tramo de esa línea es una pieza distinta.
                val arriba = if (shape.cotaHint in MARCOS_PLANTILLA) puntosArriba(index) else emptyList()
                val superiores = if (arriba.size > 2) {
                    (0 until arriba.size - 1).map { lado(arriba[it], arriba[it + 1]) }
                } else {
                    listOf(lado(shape.topLeft, shape.topRight))
                }
                superiores + listOf(
                    lado(shape.topRight, shape.bottomRight),
                    lado(shape.bottomLeft, shape.bottomRight),
                    lado(shape.topLeft, shape.bottomLeft)
                )
            }
            Tool.TRIANGLE -> {
                val (punta, der, izq) = verticesTriangulo(shape)
                listOf(lado(izq, der), lado(der, punta), lado(punta, izq))
            }
            // Del círculo se corta su contorno: el desarrollo de la circunferencia.
            Tool.CIRCLE -> listOf(LadoMedido(shape.diameterCm * Math.PI.toFloat(), nombre))
            Tool.LINE, Tool.ORTHO_LINE -> listOf(LadoMedido(shape.lengthCm, nombre))
            Tool.NONE, Tool.FREEHAND, Tool.MAGNET_PEN, Tool.TEXT, Tool.SELECT, Tool.NODO -> emptyList()
        }
    }

    private fun ladosDeComposite(composite: Element.Composite, figura: Int): List<LadoMedido> {
        val nombre = "Forma $figura"
        return composite.sideCms.flatten().map { LadoMedido(it, nombre) }
    }

    private fun nombreFigura(shape: Element.Shape, figura: Int): String = when (shape.cotaHint) {
        PUERTA_MARCO -> "Puerta"
        VENTANA_MARCO -> "Ventana"
        MAMPARA_MARCO -> "Mampara"
        ESQUINA_MARCO -> "V. esquina"
        PUERTA_PUENTE -> "Puente"
        PUERTA_DIVISION -> "Division"
        ESQUINA_QUIEBRE -> "Arista"
        else -> when (shape.tool) {
            Tool.RECTANGLE -> "Rect $figura"
            Tool.TRIANGLE -> "Triang $figura"
            Tool.CIRCLE -> "Circulo $figura"
            Tool.LINE, Tool.ORTHO_LINE -> "Linea $figura"
            else -> "Figura $figura"
        }
    }

    fun productoDelDibujo(): String? {
        val hints = elementos.mapNotNull { (it as? Element.Shape)?.cotaHint }
        return when {
            PUERTA_MARCO in hints -> "Puerta"
            VENTANA_MARCO in hints -> "Ventana"
            MAMPARA_MARCO in hints -> "Mampara"
            ESQUINA_MARCO in hints -> "Ventana"
            else -> null
        }
    }

    private fun esMarcoPlantilla(index: Int): Boolean =
        (elementos.getOrNull(index) as? Element.Shape)?.cotaHint in MARCOS_PLANTILLA

    private fun esPuente(index: Int): Boolean =
        (elementos.getOrNull(index) as? Element.Shape)?.cotaHint == PUERTA_PUENTE

    /**
     * Zona de influencia del marco: lo que cae dentro se considera parte de ESA puerta.
     *
     * No hay grupos en el apunte —cada figura y cada símbolo viven sueltos—, así que la pertenencia
     * se decide por sitio. Se estira hacia abajo porque el michi va debajo del marco.
     */
    private fun zonaDelMarco(marco: Element.Shape): RectF {
        // Holgada de sobra: por debajo va la banda de rótulos (ángulos, contadores, alfeizar), que
        // se mide en pantalla y con el dibujo reducido cae lejos en coordenadas del apunte.
        val margen = maxOf(symbolSize() * 2.5f, ce(140f))
        return RectF(marco.rect).apply {
            inset(-margen, -margen)
            bottom += maxOf(symbolSize() * 2f, ce(180f))
            // Y en la de esquina, la planta y sus ángulos, que cuelgan bastante más abajo: fuera de
            // la zona dejaban de contarse como piezas del marco y el rótulo se quedaba colgado.
            if (marco.cotaHint == ESQUINA_MARCO) {
                bottom += huecoPlantaPx + marco.rect.width() + cmToPx(30f)
                right += marco.rect.width()
            }
        }
    }

    /** Símbolos y rótulos al pie que acompañan a un marco. */
    private fun piezasDelMarco(marcoIndex: Int): List<Int> {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return emptyList()
        if (marco.cotaHint !in MARCOS_PLANTILLA) return emptyList()
        val zona = zonaDelMarco(marco)
        return elementos.indices.filter { i ->
            when (val e = elementos[i]) {
                is Element.Symbol -> e.drawableName in SIMBOLOS_PUERTA &&
                    zona.contains(e.rect.centerX(), e.rect.centerY())
                is Element.TextLabel -> e.rol in ROLES_AL_PIE && zona.contains(e.x, e.y)
                else -> false
            }
        }
    }

    /** El marco al que pertenece una pieza (símbolo, michi o puente). */
    private fun marcoDePieza(index: Int): Int? {
        val el = elementos.getOrNull(index) ?: return null
        val punto = when (el) {
            is Element.Symbol -> PointF(el.rect.centerX(), el.rect.centerY())
            is Element.TextLabel -> PointF(el.x, el.y)
            is Element.Shape -> PointF(el.rect.centerX(), el.rect.centerY())
            else -> return null
        }
        // Los rótulos (identificador, hojas, michi) son de la puerta aunque el zoom los deje lejos
        // del marco: para ellos vale el marco más cercano, sin pedir que caigan dentro de su zona.
        val porCercania = (el as? Element.TextLabel)?.rol != null
        return elementos.indices
            .filter { esMarcoPlantilla(it) && it != index }
            .filter { porCercania || zonaDelMarco(elementos[it] as Element.Shape).contains(punto.x, punto.y) }
            .minByOrNull { i ->
                val r = (elementos[i] as Element.Shape).rect
                hypot((r.centerX() - punto.x).toDouble(), (r.centerY() - punto.y).toDouble())
            }
    }

    private fun textoHojas(hojas: Int): String = "N° de puertas  $hojas"

    /** La línea que parte el vano: si está, la puerta es de dos hojas. */
    private fun divisionDePuerta(marcoIndex: Int): Int? {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return null
        return elementos.indices.firstOrNull { i ->
            val d = elementos.getOrNull(i) as? Element.Shape
            d != null && d.cotaHint == PUERTA_DIVISION &&
                d.start.x >= marco.rect.left - 1f && d.start.x <= marco.rect.right + 1f &&
                maxOf(d.start.y, d.end.y) > marco.rect.top && minOf(d.start.y, d.end.y) < marco.rect.bottom
        }
    }

    private fun hojasDePuerta(marcoIndex: Int): Int = if (divisionDePuerta(marcoIndex) != null) 2 else 1

    /** El rótulo de la puerta con ese papel: el más cercano al marco. */
    private fun etiquetaDelMarco(marcoIndex: Int, rol: String): Int? {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return null
        return elementos.indices
            .filter { (elementos[it] as? Element.TextLabel)?.rol == rol }
            .minByOrNull {
                val t = elementos[it] as Element.TextLabel
                hypot((t.x - marco.rect.left).toDouble(), (t.y - marco.rect.top).toDouble())
            }
    }

    /**
     * Una hoja o dos. La segunda hoja trae su línea de división, sus bisagras en el otro canto y su
     * propia flecha de apertura; al volver a una, todo eso se retira.
     */
    private fun alternarHojas(etiquetaIndex: Int) {
        val marcoIndex = marcoDePieza(etiquetaIndex) ?: return
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
        val division = divisionDePuerta(marcoIndex)
        if (division != null) quitarSegundaHoja(marcoIndex, division) else ponerSegundaHoja(marcoIndex, marco)
        // Quitar piezas corre la lista: el marco se vuelve a localizar por la figura misma.
        val ahora = elementos.indexOfFirst { it === marco }
        if (ahora < 0) return
        etiquetaDelMarco(ahora, ROL_HOJAS)?.let {
            (elementos[it] as Element.TextLabel).text = textoHojas(hojasDePuerta(ahora))
        }
        colocarPiezasDelMarco(ahora)
        registrarAccion()
        invalidate()
    }

    private fun ponerSegundaHoja(marcoIndex: Int, marco: Element.Shape) {
        val techo = techoHojaPuerta(marcoIndex, marco.rect)
        val cx = marco.rect.centerX()
        elementos.add(
            crearShape(Tool.LINE, PointF(cx, techo), PointF(cx, marco.rect.bottom), PUERTA_DIVISION)
        )
        if (drawableIdForName("bisagra") != 0) {
            repeat(3) { elementos.add(Element.Symbol("bisagra", RectF(marco.rect))) }
        }
        // La segunda hoja abre a su manera: nace con la misma dirección que la primera y desde ahí
        // se la toca aparte.
        val apertura = piezasDelMarco(marcoIndex)
            .mapNotNull { elementos.getOrNull(it) as? Element.Symbol }
            .firstOrNull { it.drawableName in SIMBOLOS_APERTURA }
            ?.drawableName
            ?: "adentro"
        if (drawableIdForName(apertura) != 0) {
            elementos.add(Element.Symbol(apertura, RectF(marco.rect)))
        }
        selectedIndices.clear()
    }

    private fun quitarSegundaHoja(marcoIndex: Int, divisionIndex: Int) {
        val piezas = piezasDelMarco(marcoIndex)
        val bisagras = piezas.filter { (elementos[it] as? Element.Symbol)?.drawableName == "bisagra" }
        val aperturas = piezas.filter { (elementos[it] as? Element.Symbol)?.drawableName in SIMBOLOS_APERTURA }
        // Sobran las bisagras del segundo canto y la flecha de la segunda hoja: se van las últimas,
        // que son las que puso la doble hoja.
        val sobran = (bisagras.drop(3) + aperturas.drop(1) + divisionIndex).sortedDescending()
        sobran.forEach { if (it in elementos.indices) elementos.removeAt(it) }
        selectedIndices.clear()
    }

    /** Los puentes del marco, de izquierda a derecha: uno por tramo. */
    private fun puentesDelMarco(marcoIndex: Int): List<Int> {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return emptyList()
        return elementos.indices
            .filter { i ->
                val p = elementos.getOrNull(i) as? Element.Shape
                p != null && p.cotaHint == PUERTA_PUENTE &&
                    p.start.y >= marco.rect.top - 1f && p.start.y <= marco.rect.bottom + 1f &&
                    maxOf(p.start.x, p.end.x) > marco.rect.left && minOf(p.start.x, p.end.x) < marco.rect.right
            }
            .sortedBy { (elementos[it] as Element.Shape).rect.centerX() }
    }

    private fun puenteDelMarco(marcoIndex: Int): Int? = puentesDelMarco(marcoIndex).firstOrNull()

    /** En qué tramo cae esa x, según los cortes del pie. */
    private fun tramoDeX(bordes: List<Float>, x: Float): Int {
        if (bordes.size < 2) return 0
        return bordes.indexOfLast { it <= x + 0.5f }.coerceIn(0, bordes.size - 2)
    }

    /**
     * Deja la puerta coherente después de tocar cualquiera de sus medidas: el puente vuelve a ir de
     * canto a canto y a su altura del piso, y los símbolos se recolocan alrededor.
     *
     * [alturaCm] es la altura que tenía el puente ANTES del cambio: al alargar la puerta, el puente
     * se queda a sus 200 del piso en vez de quedarse clavado donde estaba.
     */
    private fun sincronizarMarco(
        marcoIndex: Int,
        // Las cotas de alto se recalculan de la línea de arriba solo cuando lo que cambió fue una
        // altura; tocando anchos, cada una conserva la suya.
        reinterpolarAltos: Boolean = true
    ) {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
        // Cada tramo lleva SU puente: en una ventana que dobla, el travesaño de una pared no tiene
        // por qué ir a la misma altura que el de la otra.
        val bordes = bordesDeTramos(marcoIndex)
        val piso = marco.bottomLeft.y
        puentesDelMarco(marcoIndex).forEach { i ->
            val puente = elementos[i] as Element.Shape
            val altoPx = (piso - marco.rect.top).coerceAtLeast(cmToPx(2f))
            val desdePiso = (piso - puente.start.y).coerceIn(cmToPx(1f), altoPx - cmToPx(1f))
            val y = piso - desdePiso
            if (puente.largoFijado) {
                // Puente con largo propio: solo cambia de altura; lo que sobresalga o falte para
                // los lados se queda como está, que para eso se le escribió esa medida.
                puente.start.y = y
                puente.end.y = y
            } else {
                val tramo = tramoDeX(bordes, (puente.start.x + puente.end.x) / 2f)
                val izq = bordes.getOrNull(tramo) ?: marco.bottomLeft.x
                val der = bordes.getOrNull(tramo + 1) ?: marco.bottomRight.x
                puente.start.set(izq, y)
                puente.end.set(der, y)
                puente.lengthCm = pxToCm(der - izq)
            }
            puente.rect.set(
                minOf(puente.start.x, puente.end.x), y,
                maxOf(puente.start.x, puente.end.x), y
            )
        }
        // La división se queda donde está —la hoja izquierda conserva su ancho— y solo se recorta a
        // lo que dé el marco; el resto se lo lleva la otra hoja.
        divisionDePuerta(marcoIndex)?.let { i ->
            val division = elementos[i] as Element.Shape
            val techo = techoHojaPuerta(marcoIndex, marco.rect)
            val x = division.start.x.coerceIn(
                marco.rect.left + cmToPx(1f),
                (marco.rect.right - cmToPx(1f)).coerceAtLeast(marco.rect.left + cmToPx(1f))
            )
            division.start.set(x, techo)
            division.end.set(x, marco.rect.bottom)
            division.rect.set(x, techo, x, marco.rect.bottom)
            division.lengthCm = pxToCm(marco.rect.bottom - techo)
        }
        repartirAltos(marcoIndex, reinterpolarAltos)
        sincronizarEsquina(marcoIndex)
        colocarContadoresAltos(marcoIndex)
        colocarBandaRotulos(marcoIndex)
        actualizarRotulo(marcoIndex)
        colocarPiezasDelMarco(marcoIndex)
    }

    /** Deja cada arista de arriba abajo del vano y su ángulo escrito al lado. */
    private fun sincronizarEsquina(marcoIndex: Int) {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
        val quiebres = quiebresDelMarco(marcoIndex)
        if (quiebres.isEmpty()) return
        val anclados = altosDelMarco(marcoIndex, VENTANA_ALTO_ESQUINA)
        quiebres.forEachIndexed { orden, i ->
            val q = elementos[i] as Element.Shape
            // La arista tiene SU punto de arriba y SU punto de abajo: el lado de arriba de un tramo
            // y el de abajo son dos medidas distintas —la pared no viene a plomo— y cada una se
            // escribe sin tocar a la otra. Por eso la arista puede quedar ligeramente inclinada.
            val xAbajo = q.end.x.coerceIn(marco.bottomLeft.x, marco.bottomRight.x)
            val xArriba = q.start.x.coerceIn(marco.topLeft.x, marco.topRight.x)
            val piso = marco.bottomLeft.y
            // Si se escribió el alto de la esquina, la arista llega hasta ahí; si no, hasta la
            // línea de arriba.
            val techo = anclados.getOrNull(orden)
                ?.let { (elementos[it] as Element.Shape).end.y }
                ?: interpolarTecho(marco, xArriba)
            q.start.set(xArriba, techo)
            q.end.set(xAbajo, piso)
            q.rect.set(
                minOf(xArriba, xAbajo), minOf(techo, piso),
                maxOf(xArriba, xAbajo), maxOf(techo, piso)
            )
            q.lengthCm = pxToCm(piso - techo)
            // El ángulo va FUERA del dibujo: lo coloca la banda de rótulos, bajo su arista.
        }
    }

    /** Los ángulos escritos de un marco de esquina, en el orden de sus aristas. */
    /**
     * Los rótulos de ángulo del marco, EN EL ORDEN DE SUS ARISTAS: el primero es el de la primera
     * esquina de la alzada, de izquierda a derecha.
     *
     * Van por el orden en que se crearon, que es ese, y NO por dónde están dibujados. Desde que
     * los ángulos se leen en la planta, su sitio lo pone el recorrido doblado: en una ventana en C
     * la segunda esquina cae a la izquierda de la primera, así que ordenándolos por su x quedaban
     * cruzados —lo que se escribía en una curva se le aplicaba a la otra—.
     */
    private fun etiquetasEsquina(marcoIndex: Int): List<Int> {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return emptyList()
        val zona = zonaDelMarco(marco)
        return elementos.indices.filter { i ->
            val t = elementos.getOrNull(i) as? Element.TextLabel
            t != null && t.rol == ROL_ESQUINA && zona.contains(t.x, t.y)
        }
    }

    private fun rotuloMarco(
        hint: String?,
        anchoCm: Float,
        altoCm: Float,
        curva: Boolean = false
    ): String {
        val nombre = when {
            // La ventana curva comparte la maquinaria de la de esquina —tramos, planta, cotas—
            // pero no es la misma ventana, y en el papel tiene que decir lo que es.
            hint == ESQUINA_MARCO && curva -> "VENTANA CURVA"
            hint == ESQUINA_MARCO -> "VENTANA ESQUINA"
            hint == VENTANA_MARCO -> "VENTANA"
            hint == MAMPARA_MARCO -> "MAMPARA"
            else -> "PUERTA"
        }
        return "$nombre  ${formatCm(anchoCm)} x ${formatCm(altoCm)}"
    }

    private fun actualizarRotulo(marcoIndex: Int) {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
        etiquetaDelMarco(marcoIndex, ROL_TITULO_PUERTA)?.let {
            (elementos[it] as Element.TextLabel).text =
                rotuloMarco(marco.cotaHint, marco.widthCm, marco.heightCm, esMarcoCurvo(marcoIndex))
        }
    }

    private fun bisagrasALaIzquierda(bisagras: List<Int>, marco: RectF): Boolean {
        val x = bisagras.mapNotNull { (elementos.getOrNull(it) as? Element.Symbol)?.rect?.centerX() }
        if (x.isEmpty()) return true
        return x.average() <= marco.centerX()
    }

    /** Por dónde empieza la hoja: si hay puente, debajo de él; si no, el marco entero. */
    private fun techoHojaPuerta(marcoIndex: Int, marco: RectF): Float {
        val puente = puenteDelMarco(marcoIndex)?.let { elementos[it] as Element.Shape } ?: return marco.top
        return puente.start.y.coerceIn(marco.top, marco.bottom)
    }

    private fun centroHojaPuerta(marcoIndex: Int, marco: RectF): Float =
        (techoHojaPuerta(marcoIndex, marco) + marco.bottom) / 2f

    private fun colocarPiezasDelMarco(
        marcoIndex: Int,
        piezas: List<Int> = piezasDelMarco(marcoIndex)
    ) {
        val marco = (elementos.getOrNull(marcoIndex) as? Element.Shape)?.rect ?: return
        fun simbolo(i: Int) = elementos.getOrNull(i) as? Element.Symbol

        // Las hojas mandan el reparto: con dos, cada una cuelga de su propio canto y lleva su flecha.
        val divisionIndex = divisionDePuerta(marcoIndex)
        val division = divisionIndex?.let { elementos[it] as Element.Shape }
        val dobleHoja = division != null
        // Las bisagras cuelgan de la HOJA, no del marco entero: donde hay puente, la hoja es lo que
        // queda debajo de él (encima va el fijo, que no lleva bisagra).
        val techoHoja = techoHojaPuerta(marcoIndex, marco)

        val bisagras = piezas.filter { simbolo(it)?.drawableName == "bisagra" }
        val ladoIzquierdo = if (dobleHoja) true else bisagrasALaIzquierda(bisagras, marco)
        if (bisagras.isNotEmpty()) {
            val hs = hingeSize()
            val altoHoja = (marco.bottom - techoHoja).coerceAtLeast(hs)
            val ys = listOf(
                techoHoja + altoHoja * 0.18f,
                techoHoja + altoHoja * 0.5f,
                techoHoja + altoHoja * 0.82f
            )
            bisagras.forEachIndexed { orden, i ->
                val cy = ys.getOrNull(orden % 3) ?: marco.centerY()
                // Con dos hojas, las tres primeras van al canto izquierdo y las tres siguientes al
                // derecho: cada hoja gira hacia su lado.
                val sideX = when {
                    !dobleHoja -> if (ladoIzquierdo) marco.left else marco.right
                    orden < 3 -> marco.left
                    else -> marco.right
                }
                simbolo(i)?.rect?.set(sideX - hs / 2f, cy - hs / 2f, sideX + hs / 2f, cy + hs / 2f)
            }
        }

        val size = symbolSize()
        val margen = 12f * resources.displayMetrics.density
        val cyApertura = (techoHoja + marco.bottom) / 2f
        var ordenApertura = 0
        piezas.forEach { i ->
            val s = simbolo(i) ?: return@forEach
            when (s.drawableName) {
                "interior", "exterior" ->
                    s.rect.set(marco.right + margen, marco.top, marco.right + margen + size, marco.top + size)
                in SIMBOLOS_APERTURA -> {
                    val hojaIzquierda = !dobleHoja || ordenApertura == 0
                    val cx = when {
                        !dobleHoja -> marco.centerX()
                        hojaIzquierda -> (marco.left + (division?.start?.x ?: marco.centerX())) / 2f
                        else -> ((division?.start?.x ?: marco.centerX()) + marco.right) / 2f
                    }
                    s.rect.set(cx - size / 2f, cyApertura - size / 2f, cx + size / 2f, cyApertura + size / 2f)
                    // El dibujo trae la punta a la derecha: cuando las bisagras de ESA hoja están a
                    // la izquierda se le da la vuelta, para que la flecha acabe del lado que gira.
                    s.reflejado = if (dobleHoja) hojaIzquierda else ladoIzquierdo
                    ordenApertura++
                }
            }
        }

        // El michi va con su símbolo, y los dos son parte del dibujo: se quedan bajo el marco.
        val michi = piezas.firstOrNull { simbolo(it)?.drawableName == "michi" }
        val etiqueta = piezas.mapNotNull { elementos.getOrNull(it) as? Element.TextLabel }
            .firstOrNull { it.rol == ROL_MICHI }
        if (michi == null && etiqueta == null) return
        val gap = 8f * resources.displayMetrics.density
        sketchTextPaint.textSize = etiqueta?.textSize ?: 34f
        val anchoTexto = etiqueta?.let { sketchTextPaint.measureText(it.text) } ?: 0f
        val anchoGrupo = (if (michi != null) size + gap else 0f) + anchoTexto
        // Bien por debajo de la cota de abajo: el michi es un dato aparte y no debe estorbar a la
        // medida ni al dibujo.
        val cy = marco.bottom + size * 2.6f
        var x = marco.centerX() - anchoGrupo / 2f
        if (michi != null) {
            simbolo(michi)?.rect?.set(x, cy - size / 2f, x + size, cy + size / 2f)
            x += size + gap
        }
        etiqueta?.let {
            it.x = x
            it.y = cy + 12f
        }
    }

    /**
     * Toque sobre una pieza de plantilla. Devuelve si el gesto era para ella.
     *
     * Un tap la cambia de estado y una pulsación larga quita el puente. Si el dedo se arrastra, el
     * gesto deja de ser suyo: con la selección puesta pasa a mover la figura y con el lienzo en
     * mano, a mover el lienzo.
     */
    private fun manejarPiezaPlantilla(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val p = screenToWorld(event.x, event.y)
                val pieza = piezaEn(p.x, p.y) ?: return false
                parent?.requestDisallowInterceptTouchEvent(true)
                piezaPendiente = pieza
                piezaDownXY = PointF(event.x, event.y)
                piezaLongPressFired = false
                postDelayed(piezaLongPressRunnable, 450L)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (piezaPendiente == null) return false
                val slop = android.view.ViewConfiguration.get(context).scaledTouchSlop
                if (hypot((event.x - piezaDownXY.x).toDouble(), (event.y - piezaDownXY.y).toDouble()) > slop) {
                    removeCallbacks(piezaLongPressRunnable)
                    piezaPendiente = null
                    if (herramienta == Tool.SELECT) {
                        val inicio = screenToWorld(piezaDownXY.x, piezaDownXY.y)
                        val ahora = screenToWorld(event.x, event.y)
                        manejarSeleccionDown(inicio.x, inicio.y)
                        moverSeleccion(ahora.x, ahora.y)
                    } else {
                        panSinHerramienta = true
                        lastPanPoint = PointF(event.x, event.y)
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val pieza = piezaPendiente ?: return false
                removeCallbacks(piezaLongPressRunnable)
                piezaPendiente = null
                parent?.requestDisallowInterceptTouchEvent(false)
                if (!piezaLongPressFired && event.actionMasked == MotionEvent.ACTION_UP) {
                    val inicio = screenToWorld(piezaDownXY.x, piezaDownXY.y)
                    // Con la selección puesta la pieza además queda seleccionada, que es lo que hacía
                    // el tap antes: así se la puede seguir borrando o duplicando.
                    if (herramienta == Tool.SELECT) {
                        manejarSeleccionDown(inicio.x, inicio.y)
                        finalizarMoverSeleccion()
                    }
                    tocarPieza(pieza, inicio)
                }
                return true
            }
        }
        return false
    }

    // ===================== Herramienta de nodos =====================
    //
    // El lápiz magnético deja el contorno casi hecho, pero la esquina que no cerró hay que rematarla
    // a mano. Con esta herramienta se toca la esquina —no la figura— se arrastra sola, y si se
    // suelta encima de otra las dos se unen; cuando las líneas dan la vuelta completa, el conjunto
    // pasa a ser una forma cerrada, que ya se acota lado por lado.

    /** Un vértice agarrable: dónde está y cómo se le lleva a otro sitio. */
    private data class Nodo(
        val elementIndex: Int,
        val punto: PointF,
        val mover: (Float, Float) -> Unit
    )

    private var nodoTomado: Nodo? = null
    private var nodoArrastrado = false

    private val nodoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(21, 101, 192)
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val nodoRellenoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(235, 255, 255, 255)
        style = Paint.Style.FILL
    }

    private val nodoActivoPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.rgb(216, 27, 96)
        style = Paint.Style.FILL
    }

    /** Todos los nodos que se pueden agarrar en el dibujo. */
    private fun nodosDelDibujo(): List<Nodo> {
        val nodos = mutableListOf<Nodo>()
        elementos.forEachIndexed { index, element ->
            when (element) {
                is Element.Shape -> nodos += nodosDeShape(index, element)
                is Element.Composite -> nodos += nodosDeComposite(index, element)
                else -> Unit
            }
        }
        return nodos
    }

    private fun nodosDeShape(index: Int, shape: Element.Shape): List<Nodo> {
        fun nodoEsquina(punto: PointF) = Nodo(index, PointF(punto.x, punto.y)) { x, y ->
            punto.set(x, y)
            actualizarShapeDesdePuntos(shape)
        }
        return when (shape.tool) {
            Tool.RECTANGLE -> listOf(
                nodoEsquina(shape.topLeft), nodoEsquina(shape.topRight),
                nodoEsquina(shape.bottomRight), nodoEsquina(shape.bottomLeft)
            )
            Tool.TRIANGLE -> {
                val (punta, der, izq) = verticesTriangulo(shape)
                listOf(
                    Nodo(index, punta) { x, y -> fijarVerticesTriangulo(shape, PointF(x, y), der, izq) },
                    Nodo(index, der) { x, y -> fijarVerticesTriangulo(shape, punta, PointF(x, y), izq) },
                    Nodo(index, izq) { x, y -> fijarVerticesTriangulo(shape, punta, der, PointF(x, y)) }
                )
            }
            Tool.LINE, Tool.ORTHO_LINE -> listOf(nodoEsquina(shape.start), nodoEsquina(shape.end))
            else -> emptyList()
        }
    }

    private fun nodosDeComposite(index: Int, composite: Element.Composite): List<Nodo> {
        val nodos = mutableListOf<Nodo>()
        composite.contours.forEach { contorno ->
            contorno.forEach { punto ->
                nodos += Nodo(index, PointF(punto.x, punto.y)) { x, y ->
                    punto.set(x, y)
                    rebuildCompositePath(composite)
                    refreshCompositeSides(composite)
                    val bounds = boundsForElement(composite)
                    composite.widthCm = pxToCm(bounds.width())
                    composite.heightCm = pxToCm(bounds.height())
                    sincronizarDeclarados(composite)
                }
            }
        }
        return nodos
    }

    private fun radioNodo(): Float = 9f * resources.displayMetrics.density

    private fun nodoEn(x: Float, y: Float): Nodo? {
        val alcance = radioNodo() * 2.2f
        return nodosDelDibujo()
            .filter { distancia(it.punto, PointF(x, y)) <= alcance }
            .minByOrNull { distancia(it.punto, PointF(x, y)) }
    }

    /** El nodo de OTRA figura más cercano a un punto, para unirse con él. */
    private fun nodoParaUnir(punto: PointF, propio: Nodo): Nodo? {
        val alcance = radioNodo() * 2.2f
        return nodosDelDibujo()
            .filter { it.elementIndex != propio.elementIndex && distancia(it.punto, punto) <= alcance }
            .minByOrNull { distancia(it.punto, punto) }
    }

    private fun manejarNodos(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val p = screenToWorld(event.x, event.y)
                val nodo = nodoEn(p.x, p.y)
                if (nodo == null) {
                    nodoTomado = null
                    invalidate()
                    return false
                }
                parent?.requestDisallowInterceptTouchEvent(true)
                nodoTomado = nodo
                nodoArrastrado = false
                invalidate()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val nodo = nodoTomado ?: return false
                val p = screenToWorld(event.x, event.y)
                nodo.mover(p.x, p.y)
                nodoTomado = nodo.copy(punto = PointF(p.x, p.y))
                nodoArrastrado = true
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val nodo = nodoTomado ?: return false
                parent?.requestDisallowInterceptTouchEvent(false)
                if (nodoArrastrado) {
                    // Soltado encima de otro nodo: se clavan en el mismo punto, que es lo que une
                    // dos líneas sueltas.
                    val vecino = nodoParaUnir(nodo.punto, nodo)
                    if (vecino != null) {
                        nodo.mover(vecino.punto.x, vecino.punto.y)
                        nodoTomado = nodo.copy(punto = PointF(vecino.punto))
                    }
                    if (cerrarFormaSiDaLaVuelta()) {
                        nodoTomado = null
                        Toast.makeText(context, "Contorno cerrado", Toast.LENGTH_SHORT).show()
                    }
                    registrarAccion()
                }
                nodoArrastrado = false
                invalidate()
                return true
            }
        }
        return false
    }

    /**
     * Si las líneas sueltas dan la vuelta completa, dejan de ser líneas y pasan a ser una forma.
     *
     * Se sigue la cadena de líneas que se tocan por sus extremos; si vuelve al punto de partida con
     * tres tramos o más, se cambian todas por un contorno cerrado, que ya se acota lado por lado
     * como cualquier forma recurrente.
     */
    private fun cerrarFormaSiDaLaVuelta(): Boolean {
        val lineas = elementos.indices.filter { i ->
            val s = elementos[i] as? Element.Shape
            s != null && (s.tool == Tool.LINE || s.tool == Tool.ORTHO_LINE) && s.cotaHint == null
        }
        if (lineas.size < 3) return false
        val tolerancia = radioNodo()
        fun mismo(a: PointF, b: PointF) = distancia(a, b) <= tolerancia

        lineas.forEach { inicio ->
            val cadena = mutableListOf(inicio)
            val primera = elementos[inicio] as Element.Shape
            val arranque = PointF(primera.start.x, primera.start.y)
            var extremo = PointF(primera.end.x, primera.end.y)
            val vertices = mutableListOf(PointF(arranque.x, arranque.y))
            while (true) {
                vertices.add(PointF(extremo.x, extremo.y))
                if (mismo(extremo, arranque) && cadena.size >= 3) {
                    vertices.removeAt(vertices.lastIndex)
                    return reemplazarPorContorno(cadena, vertices)
                }
                val siguiente = lineas.firstOrNull { candidato ->
                    if (candidato in cadena) return@firstOrNull false
                    val s = elementos[candidato] as Element.Shape
                    mismo(s.start, extremo) || mismo(s.end, extremo)
                } ?: break
                val s = elementos[siguiente] as Element.Shape
                extremo = if (mismo(s.start, extremo)) PointF(s.end.x, s.end.y) else PointF(s.start.x, s.start.y)
                cadena.add(siguiente)
            }
        }
        return false
    }

    private fun reemplazarPorContorno(lineas: List<Int>, vertices: List<PointF>): Boolean {
        if (vertices.size < 3) return false
        val contours = mutableListOf(vertices.map { PointF(it.x, it.y) }.toMutableList())
        val bounds = RectF(
            vertices.minOf { it.x }, vertices.minOf { it.y },
            vertices.maxOf { it.x }, vertices.maxOf { it.y }
        )
        lineas.sortedDescending().forEach { if (it in elementos.indices) elementos.removeAt(it) }
        elementos.add(
            Element.Composite(
                path = pathFromContours(contours),
                widthCm = pxToCm(bounds.width()),
                heightCm = pxToCm(bounds.height()),
                contours = contours,
                sideCms = sideCmsForContours(contours),
                template = TEMPLATE_LIBRE
            )
        )
        selectedIndices.clear()
        return true
    }

    private fun drawNodos(canvas: Canvas) {
        if (herramienta != Tool.NODO) return
        val radio = ce(radioNodo() * 0.55f)
        nodoPaint.strokeWidth = ce(3f)
        nodosDelDibujo().forEach { nodo ->
            canvas.drawCircle(nodo.punto.x, nodo.punto.y, radio, nodoRellenoPaint)
            canvas.drawCircle(nodo.punto.x, nodo.punto.y, radio, nodoPaint)
        }
        nodoTomado?.let { canvas.drawCircle(it.punto.x, it.punto.y, radio * 1.15f, nodoActivoPaint) }
    }

    /** La pieza que hay bajo el dedo, si la hay: se busca de arriba abajo, como se ve. */
    private fun piezaEn(x: Float, y: Float): Int? =
        elementos.indices.lastOrNull { piezaTocada(elementos[it], x, y) }

    private fun piezaTocada(element: Element, x: Float, y: Float): Boolean {
        val pad = 8f * resources.displayMetrics.density
        return when (element) {
            is Element.Symbol -> element.drawableName in SIMBOLOS_PUERTA &&
                RectF(element.rect).apply { inset(-pad, -pad) }.contains(x, y)
            is Element.TextLabel -> element.rol in setOf(
                ROL_MICHI, ROL_HOJAS, ROL_ALFEIZAR, ROL_ALTOS, ROL_ESQUINA, ROL_TRAMOS, ROL_CURVA
            ) &&
                boundsForText(element).apply { inset(-pad * 1.5f, -pad * 1.5f) }.contains(x, y)
            is Element.Shape -> element.cotaHint == PUERTA_PUENTE &&
                distanciaARecta(PointF(x, y), element.start, element.end) <= pad * 1.5f
            else -> false
        }
    }

    private fun tocarPieza(index: Int, punto: PointF) {
        when (val el = elementos.getOrNull(index)) {
            is Element.Symbol -> when (el.drawableName) {
                "bisagra" -> alternarBisagras(index)
                "michi" -> etiquetaMichiDe(index)?.let { editarMichi(it) }
                else -> SIMBOLOS_ALTERNABLES[el.drawableName]?.let { cambiarSimbolo(index, it) }
            }
            is Element.TextLabel -> when (el.rol) {
                ROL_MICHI -> editarMichi(index)
                ROL_HOJAS -> alternarHojas(index)
                ROL_ALFEIZAR -> editarAlfeizar(index)
                ROL_ESQUINA -> editarAnguloEsquina(index)
                // La curva de un tramo se escribe como la de una esquina: desarrollo y cuerda.
                ROL_CURVA -> editarCurvaDeTramo(index)
                ROL_TRAMOS -> cambiarTramos(index, if (punto.x < boundsForText(el).centerX()) -1 else 1)
                // Mitad izquierda (donde está el −) quita; mitad derecha (el +) añade.
                ROL_ALTOS -> cambiarAltos(index, if (punto.x < boundsForText(el).centerX()) -1 else 1)
                else -> Unit
            }
            else -> Unit
        }
    }

    /** Pulsación larga: quita. El puente se va; el contador de cotas de alto baja una. */
    private fun mantenerPieza(index: Int) {
        if ((elementos.getOrNull(index) as? Element.TextLabel)?.rol == ROL_ALTOS) {
            cambiarAltos(index, -1)
            return
        }
        if (!esPuente(index)) return
        quitarPuente(index)
        registrarAccion()
        invalidate()
        Toast.makeText(context, "Puente quitado", Toast.LENGTH_SHORT).show()
    }

    /** La altura del antepecho: un dato del sitio, no del vano, pero va con la ventana. */
    /**
     * El dibujito del alféizar, a la izquierda de su medida: la pared, la repisa y la flecha hasta
     * el piso, que es lo que mide.
     *
     * Antes el rótulo decía "Alfeizar 90" en grande y en medio del papel. Lo que hace falta saber
     * es el número, y de qué es lo dice el dibujo.
     */
    private fun dibujarIconoAlfeizar(canvas: Canvas, etiqueta: Element.TextLabel) {
        val alto = sketchTextPaint.textSize
        val x = etiqueta.x - alto * 1.25f
        val techo = etiqueta.y - alto * 0.78f
        val piso = etiqueta.y + alto * 0.12f
        val ancho = alto * 0.85f
        val grosor = maxOf(alto * 0.07f, 2f)
        iconoPaint.strokeWidth = grosor
        // La pared, de arriba abajo, y la repisa saliendo de ella.
        canvas.drawLine(x, techo, x, piso, iconoPaint)
        canvas.drawLine(x, techo + alto * 0.30f, x + ancho, techo + alto * 0.30f, iconoPaint)
        // Y la flecha de la repisa al piso: la altura que se apunta.
        val xFlecha = x + ancho * 0.62f
        canvas.drawLine(xFlecha, techo + alto * 0.34f, xFlecha, piso, iconoPaint)
        canvas.drawLine(xFlecha, piso, xFlecha - grosor * 2f, piso - grosor * 3f, iconoPaint)
        canvas.drawLine(xFlecha, piso, xFlecha + grosor * 2f, piso - grosor * 3f, iconoPaint)
        // El piso, rayado corto, para que se lea como suelo y no como otra pieza.
        canvas.drawLine(x - grosor, piso, x + ancho, piso, iconoPaint)
    }

    /** El número de un rótulo, sin el nombre: los apuntes viejos traen "Alfeizar  90". */
    private fun soloMedida(texto: String): String {
        val n = texto.filter { it.isDigit() || it == '.' || it == ',' }.replace(",", ".")
            .toFloatOrNull() ?: return texto
        return formatCm(n)
    }

    private fun editarAlfeizar(index: Int) {
        val etiqueta = elementos.getOrNull(index) as? Element.TextLabel ?: return
        val actual = etiqueta.text.filter { it.isDigit() || it == '.' || it == ',' }.replace(",", ".")
        val input = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(actual)
            setSelectAllOnFocus(true)
        }
        AlertDialog.Builder(context)
            .setTitle("Alfeizar (cm)")
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                val nuevo = input.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                    ?: return@setPositiveButton
                etiqueta.text = formatCm(abs(nuevo))
                marcoDePieza(index)?.let { colocarPiezasDelMarco(it) }
                registrarAccion()
                invalidate()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Las piezas y cotas del marco que están a la derecha de esa x.
     *
     * El puente de la pared siguiente ARRANCA en la arista, así que se cuenta; la arista y su alto
     * están justo ahí y se quedan, que son el final de la pared de la izquierda.
     */
    private fun piezasYCotasALaDerecha(marcoIndex: Int, x: Float): List<Int> {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return emptyList()
        val zona = zonaDelMarco(marco)
        return elementos.indices.filter { i ->
            if (i == marcoIndex) return@filter false
            when (val e = elementos[i]) {
                is Element.Shape -> {
                    val izq = minOf(e.start.x, e.end.x)
                    val suyo = zona.contains(e.rect.centerX(), e.rect.centerY())
                    when (e.cotaHint) {
                        PUERTA_PUENTE -> suyo && izq >= x - 0.5f
                        ESQUINA_QUIEBRE, ESQUINA_CURVA_FIN, VENTANA_ALTO_ESQUINA, VENTANA_ALTO ->
                            suyo && izq > x + 0.5f
                        else -> false
                    }
                }
                is Element.TextLabel -> e.rol in setOf(ROL_ESQUINA, ROL_ALTOS) &&
                    zona.contains(e.x, e.y) && e.x > x + 0.5f
                else -> false
            }
        }
    }

    /** Corre una pieza a lo ancho, sin tocar nada más. */
    private fun correrEnX(index: Int, dx: Float) {
        when (val e = elementos.getOrNull(index)) {
            is Element.Shape -> {
                e.start.x += dx; e.end.x += dx
                e.topLeft.x += dx; e.topRight.x += dx
                e.bottomLeft.x += dx; e.bottomRight.x += dx
                e.rect.offset(dx, 0f)
            }
            is Element.TextLabel -> e.x += dx
            else -> Unit
        }
    }

    /**
     * Pone al día la banda que una pared curva ocupa en el desarrollo.
     *
     * La curva es aluminio que hay que cortar, así que en la alzada tiene su trozo: se mete entre
     * las dos paredes con el ancho de su DESARROLLO y el vano crece con él. Aquí no hay esquinas ni
     * ángulos que tocar —el giro lo lleva la curva, que vive en el rótulo de su arista—: esto solo
     * abre, ajusta o cierra ese trozo.
     *
     * [desarrolloCm] en cero cierra la banda y el vano vuelve a lo que medían sus paredes.
     */
    private fun ajustarBandaDeCurva(marcoIndex: Int, arista: Int, desarrolloCm: Float) {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
        val quiebre = quiebresDelMarco(marcoIndex).getOrNull(arista) ?: return
        val xa = (elementos[quiebre] as Element.Shape).end.x
        val banda = bandasDeCurva(marcoIndex).firstOrNull {
            (elementos[it] as Element.Shape).end.x > xa + 0.5f &&
                quiebresDelMarco(marcoIndex).none { q ->
                    val xq = (elementos[q] as Element.Shape).end.x
                    xq > xa + 0.5f && xq < (elementos[it] as Element.Shape).end.x
                }
        }
        val ahoraPx = banda?.let { (elementos[it] as Element.Shape).end.x - xa } ?: 0f
        val quieroPx = if (desarrolloCm > 0.5f) cmToPx(desarrolloCm) else 0f
        val delta = quieroPx - ahoraPx
        if (abs(delta) < 0.5f) return

        // Lo que está a la derecha de la banda se corre: la pared curva ocupa su sitio.
        val bordeDerecho = if (banda != null) xa + ahoraPx else xa
        piezasYCotasALaDerecha(marcoIndex, bordeDerecho)
            .filterNot { it == banda }
            .forEach { correrEnX(it, delta) }
        marco.topRight.x += delta
        marco.bottomRight.x += delta
        actualizarBoundsRectangulo(marco)

        if (quieroPx <= 0f) {
            // Se cierra: se va la banda, el puente que la cruzaba, su contador y las cotas de alto
            // que hubiera dentro. Igual que cuando se quita un tramo: el trozo entero desaparece.
            val puente = puentesDelMarco(marcoIndex).firstOrNull {
                val p = elementos[it] as Element.Shape
                minOf(p.start.x, p.end.x) >= xa - 0.5f && maxOf(p.start.x, p.end.x) <= bordeDerecho + 0.5f
            }
            val contador = contadoresAltos(marcoIndex).firstOrNull {
                val t = elementos[it] as Element.TextLabel
                t.x >= xa - 0.5f && t.x <= bordeDerecho + 0.5f
            }
            val dentro = altosLibres(marcoIndex).filter {
                val a = elementos[it] as Element.Shape
                a.start.x >= xa - 0.5f && a.start.x <= bordeDerecho + 0.5f
            }
            (listOfNotNull(banda, puente, contador) + dentro).distinct().sortedDescending().forEach {
                if (it in elementos.indices) elementos.removeAt(it)
            }
        } else if (banda != null) {
            val linea = elementos[banda] as Element.Shape
            linea.start.x = xa + quieroPx
            linea.end.x = xa + quieroPx
            linea.rect.set(linea.start.x, linea.rect.top, linea.start.x, linea.rect.bottom)
        } else {
            val xb = xa + quieroPx
            elementos.add(
                crearShape(Tool.LINE, PointF(xb, marco.rect.top), PointF(xb, marco.rect.bottom), ESQUINA_CURVA_FIN)
            )
            // Su puente, a la altura de los demás: la curva también es ventana.
            val alturaPuente = puentesDelMarco(marcoIndex).firstOrNull()
                ?.let { (elementos[it] as Element.Shape).start.y }
                ?: (marco.rect.bottom - marco.rect.height() * 0.75f)
            elementos.add(
                crearShape(Tool.LINE, PointF(xa, alturaPuente), PointF(xb, alturaPuente), PUERTA_PUENTE)
            )
            // Y su contador de cotas de alto: la banda es un tramo mÃ¡s, y los contadores van uno
            // por tramo y en su orden. Sin Ã©l, el de la pared de al lado se quedaba mandando en el
            // tramo equivocado.
            elementos.add(
                Element.TextLabel(
                    text = textoAltos(0),
                    x = xa + quieroPx / 2f,
                    y = marco.rect.bottom,
                    textSize = 44f,
                    rol = ROL_ALTOS
                )
            )
        }
        val ahora = elementos.indexOfFirst { it === marco }
        if (ahora >= 0) sincronizarMarco(ahora, reinterpolarAltos = false)
    }

    /** El ángulo con el que dobla la ventana en esa arista; de ahí sale el inglete del parante. */
    private fun editarAnguloEsquina(index: Int) {
        val etiqueta = elementos.getOrNull(index) as? Element.TextLabel ?: return
        val actual = etiqueta.text.filter { it.isDigit() || it == '.' || it == ',' }.replace(",", ".")

        val dp = resources.displayMetrics.density
        val cont = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((20 * dp).toInt(), (12 * dp).toInt(), (20 * dp).toInt(), 0)
        }
        val input = EditText(context).apply {
            hint = "Grados"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(actual)
            setSelectAllOnFocus(true)
        }
        cont.addView(input)
        // Hacia dónde dobla la pared. Una ventana que abraza la esquina de un edificio dobla al
        // revés que la que se mete en un rincón, y en la planta se ve una u otra.
        val afuera = android.widget.CheckBox(context).apply {
            text = "Dobla hacia afuera"
            isChecked = etiqueta.text.trim().let { it.startsWith("-") || it.startsWith("−") }
            setPadding(0, (6 * dp).toInt(), 0, 0)
        }
        cont.addView(afuera)

        // La esquina curva: en vez de doblar en punta, la unen con un arco que va de la esquina de
        // una pared a la de la otra. Se describe con lo que se corta y lo que mide en recto.
        val esquinaActual = esquinaDesdeTexto(etiqueta.text)
        val curva = android.widget.CheckBox(context).apply {
            text = "Esquina curva"
            isChecked = esquinaActual.arco != null
            setPadding(0, (10 * dp).toInt(), 0, 0)
        }
        cont.addView(curva)
        val etDesarrollo = EditText(context).apply {
            hint = "Desarrollo de la curva (lo que se corta)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(esquinaActual.arco?.let { formatCm(it.desarrollo) } ?: "")
        }
        val etCuerda = EditText(context).apply {
            hint = "Cuerda: de esquina a esquina"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(esquinaActual.arco?.let { formatCm(it.cuerda) } ?: "")
        }
        cont.addView(etDesarrollo)
        cont.addView(etCuerda)
        val avisoCurva = TextView(context).apply {
            textSize = 12f
            setPadding(0, (4 * dp).toInt(), 0, 0)
        }
        cont.addView(avisoCurva)
        fun mostrarCurva(activa: Boolean) {
            val visible = if (activa) View.VISIBLE else View.GONE
            etDesarrollo.visibility = visible
            etCuerda.visibility = visible
            avisoCurva.visibility = visible
            input.isEnabled = !activa
        }
        mostrarCurva(curva.isChecked)
        curva.setOnCheckedChangeListener { _, activa -> mostrarCurva(activa) }
        // Con el desarrollo y la cuerda sale cuánto dobla la curva, que es lo que gira la pared.
        val watcherCurva = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val d = etDesarrollo.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                val c = etCuerda.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                if (d == null || c == null) { avisoCurva.text = ""; return }
                val arco = ArcoEsquina.deDesarrolloYCuerda(d, c)
                avisoCurva.text = if (arco == null) {
                    "Esas dos no dan una curva: la cuerda siempre es más corta que el desarrollo."
                } else {
                    "Dobla ${formatCm(arco.anguloGrados)}° · flecha ${formatCm(arco.flecha)} · " +
                        "radio ${formatCm(arco.radio)}"
                }
            }
        }
        etDesarrollo.addTextChangedListener(watcherCurva)
        etCuerda.addTextChangedListener(watcherCurva)
        // En obra nadie lleva goniómetro: se marca lo mismo a cada lado de la esquina y se mide de
        // marca a marca. Con los dos lados y esa distancia sale el ángulo, y sale bien.
        cont.addView(TextView(context).apply {
            text = "O por medida: marca lo mismo a cada lado de la esquina y mide entre las marcas."
            textSize = 12f
            setPadding(0, (14 * dp).toInt(), 0, (4 * dp).toInt())
        })
        val etLado = EditText(context).apply {
            hint = "A cada lado (cm)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText("10")
        }
        val etEntre = EditText(context).apply {
            hint = "Entre las marcas (cm)"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        cont.addView(etLado)
        cont.addView(etEntre)
        val aviso = TextView(context).apply {
            textSize = 12f
            setPadding(0, (6 * dp).toInt(), 0, 0)
        }
        cont.addView(aviso)

        // Lo que se escribe en las marcas manda: el ángulo se recalcula y se ve al momento.
        val watcher = object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun onTextChanged(s: CharSequence?, a: Int, b: Int, c: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                val lado = etLado.text?.toString()?.replace(",", ".")?.toFloatOrNull() ?: 0f
                val entre = etEntre.text?.toString()?.replace(",", ".")?.toFloatOrNull() ?: 0f
                if (lado <= 0f || entre <= 0f) { aviso.text = ""; return }
                val grados = anguloPorMedidas(lado, entre)
                if (grados == null) {
                    aviso.text = "Entre las marcas no puede pasar de ${formatCm(lado * 2)} cm."
                } else {
                    aviso.text = "Ángulo: ${formatCm(grados)}°"
                    input.setText(formatCm(grados))
                }
            }
        }
        etLado.addTextChangedListener(watcher)
        etEntre.addTextChangedListener(watcher)

        AlertDialog.Builder(context)
            .setTitle("Ángulo de la esquina")
            .setView(cont)
            .setPositiveButton("Aceptar") { _, _ ->
                val signo = if (afuera.isChecked) "-" else ""
                if (curva.isChecked) {
                    val d = etDesarrollo.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                    val c = etCuerda.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                    val arco = if (d != null && c != null) ArcoEsquina.deDesarrolloYCuerda(d, c) else null
                    if (arco == null) {
                        Toast.makeText(
                            context,
                            "La curva necesita su desarrollo y su cuerda, y la cuerda más corta.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@setPositiveButton
                    }
                    etiqueta.text = "$signo$MARCA_CURVA ${formatCm(d!!)}|${formatCm(c!!)}"
                } else {
                    val nuevo = input.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                        ?: return@setPositiveButton
                    // El signo guarda el sentido: en menos, la pared dobla hacia afuera.
                    val grados = abs(nuevo).coerceIn(1f, 359f)
                    etiqueta.text = textoEsquina(if (afuera.isChecked) -grados else grados)
                }
                // Y en el desarrollo, la curva ocupa su trozo: se abre, se ajusta o se cierra según
                // lo que haya quedado escrito en la esquina.
                marcoDePieza(index)?.let { marco ->
                    val arista = etiquetasEsquina(marco).indexOf(index)
                    if (arista >= 0) {
                        val arco = esquinaDesdeTexto(etiqueta.text).arco
                        ajustarBandaDeCurva(marco, arista, arco?.desarrollo ?: 0f)
                    }
                }
                marcoDePieza(index)?.let { sincronizarMarco(it, reinterpolarAltos = false) }
                registrarAccion()
                invalidate()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * El ángulo de una esquina a partir de dos marcas: [ladoCm] a cada lado desde el rincón y
     * [entreCm] de marca a marca.
     *
     * Es el triángulo isósceles que forman las dos marcas con la esquina, así que el ángulo sale
     * del seno de su mitad. Nulo si esa medida no puede ser: entre las marcas nunca hay más que la
     * suma de los dos lados.
     */
    private fun anguloPorMedidas(ladoCm: Float, entreCm: Float): Float? {
        if (ladoCm <= 0f || entreCm <= 0f) return null
        val seno = entreCm / (2f * ladoCm)
        if (seno > 1f) return null
        return (2.0 * kotlin.math.asin(seno.toDouble()) * 180.0 / Math.PI).toFloat()
    }

    /**
     * Añade o quita un tramo de la ventana de esquina. El tramo nuevo entra por la derecha con su
     * arista, su ángulo y su cota de alto; el vano crece con él, porque cada tramo es una pared.
     */
    private fun cambiarTramos(etiquetaIndex: Int, delta: Int) {
        val marcoIndex = marcoDePieza(etiquetaIndex) ?: return
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
        val quiebres = quiebresDelMarco(marcoIndex)
        val tramos = quiebres.size + 1
        // Sin tope por arriba: una ventana en serie lleva los lados que lleve la obra. Abajo, dos
        // en la de esquina —con una sola pared no hay esquina que doblar— y UNO en la curva: un
        // arco solo ya es una ventana, y partirlo es cosa de la obra, no una obligación.
        val curvo = esMarcoCurvo(marcoIndex)
        // Lo que mide la curva ENTERA y de qué círculo es, antes de tocar nada. Mientras todos los
        // trozos sean del mismo círculo, partir la ventana no la hace más grande: reparte ese
        // mismo arco entre los trozos que queden. Un punto de alto al medio de una curva de 180
        // deja dos trozos de 90, cada uno con la cuerda que le toca por el radio.
        val arcosAntes = curvasDeTramo(marcoIndex).values.mapNotNull { it.arco }
        val desarrolloAntes = arcosAntes.sumOf { it.desarrollo.toDouble() }.toFloat()
        // Si el vidriero ya midió los trozos por separado, los radios no coinciden: eso ya no es
        // un arco de círculo y no hay nada que repartir. Repartiéndolo se le borrarían las medidas.
        val mismoCirculo = arcosAntes.isNotEmpty() &&
            arcosAntes.all { abs(it.radio - arcosAntes[0].radio) <= arcosAntes[0].radio * 0.02f }
        val radioDeLaCurva = if (mismoCirculo) arcosAntes[0].radio else null
        val minimo = if (curvo) 1 else 2
        val destino = (tramos + delta).coerceAtLeast(minimo)
        if (destino == tramos) {
            Toast.makeText(
                context,
                if (minimo == 1) "Una ventana curva lleva al menos un tramo"
                else "Una esquina lleva al menos dos tramos",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val bordes = bordesDeTramos(marcoIndex)
        if (destino > tramos) {
            // El tramo nuevo mide como el último, y la arista queda donde estaba el canto derecho.
            val ultimo = bordes[bordes.size - 1] - bordes[bordes.size - 2]
            val abajo = marco.bottomRight.x
            val arriba = marco.topRight.x
            elementos.add(crearShape(Tool.LINE, PointF(arriba, marco.topRight.y), PointF(abajo, marco.bottomRight.y), ESQUINA_QUIEBRE))
            elementos.add(crearShape(Tool.LINE, PointF(abajo, marco.bottomRight.y), PointF(abajo, marco.topRight.y), VENTANA_ALTO_ESQUINA))
            // En una ventana curva no hay ángulo entre tramo y tramo: dos arcos se encuentran sin
            // doblar en punta. Lo que estrena el tramo nuevo es SU arco, que arranca siguiendo el
            // radio del último —la curva no se corta ahí— y se edita después con lo medido en la
            // pared. Así el trozo entre este punto de alto y el anterior tiene su propia curva, y
            // la ventana entera deja de ser el arco de un solo círculo.
            if (curvo) {
                val radio = curvasDeTramo(marcoIndex).values.lastOrNull()?.arco?.radio
                val desarrollo = pxToCm(ultimo)
                val arco = radio?.let { ArcoEsquina.deDesarrolloYRadio(desarrollo, it) }
                elementos.add(
                    Element.TextLabel(
                        text = textoCurva(desarrollo, arco?.cuerda ?: desarrollo),
                        x = abajo + ultimo / 2f,
                        y = marco.bottomRight.y,
                        textSize = 44f,
                        rol = ROL_CURVA
                    )
                )
            } else {
                // Una esquina nueva nace SIEMPRE en punta: hereda los grados de la última que doble
                // así, y si no hay ninguna empieza por la escuadra. Heredando la etiqueta tal cual,
                // al añadir un tramo detrás de una esquina curva el tramo nuevo salía redondeado
                // sin que nadie lo hubiera pedido; el vidriero lo curva después si quiere.
                val anguloPrevio = etiquetasEsquina(marcoIndex)
                    .map { (elementos[it] as Element.TextLabel).text }
                    .lastOrNull { !it.contains(MARCA_CURVA) }
                    ?: textoEsquina(90f)
                elementos.add(
                    Element.TextLabel(
                        text = anguloPrevio,
                        x = arriba,
                        y = marco.topRight.y,
                        textSize = 44f,
                        rol = ROL_ESQUINA
                    )
                )
            }
            elementos.add(
                Element.TextLabel(
                    text = textoAltos(0),
                    x = abajo + ultimo / 2f,
                    y = marco.bottomRight.y,
                    textSize = 44f,
                    rol = ROL_ALTOS
                )
            )
            // El tramo nuevo estrena su puente, a la altura del último que hubiera.
            val yPuente = puentesDelMarco(marcoIndex).lastOrNull()
                ?.let { (elementos[it] as Element.Shape).start.y }
                ?: (marco.bottomRight.y - (marco.bottomRight.y - marco.topRight.y) * 0.75f)
            elementos.add(
                crearShape(Tool.LINE, PointF(abajo, yPuente), PointF(abajo + ultimo, yPuente), PUERTA_PUENTE)
            )
            marco.topRight.x += ultimo
            marco.bottomRight.x += ultimo
        } else {
            val ultimoAncho = bordes[bordes.size - 1] - bordes[bordes.size - 2]
            // Se va el último tramo con todo lo suyo: su arista, su ángulo o su arco, su alto de
            // esquina, su contador y las cotas de alto que tuviera dentro. Sin llevarse el arco, el
            // rótulo se quedaba suelto y la ventana seguía contando una curva que ya no existe.
            val sobran = (
                listOfNotNull(
                    quiebres.lastOrNull(),
                    altosDelMarco(marcoIndex, VENTANA_ALTO_ESQUINA).lastOrNull(),
                    etiquetasEsquina(marcoIndex).lastOrNull(),
                    etiquetasCurva(marcoIndex).takeIf { curvo && it.size > 1 }?.lastOrNull(),
                    contadoresAltos(marcoIndex).lastOrNull(),
                    puentesDelMarco(marcoIndex).takeIf { it.size > 1 }?.lastOrNull()
                ) + altosLibresPorTramo(marcoIndex).lastOrNull().orEmpty()
                ).distinct().sortedDescending()
            sobran.forEach { if (it in elementos.indices) elementos.removeAt(it) }
            marco.topRight.x -= ultimoAncho
            marco.bottomRight.x -= ultimoAncho
        }
        actualizarBoundsRectangulo(marco)
        var ahora = elementos.indexOfFirst { it === marco }
        if (ahora < 0) return
        // Y si es una curva de un solo círculo, se reparte entre los trozos que hayan quedado: la
        // ventana sigue midiendo lo mismo, que su desarrollo es una medida de la pared y no cambia
        // porque se le ponga o se le quite un punto de alto.
        if (curvo && radioDeLaCurva != null) {
            repartirLaCurva(ahora, desarrolloAntes, radioDeLaCurva)
            ahora = elementos.indexOfFirst { it === marco }
            if (ahora < 0) return
        }
        etiquetaDelMarco(ahora, ROL_TRAMOS)?.let {
            (elementos[it] as Element.TextLabel).text = textoTramos(quiebresDelMarco(ahora).size + 1)
        }
        selectedIndices.clear()
        sincronizarMarco(ahora, reinterpolarAltos = false)
        registrarAccion()
        invalidate()
    }

    /**
     * Reparte una curva de [totalCm] de desarrollo entre los trozos que tenga, a partes iguales.
     *
     * Todos siguen el mismo [radio], que es el de la curva de antes: partir la ventana no la
     * cambia, solo dice por dónde se va a medir. Una de 180 con un punto al medio son dos trozos
     * de 90, y la cuerda de cada uno sale del círculo —no es la mitad de la cuerda entera—. De ahí
     * en adelante el vidriero corrige el trozo que no cuadre con la pared, y la ventana deja de
     * ser el arco de un círculo.
     */
    private fun repartirLaCurva(marcoIndex: Int, totalCm: Float, radio: Float) {
        val etiquetas = etiquetasCurva(marcoIndex)
        if (etiquetas.isEmpty() || totalCm <= 0.5f) return
        val cacho = totalCm / etiquetas.size
        if (cacho <= 0.5f) return
        val arco = ArcoEsquina.deDesarrolloYRadio(cacho, radio)
        etiquetas.forEach { i ->
            (elementos.getOrNull(i) as? Element.TextLabel)?.text =
                textoCurva(cacho, arco?.cuerda ?: cacho)
        }
        // Y el dibujo: cada trozo mide su desarrollo, que es lo que se ve en la alzada.
        etiquetas.indices.forEach { tramo -> aplicarAnchoTramoEnPlanta(marcoIndex, tramo, cacho) }
    }

    /**
     * Añade o quita una cota de alto interior. La automática es una por cada 120 cm de ancho, pero
     * el vano manda: donde haga falta otra medida se pone, y donde sobre se quita.
     */
    private fun cambiarAltos(etiquetaIndex: Int, delta: Int) {
        val marcoIndex = marcoDePieza(etiquetaIndex) ?: return
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
        val bordes = bordesDeTramos(marcoIndex)
        // Se toca el contador del tramo, así que la cota se añade o se quita EN ESE tramo.
        val tramo = contadoresAltos(marcoIndex).indexOf(etiquetaIndex).coerceAtLeast(0)
        if (tramo + 1 >= bordes.size) return
        val enElTramo = altosLibresPorTramo(marcoIndex).getOrNull(tramo).orEmpty()
        if ((delta > 0 && enElTramo.size >= 5) || (delta < 0 && enElTramo.isEmpty())) {
            Toast.makeText(
                context,
                if (delta > 0) "No caben más cotas de alto en este tramo"
                else "Este tramo no tiene cotas de alto",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (delta > 0) {
            agregarAltoEnTramo(bordes[tramo], bordes[tramo + 1], marco)
        } else {
            elementos.removeAt(enElTramo.last())
        }
        rehacerAltos(marco)
        val ahora = elementos.indexOfFirst { it === marco }
        if (ahora < 0) return
        selectedIndices.clear()
        sincronizarMarco(ahora, reinterpolarAltos = false)
        registrarAccion()
        invalidate()
    }

    /** Cuántas cotas de alto pide el dibujo: una por cada 120 cm, contadas TRAMO A TRAMO. */
    private fun altosAutomaticosDelMarco(marcoIndex: Int): Int {
        val bordes = bordesDeTramos(marcoIndex)
        if (bordes.size < 2) return 0
        return (0 until bordes.size - 1).sumOf { altosAutomaticos(pxToCm(bordes[it + 1] - bordes[it])) }
    }

    /**
     * Deja cada tramo con las cotas de alto que le tocan por SU ancho.
     *
     * El total no sirve para repartirlas: dos tramos de 80 no piden ninguna aunque sumen 160, y uno
     * de 250 pide dos aunque el de al lado sea corto. Cada tramo es una pared.
     */
    private fun ajustarAltosAutomaticos(marcoIndex: Int): Boolean {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return false
        var cambio = false
        var seguir = true
        while (seguir) {
            seguir = false
            val ahora = elementos.indexOfFirst { it === marco }
            if (ahora < 0) break
            val bordes = bordesDeTramos(ahora)
            val grupos = altosLibresPorTramo(ahora)
            for (tramo in 0 until bordes.size - 1) {
                val objetivo = altosAutomaticos(pxToCm(bordes[tramo + 1] - bordes[tramo]))
                val enElTramo = grupos.getOrNull(tramo).orEmpty()
                if (enElTramo.size < objetivo) {
                    agregarAltoEnTramo(bordes[tramo], bordes[tramo + 1], marco)
                    cambio = true
                    seguir = true
                    break
                }
                if (enElTramo.size > objetivo) {
                    elementos.removeAt(enElTramo.last())
                    cambio = true
                    seguir = true
                    break
                }
            }
        }
        if (cambio) rehacerAltos(marco)
        return cambio
    }

    private fun agregarAltoEnTramo(izq: Float, der: Float, marco: Element.Shape) {
        val x = (izq + der) / 2f
        elementos.add(
            crearShape(
                Tool.LINE,
                PointF(x, marco.rect.bottom),
                PointF(x, marco.rect.top),
                VENTANA_ALTO
            )
        )
    }

    private fun rehacerAltos(marco: Element.Shape) {
        val ahora = elementos.indexOfFirst { it === marco }
        if (ahora < 0) return
        repartirAltos(ahora, reinterpolar = false)
        colocarContadoresAltos(ahora)
        colocarBandaRotulos(ahora)
    }

    /**
     * Quita el puente y devuelve la puerta a su sitio: sin travesaño, la hoja es el marco entero y
     * las bisagras vuelven a repartirse por todo el alto.
     */
    private fun quitarPuente(puenteIndex: Int) {
        val marcoIndex = marcoDePieza(puenteIndex)
        elementos.removeAt(puenteIndex)
        selectedIndices.clear()
        val marcoTrasQuitar = marcoIndex?.let { if (it > puenteIndex) it - 1 else it }
        marcoTrasQuitar?.let { colocarPiezasDelMarco(it) }
    }

    /** Cambia la bisagra de lado: van las tres juntas, que es como se pone una puerta. */
    private fun alternarBisagras(index: Int) {
        val marcoIndex = marcoDePieza(index)
        // Con dos hojas cada una gira hacia su canto: no hay lado que cambiar.
        if (marcoIndex != null && hojasDePuerta(marcoIndex) == 2) {
            Toast.makeText(context, "Con dos hojas, cada una lleva su bisagra", Toast.LENGTH_SHORT).show()
            return
        }
        val marco = (elementos.getOrNull(marcoIndex ?: -1) as? Element.Shape)?.rect
            ?: boundsForDisenoBase()
            ?: return
        val bisagras = marcoIndex
            ?.let { piezasDelMarco(it).filter { i -> (elementos[i] as? Element.Symbol)?.drawableName == "bisagra" } }
            ?.takeIf { it.isNotEmpty() }
            ?: listOf(index)
        val hs = hingeSize()
        val sideX = if (bisagrasALaIzquierda(bisagras, marco)) marco.right else marco.left
        bisagras.forEach { i ->
            val r = (elementos.getOrNull(i) as? Element.Symbol)?.rect ?: return@forEach
            r.set(sideX - hs / 2f, r.top, sideX + hs / 2f, r.bottom)
        }
        // Cambiar de lado la bisagra cambia el lado hacia el que gira la puerta: la flecha de la
        // apertura se voltea con ella.
        marcoIndex?.let { colocarPiezasDelMarco(it) }
        registrarAccion()
        invalidate()
    }

    /** Vista interior ↔ exterior y apertura adentro ↔ afuera: el mismo sitio, el otro dibujo. */
    private fun cambiarSimbolo(index: Int, nuevo: String) {
        val actual = elementos.getOrNull(index) as? Element.Symbol ?: return
        if (drawableIdForName(nuevo) == 0) return
        elementos[index] = Element.Symbol(nuevo, RectF(actual.rect))
        registrarAccion()
        invalidate()
    }

    private fun etiquetaMichiDe(simboloIndex: Int): Int? {
        val simbolo = elementos.getOrNull(simboloIndex) as? Element.Symbol ?: return null
        return elementos.indices
            .filter { (elementos[it] as? Element.TextLabel)?.rol == ROL_MICHI }
            .minByOrNull {
                val t = elementos[it] as Element.TextLabel
                hypot((t.x - simbolo.rect.right).toDouble(), (t.y - simbolo.rect.centerY()).toDouble())
            }
    }

    /** La medida del michi se toca y se escribe, igual que una cota. */
    private fun editarMichi(index: Int) {
        val etiqueta = elementos.getOrNull(index) as? Element.TextLabel ?: return
        val input = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(etiqueta.text)
            setSelectAllOnFocus(true)
        }
        AlertDialog.Builder(context)
            .setTitle("Michi (cm)")
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                val nuevo = input.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                    ?: return@setPositiveButton
                etiqueta.text = formatCm(abs(nuevo))
                marcoDePieza(index)?.let { colocarPiezasDelMarco(it) }
                registrarAccion()
                invalidate()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    fun insertarResultadoCompas(restanteCm: Float, angulo: Float, complemento: Float, inglete: Float) {
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val boxW = 190f * resources.displayMetrics.density
        val boxH = 118f * resources.displayMetrics.density
        val rect = RectF(
            center.x - boxW / 2f,
            center.y - boxH / 2f,
            center.x + boxW / 2f,
            center.y + boxH / 2f
        )
        val texto = listOf(
            "COMPAS",
            "Restante: ${formatCm(restanteCm)}",
            "Angulo: ${formatAngle(angulo)} deg",
            "90 - angulo: ${formatAngle(complemento)} deg",
            "Inglete: ${formatAngle(inglete)} deg"
        ).joinToString("\n")
        elementos.add(Element.InfoBox(texto, rect, spToPx(12f)))
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
    }

    private fun agregarPlantilla(nuevos: List<Element>) {
        val startIndex = elementos.size
        elementos.addAll(nuevos)
        selectedIndices.clear()
        nuevos.indices.forEach { selectedIndices.add(startIndex + it) }
        registrarAccion()
        invalidate()
    }

    private fun crearShape(tool: Tool, start: PointF, end: PointF, cotaHint: String? = null): Element.Shape {
        val rect = rectFrom(start, end.x, end.y)
        val lineLengthPx = hypot((end.x - start.x).toDouble(), (end.y - start.y).toDouble()).toFloat()
        val widthCm = pxToCm(rect.width())
        val heightCm = pxToCm(rect.height())
        return Element.Shape(
            tool = tool,
            rect = rect,
            start = PointF(start.x, start.y),
            end = PointF(end.x, end.y),
            widthCm = widthCm,
            heightCm = heightCm,
            diameterCm = pxToCm(maxOf(rect.width(), rect.height())),
            lengthCm = pxToCm(lineLengthPx),
            topLeft = PointF(rect.left, rect.top),
            topRight = PointF(rect.right, rect.top),
            bottomRight = PointF(rect.right, rect.bottom),
            bottomLeft = PointF(rect.left, rect.bottom),
            topCm = widthCm,
            rightCm = heightCm,
            bottomCm = widthCm,
            leftCm = heightCm,
            cotaHint = cotaHint
        )
    }

    private fun symbolSize(): Float = 28f * resources.displayMetrics.density

    private fun hingeSize(): Float = 22f * resources.displayMetrics.density

    private fun boundsForDisenoBase(): RectF? {
        var union: RectF? = null
        for (element in elementos) {
            if (element is Element.Symbol) continue
            val bounds = boundsForElement(element)
            if (union == null) {
                union = RectF(bounds)
            } else {
                union.union(bounds)
            }
        }
        return union ?: boundsForContent()
    }

    private fun posicionLibreParaSimbolo(
        original: RectF,
        fixedX: Boolean = false,
        occupied: List<RectF> = emptyList()
    ): RectF {
        if (!chocaConCotaOSimbolo(original, occupied)) return original
        val step = original.height().coerceAtLeast(18f)
        val candidates = mutableListOf<RectF>()
        for (level in 1..6) {
            val distance = step * level
            candidates.add(RectF(original).apply { offset(0f, -distance) })
            candidates.add(RectF(original).apply { offset(0f, distance) })
            if (!fixedX) {
                candidates.add(RectF(original).apply { offset(-distance, 0f) })
                candidates.add(RectF(original).apply { offset(distance, 0f) })
                candidates.add(RectF(original).apply { offset(-distance, -distance) })
                candidates.add(RectF(original).apply { offset(distance, -distance) })
                candidates.add(RectF(original).apply { offset(-distance, distance) })
                candidates.add(RectF(original).apply { offset(distance, distance) })
            }
        }
        return candidates.firstOrNull { !chocaConCotaOSimbolo(it, occupied) }
            ?: candidates.minByOrNull { areaChoqueSimbolo(it, occupied) }
            ?: original
    }

    private fun chocaConCotaOSimbolo(rect: RectF, occupied: List<RectF>): Boolean {
        val padded = RectF(rect).apply { inset(-6f, -6f) }
        return cotaTextRects.any { RectF.intersects(padded, it) } ||
            occupied.any { RectF.intersects(padded, it) }
    }

    private fun areaChoqueSimbolo(rect: RectF, occupied: List<RectF>): Float {
        val padded = RectF(rect).apply { inset(-6f, -6f) }
        return (cotaTextRects + occupied).sumOf { existing ->
            val left = maxOf(padded.left, existing.left)
            val top = maxOf(padded.top, existing.top)
            val right = minOf(padded.right, existing.right)
            val bottom = minOf(padded.bottom, existing.bottom)
            if (right > left && bottom > top) ((right - left) * (bottom - top)).toDouble() else 0.0
        }.toFloat()
    }

    private fun boundsReferenciaBisagras(): RectF {
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val alto = 180f * resources.displayMetrics.density
        val ancho = 96f * resources.displayMetrics.density
        return RectF(
            center.x - ancho / 2f,
            center.y - alto / 2f,
            center.x + ancho / 2f,
            center.y + alto / 2f
        )
    }

    private fun pedirTexto(x: Float, y: Float) {
        val input = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            setSingleLine(true)
            hint = "Texto"
        }
        AlertDialog.Builder(context)
            .setTitle("Texto")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                val texto = input.text?.toString()?.trim().orEmpty()
                if (texto.isNotBlank()) {
                    elementos.add(Element.TextLabel(texto, x, y, spToPx(12f)))
                    registrarAccion()
                    invalidate()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // ==================== ENGANCHES PARA LAS PRUEBAS ====================
    // Cortar y unir se hacen con el dedo y sobre lo que esté seleccionado. Para poder probarlo sin
    // coordenadas de pantalla hace falta poner figuras y elegirlas a mano.

    /** Pone un rectángulo en esas coordenadas de píxel y devuelve su índice. */
    @androidx.annotation.VisibleForTesting
    fun agregarRectanguloParaPruebas(izq: Float, arriba: Float, der: Float, abajo: Float): Int {
        val rect = RectF(izq, arriba, der, abajo)
        val ancho = pxToCm(rect.width())
        val alto = pxToCm(rect.height())
        elementos.add(
            Element.Shape(
                tool = Tool.RECTANGLE,
                rect = rect,
                start = PointF(rect.left, rect.top),
                end = PointF(rect.right, rect.bottom),
                widthCm = ancho,
                heightCm = alto,
                diameterCm = pxToCm(maxOf(rect.width(), rect.height())),
                lengthCm = 0f,
                topLeft = PointF(rect.left, rect.top),
                topRight = PointF(rect.right, rect.top),
                bottomRight = PointF(rect.right, rect.bottom),
                bottomLeft = PointF(rect.left, rect.bottom),
                topCm = ancho,
                rightCm = alto,
                bottomCm = ancho,
                leftCm = alto
            )
        )
        return elementos.lastIndex
    }

    /**
     * Pone un triángulo con la base arriba y la punta abajo —el vano invertido— y devuelve su
     * índice. Es el que se dibuja con la herramienta de triángulo y luego se voltea con los nodos.
     */
    @androidx.annotation.VisibleForTesting
    fun agregarTrianguloInvertidoParaPruebas(izq: Float, arriba: Float, der: Float, abajo: Float): Int {
        val indice = agregarRectanguloParaPruebas(izq, arriba, der, abajo)
        val shape = elementos[indice] as Element.Shape
        val volteado = Element.Shape(
            tool = Tool.TRIANGLE,
            rect = RectF(shape.rect),
            start = PointF(shape.start), end = PointF(shape.end),
            widthCm = shape.widthCm, heightCm = shape.heightCm,
            diameterCm = shape.diameterCm, lengthCm = shape.lengthCm,
            topLeft = PointF(shape.topLeft), topRight = PointF(shape.topRight),
            bottomRight = PointF(shape.bottomRight), bottomLeft = PointF(shape.bottomLeft),
            topCm = shape.topCm, rightCm = shape.rightCm,
            bottomCm = shape.bottomCm, leftCm = shape.leftCm
        )
        elementos[indice] = volteado
        fijarVerticesTriangulo(
            volteado,
            punta = PointF((izq + der) / 2f, abajo),
            der = PointF(der, arriba),
            izq = PointF(izq, arriba)
        )
        return indice
    }

    /** Elige los elementos sobre los que trabajan cortar y unir. */
    @androidx.annotation.VisibleForTesting
    fun seleccionarParaPruebas(vararg indices: Int) {
        selectedIndices.clear()
        indices.forEach { selectedIndices.add(it) }
    }

    /** Escribe el ancho de un tramo como si se tocara la cota de la PLANTA. */
    @androidx.annotation.VisibleForTesting
    fun anchoDeTramoEnPlantaParaPruebas(tramo: Int, valueCm: Float) {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return
        aplicarAnchoTramoEnPlanta(marco, tramo, valueCm)
        invalidate()
    }

    /** Escribe el ancho de un tramo como si se tocara la cota de ABAJO de la alzada. */
    @androidx.annotation.VisibleForTesting
    fun anchoDeAbajoParaPruebas(tramo: Int, valueCm: Float) {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return
        aplicarAnchoTramo(marco, tramo, valueCm)
        invalidate()
    }

    /** Escribe el lado de ARRIBA de un tramo, como la cota de la alzada: así se apunta el descuadre. */
    @androidx.annotation.VisibleForTesting
    fun anchoDeArribaParaPruebas(tramo: Int, valueCm: Float) {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return
        aplicarArribaTramo(marco, tramo, valueCm)
        invalidate()
    }

    /** Lo que mide un tramo por abajo y por arriba, que con descuadre no es lo mismo. */
    @androidx.annotation.VisibleForTesting
    fun ladosDeTramoParaPruebas(tramo: Int): Pair<Float, Float>? {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return null
        val bordes = bordesDeTramos(marco)
        val arriba = puntosArriba(marco)
        if (tramo + 1 >= bordes.size || tramo + 1 >= arriba.size) return null
        return pxToCm(bordes[tramo + 1] - bordes[tramo]) to pxToCm(arriba[tramo + 1].x - arriba[tramo].x)
    }

    /** Añade o quita un tramo, como el contador "N° de tramos" de la pantalla. */
    @androidx.annotation.VisibleForTesting
    fun cambiarTramosParaPruebas(delta: Int) {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return
        val etiqueta = etiquetaDelMarco(marco, ROL_TRAMOS) ?: return
        cambiarTramos(etiqueta, delta)
    }

    /**
     * Escribe la curva de un tramo de una ventana curva, como al tocar su rótulo.
     *
     * Es el arco de una PARED, no el de una arista entre dos: en la ventana curva lo que se curva
     * son los tramos.
     */
    @androidx.annotation.VisibleForTesting
    fun curvaDeTramoParaPruebas(tramo: Int, desarrolloCm: Float, cuerdaCm: Float) {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return
        val etiqueta = etiquetasCurva(marco).getOrNull(tramo) ?: return
        (elementos[etiqueta] as Element.TextLabel).text = textoCurva(desarrolloCm, cuerdaCm)
        aplicarAnchoTramoEnPlanta(marco, tramo, desarrolloCm)
        elementos.indices.firstOrNull { esMarcoEsquina(it) }?.let {
            sincronizarMarco(it, reinterpolarAltos = false)
        }
        invalidate()
    }

    /** Pone una esquina curva en esa arista, con su desarrollo y su cuerda. */
    @androidx.annotation.VisibleForTesting
    fun curvarEsquinaParaPruebas(arista: Int, desarrolloCm: Float, cuerdaCm: Float) {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return
        val etiqueta = etiquetasEsquina(marco).getOrNull(arista) ?: return
        (elementos[etiqueta] as Element.TextLabel).text =
            if (desarrolloCm > 0.5f) "$MARCA_CURVA ${formatCm(desarrolloCm)}|${formatCm(cuerdaCm)}"
            else textoEsquina(90f)
        ajustarBandaDeCurva(marco, arista, if (desarrolloCm > 0.5f) desarrolloCm else 0f)
        elementos.indices.firstOrNull { esMarcoEsquina(it) }?.let {
            sincronizarMarco(it, reinterpolarAltos = false)
        }
        invalidate()
    }

    /** Pone ese zoom y redibuja, como el pellizco de dos dedos. */
    @androidx.annotation.VisibleForTesting
    fun zoomParaPruebas(escala: Float) {
        viewScale = escala
        dibujarEnBitmapParaPruebas()
    }

    /** Cuántos rótulos de la banda se pisan entre ellos ahora mismo. */
    @androidx.annotation.VisibleForTesting
    fun rotulosMontadosParaPruebas(): Int {
        dibujarEnBitmapParaPruebas()
        val cajas = elementos.filterIsInstance<Element.TextLabel>()
            .filter { it.rol in setOf(ROL_ESQUINA, ROL_ALTOS, ROL_ALFEIZAR, ROL_CURVA) && !it.titulo }
            .map { boundsForText(it) }
        // Solo lo que se lee mal: dos rótulos que se tocan por un pelo no molestan a nadie.
        val roce = ce(6f)
        var montados = 0
        for (i in cajas.indices) for (j in i + 1 until cajas.size) {
            val a = RectF(cajas[i]).apply { inset(roce, roce) }
            if (RectF(a).intersect(cajas[j])) montados++
        }
        return montados
    }

    /** Qué rótulos de la banda hay y dónde, para diagnosticar los solapes. */
    @androidx.annotation.VisibleForTesting
    fun diagRotulosParaPruebas(): String {
        dibujarEnBitmapParaPruebas()
        return elementos.filterIsInstance<Element.TextLabel>()
            .filter { it.rol != null }
            .joinToString("\n") { et ->
                val c = boundsForText(et)
                "'${et.text}' rol=${et.rol} titulo=${et.titulo} " +
                    "x=${c.left.toInt()}..${c.right.toInt()} y=${c.top.toInt()}..${c.bottom.toInt()}"
            }
    }

    /** ¿El identificador cabe en la pantalla, desde donde empieza hasta el borde derecho? */
    @androidx.annotation.VisibleForTesting
    fun identificadorCabeParaPruebas(): Boolean {
        dibujarEnBitmapParaPruebas()
        val titulo = elementos.filterIsInstance<Element.TextLabel>().firstOrNull { it.titulo }
            ?: return true
        sketchTextPaint.textSize = tamanoTexto(titulo)
        val derechaEnPantalla = mundoAPantallaX(titulo.x + sketchTextPaint.measureText(titulo.text))
        return derechaEnPantalla <= width + 1f
    }

    /** Corre el dibujo por la pantalla, como el arrastre con el dedo. */
    @androidx.annotation.VisibleForTesting
    fun desplazarParaPruebas(dx: Float, dy: Float) {
        viewOffsetX += dx
        viewOffsetY += dy
        dibujarEnBitmapParaPruebas()
    }

    /** La vista tal como se ve en la pantalla, para poder mirarla desde fuera. */
    @androidx.annotation.VisibleForTesting
    fun pantallaParaPruebas(): Bitmap {
        val bmp = Bitmap.createBitmap(
            width.coerceAtLeast(1), height.coerceAtLeast(1), Bitmap.Config.ARGB_8888
        )
        draw(Canvas(bmp))
        return bmp
    }

    /** Dibuja una vez en un bitmap aparte, para que se recoloquen las cotas y los rótulos. */
    private fun dibujarEnBitmapParaPruebas() {
        if (width <= 0 || height <= 0) return
        draw(Canvas(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)))
    }

    /** El ancho de la ventana en el desarrollo: la suma de sus paredes, curvas incluidas. */
    @androidx.annotation.VisibleForTesting
    fun anchoDeLaVentanaParaPruebas(): Float {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return 0f
        return pxToCm((elementos[marco] as Element.Shape).rect.width())
    }

    /** Los anchos de cada pared en el desarrollo, de izquierda a derecha. */
    @androidx.annotation.VisibleForTesting
    fun anchosDeParedParaPruebas(): List<Float> {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return emptyList()
        val bordes = bordesDeTramos(marco)
        return (0 until bordes.size - 1).map { pxToCm(bordes[it + 1] - bordes[it]) }
    }

    /** El arco de una arista curva: desarrollo, cuerda, flecha y cuánto dobla. Null si va en punta. */
    @androidx.annotation.VisibleForTesting
    fun esquinaCurvaParaPruebas(arista: Int): List<Float>? {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return null
        val a = esquinasDeVentana(marco).getOrNull(arista)?.arco ?: return null
        return listOf(a.desarrollo, a.cuerda, a.flecha, a.anguloGrados)
    }

    /** Escribe el ángulo de una arista, con su signo: en menos, la pared dobla hacia afuera. */
    @androidx.annotation.VisibleForTesting
    fun anguloDeEsquinaParaPruebas(arista: Int, grados: Float) {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return
        val etiqueta = etiquetasEsquina(marco).getOrNull(arista) ?: return
        (elementos[etiqueta] as Element.TextLabel).text = textoEsquina(grados)
        colocarPiezasDelMarco(marco)
        invalidate()
    }

    /** El recorrido de la planta en centímetros, desde su arranque. */
    @androidx.annotation.VisibleForTesting
    fun recorridoPlantaParaPruebas(): List<Pair<Float, Float>> {
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return emptyList()
        val puntos = recorridoPlanta(marco)
        val origen = puntos.firstOrNull() ?: return emptyList()
        return puntos.map { pxToCm(it.x - origen.x) to pxToCm(it.y - origen.y) }
    }

    /** El ángulo que sale de medir a los dos lados de la esquina. Nulo si esa medida no puede ser. */
    @androidx.annotation.VisibleForTesting
    fun anguloPorMedidasParaPruebas(ladoCm: Float, entreCm: Float): Float? =
        anguloPorMedidas(ladoCm, entreCm)

    /**
     * Cuántas cotas de ancho de tramo quedan tocables tras dibujar, y cuántas de ellas están en la
     * planta. La planta pone las suyas además de las del pie de la alzada.
     */
    @androidx.annotation.VisibleForTesting
    fun cotasDeTramoParaPruebas(): Pair<Int, Int> {
        val bmp = Bitmap.createBitmap(
            width.coerceAtLeast(1), height.coerceAtLeast(1), Bitmap.Config.ARGB_8888
        )
        draw(Canvas(bmp))
        val marco = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return 0 to 0
        val pie = (elementos[marco] as Element.Shape).rect.bottom
        val deTramo = cotaHits.filter {
            it.type == CotaType.ESQUINA_TRAMO || it.type == CotaType.ESQUINA_TRAMO_PLANTA
        }
        return deTramo.size to deTramo.count { pantallaAMundoY(it.rect.centerY()) > pie + huecoPlantaPx / 2f }
    }

    /** Las cotas de ancho que salen en la alzada: al pie y sobre la línea de arriba. */
    @androidx.annotation.VisibleForTesting
    fun cotasDeAnchoEnAlzadoParaPruebas(): Pair<Int, Int> {
        val bmp = Bitmap.createBitmap(
            width.coerceAtLeast(1), height.coerceAtLeast(1), Bitmap.Config.ARGB_8888
        )
        draw(Canvas(bmp))
        return cotaHits.count { it.type == CotaType.ESQUINA_TRAMO } to
            cotaHits.count { it.type == CotaType.ESQUINA_TRAMO_ARRIBA }
    }

    /** El contorno de la figura más grande, en píxeles, tal como se guarda. */
    @androidx.annotation.VisibleForTesting
    fun contornoGuardadoParaPruebas(): List<Pair<Float, Float>> {
        val composite = elementos.filterIsInstance<Element.Composite>().maxByOrNull {
            val b = boundsForElement(it)
            b.width() * b.height()
        } ?: return emptyList()
        return composite.contours.firstOrNull()?.map { it.x to it.y }.orEmpty()
    }

    fun weldSelected(): Boolean {
        if (selectedIndices.size != 2) return false
        val aIndex = selectedIndices[0]
        val bIndex = selectedIndices[1]
        val pathA = pathForElement(elementos.getOrNull(aIndex) ?: return false)
        val pathB = pathForElement(elementos.getOrNull(bIndex) ?: return false)
        val result = Path(pathA)
        result.op(pathB, Path.Op.UNION)
        reemplazarSeleccionCon(compositeFromPath(result))
        registrarAccion()
        return true
    }

    fun subtractSelected(): Boolean {
        if (selectedIndices.size != 2) return false
        val cutterIndex = selectedIndices[0]
        val baseIndex = selectedIndices[1]
        val cutterElement = elementos.getOrNull(cutterIndex) ?: return false
        val baseElement = elementos.getOrNull(baseIndex) ?: return false
        val cutter = pathForElement(cutterElement)
        val base = pathForElement(baseElement)
        val result = Path(base)
        result.op(cutter, Path.Op.DIFFERENCE)
        reemplazarSeleccionCon(compositeFromDifference(result, baseElement, cutterElement))
        registrarAccion()
        return true
    }

    fun duplicateSelected(): Boolean {
        if (selectedIndices.size != 1) return false
        val original = elementos.getOrNull(selectedIndices.first()) ?: return false
        val copy = cloneElement(original)
        val bounds = boundsForElement(original)
        translateElement(copy, bounds.width() + snapThresholdPx(), 0f)
        elementos.add(copy)
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
        return true
    }

    fun mirrorCopySelected(): Boolean {
        if (selectedIndices.size != 1) return false
        val original = elementos.getOrNull(selectedIndices.first()) ?: return false
        val bounds = boundsForElement(original)
        val copy = cloneElement(original)
        mirrorElementHorizontally(copy, bounds.centerX())
        val mirroredBounds = boundsForElement(copy)
        translateElement(copy, bounds.right + snapThresholdPx() - mirroredBounds.left, 0f)
        elementos.add(copy)
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
        return true
    }

    // Refleja la figura seleccionada EN SU SITIO (sin crear copia).
    fun mirrorSelectedInPlace(): Boolean {
        if (selectedIndices.size != 1) return false
        val element = elementos.getOrNull(selectedIndices.first()) ?: return false
        mirrorElementHorizontally(element, boundsForElement(element).centerX())
        registrarAccion()
        invalidate()
        return true
    }

    // Crea varias copias reflejadas en fila, contiguas hacia la derecha.
    fun mirrorCopiesSelected(count: Int): Boolean {
        if (selectedIndices.size != 1) return false
        val original = elementos.getOrNull(selectedIndices.first()) ?: return false
        val total = count.coerceIn(1, 20)
        var refBounds = boundsForElement(original)
        var lastIndex = selectedIndices.first()
        for (k in 0 until total) {
            val copy = cloneElement(original)
            mirrorElementHorizontally(copy, refBounds.centerX())
            val mb = boundsForElement(copy)
            translateElement(copy, refBounds.right + snapThresholdPx() - mb.left, 0f)
            elementos.add(copy)
            lastIndex = elementos.lastIndex
            refBounds = boundsForElement(copy)
        }
        selectedIndices.clear()
        selectedIndices.add(lastIndex)
        registrarAccion()
        invalidate()
        return true
    }

    fun groupSelected(): Boolean {
        if (selectedIndices.size < 2) return false
        val indices = selectedIndices.sorted()
        val children = indices.mapNotNull { index -> elementos.getOrNull(index)?.let { cloneElement(it) } }.toMutableList()
        if (children.size < 2) return false
        indices.sortedDescending().forEach { index ->
            if (index in elementos.indices) elementos.removeAt(index)
        }
        elementos.add(Element.Group(children))
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        registrarAccion()
        invalidate()
        return true
    }

    fun ungroupSelected(): Boolean {
        if (selectedIndices.size != 1) return false
        val index = selectedIndices.first()
        val group = elementos.getOrNull(index) as? Element.Group ?: return false
        elementos.removeAt(index)
        val insertAt = index.coerceIn(0, elementos.size)
        val children = group.children.map { cloneElement(it) }
        elementos.addAll(insertAt, children)
        selectedIndices.clear()
        children.indices.forEach { selectedIndices.add(insertAt + it) }
        registrarAccion()
        invalidate()
        return true
    }

    fun deleteSelected(): Boolean {
        if (selectedIndices.isEmpty()) return false
        selectedIndices.sortedDescending().forEach { index ->
            if (index in elementos.indices) elementos.removeAt(index)
        }
        selectedIndices.clear()
        registrarAccion()
        invalidate()
        return true
    }

    fun scaleSelected(factor: Float): Boolean {
        if (selectedIndices.isEmpty() || factor <= 0f) return false
        val bounds = boundsForIndices(selectedIndices) ?: return false
        val pivot = PointF(bounds.centerX(), bounds.centerY())
        selectedIndices.forEach { index ->
            elementos.getOrNull(index)?.let { scaleElement(it, factor, pivot) }
        }
        registrarAccion()
        invalidate()
        return true
    }

    fun rotateSelected(degrees: Float = 90f): Boolean {
        if (selectedIndices.isEmpty()) return false
        val bounds = boundsForIndices(selectedIndices) ?: return false
        val pivot = PointF(bounds.centerX(), bounds.centerY())
        selectedIndices.forEach { index ->
            elementos.getOrNull(index)?.let { rotateElement(it, degrees, pivot) }
        }
        registrarAccion()
        invalidate()
        return true
    }

    fun redondearEsquinaSeleccionada(esquina: Int, radioCm: Float): Boolean {
        if (selectedIndices.size != 1 || esquina !in 0..3) return false
        val index = selectedIndices.first()
        val element = elementos.getOrNull(index) ?: return false
        val dims = roundedDimsFromElement(element) ?: return false
        val radius = cmToPx(radioCm).coerceIn(cmToPx(1f), minOf(dims.rectW, dims.rectH) / 2f)
        dims.radii[esquina] = radius
        val contour = rectContour(RectF(dims.left, dims.top, dims.left + dims.rectW, dims.top + dims.rectH))
        val contours = mutableListOf(contour)
        elementos[index] = Element.Composite(
            path = pathRoundedCorners(dims),
            widthCm = pxToCm(dims.rectW),
            heightCm = pxToCm(dims.rectH),
            contours = contours,
            sideCms = sideCmsRoundedCorners(dims),
            template = TEMPLATE_ROUNDED
        )
        registrarAccion()
        invalidate()
        return true
    }

    // Copia PROFUNDA de un elemento (Path, puntos, contornos…) para que el snapshot sea independiente.
    private fun copyElement(e: Element): Element = when (e) {
        is Element.Freehand -> Element.Freehand(Path(e.path))
        is Element.Composite -> Element.Composite(
            Path(e.path), e.widthCm, e.heightCm,
            e.contours.map { c -> c.map { PointF(it.x, it.y) }.toMutableList() }.toMutableList(),
            e.sideCms.map { it.toMutableList() }.toMutableList(),
            e.template, e.rotationDeg, e.bloqueados.toMutableSet(), e.reflejado,
            LinkedHashMap(e.declarados)
        )
        is Element.Shape -> Element.Shape(
            e.tool, RectF(e.rect), PointF(e.start.x, e.start.y), PointF(e.end.x, e.end.y),
            e.widthCm, e.heightCm, e.diameterCm, e.lengthCm,
            PointF(e.topLeft.x, e.topLeft.y), PointF(e.topRight.x, e.topRight.y),
            PointF(e.bottomRight.x, e.bottomRight.y), PointF(e.bottomLeft.x, e.bottomLeft.y),
            e.topCm, e.rightCm, e.bottomCm, e.leftCm, e.cotaHint, e.largoFijado
        )
        is Element.Group -> Element.Group(e.children.map { copyElement(it) }.toMutableList())
        is Element.TextLabel -> Element.TextLabel(e.text, e.x, e.y, e.textSize, e.titulo, e.movida, e.rol)
        is Element.InfoBox -> Element.InfoBox(e.text, RectF(e.rect), e.textSize)
        is Element.Symbol -> Element.Symbol(e.drawableName, RectF(e.rect), e.reflejado)
    }

    private fun snapshot(): List<Element> = elementos.map { copyElement(it) }

    // Marca una acción YA realizada: guarda en el historial el estado que había ANTES, y toma como
    // nuevo "previo" el estado actual (post-mutación). Debe llamarse DESPUÉS de mutar los elementos.
    private fun registrarAccion() {
        historia.addLast(estadoPrevio)
        while (historia.size > maxHistorial) historia.removeFirst()
        estadoPrevio = snapshot()
        futuro.clear()
    }

    private fun restaurar(snap: List<Element>) {
        elementos.clear()
        elementos.addAll(snap.map { copyElement(it) })
        selectedIndices.clear()
        cotaHits.clear()
    }

    // Fija el estado actual como base del historial (p. ej. al cargar un sketch): undo no va antes de aquí.
    private fun establecerBaseline() {
        estadoPrevio = snapshot()
        historia.clear()
        futuro.clear()
    }

    fun undo() {
        if (historia.isEmpty()) return
        futuro.addLast(estadoPrevio)
        val prev = historia.removeLast()
        estadoPrevio = prev
        restaurar(prev)
        invalidate()
    }

    fun redo() {
        if (futuro.isEmpty()) return
        historia.addLast(estadoPrevio)
        val next = futuro.removeLast()
        estadoPrevio = next
        restaurar(next)
        invalidate()
    }

    fun clear() {
        elementos.clear()
        registrarAccion()
        cotaHits.clear()
        trazoActual.reset()
        fondo = null
        dibujando = false
        resetViewport()
        invalidate()
    }

    fun hasContent(): Boolean = elementos.isNotEmpty() || fondo != null

    fun hasDrawing(): Boolean = elementos.isNotEmpty()



    /** La esquina de arriba a la izquierda de la figura recortada, en píxeles del apunte. */
    @androidx.annotation.VisibleForTesting
    fun cajaDelCompositeParaPruebas(): Pair<Float, Float> {
        val c = compositePrincipal() ?: return 0f to 0f
        val caja = boundsForElement(c)
        return caja.left to caja.top
    }

    /** Centímetros a píxeles del apunte, para poder tocar donde toca. */
    @androidx.annotation.VisibleForTesting
    fun cmAPixelesParaPruebas(cm: Float): Float = cmToPx(cm)

    /** Manda un toque al lienzo, como el dedo. Devuelve true si la vista se lo quedó. */
    @androidx.annotation.VisibleForTesting
    fun toqueParaPruebas(accion: Int, x: Float, y: Float): Boolean {
        val ahora = android.os.SystemClock.uptimeMillis()
        val e = MotionEvent.obtain(ahora, ahora, accion, x, y, 0)
        val comido = onTouchEvent(e)
        e.recycle()
        return comido
    }
    /**
     * Pone una cota a escuadra tocando cerca de esa esquina y arrastrando hasta (hx,hy).
     *
     * Sin arrastre —que es lo que pasa si no se le dan hx,hy— se queda con la cota más corta.
     */
    @androidx.annotation.VisibleForTesting
    fun cotaAEscuadraParaPruebas(x: Float, y: Float, hx: Float = x, hy: Float = y): Boolean {
        if (!eligiendoEscuadra) activarCotaAEscuadra()
        return ponerCotaAEscuadra(PointF(x, y), PointF(hx, hy))
    }

    /** Cuántas cotas a escuadra hay puestas en el apunte. */
    @androidx.annotation.VisibleForTesting
    fun cuantasCotasAEscuadraParaPruebas(): Int = cotasAEscuadra().size

    /** Lo que mide la primera cota a escuadra del apunte, en cm; null si no hay ninguna. */
    @androidx.annotation.VisibleForTesting
    fun medidaAEscuadraParaPruebas(): Float? {
        val i = cotasAEscuadra().firstOrNull() ?: return null
        val (_, medida) = medidaDeLaCotaAEscuadra(i) ?: return null
        return pxToCm(medida.distanciaCm)
    }

    /** Escribe otra medida en la primera cota a escuadra, como al editarla en la pantalla. */
    @androidx.annotation.VisibleForTesting
    fun escribirEscuadraParaPruebas(valueCm: Float) {
        val i = cotasAEscuadra().firstOrNull() ?: return
        aplicarCotaAEscuadra(i, valueCm)
    }

    /** El contorno de la figura recortada, en cm, con el origen en su esquina de arriba. */
    @androidx.annotation.VisibleForTesting
    fun contornoDelCompositeParaPruebas(): List<Pair<Float, Float>> {
        val c = compositePrincipal() ?: return emptyList()
        val caja = boundsForElement(c)
        return contornoDelComposite(c)?.map {
            pxToCm(it.first - caja.left) to pxToCm(it.second - caja.top)
        } ?: emptyList()
    }
    fun insertarRecurrenteF1() {
        val ancho = 160f
        val alto = 110f
        val corteAncho = 48f
        val corteAlto = 36f
        val left = maxOf(24f, screenToWorld(width * 0.5f, height * 0.5f).x - cmToPx(ancho) / 2f)
        val top = maxOf(24f, screenToWorld(width * 0.5f, height * 0.5f).y - cmToPx(alto) / 2f)
        elementos.add(crearRecurrenteF1(left, top, ancho, alto, corteAncho, corteAlto))
        registrarAccion()
        invalidate()
    }

    fun insertarRecurrenteF2() {
        val ancho = 160f
        val alto = 110f
        val corteAncho = 58f
        val corteAlto = 40f
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = maxOf(24f, center.x - cmToPx(ancho) / 2f)
        val top = maxOf(24f, center.y - cmToPx(alto) / 2f)
        elementos.add(crearRecurrenteF2(left, top, ancho, alto, corteAncho, corteAlto))
        registrarAccion()
        invalidate()
    }

    fun insertarRecurrenteF3() {
        val anchoSuperior = 170f
        val anchoInferior = 105f
        val alto = 105f
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = maxOf(24f, center.x - cmToPx(anchoSuperior) / 2f)
        val top = maxOf(24f, center.y - cmToPx(alto) / 2f)
        elementos.add(crearRecurrenteF3(left, top, anchoSuperior, anchoInferior, alto))
        registrarAccion()
        invalidate()
    }

    fun insertarRecurrenteF4(conIzquierdo: Boolean, conDerecho: Boolean) {
        val centralAncho = 75f
        val centralAlto = 150f
        val agregadoAncho = 34f
        val agregadoAlto = 34f
        val anchoTotal = centralAncho +
            (if (conIzquierdo) agregadoAncho else 0f) +
            (if (conDerecho) agregadoAncho else 0f)
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val centralLeft = maxOf(
            24f,
            center.x - cmToPx(anchoTotal) / 2f + if (conIzquierdo) cmToPx(agregadoAncho) else 0f
        )
        val top = maxOf(24f, center.y - cmToPx(centralAlto) / 2f)
        elementos.add(
            crearRecurrenteF4(
                centralLeft = centralLeft,
                top = top,
                centralAnchoCm = centralAncho,
                centralAltoCm = centralAlto,
                agregadoAnchoCm = agregadoAncho,
                agregadoAltoCm = agregadoAlto,
                conIzquierdo = conIzquierdo,
                conDerecho = conDerecho
            )
        )
        registrarAccion()
        invalidate()
    }

    fun insertarRecurrenteF5() {
        val rectAncho = 120f
        val rectAlto = 120f
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = maxOf(24f, center.x - cmToPx(rectAncho) / 2f)
        val top = maxOf(24f, center.y - cmToPx(rectAlto) / 2f)
        elementos.add(crearRecurrenteF5(left, top, rectAncho, rectAlto))
        registrarAccion()
        invalidate()
    }

    fun insertarRecurrenteF6(esquinas: Set<Int> = setOf(0, 1, 2, 3), radioCm: Float = 18f) {
        val rectAncho = 120f
        val rectAlto = 90f
        val center = screenToWorld(width * 0.5f, height * 0.5f)
        val left = maxOf(24f, center.x - cmToPx(rectAncho) / 2f)
        val top = maxOf(24f, center.y - cmToPx(rectAlto) / 2f)
        val rect = RectF(left, top, left + cmToPx(rectAncho), top + cmToPx(rectAlto))
        val element = if (esquinas.size >= 4) {
            crearRecurrenteF6(left, top, rectAncho, rectAlto, radioCm)
        } else {
            crearRectanguloConEsquinasRedondeadas(rect, esquinas, radioCm)
        }
        elementos.add(element)
        registrarAccion()
        invalidate()
    }

    /**
     * El contorno del vano, en centímetros y con la Y hacia abajo: el dintel es la Y más pequeña.
     *
     * Se devuelve el contorno de la figura MÁS GRANDE del apunte, que es el hueco de la ventana;
     * los puentes, símbolos y textos que haya alrededor no son vanos. Las coordenadas se dan
     * relativas a su propia esquina superior izquierda, para que no arrastren dónde estaba
     * dibujada.
     *
     * De aquí salen los tramos de la ventana: un vano con el alféizar subido en un trozo es, en el
     * diseño, dos tramos de distinto alto colgando del mismo dintel.
     */
    /**
     * La ventana de esquina del apunte, lado por lado, para que la calculadora la arme en L, en C
     * o en serie. Null si el apunte no tiene ninguna.
     *
     * Una pared curva NO es un lado: en la alzada ocupa su banda como un tramo más, pero no es una
     * pared que se mida y se corte como las otras. Se salta, y su arista queda marcada como curva
     * para que la calculadora avise de que ese trozo no entra en el cálculo.
     */
    fun esquinaPrincipalEnCm(): EsquinaMedida? {
        val marcoIndex = elementos.indices.firstOrNull { esMarcoEsquina(it) } ?: return null
        val marco = elementos[marcoIndex] as Element.Shape
        val bordes = bordesDeTramos(marcoIndex)
        val arriba = puntosArriba(marcoIndex)
        // Dos bordes ya son un tramo: una ventana curva de un solo arco.
        if (bordes.size < 2 || arriba.size != bordes.size) return null

        val bandas = bandasDeCurva(marcoIndex).map { (elementos[it] as Element.Shape).end.x }
        val curvasPropias = curvasDeTramo(marcoIndex)
        val puentes = puentesDelMarco(marcoIndex).map { elementos[it] as Element.Shape }
        val esquinas = esquinasDeVentana(marcoIndex)
        val pie = marco.rect.bottom

        val lados = mutableListOf<LadoEsquina>()
        val angulos = mutableListOf<String>()
        var arista = 0
        // Una pared de cero no es un lado, pero deja su sitio libre: la curva que viene detrás lo
        // ocupa y pasa a ser una pared más. Es la ventana que empieza en la curva.
        var sitioLibre = false
        for (tramo in 0 until bordes.size - 1) {
            val izq = bordes[tramo]
            val der = bordes[tramo + 1]
            val esBanda = bandas.any { abs(it - der) < 0.5f }
            val mide = der - izq >= cmToPx(0.5f)
            if (!esBanda && !mide) sitioLibre = true
            if (esBanda && sitioLibre) {
                // La curva ocupa el sitio de la pared que no existe: su ancho es el desarrollo y
                // se lleva su panza. Y la arista que la traía deja de ser una curva y pasa a ser
                // el giro que hace, porque ya no es la esquina entre dos paredes: es una pared.
                val arco = esquinas.getOrNull(arista - 1)?.arco
                val puenteCurva = puentes.firstOrNull {
                    it.rect.centerX() > izq - 0.5f && it.rect.centerX() < der + 0.5f
                }
                lados.add(
                    LadoEsquina(
                        anchoAbajo = pxToCm(der - izq),
                        anchoArriba = pxToCm(arriba[tramo + 1].x - arriba[tramo].x),
                        altoIzq = pxToCm(pie - arriba[tramo].y),
                        altoDer = pxToCm(pie - arriba[tramo + 1].y),
                        puente = puenteCurva?.let { pxToCm(pie - it.start.y) } ?: 0f,
                        flecha = arco?.flecha ?: 0f
                    )
                )
                if (arco != null && angulos.isNotEmpty()) {
                    angulos[angulos.lastIndex] = formatCm(arco.anguloGrados)
                }
                sitioLibre = false
            }
            if (!esBanda && mide) {
                sitioLibre = false
                val propia = curvasPropias[tramo]?.arco
                // El puente de ESTE tramo: cada pared lleva el suyo y puede estar a otra altura.
                val puente = puentes.firstOrNull {
                    it.rect.centerX() > izq - 0.5f && it.rect.centerX() < der + 0.5f
                }
                lados.add(
                    LadoEsquina(
                        anchoAbajo = pxToCm(der - izq),
                        anchoArriba = pxToCm(arriba[tramo + 1].x - arriba[tramo].x),
                        altoIzq = pxToCm(pie - arriba[tramo].y),
                        altoDer = pxToCm(pie - arriba[tramo + 1].y),
                        puente = puente?.let { pxToCm(pie - it.start.y) } ?: 0f,
                        // En una ventana curva la pared entera va curvada, y su ancho ES su
                        // desarrollo: lo que se corta. La panza viaja con ella.
                        flecha = propia?.flecha ?: 0f
                    )
                )
            }
            // El borde derecho de una pared es una arista: ahí dobla, en punta o en curva.
            if (!esBanda && tramo < bordes.size - 2) {
                val esquina = esquinas.getOrNull(arista)
                arista++
                val arco = esquina?.arco
                angulos.add(
                    // Sin rótulo de ángulo: si este tramo es un arco, el siguiente se encuentra
                    // con él sin doblar —180°, seguir de largo—; si es una pared recta sin
                    // ángulo escrito, la escuadra de siempre.
                    if (arco == null) formatCm(
                        esquina?.grados ?: if (curvasPropias[tramo] != null) 180f else 90f
                    )
                    else {
                        // La curva va con su paño: el alto y el puente los lleva su propia banda,
                        // que es el tramo siguiente, y se miden como los de cualquier pared.
                        val banda = tramo + 1
                        val altoCurva = if (banda + 1 < arriba.size) {
                            maxOf(pxToCm(pie - arriba[banda].y), pxToCm(pie - arriba[banda + 1].y))
                        } else 0f
                        val puenteCurva = if (banda + 1 < bordes.size) {
                            puentes.firstOrNull {
                                it.rect.centerX() > bordes[banda] - 0.5f &&
                                    it.rect.centerX() < bordes[banda + 1] + 0.5f
                            }?.let { pxToCm(pie - it.start.y) } ?: 0f
                        } else 0f
                        EsquinaMedida.textoDeCurva(
                            CurvaEsquina(arco.desarrollo, arco.cuerda, altoCurva, puenteCurva)
                        )
                    }
                )
            }
        }
        // Dos paredes hacen una esquina, pero UNA sola curva ya es una ventana: la curva no
        // necesita que haya otra pared para ser algo que la calculadora sepa armar.
        val vale = lados.size >= 2 || (lados.size == 1 && lados[0].esCurva)
        return if (vale) EsquinaMedida(lados, angulos) else null

    }

    fun contornoPrincipalEnCm(): List<Pair<Float, Float>>? {

        fun area(e: Element): Float = boundsForElement(e).let { it.width() * it.height() }
        val composite = elementos.filterIsInstance<Element.Composite>().maxByOrNull { area(it) }
        // El triángulo se dibuja con su herramienta, así que no es una figura recortada y no tiene
        // contorno guardado: sin esto, un vano triangular llegaba a la calculadora como el
        // rectángulo de su caja y el diseño salía recto.
        val triangulo = elementos.filterIsInstance<Element.Shape>()
            .filter { it.tool == Tool.TRIANGLE }
            .maxByOrNull { area(it) }
        if (triangulo != null && (composite == null || area(triangulo) > area(composite))) {
            return contornoDeTriangulo(triangulo).takeIf { it.size >= 3 }
        }
        val elegido = composite ?: return null
        // Tres puntos ya son un vano: un triángulo recortado es una ventana como cualquier otra.
        val contorno = elegido.contours.firstOrNull()?.takeIf { it.size >= 3 } ?: return null
        val caja = boundsForElement(elegido)
        return contorno.map { p -> pxToCm(p.x - caja.left) to pxToCm(p.y - caja.top) }
    }

    /**
     * Los tres vértices del triángulo en centímetros, referidos a su esquina superior izquierda.
     *
     * Se escalan con los centímetros ACOTADOS de la figura, no con la densidad de pantalla: así el
     * contorno mide exactamente lo que la medida que viaja con él, que es de donde la calculadora
     * saca el ancho y el alto.
     */
    private fun contornoDeTriangulo(shape: Element.Shape): List<Pair<Float, Float>> {
        val caja = boundsForElement(shape)
        if (caja.width() <= 1f || caja.height() <= 1f) return emptyList()
        val kx = if (shape.widthCm > 0f) shape.widthCm / caja.width() else pxToCm(1f)
        val ky = if (shape.heightCm > 0f) shape.heightCm / caja.height() else pxToCm(1f)
        val (punta, der, izq) = verticesTriangulo(shape)
        return listOf(izq, der, punta).map { p ->
            (p.x - caja.left) * kx to (p.y - caja.top) * ky
        }
    }

    /**
     * Medida mayor del apunte: el lado horizontal y el vertical más grandes.
     *
     * Se leen primero los centímetros que cada figura guarda, porque son independientes del equipo.
     * Reconstruirlos desde los píxeles falla cuando el apunte se dibujó en un celular con otra
     * densidad de pantalla: las coordenadas se guardan en píxeles de quien dibujó, así que un
     * rectángulo de 101 cm trazado a densidad 2.75 se leía como 158.7 cm en un equipo de 1.75
     * (todas las medidas escaladas por el mismo factor). El cálculo por píxeles queda de reserva
     * para apuntes sin figuras acotadas, como los que solo tienen trazo libre o una imagen de fondo.
     */
    fun medidaPrincipal(): MedidaPrincipal? {
        medidaPrincipalEnCm()?.let { return it }

        val segmentos = segmentosExistentes()
        val horizontal = segmentos
            .filter { (a, b) -> abs(a.y - b.y) <= maxOf(4f, abs(a.x - b.x) * 0.08f) }
            .maxOfOrNull { (a, b) -> pxToCm(abs(b.x - a.x)) }
            ?: 0f
        val vertical = segmentos
            .filter { (a, b) -> abs(a.x - b.x) <= maxOf(4f, abs(a.y - b.y) * 0.08f) }
            .maxOfOrNull { (a, b) -> pxToCm(abs(b.y - a.y)) }
            ?: 0f

        if (horizontal > 0f && vertical > 0f) return MedidaPrincipal(horizontal, vertical)

        val bounds = boundsForContent() ?: return null
        if (bounds.width() <= 1f || bounds.height() <= 1f) return null
        return MedidaPrincipal(pxToCm(bounds.width()), pxToCm(bounds.height()))
    }

    /**
     * Ajusta un boceto recién cargado a la densidad de ESTE equipo.
     *
     * Las coordenadas se guardan en píxeles del celular que dibujó, mientras que la medida real vive
     * en los centímetros acotados de cada figura. Si el apunte viene de un equipo con otra densidad,
     * el trazo queda a otra escala: se ve distinto y, sobre todo, cualquier edición recalcularía las
     * cotas con la densidad de aquí y CAMBIARÍA medidas reales (un rectángulo de 101 cm dibujado a
     * 2.75 pasaba a 158.7 cm en un equipo de 1.75, y al reeditarlo se encogía).
     *
     * Se deduce la densidad con que se dibujó comparando píxeles contra centímetros acotados, se
     * reescala el trazo y se reponen los centímetros originales tal cual: el dibujo se adapta, la
     * medida no se toca. El tamaño exacto del gráfico no importa; la medida en cm sí.
     */
    private fun normalizarDensidad(cargados: List<Element>) {
        val pxPorCmDibujo = pxPorCmDelDibujo(cargados) ?: return
        if (pxPorCmDibujo <= 0.01f) return
        val factor = resources.displayMetrics.density / pxPorCmDibujo
        if (!factor.isFinite() || factor <= 0f || abs(factor - 1f) < 0.01f) return
        val cotas = capturarCotas(cargados)
        val origen = PointF(0f, 0f)
        cargados.forEach { scaleElement(it, factor, origen) }
        restaurarCotas(cargados, cotas)
    }

    /** Píxeles por centímetro con que se dibujó el apunte (mediana de las figuras acotadas). */
    private fun pxPorCmDelDibujo(cargados: List<Element>): Float? {
        val muestras = mutableListOf<Float>()
        fun considerar(element: Element) {
            when (element) {
                is Element.Group -> element.children.forEach { considerar(it) }
                is Element.Shape -> when (element.tool) {
                    Tool.RECTANGLE, Tool.TRIANGLE, Tool.CIRCLE -> {
                        val ancho = abs(element.rect.width())
                        val alto = abs(element.rect.height())
                        if (element.widthCm > 0.5f && ancho > 1f) muestras += ancho / element.widthCm
                        if (element.heightCm > 0.5f && alto > 1f) muestras += alto / element.heightCm
                    }
                    Tool.LINE, Tool.ORTHO_LINE -> {
                        val largo = distancia(element.start, element.end)
                        if (element.lengthCm > 0.5f && largo > 1f) muestras += largo / element.lengthCm
                    }
                    Tool.NONE, Tool.FREEHAND, Tool.MAGNET_PEN, Tool.TEXT, Tool.SELECT, Tool.NODO -> Unit
                }
                else -> Unit
            }
        }
        cargados.forEach { considerar(it) }
        if (muestras.isEmpty()) return null
        return muestras.sorted()[muestras.size / 2]
    }

    private fun capturarCotas(cargados: List<Element>): List<FloatArray> {
        val cotas = mutableListOf<FloatArray>()
        fun recorrer(element: Element) {
            when (element) {
                is Element.Group -> element.children.forEach { recorrer(it) }
                is Element.Shape -> cotas += floatArrayOf(
                    element.widthCm, element.heightCm, element.diameterCm, element.lengthCm,
                    element.topCm, element.rightCm, element.bottomCm, element.leftCm
                )
                is Element.Composite -> cotas += floatArrayOf(element.widthCm, element.heightCm)
                else -> Unit
            }
        }
        cargados.forEach { recorrer(it) }
        return cotas
    }

    private fun restaurarCotas(cargados: List<Element>, cotas: List<FloatArray>) {
        var indice = 0
        fun recorrer(element: Element) {
            when (element) {
                is Element.Group -> element.children.forEach { recorrer(it) }
                is Element.Shape -> {
                    val c = cotas.getOrNull(indice++) ?: return
                    element.widthCm = c[0]; element.heightCm = c[1]
                    element.diameterCm = c[2]; element.lengthCm = c[3]
                    element.topCm = c[4]; element.rightCm = c[5]
                    element.bottomCm = c[6]; element.leftCm = c[7]
                }
                is Element.Composite -> {
                    val c = cotas.getOrNull(indice++) ?: return
                    element.widthCm = c[0]; element.heightCm = c[1]
                }
                else -> Unit
            }
        }
        cargados.forEach { recorrer(it) }
    }

    /** Mayor ancho y mayor alto según los centímetros acotados en las figuras, o null si no hay. */
    private fun medidaPrincipalEnCm(): MedidaPrincipal? {
        var horizontal = 0f
        var vertical = 0f

        fun considerar(element: Element) {
            when (element) {
                is Element.Group -> element.children.forEach { considerar(it) }
                is Element.Shape -> when (element.tool) {
                    Tool.RECTANGLE -> {
                        horizontal = maxOf(horizontal, element.widthCm, element.topCm, element.bottomCm)
                        vertical = maxOf(vertical, element.heightCm, element.leftCm, element.rightCm)
                    }
                    Tool.TRIANGLE, Tool.CIRCLE -> {
                        horizontal = maxOf(horizontal, element.widthCm)
                        vertical = maxOf(vertical, element.heightCm)
                    }
                    Tool.LINE, Tool.ORTHO_LINE -> {
                        // Una línea suma a lo ancho o a lo alto según hacia dónde corre.
                        if (abs(element.end.x - element.start.x) >= abs(element.end.y - element.start.y)) {
                            horizontal = maxOf(horizontal, element.lengthCm)
                        } else {
                            vertical = maxOf(vertical, element.lengthCm)
                        }
                    }
                    Tool.NONE, Tool.FREEHAND, Tool.MAGNET_PEN, Tool.TEXT, Tool.SELECT, Tool.NODO -> Unit
                }
                else -> Unit
            }
        }

        elementos.forEach { considerar(it) }
        return if (horizontal > 0f && vertical > 0f) MedidaPrincipal(horizontal, vertical) else null
    }

    fun loadBackground(file: File?) {
        fondo = file?.let { BitmapFactory.decodeFile(it.absolutePath) }
        elementos.clear()
        registrarAccion()
        cotaHits.clear()
        trazoActual.reset()
        dibujando = false
        resetViewport()
        invalidate()
    }

    fun exportEditableState(): String {
        val root = JSONObject()
        val items = JSONArray()
        elementos.forEach { element -> items.put(elementJson(element)) }
        root.put("version", 1)
        root.put("elements", items)
        return root.toString()
    }

    fun loadEditableState(json: String): Boolean {
        return runCatching {
            val root = JSONObject(json)
            val items = root.optJSONArray("elements") ?: JSONArray()
            val cargados = mutableListOf<Element>()
            for (i in 0 until items.length()) {
                val obj = items.getJSONObject(i)
                readElementJson(obj)?.let { cargados.add(it) }
            }
            normalizarDensidad(cargados)
            elementos.clear()
            elementos.addAll(cargados)
            establecerBaseline()   // el sketch cargado es la BASE del historial (undo no lo borra)
            cotaHits.clear()
            selectedIndices.clear()
            trazoActual.reset()
            fondo = null
            dibujando = false
            resetViewport()
            invalidate()
            true
        }.getOrDefault(false)
    }

    private fun pointJson(point: PointF): JSONArray = JSONArray()
        .put(point.x.toDouble())
        .put(point.y.toDouble())

    private fun readPoint(array: JSONArray): PointF {
        return PointF(
            array.optDouble(0, 0.0).toFloat(),
            array.optDouble(1, 0.0).toFloat()
        )
    }

    private fun rectJson(rect: RectF): JSONArray = JSONArray()
        .put(rect.left.toDouble())
        .put(rect.top.toDouble())
        .put(rect.right.toDouble())
        .put(rect.bottom.toDouble())

    private fun readRect(array: JSONArray): RectF {
        return RectF(
            array.optDouble(0, 0.0).toFloat(),
            array.optDouble(1, 0.0).toFloat(),
            array.optDouble(2, 0.0).toFloat(),
            array.optDouble(3, 0.0).toFloat()
        )
    }

    private fun pointsJson(points: List<PointF>): JSONArray {
        val array = JSONArray()
        points.forEach { array.put(pointJson(it)) }
        return array
    }

    private fun readPoints(array: JSONArray): MutableList<PointF> {
        val points = mutableListOf<PointF>()
        for (i in 0 until array.length()) {
            points.add(readPoint(array.optJSONArray(i) ?: JSONArray()))
        }
        return points
    }

    private fun contoursJson(contours: List<List<PointF>>): JSONArray {
        val array = JSONArray()
        contours.forEach { array.put(pointsJson(it)) }
        return array
    }

    private fun readContours(array: JSONArray): MutableList<MutableList<PointF>> {
        val contours = mutableListOf<MutableList<PointF>>()
        for (i in 0 until array.length()) {
            val contour = readPoints(array.optJSONArray(i) ?: JSONArray())
            if (contour.size >= 2) contours.add(contour)
        }
        return contours
    }

    private fun floatMatrixJson(values: List<List<Float>>): JSONArray {
        val array = JSONArray()
        values.forEach { row ->
            val rowArray = JSONArray()
            row.forEach { rowArray.put(it.toDouble()) }
            array.put(rowArray)
        }
        return array
    }

    private fun readFloatMatrix(array: JSONArray): MutableList<MutableList<Float>> {
        val result = mutableListOf<MutableList<Float>>()
        for (i in 0 until array.length()) {
            val row = array.optJSONArray(i) ?: JSONArray()
            val values = mutableListOf<Float>()
            for (j in 0 until row.length()) values.add(row.optDouble(j, 0.0).toFloat())
            result.add(values)
        }
        return result
    }

    private fun elementJson(element: Element): JSONObject {
        return when (element) {
            is Element.Freehand -> JSONObject()
                .put("type", "freehand")
                .put("points", pointsJson(pointsFromPath(element.path)))
            is Element.Composite -> JSONObject()
                .put("type", "composite")
                .put("template", element.template)
                .put("widthCm", element.widthCm)
                .put("heightCm", element.heightCm)
                .put("contours", contoursJson(element.contours))
                .put("sideCms", floatMatrixJson(element.sideCms))
                .put("rotationDeg", element.rotationDeg)
                .put("reflejado", element.reflejado)
            is Element.Shape -> JSONObject()
                .put("type", "shape")
                .put("tool", element.tool.name)
                .put("rect", rectJson(element.rect))
                .put("start", pointJson(element.start))
                .put("end", pointJson(element.end))
                .put("widthCm", element.widthCm)
                .put("heightCm", element.heightCm)
                .put("diameterCm", element.diameterCm)
                .put("lengthCm", element.lengthCm)
                .put("topLeft", pointJson(element.topLeft))
                .put("topRight", pointJson(element.topRight))
                .put("bottomRight", pointJson(element.bottomRight))
                .put("bottomLeft", pointJson(element.bottomLeft))
                .put("topCm", element.topCm)
                .put("rightCm", element.rightCm)
                .put("bottomCm", element.bottomCm)
                .put("leftCm", element.leftCm)
                .put("cotaHint", element.cotaHint)
                .put("largoFijado", element.largoFijado)
            is Element.Group -> JSONObject()
                .put("type", "group")
                .put("children", JSONArray().apply {
                    element.children.forEach { put(elementJson(it)) }
                })
            is Element.TextLabel -> JSONObject()
                .put("type", "text")
                .put("titulo", element.titulo)
                .put("movida", element.movida)
                .put("rol", element.rol)
                .put("text", element.text)
                .put("x", element.x)
                .put("y", element.y)
                .put("textSize", element.textSize)
            is Element.InfoBox -> JSONObject()
                .put("type", "infoBox")
                .put("text", element.text)
                .put("rect", rectJson(element.rect))
                .put("textSize", element.textSize)
            is Element.Symbol -> JSONObject()
                .put("type", "symbol")
                .put("drawableName", element.drawableName)
                .put("rect", rectJson(element.rect))
                .put("reflejado", element.reflejado)
        }
    }

    private fun readElementJson(obj: JSONObject): Element? {
        return when (obj.optString("type")) {
            "freehand" -> {
                val points = readPoints(obj.optJSONArray("points") ?: JSONArray())
                if (points.size >= 2) Element.Freehand(pathFromPoints(points)) else null
            }
            "composite" -> {
                val contours = readContours(obj.optJSONArray("contours") ?: JSONArray())
                if (contours.isEmpty()) return null
                val sideCms = readFloatMatrix(obj.optJSONArray("sideCms") ?: JSONArray())
                    .ifEmpty { sideCmsForContours(contours) }
                val template = obj.optString("template").takeIf { it.isNotBlank() && it != "null" }
                val path = when (template) {
                    TEMPLATE_F5 -> pathRecurrenteF5(dimsRecurrenteF5(contours.first(), sideCms) ?: return null)
                    TEMPLATE_F6 -> pathRecurrenteF6(dimsRecurrenteF6(contours.first(), sideCms) ?: return null)
                    TEMPLATE_ROUNDED -> pathRoundedCorners(dimsRoundedCorners(contours.first(), sideCms) ?: return null)
                    else -> pathFromContours(contours)
                }
                val bounds = RectF()
                path.computeBounds(bounds, true)
                val compuesto = Element.Composite(
                    path = path,
                    widthCm = obj.optDouble("widthCm", pxToCm(bounds.width()).toDouble()).toFloat(),
                    heightCm = obj.optDouble("heightCm", pxToCm(bounds.height()).toDouble()).toFloat(),
                    contours = contours,
                    sideCms = sideCms,
                    template = template,
                    rotationDeg = obj.optDouble("rotationDeg", 0.0).toFloat(),
                    reflejado = obj.optBoolean("reflejado", false)
                )
                // Un apunte guardado ya trae sus medidas puestas: se dan por anotadas, así que
                // corregir una sola vuelve a rehacer la forma en vez de pedirlas todas de nuevo.
                if (template in TEMPLATES_POLIGONALES) sincronizarDeclarados(compuesto)
                compuesto
            }
            "shape" -> {
                val rect = readRect(obj.optJSONArray("rect") ?: JSONArray())
                val tool = runCatching {
                    Tool.valueOf(obj.optString("tool", Tool.RECTANGLE.name))
                }.getOrDefault(Tool.RECTANGLE)
                Element.Shape(
                    tool = tool,
                    rect = rect,
                    start = readPoint(obj.optJSONArray("start") ?: JSONArray()),
                    end = readPoint(obj.optJSONArray("end") ?: JSONArray()),
                    widthCm = obj.optDouble("widthCm", pxToCm(rect.width()).toDouble()).toFloat(),
                    heightCm = obj.optDouble("heightCm", pxToCm(rect.height()).toDouble()).toFloat(),
                    diameterCm = obj.optDouble("diameterCm", pxToCm(maxOf(rect.width(), rect.height())).toDouble()).toFloat(),
                    lengthCm = obj.optDouble("lengthCm", pxToCm(hypot(rect.width().toDouble(), rect.height().toDouble()).toFloat()).toDouble()).toFloat(),
                    topLeft = readPoint(obj.optJSONArray("topLeft") ?: JSONArray()),
                    topRight = readPoint(obj.optJSONArray("topRight") ?: JSONArray()),
                    bottomRight = readPoint(obj.optJSONArray("bottomRight") ?: JSONArray()),
                    bottomLeft = readPoint(obj.optJSONArray("bottomLeft") ?: JSONArray()),
                    topCm = obj.optDouble("topCm", pxToCm(rect.width()).toDouble()).toFloat(),
                    rightCm = obj.optDouble("rightCm", pxToCm(rect.height()).toDouble()).toFloat(),
                    bottomCm = obj.optDouble("bottomCm", pxToCm(rect.width()).toDouble()).toFloat(),
                    leftCm = obj.optDouble("leftCm", pxToCm(rect.height()).toDouble()).toFloat(),
                    cotaHint = obj.optString("cotaHint").takeIf { it.isNotBlank() && it != "null" },
                    largoFijado = obj.optBoolean("largoFijado", false)
                )
            }
            "group" -> {
                val childrenJson = obj.optJSONArray("children") ?: JSONArray()
                val children = mutableListOf<Element>()
                for (i in 0 until childrenJson.length()) {
                    readElementJson(childrenJson.getJSONObject(i))?.let { children.add(it) }
                }
                if (children.isNotEmpty()) Element.Group(children) else null
            }
            "text" -> {
                val text = obj.optString("text").trim()
                if (text.isBlank()) {
                    null
                } else {
                    Element.TextLabel(
                        text = text,
                        x = obj.optDouble("x", 0.0).toFloat(),
                        y = obj.optDouble("y", 0.0).toFloat(),
                        textSize = obj.optDouble("textSize", 34.0).toFloat(),
                        // Los apuntes de antes no traían la marca: se quedan donde están, que es
                        // como se guardaron.
                        titulo = obj.optBoolean("titulo", false),
                        movida = obj.optBoolean("movida", false),
                        rol = obj.optString("rol").takeIf { it.isNotBlank() && it != "null" }
                    )
                }
            }
            "infoBox" -> {
                val text = obj.optString("text").trim()
                if (text.isBlank()) {
                    null
                } else {
                    Element.InfoBox(
                        text = text,
                        rect = readRect(obj.optJSONArray("rect") ?: JSONArray()),
                        textSize = obj.optDouble("textSize", spToPx(12f).toDouble()).toFloat()
                    )
                }
            }
            "symbol" -> {
                val drawableName = obj.optString("drawableName").trim()
                if (drawableName.isBlank() || drawableIdForName(drawableName) == 0) {
                    null
                } else {
                    Element.Symbol(
                        drawableName = drawableName,
                        rect = readRect(obj.optJSONArray("rect") ?: JSONArray()),
                        reflejado = obj.optBoolean("reflejado", false)
                    )
                }
            }
            else -> null
        }
    }

    private fun pointsFromPath(path: Path): List<PointF> {
        val result = mutableListOf<PointF>()
        val measure = PathMeasure(path, false)
        val pos = FloatArray(2)
        do {
            val length = measure.length
            if (length <= 0f) continue
            var distance = 0f
            while (distance < length) {
                measure.getPosTan(distance, pos, null)
                addPointIfDistinct(result, PointF(pos[0], pos[1]))
                distance += 3f
            }
            measure.getPosTan(length, pos, null)
            addPointIfDistinct(result, PointF(pos[0], pos[1]))
        } while (measure.nextContour())
        return result
    }

    private fun pathFromPoints(points: List<PointF>): Path {
        return Path().apply {
            if (points.isEmpty()) return@apply
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
    }

    private fun pathFromContours(contours: List<List<PointF>>): Path {
        return Path().apply {
            contours.forEach { contour ->
                if (contour.isEmpty()) return@forEach
                moveTo(contour.first().x, contour.first().y)
                contour.drop(1).forEach { lineTo(it.x, it.y) }
                close()
            }
        }
    }

    fun exportBitmap(): Bitmap {
        // El export no pasa por onDraw y aquí no hay zoom de pantalla: las cotas se miden con la
        // escala del propio dibujo, como se veían siempre en el PDF. Y los títulos se recolocan
        // antes de medir el encuadre, para que no se salgan del papel.
        val escalaPrevia = escalaCota
        escalaCota = 1f
        exportando = true
        reubicarTitulos()
        val contentBounds = boundsForContent()
        if (contentBounds == null) {
            fondo?.let { return it.copy(Bitmap.Config.ARGB_8888, false) }
        }
        val bounds = contentBounds ?: RectF(0f, 0f, width.toFloat().coerceAtLeast(1f), height.toFloat().coerceAtLeast(1f))
        val margin = margenExportacion(bounds)
        val exportBounds = RectF(bounds).apply { inset(-margin, -margin) }
        val rawW = exportBounds.width().coerceAtLeast(1f)
        val rawH = exportBounds.height().coerceAtLeast(1f)
        val maxSide = 2400f
        val exportScale = minOf(1f, maxSide / maxOf(rawW, rawH))
        val outW = (rawW * exportScale).toInt().coerceAtLeast(1)
        val outH = (rawH * exportScale).toInt().coerceAtLeast(1)
        val out = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(out)
        canvas.drawColor(0xFFF7F4EC.toInt(), PorterDuff.Mode.SRC)
        canvas.save()
        canvas.scale(exportScale, exportScale)
        canvas.translate(-exportBounds.left, -exportBounds.top)
        cotaTextRects.clear()
        elementos.forEachIndexed { index, element -> drawElement(canvas, index, element, false) }
        dibujarPlantasDeEsquina(canvas, false)
        dibujarCotasAEscuadra(canvas, false)
        cotaTextRects.clear()
        canvas.restore()
        escalaCota = escalaPrevia
        exportando = false
        return out
    }

    private fun margenExportacion(bounds: RectF): Float {
        val base = 96f
        val proporcional = maxOf(bounds.width(), bounds.height()) * 0.025f
        return maxOf(base, proporcional).coerceAtMost(220f)
    }

    // Dentro de estos grados respecto a la horizontal o la vertical, el tramo se endereza. Más
    // inclinado que eso se respeta tal cual: entonces la diagonal es a propósito.
    private val GRADOS_IMAN = 20f

    /**
     * Lápiz magnético: convierte el garabato en tramos rectos.
     *
     * El trazo se reduce a los vértices que de verdad lo definen y cada tramo se lleva a la
     * horizontal o a la vertical, que es como se miden las piezas; el que sale claramente inclinado
     * —una diagonal de verdad— se deja como se dibujó. Si el trazo vuelve cerca de donde empezó, se
     * cierra y queda un polígono con la cota de cada lado; si no, queda una cadena de líneas, cada
     * una con su medida. Sirve para tomar del sitio una pieza irregular sin pelearse con el pulso.
     */
    private fun crearTrazoImantado(): List<Element> {
        val puntos = pointsFromPath(trazoActual)
        if (puntos.size < 2) return emptyList()
        val minTramo = 16f * resources.displayMetrics.density
        val vertices = rdp(puntos, minTramo * 0.45f)
        // Tramos demasiado cortos son pulso, no esquinas: se funden con el vecino.
        var i = 1
        while (i < vertices.size - 1) {
            if (distancia(vertices[i], vertices[i + 1]) < minTramo) vertices.removeAt(i) else i++
        }
        if (vertices.size < 2) return emptyList()

        val cerrado = vertices.size >= 4 &&
            distancia(vertices.first(), vertices.last()) <= maxOf(minTramo * 1.8f, 44f)
        if (cerrado) vertices.removeAt(vertices.lastIndex)
        if (vertices.size < 2) return emptyList()

        enderezarTramos(vertices, cerrado)
        fusionarColineales(vertices, cerrado)
        if (cerrado && vertices.size < 3) return emptyList()
        if (vertices.size < 2) return emptyList()

        if (!cerrado) {
            return (0 until vertices.size - 1).map { idx ->
                crearShape(Tool.LINE, vertices[idx], vertices[idx + 1])
            }
        }
        val contours = mutableListOf(vertices)
        val bounds = RectF(
            vertices.minOf { it.x }, vertices.minOf { it.y },
            vertices.maxOf { it.x }, vertices.maxOf { it.y }
        )
        return listOf(
            Element.Composite(
                path = pathFromContours(contours),
                widthCm = pxToCm(bounds.width()),
                heightCm = pxToCm(bounds.height()),
                contours = contours,
                sideCms = sideCmsForContours(contours),
                // Se acota y se edita como una forma recurrente: se anotan todas las medidas y la
                // figura se rehace de una vez con la última, sin deformarse por el camino.
                template = TEMPLATE_LIBRE
            )
        )
    }

    /**
     * Endereza la cadena de vértices. Cada tramo arrastra a su vértice final, así que el siguiente
     * arranca ya corregido y la figura no se desmonta.
     */
    private fun enderezarTramos(v: MutableList<PointF>, cerrado: Boolean) {
        val n = v.size
        for (i in 0 until n - 1) {
            val a = v[i]
            val b = v[i + 1]
            val grados = Math.toDegrees(
                kotlin.math.atan2(abs(b.y - a.y).toDouble(), abs(b.x - a.x).toDouble())
            ).toFloat()
            when {
                grados <= GRADOS_IMAN -> b.y = a.y              // horizontal
                grados >= 90f - GRADOS_IMAN -> b.x = a.x        // vertical
                else -> Unit                                     // diagonal buscada: se respeta
            }
        }
        if (!cerrado || n < 3) return
        // El tramo de cierre vuelve al primer vértice: se ajusta el último para que cierre recto
        // sin torcer el tramo anterior (si el de cierre es horizontal, el anterior era vertical).
        val ultimo = v[n - 1]
        val primero = v[0]
        val grados = Math.toDegrees(
            kotlin.math.atan2(abs(primero.y - ultimo.y).toDouble(), abs(primero.x - ultimo.x).toDouble())
        ).toFloat()
        when {
            grados <= GRADOS_IMAN -> ultimo.y = primero.y
            grados >= 90f - GRADOS_IMAN -> ultimo.x = primero.x
        }
    }

    /**
     * Funde los tramos seguidos que van en la misma dirección.
     *
     * Al enderezar, un temblor en mitad de un lado deja dos tramos rectos y alineados: se veía una
     * sola raya, pero con DOS cotas. Una recta tiene una medida, así que el vértice sobrante se
     * quita y los dos tramos pasan a ser uno.
     */
    private fun fusionarColineales(v: MutableList<PointF>, cerrado: Boolean) {
        var i = if (cerrado) 0 else 1
        val minimo = if (cerrado) 3 else 2
        while (v.size > minimo && i < (if (cerrado) v.size else v.size - 1)) {
            val n = v.size
            val a = v[(i - 1 + n) % n]
            val b = v[i]
            val c = v[(i + 1) % n]
            if (mismaDireccion(a, b, c)) {
                v.removeAt(i)
                if (i > (if (cerrado) 0 else 1)) i--   // el vecino de atrás puede alinearse ahora
            } else {
                i++
            }
        }
    }

    /** Dos tramos van en la misma dirección si su giro es despreciable (menos de 8 grados). */
    private fun mismaDireccion(a: PointF, b: PointF, c: PointF): Boolean {
        if (distancia(a, b) < 0.5f || distancia(b, c) < 0.5f) return true
        val uno = kotlin.math.atan2((b.y - a.y).toDouble(), (b.x - a.x).toDouble())
        val dos = kotlin.math.atan2((c.y - b.y).toDouble(), (c.x - b.x).toDouble())
        var giro = Math.toDegrees(uno - dos)
        giro = ((giro % 360.0) + 360.0) % 360.0
        if (giro > 180.0) giro = 360.0 - giro
        return giro <= 8.0
    }

    /** Douglas-Peucker: deja solo los vértices que de verdad definen el trazo. */
    private fun rdp(puntos: List<PointF>, tolerancia: Float): MutableList<PointF> {
        if (puntos.size < 3) return puntos.map { PointF(it.x, it.y) }.toMutableList()
        var indice = 0
        var maxima = 0f
        for (i in 1 until puntos.size - 1) {
            val d = distanciaARecta(puntos[i], puntos.first(), puntos.last())
            if (d > maxima) { maxima = d; indice = i }
        }
        if (maxima <= tolerancia) {
            return mutableListOf(
                PointF(puntos.first().x, puntos.first().y),
                PointF(puntos.last().x, puntos.last().y)
            )
        }
        val izquierda = rdp(puntos.subList(0, indice + 1), tolerancia)
        val derecha = rdp(puntos.subList(indice, puntos.size), tolerancia)
        izquierda.removeAt(izquierda.lastIndex)
        izquierda.addAll(derecha)
        return izquierda
    }

    private fun distanciaARecta(p: PointF, a: PointF, b: PointF): Float {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val largo2 = dx * dx + dy * dy
        if (largo2 <= 0.0001f) return distancia(p, a)
        val t = (((p.x - a.x) * dx + (p.y - a.y) * dy) / largo2).coerceIn(0f, 1f)
        return distancia(p, PointF(a.x + t * dx, a.y + t * dy))
    }

    private fun crearElemento(x: Float, y: Float): Element? {
        if (!trazoValido(x, y)) return null
        if (herramienta == Tool.FREEHAND) return Element.Freehand(Path(trazoActual))

        val endPoint = puntoFinalHerramienta(x, y)
        val rect = rectFrom(startPoint, endPoint.x, endPoint.y)
        val lineLengthPx = hypot((endPoint.x - startPoint.x).toDouble(), (endPoint.y - startPoint.y).toDouble()).toFloat()
        val widthCm = pxToCm(rect.width())
        val heightCm = pxToCm(rect.height())
        if (herramienta == Tool.RECTANGLE && rectangleRoundedCorner != null && rectangleRoundedRadiusCm > 0f) {
            return crearRectanguloConEsquinaRedondeada(rect, rectangleRoundedCorner ?: 0, rectangleRoundedRadiusCm)
        }
        return Element.Shape(
            tool = herramienta,
            rect = rect,
            start = PointF(startPoint.x, startPoint.y),
            end = PointF(endPoint.x, endPoint.y),
            widthCm = widthCm,
            heightCm = heightCm,
            diameterCm = pxToCm(maxOf(rect.width(), rect.height())),
            lengthCm = pxToCm(lineLengthPx),
            topLeft = PointF(rect.left, rect.top),
            topRight = PointF(rect.right, rect.top),
            bottomRight = PointF(rect.right, rect.bottom),
            bottomLeft = PointF(rect.left, rect.bottom),
            topCm = widthCm,
            rightCm = heightCm,
            bottomCm = widthCm,
            leftCm = heightCm
        )
    }

    private fun drawElement(canvas: Canvas, index: Int, element: Element, collectHits: Boolean) {
        // La línea de una cota a escuadra no es un trazo del apunte: la dibuja su propia rutina,
        // con su número y su flecha. Pintándola aquí salía además como una raya suelta con su
        // cota de largo, que mide lo mismo pero no es lo mismo.
        if ((element as? Element.Shape)?.cotaHint == COTA_ESCUADRA) return
        when (element) {
            is Element.Freehand -> canvas.drawPath(element.path, paint)
            is Element.TextLabel -> {
                sketchTextPaint.textSize = tamanoTexto(element)
                if (element.rol == ROL_ALFEIZAR) dibujarIconoAlfeizar(canvas, element)
                canvas.drawText(element.text, element.x, element.y, sketchTextPaint)
            }
            is Element.InfoBox -> drawInfoBox(canvas, element)
            is Element.Symbol -> drawSymbol(canvas, element)
            is Element.Composite -> {
                canvas.drawPath(element.path, paint)
                drawCompositeCotas(canvas, index, element, collectHits)
            }
            is Element.Shape -> {
                // La cota de alto interior no es trazo: solo su número. El marco de la ventana se
                // dibuja quebrado por las alturas que se hayan escrito dentro de él.
                val shapePath = when {
                    element.cotaHint in MARCOS_PLANTILLA ->
                        pathMarcoVentana(index, element) ?: pathForShape(element)
                    // La arista se dibuja doble: es donde la ventana dobla, no un parante más.
                    element.cotaHint == ESQUINA_QUIEBRE -> pathQuiebre(element)
                    else -> pathForShape(element)
                }
                if (element.cotaHint != "GRADA_TOTAL_COTA" && element.cotaHint != VENTANA_ALTO) {
                    canvas.drawPath(shapePath, paint)
                }
                drawCotas(canvas, index, element, collectHits)
            }
            is Element.Group -> {
                element.children.forEach { child -> drawElement(canvas, index, child, false) }
            }
        }
    }

    private fun drawInfoBox(canvas: Canvas, element: Element.InfoBox) {
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 30, 30)
            style = Paint.Style.STROKE
            strokeWidth = 2f
        }
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(230, 247, 244, 236)
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(element.rect, 8f, 8f, fillPaint)
        canvas.drawRoundRect(element.rect, 8f, 8f, borderPaint)
        sketchTextPaint.textSize = element.textSize
        sketchTextPaint.textAlign = Paint.Align.LEFT
        val lineHeight = element.textSize * 1.25f
        var y = element.rect.top + lineHeight
        element.text.lines().forEach { line ->
            canvas.drawText(line, element.rect.left + 12f, y, sketchTextPaint)
            y += lineHeight
        }
        sketchTextPaint.textAlign = Paint.Align.LEFT
    }

    private fun drawSymbol(canvas: Canvas, element: Element.Symbol) {
        val drawable = context.getDrawable(drawableIdForName(element.drawableName)) ?: return
        drawable.setBounds(
            element.rect.left.toInt(),
            element.rect.top.toInt(),
            element.rect.right.toInt(),
            element.rect.bottom.toInt()
        )
        if (!element.reflejado) {
            drawable.draw(canvas)
            return
        }
        canvas.save()
        canvas.scale(-1f, 1f, element.rect.centerX(), element.rect.centerY())
        drawable.draw(canvas)
        canvas.restore()
    }

    private fun drawableIdForName(drawableName: String): Int {
        return resources.getIdentifier(drawableName, "drawable", context.packageName)
    }

    private fun drawSelection(canvas: Canvas) {
        for (index in selectedIndices) {
            val element = elementos.getOrNull(index) ?: continue
            val bounds = boundsForElement(element)
            bounds.inset(-10f, -10f)
            canvas.drawRect(bounds, selectionPaint)
        }
    }

    private fun manejarSeleccionDown(x: Float, y: Float) {
        parent?.requestDisallowInterceptTouchEvent(true)
        val hit = hitElement(x, y)
        if (hit == null) {
            selectedIndices.clear()
            dragIndex = null
            invalidate()
            return
        }
        if (hit !in selectedIndices) {
            selectedIndices.add(hit)
        }
        dragIndex = hit
        lastDragPoint = PointF(x, y)
        movedSelection = false
        invalidate()
    }

    private fun moverSeleccion(x: Float, y: Float) {
        val index = dragIndex ?: return
        if (index !in selectedIndices) return
        val dx = x - lastDragPoint.x
        val dy = y - lastDragPoint.y
        if (abs(dx) > 0.5f || abs(dy) > 0.5f) {
            selectedIndices.forEach { indice ->
                translateElement(indice, dx, dy)
                // Movido a mano: desde aquí manda el sitio que le dio el usuario.
                (elementos.getOrNull(indice) as? Element.TextLabel)?.movida = true
            }
            movedSelection = true
            lastDragPoint = PointF(x, y)
            invalidate()
        }
    }

    private fun finalizarMoverSeleccion() {
        if (movedSelection) {
            aplicarImanASeleccion()
            registrarAccion()   // el arrastre completo es UNA acción reversible
        }
        dragIndex = null
        movedSelection = false
        parent?.requestDisallowInterceptTouchEvent(false)
        invalidate()
    }

    private fun hitElement(x: Float, y: Float): Int? {
        val touchPad = 24f * resources.displayMetrics.density
        for (i in elementos.indices.reversed()) {
            val bounds = boundsForElement(elementos[i])
            bounds.inset(-touchPad, -touchPad)
            if (bounds.contains(x, y)) return i
        }
        return null
    }

    private fun reemplazarSeleccionCon(element: Element) {
        val sorted = selectedIndices.sortedDescending()
        sorted.forEach { index ->
            if (index in elementos.indices) elementos.removeAt(index)
        }
        elementos.add(element)
        selectedIndices.clear()
        selectedIndices.add(elementos.lastIndex)
        invalidate()
    }

    private fun compositeFromPath(path: Path): Element.Composite {
        val bounds = RectF()
        path.computeBounds(bounds, true)
        val contours = contoursFromPath(path).ifEmpty { mutableListOf(rectContour(bounds)) }
        return Element.Composite(
            path = path,
            widthCm = pxToCm(bounds.width()),
            heightCm = pxToCm(bounds.height()),
            contours = contours,
            sideCms = sideCmsForContours(contours)
        )
    }

    /**
     * ¿Ese elemento es un rectángulo de verdad?
     *
     * Importa para cortar: el atajo que calcula el contorno desde las CAJAS de las dos figuras
     * solo vale si la que se corta es un rectángulo. Con una figura ya cortada, su caja vuelve a
     * ser el rectángulo entero y el corte anterior se perdía —la figura se veía bien, porque el
     * dibujo usa el camino de verdad, pero lo guardado tenía un solo corte.
     */
    private fun esRectangulo(elemento: Element): Boolean = when (elemento) {
        is Element.Shape -> elemento.tool == Tool.RECTANGLE
        is Element.Composite -> {
            val contorno = elemento.contours.singleOrNull()
            if (contorno == null || contorno.size != 4) false
            else {
                val caja = boundsForElement(elemento)
                contorno.all { p ->
                    (same(p.x, caja.left) || same(p.x, caja.right)) &&
                        (same(p.y, caja.top) || same(p.y, caja.bottom))
                }
            }
        }
        else -> false
    }

    private fun compositeFromDifference(path: Path, baseElement: Element, cutterElement: Element): Element.Composite {
        val bounds = RectF()
        path.computeBounds(bounds, true)
        // El atajo por cajas solo vale si lo que se corta es un rectángulo; si ya venía cortado,
        // manda el camino de verdad o se perdería el corte anterior.
        val contours = (if (esRectangulo(baseElement) && esRectangulo(cutterElement))
            contoursFromRectDifference(boundsForElement(baseElement), boundsForElement(cutterElement))
        else null)
            ?: contoursFromPath(path).ifEmpty { mutableListOf(rectContour(bounds)) }
        return Element.Composite(
            path = path,
            widthCm = pxToCm(bounds.width()),
            heightCm = pxToCm(bounds.height()),
            contours = contours,
            sideCms = sideCmsForContours(contours)
        )
    }

    private fun contoursFromPath(path: Path): MutableList<MutableList<PointF>> {
        val result = mutableListOf<MutableList<PointF>>()
        val measure = PathMeasure(path, true)
        val pos = FloatArray(2)
        do {
            val length = measure.length
            if (length <= 2f) continue

            val points = mutableListOf<PointF>()
            var distance = 0f
            // El paso tiene que ser MAYOR que las distancias con las que se descartan puntos: 1.5 px
            // al recogerlos y 3 px al simplificar. Muestreando cada 1 px, cada punto caía
            // demasiado cerca del anterior y se descartaban todos: el contorno salía vacío y la
            // figura se guardaba como su caja —una L unida volvía a ser un rectángulo.
            while (distance < length) {
                measure.getPosTan(distance, pos, null)
                addPointIfDistinct(points, PointF(pos[0], pos[1]))
                distance += 4f
            }
            measure.getPosTan(length, pos, null)
            addPointIfDistinct(points, PointF(pos[0], pos[1]))

            val simplified = simplifyContour(points)
            if (simplified.size >= 3) result.add(simplified)
        } while (measure.nextContour())
        return result
    }

    private fun contoursFromRectDifference(base: RectF, cutter: RectF): MutableList<MutableList<PointF>>? {
        if (base.width() <= 1f || base.height() <= 1f || cutter.width() <= 1f || cutter.height() <= 1f) return null
        val cut = RectF(
            maxOf(base.left, cutter.left),
            maxOf(base.top, cutter.top),
            minOf(base.right, cutter.right),
            minOf(base.bottom, cutter.bottom)
        )
        if (cut.width() <= 1f || cut.height() <= 1f) return mutableListOf(rectContour(base))
        if (same(cut.left, base.left) && same(cut.top, base.top) && same(cut.right, base.right) && same(cut.bottom, base.bottom)) {
            return null
        }

        val touchesLeft = same(cut.left, base.left)
        val touchesTop = same(cut.top, base.top)
        val touchesRight = same(cut.right, base.right)
        val touchesBottom = same(cut.bottom, base.bottom)

        val contour = when {
            touchesLeft && touchesTop -> mutableListOf(
                PointF(cut.right, base.top),
                PointF(base.right, base.top),
                PointF(base.right, base.bottom),
                PointF(base.left, base.bottom),
                PointF(base.left, cut.bottom),
                PointF(cut.right, cut.bottom)
            )
            touchesRight && touchesTop -> mutableListOf(
                PointF(base.left, base.top),
                PointF(cut.left, base.top),
                PointF(cut.left, cut.bottom),
                PointF(base.right, cut.bottom),
                PointF(base.right, base.bottom),
                PointF(base.left, base.bottom)
            )
            touchesRight && touchesBottom -> mutableListOf(
                PointF(base.left, base.top),
                PointF(base.right, base.top),
                PointF(base.right, cut.top),
                PointF(cut.left, cut.top),
                PointF(cut.left, base.bottom),
                PointF(base.left, base.bottom)
            )
            touchesLeft && touchesBottom -> mutableListOf(
                PointF(base.left, base.top),
                PointF(base.right, base.top),
                PointF(base.right, base.bottom),
                PointF(cut.right, base.bottom),
                PointF(cut.right, cut.top),
                PointF(base.left, cut.top)
            )
            touchesTop -> mutableListOf(
                PointF(base.left, base.top),
                PointF(cut.left, base.top),
                PointF(cut.left, cut.bottom),
                PointF(cut.right, cut.bottom),
                PointF(cut.right, base.top),
                PointF(base.right, base.top),
                PointF(base.right, base.bottom),
                PointF(base.left, base.bottom)
            )
            touchesRight -> mutableListOf(
                PointF(base.left, base.top),
                PointF(base.right, base.top),
                PointF(base.right, cut.top),
                PointF(cut.left, cut.top),
                PointF(cut.left, cut.bottom),
                PointF(base.right, cut.bottom),
                PointF(base.right, base.bottom),
                PointF(base.left, base.bottom)
            )
            touchesBottom -> mutableListOf(
                PointF(base.left, base.top),
                PointF(base.right, base.top),
                PointF(base.right, base.bottom),
                PointF(cut.right, base.bottom),
                PointF(cut.right, cut.top),
                PointF(cut.left, cut.top),
                PointF(cut.left, base.bottom),
                PointF(base.left, base.bottom)
            )
            touchesLeft -> mutableListOf(
                PointF(base.left, base.top),
                PointF(base.right, base.top),
                PointF(base.right, base.bottom),
                PointF(base.left, base.bottom),
                PointF(base.left, cut.bottom),
                PointF(cut.right, cut.bottom),
                PointF(cut.right, cut.top),
                PointF(base.left, cut.top)
            )
            else -> return mutableListOf(rectContour(base), rectContour(cut))
        }

        return mutableListOf(simplifyContour(contour))
    }

    private fun rectContour(rect: RectF): MutableList<PointF> {
        return mutableListOf(
            PointF(rect.left, rect.top),
            PointF(rect.right, rect.top),
            PointF(rect.right, rect.bottom),
            PointF(rect.left, rect.bottom)
        )
    }

    private fun same(a: Float, b: Float): Boolean = abs(a - b) <= 2.5f

    private fun sideCmsForContours(contours: List<List<PointF>>): MutableList<MutableList<Float>> {
        return contours.map { contour ->
            contour.indices.map { i ->
                pxToCm(distancia(contour[i], contour[(i + 1) % contour.size]))
            }.toMutableList()
        }.toMutableList()
    }

    private fun addPointIfDistinct(points: MutableList<PointF>, point: PointF) {
        if (points.lastOrNull()?.let { distancia(it, point) < 1.5f } == true) return
        points.add(point)
    }

    private fun simplifyContour(points: List<PointF>): MutableList<PointF> {
        val contour = points.map { PointF(it.x, it.y) }.toMutableList()
        if (contour.size < 3) return contour
        if (distancia(contour.first(), contour.last()) < 2f) contour.removeAt(contour.lastIndex)

        var changed: Boolean
        do {
            changed = false
            if (contour.size < 3) break
            var i = 0
            while (i < contour.size) {
                val previous = contour[(i - 1 + contour.size) % contour.size]
                val current = contour[i]
                val next = contour[(i + 1) % contour.size]
                if (distancia(previous, current) < 3f || distancia(current, next) < 3f || areCollinear(previous, current, next)) {
                    contour.removeAt(i)
                    changed = true
                } else {
                    i++
                }
            }
        } while (changed)

        snapAlmostOrthogonal(contour)
        return contour
    }

    private fun areCollinear(a: PointF, b: PointF, c: PointF): Boolean {
        val abx = b.x - a.x
        val aby = b.y - a.y
        val bcx = c.x - b.x
        val bcy = c.y - b.y
        val cross = abs((abx * bcy) - (aby * bcx))
        val base = distancia(a, c).coerceAtLeast(1f)
        val dot = (abx * bcx) + (aby * bcy)
        return cross / base < 1.8f && dot >= 0f
    }

    private fun snapAlmostOrthogonal(points: MutableList<PointF>) {
        if (points.size < 2) return
        points.indices.forEach { i ->
            val a = points[i]
            val b = points[(i + 1) % points.size]
            val dx = abs(b.x - a.x)
            val dy = abs(b.y - a.y)
            if (dx < 2.5f && dy > dx) {
                b.x = a.x
            } else if (dy < 2.5f && dx > dy) {
                b.y = a.y
            }
        }
    }

    private fun pathForElement(element: Element): Path {
        return when (element) {
            is Element.Freehand -> Path(element.path)
            is Element.TextLabel -> Path().apply {
                addRect(boundsForText(element), Path.Direction.CW)
            }
            is Element.InfoBox -> Path().apply {
                addRect(element.rect, Path.Direction.CW)
            }
            is Element.Symbol -> Path().apply {
                addRect(element.rect, Path.Direction.CW)
            }
            is Element.Composite -> Path(element.path)
            is Element.Shape -> pathForShape(element)
            is Element.Group -> Path().apply {
                element.children.forEach { child -> addPath(pathForElement(child)) }
            }
        }
    }

    /**
     * Los rótulos de plantilla (identificador, contador) se leen igual de grandes esté el dibujo
     * ampliado o no, como las cotas: creciendo con el zoom taparían el diseño al alejarse y
     * quedarían minúsculos al acercarse. El resto de textos son parte del dibujo y van con él.
     */
    /** El zoom con el que se colocó por última vez la banda de rótulos. */
    private var zoomDeLasBandas = 0f

    /**
     * Lo más grande que puede ser la letra de un rótulo de la banda, en medidas del dibujo.
     *
     * Sale del ancho del apunte: así entran unos cuantos rótulos por fila aunque se aleje mucho.
     */
    private fun techoDeRotulo(): Float {
        val ancho = boundsDelDibujo()?.width() ?: return Float.MAX_VALUE
        return (ancho / 14f).coerceAtLeast(8f)
    }

    /**
     * Recoloca la banda de rótulos de cada plantilla cuando cambia el zoom.
     *
     * Los rótulos de la banda —ángulos, contadores, alféizar— se miden en pantalla para que su
     * letra se vea siempre igual, pero se colocan en el papel: al ampliar o reducir, su letra
     * cambiaba de tamaño y los sitios no, así que se montaban unos encima de otros. Se vuelven a
     * repartir con la escala nueva.
     */
    private fun recolocarBandasSiCambioElZoom() {
        if (abs(viewScale - zoomDeLasBandas) < 0.0005f) return
        zoomDeLasBandas = viewScale
        elementos.indices.filter { esMarcoPlantilla(it) }.forEach { colocarBandaRotulos(it) }
    }

    /**
     * El tamaño de letra más grande que deja ese rótulo dentro de lo que se ve, sin pasar de
     * [tamanoPedido].
     *
     * Lo que se ve es el ancho de la pantalla llevado a las medidas del dibujo, con un margen a
     * cada lado. Al exportar no hay zoom, así que sale el ancho del papel y el identificador cabe
     * igual.
     */
    private fun tamanoQueQuepa(element: Element.TextLabel, tamanoPedido: Float): Float {
        if (element.text.isBlank() || width <= 0 || exportando) return tamanoPedido
        // Lo que hay DESDE DONDE EMPIEZA el rótulo hasta el borde derecho de la pantalla: el
        // identificador arranca en el canto del dibujo, no en el borde, así que medirlo contra el
        // ancho de la pantalla no servía —con el apunte a la derecha se salía igual—.
        val margen = 8f * escalaCota
        val disponible = pantallaAMundoX(width.toFloat()) - element.x - margen
        if (disponible <= 1f) return tamanoPedido
        sketchTextPaint.textSize = tamanoPedido
        val ancho = sketchTextPaint.measureText(element.text)
        if (ancho <= disponible || ancho <= 0f) return tamanoPedido
        return tamanoPedido * disponible / ancho
    }

    /** Mientras se exporta no hay pantalla que respetar: el rótulo va a su tamaño. */
    private var exportando = false

    private fun tamanoTexto(element: Element.TextLabel): Float = when {
        // El identificador se recorta a lo que hay de ancho: con la letra medida en pantalla, en un
        // apunte reducido acababa más largo que el papel y se salía por los lados.
        element.titulo -> tamanoQueQuepa(element, ce(maxOf(element.textSize, 50f)))
        // Van fuera del dibujo, en su banda: se miden en pantalla como las cotas, para que la banda
        // no se descuadre al ampliar ni al reducir, pero con un techo en medidas del dibujo. Sin él,
        // al alejar la letra crecía tanto respecto al apunte que ninguna fila cabía y todos los
        // rótulos acababan unos sobre otros.
        element.rol in setOf(ROL_ESQUINA, ROL_ALTOS, ROL_ALFEIZAR, ROL_CURVA) ->
            minOf(ce(maxOf(element.textSize, 44f)), techoDeRotulo())
        element.rol != null -> maxOf(element.textSize, 44f)
        else -> element.textSize
    }

    private fun boundsForText(element: Element.TextLabel): RectF {
        val tamano = tamanoTexto(element)
        sketchTextPaint.textSize = tamano
        val width = sketchTextPaint.measureText(element.text).coerceAtLeast(1f)
        val top = element.y - tamano
        val bottom = element.y + tamano * 0.28f
        return RectF(element.x, top, element.x + width, bottom)
    }

    private fun boundsForElement(element: Element): RectF {
        val bounds = RectF()
        pathForElement(element).computeBounds(bounds, true)
        return bounds
    }

    /**
     * Vuelve a colocar los títulos de plantilla encima del dibujo, a una distancia que deja libre
     * la banda de las cotas.
     *
     * El título nace al traer una medida del presupuesto o de una plantilla y antes se quedaba
     * clavado donde apareció: en cuanto se corregían las medidas, el dibujo crecía y se le montaba
     * encima. Ahora sigue al dibujo mientras el usuario no lo mueva a mano.
     */
    private fun reubicarTitulos() {
        val titulos = elementos.filterIsInstance<Element.TextLabel>().filter { it.titulo && !it.movida }
        if (titulos.isEmpty()) return
        val bounds = boundsDelDibujo() ?: return
        // Rótulo y contador se miden en pantalla, igual que su letra: así la separación se ve
        // siempre la misma, sin alejarse del dibujo al reducir ni montársele al ampliar. Por encima
        // del dibujo pasa la cota de arriba, y la banda le deja sitio.
        val separacion = maxOf(ce(76f), cmToPx(3f))
        // Se apilan de abajo hacia arriba: los contadores pegados al dibujo y el identificador
        // arriba del todo, cada uno en su renglón.
        var y = bounds.top - separacion
        titulos
            .sortedBy { ORDEN_ROTULOS.indexOf(it.rol).takeIf { pos -> pos >= 0 } ?: ORDEN_ROTULOS.size }
            .reversed()
            .forEach { titulo ->
                titulo.x = bounds.left
                titulo.y = y
                y -= tamanoTexto(titulo) * 1.45f
            }
    }

    /** Lo que ocupa el dibujo sin contar los textos: es a lo que se ancla el título. */
    private fun boundsDelDibujo(): RectF? {
        var union: RectF? = null
        for (element in elementos) {
            if (element is Element.TextLabel) continue
            val bounds = boundsForElement(element)
            if (union == null) union = RectF(bounds) else union.union(bounds)
        }
        return union
    }

    private fun boundsForContent(): RectF? {
        var union: RectF? = null
        for (element in elementos) {
            val bounds = boundsForElement(element)
            if (union == null) {
                union = RectF(bounds)
            } else {
                union.union(bounds)
            }
        }
        // La planta de una ventana de esquina no es un elemento —se dibuja desde la alzada— pero
        // ocupa papel: sin contarla, el apunte exportado la dejaba cortada.
        elementos.indices.filter { esMarcoEsquina(it) }.forEach { marcoIndex ->
            recorridoPlanta(marcoIndex).forEach { p ->
                val suyo = RectF(p.x, p.y, p.x, p.y).apply { inset(-cmToPx(12f), -cmToPx(12f)) }
                if (union == null) union = RectF(suyo) else union!!.union(suyo)
            }
        }
        return union
    }

    private fun translateElement(index: Int, dx: Float, dy: Float) {
        val element = elementos.getOrNull(index) ?: return
        translateElement(element, dx, dy)
    }

    private fun translateElement(element: Element, dx: Float, dy: Float) {
        when (element) {
            is Element.Freehand -> element.path.transform(Matrix().apply { setTranslate(dx, dy) })
            is Element.TextLabel -> {
                element.x += dx
                element.y += dy
            }
            is Element.InfoBox -> element.rect.offset(dx, dy)
            is Element.Symbol -> element.rect.offset(dx, dy)
            is Element.Composite -> {
                element.path.transform(Matrix().apply { setTranslate(dx, dy) })
                element.contours.flatten().forEach { it.offset(dx, dy) }
            }
            is Element.Shape -> {
                element.rect.offset(dx, dy)
                element.start.offset(dx, dy)
                element.end.offset(dx, dy)
                element.topLeft.offset(dx, dy)
                element.topRight.offset(dx, dy)
                element.bottomRight.offset(dx, dy)
                element.bottomLeft.offset(dx, dy)
            }
            is Element.Group -> element.children.forEach { child -> translateElement(child, dx, dy) }
        }
    }

    private fun scaleElement(element: Element, factor: Float, pivot: PointF) {
        val matrix = Matrix().apply { setScale(factor, factor, pivot.x, pivot.y) }
        when (element) {
            is Element.Freehand -> element.path.transform(matrix)
            is Element.TextLabel -> {
                scalePoint(element.x, element.y, factor, pivot).also {
                    element.x = it.x
                    element.y = it.y
                }
                element.textSize *= factor
            }
            is Element.InfoBox -> {
                matrix.mapRect(element.rect)
                element.textSize *= factor
            }
            is Element.Symbol -> matrix.mapRect(element.rect)
            is Element.Composite -> {
                element.path.transform(matrix)
                transformCompositePoints(element, matrix)
                rebuildCompositePath(element)
                refreshCompositeSides(element)
                val bounds = boundsForElement(element)
                element.widthCm = pxToCm(bounds.width())
                element.heightCm = pxToCm(bounds.height())
            }
            is Element.Shape -> {
                transformPoint(element.start, matrix)
                transformPoint(element.end, matrix)
                transformPoint(element.topLeft, matrix)
                transformPoint(element.topRight, matrix)
                transformPoint(element.bottomRight, matrix)
                transformPoint(element.bottomLeft, matrix)
                actualizarShapeDesdePuntos(element)
            }
            is Element.Group -> element.children.forEach { child -> scaleElement(child, factor, pivot) }
        }
    }

    private fun rotateElement(element: Element, degrees: Float, pivot: PointF) {
        val matrix = Matrix().apply { setRotate(degrees, pivot.x, pivot.y) }
        when (element) {
            is Element.Freehand -> element.path.transform(matrix)
            is Element.TextLabel -> {
                val point = PointF(element.x, element.y)
                transformPoint(point, matrix)
                element.x = point.x
                element.y = point.y
            }
            is Element.InfoBox -> matrix.mapRect(element.rect)
            is Element.Symbol -> matrix.mapRect(element.rect)
            is Element.Composite -> {
                element.path.transform(matrix)
                transformCompositePoints(element, matrix)
                rebuildCompositePath(element)
                refreshCompositeSides(element)
                val bounds = boundsForElement(element)
                element.widthCm = pxToCm(bounds.width())
                element.heightCm = pxToCm(bounds.height())
                element.rotationDeg = ((element.rotationDeg + degrees) % 360f + 360f) % 360f
            }
            is Element.Shape -> {
                transformPoint(element.start, matrix)
                transformPoint(element.end, matrix)
                transformPoint(element.topLeft, matrix)
                transformPoint(element.topRight, matrix)
                transformPoint(element.bottomRight, matrix)
                transformPoint(element.bottomLeft, matrix)
                actualizarShapeDesdePuntos(element)
            }
            is Element.Group -> element.children.forEach { child -> rotateElement(child, degrees, pivot) }
        }
    }

    private fun scalePoint(x: Float, y: Float, factor: Float, pivot: PointF): PointF {
        return PointF(
            pivot.x + (x - pivot.x) * factor,
            pivot.y + (y - pivot.y) * factor
        )
    }

    private fun transformPoint(point: PointF, matrix: Matrix) {
        val values = floatArrayOf(point.x, point.y)
        matrix.mapPoints(values)
        point.x = values[0]
        point.y = values[1]
    }

    private fun actualizarShapeDesdePuntos(shape: Element.Shape) {
        shape.rect.set(
            minOf(shape.start.x, shape.end.x, shape.topLeft.x, shape.topRight.x, shape.bottomRight.x, shape.bottomLeft.x),
            minOf(shape.start.y, shape.end.y, shape.topLeft.y, shape.topRight.y, shape.bottomRight.y, shape.bottomLeft.y),
            maxOf(shape.start.x, shape.end.x, shape.topLeft.x, shape.topRight.x, shape.bottomRight.x, shape.bottomLeft.x),
            maxOf(shape.start.y, shape.end.y, shape.topLeft.y, shape.topRight.y, shape.bottomRight.y, shape.bottomLeft.y)
        )
        shape.widthCm = pxToCm(shape.rect.width())
        shape.heightCm = pxToCm(shape.rect.height())
        shape.diameterCm = pxToCm(maxOf(shape.rect.width(), shape.rect.height()))
        shape.lengthCm = pxToCm(distancia(shape.start, shape.end))
        shape.topCm = pxToCm(distancia(shape.topLeft, shape.topRight))
        shape.rightCm = pxToCm(distancia(shape.topRight, shape.bottomRight))
        shape.bottomCm = pxToCm(distancia(shape.bottomLeft, shape.bottomRight))
        shape.leftCm = pxToCm(distancia(shape.topLeft, shape.bottomLeft))
    }

    private fun cloneElement(element: Element): Element {
        return when (element) {
            is Element.Freehand -> Element.Freehand(Path(element.path))
            is Element.TextLabel -> Element.TextLabel(
                text = element.text,
                x = element.x,
                y = element.y,
                textSize = element.textSize,
                titulo = element.titulo,
                movida = element.movida
            )
            is Element.InfoBox -> Element.InfoBox(
                text = element.text,
                rect = RectF(element.rect),
                textSize = element.textSize
            )
            is Element.Symbol -> Element.Symbol(
                drawableName = element.drawableName,
                rect = RectF(element.rect),
                reflejado = element.reflejado
            )
            is Element.Composite -> Element.Composite(
                path = Path(element.path),
                widthCm = element.widthCm,
                heightCm = element.heightCm,
                contours = element.contours.map { contour ->
                    contour.map { PointF(it.x, it.y) }.toMutableList()
                }.toMutableList(),
                sideCms = element.sideCms.map { it.toMutableList() }.toMutableList(),
                template = element.template,
                rotationDeg = element.rotationDeg,
                reflejado = element.reflejado
            )
            is Element.Shape -> Element.Shape(
                tool = element.tool,
                rect = RectF(element.rect),
                start = PointF(element.start.x, element.start.y),
                end = PointF(element.end.x, element.end.y),
                widthCm = element.widthCm,
                heightCm = element.heightCm,
                diameterCm = element.diameterCm,
                lengthCm = element.lengthCm,
                topLeft = PointF(element.topLeft.x, element.topLeft.y),
                topRight = PointF(element.topRight.x, element.topRight.y),
                bottomRight = PointF(element.bottomRight.x, element.bottomRight.y),
                bottomLeft = PointF(element.bottomLeft.x, element.bottomLeft.y),
                topCm = element.topCm,
                rightCm = element.rightCm,
                bottomCm = element.bottomCm,
                leftCm = element.leftCm,
                cotaHint = element.cotaHint,
                largoFijado = element.largoFijado
            )
            is Element.Group -> Element.Group(element.children.map { cloneElement(it) }.toMutableList())
        }
    }

    private fun mirrorElementHorizontally(element: Element, axisX: Float) {
        val matrix = Matrix().apply { setScale(-1f, 1f, axisX, 0f) }
        when (element) {
            is Element.Freehand -> element.path.transform(matrix)
            is Element.TextLabel -> {
                val bounds = boundsForText(element)
                element.x = axisX - (bounds.right - axisX)
            }
            is Element.InfoBox -> {
                val oldLeft = element.rect.left
                val oldRight = element.rect.right
                element.rect.left = axisX - (oldRight - axisX)
                element.rect.right = axisX - (oldLeft - axisX)
            }
            is Element.Symbol -> {
                val oldLeft = element.rect.left
                val oldRight = element.rect.right
                element.rect.left = axisX - (oldRight - axisX)
                element.rect.right = axisX - (oldLeft - axisX)
            }
            is Element.Composite -> {
                element.path.transform(matrix)
                transformCompositePoints(element, matrix)
                element.contours.forEach { it.reverse() }
                // Reflejar invierte el sentido de la rotación: así el ángulo guardado sigue siendo
                // el que hay que des-rotar para normalizar (importa al combinar rotar + reflejar).
                element.rotationDeg = ((360f - element.rotationDeg) % 360f + 360f) % 360f
                element.reflejado = !element.reflejado
                rebuildCompositePath(element)
                refreshCompositeSides(element)
            }
            is Element.Shape -> {
                mirrorPoint(element.start, axisX)
                mirrorPoint(element.end, axisX)
                mirrorPoint(element.topLeft, axisX)
                mirrorPoint(element.topRight, axisX)
                mirrorPoint(element.bottomRight, axisX)
                mirrorPoint(element.bottomLeft, axisX)
                val oldTopLeft = PointF(element.topLeft.x, element.topLeft.y)
                val oldBottomLeft = PointF(element.bottomLeft.x, element.bottomLeft.y)
                element.topLeft.x = element.topRight.x.also { element.topRight.x = oldTopLeft.x }
                element.topLeft.y = element.topRight.y.also { element.topRight.y = oldTopLeft.y }
                element.bottomLeft.x = element.bottomRight.x.also { element.bottomRight.x = oldBottomLeft.x }
                element.bottomLeft.y = element.bottomRight.y.also { element.bottomRight.y = oldBottomLeft.y }
                element.rect.set(
                    minOf(element.topLeft.x, element.topRight.x, element.bottomRight.x, element.bottomLeft.x),
                    minOf(element.topLeft.y, element.topRight.y, element.bottomRight.y, element.bottomLeft.y),
                    maxOf(element.topLeft.x, element.topRight.x, element.bottomRight.x, element.bottomLeft.x),
                    maxOf(element.topLeft.y, element.topRight.y, element.bottomRight.y, element.bottomLeft.y)
                )
            }
            is Element.Group -> element.children.forEach { child -> mirrorElementHorizontally(child, axisX) }
        }
    }

    private fun mirrorPoint(point: PointF, axisX: Float) {
        point.x = axisX - (point.x - axisX)
    }

    /** La arista de esquina: dos líneas juntas, que es como se marca un quiebre en un plano. */
    private fun pathQuiebre(shape: Element.Shape): Path {
        val separacion = 3f * resources.displayMetrics.density
        return Path().apply {
            moveTo(shape.start.x - separacion, shape.start.y)
            lineTo(shape.end.x - separacion, shape.end.y)
            moveTo(shape.start.x + separacion, shape.start.y)
            lineTo(shape.end.x + separacion, shape.end.y)
        }
    }

    /**
     * Los tres vértices del triángulo: la punta (medio del lado de arriba) y los dos de la base.
     *
     * Salen de las cuatro esquinas guardadas, que son las que giran, se escalan y se reflejan con
     * la figura; la caja envolvente vuelve a quedar recta en cada giro y no sirve para dibujarlo.
     */
    private fun verticesTriangulo(shape: Element.Shape): Triple<PointF, PointF, PointF> {
        val punta = PointF(
            (shape.topLeft.x + shape.topRight.x) / 2f,
            (shape.topLeft.y + shape.topRight.y) / 2f
        )
        return Triple(punta, PointF(shape.bottomRight), PointF(shape.bottomLeft))
    }

    /** El pie de la altura: dónde cae la punta sobre la línea de la base. */
    private fun pieAlturaTriangulo(shape: Element.Shape): PointF {
        val (punta, der, izq) = verticesTriangulo(shape)
        val dx = der.x - izq.x
        val dy = der.y - izq.y
        val largo2 = dx * dx + dy * dy
        if (largo2 < 0.25f) return PointF(izq.x, izq.y)
        val t = ((punta.x - izq.x) * dx + (punta.y - izq.y) * dy) / largo2
        return PointF(izq.x + dx * t, izq.y + dy * t)
    }

    private fun baseTrianguloPx(shape: Element.Shape): Float {
        val (_, der, izq) = verticesTriangulo(shape)
        return distancia(izq, der)
    }

    private fun alturaTrianguloPx(shape: Element.Shape): Float {
        val (punta, _, _) = verticesTriangulo(shape)
        return distancia(pieAlturaTriangulo(shape), punta)
    }

    /**
     * Deja el triángulo con esos tres vértices.
     *
     * La punta se guarda en las DOS esquinas de arriba (quedan en el mismo punto), que es como
     * [verticesTriangulo] la lee: así la figura sigue girando, escalándose y reflejándose con el
     * mismo código de siempre, sin un tipo de elemento nuevo.
     */
    private fun fijarVerticesTriangulo(shape: Element.Shape, punta: PointF, der: PointF, izq: PointF) {
        shape.topLeft.set(punta.x, punta.y)
        shape.topRight.set(punta.x, punta.y)
        shape.bottomRight.set(der.x, der.y)
        shape.bottomLeft.set(izq.x, izq.y)
        shape.start.set(minOf(punta.x, der.x, izq.x), minOf(punta.y, der.y, izq.y))
        shape.end.set(maxOf(punta.x, der.x, izq.x), maxOf(punta.y, der.y, izq.y))
        actualizarShapeDesdePuntos(shape)
    }

    /** La base se estira sobre sí misma, desde su punto medio; la punta se queda donde está. */
    private fun aplicarBaseTriangulo(shape: Element.Shape, valueCm: Float) {
        val (punta, der, izq) = verticesTriangulo(shape)
        val factor = factorEstirado(distancia(izq, der), valueCm)
        val mx = (izq.x + der.x) / 2f
        val my = (izq.y + der.y) / 2f
        fijarVerticesTriangulo(
            shape,
            punta,
            PointF(mx + (der.x - mx) * factor, my + (der.y - my) * factor),
            PointF(mx + (izq.x - mx) * factor, my + (izq.y - my) * factor)
        )
    }

    /** La altura mueve la punta por su perpendicular; la base no se toca. */
    private fun aplicarAlturaTriangulo(shape: Element.Shape, valueCm: Float) {
        val (punta, der, izq) = verticesTriangulo(shape)
        val pie = pieAlturaTriangulo(shape)
        var nx = punta.x - pie.x
        var ny = punta.y - pie.y
        val largo = hypot(nx.toDouble(), ny.toDouble()).toFloat()
        if (largo < 0.5f) {
            // Triángulo aplastado: no hay altura de la que sacar dirección, se usa la perpendicular
            // a la base.
            val dx = der.x - izq.x
            val dy = der.y - izq.y
            val d = hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(1f)
            nx = -dy / d
            ny = dx / d
        } else {
            nx /= largo
            ny /= largo
        }
        val alto = cmToPx(abs(valueCm))
        fijarVerticesTriangulo(shape, PointF(pie.x + nx * alto, pie.y + ny * alto), der, izq)
    }

    private fun pathForShape(shape: Element.Shape): Path {
        return Path().apply {
            when (shape.tool) {
                Tool.RECTANGLE -> {
                    moveTo(shape.topLeft.x, shape.topLeft.y)
                    lineTo(shape.topRight.x, shape.topRight.y)
                    lineTo(shape.bottomRight.x, shape.bottomRight.y)
                    lineTo(shape.bottomLeft.x, shape.bottomLeft.y)
                    close()
                }
                Tool.TRIANGLE -> {
                    // Se dibuja por sus VÉRTICES, no por la caja: girándolo, la caja vuelve a
                    // quedar recta y el triángulo se redibujaba igual que antes —solo cambiaban
                    // los números—. Los vértices sí giran, así que la punta va donde toca.
                    val (punta, derecha, izquierda) = verticesTriangulo(shape)
                    moveTo(punta.x, punta.y)
                    lineTo(derecha.x, derecha.y)
                    lineTo(izquierda.x, izquierda.y)
                    close()
                }
                Tool.CIRCLE -> addOval(shape.rect, Path.Direction.CW)
                Tool.LINE, Tool.ORTHO_LINE -> {
                    moveTo(shape.start.x, shape.start.y)
                    lineTo(shape.end.x, shape.end.y)
                }
                Tool.NONE, Tool.FREEHAND, Tool.MAGNET_PEN, Tool.TEXT, Tool.SELECT, Tool.NODO -> Unit
            }
        }
    }

    private fun drawCotas(canvas: Canvas, index: Int, shape: Element.Shape, collectHits: Boolean) {
        // En la puerta las bisagras van montadas sobre el canto: las cotas de los lados se apartan
        // por fuera de ellas para que la línea y el número no queden cortados.
        val apartar = if (shape.cotaHint == PUERTA_MARCO) hingeSize() * 0.8f else 0f
        // Con dos hojas, debajo del marco van primero los anchos de cada hoja: el total se aparta
        // un renglón más, que es como se acota un vano partido.
        val partido = (shape.cotaHint == PUERTA_MARCO && divisionDePuerta(index) != null) ||
            (shape.cotaHint == ESQUINA_MARCO && quiebresDelMarco(index).isNotEmpty())
        val apartarAbajo = if (partido) ce(40f) else 0f
        when (shape.tool) {
            Tool.RECTANGLE -> {
                // Cada cota va PARALELA a su lado. En cuanto los dos costados miden distinto, el
                // lado de arriba queda inclinado y su medida real ya no es la separación horizontal
                // entre esquinas: dibujarla en paralelo es lo que deja leerla y escribirla.
                val centro = PointF(
                    (shape.topLeft.x + shape.topRight.x + shape.bottomRight.x + shape.bottomLeft.x) / 4f,
                    (shape.topLeft.y + shape.topRight.y + shape.bottomRight.y + shape.bottomLeft.y) / 4f
                )
                // En la esquina, el lado de arriba de cada tramo es una pieza distinta —va de una
                // arista a la otra y con los altos desiguales queda inclinado—, así que cada uno
                // lleva su cota en vez de una sola de canto a canto, que ahí no mediría nada real.
                val arriba = if (shape.cotaHint == ESQUINA_MARCO) puntosArriba(index) else emptyList()
                if (arriba.size > 2) {
                    for (tramo in 0 until arriba.size - 1) {
                        // Un lado de cero no lleva cota: no hay pared que medir, la ventana
                        // empieza o acaba en su curva. En la planta sí se le deja la suya, que
                        // es por donde se le vuelve a dar medida.
                        if (arriba[tramo + 1].x - arriba[tramo].x < cmToPx(0.5f)) continue
                        drawCotaLado(
                            canvas, index, CotaType.ESQUINA_TRAMO_ARRIBA,
                            arriba[tramo], arriba[tramo + 1], centro,
                            pxToCm(distancia(arriba[tramo], arriba[tramo + 1])), collectHits,
                            sideIndex = tramo
                        )
                    }
                } else {
                    drawCotaLado(canvas, index, CotaType.RECT_TOP, shape.topLeft, shape.topRight, centro, shape.topCm, collectHits)
                }
                drawCotaLado(canvas, index, CotaType.RECT_RIGHT, shape.topRight, shape.bottomRight, centro, shape.rightCm, collectHits, apartar)
                drawCotaLado(canvas, index, CotaType.RECT_BOTTOM, shape.bottomLeft, shape.bottomRight, centro, shape.bottomCm, collectHits, apartarAbajo)
                drawCotaLado(canvas, index, CotaType.RECT_LEFT, shape.topLeft, shape.bottomLeft, centro, shape.leftCm, collectHits, apartar)
                // Ventana de esquina: cada tramo lleva su ancho al pie, y el total va un renglón
                // más abajo. Cada tramo se mide contra su pared, así que el total es su suma.
                if (shape.cotaHint == ESQUINA_MARCO) {
                    val bordes = bordesDeTramos(index)
                    for (tramo in 0 until bordes.size - 1) {
                        // Igual al pie: el lado de cero no se acota aquí.
                        if (bordes[tramo + 1] - bordes[tramo] < cmToPx(0.5f)) continue
                        drawCotaLado(
                            canvas, index, CotaType.ESQUINA_TRAMO,
                            PointF(bordes[tramo], shape.rect.bottom),
                            PointF(bordes[tramo + 1], shape.rect.bottom),
                            centro, pxToCm(bordes[tramo + 1] - bordes[tramo]), collectHits,
                            sideIndex = tramo
                        )
                    }
                }
            }
            Tool.TRIANGLE -> {
                // Cada cota va con SU lado: la base pegada a la base y la altura sobre la altura,
                // giren como giren. Antes iban por la caja envolvente, así que al girar el
                // triángulo la base quedaba dibujada del lado de la punta.
                val (punta, der, izq) = verticesTriangulo(shape)
                val centro = PointF((punta.x + der.x + izq.x) / 3f, (punta.y + der.y + izq.y) / 3f)
                drawCotaLado(
                    canvas, index, CotaType.WIDTH, izq, der, centro,
                    pxToCm(baseTrianguloPx(shape)), collectHits
                )
                drawCotaLado(
                    canvas, index, CotaType.HEIGHT, pieAlturaTriangulo(shape), punta, der,
                    pxToCm(alturaTrianguloPx(shape)), collectHits
                )
            }
            Tool.CIRCLE -> {
                drawHorizontalCota(canvas, index, CotaType.DIAMETER, shape.rect.left, shape.rect.right, shape.rect.centerY(), shape.diameterCm, collectHits)
            }
            Tool.LINE, Tool.ORTHO_LINE -> {
                if (shape.cotaHint == "GRADA_TOTAL_COTA") {
                    drawCotaTotalGraderia(canvas, index, shape, collectHits)
                    return
                }
                if (shape.cotaHint == PUERTA_PUENTE) {
                    drawCotasPuente(canvas, index, shape, collectHits)
                    return
                }
                if (shape.cotaHint == PUERTA_DIVISION) {
                    drawCotasHojas(canvas, index, shape, collectHits)
                    return
                }
                if (shape.cotaHint == ESQUINA_QUIEBRE) return
                if (shape.cotaHint == VENTANA_ALTO || shape.cotaHint == VENTANA_ALTO_ESQUINA) {
                    // En una ventana de esquina el número vive en la planta, sobre el punto donde
                    // se midió: aquí queda la marca, para ver por dónde pasa, y el desarrollo deja
                    // de ser una fila de números que no se sabe de qué tramo son.
                    if (altoDeVentanaEsquina(index)) {
                        canvas.drawLine(
                            shape.start.x, shape.start.y, shape.end.x, shape.end.y, cotaGuiaPaint
                        )
                        return
                    }
                    // Va por dentro del vano, en su sitio: es el alto de ESE punto.
                    drawVerticalCota(
                        canvas = canvas,
                        index = index,
                        type = CotaType.LENGTH,
                        xBase = shape.start.x,
                        top = minOf(shape.start.y, shape.end.y),
                        bottom = maxOf(shape.start.y, shape.end.y),
                        value = shape.lengthCm,
                        collectHits = collectHits,
                        preferOutsideRight = true
                    )
                    return
                }
                val midX = (shape.start.x + shape.end.x) / 2f
                val midY = (shape.start.y + shape.end.y) / 2f
                canvas.drawLine(shape.start.x, shape.start.y, shape.end.x, shape.end.y, cotaLinePaint)
                val offset = ce(18f)
                val (labelX, labelY) = when (shape.cotaHint) {
                    "GRADA_PASO" -> midX to (midY + offset)
                    "GRADA_CONTRAPASO" -> (minOf(shape.start.x, shape.end.x) - offset) to midY
                    else -> midX to (midY - offset)
                }
                if (shape.cotaHint == "GRADA_PASO" || shape.cotaHint == "GRADA_CONTRAPASO") {
                    drawCotaTextFija(canvas, index, CotaType.LENGTH, labelX, labelY, shape.lengthCm, collectHits)
                } else {
                    drawCotaText(canvas, index, CotaType.LENGTH, labelX, labelY, shape.lengthCm, collectHits)
                }
            }
            Tool.NONE, Tool.FREEHAND, Tool.MAGNET_PEN, Tool.TEXT, Tool.SELECT, Tool.NODO -> Unit
        }
    }

    /**
     * El puente lleva sus dos medidas: lo que mide de canto a canto y a qué altura del piso va.
     * Con esas dos se coloca solo, sin tener que arrastrar nada.
     */
    private fun drawCotasPuente(canvas: Canvas, index: Int, puente: Element.Shape, collectHits: Boolean) {
        val left = minOf(puente.start.x, puente.end.x)
        val right = maxOf(puente.start.x, puente.end.x)
        val y = puente.start.y
        drawCotaText(canvas, index, CotaType.LENGTH, (left + right) / 2f, y - ce(18f), puente.lengthCm, collectHits)
        val marco = (elementos.getOrNull(marcoDePieza(index) ?: -1) as? Element.Shape) ?: return
        val alturaCm = pxToCm(marco.rect.bottom - y)
        if (alturaCm <= 0f) return
        drawVerticalCota(
            canvas = canvas,
            index = index,
            type = CotaType.PUERTA_ALTURA,
            xBase = left + (right - left) * 0.3f,
            top = y,
            bottom = marco.rect.bottom,
            value = alturaCm,
            collectHits = collectHits,
            preferOutsideRight = true
        )
    }

    /**
     * Los anchos de las dos hojas, pegados al pie del marco. Lo que se le escribe a una se lo quita
     * a la otra: el vano total no lo tocan.
     */
    private fun drawCotasHojas(canvas: Canvas, index: Int, division: Element.Shape, collectHits: Boolean) {
        val marco = (elementos.getOrNull(marcoDePieza(index) ?: -1) as? Element.Shape)?.rect ?: return
        val centro = PointF(marco.centerX(), marco.centerY())
        val x = division.start.x
        drawCotaLado(
            canvas, index, CotaType.PUERTA_HOJA_IZQ,
            PointF(marco.left, marco.bottom), PointF(x, marco.bottom),
            centro, pxToCm(x - marco.left), collectHits
        )
        drawCotaLado(
            canvas, index, CotaType.PUERTA_HOJA_DER,
            PointF(x, marco.bottom), PointF(marco.right, marco.bottom),
            centro, pxToCm(marco.right - x), collectHits
        )
    }

    private fun drawCotaTotalGraderia(canvas: Canvas, index: Int, shape: Element.Shape, collectHits: Boolean) {
        val dx = shape.end.x - shape.start.x
        val dy = shape.end.y - shape.start.y
        val len = hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(1f)
        var nx = -dy / len
        var ny = dx / len
        if (ny < 0f) {
            nx = -nx
            ny = -ny
        }
        val offset = ce(42f)
        val ax = shape.start.x + nx * offset
        val ay = shape.start.y + ny * offset
        val bx = shape.end.x + nx * offset
        val by = shape.end.y + ny * offset
        canvas.drawLine(ax, ay, bx, by, cotaLinePaint)
        canvas.drawLine(ax - nx * ce(8f), ay - ny * ce(8f), ax + nx * ce(8f), ay + ny * ce(8f), cotaLinePaint)
        canvas.drawLine(bx - nx * ce(8f), by - ny * ce(8f), bx + nx * ce(8f), by + ny * ce(8f), cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.LENGTH,
            cx = (ax + bx) / 2f,
            cy = (ay + by) / 2f + ce(14f),
            value = shape.lengthCm,
            collectHits = collectHits
        )
    }

    private fun drawCotaTextFija(
        canvas: Canvas,
        index: Int,
        type: CotaType,
        cx: Float,
        cy: Float,
        value: Float,
        collectHits: Boolean
    ) {
        val text = formatCm(value)
        val widthText = cotaTextPaint.measureText(text)
        val fontMetrics = cotaTextPaint.fontMetrics
        val padH = ce(4f * resources.displayMetrics.density)
        val padV = ce(2f * resources.displayMetrics.density)
        val textHeight = fontMetrics.descent - fontMetrics.ascent
        val hit = RectF(
            cx - widthText / 2f - padH,
            cy - textHeight / 2f - padV,
            cx + widthText / 2f + padH,
            cy + textHeight / 2f + padV
        )
        val drawCx = hit.centerX()
        val drawCy = hit.centerY() - (fontMetrics.ascent + fontMetrics.descent) / 2f
        canvas.drawRoundRect(hit, ce(6f), ce(6f), cotaBgPaint)
        canvas.drawText(text, drawCx, drawCy, cotaTextPaint)
        cotaTextRects.add(RectF(hit))
        if (collectHits) cotaHits.add(CotaHit(index, type, hit))
    }

    private fun drawCompositeCotas(canvas: Canvas, index: Int, composite: Element.Composite, collectHits: Boolean) {
        if (composite.template == TEMPLATE_F5) {
            drawF5Cotas(canvas, index, composite, collectHits)
            return
        }
        if (composite.template == TEMPLATE_F6) {
            drawF6Cotas(canvas, index, composite, collectHits)
            return
        }
        if (composite.template == TEMPLATE_ROUNDED) {
            drawRoundedCornerCotas(canvas, index, composite, collectHits)
            return
        }
        val bounds = boundsForElement(composite)
        composite.contours.forEachIndexed { contourIndex, contour ->
            if (contour.size < 2) return@forEachIndexed
            contour.indices.forEach { sideIndex ->
                val a = contour[sideIndex]
                val b = contour[(sideIndex + 1) % contour.size]
                // Manda lo que el usuario escribió: mientras falten medidas la figura no se ha
                // rehecho todavía, pero la cota ya debe decir lo que él midió.
                val declarado = composite.declarados[ladoKey(contourIndex, sideIndex)]
                val value = declarado
                    ?: composite.sideCms
                        .getOrNull(contourIndex)
                        ?.getOrNull(sideIndex)
                    ?: pxToCm(distancia(a, b))
                drawCompositeSideCota(
                    canvas = canvas,
                    index = index,
                    contourIndex = contourIndex,
                    sideIndex = sideIndex,
                    a = a,
                    b = b,
                    bounds = bounds,
                    value = value,
                    collectHits = collectHits,
                    pendiente = declarado != null && !medidasCompletas(composite)
                )
            }
        }
        if (composite.template == TEMPLATE_F4) {
            drawF4CotaTotalInterior(canvas, index, composite, collectHits)
        }
    }

    private fun drawF5Cotas(canvas: Canvas, index: Int, composite: Element.Composite, collectHits: Boolean) {
        val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val ellipseH = dims.flecha * 2f
        val topArc = RectF(
            dims.left,
            dims.top - ellipseH / 2f - ce(24f),
            dims.left + dims.rectW,
            dims.top + ellipseH / 2f - ce(24f)
        )
        canvas.drawArc(topArc, 180f, 180f, false, cotaLinePaint)
        canvas.drawLine(dims.left, dims.top - ce(8f), dims.left, dims.top - ce(32f), cotaLinePaint)
        canvas.drawLine(dims.left + dims.rectW, dims.top - ce(8f), dims.left + dims.rectW, dims.top - ce(32f), cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.F5_DESARROLLO,
            cx = dims.left + dims.rectW / 2f,
            cy = topArc.top - ce(8f),
            value = pxToCm(longitudMediaElipsePx(dims.rectW, dims.flecha)),
            collectHits = collectHits
        )

        drawHorizontalCota(
            canvas = canvas,
            index = index,
            type = CotaType.WIDTH,
            left = dims.left,
            right = dims.left + dims.rectW,
            yBase = dims.top + dims.rectH,
            value = pxToCm(dims.rectW),
            collectHits = collectHits,
            preferOutsideAbove = false
        )

        val centerX = dims.left + dims.rectW / 2f
        val chordY = dims.top + dims.rectH
        val arcCenterY = chordY - dims.flecha
        canvas.drawLine(centerX, chordY, centerX, arcCenterY, cotaLinePaint)
        canvas.drawLine(centerX - ce(8f), chordY, centerX + ce(8f), chordY, cotaLinePaint)
        canvas.drawLine(centerX - ce(8f), arcCenterY, centerX + ce(8f), arcCenterY, cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.F5_FLECHA,
            cx = centerX + ce(28f),
            cy = (chordY + arcCenterY) / 2f,
            value = pxToCm(dims.flecha),
            collectHits = collectHits,
            angleDegrees = -90f
        )

        drawVerticalCota(
            canvas = canvas,
            index = index,
            type = CotaType.HEIGHT,
            xBase = dims.left,
            top = dims.top,
            bottom = dims.top + dims.rectH,
            value = pxToCm(dims.rectH),
            collectHits = collectHits,
            preferOutsideRight = false
        )
    }

    private fun drawRoundedCornerCotas(canvas: Canvas, index: Int, composite: Element.Composite, collectHits: Boolean) {
        val dims = dimsRoundedCorners(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        drawHorizontalCota(
            canvas = canvas,
            index = index,
            type = CotaType.WIDTH,
            left = dims.left,
            right = dims.left + dims.rectW,
            yBase = dims.top + dims.rectH,
            value = pxToCm(dims.rectW),
            collectHits = collectHits,
            preferOutsideAbove = false
        )
        drawVerticalCota(
            canvas = canvas,
            index = index,
            type = CotaType.HEIGHT,
            xBase = dims.left,
            top = dims.top,
            bottom = dims.top + dims.rectH,
            value = pxToCm(dims.rectH),
            collectHits = collectHits,
            preferOutsideRight = false
        )
        dims.radii.forEachIndexed { corner, radius ->
            if (radius > 0f) drawRoundedCornerRadiusCota(canvas, index, corner, dims, radius, collectHits)
        }
    }

    private fun drawRoundedCornerRadiusCota(
        canvas: Canvas,
        index: Int,
        corner: Int,
        dims: RoundedCornerDims,
        radius: Float,
        collectHits: Boolean
    ) {
        val (cx, cy) = when (corner) {
            0 -> dims.left + radius to dims.top + radius
            1 -> dims.left + dims.rectW - radius to dims.top + radius
            2 -> dims.left + dims.rectW - radius to dims.top + dims.rectH - radius
            else -> dims.left + radius to dims.top + dims.rectH - radius
        }
        val labelX = when (corner) {
            0, 3 -> cx + ce(36f)
            else -> cx - ce(36f)
        }
        val labelY = when (corner) {
            0, 1 -> cy + ce(4f)
            else -> cy - ce(18f)
        }
        canvas.drawLine(cx, cy, labelX, labelY, cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.ROUNDED_RADIUS,
            cx = labelX,
            cy = labelY,
            value = pxToCm(radius),
            collectHits = collectHits,
            sideIndex = corner
        )
    }

    private fun drawF6Cotas(canvas: Canvas, index: Int, composite: Element.Composite, collectHits: Boolean) {
        val dims = dimsRecurrenteF6(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        drawHorizontalCota(
            canvas = canvas,
            index = index,
            type = CotaType.WIDTH,
            left = dims.left,
            right = dims.left + dims.rectW,
            yBase = dims.top + dims.rectH,
            value = pxToCm(dims.rectW),
            collectHits = collectHits,
            preferOutsideAbove = false
        )
        drawVerticalCota(
            canvas = canvas,
            index = index,
            type = CotaType.HEIGHT,
            xBase = dims.left,
            top = dims.top,
            bottom = dims.top + dims.rectH,
            value = pxToCm(dims.rectH),
            collectHits = collectHits,
            preferOutsideRight = false
        )

        val arcBounds = RectF(dims.left, dims.top, dims.left + dims.radius * 2f, dims.top + dims.radius * 2f)
        canvas.drawArc(arcBounds, 180f, 90f, false, cotaLinePaint)
        canvas.drawLine(dims.left, dims.top + dims.radius, dims.left + dims.radius, dims.top + dims.radius, cotaLinePaint)
        canvas.drawLine(dims.left + dims.radius, dims.top, dims.left + dims.radius, dims.top + dims.radius, cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.F6_RADIO,
            cx = dims.left + dims.radius + ce(34f),
            cy = dims.top + dims.radius,
            value = pxToCm(dims.radius),
            collectHits = collectHits
        )
    }

    private fun drawCompositeSideCota(
        canvas: Canvas,
        index: Int,
        contourIndex: Int,
        sideIndex: Int,
        a: PointF,
        b: PointF,
        bounds: RectF,
        value: Float,
        collectHits: Boolean,
        pendiente: Boolean = false
    ) {
        if (distancia(a, b) < ce(5f)) return
        // Resaltar el borde si el lado está bloqueado.
        (elementos.getOrNull(index) as? Element.Composite)?.let {
            if (ladoBloqueado(it, contourIndex, sideIndex)) canvas.drawLine(a.x, a.y, b.x, b.y, ladoBloqueadoPaint)
        }
        val dx = b.x - a.x
        val dy = b.y - a.y
        if (abs(dx) > ce(4f) && abs(dy) > ce(4f)) {
            drawDiagonalCompositeCota(
                canvas = canvas,
                index = index,
                contourIndex = contourIndex,
                sideIndex = sideIndex,
                a = a,
                b = b,
                bounds = bounds,
                value = value,
                collectHits = collectHits,
                pendiente = pendiente
            )
        } else if (abs(dx) >= abs(dy)) {
            // El tope es la parte visible de la pantalla, no la altura del lienzo: con el apunte
            // ampliado o desplazado, comparar contra `height` dejaba la cota fuera de la vista.
            val y = (if ((a.y + b.y) / 2f < bounds.centerY()) minOf(a.y, b.y) - ce(20f) else maxOf(a.y, b.y) + ce(34f))
                .coerceIn(pantallaAMundoY(32f), pantallaAMundoY(height - 12f))
            canvas.drawLine(a.x, y, b.x, y, cotaLinePaint)
            canvas.drawLine(a.x, y - ce(8f), a.x, y + ce(8f), cotaLinePaint)
            canvas.drawLine(b.x, y - ce(8f), b.x, y + ce(8f), cotaLinePaint)
            drawCotaText(
                canvas = canvas,
                index = index,
                type = CotaType.COMPOSITE_SIDE,
                cx = (a.x + b.x) / 2f,
                cy = y - ce(8f),
                value = value,
                collectHits = collectHits,
                contourIndex = contourIndex,
                sideIndex = sideIndex,
                pendiente = pendiente
            )
        } else {
            val x = (if ((a.x + b.x) / 2f < bounds.centerX()) minOf(a.x, b.x) - ce(28f) else maxOf(a.x, b.x) + ce(28f))
                .coerceIn(pantallaAMundoX(40f), pantallaAMundoX(width - 40f))
            canvas.drawLine(x, a.y, x, b.y, cotaLinePaint)
            canvas.drawLine(x - ce(8f), a.y, x + ce(8f), a.y, cotaLinePaint)
            canvas.drawLine(x - ce(8f), b.y, x + ce(8f), b.y, cotaLinePaint)
            drawCotaText(
                canvas = canvas,
                index = index,
                type = CotaType.COMPOSITE_SIDE,
                cx = x,
                cy = (a.y + b.y) / 2f,
                value = value,
                collectHits = collectHits,
                contourIndex = contourIndex,
                sideIndex = sideIndex,
                pendiente = pendiente
            )
        }
    }

    private fun drawDiagonalCompositeCota(
        canvas: Canvas,
        index: Int,
        contourIndex: Int,
        sideIndex: Int,
        a: PointF,
        b: PointF,
        bounds: RectF,
        value: Float,
        collectHits: Boolean,
        pendiente: Boolean = false
    ) {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val len = hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(1f)
        var nx = -dy / len
        var ny = dx / len
        val midX = (a.x + b.x) / 2f
        val midY = (a.y + b.y) / 2f
        val offset = ce(30f)
        val outA = distancia(PointF(midX + nx * offset, midY + ny * offset), PointF(bounds.centerX(), bounds.centerY()))
        val outB = distancia(PointF(midX - nx * offset, midY - ny * offset), PointF(bounds.centerX(), bounds.centerY()))
        if (outB > outA) {
            nx = -nx
            ny = -ny
        }
        val ax = a.x + nx * offset
        val ay = a.y + ny * offset
        val bx = b.x + nx * offset
        val by = b.y + ny * offset
        canvas.drawLine(ax, ay, bx, by, cotaLinePaint)
        canvas.drawLine(ax - nx * ce(8f), ay - ny * ce(8f), ax + nx * ce(8f), ay + ny * ce(8f), cotaLinePaint)
        canvas.drawLine(bx - nx * ce(8f), by - ny * ce(8f), bx + nx * ce(8f), by + ny * ce(8f), cotaLinePaint)
        var angle = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
        if (angle > 90f) angle -= 180f
        if (angle < -90f) angle += 180f
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.COMPOSITE_SIDE,
            cx = (ax + bx) / 2f,
            cy = (ay + by) / 2f - ce(8f),
            value = value,
            collectHits = collectHits,
            contourIndex = contourIndex,
            sideIndex = sideIndex,
            angleDegrees = angle,
            pendiente = pendiente
        )
    }

    private fun drawF4CotaTotalInterior(
        canvas: Canvas,
        index: Int,
        composite: Element.Composite,
        collectHits: Boolean
    ) {
        val contour = composite.contours.firstOrNull() ?: return
        val dims = dimsRecurrenteF4(contour) ?: return
        if (dims.leftW <= 0f || dims.rightW <= 0f) return
        val x = dims.centralLeft + dims.centralW / 2f
        val top = dims.top
        val bottom = dims.top + dims.centralH
        canvas.drawLine(x, top, x, bottom, cotaLinePaint)
        canvas.drawLine(x - ce(8f), top, x + ce(8f), top, cotaLinePaint)
        canvas.drawLine(x - ce(8f), bottom, x + ce(8f), bottom, cotaLinePaint)
        drawCotaText(
            canvas = canvas,
            index = index,
            type = CotaType.HEIGHT,
            cx = x,
            cy = (top + bottom) / 2f,
            value = pxToCm(dims.centralH),
            collectHits = collectHits,
            angleDegrees = -90f
        )
    }

    /**
     * Cota pegada a su lado, sea cual sea la inclinación de este: la línea corre en paralelo, por
     * fuera de la figura, y el número se ladea con ella cuando el lado está inclinado (en los lados
     * rectos se deja derecho, que es como se lee mejor).
     */

    // ==================== COTA A ESCUADRA ====================
    // De la esquina de un corte al lado de enfrente, con la cinta perpendicular. Es la medida que
    // hace falta cuando la forma tiene un corte y lo que importa no es el largo de un lado sino a
    // qué altura queda ese corte contra la pared de enfrente. Y al escribirla, empuja esa arista.
    // La cuenta vive en [CotaAEscuadra], aparte y probada en frío; aquí solo se toca y se dibuja.

    /** true mientras se espera que el dedo elija la esquina desde la que medir. */
    private var eligiendoEscuadra = false

    /** Dónde bajó el dedo: de ahí sale la esquina que coge el imán. */
    private var apuntandoEscuadra: PointF? = null

    /** Dónde está el dedo AHORA: del tirón desde la esquina sale a qué lado va la escuadra. */
    private var arrastrandoEscuadra: PointF? = null

    /** Menos tirón que esto es un toque sin dirección, y entonces manda la cota más corta. */
    private val tironMinimo: Float get() = cmToPx(4f)

    private val imanFlojo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#5500AFEF"); style = Paint.Style.STROKE; strokeWidth = 2f
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    private val imanRelleno = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#5500AFEF"); style = Paint.Style.FILL
    }
    private val imanBorde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#00AFEF"); style = Paint.Style.STROKE; strokeWidth = 4f
    }

    /**
     * Deja el apunte esperando que se toque una esquina para poner ahí su cota a escuadra.
     *
     * Tocando el botón otra vez se sale sin poner nada: si no, la única manera de salirse era
     * poner una cota y luego borrarla.
     */
    fun activarCotaAEscuadra() {
        if (eligiendoEscuadra) {
            eligiendoEscuadra = false
            apuntandoEscuadra = null
            invalidate()
            Toast.makeText(context, "Cota a escuadra: cancelada", Toast.LENGTH_SHORT).show()
            return
        }
        if (compositePrincipal() == null) {
            Toast.makeText(context, "Primero pon una forma con un corte", Toast.LENGTH_SHORT).show()
            return
        }
        eligiendoEscuadra = true
        Toast.makeText(
            context,
            "Toca la esquina del corte y arrastra hacia el lado que quieras medir",
            Toast.LENGTH_LONG
        ).show()
    }

    /** La figura recortada más grande del apunte, que es de la que se miden estas cotas. */
    private fun compositePrincipal(): Element.Composite? =
        elementos.filterIsInstance<Element.Composite>().maxByOrNull {
            boundsForElement(it).let { b -> b.width() * b.height() }
        }

    /** El contorno de esa figura, en píxeles del apunte. */
    private fun contornoDelComposite(c: Element.Composite): List<Pair<Float, Float>>? =
        c.contours.firstOrNull()?.takeIf { it.size >= 3 }?.map { it.x to it.y }

    /**
     * Pone la cota: el imán coge la esquina de [punto] y el tirón hasta [hacia] dice a qué lado.
     *
     * Una esquina de corte tiene casi siempre más de un lado de enfrente —el suelo debajo y el
     * costado al lado—, y cuál se quiere no lo puede adivinar el programa. Por eso se baja el dedo
     * en la esquina y se arrastra hacia el lado que se quiere medir. Un toque sin arrastre coge la
     * cota más corta, que es lo que uno mediría si no dice otra cosa.
     *
     * Se trabaja en píxeles del apunte, que es como se guarda el contorno; a centímetros se pasa
     * solo para enseñar la medida y para leer la que se escriba.
     */
    private fun ponerCotaAEscuadra(punto: PointF, hacia: PointF = punto): Boolean {
        val composite = compositePrincipal()
        val contorno = composite?.let { contornoDelComposite(it) }
        if (composite == null || contorno == null) {
            eligiendoEscuadra = false
            Toast.makeText(context, "Aquí no hay una forma que medir", Toast.LENGTH_SHORT).show()
            return false
        }
        // Si el dedo no acertó se sigue esperando: se vuelve a intentar sin tocar otra vez el
        // botón, y se sale con el botón, que para eso cancela.
        val nodo = CotaAEscuadra.nodoMasCerca(contorno, punto.x to punto.y, cmToPx(30f))
        if (nodo == null) {
            Toast.makeText(context, "Toca más cerca de una esquina", Toast.LENGTH_SHORT).show()
            return true
        }
        val medida = CotaAEscuadra.haciaDonde(
            contorno, nodo,
            (hacia.x - contorno[nodo].first) to (hacia.y - contorno[nodo].second),
            tironMinimo
        )
        if (medida == null) {
            Toast.makeText(
                context,
                "Desde esa esquina no hay lado de enfrente que medir a escuadra",
                Toast.LENGTH_LONG
            ).show()
            return true
        }
        // Puesta la cota, la herramienta se APAGA: mientras está puesta se come los toques del
        // lienzo, y dejarla prendida para encadenar varias era un estorbo —no se podía mover el
        // dibujo ni hacer otra cosa sin acordarse de apagarla—. Para otra cota, el botón otra vez.
        eligiendoEscuadra = false
        elementos.add(
            crearShape(
                Tool.LINE,
                PointF(contorno[nodo].first, contorno[nodo].second),
                PointF(medida.pie.first, medida.pie.second),
                COTA_ESCUADRA
            )
        )
        registrarAccion()
        invalidate()
        return true
    }

    /** Apaga la herramienta sin decir nada: para cuando se coge otra o se abre otro panel. */
    fun cancelarCotaAEscuadra() {
        if (!eligiendoEscuadra && apuntandoEscuadra == null) return
        eligiendoEscuadra = false
        apuntandoEscuadra = null
        arrastrandoEscuadra = null
        invalidate()
    }

    /** Si la herramienta está puesta, para que quien tiene el botón sepa si prenderla o apagarla. */
    val eligiendoCotaAEscuadra: Boolean get() = eligiendoEscuadra

    /** Las cotas a escuadra que hay puestas en el apunte. */
    private fun cotasAEscuadra(): List<Int> =
        elementos.indices.filter {
            (elementos.getOrNull(it) as? Element.Shape)?.cotaHint == COTA_ESCUADRA
        }

    /**
     * La medida que tiene esa cota AHORA.
     *
     * Se vuelve a buscar su esquina en el contorno de hoy en vez de fiarse de dónde se puso: así la
     * cota sigue a la forma cuando esta cambia, en lugar de quedarse clavada midiendo el aire.
     *
     * Y el lado de enfrente se vuelve a elegir por donde APUNTA la propia cota, no por cercanía:
     * una esquina tiene varios lados a los que llegar a escuadra, y el que se eligió al ponerla es
     * el que la línea ya está señalando. Buscando otra vez el más cercano, una cota puesta hacia el
     * costado se saltaba sola al suelo en cuanto la figura cambiaba.
     */
    private fun medidaDeLaCotaAEscuadra(index: Int): Pair<Element.Composite, MedidaAEscuadra>? {
        val linea = elementos.getOrNull(index) as? Element.Shape ?: return null
        val composite = compositePrincipal() ?: return null
        val contorno = contornoDelComposite(composite) ?: return null
        val nodo = CotaAEscuadra.nodoMasCerca(contorno, linea.start.x to linea.start.y, cmToPx(60f))
            ?: return null
        val medida = CotaAEscuadra.haciaDonde(
            contorno, nodo,
            (linea.end.x - linea.start.x) to (linea.end.y - linea.start.y)
        ) ?: return null
        // La línea se recoloca sola: es lo que la hace seguir a la forma.
        linea.start.set(contorno[nodo].first, contorno[nodo].second)
        linea.end.set(medida.pie.first, medida.pie.second)
        return composite to medida
    }

    /**
     * Mientras se apunta: la esquina cogida, TODAS las cotas que salen de ella en flojo, y maciza
     * la que se llevaría el dedo si lo levantara ahí.
     *
     * Enseñar las otras es la mitad del asunto: así se ve de un vistazo que esa esquina tiene suelo
     * y costado, y hacia dónde hay que tirar para coger el que se quiere.
     */
    private fun dibujarImanEscuadra(canvas: Canvas) {
        val donde = apuntandoEscuadra ?: return
        val composite = compositePrincipal() ?: return
        val contorno = contornoDelComposite(composite) ?: return
        val nodo = CotaAEscuadra.nodoMasCerca(contorno, donde.x to donde.y, cmToPx(30f)) ?: return
        val p = contorno[nodo]
        val r = ce(16f)
        canvas.drawCircle(p.first, p.second, r, imanRelleno)
        canvas.drawCircle(p.first, p.second, r, imanBorde)

        val candidatas = CotaAEscuadra.candidatasDesdeNodo(contorno, nodo)
        candidatas.forEach {
            canvas.drawLine(p.first, p.second, it.pie.first, it.pie.second, imanFlojo)
        }
        val tirando = arrastrandoEscuadra ?: donde
        CotaAEscuadra.haciaDonde(
            contorno, nodo,
            (tirando.x - p.first) to (tirando.y - p.second),
            tironMinimo
        )?.let { medida ->
            canvas.drawLine(p.first, p.second, medida.pie.first, medida.pie.second, imanBorde)
            canvas.drawCircle(medida.pie.first, medida.pie.second, ce(8f), imanRelleno)
        }
    }

    private fun dibujarCotasAEscuadra(canvas: Canvas, collectHits: Boolean) {

        cotasAEscuadra().forEach { i ->
            val (_, medida) = medidaDeLaCotaAEscuadra(i) ?: return@forEach
            val linea = elementos[i] as Element.Shape
            // El centro se pone del otro lado de la cota para que su número salga por fuera, que
            // es donde se lee sin taparse con la figura.
            val centro = PointF(
                linea.start.x + (linea.start.x - linea.end.x),
                linea.start.y + (linea.start.y - linea.end.y)
            )
            drawCotaLado(
                canvas, i, CotaType.A_ESCUADRA,
                PointF(linea.start.x, linea.start.y),
                PointF(linea.end.x, linea.end.y),
                centro, pxToCm(medida.distanciaCm), collectHits
            )
        }
    }

    /** Escribe otra medida: empuja la arista del corte y rehace la figura. */
    private fun aplicarCotaAEscuadra(index: Int, valueCm: Float) {
        if (valueCm <= 0f) return
        val (composite, medida) = medidaDeLaCotaAEscuadra(index) ?: return
        val contorno = contornoDelComposite(composite) ?: return
        val movido = CotaAEscuadra.conDistancia(contorno, medida, cmToPx(valueCm))
        val puntos = composite.contours.firstOrNull() ?: return
        if (movido.size != puntos.size) return
        puntos.forEachIndexed { k, p -> p.set(movido[k].first, movido[k].second) }
        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
        val caja = boundsForElement(composite)
        composite.widthCm = pxToCm(caja.width())
        composite.heightCm = pxToCm(caja.height())
        medidaDeLaCotaAEscuadra(index)
        registrarAccion()
        invalidate()
    }
    private fun drawCotaLado(
        canvas: Canvas,
        index: Int,
        type: CotaType,
        a: PointF,
        b: PointF,
        centro: PointF,
        value: Float,
        collectHits: Boolean,
        separacionExtra: Float = 0f,
        sideIndex: Int? = null
    ) {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val largo = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        if (largo < 1f) return
        var nx = -dy / largo
        var ny = dx / largo
        val mx = (a.x + b.x) / 2f
        val my = (a.y + b.y) / 2f
        // La normal mira hacia afuera: la cota nunca se dibuja por dentro de la figura.
        if ((mx - centro.x) * nx + (my - centro.y) * ny < 0f) {
            nx = -nx
            ny = -ny
        }
        val separacion = ce(28f) + separacionExtra
        var ax = a.x + nx * separacion
        var ay = a.y + ny * separacion
        var bx = b.x + nx * separacion
        var by = b.y + ny * separacion
        // Si por fuera se saldría de la pantalla, se mete hacia dentro, como hacían las cotas de
        // antes: más vale leerla encima del dibujo que no verla.
        // Solo cuenta el borde hacia el que se apartó: una puerta más alta que la pantalla tiene sus
        // cotas laterales con los extremos fuera y no por eso hay que meterlas.
        val seSale = if (abs(nx) >= abs(ny)) {
            val xPantalla = mundoAPantallaX((ax + bx) / 2f)
            xPantalla < 58f || xPantalla > width - 58f
        } else {
            val yPantalla = mundoAPantallaY((ay + by) / 2f)
            yPantalla < 58f || yPantalla > height - 58f
        }
        if (seSale) {
            nx = -nx
            ny = -ny
            ax = a.x + nx * separacion
            ay = a.y + ny * separacion
            bx = b.x + nx * separacion
            by = b.y + ny * separacion
        }
        canvas.drawLine(ax, ay, bx, by, cotaLinePaint)
        canvas.drawLine(ax - nx * ce(8f), ay - ny * ce(8f), ax + nx * ce(8f), ay + ny * ce(8f), cotaLinePaint)
        canvas.drawLine(bx - nx * ce(8f), by - ny * ce(8f), bx + nx * ce(8f), by + ny * ce(8f), cotaLinePaint)
        var angulo = Math.toDegrees(kotlin.math.atan2(dy.toDouble(), dx.toDouble())).toFloat()
        if (angulo > 90f) angulo -= 180f
        if (angulo < -90f) angulo += 180f
        val ladeado = abs(angulo) > 8f && abs(abs(angulo) - 90f) > 8f
        drawCotaText(
            canvas = canvas,
            index = index,
            type = type,
            cx = (ax + bx) / 2f + nx * ce(10f),
            cy = (ay + by) / 2f + ny * ce(10f),
            value = value,
            collectHits = collectHits,
            sideIndex = sideIndex,
            angleDegrees = if (ladeado) angulo else 0f
        )
    }

    private fun drawHorizontalCota(
        canvas: Canvas,
        index: Int,
        type: CotaType,
        left: Float,
        right: Float,
        yBase: Float,
        value: Float,
        collectHits: Boolean,
        preferOutsideAbove: Boolean? = null
    ) {
        // El borde se mira en pantalla: es donde el usuario ve si la cota se le sale o no.
        val yPantalla = mundoAPantallaY(yBase)
        val y = when (preferOutsideAbove) {
            true -> if (yPantalla < 58f) yBase + ce(28f) else yBase - ce(20f)
            false -> if (yPantalla > height - 58f) yBase - ce(28f) else yBase + ce(34f)
            null -> if (yPantalla < 58f) yBase + ce(28f) else yBase - ce(20f)
        }
        canvas.drawLine(left, y, right, y, cotaLinePaint)
        canvas.drawLine(left, y - ce(8f), left, y + ce(8f), cotaLinePaint)
        canvas.drawLine(right, y - ce(8f), right, y + ce(8f), cotaLinePaint)
        drawCotaText(canvas, index, type, (left + right) / 2f, y - ce(8f), value, collectHits)
    }

    private fun drawVerticalCota(
        canvas: Canvas,
        index: Int,
        type: CotaType,
        xBase: Float,
        top: Float,
        bottom: Float,
        value: Float,
        collectHits: Boolean,
        preferOutsideRight: Boolean? = null,
        // Separación de más, en píxeles del dibujo: aparta la cota de lo que haya pegado al lado
        // (las bisagras van montadas sobre el canto y le pasaban por encima al número).
        separacionExtra: Float = 0f
    ) {
        val xPantalla = mundoAPantallaX(xBase)
        val x = when (preferOutsideRight) {
            true -> if (xPantalla > width - 70f) xBase - ce(34f) - separacionExtra else xBase + ce(28f) + separacionExtra
            false -> if (xPantalla < 70f) xBase + ce(34f) + separacionExtra else xBase - ce(28f) - separacionExtra
            null -> if (xPantalla > width - 70f) xBase - ce(34f) - separacionExtra else xBase + ce(28f) + separacionExtra
        }
        canvas.drawLine(x, top, x, bottom, cotaLinePaint)
        canvas.drawLine(x - ce(8f), top, x + ce(8f), top, cotaLinePaint)
        canvas.drawLine(x - ce(8f), bottom, x + ce(8f), bottom, cotaLinePaint)
        drawCotaText(canvas, index, type, x, (top + bottom) / 2f, value, collectHits)
    }

    private fun drawCotaText(
        canvas: Canvas,
        index: Int,
        type: CotaType,
        cx: Float,
        cy: Float,
        value: Float,
        collectHits: Boolean,
        contourIndex: Int? = null,
        sideIndex: Int? = null,
        angleDegrees: Float = 0f,
        pendiente: Boolean = false
    ) {
        // `pendiente` = medida ya escrita que la figura todavía no respeta: se muestra igual, que es
        // lo que el taller necesita leer, pero en otro color para que se note que falta completar.
        val paint = if (pendiente) cotaTextPendientePaint else cotaTextPaint
        val text = formatCm(value)
        val widthText = paint.measureText(text)
        val fontMetrics = paint.fontMetrics
        val padH = ce(4f * resources.displayMetrics.density)
        val padV = ce(2f * resources.displayMetrics.density)
        val textHeight = fontMetrics.descent - fontMetrics.ascent
        val original = RectF(
            cx - widthText / 2f - padH,
            cy - textHeight / 2f - padV,
            cx + widthText / 2f + padH,
            cy + textHeight / 2f + padV
        )
        val hit = posicionLibreParaCota(original)
        val drawCx = hit.centerX()
        val drawCy = hit.centerY() - (fontMetrics.ascent + fontMetrics.descent) / 2f
        // Si hubo que apartarla de su sitio, una guia la une con el punto que le toca: es lo que
        // evita que dos numeros apartados se confundan de lado.
        if (abs(hit.centerX() - cx) > ce(1f) || abs(hit.centerY() - cy) > ce(1f)) {
            canvas.drawLine(hit.centerX(), hit.centerY(), cx, cy, cotaGuiaPaint)
        }
        if (abs(angleDegrees) > 0.1f) {
            canvas.save()
            canvas.rotate(angleDegrees, hit.centerX(), hit.centerY())
            canvas.drawRoundRect(hit, ce(6f), ce(6f), cotaBgPaint)
            canvas.drawText(text, drawCx, drawCy, paint)
            canvas.restore()
        } else {
            canvas.drawRoundRect(hit, ce(6f), ce(6f), cotaBgPaint)
            canvas.drawText(text, drawCx, drawCy, paint)
        }
        cotaTextRects.add(RectF(hit))
        if (collectHits) cotaHits.add(CotaHit(index, type, hit, contourIndex, sideIndex))
    }

    private fun posicionLibreParaCota(original: RectF): RectF {
        if (!chocaConCota(original)) return original
        val step = ce(38f)
        val candidates = mutableListOf<RectF>()
        for (level in 1..5) {
            val distance = step * level
            candidates.add(RectF(original).apply { offset(0f, -distance) })
            candidates.add(RectF(original).apply { offset(0f, distance) })
            candidates.add(RectF(original).apply { offset(-distance, 0f) })
            candidates.add(RectF(original).apply { offset(distance, 0f) })
            candidates.add(RectF(original).apply { offset(-distance, -distance) })
            candidates.add(RectF(original).apply { offset(distance, -distance) })
            candidates.add(RectF(original).apply { offset(-distance, distance) })
            candidates.add(RectF(original).apply { offset(distance, distance) })
        }
        return candidates.firstOrNull { !chocaConCota(it) } ?: candidates.minByOrNull { areaChoqueCotas(it) } ?: original
    }

    private fun recogerSimbolos() {
        simboloRects.clear()
        elementos.forEach { if (it is Element.Symbol) simboloRects.add(RectF(it.rect)) }
    }

    private fun chocaConCota(rect: RectF): Boolean {
        val padded = RectF(rect).apply { inset(-ce(6f), -ce(6f)) }
        return cotaTextRects.any { RectF.intersects(padded, it) } ||
            simboloRects.any { RectF.intersects(padded, it) }
    }

    private fun areaChoqueCotas(rect: RectF): Float {
        val padded = RectF(rect).apply { inset(-ce(6f), -ce(6f)) }
        return (cotaTextRects + simboloRects).sumOf { existing ->
            val left = maxOf(padded.left, existing.left)
            val top = maxOf(padded.top, existing.top)
            val right = minOf(padded.right, existing.right)
            val bottom = minOf(padded.bottom, existing.bottom)
            if (right > left && bottom > top) ((right - left) * (bottom - top)).toDouble() else 0.0
        }.toFloat()
    }

    private fun cotaEn(x: Float, y: Float): CotaHit? {
        return cotaHits.lastOrNull { it.rect.contains(x, y) }
    }

    private fun editarCota(hit: CotaHit) {
        val element = elementos.getOrNull(hit.elementIndex) ?: return
        val actual = when (hit.type) {
            CotaType.WIDTH -> when (element) {
                // En el triángulo la cota es la BASE, no el ancho de la caja: girado, no son lo
                // mismo, y el campo tiene que traer la medida que se está tocando.
                is Element.Shape ->
                    if (element.tool == Tool.TRIANGLE) pxToCm(baseTrianguloPx(element)) else element.widthCm
                is Element.Composite -> element.widthCm
                is Element.Freehand -> return
                is Element.Group -> return
                is Element.TextLabel -> return
                is Element.InfoBox -> return
                is Element.Symbol -> return
            }
            CotaType.HEIGHT -> when (element) {
                is Element.Shape ->
                    if (element.tool == Tool.TRIANGLE) pxToCm(alturaTrianguloPx(element)) else element.heightCm
                is Element.Composite -> element.heightCm
                is Element.Freehand -> return
                is Element.Group -> return
                is Element.TextLabel -> return
                is Element.InfoBox -> return
                is Element.Symbol -> return
            }
            CotaType.RECT_TOP -> (element as? Element.Shape)?.topCm ?: return
            CotaType.RECT_RIGHT -> (element as? Element.Shape)?.rightCm ?: return
            CotaType.RECT_BOTTOM -> (element as? Element.Shape)?.bottomCm ?: return
            CotaType.RECT_LEFT -> (element as? Element.Shape)?.leftCm ?: return
            CotaType.DIAMETER -> (element as? Element.Shape)?.diameterCm ?: return
            CotaType.LENGTH -> (element as? Element.Shape)?.lengthCm ?: return
            CotaType.COMPOSITE_SIDE -> {
                val composite = element as? Element.Composite ?: return
                val contourIndex = hit.contourIndex ?: return
                val sideIndex = hit.sideIndex ?: return
                // Lo anotado manda sobre lo dibujado: si el usuario ya escribió esta medida, el
                // campo tiene que traerla a él, no la del dibujo que aún no la respeta.
                composite.declarados[ladoKey(contourIndex, sideIndex)]
                    ?: composite.sideCms.getOrNull(contourIndex)?.getOrNull(sideIndex)
                    ?: return
            }
            CotaType.F5_DESARROLLO -> {
                val composite = element as? Element.Composite ?: return
                val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
                pxToCm(longitudMediaElipsePx(dims.rectW, dims.flecha))
            }
            CotaType.F5_FLECHA -> {
                val composite = element as? Element.Composite ?: return
                val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
                pxToCm(dims.flecha)
            }
            CotaType.F6_RADIO -> {
                val composite = element as? Element.Composite ?: return
                val dims = dimsRecurrenteF6(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
                pxToCm(dims.radius)
            }
            CotaType.ROUNDED_RADIUS -> {
                val composite = element as? Element.Composite ?: return
                val dims = dimsRoundedCorners(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
                val corner = hit.sideIndex ?: return
                pxToCm(dims.radii.getOrNull(corner) ?: return)
            }
            CotaType.PUERTA_ALTURA -> {
                val puente = element as? Element.Shape ?: return
                val marco = (elementos.getOrNull(marcoDePieza(hit.elementIndex) ?: -1) as? Element.Shape) ?: return
                pxToCm(marco.rect.bottom - puente.start.y)
            }
            CotaType.ESQUINA_TRAMO, CotaType.ESQUINA_TRAMO_PLANTA -> {
                val bordes = bordesDeTramos(hit.elementIndex)
                val tramo = hit.sideIndex ?: return
                if (tramo + 1 >= bordes.size) return
                pxToCm(bordes[tramo + 1] - bordes[tramo])
            }
            CotaType.ESQUINA_TRAMO_ARRIBA -> {
                val arriba = puntosArriba(hit.elementIndex)
                val tramo = hit.sideIndex ?: return
                if (tramo + 1 >= arriba.size) return
                pxToCm(distancia(arriba[tramo], arriba[tramo + 1]))
            }
            CotaType.A_ESCUADRA -> {
                val (_, medida) = medidaDeLaCotaAEscuadra(hit.elementIndex) ?: return
                pxToCm(medida.distanciaCm)
            }
            CotaType.PUERTA_HOJA_IZQ, CotaType.PUERTA_HOJA_DER -> {
                val division = element as? Element.Shape ?: return
                val marco = (elementos.getOrNull(marcoDePieza(hit.elementIndex) ?: -1) as? Element.Shape) ?: return
                if (hit.type == CotaType.PUERTA_HOJA_IZQ) {
                    pxToCm(division.start.x - marco.rect.left)
                } else {
                    pxToCm(marco.rect.right - division.start.x)
                }
            }
        }
        val input = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or
                InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(formatCm(actual))
            setSelectAllOnFocus(true)
        }
        // En las formas recurrentes la medida se ANOTA y la figura se rehace cuando estén todas,
        // así que ahí el signo no pinta nada: la dirección la pone el contorno.
        val porAnotar = element is Element.Composite &&
            hit.type == CotaType.COMPOSITE_SIDE && usaMedidasPrimero(element)
        AlertDialog.Builder(context)
            .setTitle(
                if (porAnotar) "Medida del lado (cm)"
                else "Editar cota (negativo = izquierda/abajo)"
            )
            .setView(input)
            .setPositiveButton("Aceptar") { _, _ ->
                val nuevo = input.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                // El cero no es medida en casi nada —y escribirlo es la forma corta de salirse sin
                // tocar nada—, pero en los tramos de la ventana de esquina sí dice algo: ese lado
                // no existe, la ventana empieza o acaba en la esquina.
                val ceroVale = hit.type in setOf(
                    CotaType.ESQUINA_TRAMO,
                    CotaType.ESQUINA_TRAMO_PLANTA,
                    CotaType.ESQUINA_TRAMO_ARRIBA
                )
                if (nuevo != null && (nuevo != 0f || ceroVale)) {
                    if (porAnotar && element is Element.Composite) {
                        anotarMedidaLado(element, hit, nuevo)
                    } else {
                        aplicarNuevaCota(element, hit, nuevo)
                        // Cotas que no son de lado (el alto total de F4) sí se aplican al momento:
                        // si la forma ya estaba completa, lo anotado se relee del dibujo para que
                        // ninguna cota se quede diciendo lo de antes.
                        if (element is Element.Composite && medidasCompletas(element)) {
                            sincronizarDeclarados(element)
                        }
                    }
                    registrarAccion()
                    invalidate()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun ladoKey(contour: Int, side: Int): Long = contour.toLong() * 100000L + side.toLong()

    private fun ladoBloqueado(composite: Element.Composite, contour: Int, side: Int): Boolean =
        composite.bloqueados.contains(ladoKey(contour, side))

    // Long-press sobre una cota de lado → bloquear/desbloquear ese lado (queda inmune a ediciones).
    private fun alternarBloqueoLado(hit: CotaHit) {
        val el = elementos.getOrNull(hit.elementIndex) as? Element.Composite ?: run {
            Toast.makeText(context, "Solo se bloquean lados de formas recurrentes", Toast.LENGTH_SHORT).show()
            return
        }
        // El bloqueo existía para que un lado ya medido no se moviera al editar otro. Estas formas
        // se rehacen con todas las medidas juntas, así que ningún lado se mueve solo y el candado
        // sobra: el gesto pasa a servir para rehacerla YA con lo que se lleve anotado, sin esperar
        // a tenerlas todas (los lados sin medida conservan la que tienen en el dibujo).
        if (usaMedidasPrimero(el)) {
            if (el.declarados.isEmpty()) {
                Toast.makeText(context, "Escribe primero alguna medida.", Toast.LENGTH_SHORT).show()
                return
            }
            reconstruirDesdeDeclarados(el)
            Toast.makeText(context, "Forma rehecha con las medidas anotadas.", Toast.LENGTH_SHORT).show()
            registrarAccion()
            invalidate()
            return
        }
        val c = hit.contourIndex ?: 0
        val s = hit.sideIndex ?: run {
            Toast.makeText(context, "Ese no es un lado individual", Toast.LENGTH_SHORT).show()
            return
        }
        val key = ladoKey(c, s)
        if (el.bloqueados.remove(key)) {
            Toast.makeText(context, "Lado desbloqueado 🔓", Toast.LENGTH_SHORT).show()
        } else {
            el.bloqueados.add(key)
            Toast.makeText(context, "Lado bloqueado 🔒 (no se moverá al editar otros)", Toast.LENGTH_SHORT).show()
        }
        registrarAccion()
        invalidate()
    }

    // Aplica una matriz a un Composite (path + contornos + reconstrucción + lados). Se usa para
    // des-rotar/re-rotar al editar cotas de formas rotadas, sin deformarlas.
    private fun transformarComposite(composite: Element.Composite, matrix: Matrix, actualizarCm: Boolean) {
        composite.path.transform(matrix)
        transformCompositePoints(composite, matrix)
        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
        if (actualizarCm) {
            val b = boundsForElement(composite)
            composite.widthCm = pxToCm(b.width())
            composite.heightCm = pxToCm(b.height())
        }
    }

    // Plantillas poligonales cuya edición lee diferencias de vértices con signo (sensibles a la
    // reflexión). Las paramétricas (F5/F6/redondeados) se editan por límites y no lo necesitan.
    private val TEMPLATES_POLIGONALES =
        setOf(TEMPLATE_F1, TEMPLATE_F2, TEMPLATE_F3, TEMPLATE_F4, TEMPLATE_LIBRE)

    // El espejo hace flip + reverse (que conserva el signo del área), así que la reflexión no se
    // puede detectar por geometría: se rastrea con la bandera `reflejado` que alterna el espejo.
    private fun compositeReflejado(element: Element.Composite): Boolean =
        element.template in TEMPLATES_POLIGONALES && element.reflejado

    // ===================== Medidas primero (formas recurrentes) =====================
    // La forma se inserta con una proporción cualquiera (160 x 110, por ejemplo) y las medidas
    // reales casi nunca se le parecen. Aplicando cada cota en cuanto se escribe, los lados se
    // reparten entre ellos y la figura se deforma tanto que las cotas que faltan ya no se pueden
    // ni tocar. Por eso aquí solo se ANOTA la medida —la cota ya la muestra— y la figura se rehace
    // de una sola vez cuando están todas.

    /** Lados acotados de la forma: el total de medidas que hay que anotar. */
    private fun totalLados(c: Element.Composite): Int = c.contours.sumOf { it.size }

    /** Plantillas cuyos lados se reparten entre sí; son las que se deformaban al editar una a una. */
    private fun usaMedidasPrimero(c: Element.Composite): Boolean = c.template in TEMPLATES_POLIGONALES

    /** Todas las medidas anotadas: desde aquí, tocar una sola cota rehace la forma entera. */
    private fun medidasCompletas(c: Element.Composite): Boolean =
        usaMedidasPrimero(c) && c.declarados.size >= totalLados(c)

    /**
     * Anota la medida de un lado. Mientras falte alguna, la figura NO se toca: se guarda y la cota
     * la muestra. Con todas anotadas, la forma se rehace de una vez con el juego completo.
     */
    private fun anotarMedidaLado(c: Element.Composite, hit: CotaHit, valueCm: Float) {
        val contourIndex = hit.contourIndex ?: return
        val sideIndex = hit.sideIndex ?: return
        // El signo aquí no significa nada: la dirección de cada lado la pone el propio contorno.
        c.declarados[ladoKey(contourIndex, sideIndex)] = abs(valueCm).coerceAtLeast(0.1f)
        if (!medidasCompletas(c)) {
            val faltan = totalLados(c) - c.declarados.size
            val cuenta = if (faltan == 1) "Falta 1 medida" else "Faltan $faltan medidas"
            // La primera vez se explica el atajo: hay apuntes en los que no se miden todos los
            // lados y el usuario necesita poder rehacer la forma con lo que tenga.
            if (c.declarados.size == 1) {
                Toast.makeText(
                    context,
                    "Anotada. $cuenta para rehacer la forma.\n" +
                        "Mantén pulsada una cota para rehacerla ya con lo anotado.",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(context, "Anotada. $cuenta.", Toast.LENGTH_SHORT).show()
            }
            return
        }
        reconstruirDesdeDeclarados(c)
    }

    /**
     * Rehace la forma con TODAS las medidas anotadas a la vez.
     *
     * Se recorre el contorno con la dirección que ya tiene cada lado y el largo que se anotó. Las
     * medidas tomadas a mano casi nunca cierran el polígono exacto, así que el error de cierre se
     * reparte entre los lados de cada eje en proporción a su largo: los lados rectos siguen rectos
     * y la figura no se tuerce. Después las cotas vuelven a decir lo escrito, aunque el cierre haya
     * movido un milímetro: en el taller manda la medida, no el dibujo.
     */
    private fun reconstruirDesdeDeclarados(c: Element.Composite) {
        // Copia de lo escrito: al final se vuelve a poner, porque refreshCompositeSides relee las
        // medidas del dibujo y el reparto del cierre pudo mover algún lado un milímetro.
        val anotadas = LinkedHashMap(c.declarados)
        val antes = boundsForElement(c)
        // El trapecio se rehace con sus medidas rectas (ver abajo); el resto, recorriendo el
        // contorno. Rotado o reflejado no vale el atajo: rehacerlo lo devolvería a la horizontal.
        val porPlantilla = c.template == TEMPLATE_F3 && c.rotationDeg == 0f && !c.reflejado &&
            reconstruirF3Declarado(c)
        if (!porPlantilla) c.contours.forEachIndexed { ci, contour ->
            val n = contour.size
            if (n < 3) return@forEachIndexed
            val dirX = FloatArray(n)
            val dirY = FloatArray(n)
            val largo = FloatArray(n)
            for (i in 0 until n) {
                val a = contour[i]
                val b = contour[(i + 1) % n]
                val d = distancia(a, b).coerceAtLeast(0.01f)
                dirX[i] = (b.x - a.x) / d
                dirY[i] = (b.y - a.y) / d
                val anotado = c.declarados[ladoKey(ci, i)]
                largo[i] = if (anotado != null) cmToPx(anotado).coerceAtLeast(cmToPx(0.1f)) else d
            }
            var errorX = 0f
            var errorY = 0f
            for (i in 0 until n) {
                errorX += dirX[i] * largo[i]
                errorY += dirY[i] * largo[i]
            }
            repartirCierre(dirX, largo, errorX)
            repartirCierre(dirY, largo, errorY)
            var x = contour[0].x
            var y = contour[0].y
            for (i in 0 until n) {
                contour[i].set(x, y)
                x += dirX[i] * largo[i]
                y += dirY[i] * largo[i]
            }
        }
        rebuildCompositePath(c)
        // Dejarla donde estaba: la medida real puede ser mucho mayor que el dibujo de partida y sin
        // recentrar la forma se iría del papel.
        val ahora = boundsForElement(c)
        val dx = antes.centerX() - ahora.centerX()
        val dy = antes.centerY() - ahora.centerY()
        if (dx != 0f || dy != 0f) {
            val m = Matrix().apply { setTranslate(dx, dy) }
            c.path.transform(m)
            transformCompositePoints(c, m)
        }
        refreshCompositeSides(c)
        c.declarados.clear()
        c.declarados.putAll(anotadas)
        anotadas.forEach { (clave, valor) ->
            val ci = (clave / 100000L).toInt()
            val si = (clave % 100000L).toInt()
            c.sideCms.getOrNull(ci)?.let { lados -> if (si in lados.indices) lados[si] = valor }
        }
        val bnds = boundsForElement(c)
        c.widthCm = pxToCm(bnds.width())
        c.heightCm = pxToCm(bnds.height())
        asegurarVisible(c)
    }

    /**
     * F3 (trapecio) es la única plantilla con un lado en diagonal. El recorrido general respeta la
     * inclinación que ese lado ya tenía, y con medidas nuevas eso deja un error de cierre que se
     * come unos centímetros de los otros lados. Aquí se rehace con sus tres medidas rectas —lado
     * superior, inferior y vertical—, que lo definen entero; la diagonal sigue mostrando lo medido.
     */
    private fun reconstruirF3Declarado(c: Element.Composite): Boolean {
        val contour = c.contours.firstOrNull()?.takeIf { it.size == 4 } ?: return false
        val minSize = cmToPx(0.1f)
        val superior = c.declarados[ladoKey(0, 0)]?.let { cmToPx(it) } ?: return false
        val inferior = c.declarados[ladoKey(0, 2)]?.let { cmToPx(it) } ?: return false
        val vertical = c.declarados[ladoKey(0, 3)]?.let { cmToPx(it) } ?: return false
        val nuevo = contourRecurrenteF3(
            left = contour.minOf { it.x },
            top = contour.minOf { it.y },
            topW = superior.coerceAtLeast(minSize),
            bottomW = inferior.coerceAtLeast(minSize),
            totalH = vertical.coerceAtLeast(minSize)
        )
        contour.clear()
        contour.addAll(nuevo)
        return true
    }

    /**
     * Reparte el error de cierre de un eje SOLO entre los lados que corren por ese eje, en
     * proporción a su largo. Los diagonales no se tocan: su medida es la que dio el usuario y
     * estirarla movería también el otro eje.
     */
    private fun repartirCierre(dir: FloatArray, largo: FloatArray, error: Float) {
        if (abs(error) < 0.01f) return
        val delEje = dir.indices.filter { abs(dir[it]) > 0.999f }
        val suma = delEje.sumOf { largo[it].toDouble() }.toFloat()
        if (delEje.isEmpty() || suma <= 0.01f) return
        for (i in delEje) {
            val ajuste = error * (largo[i] / suma) * dir[i]
            largo[i] = (largo[i] - ajuste).coerceAtLeast(cmToPx(0.1f))
        }
    }

    /** Si al rehacerla la forma ya no cabe en pantalla, se encuadra el apunte. */
    private fun asegurarVisible(c: Element.Composite) {
        if (width <= 0 || height <= 0) return
        val b = boundsForElement(c)
        val izquierda = b.left * viewScale + viewOffsetX
        val arriba = b.top * viewScale + viewOffsetY
        val derecha = b.right * viewScale + viewOffsetX
        val abajo = b.bottom * viewScale + viewOffsetY
        if (izquierda < 0f || arriba < 0f || derecha > width || abajo > height) fitContentInView()
    }

    private fun aplicarNuevaCota(element: Element, hit: CotaHit, valueCm: Float) {
        when (element) {
            is Element.Shape -> {
                val esMarco = element.cotaHint in MARCOS_PLANTILLA
                // Las cotas de alto interiores son una por cada 120 cm: si el ancho cambia y nadie
                // las había tocado a mano, se recalculan; si las tocó, se respeta lo que puso.
                val altosAntes = if (esMarco) altosLibres(hit.elementIndex).size else 0
                val automaticasAntes = if (esMarco) altosAutomaticosDelMarco(hit.elementIndex) else 0
                // Tocando una altura, las cotas de alto se releen de la línea de arriba; tocando un
                // ancho, cada una conserva la suya y no se mueve la del tramo de al lado.
                val cambiaAltura = hit.type in setOf(
                    CotaType.RECT_LEFT, CotaType.RECT_RIGHT, CotaType.HEIGHT
                )
                aplicarNuevaCotaShape(element, hit.type, valueCm, hit.elementIndex, hit.sideIndex)
                if (esMarco) {
                    if (altosAntes == automaticasAntes) ajustarAltosAutomaticos(hit.elementIndex)
                    sincronizarMarco(hit.elementIndex, cambiaAltura)
                }
            }
            is Element.Composite -> {
                val ang = element.rotationDeg
                val reflejado = compositeReflejado(element)
                if (ang == 0f && !reflejado) {
                    aplicarNuevaCotaComposite(element, hit, valueCm)
                } else {
                    // La lógica de edición asume ejes de pantalla y el orden canónico de vértices.
                    // Normalizo: des-roto y, si está reflejada, la des-reflejo (esto invierte el orden,
                    // así que remapeo el lado tocado). Edito en canónico y vuelvo a reflejar/rotar en el
                    // mismo lugar: se respeta la posición actual (rotada/reflejada) sin deformar.
                    val c0 = boundsForElement(element).let { PointF(it.centerX(), it.centerY()) }
                    if (ang != 0f) {
                        transformarComposite(element, Matrix().apply { setRotate(-ang, c0.x, c0.y) }, actualizarCm = true)
                    }
                    var hitCanonico = hit
                    val axisX = boundsForElement(element).centerX()
                    if (reflejado) {
                        mirrorElementHorizontally(element, axisX)  // des-reflejar (eje fijo)
                        val n = element.contours.getOrNull(hit.contourIndex ?: 0)?.size
                        val s = hit.sideIndex
                        if (n != null && s != null) {
                            hitCanonico = hit.copy(sideIndex = ((n - 2 - s) % n + n) % n)
                        }
                    }
                    aplicarNuevaCotaComposite(element, hitCanonico, valueCm)
                    if (reflejado) {
                        mirrorElementHorizontally(element, axisX)  // volver a reflejar (mismo eje)
                    }
                    if (ang != 0f) {
                        val c1 = boundsForElement(element).let { PointF(it.centerX(), it.centerY()) }
                        transformarComposite(element, Matrix().apply {
                            postTranslate(c0.x - c1.x, c0.y - c1.y) // re-centrar en el mismo punto
                            postRotate(ang, c0.x, c0.y)             // volver a rotar
                        }, actualizarCm = false)
                    }
                }
            }
            is Element.Freehand -> Unit
            is Element.Group -> Unit
            is Element.TextLabel -> Unit
            is Element.InfoBox -> Unit
            is Element.Symbol -> Unit
        }
    }

    private fun aplicarNuevaCotaShape(
        shape: Element.Shape,
        type: CotaType,
        valueCm: Float,
        elementIndex: Int? = null,
        sideIndex: Int? = null
    ) {
        when (type) {
            // Ancho y alto se aplican estirando la figura desde su esquina de arriba a la izquierda.
            // Se estiran TODOS sus puntos, no solo la caja: el triángulo se dibuja por sus vértices
            // y, tocando solo la caja, la medida escrita no se veía en el dibujo.
            CotaType.WIDTH -> {
                if (shape.tool == Tool.TRIANGLE) {
                    aplicarBaseTriangulo(shape, valueCm)
                    return
                }
                val factor = factorEstirado(shape.rect.width(), valueCm)
                val vertical = if (shape.tool == Tool.CIRCLE) factor else 1f
                estirarShape(shape, factor, vertical, PointF(shape.rect.left, shape.rect.top))
                actualizarShapeDesdePuntos(shape)
                shape.widthCm = valueCm
                if (shape.tool == Tool.CIRCLE) {
                    shape.diameterCm = valueCm
                    shape.heightCm = valueCm
                }
            }
            CotaType.HEIGHT -> {
                if (shape.tool == Tool.TRIANGLE) {
                    aplicarAlturaTriangulo(shape, valueCm)
                    return
                }
                val factor = factorEstirado(shape.rect.height(), valueCm)
                estirarShape(shape, 1f, factor, PointF(shape.rect.left, shape.rect.top))
                actualizarShapeDesdePuntos(shape)
                shape.heightCm = valueCm
            }
            // Cada lado se estira SOBRE SÍ MISMO: la medida escrita es la del lado, con la
            // inclinación que tenga. Un lado inclinado mide más que la separación entre esquinas, y
            // era esa separación la que se escribía antes.
            CotaType.RECT_TOP -> {
                shape.topCm = valueCm
                moverEsquina(shape.topRight, shape.topLeft, shape.topRight, PointF(1f, 0f), valueCm)
                actualizarBoundsRectangulo(shape)
            }
            CotaType.RECT_RIGHT -> {
                shape.rightCm = valueCm
                fijarBaseRectangulo(shape)
                moverEsquina(shape.topRight, shape.bottomRight, shape.topRight, PointF(0f, -1f), valueCm)
                actualizarBoundsRectangulo(shape)
            }
            CotaType.RECT_BOTTOM -> {
                shape.bottomCm = valueCm
                moverEsquina(shape.bottomRight, shape.bottomLeft, shape.bottomRight, PointF(1f, 0f), valueCm)
                actualizarBoundsRectangulo(shape)
            }
            CotaType.RECT_LEFT -> {
                shape.leftCm = valueCm
                fijarBaseRectangulo(shape)
                moverEsquina(shape.topLeft, shape.bottomLeft, shape.topLeft, PointF(0f, -1f), valueCm)
                actualizarBoundsRectangulo(shape)
            }
            CotaType.DIAMETER -> {
                shape.diameterCm = valueCm
                shape.widthCm = valueCm
                shape.heightCm = valueCm
                val centerX = shape.rect.centerX()
                val centerY = shape.rect.centerY()
                val size = cmToPx(valueCm)
                shape.rect.set(
                    centerX - size / 2f,
                    centerY - size / 2f,
                    centerX + size / 2f,
                    centerY + size / 2f
                )
                shape.start.x = shape.rect.left
                shape.start.y = shape.rect.top
                shape.end.x = shape.rect.right
                shape.end.y = shape.rect.bottom
            }
            CotaType.LENGTH -> {
                if (shape.cotaHint == "GRADA_PASO" || shape.cotaHint == "GRADA_CONTRAPASO") {
                    aplicarNuevaCotaGraderia(elementIndex ?: return, valueCm)
                    return
                }
                if (shape.cotaHint == PUERTA_PUENTE) {
                    aplicarLargoPuente(elementIndex ?: return, abs(valueCm))
                    return
                }
                if (shape.cotaHint == VENTANA_ALTO || shape.cotaHint == VENTANA_ALTO_ESQUINA) {
                    aplicarAltoVentana(elementIndex ?: return, abs(valueCm))
                    return
                }
                shape.lengthCm = valueCm
                val dx = shape.end.x - shape.start.x
                val dy = shape.end.y - shape.start.y
                val currentLength = hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(1f)
                val angle = kotlin.math.atan2(dy.toDouble(), dx.toDouble())
                val newLength = cmToPx(valueCm)
                shape.end.x = shape.start.x + (cos(angle) * newLength).toFloat()
                shape.end.y = shape.start.y + (sin(angle) * newLength).toFloat()
                shape.rect.set(
                    minOf(shape.start.x, shape.end.x),
                    minOf(shape.start.y, shape.end.y),
                    maxOf(shape.start.x, shape.end.x),
                    maxOf(shape.start.y, shape.end.y)
                )
                if (currentLength > 0f) Unit
            }
            CotaType.PUERTA_ALTURA -> {
                val puenteIndex = elementIndex ?: return
                val puente = elementos.getOrNull(puenteIndex) as? Element.Shape ?: return
                val marcoIndex = marcoDePieza(puenteIndex) ?: return
                val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
                val altura = abs(valueCm)
                // Subirlo por encima del alto total es la forma corta de decir que ahí no va: se
                // queda sin travesaño en vez de con la línea pegada al dintel.
                if (altura >= marco.heightCm) {
                    quitarPuente(puenteIndex)
                    Toast.makeText(context, "Puente quitado", Toast.LENGTH_SHORT).show()
                    return
                }
                // Solo sube o baja ESTE puente: el del tramo de al lado va a lo suyo.
                val y = marco.bottomLeft.y - cmToPx(altura)
                puente.start.y = y
                puente.end.y = y
                sincronizarMarco(marcoIndex, reinterpolarAltos = false)
            }
            CotaType.PUERTA_HOJA_IZQ, CotaType.PUERTA_HOJA_DER -> {
                aplicarAnchoHoja(elementIndex ?: return, type, abs(valueCm))
            }
            CotaType.ESQUINA_TRAMO -> {
                aplicarAnchoTramo(elementIndex ?: return, sideIndex ?: return, abs(valueCm))
            }
            CotaType.ESQUINA_TRAMO_PLANTA -> {
                aplicarAnchoTramoEnPlanta(elementIndex ?: return, sideIndex ?: return, abs(valueCm))
            }
            CotaType.ESQUINA_TRAMO_ARRIBA -> {
                aplicarArribaTramo(elementIndex ?: return, sideIndex ?: return, abs(valueCm))
            }
            CotaType.A_ESCUADRA -> {
                aplicarCotaAEscuadra(elementIndex ?: return, abs(valueCm))
            }
            CotaType.COMPOSITE_SIDE,
            CotaType.F5_DESARROLLO,
            CotaType.F5_FLECHA,
            CotaType.F6_RADIO,
            CotaType.ROUNDED_RADIUS -> Unit
        }
    }

    /**
     * El largo del puente es SUYO: se estira o se encoge él solo, sobre su mismo centro, y el marco
     * se queda como está. Si sobra o falta respecto de los lados, así queda —es lo que se dibujó—;
     * el marco se cambia desde sus propias cotas, no desde esta.
     */
    private fun aplicarLargoPuente(puenteIndex: Int, valueCm: Float) {
        val puente = elementos.getOrNull(puenteIndex) as? Element.Shape ?: return
        val centro = (puente.start.x + puente.end.x) / 2f
        val mitad = cmToPx(valueCm.coerceAtLeast(1f)) / 2f
        puente.lengthCm = valueCm
        puente.largoFijado = true
        puente.start.x = centro - mitad
        puente.end.x = centro + mitad
        puente.rect.set(puente.start.x, puente.start.y, puente.end.x, puente.end.y)
    }

    /**
     * Ancho de una hoja. El vano no se mueve: la división se corre y la otra hoja se queda con lo
     * que sobra —140 en total y 90 en una son 50 en la otra—.
     */
    private fun aplicarAnchoHoja(divisionIndex: Int, type: CotaType, valueCm: Float) {
        val division = elementos.getOrNull(divisionIndex) as? Element.Shape ?: return
        val marcoIndex = marcoDePieza(divisionIndex) ?: return
        val marco = (elementos.getOrNull(marcoIndex) as? Element.Shape)?.rect ?: return
        val minimo = cmToPx(1f)
        val destino = if (type == CotaType.PUERTA_HOJA_IZQ) {
            marco.left + cmToPx(valueCm)
        } else {
            marco.right - cmToPx(valueCm)
        }
        val x = destino.coerceIn(
            marco.left + minimo,
            (marco.right - minimo).coerceAtLeast(marco.left + minimo)
        )
        division.start.x = x
        division.end.x = x
        division.rect.set(x, minOf(division.start.y, division.end.y), x, maxOf(division.start.y, division.end.y))
        colocarPiezasDelMarco(marcoIndex)
    }

    /**
     * Alto tomado por dentro del vano. Escribirlo lo fija: desde ahí manda esa medida y la línea de
     * arriba se quiebra por ese punto, que es como está el vano de verdad.
     */
    private fun aplicarAltoVentana(altoIndex: Int, valueCm: Float) {
        val alto = elementos.getOrNull(altoIndex) as? Element.Shape ?: return
        alto.lengthCm = valueCm
        alto.largoFijado = true
        val marcoIndex = marcoDePieza(altoIndex)
        if (marcoIndex != null) repartirAltos(marcoIndex)
    }

    /**
     * Ancho de un tramo de la ventana de esquina. Cada tramo se mide contra su pared: el que se
     * edita crece o encoge, los de su derecha se corren enteros y el ancho total es la suma.
     */
    private fun aplicarAnchoTramo(marcoIndex: Int, tramo: Int, valueCm: Float) {
        // Un lado puede medir CERO: es la ventana que empieza o acaba en la esquina, sin pared
        // recta por ese lado. Y entonces se va ENTERO, por los dos lados: en una pared que no
        // existe no hay descuadre que apuntar.
        if (valueCm <= 0.5f) {
            anularTramo(marcoIndex, tramo)
            return
        }
        aplicarAnchoAbajoTramo(marcoIndex, tramo, cmToPx(valueCm))
    }

    /**
     * Deja un tramo en cero, venga el cero de la cota que venga: la ventana arranca (o acaba) en la
     * esquina. Alguna pared tiene que quedar en pie, que una ventana entera en cero no es ventana.
     */
    private fun anularTramo(marcoIndex: Int, tramo: Int) {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
        val bordes = bordesDeTramos(marcoIndex)
        if (tramo + 1 >= bordes.size) return
        if (pxToCm(marco.rect.width() - (bordes[tramo + 1] - bordes[tramo])) < 0.5f) {
            Toast.makeText(
                context,
                "Algún lado tiene que medir: la ventana no puede quedar toda en cero",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        aplicarAnchoAbajoTramo(marcoIndex, tramo, 0f)
        aplicarAnchoArribaTramo(marcoIndex, tramo, 0f)
    }

    /** Ancho del lado de ABAJO de un tramo; el de arriba va con su propia cota. */
    private fun aplicarAnchoAbajoTramo(marcoIndex: Int, tramo: Int, anchoPx: Float) {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
        val bordes = bordesDeTramos(marcoIndex)
        if (tramo + 1 >= bordes.size) return
        val delta = anchoPx.coerceAtLeast(0f) - (bordes[tramo + 1] - bordes[tramo])
        if (abs(delta) < 0.01f) return
        // Solo se mueve la línea de ABAJO: el lado de arriba es otra medida y se escribe aparte.
        val borde = bordes[tramo + 1]
        quiebresDelMarco(marcoIndex).forEach { i ->
            val q = elementos[i] as Element.Shape
            if (q.end.x >= borde - 0.5f) q.end.x += delta
        }
        // Y con ellas las bandas de pared curva, que también cortan el desarrollo: sin esto, al
        // cambiar un lado la banda se quedaba clavada y la curva perdía su trozo. Solo su lado de
        // abajo, como las aristas: el de arriba es otra medida y se mueve con la suya.
        bandasDeCurva(marcoIndex).forEach { i ->
            val b = elementos[i] as Element.Shape
            if (b.end.x >= borde - 0.5f) {
                b.end.x += delta
                b.rect.set(minOf(b.start.x, b.end.x), b.rect.top, maxOf(b.start.x, b.end.x), b.rect.bottom)
            }
        }
        marco.bottomRight.x += delta
        actualizarBoundsRectangulo(marco)
        sincronizarMarco(marcoIndex, reinterpolarAltos = false)
    }

    /**
     * El ancho de un tramo escrito en la PLANTA: ahí el tramo es la pared entera, no uno de sus
     * lados, así que se mueven los dos —el de arriba y el de abajo— y el cambio se ve en la alzada.
     *
     * El descuadre que ya estuviera apuntado se conserva: los dos lados se mueven lo MISMO, no se
     * igualan. Si arriba medía un centímetro más que abajo, lo sigue midiendo; ese dato se tomó en
     * obra y no lo borra una medida escrita en la planta.
     */
    private fun aplicarAnchoTramoEnPlanta(marcoIndex: Int, tramo: Int, valueCm: Float) {
        val bordes = bordesDeTramos(marcoIndex)
        if (tramo + 1 >= bordes.size) return
        val arriba = puntosArriba(marcoIndex)
        val anchoArriba = if (tramo + 1 < arriba.size) arriba[tramo + 1].x - arriba[tramo].x else null
        // El cero se lo lleva el tramo entero, sin repartos: es la pared que no existe.
        if (valueCm <= 0.5f) {
            anularTramo(marcoIndex, tramo)
            return
        }
        val delta = cmToPx(valueCm) - (bordes[tramo + 1] - bordes[tramo])
        if (abs(delta) < 0.01f) return
        aplicarAnchoTramo(marcoIndex, tramo, valueCm)
        anchoArriba?.let { aplicarAnchoArribaTramo(marcoIndex, tramo, it + delta) }
    }

    /**
     * Ancho del lado de arriba de un tramo. Mueve solo la línea de arriba —la de abajo se midió
     * aparte y no se toca—, así que la arista queda un poco inclinada: es lo que pasa cuando la
     * pared no viene a plomo.
     */
    private fun aplicarAnchoArribaTramo(marcoIndex: Int, tramo: Int, anchoPx: Float) {
        val marco = elementos.getOrNull(marcoIndex) as? Element.Shape ?: return
        val arriba = puntosArriba(marcoIndex)
        if (tramo + 1 >= arriba.size) return
        val delta = anchoPx - (arriba[tramo + 1].x - arriba[tramo].x)
        if (abs(delta) < 0.01f) return
        val borde = arriba[tramo + 1].x
        quiebresDelMarco(marcoIndex).forEach { i ->
            val q = elementos[i] as Element.Shape
            if (q.start.x >= borde - 0.5f) q.start.x += delta
        }
        // Las bandas de pared curva cortan el cabezal igual que una arista: su lado de arriba se
        // mueve con él, y el de abajo con la cota de abajo.
        bandasDeCurva(marcoIndex).forEach { i ->
            val b = elementos[i] as Element.Shape
            if (b.start.x >= borde - 0.5f) {
                b.start.x += delta
                b.rect.set(minOf(b.start.x, b.end.x), b.rect.top, maxOf(b.start.x, b.end.x), b.rect.bottom)
            }
        }
        marco.topRight.x += delta
        actualizarBoundsRectangulo(marco)
        sincronizarMarco(marcoIndex, reinterpolarAltos = false)
    }

    /**
     * Lado de arriba de un tramo. Los altos de las dos aristas no se tocan —esos se midieron—, así
     * que lo que da esa medida es el ancho del tramo: se despeja y se aplica como si se hubiera
     * escrito abajo. Si se pide un cabezal más corto que el propio desnivel, no hay triángulo que
     * lo aguante y se avisa.
     */
    private fun aplicarArribaTramo(marcoIndex: Int, tramo: Int, valueCm: Float) {
        val arriba = puntosArriba(marcoIndex)
        if (tramo + 1 >= arriba.size) return
        // El cero no es un cabezal corto: es el tramo que desaparece, y se va por los dos lados.
        if (valueCm <= 0.5f) {
            anularTramo(marcoIndex, tramo)
            return
        }
        val desnivel = abs(arriba[tramo + 1].y - arriba[tramo].y)
        val largo = cmToPx(valueCm)
        if (largo <= desnivel + cmToPx(1f)) {
            Toast.makeText(
                context,
                "Ese lado no puede medir menos que el desnivel entre sus alturas",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val ancho = kotlin.math.sqrt((largo * largo - desnivel * desnivel).toDouble()).toFloat()
        aplicarAnchoArribaTramo(marcoIndex, tramo, ancho)
    }

    private fun aplicarNuevaCotaGraderia(elementIndex: Int, valueCm: Float) {
        val edited = elementos.getOrNull(elementIndex) as? Element.Shape ?: return
        if (edited.cotaHint !in setOf("GRADA_PASO", "GRADA_CONTRAPASO")) return

        val startIndex = buscarInicioBloqueGraderia(elementIndex)
        val endIndex = buscarFinBloqueGraderia(elementIndex)
        val tramoIndices = (startIndex..endIndex).filter { index ->
            (elementos.getOrNull(index) as? Element.Shape)?.cotaHint in setOf("GRADA_PASO", "GRADA_CONTRAPASO")
        }
        if (tramoIndices.isEmpty()) return

        val editedTramo = tramoIndices.indexOf(elementIndex).takeIf { it >= 0 } ?: return
        val lengthsCm = tramoIndices.map { index ->
            val shape = elementos[index] as Element.Shape
            if (index == elementIndex) valueCm else shape.lengthCm
        }
        val hints = tramoIndices.map { index -> (elementos[index] as Element.Shape).cotaHint.orEmpty() }
        val origin = PointF(
            (elementos[tramoIndices.first()] as Element.Shape).start.x,
            (elementos[tramoIndices.first()] as Element.Shape).start.y
        )

        var x = origin.x
        var y = origin.y
        tramoIndices.forEachIndexed { tramoIndex, elementPosition ->
            val shape = elementos[elementPosition] as Element.Shape
            val lengthPx = cmToPx(lengthsCm[tramoIndex])
            val start = PointF(x, y)
            val end = if (hints[tramoIndex] == "GRADA_CONTRAPASO") {
                PointF(x, y - lengthPx)
            } else {
                PointF(x + lengthPx, y)
            }
            setShapeLine(shape, start, end, lengthsCm[tramoIndex])
            x = end.x
            y = end.y
        }

        val total = (startIndex..endIndex).firstNotNullOfOrNull { index ->
            (elementos.getOrNull(index) as? Element.Shape)?.takeIf { it.cotaHint == "GRADA_TOTAL_COTA" }
        }
        total?.let {
            val totalStart = PointF(origin.x, origin.y)
            val totalEnd = PointF(x, y)
            setShapeLine(it, totalStart, totalEnd, pxToCm(distancia(totalStart, totalEnd)))
        }

        selectedIndices.clear()
        selectedIndices.add(tramoIndices[editedTramo])
    }

    private fun buscarInicioBloqueGraderia(fromIndex: Int): Int {
        var index = fromIndex
        while (index > 0 && esElementoGraderia(elementos.getOrNull(index - 1))) index--
        return index
    }

    private fun buscarFinBloqueGraderia(fromIndex: Int): Int {
        var index = fromIndex
        while (index < elementos.lastIndex && esElementoGraderia(elementos.getOrNull(index + 1))) index++
        return index
    }

    private fun esElementoGraderia(element: Element?): Boolean {
        return (element as? Element.Shape)?.cotaHint in setOf("GRADA_PASO", "GRADA_CONTRAPASO", "GRADA_TOTAL_COTA")
    }

    private fun setShapeLine(shape: Element.Shape, start: PointF, end: PointF, lengthCm: Float) {
        shape.start.x = start.x
        shape.start.y = start.y
        shape.end.x = end.x
        shape.end.y = end.y
        shape.lengthCm = lengthCm
        shape.rect.set(
            minOf(start.x, end.x),
            minOf(start.y, end.y),
            maxOf(start.x, end.x),
            maxOf(start.y, end.y)
        )
        shape.widthCm = pxToCm(shape.rect.width())
        shape.heightCm = pxToCm(shape.rect.height())
        shape.diameterCm = pxToCm(maxOf(shape.rect.width(), shape.rect.height()))
        shape.topLeft.x = shape.rect.left
        shape.topLeft.y = shape.rect.top
        shape.topRight.x = shape.rect.right
        shape.topRight.y = shape.rect.top
        shape.bottomRight.x = shape.rect.right
        shape.bottomRight.y = shape.rect.bottom
        shape.bottomLeft.x = shape.rect.left
        shape.bottomLeft.y = shape.rect.bottom
        shape.topCm = shape.widthCm
        shape.rightCm = shape.heightCm
        shape.bottomCm = shape.widthCm
        shape.leftCm = shape.heightCm
    }

    private fun aplicarNuevaCotaComposite(composite: Element.Composite, hit: CotaHit, valueCmRaw: Float) {
        // El signo (dirección) SOLO aplica a lados individuales; el resto usa el valor absoluto.
        val valueCm = if (hit.type == CotaType.COMPOSITE_SIDE) valueCmRaw else kotlin.math.abs(valueCmRaw)
        val bounds = boundsForElement(composite)
        when (hit.type) {
            CotaType.WIDTH -> {
                if (composite.template == TEMPLATE_F5) {
                    aplicarMedidasRecurrenteF5(composite, anchoCm = valueCm, altoCm = null)
                    return
                }
                if (composite.template == TEMPLATE_F6) {
                    aplicarMedidasRecurrenteF6(composite, anchoCm = valueCm, altoCm = null)
                    return
                }
                if (composite.template == TEMPLATE_ROUNDED) {
                    aplicarMedidasRoundedCorners(composite, anchoCm = valueCm, altoCm = null)
                    return
                }
                val current = bounds.width().coerceAtLeast(1f)
                val scale = cmToPx(valueCm) / current
                val matrix = Matrix().apply { setScale(scale, 1f, bounds.left, bounds.bottom) }
                composite.path.transform(matrix)
                transformCompositePoints(composite, matrix)
                composite.widthCm = valueCm
                refreshCompositeSides(composite)
            }
            CotaType.HEIGHT -> {
                if (composite.template == TEMPLATE_F5) {
                    aplicarMedidasRecurrenteF5(composite, anchoCm = null, altoCm = valueCm)
                    return
                }
                if (composite.template == TEMPLATE_F6) {
                    aplicarMedidasRecurrenteF6(composite, anchoCm = null, altoCm = valueCm)
                    return
                }
                if (composite.template == TEMPLATE_ROUNDED) {
                    aplicarMedidasRoundedCorners(composite, anchoCm = null, altoCm = valueCm)
                    return
                }
                if (composite.template == TEMPLATE_F4) {
                    aplicarAlturaTotalRecurrenteF4(composite, valueCm)
                    return
                }
                val current = bounds.height().coerceAtLeast(1f)
                val scale = cmToPx(valueCm) / current
                val matrix = Matrix().apply { setScale(1f, scale, bounds.left, bounds.bottom) }
                composite.path.transform(matrix)
                transformCompositePoints(composite, matrix)
                composite.heightCm = valueCm
                refreshCompositeSides(composite)
            }
            CotaType.COMPOSITE_SIDE -> {
                aplicarNuevaCotaCompositeSide(composite, hit, valueCm)
            }
            CotaType.F5_DESARROLLO -> {
                if (composite.template == TEMPLATE_F5) {
                    aplicarDesarrolloRecurrenteF5(composite, valueCm)
                }
            }
            CotaType.F5_FLECHA -> {
                if (composite.template == TEMPLATE_F5) {
                    aplicarFlechaRecurrenteF5(composite, valueCm)
                }
            }
            CotaType.F6_RADIO -> {
                if (composite.template == TEMPLATE_F6) {
                    aplicarRadioRecurrenteF6(composite, valueCm)
                }
            }
            CotaType.ROUNDED_RADIUS -> {
                if (composite.template == TEMPLATE_ROUNDED) {
                    aplicarRadioRoundedCorner(composite, hit.sideIndex ?: return, valueCm)
                }
            }
            CotaType.RECT_TOP,
            CotaType.RECT_RIGHT,
            CotaType.RECT_BOTTOM,
            CotaType.RECT_LEFT,
            CotaType.DIAMETER,
            CotaType.LENGTH,
            CotaType.PUERTA_ALTURA,
            CotaType.PUERTA_HOJA_IZQ,
            CotaType.PUERTA_HOJA_DER,
            CotaType.ESQUINA_TRAMO,
            CotaType.ESQUINA_TRAMO_PLANTA,
            CotaType.ESQUINA_TRAMO_ARRIBA,
            // La cota a escuadra no es del composite: es una línea suelta que lo mide, y se aplica
            // por su propio camino.
            CotaType.A_ESCUADRA -> Unit
        }
    }

    private fun aplicarNuevaCotaCompositeSide(composite: Element.Composite, hit: CotaHit, valueCm: Float) {
        val contourIndex = hit.contourIndex ?: return
        val sideIndex = hit.sideIndex ?: return
        if (composite.template == TEMPLATE_F1 && contourIndex == 0) {
            aplicarNuevaCotaRecurrenteF1(composite, sideIndex, valueCm)
            return
        }
        // F2 (forma en "U"): los tres lados paralelos a la base (2 armes de arriba + fondo del
        // corte) suman la base. Editar la base reparte el cambio entre los tres; editar un
        // paralelo lo fija y reparte su diferencia entre los otros aún libres.
        if (composite.template == TEMPLATE_F2) {
            aplicarNuevaCotaRecurrenteF2(composite, contourIndex, sideIndex, valueCm)
            return
        }
        if (composite.template == TEMPLATE_F3) {
            aplicarNuevaCotaRecurrenteF3(composite, contourIndex, sideIndex, valueCm)
            return
        }
        if (composite.template == TEMPLATE_F4) {
            aplicarNuevaCotaRecurrenteF4(composite, contourIndex, sideIndex, valueCm)
            return
        }
        val contour = composite.contours.getOrNull(contourIndex) ?: return
        val n = contour.size
        if (n < 2 || sideIndex !in contour.indices) return

        // Si el propio lado está bloqueado, no se edita.
        if (ladoBloqueado(composite, contourIndex, sideIndex)) {
            Toast.makeText(context, "Ese lado está bloqueado 🔒", Toast.LENGTH_SHORT).show()
            return
        }
        val i0 = sideIndex
        val i1 = (sideIndex + 1) % n
        val p0 = contour[i0]; val p1 = contour[i1]
        val horizontal = abs(p1.x - p0.x) >= abs(p1.y - p0.y)
        val negativo = valueCm < 0f
        val largo = cmToPx(abs(valueCm)).coerceAtLeast(cmToPx(0.1f))

        // El SIGNO decide la dirección (absoluta): horizontal +→derecha / -→izquierda;
        // vertical +→arriba / -→abajo. Se mueve ese vértice y se ancla el opuesto.
        val idxMover: Int
        val idxAncla: Int
        if (horizontal) {
            val der = if (p0.x >= p1.x) i0 else i1
            val izq = if (der == i0) i1 else i0
            if (!negativo) { idxMover = der; idxAncla = izq } else { idxMover = izq; idxAncla = der }
        } else {
            val arriba = if (p0.y <= p1.y) i0 else i1
            val abajo = if (arriba == i0) i1 else i0
            if (!negativo) { idxMover = arriba; idxAncla = abajo } else { idxMover = abajo; idxAncla = arriba }
        }

        // Bloqueo: el vértice a mover está pinchado si su OTRO lado vecino está bloqueado.
        val moverPinchado =
            (idxMover == i0 && ladoBloqueado(composite, contourIndex, (sideIndex - 1 + n) % n)) ||
            (idxMover == i1 && ladoBloqueado(composite, contourIndex, (sideIndex + 1) % n))
        if (moverPinchado) {
            Toast.makeText(context, "Ese lado empuja hacia un lado bloqueado. Usa el signo contrario.", Toast.LENGTH_SHORT).show()
            return
        }

        val ancla = contour[idxAncla]
        val movil = contour[idxMover]
        if (horizontal) {
            val dir = if (movil.x >= ancla.x) 1f else -1f
            movil.x = ancla.x + dir * largo
            movil.y = ancla.y
        } else {
            val dir = if (movil.y >= ancla.y) 1f else -1f
            movil.y = ancla.y + dir * largo
            movil.x = ancla.x
        }
        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
        val bnds = boundsForElement(composite)
        composite.widthCm = pxToCm(bnds.width())
        composite.heightCm = pxToCm(bnds.height())
    }

    private fun crearRecurrenteF1(
        left: Float,
        top: Float,
        anchoCm: Float,
        altoCm: Float,
        corteAnchoCm: Float,
        corteAltoCm: Float
    ): Element.Composite {
        val totalW = cmToPx(anchoCm)
        val totalH = cmToPx(altoCm)
        val cutW = cmToPx(corteAnchoCm).coerceIn(cmToPx(5f), totalW - cmToPx(5f))
        val cutH = cmToPx(corteAltoCm).coerceIn(cmToPx(5f), totalH - cmToPx(5f))
        val contour = contourRecurrenteF1(left, top, totalW, totalH, cutW, cutH)
        val contours = mutableListOf(contour)
        return Element.Composite(
            path = pathFromContours(contours),
            widthCm = pxToCm(totalW),
            heightCm = pxToCm(totalH),
            contours = contours,
            sideCms = sideCmsForContours(contours),
            template = TEMPLATE_F1
        )
    }

    // F1 (rectángulo con esquina superior derecha cortada). Misma lógica de paralelos que F2, en
    // dos grupos independientes:
    //   - horizontal: paralelos {0 = tramo superior, 2 = fondo del corte}; completo = 4 (ancho).
    //   - vertical:   paralelos {1 = vertical del corte, 3 = lado bajo el corte}; completo = 5 (alto).
    // Editar el completo reparte proporcionalmente entre sus paralelos; editar un paralelo lo fija y
    // reparte al otro; una vez ambos fijados, el editado toma su valor y la figura se deforma.
    private fun aplicarNuevaCotaRecurrenteF1(composite: Element.Composite, sideIndex: Int, valueCm: Float) {
        val contour = composite.contours.firstOrNull()?.takeIf { it.size == 6 } ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val nuevo = cmToPx(kotlin.math.abs(valueCm)).coerceAtLeast(minSize)

        fun corto() = Toast.makeText(context, "Ese cambio dejaría un lado demasiado corto.", Toast.LENGTH_SHORT).show()
        fun fijado(lado: Int) {
            composite.bloqueados.add(ladoKey(0, lado))
            Toast.makeText(context, "Lado fijado 🔒", Toast.LENGTH_SHORT).show()
        }

        when (sideIndex) {
            0, 2 -> {
                val actuales = floatArrayOf(contour[1].x - contour[0].x, contour[3].x - contour[2].x)
                val idx = if (sideIndex == 0) 0 else 1
                val res = resolverParaleloEditado(actuales, idx, nuevo,
                    { i -> !ladoBloqueado(composite, 0, if (i == 0) 0 else 2) }, minSize) ?: return corto()
                aplicarHorizontalF1(contour, res.valores, res.irregular)
                fijado(sideIndex)
            }
            4 -> {
                val actuales = floatArrayOf(contour[1].x - contour[0].x, contour[3].x - contour[2].x)
                val v = distribuirProporcional(actuales, nuevo, minSize) ?: return corto()
                aplicarHorizontalF1(contour, v, irregular = false)
            }
            1, 3 -> {
                val actuales = floatArrayOf(contour[2].y - contour[1].y, contour[4].y - contour[3].y)
                val idx = if (sideIndex == 1) 0 else 1
                val res = resolverParaleloEditado(actuales, idx, nuevo,
                    { i -> !ladoBloqueado(composite, 0, if (i == 0) 1 else 3) }, minSize) ?: return corto()
                aplicarVerticalF1(contour, res.valores, res.irregular)
                fijado(sideIndex)
            }
            5 -> {
                val actuales = floatArrayOf(contour[2].y - contour[1].y, contour[4].y - contour[3].y)
                val v = distribuirProporcional(actuales, nuevo, minSize) ?: return corto()
                aplicarVerticalF1(contour, v, irregular = false)
            }
            else -> return
        }

        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
        val b = boundsForElement(composite)
        composite.widthCm = pxToCm(b.width())
        composite.heightCm = pxToCm(b.height())
    }

    // Recoloca los vértices horizontales de F1 (x). Con regular=true la base inferior sigue al total;
    // con regular=false la base queda fija y el costado derecho se inclina (deformación).
    private fun aplicarHorizontalF1(contour: MutableList<PointF>, valores: FloatArray, irregular: Boolean) {
        val left = contour[0].x
        val innerX = left + valores[0]        // fin del tramo superior / vertical del corte
        val outerX = innerX + valores[1]      // fin del fondo del corte
        contour[1].x = innerX
        contour[2].x = innerX
        contour[3].x = outerX
        if (!irregular) contour[4].x = outerX // base inferior derecha sigue al total
        // contour[0].x y contour[5].x (izquierda) se mantienen
    }

    // Recoloca los vértices verticales de F1 (y). Con regular=true la base inferior sigue al total;
    // con regular=false el lado izquierdo queda fijo y la base se inclina (deformación).
    private fun aplicarVerticalF1(contour: MutableList<PointF>, valores: FloatArray, irregular: Boolean) {
        val top = contour[0].y
        val cutY = top + valores[0]           // altura del fondo del corte
        val bottom = cutY + valores[1]
        contour[2].y = cutY
        contour[3].y = cutY
        contour[4].y = bottom                 // lado derecho inferior
        if (!irregular) contour[5].y = bottom // base izquierda sigue al total
        // contour[0].y y contour[1].y (borde superior) se mantienen
    }

    private fun contourRecurrenteF1(
        left: Float,
        top: Float,
        totalW: Float,
        totalH: Float,
        cutW: Float,
        cutH: Float
    ): MutableList<PointF> {
        val right = left + totalW
        val bottom = top + totalH
        val cutX = right - cutW
        val cutY = top + cutH
        return mutableListOf(
            PointF(left, top),
            PointF(cutX, top),
            PointF(cutX, cutY),
            PointF(right, cutY),
            PointF(right, bottom),
            PointF(left, bottom)
        )
    }

    private fun crearRecurrenteF2(
        left: Float,
        top: Float,
        anchoCm: Float,
        altoCm: Float,
        corteAnchoCm: Float,
        corteAltoCm: Float
    ): Element.Composite {
        val totalW = cmToPx(anchoCm)
        val totalH = cmToPx(altoCm)
        val cutW = cmToPx(corteAnchoCm).coerceIn(cmToPx(5f), totalW - cmToPx(10f))
        val cutH = cmToPx(corteAltoCm).coerceIn(cmToPx(5f), totalH - cmToPx(10f))
        val contours = contoursRecurrenteF2(left, top, totalW, totalH, cutW, cutH)
        return Element.Composite(
            path = pathFromContours(contours),
            widthCm = pxToCm(totalW),
            heightCm = pxToCm(totalH),
            contours = contours,
            sideCms = sideCmsForContours(contours),
            template = TEMPLATE_F2
        )
    }

    // Índices de lado (contorno de 8 puntos) de los tres lados paralelos a la base:
    //   0 = arme izquierdo (arriba)   2 = fondo del corte   4 = arme derecho (arriba)
    // Su suma es siempre igual a la base (lado 6). La base es el lado 6; los verticales
    // del corte son 1 y 3; los laterales (alto) son 5 y 7.
    private val F2_LADOS_PARALELOS = intArrayOf(0, 2, 4)

    private fun aplicarNuevaCotaRecurrenteF2(
        composite: Element.Composite,
        contourIndex: Int,
        sideIndex: Int,
        valueCm: Float
    ) {
        if (contourIndex != 0) return
        val contour = composite.contours.getOrNull(0)?.takeIf { it.size == 8 } ?: return
        // Mínimo mínimo: todos los lados aceptan medidas menores a 5; solo se evita 0/negativo.
        val minSize = cmToPx(0.1f)
        val top = contour[0].y
        val nuevo = cmToPx(kotlin.math.abs(valueCm)).coerceAtLeast(minSize)

        // Edición por mutación directa de vértices: así se conserva la geometría aunque la forma
        // haya quedado irregular (parte superior más ancha que la base).
        when (sideIndex) {
            0, 2, 4 -> if (!editarLadoParaleloF2(composite, sideIndex, nuevo, minSize)) return
            1, 3 -> {
                // Verticales del corte (se introducen hacia la forma), paralelos entre sí.
                // Si ninguno fue editado aún, ambos se mueven juntos (corte simétrico); una vez uno
                // fue editado, el otro puede tomar una profundidad distinta (fondo del corte inclinado).
                val totalH = contour[7].y - top
                val cutH = nuevo.coerceAtMost(totalH - minSize)
                val notchY = top + cutH
                val juntos = !ladoBloqueado(composite, 0, 1) && !ladoBloqueado(composite, 0, 3)
                if (sideIndex == 1 || juntos) contour[2].y = notchY
                if (sideIndex == 3 || juntos) contour[3].y = notchY
                composite.bloqueados.add(ladoKey(0, sideIndex))
            }
            5, 7 -> {
                // Alto (laterales): mueve la base inferior sin tocar la parte superior ni el corte.
                val cutH = contour[2].y - top
                val totalH = nuevo.coerceAtLeast(cutH + minSize)
                val bottomY = top + totalH
                contour[6].y = bottomY
                contour[7].y = bottomY
            }
            6 -> if (!editarBaseF2(composite, nuevo, minSize)) return
            else -> return
        }

        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
        val bnds = boundsForElement(composite)
        composite.widthCm = pxToCm(bnds.width())
        composite.heightCm = pxToCm(bnds.height())
    }

    // Edición de un lado paralelo a la base (armes de arriba o fondo del corte).
    //  - Si queda algún OTRO paralelo libre: fija el editado, reparte su diferencia entre los
    //    libres y la base inferior no cambia (la U se mantiene recta).
    //  - Si los otros dos ya están fijados: se permite igualmente; el editado toma su valor, los
    //    otros no se tocan y la base inferior queda FIJA, de modo que el total de arriba crece y
    //    la forma queda irregular (el lado editado sobresale, ese costado queda inclinado).
    // El lado editado siempre queda fijado. Un lado fijado sigue siendo editable directamente;
    // "fijado" solo significa que no se moverá al repartir la edición de otro lado.
    // ---- Lógica de paralelos reutilizable (extraída de F2, usada también por F1 y las demás) ----
    // Un grupo de lados "paralelos" normalmente suma un lado completo (ancho o alto). Al editar uno:
    //   - si queda algún otro paralelo libre, se reparte la diferencia entre los libres a partes
    //     iguales y el total no cambia (forma recta);
    //   - si los demás ya están fijados, el editado toma su valor y el total crece o mengua
    //     (forma deformada / irregular).
    private class ParaleloEdit(val valores: FloatArray, val irregular: Boolean)

    private fun resolverParaleloEditado(
        actuales: FloatArray,
        indice: Int,
        nuevo: Float,
        libre: (Int) -> Boolean,
        minSize: Float
    ): ParaleloEdit? {
        val valores = actuales.copyOf()
        val libres = actuales.indices.filter { it != indice && libre(it) }
        val irregular = libres.isEmpty()
        if (irregular) {
            valores[indice] = nuevo
        } else {
            val reparto = (nuevo - valores[indice]) / libres.size
            valores[indice] = nuevo
            libres.forEach { valores[it] = valores[it] - reparto }
        }
        return if (valores.any { it < minSize }) null else ParaleloEdit(valores, irregular)
    }

    // Al editar el lado "completo": los paralelos crecen proporcionalmente para sumar el nuevo total.
    private fun distribuirProporcional(actuales: FloatArray, nuevoTotal: Float, minSize: Float): FloatArray? {
        val suma = actuales.sum()
        if (suma <= 0f) return null
        val escala = nuevoTotal / suma
        val v = FloatArray(actuales.size) { actuales[it] * escala }
        return if (v.any { it < minSize }) null else v
    }

    private fun editarLadoParaleloF2(
        composite: Element.Composite,
        sideIndex: Int,
        nuevoPx: Float,
        minSize: Float
    ): Boolean {
        val contour = composite.contours[0]
        val actuales = floatArrayOf(
            contour[1].x - contour[0].x,   // paralelo 0 -> lado 0 (arme izq)
            contour[4].x - contour[1].x,   // paralelo 1 -> lado 2 (fondo del corte)
            contour[5].x - contour[4].x    // paralelo 2 -> lado 4 (arme der)
        )
        val p = when (sideIndex) { 0 -> 0; 2 -> 1; else -> 2 }
        val res = resolverParaleloEditado(actuales, p, nuevoPx,
            { i -> !ladoBloqueado(composite, 0, F2_LADOS_PARALELOS[i]) }, minSize)
        if (res == null) {
            Toast.makeText(context, "Ese cambio dejaría un lado demasiado corto.", Toast.LENGTH_SHORT).show()
            return false
        }
        val v = res.valores
        val bottomLeft = contour[7].x
        val bottomRight = contour[6].x
        if (!res.irregular) {
            // Reparto: la base inferior se mantiene, forma recta.
            reconstruirTopF2(contour, bottomLeft, v[0], v[1], v[2], regular = true)
        } else {
            // Todos los demás fijados: crece el total de arriba y la base inferior no cambia.
            val total = v[0] + v[1] + v[2]
            val leftTopX = when (sideIndex) {
                0 -> bottomRight - total                            // arme izq: sobresale a la izquierda
                4 -> bottomLeft                                     // arme der: sobresale a la derecha
                else -> (bottomLeft + bottomRight) / 2f - total / 2f // fondo: centrado
            }
            reconstruirTopF2(contour, leftTopX, v[0], v[1], v[2], regular = false)
        }
        composite.bloqueados.add(ladoKey(0, sideIndex))
        Toast.makeText(context, "Lado fijado 🔒", Toast.LENGTH_SHORT).show()
        return true
    }

    // Edición de la base: reparte el cambio entre los paralelos libres (o los tres si no hay
    // libres) y devuelve la forma a una U recta con ese ancho inferior.
    private fun editarBaseF2(composite: Element.Composite, nuevaBasePx: Float, minSize: Float): Boolean {
        val contour = composite.contours[0]
        val valores = floatArrayOf(
            contour[1].x - contour[0].x,
            contour[4].x - contour[1].x,
            contour[5].x - contour[4].x
        )
        val nuevaBase = nuevaBasePx.coerceAtLeast(minSize * 3f)
        val delta = nuevaBase - valores.sum()
        val libres = (0..2).filter { !ladoBloqueado(composite, 0, F2_LADOS_PARALELOS[it]) }
        val destino = if (libres.isEmpty()) listOf(0, 1, 2) else libres
        val reparto = delta / destino.size
        destino.forEach { valores[it] = valores[it] + reparto }
        if (valores.any { it < minSize }) {
            Toast.makeText(context, "Esa base dejaría un lado demasiado corto.", Toast.LENGTH_SHORT).show()
            return false
        }
        reconstruirTopF2(contour, contour[7].x, valores[0], valores[1], valores[2], regular = true)
        return true
    }

    // Recoloca la parte superior (vértices 0..5) a partir de un origen izquierdo y los tres anchos
    // paralelos, conservando la altura del corte (y de p2/p3). Con regular=true la base inferior
    // (p6/p7) sigue a la parte superior; con regular=false la base queda intacta (forma irregular).
    private fun reconstruirTopF2(
        contour: MutableList<PointF>,
        leftTopX: Float,
        armeIzq: Float,
        fondo: Float,
        armeDer: Float,
        regular: Boolean
    ) {
        val topY = contour[0].y
        val notchLeft = leftTopX + armeIzq
        val notchRight = notchLeft + fondo
        val topRight = notchRight + armeDer
        contour[0].x = leftTopX;   contour[0].y = topY
        contour[1].x = notchLeft;  contour[1].y = topY
        contour[2].x = notchLeft   // y (fondo del corte) se conserva
        contour[3].x = notchRight
        contour[4].x = notchRight; contour[4].y = topY
        contour[5].x = topRight;   contour[5].y = topY
        if (regular) {
            contour[7].x = leftTopX
            contour[6].x = topRight
        }
    }

    private fun contoursRecurrenteF2(
        left: Float,
        top: Float,
        totalW: Float,
        totalH: Float,
        cutW: Float,
        cutH: Float
    ): MutableList<MutableList<PointF>> {
        val armeIzq = ((totalW - cutW) / 2f).coerceAtLeast(cmToPx(5f))
        val armeDer = (totalW - cutW - armeIzq).coerceAtLeast(cmToPx(5f))
        return contoursRecurrenteF2Asimetrico(left, top, armeIzq, cutW, armeDer, cutH, totalH)
    }

    private fun contoursRecurrenteF2Asimetrico(
        left: Float,
        top: Float,
        armeIzq: Float,
        fondo: Float,
        armeDer: Float,
        cutH: Float,
        totalH: Float
    ): MutableList<MutableList<PointF>> {
        val notchLeft = left + armeIzq
        val notchRight = notchLeft + fondo
        val right = notchRight + armeDer
        val bottom = top + totalH
        val notchBottom = top + cutH
        return mutableListOf(
            mutableListOf(
                PointF(left, top),
                PointF(notchLeft, top),
                PointF(notchLeft, notchBottom),
                PointF(notchRight, notchBottom),
                PointF(notchRight, top),
                PointF(right, top),
                PointF(right, bottom),
                PointF(left, bottom)
            )
        )
    }

    private fun crearRecurrenteF3(
        left: Float,
        top: Float,
        anchoSuperiorCm: Float,
        anchoInferiorCm: Float,
        altoCm: Float
    ): Element.Composite {
        val topW = cmToPx(anchoSuperiorCm)
        val bottomW = cmToPx(anchoInferiorCm).coerceAtMost(topW - cmToPx(5f))
        val totalH = cmToPx(altoCm)
        val contours = mutableListOf(contourRecurrenteF3(left, top, topW, bottomW, totalH))
        return Element.Composite(
            path = pathFromContours(contours),
            widthCm = pxToCm(topW),
            heightCm = pxToCm(totalH),
            contours = contours,
            sideCms = sideCmsForContours(contours),
            template = TEMPLATE_F3
        )
    }

    private fun aplicarNuevaCotaRecurrenteF3(
        composite: Element.Composite,
        contourIndex: Int,
        sideIndex: Int,
        valueCm: Float
    ) {
        if (contourIndex != 0) return
        val contour = composite.contours.getOrNull(0)?.takeIf { it.size == 4 } ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val left = contour.minOf { it.x }
        val top = contour.minOf { it.y }
        var topW = (contour[1].x - contour[0].x).coerceAtLeast(minSize * 2f)
        var bottomW = (contour[2].x - contour[3].x).coerceAtLeast(minSize)
        var totalH = (contour[3].y - contour[0].y).coerceAtLeast(minSize)
        val nuevo = cmToPx(valueCm).coerceAtLeast(minSize)

        when (sideIndex) {
            0 -> topW = nuevo.coerceAtLeast(bottomW + minSize)
            1 -> {
                val diagonal = nuevo
                val diferencia = (topW - bottomW).coerceAtLeast(minSize)
                if (diagonal > diferencia + minSize) {
                    totalH = kotlin.math.sqrt((diagonal * diagonal - diferencia * diferencia).toDouble()).toFloat()
                } else {
                    totalH = minSize
                    val nuevaDiferencia = kotlin.math.sqrt((diagonal * diagonal - totalH * totalH).coerceAtLeast(minSize * minSize).toDouble()).toFloat()
                    bottomW = (topW - nuevaDiferencia)
                        .coerceAtLeast(minSize)
                }
            }
            2 -> bottomW = nuevo.coerceAtMost(topW - minSize)
            3 -> totalH = nuevo
            else -> return
        }

        val updated = contourRecurrenteF3(left, top, topW, bottomW, totalH)
        contour.clear()
        contour.addAll(updated)
        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
    }

    private fun contourRecurrenteF3(
        left: Float,
        top: Float,
        topW: Float,
        bottomW: Float,
        totalH: Float
    ): MutableList<PointF> {
        val bottom = top + totalH
        return mutableListOf(
            PointF(left, top),
            PointF(left + topW, top),
            PointF(left + bottomW, bottom),
            PointF(left, bottom)
        )
    }

    private fun crearRecurrenteF4(
        centralLeft: Float,
        top: Float,
        centralAnchoCm: Float,
        centralAltoCm: Float,
        agregadoAnchoCm: Float,
        agregadoAltoCm: Float,
        conIzquierdo: Boolean,
        conDerecho: Boolean
    ): Element.Composite {
        val centralW = cmToPx(centralAnchoCm)
        val centralH = cmToPx(centralAltoCm)
        val leftW = if (conIzquierdo) cmToPx(agregadoAnchoCm) else 0f
        val rightW = if (conDerecho) cmToPx(agregadoAnchoCm) else 0f
        val addonH = cmToPx(agregadoAltoCm).coerceAtMost(centralH - cmToPx(5f))
        val contours = mutableListOf(contourRecurrenteF4(centralLeft, top, centralW, centralH, leftW, rightW, addonH))
        return Element.Composite(
            path = pathFromContours(contours),
            widthCm = pxToCm(centralW + leftW + rightW),
            heightCm = pxToCm(centralH),
            contours = contours,
            sideCms = sideCmsForContours(contours),
            template = TEMPLATE_F4
        )
    }

    private fun aplicarNuevaCotaRecurrenteF4(
        composite: Element.Composite,
        contourIndex: Int,
        sideIndex: Int,
        valueCm: Float
    ) {
        if (contourIndex != 0) return
        val dims = dimsRecurrenteF4(composite.contours.getOrNull(0) ?: return) ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        var centralW = dims.centralW.coerceAtLeast(minSize)
        var centralH = dims.centralH.coerceAtLeast(minSize * 2f)
        var leftW = dims.leftW
        var rightW = dims.rightW
        var addonH = dims.addonH.coerceIn(minSize, centralH - minSize)
        val nuevo = cmToPx(valueCm).coerceAtLeast(minSize)

        when {
            leftW > 0f && rightW > 0f -> when (sideIndex) {
                0 -> centralW = (nuevo - leftW - rightW).coerceAtLeast(minSize)
                1, 7 -> addonH = nuevo.coerceAtMost(centralH - minSize)
                2 -> rightW = nuevo
                3, 5 -> centralH = (addonH + nuevo).coerceAtLeast(addonH + minSize)
                4 -> centralW = nuevo
                6 -> leftW = nuevo
                else -> return
            }
            leftW > 0f -> when (sideIndex) {
                0 -> centralW = (nuevo - leftW).coerceAtLeast(minSize)
                1 -> centralH = nuevo.coerceAtLeast(addonH + minSize)
                2 -> centralW = nuevo
                3 -> centralH = (addonH + nuevo).coerceAtLeast(addonH + minSize)
                4 -> leftW = nuevo
                5 -> addonH = nuevo.coerceAtMost(centralH - minSize)
                else -> return
            }
            rightW > 0f -> when (sideIndex) {
                0 -> centralW = (nuevo - rightW).coerceAtLeast(minSize)
                1 -> addonH = nuevo.coerceAtMost(centralH - minSize)
                2 -> rightW = nuevo
                3 -> centralH = (addonH + nuevo).coerceAtLeast(addonH + minSize)
                4 -> centralW = nuevo
                5 -> centralH = nuevo.coerceAtLeast(addonH + minSize)
                else -> return
            }
            else -> return
        }

        addonH = addonH.coerceAtMost(centralH - minSize)
        val contour = composite.contours.first()
        contour.clear()
        contour.addAll(contourRecurrenteF4(dims.centralLeft, dims.top, centralW, centralH, leftW, rightW, addonH))
        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
    }

    private fun aplicarAlturaTotalRecurrenteF4(composite: Element.Composite, valueCm: Float) {
        val dims = dimsRecurrenteF4(composite.contours.getOrNull(0) ?: return) ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val centralH = cmToPx(valueCm).coerceAtLeast(dims.addonH + minSize)
        val addonH = dims.addonH.coerceAtMost(centralH - minSize)
        val contour = composite.contours.first()
        contour.clear()
        contour.addAll(
            contourRecurrenteF4(
                centralLeft = dims.centralLeft,
                top = dims.top,
                centralW = dims.centralW,
                centralH = centralH,
                leftW = dims.leftW,
                rightW = dims.rightW,
                addonH = addonH
            )
        )
        rebuildCompositePath(composite)
        refreshCompositeSides(composite)
    }

    private fun dimsRecurrenteF4(contour: List<PointF>): RecurrenteF4Dims? {
        if (contour.size == 8) {
            val centralLeft = contour[6].x
            val centralRight = contour[3].x
            return RecurrenteF4Dims(
                centralLeft = centralLeft,
                top = contour[0].y,
                centralW = centralRight - centralLeft,
                centralH = contour[5].y - contour[0].y,
                leftW = centralLeft - contour[0].x,
                rightW = contour[1].x - centralRight,
                addonH = contour[3].y - contour[0].y
            )
        }
        if (contour.size == 6 && contour[0].x < contour[5].x) {
            val centralLeft = contour[3].x
            val centralRight = contour[1].x
            return RecurrenteF4Dims(
                centralLeft = centralLeft,
                top = contour[0].y,
                centralW = centralRight - centralLeft,
                centralH = contour[3].y - contour[0].y,
                leftW = centralLeft - contour[0].x,
                rightW = 0f,
                addonH = contour[4].y - contour[0].y
            )
        }
        if (contour.size == 6) {
            val centralLeft = contour[5].x
            val centralRight = contour[3].x
            return RecurrenteF4Dims(
                centralLeft = centralLeft,
                top = contour[0].y,
                centralW = centralRight - centralLeft,
                centralH = contour[4].y - contour[0].y,
                leftW = 0f,
                rightW = contour[1].x - centralRight,
                addonH = contour[2].y - contour[0].y
            )
        }
        return null
    }

    private fun contourRecurrenteF4(
        centralLeft: Float,
        top: Float,
        centralW: Float,
        centralH: Float,
        leftW: Float,
        rightW: Float,
        addonH: Float
    ): MutableList<PointF> {
        val centralRight = centralLeft + centralW
        val bottom = top + centralH
        val shoulderY = top + addonH
        return when {
            leftW > 0f && rightW > 0f -> mutableListOf(
                PointF(centralLeft - leftW, top),
                PointF(centralRight + rightW, top),
                PointF(centralRight + rightW, shoulderY),
                PointF(centralRight, shoulderY),
                PointF(centralRight, bottom),
                PointF(centralLeft, bottom),
                PointF(centralLeft, shoulderY),
                PointF(centralLeft - leftW, shoulderY)
            )
            leftW > 0f -> mutableListOf(
                PointF(centralLeft - leftW, top),
                PointF(centralRight, top),
                PointF(centralRight, bottom),
                PointF(centralLeft, bottom),
                PointF(centralLeft, shoulderY),
                PointF(centralLeft - leftW, shoulderY)
            )
            rightW > 0f -> mutableListOf(
                PointF(centralLeft, top),
                PointF(centralRight + rightW, top),
                PointF(centralRight + rightW, shoulderY),
                PointF(centralRight, shoulderY),
                PointF(centralRight, bottom),
                PointF(centralLeft, bottom)
            )
            else -> rectContour(RectF(centralLeft, top, centralRight, bottom))
        }
    }

    private fun crearRecurrenteF5(
        left: Float,
        top: Float,
        anchoCm: Float,
        altoCm: Float
    ): Element.Composite {
        val rectW = cmToPx(anchoCm)
        val rectH = cmToPx(altoCm)
        val contour = rectContour(RectF(left, top, left + rectW, top + rectH))
        val contours = mutableListOf(contour)
        val dims = RecurrenteF5Dims(left, top, rectW, rectH, rectW / 4f)
        return Element.Composite(
            path = pathRecurrenteF5(dims),
            widthCm = pxToCm(rectW),
            heightCm = pxToCm(rectH),
            contours = contours,
            sideCms = sideCmsRecurrenteF5(dims),
            template = TEMPLATE_F5
        )
    }

    private fun aplicarMedidasRecurrenteF5(composite: Element.Composite, anchoCm: Float?, altoCm: Float?) {
        val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val newW = anchoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectW
        val newH = altoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectH
        val centerX = dims.left + dims.rectW / 2f
        val left = centerX - newW / 2f
        val newFlecha = dims.flecha.coerceAtMost(newW / 2f)
        val newDims = RecurrenteF5Dims(left, dims.top, newW, newH, newFlecha)
        val contour = composite.contours.first()
        contour.clear()
        contour.addAll(rectContour(RectF(left, dims.top, left + newW, dims.top + newH)))
        composite.path.set(pathRecurrenteF5(newDims))
        composite.widthCm = pxToCm(newW)
        composite.heightCm = pxToCm(newH)
        setSideCmsRecurrenteF5(composite, newDims)
    }

    private fun aplicarDesarrolloRecurrenteF5(composite: Element.Composite, desarrolloCm: Float) {
        val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val desired = cmToPx(desarrolloCm).coerceAtLeast(cmToPx(1f))
        val minFlecha = cmToPx(1f)
        val maxFlecha = (dims.rectW / 2f).coerceAtLeast(minFlecha)
        var low = minFlecha
        var high = maxFlecha
        repeat(32) {
            val mid = (low + high) / 2f
            if (longitudMediaElipsePx(dims.rectW, mid) < desired) low = mid else high = mid
        }
        aplicarFlechaRecurrenteF5(composite, pxToCm((low + high) / 2f))
    }

    private fun aplicarFlechaRecurrenteF5(composite: Element.Composite, flechaCm: Float) {
        val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val newFlecha = cmToPx(flechaCm).coerceIn(cmToPx(1f), dims.rectW / 2f)
        val newDims = dims.copy(flecha = newFlecha)
        composite.path.set(pathRecurrenteF5(newDims))
        setSideCmsRecurrenteF5(composite, newDims)
    }

    private fun dimsRecurrenteF5(
        contour: List<PointF>,
        sideCms: List<List<Float>> = emptyList()
    ): RecurrenteF5Dims? {
        if (contour.size < 4) return null
        val left = contour.minOf { it.x }
        val top = contour.minOf { it.y }
        val right = contour.maxOf { it.x }
        val bottom = contour.maxOf { it.y }
        if (right <= left || bottom <= top) return null
        val rectW = right - left
        val flechaCm = sideCms.getOrNull(0)?.getOrNull(2)?.takeIf { it > 0f }
        val flecha = flechaCm?.let { cmToPx(it) } ?: rectW / 4f
        return RecurrenteF5Dims(left, top, rectW, bottom - top, flecha.coerceIn(cmToPx(1f), rectW / 2f))
    }

    private fun pathRecurrenteF5(dims: RecurrenteF5Dims): Path {
        val rect = RectF(dims.left, dims.top, dims.left + dims.rectW, dims.top + dims.rectH)
        val ellipseH = dims.flecha * 2f
        val topOval = RectF(dims.left, dims.top - ellipseH / 2f, dims.left + dims.rectW, dims.top + ellipseH / 2f)
        val bottomOval = RectF(dims.left, dims.top + dims.rectH - ellipseH / 2f, dims.left + dims.rectW, dims.top + dims.rectH + ellipseH / 2f)
        val result = Path().apply { addRect(rect, Path.Direction.CW) }
        val topPath = Path().apply { addOval(topOval, Path.Direction.CW) }
        val bottomPath = Path().apply { addOval(bottomOval, Path.Direction.CW) }
        result.op(topPath, Path.Op.UNION)
        result.op(bottomPath, Path.Op.DIFFERENCE)
        return result
    }

    private fun longitudMediaElipsePx(width: Float, flecha: Float): Float {
        val a = width / 2f
        val b = flecha
        val full = Math.PI * (3.0 * (a + b) - kotlin.math.sqrt(((3f * a + b) * (a + 3f * b)).toDouble()))
        return (full / 2.0).toFloat()
    }

    private fun sideCmsRecurrenteF5(dims: RecurrenteF5Dims): MutableList<MutableList<Float>> {
        return mutableListOf(
            mutableListOf(
                pxToCm(dims.rectW),
                pxToCm(dims.rectH),
                pxToCm(dims.flecha)
            )
        )
    }

    private fun setSideCmsRecurrenteF5(composite: Element.Composite, dims: RecurrenteF5Dims) {
        composite.sideCms.clear()
        composite.sideCms.addAll(sideCmsRecurrenteF5(dims))
    }

    private fun crearRecurrenteF6(
        left: Float,
        top: Float,
        anchoCm: Float,
        altoCm: Float,
        radioCm: Float
    ): Element.Composite {
        val rectW = cmToPx(anchoCm)
        val rectH = cmToPx(altoCm)
        val radius = cmToPx(radioCm).coerceIn(cmToPx(1f), minOf(rectW, rectH) / 2f)
        val contour = rectContour(RectF(left, top, left + rectW, top + rectH))
        val contours = mutableListOf(contour)
        val dims = RecurrenteF6Dims(left, top, rectW, rectH, radius)
        return Element.Composite(
            path = pathRecurrenteF6(dims),
            widthCm = pxToCm(rectW),
            heightCm = pxToCm(rectH),
            contours = contours,
            sideCms = sideCmsRecurrenteF6(dims),
            template = TEMPLATE_F6
        )
    }

    private fun aplicarMedidasRecurrenteF6(composite: Element.Composite, anchoCm: Float?, altoCm: Float?) {
        val dims = dimsRecurrenteF6(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val newW = anchoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectW
        val newH = altoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectH
        val centerX = dims.left + dims.rectW / 2f
        val left = centerX - newW / 2f
        val newRadius = dims.radius.coerceAtMost(minOf(newW, newH) / 2f)
        setRecurrenteF6(composite, RecurrenteF6Dims(left, dims.top, newW, newH, newRadius))
    }

    private fun aplicarRadioRecurrenteF6(composite: Element.Composite, radioCm: Float) {
        val dims = dimsRecurrenteF6(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val radius = cmToPx(radioCm).coerceIn(cmToPx(1f), minOf(dims.rectW, dims.rectH) / 2f)
        setRecurrenteF6(composite, dims.copy(radius = radius))
    }

    private fun setRecurrenteF6(composite: Element.Composite, dims: RecurrenteF6Dims) {
        val contour = composite.contours.first()
        contour.clear()
        contour.addAll(rectContour(RectF(dims.left, dims.top, dims.left + dims.rectW, dims.top + dims.rectH)))
        composite.path.set(pathRecurrenteF6(dims))
        composite.widthCm = pxToCm(dims.rectW)
        composite.heightCm = pxToCm(dims.rectH)
        setSideCmsRecurrenteF6(composite, dims)
    }

    private fun dimsRecurrenteF6(
        contour: List<PointF>,
        sideCms: List<List<Float>> = emptyList()
    ): RecurrenteF6Dims? {
        if (contour.size < 4) return null
        val left = contour.minOf { it.x }
        val top = contour.minOf { it.y }
        val right = contour.maxOf { it.x }
        val bottom = contour.maxOf { it.y }
        if (right <= left || bottom <= top) return null
        val rectW = right - left
        val rectH = bottom - top
        val radiusCm = sideCms.getOrNull(0)?.getOrNull(2)?.takeIf { it > 0f }
        val radius = radiusCm?.let { cmToPx(it) } ?: minOf(rectW, rectH) * 0.18f
        return RecurrenteF6Dims(left, top, rectW, rectH, radius.coerceIn(cmToPx(1f), minOf(rectW, rectH) / 2f))
    }

    private fun pathRecurrenteF6(dims: RecurrenteF6Dims): Path {
        return Path().apply {
            addRoundRect(
                RectF(dims.left, dims.top, dims.left + dims.rectW, dims.top + dims.rectH),
                dims.radius,
                dims.radius,
                Path.Direction.CW
            )
        }
    }

    private fun sideCmsRecurrenteF6(dims: RecurrenteF6Dims): MutableList<MutableList<Float>> {
        return mutableListOf(
            mutableListOf(
                pxToCm(dims.rectW),
                pxToCm(dims.rectH),
                pxToCm(dims.radius)
            )
        )
    }

    private fun setSideCmsRecurrenteF6(composite: Element.Composite, dims: RecurrenteF6Dims) {
        composite.sideCms.clear()
        composite.sideCms.addAll(sideCmsRecurrenteF6(dims))
    }

    private fun roundedDimsFromElement(element: Element): RoundedCornerDims? {
        return when (element) {
            is Element.Shape -> {
                if (element.tool != Tool.RECTANGLE) return null
                val bounds = boundsForElement(element)
                RoundedCornerDims(bounds.left, bounds.top, bounds.width(), bounds.height(), mutableListOf(0f, 0f, 0f, 0f))
            }
            is Element.Composite -> when (element.template) {
                TEMPLATE_ROUNDED -> dimsRoundedCorners(element.contours.firstOrNull() ?: return null, element.sideCms)
                TEMPLATE_F6 -> {
                    val dims = dimsRecurrenteF6(element.contours.firstOrNull() ?: return null, element.sideCms) ?: return null
                    RoundedCornerDims(
                        dims.left,
                        dims.top,
                        dims.rectW,
                        dims.rectH,
                        mutableListOf(dims.radius, dims.radius, dims.radius, dims.radius)
                    )
                }
                else -> {
                    val contour = element.contours.firstOrNull()?.takeIf { it.size == 4 } ?: return null
                    val bounds = RectF()
                    pathFromContours(listOf(contour)).computeBounds(bounds, true)
                    RoundedCornerDims(bounds.left, bounds.top, bounds.width(), bounds.height(), mutableListOf(0f, 0f, 0f, 0f))
                }
            }
            is Element.Freehand -> null
            is Element.Group -> null
            is Element.TextLabel -> null
            is Element.InfoBox -> null
            is Element.Symbol -> null
        }
    }

    private fun crearRectanguloConEsquinaRedondeada(rect: RectF, corner: Int, radioCm: Float): Element.Composite {
        return crearRectanguloConEsquinasRedondeadas(rect, setOf(corner), radioCm)
    }

    private fun crearRectanguloConEsquinasRedondeadas(rect: RectF, corners: Set<Int>, radioCm: Float): Element.Composite {
        val maxRadius = minOf(rect.width(), rect.height()) / 2f
        val radii = mutableListOf(0f, 0f, 0f, 0f)
        val radius = cmToPx(radioCm).coerceIn(cmToPx(1f), maxRadius)
        corners.filter { it in 0..3 }.forEach { corner -> radii[corner] = radius }
        val dims = RoundedCornerDims(rect.left, rect.top, rect.width(), rect.height(), radii)
        val contours = mutableListOf(rectContour(rect))
        return Element.Composite(
            path = pathRoundedCorners(dims),
            widthCm = pxToCm(rect.width()),
            heightCm = pxToCm(rect.height()),
            contours = contours,
            sideCms = sideCmsRoundedCorners(dims),
            template = TEMPLATE_ROUNDED
        )
    }

    private fun aplicarMedidasRoundedCorners(composite: Element.Composite, anchoCm: Float?, altoCm: Float?) {
        val dims = dimsRoundedCorners(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val minSize = cmToPx(0.1f)  // todos los lados aceptan medidas menores a 5
        val newW = anchoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectW
        val newH = altoCm?.let { cmToPx(it).coerceAtLeast(minSize) } ?: dims.rectH
        val centerX = dims.left + dims.rectW / 2f
        val left = centerX - newW / 2f
        val maxRadius = minOf(newW, newH) / 2f
        val updated = RoundedCornerDims(
            left = left,
            top = dims.top,
            rectW = newW,
            rectH = newH,
            radii = dims.radii.map { it.coerceAtMost(maxRadius) }.toMutableList()
        )
        setRoundedCorners(composite, updated)
    }

    private fun aplicarRadioRoundedCorner(composite: Element.Composite, corner: Int, radioCm: Float) {
        if (corner !in 0..3) return
        val dims = dimsRoundedCorners(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
        val maxRadius = minOf(dims.rectW, dims.rectH) / 2f
        dims.radii[corner] = cmToPx(radioCm).coerceIn(cmToPx(1f), maxRadius)
        setRoundedCorners(composite, dims)
    }

    private fun setRoundedCorners(composite: Element.Composite, dims: RoundedCornerDims) {
        val contour = composite.contours.first()
        contour.clear()
        contour.addAll(rectContour(RectF(dims.left, dims.top, dims.left + dims.rectW, dims.top + dims.rectH)))
        composite.path.set(pathRoundedCorners(dims))
        composite.widthCm = pxToCm(dims.rectW)
        composite.heightCm = pxToCm(dims.rectH)
        setSideCmsRoundedCorners(composite, dims)
    }

    private fun dimsRoundedCorners(
        contour: List<PointF>,
        sideCms: List<List<Float>> = emptyList()
    ): RoundedCornerDims? {
        if (contour.size < 4) return null
        val left = contour.minOf { it.x }
        val top = contour.minOf { it.y }
        val right = contour.maxOf { it.x }
        val bottom = contour.maxOf { it.y }
        if (right <= left || bottom <= top) return null
        val rectW = right - left
        val rectH = bottom - top
        val maxRadius = minOf(rectW, rectH) / 2f
        val stored = sideCms.getOrNull(0).orEmpty()
        val radii = (0..3).map { i ->
            stored.getOrNull(i + 2)?.let { cmToPx(it) }?.coerceIn(0f, maxRadius) ?: 0f
        }.toMutableList()
        return RoundedCornerDims(left, top, rectW, rectH, radii)
    }

    private fun pathRoundedCorners(dims: RoundedCornerDims): Path {
        val r = dims.radii.map { it.coerceIn(0f, minOf(dims.rectW, dims.rectH) / 2f) }
        val left = dims.left
        val top = dims.top
        val right = dims.left + dims.rectW
        val bottom = dims.top + dims.rectH
        return Path().apply {
            moveTo(left + r[0], top)
            lineTo(right - r[1], top)
            if (r[1] > 0f) quadTo(right, top, right, top + r[1]) else lineTo(right, top)
            lineTo(right, bottom - r[2])
            if (r[2] > 0f) quadTo(right, bottom, right - r[2], bottom) else lineTo(right, bottom)
            lineTo(left + r[3], bottom)
            if (r[3] > 0f) quadTo(left, bottom, left, bottom - r[3]) else lineTo(left, bottom)
            lineTo(left, top + r[0])
            if (r[0] > 0f) quadTo(left, top, left + r[0], top) else lineTo(left, top)
            close()
        }
    }

    private fun sideCmsRoundedCorners(dims: RoundedCornerDims): MutableList<MutableList<Float>> {
        return mutableListOf(
            mutableListOf(
                pxToCm(dims.rectW),
                pxToCm(dims.rectH),
                pxToCm(dims.radii[0]),
                pxToCm(dims.radii[1]),
                pxToCm(dims.radii[2]),
                pxToCm(dims.radii[3])
            )
        )
    }

    private fun setSideCmsRoundedCorners(composite: Element.Composite, dims: RoundedCornerDims) {
        composite.sideCms.clear()
        composite.sideCms.addAll(sideCmsRoundedCorners(dims))
    }

    private fun rebuildCompositePath(composite: Element.Composite) {
        if (composite.template == TEMPLATE_F5) {
            val dims = dimsRecurrenteF5(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
            composite.path.set(pathRecurrenteF5(dims))
            composite.widthCm = pxToCm(dims.rectW)
            composite.heightCm = pxToCm(dims.rectH)
            setSideCmsRecurrenteF5(composite, dims)
            return
        }
        if (composite.template == TEMPLATE_F6) {
            val dims = dimsRecurrenteF6(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
            composite.path.set(pathRecurrenteF6(dims))
            composite.widthCm = pxToCm(dims.rectW)
            composite.heightCm = pxToCm(dims.rectH)
            setSideCmsRecurrenteF6(composite, dims)
            return
        }
        if (composite.template == TEMPLATE_ROUNDED) {
            val dims = dimsRoundedCorners(composite.contours.firstOrNull() ?: return, composite.sideCms) ?: return
            composite.path.set(pathRoundedCorners(dims))
            composite.widthCm = pxToCm(dims.rectW)
            composite.heightCm = pxToCm(dims.rectH)
            setSideCmsRoundedCorners(composite, dims)
            return
        }
        composite.path.reset()
        for (contour in composite.contours) {
            if (contour.size < 2) continue
            composite.path.moveTo(contour.first().x, contour.first().y)
            for (i in 1 until contour.size) {
                composite.path.lineTo(contour[i].x, contour[i].y)
            }
            composite.path.close()
        }
        val bounds = boundsForElement(composite)
        composite.widthCm = pxToCm(bounds.width())
        composite.heightCm = pxToCm(bounds.height())
    }

    private fun transformCompositePoints(composite: Element.Composite, matrix: Matrix) {
        val values = FloatArray(2)
        composite.contours.forEach { contour ->
            contour.forEach { point ->
                values[0] = point.x
                values[1] = point.y
                matrix.mapPoints(values)
                point.x = values[0]
                point.y = values[1]
            }
        }
    }

    private fun refreshCompositeSides(composite: Element.Composite) {
        composite.sideCms.clear()
        composite.sideCms.addAll(sideCmsForContours(composite.contours))
        // Con la forma ya completa, cualquier otro cambio del dibujo (arrastrarla, escalarla) manda
        // sobre lo anotado: las cotas siguen al dibujo como siempre. Mientras faltan medidas NO se
        // toca lo anotado, que es justo lo que el usuario acaba de escribir.
        if (medidasCompletas(composite)) sincronizarDeclarados(composite)
    }

    /** Toma como anotadas las medidas que hoy tiene el dibujo. */
    private fun sincronizarDeclarados(c: Element.Composite) {
        c.declarados.clear()
        c.contours.forEachIndexed { ci, cont ->
            cont.indices.forEach { si ->
                c.sideCms.getOrNull(ci)?.getOrNull(si)?.let { c.declarados[ladoKey(ci, si)] = it }
            }
        }
    }

    /**
     * Lleva [esquina] a la distancia pedida desde [ancla], por la línea que hoy forman [ancla] y
     * [haciaDonde]. Si ese lado está aplastado no hay dirección que seguir y se usa [pordefecto].
     */
    /** Cuánto hay que estirar para que ese tramo pase a medir lo escrito. */
    private fun factorEstirado(actualPx: Float, valueCm: Float): Float {
        if (actualPx < 0.5f) return 1f
        return (cmToPx(abs(valueCm)) / actualPx).coerceAtLeast(0.001f)
    }

    /** Estira la figura entera —caja y vértices— desde [pivote]. */
    private fun estirarShape(shape: Element.Shape, fx: Float, fy: Float, pivote: PointF) {
        listOf(
            shape.start, shape.end,
            shape.topLeft, shape.topRight, shape.bottomRight, shape.bottomLeft
        ).forEach { punto ->
            punto.x = pivote.x + (punto.x - pivote.x) * fx
            punto.y = pivote.y + (punto.y - pivote.y) * fy
        }
    }

    private fun moverEsquina(
        esquina: PointF,
        ancla: PointF,
        haciaDonde: PointF,
        pordefecto: PointF,
        valueCm: Float
    ) {
        val dx = haciaDonde.x - ancla.x
        val dy = haciaDonde.y - ancla.y
        val largo = hypot(dx.toDouble(), dy.toDouble()).toFloat()
        val ux = if (largo < 0.5f) pordefecto.x else dx / largo
        val uy = if (largo < 0.5f) pordefecto.y else dy / largo
        val nuevo = cmToPx(valueCm)
        esquina.set(ancla.x + ux * nuevo, ancla.y + uy * nuevo)
    }

    private fun actualizarBoundsRectangulo(shape: Element.Shape) {
        shape.rect.set(
            minOf(shape.topLeft.x, shape.topRight.x, shape.bottomRight.x, shape.bottomLeft.x),
            minOf(shape.topLeft.y, shape.topRight.y, shape.bottomRight.y, shape.bottomLeft.y),
            maxOf(shape.topLeft.x, shape.topRight.x, shape.bottomRight.x, shape.bottomLeft.x),
            maxOf(shape.topLeft.y, shape.topRight.y, shape.bottomRight.y, shape.bottomLeft.y)
        )
        // Los cuatro lados se releen del dibujo: mover una esquina cambia también el lado vecino, y
        // su cota tiene que decir lo que ese lado mide AHORA, no lo que medía antes.
        shape.topCm = pxToCm(distancia(shape.topLeft, shape.topRight))
        shape.rightCm = pxToCm(distancia(shape.topRight, shape.bottomRight))
        shape.bottomCm = pxToCm(distancia(shape.bottomLeft, shape.bottomRight))
        shape.leftCm = pxToCm(distancia(shape.topLeft, shape.bottomLeft))
        shape.widthCm = maxOf(shape.topCm, shape.bottomCm)
        shape.heightCm = maxOf(shape.leftCm, shape.rightCm)
        shape.start.x = shape.rect.left
        shape.start.y = shape.rect.top
        shape.end.x = shape.rect.right
        shape.end.y = shape.rect.bottom
    }

    private fun fijarBaseRectangulo(shape: Element.Shape) {
        val baseY = maxOf(shape.bottomLeft.y, shape.bottomRight.y)
        shape.bottomLeft.y = baseY
        shape.bottomRight.y = baseY
    }

    private fun actualizarTrazo(x: Float, y: Float) {
        when (herramienta) {
            Tool.NONE, Tool.NODO -> Unit
            Tool.FREEHAND, Tool.MAGNET_PEN -> trazoActual.lineTo(x, y)
            Tool.RECTANGLE -> {
                trazoActual.reset()
                trazoActual.addRect(rectFrom(startPoint, x, y), Path.Direction.CW)
            }
            Tool.TRIANGLE -> {
                val r = rectFrom(startPoint, x, y)
                trazoActual.reset()
                trazoActual.moveTo(r.centerX(), r.top)
                trazoActual.lineTo(r.right, r.bottom)
                trazoActual.lineTo(r.left, r.bottom)
                trazoActual.close()
            }
            Tool.CIRCLE -> {
                trazoActual.reset()
                trazoActual.addOval(rectFrom(startPoint, x, y), Path.Direction.CW)
            }
            Tool.LINE, Tool.ORTHO_LINE -> {
                val endPoint = puntoFinalHerramienta(x, y)
                trazoActual.reset()
                trazoActual.moveTo(startPoint.x, startPoint.y)
                trazoActual.lineTo(endPoint.x, endPoint.y)
            }
            Tool.TEXT, Tool.SELECT -> Unit
        }
    }

    private fun trazoValido(x: Float, y: Float): Boolean {
        if (herramienta == Tool.NONE) return false
        if (herramienta == Tool.FREEHAND || herramienta == Tool.MAGNET_PEN) return true
        if (herramienta == Tool.TEXT || herramienta == Tool.SELECT) return false
        return abs(x - startPoint.x) >= 12f || abs(y - startPoint.y) >= 12f
    }

    private fun rectFrom(start: PointF, x: Float, y: Float): RectF {
        return RectF(
            minOf(start.x, x),
            minOf(start.y, y),
            maxOf(start.x, x),
            maxOf(start.y, y)
        )
    }

    private fun puntoFinalHerramienta(x: Float, y: Float): PointF {
        return when (herramienta) {
            Tool.LINE -> snapLibre(PointF(x, y)) ?: PointF(x, y)
            Tool.ORTHO_LINE -> {
                val axis = if (abs(x - startPoint.x) >= abs(y - startPoint.y)) Axis.HORIZONTAL else Axis.VERTICAL
                val ortho = when (axis) {
                    Axis.HORIZONTAL -> PointF(x, startPoint.y)
                    Axis.VERTICAL -> PointF(startPoint.x, y)
                }
                snapOrtogonal(ortho, axis) ?: ortho
            }
            Tool.TEXT, Tool.SELECT -> PointF(x, y)
            else -> PointF(x, y)
        }
    }

    private fun snapLibre(point: PointF): PointF? {
        var mejor: PointF? = null
        var mejorDist = snapThresholdPx()
        for ((a, b) in segmentosExistentes()) {
            val candidate = puntoMasCercanoEnSegmento(point, a, b)
            val dist = distancia(point, candidate)
            if (dist < mejorDist) {
                mejorDist = dist
                mejor = candidate
            }
        }
        return mejor
    }

    private fun snapOrtogonal(point: PointF, axis: Axis): PointF? {
        var mejor: PointF? = null
        var mejorDist = snapThresholdPx()
        for ((a, b) in segmentosExistentes()) {
            val candidate = when (axis) {
                Axis.HORIZONTAL -> interseccionConHorizontal(point.y, a, b)
                Axis.VERTICAL -> interseccionConVertical(point.x, a, b)
            } ?: puntoMasCercanoEnSegmento(point, a, b).let {
                when (axis) {
                    Axis.HORIZONTAL -> PointF(it.x, point.y)
                    Axis.VERTICAL -> PointF(point.x, it.y)
                }
            }
            val dist = distancia(point, candidate)
            if (dist < mejorDist) {
                mejorDist = dist
                mejor = candidate
            }
        }
        return mejor
    }

    private fun aplicarImanASeleccion() {
        if (selectedIndices.isEmpty()) return
        val exclude = selectedIndices.toSet()
        val segmentosDestino = segmentosExistentes(exclude)
        if (segmentosDestino.isEmpty()) return

        val deltaLados = deltaAlineacionPorLados(segmentosDestino)
        if (deltaLados != null) {
            selectedIndices.forEach { translateElement(it, deltaLados.x, deltaLados.y) }
            return
        }

        var mejorDelta: PointF? = null
        var mejorDist = snapThresholdPx()
        for (p in puntosDeElementos(selectedIndices)) {
            for ((a, b) in segmentosDestino) {
                val candidate = puntoMasCercanoEnSegmento(p, a, b)
                val dist = distancia(p, candidate)
                if (dist < mejorDist) {
                    mejorDist = dist
                    mejorDelta = PointF(candidate.x - p.x, candidate.y - p.y)
                }
            }
        }
        mejorDelta?.let { delta ->
            selectedIndices.forEach { translateElement(it, delta.x, delta.y) }
        }
    }

    private fun deltaAlineacionPorLados(segmentosDestino: List<Pair<PointF, PointF>>): PointF? {
        val bounds = boundsForIndices(selectedIndices) ?: return null
        val threshold = snapThresholdPx()
        var mejorDx: Float? = null
        var mejorDy: Float? = null
        var mejorDxDist = threshold
        var mejorDyDist = threshold

        for ((a, b) in segmentosDestino) {
            if (abs(a.x - b.x) < 1f && rangosSeCruzan(bounds.top, bounds.bottom, minOf(a.y, b.y), maxOf(a.y, b.y), threshold)) {
                val destinoX = (a.x + b.x) / 2f
                listOf(bounds.left, bounds.right).forEach { origenX ->
                    val delta = destinoX - origenX
                    val dist = abs(delta)
                    if (dist < mejorDxDist) {
                        mejorDxDist = dist
                        mejorDx = delta
                    }
                }
            }
            if (abs(a.y - b.y) < 1f && rangosSeCruzan(bounds.left, bounds.right, minOf(a.x, b.x), maxOf(a.x, b.x), threshold)) {
                val destinoY = (a.y + b.y) / 2f
                listOf(bounds.top, bounds.bottom).forEach { origenY ->
                    val delta = destinoY - origenY
                    val dist = abs(delta)
                    if (dist < mejorDyDist) {
                        mejorDyDist = dist
                        mejorDy = delta
                    }
                }
            }
        }

        if (mejorDx == null && mejorDy == null) return null
        return PointF(mejorDx ?: 0f, mejorDy ?: 0f)
    }

    private fun boundsForIndices(indices: List<Int>): RectF? {
        var union: RectF? = null
        for (index in indices) {
            val element = elementos.getOrNull(index) ?: continue
            val bounds = boundsForElement(element)
            if (union == null) {
                union = RectF(bounds)
            } else {
                union.union(bounds)
            }
        }
        return union
    }

    private fun rangosSeCruzan(aStart: Float, aEnd: Float, bStart: Float, bEnd: Float, tolerancia: Float): Boolean {
        return maxOf(aStart, bStart) <= minOf(aEnd, bEnd) + tolerancia
    }

    private fun puntosDeElementos(indices: List<Int>): List<PointF> {
        return indices.flatMap { index ->
            val element = elementos.getOrNull(index) ?: return@flatMap emptyList()
            puntosDeElemento(element)
        }
    }

    private fun puntosDeElemento(element: Element): List<PointF> {
        return when (element) {
            is Element.Freehand -> puntosBounds(boundsForElement(element))
            is Element.Composite -> element.contours.flatten().ifEmpty { puntosBounds(boundsForElement(element)) }
            is Element.Group -> element.children.flatMap { puntosDeElemento(it) }.ifEmpty { puntosBounds(boundsForElement(element)) }
            is Element.TextLabel -> puntosBounds(boundsForElement(element))
            is Element.InfoBox -> puntosBounds(element.rect)
            is Element.Symbol -> puntosBounds(element.rect)
            is Element.Shape -> when (element.tool) {
                Tool.RECTANGLE -> listOf(element.topLeft, element.topRight, element.bottomRight, element.bottomLeft)
                Tool.TRIANGLE -> verticesTriangulo(element).toList()
                Tool.CIRCLE -> puntosBounds(element.rect)
                Tool.LINE, Tool.ORTHO_LINE -> listOf(element.start, element.end)
                Tool.NONE, Tool.FREEHAND, Tool.MAGNET_PEN, Tool.TEXT, Tool.SELECT, Tool.NODO -> puntosBounds(boundsForElement(element))
            }
        }.map { PointF(it.x, it.y) }
    }

    private fun puntosBounds(rect: RectF): List<PointF> {
        return listOf(
            PointF(rect.left, rect.top),
            PointF(rect.right, rect.top),
            PointF(rect.right, rect.bottom),
            PointF(rect.left, rect.bottom),
            PointF(rect.centerX(), rect.centerY())
        )
    }

    private fun segmentosExistentes(excludeIndices: Set<Int> = emptySet()): List<Pair<PointF, PointF>> {
        val segmentos = mutableListOf<Pair<PointF, PointF>>()
        for ((index, element) in elementos.withIndex()) {
            if (index in excludeIndices) continue
            when (element) {
                is Element.Freehand -> segmentos += segmentosBounds(boundsForElement(element))
                is Element.TextLabel -> segmentos += segmentosBounds(boundsForElement(element))
                is Element.InfoBox -> segmentos += segmentosBounds(element.rect)
                is Element.Symbol -> segmentos += segmentosBounds(element.rect)
                is Element.Composite -> {
                    val compositeSegments = segmentosContours(element.contours)
                    segmentos += compositeSegments.ifEmpty { segmentosBounds(boundsForElement(element)) }
                }
                is Element.Group -> element.children.forEach { child -> segmentos += segmentosDeElemento(child) }
                is Element.Shape -> when (element.tool) {
                    Tool.RECTANGLE -> {
                        segmentos += element.topLeft to element.topRight
                        segmentos += element.topRight to element.bottomRight
                        segmentos += element.bottomRight to element.bottomLeft
                        segmentos += element.bottomLeft to element.topLeft
                    }
                    Tool.TRIANGLE -> {
                        val (top, right, left) = verticesTriangulo(element)
                        segmentos += top to right
                        segmentos += right to left
                        segmentos += left to top
                    }
                    Tool.LINE, Tool.ORTHO_LINE -> {
                        segmentos += element.start to element.end
                    }
                    Tool.CIRCLE -> {
                        segmentos += segmentosOvalo(element.rect)
                    }
                    Tool.NONE, Tool.FREEHAND, Tool.MAGNET_PEN, Tool.TEXT, Tool.SELECT, Tool.NODO -> Unit
                }
            }
        }
        return segmentos
    }

    private fun segmentosDeElemento(element: Element): List<Pair<PointF, PointF>> {
        return when (element) {
            is Element.Freehand -> segmentosBounds(boundsForElement(element))
            is Element.TextLabel -> segmentosBounds(boundsForElement(element))
            is Element.InfoBox -> segmentosBounds(element.rect)
            is Element.Symbol -> segmentosBounds(element.rect)
            is Element.Composite -> segmentosContours(element.contours).ifEmpty { segmentosBounds(boundsForElement(element)) }
            is Element.Group -> element.children.flatMap { segmentosDeElemento(it) }
            is Element.Shape -> when (element.tool) {
                Tool.RECTANGLE -> listOf(
                    element.topLeft to element.topRight,
                    element.topRight to element.bottomRight,
                    element.bottomRight to element.bottomLeft,
                    element.bottomLeft to element.topLeft
                )
                Tool.TRIANGLE -> {
                    val top = PointF(element.rect.centerX(), element.rect.top)
                    val right = PointF(element.rect.right, element.rect.bottom)
                    val left = PointF(element.rect.left, element.rect.bottom)
                    listOf(top to right, right to left, left to top)
                }
                Tool.LINE, Tool.ORTHO_LINE -> listOf(element.start to element.end)
                Tool.CIRCLE -> segmentosOvalo(element.rect)
                Tool.NONE, Tool.FREEHAND, Tool.MAGNET_PEN, Tool.TEXT, Tool.SELECT, Tool.NODO -> emptyList()
            }
        }
    }

    private fun segmentosBounds(rect: RectF): List<Pair<PointF, PointF>> {
        val tl = PointF(rect.left, rect.top)
        val tr = PointF(rect.right, rect.top)
        val br = PointF(rect.right, rect.bottom)
        val bl = PointF(rect.left, rect.bottom)
        return listOf(tl to tr, tr to br, br to bl, bl to tl)
    }

    private fun segmentosContours(contours: List<List<PointF>>): List<Pair<PointF, PointF>> {
        return contours.flatMap { contour ->
            if (contour.size < 2) {
                emptyList()
            } else {
                contour.indices.map { i -> contour[i] to contour[(i + 1) % contour.size] }
            }
        }
    }

    private fun segmentosOvalo(rect: RectF): List<Pair<PointF, PointF>> {
        val puntos = (0 until 16).map { i ->
            val a = (Math.PI * 2.0 * i) / 16.0
            PointF(
                rect.centerX() + (cos(a) * rect.width() / 2f).toFloat(),
                rect.centerY() + (sin(a) * rect.height() / 2f).toFloat()
            )
        }
        return puntos.indices.map { i -> puntos[i] to puntos[(i + 1) % puntos.size] }
    }

    private fun puntoMasCercanoEnSegmento(p: PointF, a: PointF, b: PointF): PointF {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val len2 = (dx * dx) + (dy * dy)
        if (len2 <= 0.001f) return PointF(a.x, a.y)
        val t = (((p.x - a.x) * dx) + ((p.y - a.y) * dy)) / len2
        val tc = t.coerceIn(0f, 1f)
        return PointF(a.x + (dx * tc), a.y + (dy * tc))
    }

    private fun interseccionConHorizontal(y: Float, a: PointF, b: PointF): PointF? {
        val dy = b.y - a.y
        if (abs(dy) < 0.001f) {
            if (abs(y - a.y) > snapThresholdPx()) return null
            return PointF(((a.x + b.x) / 2f).coerceIn(minOf(a.x, b.x), maxOf(a.x, b.x)), y)
        }
        val t = (y - a.y) / dy
        if (t !in 0f..1f) return null
        return PointF(a.x + ((b.x - a.x) * t), y)
    }

    private fun interseccionConVertical(x: Float, a: PointF, b: PointF): PointF? {
        val dx = b.x - a.x
        if (abs(dx) < 0.001f) {
            if (abs(x - a.x) > snapThresholdPx()) return null
            return PointF(x, ((a.y + b.y) / 2f).coerceIn(minOf(a.y, b.y), maxOf(a.y, b.y)))
        }
        val t = (x - a.x) / dx
        if (t !in 0f..1f) return null
        return PointF(x, a.y + ((b.y - a.y) * t))
    }

    private fun distancia(a: PointF, b: PointF): Float {
        return hypot((a.x - b.x).toDouble(), (a.y - b.y).toDouble()).toFloat()
    }

    private fun snapThresholdPx(): Float = 26f * resources.displayMetrics.density

    private fun pxToCm(px: Float): Float {
        val dp = px / resources.displayMetrics.density
        // Redondear, no truncar: truncando, un ida y vuelta cm → px → cm perdía hasta 1 mm en cada
        // edición (143.6 volvía como 143.5) y la medida se iba corriendo sola.
        return Math.round(dp * 10f) / 10f
    }

    private fun cmToPx(cm: Float): Float {
        return cm * resources.displayMetrics.density
    }

    private fun spToPx(sp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics)
    }

    private fun formatCm(value: Float): String {
        return if (value % 1f == 0f) {
            value.toInt().toString()
        } else {
            String.format(Locale.US, "%.1f", value)
        }
    }

    private fun formatAngle(value: Float): String {
        return if (abs(value % 1f) < 0.05f) {
            value.toInt().toString()
        } else {
            String.format(Locale.US, "%.1f", value)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        val bitmap = fondo ?: return
        val scale = minOf(width / bitmap.width.toFloat(), height / bitmap.height.toFloat())
        val drawW = bitmap.width * scale
        val drawH = bitmap.height * scale
        val left = (width - drawW) / 2f
        val top = (height - drawH) / 2f
        canvas.drawBitmap(bitmap, null, RectF(left, top, left + drawW, top + drawH), fondoPaint)
    }
}
