package crystal.crystal.datos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Query("SELECT * FROM products ORDER BY nombre ASC")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT * FROM products WHERE nombre = :nombre LIMIT 1")
    suspend fun getProductByName(nombre: String): Product?

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("SELECT * FROM products WHERE nombre LIKE :searchText ORDER BY nombre ASC")
    suspend fun searchProductsByName(searchText: String): List<Product>

    @Query("SELECT * FROM products WHERE nombre LIKE :searchText ORDER BY nombre ASC")
    suspend fun searchProductsByDescription(searchText: String): List<Product>
}
