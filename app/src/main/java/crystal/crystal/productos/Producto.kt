package crystal.crystal.productos

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize  // ⭐ AGREGAR ESTO
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    // Información básica
    val nombre: String,
    val categoria: String,
    val descripcion: String? = null,

    // ⭐ PRECIOS ACTUALIZADOS
    val precioCompra: Float = 0f,      // Precio al que compramos
    val precioVenta: Float,             // Precio al que vendemos

    // Stock
    val stock: Int = 0,
    val stockMinimo: Int = 10,
    val unidad: String = "m2",

    // Medidas (para vidrios)
    val espesor: String? = null,
    val tipo: String? = null,

    // Sincronización
    val activo: Boolean = true,
    val pendienteSincronizar: Boolean = false,
    val ultimaActualizacion: Long = System.currentTimeMillis(),
    val ultimaSincronizacion: Long = 0L,
    val ultimaActualizacionLocal: Long = System.currentTimeMillis()
) : Parcelable {  // ⭐ IMPLEMENTAR PARCELABLE

    /**
     * Calcular margen de ganancia
     */
    fun calcularMargen(): Float {
        if (precioCompra == 0f) return 0f
        return ((precioVenta - precioCompra) / precioCompra) * 100
    }

    /**
     * Calcular ganancia unitaria
     */
    fun calcularGanancia(): Float {
        return precioVenta - precioCompra
    }

    /**
     * Calcular valor del inventario (compra)
     */
    fun valorInventarioCompra(): Float {
        return stock * precioCompra
    }

    /**
     * Calcular valor del inventario (venta)
     */
    fun valorInventarioVenta(): Float {
        return stock * precioVenta
    }

    /**
     * Texto para mostrar en búsquedas
     */
    fun getTextoCompleto(): String {
        val partes = mutableListOf<String>()

        partes.add(nombre)

        if (!espesor.isNullOrEmpty()) {
            partes.add(espesor)
        }

        if (!tipo.isNullOrEmpty()) {
            partes.add(tipo)
        }

        if (stock > 0) {
            partes.add("Stock: $stock")
        }

        return partes.joinToString(" • ")
    }

    fun necesitaReabastecimiento(): Boolean = stock <= stockMinimo
    fun tieneStock(cantidad: Int = 1): Boolean = stock >= cantidad

    fun marcarPendiente(): Producto = copy(
        pendienteSincronizar = true,
        ultimaActualizacionLocal = System.currentTimeMillis()
    )

    fun marcarSincronizado(): Producto = copy(
        pendienteSincronizar = false,
        ultimaSincronizacion = System.currentTimeMillis()
    )

    fun actualizarStock(cantidad: Int): Producto = copy(
        stock = stock + cantidad,
        ultimaActualizacion = System.currentTimeMillis(),
        pendienteSincronizar = true
    )

    /**
     * Convertir a Map para Firestore
     */
    fun toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "nombre" to nombre,
        "categoria" to categoria,
        "descripcion" to descripcion,
        "precioCompra" to precioCompra,     // ⭐ NUEVO
        "precioVenta" to precioVenta,       // ⭐ RENOMBRADO
        "stock" to stock,
        "stockMinimo" to stockMinimo,
        "unidad" to unidad,
        "espesor" to espesor,
        "tipo" to tipo,
        "activo" to activo,
        "ultimaActualizacion" to ultimaActualizacion
    )

    companion object {
        /**
         * Crear desde Map de Firestore
         */
        fun fromFirestoreMap(map: Map<String, Any>): Producto {
            return Producto(
                id = map["id"] as? String ?: UUID.randomUUID().toString(),
                nombre = map["nombre"] as? String ?: "",
                categoria = map["categoria"] as? String ?: "",
                descripcion = map["descripcion"] as? String,
                precioCompra = (map["precioCompra"] as? Number)?.toFloat() ?: 0f,  // ⭐ NUEVO
                precioVenta = (map["precioVenta"] as? Number)?.toFloat()
                    ?: (map["precio"] as? Number)?.toFloat() ?: 0f,  // ⭐ Compatibilidad con datos viejos
                stock = (map["stock"] as? Number)?.toInt() ?: 0,
                stockMinimo = (map["stockMinimo"] as? Number)?.toInt() ?: 10,
                unidad = map["unidad"] as? String ?: "m2",
                espesor = map["espesor"] as? String,
                tipo = map["tipo"] as? String,
                activo = map["activo"] as? Boolean ?: true,
                ultimaActualizacion = (map["ultimaActualizacion"] as? Number)?.toLong()
                    ?: System.currentTimeMillis(),
                pendienteSincronizar = false,
                ultimaSincronizacion = System.currentTimeMillis()
            )
        }
    }
}

enum class CategoriaProducto(val nombre: String) {
    TEMPLADO("Vidrio Templado"),
    CRUDO("Vidrio Crudo"),
    LAMINADO("Vidrio Laminado"),
    ESPEJO("Espejo"),
    ALUMINIO("Aluminio"),
    ACCESORIOS("Accesorios"),
    OTRO("Otro");

    companion object {
        fun fromString(nombre: String): CategoriaProducto {
            return entries.find { it.nombre.equals(nombre, ignoreCase = true) } ?: OTRO
        }
    }
}