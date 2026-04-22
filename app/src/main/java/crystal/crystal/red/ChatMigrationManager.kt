package crystal.crystal.red

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class ChatMigrationResult(
    val updatedChats: Int = 0,
    val updatedMessages: Int = 0,
    val scannedChats: Int = 0
)

object ChatMigrationManager {

    suspend fun migrateChatsForUser(
        db: FirebaseFirestore,
        stableUid: String,
        knownAliases: List<String>,
        explicitLegacyUids: List<String> = emptyList()
    ): ChatMigrationResult {
        val legacyUids = (knownAliases + explicitLegacyUids)
            .filter { it.isNotBlank() && it != stableUid }
            .distinct()

        val queryIds = (listOf(stableUid) + legacyUids).distinct()
        val chatDocs = linkedMapOf<String, DocumentSnapshot>()

        queryIds.chunked(10).forEach { chunk ->
            val snap = db.collection("chats")
                .whereArrayContainsAny("users", chunk)
                .get()
                .await()

            snap.documents.forEach { doc ->
                chatDocs[doc.id] = doc
            }
        }

        var updatedChats = 0
        var updatedMessages = 0
        var scannedChats = 0

        for (doc in chatDocs.values) {
            scannedChats++
            val users = (doc.get("users") as? List<*>)?.mapNotNull { it as? String } ?: emptyList()
            if (users.isEmpty()) continue

            val normalizedUsers = users.map { uid ->
                if (uid in legacyUids) stableUid else uid
            }.distinct()

            val participantsKey = ChatIdentity.buildParticipantsKey(normalizedUsers)
            val currentKey = doc.getString("participantsKey").orEmpty()

            if (normalizedUsers != users || currentKey != participantsKey) {
                db.collection("chats").document(doc.id)
                    .update(
                        mapOf(
                            "users" to normalizedUsers,
                            "participantsKey" to participantsKey
                        )
                    )
                    .await()
                updatedChats++
            }

            if (legacyUids.isNotEmpty()) {
                legacyUids.chunked(10).forEach { chunk ->
                    val msgSnap = db.collection("chats")
                        .document(doc.id)
                        .collection("messages")
                        .whereIn("from", chunk)
                        .get()
                        .await()

                    for (msgDoc in msgSnap.documents) {
                        msgDoc.reference.update("from", stableUid).await()
                        updatedMessages++
                    }
                }
            }
        }

        return ChatMigrationResult(
            updatedChats = updatedChats,
            updatedMessages = updatedMessages,
            scannedChats = scannedChats
        )
    }
}
