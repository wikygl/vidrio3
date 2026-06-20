package crystal.crystal.taller.puerta.logica

object CalculosLina {
    // ── Constantes ──────────────────────────────────────────────────────────
    const val HOJA = 199f
    private const val MARCO = 2.2f       // canal fijo en variantes h/b
    private const val MARCO_V = 2.0f     // marco plegado fijo
    private const val PAFLON = 8.25f
    private const val TOPE_CONST = 1.5f
    private const val TRES = 3.0f
    private const val TRES_DOS = 3.0f
    private const val TUBO = 2.5f        // tubo 2⅜ × 1

    private fun df1(v: Float) = CalculosPuerta.df1(v)

    // ── Alto efectivo de hoja ────────────────────────────────────────────────
    // esPlegado: variante Lina p; hHoja: valor de etHoja (0 = no ingresado)
    fun hojaH(med2: Float, hHoja: Float, esPlegado: Boolean, marco: Float = MARCO): Float = when {
        hHoja > 0f && hHoja < med2 -> hHoja
        esPlegado -> if (hHoja >= med2) med2 - 3f else HOJA + 0.2f
        else      -> if (hHoja >= med2) med2 - marco else HOJA
    }

    fun hojaHConPiso(med2: Float, hHoja: Float, piso: Float, esPlegado: Boolean, marco: Float = MARCO): Float {
        if (esPlegado) return hojaH(med2, hHoja, true, marco)
        return CalculosPuerta.hPuente(med2, hHoja, piso, HOJA, marco)
    }

    fun altoBastidor(hPuente: Float, piso: Float = 0f): Float =
        CalculosPuerta.parante(hPuente, piso)

    // ── Ancho efectivo de hoja (Lina p usa marcoVar) ──────────────────────
    fun hojaV(med1: Float, marcoVar: Float): Float =
        med1 - (2f * marcoVar + 1f + 0.3f)

    // ── Referencias de panel ─────────────────────────────────────────────────
    fun panelRefV(hH: Float): Float = (hH - 1f - 4f * 0.6f) / 5f
    // Para Lina h: usa med1 con canal fijo
    fun panelAltoH(med1: Float, marco: Float = MARCO, gruna: Float = 0.8f, panelDelgado: Float = 0f): Float =
        panelDelgado.takeIf { it > 0f } ?: ((med1 - 2f * marco - gruna) / 4f)

    fun panelRefH_h(med1: Float, marco: Float = MARCO, gruna: Float = 0.8f, panelDelgado: Float = 0f): Float =
        med1 - (2f * marco + 1f) - panelAltoH(med1, marco, gruna, panelDelgado) - 0.6f
    // Para Lina p: usa hojaV
    fun panelRefH_p(hV: Float): Float = hV - 30f - 0.6f

    // ════════════════════════════════════════════════════════════════════════
    // Lina h   ──────────────────────────────────────────────────────────────
    // ════════════════════════════════════════════════════════════════════════

    // tvMarco
    fun canal(med1: Float, med2: Float, marco: Float = MARCO): String =
        "${df1(med1 - 2f * marco)} = 1\n${df1(med2)} = 2"

    // tvTubo
    fun tuboPuente(med1: Float, marco: Float = MARCO): String =
        "${df1(med1 - 2f * marco)} = 1"

    // tvPaflon: Tubo □ 3cm
    fun tres(hH: Float, med1: Float, panelRefH: Float, marco: Float = MARCO): String {
        val base = hH - 1f
        val anchT = med1 - (marco * 2f + 1f) - 2f * TRES
        val medCell = (panelRefH - 21f) / 2f
        return buildString {
            append("${df1(base)} = 2\n")
            append("${df1(anchT)} = 2\n")
            append("${df1(base - TRES * 2f)} = 1\n")
            append("${df1(anchT - 15f)} = 2\n")
            append("${df1(medCell - TRES)} = 4\n")
            append("${df1(medCell - 1.2f)} = 2")
        }
    }

    // tvJunki: Tubo □ 1½ + Tope
    fun tresOcho(hH: Float, panelRefV: Float): String =
        "${df1(hH - 1.07f - (panelRefV * 2f + 1.2f))} = 2\n21 = 2"

    fun junquilloMocheta(med1: Float, med2: Float, hH: Float, marco: Float = MARCO, junki: Float = 0f): String {
        val mocheta = med2 - (hH + marco + TUBO)
        return if (mocheta > 0f) {
            "${df1(med1 - 2f * marco)} = 2\n${df1(mocheta - 2f * junki)} = 2"
        } else {
            ""
        }
    }

    fun topeComun(med1: Float, hH: Float, marco: Float = MARCO): String =
        "${df1(med1 - 2f * marco)} = 1\n${df1(hH)} = 2"

    fun tope(hH: Float, med1: Float, med2: Float, marco: Float = MARCO): String {
        val ultim = med2 - (hH + TUBO + marco + 2f * TOPE_CONST)
        return "${df1(hH)} = 2\n${df1(med1 - 2f * marco)} = 3\n${df1(ultim)} = 2"
    }

    // tvVidrios: Panel + vidrio
    fun panelH(hH: Float, panelRefV: Float, panelRefH: Float, med1: Float, marco: Float = MARCO, gruna: Float = 0.8f, panelDelgado: Float = 0f): String {
        val medCell = (panelRefH - 21f) / 2f
        val panelAlto = panelAltoH(med1, marco, gruna, panelDelgado)
        return buildString {
            append("${df1(hH - 1.2f)} x ${df1(panelAlto)} = 2\n")
            append("${df1(panelRefH)} x ${df1(panelRefV)} = 4\n")
            append("${df1(medCell)} x ${df1(panelRefV)} = 12")
        }
    }

    fun panelB(hH: Float, med1: Float, marco: Float = MARCO, divisiones: Int = 5, gruna: Float = 0.8f, piso: Float = 0f, panelDelgado: Float = 0f): String {
        val nDiv = divisiones.coerceAtLeast(1)
        val panelAlto = panelAltoH(med1, marco, gruna, panelDelgado)
        val altoBastidor = altoBastidor(hH, piso)
        val panelRefHB = med1 - (2f * marco + 1f) - panelAlto - 0.7f
        val panelRefV = (altoBastidor - (nDiv - 1) * gruna) / nDiv
        return buildString {
            append("${df1(altoBastidor)} x ${df1(panelAlto)} = 2\n")
            append("${df1(panelRefHB)} x ${df1(panelRefV)} = ${nDiv * 2}")
        }
    }

    fun vidrioH(hH: Float, med1: Float, med2: Float, marco: Float = MARCO): String {
        if (hH >= med2) return ""
        val anchV = med1 - 2f * marco - 0.4f
        val altV = med2 - (hH + TUBO + 1f + marco + 0.4f)
        return "${df1(anchV)} x ${df1(altV)} = 1"
    }

    // ════════════════════════════════════════════════════════════════════════
    // Lina b   ──────────────────────────────────────────────────────────────
    // ════════════════════════════════════════════════════════════════════════

    // tvPaflon
    fun paflonB(hH: Float, med1: Float, marco: Float = MARCO, divisiones: Int = 5, gruna: Float = 0.8f, materialInterno: Float = PAFLON, piso: Float = 0f, panelDelgado: Float = 0f): String {
        return "${paflonBBastidor(hH, med1, marco, piso)}\n${paflonBInterno(hH, med1, marco, divisiones, gruna, materialInterno, piso, panelDelgado)}"
    }

    fun paflonBBastidor(hH: Float, med1: Float, marco: Float = MARCO, piso: Float = 0f): String {
        val altoBastidor = altoBastidor(hH, piso)
        val anchP = med1 - (marco * 2f + 1f) - 2f * PAFLON
        return "${df1(altoBastidor)} = 2\n${df1(anchP)} = 2"
    }

    fun paflonBInterno(hH: Float, med1: Float, marco: Float = MARCO, divisiones: Int = 5, gruna: Float = 0.8f, materialInterno: Float = PAFLON, piso: Float = 0f, panelDelgado: Float = 0f): String {
        val nDiv = divisiones.coerceAtLeast(1)
        val altoBastidor = altoBastidor(hH, piso)
        val altoInterno = altoBastidor - 2f * PAFLON
        val anchoHoja = med1 - 2f * marco - 1f
        val anchoPanelDelgado = panelDelgado.takeIf { it > 0f } ?: (anchoHoja / 4f)
        val anchInterno = (anchoHoja - (anchoPanelDelgado + gruna / 2f)) - (PAFLON + materialInterno / 2f)
        val nGrunas = (nDiv - 1).coerceAtLeast(0)
        return "${df1(altoInterno)} = 1\n${df1(anchInterno)} = $nGrunas"
    }

    fun paflon(hH: Float, med1: Float): String {
        val anchP = med1 - (MARCO * 2f + 1f) - 2f * PAFLON
        return "${df1(hH - 1f)} = 2\n${df1(anchP)} = 2"
    }

    fun riel(hH: Float, med1: Float): String {
        val anchP = med1 - (MARCO * 2f + 1f) - 2f * PAFLON
        return "${df1(hH - 1f - PAFLON * 2f)} = 2\n${df1(anchP)} = 2"
    }

    // tvJunki: Tubo □ 1 + Tope
    fun unoB(hH: Float, med1: Float): String {
        val anchP = med1 - (MARCO * 2f + 1f) - 2f * PAFLON
        return "${df1(hH - 1f - 2f * PAFLON)} = 1\n${df1(anchP - 12f - TUBO)} = 4"
    }

    // tvVidrios
    fun vidrioB(hH: Float, med1: Float, med2: Float): String {
        val anchP = med1 - (MARCO * 2f + 1f) - 2f * PAFLON
        val anchV2 = anchP - 0.5f
        val altV2 = hH - 1f - PAFLON * 2f - 0.4f
        return if (hH < med2) {
            val anchV1 = med1 - 2f * MARCO - 0.4f
            val altV1 = med2 - (HOJA + TUBO + 1f + MARCO + 0.4f)
            "${df1(anchV1)} x ${df1(altV1)} = 1\n${df1(anchV2)} x ${df1(altV2)} = 1"
        } else {
            "${df1(anchV2)} x ${df1(altV2)} = 1"
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Lina p   ──────────────────────────────────────────────────────────────
    // ════════════════════════════════════════════════════════════════════════

    // tvMarco: Marco Plegado + Contramarco
    fun marcoPlegado(med1: Float, med2: Float): String =
        "${df1(med1)} = 1\n${df1(med2 - MARCO_V)} = 2"

    fun contraMarco(med1: Float, med2: Float, marco: Float = MARCO): String =
        "${df1(med1)} = 1\n${df1(med2 - marco)} = 2"

    // tvPaflon: Bandejas + Fierro □ 3¼ + Platina
    fun bandejas(hH: Float, med1: Float, med2: Float, marcoVar: Float, hV: Float): String {
        val hBase = hH - 1.2f
        val cell = (hBase - TRES_DOS * 2f - TRES_DOS * 4f) / 5f
        val altB = hV - (TRES_DOS * 2f + 28.8f)
        val anchBig = med1 - 2f * marcoVar
        val altBig = med2 - (hBase + marcoVar + TRES_DOS + 1.5f)
        return buildString {
            append("25.8 x ${df1(hBase - TRES_DOS * 2f)} = 1\n")
            append("${df1(cell)} x ${df1(altB)} = 5\n")
            append("${df1(anchBig)} x ${df1(altBig)} = 1")
        }
    }

    fun fierroTresDos(hH: Float, med1: Float, marcoVar: Float, hV: Float): String {
        val hBase = hH - 1.2f
        val altB = hV - (TRES_DOS * 2f + 28.8f)
        return buildString {
            append("${df1(hBase)} = 2\n")
            append("${df1(hBase - TRES_DOS * 2f)} = 1\n")
            append("${df1(hV)} = 2\n")
            append("${df1(altB)} = 4\n")
            append("${df1(med1 - 2f * marcoVar)} = 1\n")
            append("9 = 2")
        }
    }

    fun platina(hH: Float): String = "${df1(hH - 1.2f)} = 2"

    // tvVidrios: Panel + Plancha
    fun panelP(hH: Float, med1: Float, med2: Float, panelRefV: Float, panelRefH: Float): String {
        val sobra = med2 - (MARCO_V + 1.5f + hH)
        val anchP = med1 - MARCO_V * 2f
        return buildString {
            append("${df1(hH - 1.2f)} x 30 = 2\n")
            append("${df1(panelRefH)} x ${df1(panelRefV - 0.1f)} = 8\n")
            append("${df1(sobra)} x ${df1(anchP)} = 2")
        }
    }

    fun plancha(hH: Float, med1: Float, med2: Float, marcoVar: Float, hV: Float): String {
        val hBase = hH - 1.2f
        val cell = (hBase - TRES_DOS * 2f - TRES_DOS * 4f) / 5f + 2f
        val altB = hV - (TRES_DOS * 2f + 28.8f) + 2f
        val anchBig = med1 - 2f * marcoVar + 2f
        val altBig = med2 - (hBase + marcoVar + TRES_DOS + 1.5f) + 2f
        return buildString {
            append("27.8 x ${df1(hBase - TRES_DOS * 2f + 2f)} = 1\n")
            append("${df1(cell)} x ${df1(altB)} = 5\n")
            append("${df1(anchBig)} x ${df1(altBig)} = 1")
        }
    }
}
