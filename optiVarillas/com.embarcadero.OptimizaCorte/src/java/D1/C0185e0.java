package D1;

import a2.C0344b;
import android.content.Context;
import android.os.Message;
import com.google.android.gms.internal.ads.WJ;
import com.google.android.gms.internal.ads.zc;

/* renamed from: D1.e0  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0185e0 extends WJ {
    public final void a(Message message) {
        try {
            super.a(message);
        } catch (Throwable th) {
            t0 t0Var = z1.p.f6575A.f6578c;
            Context context = z1.p.f6575A.f6581g.e;
            if (context != null) {
                try {
                    if (((Boolean) zc.b.e()).booleanValue()) {
                        C0344b.a(context, th);
                    }
                } catch (IllegalStateException unused) {
                }
            }
            throw th;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void handleMessage(Message message) {
        try {
            super/*android.os.Handler*/.handleMessage(message);
        } catch (Exception e4) {
            z1.p.f6575A.f6581g.h("AdMobHandler.handleMessage", e4);
        }
    }
}
