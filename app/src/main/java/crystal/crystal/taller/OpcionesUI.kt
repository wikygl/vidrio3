package crystal.crystal.taller

import android.app.Activity
import android.content.Context
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

/**
 * Piezas comunes del diálogo de opciones de las calculadoras. Están aquí para que el vidriero
 * encuentre lo mismo en el mismo sitio en todas: si cada pantalla arma su propio flujo, hay que
 * aprender uno por calculadora.
 */
object OpcionesUI {

    /**
     * Fila "Cantidad": cuántas piezas iguales lleva el producto. Al archivar se guarda una copia
     * por unidad, cada una con su número correlativo, así que el material queda multiplicado.
     *
     * Devuelve la vista y la función que lee lo escrito; en blanco o 0 vale 1.
     */
    fun filaCantidad(context: Context, valorInicial: Int): Pair<View, () -> Int> {
        val dens = context.resources.displayMetrics.density
        val fila = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, (12 * dens).toInt())
        }
        fila.addView(TextView(context).apply {
            text = "Cantidad"
            textSize = 13f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
        })
        val et = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            setText(valorInicial.coerceAtLeast(1).toString())
            setEms(3)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { marginStart = (12 * dens).toInt() }
        }
        fila.addView(et)
        fila.addView(TextView(context).apply {
            text = "iguales, numeradas seguidas"
            textSize = 11f
            layoutParams = LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
            ).apply { marginStart = (12 * dens).toInt() }
        })
        return fila to {
            et.text?.toString()?.trim()?.toIntOrNull()?.coerceAtLeast(1) ?: 1
        }
    }

    /**
     * Diálogo de opciones estándar: la fila de cantidad arriba y debajo lo que aporte cada
     * calculadora. [onAceptar] recibe la cantidad leída.
     */
    fun mostrar(
        activity: Activity,
        titulo: String,
        cantidadInicial: Int,
        contenido: View? = null,
        onAceptar: (cantidad: Int) -> Unit
    ) {
        val dens = activity.resources.displayMetrics.density
        val pad = (16 * dens).toInt()
        val (filaCantidad, leerCantidad) = filaCantidad(activity, cantidadInicial)
        val cont = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, 0)
            addView(filaCantidad)
            if (contenido != null) addView(contenido)
        }
        AlertDialog.Builder(activity)
            .setTitle(titulo)
            .setView(ScrollView(activity).apply { addView(cont) })
            .setPositiveButton("Listo") { _, _ -> onAceptar(leerCantidad()) }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
