package crystal.crystal.optimizadores.corte

import android.app.AlertDialog
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import crystal.crystal.R
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager

/**
 * Maneja las listas predefinidas desde MapStorage
 * Extraído del código original de Corte.kt
 */
class CorteListManager(private val context: Context) {

    // Claves que no son materiales: descripciones, diseños y metadatos. Si alguna se colara, sus
    // textos entrarían al optimizador como si fueran medidas de corte.
    private val listasExcluidas = setOf(
        "Diseño", "DisenoPaquete", "DisenoSimbolicoV2", "MetadatosProduccion", "Grados", "Referencias",
        "DisenoPuerta", "DisenoMampara", "DisenoVentanaAl", "DisenoVitroven"
    )

    // Ancho/Alto son medidas de REFERENCIA (p. ej. de muro cortina), no listas de piezas a cortar,
    // así que no deben ofrecerse como listas cortables. Se excluyen tolerando ":" y mayúsculas.
    private fun esListaExcluida(nombre: String): Boolean {
        if (nombre in listasExcluidas) return true
        val n = nombre.trim().trimEnd(':').lowercase()
        return n == "ancho" || n == "alto"
    }

    private fun ensureProjectInitialized() {
        if (!ProyectoManager.hayProyectoActivo()) {
            ProyectoManager.inicializarDesdeStorage(context)
        }
    }

    // idVentana -> (colorAluminio, tipoVidrio), leído desde la fuente única de metadatos de producción.
    private fun construirVentanaColorMap(mapListas: Map<String, List<List<String>>>): Map<String, Pair<String, String>> =
        crystal.crystal.casilla.MetadatosProduccion.mapaPorVentana(mapListas)

    /**
     * Muestra diálogo de selección de proyectos y luego pobla el spinner con las listas encontradas
     */
    fun poblarSpinnerConDatosGuardados(spinner: Spinner) {
        ensureProjectInitialized()
        val proyectos = MapStorage.obtenerListaProyectos(context)
        if (proyectos.isEmpty()) {
            Toast.makeText(context, "No hay proyectos disponibles.", Toast.LENGTH_SHORT).show()
            return
        }
        val seleccionados = BooleanArray(proyectos.size) { false }
        AlertDialog.Builder(context)
            .setTitle("Seleccionar proyectos")
            .setMultiChoiceItems(proyectos.toTypedArray(), seleccionados) { _, i, checked ->
                seleccionados[i] = checked
            }
            .setPositiveButton("Cargar") { _, _ ->
                val elegidos = proyectos.filterIndexed { i, _ -> seleccionados[i] }
                if (elegidos.isEmpty()) {
                    Toast.makeText(context, "Selecciona al menos un proyecto", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val nombres = cargarNombresDeProyectos(elegidos)
                if (nombres.isNotEmpty()) {
                    val adapter = ArrayAdapter(context, R.layout.lista_spinner, nombres)
                    adapter.setDropDownViewResource(R.layout.lista_spinner)
                    spinner.adapter = null
                    spinner.adapter = adapter
                } else {
                    Toast.makeText(context, "No se encontraron listas válidas en los proyectos seleccionados.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cargarNombresDeProyectos(proyectos: List<String>): List<String> {
        val merged = mutableMapOf<String, MutableList<MutableList<String>>>()
        for (proyecto in proyectos) {
            val map = MapStorage.cargarProyecto(context, proyecto) ?: continue
            for ((clave, entradas) in map) {
                // Include ALL keys (including DisenoSimbolicoV2) so ventanaColorMap can be built
                val lista = merged.getOrPut(clave) { mutableListOf() }
                for (entrada in entradas) {
                    val e = entrada.toMutableList()
                    while (e.size < 4) e.add("")
                    if (e[3].isBlank()) e[3] = proyecto
                    lista.add(e)
                }
            }
        }
        // Cache the merged map for cargarLista() to use
        cachedMergedMap = merged

        val ventanaColorMap = construirVentanaColorMap(merged)
        val nombresListasValidas = mutableListOf<String>()

        merged.forEach { (nombreLista, listas) ->
            if (esListaExcluida(nombreLista)) return@forEach

            val listasValidas = listas.filter { lista ->
                if (lista.size < 3) return@filter false
                val dato2 = lista[1].trim()
                !dato2.matches(Regex(".*[a-zA-Z].*")) &&
                    dato2.toIntOrNull()?.let { it > 0 } == true &&
                    lista[0].trim().toFloatOrNull()?.let { it > 0f } == true
            }
            if (listasValidas.isEmpty()) return@forEach

            val esVidrio = nombreLista.startsWith("Vidrio", ignoreCase = true)
            val sufijosUsados = mutableSetOf<String>()
            listasValidas.forEach { lista ->
                val ventana = lista[2]
                val (colorAlu, tipoVidrio) = ventanaColorMap[ventana] ?: Pair("", "")
                val sufijo = if (esVidrio) tipoVidrio else colorAlu
                if (sufijo.isNotBlank() && !nombreLista.contains(sufijo, ignoreCase = true)) {
                    sufijosUsados.add(sufijo)
                }
            }

            if (sufijosUsados.isNotEmpty()) {
                sufijosUsados.sorted().forEach { sufijo -> nombresListasValidas.add("$nombreLista [$sufijo]") }
            } else {
                nombresListasValidas.add(nombreLista)
            }
        }
        return nombresListasValidas
    }

    // Holds the last merged map so cargarLista() can filter from it
    private var cachedMergedMap: MutableMap<String, MutableList<MutableList<String>>> = mutableMapOf()

    /**
     * Carga una lista específica desde el mapa cacheado; soporta claves compuestas como "u-13 [negro]"
     */
    fun cargarLista(nombreLista: String): List<PiezaCorte> {
        val listaResultado = mutableListOf<PiezaCorte>()
        val mapListas = cachedMergedMap.ifEmpty {
            ensureProjectInitialized()
            MapStorage.cargarMap(context) ?: run {
                Toast.makeText(context, "No se encontró la lista seleccionada.", Toast.LENGTH_SHORT).show()
                return listaResultado
            }
        }

        val bracketMatch = Regex("^(.+)\\s+\\[([^\\]]+)\\]$").find(nombreLista)
        val nombreBase = bracketMatch?.groupValues?.get(1) ?: nombreLista
        val colorFiltro = bracketMatch?.groupValues?.get(2)?.lowercase()

        val listas = mapListas[nombreBase]
        if (listas.isNullOrEmpty()) {
            Toast.makeText(context, "No se encontró la lista seleccionada.", Toast.LENGTH_SHORT).show()
            return listaResultado
        }

        val ventanaColorMap = construirVentanaColorMap(mapListas)
        val esVidrio = nombreBase.startsWith("Vidrio", ignoreCase = true)
        var errores = 0

        listas.forEach { subLista ->
            if (subLista.size < 3) { errores++; return@forEach }
            val dato1Str = subLista[0].trim()
            val dato2Str = subLista[1].trim()
            val ventana = subLista[2].trim()
            // Solo el número del id (primer token): los ids viejos traen ", cliente" (puerta) o
            // " proyecto" (nova); cualquiera de los dos se descarta para no duplicar ni mostrar el cliente.
            val ventanaNum = ventana.split(',', ' ').first().trim()

            if (colorFiltro != null) {
                val (colorAlu, tipoVidrio) = ventanaColorMap[ventana] ?: Pair("", "")
                val sufijoEntrada = (if (esVidrio) tipoVidrio else colorAlu).lowercase()
                if (sufijoEntrada != colorFiltro) return@forEach
            }

            val dato1 = dato1Str.toFloatOrNull()
            val dato2 = dato2Str.toIntOrNull()
            if (dato1 != null && dato2 != null) {
                val proyecto = subLista.getOrElse(3) { "" }.trim()
                val referenciaBase = if (proyecto.isNotBlank()) "$ventanaNum $proyecto" else ventanaNum
                val referencia = agregarListaAReferencia(referenciaBase, nombreBase)
                listaResultado.add(PiezaCorte(dato1, dato2, referencia, true))
            } else {
                errores++
            }
        }

        if (errores > 0) {
            Toast.makeText(context, "Hay $errores entradas inválidas en la lista seleccionada.", Toast.LENGTH_SHORT).show()
        }
        if (listaResultado.isEmpty()) {
            Toast.makeText(context, "La lista seleccionada está vacía.", Toast.LENGTH_SHORT).show()
        }

        return listaResultado
    }

    private fun agregarListaAReferencia(referencia: String, nombreLista: String): String {
        val lista = nombreLista.trim()
        if (lista.isBlank()) return referencia
        return "${referencia.trim()},$lista"
    }

    /**
     * Verifica si hay listas disponibles
     */
    fun hayListasDisponibles(): Boolean {
        val mapListas = MapStorage.cargarMap(context)
        return mapListas != null && mapListas.isNotEmpty()
    }

    fun nombresListasDisponibles(): Set<String> =
        cachedMergedMap.keys
            .filter { !esListaExcluida(it) }
            .toSet()
}
