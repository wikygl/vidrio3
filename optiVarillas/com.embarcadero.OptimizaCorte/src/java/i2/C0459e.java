package i2;

import android.app.Application;
import com.google.android.gms.internal.ads.Xp;

/* renamed from: i2.e  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class C0459e extends AbstractC0455a {

    /* renamed from: b  reason: collision with root package name */
    public final C0459e f3722b = this;

    /* renamed from: c  reason: collision with root package name */
    public final Y f3723c;

    /* renamed from: d  reason: collision with root package name */
    public final X f3724d;

    /* renamed from: e  reason: collision with root package name */
    public final X f3725e;
    public final X f;

    /* renamed from: g  reason: collision with root package name */
    public final X f3726g;

    /* renamed from: h  reason: collision with root package name */
    public final C0462h f3727h;

    /* renamed from: i  reason: collision with root package name */
    public final X f3728i;

    /* JADX WARN: Type inference failed for: r0v2, types: [i2.Z, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r11v1, types: [i2.Z, java.lang.Object, K1.e] */
    public C0459e(Application application) {
        Y y4 = new Y(application);
        this.f3723c = y4;
        ?? obj = new Object();
        obj.f1368j = y4;
        X b4 = X.b(obj);
        this.f3724d = b4;
        X b5 = X.b(C0458d.f3718j);
        this.f3725e = b5;
        X b6 = X.b(new D1.E(new R2.d(17, this)));
        this.f = b6;
        d0 d0Var = new d0(y4, b4);
        X b7 = X.b(new Object());
        this.f3726g = b7;
        C0462h c0462h = new C0462h(y4, b4);
        this.f3727h = c0462h;
        this.f3728i = X.b(new Xp(b4, new g0(y4, b5, b4, b6, d0Var, new C0456b(b7, c0462h, b4), b7), b6));
    }

    @Override // i2.AbstractC0455a
    public final c0 b() {
        return (c0) this.f3728i.a();
    }

    @Override // i2.AbstractC0455a
    public final C0469o c() {
        return (C0469o) this.f.a();
    }
}
