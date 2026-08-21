package crystal.crystal.red.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import crystal.crystal.red.Chat
import crystal.crystal.red.ChatUserDocReader
import crystal.crystal.red.interop.ChatPlatformResolver
import crystal.crystal.red.interop.toChatPreviewText
import crystal.crystal.red.interop.toLegacyChatCompat
import kotlinx.coroutines.async
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class ChatListRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    companion object {
        private const val TAG = "ChatListRepository"

        // Caché en memoria de datos de usuario (nombre/foto/plataforma) compartida entre instancias.
        // Evita re-consultar usuarios/{uid} en cada emisión del listener (que se dispara con cada
        // cambio de no-leídos / último mensaje), que es lo que hacía lenta la bandeja.
        private val userInfoCache = java.util.concurrent.ConcurrentHashMap<String, UserInfo>()
    }

    private data class UserInfo(val name: String, val photo: String, val platform: String)

    fun observeChats(
        queryUserId: String,
        currentUserId: String,
        aliases: List<String>,
        onResult: (List<Chat>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration {
        return db.collection("chats")
            .whereArrayContains("users", queryUserId)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    onError(err)
                    return@addSnapshotListener
                }

                if (snap == null || snap.isEmpty) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val unreadAliasKeys = aliases.toSet() + queryUserId + currentUserId
                        val baseChats = snap.documents
                            .mapNotNull { doc ->
                                doc.toLegacyChatCompat()?.apply {
                                    if (id.isBlank()) id = doc.id
                                    unreadCount = resolveUnreadCount(doc, unreadAliasKeys)
                                }
                            }
                            .filter { chat -> chat.users.any { it in aliases } }
                            .sortedByDescending { it.lastMsgDate }

                        onResult(baseChats)

                        val hydratedChats = coroutineScope {
                            baseChats.map { chat ->
                                async { hydrateChat(chat, currentUserId, aliases) }
                            }.awaitAll()
                        }

                        onResult(hydratedChats)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading chats", e)
                        onError(e)
                    }
                }
            }
    }

    suspend fun findDirectChat(
        queryUserId: String,
        currentUserId: String,
        aliases: List<String>,
        otherUid: String,
        canonicalKey: String,
        legacyKeys: List<String>
    ): Chat? {
        val snapshot = db.collection("chats")
            .whereArrayContains("users", queryUserId)
            .get()
            .await()

        return snapshot.documents
            .mapNotNull { doc ->
                doc.toLegacyChatCompat()?.apply {
                    if (id.isBlank()) id = doc.id
                    unreadCount = resolveUnreadCount(doc, aliases.toSet() + queryUserId + currentUserId)
                }
            }
            .filter { chat ->
                chat.users.any { it in aliases } && chat.users.contains(otherUid)
            }
            .sortedWith(
                compareByDescending<Chat> { it.participantsKey == canonicalKey }
                    .thenByDescending { it.participantsKey in legacyKeys }
                    .thenByDescending { it.lastMsgDate }
            )
            .firstOrNull()
    }

    private suspend fun hydrateChat(chat: Chat, currentUserId: String, aliases: List<String>): Chat {
        val ownIds = aliases.toSet() + currentUserId
        val otherUid = chat.users.firstOrNull { it !in ownIds } ?: currentUserId

        if (chat.esSoporte) {
            chat.name = "Soporte Crystal"
            chat.photoUrl = ""
        } else if (otherUid == currentUserId) {
            chat.name = "Mensajes guardados"
            chat.photoUrl = ""
        } else {
            val info = userInfoCache[otherUid] ?: run {
                val otherUserDoc = db.collection("usuarios")
                    .document(otherUid)
                    .get()
                    .await()
                val cargado = UserInfo(
                    name = ChatUserDocReader.getName(otherUserDoc)?.takeIf { it.isNotBlank() }.orEmpty(),
                    photo = ChatUserDocReader.getPhotoUrl(otherUserDoc).orEmpty(),
                    platform = ChatPlatformResolver.resolve(otherUserDoc).wireValue
                )
                userInfoCache[otherUid] = cargado
                cargado
            }
            chat.name = info.name.ifBlank { chat.name.ifBlank { "Usuario" } }
            chat.photoUrl = info.photo
            chat.peerPlatform = info.platform
        }

        if (chat.lastMessageText.isBlank()) {
            val lastSnapshot = db.collection("chats")
                .document(chat.id)
                .collection("messages")
                .orderBy("dob", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            chat.lastMessageText = if (!lastSnapshot.isEmpty) {
                lastSnapshot.documents.first().toChatPreviewText()
            } else {
                ""
            }
        }

        return chat
    }

    private fun resolveUnreadCount(
        doc: com.google.firebase.firestore.DocumentSnapshot,
        aliases: Set<String>
    ): Int {
        val unreadBy = doc.get("unreadBy") as? Map<*, *> ?: return 0
        aliases.forEach { alias ->
            val value = unreadBy[alias] ?: return@forEach
            return value.toString().toIntOrNull() ?: 0
        }
        return 0
    }
}
