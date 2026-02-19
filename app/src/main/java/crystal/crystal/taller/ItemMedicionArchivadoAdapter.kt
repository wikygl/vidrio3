package crystal.crystal.taller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R
import crystal.crystal.medicion.ItemMedicionObra
import java.util.Locale

class ItemMedicionArchivadoAdapter(
    private val onEditarClick: (ItemMedicionObra) -> Unit,
    private val onEliminarClick: (ItemMedicionObra) -> Unit,
    private val onDuplicarClick: (ItemMedicionObra) -> Unit
) : RecyclerView.Adapter<ItemMedicionArchivadoAdapter.ViewHolder>() {

    private var items: List<ItemMedicionObra> = emptyList()

    fun submitList(nuevosItems: List<ItemMedicionObra>) {
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
        private val btnDuplicar: ImageButton? = itemView.findViewById(R.id.btnDuplicarItem)

        fun bind(item: ItemMedicionObra) {
            val categoria = item.categoria.lowercase(Locale.getDefault()).replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
            tvItemNumero.text = "$categoria ${item.numero} · ${item.estado.name}"
            tvItemDescripcion.text = item.descripcionCorta()

            btnEditar.setOnClickListener { onEditarClick(item) }
            btnEliminar.setOnClickListener { onEliminarClick(item) }
            btnDuplicar?.setOnClickListener { onDuplicarClick(item) }
        }
    }
}

