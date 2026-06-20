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

    /**
     * Agrupa por el valor MOSTRADO (df1): medidas que se ven iguales se cuentan juntas, aunque
     * difieran en crudo (p. ej. 50.42 de un tramo y 50.35 de otro, ambos se muestran 50.4). Antes
     * agrupaba por crudo con tolerancia 0.05 y dejaba dos líneas "50.4" separadas.
     */
    fun agrupar(medidas: List<Float>): String = agruparConCantidad(medidas, 1)

    private fun agruparConCantidad(medidas: List<Float>, cantidadPorMedida: Int): String {
        if (medidas.isEmpty() || cantidadPorMedida <= 0) return ""
        val grupos = linkedMapOf<String, Int>()
        for (m in medidas) {
            val key = NovaCalculos.df1(m)
            grupos[key] = (grupos[key] ?: 0) + cantidadPorMedida
        }
        return grupos.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    private fun esPuenteMultipleOGorrito(puente: String): Boolean {
        val p = puente.lowercase().trim()
        return p.contains("multi") || p.contains("múlt") || p.contains("mÃºlt") || p.contains("gorrito") || p.contains("ltiple")
    }

    fun textoUAnchoMochetaPorTramos(
        modulos: List<ModuloDesigual>,
        parantes: List<Int>,
        ancho: Float,
        tipo: String,
        tubo: Float,
        puente: String
    ): String {
        val segs = segmentos(modulos, parantes)
        val anchosSegmento = segs.map { seg -> seg.sumOf { it.ancho.toDouble() }.toFloat() }
        if (anchosSegmento.isEmpty()) return ""

        return if (tipo == "apa") {
            // El ancho de la mocheta de cada tramo es el ancho PROPIO del tramo (suma de sus
            // módulos), no una redistribución del total: cada tramo es independiente.
            val cantidadPorTramo = if (esPuenteMultipleOGorrito(puente)) 1 else 2
            agruparConCantidad(anchosSegmento, cantidadPorTramo)
        } else {
            agrupar(anchosSegmento)
        }
    }

    fun textoTramosUnitario(
        modulos: List<ModuloDesigual>,
        parantes: List<Int>,
        ancho: Float,
        tipo: String,
        tubo: Float,
        puente: String
    ): String {
        val segs = segmentos(modulos, parantes)
        val anchosSegmento = segs.map { seg -> seg.sumOf { it.ancho.toDouble() }.toFloat() }
        if (anchosSegmento.isEmpty()) return ""
        // Ancho propio de cada tramo (1 por tramo), sin redistribuir por el total.
        return agruparConCantidad(anchosSegmento, 1)
    }

    /** Agrupa medidas 2D por el valor mostrado (df1 x df1): "60 x 110 = 2\n50 x 110 = 3". */
    fun agrupar2D(medidas: List<Pair<Float, Float>>): String {
        if (medidas.isEmpty()) return ""
        val grupos = linkedMapOf<String, Int>()
        for ((a, b) in medidas) {
            val key = "${NovaCalculos.df1(a)} x ${NovaCalculos.df1(b)}"
            grupos[key] = (grupos[key] ?: 0) + 1
        }
        return grupos.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    /**
     * Anchos sumados por RACHA de corredizas adyacentes (sin cruzar parantes). En `fccf` las dos
     * corredizas forman una sola racha cuyo ancho es la suma de ambas. Para la mocheta INA, donde
     * las corredizas juntas comparten un único vidrio de mocheta.
     */
    fun rachasCorredizasAnchos(modulos: List<ModuloDesigual>, parantes: List<Int>): List<Float> {
        val runs = mutableListOf<Float>()
        for (seg in segmentos(modulos, parantes)) {
            var i = 0
            while (i < seg.size) {
                if (seg[i].tipo == 'c') {
                    var suma = 0f
                    while (i < seg.size && seg[i].tipo == 'c') { suma += seg[i].ancho; i++ }
                    runs.add(suma)
                } else i++
            }
        }
        return runs
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

    private fun cantidadPortafelpas(modulos: List<ModuloDesigual>, parantes: List<Int>): Int {
        val segs = segmentos(modulos, parantes)
        var total = 0
        for (seg in segs) {
            total += seg.count { it.tipo == 'c' } * 2
            for (i in seg.indices) {
                if (seg[i].tipo != 'f') continue
                if (i > 0 && seg[i - 1].tipo == 'c') total += 1
                if (i < seg.lastIndex && seg[i + 1].tipo == 'c') total += 1
            }
        }
        return total
    }

    // ==================== AJUSTE DE CRUCE ====================

    fun calcularAjuste(
        modulos: List<ModuloDesigual>,
        parantes: List<Int>,
        cruce: Float,
        tipo: String,
        valorParanteApa: Float = 2.5f
    ): Float {
        // El ancho de cada módulo ya viene NETO de parantes (generarTramos y el editor usan
        // anchoUtil = ancho - parantes al calcular el <w>). Por eso el ajuste solo reparte el
        // cruce entre las divisiones; restar parantes aquí los descontaría dos veces y los
        // materiales saldrían pequeños. Equivale exactamente a uFijosAparente para el caso igual.
        val transiciones = contarTransiciones(modulos, parantes)
        val cruceTotal = transiciones * cruce
        return cruceTotal / modulos.size.coerceAtLeast(1)
    }

    /**
     * Ajuste de cruce POR MÓDULO, por tramo: cada módulo recibe
     * (cruces_del_tramo × cruce) / divisiones_del_tramo.
     *
     * El cruce se cuenta sobre el PATRÓN REAL del tramo (no el estándar), porque el tramo del
     * diseño no tiene parantes internos ni necesariamente el patrón por defecto. No se reusa
     * NovaCalculos.uFijos aquí porque asume una ventana completa (parante fantasma + patrón
     * estándar) y eso desviaba el valor.
     */
    fun ajustePorModulo(modulos: List<ModuloDesigual>, parantes: List<Int>, cruce: Float, tipo: String): FloatArray {
        val out = FloatArray(modulos.size)
        var idx = 0
        for (seg in segmentos(modulos, parantes)) {
            val div = seg.size.coerceAtLeast(1)
            var trans = 0
            for (i in 0 until seg.size - 1) if (seg[i].tipo != seg[i + 1].tipo) trans++
            val aj = trans * cruce / div
            repeat(seg.size) { if (idx < out.size) out[idx] = aj; idx++ }
        }
        return out
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
        tubo: Float,
        puente: String = "M\u00FAltiple",
        incluirUFijos: Boolean = true
    ): String {
        val ajustes = ajustePorModulo(modulos, parantes, cruce, tipo)
        val nParantes = parantes.size
        val lines = mutableListOf<String>()

        // U fijos: combinando RACHAS de fijos adyacentes en un solo perfil (suma de sus U).
        // INA: el parante es transparente → los fijos siguen su racha a través de él (fPf también
        // se suma). APA: el parante rompe la racha (tramos independientes).
        val uFijosMedidas = mutableListOf<Float>()
        if (tipo != "apa") {
            val patron = modulos.map { it.tipo }
            val uPorFijo = modulos.indices.filter { modulos[it].tipo == 'f' }
                .map { modulos[it].ancho + ajustes[it] }
            uFijosMedidas.addAll(NovaCalculos.uFijosPorRachas(patron, uPorFijo))
        } else {
            var gU = 0
            for (seg in segmentos(modulos, parantes)) {
                val patron = seg.map { it.tipo }
                val uPorFijo = mutableListOf<Float>()
                for (i in seg.indices) {
                    if (seg[i].tipo == 'f') uPorFijo.add(seg[i].ancho + ajustes[gU])
                    gU++
                }
                uFijosMedidas.addAll(NovaCalculos.uFijosPorRachas(patron, uPorFijo))
            }
        }
        if (incluirUFijos && uFijosMedidas.isNotEmpty()) {
            lines.add(agrupar(uFijosMedidas))
        }

        val esIna = tipo != "apa"
        val tramosChars = segmentos(modulos, parantes).map { seg -> seg.map { it.tipo } }
        // Fijos / corredizas en los MUROS de la ventana (primitivos compartidos con el motor igual).
        // En INA el fijo en muro lleva U de parante; la corrediza en muro lleva U de mocheta
        // (uParante2). Los módulos junto a un parante no cuentan (no son muro).
        val nMurosFijo = NovaCalculos.fijosEnMuros(tramosChars)
        val nMurosCorrediza = NovaCalculos.corredizasEnMuros(tramosChars)

        // U parantes (verticales)
        if (us != 0f) {
            val nUParantes = if (esIna) nMurosFijo
                             else (tramosChars.size * 2 - NovaCalculos.anguloTopeBordes(tramosChars)).coerceAtLeast(0)
            if (nUParantes > 0) {
                val uParante = if (esIna) NovaPerfilesHelper.calcularUParanteInaparente(alto, us)
                               else NovaPerfilesHelper.calcularUParante(altoHoja, us)
                lines.add("${NovaCalculos.df1(uParante)} = $nUParantes")
            }
        }

        // U mocheta
        if (alto > altoHoja) {
            val altoMocheta = NovaCalculos.altoMocheta(alto, altoHoja, tubo)
            if (us != 0f) {
                // INA: U de mocheta vertical solo en las CORREDIZAS que colindan con muro (uParante2).
                // APA: 2 por tramo.
                val nUMocheta = if (esIna) nMurosCorrediza else (nParantes + 1) * 2
                if (nUMocheta > 0) {
                    val uMocheta = if (esIna) NovaInaCalculos.uParante2(alto, altoHoja, us)
                                   else NovaPerfilesHelper.calcularUMocheta(altoMocheta, us)
                    lines.add("${NovaCalculos.df1(uMocheta)} = $nUMocheta")
                }
            }
            // Horizontal: INA = U superior del ancho total (1 pieza). APA = por tramo.
            if (esIna) {
                lines.add("${NovaCalculos.df1(NovaInaCalculos.uSuperior(ancho))} = 1")
            } else {
                lines.add(textoUAnchoMochetaPorTramos(modulos, parantes, ancho, tipo, tubo, puente))
            }
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

        // Puente/riel de cada tramo = la suma de los anchos de SUS módulos. Los <w> ya vienen
        // netos de parantes (el editor los reescala al ancho útil del tramo), así que cada
        // tramo es independiente: no se redistribuye por el total (eso mezclaba tramos y hacía
        // que un cambio en uno arrastrara a los otros con valores incorrectos).
        val anchosSegs = segs.map { seg -> seg.sumOf { it.ancho.toDouble() }.toFloat() }
        val anchosAjustados = anchosSegs

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
        tubo: Float,
        modelo: String = "nn",
        mochetaInferior: Float = 0f,
        mochetaInvertidaIna: Boolean = false,
        puente: String = "Múltiple"
    ): String {
        val ajustes = ajustePorModulo(modulos, parantes, cruce, tipo)
        val esIna = tipo != "apa"
        val lines = mutableListOf<String>()

        val holgura = if (us == 0f) 1f else 0.2f
        val altoVidrioFijo = altoHoja - (us + holgura)
        val altoVidrioCorre = altoHoja - 3.5f
        val vidriosFijos = mutableListOf<Pair<Float, Float>>()
        val vidriosCorre = mutableListOf<Pair<Float, Float>>()
        val patronGlobal = modulos.map { it.tipo }
        var g = 0
        for (seg in segmentos(modulos, parantes)) {
            val patronSeg = seg.map { it.tipo }
            for (i in seg.indices) {
                if (seg[i].tipo == 'f') {
                    // Descuento por COLINDANCIA. INA: el parante es transparente → se mira sobre
                    // TODA la ventana (un fijo junto a un parante ve el fijo del otro lado → 0.2,
                    // no 0.4). APA: por tramo (el parante actúa como muro → 0.4 en el borde).
                    val descuento = if (esIna) NovaCalculos.descuentoColindanciaFijo(patronGlobal, g)
                                    else NovaCalculos.descuentoColindanciaFijo(patronSeg, i)
                    vidriosFijos.add(Pair(seg[i].ancho + ajustes[g] - descuento, altoVidrioFijo))
                } else {
                    vidriosCorre.add(Pair(seg[i].ancho + ajustes[g] - 1.4f, altoVidrioCorre))
                }
                g++
            }
        }
        if (vidriosFijos.isNotEmpty()) lines.add(agrupar2D(vidriosFijos))
        if (vidriosCorre.isNotEmpty()) lines.add(agrupar2D(vidriosCorre))

        // Vidrios mocheta
        if (alto > altoHoja && mocheta.isNotEmpty()) {
            // Holgura de ancho alineada al motor igual (NovaVidriosHelper.calcularVidrioMochetaAparente).
            val holguraMochAncho = 0.5f

            val alturasMochetas = if (modelo == "np") {
                NovaInaCalculos.alturasMochetasPorModelo(
                    modelo = modelo,
                    alto = alto,
                    altoHoja = altoHoja,
                    alturaPuente = tubo,
                    mochetaInferior = mochetaInferior
                ).listaConUbicacion()
            } else {
                listOf(NovaInaCalculos.AlturaMocheta(NovaCalculos.altoMocheta(alto, altoHoja, tubo), inferior = false))
            }

            val vidriosMoch = mutableListOf<Pair<Float, Float>>()
            for (alturaMocheta in alturasMochetas) {
                val altoVidrioMoch = when (tipo) {
                    "ina", "piv" -> if (mochetaInvertidaIna && tipo == "ina") {
                        (alto - altoHoja - 0.5f).coerceAtLeast(0f)
                    } else if (alturaMocheta.inferior) {
                        (alturaMocheta.valor - 0.5f).coerceAtLeast(0f)
                    } else {
                        alturaMocheta.valor + 1f
                    }
                    // APA: mismo alto que el motor igual → altoMocheta − us − 0.3 (normal)
                    // o altoMocheta − 0.5 con puente múltiple/gorrito.
                    else -> if (esPuenteMultipleOGorrito(puente)) {
                        (alturaMocheta.valor - 0.5f).coerceAtLeast(0f)
                    } else {
                        (alturaMocheta.valor - us - 0.3f).coerceAtLeast(0f)
                    }
                }
                if (tipo != "apa") {
                    // INA: 1 vidrio de mocheta por RACHA de corredizas adyacentes; ancho = suma de
                    // los anchos de la racha − 0.6 (las corredizas juntas comparten un solo vidrio).
                    for (w in rachasCorredizasAnchos(modulos, parantes)) {
                        vidriosMoch.add(Pair((w - 0.6f).coerceAtLeast(0f), altoVidrioMoch))
                    }
                } else {
                    mocheta.forEach { mod ->
                        val anchoVidrio = mod.ancho - holguraMochAncho
                        vidriosMoch.add(Pair(anchoVidrio, altoVidrioMoch))
                    }
                }
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
        tubo: Float,
        puente: String = "M\u00FAltiple",
        modelo: String = "nn",
        mochetaInferior: Float = 0f,
        mocheta: List<ModuloDesigual> = emptyList()
    ): OtrosResult {
        val ajustes = ajustePorModulo(modulos, parantes, cruce, tipo)
        val nCorredizas = modulos.count { it.tipo == 'c' }

        // Portafelpa: valor del primitivo del motor igual (altoHoja - 1.6).
        val portafelpaVal = NovaCalculos.portafelpa(altoHoja)
        val divDePortas = cantidadPortafelpas(modulos, parantes)
        val portafelpaTxt = if (divDePortas > 0) "${NovaCalculos.df1(portafelpaVal)} = $divDePortas" else ""

        // Hache: misma medida que uFijos por módulo corredizo
        val hacheMedidas = modulos.indices.filter { modulos[it].tipo == 'c' }.map { modulos[it].ancho + ajustes[it] }
        val hacheTxt = if (hacheMedidas.isNotEmpty()) agrupar(hacheMedidas) else ""

        // Tee [APA]: medida = u alto mocheta, cantidad = sum(tramoMocheta - 1)
        val teeTxt = if (tipo == "apa" && alto > altoHoja) {
            val alturasMochetas = if (modelo == "np") {
                NovaInaCalculos.alturasMochetasPorModelo(
                    modelo = modelo,
                    alto = alto,
                    altoHoja = altoHoja,
                    alturaPuente = tubo,
                    mochetaInferior = mochetaInferior
                ).lista()
            } else {
                listOf(NovaCalculos.altoMocheta(alto, altoHoja, tubo))
            }
            // Tees = (paños de mocheta reales del diseño) − (nº de tramos): un tee entre cada par
            // de paños adyacentes dentro de cada tramo. Usa los paños editados, no anchMota(ancho),
            // así respeta cuando decides que un tramo lleve un solo vidrio de mocheta.
            val nTramosMocheta = segmentos(modulos, parantes).size
            val nTeesBase = if (mocheta.isNotEmpty()) {
                (mocheta.size - nTramosMocheta).coerceAtLeast(0)
            } else {
                segmentos(modulos, parantes).sumOf { seg ->
                    (NovaCalculos.anchMota(seg.sumOf { it.ancho.toDouble() }.toFloat()) - 1).coerceAtLeast(0)
                }
            }
            if (nTeesBase <= 0 || alturasMochetas.isEmpty()) {
                ""
            } else {
                val lineas = linkedMapOf<String, Int>()
                for (alturaMocheta in alturasMochetas) {
                    // Valor del tee desde el primitivo del motor igual (NovaPerfilesHelper).
                    val teeVal = NovaPerfilesHelper.calcularTe(alturaMocheta, us, puente)
                    if (teeVal <= 0f) continue
                    val key = NovaCalculos.df1(teeVal)
                    lineas[key] = (lineas[key] ?: 0) + nTeesBase
                }
                lineas.entries.joinToString("\n") { "${it.key} = ${it.value}" }
            }
        } else ""

        // Ángulo tope: 1 por cada corrediza que toca un borde de tramo (muro/parante), donde
        // reemplaza a la U de parante. Reusa el primitivo de NovaCalculos.
        val tramosTope = segmentos(modulos, parantes).map { seg -> seg.map { it.tipo } }
        val nTope = NovaCalculos.anguloTopeBordes(tramosTope)
        val topeTxt = if (nTope > 0) "${NovaCalculos.df1(altoHoja - 0.9f)} = $nTope" else ""

        // Tubo
        val tuboTxt = if (alto > altoHoja) "${NovaCalculos.df1(ancho)} = 1" else ""

        return OtrosResult(portafelpaTxt, hacheTxt, teeTxt, topeTxt, tuboTxt)
    }
}
