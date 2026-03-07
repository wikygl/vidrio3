package crystal.crystal.taller.nova

import kotlin.math.ceil
import kotlin.math.floor

object NovaCalculos {

    // ==================== FUNCIONES DE FORMATO ====================
    fun df1(defo: Float): String {
        val truncado = if (defo >= 0f) {
            floor(defo * 10f) / 10f
        } else {
            kotlin.math.ceil(defo * 10f) / 10f
        }

        return if (truncado % 1 == 0f) {
            truncado.toInt().toString()
        } else {
            "%.1f".format(truncado).replace(",", ".")
        }
    }

    private fun divisionesAuto(ancho: Float): Int =
        if (ancho <= 0f) 1 else ceil(ancho / 60.0).toInt().coerceAtLeast(1)

    private fun nCorredizasGenerico(divisiones: Int): Int {
        if (divisiones <= 1) return 0
        val base = divisiones / 2
        val ajuste = if (divisiones >= 6 && divisiones % 4 == 2) 1 else 0
        return (base - ajuste).coerceAtLeast(0)
    }

    private fun patronGenerico(divisiones: Int): String {
        if (divisiones <= 1) return "f"
        val nC = nCorredizasGenerico(divisiones).coerceAtMost(divisiones - 1)
        val arr = MutableList(divisiones) { 'f' }
        if (nC <= 0) return arr.joinToString("")

        val usados = mutableSetOf<Int>()
        for (i in 0 until nC) {
            val ideal = (((i + 1f) * (divisiones - 1)) / (nC + 1f)).toInt().coerceIn(1, divisiones - 1)
            var pos = ideal
            while (pos < divisiones && pos in usados) pos++
            if (pos >= divisiones) {
                pos = ideal
                while (pos > 0 && pos in usados) pos--
            }
            if (pos in 1 until divisiones) {
                usados.add(pos)
                arr[pos] = 'c'
            }
        }
        return arr.joinToString("")
    }

    private fun patronPorDivisiones(divisiones: Int): String {
        return when (divisiones) {
            1 -> "f"
            2 -> "fc"
            3 -> "fcf"
            4 -> "fccf"
            5 -> "fcfcf"
            6 -> "fcffcf"
            7 -> "fcfcfcf"
            8 -> "fccffccf"
            9 -> "fcfcfcfcf"
            10 -> "fcfcffcfcf"
            11 -> "fcfcfcfcfcf"
            12 -> "fccffccffccf"
            13 -> "fcfcfcfcfcfcf"
            14 -> "fcfcffccffcfcf"
            15 -> "fcfcfcfcfcfcfcf"
            else -> patronGenerico(divisiones)
        }
    }

    private fun contarCrucesEnPatron(patron: String): Int {
        if (patron.length <= 1) return 0
        var cruces = 0
        for (i in 0 until patron.length - 1) {
            if (patron[i] != patron[i + 1]) cruces++
        }
        return cruces
    }

    private fun contarCrucesDesdeOrden(orden: String): Int {
        return orden
            .split(";P;")
            .sumOf { tramo ->
                val secuencia = tramo.filter { it == 'f' || it == 'c' }
                var cruces = 0
                for (i in 0 until secuencia.length - 1) {
                    if (secuencia[i] != secuencia[i + 1]) cruces++
                }
                cruces
            }
    }

    fun cantidadCruces(ancho: Float, divisiones: Int): Int {
        if (divisiones <= 1) return 0
        val ordenConParantes = ordenDivisConParantes(divisiones, ancho)
        return contarCrucesDesdeOrden(ordenConParantes)
    }

    fun cantidadParantes(ancho: Float, divisiones: Int, valorParante: Float = 2.5f): Float {
        if (divisiones <= 1) return 0f
        val ordenConParantes = ordenDivisConParantes(divisiones, ancho)
        val nParantes = (ordenConParantes.split(";P;").size - 1).coerceAtLeast(0)
        return nParantes * valorParante
    }

    private fun nParantesCadaNCorredizas(divisiones: Int, cadaN: Int): Int {
        if (divisiones <= cadaN || cadaN <= 0) return 0
        return ((divisiones - 1) / cadaN).coerceAtLeast(0)
    }

    fun nParantesCadaDosCorredizas(divisiones: Int): Int = nParantesCadaNCorredizas(divisiones, 2)

    fun nParantesCadaTresCorredizas(divisiones: Int): Int = nParantesCadaNCorredizas(divisiones, 3)

    fun ordenCorredizasConParantes(divisiones: Int, ancho: Float, cadaN: Int = 2): String {
        if (divisiones <= 0) return ""
        val n = cadaN.coerceAtLeast(1)
        val c = "c<${df1(ancho / divisiones)}>"
        val partes = mutableListOf<String>()
        for (i in 1..divisiones) {
            partes.add(c)
            if (i < divisiones && i % n == 0) {
                partes.add(";P;")
            }
        }
        return partes.joinToString("")
    }

    fun ordenNcfcConParantes(divisiones: Int, ancho: Float): String {
        if (divisiones <= 0) return ""
        val c = "c<${df1(ancho / divisiones)}>"
        val f = "f<${df1(ancho / divisiones)}>"
        val partes = mutableListOf<String>()
        for (i in 1..divisiones) {
            val pos = (i - 1) % 3
            partes.add(if (pos == 1) f else c)
            if (i < divisiones && i % 3 == 0) {
                partes.add(";P;")
            }
        }
        return partes.joinToString("")
    }

    fun nParantesDiseno(ancho: Float, divisiones: Int): Int {
        if (divisiones <= 1) return 0
        val ordenConParantes = ordenDivisConParantes(divisiones, ancho)
        return (ordenConParantes.split(";P;").size - 1).coerceAtLeast(0)
    }

    private fun gruposDivisiones(divisiones: Int, maxPorGrupo: Int = 5): List<Int> {
        if (divisiones <= maxPorGrupo) return listOf(divisiones)
        val grupos = ceil(divisiones / maxPorGrupo.toDouble()).toInt().coerceAtLeast(1)
        val base = divisiones / grupos
        val extra = divisiones % grupos
        return (0 until grupos).map { idx -> if (idx < extra) base + 1 else base }
    }

    private fun repartirDivisionesEnTramos(divisiones: Int, tramos: Int): List<Int> {
        if (tramos <= 1) return listOf(divisiones.coerceAtLeast(1))
        val base = divisiones / tramos
        val extra = divisiones % tramos
        val reparto = MutableList(tramos) { base }

        var extrasPendientes = extra
        val esImpar = tramos % 2 != 0

        if (esImpar) {
            val centro = tramos / 2
            // Si extras es impar, uno al centro.
            if (extrasPendientes % 2 != 0) {
                reparto[centro] += 1
                extrasPendientes -= 1
            }
            // Luego pares simétricos desde el centro hacia afuera.
            var offset = 1
            while (extrasPendientes >= 2 && (centro - offset) >= 0 && (centro + offset) < tramos) {
                reparto[centro - offset] += 1
                reparto[centro + offset] += 1
                extrasPendientes -= 2
                offset += 1
            }
        } else {
            // Tramos pares: repartir en pares simétricos alrededor del centro (sin índice central).
            val izqCentro = tramos / 2 - 1
            val derCentro = tramos / 2
            var offset = 0
            while (extrasPendientes >= 2 && (izqCentro - offset) >= 0 && (derCentro + offset) < tramos) {
                reparto[izqCentro - offset] += 1
                reparto[derCentro + offset] += 1
                extrasPendientes -= 2
                offset += 1
            }
            // Si queda 1 extra (simetría imposible), asignar al primer extremo disponible.
            if (extrasPendientes == 1) {
                val candidatos = listOf(izqCentro - offset, derCentro + offset, izqCentro, derCentro)
                val idx = candidatos.firstOrNull { it in reparto.indices && reparto[it] == base } ?: izqCentro
                reparto[idx] += 1
                extrasPendientes = 0
            }
        }
        return reparto
    }

    fun gruposDivisionesPorTramo(ancho: Float, divisiones: Int): List<Int> {
        val tramos = nPuentesEfectivos(ancho, divisiones)
        return when {
            tramos == 2 && divisiones == 6 -> listOf(3, 3)
            tramos == 2 && divisiones == 8 -> listOf(4, 4)
            tramos == 2 && divisiones == 10 -> listOf(5, 5)
            tramos == 3 && divisiones == 12 -> listOf(4, 4, 4)
            tramos == 3 && divisiones == 14 -> listOf(5, 4, 5)
            else -> repartirDivisionesEnTramos(divisiones, tramos)
        }
    }

    private fun gruposCorredizasCadaN(divisiones: Int, cadaN: Int): List<Int> {
        if (divisiones <= 0) return emptyList()
        val n = cadaN.coerceAtLeast(1)
        val grupos = mutableListOf<Int>()
        var restantes = divisiones
        while (restantes > 0) {
            val tramo = minOf(n, restantes)
            grupos.add(tramo)
            restantes -= tramo
        }
        return grupos
    }

    fun gruposDivisionesMochetaPorModelo(ancho: Float, divisiones: Int, modelo: String): List<Int> {
        return when (modelo) {
            "ncc" -> gruposCorredizasCadaN(divisiones, 2)
            "n3c" -> gruposCorredizasCadaN(divisiones, 3)
            "ncfc" -> gruposCorredizasCadaN(divisiones, 3)
            else -> gruposDivisionesPorTramo(ancho, divisiones)
        }
    }

    // ==================== FUNCIONES DE DIVISIÖN Y ESTRUCTURA ====================
    fun divisiones(ancho: Float, divisManual: Int, tipo: String = "nn"): Int {
        return when (tipo) {
            "nn", "nl" -> if (divisManual == 0) divisionesAuto(ancho) else divisManual.coerceAtLeast(1)
            "ncc" -> 2
            "n3c", "ncfc" -> 3
            else -> 0
        }
    }
    fun nFijos(divisiones: Int, tipo: String = "nn"): Int {
        return when (tipo) {
            "nn", "nl" -> when (divisiones) {
                1 -> 1
                2 -> 1
                3 -> 2
                4 -> 2
                5 -> 3
                6 -> 4
                7 -> 4
                8 -> 4
                9 -> 5
                10 -> 6
                11 -> 6
                12 -> 6
                13 -> 7
                14 -> 8
                15 -> 8
                else -> divisiones - nCorredizasGenerico(divisiones)
            }
            "ncc" -> 0
            "n3c" -> 0
            "ncfc" -> {
                if (divisiones <= 0) 0 else {
                    val base = divisiones / 3
                    val rem = divisiones % 3
                    base + if (rem == 2) 1 else 0
                }
            }
            else -> 0
        }
    }
    fun nCorredizas(divisiones: Int, tipo: String = "nn"): Int {
        return when (tipo) {
            "nn", "nl" -> when (divisiones) {
                1 -> 0
                2 -> 1
                3 -> 1
                4 -> 2
                5 -> 2
                6 -> 2
                7 -> 3
                8 -> 4
                9 -> 4
                10 -> 4
                11 -> 5
                12 -> 6
                13 -> 6
                14 -> 6
                15 -> 7
                else -> nCorredizasGenerico(divisiones)
            }
            "ncc" -> 2
            "n3c" -> 3
            "ncfc" -> {
                if (divisiones <= 0) 0 else divisiones - nFijos(divisiones, "ncfc")
            }
            else -> 0
        }
    }
    fun ordenDivis(divisiones: Int,ancho: Float): String {
        val f = "f<${df1(ancho/divisiones)}>"
        val c = "c<${df1(ancho/divisiones)}>"
         return when (divisiones) {
            1 -> f
            2 -> "$f$c"
            3 -> "$f$c$f"
            4 -> "$f$c$c$f"
            5 -> "$f$c$f$c$f"
            6 -> "$f$c$f$f$c$f"
            7 -> "$f$c$f$c$f$c$f"
            8 -> "$f$c$c$f$f$c$c$f"
            9 -> "$f$c$f$c$f$c$f$c$f"
            10 -> "$f$c$f$c$f$f$c$f$c$f"
            11 -> "$f$c$f$c$f$c$f$c$f$c$f"
            12 -> "$f$c$c$f$f$c$c$f$f$c$c$f"
            13 -> "$f$c$f$c$f$c$f$c$f$c$f$c$f"
            14 -> "$f$c$f$c$f$f$c$c$f$f$c$f$c$f"
            15 -> "$f$c$f$c$f$c$f$c$f$c$f$c$f$c$f"
            else -> patronGenerico(divisiones).map { "$it<${df1(ancho / divisiones)}>" }.joinToString("")
        }
    }
    fun ordenDivisConParantes(divisiones: Int, ancho: Float): String {
        val nP = nPuentesEfectivos(ancho, divisiones)
        if (nP <= 1) return ordenDivis(divisiones, ancho)
        val f = "f<${df1(ancho / divisiones)}>"
        val c = "c<${df1(ancho / divisiones)}>"
        val grupos = when {
            nP == 2 && divisiones == 6 -> listOf("$f$c$f", "$f$c$f")
            nP == 2 && divisiones == 8 -> listOf("$f$c$c$f", "$f$c$c$f")
            nP == 2 && divisiones == 10 -> listOf("$f$c$f$c$f", "$f$c$f$c$f")
            nP == 3 && divisiones == 12 -> listOf("$f$c$c$f", "$f$c$c$f", "$f$c$c$f")
            nP == 3 && divisiones == 14 -> listOf("$f$c$f$c$f", "$f$c$c$f", "$f$c$f$c$f")
            else -> {
                val gruposAuto = gruposDivisionesPorTramo(ancho, divisiones)
                return gruposAuto.joinToString(";P;") { nDiv ->
                    val anchoSeccion = (ancho / divisiones) * nDiv
                    ordenDivis(nDiv, anchoSeccion)
                }
            }
        }
        return grupos.joinToString(";P;")
    }
    fun ordenMochetas(totalMochetas: Int, ancho: Float): String {
        if (totalMochetas <= 0) return "f<${df1(ancho)}>"
        val m = "f<${df1(ancho / totalMochetas)}>"
        return (1..totalMochetas).joinToString("") { m }
    }
    fun ordenMochetasConParantes(divisiones: Int, ancho: Float): String {
        return ordenMochetasConParantesModelo(divisiones, ancho, "nn")
    }

    fun ordenMochetasConParantesModelo(divisiones: Int, ancho: Float, modelo: String): String {
        if (divisiones <= 0) return ""
        val gruposDivs = gruposDivisionesMochetaPorModelo(ancho, divisiones, modelo)
        if (gruposDivs.isEmpty()) return ""

        val anchoPorDiv = ancho / divisiones
        val tramosMochetas = gruposDivs.map { nDivs ->
            val anchoSeccion = nDivs * anchoPorDiv
            val am = anchMota(anchoSeccion)
            val anchoVidrio = anchoSeccion / am
            buildString {
                repeat(am) { append("f<${df1(anchoVidrio)}>") }
            }
        }
        return tramosMochetas.joinToString(";P;")
    }
    fun altoHoja(alto: Float, hoja: Float): Float {
        val corre = if (hoja >= alto) alto else hoja
        return if (hoja == 0f) {
            alto / 7 * 5
        } else {
            corre
        }
    }
    fun siNoMoch(alto: Float, hoja: Float): Int {
        return if (hoja >= alto) 0 else 1
    }
    fun altoMocheta(alto: Float, altoHoja: Float, tubo: Float): Float {
        return alto - (altoHoja + tubo)
    }
    fun nPuentes(divisiones: Int): Int {
        return when {
            divisiones <= 0 -> 0
            divisiones <= 5 -> 1
            else -> ceil(divisiones / 5.0).toInt().coerceAtLeast(1)
        }
    }

    fun nPuentesEfectivos(ancho: Float, divisiones: Int): Int {
        return nPuentes(divisiones).coerceAtLeast(1)
    }
    /**
     * Calcula medida de puentes para APARENTE
     */
    fun mPuentes1Aparente(ancho: Float, divisiones: Int): Float {
        val parantes = 2.5f
        val base = when (divisiones) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> ancho
            6, 8 -> (ancho - parantes) / 2
            10 -> (ancho - (2 * parantes)) / divisiones * 3
            12 -> (ancho - (2 * parantes)) / 3
            14 -> (ancho - (2 * parantes)) / divisiones * 5
            else -> {
                if (divisiones > 15) {
                    val puentes = nPuentes(divisiones)
                    (ancho - (2.5f * (puentes - 1))) / puentes
                } else 0f
            }
        }
        val puentes = nPuentes(divisiones)
        if (divisiones % 2 != 0 && puentes > 1) {
            return (ancho - (parantes * (puentes - 1))) / puentes
        }
        return base
    }

    /**
     * Calcula medida de puentes para INAPARENTE/PIVOTANTE
     */
    fun mPuentes1Inaparente(ancho: Float, divisiones: Int): Float {
        val parantes = 2.5f
        val base = when (divisiones) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> ancho
            6, 8, 10 -> (ancho - parantes) / 2
            12 -> (ancho - (2 * parantes)) / 3
            14 -> (ancho - (2 * parantes)) / divisiones * 5
            else -> {
                if (divisiones > 15) {
                    val puentes = nPuentes(divisiones)
                    (ancho - (2.5f * (puentes - 1))) / puentes
                } else 0f
            }
        }
        val puentes = nPuentes(divisiones)
        if (divisiones % 2 != 0 && puentes > 1) {
            return (ancho - (parantes * (puentes - 1))) / puentes
        }
        return base
    }

    // Wrapper para compatibilidad

    fun mPuentes1(ancho: Float, divisiones: Int, tipoVentana: String = "apa"): Float {
        return when (tipoVentana) {
            "apa" -> mPuentes1Aparente(ancho, divisiones)
            "ina", "piv" -> mPuentes1Inaparente(ancho, divisiones)
            else -> mPuentes1Aparente(ancho, divisiones)
        }
    }
    fun mPuentes2Aparente(ancho: Float, divisiones: Int): Float {
        val parantes = 2.5f
        return when (divisiones) {
            10 -> (ancho - (2 * parantes)) / divisiones * 4
            14 -> (ancho - (2 * parantes)) / divisiones * 4
            else -> 0f
        }
    }
    fun mPuentes2Inaparente(ancho: Float, divisiones: Int): Float {
        val parantes = 2.5f
        return when (divisiones) {
            14 -> (ancho - (2 * parantes)) / divisiones * 4
            else -> 0f  // En INA, división 10 no usa mPuentes2
        }
    }
    fun mPuentes2(ancho: Float, divisiones: Int, tipoVentana: String = "apa"): Float {
        return when (tipoVentana) {
            "apa" -> mPuentes2Aparente(ancho, divisiones)
            "ina", "piv" -> mPuentes2Inaparente(ancho, divisiones)
            else -> mPuentes2Aparente(ancho, divisiones)
        }
    }
    // ==================== FUNCIONES DE U ====================
    // Calcula U fijos para APARENTE Fórmula: ((ancho - (2.5 * (nPuentes - 1))) + cruceTotal) / divisiones/*

    fun uFijosAparente(ancho: Float, divisiones: Int, cruce: Float, valorParante: Float = 2.5f): Float {
        if (divisiones <= 0) return 0f
        val parantes = cantidadParantes(ancho, divisiones, valorParante)
        val cruces = cantidadCruces(ancho, divisiones)
        return ((ancho - parantes) + (cruces * cruce)) / divisiones
    }

    // Calcula U fijos para INAPARENTE/PIVOTANTEFórmula: (ancho + cruceTotal) / divisiones

    fun uFijosInaparente(ancho: Float, divisiones: Int, cruce: Float): Float {
        if (divisiones <= 0) return 0f
        val cruces = cantidadCruces(ancho, divisiones)
        return (ancho + (cruces * cruce)) / divisiones
    }

    /**
     * Wrapper para compatibilidad - usa tipo para elegir fórmula
     */
    fun rachasFijosColindantes(ancho: Float, divisiones: Int): List<Int> {
        if (divisiones <= 0) return emptyList()
        val secuencia = ordenDivisConParantes(divisiones, ancho).filter { it == 'f' || it == 'c' }
        if (secuencia.isEmpty()) return emptyList()

        val rachas = mutableListOf<Int>()
        var actual = 0
        for (ch in secuencia) {
            if (ch == 'f') {
                actual++
            } else if (actual > 0) {
                rachas.add(actual)
                actual = 0
            }
        }
        if (actual > 0) rachas.add(actual)
        return rachas
    }

    fun textoUFijosColindantes(ancho: Float, divisiones: Int, uFijos: Float): String {
        val rachas = rachasFijosColindantes(ancho, divisiones)
        if (rachas.isEmpty()) return ""

        val conteo = linkedMapOf<String, Int>()
        for (racha in rachas) {
            val medida = df1(uFijos * racha)
            conteo[medida] = (conteo[medida] ?: 0) + 1
        }
        return conteo.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    private fun esPuenteMultipleOGorrito(puente: String): Boolean {
        val p = puente.lowercase().trim()
        return p.contains("multi") || p.contains("múlt") || p.contains("mÃºlt") || p.contains("gorrito") || p.contains("ltiple")
    }

    fun textoUMochetaPorTramosAparente(
        ancho: Float,
        divisiones: Int,
        puente: String,
        valorSpinner: Float,
        modelo: String = "nn"
    ): String {
        if (divisiones <= 0) return ""
        val grupos = gruposDivisionesMochetaPorModelo(ancho, divisiones, modelo)
        if (grupos.isEmpty()) return ""

        val anchosTramo = grupos.map { (ancho / divisiones) * it }
        val nParantes = (grupos.size - 1).coerceAtLeast(0)
        val fijo = esPuenteMultipleOGorrito(puente)
        val parante = if (fijo) 2.5f else valorSpinner
        val cantidadPorTramo = if (fijo) 1 else 2
        val totalAnchos = anchosTramo.sum()
        val anchoDescontado = (ancho - (parante * nParantes)).coerceAtLeast(0f)
        val medidas = if (totalAnchos > 0f) {
            anchosTramo.map { (it / totalAnchos) * anchoDescontado }
        } else {
            anchosTramo
        }

        val conteo = linkedMapOf<String, Int>()
        for (m in medidas) {
            val key = df1(m)
            conteo[key] = (conteo[key] ?: 0) + cantidadPorTramo
        }
        return conteo.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    fun textoTramosUnitarioAparente(
        ancho: Float,
        divisiones: Int,
        puente: String,
        valorSpinner: Float,
        modelo: String = "nn"
    ): String {
        if (divisiones <= 0) return ""
        val grupos = gruposDivisionesMochetaPorModelo(ancho, divisiones, modelo)
        if (grupos.isEmpty()) return ""

        val anchosTramo = grupos.map { (ancho / divisiones) * it }
        val nParantes = (grupos.size - 1).coerceAtLeast(0)
        val fijo = esPuenteMultipleOGorrito(puente)
        val parante = if (fijo) 2.5f else valorSpinner
        val totalAnchos = anchosTramo.sum()
        val anchoDescontado = (ancho - (parante * nParantes)).coerceAtLeast(0f)
        val medidas = if (totalAnchos > 0f) {
            anchosTramo.map { (it / totalAnchos) * anchoDescontado }
        } else {
            anchosTramo
        }

        val conteo = linkedMapOf<String, Int>()
        for (m in medidas) {
            val key = df1(m)
            conteo[key] = (conteo[key] ?: 0) + 1
        }
        return conteo.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    fun uFijos(
        ancho: Float,
        divisiones: Int,
        cruce: Float,
        tipoVentana: String = "apa",
        valorParanteApa: Float = 2.5f
    ): Float {
        return when (tipoVentana) {
            "apa" -> uFijosAparente(ancho, divisiones, cruce, valorParanteApa)
            "ina", "piv" -> uFijosInaparente(ancho, divisiones, cruce)
            else -> uFijosAparente(ancho, divisiones, cruce, valorParanteApa)
        }
    }
    fun fijoUParante(divisiones: Int): Int {
        return when (divisiones) {
            1 -> 2
            2 -> 1
            3, 4, 5, 7, 9, 11, 13, 15 -> 2
            6, 8, 10, 12, 14 -> when (divisiones) {
                6, 8 -> 4
                10, 14 -> 6
                12 -> 6
                else -> 2
            }
            else -> if (divisiones > 0) (nPuentes(divisiones) * 2).coerceAtLeast(2) else 0
        }
    }

    fun fijoUParante(divisiones: Int, ancho: Float): Int {
        val base = fijoUParante(divisiones)
        val porLongitud = (nPuentesEfectivos(ancho, divisiones) * 2).coerceAtLeast(2)
        return maxOf(base, porLongitud)
    }

    fun mochetaUParante(divisiones: Int): Int {
        return when (divisiones) {
            1, 2, 3, 4, 5, 7, 9, 11, 13, 15 -> 2
            6, 8 -> 4
            10, 12, 14 -> 6
            else -> if (divisiones > 0) (nPuentes(divisiones) * 2).coerceAtLeast(2) else 0
        }
    }

    fun mochetaUParante(divisiones: Int, ancho: Float): Int {
        val base = mochetaUParante(divisiones)
        val porLongitud = (nPuentesEfectivos(ancho, divisiones) * 2).coerceAtLeast(2)
        return maxOf(base, porLongitud)
    }
    // ==================== FUNCIONES DE PORTAFELPA ====================
    fun cantidadPortafelpas(divisiones: Int, ancho: Float = divisiones.toFloat()): Int {
        if (divisiones <= 1) return 0
        val orden = ordenDivisConParantes(divisiones, ancho)
        val tramos = orden
            .split(";P;")
            .map { tramo -> tramo.filter { it == 'f' || it == 'c' } }
            .filter { it.isNotEmpty() }

        var total = 0
        for (tramo in tramos) {
            total += tramo.count { it == 'c' } * 2
            for (i in tramo.indices) {
                if (tramo[i] != 'f') continue
                if (i > 0 && tramo[i - 1] == 'c') total += 1
                if (i < tramo.lastIndex && tramo[i + 1] == 'c') total += 1
            }
        }
        return total
    }

    fun divDePortas(divisiones: Int, nCorredizas: Int): Int {
        if (divisiones <= 1 || nCorredizas <= 0) return 0
        return cantidadPortafelpas(divisiones)
    }
    fun portafelpa(altoHoja: Float): Float {
        return altoHoja - 1.6f
    }
    // ==================== FUNCIONES DE MOCHETA ====================
    fun anchMota(mPuentes: Float): Int {
        if (mPuentes <= 0f) return 1
        return ceil(mPuentes / 180.0).toInt().coerceAtLeast(1)
    }
    fun diviMocheta(x: Float, nMochetasManual: Int): Int {
        require(x > 0) { "El valor debe ser positivo y mayor que cero." }
        return if (nMochetasManual == 0) {
            ceil(x / 240.0).toInt()
        } else {
            nMochetasManual
        }
    }

    fun cantidadTeePorTramosMocheta(ancho: Float, divisiones: Int, modelo: String = "nn"): Int {
        if (divisiones <= 0) return 0
        val grupos = gruposDivisionesMochetaPorModelo(ancho, divisiones, modelo)
        val anchoPorDiv = ancho / divisiones
        return grupos.sumOf { nDivs ->
            val anchoSeccion = nDivs * anchoPorDiv
            (anchMota(anchoSeccion) - 1).coerceAtLeast(0)
        }
    }
    fun calcularCruce(cruceExacto: Float, divisiones: Int): Float {
        val cruceDefault = 0.7f
        return if (cruceExacto == 0f) cruceDefault else cruceExacto
    }
    fun ancho (ancho: Float): Float {
        return ancho
    }
    fun alto (alto: Float): Float {
        return alto
    }
}


