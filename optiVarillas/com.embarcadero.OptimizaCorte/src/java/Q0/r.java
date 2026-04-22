package Q0;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import com.google.android.gms.internal.play_billing.M;
import com.google.android.gms.internal.play_billing.V;
import com.google.android.gms.internal.play_billing.v1;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class r extends BroadcastReceiver {

    /* renamed from: a  reason: collision with root package name */
    public boolean f1996a;

    /* renamed from: b  reason: collision with root package name */
    public final boolean f1997b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ s f1998c;

    public r(s sVar, boolean z4) {
        this.f1998c = sVar;
        this.f1997b = z4;
    }

    public final synchronized void a(Context context, IntentFilter intentFilter) {
        int i4;
        try {
            if (this.f1996a) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 33) {
                if (true != this.f1997b) {
                    i4 = 4;
                } else {
                    i4 = 2;
                }
                context.registerReceiver(this, intentFilter, i4);
            } else {
                context.registerReceiver(this, intentFilter);
            }
            this.f1996a = true;
        } catch (Throwable th) {
            throw th;
        }
    }

    public final void b(Bundle bundle, com.android.billingclient.api.a aVar, int i4) {
        M m4;
        if (bundle.getByteArray("FAILURE_LOGGING_PAYLOAD") != null) {
            try {
                n nVar = this.f1998c.f2001c;
                byte[] byteArray = bundle.getByteArray("FAILURE_LOGGING_PAYLOAD");
                M m5 = M.b;
                if (m5 == null) {
                    synchronized (M.class) {
                        m4 = M.b;
                        if (m4 == null) {
                            m4 = V.b();
                            M.b = m4;
                        }
                    }
                    m5 = m4;
                }
                ((L0.f) nVar).c(v1.p(byteArray, m5));
                return;
            } catch (Throwable unused) {
                com.google.android.gms.internal.play_billing.u.e("BillingBroadcastManager", "Failed parsing Api failure.");
                return;
            }
        }
        ((L0.f) this.f1998c.f2001c).c(m.a(23, i4, aVar));
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x0108  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x0114  */
    @Override // android.content.BroadcastReceiver
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void onReceive(android.content.Context r11, android.content.Intent r12) {
        /*
            Method dump skipped, instructions count: 287
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: Q0.r.onReceive(android.content.Context, android.content.Intent):void");
    }
}
