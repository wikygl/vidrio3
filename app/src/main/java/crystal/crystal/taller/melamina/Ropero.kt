package crystal.crystal.taller.melamina

import org.json.JSONArray
import org.json.JSONObject

/**
 * Un ropero empotrado de melamina, tal como se apunta en obra: el hueco (ancho, alto, fondo) y
 * cómo se reparte por dentro.
 *
 * Todo en centímetros. El armazón son dos laterales de piso a techo, el piso sobre un zócalo, el
 * techo entre los laterales, y entre piso y techo las divisiones que separan los cuerpos. Cada
 * cuerpo lleva lo suyo: colgador, entrepaños, cajones o mezcla. Encima puede ir el maletero, una
 * repisa fija a lo ancho que hace un compartimento alto. Las puertas cubren todo el frente:
 * batientes (una o dos por cuerpo, y las del maletero aparte) o corredizas (dos o tres hojas).
 */
data class Ropero(
    val anchoCm: Float = 240f,
    val altoCm: Float = 240f,
    val fondoCm: Float = 60f,
    /** Espesor de la melamina del armazón y las puertas: 18 o 15 mm. */
    val espesorMm: Int = 18,
    val zocaloCm: Float = 10f,
    /** Alto interior del maletero (el compartimento de arriba). 0 = sin maletero. */
    val maleteroCm: Float = 0f,
    val cuerpos: List<Cuerpo> = listOf(Cuerpo(tipo = TipoCuerpo.COLGAR), Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 4)),
    val puertas: TipoPuertas = TipoPuertas.BATIENTES,
    /** Fondo de nordex (3 mm) clavado atrás. Empotrado a veces se deja la pared. */
    val conFondo: Boolean = true,
    /** Alto del frente de cada cajón. */
    val altoCajonCm: Float = 20f,
    /** Cómo se quiere ver en el apunte: con las puertas puestas o el interior. No cambia el cálculo. */
    val verPuertas: Boolean = false,
    // ---- Lo fino, que se toca en la pantalla de diseño ----
    /** Grosor del tapacanto (PVC) de lo de dentro: 0.45, 1, 2 o 3 mm; lo corriente es 0.45. */
    val tapacantoGrosorMm: Float = 0.45f,
    /** Grosor del tapacanto de lo que se ve (puertas, zócalo, frentes a la vista): lo corriente es 3. */
    val tapacantoPuertasMm: Float = 3f,
    /**
     * En cuántos compartimentos se parte el maletero. 0 = sigue a los cuerpos de abajo (la
     * repisa por cuerpo y las divisiones de piso a techo). Con 1 o más, la repisa va de
     * lateral a lateral, las divisiones de abajo llegan hasta ella y el maletero lleva las
     * suyas, a partes iguales.
     */
    val maleteroCuerpos: Int = 0,
    /** El zócalo delante, en el plano de las puertas (lo corriente), o metido debajo del piso entre los laterales. */
    val zocaloDelante: Boolean = true,
    /** Puertas interiores (dentro del armazón, cada una en su hueco) o frontales (encima del armazón, tapando medias divisiones). */
    val puertasInteriores: Boolean = false,
    /** Lo de dentro en melamina blanca y solo lo que se ve sin abrir (puertas, frentes a la vista, zócalo delante) del color elegido. */
    val interiorBlanco: Boolean = true,
    /** Hojas de cada puerta del maletero propio: 0 = las que tocan (1 hasta 60, 2 si es más ancho), 1 o 2. */
    val maleteroHojas: Int = 0,
    /** Espesor del fondo: 3 (nordex) o 5.5 (MDF). */
    val espesorFondoMm: Float = 3f,
    /** Cuántas hojas corredizas; 0 = las que tocan por el ancho (2 hasta 240, 3 más allá). */
    val hojasCorredizas: Int = 0,
    /** A cuánto del tope va el tubo del colgador. */
    val tuboBajoTopeCm: Float = 6f
) {
    val espesorCm: Float get() = espesorMm / 10f
    val tapacantoCm: Float get() = tapacantoGrosorMm / 10f
    val tapacantoPuertasCm: Float get() = tapacantoPuertasMm / 10f

    /** El maletero con sus propios compartimentos (no se puede bajo una escalera: ahí sigue a los cuerpos). */
    val maleteroPropio: Boolean get() = maleteroCm > 0f && maleteroCuerpos > 0 && !altosDesiguales
    val fondoNordexCm: Float get() = if (conFondo) espesorFondoMm / 10f else 0f

    /** El ancho libre entre los dos laterales. */
    val anchoInteriorCm: Float get() = anchoCm - 2 * espesorCm

    /** Lo que queda entre el piso y el techo. */
    val altoInteriorCm: Float get() = altoCm - zocaloCm - 2 * espesorCm

    /** El fondo de las piezas del armazón: el hueco menos el nordex de atrás. */
    val fondoArmazonCm: Float get() = fondoCm - fondoNordexCm

    /** El fondo útil de entrepaños y cajones: con corredizas, las hojas corren por dentro y se comen su carril. */
    val fondoInteriorCm: Float get() = fondoArmazonCm - (if (puertas == TipoPuertas.CORREDIZAS) CARRIL_CORREDIZAS_CM else 0f)

    /** Alto interior de la parte baja de cada cuerpo, debajo de la repisa del maletero si la hay. */
    val altoBajoCm: Float get() = altoInteriorCm - (if (maleteroCm > 0f) maleteroCm + espesorCm else 0f)

    /** El alto del ropero en el cuerpo [i]: el suyo si lo tiene, y si no el general. */
    fun altoDeCuerpo(i: Int): Float = cuerpos.getOrNull(i)?.altoCm?.takeIf { it >= 30f } ?: altoCm

    /** ¿Algún cuerpo tiene su propio alto? Entonces el techo va por cuerpos y los laterales cada uno a lo suyo. */
    val altosDesiguales: Boolean get() = cuerpos.any { it.altoCm >= 30f && kotlin.math.abs(it.altoCm - altoCm) > 0.05f }

    /** El alto mayor de todos, para encuadrar y para el fondo. */
    val altoMayorCm: Float get() = cuerpos.indices.maxOfOrNull { altoDeCuerpo(it) } ?: altoCm

    /** El mismo ropero con los cuerpos repartidos a partes iguales. */
    fun conCuerposIguales(cantidad: Int): Ropero {
        val n = cantidad.coerceIn(1, 8)
        val anchoCada = anchoDeCuerpoIgual(n)
        val nuevos = (0 until n).map { i -> (cuerpos.getOrNull(i) ?: if (i % 2 == 0) Cuerpo(tipo = TipoCuerpo.COLGAR) else Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 4)).copy(anchoCm = anchoCada) }
        return copy(cuerpos = nuevos)
    }

    fun anchoDeCuerpoIgual(n: Int): Float = ((anchoInteriorCm - (n - 1) * espesorCm) / n).coerceAtLeast(1f)

    /**
     * Cambia el ancho de un cuerpo y reparte lo que sobre o falte entre los demás, a partes
     * iguales, para que el conjunto siga midiendo el ancho del hueco.
     */
    fun conAnchoDeCuerpo(indice: Int, anchoCm: Float): Ropero {
        if (indice !in cuerpos.indices) return this
        val libre = anchoInteriorCm - (cuerpos.size - 1) * espesorCm
        // Los cuerpos que cambian de ancho reparten el suyo entre sus columnas (las fijadas se quedan).
        val nuevos = Cuerpo.conAnchoFijado(cuerpos, indice, anchoCm, libre)
        return copy(cuerpos = nuevos.mapIndexed { i, c -> if (kotlin.math.abs(c.anchoCm - cuerpos[i].anchoCm) > 0.01f) c.conAnchoTotal(c.anchoCm, espesorCm) else c })
    }

    /**
     * El ancho escrito para el cuerpo o la columna de esa [ruta], que se respeta: sus hermanas
     * fijadas no se tocan, y lo que cambie se lleva hacia arriba (el casillero, su cuerpo…) hasta
     * los cuerpos del ropero, donde lo absorben los que no están fijados. La medida de arriba es
     * la suma de las de abajo, no al revés.
     */
    fun conAnchoEn(indice: Int, ruta: List<Int>, anchoCm: Float): Ropero {
        if (ruta.size < 2) return conAnchoDeCuerpo(indice, anchoCm)
        val rutaPadre = ruta.dropLast(2)
        val k = ruta[ruta.size - 2]; val j = ruta.last()
        val padre = cuerpoEn(indice, rutaPadre) ?: return this
        val columnas = padre.columnasDe(k)
        if (j !in columnas.indices) return this
        val nuevasColumnas = columnas.mapIndexed { jj, col -> if (jj == j) col.conAnchoTotal(anchoCm.coerceAtLeast(5f), espesorCm).copy(anchoFijo = true) else col }
        val anchoPadre = nuevasColumnas.sumOf { it.anchoCm.toDouble() }.toFloat() + (columnas.size - 1) * espesorCm
        // El padre con esa columna puesta y sus otros casilleros repartidos al ancho nuevo.
        val padreNuevo = padre.conColumnas(k, nuevasColumnas).conAnchoTotal(anchoPadre, espesorCm, exceptoCasillero = k)
        return conCuerpoEn(indice, rutaPadre, padreNuevo).conAnchoEn(indice, rutaPadre, anchoPadre)
    }

    fun conCuerpo(indice: Int, cuerpo: Cuerpo): Ropero =
        if (indice !in cuerpos.indices) this else copy(cuerpos = cuerpos.mapIndexed { i, c -> if (i == indice) cuerpo.copy(anchoCm = c.anchoCm) else c })

    /**
     * El cuerpo que hay en la [ruta] dentro del cuerpo [indice]: la ruta va por pares
     * (casillero, columna), y vacía es el cuerpo mismo.
     */
    fun cuerpoEn(indice: Int, ruta: List<Int> = emptyList()): Cuerpo? {
        var c = cuerpos.getOrNull(indice) ?: return null
        var i = 0
        while (i + 1 < ruta.size) {
            c = c.columnasDe(ruta[i]).getOrNull(ruta[i + 1]) ?: return null
            i += 2
        }
        return c
    }

    /** El ropero con el árbol de cada cuerpo aplanado (ver [Cuerpo.aplanado]). */
    fun aplanado(): Ropero = copy(cuerpos = cuerpos.map { it.aplanado() })

    /** El ropero con el cuerpo de esa [ruta] cambiado por [nuevo] (el ancho se conserva, como en [conCuerpo]). */
    fun conCuerpoEn(indice: Int, ruta: List<Int>, nuevo: Cuerpo): Ropero {
        val c = cuerpos.getOrNull(indice) ?: return this
        return conCuerpo(indice, c.conCuerpoEn(ruta, nuevo))
    }

    /** Las medidas del hueco cambiadas: si cambió el ancho, los cuerpos se reparten de nuevo. */
    fun conHueco(ancho: Float, alto: Float, fondo: Float): Ropero {
        val base = copy(anchoCm = ancho.coerceAtLeast(30f), altoCm = alto.coerceAtLeast(30f), fondoCm = fondo.coerceAtLeast(20f))
        return if (kotlin.math.abs(base.anchoCm - anchoCm) > 0.01f) base.conCuerposIguales(cuerpos.size.coerceAtLeast(1)) else base
    }

    fun aJson(): String = JSONObject().apply {
        put("ancho", anchoCm); put("alto", altoCm); put("fondo", fondoCm)
        put("espesor", espesorMm); put("zocalo", zocaloCm); put("maletero", maleteroCm)
        put("puertas", puertas.name); put("conFondo", conFondo); put("altoCajon", altoCajonCm)
        put("verPuertas", verPuertas)
        put("tapacanto", tapacantoGrosorMm); put("tapacantoPuertas", tapacantoPuertasMm); put("espesorFondo", espesorFondoMm)
        put("maleteroCuerpos", maleteroCuerpos); put("maleteroHojas", maleteroHojas)
        put("zocaloDelante", zocaloDelante); put("puertasInteriores", puertasInteriores); put("interiorBlanco", interiorBlanco)
        put("hojasCorredizas", hojasCorredizas); put("tuboBajoTope", tuboBajoTopeCm)
        put("cuerpos", JSONArray().apply { cuerpos.forEach { put(it.aJson()) } })
    }.toString()

    companion object {
        /** Lo que las hojas corredizas se llevan del fondo: los rieles y el grosor de las hojas. */
        const val CARRIL_CORREDIZAS_CM = 8f

        fun desdeJson(texto: String?): Ropero? = runCatching {
            val o = JSONObject(texto.orEmpty())
            val cuerpos = mutableListOf<Cuerpo>()
            val arr = o.optJSONArray("cuerpos") ?: JSONArray()
            for (i in 0 until arr.length()) Cuerpo.desdeJson(arr.getJSONObject(i))?.let { cuerpos.add(it) }
            Ropero(
                anchoCm = o.optDouble("ancho", 240.0).toFloat(),
                altoCm = o.optDouble("alto", 240.0).toFloat(),
                fondoCm = o.optDouble("fondo", 60.0).toFloat(),
                espesorMm = o.optInt("espesor", 18),
                zocaloCm = o.optDouble("zocalo", 10.0).toFloat(),
                maleteroCm = o.optDouble("maletero", 0.0).toFloat(),
                cuerpos = cuerpos.ifEmpty { Ropero().cuerpos },
                puertas = runCatching { TipoPuertas.valueOf(o.optString("puertas", "BATIENTES")) }.getOrDefault(TipoPuertas.BATIENTES),
                conFondo = o.optBoolean("conFondo", true),
                altoCajonCm = o.optDouble("altoCajon", 20.0).toFloat(),
                verPuertas = o.optBoolean("verPuertas", false),
                tapacantoGrosorMm = o.optDouble("tapacanto", 0.45).toFloat(),
                tapacantoPuertasMm = o.optDouble("tapacantoPuertas", 3.0).toFloat(),
                maleteroCuerpos = o.optInt("maleteroCuerpos", 0),
                maleteroHojas = o.optInt("maleteroHojas", 0),
                zocaloDelante = o.optBoolean("zocaloDelante", true),
                puertasInteriores = o.optBoolean("puertasInteriores", false),
                interiorBlanco = o.optBoolean("interiorBlanco", true),
                espesorFondoMm = o.optDouble("espesorFondo", 3.0).toFloat(),
                hojasCorredizas = o.optInt("hojasCorredizas", 0),
                tuboBajoTopeCm = o.optDouble("tuboBajoTope", 6.0).toFloat()
            )
        }.getOrNull()
    }
}

enum class TipoCuerpo(val etiqueta: String) {
    COLGAR("Colgadores"),
    /** Repisas: los casilleros del plano. */
    ENTREPANOS("Casilleros"),
    CAJONES("Cajones"),
    /** Colgador arriba y cajones abajo, con casilleros entre medio si se piden. */
    MIXTO("Colgador + cajones"),
    /** Colgador arriba y casilleros abajo, repartidos bajo la ropa colgada. */
    COLGAR_CASILLEROS("Colgador + casilleros"),
    /** Cajones abajo y casilleros encima, sin colgador. */
    CAJONES_CASILLEROS("Casilleros + cajones")
}

enum class TipoPuertas(val etiqueta: String) {
    SIN("Sin puertas"),
    BATIENTES("Batientes"),
    CORREDIZAS("Corredizas")
}

/**
 * Un cuerpo del ropero: lo que hay entre dos divisiones (o división y lateral).
 *
 * [anchoCm] es su ancho interior. [entrepanos] son las repisas de la parte baja (en el
 * colgador, las de abajo, debajo de la ropa); [cajones] los cajones apilados desde el piso.
 */
data class Cuerpo(
    val anchoCm: Float = 60f,
    val tipo: TipoCuerpo = TipoCuerpo.ENTREPANOS,
    val entrepanos: Int = 0,
    val cajones: Int = 0,
    /** El alto de cada cajón, de abajo arriba; los que falten van con el alto por defecto del ropero. */
    val altosCajonesCm: List<Float> = emptyList(),
    /**
     * A qué altura va cada repisa (su cara de abajo), medida desde la cara de arriba del piso, de
     * abajo arriba. Vacío = repartidas a partes iguales en lo que queda libre.
     */
    val alturasEntrepanosCm: List<Float> = emptyList(),
    /** Hojas batientes de este cuerpo; 0 = las que tocan (1 hasta 60 cm de luz, 2 si es más ancho). */
    val hojasBatientes: Int = 0,
    /** El alto de este lado del ropero, si no es el general (bajo una escalera, un techo que baja): 0 = el general. */
    val altoCm: Float = 0f,
    /** Los cajones se ven desde fuera: sus frentes van en el plano de las puertas y la puerta del cuerpo arranca sobre ellos. */
    val cajonesALaVista: Boolean = false,
    /** El ancho se escribió a mano: al mover otro cuerpo, este no se toca. */
    val anchoFijo: Boolean = false,
    /**
     * Casilleros partidos en columnas: para el casillero [k], sus columnas, cada una un cuerpo
     * con lo suyo (repisas, cajones, colgador… y más columnas). Los anchos de las columnas
     * suman el ancho del casillero menos las divisiones entre ellas.
     */
    val partes: Map<Int, List<Cuerpo>> = emptyMap(),
    /** Puertas de este cuerpo, si no son las del ropero (en una columna, si las lleva). */
    val puertasPropias: TipoPuertas? = null,
    /** Cada casillero (y el colgador) con sus propias puertas, dentro de su hueco. */
    val puertasPorCasillero: Boolean = false,
    /** La tapa de melamina sobre los cajones. Sin ella, los cajones quedan dentro del casillero de encima (unidos). */
    val tapaSobreCajones: Boolean = true,
    /**
     * El alto del espacio de los cajones escrito a mano (0 = libre). Fijo, no se mueve: al
     * cambiar un cajón, los demás se reparten lo que quede; al cambiar cuántos son, se reparten.
     */
    val altoCajonesFijoCm: Float = 0f
) {
    /**
     * Una columna que no es más que un reparto: sin repisas, cajones, tubo ni puertas, y con su
     * único casillero partido en columnas. Sus columnas son hermanas de las de al lado.
     */
    val esSoloReparto: Boolean
        get() = entrepanosEfectivos == 0 && cajonesEfectivos == 0 && !llevaTubo && puertasPropias == null && !puertasPorCasillero
            && partes.keys == setOf(0) && columnasDe(0).size >= 2

    /**
     * El cuerpo con el árbol aplanado: las columnas que solo son un reparto se sustituyen por
     * sus columnas (que pasan a ser hermanas), y un casillero con una sola columna que es solo
     * reparto se parte directamente en las de ella. Así lo que se ve al lado es vecino de verdad.
     */
    fun aplanado(): Cuerpo {
        val nuevas = partes.mapValues { (_, cols) ->
            cols.map { it.aplanado() }.flatMap { col -> if (col.esSoloReparto) col.columnasDe(0) else listOf(col) }
        }
        return copy(partes = nuevas)
    }

    /** Las columnas en que está partido el casillero [k]; vacío si no lo está. */
    fun columnasDe(k: Int): List<Cuerpo> = partes[k].orEmpty()

    /** Este cuerpo con el casillero [k] partido en [columnas] (vacío = sin partir). */
    fun conColumnas(k: Int, columnas: List<Cuerpo>): Cuerpo =
        copy(partes = if (columnas.isEmpty()) partes - k else partes + (k to columnas))

    /** El casillero [k] partido en [n] columnas iguales (o sin partir con n < 2), de ancho total [anchoLibre] menos las divisiones. */
    fun conCasilleroPartido(k: Int, n: Int, anchoLibre: Float, espesorCm: Float): Cuerpo {
        if (n < 2) return conColumnas(k, emptyList())
        val cada = ((anchoLibre - (n - 1) * espesorCm) / n).coerceAtLeast(5f)
        val viejas = columnasDe(k)
        return conColumnas(k, (0 until n).map { j -> (viejas.getOrNull(j) ?: Cuerpo(tipo = TipoCuerpo.ENTREPANOS)).copy(anchoCm = cada, partes = viejas.getOrNull(j)?.partes.orEmpty()) })
    }

    /** Este cuerpo con lo que hay en la [ruta] (pares casillero, columna) cambiado por [nuevo]; el ancho de esa columna se conserva. */
    fun conCuerpoEn(ruta: List<Int>, nuevo: Cuerpo): Cuerpo {
        if (ruta.size < 2) return nuevo.copy(anchoCm = anchoCm)
        val k = ruta[0]; val j = ruta[1]
        val columnas = columnasDe(k)
        if (j !in columnas.indices) return this
        return conColumnas(k, columnas.mapIndexed { jj, col -> if (jj == j) col.conCuerpoEn(ruta.drop(2), nuevo) else col })
    }

    /**
     * Este cuerpo a [anchoCm] de ancho, con las columnas de cada casillero partido repartidas al
     * ancho nuevo: las fijadas se quedan y las demás se reparten lo que quede (o todas a escala
     * si todas están fijadas). Cada columna que cambie hace lo mismo con las suyas.
     */
    fun conAnchoTotal(anchoCm: Float, espesorCm: Float, exceptoCasillero: Int = -1): Cuerpo =
        copy(anchoCm = anchoCm, partes = partes.mapValues { (k, cols) ->
            if (k == exceptoCasillero) cols else repartir(cols, anchoCm - (cols.size - 1) * espesorCm, espesorCm)
        })

    val entrepanosEfectivos: Int get() = when (tipo) {
        TipoCuerpo.COLGAR -> entrepanos.coerceIn(0, 2)
        TipoCuerpo.ENTREPANOS -> entrepanos.coerceIn(0, 12)
        TipoCuerpo.CAJONES -> 0
        TipoCuerpo.MIXTO -> entrepanos.coerceIn(0, 4)
        TipoCuerpo.COLGAR_CASILLEROS -> entrepanos.coerceIn(1, 6)
        TipoCuerpo.CAJONES_CASILLEROS -> entrepanos.coerceIn(1, 12)
    }
    val cajonesEfectivos: Int get() = when (tipo) {
        TipoCuerpo.CAJONES -> cajones.coerceIn(1, 10)
        TipoCuerpo.MIXTO -> cajones.coerceIn(1, 6)
        TipoCuerpo.CAJONES_CASILLEROS -> cajones.coerceIn(1, 10)
        else -> 0
    }
    val llevaTubo: Boolean get() = tipo == TipoCuerpo.COLGAR || tipo == TipoCuerpo.MIXTO || tipo == TipoCuerpo.COLGAR_CASILLEROS

    /** El alto de cada cajón que se corta, de abajo arriba. */
    fun altosDeCajones(altoPorDefectoCm: Float): List<Float> {
        val altos = (0 until cajonesEfectivos).map { k -> altosCajonesCm.getOrNull(k)?.takeIf { it >= 8f } ?: altoPorDefectoCm }
        // Con el espacio fijo, entre todos suman eso: si no cuadran (cambió cuántos son), a escala.
        val suma = altos.sum()
        return if (altoCajonesFijoCm > 0f && altos.isNotEmpty() && kotlin.math.abs(suma - altoCajonesFijoCm) > 0.01f) altos.map { it * altoCajonesFijoCm / suma } else altos
    }

    /**
     * Este cuerpo con el cajón [k] de [altoCm]. Con el espacio libre, los demás se quedan como
     * estaban (el espacio crece o mengua); con el espacio fijo, los demás se reparten lo que
     * queda, a escala de lo que tenían, y el espacio no se mueve.
     */
    fun conAltoDeCajon(k: Int, altoCm: Float, altoPorDefectoCm: Float): Cuerpo {
        val altos = altosDeCajones(altoPorDefectoCm).toMutableList()
        if (k !in altos.indices) return this
        if (altoCajonesFijoCm <= 0f || altos.size == 1) {
            altos[k] = altoCm.coerceIn(8f, 80f)
            return copy(altosCajonesCm = altos)
        }
        val otros = altos.indices.filter { it != k }
        val nuevo = altoCm.coerceIn(8f, altoCajonesFijoCm - 8f * otros.size)
        val quedan = altoCajonesFijoCm - nuevo
        val sumaOtros = otros.sumOf { altos[it].toDouble() }.toFloat().coerceAtLeast(0.01f)
        otros.forEach { altos[it] = (altos[it] * quedan / sumaOtros).coerceAtLeast(8f) }
        altos[k] = nuevo
        return copy(altosCajonesCm = altos)
    }

    /** Este cuerpo con la repisa [k] a [alturaCm] del piso; las demás donde se reparten hoy. */
    fun conAlturaDeEntrepano(k: Int, alturaCm: Float, repartidas: List<Float>): Cuerpo {
        val alturas = (alturasEntrepanosCm.takeIf { it.size == repartidas.size } ?: repartidas).toMutableList()
        if (k !in alturas.indices) return this
        alturas[k] = alturaCm.coerceAtLeast(5f)
        return copy(alturasEntrepanosCm = alturas.sorted())
    }

    fun aJson(): JSONObject = JSONObject().apply {
        put("ancho", anchoCm); put("tipo", tipo.name); put("entrepanos", entrepanos); put("cajones", cajones)
        put("hojasBatientes", hojasBatientes); put("alto", altoCm); put("aLaVista", cajonesALaVista); put("altoCajonesFijo", altoCajonesFijoCm)
        put("anchoFijo", anchoFijo); put("puertasPorCasillero", puertasPorCasillero); put("tapa", tapaSobreCajones)
        puertasPropias?.let { put("puertasPropias", it.name) }
        if (partes.isNotEmpty()) put("partes", JSONArray().apply {
            partes.forEach { (k, columnas) -> put(JSONObject().apply { put("casillero", k); put("columnas", JSONArray().apply { columnas.forEach { put(it.aJson()) } }) }) }
        })
        put("altosCajones", JSONArray().apply { altosCajonesCm.forEach { put(it.toDouble()) } })
        put("alturasEntrepanos", JSONArray().apply { alturasEntrepanosCm.forEach { put(it.toDouble()) } })
    }

    companion object {
        /**
         * Las columnas repartidas para que sumen [libre]: las fijadas se quedan, las sueltas se
         * reparten el resto a partes iguales; si todas están fijadas, todas a escala. Las que
         * cambian reparten a su vez las suyas.
         */
        fun repartir(columnas: List<Cuerpo>, libre: Float, espesorCm: Float): List<Cuerpo> {
            if (columnas.isEmpty()) return columnas
            val suma = columnas.sumOf { it.anchoCm.toDouble() }.toFloat()
            if (kotlin.math.abs(suma - libre) < 0.01f) return columnas
            val sueltas = columnas.indices.filter { !columnas[it].anchoFijo }
            val nuevos: List<Float> = if (sueltas.isEmpty()) {
                val factor = libre / suma.coerceAtLeast(1f)
                columnas.map { it.anchoCm * factor }
            } else {
                val fijo = columnas.filter { it.anchoFijo }.sumOf { it.anchoCm.toDouble() }.toFloat()
                val cada = ((libre - fijo) / sueltas.size).coerceAtLeast(5f)
                columnas.mapIndexed { i, c -> if (i in sueltas) cada else c.anchoCm }
            }
            return columnas.mapIndexed { i, c -> if (kotlin.math.abs(nuevos[i] - c.anchoCm) > 0.01f) c.conAnchoTotal(nuevos[i], espesorCm) else c }
        }

        /**
         * Los cuerpos con el [indice] a [anchoCm], fijado, y lo que sobre o falte repartido entre
         * los que no están fijados (o entre todos los demás si todos lo están), para que sigan
         * sumando [libre].
         */
        fun conAnchoFijado(cuerpos: List<Cuerpo>, indice: Int, anchoCm: Float, libre: Float): List<Cuerpo> {
            val n = cuerpos.size
            if (indice !in cuerpos.indices) return cuerpos
            val nuevo = anchoCm.coerceIn(5f, (libre - 5f * (n - 1)).coerceAtLeast(5f))
            val otros = cuerpos.indices.filter { it != indice }
            var sueltos = otros.filter { !cuerpos[it].anchoFijo }
            if (sueltos.isEmpty()) sueltos = otros
            val fijos = otros - sueltos.toSet()
            val resto = libre - nuevo - fijos.sumOf { cuerpos[it].anchoCm.toDouble() }.toFloat()
            val cadaSuelto = if (sueltos.isEmpty()) 0f else (resto / sueltos.size).coerceAtLeast(5f)
            return cuerpos.mapIndexed { i, c ->
                when (i) {
                    indice -> c.copy(anchoCm = nuevo, anchoFijo = true)
                    in sueltos -> c.copy(anchoCm = cadaSuelto)
                    else -> c
                }
            }
        }

        fun desdeJson(o: JSONObject): Cuerpo? = runCatching {
            Cuerpo(
                anchoCm = o.optDouble("ancho", 60.0).toFloat(),
                tipo = TipoCuerpo.valueOf(o.optString("tipo", "ENTREPANOS")),
                entrepanos = o.optInt("entrepanos", 0),
                cajones = o.optInt("cajones", 0),
                hojasBatientes = o.optInt("hojasBatientes", 0),
                altoCm = o.optDouble("alto", 0.0).toFloat(),
                cajonesALaVista = o.optBoolean("aLaVista", false),
                anchoFijo = o.optBoolean("anchoFijo", false),
                puertasPorCasillero = o.optBoolean("puertasPorCasillero", false),
                tapaSobreCajones = o.optBoolean("tapa", true),
                altoCajonesFijoCm = o.optDouble("altoCajonesFijo", 0.0).toFloat(),
                puertasPropias = o.optString("puertasPropias", "").takeIf { it.isNotBlank() }?.let { runCatching { TipoPuertas.valueOf(it) }.getOrNull() },
                partes = (o.optJSONArray("partes") ?: JSONArray()).let { arr ->
                    (0 until arr.length()).mapNotNull { idx ->
                        val p = arr.optJSONObject(idx) ?: return@mapNotNull null
                        val cols = p.optJSONArray("columnas") ?: JSONArray()
                        p.optInt("casillero") to (0 until cols.length()).mapNotNull { j -> cols.optJSONObject(j)?.let { desdeJson(it) } }
                    }.filter { it.second.isNotEmpty() }.toMap()
                },
                altosCajonesCm = lista(o.optJSONArray("altosCajones")),
                alturasEntrepanosCm = lista(o.optJSONArray("alturasEntrepanos"))
            )
        }.getOrNull()

        private fun lista(arr: JSONArray?): List<Float> =
            if (arr == null) emptyList() else (0 until arr.length()).map { arr.optDouble(it, 0.0).toFloat() }
    }
}
