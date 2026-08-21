package crystal.crystal.optimizadores.planchas

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import crystal.crystal.R

class ResultadoPlanchasAdapter(
    private val context: Context,
    val planchas: MutableList<PlanchaOptimizada>,
    private val onExportarPdf: ((PlanchaOptimizada) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val formatter = PlanchaFormatter()
    private var zoom = 1.0f

    // Cortes que NO caben en las planchas disponibles; se muestran como pie de la lista.
    private var faltantes: List<PiezaPlancha> = emptyList()

    fun setFaltantes(lista: List<PiezaPlancha>) {
        faltantes = lista
        notifyDataSetChanged()
    }

    private val hayFooter get() = faltantes.isNotEmpty()

    fun setZoom(nuevoZoom: Float) {
        zoom = nuevoZoom.coerceIn(0.75f, 2.0f)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        if (hayFooter && position == planchas.size) TIPO_FOOTER else TIPO_PLANCHA

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TIPO_FOOTER) return FooterVH(crearVistaFooter())
        val v = LayoutInflater.from(context).inflate(R.layout.item_plancha_resultado, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VH -> holder.bind(planchas[position])
            is FooterVH -> holder.bind()
        }
    }

    override fun getItemCount() = planchas.size + if (hayFooter) 1 else 0

    // ── Footer: título rojo + lista de faltantes en letra normal ──────────────
    private fun crearVistaFooter(): View {
        val cont = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            )
            val p = (16 * context.resources.displayMetrics.density).toInt()
            setPadding(p, p, p, p)
        }
        val titulo = TextView(context).apply {
            id = View.generateViewId()
            textSize = 15f
            setTextColor(ContextCompat.getColor(context, R.color.rojo))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }
        val cuerpo = TextView(context).apply {
            id = View.generateViewId()
            textSize = 13f
            setTextColor(Color.parseColor("#37474F"))
            val t = (4 * context.resources.displayMetrics.density).toInt()
            setPadding(0, t, 0, 0)
        }
        cont.addView(titulo)
        cont.addView(cuerpo)
        cont.tag = titulo.id to cuerpo.id
        return cont
    }

    inner class FooterVH(v: View) : RecyclerView.ViewHolder(v) {
        @Suppress("UNCHECKED_CAST")
        fun bind() {
            val (idT, idC) = itemView.tag as Pair<Int, Int>
            val titulo = itemView.findViewById<TextView>(idT)
            val cuerpo = itemView.findViewById<TextView>(idC)
            titulo.text = "⚠ ${faltantes.size} corte(s) NO caben en las planchas disponibles"
            cuerpo.text = faltantes
                .groupingBy {
                    val a = formatter.df1(it.anchoMm / 10f)
                    val b = formatter.df1(it.altoMm / 10f)
                    val nom = it.descripcion.trim()
                    "$a × $b cm${if (nom.isNotBlank()) "  ($nom)" else ""}"
                }
                .eachCount()
                .entries
                .joinToString("\n") { "• ${it.key} = ${it.value}" }
        }
    }

    companion object {
        private const val TIPO_PLANCHA = 0
        private const val TIPO_FOOTER = 1
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val tvNombre: TextView = v.findViewById(R.id.tvNombrePlancha)
        private val tvInfo: TextView = v.findViewById(R.id.tvInfoPlancha)
        private val tvEfic: TextView = v.findViewById(R.id.tvEficiencia)
        private val vistaCorte: PlanchaVistaCorte = v.findViewById(R.id.vistaCorte)
        private val scrollVistaCorte: HorizontalScrollView = v.findViewById(R.id.scrollVistaCorte)
        private val layoutLeyenda: LinearLayout = v.findViewById(R.id.layoutLeyenda)
        private val btnPdf: android.widget.Button = v.findViewById(R.id.btnPdfPlancha)

        fun bind(p: PlanchaOptimizada) {
            btnPdf.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onExportarPdf?.invoke(planchas[pos])
            }
            itemView.setOnClickListener {
                val posicion = bindingAdapterPosition
                if (posicion != RecyclerView.NO_POSITION) {
                    planchas[posicion] = planchas[posicion].copy(cortada = !planchas[posicion].cortada)
                    notifyItemChanged(posicion)
                }
            }

            val colorFondo = if (p.cortada) R.color.verdet else android.R.color.white
            (itemView as? CardView)?.setCardBackgroundColor(ContextCompat.getColor(context, colorFondo))
            itemView.alpha = if (p.cortada) 0.7f else 1.0f

            tvNombre.text = p.nombre

            val tipo = if (p.esRetazoEntrada) "retazo" else "plancha entera"
            val anchoC = formatter.df1(p.anchoMm / 10f)
            val altoC = formatter.df1(p.altoMm / 10f)
            tvInfo.text = "$anchoC × $altoC cm  •  ${p.cortes.size} pieza(s)  •  $tipo"

            val areaTotal = p.anchoMm.toLong() * p.altoMm.toLong()
            val efic = if (areaTotal > 0) (p.areaUsadaMm2 * 100f / areaTotal) else 0f
            val eficStr = "%.1f".format(efic).replace(",", ".")
            tvEfic.text = "$eficStr%"
            tvEfic.setTextColor(when {
                efic >= 90f -> ContextCompat.getColor(context, R.color.verde)
                efic >= 70f -> ContextCompat.getColor(context, R.color.naranja)
                else -> ContextCompat.getColor(context, R.color.rojo)
            })
            tvEfic.setBackgroundColor(when {
                efic >= 90f -> ContextCompat.getColor(context, R.color.verdet)
                efic >= 70f -> ContextCompat.getColor(context, R.color.naranjat)
                else -> ContextCompat.getColor(context, R.color.rojot)
            })

            // Vista visual con números
            val anchoBase = (context.resources.displayMetrics.widthPixels - dp(56)).coerceAtLeast(dp(220))
            vistaCorte.setPlancha(p)
            vistaCorte.setZoom(zoom, anchoBase)
            scrollVistaCorte.post { scrollVistaCorte.scrollTo(0, 0) }

            // Leyenda numerada
            layoutLeyenda.removeAllViews()
            if (p.cortes.isEmpty()) {
                layoutLeyenda.visibility = View.GONE
                return
            }
            layoutLeyenda.visibility = View.VISIBLE

            val paleta = vistaCorte.paleta
            val dp4 = dp(4)
            val dp6 = dp(6)
            val dp16 = dp(16)

            p.cortes.forEachIndexed { i, corte ->
                val fila = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = android.view.Gravity.CENTER_VERTICAL
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.bottomMargin = dp4
                    layoutParams = lp
                }

                // Cuadrado de color con número
                val tvNum = TextView(context).apply {
                    val size = dp(22)
                    layoutParams = LinearLayout.LayoutParams(size, size).also { it.marginEnd = dp6 }
                    gravity = android.view.Gravity.CENTER
                    text = (i + 1).toString()
                    textSize = 11f
                    setTextColor(Color.parseColor("#1A237E"))
                    setTypeface(null, android.graphics.Typeface.BOLD)
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.RECTANGLE
                        cornerRadius = dp(3).toFloat()
                        setColor(paleta[i % paleta.size])
                        setStroke(dp(1), Color.parseColor("#455A64"))
                    }
                }

                // Texto: descripción + medidas
                val anchoCCorte = formatter.df1(corte.anchoMm / 10f)
                val altoCCorte = formatter.df1(corte.altoMm / 10f)
                val rot = if (corte.rotada) " ↺" else ""
                val tvTexto = TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                    )
                    text = "${corte.descripcion}  —  $anchoCCorte × $altoCCorte cm$rot"
                    textSize = 12f
                    setTextColor(Color.parseColor("#37474F"))
                    setPadding(0, 0, dp16, 0)
                }

                fila.addView(tvNum)
                fila.addView(tvTexto)
                layoutLeyenda.addView(fila)
            }
        }

        private fun dp(v: Int) = (v * context.resources.displayMetrics.density).toInt()
    }
}
