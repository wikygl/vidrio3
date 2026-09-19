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
    val altoCajonCm: Float = 20f
) {
    val espesorCm: Float get() = espesorMm / 10f
    val fondoNordexCm: Float get() = if (conFondo) 0.3f else 0f

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

    /** Los anchos interiores de los cuerpos, que con las divisiones entre ellos suman el ancho interior. */
    val anchosDeCuerpos: List<Float> get() = cuerpos.map { it.anchoCm }

    /** El mismo ropero con los cuerpos repartidos a partes iguales. */
    fun conCuerposIguales(cantidad: Int): Ropero {
        val n = cantidad.coerceIn(1, 8)
        val anchoCada = anchoDeCuerpoIgual(n)
        val nuevos = (0 until n).map { i -> (cuerpos.getOrNull(i) ?: if (i % 2 == 0) Cuerpo(tipo = TipoCuerpo.COLGAR, entrepanos = 1) else Cuerpo(tipo = TipoCuerpo.ENTREPANOS, entrepanos = 4)).copy(anchoCm = anchoCada) }
        return copy(cuerpos = nuevos)
    }

    fun anchoDeCuerpoIgual(n: Int): Float = ((anchoInteriorCm - (n - 1) * espesorCm) / n).coerceAtLeast(1f)

    /**
     * Cambia el ancho de un cuerpo y reparte lo que sobre o falte entre los demás, a partes
     * iguales, para que el conjunto siga midiendo el ancho del hueco.
     */
    fun conAnchoDeCuerpo(indice: Int, anchoCm: Float): Ropero {
        if (indice !in cuerpos.indices) return this
        val n = cuerpos.size
        val libre = anchoInteriorCm - (n - 1) * espesorCm
        val nuevo = anchoCm.coerceIn(10f, (libre - 10f * (n - 1)).coerceAtLeast(10f))
        val restoCada = if (n > 1) (libre - nuevo) / (n - 1) else 0f
        return copy(cuerpos = cuerpos.mapIndexed { i, c -> c.copy(anchoCm = if (i == indice) nuevo else restoCada) })
    }

    fun conCuerpo(indice: Int, cuerpo: Cuerpo): Ropero =
        if (indice !in cuerpos.indices) this else copy(cuerpos = cuerpos.mapIndexed { i, c -> if (i == indice) cuerpo.copy(anchoCm = c.anchoCm) else c })

    /** Las medidas del hueco cambiadas: los cuerpos se reparten de nuevo a lo ancho. */
    fun conHueco(ancho: Float, alto: Float, fondo: Float): Ropero {
        val base = copy(anchoCm = ancho.coerceAtLeast(30f), altoCm = alto.coerceAtLeast(30f), fondoCm = fondo.coerceAtLeast(20f))
        return base.conCuerposIguales(cuerpos.size.coerceAtLeast(1))
    }

    fun aJson(): String = JSONObject().apply {
        put("ancho", anchoCm); put("alto", altoCm); put("fondo", fondoCm)
        put("espesor", espesorMm); put("zocalo", zocaloCm); put("maletero", maleteroCm)
        put("puertas", puertas.name); put("conFondo", conFondo); put("altoCajon", altoCajonCm)
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
                altoCajonCm = o.optDouble("altoCajon", 20.0).toFloat()
            )
        }.getOrNull()
    }
}

enum class TipoCuerpo(val etiqueta: String) {
    COLGAR("Colgador"),
    ENTREPANOS("Entrepaños"),
    CAJONES("Cajones"),
    /** Colgador arriba y cajones abajo, con entrepaños entre medio si se piden. */
    MIXTO("Colgador + cajones")
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
    val cajones: Int = 0
) {
    val entrepanosEfectivos: Int get() = when (tipo) {
        TipoCuerpo.COLGAR -> entrepanos.coerceIn(0, 2)
        TipoCuerpo.ENTREPANOS -> entrepanos.coerceIn(0, 12)
        TipoCuerpo.CAJONES -> 0
        TipoCuerpo.MIXTO -> entrepanos.coerceIn(0, 4)
    }
    val cajonesEfectivos: Int get() = when (tipo) {
        TipoCuerpo.CAJONES -> cajones.coerceIn(1, 10)
        TipoCuerpo.MIXTO -> cajones.coerceIn(1, 6)
        else -> 0
    }
    val llevaTubo: Boolean get() = tipo == TipoCuerpo.COLGAR || tipo == TipoCuerpo.MIXTO

    fun aJson(): JSONObject = JSONObject().apply {
        put("ancho", anchoCm); put("tipo", tipo.name); put("entrepanos", entrepanos); put("cajones", cajones)
    }

    companion object {
        fun desdeJson(o: JSONObject): Cuerpo? = runCatching {
            Cuerpo(
                anchoCm = o.optDouble("ancho", 60.0).toFloat(),
                tipo = TipoCuerpo.valueOf(o.optString("tipo", "ENTREPANOS")),
                entrepanos = o.optInt("entrepanos", 0),
                cajones = o.optInt("cajones", 0)
            )
        }.getOrNull()
    }
}
