package crystal.crystal.pagos

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import crystal.crystal.registro.PlanSelectionActivity
import crystal.crystal.registro.WalletActivity

/**
 * Canal de cobro — variante DIRECTO (reparto del APK fuera de tiendas).
 *
 * Esta clase existe por duplicado, una vez en cada flavor, con la misma firma. Es lo que permite que
 * el código de `main` llame al wallet sin conocerlo: en la build de Play la otra implementación no
 * arrastra ninguna pantalla de pago, y las clases y layouts del wallet ni siquiera entran al APK.
 *
 * Aquí sí: el usuario recarga y compra su plan dentro de la app, con Yape.
 */
object CanalPagos {

    /** Hay pantallas de pago dentro de la app. La UI la consulta para mostrar u ocultar accesos. */
    const val DISPONIBLE_EN_LA_APP = true

    /**
     * Extra de [abrirPlanes]: la pantalla de planes se abrió desde un candado de pago y, al
     * terminar, debe DEVOLVER al usuario a donde estaba en vez de mandarlo a MainActivity.
     */
    const val EXTRA_VOLVER_ATRAS = "volver_atras"

    fun abrirRecarga(ctx: Context, vararg extras: Pair<String, Any?>) {
        ctx.startActivity(Intent(ctx, WalletActivity::class.java).putExtras(bundleOf(*extras)))
    }

    fun abrirPlanes(ctx: Context, vararg extras: Pair<String, Any?>) {
        ctx.startActivity(Intent(ctx, PlanSelectionActivity::class.java).putExtras(bundleOf(*extras)))
    }

    /**
     * Toma una imagen que llegó por el chat y la adjunta como constancia de una recarga en curso.
     * Vivía dentro de ChatActivity, pero habla con `reservas_recarga` y con la función
     * `adjuntarComprobante`: en la build de Play no puede existir ni como código muerto.
     */
    fun usarComprobanteParaRecarga(ctx: Context, voucherUrl: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("reservas_recarga")
            .whereEqualTo("uid", uid)
            .whereEqualTo("estado", "esperando")
            .get()
            .addOnSuccessListener { qs ->
                val pend = qs.documents
                when {
                    pend.isEmpty() -> Toast.makeText(
                        ctx, "No tienes una recarga en curso. Primero toca 💳 Recargar en el Wallet.", Toast.LENGTH_LONG
                    ).show()
                    pend.size == 1 -> adjuntar(ctx, pend[0].id, pend[0].getLong("totalCent") ?: 0L, voucherUrl)
                    else -> {
                        val labels = pend.map { "S/ %.2f".format((it.getLong("totalCent") ?: 0L) / 100.0) }.toTypedArray()
                        AlertDialog.Builder(ctx)
                            .setTitle("¿A qué recarga pertenece?")
                            .setItems(labels) { _, i -> adjuntar(ctx, pend[i].id, pend[i].getLong("totalCent") ?: 0L, voucherUrl) }
                            .show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(ctx, "Error buscando tu recarga: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun adjuntar(ctx: Context, reservaId: String, totalCent: Long, voucherUrl: String) {
        FirebaseFunctions.getInstance()
            .getHttpsCallable("adjuntarComprobante")
            .call(mapOf("reservaId" to reservaId, "voucherPath" to "", "voucherUrl" to voucherUrl))
            .addOnSuccessListener {
                Toast.makeText(ctx, "Comprobante adjuntado ✅. Abriendo tu Wallet…", Toast.LENGTH_SHORT).show()
                abrirRecarga(ctx, "abrir_reserva_id" to reservaId, "abrir_reserva_cent" to totalCent)
            }
            .addOnFailureListener { e ->
                Toast.makeText(ctx, "No se pudo adjuntar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}
