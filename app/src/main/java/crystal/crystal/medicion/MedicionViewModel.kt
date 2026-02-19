package crystal.crystal.medicion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MedicionViewModel(application: Application) : AndroidViewModel(application) {
    private val repo: MedicionRepository = MedicionRepository(
        MedicionDatabase.getDatabase(application).itemMedicionObraDao()
    )

    val items: LiveData<List<ItemMedicionObra>> = repo.itemsLive

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> = _mensaje

    fun guardar(item: ItemMedicionObra, onSuccess: ((ItemMedicionObra) -> Unit)? = null) {
        viewModelScope.launch {
            runCatching { repo.guardar(item) }
                .onSuccess {
                    onSuccess?.invoke(it)
                    _mensaje.value = "Ítem guardado"
                }
                .onFailure { _mensaje.value = "No se pudo guardar: ${it.message}" }
        }
    }

    fun eliminar(id: String) {
        viewModelScope.launch {
            runCatching { repo.eliminar(id) }
                .onFailure { _mensaje.value = "No se pudo eliminar: ${it.message}" }
        }
    }

    fun cambiarEstado(id: String, estado: EstadoMedicion) {
        viewModelScope.launch {
            runCatching { repo.cambiarEstado(id, estado) }
                .onFailure { _mensaje.value = "No se pudo cambiar estado: ${it.message}" }
        }
    }

    fun duplicar(item: ItemMedicionObra) {
        viewModelScope.launch {
            runCatching { repo.duplicar(item) }
                .onFailure { _mensaje.value = "No se pudo duplicar: ${it.message}" }
        }
    }
}

