package crystal.crystal.pos

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Registro de una venta procesada (comprobante emitido). Antes las ventas no se guardaban en ningún
 * lado (solo se generaba el PDF); esta entidad les da persistencia local para numeración correlativa
 * y reportes. Sigue el patrón de sincronización del proyecto (pendienteSincronizar / ultimaSincronizacion).
 */
@Entity(tableName = "ventas")
data class Venta(
    @PrimaryKey val id: String,
    val numeroComprobante: String,
    val serie: String,
    val numero: Long,
    val tipoComprobante: String,
    val cliente: String,
    val subtotal: Float,
    val igv: Float,
    val total: Float,
    val formaPago: String,
    val vendedor: String,
    val terminal: String?,
    /** Items de la venta serializados a JSON (List<ItemVenta>). */
    val itemsJson: String,
    val fecha: Long,
    /** UID del patrón (o del usuario) al que pertenece la venta; agrupa por cuenta. */
    val uidPatron: String,
    val pendienteSincronizar: Boolean = true,
    val ultimaSincronizacion: Long = 0L
)
