package crystal.crystal.taller.mamparas

import android.app.Activity
import android.view.Gravity
import android.view.View
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
 * Devuelve el patrón en la forma `fcf|cf` — tramos separados por `|` — que es lo que
 * [MamparaModulos.patronManual] entiende. Cadena vacía = volver al automático.
 */
object DialogoDisenoMampara {

    /**
     * @param patronActual el que esté guardado, o vacío para partir del automático
     * @param divisiones   módulos que tiene hoy la mampara, para construir el punto de partida
     * @param onAplicar    recibe el patrón elegido, o "" si se pidió volver al automático
     */
    fun mostrar(
        act: Activity,
        patronActual: String,
        divisiones: Int,
        onAplicar: (String) -> Unit
    ) {
        val b = DialogDisenoMamparaBinding.inflate(act.layoutInflater)

        // Se parte de lo que haya guardado; si no hay nada, del automático, para que el editor
        // arranque en la disposición que el usuario ya está viendo y no en una en blanco.
        val tramos: MutableList<StringBuilder> =
            (MamparaModulos.patronManual(patronActual) ?: patronAutomatico(divisiones))
                .map { StringBuilder(it) }
                .toMutableList()

        val dlg = AlertDialog.Builder(act).setView(b.root).create()
        val d = act.resources.displayMetrics.density

        fun pintar() {
            b.dmContenedorTramos.removeAllViews()
            tramos.forEachIndexed { iTramo, pat ->
                b.dmContenedorTramos.addView(filaTramo(act, d, iTramo, tramos.size, pat) { pintar() })
            }
            val total = tramos.sumOf { it.length }
            val corr = tramos.sumOf { t -> t.count { it == 'c' } }
            b.dmResumen.text = "$total módulos · ${total - corr} fijos · $corr corredizas" +
                if (tramos.size > 1) " · ${tramos.size} tramos" else ""

            // Un tramo sin ningún módulo no se puede dibujar ni calcular: se avisa y se impide
            // aplicar, en vez de dejar que reviente más adelante.
            val vacio = tramos.any { it.isEmpty() }
            b.dmAviso.visibility = if (vacio) View.VISIBLE else View.GONE
            if (vacio) b.dmAviso.text = "Hay un tramo sin módulos. Añade uno o quita el tramo."
            b.dmAplicar.isEnabled = !vacio
            b.dmQuitarTramo.isEnabled = tramos.size > 1
        }

        b.dmAgregarTramo.setOnClickListener {
            // Un tramo nuevo empieza con un fijo: es lo mínimo que se sostiene solo.
            tramos.add(StringBuilder("f"))
            pintar()
        }
        b.dmQuitarTramo.setOnClickListener {
            if (tramos.size > 1) { tramos.removeAt(tramos.lastIndex); pintar() }
        }
        b.dmAuto.setOnClickListener { onAplicar(""); dlg.dismiss() }
        b.dmAplicar.setOnClickListener {
            onAplicar(tramos.joinToString("|") { it.toString() })
            dlg.dismiss()
        }

        pintar()
        dlg.show()
    }

    /** El patrón que saldría solo, para partir de él en vez de una mampara en blanco. */
    private fun patronAutomatico(divisiones: Int): List<String> {
        val d = MamparaPaflonDescriptor(
            ancho = 100f, alto = 100f, altoHoja = 100f,
            divisiones = divisiones.coerceAtLeast(1),
            bastidor = 0f, marco = 0f, nMochetas = 0
        )
        return MamparaModulos.desde(d).tramos.map { t -> t.map { it.tipo }.joinToString("") }
    }

    /** Una fila: los módulos del tramo, más los botones de añadir y quitar módulo. */
    private fun filaTramo(
        act: Activity,
        d: Float,
        indice: Int,
        totalTramos: Int,
        pat: StringBuilder,
        repintar: () -> Unit
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
        val lado = (44 * d).toInt()
        pat.forEachIndexed { i, c ->
            modulos.addView(TextView(act).apply {
                layoutParams = LinearLayout.LayoutParams(lado, lado).also {
                    it.setMargins((3 * d).toInt(), 0, (3 * d).toInt(), 0)
                }
                gravity = Gravity.CENTER
                text = if (c == 'c') "C" else "F"
                textSize = 16f
                setTypeface(null, android.graphics.Typeface.BOLD)
                // La corriza se distingue por color además de por la letra: en obra se mira rápido.
                setTextColor(if (c == 'c') 0xFF1565C0.toInt() else 0xFF37474F.toInt())
                setBackgroundResource(R.drawable.bg_opcion_seleccionada)
                setOnClickListener {
                    pat[i] = if (pat[i] == 'c') 'f' else 'c'
                    repintar()
                }
            })
        }
        modulos.addView(TextView(act).apply {
            layoutParams = LinearLayout.LayoutParams(lado, lado).also {
                it.setMargins((10 * d).toInt(), 0, 0, 0)
            }
            gravity = Gravity.CENTER
            text = "+"
            textSize = 20f
            setTextColor(0xFF1565C0.toInt())
            setOnClickListener { pat.append('f'); repintar() }
        })
        modulos.addView(TextView(act).apply {
            layoutParams = LinearLayout.LayoutParams(lado, lado)
            gravity = Gravity.CENTER
            text = "−"
            textSize = 20f
            setTextColor(if (pat.isEmpty()) 0xFFBBBBBB.toInt() else 0xFF1565C0.toInt())
            setOnClickListener { if (pat.isNotEmpty()) { pat.deleteCharAt(pat.lastIndex); repintar() } }
        })
        fila.addView(modulos)
        return fila
    }
}
