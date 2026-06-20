package crystal.crystal.red

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging

object CrystalFcmTokenManager {
    private const val TAG = "CrystalFcmToken"
    private const val PREFS = "crystal_fcm"
    private const val PREF_PENDING_TOKEN = "pending_token"

    fun registerCurrentDevice(context: Context, preferredUid: String? = null) {
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token ->
                saveToken(context.applicationContext, token, preferredUid)
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "No se pudo obtener token FCM", error)
            }
    }

    fun saveToken(context: Context, token: String, preferredUid: String? = null) {
        if (token.isBlank()) return

        val auth = FirebaseAuth.getInstance()
        val aliases = ChatIdentity.resolveChatIdentityAliases(context, auth, preferredUid)
        if (aliases.isEmpty()) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(PREF_PENDING_TOKEN, token)
                .apply()
            return
        }

        val data = mapOf(
            "fcmTokens" to FieldValue.arrayUnion(token),
            "fcmTokenUpdatedAt" to FieldValue.serverTimestamp()
        )

        val firestore = FirebaseFirestore.getInstance()
        var pending = aliases.size
        var saved = false
        aliases.forEach { uid ->
            firestore
                .collection("usuarios")
                .document(uid)
                .set(data, SetOptions.merge())
            .addOnSuccessListener {
                saved = true
                pending--
                if (pending == 0 && saved) {
                    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                        .edit()
                        .remove(PREF_PENDING_TOKEN)
                        .apply()
                }
            }
            .addOnFailureListener { error ->
                pending--
                Log.e(TAG, "No se pudo guardar token FCM para $uid", error)
            }
        }
    }

    fun flushPendingToken(context: Context, preferredUid: String? = null) {
        val token = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(PREF_PENDING_TOKEN, null)
            .orEmpty()
        if (token.isNotBlank()) {
            saveToken(context.applicationContext, token, preferredUid)
        }
    }
}
