package crystal.crystal.registro

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import crystal.crystal.R
import crystal.crystal.databinding.ActivityGestiondispositivosactivityBinding
import crystal.crystal.databinding.ItemDispositivoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class DispositivoAutorizado(
    val deviceId: String = "",
    val marca: String = "",
    val modelo: String = "",
    val android: String = "",
    val autorizadoEn: Date? = null,
    val activo: Boolean = true,
    val tipoAutorizacion: String = "auto"
)

@RequiresApi(Build.VERSION_CODES.M)
class GestionDispositivosActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGestiondispositivosactivityBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("MyPrefs", MODE_PRIVATE)
    }

    private val listaDispositivos = mutableListOf<DispositivoAutorizado>()
    private lateinit var adapter: DispositivosAdapter

    private var terminalesContratadas = 0
    private var terminalesAutorizadas = 0
    private val REQUEST_FOTO_TERMINAL = 100
    private var fotoTerminalUri: Uri? = null
    private var nombreVendedorTemporal: String? = null

    // Variable para mantener referencia al ImageView del diálogo
    private var ivFotoDialogo: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGestiondispositivosactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Gestión de Terminales"

        configurarRecyclerView()
        cargarDatos()

        binding.btnAmpliarPlan.setOnClickListener {
            val intent = Intent(this, PlanSelectionActivity::class.java)
            intent.putExtra("ES_AMPLIACION", true)
            intent.putExtra("TERMINALES_ACTUALES", terminalesContratadas)
            startActivity(intent)
        }

        binding.swipeRefresh.setOnRefreshListener {
            cargarDatos()
        }

        binding.btnAgregarTerminal.setOnClickListener {
            mostrarDialogoAgregarTerminal()
        }
        verificarEstadoDispositivo()

        binding.btnConvertirEnPatron.setOnClickListener {
            mostrarDialogoConversionPatron()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun configurarRecyclerView() {
        adapter = DispositivosAdapter(
            dispositivos = listaDispositivos,
            onDesautorizar = { dispositivo ->
                mostrarDialogoDesautorizar(dispositivo)
            }
        )
        binding.rvDispositivos.layoutManager = LinearLayoutManager(this)
        binding.rvDispositivos.adapter = adapter
    }

    private fun cargarDatos() {
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "No autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                // Cargar configuración
                val configDoc = db.collection("usuarios")
                    .document(uid)
                    .collection("plan_ventas")
                    .document("config")
                    .get()
                    .await()

                terminalesContratadas = configDoc.getLong("terminales_contratadas")?.toInt() ?: 0

                // ⭐ ELIMINADO: terminalesAutorizadas = configDoc.getLong("terminales_autorizadas")?.toInt() ?: 0

                // Cargar dispositivos PRIMERO
                val dispositivosSnapshot = db.collection("usuarios")
                    .document(uid)
                    .collection("plan_ventas")
                    .document("dispositivos")
                    .collection("autorizados")
                    .get()
                    .await()

                // ⭐ CONTAR REAL en lugar de usar campo
                terminalesAutorizadas = dispositivosSnapshot.size()

                // Actualizar UI
                binding.tvContador.text = "Usando $terminalesAutorizadas/$terminalesContratadas terminales"

                val porcentaje = if (terminalesContratadas > 0) {
                    (terminalesAutorizadas.toFloat() / terminalesContratadas * 100).toInt()
                } else 0

                binding.progressBar.progress = porcentaje

                // Cambiar color según uso
                when {
                    terminalesAutorizadas >= terminalesContratadas -> {
                        binding.tvContador.setTextColor(getColor(android.R.color.holo_red_dark))
                    }
                    terminalesAutorizadas >= terminalesContratadas * 0.8 -> {
                        binding.tvContador.setTextColor(getColor(android.R.color.holo_orange_dark))
                    }
                    else -> {
                        binding.tvContador.setTextColor(getColor(android.R.color.holo_green_dark))
                    }
                }

                // Procesar dispositivos (MISMO código)
                listaDispositivos.clear()
                for (doc in dispositivosSnapshot.documents) {
                    val dispositivo = DispositivoAutorizado(
                        deviceId = doc.id,
                        marca = doc.getString("marca") ?: "",
                        modelo = doc.getString("modelo") ?: "",
                        android = doc.getString("android") ?: "",
                        autorizadoEn = try {
                            doc.getTimestamp("autorizado_en")?.toDate()
                        } catch (e: Exception) {
                            doc.getLong("autorizado_en")?.let { Date(it) }
                        },
                        activo = doc.getBoolean("activo") ?: false,
                        tipoAutorizacion = doc.getString("tipo_autorizacion") ?: "auto"
                    )
                    listaDispositivos.add(dispositivo)
                }

                // Ordenar: activos primero, luego por fecha
                listaDispositivos.sortWith(
                    compareByDescending<DispositivoAutorizado> { it.activo }
                        .thenByDescending { it.autorizadoEn }
                )

                adapter.notifyDataSetChanged()

                // Mostrar mensaje si no hay dispositivos
                if (listaDispositivos.isEmpty()) {
                    binding.tvVacio.visibility = View.VISIBLE
                    binding.rvDispositivos.visibility = View.GONE
                } else {
                    binding.tvVacio.visibility = View.GONE
                    binding.rvDispositivos.visibility = View.VISIBLE
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this@GestionDispositivosActivity,
                    "Error cargando datos: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.swipeRefresh.isRefreshing = false
            }
        }
    }

    private fun mostrarDialogoDesautorizar(dispositivo: DispositivoAutorizado) {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Desautorizar terminal")
            .setMessage(
                """
                ¿Desautorizar este dispositivo?
                
                ${dispositivo.marca} ${dispositivo.modelo}
                ID: ${dispositivo.deviceId.take(8)}...
                
                Este dispositivo ya no podrá usar Crystal hasta que vuelvas a autorizarlo.
                """.trimIndent()
            )
            .setPositiveButton("Desautorizar") { _, _ ->
                desautorizarDispositivo(dispositivo)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun desautorizarDispositivo(dispositivo: DispositivoAutorizado) {
        val uid = auth.currentUser?.uid ?: return

        lifecycleScope.launch {
            try {
                // 1. Eliminar documento del terminal
                db.collection("usuarios")
                    .document(uid)
                    .collection("plan_ventas")
                    .document("dispositivos")
                    .collection("autorizados")
                    .document(dispositivo.deviceId)
                    .delete()
                    .await()

                // 2. ⭐ BUSCAR Y ELIMINAR código temporal asociado
                val codigosSnapshot = db.collection("usuarios")
                    .document(uid)
                    .collection("plan_ventas")
                    .document("dispositivos")
                    .collection("codigos_temporales")
                    .whereEqualTo("usado_por_device", dispositivo.deviceId)
                    .get()
                    .await()

                // Eliminar todos los códigos que usó este dispositivo
                for (doc in codigosSnapshot.documents) {
                    doc.reference.delete().await()
                }

                Toast.makeText(
                    this@GestionDispositivosActivity,
                    "✅ Terminal desautorizado y eliminado",
                    Toast.LENGTH_SHORT
                ).show()

                cargarDatos()

            } catch (e: Exception) {
                Toast.makeText(
                    this@GestionDispositivosActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // =================== ADAPTER ===================

    class DispositivosAdapter(
        private val dispositivos: List<DispositivoAutorizado>,
        private val onDesautorizar: (DispositivoAutorizado) -> Unit
    ) : RecyclerView.Adapter<DispositivosAdapter.ViewHolder>() {

        class ViewHolder(val binding: ItemDispositivoBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemDispositivoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val dispositivo = dispositivos[position]

            holder.binding.apply {
                tvNombre.text = "${dispositivo.marca} ${dispositivo.modelo}"
                tvDeviceId.text = "ID: ${dispositivo.deviceId.take(8)}..."
                tvAndroid.text = "Android ${dispositivo.android}"

                val fecha = dispositivo.autorizadoEn?.let {
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(it)
                } ?: "Desconocida"
                tvFecha.text = "Autorizado: $fecha"

                if (dispositivo.activo) {
                    tvEstado.text = "✅ Activo"
                    tvEstado.setTextColor(root.context.getColor(android.R.color.holo_green_dark))
                    btnDesautorizar.isEnabled = true
                    btnDesautorizar.alpha = 1.0f
                } else {
                    tvEstado.text = "❌ Desautorizado"
                    tvEstado.setTextColor(root.context.getColor(android.R.color.holo_red_dark))
                    btnDesautorizar.isEnabled = false
                    btnDesautorizar.alpha = 0.5f
                }

                btnDesautorizar.setOnClickListener {
                    onDesautorizar(dispositivo)
                }
            }
        }

        override fun getItemCount() = dispositivos.size
    }

    /**
     * ⭐ MODIFICADO: Muestra diálogo con selector de foto
     */
    @SuppressLint("MissingInflatedId")
    private fun mostrarDialogoAgregarTerminal() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_agregar_terminal, null)
        val etNombreVendedor = dialogView.findViewById<EditText>(R.id.etNombreVendedor)
        val radioQR = dialogView.findViewById<RadioButton>(R.id.radioQR)
        val radioPIN = dialogView.findViewById<RadioButton>(R.id.radioPIN)
        val ivFotoTerminal = dialogView.findViewById<ImageView>(R.id.ivFotoTerminal)
        val btnSeleccionarFoto = dialogView.findViewById<Button>(R.id.btnSeleccionarFoto)

        // Guardar referencia al ImageView
        ivFotoDialogo = ivFotoTerminal

        // Reset variables
        fotoTerminalUri = null
        nombreVendedorTemporal = null

        // Mostrar foto por defecto
        Glide.with(this)
            .load(R.drawable.usr)
            .circleCrop()
            .into(ivFotoTerminal)

        btnSeleccionarFoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_FOTO_TERMINAL)
        }

        AlertDialog.Builder(this)
            .setTitle("➕ Agregar Terminal")
            .setView(dialogView)
            .setPositiveButton("Generar") { _, _ ->
                val nombreVendedor = etNombreVendedor.text.toString().trim()

                if (nombreVendedor.isEmpty()) {
                    Toast.makeText(this, "⚠️ Ingresa el nombre del vendedor", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Guardar nombre temporal para asociar con la foto
                nombreVendedorTemporal = nombreVendedor

                if (radioQR.isChecked) {
                    generarQRParaTerminal(nombreVendedor, fotoTerminalUri)
                } else {
                    generarPINParaTerminal(nombreVendedor, fotoTerminalUri)
                }
            }
            .setNegativeButton("Cancelar") { _, _ ->
                // Limpiar referencias
                ivFotoDialogo = null
                fotoTerminalUri = null
                nombreVendedorTemporal = null
            }
            .show()
    }

    /**
     * ⭐ NUEVO: Maneja resultado de selección de foto
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_FOTO_TERMINAL && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                fotoTerminalUri = uri

                // Actualizar vista del diálogo si está disponible
                ivFotoDialogo?.let { imageView ->
                    Glide.with(this)
                        .load(uri)
                        .circleCrop()
                        .into(imageView)
                }

                Toast.makeText(this, "✅ Foto seleccionada", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * ⭐ MODIFICADO: Genera código QR + sube foto si existe
     */
    private fun generarQRParaTerminal(nombreVendedor: String, fotoUri: Uri?) {
        val uid = auth.currentUser?.uid ?: return
        val pin = generarPINAleatorio()

        val datosQR = JSONObject().apply {
            put("tipo", "crystal_terminal")
            put("uid", uid)
            put("pin", pin)
            put("nombre", nombreVendedor)
            put("validez", System.currentTimeMillis() + (24 * 60 * 60 * 1000))
        }

        // Guardar código temporal
        db.collection("usuarios")
            .document(uid)
            .collection("plan_ventas")
            .document("dispositivos")
            .collection("codigos_temporales")
            .document(pin)
            .set(mapOf(
                "pin" to pin,
                "nombre_terminal" to nombreVendedor,
                "validez" to System.currentTimeMillis() + (24 * 60 * 60 * 1000),
                "usado" to false,
                "creado_en" to System.currentTimeMillis()
            ))
            .addOnSuccessListener {
                // ⭐ Subir foto si existe
                if (fotoUri != null) {
                    subirFotoTerminal(nombreVendedor, fotoUri)
                }

                // Generar QR
                val bitmap = generarImagenQR(datosQR.toString(), 512, 512)
                if (bitmap != null) {
                    mostrarDialogoQR(bitmap, pin, nombreVendedor)
                } else {
                    Toast.makeText(this, "❌ Error generando QR", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Genera imagen de código QR
     */
    private fun generarImagenQR(contenido: String, width: Int, height: Int): Bitmap? {
        return try {
            val hints = hashMapOf<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 1

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(
                contenido,
                BarcodeFormat.QR_CODE,
                width,
                height,
                hints
            )

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(
                        x,
                        y,
                        if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                    )
                }
            }

            bitmap

        } catch (e: WriterException) {
            Log.e("QRGenerator", "Error generando QR: ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e("QRGenerator", "Error inesperado: ${e.message}", e)
            null
        }
    }

    /**
     * Muestra diálogo con código QR generado
     */
    private fun mostrarDialogoQR(bitmap: Bitmap, pin: String, nombreVendedor: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_mostrar_qr, null)
        val ivQR = dialogView.findViewById<ImageView>(R.id.ivQR)
        val tvInstrucciones = dialogView.findViewById<TextView>(R.id.tvInstrucciones)
        val tvPIN = dialogView.findViewById<TextView>(R.id.tvPIN)
        val tvValidez = dialogView.findViewById<TextView>(R.id.tvValidez)

        ivQR.setImageBitmap(bitmap)

        val horaExpiracion = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fechaExpiracion = sdf.format(Date(horaExpiracion))

        tvInstrucciones.text = """
        Terminal: $nombreVendedor
        
        1. Abre Crystal POS en el terminal
        2. Selecciona "Acceso Terminal"
        3. Escanea este código QR
    """.trimIndent()

        tvPIN.text = "PIN de respaldo: $pin"
        tvValidez.text = "⏰ Válido hasta: $fechaExpiracion"

        AlertDialog.Builder(this)
            .setTitle("📱 Código QR Generado")
            .setView(dialogView)
            .setPositiveButton("Cerrar", null)
            .setNeutralButton("Compartir PIN") { _, _ ->
                compartirPIN(pin, nombreVendedor)
            }
            .show()
    }

    /**
     * ⭐ MODIFICADO: Genera PIN + sube foto si existe
     */
    private fun generarPINParaTerminal(nombreVendedor: String, fotoUri: Uri?) {
        val uid = auth.currentUser?.uid ?: return
        val pin = generarPINAleatorio()

        // Guardar código temporal
        db.collection("usuarios")
            .document(uid)
            .collection("plan_ventas")
            .document("dispositivos")
            .collection("codigos_temporales")
            .document(pin)
            .set(mapOf(
                "pin" to pin,
                "nombre_terminal" to nombreVendedor,
                "validez" to System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000),
                "usado" to false,
                "creado_en" to System.currentTimeMillis()
            ))
            .addOnSuccessListener {
                // ⭐ Subir foto si existe
                if (fotoUri != null) {
                    subirFotoTerminal(nombreVendedor, fotoUri)
                }

                mostrarDialogoPIN(pin, nombreVendedor)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    /**
     * ⭐ NUEVO: Sube foto del terminal a Firebase Storage
     */
    /**
     * ⭐ MODIFICADO: Sube foto a Storage Y guarda referencia en Firestore
     */
    private fun subirFotoTerminal(nombreVendedor: String, fotoUri: Uri) {
        val uid = auth.currentUser?.uid ?: return

        lifecycleScope.launch {
            try {
                Log.d("GestionDispositivos", "📸 Subiendo foto para: $nombreVendedor")

                // Crear referencia única en Storage
                val storageRef = storage.reference
                val timestamp = System.currentTimeMillis()
                val fotoRef = storageRef.child("terminales/$uid/${nombreVendedor}_${timestamp}.jpg")

                // Subir foto
                withContext(Dispatchers.IO) {
                    fotoRef.putFile(fotoUri).await()
                }

                // Obtener URL de descarga
                val downloadUrl = withContext(Dispatchers.IO) {
                    fotoRef.downloadUrl.await()
                }

                Log.d("GestionDispositivos", "✅ Foto subida: $downloadUrl")

                // ⭐ GUARDAR EN FIRESTORE (colección temporal)
                db.collection("usuarios")
                    .document(uid)
                    .collection("plan_ventas")
                    .document("dispositivos")
                    .collection("fotos_temporales")
                    .add(mapOf(
                        "nombre_terminal" to nombreVendedor,
                        "foto_url" to downloadUrl.toString(),
                        "creado_en" to System.currentTimeMillis(),
                        "validez" to System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000) // 7 días
                    ))
                    .await()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@GestionDispositivosActivity,
                        "✅ Foto guardada",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Log.e("GestionDispositivos", "❌ Error subiendo foto: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@GestionDispositivosActivity,
                        "⚠️ Error subiendo foto: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    /**
     * Muestra diálogo con PIN generado
     */
    private fun mostrarDialogoPIN(pin: String, nombreVendedor: String) {
        val mensaje = """
        Terminal: $nombreVendedor
        
        PIN: $pin
        
        📱 Instrucciones:
        1. Abre Crystal POS en el terminal
        2. Selecciona "Acceso Terminal"
        3. Cambia a método PIN
        4. Ingresa el código: $pin
        
        ⏰ Válido por 7 días
    """.trimIndent()

        AlertDialog.Builder(this)
            .setTitle("🔐 PIN Generado")
            .setMessage(mensaje)
            .setPositiveButton("Cerrar", null)
            .setNeutralButton("Compartir") { _, _ ->
                compartirPIN(pin, nombreVendedor)
            }
            .show()
    }
    /**
     * Genera PIN aleatorio de 6 dígitos
     */
    private fun generarPINAleatorio(): String {
        return (100000..999999).random().toString()
    }
    /**
     * Comparte el PIN por WhatsApp, SMS, etc.
     */
    private fun compartirPIN(pin: String, nombreVendedor: String) {
        val mensaje = """
        🏪 CRYSTAL POS - Acceso Terminal
        
        Terminal: $nombreVendedor
        PIN: $pin
        
        Para iniciar sesión:
        1. Abre Crystal POS en el terminal
        2. Selecciona "Acceso Terminal"
        3. Ingresa el PIN
    """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, mensaje)
        }
        startActivity(Intent.createChooser(intent, "Compartir PIN"))
    }
    @SuppressLint("HardwareIds")
    private fun verificarEstadoDispositivo() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val deviceId = android.provider.Settings.Secure.getString(
                    contentResolver,
                    android.provider.Settings.Secure.ANDROID_ID
                )

                val dispositivoDoc = db.collection("usuarios")
                    .document(uid)
                    .collection("plan_ventas")
                    .document("dispositivos")
                    .collection("autorizados")
                    .document(deviceId)
                    .get()
                    .await()

                withContext(Dispatchers.Main) {
                    if (dispositivoDoc.exists()) {
                        val rol = dispositivoDoc.getString("rol") ?: "DESCONOCIDO"
                        val esPatron = dispositivoDoc.getBoolean("es_patron") ?: false

                        if (esPatron || rol == "PATRON") {
                            // Ya es patrón, ocultar botón
                            binding.btnConvertirEnPatron.visibility = View.GONE
                        } else {
                            // No es patrón, mostrar botón
                            binding.btnConvertirEnPatron.visibility = View.VISIBLE
                        }
                    } else {
                        // No está registrado, mostrar botón
                        binding.btnConvertirEnPatron.visibility = View.VISIBLE
                    }
                }

            } catch (e: Exception) {
                Log.e("GestionDispositivos", "Error verificando estado: ${e.message}", e)
            }
        }
    }

    /**
     * Muestra diálogo de confirmación para conversión a patrón
     */
    private fun mostrarDialogoConversionPatron() {
        AlertDialog.Builder(this)
            .setTitle("📱 Cambiar dispositivo patrón")
            .setMessage(
                """
            ¿Deseas convertir este dispositivo en el nuevo PATRÓN?
            
            ✅ Beneficios:
            • Tendrás acceso completo
            • Podrás gestionar terminales
            • Configurar datos de empresa
            
            ℹ️ Importante:
            • Los terminales seguirán funcionando
            • No necesitan volver a autorizarse
            • Este es útil si cambiaste de celular
            """.trimIndent()
            )
            .setPositiveButton("Sí, convertir") { _, _ ->
                realizarConversionAPatron()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    /**
     * Convierte el dispositivo actual en PATRÓN
     */
    @SuppressLint("HardwareIds")
    private fun realizarConversionAPatron() {
        // Mostrar progress
        binding.swipeRefresh.isRefreshing = true
        binding.btnConvertirEnPatron.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val uid = auth.currentUser?.uid
                if (uid == null) {
                    withContext(Dispatchers.Main) {
                        binding.swipeRefresh.isRefreshing = false
                        binding.btnConvertirEnPatron.isEnabled = true
                        Toast.makeText(
                            this@GestionDispositivosActivity,
                            "Error: Debes iniciar sesión primero",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@launch
                }

                val deviceId = android.provider.Settings.Secure.getString(
                    contentResolver,
                    android.provider.Settings.Secure.ANDROID_ID
                )

                Log.d("GestionDispositivos", "════════════════════════════════")
                Log.d("GestionDispositivos", "🔄 CONVIRTIENDO EN PATRÓN")
                Log.d("GestionDispositivos", "   UID: $uid")
                Log.d("GestionDispositivos", "   DeviceID: $deviceId")
                Log.d("GestionDispositivos", "════════════════════════════════")

                // Crear/actualizar este dispositivo como PATRÓN
                db.collection("usuarios")
                    .document(uid)
                    .collection("plan_ventas")
                    .document("dispositivos")
                    .collection("autorizados")
                    .document(deviceId)
                    .set(
                        mapOf(
                            "deviceId" to deviceId,
                            "rol" to "PATRON",
                            "activo" to true,
                            "nombre_vendedor" to "Administrador",
                            "marca" to Build.MANUFACTURER,
                            "modelo" to Build.MODEL,
                            "android" to Build.VERSION.SDK_INT.toString(),
                            "autorizado_en" to System.currentTimeMillis(),
                            "es_patron" to true,
                            "tipo_autorizacion" to "conversion"
                        )
                    )
                    .await()

                // Actualizar SharedPreferences
                sharedPreferences.edit()
                    .putBoolean("es_terminal_pin", false)  // Ya no es terminal
                    .putString("patron_uid", uid)  // Guardar propio UID
                    .putString("session_type", "PATRON")
                    .apply()

                Log.d("GestionDispositivos", "✅ Dispositivo convertido a PATRÓN")

                withContext(Dispatchers.Main) {
                    binding.swipeRefresh.isRefreshing = false

                    AlertDialog.Builder(this@GestionDispositivosActivity)
                        .setTitle("✅ Conversión exitosa")
                        .setMessage(
                            """
                        Este dispositivo ahora es el PATRÓN.
                        
                        Ahora puedes:
                        • Gestionar terminales
                        • Configurar empresa
                        • Generar códigos QR/PIN
                        
                        Los terminales existentes seguirán funcionando normalmente.
                        
                        La app se reiniciará.
                        """.trimIndent()
                        )
                        .setPositiveButton("Entendido") { _, _ ->
                            reiniciarApp()
                        }
                        .setCancelable(false)
                        .show()
                }

            } catch (e: Exception) {
                Log.e("GestionDispositivos", "❌ Error convirtiendo a patrón: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    binding.swipeRefresh.isRefreshing = false
                    binding.btnConvertirEnPatron.isEnabled = true
                    Toast.makeText(
                        this@GestionDispositivosActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Reinicia la aplicación
     */
    private fun reiniciarApp() {
        val intent = Intent(this, crystal.crystal.MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}