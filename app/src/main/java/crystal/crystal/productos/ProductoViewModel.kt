package crystal.crystal.productos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de productos
 */
class ProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductoRepository
    val todosLosProductos: LiveData<List<Producto>>

    init {
        val database = ProductoDatabase.getDatabase(application)
        repository = ProductoRepository(
            database.productoDao(),
            application.applicationContext
        )
        todosLosProductos = repository.todosLosProductos
    }

    // ========== CRUD ==========

    fun crear(producto: Producto, onResult: (Result<String>) -> Unit) {
        viewModelScope.launch {
            val result = repository.crear(producto)
            onResult(result)
        }
    }

    fun actualizar(producto: Producto, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.actualizar(producto)
            onResult(result)
        }
    }

    fun eliminar(productoId: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.eliminar(productoId)
            onResult(result)
        }
    }

    // ========== CONSULTAS ==========

    fun obtenerTodos(onResult: (List<Producto>) -> Unit) {
        viewModelScope.launch {
            val productos = repository.obtenerTodos()
            onResult(productos)
        }
    }

    fun obtenerPorId(id: String, onResult: (Producto?) -> Unit) {
        viewModelScope.launch {
            val producto = repository.obtenerPorId(id)
            onResult(producto)
        }
    }

    fun obtenerPorNombre(nombre: String, onResult: (Producto?) -> Unit) {
        viewModelScope.launch {
            val producto = repository.obtenerPorNombre(nombre)
            onResult(producto)
        }
    }

    fun buscar(consulta: String, limite: Int = 10, onResult: (List<Producto>) -> Unit) {
        viewModelScope.launch {
            val resultados = repository.buscar(consulta, limite)
            onResult(resultados)
        }
    }

    fun obtenerPorCategoria(categoria: String, onResult: (List<Producto>) -> Unit) {
        viewModelScope.launch {
            val productos = repository.obtenerPorCategoria(categoria)
            onResult(productos)
        }
    }

    fun obtenerPorEspesor(espesor: String, onResult: (List<Producto>) -> Unit) {
        viewModelScope.launch {
            val productos = repository.obtenerPorEspesor(espesor)
            onResult(productos)
        }
    }

    // ========== STOCK ==========

    fun registrarVenta(productoId: String, cantidad: Int, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.registrarVenta(productoId, cantidad)
            onResult(result)
        }
    }

    fun agregarStock(productoId: String, cantidad: Int, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.agregarStock(productoId, cantidad)
            onResult(result)
        }
    }

    fun obtenerStockBajo(onResult: (List<Producto>) -> Unit) {
        viewModelScope.launch {
            val productos = repository.obtenerStockBajo()
            onResult(productos)
        }
    }

    fun obtenerSinStock(onResult: (List<Producto>) -> Unit) {
        viewModelScope.launch {
            val productos = repository.obtenerSinStock()
            onResult(productos)
        }
    }

    fun obtenerValorInventario(onResult: (Float) -> Unit) {
        viewModelScope.launch {
            val valor = repository.obtenerValorInventario()
            onResult(valor)
        }
    }

    // ========== ESTADÍSTICAS ==========

    fun obtenerEstadisticas(onResult: (List<EstadisticaCategoria>) -> Unit) {
        viewModelScope.launch {
            val stats = repository.obtenerEstadisticas()
            onResult(stats)
        }
    }

    fun contarActivos(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.contarActivos()
            onResult(count)
        }
    }

    fun contarConStock(onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val count = repository.contarConStock()
            onResult(count)
        }
    }

    // ========== SINCRONIZACIÓN ==========

    fun sincronizarAhora(onResult: (Result<Pair<Int, Int>>) -> Unit) {
        viewModelScope.launch {
            val result = repository.sincronizarCompleta()
            onResult(result)
        }
    }

    fun descargarCatalogo(onResult: (Result<Int>) -> Unit) {
        viewModelScope.launch {
            val result = repository.descargarTodoDesdeFirestore()
            onResult(result)
        }
    }
}