package crystal.crystal.Diseno

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.casilla.Material
import crystal.crystal.databinding.MoTuboBinding

/**
 * Adapter optimizado usando ListAdapter + DiffUtil para actualizar sólo los ítems necesarios,
 * sin usar IDs estables para evitar colisiones.
 */
class MatAdapter(
    private val onClick: (Material) -> Unit
) : ListAdapter<Material, MatAdapter.VH>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = MoTuboBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(
        private val binding: MoTuboBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val material = getItem(bindingAdapterPosition)
                onClick(material)
            }
        }

        fun bind(mat: Material) {
            binding.imgTubo.setImageResource(mat.drawableId)
            binding.txNombre.text = mat.nombre
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Material>() {
            override fun areItemsTheSame(old: Material, new: Material): Boolean =
                old.id == new.id

            override fun areContentsTheSame(old: Material, new: Material): Boolean =
                old == new
        }
    }
}



