package crystal.crystal.registro

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import crystal.crystal.databinding.ActivityAclienteBinding

class Acliente : AppCompatActivity() {

    private lateinit var binding: ActivityAclienteBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAclienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "No hay usuario activo", Toast.LENGTH_LONG).show()
            return
        }

        // Escucha cambios en Firestore
        db.collection("usuarios").document(user.uid)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                val saldo = snapshot.getDouble("wallet.saldo") ?: 0.0
                val planTipo = snapshot.getString("plan.tipo") ?: "free"
                val planEstado = snapshot.getString("plan.estado") ?: "activo"
                val trial = (snapshot.getLong("trial.creditos") ?: 0L).toInt()

                binding.etCliente.setText(
                    "Saldo: S/ %.2f\nPlan: %s (%s)\nCréditos trial: %d"
                        .format(saldo, planTipo, planEstado, trial)
                )
            }

        // Botón “Ir a Wallet”
        binding.btGo.setOnClickListener {
            startActivity(Intent(this, WalletActivity::class.java))
        }
    }
}
