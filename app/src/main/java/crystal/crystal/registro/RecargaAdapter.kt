package crystal.crystal.registro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.databinding.ItemRecargaBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Recarga(
    var montoDetectado: Double? = null,
    val codigoOperacion: String = "",
    val telefono: String = "",
    val estado: String = "",
    val tipoVoucher: String = "",
    val fecha: Date? = null
)

class RecargaAdapter(
    private val lista: List<Recarga>
) : RecyclerView.Adapter<RecargaAdapter.RecargaViewHolder>() {

    inner class RecargaViewHolder(val binding: ItemRecargaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecargaViewHolder {
        val binding = ItemRecargaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecargaViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: RecargaViewHolder, position: Int) {
        val recarga = lista[position]
        val ctx = holder.itemView.context
        holder.binding.apply {
            tvMonto.text = "S/ %.2f".format(recarga.montoDetectado)
            tvCodigo.text = "Cód: ${recarga.codigoOperacion.ifBlank { "—" }}"
            // Origen → destino consistente para TODOS los tipos (Yape/Plin en cualquier combinación).
            val tipo = tipoLegible(recarga.tipoVoucher)
            tvTelefono.text = if (tipo.isNotEmpty()) tipo else "Tel: ${recarga.telefono}"
            tvEstado.text = estadoLegible(recarga.estado)
            tvEstado.backgroundTintList = android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(ctx, estadoColor(recarga.estado))
            )
            tvFecha.text = recarga.fecha?.let { "· ${formatearFecha(it)}" } ?: ""
        }
    }

    private fun estadoColor(e: String): Int {
        val low = e.lowercase(Locale.getDefault())
        return when {
            low.startsWith("aprobada") || low == "aplicado" -> crystal.crystal.R.color.verde
            low.contains("rechaz") -> crystal.crystal.R.color.rojo
            else -> crystal.crystal.R.color.naranja
        }
    }

    /** "plin_yape" → "Plin → Yape" (siempre origen y destino, no solo uno). */
    private fun tipoLegible(t: String): String = when (t.lowercase(Locale.getDefault())) {
        "yape_yape" -> "Yape → Yape"
        "plin_yape" -> "Plin → Yape"
        "yape_plin" -> "Yape → Plin"
        "plin_plin" -> "Plin → Plin"
        "reclamo_manual" -> "Reclamo manual"
        else -> ""
    }

    /** Estado crudo del backend → chip corto. */
    private fun estadoLegible(e: String): String {
        val low = e.lowercase(Locale.getDefault())
        return when {
            low.startsWith("aprobada") && low.contains("auto") -> "✅ Auto"
            low.startsWith("aprobada") || low == "aplicado" -> "✅ Aprobada"
            low.contains("rechaz") -> "❌ Rechazada"
            else -> "⏳ Pendiente"
        }
    }

    private fun formatearFecha(fecha: Date): String {
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formato.format(fecha)
    }
}
