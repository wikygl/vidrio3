package crystal.crystal

import android.app.Application
import crystal.crystal.red.CrystalFcmTokenManager
import crystal.crystal.red.CrystalMessageNotifier

class CrystalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CrystalMessageNotifier.ensureChannel(this)
        CrystalFcmTokenManager.flushPendingToken(this)
    }
}
