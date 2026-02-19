package crystal.crystal.medicion

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ItemMedicionObraDao {
    @Query("SELECT * FROM items_medicion_obra ORDER BY actualizadoEn DESC")
    fun observarTodos(): LiveData<List<ItemMedicionObraEntity>>

    @Query("SELECT * FROM items_medicion_obra ORDER BY actualizadoEn DESC")
    suspend fun obtenerTodos(): List<ItemMedicionObraEntity>

    @Query("SELECT * FROM items_medicion_obra WHERE estado = :estado ORDER BY actualizadoEn DESC")
    suspend fun obtenerPorEstado(estado: String): List<ItemMedicionObraEntity>

    @Query(
        """
        SELECT * FROM items_medicion_obra 
        WHERE categoria LIKE '%' || :texto || '%'
           OR sistema LIKE '%' || :texto || '%'
           OR tipoApertura LIKE '%' || :texto || '%'
           OR COALESCE(clienteNombre, '') LIKE '%' || :texto || '%'
           OR COALESCE(ubicacionObra, '') LIKE '%' || :texto || '%'
        ORDER BY actualizadoEn DESC
        """
    )
    suspend fun buscar(texto: String): List<ItemMedicionObraEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(item: ItemMedicionObraEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodos(items: List<ItemMedicionObraEntity>)

    @Update
    suspend fun actualizar(item: ItemMedicionObraEntity)

    @Query("DELETE FROM items_medicion_obra WHERE id = :id")
    suspend fun eliminarPorId(id: String)

    @Query("DELETE FROM items_medicion_obra")
    suspend fun eliminarTodo()

    @Query("UPDATE items_medicion_obra SET estado = :estado, actualizadoEn = :actualizadoEn, pendienteSincronizar = 1 WHERE id = :id")
    suspend fun actualizarEstado(id: String, estado: String, actualizadoEn: Long = System.currentTimeMillis())
}

