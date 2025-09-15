package crystal.crystal.catalogo

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import crystal.crystal.databinding.ModCatalogoBinding

class Holder(view: View) : RecyclerView.ViewHolder(view) {

    val binding = ModCatalogoBinding.bind(view)

    fun render(
        data: Data,
        clicado: (Data) -> Unit,
        longClick: ((Data) -> Unit)? = null
    ) {
        binding.producto.text = data.nombre
        Glide.with(binding.foto.context).load(data.productos).into(binding.foto)

        binding.foto.setOnClickListener { clicado(data) }

        // Agregar long click si se proporciona
        longClick?.let { longClickListener ->
            binding.foto.setOnLongClickListener {
                longClickListener(data)
                true
            }
        }
    }
}