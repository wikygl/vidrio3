package calculaora.e.vidrio3.registro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import calculaora.e.vidrio3.MainActivity
import calculaora.e.vidrio3.R
import calculaora.e.vidrio3.databinding.ActivityInicioActtivityBinding
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InicioActtivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val GOOGLE_SIGN_IN_CODE = 100

    private lateinit var binding: ActivityInicioActtivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInicioActtivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        verificarUsuario()
        binding.btnRegis.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }

        binding.lyFree.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        binding.lyGoogle.setOnClickListener {
            iniciarSesionConGoogle()
        }
        ani(binding.lo, R.raw.npaf)
    }

    private fun ani(imageView: LottieAnimationView, animacion: Int) {
        imageView.setAnimation(animacion)
        imageView.repeatCount = 9
        imageView.playAnimation()
    }

    private fun verificarUsuario() {
        val usuario = auth.currentUser
        if (usuario != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("user", usuario.email)
            startActivity(intent)
            finish()
        }
    }

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
                // Aquí puedes acceder a los datos de la cuenta, como el correo electrónico, el ID, etc.
                // Luego, puedes guardar estos datos en la base de datos de Firebase.
                guardarDatosEnFirebase(cuenta)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun guardarDatosEnFirebase(account: GoogleSignInAccount) {
        val db = FirebaseFirestore.getInstance()

        val usuario = hashMapOf(
            "nombre" to account.displayName,
            "email" to account.email,
            "imagenPerfil" to account.photoUrl.toString()
            // Agrega otros campos si lo deseas
        )

        val usuarioRef = db.collection("usuarios").document(account.id ?: "")

        usuarioRef.set(usuario)
            .addOnSuccessListener {
                // Datos guardados exitosamente
                Toast.makeText(this, "Datos guardados en Firebase", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                // Manejo de errores
                Toast.makeText(this, "Error al guardar los datos en Firebase", Toast.LENGTH_LONG).show()
            }
    }
}
