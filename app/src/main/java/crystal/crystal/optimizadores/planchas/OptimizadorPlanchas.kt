package crystal.crystal.optimizadores.planchas

import android.util.Log
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

// ── Tipos públicos ─────────────────────────────────────────────────────────────

data class PiezaPlancha(
    val id: String,
    val descripcion: String,
    val anchoMm: Int,
    val altoMm: Int,
    val cantidad: Int,
    val material: String = "General",
    val rotacionPermitida: Boolean = true
)

/**
 * Una entrada del INVENTARIO de material del que se puede cortar: plancha entera o retazo, es lo
 * mismo. [cantidad] son las unidades que hay de esa medida.
 *
 * El optimizador solo abre unidades que existen en el inventario y ninguna entrada tiene privilegio
 * por su posición en la lista: el orden en que se consume lo decide el costo (primero el material
 * sobrante, luego el más chico que sirva), no la fila en que se escribió.
 *
 * [esRetazo] marca material ya pagado (recorte sobrante). No limita nada: solo abarata su consumo
 * frente a una plancha entera y etiqueta el resultado en el dibujo y el PDF.
 */
data class PlanchaStock(
    val nombre: String,
    val anchoMm: Int,
    val altoMm: Int,
    val cantidad: Int,
    val material: String = "General",
    val esRetazo: Boolean = false
)

data class CorteUbicadoPlancha(
    val piezaId: String,
    val descripcion: String,
    val material: String,
    val xMm: Int,
    val yMm: Int,
    val anchoMm: Int,
    val altoMm: Int,
    val rotada: Boolean
)

data class PlanchaOptimizada(
    val indice: Int,
    val nombre: String,
    val material: String,
    val anchoMm: Int,
    val altoMm: Int,
    val esRetazoEntrada: Boolean,
    val cortes: List<CorteUbicadoPlancha>,
    val areaUsadaMm2: Long,
    val areaDesperdicioMm2: Long,
    val cortada: Boolean = false
)

data class ResultadoOptimizacionPlanchas(
    val planchas: List<PlanchaOptimizada>,
    val piezasSinUbicar: List<PiezaPlancha>,
    val areaUsadaMm2: Long,
    val areaDesperdicioMm2: Long
)

enum class ObjetivoOptimizacionPlanchas { MENOS_PLANCHAS, MENOS_DESPERDICIO }
enum class IntensidadOptimizacionPlanchas { RAPIDO, NORMAL, PROFUNDO }

// ── Tipos internos ─────────────────────────────────────────────────────────────

private data class PiezaExp(
    val id: String,
    val descripcion: String,
    val anchoMm: Int,
    val altoMm: Int,
    val material: String,
    val rotacionPermitida: Boolean
)

private data class FreeRect(val x: Int, val y: Int, val w: Int, val h: Int)

private data class Bin(
    val anchoMm: Int,
    val altoMm: Int,
    val esRetazo: Boolean,
    val nombre: String,
    val material: String,
    /** Entrada del inventario de la que salió esta unidad, para contar el stock consumido. */
    val stockIdx: Int,
    val libres: MutableList<FreeRect>,
    val cortes: MutableList<CorteUbicadoPlancha>
)

private data class Placement(
    val binIdx: Int,
    val rectIdx: Int,
    val pw: Int,
    val ph: Int,
    val rotada: Boolean,
    val score: Long
)

/** Un layout completo: material abierto, lo que no entró y su puntaje. */
private class Solucion(
    val bins: List<Bin>,
    val sinUbicar: List<PiezaExp>,
    val score: ScorePlanchas
)

/**
 * Puntaje comparable de un layout, en orden lexicográfico de prioridades:
 *
 * 1. **Cortes logrados.** Menos piezas sin ubicar manda sobre todo lo demás. Con esto, agregar
 *    material al inventario nunca puede empeorar la cantidad de cortes que se consiguen.
 * 2. **Material gastado.** Primero menos planchas ENTERAS abiertas (un retazo es material sobrante,
 *    ya pagado), luego menos área total abierta y por último menos unidades.
 * 3. **Calidad del sobrante.** Un solo rectángulo libre grande y reutilizable, poco fragmentado.
 *
 * Cuando el inventario alcanza para todo, el primer criterio se anula solo (0 faltantes en todos los
 * candidatos) y el puntaje se reduce al de siempre: mínimo de planchas con el sobrante consolidado.
 * Cuando el inventario NO alcanza, pasa a mandar el máximo de cortes. No hace falta detectar el
 * régimen: sale del propio orden de prioridades.
 */
private class ScorePlanchas(
    val objetivo: ObjetivoOptimizacionPlanchas,
    val faltantes: Int,
    val areaFaltanteMm2: Long,
    val enteras: Int,
    val areaConsumidaMm2: Long,
    val unidades: Int,
    val mayorLibreMm2: Long,
    val fragmentos: Int
) : Comparable<ScorePlanchas> {
    override fun compareTo(other: ScorePlanchas): Int {
        if (faltantes != other.faltantes) return faltantes.compareTo(other.faltantes)
        if (areaFaltanteMm2 != other.areaFaltanteMm2) return areaFaltanteMm2.compareTo(other.areaFaltanteMm2)
        if (objetivo == ObjetivoOptimizacionPlanchas.MENOS_PLANCHAS) {
            if (enteras != other.enteras) return enteras.compareTo(other.enteras)
            if (areaConsumidaMm2 != other.areaConsumidaMm2) return areaConsumidaMm2.compareTo(other.areaConsumidaMm2)
        } else {
            if (areaConsumidaMm2 != other.areaConsumidaMm2) return areaConsumidaMm2.compareTo(other.areaConsumidaMm2)
            if (enteras != other.enteras) return enteras.compareTo(other.enteras)
        }
        if (unidades != other.unidades) return unidades.compareTo(other.unidades)
        // Sobrante en un solo bloque grande: más es mejor, por eso se compara al revés.
        if (mayorLibreMm2 != other.mayorLibreMm2) return other.mayorLibreMm2.compareTo(mayorLibreMm2)
        return fragmentos.compareTo(other.fragmentos)
    }
}

private enum class SortMode {
    AREA_DESC, AREA_ASC,
    PERI_DESC, PERI_ASC,
    LSIDE_DESC, SSIDE_DESC,
    DIFF_DESC, RATIO_DESC,
    HEIGHT_DESC, WIDTH_DESC
}

private enum class Heuristica {
    GUILLOTINE_BAF_SAS, GUILLOTINE_BAF_LAS,
    GUILLOTINE_BSSF_SAS, GUILLOTINE_BSSF_LAS,
    GUILLOTINE_BLSF_SAS, GUILLOTINE_BLSF_LAS
}

// ── Optimizador principal ──────────────────────────────────────────────────────

object OptimizadorPlanchas {

    /**
     * Reparte [piezas] en el material de [stock]. Solo se abren unidades que existen en el
     * inventario; lo que no entra se devuelve en `piezasSinUbicar`.
     */
    fun optimizar(
        piezas: List<PiezaPlancha>,
        stock: List<PlanchaStock>,
        objetivo: ObjetivoOptimizacionPlanchas = ObjetivoOptimizacionPlanchas.MENOS_PLANCHAS,
        intensidad: IntensidadOptimizacionPlanchas = IntensidadOptimizacionPlanchas.NORMAL,
        separacionCorteMm: Int = 0,
        margenPerimetralMm: Int = 0
    ): ResultadoOptimizacionPlanchas {
        val expandidas = piezas.flatMap { p ->
            List(max(0, p.cantidad)) { i ->
                PiezaExp(
                    "${p.id}-${i + 1}", p.descripcion, p.anchoMm, p.altoMm,
                    p.material.ifBlank { "General" }, p.rotacionPermitida
                )
            }
        }

        val inventario = stock.filter { it.anchoMm > 0 && it.altoMm > 0 && it.cantidad > 0 }
        if (inventario.isEmpty()) {
            return ResultadoOptimizacionPlanchas(emptyList(), expandidas.map { aPiezaPlancha(it) }, 0, 0)
        }

        val materiales = expandidas.groupBy { it.material.trim().ifBlank { "General" } }
        var indiceGlobal = 1
        val hojasFinales = mutableListOf<PlanchaOptimizada>()
        val sinUbicarFinal = mutableListOf<PiezaPlancha>()

        materiales.toSortedMap(String.CASE_INSENSITIVE_ORDER).forEach { (mat, grupo) ->
            val stockMat = inventario.filter {
                it.material.trim().ifBlank { "General" }.equals(mat, ignoreCase = true)
            }
            val (bins, sinUbicar) = resolverGrupo(
                grupo, mat, stockMat, objetivo, intensidad, separacionCorteMm, margenPerimetralMm
            )
            bins.forEach { b ->
                val usado = b.cortes.sumOf { it.anchoMm.toLong() * it.altoMm }
                val total = b.anchoMm.toLong() * b.altoMm
                hojasFinales += PlanchaOptimizada(
                    indice = indiceGlobal++,
                    nombre = b.nombre,
                    material = mat,
                    anchoMm = b.anchoMm,
                    altoMm = b.altoMm,
                    esRetazoEntrada = b.esRetazo,
                    cortes = b.cortes.toList(),
                    areaUsadaMm2 = usado,
                    areaDesperdicioMm2 = max(0L, total - usado)
                )
            }
            sinUbicarFinal += sinUbicar.map { aPiezaPlancha(it) }
        }

        return ResultadoOptimizacionPlanchas(
            hojasFinales, sinUbicarFinal,
            hojasFinales.sumOf { it.areaUsadaMm2 },
            hojasFinales.sumOf { it.areaDesperdicioMm2 }
        )
    }

    private fun aPiezaPlancha(p: PiezaExp) =
        PiezaPlancha(p.id, p.descripcion, p.anchoMm, p.altoMm, 1, p.material, p.rotacionPermitida)

    private fun resolverGrupo(
        piezas: List<PiezaExp>, material: String,
        stock: List<PlanchaStock>,
        objetivo: ObjetivoOptimizacionPlanchas,
        intensidad: IntensidadOptimizacionPlanchas,
        sep: Int, margen: Int
    ): Pair<List<Bin>, List<PiezaExp>> {

        if (stock.isEmpty()) return emptyList<Bin>() to piezas

        val inicio = System.currentTimeMillis()

        val heuristicas: List<Heuristica> = when (intensidad) {
            IntensidadOptimizacionPlanchas.RAPIDO -> listOf(
                Heuristica.GUILLOTINE_BAF_SAS,
                Heuristica.GUILLOTINE_BAF_LAS,
                Heuristica.GUILLOTINE_BSSF_SAS
            )
            IntensidadOptimizacionPlanchas.NORMAL,
            IntensidadOptimizacionPlanchas.PROFUNDO -> Heuristica.values().toList()
        }

        val sorts: List<SortMode> = when (intensidad) {
            IntensidadOptimizacionPlanchas.RAPIDO -> listOf(
                SortMode.AREA_DESC, SortMode.LSIDE_DESC, SortMode.PERI_DESC
            )
            IntensidadOptimizacionPlanchas.NORMAL,
            IntensidadOptimizacionPlanchas.PROFUNDO -> SortMode.values().toList()
        }

        val rotaciones: List<Boolean> = when (intensidad) {
            IntensidadOptimizacionPlanchas.RAPIDO -> listOf(true)
            else -> listOf(true, false)
        }

        // Semillas aleatorias: en PROFUNDO se prueban hasta 1000 órdenes
        // distintos para tener la mejor probabilidad de encontrar el mínimo.
        val randSeeds: List<Long> = when (intensidad) {
            IntensidadOptimizacionPlanchas.RAPIDO -> emptyList()
            IntensidadOptimizacionPlanchas.NORMAL -> (1L..120L).toList()
            IntensidadOptimizacionPlanchas.PROFUNDO -> (1L..1000L).toList()
        }

        // Tiempo máximo. PROFUNDO sube a 60s para permitir el barrido amplio
        // y el paso adicional de relleno.
        val tiempoMaxMs = when (intensidad) {
            IntensidadOptimizacionPlanchas.RAPIDO -> 2_000L
            IntensidadOptimizacionPlanchas.NORMAL -> 10_000L
            IntensidadOptimizacionPlanchas.PROFUNDO -> 60_000L
        }

        var mejorSol: Solucion? = null
        var mejorDesc = ""
        var combinaciones = 0

        // Acepta un layout solo si mejora el puntaje Y es cortable con guillotina. Se puntúa antes de
        // validar porque el puntaje es barato y descarta la mayoría de los candidatos.
        fun considerar(bins: List<Bin>, sin: List<PiezaExp>, desc: String, chequearSolapes: Boolean) {
            val sc = calcularScore(bins, sin, objetivo)
            val actual = mejorSol
            if (actual != null && sc >= actual.score) return
            if (chequearSolapes && !esResultadoSinSolapes(bins)) return
            if (!esResultadoCortable(bins)) return
            mejorSol = Solucion(bins, sin, sc)
            mejorDesc = desc
        }

        fun probar(ordenadas: List<PiezaExp>, rotacion: Boolean, heur: Heuristica, desc: String) {
            if (System.currentTimeMillis() - inicio > tiempoMaxMs) return
            combinaciones++
            val (bins, sin) = empaquetar(ordenadas, material, stock, margen, sep, rotacion, heur)
            considerar(bins, sin, desc, false)
        }

        for (heur in heuristicas) {
            for (sort in sorts) {
                val ordenadas = ordenar(piezas, sort)
                for (rotacion in rotaciones) {
                    probar(ordenadas, rotacion, heur, "$heur+$sort+rot=$rotacion")
                }
            }
            for (seed in randSeeds) {
                val rng = Random(seed)
                val shuffled = piezas.shuffled(rng)
                for (rotacion in rotaciones) {
                    probar(shuffled, rotacion, heur, "$heur+rand$seed+rot=$rotacion")
                }
            }
        }

        // ── Motor de SPLIT MIXTO (dirección por retazo) — el más potente ──────
        // Cada retazo elige su dirección de corte (H o V) de forma independiente, generando patrones
        // guillotina de dirección MIXTA que el split fijo no alcanza. Es lo que realmente llega al
        // óptimo guillotina (en el caso real bajó de 5 a 4 planchas). Siempre cortable por
        // construcción; se valida solo al mejorar (barato) para no frenar el barrido.
        val intentosMixto = when (intensidad) {
            IntensidadOptimizacionPlanchas.RAPIDO -> 6_000
            IntensidadOptimizacionPlanchas.NORMAL -> 250_000
            IntensidadOptimizacionPlanchas.PROFUNDO -> 1_000_000
        }
        val heurArr = Heuristica.values()
        val sortArr = SortMode.values()
        for (a in 0 until intentosMixto) {
            if (a and 0x3FF == 0 && System.currentTimeMillis() - inicio > tiempoMaxMs) break
            combinaciones++
            val rng = Random(a.toLong() * 2654435761L + 12345L)
            // La mitad de los intentos usan un orden AGRUPADO (por dimensión): así piezas similares
            // se colocan juntas y tienden a formar bandas limpias con retazo aprovechable.
            val orden = if (a % 2 == 0) ordenar(piezas, sortArr[a % sortArr.size]) else piezas.shuffled(rng)
            val heur = heurArr[rng.nextInt(heurArr.size)]
            val (bins, sin) = empaquetar(orden, material, stock, margen, sep, true, heur, rng)
            considerar(bins, sin, "MIXTO#$a", true)
        }

        // ── Motor MAXRECTS (bonus) ────────────────────────────────────────────
        // Acomoda denso; se acepta solo si el layout es cortable con guillotina y sin solapes.
        // Blindado por el score: nunca empeora. Barrido pequeño (el split mixto es el principal).
        fun probarMR(ordenadas: List<PiezaExp>, rot: Boolean, desc: String) {
            if (System.currentTimeMillis() - inicio > tiempoMaxMs) return
            combinaciones++
            val (bins, sin) = empaquetarMaxRects(ordenadas, material, stock, margen, sep, rot)
            considerar(bins, sin, "MAXRECTS+$desc", true)
        }
        for (sort in sorts) {
            val ord = ordenar(piezas, sort)
            for (rot in rotaciones) probarMR(ord, rot, "$sort+rot=$rot")
        }

        // ── Motor de BANDAS (shelf): agrupa por altura ────────────────────────
        // Construye bandas de piezas similares (largas juntas, etc.). Aporta el layout AGRUPADO
        // que la métrica de retazo limpio prefiere. Gateado igual: cortable + sin solapes + score.
        for (rot in rotaciones) {
            if (System.currentTimeMillis() - inicio > tiempoMaxMs) break
            combinaciones++
            val (bins, sin) = empaquetarShelf(piezas, material, stock, margen, sep, rot)
            considerar(bins, sin, "SHELF+rot=$rot", true)
        }

        // ── PASO DE RELLENO POST-EMPAQUETADO ──────────────────────────────────
        // Solo en NORMAL y PROFUNDO. Para cada plancha bajo el umbral de
        // aprovechamiento, intenta mover piezas de planchas posteriores que
        // quepan en sus huecos. Si una plancha posterior queda vacía,
        // desaparece → ahorro real.
        //
        // VALIDACIÓN CRÍTICA: el resultado post-relleno se acepta SOLO si
        // (a) sigue siendo cortable con guillotina y
        // (b) mejora el puntaje (más cortes, o menos material para los mismos).
        // Si no se cumplen ambas, se descarta el relleno y se conserva el
        // resultado original.
        if (mejorSol != null && intensidad != IntensidadOptimizacionPlanchas.RAPIDO) {
            val umbral = if (intensidad == IntensidadOptimizacionPlanchas.PROFUNDO) 0.90f else 0.85f
            // Iterar: cada pasada que logre consolidar se acepta y se vuelve a intentar sobre el
            // nuevo layout, encadenando reducciones. Se corta al dejar de mejorar o al agotar el
            // presupuesto de tiempo de la fase.
            val rellenoDeadline = System.currentTimeMillis() + when (intensidad) {
                IntensidadOptimizacionPlanchas.PROFUNDO -> 30_000L
                else -> 8_000L
            }
            var iter = 0
            while (iter < 6 && System.currentTimeMillis() < rellenoDeadline) {
                iter++
                val base = mejorSol ?: break
                val candidato = rellenarPlanchasBajas(
                    base, piezas, stock, material, umbral, true, objetivo, sep, margen, rellenoDeadline
                ) ?: break
                if (candidato.score < base.score && esResultadoCortable(candidato.bins)) {
                    Log.d("OptimizadorPlanchas",
                        "Consolidación $iter: ${base.bins.size} → ${candidato.bins.size} unidades, " +
                                "faltantes ${base.sinUbicar.size} → ${candidato.sinUbicar.size}")
                    mejorSol = candidato
                } else break
            }
        }

        // ── PASO DE COMPLETADO ────────────────────────────────────────────────
        // Red de seguridad para el caso de material escaso: si quedaron piezas sin ubicar, se
        // intenta meterlas una por una en los huecos que aún quedan libres y en las unidades de
        // inventario todavía sin abrir. Solo puede reducir los faltantes, nunca aumentarlos, y por
        // eso es lo que garantiza en la práctica que agregar material no empeore el resultado.
        var ronda = 0
        while (ronda < 3) {
            val base = mejorSol ?: break
            if (base.sinUbicar.isEmpty()) break
            ronda++
            for (heur in heurArr) {
                for (rot in rotaciones) {
                    val (bins, sin) = completarFaltantes(
                        base.bins, base.sinUbicar, stock, material, margen, sep, rot, heur
                    )
                    considerar(bins, sin, "COMPLETAR+$heur+rot=$rot", false)
                }
            }
            if (mejorSol === base) break
        }

        val tiempo = System.currentTimeMillis() - inicio
        val sol = mejorSol
        Log.d("OptimizadorPlanchas",
            "[$material] $combinaciones combinaciones en ${tiempo}ms: " +
                    "${sol?.bins?.count { it.cortes.isNotEmpty() } ?: 0} unidades, " +
                    "${sol?.sinUbicar?.size ?: piezas.size} faltantes — $mejorDesc"
        )

        if (sol == null) return emptyList<Bin>() to piezas
        return sol.bins.filter { it.cortes.isNotEmpty() } to sol.sinUbicar
    }

    // ── PASO DE RELLENO ───────────────────────────────────────────────────────
    //
    // Estrategia: identifica las planchas con bajo aprovechamiento, "abre" las
    // siguientes planchas (las que están debajo de ellas en el orden), y re-empaca
    // todas esas piezas desde cero usando el algoritmo base — más las piezas que
    // habían quedado sin ubicar, que también entran al re-empaquetado.
    //
    // Esto evita los bugs de mover piezas una por una con manejo manual de rotaciones.
    // Reusa el código de empaquetado base (que ya está probado) y garantiza que el
    // resultado sea cortable con guillotina (mismo split SAS/LAS, misma fusión de
    // libres, mismo validador).
    //
    // Devuelve null si no encontró nada mejor que [base].

    private fun rellenarPlanchasBajas(
        base: Solucion,
        piezas: List<PiezaExp>,
        stock: List<PlanchaStock>,
        material: String,
        umbral: Float,
        rotacion: Boolean,
        objetivo: ObjetivoOptimizacionPlanchas,
        sep: Int, margen: Int,
        deadlineMs: Long
    ): Solucion? {
        val binsOriginales = base.bins.filter { it.cortes.isNotEmpty() }
        if (binsOriginales.size <= 1) return null

        // Encontrar la PRIMERA plancha con aprovechamiento bajo
        val primeraBaja = binsOriginales.indexOfFirst { b ->
            val area = b.anchoMm.toLong() * b.altoMm
            val usado = b.cortes.sumOf { it.anchoMm.toLong() * it.altoMm }
            area > 0 && usado.toFloat() / area < umbral
        }
        if (primeraBaja < 0 || primeraBaja >= binsOriginales.size - 1) return null

        var mejor: Solucion? = null
        var mejorScore = base.score

        // Búsqueda amplia (todos los ordenamientos + semillas y todas las heurísticas). El re-
        // empaquetado respeta el kerf del disco (sep) y el margen, igual que el empaquetado base,
        // para que un layout consolidado sea realmente cortable con la separación real.
        val sortsPrueba = SortMode.values().toList()
        val heurPrueba = Heuristica.values().toList()

        // Se prueban puntos de corte enfocados: conservar (primeraBaja-1) o primeraBaja planchas
        // iniciales y re-empaquetar el resto. Conservar una menos da libertad para mover piezas de
        // una plancha llena y así eliminar otra. Todo sigue blindado por el score y la cortabilidad.
        val puntosCorte = listOf((primeraBaja - 1).coerceAtLeast(0), primeraBaja).distinct()
        for (conservarN in puntosCorte) {
            if (System.currentTimeMillis() >= deadlineMs) break
            val conservadas = binsOriginales.subList(0, conservarN).map { copiarBin(it) }

            // El stock que queda tras las unidades que se conservan (se mantienen los índices para
            // que el conteo de unidades siga siendo válido más adelante).
            val consumidas = IntArray(stock.size)
            conservadas.forEach { if (it.stockIdx in stock.indices) consumidas[it.stockIdx]++ }
            val stockRestante = stock.mapIndexed { i, e ->
                e.copy(cantidad = (e.cantidad - consumidas[i]).coerceAtLeast(0))
            }

            // Cada pieza vuelve tal como entró, con su permiso de rotación: antes se rearmaba con la
            // rotación permitida a la fuerza y en este paso el bloqueo de rotación no servía.
            val porId = piezas.associateBy { it.id }
            val piezasARecolocar = mutableListOf<PiezaExp>()
            for (i in conservarN until binsOriginales.size) {
                for (c in binsOriginales[i].cortes) {
                    val anchoOrig = if (c.rotada) c.altoMm else c.anchoMm
                    val altoOrig  = if (c.rotada) c.anchoMm else c.altoMm
                    piezasARecolocar.add(porId[c.piezaId] ?: PiezaExp(
                        c.piezaId, c.descripcion, anchoOrig, altoOrig, c.material, true
                    ))
                }
            }
            // Las que no habían entrado vuelven a competir por un lugar.
            piezasARecolocar.addAll(base.sinUbicar)

            fun registrar(bins: List<Bin>, sin: List<PiezaExp>, chequearSolapes: Boolean) {
                val candidato = conservadas + bins
                val s = calcularScore(candidato, sin, objetivo)
                if (s >= mejorScore) return
                if (chequearSolapes && !esResultadoSinSolapes(candidato)) return
                if (!esResultadoCortable(candidato)) return
                mejorScore = s
                mejor = Solucion(candidato, sin, s)
            }

            fun evaluar(ordenadas: List<PiezaExp>) {
                if (System.currentTimeMillis() >= deadlineMs) return
                for (heur in heurPrueba) {
                    val (rec, sin) = empaquetar(
                        ordenadas, material, stockRestante, margen, sep, rotacion, heur
                    )
                    registrar(rec, sin, false)
                }
                // MAXRECTS en la consolidación (mismo blindaje: cortable + sin solapes).
                for (rot in listOf(true, false)) {
                    val (rec, sin) = empaquetarMaxRects(
                        ordenadas, material, stockRestante, margen, sep, rot
                    )
                    registrar(rec, sin, true)
                }
            }

            for (sort in sortsPrueba) evaluar(ordenar(piezasARecolocar, sort))
            for (seed in 1L..30L) evaluar(piezasARecolocar.shuffled(Random(seed)))

            // Split MIXTO en la consolidación (dirección por retazo): el motor más fuerte, también
            // aquí. Cortable por construcción; se valida solo al mejorar.
            val heurArr = Heuristica.values()
            val sortArr = SortMode.values()
            var a = 0
            while (a < 20_000 && System.currentTimeMillis() < deadlineMs) {
                val rng = Random(a.toLong() * 40503L + 7L)
                val orden = if (a % 4 == 0) ordenar(piezasARecolocar, sortArr[a % sortArr.size])
                            else piezasARecolocar.shuffled(rng)
                val heur = heurArr[rng.nextInt(heurArr.size)]
                val (rec, sin) = empaquetar(
                    orden, material, stockRestante, margen, sep, rotacion, heur, rng
                )
                registrar(rec, sin, true)
                a++
            }
        }

        return mejor
    }

    // ── PASO DE COMPLETADO ────────────────────────────────────────────────────
    // Intenta ubicar las piezas que quedaron fuera sin tocar lo ya colocado: primero en los huecos
    // libres de las unidades abiertas, después abriendo unidades de inventario que sigan sin usar.
    // Es monótono por construcción: solo puede bajar la cantidad de faltantes.
    private fun completarFaltantes(
        bins: List<Bin>, sinUbicar: List<PiezaExp>,
        stock: List<PlanchaStock>, material: String,
        margen: Int, sep: Int, rotacion: Boolean, heuristica: Heuristica
    ): Pair<List<Bin>, List<PiezaExp>> {
        if (sinUbicar.isEmpty()) return bins to sinUbicar

        val copia = bins.filter { it.cortes.isNotEmpty() }.map { copiarBin(it) }.toMutableList()
        val usados = IntArray(stock.size)
        copia.forEach { if (it.stockIdx in stock.indices) usados[it.stockIdx]++ }

        val restantes = mutableListOf<PiezaExp>()
        // De mayor a menor: las grandes son las difíciles de encajar y merecen el primer intento.
        for (pieza in sinUbicar.sortedByDescending { it.anchoMm.toLong() * it.altoMm }) {
            val pw0 = pieza.anchoMm + sep
            val ph0 = pieza.altoMm + sep
            val hueco = encontrarMejorPlacement(pieza, pw0, ph0, copia, rotacion, heuristica)
            if (hueco != null) {
                colocarEnBin(pieza, copia[hueco.binIdx], hueco.rectIdx,
                    hueco.pw, hueco.ph, hueco.rotada, heuristica, null)
                continue
            }
            val nuevo = abrirUnidad(stock, usados, material, pieza, pw0, ph0, margen, rotacion, null)
            if (nuevo != null) {
                val fit = encontrarMejorPlacementEnRect(pieza, pw0, ph0, 0, nuevo.libres[0], rotacion, heuristica)
                if (fit != null) {
                    colocarEnBin(pieza, nuevo, 0, fit.pw, fit.ph, fit.rotada, heuristica, null)
                    copia.add(nuevo)
                    continue
                }
            }
            restantes += pieza
        }
        return copia to restantes
    }

    private fun copiarBin(b: Bin): Bin = Bin(
        b.anchoMm, b.altoMm, b.esRetazo, b.nombre, b.material, b.stockIdx,
        b.libres.toMutableList(), b.cortes.toMutableList()
    )

    // ── Validador de cortabilidad guillotina ──────────────────────────────────

    private fun esResultadoCortable(bins: List<Bin>): Boolean =
        bins.all { bin -> esBinCortable(bin.cortes, 0, 0, bin.anchoMm, bin.altoMm) }

    private fun esBinCortable(
        cortes: List<CorteUbicadoPlancha>,
        x0: Int, y0: Int, w: Int, h: Int
    ): Boolean {
        if (cortes.size <= 1) return true
        val xCandidatos = sortedSetOf<Int>()
        val yCandidatos = sortedSetOf<Int>()
        for (c in cortes) {
            if (c.xMm > x0) xCandidatos.add(c.xMm)
            if (c.xMm + c.anchoMm < x0 + w) xCandidatos.add(c.xMm + c.anchoMm)
            if (c.yMm > y0) yCandidatos.add(c.yMm)
            if (c.yMm + c.altoMm < y0 + h) yCandidatos.add(c.yMm + c.altoMm)
        }
        for (xCut in xCandidatos) {
            val cruza = cortes.any { c -> c.xMm < xCut && c.xMm + c.anchoMm > xCut }
            if (!cruza) {
                val izq = cortes.filter { it.xMm + it.anchoMm <= xCut }
                val der = cortes.filter { it.xMm >= xCut }
                if (izq.size + der.size == cortes.size && izq.isNotEmpty() && der.isNotEmpty()) {
                    if (esBinCortable(izq, x0, y0, xCut - x0, h) &&
                        esBinCortable(der, xCut, y0, x0 + w - xCut, h)) return true
                }
            }
        }
        for (yCut in yCandidatos) {
            val cruza = cortes.any { c -> c.yMm < yCut && c.yMm + c.altoMm > yCut }
            if (!cruza) {
                val abajo = cortes.filter { it.yMm + it.altoMm <= yCut }
                val arriba = cortes.filter { it.yMm >= yCut }
                if (abajo.size + arriba.size == cortes.size && abajo.isNotEmpty() && arriba.isNotEmpty()) {
                    if (esBinCortable(abajo, x0, y0, w, yCut - y0) &&
                        esBinCortable(arriba, x0, yCut, w, y0 + h - yCut)) return true
                }
            }
        }
        return false
    }

    // ── Inventario: apertura de unidades ──────────────────────────────────────

    /** ¿Cabe la pieza en una unidad virgen de esta entrada? Mismo criterio que el placement. */
    private fun cabeEnEntrada(
        e: PlanchaStock, pieza: PiezaExp, pw0: Int, ph0: Int, margen: Int, rotacion: Boolean
    ): Boolean {
        val w = e.anchoMm - 2 * margen
        val h = e.altoMm - 2 * margen
        if (w <= 0 || h <= 0) return false
        if (pw0 <= w && ph0 <= h) return true
        return rotacion && pieza.rotacionPermitida && pieza.anchoMm != pieza.altoMm &&
                ph0 <= w && pw0 <= h
    }

    /**
     * Elige qué entrada del inventario abrir. El orden de la lista NO influye: primero el material
     * sobrante (retazo, ya pagado), después el ajuste más justo (menos área desperdiciada de entrada)
     * y por último el índice, solo para que la elección sea determinista.
     *
     * Con [rng] (motor de split mixto) una parte de los intentos elige al azar entre las entradas
     * que sirven, para que la búsqueda no quede atada a esta preferencia.
     */
    private fun elegirEntrada(
        stock: List<PlanchaStock>, usados: IntArray, areaPiezaMm2: Long, rng: Random?,
        cabe: (PlanchaStock) -> Boolean
    ): Int {
        val candidatos = stock.indices.filter { i -> usados[i] < stock[i].cantidad && cabe(stock[i]) }
        if (candidatos.isEmpty()) return -1
        if (rng != null && candidatos.size > 1 && rng.nextInt(4) == 0) {
            return candidatos[rng.nextInt(candidatos.size)]
        }
        return candidatos.minWithOrNull(
            compareBy(
                { !stock[it].esRetazo },
                { stock[it].anchoMm.toLong() * stock[it].altoMm - areaPiezaMm2 },
                { it }
            )
        ) ?: -1
    }

    private fun nuevoBinDe(e: PlanchaStock, idx: Int, material: String, margen: Int) = Bin(
        e.anchoMm, e.altoMm, e.esRetazo,
        e.nombre.ifBlank { if (e.esRetazo) "Retazo" else "Plancha" }, material, idx,
        mutableListOf(FreeRect(margen, margen, e.anchoMm - 2 * margen, e.altoMm - 2 * margen)),
        mutableListOf()
    )

    /** Abre una unidad del inventario donde quepa [pieza], o null si ya no queda material que sirva. */
    private fun abrirUnidad(
        stock: List<PlanchaStock>, usados: IntArray, material: String,
        pieza: PiezaExp, pw0: Int, ph0: Int, margen: Int, rotacion: Boolean, rng: Random?
    ): Bin? {
        val idx = elegirEntrada(
            stock, usados, pieza.anchoMm.toLong() * pieza.altoMm, rng
        ) { e -> cabeEnEntrada(e, pieza, pw0, ph0, margen, rotacion) }
        if (idx < 0) return null
        usados[idx]++
        return nuevoBinDe(stock[idx], idx, material, margen)
    }

    // ── Empaquetado ───────────────────────────────────────────────────────────

    private fun empaquetar(
        piezas: List<PiezaExp>,
        material: String,
        stock: List<PlanchaStock>,
        margen: Int, sep: Int,
        rotacion: Boolean,
        heuristica: Heuristica,
        rng: Random? = null
    ): Pair<List<Bin>, List<PiezaExp>> {
        val bins = mutableListOf<Bin>()
        val usados = IntArray(stock.size)
        val sinUbicar = mutableListOf<PiezaExp>()
        for (pieza in piezas) {
            val pw0 = pieza.anchoMm + sep
            val ph0 = pieza.altoMm + sep
            val mejor = encontrarMejorPlacement(pieza, pw0, ph0, bins, rotacion, heuristica)
            if (mejor != null) {
                colocarEnBin(pieza, bins[mejor.binIdx], mejor.rectIdx,
                    mejor.pw, mejor.ph, mejor.rotada, heuristica, rng)
                continue
            }
            // No entra en nada abierto: se abre una unidad nueva del inventario. Si no queda
            // material donde quepa, la pieza no se corta (antes se descartaba el intento completo).
            val nuevoBin = abrirUnidad(stock, usados, material, pieza, pw0, ph0, margen, rotacion, rng)
            if (nuevoBin == null) {
                sinUbicar += pieza
                continue
            }
            val fit = encontrarMejorPlacementEnRect(
                pieza, pw0, ph0, 0, nuevoBin.libres[0], rotacion, heuristica
            )
            if (fit == null) {
                sinUbicar += pieza
                continue
            }
            colocarEnBin(pieza, nuevoBin, 0, fit.pw, fit.ph, fit.rotada, heuristica, rng)
            bins.add(nuevoBin)
        }
        return bins to sinUbicar
    }

    // Preferencia entre unidades YA abiertas: gana el retazo (para gastar primero el material
    // sobrante y dejar intacto el espacio de la plancha entera); a igualdad de tipo, el mejor ajuste.
    private fun prefierePlacement(candRetazo: Boolean, candScore: Long, mejorRetazo: Boolean, mejorScore: Long): Boolean =
        if (candRetazo != mejorRetazo) candRetazo else candScore < mejorScore

    private fun encontrarMejorPlacement(
        pieza: PiezaExp, pw0: Int, ph0: Int,
        bins: List<Bin>, rotacion: Boolean, heuristica: Heuristica
    ): Placement? {
        var mejor: Placement? = null
        var mejorRetazo = false
        bins.forEachIndexed { bIdx, bin ->
            bin.libres.forEachIndexed { rIdx, r ->
                val p = encontrarMejorPlacementEnRect(pieza, pw0, ph0, bIdx, r, rotacion, heuristica)
                    ?: return@forEachIndexed
                if (mejor == null || prefierePlacement(bin.esRetazo, p.score, mejorRetazo, mejor!!.score)) {
                    mejor = p.copy(binIdx = bIdx, rectIdx = rIdx)
                    mejorRetazo = bin.esRetazo
                }
            }
        }
        return mejor
    }

    private fun encontrarMejorPlacementEnRect(
        pieza: PiezaExp, pw0: Int, ph0: Int,
        binIdx: Int, r: FreeRect, rotacion: Boolean, heuristica: Heuristica
    ): Placement? {
        var best: Placement? = null
        if (pw0 <= r.w && ph0 <= r.h) {
            val s = puntuar(heuristica, r, pw0, ph0)
            if (best == null || s < best.score)
                best = Placement(binIdx, 0, pw0, ph0, false, s)
        }
        if (rotacion && pieza.rotacionPermitida && pieza.anchoMm != pieza.altoMm
            && ph0 <= r.w && pw0 <= r.h) {
            val s = puntuar(heuristica, r, ph0, pw0)
            if (best == null || s < best.score)
                best = Placement(binIdx, 0, ph0, pw0, true, s)
        }
        return best
    }

    private fun colocarEnBin(
        pieza: PiezaExp, bin: Bin, rectIdx: Int,
        pw: Int, ph: Int, rotada: Boolean,
        heuristica: Heuristica,
        rng: Random? = null
    ) {
        val r = bin.libres[rectIdx]
        val cortePw = if (rotada) pieza.altoMm else pieza.anchoMm
        val cortePh = if (rotada) pieza.anchoMm else pieza.altoMm
        bin.cortes.add(CorteUbicadoPlancha(
            pieza.id, pieza.descripcion, pieza.material,
            r.x, r.y, cortePw, cortePh, rotada
        ))
        bin.libres.removeAt(rectIdx)
        // Con rng, la dirección del corte se decide POR RETAZO (no fija para toda la hoja): así se
        // generan patrones guillotina de dirección MIXTA (H y V combinados), que el split fijo no
        // alcanza. Sin rng, se usa la regla fija de la heurística (comportamiento previo).
        val splitHorizontal = if (rng != null) rng.nextBoolean() else when (heuristica) {
            Heuristica.GUILLOTINE_BAF_SAS,
            Heuristica.GUILLOTINE_BSSF_SAS,
            Heuristica.GUILLOTINE_BLSF_SAS -> r.w < r.h
            Heuristica.GUILLOTINE_BAF_LAS,
            Heuristica.GUILLOTINE_BSSF_LAS,
            Heuristica.GUILLOTINE_BLSF_LAS -> r.w >= r.h
        }
        if (splitHorizontal) {
            if (r.h - ph > 0) agregarLibre(bin, FreeRect(r.x, r.y + ph, r.w, r.h - ph))
            if (r.w - pw > 0) agregarLibre(bin, FreeRect(r.x + pw, r.y, r.w - pw, ph))
        } else {
            if (r.h - ph > 0) agregarLibre(bin, FreeRect(r.x, r.y + ph, pw, r.h - ph))
            if (r.w - pw > 0) agregarLibre(bin, FreeRect(r.x + pw, r.y, r.w - pw, r.h))
        }
    }

    private fun agregarLibre(bin: Bin, nuevo: FreeRect) {
        var actual = nuevo
        var cambio = true
        while (cambio) {
            cambio = false
            val it = bin.libres.iterator()
            while (it.hasNext()) {
                val otro = it.next()
                if (actual.y == otro.y && actual.h == otro.h) {
                    if (actual.x + actual.w == otro.x) {
                        actual = FreeRect(actual.x, actual.y, actual.w + otro.w, actual.h)
                        it.remove(); cambio = true; break
                    }
                    if (otro.x + otro.w == actual.x) {
                        actual = FreeRect(otro.x, actual.y, actual.w + otro.w, actual.h)
                        it.remove(); cambio = true; break
                    }
                }
                if (actual.x == otro.x && actual.w == otro.w) {
                    if (actual.y + actual.h == otro.y) {
                        actual = FreeRect(actual.x, actual.y, actual.w, actual.h + otro.h)
                        it.remove(); cambio = true; break
                    }
                    if (otro.y + otro.h == actual.y) {
                        actual = FreeRect(actual.x, otro.y, actual.w, actual.h + otro.h)
                        it.remove(); cambio = true; break
                    }
                }
            }
        }
        bin.libres.add(actual)
    }

    private fun puntuar(h: Heuristica, r: FreeRect, w: Int, hp: Int): Long {
        val sobraW = (r.w - w).toLong()
        val sobraH = (r.h - hp).toLong()
        return when (h) {
            Heuristica.GUILLOTINE_BAF_SAS,
            Heuristica.GUILLOTINE_BAF_LAS  -> r.w.toLong() * r.h - w.toLong() * hp
            Heuristica.GUILLOTINE_BSSF_SAS,
            Heuristica.GUILLOTINE_BSSF_LAS -> min(sobraW, sobraH)
            Heuristica.GUILLOTINE_BLSF_SAS,
            Heuristica.GUILLOTINE_BLSF_LAS -> max(sobraW, sobraH)
        }
    }

    private fun ordenar(piezas: List<PiezaExp>, modo: SortMode): List<PiezaExp> = when (modo) {
        SortMode.AREA_DESC   -> piezas.sortedByDescending { it.anchoMm.toLong() * it.altoMm }
        SortMode.AREA_ASC    -> piezas.sortedBy { it.anchoMm.toLong() * it.altoMm }
        SortMode.PERI_DESC   -> piezas.sortedByDescending { it.anchoMm + it.altoMm }
        SortMode.PERI_ASC    -> piezas.sortedBy { it.anchoMm + it.altoMm }
        SortMode.LSIDE_DESC  -> piezas.sortedByDescending { max(it.anchoMm, it.altoMm) }
        SortMode.SSIDE_DESC  -> piezas.sortedByDescending { min(it.anchoMm, it.altoMm) }
        SortMode.DIFF_DESC   -> piezas.sortedByDescending { abs(it.anchoMm - it.altoMm) }
        SortMode.RATIO_DESC  -> piezas.sortedByDescending {
            max(it.anchoMm, it.altoMm).toDouble() / max(1, min(it.anchoMm, it.altoMm))
        }
        SortMode.HEIGHT_DESC -> piezas.sortedByDescending { it.altoMm }
        SortMode.WIDTH_DESC  -> piezas.sortedByDescending { it.anchoMm }
    }

    // ── Validador anti-solapes (red de seguridad del motor MAXRECTS) ──────────
    // MAXRECTS es más complejo que el guillotina-split; este chequeo garantiza que jamás se
    // acepte un layout con piezas superpuestas o fuera de la plancha por un bug de partición.
    private fun esResultadoSinSolapes(bins: List<Bin>): Boolean {
        for (bin in bins) {
            for (c in bin.cortes) {
                if (c.xMm < 0 || c.yMm < 0 ||
                    c.xMm + c.anchoMm > bin.anchoMm || c.yMm + c.altoMm > bin.altoMm) return false
            }
            val cs = bin.cortes
            for (i in cs.indices) for (j in i + 1 until cs.size) {
                val a = cs[i]; val b = cs[j]
                if (a.xMm < b.xMm + b.anchoMm && a.xMm + a.anchoMm > b.xMm &&
                    a.yMm < b.yMm + b.altoMm && a.yMm + a.altoMm > b.yMm) return false
            }
        }
        return true
    }

    // ── Motor MAXRECTS (Best Short Side Fit) ──────────────────────────────────
    // Mantiene TODOS los rectángulos libres maximales (no parte a lo guillotina), lo que halla
    // colocaciones más densas que el guillotina-split. El resultado se acepta solo si además es
    // cortable con guillotina y sin solapes (validado aparte), así que sirve para vidrio.
    private data class MRPlacement(
        val binIdx: Int, val x: Int, val y: Int, val pw: Int, val ph: Int, val rotada: Boolean, val score: Long
    )

    private fun mrContiene(a: FreeRect, b: FreeRect): Boolean =
        a.x <= b.x && a.y <= b.y && a.x + a.w >= b.x + b.w && a.y + a.h >= b.y + b.h

    private fun mrMejorEnRects(
        libres: List<FreeRect>, pw: Int, ph: Int, pieza: PiezaExp, rotacion: Boolean
    ): MRPlacement? {
        var best: MRPlacement? = null
        for (f in libres) {
            if (pw <= f.w && ph <= f.h) {
                val score = min((f.w - pw).toLong(), (f.h - ph).toLong())
                if (best == null || score < best!!.score) best = MRPlacement(0, f.x, f.y, pw, ph, false, score)
            }
            if (rotacion && pieza.rotacionPermitida && pieza.anchoMm != pieza.altoMm && ph <= f.w && pw <= f.h) {
                val score = min((f.w - ph).toLong(), (f.h - pw).toLong())
                if (best == null || score < best!!.score) best = MRPlacement(0, f.x, f.y, ph, pw, true, score)
            }
        }
        return best
    }

    private fun mrColocar(bin: Bin, pieza: PiezaExp, p: MRPlacement) {
        val cortePw = if (p.rotada) pieza.altoMm else pieza.anchoMm
        val cortePh = if (p.rotada) pieza.anchoMm else pieza.altoMm
        bin.cortes.add(CorteUbicadoPlancha(
            pieza.id, pieza.descripcion, pieza.material, p.x, p.y, cortePw, cortePh, p.rotada
        ))
        dividirLibres(bin, p.x, p.y, p.pw, p.ph)
    }

    private fun empaquetarMaxRects(
        piezas: List<PiezaExp>, material: String,
        stock: List<PlanchaStock>,
        margen: Int, sep: Int, rotacion: Boolean
    ): Pair<List<Bin>, List<PiezaExp>> {
        val bins = mutableListOf<Bin>()
        val usados = IntArray(stock.size)
        val sinUbicar = mutableListOf<PiezaExp>()
        for (pieza in piezas) {
            val pw = pieza.anchoMm + sep
            val ph = pieza.altoMm + sep
            var mejor: MRPlacement? = null
            var mejorRetazo = false
            bins.forEachIndexed { bIdx, bin ->
                val p = mrMejorEnRects(bin.libres, pw, ph, pieza, rotacion)
                if (p != null && (mejor == null || prefierePlacement(bin.esRetazo, p.score, mejorRetazo, mejor!!.score))) {
                    mejor = p.copy(binIdx = bIdx)
                    mejorRetazo = bin.esRetazo
                }
            }
            if (mejor != null) {
                mrColocar(bins[mejor!!.binIdx], pieza, mejor!!)
                continue
            }
            val nuevoBin = abrirUnidad(stock, usados, material, pieza, pw, ph, margen, rotacion, null)
            if (nuevoBin == null) {
                sinUbicar += pieza
                continue
            }
            val p = mrMejorEnRects(nuevoBin.libres, pw, ph, pieza, rotacion)
            if (p == null) {
                sinUbicar += pieza
                continue
            }
            mrColocar(nuevoBin, pieza, p)
            bins.add(nuevoBin)
        }
        return bins to sinUbicar
    }

    // ── Motor de BANDAS (shelf) ───────────────────────────────────────────────
    // Agrupa piezas por altura en bandas horizontales (así las similares quedan JUNTAS y forman
    // bandas limpias con un retazo lateral aprovechable). Es guillotina por construcción (cortes
    // horizontales entre bandas, verticales dentro). El sobrante lateral de cada banda queda como
    // rectángulo libre, para que la fase de consolidación / otros motores puedan rellenarlo.
    private data class ItemShelf(
        val w: Int, val h: Int, val rot: Boolean, val pieza: PiezaExp
    )

    // Divide los rectángulos libres al colocar una pieza (gestión maxrects, mantiene maximales).
    private fun dividirLibres(bin: Bin, rx: Int, ry: Int, rw: Int, rh: Int) {
        val nuevos = mutableListOf<FreeRect>()
        val it = bin.libres.iterator()
        while (it.hasNext()) {
            val f = it.next()
            if (rx < f.x + f.w && rx + rw > f.x && ry < f.y + f.h && ry + rh > f.y) {
                it.remove()
                if (rx > f.x) nuevos.add(FreeRect(f.x, f.y, rx - f.x, f.h))
                if (rx + rw < f.x + f.w) nuevos.add(FreeRect(rx + rw, f.y, f.x + f.w - (rx + rw), f.h))
                if (ry > f.y) nuevos.add(FreeRect(f.x, f.y, f.w, ry - f.y))
                if (ry + rh < f.y + f.h) nuevos.add(FreeRect(f.x, ry + rh, f.w, f.y + f.h - (ry + rh)))
            }
        }
        for (n in nuevos) {
            if (n.w <= 0 || n.h <= 0) continue
            if (bin.libres.any { mrContiene(it, n) }) continue
            bin.libres.removeAll { mrContiene(n, it) }
            bin.libres.add(n)
        }
    }

    // Motor de BANDAS: maxrects con selección BOTTOM-LEFT (menor y, luego menor x) y orden por
    // altura desc. La colocación bottom-left forma bandas de piezas similares (largas juntas, etc.)
    // y, como mantiene los retazos laterales como rectángulos libres, las piezas chicas siguientes
    // caen en esas tiras. Resultado: agrupado y con relleno de tiras. Guillotina por construcción
    // (se valida igual). Prefiere retazos de entrada antes que abrir enteros.
    private fun empaquetarShelf(
        piezas: List<PiezaExp>, material: String,
        stock: List<PlanchaStock>,
        margen: Int, sep: Int, rotacion: Boolean
    ): Pair<List<Bin>, List<PiezaExp>> {
        val items = piezas.map { p ->
            if (rotacion && p.rotacionPermitida && p.altoMm > p.anchoMm)
                ItemShelf(p.altoMm, p.anchoMm, true, p)
            else ItemShelf(p.anchoMm, p.altoMm, false, p)
        }.sortedWith(compareByDescending<ItemShelf> { it.h }.thenByDescending { it.w })

        val bins = mutableListOf<Bin>()
        val usados = IntArray(stock.size)
        val sinUbicar = mutableListOf<PiezaExp>()
        for (item in items) {
            val pw = item.w + sep
            val ph = item.h + sep
            var bBin = -1; var bx = 0; var by = 0; var bRet = false; var bScore = Long.MAX_VALUE
            bins.forEachIndexed { idx, bin ->
                for (f in bin.libres) if (pw <= f.w && ph <= f.h) {
                    val sc = f.y.toLong() * bin.anchoMm + f.x   // bottom-left
                    val mejor = when {
                        bBin < 0 -> true
                        bin.esRetazo != bRet -> bin.esRetazo   // retazos primero
                        else -> sc < bScore
                    }
                    if (mejor) { bBin = idx; bx = f.x; by = f.y; bRet = bin.esRetazo; bScore = sc }
                }
            }
            if (bBin >= 0) {
                val bin = bins[bBin]
                bin.cortes.add(CorteUbicadoPlancha(
                    item.pieza.id, item.pieza.descripcion, item.pieza.material,
                    bx, by, item.w, item.h, item.rot
                ))
                dividirLibres(bin, bx, by, pw, ph)
                continue
            }
            // El item ya viene orientado, así que la unidad nueva debe aceptarlo en esa orientación.
            val idx = elegirEntrada(
                stock, usados, item.w.toLong() * item.h, null
            ) { e -> pw <= e.anchoMm - 2 * margen && ph <= e.altoMm - 2 * margen }
            if (idx < 0) {
                sinUbicar += item.pieza
                continue
            }
            usados[idx]++
            val nb = nuevoBinDe(stock[idx], idx, material, margen)
            nb.cortes.add(CorteUbicadoPlancha(
                item.pieza.id, item.pieza.descripcion, item.pieza.material,
                margen, margen, item.w, item.h, item.rot
            ))
            dividirLibres(nb, margen, margen, pw, ph)
            bins.add(nb)
        }
        return bins to sinUbicar
    }

    private fun calcularScore(
        bins: List<Bin>, sinUbicar: List<PiezaExp>, obj: ObjetivoOptimizacionPlanchas
    ): ScorePlanchas {
        val usadas = bins.filter { it.cortes.isNotEmpty() }
        return ScorePlanchas(
            objetivo = obj,
            faltantes = sinUbicar.size,
            areaFaltanteMm2 = sinUbicar.sumOf { it.anchoMm.toLong() * it.altoMm },
            enteras = usadas.count { !it.esRetazo },
            areaConsumidaMm2 = usadas.sumOf { it.anchoMm.toLong() * it.altoMm },
            unidades = usadas.size,
            // Se premia el MAYOR rectángulo libre único de todo el layout: maximizarlo obliga a
            // juntar el sobrante en un solo bloque reutilizable (donde quepan más piezas) en vez de
            // repartirlo en tiras inservibles, y de paso agrupa piezas similares en bandas limpias.
            // Nunca altera el material gastado: solo decide entre layouts que gastan lo mismo.
            mayorLibreMm2 = usadas.flatMap { it.libres }.maxOfOrNull { it.w.toLong() * it.h } ?: 0L,
            fragmentos = usadas.sumOf { it.libres.size }
        )
    }
}
