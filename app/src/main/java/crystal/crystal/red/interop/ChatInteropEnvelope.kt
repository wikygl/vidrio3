package crystal.crystal.red.interop

import com.google.firebase.firestore.Exclude
import crystal.crystal.red.Message
import java.util.Date

data class ChatInteropEnvelope(
    var id: String = "",
    var chatId: String = "",
    var text: String = "",
    var fromUid: String = "",
    var fromDisplayName: String = "",
    var type: String = ChatInteropMessageType.TEXT.wireValue,
    var sourceApp: String = ChatPlatform.CRYSTAL.wireValue,
    var targetApp: String = ChatPlatform.UNKNOWN.wireValue,
    var schemaVersion: Int = 1,
    var payload: Map<String, Any?> = emptyMap(),
    var fileUrl: String = "",
    var fileName: String = "",
    var createdAt: Date? = null,
    var legacyFrom: String = "",
    var legacyTipo: String = "texto"
) {
    @get:Exclude
    @set:Exclude
    var hasPendingWrites: Boolean = false

    fun resolvedType(): ChatInteropMessageType = ChatInteropMessageType.fromWireValue(type)

    fun resolvedSourceApp(): ChatPlatform = ChatPlatform.fromWireValue(sourceApp)

    fun resolvedTargetApp(): ChatPlatform = ChatPlatform.fromWireValue(targetApp)

    fun toLegacyMessage(): Message {
        return Message(
            id = id,
            message = text,
            from = fromUid.ifBlank { legacyFrom },
            dob = createdAt,
            tipo = when (resolvedType()) {
                ChatInteropMessageType.TEXT -> "texto"
                ChatInteropMessageType.CRYSTAL_BUDGET -> "presupuesto"
                ChatInteropMessageType.MATERIALS_REQUEST -> "medidas"
                ChatInteropMessageType.PUNTOS_QUOTE -> "cotizacion"
                ChatInteropMessageType.FILE -> "archivo"
                ChatInteropMessageType.UNKNOWN -> legacyTipo.ifBlank { "texto" }
            },
            nombreArchivo = fileName
        ).also {
            it.hasPendingWrites = hasPendingWrites
        }
    }

    companion object {
        fun fromLegacyMessage(
            chatId: String,
            message: Message,
            sourceApp: ChatPlatform = ChatPlatform.CRYSTAL,
            targetApp: ChatPlatform = ChatPlatform.UNKNOWN
        ): ChatInteropEnvelope {
            return ChatInteropEnvelope(
                id = message.id,
                chatId = chatId,
                text = message.message,
                fromUid = message.from,
                type = mapLegacyType(message.tipo).wireValue,
                sourceApp = sourceApp.wireValue,
                targetApp = targetApp.wireValue,
                fileName = message.nombreArchivo,
                createdAt = message.dob,
                legacyFrom = message.from,
                legacyTipo = message.tipo
            ).also {
                it.hasPendingWrites = message.hasPendingWrites
            }
        }

        private fun mapLegacyType(tipo: String): ChatInteropMessageType {
            return when (tipo.trim().lowercase()) {
                "texto" -> ChatInteropMessageType.TEXT
                "medidas" -> ChatInteropMessageType.MATERIALS_REQUEST
                "presupuesto" -> ChatInteropMessageType.CRYSTAL_BUDGET
                "cotizacion" -> ChatInteropMessageType.PUNTOS_QUOTE
                "archivo", "imagen", "video", "audio", "pdf", "medidas_crystal", "corte_crystal", "plancha_crystal" -> ChatInteropMessageType.FILE
                else -> ChatInteropMessageType.UNKNOWN
            }
        }
    }
}
