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
        holder.binding.apply {
            tvMonto.text = "S/ %.2f".format(recarga.montoDetectado)
            tvCodigo.text = "Código: ${recarga.codigoOperacion}"
            tvTelefono.text = "Teléfono: ${recarga.telefono}"
            tvEstado.text = "Estado: ${recarga.estado}"
            tvFecha.text = "Fecha: ${recarga.fecha?.let { formatearFecha(it) } ?: "---"}"
        }
    }

    private fun formatearFecha(fecha: Date): String {
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formato.format(fecha)
    }
}
