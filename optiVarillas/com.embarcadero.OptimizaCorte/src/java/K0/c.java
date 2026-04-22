package K0;

import android.app.Notification;
import android.os.Build;
import androidx.work.impl.foreground.SystemForegroundService;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class c implements Runnable {

    /* renamed from: j  reason: collision with root package name */
    public final /* synthetic */ int f1270j;

    /* renamed from: k  reason: collision with root package name */
    public final /* synthetic */ Notification f1271k;

    /* renamed from: l  reason: collision with root package name */
    public final /* synthetic */ int f1272l;

    /* renamed from: m  reason: collision with root package name */
    public final /* synthetic */ SystemForegroundService f1273m;

    public c(SystemForegroundService systemForegroundService, int i4, Notification notification, int i5) {
        this.f1273m = systemForegroundService;
        this.f1270j = i4;
        this.f1271k = notification;
        this.f1272l = i5;
    }

    @Override // java.lang.Runnable
    public final void run() {
        int i4 = Build.VERSION.SDK_INT;
        Notification notification = this.f1271k;
        int i5 = this.f1270j;
        SystemForegroundService systemForegroundService = this.f1273m;
        if (i4 >= 29) {
            systemForegroundService.startForeground(i5, notification, this.f1272l);
        } else {
            systemForegroundService.startForeground(i5, notification);
        }
    }
}
