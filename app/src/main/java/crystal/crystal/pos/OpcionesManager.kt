package crystal.crystal.pos

import android.app.Activity
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import crystal.crystal.Listado

/**
 * Las opciones de un ítem, escritas a mano desde la lista del presupuesto.
 *
 * Es para la proforma de elección: la clínica pide el mismo producto en vidrio arenado laminado, en
 * policarbonato y en serie 80, y quiere ver los tres precios de la misma ventana para escoger uno.
 * Aquí se apunta cada opción con su nombre y su precio; el ítem no cambia de medida.
 */
class OpcionesManager(
    private val activity: Activity,
    private val lista: MutableList<Listado>
) {
    var onListaModificada: (() -> Unit)? = null

    /** Abre las opciones de ese ítem: las que tiene, para tocarlas, y una más para añadir. */
    fun mostrar(posicion: Int) {
        val item = lista.getOrNull(posicion) ?: return
        val opciones = OpcionesDeProforma.de(item).toMutableList()

        val filas = mutableListOf<String>()
        // La primera línea es lo que el ítem ya es: su producto y su precio. No se toca aquí —se
        // edita donde siempre—, pero se enseña porque en la proforma sale como una opción más.
        val propia = OpcionesDeProforma.comoEstaApuntado(item)
        filas.add("• ${propia.producto}  ·  ${df2(OpcionesDeProforma.precioUnitario(item, propia))} c/u")
        opciones.forEach { o ->
            filas.add("• ${o.producto}  ·  ${df2(OpcionesDeProforma.precioUnitario(item, o))} c/u")
        }
        filas.add("➕  Añadir otra opción")
        if (opciones.isNotEmpty()) filas.add("🗑  Quitar la última")

        AlertDialog.Builder(activity)
            .setTitle("Opciones de ${item.producto}")
            .setItems(filas.toTypedArray()) { _, cual ->
                // Las filas van: la del propio ítem, las opciones, añadir y quitar.
                val iAnadir = opciones.size + 1
                val iQuitar = if (opciones.isEmpty()) -1 else opciones.size + 2
                when (cual) {
                    // La primera es la del propio ítem: se edita en su diálogo de siempre.
                    0 -> Toast.makeText(
                        activity,
                        "Esa es la del ítem: se cambia en Editar",
                        Toast.LENGTH_SHORT
                    ).show()
                    iAnadir -> pedirOpcion(posicion, null)
                    iQuitar -> {
                        opciones.removeAt(opciones.size - 1)
                        OpcionesDeProforma.guardar(item, opciones)
                        onListaModificada?.invoke()
                        mostrar(posicion)
                    }
                    else -> pedirOpcion(posicion, cual - 1)
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    /** Pide el material y su precio. Con [cual] a null, la opción es nueva. */
    private fun pedirOpcion(posicion: Int, cual: Int?) {
        val item = lista.getOrNull(posicion) ?: return
        val opciones = OpcionesDeProforma.de(item).toMutableList()
        val actual = cual?.let { opciones.getOrNull(it) }
        val dp = activity.resources.displayMetrics.density

        val etProducto = EditText(activity).apply {
            hint = "Material (vidrio arenado laminado, policarbonato, serie 80…)"
            setText(actual?.producto ?: "")
        }
        val etPrecio = EditText(activity).apply {
            hint = "Precio por ${item.escala}"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(actual?.precio?.let { df2(it) } ?: "")
        }
        val cont = LinearLayout(activity).apply {
            orientation = LinearLayout.VERTICAL
            setPadding((20 * dp).toInt(), (12 * dp).toInt(), (20 * dp).toInt(), 0)
            addView(etProducto)
            addView(etPrecio)
        }

        AlertDialog.Builder(activity)
            .setTitle(if (actual == null) "Otra opción" else "Cambiar la opción")
            .setView(cont)
            .setPositiveButton("Aceptar") { _, _ ->
                val producto = etProducto.text?.toString()?.trim().orEmpty()
                val precio = etPrecio.text?.toString()?.replace(",", ".")?.toFloatOrNull()
                if (producto.isEmpty() || precio == null || precio <= 0f) {
                    Toast.makeText(
                        activity,
                        "La opción necesita su material y su precio",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }
                val nueva = OpcionDeProforma(producto, precio)
                if (cual != null && cual in opciones.indices) opciones[cual] = nueva
                else opciones.add(nueva)
                OpcionesDeProforma.guardar(item, opciones)
                onListaModificada?.invoke()
                mostrar(posicion)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun df2(valor: Float): String = "%.2f".format(valor)
}
