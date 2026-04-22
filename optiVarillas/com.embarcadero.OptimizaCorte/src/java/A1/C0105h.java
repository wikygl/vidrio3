package A1;

import android.content.Context;

/* renamed from: A1.h  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/3.dex */
public final class C0105h extends AbstractC0122o {

    /* renamed from: b  reason: collision with root package name */
    public final /* synthetic */ Context f126b;

    /* renamed from: c  reason: collision with root package name */
    public final /* synthetic */ C1 f127c;

    /* renamed from: d  reason: collision with root package name */
    public final /* synthetic */ String f128d;

    /* renamed from: e  reason: collision with root package name */
    public final /* synthetic */ C0120n f129e;

    public C0105h(C0120n c0120n, Context context, C1 c12, String str) {
        this.f126b = context;
        this.f127c = c12;
        this.f128d = str;
        this.f129e = c0120n;
    }

    @Override // A1.AbstractC0122o
    public final Object a() {
        C0120n.b(this.f126b, "search");
        return new K();
    }

    @Override // A1.AbstractC0122o
    public final Object b() {
        return AbstractC0122o.f160a.I3(new c2.b(this.f126b), this.f127c, this.f128d, 241199000);
    }

    @Override // A1.AbstractC0122o
    public final /* bridge */ /* synthetic */ Object c() {
        return ((x1) this.f129e.f153j).c(this.f126b, this.f127c, this.f128d, null, 3);
    }
}
