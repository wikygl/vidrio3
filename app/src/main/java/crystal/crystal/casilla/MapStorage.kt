package crystal.crystal.casilla

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object MapStorage {

    private val gson = Gson()

    // ==================== FUNCIONES ORIGINALES (mantenidas para compatibilidad) ====================

    fun guardarMap(context: Context, mapListas: MutableMap<String, MutableList<MutableList<String>>>) {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            guardarProyecto(context, proyectoActivo, mapListas)
        } else {
            // Fallback al mÃ©todo original si no hay proyecto activo
            guardarMapOriginal(context, mapListas)
        }
    }

    fun cargarMap(context: Context): MutableMap<String, MutableList<MutableList<String>>>? {
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        return if (proyectoActivo != null) {
            cargarProyecto(context, proyectoActivo)
        } else {
            cargarMapOriginal(context)
        }
    }

    // ==================== NUEVAS FUNCIONES PARA MÃšLTIPLES PROYECTOS ====================

    // Crear nuevo proyecto
    fun crearProyecto(context: Context, nombreProyecto: String, descripcion: String = ""): Boolean {
        if (existeProyecto(context, nombreProyecto)) {
            return false // El proyecto ya existe
        }

        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Crear metadata del proyecto
        val metadata = ProyectoMetadata(nombreProyecto, descripcion)
        val metadataJson = gson.toJson(metadata)
        editor.putString("${nombreProyecto}_metadata", metadataJson)

        // Crear mapa vacÃ­o para el proyecto
        val mapaVacio = mutableMapOf<String, MutableList<MutableList<String>>>()
        val mapaJson = gson.toJson(mapaVacio)
        editor.putString("${nombreProyecto}_mapListas", mapaJson)

        // Agregar a la lista de proyectos
        val listaProyectos = obtenerListaProyectos(context).toMutableList()
        listaProyectos.add(nombreProyecto)
        val listaJson = gson.toJson(listaProyectos)
        editor.putString("lista_proyectos", listaJson)

        editor.apply()
        return true
    }

    // Guardar proyecto especÃ­fico
    fun guardarProyecto(context: Context, nombreProyecto: String, mapListas: MutableMap<String, MutableList<MutableList<String>>>) {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convertir el Map a JSON
        val json = gson.toJson(mapListas)
        editor.putString("${nombreProyecto}_mapListas", json)

        // Actualizar fecha de modificaciÃ³n en metadata
        val metadata = cargarMetadataProyecto(context, nombreProyecto)
        metadata?.let {
            it.actualizarFechaModificacion()
            guardarMetadataProyecto(context, nombreProyecto, it)
        }

        editor.apply()
    }

    // Cargar proyecto especÃ­fico
    fun cargarProyecto(context: Context, nombreProyecto: String): MutableMap<String, MutableList<MutableList<String>>>? {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("${nombreProyecto}_mapListas", null)

        return if (json != null) {
            val type = object : TypeToken<MutableMap<String, MutableList<MutableList<String>>>>() {}.type
            gson.fromJson<MutableMap<String, MutableList<MutableList<String>>>>(json, type)
        } else {
            null
        }
    }

    // Agregar elementos a un proyecto existente
    fun agregarAlProyecto(context: Context, nombreProyecto: String, nuevosElementos: MutableMap<String, MutableList<MutableList<String>>>) {
        val mapaExistente = cargarProyecto(context, nombreProyecto) ?: mutableMapOf()

        // Fusionar los mapas
        for ((clave, valor) in nuevosElementos) {
            if (mapaExistente.containsKey(clave)) {
                mapaExistente[clave]?.addAll(valor)
            } else {
                mapaExistente[clave] = valor
            }
        }

        guardarProyecto(context, nombreProyecto, mapaExistente)
    }

    // Obtener lista de todos los proyectos
    fun obtenerListaProyectos(context: Context): List<String> {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("lista_proyectos", null)

        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson<List<String>>(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    // Verificar si existe un proyecto
    fun existeProyecto(context: Context, nombreProyecto: String): Boolean {
        return obtenerListaProyectos(context).contains(nombreProyecto)
    }

    // Eliminar proyecto
    fun eliminarProyecto(context: Context, nombreProyecto: String): Boolean {
        if (!existeProyecto(context, nombreProyecto)) {
            return false
        }

        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Eliminar datos del proyecto
        editor.remove("${nombreProyecto}_mapListas")
        editor.remove("${nombreProyecto}_metadata")

        // Eliminar de la lista de proyectos
        val listaProyectos = obtenerListaProyectos(context).toMutableList()
        listaProyectos.remove(nombreProyecto)
        val listaJson = gson.toJson(listaProyectos)
        editor.putString("lista_proyectos", listaJson)

        // Si era el proyecto activo, limpiarlo
        if (ProyectoManager.getProyectoActivo() == nombreProyecto) {
            ProyectoManager.limpiarProyectoActivo(context)
        }

        editor.apply()
        return true
    }

    // ==================== FUNCIONES DE METADATA ====================

    fun cargarMetadataProyecto(context: Context, nombreProyecto: String): ProyectoMetadata? {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("${nombreProyecto}_metadata", null)

        return if (json != null) {
            gson.fromJson(json, ProyectoMetadata::class.java)
        } else {
            null
        }
    }

    fun guardarMetadataProyecto(context: Context, nombreProyecto: String, metadata: ProyectoMetadata) {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = gson.toJson(metadata)
        editor.putString("${nombreProyecto}_metadata", json)
        editor.apply()
    }

    fun obtenerListaProyectosConMetadata(context: Context): List<ProyectoMetadata> {
        val proyectos = obtenerListaProyectos(context)
        return proyectos.mapNotNull { nombreProyecto ->
            cargarMetadataProyecto(context, nombreProyecto)
        }
    }

    // ==================== FUNCIONES ORIGINALES PRIVADAS ====================

    private fun guardarMapOriginal(context: Context, mapListas: MutableMap<String, MutableList<MutableList<String>>>) {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = gson.toJson(mapListas)
        editor.putString("mapListas", json)
        editor.apply()
    }

    private fun cargarMapOriginal(context: Context): MutableMap<String, MutableList<MutableList<String>>>? {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("mapListas", null)

        return if (json != null) {
            val type = object : TypeToken<MutableMap<String, MutableList<MutableList<String>>>>() {}.type
            gson.fromJson<MutableMap<String, MutableList<MutableList<String>>>>(json, type)
        } else {
            null
        }
    }
}
