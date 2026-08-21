package crystal.crystal.pos

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import crystal.crystal.Listado

class EdicionMasivaManager(
    private val activity: Activity,
    private val lista: MutableList<Listado>
) {
    var onListaModificada: (() -> Unit)? = null

    fun mostrarMenuEdicionMasiva() {
        // Candado (Fase 3): la edición masiva es de pago.
        if (!crystal.crystal.Suscripcion.exigir(activity, crystal.crystal.Suscripcion.puedeModoMasivo(),
                "La edición masiva es una función de pago.")) return
        if (lista.isEmpty()) {
            Toast.makeText(activity, "No hay elementos para editar", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("🔧 Edición Masiva")

        val opciones = arrayOf(
            "📝 Editar precios por producto",
            "🏷️ Cambiar nombre de producto",
            "💰 Aplicar descuento general",
            "🔄 Recalcular todos los costos",
            "💰 Precio pactado"
        )

        builder.setItems(opciones) { _, which ->
            when (which) {
                0 -> mostrarEdicionPreciosPorProductoCompatible()
                1 -> mostrarCambioNombreProductoCompatible()
                2 -> mostrarAplicarDescuentoCompatible()
                3 -> recalcularTodosLosCostosCompatible()
                4 -> mostrarDialogoPrecioPactado()
            }
        }

        builder.setNegativeButton("❌ Cancelar", null)
        builder.show()
    }

    @SuppressLint("NewApi")
    private fun mostrarEdicionPreciosPorProductoCompatible() {
        val productosUnicos = agruparPorProducto()

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("💰 Editar Precios por Producto")

        val opcionesLista = mutableListOf<String>()
        val productosOrdenados = mutableListOf<String>()

        for ((producto, elementos) in productosUnicos) {
            val precioActual = elementos[0].precio
            val cantidad = elementos.size
            opcionesLista.add("🏷️ $producto\n💰 Precio: S/ ${formatearPrecio(precioActual)}\n📦 $cantidad elementos")
            productosOrdenados.add(producto)
        }

        builder.setItems(opcionesLista.toTypedArray()) { _, which ->
            val productoSeleccionado = productosOrdenados[which]
            val elementosDelProducto = productosUnicos[productoSeleccionado]!!
            mostrarDialogoEditarPrecioCompatible(productoSeleccionado, elementosDelProducto)
        }

        builder.setNegativeButton("🔙 Volver", null)
        builder.show()
    }

    private fun formatearPrecio(precio: Float): String {
        return "%.2f".format(precio)
    }

    private fun df2(defo: Float): String {
        return "%.2f".format(defo).replace(".", ",")
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarDialogoEditarPrecioCompatible(producto: String, elementos: List<Listado>) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("💰 Editar Precio")

        val precioActual = elementos[0].precio
        val mensaje = "🏷️ Producto: $producto\n" +
                "📦 Elementos: ${elementos.size}\n" +
                "💰 Precio actual: S/ ${formatearPrecio(precioActual)}\n\n" +
                "Ingresa el nuevo precio:"

        builder.setMessage(mensaje)

        val input = EditText(activity)
        input.setText(precioActual.toString())
        input.selectAll()
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        builder.setView(input)

        builder.setPositiveButton("✅ Aplicar") { _, _ ->
            val nuevoPrecio = input.text.toString().toFloatOrNull()
            if (nuevoPrecio != null && nuevoPrecio > 0) {
                aplicarNuevoPrecioCompatible(elementos, nuevoPrecio)
            } else {
                Toast.makeText(activity, "❌ Precio inválido", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("❌ Cancelar", null)
        builder.show()
        input.requestFocus()
    }

    private fun aplicarNuevoPrecioCompatible(elementos: List<Listado>, nuevoPrecio: Float) {
        var elementosActualizados = 0

        for (elemento in elementos) {
            var index = -1
            for (i in 0 until lista.size) {
                if (lista[i] === elemento) {
                    index = i
                    break
                }
            }

            if (index != -1) {
                lista[index].precio = nuevoPrecio
                lista[index].costo = calcularCosto(lista[index], nuevoPrecio)
                elementosActualizados++
            }
        }

        onListaModificada?.invoke()
        Toast.makeText(activity, "✅ $elementosActualizados elementos actualizados con precio S/ ${formatearPrecio(nuevoPrecio)}", Toast.LENGTH_LONG).show()
    }

    private fun mostrarCambioNombreProductoCompatible() {
        val productosUnicos = agruparPorProducto()

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("🏷️ Cambiar Nombre de Producto")

        val opcionesLista = mutableListOf<String>()
        val productosOrdenados = mutableListOf<String>()

        for ((producto, elementos) in productosUnicos) {
            opcionesLista.add("🏷️ $producto (${elementos.size} elementos)")
            productosOrdenados.add(producto)
        }

        builder.setItems(opcionesLista.toTypedArray()) { _, which ->
            val productoSeleccionado = productosOrdenados[which]
            val elementosDelProducto = productosUnicos[productoSeleccionado]!!
            mostrarDialogoCambiarNombreCompatible(productoSeleccionado, elementosDelProducto)
        }

        builder.setNegativeButton("🔙 Volver", null)
        builder.show()
    }

    private fun mostrarDialogoCambiarNombreCompatible(productoActual: String, elementos: List<Listado>) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("🏷️ Cambiar Nombre")
        builder.setMessage("Producto actual: $productoActual\nElementos: ${elementos.size}\n\nNuevo nombre:")

        val input = EditText(activity)
        input.setText(productoActual)
        input.selectAll()

        builder.setView(input)

        builder.setPositiveButton("✅ Cambiar") { _, _ ->
            val nuevoNombre = input.text.toString().trim()
            if (nuevoNombre.isNotEmpty()) {
                for (elemento in elementos) {
                    var index = -1
                    for (i in 0 until lista.size) {
                        if (lista[i] === elemento) {
                            index = i
                            break
                        }
                    }
                    if (index != -1) {
                        lista[index].producto = nuevoNombre
                    }
                }
                onListaModificada?.invoke()
                Toast.makeText(activity, "✅ Nombre cambiado a: $nuevoNombre", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "❌ Nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("❌ Cancelar", null)
        builder.show()
    }

    private fun mostrarAplicarDescuentoCompatible() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("💰 Aplicar Descuento General")
        builder.setMessage("Aplicar descuento a TODOS los elementos:\n\nIngresa el porcentaje de descuento:")

        val input = EditText(activity)
        input.hint = "Ejemplo: 10 (para 10%)"
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        builder.setView(input)

        builder.setPositiveButton("✅ Aplicar") { _, _ ->
            val porcentaje = input.text.toString().toFloatOrNull()

            if (porcentaje != null && porcentaje > 0 && porcentaje <= 100) {
                val factor = 1 - (porcentaje / 100)
                var elementosActualizados = 0

                for (i in 0 until lista.size) {
                    val nuevoPrecio = lista[i].precio * factor
                    lista[i].precio = nuevoPrecio
                    lista[i].costo = calcularCosto(lista[i], nuevoPrecio)
                    elementosActualizados++
                }

                onListaModificada?.invoke()
                Toast.makeText(activity, "✅ Descuento del $porcentaje% aplicado a $elementosActualizados elementos", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, "❌ Porcentaje inválido (debe ser entre 1 y 100)", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("❌ Cancelar", null)
        builder.show()
    }

    private fun recalcularTodosLosCostosCompatible() {
        for (i in 0 until lista.size) {
            lista[i].costo = calcularCosto(lista[i], lista[i].precio)
        }

        onListaModificada?.invoke()
        Toast.makeText(activity, "✅ Todos los costos recalculados", Toast.LENGTH_SHORT).show()
    }

    private fun mostrarDialogoPrecioPactado() {
        if (lista.isEmpty()) {
            Toast.makeText(activity, "No hay elementos para ajustar", Toast.LENGTH_SHORT).show()
            return
        }

        val costoTotalActual = lista.sumOf { it.costo.toDouble() }.toFloat()

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("💰 Precio Pactado")

        val mensaje = "💰 Costo actual: S/ ${df2(costoTotalActual)}\n\n" +
                "Ingresa el precio pactado con el cliente:"

        builder.setMessage(mensaje)

        val input = EditText(activity)
        input.hint = "Ejemplo: 1500.00"
        input.setText(costoTotalActual.toString())
        input.selectAll()
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL

        builder.setView(input)

        builder.setPositiveButton("✅ Aplicar") { _, _ ->
            val precioPactado = input.text.toString().toFloatOrNull()
            if (precioPactado != null && precioPactado > 0) {
                aplicarPrecioPactado(costoTotalActual, precioPactado)
            } else {
                Toast.makeText(activity, "❌ Precio inválido", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("❌ Cancelar", null)
        builder.show()
        input.requestFocus()
    }

    private fun aplicarPrecioPactado(costoActual: Float, precioPactado: Float) {
        val factor = precioPactado / costoActual

        val porcentajeDescuento = if (factor < 1) {
            ((1 - factor) * 100)
        } else {
            0f
        }

        var elementosActualizados = 0

        for (i in 0 until lista.size) {
            val nuevoPrecio = lista[i].precio * factor
            lista[i].precio = nuevoPrecio
            lista[i].costo = calcularCosto(lista[i], nuevoPrecio)
            elementosActualizados++
        }

        onListaModificada?.invoke()

        val mensaje = if (factor < 1) {
            "✅ Precio pactado: S/ ${df2(precioPactado)}\n" +
                    "📉 Descuento aplicado: ${df2(porcentajeDescuento)}%\n" +
                    "📦 $elementosActualizados elementos actualizados"
        } else {
            "✅ Precio pactado: S/ ${df2(precioPactado)}\n" +
                    "📈 Aumento aplicado: ${df2((factor - 1) * 100)}%\n" +
                    "📦 $elementosActualizados elementos actualizados"
        }

        Toast.makeText(activity, mensaje, Toast.LENGTH_LONG).show()
    }

    // --- Helpers ---

    private fun agruparPorProducto(): MutableMap<String, MutableList<Listado>> {
        val productosUnicos = mutableMapOf<String, MutableList<Listado>>()
        for (elemento in lista) {
            val producto = elemento.producto
            if (productosUnicos.containsKey(producto)) {
                productosUnicos[producto]!!.add(elemento)
            } else {
                productosUnicos[producto] = mutableListOf(elemento)
            }
        }
        return productosUnicos
    }

    private fun calcularCosto(item: Listado, precio: Float): Float {
        return when (item.escala) {
            "p2" -> item.piescua * precio
            "m2" -> item.metcua * precio
            "ml" -> item.metli * precio
            "m3" -> item.metcub * precio
            "uni" -> item.canti * precio
            else -> item.piescua * precio
        }
    }
}
