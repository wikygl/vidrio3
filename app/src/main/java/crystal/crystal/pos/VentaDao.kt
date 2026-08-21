package crystal.crystal.pos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VentaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(venta: Venta)

    /** Último correlativo emitido para una serie (para calcular el siguiente número). */
    @Query("SELECT MAX(numero) FROM ventas WHERE uidPatron = :uid AND serie = :serie")
    suspend fun ultimoNumero(uid: String, serie: String): Long?

    @Query("SELECT * FROM ventas WHERE uidPatron = :uid ORDER BY fecha DESC")
    suspend fun listar(uid: String): List<Venta>

    @Query("SELECT * FROM ventas WHERE uidPatron = :uid AND fecha >= :desde ORDER BY fecha DESC")
    suspend fun listarDesde(uid: String, desde: Long): List<Venta>

    @Query("SELECT * FROM ventas WHERE pendienteSincronizar = 1")
    suspend fun pendientesSincronizar(): List<Venta>

    @Query("UPDATE ventas SET pendienteSincronizar = 0, ultimaSincronizacion = :ts WHERE id = :id")
    suspend fun marcarSincronizada(id: String, ts: Long)
}
