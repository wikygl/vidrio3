package crystal.crystal.optimizadores.corte

import android.annotation.SuppressLint
import android.os.Bundle
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

        Log.d("ResultadoOptimizacionDebug", "=== INICIO mostrarResultados ===")
        Log.d("ResultadoOptimizacionDebug", "Resultado recibido: ${resultado != null}")

        if (resultado != null) {
            Log.d("ResultadoOptimizacionDebug", "Total barras: ${resultado.totalBarrasUsadas}")
            Log.d("ResultadoOptimizacionDebug", "Varillas en resultado: ${resultado.varillasUsadas.size}")

            // Mostrar estadísticas en el header
            tvBarrasUsadas.text = "Barras a cortar: ${resultado.totalBarrasUsadas}"
            tvCantidadCortes.text = "Cantidad de cortes: ${resultado.totalCortes} (sin errores)"

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
                val adapter = ResultadoAdapter(this, resultado.varillasUsadas.toMutableList())
                recyclerResultados.adapter = adapter
            }

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

    /**
     * Procesa las varillas que fueron marcadas como cortadas y las guarda
     */
    private fun procesarVarillasCortadas() {
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