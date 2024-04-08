package calculaora.e.vidrio3.registro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import calculaora.e.vidrio3.MainActivity
import calculaora.e.vidrio3.databinding.ActivityRegistroBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit


class Registro : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private lateinit var binding: ActivityRegistroBinding
    private val GOOGLE_SIGN_IN_CODE = 100
    private val IMAGE_PICK_CODE = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegis.setOnClickListener {
            crearUsuario()
        }

        binding.foto.setOnClickListener {
            seleccionarImagenDeGaleria()
        }
        verificarUsuario()
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

    private fun crearUsuario() {
        val email = binding.emailUser.text.toString()

        val contra = binding.contrasenaUser.text.toString()

        if (email.isNotEmpty() && contra.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, contra).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    verificarUsuario()
                } else {
                    task.exception?.let {
                        Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun iniciarSesionUsuario() {
        val email = binding.emailUser.text.toString()
        val contra = binding.contrasenaUser.text.toString()

        if (email.isNotEmpty() && contra.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, contra).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    verificarUsuario()
                } else {
                    task.exception?.let {
                        Toast.makeText(
                            this, "El usuario no existe o la contraseña es incorrecta",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT)
                .show()
        }
    }

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
                        Toast.makeText(
                            this@Registro, "Verificación fallida: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    override fun onCodeSent(
                        verificationId: String,
                        token: PhoneAuthProvider.ForceResendingToken
                    ) {
                        // Guarda el verificationId en una variable si es necesario para usarlo más tarde.
                        // Por ejemplo, podrías guardar esta variable en el estado de tu actividad o ViewModel.
                    }
                })
                .build()

            PhoneAuthProvider.verifyPhoneNumber(opciones)
        } else {
            Toast.makeText(
                this,
                "Por favor, ingrese su número de teléfono.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun iniciarSesionConCredencialTelefonica(credencial: PhoneAuthCredential) {
        auth.signInWithCredential(credencial).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val usuario = task.result?.user
                Toast.makeText(
                    this, "Inicio de sesión con número de teléfono exitoso.",
                    Toast.LENGTH_LONG
                ).show()
                // Realiza las acciones adicionales necesarias después de un inicio de sesión exitoso.
            } else {
                Toast.makeText(
                    this, "Error al iniciar sesión con número de teléfono.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    // INICIO DE SESIÓN CON GOOGLE

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
                iniciarSesionConCredencialGoogle(cuenta.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun iniciarSesionConCredencialGoogle(tokenId: String) {
        val credencial = GoogleAuthProvider.getCredential(tokenId, null)
        auth.signInWithCredential(credencial).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Inicio de sesión con Google exitoso
                verificarUsuario()
            } else {
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    // LÓGICA PARA ELEGIR IMAGEN DE GALERÍA

    private fun seleccionarImagenDeGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        seleccionarImagen.launch(intent)
    }

    // Registra el resultado de la selección de la imagen de galería
    private val seleccionarImagen =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val imageUri = data?.data
                // Aquí puedes procesar la imagen y guardar su URL en la base de datos
                // Por ejemplo, puedes mostrar la imagen seleccionada en una ImageView
                binding.foto.setImageURI(imageUri)
            }
        }
}

