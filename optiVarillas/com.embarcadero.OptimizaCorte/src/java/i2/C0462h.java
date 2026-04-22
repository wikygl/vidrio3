package i2;

import android.app.Application;

/* renamed from: i2.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0462h implements Z {

    /* renamed from: j  reason: collision with root package name */
    public final Z f3752j;

    /* renamed from: k  reason: collision with root package name */
    public final Z f3753k;

    public C0462h(Y y4, X x4) {
        this.f3752j = y4;
        this.f3753k = x4;
    }

    @Override // i2.Z
    /* renamed from: b */
    public final C0461g a() {
        F f = G.f3680b;
        H.a.t(f);
        return new C0461g((Application) this.f3752j.a(), (C0463i) this.f3753k.a(), f);
    }
}
