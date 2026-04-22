package crystal.crystal.red.interop

import crystal.crystal.red.ChatIdentity
import java.util.Date

object ChatInteropDocumentFactory {

    fun createDirectChat(
        chatId: String,
        currentUserId: String,
        otherUid: String,
        name: String,
        peerPlatform: ChatPlatform = ChatPlatform.CRYSTAL,
        peerExternalUserId: String = otherUid,
        peerCompanyName: String = "",
        peerCanReceive: List<String> = emptyList(),
        createdAt: Date = Date()
    ): Map<String, Any?> {
        return mapOf(
            ChatInteropFields.Chat.ID to chatId,
            ChatInteropFields.Chat.NAME to name,
            ChatInteropFields.Chat.USERS to listOf(currentUserId, otherUid),
            ChatInteropFields.Chat.PARTICIPANTS_KEY to ChatIdentity.buildParticipantsKey(currentUserId, otherUid),
            ChatInteropFields.Chat.LAST_MSG_DATE to createdAt,
            ChatInteropFields.Chat.LAST_MESSAGE_PREVIEW to "",
            ChatInteropFields.Chat.LAST_MESSAGE_TYPE to ChatInteropMessageType.TEXT.wireValue,
            ChatInteropFields.Chat.PEER_PLATFORM to peerPlatform.wireValue,
            ChatInteropFields.Chat.PEER_EXTERNAL_USER_ID to peerExternalUserId,
            ChatInteropFields.Chat.PEER_COMPANY_NAME to peerCompanyName,
            ChatInteropFields.Chat.PEER_CAN_RECEIVE to peerCanReceive,
            ChatInteropFields.Chat.UNREAD_BY to mapOf(
                currentUserId to 0,
                otherUid to 0
            )
        )
    }

    fun createMessage(
        id: String,
        fromUid: String,
        message: String,
        legacyType: String,
        createdAt: Date?,
        sourceApp: ChatPlatform = ChatPlatform.CRYSTAL,
        targetApp: ChatPlatform = ChatPlatform.UNKNOWN,
        targetExternalUserId: String = "",
        syncStatus: String = "local",
        fileName: String = ""
    ): Map<String, Any?> {
        return mapOf(
            ChatInteropFields.Message.ID to id,
            ChatInteropFields.Message.MESSAGE to message,
            ChatInteropFields.Message.FROM to fromUid,
            ChatInteropFields.Message.FROM_UID to fromUid,
            ChatInteropFields.Message.CREATED_AT to createdAt,
            ChatInteropFields.Message.LEGACY_TYPE to legacyType,
            ChatInteropFields.Message.TYPE to mapLegacyType(legacyType).wireValue,
            ChatInteropFields.Message.SOURCE_APP to sourceApp.wireValue,
            ChatInteropFields.Message.TARGET_APP to targetApp.wireValue,
            ChatInteropFields.Message.TARGET_EXTERNAL_USER_ID to targetExternalUserId,
            ChatInteropFields.Message.SYNC_STATUS to syncStatus,
            ChatInteropFields.Message.SCHEMA_VERSION to ChatInteropSupport.CURRENT_SCHEMA_VERSION,
            ChatInteropFields.Message.LEGACY_FILE_NAME to fileName,
            ChatInteropFields.Message.FILE_NAME to fileName
        )
    }

    fun createFileContentUpdate(url: String): Map<String, Any?> {
        return mapOf(
            ChatInteropFields.Message.MESSAGE to url,
            ChatInteropFields.Message.FILE_URL to url
        )
    }

    fun createChatPreviewUpdate(
        legacyType: String,
        previewValue: String,
        updatedAt: Date
    ): Map<String, Any?> {
        return mapOf(
            ChatInteropFields.Chat.LAST_MSG_DATE to updatedAt,
            ChatInteropFields.Chat.LAST_MESSAGE_PREVIEW to buildChatPreviewForType(legacyType, previewValue),
            ChatInteropFields.Chat.LAST_MESSAGE_TYPE to mapLegacyType(legacyType).wireValue
        )
    }

    fun mapLegacyType(tipo: String): ChatInteropMessageType {
        return when (tipo.trim().lowercase()) {
            "texto" -> ChatInteropMessageType.TEXT
            "medidas" -> ChatInteropMessageType.MATERIALS_REQUEST
            "presupuesto" -> ChatInteropMessageType.CRYSTAL_BUDGET
            "cotizacion" -> ChatInteropMessageType.PUNTOS_QUOTE
            "archivo", "imagen", "video", "audio", "pdf" -> ChatInteropMessageType.FILE
            else -> ChatInteropMessageType.UNKNOWN
        }
    }

    fun buildChatPreviewForType(tipo: String, value: String): String {
        return when (tipo.trim().lowercase()) {
            "presupuesto" -> "Presupuesto: $value"
            "medidas" -> "Lista de medidas"
            "pdf" -> "PDF: $value"
            "audio" -> "Audio: $value"
            "video" -> "Video: $value"
            "imagen" -> "Imagen"
            else -> value
        }
    }
}
