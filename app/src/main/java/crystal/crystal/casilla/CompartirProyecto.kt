package crystal.crystal.casilla

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import crystal.crystal.taller.Taller
import java.io.File

/**
 * Envío de un proyecto por Crystal chat.
 *
 * Vive aquí, y no dentro de Taller, para que el mismo envío esté disponible desde cualquier sitio
 * que muestre las opciones de un proyecto: el botón de Taller y el visor de proyecto activo de
 * todas las calculadoras usan el mismo diálogo.
 */
object CompartirProyecto {

    fun porChat(context: Context, nombre: String) {
        val mapa = MapStorage.cargarProyecto(context, nombre)
        if (mapa.isNullOrEmpty()) {
            Toast.makeText(context, "El proyecto está vacío o no se pudo cargar", Toast.LENGTH_SHORT).show()
            return
        }
        runCatching {
            val envoltura = JsonObject().apply {
                addProperty("format", Taller.FORMAT_PROYECTO_CRYSTAL)
                addProperty("version", 1)
                addProperty("proyecto", nombre)
                addProperty("exportedAt", System.currentTimeMillis())
                add("data", Gson().toJsonTree(mapa))
            }
            val shareDir = File(context.cacheDir, "proyectoshare").apply { mkdirs() }
            val file = File(shareDir, "${sanitizarNombreArchivo(nombre)}.${Taller.EXTENSION_PROYECTO_CRYSTAL}")
            file.writeText(envoltura.toString())
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(context, crystal.crystal.red.ListChatActivity::class.java).apply {
                putExtra(crystal.crystal.red.interop.ChatInteropIntents.EXTRA_SEND_SHARED_URI, uri.toString())
                putExtra(crystal.crystal.red.interop.ChatInteropIntents.EXTRA_SEND_SHARED_NAME, file.name)
                putExtra(crystal.crystal.red.interop.ChatInteropIntents.EXTRA_SEND_SHARED_MIME, Taller.MIME_PROYECTO_CRYSTAL)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                clipData = ClipData.newUri(context.contentResolver, "proyecto_crystal", uri)
            }
            context.startActivity(intent)
        }.onFailure {
            Toast.makeText(context, "No se pudo compartir el proyecto: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sanitizarNombreArchivo(valor: String): String =
        valor.trim().replace(Regex("[^A-Za-z0-9_-]+"), "_").trim('_').ifBlank { "proyecto" }
}
