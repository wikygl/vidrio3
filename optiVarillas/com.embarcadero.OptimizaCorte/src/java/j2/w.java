package j2;

import W1.C0324l;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/4.dex */
public final class w extends C {

    /* renamed from: n  reason: collision with root package name */
    public final /* synthetic */ String f4847n = "Error with data collection. Data lost.";

    /* renamed from: o  reason: collision with root package name */
    public final /* synthetic */ Object f4848o;

    /* renamed from: p  reason: collision with root package name */
    public final /* synthetic */ G f4849p;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public w(G g4, Object obj) {
        super(g4, false);
        this.f4849p = g4;
        this.f4848o = obj;
    }

    @Override // j2.C
    public final void a() {
        InterfaceC0675e interfaceC0675e = this.f4849p.f4790h;
        C0324l.d(interfaceC0675e);
        interfaceC0675e.E1(this.f4847n, new c2.b(this.f4848o), new c2.b(null), new c2.b(null));
    }
}
