package crystal.crystal.red.data

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import android.util.Log
import crystal.crystal.red.Message
import crystal.crystal.red.interop.toLegacyMessageCompat

class ChatMessageRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    companion object {
        private const val TAG = "ChatMessageRepository"

        /**
         * Cuántos mensajes se traen al abrir un chat.
         *
         * Antes se pedía la conversación ENTERA y se dejaba una escucha viva sobre todos sus
         * mensajes: un chat con miles de mensajes los descargaba todos cada vez que se abría, y el
         * costo crecía sin techo con el uso. Con el tope, abrir un chat cuesta lo mismo el primer
         * día que al año.
         *
         * 300 cubre de sobra lo que se lee de corrido en una conversación de trabajo. Lo más viejo
         * sigue guardado en el servidor: falta paginarlo al desplazarse hacia arriba.
         */
        private const val MAX_MENSAJES_VISIBLES = 300L
    }

    fun observeMessages(
        chatId: String,
        currentUserId: String,
        aliases: List<String>,
        onResult: (List<Message>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        // Se pide DESCENDENTE con tope para quedarse con los ÚLTIMOS mensajes (con ascendente, el
        // límite dejaría los más viejos). La lista se devuelve invertida, en el orden de siempre.
        return db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("dob", Query.Direction.DESCENDING)
            .limit(MAX_MENSAJES_VISIBLES)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snap, e ->
                if (e != null) {
                    onError(e)
                    return@addSnapshotListener
                }
                if (snap == null) return@addSnapshotListener

                val list = mutableListOf<Message>()
                val batch = db.batch()
                val chatRef = db.collection("chats").document(chatId)
                var hasUpdates = false
                var hasUnreadFromOthers = false

                for (doc in snap.documents) {
                    val message = doc.toLegacyMessageCompat() ?: continue
                    message.id = doc.id
                    message.hasPendingWrites = doc.metadata.hasPendingWrites()
                    message.entregado = doc.getBoolean("entregado") ?: message.entregado
                    message.leido = doc.getBoolean("leido") ?: message.leido
                    message.esPedido = doc.getBoolean("esPedido") ?: false
                    message.estadoPedido = doc.getString("estadoPedido") ?: ""
                    message.atendidoPor = doc.getString("atendidoPor") ?: ""
                    message.atendidoNombre = doc.getString("atendidoNombre") ?: ""
                    list.add(message)

                    if (!message.hasPendingWrites && message.from != currentUserId) {
                        if (!message.leido) {
                            hasUnreadFromOthers = true
                        }
                        if (!message.leido) {
                            batch.update(doc.reference, "leido", true)
                            hasUpdates = true
                        }
                    }
                }

                if (hasUpdates) {
                    batch.update(chatRef, "unreadBy.$currentUserId", 0)
                    batch.commit().addOnFailureListener { error ->
                        Log.e(TAG, "No se pudieron actualizar estados del chat", error)
                    }
                } else if (!hasUnreadFromOthers) {
                    chatRef.update("unreadBy.$currentUserId", 0)
                        .addOnFailureListener { error ->
                            Log.e(TAG, "No se pudo limpiar unreadBy del chat", error)
                        }
                }
                // La consulta viene del más nuevo al más viejo; la pantalla los espera al revés.
                list.reverse()
                onResult(list)
            }
    }

    fun deleteForMe(chatId: String, messageId: String, currentUserId: String) {
        db.collection("chats").document(chatId)
            .collection("messages")
            .document(messageId)
            .update("deletedFor", FieldValue.arrayUnion(currentUserId))
    }

    fun deleteForEveryone(chatId: String, messageId: String) {
        db.collection("chats").document(chatId)
            .collection("messages")
            .document(messageId)
            .update(
                mapOf(
                    "message" to "mensaje borrado.",
                    "deletedForEveryone" to true
                )
            )
    }

    fun editTextMessage(chatId: String, messageId: String, newValue: String) {
        db.collection("chats").document(chatId)
            .collection("messages")
            .document(messageId)
            .update("message", newValue)
    }
}
