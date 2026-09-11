package crystal.crystal.Diseno.nova

import crystal.crystal.taller.nova.NovaCalculos
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Convierte el contorno de un vano —el que se dibuja en MedidaActivity— en los tramos de una
 * ventana Nova.
 *
 * En obra los vanos rectos son la excepción: lo normal es que el alféizar suba en un trozo, que
 * quede una columna en medio o que el dintel baje sobre una puerta. Todo eso es la misma cosa
 * vista desde el diseño: **tramos de distinto alto colgando del mismo dintel**, que es justo lo
 * que [DisenoNova] sabe describir desde que el tramo tiene su propio alto.
 *
 * El contorno llega en centímetros y con la Y hacia abajo, como el apunte: el dintel es la Y más
 * pequeña. Solo se entienden contornos ortogonales —lados horizontales y verticales—, que son los
 * que salen del lápiz magnético; un contorno con lados inclinados devuelve un solo tramo con su
 * caja, que es lo que hacía la calculadora hasta ahora.
 */
object ContornoEnTramos {

    /**
     * El contorno tal como viaja con la medida: `x,y;x,y;…` en centímetros.
     *
     * Se escribe así de simple a propósito: la cola de medidas se guarda serializada y un texto
     * corto sobrevive a todo —a las versiones viejas del app, que lo ignoran, y a mirarlo a ojo
     * cuando algo no cuadra.
     */
    fun aTexto(puntos: List<Pair<Float, Float>>): String =
        puntos.joinToString(";") { (x, y) -> "${NovaCalculos.df1(x)},${NovaCalculos.df1(y)}" }

    /** Lee el contorno que viene con la medida. Devuelve la lista vacía si no se entiende. */
    fun desdeTexto(texto: String): List<Pair<Float, Float>> {
        if (texto.isBlank()) return emptyList()
        return texto.split(";").mapNotNull { par ->
            val xy = par.split(",")
            val x = xy.getOrNull(0)?.trim()?.toFloatOrNull()
            val y = xy.getOrNull(1)?.trim()?.toFloatOrNull()
            if (x == null || y == null) null else x to y
        }
    }

    /**
     * Un trozo de vano. Con los dos lados iguales es un rectángulo; con lados distintos, el
     * cuadrilátero que sale de un dintel o un alféizar inclinado.
     */
    data class Banda(
        val anchoCm: Float,
        val altoCm: Float,
        val caidaCm: Float = 0f,
        val altoDerCm: Float = altoCm,
        val caidaDerCm: Float = caidaCm
    ) {
        val esInclinada: Boolean
            get() = abs(altoDerCm - altoCm) > 0.15f || abs(caidaDerCm - caidaCm) > 0.15f
    }

    /** Milímetro y medio: por debajo de eso son la misma medida, no un escalón. */
    private const val TOLERANCIA = 0.15f

    /**
     * Las bandas verticales del contorno, de izquierda a derecha. Dos bandas seguidas iguales
     * —mismas medidas y ninguna inclinada— se juntan en una: un vano recto da una sola banda.
     *
     * Cada banda se mide en sus DOS bordes, no en el medio: así un lado inclinado se lee como lo
     * que es, un cuadrilátero, en vez de aplanarse a un rectángulo con la medida del centro.
     */
    fun bandas(puntos: List<Pair<Float, Float>>): List<Banda> {
        // Tres puntos ya son un vano: el triángulo es una forma de ventana como cualquier otra.
        if (puntos.size < 3) return emptyList()
        val xs = puntos.map { it.first }.distinctBy { Math.round(it / TOLERANCIA) }.sorted()
        if (xs.size < 2) return emptyList()

        val dintel = puntos.minOf { it.second }
        val bandas = mutableListOf<Banda>()
        for (i in 0 until xs.size - 1) {
            val x0 = xs[i]
            val x1 = xs[i + 1]
            val ancho = x1 - x0
            if (ancho <= TOLERANCIA) continue
            // Un pelo hacia dentro: justo en el vértice se cruzan dos lados y la medida sale doble.
            val dentro = (ancho * 0.02f).coerceAtMost(0.5f)
            val izq = bordesEn(puntos, x0 + dentro) ?: continue
            val der = bordesEn(puntos, x1 - dentro) ?: continue
            val altoIzq = izq.second - izq.first
            val altoDer = der.second - der.first
            if (altoIzq <= TOLERANCIA && altoDer <= TOLERANCIA) continue
            val banda = Banda(
                anchoCm = ancho,
                altoCm = altoIzq,
                caidaCm = (izq.first - dintel).coerceAtLeast(0f),
                altoDerCm = altoDer,
                caidaDerCm = (der.first - dintel).coerceAtLeast(0f)
            )
            val ultima = bandas.lastOrNull()
            val sigue = ultima != null && !ultima.esInclinada && !banda.esInclinada &&
                abs(ultima.altoCm - banda.altoCm) <= TOLERANCIA &&
                abs(ultima.caidaCm - banda.caidaCm) <= TOLERANCIA
            if (sigue) {
                bandas[bandas.lastIndex] = ultima!!.copy(anchoCm = ultima.anchoCm + ancho)
            } else {
                bandas.add(banda)
            }
        }
        return bandas
    }

    /**
     * Dónde empieza y dónde acaba el vano en esa vertical. Vale para escalones arriba, abajo o los
     * dos; un vano con un agujero en medio —dos trozos separados en la misma vertical— se leería
     * como uno solo, y eso todavía no existe.
     */
    private fun bordesEn(puntos: List<Pair<Float, Float>>, x: Float): Pair<Float, Float>? {
        var arriba = Float.MAX_VALUE
        var abajo = -Float.MAX_VALUE
        var hubo = false
        for (i in puntos.indices) {
            val (ax, ay) = puntos[i]
            val (bx, by) = puntos[(i + 1) % puntos.size]
            if (x < min(ax, bx) || x > max(ax, bx)) continue
            if (abs(bx - ax) <= TOLERANCIA) continue // lado vertical: no cruza, es el borde
            val t = (x - ax) / (bx - ax)
            val y = ay + t * (by - ay)
            arriba = min(arriba, y)
            abajo = max(abajo, y)
            hubo = true
        }
        return if (hubo) arriba to abajo else null
    }
    /**
     * El diseño de arranque para ese vano: un tramo por banda, cada uno en su sitio y repartido
     * con las reglas de siempre.
     *
     * El alto de la ventana es el del vano entero, de lo más alto a lo más bajo. Cada tramo se
     * queda con lo suyo: lo que baja su dintel y lo que mide desde ahí. Los que van de punta a
     * punta no llevan ninguna de las dos, que es el caso normal.
     */
    fun disenoDesdeContorno(
        puntos: List<Pair<Float, Float>>,
        acabado: String = "apa",
        altoHoja: Float = 0f,
        anchoParante: Float = 2.5f
    ): DisenoNova? {
        val bandas = bandas(puntos)
        if (bandas.isEmpty()) return null
        val alto = bandas.maxOf { maxOf(it.caidaCm + it.altoCm, it.caidaDerCm + it.altoDerCm) }
        val ancho = bandas.sumOf { it.anchoCm.toDouble() }.toFloat()
        val tramos = bandas.map { banda ->
            val base = DisenoNova.nuevo(
                acabado = acabado,
                ancho = banda.anchoCm,
                alto = banda.altoCm,
                altoHoja = if (altoHoja > 0f) min(altoHoja, banda.altoCm) else banda.altoCm,
                anchoParante = anchoParante
            )
            // `nuevo` reparte el ancho de la banda en sus módulos; aquí solo se toman sus franjas.
            val franjas = base.tramos.firstOrNull()?.franjas ?: return null
            val rectaYEntera = !banda.esInclinada &&
                banda.caidaCm <= TOLERANCIA &&
                abs(banda.altoCm - alto) <= TOLERANCIA
            NovaTramo(
                ancho = banda.anchoCm,
                franjas = franjas,
                // Un tramo que va de dintel a alféizar no anota nada: es el caso normal.
                alto = if (rectaYEntera) 0f else banda.altoCm,
                caida = if (rectaYEntera) 0f else banda.caidaCm,
                altoDer = if (banda.esInclinada) banda.altoDerCm else null,
                caidaDer = if (banda.esInclinada) banda.caidaDerCm else null
            )
        }
        return DisenoNova(acabado, ancho, alto, tramos)
    }
}
