package crystal.crystal.registro

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import crystal.crystal.red.ChatIdentity
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
        val nombreNormalizado = ChatIdentity.normalizeSearchText(nombre)

        val base = hashMapOf(
            "uid" to uid,                  // <- para reglas
            "ownerUid" to uid,             // <- para reglas
            "nombre" to nombre,
            "nombreNormalizado" to nombreNormalizado,
            "email" to email,
            "imagenPerfil" to foto,
            "perfil" to hashMapOf(
                "nombre" to nombre,
                "nombreNormalizado" to nombreNormalizado,
                "email" to email,
                "imagenPerfil" to foto,
                "actualizadoEn" to FieldValue.serverTimestamp()
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
                    "full_until" to fin,   // mismo campo que escriben los planes
                    "lastRecalcAt" to FieldValue.serverTimestamp()
                )
            )
            uref.set(nuevo, SetOptions.merge()).await()
            return
        }

        // El reloj del cliente es solo para UX; la validación real irá en reglas de Firestore (Fase 2).
        val modeActual = estado["mode"] as? String ?: "BASIC"
        val fullUntilMs = (estado["full_until"] as? Timestamp)?.toDate()?.time
            ?: (estado["full_until"] as? Number)?.toLong()

        // FULL es por tiempo: si su vencimiento ya pasó, o si es un FULL SIN vencimiento (dato
        // inconsistente que antes se quedaba "vigente" para siempre), baja a BASIC. Los modos sin
        // vencimiento distintos de FULL (p. ej. VENTAS heredado) se respetan.
        val vencido = (fullUntilMs != null && fullUntilMs < ahora) ||
            (modeActual == "FULL" && fullUntilMs == null)

        if (vencido) {
            // Periodo vencido: si no hay un plan activo posterior, bajar a BASIC
            val nuevo = mapOf(
                "estado_servicio" to mapOf(
                    "mode" to "BASIC",
                    "source" to "EXPIRED",
                    "periodId" to null,
                    "full_until" to null,
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
