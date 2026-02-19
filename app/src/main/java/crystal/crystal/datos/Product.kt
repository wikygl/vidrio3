package crystal.crystal.datos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String,
    val price: Double,
    val stock: Int
)
