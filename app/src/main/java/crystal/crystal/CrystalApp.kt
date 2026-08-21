package crystal.crystal

import android.app.Application
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import crystal.crystal.red.CrystalFcmTokenManager
import crystal.crystal.red.CrystalMessageNotifier

class CrystalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        prepararReporteDeFallos()
        instalarAppCheck(this)   // definido por variante (Play Integrity en release, debug provider en debug)
        // Suscripción: cargar el estado (cache offline) y enganchar el listener aquí, en la Application,
        // que SIEMPRE se crea al arrancar el proceso. Si solo se inicializa en MainActivity, al matar el
        // proceso en segundo plano y restaurar directo en una calculadora, el estado quedaba en BASIC por
        // defecto y salía el paywall aunque el usuario fuera FULL.
        Suscripcion.iniciar(this)
        // Banner "en desarrollo" para las calculadoras aún no terminadas (v1).
        crystal.crystal.taller.AvisoDesarrollo.registrar(this)
        CrystalMessageNotifier.ensureChannel(this)
        CrystalFcmTokenManager.flushPendingToken(this)
    }

    /**
     * Etiqueta los reportes de fallo con quién y con qué build ocurrió.
     *
     * Un reporte sin contexto obliga a preguntar de vuelta "¿qué versión tienes?, ¿qué estabas
     * haciendo?", y para entonces la persona ya perdió el interés. Con el uid se puede cruzar el
     * fallo con lo que esa cuenta estaba usando; con el rol se distingue un patrón de una terminal.
     */
    private fun prepararReporteDeFallos() {
        runCatching {
            val prefs = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            FirebaseCrashlytics.getInstance().apply {
                FirebaseAuth.getInstance().currentUser?.uid?.let { setUserId(it) }
                setCustomKey("versionCode", BuildConfig.VERSION_CODE)
                setCustomKey("versionName", BuildConfig.VERSION_NAME)
                setCustomKey("rol", prefs.getString("session_type", "?") ?: "?")
                setCustomKey("patronUid", prefs.getString("patron_uid", "") ?: "")
            }
        }
    }
}
