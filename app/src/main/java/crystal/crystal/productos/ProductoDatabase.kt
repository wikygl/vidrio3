package crystal.crystal.productos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 * Database Room para productos
 * ⭐ ACTUALIZADO: Versión 2 con precioCompra y precioVenta
 */
@Database(
    entities = [Producto::class],
    version = 2,  // ⭐ INCREMENTADO de 1 a 2
    exportSchema = false
)
abstract class ProductoDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: ProductoDatabase? = null

        // ⭐ MIGRACIÓN DE VERSIÓN 1 A 2
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // SQLite no soporta RENAME COLUMN ni ALTER COLUMN
                // Solución: Crear tabla nueva, copiar datos, eliminar vieja, renombrar nueva

                // 1. Crear tabla temporal con nueva estructura
                database.execSQL("""
                    CREATE TABLE productos_new (
                        id TEXT PRIMARY KEY NOT NULL,
                        nombre TEXT NOT NULL,
                        categoria TEXT NOT NULL,
                        descripcion TEXT,
                        precioCompra REAL NOT NULL DEFAULT 0.0,
                        precioVenta REAL NOT NULL,
                        stock INTEGER NOT NULL DEFAULT 0,
                        stockMinimo INTEGER NOT NULL DEFAULT 10,
                        unidad TEXT NOT NULL DEFAULT 'm2',
                        espesor TEXT,
                        tipo TEXT,
                        activo INTEGER NOT NULL DEFAULT 1,
                        pendienteSincronizar INTEGER NOT NULL DEFAULT 0,
                        ultimaActualizacion INTEGER NOT NULL,
                        ultimaSincronizacion INTEGER NOT NULL DEFAULT 0,
                        ultimaActualizacionLocal INTEGER NOT NULL
                    )
                """.trimIndent())

                // 2. Copiar datos (precio → precioVenta, precioCompra = 0)
                database.execSQL("""
                    INSERT INTO productos_new 
                    SELECT 
                        id, 
                        nombre, 
                        categoria, 
                        descripcion,
                        0.0 as precioCompra,
                        precio as precioVenta,
                        stock, 
                        stockMinimo, 
                        unidad, 
                        espesor, 
                        tipo,
                        activo, 
                        pendienteSincronizar, 
                        ultimaActualizacion,
                        ultimaSincronizacion, 
                        ultimaActualizacionLocal
                    FROM productos
                """.trimIndent())

                // 3. Eliminar tabla vieja
                database.execSQL("DROP TABLE productos")

                // 4. Renombrar tabla nueva
                database.execSQL("ALTER TABLE productos_new RENAME TO productos")
            }
        }

        fun getDatabase(context: Context): ProductoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductoDatabase::class.java,
                    "producto_database"
                )
                    .addMigrations(MIGRATION_1_2)  // ⭐ AGREGAR MIGRACIÓN
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/**
 * Worker para sincronización periódica de productos
 * Ejecuta cada 2 horas (menos frecuente que clientes)
 */
class SyncProductosWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "SyncProductosWorker"
        private const val WORK_NAME = "sync_productos_periodico"

        /**
         * Programar sincronización periódica (cada 2 horas)
         */
        fun programarSincronizacionPeriodica(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<SyncProductosWorker>(
                2, TimeUnit.HOURS,  // Cada 2 horas (productos cambian menos que clientes)
                30, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    15, TimeUnit.MINUTES
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )

            android.util.Log.d(TAG, "⏰ Sincronización programada cada 2 horas")
        }

        /**
         * Sincronizar ahora (manual)
         */
        fun sincronizarAhora(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncProductosWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(syncRequest)

            android.util.Log.d(TAG, "🔄 Sincronización manual iniciada")
        }

        /**
         * Cancelar sincronización periódica
         */
        fun cancelarSincronizacion(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            android.util.Log.d(TAG, "❌ Sincronización cancelada")
        }
    }

    override suspend fun doWork(): Result {
        return try {
            android.util.Log.d(TAG, "🔄 Iniciando sincronización...")

            val database = ProductoDatabase.getDatabase(applicationContext)
            val repository = ProductoRepository(
                database.productoDao(),
                applicationContext
            )

            val resultado = repository.sincronizarCompleta()

            if (resultado.isSuccess) {
                val (subidos, descargados) = resultado.getOrDefault(Pair(0, 0))
                android.util.Log.d(TAG, "✅ Sincronización exitosa: ↑$subidos ↓$descargados")
                Result.success()
            } else {
                android.util.Log.e(TAG, "❌ Error en sincronización")
                Result.retry()
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Error: ${e.message}", e)
            Result.retry()
        }
    }
}

/**
 * Worker para sincronización inicial (primera vez)
 */
class SyncInicialProductosWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "SyncInicialProductos"

        /**
         * Descargar todos los productos la primera vez
         */
        fun descargarProductosIniciales(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = OneTimeWorkRequestBuilder<SyncInicialProductosWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(syncRequest)

            android.util.Log.d(TAG, "📥 Descarga inicial programada")
        }
    }

    override suspend fun doWork(): Result {
        return try {
            android.util.Log.d(TAG, "📥 Descargando catálogo inicial...")

            val database = ProductoDatabase.getDatabase(applicationContext)
            val repository = ProductoRepository(
                database.productoDao(),
                applicationContext
            )

            val resultado = repository.descargarTodoDesdeFirestore()

            if (resultado.isSuccess) {
                val total = resultado.getOrDefault(0)
                android.util.Log.d(TAG, "✅ Descargados $total productos")
                Result.success()
            } else {
                android.util.Log.e(TAG, "❌ Error descargando")
                Result.retry()
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "❌ Error: ${e.message}", e)
            Result.retry()
        }
    }
}