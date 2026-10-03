package crystal.crystal.taller

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.InputType
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView





/**
 * Los lados de una ventana en lista (2026-10-03), para Medidas y para Nova: cada lado recto o curvo, y
 * entre cada dos su unión. Reemplaza a los campos fijos de cada geometría (lado 1, lado 2, la
 * esquina curva 1 y 2, la curva final): una curva es un lado más, con su casilla "Curvo".
 *
 * Las filas se arman con el mismo estilo que las de la pantalla (rótulo a la izquierda, casilla a la
 * derecha). Lo que se lee es una [VentanaMedida], la misma que llega de Medidas.
 */
class EditorDeLados(
    private val contenedor: LinearLayout,
    private val tinte: () -> ColorStateList?,
    private val colorTitulo: () -> Int,
    /** Se llama cuando cambia la cantidad de lados o alguno pasa a curvo o a recto. */
    private val alCambiar: () -> Unit = {}
) {
    private val contexto = contenedor.context
    private val dp = contexto.resources.displayMetrics.density

    private class Fila(
        val raiz: LinearLayout,
        val titulo: TextView,
        val curvo: CheckBox,
        val etiquetaAncho: TextView,
        val ancho: EditText,
        val alto: EditText,
        val puente: EditText,
        val divisiones: EditText,
        val lyCurva: LinearLayout,
        val flecha: EditText,
        val cuerda: EditText,
        val adentro: CheckBox,
        /** La unión con el lado de antes; no la tiene el primero. */
        val lyUnion: LinearLayout,
        val grados: EditText,
        val parante: CheckBox
    )

    private val filas = mutableListOf<Fila>()

    val cantidad: Int get() = filas.size

    fun limpiar() {
        filas.clear()
        contenedor.removeAllViews()
    }

    /** Pone en la lista los lados de una medida, con sus uniones. */
    fun cargar(v: VentanaMedida) {
        limpiar()
        v.lados.forEachIndexed { i, lado -> agregar(lado, lado.esCurvo, v.uniones.getOrNull(i - 1)) }
        alCambiar()
    }

    /**
     * Agrega un lado al final. Sin [union], la de siempre: entre dos rectos la escuadra (90); si uno
     * es curvo, sigue de largo (180), que es la curva tangente.
     */
    fun agregar(lado: LadoDeVentana? = null, curvo: Boolean = false, union: UnionDeLados? = null) {
        val anterior = filas.lastOrNull()
        val fila = nuevaFila()
        filas.add(fila)
        contenedor.addView(fila.raiz)
        fila.curvo.isChecked = curvo
        if (lado != null) {
            fila.ancho.setText(num(lado.ancho))
            fila.alto.setText(num(lado.alto))
            fila.puente.setText(if (lado.puente > 0f) num(lado.puente) else "")
            fila.divisiones.setText(lado.divisiones.toString())
            if (lado.esCurvo) {
                fila.flecha.setText(num(kotlin.math.abs(lado.flecha)))
                fila.cuerda.setText(lado.cuerda?.let { num(it) }.orEmpty())
                fila.adentro.isChecked = lado.flecha < 0f
            }
        } else if (anterior != null) {
            // El alto y el puente se repiten casi siempre: se proponen los del lado de antes.
            fila.alto.setText(anterior.alto.text)
            fila.puente.setText(anterior.puente.text)
        }
        val grados = union?.grados ?: if (curvo || anterior?.curvo?.isChecked == true) 180f else 90f
        fila.grados.setText(num(grados))
        fila.parante.isChecked = union?.parante ?: true
        actualizar()
        if (lado == null) alCambiar()
    }

    private fun quitar(fila: Fila) {
        if (filas.size <= 1) return
        filas.remove(fila)
        contenedor.removeView(fila.raiz)
        actualizar()
        alCambiar()
    }

    /** Numera los lados y muestra lo que corresponde a cada uno: curva, unión, parante. */
    private fun actualizar() {
        filas.forEachIndexed { i, f ->
            val curvo = f.curvo.isChecked
            f.titulo.text = if (curvo) "Lado ${i + 1} · curva" else "Lado ${i + 1}"
            f.etiquetaAncho.text = if (curvo) "Desarrollo" else "Ancho"
            f.lyCurva.visibility = if (curvo) View.VISIBLE else View.GONE
            f.lyUnion.visibility = if (i == 0) View.GONE else View.VISIBLE
            // El parante solo se puede quitar entre dos curvas (templado, 2026-10-03).
            val entreCurvas = i > 0 && curvo && filas[i - 1].curvo.isChecked
            f.parante.visibility = if (entreCurvas) View.VISIBLE else View.GONE
        }
    }

    /** El resultado de leer la lista: la medida, o qué falta. */
    data class Lectura(val medida: VentanaMedida?, val error: String?)

    fun leer(): Lectura {
        if (filas.isEmpty()) return Lectura(null, "Agregue al menos un lado")
        fun valor(et: EditText) = et.text?.toString()?.trim()?.replace(",", ".")?.toFloatOrNull()
        val lados = mutableListOf<LadoDeVentana>()
        filas.forEachIndexed { i, f ->
            val n = i + 1
            val ancho = valor(f.ancho)?.takeIf { it > 0f }
                ?: return Lectura(null, if (f.curvo.isChecked) "Lado $n: falta el desarrollo" else "Lado $n: falta el ancho")
            // Una curva sin alto o sin puente toma los del lado de al lado.
            val vecino = filas.getOrNull(i - 1) ?: filas.getOrNull(i + 1)
            val alto = valor(f.alto)?.takeIf { it > 0f } ?: vecino?.let { valor(it.alto) }?.takeIf { f.curvo.isChecked && it > 0f }
                ?: return Lectura(null, "Lado $n: falta el alto")
            val puente = valor(f.puente) ?: vecino?.let { valor(it.puente) }?.takeIf { f.curvo.isChecked } ?: 0f
            val divisiones = f.divisiones.text?.toString()?.trim()?.toIntOrNull()?.coerceAtLeast(0) ?: 0
            val flecha = if (f.curvo.isChecked) {
                val fl = flechaDe(ancho, valor(f.flecha), valor(f.cuerda))
                    ?: return Lectura(null, "Lado $n: la cuerda tiene que ser más corta que el desarrollo")
                if (f.adentro.isChecked) -fl else fl
            } else 0f
            lados.add(LadoDeVentana(ancho, alto, puente, divisiones, flecha))
        }
        val uniones = filas.drop(1).mapIndexed { k, f ->
            val n = k + 2
            val grados = valor(f.grados) ?: return Lectura(null, "Lado $n: falta el ángulo de la unión")
            UnionDeLados(grados, parante = f.parante.visibility != View.VISIBLE || f.parante.isChecked)
        }
        return Lectura(VentanaMedida(lados, uniones), null)
    }

    /**
     * La flecha de un lado curvo: la escrita, o la que sale de la cuerda. Sin ninguna de las dos, un
     * cuarto de círculo (como la esquina curva escrita a mano). Null si la cuerda no puede ser.
     */
    private fun flechaDe(desarrollo: Float, flecha: Float?, cuerda: Float?): Float? = when {
        flecha != null && flecha > 0f -> flecha
        cuerda != null && cuerda > 0f -> ArcoEsquina.deDesarrolloYCuerda(desarrollo, cuerda)?.flecha
        else -> {
            val radio = desarrollo / (Math.PI / 2).toFloat()
            radio * (1f - kotlin.math.cos(Math.PI / 4).toFloat())
        }
    }

    // ---------------------------------------------------------------- las filas

    private fun nuevaFila(): Fila {
        val raiz = LinearLayout(contexto).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        }

        // La unión con el lado de antes: su ángulo y, entre dos curvas, si lleva parante.
        val grados = casilla(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
        val parante = CheckBox(contexto).apply { text = "Parante"; textSize = 12f }
        val lyUnion = renglon("Unión (°)", grados).apply { addView(parante) }
        raiz.addView(lyUnion)

        // El título del lado, con su casilla "Curvo" y la ✕ para quitarlo.
        val titulo = TextView(contexto).apply {
            setTextColor(colorTitulo())
            setTypeface(typeface, Typeface.BOLD)
            setPadding((6 * dp).toInt(), (6 * dp).toInt(), 0, 0)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }
        val curvo = CheckBox(contexto).apply { text = "Curvo" }
        val quitar = TextView(contexto).apply {
            text = "✕"
            textSize = 18f
            setPadding((10 * dp).toInt(), 0, (6 * dp).toInt(), 0)
        }
        raiz.addView(LinearLayout(contexto).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            addView(titulo); addView(curvo); addView(quitar)
        })

        val decimal = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        val ancho = casilla(decimal)
        val lyAncho = renglon("Ancho", ancho)
        val alto = casilla(decimal)
        val puente = casilla(decimal)
        val divisiones = casilla(InputType.TYPE_CLASS_NUMBER).apply { setText("0") }
        raiz.addView(lyAncho)
        raiz.addView(renglon("Alto", alto))
        raiz.addView(renglon("h.Puente", puente))
        raiz.addView(renglon("Divisiones", divisiones))

        // Lo de la curva: flecha o cuerda (con una basta; sin ninguna, un cuarto de círculo).
        val flecha = casilla(decimal)
        val cuerda = casilla(decimal)
        val adentro = CheckBox(contexto).apply { text = "Panza hacia adentro" }
        val lyCurva = LinearLayout(contexto).apply {
            orientation = LinearLayout.VERTICAL
            addView(renglon("Flecha", flecha))
            addView(renglon("Cuerda", cuerda))
            addView(adentro)
        }
        raiz.addView(lyCurva)

        val fila = Fila(
            raiz, titulo, curvo, lyAncho.getChildAt(0) as TextView, ancho, alto, puente, divisiones,
            lyCurva, flecha, cuerda, adentro, lyUnion, grados, parante
        )
        curvo.setOnCheckedChangeListener { _, _ -> actualizar(); alCambiar() }
        quitar.setOnClickListener { quitar(fila) }
        return fila
    }

    /** Un renglón como los de la pantalla: el rótulo a la izquierda y la casilla a la derecha. */
    private fun renglon(rotulo: String, casilla: EditText): LinearLayout =
        LinearLayout(contexto).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            addView(TextView(contexto).apply {
                text = rotulo
                setPadding((6 * dp).toInt(), 0, 0, 0)
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 0.81f)
                    .apply { marginStart = (3 * dp).toInt() }
            })
            addView(casilla)
        }

    private fun casilla(tipo: Int): EditText = EditText(contexto).apply {
        inputType = tipo
        backgroundTintList = tinte()
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
    }

    private fun num(v: Float): String = crystal.crystal.taller.nova.NovaCalculos.df1(v)
}
