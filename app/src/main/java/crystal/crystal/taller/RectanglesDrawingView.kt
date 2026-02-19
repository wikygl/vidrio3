package crystal.crystal.taller

import android.app.AlertDialog
import android.graphics.Bitmap
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.RectF
import android.util.AttributeSet
import android.view.WindowManager
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import kotlin.math.acos
import kotlin.math.abs
import kotlin.math.sin
import kotlin.math.sqrt

class RectanglesDrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {
    companion object {
        private const val BASE_WALL_HEIGHT_CM = 240f
    }

    enum class Side {
        TOP, RIGHT, BOTTOM, LEFT
    }

    enum class ProductType {
        NONE, VENTANA, PUERTA, MAMPARA
    }

    enum class EncounterType {
        MURO, ACOPLADO, INDEPENDIENTE, BORDE_LIBRE
    }

    enum class GeometryType {
        PLANO, ESQUINERO, CURVO, C_TOP, MULTI_LADO
    }

    enum class AletaDirection {
        INTERIOR, EXTERIOR
    }

    enum class AletaSide {
        RIGHT, LEFT
    }

    enum class EsquineroVariant {
        RECTO, ARCO, ARCO_RECTO
    }

    var onSideMeasurementChanged: ((Side, Float, Float) -> Unit)? = null
    var onEsquineroAletaAnchoChanged: ((Float, Float) -> Unit)? = null
    var onEsquineroAletaAltoChanged: ((Float, Float) -> Unit)? = null
    var onMultiLadoAletaAnchoChanged: ((Int, Float, Float) -> Unit)? = null
    var onMultiLadoAletaAltoChanged: ((Int, Float, Float) -> Unit)? = null
    private var productType: ProductType = ProductType.NONE
    private var encounterType: EncounterType = EncounterType.MURO
    private var geometryType: GeometryType = GeometryType.PLANO
    private val acopleSides = linkedSetOf<Side>()
    private var acopleProductType: ProductType = ProductType.PUERTA
    private var acopleBaseWidthCm = 60f
    private var acopleBaseHeightCm = 100f
    private var esquineroLado1Cm = 30f
    private var esquineroLado2Cm = 40f
    private var esquineroHipotenusaCm = 50f
    private var esquineroAnguloDeg = 90f
    private var esquineroAletaAnchoCm = 40f
    private var esquineroAletaAltoCm = 90f
    private var esquineroAletaDirection = AletaDirection.INTERIOR
    private var esquineroAletaSide = AletaSide.RIGHT
    private var esquineroVariant = EsquineroVariant.RECTO
    private var multiLadoCount = 2
    private var multiLadoSide = AletaSide.RIGHT
    private var multiLadoDirection = AletaDirection.INTERIOR
    private var multiLadoChainSameDirection = true
    private val multiLadoAletas = mutableListOf<MultiLadoAletaConfig>()
    private var cTopRightLado1Cm = 30f
    private var cTopRightLado2Cm = 40f
    private var cTopRightHipotenusaCm = 50f
    private var cTopRightAnguloDeg = 90f
    private var cTopRightAletaAnchoCm = 40f
    private var cTopRightAletaAltoCm = 90f
    private var cTopLeftLado1Cm = 30f
    private var cTopLeftLado2Cm = 40f
    private var cTopLeftHipotenusaCm = 50f
    private var cTopLeftAnguloDeg = 90f
    private var cTopLeftAletaAnchoCm = 40f
    private var cTopLeftAletaAltoCm = 90f
    private var curvoDesarrolloCm = 130f
    private var curvoCuerdaCm = 120f
    private var curvoFlechaCm: Float? = null
    private var curvoDirection = AletaDirection.INTERIOR

    private var presetWidth = 120f
    private var presetHeight = 90f
    private var presetRect = RectF()
    private var sideTop = 120f
    private var sideRight = 90f
    private var sideBottom = 120f
    private var sideLeft = 90f
    private var wallHeightCm = BASE_WALL_HEIGHT_CM
    private var sillHeightCm = 90f
    private var headClearanceCm = 0f

    private val presetFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(40, 46, 125, 50)
    }

    private val presetStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = Color.rgb(46, 125, 50)
    }
    private val shapePath = Path()
    private var leftX = 0f
    private var rightX = 0f
    private var topLeftX = 0f
    private var topRightX = 0f
    private var bottomY = 0f
    private var topLeftY = 0f
    private var topRightY = 0f
    private var floorTopY = 0f
    private var ceilingBottomY = 0f
    private var scalePxPerCm = 1f
    private var pendingTapSide: Side? = null
    private var pendingTapAletaAncho = false
    private var pendingTapAletaAlto = false
    private var pendingTapAletaAnchoSide: AletaSide? = null
    private var pendingTapAletaAltoSide: AletaSide? = null
    private var pendingTapMultiAletaAnchoIndex: Int? = null
    private var pendingTapMultiAletaAltoIndex: Int? = null
    private val topTextHit = RectF()
    private val rightTextHit = RectF()
    private val bottomTextHit = RectF()
    private val leftTextHit = RectF()
    private val aletaAnchoTextHit = RectF()
    private val aletaAltoTextHit = RectF()
    private val aletaAnchoTextHitRight = RectF()
    private val aletaAltoTextHitRight = RectF()
    private val aletaAnchoTextHitLeft = RectF()
    private val aletaAltoTextHitLeft = RectF()
    private var aletaP2x = 0f
    private var aletaP2y = 0f
    private var aletaP3x = 0f
    private var aletaP3y = 0f
    private var aletaP4x = 0f
    private var aletaP4y = 0f
    private var aletaCenterX = 0f
    private var aletaCenterY = 0f
    private var cTopRightAletaGeometry: AletaGeometry? = null
    private var cTopLeftAletaGeometry: AletaGeometry? = null
    private val multiLadoAletaGeometry = mutableListOf<AletaGeometry>()
    private data class MultiLadoCotaHit(
        val index: Int,
        val anchoRect: RectF,
        val altoRect: RectF
    )
    private val multiLadoCotaHits = mutableListOf<MultiLadoCotaHit>()
    private var showMultiDialogTopGuide = false

    private data class AletaGeometry(
        val p2x: Float,
        val p2y: Float,
        val p3x: Float,
        val p3y: Float,
        val p4x: Float,
        val p4y: Float,
        val centerX: Float,
        val centerY: Float
    )

    private data class AletaDrawResult(
        val geometry: AletaGeometry,
        val outerTop: PointF,
        val outerBottom: PointF
    )

    private data class AletaProjected(
        val p1x: Float,
        val p1y: Float,
        val p2x: Float,
        val p2y: Float,
        val p3x: Float,
        val p3y: Float,
        val p4x: Float,
        val p4y: Float,
        val anchorSide: AletaSide,
        val direction: AletaDirection
    )

    private data class TurnAxisGuide(
        val top: PointF,
        val bottom: PointF
    )

    private data class TopGuideSegment(
        val startX: Float,
        val startY: Float,
        val endX: Float,
        val endY: Float,
        val direction: AletaDirection,
        val anguloDeg: Float,
        val anchoCm: Float
    )

    data class CTopAletaConfig(
        val lado1Cm: Float,
        val lado2Cm: Float,
        val hipotenusaCm: Float,
        val anguloDeg: Float,
        val aletaAnchoCm: Float,
        val aletaAltoCm: Float
    )

    data class MultiLadoConfig(
        val count: Int,
        val side: AletaSide,
        val direction: AletaDirection,
        val chainSameDirection: Boolean
    )

    data class MultiLadoAletaConfig(
        val side: AletaSide,
        val direction: AletaDirection,
        val lado1Cm: Float,
        val lado2Cm: Float,
        val hipotenusaCm: Float,
        val aletaAnchoCm: Float,
        val aletaAltoCm: Float
    )

    data class CurvoConfig(
        val desarrolloCm: Float,
        val cuerdaCm: Float,
        val flechaCm: Float?,
        val direction: AletaDirection,
        val flechaEfectivaCm: Float
    )

    private val coteLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.rgb(55, 71, 79)
    }

    private val coteTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 30f
        color = Color.rgb(33, 33, 33)
        textAlign = Paint.Align.CENTER
    }

    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(189, 193, 198)
    }

    private val wallStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.rgb(110, 114, 120)
    }

    private var contextBitmap: Bitmap? = null
    private var contextCanvas: Canvas? = null
    private var contextDirty = true

    private val floorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(203, 193, 178)
    }

    private val ceilingPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(214, 214, 209)
    }

    private val acoplePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(150, 255, 179, 71)
    }

    private val acopleStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.rgb(230, 126, 34)
    }

    private val secondaryFaceFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(55, 39, 99, 43)
    }

    private val secondaryFaceStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.rgb(35, 94, 39)
    }

    private val multiTurnAxisPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = Color.rgb(0, 121, 191)
    }

    private val multiTurnAxisNodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(0, 121, 191)
    }

    private val multiTurnAxisTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 22f
        color = Color.rgb(0, 94, 153)
        textAlign = Paint.Align.LEFT
    }

    private val topGuidePanelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.argb(220, 245, 246, 248)
    }

    private val topGuideBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
        color = Color.rgb(111, 120, 127)
    }

    private val topGuideLineInteriorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.rgb(43, 125, 66)
    }

    private val topGuideLineExteriorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        color = Color.rgb(198, 112, 34)
    }

    private val topGuideNodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(55, 71, 79)
    }

    private val topGuideNextPointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.rgb(216, 27, 96)
    }

    private val topGuideNextPointStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.rgb(136, 14, 79)
    }

    private val topGuideTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 24f
        color = Color.rgb(38, 50, 56)
        textAlign = Paint.Align.LEFT
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (!presetRect.isEmpty) {
            drawCachedContext(canvas)
            drawMainOpening(canvas)
            drawAcoples(canvas)
            drawCotas(canvas)
            drawTopGuideOverlay(canvas)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        contextBitmap = null
        contextCanvas = null
        contextDirty = true
        recomputePresetRect()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                pendingTapSide = getTappedSide(event.x, event.y)
                pendingTapAletaAnchoSide = if (pendingTapSide == null) getTappedAletaAnchoSide(event.x, event.y) else null
                pendingTapAletaAncho = pendingTapAletaAnchoSide != null
                pendingTapAletaAltoSide = if (pendingTapSide == null && !pendingTapAletaAncho) getTappedAletaAltoSide(event.x, event.y) else null
                pendingTapAletaAlto = pendingTapAletaAltoSide != null
                pendingTapMultiAletaAnchoIndex = if (pendingTapSide == null && !pendingTapAletaAncho && !pendingTapAletaAlto) {
                    getTappedMultiLadoAletaAnchoIndex(event.x, event.y)
                } else {
                    null
                }
                pendingTapMultiAletaAltoIndex = if (pendingTapSide == null && !pendingTapAletaAncho && !pendingTapAletaAlto && pendingTapMultiAletaAnchoIndex == null) {
                    getTappedMultiLadoAletaAltoIndex(event.x, event.y)
                } else {
                    null
                }
                return pendingTapSide != null || pendingTapAletaAncho || pendingTapAletaAlto ||
                    pendingTapMultiAletaAnchoIndex != null || pendingTapMultiAletaAltoIndex != null
            }

            MotionEvent.ACTION_UP -> {
                val sideAtUp = getTappedSide(event.x, event.y)
                val side = pendingTapSide
                val anchoSideAtUp = getTappedAletaAnchoSide(event.x, event.y)
                val altoSideAtUp = getTappedAletaAltoSide(event.x, event.y)
                val multiAnchoIdxAtUp = getTappedMultiLadoAletaAnchoIndex(event.x, event.y)
                val multiAltoIdxAtUp = getTappedMultiLadoAletaAltoIndex(event.x, event.y)
                pendingTapSide = null
                val anchoTap = pendingTapAletaAncho
                val altoTap = pendingTapAletaAlto
                val anchoTapSide = pendingTapAletaAnchoSide
                val altoTapSide = pendingTapAletaAltoSide
                val multiAnchoTapIndex = pendingTapMultiAletaAnchoIndex
                val multiAltoTapIndex = pendingTapMultiAletaAltoIndex
                pendingTapAletaAncho = false
                pendingTapAletaAlto = false
                pendingTapAletaAnchoSide = null
                pendingTapAletaAltoSide = null
                pendingTapMultiAletaAnchoIndex = null
                pendingTapMultiAletaAltoIndex = null
                if (side != null && side == sideAtUp) {
                    showSideInputDialog(side)
                    return true
                }
                if (anchoTap && anchoTapSide != null && anchoTapSide == anchoSideAtUp) {
                    showAletaAnchoInputDialog(anchoTapSide)
                    return true
                }
                if (altoTap && altoTapSide != null && altoTapSide == altoSideAtUp) {
                    showAletaAltoInputDialog(altoTapSide)
                    return true
                }
                if (multiAnchoTapIndex != null && multiAnchoTapIndex == multiAnchoIdxAtUp) {
                    showMultiLadoAletaAnchoInputDialog(multiAnchoTapIndex)
                    return true
                }
                if (multiAltoTapIndex != null && multiAltoTapIndex == multiAltoIdxAtUp) {
                    showMultiLadoAletaAltoInputDialog(multiAltoTapIndex)
                    return true
                }
                return false
            }

            MotionEvent.ACTION_CANCEL -> {
                pendingTapSide = null
                pendingTapAletaAncho = false
                pendingTapAletaAlto = false
                pendingTapAletaAnchoSide = null
                pendingTapAletaAltoSide = null
                pendingTapMultiAletaAnchoIndex = null
                pendingTapMultiAletaAltoIndex = null
                return false
            }
        }
        return false
    }

    fun clearRectangles() {
        presetWidth = 120f
        presetHeight = 90f
        sideTop = presetWidth
        sideBottom = presetWidth
        sideLeft = presetHeight
        sideRight = presetHeight
        wallHeightCm = BASE_WALL_HEIGHT_CM
        contextDirty = true
        recomputePresetRect()
        invalidate()
    }

    fun setProductType(type: ProductType) {
        if (productType != type) {
            productType = type
            sillHeightCm = when (type) {
                ProductType.VENTANA -> 90f
                ProductType.PUERTA, ProductType.MAMPARA -> 0f
                ProductType.NONE -> sillHeightCm
            }
            headClearanceCm = when (type) {
                ProductType.VENTANA -> 0f
                ProductType.PUERTA, ProductType.MAMPARA -> 20f
                ProductType.NONE -> headClearanceCm
            }
        }
        contextDirty = true
        recomputePresetRect()
        invalidate()
    }

    fun setEncounterType(type: EncounterType) {
        encounterType = type
        if (type != EncounterType.ACOPLADO && type != EncounterType.BORDE_LIBRE) {
            acopleSides.clear()
        }
        contextDirty = true
        invalidate()
    }

    fun setAcopleSides(sides: Set<Side>) {
        acopleSides.clear()
        acopleSides.addAll(sides)
        encounterType = EncounterType.ACOPLADO
        invalidate()
    }

    fun setAcopleConfiguration(sides: Set<Side>, productType: ProductType) {
        acopleSides.clear()
        acopleSides.addAll(sides)
        acopleProductType = productType
        encounterType = EncounterType.ACOPLADO
        wallHeightCm = maxOf(wallHeightCm, acopleBaseHeightCm + defaultSillForProduct(acopleProductType))
        contextDirty = true
        recomputePresetRect()
        invalidate()
    }

    fun setBordeLibreSides(sides: Set<Side>) {
        acopleSides.clear()
        acopleSides.addAll(sides)
        encounterType = EncounterType.BORDE_LIBRE
        contextDirty = true
        recomputePresetRect()
        invalidate()
    }

    fun setAcopleBaseSize(widthCm: Float?, heightCm: Float?) {
        if (widthCm != null && widthCm > 0f) acopleBaseWidthCm = widthCm
        if (heightCm != null && heightCm > 0f) acopleBaseHeightCm = heightCm
        wallHeightCm = maxOf(wallHeightCm, acopleBaseHeightCm + defaultSillForProduct(acopleProductType))
        if (encounterType == EncounterType.ACOPLADO) {
            contextDirty = true
            recomputePresetRect()
            invalidate()
        }
    }

    fun setGeometryType(type: GeometryType) {
        geometryType = type
        contextDirty = true
        recomputePresetRect()
        invalidate()
    }

    fun setCurvoConfig(config: CurvoConfig): Boolean {
        val desarrollo = config.desarrolloCm.coerceAtLeast(0f)
        val cuerda = config.cuerdaCm.coerceAtLeast(0f)
        if (desarrollo <= 0f || cuerda <= 0f || desarrollo < cuerda) return false
        val flechaIngresada = config.flechaCm?.takeIf { it > 0f }
        val flechaEfectiva = flechaIngresada ?: calcularFlechaPorDesarrolloYCuerda(desarrollo, cuerda) ?: return false

        curvoDesarrolloCm = desarrollo
        curvoCuerdaCm = cuerda
        curvoFlechaCm = flechaIngresada
        curvoDirection = config.direction
        sideTop = cuerda
        presetWidth = ((sideTop + sideBottom) / 2f).coerceAtLeast(1f)

        if (flechaEfectiva.isNaN() || flechaEfectiva < 0f) return false
        contextDirty = true
        recomputePresetRect()
        invalidate()
        return true
    }

    fun getCurvoConfig(): CurvoConfig {
        val flechaEfectiva = curvoFlechaCm ?: calcularFlechaPorDesarrolloYCuerda(curvoDesarrolloCm, curvoCuerdaCm) ?: 0f
        return CurvoConfig(
            desarrolloCm = curvoDesarrolloCm,
            cuerdaCm = curvoCuerdaCm,
            flechaCm = curvoFlechaCm,
            direction = curvoDirection,
            flechaEfectivaCm = flechaEfectiva
        )
    }

    fun setMultiDialogTopGuideVisible(visible: Boolean) {
        if (showMultiDialogTopGuide == visible) return
        showMultiDialogTopGuide = visible
        invalidate()
    }

    fun setEsquineroAletaSize(anchoCm: Float?, altoCm: Float?) {
        if (anchoCm != null && anchoCm > 0f) esquineroAletaAnchoCm = anchoCm
        if (altoCm != null && altoCm > 0f) esquineroAletaAltoCm = altoCm
        contextDirty = true
        recomputePresetRect()
        invalidate()
    }

    fun setEsquineroAletaDirection(direction: AletaDirection) {
        esquineroAletaDirection = direction
        contextDirty = true
        recomputePresetRect()
        invalidate()
    }

    fun setEsquineroAletaSide(side: AletaSide) {
        esquineroAletaSide = side
        contextDirty = true
        recomputePresetRect()
        invalidate()
    }

    fun setEsquineroVariant(variant: EsquineroVariant) {
        esquineroVariant = variant
        contextDirty = true
        recomputePresetRect()
        invalidate()
    }

    fun getEsquineroVariant(): EsquineroVariant = esquineroVariant

    fun setEsquineroByHypotenuse345(hypotenuseCm: Float): Boolean {
        if (hypotenuseCm <= 0f) return false
        return setEsquineroTriangle(
            lado1Cm = esquineroLado1Cm,
            lado2Cm = esquineroLado2Cm,
            hipotenusaCm = hypotenuseCm
        )
    }

    fun setEsquineroTriangle(lado1Cm: Float, lado2Cm: Float, hipotenusaCm: Float): Boolean {
        if (lado1Cm <= 0f || lado2Cm <= 0f || hipotenusaCm <= 0f) return false
        if (lado1Cm + lado2Cm <= hipotenusaCm) return false
        if (lado1Cm + hipotenusaCm <= lado2Cm) return false
        if (lado2Cm + hipotenusaCm <= lado1Cm) return false

        esquineroLado1Cm = lado1Cm
        esquineroLado2Cm = lado2Cm
        esquineroHipotenusaCm = hipotenusaCm
        esquineroAnguloDeg = calcularAnguloEsquinero(lado1Cm, lado2Cm, hipotenusaCm)
        contextDirty = true
        recomputePresetRect()
        invalidate()
        return true
    }

    fun getEsquineroConfig(): EsquineroConfig {
        return EsquineroConfig(
            lado1Cm = esquineroLado1Cm,
            lado2Cm = esquineroLado2Cm,
            hipotenusaCm = esquineroHipotenusaCm,
            anguloDeg = esquineroAnguloDeg,
            aletaAnchoCm = esquineroAletaAnchoCm,
            aletaAltoCm = esquineroAletaAltoCm,
            aletaDirection = esquineroAletaDirection,
            aletaSide = esquineroAletaSide,
            variant = esquineroVariant
        )
    }

    fun setCTopAletaConfig(side: AletaSide, config: CTopAletaConfig): Boolean {
        val l1 = config.lado1Cm
        val l2 = config.lado2Cm
        val h = config.hipotenusaCm
        if (l1 <= 0f || l2 <= 0f || h <= 0f) return false
        if (l1 + l2 <= h) return false
        if (l1 + h <= l2) return false
        if (l2 + h <= l1) return false
        val ancho = config.aletaAnchoCm.coerceAtLeast(1f)
        val alto = config.aletaAltoCm.coerceAtLeast(1f)
        val ang = calcularAnguloEsquinero(l1, l2, h)
        if (side == AletaSide.RIGHT) {
            cTopRightLado1Cm = l1
            cTopRightLado2Cm = l2
            cTopRightHipotenusaCm = h
            cTopRightAnguloDeg = ang
            cTopRightAletaAnchoCm = ancho
            cTopRightAletaAltoCm = alto
        } else {
            cTopLeftLado1Cm = l1
            cTopLeftLado2Cm = l2
            cTopLeftHipotenusaCm = h
            cTopLeftAnguloDeg = ang
            cTopLeftAletaAnchoCm = ancho
            cTopLeftAletaAltoCm = alto
        }
        contextDirty = true
        recomputePresetRect()
        invalidate()
        return true
    }

    fun getCTopAletaConfig(side: AletaSide): CTopAletaConfig {
        return if (side == AletaSide.RIGHT) {
            CTopAletaConfig(
                lado1Cm = cTopRightLado1Cm,
                lado2Cm = cTopRightLado2Cm,
                hipotenusaCm = cTopRightHipotenusaCm,
                anguloDeg = cTopRightAnguloDeg,
                aletaAnchoCm = cTopRightAletaAnchoCm,
                aletaAltoCm = cTopRightAletaAltoCm
            )
        } else {
            CTopAletaConfig(
                lado1Cm = cTopLeftLado1Cm,
                lado2Cm = cTopLeftLado2Cm,
                hipotenusaCm = cTopLeftHipotenusaCm,
                anguloDeg = cTopLeftAnguloDeg,
                aletaAnchoCm = cTopLeftAletaAnchoCm,
                aletaAltoCm = cTopLeftAletaAltoCm
            )
        }
    }

    fun setMultiLadoConfig(config: MultiLadoConfig) {
        multiLadoCount = config.count.coerceAtLeast(1).coerceAtMost(12)
        multiLadoSide = config.side
        multiLadoDirection = config.direction
        multiLadoChainSameDirection = config.chainSameDirection
        contextDirty = true
        recomputePresetRect()
        invalidate()
    }

    fun getMultiLadoConfig(): MultiLadoConfig {
        return MultiLadoConfig(
            count = multiLadoCount,
            side = multiLadoSide,
            direction = multiLadoDirection,
            chainSameDirection = multiLadoChainSameDirection
        )
    }

    fun setMultiLadoAletas(list: List<MultiLadoAletaConfig>) {
        multiLadoAletas.clear()
        list.forEach { item ->
            val l1 = abs(item.lado1Cm)
            val l2 = abs(item.lado2Cm)
            val h = abs(item.hipotenusaCm)
            if (l1 <= 0f || l2 <= 0f || h <= 0f) return@forEach
            if (l1 + l2 <= h) return@forEach
            if (l1 + h <= l2) return@forEach
            if (l2 + h <= l1) return@forEach
            multiLadoAletas.add(
                item.copy(
                    aletaAnchoCm = item.aletaAnchoCm.coerceAtLeast(1f),
                    aletaAltoCm = item.aletaAltoCm.coerceAtLeast(1f)
                )
            )
        }
        contextDirty = true
        recomputePresetRect()
        invalidate()
    }

    fun getMultiLadoAletas(): List<MultiLadoAletaConfig> = multiLadoAletas.toList()

    fun updatePresetSize(width: Float?, height: Float?) {
        if (width != null && width > 0f) presetWidth = width
        if (height != null && height > 0f) presetHeight = height
        contextDirty = true
        recomputePresetRect()
    }

    fun updateSideMeasurements(
        top: Float?,
        right: Float?,
        bottom: Float?,
        left: Float?
    ) {
        if (top != null && top > 0f) sideTop = top
        if (right != null && right > 0f) sideRight = right
        if (bottom != null && bottom > 0f) sideBottom = bottom
        if (left != null && left > 0f) sideLeft = left

        presetWidth = ((sideTop + sideBottom) / 2f).coerceAtLeast(1f)
        presetHeight = ((sideLeft + sideRight) / 2f).coerceAtLeast(1f)
        contextDirty = true
        recomputePresetRect()
    }

    fun updateClearances(
        sill: Float?,
        head: Float?
    ) {
        if (sill != null && sill >= 0f) sillHeightCm = sill
        if (head != null && head >= 0f) headClearanceCm = head
        contextDirty = true
        recomputePresetRect()
    }

    fun updateWallHeight(height: Float?) {
        if (height == null || height <= 0f) return
        wallHeightCm = maxOf(BASE_WALL_HEIGHT_CM, height)
        contextDirty = true
        recomputePresetRect()
    }    private fun recomputePresetRect() {
        if (width <= 0 || height <= 0) return

        val modelBottom = sideBottom.coerceAtLeast(1f)
        val modelTop = sideTop.coerceAtLeast(1f)
        val modelLeft = sideLeft.coerceAtLeast(1f)
        val modelRight = sideRight.coerceAtLeast(1f)
        val modelOpenHeight = maxOf(modelLeft, modelRight)
        val useWallConstraints = productType != ProductType.NONE

        val modelSill = if (useWallConstraints) {
            val maxSill = (wallHeightCm - modelOpenHeight).coerceAtLeast(0f)
            sillHeightCm.coerceIn(0f, maxSill)
        } else {
            0f
        }
        val modelHead = if (useWallConstraints) {
            (wallHeightCm - (modelSill + modelOpenHeight)).coerceAtLeast(0f)
        } else {
            0f
        }

        if (useWallConstraints) {
            sillHeightCm = modelSill
            headClearanceCm = modelHead
        }
        val modelTotalHeight = if (useWallConstraints) wallHeightCm else modelOpenHeight

        val modelTopOffset = (modelBottom - modelTop) / 2f
        val modelLeftX = 0f
        val modelRightX = modelBottom
        val modelTopLeftX = modelTopOffset
        val modelTopRightX = modelTopOffset + modelTop

        val modelMinX = minOf(minOf(modelLeftX, modelRightX), minOf(modelTopLeftX, modelTopRightX))
        val modelMaxX = maxOf(maxOf(modelLeftX, modelRightX), maxOf(modelTopLeftX, modelTopRightX))

        val modelWidth = (modelMaxX - modelMinX).coerceAtLeast(1f)
        val modelHeight = modelTotalHeight.coerceAtLeast(1f)
        val acopleGapCm = 2f
        val acopleTopBottomScale = 0.45f
        val extraLeftCm = if (encounterType == EncounterType.ACOPLADO && acopleSides.contains(Side.LEFT)) acopleBaseWidthCm + acopleGapCm else 0f
        val extraRightCm = if (encounterType == EncounterType.ACOPLADO && acopleSides.contains(Side.RIGHT)) acopleBaseWidthCm + acopleGapCm else 0f
        val extraTopCm = if (encounterType == EncounterType.ACOPLADO && acopleSides.contains(Side.TOP)) (acopleBaseHeightCm * acopleTopBottomScale) + acopleGapCm else 0f
        val extraBottomCm = if (encounterType == EncounterType.ACOPLADO && acopleSides.contains(Side.BOTTOM)) (acopleBaseHeightCm * acopleTopBottomScale) + acopleGapCm else 0f

        val geometryExtraLeftCm: Float
        val geometryExtraRightCm: Float
        val geometryExtraTopCm: Float
        when (geometryType) {
            GeometryType.ESQUINERO -> {
                val factor = when (esquineroVariant) {
                    EsquineroVariant.RECTO -> 0.38f
                    EsquineroVariant.ARCO -> 0.44f
                    EsquineroVariant.ARCO_RECTO -> 0.54f
                }
                val esquineroExtraCm = modelWidth * factor
                geometryExtraLeftCm = if (esquineroAletaSide == AletaSide.LEFT) esquineroExtraCm else 0f
                geometryExtraRightCm = if (esquineroAletaSide == AletaSide.RIGHT) esquineroExtraCm else 0f
                geometryExtraTopCm = 0f
            }
            GeometryType.C_TOP -> {
                val esquineroExtraCm = modelWidth * 0.38f
                geometryExtraLeftCm = esquineroExtraCm
                geometryExtraRightCm = esquineroExtraCm
                geometryExtraTopCm = 0f
            }
            GeometryType.MULTI_LADO -> {
                val segments = if (multiLadoAletas.isNotEmpty()) multiLadoAletas.size else multiLadoCount.coerceAtLeast(1)
                val dominantSide = multiLadoAletas.lastOrNull()?.side ?: multiLadoSide
                val multiExtra = modelWidth * (0.34f * segments)
                geometryExtraLeftCm = if (dominantSide == AletaSide.LEFT) multiExtra else 0f
                geometryExtraRightCm = if (dominantSide == AletaSide.RIGHT) multiExtra else 0f
                geometryExtraTopCm = (modelHeight * 0.06f)
            }
            GeometryType.CURVO -> {
                geometryExtraLeftCm = 0f
                geometryExtraRightCm = 0f
                geometryExtraTopCm = if (curvoDirection == AletaDirection.EXTERIOR) {
                    curvoFlechaEfectivaCm() * 1.15f
                } else {
                    0f
                }
            }
            else -> {
                geometryExtraLeftCm = 0f
                geometryExtraRightCm = 0f
                geometryExtraTopCm = 0f
            }
        }

        val fitWidthCm = modelWidth + extraLeftCm + extraRightCm + geometryExtraLeftCm + geometryExtraRightCm
        val fitHeightCm = modelHeight + extraTopCm + extraBottomCm + geometryExtraTopCm
        val availableWidth = width * 0.72f
        ceilingBottomY = height * 0.12f
        val designFloorY = height * 0.78f
        val availableHeight = (designFloorY - ceilingBottomY).coerceAtLeast(1f)
        val scale = minOf(
            availableWidth / fitWidthCm.coerceAtLeast(1f),
            availableHeight / fitHeightCm.coerceAtLeast(1f)
        ).coerceAtLeast(0.1f)
        scalePxPerCm = scale

        val fitWidthPx = fitWidthCm * scale
        val startX = (width - fitWidthPx) / 2f + ((extraLeftCm + geometryExtraLeftCm) * scale)
        val startY = if (useWallConstraints) {
            floorTopY = ceilingBottomY + (modelTotalHeight * scale)
            ceilingBottomY + ((extraTopCm + geometryExtraTopCm) * scale)
        } else {
            floorTopY = designFloorY
            ((height - ((modelOpenHeight + extraTopCm + extraBottomCm + geometryExtraTopCm) * scale)) / 2f).coerceAtLeast(0f) + ((extraTopCm + geometryExtraTopCm) * scale)
        }

        fun sx(modelX: Float): Float = startX + (modelX - modelMinX) * scale

        leftX = sx(modelLeftX)
        rightX = sx(modelRightX)
        topLeftX = sx(modelTopLeftX)
        topRightX = sx(modelTopRightX)
        val openingTopY = startY + (modelHead * scale)
        bottomY = openingTopY + (modelOpenHeight * scale)
        topLeftY = bottomY - (modelLeft * scale)
        topRightY = bottomY - (modelRight * scale)

        val top = minOf(topLeftY, topRightY)
        val minX = minOf(leftX, topLeftX)
        val maxX = maxOf(rightX, topRightX)
        presetRect.set(minX, top, maxX, bottomY)
        invalidate()
    }

    private fun drawCotas(canvas: Canvas) {
        val textMargin = 18f
        val lineOffset = 8f

        val topLineY = minOf(topLeftY, topRightY) - lineOffset
        canvas.drawLine(topLeftX, topLineY, topRightX, topLineY, coteLinePaint)
        val topText = formatCota(sideTop)
        val topTextX = (topLeftX + topRightX) / 2f
        val topTextY = topLineY - textMargin
        canvas.drawText(topText, topTextX, topTextY, coteTextPaint)
        updateTextHitRect(topTextHit, topText, topTextX, topTextY)

        val bottomLineY = bottomY + lineOffset
        canvas.drawLine(leftX, bottomLineY, rightX, bottomLineY, coteLinePaint)
        val bottomText = formatCota(sideBottom)
        val bottomTextX = (leftX + rightX) / 2f
        val bottomTextY = bottomLineY + textMargin + 16f
        canvas.drawText(bottomText, bottomTextX, bottomTextY, coteTextPaint)
        updateTextHitRect(bottomTextHit, bottomText, bottomTextX, bottomTextY)

        val leftLineX = minOf(leftX, topLeftX) - lineOffset
        canvas.drawLine(leftLineX, topLeftY, leftLineX, bottomY, coteLinePaint)
        val leftText = formatCota(sideLeft)
        val leftTextX = leftLineX - 34f
        val leftTextY = (topLeftY + bottomY) / 2f
        canvas.drawText(leftText, leftTextX, leftTextY, coteTextPaint)
        updateTextHitRect(leftTextHit, leftText, leftTextX, leftTextY)

        val rightLineX = maxOf(rightX, topRightX) + lineOffset
        canvas.drawLine(rightLineX, topRightY, rightLineX, bottomY, coteLinePaint)
        val rightText = formatCota(sideRight)
        val rightTextX = rightLineX + 34f
        val rightTextY = (topRightY + bottomY) / 2f
        canvas.drawText(rightText, rightTextX, rightTextY, coteTextPaint)
        updateTextHitRect(rightTextHit, rightText, rightTextX, rightTextY)

        if (geometryType == GeometryType.ESQUINERO) {
            val geometry = AletaGeometry(
                p2x = aletaP2x,
                p2y = aletaP2y,
                p3x = aletaP3x,
                p3y = aletaP3y,
                p4x = aletaP4x,
                p4y = aletaP4y,
                centerX = aletaCenterX,
                centerY = aletaCenterY
            )
            drawAletaCotas(canvas, geometry, esquineroAletaAnchoCm, esquineroAletaAltoCm, updateHit = true)
        } else if (geometryType == GeometryType.C_TOP) {
            val rightRects = cTopRightAletaGeometry?.let { drawAletaCotas(canvas, it, cTopRightAletaAnchoCm, cTopRightAletaAltoCm, updateHit = false) }
            val leftRects = cTopLeftAletaGeometry?.let { drawAletaCotas(canvas, it, cTopLeftAletaAnchoCm, cTopLeftAletaAltoCm, updateHit = false) }
            if (rightRects != null) {
                aletaAnchoTextHitRight.set(rightRects.first)
                aletaAltoTextHitRight.set(rightRects.second)
            } else {
                aletaAnchoTextHitRight.setEmpty()
                aletaAltoTextHitRight.setEmpty()
            }
            if (leftRects != null) {
                aletaAnchoTextHitLeft.set(leftRects.first)
                aletaAltoTextHitLeft.set(leftRects.second)
            } else {
                aletaAnchoTextHitLeft.setEmpty()
                aletaAltoTextHitLeft.setEmpty()
            }
            val anchoUnion = unionRects(rightRects?.first, leftRects?.first)
            val altoUnion = unionRects(rightRects?.second, leftRects?.second)
            if (anchoUnion != null && altoUnion != null) {
                aletaAnchoTextHit.set(anchoUnion)
                aletaAltoTextHit.set(altoUnion)
            } else {
                aletaAnchoTextHit.setEmpty()
                aletaAltoTextHit.setEmpty()
            }
            multiLadoCotaHits.clear()
        } else if (geometryType == GeometryType.MULTI_LADO) {
            multiLadoCotaHits.clear()
            val chain = if (multiLadoAletas.isNotEmpty()) multiLadoAletas else List(multiLadoCount.coerceAtLeast(1)) {
                MultiLadoAletaConfig(
                    side = multiLadoSide,
                    direction = multiLadoDirection,
                    lado1Cm = esquineroLado1Cm,
                    lado2Cm = esquineroLado2Cm,
                    hipotenusaCm = esquineroHipotenusaCm,
                    aletaAnchoCm = esquineroAletaAnchoCm,
                    aletaAltoCm = esquineroAletaAltoCm
                )
            }
            multiLadoAletaGeometry.forEachIndexed { idx, geometry ->
                val cfg = chain.getOrNull(idx)
                if (cfg != null) {
                    val rects = drawAletaCotas(
                        canvas = canvas,
                        geometry = geometry,
                        anchoCm = cfg.aletaAnchoCm,
                        altoCm = cfg.aletaAltoCm,
                        updateHit = false
                    )
                    multiLadoCotaHits.add(
                        MultiLadoCotaHit(
                            index = idx,
                            anchoRect = RectF(rects.first),
                            altoRect = RectF(rects.second)
                        )
                    )
                }
            }
            aletaAnchoTextHit.setEmpty()
            aletaAltoTextHit.setEmpty()
            aletaAnchoTextHitRight.setEmpty()
            aletaAltoTextHitRight.setEmpty()
            aletaAnchoTextHitLeft.setEmpty()
            aletaAltoTextHitLeft.setEmpty()
        } else {
            aletaAnchoTextHit.setEmpty()
            aletaAltoTextHit.setEmpty()
            aletaAnchoTextHitRight.setEmpty()
            aletaAltoTextHitRight.setEmpty()
            aletaAnchoTextHitLeft.setEmpty()
            aletaAltoTextHitLeft.setEmpty()
            multiLadoCotaHits.clear()
        }
    }

    private fun drawMainOpening(canvas: Canvas) {
        val openingPath = buildOpeningPath()
        canvas.drawPath(openingPath, presetFillPaint)
        canvas.drawPath(openingPath, presetStrokePaint)
        when (geometryType) {
            GeometryType.ESQUINERO -> drawEsquineroSecondaryFace(canvas)
            GeometryType.C_TOP -> drawCTopFaces(canvas)
            GeometryType.MULTI_LADO -> drawMultiLadoFaces(canvas)
            else -> Unit
        }
    }
    private fun buildOpeningPath(): Path {
        return when (geometryType) {
            GeometryType.PLANO, GeometryType.ESQUINERO, GeometryType.C_TOP, GeometryType.MULTI_LADO -> Path().apply {
                moveTo(leftX, bottomY)
                lineTo(rightX, bottomY)
                lineTo(topRightX, topRightY)
                lineTo(topLeftX, topLeftY)
                close()
            }

            GeometryType.CURVO -> {
                val sagittaPx = (curvoFlechaEfectivaCm() * scalePxPerCm).coerceAtLeast(0f)
                val ax = topLeftX
                val ay = topLeftY
                val bx = topRightX
                val by = topRightY
                val mx = (ax + bx) * 0.5f
                val my = (ay + by) * 0.5f
                val vx = bx - ax
                val vy = by - ay
                val len = kotlin.math.hypot(vx.toDouble(), vy.toDouble()).toFloat().coerceAtLeast(0.001f)
                val nx1 = -vy / len
                val ny1 = vx / len
                val interiorTargetX = (leftX + rightX) * 0.5f
                val interiorTargetY = bottomY
                val dot1 = ((interiorTargetX - mx) * nx1) + ((interiorTargetY - my) * ny1)
                val interiorNx = if (dot1 >= 0f) nx1 else -nx1
                val interiorNy = if (dot1 >= 0f) ny1 else -ny1
                // En vista frontal, la percepción de interior/exterior queda invertida respecto al eje usado en top.
                val curveDir = if (curvoDirection == AletaDirection.INTERIOR) -1f else 1f
                val controlOffset = sagittaPx * 2f * curveDir
                val cx = mx + (interiorNx * controlOffset)
                val cy = my + (interiorNy * controlOffset)
                val bmx = (leftX + rightX) * 0.5f
                val bmy = bottomY
                val bcx = bmx - (interiorNx * controlOffset)
                val bcy = bmy - (interiorNy * controlOffset)

                Path().apply {
                    moveTo(leftX, bottomY)
                    quadTo(bcx, bcy, rightX, bottomY)
                    lineTo(topRightX, topRightY)
                    quadTo(cx, cy, topLeftX, topLeftY)
                    close()
                }
            }
        }
    }

    private fun drawCTopFaces(canvas: Canvas) {
        cTopRightAletaGeometry = drawEsquineroSecondaryFace(
            canvas = canvas,
            usarBordeDerecho = true,
            actualizarCotas = false,
            anguloDeg = cTopRightAnguloDeg,
            aletaAnchoCm = cTopRightAletaAnchoCm
        ).geometry
        cTopLeftAletaGeometry = drawEsquineroSecondaryFace(
            canvas = canvas,
            usarBordeDerecho = false,
            actualizarCotas = false,
            anguloDeg = cTopLeftAnguloDeg,
            aletaAnchoCm = cTopLeftAletaAnchoCm
        ).geometry
    }

    private fun drawMultiLadoFaces(canvas: Canvas) {
        multiLadoAletaGeometry.clear()
        val chain = if (multiLadoAletas.isNotEmpty()) multiLadoAletas else List(multiLadoCount.coerceAtLeast(1)) {
            MultiLadoAletaConfig(
                side = multiLadoSide,
                direction = multiLadoDirection,
                lado1Cm = esquineroLado1Cm,
                lado2Cm = esquineroLado2Cm,
                hipotenusaCm = esquineroHipotenusaCm,
                aletaAnchoCm = esquineroAletaAnchoCm,
                aletaAltoCm = esquineroAletaAltoCm
            )
        }
        val turnAxes = mutableListOf<TurnAxisGuide>()
        var previous: AletaDrawResult? = null
        chain.forEachIndexed { idx, cfg ->
            val angulo = calcularAnguloEsquinero(cfg.lado1Cm, cfg.lado2Cm, cfg.hipotenusaCm)
            val usePreviousEdge = (idx > 0 && previous != null)
            val result = if (usePreviousEdge) {
                val prev = previous!!
                turnAxes.add(TurnAxisGuide(top = PointF(prev.outerTop.x, prev.outerTop.y), bottom = PointF(prev.outerBottom.x, prev.outerBottom.y)))
                val prevVx = prev.geometry.p3x - prev.geometry.p2x
                val prevVy = prev.geometry.p3y - prev.geometry.p2y
                val prevLen = kotlin.math.hypot(prevVx.toDouble(), prevVy.toDouble()).toFloat().coerceAtLeast(1f)
                val baseNx = prevVx / prevLen
                val baseNy = prevVy / prevLen
                val turnedDir = rotateVectorTowardSide(baseNx, baseNy, cfg.side, angulo)
                val factorApertura = ((angulo.coerceIn(15f, 165f) - 15f) / (165f - 15f)).coerceIn(0.25f, 1f)
                val lenPx = (cfg.aletaAnchoCm.coerceAtLeast(1f) * factorApertura * scalePxPerCm).coerceAtLeast(12f)
                drawAletaFromEdgeWithVector(
                    canvas = canvas,
                    edgeTop = prev.outerTop,
                    edgeBottom = prev.outerBottom,
                    vx = turnedDir.x * lenPx,
                    vy = turnedDir.y * lenPx,
                    actualizarCotas = false
                )
            } else {
                drawEsquineroSecondaryFace(
                    canvas = canvas,
                    usarBordeDerecho = cfg.side == AletaSide.RIGHT,
                    actualizarCotas = false,
                    anguloDeg = angulo,
                    aletaAnchoCm = cfg.aletaAnchoCm,
                    directionOverride = cfg.direction
                )
            }
            multiLadoAletaGeometry.add(result.geometry)
            previous = result
        }
        drawTurnAxes(canvas, turnAxes)
    }

    private fun drawTurnAxes(canvas: Canvas, axes: List<TurnAxisGuide>) {
        axes.forEachIndexed { idx, axis ->
            canvas.drawLine(axis.top.x, axis.top.y, axis.bottom.x, axis.bottom.y, multiTurnAxisPaint)
            canvas.drawCircle(axis.top.x, axis.top.y, 5f, multiTurnAxisNodePaint)
            canvas.drawCircle(axis.bottom.x, axis.bottom.y, 5f, multiTurnAxisNodePaint)
            val mx = (axis.top.x + axis.bottom.x) * 0.5f
            val my = (axis.top.y + axis.bottom.y) * 0.5f
            canvas.drawText("Eje ${idx + 2}", mx + 8f, my - 8f, multiTurnAxisTextPaint)
        }
    }

    private fun drawTopGuideOverlay(canvas: Canvas) {
        if (presetRect.isEmpty) return
        if (!showMultiDialogTopGuide && geometryType == GeometryType.PLANO) return

        val boxWidth = minOf(width * 0.34f, 360f)
        val boxHeight = minOf(height * 0.27f, 260f)
        val boxLeft = (width - boxWidth - 12f).coerceAtLeast(8f)
        val boxTop = (ceilingBottomY + 10f).coerceAtLeast(8f)
        val boxRect = RectF(boxLeft, boxTop, boxLeft + boxWidth, boxTop + boxHeight)
        canvas.drawRoundRect(boxRect, 14f, 14f, topGuidePanelPaint)
        canvas.drawRoundRect(boxRect, 14f, 14f, topGuideBorderPaint)

        val titleY = boxRect.top + 30f
        canvas.drawText("Vista top (guia)", boxRect.left + 12f, titleY, topGuideTextPaint)
        val legendY = titleY + 24f
        canvas.drawText("Int", boxRect.left + 12f, legendY, topGuideLineInteriorPaint)
        canvas.drawText("Ext", boxRect.left + 64f, legendY, topGuideLineExteriorPaint)
        canvas.drawText("Afuera", boxRect.right - 84f, titleY - 2f, topGuideTextPaint)
        canvas.drawText("Adentro", boxRect.right - 90f, boxRect.bottom - 10f, topGuideTextPaint)

        if (geometryType == GeometryType.CURVO) {
            drawCurvoTopGuide(canvas, boxRect, legendY)
            return
        }
        if (geometryType == GeometryType.ESQUINERO && esquineroVariant != EsquineroVariant.RECTO) {
            drawEsquineroArcTopGuide(
                canvas = canvas,
                boxRect = boxRect,
                legendY = legendY,
                includeRectoTail = esquineroVariant == EsquineroVariant.ARCO_RECTO
            )
            return
        }

        val segments = buildTopGuideSegments()
        if (segments.isEmpty()) return

        var minX = Float.POSITIVE_INFINITY
        var minY = Float.POSITIVE_INFINITY
        var maxX = Float.NEGATIVE_INFINITY
        var maxY = Float.NEGATIVE_INFINITY
        val productHalf = topGuideProductHalfCm()
        minX = minOf(minX, -productHalf)
        maxX = maxOf(maxX, productHalf)
        minY = minOf(minY, 0f)
        maxY = maxOf(maxY, 0f)
        segments.forEach { s ->
            minX = minOf(minX, s.startX, s.endX)
            minY = minOf(minY, s.startY, s.endY)
            maxX = maxOf(maxX, s.startX, s.endX)
            maxY = maxOf(maxY, s.startY, s.endY)
        }
        val modelW = (maxX - minX).coerceAtLeast(1f)
        val modelH = (maxY - minY).coerceAtLeast(1f)
        val contentRect = RectF(
            boxRect.left + 12f,
            legendY + 10f,
            boxRect.right - 12f,
            boxRect.bottom - 12f
        )
        val scale = minOf(
            contentRect.width() / modelW,
            contentRect.height() / modelH
        ).coerceAtLeast(0.2f)
        val dx = contentRect.left + ((contentRect.width() - (modelW * scale)) * 0.5f) - (minX * scale)
        val dy = contentRect.top + ((contentRect.height() - (modelH * scale)) * 0.5f) - (minY * scale)

        val productY = (0f * scale) + dy
        val productX1 = (-productHalf * scale) + dx
        val productX2 = (productHalf * scale) + dx
        canvas.drawLine(productX1, productY, productX2, productY, topGuideBorderPaint)
        canvas.drawCircle(productX1, productY, 3.2f, topGuideNodePaint)
        canvas.drawCircle(productX2, productY, 3.2f, topGuideNodePaint)
        canvas.drawText("Producto", productX1 + 4f, productY - 6f, topGuideTextPaint)

        segments.forEachIndexed { idx, s ->
            val sx = (s.startX * scale) + dx
            val sy = (s.startY * scale) + dy
            val ex = (s.endX * scale) + dx
            val ey = (s.endY * scale) + dy
            val linePaint = if (s.direction == AletaDirection.INTERIOR) topGuideLineInteriorPaint else topGuideLineExteriorPaint
            canvas.drawLine(sx, sy, ex, ey, linePaint)
            canvas.drawCircle(ex, ey, 3.6f, topGuideNodePaint)
            val angleLabel = if (s.anguloDeg > 0f) "${idx + 1}:${formatTopAngle(s.anguloDeg)}°" else (idx + 1).toString()
            val anchoLabel = formatCota(s.anchoCm)
            canvas.drawText("$angleLabel | A:$anchoLabel", ex + 4f, ey - 4f, topGuideTextPaint)
        }

        if (showMultiDialogTopGuide && segments.isNotEmpty()) {
            val last = segments.last()
            val px = (last.endX * scale) + dx
            val py = (last.endY * scale) + dy
            canvas.drawCircle(px, py, 6.8f, topGuideNextPointPaint)
            canvas.drawCircle(px, py, 9.6f, topGuideNextPointStrokePaint)
            canvas.drawText("Siguiente giro", px + 10f, py + 16f, topGuideTextPaint)
        }
    }

    private fun drawCurvoTopGuide(canvas: Canvas, boxRect: RectF, legendY: Float) {
        val cuerdaCm = curvoCuerdaCm.coerceAtLeast(1f)
        val desarrolloCm = curvoDesarrolloCm.coerceAtLeast(cuerdaCm)
        val flechaCm = curvoFlechaEfectivaCm().coerceAtLeast(0f)
        val directionSign = if (curvoDirection == AletaDirection.INTERIOR) 1f else -1f
        val apexYModel = directionSign * flechaCm

        val contentRect = RectF(
            boxRect.left + 12f,
            legendY + 10f,
            boxRect.right - 12f,
            boxRect.bottom - 12f
        )

        val minX = -cuerdaCm * 0.5f
        val maxX = cuerdaCm * 0.5f
        val padY = maxOf(6f, flechaCm * 0.55f)
        val minY = minOf(0f, apexYModel) - padY
        val maxY = maxOf(0f, apexYModel) + padY
        val modelW = (maxX - minX).coerceAtLeast(1f)
        val modelH = (maxY - minY).coerceAtLeast(1f)
        val scale = minOf(
            contentRect.width() / modelW,
            contentRect.height() / modelH
        ).coerceAtLeast(0.2f)
        val dx = contentRect.left + ((contentRect.width() - (modelW * scale)) * 0.5f) - (minX * scale)
        val dy = contentRect.top + ((contentRect.height() - (modelH * scale)) * 0.5f) - (minY * scale)

        fun pxX(xModel: Float): Float = (xModel * scale) + dx
        fun pxY(yModel: Float): Float = (yModel * scale) + dy

        val x1 = pxX(-cuerdaCm * 0.5f)
        val x2 = pxX(cuerdaCm * 0.5f)
        val yChord = pxY(0f)
        val xMid = pxX(0f)
        val yApex = pxY(apexYModel)
        val yControl = pxY(apexYModel * 2f)

        val linePaint = if (curvoDirection == AletaDirection.INTERIOR) topGuideLineInteriorPaint else topGuideLineExteriorPaint
        val arcPath = Path().apply {
            moveTo(x1, yChord)
            quadTo(xMid, yControl, x2, yChord)
        }

        canvas.drawPath(arcPath, linePaint)
        canvas.drawLine(x1, yChord, x2, yChord, topGuideBorderPaint)
        canvas.drawCircle(x1, yChord, 3.2f, topGuideNodePaint)
        canvas.drawCircle(x2, yChord, 3.2f, topGuideNodePaint)
        canvas.drawCircle(xMid, yApex, 3.6f, topGuideNodePaint)

        val labelOffsetY = if (curvoDirection == AletaDirection.INTERIOR) -8f else 16f
        canvas.drawText("C:${formatCota(cuerdaCm)}", xMid + 6f, yChord + labelOffsetY, topGuideTextPaint)
        canvas.drawLine(xMid, yChord, xMid, yApex, topGuideBorderPaint)
        val fy = (yChord + yApex) * 0.5f + if (curvoDirection == AletaDirection.INTERIOR) -4f else 12f
        canvas.drawText("F:${formatCota(flechaCm)}", xMid + 8f, fy, topGuideTextPaint)
        val dyText = if (curvoDirection == AletaDirection.INTERIOR) 14f else -10f
        canvas.drawText("D:${formatCota(desarrolloCm)}", xMid + 6f, yApex + dyText, topGuideTextPaint)
    }

    private fun drawEsquineroArcTopGuide(
        canvas: Canvas,
        boxRect: RectF,
        legendY: Float,
        includeRectoTail: Boolean
    ) {
        val seg = buildSingleTopSegment(
            side = esquineroAletaSide,
            direction = esquineroAletaDirection,
            anguloDeg = esquineroAnguloDeg,
            anchoCm = esquineroAletaAnchoCm
        )
        val seamT = if (includeRectoTail) 0.58f else 1f
        val sx = seg.startX
        val sy = seg.startY
        val ex = seg.endX
        val ey = seg.endY
        val mx = sx + ((ex - sx) * seamT)
        val my = sy + ((ey - sy) * seamT)

        val segDx = mx - sx
        val segDy = my - sy
        val segLen = kotlin.math.hypot(segDx.toDouble(), segDy.toDouble()).toFloat().coerceAtLeast(0.001f)
        val nx = -segDy / segLen
        val ny = segDx / segLen
        val sideSign = if (esquineroAletaSide == AletaSide.RIGHT) 1f else -1f
        val dirSign = if (esquineroAletaDirection == AletaDirection.INTERIOR) 1f else -1f
        val bulge = segLen * 0.28f * sideSign * dirSign
        val cx = (sx + mx) * 0.5f + (nx * bulge)
        val cy = (sy + my) * 0.5f + (ny * bulge)

        var minX = minOf(-topGuideProductHalfCm(), topGuideProductHalfCm(), sx, ex, mx, cx)
        var minY = minOf(0f, sy, ey, my, cy)
        var maxX = maxOf(-topGuideProductHalfCm(), topGuideProductHalfCm(), sx, ex, mx, cx)
        var maxY = maxOf(0f, sy, ey, my, cy)
        if (includeRectoTail) {
            minX = minOf(minX, ex)
            minY = minOf(minY, ey)
            maxX = maxOf(maxX, ex)
            maxY = maxOf(maxY, ey)
        }

        val modelW = (maxX - minX).coerceAtLeast(1f)
        val modelH = (maxY - minY).coerceAtLeast(1f)
        val contentRect = RectF(
            boxRect.left + 12f,
            legendY + 10f,
            boxRect.right - 12f,
            boxRect.bottom - 12f
        )
        val scale = minOf(contentRect.width() / modelW, contentRect.height() / modelH).coerceAtLeast(0.2f)
        val dx = contentRect.left + ((contentRect.width() - (modelW * scale)) * 0.5f) - (minX * scale)
        val dy = contentRect.top + ((contentRect.height() - (modelH * scale)) * 0.5f) - (minY * scale)
        fun pxX(xModel: Float): Float = (xModel * scale) + dx
        fun pxY(yModel: Float): Float = (yModel * scale) + dy

        val productHalf = topGuideProductHalfCm()
        val productY = pxY(0f)
        canvas.drawLine(pxX(-productHalf), productY, pxX(productHalf), productY, topGuideBorderPaint)
        canvas.drawCircle(pxX(-productHalf), productY, 3.2f, topGuideNodePaint)
        canvas.drawCircle(pxX(productHalf), productY, 3.2f, topGuideNodePaint)
        canvas.drawText("Producto", pxX(-productHalf) + 4f, productY - 6f, topGuideTextPaint)

        val linePaint = if (esquineroAletaDirection == AletaDirection.INTERIOR) topGuideLineInteriorPaint else topGuideLineExteriorPaint
        val arcPath = Path().apply {
            moveTo(pxX(sx), pxY(sy))
            quadTo(pxX(cx), pxY(cy), pxX(mx), pxY(my))
        }
        canvas.drawPath(arcPath, linePaint)
        if (includeRectoTail) {
            canvas.drawLine(pxX(mx), pxY(my), pxX(ex), pxY(ey), linePaint)
        }
        canvas.drawCircle(pxX(ex), pxY(ey), 3.6f, topGuideNodePaint)
        val angleLabel = formatTopAngle(esquineroAnguloDeg)
        val anchoLabel = formatCota(esquineroAletaAnchoCm)
        val tipo = if (includeRectoTail) "Arco+R" else "Arco"
        canvas.drawText("$tipo $angleLabel° | A:$anchoLabel", pxX(ex) + 4f, pxY(ey) - 4f, topGuideTextPaint)
    }

    private fun buildTopGuideSegments(): List<TopGuideSegment> {
        if (showMultiDialogTopGuide) {
            return buildMultiTopSegments()
        }
        return when (geometryType) {
            GeometryType.ESQUINERO -> {
                listOf(
                    buildSingleTopSegment(
                        side = esquineroAletaSide,
                        direction = esquineroAletaDirection,
                        anguloDeg = esquineroAnguloDeg,
                        anchoCm = esquineroAletaAnchoCm
                    )
                )
            }
            GeometryType.C_TOP -> {
                listOf(
                    buildSingleTopSegment(
                        side = AletaSide.RIGHT,
                        direction = esquineroAletaDirection,
                        anguloDeg = cTopRightAnguloDeg,
                        anchoCm = cTopRightAletaAnchoCm
                    ),
                    buildSingleTopSegment(
                        side = AletaSide.LEFT,
                        direction = esquineroAletaDirection,
                        anguloDeg = cTopLeftAnguloDeg,
                        anchoCm = cTopLeftAletaAnchoCm
                    )
                )
            }
            GeometryType.MULTI_LADO -> buildMultiTopSegments()
            GeometryType.CURVO -> {
                val flecha = curvoFlechaEfectivaCm().coerceAtLeast(0f)
                if (flecha <= 0f) {
                    emptyList()
                } else {
                    val destinoY = if (curvoDirection == AletaDirection.INTERIOR) flecha else -flecha
                    listOf(
                        TopGuideSegment(
                            startX = -topGuideProductHalfCm(),
                            startY = 0f,
                            endX = 0f,
                            endY = destinoY,
                            direction = curvoDirection,
                            anguloDeg = 0f,
                            anchoCm = curvoCuerdaCm.coerceAtLeast(1f)
                        ),
                        TopGuideSegment(
                            startX = topGuideProductHalfCm(),
                            startY = 0f,
                            endX = 0f,
                            endY = destinoY,
                            direction = curvoDirection,
                            anguloDeg = 0f,
                            anchoCm = curvoCuerdaCm.coerceAtLeast(1f)
                        )
                    )
                }
            }
            else -> emptyList()
        }
    }

    private fun buildSingleTopSegment(
        side: AletaSide,
        direction: AletaDirection,
        anguloDeg: Float,
        anchoCm: Float,
        originX: Float = if (side == AletaSide.RIGHT) topGuideProductHalfCm() else -topGuideProductHalfCm()
    ): TopGuideSegment {
        val len = anchoCm.coerceAtLeast(1f)
        val dir = enforceTopDirectionHalf(computeTopInitialUnitVector(side, direction, anguloDeg), direction)
        val endX = originX + (dir.x * len)
        val endY = dir.y * len
        return TopGuideSegment(
            startX = originX,
            startY = 0f,
            endX = endX,
            endY = endY,
            direction = direction,
            anguloDeg = anguloDeg,
            anchoCm = anchoCm.coerceAtLeast(1f)
        )
    }

    private fun buildMultiTopSegments(): List<TopGuideSegment> {
        val chain = if (multiLadoAletas.isNotEmpty()) multiLadoAletas else List(multiLadoCount.coerceAtLeast(1)) {
            MultiLadoAletaConfig(
                side = multiLadoSide,
                direction = multiLadoDirection,
                lado1Cm = esquineroLado1Cm,
                lado2Cm = esquineroLado2Cm,
                hipotenusaCm = esquineroHipotenusaCm,
                aletaAnchoCm = esquineroAletaAnchoCm,
                aletaAltoCm = esquineroAletaAltoCm
            )
        }
        if (chain.isEmpty()) return emptyList()
        val result = mutableListOf<TopGuideSegment>()
        val first = chain.first()
        var cx = if (first.side == AletaSide.RIGHT) topGuideProductHalfCm() else -topGuideProductHalfCm()
        var cy = 0f
        val firstAngulo = calcularAnguloEsquinero(first.lado1Cm, first.lado2Cm, first.hipotenusaCm)
        var dir = enforceTopDirectionHalf(computeTopInitialUnitVector(first.side, first.direction, firstAngulo), first.direction)
        var prevDirection = first.direction
        chain.forEachIndexed { idx, cfg ->
            val angulo = calcularAnguloEsquinero(cfg.lado1Cm, cfg.lado2Cm, cfg.hipotenusaCm)
            if (idx > 0) {
                dir = rotateVectorTowardSide(dir.x, dir.y, cfg.side, angulo)
                if (cfg.direction != prevDirection) {
                    // Solo forzamos "arriba/abajo" cuando cambia explícitamente la dirección.
                    // Evita reflejos espurios al cruzar el eje horizontal en giros laterales.
                    dir = enforceTopDirectionHalf(dir, cfg.direction)
                }
            }
            val len = cfg.aletaAnchoCm.coerceAtLeast(1f)
            val nx = cx + (dir.x * len)
            val ny = cy + (dir.y * len)
            result.add(
                TopGuideSegment(
                    startX = cx,
                    startY = cy,
                    endX = nx,
                    endY = ny,
                    direction = cfg.direction,
                    anguloDeg = angulo,
                    anchoCm = cfg.aletaAnchoCm.coerceAtLeast(1f)
                )
            )
            cx = nx
            cy = ny
            prevDirection = cfg.direction
        }
        return result
    }

    private fun formatTopAngle(value: Float): String {
        return if (value % 1f == 0f) value.toInt().toString() else String.format(java.util.Locale.US, "%.1f", value)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun computeTopInitialUnitVector(
        side: AletaSide,
        direction: AletaDirection,
        anguloDeg: Float
    ): PointF {
        // Primera aleta (vista top):
        // - Eje base = eje del producto hacia el interior del vano.
        // - El ángulo se mide respecto a ese eje base.
        // - Interior/Exterior define el semiplano (abajo/arriba en pantalla).
        val baseX = if (side == AletaSide.RIGHT) -1f else 1f
        val theta = Math.toRadians(anguloDeg.coerceIn(0f, 180f).toDouble()).toFloat()
        val cosT = kotlin.math.cos(theta)
        val sinT = kotlin.math.sin(theta)

        val x = baseX * cosT
        val yAbs = kotlin.math.abs(sinT)
        val y = if (direction == AletaDirection.INTERIOR) yAbs else -yAbs
        val len = kotlin.math.hypot(x.toDouble(), y.toDouble()).toFloat().coerceAtLeast(0.001f)
        return PointF(x / len, y / len)
    }

    private fun topGuideProductHalfCm(): Float {
        // En vista top, el ancho del producto debe seguir la edición actual del producto (presetWidth).
        return maxOf(presetWidth, 40f) * 0.5f
    }

    private fun rotateVectorTowardSide(baseX: Float, baseY: Float, side: AletaSide, angleDeg: Float): PointF {
        val len = kotlin.math.hypot(baseX.toDouble(), baseY.toDouble()).toFloat().coerceAtLeast(0.001f)
        val tx = baseX / len
        val ty = baseY / len
        val theta = Math.toRadians(angleDeg.coerceIn(0f, 180f).toDouble()).toFloat()
        val cosT = kotlin.math.cos(theta)
        val sinT = kotlin.math.sin(theta)

        val rightNx = ty
        val rightNy = -tx
        val leftNx = -ty
        val leftNy = tx

        val candRightX = (tx * cosT) + (rightNx * sinT)
        val candRightY = (ty * cosT) + (rightNy * sinT)
        val candLeftX = (tx * cosT) + (leftNx * sinT)
        val candLeftY = (ty * cosT) + (leftNy * sinT)

        // En vista top, "derecha/izquierda" debe ser global en pantalla.
        // Elegimos la rama que más se desplaza en X al lado solicitado.
        val useRightCandidate = if (side == AletaSide.RIGHT) {
            candRightX >= candLeftX
        } else {
            candRightX > candLeftX
        }

        val rx = if (useRightCandidate) candRightX else candLeftX
        val ry = if (useRightCandidate) candRightY else candLeftY
        val outLen = kotlin.math.hypot(rx.toDouble(), ry.toDouble()).toFloat().coerceAtLeast(0.001f)
        return PointF(rx / outLen, ry / outLen)
    }

    private fun enforceTopDirectionHalf(dir: PointF, direction: AletaDirection): PointF {
        val desiredInterior = direction == AletaDirection.INTERIOR
        var x = dir.x
        var y = dir.y
        if (desiredInterior && y < 0f) y *= -1f
        if (!desiredInterior && y > 0f) y *= -1f
        val len = kotlin.math.hypot(x.toDouble(), y.toDouble()).toFloat().coerceAtLeast(0.001f)
        return PointF(x / len, y / len)
    }

    private fun formatCota(value: Float): String {
        return if (value % 1f == 0f) value.toInt().toString() else String.format(java.util.Locale.US, "%.1f", value)
    }

    private fun updateTextHitRect(rect: RectF, text: String, cx: Float, cy: Float) {
        val w = coteTextPaint.measureText(text)
        val fm = coteTextPaint.fontMetrics
        val h = fm.bottom - fm.top
        rect.set(
            cx - (w * 0.5f) - 16f,
            cy - h - 16f,
            cx + (w * 0.5f) + 16f,
            cy + 16f
        )
    }

    private fun unionRects(a: RectF?, b: RectF?): RectF? {
        if (a == null && b == null) return null
        if (a == null) return RectF(b)
        if (b == null) return RectF(a)
        return RectF(
            minOf(a.left, b.left),
            minOf(a.top, b.top),
            maxOf(a.right, b.right),
            maxOf(a.bottom, b.bottom)
        )
    }

    private fun drawAletaCotas(
        canvas: Canvas,
        geometry: AletaGeometry,
        anchoCm: Float,
        altoCm: Float,
        updateHit: Boolean
    ): Pair<RectF, RectF> {
        val p2x = geometry.p2x
        val p2y = geometry.p2y
        val p3x = geometry.p3x
        val p3y = geometry.p3y
        val p4x = geometry.p4x
        val p4y = geometry.p4y

        val anchoText = formatCota(anchoCm.coerceAtLeast(1f))
        val anchoX = (p2x + p3x) * 0.5f
        val anchoY = (p2y + p3y) * 0.5f - 10f
        canvas.drawText(anchoText, anchoX, anchoY, coteTextPaint)
        val anchoRect = RectF()
        updateTextHitRect(anchoRect, anchoText, anchoX, anchoY)
        if (updateHit) aletaAnchoTextHit.set(anchoRect)

        val altoText = formatCota(altoCm.coerceAtLeast(1f))
        val altoX = (p3x + p4x) * 0.5f + 26f
        val altoY = (p3y + p4y) * 0.5f
        canvas.drawText(altoText, altoX, altoY, coteTextPaint)
        val altoRect = RectF()
        updateTextHitRect(altoRect, altoText, altoX, altoY)
        if (updateHit) aletaAltoTextHit.set(altoRect)

        return Pair(anchoRect, altoRect)
    }

    private fun getTappedSide(x: Float, y: Float): Side? {
        return when {
            topTextHit.contains(x, y) -> Side.TOP
            rightTextHit.contains(x, y) -> Side.RIGHT
            bottomTextHit.contains(x, y) -> Side.BOTTOM
            leftTextHit.contains(x, y) -> Side.LEFT
            else -> null
        }
    }

    private fun getTappedAletaAnchoSide(x: Float, y: Float): AletaSide? {
        return when (geometryType) {
            GeometryType.C_TOP -> when {
                aletaAnchoTextHitRight.contains(x, y) -> AletaSide.RIGHT
                aletaAnchoTextHitLeft.contains(x, y) -> AletaSide.LEFT
                else -> null
            }
            GeometryType.ESQUINERO -> if (aletaAnchoTextHit.contains(x, y)) esquineroAletaSide else null
            else -> null
        }
    }

    private fun getTappedAletaAltoSide(x: Float, y: Float): AletaSide? {
        return when (geometryType) {
            GeometryType.C_TOP -> when {
                aletaAltoTextHitRight.contains(x, y) -> AletaSide.RIGHT
                aletaAltoTextHitLeft.contains(x, y) -> AletaSide.LEFT
                else -> null
            }
            GeometryType.ESQUINERO -> if (aletaAltoTextHit.contains(x, y)) esquineroAletaSide else null
            else -> null
        }
    }

    private fun getTappedMultiLadoAletaAnchoIndex(x: Float, y: Float): Int? {
        val hit = multiLadoCotaHits.firstOrNull { it.anchoRect.contains(x, y) }
        return hit?.index
    }

    private fun getTappedMultiLadoAletaAltoIndex(x: Float, y: Float): Int? {
        val hit = multiLadoCotaHits.firstOrNull { it.altoRect.contains(x, y) }
        return hit?.index
    }

    private fun showSideInputDialog(side: Side) {
        val current = when (side) {
            Side.TOP -> sideTop
            Side.RIGHT -> sideRight
            Side.BOTTOM -> sideBottom
            Side.LEFT -> sideLeft
        }
        val input = EditText(context).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatCota(current))
            setSelection(text?.length ?: 0)
        }
        AlertDialog.Builder(context)
            .setTitle("Editar cota")
            .setView(input)
            .setPositiveButton("Aplicar") { _, _ ->
                val value = input.text?.toString()?.toFloatOrNull()?.coerceAtLeast(1f) ?: return@setPositiveButton
                val previous = current
                when (side) {
                    Side.TOP -> sideTop = value
                    Side.RIGHT -> sideRight = value
                    Side.BOTTOM -> sideBottom = value
                    Side.LEFT -> sideLeft = value
                }
                presetWidth = ((sideTop + sideBottom) * 0.5f).coerceAtLeast(1f)
                presetHeight = ((sideLeft + sideRight) * 0.5f).coerceAtLeast(1f)
                onSideMeasurementChanged?.invoke(side, previous, value)
                contextDirty = true
                recomputePresetRect()
                invalidate()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAletaAnchoInputDialog(side: AletaSide) {
        val current = if (geometryType == GeometryType.C_TOP) {
            if (side == AletaSide.RIGHT) cTopRightAletaAnchoCm else cTopLeftAletaAnchoCm
        } else {
            esquineroAletaAnchoCm
        }
        val input = EditText(context).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatCota(current))
            setSelection(text?.length ?: 0)
        }
        AlertDialog.Builder(context)
            .setTitle("Editar ancho de aleta")
            .setView(input)
            .setPositiveButton("Aplicar") { _, _ ->
                val nuevo = input.text?.toString()?.toFloatOrNull()?.coerceAtLeast(1f) ?: return@setPositiveButton
                val previo = current
                if (geometryType == GeometryType.C_TOP) {
                    if (side == AletaSide.RIGHT) cTopRightAletaAnchoCm = nuevo else cTopLeftAletaAnchoCm = nuevo
                } else {
                    esquineroAletaAnchoCm = nuevo
                }
                onEsquineroAletaAnchoChanged?.invoke(previo, nuevo)
                contextDirty = true
                recomputePresetRect()
                invalidate()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showAletaAltoInputDialog(side: AletaSide) {
        val current = if (geometryType == GeometryType.C_TOP) {
            if (side == AletaSide.RIGHT) cTopRightAletaAltoCm else cTopLeftAletaAltoCm
        } else {
            esquineroAletaAltoCm
        }
        val input = EditText(context).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatCota(current))
            setSelection(text?.length ?: 0)
        }
        AlertDialog.Builder(context)
            .setTitle("Editar alto de aleta")
            .setView(input)
            .setPositiveButton("Aplicar") { _, _ ->
                val nuevo = input.text?.toString()?.toFloatOrNull()?.coerceAtLeast(1f) ?: return@setPositiveButton
                val previo = current
                if (geometryType == GeometryType.C_TOP) {
                    if (side == AletaSide.RIGHT) cTopRightAletaAltoCm = nuevo else cTopLeftAletaAltoCm = nuevo
                } else {
                    esquineroAletaAltoCm = nuevo
                }
                onEsquineroAletaAltoChanged?.invoke(previo, nuevo)
                contextDirty = true
                recomputePresetRect()
                invalidate()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showMultiLadoAletaAnchoInputDialog(index: Int) {
        val actual = multiLadoAletas.getOrNull(index)?.aletaAnchoCm ?: return
        val input = EditText(context).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatCota(actual))
            setSelection(text?.length ?: 0)
        }
        AlertDialog.Builder(context)
            .setTitle("Editar ancho de aleta #${index + 1}")
            .setView(input)
            .setPositiveButton("Aplicar") { _, _ ->
                val nuevo = input.text?.toString()?.toFloatOrNull()?.coerceAtLeast(1f) ?: return@setPositiveButton
                val previo = actual
                val lista = multiLadoAletas.toMutableList()
                val item = lista[index]
                lista[index] = item.copy(aletaAnchoCm = nuevo)
                multiLadoAletas.clear()
                multiLadoAletas.addAll(lista)
                onMultiLadoAletaAnchoChanged?.invoke(index, previo, nuevo)
                contextDirty = true
                recomputePresetRect()
                invalidate()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showMultiLadoAletaAltoInputDialog(index: Int) {
        val actual = multiLadoAletas.getOrNull(index)?.aletaAltoCm ?: return
        val input = EditText(context).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(formatCota(actual))
            setSelection(text?.length ?: 0)
        }
        AlertDialog.Builder(context)
            .setTitle("Editar alto de aleta #${index + 1}")
            .setView(input)
            .setPositiveButton("Aplicar") { _, _ ->
                val nuevo = input.text?.toString()?.toFloatOrNull()?.coerceAtLeast(1f) ?: return@setPositiveButton
                val previo = actual
                val lista = multiLadoAletas.toMutableList()
                val item = lista[index]
                lista[index] = item.copy(aletaAltoCm = nuevo)
                multiLadoAletas.clear()
                multiLadoAletas.addAll(lista)
                onMultiLadoAletaAltoChanged?.invoke(index, previo, nuevo)
                contextDirty = true
                recomputePresetRect()
                invalidate()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun curvoFlechaEfectivaCm(): Float {
        return (curvoFlechaCm ?: calcularFlechaPorDesarrolloYCuerda(curvoDesarrolloCm, curvoCuerdaCm) ?: 0f)
            .coerceAtLeast(0f)
    }

    private fun calcularFlechaPorDesarrolloYCuerda(desarrolloCm: Float, cuerdaCm: Float): Float? {
        if (desarrolloCm <= 0f || cuerdaCm <= 0f || desarrolloCm < cuerdaCm) return null
        val ratio = (cuerdaCm / desarrolloCm).coerceIn(0f, 1f)
        if (kotlin.math.abs(1f - ratio) < 1e-5f) return 0f

        var low = 1e-4
        var high = Math.PI - 1e-4
        repeat(72) {
            val mid = (low + high) * 0.5
            val value = sin(mid) / mid
            if (value > ratio) {
                low = mid
            } else {
                high = mid
            }
        }
        val x = ((low + high) * 0.5).toFloat()
        val radius = desarrolloCm / (2f * x)
        val halfChord = cuerdaCm * 0.5f
        val under = ((radius * radius) - (halfChord * halfChord)).coerceAtLeast(0f)
        return (radius - sqrt(under)).coerceAtLeast(0f)
    }

    private fun drawAletaFromEdgeWithVector(
        canvas: Canvas,
        edgeTop: PointF,
        edgeBottom: PointF,
        vx: Float,
        vy: Float,
        actualizarCotas: Boolean
    ): AletaDrawResult {
        val p2x = edgeTop.x
        val p2y = edgeTop.y
        val p1x = edgeBottom.x
        val p1y = edgeBottom.y
        val p3x = p2x + vx
        val p3y = p2y + vy
        val p4x = p1x + vx
        val p4y = p1y + vy
        val sidePath = Path().apply {
            moveTo(p2x, p2y)
            lineTo(p3x, p3y)
            lineTo(p4x, p4y)
            lineTo(p1x, p1y)
            close()
        }
        if (actualizarCotas) {
            aletaP2x = p2x
            aletaP2y = p2y
            aletaP3x = p3x
            aletaP3y = p3y
            aletaP4x = p4x
            aletaP4y = p4y
            aletaCenterX = (p1x + p2x + p3x + p4x) * 0.25f
            aletaCenterY = (p1y + p2y + p3y + p4y) * 0.25f
        }
        val geometry = AletaGeometry(
            p2x = p2x,
            p2y = p2y,
            p3x = p3x,
            p3y = p3y,
            p4x = p4x,
            p4y = p4y,
            centerX = (p1x + p2x + p3x + p4x) * 0.25f,
            centerY = (p1y + p2y + p3y + p4y) * 0.25f
        )
        canvas.drawPath(sidePath, secondaryFaceFillPaint)
        canvas.drawPath(sidePath, secondaryFaceStrokePaint)
        canvas.drawLine(p2x, p2y, p3x, p3y, secondaryFaceStrokePaint)
        canvas.drawLine(p1x, p1y, p4x, p4y, secondaryFaceStrokePaint)
        return AletaDrawResult(
            geometry = geometry,
            outerTop = PointF(p3x, p3y),
            outerBottom = PointF(p4x, p4y)
        )
    }

    private fun drawEsquineroSecondaryFace(canvas: Canvas) {
        cTopRightAletaGeometry = null
        cTopLeftAletaGeometry = null
        when (esquineroVariant) {
            EsquineroVariant.RECTO -> {
                drawEsquineroSecondaryFace(
                    canvas = canvas,
                    usarBordeDerecho = esquineroAletaSide == AletaSide.RIGHT,
                    actualizarCotas = true,
                    anguloDeg = esquineroAnguloDeg,
                    aletaAnchoCm = esquineroAletaAnchoCm
                )
            }
            EsquineroVariant.ARCO -> {
                drawEsquineroSecondaryFaceArc(
                    canvas = canvas,
                    usarBordeDerecho = esquineroAletaSide == AletaSide.RIGHT,
                    actualizarCotas = true,
                    anguloDeg = esquineroAnguloDeg,
                    aletaAnchoCm = esquineroAletaAnchoCm,
                    includeRectoTail = false
                )
            }
            EsquineroVariant.ARCO_RECTO -> {
                drawEsquineroSecondaryFaceArc(
                    canvas = canvas,
                    usarBordeDerecho = esquineroAletaSide == AletaSide.RIGHT,
                    actualizarCotas = true,
                    anguloDeg = esquineroAnguloDeg,
                    aletaAnchoCm = esquineroAletaAnchoCm,
                    includeRectoTail = true
                )
            }
        }
    }

    private fun drawEsquineroSecondaryFace(
        canvas: Canvas,
        usarBordeDerecho: Boolean,
        actualizarCotas: Boolean,
        anguloDeg: Float,
        aletaAnchoCm: Float,
        directionOverride: AletaDirection? = null
    ): AletaDrawResult {
        val edgeTop = if (usarBordeDerecho) PointF(topRightX, topRightY) else PointF(topLeftX, topLeftY)
        val edgeBottom = if (usarBordeDerecho) PointF(rightX, bottomY) else PointF(leftX, bottomY)
        return drawAletaFromEdge(
            canvas = canvas,
            edgeTop = edgeTop,
            edgeBottom = edgeBottom,
            anchorSide = if (usarBordeDerecho) AletaSide.RIGHT else AletaSide.LEFT,
            direction = directionOverride ?: esquineroAletaDirection,
            anguloDeg = anguloDeg,
            aletaAnchoCm = aletaAnchoCm,
            actualizarCotas = actualizarCotas
        )
    }

    private fun drawEsquineroSecondaryFaceArc(
        canvas: Canvas,
        usarBordeDerecho: Boolean,
        actualizarCotas: Boolean,
        anguloDeg: Float,
        aletaAnchoCm: Float,
        includeRectoTail: Boolean
    ): AletaDrawResult {
        val edgeTop = if (usarBordeDerecho) PointF(topRightX, topRightY) else PointF(topLeftX, topLeftY)
        val edgeBottom = if (usarBordeDerecho) PointF(rightX, bottomY) else PointF(leftX, bottomY)
        val projected = projectAletaFromEdge(
            edgeTop = edgeTop,
            edgeBottom = edgeBottom,
            anchorSide = if (usarBordeDerecho) AletaSide.RIGHT else AletaSide.LEFT,
            direction = esquineroAletaDirection,
            anguloDeg = anguloDeg,
            aletaAnchoCm = aletaAnchoCm
        )
        return drawProjectedAletaArc(
            canvas = canvas,
            projected = projected,
            actualizarCotas = actualizarCotas,
            includeRectoTail = includeRectoTail
        )
    }

    private fun drawAletaFromEdge(
        canvas: Canvas,
        edgeTop: PointF,
        edgeBottom: PointF,
        anchorSide: AletaSide,
        direction: AletaDirection,
        anguloDeg: Float,
        aletaAnchoCm: Float,
        actualizarCotas: Boolean
    ): AletaDrawResult {
        val projected = projectAletaFromEdge(
            edgeTop = edgeTop,
            edgeBottom = edgeBottom,
            anchorSide = anchorSide,
            direction = direction,
            anguloDeg = anguloDeg,
            aletaAnchoCm = aletaAnchoCm
        )
        return drawProjectedAletaRecta(
            canvas = canvas,
            projected = projected,
            actualizarCotas = actualizarCotas
        )
    }

    private fun projectAletaFromEdge(
        edgeTop: PointF,
        edgeBottom: PointF,
        anchorSide: AletaSide,
        direction: AletaDirection,
        anguloDeg: Float,
        aletaAnchoCm: Float
    ): AletaProjected {
        val aletaHaciaInterior = direction == AletaDirection.INTERIOR
        val altoMuroCm = 240f
        val altoMuroPx = (floorTopY - ceilingBottomY).coerceAtLeast(1f)
        val pxPorCm = altoMuroPx / altoMuroCm
        val openingWidthPx = kotlin.math.abs(rightX - leftX).coerceAtLeast(1f)
        val topCenterY = (topLeftY + topRightY) * 0.5f
        val xVP = if (aletaHaciaInterior) {
            (leftX + rightX) * 0.5f
        } else {
            val fueraOffset = openingWidthPx * 1.15f
            if (anchorSide == AletaSide.RIGHT) edgeTop.x + fueraOffset else edgeTop.x - fueraOffset
        }
        val yVP = (topCenterY + bottomY) * 0.5f

        val profundidadBaseCm = aletaAnchoCm.coerceAtLeast(1f)
        val anguloAperturaDeg = anguloDeg.coerceIn(15f, 165f)
        val factorApertura = ((anguloAperturaDeg - 15f) / (165f - 15f))
            .coerceIn(0.25f, 1f)
        val profundidadCm = profundidadBaseCm * factorApertura
        val zPx = profundidadCm * pxPorCm
        val anchoVanoCm = (openingWidthPx / scalePxPerCm).coerceAtLeast(1f)
        val distanciaVistaCm = if (aletaHaciaInterior) {
            (anchoVanoCm * 0.5f).coerceAtLeast(30f)
        } else {
            (anchoVanoCm * 0.9f).coerceAtLeast(45f)
        }
        val distanciaFocalPx = (distanciaVistaCm * pxPorCm).coerceIn(220f, 1400f)

        val pTop2 = proyectarZ(edgeTop.x, edgeTop.y, zPx, xVP, yVP, distanciaFocalPx, haciaObservador = aletaHaciaInterior)
        val pBot2 = proyectarZ(edgeBottom.x, edgeBottom.y, zPx, xVP, yVP, distanciaFocalPx, haciaObservador = aletaHaciaInterior)

        return AletaProjected(
            p1x = edgeBottom.x,
            p1y = edgeBottom.y,
            p2x = edgeTop.x,
            p2y = edgeTop.y,
            p3x = pTop2.x,
            p3y = pTop2.y,
            p4x = pBot2.x,
            p4y = pBot2.y,
            anchorSide = anchorSide,
            direction = direction
        )
    }

    private fun drawProjectedAletaRecta(
        canvas: Canvas,
        projected: AletaProjected,
        actualizarCotas: Boolean
    ): AletaDrawResult {
        val p1x = projected.p1x
        val p1y = projected.p1y
        val p2x = projected.p2x
        val p2y = projected.p2y
        val p3x = projected.p3x
        val p3y = projected.p3y
        val p4x = projected.p4x
        val p4y = projected.p4y
        val sidePath = Path().apply {
            moveTo(p2x, p2y)
            lineTo(p3x, p3y)
            lineTo(p4x, p4y)
            lineTo(p1x, p1y)
            close()
        }
        if (actualizarCotas) {
            aletaP2x = p2x
            aletaP2y = p2y
            aletaP3x = p3x
            aletaP3y = p3y
            aletaP4x = p4x
            aletaP4y = p4y
            aletaCenterX = (p1x + p2x + p3x + p4x) * 0.25f
            aletaCenterY = (p1y + p2y + p3y + p4y) * 0.25f
        }
        val geometry = AletaGeometry(
            p2x = p2x,
            p2y = p2y,
            p3x = p3x,
            p3y = p3y,
            p4x = p4x,
            p4y = p4y,
            centerX = (p1x + p2x + p3x + p4x) * 0.25f,
            centerY = (p1y + p2y + p3y + p4y) * 0.25f
        )
        canvas.drawPath(sidePath, secondaryFaceFillPaint)
        canvas.drawPath(sidePath, secondaryFaceStrokePaint)
        canvas.drawLine(p2x, p2y, p3x, p3y, secondaryFaceStrokePaint)
        canvas.drawLine(p1x, p1y, p4x, p4y, secondaryFaceStrokePaint)
        return AletaDrawResult(
            geometry = geometry,
            outerTop = PointF(p3x, p3y),
            outerBottom = PointF(p4x, p4y)
        )
    }

    private fun drawProjectedAletaArc(
        canvas: Canvas,
        projected: AletaProjected,
        actualizarCotas: Boolean,
        includeRectoTail: Boolean
    ): AletaDrawResult {
        val p1x = projected.p1x
        val p1y = projected.p1y
        val p2x = projected.p2x
        val p2y = projected.p2y
        val p3x = projected.p3x
        val p3y = projected.p3y
        val p4x = projected.p4x
        val p4y = projected.p4y

        val seamT = if (includeRectoTail) 0.58f else 1f
        val sTopX = p2x + ((p3x - p2x) * seamT)
        val sTopY = p2y + ((p3y - p2y) * seamT)
        val sBotX = p1x + ((p4x - p1x) * seamT)
        val sBotY = p1y + ((p4y - p1y) * seamT)

        fun controlPoint(ax: Float, ay: Float, bx: Float, by: Float): PointF {
            val dx = bx - ax
            val dy = by - ay
            val len = kotlin.math.hypot(dx.toDouble(), dy.toDouble()).toFloat().coerceAtLeast(0.001f)
            val nx = -dy / len
            val ny = dx / len
            val sideSign = if (projected.anchorSide == AletaSide.RIGHT) 1f else -1f
            val dirSign = if (projected.direction == AletaDirection.INTERIOR) 1f else -1f
            val offset = len * 0.34f * sideSign * dirSign
            return PointF(((ax + bx) * 0.5f) + (nx * offset), ((ay + by) * 0.5f) + (ny * offset))
        }

        val cTop = controlPoint(p2x, p2y, sTopX, sTopY)
        val cBot = controlPoint(p1x, p1y, sBotX, sBotY)

        val facePath = Path().apply {
            moveTo(p2x, p2y)
            quadTo(cTop.x, cTop.y, sTopX, sTopY)
            lineTo(p3x, p3y)
            lineTo(p4x, p4y)
            lineTo(sBotX, sBotY)
            quadTo(cBot.x, cBot.y, p1x, p1y)
            close()
        }

        canvas.drawPath(facePath, secondaryFaceFillPaint)
        canvas.drawPath(facePath, secondaryFaceStrokePaint)
        canvas.drawLine(p3x, p3y, p4x, p4y, secondaryFaceStrokePaint)
        if (includeRectoTail) {
            canvas.drawLine(sTopX, sTopY, sBotX, sBotY, secondaryFaceStrokePaint)
        }

        val cX = (p1x + p2x + p3x + p4x) * 0.25f
        val cY = (p1y + p2y + p3y + p4y) * 0.25f
        if (actualizarCotas) {
            aletaP2x = p2x
            aletaP2y = p2y
            aletaP3x = p3x
            aletaP3y = p3y
            aletaP4x = p4x
            aletaP4y = p4y
            aletaCenterX = cX
            aletaCenterY = cY
        }
        val geometry = AletaGeometry(
            p2x = p2x,
            p2y = p2y,
            p3x = p3x,
            p3y = p3y,
            p4x = p4x,
            p4y = p4y,
            centerX = cX,
            centerY = cY
        )
        return AletaDrawResult(
            geometry = geometry,
            outerTop = PointF(p3x, p3y),
            outerBottom = PointF(p4x, p4y)
        )
    }

    private fun proyectarZ(
        x: Float,
        y: Float,
        zPx: Float,
        xVP: Float,
        yVP: Float,
        focalPx: Float = 300f,
        haciaObservador: Boolean = false
    ): PointF {
        val distancia = focalPx.coerceAtLeast(80f)
        val k = zPx / (zPx + distancia)
        val direccion = if (haciaObservador) -1f else 1f
        val x2 = x + (xVP - x) * k * direccion
        val y2 = y + (yVP - y) * k * direccion
        return PointF(x2, y2)
    }

    private fun calcularAnguloEsquinero(lado1Cm: Float, lado2Cm: Float, hipotenusaCm: Float): Float {
        val usarSuplementario = lado1Cm < 0f || lado2Cm < 0f || hipotenusaCm < 0f
        val a = abs(lado1Cm)
        val b = abs(lado2Cm)
        val c = abs(hipotenusaCm)
        val num = (a * a) + (b * b) - (c * c)
        val den = (2f * a * b).coerceAtLeast(0.0001f)
        val cosTheta = (num / den).coerceIn(-1f, 1f)
        val anguloBase = Math.toDegrees(acos(cosTheta).toDouble()).toFloat()
        return if (usarSuplementario) 180f - anguloBase else anguloBase
    }

    data class EsquineroConfig(
        val lado1Cm: Float,
        val lado2Cm: Float,
        val hipotenusaCm: Float,
        val anguloDeg: Float,
        val aletaAnchoCm: Float,
        val aletaAltoCm: Float,
        val aletaDirection: AletaDirection,
        val aletaSide: AletaSide,
        val variant: EsquineroVariant
    )

    private fun drawAcoples(canvas: Canvas) {
        if (encounterType != EncounterType.ACOPLADO || acopleSides.isEmpty()) return

        when (acopleProductType) {
            ProductType.VENTANA -> {
                acoplePaint.color = Color.argb(150, 66, 165, 245)
                acopleStrokePaint.color = Color.rgb(33, 120, 190)
            }
            ProductType.PUERTA -> {
                acoplePaint.color = Color.argb(150, 255, 179, 71)
                acopleStrokePaint.color = Color.rgb(230, 126, 34)
            }
            ProductType.MAMPARA -> {
                acoplePaint.color = Color.argb(150, 174, 213, 129)
                acopleStrokePaint.color = Color.rgb(104, 159, 56)
            }
            ProductType.NONE -> {
                acoplePaint.color = Color.argb(150, 255, 179, 71)
                acopleStrokePaint.color = Color.rgb(230, 126, 34)
            }
        }

        val minX = minOf(leftX, topLeftX)
        val maxX = maxOf(rightX, topRightX)
        val minY = minOf(topLeftY, topRightY)
        val maxY = bottomY
        val openingHeight = (maxY - minY).coerceAtLeast(1f)
        val acopleWidthPx = acopleBaseWidthCm * scalePxPerCm
        val acopleHeightPx = acopleBaseHeightCm * scalePxPerCm
        val acopleSillPx = defaultSillForProduct(acopleProductType) * scalePxPerCm
        val acopleBottomY = floorTopY - acopleSillPx
        val acopleTopY = (acopleBottomY - acopleHeightPx).coerceAtLeast(ceilingBottomY)
        val gap = 3f

        acopleSides.forEach { side ->
            val rect = when (side) {
                Side.LEFT -> {
                    RectF(minX - acopleWidthPx - gap, acopleTopY, minX - gap, acopleBottomY)
                }

                Side.RIGHT -> {
                    RectF(maxX + gap, acopleTopY, maxX + acopleWidthPx + gap, acopleBottomY)
                }

                Side.TOP -> {
                    val w = acopleWidthPx
                    val h = maxOf(openingHeight * 0.42f, acopleHeightPx * 0.45f)
                    val bottom = (minY - gap).coerceAtMost(floorTopY)
                    val top = (bottom - h).coerceAtLeast(ceilingBottomY)
                    RectF((minX + maxX - w) / 2f, top, (minX + maxX + w) / 2f, bottom)
                }

                Side.BOTTOM -> {
                    val w = acopleWidthPx
                    val h = maxOf(openingHeight * 0.42f, acopleHeightPx * 0.45f)
                    val top = (maxY + gap).coerceAtLeast(ceilingBottomY)
                    val bottom = (top + h).coerceAtMost(floorTopY)
                    RectF((minX + maxX - w) / 2f, top, (minX + maxX + w) / 2f, bottom)
                }
            }
            canvas.drawRect(rect, acoplePaint)
            canvas.drawRect(rect, acopleStrokePaint)
        }
    }

    private fun defaultSillForProduct(type: ProductType): Float {
        return when (type) {
            ProductType.VENTANA -> 90f
            ProductType.PUERTA, ProductType.MAMPARA, ProductType.NONE -> 0f
        }
    }

    private fun drawContextByProduct(canvas: Canvas) {
        val w = width.toFloat().coerceAtLeast(1f)
        val h = height.toFloat().coerceAtLeast(1f)
        val top = ceilingBottomY.coerceAtLeast(0f)
        val bottom = floorTopY.coerceAtMost(h).coerceAtLeast(top + 1f)

        canvas.drawRect(0f, 0f, w, top, ceilingPaint)
        canvas.drawRect(0f, bottom, w, h, floorPaint)
        canvas.drawRect(0f, top, w, bottom, wallPaint)
        canvas.drawRect(0f, top, w, bottom, wallStrokePaint)

        val openingPath = buildOpeningPath()
        val clearPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            xfermode = android.graphics.PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        canvas.drawPath(openingPath, clearPaint)
    }

    private fun drawCachedContext(canvas: Canvas) {
        if (contextBitmap == null || contextBitmap?.width != width || contextBitmap?.height != height) {
            contextBitmap = Bitmap.createBitmap(width.coerceAtLeast(1), height.coerceAtLeast(1), Bitmap.Config.ARGB_8888)
            contextCanvas = Canvas(contextBitmap!!)
            contextDirty = true
        }
        if (contextDirty) {
            contextCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            contextCanvas?.let { drawContextByProduct(it) }
            contextDirty = false
        }
        contextBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
    }

}



