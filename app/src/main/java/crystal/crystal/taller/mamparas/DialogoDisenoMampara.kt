package crystal.crystal.taller.mamparas

import android.app.Activity
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import crystal.crystal.R
import crystal.crystal.databinding.DialogDisenoMamparaBinding

/**
 * Editor de la disposición de la mampara paflón.
 *
 * Se abre al tocar el diseño, igual que el diálogo de opciones de Nova, pero **edita** en vez de
 * elegir de un catálogo: la mampara admite combinaciones que ninguna tabla cubre (fcc, ccf, cc|fc…)
 * y `NovaCalculos.ordenDivis` solo devuelve una disposición por número de divisiones.
 *
 * Un toque cambia fijo/corrediza; una pulsación larga fija el ancho de ese módulo. Los que no
 * llevan medida se reparten lo que sobra, así que clavar las dos corredizas a 100 ensancha el fijo
 * del centro — que es de lo que se trata la mampara desigual.
 *
 * Devuelve el patrón en la forma `c<100>fc<100>|fc` que [MamparaModulos.patronManual] entiende.
 * Cadena vacía = volver al automático.
 */
object DialogoDisenoMampara {

    /**
     * @param patronActual el que esté guardado, o vacío para partir del automático
     * @param divisiones   módulos que tiene hoy la mampara, para construir el punto de partida
     * @param anchoUtil    hueco entre marcos (cm), para avisar si las medidas fijadas no caben
     * @param bastidor     ancho del bastidor (cm), que también come del hueco
     * @param onAplicar    recibe el patrón elegido, o "" si se pidió volver al automático
     */
    fun mostrar(
        act: Activity,
        patronActual: String,
        divisiones: Int,
        anchoUtil: Float,
        bastidor: Float,
        onAplicar: (String) -> Unit
    ) {
        val b = DialogDisenoMamparaBinding.inflate(act.layoutInflater)

        // Se parte de lo que haya guardado; si no hay nada, del automático, para que el editor
        // arranque en la disposición que el usuario ya está viendo y no en una en blanco.
        val tramos: MutableList<MutableList<ModuloPedido>> =
            (MamparaModulos.patronManual(patronActual) ?: patronAutomatico(divisiones))
                .map { it.toMutableList() }
                .toMutableList()

        val dlg = AlertDialog.Builder(act).setView(b.root).create()
        val d = act.resources.displayMetrics.density

        // Lo que sobra para los módulos sin medida fijada. Es el mismo reparto que hace
        // MamparaModulos: se replica aquí para poder avisar ANTES de aplicar.
        fun anchoLibre(): Float {
            val nBast = tramos.sumOf { bastVisibles(it) }
            val nPar = (tramos.size - 1).coerceAtLeast(0)
            val disponible = anchoUtil - nBast * bastidor - nPar * MamparaModulos.P_ALT
            val fijado = tramos.sumOf { t -> t.sumOf { (it.ancho ?: 0f).toDouble() } }.toFloat()
            val nLibres = tramos.sumOf { t -> t.count { it.ancho == null } }
            return if (nLibres > 0) (disponible - fijado) / nLibres else 0f
        }

        lateinit var pintar: () -> Unit

        /** Pulsación larga: fijar o soltar el ancho de un módulo. */
        fun pedirAncho(m: ModuloPedido, tramo: MutableList<ModuloPedido>, idx: Int) {
            val campo = EditText(act).apply {
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                hint = "Ancho del vidrio en cm"
                setText(m.ancho?.let { "%.1f".format(it).replace(",", ".") } ?: "")
            }
            AlertDialog.Builder(act)
                .setTitle(if (m.tipo == 'c') "Ancho de la corrediza" else "Ancho del fijo")
                .setMessage(
                    "Es el ancho del VIDRIO, sin bastidores.\n\n" +
                        "Si lo dejas vacío, este módulo se reparte lo que sobre junto con los demás " +
                        "que tampoco lleven medida."
                )
                .setView(campo)
                .setPositiveButton("Fijar") { _, _ ->
                    val v = campo.text.toString().replace(",", ".").toFloatOrNull()
                    tramo[idx] = m.copy(ancho = if (v != null && v > 0f) v else null)
                    pintar()
                }
                .setNeutralButton("Libre") { _, _ ->
                    tramo[idx] = m.copy(ancho = null)
                    pintar()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        pintar = {
            b.dmContenedorTramos.removeAllViews()
            val libre = anchoLibre()
            tramos.forEachIndexed { iTramo, pat ->
                b.dmContenedorTramos.addView(
                    filaTramo(act, d, iTramo, tramos.size, pat, libre, { pintar() }, ::pedirAncho)
                )
            }
            val total = tramos.sumOf { it.size }
            val corr = tramos.sumOf { t -> t.count { it.tipo == 'c' } }
            val nLibres = tramos.sumOf { t -> t.count { it.ancho == null } }
            b.dmResumen.text = buildString {
                append("$total módulos · ${total - corr} fijos · $corr corredizas")
                if (tramos.size > 1) append(" · ${tramos.size} tramos")
                if (nLibres > 0) append("\nLibres a ${fmt(libre)} cm cada uno")
            }

            // Se avisa ANTES de aplicar, no después: un vidrio de ancho cero o negativo llegaría a
            // la lista de cortes y de ahí al taller.
            val vacio = tramos.any { it.isEmpty() }
            val noCabe = nLibres > 0 && libre <= 0f
            val sinLibres = nLibres == 0
            val sobra = if (sinLibres) {
                val nBast = tramos.sumOf { bastVisibles(it) }
                val nPar = (tramos.size - 1).coerceAtLeast(0)
                val ocupado = tramos.sumOf { t -> t.sumOf { (it.ancho ?: 0f).toDouble() } }.toFloat() +
                    nBast * bastidor + nPar * MamparaModulos.P_ALT
                anchoUtil - ocupado
            } else 0f

            val aviso = when {
                vacio -> "Hay un tramo sin módulos. Añade uno o quita el tramo."
                noCabe -> "Las medidas fijadas ocupan todo el hueco: no queda ancho para los demás."
                sinLibres && kotlin.math.abs(sobra) > 0.15f ->
                    "Fijaste todos los módulos y " +
                        (if (sobra > 0) "sobran ${fmt(sobra)} cm." else "faltan ${fmt(-sobra)} cm.") +
                        " Deja alguno libre para que cuadre."
                else -> ""
            }
            b.dmAviso.visibility = if (aviso.isEmpty()) View.GONE else View.VISIBLE
            b.dmAviso.text = aviso
            b.dmAplicar.isEnabled = !vacio && !noCabe
            b.dmQuitarTramo.isEnabled = tramos.size > 1
        }

        b.dmAgregarTramo.setOnClickListener {
            // Un tramo nuevo empieza con un fijo: es lo mínimo que se sostiene solo.
            tramos.add(mutableListOf(ModuloPedido('f')))
            pintar()
        }
        b.dmQuitarTramo.setOnClickListener {
            if (tramos.size > 1) { tramos.removeAt(tramos.lastIndex); pintar() }
        }
        b.dmAuto.setOnClickListener { onAplicar(""); dlg.dismiss() }
        b.dmAplicar.setOnClickListener {
            onAplicar(MamparaModulos.serializarPatron(tramos))
            dlg.dismiss()
        }

        pintar()
        dlg.show()
    }

    private fun fmt(v: Float) = "%.1f".format(v).replace(",", ".")

    /** Bastidores visibles del tramo — el mismo criterio que el dibujo y el cálculo. */
    private fun bastVisibles(t: List<ModuloPedido>): Int {
        var b = 0
        for (i in 0 until t.size - 1) b += if (t[i].tipo != t[i + 1].tipo) 1 else 2
        if (t.firstOrNull()?.tipo == 'c') b += 1
        if (t.lastOrNull()?.tipo == 'c') b += 1
        return b
    }

    /** El patrón que saldría solo, para partir de él en vez de una mampara en blanco. */
    private fun patronAutomatico(divisiones: Int): List<List<ModuloPedido>> {
        val d = MamparaPaflonDescriptor(
            ancho = 100f, alto = 100f, altoHoja = 100f,
            divisiones = divisiones.coerceAtLeast(1),
            bastidor = 0f, marco = 0f, nMochetas = 0
        )
        return MamparaModulos.desde(d).tramos.map { t -> t.map { ModuloPedido(it.tipo) } }
    }

    /** Una fila: los módulos del tramo, más los botones de añadir y quitar módulo. */
    private fun filaTramo(
        act: Activity,
        d: Float,
        indice: Int,
        totalTramos: Int,
        pat: MutableList<ModuloPedido>,
        anchoLibre: Float,
        repintar: () -> Unit,
        pedirAncho: (ModuloPedido, MutableList<ModuloPedido>, Int) -> Unit
    ): View {
        val fila = LinearLayout(act).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, (6 * d).toInt(), 0, (6 * d).toInt())
        }
        if (totalTramos > 1) {
            fila.addView(TextView(act).apply {
                text = "Tramo ${indice + 1}"
                textSize = 12f
                setTextColor(0xFF888888.toInt())
            })
        }
        val modulos = LinearLayout(act).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val lado = (52 * d).toInt()
        pat.forEachIndexed { i, m ->
            val celda = LinearLayout(act).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(lado, LinearLayout.LayoutParams.WRAP_CONTENT)
                    .also { it.setMargins((3 * d).toInt(), 0, (3 * d).toInt(), 0) }
                setBackgroundResource(R.drawable.bg_opcion_seleccionada)
                setPadding(0, (6 * d).toInt(), 0, (6 * d).toInt())
                setOnClickListener {
                    pat[i] = m.copy(tipo = if (m.tipo == 'c') 'f' else 'c')
                    repintar()
                }
                setOnLongClickListener { pedirAncho(m, pat, i); true }
            }
            celda.addView(TextView(act).apply {
                text = if (m.tipo == 'c') "C" else "F"
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                // La corrediza se distingue por color además de por la letra: en obra se mira rápido.
                setTextColor(if (m.tipo == 'c') 0xFF1565C0.toInt() else 0xFF37474F.toInt())
            })
            // Debajo, la medida: en negrita si la fijó el usuario, tenue si es la que le tocó.
            celda.addView(TextView(act).apply {
                val fijado = m.ancho != null
                text = fmt(m.ancho ?: anchoLibre)
                textSize = 11f
                setTextColor(if (fijado) 0xFF1565C0.toInt() else 0xFF999999.toInt())
                if (fijado) setTypeface(null, android.graphics.Typeface.BOLD)
            })
            modulos.addView(celda)
        }
        modulos.addView(TextView(act).apply {
            layoutParams = LinearLayout.LayoutParams(lado, lado).also {
                it.setMargins((10 * d).toInt(), 0, 0, 0)
            }
            gravity = Gravity.CENTER
            text = "+"
            textSize = 20f
            setTextColor(0xFF1565C0.toInt())
            setOnClickListener { pat.add(ModuloPedido('f')); repintar() }
        })
        modulos.addView(TextView(act).apply {
            layoutParams = LinearLayout.LayoutParams(lado, lado)
            gravity = Gravity.CENTER
            text = "−"
            textSize = 20f
            setTextColor(if (pat.isEmpty()) 0xFFBBBBBB.toInt() else 0xFF1565C0.toInt())
            setOnClickListener { if (pat.isNotEmpty()) { pat.removeAt(pat.lastIndex); repintar() } }
        })
        fila.addView(modulos)
        return fila
    }
}
