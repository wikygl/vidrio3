package crystal.crystal.optimizadores.planchas

import android.app.AlertDialog
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import crystal.crystal.R
import crystal.crystal.casilla.MapStorage
import crystal.crystal.casilla.ProyectoManager

class PlanchaListManager(private val context: Context) {

    // Claves que no son materiales: descripciones, diseños y metadatos. Si alguna se colara, sus
    // textos entrarían al optimizador como si fueran medidas de vidrio.
    private val listasExcluidas = setOf(
        "Diseño", "DisenoPaquete", "DisenoSimbolicoV2", "Grados", "Referencias",
        "DisenoPuerta", "DisenoMampara", "DisenoVentanaAl", "DisenoVitroven"
    )
    // Acepta "120 x 80", "120x80", "120 X 80", "120×80", "120*80" (x/X/×/* con o sin espacios).
    private val patronDosD = Regex("""^\d+(?:[.,]\d+)?\s*[xX×*]\s*\d+(?:[.,]\d+)?$""")
    private val separadorDosD = Regex("""\s*[xX×*]\s*""")
    private val bracketRegex = Regex("""^(.+)\s+\[([^\]]+)\]$""")

    private var cachedMergedMap: MutableMap<String, MutableList<MutableList<String>>> = mutableMapOf()

    private fun ensureProjectInitialized() {
        if (!ProyectoManager.hayProyectoActivo()) {
            ProyectoManager.inicializarDesdeStorage(context)
        }
    }

    // idVentana -> tipo de vidrio, leído desde la fuente única de metadatos de producción.
    private fun construirVentanaColorMap(mapListas: Map<String, List<List<String>>>): Map<String, String> =
        crystal.crystal.casilla.MetadatosProduccion.mapaPorVentana(mapListas)
            .mapValues { it.value.second }
            .filterValues { it.isNotBlank() }

    fun hayListasDisponibles(): Boolean {
        val map = MapStorage.cargarMap(context)
        return map != null && map.isNotEmpty()
    }

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
                    Toast.makeText(context, "No se encontraron vidrios en los proyectos seleccionados.", Toast.LENGTH_SHORT).show()
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
                val lista = merged.getOrPut(clave) { mutableListOf() }
                for (entrada in entradas) {
                    val e = entrada.toMutableList()
                    while (e.size < 4) e.add("")
                    if (e[3].isBlank()) e[3] = proyecto
                    lista.add(e)
                }
            }
        }
        cachedMergedMap = merged

        val ventanaColorMap = construirVentanaColorMap(merged)
        val nombresValidos = mutableListOf<String>()

        merged.forEach { (nombreLista, listas) ->
            if (nombreLista in listasExcluidas) return@forEach

            val vidriosValidos = listas.filter { esEntradaVidrio(it) }
            if (vidriosValidos.isEmpty()) return@forEach

            // Recopilar tipos de vidrio distintos via DisenoSimbolicoV2
            val tiposUsados = mutableSetOf<String>()
            vidriosValidos.forEach { subLista ->
                val ventana = subLista.getOrElse(2) { "" }.trim()
                val tipo = ventanaColorMap[ventana] ?: ""
                if (tipo.isNotBlank() && !nombreLista.contains(tipo, ignoreCase = true)) {
                    tiposUsados.add(tipo)
                }
            }

            if (tiposUsados.size > 1) {
                // Hay múltiples tipos: crear entrada por cada tipo + una para los sin tipo
                tiposUsados.sorted().forEach { tipo -> nombresValidos.add("$nombreLista [$tipo]") }
                val sinTipo = vidriosValidos.any { subLista ->
                    val ventana = subLista.getOrElse(2) { "" }.trim()
                    ventanaColorMap[ventana].isNullOrBlank()
                }
                if (sinTipo) nombresValidos.add(nombreLista)
            } else if (tiposUsados.size == 1) {
                // Hay un único tipo: mostrar con sufijo
                nombresValidos.add("$nombreLista [${tiposUsados.first()}]")
            } else {
                // Sin información de tipo: mostrar lista base
                nombresValidos.add(nombreLista)
            }
        }
        return nombresValidos
    }

    fun cargarLista(nombreLista: String): List<ItemListaPlanchas> {
        val resultado = mutableListOf<ItemListaPlanchas>()
        val mapListas = cachedMergedMap.ifEmpty {
            ensureProjectInitialized()
            MapStorage.cargarMap(context) ?: run {
                Toast.makeText(context, "No se encontró la lista seleccionada.", Toast.LENGTH_SHORT).show()
                return resultado
            }
        }

        val bracketMatch = bracketRegex.find(nombreLista)
        val nombreBase = bracketMatch?.groupValues?.get(1) ?: nombreLista
        val tipoFiltro = bracketMatch?.groupValues?.get(2)?.lowercase()

        val listas = mapListas[nombreBase]
        if (listas.isNullOrEmpty()) {
            Toast.makeText(context, "No se encontró la lista seleccionada.", Toast.LENGTH_SHORT).show()
            return resultado
        }

        val ventanaColorMap = construirVentanaColorMap(mapListas)

        listas.forEach { subLista ->
            if (!esEntradaVidrio(subLista)) return@forEach

            val ventana = subLista.getOrElse(2) { "" }.trim()

            // Filtrar por tipo de vidrio si hay sufijo en el nombre
            if (tipoFiltro != null) {
                val tipoEntrada = (ventanaColorMap[ventana] ?: "").lowercase()
                if (tipoEntrada != tipoFiltro) return@forEach
            }

            val partes = subLista[0].trim().split(separadorDosD)
            if (partes.size != 2) return@forEach
            val ancho = partes[0].replace(",", ".").toFloatOrNull() ?: return@forEach
            val alto = partes[1].replace(",", ".").toFloatOrNull() ?: return@forEach
            val cant = subLista[1].trim().toIntOrNull() ?: return@forEach
            val proyecto = subLista.getOrElse(3) { "" }.trim()
            // Solo el número del id (primer token): los ids viejos traen ", cliente" (puerta) o
            // " proyecto" (nova); cualquiera se descarta para no duplicar ni mostrar el cliente.
            val ventanaNum = ventana.split(',', ' ').first().trim()
            val info = if (proyecto.isNotBlank()) "$ventanaNum $proyecto" else ventanaNum
            resultado.add(ItemListaPlanchas(ancho = ancho, alto = alto, cantidad = cant, info = info))
        }

        if (resultado.isEmpty()) {
            Toast.makeText(context, "No se encontraron vidrios en esta lista.", Toast.LENGTH_SHORT).show()
        }
        return resultado
    }

    private fun esEntradaVidrio(subLista: List<String>): Boolean {
        if (subLista.size < 2) return false
        val cant = subLista[1].trim().toIntOrNull() ?: return false
        if (cant <= 0) return false
        return patronDosD.matches(subLista[0].trim())
    }
}
