package crystal.crystal.registro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import crystal.crystal.databinding.ActivityWalletBinding
import crystal.crystal.wallet.ReclamoRecargaActivity
import java.util.Date

class WalletActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletBinding

    // =================== Firebase ===================
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }
    private val storage by lazy { FirebaseStorage.getInstance() }
    private val recognizer by lazy { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }

    // =================== Usuario actual ===================
    private val user: FirebaseUser?
        get() = auth.currentUser

    // =================== Estado de wallet ===================
    private var walletCongelada: Boolean = false
    private var listenerWalletState: ListenerRegistration? = null
    private var listenerRecargas: ListenerRegistration? = null

    // =================== Cronómetro de plan ===================
    private var countdownRunnable: Runnable? = null
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())

    // =================== Selector de imagen ===================
    private val picker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            procesarComprobante(uri)
        } else {
            Toast.makeText(this, "No se seleccionó imagen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarWallet)

        inicializarUI()
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerWalletState?.remove()
        listenerRecargas?.remove()
        detenerCronometro()
    }

    // =================== FUNCIÓN CORREGIDA inicializarUI() ===================

    private fun inicializarUI() {
        val usuario = user ?: run {
            Toast.makeText(this, "Debes iniciar sesión con tu cuenta Google.", Toast.LENGTH_LONG)
                .show()
            finish()
            return
        }

        // --------- Saldo en vivo ----------
        db.collection("usuarios").document(usuario.uid)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snap == null || !snap.exists()) {
                    return@addSnapshotListener
                }

                val saldoCent = snap.getLong("wallet_saldo_cent") ?: 0
                val saldo = saldoCent / 100.0
                binding.tvSaldo.text = "Saldo: S/ %.2f".format(saldo)
            }

        // ⭐ --------- Estado del plan y cronómetro (LISTENER COMBINADO) ----------
        db.collection("usuarios").document(usuario.uid)
            .addSnapshotListener { snap, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                if (snap == null || !snap.exists()) {
                    return@addSnapshotListener
                }

                // Leer estado_servicio
                val estadoServicio = snap.get("estado_servicio") as? Map<*, *>
                val modo = estadoServicio?.get("mode") as? String ?: "BASIC"
                val source = estadoServicio?.get("source") as? String ?: ""
                val fullUntil = estadoServicio?.get("full_until") as? com.google.firebase.Timestamp

                // ⭐ Si es VENTAS, cargar terminales desde plan_ventas/config
                if (modo == "VENTAS") {
                    cargarYMostrarPlanVentas(usuario.uid, fullUntil)
                } else {
                    // Planes BASIC y FULL normales
                    mostrarPlanNormal(modo, source, fullUntil)
                }
            }

        // --------- Lista de recargas ----------
        val listaRecargas = mutableListOf<Recarga>()
        val adapter = RecargaAdapter(listaRecargas)
        binding.rvRecargas.layoutManager = LinearLayoutManager(this)
        binding.rvRecargas.adapter = adapter

        listenerRecargas = db.collection("usuarios").document(usuario.uid)
            .collection("recargas")
            .orderBy("ts_envio", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, _ ->
                listaRecargas.clear()
                for (doc in snap?.documents ?: emptyList()) {
                    val recarga = doc.toObject(Recarga::class.java)
                    if (recarga != null) listaRecargas.add(recarga)
                }
                adapter.notifyDataSetChanged()
            }

        // --------- Estado wallet_frozen ----------
        listenerWalletState = db.collection("usuarios").document(usuario.uid)
            .collection("security")
            .document("wallet_state")
            .addSnapshotListener { snap, _ ->
                walletCongelada = snap?.getBoolean("wallet_frozen") == true

                binding.tvEstadoWallet.text =
                    if (walletCongelada) "Wallet: CONGELADA" else "Wallet: ACTIVA"

                try {
                    binding.btnCongelar.isEnabled = !walletCongelada
                    binding.btnDescongelar.isEnabled = walletCongelada
                } catch (_: Exception) {
                }
            }

        // --------- Botones: Congelar / descongelar ----------
        try {
            binding.btnCongelar.setOnClickListener {
                if (walletCongelada) {
                    Toast.makeText(this, "Tu wallet ya está congelada.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                cambiarEstadoCongelado(true)
            }

            binding.btnDescongelar.setOnClickListener {
                if (!walletCongelada) {
                    Toast.makeText(this, "Tu wallet ya está activa.", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                cambiarEstadoCongelado(false)
            }
        } catch (_: Exception) {
        }

        // --------- Botón: seleccionar comprobante ----------
        binding.btnSeleccionarComprobante.setOnClickListener {
            if (walletCongelada) {
                Toast.makeText(
                    this,
                    "Tu wallet está congelada. No puedes enviar comprobantes.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            try {
                picker.launch("image/*")
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Error abriendo galería: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // --------- Botón: recarga manual ----------
        binding.btnRecargar.setOnClickListener {
            val intent = Intent(this, ReclamoRecargaActivity::class.java)
            startActivity(intent)
        }

        // --------- Botón: Ver / cambiar plan ----------
        try {
            binding.btnPlanes.setOnClickListener {
                val u = user
                if (u == null) {
                    Toast.makeText(
                        this,
                        "Debes iniciar sesión con tu cuenta Google.",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    startActivity(Intent(this, PlanSelectionActivity::class.java))
                }
            }
        } catch (_: Exception) {
        }
    }

    // ⭐ =================== NUEVAS FUNCIONES PARA PLAN VENTAS ===================

    /**
     * Carga y muestra el plan VENTAS con número de terminales desde plan_ventas/config
     */
    private fun cargarYMostrarPlanVentas(
        uid: String,
        fullUntil: com.google.firebase.Timestamp?
    ) {
        // Mostrar temporal mientras carga
        binding.tvEstadoPlan.text = "Plan: VENTAS (cargando...)"

        // ⭐ Cargar desde plan_ventas/config (fuente única de verdad)
        db.collection("usuarios")
            .document(uid)
            .collection("plan_ventas")
            .document("config")
            .get()
            .addOnSuccessListener { doc ->
                val numTerminales = doc.getLong("terminales_contratadas")?.toInt() ?: 1
                val planTexto = "Plan: VENTAS ($numTerminales terminal${if (numTerminales > 1) "es" else ""})"
                binding.tvEstadoPlan.text = planTexto

                // Iniciar cronómetro si hay fecha de vencimiento
                if (fullUntil != null) {
                    iniciarCronometro(fullUntil, "VENTAS")
                } else {
                    detenerCronometro()
                    binding.tvCronometroPlan.text = "Sin límite de tiempo"
                }
            }
            .addOnFailureListener { e ->
                Log.e("WalletActivity", "Error cargando terminales: ${e.message}")
                // Si falla, mostrar sin número
                binding.tvEstadoPlan.text = "Plan: VENTAS"

                // Intentar cronómetro de todas formas
                if (fullUntil != null) {
                    iniciarCronometro(fullUntil, "VENTAS")
                } else {
                    detenerCronometro()
                    binding.tvCronometroPlan.text = "Sin límite de tiempo"
                }
            }
    }

    /**
     * Muestra planes normales (BASIC y FULL)
     */
    private fun mostrarPlanNormal(
        modo: String,
        source: String,
        fullUntil: com.google.firebase.Timestamp?
    ) {
        val planTexto = when (modo) {
            "BASIC" -> "Plan: BASIC (Gratuito)"
            "FULL" -> {
                val origen = when (source) {
                    "MENSUAL" -> "Mensual"
                    "ANUAL" -> "Anual"
                    "PREPAGO" -> "Prepago"
                    "TRIAL_1M" -> "Trial"
                    else -> source
                }
                "Plan: FULL ($origen)"
            }
            else -> "Plan: $modo"
        }

        binding.tvEstadoPlan.text = planTexto

        // Iniciar cronómetro si hay fecha de vencimiento
        if (fullUntil != null && modo != "BASIC") {
            iniciarCronometro(fullUntil, modo)
        } else {
            detenerCronometro()
            binding.tvCronometroPlan.text = if (modo == "BASIC") {
                "Actualiza a FULL o VENTAS"
            } else {
                "Sin límite de tiempo"
            }
        }
    }

    // =================== FUNCIONES DEL CRONÓMETRO ===================

    /**
     * Inicia el cronómetro regresivo del plan
     */
    private fun iniciarCronometro(fullUntil: com.google.firebase.Timestamp, modo: String) {
        // Detener cronómetro anterior si existe
        detenerCronometro()

        countdownRunnable = object : Runnable {
            override fun run() {
                val ahora = System.currentTimeMillis()
                val vencimiento = fullUntil.toDate().time
                val tiempoRestante = vencimiento - ahora

                if (tiempoRestante <= 0) {
                    // Plan vencido
                    binding.tvCronometroPlan.text = "⚠️ Plan vencido"
                    binding.tvCronometroPlan.setTextColor(
                        ContextCompat.getColor(this@WalletActivity, android.R.color.holo_red_dark)
                    )
                    detenerCronometro()
                    return
                }

                // Calcular días, horas, minutos
                val dias = java.util.concurrent.TimeUnit.MILLISECONDS.toDays(tiempoRestante)
                val horas = java.util.concurrent.TimeUnit.MILLISECONDS.toHours(tiempoRestante) % 24
                val minutos = java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(tiempoRestante) % 60

                // Formatear texto
                val cronometroTexto = buildString {
                    append("⏱️ ")

                    if (dias > 0) {
                        append("$dias día${if (dias != 1L) "s" else ""}")
                    }

                    if (dias > 0 || horas > 0) {
                        if (dias > 0) append(", ")
                        append("$horas hora${if (horas != 1L) "s" else ""}")
                    }

                    if (dias == 0L) {
                        if (horas > 0) append(", ")
                        append("$minutos min")
                    }
                }

                binding.tvCronometroPlan.text = cronometroTexto

                // Cambiar color según tiempo restante
                val color = when {
                    dias < 1 -> android.R.color.holo_red_dark  // Menos de 1 día: rojo
                    dias < 3 -> android.R.color.holo_orange_dark  // Menos de 3 días: naranja
                    else -> android.R.color.holo_green_dark  // Más de 3 días: verde
                }
                binding.tvCronometroPlan.setTextColor(ContextCompat.getColor(this@WalletActivity, color))

                // Actualizar cada minuto
                handler.postDelayed(this, 60000) // 60 segundos
            }
        }

        // Ejecutar inmediatamente
        handler.post(countdownRunnable!!)
    }

    /**
     * Detiene el cronómetro
     */
    private fun detenerCronometro() {
        countdownRunnable?.let { handler.removeCallbacks(it) }
        countdownRunnable = null
    }

    // =================== Congelar / descongelar wallet ===================
    private fun cambiarEstadoCongelado(congelar: Boolean) {
        val u = user ?: run {
            Toast.makeText(this, "Usuario no autenticado.", Toast.LENGTH_LONG).show()
            return
        }

        val datos = mapOf(
            "wallet_frozen" to congelar,
            "updatedAt" to FieldValue.serverTimestamp(),
            "origen" to "wallet_activity"
        )

        db.collection("usuarios").document(u.uid)
            .collection("security")
            .document("wallet_state")
            .set(datos, SetOptions.merge())
            .addOnSuccessListener {
                walletCongelada = congelar
                val msg = if (congelar) {
                    "Wallet congelada en todos tus dispositivos."
                } else {
                    "Wallet activada de nuevo."
                }
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    this,
                    "Error cambiando estado de wallet: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    // ========= Helper local: clasificar tipoVoucher a partir del texto bruto =========
    private fun clasificarTipoVoucherLocal(bruto: String, sugerido: String?): String {
        val t = bruto.lowercase()
        val sug = (sugerido ?: "").lowercase()

        Log.d("WalletActivity", """
            🔍 Clasificando tipo voucher:
            - Sugerido por OCR: ${sugerido ?: "ninguno"}
            - Texto (primeros 100 chars): ${bruto.take(100)}
        """.trimIndent())

        // Si el OCR ya trae un tipo válido Y NO ES "desconocido", respétalo
        if (sug == "yape_yape" || sug == "plin_yape" || sug == "yape_plin") {
            Log.d("WalletActivity", "✅ Usando tipo del OCR: $sug")
            return sug
        }

        // Si es "desconocido", intentar clasificación local
        Log.d("WalletActivity", "⚠️ OCR no clasificó (sugerido=$sug), clasificando localmente...")

        // Helpers para detección robusta
        fun contienePlin(texto: String): Boolean {
            val tNorm = texto.replace("ı", "i").replace("1", "i")
            return tNorm.contains("plin") ||
                    tNorm.contains("pl1n") ||
                    tNorm.contains("plın") ||
                    Regex("""pl[i1ı]n""", RegexOption.IGNORE_CASE).containsMatchIn(texto)
        }

        fun contieneInterbank(texto: String): Boolean {
            return texto.contains("interbank", ignoreCase = true) ||
                    texto.contains("1nterbank", ignoreCase = true) ||
                    texto.contains("ínterbank", ignoreCase = true)
        }

        fun tieneCodigoSeguridad(texto: String): Boolean {
            return texto.contains("cód. de seguridad", ignoreCase = true) ||
                    texto.contains("cod. de seguridad", ignoreCase = true) ||
                    texto.contains("código de seguridad", ignoreCase = true) ||
                    texto.contains("codigo de seguridad", ignoreCase = true)
        }

        fun detectarDestino(texto: String): String? {
            val idx = texto.indexOfAny(listOf("destino:", "destino", "dest1no"), ignoreCase = true)
            if (idx >= 0) {
                val fin = if (idx + 80 < texto.length) idx + 80 else texto.length
                val sub = texto.substring(idx, fin).lowercase()

                if (sub.contains("yape")) return "yape"
                if (contienePlin(sub)) return "plin"
            }
            return null
        }

        val destino = detectarDestino(t)
        val hayInterbank = contieneInterbank(t)
        val hayPlin = contienePlin(t)
        val hayYapeaste = t.contains("¡yapeaste!", ignoreCase = true) ||
                t.contains("yapeaste!", ignoreCase = true)
        val hayCodigoSeguridad = tieneCodigoSeguridad(t)

        Log.d("WalletActivity", """
            📊 Análisis local:
            - Interbank: $hayInterbank
            - Plin: $hayPlin
            - Destino: ${destino ?: "no detectado"}
            - Yapeaste: $hayYapeaste
            - Cód. Seguridad: $hayCodigoSeguridad
        """.trimIndent())

        // 1) PLIN → Yape (Interbank o plin presente + destino yape)
        if ((hayInterbank || hayPlin) && destino == "yape") {
            Log.d("WalletActivity", "✅ Clasificado local: plin_yape")
            return "plin_yape"
        }

        // 2) Yape → Plin (Yapeaste + destino plin)
        if (hayYapeaste && destino == "plin") {
            Log.d("WalletActivity", "✅ Clasificado local: yape_plin")
            return "yape_plin"
        }

        // 3) Yape → Yape (código de seguridad)
        if (hayCodigoSeguridad) {
            Log.d("WalletActivity", "✅ Clasificado local: yape_yape (tiene cód. seguridad)")
            return "yape_yape"
        }

        // 4) Fallback: si hay interbank/plin pero no destino claro, asumimos plin_yape
        if (hayInterbank || hayPlin) {
            Log.d("WalletActivity", "⚠️ Fallback: plin_yape (hay plin/interbank)")
            return "plin_yape"
        }

        // Por defecto consideramos Yape-Yape (conservador)
        Log.w("WalletActivity", "⚠️ No se pudo clasificar, asumiendo yape_yape por defecto")
        return "yape_yape"
    }

    // =================== OCR del comprobante + anti-duplicado SIMPLIFICADO ===================
    private fun procesarComprobante(uri: Uri) {
        val usuarioActual = user ?: run {
            Toast.makeText(this, "Inicia sesión", Toast.LENGTH_LONG).show()
            return
        }

        if (walletCongelada) {
            Toast.makeText(
                this,
                "Tu wallet está congelada. No puedes enviar comprobantes.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        try {
            val image = InputImage.fromFilePath(this, uri)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    try {
                        // ⭐ Parseo con VoucherOcr (ahora corregido con AM/PM)
                        val parsed = VoucherOcr.parseVoucher(visionText.text)

                        val bruto = parsed.bruto ?: visionText.text
                        val monto = parsed.monto ?: 0.0
                        var codigo = parsed.codigo ?: ""
                        val telefono = parsed.telefono ?: ""

                        // ⭐ CRÍTICO: Usar la fecha parseada del OCR, NO Date()
                        val fechaOperacion = if (parsed.tsOperacion != null) {
                            Date(parsed.tsOperacion)
                        } else {
                            Log.w("WalletActivity", "⚠️ No se pudo parsear fecha, usando hora actual")
                            Date()
                        }

                        Log.d("WalletActivity", "📅 Fecha del voucher: $fechaOperacion")

                        // Tipo de voucher (YAPE_YAPE vs PLIN_YAPE vs YAPE_PLIN)
                        val tipoVoucher = clasificarTipoVoucherLocal(bruto, parsed.tipoVoucher)

                        // --- Normalizar código SOLO para Yape-Yape (cód. de seguridad numérico) ---
                        if (tipoVoucher == "yape_yape") {
                            codigo = codigo.filter { it.isDigit() }
                        }

                        val esYape = tipoVoucher == "yape_yape"

                        // Validación mínima:
                        // - Siempre necesitamos monto > 0
                        // - En Yape-Yape además necesitamos código (cód. de seguridad)
                        if (monto <= 0.0 || (esYape && codigo.isBlank())) {
                            Toast.makeText(
                                this,
                                "No se pudo leer monto o código del comprobante.",
                                Toast.LENGTH_LONG
                            ).show()
                            return@addOnSuccessListener
                        }

                        Log.d("WalletActivity", """
                            ✅ Voucher parseado:
                            - Tipo: $tipoVoucher
                            - Monto: $monto
                            - Código: ${if (codigo.isBlank()) "sin código" else codigo}
                            - Fecha: $fechaOperacion
                        """.trimIndent())

                        // ⭐ ANTI-DUPLICADO SIMPLIFICADO (solo por código)
                        if (codigo.isNotBlank()) {
                            // Si hay código, verificar duplicado por código
                            db.collection("usuarios")
                                .document(usuarioActual.uid)
                                .collection("recargas")
                                .whereEqualTo("codigoOperacion", codigo)
                                .limit(1)
                                .get()
                                .addOnSuccessListener { qs ->
                                    if (!qs.isEmpty) {
                                        Toast.makeText(
                                            this,
                                            "Este comprobante ya fue registrado.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        return@addOnSuccessListener
                                    }

                                    // No hay duplicado → subir
                                    subirImagenYCrearRecarga(
                                        usuarioUid = usuarioActual.uid,
                                        uri = uri,
                                        ocrBruto = bruto,
                                        codigo = codigo,
                                        telefono = telefono,
                                        monto = monto,
                                        fechaOperacion = fechaOperacion,
                                        tipoVoucher = tipoVoucher
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.e("WalletActivity", "Error verificando duplicado: ${e.message}")
                                    Toast.makeText(
                                        this,
                                        "Error verificando duplicado: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        } else {
                            // Sin código (plin_yape sin código): subir directo
                            // La Cloud Function hará el anti-duplicado por monto+fecha
                            Log.d("WalletActivity", "⚠️ Sin código, subiendo directo (CF validará)")
                            subirImagenYCrearRecarga(
                                usuarioUid = usuarioActual.uid,
                                uri = uri,
                                ocrBruto = bruto,
                                codigo = codigo,
                                telefono = telefono,
                                monto = monto,
                                fechaOperacion = fechaOperacion,
                                tipoVoucher = tipoVoucher
                            )
                        }

                    } catch (e: Exception) {
                        Log.e("WalletActivity", "Error procesando imagen: ${e.message}", e)
                        Toast.makeText(
                            this,
                            "Error procesando imagen: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("WalletActivity", "Error en OCR: ${e.message}", e)
                    Toast.makeText(
                        this,
                        "No se pudo leer el comprobante.",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } catch (e: Exception) {
            Log.e("WalletActivity", "Error general: ${e.message}", e)
            Toast.makeText(this, "Error procesando imagen: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // =================== Subir imagen + crear recarga ===================
    private fun subirImagenYCrearRecarga(
        usuarioUid: String,
        uri: Uri,
        ocrBruto: String,
        codigo: String,
        telefono: String,
        monto: Double,
        fechaOperacion: Date,
        tipoVoucher: String
    ) {
        val nombreArchivo = "voucher_${codigo.ifBlank { "sin_codigo" }}_${System.currentTimeMillis()}.jpg"
        val rutaStorage = "vouchers/$usuarioUid/$nombreArchivo"
        val refStorage = storage.reference.child(rutaStorage)

        Log.d("WalletActivity", "📤 Subiendo imagen a Storage: $rutaStorage")

        refStorage.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception ?: Exception("Error subiendo imagen")
                }
                refStorage.downloadUrl
            }
            .addOnSuccessListener { downloadUri ->
                val urlDescarga = downloadUri.toString()

                val recargaData = hashMapOf(
                    "montoDetectado" to monto,
                    "codigoOperacion" to codigo,
                    "telefono" to telefono,
                    "estado" to "pendiente_revision",
                    "fecha" to fechaOperacion,
                    "ts_envio" to FieldValue.serverTimestamp(),
                    "voucherPath" to rutaStorage,
                    "voucherUrl" to urlDescarga,
                    "ocrBruto" to ocrBruto,
                    "tipoVoucher" to tipoVoucher
                )

                Log.d("WalletActivity", "💾 Guardando recarga en Firestore: $tipoVoucher, S/ $monto")

                db.collection("usuarios")
                    .document(usuarioUid)
                    .collection("recargas")
                    .add(recargaData)
                    .addOnSuccessListener {
                        Log.d("WalletActivity", "✅ Recarga guardada exitosamente")
                        Toast.makeText(
                            this,
                            "Recarga enviada, se validará automáticamente.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("WalletActivity", "❌ Error guardando recarga: ${e.message}", e)
                        Toast.makeText(
                            this,
                            "Error al registrar la recarga.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("WalletActivity", "❌ Error subiendo imagen: ${e.message}", e)
                Toast.makeText(
                    this,
                    "Error subiendo la imagen del comprobante.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}