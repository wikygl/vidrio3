package crystal.crystal.clientes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * ViewModel para GestionClientesActivity
 */
class ClienteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ClienteRepository

    // LiveData observable para la UI
    val todosLosClientes: LiveData<List<Cliente>>

    init {
        val database = ClienteDatabase.getDatabase(application)
        repository = ClienteRepository(database.clienteDao(),application.applicationContext)
        todosLosClientes = repository.todosLosClientes
    }

    // ========== OPERACIONES CRUD ==========

    fun crear(cliente: Cliente) = viewModelScope.launch {
        repository.crear(cliente)
    }

    fun actualizar(cliente: Cliente) = viewModelScope.launch {
        repository.actualizar(cliente)
    }

    fun eliminar(clienteId: String) = viewModelScope.launch {
        repository.eliminar(clienteId)
    }

    suspend fun buscar(consulta: String): List<Cliente> {
        return repository.buscar(consulta)
    }

    suspend fun obtenerPorDocumento(documento: String): Cliente? {
        return repository.obtenerPorDocumento(documento)
    }

    // ========== SINCRONIZACIÓN ==========

    fun sincronizarAhora() = viewModelScope.launch {
        repository.sincronizarCompleta()
    }

    // ========== ESTADÍSTICAS ==========

    suspend fun obtenerTopClientes(limite: Int = 10): List<Cliente> {
        return repository.obtenerTopClientes(limite)
    }

    suspend fun contarClientes(): Int {
        return repository.contarActivos()
    }

    suspend fun obtenerTotalVendido(): Float {
        return repository.obtenerTotalVendido()
    }
}