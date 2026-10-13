package crystal.crystal.tienda

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import crystal.crystal.red.ChatActivity
import crystal.crystal.red.ChatIdentity
import crystal.crystal.red.data.ChatConversationRepository
import crystal.crystal.red.data.ChatDirectoryRepository
import crystal.crystal.red.data.ChatListRepository
import crystal.crystal.red.interop.ChatInteropIntents

/**
 * Abrir el chat de Crystal con alguien de la Tienda —el vendedor de un anuncio, un proveedor de la
 * vitrina— con el primer mensaje ya escrito o con lo que se iba a enviar (medidas, cortes, un plano).
 * Reutiliza `getOrCreateDirectChat`: si ya hablaron, se abre la misma conversación.
 */
object ChatDeTienda {

    /**
     * Abre el chat con [otroUid]. [textoInicial] queda escrito sin enviar; [extras] pasa tal cual a
     * ChatActivity (lo que se compartía desde otra pantalla). Devuelve false si no se pudo y ya lo dijo.
     */
    suspend fun abrir(activity: Activity, otroUid: String, textoInicial: String? = null, extras: Bundle? = null): Boolean {
        val auth = FirebaseAuth.getInstance()
        val yo = ChatIdentity.resolveChatIdentityUid(activity, auth)
        if (yo.isBlank()) return false
        if (yo == otroUid) {
            Toast.makeText(activity, "Es su propia cuenta", Toast.LENGTH_SHORT).show()
            return false
        }
        val db = FirebaseFirestore.getInstance()
        val otro = ChatDirectoryRepository(db).getUser(otroUid)
        if (otro == null) {
            Toast.makeText(activity, "Esta cuenta ya no está disponible en el chat", Toast.LENGTH_LONG).show()
            return false
        }
        val chatId = ChatConversationRepository(db, ChatListRepository(db)).getOrCreateDirectChat(
            queryUserId = yo,
            currentUserId = yo,
            aliases = ChatIdentity.resolveChatIdentityAliases(activity, auth, yo),
            otherUser = otro
        )
        val i = Intent(activity, ChatActivity::class.java)
            .putExtra("chatId", chatId)
            .putExtra("usuario", yo)
        extras?.let { i.putExtras(it) }
        if (!textoInicial.isNullOrBlank()) i.putExtra(ChatInteropIntents.EXTRA_PREFILL_TEXT, textoInicial)
        activity.startActivity(i)
        return true
    }
}
