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
        val pasa = { a: Anuncio ->
            a.visibleEnLaTienda(ahora) &&
                (categoria == null || a.categoria == categoria) &&
                (tipo == null || a.tipo == tipo) &&
                (texto.isBlank() || TiendaReglas.casa(a, texto))
        }
        // La primera página lleva arriba los destacados vigentes que casan con lo elegido, sin
        // repetirlos abajo. Si esa consulta falla, la lista sale igual, sin destacados.
        val destacados = if (despuesDe != null) emptyList() else runCatching {
            col.whereEqualTo("estado", Anuncio.ESTADO_ACTIVO)
                .whereGreaterThan("destacadoHasta", com.google.firebase.Timestamp.now())
                .orderBy("destacadoHasta", Query.Direction.ASCENDING)
                .limit(10)
                .get().await()
                .documents.mapNotNull { Anuncio.desde(it) }.filter(pasa)
        }.getOrDefault(emptyList())
        val arriba = destacados.map { it.id }.toSet()
        val anuncios = destacados + snap.documents.mapNotNull { Anuncio.desde(it) }.filter { it.id !in arriba && pasa(it) }
        return Pagina(anuncios, snap.documents.lastOrNull(), snap.size() == PAGINA)
    }

    /** "ventanas" busca también "ventana" y al revés, que es como quedan guardadas las palabras. */
    private fun variantes(p: String): List<String> =
        listOf(p, p + "s", p + "es", TiendaReglas.singular(p)).distinct().take(10)

    /** Los anuncios propios, en cualquier estado. */
    suspend fun misAnuncios(): List<Anuncio> {
        val yo = uid ?: return emptyList()
        return anunciosDe(yo)
    }

    /** Los anuncios de un vendedor; de otro, solo los que se ven en la Tienda. */
    suspend fun anunciosDe(vendedorUid: String): List<Anuncio> {
        val ahora = System.currentTimeMillis()
        return col.whereEqualTo("uid", vendedorUid)
            .orderBy("publicadoEn", Query.Direction.DESCENDING)
            .limit(100)
            .get().await()
            .documents.mapNotNull { Anuncio.desde(it) }
            .filter { vendedorUid == uid || it.visibleEnLaTienda(ahora) }
    }

    suspend fun obtener(id: String): Anuncio? = Anuncio.desde(col.document(id).get().await())

    /** Quien abre un anuncio ajeno suma una vista. Si falla, no importa. */
    fun sumarVista(id: String) {
        col.document(id).update("vistas", FieldValue.increment(1))
    }

    /** Lo que cuesta publicar (o renovar) un [tipo] hoy y el saldo del usuario, en céntimos. */
    suspend fun precioYSaldo(tipo: String = TiendaReglas.PRODUCTO): Triple<Long, Int, Long> {
        val (t, saldo) = tarifasYSaldo()
        val (precio, dias) = t.publicar(tipo)
        return Triple(precio, dias, saldo)
    }

    /** Todas las tarifas de `config/anuncios` y el saldo del usuario, en céntimos. */
    suspend fun tarifasYSaldo(): Pair<TiendaReglas.Tarifas, Long> {
        val cfg = runCatching { db.collection("config").document("anuncios").get().await() }.getOrNull()
        val saldo = uid?.let { yo ->
            runCatching { db.collection("usuarios").document(yo).get().await().getLong("wallet_saldo_cent") }.getOrNull()
        } ?: 0L
        return TiendaReglas.tarifas(cfg?.data) to saldo
    }

    /** Cobra unos días de "Destacado". Devuelve hasta cuándo, en ms. */
    suspend fun destacar(anuncioId: String): Long {
        val r = funciones.getHttpsCallable("destacarAnuncio").call(mapOf("anuncioId" to anuncioId)).await()
        return ((r.data as? Map<*, *>)?.get("destacadoHasta") as? Number)?.toLong() ?: 0L
    }

    // ───────────── Vitrina de proveedores ─────────────

    suspend fun miProveedor(): Proveedor? {
        val yo = uid ?: return null
        return Proveedor.desde(db.collection(COL_PROVEEDORES).document(yo).get().await())
    }

    /**
     * Los proveedores vigentes que atienden [ciudad]. Si no hay ninguno, los de todo el país: mejor
     * uno lejos que una lista vacía. El segundo valor dice si son de la ciudad.
     */
    suspend fun proveedoresPara(ciudad: String): Pair<List<Proveedor>, Boolean> {
        val ahora = System.currentTimeMillis()
        val clave = TiendaReglas.claveCiudad(ciudad)
        if (clave.isNotEmpty()) {
            val locales = db.collection(COL_PROVEEDORES)
                .whereArrayContains("zonasClave", clave)
                .whereEqualTo("estado", Proveedor.ESTADO_ACTIVO)
                .limit(50)
                .get().await()
                .documents.mapNotNull { Proveedor.desde(it) }.filter { it.vigente(ahora) }
            if (locales.isNotEmpty()) return locales to true
        }
        val todos = db.collection(COL_PROVEEDORES)
            .whereEqualTo("estado", Proveedor.ESTADO_ACTIVO)
            .limit(50)
            .get().await()
            .documents.mapNotNull { Proveedor.desde(it) }.filter { it.vigente(ahora) }
        return todos to false
    }

    /** Cobra un período de la vitrina con la ficha. Devuelve el nuevo vencimiento en ms. */
    suspend fun suscribirProveedor(ficha: Map<String, Any>): Long {
        val r = funciones.getHttpsCallable("suscribirProveedor").call(ficha).await()
        return ((r.data as? Map<*, *>)?.get("vigenteHasta") as? Number)?.toLong() ?: 0L
    }

    suspend fun guardarProveedor(ficha: Map<String, Any>) {
        funciones.getHttpsCallable("guardarProveedor").call(ficha).await()
    }

    /** El logo de la vitrina, en miniatura, a `proveedores/{uid}/`. */
    suspend fun subirLogo(context: Context, uri: Uri): String {
        val yo = uid ?: throw IllegalStateException("Inicie sesión.")
        val (_, mini) = withContext(Dispatchers.IO) { FotosAnuncio.preparar(context, uri) }
            ?: throw IllegalStateException("No se pudo leer la imagen.")
        val meta = StorageMetadata.Builder()
            .setContentType("image/jpeg")
            .setCacheControl("public, max-age=31536000")
            .build()
        val ref = storage.reference.child("$COL_PROVEEDORES/$yo/logo_${System.currentTimeMillis()}.jpg")
        ref.putBytes(mini, meta).await()
        return ref.downloadUrl.await().toString()
    }

    // ───────────── Pedidos de clientes de la web ─────────────

    /**
     * Los pedidos abiertos de [ciudad] y los dirigidos a mis anuncios, lo más nuevo primero. Los
     * dirigidos a otro vendedor no se muestran.
     */
    suspend fun pedidosPara(ciudad: String): List<PedidoCliente> {
        val yo = uid ?: return emptyList()
        val ahora = System.currentTimeMillis()
        val clave = TiendaReglas.claveCiudad(ciudad)
        val deLaCiudad = if (clave.isEmpty()) emptyList() else db.collection(COL_PEDIDOS)
            .whereEqualTo("ciudadClave", clave)
            .whereEqualTo("estado", PedidoCliente.ESTADO_ABIERTO)
            .orderBy("creadoEn", Query.Direction.DESCENDING)
            .limit(50)
            .get().await()
            .documents.mapNotNull { PedidoCliente.desde(it) }
        val paraMi = runCatching {
            db.collection(COL_PEDIDOS)
                .whereEqualTo("paraUid", yo)
                .orderBy("creadoEn", Query.Direction.DESCENDING)
                .limit(30)
                .get().await()
                .documents.mapNotNull { PedidoCliente.desde(it) }
        }.getOrDefault(emptyList())
        return (paraMi + deLaCiudad).distinctBy { it.id }
            .filter { yo !in it.tomadoPor && it.disponiblePara(yo, ahora) }
            .sortedByDescending { it.creadoEnMs }
    }

    /** Los pedidos que ya tomé, con su contacto a un toque. */
    suspend fun pedidosTomados(): List<PedidoCliente> {
        val yo = uid ?: return emptyList()
        return db.collection(COL_PEDIDOS)
            .whereArrayContains("tomadoPor", yo)
            .orderBy("creadoEn", Query.Direction.DESCENDING)
            .limit(50)
            .get().await()
            .documents.mapNotNull { PedidoCliente.desde(it) }
    }

    suspend fun pedido(id: String): PedidoCliente? = PedidoCliente.desde(db.collection(COL_PEDIDOS).document(id).get().await())

    /** El contacto de un pedido que ya tomé, o null si no lo tomé. */
    suspend fun miToma(pedidoId: String): TomaPedido? {
        val yo = uid ?: return null
        val d = runCatching { db.collection(COL_PEDIDOS).document(pedidoId).collection("tomas").document(yo).get().await() }
            .getOrNull() ?: return null
        if (!d.exists()) return null
        return TomaPedido(d.getString("nombre").orEmpty(), d.getString("telefono").orEmpty(), d.getBoolean("reportado") == true)
    }

    /** Toma el pedido (cobra en el servidor, salvo los dirigidos a mi anuncio) y devuelve el contacto. */
    suspend fun tomarPedido(pedidoId: String): TomaPedido {
        val r = funciones.getHttpsCallable("tomarPedidoCliente").call(mapOf("pedidoId" to pedidoId)).await()
        val m = r.data as? Map<*, *> ?: emptyMap<String, Any>()
        return TomaPedido(m["nombre"]?.toString().orEmpty(), m["telefono"]?.toString().orEmpty())
    }

    suspend fun reportarPedido(pedidoId: String, motivo: String) {
        funciones.getHttpsCallable("reportarPedidoCliente").call(mapOf("pedidoId" to pedidoId, "motivo" to motivo)).await()
    }

    // ───────────── Precios de referencia (plan FULL) ─────────────

    data class Referencia(val ciudad: List<PrecioReferencia>, val pais: List<PrecioReferencia>, val revisados: Int)

    suspend fun preciosReferencia(texto: String, ciudad: String): Referencia {
        val r = funciones.getHttpsCallable("preciosReferencia").call(mapOf("texto" to texto, "ciudad" to ciudad)).await()
        val m = r.data as? Map<*, *> ?: emptyMap<String, Any>()
        return Referencia(
            PrecioReferencia.lista(m["ciudad"]), PrecioReferencia.lista(m["pais"]), (m["revisados"] as? Number)?.toInt() ?: 0
        )
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
        const val COL_PROVEEDORES = "proveedores"
        const val COL_PEDIDOS = "pedidos_clientes"
        const val PAGINA = 30

        /** ¿El servidor dijo que no alcanza el saldo? */
        fun esSaldoInsuficiente(e: Throwable): Boolean =
            (e as? FirebaseFunctionsException)?.message?.contains("SALDO_INSUFICIENTE") == true ||
                e.message?.contains("SALDO_INSUFICIENTE") == true

        /** ¿El servidor dijo que hace falta el plan FULL? */
        fun esFaltaPlan(e: Throwable): Boolean = e.message?.contains("PLAN_FULL") == true

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
