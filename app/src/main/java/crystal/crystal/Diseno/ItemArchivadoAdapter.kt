package crystal.crystal.Diseno

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R
import java.util.Locale

class ItemArchivadoAdapter(
    private val onEditarClick: (ItemDisenoConfiguracion) -> Unit,
    private val onEliminarClick: (ItemDisenoConfiguracion) -> Unit
) : RecyclerView.Adapter<ItemArchivadoAdapter.ViewHolder>() {

    private var items: List<ItemDisenoConfiguracion> = emptyList()

    fun submitList(nuevosItems: List<ItemDisenoConfiguracion>) {
        items = nuevosItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_diseno_archivado,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvItemNumero: TextView = itemView.findViewById(R.id.tvItemNumero)
        private val tvItemDescripcion: TextView = itemView.findViewById(R.id.tvItemDescripcion)
        private val btnEditar: ImageButton = itemView.findViewById(R.id.btnEditarItem)
        private val btnEliminar: ImageButton = itemView.findViewById(R.id.btnEliminarItem)

        fun bind(item: ItemDisenoConfiguracion) {
            val categoria = item.categoria.lowercase(Locale.getDefault()).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            tvItemNumero.text = "$categoria ${item.numero}"
            tvItemDescripcion.text = item.getDescripcionCorta()

            btnEditar.setOnClickListener {
                onEditarClick(item)
            }

            btnEliminar.setOnClickListener {
                onEliminarClick(item)
            }
        }
    }
}
