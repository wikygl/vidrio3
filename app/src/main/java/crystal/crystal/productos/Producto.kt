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

    // Stock. Es decimal porque el vidrio se vende por área: de una plancha salen fracciones.
    val stock: Float = 0f,
    val stockMinimo: Float = 10f,
    val unidad: String = "m2",

    // Medidas (para vidrios)
    val espesor: String? = null,
    val tipo: String? = null,

    // ── Vidrio: stock en dos dimensiones ────────────────────────────────────────
    // Un vidrio se guarda en PLANCHAS enteras pero se vende por ÁREA (p2/m2) o por plancha
    // completa. [stock] lleva el área disponible y [stockPlanchas] las planchas físicas: vender
    // 2.5 m² descuenta el área y la fracción de plancha que le corresponde. Para lo que no es
    // vidrio, [stock] son unidades sueltas y estos campos quedan en cero.
    val stockPlanchas: Float = 0f,
    /** Medidas de la plancha en cm, para convertir entre área y planchas. */
    val anchoPlancha: Float = 0f,
    val altoPlancha: Float = 0f,
    /** Cuántas planchas trae una caja (0 si no se vende por caja). */
    val planchasPorCaja: Int = 0,

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
    fun tieneStock(cantidad: Float = 1f): Boolean = stock >= cantidad

    // ── Vidrio ──────────────────────────────────────────────────────────────────

    /** El vidrio se descuenta por área y por planchas; el resto de categorías, por unidades. */
    fun esVidrio(): Boolean = CategoriaProducto.fromString(categoria).esVidrio

    /**
     * Área de una plancha en pies cuadrados. Las medidas se guardan en cm, pero se aceptan también
     * en metros: un valor de 10 o menos se toma como metros (no existe una plancha de 3 cm).
     */
    fun areaPlanchaP2(): Float {
        if (anchoPlancha <= 0f || altoPlancha <= 0f) return 0f
        val anchoM = if (anchoPlancha > 10f) anchoPlancha / 100f else anchoPlancha
        val altoM = if (altoPlancha > 10f) altoPlancha / 100f else altoPlancha
        return anchoM * altoM * P2_POR_M2
    }

    fun cajasDisponibles(): Float =
        if (planchasPorCaja <= 0) 0f else stockPlanchas / planchasPorCaja

    /**
     * Cuántas planchas consume vender [areaP2] pies cuadrados. Devuelve 0 si no es vidrio o si no
     * se cargaron las medidas de la plancha (ahí solo se descuenta el área).
     */
    fun planchasParaArea(areaP2: Float): Float {
        val areaPlancha = areaPlanchaP2()
        return if (areaPlancha <= 0f) 0f else areaP2 / areaPlancha
    }

    fun marcarPendiente(): Producto = copy(
        pendienteSincronizar = true,
        ultimaActualizacionLocal = System.currentTimeMillis()
    )

    fun marcarSincronizado(): Producto = copy(
        pendienteSincronizar = false,
        ultimaSincronizacion = System.currentTimeMillis()
    )

    fun actualizarStock(cantidad: Float): Producto = copy(
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
        "stockPlanchas" to stockPlanchas,
        "anchoPlancha" to anchoPlancha,
        "altoPlancha" to altoPlancha,
        "planchasPorCaja" to planchasPorCaja,
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
                stock = (map["stock"] as? Number)?.toFloat() ?: 0f,
                stockMinimo = (map["stockMinimo"] as? Number)?.toFloat() ?: 10f,
                unidad = map["unidad"] as? String ?: "m2",
                espesor = map["espesor"] as? String,
                tipo = map["tipo"] as? String,
                stockPlanchas = (map["stockPlanchas"] as? Number)?.toFloat() ?: 0f,
                anchoPlancha = (map["anchoPlancha"] as? Number)?.toFloat() ?: 0f,
                altoPlancha = (map["altoPlancha"] as? Number)?.toFloat() ?: 0f,
                planchasPorCaja = (map["planchasPorCaja"] as? Number)?.toInt() ?: 0,
                activo = map["activo"] as? Boolean ?: true,
                ultimaActualizacion = (map["ultimaActualizacion"] as? Number)?.toLong()
                    ?: System.currentTimeMillis(),
                pendienteSincronizar = false,
                ultimaSincronizacion = System.currentTimeMillis()
            )
        }
    }
}

/** Factor de conversión de metro cuadrado a pie cuadrado. */
const val P2_POR_M2 = 11.1f

enum class CategoriaProducto(val nombre: String, val esVidrio: Boolean = false) {
    TEMPLADO("Vidrio Templado", esVidrio = true),
    CRUDO("Vidrio Crudo", esVidrio = true),
    LAMINADO("Vidrio Laminado", esVidrio = true),
    ESPEJO("Espejo", esVidrio = true),
    ALUMINIO("Aluminio"),
    ACCESORIOS("Accesorios"),
    OTRO("Otro");

    companion object {
        fun fromString(nombre: String): CategoriaProducto {
            return entries.find { it.nombre.equals(nombre, ignoreCase = true) } ?: OTRO
        }
    }
}