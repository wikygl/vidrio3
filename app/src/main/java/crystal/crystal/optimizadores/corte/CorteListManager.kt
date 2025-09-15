package crystal.crystal.optimizadores.corte

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import crystal.crystal.R
import crystal.crystal.casilla.MapStorage

/**
 * Maneja las listas predefinidas desde MapStorage
 * Extraído del código original de Corte.kt
 */
class CorteListManager(private val context: Context) {

    /**
     * Pobla el spinner con datos guardados - exactamente como en el código original
     */
    fun poblarSpinnerConDatosGuardados(spinner: Spinner): List<String> {
        try {
            // Cargar el mapa desde MapStorage
            val mapListas = MapStorage.cargarMap(context)

            if (mapListas != null && mapListas.isNotEmpty()) {
                val nombresListasValidas = mutableListOf<String>() // Lista para almacenar nombres válidos

                // Procesar las listas
                mapListas.forEach { (nombreLista, listas) ->
                    val listasValidas = listas.filter { lista ->
                        lista.size == 3 && !lista[1].matches(Regex(".*[a-zA-Z].*"))
                    }

                    if (listasValidas.isNotEmpty()) {
                        nombresListasValidas.add(nombreLista)
                    }
                }

                // Verificar si hay listas válidas para mostrar
                if (nombresListasValidas.isNotEmpty()) {
                    // Crear un ArrayAdapter con los nombres de las listas válidas
                    val adapter = ArrayAdapter(context, R.layout.lista_spinner, nombresListasValidas)
                    adapter.setDropDownViewResource(R.layout.lista_spinner)

                    // Limpiar adaptador anterior para evitar duplicación
                    spinner.adapter = null

                    // Asignar el adaptador al Spinner
                    spinner.adapter = adapter

                    return nombresListasValidas
                } else {
                    Toast.makeText(context, "No se encontraron listas válidas para mostrar.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No se encontraron listas guardadas.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al recuperar datos: ${e.message}", Toast.LENGTH_LONG).show()
        }

        return emptyList()
    }

    /**
     * Carga una lista específica - exactamente como en el código original
     */
    fun cargarLista(nombreLista: String): List<PiezaCorte> {
        val listaResultado = mutableListOf<PiezaCorte>()

        // Cargar el mapa de listas desde MapStorage
        val mapListas = MapStorage.cargarMap(context)

        // Verificar si el mapa contiene la lista seleccionada
        if (mapListas != null && mapListas.containsKey(nombreLista)) {
            val listas = mapListas[nombreLista]

            if (listas != null && listas.isNotEmpty()) {
                var errores = 0

                // Recorrer las sublistas y convertir los tipos
                listas.forEach { subLista ->
                    if (subLista.size == 3) {
                        val dato1Str = subLista[0].trim()
                        val dato2Str = subLista[1].trim()
                        val ventana = subLista[2].trim()

                        // Convertir los strings a Float e Int de manera segura
                        val dato1 = dato1Str.toFloatOrNull()
                        val dato2 = dato2Str.toIntOrNull()

                        if (dato1 != null && dato2 != null) {
                            // Añadir el PiezaCorte a la lista (todos entran como true por defecto)
                            listaResultado.add(PiezaCorte(dato1, dato2, ventana, true))
                        } else {
                            // Contar errores para mostrar un solo Toast al final
                            errores++
                        }
                    } else {
                        // Contar errores si la subLista no tiene 3 elementos
                        errores++
                    }
                }

                // Mostrar Toast si hay errores
                if (errores > 0) {
                    Toast.makeText(context, "Hay $errores entradas inválidas en la lista seleccionada.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Si la lista seleccionada está vacía
                Toast.makeText(context, "La lista seleccionada está vacía.", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Si el nombre seleccionado no existe en el mapa
            Toast.makeText(context, "No se encontró la lista seleccionada.", Toast.LENGTH_SHORT).show()
        }

        return listaResultado
    }

    /**
     * Verifica si hay listas disponibles
     */
    fun hayListasDisponibles(): Boolean {
        val mapListas = MapStorage.cargarMap(context)
        return mapListas != null && mapListas.isNotEmpty()
    }
}