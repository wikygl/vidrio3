package crystal.crystal.taller.nova

import crystal.crystal.taller.nova.NovaCorrediza.ModuloDesigual

/**
 * Cálculos de materiales para divisiones desiguales (anchos por módulo).
 * Complementa NovaCalculos cuando los módulos no tienen todos el mismo ancho.
 *
 * Fórmula clave:
 *   ajuste_APA = (cruceTotal - 2.5 * nParantes) / divisiones
 *   ajuste_INA = cruceTotal / divisiones
 *   uFijo_i = ancho_modulo_i + ajuste
 */
object NovaCalculosDesiguales {

    // ==================== UTILIDADES ====================

    /** Cuenta transiciones f↔c (excluyendo fronteras con parantes). */
    fun contarTransiciones(modulos: List<ModuloDesigual>, parantes: List<Int>): Int {
        var transiciones = 0
        for (i in 0 until modulos.size - 1) {
            // Si hay un parante entre i y i+1, no es transición de cruce
            if (parantes.contains(i + 1)) continue
            if (modulos[i].tipo != modulos[i + 1].tipo) transiciones++
        }
        return transiciones
    }

    /** Agrupa medidas iguales (±0.05): "65.3 = 3\n50.2 = 2" */
    fun agrupar(medidas: List<Float>): String {
        if (medidas.isEmpty()) return ""
        val grupos = mutableListOf<Pair<Float, Int>>()
        for (m in medidas) {
            val existente = grupos.indexOfFirst { kotlin.math.abs(it.first - m) < 0.05f }
            if (existente >= 0) {
                grupos[existente] = grupos[existente].copy(second = grupos[existente].second + 1)
            } else {
                grupos.add(m to 1)
            }
        }
        return grupos.joinToString("\n") { "${NovaCalculos.df1(it.first)} = ${it.second}" }
    }

    /** Agrupa medidas 2D (ancho x alto): "60 x 110 = 2\n50 x 110 = 3" */
    fun agrupar2D(medidas: List<Pair<Float, Float>>): String {
        if (medidas.isEmpty()) return ""
        val grupos = mutableListOf<Triple<Float, Float, Int>>()
        for ((a, b) in medidas) {
            val existente = grupos.indexOfFirst {
                kotlin.math.abs(it.first - a) < 0.05f && kotlin.math.abs(it.second - b) < 0.05f
            }
            if (existente >= 0) {
                grupos[existente] = Triple(grupos[existente].first, grupos[existente].second, grupos[existente].third + 1)
            } else {
                grupos.add(Triple(a, b, 1))
            }
        }
        return grupos.joinToString("\n") {
            "${NovaCalculos.df1(it.first)} x ${NovaCalculos.df1(it.second)} = ${it.third}"
        }
    }

    /** Divide módulos en segmentos separados por parantes. */
    fun segmentos(modulos: List<ModuloDesigual>, parantes: List<Int>): List<List<ModuloDesigual>> {
        if (parantes.isEmpty()) return listOf(modulos)
        val result = mutableListOf<List<ModuloDesigual>>()
        var start = 0
        for (p in parantes.sorted()) {
            result.add(modulos.subList(start, p))
            start = p
        }
        result.add(modulos.subList(start, modulos.size))
        return result
    }

    // ==================== AJUSTE DE CRUCE ====================

    fun calcularAjuste(
        modulos: List<ModuloDesigual>,
        parantes: List<Int>,
        cruce: Float,
        tipo: String
    ): Float {
        val transiciones = contarTransiciones(modulos, parantes)
        val cruceTotal = transiciones * cruce
        val nParantes = parantes.size
        return when (tipo) {
            "apa" -> (cruceTotal - 2.5f * nParantes) / modulos.size
            else -> cruceTotal / modulos.size
        }
    }

    // ==================== U PERFILES ====================

    fun calcularU(
        modulos: List<ModuloDesigual>,
        parantes: List<Int>,
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        us: Float,
        cruce: Float,
        tipo: String,
        tubo: Float
    ): String {
        val ajuste = calcularAjuste(modulos, parantes, cruce, tipo)
        val nParantes = parantes.size
        val lines = mutableListOf<String>()

        // U fijos
        val uFijosMedidas = modulos.filter { it.tipo == 'f' }.map { it.ancho + ajuste }
        if (uFijosMedidas.isNotEmpty()) {
            lines.add(agrupar(uFijosMedidas))
        }

        // U parantes (verticales)
        if (us != 0f) {
            val uParante = when (tipo) {
                "apa" -> altoHoja - (us + 0.2f)
                else -> alto - (2 * us)
            }
            val nUParantes = if (modulos.size == 1) 2 else (nParantes * 2) + 2
            lines.add("${NovaCalculos.df1(uParante)} = $nUParantes")
        }

        // U mocheta
        if (alto > altoHoja) {
            val altoMocheta = NovaCalculos.altoMocheta(alto, altoHoja, tubo)
            if (us != 0f) {
                val uMocheta = altoMocheta - us
                val nUMocheta = (nParantes + 1) * 2
                lines.add("${NovaCalculos.df1(uMocheta)} = $nUMocheta")
            }

            // Puentes superiores (horizontales) por segmento
            val segs = segmentos(modulos, parantes)
            val puenteMedidas = segs.map { seg -> seg.sumOf { it.ancho.toDouble() }.toFloat() }
            lines.add(agrupar(puenteMedidas))
        }

        return lines.filter { it.isNotBlank() }.joinToString("\n")
    }

    // ==================== PUENTES Y RIELES ====================

    data class PuentesRielesResult(val puentes: String, val rieles: String)

    fun calcularPuentesYRieles(
        modulos: List<ModuloDesigual>,
        parantes: List<Int>,
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        tipo: String
    ): PuentesRielesResult {
        val segs = segmentos(modulos, parantes)
        val nParantes = parantes.size

        // Ancho de cada segmento
        val anchosSegs = segs.map { seg -> seg.sumOf { it.ancho.toDouble() }.toFloat() }

        // Ajuste por parantes en APA: descontar espacio de parantes
        val anchosAjustados = if (tipo == "apa" && nParantes > 0) {
            val totalParantes = 2.5f * nParantes
            val anchoPuro = ancho - totalParantes
            // Proporcional
            val totalSegs = anchosSegs.sum()
            if (totalSegs > 0f) anchosSegs.map { it / totalSegs * anchoPuro } else anchosSegs
        } else {
            anchosSegs
        }

        // Puentes = ancho del segmento (1 por segmento) + alto del parante
        val puentesMedidas = mutableListOf<Float>()
        puentesMedidas.addAll(anchosAjustados)
        // Parantes verticales al alto completo
        val puentesParantes = if (nParantes > 0) {
            "\n${NovaCalculos.df1(alto)} = $nParantes"
        } else ""

        val textoPuentes = agrupar(puentesMedidas) + puentesParantes

        // Rieles = puente - 0.06 (INA) o puente tal cual (APA)
        val rielesMedidas = when (tipo) {
            "ina", "piv" -> anchosAjustados.map { it - 0.06f }
            else -> anchosAjustados.map { it - 0.06f }
        }
        val textoRieles = agrupar(rielesMedidas)

        return PuentesRielesResult(textoPuentes, textoRieles)
    }

    // ==================== VIDRIOS ====================

    fun calcularVidrios(
        modulos: List<ModuloDesigual>,
        mocheta: List<ModuloDesigual>,
        parantes: List<Int>,
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        us: Float,
        cruce: Float,
        tipo: String,
        tubo: Float
    ): String {
        val ajuste = calcularAjuste(modulos, parantes, cruce, tipo)
        val lines = mutableListOf<String>()

        // Vidrios fijos
        val holgura = if (us == 0f) 1f else 0.2f
        val altoVidrioFijo = altoHoja - (us + holgura)
        val vidriosFijos = modulos.filter { it.tipo == 'f' }.map { mod ->
            val anchoVidrio = mod.ancho + ajuste - 0.4f
            Pair(anchoVidrio, altoVidrioFijo)
        }
        if (vidriosFijos.isNotEmpty()) {
            lines.add(agrupar2D(vidriosFijos))
        }

        // Vidrios corredizos
        val altoVidrioCorre = altoHoja - 3.5f
        val vidriosCorre = modulos.filter { it.tipo == 'c' }.map { mod ->
            val anchoVidrio = mod.ancho + ajuste - 1.4f
            Pair(anchoVidrio, altoVidrioCorre)
        }
        if (vidriosCorre.isNotEmpty()) {
            lines.add(agrupar2D(vidriosCorre))
        }

        // Vidrios mocheta
        if (alto > altoHoja && mocheta.isNotEmpty()) {
            val altoMocheta = NovaCalculos.altoMocheta(alto, altoHoja, tubo)
            val holguraMoch = 0.36f
            val altoVidrioMoch = altoMocheta - holguraMoch
            val vidriosMoch = mocheta.map { mod ->
                val anchoVidrio = mod.ancho - holguraMoch
                Pair(anchoVidrio, altoVidrioMoch)
            }
            lines.add(agrupar2D(vidriosMoch))
        }

        return lines.filter { it.isNotBlank() }.joinToString("\n")
    }

    // ==================== OTROS PERFILES ====================

    data class OtrosResult(
        val portafelpa: String,
        val hache: String,
        val tee: String,
        val tope: String,
        val tuboTxt: String
    )

    fun calcularOtros(
        modulos: List<ModuloDesigual>,
        parantes: List<Int>,
        ancho: Float,
        alto: Float,
        altoHoja: Float,
        us: Float,
        cruce: Float,
        tipo: String,
        tubo: Float
    ): OtrosResult {
        val ajuste = calcularAjuste(modulos, parantes, cruce, tipo)
        val nCorredizas = modulos.count { it.tipo == 'c' }

        // Portafelpa: altoHoja - 1.6 (no varía con ancho)
        val portafelpaVal = altoHoja - 1.6f
        val divDePortas = when {
            modulos.size == 1 -> 0
            modulos.size == 2 || modulos.size == 4 || modulos.size == 8 || modulos.size == 12 ->
                nCorredizas * 3
            modulos.size == 14 -> (nCorredizas * 4) - 2
            else -> nCorredizas * 4
        }
        val portafelpaTxt = if (divDePortas > 0) "${NovaCalculos.df1(portafelpaVal)} = $divDePortas" else ""

        // Hache: misma medida que uFijos por módulo corredizo
        val hacheMedidas = modulos.filter { it.tipo == 'c' }.map { it.ancho + ajuste }
        val hacheTxt = if (hacheMedidas.isNotEmpty()) agrupar(hacheMedidas) else ""

        // Tee [APA]: altoMocheta - us
        val teeTxt = if (tipo == "apa" && alto > altoHoja) {
            val altoMocheta = NovaCalculos.altoMocheta(alto, altoHoja, tubo)
            val teeVal = altoMocheta - us
            if (teeVal > 0f) "${NovaCalculos.df1(teeVal)} = $nCorredizas" else ""
        } else ""

        // Ángulo tope: solo cuando div==2
        val topeTxt = if (modulos.size == 2) "${NovaCalculos.df1(altoHoja - 0.9f)} = 1" else ""

        // Tubo
        val tuboTxt = if (alto > altoHoja) "${NovaCalculos.df1(ancho)} = 1" else ""

        return OtrosResult(portafelpaTxt, hacheTxt, teeTxt, topeTxt, tuboTxt)
    }
}
