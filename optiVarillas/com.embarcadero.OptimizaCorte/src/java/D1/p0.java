package D1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/0.dex */
public final class p0 extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public final void onReceive(Context context, Intent intent) {
        synchronized (E1.l.f870b) {
            E1.l.f871c = false;
            E1.l.f872d = false;
            E1.m.g("Ad debug logging enablement is out of date.");
        }
        C3.C.f(context);
    }
}
