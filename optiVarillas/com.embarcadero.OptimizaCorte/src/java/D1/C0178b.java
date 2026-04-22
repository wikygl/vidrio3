package D1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.telephony.TelephonyManager;
import com.google.android.gms.internal.ads.lb;

/* renamed from: D1.b  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public class C0178b {
    public boolean a(Activity activity, Configuration configuration) {
        return false;
    }

    public Intent b(Activity activity) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("app_package", activity.getPackageName());
        intent.putExtra("app_uid", activity.getApplicationInfo().uid);
        return intent;
    }

    public lb c(Context context, TelephonyManager telephonyManager) {
        return lb.m;
    }

    public boolean e(Context context) {
        return false;
    }

    public int f(AudioManager audioManager) {
        return 0;
    }

    public int h(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getNetworkType();
    }

    public void d(Context context) {
    }

    public void g(Activity activity) {
    }
}
