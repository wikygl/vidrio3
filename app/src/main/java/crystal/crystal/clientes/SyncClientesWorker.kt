package crystal.crystal.clientes

import android.content.Context
import android.util.Log
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * Worker para sincronización automática en background
 * Se ejecuta periódicamente para mantener Room y Firestore sincronizados
 */
class SyncClientesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "SyncClientesWorker"
        private const val WORK_NAME = "sync_clientes_periodic"

        /**
         * Programa sincronización periódica
         */
        fun programarSincronizacionPeriodica(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)  // Solo con internet
                .setRequiresBatteryNotLow(true)  // No sincronizar con batería baja
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncClientesWorker>(
                repeatInterval = 30,  // Cada 30 minutos
                repeatIntervalTimeUnit = TimeUnit.MINUTES,
                flexTimeInterval = 10,  // Ventana de 10 minutos
                flexTimeIntervalUnit = TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .addTag("sync")
                .addTag("clientes")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,  // No reemplazar si ya existe
                syncRequest
            )

            Log.d(TAG, "✅ Sincronización periódica programada (cada 30 min)")
        }

        /**
         * Ejecuta sincronización inmediata (una sola vez)
         */
        fun sincronizarAhora(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncClientesWorker>()
                .setConstraints(constraints)
                .addTag("sync")
                .addTag("sync_inmediata")
                .build()

            WorkManager.getInstance(context).enqueue(syncRequest)

            Log.d(TAG, "🔄 Sincronización inmediata encolada")
        }

        /**
         * Cancela sincronización periódica
         */
        fun cancelarSincronizacion(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Log.d(TAG, "❌ Sincronización periódica cancelada")
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "════════════════════════════════════════")
            Log.d(TAG, "🔄 INICIANDO SINCRONIZACIÓN AUTOMÁTICA")
            Log.d(TAG, "════════════════════════════════════════")

            // Obtener repository
            val database = ClienteDatabase.getDatabase(applicationContext)
            val repository = ClienteRepository(database.clienteDao(),applicationContext)

            // Ejecutar sincronización completa
            val resultado = repository.sincronizarCompleta()

            if (resultado.isSuccess) {
                val (subidos, descargados) = resultado.getOrThrow()

                Log.d(TAG, "✅ SINCRONIZACIÓN EXITOSA")
                Log.d(TAG, "   ↑ Clientes subidos: $subidos")
                Log.d(TAG, "   ↓ Clientes descargados: $descargados")
                Log.d(TAG, "════════════════════════════════════════")

                // Mostrar notificación si hubo cambios significativos
                if (descargados > 0) {
                    mostrarNotificacion(descargados)
                }

                Result.success()
            } else {
                val error = resultado.exceptionOrNull()
                Log.e(TAG, "❌ ERROR EN SINCRONIZACIÓN: ${error?.message}", error)
                Log.d(TAG, "════════════════════════════════════════")

                // Reintentar después
                Result.retry()
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ EXCEPCIÓN EN WORKER: ${e.message}", e)
            Log.d(TAG, "════════════════════════════════════════")
            Result.retry()
        }
    }

    /**
     * Muestra notificación de cambios sincronizados
     */
    private fun mostrarNotificacion(clientesNuevos: Int) {
        // TODO: Implementar notificación si se desea
        // Por ahora solo log
        Log.i(TAG, "💡 Se descargaron $clientesNuevos clientes nuevos")
    }
}

/**
 * Worker para sincronización inicial (primera vez)
 */
class SyncInicialClientesWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "SyncInicialClientes"

        /**
         * Ejecuta descarga inicial de todos los clientes
         */
        fun descargarClientesIniciales(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncInicialClientesWorker>()
                .setConstraints(constraints)
                .addTag("sync_inicial")
                .build()

            WorkManager.getInstance(context).enqueue(syncRequest)

            Log.d(TAG, "📥 Descarga inicial encolada")
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "📥 DESCARGANDO CLIENTES INICIALES...")

            val database = ClienteDatabase.getDatabase(applicationContext)
            val repository = ClienteRepository(database.clienteDao(),applicationContext)

            val resultado = repository.descargarTodoDesdeFirestore()

            if (resultado.isSuccess) {
                val total = resultado.getOrThrow()
                Log.d(TAG, "✅ Descargados $total clientes totales")

                // Programar sincronización periódica después de la inicial
                SyncClientesWorker.programarSincronizacionPeriodica(applicationContext)

                Result.success()
            } else {
                Log.e(TAG, "❌ Error en descarga inicial")
                Result.retry()
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ EXCEPCIÓN: ${e.message}", e)
            Result.retry()
        }
    }
}