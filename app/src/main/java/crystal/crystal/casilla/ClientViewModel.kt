package crystal.crystal.casilla

// ClientViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClientViewModel : ViewModel() {
    private val _cliente = MutableLiveData<String>()
    val cliente: LiveData<String> get() = _cliente

    fun setCliente(newCliente: String) {
        _cliente.value = newCliente
    }

    fun initializeCliente(initialCliente: String?) {
        if (_cliente.value.isNullOrEmpty() && initialCliente != null) {
            _cliente.value = initialCliente
        }
    }
}
