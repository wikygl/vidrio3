package calculaora.e.vidrio3.catalogo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import calculaora.e.vidrio3.R

class Adapter(val lista:List<Data>,private val clicado:(Data) -> Unit):RecyclerView.Adapter<Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return Holder(layoutInflater.inflate(R.layout.mod_catalogo,parent,false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item=lista[position]
        holder.render(item,clicado)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}
/*class AdapterFotos(val array:List<Data>):RecyclerView.Adapter<HolderFotos>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderFotos {
        val layoutInflater = LayoutInflater.from(parent.context)
        return HolderFotos(layoutInflater.inflate(R.layout.mo_fotos,parent,false))
    }

    override fun onBindViewHolder(holder: HolderFotos, position: Int) {
        val item=array[position]
        holder.render(item)
    }

    override fun getItemCount(): Int {
        return array.size
    }*/

