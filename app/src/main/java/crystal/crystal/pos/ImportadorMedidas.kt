package crystal.crystal.pos

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import crystal.crystal.Listado
import crystal.crystal.R
import crystal.crystal.datos.DatabaseProvider
import crystal.crystal.datos.Product
import crystal.crystal.databinding.ActivityMainBinding
import crystal.crystal.red.interop.ChatInteropIntents
import crystal.crystal.red.interop.MeasuresMessageCodec
import crystal.crystal.red.interop.ParsedMeasuresMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImportadorMedidas(
    private val activity: AppCompatActivity,
    private val binding: ActivityMainBinding,
    private val obtenerLista: () -> MutableList<Listado>,
    private val actualizar: () -> Unit,
    private val conversor: (Float?) -> Float,
    private val calcPies: (Float, Float) -> Float,
    private val calcMetroCua: (Float, Float) -> Float,
    private val calcMLineales: (Float, Float) -> Float,
    private val calcMCubicos: (Float, Float, Float) -> Float
) {

    fun manejarMensajeMedidas() {
        val mensajeTexto = ChatInteropIntents.consumeStringExtra(
            intent = activity.intent,
            key = ChatInteropIntents.EXTRA_IMPORT_MEASURES_TEXT
        )

        if (mensajeTexto != null) {
            val parsedMeasures = MeasuresMessageCodec.parse(mensajeTexto)
            if (parsedMeasures != null) {
                importarMedidasParseadas(parsedMeasures)
            } else {
                Toast.makeText(activity, "No se pudo parsear el mensaje de medidas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("NewApi")
    private fun importarMedidasParseadas(parsedMeasures: ParsedMeasuresMessage) {
        if (parsedMeasures.items.isEmpty()) {
            Toast.makeText(activity, "No se encontraron medidas validas", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(activity)
            .setTitle("Importar Medidas")
            .setMessage(
                "Producto: ${parsedMeasures.productName}\n" +
                    "Elementos encontrados: ${parsedMeasures.items.size}\n" +
                    "Unidad: Pies cuadrados (p2)\n\n" +
                    "Ejemplos:\n" +
                    parsedMeasures.items.take(3).joinToString("\n") {
                        "${it.productName}: ${it.width} x ${it.height} = ${it.quantity}"
                    } +
                    if (parsedMeasures.items.size > 3) "\n..." else "" +
                    "\n\nDeseas buscar el precio en la base de datos?"
            )
            .setPositiveButton("Buscar precio") { _, _ ->
                buscarPrecioEnBaseDatos(parsedMeasures)
            }
            .setNegativeButton("Sin precio") { _, _ ->
                importarMedidasConPrecio(parsedMeasures, 0.0)
            }
            .setNeutralButton("Cancelar", null)
            .show()
    }

    private fun buscarPrecioEnBaseDatos(parsedMeasures: ParsedMeasuresMessage) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = DatabaseProvider.getInstance(activity)
                val productosEncontrados = db.productDao()
                    .searchProductsByDescription("%${parsedMeasures.productName}%")

                withContext(Dispatchers.Main) {
                    if (productosEncontrados.isNotEmpty()) {
                        mostrarOpcionesProductos(productosEncontrados, parsedMeasures)
                    } else {
                        Toast.makeText(
                            activity,
                            "No se encontraron productos similares en la base de datos",
                            Toast.LENGTH_SHORT
                        ).show()
                        importarMedidasConPrecio(parsedMeasures, 0.0)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "Error al buscar en BD: ${e.message}", Toast.LENGTH_SHORT).show()
                    importarMedidasConPrecio(parsedMeasures, 0.0)
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun mostrarOpcionesProductos(
        productos: List<Product>,
        parsedMeasures: ParsedMeasuresMessage
    ) {
        val opciones = mutableListOf<String>()
        productos.forEach { producto ->
            opciones += "${producto.description}\n   Precio: S/ ${String.format("%.2f", producto.price)}"
        }
        opciones += "Sin precio (S/ 0.00)"
        opciones += "Cancelar"

        AlertDialog.Builder(activity)
            .setTitle("Seleccionar Producto (${productos.size} encontrados)")
            .setItems(opciones.toTypedArray()) { _, which ->
                when {
                    which < productos.size -> {
                        val productoSeleccionado = productos[which]
                        Toast.makeText(
                            activity,
                            "Seleccionado: ${productoSeleccionado.description}",
                            Toast.LENGTH_SHORT
                        ).show()
                        importarMedidasConPrecio(parsedMeasures, productoSeleccionado.price)
                    }
                    which == productos.size -> {
                        Toast.makeText(activity, "Sin precio", Toast.LENGTH_SHORT).show()
                        importarMedidasConPrecio(parsedMeasures, 0.0)
                    }
                    else -> {
                        Toast.makeText(activity, "Cancelado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun importarMedidasConPrecio(parsedMeasures: ParsedMeasuresMessage, precio: Double) {
        val lista = obtenerLista()
        var elementosAgregados = 0

        for (item in parsedMeasures.items) {
            try {
                val unidadOriginal = binding.prTxt.text.toString()
                binding.prTxt.text = "Cent\u00edmetros"

                val piescua = calcPies(item.width, item.height)
                val metroscua = calcMetroCua(item.width, item.height)
                val ml = calcMLineales(item.width, item.height)
                val cub = calcMCubicos(item.width, item.height, 1f)
                val peri = ((conversor(item.width) * 2) + (conversor(item.height) * 2))

                binding.prTxt.text = unidadOriginal

                val costoTotal = (piescua * item.quantity) * precio.toFloat()

                val elemento = Listado(
                    escala = "p2",
                    uni = "Cent\u00edmetros",
                    medi1 = item.width,
                    medi2 = item.height,
                    medi3 = 1f,
                    canti = item.quantity,
                    piescua = piescua * item.quantity,
                    precio = precio.toFloat(),
                    costo = costoTotal,
                    producto = item.productName,
                    peri = peri,
                    metcua = metroscua,
                    metli = ml * item.quantity,
                    metcub = cub,
                    color = ContextCompat.getColor(activity, R.color.color),
                    uri = ""
                )

                lista.add(elemento)
                elementosAgregados++
            } catch (_: Exception) {
            }
        }

        if (elementosAgregados > 0) {
            actualizar()
            val mensajePrecio = if (precio > 0) {
                "con precio S/ ${String.format("%.2f", precio)}"
            } else {
                "sin precio (completar manualmente)"
            }
            Toast.makeText(activity, "$elementosAgregados elementos importados $mensajePrecio", Toast.LENGTH_LONG).show()

            if (precio == 0.0) {
                binding.precioEditxt.requestFocus()
            }
        } else {
            Toast.makeText(activity, "No se pudo importar ningun elemento", Toast.LENGTH_SHORT).show()
        }
    }
}
