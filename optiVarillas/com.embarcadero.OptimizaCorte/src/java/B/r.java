package B;

import android.app.Notification;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class r extends u {

    /* renamed from: b  reason: collision with root package name */
    public CharSequence f254b;

    @Override // B.u
    public final void b(v vVar) {
        new Notification.BigTextStyle(vVar.f273b).setBigContentTitle(null).bigText(this.f254b);
    }

    @Override // B.u
    public final String c() {
        return "androidx.core.app.NotificationCompat$BigTextStyle";
    }
}
