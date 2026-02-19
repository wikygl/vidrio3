package crystal.crystal.productos

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Repository para Producto - Room + Firestore
 * Sincronización optimizada solo de cambios
 */
class ProductoRepository(
    private val productoDao: ProductoDao,
    private val context: Context,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    companion object {
        private const val TAG = "ProductoRepository"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    val todosLosProductos: LiveData<List<Producto>> = productoDao.obtenerTodosLive()

    /**
     * Obtiene el UID de la empresa (patron_uid)
     */
    private fun obtenerUidEmpresa(): String? {
        val patronUid = sharedPreferences.getString("patron_uid", null)
        if (patronUid != null) {
            Log.d(TAG, "📱 Usando patron_uid: $patronUid")
            return patronUid
        }

        val currentUid = auth.currentUser?.uid
        if (currentUid != null) {
            Log.d(TAG, "👤 Usando auth.uid: $currentUid")
            return currentUid
        }

        Log.e(TAG, "❌ Sin UID de empresa")
        return null
    }

    // ========== CRUD ==========

    suspend fun crear(producto: Producto): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "➕ Creando: ${producto.nombre}")

            val productoPendiente = producto.copy(pendienteSincronizar = true)
            productoDao.insertar(productoPendiente)
            sincronizarProductoAFirestore(productoPendiente)

            Result.success(producto.id)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creando", e)
            Result.failure(e)
        }
    }

    suspend fun actualizar(producto: Producto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val productoPendiente = producto.marcarPendiente()
            productoDao.actualizar(productoPendiente)
            sincronizarProductoAFirestore(productoPendiente)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminar(productoId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            productoDao.obtenerPorId(productoId)?.let { producto ->
                val inactivo = producto.copy(activo = false).marcarPendiente()
                productoDao.actualizar(inactivo)
                sincronizarProductoAFirestore(inactivo)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== CONSULTAS ==========

    suspend fun obtenerTodos() = productoDao.obtenerTodos()
    suspend fun obtenerPorId(id: String) = productoDao.obtenerPorId(id)
    suspend fun obtenerPorNombre(nombre: String) = productoDao.obtenerPorNombre(nombre)
    suspend fun buscar(q: String, lim: Int = 10) = productoDao.buscar(q, lim)
    suspend fun obtenerPorCategoria(cat: String) = productoDao.obtenerPorCategoria(cat)
    suspend fun obtenerPorEspesor(esp: String) = productoDao.obtenerPorEspesor(esp)

    // ========== STOCK ==========

    /**
     * Registrar venta - RESTA del stock
     */
    suspend fun registrarVenta(productoId: String, cantidad: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                // Verificar stock disponible
                val producto = productoDao.obtenerPorId(productoId)
                if (producto == null) {
                    return@withContext Result.failure(Exception("Producto no encontrado"))
                }

                if (!producto.tieneStock(cantidad)) {
                    return@withContext Result.failure(
                        Exception("Stock insuficiente. Disponible: ${producto.stock}")
                    )
                }

                // Actualizar stock (restar)
                productoDao.actualizarStock(productoId, -cantidad)

                // Sincronizar
                productoDao.obtenerPorId(productoId)?.let {
                    sincronizarProductoAFirestore(it)
                }

                Log.d(TAG, "📦 Venta registrada: -$cantidad de ${producto.nombre}")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error registrando venta", e)
                Result.failure(e)
            }
        }

    /**
     * Agregar stock - SUMA al stock
     */
    suspend fun agregarStock(productoId: String, cantidad: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                productoDao.actualizarStock(productoId, cantidad)

                productoDao.obtenerPorId(productoId)?.let {
                    sincronizarProductoAFirestore(it)
                }

                Log.d(TAG, "📦 Stock agregado: +$cantidad")
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun obtenerStockBajo() = productoDao.obtenerStockBajo()
    suspend fun obtenerSinStock() = productoDao.obtenerSinStock()
    suspend fun obtenerValorInventario() = productoDao.obtenerValorTotalInventario() ?: 0f

    // ========== ESTADÍSTICAS ==========

    suspend fun contarActivos() = productoDao.contarActivos()
    suspend fun contarConStock() = productoDao.contarConStock()
    suspend fun obtenerEstadisticas() = productoDao.obtenerEstadisticasPorCategoria()

    // ========== SINCRONIZACIÓN ==========

    private suspend fun sincronizarProductoAFirestore(producto: Producto): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val uid = obtenerUidEmpresa() ?: return@withContext Result.failure(
                    Exception("Sin UID empresa")
                )

                Log.d(TAG, "☁️ Subiendo: ${producto.nombre}")
                Log.d(TAG, "   → usuarios/$uid/productos/${producto.id}")

                firestore.collection("usuarios")
                    .document(uid)
                    .collection("productos")
                    .document(producto.id)
                    .set(producto.toFirestoreMap())
                    .await()

                productoDao.marcarComoSincronizado(producto.id)
                Log.d(TAG, "✅ Sincronizado: ${producto.nombre}")

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error: ${e.message}", e)
                Result.failure(e)
            }
        }

    suspend fun sincronizarPendientesAFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val pendientes = productoDao.obtenerPendientesSincronizar()
            if (pendientes.isEmpty()) {
                Log.d(TAG, "✅ Sin pendientes")
                return@withContext Result.success(0)
            }

            Log.d(TAG, "🔄 Sincronizando ${pendientes.size} pendientes...")
            var exitosos = 0
            pendientes.forEach {
                if (sincronizarProductoAFirestore(it).isSuccess) exitosos++
            }

            Log.d(TAG, "✅ $exitosos/${pendientes.size} sincronizados")
            Result.success(exitosos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun descargarCambiosDesdeFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val uid = obtenerUidEmpresa() ?: return@withContext Result.failure(
                Exception("Sin UID empresa")
            )

            val ultimaSync = productoDao.obtenerUltimaSincronizacion() ?: 0L

            Log.d(TAG, "🔽 Descargando cambios...")
            Log.d(TAG, "   → usuarios/$uid/productos")

            val snapshot = firestore.collection("usuarios")
                .document(uid)
                .collection("productos")
                .whereGreaterThan("ultimaActualizacion", ultimaSync)
                .get()
                .await()

            if (snapshot.isEmpty) {
                Log.d(TAG, "✅ Sin cambios")
                return@withContext Result.success(0)
            }

            val productos = snapshot.documents.mapNotNull {
                try {
                    Producto.fromFirestoreMap(it.data ?: emptyMap())
                } catch (e: Exception) {
                    null
                }
            }

            productoDao.insertarVarios(productos)
            Log.d(TAG, "✅ Descargados ${productos.size}")

            Result.success(productos.size)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error descarga", e)
            Result.failure(e)
        }
    }

    suspend fun sincronizarCompleta(): Result<Pair<Int, Int>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🔄 SYNC COMPLETA")

            val subidos = sincronizarPendientesAFirestore().getOrDefault(0)
            val descargados = descargarCambiosDesdeFirestore().getOrDefault(0)

            Log.d(TAG, "✅ ↑$subidos ↓$descargados")
            Result.success(Pair(subidos, descargados))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun descargarTodoDesdeFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val uid = obtenerUidEmpresa() ?: return@withContext Result.failure(
                Exception("Sin UID")
            )

            Log.d(TAG, "📥 Descarga completa...")

            val snapshot = firestore.collection("usuarios")
                .document(uid)
                .collection("productos")
                .get()
                .await()

            val productos = snapshot.documents.mapNotNull {
                try {
                    Producto.fromFirestoreMap(it.data ?: emptyMap())
                } catch (e: Exception) {
                    null
                }
            }

            productoDao.insertarVarios(productos)
            Log.d(TAG, "✅ Total: ${productos.size}")

            Result.success(productos.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}