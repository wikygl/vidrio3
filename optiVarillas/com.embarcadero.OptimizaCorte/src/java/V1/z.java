package V1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class z extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    public Context f2626a;

    /* renamed from: b  reason: collision with root package name */
    public final C0306l f2627b;

    public z(C0306l c0306l) {
        this.f2627b = c0306l;
    }

    @Override // android.content.BroadcastReceiver
    public final void onReceive(Context context, Intent intent) {
        Uri data = intent.getData();
        String str = null;
        if (data != null) {
            str = data.getSchemeSpecificPart();
        }
        if (!"com.google.android.gms".equals(str)) {
            return;
        }
        ((O) this.f2627b.f2595l).getClass();
        throw null;
    }
}
