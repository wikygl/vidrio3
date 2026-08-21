package crystal.crystal

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings

/**
 * Gestiona el permiso "Acceso a todos los archivos" (MANAGE_EXTERNAL_STORAGE), necesario en
 * Android 11+ para que la app lea/escriba en la carpeta pública Descargas/Crystal (medidas y
 * respaldo de proyectos), en vez de quedar encerrada en su almacenamiento privado.
 */
object AccesoArchivos {

    @Volatile private var preguntadoEstaSesion = false

    fun tieneAccesoTotal(): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()

    // Muestra UNA vez por sesión un aviso para conceder el permiso si falta. No bloquea la app.
    fun pedirSiFalta(activity: Activity) {
        if (tieneAccesoTotal() || preguntadoEstaSesion) return
        preguntadoEstaSesion = true
        runCatching {
            AlertDialog.Builder(activity)
                .setTitle("Permitir acceso a archivos")
                .setMessage(
                    "Crystal guarda y lee tus medidas y el respaldo de proyectos en la carpeta " +
                        "Descargas/Crystal. Para eso necesita el permiso \"Acceso a todos los archivos\".\n\n" +
                        "Se concede una sola vez. Si el interruptor aparece en gris, abre el menú de " +
                        "tres puntos (arriba a la derecha) y elige \"Permitir ajustes restringidos\"."
                )
                .setPositiveButton("Abrir ajustes") { _, _ -> abrirAjustes(activity) }
                .setNegativeButton("Ahora no", null)
                .show()
        }
    }

    fun abrirAjustes(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) return
        val directo = Intent(
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
            Uri.parse("package:${activity.packageName}")
        )
        runCatching { activity.startActivity(directo) }
            .onFailure {
                runCatching { activity.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)) }
            }
    }
}
