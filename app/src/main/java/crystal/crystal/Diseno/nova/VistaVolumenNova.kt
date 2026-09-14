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

    /** Desde dónde se mira. Se puede girar arrastrando el dedo. */
    var giroGrados: Float = VolumenDelDiseno.GIRO_POR_DEFECTO
        set(value) {
            field = ((value % 360f) + 360f) % 360f
            invalidate()
        }

    private val pVidrio = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#CFE3F5"); style = Paint.Style.FILL
    }
    private val pVidrioFondo = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Las paredes que miran al otro lado van un punto más apagadas: es lo que deja ver de un
        // vistazo cuáles dan la cara y cuáles se van al fondo.
        color = Color.parseColor("#AFC6DC"); style = Paint.Style.FILL
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
        val planta = d?.let { PlantaDelDiseno.de(it, panza) }
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

    /** Cuántas de sus caras salieron curvas. 0 = la ventana se armó plana. */
    @androidx.annotation.VisibleForTesting
    fun carasCurvasParaPruebas(): Int = volumen?.caras?.count { it.esCurva } ?: 0

    // Girar la ventana arrastrando: es lo que salva al isométrico de su pega, que desde un solo
    // sitio siempre hay una pared que se ve de canto.
    private var xAnterior = 0f

    @android.annotation.SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: android.view.MotionEvent): Boolean {
        when (event.actionMasked) {
            android.view.MotionEvent.ACTION_DOWN -> {
                xAnterior = event.x
                return true
            }
            android.view.MotionEvent.ACTION_MOVE -> {
                giroGrados += (event.x - xAnterior) * 0.4f
                xAnterior = event.x
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
        val caras = v.deLejosACerca(giroGrados)
        val puntos = caras.flatMap { cara ->
            cara.esquinas.map { VolumenDelDiseno.proyectar(it, giroGrados) }
        }
        val minX = puntos.minOf { it.x }
        val maxX = puntos.maxOf { it.x }
        val minY = puntos.minOf { it.y }
        val maxY = puntos.maxOf { it.y }
        val margen = 28f
        val escala = minOf(
            (width - margen * 2) / (maxX - minX).coerceAtLeast(1f),
            (height - margen * 2) / (maxY - minY).coerceAtLeast(1f)
        ).coerceAtLeast(0.01f)
        val dx = margen - minX * escala + ((width - margen * 2) - (maxX - minX) * escala) / 2f
        val dy = margen - minY * escala + ((height - margen * 2) - (maxY - minY) * escala) / 2f

        fun aPapel(p: Punto3D): PuntoPlano {
            val q = VolumenDelDiseno.proyectar(p, giroGrados)
            return PuntoPlano(q.x * escala + dx, q.y * escala + dy)
        }

        // La huella en el suelo, punteada: dice de un vistazo cómo dobla la ventana.
        val suelo = Path()
        caras.sortedBy { it.indiceTramo }.forEachIndexed { i, cara ->
            val a = aPapel(cara.abajoIzq)
            if (i == 0) suelo.moveTo(a.x, a.y) else suelo.lineTo(a.x, a.y)
            val b = aPapel(cara.abajoDer)
            suelo.lineTo(b.x, b.y)
        }
        canvas.drawPath(suelo, pSuelo)

        // Y las caras, de la más lejana a la más cercana: lo de atrás se tapa solo.
        caras.forEach { cara -> dibujarCara(canvas, cara, d, ::aPapel) }
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
        aPapel: (Punto3D) -> PuntoPlano
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
        val daLaCara = der.x >= izq.x
        canvas.drawPath(cuadro(0f, 0f, 1f, 1f), if (daLaCara) pVidrio else pVidrioFondo)

        // Las franjas, de abajo arriba, y dentro de cada una sus módulos.
        val franjas = tramo?.franjas.orEmpty()
        val altoDeclarado = franjas.sumOf { it.alto.toDouble() }.toFloat()
        var v0 = 0f
        franjas.forEach { franja ->
            val parte = when {
                altoDeclarado > 0f && franja.alto > 0f -> franja.alto / altoDeclarado
                franjas.isNotEmpty() -> 1f / franjas.size
                else -> 1f
            }
            val v1 = (v0 + parte).coerceAtMost(1f)
            if (v1 > v0 + 0.001f) {
                // La raya entre franja y franja.
                if (v0 > 0.001f) {
                    val a = enLaCara(0f, v0)
                    val b = enLaCara(1f, v0)
                    canvas.drawLine(a.x, a.y, b.x, b.y, pDivision)
                }
                // Y los cortes de esta franja que caen en el trozo de pared de esta cara: entre
                // módulo y módulo una raya fina, y donde hay parante la raya gorda del marco.
                //
                // El corte que cae JUSTO en el borde entre dos facetas del arco cuenta para las
                // dos, y por eso el borde va incluido: descartándolo, el parante de una ventana
                // curva —que cae al medio, y el medio es borde de faceta— no lo dibujaba ninguna
                // de las dos y la ventana salía sin él, ni una raya. Dibujarlo dos veces no se
                // nota: es el mismo canto de la misma pared.
                cortesDeLaFranja(franja).forEach { (uTramo, esParante) ->
                    if (uTramo < cara.desdeU - 0.0001f || uTramo > cara.hastaU + 0.0001f) {
                        return@forEach
                    }
                    val u = (uTramo - cara.desdeU) / (cara.hastaU - cara.desdeU).coerceAtLeast(0.0001f)
                    val a = enLaCara(u, v0)
                    val b = enLaCara(u, v1)
                    canvas.drawLine(a.x, a.y, b.x, b.y, if (esParante) pMarco else pDivision)
                }
            }
            v0 = v1
        }

        // El contorno, encima de todo. En una pared curva NO se cierra cada trozo: el facetado es
        // cosa del dibujo, no de la ventana, y trazándolo entero la curva salía como una empalizada
        // de diez paños. Solo sus rieles, y los cantos donde la pared de verdad empieza y acaba.
        if (!cara.esCurva) {
            canvas.drawPath(cuadro(0f, 0f, 1f, 1f), pMarco)
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
