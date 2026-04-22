package i2;

import android.app.Application;
import android.os.Handler;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class g0 implements Z {

    /* renamed from: j  reason: collision with root package name */
    public final Z f3745j;

    /* renamed from: k  reason: collision with root package name */
    public final Z f3746k;

    /* renamed from: l  reason: collision with root package name */
    public final Z f3747l;

    /* renamed from: m  reason: collision with root package name */
    public final Z f3748m;

    /* renamed from: n  reason: collision with root package name */
    public final Z f3749n;

    /* renamed from: o  reason: collision with root package name */
    public final Z f3750o;

    /* renamed from: p  reason: collision with root package name */
    public final Z f3751p;

    public g0(Y y4, X x4, X x5, X x6, d0 d0Var, C0456b c0456b, X x7) {
        this.f3745j = y4;
        this.f3746k = x4;
        this.f3747l = x5;
        this.f3748m = x6;
        this.f3749n = d0Var;
        this.f3750o = c0456b;
        this.f3751p = x7;
    }

    @Override // i2.Z
    /* renamed from: b */
    public final f0 a() {
        Application application = (Application) this.f3745j.a();
        C0457c c0457c = (C0457c) this.f3746k.a();
        Handler handler = G.f3679a;
        H.a.t(handler);
        F f = G.f3680b;
        H.a.t(f);
        return new f0(application, handler, f, (C0463i) this.f3747l.a(), (C0469o) this.f3748m.a(), ((d0) this.f3749n).a(), ((C0456b) this.f3750o).a(), (a0) this.f3751p.a());
    }
}
