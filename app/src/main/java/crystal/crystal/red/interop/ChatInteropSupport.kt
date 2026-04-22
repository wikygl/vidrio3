package crystal.crystal.red.interop

object ChatInteropSupport {

    const val CURRENT_SCHEMA_VERSION = 1

    fun isBudgetType(type: String?): Boolean {
        return ChatInteropMessageType.fromWireValue(type) == ChatInteropMessageType.CRYSTAL_BUDGET
    }

    fun isMaterialsRequestType(type: String?): Boolean {
        return ChatInteropMessageType.fromWireValue(type) == ChatInteropMessageType.MATERIALS_REQUEST
    }

    fun isPuntosQuoteType(type: String?): Boolean {
        return ChatInteropMessageType.fromWireValue(type) == ChatInteropMessageType.PUNTOS_QUOTE
    }

    fun normalizeTargetApp(sourceApp: ChatPlatform, explicitTarget: String?): ChatPlatform {
        val resolved = ChatPlatform.fromWireValue(explicitTarget)
        if (resolved != ChatPlatform.UNKNOWN) return resolved
        return when (sourceApp) {
            ChatPlatform.CRYSTAL -> ChatPlatform.CRYSTAL
            ChatPlatform.PUNTOS -> ChatPlatform.PUNTOS
            ChatPlatform.UNKNOWN -> ChatPlatform.UNKNOWN
        }
    }

    fun basePayload(
        schemaVersion: Int = CURRENT_SCHEMA_VERSION,
        sourceApp: ChatPlatform,
        targetApp: ChatPlatform
    ): Map<String, Any> {
        return mapOf(
            "schemaVersion" to schemaVersion,
            "sourceApp" to sourceApp.wireValue,
            "targetApp" to targetApp.wireValue
        )
    }
}
