package crystal.crystal.red

import java.util.Date

data class Chat(
    var id: String = "",
    var name: String = "",
    var users: List<String> = emptyList(),
    var peerPlatform: String = "crystal",
    var participantsKey: String = "",
    var photoUrl: String = "",
    var lastMsgDate: Date? = null,
    var unreadCount: Int = 0,
    var lastMessageText: String = "",
    var esSoporte: Boolean = false
)
