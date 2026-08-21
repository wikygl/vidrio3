package crystal.crystal.datos

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs

/**
 * Respaldo / restauración de la base local de productos (tabla `products`) en Firebase, para que no
 * se pierda al reinstalar la app o cambiar de equipo.
 *  - Productos (nombre, precio, URLs de imágenes) → Firestore: usuarios/{uid}/base_local/{docId}
 *  - Imágenes → Firebase Storage: usuarios/{uid}/base_local/... (se guarda la URL https, durable)
 *
 * Requiere sesión (uid). Sin sesión, las funciones devuelven -1 / false y no hacen nada.
 */
object BaseLocalBackup {

    private fun uid(): String? = FirebaseAuth.getInstance().currentUser?.uid

    private fun col(uid: String) =
        FirebaseFirestore.getInstance().collection("usuarios").document(uid).collection("base_local")

    /** ID de documento estable y válido para Firestore a partir del nombre (que es la PK). */
    private fun docId(nombre: String): String {
        val slug = nombre.trim().lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
        return if (slug.isBlank()) "p_${abs(nombre.hashCode())}" else "${slug.take(80)}_${abs(nombre.hashCode())}"
    }

    /**
     * Sube una imagen (content://, file:// o ruta) a Storage y devuelve su URL https. Las que ya son
     * https se devuelven tal cual (no se re-suben). Si no se puede leer, devuelve null (se omite).
     */
    private suspend fun subirImagen(context: Context, uid: String, imageUri: String): String? {
        if (imageUri.startsWith("http")) return imageUri
        return try {
            val bytes = when {
                imageUri.startsWith("content://") ->
                    context.contentResolver.openInputStream(Uri.parse(imageUri))?.use { it.readBytes() }
                imageUri.startsWith("file://") ->
                    Uri.parse(imageUri).path?.let { p -> File(p).takeIf { it.exists() }?.readBytes() }
                else -> File(imageUri).takeIf { it.exists() }?.readBytes()
            } ?: return null
            val ref = FirebaseStorage.getInstance().reference
                .child("usuarios/$uid/base_local/${System.currentTimeMillis()}_${abs(imageUri.hashCode())}.jpg")
            ref.putBytes(bytes).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Respalda un producto: sube sus imágenes (si hace falta), fija las URLs https resultantes en la
     * base local (imágenes durables y sin re-subir) y escribe el documento en Firestore.
     */
    suspend fun respaldarProducto(context: Context, product: Product): Boolean = withContext(Dispatchers.IO) {
        val uid = uid() ?: return@withContext false
        try {
            val urls = product.imagenes().mapNotNull { subirImagen(context, uid, it) }
            val actualizado = product.copy(imagenes = urls.joinToString("\n"))
            if (actualizado.imagenes != product.imagenes) {
                DatabaseProvider.getInstance(context).productDao().insertProduct(actualizado)
            }
            col(uid).document(docId(product.nombre)).set(
                mapOf(
                    "nombre" to product.nombre,
                    "price" to product.price,
                    "imagenes" to urls,
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /** Elimina el respaldo de un producto en Firestore (por su nombre). */
    suspend fun eliminarRemoto(nombre: String): Unit = withContext(Dispatchers.IO) {
        val uid = uid() ?: return@withContext
        runCatching { col(uid).document(docId(nombre)).delete().await() }
        Unit
    }

    /** Respalda TODA la base local. Devuelve cuántos productos se respaldaron, o -1 si no hay sesión. */
    suspend fun respaldarTodo(context: Context): Int = withContext(Dispatchers.IO) {
        if (uid() == null) return@withContext -1
        val productos = DatabaseProvider.getInstance(context).productDao().getAllProducts()
        var n = 0
        for (p in productos) if (respaldarProducto(context, p)) n++
        n
    }

    /** Restaura la base local desde Firestore (upsert en Room). Devuelve cuántos, o -1 si falla/sin sesión. */
    suspend fun restaurar(context: Context): Int = withContext(Dispatchers.IO) {
        val uid = uid() ?: return@withContext -1
        try {
            val snap = col(uid).get().await()
            val dao = DatabaseProvider.getInstance(context).productDao()
            var n = 0
            for (d in snap.documents) {
                val nombre = d.getString("nombre") ?: continue
                val price = d.getDouble("price") ?: 0.0
                @Suppress("UNCHECKED_CAST")
                val imgs = (d.get("imagenes") as? List<String>) ?: emptyList()
                dao.insertProduct(Product(nombre, price, imgs.joinToString("\n")))
                n++
            }
            n
        } catch (e: Exception) {
            -1
        }
    }
}
