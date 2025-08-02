package crystal.crystal.registro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import crystal.crystal.MainActivity
import crystal.crystal.R
import crystal.crystal.databinding.ActivityInicioActtivityBinding

@Suppress("DEPRECATION")
class InicioActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val GOOGLE_SIGN_IN_CODE = 100

    private lateinit var binding: ActivityInicioActtivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioActtivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verificarUsuario()  // Revisa si ya hay alguien logueado en Firebase

        // Opción FREE: el usuario entra sin registro
        binding.lyFree.setOnClickListener {
            // Puedes guardar algún tipo de info básica en Firebase si quieres,
            // o simplemente ir al MainActivity.
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Opción Google Sign-In
        binding.lyGoogle.setOnClickListener {
            iniciarSesionConGoogle()
        }

        // Opción Teléfono
        binding.lyTelf.setOnClickListener {
            // Lanzamos la pantalla de login por teléfono
            val intent = Intent(this, PhoneLoginActivity::class.java)
            startActivity(intent)
        }

        // Animación (si la necesitas)
        ani(binding.lo, R.raw.tee)
    }

    private fun ani(imageView: LottieAnimationView, animacion: Int) {
        imageView.setAnimation(animacion)
        imageView.repeatCount = 9
        imageView.playAnimation()
    }

    private fun verificarUsuario() {
        val usuario = auth.currentUser
        if (usuario != null) {
            // Ya está logueado: redirigir a MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // Configuración en onCreate() o en iniciarSesionConGoogle()
    private fun iniciarSesionConGoogle() {
        val opcionesInicioSesionGoogle =
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                // Asegúrate de tener este string correctamente configurado
                .requestEmail()
                .build()

        val clienteInicioSesionGoogle = GoogleSignIn.getClient(this, opcionesInicioSesionGoogle)
        val intentInicioSesionGoogle = clienteInicioSesionGoogle.signInIntent
        startActivityForResult(intentInicioSesionGoogle, GOOGLE_SIGN_IN_CODE)
    }

    // En onActivityResult, después de obtener la cuenta:
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            val resultado = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val cuenta = resultado.getResult(ApiException::class.java)
                // Crear credencial para Firebase
                val credential = GoogleAuthProvider.getCredential(cuenta.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Ahora el usuario está autenticado en Firebase,
                        // puedes guardar sus datos en Firestore.
                        guardarDatosInicialesEnFirebase(cuenta)
                        // Luego redirige a la pantalla de selección de plan
                        val intent = Intent(this, PlanSelectionActivity::class.java)
                        intent.putExtra("googleId", cuenta.id ?: "")
                        intent.putExtra("googleEmail", cuenta.email)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Firebase auth falló", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Función actualizada para guardar en Firebase:
    private fun guardarDatosInicialesEnFirebase(account: GoogleSignInAccount) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            Log.e("InicioActivity", "Firebase user es nulo")
            return
        }
        val db = FirebaseFirestore.getInstance()
        val usuario = hashMapOf(
            "nombre" to (account.displayName ?: ""),
            "email" to (account.email ?: ""),
            "imagenPerfil" to (account.photoUrl?.toString() ?: ""),
            "plan" to "pendiente"
        )
        val usuarioRef = db.collection("usuarios").document(firebaseUser.uid)
        usuarioRef.set(usuario)
            .addOnSuccessListener {
                Log.d("InicioActivity", "Datos guardados en Firebase exitosamente")
                Toast.makeText(this, "Datos guardados en Firebase", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Log.e("InicioActivity", "Error al guardar datos: ${e.message}")
                Toast.makeText(this, "Error al guardar los datos en Firebase", Toast.LENGTH_LONG).show()
            }
    }

}

