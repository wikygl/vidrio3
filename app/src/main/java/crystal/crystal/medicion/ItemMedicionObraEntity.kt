package crystal.crystal.medicion

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "items_medicion_obra",
    indices = [
        Index("estado"),
        Index("clienteId"),
        Index("categoria"),
        Index("actualizadoEn")
    ]
)
data class ItemMedicionObraEntity(
    @PrimaryKey val id: String,
    val numero: Int,
    val clienteId: String?,
    val clienteNombre: String?,
    val categoria: String,
    val sistema: String,
    val tipoApertura: String,
    val geometria: String,
    val encuentros: String,
    val modelo: String,
    val acabado: String,
    val especificaciones: Map<String, String>,
    val accesorios: List<String>,
    val anchoCm: Float,
    val altoCm: Float,
    val alturaPuenteCm: Float,
    val unidadCaptura: String,
    val ubicacionObra: String?,
    val notasObra: String?,
    val evidencias: List<String>,
    val estado: String,
    val pendienteSincronizar: Boolean,
    val medidoPor: String,
    val dispositivoId: String,
    val creadoEn: Long,
    val actualizadoEn: Long
)

