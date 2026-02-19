package crystal.crystal.productos

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO para Producto con queries optimizadas
 * ⭐ ACTUALIZADO: Queries para precioCompra y precioVenta
 */
@Dao
interface ProductoDao {

    // ========== CRUD BÁSICO ==========

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(productos: List<Producto>)

    @Update
    suspend fun actualizar(producto: Producto)

    @Query("UPDATE productos SET activo = 0, pendienteSincronizar = 1 WHERE id = :productoId")
    suspend fun eliminar(productoId: String)

    // ========== CONSULTAS ==========

    @Query("SELECT * FROM productos WHERE activo = 1 ORDER BY nombre ASC")
    suspend fun obtenerTodos(): List<Producto>

    @Query("SELECT * FROM productos WHERE activo = 1 ORDER BY nombre ASC")
    fun obtenerTodosLive(): LiveData<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: String): Producto?

    @Query("SELECT * FROM productos WHERE nombre = :nombre AND activo = 1 LIMIT 1")
    suspend fun obtenerPorNombre(nombre: String): Producto?

    // ========== BÚSQUEDA INTELIGENTE ==========

    @Query("""
        SELECT * FROM productos 
        WHERE activo = 1 
        AND (
            nombre LIKE '%' || :consulta || '%' 
            OR categoria LIKE '%' || :consulta || '%'
            OR descripcion LIKE '%' || :consulta || '%'
            OR espesor LIKE '%' || :consulta || '%'
            OR tipo LIKE '%' || :consulta || '%'
        )
        ORDER BY 
            CASE 
                WHEN nombre LIKE :consulta || '%' THEN 1
                WHEN nombre LIKE '%' || :consulta || '%' THEN 2
                ELSE 3
            END,
            nombre ASC
        LIMIT :limite
    """)
    suspend fun buscar(consulta: String, limite: Int = 10): List<Producto>

    @Query("""
        SELECT * FROM productos 
        WHERE activo = 1 
        AND categoria = :categoria
        ORDER BY nombre ASC
    """)
    suspend fun obtenerPorCategoria(categoria: String): List<Producto>

    @Query("""
        SELECT * FROM productos 
        WHERE activo = 1 
        AND espesor = :espesor
        ORDER BY nombre ASC
    """)
    suspend fun obtenerPorEspesor(espesor: String): List<Producto>

    // ========== STOCK ==========

    @Query("UPDATE productos SET stock = stock + :cantidad, pendienteSincronizar = 1, ultimaActualizacion = :timestamp WHERE id = :productoId")
    suspend fun actualizarStock(productoId: String, cantidad: Int, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT * FROM productos WHERE stock <= stockMinimo AND activo = 1 ORDER BY stock ASC")
    suspend fun obtenerStockBajo(): List<Producto>

    @Query("SELECT * FROM productos WHERE stock <= 0 AND activo = 1")
    suspend fun obtenerSinStock(): List<Producto>

    // ⭐ ACTUALIZADO: Usar precioVenta para valor de inventario
    @Query("SELECT SUM(stock * precioVenta) FROM productos WHERE activo = 1")
    suspend fun obtenerValorTotalInventario(): Float?

    // ⭐ NUEVO: Valor de inventario a precio de compra
    @Query("SELECT SUM(stock * precioCompra) FROM productos WHERE activo = 1")
    suspend fun obtenerValorInventarioCompra(): Float?

    // ⭐ NUEVO: Valor de inventario a precio de venta
    @Query("SELECT SUM(stock * precioVenta) FROM productos WHERE activo = 1")
    suspend fun obtenerValorInventarioVenta(): Float?

    // ⭐ NUEVO: Ganancia potencial total
    @Query("SELECT SUM((precioVenta - precioCompra) * stock) FROM productos WHERE activo = 1 AND precioCompra > 0")
    suspend fun obtenerGananciaPotencial(): Float?

    // ⭐ NUEVO: Productos más rentables (mayor ganancia unitaria)
    @Query("""
        SELECT * FROM productos 
        WHERE activo = 1 AND stock > 0 AND precioCompra > 0
        ORDER BY (precioVenta - precioCompra) DESC
        LIMIT :limite
    """)
    suspend fun obtenerMasRentables(limite: Int = 10): List<Producto>

    // ⭐ NUEVO: Productos con mejor margen (%)
    @Query("""
        SELECT * FROM productos 
        WHERE activo = 1 AND precioCompra > 0 AND stock > 0
        ORDER BY ((precioVenta - precioCompra) / precioCompra) DESC
        LIMIT :limite
    """)
    suspend fun obtenerMejorMargen(limite: Int = 10): List<Producto>

    // ========== SINCRONIZACIÓN ==========

    @Query("SELECT * FROM productos WHERE pendienteSincronizar = 1")
    suspend fun obtenerPendientesSincronizar(): List<Producto>

    @Query("UPDATE productos SET pendienteSincronizar = 0, ultimaSincronizacion = :timestamp WHERE id = :productoId")
    suspend fun marcarComoSincronizado(productoId: String, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT MAX(ultimaSincronizacion) FROM productos")
    suspend fun obtenerUltimaSincronizacion(): Long?

    @Query("""
        SELECT * FROM productos 
        WHERE ultimaActualizacion > :timestamp
        ORDER BY ultimaActualizacion DESC
    """)
    suspend fun obtenerModificadosDespuesDe(timestamp: Long): List<Producto>

    // ========== ESTADÍSTICAS ==========

    @Query("SELECT COUNT(*) FROM productos WHERE activo = 1")
    suspend fun contarActivos(): Int

    @Query("SELECT COUNT(*) FROM productos WHERE activo = 1 AND stock > 0")
    suspend fun contarConStock(): Int

    @Query("SELECT COUNT(DISTINCT categoria) FROM productos WHERE activo = 1")
    suspend fun contarCategorias(): Int

    @Query("""
        SELECT categoria, COUNT(*) as cantidad 
        FROM productos 
        WHERE activo = 1 
        GROUP BY categoria
        ORDER BY cantidad DESC
    """)
    suspend fun obtenerEstadisticasPorCategoria(): List<EstadisticaCategoria>
}

/**
 * Clase para estadísticas por categoría
 */
data class EstadisticaCategoria(
    val categoria: String,
    val cantidad: Int
)