package crystal.crystal.registro

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import crystal.crystal.databinding.ActivityPinAuthBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.Normalizer
import kotlin.math.max

class PinAuthActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_RESET_DESDE_WALLET = "reset_desde_wallet"
    }

    private lateinit var binding: ActivityPinAuthBinding
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    // Configuración
    private val pinLength = 4                 // dígitos requeridos
    private val maxAttemptsPin = 5            // intentos PIN
    private val maxAttemptsSec = 5            // intentos respuesta seguridad
    private val lockMinutesPin = 5L           // minutos bloqueo por PIN
    private val lockMinutesSec = 5L           // minutos bloqueo por seguridad
    private val iterPBKDF2 = 10000            // iteraciones PBKDF2

    // Estado interno
    private var resetAutorizado = false       // true tras respuesta correcta o reauth Google
    private var resetDesdeWallet = false

    // Preguntas sugeridas
    private val preguntasSeguridad = listOf(
        "¿Cuál es el nombre de tu primera mascota?",
        "¿En qué ciudad naciste?",
        "¿Cuál es tu apodo de infancia?",
        "¿Cuál es tu película favorita?",
        "¿Cuál fue tu primer colegio?",
        "¿Cuál es el segundo nombre de tu madre?"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        resetDesdeWallet = intent.getBooleanExtra(EXTRA_RESET_DESDE_WALLET, false)
        if (resetDesdeWallet) {
            // WalletActivity ya hizo reauth Google, así que permitimos cambiar PIN
            resetAutorizado = true
        }

        // Entradas numéricas con longitud fija
        listOf(binding.etPin, binding.etPinConfirm, binding.etPinLogin).forEach {
            it.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            it.filters = arrayOf(InputFilter.LengthFilter(pinLength))
            it.transformationMethod = PasswordTransformationMethod.getInstance()
        }

        // Botones
        binding.btCancelar.setOnClickListener { finish() }
        binding.tvOlvidePin.setOnClickListener { onOlvidePin() }

        // Ojos para ver/ocultar PIN
        configurarToggleVisibilidadPin(binding.etPin, binding.ivTogglePinCrear)
        configurarToggleVisibilidadPin(binding.etPinConfirm, binding.ivTogglePinConfirm)
        configurarToggleVisibilidadPin(binding.etPinLogin, binding.ivTogglePinLogin)

        CoroutineScope(Dispatchers.Main).launch { decidirFlujo() }
    }

    // -------------------------------- LÓGICA INICIAL --------------------------------

    private suspend fun decidirFlujo() {
        val uid = auth.currentUser?.uid ?: run {
            toastLargo("Sesión inválida")
            finish(); return
        }

        val docRef = docRefPin(uid)
        val snap = withContext(Dispatchers.IO) { docRef.get().await() }

        if (!snap.exists()) {
            // Primera vez: creación de PIN + registro de pregunta/resp seguridad
            mostrarVistaCreacion(primeraVez = true)
        } else {
            // Si venimos desde Wallet con reauth Google, vamos directo a cambio de PIN
            if (resetDesdeWallet && resetAutorizado) {
                mostrarVistaCreacion(primeraVez = false)
                return
            }

            // Existe PIN: validar
            val lockedUntil = snap.getTimestamp("pinLockedUntil")
            if (estaBloqueado(lockedUntil)) {
                mostrarTiempoRestanteYSalir(lockedUntil)
                return
            }
            mostrarVistaValidacion()
        }
    }

    // -------------------------------- CREACIÓN PIN --------------------------------

    private fun mostrarVistaCreacion(primeraVez: Boolean) {
        binding.grpCrear.visibility = View.VISIBLE
        binding.grpValidar.visibility = View.GONE

        binding.btCrear.setOnClickListener {
            val p1 = binding.etPin.text?.toString()?.trim().orEmpty()
            val p2 = binding.etPinConfirm.text?.toString()?.trim().orEmpty()

            if (p1.length != pinLength || p2.length != pinLength) {
                toastCorto("PIN debe tener $pinLength dígitos")
                return@setOnClickListener
            }
            if (p1 != p2) {
                toastCorto("Los PIN no coinciden")
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.Main).launch {
                val uid = auth.currentUser?.uid ?: return@launch
                val docRef = docRefPin(uid)
                val snap = withContext(Dispatchers.IO) { docRef.get().await() }

                if (primeraVez) {
                    // Primera vez: siempre se pide preguntar/resp
                    pedirPreguntaYRespuesta { pregunta, respuesta ->
                        CoroutineScope(Dispatchers.Main).launch {
                            crearPinSeguro(
                                nuevoPin = p1,
                                registrarPregunta = true,
                                pregunta = pregunta,
                                respuesta = respuesta
                            )
                        }
                    }
                } else {
                    // Reset autorizado: puede venir desde pregunta de seguridad o desde Wallet
                    if (!resetAutorizado) {
                        toastLargo("Operación no autorizada")
                        mostrarVistaValidacion()
                        return@launch
                    }

                    // Verificamos si ya tiene pregunta configurada
                    val yaTienePregunta = snap.getString("securityQuestion").isNullOrBlank().not() &&
                            snap.getString("securityAnswerHash").isNullOrBlank().not()
                    if (!yaTienePregunta) {
                        pedirPreguntaYRespuesta { pregunta, respuesta ->
                            CoroutineScope(Dispatchers.Main).launch {
                                crearPinSeguro(
                                    nuevoPin = p1,
                                    registrarPregunta = true,
                                    pregunta = pregunta,
                                    respuesta = respuesta
                                )
                            }
                        }
                    } else {
                        crearPinSeguro(
                            nuevoPin = p1,
                            registrarPregunta = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun crearPinSeguro(
        nuevoPin: String,
        registrarPregunta: Boolean,
        pregunta: String? = null,
        respuesta: String? = null
    ) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = docRefPin(uid)

        // Hash del PIN
        val saltPin = SecurityUtils.generateSalt()
        val hashPin = SecurityUtils.pbkdf2(nuevoPin, saltPin, iterPBKDF2)

        // Datos base
        val data = mutableMapOf<String, Any?>(
            "hash" to SecurityUtils.toBase64(hashPin),
            "salt" to SecurityUtils.toBase64(saltPin),
            "iter" to iterPBKDF2,
            "createdAt" to FieldValue.serverTimestamp(),
            "failedCountPin" to 0,
            "pinLockedUntil" to null,
            "failedCountSec" to 0,
            "secLockedUntil" to null
        )

        // Pregunta/resp de seguridad
        if (registrarPregunta) {
            val respNorm = normalizarRespuesta(respuesta.orEmpty())
            val saltSec = SecurityUtils.generateSalt()
            val hashSec = SecurityUtils.pbkdf2(respNorm, saltSec, iterPBKDF2)
            data["securityQuestion"] = pregunta.orEmpty()
            data["securityAnswerSalt"] = SecurityUtils.toBase64(saltSec)
            data["securityAnswerHash"] = SecurityUtils.toBase64(hashSec)
        }

        withContext(Dispatchers.IO) {
            docRef.set(data, SetOptions.merge()).await()
            // Auditoría simple
            db.collection("usuarios").document(uid)
                .collection("security")
                .document("walletPin_audit_${System.currentTimeMillis()}")
                .set(mapOf("changedAt" to Timestamp.now()))
                .await()
        }

        toastCorto("PIN guardado")
        abrirWallet()
    }

    // -------------------------------- VALIDACIÓN PIN --------------------------------

    private fun mostrarVistaValidacion() {
        binding.grpCrear.visibility = View.GONE
        binding.grpValidar.visibility = View.VISIBLE

        binding.btIngresar.setOnClickListener {
            val pin = binding.etPinLogin.text?.toString()?.trim().orEmpty()
            if (pin.length != pinLength) {
                toastCorto("PIN debe tener $pinLength dígitos")
                return@setOnClickListener
            }
            CoroutineScope(Dispatchers.Main).launch { validarPin(pin) }
        }
    }

    private suspend fun validarPin(pin: String) {
        val uid = auth.currentUser?.uid ?: return
        val docRef = docRefPin(uid)
        val snap = withContext(Dispatchers.IO) { docRef.get().await() }
        if (!snap.exists()) { mostrarVistaCreacion(primeraVez = true); return }

        val lockedUntil = snap.getTimestamp("pinLockedUntil")
        if (estaBloqueado(lockedUntil)) {
            mostrarTiempoRestanteYSalir(lockedUntil); return
        }

        val saltB64 = snap.getString("salt").orEmpty()
        val hashB64 = snap.getString("hash").orEmpty()
        val iter = (snap.getLong("iter") ?: iterPBKDF2.toLong()).toInt()
        val failedCount = (snap.getLong("failedCountPin") ?: 0L).toInt()

        val expected = SecurityUtils.fromBase64(hashB64)
        val salt = SecurityUtils.fromBase64(saltB64)
        val actual = SecurityUtils.pbkdf2(pin, salt, iter)

        val ok = SecurityUtils.constantTimeEquals(expected, actual)

        if (ok) {
            withContext(Dispatchers.IO) {
                docRef.update(mapOf("failedCountPin" to 0, "pinLockedUntil" to null)).await()
            }
            abrirWallet()
        } else {
            val nuevosFallos = failedCount + 1
            val updates = mutableMapOf<String, Any?>("failedCountPin" to nuevosFallos)
            if (nuevosFallos >= maxAttemptsPin) {
                val lockUntil = Timestamp(java.util.Date(System.currentTimeMillis() + lockMinutesPin * 60 * 1000))
                updates["pinLockedUntil"] = lockUntil
            }
            withContext(Dispatchers.IO) { docRef.set(updates, SetOptions.merge()).await() }
            val restantes = max(0, maxAttemptsPin - nuevosFallos)
            val msg = if (restantes > 0) "PIN incorrecto. Intentos restantes: $restantes"
            else "PIN bloqueado por $lockMinutesPin min."
            toastLargo(msg)
            if (restantes == 0) finish()
        }
    }

    // ---------------------------- OLVIDÉ MI PIN ----------------------------

    private fun onOlvidePin() {
        CoroutineScope(Dispatchers.Main).launch {
            val uid = auth.currentUser?.uid ?: run {
                toastLargo("Sesión inválida"); return@launch
            }

            val docRef = docRefPin(uid)
            val snap = withContext(Dispatchers.IO) { docRef.get().await() }
            if (!snap.exists()) {
                toastLargo("Aún no has creado un PIN. Crea uno ahora.")
                mostrarVistaCreacion(primeraVez = true)
                return@launch
            }

            val secLockedUntil = snap.getTimestamp("secLockedUntil")
            if (estaBloqueado(secLockedUntil)) {
                val mins = minutosRestantes(secLockedUntil)
                toastLargo("Recuperación bloqueada. Intenta en $mins min.")
                return@launch
            }

            val pregunta = snap.getString("securityQuestion").orEmpty()
            val saltRespB64 = snap.getString("securityAnswerSalt")
            val hashRespB64 = snap.getString("securityAnswerHash")

            if (pregunta.isBlank() || saltRespB64.isNullOrBlank() || hashRespB64.isNullOrBlank()) {
                toastLargo("No tienes pregunta de seguridad configurada. Ingresa con tu PIN y configúrala.")
                return@launch
            }

            pedirRespuestaDeSeguridad(pregunta, saltRespB64, hashRespB64, docRef)
        }
    }

    private fun pedirRespuestaDeSeguridad(
        pregunta: String,
        saltRespB64: String,
        hashRespB64: String,
        docRef: com.google.firebase.firestore.DocumentReference
    ) {
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 8, 0, 0)
        }

        val et = EditText(this).apply {
            hint = "Escribe tu respuesta"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val ivToggle = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_menu_view)
            setPadding(16, 16, 16, 16)
        }

        cont.addView(et)
        cont.addView(ivToggle)

        configurarToggleVisibilidadTexto(et, ivToggle)

        AlertDialog.Builder(this)
            .setTitle("Verificación de seguridad")
            .setMessage(pregunta)
            .setView(cont)
            .setPositiveButton("Verificar") { dlg, _ ->
                val resp = et.text?.toString()?.trim().orEmpty()
                if (resp.isEmpty()) {
                    toastCorto("La respuesta no puede estar vacía")
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        validarRespuestaSeguridad(resp, saltRespB64, hashRespB64, docRef)
                    }
                }
                dlg.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private suspend fun validarRespuestaSeguridad(
        respuestaIngresada: String,
        saltRespB64: String,
        hashRespB64: String,
        docRef: com.google.firebase.firestore.DocumentReference
    ) {
        val uid = auth.currentUser?.uid ?: return
        val snap = withContext(Dispatchers.IO) { docRef.get().await() }

        val respNorm = normalizarRespuesta(respuestaIngresada)
        val expected = SecurityUtils.fromBase64(hashRespB64)
        val salt = SecurityUtils.fromBase64(saltRespB64)
        val actual = SecurityUtils.pbkdf2(respNorm, salt, iterPBKDF2)

        if (SecurityUtils.constantTimeEquals(expected, actual)) {
            withContext(Dispatchers.IO) {
                docRef.update(mapOf("failedCountSec" to 0, "secLockedUntil" to null)).await()
            }
            resetAutorizado = true
            toastLargo("Verificación correcta. Define un nuevo PIN.")
            mostrarVistaCreacion(primeraVez = false)
        } else {
            val fallos = (snap.getLong("failedCountSec") ?: 0L).toInt() + 1
            val updates = mutableMapOf<String, Any?>("failedCountSec" to fallos)
            if (fallos >= maxAttemptsSec) {
                updates["secLockedUntil"] = Timestamp(java.util.Date(System.currentTimeMillis() + lockMinutesSec * 60 * 1000))
            }
            withContext(Dispatchers.IO) { docRef.set(updates, SetOptions.merge()).await() }
            val restantes = max(0, maxAttemptsSec - fallos)
            val msg = if (restantes > 0) "Respuesta incorrecta. Intentos restantes: $restantes"
            else "Recuperación bloqueada por $lockMinutesSec min."
            toastLargo(msg)
        }
    }

    // ------------------------------- DIÁLOGOS AUXILIARES -------------------------------

    private fun pedirPreguntaYRespuesta(onOk: (pregunta: String, respuesta: String) -> Unit) {
        val cont = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }

        val spinner = Spinner(this)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, preguntasSeguridad)

        val filaRespuesta = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val etRespuesta = EditText(this).apply {
            hint = "Escribe tu respuesta (mínimo 3 caracteres)"
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            transformationMethod = PasswordTransformationMethod.getInstance()
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val ivToggle = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_menu_view)
            setPadding(16, 16, 16, 16)
        }

        filaRespuesta.addView(etRespuesta)
        filaRespuesta.addView(ivToggle)

        configurarToggleVisibilidadTexto(etRespuesta, ivToggle)

        cont.addView(TextView(this).apply { text = "Pregunta de seguridad:" })
        cont.addView(spinner)
        cont.addView(TextView(this).apply { text = "Respuesta:" })
        cont.addView(filaRespuesta)

        AlertDialog.Builder(this)
            .setTitle("Configurar seguridad")
            .setView(cont)
            .setPositiveButton("Guardar") { dlg, _ ->
                val preg = preguntasSeguridad[spinner.selectedItemPosition]
                val resp = etRespuesta.text?.toString()?.trim().orEmpty()
                if (resp.length < 3) {
                    toastLargo("La respuesta debe tener al menos 3 caracteres")
                } else {
                    onOk(preg, resp)
                }
                dlg.dismiss()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // --------------------------------- UTILIDADES ---------------------------------

    private fun docRefPin(uid: String) =
        db.collection("usuarios").document(uid)
            .collection("security").document("walletPin")

    private fun estaBloqueado(lockedUntil: Timestamp?): Boolean {
        val t = lockedUntil?.toDate()?.time ?: return false
        return t > System.currentTimeMillis()
    }

    private fun minutosRestantes(lockedUntil: Timestamp?): Long {
        val t = lockedUntil?.toDate()?.time ?: 0
        val diff = t - System.currentTimeMillis()
        return (diff / 60000L) + 1
    }

    private fun mostrarTiempoRestanteYSalir(lockedUntil: Timestamp?) {
        val mins = minutosRestantes(lockedUntil)
        toastLargo("PIN bloqueado. Intenta en $mins min.")
        finish()
    }

    private fun normalizarRespuesta(raw: String): String {
        val lower = raw.trim().lowercase()
        val sinAcentos = Normalizer.normalize(lower, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
        return sinAcentos.replace("\\s+".toRegex(), " ")
    }

    private fun toastCorto(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    private fun toastLargo(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()

    private fun abrirWallet() {
        startActivity(Intent(this, WalletActivity::class.java))
        finish()
    }

    // ---- Ojos (ver/ocultar) para PIN en Activity ----
    private fun configurarToggleVisibilidadPin(editText: EditText, imageView: ImageView) {
        var visible = false
        imageView.setOnClickListener {
            visible = !visible
            editText.transformationMethod =
                if (visible) null else PasswordTransformationMethod.getInstance()
            editText.setSelection(editText.text?.length ?: 0)
        }
    }

    // ---- Ojos para campos de texto en diálogos (respuesta seguridad) ----
    private fun configurarToggleVisibilidadTexto(editText: EditText, imageView: ImageView) {
        var visible = false
        imageView.setOnClickListener {
            visible = !visible
            editText.transformationMethod =
                if (visible) null else PasswordTransformationMethod.getInstance()
            editText.setSelection(editText.text?.length ?: 0)
        }
    }
}
