package crystal.crystal.taller.melamina

/** Qué es cada cosa que hay dentro del ropero. */
enum class TipoElemento { CUERPO, CAJON, ENTREPANO, REPISA_MALETERO, TUBO, CASILLERO, TAPA_CAJONES, MALETERO, COLGADOR }

/**
 * Un elemento del ropero puesto en su sitio, en cm desde la esquina de abajo a la izquierda del
 * mueble (x hacia la derecha, y hacia arriba). [cuerpo] es el cuerpo al que pertenece e [indice]
 * cuál de los suyos es (el cajón 0 es el de abajo; la repisa 0, la más baja).
 */
data class ElementoRopero(
    val tipo: TipoElemento,
    val cuerpo: Int,
    val indice: Int,
    val x0: Float,
    val y0: Float,
    val x1: Float,
    val y1: Float
) {
    fun contiene(x: Float, y: Float): Boolean = x in x0..x1 && y in y0..y1
}

/**
 * Dónde cae cada cosa del ropero: lo que necesitan el dibujo (para pintarlo), la pantalla de
 * diseño (para saber qué se tocó) y el cálculo (para cortar cada cajón con su alto). Una sola
 * cuenta para los tres, para que lo que se ve sea lo que se corta.
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

    /**
     * El cuerpo con el casillero [k] de [altoCm] libres: se pone la repisa de encima y las de
     * más arriba se reparten de nuevo a partes iguales en lo que queda; las de abajo no se
     * tocan. En el casillero de arriba del todo se baja la repisa de debajo y no se mueve
     * ninguna otra. Así se hacen los de abajo chicos (zapatillas) y los demás iguales.
     */
    fun conAltoDeCasillero(r: Ropero, i: Int, k: Int, altoCm: Float): Cuerpo {
        val c = r.cuerpos.getOrNull(i) ?: return Cuerpo()
        val e = r.espesorCm
        val piso = pisoY(r)
        val repartidas = alturasDeEntrepanos(r, c)
        val n = repartidas.size
        if (n == 0) return c
        val alto = altoCm.coerceAtLeast(5f)
        val hasta = (if (c.llevaTubo) tuboY(r, i) - ROPA_COLGADA_CM else topeBajo(r, i)) - piso
        if (k >= n) {
            // El de arriba: la repisa de debajo baja hasta dejarle ese alto.
            return c.conAlturaDeEntrepano(n - 1, (hasta - alto - e).coerceAtLeast(5f), repartidas)
        }
        val suelo = if (k == 0) sobreLosCajones(r, c) - piso else repartidas[k - 1] + e
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

    /** La cara de abajo del techo del cuerpo [i] (cada lado puede tener su alto). */
    fun techoY(r: Ropero, i: Int = 0): Float = r.altoDeCuerpo(i) - r.espesorCm

    /** El tope de la parte baja del cuerpo: la cara de abajo de la repisa del maletero, o el techo. */
    fun topeBajo(r: Ropero, i: Int = 0): Float = techoY(r, i) - (if (r.maleteroCm > 0f) r.maleteroCm + r.espesorCm else 0f)

    /** Hasta dónde llegan los cajones apilados desde el piso. */
    fun topeDeCajones(r: Ropero, c: Cuerpo): Float = pisoY(r) + c.altosDeCajones(r.altoCajonCm).sum()

    /**
     * Sobre los cajones va una tapa de melamina que los separa de lo de arriba (el colgador o el
     * hueco). Lo de arriba arranca en su cara de arriba; sin cajones, en el piso.
     */
    fun sobreLosCajones(r: Ropero, c: Cuerpo): Float =
        if (c.cajonesEfectivos > 0) topeDeCajones(r, c) + r.espesorCm else pisoY(r)

    /** Dónde va el tubo del colgador del cuerpo [i] (su eje). */
    fun tuboY(r: Ropero, i: Int = 0): Float = topeBajo(r, i) - r.tuboBajoTopeCm

    /**
     * La altura (cara de abajo, desde la cara de arriba del piso) de cada repisa del cuerpo: las
     * escritas a mano si las hay, y si no repartidas a partes iguales entre los cajones y lo que
     * haya arriba (la ropa colgada o el tope). En el colgador, las de fábrica van cada 30 cm.
     */
    fun alturasDeEntrepanos(r: Ropero, c: Cuerpo): List<Float> {
        val n = c.entrepanosEfectivos
        if (n == 0) return emptyList()
        if (c.alturasEntrepanosCm.size == n) return c.alturasEntrepanosCm
        val i = r.cuerpos.indexOf(c).coerceAtLeast(0)
        val piso = pisoY(r)
        val desde = sobreLosCajones(r, c) - piso
        val hasta = (if (c.llevaTubo) tuboY(r, i) - ROPA_COLGADA_CM else topeBajo(r, i)) - piso
        if (c.tipo == TipoCuerpo.COLGAR) return (1..n).map { desde + 30f * it }.filter { it < hasta }
        // Primero se descuentan las repisas y lo que queda se reparte en n+1 huecos iguales:
        // así todos los casilleros tienen el mismo alto libre.
        val libre = ((hasta - desde - n * r.espesorCm) / (n + 1)).coerceAtLeast(1f)
        return (1..n).map { desde + libre * it + r.espesorCm * (it - 1) }
    }

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
            var base = piso
            c.altosDeCajones(r.altoCajonCm).forEachIndexed { k, alto ->
                salen.add(ElementoRopero(TipoElemento.CAJON, i, k, izq, base, der, base + alto))
                base += alto
            }
            if (c.cajonesEfectivos > 0) {
                salen.add(ElementoRopero(TipoElemento.TAPA_CAJONES, i, 0, izq, base, der, base + e))
                base += e
            }
            val repisas = alturasDeEntrepanos(r, c)
            repisas.forEachIndexed { k, h ->
                salen.add(ElementoRopero(TipoElemento.ENTREPANO, i, k, izq, piso + h, der, piso + h + e))
            }
            // Lo libre del cuerpo, partido en casilleros: del tope de los cajones (o el piso) a
            // cada repisa, y el último hasta el tope. El de arriba, si el cuerpo lleva tubo, es
            // el colgador. Así todo el cuerpo se toca por trozos; el cuerpo entero, por su cota.
            var suelo = base
            repisas.forEachIndexed { k, h ->
                salen.add(ElementoRopero(TipoElemento.CASILLERO, i, k, izq, suelo, der, piso + h))
                suelo = piso + h + e
            }
            if (tope > suelo) salen.add(ElementoRopero(if (c.llevaTubo) TipoElemento.COLGADOR else TipoElemento.CASILLERO, i, repisas.size, izq, suelo, der, tope))
            if (c.llevaTubo) {
                val t = tuboY(r, i)
                salen.add(ElementoRopero(TipoElemento.TUBO, i, 0, izq, t - 2f, der, t + 2f))
            }
        }
        return salen
    }

    /**
     * Lo que hay en ese punto: lo más chico primero (un cajón o una repisa antes que el casillero
     * o el colgador que lo contiene). El cuerpo entero nunca sale de aquí: se elige por su cota.
     */
    fun elementoEn(r: Ropero, x: Float, y: Float): ElementoRopero? {
        val todos = elementos(r).filter { it.contiene(x, y) && it.tipo != TipoElemento.CUERPO }
        return todos.firstOrNull { it.tipo != TipoElemento.CASILLERO && it.tipo != TipoElemento.MALETERO && it.tipo != TipoElemento.COLGADOR }
            ?: todos.firstOrNull()
    }

    /** El elemento "cuerpo entero" del cuerpo [i], el que se elige tocando su cota. */
    fun cuerpo(r: Ropero, i: Int): ElementoRopero? = elementos(r).firstOrNull { it.tipo == TipoElemento.CUERPO && it.cuerpo == i }
}
