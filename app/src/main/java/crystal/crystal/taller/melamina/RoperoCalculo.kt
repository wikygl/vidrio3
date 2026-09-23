package crystal.crystal.taller.melamina

import kotlin.math.ceil
import kotlin.math.roundToInt

/** De qué plancha sale cada pieza. La plancha estándar de melamina y nordex es de 244 x 183. */
enum class MaterialPlancha(val etiqueta: String, val planchaAnchoMm: Int, val planchaAltoMm: Int) {
    MELAMINA_18("Melamina 18 mm", 2440, 1830),
    MELAMINA_15("Melamina 15 mm", 2440, 1830),
    /** La de color: solo lo que se ve sin abrir (puertas, frentes a la vista, laterales, zócalo), cuando el interior va en blanco. */
    MELAMINA_18_COLOR("Melamina 18 mm de color", 2440, 1830),
    MELAMINA_15_COLOR("Melamina 15 mm de color", 2440, 1830),
    /** El fondo de 3 mm (del mueble y de los cajones): según la zona se le dice MDF o nordex. */
    NORDEX_3("MDF o nordex 3 mm", 2440, 1830),
    MDF_55("MDF 5.5 mm", 2440, 1830)
}

/**
 * Una pieza de la lista de corte, siempre ancho x alto: [anchoMm] es lo que sigue al ancho del
 * ropero (o del cajón) y, en las piezas con fondo, el fondo (un lateral: 57.9 x 230); [altoMm] lo
 * que sigue al alto (o al fondo, en las echadas: piso, repisas). Así el optimizador de planchas
 * puede fijar la veta a lo alto o a lo ancho de cada pieza.
 * [tapacantoMm] es el canto visible de UNA pieza, que hay que cubrir.
 */
data class PiezaMelamina(
    val nombre: String,
    val anchoMm: Int,
    val altoMm: Int,
    val cantidad: Int,
    val material: MaterialPlancha,
    val tapacantoMm: Int = 0,
    /** Cuántos cantos lleva a lo largo del ancho (0, 1 o 2) y a lo largo del alto: los que pinta el despiece. */
    val cantosEnAncho: Int = 0,
    val cantosEnAlto: Int = 0,
    /** De qué tapacanto son sus cantos. */
    val canto: TipoCanto = TipoCanto.FINO,
    /** La ranura para el fondo del cajón: a cuántos cm del canto de abajo empieza (0 = sin ranura); corre a lo largo del ancho. */
    val ranuraCm: Float = 0f,
    /** De dónde es la pieza: "armazón", "cuerpo 2", "cuerpo 2, col 1"… (las iguales juntas llevan todas las suyas). */
    val zonas: List<String> = emptyList()
) {
    val areaMm2: Long get() = anchoMm.toLong() * altoMm.toLong() * cantidad
    val medida: String get() = "${anchoMm}x${altoMm}"
}

/** El tapacanto de una pieza: el fino de dentro, el fino de color (laterales) o el grueso de lo que se ve. */
enum class TipoCanto(val etiqueta: String) { FINO("fino"), FINO_COLOR("fino de color"), GRUESO("grueso") }

/** Un accesorio con su cantidad; los que tienen largo (tubo, riel) lo llevan en cm. */
data class Accesorio(val nombre: String, val cantidad: Int, val largoCm: Float = 0f)

data class MaterialesRopero(
    val piezas: List<PiezaMelamina>,
    val accesorios: List<Accesorio>,
    /** Cantos de dentro a cubrir (tapacanto fino), en cm, con cuántos de cada largo. */
    val tapacanto: Map<Int, Int>,
    /** Cantos de lo que se ve (puertas, zócalo, frentes a la vista): el tapacanto grueso. */
    val tapacantoPuertas: Map<Int, Int> = emptyMap(),
    val planchasEstimadas: Map<MaterialPlancha, Int>,
    val referencias: String,
    /** Cantos finos de las piezas de dentro que se ven por fuera (los laterales): el fino, del color de fuera. */
    val tapacantoColor: Map<Int, Int> = emptyMap()
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

    fun lineasDeTapacantoColor(): String =
        tapacantoColor.entries.sortedByDescending { it.key }.joinToString("\n") { (largo, n) -> "$largo = $n" }

    fun lineasDeTapacantoPuertas(): String =
        tapacantoPuertas.entries.sortedByDescending { it.key }.joinToString("\n") { (largo, n) -> "$largo = $n" }

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
 * - Laterales de suelo a techo; piso y techo entre laterales. El zócalo se mide del suelo a la
 *   cara de arriba del piso: el piso es parte del zócalo (zócalo 7: piso de 5.2 a 7).
 * - Divisiones entre piso y techo, con el fondo del armazón.
 * - Entrepaños y cajones al fondo útil (con corredizas, menos el carril de las hojas).
 * - Si piso, techo, repisa del maletero o zócalo pasan de 180 (el ancho de la plancha chica menos
 *   lo de escuadrar), una división lo más centrada posible va del suelo
 *   arriba como un lateral (pasante) y los trozos llegan a su cara: dos piezas nunca se juntan a tope.
 * - Laterales y divisiones con el fondo del hueco menos el nordex y, con batientes encima del
 *   armazón, menos la puerta: el mueble con sus puertas mide el fondo apuntado.
 * - Cajones: frente falso 4 mm menor que el hueco por lado; la caja, del alto escrito en el
 *   diseño y 5 cm más corta que el fondo útil, con 13 mm por lado para los rieles, fondo de nordex.
 * - Puertas batientes superpuestas: tapan el lateral entero en las puntas y media división entre
 *   cuerpos; una por cuerpo hasta 60 cm, dos si es más ancho, 3 mm de
 *   luz; bisagras según el alto. Las del maletero aparte (por compartimento del maletero si
 *   tiene los suyos). Con los cajones a la vista, la puerta arranca sobre la tapa de los
 *   cajones y cada frente va en el plano de las puertas, con su gruña.
 * - El zócalo va en el plano de las puertas, del color de fuera: de punta a punta (tapa los
 *   laterales, como las puertas) y hasta medio piso, menos el canto grueso y una gruña de unos
 *   2 mm, al medio centímetro hacia abajo (zócalo 7 con canto 3 = melamina 5.5). Metido, también
 *   de color: se ve entero desde fuera.
 * - Los laterales, de color por su cara de fuera, con su canto fino también de color.
 * - Corredizas por dentro del armazón: dos hojas hasta 240, tres más allá, montadas 5 cm.
 * - Tapacanto en todo canto que se ve: frentes del armazón, cantos de entrepaños, contorno de
 *   puertas y frentes de cajón, canto superior de las cajas. Lo de dentro con el fino (0.45)
 *   y lo de fuera con el grueso (3). El tapacanto SE DESCUENTA de la pieza: una puerta de
 *   100 x 50 canteada a la vuelta con 3 mm se corta a 99.4 x 49.4.
 * - Planchas: el área de las piezas más un 15% de merma, a plancha entera; el optimizador de
 *   planchas da el corte real.
 */
object RoperoCalculo {

    private const val MERMA = 1.15f
    private const val RIEL_CAJON_CM = 1.3f
    private const val CAJA_MAS_CORTA_CM = 5f
    /** Del canto de abajo de la caja a donde empieza la ranura del fondo. */
    const val RANURA_CAJON_CM = 1.8f
    /** Lo que el fondo del cajón se mete en la ranura por cada lado: 8 mm de ranura menos 1 de juego. */
    private const val EN_RANURA_CM = 0.7f
    /** Desde este ancho de dentro, el fondo del cajón lleva refuerzo al centro. */
    private const val ANCHO_CON_REFUERZO_CM = 90f
    /** La gruña mínima entre el zócalo y las puertas: la de 1.5 mm se redondea a 2. */
    private const val GRUNA_ZOCALO_CM = 0.15f
    private val RIELES_CAJON = listOf(30, 35, 40, 45, 50, 55)

    /** La melamina del armazón y lo de dentro (blanca si el interior va en blanco). */
    fun melaminaInterior(r: Ropero): MaterialPlancha = if (r.espesorMm <= 15) MaterialPlancha.MELAMINA_15 else MaterialPlancha.MELAMINA_18

    /** La melamina de lo que se ve sin abrir: la de color si el interior va en blanco; si no, la misma. */
    fun melaminaVisible(r: Ropero): MaterialPlancha = when {
        !r.interiorBlanco -> melaminaInterior(r)
        r.espesorMm <= 15 -> MaterialPlancha.MELAMINA_15_COLOR
        else -> MaterialPlancha.MELAMINA_18_COLOR
    }

    fun calcular(r: Ropero): MaterialesRopero {
        val e = r.espesorCm
        val mel = melaminaInterior(r)
        val melColor = melaminaVisible(r)
        val fondoMat = if (r.espesorFondoMm >= 5f) MaterialPlancha.MDF_55 else MaterialPlancha.NORDEX_3
        val piezas = mutableListOf<PiezaMelamina>()
        val accesorios = mutableListOf<Accesorio>()
        val tapacanto = mutableMapOf<Int, Int>()
        val tapacantoPuertas = mutableMapOf<Int, Int>()
        val tapacantoColor = mutableMapOf<Int, Int>()
        /** El canto va a su lista: grueso si es de lo que se ve, fino de color si la pieza es de color (los laterales), fino si no. */
        fun canto(largoCm: Float, cantidad: Int, visible: Boolean = false, deColor: Boolean = false) {
            val l = largoCm.roundToInt()
            val mapa = when { visible -> tapacantoPuertas; deColor -> tapacantoColor; else -> tapacanto }
            if (l > 0 && cantidad > 0) mapa[l] = (mapa[l] ?: 0) + cantidad
        }
        /** De dónde es lo que se va apuntando: el armazón, un cuerpo, una columna, las puertas. */
        var zona = "armazón"
        /**
         * Una pieza por su medida de sitio ([anchoCm] x [altoCm]) y qué cantos lleva:
         * [cantosEnAncho] los cantos a lo largo del ancho (arriba y abajo: descuentan del alto),
         * [cantosEnAlto] los de los lados (descuentan del ancho). Lo [visible] lleva el canto
         * grueso. Se apunta la pieza ya descontada y los cantos con su largo de corte.
         */
        fun pieza(
            nombre: String, anchoCm: Float, altoCm: Float, cantidad: Int, material: MaterialPlancha = mel,
            cantosEnAncho: Int = 0, cantosEnAlto: Int = 0, visible: Boolean = false, ranuraCm: Float = 0f
        ) {
            if (cantidad <= 0 || anchoCm <= 0f || altoCm <= 0f) return
            val t = if (visible) r.tapacantoPuertasCm else r.tapacantoCm
            val w = anchoCm - cantosEnAlto * t
            val h = altoCm - cantosEnAncho * t
            val cantoCm = cantosEnAncho * w + cantosEnAlto * h
            // Lo visible va en la melamina de color (si el interior es blanco); el resto, en la de dentro.
            val mat = if (visible && material == mel) melColor else material
            // Una pieza de color que no es puerta (un lateral): su canto fino, también de color.
            val deColor = !visible && mat == melColor && melColor != mel
            val tipoCanto = when { visible -> TipoCanto.GRUESO; deColor -> TipoCanto.FINO_COLOR; else -> TipoCanto.FINO }
            piezas.add(PiezaMelamina(nombre, mm(w), mm(h), cantidad, mat, mm(cantoCm), cantosEnAncho, cantosEnAlto, tipoCanto, ranuraCm, listOf(zona)))
            canto(w, cantosEnAncho * cantidad, visible, deColor)
            canto(h, cantosEnAlto * cantidad, visible, deColor)
        }

        val fondoArm = r.fondoArmazonCm
        val fondoInt = r.fondoInteriorCm
        val n = r.cuerpos.size

        // ---- Armazón ----
        // Cada lateral con el alto de su lado (bajo una escalera cada cuerpo tiene el suyo).
        val altoIzq = r.altoDeCuerpo(0)
        val altoDer = r.altoDeCuerpo(n - 1)
        // Los laterales se ven por fuera: van en la melamina de color aunque el interior sea blanco.
        pieza("Lateral", fondoArm, altoIzq, 1, melColor, cantosEnAncho = 1, cantosEnAlto = 1)
        pieza("Lateral", fondoArm, altoDer, 1, melColor, cantosEnAncho = 1, cantosEnAlto = 1)
        // Piso y techo van de lateral a lateral. Si no caben en la plancha, una división va del
        // suelo arriba como un lateral (pasante) y cada trozo llega a su cara: nunca se juntan
        // dos piezas a tope, una se atornilla en la otra.
        val tramosAncho = tramosDeAncho(r)
        val pasantes = RoperoGeometria.divisionesPasantes(r)
        RoperoGeometria.tramosDeTecho(r).forEach { t -> pieza("Techo", t.x1 - t.x0, fondoArm, 1, cantosEnAncho = 1) }
        tramosAncho.forEach { w -> pieza("Piso", w, fondoArm, 1, cantosEnAncho = 1) }
        // El zócalo va delante, en el plano de las puertas, con el canto grueso arriba; la pieza
        // se pide con el canto puesto para que quede la melamina de altoZocalo.
        if (r.zocaloCm > 0.5f) {
            if (r.zocaloDelante) tramosDeZocalo(r).forEach { w -> pieza("Zócalo", w, altoZocalo(r) + r.tapacantoPuertasCm, 1, cantosEnAncho = 1, visible = true) }
            // Metido bajo el piso, entre laterales (el zócalo menos el piso): aunque vaya dentro se ve
            // entero desde fuera, así que va de color.
            else if (r.bajoPisoCm > 0.5f) tramosAncho.forEach { w -> pieza("Zócalo", w, r.bajoPisoCm, 1, melColor) }
        }
        // Cada división sube hasta el techo más alto de los dos cuerpos que separa; con el
        // maletero propio, solo hasta su repisa.
        // Las pasantes, del suelo arriba como los laterales (y cruzando el maletero).
        for (i in 0 until n - 1) {
            val altoMayor = maxOf(r.altoDeCuerpo(i), r.altoDeCuerpo(i + 1))
            if (i in pasantes) { pieza("División pasante", fondoArm, altoMayor, 1, cantosEnAncho = 1, cantosEnAlto = 1); continue }
            val altoDiv = if (r.maleteroPropio) r.altoBajoCm else altoMayor - r.pisoArribaCm - e
            pieza("División", fondoArm, altoDiv, 1, cantosEnAlto = 1)
        }
        if (r.maleteroPropio) {
            tramosAncho.forEach { w -> pieza("Repisa maletero", w, fondoArm, 1, cantosEnAncho = 1) }
            // Las del maletero que no son una pasante.
            pieza("División maletero", fondoInt, r.maleteroCm, RoperoGeometria.maleterosX(r).size - 1 - pasantes.size, cantosEnAlto = 1)
        } else if (r.maleteroCm > 0f) {
            r.cuerpos.forEach { c -> pieza("Repisa maletero", c.anchoCm, fondoInt, 1, cantosEnAncho = 1) }
        }
        if (r.conFondo) {
            // El fondo va clavado atrás, del zócalo arriba. Si no cabe en la plancha (244 x 183)
            // se parte por cuerpos, cada trozo hasta la mitad de la división.
            val altoFondo = r.altoMayorCm - r.bajoPisoCm   // de la cara de abajo del piso arriba
            val cabe = minOf(r.anchoCm, altoFondo) <= 183f && maxOf(r.anchoCm, altoFondo) <= 244f
            if ((cabe || n == 1) && !r.altosDesiguales) pieza("Fondo", r.anchoCm, altoFondo, 1, fondoMat)
            else tramosDeFondo(r).forEachIndexed { i, w -> pieza("Fondo", w, r.altoDeCuerpo(i) - r.bajoPisoCm, 1, fondoMat) }
        }

        // ---- Lo de cada cuerpo ----
        // Cada pasante suma un trozo de piso y de techo (y de repisa del maletero), con sus 4 tornillos.
        var tornillos40 = 4 * (2 + (n - 1) + (if (r.maleteroPropio) n + r.maleteroCuerpos - 1 else if (r.maleteroCm > 0f) n else 0)) + 4 +
            4 * pasantes.size * (if (r.maleteroPropio) 3 else 2)
        var tornillos16 = if (r.conFondo) ceil(2 * (r.anchoCm + r.altoMayorCm) / 20f).toInt() else 0
        var tiradores = 0

        /** Lo de dentro de un hueco: repisas, tubo, cajones con su tapa, y las columnas de los casilleros partidos. */
        fun piezasDelHueco(c: Cuerpo, h: Hueco, fondoUtil: Float, zonaHueco: String) {
            zona = zonaHueco
            val w = h.ancho
            val entrepanos = c.entrepanosEfectivos
            if (entrepanos > 0) { pieza("Entrepaño", w, fondoUtil, entrepanos, cantosEnAncho = 1); tornillos40 += 4 * entrepanos }
            if (c.llevaTubo) {
                accesorios.add(Accesorio("Tubo colgador", 1, largoCm = w))
                accesorios.add(Accesorio("Soporte de tubo", 2))
            }
            // El frente de cada cajón ocupa su tramo del espacio; la caja es la que se escribió (más baja si sobra sitio).
            val altosCajones = RoperoGeometria.altosDeCajones(r, c, h)
            val altosCajas = RoperoGeometria.altosDeCajas(r, c, h)
            val cajones = altosCajones.size
            if (cajones > 0) {
                // Cada cajón con su alto: el frente y la caja se cortan por cajón, y los iguales
                // se juntan al final en la lista.
                val cajaProf = (fondoUtil - CAJA_MAS_CORTA_CM).coerceAtLeast(20f)
                // El cajón interior (tras la puerta) pierde además dos melaminas para el riel telescópico.
                val cajaW = w - 2 * RIEL_CAJON_CM - (if (c.cajonesALaVista) 0f else 2 * e)
                altosCajas.forEach { hc ->
                    // El alto de la caja es el que se escribió en el diseño, sin descontar nada más.
                    val cajaH = hc.coerceAtLeast(6f)
                    // Las cuatro piezas de la caja llevan la ranura del fondo a 1.8 del canto de abajo:
                    // debajo queda sitio para un refuerzo de melamina que no se ve.
                    pieza("Lateral cajón", cajaProf, cajaH, 2, cantosEnAncho = 1, ranuraCm = RANURA_CAJON_CM)
                    pieza("Frente y trasera de caja", cajaW - 2 * e, cajaH, 2, cantosEnAncho = 1, ranuraCm = RANURA_CAJON_CM)
                }
                // Los frentes a la vista van con las puertas (RoperoPuertas); los interiores, aquí.
                if (!c.cajonesALaVista) {
                    val frenteW = w - 0.4f
                    altosCajones.forEach { hc -> pieza("Frente cajón", frenteW, hc - 0.4f, 1, cantosEnAncho = 2, cantosEnAlto = 2) }
                    tiradores += cajones
                }
                // El fondo, de MDF o nordex de 3, entra en la ranura de las cuatro piezas: lo de dentro de la
                // caja más lo que se mete en la ranura por cada lado (con un milímetro de juego).
                val dentroW = cajaW - 2 * e
                val dentroF = cajaProf - 2 * e
                pieza("Fondo cajón", dentroW + 2 * EN_RANURA_CM, dentroF + 2 * EN_RANURA_CM, cajones, MaterialPlancha.NORDEX_3)
                // En los cajones anchos el MDF se cuelga: un refuerzo de melamina de canto, al centro,
                // de frente a trasera, bajo el fondo (el hueco de 1.8 bajo la ranura).
                if (dentroW >= ANCHO_CON_REFUERZO_CM) pieza("Refuerzo fondo cajón", dentroF, RANURA_CAJON_CM, cajones)
                // La tapa sobre los cajones, que los separa del colgador o del hueco de arriba (si la llevan).
                if (RoperoGeometria.llevaTapa(r, c, h)) { pieza("Tapa de cajones", w, fondoUtil, 1, cantosEnAncho = 1); tornillos40 += 4 }
                val riel = RIELES_CAJON.lastOrNull { it <= cajaProf } ?: RIELES_CAJON.first()
                accesorios.add(Accesorio("Riel de cajón $riel cm (par)", cajones))
                tornillos40 += 8 * cajones
                tornillos16 += 12 * cajones
            }
            // Los casilleros partidos en columnas: una división por junta, del alto del casillero, y lo de cada columna.
            RoperoGeometria.casilleros(r, c, h).forEachIndexed { k, hk ->
                val columnas = RoperoGeometria.columnasDeCasillero(r, c, hk, k)
                if (columnas.size > 1) { pieza("División de casillero", fondoUtil, hk.alto, columnas.size - 1, cantosEnAlto = 1); tornillos40 += 4 * (columnas.size - 1) }
                columnas.forEachIndexed { j, hj -> piezasDelHueco(c.columnasDe(k)[j], hj, fondoUtil, "$zonaHueco, casillero ${k + 1} col ${j + 1}") }
                zona = zonaHueco
            }
        }
        r.cuerpos.forEachIndexed { i, c ->
            // Con corredizas propias del cuerpo, lo de dentro pierde el carril de las hojas.
            val fondoUtil = if (r.puertas != TipoPuertas.CORREDIZAS && (c.puertasPropias ?: r.puertas) == TipoPuertas.CORREDIZAS) fondoArm - Ropero.CARRIL_CORREDIZAS_CM else fondoInt
            piezasDelHueco(c, RoperoGeometria.huecoDeCuerpo(r, i), fondoUtil, "cuerpo ${i + 1}")
        }

        // ---- Puertas: donde diga RoperoPuertas, que es lo mismo que se pinta ----
        var bisagras = 0
        val puertas = RoperoPuertas.de(r)
        puertas.hojas.forEach { hoja ->
            zona = zonaDeX(r, (hoja.x0 + hoja.x1) / 2f)
            pieza(hoja.clase.nombre, hoja.ancho, hoja.alto, 1, cantosEnAncho = 2, cantosEnAlto = 2, visible = true)
            when (hoja.clase) {
                ClaseDePuerta.PUERTA, ClaseDePuerta.PUERTA_MALETERO -> { bisagras += RoperoPuertas.bisagrasPorAlto(hoja.alto); tiradores += 1 }
                ClaseDePuerta.FRENTE_CAJON -> tiradores += 1
                ClaseDePuerta.HOJA_CORREDIZA -> Unit
            }
        }
        val corredizas = puertas.hojas.count { it.clase == ClaseDePuerta.HOJA_CORREDIZA }
        if (corredizas > 0) {
            puertas.rielesCm.forEach { largo -> accesorios.add(Accesorio("Riel corredizo", 1, largoCm = largo)) }
            accesorios.add(Accesorio("Kit ruedas corredizas", corredizas))
            accesorios.add(Accesorio("Tirador embutido", corredizas))
            tornillos16 += 4 * ceil(puertas.rielesCm.sum() / 2f / 30f).toInt()
        }
        if (bisagras > 0) accesorios.add(Accesorio("Bisagra cangrejo 35 mm", bisagras))
        if (tiradores > 0) accesorios.add(Accesorio("Tirador", tiradores))
        accesorios.add(Accesorio("Tornillo 4x40 mm", tornillos40))
        if (tornillos16 > 0) accesorios.add(Accesorio("Tornillo 3x16 mm", tornillos16))
        if (r.bajoPisoCm > 0.5f) accesorios.add(Accesorio("Pata regulable", (n + 1) * 2))

        // ---- Planchas ----
        val planchas = MaterialPlancha.values().mapNotNull { m ->
            val area = piezas.filter { it.material == m }.sumOf { it.areaMm2 }
            if (area == 0L) null else m to ceil(area * MERMA / (m.planchaAnchoMm.toLong() * m.planchaAltoMm)).toInt().coerceAtLeast(1)
        }.toMap()

        // Las piezas iguales (mismo nombre, medida y material) van juntas en la lista.
        val juntas = piezas.groupBy { Triple(it.nombre, it.medida, it.material) }.values
            .map { grupo -> grupo.first().copy(cantidad = grupo.sumOf { it.cantidad }, zonas = grupo.flatMap { it.zonas }.distinct()) }
        val tapacantoM = tapacanto.entries.sumOf { (l, c) -> l.toDouble() * c } / 100.0
        val tapacantoPuertasM = tapacantoPuertas.entries.sumOf { (l, c) -> l.toDouble() * c } / 100.0
        val tapacantoColorM = tapacantoColor.entries.sumOf { (l, c) -> l.toDouble() * c } / 100.0
        val largas = juntas.filter { maxOf(it.anchoMm, it.altoMm) > LARGO_PLANCHA_MM }.map { it.nombre }.distinct()
        val referencias = buildString {
            append("Ropero empotrado ${fmt(r.anchoCm)} x ${fmt(r.altoCm)} x ${fmt(r.fondoCm)} cm, melamina ${r.espesorMm} mm")
            append(if (r.interiorBlanco) " (interior en blanco, lo visible de color)\n" else " (todo del mismo color)\n")
            append("Cuerpos: ").append(r.cuerpos.joinToString(", ") { "${fmt(it.anchoCm)} ${it.tipo.etiqueta.lowercase()}" }).append('\n')
            append(if (r.zocaloDelante) "Zócalo ${fmt(r.zocaloCm)} delante hasta medio piso (melamina ${fmt(altoZocalo(r))} + canto)" else "Zócalo ${fmt(r.zocaloCm)} metido (${fmt(r.bajoPisoCm)} bajo el piso)")
            if (r.maleteroCm > 0f) append(", maletero ${fmt(r.maleteroCm)}").append(if (r.maleteroPropio) " en ${r.maleteroCuerpos}" else "")
            append('\n')
            append("Puertas: ${r.puertas.etiqueta.lowercase()}")
            if (r.puertas == TipoPuertas.BATIENTES) append(if (r.puertasInteriores) " interiores" else " frontales")
            if (r.puertas == TipoPuertas.CORREDIZAS) append(" (${hojasCorredizas(r)} hojas)")
            append('\n')
            if (r.altosDesiguales) {
                append("Altos por lado: ").append(r.cuerpos.indices.joinToString(", ") { fmt(r.altoDeCuerpo(it)) }).append('\n')
                if (r.puertas == TipoPuertas.CORREDIZAS) append("OJO: corredizas con altos distintos: las hojas van al alto general; revisar.\n")
            }
            planchas.forEach { (m, c) -> append("${m.etiqueta}: $c plancha${if (c == 1) "" else "s"} de 244x183 (estimado con 15% de merma)\n") }
            append("${nombreTapacanto(r)}: ${String.format(java.util.Locale.US, "%.1f", tapacantoM)} m")
            if (tapacantoColorM > 0.0) append("; ${nombreTapacantoColor(r)}: ${String.format(java.util.Locale.US, "%.1f", tapacantoColorM)} m")
            if (tapacantoPuertasM > 0.0) append("; ${nombreTapacantoPuertas(r)}: ${String.format(java.util.Locale.US, "%.1f", tapacantoPuertasM)} m")
            append("\nLas piezas van con el tapacanto descontado.")
            if (largas.isNotEmpty()) append("\nOJO: ${largas.joinToString(", ")} pasan de 244: van en plancha larga (275) o partidas.")
        }
        return MaterialesRopero(juntas, accesorios, tapacanto, tapacantoPuertas, planchas, referencias, tapacantoColor)
    }

    /**
     * El alto de la melamina del zócalo de delante: llega a medio piso (el piso es parte del zócalo;
     * la otra mitad la tapan las puertas y los frentes, que topan en él), menos el canto grueso y
     * una gruña de al menos 1.5 mm, al medio centímetro hacia abajo. Zócalo 7 con melamina 18 y
     * canto 3: 7 - 0.9 - 0.3 - 0.15 = 5.65 → 5.5; con el canto, 5.8, y la puerta arranca en 6.1.
     */
    fun altoZocalo(r: Ropero): Float =
        (kotlin.math.floor((r.pisoArribaCm - r.espesorCm / 2f - r.tapacantoPuertasCm - GRUNA_ZOCALO_CM) * 2f) / 2f).coerceAtLeast(2f)

    /**
     * El zócalo en el plano de las puertas va de punta a punta del mueble (tapa los laterales,
     * como ellas); si no cabe en la plancha se parte en el centro de las divisiones pasantes, en
     * cuyo canto se atornillan los dos trozos.
     */
    fun tramosDeZocalo(r: Ropero): List<Float> {
        val e = r.espesorCm
        val xs = RoperoGeometria.cuerposX(r)
        val bordes = listOf(0f) + RoperoGeometria.divisionesPasantes(r).map { xs[it].second + e / 2f } + r.anchoCm
        return bordes.zipWithNext { a, b -> b - a }
    }

    /** El largo de la plancha de siempre, en mm. */
    const val LARGO_PLANCHA_MM = 2440

    /**
     * Los anchos del piso (y de la repisa del maletero propio): uno solo si el interior cabe en
     * la plancha; si no, trozos de lateral a división pasante ([RoperoGeometria.divisionesPasantes]).
     */
    fun tramosDeAncho(r: Ropero): List<Float> = RoperoGeometria.tramosDePiso(r).map { (x0, x1) -> x1 - x0 }

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

    /** El cuerpo en el que cae la x (de una puerta, por su centro): "cuerpo 2". */
    fun zonaDeX(r: Ropero, x: Float): String {
        val xs = RoperoGeometria.cuerposX(r)
        val i = xs.indexOfFirst { (a, b) -> x in a..b }.takeIf { it >= 0 } ?: xs.indices.minByOrNull { kotlin.math.abs((xs[it].first + xs[it].second) / 2f - x) } ?: 0
        return "cuerpo ${i + 1}"
    }

    /** Las hojas corredizas del ropero: las que se pidieron a mano, o las que tocan por el ancho. */
    fun hojasCorredizas(r: Ropero): Int = if (r.hojasCorredizas in 2..6) r.hojasCorredizas else RoperoPuertas.hojasCorredizasPorAncho(r.anchoCm)

    /** Las hojas batientes de un cuerpo: las pedidas a mano, o 1 hasta 60 cm de luz y 2 si es más ancho. */
    fun hojasBatientes(c: Cuerpo, espesorCm: Float): Int =
        if (c.hojasBatientes in 1..2) c.hojasBatientes else if (c.anchoCm + espesorCm <= 60f) 1 else 2

    /** El ancho del tapacanto que cubre el canto del tablero: 22 para 18 mm, 19 para 15. */
    fun anchoTapacantoMm(r: Ropero): Int = if (r.espesorMm <= 15) 19 else 22

    /** El nombre de la lista de tapacanto de dentro, con su ancho y su grosor: "Tapacanto 22 x 0.45 mm". */
    fun nombreTapacanto(r: Ropero): String = "Tapacanto ${anchoTapacantoMm(r)} x ${grosor(r.tapacantoGrosorMm)} mm"

    /** El del tapacanto de lo que se ve: "Tapacanto puertas 22 x 3 mm". */
    /** El del tapacanto fino de color, el de los laterales: "Tapacanto 22 x 0.45 mm de color". */
    fun nombreTapacantoColor(r: Ropero): String = "${nombreTapacanto(r)} de color"

    fun nombreTapacantoPuertas(r: Ropero): String = "Tapacanto puertas ${anchoTapacantoMm(r)} x ${grosor(r.tapacantoPuertasMm)} mm"

    /** El grosor de un tapacanto tal como se vende: 0.45, 1, 2, 3 (con un decimal se leía 0.4). */
    private fun grosor(mm: Float): String =
        java.math.BigDecimal(mm.toDouble()).setScale(2, java.math.RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()


    /** A mm, con una pizca para que 57.75 (que en float puede quedar en 57.7499) redondee como en el papel. */
    private fun mm(cm: Float): Int = (cm * 10f + 0.01f).roundToInt()

    private fun fmt(v: Float): String =
        if (v == v.toInt().toFloat()) v.toInt().toString() else String.format(java.util.Locale.US, "%.1f", v)
}
