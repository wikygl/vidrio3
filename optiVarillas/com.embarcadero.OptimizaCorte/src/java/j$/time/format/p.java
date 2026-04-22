package j$.time.format;

import j$.time.A;
import j$.time.chrono.InterfaceC0484b;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
final class p implements j$.time.temporal.o {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ InterfaceC0484b f3954a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ j$.time.temporal.o f3955b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ j$.time.chrono.n f3956c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ A f3957d;

    /* JADX INFO: Access modifiers changed from: package-private */
    public p(InterfaceC0484b interfaceC0484b, j$.time.temporal.o oVar, j$.time.chrono.n nVar, A a4) {
        this.f3954a = interfaceC0484b;
        this.f3955b = oVar;
        this.f3956c = nVar;
        this.f3957d = a4;
    }

    @Override // j$.time.temporal.o
    public final boolean f(j$.time.temporal.r rVar) {
        InterfaceC0484b interfaceC0484b = this.f3954a;
        return (interfaceC0484b == null || !rVar.v()) ? this.f3955b.f(rVar) : interfaceC0484b.f(rVar);
    }

    @Override // j$.time.temporal.o
    public final /* synthetic */ int j(j$.time.temporal.r rVar) {
        return j$.time.temporal.n.a(this, rVar);
    }

    @Override // j$.time.temporal.o
    public final j$.time.temporal.w m(j$.time.temporal.r rVar) {
        InterfaceC0484b interfaceC0484b = this.f3954a;
        return (interfaceC0484b == null || !rVar.v()) ? this.f3955b.m(rVar) : interfaceC0484b.m(rVar);
    }

    @Override // j$.time.temporal.o
    public final long r(j$.time.temporal.r rVar) {
        InterfaceC0484b interfaceC0484b = this.f3954a;
        return (interfaceC0484b == null || !rVar.v()) ? this.f3955b.r(rVar) : interfaceC0484b.r(rVar);
    }

    public final String toString() {
        String str;
        String str2 = "";
        j$.time.chrono.n nVar = this.f3956c;
        if (nVar != null) {
            str = " with chronology " + nVar;
        } else {
            str = "";
        }
        A a4 = this.f3957d;
        if (a4 != null) {
            str2 = " with zone " + a4;
        }
        return this.f3955b + str + str2;
    }

    @Override // j$.time.temporal.o
    public final Object u(j$.time.temporal.t tVar) {
        return tVar == j$.time.temporal.n.e() ? this.f3956c : tVar == j$.time.temporal.n.k() ? this.f3957d : tVar == j$.time.temporal.n.i() ? this.f3955b.u(tVar) : tVar.a(this);
    }
}
