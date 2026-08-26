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
    private var listenerReserva: ListenerRegistration? = null

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

    // Comprobante obligatorio de la recarga por monto único (evidencia para reclamos/revisión).
    private var reservaComprobantePendiente: String? = null
    private val pickerComprobanteReserva = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val reservaId = reservaComprobantePendiente
        if (uri != null && reservaId != null) subirComprobanteReserva(reservaId, uri)
        else Toast.makeText(this, "No se seleccionó imagen", Toast.LENGTH_SHORT).show()
    }

    // Comprobante para reclamar una reserva expirada.
    private var reclamoReservaPendiente: Reserva? = null
    private val pickerReclamo = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val r = reclamoReservaPendiente
        reclamoReservaPendiente = null
        if (uri != null && r != null) subirComprobanteYReclamar(r, uri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (crystal.crystal.FeaturesV1.OCULTAR_WALLET) {
            android.widget.Toast.makeText(this, "No disponible en esta versión", android.widget.Toast.LENGTH_SHORT).show()
            finish(); return
        }
        binding = ActivityWalletBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarWallet)

        inicializarUI()
        manejarComprobanteCompartido(intent)
        intent.getStringExtra("abrir_reserva_id")?.let { rid ->
            val cent = intent.getLongExtra("abrir_reserva_cent", 0L)
            mostrarDialogoPago(rid, "S/ %.2f".format(cent / 100.0))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        manejarComprobanteCompartido(intent)
    }

    // "Compartir → Crystal (Wallet)" desde Yape/Plin: usa el comprobante para la recarga en curso.
    private fun manejarComprobanteCompartido(intent: Intent?) {
        if (intent?.action != Intent.ACTION_SEND) return
        if (intent.type?.startsWith("image/") != true) return
        @Suppress("DEPRECATION")
        val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM) ?: return
        intent.action = null // consumir para no reprocesar al rotar
        val uid = user?.uid ?: return
        db.collection("reservas_recarga")
            .whereEqualTo("uid", uid)
            .whereEqualTo("estado", "esperando")
            .get()
            .addOnSuccessListener { qs ->
                val pendientes = qs.documents
                when {
                    pendientes.isEmpty() -> Toast.makeText(
                        this, "No tienes una recarga en curso. Primero toca 💳 Recargar.", Toast.LENGTH_LONG
                    ).show()
                    pendientes.size == 1 -> usarComprobantePara(pendientes[0].id, pendientes[0].getLong("totalCent") ?: 0L, uri)
                    else -> {
                        val labels = pendientes.map { "S/ %.2f".format((it.getLong("totalCent") ?: 0L) / 100.0) }.toTypedArray()
                        androidx.appcompat.app.AlertDialog.Builder(this)
                            .setTitle("¿A qué recarga pertenece el comprobante?")
                            .setItems(labels) { _, w ->
                                usarComprobantePara(pendientes[w].id, pendientes[w].getLong("totalCent") ?: 0L, uri)
                            }
                            .show()
                    }
                }
            }
            .addOnFailureListener { Toast.makeText(this, "Error buscando tu recarga: ${it.message}", Toast.LENGTH_LONG).show() }
    }

    private fun usarComprobantePara(reservaId: String, totalCent: Long, uri: Uri) {
        mostrarDialogoPago(reservaId, "S/ %.2f".format(totalCent / 100.0)) // muestra el estado en vivo
        subirComprobanteReserva(reservaId, uri)                            // sube y adjunta el comprobante
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerWalletState?.remove()
        listenerRecargas?.remove()
        listenerReserva?.remove()
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

        // --------- Historial de recargas (por monto único) ----------
        val listaReservas = mutableListOf<Reserva>()
        val adapterRes = ReservaAdapter(listaReservas) { r ->
            when (r.estado) {
                "expirada" -> reclamarReservaExpirada(r)           // pago hecho pero venció → a revisión
                else -> mostrarDialogoPago(r.reservaId, "S/ %.2f".format(r.totalCent / 100.0)) // reanudar
            }
        }
        binding.rvRecargas.layoutManager = LinearLayoutManager(this)
        binding.rvRecargas.adapter = adapterRes

        listenerRecargas = db.collection("reservas_recarga")
            .whereEqualTo("uid", usuario.uid)
            .addSnapshotListener { snap, _ ->
                listaReservas.clear()
                for (doc in snap?.documents ?: emptyList()) {
                    val r = doc.toObject(Reserva::class.java)
                    if (r != null) { r.reservaId = doc.id; listaReservas.add(r) }
                }
                listaReservas.sortByDescending { it.creadoMs }
                adapterRes.notifyDataSetChanged()
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

        // --------- Botón: Recargar (monto único) ----------
        binding.btnSeleccionarComprobante.setOnClickListener {
            if (walletCongelada) {
                Toast.makeText(this, "Tu wallet está congelada. No puedes recargar.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            iniciarRecargaMontoUnico()
        }
        // Respaldo (long-press): subir comprobante por OCR (estrategia anterior).
        binding.btnSeleccionarComprobante.setOnLongClickListener {
            if (!walletCongelada) {
                try { picker.launch("image/*") } catch (e: Exception) {
                    Toast.makeText(this, "Error abriendo galería: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
            true
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
                    // Lo escribe crystalAdmin al corregir a mano. Sin este caso se colaba el código
                    // interno "AJUSTE_MANUAL" a la pantalla del usuario.
                    "AJUSTE_MANUAL" -> "Activado por soporte"
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

    // =================== Recarga por monto único (firma de céntimos) ===================

    private fun iniciarRecargaMontoUnico() {
        val opciones = arrayOf("S/ 1", "S/ 5", "S/ 10", "S/ 15", "S/ 20", "Otro monto…")
        val valores = intArrayOf(1, 5, 10, 15, 20, -1)
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("¿Cuánto quieres recargar?")
            .setItems(opciones) { _, which ->
                if (valores[which] == -1) pedirMontoPersonalizado() else reservarYMostrar(valores[which])
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun pedirMontoPersonalizado() {
        val et = android.widget.EditText(this).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
            hint = "Soles (1 a 999)"
            setPadding(48, 32, 48, 32)
        }
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Monto a recargar")
            .setView(et)
            .setPositiveButton("Continuar") { _, _ ->
                val s = et.text.toString().toIntOrNull() ?: 0
                if (s in 1..999) reservarYMostrar(s)
                else Toast.makeText(this, "Ingresa entre 1 y 999 soles.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun reservarYMostrar(soles: Int) {
        val cargando = androidx.appcompat.app.AlertDialog.Builder(this)
            .setMessage("Generando tu monto de recarga…")
            .setCancelable(false).create()
        cargando.show()
        com.google.firebase.functions.FirebaseFunctions.getInstance()
            .getHttpsCallable("reservarRecarga")
            .call(mapOf("soles" to soles))
            .addOnSuccessListener { res ->
                cargando.dismiss()
                val data = res.data as? Map<*, *>
                val reservaId = data?.get("reservaId") as? String
                val totalTexto = data?.get("totalTexto") as? String ?: "S/ ?"
                if (reservaId == null) {
                    Toast.makeText(this, "No se pudo generar la recarga.", Toast.LENGTH_LONG).show()
                    return@addOnSuccessListener
                }
                mostrarDialogoPago(reservaId, totalTexto)
            }
            .addOnFailureListener { e ->
                cargando.dismiss()
                Toast.makeText(this, "No se pudo reservar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun mostrarDialogoPago(reservaId: String, totalTexto: String) {
        val vista = layoutInflater.inflate(crystal.crystal.R.layout.dialog_pago_monto_unico, null)
        val tvMonto = vista.findViewById<android.widget.TextView>(crystal.crystal.R.id.tvMontoPago)
        val tvNumero = vista.findViewById<android.widget.TextView>(crystal.crystal.R.id.tvNumeroPago)
        val tvEstado = vista.findViewById<android.widget.TextView>(crystal.crystal.R.id.tvEstadoPago)
        tvMonto.text = totalTexto

        // Número asignado a ESTA reserva (el primario vivo, con failover). Fallback: config/pagos.
        // El número llega del servidor y puede tardar, así que se guarda aparte: el botón de
        // copiar no puede depender de recortar el texto ya formateado.
        var numeroActual: String? = null
        fun pintarNumero(num: String?) {
            numeroActual = num
            tvNumero.text = if (!num.isNullOrBlank()) "al número:  $num" else "al número del negocio"
        }
        db.collection("reservas_recarga").document(reservaId).get().addOnSuccessListener { r ->
            val num = r.getString("numeroAsignado")
            if (!num.isNullOrBlank()) pintarNumero(num)
            else db.collection("config").document("pagos").get().addOnSuccessListener { d ->
                pintarNumero(d.getString("numeroYape") ?: d.getString("numero"))
            }
        }

        val dialog = android.app.Dialog(this)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(vista)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(
            android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
        )
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.9f).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        vista.findViewById<android.view.View>(crystal.crystal.R.id.btnCopiarMonto).setOnClickListener {
            val soloNumero = totalTexto.replace("S/", "").trim()
            val cb = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            cb.setPrimaryClip(android.content.ClipData.newPlainText("monto", soloNumero))
            Toast.makeText(this, "Monto copiado: $soloNumero", Toast.LENGTH_SHORT).show()
        }
        vista.findViewById<android.view.View>(crystal.crystal.R.id.btnCopiarNumero).setOnClickListener {
            val n = numeroActual
            if (n.isNullOrBlank()) {
                Toast.makeText(this, "Todavía no tenemos el número. Espera un momento.", Toast.LENGTH_SHORT).show()
            } else {
                val cb = getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                cb.setPrimaryClip(android.content.ClipData.newPlainText("numero", n))
                Toast.makeText(this, "Número copiado: $n", Toast.LENGTH_SHORT).show()
            }
        }
        vista.findViewById<android.view.View>(crystal.crystal.R.id.btnSubirComprobante).setOnClickListener {
            reservaComprobantePendiente = reservaId
            try { pickerComprobanteReserva.launch("image/*") } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la galería: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
        vista.findViewById<android.view.View>(crystal.crystal.R.id.btnCerrarPago).setOnClickListener {
            listenerReserva?.remove(); listenerReserva = null
            dialog.dismiss()
        }
        dialog.setOnDismissListener { listenerReserva?.remove(); listenerReserva = null }

        // Escucha la reserva: acredita SOLO con pago casado por monto + comprobante adjunto.
        listenerReserva?.remove()
        listenerReserva = db.collection("reservas_recarga").document(reservaId)
            .addSnapshotListener { snap, _ ->
                snap ?: return@addSnapshotListener
                val pago = snap.getBoolean("pagoRecibido") == true
                val comp = snap.getBoolean("tieneComprobante") == true
                when (snap.getString("estado")) {
                    "aplicado" -> {
                        tvEstado.text = "✅  ¡Saldo acreditado!"
                        tvEstado.setTextColor(ContextCompat.getColor(this, crystal.crystal.R.color.verde))
                        vista.findViewById<android.view.View>(crystal.crystal.R.id.filaCopiar).visibility = android.view.View.GONE
                        vista.findViewById<android.view.View>(crystal.crystal.R.id.btnSubirComprobante).visibility = android.view.View.GONE
                    }
                    "expirada" -> {
                        tvEstado.text = "⌛  La reserva expiró. Vuelve a intentar."
                        tvEstado.setTextColor(ContextCompat.getColor(this, crystal.crystal.R.color.rojo))
                    }
                    else -> {
                        tvEstado.text = when {
                            pago && !comp -> "⚠️ Aún NO acreditado.\nSube tu comprobante para acreditar tu saldo."
                            !pago && comp -> "📎 Comprobante recibido. Esperando confirmar tu pago…"
                            else -> "⏳  Esperando tu pago y comprobante…"
                        }
                        tvEstado.setTextColor(ContextCompat.getColor(this, crystal.crystal.R.color.naranja))
                    }
                }
            }
        dialog.show()
    }

    private fun subirComprobanteReserva(reservaId: String, uri: Uri) {
        reservaComprobantePendiente = null
        val uid = user?.uid ?: return
        Toast.makeText(this, "Subiendo comprobante…", Toast.LENGTH_SHORT).show()
        val ruta = "vouchers/$uid/reserva_${reservaId}_${System.currentTimeMillis()}.jpg"
        val ref = storage.reference.child(ruta)
        ref.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception ?: Exception("Error subiendo imagen")
                ref.downloadUrl
            }
            .addOnSuccessListener { url ->
                com.google.firebase.functions.FirebaseFunctions.getInstance()
                    .getHttpsCallable("adjuntarComprobante")
                    .call(mapOf("reservaId" to reservaId, "voucherPath" to ruta, "voucherUrl" to url.toString()))
                    .addOnSuccessListener {
                        Toast.makeText(this, "Comprobante adjuntado ✅", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "No se pudo adjuntar: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error subiendo comprobante: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ====== Reclamo de reserva expirada (pago hecho pero venció la ventana) ======
    private fun reclamarReservaExpirada(r: Reserva) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Recarga expirada")
            .setMessage(
                "Esta recarga de S/ %.2f expiró. Si ya pagaste, envíala a revisión con tu comprobante y el administrador la validará."
                    .format(r.totalCent / 100.0)
            )
            .setPositiveButton("Enviar a revisión") { _, _ -> intentarReclamar(r) }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun intentarReclamar(r: Reserva) {
        com.google.firebase.functions.FirebaseFunctions.getInstance()
            .getHttpsCallable("reclamarReserva")
            .call(mapOf("reservaId" to r.reservaId))
            .addOnSuccessListener {
                Toast.makeText(this, "Enviado a revisión ✅. El administrador lo validará.", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e ->
                if ((e.message ?: "").contains("SIN_COMPROBANTE")) {
                    Toast.makeText(this, "Adjunta el comprobante del pago para reclamar.", Toast.LENGTH_LONG).show()
                    reclamoReservaPendiente = r
                    try { pickerReclamo.launch("image/*") } catch (ex: Exception) {
                        Toast.makeText(this, "No se pudo abrir la galería: ${ex.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "No se pudo reclamar: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun subirComprobanteYReclamar(r: Reserva, uri: Uri) {
        val uid = user?.uid ?: return
        Toast.makeText(this, "Subiendo comprobante…", Toast.LENGTH_SHORT).show()
        val ruta = "vouchers/$uid/reclamo_${r.reservaId}_${System.currentTimeMillis()}.jpg"
        val ref = storage.reference.child(ruta)
        ref.putFile(uri)
            .continueWithTask { t ->
                if (!t.isSuccessful) throw t.exception ?: Exception("Error subiendo imagen")
                ref.downloadUrl
            }
            .addOnSuccessListener { url ->
                // Adjunta el comprobante a la reserva (aunque esté expirada) y luego reclama.
                com.google.firebase.functions.FirebaseFunctions.getInstance()
                    .getHttpsCallable("adjuntarComprobante")
                    .call(mapOf("reservaId" to r.reservaId, "voucherPath" to ruta, "voucherUrl" to url.toString()))
                    .addOnSuccessListener { intentarReclamar(r) }
                    .addOnFailureListener { e -> Toast.makeText(this, "No se pudo adjuntar: ${e.message}", Toast.LENGTH_LONG).show() }
            }
            .addOnFailureListener { e -> Toast.makeText(this, "Error subiendo comprobante: ${e.message}", Toast.LENGTH_LONG).show() }
    }
}