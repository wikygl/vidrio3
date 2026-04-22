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
        archivoCompartidoParaEnviar?.let { uri ->
            subirYEnviar(uri)
            archivoCompartidoParaEnviar = null
            nombreArchivoCompartido = null
            mimeArchivoCompartido = null
        }
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
                    else -> {
                        if (mensaje.tipo == "pdf") {
                            abrirPdfExterno(uri)
                        } else {
                            // Imagen y audio -> visor interno
                            VisorArchivoActivity.abrir(this, uri, mensaje.tipo)
                        }
                    }
                }
            }
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
        val chatRef = db.collection("chats").document(chatId)
        val mensajesRef = chatRef.collection("messages")
        val nuevoMensajeRef = mensajesRef.document()
        val ahora = Date()

        val tipo = detectarTipo(uri)
        val nombreOriginal = extraerNombreArchivo(uri)

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

    private fun detectarTipo(uri: Uri): String {
        val t = contentResolver.getType(uri) ?: ""
        val fileName = extraerNombreArchivo(uri)

        return when {
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











