package crystal.crystal.taller.nova

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import crystal.crystal.R

/**
 * Clase que maneja los datos y configuraciÃ³n de spinners para las actividades Nova
 * Centraliza la lÃ³gica de spinners para evitar duplicaciÃ³n
 */
object NovaSpinnerData {

    // ==================== DATA CLASS PARA OPCIONES DE TUBO ====================

    data class SpinnerTubos(
        val imageResId: Int,
        val text: String,
        val valor: Float
    )

    // ==================== CONFIGURACIÃ“N DE OPCIONES DE TUBO ====================

    fun obtenerOpcionesTubo(): List<SpinnerTubos> {
        return listOf(
            SpinnerTubos(R.drawable.ma_multi, "MÃºltiple", 1.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo 2 x 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo 2 3|8 x 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo.c 1 1|2", 3.8f),
            SpinnerTubos(R.drawable.ma_multi, "tubo.c 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "paflon 1 1|2", 3.8f),
            SpinnerTubos(R.drawable.ma_multi, "paflon 1", 2.5f),
            SpinnerTubos(R.drawable.ma_multi, "tubo 2 x 2", 5.0f),
            SpinnerTubos(R.drawable.ma_multi, "gorrito", 2.5f)
        )
    }

    // ==================== ADAPTADOR PERSONALIZADO PARA SPINNER ====================

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

    // ==================== FUNCIÃ“N PARA CONFIGURAR SPINNER ====================

    fun configurarSpinnerTubo(
        context: Context,
        spinner: Spinner,
        onSeleccion: (tubo: Float, puente: String) -> Unit
    ) {
        val spinnerOptions = obtenerOpcionesTubo()
        val adapter = AdaptadorSpinner(context, spinnerOptions)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedOption = spinnerOptions[position]
                onSeleccion(selectedOption.valor, selectedOption.text)

                Toast.makeText(
                    context,
                    "Seleccionado: ${selectedOption.text}, Valor: ${selectedOption.valor}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }

    // ==================== CONFIGURACIÃ“N DE MODELOS (PARA NOVA APA) ====================

    data class ModeloConfig(
        val imageResource: Int,
        val texto: String,
        val diseno: String,
        val otros: Boolean,
        val maxLados: Int = -1,
        val configuracionEspecial: (() -> Unit)? = null
    )

    fun obtenerModelosConfig(): Map<String, ModeloConfig> {
        return mapOf(
            "novan" to ModeloConfig(
                imageResource = R.drawable.ic_fichad3a,
                texto = "nn",
                diseno = "ic_fichad3a",
                otros = false
            ),
            "novaP2" to ModeloConfig(
                imageResource = R.drawable.nova2p,
                texto = "np",
                diseno = "nova2p",
                otros = false
            ),
            "novacc" to ModeloConfig(
                imageResource = R.drawable.novacc,
                texto = "ncc",
                diseno = "novacc",
                otros = true
            ),
            "nova3c" to ModeloConfig(
                imageResource = R.drawable.nova3c,
                texto = "n3c",
                diseno = "nova3c",
                otros = true
            ),
            "novacfc" to ModeloConfig(
                imageResource = R.drawable.novacfc,
                texto = "ncfc",
                diseno = "novacfc",
                otros = true
            ),
            "noval" to ModeloConfig(
                imageResource = R.drawable.noval,
                texto = "nl",
                diseno = "noval",
                otros = true,
                maxLados = 2
            ),
            "novau" to ModeloConfig(
                imageResource = R.drawable.novau,
                texto = "nu",
                diseno = "novau",
                otros = true,
                maxLados = 3
            ),
            "novas" to ModeloConfig(
                imageResource = R.drawable.novas,
                texto = "ns",
                diseno = "novas",
                otros = true,
                maxLados = -1
            ),
            "novacu" to ModeloConfig(
                imageResource = R.drawable.novacu,
                texto = "ncu",
                diseno = "novacu",
                otros = true
            ),
            "novaci" to ModeloConfig(
                imageResource = R.drawable.novaci,
                texto = "nci",
                diseno = "novaci",
                otros = true
            )
        )
    }

    // ==================== FUNCIONES DE VISIBILIDAD ESPECÃFICAS ====================

    fun configurarVisibilidadInaparente(
        divisiones: Int,
        alto: Float,
        hoja: Float,
        layouts: LayoutsInaparente
    ) {
        // Configurar visibilidad para H
        layouts.lyH.visibility = if (divisiones == 1) View.GONE else View.VISIBLE

        // Configurar visibilidad para Tope
        layouts.lyTo.visibility = if (divisiones != 2) View.GONE else View.VISIBLE

        // Configurar visibilidad para Portafelpa
        layouts.lyPf.visibility = if (divisiones == 1) View.GONE else View.VISIBLE

        // Configurar visibilidad para U Felpero
        layouts.lyUf.visibility = if (divisiones == 1 || alto <= hoja) View.GONE else View.VISIBLE

        // Configurar visibilidad para Fijo Corredizo
        layouts.lyFijoCorre.visibility = if (alto <= hoja && divisiones > 1) View.VISIBLE else View.GONE

        // Configurar visibilidad para Tubo
        layouts.lyTubo.visibility = if (alto <= hoja) View.GONE else View.VISIBLE
    }

    fun configurarVisibilidadU(
        us: Float,
        layouts: LayoutsU
    ) {
        when (us) {
            1f -> {
                layouts.lyU.visibility = View.GONE
                layouts.u38layout.visibility = View.VISIBLE
                layouts.ulayout.visibility = View.GONE
            }
            1.5f -> {
                layouts.lyU.visibility = View.VISIBLE
                layouts.u38layout.visibility = View.GONE
                layouts.ulayout.visibility = View.GONE
            }
            else -> {
                layouts.lyU.visibility = View.GONE
                layouts.u38layout.visibility = View.GONE
                layouts.ulayout.visibility = View.VISIBLE
            }
        }
    }

    // ==================== DATA CLASSES PARA ORGANIZAR LAYOUTS ====================

    data class LayoutsInaparente(
        val lyH: View,
        val lyTo: View,
        val lyPf: View,
        val lyUf: View,
        val lyFijoCorre: View,
        val lyTubo: View
    )

    data class LayoutsU(
        val lyU: View,
        val u38layout: View,
        val ulayout: View
    )
}