package crystal.crystal.comprobantes

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import crystal.crystal.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Fragment modal que bloquea toda la app hasta que se sincronicen
 * los datos actualizados de la empresa
 */
class ActualizacionBlockerFragment : DialogFragment() {

    private lateinit var btnSincronizar: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvMensaje: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        // No se puede cancelar tocando fuera o presionando back
        isCancelable = false

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_actualizacion_blocker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSincronizar = view.findViewById(R.id.btnSincronizar)
        progressBar = view.findViewById(R.id.progressBar)
        tvMensaje = view.findViewById(R.id.tvMensaje)

        btnSincronizar.setOnClickListener {
            sincronizarDatos()
        }
    }

    private fun sincronizarDatos() {
        // Deshabilitar botón y mostrar progreso
        btnSincronizar.isEnabled = false
        progressBar.visibility = View.VISIBLE
        tvMensaje.text = "Sincronizando datos..."

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sharedPrefs = requireContext()
                    .getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

                // Obtener patron_uid (para terminales PIN)
                val esTerminalPIN = sharedPrefs.getBoolean("es_terminal_pin", false)
                val uid = if (esTerminalPIN) {
                    sharedPrefs.getString("patron_uid", null)
                } else {
                    FirebaseAuth.getInstance().currentUser?.uid
                }

                if (uid == null) {
                    throw Exception("No se pudo obtener el UID del patrón")
                }

                // Descargar datos actualizados de Firestore
                val documento = FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .get()
                    .await()

                if (!documento.exists()) {
                    throw Exception("No se encontraron datos en Firestore")
                }

                // Extraer datos de empresa
                val empresaData = documento.get("empresa") as? Map<*, *>

                if (empresaData == null) {
                    throw Exception("No hay datos de empresa configurados")
                }

                // Guardar en SharedPreferences
                sharedPrefs.edit().apply {
                    putString("empresa_ruc", empresaData["ruc"] as? String ?: "")
                    putString("empresa_razon_social", empresaData["razonSocial"] as? String ?: "")
                    putString("empresa_nombre_comercial", empresaData["nombreComercial"] as? String)
                    putString("empresa_direccion", empresaData["direccion"] as? String ?: "")
                    putString("empresa_telefono", empresaData["telefono"] as? String ?: "")
                    putString("empresa_email", empresaData["email"] as? String)
                    putString("empresa_logo_url", empresaData["logoUrl"] as? String)

                    // Guardar versión
                    val version = (empresaData["version"] as? Long)?.toInt() ?: 0
                    putInt("empresa_version", version)

                    // Guardar timestamp
                    putLong("empresa_ultima_sincronizacion", System.currentTimeMillis())

                    // Guardar configuración de ticket
                    val disenoTicket = empresaData["disenoTicket"] as? Map<*, *>
                    if (disenoTicket != null) {
                        putString("diseno_ticket_json", com.google.gson.Gson().toJson(disenoTicket))
                    }

                    // Quitar flag de actualización pendiente
                    putBoolean("actualizacion_empresa_pendiente", false)

                    apply()
                }

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "✅ Datos sincronizados correctamente",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Cerrar el fragment y desbloquear la app
                    dismiss()

                    // Notificar a MainActivity que recargue los datos
                    requireActivity().recreate()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    btnSincronizar.isEnabled = true
                    tvMensaje.text = "El administrador actualizó los datos de empresa.\n\nDebes sincronizar antes de continuar."

                    Toast.makeText(
                        requireContext(),
                        "❌ Error: ${e.message}\n\nVerifica tu conexión a internet",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Hacer que el dialog ocupe toda la pantalla
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}