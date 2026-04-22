package crystal.crystal.red

import com.google.firebase.firestore.DocumentSnapshot

object ChatUserDocReader {

    fun getName(doc: DocumentSnapshot): String? {
        return doc.getString("nombre") ?: doc.getString("perfil.nombre")
    }

    fun getEmail(doc: DocumentSnapshot): String? {
        return doc.getString("email")?.lowercase()?.trim()
            ?: doc.getString("perfil.email")?.lowercase()?.trim()
    }

    fun getPhotoUrl(doc: DocumentSnapshot): String? {
        return doc.getString("imagenPerfil") ?: doc.getString("perfil.imagenPerfil")
    }
}
