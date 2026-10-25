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
    /**
     * Un arco dentro de un contorno de puntos (2026-10-21): el vano arqueado de Medidas llega con su
     * arco hecho de trocitos, y así se dibujaba, con una cota en cada uno. Va del vértice [desde] al
     * [hasta] (dando la vuelta si hace falta), con sus tres medidas y el punto más salido.
     */
    data class ArcoDelContorno(
        val desde: Int, val hasta: Int,
        val cuerda: Float, val flecha: Float, val desarrollo: Float,
        val apice: Pair<Float, Float>
    ) {
        /** Los lados del contorno que son de este arco (el lado i va del vértice i al i + 1). */
        fun lados(n: Int): Set<Int> {
            val r = mutableSetOf<Int>()
            var i = desde
            while (i != hasta && r.size < n) { r.add(i); i = (i + 1) % n }
            return r
        }
    }

    /**
     * Los arcos de un contorno: cinco lados o más seguidos, casi del mismo largo, que doblan poco y
     * siempre hacia el mismo lado (así se trocea un arco). Una esquina o un lado largo los corta. Un
     * contorno que es todo curva (el círculo) no tiene arcos sueltos: tiene su propio modo.
     */
    fun arcosDelContorno(puntos: List<Pair<Float, Float>>): List<ArcoDelContorno> {
        val n = puntos.size
        if (n < 8) return emptyList()
        fun largo(i: Int): Float {
            val (ax, ay) = puntos[i]; val (bx, by) = puntos[(i + 1) % n]
            return kotlin.math.hypot(bx - ax, by - ay)
        }
        // Lo que dobla el contorno en el vértice i, en grados, con signo.
        fun giro(i: Int): Float {
            val (ax, ay) = puntos[(i - 1 + n) % n]; val (bx, by) = puntos[i]; val (cx, cy) = puntos[(i + 1) % n]
            var d = Math.toDegrees((kotlin.math.atan2(cy - by, cx - bx) - kotlin.math.atan2(by - ay, bx - ax)).toDouble()).toFloat()
            while (d > 180f) d -= 360f
            while (d < -180f) d += 360f
            return d
        }
        // ¿El lado i sigue en el i + 1 como un trozo más del mismo arco?
        fun sigue(i: Int, signo: Float): Boolean {
            val g = giro((i + 1) % n)
            val a = largo(i); val b = largo((i + 1) % n)
            return abs(g) in 0.2f..20f && g * signo > 0f && a > 0.01f && b / a in 0.6f..1.67f
        }
        fun signoEn(i: Int) = if (giro((i + 1) % n) >= 0f) 1f else -1f
        val inicio = (0 until n).firstOrNull { i -> !sigue((i - 1 + n) % n, signoEn((i - 1 + n) % n)) } ?: return emptyList()
        val arcos = mutableListOf<ArcoDelContorno>()
        var k = 0
        while (k < n) {
            val primero = (inicio + k) % n
            val signo = signoEn(primero)
            var ultimo = k
            while (ultimo + 1 < n && sigue((inicio + ultimo) % n, signo)) ultimo++
            val lados = ultimo - k + 1
            if (lados >= 5) {
                val desde = primero
                val hasta = (inicio + ultimo + 1) % n
                val vertices = (k..ultimo + 1).map { puntos[(inicio + it) % n] }
                val (ax, ay) = vertices.first(); val (bx, by) = vertices.last()
                val cuerda = kotlin.math.hypot(bx - ax, by - ay)
                if (cuerda > 0.5f) {
                    fun aLaCuerda(p: Pair<Float, Float>) = abs((p.first - ax) * (by - ay) - (p.second - ay) * (bx - ax)) / cuerda
                    val apice = vertices.maxByOrNull { aLaCuerda(it) }!!
                    val desarrollo = (k..ultimo).sumOf { largo((inicio + it) % n).toDouble() }.toFloat()
                    arcos.add(ArcoDelContorno(desde, hasta, cuerda, aLaCuerda(apice), desarrollo, apice))
                }
            }
            k = ultimo + 1
        }
        return arcos
    }

    fun aTexto(puntos: List<Pair<Float, Float>>): String =
        puntos.joinToString(";") { (x, y) -> "${NovaCalculos.df1(x)},${NovaCalculos.df1(y)}" }

    /**
     * El contorno como etiqueta del paquete: `V<x/y|x/y|…>` en centímetros.
     *
     * Separadores raros a propósito: el `;` parte franjas y la `,` parte medidas en los otros tres
     * parsers —VistaDiseno, NovaCorrediza y la pantalla de diseño—, así que un contorno escrito con
     * ellos les daría medidas equivocadas en silencio. Con `|` y `/` la etiqueta les pasa de largo.
     */
    fun aEtiqueta(puntos: List<Pair<Float, Float>>): String =
        "V<" + puntos.joinToString("|") { (x, y) ->
            "${NovaCalculos.df1(x)}/${NovaCalculos.df1(y)}"
        } + ">"

    /** Lee la etiqueta `V<…>` del paquete. Lista vacía si no se entiende. */
    fun desdeEtiqueta(etiqueta: String): List<Pair<Float, Float>> {
        val dentro = etiqueta.substringAfter('<', "").substringBefore('>', "")
        if (dentro.isBlank()) return emptyList()
        val puntos = dentro.split("|").mapNotNull { par ->
            val xy = par.split("/")
            val x = xy.getOrNull(0)?.trim()?.toFloatOrNull()
            val y = xy.getOrNull(1)?.trim()?.toFloatOrNull()
            if (x == null || y == null) null else x to y
        }
        return if (puntos.size >= 3) puntos else emptyList()
    }

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
            // Se mide un pelo hacia dentro —justo en el vértice se cruzan dos lados y la medida
            // sale doble— y desde esos dos puntos se ESTIRA la recta hasta los bordes. Sin estirar,
            // un lado inclinado perdía lo que baja en ese pelo: una ventana de 210 llegaba como
            // 209.2, y ese medio centímetro no es un redondeo, es una medida equivocada.
            val dentro = (ancho * 0.02f).coerceAtMost(0.5f)
            val a = bordesEn(puntos, x0 + dentro) ?: continue
            val b = bordesEn(puntos, x1 - dentro) ?: continue
            val izq = estirar(a, b, dentro, ancho, haciaAtras = true)
            val der = estirar(a, b, dentro, ancho, haciaAtras = false)
            val altoIzq = (izq.second - izq.first).coerceAtLeast(0f)
            val altoDer = (der.second - der.first).coerceAtLeast(0f)
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
     * El ancho del vano en una franja: el del borde donde el hueco es más ancho, dentro del trozo
     * [xIni]..[xFin] que ocupa su tramo.
     *
     * En un vano con forma, los módulos de una franja NO se reparten el ancho de la ventana: se
     * reparten el hueco que hay a su altura. En un triángulo invertido de 250 × 210, la franja de
     * abajo tiene 131 de hueco —no 250—, así que dos módulos miden 65 y medio, no 125.
     */
    fun anchoDelVanoEnFranja(
        puntos: List<Pair<Float, Float>>,
        yArriba: Float,
        yAbajo: Float,
        xIni: Float,
        xFin: Float
    ): Float {
        if (puntos.size < 3 || xFin <= xIni) return (xFin - xIni).coerceAtLeast(0f)
        val trozo = trozoDelVanoEnFranja(puntos, yArriba, yAbajo, xIni, xFin) ?: return 0f
        return (trozo.second - trozo.first).coerceAtLeast(0f)
    }

    /**
     * Desde dónde hasta dónde llega el vano en una franja, dentro del trozo [xIni]..[xFin] de su
     * tramo: es lo que se reparten sus módulos.
     *
     * - Con lados inclinados (un triángulo), el borde de la franja donde el hueco es más ancho.
     * - En un vano escalonado —todos sus lados rectos, con [DESCUADRE_ESCALON] de tolerancia—, lo
     *   que mide el hueco POR DENTRO de la franja. Sus bordes no sirven: un puente puesto a ras de
     *   un escalón cae en el escalón de al lado y medía ese. En Formas (338.3 de ancho, escalón a la
     *   izquierda y recorte abajo a la derecha), la franja del medio salía de 256.3 —cinco módulos
     *   de 51.2— cuando tiene los 338.3 enteros.
     *
     * Null si a esa altura no hay vano en el tramo.
     */
    fun trozoDelVanoEnFranja(
        puntos: List<Pair<Float, Float>>,
        yArriba: Float,
        yAbajo: Float,
        xIni: Float,
        xFin: Float
    ): Pair<Float, Float>? {
        if (puntos.size < 3 || xFin <= xIni) return null
        val alturas = if (esEscalonado(puntos) && yAbajo - yArriba > 2 * TOLERANCIA) {
            // Por dentro: la mitad de cada trozo entre los quiebres del contorno, sin llegar a
            // los bordes de la franja.
            val margen = min(DESCUADRE_ESCALON, (yAbajo - yArriba) / 4f)
            val desde = yArriba + margen
            val hasta = yAbajo - margen
            val cortes = (listOf(desde, hasta) + puntos.map { it.second }.filter { it > desde && it < hasta })
                .sorted()
            cortes.zipWithNext { a, b -> (a + b) / 2f }.ifEmpty { listOf((yArriba + yAbajo) / 2f) }
        } else {
            listOf(yArriba, yAbajo)
        }
        return alturas.mapNotNull { trozoEnAltura(puntos, it, xIni, xFin) }
            .maxByOrNull { it.second - it.first }
    }

    /** Dos centímetros y medio: hasta ahí un lado se lee recto, como el descuadre de la obra. */
    const val DESCUADRE_ESCALON = 2.5f

    /** Todos los lados horizontales o verticales, dentro del descuadre: un vano escalonado. */
    fun esEscalonado(puntos: List<Pair<Float, Float>>): Boolean =
        puntos.size >= 3 && puntos.indices.all { i ->
            val (ax, ay) = puntos[i]
            val (bx, by) = puntos[(i + 1) % puntos.size]
            abs(bx - ax) <= DESCUADRE_ESCALON || abs(by - ay) <= DESCUADRE_ESCALON
        }

    /** Lo que ocupa el hueco en esa horizontal, recortado al trozo del tramo. */
    private fun trozoEnAltura(
        puntos: List<Pair<Float, Float>>,
        y: Float,
        xIni: Float,
        xFin: Float
    ): Pair<Float, Float>? {
        var izq = Float.MAX_VALUE
        var der = -Float.MAX_VALUE
        var hubo = false
        for (i in puntos.indices) {
            val (ax, ay) = puntos[i]
            val (bx, by) = puntos[(i + 1) % puntos.size]
            if (y < min(ay, by) - TOLERANCIA || y > max(ay, by) + TOLERANCIA) continue
            if (abs(by - ay) <= TOLERANCIA) {
                // Lado horizontal justo a esa altura: cuenta entero.
                izq = min(izq, min(ax, bx)); der = max(der, max(ax, bx)); hubo = true
                continue
            }
            val t = ((y - ay) / (by - ay)).coerceIn(0f, 1f)
            val x = ax + t * (bx - ax)
            izq = min(izq, x); der = max(der, x)
            hubo = true
        }
        if (!hubo) return null
        val a = max(izq, xIni)
        val b = min(der, xFin)
        return if (b > a) a to b else null
    }

    /**
     * Estira la recta que pasa por los dos puntos medidos hasta el borde de la banda.
     *
     * [a] se midió a [dentro] del borde izquierdo y [b] a [dentro] del derecho, así que entre
     * ellos hay `ancho - 2*dentro`. Con lados rectos —que es lo que sabe leer esto— la recta da el
     * valor exacto del vértice; con lados verticales u horizontales no cambia nada.
     */
    private fun estirar(
        a: Pair<Float, Float>,
        b: Pair<Float, Float>,
        dentro: Float,
        ancho: Float,
        haciaAtras: Boolean
    ): Pair<Float, Float> {
        val luz = ancho - 2f * dentro
        if (luz <= TOLERANCIA || dentro <= 0f) return if (haciaAtras) a else b
        val k = dentro / luz
        return if (haciaAtras) {
            (a.first - (b.first - a.first) * k) to (a.second - (b.second - a.second) * k)
        } else {
            (b.first + (b.first - a.first) * k) to (b.second + (b.second - a.second) * k)
        }
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
     * Un escalón del vano ya listo para ser tramo: su ancho sin los parantes, su alto y lo que baja
     * su dintel. [entero] si va de dintel a alféizar como la ventana, que no anota ni alto ni caída.
     */
    data class Escalon(val ancho: Float, val alto: Float, val caida: Float, val entero: Boolean)

    /**
     * Los tramos de un vano escalonado, uno por banda, o null si alguna banda es inclinada de verdad
     * (triángulos, dinteles en pendiente: esas formas van por su propio camino, N-67).
     *
     * Es el único sitio donde una banda se vuelve tramo, para el diseño sugerido y para Limpiar:
     * - Un desnivel de hasta 2.5 es descuadre, no pendiente (N-06, N-66): el tramo queda recto y
     *   manda su medida mayor. El recorte de Formas baja 0.9 y sigue siendo un escalón.
     * - Cada escalón deja medio parante a cada lado del corte (N-27): así el eje del parante cae
     *   justo en el borde del escalón y los tramos más los parantes suman el vano.
     */
    fun escalones(medido: List<Pair<Float, Float>>, anchoParante: Float = 2.5f): List<Escalon>? {
        val puntos = sinDescuadreLateral(medido)
        val leidas = bandas(puntos)
        if (leidas.isEmpty()) return null
        fun inclinada(b: Banda) =
            abs(b.altoDerCm - b.altoCm) > DESCUADRE_ESCALON || abs(b.caidaDerCm - b.caidaCm) > DESCUADRE_ESCALON
        if (leidas.any { inclinada(it) }) return null
        val altoVano = puntos.maxOf { it.second } - puntos.minOf { it.second }
        // Dos bandas seguidas que solo se diferencian en un descuadre —hasta 2.5 en su alto y en lo que
        // baja su dintel— son el mismo tramo (N-06, N-101): el dintel de Ele baja 0.6 de punta a punta y
        // el costado de su columna partía el vano en un tramo de 35.1 y otro de 191.8 (2026-09-29).
        val juntas = mutableListOf<Banda>()
        for (b in leidas) {
            val u = juntas.lastOrNull()
            val altoU = u?.let { maxOf(it.altoCm, it.altoDerCm) } ?: 0f
            val caidaU = u?.let { minOf(it.caidaCm, it.caidaDerCm) } ?: 0f
            val altoB = maxOf(b.altoCm, b.altoDerCm)
            val caidaB = minOf(b.caidaCm, b.caidaDerCm)
            if (u != null && abs(altoU - altoB) <= DESCUADRE_ESCALON && abs(caidaU - caidaB) <= DESCUADRE_ESCALON) {
                val alto = maxOf(altoU, altoB)
                val caida = minOf(caidaU, caidaB)
                juntas[juntas.lastIndex] = Banda(u.anchoCm + b.anchoCm, alto, caida, alto, caida)
            } else {
                juntas.add(b)
            }
        }
        return juntas.mapIndexed { i, b ->
            val caida = minOf(b.caidaCm, b.caidaDerCm)
            val alto = maxOf(b.altoCm, b.altoDerCm)
            val lados = (if (i > 0) 1 else 0) + (if (i < juntas.lastIndex) 1 else 0)
            Escalon(
                ancho = (b.anchoCm - lados * anchoParante / 2f).coerceAtLeast(1f),
                alto = alto,
                caida = caida,
                entero = caida <= TOLERANCIA && abs(alto - altoVano) <= TOLERANCIA
            )
        }
    }

    /**
     * Un lado casi vertical cuyas puntas se apartan hasta 2.5 es descuadre (N-06), no un escalón: sus
     * x se juntan en una. Es lo mismo que ya se hacía con los desniveles del dintel y del alféizar
     * (N-101), del otro lado. Sin esto, un lado que se va 1 cm en 66 de alto armaba un tramo de 1 cm
     * (Formas, la medida 11: `Tl<1>` dos veces) y el 0.3 que se desvía el costado de la columna de
     * Ele, otro (hallado el 2026-09-29, con vidrios de 0.2 x 42.4).
     *
     * Los costados de la ventana se quedan con la medida mayor (N-06): la x más afuera. Un quiebre
     * de dentro, con la del medio. Solo se usa para repartir los tramos; la silueta que viaja con el
     * diseño es la medida, tal cual.
     */
    fun sinDescuadreLateral(puntos: List<Pair<Float, Float>>): List<Pair<Float, Float>> {
        if (puntos.size < 3) return puntos
        val xs = puntos.map { it.first }.distinct().sorted()
        val grupos = mutableListOf<MutableList<Float>>()
        for (x in xs) {
            val g = grupos.lastOrNull()
            if (g != null && x - g.first() <= DESCUADRE_ESCALON) g.add(x) else grupos.add(mutableListOf(x))
        }
        if (grupos.size == xs.size) return puntos
        val minX = xs.first()
        val maxX = xs.last()
        val a = HashMap<Float, Float>()
        grupos.forEach { g ->
            val v = when {
                g.first() == minX -> minX
                g.last() == maxX -> maxX
                else -> g.average().toFloat()
            }
            g.forEach { a[it] = v }
        }
        return puntos.map { (x, y) -> (a[x] ?: x) to y }
    }

    /**
     * Las franjas del sistema y de la mocheta de un tramo recto, con N-29: la hoja que escribió el
     * vidriero ([pedida]) o, sin ella, 5/7 del alto del tramo con el reparto único
     * ([HerramientasDiseno.repartirAlto], el mismo del panel, el dibujo, la 3D y el motor). Si la
     * hoja llega al alto no hay mocheta (N-30): una sola franja, nunca una mocheta de alto 0.
     */
    private fun franjasDeEscalon(
        acabado: String, ancho: Float, alto: Float, pedida: Float, anchoParante: Float
    ): List<NovaFranja>? {
        val hoja = if (pedida > 0f) {
            pedida
        } else {
            HerramientasDiseno.repartirAlto(listOf(0f, 0f), listOf(true, false), alto)[0]
        }
        val sinMocheta = hoja >= alto - TOLERANCIA
        val base = DisenoNova.nuevo(
            acabado = acabado,
            ancho = ancho,
            alto = alto,
            altoHoja = if (sinMocheta) alto else hoja,
            anchoParante = anchoParante
        )
        // `nuevo` reparte el ancho del tramo en sus módulos; aquí solo van sus franjas.
        return base.tramos.firstOrNull()?.franjas
    }

    /**
     * El diseño de arranque para ese vano: un tramo por banda, cada uno en su sitio y repartido
     * con las reglas de siempre.
     *
     * Un vano escalonado —todos sus lados rectos, con descuadre de hasta 2.5— sale de [escalones]:
     * cada escalón es un tramo (N-66) con su sistema y su mocheta por N-29.
     *
     * El alto de la ventana es el del vano entero, de lo más alto a lo más bajo. Cada tramo se
     * queda con lo suyo: lo que baja su dintel y lo que mide desde ahí. Los que van de punta a
     * punta no llevan ninguna de las dos, que es el caso normal.
     *
     * En los tramos INCLINADOS manda la regla del oficio: **la corrediza va en su rectángulo**. Una
     * hoja corre por el riel y no puede seguir la pendiente, así que se queda en el trozo donde
     * entra entera —el rectángulo que cabe bajo el lado que baja— y donde el vano se cierra, la
     * punta de un triángulo, van fijos. Los tramos rectos no se tocan: ahí la hoja es la de siempre.
     */
    fun disenoDesdeContorno(
        medido: List<Pair<Float, Float>>,
        acabado: String = "apa",
        altoHoja: Float = 0f,
        anchoParante: Float = 2.5f,
        /**
         * Las divisiones que escribió el vidriero en Nova; 0 = la regla de los 60. Mandan (N-16), con
         * hojas iguales, donde la forma da un solo grupo de hojas: un lado o el dintel inclinado
         * (Fide), el trapecio (las del rectángulo más el fijo de cada costado inclinado). En un vano de
         * varios tramos el reparto entre ellos se hace en novaDiseño (dictado el 2026-10-01) y aquí
         * se quedan las de la regla: quien llama lo nota porque el diseño no trae esa cantidad.
         */
        divisiones: Int = 0,
        /**
         * La mocheta de abajo de una bandera de dos bordes inclinados, como franja (con su puente),
         * escrita en Nova; 0 = lo que sube el alféizar. Se ajusta a lo que deja al sistema entero
         * dentro del rectángulo donde cabe (2026-10-02).
         */
        mochetaInferior: Float = 0f
    ): DisenoNova? {
        // Los tramos se reparten sin los descuadres de los costados; la silueta sigue siendo la medida.
        val puntos = sinDescuadreLateral(medido)
        val leidas = bandas(puntos)
        if (leidas.isEmpty()) return null
        // Las medidas de la ventana salen del VANO entero, no de sumar lo que mide cada banda: la
        // ventana mide lo que mide el hueco, de punta a punta.
        val alto = puntos.maxOf { it.second } - puntos.minOf { it.second }
        val ancho = puntos.maxOf { it.first } - puntos.minOf { it.first }

        escalones(puntos, anchoParante)?.let { escalones ->
            val tramos = escalones.map { e ->
                NovaTramo(
                    ancho = e.ancho,
                    franjas = franjasDeEscalon(acabado, e.ancho, e.alto, altoHoja, anchoParante) ?: return null,
                    alto = if (e.entero) 0f else e.alto,
                    caida = if (e.entero) 0f else e.caida
                )
            }
            return DisenoNova(acabado, ancho, alto, tramos).conContornoVano(medido)
        }
        formaEnUnTramo(puntos, medido, acabado, altoHoja, anchoParante, divisiones)?.let { return it }
        // Hasta el 2026-09-28 el escalonado también pasaba por aquí: la banda recta llevaba la hoja
        // al alto entero del tramo (`s<144.4>; m<0>`), un descuadre de 0.9 la volvía inclinada con
        // la hoja de la ventana entera (`s<103.2>; m<6.1>`) y los anchos no dejaban sitio a los
        // parantes. Ahora ese caso sale de [escalones]; lo de abajo queda para los lados inclinados.
        // Contra qué alto se mide si la hoja entra: el que pidió el vidriero o, con el campo vacío,
        // los cinco séptimos del alto con los que la calculadora trabaja cuando nadie lo llena.
        val hoja = if (altoHoja > 0f) min(altoHoja, alto) else alto * 5f / 7f
        val trozos = leidas.flatMap { trozosDe(it, altoHoja, hoja) }

        val franjasPorTrozo = arrayOfNulls<List<NovaFranja>>(trozos.size)
        // Los trozos inclinados seguidos que sí admiten la hoja son UN rectángulo partido por los
        // picos del vano: sus módulos se reparten juntos. Contándolos por separado, el rectángulo
        // central de un triángulo salía en dos mitades y ninguna daba para una hoja.
        // Las divisiones escritas solo si todo el vano es un grupo (ver [divisiones]).
        val unSoloGrupo = trozos.isNotEmpty() && trozos.all { it.banda.esInclinada && it.llevaHoja }
        var i = 0
        while (i < trozos.size) {
            if (!(trozos[i].banda.esInclinada && trozos[i].llevaHoja)) { i++; continue }
            var fin = i
            while (fin + 1 < trozos.size &&
                trozos[fin + 1].banda.esInclinada && trozos[fin + 1].llevaHoja
            ) fin++
            repartirGrupo(trozos, i, fin, if (unSoloGrupo) divisiones else 0, mochetaInferior)
                .forEachIndexed { k, fr -> franjasPorTrozo[i + k] = fr }
            i = fin + 1
        }

        val tramos = trozos.mapIndexed { idx, trozo ->
            val banda = trozo.banda
            val franjas = franjasPorTrozo[idx] ?: if (trozo.llevaHoja) {
                val base = DisenoNova.nuevo(
                    acabado = acabado,
                    ancho = banda.anchoCm,
                    alto = altoLleno(banda),
                    altoHoja = trozo.hoja,
                    anchoParante = anchoParante
                )
                // `nuevo` reparte el ancho de la banda en sus módulos; aquí solo van sus franjas.
                base.tramos.firstOrNull()?.franjas ?: return null
            } else {
                franjasDeFijos(banda.anchoCm, altoLleno(banda))
            }
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
        // La silueta viaja con el diseño, aparte del reparto: los tramos se pueden borrar y rehacer
        // —limpiar es justo eso— y la forma del hueco sigue siendo la que se midió.
        return DisenoNova(acabado, ancho, alto, tramos).conContornoVano(medido)
    }

    /**
     * Un vano con lados inclinados en UN tramo (pedido del usuario el 2026-10-19, con una forma de seis
     * lados que salía en cinco tramos, uno de 6.8: "es simpático, pero no factible; cuanto más simple
     * mejor"). Es la regla del trapecio (Vna4 de "polis", N-99) para cualquier forma:
     *
     * - **El sistema es el rectángulo más ancho donde la hoja entra entera (N-66, N-67)**, apoyado en el
     *   borde recto: sobre el alféizar (normal) o, si el alféizar no es recto, colgado del dintel
     *   (invertido). Hacia arriba no importa la forma: la mocheta acepta cualquiera (N-66) y el dibujo y
     *   el motor la recortan con la silueta, módulo por módulo (N-99).
     * - **Lo que queda a cada costado es un fijo** contra el lado inclinado, con el vidrio del rectángulo
     *   que lo contiene, cortado en obra (N-99); uno cada 180, como los paños de mocheta.
     * - El rectángulo se reparte con la regla de los 60 (N-15) y el orden es el preestablecido (N-22),
     *   dado vuelta si hace falta para que los costados queden en fijo.
     * - Un costado que queda fuera solo porque el borde de enfrente baja un poco —menos que el módulo de
     *   la casa, sobre el mismo borde recto— no lleva fijo: la hoja se acorta a lo que entra ahí, como en
     *   el dintel que roza el límite.
     *
     * Antes estas formas se partían en un tramo por cada vértice: parantes por todas partes, tramos de
     * nada y corredizas colgadas de dinteles a media pendiente. Antes también el trapecio tenía su propia
     * función, que pedía el rectángulo de dintel a alféizar; ahora es un caso de esta.
     *
     * Las formas de una sola banda (el dintel o el alféizar inclinados de Fide, los dos bordes en
     * bandera) siguen su camino dictado y las de sin ningún borde recto también: null.
     */
    private fun formaEnUnTramo(
        puntos: List<Pair<Float, Float>>, medido: List<Pair<Float, Float>>, acabado: String,
        pedida: Float, anchoParante: Float, divisiones: Int = 0
    ): DisenoNova? {
        if (bandas(puntos).size < 2) return null
        val minX = puntos.minOf { it.first }
        val maxX = puntos.maxOf { it.first }
        val minY = puntos.minOf { it.second }
        val maxY = puntos.maxOf { it.second }
        val ancho = maxX - minX
        val alto = maxY - minY
        if (ancho <= TOLERANCIA || alto <= TOLERANCIA) return null
        val deLaCasa = HerramientasDiseno.repartirAlto(listOf(0f, 0f), listOf(true, false), alto)[0]
        val primera = if (pedida > 0f) min(pedida, alto) else deLaCasa

        for (abajo in listOf(true, false)) {
            val apoyo = bordeRecto(puntos, abajo)
            if (apoyo.isEmpty()) continue
            var hoja = primera
            var rect = rectanguloDelSistema(puntos, apoyo, hoja, abajo) ?: continue
            // El costado que solo pierde la hoja por poco se la queda, acortada.
            val acortada = listOf(minX to rect.first, rect.second to maxX)
                .filter { (a, b) -> b - a > DESCUADRE_ESCALON && b - a < MODULO_CASA && dentroDe(apoyo, a, b) }
                .map { (a, b) -> luzEn(puntos, a, b, abajo) }
                .minOrNull()
            if (acortada != null && acortada >= MODULO_CASA && acortada < hoja) {
                rectanguloDelSistema(puntos, apoyo, acortada, abajo)?.let { hoja = acortada; rect = it }
            }
            if (rect.second - rect.first < MODULO_CASA / 2f) continue

            // Un costado de un pelo es descuadre: va con el rectángulo.
            var wIzq = rect.first - minX
            var wDer = maxX - rect.second
            var wRect = rect.second - rect.first
            if (wIzq <= DESCUADRE_ESCALON) { wRect += wIzq; wIzq = 0f }
            if (wDer <= DESCUADRE_ESCALON) { wRect += wDer; wDer = 0f }
            val izq = wIzq > 0f
            val der = wDer > 0f
            val laterales = (if (izq) 1 else 0) + (if (der) 1 else 0)
            val nRect = if (divisiones > laterales) divisiones - laterales else NovaCalculos.divisiones(wRect, 0)
            val total = nRect + laterales
            var orden = NovaCalculos.ordenDivis(total, total.toFloat()).filter { it == 'f' || it == 'c' }
            if (orden.length != total) return null
            if ((izq && orden.first() != 'f') || (der && orden.last() != 'f')) orden = orden.reversed()
            if ((izq && orden.first() != 'f') || (der && orden.last() != 'f')) return null
            val modulos = buildList {
                orden.forEachIndexed { k, tipo ->
                    val lateral = when {
                        izq && k == 0 -> wIzq
                        der && k == orden.lastIndex -> wDer
                        else -> 0f
                    }
                    if (lateral > 0f) {
                        val panos = NovaCalculos.anchMota(lateral)
                        repeat(panos) { add(NovaModulo('f', lateral / panos)) }
                    } else {
                        add(NovaModulo(tipo, wRect / nRect))
                    }
                }
            }
            val franjas = franjasDeEscalon(acabado, ancho, alto, hoja, anchoParante) ?: return null
            val conModulos = franjas.map { f -> if (!f.esSistema) f else f.copy(modulos = modulos) }
            // De abajo arriba: el invertido lleva la mocheta debajo.
            val enOrden = if (abajo) conModulos else conModulos.reversed()
            return DisenoNova(acabado, ancho, alto, listOf(NovaTramo(ancho = ancho, franjas = enOrden)))
                .conContornoVano(medido)
        }
        return null
    }

    /**
     * Los trozos de x donde el vano tiene su borde recto: el alféizar ([abajo]) o el dintel. Son los lados
     * casi horizontales —hasta el descuadre— que están a esa altura.
     */
    private fun bordeRecto(puntos: List<Pair<Float, Float>>, abajo: Boolean): List<Pair<Float, Float>> {
        val nivel = if (abajo) puntos.maxOf { it.second } else puntos.minOf { it.second }
        val trozos = puntos.indices.mapNotNull { i ->
            val (ax, ay) = puntos[i]
            val (bx, by) = puntos[(i + 1) % puntos.size]
            val aNivel = abs(ay - nivel) <= DESCUADRE_ESCALON && abs(by - nivel) <= DESCUADRE_ESCALON
            if (aNivel && abs(bx - ax) > TOLERANCIA) min(ax, bx) to max(ax, bx) else null
        }.sortedBy { it.first }
        val juntos = mutableListOf<Pair<Float, Float>>()
        for (t in trozos) {
            val u = juntos.lastOrNull()
            if (u != null && t.first <= u.second + TOLERANCIA) juntos[juntos.lastIndex] = u.first to max(u.second, t.second)
            else juntos.add(t)
        }
        return juntos
    }

    private fun dentroDe(trozos: List<Pair<Float, Float>>, a: Float, b: Float): Boolean =
        trozos.any { it.first <= a + TOLERANCIA && it.second >= b - TOLERANCIA }

    /**
     * El rectángulo del sistema: el trozo más ancho del borde recto donde el vano deja [hoja] de luz. Sus
     * extremos caen en un vértice o donde un lado cruza la altura de la hoja; entre dos de esos puntos
     * todo es recta, así que basta mirar el medio de cada trozo.
     */
    private fun rectanguloDelSistema(
        puntos: List<Pair<Float, Float>>, apoyo: List<Pair<Float, Float>>, hoja: Float, abajo: Boolean
    ): Pair<Float, Float>? {
        val minY = puntos.minOf { it.second }
        val maxY = puntos.maxOf { it.second }
        val nivel = if (abajo) maxY - hoja else minY + hoja
        val cortes = buildList {
            puntos.forEach { add(it.first) }
            apoyo.forEach { add(it.first); add(it.second) }
            for (i in puntos.indices) {
                val (ax, ay) = puntos[i]
                val (bx, by) = puntos[(i + 1) % puntos.size]
                if ((ay - nivel) * (by - nivel) < 0f) add(ax + (nivel - ay) / (by - ay) * (bx - ax))
            }
        }.distinctBy { Math.round(it * 100f) }.sorted()
        var mejor: Pair<Float, Float>? = null
        var actual: Pair<Float, Float>? = null
        for ((a, b) in cortes.zipWithNext()) {
            if (b - a <= 0.01f) continue
            val m = (a + b) / 2f
            val bordes = bordesEn(puntos, m)
            val cabe = bordes != null && dentroDe(apoyo, m, m) &&
                (if (abajo) bordes.first <= nivel + TOLERANCIA else bordes.second >= nivel - TOLERANCIA)
            actual = if (cabe) (actual?.first ?: a) to b else null
            if (actual != null && (mejor == null || actual.second - actual.first > mejor.second - mejor.first)) mejor = actual
        }
        return mejor
    }

    /** La luz más corta que deja el vano entre [a] y [b] desde su borde recto: lo que entra de hoja ahí. */
    private fun luzEn(puntos: List<Pair<Float, Float>>, a: Float, b: Float, abajo: Boolean): Float {
        val maxY = puntos.maxOf { it.second }
        val minY = puntos.minOf { it.second }
        val dentro = 0.01f
        val xs = (listOf(a + dentro, b - dentro) + puntos.map { it.first }.filter { it > a && it < b })
        return xs.mapNotNull { x -> bordesEn(puntos, x)?.let { if (abajo) maxY - it.first else it.second - minY } }
            .minOrNull() ?: 0f
    }

    /** Un trozo de vano ya decidido: si lleva hoja corrediza y con qué alto. */
    private data class Trozo(val banda: Banda, val llevaHoja: Boolean, val hoja: Float)

    /**
     * Hasta dónde se llena un tramo: su lado MÁS alto. El dibujo lo recorta con la silueta, así que
     * el fijo de encima sigue la forma en vez de dejar el pico del vano sin franja.
     */
    private fun altoLleno(banda: Banda): Float = maxOf(banda.altoCm, banda.altoDerCm)

    /**
     * Parte la banda por donde el vano baja de [hoja]: a un lado el trozo donde la corrediza entra
     * entera, al otro el que solo admite fijos.
     *
     * Solo se parte si lo que queda fuera llega al módulo de la casa —60 cm, el mismo con el que se
     * cuentan las divisiones—. Un dintel que roza el límite en el último palmo sigue siendo un tramo
     * solo, con la hoja acortada a lo que entra: partirlo por ahí metería un parante y un tramo de
     * nada donde el vidriero no lo pondría.
     *
     * Una banda recta pasa entera y con la hoja de siempre ([pedida] tal cual, que en 0 significa el
     * alto del tramo): ahí la hoja se acorta sola al alto y no hay nada que decidir.
     */
    private fun trozosDe(banda: Banda, pedida: Float, hoja: Float): List<Trozo> {
        if (!banda.esInclinada) {
            val suya = if (pedida > 0f) min(pedida, banda.altoCm) else banda.altoCm
            return listOf(Trozo(banda, true, suya))
        }
        val menor = minOf(banda.altoCm, banda.altoDerCm)
        val mayor = maxOf(banda.altoCm, banda.altoDerCm)
        if (menor >= hoja - TOLERANCIA) return listOf(Trozo(banda, true, hoja))
        // Ni en su lado más alto entra la hoja: el vano ahí es un triángulo de vidrio fijo.
        if (mayor < hoja - TOLERANCIA) return listOf(Trozo(banda, false, 0f))

        val recorrido = banda.altoDerCm - banda.altoCm
        if (abs(recorrido) <= TOLERANCIA) return listOf(Trozo(banda, true, menor))
        val t = ((hoja - banda.altoCm) / recorrido).coerceIn(0f, 1f)
        val anchoIzq = banda.anchoCm * t
        val anchoDer = banda.anchoCm - anchoIzq
        val cabeIzq = banda.altoCm >= hoja
        val fuera = if (cabeIzq) anchoDer else anchoIzq
        // La hoja se acorta a lo que entra solo mientras siga siendo una hoja: por debajo del módulo de
        // la casa van fijos (N-67). En el pentágono de Formas salía una corrediza con sistema de 10.2.
        if (fuera < MODULO_CASA) return listOf(if (menor >= MODULO_CASA) Trozo(banda, true, menor) else Trozo(banda, false, 0f))
        val caidaCorte = banda.caidaCm + (banda.caidaDerCm - banda.caidaCm) * t
        return listOf(
            Trozo(
                Banda(anchoIzq, banda.altoCm, banda.caidaCm, hoja, caidaCorte),
                cabeIzq, if (cabeIzq) hoja else 0f
            ),
            Trozo(
                Banda(anchoDer, hoja, caidaCorte, banda.altoDerCm, banda.caidaDerCm),
                !cabeIzq, if (cabeIzq) 0f else hoja
            )
        )
    }

    /**
     * Reparte los módulos del rectángulo que forman los trozos [desde]..[hasta] y devuelve las
     * franjas de cada uno.
     *
     * Las divisiones se cuentan sobre el ancho ENTERO del rectángulo, con las reglas de siempre, y
     * después cada trozo se queda con los suyos en el orden en que salieron: así la corrediza cae
     * donde tiene que caer aunque el pico del vano parta el rectángulo en dos.
     */
    private fun repartirGrupo(trozos: List<Trozo>, desde: Int, hasta: Int, escritas: Int = 0, mochetaInferior: Float = 0f): List<List<NovaFranja>> {
        val anchos = (desde..hasta).map { trozos[it].banda.anchoCm }
        val anchoGrupo = anchos.sum()
        val hoja = trozos[desde].hoja
        val divs = NovaCalculos.divisiones(anchoGrupo, escritas)
        val tipos = NovaCalculos.ordenDivis(divs, anchoGrupo)
            .filter { it == 'f' || it == 'c' }
            .ifEmpty { "f" }
        val cuotas = repartirModulos(anchos, tipos.length)
        var leidos = 0
        return anchos.mapIndexed { k, anchoTrozo ->
            val hasta2 = (leidos + cuotas[k]).coerceAtMost(tipos.length)
            val mios = (if (leidos < hasta2) tipos.substring(leidos, hasta2) else "").ifEmpty { "f" }
            leidos += cuotas[k]
            val banda = trozos[desde + k].banda
            val alturaLlena = altoLleno(banda)
            val franjas = mutableListOf(
                NovaFranja(
                    esSistema = true,
                    alto = min(hoja, alturaLlena),
                    modulos = mios.map { NovaModulo(it, anchoTrozo / mios.length) }
                )
            )
            val altoMocheta = (alturaLlena - hoja).coerceAtLeast(0f)
            if (altoMocheta > TOLERANCIA && alfeizarInclinado(banda) && dintelInclinado(banda)) {
                // Los dos bordes inclinados (2026-10-02, 88.4 × 174.2 con 114.4 a la derecha): no hay
                // borde recto donde apoyar el sistema, que es un rectángulo (N-66). Va en bandera: una
                // mocheta abajo con lo que sube el alféizar, el sistema sobre lo más alto del alféizar y
                // arriba la mocheta con el resto. La mocheta inferior escrita en Nova lo sube o lo baja
                // (2026-10-02), sin que el sistema salga del rectángulo: abajo, por lo menos lo que sube
                // el alféizar; arriba, por lo menos lo que baja el dintel.
                val subeAlfeizar = abs((banda.caidaDerCm + banda.altoDerCm) - (banda.caidaCm + banda.altoCm))
                val bajaDintel = abs(banda.caidaDerCm - banda.caidaCm)
                val maximo = maxOf(subeAlfeizar, altoMocheta - bajaDintel)
                val abajo = if (mochetaInferior > 0f) mochetaInferior.coerceIn(subeAlfeizar, maximo) else subeAlfeizar
                val arriba = (altoMocheta - abajo).coerceAtLeast(0f)
                if (abajo > TOLERANCIA) franjas.addAll(0, franjasDeFijos(anchoTrozo, abajo, sistema = false))
                if (arriba > TOLERANCIA) franjas.addAll(franjasDeFijos(anchoTrozo, arriba, sistema = false))
            } else if (altoMocheta > TOLERANCIA) {
                // El sistema es un rectángulo (N-66): se apoya en el borde recto. Con el alféizar
                // inclinado y el dintel recto cuelga del dintel y la mocheta va abajo, invertido
                // (2026-10-01, el vano de Fide al revés). Antes iba siempre abajo, sobre el alféizar
                // inclinado.
                val mocheta = franjasDeFijos(anchoTrozo, altoMocheta, sistema = false)
                if (alfeizarInclinado(banda) && !dintelInclinado(banda)) franjas.addAll(0, mocheta)
                else franjas.addAll(mocheta)
            }
            franjas
        }
    }

    /** ¿El borde de arriba de la banda (el dintel) baja de un lado al otro? */
    private fun dintelInclinado(b: Banda): Boolean = abs(b.caidaDerCm - b.caidaCm) > TOLERANCIA

    /** ¿El borde de abajo de la banda (el alféizar) sube de un lado al otro? */
    private fun alfeizarInclinado(b: Banda): Boolean =
        abs((b.caidaDerCm + b.altoDerCm) - (b.caidaCm + b.altoCm)) > TOLERANCIA

    /** Cuántos módulos se lleva cada trozo: por su ancho, y ninguno se queda sin uno. */
    private fun repartirModulos(anchos: List<Float>, total: Int): List<Int> {
        if (anchos.isEmpty()) return emptyList()
        val suma = anchos.sum().coerceAtLeast(0.01f)
        val cuotas = anchos.map { ((it / suma) * total).toInt().coerceAtLeast(1) }.toMutableList()
        var sobran = total - cuotas.sum()
        // Lo que quedó suelto por redondear va al trozo con los módulos más anchos, uno a uno.
        while (sobran > 0) {
            val mayor = anchos.indices.maxByOrNull { anchos[it] / cuotas[it] } ?: 0
            cuotas[mayor] = cuotas[mayor] + 1
            sobran--
        }
        return cuotas
    }

    /** Paños fijos para un trozo sin hoja, contados como los de la mocheta. */
    private fun franjasDeFijos(ancho: Float, alto: Float, sistema: Boolean = true): List<NovaFranja> {
        val panos = NovaCalculos.anchMota(ancho).coerceAtLeast(1)
        return listOf(
            NovaFranja(
                esSistema = sistema,
                alto = alto,
                modulos = List(panos) { NovaModulo('f', ancho / panos) }
            )
        )
    }

    /** El ancho con el que la casa cuenta un módulo: una división cada 60 cm. */
    private const val MODULO_CASA = 60f
}
