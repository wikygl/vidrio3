package crystal.crystal.registro

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.zxing.integration.android.IntentIntegrator
import crystal.crystal.MainActivity
import crystal.crystal.databinding.ActivityTerminalLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.google.firebase.functions.ktx.functions

class TerminalLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTerminalLoginBinding
    private val db = Firebase.firestore
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTerminalLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        // Mostrar método QR por defecto
        mostrarMetodoQR()
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun setupListeners() {
        // Botón para escanear QR
        binding.btnEscanearQR.setOnClickListener {
            iniciarEscaneoQR()
        }

        // Toggle para cambiar a método PIN
        binding.btnCambiarAPIN.setOnClickListener {
            mostrarMetodoPIN()
        }

        // Toggle para cambiar a método QR
        binding.btnCambiarAQR.setOnClickListener {
            mostrarMetodoQR()
        }

        // Botón para login con PIN
        binding.btnLoginPIN.setOnClickListener {
            val pin = binding.etPIN.text.toString().trim()
            if (validarPIN(pin)) {
                loginConPIN(pin)
            }
        }

        // Botón volver (login con Google)
        binding.btnVolverGoogle.setOnClickListener {
            finish() // Vuelve a InicioActivity
        }
    }

    /**
     * Muestra interfaz de método QR
     */
    private fun mostrarMetodoQR() {
        binding.layoutQR.visibility = View.VISIBLE
        binding.layoutPIN.visibility = View.GONE
    }

    /**
     * Muestra interfaz de método PIN
     */
    private fun mostrarMetodoPIN() {
        binding.layoutQR.visibility = View.GONE
        binding.layoutPIN.visibility = View.VISIBLE
        binding.etPIN.requestFocus()
    }

    /**
     * Inicia el escaneo de código QR
     */
    private fun iniciarEscaneoQR() {
        IntentIntegrator(this).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            setPrompt("Escanea el código QR generado por el administrador")
            setBeepEnabled(true)
            setCameraId(0)  // Cámara trasera
            setBarcodeImageEnabled(false)
            setOrientationLocked(false)
            initiateScan()
        }
    }

    /**
     * Maneja el resultado del escaneo QR
     */
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Escaneo cancelado", Toast.LENGTH_SHORT).show()
            } else {
                // QR escaneado exitosamente
                procesarQR(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Procesa el contenido del QR escaneado
     */
    private fun procesarQR(contenidoQR: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnEscanearQR.isEnabled = false

        lifecycleScope.launch {
            try {
                val exitoso = autorizarConQR(contenidoQR)

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnEscanearQR.isEnabled = true

                    if (exitoso) {
                        mostrarExito("Terminal autorizado correctamente")
                        irAMainActivity()
                    } else {
                        Toast.makeText(
                            this@TerminalLoginActivity,
                            "❌ Error al autorizar terminal",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnEscanearQR.isEnabled = true
                    Toast.makeText(
                        this@TerminalLoginActivity,
                        "❌ Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Autoriza el terminal usando datos del QR
     */
    @SuppressLint("HardwareIds")
    private suspend fun autorizarConQR(datosQR: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val json = JSONObject(datosQR)

                // Validar estructura
                if (!json.has("tipo") || json.getString("tipo") != "crystal_terminal") {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@TerminalLoginActivity,
                            "⚠️ Código QR inválido",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@withContext false
                }

                // Validar vigencia
                val validez = json.getLong("validez")
                if (validez < System.currentTimeMillis()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@TerminalLoginActivity,
                            "⚠️ Código QR expirado (válido por 24h)",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@withContext false
                }

                val uid = json.getString("uid")
                val pin = json.getString("pin")
                val nombre = json.getString("nombre")

                // Verificar código temporal en Firestore
                val codigoDoc = db.collection("usuarios")
                    .document(uid)
                    .collection("plan_ventas")
                    .document("dispositivos")
                    .collection("codigos_temporales")
                    .document(pin)
                    .get()
                    .await()

                if (!codigoDoc.exists()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@TerminalLoginActivity,
                            "⚠️ Código inválido o ya usado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@withContext false
                }

                val usado = codigoDoc.getBoolean("usado") ?: false
                if (usado) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@TerminalLoginActivity,
                            "⚠️ Este código ya fue utilizado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return@withContext false
                }

                // Marcar código como usado
                codigoDoc.reference.update("usado", true).await()

                // Autorizar dispositivo
                val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                db.collection("usuarios")
                    .document(uid)
                    .collection("plan_ventas")
                    .document("dispositivos")
                    .collection("autorizados")
                    .document(deviceId)
                    .set(
                        mapOf(
                            "deviceId" to deviceId,
                            "rol" to "TERMINAL",
                            "activo" to true,
                            "nombre_vendedor" to nombre,
                            "marca" to android.os.Build.MANUFACTURER,
                            "modelo" to android.os.Build.MODEL,
                            "pin_terminal" to pin,
                            "autorizado_en" to System.currentTimeMillis(),
                            "metodo_autorizacion" to "QR"
                        )
                    )
                    .await()

                // Guardar sesión local (SIN Google Sign-In)
                sharedPreferences.edit()
                    .putString("terminal_uid", uid)
                    .putString("terminal_device_id", deviceId)
                    .putString("terminal_nombre", nombre)
                    .putString("terminal_pin", pin)
                    .putBoolean("es_terminal_pin", true)
                    .putString("patron_uid", uid)  // ← Importante para sincronización
                    .apply()

                // ✅ Autenticar anónimamente para poder leer Firestore
                val autenticado = autenticarTerminalAnonimo()
                if (!autenticado) {
                    Log.e("TerminalLogin", "⚠️ Terminal autorizado pero sin autenticación Firebase")
                }

                Log.d("TerminalLogin", "✅ Terminal autorizado: $nombre")
                true

            } catch (e: Exception) {
                Log.e("TerminalLogin", "Error autorizando con QR: ${e.message}", e)
                false
            }
        }
    }

    /**
     * Valida formato del PIN
     */
    private fun validarPIN(pin: String): Boolean {
        return when {
            pin.isEmpty() -> {
                binding.etPIN.error = "Ingresa el PIN"
                false
            }
            pin.length != 6 -> {
                binding.etPIN.error = "El PIN debe tener 6 dígitos"
                false
            }
            !pin.all { it.isDigit() } -> {
                binding.etPIN.error = "Solo números permitidos"
                false
            }
            else -> {
                binding.etPIN.error = null
                true
            }
        }
    }

    /**
     * Login usando PIN de 6 dígitos
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private fun loginConPIN(pin: String) {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLoginPIN.isEnabled = false

        lifecycleScope.launch {
            try {
                val exitoso = autorizarConPIN(pin)

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLoginPIN.isEnabled = true

                    if (exitoso) {
                        mostrarExito("Terminal autorizado correctamente")
                        irAMainActivity()
                    } else {
                        Toast.makeText(
                            this@TerminalLoginActivity,
                            "❌ PIN incorrecto o terminal no autorizado",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLoginPIN.isEnabled = true
                    Toast.makeText(
                        this@TerminalLoginActivity,
                        "❌ Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    /**
     * Autoriza el terminal usando PIN con Cloud Function
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("HardwareIds")
    private suspend fun autorizarConPIN(pin: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

                Log.d(TAG, "════════════════════════════════════════")
                Log.d(TAG, "🔍 LLAMANDO CLOUD FUNCTION")
                Log.d(TAG, "════════════════════════════════════════")
                Log.d(TAG, "📌 PIN: $pin")
                Log.d(TAG, "📱 DeviceID: $deviceId")

                // Preparar datos para la función
                val data = hashMapOf(
                    "pin" to pin,
                    "deviceId" to deviceId,
                    "marca" to Build.MANUFACTURER,
                    "modelo" to Build.MODEL,
                    "android" to Build.VERSION.SDK_INT.toString()
                )

                Log.d(TAG, "📦 Datos: $data")
                Log.d(TAG, "")
                Log.d(TAG, "⏳ Esperando respuesta...")

                // Llamar a la Cloud Function
                val functions = Firebase.functions
                val result = functions
                    .getHttpsCallable("autorizarTerminalConPIN")
                    .call(data)
                    .await()

                // Procesar respuesta
                val response = result.data as? Map<*, *>

                Log.d(TAG, "")
                Log.d(TAG, "📨 RESPUESTA RECIBIDA")
                Log.d(TAG, "════════════════════════════════════════")
                Log.d(TAG, "Response completo: $response")

                val success = response?.get("success") as? Boolean ?: false

                if (success) {
                    val patronUid = response?.get("patronUid") as? String ?: ""
                    val nombreVendedor = response?.get("nombreVendedor") as? String ?: "Terminal"
                    val rol = response?.get("rol") as? String ?: "TERMINAL"
                    val fotoUrl = response?.get("fotoUrl") as? String

                    Log.d(TAG, "✅ AUTORIZACIÓN EXITOSA")
                    Log.d(TAG, "   Patron UID: $patronUid")
                    Log.d(TAG, "   Nombre: $nombreVendedor")
                    Log.d(TAG, "   Rol: $rol")
                    Log.d(TAG, "   Foto URL: $fotoUrl")
                    Log.d(TAG, "════════════════════════════════════════")

                    // Guardar sesión CON foto_url
                    guardarSesionTerminal(patronUid, nombreVendedor, fotoUrl)

                    // ✅ Autenticar anónimamente para poder leer Firestore
                    val autenticado = autenticarTerminalAnonimo()
                    if (!autenticado) {
                        Log.e(TAG, "⚠️ Terminal autorizado pero sin autenticación Firebase")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                this@TerminalLoginActivity,
                                "⚠️ Autorizado. Presiona Sincronizar cuando tengas internet.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                    true
                } else {
                    val error = response?.get("error") as? String ?: "Error desconocido"

                    Log.e(TAG, "")
                    Log.e(TAG, "❌ AUTORIZACIÓN FALLIDA")
                    Log.e(TAG, "════════════════════════════════════════")
                    Log.e(TAG, "Error: $error")
                    Log.e(TAG, "════════════════════════════════════════")

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@TerminalLoginActivity,
                            error,
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    false
                }

            } catch (e: Exception) {
                Log.e(TAG, "")
                Log.e(TAG, "💥 EXCEPCIÓN EN CLOUD FUNCTION")
                Log.e(TAG, "════════════════════════════════════════")
                Log.e(TAG, "Tipo: ${e.javaClass.simpleName}")
                Log.e(TAG, "Mensaje: ${e.message}")
                Log.e(TAG, "Stack trace:", e)
                Log.e(TAG, "════════════════════════════════════════")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@TerminalLoginActivity,
                        "Error de conexión: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                false
            }
        }
    }

    /**
     * Autentica el terminal anónimamente con Firebase Auth
     * Necesario para que pueda leer datos de Firestore
     */
    private suspend fun autenticarTerminalAnonimo(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "════════════════════════════════════════")
                Log.d(TAG, "🔐 AUTENTICANDO TERMINAL ANÓNIMAMENTE")
                Log.d(TAG, "════════════════════════════════════════")

                FirebaseAuth.getInstance()
                    .signInAnonymously()
                    .await()

                val authUid = FirebaseAuth.getInstance().currentUser?.uid

                Log.d(TAG, "✅ Terminal autenticado anónimamente")
                Log.d(TAG, "   Auth UID: $authUid")
                Log.d(TAG, "════════════════════════════════════════")

                true

            } catch (e: Exception) {
                Log.e(TAG, "════════════════════════════════════════")
                Log.e(TAG, "❌ ERROR AUTENTICACIÓN ANÓNIMA")
                Log.e(TAG, "════════════════════════════════════════")
                Log.e(TAG, "Mensaje: ${e.message}", e)
                Log.e(TAG, "════════════════════════════════════════")

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@TerminalLoginActivity,
                        "Error de autenticación: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                false
            }
        }
    }

    /**
     * Muestra mensaje de éxito
     */
    private fun mostrarExito(mensaje: String) {
        Toast.makeText(
            this,
            "✅ $mensaje",
            Toast.LENGTH_LONG
        ).show()
    }

    /**
     * Navega a MainActivity
     */
    private fun irAMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Guarda sesión CON foto_url
     */
    private fun guardarSesionTerminal(
        patronUid: String,
        nombreVendedor: String,
        fotoUrl: String? = null
    ) {
        Log.d(TAG, "═══════════════════════════════════")
        Log.d(TAG, "💾 GUARDANDO SESIÓN TERMINAL")
        Log.d(TAG, "Patron UID: $patronUid")
        Log.d(TAG, "Nombre: $nombreVendedor")
        Log.d(TAG, "Foto URL: $fotoUrl")

        val sharedPref = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        with(sharedPref.edit()) {
            putString("session_type", "TERMINAL")
            putString("patron_uid", patronUid)
            putString("nombre_vendedor", nombreVendedor)
            putBoolean("es_terminal_pin", true)
            putString("usuario_estado", "VENTAS")
            putLong("session_timestamp", System.currentTimeMillis())

            // Guardar foto URL
            if (fotoUrl != null && fotoUrl.isNotEmpty()) {
                putString("foto_url", fotoUrl)
                Log.d(TAG, "✅ Foto URL guardada en SharedPreferences")
            } else {
                remove("foto_url")
                Log.d(TAG, "⚠️ Sin foto URL")
            }

            val success = commit()
            Log.d(TAG, "✅ Commit: $success")
        }

        // Verificación
        val verificacion = sharedPref.getString("session_type", null)
        val verificacionFoto = sharedPref.getString("foto_url", null)
        Log.d(TAG, "✅ Verificado session_type: $verificacion")
        Log.d(TAG, "✅ Verificado foto_url: $verificacionFoto")
        Log.d(TAG, "═══════════════════════════════════")
    }
}