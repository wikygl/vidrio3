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
        // El vidrio de la franja llega hasta donde el hueco da de sí: su borde más ancho.
        val arriba = anchoEnAltura(puntos, yArriba, xIni, xFin)
        val abajo = anchoEnAltura(puntos, yAbajo, xIni, xFin)
        return maxOf(arriba, abajo)
    }

    /** Lo que mide el hueco en esa horizontal, recortado al trozo del tramo. */
    private fun anchoEnAltura(
        puntos: List<Pair<Float, Float>>,
        y: Float,
        xIni: Float,
        xFin: Float
    ): Float {
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
        if (!hubo) return 0f
        val a = max(izq, xIni)
        val b = min(der, xFin)
        return (b - a).coerceAtLeast(0f)
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
     * El diseño de arranque para ese vano: un tramo por banda, cada uno en su sitio y repartido
     * con las reglas de siempre.
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
        puntos: List<Pair<Float, Float>>,
        acabado: String = "apa",
        altoHoja: Float = 0f,
        anchoParante: Float = 2.5f
    ): DisenoNova? {
        val leidas = bandas(puntos)
        if (leidas.isEmpty()) return null
        // Las medidas de la ventana salen del VANO entero, no de sumar lo que mide cada banda: la
        // ventana mide lo que mide el hueco, de punta a punta.
        val alto = puntos.maxOf { it.second } - puntos.minOf { it.second }
        val ancho = puntos.maxOf { it.first } - puntos.minOf { it.first }
        // Contra qué alto se mide si la hoja entra: el que pidió el vidriero o, con el campo vacío,
        // los cinco séptimos del alto con los que la calculadora trabaja cuando nadie lo llena.
        val hoja = if (altoHoja > 0f) min(altoHoja, alto) else alto * 5f / 7f
        val trozos = leidas.flatMap { trozosDe(it, altoHoja, hoja) }

        val franjasPorTrozo = arrayOfNulls<List<NovaFranja>>(trozos.size)
        // Los trozos inclinados seguidos que sí admiten la hoja son UN rectángulo partido por los
        // picos del vano: sus módulos se reparten juntos. Contándolos por separado, el rectángulo
        // central de un triángulo salía en dos mitades y ninguna daba para una hoja.
        var i = 0
        while (i < trozos.size) {
            if (!(trozos[i].banda.esInclinada && trozos[i].llevaHoja)) { i++; continue }
            var fin = i
            while (fin + 1 < trozos.size &&
                trozos[fin + 1].banda.esInclinada && trozos[fin + 1].llevaHoja
            ) fin++
            repartirGrupo(trozos, i, fin).forEachIndexed { k, fr -> franjasPorTrozo[i + k] = fr }
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
        return DisenoNova(acabado, ancho, alto, tramos).conContornoVano(puntos)
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
        if (fuera < MODULO_CASA) return listOf(Trozo(banda, true, menor))
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
    private fun repartirGrupo(trozos: List<Trozo>, desde: Int, hasta: Int): List<List<NovaFranja>> {
        val anchos = (desde..hasta).map { trozos[it].banda.anchoCm }
        val anchoGrupo = anchos.sum()
        val hoja = trozos[desde].hoja
        val divs = NovaCalculos.divisiones(anchoGrupo, 0)
        val tipos = NovaCalculos.ordenDivis(divs, anchoGrupo)
            .filter { it == 'f' || it == 'c' }
            .ifEmpty { "f" }
        val cuotas = repartirModulos(anchos, tipos.length)
        var leidos = 0
        return anchos.mapIndexed { k, anchoTrozo ->
            val hasta2 = (leidos + cuotas[k]).coerceAtMost(tipos.length)
            val mios = (if (leidos < hasta2) tipos.substring(leidos, hasta2) else "").ifEmpty { "f" }
            leidos += cuotas[k]
            val alturaLlena = altoLleno(trozos[desde + k].banda)
            val franjas = mutableListOf(
                NovaFranja(
                    esSistema = true,
                    alto = min(hoja, alturaLlena),
                    modulos = mios.map { NovaModulo(it, anchoTrozo / mios.length) }
                )
            )
            val altoMocheta = (alturaLlena - hoja).coerceAtLeast(0f)
            if (altoMocheta > TOLERANCIA) {
                franjas.addAll(franjasDeFijos(anchoTrozo, altoMocheta, sistema = false))
            }
            franjas
        }
    }

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
