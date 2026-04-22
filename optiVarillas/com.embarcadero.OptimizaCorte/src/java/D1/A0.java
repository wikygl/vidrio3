package D1;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import com.google.android.gms.internal.ads.Gb;
import com.google.android.gms.internal.ads.lb;

@TargetApi(26)
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public class A0 extends w0 {
    @Override // D1.C0178b
    public final Intent b(Activity activity) {
        Intent intent = new Intent();
        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
        intent.putExtra("android.provider.extra.APP_PACKAGE", activity.getPackageName());
        return intent;
    }

    @Override // D1.C0178b
    public final lb c(Context context, TelephonyManager telephonyManager) {
        boolean isDataEnabled;
        t0 t0Var = z1.p.f6575A.f6578c;
        boolean a4 = t0.a(context, "android.permission.ACCESS_NETWORK_STATE");
        lb lbVar = lb.k;
        if (a4) {
            isDataEnabled = telephonyManager.isDataEnabled();
            if (isDataEnabled) {
                return lb.l;
            }
            return lbVar;
        }
        return lbVar;
    }

    @Override // D1.C0178b
    public final void d(Context context) {
        Object systemService;
        r0.a();
        NotificationChannel b4 = z0.b(((Integer) A1.r.f168d.f171c.a(Gb.C7)).intValue());
        b4.setShowBadge(false);
        systemService = context.getSystemService(NotificationManager.class);
        ((NotificationManager) systemService).createNotificationChannel(b4);
    }

    @Override // D1.C0178b
    public final boolean e(Context context) {
        Object systemService;
        NotificationChannel notificationChannel;
        int importance;
        systemService = context.getSystemService(NotificationManager.class);
        notificationChannel = ((NotificationManager) systemService).getNotificationChannel("offline_notification_channel");
        if (notificationChannel != null) {
            importance = notificationChannel.getImportance();
            if (importance != 0) {
                return false;
            }
            return true;
        }
        return false;
    }
}
