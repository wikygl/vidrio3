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

        val tvFaltantes = findViewById<TextView>(R.id.tvFaltantesResumen)

        if (resultado != null) {
            tvPlanchas.text = "Planchas usadas: ${resultado.planchas.size}"
            tvArea.text = "Área usada: ${"%.3f".format(resultado.areaUsadaMm2 / 1_000_000.0)} m²"
            tvDesp.text = "Desperdicio: ${"%.3f".format(resultado.areaDesperdicioMm2 / 1_000_000.0)} m²"

            // Indicador rojo en el resumen: cuántos cortes no entraron (el detalle va al final).
            val nFaltantes = resultado.piezasSinUbicar.size
            if (nFaltantes > 0) {
                tvFaltantes.text = "Cortes faltantes (no cortados): $nFaltantes"
                tvFaltantes.visibility = android.view.View.VISIBLE
            } else {
                tvFaltantes.visibility = android.view.View.GONE
            }

            val planchasOrdenadas = resultado.planchas
                // De menor a mayor: primero los retazos (material sobrante / medidas menores), luego
                // las planchas por área ascendente; a igualdad, las más aprovechadas primero.
                .sortedWith(
                    compareByDescending<PlanchaOptimizada> { it.esRetazoEntrada }
                        .thenBy { it.anchoMm.toLong() * it.altoMm }
                        .thenByDescending { porcentajeUso(it) }
                )
                // Cada unidad conserva el nombre que el usuario le puso en el inventario. Si de ese
                // nombre se usó más de una unidad, se numeran nombre 1, nombre 2, … para saber cuál
                // es cuál al momento de cortar.
                .let { lista ->
                    val totalPorNombre = lista.groupingBy { it.nombre }.eachCount()
                    val usados = mutableMapOf<String, Int>()
                    lista.mapIndexed { i, p ->
                        val nombre = if ((totalPorNombre[p.nombre] ?: 1) > 1) {
                            val n = (usados[p.nombre] ?: 0) + 1
                            usados[p.nombre] = n
                            "${p.nombre} $n"
                        } else p.nombre
                        p.copy(indice = i + 1, nombre = nombre)
                    }
                }
                .toMutableList()

            val adapter = ResultadoPlanchasAdapter(this, planchasOrdenadas) { plancha ->
                PlanchaPdfExport.exportarYCompartir(this, listOf(plancha), plancha.nombre)
            }
            adapter.setFaltantes(resultado.piezasSinUbicar)
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
            if (procesarPlanchasCortadas()) finish()
        }
    }

    // Devuelve true si se puede cerrar la actividad ahora; false si se mostró la invitación a FULL
    // (la actividad se cierra al cerrar el diálogo, vía el callback).
    private fun procesarPlanchasCortadas(): Boolean {
        val planchasCortadas = adapterResultado?.planchas?.filter { it.cortada }.orEmpty()
        if (planchasCortadas.isEmpty()) return true
        // Candado (Fase 3): descontar de la lista lo ya cortado es de pago. Muestra la invitación y
        // vuelve al cerrarla (sin descontar). No cierra la actividad antes de tiempo.
        if (!crystal.crystal.Suscripcion.avanzadoActivo()) {
            crystal.crystal.Suscripcion.invitarFull(
                this,
                "Descontar de la lista las planchas cortadas es una función de pago."
            ) { finish() }
            return false
        }

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
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        if (procesarPlanchasCortadas()) finish()
        return true
    }

    private fun porcentajeUso(p: PlanchaOptimizada): Double {
        val areaTotal = p.anchoMm.toLong() * p.altoMm.toLong()
        return if (areaTotal > 0L) p.areaUsadaMm2.toDouble() / areaTotal.toDouble() else 0.0
    }
}
