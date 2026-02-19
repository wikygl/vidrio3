package crystal.crystal.datos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {

    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    // READ - Obtener todos
    @Query("SELECT * FROM products")
    suspend fun getAllProducts(): List<Product>

    // READ - Obtener por ID
    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Int): Product?

    // UPDATE
    @Update
    suspend fun updateProduct(product: Product)

    // DELETE
    @Delete
    suspend fun deleteProduct(product: Product)

    // SEARCH - por descripción (usando LIKE)
    @Query("SELECT * FROM products WHERE description LIKE :searchText")
    suspend fun searchProductsByDescription(searchText: String): List<Product>
}
