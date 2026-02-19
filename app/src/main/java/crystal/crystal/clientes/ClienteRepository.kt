package crystal.crystal.clientes

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
 * Repository CORREGIDO - Usa patron_uid en lugar de auth.uid
 * TODOS los dispositivos guardan en usuarios/{patron_uid}/clientes/
 */
class ClienteRepository(
    private val clienteDao: ClienteDao,
    private val context: Context,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    companion object {
        private const val TAG = "ClienteRepository"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    val todosLosClientes: LiveData<List<Cliente>> = clienteDao.obtenerTodosLive()

    /**
     * ⭐ CLAVE: Obtiene UID de la empresa (no del dispositivo)
     */
    private fun obtenerUidEmpresa(): String? {
        // Primero buscar patron_uid (terminales)
        val patronUid = sharedPreferences.getString("patron_uid", null)
        if (patronUid != null) {
            Log.d(TAG, "📱 Usando patron_uid: $patronUid")
            return patronUid
        }

        // Si no, usar auth.uid (patrón)
        val currentUid = auth.currentUser?.uid
        if (currentUid != null) {
            Log.d(TAG, "👤 Usando auth.uid: $currentUid")
            return currentUid
        }

        Log.e(TAG, "❌ Sin UID de empresa")
        return null
    }

    // ========== CRUD ==========

    suspend fun crear(cliente: Cliente): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "➕ Creando: ${cliente.nombreCompleto}")

            val clientePendiente = cliente.copy(pendienteSincronizar = true)
            clienteDao.insertar(clientePendiente)
            sincronizarClienteAFirestore(clientePendiente)

            Result.success(cliente.id)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creando", e)
            Result.failure(e)
        }
    }

    suspend fun actualizar(cliente: Cliente): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val clientePendiente = cliente.marcarPendiente()
            clienteDao.actualizar(clientePendiente)
            sincronizarClienteAFirestore(clientePendiente)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminar(clienteId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            clienteDao.obtenerPorId(clienteId)?.let { cliente ->
                val inactivo = cliente.copy(activo = false).marcarPendiente()
                clienteDao.actualizar(inactivo)
                sincronizarClienteAFirestore(inactivo)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerTodos() = clienteDao.obtenerTodos()
    suspend fun obtenerPorId(id: String) = clienteDao.obtenerPorId(id)
    suspend fun obtenerPorDocumento(doc: String) = clienteDao.obtenerPorDocumento(doc)
    suspend fun buscar(q: String, lim: Int = 10) = clienteDao.buscar(q, lim)
    suspend fun obtenerTopClientes(lim: Int = 10) = clienteDao.obtenerTopClientes(lim)
    suspend fun contarActivos() = clienteDao.contarActivos()
    suspend fun obtenerTotalVendido() = clienteDao.obtenerTotalVendido() ?: 0f

    suspend fun registrarVenta(clienteId: String, monto: Float): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                clienteDao.registrarVenta(clienteId, monto)
                clienteDao.obtenerPorId(clienteId)?.let { sincronizarClienteAFirestore(it) }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    // ========== SINCRONIZACIÓN ==========

    private suspend fun sincronizarClienteAFirestore(cliente: Cliente): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val uid = obtenerUidEmpresa() ?: return@withContext Result.failure(
                    Exception("Sin UID empresa")
                )

                Log.d(TAG, "☁️ Subiendo: ${cliente.nombreCompleto}")
                Log.d(TAG, "   → usuarios/$uid/clientes/${cliente.id}")

                firestore.collection("usuarios")
                    .document(uid)  // ← PATRON_UID
                    .collection("clientes")
                    .document(cliente.id)
                    .set(cliente.toFirestoreMap())
                    .await()

                clienteDao.marcarComoSincronizado(cliente.id)
                Log.d(TAG, "✅ Sincronizado: ${cliente.nombreCompleto}")

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error: ${e.message}", e)
                Result.failure(e)
            }
        }

    suspend fun sincronizarPendientesAFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val pendientes = clienteDao.obtenerPendientesSincronizar()
            if (pendientes.isEmpty()) {
                Log.d(TAG, "✅ Sin pendientes")
                return@withContext Result.success(0)
            }

            Log.d(TAG, "🔄 Sincronizando ${pendientes.size} pendientes...")
            var exitosos = 0
            pendientes.forEach {
                if (sincronizarClienteAFirestore(it).isSuccess) exitosos++
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

            val ultimaSync = clienteDao.obtenerUltimaSincronizacion() ?: 0L

            Log.d(TAG, "🔽 Descargando cambios...")
            Log.d(TAG, "   → usuarios/$uid/clientes")

            val snapshot = firestore.collection("usuarios")
                .document(uid)  // ← PATRON_UID
                .collection("clientes")
                .whereGreaterThan("ultimaActualizacion", ultimaSync)
                .get()
                .await()

            if (snapshot.isEmpty) {
                Log.d(TAG, "✅ Sin cambios")
                return@withContext Result.success(0)
            }

            val clientes = snapshot.documents.mapNotNull {
                try {
                    Cliente.fromFirestoreMap(it.data ?: emptyMap())
                } catch (e: Exception) {
                    null
                }
            }

            clienteDao.insertarVarios(clientes)
            Log.d(TAG, "✅ Descargados ${clientes.size}")

            Result.success(clientes.size)
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
                .document(uid)  // ← PATRON_UID
                .collection("clientes")
                .get()
                .await()

            val clientes = snapshot.documents.mapNotNull {
                try {
                    Cliente.fromFirestoreMap(it.data ?: emptyMap())
                } catch (e: Exception) {
                    null
                }
            }

            clienteDao.insertarVarios(clientes)
            Log.d(TAG, "✅ Total: ${clientes.size}")

            Result.success(clientes.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}