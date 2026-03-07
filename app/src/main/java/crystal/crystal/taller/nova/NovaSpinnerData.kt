package crystal.crystal.taller.nova

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import crystal.crystal.R

/**
 * Fuente unica de opciones y adapter del spinner de tubo para Nova.
 */
object NovaSpinnerData {

    data class SpinnerTubos(
        val imageResId: Int,
        val text: String,
        val valor: Float
    )

    fun obtenerOpcionesTubo(): List<SpinnerTubos> {
        return listOf(
            SpinnerTubos(R.drawable.ma_multi, "M\u00FAltiple", 1.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo 2 x 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo 2\u215C x 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo.c 1\u00BD", 3.8f),
            SpinnerTubos(R.drawable.ma_multi, "tubo.c 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "paflon 1\u00BD", 3.8f),
            SpinnerTubos(R.drawable.ma_multi, "paflon 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo 2 x 2", 5.0f),
            SpinnerTubos(R.drawable.ma_multi, "gorrito", 2.5f)
        )
    }

    class AdaptadorSpinner(
        context: Context,
        private val options: List<SpinnerTubos>
    ) : ArrayAdapter<SpinnerTubos>(context, 0, options) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createItemView(position, convertView, parent)
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            return createItemView(position, convertView, parent)
        }

        private fun createItemView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.mo_tubo, parent, false)

            val imageViewOption = view.findViewById<ImageView>(R.id.imgTubo)
            val textViewOption = view.findViewById<TextView>(R.id.txNombre)
            val spinnerOption = options[position]

            imageViewOption.setImageResource(spinnerOption.imageResId)
            textViewOption.text = spinnerOption.text

            return view
        }
    }
}
