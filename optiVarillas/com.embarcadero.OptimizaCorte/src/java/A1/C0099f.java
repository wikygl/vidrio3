package A1;

import android.content.Context;
import com.google.android.gms.internal.ads.eg;

/* renamed from: A1.f  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0099f extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f115b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ C1 f116c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ String f117d;

    /* renamed from: e  reason: collision with root package name */
    public final /* synthetic */ eg f118e;
    public final /* synthetic */ C0120n f;

    public C0099f(C0120n c0120n, Context context, C1 c12, String str, eg egVar) {
        this.f115b = context;
        this.f116c = c12;
        this.f117d = str;
        this.f118e = egVar;
        this.f = c0120n;
    }

    @Override // A1.AbstractC0122o
    public final Object a() {
        C0120n.b(this.f115b, "banner");
        return new K();
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        return AbstractC0122o.f160a.N1(new c2.b(this.f115b), this.f116c, this.f117d, this.f118e, 241199000);
    }

    @Override // A1.AbstractC0122o
    public final /* bridge */ /* synthetic */ Object c() {
        return ((x1) this.f.f153j).c(this.f115b, this.f116c, this.f117d, this.f118e, 1);
    }
}
