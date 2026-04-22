package crystal.crystal.red.interop

enum class ChatPlatform(val wireValue: String) {
    CRYSTAL("crystal"),
    PUNTOS("puntos"),
    UNKNOWN("unknown");

    companion object {
        fun fromWireValue(value: String?): ChatPlatform {
            return entries.firstOrNull { it.wireValue == value?.trim()?.lowercase() } ?: UNKNOWN
        }
    }
}
