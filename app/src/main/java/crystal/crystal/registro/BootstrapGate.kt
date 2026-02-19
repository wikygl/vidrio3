package crystal.crystal.registro

import android.content.Context

object BootstrapGate {
    private const val PREFS = "crystal_bootstrap"
    private const val KEY_OK = "bootstrap_ok"
    private const val KEY_UID = "uid_cached"
    private const val KEY_DEVICE = "device_id"

    fun localSaysBootstrapOk(ctx: Context, currentUid: String, deviceId: String): Boolean {
        val sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val ok = sp.getBoolean(KEY_OK, false)
        val uid = sp.getString(KEY_UID, null)
        val dev = sp.getString(KEY_DEVICE, null)
        return ok && uid == currentUid && dev == deviceId
    }

    fun markBootstrapOk(ctx: Context, uid: String, deviceId: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_OK, true)
            .putString(KEY_UID, uid)
            .putString(KEY_DEVICE, deviceId)
            .apply()
    }

    fun clearBootstrap(ctx: Context) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
