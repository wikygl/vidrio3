package crystal.crystal.optimizadores.corte

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R

/**
 * Activity para mostrar los resultados de optimización de manera visual
 */
class ResultadoOptimizacionActivity : AppCompatActivity() {

    private lateinit var tvBarrasUsadas: TextView
    private lateinit var tvCantidadCortes: TextView
    private lateinit var recyclerResultados: RecyclerView
    private lateinit var btnVolver: Button

    // Agregar DataManager para guardar cortes ejecutados
    private lateinit var dataManager: CorteDataManager
    private var resultadoActual: ResultadoOptimizacion? = null
    private var nombreListaActual: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_optimizacion)

        // Inicializar DataManager
        dataManager = CorteDataManager(this)

        inicializarVistas()
        configurarRecyclerView()
        mostrarResultados()
        configurarEventos()
    }

    private fun inicializarVistas() {
        tvBarrasUsadas = findViewById(R.id.tvBarrasUsadas)
        tvCantidadCortes = findViewById(R.id.tvCantidadCortes)
        recyclerResultados = findViewById(R.id.recyclerResultados)
        btnVolver = findViewById(R.id.btnVolver)
    }

    @SuppressLint("LongLogTag")
    private fun configurarRecyclerView() {
        Log.d("ResultadoOptimizacionDebug", "Configurando RecyclerView")
        recyclerResultados.layoutManager = LinearLayoutManager(this)
        Log.d("ResultadoOptimizacionDebug", "LayoutManager configurado: ${recyclerResultados.layoutManager != null}")
    }

    @SuppressLint("LongLogTag")
    private fun mostrarResultados() {
        val resultado = intent.getParcelableExtra<ResultadoOptimizacion>("resultado_optimizacion")
            ?: dataManager.recuperarResultadoOptimizacion()
        resultadoActual = resultado
        nombreListaActual = intent.getStringExtra("nombre_lista")
            ?: dataManager.recuperarNombreListaResultadoOptimizacion()

        Log.d("ResultadoOptimizacionDebug", "=== INICIO mostrarResultados ===")
        Log.d("ResultadoOptimizacionDebug", "Resultado recibido: ${resultado != null}")

        if (resultado != null) {
            Log.d("ResultadoOptimizacionDebug", "Total barras: ${resultado.totalBarrasUsadas}")
            Log.d("ResultadoOptimizacionDebug", "Varillas en resultado: ${resultado.varillasUsadas.size}")

            // Mostrar estadísticas en el header
            val nombreLista = nombreListaActual
            tvBarrasUsadas.text = if (nombreLista.isNotBlank())
                "Barras a cortar: ${resultado.totalBarrasUsadas} de $nombreLista"
            else
                "Barras a cortar: ${resultado.totalBarrasUsadas}"
            if (resultado.cortesErroneos > 0) {
                tvCantidadCortes.text = textoCortesConFaltantes(resultado)
                mostrarAvisoCortesFaltantes(resultado)
            } else {
                tvCantidadCortes.text = "Cantidad de cortes (${resultado.totalCortes})"
                tvCantidadCortes.setTextColor(Color.BLACK)
            }

            // NUEVO: Verificar si ya existe un adapter
            val adapterExistente = recyclerResultados.adapter as? ResultadoAdapter

            if (adapterExistente != null) {
                Log.d("ResultadoOptimizacionDebug", "Actualizando adapter existente")
                // Actualizar datos del adapter existente
                adapterExistente.resultados.clear()
                adapterExistente.resultados.addAll(resultado.varillasUsadas)
                adapterExistente.notifyDataSetChanged()
            } else {
                Log.d("ResultadoOptimizacionDebug", "Creando nuevo adapter")
                // Crear nuevo adapter
                val adapter = ResultadoAdapter(this, resultado.varillasUsadas.toMutableList()) {
                    guardarResultadoActual(it)
                }
                recyclerResultados.adapter = adapter
            }

            guardarResultadoActual(resultado.varillasUsadas)

            Log.d("ResultadoOptimizacionDebug", "RecyclerView actualizado")

        } else {
            Log.e("ResultadoOptimizacionDebug", "ERROR: resultado es NULL")
            tvBarrasUsadas.text = "Error: No se recibieron datos"
            tvCantidadCortes.text = "Vuelve e intenta nuevamente"
        }
    }

    private fun configurarEventos() {
        btnVolver.setOnClickListener {
            procesarVarillasCortadas()
            finish() // Cierra esta activity y vuelve a la anterior
        }
    }

    private fun mostrarAvisoCortesFaltantes(resultado: ResultadoOptimizacion) {
        val detalle = resultado.cortesFaltantes
            .take(12)
            .joinToString("\n") { faltante ->
                "${formatearNumero(faltante.longitud)} cm = ${faltante.cantidad} (${faltante.referencia})"
            }
        val extra = if (resultado.cortesFaltantes.size > 12) {
            "\n... y ${resultado.cortesFaltantes.size - 12} medidas mas"
        } else {
            ""
        }

        AlertDialog.Builder(this)
            .setTitle("Faltan cortes")
            .setMessage("No alcanzaron las varillas para completar todo.\n\nFaltan ${resultado.cortesErroneos} corte(s):\n\n$detalle$extra")
            .setPositiveButton("Entendido", null)
            .show()
    }

    private fun textoCortesConFaltantes(resultado: ResultadoOptimizacion): SpannableString {
        val primeraLinea = "Cantidad de cortes (${resultado.totalCortes})"
        val segundaLinea = "Cortes no incluidos (${resultado.cortesErroneos})"
        val texto = "$primeraLinea\n$segundaLinea"
        return SpannableString(texto).apply {
            setSpan(
                ForegroundColorSpan(Color.BLACK),
                0,
                primeraLinea.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            setSpan(
                ForegroundColorSpan(Color.RED),
                primeraLinea.length + 1,
                texto.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    private fun formatearNumero(numero: Float): String {
        return if (numero % 1.0f == 0.0f) {
            numero.toInt().toString()
        } else {
            "%.1f".format(numero).replace(",", ".")
        }
    }

    override fun onPause() {
        guardarResultadoActual()
        super.onPause()
    }

    private fun guardarResultadoActual(varillas: List<VarillaResultado>? = null) {
        val base = resultadoActual ?: return
        val varillasActuales = varillas ?: (recyclerResultados.adapter as? ResultadoAdapter)?.resultados ?: base.varillasUsadas
        val actualizado = base.copy(varillasUsadas = varillasActuales)
        resultadoActual = actualizado
        dataManager.guardarResultadoOptimizacion(actualizado, nombreListaActual)
    }

    /**
     * Procesa las varillas que fueron marcadas como cortadas y las guarda
     */
    private fun procesarVarillasCortadas() {
        guardarResultadoActual()
        val adapter = recyclerResultados.adapter as? ResultadoAdapter
        if (adapter != null) {
            val varillasResultado = adapter.resultados
            val cortesEjecutados = mutableListOf<VarillaCortada>()

            // Procesar cada varilla del resultado
            varillasResultado.forEach { varillaResultado ->
                if (varillaResultado.cortada) { // Si está marcada como cortada
                    // Agrupar los cortes por referencia y cantidad
                    val cortesAgrupados = agruparCortesPorReferencia(varillaResultado.cortesConReferencias)

                    val varillaCortada = VarillaCortada(
                        longitudVarilla = varillaResultado.longitudVarilla,
                        cortesEjecutados = cortesAgrupados
                    )
                    cortesEjecutados.add(varillaCortada)
                }
            }

            // DEBUG: Log de los cortes ejecutados
            if (cortesEjecutados.isNotEmpty()) {
                DebugHelper.logCortesEjecutados(cortesEjecutados)
                dataManager.guardarCortesEjecutados(cortesEjecutados)
            }
        }
    }

    /**
     * Agrupa cortes con la misma longitud y referencia, sumando las cantidades
     */
    private fun agruparCortesPorReferencia(cortesConRefs: List<CorteConReferencia>): List<CorteEjecutado> {
        // Agrupar por longitud + referencia
        val agrupados = cortesConRefs.groupBy { "${it.longitud}-${it.referencia}" }

        return agrupados.map { (_, lista) ->
            val primer = lista.first()
            CorteEjecutado(
                longitud = primer.longitud,
                referencia = primer.referencia,
                cantidad = lista.size // Contar cuántas veces aparece
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        procesarVarillasCortadas()
        finish()
        return true
    }
}
