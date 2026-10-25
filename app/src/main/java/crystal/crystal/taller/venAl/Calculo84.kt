package crystal.crystal.taller.venAl

import crystal.crystal.taller.mamparas.MamparaModulos
import crystal.crystal.taller.mamparas.ModuloPedido
import crystal.crystal.taller.nova.NovaCalculos
import kotlin.math.abs
import kotlin.math.ceil

/**
 * Materiales de la clásica serie 84 y de la 84 económica (solo cambia el ancho de la pierna).
 * Cálculo puro, sin pantalla, para poder probarlo en la PC. Es la única fuente de la geometría:
 * el dibujo (VentanaAlRender) y el editor del diseño (DialogoDiseno84) leen de aquí.
 *
 * Reglas que sigue (docs/REGLAS_NOVA.md, sección 1, valen para todas las calculadoras):
 *  - G-06: "sin puente" es sí/no. Dictado el 2026-10-15: no lleva puente cuando la hoja llega al
 *    alto útil **menos el puente** (2.5): ahí la mocheta ya no cabe. Hoja 0 = 5/7 del alto útil.
 *  - G-07: nada de estado compartido: el reparto a mano que se elija en Nova no entra aquí.
 *  - G-11 / N-10: todo en float; `df1` (trunca) solo al presentar.
 *  - G-01 / N-14: primero lo horizontal; vidrios ancho x alto.
 *  - N-15 / N-19: regla de los 60 y máximo 5 hojas por tramo (el mismo reparto que el dibujo).
 *  - Franjas como en Nova (N-31, N-33, N-34 y sección 13.1): de abajo arriba; la mocheta se
 *    escribe como espacio, sin el puente; toda división entre franjas es un tubo del ancho del
 *    tramo; los altos sin escribir: sistema 5/7, mochetas el resto a partes iguales.
 *  - Marco (dictado el 2026-10-15, "la regla es uniformizar medidas"): cada parante de tramo va
 *    siempre de piso a techo (alto entero) y el marco superior e inferior sale en un trozo por
 *    tramo. 700 en tres tramos de 230.2 da `230.2 = 6`, no `462.9 = 2` y `230.2 = 2`.
 *
 * Fórmulas que ya tenía VentanaAl y se conservan:
 *  - hoja (ancho del módulo) = (ancho del tramo + cruces × 3.2) / módulos del tramo
 *  - zócalo = hoja − 2 × pierna (3 eco, 3.5 normal), 2 por módulo
 *  - pierna: fijo = altoHoja, corrediza = altoHoja − 1.4, 2 por módulo
 *  - vidrio: ancho = zócalo + 1.2; alto = pierna − 8
 *  - mocheta de espacio S: tubo separador = S; submódulos = ceil(tramo / 180);
 *    junquillos 2 + 2 por submódulo (el vertical descuenta 2 × junquillo);
 *    vidrio = (submódulo − 0.5) x (S + 2.5 − 2.9)
 *  - ángulo tope: largo de la pierna corrediza; 1 por tramo con dos corredizas juntas (fccf), o 1
 *    si no hay ninguno. Sin corredizas no lleva.
 */
object Calculo84 {

    /** Puente, parante de tramo y tubo de mocheta. */
    const val TUBO = 2.5f
    /** Lo que se monta una hoja sobre la otra en cada cruce fijo–corrediza. */
    const val CRUCE = 3.2f
    /** Diferencia entre la pierna del fijo y la de la corrediza. */
    const val DESCUENTO_CORREDIZA = 1.4f
    const val DESCUENTO_ALTO_VIDRIO = 8f
    const val AUMENTO_ANCHO_VIDRIO = 1.2f
    const val HOLGURA_VIDRIO_MOCHETA_ANCHO = 0.5f
    const val DESCUENTO_VIDRIO_MOCHETA_ALTO = 2.9f

    /** Una franja del tramo: 's' sistema (las hojas) o 'm' mocheta. `alto == null`: la que salga. */
    data class Franja(val tipo: Char, val alto: Float? = null) {
        val esSistema: Boolean get() = tipo == 's'
    }

    /** Un tramo tal como lo pide el usuario: sus hojas y sus franjas (de abajo arriba). */
    data class TramoPedido(val modulos: List<ModuloPedido>, val franjas: List<Franja> = FRANJAS_NORMAL) {
        val patron: String get() = modulos.joinToString("") { it.tipo.toString() }
    }

    /** Lo que sale solo: sistema abajo y mocheta encima (se cae si no cabe). */
    val FRANJAS_NORMAL = listOf(Franja('s'), Franja('m'))

    /** Un tramo ya resuelto: lo que se dibuja y lo que se corta. */
    data class TramoResuelto(
        val patron: String,
        val ancho: Float,
        /** Ancho de cada hoja, en el orden del patrón. */
        val hojas: List<Float>,
        /** Franjas con su alto ya resuelto, de abajo arriba (la mocheta como espacio). */
        val franjas: List<Franja>,
        /** Submódulos de cada mocheta (vidrios a lo ancho). */
        val mochetasVerticales: Int
    ) {
        val altoHoja: Float get() = franjas.first { it.esSistema }.alto ?: 0f
        val llevaPuente: Boolean get() = franjas.size > 1
    }

    data class Entrada(
        val ancho: Float,
        val alto: Float,
        /** Grosor del marco por lado. */
        val marco: Float = 2.2f,
        /** 0 = 5/7 del alto útil. */
        val altoHoja: Float = 0f,
        /** 0 = regla de los 60. */
        val divisiones: Int = 0,
        /** Junquillo que se descuenta en los verticales de la mocheta. */
        val junquillo: Float = 0f,
        /** 0 = una mocheta cada 180 cm; solo manda cuando la ventana es de un tramo. */
        val mochetas: Int = 0,
        /** Ancho de la pierna: 3 en la económica, 3.5 en la normal. */
        val pierna: Float = 3.5f,
        /** Diseño hecho a mano (ver [leerDiseno]); vacío o "tramos" = automático. */
        val diseno: String = ""
    )

    data class Resultado(
        val divisiones: Int,
        /** Patrón de cada tramo (fcf, fccf…), de izquierda a derecha. */
        val tramos: List<String>,
        val geometria: List<TramoResuelto>,
        val anchoUtil: Float,
        val altoUtil: Float,
        /** Alto de hoja del primer tramo (con diseño a mano cada tramo puede tener el suyo). */
        val altoHoja: Float,
        /** Algún tramo lleva puente. */
        val llevaPuente: Boolean,
        val nFijos: Int,
        val nCorredizas: Int,
        val manual: Boolean,
        /** Algo del diseño a mano no se pudo respetar (no cabía). */
        val aviso: String?,
        // Cada lista: "medida = cantidad", ya agrupadas y en el orden de G-01.
        val marco: List<String>,
        val rielSup: List<String>,
        val rielInf: List<String>,
        val pierna: List<String>,
        val zocalo: List<String>,
        val tope: List<String>,
        val tubo: List<String>,
        val junquillo: List<String>,
        val vidrios: List<String>
    )

    /** Divisiones: las escritas mandan (G-03); si no, la regla de los 60 (N-15). */
    fun divisiones(ancho: Float, escritas: Int): Int =
        if (escritas > 0) escritas else ceil(ancho / 60f).toInt().coerceAtLeast(1)

    /** Alto de hoja: 0 = 5/7 del alto útil; nunca mayor que el alto útil. */
    fun altoHoja(altoUtil: Float, escrita: Float): Float =
        if (escrita <= 0f) altoUtil / 7f * 5f else minOf(escrita, altoUtil)

    /** G-06: lleva puente solo si encima de la hoja cabe la mocheta, con su puente. */
    fun llevaPuente(altoUtil: Float, altoHoja: Float): Boolean = altoHoja < altoUtil - TUBO

    /**
     * Patrón de cada tramo con el reparto automático de Nova (máximo 5 por tramo). El reparto a
     * mano de Nova es estado de otra calculadora (G-07) y se aparta mientras se consulta.
     */
    fun patronesPorTramo(divisiones: Int, ancho: Float): List<String> {
        val previo = NovaCalculos.repartoManual
        NovaCalculos.repartoManual = null
        try {
            return NovaCalculos.gruposDivisionesPorTramo(ancho, divisiones).map { n ->
                Regex("[fc](?=<)").findAll(NovaCalculos.ordenDivis(n, ancho / divisiones * n))
                    .joinToString("") { it.value }
            }
        } finally {
            NovaCalculos.repartoManual = previo
        }
    }

    /** Cruces de un tramo: cada fijo junto a una corrediza (dos corredizas no se cruzan). */
    fun cruces(patron: String): Int = patron.zipWithNext().count { (a, b) -> a != b }

    // ---------------------------------------------------------------- diseño a mano

    /**
     * Diseño a mano: tramos separados por `;`, y en cada uno las hojas y, tras `@`, las franjas de
     * abajo arriba. Las hojas como en la mampara paflón (`f<60>c`, el número es el ancho de la
     * hoja); las franjas `s` (sistema, su número es el alto de hoja) y `m` (mocheta, su espacio sin
     * el puente). Ejemplo: `fc@sm;f<80>cf@m<20>sm`.
     *
     * No usa `|`: el diseño archivado (VentanaAlDescriptor) separa sus campos con él. Null si está
     * vacío, es "tramos" (el automático de antes) o no describe una ventana utilizable.
     */
    fun leerDiseno(raw: String?): List<TramoPedido>? {
        val s = raw?.trim().orEmpty()
        if (s.isEmpty() || s.equals("tramos", ignoreCase = true)) return null
        val tramos = s.split(";").map { bruto ->
            val partes = bruto.trim().lowercase().split("@")
            if (partes.size > 2) return null
            val modulos = MamparaModulos.patronManual(partes[0])?.singleOrNull() ?: return null
            val franjas = if (partes.size == 2) leerFranjas(partes[1]) ?: return null else FRANJAS_NORMAL
            TramoPedido(modulos, franjas)
        }
        return tramos.ifEmpty { null }
    }

    private fun leerFranjas(t: String): List<Franja>? {
        val franjas = mutableListOf<Franja>()
        var i = 0
        while (i < t.length) {
            val tipo = t[i]
            if (tipo != 's' && tipo != 'm') return null
            i++
            var alto: Float? = null
            if (i < t.length && t[i] == '<') {
                val fin = t.indexOf('>', i)
                if (fin < 0) return null
                alto = t.substring(i + 1, fin).toFloatOrNull()?.takeIf { it > 0f } ?: return null
                i = fin + 1
            }
            franjas += Franja(tipo, alto)
        }
        // Un sistema, ni más ni menos: es la franja que lleva las hojas.
        return if (franjas.count { it.esSistema } == 1) franjas else null
    }

    fun escribirDiseno(tramos: List<TramoPedido>): String = tramos.joinToString(";") { t ->
        MamparaModulos.serializarPatron(listOf(t.modulos)) + "@" +
            t.franjas.joinToString("") { f -> f.tipo + (f.alto?.let { "<${fmt(it)}>" } ?: "") }
    }

    private fun fmt(v: Float) = "%.1f".format(v).replace(",", ".")

    /**
     * Altos de las franjas de un tramo. Las mochetas sin alto se reparten lo que queda; si todas
     * lo llevan, el sistema sin alto se queda con el resto, y si también lo lleva, la de arriba
     * absorbe la diferencia. Una mocheta que no cabe (espacio ≤ 0) se quita con su puente: así
     * la hoja de alto útil − 2.5 queda sin puente (G-06).
     */
    fun resolverFranjas(pedidas: List<Franja>, altoUtil: Float, hojaBase: Float): List<Franja> {
        val altos = altosDeFranjas(pedidas, altoUtil, hojaBase)
        return pedidas.indices.mapNotNull { i -> altos[i]?.let { Franja(pedidas[i].tipo, it) } }
    }

    /** El alto que le toca a cada franja pedida, en su mismo orden; null en la mocheta que no cabe. */
    fun altosDeFranjas(pedidas: List<Franja>, altoUtil: Float, hojaBase: Float): List<Float?> {
        var vivas = pedidas.indices.toList()
        while (true) {
            val r = repartirFranjas(vivas.map { pedidas[it] }, altoUtil, hojaBase)
            val sinSitio = r.indices.filter { !r[it].esSistema && (r[it].alto ?: 0f) <= 0.05f }
            if (sinSitio.isEmpty()) return pedidas.indices.map { i -> vivas.indexOf(i).takeIf { it >= 0 }?.let { r[it].alto } }
            vivas = vivas.filterIndexed { j, _ -> j !in sinSitio }
        }
    }

    private fun repartirFranjas(fs: List<Franja>, altoU: Float, hojaBase: Float): List<Franja> {
        if (fs.size <= 1) return listOf(Franja('s', altoU))
        val espacio = altoU - (fs.size - 1) * TUBO
        val sistema = fs.first { it.esSistema }
        val mochetas = fs.filter { !it.esSistema }
        val fijado = mochetas.sumOf { (it.alto ?: 0f).toDouble() }.toFloat()
        val libres = mochetas.count { it.alto == null }
        return when {
            libres > 0 -> {
                val hoja = minOf(sistema.alto ?: hojaBase, altoU)
                val libre = (espacio - hoja - fijado) / libres
                fs.map { if (it.esSistema) Franja('s', hoja) else Franja('m', it.alto ?: libre) }
            }
            sistema.alto == null -> fs.map { if (it.esSistema) Franja('s', espacio - fijado) else it }
            else -> {
                val dif = espacio - sistema.alto - fijado
                fs.mapIndexed { i, f -> if (i == fs.lastIndex) f.copy(alto = (f.alto ?: 0f) + dif) else f }
            }
        }
    }

    /**
     * Ancho de las hojas de cada tramo. Sin anchos escritos, como siempre: cada tramo se lleva su
     * parte del ancho según sus hojas. Con alguno escrito, las hojas libres se reparten lo que
     * queda, a lo largo de toda la ventana. Null si las escritas no caben.
     */
    private fun anchosDeHojas(pedidos: List<TramoPedido>, anchoTramos: Float): List<List<Float>>? {
        val div = pedidos.sumOf { it.modulos.size }
        if (pedidos.none { t -> t.modulos.any { it.ancho != null } }) {
            return pedidos.map { t ->
                val n = t.modulos.size
                val anchoTramo = anchoTramos * n / div
                val hoja = if (n == 1) anchoTramo else (anchoTramo + cruces(t.patron) * CRUCE) / n
                List(n) { hoja }
            }
        }
        val fijado = pedidos.sumOf { t -> t.modulos.sumOf { (it.ancho ?: 0f).toDouble() } }.toFloat()
        val libres = pedidos.sumOf { t -> t.modulos.count { it.ancho == null } }
        val totalHojas = anchoTramos + pedidos.sumOf { cruces(it.patron) } * CRUCE
        if (libres == 0) return if (abs(totalHojas - fijado) <= 0.15f) pedidos.map { t -> t.modulos.map { it.ancho!! } } else null
        val libre = (totalHojas - fijado) / libres
        if (libre <= 0f) return null
        return pedidos.map { t -> t.modulos.map { it.ancho ?: libre } }
    }

    // ---------------------------------------------------------------- cálculo

    fun calcular(e: Entrada): Resultado {
        val anchoU = e.ancho - 2 * e.marco
        val altoU = e.alto - 2 * e.marco
        val hojaBase = altoHoja(altoU, e.altoHoja)
        val manual = leerDiseno(e.diseno)
        val automatico = { div: Int -> patronesPorTramo(div, anchoU).map { p -> TramoPedido(p.map { ModuloPedido(it) }) } }
        var pedidos = manual ?: automatico(divisiones(e.ancho, e.divisiones))
        val nT = pedidos.size
        val anchoTramos = anchoU - (nT - 1) * TUBO

        var aviso: String? = null
        val hojas = anchosDeHojas(pedidos, anchoTramos) ?: run {
            aviso = "Las hojas con ancho escrito no caben: se repartieron por igual."
            pedidos = pedidos.map { t -> t.copy(modulos = t.modulos.map { it.copy(ancho = null) }) }
            anchosDeHojas(pedidos, anchoTramos)!!
        }
        val div = pedidos.sumOf { it.modulos.size }

        val geometria = pedidos.mapIndexed { i, t ->
            var franjas = resolverFranjas(t.franjas, altoU, hojaBase)
            if ((franjas.first { it.esSistema }.alto ?: 0f) <= 0f) {
                aviso = "Las mochetas escritas no dejan sitio a la hoja: se usó la hoja de la pantalla."
                franjas = resolverFranjas(FRANJAS_NORMAL, altoU, hojaBase)
            }
            val anchoTramo = hojas[i].sum() - cruces(t.patron) * CRUCE
            TramoResuelto(
                patron = t.patron,
                ancho = if (t.modulos.size == 1) hojas[i][0] else anchoTramo,
                hojas = hojas[i],
                franjas = franjas,
                mochetasVerticales = if (nT == 1 && e.mochetas > 0) e.mochetas else NovaCalculos.anchMota(anchoTramo)
            )
        }

        val marco = Acumulador(); val rielS = Acumulador(); val rielI = Acumulador()
        val pierna = Acumulador(); val zocalo = Acumulador(); val tope = Acumulador()
        val tubo = Acumulador(); val junq = Acumulador(); val vidrios = Acumulador()
        val vidriosMocheta = Acumulador()
        var nFijos = 0
        var nCorr = 0

        // Horizontales primero (G-01): el marco superior e inferior, un trozo por tramo (el
        // parante de tramo va de piso a techo y lo corta).
        for (g in geometria) marco.add(g.ancho, 2)
        marco.add(e.alto, 2)

        for (g in geometria) {
            val hoja = g.altoHoja
            val piernaFijo = hoja
            val piernaCorr = hoja - DESCUENTO_CORREDIZA
            rielS.add(g.ancho, 1)
            rielI.add(g.ancho, 1)

            // Fijos y luego corredizas, como salían antes.
            for (fijo in listOf(true, false)) {
                for (k in g.hojas.indices) {
                    if ((g.patron[k] == 'f') != fijo) continue
                    val z = g.hojas[k] - 2 * e.pierna
                    val largoPierna = if (fijo) piernaFijo else piernaCorr
                    zocalo.add(z, 2)
                    pierna.add(largoPierna, 2)
                    vidrios.addVidrio(z + AUMENTO_ANCHO_VIDRIO, largoPierna - DESCUENTO_ALTO_VIDRIO, 1)
                    if (fijo) nFijos++ else nCorr++
                }
            }

            // Una corrida de tubo por cada división entre franjas, del ancho del tramo.
            tubo.add(g.ancho, g.franjas.size - 1)
            val nSub = g.mochetasVerticales
            val anchoSub = (g.ancho - (nSub - 1) * TUBO) / nSub
            for (m in g.franjas.filter { !it.esSistema }) {
                val espacio = m.alto ?: 0f
                junq.add(anchoSub, 2 * nSub)
                junq.add(espacio - 2 * e.junquillo, 2 * nSub)
                vidriosMocheta.addVidrio(
                    anchoSub - HOLGURA_VIDRIO_MOCHETA_ANCHO,
                    espacio + TUBO - DESCUENTO_VIDRIO_MOCHETA_ALTO, nSub
                )
                tubo.addDespues(espacio, nSub - 1)           // separadores de mocheta
            }
        }
        // Parantes de tramo, lleve o no puente: siempre de piso a techo.
        tubo.addDespues(e.alto, nT - 1)

        val conCorrediza = geometria.filter { g -> g.patron.contains('c') }
        val conTope = conCorrediza.filter { it.patron.contains("cc") }
        if (conTope.isNotEmpty()) conTope.forEach { tope.add(it.altoHoja - DESCUENTO_CORREDIZA, 1) }
        else conCorrediza.firstOrNull()?.let { tope.add(it.altoHoja - DESCUENTO_CORREDIZA, 1) }

        return Resultado(
            divisiones = div, tramos = geometria.map { it.patron }, geometria = geometria,
            anchoUtil = anchoU, altoUtil = altoU,
            altoHoja = geometria.first().altoHoja, llevaPuente = geometria.any { it.llevaPuente },
            nFijos = nFijos, nCorredizas = nCorr, manual = manual != null, aviso = aviso,
            marco = marco.lineas(), rielSup = rielS.lineas(), rielInf = rielI.lineas(),
            pierna = pierna.lineas(), zocalo = zocalo.lineas(), tope = tope.lineas(),
            tubo = tubo.lineas(), junquillo = junq.lineas(),
            vidrios = vidrios.lineas() + vidriosMocheta.lineas()
        )
    }

    /** Agrupa piezas iguales (por su medida ya presentada) conservando el orden de llegada. */
    private class Acumulador {
        private val horizontales = LinkedHashMap<String, Int>()
        private val verticales = LinkedHashMap<String, Int>()

        fun add(medida: Float, cantidad: Int) = sumar(horizontales, NovaCalculos.df1(medida), cantidad)

        /** Lo vertical va después de lo horizontal (G-01). */
        fun addDespues(medida: Float, cantidad: Int) = sumar(verticales, NovaCalculos.df1(medida), cantidad)

        fun addVidrio(ancho: Float, alto: Float, cantidad: Int) =
            sumar(horizontales, "${NovaCalculos.df1(ancho)} x ${NovaCalculos.df1(alto)}", cantidad)

        private fun sumar(m: LinkedHashMap<String, Int>, clave: String, cantidad: Int) {
            if (cantidad > 0) m[clave] = (m[clave] ?: 0) + cantidad
        }

        fun lineas(): List<String> = (horizontales.entries + verticales.entries).map { "${it.key} = ${it.value}" }
    }
}
