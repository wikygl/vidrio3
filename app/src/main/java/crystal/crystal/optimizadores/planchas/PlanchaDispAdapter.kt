package crystal.crystal.optimizadores.planchas

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import crystal.crystal.R

class PlanchaDispAdapter(
    private val context: Context,
    private val items: MutableList<PlanchaDisponible>,
    private val formatter: PlanchaFormatter
) : BaseAdapter() {

    override fun getCount() = items.size
    override fun getItem(pos: Int) = items[pos]
    override fun getItemId(pos: Int) = pos.toLong()

    override fun getView(pos: Int, convertView: View?, parent: ViewGroup?): View {
        val v = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_pieza_corte, parent, false)
        val ivEstado = v.findViewById<ImageView>(R.id.ivEstado)
        val tvTexto = v.findViewById<TextView>(R.id.tvTexto)
        val p = items[pos]
        ivEstado.setImageResource(
            if (p.activa) R.drawable.ic_chckr else R.drawable.ic_chck
        )
        tvTexto.text = "${formatter.df1(p.ancho)}x${formatter.df1(p.alto)} cm — ${p.cantidad} uni (${p.nombre})"
        return v
    }
}