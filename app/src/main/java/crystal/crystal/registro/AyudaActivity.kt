@file:Suppress("DEPRECATION")

package crystal.crystal.registro

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import crystal.crystal.R
import crystal.crystal.databinding.ActivityAyudaBinding
import kotlin.math.max

class AyudaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAyudaBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    // Parámetros de seguridad – mismos que PinAuthActivity
    private val pinLength = 4
    private val maxAttemptsPin = 5
    private val lockMinutesPin = 5L
    private val iterPBKDF2 = 10_000

    // Launcher para LOGIN con Google (solo para esta Activity)
    private val lanzadorLogin =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            procesarResultadoLogin(result.resultCode, result.data)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAyudaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar
        setSupportActionBar(binding.toolbarAyuda)
        supportActionBar?.title = "Ayuda"
        binding.toolbarAyuda.setTitleTextColor(Color.WHITE)

        // Tabs del toolbar
        binding.tabAyudaToolbar.setOnClickListener { mostrarSeccionAyuda() }
        binding.tvTabAyuda.setOnClickListener { mostrarSeccionAyuda() }
        binding.tabTutosToolbar.setOnClickListener { mostrarSeccionTutos() }
        binding.tvTabTutos.setOnClickListener { mostrarSeccionTutos() }

        // Botón "Iniciar sesión" – login Google de la cuenta a bloquear
        binding.btnIniciarSesion.setOnClickListener {
            iniciarSesionGoogleParaBloqueo()
        }

        // Botón rojo – pide PIN y bloquea la wallet de la cuenta actualmente logueada
        binding.btnBloquearEmergencia.setOnClickListener {
            pedirPinYValidarAntesDeBloquear()
        }

        // Hasta que no haya login de la cuenta objetivo, no se puede usar el botón rojo
        binding.btnBloquearEmergencia.visibility = View.GONE

        // Por defecto mostramos la sección de Ayuda
        mostrarSeccionAyuda()
    }

    // =================== Tabs: Ayuda / Tutos ===================

    private fun mostrarSeccionAyuda() {
        binding.seccionAyuda.visibility = View.VISIBLE
        binding.seccionTutos.visibility = View.GONE
        actualizarEstadoTabsToolbar(activaAyuda = true)
    }

    private fun mostrarSeccionTutos() {
        binding.seccionAyuda.visibility = View.GONE
        binding.seccionTutos.visibility = View.VISIBLE
        actualizarEstadoTabsToolbar(activaAyuda = false)
    }

    private fun actualizarEstadoTabsToolbar(activaAyuda: Boolean) {
        val fondoActivo = 0x33FFFFFF.toInt()
        val fondoInactivo = Color.TRANSPARENT
        val textoActivo = Color.WHITE
        val textoInactivo = 0xCCFFFFFF.toInt()

        if (activaAyuda) {
            binding.tabAyudaToolbar.setBackgroundColor(fondoActivo)
            binding.tabTutosToolbar.setBackgroundColor(fondoInactivo)
            binding.tvTabAyuda.setTextColor(textoActivo)
            binding.tvTabTutos.setTextColor(textoInactivo)
        } else {
            binding.tabAyudaToolbar.setBackgroundColor(fondoInactivo)
            binding.tabTutosToolbar.setBackgroundColor(fondoActivo)
            binding.tvTabAyuda.setTextColor(textoInactivo)
            binding.tvTabTutos.setTextColor(textoActivo)
        }
    }

    // =================== LOGIN Google para la cuenta a bloquear ===================

    private fun iniciarSesionGoogleParaBloqueo() {
        // 1) Cerrar cualquier sesión previa asociada a esta actividad
        auth.signOut()
        val gsoLogout = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        GoogleSignIn.getClient(this, gsoLogout).signOut()

        // 2) Preparar Google Sign-In normal (el usuario elegirá la cuenta)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val clienteGoogle = GoogleSignIn.getClient(this, gso)
        lanzadorLogin.launch(clienteGoogle.signInIntent)
    }

    private fun procesarResultadoLogin(resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(
                this,
                "Inicio de sesión cancelado. Vuelve a pulsar «Iniciar sesión» y completa el proceso.",
                Toast.LENGTH_LONG
            ).show()
            binding.btnBloquearEmergencia.visibility = View.GONE
            return
        }

        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val cuenta = task.getResult(ApiException::class.java)
            val token = cuenta.idToken

            if (token.isNullOrEmpty()) {
                Toast.makeText(
                    this,
                    "No se pudo obtener el token de Google.",
                    Toast.LENGTH_LONG
                ).show()
                binding.btnBloquearEmergencia.visibility = View.GONE
                return
            }

            val credencial = GoogleAuthProvider.getCredential(token, null)

            auth.signInWithCredential(credencial)
                .addOnSuccessListener {
                    val uid = auth.currentUser?.uid
                    if (uid == null) {
                        Toast.makeText(
                            this,
                            "Sesión iniciada, pero no se pudo obtener el usuario.",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.btnBloquearEmergencia.visibility = View.GONE
                        return@addOnSuccessListener
                    }

                    db.collection("usuarios").document(uid).get()
                        .addOnSuccessListener { snap ->
                            if (!snap.exists()) {
                                Toast.makeText(
                                    this,
                                    "Esta cuenta Google no tiene usuario creado en Crystal.\n" +
                                            "Regístrate primero con esta cuenta y luego vuelve a intentar bloquearla.",
                                    Toast.LENGTH_LONG
                                ).show()
                                auth.signOut()
                                binding.btnBloquearEmergencia.visibility = View.GONE
                            } else {
                                Toast.makeText(
                                    this,
                                    "Sesión iniciada para bloqueo de wallet.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.btnBloquearEmergencia.visibility = View.VISIBLE
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this,
                                "No se pudo comprobar la cuenta en Crystal: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            binding.btnBloquearEmergencia.visibility = View.GONE
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Error al iniciar sesión con Google: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnBloquearEmergencia.visibility = View.GONE
                }

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Error al procesar inicio de sesión: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
            binding.btnBloquearEmergencia.visibility = View.GONE
        }
    }

    // =================== PIN y validación ===================

    private fun pedirPinYValidarAntesDeBloquear() {
        val usuario = auth.currentUser
        if (usuario == null) {
            Toast.makeText(
                this,
                "Primero usa «Iniciar sesión» con la cuenta que quieres bloquear.",
                Toast.LENGTH_LONG
            ).show()
            binding.btnBloquearEmergencia.visibility = View.GONE
            return
        }

        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 0)
        }
        val etPin = EditText(this).apply {
            hint = "PIN de $pinLength dígitos"
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            filters = arrayOf(InputFilter.LengthFilter(pinLength))
        }
        contenedor.addView(etPin)

        AlertDialog.Builder(this)
            .setTitle("Confirma tu PIN de wallet")
            .setMessage(
                "Escribe el PIN de tu wallet y pulsa «Continuar».\n" +
                        "Si el PIN es correcto, se bloqueará tu wallet en todos los dispositivos."
            )
            .setView(contenedor)
            .setPositiveButton("Continuar") { _, _ ->
                val pin = etPin.text?.toString()?.trim().orEmpty()
                if (pin.length != pinLength) {
                    Toast.makeText(
                        this,
                        "El PIN debe tener exactamente $pinLength dígitos.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setPositiveButton
                }
                validarPinAntesDeBloquear(usuario.uid, pin)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun validarPinAntesDeBloquear(uid: String, pin: String) {
        val docRef = db.collection("usuarios")
            .document(uid)
            .collection("security")
            .document("walletPin")

        docRef.get()
            .addOnSuccessListener { snap ->
                if (!snap.exists()) {
                    Toast.makeText(
                        this,
                        "Esta cuenta aún no tiene PIN configurado.\n" +
                                "Configura un PIN desde tu dispositivo principal y vuelve a intentarlo.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }

                val lockedUntil = snap.getTimestamp("pinLockedUntil")
                if (estaBloqueado(lockedUntil)) {
                    val minRest = minutosRestantes(lockedUntil)
                    Toast.makeText(
                        this,
                        "Has superado el número de intentos. El PIN estará bloqueado unos $minRest minutos.",
                        Toast.LENGTH_LONG
                    ).show()
                    return@addOnSuccessListener
                }

                val saltB64 = snap.getString("salt").orEmpty()
                val hashB64 = snap.getString("hash").orEmpty()
                val iter = (snap.getLong("iter") ?: iterPBKDF2.toLong()).toInt()
                val fallos = (snap.getLong("failedCountPin") ?: 0L).toInt()

                val esperado = SecurityUtils.fromBase64(hashB64)
                val salt = SecurityUtils.fromBase64(saltB64)
                val actual = SecurityUtils.pbkdf2(pin, salt, iter)

                val ok = SecurityUtils.constantTimeEquals(esperado, actual)

                if (ok) {
                    val updates = mapOf(
                        "failedCountPin" to 0,
                        "pinLockedUntil" to null
                    )
                    docRef.set(updates, SetOptions.merge())
                        .addOnCompleteListener {
                            confirmarYCongelarWallet(uid)
                        }
                } else {
                    val nuevosFallos = fallos + 1
                    val updates = mutableMapOf<String, Any?>(
                        "failedCountPin" to nuevosFallos
                    )
                    if (nuevosFallos >= maxAttemptsPin) {
                        val lockUntil = Timestamp(
                            java.util.Date(
                                System.currentTimeMillis() + lockMinutesPin * 60 * 1000
                            )
                        )
                        updates["pinLockedUntil"] = lockUntil
                    }
                    docRef.set(updates, SetOptions.merge())
                        .addOnCompleteListener {
                            val restantes = max(0, maxAttemptsPin - nuevosFallos)
                            val msg = if (restantes > 0)
                                "PIN incorrecto. Intentos restantes: $restantes."
                            else
                                "PIN incorrecto. El PIN quedó bloqueado por $lockMinutesPin minutos."
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error verificando el PIN: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun estaBloqueado(lockedUntil: Timestamp?): Boolean {
        val t = lockedUntil?.toDate()?.time ?: return false
        return t > System.currentTimeMillis()
    }

    private fun minutosRestantes(lockedUntil: Timestamp?): Long {
        val t = lockedUntil?.toDate()?.time ?: return 0
        val diff = t - System.currentTimeMillis()
        return (diff / 60000L) + 1
    }

    // =================== Bloquear wallet y volver a Inicio ===================

    private fun confirmarYCongelarWallet(uid: String) {
        AlertDialog.Builder(this)
            .setTitle("Bloquear wallet en todos los dispositivos")
            .setMessage(
                "Se bloqueará tu wallet en todos los dispositivos donde hayas iniciado sesión con esta cuenta.\n\n" +
                        "¿Confirmas que quieres bloquear tu wallet?"
            )
            .setPositiveButton("Sí, bloquear") { _, _ ->
                cambiarEstadoCongelado(uid, true)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun cambiarEstadoCongelado(uid: String, congelar: Boolean) {
        val userRef = db.collection("usuarios").document(uid)
        val stateRef = userRef.collection("security").document("wallet_state")
        val auditRef = userRef.collection("security")
            .document("walletState_audit_${System.currentTimeMillis()}")

        val updates = hashMapOf<String, Any>(
            "wallet_frozen" to congelar,
            "updatedAt" to FieldValue.serverTimestamp()
        )

        db.runBatch { b ->
            b.set(stateRef, updates, SetOptions.merge())
            b.set(
                auditRef,
                mapOf(
                    "wallet_frozen" to congelar,
                    "changedAt" to FieldValue.serverTimestamp(),
                    "by" to "user",
                    "client" to "android_ayuda_emergencia"
                )
            )
        }.addOnSuccessListener {
            Toast.makeText(
                this,
                "Tu wallet ha sido congelada en todos los dispositivos.\n" +
                        "Cerrando sesión y volviendo a inicio.",
                Toast.LENGTH_LONG
            ).show()
            cerrarSesionYVolverAInicio()
        }.addOnFailureListener { e ->
            Toast.makeText(
                this,
                "Error actualizando estado de wallet: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun cerrarSesionYVolverAInicio() {
        auth.signOut()
        val intent = Intent(this, InicioActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
    }
}
