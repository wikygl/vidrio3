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
    entities = [Producto::class, MovimientoInventario::class],
    version = 3,  // v2: precioCompra/precioVenta · v3: stock decimal + planchas + movimientos
    exportSchema = false
)
abstract class ProductoDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun movimientoInventarioDao(): MovimientoInventarioDao

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

        /**
         * v3: el stock pasa a decimal (el vidrio se vende por área, no por unidades enteras), se
         * agregan las columnas de plancha y aparece el libro de movimientos.
         *
         * SQLite no permite cambiar el tipo de una columna, así que se recrea la tabla. Los valores
         * existentes se conservan: INTEGER a REAL es una conversión sin pérdida.
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE productos_new (
                        id TEXT PRIMARY KEY NOT NULL,
                        nombre TEXT NOT NULL,
                        categoria TEXT NOT NULL,
                        descripcion TEXT,
                        precioCompra REAL NOT NULL DEFAULT 0.0,
                        precioVenta REAL NOT NULL,
                        stock REAL NOT NULL DEFAULT 0.0,
                        stockMinimo REAL NOT NULL DEFAULT 10.0,
                        unidad TEXT NOT NULL DEFAULT 'm2',
                        espesor TEXT,
                        tipo TEXT,
                        stockPlanchas REAL NOT NULL DEFAULT 0.0,
                        anchoPlancha REAL NOT NULL DEFAULT 0.0,
                        altoPlancha REAL NOT NULL DEFAULT 0.0,
                        planchasPorCaja INTEGER NOT NULL DEFAULT 0,
                        activo INTEGER NOT NULL DEFAULT 1,
                        pendienteSincronizar INTEGER NOT NULL DEFAULT 0,
                        ultimaActualizacion INTEGER NOT NULL,
                        ultimaSincronizacion INTEGER NOT NULL DEFAULT 0,
                        ultimaActualizacionLocal INTEGER NOT NULL
                    )
                """.trimIndent())

                database.execSQL("""
                    INSERT INTO productos_new (
                        id, nombre, categoria, descripcion, precioCompra, precioVenta,
                        stock, stockMinimo, unidad, espesor, tipo,
                        stockPlanchas, anchoPlancha, altoPlancha, planchasPorCaja,
                        activo, pendienteSincronizar, ultimaActualizacion,
                        ultimaSincronizacion, ultimaActualizacionLocal
                    )
                    SELECT
                        id, nombre, categoria, descripcion, precioCompra, precioVenta,
                        CAST(stock AS REAL), CAST(stockMinimo AS REAL), unidad, espesor, tipo,
                        0.0, 0.0, 0.0, 0,
                        activo, pendienteSincronizar, ultimaActualizacion,
                        ultimaSincronizacion, ultimaActualizacionLocal
                    FROM productos
                """.trimIndent())

                database.execSQL("DROP TABLE productos")
                database.execSQL("ALTER TABLE productos_new RENAME TO productos")

                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS movimientos_inventario (
                        id TEXT PRIMARY KEY NOT NULL,
                        productoId TEXT NOT NULL,
                        productoNombre TEXT NOT NULL,
                        tipo TEXT NOT NULL,
                        cantidad REAL NOT NULL,
                        cantidadPlanchas REAL NOT NULL DEFAULT 0.0,
                        stockAnterior REAL NOT NULL,
                        stockNuevo REAL NOT NULL,
                        stockPlanchasAnterior REAL NOT NULL DEFAULT 0.0,
                        stockPlanchasNuevo REAL NOT NULL DEFAULT 0.0,
                        referencia TEXT NOT NULL DEFAULT '',
                        vendedor TEXT NOT NULL DEFAULT '',
                        terminal TEXT,
                        observaciones TEXT NOT NULL DEFAULT '',
                        fecha INTEGER NOT NULL,
                        uidPatron TEXT NOT NULL DEFAULT '',
                        pendienteSincronizar INTEGER NOT NULL DEFAULT 1,
                        ultimaSincronizacion INTEGER NOT NULL DEFAULT 0
                    )
                """.trimIndent())
            }
        }

        fun getDatabase(context: Context): ProductoDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductoDatabase::class.java,
                    "producto_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
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
                applicationContext,
                database.movimientoInventarioDao()
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
                applicationContext,
                database.movimientoInventarioDao()
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