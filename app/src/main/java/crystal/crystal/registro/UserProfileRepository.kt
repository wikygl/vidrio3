package crystal.crystal.registro

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import crystal.crystal.red.ChatIdentity
import kotlinx.coroutines.tasks.await

data class EditableUserProfile(
    val uid: String,
    val nombre: String,
    val email: String,
    val imagenPerfil: String,
    val platforms: List<String>,
    val perfilCompleto: Boolean
)

class UserProfileRepository(
    context: Context,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun resolveProfileUid(): String? {
        return prefs.getString("patron_uid", null)
            ?: auth.currentUser?.uid
    }

    suspend fun loadProfile(): EditableUserProfile? {
        val uid = resolveProfileUid() ?: return null
        val current = auth.currentUser
        val doc = firestore.collection("usuarios").document(uid).get().await()

        val nombre = doc.getString("nombre")
            ?: doc.getString("perfil.nombre")
            ?: current?.displayName
            ?: ""
        val email = doc.getString("email")
            ?: doc.getString("perfil.email")
            ?: current?.email
            ?: ""
        val imagenPerfil = doc.getString("imagenPerfil")
            ?: doc.getString("perfil.imagenPerfil")
            ?: current?.photoUrl?.toString()
            ?: ""
        val platforms = (doc.get("platforms") as? List<*>)?.filterIsInstance<String>()
            ?: doc.getString("platform")?.let { listOf(it) }
            ?: listOf("crystal")
        val perfilCompleto = doc.getBoolean("perfilCompleto") ?: false

        return EditableUserProfile(
            uid = uid,
            nombre = nombre,
            email = email,
            imagenPerfil = imagenPerfil,
            platforms = platforms.ifEmpty { listOf("crystal") },
            perfilCompleto = perfilCompleto
        )
    }

    suspend fun hasCompleteProfile(): Boolean {
        val profile = loadProfile() ?: return false
        return profile.perfilCompleto &&
            profile.nombre.isNotBlank() &&
            profile.email.isNotBlank() &&
            profile.email.contains("@")
    }

    suspend fun saveProfile(
        nombre: String,
        email: String,
        imagenPerfil: String,
        platforms: List<String>
    ) {
        val uid = resolveProfileUid() ?: error("No se pudo resolver uid de perfil")
        val normalizedName = ChatIdentity.normalizeSearchText(nombre)
        val normalizedPlatforms = platforms.map { it.trim().lowercase() }.distinct().ifEmpty { listOf("crystal") }

        val data = mapOf(
            "uid" to uid,
            "ownerUid" to uid,
            "externalUserId" to uid,
            "nombre" to nombre,
            "nombreNormalizado" to normalizedName,
            "email" to email.lowercase().trim(),
            "imagenPerfil" to imagenPerfil.trim(),
            "platform" to normalizedPlatforms.first(),
            "platforms" to normalizedPlatforms,
            "canReceive" to listOf("text", "materials_request", "crystal_budget", "file"),
            "perfilCompleto" to true,
            "actualizadoEn" to FieldValue.serverTimestamp(),
            "perfil" to mapOf(
                "nombre" to nombre,
                "nombreNormalizado" to normalizedName,
                "email" to email.lowercase().trim(),
                "imagenPerfil" to imagenPerfil.trim(),
                "externalUserId" to uid,
                "actualizadoEn" to FieldValue.serverTimestamp()
            )
        )

        firestore.collection("usuarios")
            .document(uid)
            .set(data, SetOptions.merge())
            .await()
    }
}
