package crystal.crystal.taller.melamina

/** Qué es cada cosa que hay dentro del ropero. */
enum class TipoElemento { CUERPO, CAJON, ENTREPANO, REPISA_MALETERO, TUBO, CASILLERO, TAPA_CAJONES, MALETERO, COLGADOR, ZONA_CAJONES, DIVISION_COLUMNA, COLUMNA }

/**
 * Un elemento del ropero puesto en su sitio, en cm desde la esquina de abajo a la izquierda del
 * mueble (x hacia la derecha, y hacia arriba). [cuerpo] es el cuerpo al que pertenece, [ruta]
 * la columna dentro de él (pares casillero, columna; vacía = el cuerpo mismo) e [indice] cuál de
 * los suyos es (el cajón 0 es el de abajo; la repisa 0, la más baja).
 */
data class ElementoRopero(
    val tipo: TipoElemento,
    val cuerpo: Int,
    val indice: Int,
    val x0: Float,
    val y0: Float,
    val x1: Float,
    val y1: Float,
    val ruta: List<Int> = emptyList()
) {
    fun contiene(x: Float, y: Float): Boolean = x in x0..x1 && y in y0..y1
    val area: Float get() = (x1 - x0) * (y1 - y0)
    /** ¿Es el mismo elemento (aunque haya cambiado de sitio)? */
    fun esElMismo(o: ElementoRopero?): Boolean = o != null && o.tipo == tipo && o.cuerpo == cuerpo && o.indice == indice && o.ruta == ruta
}

/** Un hueco del ropero: de cara a cara (x0, x1) y de la cara de arriba de su piso (y0) a la cara de abajo de su techo (y1). */
data class Hueco(val x0: Float, val x1: Float, val y0: Float, val y1: Float) {
    val ancho: Float get() = x1 - x0
    val alto: Float get() = y1 - y0
}

/**
 * Dónde cae cada cosa del ropero: lo que necesitan el dibujo (para pintarlo), la pantalla de
 * diseño (para saber qué se tocó) y el cálculo (para cortar cada cajón con su alto). Una sola
 * cuenta para los tres, para que lo que se ve sea lo que se corta.
 *
 * Todo lo de dentro de un cuerpo se cuenta sobre su [Hueco]: así vale igual para un cuerpo
 * entero (del piso al maletero o al techo) que para una columna dentro de un casillero.
 */
object RoperoGeometria {

    /** Cuánto lleva la ropa colgada: de la percha para abajo, lo que hay que dejar libre bajo el tubo. */
    const val ROPA_COLGADA_CM = 45f

    /** El x de cada cuerpo, de su cara izquierda a su cara derecha. */
    fun cuerposX(r: Ropero): List<Pair<Float, Float>> {
        var cx = r.espesorCm
        return r.cuerpos.map { c -> val izq = cx; cx += c.anchoCm + r.espesorCm; izq to izq + c.anchoCm }
    }

    /** La cara de arriba del piso. */
    fun pisoY(r: Ropero): Float = r.zocaloCm + r.espesorCm

    /** La cara de abajo del techo del cuerpo [i] (cada lado puede tener su alto). */
    fun techoY(r: Ropero, i: Int = 0): Float = r.altoDeCuerpo(i) - r.espesorCm

    /** El tope de la parte baja del cuerpo: la cara de abajo de la repisa del maletero, o el techo. */
    fun topeBajo(r: Ropero, i: Int = 0): Float = techoY(r, i) - (if (r.maleteroCm > 0f) r.maleteroCm + r.espesorCm else 0f)

    /** El hueco del cuerpo [i]: del piso al maletero (o al techo). */
    fun huecoDeCuerpo(r: Ropero, i: Int): Hueco {
        val (izq, der) = cuerposX(r)[i]
        return Hueco(izq, der, pisoY(r), topeBajo(r, i))
    }

    /**
     * El x de cada compartimento del maletero, de cara a cara. Si el maletero no tiene los
     * suyos, son los de los cuerpos.
     */
    fun maleterosX(r: Ropero): List<Pair<Float, Float>> {
        if (!r.maleteroPropio) return cuerposX(r)
        val e = r.espesorCm
        val m = r.maleteroCuerpos
        val ancho = (r.anchoInteriorCm - (m - 1) * e) / m
        return (0 until m).map { k -> val izq = e + k * (ancho + e); izq to izq + ancho }
    }

    // ==================== Lo de dentro de un hueco ====================

    /** Hasta dónde llegan los cajones apilados desde el piso del hueco. */
    fun topeDeCajones(r: Ropero, c: Cuerpo, h: Hueco): Float = h.y0 + c.altosDeCajones(r.altoCajonCm).sum()

    /**
     * Sobre los cajones va una tapa de melamina que los separa de lo de arriba (el colgador o el
     * hueco). Lo de arriba arranca en su cara de arriba; sin cajones, en el piso.
     */
    fun sobreLosCajones(r: Ropero, c: Cuerpo, h: Hueco): Float =
        if (c.cajonesEfectivos > 0) topeDeCajones(r, c, h) + (if (c.tapaSobreCajones) r.espesorCm else 0f) else h.y0

    /** Dónde va el tubo del colgador (su eje) en el hueco. */
    fun tuboY(r: Ropero, h: Hueco): Float = h.y1 - r.tuboBajoTopeCm

    /** Hasta dónde llegan las repisas: bajo la ropa colgada si hay tubo, o hasta el tope. Desde el piso del hueco. */
    private fun hastaLasRepisas(r: Ropero, c: Cuerpo, h: Hueco): Float =
        (if (c.llevaTubo) tuboY(r, h) - ROPA_COLGADA_CM else h.y1) - h.y0

    /**
     * La altura (cara de abajo, desde la cara de arriba del piso del hueco) de cada repisa: las
     * escritas a mano si las hay, y si no repartidas a partes iguales entre los cajones y lo que
     * haya arriba (la ropa colgada o el tope). En el colgador, las de fábrica van cada 30 cm.
     */
    fun alturasDeEntrepanos(r: Ropero, c: Cuerpo, h: Hueco): List<Float> {
        val n = c.entrepanosEfectivos
        if (n == 0) return emptyList()
        if (c.alturasEntrepanosCm.size == n) return c.alturasEntrepanosCm
        val desde = sobreLosCajones(r, c, h) - h.y0
        val hasta = hastaLasRepisas(r, c, h)
        if (c.tipo == TipoCuerpo.COLGAR) return (1..n).map { desde + 30f * it }.filter { it < hasta }
        // Primero se descuentan las repisas y lo que queda se reparte en n+1 huecos iguales:
        // así todos los casilleros tienen el mismo alto libre.
        val libre = ((hasta - desde - n * r.espesorCm) / (n + 1)).coerceAtLeast(1f)
        return (1..n).map { desde + libre * it + r.espesorCm * (it - 1) }
    }

    /**
     * El cuerpo con el casillero [k] de [altoCm] libres: se pone la repisa de encima y las de
     * más arriba se reparten de nuevo a partes iguales en lo que queda; las de abajo no se
     * tocan. En el casillero de arriba del todo se baja la repisa de debajo y no se mueve
     * ninguna otra. Así se hacen los de abajo chicos (zapatillas) y los demás iguales.
     */
    fun conAltoDeCasillero(r: Ropero, c: Cuerpo, h: Hueco, k: Int, altoCm: Float): Cuerpo {
        val e = r.espesorCm
        val repartidas = alturasDeEntrepanos(r, c, h)
        val n = repartidas.size
        if (n == 0) return c
        val alto = altoCm.coerceAtLeast(5f)
        val hasta = hastaLasRepisas(r, c, h)
        if (k >= n) {
            // El de arriba: la repisa de debajo baja hasta dejarle ese alto.
            return c.conAlturaDeEntrepano(n - 1, (hasta - alto - e).coerceAtLeast(5f), repartidas)
        }
        val suelo = if (k == 0) sobreLosCajones(r, c, h) - h.y0 else repartidas[k - 1] + e
        val nuevas = repartidas.toMutableList()
        nuevas[k] = suelo + alto
        val quedan = n - 1 - k
        if (quedan > 0) {
            val desde = nuevas[k] + e
            val libre = ((hasta - desde - quedan * e) / (quedan + 1)).coerceAtLeast(1f)
            for (j in 1..quedan) nuevas[k + j] = desde + libre * j + e * (j - 1)
        }
        return c.copy(alturasEntrepanosCm = nuevas)
    }

    /**
     * Los casilleros del hueco, de abajo arriba: del tope de los cajones (o el piso) a cada
     * repisa, y el último hasta el tope. Cada uno con su hueco (de cara a cara).
     */
    fun casilleros(r: Ropero, c: Cuerpo, h: Hueco): List<Hueco> {
        val e = r.espesorCm
        val salen = mutableListOf<Hueco>()
        var suelo = sobreLosCajones(r, c, h)
        alturasDeEntrepanos(r, c, h).forEach { alt ->
            salen.add(Hueco(h.x0, h.x1, suelo, h.y0 + alt))
            suelo = h.y0 + alt + e
        }
        if (h.y1 > suelo) salen.add(Hueco(h.x0, h.x1, suelo, h.y1))
        return salen
    }

    /** Los huecos de las columnas del casillero [k] (vacío si no está partido), con sus anchos ajustados al casillero. */
    fun columnasDeCasillero(r: Ropero, c: Cuerpo, casillero: Hueco, k: Int): List<Hueco> {
        val columnas = c.columnasDe(k)
        if (columnas.isEmpty()) return emptyList()
        val e = r.espesorCm
        val libre = casillero.ancho - (columnas.size - 1) * e
        val suma = columnas.sumOf { it.anchoCm.toDouble() }.toFloat().coerceAtLeast(1f)
        val factor = libre / suma
        var x = casillero.x0
        return columnas.map { col ->
            val w = col.anchoCm * factor
            val hh = Hueco(x, x + w, casillero.y0, casillero.y1)
            x += w + e
            hh
        }
    }

    // ==================== Por ruta ====================

    /** El hueco del cuerpo [i] o de la columna de su [ruta] (pares casillero, columna). */
    fun huecoDe(r: Ropero, i: Int, ruta: List<Int> = emptyList()): Hueco? {
        var c = r.cuerpos.getOrNull(i) ?: return null
        var h = huecoDeCuerpo(r, i)
        var p = 0
        while (p + 1 < ruta.size) {
            val k = ruta[p]; val j = ruta[p + 1]
            val cas = casilleros(r, c, h).getOrNull(k) ?: return null
            h = columnasDeCasillero(r, c, cas, k).getOrNull(j) ?: return null
            c = c.columnasDe(k)[j]
            p += 2
        }
        return h
    }

    /** Las repisas del cuerpo o columna de esa ruta, desde el piso de su hueco. */
    fun alturasDeEntrepanosDe(r: Ropero, i: Int, ruta: List<Int> = emptyList()): List<Float> {
        val c = r.cuerpoEn(i, ruta) ?: return emptyList()
        val h = huecoDe(r, i, ruta) ?: return emptyList()
        return alturasDeEntrepanos(r, c, h)
    }

    // ==================== Los elementos ====================

    /** Todo lo que hay en el ropero, con su sitio. Los cuerpos van primero, y lo suyo después. */
    fun elementos(r: Ropero): List<ElementoRopero> {
        val salen = mutableListOf<ElementoRopero>()
        val piso = pisoY(r)
        val e = r.espesorCm
        cuerposX(r).forEachIndexed { i, (izq, der) ->
            val c = r.cuerpos[i]
            val techo = techoY(r, i)
            val tope = topeBajo(r, i)
            // El cuerpo es la parte baja: con maletero, del piso a su repisa. El maletero se toca aparte.
            salen.add(ElementoRopero(TipoElemento.CUERPO, i, 0, izq, piso, der, if (r.maleteroCm > 0f) tope else techo))
            // La repisa del maletero: una por cuerpo, o una sola de lateral a lateral si el maletero es propio.
            if (r.maleteroCm > 0f && !r.maleteroPropio) {
                salen.add(ElementoRopero(TipoElemento.REPISA_MALETERO, i, 0, izq, tope, der, tope + e))
                salen.add(ElementoRopero(TipoElemento.MALETERO, i, 0, izq, tope + e, der, techo))
            }
            if (r.maleteroPropio && i == 0) {
                salen.add(ElementoRopero(TipoElemento.REPISA_MALETERO, 0, 0, e, tope, r.anchoCm - e, tope + e))
                maleterosX(r).forEachIndexed { k, (x0, x1) -> salen.add(ElementoRopero(TipoElemento.MALETERO, 0, k, x0, tope + e, x1, techo)) }
            }
            elementosDelHueco(r, i, emptyList(), c, huecoDeCuerpo(r, i), salen)
        }
        return salen
    }

    /** Lo de dentro de un hueco (cajones, tapa, repisas, casilleros, colgador, tubo) y, en los casilleros partidos, sus columnas. */
    private fun elementosDelHueco(r: Ropero, i: Int, ruta: List<Int>, c: Cuerpo, h: Hueco, salen: MutableList<ElementoRopero>) {
        val e = r.espesorCm
        var base = h.y0
        c.altosDeCajones(r.altoCajonCm).forEachIndexed { k, alto ->
            salen.add(ElementoRopero(TipoElemento.CAJON, i, k, h.x0, base, h.x1, base + alto, ruta))
            base += alto
        }
        if (c.cajonesEfectivos > 0) {
            // El espacio de todos los cajones (para su cota) y la tapa que los remata (si la llevan).
            salen.add(ElementoRopero(TipoElemento.ZONA_CAJONES, i, 0, h.x0, h.y0, h.x1, base, ruta))
            if (c.tapaSobreCajones) salen.add(ElementoRopero(TipoElemento.TAPA_CAJONES, i, 0, h.x0, base, h.x1, base + e, ruta))
        }
        alturasDeEntrepanos(r, c, h).forEachIndexed { k, alt ->
            salen.add(ElementoRopero(TipoElemento.ENTREPANO, i, k, h.x0, h.y0 + alt, h.x1, h.y0 + alt + e, ruta))
        }
        // Lo libre, partido en casilleros. El de arriba, si el cuerpo lleva tubo, es el colgador.
        // Así todo el cuerpo se toca por trozos; el cuerpo entero, por su cota.
        val cas = casilleros(r, c, h)
        cas.forEachIndexed { k, hk ->
            val esColgador = c.llevaTubo && k == cas.lastIndex
            salen.add(ElementoRopero(if (esColgador) TipoElemento.COLGADOR else TipoElemento.CASILLERO, i, k, hk.x0, hk.y0, hk.x1, hk.y1, ruta))
            val columnas = columnasDeCasillero(r, c, hk, k)
            columnas.forEachIndexed { j, hj ->
                if (j < columnas.lastIndex) salen.add(ElementoRopero(TipoElemento.DIVISION_COLUMNA, i, j, hj.x1, hk.y0, hj.x1 + e, hk.y1, ruta + listOf(k)))
                // La columna entera (como el cuerpo entero): se elige por su cota de abajo.
                salen.add(ElementoRopero(TipoElemento.COLUMNA, i, j, hj.x0, hj.y0, hj.x1, hj.y1, ruta + listOf(k, j)))
                elementosDelHueco(r, i, ruta + listOf(k, j), c.columnasDe(k)[j], hj, salen)
            }
        }
        if (c.llevaTubo) {
            val t = tuboY(r, h)
            salen.add(ElementoRopero(TipoElemento.TUBO, i, 0, h.x0, t - 2f, h.x1, t + 2f, ruta))
        }
    }

    private val trozos = setOf(TipoElemento.CASILLERO, TipoElemento.MALETERO, TipoElemento.COLGADOR, TipoElemento.ZONA_CAJONES)

    /**
     * Lo que hay en ese punto: lo más chico primero (un cajón o una repisa antes que el casillero
     * o el colgador que lo contiene, y dentro de un casillero partido lo de sus columnas antes
     * que él). El cuerpo entero nunca sale de aquí: se elige por su cota.
     */
    fun elementoEn(r: Ropero, x: Float, y: Float): ElementoRopero? {
        val todos = elementos(r).filter { it.contiene(x, y) && it.tipo != TipoElemento.CUERPO && it.tipo != TipoElemento.COLUMNA }
        return todos.filter { it.tipo !in trozos }.minByOrNull { it.area }
            ?: todos.filter { it.tipo in trozos }.minByOrNull { it.area }
    }

    /**
     * El ropero con el trozo [el] (casillero, colgador, maletero o espacio de cajones) puesto a
     * [altoCm] libres: lo que hace la cota de alto de cada trozo.
     */
    fun conAltoDeTrozo(r: Ropero, el: ElementoRopero, altoCm: Float): Ropero {
        if (el.tipo == TipoElemento.MALETERO) return r.copy(maleteroCm = altoCm.coerceIn(5f, 120f))
        val c = r.cuerpoEn(el.cuerpo, el.ruta) ?: return r
        val h = huecoDe(r, el.cuerpo, el.ruta) ?: return r
        val alto = altoCm.coerceAtLeast(5f)
        val nuevo = when (el.tipo) {
            TipoElemento.CASILLERO -> conAltoDeCasillero(r, c, h, el.indice, alto)
            TipoElemento.ZONA_CAJONES -> {
                // Todos los cajones del cuerpo a partes iguales en ese alto.
                val n = c.cajonesEfectivos
                c.copy(altosCajonesCm = List(n) { (alto / n).coerceIn(8f, 80f) })
            }
            TipoElemento.COLGADOR -> {
                val repisas = alturasDeEntrepanos(r, c, h)
                if (repisas.isNotEmpty()) conAltoDeCasillero(r, c, h, repisas.size, alto)
                else if (c.cajonesEfectivos > 0) {
                    // Sin repisas, lo que sobra se lo llevan los cajones, a partes iguales.
                    val n = c.cajonesEfectivos
                    val paraCajones = h.y1 - alto - r.espesorCm - h.y0
                    c.copy(altosCajonesCm = List(n) { (paraCajones / n).coerceIn(8f, 80f) })
                } else c
            }
            else -> c
        }
        return r.conCuerpoEn(el.cuerpo, el.ruta, nuevo)
    }

    /** El elemento "cuerpo entero" del cuerpo [i], el que se elige tocando su cota. */
    fun cuerpo(r: Ropero, i: Int): ElementoRopero? = elementos(r).firstOrNull { it.tipo == TipoElemento.CUERPO && it.cuerpo == i }
}
