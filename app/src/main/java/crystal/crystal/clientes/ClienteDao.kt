package crystal.crystal.clientes

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ClienteDao {

    // ========== OPERACIONES BÁSICAS ==========

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(cliente: Cliente): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(clientes: List<Cliente>)

    @Update
    suspend fun actualizar(cliente: Cliente)

    @Query("DELETE FROM clientes WHERE id = :clienteId")
    suspend fun eliminarPorId(clienteId: String)

    // ========== CONSULTAS ==========

    @Query("SELECT * FROM clientes WHERE activo = 1 ORDER BY nombreCompleto ASC")
    fun obtenerTodosLive(): LiveData<List<Cliente>>

    @Query("SELECT * FROM clientes WHERE activo = 1 ORDER BY nombreCompleto ASC")
    suspend fun obtenerTodos(): List<Cliente>

    @Query("SELECT * FROM clientes WHERE id = :id")
    suspend fun obtenerPorId(id: String): Cliente?

    @Query("SELECT * FROM clientes WHERE numeroDocumento = :documento")
    suspend fun obtenerPorDocumento(documento: String): Cliente?

    /**
     * Búsqueda inteligente para reconocimiento de voz
     */
    @Query("""
        SELECT * FROM clientes 
        WHERE activo = 1 
        AND (
            nombreCompleto LIKE '%' || :consulta || '%' 
            OR numeroDocumento LIKE '%' || :consulta || '%'
        )
        ORDER BY 
            CASE 
                WHEN nombreCompleto LIKE :consulta || '%' THEN 1
                WHEN nombreCompleto LIKE '%' || :consulta || '%' THEN 2
                ELSE 3
            END,
            nombreCompleto ASC
        LIMIT :limite
    """)
    suspend fun buscar(consulta: String, limite: Int = 10): List<Cliente>

    // ========== SINCRONIZACIÓN ==========

    /**
     * Obtiene clientes pendientes de sincronizar
     */
    @Query("SELECT * FROM clientes WHERE pendienteSincronizar = 1")
    suspend fun obtenerPendientesSincronizar(): List<Cliente>

    /**
     * Obtiene clientes modificados después de un timestamp
     * Para sincronización incremental
     */
    @Query("""
        SELECT * FROM clientes 
        WHERE ultimaActualizacionLocal > :timestamp
        ORDER BY ultimaActualizacionLocal ASC
    """)
    suspend fun obtenerModificadosDespuesDe(timestamp: Long): List<Cliente>

    /**
     * Marca cliente como sincronizado
     */
    @Query("""
        UPDATE clientes 
        SET pendienteSincronizar = 0,
            ultimaSincronizacion = :timestamp
        WHERE id = :clienteId
    """)
    suspend fun marcarComoSincronizado(clienteId: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Marca cliente como pendiente de sincronizar
     */
    @Query("""
        UPDATE clientes 
        SET pendienteSincronizar = 1,
            ultimaActualizacionLocal = :timestamp
        WHERE id = :clienteId
    """)
    suspend fun marcarComoPendiente(clienteId: String, timestamp: Long = System.currentTimeMillis())

    // ========== ESTADÍSTICAS ==========

    /**
     * Actualiza estadísticas después de una venta
     */
    @Query("""
        UPDATE clientes 
        SET ultimaCompra = :timestamp,
            totalCompras = totalCompras + :monto,
            numeroCompras = numeroCompras + 1,
            pendienteSincronizar = 1,
            ultimaActualizacionLocal = :timestamp
        WHERE id = :clienteId
    """)
    suspend fun registrarVenta(clienteId: String, monto: Float, timestamp: Long = System.currentTimeMillis())

    /**
     * Top clientes por monto
     */
    @Query("""
        SELECT * FROM clientes 
        WHERE activo = 1 AND totalCompras > 0
        ORDER BY totalCompras DESC 
        LIMIT :limite
    """)
    suspend fun obtenerTopClientes(limite: Int = 10): List<Cliente>

    // ========== UTILIDADES ==========

    @Query("SELECT COUNT(*) FROM clientes WHERE activo = 1")
    suspend fun contarActivos(): Int

    @Query("SELECT COUNT(*) FROM clientes WHERE pendienteSincronizar = 1")
    suspend fun contarPendientesSincronizar(): Int

    @Query("SELECT SUM(totalCompras) FROM clientes WHERE activo = 1")
    suspend fun obtenerTotalVendido(): Float?

    /**
     * Obtiene timestamp de última sincronización
     */
    @Query("SELECT MAX(ultimaSincronizacion) FROM clientes")
    suspend fun obtenerUltimaSincronizacion(): Long?
}