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
    // `holgura` es el juego entre la hoja y el marco: el centímetro de siempre, ahora editable.
    fun hojaV(med1: Float, marcoVar: Float, holgura: Float = 1f): Float =
        med1 - (2f * marcoVar + holgura + 0.3f)

    // ── Referencias de panel ─────────────────────────────────────────────────
    fun panelRefV(hH: Float): Float = (hH - 1f - 4f * 0.6f) / 5f
    // Para Lina h: usa med1 con canal fijo
    fun panelAltoH(med1: Float, marco: Float = MARCO, gruna: Float = 0.8f, panelDelgado: Float = 0f): Float =
        panelDelgado.takeIf { it > 0f } ?: ((med1 - 2f * marco - gruna) / 4f)

    fun panelRefH_h(
        med1: Float, marco: Float = MARCO, gruna: Float = 0.8f, panelDelgado: Float = 0f,
        holgura: Float = 1f
    ): Float = med1 - (2f * marco + holgura) - panelAltoH(med1, marco, gruna, panelDelgado) - 0.6f
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
    fun tres(hH: Float, med1: Float, panelRefH: Float, marco: Float = MARCO, holgura: Float = 1f): String {
        val base = hH - 1f
        val anchT = med1 - (marco * 2f + holgura) - 2f * TRES
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

    fun panelB(hH: Float, med1: Float, marco: Float = MARCO, divisiones: Int = 5, gruna: Float = 0.8f, piso: Float = 0f, panelDelgado: Float = 0f, holgura: Float = 1f): String {
        val nDiv = divisiones.coerceAtLeast(1)
        val panelAlto = panelAltoH(med1, marco, gruna, panelDelgado)
        val altoBastidor = altoBastidor(hH, piso)
        val panelRefHB = med1 - (2f * marco + holgura) - panelAlto - 0.7f
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

    fun paflonBBastidor(
        hH: Float, med1: Float, marco: Float = MARCO, piso: Float = 0f, holgura: Float = 1f
    ): String {
        val altoBastidor = altoBastidor(hH, piso)
        val anchP = med1 - (marco * 2f + holgura) - 2f * PAFLON
        return "${df1(altoBastidor)} = 2\n${df1(anchP)} = 2"
    }

    // ════════════════════════════════════════════════════════════════════════
    // Lina c   ──────────────────────────────────────────────────────────────
    // Un solo panel que cubre la hoja entera. Sin gruma: no hay junta que repartir porque no hay
    // dos planchas que juntar, así que el bastidor va igual que en "Lina b" pero sin divisores.
    // ════════════════════════════════════════════════════════════════════════

    // ── Plano técnico del interior ───────────────────────────────────────────
    // Lo que el plano tiene que mostrar es la estructura, no las planchas: una vez atornilladas ya no
    // se ve nada de esto. En "Lina b" son el parante interior que separa las dos columnas y los
    // rellenos horizontales que caen detrás de cada junta de panel.

    /** Largo de los rellenos horizontales de "Lina b". */
    fun largoRellenoB(
        med1: Float, marco: Float = MARCO, gruna: Float = 0.8f, relleno: Float = 3.8f,
        panelDelgado: Float = 0f, bastidor: Float = PAFLON, holgura: Float = 1f
    ): Float {
        val anchoHoja = med1 - 2f * marco - holgura
        val panel = panelAltoH(med1, marco, gruna, panelDelgado)
        return ((anchoHoja - (panel + gruna / 2f)) - (bastidor + relleno / 2f)).coerceAtLeast(0f)
    }

    /**
     * Dónde arranca el parante interior, medido desde el borde interior del bastidor. Sale de restar:
     * el relleno llega hasta su eje, así que el eje está a `anchoHoja - bastidor - largoRelleno` del
     * borde de la hoja, y la pieza empieza medio relleno antes.
     */
    fun parantePosicionB(
        med1: Float, marco: Float = MARCO, gruna: Float = 0.8f, relleno: Float = 3.8f,
        panelDelgado: Float = 0f, bastidor: Float = PAFLON, holgura: Float = 1f
    ): Float {
        val anchoHoja = med1 - 2f * marco - holgura
        val eje = anchoHoja - bastidor -
            largoRellenoB(med1, marco, gruna, relleno, panelDelgado, bastidor, holgura)
        return (eje - relleno / 2f - bastidor).coerceAtLeast(0f)
    }

    /** Alturas de los rellenos horizontales, desde la base de la hoja: caen en cada junta de panel. */
    fun alturasRellenoB(
        hH: Float, divisiones: Int = 5, gruna: Float = 0.8f, piso: Float = 0f
    ): List<Float> {
        val nDiv = divisiones.coerceAtLeast(1)
        if (nDiv <= 1) return emptyList()
        val alto = altoBastidor(hH, piso)
        val panel = (alto - (nDiv - 1) * gruna) / nDiv
        return (1 until nDiv).map { i -> panel * i + gruna * (i - 1) }
    }

    /** Vacío del bastidor: lo que queda dentro del cuadro de paflón. */
    fun interiorContraplacado(
        hH: Float, med1: Float, marco: Float = MARCO, piso: Float = 0f, holgura: Float = 1f
    ): Pair<Float, Float> {
        val ancho = (med1 - (2f * marco + holgura) - 2f * PAFLON).coerceAtLeast(0f)
        val alto = (altoBastidor(hH, piso) - 2f * PAFLON).coerceAtLeast(0f)
        return ancho to alto
    }

    /**
     * Cuántas divisiones lleva la estructura interior. Como esta variante no pide un número de
     * divisiones, se calcula: se agregan travesaños hasta que cada tramo baje de 40, que es lo que
     * aguanta la plancha sin pandearse.
     */
    fun divisionesContraplacado(altoInterno: Float, estructura: Float, maxTramo: Float = 40f): Int {
        if (altoInterno <= 0f || maxTramo <= 0f) return 1
        var n = 1
        while (n < 40 && ((altoInterno - (n - 1) * estructura) / n) >= maxTramo) n++
        return n
    }

    /** Alto de cada tramo entre travesaños. */
    fun tramoContraplacado(altoInterno: Float, estructura: Float, nDiv: Int): Float =
        if (nDiv <= 0) 0f else ((altoInterno - (nDiv - 1) * estructura) / nDiv).coerceAtLeast(0f)

    /** Travesaños de la estructura: (divisiones - 1) piezas del ancho del vacío. */
    fun estructuraContraplacado(
        hH: Float, med1: Float, marco: Float = MARCO, piso: Float = 0f,
        estructura: Float = 3.8f, maxTramo: Float = 40f, holgura: Float = 1f
    ): String {
        val (ancho, alto) = interiorContraplacado(hH, med1, marco, piso, holgura)
        if (ancho <= 0f || alto <= 0f) return ""
        val n = divisionesContraplacado(alto, estructura, maxTramo)
        val travesanos = n - 1
        return if (travesanos > 0) "${df1(ancho)} = $travesanos" else ""
    }

    /**
     * Cotas del plano, acumuladas desde la base de la hoja: el horizontal de abajo del bastidor,
     * cada travesaño y el horizontal de arriba. Es lo que se marca sobre el parante al armar.
     */
    fun cotasContraplacado(
        hH: Float, med1: Float, marco: Float = MARCO, piso: Float = 0f,
        estructura: Float = 3.8f, maxTramo: Float = 40f, holgura: Float = 1f
    ): List<Float> {
        val (_, alto) = interiorContraplacado(hH, med1, marco, piso, holgura)
        if (alto <= 0f) return emptyList()
        val n = divisionesContraplacado(alto, estructura, maxTramo)
        val tramo = tramoContraplacado(alto, estructura, n)
        val travesanos = (0 until n - 1).map { i -> PAFLON + (i + 1) * tramo + i * estructura }
        return listOf(PAFLON) + travesanos + listOf(PAFLON + alto)
    }

    /** El panel es la hoja completa: su ancho útil por el alto del bastidor. */
    fun panelCompleto(
        hH: Float, med1: Float, marco: Float = MARCO, piso: Float = 0f, holgura: Float = 1f
    ): String {
        val alto = altoBastidor(hH, piso)
        val ancho = med1 - (2f * marco + holgura)
        if (alto <= 0f || ancho <= 0f) return ""
        return "${df1(ancho)} x ${df1(alto)} = 1"
    }

    fun paflonBInterno(
        hH: Float, med1: Float, marco: Float = MARCO, divisiones: Int = 5, gruna: Float = 0.8f,
        materialInterno: Float = PAFLON, piso: Float = 0f, panelDelgado: Float = 0f,
        bastidor: Float = PAFLON, holgura: Float = 1f
    ): String {
        val nDiv = divisiones.coerceAtLeast(1)
        val altoBastidor = altoBastidor(hH, piso)
        val altoInterno = altoBastidor - 2f * bastidor
        val anchoHoja = med1 - 2f * marco - holgura
        // El ancho del panel delgado sale de panelAltoH, la misma referencia que usan los paneles:
        // con una puerta de 70 son 16.2. Antes se tomaba anchoHoja / 4 = 16.15 y no coincidían.
        val anchoPanelDelgado = panelAltoH(med1, marco, gruna, panelDelgado)
        val anchInterno = (anchoHoja - (anchoPanelDelgado + gruna / 2f)) - (bastidor + materialInterno / 2f)
        val nGrunas = (nDiv - 1).coerceAtLeast(0)
        return "${df1(altoInterno)} = 1\n${df1(anchInterno)} = $nGrunas"
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
