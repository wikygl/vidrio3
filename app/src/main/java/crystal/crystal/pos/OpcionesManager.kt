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
 * Aquí se apunta cada opción con su nombre y su precio; el ítem no cambia de medida. Y cuando el
 * cliente escoge, aquí se marca la escogida: desde ahí la proforma que suma y el contrato salen
 * con ella.
 *
 * Las filas van con la letra de la proforma de elección —A, B, C…— y no se mueven al elegir, para
 * que "me quedo con la B" sea siempre la misma.
 */
class OpcionesManager(
    private val activity: Activity,
    private val lista: MutableList<Listado>
) {
    var onListaModificada: (() -> Unit)? = null

    /**
     * Le pide a la pantalla que abra el anexador para la opción [cual] del ítem [posicion] (0 es
     * la A).
     *
     * La imagen se elige con lo de siempre —galería, base local o catálogo—, que vive en la
     * pantalla principal; aquí solo se dice cuál es la opción que la está esperando.
     */
    var alPedirImagen: ((posicion: Int, cual: Int) -> Unit)? = null

    /** Abre las opciones de ese ítem: las que tiene, para tocarlas, y una más para añadir. */
    fun mostrar(posicion: Int) {
        val item = lista.getOrNull(posicion) ?: return
        val todas = OpcionesDeProforma.todas(item)
        val elegida = OpcionesDeProforma.elegida(item)

        val filas = mutableListOf<String>()
        // La A es lo que se apuntó al medir; se toca como las demás: su material, su precio y su
        // imagen.
        todas.forEachIndexed { orden, o ->
            val marca = if (orden == elegida) "✔ " else "• "
            val conFoto = if (o.imagen.isNotBlank()) "  🖼" else ""
            filas.add(
                "$marca${OpcionesDeProforma.letra(orden)}. ${o.producto}  ·  " +
                    "${df2(OpcionesDeProforma.precioUnitario(item, o))} c/u$conFoto"
            )
        }
        val hayOpciones = todas.size > 1
        if (hayOpciones) filas.add("☑  Elegir la que escogió el cliente")
        filas.add("➕  Añadir otra opción")
        if (hayOpciones) filas.add("🗑  Quitar la última")

        AlertDialog.Builder(activity)
            .setTitle("Opciones de ${todas.first().producto}")
            .setItems(filas.toTypedArray()) { _, cual ->
                // Las filas van: las opciones, elegir, añadir y quitar.
                val iElegir = if (hayOpciones) todas.size else -1
                val iAnadir = if (hayOpciones) todas.size + 1 else todas.size
                val iQuitar = if (hayOpciones) todas.size + 2 else -1
                when (cual) {
                    iElegir -> pedirElegida(posicion)
                    iAnadir -> pedirOpcion(posicion, null)
                    iQuitar -> {
                        val quedan = todas.toMutableList()
                        quedan.removeAt(quedan.size - 1)
                        // Si la quitada era la escogida, el ítem se queda sin elegir.
                        OpcionesDeProforma.ponerTodas(
                            item, quedan, elegida?.takeIf { it < quedan.size }
                        )
                        onListaModificada?.invoke()
                        mostrar(posicion)
                    }
                    else -> pedirOpcion(posicion, cual)
                }
            }
            .setNegativeButton("Cerrar", null)
            .show()
    }

    /**
     * La opción que escogió el cliente. Pasa a ser el ítem: la proforma que suma, el total de la
     * lista y el contrato salen con ella. Se puede cambiar de idea o dejarla sin elegir.
     */
    private fun pedirElegida(posicion: Int) {
        val item = lista.getOrNull(posicion) ?: return
        val todas = OpcionesDeProforma.todas(item)
        val filas = todas.mapIndexed { orden, o ->
            "${OpcionesDeProforma.letra(orden)}. ${o.producto}  ·  " +
                "S/ ${df2(OpcionesDeProforma.precioPorLaCantidad(item, o))}"
        }
        AlertDialog.Builder(activity)
            .setTitle("¿Cuál escogió el cliente?")
            .setSingleChoiceItems(
                filas.toTypedArray(), OpcionesDeProforma.elegida(item) ?: -1
            ) { dialogo, cual ->
                dialogo.dismiss()
                OpcionesDeProforma.elegir(item, cual)
                onListaModificada?.invoke()
                Toast.makeText(
                    activity,
                    "Elegida la ${OpcionesDeProforma.letra(cual)}: ${todas[cual].producto}",
                    Toast.LENGTH_SHORT
                ).show()
                mostrar(posicion)
            }
            .setNeutralButton("Sin elegir") { _, _ ->
                OpcionesDeProforma.elegir(item, null)
                onListaModificada?.invoke()
                mostrar(posicion)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Pide el material y su precio. Con [cual] a null la opción es nueva; si no, es la de esa letra
     * (0 es la A).
     */
    private fun pedirOpcion(posicion: Int, cual: Int?) {
        val item = lista.getOrNull(posicion) ?: return
        val todas = OpcionesDeProforma.todas(item).toMutableList()
        val actual = cual?.let { todas.getOrNull(it) }
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

        /** Lo escrito, guardado en su sitio. Devuelve en qué letra quedó la opción. */
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
            // La imagen que ya tuviera se respeta: aquí se escriben el material y el precio. Y la
            // escogida sigue siendo la misma: si es esta, el ítem y su costo cambian con ella, con
            // la misma cuenta de siempre, para que la lista y la proforma no digan cosas distintas.
            val nueva = OpcionDeProforma(producto, precio, actual?.imagen.orEmpty())
            val donde = if (cual != null && cual in todas.indices) {
                todas[cual] = nueva; cual
            } else {
                todas.add(nueva); todas.size - 1
            }
            OpcionesDeProforma.ponerTodas(item, todas, OpcionesDeProforma.elegida(item))
            onListaModificada?.invoke()
            return donde
        }

        val tieneImagen = !actual?.imagen.isNullOrBlank()
        AlertDialog.Builder(activity)
            .setTitle(
                if (actual == null) "Otra opción"
                else "Cambiar la opción ${OpcionesDeProforma.letra(cual!!)}"
            )
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

    /**
     * Guarda la imagen elegida en la opción [cual] (0 es la A). La llama la pantalla al volver del
     * anexador. La de la opción que está en el ítem queda en el ítem, que es la que la proforma
     * que suma enseña en su recuadro.
     */
    fun ponerImagen(posicion: Int, cual: Int, imagen: String) {
        val item = lista.getOrNull(posicion) ?: return
        val todas = OpcionesDeProforma.todas(item).toMutableList()
        val opcion = todas.getOrNull(cual) ?: return
        todas[cual] = opcion.copy(imagen = imagen.trim())
        OpcionesDeProforma.ponerTodas(item, todas, OpcionesDeProforma.elegida(item))
        onListaModificada?.invoke()
        Toast.makeText(activity, "Imagen puesta en ${opcion.producto}", Toast.LENGTH_SHORT).show()
    }

    private fun df2(valor: Float): String = "%.2f".format(valor)
}
