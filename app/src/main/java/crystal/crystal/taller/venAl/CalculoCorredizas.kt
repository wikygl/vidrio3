package crystal.crystal.taller.venAl

import crystal.crystal.taller.nova.NovaCalculos
import kotlin.math.ceil

/**
 * Series corredizas con plantilla de cortes del proveedor (20, 25, 42, 46, 62 y 80): por modelo de
 * hojas, cada perfil con su cantidad y su descuento, copiados de las hojas de cálculo (en mm, como el
 * proveedor; se pasan a cm al calcular). Es la única fuente de esos cortes: la leen VentanaAl (las
 * casillas), el dibujo (patrones) y `CalculoCorredizasTest`.
 *
 * Bases de una pieza: ANCHO = ancho del tramo, MODULO = ancho del tramo / nº de hojas, ALTO = alto,
 * FIJO = largo fijo (cortaviento). Las piezas de igual lista y medida se suman (el cabezal de la
 * corrediza y el del fijo suelen coincidir).
 *
 * "tramos" reparte las divisiones (regla de los 60) en tramos de 2 a 4 hojas; cada tramo es una
 * ventana completa y entre tramos va un parante de 2.5 a todo el alto.
 *
 * Correcciones del 2026-10-16 al comparar con las hojas del proveedor (antes estaban en
 * `VentanaAl.calcularSerieTramos`):
 * - Serie 20: la jamba salía una por corrediza (fc 1, cccc 4); son siempre 2.
 * - Serie 20: el adaptador salía ×2 y solo en fccf; es ×1 en fccf y en cccc.
 * - Serie 20 cccc: el vidrio era A/4 + 4.9 (copiado de la celda del Excel, que tiene el signo
 *   cambiado: el vidrio salía más ancho que la hoja); es A/4 − 4.9, como en fccf.
 * - Serie 25 cccc: faltaba el adaptador de hoja (H − 3.5).
 * - Serie 62: faltaba el cortaviento (3.4) y, con tramos de 2 y 3 hojas, el marco doble y el
 *   triple salían en una sola lista.
 */
object CalculoCorredizas {

    enum class Base { ANCHO, MODULO, ALTO, FIJO }

    data class Pieza(val lista: String, val base: Base, val mm: Float, val cant: Int)

    /** Vidrio: ancho sobre el módulo (o sobre el ancho, en el paño fijo) y alto sobre el alto. */
    data class Vidrio(val anchoMm: Float, val altoMm: Float, val cant: Int, val base: Base = Base.MODULO)

    data class Modelo(val piezas: List<Pieza>, val vidrios: List<Vidrio>)

    data class Serie(
        val nombre: String,
        /** Modelo de 3 hojas que usa el reparto en tramos. */
        val pat3: String,
        val modelos: Map<String, Modelo>
    ) {
        /** Orden de las listas: el de su primera aparición en los modelos. */
        val orden: List<String> = modelos.values.flatMap { m -> m.piezas.map { it.lista } }.distinct()
    }

    data class Resultado(
        val tramos: List<String>,
        /** Lista del proyecto → piezas `medida = cantidad` (cm, df1), en el orden de la serie. */
        val listas: List<Pair<String, List<String>>>,
        val vidrios: List<String>
    )

    const val PARANTE = "Parante"
    private const val PAFLON = 2.5f

    /** Serie por su nombre en VentanaAl (null si no es de esta familia). */
    fun serie(nombre: String): Serie? = SERIES[nombre]

    /** Opciones del diálogo: "tramos" y los modelos de la serie. */
    fun patrones(nombre: String): Array<String> =
        serie(nombre)?.let { arrayOf("tramos") + it.modelos.keys } ?: arrayOf("tramos")

    fun calcular(nombre: String, ancho: Float, alto: Float, patron: String, divisiones: Int): Resultado? {
        val s = serie(nombre) ?: return null
        if (ancho <= 0f || alto <= 0f) return null
        val esTramos = patron.isBlank() || patron.equals("tramos", ignoreCase = true)
        val tramos = if (esTramos) tramosDe(divisiones.coerceAtLeast(2), s.pat3) else listOf(patron)
        if (tramos.any { it !in s.modelos }) return null
        val div = tramos.sumOf { it.length }
        val nParantes = tramos.size - 1
        val anchoModulo = (ancho - nParantes * PAFLON) / div

        val listas = LinkedHashMap<String, LinkedHashMap<String, Int>>()
        val vidrios = LinkedHashMap<String, Int>()
        fun suma(m: LinkedHashMap<String, Int>, clave: String, q: Int) { if (q > 0) m[clave] = (m[clave] ?: 0) + q }

        for (pat in tramos) {
            val modelo = s.modelos.getValue(pat)
            val tw = pat.length * anchoModulo
            fun medida(base: Base, mm: Float): Float = when (base) {
                Base.ANCHO -> tw + mm / 10f
                Base.MODULO -> tw / pat.length + mm / 10f
                Base.ALTO -> alto + mm / 10f
                Base.FIJO -> mm / 10f
            }
            for (p in modelo.piezas) suma(listas.getOrPut(p.lista) { LinkedHashMap() }, df1(medida(p.base, p.mm)), p.cant)
            for (v in modelo.vidrios) suma(vidrios, "${df1(medida(v.base, v.anchoMm))} x ${df1(alto + v.altoMm / 10f)}", v.cant)
        }
        if (nParantes > 0) suma(listas.getOrPut(PARANTE) { LinkedHashMap() }, df1(alto), nParantes)

        fun lineas(m: Map<String, Int>) = m.entries.map { "${it.key} = ${it.value}" }
        val ordenadas = listas.entries
            .sortedBy { (lista, _) -> s.orden.indexOf(lista).let { if (it < 0) Int.MAX_VALUE else it } }
            .map { (lista, piezas) -> lista to lineas(piezas) }
        return Resultado(tramos, ordenadas, lineas(vidrios))
    }

    /**
     * Reparte `divisiones` (= ceil(ancho/60), como Nova) en tramos de 2–4 hojas, lo más parejo
     * posible: 2 → fc, 3 → `pat3`, 4 → fccf. Ej.: 5 → [pat3, fc], 6 → [pat3, pat3].
     */
    fun tramosDe(divisiones: Int, pat3: String): List<String> {
        val n = divisiones.coerceAtLeast(2)
        val t = ceil(n / 4.0).toInt().coerceAtLeast(1)
        val base = n / t
        val extra = n % t
        return (0 until t).map { i ->
            when (if (i < extra) base + 1 else base) { 2 -> "fc"; 3 -> pat3; else -> "fccf" }
        }
    }

    private fun df1(x: Float): String = NovaCalculos.df1(x)

    // ---------------------------------------------------------------------------------------------
    // Tablas (mm). Cada línea dice de qué columna del Excel sale.

    private fun a(l: String, mm: Float, n: Int) = Pieza(l, Base.ANCHO, mm, n)
    private fun m(l: String, mm: Float, n: Int) = Pieza(l, Base.MODULO, mm, n)
    private fun h(l: String, mm: Float, n: Int) = Pieza(l, Base.ALTO, mm, n)
    private fun k(l: String, mm: Float, n: Int) = Pieza(l, Base.FIJO, mm, n)
    private fun v(ancho: Float, alto: Float, n: Int) = Vidrio(ancho, alto, n)
    private fun modelo(vararg p: Pieza, vidrios: List<Vidrio>) = Modelo(p.toList(), vidrios)

    // Familia de riel (20, 25, 42): rieles + jamba, y por hoja cabezal, zócalo, pierna y traslape.
    private const val RS = "Riel Sup."
    private const val RI = "Riel Inf."
    private const val J = "Jamba"
    private const val RTS = "Riel triple sup."
    private const val RTI = "Riel triple inf."
    private const val JT = "Jamba triple"
    private const val CAB = "Cabezal"
    private const val ZOC = "Zócalo"
    private const val PIE = "Pierna"
    private const val TRA = "Traslape"
    private const val ADA = "Adaptador de hoja"

    /** Serie 20 / 19 (S20-19DSCTOS, hoja MODULACION SERIE 20). */
    private val SERIE_20: Serie = run {
        val rieles = arrayOf(a(RS, -12f, 1), a(RI, -12f, 1), h(J, 0f, 2))
        Serie("Serie 20", "cfc", linkedMapOf(
            "fc" to modelo(*rieles, m(CAB, 1f, 2), m(ZOC, -3f, 1), m(ZOC, 1f, 1),
                h(PIE, -25f, 2), h(TRA, -25f, 2), vidrios = listOf(v(-52f, -98f, 2))),
            "cc" to modelo(*rieles, m(CAB, 1f, 2), m(ZOC, -3f, 2),
                h(PIE, -25f, 2), h(TRA, -25f, 2), vidrios = listOf(v(-52f, -98f, 2))),
            // 3 HOJAS (C/FM/C): fijo al medio, las tres de igual ancho.
            "cfc" to modelo(*rieles, m(CAB, -1f, 2), m(CAB, 29f, 1), m(ZOC, -5f, 2), m(ZOC, 29f, 1),
                h(PIE, -25f, 2), h(TRA, -25f, 4), vidrios = listOf(v(-54f, -98f, 2), v(-6f, -98f, 1))),
            "fccf" to modelo(*rieles, m(CAB, 4f, 4), m(ZOC, 0f, 2), m(ZOC, 4f, 2),
                h(PIE, -25f, 4), h(TRA, -25f, 4), h(ADA, -43f, 1), vidrios = listOf(v(-49f, -98f, 4))),
            // El Excel pone el vidrio en A/4 + 49 (columna AJ): signo cambiado, va − 49 como en fccf.
            "cccc" to modelo(*rieles, m(CAB, 4f, 4), m(ZOC, 0f, 4),
                h(PIE, -25f, 4), h(TRA, -25f, 4), h(ADA, -43f, 1), vidrios = listOf(v(-49f, -98f, 4)))
        ))
    }

    /** Serie 25 / 26 (S25-26DSCTOS): riel inferior convencional, hoja con cabezal. */
    private val SERIE_25: Serie = run {
        val doble = arrayOf(a(RS, -16f, 1), a(RI, -16f, 1), h(J, 0f, 2))
        val triple = arrayOf(a(RTS, -16f, 1), a(RTI, -16f, 1), h(JT, 0f, 2))
        Serie("Serie 25", "cfc", linkedMapOf(
            "fc" to modelo(*doble, m(CAB, 4f, 2), m(ZOC, 2f, 1), m(ZOC, 4f, 1),
                h(PIE, -35f, 2), h(TRA, -35f, 2), vidrios = listOf(v(-64f, -125f, 2))),
            "cc" to modelo(*doble, m(CAB, 4f, 2), m(ZOC, 2f, 2),
                h(PIE, -35f, 2), h(TRA, -35f, 2), vidrios = listOf(v(-64f, -125f, 2))),
            // 3 HOJAS (C/FM/C).
            "cfc" to modelo(*doble, m(CAB, 0f, 2), m(CAB, 42f, 1), m(ZOC, -2f, 2), m(ZOC, 42f, 1),
                h(PIE, -35f, 2), h(TRA, -35f, 4), vidrios = listOf(v(-68f, -125f, 2), v(-13f, -125f, 1))),
            // 3 HOJAS (F/C/F): la corrediza cruza un fijo; el otro lleva adaptador.
            "fcf" to modelo(*doble, m(CAB, -1f, 3), m(ZOC, -3f, 1), m(ZOC, -1f, 2),
                h(PIE, -35f, 4), h(TRA, -35f, 2), h(ADA, -35f, 1),
                vidrios = listOf(v(-69f, -125f, 2), v(-82f, -125f, 1))),
            "fccf" to modelo(*doble, m(CAB, 8f, 4), m(ZOC, 6f, 2), m(ZOC, 8f, 2),
                h(PIE, -35f, 4), h(TRA, -35f, 4), h(ADA, -35f, 1), vidrios = listOf(v(-60f, -125f, 4))),
            "cccc" to modelo(*doble, m(CAB, 8f, 4), m(ZOC, 6f, 4),
                h(PIE, -35f, 4), h(TRA, -35f, 4), h(ADA, -35f, 1), vidrios = listOf(v(-60f, -125f, 4))),
            // Riel triple.
            "fcc" to modelo(*triple, m(CAB, 14.07f, 3), m(ZOC, 12.07f, 2), m(ZOC, 14.07f, 1),
                h(PIE, -35f, 2), h(TRA, -35f, 4), vidrios = listOf(v(-52.33f, -123f, 2), v(-39.33f, -123f, 1))),
            "ccc" to modelo(*triple, m(CAB, 14.07f, 3), m(ZOC, 12.07f, 3),
                h(PIE, -35f, 2), h(TRA, -35f, 4), vidrios = listOf(v(-52.33f, -123f, 2), v(-39.33f, -123f, 1))),
            "fccccf" to modelo(*triple, m(CAB, 18.23f, 6), m(ZOC, 16.23f, 4), m(ZOC, 18.23f, 2),
                h(PIE, -35f, 4), h(TRA, -35f, 8), h(ADA, -35f, 1),
                vidrios = listOf(v(-48.17f, -123f, 4), v(-35.17f, -123f, 2))),
            "cccccc" to modelo(*triple, m(CAB, 18.23f, 6), m(ZOC, 16.23f, 6),
                h(PIE, -35f, 4), h(TRA, -35f, 8), h(ADA, -35f, 1),
                vidrios = listOf(v(-48.17f, -123f, 4), v(-35.17f, -123f, 2)))
        ))
    }

    // Serie 42 (SVL42ML42DCTO, hoja MODULACION VL42-ML42): la hoja lleva marco lateral y traslape.
    private const val LAT = "Lateral"
    private const val RM = "Riel mampara"
    private const val RTM = "Riel triple mampara"
    private const val ZM = "Zócalo mampara"
    private const val TM = "Traslape mampara"

    /** VL42, tipo VENTANA. */
    private val SERIE_42: Serie = run {
        val doble = arrayOf(a(RS, -14f, 1), a(RI, -14f, 1), h(J, 0f, 2))
        val triple = arrayOf(a(RTS, -18f, 1), a(RTI, -18f, 1), h(JT, 0f, 2))
        val dos = arrayOf(m(CAB, 6.4f, 2), m(ZOC, 6.4f, 2), h(LAT, -32f, 2), h(TRA, -32f, 2))
        val tres = arrayOf(m(CAB, 14.06f, 3), m(ZOC, 14.06f, 3), h(LAT, -32f, 2), h(TRA, -32f, 4))
        val cuatro = arrayOf(m(CAB, 9.575f, 4), m(ZOC, 9.575f, 4), h(LAT, -32f, 4), h(TRA, -32f, 4), h(ADA, -55f, 1))
        // El T/T de F/C/C va a H − 110.33 en el Excel; las demás hojas a H − 110.
        val vTres = listOf(v(-51.33f, -110f, 2), v(-38.33f, -110f, 1))
        Serie("Serie 42", "fcc", linkedMapOf(
            "fc" to modelo(*doble, *dos, vidrios = listOf(v(-59f, -110f, 2))),
            "cc" to modelo(*doble, *dos, vidrios = listOf(v(-59f, -110f, 2))),
            "fcc" to modelo(*triple, *tres, vidrios = vTres),
            "ccc" to modelo(*triple, *tres, vidrios = vTres),
            "fccf" to modelo(*doble, *cuatro, vidrios = listOf(v(-55.825f, -110f, 4))),
            "cccc" to modelo(*doble, *cuatro, vidrios = listOf(v(-55.825f, -110f, 4)))
        ))
    }

    /** ML42, tipo MAMPARA: riel de mampara abajo, zócalo y traslape de mampara. */
    private val SERIE_42_MAMPARA: Serie = run {
        val doble = arrayOf(a(RS, -14f, 1), a(RM, -14f, 1), h(J, 0f, 2))
        val triple = arrayOf(a(RTS, -18f, 1), a(RTM, -18f, 1), h(JT, 0f, 2))
        val dos = arrayOf(m(CAB, 12.9f, 2), m(ZM, 12.9f, 2), h(LAT, -22f, 2), h(TM, -22f, 2))
        val tres = arrayOf(m(CAB, 22.73f, 3), m(ZM, 22.73f, 3), h(LAT, -22f, 2), h(TM, -22f, 4))
        val cuatro = arrayOf(m(CAB, 16.075f, 4), m(ZM, 16.075f, 4), h(LAT, -22f, 4), h(TM, -22f, 4), h(ADA, -48f, 1))
        val vTres = listOf(v(-56.66f, -112f, 2), v(-55.66f, -112f, 1))
        // F/C/C/F: el Excel pone el fijo en A/4 − 63.325 y la corrediza en A/4 − 63.25; van iguales.
        val vCuatro = listOf(v(-63.25f, -112f, 4))
        Serie("Serie 42 mampara", "fcc", linkedMapOf(
            "fc" to modelo(*doble, *dos, vidrios = listOf(v(-66.5f, -112f, 2))),
            "cc" to modelo(*doble, *dos, vidrios = listOf(v(-66.5f, -112f, 2))),
            "fcc" to modelo(*triple, *tres, vidrios = vTres),
            "ccc" to modelo(*triple, *tres, vidrios = vTres),
            "fccf" to modelo(*doble, *cuatro, vidrios = vCuatro),
            "cccc" to modelo(*doble, *cuatro, vidrios = vCuatro)
        ))
    }

    // Familia de marco de hoja (62, 80, 46): marco a 45º en los 4 lados y la hoja con marco propio.
    private const val MD = "Marco"
    private const val MT = "Marco triple corrediza"
    private const val BAS = "Bastidor"
    private const val TRH = "Traslape hoja"
    private const val CV = "Cortaviento"

    /** Datos de una serie de marco de hoja para 2, 3 y 4 (y 6) hojas, en mm. */
    private class Hojas(
        val horizontal: Float, val traslape: Int, val vidrioAncho: Float,
        val cortaviento: List<Pair<Float, Int>> = emptyList()
    )

    private fun marcoDeHoja(n: Int, marco: String, d: Hojas, vertical: Float, vidrioAlto: Float): Modelo =
        Modelo(
            listOf(
                a(marco, 0f, 2), h(marco, 0f, 2),
                m(BAS, d.horizontal, 2 * n), h(BAS, vertical, 2 * n),
                h(TRH, vertical, d.traslape)
            ) + (if (n >= 4) listOf(h(ADA, vertical, 1)) else emptyList()) +
                d.cortaviento.map { (mm, q) -> k(CV, mm, q) },
            listOf(v(d.vidrioAncho, vidrioAlto, n))
        )

    private fun serieMarcoDeHoja(
        nombre: String, vertical: Float, vidrioAlto: Float,
        dos: Hojas, tres: Hojas, cuatro: Hojas, seis: Hojas? = null, fijo: Modelo? = null
    ): Serie {
        val modelos = linkedMapOf(
            "fc" to marcoDeHoja(2, MD, dos, vertical, vidrioAlto),
            "cc" to marcoDeHoja(2, MD, dos, vertical, vidrioAlto),
            "fcc" to marcoDeHoja(3, MT, tres, vertical, vidrioAlto),
            "ccc" to marcoDeHoja(3, MT, tres, vertical, vidrioAlto),
            "fccf" to marcoDeHoja(4, MD, cuatro, vertical, vidrioAlto),
            "cccc" to marcoDeHoja(4, MD, cuatro, vertical, vidrioAlto)
        )
        if (seis != null) {
            modelos["fccccf"] = marcoDeHoja(6, MT, seis, vertical, vidrioAlto)
            modelos["cccccc"] = marcoDeHoja(6, MT, seis, vertical, vidrioAlto)
        }
        if (fijo != null) modelos["f"] = fijo
        return Serie(nombre, "fcc", modelos)
    }

    /** Serie 62 / VL48 (S62-VL48, hoja MODULACION VL48-62). */
    private val SERIE_62 = serieMarcoDeHoja(
        "Serie 62 europea", vertical = -50f, vidrioAlto = -154f,
        dos = Hojas(8f, 2, -95f, listOf(34f to 2)),
        tres = Hojas(26f, 4, -77f, listOf(34f to 4)),
        cuatro = Hojas(19f, 4, -84f, listOf(34f to 4))
    )

    /**
     * Serie 80 / ML48 (S80-ML48DSCTO, hoja MODULACION ML48-80). Seis hojas con marco triple. El Excel
     * da 4 vidrios en F/C/C/C/C/F (son 6 hojas) y el de C/C/C sobre el alto (H/3 − 97); aquí van 6 y
     * A/3 − 97.
     */
    private val SERIE_80 = serieMarcoDeHoja(
        "Serie 80", vertical = -56f, vidrioAlto = -190f,
        dos = Hojas(15f, 2, -119f, listOf(33f to 2)),
        tres = Hojas(37f, 4, -97f, listOf(33f to 2, 43f to 2)),
        cuatro = Hojas(27f, 4, -107f, listOf(33f to 4)),
        seis = Hojas(45f, 8, -89f, listOf(33f to 4, 43f to 4))
    )

    /** Paño fijo de la 46: marco VL4607 y perfil VL4608 (el Excel los rotula "jamba triple" y "riel triple mampara"). */
    private val FIJO_46 = Modelo(
        listOf(
            a("Marco fijo", 0f, 2), h("Marco fijo", 0f, 2),
            a("Perfil VL4608", -55f, 2), h("Perfil VL4608", -93f, 2)
        ),
        listOf(Vidrio(-67f, -67f, 1, Base.ANCHO))
    )

    /** Serie VL46 (SVL46DSCTOS). El traslape es el VL4603 (el Excel lo rotula "riel inferior triple"). */
    private val SERIE_46 = serieMarcoDeHoja(
        "Serie 46", vertical = -66f, vidrioAlto = -167f,
        dos = Hojas(-1f, 2, -101f),
        tres = Hojas(19f, 4, -81f),
        cuatro = Hojas(14f, 4, -87f),
        fijo = FIJO_46
    )

    /**
     * Serie ML46 (SML46DSCTOS). En 3 hojas el Excel pide marco doble y triple a la vez; aquí va solo el
     * triple, como en la VL46.
     */
    private val SERIE_46_MAMPARA = serieMarcoDeHoja(
        "Serie 46 mampara", vertical = -77f, vidrioAlto = -200f,
        dos = Hojas(-2f, 2, -124f),
        tres = Hojas(26f, 4, -98f),
        cuatro = Hojas(17f, 4, -105f),
        fijo = FIJO_46
    )

    private val SERIES: Map<String, Serie> = listOf(
        SERIE_20, SERIE_25, SERIE_62, SERIE_42, SERIE_42_MAMPARA, SERIE_46, SERIE_46_MAMPARA, SERIE_80
    ).associateBy { it.nombre }
}
