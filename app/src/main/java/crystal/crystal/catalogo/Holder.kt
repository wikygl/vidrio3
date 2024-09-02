package crystal.crystal.catalogo

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import crystal.crystal.databinding.ModCatalogoBinding


class Holder(view:View): RecyclerView.ViewHolder(view){

    val binding= ModCatalogoBinding.bind(view)

    fun render(data: Data,clicado:(Data) -> Unit){
        binding.producto.text=data.nombre
        Glide.with(binding.foto.context).load(data.productos).into(binding.foto)
        binding.foto.setOnClickListener {clicado(data)}

    }

}

/*class HolderFotos(view:View): RecyclerView.ViewHolder(view){

    private val foto: ImageView = view.findViewById(R.id.foto)
    private val producto: TextView =view.findViewById(R.id.autor)

    fun render(data: Data){
        producto.text=data.nombre
        Glide.with(foto.context).load(data.productos).into(foto)
    }
}*/