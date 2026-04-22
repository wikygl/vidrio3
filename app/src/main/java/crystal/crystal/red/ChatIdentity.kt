package crystal.crystal.red

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import java.text.Normalizer
import java.util.Locale

object ChatIdentity {

    fun resolveChatIdentityUid(
        context: Context,
        auth: FirebaseAuth,
        preferredUid: String? = null
    ): String {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val patronUid = prefs.getString("patron_uid", null)

        return preferredUid?.takeIf { it.isNotBlank() }
            ?: patronUid?.takeIf { it.isNotBlank() }
            ?: auth.currentUser?.uid.orEmpty()
    }

    fun resolveChatIdentityAliases(
        context: Context,
        auth: FirebaseAuth,
        preferredUid: String? = null
    ): List<String> {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val patronUid = prefs.getString("patron_uid", null)

        return linkedSetOf(
            preferredUid?.takeIf { it.isNotBlank() },
            patronUid?.takeIf { it.isNotBlank() },
            auth.currentUser?.uid?.takeIf { it.isNotBlank() }
        ).filterNotNull().toList()
    }

    fun resolveFirestoreChatActorUid(
        context: Context,
        auth: FirebaseAuth,
        preferredUid: String? = null
    ): String {
        return auth.currentUser?.uid?.takeIf { it.isNotBlank() }
            ?: resolveChatIdentityUid(context, auth, preferredUid)
    }

    fun buildParticipantsKey(uidA: String, uidB: String): String {
        return listOf(uidA, uidB).sorted().joinToString("_")
    }

    fun buildParticipantsKey(participants: List<String>): String {
        return participants
            .filter { it.isNotBlank() }
            .distinct()
            .sorted()
            .joinToString("_")
    }

    fun normalizeSearchText(value: String): String {
        val trimmed = value.trim().lowercase(Locale.ROOT)
        val normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
        return normalized.replace("\\p{Mn}+".toRegex(), "")
    }
}
