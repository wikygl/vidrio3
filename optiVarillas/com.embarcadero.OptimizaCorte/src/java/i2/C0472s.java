package i2;

import android.os.Handler;

/* renamed from: i2.s  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0472s implements Z {

    /* renamed from: j  reason: collision with root package name */
    public final Z f3800j;

    /* renamed from: k  reason: collision with root package name */
    public final Z f3801k;

    public C0472s(X x4, C0476w c0476w) {
        this.f3800j = x4;
        this.f3801k = c0476w;
    }

    @Override // i2.Z
    public final Object a() {
        Handler handler = G.f3679a;
        H.a.t(handler);
        return new r((C0473t) this.f3800j.a(), handler, ((C0476w) this.f3801k).a());
    }
}
