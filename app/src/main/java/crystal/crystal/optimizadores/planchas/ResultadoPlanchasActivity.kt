package crystal.crystal.optimizadores.planchas

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R
import kotlin.math.roundToInt

class ResultadoPlanchasActivity : AppCompatActivity() {

    private lateinit var dataManager: PlanchaDataManager
    private var adapterResultado: ResultadoPlanchasAdapter? = null
    private var zoomPlanchas = 1.0f
    private val zoomMin = 0.75f
    private val zoomMax = 2.0f
    private val zoomPaso = 0.25f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_planchas)

        dataManager = PlanchaDataManager(this)

        val resultado = dataManager.recuperarResultado()

        val tvPlanchas = findViewById<TextView>(R.id.tvPlanchasUsadas)
        val tvArea = findViewById<TextView>(R.id.tvAreaUsada)
        val tvDesp = findViewById<TextView>(R.id.tvDesperdicio)
        val recycler = findViewById<RecyclerView>(R.id.recyclerPlanchas)
        val btnZoomMenos = findViewById<Button>(R.id.btnZoomMenos)
        val btnZoomMas = findViewById<Button>(R.id.btnZoomMas)
        val tvZoom = findViewById<TextView>(R.id.tvZoomPlanchas)
        val btnVolver = findViewById<Button>(R.id.btnVolver)

        if (resultado != null) {
            tvPlanchas.text = "Planchas usadas: ${resultado.planchas.size}"
            tvArea.text = "Área usada: ${"%.3f".format(resultado.areaUsadaMm2 / 1_000_000.0)} m²"
            tvDesp.text = "Desperdicio: ${"%.3f".format(resultado.areaDesperdicioMm2 / 1_000_000.0)} m²"

            val planchasOrdenadas = resultado.planchas
                .sortedWith(
                    compareByDescending<PlanchaOptimizada> { porcentajeUso(it) }
                        .thenByDescending { it.areaUsadaMm2 }
                        .thenBy { it.indice }
                )
                .toMutableList()

            val adapter = ResultadoPlanchasAdapter(this, planchasOrdenadas) { plancha ->
                PlanchaPdfExport.exportarYCompartir(this, listOf(plancha), plancha.nombre)
            }
            adapterResultado = adapter
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.adapter = adapter

            findViewById<Button>(R.id.btnPdfTodos).setOnClickListener {
                // Aprovecha la selección verde (cortada): exporta las seleccionadas;
                // si no hay ninguna seleccionada, exporta todas.
                val seleccionadas = planchasOrdenadas.filter { it.cortada }
                val aExportar = if (seleccionadas.isNotEmpty()) seleccionadas else planchasOrdenadas
                PlanchaPdfExport.exportarYCompartir(this, aExportar, "planchas_optimizadas")
            }

            fun actualizarZoom() {
                tvZoom.text = "${(zoomPlanchas * 100).roundToInt()}%"
                btnZoomMenos.isEnabled = zoomPlanchas > zoomMin
                btnZoomMas.isEnabled = zoomPlanchas < zoomMax
                adapter.setZoom(zoomPlanchas)
            }

            btnZoomMenos.setOnClickListener {
                zoomPlanchas = (zoomPlanchas - zoomPaso).coerceAtLeast(zoomMin)
                actualizarZoom()
            }

            btnZoomMas.setOnClickListener {
                zoomPlanchas = (zoomPlanchas + zoomPaso).coerceAtMost(zoomMax)
                actualizarZoom()
            }

            actualizarZoom()
        } else {
            tvPlanchas.text = "Error: no se recibieron datos"
        }

        btnVolver.setOnClickListener {
            procesarPlanchasCortadas()
            finish()
        }
    }

    private fun procesarPlanchasCortadas() {
        val planchasCortadas = adapterResultado?.planchas?.filter { it.cortada }.orEmpty()
        if (planchasCortadas.isEmpty()) return

        val piezas = planchasCortadas
            .flatMap { it.cortes }
            .groupBy { corte ->
                val anchoOriginalMm = if (corte.rotada) corte.altoMm else corte.anchoMm
                val altoOriginalMm = if (corte.rotada) corte.anchoMm else corte.altoMm
                "${anchoOriginalMm}|${altoOriginalMm}|${corte.descripcion}"
            }
            .map { (_, cortes) ->
                val primer = cortes.first()
                val anchoOriginalMm = if (primer.rotada) primer.altoMm else primer.anchoMm
                val altoOriginalMm = if (primer.rotada) primer.anchoMm else primer.altoMm
                PiezaPlanchaEjecutada(
                    ancho = anchoOriginalMm / 10f,
                    alto = altoOriginalMm / 10f,
                    info = primer.descripcion,
                    cantidad = cortes.size
                )
            }

        if (piezas.isNotEmpty()) {
            dataManager.guardarPiezasEjecutadas(piezas)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        procesarPlanchasCortadas()
        finish()
        return true
    }

    private fun porcentajeUso(p: PlanchaOptimizada): Double {
        val areaTotal = p.anchoMm.toLong() * p.altoMm.toLong()
        return if (areaTotal > 0L) p.areaUsadaMm2.toDouble() / areaTotal.toDouble() else 0.0
    }
}
