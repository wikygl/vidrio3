package crystal.crystal

import android.app.Application
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

// Release: App Check con Play Integrity (verifica que la app es genuina y viene de Play).
fun instalarAppCheck(app: Application) {
    runCatching {
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
    }
}
