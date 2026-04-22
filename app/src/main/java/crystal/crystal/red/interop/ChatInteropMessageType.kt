package crystal.crystal.red.interop

enum class ChatInteropMessageType(val wireValue: String) {
    TEXT("text"),
    CRYSTAL_BUDGET("crystal_budget"),
    MATERIALS_REQUEST("materials_request"),
    PUNTOS_QUOTE("puntos_quote"),
    FILE("file"),
    UNKNOWN("unknown");

    companion object {
        fun fromWireValue(value: String?): ChatInteropMessageType {
            return entries.firstOrNull { it.wireValue == value?.trim()?.lowercase() } ?: UNKNOWN
        }
    }
}
