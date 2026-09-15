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

    /**
     * Le pide a la pantalla que abra el anexador para la opción [cual] del ítem [posicion].
     *
     * La imagen se elige con lo de siempre —galería, base local o catálogo—, que vive en la
     * pantalla principal; aquí solo se dice cuál es la opción que la está esperando.
     */
    var alPedirImagen: ((posicion: Int, cual: Int) -> Unit)? = null

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
            val conFoto = if (o.imagen.isNotEmpty()) "  🖼" else ""
            filas.add("• ${o.producto}  ·  ${df2(OpcionesDeProforma.precioUnitario(item, o))} c/u$conFoto")
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

        /** Lo escrito, guardado en su sitio. Devuelve en qué posición quedó la opción. */
        fun guardarLoEscrito(): Int? {
            val producto = etProducto.text?.toString()?.trim().orEmpty()
            val precio = etPrecio.text?.toString()?.replace(",", ".")?.toFloatOrNull()
            if (producto.isEmpty() || precio == null || precio <= 0f) {
                Toast.makeText(
                    activity,
                    "La opción necesita su material y su precio",
                    Toast.LENGTH_LONG
                ).show()
                return null
            }
            // La imagen que ya tuviera se respeta: aquí se escriben el material y el precio.
            val nueva = OpcionDeProforma(producto, precio, actual?.imagen.orEmpty())
            val donde = if (cual != null && cual in opciones.indices) {
                opciones[cual] = nueva; cual
            } else {
                opciones.add(nueva); opciones.size - 1
            }
            OpcionesDeProforma.guardar(item, opciones)
            onListaModificada?.invoke()
            return donde
        }

        val tieneImagen = !actual?.imagen.isNullOrEmpty()
        AlertDialog.Builder(activity)
            .setTitle(if (actual == null) "Otra opción" else "Cambiar la opción")
            .setView(cont)
            .setPositiveButton("Aceptar") { _, _ ->
                if (guardarLoEscrito() != null) mostrar(posicion)
            }
            // La imagen va aparte porque se elige en otra pantalla: primero se guarda lo escrito
            // —si no, al volver ya no estaría— y después se abre el anexador.
            .setNeutralButton(if (tieneImagen) "Cambiar imagen" else "Imagen…") { _, _ ->
                val donde = guardarLoEscrito() ?: return@setNeutralButton
                alPedirImagen?.invoke(posicion, donde)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** Guarda la imagen elegida en esa opción. La llama la pantalla al volver del anexador. */
    fun ponerImagen(posicion: Int, cual: Int, imagen: String) {
        val item = lista.getOrNull(posicion) ?: return
        val opciones = OpcionesDeProforma.de(item).toMutableList()
        val opcion = opciones.getOrNull(cual) ?: return
        opciones[cual] = opcion.copy(imagen = imagen.trim())
        OpcionesDeProforma.guardar(item, opciones)
        onListaModificada?.invoke()
        Toast.makeText(activity, "Imagen puesta en ${opcion.producto}", Toast.LENGTH_SHORT).show()
    }

    private fun df2(valor: Float): String = "%.2f".format(valor)
}
