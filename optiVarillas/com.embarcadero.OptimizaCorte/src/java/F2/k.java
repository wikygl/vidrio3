package f2;

import K1.C0210d;
import android.content.Context;
import p2.AbstractC0757f;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class k implements Q1.a {

    /* renamed from: a  reason: collision with root package name */
    public final j f3408a;

    /* renamed from: b  reason: collision with root package name */
    public final g f3409b;

    public k(Context context) {
        g gVar;
        this.f3408a = new j(context, T1.f.f2354b);
        synchronized (g.class) {
            try {
                if (g.f3401c == null) {
                    g.f3401c = new g(context.getApplicationContext());
                }
                gVar = g.f3401c;
            } catch (Throwable th) {
                throw th;
            }
        }
        this.f3409b = gVar;
    }

    @Override // Q1.a
    public final AbstractC0757f<Q1.b> a() {
        return this.f3408a.a().f(new C0210d(this));
    }
}
