package crystal.crystal.red

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
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

    private var presupuestoParaEnviar: String? = null
    private var nombrePresupuesto: String? = null

    // Listas
    private var chatsActivos = mutableListOf<Chat>()
    private var todosLosContactos = mutableListOf<ContactoTelefono>()
    private var contactosConCrystal = mutableSetOf<String>() // Set de emails

    private val CONTACTS_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presupuestoParaEnviar = intent.getStringExtra("enviar_presupuesto")
        nombrePresupuesto = intent.getStringExtra("nombre_presupuesto")

        if (presupuestoParaEnviar != null) {
            Toast.makeText(this, "Selecciona un chat para enviar: $nombrePresupuesto", Toast.LENGTH_LONG).show()
            supportActionBar?.subtitle = "Enviando: $nombrePresupuesto"
        }

        currentUserId = intent.getStringExtra("usuario")
            ?: auth.currentUser?.uid
                    ?: ""

        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "Error: No se pudo identificar usuario", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupTabs()
        setupRecycler()
        cargarChats()

        binding.btnEnviar.setOnClickListener { buscarPorEmail() }

        verificarPermisoContactos()
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Chats"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Contactos"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Buscar"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> mostrarChats()
                    1 -> mostrarContactos()
                    2 -> mostrarBusqueda()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupRecycler() {
        binding.rvChatList.layoutManager = LinearLayoutManager(this)
        binding.rvChatList.adapter = ChatAdapter(currentUserId) { chat ->
            abrirChat(chat)
        }
    }

    private fun cargarChats() {
        db.collection("chats")
            .whereArrayContains("users", currentUserId)
            .orderBy("lastMsgDate", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    Log.e("ListChat", "Error: ${err.message}")
                    return@addSnapshotListener
                }

                if (snap == null || snap.isEmpty) {
                    chatsActivos.clear()
                    (binding.rvChatList.adapter as ChatAdapter).setData(emptyList())
                    return@addSnapshotListener
                }

                chatsActivos = snap.toObjects(Chat::class.java).toMutableList()

                chatsActivos.forEach { chat ->
                    val otherUid = chat.users.firstOrNull { it != currentUserId } ?: currentUserId

                    if (otherUid == currentUserId) {
                        chat.name = "Mensajes guardados"
                    }

                    db.collection("chats")
                        .document(chat.id)
                        .collection("messages")
                        .whereEqualTo("leido", false)
                        .whereNotEqualTo("from", currentUserId)
                        .get()
                        .addOnSuccessListener { msgsSnap ->
                            chat.unreadCount = msgsSnap.size()
                        }

                    db.collection("chats")
                        .document(chat.id)
                        .collection("messages")
                        .orderBy("dob", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .addOnSuccessListener { lastSnap ->
                            chat.lastMessageText = if (!lastSnap.isEmpty) {
                                lastSnap.documents[0].getString("message") ?: ""
                            } else {
                                ""
                            }
                        }
                }

                (binding.rvChatList.adapter as ChatAdapter).setData(chatsActivos)
            }
    }

    private fun verificarPermisoContactos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                CONTACTS_PERMISSION_CODE
            )
        } else {
            cargarContactosDelTelefono()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CONTACTS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cargarContactosDelTelefono()
            } else {
                Toast.makeText(this, "Permiso de contactos denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarContactosDelTelefono() {
        todosLosContactos.clear()
        val contactosMap = mutableMapOf<String, ContactoTelefono>()

        // Leer TODOS los contactos con email
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            arrayOf(
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.DISPLAY_NAME
            ),
            null, null,
            ContactsContract.CommonDataKinds.Email.DISPLAY_NAME + " ASC"
        )

        cursor?.use {
            val emailIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS)
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Email.DISPLAY_NAME)

            while (it.moveToNext()) {
                val email = it.getString(emailIndex)?.lowercase()?.trim()
                val nombre = it.getString(nameIndex) ?: "Sin nombre"

                if (!email.isNullOrBlank() && email.contains("@")) {
                    // Evitar duplicados por email
                    if (!contactosMap.containsKey(email)) {
                        contactosMap[email] = ContactoTelefono(
                            nombre = nombre,
                            email = email,
                            tieneCrystal = false
                        )
                    }
                }
            }
        }

        todosLosContactos.addAll(contactosMap.values)

        Log.d("ListChat", "Contactos del teléfono: ${todosLosContactos.size}")

        // Ahora buscar cuáles tienen Crystal
        buscarContactosEnFirebase()
    }

    private fun buscarContactosEnFirebase() {
        if (todosLosContactos.isEmpty()) return

        val emails = todosLosContactos.map { it.email }
        contactosConCrystal.clear()

        // Buscar en batches de 10
        for (i in emails.indices step 10) {
            val batch = emails.subList(i, minOf(i + 10, emails.size))

            db.collection("usuarios")
                .whereIn("email", batch)
                .get()
                .addOnSuccessListener { snapshot ->
                    snapshot.documents.forEach { doc ->
                        val email = doc.getString("email")?.lowercase()?.trim()
                        if (email != null) {
                            contactosConCrystal.add(email)
                        }
                    }

                    // Actualizar lista
                    todosLosContactos.forEach { contacto ->
                        contacto.tieneCrystal = contactosConCrystal.contains(contacto.email)
                    }

                    Log.d("ListChat", "Contactos con Crystal: ${contactosConCrystal.size}")
                }
        }
    }

    private fun mostrarChats() {
        binding.etNuevoMsm.visibility = android.view.View.GONE
        binding.btnEnviar.visibility = android.view.View.GONE
        (binding.rvChatList.adapter as ChatAdapter).setData(chatsActivos)
    }

    private fun mostrarContactos() {
        binding.etNuevoMsm.visibility = android.view.View.GONE
        binding.btnEnviar.visibility = android.view.View.GONE

        Log.d("ListChat", "Mostrando ${todosLosContactos.size} contactos")

        // Convertir a Chat para reusar ChatAdapter
        val chatsDeContactos = todosLosContactos.map { contacto ->
            Chat(
                id = if (contacto.tieneCrystal) "" else "invitar",
                name = contacto.nombre,
                users = listOf(currentUserId),
                lastMsgDate = null,
                unreadCount = 0,
                lastMessageText = if (contacto.tieneCrystal) {
                    "✅ ${contacto.email}"
                } else {
                    "📧 Invitar: ${contacto.email}"
                }
            )
        }

        (binding.rvChatList.adapter as ChatAdapter).setData(chatsDeContactos)
    }

    private fun mostrarBusqueda() {
        binding.etNuevoMsm.visibility = android.view.View.VISIBLE
        binding.btnEnviar.visibility = android.view.View.VISIBLE
        binding.etNuevoMsm.hint = "Buscar por email"
    }

    private fun buscarPorEmail() {
        val email = binding.etNuevoMsm.text.toString().trim().lowercase()
        if (email.isEmpty()) {
            Toast.makeText(this, "Ingresa un email", Toast.LENGTH_SHORT).show()
            return
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("usuarios")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                } else {
                    val otherUid = snapshot.documents[0].id
                    if (otherUid == currentUserId) {
                        Toast.makeText(this, "No puedes chatear contigo mismo", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    buscarOCrearChat(otherUid)
                }
            }
    }

    private fun buscarOCrearChat(otherUid: String) {
        db.collection("chats")
            .whereArrayContains("users", currentUserId)
            .get()
            .addOnSuccessListener { snapshot ->
                val chatExistente = snapshot.documents.firstOrNull { doc ->
                    val users = doc.get("users") as? List<String> ?: emptyList()
                    users.contains(otherUid)
                }

                if (chatExistente != null) {
                    val chat = chatExistente.toObject(Chat::class.java)!!
                    abrirChatActivity(chat.id)
                } else {
                    crearYAbrirChat(otherUid)
                }
            }
    }

    private fun crearYAbrirChat(otherUid: String) {
        db.collection("usuarios")
            .document(otherUid)
            .get()
            .addOnSuccessListener { doc ->
                val nombre = doc.getString("nombre") ?: doc.getString("email") ?: otherUid
                crearYAbrirChatNuevo(otherUid, nombre)
            }
    }

    private fun abrirChat(chat: Chat) {
        // Si es "invitar", enviar SMS/Email
        if (chat.id == "invitar") {
            val email = chat.lastMessageText.replace("📧 Invitar: ", "")
            invitarContacto(email)
            return
        }

        // Si no tiene ID (contacto con Crystal), buscar su email y crear chat
        if (chat.id.isEmpty()) {
            val email = chat.lastMessageText.replace("✅ ", "")
            buscarUsuarioPorEmailYAbrirChat(email)
            return
        }

        // Chat normal
        abrirChatActivity(chat.id)
    }

    private fun buscarUsuarioPorEmailYAbrirChat(email: String) {
        db.collection("usuarios")
            .whereEqualTo("email", email)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val otherUid = snapshot.documents[0].id
                    buscarOCrearChat(otherUid)
                }
            }
    }

    private fun invitarContacto(email: String) {
        val mensaje = "¡Hola! Te invito a usar Crystal. Descárgala aquí: [link]"

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "Invitación a Crystal")
            putExtra(Intent.EXTRA_TEXT, mensaje)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No hay app de email instalada", Toast.LENGTH_SHORT).show()
        }
    }

    private fun crearYAbrirChatNuevo(otherUid: String, nombre: String) {
        val chatId = UUID.randomUUID().toString()
        val nuevoChat = Chat(
            id = chatId,
            name = nombre,
            users = listOf(currentUserId, otherUid),
            lastMsgDate = Date(),
            unreadCount = 0,
            lastMessageText = ""
        )

        db.collection("chats")
            .document(chatId)
            .set(nuevoChat)
            .addOnSuccessListener {
                abrirChatActivity(chatId)
            }
    }

    private fun abrirChatActivity(chatId: String) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("chatId", chatId)
            putExtra("usuario", currentUserId)
            presupuestoParaEnviar?.let { putExtra("enviar_presupuesto", it) }
            nombrePresupuesto?.let { putExtra("nombre_presupuesto", it) }
        }
        startActivity(intent)
    }
}

data class ContactoTelefono(
    val nombre: String,
    val email: String,
    var tieneCrystal: Boolean
)