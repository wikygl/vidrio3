package crystal.crystal.red.interop

import com.google.firebase.firestore.DocumentSnapshot

object ChatPlatformResolver {

    fun resolve(doc: DocumentSnapshot): ChatPlatform {
        val single = doc.getString("platform")
        if (!single.isNullOrBlank()) {
            return ChatPlatform.fromWireValue(single)
        }

        val platforms = (doc.get("platforms") as? List<*>)?.filterIsInstance<String>().orEmpty()
        if (platforms.any { ChatPlatform.fromWireValue(it) == ChatPlatform.PUNTOS }) {
            return ChatPlatform.PUNTOS
        }
        if (platforms.any { ChatPlatform.fromWireValue(it) == ChatPlatform.CRYSTAL }) {
            return ChatPlatform.CRYSTAL
        }

        return ChatPlatform.CRYSTAL
    }
}
