package N0;

import N0.a;
import a3.InterfaceFutureC0346a;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public final class c<V> extends a<V> {
    public final boolean j(V v4) {
        if (v4 == null) {
            v4 = (V) a.f1763p;
        }
        if (a.f1762o.b(this, null, v4)) {
            a.c(this);
            return true;
        }
        return false;
    }

    public final boolean k(Throwable th) {
        th.getClass();
        if (a.f1762o.b(this, null, new a.c(th))) {
            a.c(this);
            return true;
        }
        return false;
    }

    public final boolean l(InterfaceFutureC0346a<? extends V> interfaceFutureC0346a) {
        a.c cVar;
        interfaceFutureC0346a.getClass();
        Object obj = this.f1764j;
        if (obj == null) {
            if (interfaceFutureC0346a.isDone()) {
                if (!a.f1762o.b(this, null, a.f(interfaceFutureC0346a))) {
                    return false;
                }
                a.c(this);
            } else {
                a.f fVar = new a.f(this, interfaceFutureC0346a);
                if (a.f1762o.b(this, null, fVar)) {
                    try {
                        interfaceFutureC0346a.a(fVar, b.f1787j);
                    } catch (Throwable th) {
                        try {
                            cVar = new a.c(th);
                        } catch (Throwable unused) {
                            cVar = a.c.f1771b;
                        }
                        a.f1762o.b(this, fVar, cVar);
                    }
                } else {
                    obj = this.f1764j;
                }
            }
            return true;
        }
        if (!(obj instanceof a.b)) {
            return false;
        }
        interfaceFutureC0346a.cancel(((a.b) obj).f1769a);
        return false;
    }
}
