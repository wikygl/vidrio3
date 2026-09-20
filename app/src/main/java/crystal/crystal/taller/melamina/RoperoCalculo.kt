package crystal.crystal.taller.melamina

import kotlin.math.ceil
import kotlin.math.roundToInt

/** De qué plancha sale cada pieza. La plancha estándar de melamina y nordex es de 244 x 183. */
enum class MaterialPlancha(val etiqueta: String, val planchaAnchoMm: Int, val planchaAltoMm: Int) {
    MELAMINA_18("Melamina 18 mm", 2440, 1830),
    MELAMINA_15("Melamina 15 mm", 2440, 1830),
    NORDEX_3("Nordex 3 mm", 2440, 1830),
    MDF_55("MDF 5.5 mm", 2440, 1830)
}

/**
 * Una pieza de la lista de corte. [anchoMm] es lo que va a lo ancho del ropero (o del cajón) y
 * [altoMm] lo que va a lo alto o al fondo; en la plancha da igual, el optimizador las gira.
 * [tapacantoMm] es el canto visible de UNA pieza, que hay que cubrir.
 */
data class PiezaMelamina(
    val nombre: String,
    val anchoMm: Int,
    val altoMm: Int,
    val cantidad: Int,
    val material: MaterialPlancha,
    val tapacantoMm: Int = 0
) {
    val areaMm2: Long get() = anchoMm.toLong() * altoMm.toLong() * cantidad
    val medida: String get() = "${anchoMm}x${altoMm}"
}

/** Un accesorio con su cantidad; los que tienen largo (tubo, riel) lo llevan en cm. */
data class Accesorio(val nombre: String, val cantidad: Int, val largoCm: Float = 0f)

data class MaterialesRopero(
    val piezas: List<PiezaMelamina>,
    val accesorios: List<Accesorio>,
    /** Cantos a cubrir, en cm, con cuántos de cada largo. */
    val tapacanto: Map<Int, Int>,
    val planchasEstimadas: Map<MaterialPlancha, Int>,
    val referencias: String
) {
    val tapacantoTotalM: Float get() = tapacanto.entries.sumOf { (largo, n) -> largo.toDouble() * n }.toFloat() / 100f
    fun piezasDe(material: MaterialPlancha) = piezas.filter { it.material == material }

    /** Las líneas de una lista de material como las archiva el proyecto: `medida = cantidad`. */
    fun lineasDePiezas(material: MaterialPlancha): String =
        piezasDe(material).groupBy { it.medida }.entries
            .sortedByDescending { (_, ps) -> ps.first().areaMm2 / ps.first().cantidad }
            .joinToString("\n") { (medida, ps) -> "${medidaEnCm(medida)} = ${ps.sumOf { it.cantidad }}" }

    fun lineasDeTapacanto(): String =
        tapacanto.entries.sortedByDescending { it.key }.joinToString("\n") { (largo, n) -> "$largo = $n" }

    /** Los accesorios sin largo, sumados por nombre: los soportes de tres colgadores van en una fila. */
    fun lineasDeAccesorios(): String =
        accesorios.filter { it.largoCm <= 0f }.groupBy { it.nombre }.entries
            .joinToString("\n") { (nombre, xs) -> "$nombre = ${xs.sumOf { it.cantidad }}" }

    /** Los accesorios con largo (tubos, rieles corredizos), agrupados por nombre: `largo = cantidad`. */
    fun lineasConLargo(nombre: String): String =
        accesorios.filter { it.nombre == nombre && it.largoCm > 0f }
            .groupBy { it.largoCm.roundToInt() }.entries.sortedByDescending { it.key }
            .joinToString("\n") { (largo, xs) -> "$largo = ${xs.sumOf { it.cantidad }}" }

    fun nombresConLargo(): List<String> = accesorios.filter { it.largoCm > 0f }.map { it.nombre }.distinct()

    /** La lista de corte legible, pieza por pieza, en cm. */
    fun listaDeCorte(): String = piezas.joinToString("\n") { p ->
        "${p.cantidad} x ${p.nombre}: ${medidaEnCm(p.medida)} (${p.material.etiqueta})"
    }

    private fun medidaEnCm(medidaMm: String): String {
        val (a, b) = medidaMm.split("x").map { it.toInt() }
        return "${cm(a)}x${cm(b)}"
    }

    private fun cm(mm: Int): String {
        val v = mm / 10f
        return if (v == v.toInt().toFloat()) v.toInt().toString() else String.format(java.util.Locale.US, "%.1f", v)
    }
}

/**
 * Los materiales de un ropero de melamina: la lista de corte, los cantos, los accesorios y las
 * planchas que hacen falta.
 *
 * Las reglas son las del taller de melamina de siempre:
 * - Laterales de piso a techo; piso y techo entre laterales; zócalo de frente bajo el piso.
 * - Divisiones entre piso y techo, con el fondo del armazón.
 * - Entrepaños y cajones al fondo útil (con corredizas, menos el carril de las hojas).
 * - Cajones: frente falso 4 mm menor que el hueco por lado, caja 4 cm más baja que el frente y
 *   5 cm más corta que el fondo útil, con 13 mm por lado para los rieles, fondo de nordex.
 * - Puertas batientes superpuestas: una por cuerpo hasta 60 cm, dos si es más ancho, 3 mm de
 *   luz; bisagras según el alto. Las del maletero aparte.
 * - Corredizas por dentro del armazón: dos hojas hasta 240, tres más allá, montadas 5 cm.
 * - Tapacanto en todo canto que se ve: frentes del armazón, cantos de entrepaños, contorno de
 *   puertas y frentes de cajón, canto superior de las cajas.
 * - Planchas: el área de las piezas más un 15% de merma, a plancha entera; el optimizador de
 *   planchas da el corte real.
 */
object RoperoCalculo {

    private const val MERMA = 1.15f
    private const val LUZ_PUERTA_CM = 0.3f
    private const val MONTA_CORREDIZA_CM = 5f
    private const val RIEL_CAJON_CM = 1.3f
    private const val CAJA_MAS_BAJA_CM = 4f
    private const val CAJA_MAS_CORTA_CM = 5f
    private val RIELES_CAJON = listOf(30, 35, 40, 45, 50, 55)

    fun calcular(r: Ropero): MaterialesRopero {
        val e = r.espesorCm
        val mel = if (r.espesorMm <= 15) MaterialPlancha.MELAMINA_15 else MaterialPlancha.MELAMINA_18
        val fondoMat = if (r.espesorFondoMm >= 5f) MaterialPlancha.MDF_55 else MaterialPlancha.NORDEX_3
        val piezas = mutableListOf<PiezaMelamina>()
        val accesorios = mutableListOf<Accesorio>()
        val tapacanto = mutableMapOf<Int, Int>()
        fun canto(largoCm: Float, cantidad: Int) {
            val l = largoCm.roundToInt()
            if (l > 0 && cantidad > 0) tapacanto[l] = (tapacanto[l] ?: 0) + cantidad
        }
        fun pieza(nombre: String, anchoCm: Float, altoCm: Float, cantidad: Int, material: MaterialPlancha = mel, cantoCm: Float = 0f) {
            if (cantidad <= 0 || anchoCm <= 0f || altoCm <= 0f) return
            piezas.add(PiezaMelamina(nombre, mm(anchoCm), mm(altoCm), cantidad, material, mm(cantoCm)))
        }

        val fondoArm = r.fondoArmazonCm
        val fondoInt = r.fondoInteriorCm
        val wi = r.anchoInteriorCm
        val hi = r.altoInteriorCm
        val n = r.cuerpos.size

        // ---- Armazón ----
        // Cada lateral con el alto de su lado (bajo una escalera cada cuerpo tiene el suyo).
        val altoIzq = r.altoDeCuerpo(0)
        val altoDer = r.altoDeCuerpo(n - 1)
        pieza("Lateral", fondoArm, altoIzq, 1, cantoCm = altoIzq + fondoArm); canto(altoIzq, 1); canto(fondoArm, 1)
        pieza("Lateral", fondoArm, altoDer, 1, cantoCm = altoDer + fondoArm); canto(altoDer, 1); canto(fondoArm, 1)
        // Piso y zócalo van de lateral a lateral; si no caben en la plancha se parten en el centro
        // de una división, que es donde la unión no se ve y tiene dónde atornillarse.
        val tramosAncho = tramosDeAncho(r)
        if (r.altosDesiguales) {
            // Con altos distintos el techo va por cuerpos, cada trozo a su alto, hasta la mitad de
            // la división (y hasta el lateral en las puntas).
            tramosDeTechoPorCuerpo(r).forEach { w -> pieza("Techo", w, fondoArm, 1, cantoCm = w); canto(w, 1) }
        } else {
            tramosAncho.forEach { w -> pieza("Techo", w, fondoArm, 1, cantoCm = w); canto(w, 1) }
        }
        tramosAncho.forEach { w -> pieza("Piso", w, fondoArm, 1, cantoCm = w); canto(w, 1) }
        if (r.zocaloCm > 0.5f) tramosAncho.forEach { w -> pieza("Zócalo", w, r.zocaloCm, 1) }
        // Cada división sube hasta el techo más alto de los dos cuerpos que separa.
        for (i in 0 until n - 1) {
            val altoDiv = maxOf(r.altoDeCuerpo(i), r.altoDeCuerpo(i + 1)) - r.zocaloCm - 2 * e
            pieza("División", fondoArm, altoDiv, 1, cantoCm = altoDiv); canto(altoDiv, 1)
        }
        if (r.maleteroCm > 0f) {
            r.cuerpos.forEach { c -> pieza("Repisa maletero", c.anchoCm, fondoInt, 1, cantoCm = c.anchoCm); canto(c.anchoCm, 1) }
        }
        if (r.conFondo) {
            // El fondo va clavado atrás, del zócalo arriba. Si no cabe en la plancha (244 x 183)
            // se parte por cuerpos, cada trozo hasta la mitad de la división.
            val altoFondo = r.altoMayorCm - r.zocaloCm
            val cabe = minOf(r.anchoCm, altoFondo) <= 183f && maxOf(r.anchoCm, altoFondo) <= 244f
            if ((cabe || n == 1) && !r.altosDesiguales) pieza("Fondo", r.anchoCm, altoFondo, 1, fondoMat)
            else tramosDeFondo(r).forEachIndexed { i, w -> pieza("Fondo", w, r.altoDeCuerpo(i) - r.zocaloCm, 1, fondoMat) }
        }

        // ---- Lo de cada cuerpo ----
        var tornillos40 = 4 * (2 + (n - 1) + (if (r.maleteroCm > 0f) n else 0)) + 4
        var tornillos16 = if (r.conFondo) ceil(2 * (r.anchoCm + r.altoMayorCm) / 20f).toInt() else 0
        var tiradores = 0
        r.cuerpos.forEach { c ->
            val w = c.anchoCm
            val entrepanos = c.entrepanosEfectivos
            if (entrepanos > 0) { pieza("Entrepaño", w, fondoInt, entrepanos, cantoCm = w); canto(w, entrepanos); tornillos40 += 4 * entrepanos }
            if (c.llevaTubo) {
                accesorios.add(Accesorio("Tubo colgador", 1, largoCm = w))
                accesorios.add(Accesorio("Soporte de tubo", 2))
            }
            val altosCajones = c.altosDeCajones(r.altoCajonCm)
            val cajones = altosCajones.size
            if (cajones > 0) {
                // Cada cajón con su alto: el frente y la caja se cortan por cajón, y los iguales
                // se juntan al final en la lista.
                val frenteW = w - 0.4f
                val cajaProf = (fondoInt - CAJA_MAS_CORTA_CM).coerceAtLeast(20f)
                val cajaW = w - 2 * RIEL_CAJON_CM
                altosCajones.forEach { hc ->
                    val frenteH = hc - 0.4f
                    pieza("Frente cajón", frenteW, frenteH, 1, cantoCm = 2 * (frenteW + frenteH)); canto(frenteW, 2); canto(frenteH, 2)
                    val cajaH = (hc - CAJA_MAS_BAJA_CM).coerceAtLeast(6f)
                    pieza("Lateral cajón", cajaProf, cajaH, 2, cantoCm = cajaProf); canto(cajaProf, 2)
                    pieza("Frente y trasera de caja", cajaW - 2 * e, cajaH, 2, cantoCm = cajaW - 2 * e); canto(cajaW - 2 * e, 2)
                }
                pieza("Fondo cajón", cajaW, cajaProf, cajones, fondoMat)
                val riel = RIELES_CAJON.lastOrNull { it <= cajaProf } ?: RIELES_CAJON.first()
                accesorios.add(Accesorio("Riel de cajón $riel cm (par)", cajones))
                tiradores += cajones
                tornillos40 += 8 * cajones
                tornillos16 += 12 * cajones
            }
        }

        // ---- Puertas ----
        var bisagras = 0
        when (r.puertas) {
            TipoPuertas.SIN -> Unit
            TipoPuertas.BATIENTES -> {
                r.cuerpos.forEachIndexed { i, c ->
                    // Cada cuerpo tapa su hueco y media división a cada lado (o medio lateral), a su alto.
                    val luz = c.anchoCm + e
                    val hojas = hojasBatientes(c, e)
                    val ancho = luz / hojas - LUZ_PUERTA_CM
                    val altoCuerpo = r.altoDeCuerpo(i)
                    val altoBajo = (if (r.maleteroCm > 0f) altoCuerpo - r.zocaloCm - r.maleteroCm - e else altoCuerpo - r.zocaloCm) - LUZ_PUERTA_CM
                    pieza("Puerta", ancho, altoBajo, hojas, cantoCm = 2 * (ancho + altoBajo)); canto(ancho, 2 * hojas); canto(altoBajo, 2 * hojas)
                    bisagras += hojas * bisagrasPorAlto(altoBajo)
                    tiradores += hojas
                    if (r.maleteroCm > 0f) {
                        val altoMal = r.maleteroCm + e - LUZ_PUERTA_CM
                        pieza("Puerta maletero", ancho, altoMal, hojas, cantoCm = 2 * (ancho + altoMal)); canto(ancho, 2 * hojas); canto(altoMal, 2 * hojas)
                        bisagras += hojas * bisagrasPorAlto(altoMal)
                        tiradores += hojas
                    }
                }
            }
            TipoPuertas.CORREDIZAS -> {
                val hojas = hojasCorredizas(r)
                val ancho = (wi + (hojas - 1) * MONTA_CORREDIZA_CM) / hojas
                val alto = hi - 3.5f
                pieza("Hoja corrediza", ancho, alto, hojas, cantoCm = 2 * (ancho + alto)); canto(ancho, 2 * hojas); canto(alto, 2 * hojas)
                accesorios.add(Accesorio("Riel corredizo", 2, largoCm = wi))
                accesorios.add(Accesorio("Kit ruedas corredizas", hojas))
                accesorios.add(Accesorio("Tirador embutido", hojas))
                tornillos16 += 4 * ceil(wi / 30f).toInt()
            }
        }
        if (bisagras > 0) accesorios.add(Accesorio("Bisagra cangrejo 35 mm", bisagras))
        if (tiradores > 0) accesorios.add(Accesorio("Tirador", tiradores))
        accesorios.add(Accesorio("Tornillo 4x40 mm", tornillos40))
        if (tornillos16 > 0) accesorios.add(Accesorio("Tornillo 3x16 mm", tornillos16))
        if (r.zocaloCm > 0.5f) accesorios.add(Accesorio("Pata regulable", (n + 1) * 2))

        // ---- Planchas ----
        val planchas = MaterialPlancha.values().mapNotNull { m ->
            val area = piezas.filter { it.material == m }.sumOf { it.areaMm2 }
            if (area == 0L) null else m to ceil(area * MERMA / (m.planchaAnchoMm.toLong() * m.planchaAltoMm)).toInt().coerceAtLeast(1)
        }.toMap()

        // Las piezas iguales (mismo nombre, medida y material) van juntas en la lista.
        val juntas = piezas.groupBy { Triple(it.nombre, it.medida, it.material) }.values
            .map { grupo -> grupo.first().copy(cantidad = grupo.sumOf { it.cantidad }) }
        val tapacantoM = tapacanto.entries.sumOf { (l, c) -> l.toDouble() * c } / 100.0
        val largas = juntas.filter { maxOf(it.anchoMm, it.altoMm) > LARGO_PLANCHA_MM }.map { it.nombre }.distinct()
        val referencias = buildString {
            append("Ropero empotrado ${fmt(r.anchoCm)} x ${fmt(r.altoCm)} x ${fmt(r.fondoCm)} cm, melamina ${r.espesorMm} mm\n")
            append("Cuerpos: ").append(r.cuerpos.joinToString(", ") { "${fmt(it.anchoCm)} ${it.tipo.etiqueta.lowercase()}" }).append('\n')
            append("Zócalo ${fmt(r.zocaloCm)}").append(if (r.maleteroCm > 0f) ", maletero ${fmt(r.maleteroCm)}" else "").append('\n')
            append("Puertas: ${r.puertas.etiqueta.lowercase()}")
            if (r.puertas == TipoPuertas.CORREDIZAS) append(" (${hojasCorredizas(r)} hojas)")
            append('\n')
            if (r.altosDesiguales) {
                append("Altos por lado: ").append(r.cuerpos.indices.joinToString(", ") { fmt(r.altoDeCuerpo(it)) }).append('\n')
                if (r.puertas == TipoPuertas.CORREDIZAS) append("OJO: corredizas con altos distintos: las hojas van al alto general; revisar.\n")
            }
            planchas.forEach { (m, c) -> append("${m.etiqueta}: $c plancha${if (c == 1) "" else "s"} de 244x183 (estimado con 15% de merma)\n") }
            append("Tapacanto ${anchoTapacantoMm(r)} x ${fmt(r.tapacantoGrosorMm)} mm: ${String.format(java.util.Locale.US, "%.1f", tapacantoM)} m")
            if (largas.isNotEmpty()) append("\nOJO: ${largas.joinToString(", ")} pasan de 244: van en plancha larga (275) o partidas.")
        }
        return MaterialesRopero(juntas, accesorios, tapacanto, planchas, referencias)
    }

    /** El largo de la plancha de siempre, en mm. */
    const val LARGO_PLANCHA_MM = 2440

    /**
     * Los anchos de las piezas que van de lateral a lateral (techo, piso, zócalo): una sola si el
     * interior cabe en la plancha; si no, trozos que se parten en el centro de una división,
     * juntando cuerpos de izquierda a derecha mientras quepan en 244.
     */
    fun tramosDeAncho(r: Ropero): List<Float> {
        val wi = r.anchoInteriorCm
        val largo = LARGO_PLANCHA_MM / 10f
        if (wi <= largo || r.cuerpos.size < 2) return listOf(wi)
        val e = r.espesorCm
        // Los cortes posibles: el centro de cada división.
        val cortes = mutableListOf<Float>()
        var pos = 0f
        r.cuerpos.dropLast(1).forEach { c -> pos += c.anchoCm + e; cortes.add(pos - e / 2f) }
        val tramos = mutableListOf<Float>()
        var desde = 0f
        var i = 0
        while (i < cortes.size) {
            // Avanza hasta el último corte que todavía cabe desde [desde].
            var hasta = cortes[i]
            while (i + 1 < cortes.size && cortes[i + 1] - desde <= largo) { i++; hasta = cortes[i] }
            if (wi - desde <= largo) break
            tramos.add(hasta - desde)
            desde = hasta
            i++
        }
        tramos.add(wi - desde)
        return tramos
    }

    /**
     * Los trozos del fondo cuando no cabe entero: se parte en el centro de cada división, y los
     * de las puntas llevan además el lateral, así que entre todos suman el ancho del ropero.
     */
    fun tramosDeFondo(r: Ropero): List<Float> {
        val e = r.espesorCm
        val n = r.cuerpos.size
        if (n < 2) return listOf(r.anchoCm)
        // A cada lado del cuerpo: el lateral entero si es una punta, media división si no.
        return r.cuerpos.mapIndexed { i, c ->
            c.anchoCm + (if (i == 0) e else e / 2f) + (if (i == n - 1) e else e / 2f)
        }
    }

    /**
     * El techo por cuerpos (cuando cada lado tiene su alto): cada trozo va entre laterales o
     * hasta la mitad de la división, así que entre todos suman el ancho interior.
     */
    fun tramosDeTechoPorCuerpo(r: Ropero): List<Float> {
        val e = r.espesorCm
        val n = r.cuerpos.size
        return r.cuerpos.mapIndexed { i, c ->
            c.anchoCm + (if (i == 0) 0f else e / 2f) + (if (i == n - 1) 0f else e / 2f)
        }
    }

    fun hojasCorredizas(anchoCm: Float): Int = if (anchoCm <= 240f) 2 else ceil(anchoCm / 120f).toInt().coerceAtLeast(3)

    /** Las hojas corredizas del ropero: las que se pidieron a mano, o las que tocan por el ancho. */
    fun hojasCorredizas(r: Ropero): Int = if (r.hojasCorredizas in 2..6) r.hojasCorredizas else hojasCorredizas(r.anchoCm)

    /** Las hojas batientes de un cuerpo: las pedidas a mano, o 1 hasta 60 cm de luz y 2 si es más ancho. */
    fun hojasBatientes(c: Cuerpo, espesorCm: Float): Int =
        if (c.hojasBatientes in 1..2) c.hojasBatientes else if (c.anchoCm + espesorCm <= 60f) 1 else 2

    /** El ancho del tapacanto que cubre el canto del tablero: 22 para 18 mm, 19 para 15. */
    fun anchoTapacantoMm(r: Ropero): Int = if (r.espesorMm <= 15) 19 else 22

    /** El nombre de la lista de tapacanto, con su ancho y su grosor: "Tapacanto 22 x 0.45 mm". */
    fun nombreTapacanto(r: Ropero): String = "Tapacanto ${anchoTapacantoMm(r)} x ${fmt(r.tapacantoGrosorMm)} mm"

    fun bisagrasPorAlto(altoCm: Float): Int = when {
        altoCm <= 90f -> 2
        altoCm <= 150f -> 3
        altoCm <= 200f -> 4
        else -> 5
    }

    private fun mm(cm: Float): Int = (cm * 10f).roundToInt()

    private fun fmt(v: Float): String =
        if (v == v.toInt().toFloat()) v.toInt().toString() else String.format(java.util.Locale.US, "%.1f", v)
}
