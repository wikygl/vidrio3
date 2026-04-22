package V1;

import p2.C0758g;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class K extends I {

    /* renamed from: c  reason: collision with root package name */
    public final C0301g f2555c;

    public K(C0301g c0301g, C0758g c0758g) {
        super(c0758g);
        this.f2555c = c0301g;
    }

    @Override // V1.A
    public final boolean f(u uVar) {
        if (((E) uVar.f2609o.get(this.f2555c)) == null) {
            return false;
        }
        throw null;
    }

    @Override // V1.A
    public final T1.d[] g(u uVar) {
        if (((E) uVar.f2609o.get(this.f2555c)) == null) {
            return null;
        }
        throw null;
    }

    @Override // V1.I
    public final void h(u uVar) {
        if (((E) uVar.f2609o.remove(this.f2555c)) == null) {
            C0758g c0758g = this.f2551b;
            Boolean bool = Boolean.FALSE;
            p2.q qVar = c0758g.f5552a;
            synchronized (qVar.f5572a) {
                try {
                    if (!qVar.f5574c) {
                        qVar.f5574c = true;
                        qVar.f5576e = bool;
                        qVar.f5573b.d(qVar);
                        return;
                    }
                    return;
                } finally {
                }
            }
        }
        throw null;
    }

    @Override // V1.L
    public final /* bridge */ /* synthetic */ void d(C0307m c0307m, boolean z4) {
    }
}
