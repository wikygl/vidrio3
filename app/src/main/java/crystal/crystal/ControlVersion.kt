package crystal.crystal

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Exige una versión mínima de la app, leída de `config/app` en Firestore.
 *
 * Durante las pruebas de campo se reparten builds seguido. Sin este control, cuando se corrige algo
 * que reportó un probador, los demás siguen con la versión vieja: vuelven a chocar con lo mismo y
 * lo vuelven a reportar. Peor todavía, se termina diagnosticando contra código que ya no existe.
 *
 * Documento esperado (todos los campos opcionales):
 * ```
 * config/app {
 *   versionMinima: 3,                     // versionCode mínimo aceptado
 *   versionMensaje: "Corrige el cálculo de vitroven",
 *   versionUrl: "https://..."             // de dónde bajar la nueva
 * }
 * ```
 * Si el documento no existe o no se puede leer, NO bloquea: preferimos dejar pasar antes que dejar
 * a alguien fuera de la app por una falla de red.
 */
object ControlVersion {

    private const val TAG = "ControlVersion"
    private var yaVerificado = false

    /** Llamar desde la pantalla principal. Bloquea con un diálogo sin salida si la versión venció. */
    fun verificar(activity: Activity) {
        if (yaVerificado) return
        yaVerificado = true

        FirebaseFirestore.getInstance().collection("config").document("app").get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener
                val minima = (doc.getLong("versionMinima") ?: 0L).toInt()
                val actual = BuildConfig.VERSION_CODE
                if (actual >= minima) return@addOnSuccessListener

                Log.w(TAG, "Versión $actual por debajo de la mínima $minima")
                if (activity.isFinishing || activity.isDestroyed) return@addOnSuccessListener
                mostrarBloqueo(
                    activity,
                    doc.getString("versionMensaje").orEmpty(),
                    doc.getString("versionUrl").orEmpty()
                )
            }
            .addOnFailureListener { e ->
                // Sin conexión o sin permiso: no se bloquea a nadie.
                Log.d(TAG, "No se pudo verificar la versión: ${e.message}")
            }
    }

    private fun mostrarBloqueo(activity: Activity, mensaje: String, url: String) {
        val detalle = if (mensaje.isBlank()) {
            "Hay una versión nueva de Crystal con correcciones importantes."
        } else mensaje

        val dialogo = AlertDialog.Builder(activity)
            .setTitle("Actualiza Crystal")
            .setMessage("$detalle\n\nTu versión: ${BuildConfig.VERSION_NAME}")
            .setCancelable(false)

        if (url.isNotBlank()) {
            dialogo.setPositiveButton("Descargar") { _, _ ->
                runCatching {
                    activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
                activity.finish()
            }
        }
        dialogo.setNegativeButton("Salir") { _, _ -> activity.finish() }
        dialogo.show()
    }
}
