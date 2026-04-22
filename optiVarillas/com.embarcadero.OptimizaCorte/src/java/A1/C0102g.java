package A1;

import android.content.Context;
import com.google.android.gms.internal.ads.eg;

/* renamed from: A1.g  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0102g extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f120b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ C1 f121c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ String f122d;

    /* renamed from: e  reason: collision with root package name */
    public final /* synthetic */ eg f123e;
    public final /* synthetic */ C0120n f;

    public C0102g(C0120n c0120n, Context context, C1 c12, String str, eg egVar) {
        this.f120b = context;
        this.f121c = c12;
        this.f122d = str;
        this.f123e = egVar;
        this.f = c0120n;
    }

    @Override // A1.AbstractC0122o
    public final Object a() {
        C0120n.b(this.f120b, "app_open");
        return new K();
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        return AbstractC0122o.f160a.p2(new c2.b(this.f120b), this.f121c, this.f122d, this.f123e, 241199000);
    }

    @Override // A1.AbstractC0122o
    public final /* bridge */ /* synthetic */ Object c() {
        return ((x1) this.f.f153j).c(this.f120b, this.f121c, this.f122d, this.f123e, 4);
    }
}
