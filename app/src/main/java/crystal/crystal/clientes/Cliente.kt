package crystal.crystal.clientes

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "clientes",
    indices = [
        Index(value = ["numeroDocumento"], unique = true),
        Index(value = ["nombreCompleto"]),
        Index(value = ["pendienteSincronizar"])  // Índice para queries de sync
    ]
)
data class Cliente(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),  // UUID compartido entre dispositivos

    // Identificación
    val tipoDocumento: TipoDocumento = TipoDocumento.DNI,
    val numeroDocumento: String,
    val nombreCompleto: String,

    // Contacto
    val direccion: String? = null,
    val telefono: String? = null,
    val email: String? = null,

    // Comercial
    val descuentoPorcentaje: Float? = null,

    // Estadísticas (se actualizan localmente)
    val fechaRegistro: Long = System.currentTimeMillis(),
    val ultimaCompra: Long? = null,
    val totalCompras: Float = 0f,
    val numeroCompras: Int = 0,

    // Sincronización
    val pendienteSincronizar: Boolean = false,  // true = hay cambios locales sin subir
    val ultimaActualizacionLocal: Long = System.currentTimeMillis(),  // Última modificación local
    val ultimaSincronizacion: Long = 0,  // Última vez que se sincronizó con Firestore

    // Otros
    val notas: String? = null,
    val activo: Boolean = true
) {

    fun toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "tipoDocumento" to tipoDocumento.name,
            "numeroDocumento" to numeroDocumento,
            "nombreCompleto" to nombreCompleto,
            "direccion" to direccion,
            "telefono" to telefono,
            "email" to email,
            "descuentoPorcentaje" to descuentoPorcentaje,
            "fechaRegistro" to fechaRegistro,
            "ultimaCompra" to ultimaCompra,
            "totalCompras" to totalCompras,
            "numeroCompras" to numeroCompras,
            "notas" to notas,
            "activo" to activo,
            "ultimaActualizacion" to ultimaActualizacionLocal  // Timestamp para Firestore
        )
    }

    fun getTextoCompleto(): String {
        val descuento = if (descuentoPorcentaje != null && descuentoPorcentaje > 0) {
            " (${descuentoPorcentaje.toInt()}% desc.)"
        } else ""
        return "$nombreCompleto$descuento"
    }

    fun marcarPendiente(): Cliente {
        return this.copy(
            pendienteSincronizar = true,
            ultimaActualizacionLocal = System.currentTimeMillis()
        )
    }

    fun marcarSincronizado(): Cliente {
        return this.copy(
            pendienteSincronizar = false,
            ultimaSincronizacion = System.currentTimeMillis()
        )
    }

    companion object {

        fun fromFirestoreMap(map: Map<String, Any?>): Cliente {
            return Cliente(
                id = map["id"] as? String ?: UUID.randomUUID().toString(),
                tipoDocumento = TipoDocumento.fromString(map["tipoDocumento"] as? String ?: "DNI"),
                numeroDocumento = map["numeroDocumento"] as? String ?: "",
                nombreCompleto = map["nombreCompleto"] as? String ?: "",
                direccion = map["direccion"] as? String,
                telefono = map["telefono"] as? String,
                email = map["email"] as? String,
                descuentoPorcentaje = (map["descuentoPorcentaje"] as? Number)?.toFloat(),
                fechaRegistro = (map["fechaRegistro"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                ultimaCompra = (map["ultimaCompra"] as? Number)?.toLong(),
                totalCompras = (map["totalCompras"] as? Number)?.toFloat() ?: 0f,
                numeroCompras = (map["numeroCompras"] as? Number)?.toInt() ?: 0,
                notas = map["notas"] as? String,
                activo = map["activo"] as? Boolean ?: true,
                pendienteSincronizar = false,  // Viene de Firestore, ya está sincronizado
                ultimaSincronizacion = System.currentTimeMillis(),
                ultimaActualizacionLocal = (map["ultimaActualizacion"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        }
    }
}

enum class TipoDocumento(val nombre: String, val longitud: Int) {
    DNI("DNI", 8),
    RUC("RUC", 11),
    CE("Carnet de Extranjería", 12),
    PASAPORTE("Pasaporte", 12);

    companion object {
        fun fromString(valor: String): TipoDocumento {
            return values().find { it.name.equals(valor, ignoreCase = true) } ?: DNI
        }
    }
}