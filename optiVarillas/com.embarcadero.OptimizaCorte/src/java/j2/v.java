package j2;

import W1.C0324l;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class v extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ String f4842n;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ String f4843o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ boolean f4844p;

    /* renamed from: q  reason: collision with root package name */
    public final /* synthetic */ BinderC0672b f4845q;

    /* renamed from: r  reason: collision with root package name */
    public final /* synthetic */ G f4846r;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public v(G g4, String str, String str2, boolean z4, BinderC0672b binderC0672b) {
        super(g4, true);
        this.f4846r = g4;
        this.f4842n = str;
        this.f4843o = str2;
        this.f4844p = z4;
        this.f4845q = binderC0672b;
    }

    @Override // j2.C
    public final void a() {
        InterfaceC0675e interfaceC0675e = this.f4846r.f4790h;
        C0324l.d(interfaceC0675e);
        interfaceC0675e.b3(this.f4842n, this.f4843o, this.f4844p, this.f4845q);
    }

    @Override // j2.C
    public final void b() {
        this.f4845q.Z(null);
    }
}
