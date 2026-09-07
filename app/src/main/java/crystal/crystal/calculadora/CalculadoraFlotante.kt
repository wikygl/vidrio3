package crystal.crystal.calculadora

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import crystal.crystal.R
import java.lang.ref.WeakReference

/**
 * Calculadora chica que flota sobre la pantalla de medición, con lo básico: sumar, restar,
 * multiplicar, dividir y paréntesis.
 *
 * Se abre tocando el título "Referencias y Cálculos" de cualquier calculadora de taller. No bloquea
 * la pantalla —se puede seguir escribiendo medidas con ella abierta— y se arrastra desde su barra
 * para dejarla donde no tape lo que se está mirando. El resultado se toca y se copia, que es lo que
 * casi siempre se hace con él.
 *
 * Las cuentas las hace [Expresion], el mismo evaluador de la calculadora grande (VendePapa): dos
 * evaluadores distintos acabarían dando dos resultados distintos para la misma cuenta.
 */
object CalculadoraFlotante {

    /**
     * La que esté abierta. Es débil a propósito: si la pantalla se cierra sin pasar por aquí, no
     * queda la Activity retenida por una referencia estática.
     */
    private var abierta: WeakReference<Dialog>? = null

    /**
     * Cuelga la calculadora del título "Referencias y Cálculos" de la pantalla. Las catorce
     * calculadoras de taller usan el mismo id para ese título, así que basta con buscarlo.
     */
    fun instalarEnReferencias(activity: Activity) {
        val titulo = activity.findViewById<View>(R.id.textView28) ?: return
        titulo.setOnClickListener { alternar(activity) }
    }

    /** Tocar el título otra vez la cierra: es el mismo gesto para abrir y para quitar de en medio. */
    fun alternar(activity: Activity) {
        val actual = abierta?.get()
        if (actual != null && actual.isShowing) {
            actual.dismiss()
            return
        }
        mostrar(activity)
    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    fun mostrar(activity: Activity) {
        if (activity.isFinishing || activity.isDestroyed) return
        val d = activity.resources.displayMetrics.density
        val vista = activity.layoutInflater.inflate(R.layout.dialog_calculadora_flotante, null)
        val expresion = vista.findViewById<TextView>(R.id.calcExpresion)
        val resultado = vista.findViewById<TextView>(R.id.calcResultado)
        val teclado = vista.findViewById<GridLayout>(R.id.calcTeclado)

        // La cuenta se sigue escribiendo aunque ya no quepa en la línea, así que la letra se
        // achica sola hasta que entre. Por debajo del mínimo manda el recorte por el principio que
        // pide el layout: lo que se acaba de teclear no se pierde de vista nunca.
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            resultado, 12, 28, 1, TypedValue.COMPLEX_UNIT_SP
        )
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            expresion, 9, 14, 1, TypedValue.COMPLEX_UNIT_SP
        )

        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(vista)
        dialog.setCanceledOnTouchOutside(false)

        val ancho = (280 * d).toInt()
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // Sin foco ni oscurecido: la pantalla de atrás sigue viva, con su teclado y sus campos,
            // que es de lo que se trata —calcular una medida y escribirla sin cerrar nada—.
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
            val lp = attributes
            lp.width = ancho
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT
            lp.gravity = Gravity.TOP or Gravity.START
            lp.dimAmount = 0f
            lp.x = (activity.resources.displayMetrics.widthPixels - ancho - 12 * d).toInt().coerceAtLeast(0)
            lp.y = (activity.resources.displayMetrics.heightPixels * 0.28f).toInt()
            attributes = lp
        }

        // ===== Estado: lo que se está escribiendo =====
        var actual = ""

        fun pintar() {
            resultado.text = if (actual.isEmpty()) "0" else actual
            // Avance del resultado mientras se escribe: si la cuenta ya se entiende, se ve a cuánto
            // va. Si aún no cierra un paréntesis o falta un número, no se muestra nada.
            val valor = if (actual.isBlank()) null else Expresion.evaluarONulo(actual)
            expresion.text = if (valor != null && actual.any { Expresion.esOperador(it) || it == '(' }) {
                "= ${Expresion.formatear(valor)}"
            } else {
                ""
            }
        }

        fun digito(s: String) {
            actual = if (actual == "0") s else actual + s
            pintar()
        }

        fun punto() {
            val segmento = actual.takeLastWhile { !Expresion.esOperador(it) && it != '(' && it != ')' }
            if (!segmento.contains(".")) {
                actual += if (segmento.isEmpty()) "0." else "."
                pintar()
            }
        }

        fun operador(op: String) {
            if (actual.isEmpty()) {
                if (op == "-") { actual = "-"; pintar() }
                return
            }
            val ultimo = actual.last()
            actual = if (Expresion.esOperador(ultimo) || ultimo == '.') actual.dropLast(1) + op else actual + op
            pintar()
        }

        fun parentesis(p: String) {
            if (p == "(") {
                // Un paréntesis pegado a un número multiplica: 3(4+1) es lo que se espera.
                val ultimo = actual.lastOrNull()
                actual += if (ultimo?.isDigit() == true || ultimo == ')') "*(" else "("
                pintar()
            } else if (Expresion.puedeCerrarParentesis(actual)) {
                actual += ")"
                pintar()
            }
        }

        fun igual() {
            if (actual.isBlank()) return
            val valor = runCatching { Expresion.evaluar(actual) }
                .onFailure { Toast.makeText(activity, it.message ?: "Operación inválida", Toast.LENGTH_SHORT).show() }
                .getOrNull() ?: return
            expresion.text = "$actual ="
            actual = Expresion.formatear(valor)
            resultado.text = actual
        }

        // ===== Teclado =====
        val teclas = listOf(
            Tecla("C", Tipo.MANDO) { actual = ""; expresion.text = ""; pintar() },
            Tecla("(", Tipo.OPERADOR) { parentesis("(") },
            Tecla(")", Tipo.OPERADOR) { parentesis(")") },
            Tecla("←", Tipo.MANDO) { if (actual.isNotEmpty()) { actual = actual.dropLast(1); pintar() } },
            Tecla("7", Tipo.DIGITO) { digito("7") },
            Tecla("8", Tipo.DIGITO) { digito("8") },
            Tecla("9", Tipo.DIGITO) { digito("9") },
            Tecla("÷", Tipo.OPERADOR) { operador("/") },
            Tecla("4", Tipo.DIGITO) { digito("4") },
            Tecla("5", Tipo.DIGITO) { digito("5") },
            Tecla("6", Tipo.DIGITO) { digito("6") },
            Tecla("×", Tipo.OPERADOR) { operador("*") },
            Tecla("1", Tipo.DIGITO) { digito("1") },
            Tecla("2", Tipo.DIGITO) { digito("2") },
            Tecla("3", Tipo.DIGITO) { digito("3") },
            Tecla("−", Tipo.OPERADOR) { operador("-") },
            Tecla("0", Tipo.DIGITO) { digito("0") },
            Tecla(".", Tipo.DIGITO) { punto() },
            Tecla("=", Tipo.IGUAL) { igual() },
            Tecla("+", Tipo.OPERADOR) { operador("+") }
        )
        teclas.forEachIndexed { i, tecla ->
            teclado.addView(botón(activity, d, tecla), GridLayout.LayoutParams(
                GridLayout.spec(i / 4),
                GridLayout.spec(i % 4, 1f)
            ).apply {
                width = 0
                height = (46 * d).toInt()
                setMargins((3 * d).toInt(), (3 * d).toInt(), (3 * d).toInt(), (3 * d).toInt())
            })
        }

        // Tocar el resultado lo copia: de aquí sale casi siempre a un campo de medida.
        resultado.setOnClickListener {
            val texto = resultado.text?.toString().orEmpty()
            if (texto.isBlank()) return@setOnClickListener
            val cb = activity.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            cb?.setPrimaryClip(ClipData.newPlainText("resultado", texto))
            Toast.makeText(activity, "Copiado: $texto", Toast.LENGTH_SHORT).show()
        }

        vista.findViewById<View>(R.id.calcCerrar).setOnClickListener { dialog.dismiss() }

        // Arrastre desde la barra del título, para dejarla donde no tape lo que se mira.
        val barra = vista.findViewById<View>(R.id.calcBarra)
        var tocX = 0f
        var tocY = 0f
        var origenX = 0
        var origenY = 0
        barra.setOnTouchListener { _, e ->
            val ventana = dialog.window ?: return@setOnTouchListener false
            val lp = ventana.attributes
            when (e.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    tocX = e.rawX; tocY = e.rawY
                    origenX = lp.x; origenY = lp.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dm = activity.resources.displayMetrics
                    val alto = vista.height.takeIf { it > 0 } ?: (300 * d).toInt()
                    lp.x = (origenX + (e.rawX - tocX)).toInt().coerceIn(0, (dm.widthPixels - ancho).coerceAtLeast(0))
                    lp.y = (origenY + (e.rawY - tocY)).toInt().coerceIn(0, (dm.heightPixels - alto).coerceAtLeast(0))
                    ventana.attributes = lp
                }
            }
            true
        }

        // La ventana muere con la pantalla que la abrió: si no, al salir queda colgada y Android
        // avisa de una ventana filtrada.
        (activity as? LifecycleOwner)?.lifecycle?.addObserver(LifecycleEventObserver { _, evento ->
            if (evento == Lifecycle.Event.ON_DESTROY && dialog.isShowing) dialog.dismiss()
        })
        dialog.setOnDismissListener { abierta = null }

        pintar()
        dialog.show()
        abierta = WeakReference(dialog)
    }

    private enum class Tipo { DIGITO, OPERADOR, MANDO, IGUAL }

    private class Tecla(val texto: String, val tipo: Tipo, val accion: () -> Unit)

    private fun botón(activity: Activity, d: Float, tecla: Tecla): TextView =
        TextView(activity).apply {
            text = tecla.texto
            gravity = Gravity.CENTER
            textSize = if (tecla.tipo == Tipo.DIGITO) 19f else 18f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setBackgroundResource(R.drawable.bg_opcion_seleccionada)
            setTextColor(
                when (tecla.tipo) {
                    Tipo.DIGITO -> Color.rgb(38, 50, 56)
                    Tipo.OPERADOR -> Color.rgb(1, 49, 92)
                    Tipo.MANDO -> Color.rgb(198, 93, 7)
                    Tipo.IGUAL -> Color.rgb(21, 158, 194)
                }
            )
            isClickable = true
            setOnClickListener { tecla.accion() }
        }
}
