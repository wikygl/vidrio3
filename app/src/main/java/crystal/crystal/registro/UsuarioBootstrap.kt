package crystal.crystal.registro

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object UsuarioBootstrap {

    suspend fun asegurarPerfilUsuario(context: Context, account: GoogleSignInAccount? = null) {
        val current = FirebaseAuth.getInstance().currentUser ?: return
        val uid = current.uid
        val db = FirebaseFirestore.getInstance()
        val uref = db.collection("usuarios").document(uid)

        val nombre = account?.displayName ?: (current.displayName ?: "")
        val email = account?.email ?: (current.email ?: "")
        val foto = account?.photoUrl?.toString() ?: (current.photoUrl?.toString() ?: "")

        val base = hashMapOf(
            "uid" to uid,                  // <- para reglas
            "ownerUid" to uid,             // <- para reglas
            "perfil" to hashMapOf(
                "nombre" to nombre,
                "email" to email,
                "imagenPerfil" to foto,
                "actualizadoEn" to FieldValue.serverTimestamp()
            ),
            "wallet" to hashMapOf(
                "saldo" to 0.0
            ),
            "trial" to hashMapOf(
                "ultimo_estado" to "BASIC",
                "prepagos_usados" to 0
            )
        )
        uref.set(base, SetOptions.merge()).await()
    }

    /**
     * Recalcula el estado de servicio:
     *  - Si no existe estado_servicio, crea un TRIAL inicial de 30 días FULL.
     *  - Si existe y ya venció, baja a BASIC.
     *  - Si existe y está vigente, lo respeta.
     */
    suspend fun recalcularEstadoServicio(context: Context) {
        val current = FirebaseAuth.getInstance().currentUser ?: return
        val uid = current.uid
        val db = FirebaseFirestore.getInstance()
        val uref = db.collection("usuarios").document(uid)

        val snap = uref.get().await()
        val ahora = System.currentTimeMillis()

        val estado = snap.get("estado_servicio") as? Map<*, *>

        if (estado == null) {
            // Primer login sin estado: damos 30 días FULL gratis
            val inicio = Timestamp.now()
            val fin = Timestamp(inicio.seconds + 30L * 24L * 60L * 60L, 0)
            val periodId = "TRIAL_1M_${System.currentTimeMillis()}"

            val nuevo = mapOf(
                "estado_servicio" to mapOf(
                    "mode" to "FULL",
                    "source" to "TRIAL_1M",
                    "periodId" to periodId,
                    "validFrom" to inicio,
                    "validTo" to fin,
                    "lastRecalcAt" to FieldValue.serverTimestamp()
                )
            )
            uref.set(nuevo, SetOptions.merge()).await()
            return
        }

        val validTo = estado["validTo"] as? Timestamp
        val modeActual = estado["mode"] as? String ?: "BASIC"
        val sourceActual = estado["source"] as? String

        if (validTo != null && validTo.toDate().time < ahora) {
            // Periodo vencido: si no hay un plan activo posterior, bajar a BASIC
            val nuevo = mapOf(
                "estado_servicio" to mapOf(
                    "mode" to "BASIC",
                    "source" to "EXPIRED",
                    "periodId" to null,
                    "validFrom" to null,
                    "validTo" to null,
                    "lastRecalcAt" to FieldValue.serverTimestamp()
                )
            )
            uref.set(nuevo, SetOptions.merge()).await()
        } else {
            // Vigente: solo actualizamos lastRecalcAt y respetamos FULL/BASIC existente
            uref.set(
                mapOf("estado_servicio.lastRecalcAt" to FieldValue.serverTimestamp()),
                SetOptions.merge()
            ).await()
        }
    }
}
