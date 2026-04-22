package crystal.crystal.red.data

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import crystal.crystal.red.ChatUserDocReader
import crystal.crystal.red.ContactoTelefono
import crystal.crystal.red.interop.ChatPlatform
import crystal.crystal.red.interop.ChatPlatformResolver

data class ChatDirectoryUser(
    val uid: String,
    val name: String,
    val email: String,
    val platform: ChatPlatform,
    val externalUserId: String,
    val companyName: String,
    val canReceive: List<String>
)

class ChatDirectoryRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun enrichPhoneContacts(contactos: List<ContactoTelefono>): List<ContactoTelefono> {
        if (contactos.isEmpty()) return contactos

        val enriched = contactos.map { it.copy() }.toMutableList()
        val emailToIndex = enriched.mapIndexed { index, contacto -> contacto.email to index }.toMap()
        val emails = enriched.map { it.email }

        for (i in emails.indices step 10) {
            val batch = emails.subList(i, minOf(i + 10, emails.size))
            applyMatches(
                docs = db.collection("usuarios").whereIn("perfil.email", batch).get().await().documents,
                emailToIndex = emailToIndex,
                contactos = enriched
            )
            applyMatches(
                docs = db.collection("usuarios").whereIn("email", batch).get().await().documents,
                emailToIndex = emailToIndex,
                contactos = enriched
            )
        }

        return enriched
    }

    suspend fun findByEmail(email: String): ChatDirectoryUser? {
        val normalized = email.lowercase().trim()

        val profileMatch = db.collection("usuarios")
            .whereEqualTo("perfil.email", normalized)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()

        val doc = profileMatch ?: db.collection("usuarios")
            .whereEqualTo("email", normalized)
            .limit(1)
            .get()
            .await()
            .documents
            .firstOrNull()

        return doc?.toDirectoryUser()
    }

    suspend fun searchByNameOrEmail(term: String, excludeUid: String): ChatDirectoryUser? {
        val normalized = term.trim().lowercase()
        val snapshot = db.collection("usuarios").get().await()

        return snapshot.documents.firstOrNull { doc ->
            val name = ChatUserDocReader.getName(doc).orEmpty().trim().lowercase()
            val email = ChatUserDocReader.getEmail(doc).orEmpty().trim().lowercase()
            doc.id != excludeUid && (name.contains(normalized) || email.contains(normalized))
        }?.toDirectoryUser()
    }

    suspend fun getUser(uid: String): ChatDirectoryUser? {
        val doc = db.collection("usuarios").document(uid).get().await()
        return if (doc.exists()) doc.toDirectoryUser() else null
    }

    private fun applyMatches(
        docs: List<DocumentSnapshot>,
        emailToIndex: Map<String, Int>,
        contactos: MutableList<ContactoTelefono>
    ) {
        docs.forEach { doc ->
            val email = ChatUserDocReader.getEmail(doc) ?: return@forEach
            val index = emailToIndex[email] ?: return@forEach
            val previous = contactos[index]
            contactos[index] = previous.copy(
                tieneCrystal = true,
                platform = ChatPlatformResolver.resolve(doc).wireValue
            )
        }
    }

    private fun DocumentSnapshot.toDirectoryUser(): ChatDirectoryUser? {
        val email = ChatUserDocReader.getEmail(this).orEmpty()
        val name = ChatUserDocReader.getName(this).orEmpty().ifBlank { email.ifBlank { id } }
        return ChatDirectoryUser(
            uid = id,
            name = name,
            email = email,
            platform = ChatPlatformResolver.resolve(this),
            externalUserId = getString("externalUserId")
                ?.takeIf { it.isNotBlank() }
                ?: id,
            companyName = getString("empresaNombre")
                ?: getString("companyName")
                ?: "",
            canReceive = (get("canReceive") as? List<*>)?.filterIsInstance<String>().orEmpty()
        )
    }
}
