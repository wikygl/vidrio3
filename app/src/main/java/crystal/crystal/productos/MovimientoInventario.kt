package crystal.crystal.productos

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

/**
 * Un movimiento de inventario: cada vez que el stock cambia queda asentado aquí, con el valor
 * anterior y el nuevo. Es un libro, no un estado: **nunca se edita ni se borra**.
 *
 * Existe por dos razones:
 *
 * 1. **Auditoría.** Responde "¿por qué el stock dice 4 si compré 10?" — quién, cuándo, por qué
 *    comprobante. Sin esto un descuadre no se puede investigar.
 * 2. **Sincronización sin pérdidas.** Un movimiento es una DIFERENCIA (−3), no un total (7). Si dos
 *    terminales venden sin señal y cada una manda su total, gana la última y se pierden unidades;
 *    si mandan −3 y −2, el servidor los suma y el resultado es correcto sin importar el orden ni
 *    quién llegue primero. Por eso lo pendiente de subir son los movimientos, no el producto.
 */
@Entity(tableName = "movimientos_inventario")
data class MovimientoInventario(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),

    val productoId: String,
    val productoNombre: String,

    /** VENTA, INGRESO, AJUSTE, ANULACION. */
    val tipo: String,

    /** Cantidad del movimiento con signo: negativa si sale, positiva si entra. */
    val cantidad: Float,
    /** Planchas que entran o salen (solo vidrio); mismo signo que [cantidad]. */
    val cantidadPlanchas: Float = 0f,

    val stockAnterior: Float,
    val stockNuevo: Float,
    val stockPlanchasAnterior: Float = 0f,
    val stockPlanchasNuevo: Float = 0f,

    /** A qué documento responde el movimiento (p. ej. "VENTA-B001-00000042"). */
    val referencia: String = "",
    val vendedor: String = "",
    val terminal: String? = null,
    val observaciones: String = "",

    val fecha: Long = System.currentTimeMillis(),

    /** Cuenta a la que pertenece el movimiento (uid del patrón), para agrupar como las ventas. */
    val uidPatron: String = "",
    val pendienteSincronizar: Boolean = true,
    val ultimaSincronizacion: Long = 0L
) {
    fun esSalida(): Boolean = cantidad < 0f

    fun toFirestoreMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "productoId" to productoId,
        "productoNombre" to productoNombre,
        "tipo" to tipo,
        "cantidad" to cantidad,
        "cantidadPlanchas" to cantidadPlanchas,
        "stockAnterior" to stockAnterior,
        "stockNuevo" to stockNuevo,
        "stockPlanchasAnterior" to stockPlanchasAnterior,
        "stockPlanchasNuevo" to stockPlanchasNuevo,
        "referencia" to referencia,
        "vendedor" to vendedor,
        "terminal" to terminal,
        "observaciones" to observaciones,
        "fecha" to fecha
    )
}
