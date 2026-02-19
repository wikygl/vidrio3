package crystal.crystal.wallet

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import crystal.crystal.databinding.ActivityReclamoRecargaBinding
import java.util.*

class ReclamoRecargaActivity : AppCompatActivity() {

    private lateinit var b: ActivityReclamoRecargaBinding
    private var voucherUri: Uri? = null
    private val uid = Firebase.auth.currentUser?.uid

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            voucherUri = result.data?.data
            b.ivVoucherPreview.setImageURI(voucherUri)
            b.tvVoucherStatus.text = "✅ Voucher seleccionado"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityReclamoRecargaBinding.inflate(layoutInflater)
        setContentView(b.root)

        supportActionBar?.title = "Reclamar Recarga"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupUI()
    }

    private fun setupUI() {
        b.btnSeleccionarVoucher.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "image/*"
            }
            pickImageLauncher.launch(intent)
        }

        b.btnEnviarReclamo.setOnClickListener {
            enviarReclamo()
        }
    }

    private fun enviarReclamo() {
        val monto = b.etMonto.text.toString().toDoubleOrNull()
        val tipoPago = when (b.rgTipoPago.checkedRadioButtonId) {
            b.rbYapeYape.id -> "yape_yape"
            b.rbPlinYape.id -> "plin_yape"
            b.rbYapePlin.id -> "yape_plin"
            else -> null
        }
        val explicacion = b.etExplicacion.text.toString().trim()

        // Validaciones
        if (voucherUri == null) {
            Toast.makeText(this, "❌ Debes adjuntar el voucher", Toast.LENGTH_SHORT).show()
            return
        }

        if (monto == null || monto <= 0) {
            Toast.makeText(this, "❌ Ingresa un monto válido", Toast.LENGTH_SHORT).show()
            return
        }

        if (tipoPago == null) {
            Toast.makeText(this, "❌ Selecciona el tipo de pago", Toast.LENGTH_SHORT).show()
            return
        }

        if (explicacion.length < 10) {
            Toast.makeText(this, "❌ Explica brevemente el problema (mínimo 10 caracteres)", Toast.LENGTH_SHORT).show()
            return
        }

        if (uid == null) {
            Toast.makeText(this, "❌ Error: No autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // Deshabilitar botón
        b.btnEnviarReclamo.isEnabled = false
        b.progressBar.visibility = android.view.View.VISIBLE

        // Subir voucher a Storage
        val storageRef = Firebase.storage.reference
        val fileName = "reclamos/${uid}/${System.currentTimeMillis()}.jpg"
        val voucherRef = storageRef.child(fileName)

        voucherRef.putFile(voucherUri!!)
            .addOnSuccessListener { taskSnapshot ->
                voucherRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    crearReclamoEnFirestore(monto, tipoPago, explicacion, downloadUri.toString())
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "❌ Error al subir voucher: ${e.message}", Toast.LENGTH_LONG).show()
                b.btnEnviarReclamo.isEnabled = true
                b.progressBar.visibility = android.view.View.GONE
            }
    }

    private fun crearReclamoEnFirestore(
        monto: Double,
        tipoPago: String,
        explicacion: String,
        voucherUrl: String
    ) {
        val reclamoData = hashMapOf(
            "montoDetectado" to monto,
            "tipoVoucher" to "reclamo_manual",  // ⭐ TIPO ESPECIAL
            "tipoPagoDeclarado" to tipoPago,    // yape_yape, plin_yape, etc.
            "explicacionUsuario" to explicacion,
            "voucherUrl" to voucherUrl,
            "fecha" to com.google.firebase.Timestamp.now(),
            "estado" to "pendiente_revision",
            "motivoPendiente" to "RECLAMO_USUARIO",
            "codigoOperacion" to "",
            "deviceId" to ""
        )

        Firebase.firestore
            .collection("usuarios")
            .document(uid!!)
            .collection("recargas")
            .add(reclamoData)
            .addOnSuccessListener {
                Toast.makeText(
                    this,
                    "✅ Reclamo enviado. Te notificaremos cuando sea revisado.",
                    Toast.LENGTH_LONG
                ).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "❌ Error: ${e.message}", Toast.LENGTH_LONG).show()
                b.btnEnviarReclamo.isEnabled = true
                b.progressBar.visibility = android.view.View.GONE
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}