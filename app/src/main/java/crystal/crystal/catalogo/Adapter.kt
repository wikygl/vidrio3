package crystal.crystal.catalogo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R

class Adapter(
    val lista: List<Data>,
    private val clicado: (Data) -> Unit,
    private val longClick: ((Data) -> Unit)? = null
) : RecyclerView.Adapter<Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return Holder(layoutInflater.inflate(R.layout.mod_catalogo, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = lista[position]
        holder.render(item, clicado, longClick)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}