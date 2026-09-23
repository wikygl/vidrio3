# Optimizacion de cortes de planchas para Crystal

Este documento consolida en un solo archivo toda la logica necesaria para replicar en Crystal la optimizacion de cortes de planchas desde Puntos.

## Que incluye

- Solver completo de corte de planchas.
- Capa de preparacion y agrupacion por material.
- Capa opcional de documento imprimible.
- Contratos minimos de `Listado` y `AppConfigEntity` para adaptar en Crystal.

## 1. Solver completo

Archivo fuente original: `composeApp/src/commonMain/kotlin/logic/OptimizadorCortesVidrio.kt`

```kotlin
package logic

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class PiezaCorteVidrio(
    val id: String,
    val descripcion: String,
    val anchoMm: Int,
    val altoMm: Int,
    val cantidad: Int,
    val rotacionPermitida: Boolean = true
)

data class CorteVidrioUbicado(
    val piezaId: String,
    val descripcion: String,
    val xMm: Int,
    val yMm: Int,
    val anchoMm: Int,
    val altoMm: Int,
    val rotada: Boolean
)

data class PlanchaCorteVidrio(
    val indice: Int,
    val nombreSuperficie: String = "",
    val esRetazoEntrada: Boolean = false,
    val anchoMm: Int,
    val altoMm: Int,
    val cortes: List<CorteVidrioUbicado>,
    val areaUsadaMm2: Long,
    val areaDesperdicioMm2: Long
)

data class ResultadoOptimizacionVidrio(
    val planchas: List<PlanchaCorteVidrio>,
    val piezasSinUbicar: List<PiezaCorteVidrio>,
    val areaUsadaMm2: Long,
    val areaDesperdicioMm2: Long
)

enum class ObjetivoOptimizacionCorteVidrio {
    MENOS_PLANCHAS,
    MENOS_DESPERDICIO
}

enum class IntensidadOptimizacionCorteVidrio {
    RAPIDO,
    NORMAL,
    PROFUNDO
}

data class SuperficieDisponibleCorteVidrio(
    val nombre: String,
    val anchoMm: Int,
    val altoMm: Int,
    val esRetazo: Boolean = false
)

private data class PiezaExpandidaVidrio(
    val id: String,
    val descripcion: String,
    val anchoMm: Int,
    val altoMm: Int,
    val rotacionPermitida: Boolean
)

private data class DemandaRetazoPendiente(
    val anchoMm: Int,
    val altoMm: Int,
    val rotacionPermitida: Boolean,
    val cantidad: Int
)

private data class RectanguloLibre(
    val xMm: Int,
    val yMm: Int,
    val anchoMm: Int,
    val altoMm: Int
) {
    val area: Int get() = anchoMm * altoMm
}

private enum class EstrategiaOrden {
    AREA_DESC,
    LADO_LARGO_DESC,
    LADO_CORTO_DESC,
    PERIMETRO_DESC,
    ANCHO_DESC,
    ALTO_DESC,
    AREA_ASC,
    MIXTA
}

private enum class EstrategiaColocacion {
    BEST_AREA_FIT,
    BEST_SHORT_SIDE_FIT,
    BEST_LONG_SIDE_FIT,
    BEST_EXACT_EDGE_FIT
}

private enum class EstrategiaCorte {
    HORIZONTAL_PRIMERO,
    VERTICAL_PRIMERO,
    MEJOR_SOBRANTE
}

private data class ConfiguracionOptimizacion(
    val orden: EstrategiaOrden,
    val colocacion: EstrategiaColocacion,
    val corte: EstrategiaCorte
)

private data class ColocacionCandidata(
    val indiceRectangulo: Int,
    val anchoMm: Int,
    val altoMm: Int,
    val rotada: Boolean,
    val shortSideFit: Int,
    val longSideFit: Int,
    val areaFit: Int,
    val edgeDelta: Int
)

private data class ResultadoInternoOptimizacion(
    val resultado: ResultadoOptimizacionVidrio,
    val score: Double
)

private data class PlanchaTrabajo(
    val indice: Int,
    val nombreSuperficie: String,
    val esRetazoEntrada: Boolean,
    val anchoPlanchaMm: Int,
    val altoPlanchaMm: Int,
    val rectangulosLibres: MutableList<RectanguloLibre>,
    val cortes: MutableList<CorteVidrioUbicado>
)

private data class EstadoBeamOptimizacion(
    val planchas: MutableList<PlanchaTrabajo>,
    val piezasNoUbicadas: MutableList<PiezaExpandidaVidrio>,
    val siguienteIndicePieza: Int,
    val scoreParcial: Double
)

private data class OpcionBeam(
    val indicePlancha: Int,
    val candidata: ColocacionCandidata,
    val creaPlanchaNueva: Boolean
)

private data class TipoPiezaPatron(
    val descripcion: String,
    val anchoMm: Int,
    val altoMm: Int,
    val rotacionPermitida: Boolean,
    val cantidadDisponible: Int
)

private data class PiezaPatronElegida(
    val descripcion: String,
    val anchoMm: Int,
    val altoMm: Int,
    val cantidad: Int
)

private data class FilaPatron(
    val piezas: List<PiezaPatronElegida>,
    val anchoUsadoMm: Int,
    val altoFilaMm: Int
)

private data class HojaPatronExacta(
    val filas: List<FilaPatron>,
    val areaUsada: Long,
    val altoUsado: Int
)

private data class TipoPiezaExacta(
    val descripcion: String,
    val anchoOriginalMm: Int,
    val altoOriginalMm: Int,
    val rotacionPermitida: Boolean,
    val cantidadDisponible: Int
)

private data class VarianteFilaExacta(
    val descripcion: String,
    val anchoOriginalMm: Int,
    val altoOriginalMm: Int,
    val anchoColocacionMm: Int,
    val altoColocacionMm: Int,
    val rotada: Boolean
)

private data class PiezaFilaExacta(
    val descripcion: String,
    val anchoOriginalMm: Int,
    val altoOriginalMm: Int,
    val anchoColocacionMm: Int,
    val altoColocacionMm: Int,
    val rotada: Boolean,
    val cantidad: Int
)

private data class FilaExactaCapas(
    val piezas: List<PiezaFilaExacta>,
    val anchoUsadoMm: Int,
    val altoFilaMm: Int,
    val areaUsadaMm2: Long
)

private data class HojaExactaCapas(
    val filas: List<FilaExactaCapas>,
    val altoUsadoMm: Int,
    val areaUsadaMm2: Long,
    val piezasColocadas: Int
)

private enum class MetodoEmpaqueCorteVidrio {
    GUILLOTINA,
    MAX_RECTS
}

fun optimizarCortesVidrio(
    piezas: List<PiezaCorteVidrio>,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    superficiesIniciales: List<SuperficieDisponibleCorteVidrio> = emptyList(),
    objetivo: ObjetivoOptimizacionCorteVidrio = ObjetivoOptimizacionCorteVidrio.MENOS_PLANCHAS,
    intensidad: IntensidadOptimizacionCorteVidrio = IntensidadOptimizacionCorteVidrio.NORMAL,
    separacionCorteMm: Int = 0,
    margenPerimetralMm: Int = 0
): ResultadoOptimizacionVidrio {
    if (anchoPlanchaMm <= 0 || altoPlanchaMm <= 0) {
        return ResultadoOptimizacionVidrio(emptyList(), piezas, 0, 0)
    }

    val anchoUtil = anchoPlanchaMm - (margenPerimetralMm * 2)
    val altoUtil = altoPlanchaMm - (margenPerimetralMm * 2)
    if (anchoUtil <= 0 || altoUtil <= 0) {
        return ResultadoOptimizacionVidrio(emptyList(), piezas, 0, 0)
    }

    val piezasExpandidas = expandirPiezas(piezas)
    if (piezasExpandidas.isEmpty()) {
        return ResultadoOptimizacionVidrio(emptyList(), emptyList(), 0, 0)
    }
    val aplicarMejoraPesada = piezasExpandidas.size <= 18

    val nuevoMotor = resolverPorCapasExactas(
        piezasExpandidas = piezasExpandidas,
        anchoPlanchaMm = anchoPlanchaMm,
        altoPlanchaMm = altoPlanchaMm,
        superficiesIniciales = superficiesIniciales,
        objetivo = objetivo,
        intensidad = intensidad,
        separacionCorteMm = separacionCorteMm.coerceAtLeast(0),
        margenPerimetralMm = margenPerimetralMm.coerceAtLeast(0)
    )
    if (nuevoMotor != null && nuevoMotor.resultado.planchas.isNotEmpty()) {
        val mejorado = if (aplicarMejoraPesada) {
            mejorarSolucionEntrePlanchas(
                resultado = nuevoMotor.resultado,
                objetivo = objetivo,
                anchoPlanchaMm = anchoPlanchaMm,
                altoPlanchaMm = altoPlanchaMm,
                separacionCorteMm = separacionCorteMm.coerceAtLeast(0),
                margenPerimetralMm = margenPerimetralMm.coerceAtLeast(0)
            )
        } else {
            nuevoMotor.resultado
        }
        return compactarPorAprovechamiento(
            resultado = mejorado,
            separacionCorteMm = separacionCorteMm.coerceAtLeast(0),
            margenPerimetralMm = margenPerimetralMm.coerceAtLeast(0)
        )
    }

    val beamWidth = when (intensidad) {
        IntensidadOptimizacionCorteVidrio.RAPIDO -> 3
        IntensidadOptimizacionCorteVidrio.NORMAL -> 6
        IntensidadOptimizacionCorteVidrio.PROFUNDO -> 12
    }
    val expansionLimit = when (intensidad) {
        IntensidadOptimizacionCorteVidrio.RAPIDO -> 2
        IntensidadOptimizacionCorteVidrio.NORMAL -> 5
        IntensidadOptimizacionCorteVidrio.PROFUNDO -> 10
    }
    val refinamientoTop = when (intensidad) {
        IntensidadOptimizacionCorteVidrio.RAPIDO -> 1
        IntensidadOptimizacionCorteVidrio.NORMAL -> 4
        IntensidadOptimizacionCorteVidrio.PROFUNDO -> 8
    }
    val usarSolverExacto = intensidad != IntensidadOptimizacionCorteVidrio.RAPIDO
    val usarMejoraEntrePlanchas = intensidad == IntensidadOptimizacionCorteVidrio.PROFUNDO
    val exacto = if (usarSolverExacto) {
        resolverMinimoRetazo(
            piezasExpandidas = piezasExpandidas,
            anchoPlanchaMm = anchoPlanchaMm,
            altoPlanchaMm = altoPlanchaMm,
            superficiesIniciales = superficiesIniciales,
            objetivo = objetivo,
            separacionCorteMm = separacionCorteMm.coerceAtLeast(0),
            margenPerimetralMm = margenPerimetralMm.coerceAtLeast(0),
            intensidad = intensidad
        )
    } else {
        null
    }

    val configuraciones = buildList {
        EstrategiaOrden.entries.forEach { orden ->
            EstrategiaColocacion.entries.forEach { colocacion ->
                EstrategiaCorte.entries.forEach { corte ->
                    add(ConfiguracionOptimizacion(orden, colocacion, corte))
                }
            }
        }
    }

    val metodosActivos = listOf(MetodoEmpaqueCorteVidrio.GUILLOTINA)

    val candidatos = configuraciones.flatMap { configuracion ->
        val piezasOrdenadas = ordenarPiezas(piezasExpandidas, configuracion.orden)
        metodosActivos.flatMap { metodo ->
            val greedy = resolverGreedy(
                piezasExpandidas = piezasOrdenadas,
                anchoPlanchaMm = anchoPlanchaMm,
                altoPlanchaMm = altoPlanchaMm,
                superficiesIniciales = superficiesIniciales,
                objetivo = objetivo,
                anchoUtil = anchoUtil,
                altoUtil = altoUtil,
                separacionCorteMm = separacionCorteMm.coerceAtLeast(0),
                margenPerimetralMm = margenPerimetralMm.coerceAtLeast(0),
                configuracion = configuracion,
                metodo = metodo
            )
            val beam = resolverBeam(
                piezasExpandidas = piezasOrdenadas,
                anchoPlanchaMm = anchoPlanchaMm,
                altoPlanchaMm = altoPlanchaMm,
                superficiesIniciales = superficiesIniciales,
                objetivo = objetivo,
                anchoUtil = anchoUtil,
                altoUtil = altoUtil,
                separacionCorteMm = separacionCorteMm.coerceAtLeast(0),
                margenPerimetralMm = margenPerimetralMm.coerceAtLeast(0),
                configuracion = configuracion,
                metodo = metodo,
                beamWidth = beamWidth,
                expansionLimit = expansionLimit
            )
            listOf(greedy, beam)
        }
    }

    val refinados = candidatos
        .sortedBy { it.score }
        .take(refinamientoTop)
        .flatMap { base ->
            refinarLocalmente(
                piezasExpandidas = piezasExpandidas,
                anchoPlanchaMm = anchoPlanchaMm,
                altoPlanchaMm = altoPlanchaMm,
                superficiesIniciales = superficiesIniciales,
                objetivo = objetivo,
                intensidad = intensidad,
                anchoUtil = anchoUtil,
                altoUtil = altoUtil,
                separacionCorteMm = separacionCorteMm.coerceAtLeast(0),
                margenPerimetralMm = margenPerimetralMm.coerceAtLeast(0)
            )
        }

    val patrones = if (usarSolverExacto) {
        resolverPorPatronesFilas(
            piezasExpandidas = piezasExpandidas,
            anchoPlanchaMm = anchoPlanchaMm,
            altoPlanchaMm = altoPlanchaMm,
            superficiesIniciales = superficiesIniciales,
            objetivo = objetivo,
            separacionCorteMm = separacionCorteMm.coerceAtLeast(0),
            margenPerimetralMm = margenPerimetralMm.coerceAtLeast(0)
        )
    } else {
        null
    }

    val mejor = (candidatos + refinados + listOfNotNull(patrones) + if (usarSolverExacto) listOfNotNull(exacto) else emptyList()).minByOrNull { it.score }
    val resultadoBase = mejor?.resultado ?: ResultadoOptimizacionVidrio(emptyList(), piezas, 0, 0)
    val resultadoMejorado = if (usarMejoraEntrePlanchas) {
        mejorarSolucionEntrePlanchas(
            resultado = resultadoBase,
            objetivo = objetivo,
            anchoPlanchaMm = anchoPlanchaMm,
            altoPlanchaMm = altoPlanchaMm,
            separacionCorteMm = separacionCorteMm.coerceAtLeast(0),
            margenPerimetralMm = margenPerimetralMm.coerceAtLeast(0)
        )
    } else {
        resultadoBase
    }
    return compactarPorAprovechamiento(
        resultado = resultadoMejorado,
        separacionCorteMm = separacionCorteMm.coerceAtLeast(0),
        margenPerimetralMm = margenPerimetralMm.coerceAtLeast(0)
    )
}

private fun resolverPorCapasExactas(
    piezasExpandidas: List<PiezaExpandidaVidrio>,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    superficiesIniciales: List<SuperficieDisponibleCorteVidrio>,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    intensidad: IntensidadOptimizacionCorteVidrio,
    separacionCorteMm: Int,
    margenPerimetralMm: Int
): ResultadoInternoOptimizacion? {
    val piezasDisponibles = piezasExpandidas.toMutableList()
    if (piezasDisponibles.isEmpty()) return null
    val maxNivelBusqueda = when {
        piezasExpandidas.size <= 18 -> 2
        piezasExpandidas.size <= 30 -> 1
        else -> 0
    }

    val anchoUtil = anchoPlanchaMm - (margenPerimetralMm * 2)
    val altoUtil = altoPlanchaMm - (margenPerimetralMm * 2)
    if (anchoUtil <= 0 || altoUtil <= 0) return null

    val planchas = mutableListOf<PlanchaTrabajo>()

    ordenarSuperficiesPorMejorAjuste(superficiesIniciales, piezasDisponibles)
        .forEachIndexed { index, superficie ->
            val hoja = buscarMejorHojaCapasExactas(
                tipos = agruparTiposExactos(piezasDisponibles),
                anchoObjetivoMm = superficie.anchoMm,
                altoObjetivoMm = superficie.altoMm,
                objetivo = objetivo,
                intensidad = intensidad,
                separacionCorteMm = separacionCorteMm,
                maxNivelBusqueda = maxNivelBusqueda,
                permitirAprovechamientoParcial = true
            ) ?: return@forEachIndexed
            if (hoja.filas.isEmpty()) return@forEachIndexed
            val plancha = PlanchaTrabajo(
                indice = planchas.size + 1,
                nombreSuperficie = superficie.nombre.ifBlank { "Retazo ${index + 1}" },
                esRetazoEntrada = superficie.esRetazo,
                anchoPlanchaMm = superficie.anchoMm,
                altoPlanchaMm = superficie.altoMm,
                rectangulosLibres = mutableListOf(),
                cortes = mutableListOf()
            )
            materializarHojaCapas(
                plancha = plancha,
                hoja = hoja,
                piezasDisponibles = piezasDisponibles,
                separacionCorteMm = separacionCorteMm,
                offsetX = 0,
                offsetY = 0
            )
            if (plancha.cortes.isNotEmpty()) {
                planchas += plancha
            }
        }

    var numeroPlancha = 1
    while (piezasDisponibles.isNotEmpty()) {
        val hoja = buscarMejorHojaCapasExactas(
            tipos = agruparTiposExactos(piezasDisponibles),
            anchoObjetivoMm = anchoUtil,
            altoObjetivoMm = altoUtil,
            objetivo = objetivo,
            intensidad = intensidad,
            separacionCorteMm = separacionCorteMm,
            maxNivelBusqueda = maxNivelBusqueda,
            permitirAprovechamientoParcial = false
        ) ?: break
        if (hoja.filas.isEmpty()) break
        val plancha = PlanchaTrabajo(
            indice = planchas.size + 1,
            nombreSuperficie = "Plancha $numeroPlancha",
            esRetazoEntrada = false,
            anchoPlanchaMm = anchoPlanchaMm,
            altoPlanchaMm = altoPlanchaMm,
            rectangulosLibres = mutableListOf(),
            cortes = mutableListOf()
        )
        materializarHojaCapas(
            plancha = plancha,
            hoja = hoja,
            piezasDisponibles = piezasDisponibles,
            separacionCorteMm = separacionCorteMm,
            offsetX = margenPerimetralMm,
            offsetY = margenPerimetralMm
        )
        if (plancha.cortes.isEmpty()) break
        planchas += plancha
        numeroPlancha++
    }

    if (planchas.isEmpty()) return null
    val resultado = construirResultado(planchas, piezasDisponibles)
    return ResultadoInternoOptimizacion(
        resultado = resultado,
        score = calcularScoreResultado(resultado, planchas, objetivo)
    )
}

private fun agruparTiposExactos(piezas: List<PiezaExpandidaVidrio>): List<TipoPiezaExacta> {
    return piezas
        .groupBy { Triple(it.descripcion, it.anchoMm, it.altoMm) }
        .map { (clave, grupo) ->
            TipoPiezaExacta(
                descripcion = clave.first,
                anchoOriginalMm = clave.second,
                altoOriginalMm = clave.third,
                rotacionPermitida = grupo.any { it.rotacionPermitida },
                cantidadDisponible = grupo.size
            )
        }
        .sortedByDescending { max(it.anchoOriginalMm, it.altoOriginalMm) * min(it.anchoOriginalMm, it.altoOriginalMm) }
}

private fun ordenarSuperficiesPorMejorAjuste(
    superficies: List<SuperficieDisponibleCorteVidrio>,
    piezasPendientes: List<PiezaExpandidaVidrio>
): List<SuperficieDisponibleCorteVidrio> {
    if (superficies.size <= 1 || piezasPendientes.isEmpty()) return superficies
    val demandas = piezasPendientes
        .groupBy { Pair(it.anchoMm, it.altoMm) }
        .map { (medida, grupo) ->
            DemandaRetazoPendiente(
                anchoMm = medida.first,
                altoMm = medida.second,
                rotacionPermitida = grupo.any { it.rotacionPermitida },
                cantidad = grupo.size
            )
        }
        .sortedByDescending { it.anchoMm * it.altoMm }

    return superficies.sortedWith(
        compareBy<SuperficieDisponibleCorteVidrio> { puntuarSuperficieParaDemanda(it, demandas) }
            .thenBy { it.anchoMm * it.altoMm }
            .thenBy { max(it.anchoMm, it.altoMm) }
            .thenBy { min(it.anchoMm, it.altoMm) }
    )
}

private fun puntuarSuperficieParaDemanda(
    superficie: SuperficieDisponibleCorteVidrio,
    demandas: List<DemandaRetazoPendiente>
): Double {
    val areaSuperficie = superficie.anchoMm.toLong() * superficie.altoMm.toLong()
    if (areaSuperficie <= 0L || demandas.isEmpty()) return Double.MAX_VALUE

    var mejorScore = Double.MAX_VALUE
    var encontroAjuste = false

    demandas.forEach { demanda ->
        val orientaciones = buildList {
            add(Pair(demanda.anchoMm, demanda.altoMm))
            if (demanda.rotacionPermitida && demanda.anchoMm != demanda.altoMm) {
                add(Pair(demanda.altoMm, demanda.anchoMm))
            }
        }.distinct()

        orientaciones.forEach { (anchoPieza, altoPieza) ->
            if (anchoPieza > superficie.anchoMm || altoPieza > superficie.altoMm) return@forEach
            val columnas = superficie.anchoMm / anchoPieza
            val filas = superficie.altoMm / altoPieza
            val capacidad = columnas * filas
            if (capacidad <= 0) return@forEach

            encontroAjuste = true
            val piezasAprovechables = min(capacidad, demanda.cantidad)
            val areaPieza = anchoPieza.toLong() * altoPieza.toLong()
            val areaUsada = areaPieza * piezasAprovechables
            val residuo = (areaSuperficie - areaUsada).coerceAtLeast(0L)
            val residuoRelativo = residuo.toDouble() / areaSuperficie.toDouble()
            val holguraBordes = (superficie.anchoMm % anchoPieza) + (superficie.altoMm % altoPieza)
            val cobertura = piezasAprovechables.toDouble() / demanda.cantidad.toDouble()
            val score = residuoRelativo * 1_000_000.0 +
                holguraBordes * 10.0 -
                cobertura * 1_000.0 -
                piezasAprovechables * 100.0
            if (score < mejorScore) mejorScore = score
        }
    }

    return if (encontroAjuste) mejorScore else 1_000_000_000.0 + areaSuperficie.toDouble()
}

private fun buscarMejorHojaCapasExactas(
    tipos: List<TipoPiezaExacta>,
    anchoObjetivoMm: Int,
    altoObjetivoMm: Int,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    intensidad: IntensidadOptimizacionCorteVidrio,
    separacionCorteMm: Int,
    maxNivelBusqueda: Int,
    permitirAprovechamientoParcial: Boolean = false
): HojaExactaCapas? {
    if (tipos.isEmpty() || anchoObjetivoMm <= 0 || altoObjetivoMm <= 0) return null
    val esRemateFinal = puedeIntentarRemateFinal(tipos, anchoObjetivoMm, altoObjetivoMm)
    val modoFlexible = esRemateFinal || (
        permitirAprovechamientoParcial &&
            puedeAprovecharSuperficieParcial(tipos, anchoObjetivoMm, altoObjetivoMm)
        )
    val base = resolverBusquedaHojaCapasExactas(
        tipos = tipos,
        anchoObjetivoMm = anchoObjetivoMm,
        altoObjetivoMm = altoObjetivoMm,
        objetivo = objetivo,
        intensidad = intensidad,
        separacionCorteMm = separacionCorteMm,
        nivelBusqueda = 0,
        esRemateFinal = modoFlexible
    )
    val retazoBase = base?.let { porcentajeRetazoHoja(it, anchoObjetivoMm, altoObjetivoMm) } ?: 1.0
    if (modoFlexible || retazoBase <= 0.07 || maxNivelBusqueda == 0) return base

    val agresiva = resolverBusquedaHojaCapasExactas(
        tipos = tipos,
        anchoObjetivoMm = anchoObjetivoMm,
        altoObjetivoMm = altoObjetivoMm,
        objetivo = objetivo,
        intensidad = intensidad,
        separacionCorteMm = separacionCorteMm,
        nivelBusqueda = 1,
        esRemateFinal = false
    )
    val retazoAgresiva = agresiva?.let { porcentajeRetazoHoja(it, anchoObjetivoMm, altoObjetivoMm) } ?: 1.0
    if (retazoAgresiva <= 0.07 || maxNivelBusqueda == 1) {
        return when {
            agresiva == null -> base
            base == null -> agresiva
            compararHojasCapas(agresiva, base, anchoObjetivoMm, altoObjetivoMm, objetivo) < 0 -> agresiva
            else -> base
        }
    }

    val profunda = resolverBusquedaHojaCapasExactas(
        tipos = tipos,
        anchoObjetivoMm = anchoObjetivoMm,
        altoObjetivoMm = altoObjetivoMm,
        objetivo = objetivo,
        intensidad = intensidad,
        separacionCorteMm = separacionCorteMm,
        nivelBusqueda = 2,
        esRemateFinal = false
    )
    return when {
        profunda != null && agresiva != null && base != null ->
            listOf(base, agresiva, profunda).minByOrNull {
                scoreHojaCapas(it, anchoObjetivoMm, altoObjetivoMm, objetivo)
            }
        profunda != null && agresiva != null ->
            listOf(agresiva, profunda).minByOrNull {
                scoreHojaCapas(it, anchoObjetivoMm, altoObjetivoMm, objetivo)
            }
        profunda != null && base != null ->
            listOf(base, profunda).minByOrNull {
                scoreHojaCapas(it, anchoObjetivoMm, altoObjetivoMm, objetivo)
            }
        profunda != null -> profunda
        agresiva == null -> base
        base == null -> agresiva
        compararHojasCapas(agresiva, base, anchoObjetivoMm, altoObjetivoMm, objetivo) < 0 -> agresiva
        else -> base
    }
}

private fun puedeIntentarRemateFinal(
    tipos: List<TipoPiezaExacta>,
    anchoObjetivoMm: Int,
    altoObjetivoMm: Int
): Boolean {
    val areaObjetivo = anchoObjetivoMm.toLong() * altoObjetivoMm.toLong()
    if (areaObjetivo <= 0L) return false
    val areaRestante = tipos.sumOf {
        it.anchoOriginalMm.toLong() * it.altoOriginalMm.toLong() * it.cantidadDisponible.toLong()
    }
    if (areaRestante > areaObjetivo) return false
    return tipos.all { tipo ->
        val entraNormal = tipo.anchoOriginalMm <= anchoObjetivoMm && tipo.altoOriginalMm <= altoObjetivoMm
        val entraRotado = tipo.rotacionPermitida &&
            tipo.altoOriginalMm <= anchoObjetivoMm &&
            tipo.anchoOriginalMm <= altoObjetivoMm
        entraNormal || entraRotado
    }
}

private fun resolverBusquedaHojaCapasExactas(
    tipos: List<TipoPiezaExacta>,
    anchoObjetivoMm: Int,
    altoObjetivoMm: Int,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    intensidad: IntensidadOptimizacionCorteVidrio,
    separacionCorteMm: Int,
    nivelBusqueda: Int,
    esRemateFinal: Boolean
): HojaExactaCapas? {
    val filas = generarFilasCapasExactas(
        tipos = tipos,
        anchoObjetivoMm = anchoObjetivoMm,
        separacionCorteMm = separacionCorteMm,
        intensidad = intensidad,
        nivelBusqueda = nivelBusqueda,
        esRemateFinal = esRemateFinal
    )
    if (filas.isEmpty()) return null

    val ramasBase = when (intensidad) {
        IntensidadOptimizacionCorteVidrio.RAPIDO -> 14
        IntensidadOptimizacionCorteVidrio.NORMAL -> 28
        IntensidadOptimizacionCorteVidrio.PROFUNDO -> 52
    }
    val ramasPorNivel = when (nivelBusqueda) {
        0 -> ramasBase
        1 -> ramasBase * 4
        else -> ramasBase * 7
    }
    val cantidadesIniciales = tipos.associate {
        claveTipoExacto(it.descripcion, it.anchoOriginalMm, it.altoOriginalMm) to it.cantidadDisponible
    }
    val memo = mutableMapOf<String, HojaExactaCapas>()

    fun resolver(altoRestanteMm: Int, tieneFilas: Boolean, restantes: Map<String, Int>): HojaExactaCapas {
        val claveMemo = buildString {
            append(altoRestanteMm)
            append('|')
            append(if (tieneFilas) '1' else '0')
            append('|')
            append(nivelBusqueda)
            append('|')
            restantes.toSortedMap().forEach { (clave, cantidad) ->
                append(clave)
                append(':')
                append(cantidad)
                append(';')
            }
        }
        memo[claveMemo]?.let { return it }

        var mejor = HojaExactaCapas(emptyList(), 0, 0, 0)
        val candidatas = filas
            .asSequence()
            .filter { fila ->
                val altoConsumido = fila.altoFilaMm + if (tieneFilas) separacionCorteMm else 0
                altoConsumido <= altoRestanteMm && filaDisponibleExacta(fila, restantes)
            }
            .take(ramasPorNivel)
            .toList()

        candidatas.forEach { fila ->
            val altoConsumido = fila.altoFilaMm + if (tieneFilas) separacionCorteMm else 0
            val resto = resolver(
                altoRestanteMm = altoRestanteMm - altoConsumido,
                tieneFilas = true,
                restantes = consumirFilaExacta(restantes, fila)
            )
            val candidata = HojaExactaCapas(
                filas = listOf(fila) + resto.filas,
                altoUsadoMm = altoConsumido + resto.altoUsadoMm,
                areaUsadaMm2 = fila.areaUsadaMm2 + resto.areaUsadaMm2,
                piezasColocadas = contarPiezasFilaExacta(fila) + resto.piezasColocadas
            )
            if (compararHojasCapas(candidata, mejor, anchoObjetivoMm, altoObjetivoMm, objetivo) < 0) {
                mejor = candidata
            }
        }

        memo[claveMemo] = mejor
        return mejor
    }

    return resolver(altoObjetivoMm, false, cantidadesIniciales).takeIf { it.filas.isNotEmpty() }
}

private fun generarFilasCapasExactas(
    tipos: List<TipoPiezaExacta>,
    anchoObjetivoMm: Int,
    separacionCorteMm: Int,
    intensidad: IntensidadOptimizacionCorteVidrio,
    nivelBusqueda: Int = 0,
    esRemateFinal: Boolean = false
): List<FilaExactaCapas> {
    val maxPiezasPorFila = when (intensidad) {
        IntensidadOptimizacionCorteVidrio.RAPIDO -> 4
        IntensidadOptimizacionCorteVidrio.NORMAL -> 6
        IntensidadOptimizacionCorteVidrio.PROFUNDO -> 8
    } + when (nivelBusqueda) {
        0 -> 0
        1 -> 4
        else -> 6
    }
    val maxFilasGuardadas = when (intensidad) {
        IntensidadOptimizacionCorteVidrio.RAPIDO -> 60
        IntensidadOptimizacionCorteVidrio.NORMAL -> 120
        IntensidadOptimizacionCorteVidrio.PROFUNDO -> 220
    } * when (nivelBusqueda) {
        0 -> 1
        1 -> 4
        else -> 7
    }
    val variantes = tipos.flatMap { tipo ->
        buildList {
            add(
                VarianteFilaExacta(
                    descripcion = tipo.descripcion,
                    anchoOriginalMm = tipo.anchoOriginalMm,
                    altoOriginalMm = tipo.altoOriginalMm,
                    anchoColocacionMm = tipo.anchoOriginalMm,
                    altoColocacionMm = tipo.altoOriginalMm,
                    rotada = false
                )
            )
            if (tipo.rotacionPermitida && tipo.anchoOriginalMm != tipo.altoOriginalMm) {
                add(
                    VarianteFilaExacta(
                        descripcion = tipo.descripcion,
                        anchoOriginalMm = tipo.anchoOriginalMm,
                        altoOriginalMm = tipo.altoOriginalMm,
                        anchoColocacionMm = tipo.altoOriginalMm,
                        altoColocacionMm = tipo.anchoOriginalMm,
                        rotada = true
                    )
                )
            }
        }
    }.sortedByDescending { it.anchoColocacionMm * it.altoColocacionMm }
    val minAnchoVariante = variantes.minOfOrNull { it.anchoColocacionMm } ?: return emptyList()
    val maxPorAncho = (anchoObjetivoMm / max(1, minAnchoVariante + separacionCorteMm)).coerceAtLeast(1)
    val maxPiezasEfectivas = min(maxPiezasPorFila, maxPorAncho.coerceAtMost(14))
    val coberturaMinima = if (esRemateFinal) {
        0.25
    } else {
        when (nivelBusqueda) {
            0 -> 0.78
            1 -> 0.72
            else -> 0.66
        }
    }
    val sobranteMaximoRegistro = if (esRemateFinal) {
        anchoObjetivoMm
    } else {
        when (nivelBusqueda) {
            0 -> (anchoObjetivoMm * 0.22).toInt()
            1 -> (anchoObjetivoMm * 0.30).toInt()
            else -> (anchoObjetivoMm * 0.38).toInt()
        }
    }
    val disponibles = tipos.associate {
        claveTipoExacto(it.descripcion, it.anchoOriginalMm, it.altoOriginalMm) to it.cantidadDisponible
    }
    val filasPorFirma = linkedMapOf<String, FilaExactaCapas>()

    fun registrarFila(piezas: List<PiezaFilaExacta>, anchoUsadoMm: Int, altoFilaMm: Int) {
        if (piezas.isEmpty()) return
        val sobrante = (anchoObjetivoMm - anchoUsadoMm).coerceAtLeast(0)
        val cobertura = if (anchoObjetivoMm > 0) anchoUsadoMm.toDouble() / anchoObjetivoMm.toDouble() else 0.0
        if (cobertura < coberturaMinima && sobrante > sobranteMaximoRegistro) return
        val vacioInterno = (anchoUsadoMm.toLong() * altoFilaMm.toLong()) - piezas.sumOf {
            it.anchoColocacionMm.toLong() * it.altoColocacionMm.toLong() * it.cantidad.toLong()
        }
        val vacioRelativo = if (anchoUsadoMm > 0 && altoFilaMm > 0) {
            vacioInterno.toDouble() / (anchoUsadoMm.toDouble() * altoFilaMm.toDouble())
        } else {
            0.0
        }
        if (!esRemateFinal && vacioRelativo > 0.12 && piezas.size > 1) return
        val fila = FilaExactaCapas(
            piezas = piezas.sortedByDescending { it.anchoColocacionMm * it.cantidad },
            anchoUsadoMm = anchoUsadoMm,
            altoFilaMm = altoFilaMm,
            areaUsadaMm2 = piezas.sumOf {
                it.anchoColocacionMm.toLong() * it.altoColocacionMm.toLong() * it.cantidad.toLong()
            }
        )
        val firma = fila.piezas.joinToString("|") {
            "${it.descripcion}:${it.anchoOriginalMm}x${it.altoOriginalMm}:${it.anchoColocacionMm}x${it.altoColocacionMm}:${it.cantidad}"
        }
        val actual = filasPorFirma[firma]
        if (actual == null || scoreFilaCapas(fila, anchoObjetivoMm) < scoreFilaCapas(actual, anchoObjetivoMm)) {
            filasPorFirma[firma] = fila
        }
    }

    fun backtrack(
        indiceInicio: Int,
        piezasActuales: MutableList<PiezaFilaExacta>,
        usadas: MutableMap<String, Int>,
        anchoUsadoMm: Int,
        altoFilaMm: Int,
        piezasColocadas: Int
    ) {
        registrarFila(piezasActuales, anchoUsadoMm, altoFilaMm)
        if (piezasColocadas >= maxPiezasEfectivas) return

        for (i in indiceInicio until variantes.size) {
            val variante = variantes[i]
            val clave = claveTipoExacto(variante.descripcion, variante.anchoOriginalMm, variante.altoOriginalMm)
            val usadasActuales = usadas[clave] ?: 0
            val disponiblesActuales = disponibles[clave] ?: 0
            if (usadasActuales >= disponiblesActuales) continue
            val orientacionConflicto = piezasActuales.any {
                it.descripcion == variante.descripcion &&
                    it.anchoOriginalMm == variante.anchoOriginalMm &&
                    it.altoOriginalMm == variante.altoOriginalMm &&
                    it.rotada != variante.rotada
            }
            if (orientacionConflicto) continue

            val anchoNuevo = if (piezasActuales.isEmpty()) {
                variante.anchoColocacionMm
            } else {
                anchoUsadoMm + separacionCorteMm + variante.anchoColocacionMm
            }
            if (anchoNuevo > anchoObjetivoMm) continue
            val sobranteNuevo = (anchoObjetivoMm - anchoNuevo).coerceAtLeast(0)
            val mejorAnchoPosible = anchoNuevo + ((maxPiezasEfectivas - (piezasColocadas + 1)) * (variantes.firstOrNull()?.anchoColocacionMm ?: 0))
            if (sobranteNuevo > sobranteMaximoRegistro * 2 && mejorAnchoPosible < (anchoObjetivoMm * coberturaMinima).toInt()) {
                continue
            }

            usadas[clave] = usadasActuales + 1
            val existente = piezasActuales.indexOfFirst {
                it.descripcion == variante.descripcion &&
                    it.anchoOriginalMm == variante.anchoOriginalMm &&
                    it.altoOriginalMm == variante.altoOriginalMm &&
                    it.anchoColocacionMm == variante.anchoColocacionMm &&
                    it.altoColocacionMm == variante.altoColocacionMm &&
                    it.rotada == variante.rotada
            }
            if (existente >= 0) {
                val actual = piezasActuales[existente]
                piezasActuales[existente] = actual.copy(cantidad = actual.cantidad + 1)
                backtrack(i, piezasActuales, usadas, anchoNuevo, max(altoFilaMm, variante.altoColocacionMm), piezasColocadas + 1)
                piezasActuales[existente] = actual
            } else {
                piezasActuales += PiezaFilaExacta(
                    descripcion = variante.descripcion,
                    anchoOriginalMm = variante.anchoOriginalMm,
                    altoOriginalMm = variante.altoOriginalMm,
                    anchoColocacionMm = variante.anchoColocacionMm,
                    altoColocacionMm = variante.altoColocacionMm,
                    rotada = variante.rotada,
                    cantidad = 1
                )
                backtrack(i, piezasActuales, usadas, anchoNuevo, max(altoFilaMm, variante.altoColocacionMm), piezasColocadas + 1)
                piezasActuales.removeAt(piezasActuales.lastIndex)
            }
            if (usadasActuales == 0) {
                usadas.remove(clave)
            } else {
                usadas[clave] = usadasActuales
            }
        }
    }

    backtrack(0, mutableListOf(), mutableMapOf(), 0, 0, 0)

    return filasPorFirma.values
        .sortedBy { scoreFilaCapas(it, anchoObjetivoMm) }
        .take(maxFilasGuardadas)
}

private fun filaDisponibleExacta(fila: FilaExactaCapas, restantes: Map<String, Int>): Boolean {
    return fila.piezas.all { pieza ->
        val clave = claveTipoExacto(pieza.descripcion, pieza.anchoOriginalMm, pieza.altoOriginalMm)
        (restantes[clave] ?: 0) >= pieza.cantidad
    }
}

private fun consumirFilaExacta(restantes: Map<String, Int>, fila: FilaExactaCapas): Map<String, Int> {
    val nuevo = restantes.toMutableMap()
    fila.piezas.forEach { pieza ->
        val clave = claveTipoExacto(pieza.descripcion, pieza.anchoOriginalMm, pieza.altoOriginalMm)
        nuevo[clave] = ((nuevo[clave] ?: 0) - pieza.cantidad).coerceAtLeast(0)
    }
    return nuevo
}

private fun contarPiezasFilaExacta(fila: FilaExactaCapas): Int {
    return fila.piezas.sumOf { it.cantidad }
}

private fun scoreFilaCapas(fila: FilaExactaCapas, anchoObjetivoMm: Int): Double {
    val sobrante = (anchoObjetivoMm - fila.anchoUsadoMm).coerceAtLeast(0)
    val franjaSobrante = sobrante.toDouble() * fila.altoFilaMm.toDouble()
    val areaCajaFila = fila.anchoUsadoMm.toLong() * fila.altoFilaMm.toLong()
    val vacioInterno = (areaCajaFila - fila.areaUsadaMm2).coerceAtLeast(0L).toDouble()
    val vacioRelativo = if (areaCajaFila > 0L) vacioInterno / areaCajaFila.toDouble() else 0.0
    val alturasDistintas = fila.piezas.map { it.altoColocacionMm }.distinct().size
    val orientacionesDistintas = fila.piezas.map { it.rotada }.distinct().size
    val desperdicioRelativo = if (anchoObjetivoMm > 0) {
        franjaSobrante / anchoObjetivoMm.toDouble()
    } else {
        franjaSobrante
    }
    val premioCierreExacto = when {
        sobrante == 0 -> 1_500_000.0
        sobrante <= 5 -> 900_000.0
        sobrante <= 10 -> 450_000.0
        else -> 0.0
    }
    val penalidadFranjaGrande = if (sobrante > anchoObjetivoMm * 0.18) {
        franjaSobrante * 6.0
    } else {
        franjaSobrante * 1.5
    }
    val penalidadVacioInterno = (vacioInterno * 12.0) + (vacioRelativo * 4_500_000.0)
    val penalidadDesalineacion = ((alturasDistintas - 1).coerceAtLeast(0) * 180_000.0) +
        ((orientacionesDistintas - 1).coerceAtLeast(0) * 80_000.0)
    return (sobrante * 1200.0) +
        (desperdicioRelativo * 2200.0) +
        penalidadFranjaGrande +
        penalidadVacioInterno +
        penalidadDesalineacion +
        (fila.piezas.size * 250.0) -
        (fila.areaUsadaMm2 / 1000.0) -
        premioCierreExacto
}

private fun compararHojasCapas(
    candidata: HojaExactaCapas,
    actual: HojaExactaCapas,
    anchoObjetivoMm: Int,
    altoObjetivoMm: Int,
    objetivo: ObjetivoOptimizacionCorteVidrio
): Int {
    return scoreHojaCapas(candidata, anchoObjetivoMm, altoObjetivoMm, objetivo)
        .compareTo(scoreHojaCapas(actual, anchoObjetivoMm, altoObjetivoMm, objetivo))
}

private fun scoreHojaCapas(
    hoja: HojaExactaCapas,
    anchoObjetivoMm: Int,
    altoObjetivoMm: Int,
    objetivo: ObjetivoOptimizacionCorteVidrio
): Double {
    if (hoja.filas.isEmpty()) return Double.MAX_VALUE
    val areaObjetivo = anchoObjetivoMm.toLong() * altoObjetivoMm.toLong()
    val desperdicio = (areaObjetivo - hoja.areaUsadaMm2).coerceAtLeast(0L).toDouble()
    val desperdicioRatio = if (areaObjetivo > 0L) {
        desperdicio / areaObjetivo.toDouble()
    } else {
        1.0
    }
    val retazoVertical = (altoObjetivoMm - hoja.altoUsadoMm).coerceAtLeast(0)
    val retazoHorizontal = hoja.filas.sumOf { (anchoObjetivoMm - it.anchoUsadoMm).coerceAtLeast(0) }
    val peorFranjaHorizontal = hoja.filas.maxOf { fila ->
        val sobranteFila = (anchoObjetivoMm - fila.anchoUsadoMm).coerceAtLeast(0)
        sobranteFila.toDouble() * fila.altoFilaMm.toDouble()
    }
    val filasCasiExactas = hoja.filas.count {
        (anchoObjetivoMm - it.anchoUsadoMm).coerceAtLeast(0) <= 10
    }
    val penalidadRetazoVertical = if (retazoVertical > altoObjetivoMm * 0.22) {
        retazoVertical * 9000.0
    } else {
        retazoVertical * 5000.0
    }
    return when (objetivo) {
        ObjetivoOptimizacionCorteVidrio.MENOS_PLANCHAS ->
            (desperdicio * 1.6) +
                (desperdicioRatio * 18_000_000.0) +
                penalidadRetazoVertical +
                (retazoHorizontal * 1400.0) +
                (peorFranjaHorizontal * 5.0) -
                (filasCasiExactas * 350_000.0) -
                (hoja.piezasColocadas * 120_000.0)
        ObjetivoOptimizacionCorteVidrio.MENOS_DESPERDICIO ->
            (desperdicio * 2.8) +
                (desperdicioRatio * 42_000_000.0) +
                penalidadRetazoVertical +
                (retazoHorizontal * 2000.0) +
                (peorFranjaHorizontal * 7.5) -
                (filasCasiExactas * 450_000.0) -
                (hoja.piezasColocadas * 80_000.0)
    }
}

private fun porcentajeRetazoHoja(
    hoja: HojaExactaCapas,
    anchoObjetivoMm: Int,
    altoObjetivoMm: Int
): Double {
    val areaObjetivo = anchoObjetivoMm.toLong() * altoObjetivoMm.toLong()
    if (areaObjetivo <= 0L) return 1.0
    val desperdicio = (areaObjetivo - hoja.areaUsadaMm2).coerceAtLeast(0L)
    return desperdicio.toDouble() / areaObjetivo.toDouble()
}

private fun materializarHojaCapas(
    plancha: PlanchaTrabajo,
    hoja: HojaExactaCapas,
    piezasDisponibles: MutableList<PiezaExpandidaVidrio>,
    separacionCorteMm: Int,
    offsetX: Int,
    offsetY: Int
) {
    var cursorY = offsetY
    hoja.filas.forEach { fila ->
        var cursorX = offsetX
        fila.piezas.forEach { piezaFila ->
            repeat(piezaFila.cantidad) {
                val indicePieza = piezasDisponibles.indexOfFirst {
                    it.descripcion == piezaFila.descripcion &&
                        it.anchoMm == piezaFila.anchoOriginalMm &&
                        it.altoMm == piezaFila.altoOriginalMm
                }
                if (indicePieza == -1) return@repeat
                val piezaReal = piezasDisponibles.removeAt(indicePieza)
                plancha.cortes += CorteVidrioUbicado(
                    piezaId = piezaReal.id,
                    descripcion = piezaReal.descripcion,
                    xMm = cursorX,
                    yMm = cursorY,
                    anchoMm = piezaFila.anchoColocacionMm,
                    altoMm = piezaFila.altoColocacionMm,
                    rotada = piezaFila.rotada
                )
                cursorX += piezaFila.anchoColocacionMm + separacionCorteMm
            }
        }
        cursorY += fila.altoFilaMm + separacionCorteMm
    }
}

private fun claveTipoExacto(descripcion: String, anchoMm: Int, altoMm: Int): String {
    return "$descripcion|$anchoMm|$altoMm"
}

private fun expandirPiezas(piezas: List<PiezaCorteVidrio>): List<PiezaExpandidaVidrio> {
    return piezas.flatMap { pieza ->
        List(pieza.cantidad.coerceAtLeast(0)) { indice ->
            PiezaExpandidaVidrio(
                id = "${pieza.id}-${indice + 1}",
                descripcion = pieza.descripcion,
                anchoMm = pieza.anchoMm,
                altoMm = pieza.altoMm,
                rotacionPermitida = pieza.rotacionPermitida
            )
        }
    }
}

private fun resolverGreedy(
    piezasExpandidas: List<PiezaExpandidaVidrio>,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    superficiesIniciales: List<SuperficieDisponibleCorteVidrio>,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    anchoUtil: Int,
    altoUtil: Int,
    separacionCorteMm: Int,
    margenPerimetralMm: Int,
    configuracion: ConfiguracionOptimizacion,
    metodo: MetodoEmpaqueCorteVidrio
): ResultadoInternoOptimizacion {
    val planchas = crearPlanchasIniciales(superficiesIniciales)
    val noUbicadas = mutableListOf<PiezaExpandidaVidrio>()

    for (pieza in piezasExpandidas) {
        val opcion = generarOpcionesColocacion(
            planchas = planchas,
            pieza = pieza,
            anchoPlanchaMm = anchoPlanchaMm,
            altoPlanchaMm = altoPlanchaMm,
            anchoUtil = anchoUtil,
            altoUtil = altoUtil,
            separacionCorteMm = separacionCorteMm,
            margenPerimetralMm = margenPerimetralMm,
            configuracion = configuracion,
            expansionLimit = 1
        ).firstOrNull()

        if (opcion == null) {
            noUbicadas += pieza
            continue
        }

        val indice = if (opcion.creaPlanchaNueva) {
            planchas += crearPlanchaNueva(
                indice = planchas.size + 1,
                numeroPlancha = planchas.count { !it.esRetazoEntrada } + 1,
                anchoPlanchaMm = anchoPlanchaMm,
                altoPlanchaMm = altoPlanchaMm,
                anchoUtil = anchoUtil,
                altoUtil = altoUtil,
                margenPerimetralMm = margenPerimetralMm
            )
            planchas.lastIndex
        } else {
            opcion.indicePlancha
        }
        colocarPiezaEnPlancha(planchas[indice], pieza, opcion.candidata, separacionCorteMm, configuracion.corte, metodo)
    }

    val resultado = construirResultado(planchas, noUbicadas)
    return ResultadoInternoOptimizacion(resultado, calcularScoreResultado(resultado, planchas, objetivo))
}

private fun resolverBeam(
    piezasExpandidas: List<PiezaExpandidaVidrio>,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    superficiesIniciales: List<SuperficieDisponibleCorteVidrio>,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    anchoUtil: Int,
    altoUtil: Int,
    separacionCorteMm: Int,
    margenPerimetralMm: Int,
    configuracion: ConfiguracionOptimizacion,
    metodo: MetodoEmpaqueCorteVidrio,
    beamWidth: Int,
    expansionLimit: Int
): ResultadoInternoOptimizacion {
    var estados = listOf(
        EstadoBeamOptimizacion(
            planchas = crearPlanchasIniciales(superficiesIniciales),
            piezasNoUbicadas = mutableListOf(),
            siguienteIndicePieza = 0,
            scoreParcial = 0.0
        )
    )

    while (estados.isNotEmpty() && estados.first().siguienteIndicePieza < piezasExpandidas.size) {
        val siguientes = mutableListOf<EstadoBeamOptimizacion>()
        estados.forEach { estado ->
            val pieza = piezasExpandidas[estado.siguienteIndicePieza]
            val opciones = generarOpcionesColocacion(
                planchas = estado.planchas,
                pieza = pieza,
                anchoPlanchaMm = anchoPlanchaMm,
                altoPlanchaMm = altoPlanchaMm,
                anchoUtil = anchoUtil,
                altoUtil = altoUtil,
                separacionCorteMm = separacionCorteMm,
                margenPerimetralMm = margenPerimetralMm,
                configuracion = configuracion,
                expansionLimit = expansionLimit
            )
            if (opciones.isEmpty()) {
                val nuevasNoUbicadas = estado.piezasNoUbicadas.toMutableList().apply { add(pieza) }
                val planchasClon = clonarPlanchas(estado.planchas)
                siguientes += EstadoBeamOptimizacion(
                    planchas = planchasClon,
                    piezasNoUbicadas = nuevasNoUbicadas,
                    siguienteIndicePieza = estado.siguienteIndicePieza + 1,
                    scoreParcial = scoreParcialBeam(planchasClon, nuevasNoUbicadas, objetivo)
                )
            } else {
                opciones.forEach { opcion ->
                    val planchasClon = clonarPlanchas(estado.planchas)
                    val indice = if (opcion.creaPlanchaNueva) {
                        planchasClon += crearPlanchaNueva(
                            indice = planchasClon.size + 1,
                            numeroPlancha = planchasClon.count { !it.esRetazoEntrada } + 1,
                            anchoPlanchaMm = anchoPlanchaMm,
                            altoPlanchaMm = altoPlanchaMm,
                            anchoUtil = anchoUtil,
                            altoUtil = altoUtil,
                            margenPerimetralMm = margenPerimetralMm
                        )
                        planchasClon.lastIndex
                    } else {
                        opcion.indicePlancha
                    }
                    colocarPiezaEnPlancha(planchasClon[indice], pieza, opcion.candidata, separacionCorteMm, configuracion.corte, metodo)
                    siguientes += EstadoBeamOptimizacion(
                        planchas = planchasClon,
                        piezasNoUbicadas = estado.piezasNoUbicadas.toMutableList(),
                        siguienteIndicePieza = estado.siguienteIndicePieza + 1,
                        scoreParcial = scoreParcialBeam(planchasClon, estado.piezasNoUbicadas, objetivo)
                    )
                }
            }
        }
        estados = siguientes.sortedBy { it.scoreParcial }.take(beamWidth)
    }

    val mejorEstado = estados.minByOrNull { it.scoreParcial }
    val resultado = construirResultado(
        planchas = mejorEstado?.planchas.orEmpty(),
        noUbicadas = mejorEstado?.piezasNoUbicadas.orEmpty()
    )
    return ResultadoInternoOptimizacion(
        resultado = resultado,
        score = calcularScoreResultado(resultado, mejorEstado?.planchas.orEmpty(), objetivo)
    )
}

private fun refinarLocalmente(
    piezasExpandidas: List<PiezaExpandidaVidrio>,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    superficiesIniciales: List<SuperficieDisponibleCorteVidrio>,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    intensidad: IntensidadOptimizacionCorteVidrio,
    anchoUtil: Int,
    altoUtil: Int,
    separacionCorteMm: Int,
    margenPerimetralMm: Int
): List<ResultadoInternoOptimizacion> {
    if (piezasExpandidas.size < 4) return emptyList()
    val variantes = buildList {
        add(piezasExpandidas)
        add(intercambiarIndices(piezasExpandidas, 0, min(2, piezasExpandidas.lastIndex)))
        add(intercambiarIndices(piezasExpandidas, 1, min(3, piezasExpandidas.lastIndex)))
        add(piezasExpandidas.chunked(2).flatMap { it.reversed() })
        add(piezasExpandidas.chunked(3).flatMap { it.reversed() })
        if (intensidad == IntensidadOptimizacionCorteVidrio.PROFUNDO) {
            add(piezasExpandidas.reversed())
            add(intercambiarIndices(piezasExpandidas, 0, piezasExpandidas.lastIndex))
            add(piezasExpandidas.chunked(4).flatMap { it.reversed() })
        }
    }.distinct()

    val beamWidth = when (intensidad) {
        IntensidadOptimizacionCorteVidrio.RAPIDO -> 3
        IntensidadOptimizacionCorteVidrio.NORMAL -> 5
        IntensidadOptimizacionCorteVidrio.PROFUNDO -> 10
    }
    val expansionLimit = when (intensidad) {
        IntensidadOptimizacionCorteVidrio.RAPIDO -> 2
        IntensidadOptimizacionCorteVidrio.NORMAL -> 4
        IntensidadOptimizacionCorteVidrio.PROFUNDO -> 8
    }

    return variantes.map { variante ->
        resolverBeam(
            piezasExpandidas = variante,
            anchoPlanchaMm = anchoPlanchaMm,
            altoPlanchaMm = altoPlanchaMm,
            superficiesIniciales = superficiesIniciales,
            objetivo = objetivo,
            anchoUtil = anchoUtil,
            altoUtil = altoUtil,
            separacionCorteMm = separacionCorteMm,
            margenPerimetralMm = margenPerimetralMm,
            configuracion = ConfiguracionOptimizacion(
                orden = EstrategiaOrden.MIXTA,
                colocacion = EstrategiaColocacion.BEST_SHORT_SIDE_FIT,
                corte = EstrategiaCorte.MEJOR_SOBRANTE
            ),
            metodo = MetodoEmpaqueCorteVidrio.GUILLOTINA,
            beamWidth = beamWidth,
            expansionLimit = expansionLimit
        )
    }
}

private fun ordenarPiezas(
    piezas: List<PiezaExpandidaVidrio>,
    estrategia: EstrategiaOrden
): List<PiezaExpandidaVidrio> {
    val comparador = when (estrategia) {
        EstrategiaOrden.AREA_DESC -> compareByDescending<PiezaExpandidaVidrio> { it.anchoMm * it.altoMm }
            .thenByDescending { max(it.anchoMm, it.altoMm) }
        EstrategiaOrden.LADO_LARGO_DESC -> compareByDescending<PiezaExpandidaVidrio> { max(it.anchoMm, it.altoMm) }
            .thenByDescending { min(it.anchoMm, it.altoMm) }
            .thenByDescending { it.anchoMm * it.altoMm }
        EstrategiaOrden.LADO_CORTO_DESC -> compareByDescending<PiezaExpandidaVidrio> { min(it.anchoMm, it.altoMm) }
            .thenByDescending { max(it.anchoMm, it.altoMm) }
        EstrategiaOrden.PERIMETRO_DESC -> compareByDescending<PiezaExpandidaVidrio> { (it.anchoMm + it.altoMm) * 2 }
            .thenByDescending { it.anchoMm * it.altoMm }
        EstrategiaOrden.ANCHO_DESC -> compareByDescending<PiezaExpandidaVidrio> { it.anchoMm }
            .thenByDescending { it.altoMm }
            .thenByDescending { it.anchoMm * it.altoMm }
        EstrategiaOrden.ALTO_DESC -> compareByDescending<PiezaExpandidaVidrio> { it.altoMm }
            .thenByDescending { it.anchoMm }
            .thenByDescending { it.anchoMm * it.altoMm }
        EstrategiaOrden.AREA_ASC -> compareBy<PiezaExpandidaVidrio> { it.anchoMm * it.altoMm }
            .thenByDescending { max(it.anchoMm, it.altoMm) }
        EstrategiaOrden.MIXTA -> compareByDescending<PiezaExpandidaVidrio> {
            (it.anchoMm * it.altoMm) + (max(it.anchoMm, it.altoMm) * 10)
        }.thenByDescending { min(it.anchoMm, it.altoMm) }
    }
    return piezas.sortedWith(comparador)
}

private fun generarOpcionesColocacion(
    planchas: List<PlanchaTrabajo>,
    pieza: PiezaExpandidaVidrio,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    anchoUtil: Int,
    altoUtil: Int,
    separacionCorteMm: Int,
    margenPerimetralMm: Int,
    configuracion: ConfiguracionOptimizacion,
    expansionLimit: Int
): List<OpcionBeam> {
    val opciones = mutableListOf<OpcionBeam>()
    planchas.forEachIndexed { indice, plancha ->
        buscarTopColocaciones(plancha.rectangulosLibres, pieza, configuracion.colocacion, expansionLimit)
            .forEach { opciones += OpcionBeam(indice, it, false) }
    }
    val rectNueva = listOf(RectanguloLibre(margenPerimetralMm, margenPerimetralMm, anchoUtil, altoUtil))
    buscarTopColocaciones(rectNueva, pieza, configuracion.colocacion, 1)
        .forEach { opciones += OpcionBeam(planchas.size, it, true) }
    return opciones
        .sortedWith(compareBy({ it.candidata.areaFit }, { it.candidata.shortSideFit }, { it.candidata.longSideFit }))
        .take(expansionLimit)
}

private fun buscarTopColocaciones(
    rectangulos: List<RectanguloLibre>,
    pieza: PiezaExpandidaVidrio,
    estrategia: EstrategiaColocacion,
    limite: Int
): List<ColocacionCandidata> {
    val candidatas = mutableListOf<ColocacionCandidata>()
    rectangulos.forEachIndexed { indice, rect ->
        val opciones = buildList {
            add(Triple(pieza.anchoMm, pieza.altoMm, false))
            if (pieza.rotacionPermitida && pieza.anchoMm != pieza.altoMm) {
                add(Triple(pieza.altoMm, pieza.anchoMm, true))
            }
        }
        for ((ancho, alto, rotada) in opciones) {
            if (ancho <= rect.anchoMm && alto <= rect.altoMm) {
                val restoHorizontal = rect.anchoMm - ancho
                val restoVertical = rect.altoMm - alto
                candidatas += ColocacionCandidata(
                    indiceRectangulo = indice,
                    anchoMm = ancho,
                    altoMm = alto,
                    rotada = rotada,
                    shortSideFit = min(restoHorizontal, restoVertical),
                    longSideFit = max(restoHorizontal, restoVertical),
                    areaFit = rect.area - (ancho * alto),
                    edgeDelta = abs(restoHorizontal) + abs(restoVertical)
                )
            }
        }
    }
    return candidatas.sortedWith { a, b ->
        when {
            esMejorCandidata(a, b, estrategia) -> -1
            esMejorCandidata(b, a, estrategia) -> 1
            else -> 0
        }
    }.take(limite)
}

private fun esMejorCandidata(
    candidata: ColocacionCandidata,
    mejorActual: ColocacionCandidata?,
    estrategia: EstrategiaColocacion
): Boolean {
    if (mejorActual == null) return true
    return when (estrategia) {
        EstrategiaColocacion.BEST_AREA_FIT -> compararTuplas(
            candidata.areaFit, candidata.shortSideFit, candidata.longSideFit,
            mejorActual.areaFit, mejorActual.shortSideFit, mejorActual.longSideFit
        )
        EstrategiaColocacion.BEST_SHORT_SIDE_FIT -> compararTuplas(
            candidata.shortSideFit, candidata.longSideFit, candidata.areaFit,
            mejorActual.shortSideFit, mejorActual.longSideFit, mejorActual.areaFit
        )
        EstrategiaColocacion.BEST_LONG_SIDE_FIT -> compararTuplas(
            candidata.longSideFit, candidata.shortSideFit, candidata.areaFit,
            mejorActual.longSideFit, mejorActual.shortSideFit, mejorActual.areaFit
        )
        EstrategiaColocacion.BEST_EXACT_EDGE_FIT -> compararTuplas(
            candidata.edgeDelta, candidata.shortSideFit, candidata.areaFit,
            mejorActual.edgeDelta, mejorActual.shortSideFit, mejorActual.areaFit
        )
    }
}

private fun compararTuplas(a1: Int, a2: Int, a3: Int, b1: Int, b2: Int, b3: Int): Boolean {
    return when {
        a1 != b1 -> a1 < b1
        a2 != b2 -> a2 < b2
        else -> a3 < b3
    }
}

private fun colocarPiezaEnPlancha(
    plancha: PlanchaTrabajo,
    pieza: PiezaExpandidaVidrio,
    candidata: ColocacionCandidata,
    separacionCorteMm: Int,
    estrategiaCorte: EstrategiaCorte,
    metodo: MetodoEmpaqueCorteVidrio
) {
    val rect = plancha.rectangulosLibres.removeAt(candidata.indiceRectangulo)
    val corte = CorteVidrioUbicado(
        piezaId = pieza.id,
        descripcion = pieza.descripcion,
        xMm = rect.xMm,
        yMm = rect.yMm,
        anchoMm = candidata.anchoMm,
        altoMm = candidata.altoMm,
        rotada = candidata.rotada
    )
    plancha.cortes += corte
    when (metodo) {
        MetodoEmpaqueCorteVidrio.GUILLOTINA -> {
            plancha.rectangulosLibres += dividirRectanguloLibre(rect, corte, separacionCorteMm, estrategiaCorte)
            depurarRectangulos(plancha.rectangulosLibres)
        }
        MetodoEmpaqueCorteVidrio.MAX_RECTS -> {
            plancha.rectangulosLibres += dividirRectanguloLibreMaxRects(rect, corte, separacionCorteMm)
            normalizarRectangulosMaxRects(plancha.rectangulosLibres)
        }
    }
}

private fun dividirRectanguloLibre(
    rect: RectanguloLibre,
    corte: CorteVidrioUbicado,
    separacionCorteMm: Int,
    estrategiaCorte: EstrategiaCorte
): List<RectanguloLibre> {
    val derecha = RectanguloLibre(
        xMm = rect.xMm + corte.anchoMm + separacionCorteMm,
        yMm = rect.yMm,
        anchoMm = rect.anchoMm - corte.anchoMm - separacionCorteMm,
        altoMm = rect.altoMm
    )
    val abajo = RectanguloLibre(
        xMm = rect.xMm,
        yMm = rect.yMm + corte.altoMm + separacionCorteMm,
        anchoMm = rect.anchoMm,
        altoMm = rect.altoMm - corte.altoMm - separacionCorteMm
    )
    val derechaAjustada = RectanguloLibre(
        xMm = rect.xMm + corte.anchoMm + separacionCorteMm,
        yMm = rect.yMm,
        anchoMm = rect.anchoMm - corte.anchoMm - separacionCorteMm,
        altoMm = corte.altoMm
    )
    val abajoAjustado = RectanguloLibre(
        xMm = rect.xMm,
        yMm = rect.yMm + corte.altoMm + separacionCorteMm,
        anchoMm = corte.anchoMm,
        altoMm = rect.altoMm - corte.altoMm - separacionCorteMm
    )
    val horizontal = listOf(derecha, abajoAjustado)
    val vertical = listOf(derechaAjustada, abajo)
    val elegidos = when (estrategiaCorte) {
        EstrategiaCorte.HORIZONTAL_PRIMERO -> horizontal
        EstrategiaCorte.VERTICAL_PRIMERO -> vertical
        EstrategiaCorte.MEJOR_SOBRANTE -> if (evaluarFragmentacion(horizontal) <= evaluarFragmentacion(vertical)) horizontal else vertical
    }
    return elegidos.filter { it.anchoMm > 0 && it.altoMm > 0 }
}

private fun evaluarFragmentacion(rectangulos: List<RectanguloLibre>): Int {
    if (rectangulos.isEmpty()) return Int.MAX_VALUE
    val areaMayor = rectangulos.maxOf { it.area }
    val areaTotal = rectangulos.sumOf { it.area }
    return areaTotal - areaMayor
}

private fun dividirRectanguloLibreMaxRects(
    rectBase: RectanguloLibre,
    corte: CorteVidrioUbicado,
    separacionCorteMm: Int
): List<RectanguloLibre> {
    val usadosX = corte.xMm
    val usadosY = corte.yMm
    val usadosRight = corte.xMm + corte.anchoMm + separacionCorteMm
    val usadosBottom = corte.yMm + corte.altoMm + separacionCorteMm
    val rectRight = rectBase.xMm + rectBase.anchoMm
    val rectBottom = rectBase.yMm + rectBase.altoMm

    if (usadosX >= rectRight || usadosRight <= rectBase.xMm || usadosY >= rectBottom || usadosBottom <= rectBase.yMm) {
        return listOf(rectBase)
    }

    val nuevos = mutableListOf<RectanguloLibre>()
    if (usadosX > rectBase.xMm) {
        nuevos += RectanguloLibre(
            xMm = rectBase.xMm,
            yMm = rectBase.yMm,
            anchoMm = usadosX - rectBase.xMm,
            altoMm = rectBase.altoMm
        )
    }
    if (usadosRight < rectRight) {
        nuevos += RectanguloLibre(
            xMm = usadosRight,
            yMm = rectBase.yMm,
            anchoMm = rectRight - usadosRight,
            altoMm = rectBase.altoMm
        )
    }
    if (usadosY > rectBase.yMm) {
        nuevos += RectanguloLibre(
            xMm = rectBase.xMm,
            yMm = rectBase.yMm,
            anchoMm = rectBase.anchoMm,
            altoMm = usadosY - rectBase.yMm
        )
    }
    if (usadosBottom < rectBottom) {
        nuevos += RectanguloLibre(
            xMm = rectBase.xMm,
            yMm = usadosBottom,
            anchoMm = rectBase.anchoMm,
            altoMm = rectBottom - usadosBottom
        )
    }
    return nuevos.filter { it.anchoMm > 0 && it.altoMm > 0 }
}

private fun normalizarRectangulosMaxRects(rectangulos: MutableList<RectanguloLibre>) {
    val base = rectangulos.filter { it.anchoMm > 0 && it.altoMm > 0 }.toMutableList()
    val depurados = mutableListOf<RectanguloLibre>()
    base.forEach { rect ->
        val contenido = base.any { otro ->
            otro !== rect &&
                rect.xMm >= otro.xMm &&
                rect.yMm >= otro.yMm &&
                rect.xMm + rect.anchoMm <= otro.xMm + otro.anchoMm &&
                rect.yMm + rect.altoMm <= otro.yMm + otro.altoMm
        }
        if (!contenido) {
            depurados += rect
        }
    }
    rectangulos.clear()
    rectangulos += depurados.distinct().sortedWith(compareBy({ it.yMm }, { it.xMm }))
}

private fun depurarRectangulos(rectangulos: MutableList<RectanguloLibre>) {
    val validos = rectangulos.filter { it.anchoMm > 0 && it.altoMm > 0 }
    val resultado = validos.filterNot { rect ->
        validos.any { otro ->
            otro !== rect &&
                rect.xMm >= otro.xMm &&
                rect.yMm >= otro.yMm &&
                rect.xMm + rect.anchoMm <= otro.xMm + otro.anchoMm &&
                rect.yMm + rect.altoMm <= otro.yMm + otro.altoMm
        }
    }.distinct().sortedWith(compareBy({ it.yMm }, { it.xMm }))
    rectangulos.clear()
    rectangulos += resultado
}

private fun crearPlanchasIniciales(superficiesIniciales: List<SuperficieDisponibleCorteVidrio>): MutableList<PlanchaTrabajo> {
    return superficiesIniciales.mapIndexedNotNull { index, superficie ->
        if (superficie.anchoMm <= 0 || superficie.altoMm <= 0) null else PlanchaTrabajo(
            indice = index + 1,
            nombreSuperficie = superficie.nombre,
            esRetazoEntrada = superficie.esRetazo,
            anchoPlanchaMm = superficie.anchoMm,
            altoPlanchaMm = superficie.altoMm,
            rectangulosLibres = mutableListOf(RectanguloLibre(0, 0, superficie.anchoMm, superficie.altoMm)),
            cortes = mutableListOf()
        )
    }.toMutableList()
}

private fun crearPlanchaNueva(
    indice: Int,
    numeroPlancha: Int,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    anchoUtil: Int,
    altoUtil: Int,
    margenPerimetralMm: Int
): PlanchaTrabajo {
    return PlanchaTrabajo(
        indice = indice,
        nombreSuperficie = "Plancha $numeroPlancha",
        esRetazoEntrada = false,
        anchoPlanchaMm = anchoPlanchaMm,
        altoPlanchaMm = altoPlanchaMm,
        rectangulosLibres = mutableListOf(
            RectanguloLibre(margenPerimetralMm, margenPerimetralMm, anchoUtil, altoUtil)
        ),
        cortes = mutableListOf()
    )
}

private fun clonarPlanchas(planchas: List<PlanchaTrabajo>): MutableList<PlanchaTrabajo> {
    return planchas.map {
        PlanchaTrabajo(
            indice = it.indice,
            nombreSuperficie = it.nombreSuperficie,
            esRetazoEntrada = it.esRetazoEntrada,
            anchoPlanchaMm = it.anchoPlanchaMm,
            altoPlanchaMm = it.altoPlanchaMm,
            rectangulosLibres = it.rectangulosLibres.toMutableList(),
            cortes = it.cortes.toMutableList()
        )
    }.toMutableList()
}

private fun construirResultado(
    planchas: List<PlanchaTrabajo>,
    noUbicadas: List<PiezaExpandidaVidrio>
): ResultadoOptimizacionVidrio {
    val planchasFinales = planchas.map { plancha ->
        val areaTotal = plancha.anchoPlanchaMm.toLong() * plancha.altoPlanchaMm.toLong()
        val areaUsada = plancha.cortes.sumOf { it.anchoMm.toLong() * it.altoMm.toLong() }
        PlanchaCorteVidrio(
            indice = plancha.indice,
            nombreSuperficie = plancha.nombreSuperficie,
            esRetazoEntrada = plancha.esRetazoEntrada,
            anchoMm = plancha.anchoPlanchaMm,
            altoMm = plancha.altoPlanchaMm,
            cortes = plancha.cortes.toList(),
            areaUsadaMm2 = areaUsada,
            areaDesperdicioMm2 = (areaTotal - areaUsada).coerceAtLeast(0L)
        )
    }
    return ResultadoOptimizacionVidrio(
        planchas = planchasFinales,
        piezasSinUbicar = noUbicadas.map {
            PiezaCorteVidrio(it.id, it.descripcion, it.anchoMm, it.altoMm, 1, it.rotacionPermitida)
        },
        areaUsadaMm2 = planchasFinales.sumOf { it.areaUsadaMm2 },
        areaDesperdicioMm2 = planchasFinales.sumOf { it.areaDesperdicioMm2 }
    )
}

private fun scoreParcialBeam(
    planchas: List<PlanchaTrabajo>,
    noUbicadas: List<PiezaExpandidaVidrio>,
    objetivo: ObjetivoOptimizacionCorteVidrio
): Double {
    val resultado = construirResultado(planchas, noUbicadas)
    return calcularScoreResultado(resultado, planchas, objetivo)
}

private fun calcularScoreResultado(
    resultado: ResultadoOptimizacionVidrio,
    planchas: List<PlanchaTrabajo>,
    objetivo: ObjetivoOptimizacionCorteVidrio
): Double {
    val areaTotal = resultado.areaUsadaMm2 + resultado.areaDesperdicioMm2
    val aprovechamiento = if (areaTotal > 0L) resultado.areaUsadaMm2.toDouble() / areaTotal.toDouble() else 0.0
    val planchasCompletasUsadas = planchas.count { !it.esRetazoEntrada && it.cortes.isNotEmpty() }
    val retazosUsados = planchas.count { it.esRetazoEntrada && it.cortes.isNotEmpty() }
    val fragmentacion = planchas.sumOf { it.rectangulosLibres.size * 1000.0 }
    val sobrantesPobres = planchas.sumOf { plancha ->
        plancha.rectangulosLibres.filter { it.area < 80_000 }.sumOf { it.area.toDouble() }
    }
    return when (objetivo) {
        ObjetivoOptimizacionCorteVidrio.MENOS_PLANCHAS ->
            (resultado.piezasSinUbicar.size * 1_000_000_000.0) +
                (planchasCompletasUsadas * 10_000_000.0) +
                (retazosUsados * 50_000.0) +
                resultado.areaDesperdicioMm2.toDouble() +
                fragmentacion +
                sobrantesPobres -
                (aprovechamiento * 100_000.0)
        ObjetivoOptimizacionCorteVidrio.MENOS_DESPERDICIO ->
            (resultado.piezasSinUbicar.size * 1_000_000_000.0) +
                resultado.areaDesperdicioMm2.toDouble() +
                (planchasCompletasUsadas * 2_500_000.0) +
                (retazosUsados * 25_000.0) +
                (fragmentacion * 1.5) +
                (sobrantesPobres * 1.2) -
                (aprovechamiento * 250_000.0)
    }
}

private fun intercambiarIndices(
    piezas: List<PiezaExpandidaVidrio>,
    a: Int,
    b: Int
): List<PiezaExpandidaVidrio> {
    if (a !in piezas.indices || b !in piezas.indices) return piezas
    val copia = piezas.toMutableList()
    val temp = copia[a]
    copia[a] = copia[b]
    copia[b] = temp
    return copia
}

private fun mejorarSolucionEntrePlanchas(
    resultado: ResultadoOptimizacionVidrio,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    separacionCorteMm: Int,
    margenPerimetralMm: Int
): ResultadoOptimizacionVidrio {
    if (resultado.planchas.size < 2 || resultado.piezasSinUbicar.isNotEmpty()) return resultado

    val planchas = resultado.planchas.toMutableList()

    while (true) {
        var mejorI = -1
        var mejorJ = -1
        var mejorReemplazo: List<PlanchaCorteVidrio>? = null
        var mejorGanancia = 0L

        for (i in 0 until planchas.lastIndex) {
            for (j in i + 1 until planchas.size) {
                val planchaA = planchas[i]
                val planchaB = planchas[j]
                if (planchaA.cortes.isEmpty() || planchaB.cortes.isEmpty()) continue
                if (planchaA.esRetazoEntrada || planchaB.esRetazoEntrada) continue

                val piezasPar = (planchaA.cortes + planchaB.cortes).mapIndexed { index, corte ->
                    PiezaExpandidaVidrio(
                        id = "par-${i + 1}-${j + 1}-${index + 1}",
                        descripcion = corte.descripcion,
                        anchoMm = corte.anchoMm,
                        altoMm = corte.altoMm,
                        rotacionPermitida = true
                    )
                }
                val candidato = listOfNotNull(
                    resolverPorPatronesFilasLimitado(
                        piezasExpandidas = piezasPar,
                        anchoPlanchaMm = anchoPlanchaMm,
                        altoPlanchaMm = altoPlanchaMm,
                        objetivo = objetivo,
                        separacionCorteMm = separacionCorteMm,
                        margenPerimetralMm = margenPerimetralMm,
                        maxPlanchas = 2
                    ),
                    resolverPorCapasExactas(
                        piezasExpandidas = piezasPar,
                        anchoPlanchaMm = anchoPlanchaMm,
                        altoPlanchaMm = altoPlanchaMm,
                        superficiesIniciales = emptyList(),
                        objetivo = objetivo,
                        intensidad = IntensidadOptimizacionCorteVidrio.NORMAL,
                        separacionCorteMm = separacionCorteMm,
                        margenPerimetralMm = margenPerimetralMm
                    )?.resultado
                )
                    .filter { it.planchas.size <= 2 && it.piezasSinUbicar.isEmpty() }
                    .minByOrNull { it.areaDesperdicioMm2 }
                    ?: continue

                val desperdicioActual = planchaA.areaDesperdicioMm2 + planchaB.areaDesperdicioMm2
                val desperdicioNuevo = candidato.areaDesperdicioMm2
                val ganancia = desperdicioActual - desperdicioNuevo
                if (ganancia <= 1_000L) continue

                if (ganancia > mejorGanancia) {
                    mejorGanancia = ganancia
                    mejorI = i
                    mejorJ = j
                    mejorReemplazo = normalizarPlanchasResultado(
                        candidato.planchas,
                        inicioIndice = planchaA.indice,
                        nombreBase = planchaA.nombreSuperficie.substringBeforeLast(" ").ifBlank { "Plancha" }
                    )
                }
            }
        }

        if (mejorI < 0 || mejorJ < 0 || mejorReemplazo == null) {
            break
        }

        planchas.removeAt(mejorJ)
        planchas.removeAt(mejorI)
        planchas.addAll(mejorI, mejorReemplazo)
    }

    return reconstruirResultadoFinal(
        planchas = planchas,
        piezasSinUbicar = resultado.piezasSinUbicar
    )
}

private fun puedeAprovecharSuperficieParcial(
    tipos: List<TipoPiezaExacta>,
    anchoObjetivoMm: Int,
    altoObjetivoMm: Int
): Boolean {
    return tipos.any { tipo ->
        val entraNormal = tipo.anchoOriginalMm <= anchoObjetivoMm && tipo.altoOriginalMm <= altoObjetivoMm
        val entraRotado = tipo.rotacionPermitida &&
            tipo.altoOriginalMm <= anchoObjetivoMm &&
            tipo.anchoOriginalMm <= altoObjetivoMm
        entraNormal || entraRotado
    }
}

private fun compactarPorAprovechamiento(
    resultado: ResultadoOptimizacionVidrio,
    separacionCorteMm: Int,
    margenPerimetralMm: Int
): ResultadoOptimizacionVidrio {
    if (resultado.planchas.size < 2 || resultado.piezasSinUbicar.isNotEmpty()) return resultado

    val planchas = resultado.planchas.toMutableList()
    val maxIteraciones = 3
    val umbralAprovechamiento = 0.90
    repeat(maxIteraciones) {
        val planchasBajoUmbral = planchas
            .filter { !it.esRetazoEntrada && it.cortes.isNotEmpty() }
            .filter { aprovechamientoPlancha(it) < umbralAprovechamiento }

        if (planchasBajoUmbral.size < 2) {
            return reconstruirResultadoFinal(
                planchas = planchas,
                piezasSinUbicar = resultado.piezasSinUbicar
            )
        }

        val donantes = planchasBajoUmbral.sortedBy { aprovechamientoPlancha(it) }

        var huboMejora = false
        donantes.forEach { donante ->
            val indiceDonante = planchas.indexOfFirst { it.indice == donante.indice }
            if (indiceDonante == -1) return@forEach

            val candidatas = planchasBajoUmbral
                .filter { plancha ->
                    plancha.indice != donante.indice
                }
                .sortedByDescending { it.areaDesperdicioMm2 }

            candidatas.forEach { receptora ->
                val absorbida = intentarAbsorberCortesEnPlancha(
                    planchaBase = receptora,
                    cortesAdicionales = donante.cortes,
                    separacionCorteMm = separacionCorteMm,
                    margenPerimetralMm = margenPerimetralMm
                ) ?: return@forEach

                val indiceReceptora = planchas.indexOfFirst { it.indice == receptora.indice }
                if (indiceReceptora == -1) return@forEach
                planchas[indiceReceptora] = absorbida.copy(
                    indice = receptora.indice,
                    nombreSuperficie = receptora.nombreSuperficie
                )
                planchas.removeAt(indiceDonante)
                huboMejora = true
                return@repeat
            }

            val redistribuidas = intentarRedistribuirCortesEnPlanchas(
                planchasBase = candidatas,
                cortesAdicionales = donante.cortes,
                separacionCorteMm = separacionCorteMm,
                margenPerimetralMm = margenPerimetralMm
            )
            if (redistribuidas != null) {
                redistribuidas.forEach { actualizada ->
                    val indice = planchas.indexOfFirst { it.indice == actualizada.indice }
                    if (indice != -1) {
                        planchas[indice] = actualizada
                    }
                }
                val indiceActualDonante = planchas.indexOfFirst { it.indice == donante.indice }
                if (indiceActualDonante != -1) {
                    planchas.removeAt(indiceActualDonante)
                }
                huboMejora = true
                return@repeat
            }
        }

        if (!huboMejora) {
            return reconstruirResultadoFinal(
                planchas = planchas,
                piezasSinUbicar = resultado.piezasSinUbicar
            )
        }
    }

    return reconstruirResultadoFinal(
        planchas = planchas,
        piezasSinUbicar = resultado.piezasSinUbicar
    )
}

private fun intentarRedistribuirCortesEnPlanchas(
    planchasBase: List<PlanchaCorteVidrio>,
    cortesAdicionales: List<CorteVidrioUbicado>,
    separacionCorteMm: Int,
    margenPerimetralMm: Int
): List<PlanchaCorteVidrio>? {
    if (planchasBase.isEmpty() || cortesAdicionales.isEmpty()) return null

    val actualizadas = planchasBase.associateBy { it.indice }.toMutableMap()
    val cortesPendientes = cortesAdicionales.sortedWith(
        compareByDescending<CorteVidrioUbicado> { it.anchoMm * it.altoMm }
            .thenByDescending { max(it.anchoMm, it.altoMm) }
            .thenByDescending { min(it.anchoMm, it.altoMm) }
    )

    cortesPendientes.forEach { corte ->
        val mejor = actualizadas.values
            .sortedByDescending { it.areaDesperdicioMm2 }
            .mapNotNull { plancha ->
                val absorbida = intentarAbsorberCortesEnPlancha(
                    planchaBase = plancha,
                    cortesAdicionales = listOf(corte),
                    separacionCorteMm = separacionCorteMm,
                    margenPerimetralMm = margenPerimetralMm
                ) ?: return@mapNotNull null
                Triple(plancha.indice, absorbida, plancha.areaDesperdicioMm2 - absorbida.areaDesperdicioMm2)
            }
            .maxByOrNull { it.third }

        if (mejor == null) return null
        actualizadas[mejor.first] = mejor.second
    }

    return planchasBase.mapNotNull { actualizadas[it.indice] }
}

private fun aprovecharamientoArea(plancha: PlanchaCorteVidrio): Pair<Long, Long> {
    val areaTotal = plancha.anchoMm.toLong() * plancha.altoMm.toLong()
    return Pair(plancha.areaUsadaMm2, areaTotal)
}

private fun aprovechamientoPlancha(plancha: PlanchaCorteVidrio): Double {
    val (usada, total) = aprovecharamientoArea(plancha)
    if (total <= 0L) return 0.0
    return usada.toDouble() / total.toDouble()
}

private fun intentarAbsorberCortesEnPlancha(
    planchaBase: PlanchaCorteVidrio,
    cortesAdicionales: List<CorteVidrioUbicado>,
    separacionCorteMm: Int,
    margenPerimetralMm: Int
): PlanchaCorteVidrio? {
    if (cortesAdicionales.isEmpty()) return null
    val trabajo = reconstruirPlanchaTrabajoDesdeCortes(
        plancha = planchaBase,
        separacionCorteMm = separacionCorteMm,
        margenPerimetralMm = margenPerimetralMm
    ) ?: return null

    val piezasPendientes = cortesAdicionales
        .mapIndexed { index, corte ->
            PiezaExpandidaVidrio(
                id = "compacta-final-${planchaBase.indice}-${index + 1}",
                descripcion = corte.descripcion,
                anchoMm = corte.anchoMm,
                altoMm = corte.altoMm,
                rotacionPermitida = true
            )
        }
        .sortedWith(
            compareByDescending<PiezaExpandidaVidrio> { it.anchoMm * it.altoMm }
                .thenByDescending { max(it.anchoMm, it.altoMm) }
                .thenByDescending { min(it.anchoMm, it.altoMm) }
        )

    val trabajoFinal = absorberCortesConBusqueda(
        trabajo = trabajo,
        piezasPendientes = piezasPendientes,
        indice = 0,
        separacionCorteMm = separacionCorteMm,
        profundidadMaxima = if (piezasPendientes.size <= 6) piezasPendientes.size else 6
    ) ?: return null

    if (planchaTieneSolapamientos(trabajoFinal.cortes)) return null

    val areaUsada = trabajoFinal.cortes.sumOf { it.anchoMm.toLong() * it.altoMm.toLong() }
    val areaTotal = planchaBase.anchoMm.toLong() * planchaBase.altoMm.toLong()
    return PlanchaCorteVidrio(
        indice = planchaBase.indice,
        nombreSuperficie = planchaBase.nombreSuperficie,
        esRetazoEntrada = planchaBase.esRetazoEntrada,
        anchoMm = planchaBase.anchoMm,
        altoMm = planchaBase.altoMm,
        cortes = trabajoFinal.cortes.toList(),
        areaUsadaMm2 = areaUsada,
        areaDesperdicioMm2 = (areaTotal - areaUsada).coerceAtLeast(0L)
    )
}

private fun absorberCortesConBusqueda(
    trabajo: PlanchaTrabajo,
    piezasPendientes: List<PiezaExpandidaVidrio>,
    indice: Int,
    separacionCorteMm: Int,
    profundidadMaxima: Int
): PlanchaTrabajo? {
    if (indice >= piezasPendientes.size) return trabajo

    val pieza = piezasPendientes[indice]
    val limite = if (indice < profundidadMaxima) 8 else 3
    val candidatas = buscarTopColocaciones(
        rectangulos = trabajo.rectangulosLibres,
        pieza = pieza,
        estrategia = EstrategiaColocacion.BEST_AREA_FIT,
        limite = limite
    )
    if (candidatas.isEmpty()) return null

    candidatas.forEach { candidata ->
        val clon = clonarPlanchaTrabajo(trabajo)
        colocarPiezaEnPlancha(
            plancha = clon,
            pieza = pieza,
            candidata = candidata,
            separacionCorteMm = separacionCorteMm,
            estrategiaCorte = EstrategiaCorte.MEJOR_SOBRANTE,
            metodo = MetodoEmpaqueCorteVidrio.MAX_RECTS
        )
        val resuelto = absorberCortesConBusqueda(
            trabajo = clon,
            piezasPendientes = piezasPendientes,
            indice = indice + 1,
            separacionCorteMm = separacionCorteMm,
            profundidadMaxima = profundidadMaxima
        )
        if (resuelto != null) return resuelto
    }
    return null
}

private fun clonarPlanchaTrabajo(plancha: PlanchaTrabajo): PlanchaTrabajo {
    return PlanchaTrabajo(
        indice = plancha.indice,
        nombreSuperficie = plancha.nombreSuperficie,
        esRetazoEntrada = plancha.esRetazoEntrada,
        anchoPlanchaMm = plancha.anchoPlanchaMm,
        altoPlanchaMm = plancha.altoPlanchaMm,
        rectangulosLibres = plancha.rectangulosLibres.toMutableList(),
        cortes = plancha.cortes.toMutableList()
    )
}

private fun reconstruirPlanchaTrabajoDesdeCortes(
    plancha: PlanchaCorteVidrio,
    separacionCorteMm: Int,
    margenPerimetralMm: Int
): PlanchaTrabajo? {
    val anchoUtil = plancha.anchoMm - (margenPerimetralMm * 2)
    val altoUtil = plancha.altoMm - (margenPerimetralMm * 2)
    if (anchoUtil <= 0 || altoUtil <= 0) return null

    val rectangulos = mutableListOf(
        RectanguloLibre(
            xMm = margenPerimetralMm,
            yMm = margenPerimetralMm,
            anchoMm = anchoUtil,
            altoMm = altoUtil
        )
    )

    plancha.cortes.sortedWith(compareBy({ it.yMm }, { it.xMm })).forEach { corte ->
        val nuevos = mutableListOf<RectanguloLibre>()
        rectangulos.forEach { rect ->
            nuevos += dividirRectanguloLibreMaxRects(rect, corte, separacionCorteMm)
        }
        rectangulos.clear()
        rectangulos += nuevos
        normalizarRectangulosMaxRects(rectangulos)
    }

    return PlanchaTrabajo(
        indice = plancha.indice,
        nombreSuperficie = plancha.nombreSuperficie,
        esRetazoEntrada = plancha.esRetazoEntrada,
        anchoPlanchaMm = plancha.anchoMm,
        altoPlanchaMm = plancha.altoMm,
        rectangulosLibres = rectangulos,
        cortes = plancha.cortes.toMutableList()
    )
}

private fun planchaTieneSolapamientos(cortes: List<CorteVidrioUbicado>): Boolean {
    for (i in 0 until cortes.lastIndex) {
        for (j in i + 1 until cortes.size) {
            if (cortesSeSolapan(cortes[i], cortes[j])) {
                return true
            }
        }
    }
    return false
}

private fun cortesSeSolapan(a: CorteVidrioUbicado, b: CorteVidrioUbicado): Boolean {
    val aRight = a.xMm + a.anchoMm
    val aBottom = a.yMm + a.altoMm
    val bRight = b.xMm + b.anchoMm
    val bBottom = b.yMm + b.altoMm

    val separados = aRight <= b.xMm ||
        bRight <= a.xMm ||
        aBottom <= b.yMm ||
        bBottom <= a.yMm

    return !separados
}

private fun resolverPorPatronesFilasLimitado(
    piezasExpandidas: List<PiezaExpandidaVidrio>,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    separacionCorteMm: Int,
    margenPerimetralMm: Int,
    maxPlanchas: Int
): ResultadoOptimizacionVidrio? {
    val anchoUtil = anchoPlanchaMm - (margenPerimetralMm * 2)
    val altoUtil = altoPlanchaMm - (margenPerimetralMm * 2)
    if (anchoUtil <= 0 || altoUtil <= 0) return null
    val anclasAncho = anclasDivision(anchoUtil)
    val anclasAlto = anclasDivision(altoUtil)

    val tipos = piezasExpandidas
        .groupBy { Triple(it.descripcion, it.anchoMm, it.altoMm) }
        .map { (clave, grupo) ->
            TipoPiezaPatron(
                descripcion = clave.first,
                anchoMm = clave.second,
                altoMm = clave.third,
                rotacionPermitida = grupo.any { it.rotacionPermitida },
                cantidadDisponible = grupo.size
            )
        }
        .sortedByDescending { it.anchoMm * it.altoMm }

    val filas = generarFilasPatron(tipos, anchoUtil, separacionCorteMm)
        .sortedBy { scoreFilaPatron(it, anchoUtil, anclasAncho, anclasAlto) }
        .take(24)
    if (filas.isEmpty()) return null

    val planchas = mutableListOf<PlanchaTrabajo>()
    val restantes = tipos.associateBy(
        keySelector = { Triple(it.descripcion, it.anchoMm, it.altoMm) },
        valueTransform = { it.cantidadDisponible }
    ).toMutableMap()
    val piezasDisponibles = piezasExpandidas.toMutableList()

    repeat(maxPlanchas) { numero ->
        if (restantes.values.none { it > 0 }) return@repeat
        val combinacion = elegirCombinacionFilas(
            filas = filas,
            restantes = restantes,
            altoObjetivoMm = altoUtil,
            separacionCorteMm = separacionCorteMm,
            anchoObjetivoMm = anchoUtil,
            anclasAncho = anclasAncho,
            anclasAlto = anclasAlto
        )
        if (combinacion.isEmpty()) return@repeat

        val plancha = crearPlanchaNueva(
            indice = planchas.size + 1,
            numeroPlancha = numero + 1,
            anchoPlanchaMm = anchoPlanchaMm,
            altoPlanchaMm = altoPlanchaMm,
            anchoUtil = anchoUtil,
            altoUtil = altoUtil,
            margenPerimetralMm = margenPerimetralMm
        )
        plancha.rectangulosLibres.clear()
        var cursorY = margenPerimetralMm
        combinacion.forEach { fila ->
            var cursorX = margenPerimetralMm
            fila.piezas.forEach { piezaFila ->
                repeat(piezaFila.cantidad) {
                    val indicePieza = piezasDisponibles.indexOfFirst {
                        it.descripcion == piezaFila.descripcion &&
                            it.anchoMm == piezaFila.anchoMm &&
                            it.altoMm == piezaFila.altoMm
                    }
                    if (indicePieza == -1) return@repeat
                    val piezaReal = piezasDisponibles.removeAt(indicePieza)
                    plancha.cortes += CorteVidrioUbicado(
                        piezaId = piezaReal.id,
                        descripcion = piezaReal.descripcion,
                        xMm = cursorX,
                        yMm = cursorY,
                        anchoMm = piezaFila.anchoMm,
                        altoMm = piezaFila.altoMm,
                        rotada = false
                    )
                    val clave = Triple(piezaFila.descripcion, piezaFila.anchoMm, piezaFila.altoMm)
                    restantes[clave] = (restantes[clave] ?: 0) - 1
                    cursorX += piezaFila.anchoMm + separacionCorteMm
                }
            }
            cursorY += fila.altoFilaMm + separacionCorteMm
        }
        planchas += plancha
    }

    val noUbicadas = piezasDisponibles
    val resultado = construirResultado(planchas, noUbicadas)
    return if (resultado.piezasSinUbicar.isEmpty() && resultado.planchas.size <= maxPlanchas) {
        resultado
    } else {
        null
    }
}

private fun normalizarPlanchasResultado(
    planchas: List<PlanchaCorteVidrio>,
    inicioIndice: Int,
    nombreBase: String
): List<PlanchaCorteVidrio> {
    return planchas.mapIndexed { index, plancha ->
        plancha.copy(
            indice = inicioIndice + index,
            nombreSuperficie = "$nombreBase ${inicioIndice + index}"
        )
    }
}

private fun reconstruirResultadoFinal(
    planchas: List<PlanchaCorteVidrio>,
    piezasSinUbicar: List<PiezaCorteVidrio>
): ResultadoOptimizacionVidrio {
    val planchasOrdenadas = planchas.mapIndexed { index, plancha ->
        plancha.copy(indice = index + 1)
    }
    return ResultadoOptimizacionVidrio(
        planchas = planchasOrdenadas,
        piezasSinUbicar = piezasSinUbicar,
        areaUsadaMm2 = planchasOrdenadas.sumOf { it.areaUsadaMm2 },
        areaDesperdicioMm2 = planchasOrdenadas.sumOf { it.areaDesperdicioMm2 }
    )
}

private fun resolverPorPatronesFilas(
    piezasExpandidas: List<PiezaExpandidaVidrio>,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    superficiesIniciales: List<SuperficieDisponibleCorteVidrio>,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    separacionCorteMm: Int,
    margenPerimetralMm: Int
): ResultadoInternoOptimizacion? {
    if (superficiesIniciales.isNotEmpty()) return null
    val anchoUtil = anchoPlanchaMm - (margenPerimetralMm * 2)
    val altoUtil = altoPlanchaMm - (margenPerimetralMm * 2)
    if (anchoUtil <= 0 || altoUtil <= 0) return null
    val anclasAncho = anclasDivision(anchoUtil)
    val anclasAlto = anclasDivision(altoUtil)

    val tipos = piezasExpandidas
        .groupBy { Triple(it.descripcion, it.anchoMm, it.altoMm) }
        .map { (clave, grupo) ->
            TipoPiezaPatron(
                descripcion = clave.first,
                anchoMm = clave.second,
                altoMm = clave.third,
                rotacionPermitida = grupo.any { it.rotacionPermitida },
                cantidadDisponible = grupo.size
            )
        }
        .sortedByDescending { it.anchoMm * it.altoMm }

    val filas = generarFilasPatron(tipos, anchoUtil, separacionCorteMm)
        .sortedBy { scoreFilaPatron(it, anchoUtil, anclasAncho, anclasAlto) }
        .take(24)
    if (filas.isEmpty()) return null

    val planchas = mutableListOf<PlanchaTrabajo>()
    val restantes = tipos.associateBy(
        keySelector = { Triple(it.descripcion, it.anchoMm, it.altoMm) },
        valueTransform = { it.cantidadDisponible }
    ).toMutableMap()
    val piezasNoUbicadas = mutableListOf<PiezaExpandidaVidrio>()
    val piezasDisponibles = piezasExpandidas.toMutableList()

    while (restantes.values.any { it > 0 }) {
        val combinacion = elegirCombinacionFilas(
            filas = filas,
            restantes = restantes,
            altoObjetivoMm = altoUtil,
            separacionCorteMm = separacionCorteMm,
            anchoObjetivoMm = anchoUtil,
            anclasAncho = anclasAncho,
            anclasAlto = anclasAlto
        )
        if (combinacion.isEmpty()) break
        val plancha = crearPlanchaNueva(
            indice = planchas.size + 1,
            numeroPlancha = planchas.size + 1,
            anchoPlanchaMm = anchoPlanchaMm,
            altoPlanchaMm = altoPlanchaMm,
            anchoUtil = anchoUtil,
            altoUtil = altoUtil,
            margenPerimetralMm = margenPerimetralMm
        )
        plancha.rectangulosLibres.clear()
        var cursorY = margenPerimetralMm
        combinacion.forEach { fila ->
            var cursorX = margenPerimetralMm
            fila.piezas.forEach { piezaFila ->
                repeat(piezaFila.cantidad) {
                    val indicePieza = piezasDisponibles.indexOfFirst {
                        it.descripcion == piezaFila.descripcion &&
                            it.anchoMm == piezaFila.anchoMm &&
                            it.altoMm == piezaFila.altoMm
                    }
                    if (indicePieza == -1) return@repeat
                    val piezaReal = piezasDisponibles.removeAt(indicePieza)
                    plancha.cortes += CorteVidrioUbicado(
                        piezaId = piezaReal.id,
                        descripcion = piezaReal.descripcion,
                        xMm = cursorX,
                        yMm = cursorY,
                        anchoMm = piezaFila.anchoMm,
                        altoMm = piezaFila.altoMm,
                        rotada = false
                    )
                    val clave = Triple(piezaFila.descripcion, piezaFila.anchoMm, piezaFila.altoMm)
                    restantes[clave] = (restantes[clave] ?: 0) - 1
                    cursorX += piezaFila.anchoMm + separacionCorteMm
                }
            }
            cursorY += fila.altoFilaMm + separacionCorteMm
        }
        planchas += plancha
    }

    piezasNoUbicadas += piezasDisponibles
    if (planchas.isEmpty()) return null
    val resultado = construirResultado(planchas, piezasNoUbicadas)
    return ResultadoInternoOptimizacion(
        resultado = resultado,
        score = calcularScoreResultado(resultado, planchas, objetivo)
    )
}

private fun resolverMinimoRetazo(
    piezasExpandidas: List<PiezaExpandidaVidrio>,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    superficiesIniciales: List<SuperficieDisponibleCorteVidrio>,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    separacionCorteMm: Int,
    margenPerimetralMm: Int,
    intensidad: IntensidadOptimizacionCorteVidrio
): ResultadoInternoOptimizacion? {
    val tiposIniciales = piezasExpandidas
        .groupBy { Triple(it.descripcion, it.anchoMm, it.altoMm) }
        .map { (clave, grupo) ->
            TipoPiezaPatron(
                descripcion = clave.first,
                anchoMm = clave.second,
                altoMm = clave.third,
                rotacionPermitida = grupo.any { it.rotacionPermitida },
                cantidadDisponible = grupo.size
            )
        }
        .sortedByDescending { it.anchoMm * it.altoMm }
        .toMutableList()
    if (tiposIniciales.isEmpty()) return null

    val anchoUtilPlancha = anchoPlanchaMm - (margenPerimetralMm * 2)
    val altoUtilPlancha = altoPlanchaMm - (margenPerimetralMm * 2)
    if (anchoUtilPlancha <= 0 || altoUtilPlancha <= 0) return null

    val beamHojas = when (intensidad) {
        IntensidadOptimizacionCorteVidrio.RAPIDO -> 6
        IntensidadOptimizacionCorteVidrio.NORMAL -> 12
        IntensidadOptimizacionCorteVidrio.PROFUNDO -> 20
    }
    val maxFilasPorHoja = when (intensidad) {
        IntensidadOptimizacionCorteVidrio.RAPIDO -> 4
        IntensidadOptimizacionCorteVidrio.NORMAL -> 5
        IntensidadOptimizacionCorteVidrio.PROFUNDO -> 6
    }

    val planchasResultado = mutableListOf<PlanchaTrabajo>()
    val piezasDisponibles = piezasExpandidas.toMutableList()

    ordenarSuperficiesPorMejorAjuste(superficiesIniciales, piezasDisponibles)
        .forEachIndexed { index, superficie ->
            val tiposSuperficie = tiposIniciales
                .filter { it.cantidadDisponible > 0 }
                .toMutableList()
            val hoja = buscarMejorHojaExacta(
                tipos = tiposSuperficie,
                anchoObjetivoMm = superficie.anchoMm,
                altoObjetivoMm = superficie.altoMm,
                objetivo = objetivo,
                separacionCorteMm = separacionCorteMm,
                beamFilas = beamHojas,
                maxFilas = maxFilasPorHoja
            ) ?: return@forEachIndexed
            if (hoja.filas.isEmpty()) return@forEachIndexed
            val plancha = PlanchaTrabajo(
                indice = index + 1,
                nombreSuperficie = superficie.nombre,
                esRetazoEntrada = superficie.esRetazo,
                anchoPlanchaMm = superficie.anchoMm,
                altoPlanchaMm = superficie.altoMm,
                rectangulosLibres = mutableListOf(),
                cortes = mutableListOf()
            )
            materializarHojaEnPlancha(plancha, hoja, piezasDisponibles, tiposIniciales, separacionCorteMm, 0, 0)
            if (plancha.cortes.isNotEmpty()) {
                planchasResultado += plancha
            }
        }

    var numeroPlancha = planchasResultado.count { !it.esRetazoEntrada } + 1
    while (tiposIniciales.any { it.cantidadDisponible > 0 }) {
        val hoja = buscarMejorHojaExacta(
            tipos = tiposIniciales.filter { it.cantidadDisponible > 0 }.toMutableList(),
            anchoObjetivoMm = anchoUtilPlancha,
            altoObjetivoMm = altoUtilPlancha,
            objetivo = objetivo,
            separacionCorteMm = separacionCorteMm,
            beamFilas = beamHojas,
            maxFilas = maxFilasPorHoja
        ) ?: break
        if (hoja.filas.isEmpty()) break
        val plancha = PlanchaTrabajo(
            indice = planchasResultado.size + 1,
            nombreSuperficie = "Plancha $numeroPlancha",
            esRetazoEntrada = false,
            anchoPlanchaMm = anchoPlanchaMm,
            altoPlanchaMm = altoPlanchaMm,
            rectangulosLibres = mutableListOf(),
            cortes = mutableListOf()
        )
        materializarHojaEnPlancha(
            plancha = plancha,
            hoja = hoja,
            piezasDisponibles = piezasDisponibles,
            tipos = tiposIniciales,
            separacionCorteMm = separacionCorteMm,
            offsetX = margenPerimetralMm,
            offsetY = margenPerimetralMm
        )
        if (plancha.cortes.isEmpty()) break
        planchasResultado += plancha
        numeroPlancha++
    }

    val resultado = construirResultado(planchasResultado, piezasDisponibles)
    return ResultadoInternoOptimizacion(
        resultado = resultado,
        score = calcularScoreResultado(resultado, planchasResultado, objetivo)
    )
}

private fun buscarMejorHojaExacta(
    tipos: MutableList<TipoPiezaPatron>,
    anchoObjetivoMm: Int,
    altoObjetivoMm: Int,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    separacionCorteMm: Int,
    beamFilas: Int,
    maxFilas: Int
): HojaPatronExacta? {
    val filasCandidatas = generarFilasPatron(tipos, anchoObjetivoMm, separacionCorteMm)
        .sortedBy { fila -> (anchoObjetivoMm - fila.anchoUsadoMm).coerceAtLeast(0) }
        .take(beamFilas * 8)
    if (filasCandidatas.isEmpty()) return null

    var mejores = mutableListOf(HojaPatronExacta(emptyList(), 0L, 0))
    var mejorFinal: HojaPatronExacta? = null

    repeat(maxFilas) {
        val siguientes = mutableListOf<HojaPatronExacta>()
        mejores.forEach { hoja ->
            val usadas = consumirConteoHoja(hoja)
            filasCandidatas.forEach { fila ->
                if (!filaCabeEnHoja(fila, hoja, altoObjetivoMm, separacionCorteMm)) return@forEach
                if (!filaDisponible(fila, tipos, usadas)) return@forEach
                val altoNuevo = if (hoja.filas.isEmpty()) {
                    fila.altoFilaMm
                } else {
                    hoja.altoUsado + separacionCorteMm + fila.altoFilaMm
                }
                val areaNueva = hoja.areaUsada + areaFila(fila, separacionCorteMm)
                val nuevaHoja = HojaPatronExacta(
                    filas = hoja.filas + fila,
                    areaUsada = areaNueva,
                    altoUsado = altoNuevo
                )
                siguientes += nuevaHoja
                if (mejorFinal == null || scoreHoja(nuevaHoja, anchoObjetivoMm, altoObjetivoMm, objetivo) < scoreHoja(mejorFinal!!, anchoObjetivoMm, altoObjetivoMm, objetivo)) {
                    mejorFinal = nuevaHoja
                }
            }
        }
        if (siguientes.isEmpty()) return@repeat
        mejores = siguientes
            .sortedBy { scoreHoja(it, anchoObjetivoMm, altoObjetivoMm, objetivo) }
            .take(beamFilas)
            .toMutableList()
    }

    return mejorFinal
}

private fun filaCabeEnHoja(
    fila: FilaPatron,
    hoja: HojaPatronExacta,
    altoObjetivoMm: Int,
    separacionCorteMm: Int
): Boolean {
    val altoNuevo = if (hoja.filas.isEmpty()) {
        fila.altoFilaMm
    } else {
        hoja.altoUsado + separacionCorteMm + fila.altoFilaMm
    }
    return altoNuevo <= altoObjetivoMm
}

private fun filaDisponible(
    fila: FilaPatron,
    tipos: List<TipoPiezaPatron>,
    usadas: Map<Triple<String, Int, Int>, Int>
): Boolean {
    return fila.piezas.all { pieza ->
        val clave = Triple(pieza.descripcion, pieza.anchoMm, pieza.altoMm)
        val disponible = tipos.firstOrNull {
            it.descripcion == pieza.descripcion &&
                it.anchoMm == pieza.anchoMm &&
                it.altoMm == pieza.altoMm
        }?.cantidadDisponible ?: 0
        (usadas[clave] ?: 0) + pieza.cantidad <= disponible
    }
}

private fun consumirConteoHoja(hoja: HojaPatronExacta): Map<Triple<String, Int, Int>, Int> {
    val usadas = mutableMapOf<Triple<String, Int, Int>, Int>()
    hoja.filas.forEach { fila ->
        fila.piezas.forEach { pieza ->
            val clave = Triple(pieza.descripcion, pieza.anchoMm, pieza.altoMm)
            usadas[clave] = (usadas[clave] ?: 0) + pieza.cantidad
        }
    }
    return usadas
}

private fun areaFila(fila: FilaPatron, separacionCorteMm: Int): Long {
    val anchoReal = fila.anchoUsadoMm + if (fila.piezas.size > 1) separacionCorteMm * (fila.piezas.size - 1) else 0
    return anchoReal.toLong() * fila.altoFilaMm.toLong()
}

private fun scoreHoja(
    hoja: HojaPatronExacta,
    anchoObjetivoMm: Int,
    altoObjetivoMm: Int,
    objetivo: ObjetivoOptimizacionCorteVidrio
): Double {
    if (hoja.filas.isEmpty()) return Double.MAX_VALUE
    val areaObjetivo = anchoObjetivoMm.toLong() * altoObjetivoMm.toLong()
    val desperdicio = (areaObjetivo - hoja.areaUsada).coerceAtLeast(0L)
    val retazoVertical = (altoObjetivoMm - hoja.altoUsado).coerceAtLeast(0)
    val retazoHorizontal = hoja.filas.sumOf { (anchoObjetivoMm - it.anchoUsadoMm).coerceAtLeast(0) }
    val piezas = hoja.filas.sumOf { fila -> fila.piezas.sumOf { it.cantidad } }
    return when (objetivo) {
        ObjetivoOptimizacionCorteVidrio.MENOS_PLANCHAS ->
            desperdicio.toDouble() + (retazoVertical * 3.0) + (retazoHorizontal * 1.5) - (piezas * 55.0)
        ObjetivoOptimizacionCorteVidrio.MENOS_DESPERDICIO ->
            desperdicio.toDouble() + (retazoVertical * 8.0) + (retazoHorizontal * 5.0) - (piezas * 25.0)
    }
}

private fun materializarHojaEnPlancha(
    plancha: PlanchaTrabajo,
    hoja: HojaPatronExacta,
    piezasDisponibles: MutableList<PiezaExpandidaVidrio>,
    tipos: MutableList<TipoPiezaPatron>,
    separacionCorteMm: Int,
    offsetX: Int,
    offsetY: Int
) {
    var cursorY = offsetY
    hoja.filas.forEach { fila ->
        var cursorX = offsetX
        fila.piezas.forEach { piezaFila ->
            repeat(piezaFila.cantidad) {
                val indicePieza = piezasDisponibles.indexOfFirst {
                    it.descripcion == piezaFila.descripcion &&
                        it.anchoMm == piezaFila.anchoMm &&
                        it.altoMm == piezaFila.altoMm
                }
                if (indicePieza == -1) return@repeat
                val piezaReal = piezasDisponibles.removeAt(indicePieza)
                plancha.cortes += CorteVidrioUbicado(
                    piezaId = piezaReal.id,
                    descripcion = piezaReal.descripcion,
                    xMm = cursorX,
                    yMm = cursorY,
                    anchoMm = piezaFila.anchoMm,
                    altoMm = piezaFila.altoMm,
                    rotada = false
                )
                val indiceTipo = tipos.indexOfFirst {
                    it.descripcion == piezaFila.descripcion &&
                        it.anchoMm == piezaFila.anchoMm &&
                        it.altoMm == piezaFila.altoMm
                }
                if (indiceTipo >= 0) {
                    val tipo = tipos[indiceTipo]
                    tipos[indiceTipo] = tipo.copy(cantidadDisponible = (tipo.cantidadDisponible - 1).coerceAtLeast(0))
                }
                cursorX += piezaFila.anchoMm + separacionCorteMm
            }
        }
        cursorY += fila.altoFilaMm + separacionCorteMm
    }
}

private fun generarFilasPatron(
    tipos: List<TipoPiezaPatron>,
    anchoObjetivoMm: Int,
    separacionCorteMm: Int
): List<FilaPatron> {
    val filas = mutableListOf<FilaPatron>()

    fun backtrack(
        indiceInicio: Int,
        piezasActuales: MutableList<PiezaPatronElegida>,
        anchoUsado: Int,
        altoFila: Int
    ) {
        if (piezasActuales.isNotEmpty()) {
            filas += FilaPatron(
                piezas = piezasActuales.toList(),
                anchoUsadoMm = anchoUsado,
                altoFilaMm = altoFila
            )
        }
        if (piezasActuales.size >= 4) return

        for (i in indiceInicio until tipos.size) {
            val tipo = tipos[i]
            val anchoNuevo = if (piezasActuales.isEmpty()) {
                tipo.anchoMm
            } else {
                anchoUsado + separacionCorteMm + tipo.anchoMm
            }
            if (anchoNuevo > anchoObjetivoMm) continue
            val existente = piezasActuales.indexOfFirst {
                it.descripcion == tipo.descripcion &&
                    it.anchoMm == tipo.anchoMm &&
                    it.altoMm == tipo.altoMm
            }
            if (existente >= 0) {
                val actual = piezasActuales[existente]
                if (actual.cantidad >= tipo.cantidadDisponible) continue
                piezasActuales[existente] = actual.copy(cantidad = actual.cantidad + 1)
                backtrack(i, piezasActuales, anchoNuevo, max(altoFila, tipo.altoMm))
                piezasActuales[existente] = actual
            } else {
                piezasActuales += PiezaPatronElegida(tipo.descripcion, tipo.anchoMm, tipo.altoMm, 1)
                backtrack(i, piezasActuales, anchoNuevo, max(altoFila, tipo.altoMm))
                piezasActuales.removeAt(piezasActuales.lastIndex)
            }
        }
    }

    backtrack(
        indiceInicio = 0,
        piezasActuales = mutableListOf(),
        anchoUsado = 0,
        altoFila = 0
    )

    return filas
        .distinctBy { fila ->
            fila.piezas.joinToString("|") { "${it.descripcion}:${it.anchoMm}:${it.altoMm}:${it.cantidad}" }
        }
        .filter { it.anchoUsadoMm >= (anchoObjetivoMm * 0.72f).toInt() }
}

private fun elegirCombinacionFilas(
    filas: List<FilaPatron>,
    restantes: Map<Triple<String, Int, Int>, Int>,
    altoObjetivoMm: Int,
    separacionCorteMm: Int,
    anchoObjetivoMm: Int,
    anclasAncho: List<Double>,
    anclasAlto: List<Double>
): List<FilaPatron> {
    var mejor: List<FilaPatron> = emptyList()
    var mejorScore = Double.MAX_VALUE

    fun puedeUsarFila(fila: FilaPatron, usados: Map<Triple<String, Int, Int>, Int>): Boolean {
        return fila.piezas.all { pieza ->
            val clave = Triple(pieza.descripcion, pieza.anchoMm, pieza.altoMm)
            (usados[clave] ?: 0) + pieza.cantidad <= (restantes[clave] ?: 0)
        }
    }

    fun backtrack(
        indiceInicio: Int,
        actuales: MutableList<FilaPatron>,
        usados: MutableMap<Triple<String, Int, Int>, Int>,
        altoUsado: Int
    ) {
        if (actuales.isNotEmpty()) {
            val score = scoreCombinacionFilas(
                filas = actuales,
                anchoObjetivoMm = anchoObjetivoMm,
                altoObjetivoMm = altoObjetivoMm,
                anclasAncho = anclasAncho,
                anclasAlto = anclasAlto,
                altoUsado = altoUsado
            )
            if (score >= 0 && score < mejorScore) {
                mejor = actuales.toList()
                mejorScore = score
            }
        }
        if (actuales.size >= 4) return

        for (i in indiceInicio until filas.size) {
            val fila = filas[i]
            val altoNuevo = if (actuales.isEmpty()) {
                fila.altoFilaMm
            } else {
                altoUsado + separacionCorteMm + fila.altoFilaMm
            }
            if (altoNuevo > altoObjetivoMm) continue
            if (!puedeUsarFila(fila, usados)) continue
            fila.piezas.forEach { pieza ->
                val clave = Triple(pieza.descripcion, pieza.anchoMm, pieza.altoMm)
                usados[clave] = (usados[clave] ?: 0) + pieza.cantidad
            }
            actuales += fila
            backtrack(i, actuales, usados, altoNuevo)
            actuales.removeAt(actuales.lastIndex)
            fila.piezas.forEach { pieza ->
                val clave = Triple(pieza.descripcion, pieza.anchoMm, pieza.altoMm)
                val nuevo = (usados[clave] ?: 0) - pieza.cantidad
                if (nuevo <= 0) usados.remove(clave) else usados[clave] = nuevo
            }
        }
    }

    backtrack(0, mutableListOf(), mutableMapOf(), 0)
    return mejor
}

private fun anclasDivision(valor: Int): List<Double> {
    return (1..9).map { divisor -> valor.toDouble() / divisor.toDouble() }
}

private fun distanciaMinimaAAnclas(valor: Int, anclas: List<Double>): Double {
    if (valor <= 0) return 0.0
    return anclas.minOfOrNull { abs(valor.toDouble() - it) } ?: 0.0
}

private fun scoreFilaPatron(
    fila: FilaPatron,
    anchoObjetivoMm: Int,
    anclasAncho: List<Double>,
    anclasAlto: List<Double>
): Double {
    val retazoHorizontal = (anchoObjetivoMm - fila.anchoUsadoMm).coerceAtLeast(0)
    val uso = retazoHorizontal.toDouble() * 5.0
    val afinidadRetazo = distanciaMinimaAAnclas(retazoHorizontal, anclasAncho)
    val afinidadAlto = distanciaMinimaAAnclas(fila.altoFilaMm, anclasAlto)
    val afinidadPiezas = fila.piezas.sumOf { pieza ->
        min(
            distanciaMinimaAAnclas(pieza.anchoMm, anclasAncho),
            distanciaMinimaAAnclas(pieza.altoMm, anclasAlto)
        ) * pieza.cantidad
    }
    val cantidadPiezas = fila.piezas.sumOf { it.cantidad }
    return uso + (afinidadRetazo * 0.8) + (afinidadAlto * 0.6) + (afinidadPiezas * 0.15) - (cantidadPiezas * 30.0)
}

private fun scoreCombinacionFilas(
    filas: List<FilaPatron>,
    anchoObjetivoMm: Int,
    altoObjetivoMm: Int,
    anclasAncho: List<Double>,
    anclasAlto: List<Double>,
    altoUsado: Int
): Double {
    val retazoVertical = (altoObjetivoMm - altoUsado).coerceAtLeast(0)
    val desperdicioHorizontal = filas.sumOf { (anchoObjetivoMm - it.anchoUsadoMm).coerceAtLeast(0) }
    val afinidadVertical = distanciaMinimaAAnclas(retazoVertical, anclasAlto)
    val afinidadHorizontal = filas.sumOf { distanciaMinimaAAnclas((anchoObjetivoMm - it.anchoUsadoMm).coerceAtLeast(0), anclasAncho) }
    val piezas = filas.sumOf { fila -> fila.piezas.sumOf { it.cantidad } }
    return (retazoVertical * 12.0) +
        (desperdicioHorizontal * 4.0) +
        (afinidadVertical * 1.1) +
        (afinidadHorizontal * 0.7) -
        (piezas * 25.0)
}

```

## 2. Capa de preparacion y orquestacion

Archivo fuente original: `composeApp/src/commonMain/kotlin/logic/TrabajoOptimizacionCortes.kt`

```kotlin
package logic

import kotlin.math.roundToInt
import models.Listado

data class AnalisisListadoOptimizacionCortes(
    val piezas: List<PiezaCorteVidrio>,
    val descartes: List<ItemDescartadoOptimizacionCortes>
)

data class ItemDescartadoOptimizacionCortes(
    val nombre: String,
    val motivo: String
)

data class PlanchaBaseDetectadaOptimizacionCortes(
    val anchoCm: String,
    val altoCm: String,
    val areaCm2: Float,
    val repeticiones: Int
)

data class TrabajoPreparadoOptimizacionCortes(
    val analisis: AnalisisListadoOptimizacionCortes,
    val retazosDisponibles: List<SuperficieDisponibleCorteVidrio>,
    val planchasBaseDetectadas: List<PlanchaBaseDetectadaOptimizacionCortes>
)

fun prepararTrabajoOptimizacionCortes(items: List<Listado>): TrabajoPreparadoOptimizacionCortes {
    return TrabajoPreparadoOptimizacionCortes(
        analisis = analizarListadoParaOptimizacionCortes(items),
        retazosDisponibles = extraerRetazosDisponiblesParaOptimizacion(items),
        planchasBaseDetectadas = detectarPlanchasBaseParaOptimizacion(items)
    )
}

fun analizarListadoParaOptimizacionCortes(items: List<Listado>): AnalisisListadoOptimizacionCortes {
    val piezas = mutableListOf<PiezaCorteVidrio>()
    val descartes = mutableListOf<ItemDescartadoOptimizacionCortes>()

    items.forEachIndexed { indice, item ->
        val nombre = item.producto.ifBlank { "Item ${indice + 1}" }
        if (!item.tipoProducto.equals("VIDRIO", ignoreCase = true)) {
            descartes += ItemDescartadoOptimizacionCortes(
                nombre = nombre,
                motivo = "No es un producto de vidrio."
            )
            return@forEachIndexed
        }
        val modalidad = item.modalidadVenta.trim().uppercase()
        if (modalidad.isBlank() || modalidad == "ESTANDAR" || modalidad == "PLANCHA" || modalidad == "CAJA") {
            descartes += ItemDescartadoOptimizacionCortes(
                nombre = nombre,
                motivo = "No representa una pieza de corte rectangular."
            )
            return@forEachIndexed
        }

        val anchoMm = convertirUnidadAMm(item.medi1, item.uni)
        val altoMm = convertirUnidadAMm(item.medi2, item.uni)
        val cantidad = item.canti.aCantidadEntera()

        if (anchoMm <= 0 || altoMm <= 0) {
            descartes += ItemDescartadoOptimizacionCortes(
                nombre = nombre,
                motivo = "No tiene medidas validas para corte."
            )
            return@forEachIndexed
        }
        if (cantidad <= 0) {
            descartes += ItemDescartadoOptimizacionCortes(
                nombre = nombre,
                motivo = "La cantidad es invalida."
            )
            return@forEachIndexed
        }

        piezas += PiezaCorteVidrio(
            id = "pieza-${indice + 1}",
            descripcion = nombre,
            anchoMm = anchoMm,
            altoMm = altoMm,
            cantidad = cantidad,
            rotacionPermitida = true
        )
    }

    return AnalisisListadoOptimizacionCortes(
        piezas = piezas,
        descartes = descartes
    )
}

fun extraerRetazosDisponiblesParaOptimizacion(items: List<Listado>): List<SuperficieDisponibleCorteVidrio> {
    val productosUnicos = items
        .filter { it.tipoProducto.equals("VIDRIO", ignoreCase = true) }
        .distinctBy { "${it.productoId}|${it.producto}|${it.retazosDetalleProducto}" }

    val superficies = mutableListOf<SuperficieDisponibleCorteVidrio>()
    productosUnicos.forEach { item ->
        val retazos = parsearDetalleRetazosActual(item.retazosDetalleProducto)
        retazos.forEachIndexed { indice, retazo ->
            repeat(retazo.cantidad) { nro ->
                superficies += SuperficieDisponibleCorteVidrio(
                    nombre = "Retazo ${item.producto.ifBlank { item.productoId?.toString() ?: "" }} ${indice + 1}.${nro + 1}",
                    anchoMm = (retazo.anchoCm * 10f).roundToInt(),
                    altoMm = (retazo.altoCm * 10f).roundToInt(),
                    esRetazo = true
                )
            }
        }
    }
    return superficies.sortedBy { it.anchoMm * it.altoMm }
}

fun detectarPlanchasBaseParaOptimizacion(items: List<Listado>): List<PlanchaBaseDetectadaOptimizacionCortes> {
    return items
        .filter { it.tipoProducto.equals("VIDRIO", ignoreCase = true) }
        .flatMap { item ->
            val directas = buildList {
                val anchoCm = normalizarDimensionPlanchaACm(item.anchoPlanchaProducto)
                val altoCm = normalizarDimensionPlanchaACm(item.altoPlanchaProducto)
                if (anchoCm > 0f && altoCm > 0f) {
                    add(Pair(anchoCm, altoCm))
                }
            }
            val candidatas = parsearPlanchasCandidatasParaOptimizacion(item.planchasCandidatasProducto)
            (directas + candidatas).distinct()
        }
        .groupingBy { it }
        .eachCount()
        .map { (medida, repeticiones) ->
            PlanchaBaseDetectadaOptimizacionCortes(
                anchoCm = formatCmOptimizacion(medida.first),
                altoCm = formatCmOptimizacion(medida.second),
                areaCm2 = medida.first * medida.second,
                repeticiones = repeticiones
            )
        }
        .sortedWith(
            compareByDescending<PlanchaBaseDetectadaOptimizacionCortes> { it.repeticiones }
                .thenByDescending { it.areaCm2 }
        )
}

fun optimizarCortesPorMaterial(
    piezas: List<PiezaCorteVidrio>,
    anchoPlanchaMm: Int,
    altoPlanchaMm: Int,
    superficiesIniciales: List<SuperficieDisponibleCorteVidrio>,
    objetivo: ObjetivoOptimizacionCorteVidrio,
    intensidad: IntensidadOptimizacionCorteVidrio,
    separacionCorteMm: Int,
    margenPerimetralMm: Int,
    obtenerMaterialPieza: (PiezaCorteVidrio) -> String = { materialDesdeDescripcionOptimizacion(it.descripcion) },
    obtenerMaterialSuperficie: (SuperficieDisponibleCorteVidrio) -> String = { materialDesdeNombreRetazoOptimizacion(it.nombre) },
    renombrarPlancha: (material: String, plancha: PlanchaCorteVidrio, indiceGlobal: Int) -> String = { material, plancha, _ ->
        construirNombrePlanchaPorMaterialOptimizacion(material, plancha)
    }
): ResultadoOptimizacionVidrio {
    if (piezas.isEmpty()) {
        return ResultadoOptimizacionVidrio(emptyList(), emptyList(), 0, 0)
    }

    val retazosPorMaterial = superficiesIniciales.groupBy { obtenerMaterialSuperficie(it) }
    val gruposPiezas = piezas.groupBy { obtenerMaterialPieza(it) }
        .toList()
        .sortedBy { it.first.lowercase() }

    val planchas = mutableListOf<PlanchaCorteVidrio>()
    val piezasSinUbicar = mutableListOf<PiezaCorteVidrio>()
    var areaUsada = 0L
    var areaDesperdicio = 0L
    var indicePlanchaGlobal = 1

    gruposPiezas.forEach { (material, piezasMaterial) ->
        val resultadoMaterial = optimizarCortesVidrio(
            piezas = piezasMaterial,
            anchoPlanchaMm = anchoPlanchaMm,
            altoPlanchaMm = altoPlanchaMm,
            superficiesIniciales = retazosPorMaterial[material].orEmpty(),
            objetivo = objetivo,
            intensidad = intensidad,
            separacionCorteMm = separacionCorteMm,
            margenPerimetralMm = margenPerimetralMm
        )

        planchas += resultadoMaterial.planchas.map { plancha ->
            val indiceGlobal = indicePlanchaGlobal++
            plancha.copy(
                indice = indiceGlobal,
                nombreSuperficie = renombrarPlancha(material, plancha, indiceGlobal)
            )
        }
        piezasSinUbicar += resultadoMaterial.piezasSinUbicar
        areaUsada += resultadoMaterial.areaUsadaMm2
        areaDesperdicio += resultadoMaterial.areaDesperdicioMm2
    }

    return ResultadoOptimizacionVidrio(
        planchas = planchas,
        piezasSinUbicar = piezasSinUbicar,
        areaUsadaMm2 = areaUsada,
        areaDesperdicioMm2 = areaDesperdicio
    )
}

fun materialDesdeDescripcionOptimizacion(descripcion: String): String {
    return descripcion.split("|")
        .firstOrNull()
        ?.trim()
        ?.ifBlank { "General" }
        ?: "General"
}

fun materialDesdeNombreRetazoOptimizacion(nombre: String): String {
    return limpiarTituloRetazoOptimizacion(nombre)
        .ifBlank { materialDesdeDescripcionOptimizacion(nombre.removePrefix("Retazo ").trim()) }
}

fun construirNombrePlanchaPorMaterialOptimizacion(
    material: String,
    plancha: PlanchaCorteVidrio
): String {
    val base = if (plancha.esRetazoEntrada) {
        plancha.nombreSuperficie.ifBlank { "Retazo" }
    } else {
        "Plancha ${plancha.indice}"
    }
    return if (material.isBlank()) {
        base
    } else {
        "$material - $base"
    }
}

private data class RetazoDetalleOptimizacion(
    val anchoCm: Float,
    val altoCm: Float,
    val cantidad: Int
)

private fun parsearPlanchasCandidatasParaOptimizacion(valor: String): List<Pair<Float, Float>> {
    if (valor.isBlank()) return emptyList()
    return valor.split("|").mapNotNull { bloque ->
        val partes = bloque.split("x")
        if (partes.size != 2) return@mapNotNull null
        val ancho = normalizarDimensionPlanchaACm(partes[0].trim().replace(",", ".").toFloatOrNull() ?: 0f)
        val alto = normalizarDimensionPlanchaACm(partes[1].trim().replace(",", ".").toFloatOrNull() ?: 0f)
        if (ancho <= 0f || alto <= 0f) null else Pair(ancho, alto)
    }
}

private fun parsearDetalleRetazosActual(detalle: String): List<RetazoDetalleOptimizacion> {
    if (detalle.isBlank()) return emptyList()
    val patron = Regex("""(\d+(?:[.,]\d+)?)\s*[xX*]\s*(\d+(?:[.,]\d+)?)\s*=\s*(\d+(?:[.,]\d+)?)""")
    return detalle
        .split(",", "\n")
        .mapNotNull { bloque ->
            val coincidencia = patron.find(bloque.trim()) ?: return@mapNotNull null
            val ancho = coincidencia.groupValues[1].replace(",", ".").toFloatOrNull() ?: return@mapNotNull null
            val alto = coincidencia.groupValues[2].replace(",", ".").toFloatOrNull() ?: return@mapNotNull null
            val cantidad = coincidencia.groupValues[3].replace(",", ".").toFloatOrNull()?.roundToInt() ?: return@mapNotNull null
            if (ancho <= 0f || alto <= 0f || cantidad <= 0) return@mapNotNull null
            RetazoDetalleOptimizacion(anchoCm = ancho, altoCm = alto, cantidad = cantidad)
        }
}

private fun limpiarTituloRetazoOptimizacion(nombre: String): String {
    val sinPrefijo = nombre.removePrefix("Retazo ").trim()
    return sinPrefijo.replace(Regex("""\s+\d+\.\d+$"""), "").trim()
}

private fun normalizarDimensionPlanchaACm(valor: Float): Float {
    if (valor <= 0f) return 0f
    return if (valor <= 10f) valor * 100f else valor
}

private fun formatCmOptimizacion(valor: Float): String {
    val redondeado = ((valor * 10f).roundToInt()) / 10f
    return if (redondeado % 1f == 0f) {
        redondeado.toInt().toString()
    } else {
        redondeado.toString()
    }
}

private fun convertirUnidadAMm(valor: Float, unidad: String): Int {
    val factor = when (unidad.trim().lowercase()) {
        "milimetros" -> 1f
        "metros" -> 1000f
        "pulgadas" -> 25.4f
        else -> 10f
    }
    return (valor * factor).toInt()
}

private fun Float.aCantidadEntera(): Int {
    return if (this <= 0f) 0 else this.toInt().coerceAtLeast(1)
}

```

## 3. Modelos de documento imprimible

Archivo fuente original: `composeApp/src/commonMain/kotlin/platform/ServicioDocumentoPlanoCorte.kt`

```kotlin
package platform

import androidx.compose.runtime.Composable

data class PiezaPlanoCorteImprimible(
    val descripcion: String,
    val anchoMm: Int,
    val altoMm: Int,
    val xMm: Int,
    val yMm: Int,
    val rotada: Boolean
)

data class PlanchaPlanoCorteImprimible(
    val nombre: String,
    val anchoMm: Int,
    val altoMm: Int,
    val esRetazo: Boolean,
    val areaUsadaMm2: Long,
    val areaDesperdicioMm2: Long,
    val piezas: List<PiezaPlanoCorteImprimible>
)

data class DocumentoPlanoCorteImprimible(
    val titulo: String,
    val negocioNombre: String,
    val subtitulo: String,
    val logoUri: String,
    val negocioDetalle: String,
    val cliente: String,
    val fecha: String,
    val resumen: String,
    val observaciones: String,
    val planchas: List<PlanchaPlanoCorteImprimible>
)

interface ServicioDocumentoPlanoCorte {
    fun compartirPdfA4(documento: DocumentoPlanoCorteImprimible): Boolean
    fun imprimirA4(documento: DocumentoPlanoCorteImprimible): Boolean
}

@Composable
expect fun recordarServicioDocumentoPlanoCorte(): ServicioDocumentoPlanoCorte

```

## 4. Fabrica del documento imprimible

Archivo fuente original: `composeApp/src/commonMain/kotlin/logic/FabricaDocumentosPlanoCorte.kt`

```kotlin
package logic

import database.AppConfigEntity
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.DocumentoPlanoCorteImprimible
import platform.PiezaPlanoCorteImprimible
import platform.PlanchaPlanoCorteImprimible

fun construirDocumentoPlanoCorte(
    resultado: ResultadoOptimizacionVidrio,
    config: AppConfigEntity? = null,
    cliente: String = "",
    observaciones: String = ""
): DocumentoPlanoCorteImprimible {
    val fecha = try {
        val dt = Instant.fromEpochMilliseconds(System.currentTimeMillis())
            .toLocalDateTime(TimeZone.currentSystemDefault())
        "${dt.dayOfMonth}/${dt.monthNumber}/${dt.year} ${dt.hour.toString().padStart(2, '0')}:${dt.minute.toString().padStart(2, '0')}"
    } catch (_: Exception) {
        "---"
    }

    val resumen = buildString {
        append("Planchas: ${resultado.planchas.size}")
        append(" | Retazos usados: ${resultado.planchas.count { it.esRetazoEntrada && it.cortes.isNotEmpty() }}")
        append(" | Planchas completas: ${resultado.planchas.count { !it.esRetazoEntrada && it.cortes.isNotEmpty() }}")
        append(" | Desperdicio: ${formatCm2(resultado.areaDesperdicioMm2)} cm2")
    }

    return DocumentoPlanoCorteImprimible(
        titulo = "Plano de corte",
        negocioNombre = config?.negocioNombre?.ifBlank { "Puntos" } ?: "Puntos",
        subtitulo = config?.negocioSubtitulo.orEmpty(),
        logoUri = config?.pdfLogoUri.orEmpty(),
        negocioDetalle = buildBusinessDetail(config),
        cliente = cliente.ifBlank { "Publico general" },
        fecha = fecha,
        resumen = resumen,
        observaciones = observaciones,
        planchas = resultado.planchas.map { plancha ->
            PlanchaPlanoCorteImprimible(
                nombre = plancha.nombreSuperficie.ifBlank { "Plancha ${plancha.indice}" },
                anchoMm = plancha.anchoMm,
                altoMm = plancha.altoMm,
                esRetazo = plancha.esRetazoEntrada,
                areaUsadaMm2 = plancha.areaUsadaMm2,
                areaDesperdicioMm2 = plancha.areaDesperdicioMm2,
                piezas = plancha.cortes.map { corte ->
                    PiezaPlanoCorteImprimible(
                        descripcion = corte.descripcion,
                        anchoMm = corte.anchoMm,
                        altoMm = corte.altoMm,
                        xMm = corte.xMm,
                        yMm = corte.yMm,
                        rotada = corte.rotada
                    )
                }
            )
        }
    )
}

private fun buildBusinessDetail(config: AppConfigEntity?): String {
    val partes = listOf(
        config?.negocioDireccion.orEmpty(),
        config?.negocioTelefono.orEmpty()
    ).filter { it.isNotBlank() }
    return partes.joinToString(" | ")
}

private fun formatCm2(valorMm2: Long): String {
    val centimetros2 = valorMm2 / 100.0
    val redondeado = kotlin.math.round(centimetros2 * 100.0) / 100.0
    return if (redondeado % 1.0 == 0.0) {
        redondeado.toInt().toString()
    } else {
        redondeado.toString()
    }
}

```

## 5. Contrato minimo de entrada `Listado`

Archivo fuente original: `composeApp/src/commonMain/kotlin/models/POSModels.kt`

Crystal no necesita copiar todo el archivo si ya tiene su propio modelo, pero si debe mapear al menos estos campos.

```kotlin
package models

import kotlinx.serialization.Serializable

@Serializable
data class TrabajoOptimizacionPayload(
    val cliente: String = "",
    val items: List<Listado> = emptyList()
)

@Serializable
data class TrabajoCajaPayload(
    val clienteId: Long? = null,
    val cliente: String = "",
    val items: List<Listado> = emptyList()
)

@Serializable
data class Listado(
    val productoId: Long? = null,
    val tipoProducto: String = "GENERAL",
    val retazosDetalleProducto: String = "",
    val anchoPlanchaProducto: Float = 0f,
    val altoPlanchaProducto: Float = 0f,
    val planchasCandidatasProducto: String = "",
    val escala: String,
    val uni: String,
    var medi1: Float,
    var medi2: Float,
    var medi3: Float,
    var canti: Float,
    var piescua: Float,
    var precio: Float,
    var costo: Float,
    var producto: String,
    var peri: Float,
    var metcua: Float,
    var metli: Float,
    var metcub: Float,
    var color: Int,
    var uri: String = "",
    val modalidadVenta: String = "",
    val perfilPrecio: String = "",
    val cantidadStock: Float = 0f,
    val cantidadPlanchas: Float = 0f
)

@Serializable
data class PresupuestoCompleto(
    val cliente: String,
    val fechaCreacion: String,
    val elementos: List<Listado>,
    val precioTotal: String,
    val metrosTotal: String,
    val piesTotal: String,
    val perimetroTotal: String
)

```

## 6. Contrato minimo opcional de configuracion para imprimir

Archivo fuente original: `composeApp/src/commonMain/kotlin/database/Entities.kt`

Si Crystal tambien va a generar el plano imprimible, debe proveer al menos estos campos de configuracion o un equivalente.

```kotlin
@Entity(tableName = "app_config")
@Serializable
data class AppConfigEntity(
    @PrimaryKey val id: Int = 1,
    val pdfLogoUri: String = "",
    val negocioNombre: String = "Puntos",
    val negocioSubtitulo: String = "",
    val negocioTelefono: String = "",
    val negocioDireccion: String = "",
    val boletaSerie: String = "B001",
    val boletaCorrelativoInicial: Int = 1,
    val facturaSerie: String = "F001",
    val facturaCorrelativoInicial: Int = 1,
    val empresaId: String = "",
    val patronUid: String = "",
    val deviceId: String = "",
    val deviceName: String = "",
    val deviceType: String = "",
    val deviceRole: String = "",
    val aceptaTrabajosRemotos: Boolean = false,
```

## 7. Prompt corto para Codex de Crystal

```text
Replica en Crystal la logica completa de optimizacion de cortes de planchas usando este documento como fuente unica.

Objetivo:
- portar el solver completo de corte de planchas
- portar la capa de preparacion y agrupacion por material
- adaptar el input del modelo actual de Crystal al contrato equivalente de `Listado`
- exponer una API equivalente a `optimizarCortesPorMaterial(...)`
- si Crystal necesita PDF o impresion, portar tambien la fabrica de documento y sus modelos

Restricciones:
- no copies la UI Compose
- conserva el comportamiento del solver lo mas fiel posible
- deja el modulo en una capa compartida de dominio o logic de Crystal
- documenta cualquier adaptacion de tipos que hagas
```
