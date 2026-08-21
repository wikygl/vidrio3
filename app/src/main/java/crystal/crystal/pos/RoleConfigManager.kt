package crystal.crystal.pos

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import crystal.crystal.R
import crystal.crystal.databinding.ActivityMainBinding
import crystal.crystal.registro.GestionDispositivosActivity
import crystal.crystal.registro.InicioActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class RoleConfigManager(
    private val activity: AppCompatActivity,
    private val binding: ActivityMainBinding,
    private val sharedPreferences: SharedPreferences,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    interface Callback {
        fun onRoleConfigured(rolDispositivo: String, esPatron: Boolean, nombreVendedor: String)
        fun guardarDatos()
        fun sincronizarDatosManualmente()
    }

    var callback: Callback? = null

    // --- Estado de usuario ---

    fun cargarEstadoUsuario() {
        mostrarEstadoCacheado()
        consultarEstadoFirestore()
    }

    private fun mostrarEstadoCacheado() {
        val estadoCacheado = sharedPreferences.getString("usuario_estado", "BASIC") ?: "BASIC"
        binding.txModo.text = estadoCacheado

        when (estadoCacheado) {
            "FULL" -> binding.txModo.setTextColor(activity.getColor(R.color.verde))
            "BASIC" -> binding.txModo.setTextColor(activity.getColor(R.color.naranja))
            else -> binding.txModo.setTextColor(activity.getColor(R.color.color))
        }
    }

    private fun consultarEstadoFirestore() {
        val tipoSesion = sharedPreferences.getString("session_type", null)

        if (tipoSesion == "TERMINAL") {
            cargarEstadoTerminal()
            return
        }

        val usuarioActual = auth.currentUser

        if (usuarioActual == null) {
            val esTerminalPIN = sharedPreferences.getBoolean("es_terminal_pin", false)
            if (esTerminalPIN) {
                cargarEstadoTerminal()
            } else {
                binding.txModo.text = "NO AUTH"
                binding.txModo.setTextColor(activity.getColor(R.color.fucsia))
                Log.w("RoleConfigManager", "No hay usuario autenticado")
            }
            return
        }

        val uid = usuarioActual.uid

        db.collection("usuarios")
            .document(uid)
            .get()
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    val estadoServicio = documento.get("estado_servicio") as? Map<*, *>
                    val modoCrudo = estadoServicio?.get("mode") as? String ?: "BASIC"
                    // Modo efectivo: un FULL vencido (o sin full_until válido) se muestra como BASIC,
                    // para que la cabecera no siga anunciando FULL tras expirar el periodo.
                    val modo = crystal.crystal.Suscripcion.modoEfectivo(
                        modoCrudo,
                        crystal.crystal.Suscripcion.fullUntilMillisDe(estadoServicio)
                    )

                    sharedPreferences.edit()
                        .putString("usuario_estado", modo)
                        .apply()

                    binding.txModo.text = modo

                    when (modo) {
                        "FULL" -> binding.txModo.setTextColor(activity.getColor(R.color.verde))
                        "BASIC" -> binding.txModo.setTextColor(activity.getColor(R.color.naranja))
                        "VENTAS" -> {
                            binding.txModo.setTextColor(activity.getColor(R.color.azul))
                            habilitarModoVentas()
                        }
                        else -> binding.txModo.setTextColor(activity.getColor(R.color.color))
                    }
                } else {
                    val estadoPorDefecto = "BASIC"
                    sharedPreferences.edit()
                        .putString("usuario_estado", estadoPorDefecto)
                        .apply()
                    binding.txModo.text = estadoPorDefecto
                    binding.txModo.setTextColor(activity.getColor(R.color.naranja))
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    activity,
                    "Sin conexión. Usando estado guardado.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun cargarEstadoTerminal() {
        val uidPatron = sharedPreferences.getString("patron_uid", null)
        val nombreVendedor = sharedPreferences.getString("nombre_vendedor", "Terminal")

        if (uidPatron == null) {
            binding.txModo.text = "ERROR"
            binding.txModo.setTextColor(activity.getColor(R.color.fucsia))
            Toast.makeText(activity, "Error: Sesión de terminal corrupta", Toast.LENGTH_LONG).show()
            return
        }

        val modo = sharedPreferences.getString("usuario_estado", "VENTAS") ?: "VENTAS"

        Log.d("RoleConfigManager", "📱 Terminal cargando - Modo: $modo, Vendedor: $nombreVendedor")

        binding.txUser.text = nombreVendedor
        binding.txModo.text = "TERMINAL"
        binding.txModo.setTextColor(activity.getColor(R.color.azul))

        if (modo == "VENTAS") {
            activity.lifecycleScope.launch {
                cargarRolYConfigurar()
            }
        }
    }

    fun obtenerIdUsuarioActual(): String {
        val tipoSesion = sharedPreferences.getString("session_type", null)

        return if (tipoSesion == "TERMINAL") {
            val uidPatron = sharedPreferences.getString("patron_uid", null)
            Log.d("RoleConfigManager", "📋 Terminal - usando UID del patrón: $uidPatron")
            uidPatron ?: "usuarioActualEjemplo"
        } else {
            val uidUsuario = auth.currentUser?.uid
            Log.d("RoleConfigManager", "📋 Usuario normal - UID: $uidUsuario")
            uidUsuario ?: "usuarioActualEjemplo"
        }
    }

    @SuppressLint("HardwareIds")
    fun verificarAutorizacionTerminal() {
        val tipoSesion = sharedPreferences.getString("session_type", null)

        if (tipoSesion == "TERMINAL") {
            val patronUid = sharedPreferences.getString("patron_uid", null)
            val deviceId = Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)

            if (patronUid != null) {
                activity.lifecycleScope.launch {
                    try {
                        val doc = withContext(Dispatchers.IO) {
                            db.collection("usuarios")
                                .document(patronUid)
                                .collection("plan_ventas")
                                .document("dispositivos")
                                .collection("autorizados")
                                .document(deviceId)
                                .get()
                                .await()
                        }

                        if (!doc.exists()) {
                            withContext(Dispatchers.Main) {
                                cerrarSesionTerminal()
                            }
                        }
                    } catch (_: Exception) {
                        // Error de red, permitir continuar
                    }
                }
            }
        }
    }

    private fun cerrarSesionTerminal() {
        sharedPreferences.edit().clear().apply()

        Toast.makeText(
            activity,
            "⚠️ Este terminal fue desautorizado. Contacta al administrador.",
            Toast.LENGTH_LONG
        ).show()

        val intent = Intent(activity, InicioActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }

    @SuppressLint("HardwareIds")
    suspend fun verificarAutorizacionVentas() {
        val esTerminalPIN = sharedPreferences.getBoolean("es_terminal_pin", false)
        val deviceId = Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)

        val uid = if (esTerminalPIN) {
            sharedPreferences.getString("patron_uid", null)
        } else {
            auth.currentUser?.uid
        }

        if (uid == null) {
            Log.e("RoleConfigManager", "❌ No se pudo obtener UID para verificación")
            return
        }

        try {
            Log.d("RoleConfigManager", "🔍 Verificando autorización...")
            Log.d("RoleConfigManager", "   UID: $uid")
            Log.d("RoleConfigManager", "   DeviceID: $deviceId")
            Log.d("RoleConfigManager", "   Es terminal: $esTerminalPIN")

            val dispositivoDoc = db.collection("usuarios")
                .document(uid)
                .collection("plan_ventas")
                .document("dispositivos")
                .collection("autorizados")
                .document(deviceId)
                .get()
                .await()

            if (!dispositivoDoc.exists()) {
                Log.e("RoleConfigManager", "❌ Dispositivo no encontrado en Firestore")

                if (!esTerminalPIN) {
                    withContext(Dispatchers.Main) {
                        mostrarDialogoNoAutorizado()
                    }
                } else {
                    Log.w("RoleConfigManager", "⚠️ Terminal no encontrado en Firestore pero tiene sesión local")
                }
                return
            }

            val activo = dispositivoDoc.getBoolean("activo") ?: false

            Log.d("RoleConfigManager", "✅ Dispositivo encontrado - Activo: $activo")

            if (!activo) {
                withContext(Dispatchers.Main) {
                    mostrarDialogoDesautorizado()
                }
            } else {
                Log.d("RoleConfigManager", "✅ Dispositivo autorizado correctamente")
            }
        } catch (e: Exception) {
            Log.e("RoleConfigManager", "❌ Error verificando autorización: ${e.message}", e)

            if (!esTerminalPIN) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        activity,
                        "Error verificando autorización. Verifica tu conexión.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun mostrarDialogoNoAutorizado() {
        AlertDialog.Builder(activity)
            .setTitle("⚠️ Dispositivo no autorizado")
            .setMessage("Este dispositivo no está autorizado para plan VENTAS. Contacta al administrador.")
            .setPositiveButton("Cerrar sesión") { _, _ ->
                auth.signOut()
                activity.startActivity(Intent(activity, InicioActivity::class.java))
                activity.finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun mostrarDialogoDesautorizado() {
        AlertDialog.Builder(activity)
            .setTitle("⚠️ Dispositivo desautorizado")
            .setMessage("Este dispositivo ha sido desautorizado desde el panel de administración.")
            .setPositiveButton("Cerrar sesión") { _, _ ->
                auth.signOut()
                activity.startActivity(Intent(activity, InicioActivity::class.java))
                activity.finish()
            }
            .setCancelable(false)
            .show()
    }

    fun obtenerEstadoUsuario(): String {
        return sharedPreferences.getString("usuario_estado", "BASIC") ?: "BASIC"
    }

    private fun habilitarModoVentas() {
        activity.lifecycleScope.launch {
            cargarRolYConfigurar()
        }
    }

    // --- Configuración por rol ---

    suspend fun cargarRolYConfigurar() {
        val esTerminalPIN = sharedPreferences.getBoolean("es_terminal_pin", false)

        if (esTerminalPIN) {
            cargarConfiguracionTerminalPIN()
        } else {
            cargarConfiguracionNormal()
        }
    }

    private suspend fun cargarConfiguracionTerminalPIN() {
        sharedPreferences.getString("patron_uid", null) ?: return
        val nombreVendedor = sharedPreferences.getString("nombre_vendedor", "Terminal") ?: "Terminal"

        val rolDispositivo = "TERMINAL"
        val esPatron = false
        val nombre = extraerPrimerNombre(nombreVendedor)

        callback?.onRoleConfigured(rolDispositivo, esPatron, nombre)

        withContext(Dispatchers.Main) {
            configurarTerminalVentas(nombre)
        }
    }

    @SuppressLint("HardwareIds")
    private suspend fun cargarConfiguracionNormal() {
        val uid = auth.currentUser?.uid ?: return
        val deviceId = Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)

        val usuarioDoc = db.collection("usuarios").document(uid).get().await()

        val nombreCompleto = usuarioDoc.getString("nombre")
            ?: auth.currentUser?.displayName
            ?: auth.currentUser?.email?.substringBefore("@")
            ?: "Usuario"

        val primerNombre = extraerPrimerNombre(nombreCompleto)

        val estadoServicio = usuarioDoc.get("estado_servicio") as? Map<*, *>
        val modo = estadoServicio?.get("mode") as? String ?: "BASIC"

        if (modo != "VENTAS") {
            callback?.onRoleConfigured("PATRON", true, primerNombre)

            withContext(Dispatchers.Main) {
                aplicarConfiguracionPorRol("PATRON", true, primerNombre)
            }
            return
        }

        try {
            val dispositivoDoc = db.collection("usuarios")
                .document(uid)
                .collection("plan_ventas")
                .document("dispositivos")
                .collection("autorizados")
                .document(deviceId)
                .get()
                .await()

            val rolDispositivo: String
            val esPatron: Boolean
            val nombre: String

            if (dispositivoDoc.exists()) {
                rolDispositivo = dispositivoDoc.getString("rol") ?: "TERMINAL"
                esPatron = (rolDispositivo == "PATRON")

                nombre = if (esPatron) {
                    primerNombre
                } else {
                    val nombreTerminal = dispositivoDoc.getString("nombre_vendedor")
                        ?: dispositivoDoc.getString("modelo")
                        ?: Build.MODEL
                        ?: "Terminal"
                    extraerPrimerNombre(nombreTerminal)
                }
            } else {
                rolDispositivo = "TERMINAL"
                esPatron = false
                nombre = "No autorizado"
            }

            Log.d("RoleConfigManager", "✅ Rol: $rolDispositivo, Patron: $esPatron, Nombre: $nombre")

            callback?.onRoleConfigured(rolDispositivo, esPatron, nombre)

            withContext(Dispatchers.Main) {
                aplicarConfiguracionPorRol(rolDispositivo, esPatron, nombre)
            }

        } catch (e: Exception) {
            Log.e("RoleConfigManager", "Error cargando rol: ${e.message}")

            callback?.onRoleConfigured("TERMINAL", false, "Error")

            withContext(Dispatchers.Main) {
                aplicarConfiguracionPorRol("TERMINAL", false, "Error")
            }
        }
    }

    private fun extraerPrimerNombre(nombreCompleto: String): String {
        if (nombreCompleto.isBlank()) return "Usuario"

        val nombreLimpio = nombreCompleto.trim()
        val primerNombre = nombreLimpio.split(" ", "\t", "\n")
            .firstOrNull { it.isNotBlank() }
            ?: "Usuario"

        return primerNombre.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

    private fun aplicarConfiguracionPorRol(rolDispositivo: String, esPatron: Boolean, nombreVendedor: String) {
        val modo = obtenerEstadoUsuario()

        when {
            modo == "VENTAS" && esPatron -> configurarPatronVentas(nombreVendedor)
            modo == "VENTAS" && !esPatron -> configurarTerminalVentas(nombreVendedor)
            else -> configurarModoNormal(nombreVendedor)
        }
    }

    private fun configurarPatronVentas(nombreVendedor: String) {
        Log.d("RoleConfigManager", "🔑 Configurando como PATRON VENTAS")

        binding.lyPiernas.visibility = View.VISIBLE
        binding.txPuntoV.visibility = View.GONE

        binding.btUser.visibility = View.VISIBLE
        binding.btnChat.visibility = View.VISIBLE
        binding.tallerCal.visibility = View.VISIBLE
        binding.btWallet.visibility = View.VISIBLE
        binding.btnCatalogo.visibility = View.VISIBLE
        binding.btAyuda.visibility = View.VISIBLE

        binding.txUser.text = nombreVendedor
        binding.txModo.text = "VENTAS"
        binding.txModo.setTextColor(activity.getColor(R.color.azul))

        binding.lyUs.setOnClickListener {
            activity.startActivity(Intent(activity, GestionDispositivosActivity::class.java))
        }
        callback?.guardarDatos()
    }

    private fun configurarTerminalVentas(nombreVendedor: String) {
        binding.lyPiernas.visibility = View.VISIBLE
        binding.txPuntoV.visibility = View.VISIBLE

        binding.btUser.visibility = View.VISIBLE
        // La terminal SÍ accede al chat del patrón (bandeja compartida de pedidos en línea).
        binding.btnChat.visibility = View.VISIBLE
        binding.tallerCal.visibility = View.GONE
        binding.btWallet.visibility = View.GONE
        binding.btnCatalogo.visibility = View.VISIBLE
        binding.btAyuda.visibility = View.VISIBLE

        // Asegurar el custom claim `patronUid` para que las reglas dejen a la terminal usar el chat.
        asegurarClaimPatron()

        binding.txUser.text = nombreVendedor
        binding.txModo.text = "TERMINAL"
        binding.txModo.setTextColor(activity.getColor(R.color.azul))

        binding.lyUs.setOnClickListener {
            Toast.makeText(activity, "⚠️ Acceso restringido al administrador", Toast.LENGTH_SHORT).show()
        }

        try {
            binding.precioTotal.isClickable = false
        } catch (e: Exception) {
            Log.e("RoleConfigManager", "Error ocultando elementos: ${e.message}")
        }
        callback?.guardarDatos()
    }

    private fun configurarModoNormal(nombreVendedor: String) {
        Log.d("RoleConfigManager", "👤 Configurando modo normal (BASIC/FULL)")

        binding.lyPiernas.visibility = View.GONE
        binding.txPuntoV.visibility = View.GONE

        binding.btUser.visibility = View.VISIBLE
        binding.btnChat.visibility = View.VISIBLE
        binding.tallerCal.visibility = View.VISIBLE
        binding.btWallet.visibility = View.VISIBLE
        binding.btnCatalogo.visibility = View.VISIBLE
        binding.btAyuda.visibility = View.VISIBLE

        val modo = obtenerEstadoUsuario()
        binding.txUser.text = nombreVendedor
        binding.txModo.text = modo

        when (modo) {
            "FULL" -> binding.txModo.setTextColor(activity.getColor(R.color.verde))
            "BASIC" -> binding.txModo.setTextColor(activity.getColor(R.color.naranja))
            else -> binding.txModo.setTextColor(activity.getColor(R.color.color))
        }

        if (binding.tvpCliente.text.isNullOrEmpty() || binding.tvpCliente.text == "Cliente") {
            binding.tvpCliente.text = "Presupuesto"
        }
        callback?.guardarDatos()
    }

    fun verificarAutenticacionTerminal() {
        val esTerminalPIN = sharedPreferences.getBoolean("es_terminal_pin", false)
        val firebaseAuth = FirebaseAuth.getInstance()

        if (esTerminalPIN) {
            if (firebaseAuth.currentUser == null) {
                firebaseAuth.signInAnonymously()
                    .addOnSuccessListener { asegurarClaimPatron() }
                    .addOnFailureListener {
                        Toast.makeText(
                            activity,
                            "⚠️ Sin conexión. Presiona Sincronizar cuando tengas internet",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } else {
                Log.d("RoleConfigManager", "✅ Terminal ya autenticado (Auth UID: ${firebaseAuth.currentUser?.uid})")
                asegurarClaimPatron()
            }
        } else {
            Log.d("RoleConfigManager", "ℹ️ Usuario patrón, no requiere verificación de terminal")
        }
    }

    /**
     * Pide al servidor el custom claim `patronUid` para esta terminal (Cloud Function
     * asignarClaimPatron) y refresca el token para que tome efecto, de modo que las reglas dejen a la
     * terminal usar el chat del patrón. Idempotente y silencioso: si no es terminal, no hay auth o
     * falla la red, no hace nada (se reintenta en el próximo arranque).
     */
    @SuppressLint("HardwareIds")
    fun asegurarClaimPatron() {
        val esTerminal = sharedPreferences.getString("session_type", null) == "TERMINAL" ||
            sharedPreferences.getBoolean("es_terminal_pin", false)
        if (!esTerminal) return
        if (auth.currentUser == null) return
        val patronUid = sharedPreferences.getString("patron_uid", null) ?: return
        val deviceId = Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)

        activity.lifecycleScope.launch {
            try {
                FirebaseFunctions.getInstance()
                    .getHttpsCallable("asignarClaimPatron")
                    .call(hashMapOf("patronUid" to patronUid, "deviceId" to deviceId))
                    .await()
                // Refrescar el token para que el claim tome efecto en esta sesión.
                auth.currentUser?.getIdToken(true)?.await()
                Log.d("RoleConfigManager", "✅ Claim patronUid asignado y token refrescado")
            } catch (e: Exception) {
                Log.w("RoleConfigManager", "No se pudo asignar el claim patronUid: ${e.message}")
            }
        }
    }

    fun mostrarDialogoPatronNoEncontrado() {
        AlertDialog.Builder(activity)
            .setTitle("⚠️ Administrador no encontrado")
            .setMessage(
                """
            No se encontró la cuenta del administrador.

            Posibles causas:
            • El administrador eliminó su cuenta
            • Cambió de cuenta de Google
            • Problema de conexión

            Necesitas un nuevo código de autorización.
            """.trimIndent()
            )
            .setPositiveButton("Reconectar") { _, _ ->
                limpiarSesionYReconectar()
            }
            .setNegativeButton("Reintentar") { _, _ ->
                callback?.sincronizarDatosManualmente()
            }
            .setCancelable(false)
            .show()
    }

    fun mostrarOpcionReconectar() {
        AlertDialog.Builder(activity)
            .setTitle("⚠️ Error de conexión")
            .setMessage(
                """
            No se pueden sincronizar los datos.

            ¿El administrador cambió de celular?
            Necesitas un nuevo código QR o PIN para reconectar.
            """.trimIndent()
            )
            .setPositiveButton("Ingresar nuevo código") { _, _ ->
                limpiarSesionYReconectar()
            }
            .setNegativeButton("Reintentar") { _, _ ->
                callback?.sincronizarDatosManualmente()
            }
            .show()
    }

    private fun limpiarSesionYReconectar() {
        sharedPreferences.edit().clear().apply()
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(activity, crystal.crystal.registro.TerminalLoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(intent)
        activity.finish()
    }
}
