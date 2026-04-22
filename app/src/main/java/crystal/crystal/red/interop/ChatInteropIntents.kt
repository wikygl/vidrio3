package crystal.crystal.red.interop

import android.content.Intent
import android.net.Uri

object ChatInteropIntents {
    const val EXTRA_SEND_BUDGET_URI = "enviar_presupuesto"
    const val EXTRA_BUDGET_NAME = "nombre_presupuesto"
    const val EXTRA_SEND_SHARED_URI = "enviar_archivo_uri"
    const val EXTRA_SEND_SHARED_NAME = "enviar_archivo_nombre"
    const val EXTRA_SEND_SHARED_MIME = "enviar_archivo_mime"
    const val EXTRA_LOAD_BUDGET_JSON = "cargar_presupuesto_json"
    const val EXTRA_LOAD_BUDGET_URI = "cargar_presupuesto_uri"
    const val EXTRA_LOAD_BUDGET_NAME = "cargar_presupuesto_nombre"
    const val EXTRA_IMPORT_MEASURES_TEXT = "importar_medidas_texto"

    fun consumeStringExtra(intent: Intent, key: String): String? {
        return intent.getStringExtra(key)?.also {
            intent.removeExtra(key)
        }
    }

    fun consumeUriExtra(intent: Intent, key: String): Uri? {
        val raw = intent.getStringExtra(key) ?: return null
        intent.removeExtra(key)
        return Uri.parse(raw)
    }
}
