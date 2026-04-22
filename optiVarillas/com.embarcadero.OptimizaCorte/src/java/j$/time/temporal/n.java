package j$.time.temporal;

import j$.time.AbstractC0495d;
import j$.util.Objects;

/* loaded from: /storage/emulated/0/Documents/jadec/sources/com.embarcadero.OptimizaCorte/dex-files/5.dex */
public abstract /* synthetic */ class n {

    /* renamed from: a  reason: collision with root package name */
    static final t f4019a = new s(0);

    /* renamed from: b  reason: collision with root package name */
    static final t f4020b = new s(1);

    /* renamed from: c  reason: collision with root package name */
    static final t f4021c = new s(2);

    /* renamed from: d  reason: collision with root package name */
    static final t f4022d = new s(3);

    /* renamed from: e  reason: collision with root package name */
    static final t f4023e = new s(4);
    static final t f = new s(5);

    /* renamed from: g  reason: collision with root package name */
    static final t f4024g = new s(6);

    public static int a(o oVar, r rVar) {
        w m4 = oVar.m(rVar);
        if (!m4.h()) {
            throw new RuntimeException("Invalid field " + rVar + " for get() method, use getLong() instead");
        }
        long r4 = oVar.r(rVar);
        if (m4.i(r4)) {
            return (int) r4;
        }
        throw new RuntimeException("Invalid value for " + rVar + " (valid values " + m4 + "): " + r4);
    }

    public static m b(m mVar, long j4, u uVar) {
        long j5;
        if (j4 == Long.MIN_VALUE) {
            mVar = mVar.e(Long.MAX_VALUE, uVar);
            j5 = 1;
        } else {
            j5 = -j4;
        }
        return mVar.e(j5, uVar);
    }

    public static Object c(o oVar, t tVar) {
        if (tVar == f4019a || tVar == f4020b || tVar == f4021c) {
            return null;
        }
        return tVar.a(oVar);
    }

    public static w d(o oVar, r rVar) {
        if (!(rVar instanceof a)) {
            Objects.requireNonNull(rVar, "field");
            return rVar.u(oVar);
        } else if (oVar.f(rVar)) {
            return ((a) rVar).j();
        } else {
            throw new RuntimeException(AbstractC0495d.a("Unsupported field: ", rVar));
        }
    }

    public static t e() {
        return f4020b;
    }

    public static t f() {
        return f;
    }

    public static t g() {
        return f4024g;
    }

    public static t h() {
        return f4022d;
    }

    public static t i() {
        return f4021c;
    }

    public static t j() {
        return f4023e;
    }

    public static t k() {
        return f4019a;
    }
}
