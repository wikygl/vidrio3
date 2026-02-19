package crystal.crystal.casilla

import android.content.Context

object ProyectoManager {

    private var proyectoActivo: String? = null
    private var metadataActual: ProyectoMetadata? = null

    // Obtener el proyecto activo actual
    fun getProyectoActivo(): String? = proyectoActivo

    // Obtener metadata del proyecto activo
    fun getMetadataActual(): ProyectoMetadata? = metadataActual

    // Establecer proyecto activo
    fun setProyectoActivo(context: Context, nombreProyecto: String) {
        proyectoActivo = nombreProyecto
        metadataActual = MapStorage.cargarMetadataProyecto(context, nombreProyecto)
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("proyecto_activo", nombreProyecto)
            .apply()
    }

    // Inicializar desde SharedPreferences al abrir la app
    fun inicializarDesdeStorage(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val proyectoGuardado = sharedPreferences.getString("proyecto_activo", null)

        if (proyectoGuardado != null) {
            proyectoActivo = proyectoGuardado
            metadataActual = MapStorage.cargarMetadataProyecto(context, proyectoGuardado)
        }
    }

    // Incrementar contador de ventanas del proyecto activo
    fun incrementarContadorVentanas(context: Context) {
        metadataActual?.let { metadata ->
            metadata.contadorVentanas++
            metadata.actualizarFechaModificacion()

            proyectoActivo?.let { nombreProyecto ->
                MapStorage.guardarMetadataProyecto(context, nombreProyecto, metadata)
            }
        }
    }

    // Obtener contador actual
    fun getContadorVentanas(): Int {
        return metadataActual?.contadorVentanas ?: 0
    }

    // Verificar si hay proyecto activo
    fun hayProyectoActivo(): Boolean = proyectoActivo != null

    // Limpiar proyecto activo
    fun limpiarProyectoActivo(context: Context) {
        proyectoActivo = null
        metadataActual = null

        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .remove("proyecto_activo")
            .apply()
    }

    fun actualizarContadorPorPrefijo(context: Context, prefijo: String, nuevoValor: Int) {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if (proyectoActivo != null) {
            val key = "${proyectoActivo}_contador_$prefijo"
            editor.putInt(key, nuevoValor)
            editor.apply()
        }
    }

    fun resetearContadorPorPrefijo(context: Context, prefijo: String) {
        actualizarContadorPorPrefijo(context, prefijo, 0)
    }

    fun obtenerSiguienteContadorPorPrefijo(context: Context, prefijo: String): Int {
        val proyectoActivo = getProyectoActivo() ?: return 1

        // Obtener paquetes que realmente existen en el proyecto
        val paquetesExistentes = obtenerListaPaquetes(context)

        // Filtrar solo los paquetes del prefijo específico (nuevo formato: Vna1, Vni2, etc.)
        val paquetesDelPrefijo = paquetesExistentes.filter { paquete ->
            paquete.startsWith(prefijo, ignoreCase = true)
        }

        if (paquetesDelPrefijo.isEmpty()) {
            return 1 // Si no hay paquetes del prefijo, empezar desde 1
        }

        // Extraer números de los identificadores y encontrar el más alto
        val numerosExistentes = paquetesDelPrefijo.mapNotNull { paquete ->
            // Extraer número de "Vna1" -> 1, "Vni35" -> 35, etc.
            val regex = Regex("$prefijo(\\d+)", RegexOption.IGNORE_CASE)
            val match = regex.find(paquete)
            match?.groupValues?.get(1)?.toIntOrNull()
        }

        val numeroMasAlto = numerosExistentes.maxOrNull() ?: 0
        return numeroMasAlto + 1
    }

    private fun obtenerContadorPorPrefijo(context: Context, prefijo: String): Int {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)

        return if (proyectoActivo != null) {
            val key = "${proyectoActivo}_contador_$prefijo"
            sharedPreferences.getInt(key, 0)
        } else {
            0
        }
    }

    fun obtenerTotalPaquetes(context: Context): Int {
        val proyectoActivo = getProyectoActivo() ?: return 0
        val mapListas = MapStorage.cargarProyecto(context, proyectoActivo) ?: return 0

        val paquetesEncontrados = mutableSetOf<String>()

        // Iterar a través de todas las listas en el map
        for ((_, lista) in mapListas) {
            for (elemento in lista) {
                if (elemento.size >= 3) {
                    val identificador = elemento[2] // v1NA, p2, m1MP, etc.
                    if (identificador.isNotEmpty()) {
                        paquetesEncontrados.add(identificador)
                    }
                }
            }
        }

        return paquetesEncontrados.size
    }

    fun obtenerListaPaquetes(context: Context): List<String> {
        val proyectoActivo = getProyectoActivo() ?: return emptyList()
        val mapListas = MapStorage.cargarProyecto(context, proyectoActivo) ?: return emptyList()

        val paquetesEncontrados = mutableSetOf<String>()

        // Iterar a través de todas las listas en el map
        for ((_, lista) in mapListas) {
            for (elemento in lista) {
                if (elemento.size >= 3) {
                    val identificador = elemento[2] // v1NA, p2, m1MP, etc.
                    if (identificador.isNotEmpty()) {
                        paquetesEncontrados.add(identificador)
                    }
                }
            }
        }

        return paquetesEncontrados.toList().sorted()
    }

    fun obtenerElementosPaquete(context: Context, identificadorPaquete: String): MutableMap<String, MutableList<MutableList<String>>> {
        val proyectoActivo = getProyectoActivo() ?: return mutableMapOf()
        val mapListas = MapStorage.cargarProyecto(context, proyectoActivo) ?: return mutableMapOf()

        val elementosPaquete = mutableMapOf<String, MutableList<MutableList<String>>>()

        // Filtrar elementos que pertenecen al paquete específico
        for ((categoria, lista) in mapListas) {
            val elementosDelPaquete = lista.filter { elemento ->
                elemento.size >= 3 && elemento[2] == identificadorPaquete
            }.toMutableList()

            if (elementosDelPaquete.isNotEmpty()) {
                elementosPaquete[categoria] = elementosDelPaquete
            }
        }

        return elementosPaquete
    }

    fun eliminarPaquete(context: Context, identificadorPaquete: String): Boolean {
        val proyectoActivo = getProyectoActivo() ?: return false
        val mapListas = MapStorage.cargarProyecto(context, proyectoActivo) ?: return false

        var elementosEliminados = false

        // Eliminar elementos del paquete de todas las categorías
        val iterador = mapListas.iterator()
        while (iterador.hasNext()) {
            val (categoria, lista) = iterador.next()

            // Filtrar elementos que NO pertenecen al paquete específico
            val elementosRestantes = lista.filter { elemento ->
                !(elemento.size >= 3 && elemento[2] == identificadorPaquete)
            }.toMutableList()

            if (elementosRestantes.size != lista.size) {
                elementosEliminados = true
                if (elementosRestantes.isEmpty()) {
                    // Si no quedan elementos en la categoría, eliminar la categoría
                    iterador.remove()
                } else {
                    // Actualizar la lista con los elementos restantes
                    mapListas[categoria] = elementosRestantes
                }
            }
        }

        if (elementosEliminados) {
            MapStorage.guardarProyecto(context, proyectoActivo, mapListas)
        }

        return elementosEliminados
    }
}