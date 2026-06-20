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

data class RetazoPlancha(
    val nombre: String,
    val anchoMm: Int,
    val altoMm: Int,
    val material: String = "General"
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

    fun optimizar(
        piezas: List<PiezaPlancha>,
        anchoPlanchaMm: Int,
        altoPlanchaMm: Int,
        retazos: List<RetazoPlancha> = emptyList(),
        objetivo: ObjetivoOptimizacionPlanchas = ObjetivoOptimizacionPlanchas.MENOS_PLANCHAS,
        intensidad: IntensidadOptimizacionPlanchas = IntensidadOptimizacionPlanchas.NORMAL,
        separacionCorteMm: Int = 0,
        margenPerimetralMm: Int = 0
    ): ResultadoOptimizacionPlanchas {
        if (anchoPlanchaMm <= 0 || altoPlanchaMm <= 0)
            return ResultadoOptimizacionPlanchas(emptyList(), piezas, 0, 0)

        val expandidas = piezas.flatMap { p ->
            List(max(0, p.cantidad)) { i ->
                PiezaExp(
                    "${p.id}-${i + 1}", p.descripcion, p.anchoMm, p.altoMm,
                    p.material.ifBlank { "General" }, p.rotacionPermitida
                )
            }
        }

        val materiales = expandidas.groupBy { it.material.trim().ifBlank { "General" } }
        var indiceGlobal = 1
        val hojasFinales = mutableListOf<PlanchaOptimizada>()
        val sinUbicarFinal = mutableListOf<PiezaPlancha>()

        materiales.toSortedMap(String.CASE_INSENSITIVE_ORDER).forEach { (mat, grupo) ->
            val grupoRetazos = retazos.filter { it.material.equals(mat, ignoreCase = true) }
            val (bins, sinUbicar) = resolverGrupo(
                grupo, mat, anchoPlanchaMm, altoPlanchaMm,
                grupoRetazos, objetivo, intensidad, separacionCorteMm, margenPerimetralMm
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
            sinUbicarFinal += sinUbicar.map {
                PiezaPlancha(it.id, it.descripcion, it.anchoMm, it.altoMm, 1, it.material, it.rotacionPermitida)
            }
        }

        return ResultadoOptimizacionPlanchas(
            hojasFinales, sinUbicarFinal,
            hojasFinales.sumOf { it.areaUsadaMm2 },
            hojasFinales.sumOf { it.areaDesperdicioMm2 }
        )
    }

    private fun resolverGrupo(
        piezas: List<PiezaExp>, material: String,
        anchoMm: Int, altoMm: Int,
        retazos: List<RetazoPlancha>,
        objetivo: ObjetivoOptimizacionPlanchas,
        intensidad: IntensidadOptimizacionPlanchas,
        sep: Int, margen: Int
    ): Pair<List<Bin>, List<PiezaExp>> {

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
            IntensidadOptimizacionPlanchas.NORMAL -> (1L..50L).toList()
            IntensidadOptimizacionPlanchas.PROFUNDO -> (1L..1000L).toList()
        }

        // Tiempo máximo. PROFUNDO sube a 60s para permitir el barrido amplio
        // y el paso adicional de relleno.
        val tiempoMaxMs = when (intensidad) {
            IntensidadOptimizacionPlanchas.RAPIDO -> 2_000L
            IntensidadOptimizacionPlanchas.NORMAL -> 10_000L
            IntensidadOptimizacionPlanchas.PROFUNDO -> 60_000L
        }

        var mejorBins: List<Bin>? = null
        var mejorScore = Double.MAX_VALUE
        var mejorDesc = ""
        var combinaciones = 0

        fun probar(ordenadas: List<PiezaExp>, rotacion: Boolean, heur: Heuristica, desc: String) {
            if (System.currentTimeMillis() - inicio > tiempoMaxMs) return
            combinaciones++
            val result = empaquetar(
                ordenadas, material, anchoMm, altoMm, retazos, margen, sep, rotacion, heur
            ) ?: return
            if (!esResultadoCortable(result)) return
            val score = calcularScore(result, objetivo)
            if (mejorBins == null || score < mejorScore) {
                mejorScore = score; mejorBins = result; mejorDesc = desc
            }
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

        // ── PASO DE RELLENO POST-EMPAQUETADO ──────────────────────────────────
        // Solo en NORMAL y PROFUNDO. Para cada plancha bajo el umbral de
        // aprovechamiento, intenta mover piezas de planchas posteriores que
        // quepan en sus huecos. Si una plancha posterior queda vacía,
        // desaparece → ahorro real.
        //
        // VALIDACIÓN CRÍTICA: el resultado post-relleno se acepta SOLO si
        // (a) sigue siendo cortable con guillotina y
        // (b) reduce el número de planchas.
        // Si no se cumplen ambas, se descarta el relleno y se conserva el
        // resultado original. Esto evita generar layouts no cortables
        // (piezas atrapadas) o degradar el resultado por reagrupar mal las
        // piezas en planchas posteriores.
        if (mejorBins != null && intensidad != IntensidadOptimizacionPlanchas.RAPIDO) {
            val umbral = if (intensidad == IntensidadOptimizacionPlanchas.PROFUNDO) 0.85f else 0.80f
            val candidato = rellenarPlanchasBajas(mejorBins!!, anchoMm, altoMm, umbral, true, objetivo)
            // Aceptar SOLO si mejora el score (menos planchas, o igual planchas con menos
            // desperdicio / vacío más consolidado) Y sigue siendo cortable. Nunca empeora.
            if (calcularScore(candidato, objetivo) < calcularScore(mejorBins!!, objetivo) &&
                esResultadoCortable(candidato)) {
                Log.d("OptimizadorPlanchas",
                    "Relleno aplicado: ${mejorBins!!.size} → ${candidato.size} planchas")
                mejorBins = candidato
            }
        }

        val tiempo = System.currentTimeMillis() - inicio
        Log.d("OptimizadorPlanchas",
            "[$material] $combinaciones combinaciones en ${tiempo}ms: " +
                    "${mejorBins?.count { it.cortes.isNotEmpty() } ?: 0} planchas — $mejorDesc"
        )

        if (mejorBins == null) return emptyList<Bin>() to piezas
        return mejorBins!!.filter { it.cortes.isNotEmpty() } to emptyList()
    }

    // ── PASO DE RELLENO ───────────────────────────────────────────────────────
    //
    // Estrategia: identifica las planchas con bajo aprovechamiento, "abre" las
    // siguientes planchas (las que están debajo de ellas en el orden), y re-empaca
    // todas esas piezas desde cero usando el algoritmo base — pero forzando que
    // primero se rellenen las planchas bajas existentes antes de abrir nuevas.
    //
    // Esto evita los bugs de mover piezas una por una con manejo manual de rotaciones.
    // Reusa el código de empaquetado base (que ya está probado) y garantiza que el
    // resultado sea cortable con guillotina (mismo split SAS/LAS, misma fusión de
    // libres, mismo validador).

    private fun rellenarPlanchasBajas(
        binsOriginales: List<Bin>,
        anchoMm: Int, altoMm: Int,
        umbral: Float,
        rotacion: Boolean,
        objetivo: ObjetivoOptimizacionPlanchas
    ): List<Bin> {
        if (binsOriginales.size <= 1) return binsOriginales

        // Encontrar la PRIMERA plancha con aprovechamiento bajo
        val primeraBaja = binsOriginales.indexOfFirst { b ->
            if (b.cortes.isEmpty()) false
            else {
                val area = b.anchoMm.toLong() * b.altoMm
                val usado = b.cortes.sumOf { it.anchoMm.toLong() * it.altoMm }
                usado.toFloat() / area < umbral
            }
        }
        if (primeraBaja < 0 || primeraBaja >= binsOriginales.size - 1) {
            return binsOriginales
        }

        // Conservar las planchas anteriores a la primera baja (no se tocan)
        val conservadas = binsOriginales.subList(0, primeraBaja).map { copiarBin(it) }

        // Recolectar TODAS las piezas desde la primera plancha baja en adelante
        val piezasARecolocar = mutableListOf<PiezaExp>()
        for (i in primeraBaja until binsOriginales.size) {
            for (c in binsOriginales[i].cortes) {
                // Reconstruir el PiezaExp original (deshacer rotación si la había)
                val anchoOrig = if (c.rotada) c.altoMm else c.anchoMm
                val altoOrig  = if (c.rotada) c.anchoMm else c.altoMm
                piezasARecolocar.add(PiezaExp(
                    c.piezaId, c.descripcion, anchoOrig, altoOrig, c.material, true
                ))
            }
        }

        // Probar varios órdenes/heurísticas (+ semillas) y quedarse con el LAYOUT
        // COMPLETO (conservadas + recolocación) de mejor score que siga siendo
        // cortable. El score prioriza menos planchas, luego menos desperdicio y por
        // último consolidación, así que esto rellena planchas tempranas y/o consolida
        // sin poder empeorar el resultado original.
        var mejorLayout: List<Bin> = binsOriginales
        var mejorScore = calcularScore(binsOriginales, objetivo)

        val sortsPrueba = listOf(
            SortMode.AREA_DESC, SortMode.LSIDE_DESC, SortMode.PERI_DESC,
            SortMode.SSIDE_DESC, SortMode.HEIGHT_DESC, SortMode.WIDTH_DESC
        )
        val heurPrueba = listOf(
            Heuristica.GUILLOTINE_BAF_SAS, Heuristica.GUILLOTINE_BAF_LAS,
            Heuristica.GUILLOTINE_BSSF_SAS, Heuristica.GUILLOTINE_BLSF_SAS
        )

        fun evaluar(ordenadas: List<PiezaExp>) {
            for (heur in heurPrueba) {
                val recolocadas = empaquetar(
                    ordenadas, binsOriginales[0].material,
                    anchoMm, altoMm, emptyList(), 0, 0, rotacion, heur
                ) ?: continue
                val candidato = conservadas + recolocadas
                val s = calcularScore(candidato, objetivo)
                if (s < mejorScore && esResultadoCortable(candidato)) {
                    mejorScore = s
                    mejorLayout = candidato
                }
            }
        }

        for (sort in sortsPrueba) evaluar(ordenar(piezasARecolocar, sort))
        for (seed in 1L..20L) evaluar(piezasARecolocar.shuffled(Random(seed)))

        return mejorLayout
    }

    private fun copiarBin(b: Bin): Bin = Bin(
        b.anchoMm, b.altoMm, b.esRetazo, b.nombre, b.material,
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

    // ── Empaquetado ───────────────────────────────────────────────────────────

    private fun empaquetar(
        piezas: List<PiezaExp>,
        material: String,
        anchoMm: Int, altoMm: Int,
        retazos: List<RetazoPlancha>,
        margen: Int, sep: Int,
        rotacion: Boolean,
        heuristica: Heuristica
    ): List<Bin>? {
        val bins = mutableListOf<Bin>()
        retazos.forEachIndexed { i, ret ->
            val aw = ret.anchoMm - 2 * margen
            val ah = ret.altoMm - 2 * margen
            if (aw > 0 && ah > 0) {
                bins.add(Bin(
                    ret.anchoMm, ret.altoMm, true,
                    ret.nombre.ifBlank { "Retazo ${i + 1}" }, material,
                    mutableListOf(FreeRect(margen, margen, aw, ah)),
                    mutableListOf()
                ))
            }
        }
        var nuevaCount = 0
        for (pieza in piezas) {
            val pw0 = pieza.anchoMm + sep
            val ph0 = pieza.altoMm + sep
            val mejor = encontrarMejorPlacement(pieza, pw0, ph0, bins, rotacion, heuristica)
            if (mejor != null) {
                colocarEnBin(pieza, bins[mejor.binIdx], mejor.rectIdx,
                    mejor.pw, mejor.ph, mejor.rotada, heuristica)
            } else {
                val aw = anchoMm - 2 * margen
                val ah = altoMm - 2 * margen
                if (aw <= 0 || ah <= 0) return null
                nuevaCount++
                val nuevoBin = Bin(
                    anchoMm, altoMm, false,
                    "$material - Plancha $nuevaCount", material,
                    mutableListOf(FreeRect(margen, margen, aw, ah)),
                    mutableListOf()
                )
                val fit = encontrarMejorPlacementEnRect(
                    pieza, pw0, ph0, 0, nuevoBin.libres[0], rotacion, heuristica
                ) ?: return null
                colocarEnBin(pieza, nuevoBin, 0, fit.pw, fit.ph, fit.rotada, heuristica)
                bins.add(nuevoBin)
            }
        }
        return bins
    }

    private fun encontrarMejorPlacement(
        pieza: PiezaExp, pw0: Int, ph0: Int,
        bins: List<Bin>, rotacion: Boolean, heuristica: Heuristica
    ): Placement? {
        var mejor: Placement? = null
        bins.forEachIndexed { bIdx, bin ->
            bin.libres.forEachIndexed { rIdx, r ->
                val p = encontrarMejorPlacementEnRect(pieza, pw0, ph0, bIdx, r, rotacion, heuristica)
                    ?: return@forEachIndexed
                if (mejor == null || p.score < mejor!!.score) mejor = p.copy(binIdx = bIdx, rectIdx = rIdx)
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
        heuristica: Heuristica
    ) {
        val r = bin.libres[rectIdx]
        val cortePw = if (rotada) pieza.altoMm else pieza.anchoMm
        val cortePh = if (rotada) pieza.anchoMm else pieza.altoMm
        bin.cortes.add(CorteUbicadoPlancha(
            pieza.id, pieza.descripcion, pieza.material,
            r.x, r.y, cortePw, cortePh, rotada
        ))
        bin.libres.removeAt(rectIdx)
        val splitHorizontal = when (heuristica) {
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

    private fun calcularScore(bins: List<Bin>, obj: ObjetivoOptimizacionPlanchas): Double {
        val planchasUsadas = bins.filter { it.cortes.isNotEmpty() }
        val nHojas = planchasUsadas.size.toLong()
        val despTotal = planchasUsadas.sumOf { b ->
            b.anchoMm.toLong() * b.altoMm - b.cortes.sumOf { it.anchoMm.toLong() * it.altoMm }
        }
        // Desempate por CONSOLIDACIÓN: entre layouts con igual nº de planchas y
        // desperdicio, preferir el que deja el mayor rectángulo libre único (retazo
        // aprovechable) y menos fragmentos. La escala (<1 mm²) garantiza que NUNCA
        // altera la jerarquía planchas > desperdicio; solo rompe empates.
        val libres = planchasUsadas.flatMap { it.libres }
        val mayorLibre = libres.maxOfOrNull { it.w.toLong() * it.h } ?: 0L
        val consolidacion = libres.size * 1e-3 - mayorLibre.toDouble() * 1e-12
        return when (obj) {
            ObjetivoOptimizacionPlanchas.MENOS_PLANCHAS    -> nHojas * 1e12 + despTotal + consolidacion
            ObjetivoOptimizacionPlanchas.MENOS_DESPERDICIO -> despTotal + nHojas * 100.0 + consolidacion
        }
    }
}
