package crystal.crystal.taller.drywall

import crystal.crystal.taller.ArcoEsquina
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

/** Cuántas caras lleva plancha, o si es techo. */
enum class TipoDrywall(val etiqueta: String) {
    /** Muro divisorio con estructura propia y plancha por las dos caras. */
    TABIQUE("Tabique (dos caras)"),
    /** Una sola cara de plancha: pegado a un muro, o tapando una tubería (el cajón en C). */
    FORRO("Forro (una cara)"),
    /** Falso cielo colgado: ángulo perimetral, perfiles cruzados, alambre y una cara de plancha. */
    CIELO_RASO("Cielo raso")
}

/**
 * La forma del tabique o del forro vista en planta, como en Nova corrediza (pedido del usuario,
 * 2026-10-11): recta, en L, en C, en S o curva. Cada una vale para tabique o para forro.
 */
enum class FormaDrywall(val etiqueta: String) {
    RECTA("Recta"),
    /** Dos tramos que doblan en escuadra. */
    L("En L"),
    /** Un frente y dos lados que vuelven hacia el mismo lado: el cajón que tapa un tubo contra el muro. */
    C("En C"),
    /** Tres tramos que doblan uno hacia un lado y el otro hacia el otro. */
    S("En S"),
    /** Un tramo curvo: su desarrollo y su flecha. */
    CURVA("Curva")
}

/** La plancha que se pone. Todas de 1.22 x 2.44 m. */
enum class PlanchaDrywall(val etiqueta: String, val espesorMm: Float) {
    YESO_12("Yeso 1/2\" (12.7 mm)", 12.7f),
    YESO_9("Yeso 3/8\" (9.5 mm)", 9.5f),
    YESO_RH("Yeso RH verde 1/2\"", 12.7f),
    FIBRO_8("Fibrocemento 8 mm", 8f),
    FIBRO_10("Fibrocemento 10 mm", 10f)
}

/** El ancho del parante y su riel (mm). */
enum class PerfilDrywall(val etiqueta: String, val anchoMm: Int) {
    P89("Parante 89", 89),
    P64("Parante 64", 64),
    P38("Parante 38", 38)
}

/**
 * Lo que se pide para calcular el drywall. Medidas en metros, como se habla en obra.
 *
 * En tabique y forro la planta son tramos ([tramos]): [largoM] es el primero (o el frente en la C,
 * o el desarrollo en la curva) y [lado2M] y [lado3M] los demás (en la C, los dos lados que salen
 * del muro; en la curva, [lado2M] es la flecha). En cielo raso [largoM] y [altoM] son los dos lados
 * del techo.
 */
data class Drywall(
    val tipo: TipoDrywall = TipoDrywall.TABIQUE,
    val largoM: Float = 3f,
    val altoM: Float = 2.4f,
    /** Los vanos (puertas, ventanas) que se descuentan de la plancha, en m². */
    val vanosM2: Float = 0f,
    /** Cuántos vanos son: cada uno lleva dos parantes de refuerzo y un dintel. */
    val vanos: Int = 0,
    /** Separación entre parantes, en cm: 40.6 (16") o 61 (24"). */
    val separacionCm: Float = 40.6f,
    val plancha: PlanchaDrywall = PlanchaDrywall.YESO_12,
    val perfil: PerfilDrywall = PerfilDrywall.P89,
    /** Esquinas vivas que llevan esquinero (cantonera). */
    val esquinas: Int = 0,
    /** Lana de vidrio entre parantes. */
    val conLana: Boolean = false,
    val forma: FormaDrywall = FormaDrywall.RECTA,
    val lado2M: Float = 0.18f,
    val lado3M: Float = 0.18f
) {
    /** Los tramos de la planta, en orden, en m. */
    val tramos: List<Float>
        get() = when (forma) {
            FormaDrywall.RECTA, FormaDrywall.CURVA -> listOf(largoM)
            FormaDrywall.L -> listOf(largoM, lado2M)
            FormaDrywall.C -> listOf(lado2M, largoM, lado3M)
            FormaDrywall.S -> listOf(largoM, lado2M, lado3M)
        }

    /** Las esquinas de la planta (donde dobla): 1 en la L, 2 en la C y en la S. */
    val dobleces: Int get() = (tramos.size - 1).coerceAtLeast(0)

    /** Lo que se emplancha a lo largo: la suma de los tramos. */
    val desarrolloM: Float get() = if (tipo == TipoDrywall.CIELO_RASO) largoM else tramos.sum()
    val areaM2: Float get() = desarrolloM * altoM
    val caras: Int get() = if (tipo == TipoDrywall.TABIQUE) 2 else 1
    /** Lo que se emplancha por cara: el paño menos los vanos. */
    val areaPorCaraM2: Float get() = (areaM2 - vanosM2).coerceAtLeast(0f)

    fun aJson(): String = JSONObject().apply {
        put("tipo", tipo.name); put("largo", largoM); put("alto", altoM); put("vanosM2", vanosM2); put("vanos", vanos)
        put("separacion", separacionCm); put("plancha", plancha.name); put("perfil", perfil.name); put("esquinas", esquinas); put("lana", conLana)
        put("forma", forma.name); put("lado2", lado2M); put("lado3", lado3M)
    }.toString()

    companion object {
        /** Las esquinas con esquinero que trae cada forma de fábrica (se pueden cambiar). */
        fun esquinasDeFabrica(tipo: TipoDrywall, forma: FormaDrywall): Int = when (forma) {
            FormaDrywall.RECTA, FormaDrywall.CURVA -> 0
            FormaDrywall.L -> 1
            FormaDrywall.C -> 2
            // En la S cada doblez tiene una cara de afuera: en el tabique las dos esquinas, en el forro una.
            FormaDrywall.S -> if (tipo == TipoDrywall.TABIQUE) 2 else 1
        }

        fun desdeJson(texto: String?): Drywall? = runCatching {
            val o = JSONObject(texto.orEmpty())
            val tipoGuardado = o.optString("tipo", "TABIQUE")
            // La "columna" del 2026-10-11 es un forro en C con los dos lados iguales.
            val eraColumna = tipoGuardado == "COLUMNA"
            val fondo = o.optDouble("fondo", 0.18).toFloat()
            Drywall(
                tipo = if (eraColumna) TipoDrywall.FORRO else TipoDrywall.valueOf(tipoGuardado),
                largoM = o.optDouble("largo", 3.0).toFloat(), altoM = o.optDouble("alto", 2.4).toFloat(),
                vanosM2 = o.optDouble("vanosM2", 0.0).toFloat(), vanos = o.optInt("vanos", 0),
                separacionCm = o.optDouble("separacion", 40.6).toFloat(),
                plancha = PlanchaDrywall.valueOf(o.optString("plancha", "YESO_12")),
                perfil = PerfilDrywall.valueOf(o.optString("perfil", "P89")),
                esquinas = if (eraColumna) 2 else o.optInt("esquinas", 0), conLana = o.optBoolean("lana", false),
                forma = if (eraColumna) FormaDrywall.C else FormaDrywall.valueOf(o.optString("forma", "RECTA")),
                lado2M = o.optDouble("lado2", fondo.toDouble()).toFloat(),
                lado3M = o.optDouble("lado3", fondo.toDouble()).toFloat()
            )
        }.getOrNull()
    }
}

/** Un material con su cantidad y su unidad, y de dónde sale (para leerlo). */
data class MaterialDrywall(val nombre: String, val cantidad: Float, val unidad: String, val nota: String = "") {
    /** La línea `cantidad unidad` como se archiva: enteros sin decimales. */
    val linea: String get() = "${DrywallCalculo.fmt(cantidad)} $unidad"
}

data class MaterialesDrywall(
    val planchas: MaterialDrywall,
    val perfiles: List<MaterialDrywall>,
    val fijaciones: List<MaterialDrywall>,
    val acabado: List<MaterialDrywall>,
    val referencias: String,
    /** Dónde va cada pieza y cómo se corta (en cm); vacío en el cielo raso. */
    val despiece: String = "",
    /** Las piezas de plancha como se archivan y las lee el optimizador de planchas: `ancho x alto = cantidad`, en cm. */
    val piezasPlancha: String = "",
    /** Cada perfil con sus trozos como se archivan y los lee el optimizador de varillas: `largo = cantidad`, en cm. */
    val filasPerfil: List<FilaDeCorte> = emptyList()
) {
    fun lineas(lista: List<MaterialDrywall>): String = lista.joinToString("\n") { "${it.nombre}: ${it.linea}" }

    /** Los materiales que no se cortan, como se archivan: `nombre (unidad) = cantidad`, entera hacia arriba. */
    fun lineasArchivables(lista: List<MaterialDrywall>): String = lista.joinToString("\n") {
        val nombre = if (it.unidad == "und") it.nombre else "${it.nombre} (${it.unidad})"
        "$nombre = ${kotlin.math.ceil(it.cantidad - 0.0001f).toInt()}"
    }
}

/** Una fila de corte: el nombre del material (la lista del proyecto) y sus piezas `medida = cantidad`. */
data class FilaDeCorte(val nombre: String, val lineas: String)

/** Una pieza de plancha: dónde va y lo que mide, en m. */
data class PiezaPlancha(val lugar: String, val anchoM: Float, val altoM: Float)

/** Una tira cortada a lo largo de la plancha (2.44), con las piezas apiladas en ella. */
data class ColumnaDeCorte(val anchoM: Float, val piezas: List<PiezaPlancha>)

/** Para qué va cada perfil vertical. Contra el muro va riel (dibujo del usuario, 2026-10-10). */
enum class FuncionVertical(val etiqueta: String) {
    RIEL_A_MURO("Riel a muro"),
    PUNTA("Parante de punta"),
    ESQUINA("Parante de esquina"),
    INTERMEDIO("Parante intermedio"),
    VANO("Parante de vano")
}

/** Un perfil vertical: en qué tramo del armazón y a cuánto de su arranque (m). Los de vano no tienen sitio (tramo -1). */
data class Vertical(val funcion: FuncionVertical, val tramo: Int, val enM: Float) {
    val esRiel: Boolean get() = funcion == FuncionVertical.RIEL_A_MURO
    val vaAlMuro: Boolean get() = funcion == FuncionVertical.RIEL_A_MURO || funcion == FuncionVertical.PUNTA
}

/** Todo lo que se corta para un tabique o un forro, pieza por pieza. Medidas en m. */
data class DespieceDrywall(
    /** Lo que se resta por cada plancha que tapa a otra en un doblez. */
    val espesorM: Float,
    /** Lo que mide el armazón en cada tramo: el tramo menos las planchas de los dobleces. */
    val armazonM: List<Float>,
    val piezas: List<PiezaPlancha>,
    /** Cada plancha de 1.22 x 2.44, con sus tiras. */
    val planchas: List<List<ColumnaDeCorte>>,
    val verticales: List<Vertical>,
    /** Los trozos de cada vertical: 3.00 enteros y, si no alcanza, el empalme con su traslape. */
    val trozosVertical: List<Float>,
    /** Las alturas de los amarres horizontales, sin contar el piso ni el techo. */
    val amarresM: List<Float>,
    val varillasRiel: List<List<Float>>,
    val varillasParante: List<List<Float>>
) {
    val empalmesPorVertical: Int get() = (trozosVertical.size - 1).coerceAtLeast(0)
}

/**
 * Los materiales del drywall, con las reglas de obra (cantidades comerciales de Perú):
 *
 * Tabique y forro, en cualquier forma, pieza por pieza ([despiece]; reglas del usuario del
 * 2026-10-10 con su dibujo del cajón en C):
 * - Las medidas son las de afuera. En cada doblez una plancha tapa el canto de la otra: en la C y
 *   la S la del medio va entre las otras dos, en la L la segunda va contra la primera. El armazón de
 *   cada tramo es el tramo menos la plancha de cada doblez (18 − 1.2 = 16.8 y 18 − 2 x 1.2 = 15.6).
 *   Se resta el espesor en mm enteros: 12.7 → 1.2 cm, como en su dibujo.
 * - Planchas: cada tramo en tiras de 1.22 como mucho, por franjas de 2.44 de alto y lo que queda
 *   arriba; las tiras se cortan a lo largo de la plancha y lo bajo se apila en una tira. Nunca menos
 *   que los m² (sin los vanos) más el 10 % de merma.
 * - Verticales: contra el muro, riel (en la C, las dos puntas); en cada doblez, parante de esquina;
 *   entre medio, los que pide la separación; 2 por vano. En la curva, cada 30.5 como mucho. Si el
 *   alto pasa de 3.00 se empalma con 30 cm de traslape.
 * - Riel de piso y techo por el armazón, más un dintel de 1 m por vano. En la C, amarres de riel
 *   cada 1.22 de alto (3 por alto de plancha: piso, 1.22 y 2.44).
 * - Los trozos de perfil salen de varillas de 3.00 (todos, también el esquinero), del más largo al más corto.
 * - Anclaje al piso y al techo cada 60 cm de armazón, 2 como mínimo; anclaje a muro de lo que va
 *   contra el muro, cada 60 cm de alto, de punta a punta.
 * - Tornillo drywall 6 x 1" cada 30 cm en cada perfil que toca la pieza (punta broca 6 x 1 1/4"
 *   con fibrocemento). Tornillo wafer 8 x 1/2": 4 por vertical, por empalme y por amarre.
 * - Cinta de papel (rollo de 75 m): juntas entre tiras, juntas a 2.44, los dobleces sin esquinero y
 *   la unión con el muro en las dos puntas, por cara. Masilla: 28 kg cada 25 m² de cara. Ángulo
 *   esquinero en cada esquina viva, del alto en varillas de 3.00 a tope. Lana: 15 m² por rollo.
 *
 * Cielo raso: ángulo perimetral, portantes (rieles) cada 1.22, cruzados (parantes) cada 0.61,
 * colgador de alambre #16 cada 1.22 x 1.22 a un metro cada uno.
 */
object DrywallCalculo {

    const val PLANCHA_ANCHO_M = 1.22f
    const val PLANCHA_ALTO_M = 2.44f
    const val PLANCHA_M2 = PLANCHA_ANCHO_M * PLANCHA_ALTO_M
    const val MERMA = 1.10f
    /** Todos los perfiles (parante, riel, ángulos) vienen en varillas de 3.00 m (el usuario, 2026-10-11; antes 3.05). */
    const val LARGO_PERFIL_M = 3.00f
    /** El texto del largo de varilla: "3.00 m". */
    val VARILLA = String.format(java.util.Locale.US, "%.2f m", LARGO_PERFIL_M)
    const val ROLLO_CINTA_M = 75f
    const val ROLLO_LANA_M2 = 15f
    const val TORNILLOS_POR_PLANCHA = 30
    /** Un tornillo drywall cada tanto en cada perfil que toca la plancha (el usuario, 2026-10-10). */
    const val PASO_TORNILLO_M = 0.30f
    const val MASILLA_KG_POR_M2 = 28f / 25f
    /** Lo que se monta un perfil sobre el otro al empalmarlo. */
    const val TRASLAPE_EMPALME_M = 0.30f
    /** La separación más grande entre parantes en un tramo curvo. */
    const val SEPARACION_CURVA_M = 0.305f
    /** Los amarres horizontales del cajón: uno cada medio alto de plancha (el usuario, 2026-10-10). */
    const val PASO_AMARRE_M = 1.22f

    fun calcular(d: Drywall): MaterialesDrywall =
        if (d.tipo == TipoDrywall.CIELO_RASO) cieloRaso(d) else muro(d)

    /** La separación entre parantes en m: en la curva, 30.5 como mucho. */
    fun separacionM(d: Drywall): Float =
        (d.separacionCm / 100f).let { if (d.forma == FormaDrywall.CURVA) minOf(it, SEPARACION_CURVA_M) else it }

    /** Lo que se resta por plancha: el espesor en mm enteros (12.7 → 1.2 cm, como lo dibuja el usuario). */
    fun espesorM(p: PlanchaDrywall): Float = floor(p.espesorMm + 0.001f) / 1000f

    /** El nombre de cada tramo en el despiece. */
    fun nombresDeTramos(d: Drywall): List<String> = when (d.forma) {
        FormaDrywall.RECTA -> listOf("Paño")
        FormaDrywall.CURVA -> listOf("Curva")
        FormaDrywall.C -> listOf("Lado izq.", "Frente", "Lado der.")
        else -> d.tramos.indices.map { "Tramo ${it + 1}" }
    }

    /** Cuántas planchas de otro tramo tapan los cantos de la plancha de este (en la C, el frente va entre los lados). */
    fun planchasQueLaTapan(d: Drywall, i: Int): Int = when (d.forma) {
        FormaDrywall.C, FormaDrywall.S -> if (i == 1) 2 else 0
        FormaDrywall.L -> if (i == 1) 1 else 0
        else -> 0
    }

    /** El despiece de un tabique o un forro: las piezas de plancha, los verticales y los trozos de perfil. */
    fun despiece(d: Drywall): DespieceDrywall {
        val tramos = d.tramos
        val e = espesorM(d.plancha)
        val sep = separacionM(d)
        val armazon = tramos.mapIndexed { i, t ->
            val dobleces = (if (i > 0) 1 else 0) + (if (i < tramos.lastIndex) 1 else 0)
            (t - dobleces * e).coerceAtLeast(0.01f)
        }

        val franjas = trozosDe(d.altoM, PLANCHA_ALTO_M)
        val nombres = nombresDeTramos(d)
        val piezas = (1..d.caras).flatMap { cara ->
            tramos.flatMapIndexed { i, t ->
                val lugar = nombres[i] + if (d.caras > 1) " (cara $cara)" else ""
                val ancho = (t - planchasQueLaTapan(d, i) * e).coerceAtLeast(0.01f)
                trozosDe(ancho, PLANCHA_ANCHO_M).flatMap { a -> franjas.map { h -> PiezaPlancha(lugar, a, h) } }
            }
        }

        // Verticales: la punta del arranque, los intermedios y la punta de cada tramo (esquina o final).
        val esCajon = d.forma == FormaDrywall.C
        val punta = if (esCajon) FuncionVertical.RIEL_A_MURO else FuncionVertical.PUNTA
        val verticales = mutableListOf<Vertical>()
        armazon.forEachIndexed { i, f ->
            val n = maxOf(1, ceil(f / sep - 0.0001f).toInt())
            if (i == 0) verticales.add(Vertical(punta, 0, 0f))
            for (k in 1 until n) verticales.add(Vertical(FuncionVertical.INTERMEDIO, i, f * k / n))
            verticales.add(Vertical(if (i < armazon.lastIndex) FuncionVertical.ESQUINA else punta, i, f))
        }
        repeat(2 * d.vanos) { verticales.add(Vertical(FuncionVertical.VANO, -1, 0f)) }

        val trozosVertical = conEmpalme(d.altoM)
        val amarres = if (esCajon) generateSequence(PASO_AMARRE_M) { it + PASO_AMARRE_M }.takeWhile { it < d.altoM - 0.01f }.toList() else emptyList()
        val horizontal = armazon.flatMap { trozosDe(it, LARGO_PERFIL_M) }
        val trozosRiel = verticales.filter { it.esRiel }.flatMap { trozosVertical } +
            List(2 + amarres.size) { horizontal }.flatten() + List(d.vanos) { 1f }
        val trozosParante = verticales.filterNot { it.esRiel }.flatMap { trozosVertical }

        return DespieceDrywall(
            espesorM = e, armazonM = armazon, piezas = piezas, planchas = cortarPlanchas(piezas),
            verticales = verticales, trozosVertical = trozosVertical, amarresM = amarres,
            varillasRiel = cortarVarillas(trozosRiel), varillasParante = cortarVarillas(trozosParante)
        )
    }

    /** Tabique o forro, en cualquier forma: todo sale del despiece. */
    private fun muro(d: Drywall): MaterialesDrywall {
        val x = despiece(d)
        val tramos = d.tramos
        val esCurva = d.forma == FormaDrywall.CURVA
        val sep = separacionM(d)
        val esFibro = d.plancha == PlanchaDrywall.FIBRO_8 || d.plancha == PlanchaDrywall.FIBRO_10

        // Planchas: las del despiece, nunca menos que los m² con merma.
        val areaPiezas = x.piezas.sumOf { (it.anchoM * it.altoM).toDouble() }.toFloat()
        val areaCaras = (areaPiezas - d.vanosM2 * d.caras).coerceAtLeast(0f)
        val porArea = ceil(areaCaras / PLANCHA_M2 * MERMA - 0.0001f).toInt()
        val planchas = maxOf(x.planchas.size, porArea).coerceAtLeast(if (areaCaras > 0f) 1 else 0)
        val merma = planchas - x.planchas.size
        val planchasNota = "1.22 x 2.44; ${x.planchas.size} del despiece" +
            (if (merma > 0) " + $merma de merma (${fmt(areaCaras)} m² + 10 %)" else "") +
            ", ${d.caras} cara${if (d.caras == 1) "" else "s"}"

        // Perfiles: las varillas de 3.00 de donde salen los trozos.
        val parantes = x.verticales.filterNot { it.esRiel }
        val empalmes = x.verticales.size * x.empalmesPorVertical
        val perfiles = mutableListOf<MaterialDrywall>()
        if (x.varillasParante.isNotEmpty()) perfiles.add(MaterialDrywall(
            "${d.perfil.etiqueta} x $VARILLA", x.varillasParante.size.toFloat(), "und",
            buildString {
                append("${parantes.size} parante${if (parantes.size == 1) "" else "s"} de ${fmt(d.altoM)} m, cada ${fmt(sep * 100f)} cm")
                if (d.vanos > 0) append(", ${2 * d.vanos} de vano")
                if (x.empalmesPorVertical > 0) append("; empalmados con ${fmt(TRASLAPE_EMPALME_M * 100f)} cm de traslape")
            }
        ))
        perfiles.add(MaterialDrywall(
            "Riel ${d.perfil.anchoMm} x $VARILLA", x.varillasRiel.size.toFloat(), "und",
            buildString {
                append("piso y techo")
                if (x.verticales.any { it.esRiel }) append(", ${x.verticales.count { it.esRiel }} a muro")
                if (x.amarresM.isNotEmpty()) append(", amarres a ${x.amarresM.joinToString(" y ") { fmt(it) }} m")
                if (d.vanos > 0) append(", dinteles")
                if (esCurva) append("; cortado en las alas cada 5 cm para curvarlo")
            }
        ))

        // Fijaciones.
        val armazonTotal = x.armazonM.sum()
        val anclajesPisoTecho = 2 * maxOf(2, ceil(armazonTotal / 0.6f - 0.0001f).toInt())
        val alMuro = x.verticales.filter { it.vaAlMuro }
        val anclajesMuro = alMuro.size * (ceil(d.altoM / 0.6f - 0.0001f).toInt() + 1)
        val porLinea = { h: Float -> ceil(h / PASO_TORNILLO_M - 0.0001f).toInt() + 1 }
        val tornillos = x.piezas.sumOf { p -> (2 + floor((p.anchoM - 0.01f) / sep).toInt()) * porLinea(p.altoM) }
        val amarresPiezas = x.amarresM.size * x.armazonM.size
        val wafer = 4 * (x.verticales.size + empalmes + amarresPiezas)
        val fijaciones = mutableListOf(
            MaterialDrywall(if (esFibro) "Tornillo punta broca 6 x 1 1/4\"" else "Tornillo drywall 6 x 1\"", tornillos.toFloat(), "und",
                "cada ${fmt(PASO_TORNILLO_M * 100f)} cm en cada perfil que toca la plancha"),
            MaterialDrywall("Tornillo wafer 8 x 1/2\"", wafer.toFloat(), "und",
                "4 por vertical" + (if (empalmes > 0) ", por empalme" else "") + (if (amarresPiezas > 0) ", por amarre" else "")),
            MaterialDrywall("Anclaje de piso y techo", anclajesPisoTecho.toFloat(), "und", "uno cada 60 cm de riel, 2 como mínimo"),
            MaterialDrywall("Anclaje a muro", anclajesMuro.toFloat(), "und",
                "${alMuro.size} ${if (alMuro.all { it.esRiel }) "rieles" else "perfiles"} contra el muro, cada 60 cm de punta a punta")
        )

        // Cinta: juntas entre tiras y a 2.44, dobleces sin esquinero y la unión con el muro.
        val franjas = trozosDe(d.altoM, PLANCHA_ALTO_M).size
        val anchosPorCara = tramos.indices.map { i -> trozosDe((tramos[i] - planchasQueLaTapan(d, i) * x.espesorM).coerceAtLeast(0.01f), PLANCHA_ANCHO_M) }
        val verticalesCinta = anchosPorCara.sumOf { (it.size - 1).coerceAtLeast(0) }
        val doblecesConCinta = (d.dobleces * d.caras - d.esquinas).coerceAtLeast(0)
        val cintaM = (verticalesCinta * d.altoM + (franjas - 1) * anchosPorCara.flatten().sum() + 2 * d.altoM) * d.caras + doblecesConCinta * d.altoM
        val masillaKg = areaCaras * MASILLA_KG_POR_M2
        val acabado = mutableListOf(
            MaterialDrywall("Cinta de papel 75 m", ceil(cintaM / ROLLO_CINTA_M).coerceAtLeast(if (cintaM > 0f) 1f else 0f), "rollo", "${fmt(cintaM)} m de junta y unión con el muro"),
            MaterialDrywall("Masilla para juntas", masillaKg, "kg", "${ceil(masillaKg / 28f).toInt()} balde${if (masillaKg > 28f) "s" else ""} de 28 kg")
        )
        // Ángulo esquinero (pedido del usuario, 2026-10-10): un perfil más, en cada esquina viva que
        // puede recibir un golpe (en la C, las dos donde se juntan los paneles), de punta a punta del
        // alto; los trozos a tope, y salen de varillas de 2.44 como los demás perfiles.
        val trozosEsquinero = List(d.esquinas) { trozosDe(d.altoM, LARGO_PERFIL_M) }.flatten()
        val varillasEsquinero = cortarVarillas(trozosEsquinero, LARGO_PERFIL_M)
        if (d.esquinas > 0) perfiles.add(MaterialDrywall(
            "Ángulo esquinero x $VARILLA", varillasEsquinero.size.toFloat(), "und",
            "${d.esquinas} esquina${if (d.esquinas == 1) "" else "s"} de ${fmt(d.altoM)} m, a tope"
        ))
        if (d.conLana) acabado.add(MaterialDrywall("Lana de vidrio 15 m²", ceil(d.areaM2 / ROLLO_LANA_M2), "rollo", "${fmt(d.areaM2)} m²"))

        val referencias = buildString {
            if (d.forma == FormaDrywall.RECTA) append("${d.tipo.etiqueta}: ${fmt(d.largoM)} x ${fmt(d.altoM)} m = ${fmt(d.areaM2)} m²")
            else append("${d.tipo.etiqueta}, ${d.forma.etiqueta.lowercase()}: ${tramos.joinToString(" + ") { fmt(it) }} = ${fmt(d.desarrolloM)} m x ${fmt(d.altoM)} m = ${fmt(d.areaM2)} m²")
            if (d.vanosM2 > 0f) append(", menos ${fmt(d.vanosM2)} m² de ${d.vanos} vano${if (d.vanos == 1) "" else "s"}")
            append('\n')
            if (esCurva) ArcoEsquina.deDesarrolloYFlecha(d.largoM, d.lado2M)?.let {
                append("Curva: desarrollo ${fmt(it.desarrollo)}, cuerda ${fmt(it.cuerda)}, flecha ${fmt(it.flecha)}, radio ${fmt(it.radio)} m\n")
            }
            append("${d.plancha.etiqueta}, ${d.caras} cara${if (d.caras == 1) "" else "s"}: ${fmt(areaCaras)} m²\n")
            append("${d.perfil.etiqueta} cada ${fmt(sep * 100f)} cm; perfiles de $VARILLA\n")
            append(compra(planchas, perfiles))
        }
        val filas = listOfNotNull(
            FilaDeCorte(d.perfil.etiqueta, lineasDeTrozos(x.varillasParante.flatten())).takeIf { x.varillasParante.isNotEmpty() },
            FilaDeCorte("Riel ${d.perfil.anchoMm}", lineasDeTrozos(x.varillasRiel.flatten())),
            FilaDeCorte("Ángulo esquinero", lineasDeTrozos(trozosEsquinero)).takeIf { trozosEsquinero.isNotEmpty() }
        )
        return MaterialesDrywall(
            MaterialDrywall("Plancha ${d.plancha.etiqueta}", planchas.toFloat(), "und", planchasNota),
            perfiles, fijaciones, acabado, referencias, textoDespiece(d, x),
            piezasPlancha = lineasDePiezas(x.piezas.map { it.anchoM to it.altoM }), filasPerfil = filas
        )
    }

    /** Lo que se compra, en una línea: planchas enteras y varillas de 3.00 (parantes, rieles y ángulos). */
    private fun compra(planchas: Int, perfiles: List<MaterialDrywall>): String {
        fun plural(palabra: String) = when {
            palabra.any { it.isDigit() } -> palabra
            palabra.endsWith("l") -> "${palabra}es"
            else -> "${palabra}s"
        }
        val partes = listOf("$planchas plancha${if (planchas == 1) "" else "s"}") + perfiles.map {
            val nombre = it.nombre.substringBefore(" x ").lowercase()
            "${fmt(it.cantidad)} ${if (it.cantidad == 1f) nombre else nombre.split(' ').joinToString(" ") { p -> plural(p) }}"
        }
        return "Compra: " + partes.dropLast(1).joinToString(", ") + (if (partes.size > 1) " y " else "") + partes.last() + " de $VARILLA"
    }

    /** Medida en cm con un decimal como mucho (material: df1). */
    private fun cm1(m: Float): String {
        val v = (m * 1000f).roundToInt() / 10f
        return if (abs(v - v.roundToInt()) < 0.001f) v.roundToInt().toString() else String.format(java.util.Locale.US, "%.1f", v)
    }

    /** Piezas de plancha `ancho x alto = cantidad` (en cm), las iguales juntas y en el orden en que salen. */
    fun lineasDePiezas(piezas: List<Pair<Float, Float>>): String =
        piezas.map { (a, h) -> "${cm1(a)}x${cm1(h)}" }.groupingBy { it }.eachCount().entries
            .joinToString("\n") { (medida, n) -> "$medida = $n" }

    /** Trozos de perfil `largo = cantidad` (en cm), del más largo al más corto. */
    fun lineasDeTrozos(trozos: List<Float>): String =
        trozos.sortedDescending().map { cm1(it) }.groupingBy { it }.eachCount().entries
            .joinToString("\n") { (largo, n) -> "$largo = $n" }

    /** El despiece para leer en obra, en cm: el armazón, cada pieza en su sitio y cómo sale de la plancha y de las varillas. */
    fun textoDespiece(d: Drywall, x: DespieceDrywall): String = buildString {
        fun cm(m: Float) = fmt(m * 100f)
        /** "18 × 244 + 15.6 × 71 (2)": las medidas iguales juntas, con cuántas son. */
        fun agrupar(medidas: List<String>): String =
            medidas.groupingBy { it }.eachCount().entries.joinToString(" + ") { (m, n) -> if (n > 1) "$m ($n)" else m }
        val nombres = nombresDeTramos(d)
        val armazon = x.armazonM.joinToString(" + ") { cm(it) }
        append("Medidas en cm\n")
        append("Armazón: ")
        if (x.armazonM.size > 1) append(nombres.zip(x.armazonM).joinToString(", ") { (n, m) -> "${n.lowercase()} ${cm(m)}" })
        else append(cm(x.armazonM.first()))
        if (d.dobleces > 0) append(" (se resta la plancha, ${cm(x.espesorM)})")
        append('\n')

        append("\nPLANCHAS (ancho × alto)\n")
        x.piezas.groupBy { it.lugar }.forEach { (lugar, ps) ->
            append("$lugar: ${agrupar(ps.map { "${cm(it.anchoM)} × ${cm(it.altoM)}" })}\n")
        }
        x.planchas.forEachIndexed { i, columnas ->
            append("Plancha ${i + 1}: ")
            append(columnas.joinToString(" | ") { c -> agrupar(c.piezas.map { "${cm(it.anchoM)} × ${cm(it.altoM)}" }) })
            val sobra = PLANCHA_ANCHO_M - columnas.sumOf { it.anchoM.toDouble() }.toFloat()
            if (sobra > 0.005f) append("; sobra ${cm(sobra)} × ${cm(PLANCHA_ALTO_M)}")
            append('\n')
        }

        append("\nPERFILES\n")
        val empalme = if (x.empalmesPorVertical > 0) " (${x.trozosVertical.joinToString(" + ") { cm(it) }}, ${cm(TRASLAPE_EMPALME_M)} de traslape)" else ""
        x.verticales.groupBy { it.funcion }.forEach { (f, vs) -> append("${f.etiqueta}: ${vs.size} de ${cm(d.altoM)}$empalme\n") }
        append("Riel de piso y techo: 2 de ($armazon)\n")
        if (x.amarresM.isNotEmpty()) append("Amarre de riel a ${x.amarresM.joinToString(" y ") { cm(it) }}: ${x.amarresM.size} de ($armazon)\n")
        if (d.vanos > 0) append("Dintel de vano: ${d.vanos} de 100\n")
        val trozosEsquinero = List(d.esquinas) { trozosDe(d.altoM, LARGO_PERFIL_M) }.flatten()
        if (d.esquinas > 0) append("Ángulo esquinero: ${d.esquinas} de ${cm(d.altoM)}" +
            (if (trozosEsquinero.size > d.esquinas) " (${trozosDe(d.altoM, LARGO_PERFIL_M).joinToString(" + ") { cm(it) }}, a tope)" else "") + "\n")
        fun varillas(nombre: String, lista: List<List<Float>>, largo: Float = LARGO_PERFIL_M) = lista.forEachIndexed { i, v ->
            append("$nombre ${i + 1}: ${agrupar(v.map { cm(it) })}")
            val sobra = largo - v.sum()
            if (sobra > 0.005f) append("; sobra ${cm(sobra)}")
            append('\n')
        }
        varillas("Parante", x.varillasParante)
        varillas("Riel", x.varillasRiel)
        varillas("Ángulo esquinero", cortarVarillas(trozosEsquinero, LARGO_PERFIL_M), LARGO_PERFIL_M)
    }.trimEnd()

    /** El cielo raso, como siempre. */
    private fun cieloRaso(d: Drywall): MaterialesDrywall {
        val areaCaras = d.areaPorCaraM2 * d.caras
        val planchas = ceil(areaCaras / PLANCHA_M2 * MERMA).toInt().coerceAtLeast(if (areaCaras > 0f) 1 else 0)
        val esFibro = d.plancha == PlanchaDrywall.FIBRO_8 || d.plancha == PlanchaDrywall.FIBRO_10
        // El techo: portantes (rieles) cada 1.22 a lo largo del alto, parantes cruzados cada 0.61 a lo largo del largo.
        val portantes = floor(d.altoM / 1.22f).toInt() + 1
        val cruzados = floor(d.largoM / 0.61f).toInt() + 1
        val rielesM = portantes * d.largoM
        val parantesUnidades = ceil(cruzados * d.altoM / LARGO_PERFIL_M).toInt()
        val perfiles = listOf(
            MaterialDrywall("Ángulo perimetral x $VARILLA", ceil(2 * (d.largoM + d.altoM) / LARGO_PERFIL_M).toFloat(), "und", "${fmt(2 * (d.largoM + d.altoM))} m de perímetro"),
            MaterialDrywall("Riel ${d.perfil.anchoMm} x $VARILLA (portante)", ceil(rielesM / LARGO_PERFIL_M).toFloat(), "und", "$portantes portantes cada 1.22"),
            MaterialDrywall("${d.perfil.etiqueta} x $VARILLA (cruzado)", parantesUnidades.toFloat(), "und", "$cruzados cruzados cada 0.61")
        )
        val colgadores = ceil(d.largoM / 1.22f).toInt() * ceil(d.altoM / 1.22f).toInt()
        val fijaciones = listOf(
            MaterialDrywall(if (esFibro) "Tornillo punta broca 6 x 1 1/4\"" else "Tornillo drywall 6 x 1\"", (planchas * TORNILLOS_POR_PLANCHA).toFloat(), "und", "$TORNILLOS_POR_PLANCHA por plancha"),
            MaterialDrywall("Tornillo wafer 8 x 1/2\"", (parantesUnidades * 4).toFloat(), "und", "4 por parante"),
            MaterialDrywall("Alambre galvanizado #16", colgadores.toFloat(), "m", "$colgadores colgadores cada 1.22 x 1.22, 1 m cada uno"),
            MaterialDrywall("Clavo con ojal", colgadores.toFloat(), "und")
        )
        val juntasVerticales = ceil(d.largoM / 1.22f).toInt() - 1
        val juntasHorizontales = ceil(d.altoM / 2.44f).toInt() - 1
        val cintaM = (juntasVerticales.coerceAtLeast(0) * d.altoM) + (juntasHorizontales.coerceAtLeast(0) * d.largoM)
        val masillaKg = areaCaras * MASILLA_KG_POR_M2
        val acabado = mutableListOf(
            MaterialDrywall("Cinta de papel 75 m", ceil(cintaM / ROLLO_CINTA_M).coerceAtLeast(if (cintaM > 0f) 1f else 0f), "rollo", "${fmt(cintaM)} m de junta"),
            MaterialDrywall("Masilla para juntas", masillaKg, "kg", "${ceil(masillaKg / 28f).toInt()} balde${if (masillaKg > 28f) "s" else ""} de 28 kg")
        )
        if (d.esquinas > 0) acabado.add(MaterialDrywall("Ángulo esquinero x $VARILLA", (d.esquinas * ceil(d.altoM / LARGO_PERFIL_M).toInt()).toFloat(), "und"))
        if (d.conLana) acabado.add(MaterialDrywall("Lana de vidrio 15 m²", ceil(d.areaM2 / ROLLO_LANA_M2), "rollo", "${fmt(d.areaM2)} m²"))
        val referencias = buildString {
            append("${d.tipo.etiqueta}: ${fmt(d.largoM)} x ${fmt(d.altoM)} m = ${fmt(d.areaM2)} m²")
            if (d.vanosM2 > 0f) append(", menos ${fmt(d.vanosM2)} m² de ${d.vanos} vano${if (d.vanos == 1) "" else "s"}")
            append('\n')
            append("${d.plancha.etiqueta}, ${d.caras} cara: ${fmt(areaCaras)} m² + 10 % de merma\n")
            append("Cielo raso: portantes cada 1.22, cruzados cada 0.61\n")
            append("Perfiles de $VARILLA; tornillos: $TORNILLOS_POR_PLANCHA por plancha y 4 por parante")
        }
        // Para archivar: planchas enteras y los trozos de cada perfil (los largos de más de 3.00, a tope).
        val filas = listOf(
            FilaDeCorte("Ángulo perimetral", lineasDeTrozos(listOf(d.largoM, d.largoM, d.altoM, d.altoM).flatMap { trozosDe(it, LARGO_PERFIL_M) })),
            FilaDeCorte("Riel ${d.perfil.anchoMm}", lineasDeTrozos(List(portantes) { trozosDe(d.largoM, LARGO_PERFIL_M) }.flatten())),
            FilaDeCorte(d.perfil.etiqueta, lineasDeTrozos(List(cruzados) { trozosDe(d.altoM, LARGO_PERFIL_M) }.flatten()))
        )
        return MaterialesDrywall(
            MaterialDrywall("Plancha ${d.plancha.etiqueta}", planchas.toFloat(), "und", "1.22 x 2.44; ${fmt(areaCaras)} m² + 10 %"),
            perfiles, fijaciones, acabado, referencias,
            piezasPlancha = lineasDePiezas(List(planchas) { PLANCHA_ANCHO_M to PLANCHA_ALTO_M }), filasPerfil = filas
        )
    }

    /** Un largo partido en trozos de [tope] como mucho, a tope (sin traslape): 3.15 en 2.44 → 2.44 y 0.71. */
    fun trozosDe(largo: Float, tope: Float): List<Float> {
        val n = floor(largo / tope + 0.0001f).toInt()
        return List(n) { tope } + listOfNotNull((largo - n * tope).takeIf { it > 0.005f })
    }

    /** Los trozos de un perfil vertical de [alto]: varillas enteras y, en cada empalme, lo que falta más el traslape. */
    fun conEmpalme(alto: Float): List<Float> {
        val trozos = mutableListOf<Float>()
        var resta = alto
        while (resta > LARGO_PERFIL_M + 0.005f) {
            trozos.add(LARGO_PERFIL_M)
            resta = resta - LARGO_PERFIL_M + TRASLAPE_EMPALME_M
        }
        if (resta > 0.005f) trozos.add(resta)
        return trozos
    }

    /**
     * Las piezas acomodadas en planchas de 1.22 x 2.44: cada pieza abre una tira a lo largo de la
     * plancha (de su ancho) o se apila sobre otra tira igual o más ancha que tenga sitio; las tiras
     * van lado a lado, de la más ancha a la más angosta.
     */
    fun cortarPlanchas(piezas: List<PiezaPlancha>): List<List<ColumnaDeCorte>> {
        class Tira(val ancho: Float, var libre: Float, val piezas: MutableList<PiezaPlancha>)
        val tiras = mutableListOf<Tira>()
        piezas.sortedWith(compareByDescending<PiezaPlancha> { it.altoM }.thenByDescending { it.anchoM }).forEach { p ->
            val t = tiras.filter { it.ancho >= p.anchoM - 0.001f && it.libre >= p.altoM - 0.001f }.minByOrNull { it.ancho }
            if (t != null) { t.piezas.add(p); t.libre -= p.altoM }
            else tiras.add(Tira(p.anchoM, PLANCHA_ALTO_M - p.altoM, mutableListOf(p)))
        }
        val planchas = mutableListOf<MutableList<ColumnaDeCorte>>()
        val libres = mutableListOf<Float>()
        tiras.sortedByDescending { it.ancho }.forEach { t ->
            val i = libres.indexOfFirst { it >= t.ancho - 0.001f }
            val columna = ColumnaDeCorte(t.ancho, t.piezas.toList())
            if (i >= 0) { planchas[i].add(columna); libres[i] -= t.ancho }
            else { planchas.add(mutableListOf(columna)); libres.add(PLANCHA_ANCHO_M - t.ancho) }
        }
        return planchas
    }

    /** Los trozos repartidos en varillas de [largo], del más largo al más corto (cada uno en la primera que lo admite). */
    fun cortarVarillas(trozos: List<Float>, largo: Float = LARGO_PERFIL_M): List<List<Float>> {
        val varillas = mutableListOf<MutableList<Float>>()
        val libres = mutableListOf<Float>()
        trozos.sortedDescending().forEach { t ->
            val i = libres.indexOfFirst { it >= t - 0.001f }
            if (i >= 0) { varillas[i].add(t); libres[i] -= t }
            else { varillas.add(mutableListOf(t)); libres.add(largo - t) }
        }
        return varillas
    }

    fun fmt(v: Float): String {
        val d = (v * 100f).roundToInt() / 100f
        return when {
            abs(d - d.roundToInt()) < 0.001f -> d.roundToInt().toString()
            abs(d * 10f - (d * 10f).roundToInt()) < 0.001f -> String.format(java.util.Locale.US, "%.1f", d)
            else -> String.format(java.util.Locale.US, "%.2f", d)
        }
    }
}
