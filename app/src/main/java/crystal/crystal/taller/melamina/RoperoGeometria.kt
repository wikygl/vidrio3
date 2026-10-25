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

    /** La cara de arriba del piso: el zócalo, que llega hasta ahí (el piso es parte de él). */
    fun pisoY(r: Ropero): Float = r.pisoArribaCm

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
        // Cada división pasante sube por el maletero: se queda con la división del maletero que
        // tenga a menos de 2 cm (que se corre a ella), y si no hay ninguna, hace una más.
        val caras = maleterosIgualesX(r).dropLast(1).map { it.second }.toMutableList()
        val xs = cuerposX(r)
        divisionesPasantes(r).forEach { d ->
            val p = xs[d].second
            val k = caras.indices.minByOrNull { kotlin.math.abs(caras[it] - p) }
            if (k != null && kotlin.math.abs(caras[k] - p) <= COINCIDE_MALETERO_CM) caras[k] = p else caras.add(p)
        }
        caras.sort()
        var izq = e
        return caras.map { c -> val t = izq to c; izq = c + e; t } + (izq to r.anchoCm - e)
    }

    /** Los compartimentos del maletero propio a partes iguales, sin contar las divisiones pasantes. */
    private fun maleterosIgualesX(r: Ropero): List<Pair<Float, Float>> {
        val e = r.espesorCm
        val m = r.maleteroCuerpos
        val ancho = (r.anchoInteriorCm - (m - 1) * e) / m
        return (0 until m).map { k -> val izq = e + k * (ancho + e); izq to izq + ancho }
    }

    /** A cuánto de una división pasante una división del maletero se da por la misma. */
    private const val COINCIDE_MALETERO_CM = 2f

    // ==================== El armazón partido ====================

    /**
     * El largo máximo de un trozo de lo que va a lo ancho del mueble (piso, techo, repisa del
     * maletero, zócalo): el ancho de la plancha chica (183) menos 3 cm para escuadrar y la sierra.
     * Así cada trozo entra en la plancha tanto a lo largo como atravesado, con la veta a lo alto
     * o a lo ancho.
     */
    const val TRAMO_MAXIMO_CM = 180f

    /**
     * Las divisiones (la d separa los cuerpos d y d+1) que van del suelo arriba, como un
     * lateral, porque el piso, el techo, la repisa del maletero o el zócalo pasarían de
     * [TRAMO_MAXIMO_CM] y hay que partirlos. Nunca se juntan dos piezas a tope: cada trozo llega
     * a la cara de la división pasante y se atornilla a ella.
     *
     * Se prueban todas las combinaciones (hay 7 divisiones como mucho) y gana: la de menos
     * pasantes; después la de trozos más parejos (la pasante lo más centrada posible, que además
     * reparte el peso que el piso solo no aguanta); con maletero propio, la que caiga en sus
     * divisiones (así no le añade otra). Cada trozo se mide de punta a punta como el zócalo de
     * delante (con el lateral y media pasante), que es la pieza más larga de las que se parten.
     */
    fun divisionesPasantes(r: Ropero): List<Int> {
        val n = r.cuerpos.size
        if (r.anchoCm <= TRAMO_MAXIMO_CM + 0.01f || n < 2) return emptyList()
        val e = r.espesorCm
        val xs = cuerposX(r)
        val centros = (0 until n - 1).map { d -> xs[d].second + e / 2f }
        val centrosMaletero = if (r.maleteroPropio) maleterosIgualesX(r).dropLast(1).map { it.second + e / 2f } else emptyList()
        data class Opcion(val divisiones: List<Int>, val mayor: Float, val dispar: Float, val enMaletero: Int)
        val opciones = (1 until (1 shl (n - 1))).map { mask ->
            val ds = (0 until n - 1).filter { mask and (1 shl it) != 0 }
            val bordes = listOf(0f) + ds.map { centros[it] } + r.anchoCm
            val trozos = bordes.zipWithNext { a, b -> b - a }
            Opcion(ds, trozos.max(), trozos.max() - trozos.min(),
                ds.count { d -> centrosMaletero.any { kotlin.math.abs(it - centros[d]) <= COINCIDE_MALETERO_CM } })
        }
        val caben = opciones.filter { it.mayor <= TRAMO_MAXIMO_CM + 0.01f }
        // Si un cuerpo solo ya pasa del largo, no hay reparto que alcance: el que deje el trozo mayor más corto.
        val elegida = if (caben.isNotEmpty())
            caben.minWithOrNull(compareBy<Opcion> { it.divisiones.size }.thenBy { kotlin.math.round(it.mayor) }.thenByDescending { it.enMaletero }.thenBy { it.dispar })
        else opciones.minWithOrNull(compareBy<Opcion> { kotlin.math.round(it.mayor) }.thenBy { it.divisiones.size })
        return elegida?.divisiones.orEmpty()
    }

    /** Los trozos del piso (y de la repisa del maletero propio), de cara a cara: de lateral a división pasante. */
    fun tramosDePiso(r: Ropero): List<Pair<Float, Float>> {
        val e = r.espesorCm
        val xs = cuerposX(r)
        var desde = e
        val salen = divisionesPasantes(r).map { d -> val t = desde to xs[d].second; desde = xs[d].second + e; t }
        return salen + (desde to r.anchoCm - e)
    }

    /** Un trozo de techo: de [x0] a [x1] y con la cara de arriba a [alto]. */
    data class TramoTecho(val x0: Float, val x1: Float, val alto: Float)

    /**
     * Los trozos del techo. Se parte en las divisiones pasantes (cada lado llega a su cara) y,
     * bajo una escalera, donde cambia el alto: la división sube hasta el techo más alto, el techo
     * bajo llega a su cara y el alto la tapa por encima. Los cuerpos seguidos del mismo alto
     * llevan un solo techo.
     */
    fun tramosDeTecho(r: Ropero): List<TramoTecho> {
        val e = r.espesorCm
        val xs = cuerposX(r)
        val pasantes = divisionesPasantes(r).toSet()
        val salen = mutableListOf<TramoTecho>()
        var desde = e
        for (d in 0 until r.cuerpos.size - 1) {
            val a = r.altoDeCuerpo(d); val b = r.altoDeCuerpo(d + 1)
            val cara = xs[d].second
            when {
                d in pasantes -> { salen.add(TramoTecho(desde, cara, a)); desde = cara + e }
                kotlin.math.abs(a - b) <= 0.05f -> Unit
                a < b -> { salen.add(TramoTecho(desde, cara, a)); desde = cara }
                else -> { salen.add(TramoTecho(desde, cara + e, a)); desde = cara + e }
            }
        }
        salen.add(TramoTecho(desde, r.anchoCm - e, r.altoDeCuerpo(r.cuerpos.size - 1)))
        return salen
    }

    // ==================== Lo de dentro de un hueco ====================

    /**
     * El alto de la CAJA de cada cajón del hueco, de abajo arriba: los escritos (o el de por
     * defecto), pero si entre todos se pasan de lo que hay (el hueco menos la tapa), se encogen
     * a escala hasta caber: en un hueco de 51.8 dos cajones no pueden pasar de 25.9 cada uno.
     */
    fun altosDeCajas(r: Ropero, c: Cuerpo, h: Hueco): List<Float> {
        val altos = c.altosDeCajones(r.altoCajonCm)
        if (altos.isEmpty()) return altos
        val disponible = h.alto - (if (llevaTapa(r, c, h)) r.espesorCm else 0f)
        val suma = altos.sum()
        return if (suma <= disponible + 0.01f) altos else altos.map { it * disponible / suma }
    }

    /**
     * El alto del FRENTE de cada cajón (lo que ocupa cada uno en el espacio), de abajo arriba.
     * Con el espacio libre es el de su caja. Con el espacio fijo, los frentes rellenan el
     * espacio: lo que sobra sobre las cajas se reparte a partes iguales entre ellos (tres cajas
     * de 18 en 80 salen con frentes de 26.7; cajas de 17, 15 y 16.2 en 48.2 no sobran nada).
     */
    fun altosDeCajones(r: Ropero, c: Cuerpo, h: Hueco): List<Float> {
        val cajas = altosDeCajas(r, c, h)
        if (cajas.isEmpty() || c.altoCajonesFijoCm <= 0f) return cajas
        val sobra = (altoDeZonaDeCajones(r, c, h) - cajas.sum()).coerceAtLeast(0f)
        return cajas.map { it + sobra / cajas.size }
    }

    /**
     * Si los cajones llevan tapa: la suya, salvo que llenen el hueco entero (sin repisas ni tubo
     * encima): ahí el tablero de arriba ya hace de tapa. Dos cajones de 25.9 en un hueco de 51.8.
     */
    fun llevaTapa(r: Ropero, c: Cuerpo, h: Hueco): Boolean {
        if (c.cajonesEfectivos == 0 || !c.tapaSobreCajones) return false
        if (c.entrepanosEfectivos > 0 || c.llevaTubo) return true
        val altoZona = if (c.altoCajonesFijoCm > 0f) maxOf(c.altoCajonesFijoCm, c.altosDeCajones(r.altoCajonCm).sum()) else c.altosDeCajones(r.altoCajonCm).sum()
        return altoZona < h.alto - r.espesorCm - 0.05f
    }

    /**
     * El alto del espacio de los cajones: el escrito a mano (fijo, tope del hueco menos la tapa
     * si se pasa) o, si es libre, lo que suman los cajones.
     */
    fun altoDeZonaDeCajones(r: Ropero, c: Cuerpo, h: Hueco): Float {
        if (c.cajonesEfectivos == 0) return 0f
        val suma = altosDeCajas(r, c, h).sum()
        if (c.altoCajonesFijoCm <= 0f) return suma
        val disponible = h.alto - (if (llevaTapa(r, c, h)) r.espesorCm else 0f)
        return c.altoCajonesFijoCm.coerceIn(suma, disponible.coerceAtLeast(suma))
    }

    /** Hasta dónde llega el espacio de los cajones desde el piso del hueco (la cara de abajo de la tapa). */
    fun topeDeCajones(r: Ropero, c: Cuerpo, h: Hueco): Float = h.y0 + altoDeZonaDeCajones(r, c, h)

    /**
     * Sobre los cajones va una tapa de melamina que los separa de lo de arriba (el colgador o el
     * hueco). Lo de arriba arranca en su cara de arriba; sin cajones, en el piso.
     */
    fun sobreLosCajones(r: Ropero, c: Cuerpo, h: Hueco): Float =
        if (c.cajonesEfectivos > 0) topeDeCajones(r, c, h) + (if (llevaTapa(r, c, h)) r.espesorCm else 0f) else h.y0

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

    // ==================== Casilleros fijos ====================
    // Lo que se escribe a mano de un casillero (o del colgador, que es el último) queda fijo hasta
    // que se suelta: al editar otro, repartir o cambiar los cajones, lo que falte o sobre se lo
    // llevan los libres (pedido del usuario, 2026-10-11: el colgador igualado a 93.4 cambiaba al
    // editar el casillero de debajo).

    /** Lo menos que puede quedar libre en un casillero. */
    const val CASILLERO_MINIMO_CM = 5f

    /** El alto libre de cada casillero del hueco, de abajo arriba (el colgador, si lo hay, es el último). */
    fun altosDeCasilleros(r: Ropero, c: Cuerpo, h: Hueco): List<Float> = casilleros(r, c, h).map { it.alto }

    /** Los casilleros fijos que existen hoy (con menos repisas, los de más arriba ya no cuentan). */
    fun fijosDe(c: Cuerpo): Map<Int, Float> = c.casillerosFijosCm.filterKeys { it in 0..c.entrepanosEfectivos }

    /** El cuerpo con los casilleros de esos altos libres, de abajo arriba: cada repisa donde la dejan los de debajo. */
    private fun conAltos(r: Ropero, c: Cuerpo, h: Hueco, altos: List<Float>): Cuerpo {
        var suelo = sobreLosCajones(r, c, h) - h.y0
        val alturas = altos.dropLast(1).map { a -> (suelo + a).also { suelo = it + r.espesorCm } }
        return c.copy(alturasEntrepanosCm = alturas)
    }

    /**
     * Reparte [resto] entre los casilleros [libres] a partes iguales; el colgador, si está entre
     * ellos con otros, se lleva además lo del tubo y la ropa colgada, como en el reparto de fábrica
     * (si no alcanza, todos iguales). False si no cabe [CASILLERO_MINIMO_CM] en cada uno.
     */
    private fun repartirEntre(r: Ropero, c: Cuerpo, h: Hueco, altos: MutableList<Float>, libres: List<Int>, resto: Float): Boolean {
        if (libres.isEmpty()) return false
        val colgador = if (c.llevaTubo) altos.lastIndex else -1
        var extra = if (colgador in libres && libres.size > 1) (h.y1 - h.y0) - hastaLasRepisas(r, c, h) else 0f
        var cada = (resto - extra) / libres.size
        if (cada < CASILLERO_MINIMO_CM && extra > 0f) { extra = 0f; cada = resto / libres.size }
        if (cada < CASILLERO_MINIMO_CM) return false
        libres.forEach { altos[it] = cada + if (it == colgador) extra else 0f }
        return true
    }

    /**
     * "Poner": el casillero [k] a [altoCm] libres, fijo. Los libres de arriba se reparten a partes
     * iguales lo que quede (los fijos no se tocan); si arriba no hay libres, los de abajo. Null si
     * no hay ningún libre que se lleve la diferencia o no cabe.
     */
    fun ponerAltoDeCasillero(r: Ropero, c: Cuerpo, h: Hueco, k: Int, altoCm: Float): Cuerpo? {
        val altos = altosDeCasilleros(r, c, h).toMutableList()
        if (k !in altos.indices || altos.size < 2) return null
        val alto = altoCm.coerceAtLeast(CASILLERO_MINIMO_CM)
        val fijos = fijosDe(c) - k
        val total = altos.sum()
        altos[k] = alto
        val arriba = (k + 1 until altos.size).filter { it !in fijos }
        val abajo = (0 until k).filter { it !in fijos }
        for (grupo in listOf(arriba, abajo)) {
            if (grupo.isEmpty()) continue
            val resto = total - altos.indices.filter { it !in grupo }.sumOf { altos[it].toDouble() }.toFloat()
            val prueba = altos.toMutableList()
            if (repartirEntre(r, c, h, prueba, grupo, resto)) return conAltos(r, c, h, prueba).copy(casillerosFijosCm = fijos + (k to alto))
        }
        return null
    }

    /** "Poner" que, si no se puede, deja el cuerpo como estaba. */
    fun conAltoDeCasillero(r: Ropero, c: Cuerpo, h: Hueco, k: Int, altoCm: Float): Cuerpo =
        ponerAltoDeCasillero(r, c, h, k, altoCm) ?: c

    /**
     * "Solo esta": el casillero [k] a [altoCm] libres, fijo, moviendo una sola repisa: la de encima
     * (lo cambia el casillero de arriba) o, si ese está fijo o no hay, la de debajo. Null si los dos
     * vecinos están fijos o el vecino se queda sin sitio.
     */
    fun conAltoDeCasilleroSoloEsa(r: Ropero, c: Cuerpo, h: Hueco, k: Int, altoCm: Float): Cuerpo? {
        val altos = altosDeCasilleros(r, c, h).toMutableList()
        if (k !in altos.indices || altos.size < 2) return null
        val alto = altoCm.coerceAtLeast(CASILLERO_MINIMO_CM)
        val fijos = fijosDe(c) - k
        val vecino = listOf(k + 1, k - 1).firstOrNull { it in altos.indices && it !in fijos } ?: return null
        val delVecino = altos[vecino] + altos[k] - alto
        if (delVecino < CASILLERO_MINIMO_CM) return null
        altos[k] = alto
        altos[vecino] = delVecino
        return conAltos(r, c, h, altos).copy(casillerosFijosCm = fijos + (k to alto))
    }

    /** "Repartir": sin fijos, el reparto de fábrica; con fijos, los libres a partes iguales en lo que dejan. */
    fun repartidoConFijos(r: Ropero, c: Cuerpo, h: Hueco): Cuerpo {
        val fijos = fijosDe(c)
        if (fijos.isEmpty()) return c.copy(alturasEntrepanosCm = emptyList(), casillerosFijosCm = emptyMap())
        val altos = altosDeCasilleros(r, c, h).toMutableList()
        val libres = altos.indices.filter { it !in fijos }
        val resto = altos.sum() - fijos.keys.filter { it in altos.indices }.sumOf { altos[it].toDouble() }.toFloat()
        return if (repartirEntre(r, c, h, altos, libres, resto)) conAltos(r, c, h, altos) else c
    }

    /** La repisa [k] es el techo del casillero k y el piso del k + 1: no se mueve si alguno de los dos está fijo. */
    fun repisaTocaFijo(c: Cuerpo, k: Int): Boolean = fijosDe(c).let { k in it || (k + 1) in it }

    /**
     * Los fijos de vuelta a su alto si otro cambio los movió (los cajones, el alto del lado): lo
     * que sobre o falte se lo lleva el libre más cercano de arriba (o de abajo); si no le cabe,
     * todos los libres a partes iguales. Sin libres, se queda como está.
     */
    fun conFijosRespetados(r: Ropero, c: Cuerpo, h: Hueco): Cuerpo {
        val fijos = fijosDe(c)
        if (fijos.isEmpty()) return c
        val altos = altosDeCasilleros(r, c, h).toMutableList()
        val movidos = fijos.filter { (k, alto) -> k in altos.indices && kotlin.math.abs(altos[k] - alto) > 0.05f }
        if (movidos.isEmpty()) return c
        val libres = altos.indices.filter { it !in fijos }
        if (libres.isEmpty()) return c
        val total = altos.sum()
        movidos.forEach { (k, alto) -> altos[k] = alto }
        val k0 = movidos.keys.first()
        val cercano = libres.minByOrNull { if (it > k0) it - k0 else k0 - it + altos.size } ?: return c
        val sobra = total - altos.sum()
        if (altos[cercano] + sobra >= CASILLERO_MINIMO_CM) altos[cercano] += sobra
        else {
            val resto = total - altos.indices.filter { it !in libres }.sumOf { altos[it].toDouble() }.toFloat()
            if (resto / libres.size < CASILLERO_MINIMO_CM) return c
            libres.forEach { altos[it] = resto / libres.size }
        }
        return conAltos(r, c, h, altos)
    }

    /** El ropero entero con los fijos respetados en cada cuerpo y cada columna (de fuera adentro: las columnas dependen de su casillero). */
    fun conFijosRespetados(r: Ropero): Ropero {
        var salida = r
        fun recorrer(i: Int, ruta: List<Int>) {
            val c = salida.cuerpoEn(i, ruta) ?: return
            val h = huecoDe(salida, i, ruta) ?: return
            val nuevo = conFijosRespetados(salida, c, h)
            if (nuevo != c) salida = salida.conCuerpoEn(i, ruta, nuevo)
            val actual = salida.cuerpoEn(i, ruta) ?: return
            actual.partes.forEach { (k, cols) -> cols.indices.forEach { j -> recorrer(i, ruta + listOf(k, j)) } }
        }
        r.cuerpos.indices.forEach { recorrer(it, emptyList()) }
        return salida
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
        altosDeCajones(r, c, h).forEachIndexed { k, alto ->
            salen.add(ElementoRopero(TipoElemento.CAJON, i, k, h.x0, base, h.x1, base + alto, ruta))
            base += alto
        }
        if (c.cajonesEfectivos > 0) {
            // El espacio de todos los cajones (para su cota; con alto fijo, lo que sobra queda libre
            // encima de ellos) y la tapa que lo remata (si la llevan).
            base = topeDeCajones(r, c, h)
            salen.add(ElementoRopero(TipoElemento.ZONA_CAJONES, i, 0, h.x0, h.y0, h.x1, base, ruta))
            if (llevaTapa(r, c, h)) salen.add(ElementoRopero(TipoElemento.TAPA_CAJONES, i, 0, h.x0, base, h.x1, base + e, ruta))
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
    private val finos = setOf(TipoElemento.ENTREPANO, TipoElemento.TAPA_CAJONES, TipoElemento.REPISA_MALETERO, TipoElemento.TUBO, TipoElemento.DIVISION_COLUMNA)

    /** Hasta cuánto de lejos (cm) se da por tocado un tablero fino: con el dedo no se acierta a 1.8 cm. */
    const val HOLGURA_FINOS_CM = 2.5f

    /**
     * Lo que hay en ese punto: primero un tablero fino cerca (repisa, tapa, tubo, división: se
     * dan por tocados hasta a [HOLGURA_FINOS_CM], que si no siempre gana el cajón de al lado);
     * luego lo más chico (un cajón antes que el casillero que lo contiene, y dentro de un
     * casillero partido lo de sus columnas antes que él). El cuerpo entero nunca sale de aquí:
     * se elige por su cota.
     */
    fun elementoEn(r: Ropero, x: Float, y: Float): ElementoRopero? {
        val todos = elementos(r).filter { it.tipo != TipoElemento.CUERPO && it.tipo != TipoElemento.COLUMNA }
        val fino = todos.filter { it.tipo in finos && x in it.x0..it.x1 && y > it.y0 - HOLGURA_FINOS_CM && y < it.y1 + HOLGURA_FINOS_CM }
            .minByOrNull { kotlin.math.abs((it.y0 + it.y1) / 2f - y) }
        if (fino != null) return fino
        val dentro = todos.filter { it.contiene(x, y) }
        return dentro.filter { it.tipo !in trozos }.minByOrNull { it.area }
            ?: dentro.filter { it.tipo in trozos }.minByOrNull { it.area }
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
                // Todos los cajones del cuerpo a partes iguales en ese alto, que queda fijo: al
                // cambiar luego un cajón, los demás se reparten lo que quede y el espacio no se mueve.
                val n = c.cajonesEfectivos
                c.copy(altosCajonesCm = List(n) { (alto / n).coerceIn(8f, 80f) }, altoCajonesFijoCm = alto)
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

    /** Los huecos de las hermanas de [el] (los cuerpos, o las columnas del mismo casillero), de izquierda a derecha. */
    fun hermanasDe(r: Ropero, el: ElementoRopero): List<Hueco> {
        if (el.ruta.size < 2) return r.cuerpos.indices.map { huecoDeCuerpo(r, it) }
        val rutaPadre = el.ruta.dropLast(2)
        val k = el.ruta[el.ruta.size - 2]
        val padre = r.cuerpoEn(el.cuerpo, rutaPadre) ?: return emptyList()
        val huecoPadre = huecoDe(r, el.cuerpo, rutaPadre) ?: return emptyList()
        val casillero = casilleros(r, padre, huecoPadre).getOrNull(k) ?: return emptyList()
        return columnasDeCasillero(r, padre, casillero, k)
    }

    /**
     * Dónde caería [el] si se suelta con su centro en [xCm]: el hueco entre hermanas (0 = antes de
     * la primera, n = después de la última) cuyo borde queda más cerca, y la x de ese borde.
     */
    fun destinoDeMovimiento(r: Ropero, el: ElementoRopero, xCm: Float, copia: Boolean = false): Pair<Int, Float> {
        val hermanas = hermanasDe(r, el)
        if (hermanas.isEmpty()) return 0 to xCm
        // Los bordes donde se puede meter: el borde izquierdo de cada hermana y el derecho de la última.
        val bordes = hermanas.map { it.x0 } + hermanas.last().x1
        val cual = bordes.indices.minByOrNull { kotlin.math.abs(bordes[it] - xCm) } ?: 0
        // Al mover, con el elemento fuera de la lista los huecos a su derecha corren uno; al copiar, no.
        val propio = hermanas.indexOfFirst { kotlin.math.abs(it.x0 - el.x0) < 0.01f }
        val destino = if (!copia && propio >= 0 && cual > propio) cual - 1 else cual
        return destino to bordes[cual]
    }

    /** El elemento "cuerpo entero" del cuerpo [i], el que se elige tocando su cota. */
    fun cuerpo(r: Ropero, i: Int): ElementoRopero? = elementos(r).firstOrNull { it.tipo == TipoElemento.CUERPO && it.cuerpo == i }
}
