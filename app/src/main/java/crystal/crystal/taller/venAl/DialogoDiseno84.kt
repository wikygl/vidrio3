package crystal.crystal.taller.venAl

import android.app.Activity
import android.graphics.Typeface
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
import crystal.crystal.databinding.DialogDiseno84Binding
import crystal.crystal.taller.mamparas.ModuloPedido
import crystal.crystal.taller.venAl.Calculo84.Franja
import crystal.crystal.taller.venAl.Calculo84.TramoPedido
import kotlin.math.abs

/**
 * Editor del diseño de la ventana serie 84, calcado del de la mampara paflón
 * (`DialogoDisenoMampara`): por tramo, las hojas y las franjas.
 *
 * - Hojas: un toque cambia fijo/corrediza; una pulsación larga fija su ancho. Las que no llevan
 *   ancho se reparten lo que sobra. Máximo 5 por tramo (N-19).
 * - Franjas, de abajo arriba como en Nova (N-33): las hojas (sistema) y las mochetas. Un toque fija
 *   su alto o quita la mocheta; las que no llevan alto se reparten el resto (hoja 5/7, N-29).
 * - Tramos: entre cada dos va un parante.
 *
 * Las medidas que muestra no se calculan aquí: salen de [Calculo84], el mismo cálculo que corta y
 * dibuja. Devuelve el diseño en la forma que [Calculo84.leerDiseno] entiende; cadena vacía = volver
 * al automático.
 */
object DialogoDiseno84 {

    private const val MAX_HOJAS = 5

    private class TramoEditable(val modulos: MutableList<ModuloPedido>, val franjas: MutableList<Franja>) {
        fun pedido() = TramoPedido(modulos.toList(), franjas.toList())
    }

    /**
     * @param base     la ventana con lo escrito en la pantalla; su `diseno` es el punto de partida
     * @param cantidadActual cuántas ventanas iguales lleva el producto
     * @param onAplicar recibe el diseño ("" = automático) y la cantidad
     */
    fun mostrar(
        act: Activity,
        base: Calculo84.Entrada,
        cantidadActual: Int,
        onAplicar: (String, Int) -> Unit
    ) {
        val b = DialogDiseno84Binding.inflate(act.layoutInflater)
        val d = act.resources.displayMetrics.density

        // Se parte de lo guardado o, si no hay nada, de lo que ya se ve (el automático): el editor
        // arranca en la ventana que el usuario tiene delante y no en una en blanco.
        val tramos: MutableList<TramoEditable> = (
            Calculo84.leerDiseno(base.diseno)
                ?: Calculo84.calcular(base.copy(diseno = "")).geometria.map { g ->
                    TramoPedido(
                        g.patron.map { ModuloPedido(it) },
                        if (g.llevaPuente) Calculo84.FRANJAS_NORMAL else listOf(Franja('s'))
                    )
                }
            ).map { TramoEditable(it.modulos.toMutableList(), it.franjas.toMutableList()) }.toMutableList()

        b.d84Cantidad.setText(cantidadActual.coerceAtLeast(1).toString())
        fun leerCantidad(): Int = b.d84Cantidad.text?.toString()?.trim()?.toIntOrNull()?.coerceAtLeast(1) ?: 1

        val dlg = AlertDialog.Builder(act).setView(b.root).create()
        fun diseno() = Calculo84.escribirDiseno(tramos.map { it.pedido() })

        lateinit var pintar: () -> Unit

        fun pedirMedida(titulo: String, mensaje: String, actual: Float?, quitar: (() -> Unit)?, fijar: (Float?) -> Unit) {
            val campo = EditText(act).apply {
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                hint = "cm"
                setText(actual?.let { fmt(it) } ?: "")
            }
            AlertDialog.Builder(act)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setView(campo)
                .setPositiveButton("Fijar") { _, _ ->
                    val v = campo.text.toString().replace(",", ".").toFloatOrNull()
                    fijar(if (v != null && v >= 0.1f) v else null); pintar()
                }
                .setNeutralButton("Libre") { _, _ -> fijar(null); pintar() }
                .apply {
                    if (quitar != null) setNegativeButton("Quitar") { _, _ -> quitar(); pintar() }
                    else setNegativeButton("Cancelar", null)
                }
                .show()
        }

        pintar = {
            val r = Calculo84.calcular(base.copy(diseno = diseno()))
            b.d84ContenedorTramos.removeAllViews()
            tramos.forEachIndexed { i, t ->
                b.d84ContenedorTramos.addView(
                    filaTramo(
                        act, d, i, tramos.size, t, r.geometria[i], r.altoUtil,
                        Calculo84.altoHoja(r.altoUtil, base.altoHoja), { pintar() }, ::pedirMedida
                    )
                )
            }

            b.d84Resumen.text = buildString {
                append("${r.divisiones} hojas · ${r.nFijos} fijos · ${r.nCorredizas} corredizas")
                if (tramos.size > 1) append(" · ${tramos.size} tramos")
            }

            // Se avisa ANTES de aplicar: una medida negativa llegaría a la lista de cortes.
            val franjasNoCuadran = tramos.indexOfFirst { t ->
                t.franjas.size > 1 && t.franjas.all { it.alto != null } &&
                    abs(t.franjas.sumOf { it.alto!!.toDouble() }.toFloat() +
                        (t.franjas.size - 1) * Calculo84.TUBO - r.altoUtil) > 0.15f
            }
            val sinPuente = tramos.indices.filter { r.geometria[it].franjas.size < tramos[it].franjas.size }
            val bloquea = r.aviso != null || franjasNoCuadran >= 0
            val aviso = when {
                r.aviso != null -> r.aviso
                franjasNoCuadran >= 0 ->
                    "Tramo ${franjasNoCuadran + 1}: todas las franjas tienen alto y no suman el alto útil " +
                        "(${fmt(r.altoUtil)}, con ${fmt(Calculo84.TUBO)} por puente). Deja alguna libre."
                sinPuente.isNotEmpty() ->
                    "En el tramo ${sinPuente.joinToString(", ") { "${it + 1}" }} una mocheta no cabe " +
                        "(la hoja llega al alto útil menos el puente): se quita con su puente."
                else -> ""
            }
            b.d84Aviso.visibility = if (aviso.isEmpty()) View.GONE else View.VISIBLE
            b.d84Aviso.text = aviso
            b.d84Aplicar.isEnabled = !bloquea
            b.d84QuitarTramo.isEnabled = tramos.size > 1
        }

        b.d84AgregarTramo.setOnClickListener {
            // Un tramo nuevo empieza como el de dos hojas que sale solo: fijo y corrediza.
            tramos.add(TramoEditable(mutableListOf(ModuloPedido('f'), ModuloPedido('c')), Calculo84.FRANJAS_NORMAL.toMutableList()))
            pintar()
        }
        b.d84QuitarTramo.setOnClickListener {
            if (tramos.size > 1) { tramos.removeAt(tramos.lastIndex); pintar() }
        }
        b.d84Auto.setOnClickListener { onAplicar("", leerCantidad()); dlg.dismiss() }
        b.d84Aplicar.setOnClickListener { onAplicar(diseno(), leerCantidad()); dlg.dismiss() }

        pintar()
        dlg.show()
    }

    private fun fmt(v: Float) = crystal.crystal.taller.nova.NovaCalculos.df1(v)

    private fun boton(act: Activity, d: Float, texto: String, activo: Boolean, ancho: Int, accion: () -> Unit) =
        TextView(act).apply {
            layoutParams = LinearLayout.LayoutParams(ancho, (36 * d).toInt())
                .also { it.setMargins((4 * d).toInt(), 0, 0, 0) }
            gravity = Gravity.CENTER
            text = texto
            textSize = if (texto.length == 1) 19f else 12f
            setTextColor(if (activo) 0xFF1565C0.toInt() else 0xFFBBBBBB.toInt())
            setBackgroundResource(R.drawable.bg_opcion_seleccionada)
            isEnabled = activo
            if (activo) setOnClickListener { accion() }
        }

    private fun etiqueta(act: Activity, texto: String) = TextView(act).apply {
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        text = texto
        textSize = 12f
        setTextColor(0xFF888888.toInt())
    }

    /**
     * Un bloque por tramo: las hojas en una fila (cuadros de tamaño fijo con scroll, como en la
     * mampara) y debajo las franjas apiladas, la de arriba arriba.
     */
    private fun filaTramo(
        act: Activity,
        d: Float,
        indice: Int,
        totalTramos: Int,
        t: TramoEditable,
        g: Calculo84.TramoResuelto,
        altoUtil: Float,
        hojaBase: Float,
        repintar: () -> Unit,
        pedirMedida: (String, String, Float?, (() -> Unit)?, (Float?) -> Unit) -> Unit
    ): View {
        val bloque = LinearLayout(act).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, (8 * d).toInt(), 0, (8 * d).toInt())
        }
        fun cabecera(vararg vistas: View) = LinearLayout(act).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            vistas.forEach { addView(it) }
        }

        // Hojas.
        val prefijo = if (totalTramos > 1) "Tramo ${indice + 1} · " else ""
        val chico = (40 * d).toInt()
        bloque.addView(cabecera(
            etiqueta(act, "${prefijo}hojas (ancho ${fmt(g.ancho)})"),
            boton(act, d, "−", t.modulos.size > 1, chico) { t.modulos.removeAt(t.modulos.lastIndex); repintar() },
            boton(act, d, "+", t.modulos.size < MAX_HOJAS, chico) { t.modulos.add(ModuloPedido('f')); repintar() }
        ))
        val fila = LinearLayout(act).apply { orientation = LinearLayout.HORIZONTAL }
        val lado = (58 * d).toInt()
        t.modulos.forEachIndexed { i, m ->
            val celda = LinearLayout(act).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(lado, lado).also { it.setMargins((3 * d).toInt(), 0, (3 * d).toInt(), 0) }
                setBackgroundResource(R.drawable.bg_opcion_seleccionada)
                setOnClickListener { t.modulos[i] = m.copy(tipo = if (m.tipo == 'c') 'f' else 'c'); repintar() }
                setOnLongClickListener {
                    pedirMedida(
                        if (m.tipo == 'c') "Ancho de la corrediza" else "Ancho del fijo",
                        "Es el ancho de la HOJA (con sus piernas). Si lo dejas libre, se reparte lo que " +
                            "sobre con las demás hojas que tampoco lleven medida.",
                        m.ancho, null
                    ) { v -> t.modulos[i] = m.copy(ancho = v) }
                    true
                }
            }
            celda.addView(TextView(act).apply {
                gravity = Gravity.CENTER
                text = if (m.tipo == 'c') "C" else "F"
                textSize = 17f
                setTypeface(null, Typeface.BOLD)
                setTextColor(if (m.tipo == 'c') 0xFF1565C0.toInt() else 0xFF37474F.toInt())
            })
            celda.addView(TextView(act).apply {
                gravity = Gravity.CENTER
                text = fmt(g.hojas.getOrElse(i) { 0f })
                textSize = 11f
                maxLines = 1
                val fijado = m.ancho != null
                setTextColor(if (fijado) 0xFF1565C0.toInt() else 0xFF999999.toInt())
                if (fijado) setTypeface(null, Typeface.BOLD)
            })
            fila.addView(celda)
        }
        bloque.addView(HorizontalScrollView(act).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, (6 * d).toInt(), 0, 0) }
            addView(fila, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL
            ))
        })

        // Franjas: la mocheta nueva entra arriba o abajo; el sistema siempre está.
        val ancho = (84 * d).toInt()
        bloque.addView(cabecera(
            etiqueta(act, "Franjas"),
            boton(act, d, "+ arriba", true, ancho) { t.franjas.add(Franja('m')); repintar() },
            boton(act, d, "+ abajo", true, ancho) { t.franjas.add(0, Franja('m')); repintar() }
        ).also { it.setPadding(0, (10 * d).toInt(), 0, 0) })
        // La mocheta que no cabe se quita (sale "no cabe"); las demás, con el alto que les toca.
        val altos = Calculo84.altosDeFranjas(t.franjas, altoUtil, hojaBase)
        for (k in t.franjas.indices.reversed()) {
            val f = t.franjas[k]
            val alto = altos[k]
            bloque.addView(TextView(act).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, (40 * d).toInt()
                ).also { it.setMargins(0, (4 * d).toInt(), 0, 0) }
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.bg_opcion_seleccionada)
                text = (if (f.esSistema) "Hojas · " else "Mocheta · ") + (alto?.let { fmt(it) } ?: "no cabe")
                textSize = 13f
                val fijado = f.alto != null
                setTextColor(if (fijado) 0xFF1565C0.toInt() else if (f.esSistema) 0xFF37474F.toInt() else 0xFF999999.toInt())
                if (fijado || f.esSistema) setTypeface(null, Typeface.BOLD)
                setOnClickListener {
                    if (f.esSistema) pedirMedida(
                        "Alto de hoja",
                        "Si lo dejas libre, vale el alto de hoja de la pantalla (o 5/7 del alto útil).",
                        f.alto, null
                    ) { v -> t.franjas[k] = f.copy(alto = v) }
                    else pedirMedida(
                        "Alto de la mocheta",
                        "Es el espacio de la mocheta, sin el puente. Si lo dejas libre, se reparte el " +
                            "resto con las demás mochetas libres.",
                        f.alto, { t.franjas.removeAt(k) }
                    ) { v -> t.franjas[k] = f.copy(alto = v) }
                }
            })
        }
        return bloque
    }

}
