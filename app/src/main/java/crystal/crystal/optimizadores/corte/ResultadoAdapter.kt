package crystal.crystal.optimizadores.corte

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R

/**
 * Adapter mejorado para mostrar los resultados de optimización en RecyclerView
 * Con manejo mejorado de saltos de línea
 */
class ResultadoAdapter(
    private val context: Context,
    val resultados: MutableList<VarillaResultado>,
    private val onResultadosCambiados: ((List<VarillaResultado>) -> Unit)? = null
) : RecyclerView.Adapter<ResultadoAdapter.ResultadoViewHolder>() {

    @SuppressLint("LongLogTag")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResultadoViewHolder {
        Log.d("ResultadoOptimizacionDebug", "onCreateViewHolder llamado")
        val view = LayoutInflater.from(context).inflate(R.layout.item_resultado_varilla, parent, false)
        return ResultadoViewHolder(view)
    }

    @SuppressLint("LongLogTag")
    override fun onBindViewHolder(holder: ResultadoViewHolder, position: Int) {
        Log.d("ResultadoOptimizacionDebug", "onBindViewHolder posición: $position")
        Log.d("ResultadoOptimizacionDebug", "Datos varilla: longitud=${resultados[position].longitudVarilla}, cortes=${resultados[position].cortes.size}")
        holder.bind(resultados[position])
    }

    @SuppressLint("LongLogTag")
    override fun getItemCount(): Int {
        Log.d("ResultadoOptimizacionDebug", "getItemCount: ${resultados.size}")
        return resultados.size
    }

    inner class ResultadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTituloVarilla: TextView = itemView.findViewById(R.id.tvTituloVarilla)
        private val layoutCortes: LinearLayout = itemView.findViewById(R.id.layoutCortes)
        private val viewUtilizado: View = itemView.findViewById(R.id.viewUtilizado)
        private val viewRetazo: View = itemView.findViewById(R.id.viewRetazo)
        private val tvRetazo: TextView = itemView.findViewById(R.id.tvRetazo)
        private val tvEficiencia: TextView = itemView.findViewById(R.id.tvEficiencia)

        fun bind(varilla: VarillaResultado) {
            // Configurar click listener para toda la varilla
            itemView.setOnClickListener {
                // Toggle del estado cortada
                val posicion = bindingAdapterPosition
                if (posicion != RecyclerView.NO_POSITION) {
                    resultados[posicion] = resultados[posicion].copy(cortada = !resultados[posicion].cortada)
                    onResultadosCambiados?.invoke(resultados)
                    notifyItemChanged(posicion)
                }
            }

            // Cambiar background según estado
            if (varilla.cortada) {
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.verdet))
                itemView.alpha = 0.7f
            } else {
                itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))
                itemView.alpha = 1.0f
            }

            // Título: "x varillas de x cm"
            val longitudFormateada = formatearNumero(varilla.longitudVarilla)
            val textoVarillas = if (varilla.cantidadVarillas == 1) "varilla" else "varillas"
            tvTituloVarilla.text = "${varilla.cantidadVarillas} $textoVarillas de $longitudFormateada cm"

            // Limpiar cortes anteriores
            layoutCortes.removeAllViews()

            // MEJORADO: Crear layout con saltos de línea automáticos
            crearLayoutCortesConSaltosDeLinea(varilla.cortesConReferencias)

            // Configurar gráfico de barras
            configurarGraficoBarra(varilla)

            // Información del retazo
            val retazoFormateado = formatearNumero(varilla.retazo)
            val porcentajeRetazo = formatearNumero(varilla.porcentajeRetazo)
            tvRetazo.text = "Retazo: $retazoFormateado cm ($porcentajeRetazo%)"

            // Eficiencia
            val eficiencia = formatearNumero(varilla.porcentajeUtilizado)
            tvEficiencia.text = "$eficiencia%"

            // Color de eficiencia según porcentaje
            when {
                varilla.porcentajeUtilizado >= 90 -> {
                    tvEficiencia.setTextColor(ContextCompat.getColor(context, R.color.verde))
                    tvEficiencia.setBackgroundColor(ContextCompat.getColor(context, R.color.verdet))
                }
                varilla.porcentajeUtilizado >= 75 -> {
                    tvEficiencia.setTextColor(ContextCompat.getColor(context, R.color.naranja))
                    tvEficiencia.setBackgroundColor(ContextCompat.getColor(context, R.color.naranjat))
                }
                else -> {
                    tvEficiencia.setTextColor(ContextCompat.getColor(context, R.color.rojo))
                    tvEficiencia.setBackgroundColor(ContextCompat.getColor(context, R.color.rojot))
                }
            }
        }

        /**
         * Crea un ítem por corte en layout vertical — texto completo, sin truncamiento
         */
        private fun crearLayoutCortesConSaltosDeLinea(cortesConRefs: List<CorteConReferencia>) {
            val cortesAgrupados = agruparCortesConReferencias(cortesConRefs)
            layoutCortes.orientation = LinearLayout.VERTICAL

            cortesAgrupados.forEach { textoCorte ->
                val tv = TextView(context).apply {
                    text = textoCorte
                    textSize = 14f
                    setSingleLine(false)
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply { setMargins(0, 0, 0, 6) }

                    if (textoCorte.contains("CE")) {
                        setTextColor(ContextCompat.getColor(context, android.R.color.white))
                        setBackgroundColor(ContextCompat.getColor(context, R.color.naranja))
                        setTypeface(null, android.graphics.Typeface.BOLD)
                    } else {
                        setTextColor(ContextCompat.getColor(context, android.R.color.black))
                        background = ContextCompat.getDrawable(context, R.drawable.bg_corte_chip)
                    }
                    setPadding(12, 8, 12, 8)
                }
                layoutCortes.addView(tv)
            }
        }

        private fun configurarGraficoBarra(varilla: VarillaResultado) {
            val layoutParams = viewUtilizado.layoutParams as LinearLayout.LayoutParams
            val layoutParamsRetazo = viewRetazo.layoutParams as LinearLayout.LayoutParams

            // Calcular proporción (weight) basado en porcentajes
            val pesoUtilizado = varilla.porcentajeUtilizado
            val pesoRetazo = varilla.porcentajeRetazo

            layoutParams.weight = pesoUtilizado
            layoutParamsRetazo.weight = pesoRetazo

            viewUtilizado.layoutParams = layoutParams
            viewRetazo.layoutParams = layoutParamsRetazo

            // Si no hay retazo, ocultar la parte roja
            if (varilla.retazo <= 0) {
                viewRetazo.visibility = View.GONE
            } else {
                viewRetazo.visibility = View.VISIBLE
            }
        }

        private fun formatearNumero(numero: Float): String {
            return if (numero % 1.0f == 0.0f) {
                numero.toInt().toString()
            } else {
                "%.1f".format(numero).replace(",", ".")
            }
        }

        /**
         * CORREGIDA: Agrupa los cortes evitando textos largos que se corten
         * Siempre divide en elementos individuales para evitar truncamiento
         */
        private fun agruparCortesConReferencias(cortesConRefs: List<CorteConReferencia>): List<String> {
            // Agrupar por longitud primero
            val cortesAgrupadosPorLongitud = cortesConRefs.groupBy { it.longitud }

            val resultado = mutableListOf<String>()

            cortesAgrupadosPorLongitud.forEach { (longitud, cortesDeEstaLongitud) ->
                val longitudFormateada = formatearNumero(longitud)

                // Agrupar por referencia dentro de esta longitud
                val cortesAgrupadosPorRef = cortesDeEstaLongitud.groupBy { it.referencia }

                if (cortesAgrupadosPorRef.size == 1) {
                    // Todas las referencias son iguales para esta longitud
                    val referencia = cortesAgrupadosPorRef.keys.first()
                    val cantidad = cortesDeEstaLongitud.size

                    val textoCorte = if (cantidad > 1) {
                        "$longitudFormateada($referencia)*$cantidad"
                    } else {
                        "$longitudFormateada($referencia)"
                    }
                    resultado.add(textoCorte)

                } else {
                    // CORREGIDO: Siempre dividir múltiples referencias en elementos separados
                    // Esto evita completamente el problema del truncamiento
                    cortesAgrupadosPorRef.forEach { (referencia, cortesDeEstaRef) ->
                        val cantidad = cortesDeEstaRef.size
                        val textoIndividual = if (cantidad > 1) {
                            "$longitudFormateada($referencia)*$cantidad"
                        } else {
                            "$longitudFormateada($referencia)"
                        }
                        resultado.add(textoIndividual)
                    }
                }
            }

            return resultado
        }
    }
}
