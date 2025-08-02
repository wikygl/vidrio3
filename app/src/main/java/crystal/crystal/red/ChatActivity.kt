package crystal.crystal.red

import Message
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.iceteck.silicompressorr.SiliCompressor
import crystal.crystal.R
import crystal.crystal.databinding.ActivityChatBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.Date

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance().reference

    private lateinit var mensajesListener: ListenerRegistration
    private lateinit var presenciaListener: ListenerRegistration

    private var chatId = ""
    private var usuario = ""
    private lateinit var adapter: MessageAdapter

    private var presupuestoParaEnviar: String? = null
    private var nombrePresupuesto: String? = null

    companion object {
        private const val PICK_FILE_REQ = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent.getStringExtra("chatId")?.let { chatId = it }
        intent.getStringExtra("usuario")?.let { usuario = it }

        intent.getStringExtra("enviar_presupuesto")?.let { presupuestoParaEnviar = it }
        intent.getStringExtra("nombre_presupuesto")?.let { nombrePresupuesto = it }

        if (chatId.isNotEmpty() && usuario.isNotEmpty()) {
            inicializarCabecera()
            configurarRecycler()
            binding.btEnviar.setOnClickListener { onClickEnviarTexto() }
            binding.btArchivo.setOnClickListener { seleccionarArchivo() }
            binding.btPresupuesto.setOnClickListener { enviarPresupuesto() }
            cargarChat()

            // Si hay un presupuesto para enviar, mostrarlo
            if (presupuestoParaEnviar != null) {
                mostrarOpcionEnviarPresupuesto()
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

    private fun subirYEnviarPresupuesto(uri: Uri) {
        val chatRef = db.collection("chats").document(chatId)
        val mensajesRef = chatRef.collection("messages")
        val nuevoMensajeRef = mensajesRef.document()
        val ahora = Date()

        // Crear mensaje placeholder
        val placeholder = Message(
            id = nuevoMensajeRef.id,
            message = "ENVIANDO PRESUPUESTO...",
            from = usuario,
            dob = ahora,
            leido = false,
            entregado = false,
            tipo = "presupuesto",
            nombreArchivo = nombrePresupuesto ?: "presupuesto.json"
        ).apply { hasPendingWrites = true }

        adapter.addMensajeTemporal(placeholder)
        binding.rvMensajes.scrollToPosition(adapter.itemCount - 1)

        // Guardar en Firestore
        nuevoMensajeRef.set(mapOf(
            "id" to placeholder.id,
            "message" to placeholder.message,
            "from" to placeholder.from,
            "dob" to placeholder.dob,
            "leido" to false,
            "entregado" to false,
            "deletedFor" to placeholder.deletedFor,
            "deletedForEveryone" to placeholder.deletedForEveryone,
            "tipo" to "presupuesto",
            "nombreArchivo" to placeholder.nombreArchivo
        ))

        // Subir archivo como bytes
        try {
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()

            if (bytes != null) {
                val refStorage = storage.child("chat_files/$chatId/${nuevoMensajeRef.id}")
                refStorage.putBytes(bytes)
                    .addOnSuccessListener {
                        refStorage.downloadUrl.addOnSuccessListener { url ->
                            nuevoMensajeRef.update("message", url.toString())
                            chatRef.update("lastMsgDate", ahora)
                            Toast.makeText(this, "Presupuesto enviado correctamente", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        nuevoMensajeRef.update("message", "Error al enviar presupuesto")
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                nuevoMensajeRef.update("message", "Error al leer archivo")
                Toast.makeText(this, "No se pudo leer el archivo", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            nuevoMensajeRef.update("message", "Error al procesar archivo")
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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
                        val httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(mensaje.message)
                        httpsReference.getBytes(Long.MAX_VALUE)
                            .addOnSuccessListener { bytes ->
                                try {
                                    val jsonContent = String(bytes, Charsets.UTF_8)

                                    // Crear Intent para MainActivity
                                    val intent = Intent(this, crystal.crystal.MainActivity::class.java).apply {
                                        putExtra("cargar_presupuesto_json", jsonContent)
                                        putExtra("cargar_presupuesto_nombre", mensaje.nombreArchivo)
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
                    "texto" -> {
                        // **NUEVO: Manejar mensajes de texto con formato de medidas**
                        if (esFormatoMedidas(mensaje.message)) {
                            // Es un mensaje con formato de medidas
                            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                            builder.setTitle("📐 Lista de Medidas")

                            val lineas = mensaje.message.trim().split("\n").filter { it.isNotBlank() }
                            val producto = lineas[0].trim()
                            val cantidadMedidas = lineas.size - 1

                            builder.setMessage(
                                "Producto: $producto\n" +
                                        "Elementos detectados: $cantidadMedidas\n\n" +
                                        "¿Deseas importar estas medidas a tu presupuesto?"
                            )

                            builder.setPositiveButton("📋 Importar") { _, _ ->
                                // Enviar a MainActivity para procesar
                                val intent = Intent(this, crystal.crystal.MainActivity::class.java).apply {
                                    putExtra("importar_medidas_texto", mensaje.message)
                                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                }
                                startActivity(intent)
                                Toast.makeText(this, "Abriendo calculadora para importar...", Toast.LENGTH_SHORT).show()
                            }

                            builder.setNegativeButton("❌ Cancelar", null)
                            builder.show()
                        } else {
                            // Mensaje de texto normal, no hacer nada especial
                            Toast.makeText(this, "Mensaje de texto normal", Toast.LENGTH_SHORT).show()
                        }
                    }
                    "video" -> {
                        // Código existente para videos...
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
                        // Imagen, audio o PDF → visor interno
                        VisorArchivoActivity.abrir(this, uri, mensaje.tipo)
                    }
                }
            }
        )
        binding.rvMensajes.layoutManager =
            LinearLayoutManager(this).apply { stackFromEnd = true }
        binding.rvMensajes.adapter = adapter
    }

    private fun inicializarCabecera() {
        val header = binding.root.findViewById<ConstraintLayout>(R.id.headerChat)
        val ivFoto = header.findViewById<android.widget.ImageView>(R.id.ivFoto)
        val tvNombre = header.findViewById<android.widget.TextView>(R.id.chatNameText)
        val tvEstado = header.findViewById<android.widget.TextView>(R.id.usersTextView)

        db.collection("chats").document(chatId)
            .get()
            .addOnSuccessListener { doc ->
                val users = doc.get("users") as List<*>
                val otroId = users.first { it != usuario }.toString()
                presenciaListener = db.collection("usuarios").document(otroId)
                    .addSnapshotListener { snap, e ->
                        if (e != null || snap == null || !snap.exists()) return@addSnapshotListener
                        tvNombre.text = snap.getString("nombre") ?: "—"
                        snap.getString("imagenPerfil")?.let { url ->
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
        mensajesListener = db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("dob", Query.Direction.ASCENDING)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snap, e ->
                if (e != null || snap == null) return@addSnapshotListener
                val lista = mutableListOf<Message>()
                val batch = db.batch()
                for (doc in snap.documents) {
                    val m = doc.toObject(Message::class.java)?.apply { id = doc.id } ?: continue
                    m.hasPendingWrites = doc.metadata.hasPendingWrites()
                    m.entregado = doc.getBoolean("entregado") ?: m.entregado
                    m.leido     = doc.getBoolean("leido")     ?: m.leido
                    lista.add(m)
                    if (!m.hasPendingWrites && m.from != usuario && !(doc.getBoolean("entregado") ?: false))
                        batch.update(doc.reference, "entregado", true)
                    if (m.from != usuario && !m.leido)
                        batch.update(doc.reference, "leido", true)
                }
                batch.commit()
                adapter.setData(lista)
                if (lista.isNotEmpty()) binding.rvMensajes.scrollToPosition(lista.size - 1)
            }
    }

    private fun onClickEnviarTexto() {
        val texto = binding.messageTextField.text.toString().trim()
        if (texto.isEmpty()) return
        enviarMensaje(texto, "texto")
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
            from = usuario,
            dob = ahora,
            leido = false,
            entregado = false,
            tipo = tipo,
            nombreArchivo = nombreOriginal
        ).apply { hasPendingWrites = true }

        adapter.addMensajeTemporal(placeholder)
        binding.rvMensajes.scrollToPosition(adapter.itemCount - 1)

        nuevoMensajeRef.set( mapOf(
            "id" to placeholder.id,
            "message" to placeholder.message,
            "from" to placeholder.from,
            "dob" to placeholder.dob,
            "leido" to false,
            "entregado" to false,
            "deletedFor" to placeholder.deletedFor,
            "deletedForEveryone" to placeholder.deletedForEveryone,
            "tipo" to tipo,
            "nombreArchivo" to nombreOriginal
        ))

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
                    // Actualiza el placeholder en Firestore y UI
                    nuevoMensajeRef.update("message", "CARGANDO... $pct%")
                }
            }
            // al terminar
            uploadTask.addOnSuccessListener {
                refStorage.downloadUrl.addOnSuccessListener { url ->
                    nuevoMensajeRef.update("message", url.toString())
                    chatRef.update("lastMsgDate", ahora)
                }
            }.addOnFailureListener { e ->
                nuevoMensajeRef.update("message", "Error al subir")
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
                            nuevoMensajeRef.update("message", url.toString())
                            chatRef.update("lastMsgDate", ahora)
                        }
                    }
                    .addOnFailureListener { e ->
                        nuevoMensajeRef.update("message", "Error al subir imagen")
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            "video" -> {
                // comprimir video primero
                comprimirVideo(uri) { uriComprimido ->
                    if (uriComprimido != null) {
                        realizarSubida(uriComprimido)
                    } else {
                        nuevoMensajeRef.update("message", "Error al comprimir")
                        Toast.makeText(this, "No se pudo comprimir video", Toast.LENGTH_SHORT).show()
                    }
                }
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
        val chatRef = db.collection("chats").document(chatId)
        val msgRef  = chatRef.collection("messages").document()
        val ahora   = Date()

        val msg = Message(
            id = msgRef.id,
            message = texto,
            from = usuario,
            dob = ahora,
            leido = false,
            entregado = false,
            tipo = tipo
        ).apply { hasPendingWrites = true }

        adapter.addMensajeTemporal(msg)
        binding.rvMensajes.scrollToPosition(adapter.itemCount - 1)

        val data = mapOf(
            "id"        to msg.id,
            "message"   to msg.message,
            "from"      to msg.from,
            "dob"       to msg.dob,
            "leido"     to false,
            "entregado" to false,
            "deletedFor" to msg.deletedFor,
            "deletedForEveryone" to msg.deletedForEveryone,
            "tipo"      to tipo
        )
        val batch = db.batch()
        batch.set(msgRef, data)
        batch.update(chatRef, "lastMsgDate", ahora)
        batch.commit()
            .addOnSuccessListener { if (tipo == "texto") binding.messageTextField.setText("") }
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
                    db.collection("chats").document(chatId)
                        .collection("messages")
                        .document(m.id)
                        .update("message", nuevo)
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
                    0 -> db.collection("chats").document(chatId)
                        .collection("messages")
                        .document(m.id)
                        .update("deletedFor", FieldValue.arrayUnion(usuario))
                    1 -> db.collection("chats").document(chatId)
                        .collection("messages")
                        .document(m.id)
                        .update(mapOf(
                            "message" to "mensaje borrado.",
                            "deletedForEveryone" to true
                        ))
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









