package crystal.crystal.taller.mamparas

import android.app.Activity
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
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
     * @param marcoInferiorActual si la mampara lleva hoy marco inferior (por defecto no lleva)
     * @param cantidadActual cuántas mamparas iguales lleva el producto
     * @param onAplicar    recibe el patrón elegido —o "" si se pidió volver al automático—, si
     *                     la mampara lleva marco inferior y la cantidad
     */
    fun mostrar(
        act: Activity,
        patronActual: String,
        divisiones: Int,
        anchoUtil: Float,
        bastidor: Float,
        marcoInferiorActual: Boolean,
        cantidadActual: Int,
        onAplicar: (String, Boolean, Int) -> Unit
    ) {
        val b = DialogDisenoMamparaBinding.inflate(act.layoutInflater)

        // Se parte de lo que haya guardado; si no hay nada, del automático, para que el editor
        // arranque en la disposición que el usuario ya está viendo y no en una en blanco.
        val tramos: MutableList<MutableList<ModuloPedido>> =
            (MamparaModulos.patronManual(patronActual) ?: patronAutomatico(divisiones))
                .map { it.toMutableList() }
                .toMutableList()

        b.dmMarcoInferior.isChecked = marcoInferiorActual
        b.dmCantidad.setText(cantidadActual.coerceAtLeast(1).toString())
        // En blanco o 0 vale 1, igual que en las demás calculadoras.
        fun leerCantidad(): Int =
            b.dmCantidad.text?.toString()?.trim()?.toIntOrNull()?.coerceAtLeast(1) ?: 1

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
        // El marco inferior no depende de la disposición: volver al automático tampoco lo desmarca.
        b.dmAuto.setOnClickListener {
            onAplicar("", b.dmMarcoInferior.isChecked, leerCantidad()); dlg.dismiss()
        }
        b.dmAplicar.setOnClickListener {
            onAplicar(MamparaModulos.serializarPatron(tramos), b.dmMarcoInferior.isChecked, leerCantidad())
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

    /**
     * Una fila por tramo: arriba los botones de añadir y quitar módulo, y debajo los módulos.
     *
     * Los botones van en su propia línea a propósito. Estaban al final de la fila de módulos y con
     * unos pocos se salían del diálogo, empujados por ellos: dejaban de verse y ya no había forma
     * de seguir editando. Aquí no se mueven nunca.
     *
     * Los módulos son cuadros de tamaño fijo dentro de un scroll horizontal. Se probó a repartir
     * el ancho por peso —quedaba bonito y mostraba la proporción real— pero con diez divisiones
     * cada cuadro se volvía tan estrecho que acertar al que se quería era una lotería. Aquí el
     * cuadro siempre mide lo mismo y se desplaza la fila.
     */
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
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, (8 * d).toInt(), 0, (8 * d).toInt())
        }

        // Cabecera: nombre del tramo a la izquierda, botones fijos a la derecha.
        val cabecera = LinearLayout(act).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        cabecera.addView(TextView(act).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = if (totalTramos > 1) "Tramo ${indice + 1}" else "Módulos"
            textSize = 12f
            setTextColor(0xFF888888.toInt())
        })
        fun botonModulo(simbolo: String, activo: Boolean, accion: () -> Unit) = TextView(act).apply {
            layoutParams = LinearLayout.LayoutParams((40 * d).toInt(), (36 * d).toInt())
                .also { it.setMargins((4 * d).toInt(), 0, 0, 0) }
            gravity = Gravity.CENTER
            text = simbolo
            textSize = 19f
            setTextColor(if (activo) 0xFF1565C0.toInt() else 0xFFBBBBBB.toInt())
            setBackgroundResource(R.drawable.bg_opcion_seleccionada)
            isEnabled = activo
            if (activo) setOnClickListener { accion() }
        }
        cabecera.addView(botonModulo("−", pat.size > 1) {
            pat.removeAt(pat.lastIndex); repintar()
        })
        cabecera.addView(botonModulo("+", true) {
            pat.add(ModuloPedido('f')); repintar()
        })
        fila.addView(cabecera)

        // Módulos dentro de un scroll horizontal: el cuadro no se encoge, se desplaza la fila.
        val modulos = LinearLayout(act).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        val ladoCelda = (58 * d).toInt()
        val tamLetra = 17f
        val tamMedida = 11f
        pat.forEachIndexed { i, m ->
            val celda = LinearLayout(act).apply {
                orientation = LinearLayout.VERTICAL
                // CENTER en los dos ejes: antes solo se centraba en horizontal y la letra y la
                // medida quedaban descolgadas contra el borde de arriba.
                gravity = Gravity.CENTER
                // Cuadrado y siempre del mismo tamaño: es lo que hace que se pueda acertar al que
                // se quiere por muchos módulos que haya.
                layoutParams = LinearLayout.LayoutParams(ladoCelda, ladoCelda)
                    .also { it.setMargins((3 * d).toInt(), 0, (3 * d).toInt(), 0) }
                setBackgroundResource(R.drawable.bg_opcion_seleccionada)
                setOnClickListener {
                    pat[i] = m.copy(tipo = if (m.tipo == 'c') 'f' else 'c')
                    repintar()
                }
                setOnLongClickListener { pedirAncho(m, pat, i); true }
            }
            celda.addView(TextView(act).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = Gravity.CENTER
                text = if (m.tipo == 'c') "C" else "F"
                textSize = tamLetra
                setTypeface(null, android.graphics.Typeface.BOLD)
                // La corrediza se distingue por color además de por la letra: en obra se mira rápido.
                setTextColor(if (m.tipo == 'c') 0xFF1565C0.toInt() else 0xFF37474F.toInt())
            })
            // Debajo, la medida: en azul y negrita si la fijó el usuario, tenue si le tocó.
            celda.addView(TextView(act).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = Gravity.CENTER
                val fijado = m.ancho != null
                text = fmt(m.ancho ?: anchoLibre)
                textSize = tamMedida
                maxLines = 1
                setTextColor(if (fijado) 0xFF1565C0.toInt() else 0xFF999999.toInt())
                if (fijado) setTypeface(null, android.graphics.Typeface.BOLD)
            })
            modulos.addView(celda)
        }
        fila.addView(HorizontalScrollView(act).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, (6 * d).toInt(), 0, 0) }
            isHorizontalScrollBarEnabled = true
            // Centrada mientras quepa entera: pegada a la izquierda, con dos o tres módulos queda
            // descolgada del resto del diálogo. En cuanto no cabe, el scroll manda.
            addView(
                modulos,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.CENTER_HORIZONTAL
                )
            )
        })
        return fila
    }
}
