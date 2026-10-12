package crystal.crystal.tienda

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Todo lo que la Tienda habla con Firebase: la lista, el anuncio, las fotos, el cobro (por las
 * funciones `publicarAnuncio` y `renovarAnuncio`, que descuentan del wallet en el servidor), las
 * ediciones del dueño y las denuncias.
 */
class TiendaRepositorio {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val funciones = FirebaseFunctions.getInstance()
    private val col = db.collection(COLECCION)

    /** Quien publica y paga: la cuenta con sesión (una terminal no publica; ver el plan). */
    val uid: String? get() = FirebaseAuth.getInstance().currentUser?.uid

    fun nuevoId(): String = col.document().id

    data class Pagina(val anuncios: List<Anuncio>, val ultimo: DocumentSnapshot?, val hayMas: Boolean)

    /**
     * Una página de la tienda, lo más nuevo primero. Al servidor se le pide UN filtro —la palabra
     * buscada, si no la categoría, si no el tipo— porque cada combinación pide su propio índice; lo
     * demás se filtra aquí sobre lo que llegó.
     */
    suspend fun buscar(
        tipo: String?,
        categoria: String?,
        texto: String,
        despuesDe: DocumentSnapshot?
    ): Pagina {
        var q: Query = col.whereEqualTo("estado", Anuncio.ESTADO_ACTIVO)
        val palabra = TiendaReglas.palabraDeBusqueda(texto)
        q = when {
            palabra != null -> q.whereArrayContainsAny("palabras", variantes(palabra))
            categoria != null -> q.whereEqualTo("categoria", categoria)
            tipo != null -> q.whereEqualTo("tipo", tipo)
            else -> q
        }
        q = q.orderBy("publicadoEn", Query.Direction.DESCENDING).limit(PAGINA.toLong())
        if (despuesDe != null) q = q.startAfter(despuesDe)
        val snap = q.get().await()
        val ahora = System.currentTimeMillis()
        val anuncios = snap.documents.mapNotNull { Anuncio.desde(it) }.filter { a ->
            a.visibleEnLaTienda(ahora) &&
                (categoria == null || a.categoria == categoria) &&
                (tipo == null || a.tipo == tipo) &&
                (texto.isBlank() || TiendaReglas.casa(a, texto))
        }
        return Pagina(anuncios, snap.documents.lastOrNull(), snap.size() == PAGINA)
    }

    /** "ventanas" busca también "ventana" y al revés, que es como quedan guardadas las palabras. */
    private fun variantes(p: String): List<String> =
        listOf(p, p + "s", p + "es", TiendaReglas.singular(p)).distinct().take(10)

    /** Los anuncios propios, en cualquier estado. */
    suspend fun misAnuncios(): List<Anuncio> {
        val yo = uid ?: return emptyList()
        return col.whereEqualTo("uid", yo)
            .orderBy("publicadoEn", Query.Direction.DESCENDING)
            .limit(100)
            .get().await()
            .documents.mapNotNull { Anuncio.desde(it) }
    }

    suspend fun obtener(id: String): Anuncio? = Anuncio.desde(col.document(id).get().await())

    /** Quien abre un anuncio ajeno suma una vista. Si falla, no importa. */
    fun sumarVista(id: String) {
        col.document(id).update("vistas", FieldValue.increment(1))
    }

    /** Lo que cuesta publicar hoy (de `config/anuncios`) y el saldo del usuario, en céntimos. */
    suspend fun precioYSaldo(): Triple<Long, Int, Long> {
        val cfg = runCatching { db.collection("config").document("anuncios").get().await() }.getOrNull()
        val (precio, dias) = TiendaReglas.precioVigente(cfg?.getLong("precio_cent"), cfg?.getLong("dias"))
        val saldo = uid?.let { yo ->
            runCatching { db.collection("usuarios").document(yo).get().await().getLong("wallet_saldo_cent") }.getOrNull()
        } ?: 0L
        return Triple(precio, dias, saldo)
    }

    /**
     * Sube una foto en sus dos tamaños a `anuncios/{uid}/{anuncioId}/` y devuelve (grande, miniatura).
     * El nombre lleva la hora: una foto cambiada en una edición no choca con la que tenían en caché.
     */
    suspend fun subirFoto(context: Context, anuncioId: String, uri: Uri, orden: Int): Pair<String, String> {
        val yo = uid ?: throw IllegalStateException("Inicie sesión.")
        val (grande, mini) = withContext(Dispatchers.IO) { FotosAnuncio.preparar(context, uri) }
            ?: throw IllegalStateException("No se pudo leer una de las fotos.")
        val base = "$COLECCION/$yo/$anuncioId/${System.currentTimeMillis()}_$orden"
        // Las fotos no cambian nunca (cada cambio es otro archivo): el teléfono las guarda un año y
        // no las vuelve a bajar, que es justo lo que cuesta.
        val meta = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .setCacheControl("public, max-age=31536000")
            .build()
        val refGrande = storage.reference.child("$base.jpg")
        val refMini = storage.reference.child("${base}_mini.jpg")
        refGrande.putBytes(grande, meta).await()
        refMini.putBytes(mini, meta).await()
        return refGrande.downloadUrl.await().toString() to refMini.downloadUrl.await().toString()
    }

    /** Borra fotos que ya no usa el anuncio. Si alguna no se puede, queda; no detiene nada. */
    suspend fun borrarFotos(urls: List<String>) {
        for (u in urls) runCatching { storage.getReferenceFromUrl(u).delete().await() }
    }

    /** Cobra y publica en el servidor. Devuelve el saldo que quedó, en céntimos. */
    suspend fun publicar(anuncioId: String, campos: Map<String, Any>): Long {
        val r = funciones.getHttpsCallable("publicarAnuncio")
            .call(campos + ("anuncioId" to anuncioId))
            .await()
        return ((r.data as? Map<*, *>)?.get("saldoCent") as? Number)?.toLong() ?: -1L
    }

    /** Cobra otro período. Devuelve el nuevo vencimiento en ms. */
    suspend fun renovar(anuncioId: String): Long {
        val r = funciones.getHttpsCallable("renovarAnuncio").call(mapOf("anuncioId" to anuncioId)).await()
        return ((r.data as? Map<*, *>)?.get("venceEn") as? Number)?.toLong() ?: 0L
    }

    /** Lo que el dueño puede cambiar sin pagar: texto, precio, fotos, categoría y ciudad. */
    suspend fun guardarCambios(anuncioId: String, campos: Map<String, Any>) {
        col.document(anuncioId).update(campos + ("editadoEn" to FieldValue.serverTimestamp())).await()
    }

    /** Activo, pausado o retirado: lo único que el dueño decide del estado. */
    suspend fun cambiarEstado(anuncioId: String, estado: String) {
        col.document(anuncioId).update(mapOf("estado" to estado, "editadoEn" to FieldValue.serverTimestamp())).await()
    }

    suspend fun yaDenuncie(anuncioId: String): Boolean {
        val yo = uid ?: return false
        return runCatching { col.document(anuncioId).collection("denuncias").document(yo).get().await().exists() }
            .getOrDefault(false)
    }

    suspend fun denunciar(anuncioId: String, motivo: String, detalle: String) {
        val yo = uid ?: throw IllegalStateException("Inicie sesión.")
        col.document(anuncioId).collection("denuncias").document(yo).set(
            mapOf("motivo" to motivo.take(60), "detalle" to detalle.take(500), "creadoEn" to FieldValue.serverTimestamp())
        ).await()
    }

    companion object {
        const val COLECCION = "anuncios"
        const val PAGINA = 30

        /** ¿El servidor dijo que no alcanza el saldo? */
        fun esSaldoInsuficiente(e: Throwable): Boolean =
            (e as? FirebaseFunctionsException)?.message?.contains("SALDO_INSUFICIENTE") == true ||
                e.message?.contains("SALDO_INSUFICIENTE") == true

        /** El mensaje del servidor ya viene en palabras del vendedor; si no, uno genérico. */
        fun mensaje(e: Throwable): String = when (e) {
            is FirebaseFunctionsException -> e.message?.takeIf { it.isNotBlank() && !it.contains("INTERNAL", true) }
                ?: "No se pudo completar. Revise su conexión e intente de nuevo."
            else -> e.message?.takeIf { it.isNotBlank() } ?: "No se pudo completar. Intente de nuevo."
        }

        /** "150", "150.5", "1,500.00" o "150,50" → céntimos. Null si no es un número. */
        fun aCentimos(texto: String): Long? {
            val t = texto.trim().replace(" ", "")
            if (t.isEmpty()) return null
            val normal = if (t.contains('.')) t.replace(",", "") else t.replace(',', '.')
            return normal.toBigDecimalOrNull()
                ?.setScale(2, java.math.RoundingMode.HALF_UP)
                ?.movePointRight(2)
                ?.toLong()
        }
    }
}
