package crystal.crystal.registro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import crystal.crystal.MainActivity
import crystal.crystal.R
import crystal.crystal.databinding.ActivityPlanSelectionBinding

class PlanSelectionActivity : AppCompatActivity(), PaymentOptionsBottomSheetDialogFragment.PaymentOptionListener {

    private lateinit var binding: ActivityPlanSelectionBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlanSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Botón de plan gratuito: guarda directamente el plan en Firebase
        binding.btnFree.setOnClickListener {
            guardarPlan("free")
        }

        // Para planes de pago (mensual o anual), se muestra el BottomSheet de opciones de pago
        binding.btnMensual.setOnClickListener {
            showPaymentOptions("mensual")
        }
        binding.btnAnual.setOnClickListener {
            showPaymentOptions("anual")
        }
    }

    private fun showPaymentOptions(plan: String) {
        val bottomSheet = PaymentOptionsBottomSheetDialogFragment.newInstance(plan)
        bottomSheet.show(supportFragmentManager, "PaymentOptionsBottomSheet")
    }

    // Este método se invoca desde el BottomSheet cuando el usuario selecciona la opción de pago
    override fun onPaymentMethodSelected(method: String, plan: String?) {
        if (method == "google_play") {
            Toast.makeText(this, "Procesando pago con Google Play...", Toast.LENGTH_SHORT).show()
            // Aquí se integraría el flujo de Google Play Billing.
            // Simulamos éxito y guardamos el plan en Firebase.
            guardarPlan(plan ?: "unknown")
        }
    }

    private fun guardarPlan(plan: String) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "No hay usuario autenticado", Toast.LENGTH_LONG).show()
            return
        }
        val docRef = db.collection("usuarios").document(currentUser.uid)
        val data = hashMapOf<String, Any>(
            "plan" to plan,
            "ultimoCambioPlan" to FieldValue.serverTimestamp()
        )
        docRef.set(data, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Plan $plan guardado en Firebase", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error guardando plan: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
class PaymentOptionsBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var selectedPlan: String? = null
    private var listener: PaymentOptionListener? = null

    interface PaymentOptionListener {
        fun onPaymentMethodSelected(method: String, plan: String?)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PaymentOptionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement PaymentOptionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedPlan = it.getString(ARG_SELECTED_PLAN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Configura el botón para pagar con Google Play
        view.findViewById<Button>(R.id.btnGooglePlay).setOnClickListener {
            listener?.onPaymentMethodSelected("google_play", selectedPlan)
            dismiss() // Cierra el BottomSheet
        }
    }

    companion object {
        private const val ARG_SELECTED_PLAN = "selectedPlan"

        fun newInstance(plan: String): PaymentOptionsBottomSheetDialogFragment {
            val fragment = PaymentOptionsBottomSheetDialogFragment()
            val args = Bundle()
            args.putString(ARG_SELECTED_PLAN, plan)
            fragment.arguments = args
            return fragment
        }
    }
}

