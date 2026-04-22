package crystal.crystal.red.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import crystal.crystal.red.Message
import crystal.crystal.red.interop.ChatInteropDocumentFactory
import crystal.crystal.red.interop.ChatPlatform
import java.util.Date

class ChatSendRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    fun createPendingMessage(
        chatId: String,
        fromUid: String,
        placeholderText: String,
        legacyType: String,
        createdAt: Date,
        fileName: String = "",
        targetApp: ChatPlatform = ChatPlatform.UNKNOWN,
        targetExternalUserId: String = "",
        onSuccess: (DocumentReference, Message) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val chatRef = db.collection("chats").document(chatId)
        val messageRef = chatRef.collection("messages").document()
        val placeholder = Message(
            id = messageRef.id,
            message = placeholderText,
            from = fromUid,
            dob = createdAt,
            leido = false,
            entregado = false,
            tipo = legacyType,
            nombreArchivo = fileName
        ).apply { hasPendingWrites = true }

        chatRef.get().addOnSuccessListener { chatDoc ->
            val users = (chatDoc.get("users") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
            val unreadByUpdate = buildUnreadByUpdate(users, fromUid)

            val batch = db.batch()
            batch.set(
                messageRef,
                ChatInteropDocumentFactory.createMessage(
                    id = placeholder.id,
                    fromUid = fromUid,
                    message = placeholder.message,
                    legacyType = legacyType,
                    createdAt = placeholder.dob,
                    targetApp = targetApp,
                    targetExternalUserId = targetExternalUserId,
                    syncStatus = initialSyncStatus(targetApp, targetExternalUserId),
                    fileName = fileName
                ) + mapOf(
                    "leido" to false,
                    "entregado" to true,
                    "deletedFor" to placeholder.deletedFor,
                    "deletedForEveryone" to placeholder.deletedForEveryone
                )
            )
            if (unreadByUpdate.isNotEmpty()) {
                batch.update(chatRef, unreadByUpdate)
            }
            batch.commit()
                .addOnSuccessListener { onSuccess(messageRef, placeholder) }
                .addOnFailureListener { e -> onError(e) }
        }.addOnFailureListener { e ->
            onError(e)
        }
    }

    fun sendTextMessage(
        chatId: String,
        fromUid: String,
        text: String,
        legacyType: String,
        createdAt: Date,
        targetApp: ChatPlatform = ChatPlatform.UNKNOWN,
        targetExternalUserId: String = "",
        onSuccess: (Message) -> Unit,
        onError: (Exception) -> Unit
    ) {
        val chatRef = db.collection("chats").document(chatId)
        val messageRef = chatRef.collection("messages").document()
        val message = Message(
            id = messageRef.id,
            message = text,
            from = fromUid,
            dob = createdAt,
            leido = false,
            entregado = false,
            tipo = legacyType
        ).apply { hasPendingWrites = true }

        chatRef.get().addOnSuccessListener { chatDoc ->
            val users = (chatDoc.get("users") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
            val unreadByUpdate = buildUnreadByUpdate(users, fromUid)

            val batch = db.batch()
            batch.set(
                messageRef,
                ChatInteropDocumentFactory.createMessage(
                    id = message.id,
                    fromUid = fromUid,
                    message = text,
                    legacyType = legacyType,
                    createdAt = createdAt,
                    targetApp = targetApp,
                    targetExternalUserId = targetExternalUserId,
                    syncStatus = initialSyncStatus(targetApp, targetExternalUserId)
                ) + mapOf(
                    "leido" to false,
                    "entregado" to true,
                    "deletedFor" to message.deletedFor,
                    "deletedForEveryone" to message.deletedForEveryone
                )
            )
            batch.update(
                chatRef,
                ChatInteropDocumentFactory.createChatPreviewUpdate(
                    legacyType = legacyType,
                    previewValue = text,
                    updatedAt = createdAt
                ) + unreadByUpdate
            )
            batch.commit()
                .addOnSuccessListener { onSuccess(message) }
                .addOnFailureListener { e -> onError(e) }
        }.addOnFailureListener { e -> onError(e) }
    }

    fun updateUploadProgress(messageRef: DocumentReference, percent: Int) {
        messageRef.update("message", "CARGANDO... $percent%")
    }

    fun completeUploadedMessage(
        chatId: String,
        messageRef: DocumentReference,
        legacyType: String,
        previewValue: String,
        updatedAt: Date,
        url: String
    ) {
        val chatRef = db.collection("chats").document(chatId)
        messageRef.update(
            ChatInteropDocumentFactory.createFileContentUpdate(url) + mapOf("entregado" to true)
        )
        chatRef.update(
            ChatInteropDocumentFactory.createChatPreviewUpdate(
                legacyType = legacyType,
                previewValue = previewValue,
                updatedAt = updatedAt
            )
        )
    }

    fun failMessage(messageRef: DocumentReference, errorText: String) {
        messageRef.update(
            mapOf(
                "message" to errorText,
                "entregado" to false
            )
        )
    }

    private fun buildUnreadByUpdate(users: List<String>, fromUid: String): Map<String, Any> {
        val updates = mutableMapOf<String, Any>()
        users.filter { it.isNotBlank() && it != fromUid }.forEach { uid ->
            updates["unreadBy.$uid"] = FieldValue.increment(1)
        }
        if (!updates.containsKey("unreadBy.$fromUid")) {
            updates["unreadBy.$fromUid"] = 0
        }
        return updates
    }

    private fun initialSyncStatus(targetApp: ChatPlatform, targetExternalUserId: String): String {
        return if (targetApp == ChatPlatform.PUNTOS && targetExternalUserId.isNotBlank()) {
            "pending_external"
        } else {
            "local"
        }
    }
}
