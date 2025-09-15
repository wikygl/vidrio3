package crystal.crystal.optimizadores.corte

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import crystal.crystal.R

/**
 * Adapter para mostrar PiezaCorte con drawables de estado
 */
class PiezaCorteAdapter(
    private val context: Context,
    private val piezas: MutableList<PiezaCorte>,
    private val formatter: CorteFormatter,
    private val esPieza: Boolean = true // true para piezas, false para varillas
) : BaseAdapter() {

    override fun getCount(): Int = piezas.size

    override fun getItem(position: Int): PiezaCorte = piezas[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_pieza_corte, parent, false)

        val ivEstado = view.findViewById<ImageView>(R.id.ivEstado)
        val tvTexto = view.findViewById<TextView>(R.id.tvTexto)

        val pieza = piezas[position]

        // Configurar drawable según estado
        if (pieza.cortada) {
            ivEstado.setImageResource(R.drawable.ic_chckr) // true = entra en optimización
        } else {
            ivEstado.setImageResource(R.drawable.ic_chck) // false = no entra
        }

        // Configurar texto según si es pieza o varilla
        if (esPieza) {
            tvTexto.text = "${formatter.df1(pieza.longitud)} cm (${pieza.referencia}) ------ ${pieza.cantidad} uni"
        } else {
            tvTexto.text = "${formatter.df1(pieza.longitud)} cm ------ ${pieza.cantidad} uni (${pieza.referencia})"
        }

        return view
    }
}