package crystal.crystal.productos

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Repository para Producto - Room + Firestore
 * Sincronización optimizada solo de cambios
 */
class ProductoRepository(
    private val productoDao: ProductoDao,
    private val context: Context,
    private val movimientoDao: MovimientoInventarioDao? = null,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    companion object {
        private const val TAG = "ProductoRepository"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    val todosLosProductos: LiveData<List<Producto>> = productoDao.obtenerTodosLive()

    /**
     * Obtiene el UID de la empresa (patron_uid)
     */
    private fun obtenerUidEmpresa(): String? {
        val patronUid = sharedPreferences.getString("patron_uid", null)
        if (patronUid != null) {
            Log.d(TAG, "📱 Usando patron_uid: $patronUid")
            return patronUid
        }

        val currentUid = auth.currentUser?.uid
        if (currentUid != null) {
            Log.d(TAG, "👤 Usando auth.uid: $currentUid")
            return currentUid
        }

        Log.e(TAG, "❌ Sin UID de empresa")
        return null
    }

    // ========== CRUD ==========

    suspend fun crear(producto: Producto): Result<String> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "➕ Creando: ${producto.nombre}")

            val productoPendiente = producto.copy(pendienteSincronizar = true)
            productoDao.insertar(productoPendiente)
            sincronizarProductoAFirestore(productoPendiente)

            Result.success(producto.id)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creando", e)
            Result.failure(e)
        }
    }

    suspend fun actualizar(producto: Producto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val productoPendiente = producto.marcarPendiente()
            productoDao.actualizar(productoPendiente)
            sincronizarProductoAFirestore(productoPendiente)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminar(productoId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            productoDao.obtenerPorId(productoId)?.let { producto ->
                val inactivo = producto.copy(activo = false).marcarPendiente()
                productoDao.actualizar(inactivo)
                sincronizarProductoAFirestore(inactivo)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== CONSULTAS ==========

    suspend fun obtenerTodos() = productoDao.obtenerTodos()
    suspend fun obtenerPorId(id: String) = productoDao.obtenerPorId(id)
    suspend fun obtenerPorNombre(nombre: String) = productoDao.obtenerPorNombre(nombre)
    suspend fun buscar(q: String, lim: Int = 10) = productoDao.buscar(q, lim)
    suspend fun obtenerPorCategoria(cat: String) = productoDao.obtenerPorCategoria(cat)
    suspend fun obtenerPorEspesor(esp: String) = productoDao.obtenerPorEspesor(esp)

    // ========== STOCK ==========

    /**
     * Registrar venta - RESTA del stock
     */
    /** Descuento de un solo producto. Para una venta completa usar [prepararConsumo] + [aplicarConsumo]. */
    suspend fun registrarVenta(
        productoId: String,
        cantidad: Float,
        referencia: String = "",
        vendedor: String = ""
    ): Result<Unit> = withContext(Dispatchers.IO) {
        val consumos = prepararConsumo(mapOf(productoId to cantidad))
            .getOrElse { return@withContext Result.failure(it) }
        aplicarConsumo(consumos, referencia.ifBlank { "VENTA-${System.currentTimeMillis()}" }, vendedor)
    }

    // ========== VENTA CON DESCUENTO DE STOCK ==========

    /**
     * Lo que una línea de la venta consume del inventario. Se calcula ANTES de tocar nada, para
     * poder rechazar la venta completa si a un solo ítem le falta stock.
     */
    data class ConsumoStock(
        val productoId: String,
        val productoNombre: String,
        /** Cantidad en la unidad de stock (p2 para vidrio, unidades para el resto). */
        val cantidad: Float,
        /** Planchas que consume (solo vidrio). */
        val planchas: Float
    )

    /**
     * Calcula qué consume cada línea y verifica que alcance. Devuelve el detalle, o un fallo con el
     * primer producto que no da. No modifica nada: separar el cálculo de la escritura es lo que
     * permite abortar la venta entera sin dejar descuentos a medias.
     *
     * [cantidadPorProducto] va en la unidad de stock del producto: pies cuadrados para el vidrio,
     * unidades para lo demás.
     */
    suspend fun prepararConsumo(
        cantidadPorProducto: Map<String, Float>
    ): Result<List<ConsumoStock>> = withContext(Dispatchers.IO) {
        val consumos = mutableListOf<ConsumoStock>()
        for ((productoId, cantidad) in cantidadPorProducto) {
            if (cantidad <= 0f) continue
            val producto = productoDao.obtenerPorId(productoId)
                ?: return@withContext Result.failure(Exception("Producto no encontrado en inventario"))

            if (producto.stock < cantidad) {
                return@withContext Result.failure(
                    Exception("Stock insuficiente de ${producto.nombre}: quedan ${formatear(producto.stock)} ${producto.unidad}")
                )
            }

            // El vidrio además consume planchas físicas: vender 2.5 m² gasta la fracción de plancha
            // que le corresponde. Si no se cargaron las medidas, solo se descuenta el área.
            val planchas = if (producto.esVidrio()) producto.planchasParaArea(cantidad) else 0f
            if (planchas > 0f && producto.stockPlanchas > 0f && producto.stockPlanchas < planchas) {
                return@withContext Result.failure(
                    Exception("No alcanzan las planchas de ${producto.nombre}: quedan ${formatear(producto.stockPlanchas)}")
                )
            }

            consumos += ConsumoStock(producto.id, producto.nombre, cantidad, planchas)
        }
        Result.success(consumos)
    }

    /**
     * Aplica el consumo: descuenta el stock y deja un movimiento por cada producto. Se llama después
     * de que la venta quedó registrada, con su número de comprobante como [referencia].
     *
     * El descuento se hace como diferencia (`stock = stock + delta`), no fijando un total, y el
     * movimiento guarda esa diferencia: así lo que quede pendiente de subir puede sumarse en el
     * servidor sin importar el orden en que lleguen las terminales.
     */
    suspend fun aplicarConsumo(
        consumos: List<ConsumoStock>,
        referencia: String,
        vendedor: String = "",
        terminal: String? = null,
        tipo: String = "VENTA"
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val uid = obtenerUidEmpresa().orEmpty()
            for (consumo in consumos) {
                val antes = productoDao.obtenerPorId(consumo.productoId) ?: continue
                productoDao.actualizarStockYPlanchas(
                    productoId = consumo.productoId,
                    cantidad = -consumo.cantidad,
                    planchas = -consumo.planchas
                )
                val despues = productoDao.obtenerPorId(consumo.productoId)

                movimientoDao?.insertar(
                    MovimientoInventario(
                        productoId = consumo.productoId,
                        productoNombre = consumo.productoNombre,
                        tipo = tipo,
                        cantidad = -consumo.cantidad,
                        cantidadPlanchas = -consumo.planchas,
                        stockAnterior = antes.stock,
                        stockNuevo = despues?.stock ?: (antes.stock - consumo.cantidad),
                        stockPlanchasAnterior = antes.stockPlanchas,
                        stockPlanchasNuevo = despues?.stockPlanchas
                            ?: (antes.stockPlanchas - consumo.planchas).coerceAtLeast(0f),
                        referencia = referencia,
                        vendedor = vendedor,
                        terminal = terminal,
                        uidPatron = uid
                    )
                )

                productoDao.obtenerPorId(consumo.productoId)?.let { sincronizarProductoAFirestore(it) }
            }
            Log.d(TAG, "📦 Stock descontado por $referencia: ${consumos.size} producto(s)")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error aplicando consumo de stock", e)
            Result.failure(e)
        }
    }

    private fun formatear(valor: Float): String =
        if (valor % 1f == 0f) valor.toInt().toString() else String.format("%.2f", valor)

    /**
     * Devuelve al inventario lo que consumió una venta anulada, dejando el movimiento inverso. El
     * movimiento original NO se borra: el libro registra los dos hechos.
     */
    suspend fun revertirConsumo(referencia: String, motivo: String = ""): Result<Int> =
        withContext(Dispatchers.IO) {
            try {
                val dao = movimientoDao ?: return@withContext Result.success(0)
                val originales = dao.porReferencia(referencia).filter { it.tipo != "ANULACION" }
                if (originales.isEmpty()) return@withContext Result.success(0)
                val uid = obtenerUidEmpresa().orEmpty()

                for (original in originales) {
                    val antes = productoDao.obtenerPorId(original.productoId) ?: continue
                    productoDao.actualizarStockYPlanchas(
                        productoId = original.productoId,
                        cantidad = -original.cantidad,
                        planchas = -original.cantidadPlanchas
                    )
                    val despues = productoDao.obtenerPorId(original.productoId)
                    dao.insertar(
                        MovimientoInventario(
                            productoId = original.productoId,
                            productoNombre = original.productoNombre,
                            tipo = "ANULACION",
                            cantidad = -original.cantidad,
                            cantidadPlanchas = -original.cantidadPlanchas,
                            stockAnterior = antes.stock,
                            stockNuevo = despues?.stock ?: antes.stock,
                            stockPlanchasAnterior = antes.stockPlanchas,
                            stockPlanchasNuevo = despues?.stockPlanchas ?: antes.stockPlanchas,
                            referencia = referencia,
                            observaciones = motivo.ifBlank { "Anulación de $referencia" },
                            uidPatron = uid
                        )
                    )
                    productoDao.obtenerPorId(original.productoId)?.let { sincronizarProductoAFirestore(it) }
                }
                Result.success(originales.size)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error revirtiendo $referencia", e)
                Result.failure(e)
            }
        }

    /**
     * Agregar stock - SUMA al stock
     */
    suspend fun agregarStock(productoId: String, cantidad: Float): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                productoDao.actualizarStock(productoId, cantidad)

                productoDao.obtenerPorId(productoId)?.let {
                    sincronizarProductoAFirestore(it)
                }

                Log.d(TAG, "📦 Stock agregado: +$cantidad")
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun obtenerStockBajo() = productoDao.obtenerStockBajo()
    suspend fun obtenerSinStock() = productoDao.obtenerSinStock()
    suspend fun obtenerValorInventario() = productoDao.obtenerValorTotalInventario() ?: 0f

    // ========== ESTADÍSTICAS ==========

    suspend fun contarActivos() = productoDao.contarActivos()
    suspend fun contarConStock() = productoDao.contarConStock()
    suspend fun obtenerEstadisticas() = productoDao.obtenerEstadisticasPorCategoria()

    // ========== SINCRONIZACIÓN ==========

    private suspend fun sincronizarProductoAFirestore(producto: Producto): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val uid = obtenerUidEmpresa() ?: return@withContext Result.failure(
                    Exception("Sin UID empresa")
                )

                Log.d(TAG, "☁️ Subiendo: ${producto.nombre}")
                Log.d(TAG, "   → usuarios/$uid/productos/${producto.id}")

                firestore.collection("usuarios")
                    .document(uid)
                    .collection("productos")
                    .document(producto.id)
                    .set(producto.toFirestoreMap())
                    .await()

                productoDao.marcarComoSincronizado(producto.id)
                Log.d(TAG, "✅ Sincronizado: ${producto.nombre}")

                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error: ${e.message}", e)
                Result.failure(e)
            }
        }

    suspend fun sincronizarPendientesAFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val pendientes = productoDao.obtenerPendientesSincronizar()
            if (pendientes.isEmpty()) {
                Log.d(TAG, "✅ Sin pendientes")
                return@withContext Result.success(0)
            }

            Log.d(TAG, "🔄 Sincronizando ${pendientes.size} pendientes...")
            var exitosos = 0
            pendientes.forEach {
                if (sincronizarProductoAFirestore(it).isSuccess) exitosos++
            }

            Log.d(TAG, "✅ $exitosos/${pendientes.size} sincronizados")
            Result.success(exitosos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun descargarCambiosDesdeFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val uid = obtenerUidEmpresa() ?: return@withContext Result.failure(
                Exception("Sin UID empresa")
            )

            val ultimaSync = productoDao.obtenerUltimaSincronizacion() ?: 0L

            Log.d(TAG, "🔽 Descargando cambios...")
            Log.d(TAG, "   → usuarios/$uid/productos")

            val snapshot = firestore.collection("usuarios")
                .document(uid)
                .collection("productos")
                .whereGreaterThan("ultimaActualizacion", ultimaSync)
                .get()
                .await()

            if (snapshot.isEmpty) {
                Log.d(TAG, "✅ Sin cambios")
                return@withContext Result.success(0)
            }

            val productos = snapshot.documents.mapNotNull {
                try {
                    Producto.fromFirestoreMap(it.data ?: emptyMap())
                } catch (e: Exception) {
                    null
                }
            }

            productoDao.insertarVarios(productos)
            Log.d(TAG, "✅ Descargados ${productos.size}")

            Result.success(productos.size)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error descarga", e)
            Result.failure(e)
        }
    }

    suspend fun sincronizarCompleta(): Result<Pair<Int, Int>> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "🔄 SYNC COMPLETA")

            val subidos = sincronizarPendientesAFirestore().getOrDefault(0)
            val descargados = descargarCambiosDesdeFirestore().getOrDefault(0)

            Log.d(TAG, "✅ ↑$subidos ↓$descargados")
            Result.success(Pair(subidos, descargados))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun descargarTodoDesdeFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val uid = obtenerUidEmpresa() ?: return@withContext Result.failure(
                Exception("Sin UID")
            )

            Log.d(TAG, "📥 Descarga completa...")

            val snapshot = firestore.collection("usuarios")
                .document(uid)
                .collection("productos")
                .get()
                .await()

            val productos = snapshot.documents.mapNotNull {
                try {
                    Producto.fromFirestoreMap(it.data ?: emptyMap())
                } catch (e: Exception) {
                    null
                }
            }

            productoDao.insertarVarios(productos)
            Log.d(TAG, "✅ Total: ${productos.size}")

            Result.success(productos.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}