package crystal.crystal.red

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import crystal.crystal.databinding.ActivityListChatBinding
import crystal.crystal.red.data.ChatDirectoryRepository
import crystal.crystal.red.data.ChatConversationRepository
import crystal.crystal.red.data.ChatListRepository
import crystal.crystal.red.interop.ChatInteropIntents
import crystal.crystal.red.interop.ChatPlatform
import crystal.crystal.registro.UserProfileActivity
import crystal.crystal.registro.UserProfileRepository
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class ListChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListChatBinding
    private lateinit var currentUserId: String
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val chatDirectoryRepository = ChatDirectoryRepository(db)
    private val chatListRepository = ChatListRepository(db)
    private val chatConversationRepository = ChatConversationRepository(db, chatListRepository)
    private lateinit var profileRepository: UserProfileRepository
    private var chatsListener: ListenerRegistration? = null
    private var screenInitialized = false
    private var profileFlowOpened = false
    private var notificationBaselineReady = false
    private val notifiedMessageKeys = mutableSetOf<String>()
    private val chatCachePrefs by lazy { getSharedPreferences("chat_list_cache", MODE_PRIVATE) }

    private var presupuestoParaEnviar: String? = null
    private var nombrePresupuesto: String? = null
    private var archivoCompartidoParaEnviar: String? = null
    private var nombreArchivoCompartido: String? = null
    private var mimeArchivoCompartido: String? = null
    private var textoCompartidoParaEnviar: String? = null

    // Listas
    private var chatsActivos = mutableListOf<Chat>()
    private var todosLosContactos = mutableListOf<ContactoTelefono>()
    private val CONTACTS_PERMISSION_CODE = 100
    private val NOTIFICATIONS_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        profileRepository = UserProfileRepository(this)

        presupuestoParaEnviar = intent.getStringExtra(ChatInteropIntents.EXTRA_SEND_BUDGET_URI)
        nombrePresupuesto = intent.getStringExtra(ChatInteropIntents.EXTRA_BUDGET_NAME)
        archivoCompartidoParaEnviar = intent.getStringExtra(ChatInteropIntents.EXTRA_SEND_SHARED_URI)
        nombreArchivoCompartido = intent.getStringExtra(ChatInteropIntents.EXTRA_SEND_SHARED_NAME)
        mimeArchivoCompartido = intent.getStringExtra(ChatInteropIntents.EXTRA_SEND_SHARED_MIME)
        textoCompartidoParaEnviar = intent.getStringExtra(ChatInteropIntents.EXTRA_SEND_SHARED_TEXT)

        if (presupuestoParaEnviar != null) {
            Toast.makeText(this, "Selecciona un chat para enviar: $nombrePresupuesto", Toast.LENGTH_LONG).show()
            supportActionBar?.subtitle = "Enviando: $nombrePresupuesto"
        } else if (archivoCompartidoParaEnviar != null) {
            Toast.makeText(this, "Selecciona un chat para enviar: $nombreArchivoCompartido", Toast.LENGTH_LONG).show()
            supportActionBar?.subtitle = "Enviando archivo: $nombreArchivoCompartido"
        } else if (textoCompartidoParaEnviar != null) {
            Toast.makeText(this, "Selecciona un chat para enviar las medidas", Toast.LENGTH_LONG).show()
            supportActionBar?.subtitle = "Enviando medidas"
        }

        currentUserId = ChatIdentity.resolveChatIdentityUid(
            context = this,
            auth = auth,
            preferredUid = intent.getStringExtra("usuario")
        )

        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "Error: No se pudo identificar usuario", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        verificarPerfilYContinuar()
    }

    private val MENU_PEDIDOS = 90201

    override fun onCreateOptionsMenu(menu: android.view.Menu): Boolean {
        menu.add(0, MENU_PEDIDOS, 0, "🛒 Pedidos")
            .setShowAsAction(android.view.MenuItem.SHOW_AS_ACTION_NEVER)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == MENU_PEDIDOS) {
            startActivity(
                android.content.Intent(this, PedidosActivity::class.java)
                    .putExtra(PedidosActivity.EXTRA_UID, currentUserId)
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (!screenInitialized && profileFlowOpened) {
            verificarPerfilYContinuar()
        } else if (screenInitialized) {
            cargarChats()
        }
    }

    private fun verificarPerfilYContinuar() {
        lifecycleScope.launch {
            val hasProfile = try {
                profileRepository.hasCompleteProfile()
            } catch (e: Exception) {
                Toast.makeText(
                    this@ListChatActivity,
                    "Error verificando perfil: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                false
            }

            if (!hasProfile) {
                if (profileFlowOpened) {
                    Toast.makeText(
                        this@ListChatActivity,
                        "Debes completar tu perfil antes de usar el chat",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                } else {
                    profileFlowOpened = true
                    Toast.makeText(
                        this@ListChatActivity,
                        "Completa tu perfil para poder ser encontrado en el chat",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(this@ListChatActivity, UserProfileActivity::class.java))
                }
                return@launch
            }

            if (!screenInitialized) {
                iniciarPantallaChat()
            }
        }
    }

    private fun iniciarPantallaChat() {
        screenInitialized = true
        asegurarSelfChat()
        setupTabs()
        setupRecycler()
        mostrarChatsCacheados()
        configurarAccionesBarra()
        configurarMigracionManual()
        ejecutarMigracionAutomatica()
        binding.btnEnviar.setOnClickListener { buscarUsuario() }
        verificarPermisoNotificaciones()
        CrystalFcmTokenManager.registerCurrentDevice(this, currentUserId)
        CrystalFcmTokenManager.flushPendingToken(this, currentUserId)
        verificarPermisoContactos()
    }

    // Garantiza que exista el chat "Mensajes guardados" (chat consigo mismo), siempre visible.
    private fun asegurarSelfChat() {
        val uid = currentUserId
        if (uid.isEmpty()) return
        db.collection("chats")
            .whereArrayContains("users", uid)
            .get()
            .addOnSuccessListener { snap ->
                val existe = snap.documents.any { d ->
                    val users = (d.get("users") as? List<*>)?.mapNotNull { it?.toString() } ?: emptyList()
                    d.getBoolean("esSoporte") != true && users.isNotEmpty() && users.all { it == uid }
                }
                if (!existe) {
                    val chatId = db.collection("chats").document().id
                    db.collection("chats").document(chatId).set(
                        mapOf(
                            "id" to chatId,
                            "name" to "Mensajes guardados",
                            "users" to listOf(uid, uid),
                            "participantsKey" to uid,
                            "lastMsgDate" to com.google.firebase.Timestamp.now(),
                            "lastMessagePreview" to "",
                            "lastMessageType" to "text",
                            "unreadBy" to mapOf(uid to 0)
                        )
                    )
                }
            }
    }

    private fun configurarAccionesBarra() {
        binding.imageView3.setOnClickListener {
            binding.tabLayout.getTabAt(2)?.select()
            mostrarBusqueda()
            binding.etNuevoMsm.requestFocus()
            Toast.makeText(this, "Busca por nombre o email para crear un chat", Toast.LENGTH_SHORT).show()
        }

        binding.imageView5.setOnClickListener {
            cargarChats()
            if (binding.tabLayout.selectedTabPosition == 1) {
                cargarContactosDelTelefono()
            }
            Toast.makeText(this, "Lista actualizada", Toast.LENGTH_SHORT).show()
        }

        binding.imageView6.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }
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

    private fun configurarMigracionManual() {
        binding.linearLayout4.setOnLongClickListener {
            mostrarDialogoMigracionManual()
            true
        }
    }

    private fun ejecutarMigracionAutomatica() {
        lifecycleScope.launch {
            try {
                val aliases = ChatIdentity.resolveChatIdentityAliases(this@ListChatActivity, auth, currentUserId)
                ChatMigrationManager.migrateChatsForUser(
                    db = db,
                    stableUid = currentUserId,
                    knownAliases = aliases
                )
            } catch (e: Exception) {
                Log.e("ListChat", "Error migrando chats: ${e.message}", e)
            } finally {
                cargarChats()
            }
        }
    }

    private fun cargarChats() {
        val aliases = ChatIdentity.resolveChatIdentityAliases(this, auth, currentUserId)
        if (aliases.isEmpty()) {
            chatsActivos.clear()
            (binding.rvChatList.adapter as ChatAdapter).setData(emptyList())
            return
        }

        chatsListener?.remove()
        chatsListener = chatListRepository.observeChats(
            // Identidad de chat (para una terminal = uid del patrón), así ve la bandeja del patrón.
            // Las reglas lo permiten vía el custom claim patronUid. Para usuarios normales == auth uid.
            queryUserId = currentUserId,
            currentUserId = currentUserId,
            aliases = aliases,
            onResult = { chats ->
                runOnUiThread {
                    val anteriores = chatsActivos.associateBy { it.id }
                    chatsActivos = mergeChatRows(chatsActivos, chats).toMutableList()
                    notificarMensajesNuevos(anteriores, chatsActivos)
                    guardarChatsEnCache(chatsActivos)
                    (binding.rvChatList.adapter as ChatAdapter).setData(chatsActivos)
                }
            },
            onError = { e ->
                runOnUiThread {
                    Log.e("ListChat", "Error: ${e.message}", e)
                }
            }
        )
    }

    private fun mergeChatRows(current: List<Chat>, incoming: List<Chat>): List<Chat> {
        if (current.isEmpty()) return incoming

        val currentById = current.associateBy { it.id }
        return incoming.map { fresh ->
            val cached = currentById[fresh.id] ?: return@map fresh
            val shouldKeepCachedIdentity = cached.name.isNotBlank() && fresh.photoUrl.isBlank()
            val shouldKeepCachedUnread = fresh.unreadCount == 0 &&
                cached.unreadCount > 0 &&
                fresh.photoUrl.isBlank()
            Chat(
                id = fresh.id,
                name = if (shouldKeepCachedIdentity) cached.name else fresh.name.ifBlank { cached.name },
                users = if (fresh.users.isNotEmpty()) fresh.users else cached.users,
                peerPlatform = fresh.peerPlatform.ifBlank { cached.peerPlatform },
                participantsKey = fresh.participantsKey.ifBlank { cached.participantsKey },
                photoUrl = fresh.photoUrl.ifBlank { cached.photoUrl },
                lastMsgDate = fresh.lastMsgDate ?: cached.lastMsgDate,
                unreadCount = if (shouldKeepCachedUnread) cached.unreadCount else fresh.unreadCount,
                lastMessageText = fresh.lastMessageText.ifBlank { cached.lastMessageText }
            )
        }
    }

    private fun mostrarChatsCacheados() {
        val raw = chatCachePrefs.getString(cacheKey(), null) ?: return
        try {
            val json = JSONArray(raw)
            val cached = mutableListOf<Chat>()
            for (i in 0 until json.length()) {
                val item = json.optJSONObject(i) ?: continue
                cached += Chat(
                    id = item.optString("id"),
                    name = item.optString("name"),
                    users = item.optJSONArray("users")?.let { arr ->
                        buildList {
                            for (index in 0 until arr.length()) {
                                add(arr.optString(index))
                            }
                        }
                    } ?: emptyList(),
                    peerPlatform = item.optString("peerPlatform", ChatPlatform.CRYSTAL.wireValue),
                    participantsKey = item.optString("participantsKey"),
                    photoUrl = item.optString("photoUrl"),
                    unreadCount = item.optInt("unreadCount", 0),
                    lastMessageText = item.optString("lastMessageText")
                )
            }
            if (cached.isNotEmpty()) {
                chatsActivos = cached.toMutableList()
                (binding.rvChatList.adapter as ChatAdapter).setData(chatsActivos)
            }
        } catch (e: Exception) {
            Log.e("ListChat", "Error leyendo cache de chats", e)
        }
    }

    private fun guardarChatsEnCache(chats: List<Chat>) {
        try {
            val json = JSONArray()
            chats.forEach { chat ->
                json.put(JSONObject().apply {
                    put("id", chat.id)
                    put("name", chat.name)
                    put("peerPlatform", chat.peerPlatform)
                    put("participantsKey", chat.participantsKey)
                    put("photoUrl", chat.photoUrl)
                    put("unreadCount", chat.unreadCount)
                    put("lastMessageText", chat.lastMessageText)
                    put("users", JSONArray(chat.users))
                })
            }
            chatCachePrefs.edit().putString(cacheKey(), json.toString()).apply()
        } catch (e: Exception) {
            Log.e("ListChat", "Error guardando cache de chats", e)
        }
    }

    private fun cacheKey(): String = "user_$currentUserId"

    private fun verificarPermisoNotificaciones() {
        CrystalMessageNotifier.ensureChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATIONS_PERMISSION_CODE
            )
        }
    }

    private fun notificarMensajesNuevos(anteriores: Map<String, Chat>, actuales: List<Chat>) {
        if (!notificationBaselineReady) {
            notificationBaselineReady = true
            actuales.forEach { chat ->
                val lastTime = chat.lastMsgDate?.time ?: return@forEach
                notifiedMessageKeys.add("${chat.id}:$lastTime:${chat.unreadCount}")
            }
            return
        }

        actuales.forEach { chat ->
            if (chat.unreadCount <= 0 || chat.id.isBlank()) return@forEach
            val lastTime = chat.lastMsgDate?.time ?: return@forEach
            val anterior = anteriores[chat.id]
            val previousTime = anterior?.lastMsgDate?.time ?: 0L
            val previousUnread = anterior?.unreadCount ?: 0
            val isNewer = lastTime > previousTime
            val unreadIncreased = chat.unreadCount > previousUnread
            if (!isNewer && !unreadIncreased) return@forEach

            val key = "${chat.id}:$lastTime:${chat.unreadCount}"
            if (!notifiedMessageKeys.add(key)) return@forEach

            CrystalMessageNotifier.notifyMessage(
                context = this,
                chatId = chat.id,
                currentUserId = currentUserId,
                senderName = chat.name,
                preview = chat.lastMessageText,
                unreadCount = chat.unreadCount
            )
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
        when (requestCode) {
            CONTACTS_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cargarContactosDelTelefono()
                } else {
                    Toast.makeText(this, "Permiso de contactos denegado", Toast.LENGTH_SHORT).show()
                }
            }
            NOTIFICATIONS_PERMISSION_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Las notificaciones de Crystal estan desactivadas", Toast.LENGTH_LONG).show()
                }
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
        lifecycleScope.launch {
            try {
                todosLosContactos = chatDirectoryRepository.enrichPhoneContacts(todosLosContactos).toMutableList()
                Log.d("ListChat", "Contactos enriquecidos: ${todosLosContactos.size}")
                if (binding.tabLayout.selectedTabPosition == 1) {
                    mostrarContactos()
                }
            } catch (e: Exception) {
                Log.e("ListChat", "Error enriqueciendo contactos: ${e.message}", e)
            }
        }
    }

    private fun mostrarChats() {
        binding.linearLayout3.visibility = android.view.View.GONE
        binding.etNuevoMsm.visibility = android.view.View.GONE
        binding.btnEnviar.visibility = android.view.View.GONE
        (binding.rvChatList.adapter as ChatAdapter).setData(chatsActivos)
    }

    private fun mostrarContactos() {
        binding.linearLayout3.visibility = android.view.View.GONE
        binding.etNuevoMsm.visibility = android.view.View.GONE
        binding.btnEnviar.visibility = android.view.View.GONE

        Log.d("ListChat", "Mostrando ${todosLosContactos.size} contactos")

        // Convertir a Chat para reusar ChatAdapter
        val chatsDeContactos = todosLosContactos.map { contacto ->
            Chat(
                id = if (contacto.tieneCrystal) "" else "invitar",
                name = contacto.nombre,
                users = listOf(currentUserId),
                peerPlatform = contacto.platform,
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
        binding.linearLayout3.visibility = android.view.View.VISIBLE
        binding.etNuevoMsm.visibility = android.view.View.VISIBLE
        binding.btnEnviar.visibility = android.view.View.VISIBLE
        binding.etNuevoMsm.hint = "Buscar por nombre o email"
    }

    private fun buscarUsuario() {
        val termino = binding.etNuevoMsm.text.toString().trim()
        if (termino.isEmpty()) {
            Toast.makeText(this, "Ingresa un nombre o email", Toast.LENGTH_SHORT).show()
            return
        }

        if (!termino.contains("@")) {
            buscarPorNombre(termino)
            return
        }

        val email = termino.lowercase()

        if (!termino.contains("@")) {
            Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val user = chatDirectoryRepository.findByEmail(email)
            if (user == null) {
                Toast.makeText(this@ListChatActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            } else if (user.uid == currentUserId) {
                Toast.makeText(this@ListChatActivity, "No puedes chatear contigo mismo", Toast.LENGTH_SHORT).show()
            } else {
                buscarOCrearChat(user.uid)
            }
        }
    }

    private fun buscarPorNombre(termino: String) {
        lifecycleScope.launch {
            val usuarioEncontrado = chatDirectoryRepository.searchByNameOrEmail(
                term = ChatIdentity.normalizeSearchText(termino),
                excludeUid = currentUserId
            )

            if (usuarioEncontrado == null) {
                Toast.makeText(this@ListChatActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            } else {
                buscarOCrearChat(usuarioEncontrado.uid)
            }
        }
    }

    private fun buscarOCrearChat(otherUid: String) {
        val aliases = ChatIdentity.resolveChatIdentityAliases(this, auth, currentUserId)
        lifecycleScope.launch {
            try {
                val otherUser = chatDirectoryRepository.getUser(otherUid)
                if (otherUser == null) {
                    Toast.makeText(this@ListChatActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val chatId = chatConversationRepository.getOrCreateDirectChat(
                    // Identidad de chat (terminal = patrón) para operar sobre la bandeja del patrón.
                    queryUserId = currentUserId,
                    currentUserId = currentUserId,
                    aliases = aliases,
                    otherUser = otherUser
                )
                abrirChatActivity(chatId)
            } catch (e: Exception) {
                Toast.makeText(this@ListChatActivity, "Error buscando chat: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
        lifecycleScope.launch {
            chatDirectoryRepository.findByEmail(email)?.let { user ->
                buscarOCrearChat(user.uid)
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

    private fun abrirChatActivity(chatId: String) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("chatId", chatId)
            putExtra("usuario", currentUserId)
            presupuestoParaEnviar?.let { putExtra(ChatInteropIntents.EXTRA_SEND_BUDGET_URI, it) }
            nombrePresupuesto?.let { putExtra(ChatInteropIntents.EXTRA_BUDGET_NAME, it) }
            archivoCompartidoParaEnviar?.let { putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_URI, it) }
            nombreArchivoCompartido?.let { putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_NAME, it) }
            mimeArchivoCompartido?.let { putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_MIME, it) }
            textoCompartidoParaEnviar?.let { putExtra(ChatInteropIntents.EXTRA_SEND_SHARED_TEXT, it) }
        }
        startActivity(intent)
    }

    private fun mostrarDialogoMigracionManual() {
        val input = android.widget.EditText(this).apply {
            hint = "UIDs antiguos separados por coma"
        }

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Migrar chats antiguos")
            .setMessage("Pega aquí los UIDs antiguos que quieres fusionar con esta cuenta.")
            .setView(input)
            .setPositiveButton("Migrar") { _, _ ->
                val legacyUids = input.text.toString()
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotBlank() }

                if (legacyUids.isEmpty()) {
                    Toast.makeText(this, "No ingresaste ningún UID", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    try {
                        val aliases = ChatIdentity.resolveChatIdentityAliases(this@ListChatActivity, auth, currentUserId)
                        val result = ChatMigrationManager.migrateChatsForUser(
                            db = db,
                            stableUid = currentUserId,
                            knownAliases = aliases,
                            explicitLegacyUids = legacyUids
                        )
                        Toast.makeText(
                            this@ListChatActivity,
                            "Chats actualizados: ${result.updatedChats}, mensajes: ${result.updatedMessages}",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@ListChatActivity,
                            "Error migrando: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    } finally {
                        cargarChats()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onDestroy() {
        chatsListener?.remove()
        super.onDestroy()
    }
}

data class ContactoTelefono(
    val nombre: String,
    val email: String,
    var tieneCrystal: Boolean,
    var platform: String = ChatPlatform.CRYSTAL.wireValue
)
