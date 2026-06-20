package crystal.crystal.red.interop

import com.google.firebase.firestore.DocumentSnapshot
import crystal.crystal.red.Chat
import crystal.crystal.red.Message

fun DocumentSnapshot.toLegacyMessageCompat(): Message? {
    val from = getString(ChatInteropFields.Message.FROM)
        ?: getString(ChatInteropFields.Message.FROM_UID)
        ?: return null

    val type = getString(ChatInteropFields.Message.LEGACY_TYPE)
        ?: mapWireTypeToLegacy(getString(ChatInteropFields.Message.TYPE))

    return Message(
        id = getString(ChatInteropFields.Message.ID).orEmpty().ifBlank { id },
        message = getString(ChatInteropFields.Message.MESSAGE)
            ?: getString(ChatInteropFields.Message.FILE_URL)
            ?: "",
        from = from,
        dob = getDate(ChatInteropFields.Message.CREATED_AT),
        leido = getBoolean("leido") ?: false,
        entregado = getBoolean("entregado") ?: false,
        deletedFor = (get("deletedFor") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        deletedForEveryone = getBoolean("deletedForEveryone") ?: false,
        tipo = type,
        nombreArchivo = getString(ChatInteropFields.Message.LEGACY_FILE_NAME)
            ?: getString(ChatInteropFields.Message.FILE_NAME)
            ?: ""
    )
}

fun DocumentSnapshot.toLegacyChatCompat(): Chat? {
    val users = (get(ChatInteropFields.Chat.USERS) as? List<*>)?.filterIsInstance<String>() ?: emptyList()
    if (users.isEmpty() && getString(ChatInteropFields.Chat.NAME).isNullOrBlank()) return null

    return Chat(
        id = getString(ChatInteropFields.Chat.ID).orEmpty().ifBlank { id },
        name = getString(ChatInteropFields.Chat.NAME).orEmpty(),
        users = users,
        peerPlatform = getString(ChatInteropFields.Chat.PEER_PLATFORM).orEmpty().ifBlank { ChatPlatform.CRYSTAL.wireValue },
        participantsKey = getString(ChatInteropFields.Chat.PARTICIPANTS_KEY).orEmpty(),
        photoUrl = "",
        lastMsgDate = getDate(ChatInteropFields.Chat.LAST_MSG_DATE),
        unreadCount = 0,
        lastMessageText = getString("lastMessageText")
            ?: getString(ChatInteropFields.Chat.LAST_MESSAGE_PREVIEW)
            ?: ""
    )
}

fun DocumentSnapshot.toChatPreviewText(): String {
    val legacyType = getString(ChatInteropFields.Message.LEGACY_TYPE)
    val wireType = getString(ChatInteropFields.Message.TYPE)
    val fileName = getString(ChatInteropFields.Message.LEGACY_FILE_NAME)
        ?: getString(ChatInteropFields.Message.FILE_NAME)
        ?: "archivo"
    val message = getString(ChatInteropFields.Message.MESSAGE).orEmpty()

    return when (legacyType ?: mapWireTypeToLegacy(wireType)) {
        "presupuesto" -> "Presupuesto: $fileName"
        "pdf" -> "PDF: $fileName"
        "audio" -> "Audio: $fileName"
        "video" -> "Video: $fileName"
        "imagen" -> "Imagen"
        "medidas" -> "Lista de medidas"
        "medidas_crystal" -> "Medidas Crystal: $fileName"
        "corte_crystal" -> "Corte Crystal: $fileName"
        "plancha_crystal" -> "Corte Plancha Crystal: $fileName"
        "cotizacion" -> "Cotizacion"
        else -> message
    }
}

private fun mapWireTypeToLegacy(type: String?): String {
    return when (ChatInteropMessageType.fromWireValue(type)) {
        ChatInteropMessageType.TEXT -> "texto"
        ChatInteropMessageType.CRYSTAL_BUDGET -> "presupuesto"
        ChatInteropMessageType.MATERIALS_REQUEST -> "medidas"
        ChatInteropMessageType.PUNTOS_QUOTE -> "cotizacion"
        ChatInteropMessageType.FILE -> "archivo"
        ChatInteropMessageType.UNKNOWN -> "texto"
    }
}
