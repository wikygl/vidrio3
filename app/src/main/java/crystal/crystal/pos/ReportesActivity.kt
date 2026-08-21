package crystal.crystal.pos

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import crystal.crystal.databinding.ActivityReportesBinding
import crystal.crystal.taller.ExportadorResultados
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Pantalla de reportes de ventas: filtra por RANGO DE FECHAS y por VENDEDOR, leyendo de Firestore
 * (usuarios/{uid}/ventas), o sea de todas las terminales del patrón. El uid llega por intent.
 */
class ReportesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportesBinding
    private var uid: String = ""
    private val desde = Calendar.getInstance()
    private val hasta = Calendar.getInstance()
    private var ventas: List<VentaLite> = emptyList()
    private var vendedorSeleccionado = TODOS

    private data class VentaLite(
        val numeroComprobante: String,
        val cliente: String,
        val total: Double,
        val formaPago: String,
        val vendedor: String,
        val fecha: Long
    )

    companion object {
        const val EXTRA_UID = "reportes_uid"
        private const val TODOS = "Todos"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Reportes de ventas"

        uid = intent.getStringExtra(EXTRA_UID).orEmpty()
        if (uid.isBlank()) {
            Toast.makeText(this, "No se pudo identificar la cuenta", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        presetMes() // rango por defecto

        binding.btnDesde.setOnClickListener { elegirFecha(desde) { actualizarBotonesFecha() } }
        binding.btnHasta.setOnClickListener { elegirFecha(hasta) { actualizarBotonesFecha() } }
        binding.btnHoy.setOnClickListener { presetHoy(); cargarRango() }
        binding.btnSemana.setOnClickListener { presetSemana(); cargarRango() }
        binding.btnMes.setOnClickListener { presetMes(); cargarRango() }
        binding.btnAplicar.setOnClickListener { cargarRango() }
        binding.btnExportar.setOnClickListener { exportar() }

        binding.spVendedor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>?, v: View?, pos: Int, id: Long) {
                vendedorSeleccionado = binding.spVendedor.getItemAtPosition(pos)?.toString() ?: TODOS
                render()
            }
            override fun onNothingSelected(p: AdapterView<*>?) {}
        }

        cargarRango()
    }

    // ---------- presets de rango ----------
    private fun presetHoy() {
        desde.timeInMillis = System.currentTimeMillis(); aInicioDia(desde)
        hasta.timeInMillis = System.currentTimeMillis(); aFinDia(hasta)
        actualizarBotonesFecha()
    }

    private fun presetSemana() {
        hasta.timeInMillis = System.currentTimeMillis(); aFinDia(hasta)
        desde.timeInMillis = System.currentTimeMillis()
        desde.add(Calendar.DAY_OF_YEAR, -6); aInicioDia(desde)
        actualizarBotonesFecha()
    }

    private fun presetMes() {
        hasta.timeInMillis = System.currentTimeMillis(); aFinDia(hasta)
        desde.timeInMillis = System.currentTimeMillis()
        desde.set(Calendar.DAY_OF_MONTH, 1); aInicioDia(desde)
        actualizarBotonesFecha()
    }

    private fun aInicioDia(c: Calendar) {
        c.set(Calendar.HOUR_OF_DAY, 0); c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0); c.set(Calendar.MILLISECOND, 0)
    }

    private fun aFinDia(c: Calendar) {
        c.set(Calendar.HOUR_OF_DAY, 23); c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59); c.set(Calendar.MILLISECOND, 999)
    }

    private fun actualizarBotonesFecha() {
        val f = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.btnDesde.text = "Desde: ${f.format(desde.time)}"
        binding.btnHasta.text = "Hasta: ${f.format(hasta.time)}"
    }

    private fun elegirFecha(cal: Calendar, onSet: () -> Unit) {
        DatePickerDialog(
            this,
            { _, y, m, d ->
                cal.set(Calendar.YEAR, y); cal.set(Calendar.MONTH, m); cal.set(Calendar.DAY_OF_MONTH, d)
                if (cal === hasta) aFinDia(cal) else aInicioDia(cal)
                onSet()
            },
            cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // ---------- carga y render ----------
    private fun cargarRango() {
        if (desde.timeInMillis > hasta.timeInMillis) {
            Toast.makeText(this, "El 'desde' no puede ser mayor que el 'hasta'", Toast.LENGTH_SHORT).show()
            return
        }
        binding.progress.visibility = View.VISIBLE
        binding.tvResultado.text = ""
        lifecycleScope.launch {
            try {
                val snap = withContext(Dispatchers.IO) {
                    FirebaseFirestore.getInstance()
                        .collection("usuarios").document(uid)
                        .collection("ventas")
                        .whereGreaterThanOrEqualTo("fecha", desde.timeInMillis)
                        .whereLessThanOrEqualTo("fecha", hasta.timeInMillis)
                        .get().await()
                }
                ventas = snap.documents.map { d ->
                    VentaLite(
                        numeroComprobante = d.getString("numeroComprobante") ?: "—",
                        cliente = d.getString("cliente") ?: "",
                        total = d.getDouble("total") ?: (d.getLong("total")?.toDouble() ?: 0.0),
                        formaPago = d.getString("formaPago") ?: "—",
                        vendedor = d.getString("vendedor")?.takeIf { it.isNotBlank() } ?: "—",
                        fecha = d.getLong("fecha") ?: 0L
                    )
                }.sortedByDescending { it.fecha }
                poblarVendedores()
                render()
            } catch (e: Exception) {
                binding.tvResultado.text = "No se pudieron cargar los reportes:\n${e.message}"
            } finally {
                binding.progress.visibility = View.GONE
            }
        }
    }

    private fun poblarVendedores() {
        val vendedores = mutableListOf(TODOS)
        vendedores.addAll(ventas.map { it.vendedor }.distinct().sorted())
        val prev = vendedorSeleccionado
        binding.spVendedor.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, vendedores)
        val idx = vendedores.indexOf(prev).takeIf { it >= 0 } ?: 0
        vendedorSeleccionado = vendedores[idx]
        binding.spVendedor.setSelection(idx)
    }

    private fun render() {
        val filtradas = if (vendedorSeleccionado == TODOS) ventas
        else ventas.filter { it.vendedor == vendedorSeleccionado }

        val total = filtradas.sumOf { it.total }
        val porVendedor = ventas.groupBy { it.vendedor }
            .mapValues { e -> Pair(e.value.size, e.value.sumOf { it.total }) }
            .toList().sortedByDescending { it.second.second }
        val porPago = filtradas.groupBy { it.formaPago }
            .mapValues { e -> e.value.sumOf { it.total } }
            .toList().sortedByDescending { it.second }

        val sb = StringBuilder()
        sb.appendLine("Rango: ${fmtF(desde.timeInMillis)} — ${fmtF(hasta.timeInMillis)}")
        sb.appendLine("Vendedor: $vendedorSeleccionado")
        sb.appendLine()
        sb.appendLine("Ventas: ${filtradas.size}    Total: S/ ${fmtM(total)}")
        sb.appendLine()
        sb.appendLine("── POR VENDEDOR (rango completo) ──")
        if (porVendedor.isEmpty()) sb.appendLine("Sin ventas.")
        porVendedor.forEach { (v, ct) -> sb.appendLine("• $v: ${ct.first} vtas · S/ ${fmtM(ct.second)}") }
        sb.appendLine()
        sb.appendLine("── POR FORMA DE PAGO ──")
        porPago.forEach { (fp, t) -> sb.appendLine("• $fp: S/ ${fmtM(t)}") }
        sb.appendLine()
        sb.appendLine("── DETALLE (${filtradas.size}) ──")
        filtradas.take(200).forEach { v ->
            sb.appendLine("${v.numeroComprobante}  ${v.cliente}")
            sb.appendLine("   S/ ${fmtM(v.total)} · ${v.formaPago} · ${v.vendedor} · ${fmtDH(v.fecha)}")
        }
        if (filtradas.size > 200) sb.appendLine("… y ${filtradas.size - 200} más")

        binding.tvResultado.text = sb.toString()
    }

    private fun fmtM(v: Double) = String.format(Locale.US, "%.2f", v)
    private fun fmtF(ms: Long) = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(ms))
    private fun fmtDH(ms: Long) = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(ms))

    // ---------- exportar (reutiliza ExportadorResultados: guardar + compartir) ----------
    private fun exportar() {
        if (ventas.isEmpty()) {
            Toast.makeText(this, "No hay ventas en el rango para exportar", Toast.LENGTH_SHORT).show()
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Exportar reporte")
            .setItems(arrayOf("CSV (Excel)", "TXT (resumen)")) { _, which ->
                val sello = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
                when (which) {
                    0 -> ExportadorResultados.exportarTexto(
                        this, "Reporte_ventas_$sello.csv", construirCsv(), "text/csv"
                    )
                    1 -> ExportadorResultados.exportarTexto(
                        this, "Reporte_ventas_$sello.txt", binding.tvResultado.text.toString(), "text/plain"
                    )
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun construirCsv(): String {
        val filtradas = if (vendedorSeleccionado == TODOS) ventas
        else ventas.filter { it.vendedor == vendedorSeleccionado }
        val fFecha = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return buildString {
            append("Numero,Fecha,Cliente,Vendedor,FormaPago,Total\n")
            for (v in filtradas) {
                append(csv(v.numeroComprobante)).append(",")
                append(csv(fFecha.format(Date(v.fecha)))).append(",")
                append(csv(v.cliente)).append(",")
                append(csv(v.vendedor)).append(",")
                append(csv(v.formaPago)).append(",")
                append(fmtM(v.total)).append("\n")
            }
        }
    }

    /** Escapa un campo CSV (comillas dobles si contiene coma, comilla o salto de línea). */
    private fun csv(campo: String): String {
        val limpio = campo.replace("\"", "\"\"")
        return if (campo.contains(",") || campo.contains("\"") || campo.contains("\n")) "\"$limpio\"" else limpio
    }
}
