package crystal.crystal.clientes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.databinding.ItemClienteBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Adapter para lista de clientes
 */
class ClienteAdapter(
    private val onClienteClick: (Cliente) -> Unit,
    private val onEliminarClick: (Cliente) -> Unit
) : ListAdapter<Cliente, ClienteAdapter.ClienteViewHolder>(ClienteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClienteViewHolder {
        val binding = ItemClienteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClienteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClienteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClienteViewHolder(
        private val binding: ItemClienteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cliente: Cliente) {
            binding.apply {
                // Nombre
                tvNombre.text = cliente.nombreCompleto

                // Documento
                tvDocumento.text = "${cliente.tipoDocumento.nombre}: ${cliente.numeroDocumento}"

                // Descuento (si tiene)
                if (cliente.descuentoPorcentaje != null && cliente.descuentoPorcentaje > 0) {
                    tvDescuento.visibility = android.view.View.VISIBLE
                    tvDescuento.text = "${cliente.descuentoPorcentaje.toInt()}% desc."
                } else {
                    tvDescuento.visibility = android.view.View.GONE
                }

                // Estadísticas
                val formato = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
                tvEstadisticas.text = "${cliente.numeroCompras} compras • ${formato.format(cliente.totalCompras)}"

                // Última compra
                if (cliente.ultimaCompra != null) {
                    val fecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(Date(cliente.ultimaCompra))
                    tvUltimaCompra.text = "Última: $fecha"
                    tvUltimaCompra.visibility = android.view.View.VISIBLE
                } else {
                    tvUltimaCompra.visibility = android.view.View.GONE
                }

                // Click en el item completo
                root.setOnClickListener {
                    onClienteClick(cliente)
                }

                // Botón eliminar
                btnEliminar.setOnClickListener {
                    onEliminarClick(cliente)
                }
            }
        }
    }

    class ClienteDiffCallback : DiffUtil.ItemCallback<Cliente>() {
        override fun areItemsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Cliente, newItem: Cliente): Boolean {
            return oldItem == newItem
        }
    }
}