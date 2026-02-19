package crystal.crystal.pos

import android.annotation.SuppressLint
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

import crystal.crystal.Listado
import crystal.crystal.R
import crystal.crystal.databinding.ActivityMainBinding
import crystal.crystal.datos.DatabaseProvider
import crystal.crystal.datos.Product

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
        val mensajeTexto = activity.intent.getStringExtra("importar_medidas_texto")

        if (mensajeTexto != null) {
            val resultado = parsearMensajeMedidas(mensajeTexto)

            if (resultado != null) {
                val (producto, medidas) = resultado
                importarMedidasParseadas(producto, medidas)
            } else {
                Toast.makeText(activity, "No se pudo parsear el mensaje de medidas", Toast.LENGTH_SHORT).show()
            }

            activity.intent.removeExtra("importar_medidas_texto")
        }
    }

    private fun esFormatoMedidas(mensaje: String): Boolean {
        val lineas = mensaje.trim().split("\n").filter { it.isNotBlank() }

        if (lineas.size < 2) return false

        val regexMedida = Regex("""^\s*\d+(\.\d+)?\s*[xX]\s*\d+(\.\d+)?\s*=\s*\d+(\.\d+)?\s*$""")

        var primeraMedida = -1
        for (i in lineas.indices) {
            if (regexMedida.matches(lineas[i].trim())) {
                primeraMedida = i
                break
            }
        }

        if (primeraMedida <= 0) return false

        for (i in primeraMedida until lineas.size) {
            if (!regexMedida.matches(lineas[i].trim())) {
                return false
            }
        }

        return true
    }

    private fun parsearMensajeMedidas(mensaje: String): Pair<String, List<Triple<Float, Float, Float>>>? {
        try {
            val lineas = mensaje.trim().split("\n").filter { it.isNotBlank() }

            if (!esFormatoMedidas(mensaje)) return null

            val regexMedida = Regex("""^\s*\d+(\.\d+)?\s*[xX]\s*\d+(\.\d+)?\s*=\s*\d+(\.\d+)?\s*$""")

            var inicieMedidas = -1
            for (i in lineas.indices) {
                if (regexMedida.matches(lineas[i].trim())) {
                    inicieMedidas = i
                    break
                }
            }

            val producto = lineas.take(inicieMedidas).joinToString(" ").trim()
            val medidas = mutableListOf<Triple<Float, Float, Float>>()

            for (i in inicieMedidas until lineas.size) {
                val linea = lineas[i].trim()

                val partes = linea.split("=")
                if (partes.size == 2) {
                    val cantidad = partes[1].trim().toFloatOrNull()
                    val medidaParte = partes[0].trim()
                    val medidasSplit = medidaParte.split(Regex("[xX]"))

                    if (medidasSplit.size == 2 && cantidad != null) {
                        val med1 = medidasSplit[0].trim().toFloatOrNull()
                        val med2 = medidasSplit[1].trim().toFloatOrNull()

                        if (med1 != null && med2 != null) {
                            medidas.add(Triple(med1, med2, cantidad))
                        }
                    }
                }
            }

            return if (medidas.isNotEmpty()) Pair(producto, medidas) else null

        } catch (e: Exception) {
            return null
        }
    }

    @SuppressLint("NewApi")
    private fun importarMedidasParseadas(producto: String, medidas: List<Triple<Float, Float, Float>>) {
        if (medidas.isEmpty()) {
            Toast.makeText(activity, "No se encontraron medidas válidas", Toast.LENGTH_SHORT).show()
            return
        }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Importar Medidas")
        builder.setMessage(
            "Producto: $producto\n" +
                    "Elementos encontrados: ${medidas.size}\n" +
                    "Unidad: Pies cuadrados (p2)\n\n" +
                    "Ejemplos:\n" +
                    medidas.take(3).joinToString("\n") { "${it.first} x ${it.second} = ${it.third}" } +
                    if (medidas.size > 3) "\n..." else "" +
                            "\n\nDeseas buscar el precio en la base de datos?"
        )

        builder.setPositiveButton("Buscar precio") { _, _ ->
            buscarPrecioEnBaseDatos(producto, medidas)
        }

        builder.setNegativeButton("Sin precio") { _, _ ->
            importarMedidasConPrecio(producto, medidas, 0.0)
        }

        builder.setNeutralButton("Cancelar", null)
        builder.show()
    }

    private fun buscarPrecioEnBaseDatos(producto: String, medidas: List<Triple<Float, Float, Float>>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = DatabaseProvider.getInstance(activity)
                val productosEncontrados = db.productDao().searchProductsByDescription("%$producto%")

                withContext(Dispatchers.Main) {
                    if (productosEncontrados.isNotEmpty()) {
                        mostrarOpcionesProductos(productosEncontrados, producto, medidas)
                    } else {
                        Toast.makeText(activity, "No se encontraron productos similares en la base de datos", Toast.LENGTH_SHORT).show()
                        importarMedidasConPrecio(producto, medidas, 0.0)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, "Error al buscar en BD: ${e.message}", Toast.LENGTH_SHORT).show()
                    importarMedidasConPrecio(producto, medidas, 0.0)
                }
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun mostrarOpcionesProductos(productos: List<Product>, productoOriginal: String, medidas: List<Triple<Float, Float, Float>>) {
        val opcionesLista = mutableListOf<String>()

        productos.forEach { producto ->
            opcionesLista.add("${producto.description}\n   Precio: S/ ${String.format("%.2f", producto.price)}")
        }

        opcionesLista.add("Sin precio (S/ 0.00)")
        opcionesLista.add("Cancelar")

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Seleccionar Producto (${productos.size} encontrados)")

        builder.setItems(opcionesLista.toTypedArray()) { _, which ->
            when {
                which < productos.size -> {
                    val productoSeleccionado = productos[which]
                    Toast.makeText(activity, "Seleccionado: ${productoSeleccionado.description}", Toast.LENGTH_SHORT).show()
                    importarMedidasConPrecio(productoOriginal, medidas, productoSeleccionado.price)
                }
                which == productos.size -> {
                    Toast.makeText(activity, "Sin precio", Toast.LENGTH_SHORT).show()
                    importarMedidasConPrecio(productoOriginal, medidas, 0.0)
                }
                else -> {
                    Toast.makeText(activity, "Cancelado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        builder.show()
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun importarMedidasConPrecio(producto: String, medidas: List<Triple<Float, Float, Float>>, precio: Double) {
        val lista = obtenerLista()
        var elementosAgregados = 0

        for ((med1, med2, cantidad) in medidas) {
            try {
                val unidadOriginal = binding.prTxt.text.toString()
                binding.prTxt.text = "Centímetros"

                val piescua = calcPies(med1, med2)
                val metroscua = calcMetroCua(med1, med2)
                val ml = calcMLineales(med1, med2)
                val cub = calcMCubicos(med1, med2, 1f)
                val peri = ((conversor(med1) * 2) + (conversor(med2) * 2))

                binding.prTxt.text = unidadOriginal

                val costoTotal = (piescua * cantidad) * precio.toFloat()

                val elemento = Listado(
                    escala = "p2",
                    uni = "Centímetros",
                    medi1 = med1,
                    medi2 = med2,
                    medi3 = 1f,
                    canti = cantidad,
                    piescua = piescua * cantidad,
                    precio = precio.toFloat(),
                    costo = costoTotal,
                    producto = producto,
                    peri = peri,
                    metcua = metroscua,
                    metli = ml * cantidad,
                    metcub = cub,
                    color = ContextCompat.getColor(activity, R.color.color),
                    uri = ""
                )

                lista.add(elemento)
                elementosAgregados++

            } catch (e: Exception) {
                continue
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
            Toast.makeText(activity, "No se pudo importar ningún elemento", Toast.LENGTH_SHORT).show()
        }
    }
}
