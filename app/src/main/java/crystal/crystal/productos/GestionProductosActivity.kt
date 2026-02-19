package crystal.crystal.productos

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import crystal.crystal.databinding.ActivityGestionProductosBinding

/**
 * Activity para gestionar el catálogo de productos
 * ⭐ CON LOGS DE DEBUG PARA VERIFICAR RESTRICCIONES
 */
class GestionProductosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestionProductosBinding
    private lateinit var viewModel: ProductoViewModel
    private lateinit var adapter: ProductoAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private var esTerminal = false

    companion object {
        private const val TAG = "GestionProductos"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestionProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // ⭐ IDENTIFICAR TIPO DE USUARIO PRIMERO
        identificarTipoUsuario()

        // Configurar toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gestión de Productos"

        // Inicializar ViewModel
        viewModel = ViewModelProvider(this)[ProductoViewModel::class.java]

        // Configurar RecyclerView
        configurarRecyclerView()

        // Configurar botones
        configurarBotones()

        // Observar LiveData
        observarProductos()

        // Cargar estadísticas
        cargarEstadisticas()

        // Sincronizar automáticamente
        sincronizar()
    }

    private fun identificarTipoUsuario() {
        // Leer TODAS las claves para debug
        val todasLasClaves = sharedPreferences.all
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
        Log.d(TAG, "📋 TODAS LAS CLAVES EN SHAREDPREFERENCES:")
        todasLasClaves.forEach { (key, value) ->
            Log.d(TAG, "   $key = $value")
        }
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

        // Leer rol_dispositivo
        val rolDispositivo = sharedPreferences.getString("rol_dispositivo", null)
        val esPatron = sharedPreferences.getBoolean("es_patron", true)
        val nombreVendedor = sharedPreferences.getString("nombre_vendedor", "")

        Log.d(TAG, "🔍 DATOS LEÍDOS:")
        Log.d(TAG, "   rol_dispositivo = $rolDispositivo")
        Log.d(TAG, "   es_patron = $esPatron")
        Log.d(TAG, "   nombre_vendedor = $nombreVendedor")

        // Determinar si es terminal
        esTerminal = (rolDispositivo == "TERMINAL")

        Log.d(TAG, "🎯 RESULTADO:")
        Log.d(TAG, "   esTerminal = $esTerminal")

        if (esTerminal) {
            Log.d(TAG, "🔒 MODO TERMINAL ACTIVADO - Restricciones aplicadas")
        } else {
            Log.d(TAG, "🔑 MODO PATRÓN ACTIVADO - Acceso completo")
        }
        Log.d(TAG, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    }

    private fun configurarRecyclerView() {
        adapter = ProductoAdapter(
            onProductoClick = { producto ->
                Log.d(TAG, "👆 Click en producto: ${producto.nombre}")
                mostrarDialogoDetalles(producto)
            },
            onEditClick = { producto ->
                Log.d(TAG, "✏️ Click en editar: ${producto.nombre}")
                mostrarDialogoEditar(producto)
            },
            onDeleteClick = { producto ->
                Log.d(TAG, "🗑️ Click en eliminar: ${producto.nombre}")
                confirmarEliminar(producto)
            },
            onStockClick = { producto ->
                Log.d(TAG, "📦 Click en stock: ${producto.nombre}")
                mostrarDialogoStock(producto)
            }
        )

        // ⭐ CONFIGURAR TIPO DE USUARIO EN ADAPTER
        adapter.configurarTipoUsuario(this)

        binding.recyclerProductos.apply {
            layoutManager = LinearLayoutManager(this@GestionProductosActivity)
            adapter = this@GestionProductosActivity.adapter
        }
    }

    private fun configurarBotones() {
        // Botón agregar
        if (esTerminal) {
            Log.d(TAG, "❌ FAB oculto (Terminal)")
            binding.fabAgregar.visibility = View.GONE
        } else {
            Log.d(TAG, "✅ FAB visible (Patrón)")
            binding.fabAgregar.visibility = View.VISIBLE
            binding.fabAgregar.setOnClickListener {
                mostrarDialogoNuevoProducto()
            }
        }

        // Botón sincronizar
        binding.btnSincronizar.setOnClickListener {
            sincronizar()
        }

        // SwipeRefresh
        binding.swipeRefresh.setOnRefreshListener {
            sincronizar()
        }

        // Botón stock bajo
        binding.btnStockBajo.setOnClickListener {
            if (esTerminal) {
                Toast.makeText(this, "🔒 Sin permisos para ajustar stock", Toast.LENGTH_SHORT).show()
            } else {
                mostrarProductosStockBajo()
            }
        }

        // Botón filtros
        binding.btnFiltros.setOnClickListener {
            mostrarDialogoFiltros()
        }
    }

    private fun observarProductos() {
        viewModel.todosLosProductos.observe(this) { productos ->
            Log.d(TAG, "📦 Productos cargados: ${productos.size}")
            adapter.actualizarLista(productos)

            binding.tvCantidad.text = "Total: ${productos.size} productos"

            if (productos.isEmpty()) {
                binding.recyclerProductos.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerProductos.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE
            }
        }
    }

    private fun sincronizar() {
        binding.swipeRefresh.isRefreshing = true

        viewModel.sincronizarAhora { result ->
            binding.swipeRefresh.isRefreshing = false

            if (result.isSuccess) {
                val (subidos, descargados) = result.getOrDefault(Pair(0, 0))
                Toast.makeText(
                    this,
                    "✅ Sincronizado: ↑$subidos ↓$descargados",
                    Toast.LENGTH_SHORT
                ).show()
                cargarEstadisticas()
            } else {
                Toast.makeText(this, "❌ Error sincronizando", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarEstadisticas() {
        viewModel.contarActivos { total ->
            binding.tvTotal.text = total.toString()
        }

        viewModel.contarConStock { conStock ->
            binding.tvConStock.text = conStock.toString()
        }

        viewModel.obtenerStockBajo { stockBajo ->
            binding.tvStockBajo.text = stockBajo.size.toString()

            if (stockBajo.isNotEmpty() && !esTerminal) {
                binding.btnStockBajo.visibility = View.VISIBLE
            } else {
                binding.btnStockBajo.visibility = View.GONE
            }
        }

        viewModel.obtenerValorInventario { valor ->
            binding.tvValorInventario.text = "S/ ${String.format("%.2f", valor)}"
        }
    }

    private fun mostrarDialogoNuevoProducto() {
        if (esTerminal) {
            Log.d(TAG, "🔒 BLOQUEADO: Crear producto (Terminal)")
            Toast.makeText(this, "🔒 Sin permisos para crear productos", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "✅ PERMITIDO: Crear producto (Patrón)")
        DialogoNuevoProducto.newInstance(
            producto = null,
            onGuardar = { producto ->
                viewModel.crear(producto) { result ->
                    if (result.isSuccess) {
                        Toast.makeText(this, "✅ Producto creado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "❌ Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ).show(supportFragmentManager, "DialogoNuevo")
    }

    private fun mostrarDialogoEditar(producto: Producto) {
        if (esTerminal) {
            Log.d(TAG, "🔒 BLOQUEADO: Editar producto (Terminal)")
            Toast.makeText(this, "🔒 Sin permisos para editar productos", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "✅ PERMITIDO: Editar producto (Patrón)")
        DialogoNuevoProducto.newInstance(
            producto = producto,
            onGuardar = { productoEditado ->
                viewModel.actualizar(productoEditado) { result ->
                    if (result.isSuccess) {
                        Toast.makeText(this, "✅ Producto actualizado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "❌ Error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ).show(supportFragmentManager, "DialogoEditar")
    }

    private fun confirmarEliminar(producto: Producto) {
        if (esTerminal) {
            Log.d(TAG, "🔒 BLOQUEADO: Eliminar producto (Terminal)")
            Toast.makeText(this, "🔒 Sin permisos para eliminar productos", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "✅ PERMITIDO: Eliminar producto (Patrón)")
        AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Eliminar ${producto.nombre}?")
            .setPositiveButton("Eliminar") { _, _ ->
                eliminarProducto(producto)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarProducto(producto: Producto) {
        viewModel.eliminar(producto.id) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "✅ Producto eliminado", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "❌ Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoStock(producto: Producto) {
        if (esTerminal) {
            Log.d(TAG, "🔒 BLOQUEADO: Ajustar stock (Terminal)")
            Toast.makeText(this, "🔒 Sin permisos para ajustar stock", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "✅ PERMITIDO: Ajustar stock (Patrón)")
        DialogoAjustarStock.newInstance(
            producto = producto,
            onAjustar = { cantidad ->
                if (cantidad > 0) {
                    viewModel.agregarStock(producto.id, cantidad) { result ->
                        if (result.isSuccess) {
                            Toast.makeText(
                                this,
                                "✅ Stock actualizado: +$cantidad",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else if (cantidad < 0) {
                    viewModel.registrarVenta(producto.id, -cantidad) { result ->
                        if (result.isSuccess) {
                            Toast.makeText(
                                this,
                                "✅ Stock actualizado: $cantidad",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "❌ Stock insuficiente",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        ).show(supportFragmentManager, "DialogoStock")
    }

    private fun mostrarDialogoDetalles(producto: Producto) {
        Log.d(TAG, "✅ PERMITIDO: Ver detalles (Todos)")
        DialogoDetallesProducto.newInstance(producto)
            .show(supportFragmentManager, "DialogoDetalles")
    }

    private fun mostrarProductosStockBajo() {
        viewModel.obtenerStockBajo { productos ->
            if (productos.isEmpty()) {
                Toast.makeText(this, "✅ No hay productos con stock bajo", Toast.LENGTH_SHORT).show()
                return@obtenerStockBajo
            }

            val items = productos.map {
                "${it.nombre} - Stock: ${it.stock} (Mín: ${it.stockMinimo})"
            }.toTypedArray()

            AlertDialog.Builder(this)
                .setTitle("⚠️ Stock bajo (${productos.size})")
                .setItems(items) { _, which ->
                    if (!esTerminal) {
                        mostrarDialogoStock(productos[which])
                    }
                }
                .setNegativeButton("Cerrar", null)
                .show()
        }
    }

    private fun mostrarDialogoFiltros() {
        val categorias = CategoriaProducto.values().map { it.nombre }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Filtrar por categoría")
            .setItems(categorias) { _, which ->
                val categoria = CategoriaProducto.values()[which]
                filtrarPorCategoria(categoria.nombre.lowercase())
            }
            .setNegativeButton("Todos") { _, _ ->
                adapter.limpiarFiltro()
            }
            .show()
    }

    private fun filtrarPorCategoria(categoria: String) {
        viewModel.obtenerPorCategoria(categoria) { productos ->
            adapter.actualizarLista(productos)
            binding.tvCantidad.text = "Categoría: ${productos.size} productos"
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}