package crystal.crystal.red.data

import com.google.firebase.firestore.FirebaseFirestore
import crystal.crystal.red.ChatIdentity
import crystal.crystal.red.interop.ChatInteropDocumentFactory
import java.util.Date
import java.util.UUID
import kotlinx.coroutines.tasks.await

class ChatConversationRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val chatListRepository: ChatListRepository = ChatListRepository(db)
) {

    suspend fun getOrCreateDirectChat(
        queryUserId: String,
        currentUserId: String,
        aliases: List<String>,
        otherUser: ChatDirectoryUser
    ): String {
        val canonicalKey = ChatIdentity.buildParticipantsKey(queryUserId, otherUser.uid)
        val legacyKeys = aliases.map { ChatIdentity.buildParticipantsKey(it, otherUser.uid) }
            .plus(listOf(canonicalKey))
            .distinct()

        val existingChat = chatListRepository.findDirectChat(
            queryUserId = queryUserId,
            currentUserId = currentUserId,
            aliases = aliases,
            otherUid = otherUser.uid,
            canonicalKey = canonicalKey,
            legacyKeys = legacyKeys
        )

        if (existingChat != null) {
            if (existingChat.participantsKey != canonicalKey) {
                db.collection("chats")
                    .document(existingChat.id)
                    .update("participantsKey", canonicalKey)
                    .await()
            }
            return existingChat.id
        }

        val chatId = UUID.randomUUID().toString()
        val chatDocument = ChatInteropDocumentFactory.createDirectChat(
            chatId = chatId,
            currentUserId = queryUserId,
            otherUid = otherUser.uid,
            name = otherUser.name,
            peerPlatform = otherUser.platform,
            peerExternalUserId = otherUser.externalUserId,
            peerCompanyName = otherUser.companyName,
            peerCanReceive = otherUser.canReceive,
            createdAt = Date()
        )

        db.collection("chats")
            .document(chatId)
            .set(chatDocument)
            .await()

        return chatId
    }
}
