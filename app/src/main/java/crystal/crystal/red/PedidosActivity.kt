package crystal.crystal.red

import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import crystal.crystal.R

/**
 * Bandeja de PEDIDOS EN ESPERA (adoptado del modelo de Puntos): lista única de pedidos entrantes de
 * todas las conversaciones del patrón (usuarios/{patronUid}/pedidos, estado=en_espera), en tiempo
 * real. Al tomar uno, se reclama de forma atómica (Cloud Function tomarPedido) y sale de la lista
 * para todas las terminales.
 */
class PedidosActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_UID = "pedidos_uid"
    }

    private data class PedidoItem(
        val id: String,
        val chatId: String,
        val messageId: String,
        val contactoNombre: String,
        val resumen: String,
        val fechaMs: Long
    )

    private var uid = ""
    private val items = mutableListOf<PedidoItem>()
    private lateinit var lista: ListView
    private lateinit var tvVacio: TextView
    private lateinit var adapter: ArrayAdapter<String>
    private var listener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)
        supportActionBar?.title = "Pedidos en espera"

        uid = intent.getStringExtra(EXTRA_UID).orEmpty()
        if (uid.isBlank()) {
            uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
        }
        if (uid.isBlank()) {
            Toast.makeText(this, "No se pudo identificar la cuenta", Toast.LENGTH_LONG).show()
            finish(); return
        }

        lista = findViewById(R.id.listaPedidos)
        tvVacio = findViewById(R.id.tvVacioPedidos)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        lista.adapter = adapter

        lista.setOnItemClickListener { _, _, pos, _ ->
            items.getOrNull(pos)?.let { confirmarTomar(it) }
        }
    }

    override fun onStart() {
        super.onStart()
        listener = FirebaseFirestore.getInstance()
            .collection("usuarios").document(uid)
            .collection("pedidos")
            .whereEqualTo("estado", "en_espera")
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    Toast.makeText(this, "No se pudieron cargar los pedidos: ${e.message}", Toast.LENGTH_LONG).show()
                    return@addSnapshotListener
                }
                items.clear()
                snap?.documents?.forEach { d ->
                    items.add(
                        PedidoItem(
                            id = d.getString("id") ?: d.id,
                            chatId = d.getString("chatId") ?: "",
                            messageId = d.getString("messageId") ?: d.id,
                            contactoNombre = d.getString("contactoNombre") ?: "",
                            resumen = d.getString("resumen") ?: "",
                            fechaMs = d.getTimestamp("fechaLlegada")?.toDate()?.time ?: 0L
                        )
                    )
                }
                items.sortByDescending { it.fechaMs }
                render()
            }
    }

    override fun onStop() {
        super.onStop()
        listener?.remove()
        listener = null
    }

    private fun render() {
        val filas = items.map { p ->
            val nombre = p.contactoNombre.ifBlank { "Cliente" }
            if (p.resumen.isBlank()) "👤 $nombre" else "👤 $nombre\n${p.resumen}"
        }
        adapter.clear()
        adapter.addAll(filas)
        adapter.notifyDataSetChanged()
        tvVacio.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        lista.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun confirmarTomar(p: PedidoItem) {
        AlertDialog.Builder(this)
            .setTitle("Tomar pedido")
            .setMessage("¿Tomar el pedido de ${p.contactoNombre.ifBlank { "cliente" }}?\n\n${p.resumen}")
            .setPositiveButton("Tomar") { _, _ -> tomar(p) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    @SuppressLint("HardwareIds")
    private fun tomar(p: PedidoItem) {
        val prefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val nombreVendedor = prefs.getString("nombre_vendedor", null)?.takeIf { it.isNotBlank() }
            ?: FirebaseAuth.getInstance().currentUser?.displayName?.takeIf { it.isNotBlank() }
            ?: "Vendedor"
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        val data = hashMapOf(
            "patronUid" to uid,
            "deviceId" to deviceId,
            "chatId" to p.chatId,
            "msgId" to p.messageId,
            "atendidoNombre" to nombreVendedor
        )
        com.google.firebase.functions.FirebaseFunctions.getInstance()
            .getHttpsCallable("tomarPedido").call(data)
            .addOnSuccessListener { res ->
                val m = res.data as? Map<*, *>
                if (m?.get("ok") == true) {
                    Toast.makeText(this, "Pedido tomado ✅", Toast.LENGTH_SHORT).show()
                } else {
                    val quien = m?.get("atendidoNombre")?.toString().orEmpty()
                    Toast.makeText(
                        this,
                        if (m?.get("motivo") == "ya_tomado") "Ya lo tomó ${quien.ifBlank { "otro vendedor" }}"
                        else "No se pudo tomar el pedido",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "No se pudo tomar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
