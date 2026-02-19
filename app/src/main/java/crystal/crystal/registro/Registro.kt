package crystal.crystal.registro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import crystal.crystal.databinding.ActivityRegistroBinding
import java.util.concurrent.TimeUnit

class Registro : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityRegistroBinding
    private val GOOGLE_SIGN_IN_CODE = 100
    private val IMAGE_PICK_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegis.setOnClickListener { crearUsuario() }
        binding.foto.setOnClickListener { seleccionarImagenDeGaleria() }

        verificarUsuario()
    }

    private fun verificarUsuario() {
        val usuario = auth.currentUser
        if (usuario != null) {
            // Asegurar estructura y pasar a selección de plan
            crearEstructuraBaseSiNoExiste(usuario.uid) {
                startActivity(Intent(this, PlanSelectionActivity::class.java))
                finish()
            }
        }
    }

    private fun crearUsuario() {
        val email = binding.emailUser.text.toString()
        val contra = binding.contrasenaUser.text.toString()

        if (email.isNotEmpty() && contra.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, contra).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = task.result?.user?.uid ?: return@addOnCompleteListener
                    crearEstructuraBaseSiNoExiste(uid) {
                        startActivity(Intent(this, PlanSelectionActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Complete todos los campos.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun crearEstructuraBaseSiNoExiste(uid: String, onReady: () -> Unit) {
        val doc = db.collection("usuarios").document(uid)
        doc.get().addOnSuccessListener { snap ->
            if (!snap.exists()) {
                val data = hashMapOf(
                    "nombre" to (binding.nombre.text?.toString() ?: ""),
                    "email" to (binding.emailUser.text?.toString() ?: ""),
                    "wallet" to mapOf("saldo" to 0.0),
                    "plan" to mapOf("tipo" to "free", "estado" to "activo"),
                    "trial" to mapOf("creditos" to 5)
                )
                doc.set(data).addOnSuccessListener { onReady() }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error guardando base de usuario", Toast.LENGTH_LONG).show()
                    }
            } else onReady()
        }
    }

    // -------- Teléfono (sin cambios de UX, solo flujo base) --------
    private fun iniciarSesionTelefono() {
        val telefono = binding.nombre.text.toString()
        if (telefono.isNotEmpty()) {
            val opciones = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(telefono)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                        iniciarSesionConCredencialTelefonica(credential)
                    }
                    override fun onVerificationFailed(e: FirebaseException) {
                        Toast.makeText(this@Registro, "Verificación fallida: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) { }
                })
                .build()
            PhoneAuthProvider.verifyPhoneNumber(opciones)
        } else {
            Toast.makeText(this, "Ingrese su número de teléfono.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun iniciarSesionConCredencialTelefonica(credencial: PhoneAuthCredential) {
        auth.signInWithCredential(credencial).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = task.result?.user?.uid ?: return@addOnCompleteListener
                crearEstructuraBaseSiNoExiste(uid) {
                    startActivity(Intent(this, PlanSelectionActivity::class.java))
                    finish()
                }
            } else {
                Toast.makeText(this, "Error al iniciar sesión con teléfono.", Toast.LENGTH_LONG).show()
            }
        }
    }

    // -------- Google --------
    private fun iniciarSesionConGoogle() {
        val opcionesInicioSesionGoogle =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        val clienteInicioSesionGoogle = GoogleSignIn.getClient(this, opcionesInicioSesionGoogle)
        val intentInicioSesionGoogle = clienteInicioSesionGoogle.signInIntent
        startActivityForResult(intentInicioSesionGoogle, GOOGLE_SIGN_IN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            val resultado = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = resultado.getResult(ApiException::class.java)
                val cred = GoogleAuthProvider.getCredential(cuenta.idToken, null)
                auth.signInWithCredential(cred).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = task.result?.user?.uid ?: return@addOnCompleteListener
                        crearEstructuraBaseSiNoExiste(uid) {
                            startActivity(Intent(this, PlanSelectionActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_LONG).show()
            }
        }
    }

    // -------- Imagen perfil (opcional) --------
    private fun seleccionarImagenDeGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        seleccionarImagen.launch(intent)
    }

    private val seleccionarImagen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageUri = result.data?.data
                binding.foto.setImageURI(imageUri)
            }
        }
}
