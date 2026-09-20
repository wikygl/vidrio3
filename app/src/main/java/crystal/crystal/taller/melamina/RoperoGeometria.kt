package crystal.crystal.taller.melamina

/** Qué es cada cosa que hay dentro del ropero. */
enum class TipoElemento { CUERPO, CAJON, ENTREPANO, REPISA_MALETERO, TUBO }

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

    /** La cara de abajo del techo. */
    fun techoY(r: Ropero): Float = r.altoCm - r.espesorCm

    /** El tope de la parte baja del cuerpo: la cara de abajo de la repisa del maletero, o el techo. */
    fun topeBajo(r: Ropero): Float = techoY(r) - (if (r.maleteroCm > 0f) r.maleteroCm + r.espesorCm else 0f)

    /** Hasta dónde llegan los cajones apilados desde el piso. */
    fun topeDeCajones(r: Ropero, c: Cuerpo): Float = pisoY(r) + c.altosDeCajones(r.altoCajonCm).sum()

    /** Dónde va el tubo del colgador (su eje). */
    fun tuboY(r: Ropero): Float = topeBajo(r) - r.tuboBajoTopeCm

    /**
     * La altura (cara de abajo, desde la cara de arriba del piso) de cada repisa del cuerpo: las
     * escritas a mano si las hay, y si no repartidas a partes iguales entre los cajones y lo que
     * haya arriba (la ropa colgada o el tope). En el colgador, las de fábrica van cada 30 cm.
     */
    fun alturasDeEntrepanos(r: Ropero, c: Cuerpo): List<Float> {
        val n = c.entrepanosEfectivos
        if (n == 0) return emptyList()
        if (c.alturasEntrepanosCm.size == n) return c.alturasEntrepanosCm
        val piso = pisoY(r)
        val desde = topeDeCajones(r, c) - piso
        val hasta = (if (c.llevaTubo) tuboY(r) - ROPA_COLGADA_CM else topeBajo(r)) - piso
        if (c.tipo == TipoCuerpo.COLGAR) return (1..n).map { desde + 30f * it }.filter { it < hasta }
        val paso = (hasta - desde) / (n + 1)
        return (1..n).map { desde + paso * it }
    }

    /** Todo lo que hay en el ropero, con su sitio. Los cuerpos van primero, y lo suyo después. */
    fun elementos(r: Ropero): List<ElementoRopero> {
        val salen = mutableListOf<ElementoRopero>()
        val piso = pisoY(r)
        val techo = techoY(r)
        val tope = topeBajo(r)
        val e = r.espesorCm
        cuerposX(r).forEachIndexed { i, (izq, der) ->
            val c = r.cuerpos[i]
            salen.add(ElementoRopero(TipoElemento.CUERPO, i, 0, izq, piso, der, techo))
            if (r.maleteroCm > 0f) salen.add(ElementoRopero(TipoElemento.REPISA_MALETERO, i, 0, izq, tope, der, tope + e))
            var base = piso
            c.altosDeCajones(r.altoCajonCm).forEachIndexed { k, alto ->
                salen.add(ElementoRopero(TipoElemento.CAJON, i, k, izq, base, der, base + alto))
                base += alto
            }
            alturasDeEntrepanos(r, c).forEachIndexed { k, h ->
                salen.add(ElementoRopero(TipoElemento.ENTREPANO, i, k, izq, piso + h, der, piso + h + e))
            }
            if (c.llevaTubo) {
                val t = tuboY(r)
                salen.add(ElementoRopero(TipoElemento.TUBO, i, 0, izq, t - 2f, der, t + 2f))
            }
        }
        return salen
    }

    /** Lo que hay en ese punto: lo más chico primero (un cajón antes que su cuerpo). */
    fun elementoEn(r: Ropero, x: Float, y: Float): ElementoRopero? {
        val todos = elementos(r).filter { it.contiene(x, y) }
        return todos.firstOrNull { it.tipo != TipoElemento.CUERPO } ?: todos.firstOrNull()
    }
}
