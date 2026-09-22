package crystal.crystal.taller.drywall

import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

/** Qué se arma con el drywall. */
enum class TipoDrywall(val etiqueta: String) {
    /** Muro divisorio con estructura propia y plancha por las dos caras. */
    TABIQUE("Tabique (dos caras)"),
    /** Una sola cara de plancha sobre estructura pegada a un muro existente. */
    FORRO("Forro (una cara)"),
    /** Falso cielo colgado: ángulo perimetral, perfiles cruzados, alambre y una cara de plancha. */
    CIELO_RASO("Cielo raso")
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
 * En tabique y forro [largoM] es el largo del muro y [altoM] su alto; en cielo raso son los
 * dos lados del techo.
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
    val conLana: Boolean = false
) {
    val areaM2: Float get() = largoM * altoM
    val caras: Int get() = if (tipo == TipoDrywall.TABIQUE) 2 else 1
    /** Lo que se emplancha por cara: el paño menos los vanos. */
    val areaPorCaraM2: Float get() = (areaM2 - vanosM2).coerceAtLeast(0f)

    fun aJson(): String = JSONObject().apply {
        put("tipo", tipo.name); put("largo", largoM); put("alto", altoM); put("vanosM2", vanosM2); put("vanos", vanos)
        put("separacion", separacionCm); put("plancha", plancha.name); put("perfil", perfil.name); put("esquinas", esquinas); put("lana", conLana)
    }.toString()

    companion object {
        fun desdeJson(texto: String?): Drywall? = runCatching {
            val o = JSONObject(texto.orEmpty())
            Drywall(
                tipo = TipoDrywall.valueOf(o.optString("tipo", "TABIQUE")),
                largoM = o.optDouble("largo", 3.0).toFloat(), altoM = o.optDouble("alto", 2.4).toFloat(),
                vanosM2 = o.optDouble("vanosM2", 0.0).toFloat(), vanos = o.optInt("vanos", 0),
                separacionCm = o.optDouble("separacion", 40.6).toFloat(),
                plancha = PlanchaDrywall.valueOf(o.optString("plancha", "YESO_12")),
                perfil = PerfilDrywall.valueOf(o.optString("perfil", "P89")),
                esquinas = o.optInt("esquinas", 0), conLana = o.optBoolean("lana", false)
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
    val referencias: String
) {
    fun lineas(lista: List<MaterialDrywall>): String = lista.joinToString("\n") { "${it.nombre}: ${it.linea}" }
}

/**
 * Los materiales del drywall, con las reglas de obra de siempre (las cantidades comerciales que
 * se compran en Perú):
 *
 * - Plancha 1.22 x 2.44 (2.98 m²), un 10 % de merma, por cara. Los vanos se descuentan.
 * - Parantes de 3.05 m: uno cada [Drywall.separacionCm] más el del final, más 2 por vano; si
 *   el alto pasa de 3.05 se empalma (ceil(alto / 3.05) tramos por parante).
 * - Rieles de 3.05 m: arriba y abajo (2 x largo), más un dintel por vano. En el cielo raso los
 *   rieles son los portantes cada 1.22 y los parantes van cruzados cada 0.61.
 * - Ángulo perimetral (cielo raso) de 3.05 m: el perímetro. Alambre galvanizado #16 con un
 *   colgador cada 1.22 x 1.22, a un metro por colgador.
 * - Tornillo drywall 6 x 1": 30 por plancha (uno cada 30 cm en los parantes). Con fibrocemento,
 *   tornillo punta broca 6 x 1 1/4". Tornillo wafer 8 x 1/2" (perfil con perfil): 4 por parante.
 *   Anclaje o clavo de fijación al piso y techo: uno cada 60 cm de riel.
 * - Cinta de papel (rollo de 75 m): las juntas verticales cada 1.22 y las horizontales si el
 *   alto pasa de 2.44, por cara. Masilla: 28 kg (un balde) cada 25 m² de cara.
 * - Esquinero (cantonera) de 2.44 m: por esquina, ceil(alto / 2.44).
 * - Lana de vidrio: rollo de 15 m² (1.20 x 12.5), el paño entero.
 */
object DrywallCalculo {

    const val PLANCHA_M2 = 1.22f * 2.44f
    const val MERMA = 1.10f
    const val LARGO_PERFIL_M = 3.05f
    const val LARGO_ESQUINERO_M = 2.44f
    const val ROLLO_CINTA_M = 75f
    const val ROLLO_LANA_M2 = 15f
    const val TORNILLOS_POR_PLANCHA = 30
    const val MASILLA_KG_POR_M2 = 28f / 25f

    fun calcular(d: Drywall): MaterialesDrywall {
        val esCielo = d.tipo == TipoDrywall.CIELO_RASO
        val areaCaras = d.areaPorCaraM2 * d.caras
        val planchas = ceil(areaCaras / PLANCHA_M2 * MERMA).toInt().coerceAtLeast(if (areaCaras > 0f) 1 else 0)
        val esFibro = d.plancha == PlanchaDrywall.FIBRO_8 || d.plancha == PlanchaDrywall.FIBRO_10

        val perfiles = mutableListOf<MaterialDrywall>()
        val fijaciones = mutableListOf<MaterialDrywall>()
        val acabado = mutableListOf<MaterialDrywall>()
        var parantesUnidades: Int
        var rielesM: Float

        if (!esCielo) {
            // Un parante cada separación y el del final, más los refuerzos de los vanos.
            val posiciones = floor(d.largoM * 100f / d.separacionCm).toInt() + 1
            val tramosPorParante = ceil(d.altoM / LARGO_PERFIL_M).toInt().coerceAtLeast(1)
            parantesUnidades = (posiciones + 2 * d.vanos) * tramosPorParante
            rielesM = 2 * d.largoM + d.vanos * (if (d.vanos > 0) 1f else 0f)   // un dintel de ~1 m por vano
            perfiles.add(MaterialDrywall("${d.perfil.etiqueta} x 3.05 m", parantesUnidades.toFloat(), "und", "$posiciones posiciones cada ${fmt(d.separacionCm)} cm" + (if (d.vanos > 0) " + ${2 * d.vanos} de vano" else "")))
            perfiles.add(MaterialDrywall("Riel ${d.perfil.anchoMm} x 3.05 m", ceil(rielesM / LARGO_PERFIL_M).toFloat(), "und", "${fmt(rielesM)} m: arriba y abajo" + (if (d.vanos > 0) " y dinteles" else "")))
            fijaciones.add(MaterialDrywall("Anclaje de piso y techo", ceil(2 * d.largoM / 0.6f).toFloat(), "und", "uno cada 60 cm de riel"))
        } else {
            // El techo: portantes (rieles) cada 1.22 a lo largo del alto, parantes cruzados cada 0.61 a lo largo del largo.
            val portantes = floor(d.altoM / 1.22f).toInt() + 1
            val cruzados = floor(d.largoM / 0.61f).toInt() + 1
            rielesM = portantes * d.largoM
            val parantesM = cruzados * d.altoM
            parantesUnidades = ceil(parantesM / LARGO_PERFIL_M).toInt()
            perfiles.add(MaterialDrywall("Ángulo perimetral 3.05 m", ceil(2 * (d.largoM + d.altoM) / LARGO_PERFIL_M).toFloat(), "und", "${fmt(2 * (d.largoM + d.altoM))} m de perímetro"))
            perfiles.add(MaterialDrywall("Riel ${d.perfil.anchoMm} x 3.05 m (portante)", ceil(rielesM / LARGO_PERFIL_M).toFloat(), "und", "$portantes portantes cada 1.22"))
            perfiles.add(MaterialDrywall("${d.perfil.etiqueta} x 3.05 m (cruzado)", parantesUnidades.toFloat(), "und", "$cruzados cruzados cada 0.61"))
            val colgadores = ceil(d.largoM / 1.22f).toInt() * ceil(d.altoM / 1.22f).toInt()
            fijaciones.add(MaterialDrywall("Alambre galvanizado #16", colgadores.toFloat(), "m", "$colgadores colgadores cada 1.22 x 1.22, 1 m cada uno"))
            fijaciones.add(MaterialDrywall("Clavo con ojal", colgadores.toFloat(), "und"))
        }

        // Fijaciones de plancha y de perfil.
        fijaciones.add(0, MaterialDrywall(if (esFibro) "Tornillo punta broca 6 x 1 1/4\"" else "Tornillo drywall 6 x 1\"", (planchas * TORNILLOS_POR_PLANCHA).toFloat(), "und", "$TORNILLOS_POR_PLANCHA por plancha"))
        fijaciones.add(1, MaterialDrywall("Tornillo wafer 8 x 1/2\"", (parantesUnidades * 4).toFloat(), "und", "4 por parante"))

        // Acabado: cinta en las juntas, masilla por m² de cara, esquineros, lana.
        val juntasVerticales = ceil(d.largoM / 1.22f).toInt() - 1
        val juntasHorizontales = ceil(d.altoM / 2.44f).toInt() - 1
        val cintaM = ((juntasVerticales.coerceAtLeast(0) * d.altoM) + (juntasHorizontales.coerceAtLeast(0) * d.largoM)) * d.caras
        acabado.add(MaterialDrywall("Cinta de papel 75 m", ceil(cintaM / ROLLO_CINTA_M).coerceAtLeast(if (cintaM > 0f) 1f else 0f), "rollo", "${fmt(cintaM)} m de junta"))
        val masillaKg = areaCaras * MASILLA_KG_POR_M2
        acabado.add(MaterialDrywall("Masilla para juntas", masillaKg, "kg", "${ceil(masillaKg / 28f).toInt()} balde${if (masillaKg > 28f) "s" else ""} de 28 kg"))
        if (d.esquinas > 0) acabado.add(MaterialDrywall("Esquinero 2.44 m", (d.esquinas * ceil(d.altoM / LARGO_ESQUINERO_M).toInt()).toFloat(), "und"))
        if (d.conLana) acabado.add(MaterialDrywall("Lana de vidrio 15 m²", ceil(d.areaM2 / ROLLO_LANA_M2), "rollo", "${fmt(d.areaM2)} m²"))

        val referencias = buildString {
            append("${d.tipo.etiqueta}: ${fmt(d.largoM)} x ${fmt(d.altoM)} m = ${fmt(d.areaM2)} m²")
            if (d.vanosM2 > 0f) append(", menos ${fmt(d.vanosM2)} m² de ${d.vanos} vano${if (d.vanos == 1) "" else "s"}")
            append('\n')
            append("${d.plancha.etiqueta}, ${d.caras} cara${if (d.caras == 1) "" else "s"}: ${fmt(areaCaras)} m² + 10 % de merma\n")
            if (!esCielo) append("${d.perfil.etiqueta} cada ${fmt(d.separacionCm)} cm\n") else append("Cielo raso: portantes cada 1.22, cruzados cada 0.61\n")
            append("Perfiles de 3.05 m; tornillos: $TORNILLOS_POR_PLANCHA por plancha y 4 por parante")
        }
        return MaterialesDrywall(
            MaterialDrywall("Plancha ${d.plancha.etiqueta}", planchas.toFloat(), "und", "1.22 x 2.44; ${fmt(areaCaras)} m² + 10 %"),
            perfiles, fijaciones, acabado, referencias
        )
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
