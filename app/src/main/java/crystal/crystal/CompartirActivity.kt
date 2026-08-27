package crystal.crystal

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

/**
 * Puente para lo que llega compartido desde otra aplicación.
 *
 * **Por qué existe.** Cuando MainActivity recibía el envío directamente, acababa metida dentro de la
 * tarea de quien comparte: en el conmutador aparecía "Telegram" y al abrirlo salía Crystal. Cerrar
 * Telegram se llevaba Crystal por delante. Se intentó arreglar por manifiesto tres veces
 * —`documentLaunchMode="never"`, `launchMode="singleTask"`, y las dos juntas— y ninguna sirvió: el
 * menú de compartir lanza con `NEW_TASK | MULTIPLE_TASK | NEW_DOCUMENT`, y esa última bandera es
 * incompatible con `singleTask`, así que el sistema descarta el modo y hace lo que quiere.
 *
 * La solución no depende de cómo el fabricante interprete las banderas: esta pantalla no tiene
 * interfaz y da igual en qué tarea caiga. Recoge lo compartido, se lo pasa a Crystal en SU tarea y
 * se cierra. Lo que quede en la tarea ajena es esto, que ya no existe.
 *
 * **Por qué copia el archivo.** El permiso de lectura sobre el `content://` de la otra app se
 * concede mientras viva la pantalla que lo recibió. Al cerrarse esta, se revoca — y MainActivity, ya
 * en otra tarea, se quedaría con una dirección que no puede abrir. Por eso se copia a la caché
 * propia y se reenvía como `FileProvider`, que sí sigue siendo legible después.
 */
class CompartirActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val entrante = intent
        // Copiar puede tardar con un vídeo, y bloquear aquí congelaría la animación de la otra app.
        Thread {
            val copias = extraerUris(entrante).mapNotNull { copiar(it) }
            runOnUiThread {
                if (!isFinishing && !isDestroyed) {
                    reenviar(entrante, copias)
                    finish()
                }
            }
        }.start()
    }

    private fun extraerUris(i: Intent): List<Uri> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val varias = i.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)
            if (!varias.isNullOrEmpty()) return varias
            return listOfNotNull(i.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java))
        }
        @Suppress("DEPRECATION")
        val varias = i.getParcelableArrayListExtra<Uri>(Intent.EXTRA_STREAM)
        if (!varias.isNullOrEmpty()) return varias
        @Suppress("DEPRECATION")
        return listOfNotNull(i.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))
    }

    /** Copia a la caché propia y devuelve una dirección que Crystal podrá abrir después. */
    private fun copiar(origen: Uri): Uri? = try {
        val carpeta = File(cacheDir, "compartido").apply { mkdirs() }
        val destino = File(carpeta, "${System.currentTimeMillis()}_${nombreDe(origen)}")
        contentResolver.openInputStream(origen)!!.use { entrada ->
            destino.outputStream().use { salida -> entrada.copyTo(salida) }
        }
        FileProvider.getUriForFile(this, "$packageName.fileprovider", destino)
    } catch (e: Exception) {
        null
    }

    /** El nombre real si el proveedor lo da; si no, uno inventado con la extensión del tipo. */
    private fun nombreDe(uri: Uri): String {
        val crudo = try {
            contentResolver.query(uri, null, null, null, null)?.use { c ->
                val i = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (i >= 0 && c.moveToFirst()) c.getString(i) else null
            }
        } catch (e: Exception) { null }
        val limpio = crudo?.replace(Regex("[^A-Za-z0-9._-]"), "_")?.takeIf { it.isNotBlank() }
        if (limpio != null) return limpio
        val ext = android.webkit.MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri)) ?: "bin"
        return "compartido.$ext"
    }

    private fun reenviar(entrante: Intent, copias: List<Uri>) {
        if (copias.isEmpty() && entrante.getStringExtra(Intent.EXTRA_TEXT).isNullOrBlank()) {
            Toast.makeText(this, "No se pudo leer lo que compartiste.", Toast.LENGTH_LONG).show()
            return
        }
        val i = Intent(this, MainActivity::class.java).apply {
            action = entrante.action
            type = entrante.type
            // NEW_TASK lleva el envío a la tarea de Crystal (misma afinidad, se reutiliza la que ya
            // hay); CLEAR_TOP se lo entrega a la MainActivity existente por `onNewIntent`. Aquí NO
            // van MULTIPLE_TASK ni NEW_DOCUMENT: son justo las que abrían una Crystal por envío.
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            when {
                copias.size == 1 -> putExtra(Intent.EXTRA_STREAM, copias[0])
                copias.size > 1 -> putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(copias))
            }
            entrante.getStringExtra(Intent.EXTRA_TEXT)?.let { putExtra(Intent.EXTRA_TEXT, it) }
            entrante.getStringExtra(Intent.EXTRA_SUBJECT)?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
        }
        startActivity(i)
    }
}
