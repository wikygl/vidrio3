package crystal.crystal.taller.nova

import kotlin.math.ceil
import kotlin.math.floor

object NovaCalculos {

    // Full corredizas (nfc): cada cuántas corredizas va el parante que arma otro tramo.
    // 0 = automático (reparto balanceado como el clásico); >1 = forzar corte cada N. Lo fija la UI.
    var corredizasPorTramoNfc: Int = 0

    // ncfc en INA: se trata como UN SOLO TRAMO (sin parantes que dividan). Lo fija el cálculo
    // según el acabado; afecta diseño y materiales de ncfc por igual.
    var ncfcUnTramo: Boolean = false

    // Full corredizas (nfc) en INA: también un solo tramo (máx. 6 divisiones).
    var nfcUnTramo: Boolean = false

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

    /**
     * Reparto de módulos en tramos elegido a mano, arrastrando sobre el diseño. Se guarda aquí
     * porque de este reparto sale TODO —dibujo, puentes, U, vidrios—, igual que
     * [corredizasPorTramoNfc]. Lo fija la pantalla.
     *
     * Solo se aplica si corresponde al número de divisiones en curso: si el vidriero cambia las
     * divisiones, el reparto guardado deja de valer y manda otra vez el automático.
     */
    var repartoManual: List<Int>? = null

    fun repartoManualPara(divisiones: Int): List<Int>? =
        repartoManual?.takeIf { it.isNotEmpty() && it.sum() == divisiones }

    /** Repartos simétricos de [n] módulos en [t] tramos, cada tramo entre 2 y 5. */
    private fun repartosSimetricos(n: Int, t: Int): List<List<Int>> {
        if (t <= 0) return emptyList()
        if (t == 1) return if (n in 1..5) listOf(listOf(n)) else emptyList()
        val mitad = (t + 1) / 2
        val encontrados = mutableListOf<List<Int>>()
        val parcial = IntArray(mitad)
        fun buscar(idx: Int) {
            if (idx == mitad) {
                val reparto = IntArray(t)
                for (i in 0 until mitad) {
                    reparto[i] = parcial[i]
                    reparto[t - 1 - i] = parcial[i]
                }
                if (reparto.sum() == n) encontrados.add(reparto.toList())
                return
            }
            for (v in 2..5) {
                parcial[idx] = v
                buscar(idx + 1)
            }
        }
        buscar(0)
        return encontrados
    }

    /**
     * Las formas de repartir [divisiones] módulos en tramos, para poder acortar el tramo más
     * largo sin encarecer la ventana.
     *
     * **Cada tramo de más cuesta**: un parante, su U, su puente. Por eso el orden es primero TODO
     * lo que se puede hacer con el menor número de tramos, y solo después lo que añade tramos.
     * Con 10 divisiones eso deja `[3,4,3]` y `[4,2,4]` —los dos de tres tramos, mismo costo—
     * antes que `[2,3,3,2]`, que lleva un parante más.
     *
     * Dentro de un mismo número de tramos va primero el reparto automático
     * ([repartirDivisionesEnTramos]) y luego los demás, del que deja el tramo más corto al que lo
     * deja más largo. Ningún tramo pasa de 5 módulos ni baja de 2, y todos son simétricos.
     */
    fun repartosPosibles(divisiones: Int, maxOpciones: Int = 5): List<List<Int>> {
        if (divisiones <= 0) return emptyList()
        if (divisiones <= 5) return listOf(listOf(divisiones))
        val minTramos = ceil(divisiones / 5.0).toInt().coerceAtLeast(1)
        val maxTramos = (divisiones / 2).coerceAtLeast(minTramos)
        val opciones = mutableListOf<List<Int>>()
        // El tramo más corto conseguido hasta ahora con menos tramos. Añadir un tramo solo se
        // ofrece si de verdad acorta el más largo; si no, es gasto sin ganancia.
        var mejorMaximo = Int.MAX_VALUE
        for (t in minTramos..maxTramos) {
            if (opciones.size >= maxOpciones) break
            val automatico = repartirDivisionesEnTramos(divisiones, t)
                .takeIf { it.size == t && it.all { n -> n in 2..5 } }
            val resto = repartosSimetricos(divisiones, t)
                .filter { it != automatico }
                .sortedWith(compareBy({ it.max() }, { it.max() - it.min() }))
            val candidatos = (listOfNotNull(automatico) + resto).filter { it.max() < mejorMaximo }
            // Dentro del mismo número de tramos tampoco vale el que alarga: cuesta igual y deja
            // un tramo más largo. Se quedan los que empatan con el mejor del grupo.
            val mejorDelGrupo = candidatos.minOfOrNull { it.max() }
            val delGrupo = candidatos.filter { it.max() == mejorDelGrupo }
            for (reparto in delGrupo) {
                if (opciones.size >= maxOpciones) break
                if (reparto !in opciones) opciones.add(reparto)
            }
            delGrupo.minOfOrNull { it.max() }?.let { if (it < mejorMaximo) mejorMaximo = it }
        }
        if (opciones.isEmpty()) opciones.add(listOf(divisiones))
        return opciones
    }

    fun gruposDivisionesPorTramo(ancho: Float, divisiones: Int): List<Int> {
        repartoManualPara(divisiones)?.let { return it }
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
            // ncfc se arma con el orden del clásico (cffc por grupo). El que sea un solo tramo
            // (INA) o varios (APA) lo deciden quienes lo consumen, no el agrupamiento del orden.
            "ncfc" -> gruposDivisionesPorTramo(ancho, divisiones)
            // Full corredizas: en INA un solo tramo; si no, automático = reparto balanceado del
            // clásico, o si el usuario fija un número (>1) se fuerza un parante/tramo cada N.
            "nfc" -> when {
                nfcUnTramo -> listOf(divisiones)
                corredizasPorTramoNfc > 1 -> gruposCorredizasCadaN(divisiones, corredizasPorTramoNfc)
                else -> gruposDivisionesPorTramo(ancho, divisiones)
            }
            else -> gruposDivisionesPorTramo(ancho, divisiones)
        }
    }

    // ==================== FUNCIONES DE DIVISIÖN Y ESTRUCTURA ====================
    fun divisiones(ancho: Float, divisManual: Int, tipo: String = "nn"): Int {
        return when (tipo) {
            "nn", "nl", "nff", "nfc" -> if (divisManual == 0) divisionesAuto(ancho) else divisManual.coerceAtLeast(1)
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
            // ncfc = inverso del clásico (fijo↔corrediza). Excepción: 1 división solo es fijo.
            "ncfc" -> if (divisiones <= 0) 0 else if (divisiones == 1) 1 else nCorredizas(divisiones, "nn")
            "nff" -> divisiones
            "nfc" -> 0
            else -> 0
        }
    }

    fun nFijos(ancho: Float, divisiones: Int, tipo: String = "nn"): Int {
        return when (tipo) {
            "nn", "nl" -> contarHojasDesdeOrden(ancho, divisiones, 'f')
            else -> nFijos(divisiones, tipo)
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
            // ncfc = inverso del clásico (fijo↔corrediza). Excepción: 1 división solo es fijo.
            "ncfc" -> if (divisiones <= 0) 0 else if (divisiones == 1) 0 else nFijos(divisiones, "nn")
            "nff" -> 0
            "nfc" -> divisiones
            else -> 0
        }
    }

    fun nCorredizas(ancho: Float, divisiones: Int, tipo: String = "nn"): Int {
        return when (tipo) {
            "nn", "nl" -> contarHojasDesdeOrden(ancho, divisiones, 'c')
            else -> nCorredizas(divisiones, tipo)
        }
    }

    private fun contarHojasDesdeOrden(ancho: Float, divisiones: Int, hoja: Char): Int {
        if (divisiones <= 0) return 0
        return ordenDivisConParantes(divisiones, ancho).count { it == hoja }
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
    /**
     * Patrón de ncfc: lo contrario al clásico (intercambia fijo↔corrediza). Excepción: con 1
     * división solo puede ser fijo. Ej.: 2=cf, 3=cfc, 4=cffc, 5=cfcfc; de 6 en adelante hereda
     * la estructura del clásico (esos 5 son la base) con f↔c invertidos.
     */
    fun ordenDivisCfc(divisiones: Int, ancho: Float): String {
        if (divisiones <= 1) return "f<${df1(ancho)}>"
        val clasico = ordenDivis(divisiones, ancho)
        // Intercambiar fijo↔corrediza carácter a carácter (las medidas no contienen 'f' ni 'c').
        return buildString {
            for (ch in clasico) append(when (ch) { 'f' -> 'c'; 'c' -> 'f'; else -> ch })
        }
    }

    /**
     * Secuencia de módulos (c/f) por tramo para ncfc, con el orden por grupos (cffc).
     * En INA (ncfcUnTramo) se devuelve UN solo tramo = concatenación de todos los grupos (sin
     * parantes que dividan); en APA se devuelve un tramo por grupo.
     */
    fun patronModulosNcfc(ancho: Float, divisiones: Int): List<List<Char>> {
        if (divisiones <= 0) return emptyList()
        val grupos = gruposDivisionesMochetaPorModelo(ancho, divisiones, "ncfc")
        val tramos = grupos.map { nDiv ->
            // Solo importan las letras (f/c); el ancho usado para extraerlas es indistinto.
            Regex("[fc](?=<)").findAll(ordenDivisCfc(nDiv, nDiv.toFloat())).map { it.value[0] }.toList()
        }
        return if (ncfcUnTramo) listOf(tramos.flatten()) else tramos
    }

    /**
     * U parante de fijos (ncfc): existe solo si el fijo está al INICIO o FINAL del tramo (colinda
     * con la pared o el parante de tramo). Los fijos internos no llevan U parante. Un fijo solo
     * (la división única) está en ambos extremos, por lo que suma 2.
     */
    fun uParanteFijosNcfc(ancho: Float, divisiones: Int): Int {
        var total = 0
        for (tramo in patronModulosNcfc(ancho, divisiones)) {
            if (tramo.isEmpty()) continue
            if (tramo.first() == 'f') total++   // borde inicio del tramo
            if (tramo.last() == 'f') total++    // borde fin del tramo
        }
        return total
    }

    /**
     * Corridas de corredizas adyacentes (ncfc): devuelve la longitud de cada grupo de corredizas
     * pegadas. Cada corrida es una sola mocheta (ancho = nº corredizas de la corrida × ancho de
     * corrediza). En INA (un tramo) las corridas pueden cruzar lo que serían límites de grupo.
     */
    fun corridasCorredizasNcfc(ancho: Float, divisiones: Int): List<Int> {
        val runs = mutableListOf<Int>()
        for (tramo in patronModulosNcfc(ancho, divisiones)) {
            var i = 0
            while (i < tramo.size) {
                if (tramo[i] == 'c') {
                    var run = 0
                    while (i < tramo.size && tramo[i] == 'c') { run++; i++ }
                    runs.add(run)
                } else i++
            }
        }
        return runs
    }

    /**
     * Portafelpa total (ncfc), contada por módulos sobre el patrón real: cada corrediza aporta 2
     * y cada fijo aporta 1 por cada lado que colinda con una corrediza. Consistente con el patrón
     * actual (un tramo en INA, varios en APA).
     */
    fun portafelpaTotalNcfc(ancho: Float, divisiones: Int): Int {
        var total = 0
        for (tramo in patronModulosNcfc(ancho, divisiones)) {
            for (i in tramo.indices) {
                if (tramo[i] == 'c') {
                    total += 2
                } else {
                    if (i > 0 && tramo[i - 1] == 'c') total++
                    if (i < tramo.size - 1 && tramo[i + 1] == 'c') total++
                }
            }
        }
        return total
    }

    /**
     * Cuenta los módulos del [tipo] dado que están en los MUROS de la ventana: el primer módulo del
     * primer tramo y el último módulo del último tramo. Primitivo compartido por el motor igual y el
     * desigual: en INA la corrediza en muro lleva U de mocheta (uParante2) y el fijo en muro lleva U
     * de parante.
     */
    private fun enMuros(tramos: List<List<Char>>, tipo: Char): Int {
        if (tramos.isEmpty()) return 0
        var total = 0
        val primero = tramos.first()
        val ultimo = tramos.last()
        if (primero.isNotEmpty() && primero.first() == tipo) total++
        if (ultimo.isNotEmpty() && ultimo.last() == tipo) total++
        return total
    }

    fun corredizasEnMuros(tramos: List<List<Char>>): Int = enMuros(tramos, 'c')
    fun fijosEnMuros(tramos: List<List<Char>>): Int = enMuros(tramos, 'f')

    /**
     * U parante de mocheta (ncfc): 1 por cada corrediza que colinda con la PARED (bordes externos
     * de la ventana). Delega en el primitivo general [corredizasEnMuros].
     */
    fun uParanteMochetaCorredizasNcfc(ancho: Float, divisiones: Int): Int =
        corredizasEnMuros(patronModulosNcfc(ancho, divisiones))

    /**
     * Portafelpa asociada a los fijos (ncfc): 1 por cada lado de un fijo que colinda con una
     * corrediza. Ej.: cfc → el fijo colinda con corrediza por ambos lados = 2; cffc → 1 por fijo.
     */
    fun portafelpaFijosNcfc(ancho: Float, divisiones: Int): Int {
        var total = 0
        for (tramo in patronModulosNcfc(ancho, divisiones)) {
            for (i in tramo.indices) {
                if (tramo[i] != 'f') continue
                if (i > 0 && tramo[i - 1] == 'c') total++
                if (i < tramo.size - 1 && tramo[i + 1] == 'c') total++
            }
        }
        return total
    }

    /**
     * U de fijos por RACHAS, general: los fijos adyacentes (ff, fff…) forman un solo perfil U
     * cuya medida es la SUMA de sus U individuales. Recibe el patrón del tramo (f/c en orden) y
     * la U de cada fijo (en orden de aparición). No cruza bordes de tramo (cada tramo se pasa
     * por separado). Primitivo compartido por el motor igual y el desigual.
     */
    fun uFijosPorRachas(patron: List<Char>, uPorFijo: List<Float>): List<Float> {
        val result = mutableListOf<Float>()
        var fijoIdx = 0
        var i = 0
        while (i < patron.size) {
            if (patron[i] == 'f') {
                var suma = 0f
                while (i < patron.size && patron[i] == 'f') {
                    suma += uPorFijo.getOrElse(fijoIdx) { 0f }
                    fijoIdx++
                    i++
                }
                result.add(suma)
            } else i++
        }
        return result
    }

    /**
     * U de fijos (ncfc): 1 perfil por módulo fijo, pero si hay fijos adyacentes (juntos) se SUMA
     * su medida en un solo perfil. Devuelve líneas "medida = cantidad" agrupadas. `uFijo` = medida
     * de un módulo fijo. Las corridas no cruzan el borde de tramo (ahí hay parante).
     */
    fun uFijosRunsNcfc(ancho: Float, divisiones: Int, uFijo: Float): String {
        val conteo = linkedMapOf<String, Int>()
        for (tramo in patronModulosNcfc(ancho, divisiones)) {
            var i = 0
            while (i < tramo.size) {
                if (tramo[i] == 'f') {
                    var run = 0
                    while (i < tramo.size && tramo[i] == 'f') { run++; i++ }
                    val w = df1(run * uFijo)
                    conteo[w] = (conteo[w] ?: 0) + 1
                } else i++
            }
        }
        return conteo.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    /**
     * Ancho de vidrio de fijos por COLINDANCIA (general, sirve para cualquier patrón de módulos).
     * Descuento por cada lado del fijo: pared/parante (borde de tramo/ventana) = 0.4; otro fijo =
     * 0.2; corrediza = 0. Devuelve descuento_total → cantidad de fijos con ese descuento, ordenado
     * de mayor a menor descuento (vidrios más angostos primero). El ancho final = uFijo − descuento.
     */
    /**
     * Descuento de vidrio de UN fijo por colindancia, por lado: pared/parante (borde del tramo)
     * 0.4, otro fijo 0.2, corrediza 0. Total = izquierda + derecha; un fijo entre dos corredizas
     * da 0. Primitivo compartido por el motor igual y el desigual.
     */
    fun descuentoColindanciaFijo(tramo: List<Char>, i: Int): Float {
        val izq = if (i == 0) 0.4f else if (tramo[i - 1] == 'c') 0f else 0.2f
        val der = if (i == tramo.size - 1) 0.4f else if (tramo[i + 1] == 'c') 0f else 0.2f
        return izq + der
    }

    fun descuentosVidrioFijos(tramos: List<List<Char>>): Map<Float, Int> {
        val m = LinkedHashMap<Float, Int>()
        for (tramo in tramos) {
            for (i in tramo.indices) {
                if (tramo[i] != 'f') continue
                val descuento = descuentoColindanciaFijo(tramo, i)
                m[descuento] = (m[descuento] ?: 0) + 1
            }
        }
        return m.entries.sortedByDescending { it.key }.associate { it.key to it.value }
    }

    /**
     * Ángulo tope por BORDES (general): 1 por cada corrediza que toca el inicio o el final de un
     * tramo (colinda con muro o parante). En esos bordes el ángulo tope reemplaza a la U de
     * parante. Primitivo compartido por el motor igual (ncfc) y el desigual.
     */
    fun anguloTopeBordes(tramos: List<List<Char>>): Int {
        var total = 0
        for (tramo in tramos) {
            if (tramo.isEmpty()) continue
            if (tramo.first() == 'c') total++
            if (tramo.last() == 'c') total++
        }
        return total
    }

    /** Ángulo tope (ncfc): 1 por cada corrediza que toca el inicio o el final del tramo. */
    fun anguloTopeCorredizasNcfc(ancho: Float, divisiones: Int): Int =
        anguloTopeBordes(patronModulosNcfc(ancho, divisiones))

    /**
     * Ancho de cada tramo (ncfc), igual que el dibujo y los puentes: anchoUtil/divisiones * nDiv.
     * Se usa como medida del riel y de la U felpera / fijo-corredizo (1 pieza por tramo).
     */
    fun anchosTramoNcfc(ancho: Float, divisiones: Int): List<Float> {
        if (ncfcUnTramo) return listOf(ancho)  // INA: un solo tramo, ancho completo.
        val grupos = gruposDivisionesMochetaPorModelo(ancho, divisiones, "ncfc")
        val nTramos = grupos.size
        val anchoUtil = if (nTramos > 1) ancho - (nTramos - 1) * 2.5f else ancho
        val anchoPorDiv = if (divisiones > 0) anchoUtil / divisiones else ancho
        return grupos.map { anchoPorDiv * it }
    }

    fun ordenDivisConParantes(divisiones: Int, ancho: Float): String {
        val nP = nPuentesEfectivos(ancho, divisiones)
        if (nP <= 1) return ordenDivis(divisiones, ancho)
        val f = "f<${df1(ancho / divisiones)}>"
        val c = "c<${df1(ancho / divisiones)}>"
        // Con reparto elegido a mano manda ese, no los casos escritos aquí abajo.
        val grupos = when {
            repartoManualPara(divisiones) != null -> {
                return gruposDivisionesPorTramo(ancho, divisiones).joinToString(";P;") { nDiv ->
                    ordenDivis(nDiv, (ancho / divisiones) * nDiv)
                }
            }
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

    /**
     * En cuántos TRAMOS se parte la ventana. Es el origen del que salen las cantidades de puente,
     * de U y de todo lo que va por tramo: se cuenta, no se saca de una tabla.
     *
     * El único límite es de **5 módulos por tramo**. El tamaño del módulo NO parte la ventana: si
     * el vidriero decide meter dos divisiones en una ventana de mil, es su decisión y se respeta.
     *
     * (Se probó a partir también por ancho de tramo, a 360 cm, y estaba mal: una ventana de 1000
     * con 5 divisiones salía en tres tramos, uno de ellos de un solo módulo.)
     */
    fun tramos(ancho: Float, divisiones: Int): Int {
        if (divisiones <= 0) return 1
        // Si el vidriero eligió un reparto a mano, los tramos son los suyos.
        repartoManualPara(divisiones)?.let { return it.size }
        return ceil(divisiones / 5.0).toInt().coerceAtLeast(1)
    }

    fun nPuentesEfectivos(ancho: Float, divisiones: Int): Int = tramos(ancho, divisiones)
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

    /**
     * Ancho de cada tramo, en el orden en que van. Sale del reparto de módulos: el ancho útil
     * (descontando los parantes entre tramos) repartido entre las divisiones, y cada tramo se
     * queda con los suyos. De aquí salen las medidas de puente y de riel.
     */
    fun medidasDeTramos(ancho: Float, divisiones: Int): List<Float> {
        if (divisiones <= 0 || ancho <= 0f) return emptyList()
        val grupos = gruposDivisionesPorTramo(ancho, divisiones)
        if (grupos.isEmpty()) return emptyList()
        val anchoUtil = ancho - (grupos.size - 1) * 2.5f
        val porDivision = anchoUtil / divisiones
        return grupos.map { porDivision * it }
    }

    /**
     * Medida del puente: el ancho del tramo. Cuando los tramos no son todos iguales —el reparto
     * de 14 es [5,4,5]— esta es la medida del tramo GRANDE y [mPuentes2] la del otro.
     *
     * Sale de contar el reparto, no de una tabla. Las tablas anteriores coincidían con el reparto
     * en 6, 8, 12, 14 y 16, y fallaban en 10 aparente: daban 178.5 y 238 donde los dos tramos
     * miden 298.7. El acabado no cambia la medida: el parante entre tramos es 2.5 en los dos.
     *
     *     apa: 1,2,3,4,5,7,9,11,13,15 -> ancho · 6,8 -> (ancho-2.5)/2
     *          10 -> (ancho-5)/div*3 · 12 -> (ancho-5)/3 · 14 -> (ancho-5)/div*5
     *     ina: igual, salvo 10 -> (ancho-2.5)/2
     */
    fun mPuentes1(ancho: Float, divisiones: Int, tipoVentana: String = "apa"): Float {
        val medidas = medidasDeTramos(ancho, divisiones)
        return medidas.maxOrNull() ?: ancho
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
    /**
     * Segunda medida de puente: la del tramo distinto, cuando el reparto no es parejo. Con 14
     * divisiones el reparto es [5,4,5], así que hay dos medidas y esta es la del tramo de 4. Si
     * todos los tramos miden igual devuelve 0, que es como se marca "no hay segunda medida".
     *
     * Tablas anteriores: apa `10 y 14 -> (ancho-5)/div*4`; ina `14 -> (ancho-5)/div*4`. La fila
     * de 10 en aparente sobraba: ese reparto es [5,5] y no tiene segunda medida.
     */
    fun mPuentes2(ancho: Float, divisiones: Int, tipoVentana: String = "apa"): Float {
        val medidas = medidasDeTramos(ancho, divisiones)
        val mayor = medidas.maxOrNull() ?: return 0f
        return medidas.firstOrNull { kotlin.math.abs(it - mayor) > 0.001f } ?: 0f
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
        return p.contains("multi") || p.contains("múlt") || p.contains("múlt") || p.contains("gorrito") || p.contains("ltiple")
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

    /**
     * Riel de full corredizas (nfc): por tramo, la MEDIDA es el ancho del tramo (igual que el
     * puente) y la CANTIDAD es el número de corredizas de ese tramo (1 por corrediza), no 1 por
     * tramo. Respeta el agrupamiento nfc (corredizas por tramo).
     */
    fun rielPorCorredizasNfc(ancho: Float, divisiones: Int, puente: String, valorSpinner: Float): String {
        if (divisiones <= 0) return ""
        val grupos = gruposDivisionesMochetaPorModelo(ancho, divisiones, "nfc")
        if (grupos.isEmpty()) return ""
        val anchosTramo = grupos.map { (ancho / divisiones) * it }
        val nParantes = (grupos.size - 1).coerceAtLeast(0)
        val parante = if (esPuenteMultipleOGorrito(puente)) 2.5f else valorSpinner
        val totalAnchos = anchosTramo.sum()
        val anchoDescontado = (ancho - (parante * nParantes)).coerceAtLeast(0f)
        val medidas = if (totalAnchos > 0f) anchosTramo.map { (it / totalAnchos) * anchoDescontado } else anchosTramo
        val conteo = linkedMapOf<String, Int>()
        for (idx in grupos.indices) {
            val key = df1(medidas[idx])
            conteo[key] = (conteo[key] ?: 0) + grupos[idx]   // cantidad = corredizas del tramo
        }
        return conteo.entries.joinToString("\n") { "${it.key} = ${it.value}" }
    }

    /**
     * Riel superior / doble-c de full corredizas (nfc): 1 cada dos corredizas (sin decimales →
     * se redondea hacia arriba), por tramo. Misma medida (ancho del tramo) que el riel.
     */
    fun rielSuperiorNfc(ancho: Float, divisiones: Int, puente: String, valorSpinner: Float): String {
        if (divisiones <= 0) return ""
        val grupos = gruposDivisionesMochetaPorModelo(ancho, divisiones, "nfc")
        if (grupos.isEmpty()) return ""
        val anchosTramo = grupos.map { (ancho / divisiones) * it }
        val nParantes = (grupos.size - 1).coerceAtLeast(0)
        val parante = if (esPuenteMultipleOGorrito(puente)) 2.5f else valorSpinner
        val totalAnchos = anchosTramo.sum()
        val anchoDescontado = (ancho - (parante * nParantes)).coerceAtLeast(0f)
        val medidas = if (totalAnchos > 0f) anchosTramo.map { (it / totalAnchos) * anchoDescontado } else anchosTramo
        val conteo = linkedMapOf<String, Int>()
        for (idx in grupos.indices) {
            val key = df1(medidas[idx])
            conteo[key] = (conteo[key] ?: 0) + ceil(grupos[idx] / 2.0).toInt()  // 1 cada 2 corredizas
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
    /**
     * TABLA ANTIGUA, solo de consulta: no la llame nadie. Cuenta mal en cuanto el número de
     * divisiones no es uno de los tabulados — con 7, 9, 11, 13 o 15 se queda en 2 sin mirar
     * cuántos tramos hay, y con 10 devuelve 6 cuando son dos tramos. Use [fijoUParante] con el
     * ancho, que cuenta los fijos en los extremos de cada tramo.
     *
     *     1 -> 2 · 2 -> 1 · 3,4,5,7,9,11,13,15 -> 2 · 6,8 -> 4 · 10,12,14 -> 6
     *     else -> nPuentes(divisiones) * 2
     */
    @Deprecated("Cuenta por tabla; use fijoUParante(divisiones, ancho)", ReplaceWith("fijoUParante(divisiones, ancho)"))
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
        if (divisiones <= 0) return 0
        val tramos = ordenDivisConParantes(divisiones, ancho)
            .split(";P;")
            .map { tramo -> tramo.filter { it == 'f' || it == 'c' } }
            .filter { it.isNotEmpty() }

        if (tramos.isEmpty()) return fijoUParante(divisiones)

        return tramos.sumOf { tramo ->
            var cantidad = 0
            if (tramo.first() == 'f') cantidad++
            if (tramo.last() == 'f') cantidad++
            cantidad
        }
    }

    /**
     * U parante de la mocheta: **dos por tramo**, una a cada lado.
     *
     * Sale de contar los tramos (ver [tramos]), no de una tabla. La versión anterior era una tabla
     * por número de divisiones que llegaba hasta 15:
     *
     *     1,2,3,4,5,7,9,11,13,15 -> 2
     *     6,8                    -> 4
     *     10,12,14               -> 6
     *
     * Esa tabla decía justamente "dos por tramo" en todos los casos menos en 10, donde metía la
     * ventana entre las de tres tramos y devolvía 6. Con 10 divisiones el diseño hace DOS tramos
     * de 5, así que le correspondían 4: una ventana de 586 pedía dos U de más.
     */
    fun mochetaUParante(divisiones: Int, ancho: Float): Int =
        if (divisiones <= 0) 0 else (2 * tramos(ancho, divisiones)).coerceAtLeast(2)
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

    fun cantidadTeePorTramosMocheta(
        ancho: Float,
        divisiones: Int,
        modelo: String = "nn",
        paranteAncho: Float = 2.5f
    ): Int {
        if (divisiones <= 0) return 0
        val grupos = gruposDivisionesMochetaPorModelo(ancho, divisiones, modelo)
        if (grupos.isEmpty()) return 0
        // La Tee usa el MISMO ancho ÚTIL por tramo que el vidrio de mocheta (ancho del tramo
        // menos los parantes entre tramos), para que el conteo de paños coincida siempre.
        val anchoPorDiv = ancho / divisiones
        val anchosBruto = grupos.map { it * anchoPorDiv }
        val nParantes = (grupos.size - 1).coerceAtLeast(0)
        val anchoTotalAjustado = (ancho - (nParantes * paranteAncho)).coerceAtLeast(0f)
        val suma = anchosBruto.sum()
        val anchosUtil = if (suma > 0f) anchosBruto.map { (it / suma) * anchoTotalAjustado } else anchosBruto
        return anchosUtil.sumOf { (anchMota(it) - 1).coerceAtLeast(0) }
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


