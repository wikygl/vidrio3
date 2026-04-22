package Q0;

import com.google.android.gms.internal.play_billing.P1;
import f1.C0411a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/1.dex */
public final class o {

    /* renamed from: a  reason: collision with root package name */
    public boolean f1989a;

    /* renamed from: b  reason: collision with root package name */
    public i1.t f1990b;

    public final void a(P1 p12) {
        if (this.f1989a) {
            com.google.android.gms.internal.play_billing.u.e("BillingLogger", "Skipping logging since initialization failed.");
            return;
        }
        try {
            this.f1990b.a(new C0411a(p12));
        } catch (Throwable unused) {
            com.google.android.gms.internal.play_billing.u.e("BillingLogger", "logging failed.");
        }
    }
}
