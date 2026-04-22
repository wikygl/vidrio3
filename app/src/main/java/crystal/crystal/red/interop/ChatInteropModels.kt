package crystal.crystal.red.interop

import crystal.crystal.red.Chat
import java.util.Date

data class ChatInteropParticipant(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val platform: String = ChatPlatform.CRYSTAL.wireValue
)

data class ChatInteropSummary(
    val id: String = "",
    val name: String = "",
    val participants: List<ChatInteropParticipant> = emptyList(),
    val users: List<String> = emptyList(),
    val participantsKey: String = "",
    val peerPlatform: String = ChatPlatform.CRYSTAL.wireValue,
    val lastMessagePreview: String = "",
    val lastMessageType: String = ChatInteropMessageType.TEXT.wireValue,
    val lastMsgDate: Date? = null
) {
    fun toLegacyChat(): Chat {
        return Chat(
            id = id,
            name = name,
            users = users.ifEmpty { participants.map { it.uid }.filter { it.isNotBlank() } },
            participantsKey = participantsKey,
            lastMsgDate = lastMsgDate,
            lastMessageText = lastMessagePreview
        )
    }

    companion object {
        fun fromLegacyChat(chat: Chat, peerPlatform: ChatPlatform = ChatPlatform.CRYSTAL): ChatInteropSummary {
            return ChatInteropSummary(
                id = chat.id,
                name = chat.name,
                users = chat.users,
                participantsKey = chat.participantsKey,
                peerPlatform = peerPlatform.wireValue,
                lastMessagePreview = chat.lastMessageText,
                lastMsgDate = chat.lastMsgDate
            )
        }
    }
}
