package crystal.crystal.taller

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

/**
 * Navegación de la cola de medidas DENTRO de una calculadora, sin depender del archivado.
 *
 * Antes la cola solo avanzaba al archivar (índice + 1), así que para llegar a la segunda medida
 * había que archivar la primera. Ahora un chip flotante muestra la posición ("≡ 2/8") y abre la
 * lista completa del paquete, desde donde se puede:
 *
 * - abrir cualquier medida (si es de otra calculadora, se cierra esta y se abre la que corresponde);
 * - omitir una medida o reactivarla, sin archivarla;
 * - **editar** producto, ancho, alto y cantidad — el cambio de producto reenruta la medida a su
 *   calculadora (p. ej. pasar de Nova a Ventana Aluminio durante la venta).
 *
 * El chip se agrega con `addContentView`, así que ninguna calculadora necesita tocar su layout.
 * Con toque largo se oculta, por si estorba.
 *
 * Cada calculadora lo construye pasando cómo repuebla su pantalla con una medida ([cargarEnPantalla]).
 */
class NavegadorCola(
    private val activity: AppCompatActivity,
    private val claseActual: Class<out AppCompatActivity>,
    private val formato: (Float) -> String = { f ->
        if (f % 1f == 0f) f.toInt().toString() else String.format(Locale.US, "%.1f", f)
    },
    private val cargarEnPantalla: (ColaCalculadoras.MedidaCalc, Int) -> Unit
) {

    private var chip: TextView? = null

    private val activo: Boolean get() = ColaCalculadoras.desdeMedidas(activity)

    // ---------------- chip flotante ----------------

    /** Agrega el chip de posición si hay más de una medida en la cola. */
    @SuppressLint("SetTextI18n")
    fun instalarChip() {
        if (!activo || chip != null) return
        if (ColaCalculadoras.cola(activity).size <= 1) return
        val d = activity.resources.displayMetrics.density
        val tv = TextView(activity).apply {
            setTextColor(Color.WHITE)
            textSize = 13f
            setPadding((14 * d).toInt(), (8 * d).toInt(), (14 * d).toInt(), (8 * d).toInt())
            background = GradientDrawable().apply {
                cornerRadius = 24f * d
                setColor(Color.parseColor("#DD0099FF"))
            }
            setOnClickListener { mostrarLista() }
            setOnLongClickListener {
                visibility = View.GONE
                Toast.makeText(activity, "Lista de medidas oculta", Toast.LENGTH_SHORT).show()
                true
            }
        }
        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            Gravity.BOTTOM or Gravity.START
        ).apply {
            leftMargin = (12 * d).toInt()
            bottomMargin = (12 * d).toInt()
        }
        activity.addContentView(tv, lp)
        chip = tv
        actualizarChip()
    }

    @SuppressLint("SetTextI18n")
    fun actualizarChip() {
        val tv = chip ?: return
        val cola = ColaCalculadoras.cola(activity)
        val pendientes = cola.count { it.estado == ColaCalculadoras.EstadoCola.PENDIENTE }
        tv.text = "≡ ${ColaCalculadoras.indice(activity) + 1}/${cola.size}  ·  $pendientes pend."
    }

    // ---------------- siguiente medida (tras archivar) ----------------

    /** Llamar justo después de completar el archivado: marca la actual como hecha y ofrece la siguiente. */
    fun ofrecerSiguiente() {
        if (!activo) return
        val actual = ColaCalculadoras.indice(activity)
        val cola = ColaCalculadoras.marcar(
            ColaCalculadoras.cola(activity), actual, ColaCalculadoras.EstadoCola.HECHA
        )
        ColaCalculadoras.guardarCola(activity, cola)
        actualizarChip()

        val siguiente = ColaCalculadoras.siguientePendiente(cola, actual)
        if (siguiente < 0) {
            mostrarResumenFinal(cola)
            return
        }
        val item = cola[siguiente]
        val pendientes = cola.count { it.estado == ColaCalculadoras.EstadoCola.PENDIENTE }
        val mensaje = if (pendientes == 1) {
            "Tienes 1 medida pendiente: «${item.producto}» ${formato(item.ancho)} x ${formato(item.alto)}.\n¿Deseas abrirla?"
        } else {
            "Tienes $pendientes medidas pendientes. La siguiente es «${item.producto}» ${formato(item.ancho)} x ${formato(item.alto)}.\n¿Deseas abrirla?"
        }
        AlertDialog.Builder(activity)
            .setTitle("Siguiente medida")
            .setMessage(mensaje)
            .setPositiveButton("Abrir") { _, _ -> abrir(siguiente) }
            .setNeutralButton("Ver lista") { _, _ -> mostrarLista() }
            .setNegativeButton("No", null)
            .show()
    }

    private fun mostrarResumenFinal(cola: List<ColaCalculadoras.MedidaCalc>) {
        val hechas = cola.count { it.estado == ColaCalculadoras.EstadoCola.HECHA }
        val omitidas = cola.filter { it.estado == ColaCalculadoras.EstadoCola.OMITIDA }
        if (omitidas.isEmpty()) {
            Toast.makeText(activity, "Terminaste las $hechas medidas del paquete", Toast.LENGTH_LONG).show()
            return
        }
        val detalle = omitidas.joinToString("\n") {
            "• ${it.producto} ${formato(it.ancho)} x ${formato(it.alto)}"
        }
        AlertDialog.Builder(activity)
            .setTitle("Paquete terminado")
            .setMessage("Se calcularon $hechas de ${cola.size} medidas.\n\nQuedaron sin calcular:\n$detalle")
            .setPositiveButton("Entendido", null)
            .setNeutralButton("Ver lista") { _, _ -> mostrarLista() }
            .show()
    }

    // ---------------- lista del paquete ----------------

    fun mostrarLista() {
        if (!activo) return
        val cola = ColaCalculadoras.cola(activity)
        if (cola.isEmpty()) {
            Toast.makeText(activity, "No hay medidas en la cola", Toast.LENGTH_SHORT).show()
            return
        }
        val actual = ColaCalculadoras.indice(activity)
        val etiquetas = cola.mapIndexed { i, m -> etiqueta(i, m, actual) }.toTypedArray()
        AlertDialog.Builder(activity)
            .setTitle("Medidas del paquete")
            .setItems(etiquetas) { _, which -> mostrarAcciones(which) }
            .setNeutralButton("Siguiente pendiente") { _, _ ->
                val siguiente = ColaCalculadoras.siguientePendiente(cola, actual)
                if (siguiente < 0) {
                    Toast.makeText(activity, "No quedan medidas pendientes", Toast.LENGTH_SHORT).show()
                } else {
                    abrir(siguiente)
                }
            }
            .setPositiveButton("Cerrar", null)
            .show()
    }

    private fun etiqueta(i: Int, m: ColaCalculadoras.MedidaCalc, actual: Int): String {
        val marca = when {
            i == actual -> "→"
            m.estado == ColaCalculadoras.EstadoCola.HECHA -> "✓"
            m.estado == ColaCalculadoras.EstadoCola.OMITIDA -> "✕"
            else -> "○"
        }
        val producto = m.producto.ifBlank { "Sin producto" }
        return "$marca ${i + 1}. $producto — ${formato(m.ancho)} x ${formato(m.alto)}"
    }

    private fun mostrarAcciones(indice: Int) {
        val cola = ColaCalculadoras.cola(activity)
        val item = cola.getOrNull(indice) ?: return
        val omitida = item.estado == ColaCalculadoras.EstadoCola.OMITIDA
        val opciones = arrayOf(
            "Abrir esta medida",
            "Editar producto y medidas",
            if (omitida) "Reactivar (volver a pendiente)" else "Omitir esta medida"
        )
        AlertDialog.Builder(activity)
            .setTitle("${item.producto.ifBlank { "Medida" }} ${formato(item.ancho)} x ${formato(item.alto)}")
            .setItems(opciones) { _, cual ->
                when (cual) {
                    0 -> abrir(indice)
                    1 -> editar(indice)
                    2 -> cambiarEstado(indice, omitida)
                }
            }
            .setNegativeButton("Volver") { _, _ -> mostrarLista() }
            .show()
    }

    private fun cambiarEstado(indice: Int, estabaOmitida: Boolean) {
        val nuevo = if (estabaOmitida) ColaCalculadoras.EstadoCola.PENDIENTE
                    else ColaCalculadoras.EstadoCola.OMITIDA
        val cola = ColaCalculadoras.marcar(ColaCalculadoras.cola(activity), indice, nuevo)
        ColaCalculadoras.guardarCola(activity, cola)
        actualizarChip()
        // Si se omitió justo la que está en pantalla, no tiene sentido quedarse en ella.
        if (!estabaOmitida && indice == ColaCalculadoras.indice(activity)) {
            val siguiente = ColaCalculadoras.siguientePendiente(cola, indice)
            if (siguiente >= 0) {
                AlertDialog.Builder(activity)
                    .setTitle("Medida omitida")
                    .setMessage("¿Pasar a la siguiente pendiente?")
                    .setPositiveButton("Sí") { _, _ -> abrir(siguiente) }
                    .setNegativeButton("Quedarme aquí", null)
                    .show()
                return
            }
        }
        Toast.makeText(
            activity,
            if (estabaOmitida) "Medida reactivada" else "Medida omitida",
            Toast.LENGTH_SHORT
        ).show()
    }

    /** Abre la medida [indice]: repuebla esta pantalla, o cierra y abre la calculadora que le toca. */
    private fun abrir(indice: Int) {
        val cola = ColaCalculadoras.cola(activity)
        val item = cola.getOrNull(indice) ?: return
        val destino = EnrutadorPresupuesto.destinoPara(item.producto)
        if (destino == null) {
            Toast.makeText(
                activity,
                "«${item.producto}» no tiene calculadora: edita el producto",
                Toast.LENGTH_LONG
            ).show()
            editar(indice)
            return
        }
        if (destino == claseActual) {
            ColaCalculadoras.guardarIndice(activity, indice)
            cargarEnPantalla(item, indice)
            actualizarChip()
        } else {
            ColaCalculadoras.lanzar(activity, cola, indice)
            activity.finish()
        }
    }

    // ---------------- edición de una medida ----------------

    /**
     * Edita producto, ancho, alto y cantidad de una medida de la cola. Cambiar el producto la
     * reenruta: si pasa a corresponderle otra calculadora, se ofrece abrirla ahí.
     */
    private fun editar(indice: Int) {
        val cola = ColaCalculadoras.cola(activity)
        val item = cola.getOrNull(indice) ?: return
        val d = activity.resources.displayMetrics.density
        val pad = (16 * d).toInt()
        val contenedor = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad / 2, pad, 0)
        }

        fun campo(etiqueta: String, valor: String, decimal: Boolean): EditText {
            contenedor.addView(TextView(activity).apply {
                text = etiqueta
                textSize = 12f
            })
            val et = EditText(activity).apply {
                setText(valor)
                inputType = InputType.TYPE_CLASS_NUMBER or
                    (if (decimal) InputType.TYPE_NUMBER_FLAG_DECIMAL else 0)
            }
            contenedor.addView(et)
            return et
        }

        val nombres = EnrutadorPresupuesto.NOMBRES_CALCULADORAS
        contenedor.addView(TextView(activity).apply {
            text = "Producto"
            textSize = 12f
        })
        val spProducto = Spinner(activity).apply {
            adapter = ArrayAdapter(activity, android.R.layout.simple_spinner_dropdown_item, nombres)
            // Preseleccionar el que enruta igual que el producto actual.
            val destinoActual = EnrutadorPresupuesto.destinoPara(item.producto)
            val pos = nombres.indexOfFirst { EnrutadorPresupuesto.destinoPara(it) == destinoActual }
            if (pos >= 0) setSelection(pos)
        }
        contenedor.addView(spProducto)

        val etAncho = campo("Ancho (cm)", formato(item.ancho), true)
        val etAlto = campo("Alto (cm)", formato(item.alto), true)
        val etCantidad = campo("Cantidad", formato(item.cantidad), true)

        AlertDialog.Builder(activity)
            .setTitle("Editar medida ${indice + 1}")
            .setView(contenedor)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevo = item.copy(
                    producto = spProducto.selectedItem?.toString().orEmpty().ifBlank { item.producto },
                    ancho = etAncho.text.toString().replace(",", ".").toFloatOrNull() ?: item.ancho,
                    alto = etAlto.text.toString().replace(",", ".").toFloatOrNull() ?: item.alto,
                    cantidad = etCantidad.text.toString().replace(",", ".").toFloatOrNull() ?: item.cantidad
                )
                guardarEdicion(indice, nuevo)
            }
            .setNegativeButton("Cancelar") { _, _ -> mostrarLista() }
            .show()
    }

    private fun guardarEdicion(indice: Int, nuevo: ColaCalculadoras.MedidaCalc) {
        val cola = ColaCalculadoras.reemplazar(ColaCalculadoras.cola(activity), indice, nuevo)
        ColaCalculadoras.guardarCola(activity, cola)
        actualizarChip()

        val destino = EnrutadorPresupuesto.destinoPara(nuevo.producto)
        val esActual = indice == ColaCalculadoras.indice(activity)
        when {
            destino == claseActual && esActual -> {
                cargarEnPantalla(nuevo, indice)
                Toast.makeText(activity, "Medida actualizada", Toast.LENGTH_SHORT).show()
            }
            destino == null -> Toast.makeText(
                activity, "«${nuevo.producto}» no tiene calculadora asociada", Toast.LENGTH_LONG
            ).show()
            destino != claseActual -> AlertDialog.Builder(activity)
                .setTitle("Cambió de calculadora")
                .setMessage("«${nuevo.producto}» se calcula en otra pantalla.\n¿Abrirla ahora?")
                .setPositiveButton("Abrir") { _, _ -> abrir(indice) }
                .setNegativeButton("Después", null)
                .show()
            else -> Toast.makeText(activity, "Medida actualizada", Toast.LENGTH_SHORT).show()
        }
    }
}
