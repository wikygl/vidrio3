package crystal.crystal

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Gravity
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.TextView

class DialogoPulgadas(
    private val context: Context,
    private val onResult: (Float) -> Unit
) {
    private val fracciones = listOf(
        "0" to 0f,
        "1/16" to 0.0625f,
        "1/8" to 0.125f,
        "3/16" to 0.1875f,
        "1/4" to 0.25f,
        "5/16" to 0.3125f,
        "3/8" to 0.375f,
        "7/16" to 0.4375f,
        "1/2" to 0.5f,
        "9/16" to 0.5625f,
        "5/8" to 0.625f,
        "11/16" to 0.6875f,
        "3/4" to 0.75f,
        "13/16" to 0.8125f,
        "7/8" to 0.875f,
        "15/16" to 0.9375f
    )

    private var fraccionSeleccionada = 0f
    private var botonSeleccionado: Button? = null

    fun mostrar() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_pulgadas, null)
        val etEntero = view.findViewById<EditText>(R.id.et_entero)
        val gridFracciones = view.findViewById<GridLayout>(R.id.grid_fracciones)
        val tvResultado = view.findViewById<TextView>(R.id.tv_resultado)

        gridFracciones.columnCount = 4
        gridFracciones.rowCount = 4

        fun actualizarResultado() {
            val entero = etEntero.text.toString().toIntOrNull() ?: 0
            val total = entero + fraccionSeleccionada
            tvResultado.text = "Resultado: $total\""
        }

        // Crear botones de fracciones
        for ((label, valor) in fracciones) {
            val btn = Button(context).apply {
                text = label
                textSize = 12f
                isAllCaps = false
                val params = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f)
                    setGravity(Gravity.FILL_HORIZONTAL)
                }
                layoutParams = params

                setOnClickListener {
                    botonSeleccionado?.isSelected = false
                    botonSeleccionado?.alpha = 1.0f
                    fraccionSeleccionada = valor
                    isSelected = true
                    alpha = 0.6f
                    botonSeleccionado = this
                    actualizarResultado()
                }
            }
            // Seleccionar "0" por defecto
            if (valor == 0f) {
                btn.isSelected = true
                btn.alpha = 0.6f
                botonSeleccionado = btn
            }
            gridFracciones.addView(btn)
        }

        etEntero.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { actualizarResultado() }
        })

        etEntero.requestFocus()
        etEntero.imeOptions = EditorInfo.IME_ACTION_DONE

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .setPositiveButton("Aceptar") { _, _ ->
                val entero = etEntero.text.toString().toIntOrNull() ?: 0
                val total = entero + fraccionSeleccionada
                onResult(total)
            }
            .setNegativeButton("Cancelar", null)
            .create()

        etEntero.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val entero = etEntero.text.toString().toIntOrNull() ?: 0
                val total = entero + fraccionSeleccionada
                onResult(total)
                dialog.dismiss()
                true
            } else false
        }

        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }
}
