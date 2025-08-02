package crystal.crystal.red

import java.util.Date

data class Chat(
    var id: String = "",
    var name: String = "",
    var users: List<String> = emptyList(),
    var lastMsgDate: Date? = null,
    var unreadCount: Int = 0,
    var lastMessageText: String = ""    // <— nuevo campo
)
