package crystal.crystal

import android.content.Context
import android.os.SystemClock
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * Portero central de suscripción (Fase 1). Lee `estado_servicio` del usuario en Firestore, lo cachea
 * (con respaldo offline en prefs) y expone si las funciones avanzadas (de pago) están activas.
 *
 * IMPORTANTE: la comparación de vencimiento usa un reloj anti-retroceso del dispositivo
 * ([ahoraEfectiva]) → esto es SOLO para la experiencia de usuario (mostrar/ocultar, avisos) y para
 * que el tiempo corra bien en equipos siempre offline sin dejarse burlar atrasando la hora. La
 * cerradura real y no burlable son las reglas de Firestore, que validan `full_until` contra el
 * tiempo del servidor y solo dejan OTORGAR planes desde el servidor.
 */
object Suscripcion {

    /**
     * Interruptor maestro del cobro. Mientras esté en `false`, TODAS las compuertas devuelven
     * "permitido" (solo se cablea la plomería, no se bloquea nada). Ponerlo en `true` activa el
     * cobro real. Así se puede conectar todo sin frenar el desarrollo/pruebas.
     */
    const val ENFORCEMENT = true

    /**
     * TEMPORAL solo para probar los paywalls: si es `true`, TODO queda bloqueado (simula BASIC) sin
     * tocar tu cuenta ni el trial. Ponlo en `true`, recompila, verifica que cada acción de pago
     * muestra el aviso y que lo gratis sigue funcionando; luego regrésalo a `false`.
     */
    const val SIMULAR_BASIC_PRUEBA = false

    private const val PREFS = "suscripcion"
    private const val K_MODE = "mode"
    private const val K_FULL_UNTIL = "full_until_millis"
    private const val K_MAX_SEEN = "max_seen_millis"   // mayor "ahora" visto (anti-retroceso de reloj)

    @Volatile private var mode: String = "BASIC"
    @Volatile private var fullUntilMillis: Long = 0L   // 0 = sin dato / sin límite (según mode)
    @Volatile private var listener: ListenerRegistration? = null
    @Volatile private var uidActual: String? = null

    // --- Reloj monotónico anti-retroceso (para equipos que trabajan siempre offline) ---
    // `maxSeenMillis` es el mayor "ahora" que este equipo ha visto. Nunca decrece: si el usuario
    // atrasa el reloj del celular, `ahoraEfectiva()` lo ignora. Y como avanza también con
    // `elapsedRealtime()` (reloj monotónico que corre aunque cambien la hora), el tiempo NO se
    // congela: mientras la app se usa, el vencimiento se acerca aunque no haya internet.
    @Volatile private var appContext: Context? = null
    @Volatile private var maxSeenMillis: Long = 0L
    @Volatile private var baseElapsed: Long = 0L        // elapsedRealtime en el último anclaje
    @Volatile private var lastPersistMax: Long = 0L     // último maxSeen persistido (para throttle)

    private fun resolveUid(context: Context): String? {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return prefs.getString("patron_uid", null) ?: FirebaseAuth.getInstance().currentUser?.uid
    }

    /** Arranca (o reengancha) el listener del estado de servicio del usuario actual. */
    fun iniciar(context: Context) {
        val app = context.applicationContext
        appContext = app
        cargarCache(app)                       // estado offline inmediato
        val uid = resolveUid(app) ?: return
        if (uid == uidActual && listener != null) return
        listener?.remove()
        uidActual = uid
        listener = FirebaseFirestore.getInstance()
            .collection("usuarios").document(uid)
            .addSnapshotListener { snap, _ ->
                val es = snap?.get("estado_servicio") as? Map<*, *> ?: return@addSnapshotListener
                mode = es["mode"] as? String ?: "BASIC"
                fullUntilMillis = fullUntilMillisDe(es)
                guardarCache(app)
            }
        anclarConHoraServidor()   // si hay red, fija el reloj a la hora autoritativa del servidor
    }

    /**
     * Cuando hay conexión, ancla el reloj anti-retroceso a la hora AUTORITATIVA del servidor
     * (Cloud Function `serverTime`). Así el reloj del equipo (adelantado o atrasado) deja de importar
     * en cuanto estuvo online al menos una vez. Fire-and-forget: si no hay red, falla en silencio y
     * se sigue usando el reloj local anti-retroceso.
     */
    private fun anclarConHoraServidor() {
        runCatching {
            com.google.firebase.functions.FirebaseFunctions.getInstance()
                .getHttpsCallable("serverTime")
                .call()
                .addOnSuccessListener { res ->
                    val serverNow = ((res.data as? Map<*, *>)?.get("now") as? Number)?.toLong()
                        ?: return@addOnSuccessListener
                    // Autoritativo: fija el ancla a la hora del servidor (corrige en ambos sentidos)
                    // y re-ancla el monotónico. El piso anti-retroceso local sigue rigiendo offline.
                    maxSeenMillis = serverNow
                    baseElapsed = SystemClock.elapsedRealtime()
                    lastPersistMax = serverNow
                    appContext?.let { guardarCache(it) }
                }
        }
    }

    fun detener() {
        listener?.remove(); listener = null; uidActual = null
    }

    private fun cargarCache(context: Context) {
        val p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        mode = p.getString(K_MODE, "BASIC") ?: "BASIC"
        fullUntilMillis = p.getLong(K_FULL_UNTIL, 0L)
        // Anclar el reloj monotónico al arrancar: el piso es max(persistido, reloj actual).
        maxSeenMillis = maxOf(p.getLong(K_MAX_SEEN, 0L), System.currentTimeMillis())
        baseElapsed = SystemClock.elapsedRealtime()
        lastPersistMax = maxSeenMillis
        guardarCache(context)
    }

    private fun guardarCache(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(K_MODE, mode)
            .putLong(K_FULL_UNTIL, fullUntilMillis)
            .putLong(K_MAX_SEEN, maxSeenMillis)
            .apply()
    }

    /**
     * "Ahora" a prueba de retroceso de reloj. Avanza con el reloj de pared cuando este va hacia
     * adelante, y con el reloj monotónico (`elapsedRealtime`) siempre — así el tiempo nunca retrocede
     * ni se congela aunque atrasen la hora del celular. Persiste el piso (con throttle) para que el
     * avance sobreviva a cierres del proceso.
     */
    private fun ahoraEfectiva(): Long {
        val monot = maxSeenMillis + (SystemClock.elapsedRealtime() - baseElapsed)
        val eff = maxOf(System.currentTimeMillis(), monot)
        if (eff > maxSeenMillis) {
            maxSeenMillis = eff
            baseElapsed = SystemClock.elapsedRealtime()   // re-anclar para no doble-contar el delta
            val ctx = appContext
            if (ctx != null && maxSeenMillis - lastPersistMax > 60_000L) {  // persistir a lo más 1/min
                lastPersistMax = maxSeenMillis
                guardarCache(ctx)
            }
        }
        return maxSeenMillis
    }

    /** ¿Están activas las funciones avanzadas (de pago)? */
    fun avanzadoActivo(): Boolean {
        if (SIMULAR_BASIC_PRUEBA) return false // prueba: todo bloqueado (simula BASIC)
        if (!ENFORCEMENT) return true          // cobro apagado: todo abierto
        if (mode == "BASIC") return false
        // FULL es un plan POR TIEMPO: vence EXACTO en `full_until` (misma regla que muestra Wallet).
        // Sin gracia de cliente: así la cabecera, las compuertas y Wallet dicen lo mismo. Un FULL
        // sin `full_until` (0) es un dato inconsistente y NO debe dar acceso.
        if (mode == "FULL") {
            if (fullUntilMillis <= 0L) return false
            return fullUntilMillis > ahoraEfectiva()
        }
        // Otros modos (p. ej. VENTAS): si traen vencimiento, respetarlo; si no, sin límite.
        if (fullUntilMillis <= 0L) return true
        return fullUntilMillis > ahoraEfectiva()
    }

    /**
     * Lee `full_until` en millis desde un mapa `estado_servicio`, aceptando tanto un `Timestamp`
     * de Firestore como un número (millis). Devuelve 0 si falta o es inválido. Fuente única para
     * que la cabecera y las compuertas no se desincronicen por el tipo del campo.
     */
    fun fullUntilMillisDe(estadoServicio: Map<*, *>?): Long {
        return when (val fu = estadoServicio?.get("full_until")) {
            is Timestamp -> fu.toDate().time
            is Number -> fu.toLong()
            else -> 0L
        }
    }

    /**
     * Modo efectivo para mostrar/gatear: un `FULL` con vencimiento pasado o ausente cuenta como
     * `BASIC` (mismo criterio EXACTO que [avanzadoActivo], sin gracia). Los demás modos se devuelven
     * tal cual.
     */
    fun modoEfectivo(modo: String, fullUntilMillis: Long): String {
        if (modo == "FULL") {
            val vigente = fullUntilMillis > ahoraEfectiva()
            return if (vigente) "FULL" else "BASIC"
        }
        return modo
    }

    /** Días restantes de las funciones avanzadas; null si no aplica o es sin límite. */
    fun diasRestantes(): Int? {
        if (mode == "BASIC" || fullUntilMillis <= 0L) return null
        val ms = fullUntilMillis - ahoraEfectiva()
        return if (ms <= 0) 0 else ((ms / (24L * 60 * 60 * 1000)) + 1).toInt()
    }

    /**
     * Verifica un permiso y, si está bloqueado, muestra el aviso de pago. Devuelve true si se permite.
     * Uso: `if (!Suscripcion.exigir(this, Suscripcion.puedeArchivar(), "…")) return`.
     */
    fun exigir(context: Context, permitido: Boolean, mensaje: String): Boolean {
        if (permitido) return true
        mostrarInvitacionFull(context, mensaje)
        return false
    }

    /**
     * Muestra la invitación a FULL directamente (para casos donde el llamador YA decidió que está
     * bloqueado y necesita hacer algo al cerrar el diálogo, p. ej. navegar de vuelta). `onCerrar`
     * se ejecuta cuando el diálogo se cierra por cualquier vía.
     */
    fun invitarFull(context: Context, mensaje: String, onCerrar: (() -> Unit)? = null) {
        mostrarInvitacionFull(context, mensaje, onCerrar)
    }

    // Diálogo vistoso que invita a hacerse FULL (reemplaza el Toast). Usa un Dialog propio (funciona
    // con cualquier tema) y cae a Toast si algo falla o no hay una Activity válida.
    private var dialogoInvitacionAbierto = false
    private fun mostrarInvitacionFull(context: Context, mensaje: String, onCerrar: (() -> Unit)? = null) {
        val activity = context as? android.app.Activity
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            android.widget.Toast.makeText(context, mensaje, android.widget.Toast.LENGTH_LONG).show()
            onCerrar?.invoke()
            return
        }
        if (dialogoInvitacionAbierto) return
        try {
            val vista = activity.layoutInflater.inflate(crystal.crystal.R.layout.dialog_full_invitacion, null)
            vista.findViewById<android.widget.TextView>(crystal.crystal.R.id.tvMensajeFull).text = mensaje
            val dialog = android.app.Dialog(activity)
            dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
            dialog.setContentView(vista)
            dialog.window?.setBackgroundDrawable(
                android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
            )
            dialog.window?.setLayout(
                (activity.resources.displayMetrics.widthPixels * 0.88f).toInt(),
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.setOnDismissListener {
                dialogoInvitacionAbierto = false
                onCerrar?.invoke()
            }
            vista.findViewById<android.view.View>(crystal.crystal.R.id.btnFull).setOnClickListener {
                dialog.dismiss()
                // Se pide volver atrás: el usuario estaba a media medición o a medio cálculo, y
                // devolverlo a MainActivity tras pagar le obliga a rehacer todo desde cero.
                runCatching {
                    crystal.crystal.pagos.CanalPagos.abrirPlanes(
                        activity,
                        crystal.crystal.pagos.CanalPagos.EXTRA_VOLVER_ATRAS to true
                    )
                }
            }
            vista.findViewById<android.view.View>(crystal.crystal.R.id.btnAhoraNo).setOnClickListener { dialog.dismiss() }
            dialogoInvitacionAbierto = true
            dialog.show()
        } catch (e: Exception) {
            dialogoInvitacionAbierto = false
            android.widget.Toast.makeText(context, mensaje, android.widget.Toast.LENGTH_LONG).show()
            onCerrar?.invoke()
        }
    }

    // Compuertas por función. Por ahora todas dependen de `avanzadoActivo()`; la Fase 3 las conecta
    // a cada función y, si hace falta, se pueden granular por plan más adelante.
    fun puedeArchivar() = avanzadoActivo()
    fun puedeEnviarFormato() = avanzadoActivo()
    fun puedeSincronizar() = avanzadoActivo()
    fun puedeOptimizar() = avanzadoActivo()
    fun puedeExportarPdf() = avanzadoActivo()
    fun puedeModoMasivo() = avanzadoActivo()
}
