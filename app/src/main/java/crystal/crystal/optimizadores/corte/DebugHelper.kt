package crystal.crystal.optimizadores.corte

import android.util.Log

object DebugHelper {

    private const val TAG = "CorteDebug"

    // ===== FUNCIONES ORIGINALES =====

    fun logPiezasRequeridas(piezas: List<PiezaCorte>) {
        Log.d(TAG, "=== PIEZAS REQUERIDAS ===")
        piezas.forEach { pieza ->
            Log.d(TAG, "Pieza: ${pieza.longitud} cm, cantidad: ${pieza.cantidad}, referencia: '${pieza.referencia}', cortada: ${pieza.cortada}")
        }
    }

    fun logVarillasUsadas(varillasUsadas: List<CorteOptimizer.VarillaConReferencias>) {
        Log.d(TAG, "=== VARILLAS USADAS CON REFERENCIAS ===")
        varillasUsadas.forEachIndexed { index, varillaConRef ->
            Log.d(TAG, "Varilla $index:")
            Log.d(TAG, "  Longitud: ${varillaConRef.varilla.longitud}")
            Log.d(TAG, "  Cortes: ${varillaConRef.varilla.cortes}")
            Log.d(TAG, "  Referencias: ${varillaConRef.referencias}")

            // Verificar que cortes y referencias tengan el mismo tamaño
            if (varillaConRef.varilla.cortes.size != varillaConRef.referencias.size) {
                Log.e(TAG, "  ERROR: Tamaños no coinciden! Cortes: ${varillaConRef.varilla.cortes.size}, Referencias: ${varillaConRef.referencias.size}")
            }
        }
    }

    fun logResultadoEstructurado(resultado: ResultadoOptimizacion) {
        Log.d(TAG, "=== RESULTADO ESTRUCTURADO ===")
        Log.d(TAG, "Total barras: ${resultado.totalBarrasUsadas}")
        Log.d(TAG, "Total cortes: ${resultado.totalCortes}")

        resultado.varillasUsadas.forEachIndexed { index, varilla ->
            Log.d(TAG, "VarillaResultado $index:")
            Log.d(TAG, "  Longitud: ${varilla.longitudVarilla}")
            Log.d(TAG, "  Cortes simples: ${varilla.cortes}")
            Log.d(TAG, "  Cortes con referencias:")
            varilla.cortesConReferencias.forEach { corte ->
                Log.d(TAG, "    ${corte.longitud} -> '${corte.referencia}'")
            }
        }
    }

    fun logCortesAgrupados(cortesAgrupados: List<String>) {
        Log.d(TAG, "=== CORTES AGRUPADOS PARA UI ===")
        cortesAgrupados.forEach { texto ->
            Log.d(TAG, "Chip: '$texto'")
        }
    }

    // ===== FUNCIONES PARA DESCUENTOS =====

    fun logCortesEjecutados(cortesEjecutados: List<VarillaCortada>) {
        Log.d(TAG, "=== CORTES EJECUTADOS ===")
        cortesEjecutados.forEachIndexed { index, varillaCortada ->
            Log.d(TAG, "Varilla cortada $index:")
            Log.d(TAG, "  Longitud varilla: ${varillaCortada.longitudVarilla}")
            varillaCortada.cortesEjecutados.forEach { corte ->
                Log.d(TAG, "    Corte: ${corte.longitud} cm, referencia '${corte.referencia}', cantidad: ${corte.cantidad}")
            }
        }
    }

    fun logDescuento(longitud: Float, referencia: String, cantidadOriginal: Int, cantidadDescontada: Int, cantidadFinal: Int) {
        Log.d(TAG, "=== DESCUENTO EXACTO ===")
        Log.d(TAG, "Pieza: ${longitud} cm, referencia '${referencia}'")
        Log.d(TAG, "Cantidad original: $cantidadOriginal")
        Log.d(TAG, "Cantidad descontada: $cantidadDescontada")
        Log.d(TAG, "Cantidad final: $cantidadFinal")
    }

    fun logDescuentoPorReferencia(longitud: Float, referencia: String, cantidadOriginal: Int, cantidadDescontada: Int, cantidadFinal: Int) {
        Log.d(TAG, "=== DESCUENTO POR REFERENCIA ===")
        Log.d(TAG, "Pieza: ${longitud} cm, referencia '${referencia}' (no coincidencia exacta)")
        Log.d(TAG, "Cantidad original: $cantidadOriginal")
        Log.d(TAG, "Cantidad descontada: $cantidadDescontada")
        Log.d(TAG, "Cantidad final: $cantidadFinal")
    }

    fun logListaDespuesDescuento(lista: List<PiezaCorte>) {
        Log.d(TAG, "=== LISTA DESPUÉS DE DESCUENTOS ===")
        lista.forEach { pieza ->
            Log.d(TAG, "Pieza: ${pieza.longitud} cm, cantidad: ${pieza.cantidad}, referencia: '${pieza.referencia}', cortada: ${pieza.cortada}")
        }
    }

    fun logInicioDescuentos() {
        Log.d(TAG, "=== INICIANDO PROCESAMIENTO DE DESCUENTOS ===")
    }

    fun logFinDescuentos(totalCortes: Int) {
        Log.d(TAG, "=== DESCUENTOS COMPLETADOS ===")
        Log.d(TAG, "Total de cortes descontados: $totalCortes")
    }

    // ===== FUNCIONES PARA OPTIMIZACIÓN INTELIGENTE (SIMPLIFICADAS) =====

    fun logInicioOptimizacionInteligente() {
        Log.d(TAG, "=== 🧠 INICIANDO OPTIMIZACIÓN INTELIGENTE ===")
        Log.d(TAG, "⏱️ Preparándose para explorar múltiples estrategias...")
    }

    fun logEstadisticasProblema(totalPiezas: Int, totalVarillas: Int) {
        Log.d(TAG, "=== 📊 ESTADÍSTICAS DEL PROBLEMA ===")
        Log.d(TAG, "Total piezas individuales: $totalPiezas")
        Log.d(TAG, "Total varillas disponibles: $totalVarillas")

        val complejidad = when {
            totalPiezas <= 10 -> "Baja"
            totalPiezas <= 30 -> "Media"
            totalPiezas <= 50 -> "Alta"
            else -> "Muy Alta"
        }
        Log.d(TAG, "Complejidad estimada: $complejidad")
    }

    fun logResultadoEstrategia(resultado: CorteOptimizer.ResultadoInterno) {
        Log.d(TAG, "=== 📈 RESULTADO ESTRATEGIA: ${resultado.algoritmoUsado} ===")
        Log.d(TAG, "Eficiencia: ${String.format("%.2f", resultado.eficiencia)}%")
        Log.d(TAG, "Total retazos: ${resultado.totalRetazos} cm")
        Log.d(TAG, "Varillas usadas: ${resultado.varillasUsadas.size}")
        Log.d(TAG, "Combinaciones exploradas: ${resultado.combinacionesExploradas}")
    }

    fun logErrorEstrategia(nombreEstrategia: String, error: String?) {
        Log.w(TAG, "⚠️ Error en estrategia $nombreEstrategia: ${error ?: "Error desconocido"}")
    }

    fun logFallbackAlgoritmoOriginal() {
        Log.w(TAG, "⚠️ Todas las estrategias fallaron, usando algoritmo original")
    }

    fun logOptimizacionCompletada(resultado: CorteOptimizer.ResultadoInterno, tiempoMs: Long) {
        Log.d(TAG, "=== 🎉 OPTIMIZACIÓN INTELIGENTE COMPLETADA ===")
        Log.d(TAG, "⏱️ Tiempo total: ${tiempoMs}ms (${tiempoMs/1000.0}s)")
        Log.d(TAG, "🏆 Algoritmo ganador: ${resultado.algoritmoUsado}")
        Log.d(TAG, "📊 Eficiencia final: ${String.format("%.2f", resultado.eficiencia)}%")
        Log.d(TAG, "♻️ Retazos totales: ${resultado.totalRetazos}cm")
        Log.d(TAG, "📦 Varillas utilizadas: ${resultado.varillasUsadas.size}")
        Log.d(TAG, "🔍 Total combinaciones exploradas: ${resultado.combinacionesExploradas}")

        val evaluacion = when {
            resultado.eficiencia >= 95f -> "🏆 EXCELENTE"
            resultado.eficiencia >= 90f -> "🥇 MUY BUENA"
            resultado.eficiencia >= 85f -> "🥈 BUENA"
            resultado.eficiencia >= 80f -> "🥉 ACEPTABLE"
            else -> "⚠️ MEJORABLE"
        }
        Log.d(TAG, "🏅 Evaluación: $evaluacion")
    }

    fun logPruebaOrdenamiento(nombre: String, eficiencia: Float) {
        Log.d(TAG, "🔄 Ordenamiento '$nombre': ${String.format("%.2f", eficiencia)}%")
    }

    fun logMejoraSolucion(estrategia: String, eficiencia: Float) {
        Log.d(TAG, "📈 MEJORA en $estrategia: ${String.format("%.2f", eficiencia)}%")
    }

    fun logProgresoBusqueda(combinaciones: Int, mejorEficiencia: Float) {
        Log.d(TAG, "🔍 Progreso: $combinaciones combinaciones exploradas, mejor: ${String.format("%.2f", mejorEficiencia)}%")
    }

    // ===== NUEVAS FUNCIONES PARA CORTES LARGOS =====

    fun logDeteccionCortesLargos(varillaMaxima: Float, cortesLargos: List<PiezaCorte>) {
        Log.d(TAG, "=== 🔍 DETECCIÓN DE CORTES LARGOS ===")
        Log.d(TAG, "Varilla máxima disponible: ${varillaMaxima}cm")

        if (cortesLargos.isNotEmpty()) {
            Log.d(TAG, "⚠️ Cortes que exceden varilla máxima:")
            cortesLargos.forEach { corte ->
                val exceso = corte.longitud - varillaMaxima
                Log.d(TAG, "   ${corte.longitud}cm (${corte.referencia}) - Exceso: ${exceso}cm - Cantidad: ${corte.cantidad}")
            }
        } else {
            Log.d(TAG, "✅ No hay cortes que excedan la varilla máxima")
        }
    }

    fun logTratamientoCorteLargo(
        longitudOriginal: Float,
        referenciaOriginal: String,
        varillaMaxima: Float,
        varillasCompletas: Int,
        resto: Float,
        referenciaEspecial: String
    ) {
        Log.d(TAG, "=== 🔧 TRATAMIENTO CORTE LARGO ===")
        Log.d(TAG, "Corte original: ${longitudOriginal}cm (${referenciaOriginal})")
        Log.d(TAG, "Varilla máxima: ${varillaMaxima}cm")
        Log.d(TAG, "División:")
        Log.d(TAG, "  → $varillasCompletas varillas completas de ${varillaMaxima}cm")
        if (resto > 0) {
            Log.d(TAG, "  → 1 resto de ${resto}cm")
        }
        Log.d(TAG, "Referencia especial asignada: '$referenciaEspecial'")
    }

    fun logResultadoCortesLargos(varillasCompletas: List<CorteOptimizer.VarillaConReferencias>, piezasAjustadas: List<PiezaCorte>) {
        Log.d(TAG, "=== 📋 RESULTADO CORTES LARGOS ===")
        Log.d(TAG, "Varillas completas generadas: ${varillasCompletas.size}")

        if (varillasCompletas.isNotEmpty()) {
            Log.d(TAG, "Lista de varillas completas:")
            varillasCompletas.forEach { varilla ->
                Log.d(TAG, "  ${varilla.varilla.longitud}cm → Referencia: ${varilla.referencias.firstOrNull() ?: "N/A"}")
            }
        }

        val piezasEspeciales = piezasAjustadas.filter { it.referencia.startsWith("CE") }
        if (piezasEspeciales.isNotEmpty()) {
            Log.d(TAG, "Restos generados:")
            piezasEspeciales.forEach { pieza ->
                Log.d(TAG, "  ${pieza.longitud}cm → Referencia: ${pieza.referencia}")
            }
        }
    }

    fun logCombinacionResultados(varillasCompletas: Int, varillasOptimizadas: Int, total: Int) {
        Log.d(TAG, "=== 🔗 COMBINACIÓN DE RESULTADOS ===")
        Log.d(TAG, "Varillas completas (cortes largos): $varillasCompletas")
        Log.d(TAG, "Varillas optimizadas (algoritmo): $varillasOptimizadas")
        Log.d(TAG, "Total varillas en resultado: $total")
    }

    fun logEjemploCorteLargo(longitudCorte: Float, referenciaOriginal: String, ejemplo: String) {
        Log.d(TAG, "=== 📝 EJEMPLO CORTE LARGO ===")
        Log.d(TAG, "Entrada: ${longitudCorte}cm (${referenciaOriginal})")
        Log.d(TAG, "Resultado esperado:")
        Log.d(TAG, ejemplo)
    }

    fun logDivisionCorteLargo(longitudCorte: Float, varillaMaxima: Float, varillasCompletas: Int, resto: Float) {
        Log.d(TAG, "=== ➗ DIVISIÓN MATEMÁTICA ===")
        Log.d(TAG, "Corte a dividir: ${longitudCorte}cm")
        Log.d(TAG, "Varilla máxima: ${varillaMaxima}cm")
        Log.d(TAG, "Cálculo: ${longitudCorte}cm ÷ ${varillaMaxima}cm = ${varillasCompletas} varillas + ${resto}cm resto")
        Log.d(TAG, "Verificación: ${varillasCompletas} × ${varillaMaxima}cm + ${resto}cm = ${(varillasCompletas * varillaMaxima) + resto}cm")
    }

    fun logCreacionVarillaCompleta(longitudVarilla: Float, referenciaEspecial: String, numero: Int) {
        Log.d(TAG, "=== 🏗️ CREANDO VARILLA COMPLETA ===")
        Log.d(TAG, "Varilla completa #$numero:")
        Log.d(TAG, "  Longitud: ${longitudVarilla}cm")
        Log.d(TAG, "  Referencia especial: $referenciaEspecial")
        Log.d(TAG, "  Corte: [${longitudVarilla}cm(${referenciaEspecial})]")
        Log.d(TAG, "  Retazo: 0cm (100% utilizada)")
    }

    fun logCreacionResto(resto: Float, referenciaEspecial: String) {
        Log.d(TAG, "=== 📏 CREANDO RESTO ===")
        Log.d(TAG, "Resto generado:")
        Log.d(TAG, "  Longitud: ${resto}cm")
        Log.d(TAG, "  Referencia especial: $referenciaEspecial")
        Log.d(TAG, "  Estado: Enviado a optimización normal")
    }

    fun logValidacionCortesLargos(
        piezasOriginales: Int,
        piezasAjustadas: Int,
        varillasCompletasGeneradas: Int,
        referenciasMapeadas: Map<String, String>
    ) {
        Log.d(TAG, "=== ✅ VALIDACIÓN CORTES LARGOS ===")
        Log.d(TAG, "Piezas originales: $piezasOriginales")
        Log.d(TAG, "Piezas después del ajuste: $piezasAjustadas")
        Log.d(TAG, "Varillas completas generadas: $varillasCompletasGeneradas")

        if (referenciasMapeadas.isNotEmpty()) {
            Log.d(TAG, "Mapeo de referencias:")
            referenciasMapeadas.forEach { (original, especial) ->
                Log.d(TAG, "  $original → $especial")
            }
        }

        Log.d(TAG, "Estado: ${if (piezasAjustadas + varillasCompletasGeneradas >= piezasOriginales) "✅ CORRECTO" else "❌ ERROR"}")
    }

    fun logResumenCortesLargos(
        cortesLargosDetectados: Int,
        varillasCompletasCreadas: Int,
        restosCreados: Int,
        tiempoProcesamientoMs: Long
    ) {
        Log.d(TAG, "=== 📈 RESUMEN MANEJO CORTES LARGOS ===")
        Log.d(TAG, "🔍 Cortes largos detectados: $cortesLargosDetectados")
        Log.d(TAG, "🏗️ Varillas completas creadas: $varillasCompletasCreadas")
        Log.d(TAG, "📏 Restos enviados a optimización: $restosCreados")
        Log.d(TAG, "⏱️ Tiempo de procesamiento: ${tiempoProcesamientoMs}ms")

        if (cortesLargosDetectados > 0) {
            Log.d(TAG, "💡 Beneficio: Cortes imposibles ahora son factibles")
            Log.d(TAG, "🎯 Trazabilidad: Referencias especiales mantienen relación")
        }
    }
}