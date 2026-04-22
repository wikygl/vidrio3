package C3;

import java.util.concurrent.CancellationException;

/* renamed from: C3.i  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class C0159i extends a0 implements InterfaceC0158h {

    /* renamed from: n  reason: collision with root package name */
    public final InterfaceC0160j f482n;

    public C0159i(d0 d0Var) {
        this.f482n = d0Var;
    }

    @Override // u3.l
    public final /* bridge */ /* synthetic */ l3.g g(Throwable th) {
        o(th);
        return l3.g.f5271a;
    }

    @Override // C3.InterfaceC0158h
    public final boolean j(Throwable th) {
        d0 p4 = p();
        if (th instanceof CancellationException) {
            return true;
        }
        if (p4.n(th) && p4.D()) {
            return true;
        }
        return false;
    }

    @Override // C3.AbstractC0164n
    public final void o(Throwable th) {
        this.f482n.g(p());
    }
}
