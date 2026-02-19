package crystal.crystal.registro

import android.content.Context
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

object VinculadorDispositivo {

    suspend fun vincular(uid: String, deviceId: String, ctx: Context) {
        val db = FirebaseFirestore.getInstance()
        val uref = db.collection("usuarios").document(uid)
        val dref = uref.collection("dispositivos").document(deviceId)

        val marca = android.os.Build.MANUFACTURER ?: "?"
        val modelo = android.os.Build.MODEL ?: "?"
        val androidVer = android.os.Build.VERSION.RELEASE ?: "?"

        db.runBatch { b ->
            // mínimos seguros en el usuario (no pisa lo que ya existe)
            b.set(
                uref,
                mapOf(
                    "uid" to uid,           // <- para reglas
                    "ownerUid" to uid,      // <- para reglas
                    "perfil" to mapOf(
                        "actualizadoEn" to FieldValue.serverTimestamp()
                    ),
                    "estado_servicio" to mapOf(
                        "mode" to "BASIC"
                    )
                ),
                SetOptions.merge()
            )

            // registrar este dispositivo
            b.set(
                dref,
                mapOf(
                    "uid" to uid,               // <- para reglas
                    "ownerUid" to uid,          // <- para reglas
                    "marca" to marca,
                    "modelo" to modelo,
                    "android" to androidVer,
                    "registeredAt" to FieldValue.serverTimestamp(),
                    "activo" to true
                ),
                SetOptions.merge()
            )
        }.await()
    }
}
