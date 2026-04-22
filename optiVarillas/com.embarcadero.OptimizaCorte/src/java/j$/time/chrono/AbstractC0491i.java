package j$.time.chrono;

import j$.time.AbstractC0495d;
import j$.util.Objects;

/* renamed from: j$.time.chrono.i  reason: case insensitive filesystem */
/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract /* synthetic */ class AbstractC0491i {
    public static j$.time.temporal.m a(InterfaceC0484b interfaceC0484b, j$.time.temporal.m mVar) {
        return mVar.d(interfaceC0484b.s(), j$.time.temporal.a.EPOCH_DAY);
    }

    public static int b(InterfaceC0484b interfaceC0484b, InterfaceC0484b interfaceC0484b2) {
        int compare = Long.compare(interfaceC0484b.s(), interfaceC0484b2.s());
        if (compare == 0) {
            return ((AbstractC0483a) interfaceC0484b.a()).i().compareTo(interfaceC0484b2.a().i());
        }
        return compare;
    }

    public static int c(InterfaceC0487e interfaceC0487e, InterfaceC0487e interfaceC0487e2) {
        int A4 = interfaceC0487e.c().A(interfaceC0487e2.c());
        if (A4 == 0) {
            int compareTo = interfaceC0487e.b().compareTo(interfaceC0487e2.b());
            if (compareTo == 0) {
                return ((AbstractC0483a) interfaceC0487e.a()).i().compareTo(interfaceC0487e2.a().i());
            }
            return compareTo;
        }
        return A4;
    }

    public static int d(InterfaceC0493k interfaceC0493k, InterfaceC0493k interfaceC0493k2) {
        int compare = Long.compare(interfaceC0493k.C(), interfaceC0493k2.C());
        if (compare == 0) {
            int I2 = interfaceC0493k.b().I() - interfaceC0493k2.b().I();
            if (I2 == 0) {
                int w4 = interfaceC0493k.x().w(interfaceC0493k2.x());
                if (w4 == 0) {
                    int compareTo = interfaceC0493k.p().i().compareTo(interfaceC0493k2.p().i());
                    if (compareTo == 0) {
                        return ((AbstractC0483a) interfaceC0493k.a()).i().compareTo(interfaceC0493k2.a().i());
                    }
                    return compareTo;
                }
                return w4;
            }
            return I2;
        }
        return compare;
    }

    public static int e(InterfaceC0493k interfaceC0493k, j$.time.temporal.r rVar) {
        if (rVar instanceof j$.time.temporal.a) {
            int i4 = AbstractC0492j.f3883a[((j$.time.temporal.a) rVar).ordinal()];
            if (i4 != 1) {
                return i4 != 2 ? interfaceC0493k.x().j(rVar) : interfaceC0493k.g().J();
            }
            throw new RuntimeException("Invalid field 'InstantSeconds' for get() method, use getLong() instead");
        }
        return j$.time.temporal.n.a(interfaceC0493k, rVar);
    }

    public static int f(o oVar, j$.time.temporal.r rVar) {
        return rVar == j$.time.temporal.a.ERA ? oVar.getValue() : j$.time.temporal.n.a(oVar, rVar);
    }

    public static long g(o oVar, j$.time.temporal.r rVar) {
        if (rVar == j$.time.temporal.a.ERA) {
            return oVar.getValue();
        }
        if (rVar instanceof j$.time.temporal.a) {
            throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
        return rVar.l(oVar);
    }

    public static boolean h(InterfaceC0484b interfaceC0484b, j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? ((j$.time.temporal.a) rVar).v() : rVar != null && rVar.m(interfaceC0484b);
    }

    public static boolean i(o oVar, j$.time.temporal.r rVar) {
        return rVar instanceof j$.time.temporal.a ? rVar == j$.time.temporal.a.ERA : rVar != null && rVar.m(oVar);
    }

    public static Object j(InterfaceC0484b interfaceC0484b, j$.time.temporal.t tVar) {
        if (tVar == j$.time.temporal.n.k() || tVar == j$.time.temporal.n.j() || tVar == j$.time.temporal.n.h() || tVar == j$.time.temporal.n.g()) {
            return null;
        }
        return tVar == j$.time.temporal.n.e() ? interfaceC0484b.a() : tVar == j$.time.temporal.n.i() ? j$.time.temporal.b.DAYS : tVar.a(interfaceC0484b);
    }

    public static Object k(InterfaceC0487e interfaceC0487e, j$.time.temporal.t tVar) {
        if (tVar == j$.time.temporal.n.k() || tVar == j$.time.temporal.n.j() || tVar == j$.time.temporal.n.h()) {
            return null;
        }
        return tVar == j$.time.temporal.n.g() ? interfaceC0487e.b() : tVar == j$.time.temporal.n.e() ? interfaceC0487e.a() : tVar == j$.time.temporal.n.i() ? j$.time.temporal.b.NANOS : tVar.a(interfaceC0487e);
    }

    public static Object l(InterfaceC0493k interfaceC0493k, j$.time.temporal.t tVar) {
        return (tVar == j$.time.temporal.n.j() || tVar == j$.time.temporal.n.k()) ? interfaceC0493k.p() : tVar == j$.time.temporal.n.h() ? interfaceC0493k.g() : tVar == j$.time.temporal.n.g() ? interfaceC0493k.b() : tVar == j$.time.temporal.n.e() ? interfaceC0493k.a() : tVar == j$.time.temporal.n.i() ? j$.time.temporal.b.NANOS : tVar.a(interfaceC0493k);
    }

    public static Object m(o oVar, j$.time.temporal.t tVar) {
        return tVar == j$.time.temporal.n.i() ? j$.time.temporal.b.ERAS : j$.time.temporal.n.c(oVar, tVar);
    }

    public static long n(InterfaceC0487e interfaceC0487e, j$.time.B b4) {
        Objects.requireNonNull(b4, "offset");
        return ((interfaceC0487e.c().s() * 86400) + interfaceC0487e.b().U()) - b4.J();
    }

    public static long o(InterfaceC0493k interfaceC0493k) {
        return ((interfaceC0493k.c().s() * 86400) + interfaceC0493k.b().U()) - interfaceC0493k.g().J();
    }

    public static n p(j$.time.temporal.o oVar) {
        Objects.requireNonNull(oVar, "temporal");
        Object obj = (n) oVar.u(j$.time.temporal.n.e());
        u uVar = u.f3906d;
        if (obj == null) {
            obj = Objects.requireNonNull(uVar, "defaultObj");
        }
        return (n) obj;
    }
}
