package crystal.crystal.Diseno

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class MaterialAdapter(
    private val materials: List<Int>,
    private val onLongClickRemove: (Int) -> Unit
) : RecyclerView.Adapter<MaterialAdapter.MaterialViewHolder>() {

    inner class MaterialViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView) {
        init {
            imageView.setOnLongClickListener {
                // Cuando se hace toque prolongado, eliminar el material
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onLongClickRemove(position)
                }
                true
            }
        }
    }

    class VH {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val imageView = ImageView(parent.context).apply {
            // Ajustamos para que la imagen ocupe sólo el espacio que necesita
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            adjustViewBounds = true
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        return MaterialViewHolder(imageView)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        holder.imageView.setImageResource(materials[position])
    }

    override fun getItemCount(): Int = materials.size
}
