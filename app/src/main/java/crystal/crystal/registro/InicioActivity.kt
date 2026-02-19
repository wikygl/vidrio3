package crystal.crystal.registro

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import crystal.crystal.MainActivity
import crystal.crystal.R
import crystal.crystal.databinding.ActivityInicioActtivityBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class InicioActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val GOOGLE_SIGN_IN_CODE = 100
    private lateinit var binding: ActivityInicioActtivityBinding
    private val db by lazy { FirebaseFirestore.getInstance() }
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val TAG = "InicioActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioActtivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verificarUsuario()

        binding.lyFree.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        binding.lyGoogle.setOnClickListener { iniciarSesionConGoogle() }

        try { ani(binding.lo, R.raw.tee) } catch (_: Throwable) {}
        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Verificar si ya hay sesión activa
        verificarSesionExistente()
        // DEBE ESTAR DESPUÉS DE: verificarSesionExistente()

        val sessionPrefs = getSharedPreferences("session_prefs", MODE_PRIVATE)
        val sessionType = sessionPrefs.getString("session_type", null)
        val patronUid = sessionPrefs.getString("patron_uid", null)
        Log.d(TAG, "🔍 onCreate - sessionType: $sessionType, patronUid: $patronUid")

        // Agregar botón para acceso terminal
        binding.btnAccesoTerminal.setOnClickListener {
            abrirLoginTerminal()
        }
    }

    private fun ani(imageView: LottieAnimationView, animacion: Int) {
        imageView.setAnimation(animacion)
        imageView.repeatCount = 9
        imageView.playAnimation()
    }

    private fun verificarUsuario() {
        val usuario = auth.currentUser
        if (usuario != null) {
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            if (BootstrapGate.localSaysBootstrapOk(this, usuario.uid, deviceId)) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    private fun iniciarSesionConGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(this, gso)

        // 👇 Importante: limpiar la sesión de Google para forzar el selector de cuentas
        client.signOut().addOnCompleteListener {
            // Cuando termina el signOut, ahora sí lanzamos el flujo de login
            startActivityForResult(client.signInIntent, GOOGLE_SIGN_IN_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { t ->
                    if (t.isSuccessful) {
                        lifecycleScope.launchWhenStarted {
                            manejarIngresoPostLogin(account)
                        }
                    } else {
                        Toast.makeText(this, "Firebase auth falló", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun manejarIngresoPostLogin(account: GoogleSignInAccount?) {
        val current = auth.currentUser ?: run {
            Toast.makeText(this, "Sesión inválida", Toast.LENGTH_LONG).show()
            return
        }
        val uid = current.uid
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        // ⭐ VERIFICAR SI ES PLAN VENTAS
        try {
            val estadoDoc = db.collection("usuarios").document(uid).get().await()
            val estadoServicio = estadoDoc.get("estado_servicio") as? Map<*, *>
            val modo = estadoServicio?.get("mode") as? String ?: "BASIC"

            if (modo == "VENTAS") {
                // ⭐ PLAN VENTAS: Verificar autorización de dispositivo
                val autorizacionResult = verificarYAutorizarDispositivo(uid, deviceId)

                when (autorizacionResult) {
                    ResultadoAutorizacion.AUTORIZADO -> {
                        // Ya estaba autorizado, continuar normal
                    }
                    ResultadoAutorizacion.AUTO_AUTORIZADO -> {
                        // Se auto-autorizó ahora
                        Toast.makeText(
                            this,
                            "✅ Terminal autorizada automáticamente",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    ResultadoAutorizacion.SIN_SLOTS -> {
                        // Ya no hay terminales disponibles
                        mostrarDialogoSinSlots(uid, deviceId)
                        return
                    }
                    ResultadoAutorizacion.BLOQUEADO -> {
                        // Dispositivo explícitamente bloqueado
                        mostrarDialogoBloqueado()
                        return
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error verificando plan VENTAS: ${e.message}", e)
        }

        // Continuar con bootstrap normal (para VENTAS autorizado, FULL y BASIC)
        if (BootstrapGate.localSaysBootstrapOk(this, uid, deviceId)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val dlg = crearDialogoCarga()
        dlg.show()
        var huboFallo = false

        try {
            // Paso 1: asegurar perfil
            try {
                UsuarioBootstrap.asegurarPerfilUsuario(this, account)
            } catch (e: Exception) {
                huboFallo = true
                Log.e(TAG, "Paso1 FALLÓ: asegurarPerfilUsuario -> ${e.message}", e)
            }

            // Paso 2: vincular dispositivo
            try {
                VinculadorDispositivo.vincular(uid, deviceId, this)
            } catch (e: Exception) {
                huboFallo = true
                Log.e(TAG, "Paso2 FALLÓ: vincular dispositivo -> ${e.message}", e)
            }

            // Paso 3: recalcular estado
            try {
                UsuarioBootstrap.recalcularEstadoServicio(this)
            } catch (e: Exception) {
                huboFallo = true
                Log.e(TAG, "Paso3 FALLÓ: recalcularEstadoServicio -> ${e.message}", e)
            }

            // Paso 4: bandera servidor
            try {
                db.collection("usuarios").document(uid)
                    .set(
                        mapOf(
                            "bootstrap" to mapOf(
                                "ok" to true,
                                "deviceId" to deviceId,
                                "doneAt" to FieldValue.serverTimestamp(),
                                "version" to 1
                            )
                        ),
                        SetOptions.merge()
                    ).await()
            } catch (e: Exception) {
                huboFallo = true
                Log.e(TAG, "Paso4 FALLÓ: bandera servidor -> ${e.message}", e)
            }

            // Paso 5: bandera local
            try {
                BootstrapGate.markBootstrapOk(this, uid, deviceId)
            } catch (e: Exception) {
                huboFallo = true
                Log.e(TAG, "Paso5 FALLÓ: bandera local -> ${e.message}", e)
            }

        } finally {
            dlg.dismiss()
            if (huboFallo) {
                Toast.makeText(
                    this,
                    "No pudimos preparar todo. Continuamos y sincronizamos en segundo plano.",
                    Toast.LENGTH_LONG
                ).show()
            }
            // Pase lo que pase, entra al Main
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun crearDialogoCarga(): AlertDialog {
        val pb = android.widget.ProgressBar(this)
        return AlertDialog.Builder(this)
            .setTitle("Preparando tu cuenta…")
            .setView(pb)
            .setCancelable(false)
            .create()
    }
    /**
     * Verifica si el dispositivo está autorizado para plan VENTAS
     * Si no lo está, intenta auto-autorizarlo si hay slots disponibles
     */
    private suspend fun verificarYAutorizarDispositivo(
        uid: String,
        deviceId: String
    ): ResultadoAutorizacion {
        return withContext(Dispatchers.IO) {
            try {
                val dispositivoRef = db.collection("usuarios")
                    .document(uid)
                    .collection("plan_ventas")
                    .document("dispositivos")
                    .collection("autorizados")
                    .document(deviceId)

                val dispositivoDoc = dispositivoRef.get().await()

                // Si ya está autorizado, OK
                if (dispositivoDoc.exists() && dispositivoDoc.getBoolean("activo") == true) {
                    return@withContext ResultadoAutorizacion.AUTORIZADO
                }

                // Si no existe, verificar si hay slots disponibles
                val configRef = db.collection("usuarios")
                    .document(uid)
                    .collection("plan_ventas")
                    .document("config")

                val configDoc = configRef.get().await()
                val terminalesContratadas = configDoc.getLong("terminales_contratadas")?.toInt() ?: 1
                val terminalesAutorizadas = configDoc.getLong("terminales_autorizadas")?.toInt() ?: 0

                if (terminalesAutorizadas >= terminalesContratadas) {
                    // Ya no hay slots disponibles
                    return@withContext ResultadoAutorizacion.SIN_SLOTS
                }

                // ⭐ NUEVO: Determinar rol del dispositivo
                val esPatron = (terminalesAutorizadas == 0)  // El primero es PATRON
                val rol = if (esPatron) "PATRON" else "TERMINAL"

                // Hay slots disponibles: auto-autorizar
                db.runTransaction { tx ->
                    // Crear dispositivo autorizado
                    tx.set(dispositivoRef, mapOf(
                        "deviceId" to deviceId,
                        "marca" to android.os.Build.MANUFACTURER,
                        "modelo" to android.os.Build.MODEL,
                        "android" to android.os.Build.VERSION.RELEASE,
                        "autorizado_en" to FieldValue.serverTimestamp(),
                        "activo" to true,
                        "tipo_autorizacion" to "auto",
                        "rol" to rol  // ⭐ NUEVO: Asignar rol
                    ))

                    // Incrementar contador
                    tx.update(configRef, mapOf(
                        "terminales_autorizadas" to FieldValue.increment(1)
                    ))

                    // ⭐ NUEVO: Si es PATRON, guardar en config
                    if (esPatron) {
                        tx.update(configRef, mapOf(
                            "dispositivo_patron" to deviceId
                        ))
                    }
                }.await()

                return@withContext ResultadoAutorizacion.AUTO_AUTORIZADO

            } catch (e: Exception) {
                Log.e(TAG, "Error verificando dispositivo: ${e.message}", e)
                return@withContext ResultadoAutorizacion.BLOQUEADO
            }
        }
    }
    /**
     * Muestra diálogo cuando no hay slots disponibles
     */
    private fun mostrarDialogoSinSlots(uid: String, deviceId: String) {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Límite de terminales alcanzado")
            .setMessage(
                """
            Has alcanzado el límite de terminales de tu plan VENTAS.
            
            Opciones:
            1. Desautoriza una terminal desde el panel de administración
            2. Actualiza tu plan para más terminales (S/ 10 por terminal adicional)
            
            Device ID: ${deviceId.take(8)}...
            """.trimIndent()
            )
            .setPositiveButton("Actualizar plan") { _, _ ->
                startActivity(Intent(this, PlanSelectionActivity::class.java))
                finish()
            }
            .setNegativeButton("Salir") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }
    /**
     * Muestra diálogo de dispositivo bloqueado
     */
    private fun mostrarDialogoBloqueado() {
        AlertDialog.Builder(this)
            .setTitle("🚫 Dispositivo no autorizado")
            .setMessage("Este dispositivo no está autorizado para usar Crystal con plan VENTAS.")
            .setPositiveButton("Salir") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    enum class ResultadoAutorizacion {
        AUTORIZADO,       // Ya estaba autorizado
        AUTO_AUTORIZADO,  // Se auto-autorizó ahora
        SIN_SLOTS,        // No hay slots disponibles
        BLOQUEADO         // Error o bloqueado explícitamente
    }
    /**
     * Verifica si existe una sesión activa (Google o Terminal)
     */
    private fun verificarSesionExistente() {
        val sessionType = sharedPreferences.getString("session_type", null)

        when (sessionType) {
            "TERMINAL" -> {
                val patronUid = sharedPreferences.getString("patron_uid", null)

                if (patronUid != null) {
                    irAMainActivity()
                }
            }

            "GOOGLE" -> {
                if (auth.currentUser != null) {
                    irAMainActivity()
                } else {
                    sharedPreferences.edit().clear().apply()
                }
            }
        }
    }

    private fun abrirLoginTerminal() {
        startActivity(Intent(this, TerminalLoginActivity::class.java))
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
}
