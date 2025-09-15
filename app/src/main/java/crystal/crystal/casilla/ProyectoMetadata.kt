package crystal.crystal.casilla

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ProyectoMetadata(
    val nombre: String,
    val descripcion: String = "",
    val fechaCreacion: String = obtenerFechaActual(),
    var fechaModificacion: String = obtenerFechaActual(),
    var contadorVentanas: Int = 0
) {
    companion object {
        private fun obtenerFechaActual(): String {
            val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return formato.format(Date())
        }
    }

    // FunciÃ³n para actualizar la fecha de modificaciÃ³n
    fun actualizarFechaModificacion() {
        fechaModificacion = obtenerFechaActual()
    }
}