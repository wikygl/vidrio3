package Q0;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class s {

    /* renamed from: a  reason: collision with root package name */
    public final Context f1999a;

    /* renamed from: b  reason: collision with root package name */
    public final f f2000b;

    /* renamed from: c  reason: collision with root package name */
    public final n f2001c;

    /* renamed from: d  reason: collision with root package name */
    public final r f2002d = new r(this, true);

    /* renamed from: e  reason: collision with root package name */
    public final r f2003e = new r(this, false);
    public boolean f;

    public s(Context context, Y0.a aVar, n nVar) {
        this.f1999a = context;
        this.f2000b = aVar;
        this.f2001c = nVar;
    }

    public final void a(boolean z4) {
        int i4;
        IntentFilter intentFilter = new IntentFilter("com.android.vending.billing.PURCHASES_UPDATED");
        IntentFilter intentFilter2 = new IntentFilter("com.android.vending.billing.LOCAL_BROADCAST_PURCHASES_UPDATED");
        intentFilter2.addAction("com.android.vending.billing.ALTERNATIVE_BILLING");
        this.f = z4;
        this.f2003e.a(this.f1999a, intentFilter2);
        if (this.f) {
            r rVar = this.f2002d;
            Context context = this.f1999a;
            synchronized (rVar) {
                try {
                    if (!rVar.f1996a) {
                        if (Build.VERSION.SDK_INT >= 33) {
                            if (true != rVar.f1997b) {
                                i4 = 4;
                            } else {
                                i4 = 2;
                            }
                            context.registerReceiver(rVar, intentFilter, "com.google.android.finsky.permission.PLAY_BILLING_LIBRARY_BROADCAST", null, i4);
                        } else {
                            context.registerReceiver(rVar, intentFilter, "com.google.android.finsky.permission.PLAY_BILLING_LIBRARY_BROADCAST", null);
                        }
                        rVar.f1996a = true;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            return;
        }
        this.f2002d.a(this.f1999a, intentFilter);
    }
}
