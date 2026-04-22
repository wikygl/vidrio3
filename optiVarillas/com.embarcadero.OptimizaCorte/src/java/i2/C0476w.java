package i2;

import android.app.Application;
import android.os.Handler;

/* renamed from: i2.w  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0476w implements Z {

    /* renamed from: j  reason: collision with root package name */
    public final Z f3811j;

    /* renamed from: k  reason: collision with root package name */
    public final Z f3812k;

    /* renamed from: l  reason: collision with root package name */
    public final Z f3813l;

    /* renamed from: m  reason: collision with root package name */
    public final Z f3814m;

    /* renamed from: n  reason: collision with root package name */
    public final Z f3815n;

    /* renamed from: o  reason: collision with root package name */
    public final Z f3816o;

    public C0476w(Z z4, X x4, Z z5, Z z6, C1.A a4, Z z7) {
        this.f3811j = z4;
        this.f3812k = x4;
        this.f3813l = z5;
        this.f3814m = z6;
        this.f3815n = a4;
        this.f3816o = z7;
    }

    @Override // i2.Z
    /* renamed from: b */
    public final C0475v a() {
        Handler handler = G.f3679a;
        H.a.t(handler);
        F f = G.f3680b;
        H.a.t(f);
        return new C0475v((Application) this.f3811j.a(), (C0473t) this.f3812k.a(), handler, f, (a0) this.f3813l.a(), ((C0462h) this.f3814m).a(), (C0467m) this.f3815n.a(), (C0463i) this.f3816o.a());
    }
}
