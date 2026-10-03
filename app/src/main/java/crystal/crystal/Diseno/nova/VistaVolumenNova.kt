package crystal.crystal.Diseno.nova

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * La ventana en tres dimensiones, levantada de su planta y puesta en isométrico.
 *
 * Es una vista de MIRAR: no se toca nada en ella, se edita en la alzada. Aquí solo se comprueba
 * cómo queda la ventana armada —los ángulos de verdad, las curvas girando— que en la alzada no se
 * puede ver porque allí la profundidad va fingida.
 *
 * Toda la geometría viene hecha de [PlantaDelDiseno] y [VolumenDelDiseno], que se comprueban en
 * frío; aquí solo se proyecta, se escala para que quepa y se pintan cuadriláteros.
 */
class VistaVolumenNova @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var diseno: DisenoNova? = null
    private var volumen: VolumenDelDiseno? = null

    /** La forma del vano entero en cm ([DisenoNova.siluetaDelVano]); vacía si es el rectángulo. */
    private var siluetaVano: List<Pair<Float, Float>> = emptyList()

    /** Dónde empieza cada tramo a lo largo de la ventana, en cm, contando los parantes. */
    private var inicioDeTramo: List<Float> = emptyList()

    /** Desde dónde se mira. Se puede girar arrastrando el dedo. */
    var giroGrados: Float = VolumenDelDiseno.GIRO_POR_DEFECTO
        set(value) {
            field = ((value % 360f) + 360f) % 360f
            invalidate()
        }

    private val pVidrio = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CFE3F5"); style = Paint.Style.FILL_AND_STROKE; strokeWidth = 1.5f
    }
    private val pVidrioFondo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Las paredes que miran al otro lado van un punto más apagadas: es lo que deja ver de un
        // vistazo cuáles dan la cara y cuáles se van al fondo.
        color = Color.parseColor("#AFC6DC"); style = Paint.Style.FILL_AND_STROKE; strokeWidth = 1.5f
    }
    private val pMarco = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK; style = Paint.Style.STROKE; strokeWidth = 4.5f
        strokeJoin = Paint.Join.ROUND
    }
    private val pDivision = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#22384F"); style = Paint.Style.STROKE; strokeWidth = 2.5f
    }
    private val pSuelo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#55000000"); style = Paint.Style.STROKE; strokeWidth = 2f
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(10f, 8f), 0f)
    }
    /** Lo que mide de ancho un parante, que es lo que se come del tramo. */
    private val PARANTE = 2.5f

    // Las piezas que se ven en la ventana, con su medida (2026-10-02), como en el dibujo plano.
    /** El alto del puente que se dibuja entre franja y franja. */
    private val PUENTE = 2.5f
    /** El alto de la franja oscura de abajo de la corrediza (su hache). */
    private val HACHE = 1.5f
    /** La línea de INA: el recuadro de la mocheta y el parante, que van detrás del vidrio (como el plano). */
    private val pLineaIna = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK; style = Paint.Style.STROKE; strokeWidth = 3f
    }
    private val pPieza = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#141414"); style = Paint.Style.FILL
    }
    /** Las tres rayas en diagonal del vidrio. */
    private val pRaya = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#33495E"); style = Paint.Style.STROKE; strokeWidth = 1.8f
        strokeCap = Paint.Cap.ROUND
    }

    private val pAviso = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#88000000"); textSize = 34f
    }

    /** Arma el volumen desde el paquete del diseño. Devuelve false si no se pudo leer. */
    fun mostrar(paquete: String?): Boolean {
        val d = runCatching { DisenoNova.desdePaquete(paquete.orEmpty()) }.getOrNull()
        diseno = d
        // La panza de toda la ventana se saca del paquete tal cual: la calculadora curva la mete
        // dentro de la franja de sistema, donde el modelo no la ve.
        val panza = PlantaDelDiseno.panzaDelPaquete(paquete)
        // La forma del vano entero —el círculo, la silueta medida, los escalones— recorta las
        // paredes, que para eso se levantan todas a lo alto de la ventana. Solo en la ventana que
        // no dobla: en la de esquina cada pared trae su propia silueta (`L<…>`).
        siluetaVano = d?.takeIf { it.tramos.none { t -> t.pliegue != null } }?.siluetaDelVano().orEmpty()
        inicioDeTramo = d?.tramos?.runningFold(0f) { x, t -> x + t.ancho + PARANTE }.orEmpty()
        val aLevantar = if (siluetaVano.size >= 3) d?.copy(tramos = d.tramos.map { it.copy(alto = 0f) }) else d
        val planta = aLevantar?.let { PlantaDelDiseno.de(it, panza) }
        plantaActual = planta
        volumen = planta?.let { VolumenDelDiseno.de(it) }
        planta?.let { giroGrados = giroQueLaPresentaBien(it) }
        invalidate()
        return volumen?.caras?.isNotEmpty() == true
    }

    /**
     * Desde dónde mirarla al abrirla, para que se presente de cara y no de canto.
     *
     * Una ventana curva que gira mucho, mirada desde un sitio fijo, sale casi de perfil y parece
     * una astilla. Se gira la vista lo que haga falta para que la línea que va de una punta a otra
     * de la ventana quede siempre igual de atravesada, y de ahí ya la mueve el dedo.
     */
    private fun giroQueLaPresentaBien(planta: PlantaDelDiseno): Float {
        val recorrido = planta.recorrido()
        if (recorrido.size < 2) return VolumenDelDiseno.GIRO_POR_DEFECTO
        val a = recorrido.first()
        val b = recorrido.last()
        val dx = (b.x - a.x).toDouble()
        val dy = (b.y - a.y).toDouble()
        if (kotlin.math.hypot(dx, dy) < 1.0) return VolumenDelDiseno.GIRO_POR_DEFECTO
        // La línea de punta a punta se lleva a -45° en la planta: ahí es donde el isométrico la
        // estira al máximo en el papel, y la ventana se presenta lo más ancha que puede.
        val rumbo = Math.toDegrees(kotlin.math.atan2(dy, dx)).toFloat()
        return -45f - rumbo
    }

    private var plantaActual: PlantaDelDiseno? = null

    /**
     * Mirarla como el dibujo plano (la imagen de Nova, 2026-10-02): el lado 1 de frente, un poco
     * girado para que se vea el siguiente. Por defecto **desde adentro**, que es como se mide y
     * como se trabaja (el resto de la ventana viene hacia quien mira); [desdeAfuera], si el cliente
     * la quiere ver desde la calle (la esquina hacia quien mira y el resto yéndose al fondo). Según
     * hacia dónde dobla la L, el lado 1 queda a la izquierda o a la derecha.
     */
    fun mirarDeFrente(desdeAfuera: Boolean = false) {
        val recorrido = plantaActual?.recorrido().orEmpty()
        if (recorrido.size < 3) return
        val a = recorrido[0]
        val b = recorrido[1]
        val rumbo = Math.toDegrees(kotlin.math.atan2((b.y - a.y).toDouble(), (b.x - a.x).toDouble())).toFloat()
        // −45 − rumbo pone el lado 1 de frente; girada un poco más deja ver el siguiente. Desde
        // adentro, más hacia la esquina (35°): de frente, el lado siguiente venía encima del ojo.
        val giroDeLado = if (desdeAfuera) 20f else 35f
        fun hondo(p: PuntoPlanta, g: Float) = VolumenDelDiseno.profundidad(Punto3D(p.x, p.y, 0f), g, elevacionGrados)
        fun enPapel(p: PuntoPlanta, g: Float) = VolumenDelDiseno.proyectar(Punto3D(p.x, p.y, 0f), g, elevacionGrados).x
        // En C (la última pared vuelve contraria a la primera; pedido del usuario, 2026-10-03): el lado
        // del medio de frente, sin girar, y los lados 1 y 3 a los costados. El lado del medio son las
        // paredes rectas que cruzan a la primera; si los lados 1 y 3 no miden igual, la línea de punta
        // a punta sale torcida y no sirve. Desde adentro las puntas vienen hacia quien mira; desde
        // afuera, se van al fondo.
        plantaActual?.takeIf { it.paredes.size >= 2 }?.let { p ->
            fun rumboDe(pared: ParedEnPlanta) = kotlin.math.atan2((pared.hasta.y - pared.desde.y).toDouble(), (pared.hasta.x - pared.desde.x).toDouble())
            val primero = rumboDe(p.paredes.first())
            val vuelve = kotlin.math.cos(rumboDe(p.paredes.last()) - primero) < -0.5
            if (!vuelve) return@let
            val medio = p.paredes.filter { !it.esCurva && kotlin.math.abs(kotlin.math.cos(rumboDe(it) - primero)) < 0.5 }
                .maxByOrNull { it.cuerdaCm } ?: return@let
            val rumboMedio = Math.toDegrees(rumboDe(medio)).toFloat()
            val ini = recorrido.first()
            val fin = recorrido.last()
            val centro = PuntoPlanta((medio.desde.x + medio.hasta.x) / 2f, (medio.desde.y + medio.hasta.y) / 2f)
            val puntas = PuntoPlanta((ini.x + fin.x) / 2f, (ini.y + fin.y) / 2f)
            val elegida = listOf(false, true).firstOrNull { dadaVuelta ->
                val g = -45f - rumboMedio + (if (dadaVuelta) 180f else 0f)
                (hondo(puntas, g) > hondo(centro, g)) == desdeAfuera
            } ?: return@let
            invertirCaras = elegida
            giroGrados = -45f - rumboMedio + (if (elegida) 180f else 0f)
            return
        }
        // Hacia qué lado girar depende de hacia dónde dobla: se prueban los cuatro (de un lado o del
        // otro de la ventana, girada a un lado o al otro) y se queda la que mira desde donde se pidió
        // —desde afuera el resto se va al fondo; desde adentro viene hacia quien mira— y deja la
        // ventana más abierta en el papel. Girada al lado equivocado, un lado tapaba al otro.
        data class Mirada(val giro: Float, val dadaVuelta: Boolean, val abierta: Float)
        val miradas = listOf(false, true).flatMap { dadaVuelta ->
            listOf(1f, -1f).map { sentido ->
                val g = -45f - rumbo + (if (dadaVuelta) 180f else 0f) + sentido * giroDeLado
                val abierta = recorrido.maxOf { enPapel(it, g) } - recorrido.minOf { enPapel(it, g) }
                Mirada(g, dadaVuelta, abierta.toFloat())
            }
        }.filter { m -> (hondo(recorrido.last(), m.giro) > hondo(b, m.giro)) == desdeAfuera }
        val elegida = miradas.maxByOrNull { it.abierta } ?: return
        invertirCaras = elegida.dadaVuelta
        giroGrados = elegida.giro
    }

    /**
     * Con el estilo del dibujo plano (la imagen de Nova, 2026-10-02): vidrio blanco, piezas y rayas
     * en negro, sin la huella del suelo; y las piezas más gruesas ([escalaPiezas]) para que en una
     * imagen chica se vean. La vista 3D de novaDiseño sigue con su azul.
     */
    var estiloDibujo: Boolean = false
        set(value) {
            field = value
            if (value) {
                pVidrio.color = Color.WHITE
                pVidrioFondo.color = Color.parseColor("#F2F2F2")
                pDivision.color = Color.BLACK; pDivision.strokeWidth = 2f
                pRaya.color = Color.BLACK; pRaya.strokeWidth = 2.2f
                pMarco.strokeWidth = 6f
            }
            invalidate()
        }

    /** El aire alrededor del dibujo, en px. */
    private var margen = 28f

    /**
     * Los trazos para una imagen de [anchoPx] de ancho que se va a ver de ese tamaño (la de Nova):
     * proporcionales a ella, para que ninguna línea quede por debajo de un punto y se pierda.
     * Pensados para 400 px: marco 3, divisiones 1.5, rayas del vidrio 1.2; nunca menos que eso.
     */
    fun trazosPara(anchoPx: Int) {
        val k = (anchoPx / 400f).coerceAtLeast(1f)
        pMarco.strokeWidth = 3f * k
        pDivision.strokeWidth = 1.5f * k
        pLineaIna.strokeWidth = 2.2f * k
        pRaya.strokeWidth = 1.2f * k
        pVidrio.strokeWidth = 1f * k
        pVidrioFondo.strokeWidth = 1f * k
        margen = 4f * k
        invalidate()
    }

    /** Cuánto más gruesos se dibujan el puente, el parante y la hache que su medida. */
    var escalaPiezas: Float = 1f
        set(value) { field = value; invalidate() }

    /** Lo que mide la ventana armada en su lado más largo (ancho, fondo o alto), en cm. */
    fun tamanoCm(): Float {
        val puntos = volumen?.caras?.flatMap { it.esquinas }.orEmpty()
        if (puntos.isEmpty()) return 0f
        return maxOf(
            puntos.maxOf { it.x } - puntos.minOf { it.x },
            puntos.maxOf { it.y } - puntos.minOf { it.y },
            puntos.maxOf { it.z } - puntos.minOf { it.z }
        )
    }

    /** Cuántas de sus caras salieron curvas. 0 = la ventana se armó plana. */
    @androidx.annotation.VisibleForTesting
    fun carasCurvasParaPruebas(): Int = volumen?.caras?.count { it.esCurva } ?: 0

    /** Cuántos puntos tiene la forma del vano con la que se recortan las paredes. 0 = rectángulo. */
    @androidx.annotation.VisibleForTesting
    fun puntosDeLaFormaParaPruebas(): Int = siluetaVano.size

    // Girar la ventana arrastrando: es lo que salva al isométrico de su pega, que desde un solo
    // sitio siempre hay una pared que se ve de canto. A lo ancho gira sobre el suelo; a lo alto
    // sube o baja la mirada (la elevación). Con dos dedos, en perspectiva, se acerca o aleja el ojo.
    private var xAnterior = 0f
    private var yAnterior = 0f
    private var separacionAnterior = 0f

    // Un toque sin arrastre sobre una pared la elige: quien nos usa decide qué hacer con ella
    // (en Medidas 3D, cambiarla de lado de la esquina). Se distingue del giro por el recorrido
    // del dedo, y con dos dedos nunca es toque.
    private var xInicio = 0f
    private var yInicio = 0f
    private var movido = false
    private var conDosDedos = false

    /** Avisa con el índice del tramo (pared) que se tocó sin arrastrar. */
    var alTocarPared: ((Int) -> Unit)? = null

    // Acercarse y apartarse: el dibujo se encuadra solo, y sobre ese encuadre el pellizco lo
    // agranda o achica alrededor del centro de la vista y los dos dedos lo desplazan. Doble toque
    // vuelve al encuadre. Es zoom del papel, no del ojo: el punto de vista no cambia.
    var zoom: Float = 1f
    var desplazamientoX: Float = 0f
    var desplazamientoY: Float = 0f
    private var xMedioAnterior = 0f
    private var yMedioAnterior = 0f
    private var ultimoToqueMs = 0L

    fun encuadrar() {
        zoom = 1f
        desplazamientoX = 0f
        desplazamientoY = 0f
        invalidate()
    }

    /** Cada cara tal como quedó en el papel en el último dibujo, de la más lejana a la más cercana. */
    private val carasEnPapel = mutableListOf<Pair<Int, List<PuntoPlano>>>()

    /** La pared bajo el dedo, la más cercana de las que lo tapan; null si tocó fuera. */
    private fun paredEn(x: Float, y: Float): Int? =
        carasEnPapel.asReversed().firstOrNull { (_, poligono) -> dentro(x, y, poligono) }?.first

    private fun dentro(x: Float, y: Float, poligono: List<PuntoPlano>): Boolean {
        var dentro = false
        var j = poligono.lastIndex
        for (i in poligono.indices) {
            val a = poligono[i]
            val b = poligono[j]
            if ((a.y > y) != (b.y > y) && x < (b.x - a.x) * (y - a.y) / (b.y - a.y) + a.x) dentro = !dentro
            j = i
        }
        return dentro
    }

    /** Cuánto se mira desde arriba: 30° es la isométrica de siempre; 0 de frente, 90 la planta. */
    var elevacionGrados: Float = VolumenDelDiseno.ELEVACION_POR_DEFECTO

    /**
     * Perspectiva de verdad (con punto de vista) en vez de la axonometría. Se mira desde
     * [distanciaCm] del centro de la ventana; más cerca, más se juntan las paralelas.
     */
    var perspectiva: Boolean = false
        set(value) { field = value; invalidate() }

    /**
     * Mirada dada vuelta respecto de la que se abre ([mirarDeFrente]): lo que da la cara es lo
     * contrario, así que el tono claro y el apagado se cambian de pared.
     */
    private var invertirCaras: Boolean = false
    var distanciaCm: Float = 600f

    private fun separacion(event: android.view.MotionEvent): Float {
        if (event.pointerCount < 2) return 0f
        val dx = event.getX(0) - event.getX(1)
        val dy = event.getY(0) - event.getY(1)
        return kotlin.math.hypot(dx, dy)
    }

    @android.annotation.SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        when (event.actionMasked) {
            android.view.MotionEvent.ACTION_DOWN -> {
                xAnterior = event.x
                yAnterior = event.y
                xInicio = event.x
                yInicio = event.y
                movido = false
                conDosDedos = false
                return true
            }
            android.view.MotionEvent.ACTION_POINTER_DOWN -> {
                separacionAnterior = separacion(event)
                if (event.pointerCount >= 2) {
                    xMedioAnterior = (event.getX(0) + event.getX(1)) / 2f
                    yMedioAnterior = (event.getY(0) + event.getY(1)) / 2f
                }
                conDosDedos = true
                return true
            }
            android.view.MotionEvent.ACTION_UP -> {
                if (!movido && !conDosDedos) {
                    val ahora = android.os.SystemClock.uptimeMillis()
                    if (ahora - ultimoToqueMs < 300L) {
                        // Doble toque: de vuelta al encuadre.
                        ultimoToqueMs = 0L
                        encuadrar()
                    } else {
                        ultimoToqueMs = ahora
                        paredEn(event.x, event.y)?.let { alTocarPared?.invoke(it) }
                    }
                }
                return true
            }
            android.view.MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount >= 2) {
                    // Pellizco: agranda o achica el dibujo; y los dos dedos juntos lo desplazan.
                    val ahora = separacion(event)
                    val xMedio = (event.getX(0) + event.getX(1)) / 2f
                    val yMedio = (event.getY(0) + event.getY(1)) / 2f
                    if (separacionAnterior > 1f && ahora > 1f) {
                        val factor = (ahora / separacionAnterior).coerceIn(0.5f, 2f)
                        val nuevo = (zoom * factor).coerceIn(0.3f, 8f)
                        // Se agranda alrededor del punto entre los dedos, que es lo que se está mirando.
                        val real = nuevo / zoom
                        desplazamientoX = (desplazamientoX + width / 2f - xMedio) * real - (width / 2f - xMedio)
                        desplazamientoY = (desplazamientoY + height / 2f - yMedio) * real - (height / 2f - yMedio)
                        zoom = nuevo
                    }
                    desplazamientoX += xMedio - xMedioAnterior
                    desplazamientoY += yMedio - yMedioAnterior
                    xMedioAnterior = xMedio
                    yMedioAnterior = yMedio
                    separacionAnterior = ahora
                    xAnterior = event.x
                    yAnterior = event.y
                    invalidate()
                    return true
                }
                if (!movido && kotlin.math.hypot(event.x - xInicio, event.y - yInicio) > 12f * resources.displayMetrics.density) movido = true
                if (!movido) return true
                giroGrados += (event.x - xAnterior) * 0.4f
                elevacionGrados = (elevacionGrados + (event.y - yAnterior) * 0.3f).coerceIn(0f, 89f)
                xAnterior = event.x
                yAnterior = event.y
                invalidate()
                return true
            }
            android.view.MotionEvent.ACTION_POINTER_UP -> {
                // Al soltar un dedo, el que queda sigue girando desde donde está.
                val queda = if (event.actionIndex == 0) 1 else 0
                xAnterior = event.getX(queda)
                yAnterior = event.getY(queda)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val v = volumen
        val d = diseno
        if (v == null || d == null || v.caras.isEmpty()) {
            canvas.drawText("Sin diseño que mostrar", 24f, 48f, pAviso)
            return
        }

        // Todo proyectado primero, para saber cuánto ocupa y meterlo en la pantalla.
        val caras = v.deLejosACerca(giroGrados, elevacionGrados)
        val centro = v.centro()
        val distancia = if (perspectiva) distanciaCm else 0f
        val puntos = caras.flatMap { cara ->
            cara.esquinas.map { VolumenDelDiseno.proyectar(it, giroGrados, elevacionGrados, distancia, centro) }
        }
        val minX = puntos.minOf { it.x }
        val maxX = puntos.maxOf { it.x }
        val minY = puntos.minOf { it.y }
        val maxY = puntos.maxOf { it.y }
        val escala = minOf(
            (width - margen * 2) / (maxX - minX).coerceAtLeast(1f),
            (height - margen * 2) / (maxY - minY).coerceAtLeast(1f)
        ).coerceAtLeast(0.01f)
        val dx = margen - minX * escala + ((width - margen * 2) - (maxX - minX) * escala) / 2f
        val dy = margen - minY * escala + ((height - margen * 2) - (maxY - minY) * escala) / 2f

        fun aPapel(p: Punto3D): PuntoPlano {
            val q = VolumenDelDiseno.proyectar(p, giroGrados, elevacionGrados, distancia, centro)
            // Encuadrado, y encima el zoom alrededor del centro de la vista más el desplazamiento.
            val x = q.x * escala + dx
            val y = q.y * escala + dy
            return PuntoPlano(
                width / 2f + (x - width / 2f) * zoom + desplazamientoX,
                height / 2f + (y - height / 2f) * zoom + desplazamientoY
            )
        }

        // La huella en el suelo, punteada: dice de un vistazo cómo dobla la ventana.
        val suelo = Path()
        caras.sortedBy { it.indiceTramo }.forEachIndexed { i, cara ->
            val a = aPapel(cara.abajoIzq)
            if (i == 0) suelo.moveTo(a.x, a.y) else suelo.lineTo(a.x, a.y)
            val b = aPapel(cara.abajoDer)
            suelo.lineTo(b.x, b.y)
        }
        if (!estiloDibujo) canvas.drawPath(suelo, pSuelo)

        // Y las caras, de la más lejana a la más cercana: lo de atrás se tapa solo. Se guarda
        // dónde quedó cada una para saber cuál se toca.
        carasEnPapel.clear()
        // Una pared curva son varias facetas de la misma superficie: primero el vidrio de todas y
        // después lo de encima. Pintadas de una en una, el vidrio de cada faceta tapaba el borde del
        // puente y del marco de la anterior y quedaban marcas claras (2026-10-02).
        val curvasConFondo = mutableSetOf<Int>()
        caras.forEach { cara ->
            carasEnPapel.add(cara.indiceTramo to cara.esquinas.map(::aPapel))
            if (!cara.esCurva) {
                dibujarCara(canvas, cara, d, ::aPapel)
                return@forEach
            }
            if (curvasConFondo.add(cara.indiceTramo)) {
                caras.filter { it.esCurva && it.indiceTramo == cara.indiceTramo }
                    .forEach { dibujarCara(canvas, it, d, ::aPapel, Parte.FONDO) }
            }
            dibujarCara(canvas, cara, d, ::aPapel, Parte.PIEZAS)
        }
    }

    /**
     * Dónde cae cada corte de la franja a lo largo del tramo, de 0 a 1, y si ese corte es parante.
     *
     * Se reparte por los ANCHOS que trae cada módulo, no a partes iguales: un tramo con hojas
     * desiguales —o partido por un parante, que se come sus 2.5— salía con las divisiones a ojo y
     * ninguna caía donde de verdad está. Si algún módulo viene sin ancho no hay con qué repartir y
     * se vuelve a las partes iguales, que es lo que había.
     */
    private fun cortesDeLaFranja(franja: NovaFranja): List<Pair<Float, Boolean>> {
        val n = franja.modulos.size
        if (n == 0) return emptyList()
        val anchos = franja.modulos.map { it.ancho ?: 0f }
        val porAncho = anchos.all { it > 0f }
        val total = if (porAncho) anchos.sum() + PARANTE * franja.parantes.size else n.toFloat()
        if (total <= 0f) return emptyList()
        val cortes = mutableListOf<Pair<Float, Boolean>>()
        var acumulado = 0f
        for (i in 0 until n) {
            acumulado += if (porAncho) anchos[i] else 1f
            val hayParante = i in franja.parantes
            when {
                // El parante tiene su grueso: la raya va por su mitad.
                hayParante && porAncho -> {
                    cortes.add((acumulado + PARANTE / 2f) / total to true)
                    acumulado += PARANTE
                }
                i < n - 1 -> cortes.add(acumulado / total to hayParante)
            }
        }
        return cortes
    }

    /** Lo que mide la franja a lo ancho con sus parantes, en cm; null si algún módulo no trae ancho. */
    private fun anchoDeLaFranja(franja: NovaFranja): Float? {
        val anchos = franja.modulos.map { it.ancho ?: 0f }
        if (anchos.isEmpty() || anchos.any { it <= 0f }) return null
        return anchos.sum() + PARANTE * franja.parantes.size
    }

    /**
     * Inaparente, como el dibujo plano (2026-10-02): los fijos de piso a techo, un solo paño; las
     * corredizas en el alto del sistema, con su hache abajo; y la mocheta con su puente solo sobre cada
     * racha de corredizas, sin cruzar parantes. Entre dos corredizas la raya va solo en el alto del
     * sistema; entre un fijo y lo demás, de piso a techo. Los parantes, con su ancho, como en APA.
     */
    private fun dibujarInaEnCara(
        canvas: Canvas,
        cara: CaraDelVolumen,
        franjas: List<NovaFranja>,
        iSistema: Int,
        alturas: List<Float>,
        vPiso: Float,
        vTecho: Float,
        alto: Float,
        enLaCara: (Float, Float) -> PuntoPlano,
        cuadro: (Float, Float, Float, Float) -> Path
    ) {
        val anchoCara = (cara.hastaU - cara.desdeU).coerceAtLeast(0.0001f)
        fun uCara(uTramo: Float) = (uTramo - cara.desdeU) / anchoCara
        fun cae(u0: Float, u1: Float) = u1 >= cara.desdeU - 0.0001f && u0 <= cara.hastaU + 0.0001f
        fun dv(cm: Float) = cm / alto
        // Dónde va cada franja, de abajo arriba, con el mismo reparto que en APA.
        val total = alturas.sum()
        val rangos = mutableListOf<Pair<Float, Float>>()
        var va = vPiso
        franjas.forEachIndexed { k, _ ->
            val parte = (if (total > 0f) alturas.getOrElse(k) { 0f } / total else 1f / franjas.size) * (vTecho - vPiso)
            val vb = (va + parte).coerceAtMost(vTecho)
            rangos.add(va to vb)
            va = vb
        }
        val (vS0, vS1) = rangos[iSistema]
        val sistema = franjas[iSistema]
        val totalCm = anchoDeLaFranja(sistema) ?: 1f
        fun du(cm: Float) = cm / totalCm / anchoCara
        val banda = dv(PUENTE * escalaPiezas)
        val sentido = if (enLaCara(1f, 0f).x >= enLaCara(0f, 0f).x) 1f else -1f

        /** Las tres rayas del vidrio en el medio de un paño (u del tramo, v de la cara). */
        fun rayas(u0: Float, u1: Float, vA: Float, vB: Float) {
            val centro = (u0 + u1) / 2f
            if (centro < cara.desdeU - 0.0001f || centro > cara.hastaU + 0.0001f || vB <= vA) return
            val largo = (minOf((u1 - u0) * totalCm, (vB - vA) * alto) * 0.22f).coerceIn(5f, 16f)
            val separa = largo * 0.32f
            val uc = uCara(centro)
            val vc = (vA + vB) / 2f
            for (i in -1..1) {
                val corto = if (i == 0) 1f else 0.7f
                val a = enLaCara(uc + sentido * du(i * separa - largo * corto / 2f), vc - dv(largo * corto / 2f))
                val b = enLaCara(uc + sentido * du(i * separa + largo * corto / 2f), vc + dv(largo * corto / 2f))
                canvas.drawLine(a.x, a.y, b.x, b.y, pRaya)
            }
        }

        val panos = panosDeLaFranja(sistema)
        // Cada paño del sistema: el fijo entero, la corrediza en el alto del sistema con su hache.
        panos.forEach { p ->
            if (p.corrediza) {
                if (cae(p.u0, p.u1)) canvas.drawPath(cuadro(uCara(p.u0), vS0, uCara(p.u1), (vS0 + dv(HACHE * escalaPiezas)).coerceAtMost(vS1)), pPieza)
                rayas(p.u0, p.u1, vS0, vS1)
            } else {
                rayas(p.u0, p.u1, vPiso, vTecho)
            }
        }
        // Como el plano de INA (dictado el 2026-10-02): la estructura no se ve. Ni el puente ni los
        // parantes van como pieza: sobre cada racha de corredizas solo el recuadro de la mocheta, una
        // línea en el canto que la separa del sistema, sin rayas de vidrio; los parantes, una línea.
        // (Un primer intento dibujó el puente entero, gris detrás de los fijos: el plano no lo muestra.)
        var i = 0
        while (i < panos.size) {
            if (!panos[i].corrediza) { i++; continue }
            var j = i
            while (j + 1 < panos.size && panos[j + 1].corrediza && j !in sistema.parantes) j++
            val u0 = panos[i].u0
            val u1 = panos[j].u1
            if (cae(u0, u1)) franjas.indices.filter { it != iSistema }.forEach { k ->
                val (a, b) = rangos[k]
                if (b <= a + 0.001f) return@forEach
                val canto = if (k > iSistema) a else b
                val p = enLaCara(uCara(u0), canto)
                val q = enLaCara(uCara(u1), canto)
                canvas.drawLine(p.x, p.y, q.x, q.y, pLineaIna)
            }
            i = j + 1
        }
        // Entre paño y paño una línea: entre dos corredizas en el alto del sistema; si no, de piso a
        // techo (también el parante, que en INA va detrás del vidrio).
        for (k in 1 until panos.size) {
            val izq = panos[k - 1]
            val der = panos[k]
            val medio = (izq.u1 + der.u0) / 2f
            if (!cae(medio, medio)) continue
            val u = uCara(medio)
            val (vA, vB) = if (izq.corrediza && der.corrediza && k - 1 !in sistema.parantes) vS0 to vS1 else vPiso to vTecho
            val a = enLaCara(u, vA)
            val b = enLaCara(u, vB)
            canvas.drawLine(a.x, a.y, b.x, b.y, if (k - 1 in sistema.parantes) pLineaIna else pDivision)
        }
    }

    /** Qué parte de una cara se pinta: su vidrio, lo que va encima, o todo de una vez. */
    enum class Parte { FONDO, PIEZAS, TODO }

    /** Un paño de la franja: dónde empieza y acaba a lo largo del tramo (de 0 a 1) y si corre. */
    private data class Pano(val u0: Float, val u1: Float, val corrediza: Boolean)

    /** Los paños de la franja, con el mismo reparto que [cortesDeLaFranja]. */
    private fun panosDeLaFranja(franja: NovaFranja): List<Pano> {
        val n = franja.modulos.size
        if (n == 0) return emptyList()
        val total = anchoDeLaFranja(franja)
        val out = mutableListOf<Pano>()
        var x = 0f
        franja.modulos.forEachIndexed { i, m ->
            val w = if (total != null) (m.ancho ?: 0f) / total else 1f / n
            out.add(Pano(x, x + w, !m.esFijo))
            x += w
            if (total != null && i in franja.parantes) x += PARANTE / total
        }
        return out
    }

    /**
     * Una cara con lo que lleva dentro: sus franjas de abajo arriba y los módulos de cada una.
     *
     * Los módulos se reparten sobre el trozo de tramo que ocupa esta cara ([CaraDelVolumen.desdeU]
     * a [hastaU]), así que una pared curva —que son varias caras— sale con sus divisiones
     * siguiendo el arco, sin repartir nada aparte.
     */
    private fun dibujarCara(
        canvas: Canvas,
        cara: CaraDelVolumen,
        diseno: DisenoNova,
        aPapel: (Punto3D) -> PuntoPlano,
        /** Qué se pinta: el vidrio (fondo), lo de encima (piezas) o todo. */
        parte: Parte = Parte.TODO
    ) {
        val tramo = diseno.tramos.getOrNull(cara.indiceTramo)
        val alto = (cara.arribaIzq.z - cara.abajoIzq.z).coerceAtLeast(1f)

        /** Un punto de la cara: [u] de izquierda a derecha, [v] de abajo arriba, los dos de 0 a 1. */
        fun enLaCara(u: Float, v: Float): PuntoPlano {
            val x = cara.abajoIzq.x + (cara.abajoDer.x - cara.abajoIzq.x) * u
            val y = cara.abajoIzq.y + (cara.abajoDer.y - cara.abajoIzq.y) * u
            return aPapel(Punto3D(x, y, cara.abajoIzq.z + alto * v))
        }

        fun cuadro(u0: Float, v0: Float, u1: Float, v1: Float): Path {
            val a = enLaCara(u0, v0)
            val b = enLaCara(u1, v0)
            val c = enLaCara(u1, v1)
            val e = enLaCara(u0, v1)
            return Path().apply {
                moveTo(a.x, a.y); lineTo(b.x, b.y); lineTo(c.x, c.y); lineTo(e.x, e.y); close()
            }
        }

        // La pared entera, rellena. La que nos enseña su cara va clara y la que nos da la espalda
        // más apagada, para leer de un vistazo cuál es cuál sin contar esquinas.
        //
        // Se mira por dónde cae su canto derecho respecto del izquierdo YA EN EL PAPEL: si va hacia
        // la derecha, la pared nos mira. Comparando lo lejos que cae cada canto, los trozos de una
        // curva cambiaban de tono a media pared y la curva salía a dos colores.
        val izq = enLaCara(0f, 0f)
        val der = enLaCara(1f, 0f)
        val daLaCara = (der.x >= izq.x) != invertirCaras

        // La pared con su silueta medida —el dintel en diagonal, el corte— en vez del rectángulo:
        // su contorno viene en cm desde su esquina de arriba a la izquierda, y aquí se pasa a la
        // cara (u de izquierda a derecha, v de abajo arriba). Solo cuando la pared es una sola
        // cara: partida en tramos, la silueta abarca más que esta cara y se deja el rectángulo.
        val silueta = tramo?.contorno?.takeIf { it.size >= 3 && !cara.esCurva && cara.desdeU < 0.001f && cara.hastaU > 0.999f }
        val contornoDeLaCara = silueta?.let { puntos ->
            val anchoSilueta = puntos.maxOf { it.first }.coerceAtLeast(0.01f)
            val altoSilueta = puntos.maxOf { it.second }.coerceAtLeast(0.01f)
            Path().apply {
                puntos.forEachIndexed { i, (xCm, yCm) ->
                    val p = enLaCara(xCm / anchoSilueta, 1f - yCm / altoSilueta)
                    if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
        }
        // La forma del vano entero puesta sobre esta cara. Se pasa a la cara con su sitio a lo largo
        // de la ventana, así que lo que cae fuera de esta pared queda fuera de su cuadro y se
        // recorta: cada cara se lleva solo su trozo de círculo o de escalón.
        val vanoEnLaCara = if (contornoDeLaCara == null && siluetaVano.size >= 3 && tramo != null) {
            val inicio = inicioDeTramo.getOrElse(cara.indiceTramo) { 0f }
            val x0Cara = inicio + cara.desdeU * tramo.ancho
            val anchoCara = ((cara.hastaU - cara.desdeU) * tramo.ancho).coerceAtLeast(0.01f)
            val desarrollo = (inicioDeTramo.lastOrNull() ?: 0f) - PARANTE
            val kx = desarrollo / siluetaVano.maxOf { it.first }.coerceAtLeast(0.01f)
            val altoVano = maxOf(diseno.alto, siluetaVano.maxOf { it.second }).coerceAtLeast(0.01f)
            Path().apply {
                siluetaVano.forEachIndexed { i, (xCm, yCm) ->
                    val p = enLaCara((xCm * kx - x0Cara) / anchoCara, 1f - yCm / altoVano)
                    if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
                }
                close()
            }
        } else null
        val formaDeLaPared = contornoDeLaCara
            ?: vanoEnLaCara?.let { Path().apply { op(cuadro(0f, 0f, 1f, 1f), it, Path.Op.INTERSECT) } }
            ?: cuadro(0f, 0f, 1f, 1f)
        if (parte != Parte.PIEZAS) canvas.drawPath(formaDeLaPared, if (daLaCara) pVidrio else pVidrioFondo)
        if (parte == Parte.FONDO) return
        // Lo de dentro —franjas y cortes— se queda dentro de la silueta.
        canvas.save()
        // Siempre: en una curva cada faceta pinta solo lo suyo, y una pieza que pasa a la de al lado
        // (la franja de la corrediza, las rayas del vidrio) no se sale de su pared.
        canvas.clipPath(formaDeLaPared)

        // Con la forma del vano las paredes suben el alto entero de la ventana, y las franjas de
        // cada tramo van en su trozo: de su dintel (lo que baja, `caida`) a su alféizar.
        val (vPiso, vTecho) = if (vanoEnLaCara != null && tramo != null && diseno.alto > 0f) {
            // El rectángulo del tramo lo da el modelo: en uno inclinado, de su dintel más alto a su
            // alféizar más bajo (antes, el lado izquierdo; 2026-09-28).
            val techo = diseno.caidaDeTramo(cara.indiceTramo)
            val abajo = techo + diseno.altoDeTramo(cara.indiceTramo)
            (1f - abajo / diseno.alto).coerceIn(0f, 1f) to (1f - techo / diseno.alto).coerceIn(0f, 1f)
        } else 0f to 1f

        // Las franjas, de abajo arriba, y dentro de cada una sus módulos.
        // Con el mismo reparto que el dibujo y el panel (N-29, N-34): antes una mocheta sin alto
        // junto a un sistema escrito no tenía sitio y desaparecía (pedido del usuario, 2026-09-28).
        var franjas = tramo?.franjas.orEmpty()
        val alturas = HerramientasDiseno.alturasDeFranjas(diseno, cara.indiceTramo)
        val altoRepartido = alturas.sum()
        // Las piezas van con su medida de verdad, en cm sobre la pared, como en el dibujo plano
        // (2026-10-02): así se achican con la distancia y siguen la curva. [alto] es lo que mide la
        // pared; el ancho, el de cada franja con sus parantes.
        val anchoCara = (cara.hastaU - cara.desdeU).coerceAtLeast(0.0001f)
        fun uCara(uTramo: Float) = (uTramo - cara.desdeU) / anchoCara
        fun enLaCaraSiCae(uTramo: Float) = uTramo >= cara.desdeU - 0.0001f && uTramo <= cara.hastaU + 0.0001f
        /** Cuánto mide en v (de 0 a 1) una medida vertical en cm. */
        fun dv(cm: Float) = cm / alto
        // En inaparente el dibujo es otro, como en el plano (2026-10-02: al pasar de APA a INA la imagen
        // no cambiaba): los fijos van de piso a techo, y la mocheta con su puente solo sobre cada racha
        // de corredizas.
        val iSistema = franjas.indexOfFirst { it.esSistema }
        if (diseno.acabado.equals("ina", ignoreCase = true) && iSistema >= 0 && franjas.size > 1) {
            dibujarInaEnCara(canvas, cara, franjas, iSistema, alturas, vPiso, vTecho, alto, ::enLaCara, ::cuadro)
            franjas = emptyList()   // lo de APA, abajo, ya no tiene nada que dibujar
        }
        val bordes = mutableListOf<Pair<Float, Int>>()   // dónde termina cada franja (v) y cuál es
        var v0 = vPiso
        franjas.forEachIndexed { k, franja ->
            val parte = (if (altoRepartido > 0f) alturas.getOrElse(k) { 0f } / altoRepartido else 1f / franjas.size) *
                (vTecho - vPiso)
            val v1 = (v0 + parte).coerceAtMost(vTecho)
            if (v1 > v0 + 0.001f) {
                val totalCm = anchoDeLaFranja(franja) ?: tramo?.ancho ?: 1f
                fun du(cm: Float) = cm / totalCm / anchoCara
                // Cada paño de la franja: la corrediza con su franja oscura abajo (la hache), y el
                // vidrio con sus tres rayas en diagonal, en el medio del paño.
                // Lo que se ve de la franja, sin el puente que le cae encima o debajo (va del lado de
                // la mocheta, ver abajo): las rayas del vidrio van en el medio de eso, no tapadas.
                val banda = dv(PUENTE * escalaPiezas)
                fun puenteHaciaAbajo(i: Int) = franjas.getOrNull(i)?.let { aba -> franjas.getOrNull(i + 1)?.let { arr -> !aba.esSistema && arr.esSistema } } == true
                val vA = if (k > 0 && !puenteHaciaAbajo(k - 1)) (v0 + banda).coerceAtMost(v1) else v0
                val vB = if (k < franjas.lastIndex && puenteHaciaAbajo(k)) (v1 - banda).coerceAtLeast(vA) else v1
                panosDeLaFranja(franja).forEach { p ->
                    if (franja.esSistema && p.corrediza && (p.u1 >= cara.desdeU && p.u0 <= cara.hastaU)) {
                        canvas.drawPath(cuadro(uCara(p.u0), v0, uCara(p.u1), (v0 + dv(HACHE * escalaPiezas)).coerceAtMost(v1)), pPieza)
                    }
                    val centro = (p.u0 + p.u1) / 2f
                    if (!enLaCaraSiCae(centro)) return@forEach
                    val anchoPanoCm = (p.u1 - p.u0) * totalCm
                    val altoPanoCm = (vB - vA) * alto
                    val largo = (minOf(anchoPanoCm, altoPanoCm) * 0.22f).coerceIn(5f, 16f)
                    val separa = largo * 0.32f
                    val uc = uCara(centro)
                    val vc = (vA + vB) / 2f
                    // Siempre inclinadas igual en el papel (/), aunque la pared se dibuje de derecha a
                    // izquierda (mirada desde el otro lado).
                    val sentido = if (enLaCara(1f, 0f).x >= enLaCara(0f, 0f).x) 1f else -1f
                    for (i in -1..1) {
                        val corto = if (i == 0) 1f else 0.7f
                        val a = enLaCara(uc + sentido * du(i * separa - largo * corto / 2f), vc - dv(largo * corto / 2f))
                        val b = enLaCara(uc + sentido * du(i * separa + largo * corto / 2f), vc + dv(largo * corto / 2f))
                        canvas.drawLine(a.x, a.y, b.x, b.y, pRaya)
                    }
                }
                // Los cortes de la franja que caen en esta cara: entre paño y paño una raya fina, y
                // el parante con su ancho, como una pieza.
                //
                // El corte que cae JUSTO en el borde entre dos facetas del arco cuenta para las
                // dos, y por eso el borde va incluido: descartándolo, el parante de una ventana
                // curva —que cae al medio, y el medio es borde de faceta— no lo dibujaba ninguna
                // de las dos y la ventana salía sin él, ni una raya. Dibujarlo dos veces no se
                // nota: es el mismo canto de la misma pared.
                cortesDeLaFranja(franja).forEach { (uTramo, esParante) ->
                    if (!enLaCaraSiCae(uTramo)) return@forEach
                    val u = uCara(uTramo)
                    if (esParante) {
                        canvas.drawPath(cuadro(u - du(PARANTE * escalaPiezas / 2f), v0, u + du(PARANTE * escalaPiezas / 2f), v1), pPieza)
                    } else {
                        val a = enLaCara(u, v0)
                        val b = enLaCara(u, v1)
                        canvas.drawLine(a.x, a.y, b.x, b.y, pDivision)
                    }
                }
                bordes.add(v1 to k)
            }
            v0 = v1
        }
        // El puente entre franja y franja, con su alto, del lado de la mocheta (N-31): encima del
        // sistema en una normal, debajo en una invertida; entre dos mochetas, en la de arriba.
        bordes.dropLast(1).forEach { (v, k) ->
            val abajo = franjas.getOrNull(k)
            val arriba = franjas.getOrNull(k + 1)
            val haciaAbajo = abajo != null && arriba != null && !abajo.esSistema && arriba.esSistema
            val (va, vb) = if (haciaAbajo) (v - dv(PUENTE * escalaPiezas)) to v else v to (v + dv(PUENTE * escalaPiezas))
            canvas.drawPath(cuadro(0f, va, 1f, vb), pPieza)
        }
        // El parante entre una pared y la siguiente (la esquina, las puntas de la curva): cada pared
        // pone su mitad en su borde y en la unión queda el parante entero, con el mismo grosor que
        // los de dentro del tramo (2026-10-02: salía como la raya del marco y se confundía).
        val nTramos = diseno.tramos.size
        val anchoTramoCm = tramo?.ancho?.takeIf { it > 0f }
        // En INA no: el parante va detrás del vidrio y el plano no lo muestra; queda el canto de la pared.
        if (anchoTramoCm != null && !diseno.acabado.equals("ina", ignoreCase = true)) {
            val medio = PARANTE * escalaPiezas / 2f / anchoTramoCm / anchoCara
            val pParante = pPieza
            if (cara.desdeU < 0.001f && cara.indiceTramo > 0) canvas.drawPath(cuadro(0f, vPiso, medio, vTecho), pParante)
            if (cara.hastaU > 0.999f && cara.indiceTramo < nTramos - 1) canvas.drawPath(cuadro(1f - medio, vPiso, 1f, vTecho), pParante)
        }
        canvas.restore()

        // El contorno, encima de todo. En una pared curva NO se cierra cada trozo: el facetado es
        // cosa del dibujo, no de la ventana, y trazándolo entero la curva salía como una empalizada
        // de diez paños. Solo sus rieles, y los cantos donde la pared de verdad empieza y acaba.
        if (!cara.esCurva) {
            canvas.drawPath(formaDeLaPared, pMarco)
        } else if (vanoEnLaCara != null) {
            // Curva con forma: el borde del vano que cae en esta faceta, y los cantos donde la
            // pared empieza y acaba hasta donde llega la forma.
            canvas.save()
            canvas.clipPath(cuadro(0f, 0f, 1f, 1f))
            canvas.drawPath(vanoEnLaCara, pMarco)
            canvas.restore()
            canvas.save()
            canvas.clipPath(formaDeLaPared)
            if (cara.desdeU < 0.001f) enLaCara(0f, 0f).let { a -> enLaCara(0f, 1f).let { b -> canvas.drawLine(a.x, a.y, b.x, b.y, pMarco) } }
            if (cara.hastaU > 0.999f) enLaCara(1f, 0f).let { a -> enLaCara(1f, 1f).let { b -> canvas.drawLine(a.x, a.y, b.x, b.y, pMarco) } }
            canvas.restore()
        } else {
            val abajoI = enLaCara(0f, 0f)
            val abajoD = enLaCara(1f, 0f)
            val arribaI = enLaCara(0f, 1f)
            val arribaD = enLaCara(1f, 1f)
            canvas.drawLine(abajoI.x, abajoI.y, abajoD.x, abajoD.y, pMarco)
            canvas.drawLine(arribaI.x, arribaI.y, arribaD.x, arribaD.y, pMarco)
            if (cara.desdeU < 0.001f) canvas.drawLine(abajoI.x, abajoI.y, arribaI.x, arribaI.y, pMarco)
            if (cara.hastaU > 0.999f) canvas.drawLine(abajoD.x, abajoD.y, arribaD.x, arribaD.y, pMarco)
        }
    }
}
