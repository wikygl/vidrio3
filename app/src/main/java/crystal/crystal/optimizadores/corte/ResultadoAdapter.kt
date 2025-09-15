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
    val resultados: MutableList<VarillaResultado> // Hacer público para acceso desde Activity
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
                val posicion = adapterPosition
                if (posicion != RecyclerView.NO_POSITION) {
                    resultados[posicion] = resultados[posicion].copy(cortada = !resultados[posicion].cortada)
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
         * CORREGIDA: Crea layout de cortes SIN estiramiento vertical
         */
        private fun crearLayoutCortesConSaltosDeLinea(cortesConRefs: List<CorteConReferencia>) {
            // Agrupar cortes con referencias
            val cortesAgrupados = agruparCortesConReferencias(cortesConRefs)

            // Configurar layout principal como vertical
            layoutCortes.orientation = LinearLayout.VERTICAL

            // Variables para manejar las líneas
            var lineaActual: LinearLayout? = null
            var elementosEnLineaActual = 0
            val maxElementosPorLinea = 4 // FIJO: máximo 4 elementos por línea

            cortesAgrupados.forEachIndexed { indice, textoCorte ->
                // Si es el primer elemento o si ya tenemos el máximo por línea, crear nueva línea
                if (lineaActual == null || elementosEnLineaActual >= maxElementosPorLinea) {
                    // Crear nueva línea horizontal
                    lineaActual = LinearLayout(context).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT // CRÍTICO: Solo el contenido necesario
                        ).apply {
                            setMargins(0, 0, 0, 8) // Margen entre líneas
                        }
                        gravity = android.view.Gravity.START or android.view.Gravity.CENTER_VERTICAL // CRÍTICO: Centro vertical
                        setBaselineAligned(false) // CORREGIDO: Función correcta para no alinear por baseline
                    }
                    layoutCortes.addView(lineaActual)
                    elementosEnLineaActual = 0
                }

                // Crear TextView para el corte
                val vistaCorte = crearTextViewCorte(textoCorte)

                // Agregar el corte a la línea actual
                lineaActual?.addView(vistaCorte)
                elementosEnLineaActual++

                // Agregar separador si no es el último elemento Y no es el último de la línea
                val esUltimoElemento = indice == cortesAgrupados.size - 1
                val esUltimoDeLinea = elementosEnLineaActual >= maxElementosPorLinea

                if (!esUltimoElemento && !esUltimoDeLinea) {
                    val separador = crearTextViewSeparador()
                    lineaActual?.addView(separador)
                }
            }
        }

        /**
         * CORREGIDA: TextView que NO se estira verticalmente
         */
        private fun crearTextViewCorte(textoCorte: String): TextView {
            return TextView(context).apply {
                text = textoCorte
                textSize = 14f

                // NUEVO: Detectar referencias especiales y aplicar estilo diferente
                if (textoCorte.contains("(CE") || textoCorte.contains("CE")) {
                    // Estilo especial para cortes largos divididos
                    setTextColor(ContextCompat.getColor(context, android.R.color.white))
                    setBackgroundColor(ContextCompat.getColor(context, R.color.naranja)) // Fondo naranja para destacar
                    setTypeface(null, android.graphics.Typeface.BOLD) // Negrita para destacar
                } else {
                    // Estilo normal
                    setTextColor(ContextCompat.getColor(context, android.R.color.black))
                    background = ContextCompat.getDrawable(context, R.drawable.bg_corte_chip)
                }

                setPadding(12, 8, 12, 8)

                // CRÍTICO: LayoutParams que NO permiten estiramiento vertical
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // Ancho natural
                    LinearLayout.LayoutParams.WRAP_CONTENT  // ALTURA NATURAL - No se estira
                ).apply {
                    setMargins(0, 0, 8, 4)
                    weight = 0f // Sin peso para evitar cualquier estiramiento
                    gravity = android.view.Gravity.CENTER_VERTICAL // Centrado vertical pero sin estirarse
                }

                // IMPORTANTE: Configuraciones para evitar estiramiento vertical
                maxLines = 1 // Solo una línea de altura
                isSingleLine = true // Confirmar una sola línea
                gravity = android.view.Gravity.CENTER // Texto centrado dentro del TextView

                // Altura mínima y máxima para evitar estiramientos
                minimumHeight = 0
                val densidad = context.resources.displayMetrics.density
                maxHeight = (40 * densidad).toInt() // Máximo 40dp
            }
        }

        /**
         * CORREGIDA: Separador que NO se estira verticalmente
         */
        private fun crearTextViewSeparador(): TextView {
            return TextView(context).apply {
                text = "+"
                textSize = 12f
                setTextColor(ContextCompat.getColor(context, R.color.gris))

                // CRÍTICO: LayoutParams sin estiramiento vertical
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT // ALTURA NATURAL
                ).apply {
                    setMargins(4, 0, 4, 4)
                    weight = 0f // Sin peso
                    gravity = android.view.Gravity.CENTER_VERTICAL // Centrado pero sin estirarse
                }

                // Configuraciones adicionales para evitar estiramiento
                gravity = android.view.Gravity.CENTER
                maxLines = 1
                isSingleLine = true
                val densidad = context.resources.displayMetrics.density
                maxHeight = (30 * densidad).toInt() // Máximo 30dp
            }
        }

        /**
         * Estima el ancho aproximado del texto en píxeles
         */
        private fun estimarAnchoTexto(texto: String): Int {
            // Estimación aproximada: 8-10 píxeles por carácter + padding + margins
            val caracteresPromedio = texto.length
            val anchoCaracter = 10 // píxeles aproximados por carácter
            val paddingHorizontal = 24 // 12 left + 12 right
            val marginHorizontal = 8 // margin right

            return (caracteresPromedio * anchoCaracter) + paddingHorizontal + marginHorizontal
        }

        /**
         * Obtiene el ancho máximo disponible por línea
         */
        private fun obtenerAnchoMaximoPorLinea(): Int {
            // Obtener ancho de la pantalla menos márgenes del layout padre
            val displayMetrics = context.resources.displayMetrics
            val anchoPantalla = displayMetrics.widthPixels

            // Restar márgenes típicos del RecyclerView y CardView (aproximado)
            val margenesLaterales = 32 * displayMetrics.density // 16dp left + 16dp right
            val paddingInterno = 32 * displayMetrics.density // padding interno del item

            return (anchoPantalla - margenesLaterales - paddingInterno).toInt()
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