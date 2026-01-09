package crystal.crystal.taller.puerta.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R
import crystal.crystal.taller.puerta.modelos.Variante

class VariantesAdapter(
    private val variantes: List<Variante>,
    private val alClick: (Variante) -> Unit
) : RecyclerView.Adapter<VariantesAdapter.ViewHolderVariante>() {

    inner class ViewHolderVariante(v: View) : RecyclerView.ViewHolder(v) {
        val imagen: ImageView = v.findViewById(R.id.imgVariante)
        val titulo: TextView = v.findViewById(R.id.tvVariante)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderVariante {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.variantes_puertas, parent, false)
        return ViewHolderVariante(view)
    }

    override fun onBindViewHolder(holder: ViewHolderVariante, position: Int) {
        val variante = variantes[position]
        holder.imagen.setImageResource(variante.imagen)
        holder.titulo.text = variante.nombre
        holder.itemView.setOnClickListener { alClick(variante) }
    }

    override fun getItemCount(): Int = variantes.size
}