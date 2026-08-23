package crystal.crystal.registro

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.databinding.ItemRecargaBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Reserva(
    val totalCent: Long = 0,
    val estado: String = "",
    val pagoRecibido: Boolean = false,
    val tieneComprobante: Boolean = false,
    val creadoMs: Long = 0,
    var reservaId: String = ""
)

class ReservaAdapter(
    private val lista: List<Reserva>,
    private val onReanudar: (Reserva) -> Unit
) : RecyclerView.Adapter<ReservaAdapter.VH>() {

    inner class VH(val binding: ItemRecargaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemRecargaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val r = lista[position]
        val ctx = holder.itemView.context
        holder.binding.apply {
            tvMonto.text = "S/ %.2f".format(r.totalCent / 100.0)
            tvTelefono.text = "Recarga por monto único"
            tvCodigo.text = if (r.estado == "aplicado") "Acreditada" else "Toca para continuar"
            tvEstado.text = etiqueta(r)
            tvEstado.backgroundTintList = android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(ctx, color(r))
            )
            tvFecha.text = if (r.creadoMs > 0) SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(r.creadoMs)) else ""
        }
        holder.itemView.setOnClickListener {
            if (r.estado == "esperando" || r.estado == "expirada") onReanudar(r)
        }
    }

    private fun etiqueta(r: Reserva): String = when {
        r.estado == "aplicado" -> "✅ Acreditada"
        r.estado == "reclamada" -> "🔎 En revisión"
        r.estado == "rechazada" -> "❌ Rechazada"
        r.estado == "expirada" -> "⌛ Expiró"
        r.pagoRecibido && !r.tieneComprobante -> "⚠️ Falta comprobante"
        !r.pagoRecibido && r.tieneComprobante -> "📎 Esperando pago"
        else -> "⏳ En curso"
    }

    private fun color(r: Reserva): Int = when {
        r.estado == "aplicado" -> crystal.crystal.R.color.verde
        r.estado == "rechazada" -> crystal.crystal.R.color.rojo
        r.estado == "expirada" || r.estado == "reclamada" -> crystal.crystal.R.color.gris
        else -> crystal.crystal.R.color.naranja
    }
}
