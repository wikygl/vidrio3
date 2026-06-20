package crystal.crystal.datos

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val nombre: String,
    val price: Double,
    val imagenes: String = ""
) {
    val precio: Double
        get() = price

    fun imagenes(): List<String> {
        return imagenes
            .lineSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()
    }
}
