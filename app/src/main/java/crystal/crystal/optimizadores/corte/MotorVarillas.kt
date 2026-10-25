package crystal.crystal.optimizadores.corte

import kotlin.random.Random

/**
 * El llenado de varillas de la Fórmula 1, con el llenado de cada varilla EXACTO (2026-10-05).
 *
 * La Fórmula 1 es la del taller: se toma el corte mayor que falta, se pone en la varilla menor que
 * lo admite y la varilla se llena con lo que mejor cabe al lado. En junio el "lo que mejor cabe" pasó
 * a ser una búsqueda cortada a 200 × nivel pasos, que casi nunca salía de las primeras combinaciones:
 * varillas con mucho retazo, el nivel sin efecto y, con las varillas justas, piezas que se quedaban
 * fuera aunque alcanzaban. Aquí el llenado de cada varilla es la mejor combinación que existe (una
 * mochila acotada sobre los largos), y el nivel es tiempo: con más nivel se prueban más maneras de
 * desempatar entre llenados igual de buenos y se queda la mejor solución entera.
 *
 * El modelo del disco es el de siempre: [Corte][Disco][Corte]...[Corte][Retazo], es decir
 * suma + (cortes - 1) × disco ≤ varilla, que es lo mismo que suma de (corte + disco) ≤ varilla + disco.
 *
 * Qué solución es mejor, en este orden: menos piezas sin cortar, menos varillas, menos largo de
 * varilla gastado y el retazo más junto (un retazo grande sirve; muchos chicos, no).
 */
internal object MotorVarillas {

    /** Una pieza a cortar: su largo en cm, cuántas y su referencia. */
    data class Pieza(val largo: Float, val cantidad: Int, val referencia: String)

    /** Varillas en existencia: su largo en cm y cuántas hay. */
    data class Stock(val largo: Float, val cantidad: Int)

    /** Una varilla ya cortada: su largo y sus cortes (largo y referencia), en el orden en que se cortan. */
    data class Barra(val largo: Float, val cortes: List<Pair<Float, String>>, val retazo: Float)

    /** Lo que sale: las varillas cortadas, ordenadas de menos a más retazo. */
    data class Solucion(val barras: List<Barra>, val sinCortar: Int)

    // Cuánto se busca. El nivel (1 a 20) son intentos de vaciar una varilla, no tiempo: así cada nivel
    // hace lo del anterior y algo más, y nunca sale peor.
    private const val INTENTOS_FASE_1 = 30
    private const val VUELTAS_PARA_LO_QUE_FALTA = 400
    private const val INTENTOS_POR_NIVEL = 2
    private const val VUELTAS_POR_INTENTO = 60
    /** Un tope de reloj que no debería alcanzarse nunca: solo protege de listas desmedidas. */
    private const val MS_TOPE_POR_NIVEL = 3000L

    fun optimizar(piezas: List<Pieza>, stock: List<Stock>, disco: Float, nivel: Int): Solucion {
        val tipos = piezas.filter { it.cantidad > 0 && it.largo > 0f }
        val existencias = stock.filter { it.cantidad > 0 && it.largo > 0f }
        if (tipos.isEmpty() || existencias.isEmpty()) return Solucion(emptyList(), tipos.sumOf { it.cantidad })

        // Todo en enteros, a la centésima de cm y reducido por el máximo común divisor: la mochila
        // trabaja sobre un arreglo del largo de la varilla y así queda chico (600 cm → 6000).
        fun u(cm: Float) = Math.round(cm * 100f)
        val d = u(disco).coerceAtLeast(0)
        var m = 0
        fun mcd(a: Int, b: Int): Int = if (b == 0) a else mcd(b, a % b)
        tipos.forEach { m = mcd(m, u(it.largo) + d) }
        existencias.forEach { m = mcd(m, u(it.largo) + d) }
        if (m <= 0) m = 1
        val peso = IntArray(tipos.size) { (u(tipos[it].largo) + d) / m }
        val capacidad = IntArray(existencias.size) { (u(existencias[it].largo) + d) / m }

        val nivelUsado = nivel.coerceIn(1, 20)
        val inicio = System.currentTimeMillis()
        // Solo por si acaso: una lista enorme en un celular lento no se queda colgada.
        val tope = inicio + nivelUsado * MS_TOPE_POR_NIVEL
        val azar = Random(20261005)
        val cotaInferior = cotaDeVarillas(tipos, peso, existencias, capacidad)

        // Fase 1: la Fórmula 1, varilla por varilla, con desempates distintos. Igual en todo nivel.
        var mejor = construir(tipos, peso, existencias, capacidad, disco, null)
        var intentos = 1
        while (intentos < INTENTOS_FASE_1 && !esInmejorable(mejor, cotaInferior, tipos, disco)) {
            val otra = construir(tipos, peso, existencias, capacidad, disco, azar)
            if (esMejor(otra, mejor)) mejor = otra
            intentos++
        }
        val fase1 = mejor.barras.size to mejor.sinCortar

        // Fase 2: todas las varillas a la vez (ver [Reparto]). Llenando de una en una, las primeras
        // varillas se comen las piezas chicas y al final quedan las grandes de a dos, con mucho
        // retazo; aquí se mete lo que quedó sin cortar y se vacía la varilla menos cargada repartiendo
        // sus piezas en las demás. Cada nivel son más intentos. Caso real: Lucha y Bilbao, 15 → 14.
        if (!esInmejorable(mejor, cotaInferior, tipos, disco)) {
            val reparto = Reparto.desde(mejor, tipos, peso, existencias, capacidad, azar)
            reparto.meterLoQueFalta(VUELTAS_PARA_LO_QUE_FALTA, tope)
            reparto.vaciarVarillas(nivelUsado * INTENTOS_POR_NIVEL, VUELTAS_POR_INTENTO, cotaInferior, tope)
            val otra = reparto.solucion(tipos, existencias, disco)
            if (esMejor(otra, mejor)) mejor = otra
        }
        android.util.Log.d(
            "MotorVarillas",
            "nivel=$nivelUsado ${System.currentTimeMillis() - inicio}ms fase1=${fase1.first}/${fase1.second} final=${mejor.barras.size}/${mejor.sinCortar} cota=$cotaInferior"
        )
        return mejor.copy(barras = mejor.barras.sortedWith(compareBy<Barra> { it.retazo }.thenByDescending { it.largo }))
    }

    /**
     * Las piezas repartidas en varillas, una por una, para buscar con todas a la vez.
     *
     * La jugada de siempre es la misma: se toma una varilla con lo que tiene más lo que está sin
     * cortar (la "bolsa") y se queda con la combinación que más la llena; lo que no entra vuelve a la
     * bolsa. Nunca deja la bolsa más pesada, y entre llenados igual de buenos elige al azar, que es lo
     * que la hace caminar. Cuando se estanca, una sacudida: una pieza de la bolsa entra a la fuerza en
     * una varilla, sacando lo que haga falta.
     */
    private class Reparto(
        val peso: IntArray,
        /** De qué tipo es cada pieza suelta. */
        val tipoDe: IntArray,
        val varillas: MutableList<Varilla>,
        val bolsa: MutableList<Int>,
        val azar: Random
    ) {
        class Varilla(val existencia: Int, val capacidad: Int, val piezas: MutableList<Int>)

        fun carga(v: Varilla) = v.piezas.sumOf { peso[it] }

        /** La combinación de [candidatas] que más llena [capacidad]; los empates, al azar. */
        fun mejorLlenado(candidatas: List<Int>, capacidad: Int): List<Int> {
            // De mayor a menor con algo de desorden: los empates salen distintos y las chicas, que
            // sirven para completar, tienden a quedar en la bolsa.
            val orden = candidatas.sortedByDescending { peso[it] }.toMutableList()
            for (i in 0 until orden.size - 1) if (azar.nextFloat() < 0.4f) {
                val t = orden[i]; orden[i] = orden[i + 1]; orden[i + 1] = t
            }
            val alcanzado = BooleanArray(capacidad + 1)
            val desde = IntArray(capacidad + 1) { -1 }
            alcanzado[0] = true
            var mejor = 0
            for (k in orden.indices) {
                val w = peso[orden[k]]
                if (w > capacidad) continue
                for (s in capacidad downTo w) {
                    if (!alcanzado[s] && alcanzado[s - w]) {
                        alcanzado[s] = true
                        desde[s] = k
                        if (s > mejor) mejor = s
                    }
                }
                if (mejor == capacidad) break
            }
            val elegidas = ArrayList<Int>()
            var s = mejor
            while (s > 0) {
                val k = desde[s]
                elegidas.add(orden[k])
                s -= peso[orden[k]]
            }
            return elegidas
        }

        /** Una vuelta por todas las varillas; true si la bolsa quedó más liviana. */
        private fun vuelta(): Boolean {
            val antes = bolsa.sumOf { peso[it] }
            for (v in varillas.shuffled(azar)) {
                if (bolsa.isEmpty()) break
                val todas = v.piezas + bolsa
                val quedan = mejorLlenado(todas, v.capacidad)
                if (quedan.sumOf { peso[it] } < carga(v)) continue
                val resto = todas.toMutableList()
                quedan.forEach { resto.remove(it) }
                v.piezas.clear(); v.piezas.addAll(quedan)
                bolsa.clear(); bolsa.addAll(resto)
            }
            return bolsa.sumOf { peso[it] } < antes
        }

        /** Una pieza de la bolsa entra a la fuerza en una varilla al azar; lo que sobra sale a la bolsa. */
        private fun sacudir() {
            if (bolsa.isEmpty() || varillas.isEmpty()) return
            val p = bolsa[azar.nextInt(bolsa.size)]
            val v = varillas[azar.nextInt(varillas.size)]
            if (peso[p] > v.capacidad) return
            bolsa.remove(p)
            v.piezas.add(p)
            while (carga(v) > v.capacidad) {
                val candidatas = v.piezas.filter { it != p }
                if (candidatas.isEmpty()) break
                val fuera = candidatas[azar.nextInt(candidatas.size)]
                v.piezas.remove(fuera)
                bolsa.add(fuera)
            }
        }

        /** El momento de la búsqueda con la bolsa más liviana: si no se vacía, es lo mejor que hubo. */
        private var masLiviana: Pair<List<Varilla>, List<Int>>? = null

        /**
         * Busca hasta vaciar la bolsa, como mucho [vueltas] vueltas (o hasta [tope], que solo salta
         * en listas enormes en un celular lento). True si se vació.
         */
        fun vaciarBolsa(vueltas: Int, tope: Long): Boolean {
            var quietas = 0
            var pesoMinimo = bolsa.sumOf { peso[it] }
            masLiviana = copia()
            var n = 0
            while (bolsa.isNotEmpty() && n < vueltas && System.currentTimeMillis() < tope) {
                n++
                if (vuelta()) quietas = 0 else quietas++
                val pesoBolsa = bolsa.sumOf { peso[it] }
                if (pesoBolsa < pesoMinimo) {
                    pesoMinimo = pesoBolsa
                    masLiviana = copia()
                }
                if (quietas >= 3) { sacudir(); quietas = 0 }
            }
            return bolsa.isEmpty()
        }

        /** Lo que quedó sin cortar en la Fórmula 1, metido en las varillas que hay. */
        fun meterLoQueFalta(vueltas: Int, tope: Long) {
            if (bolsa.isEmpty()) return
            val respaldo = copia()
            if (!vaciarBolsa(vueltas, tope)) restaurar(respaldo)
            else juntarRetazo()
        }

        /**
         * Se vacía una varilla y sus piezas se reparten en las demás: [intentos] veces, de
         * [vueltasPorIntento] vueltas cada una. Contados y no por reloj, para que cada nivel haga lo
         * mismo que el anterior y algo más —nunca sale peor— y la misma lista dé lo mismo en cualquier
         * celular.
         */
        fun vaciarVarillas(intentos: Int, vueltasPorIntento: Int, cota: Int, tope: Long) {
            if (bolsa.isNotEmpty()) return
            juntarRetazo()
            var fallos = 0
            var hechos = 0
            while (varillas.size > cota && hechos < intentos && System.currentTimeMillis() < tope) {
                hechos++
                val respaldo = copia()
                // La menos cargada casi siempre; a veces otra, para no insistir en la misma.
                val porCarga = varillas.sortedBy { carga(it) }
                val vaciada = porCarga[if (fallos == 0) 0 else azar.nextInt(minOf(3, porCarga.size))]
                varillas.remove(vaciada)
                bolsa.addAll(vaciada.piezas)
                if (vaciarBolsa(vueltasPorIntento, tope)) {
                    juntarRetazo()
                    fallos = 0
                    continue
                }
                // No se pudo vaciar, pero la varilla se queda con lo mínimo que hubo en la bolsa: las
                // demás más llenas y el retazo junto en ella. Así cada intento de más sirve.
                val liviana = masLiviana
                val antes = concentracion(respaldo.first)
                restaurar(respaldo)
                if (liviana != null) {
                    varillas.clear()
                    varillas.addAll(liviana.first.map { Varilla(it.existencia, it.capacidad, it.piezas.toMutableList()) })
                    varillas.add(Varilla(vaciada.existencia, vaciada.capacidad, liviana.second.toMutableList()))
                    bolsa.clear()
                    juntarRetazo()
                    if (concentracion(varillas) <= antes) restaurar(respaldo)
                }
                fallos++
            }
        }

        /** Cuánto está junto el retazo: la suma de los cuadrados de lo que sobra en cada varilla. */
        private fun concentracion(vs: List<Varilla>): Long = vs.sumOf { v ->
            val sobra = (v.capacidad - v.piezas.sumOf { peso[it] }).toLong()
            sobra * sobra
        }

        /**
         * Con las mismas varillas, el retazo junto: de cada par de varillas, una se llena lo más que
         * puede con las piezas de las dos y la otra se queda con el resto. Un retazo grande sirve;
         * dos medianos, no.
         */
        fun juntarRetazo() {
            var cambio = true
            var vueltas = 0
            while (cambio && vueltas < 4) {
                cambio = false
                vueltas++
                for (i in varillas.indices) for (j in varillas.indices) {
                    if (i == j) continue
                    val a = varillas[i]
                    val b = varillas[j]
                    val todas = a.piezas + b.piezas
                    val llenaA = mejorLlenado(todas, a.capacidad)
                    val cargaA = llenaA.sumOf { peso[it] }
                    if (cargaA <= carga(a)) continue
                    val resto = todas.toMutableList()
                    llenaA.forEach { resto.remove(it) }
                    if (resto.sumOf { peso[it] } > b.capacidad) continue
                    a.piezas.clear(); a.piezas.addAll(llenaA)
                    b.piezas.clear(); b.piezas.addAll(resto)
                    cambio = true
                }
            }
            varillas.removeAll { it.piezas.isEmpty() }
        }

        private fun copia() = varillas.map { Varilla(it.existencia, it.capacidad, it.piezas.toMutableList()) } to bolsa.toList()

        private fun restaurar(c: Pair<List<Varilla>, List<Int>>) {
            varillas.clear(); varillas.addAll(c.first)
            bolsa.clear(); bolsa.addAll(c.second)
        }

        fun solucion(tipos: List<Pieza>, existencias: List<Stock>, disco: Float): Solucion {
            val barras = varillas.filter { it.piezas.isNotEmpty() }.map { v ->
                val cortes = v.piezas.map { tipos[tipoDe[it]].largo to tipos[tipoDe[it]].referencia }
                    .sortedByDescending { it.first }
                val largo = existencias[v.existencia].largo
                val gastado = cortes.sumOf { it.first.toDouble() }.toFloat() + (cortes.size - 1).coerceAtLeast(0) * disco
                Barra(largo, cortes, (largo - gastado).coerceAtLeast(0f))
            }
            return Solucion(barras, bolsa.size)
        }

        companion object {
            /** De la solución de la Fórmula 1: cada corte pasa a ser una pieza suelta. */
            fun desde(
                s: Solucion, tipos: List<Pieza>, peso: IntArray,
                existencias: List<Stock>, capacidad: IntArray, azar: Random
            ): Reparto {
                val tipoDe = ArrayList<Int>()
                val libres = tipos.indices.map { ArrayDeque<Int>() }
                tipos.forEachIndexed { t, p -> repeat(p.cantidad) { libres[t].add(tipoDe.size); tipoDe.add(t) } }
                fun tipoDeCorte(largo: Float, ref: String) =
                    tipos.indices.first { tipos[it].largo == largo && tipos[it].referencia == ref && libres[it].isNotEmpty() }
                val usadas = IntArray(existencias.size)
                val varillas = s.barras.map { b ->
                    val e = existencias.indices.first { existencias[it].largo == b.largo && usadas[it] < existencias[it].cantidad }
                    usadas[e]++
                    Varilla(e, capacidad[e], b.cortes.map { (l, r) -> libres[tipoDeCorte(l, r)].removeFirst() }.toMutableList())
                }.toMutableList()
                val bolsa = libres.flatten().toMutableList()
                val pesoPieza = IntArray(tipoDe.size) { peso[tipoDe[it]] }
                return Reparto(pesoPieza, tipoDe.toIntArray(), varillas, bolsa, azar)
            }
        }
    }

    /** Las varillas que como mínimo hacen falta, si todas fueran de la más larga: no se puede bajar de ahí. */
    private fun cotaDeVarillas(tipos: List<Pieza>, peso: IntArray, existencias: List<Stock>, capacidad: IntArray): Int {
        val total = tipos.indices.sumOf { peso[it].toLong() * tipos[it].cantidad }
        val mayor = capacidad.maxOrNull()?.toLong() ?: return 0
        return ((total + mayor - 1) / mayor).toInt()
    }

    /** Ya no hay nada que buscar: todo cortado, en las varillas mínimas y con el retazo en una sola. */
    private fun esInmejorable(s: Solucion, cota: Int, tipos: List<Pieza>, disco: Float): Boolean {
        if (s.sinCortar > 0 || s.barras.size > cota) return false
        val menor = tipos.minOf { it.largo } + disco
        return s.barras.count { it.retazo >= menor } <= 1
    }

    private fun esMejor(a: Solucion, b: Solucion): Boolean {
        if (a.sinCortar != b.sinCortar) return a.sinCortar < b.sinCortar
        if (a.barras.size != b.barras.size) return a.barras.size < b.barras.size
        val largoA = a.barras.sumOf { it.largo.toDouble() }
        val largoB = b.barras.sumOf { it.largo.toDouble() }
        if (kotlin.math.abs(largoA - largoB) > 0.001) return largoA < largoB
        // El retazo junto: con el mismo total, más vale uno grande que muchos chicos.
        return a.barras.sumOf { it.retazo.toDouble() * it.retazo } > b.barras.sumOf { it.retazo.toDouble() * it.retazo } + 0.001
    }

    /**
     * Una pasada de la Fórmula 1. Sin [azar] es la de siempre; con azar, los llenados igual de buenos
     * se desempatan de otra manera y, a veces, la varilla arranca con uno de los tres cortes mayores
     * en vez del mayor.
     */
    private fun construir(
        tipos: List<Pieza>, peso: IntArray, existencias: List<Stock>, capacidad: IntArray,
        disco: Float, azar: Random?
    ): Solucion {
        val faltan = IntArray(tipos.size) { tipos[it].cantidad }
        val quedan = IntArray(existencias.size) { existencias[it].cantidad }
        // Los cortes de mayor a menor: el corte mayor es el primero que falta.
        val porLargo = tipos.indices.sortedByDescending { tipos[it].largo }
        val barras = mutableListOf<Barra>()
        while (true) {
            val pendientes = porLargo.filter { faltan[it] > 0 }
            if (pendientes.isEmpty()) break
            val obligado = if (azar != null && pendientes.size > 1 && azar.nextFloat() < 0.3f)
                pendientes[azar.nextInt(minOf(3, pendientes.size))] else pendientes.first()
            // Las varillas que lo admiten; de cada largo se llena la mejor combinación y gana la de
            // menos retazo (a igual retazo, la menor: la varilla menor de la Fórmula 1).
            var elegido: Pair<Int, IntArray>? = null
            var retazoElegido = Int.MAX_VALUE
            existencias.indices.sortedBy { existencias[it].largo }.forEach { v ->
                if (quedan[v] <= 0 || peso[obligado] > capacidad[v]) return@forEach
                val usados = llenar(obligado, faltan, peso, capacidad[v], azar)
                val lleno = tipos.indices.sumOf { usados[it] * peso[it] }
                val retazo = capacidad[v] - lleno
                if (retazo < retazoElegido) {
                    retazoElegido = retazo
                    elegido = v to usados
                }
            }
            val hecho = elegido
            if (hecho == null) {
                // No queda varilla donde quepa: ese corte no se puede hacer.
                faltan[obligado] = 0
                continue
            }
            val (v, usados) = hecho
            quedan[v]--
            val cortes = mutableListOf<Pair<Float, String>>()
            porLargo.forEach { t -> repeat(usados[t]) { cortes.add(tipos[t].largo to tipos[t].referencia) } }
            usados.forEachIndexed { t, n -> faltan[t] -= n }
            val largo = existencias[v].largo
            val gastado = cortes.sumOf { it.first.toDouble() }.toFloat() + (cortes.size - 1).coerceAtLeast(0) * disco
            barras.add(Barra(largo, cortes, (largo - gastado).coerceAtLeast(0f)))
        }
        val sinCortar = tipos.indices.sumOf { t -> tipos[t].cantidad } - barras.sumOf { it.cortes.size }
        return Solucion(barras, sinCortar)
    }

    /**
     * La mejor combinación para una varilla de [capacidad] con el corte [obligado] dentro: la que
     * más la llena sin pasarse. Mochila acotada: cada tipo partido en paquetes de 1, 2, 4… para que
     * cada paquete se use una vez, y en cada suma alcanzada se recuerda con qué paquete se llegó.
     * Devuelve cuántas piezas de cada tipo.
     */
    private fun llenar(obligado: Int, faltan: IntArray, peso: IntArray, capacidad: Int, azar: Random?): IntArray {
        val libre = capacidad - peso[obligado]
        val paqueteTipo = ArrayList<Int>()
        val paqueteN = ArrayList<Int>()
        // De mayor a menor: a igual llenado se prefieren los cortes grandes y los chicos quedan para
        // completar otras varillas.
        val orden = faltan.indices.sortedByDescending { peso[it] }.toMutableList()
        if (azar != null) {
            // Desempate distinto: se barajan los tipos de a pares vecinos, sin perder el orden general.
            for (i in 0 until orden.size - 1) if (azar.nextFloat() < 0.35f) {
                val t = orden[i]; orden[i] = orden[i + 1]; orden[i + 1] = t
            }
        }
        for (t in orden) {
            var n = faltan[t] - (if (t == obligado) 1 else 0)
            if (peso[t] <= 0) continue
            n = minOf(n, libre / peso[t])
            var k = 1
            while (n > 0) {
                val tomar = minOf(k, n)
                paqueteTipo.add(t); paqueteN.add(tomar)
                n -= tomar
                k *= 2
            }
        }
        val alcanzado = BooleanArray(libre + 1)
        val desde = IntArray(libre + 1) { -1 }
        alcanzado[0] = true
        var mejor = 0
        for (p in paqueteTipo.indices) {
            val w = peso[paqueteTipo[p]] * paqueteN[p]
            if (w > libre) continue
            for (s in libre downTo w) {
                if (!alcanzado[s] && alcanzado[s - w]) {
                    alcanzado[s] = true
                    desde[s] = p
                    if (s > mejor) mejor = s
                }
            }
            if (mejor == libre) break
        }
        val usados = IntArray(faltan.size)
        usados[obligado] = 1
        var s = mejor
        while (s > 0) {
            val p = desde[s]
            usados[paqueteTipo[p]] += paqueteN[p]
            s -= peso[paqueteTipo[p]] * paqueteN[p]
        }
        return usados
    }
}
