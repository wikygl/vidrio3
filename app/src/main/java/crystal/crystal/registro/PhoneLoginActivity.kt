package crystal.crystal.registro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import crystal.crystal.databinding.ActivityPhoneLoginBinding
import java.util.concurrent.TimeUnit

class PhoneLoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhoneLoginBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Callbacks que se activan cuando se ha enviado el SMS o se ha verificado automáticamente
        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // Verificación automática (algunas veces ocurre en dispositivos Android)
                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@PhoneLoginActivity, "Fallo en verificación: ${e.message}", Toast.LENGTH_LONG).show()
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                // Se ha enviado el código al usuario
                this@PhoneLoginActivity.verificationId = verificationId
                Toast.makeText(this@PhoneLoginActivity, "Código enviado", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para enviar código
        binding.btnEnviarCodigo.setOnClickListener {
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            if (phoneNumber.isNotEmpty()) {
                enviarCodigoVerificacion(phoneNumber)
            } else {
                Toast.makeText(this, "Ingresa un número válido", Toast.LENGTH_SHORT).show()
            }
        }

        // Botón para verificar código
        binding.btnVerificarCodigo.setOnClickListener {
            val code = binding.etVerificationCode.text.toString().trim()
            if (code.isNotEmpty() && verificationId != null) {
                val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Ingresa el código enviado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun enviarCodigoVerificacion(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Usuario logueado con éxito por teléfono
                    val user = task.result?.user
                    // Ir a la selección de plan
                    val intent = Intent(this, PlanSelectionActivity::class.java)
                    // Podrías pasar el UID o el teléfono
                    intent.putExtra("phoneUid", user?.uid)
                    intent.putExtra("phoneNumber", user?.phoneNumber)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al verificar código", Toast.LENGTH_LONG).show()
                }
            }
    }
}
