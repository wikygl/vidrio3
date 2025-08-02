package crystal.crystal.red

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import crystal.crystal.databinding.ActivityListChatBinding
import java.util.Date
import java.util.UUID

class ListChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListChatBinding
    private lateinit var currentUserId: String
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    // Chats existentes y lista combinada
    private var existingChats = mutableListOf<Chat>()
    private var fullChatList = mutableListOf<Chat>()

    // Mapa de UID → nombre real
    private val userNamesMap = mutableMapOf<String, String>()

    // **AGREGAR ESTAS VARIABLES PARA PRESUPUESTOS:**
    private var presupuestoParaEnviar: String? = null
    private var nombrePresupuesto: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // **AGREGAR ESTAS LÍNEAS AL INICIO DEL ONCREATE:**
        // Manejar presupuesto a enviar
        presupuestoParaEnviar = intent.getStringExtra("enviar_presupuesto")
        nombrePresupuesto = intent.getStringExtra("nombre_presupuesto")

        if (presupuestoParaEnviar != null) {
            // Mostrar mensaje informativo
            Toast.makeText(this, "Selecciona un chat para enviar: $nombrePresupuesto", Toast.LENGTH_LONG).show()

            // Opcional: Destacar visualmente que se va a enviar un presupuesto
            supportActionBar?.subtitle = "Enviando: $nombrePresupuesto"
        }

        // 1) Obtener UID auténtico
        currentUserId = auth.currentUser?.uid.orEmpty()
        if (currentUserId.isEmpty()) {
            finish()
            return
        }

        setupRecycler()
        initViews()
    }

    private fun setupRecycler() {
        binding.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.rvChatList.adapter = ChatAdapter(currentUserId) { chat ->
            chatSeleccionado(chat)
        }
    }

    private fun initViews() {
        binding.btnEnviar.setOnClickListener { nuevoChat() }

        // 2) Cargar todos los usuarios Y filtrar tu propio doc
        db.collection("usuarios")
            .get()
            .addOnSuccessListener { usersSnap ->
                // Sólo los UID distintos al tuyo
                val otherUserIds = usersSnap.documents
                    .map { it.id }
                    .filter { it != currentUserId }

                // Poblar nombres para todos (incluyendo tu UID, aunque no lo mostraremos)
                usersSnap.documents.forEach { doc ->
                    val uid = doc.id
                    val nombre = doc.getString("nombre") ?: uid
                    userNamesMap[uid] = nombre
                }
                // Nombre fijo para tu chat de "Mensajes guardados"
                userNamesMap[currentUserId] = "Mensajes guardados"

                // 3) Escuchar los chats ya creados
                escucharChatsExistentes(otherUserIds)
            }
    }

    private fun escucharChatsExistentes(otherUserIds: List<String>) {
        db.collection("usuarios")
            .document(currentUserId)
            .collection("chats")
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) return@addSnapshotListener

                // Reconstruir lista de chats existentes
                existingChats = snap.toObjects(Chat::class.java).toMutableList()

                // Para cada chat existente obtén últimas métricas
                for (chat in existingChats) {
                    val otherUid = chat.users.firstOrNull { it != currentUserId }
                        ?: currentUserId

                    // Ajustar nombre (override si es self-chat)
                    chat.name = if (otherUid == currentUserId) {
                        "Mensajes guardados"
                    } else {
                        userNamesMap[otherUid] ?: otherUid
                    }

                    if (chat.id.isNotEmpty()) {
                        // Contar no leídos
                        db.collection("chats")
                            .document(chat.id)
                            .collection("messages")
                            .whereEqualTo("leido", false)
                            .whereNotEqualTo("from", currentUserId)
                            .get()
                            .addOnSuccessListener { msgsSnap ->
                                chat.unreadCount = msgsSnap.size()

                                // Último mensaje
                                db.collection("chats")
                                    .document(chat.id)
                                    .collection("messages")
                                    .orderBy("dob", Query.Direction.DESCENDING)
                                    .limit(1)
                                    .get()
                                    .addOnSuccessListener { lastSnap ->
                                        chat.lastMessageText =
                                            if (!lastSnap.isEmpty)
                                                lastSnap.documents[0].getString("message")
                                                    ?: ""
                                            else
                                                ""
                                        fusionarChatsYUsuarios(otherUserIds)
                                    }
                            }
                    }
                }

                // Fusionar tras procesar existentes
                fusionarChatsYUsuarios(otherUserIds)
            }
    }

    private fun fusionarChatsYUsuarios(otherUserIds: List<String>) {
        fullChatList.clear()

        // 4) Agregar siempre primero el chat dummy de "Mensajes guardados"
        fullChatList.add(
            Chat(
                id = "",
                name = userNamesMap[currentUserId]!!,
                users = listOf(currentUserId, currentUserId),
                lastMsgDate = null,
                unreadCount = 0,
                lastMessageText = ""
            )
        )

        // Map de existingChats por el UID "otro"
        val mapExisting = existingChats.associateBy { chat ->
            chat.users.firstOrNull { it != currentUserId } ?: currentUserId
        }

        // 5) Luego iterar sólo sobre los demás usuarios
        for (uid in otherUserIds) {
            val chat = mapExisting[uid]
            if (chat != null) {
                fullChatList.add(chat)
            } else {
                // Dummy sin historial
                fullChatList.add(
                    Chat(
                        id = "",
                        name = userNamesMap[uid] ?: uid,
                        users = listOf(currentUserId, uid),
                        lastMsgDate = null,
                        unreadCount = 0,
                        lastMessageText = ""
                    )
                )
            }
        }

        ordenarYActualizarLista()
    }

    private fun ordenarYActualizarLista() {
        fullChatList.sortWith(
            compareByDescending<Chat> { it.unreadCount > 0 }
                .thenByDescending { it.lastMsgDate ?: Date(0) }
        )
        (binding.rvChatList.adapter as ChatAdapter).setData(fullChatList)
    }

    // 6) Al pulsar "Enviar" o crear nuevo chat
    private fun nuevoChat() {
        val otherUid = binding.etNuevoMsm.text.toString().trim()
        if (otherUid.isNotEmpty()) {
            val displayName = userNamesMap[otherUid] ?: otherUid
            val chatId = UUID.randomUUID().toString()
            val nuevo = Chat(
                id = chatId,
                name = displayName,
                users = listOf(currentUserId, otherUid),
                lastMsgDate = Date(),
                unreadCount = 0,
                lastMessageText = ""
            )
            // Guardar en colección global y en cada usuario
            db.collection("chats").document(chatId).set(nuevo)
            db.collection("usuarios").document(currentUserId)
                .collection("chats").document(chatId).set(nuevo)
            db.collection("usuarios").document(otherUid)
                .collection("chats").document(chatId).set(nuevo)

            // **MODIFICAR ESTA PARTE PARA PASAR DATOS DE PRESUPUESTO:**
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("chatId", chatId)
                putExtra("usuario", currentUserId)
                // Pasar datos de presupuesto si existen
                presupuestoParaEnviar?.let { putExtra("enviar_presupuesto", it) }
                nombrePresupuesto?.let { putExtra("nombre_presupuesto", it) }
            }
            startActivity(intent)
        }
    }

    private fun chatSeleccionado(chat: Chat) {
        var chatId = chat.id
        if (chatId.isEmpty()) {
            // Crear chat nuevo al vuelo
            val otherUid = chat.users.firstOrNull { it != currentUserId }
                ?: return
            chatId = UUID.randomUUID().toString()
            val nuevo = Chat(
                id = chatId,
                name = chat.name,
                users = listOf(currentUserId, otherUid),
                lastMsgDate = Date(),
                unreadCount = 0,
                lastMessageText = ""
            )
            db.collection("chats").document(chatId).set(nuevo)
            db.collection("usuarios").document(currentUserId)
                .collection("chats").document(chatId).set(nuevo)
            db.collection("usuarios").document(otherUid)
                .collection("chats").document(chatId).set(nuevo)
        }

        // **MODIFICAR ESTA PARTE PARA PASAR DATOS DE PRESUPUESTO:**
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("chatId", chatId)
            putExtra("usuario", currentUserId)
            // Pasar datos de presupuesto si existen
            presupuestoParaEnviar?.let { putExtra("enviar_presupuesto", it) }
            nombrePresupuesto?.let { putExtra("nombre_presupuesto", it) }
        }
        startActivity(intent)
    }
}
