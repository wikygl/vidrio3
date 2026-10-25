package crystal.crystal.taller

import android.content.Context
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File

/**
 * Copia en la nube de las medidas guardadas (Crystal/Medidas), por si el celular se pierde o se malogra.
 *
 * Cada proyecto es un documento `usuarios/{uid}/medidas_nube/{baseName}` con sus archivos de texto
 * tal cual: el .txt, el índice de bocetos y cada dibujo .json. Los PDF no se suben: se vuelven a
 * generar. Recuperar escribe los mismos archivos con los mismos nombres, así el proyecto vuelve igual.
 *
 * Solo se sube lo que cambió desde la última vez (se compara la fecha de los archivos). Sin internet
 * la escritura queda en la cola de Firestore y sale sola cuando vuelve la conexión.
 */
object MedidasNube {

    private const val COLECCION = "medidas_nube"
    private const val PREFS = "medidas_nube"
    // Firestore admite 1 MiB por documento; se deja margen para los demás campos.
    private const val LIMITE_CARACTERES = 900_000
    private const val ESPERA_SERVIDOR_MS = 20_000L

    private val segundoPlano = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    data class ProyectoNube(
        val baseName: String,
        val cliente: String,
        val dibujos: Int,
        val modificado: Long,
        val archivos: List<Pair<String, String>>
    )

    /** [confirmados] = false: se mandó pero el servidor no respondió a tiempo (quedó en cola). */
    data class Resultado(
        val subidos: Int,
        val muyGrandes: List<String>,
        val fallidos: Int,
        val confirmados: Boolean
    )

    fun uid(): String? = FirebaseAuth.getInstance().currentUser?.uid

    private fun col(uid: String) =
        FirebaseFirestore.getInstance().collection("usuarios").document(uid).collection(COLECCION)

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun claveSubido(uid: String, baseName: String) = "subido_${uid}_$baseName"

    fun ultimoRespaldo(context: Context): Long {
        val uid = uid() ?: return 0L
        return prefs(context).getLong("ultimo_$uid", 0L)
    }

    /** El .txt del proyecto, su índice y sus dibujos: misma regla que usa Medidas al eliminarlo. */
    fun archivosDelProyecto(txt: File): List<File> {
        val dir = txt.parentFile ?: return listOf(txt)
        val base = txt.nameWithoutExtension
        val jsons = dir.listFiles { f -> f.name.startsWith("${base}_") && f.extension.equals("json", true) }
            .orEmpty()
            .sortedBy { it.name }
        return listOf(txt) + jsons
    }

    private fun proyectosLocales(dir: File): List<File> =
        dir.listFiles { f -> f.extension.equals("txt", ignoreCase = true) }.orEmpty().toList()

    private fun clienteDe(textoTxt: String, baseName: String): String =
        textoTxt.lineSequence()
            .firstOrNull { it.startsWith("Cliente:", ignoreCase = true) }
            ?.substringAfter(":")?.trim()
            ?.ifBlank { null }
            ?: baseName

    /** Respaldo automático al salir de Medidas: no espera al servidor ni avisa nada. */
    fun respaldarEnSegundoPlano(context: Context, dir: File) {
        if (uid() == null) return
        val app = context.applicationContext
        segundoPlano.launch { runCatching { subir(app, dir, todos = false, esperar = false) } }
    }

    /** Sube lo que cambió (o todo, con [todos]) y espera la confirmación del servidor. */
    suspend fun subirAhora(context: Context, dir: File, todos: Boolean = false): Resultado? =
        subir(context.applicationContext, dir, todos, esperar = true)

    private suspend fun subir(context: Context, dir: File, todos: Boolean, esperar: Boolean): Resultado? =
        withContext(Dispatchers.IO) {
            val uid = uid() ?: return@withContext null
            val prefs = prefs(context)
            val tareas = mutableListOf<Task<Void>>()
            val muyGrandes = mutableListOf<String>()
            var fallidos = 0

            for (txt in proyectosLocales(dir)) {
                val base = txt.nameWithoutExtension
                val archivos = archivosDelProyecto(txt)
                val marca = archivos.maxOf { it.lastModified() }
                val clave = claveSubido(uid, base)
                if (!todos && prefs.getLong(clave, 0L) == marca) continue

                val contenidos = runCatching { archivos.map { it.name to it.readText() } }.getOrNull()
                if (contenidos == null) {
                    fallidos++
                    continue
                }
                if (contenidos.sumOf { it.first.length + it.second.length } > LIMITE_CARACTERES) {
                    muyGrandes += base
                    continue
                }
                val indice = "${base}_bocetos.json"
                val tarea = col(uid).document(base).set(
                    mapOf(
                        "baseName" to base,
                        "cliente" to clienteDe(contenidos.first().second, base),
                        "dibujos" to contenidos.count { it.first != txt.name && it.first != indice },
                        "modificado" to marca,
                        "archivos" to contenidos.map { (nombre, texto) ->
                            mapOf("nombre" to nombre, "contenido" to texto)
                        },
                        "actualizado" to FieldValue.serverTimestamp()
                    )
                )
                // Queda en la cola local de Firestore aunque no haya internet: se marca ya como
                // subido para no volver a mandarlo en cada salida.
                prefs.edit().putLong(clave, marca).apply()
                tareas += tarea
            }

            val confirmados = if (esperar && tareas.isNotEmpty()) {
                withTimeoutOrNull(ESPERA_SERVIDOR_MS) { Tasks.whenAll(tareas).await(); true } ?: false
            } else {
                true
            }
            if (confirmados) prefs.edit().putLong("ultimo_$uid", System.currentTimeMillis()).apply()
            Resultado(tareas.size, muyGrandes, fallidos, confirmados)
        }

    /** Lista lo que hay en la nube, leído del servidor (no del caché). Null si no hay sesión o conexión. */
    suspend fun listar(): List<ProyectoNube>? = withContext(Dispatchers.IO) {
        val uid = uid() ?: return@withContext null
        val snap = withTimeoutOrNull(ESPERA_SERVIDOR_MS) {
            runCatching { col(uid).get(Source.SERVER).await() }.getOrNull()
        } ?: return@withContext null
        snap.documents.mapNotNull { d ->
            @Suppress("UNCHECKED_CAST")
            val archivos = (d.get("archivos") as? List<Map<String, Any?>>).orEmpty().mapNotNull { a ->
                val nombre = a["nombre"] as? String ?: return@mapNotNull null
                // Solo nombres simples: nada de rutas que escriban fuera de Crystal/Medidas.
                if (nombre.contains('/') || nombre.contains('\\') || nombre.startsWith(".")) return@mapNotNull null
                nombre to (a["contenido"] as? String).orEmpty()
            }
            if (archivos.isEmpty()) return@mapNotNull null
            ProyectoNube(
                baseName = d.getString("baseName") ?: d.id,
                cliente = d.getString("cliente").orEmpty().ifBlank { d.id },
                dibujos = d.getLong("dibujos")?.toInt() ?: (archivos.size - 1),
                modificado = d.getLong("modificado") ?: 0L,
                archivos = archivos
            )
        }.sortedByDescending { it.baseName }
    }

    /** Escribe los archivos del proyecto en [dir]. Devuelve cuántos proyectos se recuperaron. */
    suspend fun recuperar(context: Context, dir: File, proyectos: List<ProyectoNube>): Int =
        withContext(Dispatchers.IO) {
            val uid = uid()
            val prefs = prefs(context.applicationContext)
            var n = 0
            for (p in proyectos) {
                val ok = runCatching {
                    p.archivos.forEach { (nombre, texto) -> File(dir, nombre).writeText(texto) }
                }.isSuccess
                if (!ok) continue
                n++
                // Lo recién escrito ya está en la nube: que el próximo respaldo no lo vuelva a subir.
                val txt = File(dir, "${p.baseName}.txt")
                if (uid != null && txt.exists()) {
                    val marca = archivosDelProyecto(txt).maxOf { it.lastModified() }
                    prefs.edit().putLong(claveSubido(uid, p.baseName), marca).apply()
                }
            }
            n
        }

    /** Al eliminar un proyecto en Medidas también se borra su copia en la nube. */
    fun eliminar(context: Context, baseName: String) {
        val uid = uid() ?: return
        prefs(context.applicationContext).edit().remove(claveSubido(uid, baseName)).apply()
        col(uid).document(baseName).delete()
    }
}
