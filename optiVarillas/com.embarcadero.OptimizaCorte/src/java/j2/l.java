package j2;

import W1.C0324l;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class l extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ String f4811n;

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ String f4812o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ BinderC0672b f4813p;

    /* renamed from: q  reason: collision with root package name */
    public final /* synthetic */ G f4814q;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public l(G g4, String str, String str2, BinderC0672b binderC0672b) {
        super(g4, true);
        this.f4814q = g4;
        this.f4811n = str;
        this.f4812o = str2;
        this.f4813p = binderC0672b;
    }

    @Override // j2.C
    public final void a() {
        InterfaceC0675e interfaceC0675e = this.f4814q.f4790h;
        C0324l.d(interfaceC0675e);
        interfaceC0675e.W1(this.f4811n, this.f4812o, this.f4813p);
    }

    @Override // j2.C
    public final void b() {
        this.f4813p.Z(null);
    }
}
