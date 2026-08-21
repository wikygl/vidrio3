package crystal.crystal.casilla

import android.content.Context
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object MapStorage {

    private val gson = Gson()

    // ==================== RESPALDO INMEDIATO A DISCO (sobrevive a desinstalar) ====================
    // Los proyectos viven en SharedPreferences privadas, que una desinstalación borra. Para no
    // perder trabajo entre snapshots del auto-backup de Android, se espeja TODO el almacén a un
    // archivo en Descargas/Crystal (almacenamiento compartido) en cada guardado, y se restaura
    // automáticamente al reabrir si faltan proyectos localmente (p. ej. tras reinstalar).

    @Volatile private var restauracionHecha = false

    private fun archivoRespaldo(context: Context): File {
        val publico = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "Crystal/Proyectos"
        )
        val dir = if (publico.exists() || publico.mkdirs()) publico
        else File(context.getExternalFilesDir(null), "Crystal/Proyectos").apply { mkdirs() }
        return File(dir, "crystal_proyectos_backup.json")
    }

    // Vuelca todas las claves de MapStorage (proyectos, metadata y lista) al archivo de respaldo.
    private fun respaldarADisco(context: Context) {
        runCatching {
            val sp = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
            val soloStrings = sp.all.entries
                .filter { it.value is String }
                .associate { it.key to (it.value as String) }
            archivoRespaldo(context).writeText(gson.toJson(soloStrings))
        }
    }

    // Restaura desde disco los proyectos que falten localmente (unión, sin pisar los actuales).
    // Cubre tanto "SharedPreferences vacías tras reinstalar" como "el auto-backup restauró un
    // snapshot viejo al que le faltan proyectos recientes".
    private fun restaurarDesdeDisco(context: Context) {
        runCatching {
            val archivo = archivoRespaldo(context)
            if (!archivo.exists()) return
            val texto = archivo.readText()
            if (texto.isBlank()) return
            val tipoMapa = object : TypeToken<Map<String, String>>() {}.type
            val datos: Map<String, String> = gson.fromJson(texto, tipoMapa) ?: return
            if (datos.isEmpty()) return

            val sp = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
            val editor = sp.edit()
            val tipoLista = object : TypeToken<MutableList<String>>() {}.type
            val actuales: MutableList<String> =
                sp.getString("lista_proyectos", null)?.let { gson.fromJson(it, tipoLista) } ?: mutableListOf()
            val respaldados: List<String> =
                datos["lista_proyectos"]?.let { gson.fromJson(it, tipoLista) } ?: emptyList()

            var cambios = false
            for (nombre in respaldados) {
                if (nombre in actuales) continue   // ya existe localmente: no se pisa
                datos["${nombre}_mapListas"]?.let { editor.putString("${nombre}_mapListas", it) }
                datos["${nombre}_metadata"]?.let { editor.putString("${nombre}_metadata", it) }
                actuales.add(nombre)
                cambios = true
            }
            if (sp.getString("mapListas", null) == null && datos["mapListas"] != null) {
                editor.putString("mapListas", datos["mapListas"])
                cambios = true
            }
            if (cambios) {
                editor.putString("lista_proyectos", gson.toJson(actuales))
                editor.apply()
            }
        }
    }

    private fun asegurarRestauracion(context: Context) {
        if (restauracionHecha) return
        synchronized(this) {
            if (restauracionHecha) return
            restaurarDesdeDisco(context)
            restauracionHecha = true
        }
    }

    // ==================== FUNCIONES ORIGINALES (mantenidas para compatibilidad) ====================

    fun guardarMap(context: Context, mapListas: MutableMap<String, MutableList<MutableList<String>>>) {
        // Candado de respaldo (Fase 3): archivar es de pago. Cubre TODOS los módulos que guardan aquí.
        // Con el cobro apagado (Suscripcion.ENFORCEMENT=false) siempre deja pasar.
        if (!crystal.crystal.Suscripcion.exigir(
                context,
                crystal.crystal.Suscripcion.puedeArchivar(),
                "Archivar es una función de pago. Renueva para guardar tus proyectos."
            )
        ) return
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        if (proyectoActivo != null) {
            guardarProyecto(context, proyectoActivo, mapListas)
        } else {
            // Fallback al método original si no hay proyecto activo
            guardarMapOriginal(context, mapListas)
        }
    }

    fun cargarMap(context: Context): MutableMap<String, MutableList<MutableList<String>>>? {
        asegurarRestauracion(context)
        val proyectoActivo = ProyectoManager.getProyectoActivo()
        return if (proyectoActivo != null) {
            cargarProyecto(context, proyectoActivo)
        } else {
            cargarMapOriginal(context)
        }
    }

    // ==================== NUEVAS FUNCIONES PARA MÚLTIPLES PROYECTOS ====================

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

        // Crear mapa vacío para el proyecto
        val mapaVacio = mutableMapOf<String, MutableList<MutableList<String>>>()
        val mapaJson = gson.toJson(mapaVacio)
        editor.putString("${nombreProyecto}_mapListas", mapaJson)

        // Agregar a la lista de proyectos
        val listaProyectos = obtenerListaProyectos(context).toMutableList()
        listaProyectos.add(nombreProyecto)
        val listaJson = gson.toJson(listaProyectos)
        editor.putString("lista_proyectos", listaJson)

        editor.apply()
        respaldarADisco(context)
        return true
    }

    // Guardar proyecto específico
    fun guardarProyecto(context: Context, nombreProyecto: String, mapListas: MutableMap<String, MutableList<MutableList<String>>>) {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convertir el Map a JSON
        val json = gson.toJson(mapListas)
        editor.putString("${nombreProyecto}_mapListas", json)

        // Actualizar fecha de modificación en metadata
        val metadata = cargarMetadataProyecto(context, nombreProyecto)
        metadata?.let {
            it.actualizarFechaModificacion()
            guardarMetadataProyecto(context, nombreProyecto, it)
        }

        editor.apply()
        respaldarADisco(context)
    }

    // Cargar proyecto específico
    fun cargarProyecto(context: Context, nombreProyecto: String): MutableMap<String, MutableList<MutableList<String>>>? {
        asegurarRestauracion(context)
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("${nombreProyecto}_mapListas", null)

        return if (json != null) {
            val type = object : TypeToken<MutableMap<String, MutableList<MutableList<String>>>>() {}.type
            migrarReferencias(gson.fromJson<MutableMap<String, MutableList<MutableList<String>>>>(json, type))
        } else {
            null
        }
    }

    /**
     * Migración: en versiones anteriores la mampara paflón archivaba sus referencias bajo el
     * nombre "Referencias y Cálculos" (la etiqueta del título), pero el recycler reconoce las
     * referencias solo por el nombre literal "Referencias". Aquí se fusiona la clave antigua en
     * "Referencias" al cargar, para que los datos ya archivados también aparezcan.
     */
    private fun migrarReferencias(
        mapa: MutableMap<String, MutableList<MutableList<String>>>?
    ): MutableMap<String, MutableList<MutableList<String>>>? {
        if (mapa == null) return null
        val legado = mapa.remove("Referencias y Cálculos") ?: return mapa
        val destino = mapa.getOrPut("Referencias") { mutableListOf() }
        destino.addAll(legado)
        return mapa
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
        asegurarRestauracion(context)
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
        respaldarADisco(context)
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
        respaldarADisco(context)
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
        respaldarADisco(context)
    }

    private fun cargarMapOriginal(context: Context): MutableMap<String, MutableList<MutableList<String>>>? {
        val sharedPreferences = context.getSharedPreferences("MapStorage", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("mapListas", null)

        return if (json != null) {
            val type = object : TypeToken<MutableMap<String, MutableList<MutableList<String>>>>() {}.type
            migrarReferencias(gson.fromJson<MutableMap<String, MutableList<MutableList<String>>>>(json, type))
        } else {
            null
        }
    }
}
