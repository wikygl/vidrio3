package crystal.crystal.productos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovimientoInventarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(movimiento: MovimientoInventario)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(movimientos: List<MovimientoInventario>)

    /** Historial de un producto, del más reciente al más antiguo. */
    @Query("SELECT * FROM movimientos_inventario WHERE productoId = :productoId ORDER BY fecha DESC")
    suspend fun porProducto(productoId: String): List<MovimientoInventario>

    @Query("SELECT * FROM movimientos_inventario WHERE fecha >= :desde ORDER BY fecha DESC")
    suspend fun desde(desde: Long): List<MovimientoInventario>

    @Query("SELECT * FROM movimientos_inventario WHERE referencia = :referencia ORDER BY fecha")
    suspend fun porReferencia(referencia: String): List<MovimientoInventario>

    @Query("SELECT * FROM movimientos_inventario WHERE pendienteSincronizar = 1 ORDER BY fecha")
    suspend fun pendientes(): List<MovimientoInventario>

    @Query("SELECT COUNT(*) FROM movimientos_inventario WHERE pendienteSincronizar = 1")
    suspend fun contarPendientes(): Int

    @Query("UPDATE movimientos_inventario SET pendienteSincronizar = 0, ultimaSincronizacion = :ts WHERE id = :id")
    suspend fun marcarSincronizado(id: String, ts: Long = System.currentTimeMillis())
}
