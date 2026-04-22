package A1;

import android.content.Context;
import com.google.android.gms.internal.ads.bg;
import com.google.android.gms.internal.ads.eg;

/* renamed from: A1.i  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0108i extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f131b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ C1 f132c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ String f133d;

    /* renamed from: e  reason: collision with root package name */
    public final /* synthetic */ eg f134e;
    public final /* synthetic */ C0120n f;

    public C0108i(C0120n c0120n, Context context, C1 c12, String str, bg bgVar) {
        this.f131b = context;
        this.f132c = c12;
        this.f133d = str;
        this.f134e = bgVar;
        this.f = c0120n;
    }

    @Override // A1.AbstractC0122o
    public final Object a() {
        C0120n.b(this.f131b, "interstitial");
        return new K();
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        return AbstractC0122o.f160a.P0(new c2.b(this.f131b), this.f132c, this.f133d, this.f134e, 241199000);
    }

    @Override // A1.AbstractC0122o
    public final /* bridge */ /* synthetic */ Object c() {
        return ((x1) this.f.f153j).c(this.f131b, this.f132c, this.f133d, this.f134e, 2);
    }
}
