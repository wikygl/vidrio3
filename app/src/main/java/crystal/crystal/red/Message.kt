import com.google.firebase.firestore.Exclude
import java.util.Date

data class Message(
    var id: String = "",
    var message: String = "",
    var from: String = "",
    var dob: Date? = null,
    var leido: Boolean = false,
    var entregado: Boolean = false,
    var deletedFor: List<String> = emptyList(),
    var deletedForEveryone: Boolean = false,
    var tipo: String = "texto",
    var nombreArchivo: String = ""  // <-- NUEVO campo para mostrar nombre bonito
) {
    @get:Exclude
    @set:Exclude
    var hasPendingWrites: Boolean = false
}
