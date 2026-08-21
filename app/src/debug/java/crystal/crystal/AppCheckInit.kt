package crystal.crystal

import android.app.Application
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

// Debug: proveedor de prueba. En el primer arranque, Logcat imprime un token de depuración
// que hay que registrar en la consola de Firebase (App Check > apps > administrar tokens de debug)
// para que las builds de desarrollo pasen el App Check.
fun instalarAppCheck(app: Application) {
    runCatching {
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            DebugAppCheckProviderFactory.getInstance()
        )
    }
}
