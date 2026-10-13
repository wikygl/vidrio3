package crystal.crystal.tienda

import android.app.Activity
import android.content.Context
import android.text.InputType
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.messaging.FirebaseMessaging

/**
 * La ciudad del taller, guardada en el teléfono. Con ella la Tienda muestra los proveedores y los
 * pedidos de clientes de su zona, y el teléfono se suscribe al tema FCM `pedidos_{ciudad}` para
 * enterarse de un pedido nuevo al momento.
 */
object MiCiudad {

    private const val PREFS = "tienda"
    private const val K_CIUDAD = "mi_ciudad"
    /** La que se escribió al publicar el último anuncio (fase 1): sirve de primera sugerencia. */
    private const val K_ULTIMA = "ultima_ciudad"
    private const val K_TEMA = "tema_pedidos"

    fun get(ctx: Context): String =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(K_CIUDAD, "").orEmpty()

    /** Cambia la ciudad y mueve la suscripción de avisos de la ciudad vieja a la nueva. */
    fun set(ctx: Context, ciudad: String) {
        val p = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val limpia = ciudad.replace(Regex("\\s+"), " ").trim()
        val temaViejo = p.getString(K_TEMA, "").orEmpty()
        val temaNuevo = TiendaReglas.temaPedidos(limpia)
        p.edit().putString(K_CIUDAD, limpia).putString(K_TEMA, temaNuevo).apply()
        if (temaViejo == temaNuevo) return
        runCatching {
            val fcm = FirebaseMessaging.getInstance()
            if (temaViejo.isNotEmpty()) fcm.unsubscribeFromTopic(temaViejo)
            if (temaNuevo.isNotEmpty()) fcm.subscribeToTopic(temaNuevo)
        }
    }

    /**
     * Vuelve a suscribir al abrir la Tienda: una reinstalación o un token nuevo pierden los temas, y
     * suscribirse dos veces al mismo no cuesta nada.
     */
    fun asegurarAvisos(ctx: Context) {
        val tema = TiendaReglas.temaPedidos(get(ctx))
        if (tema.isNotEmpty()) runCatching { FirebaseMessaging.getInstance().subscribeToTopic(tema) }
    }

    /** Pregunta la ciudad (con la actual o la última usada ya escrita) y la guarda. */
    fun pedir(activity: Activity, motivo: String, alGuardar: (String) -> Unit) {
        val p = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val et = EditText(activity).apply {
            hint = "Ej.: Huancayo"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            setText(get(activity).ifBlank { p.getString(K_ULTIMA, "").orEmpty() })
            setSelection(text.length)
        }
        val cont = LinearLayout(activity).apply {
            val m = (20 * activity.resources.displayMetrics.density).toInt()
            setPadding(m, 0, m, 0)
            addView(et)
        }
        AlertDialog.Builder(activity)
            .setTitle("¿En qué ciudad está su taller?")
            .setMessage(motivo)
            .setView(cont)
            .setPositiveButton("Guardar") { _, _ ->
                val c = et.text.toString().trim()
                if (TiendaReglas.claveCiudad(c).length < 2) {
                    Toast.makeText(activity, "Escriba su ciudad", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                set(activity, c)
                alGuardar(get(activity))
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /** La ciudad si ya está; si no, la pregunta primero. */
    fun conCiudad(activity: Activity, motivo: String, accion: (String) -> Unit) {
        val c = get(activity)
        if (c.isNotBlank()) accion(c) else pedir(activity, motivo, accion)
    }
}
