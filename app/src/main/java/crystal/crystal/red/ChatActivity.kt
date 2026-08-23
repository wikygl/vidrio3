package crystal.crystal.red
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.format.DateFormat
import android.widget.EditText
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.iceteck.silicompressorr.SiliCompressor
import crystal.crystal.R
import crystal.crystal.MainActivity
import crystal.crystal.databinding.ActivityChatBinding
import crystal.crystal.red.ChatUserDocReader
import crystal.crystal.red.data.ChatMessageRepository
import crystal.crystal.red.data.ChatSendRepository
import crystal.crystal.red.interop.ChatInteropDocumentFactory
import crystal.crystal.red.interop.ChatInteropIntents
import crystal.crystal.red.interop.ChatPlatform
import crystal.crystal.red.interop.MeasuresMessageCodec
import crystal.crystal.optimizadores.corte.CorteActivity
import crystal.crystal.optimizadores.planchas.OptimizacionPlanchasActivity
import crystal.crystal.taller.MedidaActivity
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.net.URL
import java.io.ByteArrayOutputStream
import java.io.File
import androidx.core.content.FileProvider
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private val chatMessageRepository = ChatMessageRepository(db)
    private val chatSendRepository = ChatSendRepository(db)

    private lateinit var mensajesListener: ListenerRegistration
    private lateinit var presenciaListener: ListenerRegistration

    private var chatId = ""
    private var usuario = ""
    private var messageActorUid = ""
    private var userAliases: List<String> = emptyList()
    private var peerPlatform: ChatPlatform = ChatPlatform.CRYSTAL
    private var peerExternalUserId: String = ""
    private lateinit var adapter: MessageAdapter

    private var presupuestoParaEnviar: String? = null
    private var nombrePresupuesto: String? = null
    private var archivoCompartidoParaEnviar: Uri? = null
    private var nombreArchivoCompartido: String? = null
    private var mimeArchivoCompartido: String? = null
    private var textoCompartidoParaEnviar: String? = null

    companion object {
        private const val PICK_FILE_REQ = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra("chatId")?.let { chatId = it }
        usuario = ChatIdentity.resolveChatIdentityUid(
            context = this,
            auth = auth,
            preferredUid = intent.getStringExtra("usuario")
        )
        messageActorUid = auth.currentUser?.uid?.takeIf { it.isNotBlank() } ?: usuario
        userAliases = ChatIdentity.resolveChatIdentityAliases(
            context = this,
            auth = auth,
            preferredUid = intent.getStringExtra("usuario")
        )

        intent.getStringExtra(ChatInteropIntents.EXTRA_SEND_BUDGET_URI)?.let { presupuestoParaEnviar = it }
        intent.getStringExtra(ChatInteropIntents.EXTRA_BUDGET_NAME)?.let { nombrePresupuesto = it }
        archivoCompartidoParaEnviar = ChatInteropIntents.consumeUriExtra(intent, ChatInteropIntents.EXTRA_SEND_SHARED_URI)
        nombreArchivoCompartido = intent.getStringExtra(ChatInteropIntents.EXTRA_SEND_SHARED_NAME)
        mimeArchivoCompartido = intent.getStringExtra(ChatInteropIntents.EXTRA_SEND_SHARED_MIME)
        textoCompartidoParaEnviar = intent.getStringExtra(ChatInteropIntents.EXTRA_SEND_SHARED_TEXT)

        if (chatId.isNotEmpty() && usuario.isNotEmpty()) {
            inicializarCabecera()
            configurarRecycler()
        binding.btEnviar.setOnClickListener { onClickEnviarTexto() }
        binding.btArchivo.setOnClickListener { seleccionarArchivo() }
        cargarChat()

            // Si hay un presupuesto para enviar, mostrarlo
            if (presupuestoParaEnviar != null) {
                mostrarOpcionEnviarPresupuesto()
            } else if (archivoCompartidoParaEnviar != null) {
                mostrarOpcionEnviarArchivoCompartido()
            } else if (textoCompartidoParaEnviar != null) {
                mostrarOpcionEnviarTextoCompartido()
            }
        }
    }

    // FUNCIONES PARA COMPARTIR PRESUPUESTO

    // Agregar función para mostrar opción de enviar presupuesto:
    private fun mostrarOpcionEnviarPresupuesto() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enviar Presupuesto")
        builder.setMessage("¿Deseas enviar el presupuesto '$nombrePresupuesto' en este chat?")
        builder.setPositiveButton("Enviar") { _, _ ->
            enviarPresupuesto()
        }
        builder.setNegativeButton("Cancelar") { _, _ ->
            presupuestoParaEnviar = null
            nombrePresupuesto = null
        }
        builder.show()
    }

    // Función para enviar presupuesto:
    private fun enviarPresupuesto() {
        // Candado (Fase 3): enviar presupuesto por el chat es de pago.
        if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeEnviarFormato(),
                "Enviar presupuestos por el chat es una función de pago.")) return
        presupuestoParaEnviar?.let { uriString ->
            val uri = Uri.parse(uriString)
            subirYEnviarPresupuesto(uri)
            presupuestoParaEnviar = null
            nombrePresupuesto = null
        }
    }

    private fun mostrarOpcionEnviarArchivoCompartido() {
        val nombre = nombreArchivoCompartido ?: "archivo"
        AlertDialog.Builder(this)
            .setTitle("Enviar archivo")
            .setMessage("¿Deseas enviar '$nombre' en este chat?")
            .setPositiveButton("Enviar") { _, _ ->
                enviarArchivoCompartido()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                archivoCompartidoParaEnviar = null
                nombreArchivoCompartido = null
                mimeArchivoCompartido = null
            }
            .show()
    }

    private fun enviarArchivoCompartido() {
        // Candado (Fase 3): enviar archivos por el chat es de pago.
        if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeEnviarFormato(),
                "Enviar archivos por el chat es una función de pago.")) return
        archivoCompartidoParaEnviar?.let { uri ->
            subirYEnviar(uri)
            archivoCompartidoParaEnviar = null
            nombreArchivoCompartido = null
            mimeArchivoCompartido = null
        }
    }

    private fun mostrarOpcionEnviarTextoCompartido() {
        val texto = textoCompartidoParaEnviar.orEmpty()
        val vistaPrevia = texto.lineSequence().take(8).joinToString("\n")
        AlertDialog.Builder(this)
            .setTitle("Enviar medidas")
            .setMessage("Deseas enviar estas medidas en este chat?\n\n$vistaPrevia")
            .setPositiveButton("Enviar") { _, _ ->
                enviarTextoCompartido()
            }
            .setNegativeButton("Cancelar") { _, _ ->
                textoCompartidoParaEnviar = null
            }
            .show()
    }

    private fun enviarTextoCompartido() {
        val texto = textoCompartidoParaEnviar?.trim().orEmpty()
        if (texto.isNotEmpty()) {
            enviarMensaje(texto, if (MeasuresMessageCodec.isMeasuresFormat(texto)) "medidas" else "texto")
        }
        textoCompartidoParaEnviar = null
    }

    private fun subirYEnviarPresupuesto(uri: Uri) {
        val ahora = Date()
        val budgetName = nombrePresupuesto ?: "presupuesto.json"
        chatSendRepository.createPendingMessage(
            chatId = chatId,
            fromUid = messageActorUid,
            placeholderText = "ENVIANDO PRESUPUESTO...",
            legacyType = "presupuesto",
            createdAt = ahora,
            fileName = budgetName,
            targetApp = peerPlatform,
            targetExternalUserId = peerExternalUserId,
            onSuccess = { messageRef, placeholder ->
                adapter.addMensajeTemporal(placeholder)
                binding.rvMensajes.scrollToPosition(adapter.itemCount - 1)

                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        val refStorage = storage.child("chat_files/$chatId/${messageRef.id}")
                        refStorage.putBytes(bytes)
                            .addOnSuccessListener {
                                refStorage.downloadUrl.addOnSuccessListener { url ->
                                    chatSendRepository.completeUploadedMessage(
                                        chatId = chatId,
                                        messageRef = messageRef,
                                        legacyType = "presupuesto",
                                        previewValue = budgetName,
                                        updatedAt = ahora,
                                        url = url.toString()
                                    )
                                    Toast.makeText(this, "Presupuesto enviado correctamente", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                chatSendRepository.failMessage(messageRef, "Error al enviar presupuesto")
                                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        chatSendRepository.failMessage(messageRef, "Error al leer archivo")
                        Toast.makeText(this, "No se pudo leer el archivo", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    chatSendRepository.failMessage(messageRef, "Error al procesar archivo")
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            },
            onError = { e ->
                Toast.makeText(this, "Error preparando mensaje: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Agregar función para detectar formato medidas (igual que las otras clases)
    private fun esFormatoMedidas(mensaje: String): Boolean {
        val lineas = mensaje.trim().split("\n").filter { it.isNotBlank() }

        if (lineas.size < 2) return false

        // Regex para detectar medidas: número x número = número (con decimales)
        val regexMedida = Regex("""^\s*\d+(\.\d+)?\s*[xX]\s*\d+(\.\d+)?\s*=\s*\d+(\.\d+)?\s*$""")

        // Buscar la primera línea que sea una medida
        var primeraMedida = -1
        for (i in lineas.indices) {
            if (regexMedida.matches(lineas[i].trim())) {
                primeraMedida = i
                break
            }
        }

        // Debe haber al menos una línea antes (producto) y debe encontrar medidas
        if (primeraMedida <= 0) return false

        // Desde la primera medida en adelante, TODAS deben ser medidas
        for (i in primeraMedida until lineas.size) {
            if (!regexMedida.matches(lineas[i].trim())) {
                return false
            }
        }

        return true
    }

    // En ChatActivity.kt, dentro de configurarRecycler()
    private fun configurarRecycler() {
        adapter = MessageAdapter(
            usuario,
            onEditar = { mostrarEditar(it) },
            onEliminar = { mostrarEliminar(it) },
            onMostrarArchivo = { mensaje ->
                val uri = Uri.parse(mensaje.message)
                // Los formatos Crystal se resuelven SIEMPRE por [abrirArchivoMensaje], que es la única
                // implementación (la usa también el menú de toque largo). Va antes del when porque el
                // tipo del mensaje depende de la versión que lo envió: un proyecto podía llegar como
                // "texto" o "archivo" y caía en el visor genérico con "Tipo no soportado".
                if (esMensajeCrystal(mensaje)) {
                    abrirArchivoMensaje(mensaje)
                    return@MessageAdapter
                }
                when (mensaje.tipo) {
                    "presupuesto" -> {
                        // Descargar contenido y abrir con MainActivity
                        Toast.makeText(this, "Descargando presupuesto...", Toast.LENGTH_SHORT).show()

                        // Descargar el archivo JSON desde Firebase Storage
                        if (!isFirebaseStorageUrl(mensaje.message)) {
                            Toast.makeText(this, "El presupuesto aun no tiene una URL valida", Toast.LENGTH_SHORT).show()
                            return@MessageAdapter
                        }
                        val httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(mensaje.message)
                        httpsReference.getBytes(Long.MAX_VALUE)
                            .addOnSuccessListener { bytes ->
                                try {
                                    val jsonContent = String(bytes, Charsets.UTF_8)

                                    // Crear Intent para MainActivity
                                    val intent = Intent(this, crystal.crystal.MainActivity::class.java).apply {
                                        putExtra(ChatInteropIntents.EXTRA_LOAD_BUDGET_JSON, jsonContent)
                                        putExtra(ChatInteropIntents.EXTRA_LOAD_BUDGET_NAME, mensaje.nombreArchivo)
                                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    }

                                    startActivity(intent)

                                } catch (e: Exception) {
                                    Toast.makeText(this, "Error al procesar presupuesto: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Error al descargar: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    "texto", "medidas" -> mostrarDialogoImportarMedidas(mensaje)
                    "medidas_crystal" -> abrirMedidasCrystalDesdeChat(mensaje)
                    "corte_crystal" -> abrirCorteCrystalDesdeChat(mensaje)
                    "plancha_crystal" -> abrirPlanchaCrystalDesdeChat(mensaje)
                    "video" -> {
                        if (!isFirebaseStorageUrl(mensaje.message)) {
                            VisorArchivoActivity.abrir(this, uri, mensaje.tipo)
                            return@MessageAdapter
                        }

                        val ref = FirebaseStorage.getInstance().getReferenceFromUrl(mensaje.message)
                        ref.metadata
                            .addOnSuccessListener { meta ->
                                val tam = meta.sizeBytes
                                if (tam > 7L * 1024 * 1024) {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(uri, "video/*")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    startActivity(Intent.createChooser(intent, "Abrir con"))
                                } else {
                                    VisorArchivoActivity.abrir(this, uri, mensaje.tipo)
                                }
                            }
                            .addOnFailureListener {
                                val intent = Intent(Intent.ACTION_VIEW).apply {
                                    setDataAndType(uri, "video/*")
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                startActivity(Intent.createChooser(intent, "Abrir con"))
                            }
                    }
                    // Imagen, audio, pdf y cualquier archivo suelto: los resuelve abrirArchivoMensaje,
                    // que además intenta reconocer por contenido lo que no viene bien etiquetado.
                    else -> abrirArchivoMensaje(mensaje)
                }
            },
            onOpciones = { m, esMio -> mostrarOpcionesMensaje(m, esMio) }
        )
        binding.rvMensajes.layoutManager =
            LinearLayoutManager(this).apply { stackFromEnd = true }
        binding.rvMensajes.adapter = adapter
    }

    private fun inicializarCabecera() {
        val ivFoto = binding.root.findViewById<android.widget.ImageView?>(R.id.ivFoto) ?: return
        val tvNombre = binding.root.findViewById<android.widget.TextView?>(R.id.chatNameText) ?: return
        val tvEstado = binding.root.findViewById<android.widget.TextView?>(R.id.usersTextView) ?: return
        val btGoMain = binding.root.findViewById<android.widget.ImageButton?>(R.id.btGoMain)

        btGoMain?.setColorFilter(ContextCompat.getColor(this, R.color.azul))
        btGoMain?.setOnClickListener {
            startActivity(
                Intent(this, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
            )
        }

        db.collection("chats").document(chatId)
            .get()
            .addOnSuccessListener { doc ->
                peerPlatform = ChatPlatform.fromWireValue(doc.getString("peerPlatform"))
                peerExternalUserId = doc.getString("peerExternalUserId").orEmpty()
                if (doc.getBoolean("esSoporte") == true) {
                    tvNombre.text = "Soporte Crystal"
                    tvEstado.text = ""
                    ivFoto.setImageResource(R.drawable.ic_chckr)
                    return@addOnSuccessListener
                }
                val users = doc.get("users") as? List<*> ?: emptyList<Any>()
                val otroId = users.firstOrNull { it != usuario }?.toString()
                if (otroId == null) {
                    tvNombre.text = "Mensajes guardados"
                    tvEstado.text = ""
                    ivFoto.setImageResource(R.drawable.ic_chckr)
                    return@addOnSuccessListener
                }
                presenciaListener = db.collection("usuarios").document(otroId)
                    .addSnapshotListener { snap, e ->
                        if (e != null || snap == null || !snap.exists()) return@addSnapshotListener
                        tvNombre.text = snap.getString("nombre") ?: "—"
                        tvNombre.text = ChatUserDocReader.getName(snap) ?: tvNombre.text
                        ChatUserDocReader.getPhotoUrl(snap)?.let { url: String ->
                            Glide.with(this).load(url).circleCrop().into(ivFoto)
                        }
                        val online = snap.getBoolean("online") ?: false
                        tvEstado.text = if (online) "En línea"
                        else "Últ. visto: " +
                                (snap.getTimestamp("lastSeen")?.toDate()?.let {
                                    DateFormat.format("dd/MM/yyyy hh:mm a", it)
                                } ?: "Desconocido")
                    }
            }
    }

    private fun cargarChat() {
        mensajesListener = chatMessageRepository.observeMessages(
            chatId = chatId,
            currentUserId = messageActorUid,
            aliases = userAliases,
            onResult = { lista ->
                runOnUiThread {
                    adapter.setData(lista)
                    if (lista.isNotEmpty()) binding.rvMensajes.scrollToPosition(lista.size - 1)
                }
            },
            onError = { e ->
                runOnUiThread {
                    Toast.makeText(this, "Error cargando mensajes: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun onClickEnviarTexto() {
        val texto = binding.messageTextField.text.toString().trim()
        if (texto.isEmpty()) return
        enviarMensaje(texto, if (MeasuresMessageCodec.isMeasuresFormat(texto)) "medidas" else "texto")
    }

    private fun seleccionarArchivo() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "*/*"
        intent.putExtra(
            Intent.EXTRA_MIME_TYPES,
            arrayOf("image/*", "audio/*", "video/*", "application/pdf")
        )
        startActivityForResult(Intent.createChooser(intent, "Selecciona un archivo"), PICK_FILE_REQ)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQ && resultCode == Activity.RESULT_OK && data?.data != null) {
            val uri = data.data!!
            subirYEnviar(uri)
        }
    }

    private fun subirYEnviar(uri: Uri) {
        // Candado (Fase 3, red de seguridad): enviar archivos por el chat es de pago.
        if (!crystal.crystal.Suscripcion.exigir(this, crystal.crystal.Suscripcion.puedeEnviarFormato(),
                "Enviar archivos por el chat es una función de pago.")) return
        val chatRef = db.collection("chats").document(chatId)
        val mensajesRef = chatRef.collection("messages")
        val nuevoMensajeRef = mensajesRef.document()
        val ahora = Date()

        val nombreOriginal = nombreArchivoCompartido ?: extraerNombreArchivo(uri)
        val tipo = detectarTipo(uri, mimeArchivoCompartido, nombreOriginal)

        // 1) Crear placeholder en Firestore
        val placeholder = Message(
            id = nuevoMensajeRef.id,
            message = if (tipo == "texto") "" else "CARGANDO... 0%",
            from = messageActorUid,
            dob = ahora,
            leido = false,
            entregado = false,
            tipo = tipo,
            nombreArchivo = nombreOriginal
        ).apply { hasPendingWrites = true }

        adapter.addMensajeTemporal(placeholder)
        binding.rvMensajes.scrollToPosition(adapter.itemCount - 1)

        nuevoMensajeRef.set(
                ChatInteropDocumentFactory.createMessage(
                    id = placeholder.id,
                    fromUid = messageActorUid,
                    message = placeholder.message,
                    legacyType = tipo,
                    createdAt = placeholder.dob,
                    targetApp = peerPlatform,
                    targetExternalUserId = peerExternalUserId,
                    syncStatus = if (peerPlatform == ChatPlatform.PUNTOS && peerExternalUserId.isNotBlank()) "pending_external" else "local",
                    fileName = nombreOriginal
                ) + mapOf(
                "leido" to false,
                "entregado" to false,
                "deletedFor" to placeholder.deletedFor,
                "deletedForEveryone" to placeholder.deletedForEveryone
            )
        )

        // 2) Prepara la subida
        val refStorage = storage.child("chat_files/$chatId/${nuevoMensajeRef.id}")

        // Lambda que realiza la subida y actualiza progreso y URL
        fun realizarSubida(uploadUri: Uri) {
            val uploadTask = refStorage.putFile(uploadUri)
            // progreso
            uploadTask.addOnProgressListener { snap ->
                val bytes = snap.bytesTransferred
                val total = snap.totalByteCount
                if (total > 0) {
                    val pct = (100 * bytes / total).toInt()
                    chatSendRepository.updateUploadProgress(nuevoMensajeRef, pct)
                }
            }
            // al terminar
            uploadTask.addOnSuccessListener {
                refStorage.downloadUrl.addOnSuccessListener { url ->
                    chatSendRepository.completeUploadedMessage(
                        chatId = chatId,
                        messageRef = nuevoMensajeRef,
                        legacyType = tipo,
                        previewValue = nombreOriginal,
                        updatedAt = ahora,
                        url = url.toString()
                    )
                }
            }.addOnFailureListener { e ->
                chatSendRepository.failMessage(nuevoMensajeRef, "Error al subir")
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // 3) Tratamiento según tipo
        when (tipo) {
            "imagen" -> {
                // comprimir imagen
                val data = comprimirImagen(uri)
                refStorage.putBytes(data)
                    .addOnSuccessListener {
                        refStorage.downloadUrl.addOnSuccessListener { url ->
                            chatSendRepository.completeUploadedMessage(
                                chatId = chatId,
                                messageRef = nuevoMensajeRef,
                                legacyType = tipo,
                                previewValue = nombreOriginal,
                                updatedAt = ahora,
                                url = url.toString()
                            )
                        }
                    }
                    .addOnFailureListener { e ->
                        chatSendRepository.failMessage(nuevoMensajeRef, "Error al subir imagen")
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            "video" -> {
                realizarSubida(uri)
            }
            else -> {
                // audio, pdf u otros
                realizarSubida(uri)
            }
        }
    }

    private fun comprimirImagen(uri: Uri): ByteArray {
        val input = contentResolver.openInputStream(uri)
        val bmp = BitmapFactory.decodeStream(input)
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        return baos.toByteArray()
    }

    private fun comprimirVideo(uriOriginal: Uri, callback: (Uri?) -> Unit) {
        Thread {
            try {
                val cache = applicationContext.cacheDir
                val outDir = File(cache, "videos_comprimidos")
                if (!outDir.exists()) outDir.mkdirs()
                // ruta de video comprimido
                val compressedPath = SiliCompressor.with(this)
                    .compressVideo(uriOriginal, outDir.path)
                val resultUri = Uri.fromFile(File(compressedPath))
                runOnUiThread { callback(resultUri) }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread { callback(null) }
            }
        }.start()
    }

    private fun detectarTipo(uri: Uri, forcedMime: String? = null, forcedFileName: String? = null): String {
        val t = forcedMime ?: contentResolver.getType(uri).orEmpty()
        val fileName = forcedFileName ?: extraerNombreArchivo(uri)

        return when {
            t == OptimizacionPlanchasActivity.MIME_PLANCHA_CRYSTAL ||
                fileName.endsWith(".${OptimizacionPlanchasActivity.EXTENSION_PLANCHA_CRYSTAL}", ignoreCase = true) -> "plancha_crystal"
            t == CorteActivity.MIME_CORTE_CRYSTAL ||
                fileName.endsWith(".${CorteActivity.EXTENSION_CORTE_CRYSTAL}", ignoreCase = true) -> "corte_crystal"
            t == MedidaActivity.MIME_MEDIDAS_CRYSTAL ||
                fileName.endsWith(".${MedidaActivity.EXTENSION_MEDIDAS_CRYSTAL}", ignoreCase = true) -> "medidas_crystal"
            t == crystal.crystal.taller.Taller.MIME_PROYECTO_CRYSTAL ||
                fileName.endsWith(".${crystal.crystal.taller.Taller.EXTENSION_PROYECTO_CRYSTAL}", ignoreCase = true) -> "proyecto_crystal"
            fileName.startsWith("presupuesto_") && fileName.endsWith(".json") -> "presupuesto"
            t.startsWith("image")   -> "imagen"
            t.startsWith("video")   -> "video"
            t.startsWith("audio")   -> "audio"
            t == "application/json" -> "presupuesto"
            t == "application/pdf"  -> "pdf"
            else                    -> "texto"
        }
    }

    private fun enviarMensaje(texto: String, tipo: String = "texto") {
        // Candado (Fase 3): el texto libre es gratis; enviar formatos (presupuesto/medidas/archivo) es
        // de pago. El candado duro está en las reglas de Firestore; esto es el aviso/UX en el cliente.
        if (tipo != "texto" && !crystal.crystal.Suscripcion.exigir(
                this,
                crystal.crystal.Suscripcion.puedeEnviarFormato(),
                "Enviar presupuestos/medidas por el chat es una función de pago."
            )
        ) return
        val ahora   = Date()
        chatSendRepository.sendTextMessage(
            chatId = chatId,
            fromUid = messageActorUid,
            text = texto,
            legacyType = tipo,
            createdAt = ahora,
            targetApp = peerPlatform,
            targetExternalUserId = peerExternalUserId,
            onSuccess = { msg ->
                adapter.addMensajeTemporal(msg)
                binding.rvMensajes.scrollToPosition(adapter.itemCount - 1)
                binding.messageTextField.setText("")
            },
            onError = { e ->
                Toast.makeText(this, "Error enviando mensaje: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun mostrarDialogoImportarMedidas(mensaje: Message) {
        val parsedMeasures = MeasuresMessageCodec.parse(mensaje.message)
        if (parsedMeasures == null) {
            if (mensaje.tipo == "texto") {
                Toast.makeText(this, "Mensaje de texto normal", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Lista de Medidas")
        builder.setMessage(
            "Producto: ${parsedMeasures.productName}\n" +
                "Elementos detectados: ${parsedMeasures.items.size}\n\n" +
                "Deseas importar estas medidas a tu presupuesto?"
        )
        builder.setPositiveButton("Importar") { _, _ ->
            val intent = Intent(this, crystal.crystal.MainActivity::class.java).apply {
                putExtra(ChatInteropIntents.EXTRA_IMPORT_MEASURES_TEXT, mensaje.message)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            startActivity(intent)
            Toast.makeText(this, "Abriendo calculadora para importar...", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun esMensajeMedidasCrystal(mensaje: Message): Boolean {
        return mensaje.tipo == "medidas_crystal" ||
            mensaje.nombreArchivo.endsWith(".${MedidaActivity.EXTENSION_MEDIDAS_CRYSTAL}", ignoreCase = true)
    }

    private fun esMensajeCorteCrystal(mensaje: Message): Boolean {
        return mensaje.tipo == "corte_crystal" ||
            mensaje.nombreArchivo.endsWith(".${CorteActivity.EXTENSION_CORTE_CRYSTAL}", ignoreCase = true)
    }

    private fun esMensajePlanchaCrystal(mensaje: Message): Boolean {
        return mensaje.tipo == "plancha_crystal" ||
            mensaje.nombreArchivo.endsWith(".${OptimizacionPlanchasActivity.EXTENSION_PLANCHA_CRYSTAL}", ignoreCase = true)
    }

    // ====== Atajo: usar un comprobante recibido por chat para la recarga en curso ======
    private fun esImagenComprobante(mensaje: Message): Boolean {
        val t = mensaje.tipo.lowercase()
        if (t == "imagen" || t == "image") return true
        val n = mensaje.nombreArchivo.lowercase()
        return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png") || n.endsWith(".webp")
    }

    private fun mostrarOpcionesImagenComprobante(mensaje: Message, uri: Uri) {
        // Sin canal de cobro en la app (build de Play) la imagen solo se ve: no hay recarga a la
        // que adjuntarla, así que ofrecerlo sería una opción muerta.
        if (!crystal.crystal.pagos.CanalPagos.DISPONIBLE_EN_LA_APP) {
            VisorArchivoActivity.abrir(this, uri, mensaje.tipo); return
        }
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setItems(arrayOf("👁️ Ver imagen", "💳 Usar para mi recarga")) { _, w ->
                if (w == 0) VisorArchivoActivity.abrir(this, uri, mensaje.tipo)
                else crystal.crystal.pagos.CanalPagos.usarComprobanteParaRecarga(this, mensaje.message)
            }
            .show()
    }

    // ===== Menú de opciones al mantener presionado un mensaje =====
    private fun mostrarOpcionesMensaje(mensaje: Message, esMio: Boolean) {
        val opciones = mutableListOf<Pair<String, () -> Unit>>()
        when {
            mensaje.tipo == "texto" -> {
                opciones += "📋 Copiar texto" to { copiarTexto(mensaje.message) }
                opciones += "↗️ Compartir" to { compartirTexto(mensaje.message) }
            }
            esImagenComprobante(mensaje) -> {
                opciones += "👁️ Ver" to { VisorArchivoActivity.abrir(this, Uri.parse(mensaje.message), mensaje.tipo) }
                opciones += "↗️ Compartir imagen" to { compartirArchivoRemoto(mensaje, "image/*") }
                opciones += "⬇️ Descargar imagen" to { descargarImagen(mensaje) }
                opciones += "📋 Copiar imagen" to { copiarImagen(mensaje) }
                if (crystal.crystal.pagos.CanalPagos.DISPONIBLE_EN_LA_APP) {
                    opciones += "💳 Usar para mi recarga" to {
                        crystal.crystal.pagos.CanalPagos.usarComprobanteParaRecarga(this, mensaje.message)
                    }
                }
            }
            mensaje.tipo in listOf("pdf", "video", "audio", "archivo", "file",
                "medidas_crystal", "corte_crystal", "plancha_crystal", "presupuesto") -> {
                opciones += "📂 Abrir" to { abrirArchivoMensaje(mensaje) }
                opciones += "↗️ Compartir" to { compartirArchivoRemoto(mensaje, mimeDeTipo(mensaje.tipo)) }
                opciones += "⬇️ Descargar" to { descargarArchivoRemoto(mensaje) }
            }
            else -> {
                opciones += "📋 Copiar" to { copiarTexto(mensaje.message) }
                opciones += "↗️ Compartir" to { compartirTexto(mensaje.message) }
            }
        }
        if (esMio && !mensaje.deletedForEveryone) {
            if (mensaje.tipo == "texto") opciones += "✏️ Editar" to { mostrarEditar(mensaje) }
            opciones += "🗑️ Eliminar" to { mostrarEliminar(mensaje) }
        }

        // Pedidos en línea: marcar / tomar / ver quién atiende.
        if (!mensaje.deletedForEveryone) {
            when {
                !mensaje.esPedido ->
                    opciones += "🛒 Marcar como pedido" to { marcarComoPedido(mensaje) }
                mensaje.estadoPedido == "en_espera" ->
                    opciones += "✋ Tomar pedido" to { tomarPedido(mensaje) }
                mensaje.estadoPedido == "cogido" ->
                    opciones += "🛒 Atendido por ${mensaje.atendidoNombre.ifBlank { "alguien" }}" to {
                        Toast.makeText(this, "Pedido ya atendido por ${mensaje.atendidoNombre.ifBlank { "alguien" }}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        val labels = opciones.map { it.first }.toTypedArray()
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setItems(labels) { _, w -> opciones[w].second() }
            .show()
    }

    // ==================== PEDIDOS EN LÍNEA ====================
    // patronUid = identidad del negocio (para una terminal, el uid del patrón; para el patrón, el suyo).
    private fun patronUidPedido(): String {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return prefs.getString("patron_uid", null)?.takeIf { it.isNotBlank() }
            ?: FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    }

    @SuppressLint("HardwareIds")
    private fun deviceIdPedido(): String =
        android.provider.Settings.Secure.getString(contentResolver, android.provider.Settings.Secure.ANDROID_ID)

    private fun nombreVendedorPedido(): String {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        return prefs.getString("nombre_vendedor", null)?.takeIf { it.isNotBlank() }
            ?: FirebaseAuth.getInstance().currentUser?.displayName?.takeIf { it.isNotBlank() }
            ?: "Vendedor"
    }

    private fun contactoNombreChat(): String =
        binding.root.findViewById<android.widget.TextView?>(R.id.chatNameText)?.text?.toString()?.trim().orEmpty()

    private fun resumenPedido(mensaje: Message): String {
        val etiqueta = when (mensaje.tipo) {
            "presupuesto" -> "Presupuesto"
            "medidas", "medidas_crystal" -> "Medidas"
            "texto" -> ""
            else -> mensaje.tipo.replaceFirstChar { it.uppercase() }
        }
        val cuerpo = if (mensaje.tipo == "texto") mensaje.message
        else mensaje.nombreArchivo.ifBlank { mensaje.message }
        return listOf(etiqueta, cuerpo.trim()).filter { it.isNotBlank() }.joinToString(": ").take(160)
    }

    private fun marcarComoPedido(mensaje: Message) {
        val data = hashMapOf(
            "patronUid" to patronUidPedido(),
            "deviceId" to deviceIdPedido(),
            "chatId" to chatId,
            "msgId" to mensaje.id,
            "contactoNombre" to contactoNombreChat(),
            "mensajeTipo" to mensaje.tipo,
            "resumen" to resumenPedido(mensaje),
            "contenidoMensaje" to mensaje.message
        )
        com.google.firebase.functions.FirebaseFunctions.getInstance()
            .getHttpsCallable("marcarPedido").call(data)
            .addOnSuccessListener {
                Toast.makeText(this, "Pedido creado (en espera)", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "No se pudo marcar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun tomarPedido(mensaje: Message) {
        val data = hashMapOf(
            "patronUid" to patronUidPedido(),
            "deviceId" to deviceIdPedido(),
            "chatId" to chatId,
            "msgId" to mensaje.id,
            "atendidoNombre" to nombreVendedorPedido()
        )
        com.google.firebase.functions.FirebaseFunctions.getInstance()
            .getHttpsCallable("tomarPedido").call(data)
            .addOnSuccessListener { res ->
                val m = res.data as? Map<*, *>
                if (m?.get("ok") == true) {
                    Toast.makeText(this, "Pedido tomado ✅", Toast.LENGTH_SHORT).show()
                } else {
                    val motivo = m?.get("motivo")?.toString()
                    val quien = m?.get("atendidoNombre")?.toString().orEmpty()
                    val msg = if (motivo == "ya_tomado")
                        "Ese pedido ya lo tomó ${quien.ifBlank { "otro vendedor" }}"
                    else "No se pudo tomar el pedido"
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "No se pudo tomar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun abrirArchivoMensaje(mensaje: Message) {
        val uri = Uri.parse(mensaje.message)
        android.util.Log.d(
            "CrystalAbrir",
            "tipo='${mensaje.tipo}' nombre='${mensaje.nombreArchivo}' msg='${mensaje.message.take(140)}'"
        )
        when {
            esMensajePlanchaCrystal(mensaje) -> abrirPlanchaCrystalDesdeChat(mensaje)
            esMensajeCorteCrystal(mensaje) -> abrirCorteCrystalDesdeChat(mensaje)
            esMensajeMedidasCrystal(mensaje) -> abrirMedidasCrystalDesdeChat(mensaje)
            esMensajeProyectoCrystal(mensaje) -> abrirProyectoCrystalDesdeChat(mensaje)
            mensaje.tipo == "pdf" -> abrirPdfExterno(uri)
            // El multimedia va derecho al visor; no tiene sentido descargarlo para inspeccionarlo.
            mensaje.tipo in listOf("imagen", "image", "video", "audio") ->
                VisorArchivoActivity.abrir(this, uri, mensaje.tipo)
            // Antes de rendirse con "Tipo no soportado": el tipo y el nombre del archivo dependen de
            // la versión que lo envió, así que si el mensaje trae un archivo de Storage se mira su
            // CONTENIDO. El campo "format" del JSON dice qué es, sin importar cómo llegó etiquetado.
            isFirebaseStorageUrl(mensaje.message) -> abrirArchivoPorContenido(mensaje, uri)
            else -> VisorArchivoActivity.abrir(this, uri, mensaje.tipo)
        }
    }

    /** Descarga el archivo y decide por su campo "format" a qué módulo mandarlo. */
    private fun abrirArchivoPorContenido(mensaje: Message, uri: Uri) {
        FirebaseStorage.getInstance().getReferenceFromUrl(mensaje.message)
            .getBytes(20L * 1024L * 1024L)
            .addOnSuccessListener { bytes ->
                val texto = runCatching { String(bytes, Charsets.UTF_8) }.getOrDefault("")
                val formato = runCatching {
                    com.google.gson.JsonParser.parseString(texto).asJsonObject.get("format")?.asString
                }.getOrNull()
                android.util.Log.d(
                    "CrystalAbrir",
                    "descargado ${bytes.size} bytes, format='$formato', inicio='${texto.take(120)}'"
                )
                when (formato) {
                    crystal.crystal.taller.Taller.FORMAT_PROYECTO_CRYSTAL -> importarProyectoCrystal(texto)
                    OptimizacionPlanchasActivity.FORMAT_PLANCHA_CRYSTAL ->
                        abrirCrystalDesdeBytes(bytes, "planchas", OptimizacionPlanchasActivity.EXTENSION_PLANCHA_CRYSTAL) {
                            abrirPlanchaCrystalUri(it)
                        }
                    CorteActivity.FORMAT_CORTE_CRYSTAL ->
                        abrirCrystalDesdeBytes(bytes, "corte", CorteActivity.EXTENSION_CORTE_CRYSTAL) {
                            abrirCorteCrystalUri(it)
                        }
                    "crystal.medidas" ->
                        abrirCrystalDesdeBytes(bytes, "medidas", MedidaActivity.EXTENSION_MEDIDAS_CRYSTAL) {
                            abrirMedidasCrystalUri(it)
                        }
                    else -> VisorArchivoActivity.abrir(this, uri, mensaje.tipo)
                }
            }
            .addOnFailureListener { e ->
                android.util.Log.e("CrystalAbrir", "fallo la descarga: ${e.message}", e)
                VisorArchivoActivity.abrir(this, uri, mensaje.tipo)
            }
    }

    private fun abrirCrystalDesdeBytes(
        bytes: ByteArray,
        prefijo: String,
        extension: String,
        abrir: (Uri) -> Unit
    ) {
        runCatching {
            val file = File(cacheDir, "${prefijo}_${System.currentTimeMillis()}.$extension")
            file.writeBytes(bytes)
            abrir(FileProvider.getUriForFile(this, "${packageName}.fileprovider", file))
        }.onFailure {
            Toast.makeText(this, "No se pudo preparar el archivo: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /** ¿El mensaje es alguno de los formatos propios de Crystal (proyecto, plancha, corte, medidas)? */
    private fun esMensajeCrystal(mensaje: Message): Boolean =
        esMensajeProyectoCrystal(mensaje) || esMensajePlanchaCrystal(mensaje) ||
            esMensajeCorteCrystal(mensaje) || esMensajeMedidasCrystal(mensaje)

    private fun esMensajeProyectoCrystal(mensaje: Message): Boolean {
        return mensaje.tipo == "proyecto_crystal" ||
            mensaje.nombreArchivo.endsWith(".${crystal.crystal.taller.Taller.EXTENSION_PROYECTO_CRYSTAL}", ignoreCase = true)
    }

    private fun abrirProyectoCrystalDesdeChat(mensaje: Message) {
        if (!isFirebaseStorageUrl(mensaje.message)) {
            importarProyectoCrystalDesdeUri(Uri.parse(mensaje.message))
            return
        }
        Toast.makeText(this, "Descargando proyecto...", Toast.LENGTH_SHORT).show()
        FirebaseStorage.getInstance().getReferenceFromUrl(mensaje.message)
            .getBytes(20L * 1024L * 1024L)
            .addOnSuccessListener { bytes -> importarProyectoCrystal(String(bytes, Charsets.UTF_8)) }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al descargar proyecto: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun importarProyectoCrystalDesdeUri(uri: Uri) {
        runCatching {
            val texto = contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }.orEmpty()
            importarProyectoCrystal(texto)
        }.onFailure {
            Toast.makeText(this, "No se pudo leer el proyecto: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun importarProyectoCrystal(json: String) {
        runCatching {
            val root = com.google.gson.JsonParser.parseString(json).asJsonObject
            if (root.get("format")?.asString != crystal.crystal.taller.Taller.FORMAT_PROYECTO_CRYSTAL) {
                Toast.makeText(this, "El archivo no es un proyecto Crystal válido", Toast.LENGTH_SHORT).show()
                return
            }
            val nombreOrig = root.get("proyecto")?.asString?.trim().takeUnless { it.isNullOrBlank() } ?: "Proyecto compartido"
            val tipo = object : com.google.gson.reflect.TypeToken<MutableMap<String, MutableList<MutableList<String>>>>() {}.type
            val mapa: MutableMap<String, MutableList<MutableList<String>>> =
                com.google.gson.Gson().fromJson(root.get("data"), tipo) ?: mutableMapOf()
            if (mapa.isEmpty()) {
                Toast.makeText(this, "El proyecto recibido está vacío", Toast.LENGTH_SHORT).show()
                return
            }
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Proyecto recibido")
                .setMessage("¿Agregar \"$nombreOrig\" a tus proyectos?")
                .setPositiveButton("Agregar") { _, _ -> guardarProyectoImportado(nombreOrig, mapa) }
                .setNegativeButton("Cancelar", null)
                .show()
        }.onFailure {
            Toast.makeText(this, "No se pudo importar el proyecto: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarProyectoImportado(
        nombreOrig: String,
        mapa: MutableMap<String, MutableList<MutableList<String>>>
    ) {
        var nombre = nombreOrig
        var i = 2
        while (crystal.crystal.casilla.MapStorage.existeProyecto(this, nombre)) {
            nombre = "$nombreOrig ($i)"; i++
        }
        crystal.crystal.casilla.MapStorage.crearProyecto(this, nombre, "Recibido por chat")
        crystal.crystal.casilla.MapStorage.guardarProyecto(this, nombre, mapa)
        Toast.makeText(this, "Proyecto agregado: $nombre", Toast.LENGTH_LONG).show()
    }

    private fun copiarTexto(texto: String) {
        val cm = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
        cm.setPrimaryClip(android.content.ClipData.newPlainText("mensaje", texto))
        Toast.makeText(this, "Texto copiado", Toast.LENGTH_SHORT).show()
    }

    private fun compartirTexto(texto: String) {
        startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"; putExtra(Intent.EXTRA_TEXT, texto)
        }, "Compartir"))
    }

    private fun nombreArchivoDe(mensaje: Message): String {
        val n = mensaje.nombreArchivo
        if (n.isNotBlank()) return n
        val ext = when (mensaje.tipo) {
            "pdf" -> "pdf"; "video" -> "mp4"; "audio" -> "m4a"
            "imagen", "image" -> "jpg"; else -> "dat"
        }
        return "crystal_${System.currentTimeMillis()}.$ext"
    }

    private fun mimeDeTipo(tipo: String): String = when (tipo) {
        "pdf" -> "application/pdf"; "video" -> "video/*"; "audio" -> "audio/*"
        "imagen", "image" -> "image/*"; else -> "*/*"
    }

    private fun descargarBytesMensaje(mensaje: Message, onOk: (ByteArray) -> Unit) {
        Toast.makeText(this, "Procesando…", Toast.LENGTH_SHORT).show()
        if (isFirebaseStorageUrl(mensaje.message)) {
            FirebaseStorage.getInstance().getReferenceFromUrl(mensaje.message)
                .getBytes(50L * 1024 * 1024)
                .addOnSuccessListener { onOk(it) }
                .addOnFailureListener { Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show() }
        } else {
            Thread {
                runCatching { java.net.URL(mensaje.message).openStream().use { it.readBytes() } }
                    .onSuccess { b -> runOnUiThread { onOk(b) } }
                    .onFailure { e -> runOnUiThread { Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show() } }
            }.start()
        }
    }

    private fun uriCacheDeBytes(bytes: ByteArray, nombre: String): Uri {
        val file = File(cacheDir, nombre)
        file.writeBytes(bytes)
        return FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
    }

    private fun compartirArchivoRemoto(mensaje: Message, mime: String) {
        descargarBytesMensaje(mensaje) { bytes ->
            runCatching {
                val uri = uriCacheDeBytes(bytes, nombreArchivoDe(mensaje))
                startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                    type = mime
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }, "Compartir"))
            }.onFailure { Toast.makeText(this, "No se pudo compartir: ${it.message}", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun copiarImagen(mensaje: Message) {
        descargarBytesMensaje(mensaje) { bytes ->
            runCatching {
                val uri = uriCacheDeBytes(bytes, nombreArchivoDe(mensaje))
                val cm = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                cm.setPrimaryClip(android.content.ClipData.newUri(contentResolver, "imagen", uri))
                Toast.makeText(this, "Imagen copiada", Toast.LENGTH_SHORT).show()
            }.onFailure { Toast.makeText(this, "No se pudo copiar: ${it.message}", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun descargarImagen(mensaje: Message) {
        descargarBytesMensaje(mensaje) { bytes ->
            runCatching {
                val nombre = nombreArchivoDe(mensaje)
                val mime = if (nombre.endsWith(".png", true)) "image/png" else "image/jpeg"
                val values = android.content.ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, nombre)
                    put(MediaStore.MediaColumns.MIME_TYPE, mime)
                    if (android.os.Build.VERSION.SDK_INT >= 29)
                        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Crystal")
                }
                val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                    ?: throw IllegalStateException("No se pudo crear el archivo")
                contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
                Toast.makeText(this, "Imagen guardada en Galería (Crystal)", Toast.LENGTH_LONG).show()
            }.onFailure { Toast.makeText(this, "No se pudo descargar: ${it.message}", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun descargarArchivoRemoto(mensaje: Message) {
        descargarBytesMensaje(mensaje) { bytes ->
            runCatching {
                val nombre = nombreArchivoDe(mensaje)
                val values = android.content.ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, nombre)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeDeTipo(mensaje.tipo))
                    if (android.os.Build.VERSION.SDK_INT >= 29)
                        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/Crystal")
                }
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: throw IllegalStateException("No se pudo crear el archivo")
                contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
                Toast.makeText(this, "Descargado en Descargas/Crystal", Toast.LENGTH_LONG).show()
            }.onFailure { Toast.makeText(this, "No se pudo descargar: ${it.message}", Toast.LENGTH_SHORT).show() }
        }
    }

    // El flujo de "usar este comprobante para mi recarga" vive ahora en CanalPagos: hablaba con
    // `reservas_recarga` y con la función `adjuntarComprobante`, y eso no puede viajar en el APK
    // de Play.

    private fun abrirMedidasCrystalDesdeChat(mensaje: Message) {
        if (!isFirebaseStorageUrl(mensaje.message)) {
            val uri = Uri.parse(mensaje.message)
            abrirMedidasCrystalUri(uri)
            return
        }

        Toast.makeText(this, "Descargando medidas...", Toast.LENGTH_SHORT).show()
        FirebaseStorage.getInstance().getReferenceFromUrl(mensaje.message)
            .getBytes(10L * 1024L * 1024L)
            .addOnSuccessListener { bytes ->
                runCatching {
                    val nombre = mensaje.nombreArchivo
                        .takeIf { it.endsWith(".${MedidaActivity.EXTENSION_MEDIDAS_CRYSTAL}", ignoreCase = true) }
                        ?: "medidas_${System.currentTimeMillis()}.${MedidaActivity.EXTENSION_MEDIDAS_CRYSTAL}"
                    val file = File(cacheDir, nombre)
                    file.writeBytes(bytes)
                    val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
                    abrirMedidasCrystalUri(uri)
                }.onFailure {
                    Toast.makeText(this, "No se pudo preparar medidas: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al descargar medidas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun abrirMedidasCrystalUri(uri: Uri) {
        val intent = Intent(this, MedidaActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uri, MedidaActivity.MIME_MEDIDAS_CRYSTAL)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = ClipData.newUri(contentResolver, "medidas_crystal", uri)
        }
        startActivity(intent)
    }

    private fun abrirCorteCrystalDesdeChat(mensaje: Message) {
        if (!isFirebaseStorageUrl(mensaje.message)) {
            abrirCorteCrystalUri(Uri.parse(mensaje.message))
            return
        }

        Toast.makeText(this, "Descargando corte...", Toast.LENGTH_SHORT).show()
        FirebaseStorage.getInstance().getReferenceFromUrl(mensaje.message)
            .getBytes(10L * 1024L * 1024L)
            .addOnSuccessListener { bytes ->
                runCatching {
                    val nombre = mensaje.nombreArchivo
                        .takeIf { it.endsWith(".${CorteActivity.EXTENSION_CORTE_CRYSTAL}", ignoreCase = true) }
                        ?: "corte_${System.currentTimeMillis()}.${CorteActivity.EXTENSION_CORTE_CRYSTAL}"
                    val file = File(cacheDir, nombre)
                    file.writeBytes(bytes)
                    val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
                    abrirCorteCrystalUri(uri)
                }.onFailure {
                    Toast.makeText(this, "No se pudo preparar corte: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al descargar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun abrirCorteCrystalUri(uri: Uri) {
        val intent = Intent(this, CorteActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uri, CorteActivity.MIME_CORTE_CRYSTAL)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = ClipData.newUri(contentResolver, "corte_crystal", uri)
        }
        startActivity(intent)
    }

    private fun abrirPlanchaCrystalDesdeChat(mensaje: Message) {
        if (!isFirebaseStorageUrl(mensaje.message)) {
            abrirPlanchaCrystalUri(Uri.parse(mensaje.message))
            return
        }

        Toast.makeText(this, "Descargando corte de planchas...", Toast.LENGTH_SHORT).show()
        FirebaseStorage.getInstance().getReferenceFromUrl(mensaje.message)
            .getBytes(10L * 1024L * 1024L)
            .addOnSuccessListener { bytes ->
                runCatching {
                    val nombre = mensaje.nombreArchivo
                        .takeIf { it.endsWith(".${OptimizacionPlanchasActivity.EXTENSION_PLANCHA_CRYSTAL}", ignoreCase = true) }
                        ?: "planchas_${System.currentTimeMillis()}.${OptimizacionPlanchasActivity.EXTENSION_PLANCHA_CRYSTAL}"
                    val file = File(cacheDir, nombre)
                    file.writeBytes(bytes)
                    val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
                    abrirPlanchaCrystalUri(uri)
                }.onFailure {
                    Toast.makeText(this, "No se pudo preparar corte de planchas: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al descargar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun abrirPlanchaCrystalUri(uri: Uri) {
        val intent = Intent(this, OptimizacionPlanchasActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            setDataAndType(uri, OptimizacionPlanchasActivity.MIME_PLANCHA_CRYSTAL)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            clipData = ClipData.newUri(contentResolver, "plancha_crystal", uri)
        }
        startActivity(intent)
    }

    private fun isFirebaseStorageUrl(value: String): Boolean {
        return value.startsWith("gs://") || value.startsWith("https://")
    }

    private fun abrirPdfExterno(uri: Uri) {
        if (uri.scheme == "http" || uri.scheme == "https") {
            descargarYAbrirPdf(uri.toString())
            return
        }

        abrirPdfLocal(uri)
    }

    private fun descargarYAbrirPdf(url: String) {
        Toast.makeText(this, "Descargando PDF...", Toast.LENGTH_SHORT).show()
        Thread {
            try {
                val localUri = guardarPdfEnDescargas(url)
                runOnUiThread {
                    abrirPdfLocal(localUri)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "No se pudo descargar el PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }

    private fun guardarPdfEnDescargas(url: String): Uri {
        val fileName = "chat_pdf_${System.currentTimeMillis()}.pdf"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            guardarPdfConMediaStore(url, fileName)
        } else {
            guardarPdfEnDescargasLegacy(url, fileName)
        }
    }

    private fun guardarPdfConMediaStore(url: String, fileName: String): Uri {
        val resolver = contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOWNLOADS}/Crystal/PDF"
            )
        }
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: throw IllegalStateException("No se pudo crear el archivo en Descargas")

        try {
            resolver.openOutputStream(uri)?.use { output ->
                URL(url).openStream().use { input ->
                    input.copyTo(output)
                }
            } ?: throw IllegalStateException("No se pudo abrir el archivo de destino")
            return uri
        } catch (e: Exception) {
            resolver.delete(uri, null, null)
            throw e
        }
    }

    private fun guardarPdfEnDescargasLegacy(url: String, fileName: String): Uri {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val crystalDir = File(downloadsDir, "Crystal/PDF").apply { mkdirs() }
        val file = File(crystalDir, fileName)
        URL(url).openStream().use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        return FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        )
    }

    private fun abrirPdfLocal(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            clipData = ClipData.newUri(contentResolver, "pdf", uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        }

        try {
            val handlers = packageManager.queryIntentActivities(intent, 0)
            handlers.forEach { handler ->
                grantUriPermission(
                    handler.activityInfo.packageName,
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            val chooser = Intent.createChooser(intent, "Abrir PDF con").apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                clipData = ClipData.newUri(contentResolver, "pdf", uri)
            }
            startActivity(chooser)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "No hay visor PDF instalado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun mostrarEditar(m: Message) {
        if (m.tipo != "texto") {
            Toast.makeText(this, "Solo puedes editar mensajes de texto", Toast.LENGTH_SHORT).show()
            return
        }
        val input = EditText(this).apply {
            setText(m.message)
            setSelection(m.message.length)
        }
        AlertDialog.Builder(this)
            .setTitle("Editar mensaje")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevo = input.text.toString().trim()
                if (nuevo.isNotEmpty()) {
                    chatMessageRepository.editTextMessage(chatId, m.id, nuevo)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun mostrarEliminar(m: Message) {
        val opts = arrayOf("Eliminar para mí", "Eliminar para todos")
        AlertDialog.Builder(this)
            .setTitle("¿Qué deseas hacer?")
            .setItems(opts) { _, idx ->
                when (idx) {
                    0 -> chatMessageRepository.deleteForMe(chatId, m.id, usuario)
                    1 -> chatMessageRepository.deleteForEveryone(chatId, m.id)
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun extraerNombreArchivo(uri: Uri): String {
        var n = uri.lastPathSegment ?: "archivo"
        if (n.contains("/")) n = n.substringAfterLast("/")
        return n
    }

    override fun onResume() {
        super.onResume()
        db.collection("usuarios").document(usuario)
            .update("online", true)
    }

    override fun onPause() {
        db.collection("usuarios").document(usuario)
            .update(mapOf(
                "online" to false,
                "lastSeen" to FieldValue.serverTimestamp()
            ))
        super.onPause()
    }

    override fun onDestroy() {
        if (::mensajesListener.isInitialized) mensajesListener.remove()
        if (::presenciaListener.isInitialized) presenciaListener.remove()
        super.onDestroy()
    }

}











